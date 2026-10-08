package com.eaioc.controlplane.output_controls;

import com.eaioc.controlplane.core.schemas.BudgetConstraints;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingResult;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.IntArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Capability 6 — Output Controls: schema validation + length enforcement on a response
 * ({@code execution-plan.md} §18.9 B row; {@code architecture.md} §12.3 Output Schema Selector,
 * §12.4 Output Length Controller).
 *
 * <p><b>Scope, per the five human decisions of 2026-10-08 resolving this unit's Architect-pre-flight
 * {@code BLOCKED} verdict (full text: {@code execution-plan.md} §18.9 B row, {@code HD-CG-P0.6-01}
 * to {@code -05}):</b>
 * <ul>
 *   <li>{@code HD-CG-P0.6-01} — operates on {@link CandidateOutput}, a minimal local type, never
 *       {@code interfaces.md} §7.2's provider-adapter-boundary {@code LLMInvocationResult}.
 *   <li>{@code HD-CG-P0.6-02} — accepts an already-resolved {@link OutputSchema} as a parameter;
 *       never performs §12.3's intent-based selection itself ({@code SOURCE-GAP-EXECPLAN-37}).
 *   <li>{@code HD-CG-P0.6-03} — length enforcement is narrowed to whole-response truncation
 *       against {@link BudgetConstraints#maxOutputTokens()}, not §12.4's undefined field-level
 *       per-named-field budgets ({@code SOURCE-GAP-EXECPLAN-38}).
 *   <li>{@code HD-CG-P0.6-04} — a schema-fail result throws {@link MalformedOutputException}, a
 *       unit-local exception; no new {@code core.errors.ControlPlaneError} code is introduced.
 *   <li>{@code HD-CG-P0.6-05} — {@link OutputSchema} is a required-field-plus-type-check list,
 *       explicitly not a full JSON Schema validator.
 * </ul>
 *
 * <p><b>Ordering (schema before length):</b> {@code optimization-catalog.md}'s own
 * {@code TECH-017}/{@code TECH-018} "Composition / Ordering / Conflicts" fields state schema
 * enforcement is "Stage 21 of 24 ... before output-length enforcement — schema shape is enforced
 * before length is bounded." This class enforces the same order: {@link #enforce} validates the
 * schema first and throws before any truncation is attempted.
 *
 * <p><b>Token counting (unit-level implementation decision, not a new contract):</b> this class
 * instantiates its own JTokkit {@code CL100K_BASE} encoding directly, independently of Capability
 * 3's {@code pipeline.t1_context.sanitizer.TokenCounter} abstraction. The frozen
 * {@code interfaces.md} §44.4 itself states: "This counter is the EAIOC P0 local estimator used by
 * T1.1 only" — a contract-level scoping statement (restated on {@code TokenCounterIdentity}'s own
 * Javadoc), not merely an implementation comment, so reusing it here would misuse a type whose
 * owning frozen contract explicitly scopes it elsewhere. Capability 6's declared dependency set
 * ({@code execution-plan.md} §18.9) also names only Capability 1, never Capability 3; reusing
 * {@code TokenCounter} would additionally be an undeclared cross-capability dependency. This does
 * not change any public contract ({@code core/schemas/}, {@code interfaces.md}) and is therefore a
 * unit-level decision, consistent with the precedent recorded for {@code EXE-P0.2.B} ("any
 * unit-local implementation mechanism that changes no public contract is a unit-level decision and
 * is not by itself a reason to block"), not a sixth human decision. (Independently confirmed by the
 * Architect review for this unit, 2026-10-08, which also noted this citation originally pointed to
 * {@code TokenCounter}'s own Javadoc rather than {@code TokenCounterIdentity}'s/§44.4's — corrected
 * here.)
 *
 * <p><b>Concurrency: NOT VERIFIED.</b> The thread-safety of JTokkit 1.1.0's {@code Encoding} was
 * not established from its documentation or source (same disclosure as
 * {@code JTokkitTokenCounter}, Capability 3, for the identical shared-static-instance mechanism).
 * Do not treat a shared {@code OutputController} as thread-safe until that is verified.
 *
 * <p><b>Zero/negative budget boundary (Reviewer finding, 2026-10-08, fixed before commit):</b> an
 * earlier draft passed {@code maxOutputTokens <= 0} straight to
 * {@code Encoding.encode(text, maxTokens)}, which — reproduced directly against this project's own
 * {@code jtokkit} 1.1.0 jar — does not reliably bound output or set {@code isTruncated()} at that
 * boundary (e.g. a 1-token candidate with {@code maxOutputTokens = 0} returned
 * {@code truncated=false}, falsely claiming budget compliance). {@link #enforce} now handles
 * {@code maxOutputTokens <= 0} explicitly before calling into JTokkit: content is truncated to
 * empty and marked truncated, unless the candidate was already empty.
 *
 * <p><b>{@code EC-050}/{@code EC-051} (Reviewer finding, 2026-10-08):</b> see {@link ValidatedOutput}
 * for the explicit, ID-cited disposition discharging the {@code EXE-P0.6.A} design note's forward
 * flag, and {@code execution-plan.md} §38's {@code SOURCE-GAP-EXECPLAN-39} for its registered,
 * non-blocking P0-gap status.
 *
 * <p><b>Observability (EXE-P0.6.D, human decisions HD-CG-P0.6-06a/b/d of 2026-10-08):</b> this
 * unit's own B-time Javadoc disclosed, and the D-time Architect pre-flight confirmed as a genuine
 * {@code BLOCKED} design fork, that {@link #enforce} originally carried no {@code tenant_id}/
 * {@code request_id}, so {@code conventions.md} §17.2's mandatory per-log-entry fields could not be
 * satisfied. Three decisions resolve this: {@code HD-CG-P0.6-06a} — {@link #enforce}'s signature is
 * changed (not an additive overload, which would have left a legacy path permanently unlogged);
 * {@code OutputControllerTest}/{@code OutputControllerIntegrationTest} call sites are updated
 * accordingly. {@code HD-CG-P0.6-06b} — the new parameters are plain {@code String requestId,
 * String tenantId}, matching {@code PolicyBudgetEnforcer.enforceRequestBudget}'s precedent exactly,
 * not a wrapper object. {@code HD-CG-P0.6-06d} — two distinct event types,
 * {@code OUTPUT_VALIDATED} (INFO, not truncated) and {@code OUTPUT_TRUNCATED} (WARNING, truncated),
 * following {@code PolicyBudgetEnforcer}'s per-outcome-naming precedent rather than one event type
 * with a variable field. Neither event name is a missed {@code interfaces.md} §23.2 citation — the
 * Standard Event Types table has no entry for output-validation completion; {@code QUALITY_VIOLATION}
 * was considered and rejected, since it is scoped to {@code QG-NNN} quality-dimension judgment
 * calls, and this capability's own Sub-phase F is {@code NOT APPLICABLE} for exactly that reason
 * (deterministic enforcement, not a quality-gated judgment). {@code component_id=OUTPUT-CONTROLLER}
 * (no canonical architecture ID exists for either §12.3 or §12.4, mirroring
 * {@code PROMPT-ASSEMBLER}/{@code CONTEXT-POLICY}'s own precedent). {@code correlation_id} defaults
 * to {@code requestId}, mirroring {@code PolicyBudgetEnforcer}'s own disclosed reasoning: no
 * separate correlation identifier exists at this capability's narrowed scope. No
 * {@code OptimizationSpan} exists in P0 ({@code conventions.md} §17.1 requires one per component
 * invocation): {@code span_id} below is a fresh per-line {@code UUID} linking to no real span —
 * the same disclosed, not-claimed-as-realized gap every other P0 log line carries
 * ({@code SOURCE-GAP-EXECPLAN-29}), explicitly cited here (Reviewer finding, 2026-10-08) rather
 * than silently repeating the same behavior without the same disclosure its own precedents give.
 *
 * <p><b>Never logs response content.</b> Mirroring {@code PromptAssembler.logAssembled}'s own rule
 * and its own citation ("No region content... is logged — only identifiers and the final token
 * count"), the log lines here never echo {@link CandidateOutput#content()} or
 * {@link CandidateOutput#fields()} values. (Architect review correction, 2026-10-08: an earlier
 * draft of this Javadoc cited {@code conventions.md} §17.4 for this rule — that section actually
 * governs durable {@code AUDIT}/{@code POLICY_VIOLATION} event-bus payloads and raw PII
 * specifically, neither of which applies to an ordinary SLF4J log line for a locally-coined event
 * type, and no event bus exists in P0 at all (`SOURCE-GAP-EXECPLAN-35`). The correct ground,
 * matching {@code PromptAssembler}'s own precedent exactly, is {@code SEC-001}/root
 * {@code CLAUDE.md} rule 6: {@code architecture.md} §12.3's output structures may carry arbitrary
 * response text, and this capability's own output could in principle carry content a future unit
 * classifies as security-relevant, so this is a precedent-following design choice, not a
 * frozen-source mandate.) Only identifiers and token counts are logged.
 *
 * <p><b>{@code architecture.md} §27.1 OUTPUT group field mapping (INFERRED, disclosed — mirrors
 * {@code Sanitizer}'s own disclosed-inference pattern for {@code tokens.raw_input}/
 * {@code tokens.sanitized}):</b> {@code tokens.raw_output} is logged as the candidate's own
 * pre-truncation token count (computed once per call, even on the non-truncated path, where it
 * equals the returned count); {@code tokens.optimized_output} is logged as
 * {@link ValidatedOutput#finalTokenCount()}. §27.1's {@code tokens.truncated} is NOT logged as its
 * own field: the source leaves its type undifferentiated, and {@link ValidatedOutput#truncated()}
 * is a boolean, not a count — reusing the field name for a differently-typed value would
 * misrepresent an exact contract match as something it is not; the removed-token quantity is
 * already fully derivable from {@code raw_output - optimized_output} by a reader who needs it.
 * {@code tokens.expanded_retry} is NOT logged: no retry mechanism exists anywhere in P0, so there is
 * nothing to report, and a fabricated {@code 0} would misrepresent "not implemented" as "measured
 * zero" (root {@code CLAUDE.md} rule 5's same discipline, applied to a metric rather than a saving).
 */
public final class OutputController {

    private static final Logger LOG = LoggerFactory.getLogger(OutputController.class);

    private static final class Holder {
        static final Encoding CL100K_BASE =
            Encodings.newDefaultEncodingRegistry().getEncoding(EncodingType.CL100K_BASE);
    }

    public OutputController() {
    }

    /**
     * Validates {@code candidate} against {@code schema}'s required fields, then enforces
     * {@code budgetConstraints}'s whole-response token budget if one is present. Emits exactly one
     * structured log line per successful call (HD-CG-P0.6-06a/b/d) — none on the thrown-exception
     * path, mirroring every sibling P0 unit's own no-log-on-failure precedent.
     *
     * @throws MalformedOutputException if a required field is missing or has the wrong type —
     *     {@code candidate} is never silently passed through as valid
     */
    public ValidatedOutput enforce(
            String requestId, String tenantId,
            CandidateOutput candidate, OutputSchema schema, BudgetConstraints budgetConstraints) {
        Objects.requireNonNull(requestId, "requestId");
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(candidate, "candidate");
        Objects.requireNonNull(schema, "schema");

        validateSchema(candidate, schema);
        return enforceLength(requestId, tenantId, candidate, budgetConstraints);
    }

    private static void validateSchema(CandidateOutput candidate, OutputSchema schema) {
        for (OutputSchema.RequiredField required : schema.requiredFields()) {
            Object value = candidate.fields().get(required.name());
            if (value == null) {
                throw new MalformedOutputException(
                    "CandidateOutput is missing required field \"" + required.name() + "\"");
            }
            if (!required.type().isInstance(value)) {
                throw new MalformedOutputException(
                    "CandidateOutput field \"" + required.name() + "\" has type "
                        + value.getClass().getName() + ", expected " + required.type().getName());
            }
        }
    }

    private static ValidatedOutput enforceLength(
            String requestId, String tenantId,
            CandidateOutput candidate, BudgetConstraints budgetConstraints) {
        Integer maxOutputTokens =
            budgetConstraints == null ? null : budgetConstraints.maxOutputTokens();
        ValidatedOutput result;
        int rawOutputTokens;

        if (maxOutputTokens == null) {
            rawOutputTokens = Holder.CL100K_BASE.countTokens(candidate.content());
            result = new ValidatedOutput(candidate.content(), false, rawOutputTokens);
        } else if (maxOutputTokens <= 0) {
            // Reviewer finding (2026-10-08, reproduced directly against the project's own jtokkit
            // 1.1.0 jar): Encoding.encode(text, maxTokens) does not reliably bound output or set
            // isTruncated() when maxTokens <= 0 -- it is undefined/buggy at that boundary, not
            // merely unverified. BudgetConstraints.maxOutputTokens (interfaces.md Sec2.2) declares
            // no minimum or positivity constraint, so 0/negative is a legitimately representable
            // input this method must handle explicitly, not an impossible one. A non-positive
            // budget means "no output tokens are permitted" -- content is truncated to empty,
            // marked truncated unless the candidate was already empty.
            rawOutputTokens = Holder.CL100K_BASE.countTokens(candidate.content());
            result = rawOutputTokens == 0
                ? new ValidatedOutput(candidate.content(), false, 0)
                : new ValidatedOutput("", true, 0);
        } else {
            EncodingResult encoded = Holder.CL100K_BASE.encode(candidate.content(), maxOutputTokens);
            IntArrayList tokens = encoded.getTokens();
            if (!encoded.isTruncated()) {
                rawOutputTokens = tokens.size();
                result = new ValidatedOutput(candidate.content(), false, rawOutputTokens);
            } else {
                // tokens.raw_output (architecture.md Sec27.1) is not otherwise computed on this
                // branch -- a dedicated count is taken purely for the D-row observability
                // requirement, not a behavior change.
                rawOutputTokens = Holder.CL100K_BASE.countTokens(candidate.content());
                String truncatedContent = Holder.CL100K_BASE.decode(tokens);
                result = new ValidatedOutput(truncatedContent, true, tokens.size());
            }
        }

        logResult(requestId, tenantId, result, rawOutputTokens);
        return result;
    }

    private static void logResult(
            String requestId, String tenantId, ValidatedOutput result, int rawOutputTokens) {
        if (result.truncated()) {
            logTruncated(requestId, tenantId, result, rawOutputTokens);
        } else {
            logValidated(requestId, tenantId, result, rawOutputTokens);
        }
    }

    /** Exactly one structured INFO line when no truncation was needed (conventions.md §17.2: "normal optimization decisions"). */
    private static void logValidated(
            String requestId, String tenantId, ValidatedOutput result, int rawOutputTokens) {
        LOG.info(
            "event_type=OUTPUT_VALIDATED component_id=OUTPUT-CONTROLLER "
                + "message=\"output validation completed\" "
                + "tenant_id={} request_id={} correlation_id={} span_id={} parent_span_id=null "
                + "fields={}",
            tenantId, requestId, requestId, UUID.randomUUID(),
            Map.of("tokens.raw_output", rawOutputTokens, "tokens.optimized_output", result.finalTokenCount()));
    }

    /** Exactly one structured WARNING line when content was truncated to fit the budget (conventions.md §17.2: "near-threshold behavior"). */
    private static void logTruncated(
            String requestId, String tenantId, ValidatedOutput result, int rawOutputTokens) {
        LOG.warn(
            "event_type=OUTPUT_TRUNCATED component_id=OUTPUT-CONTROLLER "
                + "message=\"output truncated to fit length budget\" "
                + "tenant_id={} request_id={} correlation_id={} span_id={} parent_span_id=null "
                + "fields={}",
            tenantId, requestId, requestId, UUID.randomUUID(),
            Map.of("tokens.raw_output", rawOutputTokens, "tokens.optimized_output", result.finalTokenCount()));
    }
}
