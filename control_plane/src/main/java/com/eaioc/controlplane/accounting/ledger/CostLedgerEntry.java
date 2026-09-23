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
     */
    public static CostLedgerEntry unverifiedFallback(
        String entryId, String requestId, String tenantId, String organizationId,
        String taskId, Instant timestamp) {
        return new CostLedgerEntry(
            entryId, requestId, tenantId, organizationId, null, null, taskId, timestamp,
            0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            Map.of(), "USD", "unknown", "unknown", "unknown",
            false);
    }
}
