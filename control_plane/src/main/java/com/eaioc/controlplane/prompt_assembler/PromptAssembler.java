package com.eaioc.controlplane.prompt_assembler;

import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;
import com.eaioc.controlplane.core.schemas.InstructionContext;
import com.eaioc.controlplane.pipeline.t1_context.sanitizer.SanitizerOutput;
import com.eaioc.controlplane.policy.context_policy.BudgetEnforcementResult;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EXE-P0.5.B (Capability 5, Prompt Assembler — Minimal Implementation): the assembly hook named by
 * {@code execution-plan.md} §18.8's B row, realizing {@code architecture.md} §12.2's structural
 * separation with Capability 3's sanitized input and Capability 1's/4's already-produced signals.
 *
 * <p><b>Scope (human decisions HD-CG-P0.5-01 to -04, 2026-10-07):</b>
 * <ul>
 *   <li><b>Output shape (HD-CG-P0.5-01):</b> produces {@link AssembledPrompt}, not a full
 *       {@code architecture.md} §12.2 "assembly ledger" realization — none is defined anywhere in
 *       the corpus ({@code SOURCE-GAP-EXECPLAN-36}).</li>
 *   <li><b>Budget signal (HD-CG-P0.5-02/-03):</b> accepts an already-computed {@link
 *       BudgetEnforcementResult} as a parameter — never calls {@code PolicyBudgetEnforcer} itself,
 *       mirroring HD-CG-P0.4-02's own pattern — but does not act on its {@code compliant()} value:
 *       budget enforcement is Capability 4's own gate, already built; this unit does not duplicate
 *       or second-guess it. The parameter exists (and is required non-null) only so a caller cannot
 *       silently skip the upstream policy step entirely — its content is otherwise unused here.</li>
 *   <li><b>Context scope (HD-CG-P0.5-04):</b> "include only approved context" / "policy-bounded
 *       context" (§12.2, the B row's own deliverable text) is vacuously satisfied at P0 scope — no
 *       retrieval/memory layer exists anywhere in P0 ({@code CORE-GAP-01}), so the SEMI-STABLE
 *       REGION this unit produces is always empty unless a future {@link InstructionContext} is
 *       itself marked {@link InstructionContext.Stability#SEMI_STABLE}.</li>
 *   <li><b>Two further §12.2 responsibilities, vacuously satisfied for the same reason as
 *       HD-CG-P0.5-04, disclosed explicitly here (Reviewer/Verifier finding, 2026-10-07):</b>
 *       "Select intent-specific instructions" — no intent-classification component exists anywhere
 *       in P0 ({@code architecture.md} §11.2's T1.2 Intent Classifier is not one of the six P0
 *       capabilities, nor named in any §36 Implementation Priority tier), and {@code
 *       ControlPlaneRequest} carries no realized intent signal to select
 *       with; {@code systemContext}/{@code developerContext} are each a single, already-resolved
 *       instance, not a candidate set this method selects from. "Tool definitions" (one of
 *       CACHEABLE PREFIX's listed contents) — no tool-schema type is realized anywhere in P0
 *       ({@code CORE-GAP-01}; {@code ControlPlaneRequest} has no {@code available_tools} field).
 *       Neither is implemented, and neither needs its own new {@code SOURCE-GAP-EXECPLAN-NN} entry
 *       — both are the same pre-existing {@code CORE-GAP-01} absence HD-CG-P0.5-04 already cites.</li>
 * </ul>
 *
 * <p><b>Structural separation (§12.2, realized by {@link InstructionContext#stability()}, not by
 * {@link InstructionContext#type()}):</b> {@link ControlPlaneRequest#systemContext()} and {@link
 * ControlPlaneRequest#developerContext()} (both nullable) are routed to the CACHEABLE PREFIX,
 * SEMI-STABLE REGION, or VOLATILE/DYNAMIC SUFFIX strictly by their own declared {@code stability}
 * field — {@code STABLE}/{@code SEMI_STABLE}/{@code VOLATILE} map one-to-one onto §12.2's three
 * named regions. Within each region, content is appended in §12.2's own listed order: for CACHEABLE
 * PREFIX, system before developer ("System instructions, stable developer policy..."); for
 * VOLATILE/DYNAMIC SUFFIX, the sanitized user input ({@link SanitizerOutput#sanitizedInput()},
 * "current user request") always leads, before any VOLATILE-stability instruction content
 * ("current task state" or similar) — §12.2's own listed order is user request first, task state
 * last, applied consistently across both regions (an earlier draft of this unit applied the
 * principle to one region but not the other; corrected before commit, Reviewer finding).
 *
 * <p>When two instruction blocks share a region, they are joined with a blank-line separator for
 * readability. No source specifies this separator; it carries no semantic meaning and is never
 * relied on by any test beyond confirming both contents are present.
 *
 * <p><b>Security-instruction preservation (SEC-001, root {@code CLAUDE.md} rule 6):</b> {@code
 * systemContext}/{@code developerContext} content is always appended verbatim, in full, to exactly
 * one region — never dropped, truncated, or altered by this method.
 *
 * <p><b>Final token count (§12.2, "Produce final token counts"):</b> the sum of {@code
 * systemContext}/{@code developerContext}'s own caller-supplied {@code tokenEstimate} (unverified —
 * no counter re-derives it here, to avoid a second, possibly-divergent count) plus {@link
 * SanitizerOutput#tokenCountPost()} (Capability 3's own verified, counted post-sanitization count
 * for the user input). This is a sum of mixed provenance, disclosed here rather than presented as a
 * single uniformly-verified figure.
 *
 * <p><b>Failure path:</b> a missing {@link SanitizerOutput} (the upstream Sanitizer step never ran,
 * or failed and produced none — see {@code SanitizerOutput}'s own Javadoc) blocks assembly: this
 * method throws {@link NullPointerException} rather than assembling with a silently-defaulted gap,
 * mirroring the precondition-guard pattern already used by {@code Sanitizer}/{@code
 * PolicyBudgetEnforcer}. A non-compliant {@code BudgetEnforcementResult} does not block assembly
 * (HD-CG-P0.5-03) — only a missing one does (programmer error, not a policy outcome).
 *
 * <p><b>Observability (EXE-P0.5.D, plan §18.8 D row):</b> exactly one structured log line per
 * successful call. Unlike {@code Sanitizer}/{@code PolicyBudgetEnforcer}, {@code assemble()} has no
 * conditional outcome to distinguish by level — it either throws (missing required input, no line
 * logged, matching the precondition-guard precedent) or succeeds — so a single INFO line covers
 * every call that returns a result, per {@code conventions.md} §17.2's "normal optimization
 * decisions" row. No metric or event name is cited from any source: {@code architecture.md} §23/§36
 * and {@code observability.md} name no Prompt-Assembler-specific metric, and — checked directly,
 * learning from the {@code POLICY_VIOLATION} citation lesson of {@code EXE-P0.4.D} — {@code
 * interfaces.md} §23.2's Standard Event Types table has no entry matching "ASSEMBL*"/"PROMPT*"
 * either, so {@code PROMPT_ASSEMBLED} below is genuinely local to this unit, not a citation.
 * No {@code OptimizationSpan} exists in P0 ({@code conventions.md} §17.1 requires one per component
 * invocation): {@code span_id} below is a fresh per-line {@link UUID} linking to no real span —
 * the same disclosed, not-claimed-as-realized gap every other P0 log line carries ({@code
 * SOURCE-GAP-EXECPLAN-29}), explicitly cited here (Reviewer/Verifier finding, 2026-10-07) rather
 * than silently repeating the same behavior without the same disclosure its two named precedents
 * both give.
 * {@code component_id} is {@code PROMPT-ASSEMBLER} — no canonical architecture ID exists for this
 * capability (§1 above), following the same plain, non-numbered precedent as {@code
 * ACCOUNTING-LEDGER}/{@code EVALUATION-BASELINE}/{@code CONTEXT-POLICY}. No region content (which
 * may include security-classified instructions, SEC-001) is logged — only identifiers and the
 * final token count.
 */
public final class PromptAssembler {

    private static final Logger LOG = LoggerFactory.getLogger(PromptAssembler.class);

    public AssembledPrompt assemble(
            ControlPlaneRequest request, SanitizerOutput sanitized, BudgetEnforcementResult budgetResult) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(sanitized, "sanitized");
        Objects.requireNonNull(budgetResult, "budgetResult");

        StringBuilder cacheablePrefix = new StringBuilder();
        StringBuilder semiStableRegion = new StringBuilder();
        StringBuilder volatileSuffix = new StringBuilder();
        int tokenCount = 0;

        // The sanitized user input is appended to volatileSuffix FIRST, before systemContext/
        // developerContext are routed: §12.2's own VOLATILE/DYNAMIC SUFFIX order is "current user
        // request, current retrieved context, current tool results, current task state" -- the
        // user request leads, so any VOLATILE-stability instruction (e.g. current task state)
        // must be appended after it, never before.
        append(volatileSuffix, sanitized.sanitizedInput());
        tokenCount += sanitized.tokenCountPost();

        tokenCount += route(request.systemContext(), cacheablePrefix, semiStableRegion, volatileSuffix);
        tokenCount += route(request.developerContext(), cacheablePrefix, semiStableRegion, volatileSuffix);

        AssembledPrompt result = new AssembledPrompt(
            cacheablePrefix.toString(), semiStableRegion.toString(), volatileSuffix.toString(), tokenCount);
        logAssembled(request, result);
        return result;
    }

    /**
     * Exactly one structured INFO line per successful call (conventions.md §17.2: "normal
     * optimization decisions"). {@code correlation_id} is {@code ControlPlaneRequest}'s own
     * required field (never null), unlike {@code Sanitizer}'s request fixture, so no fallback is
     * needed here.
     */
    private static void logAssembled(ControlPlaneRequest request, AssembledPrompt result) {
        LOG.info(
            "event_type=PROMPT_ASSEMBLED component_id=PROMPT-ASSEMBLER "
                + "message=\"prompt assembly completed\" "
                + "tenant_id={} request_id={} correlation_id={} span_id={} parent_span_id=null "
                + "fields={}",
            request.tenantId(),
            request.requestId(),
            request.correlationId(),
            UUID.randomUUID(),
            Map.of("final_token_count", result.finalTokenCount()));
    }

    /** Routes {@code instruction} (if present) to the region its own {@code stability} names; returns its token estimate. */
    private static int route(
            InstructionContext instruction,
            StringBuilder cacheablePrefix, StringBuilder semiStableRegion, StringBuilder volatileSuffix) {
        if (instruction == null) {
            return 0;
        }
        switch (instruction.stability()) {
            case STABLE -> append(cacheablePrefix, instruction.content());
            case SEMI_STABLE -> append(semiStableRegion, instruction.content());
            case VOLATILE -> append(volatileSuffix, instruction.content());
        }
        return instruction.tokenEstimate();
    }

    /** Appends {@code content} to {@code region}, joined by a blank line when the region already has content. */
    private static void append(StringBuilder region, String content) {
        if (region.length() > 0) {
            region.append("\n\n");
        }
        region.append(content);
    }
}
