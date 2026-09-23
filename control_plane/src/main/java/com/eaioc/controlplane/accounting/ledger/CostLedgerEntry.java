package com.eaioc.controlplane.accounting.ledger;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Per-request cost/token ledger record.
 *
 * <p>Schema: {@code interfaces.md} §28.1, {@code INTF-047 CostLedgerEntry} — chosen over
 * {@code architecture.md} §27.1's dot-notation field catalog because it is the authoritative,
 * directly-implementable contract (interfaces.md's stated purpose is exactly "contracts/schemas
 * crossing component boundaries"); §27.1 reads as a broader telemetry field catalog that does not
 * map onto one single record shape. This discrepancy was flagged, not silently resolved, in
 * {@code control_plane/accounting/ledger/LedgerEntry.md} §4 (EXE-P0.1.A) and is resolved here as
 * this sub-phase's own decision, not asserted as an upstream fact.
 *
 * <p><b>One field beyond INTF-047's literal list:</b> {@link #verified()}. Root {@code CLAUDE.md}
 * rule 5 ("never fabricate savings... mark it unverified and exclude it from savings reporting")
 * has no carrier field in INTF-047 as written. This is the minimal addition needed to satisfy that
 * non-negotiable rule, not a broader schema redesign — every other field matches INTF-047 exactly.
 *
 * <p><b>Canonical §27.1 groups ({@code REM-P0.1.B-02}, closing {@code CONTRA-EXECPLAN-01}):</b>
 * the corrected {@code INTF-047} ({@code interfaces.md} §28.1) adds all 58 {@code architecture.md}
 * §27.1 Standard Ledger Fields as nine nested groups ({@link Input} … {@link Quality}), so AC-005's
 * cached, compressed, removed, generated, retrieved and reused tokens are each recorded
 * distinctly. The 29 fields above are retained unchanged; none is an alias of a canonical member.
 * Design: {@code control_plane/accounting/ledger/LedgerContractReconciliation.md}.
 *
 * <p>Immutable by construction (Java {@code record}). The compact constructor enforces tenant
 * scoping structurally: a {@code CostLedgerEntry} cannot exist without a {@code tenantId}, so there
 * is no "add tenant scoping later" step (root {@code CLAUDE.md} rule 4; restated in
 * {@code implementation-plan.md} §8.1 and this capability's own header in
 * {@code docs/execution-plan.md} §18.4).
 */
public record CostLedgerEntry(
    String entryId,
    String requestId,
    String tenantId,
    String organizationId,
    String agentId,          // nullable — INTF-047: "agent_id: string | null"
    String sessionId,        // nullable — INTF-047: "session_id: string | null"
    String taskId,
    Instant timestamp,

    // TOKEN ECONOMY (INTF-047)
    long baselineInputTokens,
    long baselineOutputTokens,
    long actualInputTokens,
    long actualOutputTokens,
    long cachedTokens,
    long tokensAvoidedByPruning,
    long tokensAvoidedByCache,
    long tokensAvoidedByEarlyExit,
    long optimizerOverheadTokens,

    // COST (INTF-047)
    double baselineCost,
    double actualCost,
    double cachedCostSaved,
    double optimizerCost,
    double grossSavings,
    double netSavings,
    double netSavingsPct,

    // ATTRIBUTION (INTF-047)
    Map<String, Double> savingsByStage,
    String currency,
    String pricingVersion,
    String modelId,
    String providerId,

    // CANONICAL ARCH §27.1 STANDARD LEDGER FIELDS — corrected INTF-047 (interfaces.md §28.1),
    // realized per REM-P0.1.B-01 (LedgerContractReconciliation.md). Independent of the retained
    // fields above: no retained field is an alias of any canonical member (D-2).
    Input input,
    Output output,
    Cache cache,
    Model model,
    Tools tools,
    Workflow workflow,
    Cost cost,
    Performance performance,
    Quality quality,

    // Addition beyond INTF-047 — see class Javadoc.
    boolean verified
) {

    /**
     * Compact constructor: structural tenant-scoping enforcement.
     *
     * <p>Root {@code CLAUDE.md} rule 4 requires tenant scoping "from the first namespace
     * component," with no exception. Validating here — at construction, not at store-write time —
     * means it is impossible to hold a {@code CostLedgerEntry} instance without a tenant, closing
     * off any future code path that could accidentally skip the check (matches the failure-path
     * requirement in {@code docs/execution-plan.md} §18.4's Sub-phase B row: "write with missing
     * tenant_id is rejected, not silently defaulted").
     */
    public CostLedgerEntry {
        if (tenantId == null || tenantId.isBlank()) {
            throw new MissingTenantIdException(
                "CostLedgerEntry requires a non-blank tenantId (root CLAUDE.md rule 4); "
                    + "entryId=" + entryId + ", requestId=" + requestId);
        }
        Objects.requireNonNull(entryId, "entryId must not be null");
        Objects.requireNonNull(requestId, "requestId must not be null");
        Objects.requireNonNull(timestamp, "timestamp must not be null");
        // savingsByStage is copied defensively so a caller's mutable map can't alter an
        // already-constructed, supposedly-immutable ledger entry after the fact.
        savingsByStage = savingsByStage == null ? Map.of() : Map.copyOf(savingsByStage);
        // Every canonical group is part of the complete ledger contract (conventions.md §14.1).
        Objects.requireNonNull(input, "input group must not be null");
        Objects.requireNonNull(output, "output group must not be null");
        Objects.requireNonNull(cache, "cache group must not be null");
        Objects.requireNonNull(model, "model group must not be null");
        Objects.requireNonNull(tools, "tools group must not be null");
        Objects.requireNonNull(workflow, "workflow group must not be null");
        Objects.requireNonNull(cost, "cost group must not be null");
        Objects.requireNonNull(performance, "performance group must not be null");
        Objects.requireNonNull(quality, "quality group must not be null");
    }

    /**
     * Constructs the recovery-path entry for {@code EXE-P0.1.H} (Failure/Recovery): when an
     * upstream accounting computation fails partway — {@code SCN-AUDIT-003}'s own example is "the
     * provider didn't return usage data" — the caller cannot honestly populate the token/cost
     * fields. Root {@code CLAUDE.md} rule 5 forbids two wrong responses to that: silently dropping
     * the record entirely (which would under-count activity without any trace) and guessing a
     * plausible-looking value and marking it verified (fabrication). This factory is the third,
     * correct option: every token/cost field is recorded as {@code 0} — never guessed — and
     * {@link #verified()} is forced {@code false}, so the record is preserved (nothing is silently
     * lost) while being structurally impossible to mistake for a real, countable figure.
     *
     * <p>The resulting entry still passes through {@link CostLedgerStore#write}'s normal append-only
     * path — recovery from a computation failure is not a special write mode, only a specially
     * constructed entry.
     *
     * <p><b>Canonical groups ({@code REM-P0.1.B-02}):</b> every non-nullable canonical member holds
     * its DB-1 placeholder ({@code 0} / {@code 0.0} / {@code false} / {@code "unknown"}), which
     * {@code interfaces.md} §28.1 permits only in a {@code verified = false} entry and which is never
     * counted as a measured value. The two "where available" members —
     * {@link Model#reasoningTokens()} and {@link Cost#tool()} — are {@code null}, never zero (HD-4).
     */
    public static CostLedgerEntry unverifiedFallback(
        String entryId, String requestId, String tenantId, String organizationId,
        String taskId, Instant timestamp) {
        return new CostLedgerEntry(
            entryId, requestId, tenantId, organizationId, null, null, taskId, timestamp,
            0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            Map.of(), "USD", "unknown", "unknown", "unknown",
            new Input(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
            new Output(0L, 0L, 0L, 0L),
            new Cache(0L, 0L, 0L, 0L, 0L, 0L, 0L),
            new Model(UNKNOWN, UNKNOWN, UNKNOWN, false, UNKNOWN, null),
            new Tools(0L, 0L, 0L, 0L, 0L),
            new Workflow(0L, 0L, 0L, 0L, 0L),
            new Cost(0.0, 0.0, 0.0, 0.0, null, 0.0, 0.0, 0.0, 0.0),
            new Performance(0L, 0L, 0L, 0L, 0L, 0L),
            new Quality(0.0, 0.0, false, 0.0, 0.0, false),
            false);
    }

    /** DB-1 placeholder for an unmeasurable non-nullable {@code string} member. */
    private static final String UNKNOWN = "unknown";

    // ---- Canonical ARCH §27.1 groups (interfaces.md §28.1). Member types per the corrected
    // contract: integer -> long, float -> double, "| null" -> boxed; HD-1..HD-4 decided the 22
    // formerly SOURCE-UNRESOLVED types. Only Model.reasoningTokens and Cost.tool are nullable (HD-4);
    // an unmeasurable non-nullable member means the whole entry must be verified = false.

    /** §27.1 INPUT — token counts ({@code tokens.*}). */
    public record Input(
        long rawInput,
        long sanitized,
        long queryCompressed,       // AC-005: compressed
        long context,
        long retrieved,             // AC-005: retrieved
        long pruned,
        long deduplicated,
        long contextCompressed,     // AC-005: compressed
        long cachedInput,           // AC-005: cached
        long uncachedInput
    ) {
    }

    /** §27.1 OUTPUT — token counts ({@code tokens.*}). */
    public record Output(
        long rawOutput,
        long optimizedOutput,
        long truncated,
        long expandedRetry
    ) {
    }

    /** §27.1 CACHE — event counters (HD-1) and token counts. */
    public record Cache(
        long exactHits,
        long semanticHits,
        long misses,
        long writes,
        long reads,
        long cacheableTokens,
        long reusedTokens           // AC-005: reused
    ) {
    }

    /**
     * §27.1 MODEL — {@code selected}, {@code candidate}, {@code routingDecision},
     * {@code reasoningBudget} are provider-neutral strings (HD-2; no enum invented); {@code escalation}
     * records whether escalation occurred. {@code reasoningTokens} is "where available" and nullable
     * (HD-4). The four strings must be non-null — HD-4 permits no other nulls.
     */
    public record Model(
        String selected,
        String candidate,
        String routingDecision,
        boolean escalation,
        String reasoningBudget,
        Long reasoningTokens        // nullable — PS §8 "where available"
    ) {

        public Model {
            Objects.requireNonNull(selected, "model.selected must not be null (HD-4)");
            Objects.requireNonNull(candidate, "model.candidate must not be null (HD-4)");
            Objects.requireNonNull(routingDecision, "model.routingDecision must not be null (HD-4)");
            Objects.requireNonNull(reasoningBudget, "model.reasoningBudget must not be null (HD-4)");
        }
    }

    /** §27.1 TOOLS — call counters (HD-1) and token counts. */
    public record Tools(
        long callsAttempted,
        long callsAvoided,
        long outputTokens,
        long filteredTokens,
        long cachedCalls
    ) {
    }

    /** §27.1 WORKFLOW — step/event counters (HD-1). */
    public record Workflow(
        long stepsPlanned,
        long stepsExecuted,
        long stepsSkipped,
        long earlyExits,
        long retries
    ) {
    }

    /** §27.1 COST — monetary amounts in the entry's {@code currency}; {@code savingsPct} per ARCH §27.3. */
    public record Cost(
        double input,
        double output,
        double cache,
        double compression,
        Double tool,                // nullable — PS §8 "where available"
        double totalOptimized,
        double baselineEstimated,
        double netSavings,
        double savingsPct
    ) {
    }

    /** §27.1 PERFORMANCE — milliseconds. */
    public record Performance(
        long e2eLatencyMs,
        long ttftMs,                // HD-3
        long modelLatencyMs,
        long compressionLatencyMs,
        long cacheLatencyMs,
        long toolLatencyMs
    ) {
    }

    /**
     * §27.1 QUALITY — scores in {@code [0.0, 1.0]}; {@code schemaCompliance} and
     * {@code safetyValidation} are boolean validation outcomes, not scores (HD-3).
     */
    public record Quality(
        double correctnessScore,
        double relevanceScore,
        boolean schemaCompliance,
        double semanticPreservation,
        double userTaskScore,
        boolean safetyValidation
    ) {
    }
}
