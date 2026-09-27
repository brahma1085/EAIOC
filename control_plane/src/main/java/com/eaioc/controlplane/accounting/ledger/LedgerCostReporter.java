package com.eaioc.controlplane.accounting.ledger;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Realizes the {@code get_request_cost(request_id, tenant_id) -> CostLedgerEntry | null} subset of
 * {@code interfaces.md} §28.2, {@code INTF-048 CostReporter} — {@code REM-P0.2.B-02},
 * {@code docs/execution-plan.md} §18.14.8.
 *
 * <p><b>Only this one method of {@code CostReporter} is realized.</b> {@code get_session_cost},
 * {@code get_tenant_cost} and {@code get_cost_breakdown_by_stage} are not declared here — their
 * return types ({@code SessionCostSummary}, {@code TenantCostSummary}, {@code DateRange}) are not
 * defined anywhere in this corpus, and {@code interfaces.md} §28.2's own P0 realization note says so
 * explicitly. Adding a Java {@code CostReporter} interface for one-quarter of a contract whose other
 * three members have no defined return type would misrepresent how much of INTF-048 exists; this
 * class is deliberately a concrete, minimal realization instead, named after the interface it
 * partially realizes so a future unit that completes INTF-048 has an obvious place to look.
 *
 * <p><b>Same package as {@link CostLedgerStore}, not a separate {@code accounting.reporting}
 * package.</b> {@code interfaces.md} keeps {@code INTF-047 CostLedgerEntry} (§28.1) and
 * {@code INTF-048 CostReporter} (§28.2) as two separate interface numbers, and a genuinely
 * independent {@code reporting} package was this unit's first design. It was rejected because this
 * class needs to enumerate the entries matching a {@code (tenant_id, request_id)} pair before
 * applying the verified-only rule below, and {@code CostLedgerStore}'s existing, Gate-P0.1-approved
 * {@code CostLedgerTenantIsolationTest} structurally guards that class's *public* API surface down to
 * exactly {@code write} and {@code read} — enumeration had to be added as a package-private helper
 * ({@link CostLedgerStore#findByRequestId}, see its Javadoc) instead of a public one, and
 * package-private access does not cross package boundaries. Staying in this package is what lets
 * this class reach that helper without widening {@code CostLedgerStore}'s public surface or touching
 * the Gate's own test.
 *
 * <p><b>Count-all-then-verify (human contract decision of 2026-09-25; {@code interfaces.md} §18 P0
 * realization notes, "Retrieval and consumption rule (D-A)").</b> For one {@code (tenant_id,
 * request_id)}, cardinality is determined across <em>every</em> matching ledger entry, whatever its
 * {@link CostLedgerEntry#verified()} value; only when there is exactly one matching entry is the
 * {@code verified = true} test applied to it. Zero matching entries, more than one matching entry
 * (including one verified entry plus one unverified entry — that is "several", not "one"), and a
 * single matching entry that is not {@code verified = true} are three distinct situations, and this
 * class deliberately returns {@link Optional#empty()} for all three without exposing which one
 * occurred: {@code interfaces.md} defines no public error, error code or schema that distinguishes
 * them (the outcome is a plain "no measurement"; INTF-048's {@code CostLedgerEntry | null} signature
 * carries no such distinction either), and this class never chooses among several matches or selects
 * the most recent one.
 */
@Component
public class LedgerCostReporter {

    private final CostLedgerStore store;

    public LedgerCostReporter(CostLedgerStore store) {
        this.store = store;
    }

    /**
     * Returns the single {@code verified = true} ledger entry for {@code (tenantId, requestId)}, or
     * {@link Optional#empty()} when no valid baseline measurement can be identified.
     *
     * <p>Parameter order matches {@code interfaces.md} §28.2's contract text
     * ({@code get_request_cost(request_id, tenant_id)}) rather than this package's own
     * {@code tenantId}-first Java convention elsewhere ({@link CostLedgerStore#read}), so a caller
     * reading this signature against the authoritative source sees the same order.
     */
    public Optional<CostLedgerEntry> getRequestCost(String requestId, String tenantId) {
        List<CostLedgerEntry> matches = store.findByRequestId(tenantId, requestId);
        if (matches.size() != 1) {
            // Zero matches, or several (count-all-then-verify: a verified entry plus an unverified
            // one is "several") -> no measurement. Never resolved by picking one.
            return Optional.empty();
        }
        CostLedgerEntry onlyMatch = matches.get(0);
        return onlyMatch.verified() ? Optional.of(onlyMatch) : Optional.empty();
    }
}
