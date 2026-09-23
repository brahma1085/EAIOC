package com.eaioc.controlplane.accounting.ledger;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tenant-scoped, in-memory reference store for {@link CostLedgerEntry} records.
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
 */
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
}
