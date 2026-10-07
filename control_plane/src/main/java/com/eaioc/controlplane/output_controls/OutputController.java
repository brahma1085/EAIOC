package com.eaioc.controlplane.output_controls;

import com.eaioc.controlplane.core.schemas.BudgetConstraints;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingResult;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.IntArrayList;
import java.util.Objects;

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
 * <p><b>Disclosed open item (Architect review finding, 2026-10-08, not resolved by HD-CG-P0.6-01 to
 * -05):</b> unlike every sibling P0 unit's minimal-implementation method ({@code Sanitizer.sanitize},
 * {@code PromptAssembler.assemble}, {@code PolicyBudgetEnforcer.enforceRequestBudget}, each of which
 * carries a {@code tenant_id}/{@code request_id} through its own signature specifically so its own
 * Sub-phase D can satisfy {@code conventions.md} §17.2's mandatory per-log-entry {@code tenant_id}
 * rule), {@link #enforce} carries no tenant or request identifier anywhere in its signature. This
 * sub-phase's own Definition of Done is silent on it, so it does not block B, but Sub-phase D cannot
 * be AI-verified through this signature as written without first resolving it (either add
 * tenant/request parameters, or define how a future caller supplies them to the logger externally).
 * Recorded here, not silently left for D to discover.
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
 */
public final class OutputController {

    private static final class Holder {
        static final Encoding CL100K_BASE =
            Encodings.newDefaultEncodingRegistry().getEncoding(EncodingType.CL100K_BASE);
    }

    public OutputController() {
    }

    /**
     * Validates {@code candidate} against {@code schema}'s required fields, then enforces
     * {@code budgetConstraints}'s whole-response token budget if one is present.
     *
     * @throws MalformedOutputException if a required field is missing or has the wrong type —
     *     {@code candidate} is never silently passed through as valid
     */
    public ValidatedOutput enforce(
            CandidateOutput candidate, OutputSchema schema, BudgetConstraints budgetConstraints) {
        Objects.requireNonNull(candidate, "candidate");
        Objects.requireNonNull(schema, "schema");

        validateSchema(candidate, schema);
        return enforceLength(candidate, budgetConstraints);
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
            CandidateOutput candidate, BudgetConstraints budgetConstraints) {
        Integer maxOutputTokens =
            budgetConstraints == null ? null : budgetConstraints.maxOutputTokens();
        if (maxOutputTokens == null) {
            int tokenCount = Holder.CL100K_BASE.countTokens(candidate.content());
            return new ValidatedOutput(candidate.content(), false, tokenCount);
        }

        // Reviewer finding (2026-10-08, reproduced directly against the project's own jtokkit
        // 1.1.0 jar): Encoding.encode(text, maxTokens) does not reliably bound output or set
        // isTruncated() when maxTokens <= 0 -- it is undefined/buggy at that boundary, not merely
        // unverified. BudgetConstraints.maxOutputTokens (interfaces.md Sec2.2) declares no minimum
        // or positivity constraint, so 0/negative is a legitimately representable input this
        // method must handle explicitly, not an impossible one. A non-positive budget means "no
        // output tokens are permitted" -- content is truncated to empty, marked truncated unless
        // the candidate was already empty.
        if (maxOutputTokens <= 0) {
            int originalTokenCount = Holder.CL100K_BASE.countTokens(candidate.content());
            if (originalTokenCount == 0) {
                return new ValidatedOutput(candidate.content(), false, 0);
            }
            return new ValidatedOutput("", true, 0);
        }

        EncodingResult result = Holder.CL100K_BASE.encode(candidate.content(), maxOutputTokens);
        IntArrayList tokens = result.getTokens();
        if (!result.isTruncated()) {
            return new ValidatedOutput(candidate.content(), false, tokens.size());
        }
        String truncatedContent = Holder.CL100K_BASE.decode(tokens);
        return new ValidatedOutput(truncatedContent, true, tokens.size());
    }
}
