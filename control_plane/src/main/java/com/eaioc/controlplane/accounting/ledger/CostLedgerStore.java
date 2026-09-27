package com.eaioc.controlplane.accounting.ledger;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Tenant-scoped, in-memory reference store for {@link CostLedgerEntry} records.
 *
 * <p><b>{@code @Component}, added in {@code EXE-P0.1.C}:</b> registers this store in the Spring
 * application context so any future T0–T3 pipeline stage or technique (none exist yet in this
 * execution slice) can receive it via constructor injection without this class needing to grow an
 * ever-longer explicit registration list. This is what "wire the ledger as the substrate" means
 * concretely for this sub-phase ({@code docs/execution-plan.md} §18.4's {@code EXE-P0.1.C} row) —
 * the class's own read/write behavior, already built in {@code EXE-P0.1.B}, is unchanged.
 *
 * <p><b>In-memory only, deliberately:</b> {@code docs/execution-plan.md} §12 classifies the
 * token/cost ledger as an "in-memory reference store for P0; durable store gated by
 * ADR-0001/0002" — a durable backing technology is an open, ADR-gated decision this sub-phase does
 * not make. This mirrors the interim-reference-implementation pattern {@code ADR-0001} already
 * establishes for the cache backing store.
 *
 * <p><b>Tenant isolation:</b> the outer map key is {@code tenantId} — the first namespace
 * component, per root {@code CLAUDE.md} rule 4 and this capability's own Definition of Done
 * ("every record has tenant_id as first namespace component," {@code docs/execution-plan.md}
 * §18.4). A caller can only ever look up records inside the tenant partition it names; there is no
 * method on this class that can return another tenant's data. The explicit adversarial
 * cross-tenant-read test is this capability's Sub-phase E's own responsibility
 * ({@code docs/execution-plan.md} §18.4's Sub-phase E row) — this class's structure is what makes
 * that test expected to pass, not a re-implementation of it.
 *
 * <p><b>EC-077 (adversarial optimization-cost input, cited cross-cutting):</b> both operations here
 * are {@code O(1)} hash-map lookups with no per-write computation proportional to ledger size, so
 * this store's own write/read path cannot itself become a cost-amplification surface. Enforcing a
 * per-tenant optimization-overhead *budget* is {@code OI-002}'s job (a P4 capability, out of this
 * slice's scope) — not re-derived or reimplemented here.
 *
 * <p><b>Observability, added in {@code EXE-P0.1.D}:</b> every successful write emits one structured
 * log line via {@link LedgerObservability} — see that class's Javadoc for the interim-sink and
 * metric-naming decisions this sub-phase made.
 */
@Component
public class CostLedgerStore {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, CostLedgerEntry>> byTenant =
        new ConcurrentHashMap<>();

    /**
     * Writes a new ledger entry.
     *
     * <p>{@code entry.tenantId()} can never be blank — {@link CostLedgerEntry}'s own compact
     * constructor already rejects that at construction time, so there is no redundant check here.
     * This method's own responsibility is enforcing the ledger's append-only property.
     *
     * @throws DuplicateLedgerEntryException if {@code entry.entryId()} already exists for this
     *     tenant.
     */
    public void write(CostLedgerEntry entry) {
        ConcurrentHashMap<String, CostLedgerEntry> tenantEntries =
            byTenant.computeIfAbsent(entry.tenantId(), t -> new ConcurrentHashMap<>());

        CostLedgerEntry previous = tenantEntries.putIfAbsent(entry.entryId(), entry);
        if (previous != null) {
            throw new DuplicateLedgerEntryException(
                "Ledger is append-only: entryId=" + entry.entryId()
                    + " already exists for tenantId=" + entry.tenantId());
        }

        // EXE-P0.1.D (Observability): one structured log line per successful write, citing
        // observability.md's already-named metrics — see LedgerObservability's own Javadoc for
        // why this cites different literal names than EXE-P0.1.D's row examples.
        LedgerObservability.logLedgerWrite(entry);
    }

    /**
     * Reads back a previously written entry, scoped to the given tenant.
     *
     * <p>Returns {@link Optional#empty()} both when the entry does not exist and when it exists
     * under a different tenant — the two cases are indistinguishable from the outside, which is the
     * point: a caller supplying {@code tenantId} A has no way to observe that an entry exists under
     * {@code tenantId} B.
     */
    public Optional<CostLedgerEntry> read(String tenantId, String entryId) {
        Map<String, CostLedgerEntry> tenantEntries = byTenant.get(tenantId);
        if (tenantEntries == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(tenantEntries.get(entryId));
    }

    /**
     * Returns every entry in {@code tenantId}'s own partition whose {@code requestId} matches —
     * added by {@code REM-P0.2.B-02} ({@code docs/execution-plan.md} §18.14.8) purely to let
     * {@link LedgerCostReporter} determine, for one {@code (tenant_id, request_id)}, how many ledger
     * entries match before applying the {@code verified = true} test to a single one of them — the
     * "count-all-then-verify" rule ({@code interfaces.md} §18 P0 realization notes, human contract
     * decision of 2026-09-25). The count is taken over every matching entry regardless of
     * {@link CostLedgerEntry#verified()}; that filter is deliberately not applied here so the caller
     * can distinguish "several matches, one verified" (no measurement) from "exactly one match,
     * verified" (a measurement).
     *
     * <p><b>Deliberately package-private, not public.</b> {@code CostLedgerTenantIsolationTest}
     * (Capability 1, Gate P0.1 approved) structurally guards this class's *public* API surface down
     * to exactly {@code write} and {@code read} — "no list-all/iterate-all-style method may exist at
     * all" on it — and that Gate is not reopened by this remediation (§18.14.8: "That Gate approval
     * is not revoked or reopened"). Keeping this enumeration helper package-private satisfies that
     * guard exactly (it never appears in {@code CostLedgerStore.class.getMethods()}) while still
     * letting {@link LedgerCostReporter}, in this same package, reach it. This is also why
     * {@code LedgerCostReporter} lives in {@code accounting.ledger} rather than a separate
     * {@code accounting.reporting} package: package-private access does not cross package
     * boundaries, even between a package and its own sub-package.
     *
     * <p>{@code write} and {@code read(tenantId, entryId)} above are unchanged by this method: it
     * only reads, and only from {@code tenantId}'s own map entry in {@link #byTenant}, so it is
     * exactly as tenant-scoped as {@link #read} — there is no way to reach another tenant's
     * partition through this method either. Returns an empty, immutable list when the tenant has no
     * entries at all; never {@code null}.
     */
    List<CostLedgerEntry> findByRequestId(String tenantId, String requestId) {
        Map<String, CostLedgerEntry> tenantEntries = byTenant.get(tenantId);
        if (tenantEntries == null) {
            return List.of();
        }
        return tenantEntries.values().stream()
            .filter(entry -> entry.requestId().equals(requestId))
            .toList();
    }
}
