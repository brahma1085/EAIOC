package com.eaioc.controlplane.accounting.ledger;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * {@code EXE-P0.1.E} (Capability 1 — Token Accounting and Cost Ledger, Sub-phase E —
 * Security/Governance), per {@code docs/execution-plan.md} §18.4's Sub-phase E row: "Tenant
 * isolation enforced structurally, not by convention alone" / Definition of Done: "No code path
 * can construct a cross-tenant read."
 *
 * <p>Separate from {@code CostLedgerStoreTest}'s own basic tenant-scoping check ({@code
 * EXE-P0.1.B}) — that test confirms a wrong-tenant read for an entry that exists elsewhere returns
 * empty. This class goes further, per this sub-phase's own dedicated mandate: it tests the
 * genuinely adversarial case (colliding {@code entryId} values across tenants, an unknown-tenant
 * query, and — the strongest form of "no code path can construct a cross-tenant read" — a
 * reflection-based assertion that {@link CostLedgerStore}'s public API surface contains no method
 * that could return data without a caller-supplied {@code tenantId} at all).
 */
class CostLedgerTenantIsolationTest {

    @Test
    void collidingEntryId_acrossTwoTenants_neverCrossContaminates() {
        CostLedgerStore store = new CostLedgerStore();

        // Adversarial case: the same entryId is legitimately reused by two different tenants
        // (entryId uniqueness is only ever scoped per-tenant — nothing in this capability's
        // contract makes entryId globally unique). If the store's internal structure were ever a
        // single flat map instead of a map-of-maps, this is exactly the case that would leak.
        CostLedgerEntry tenantAEntry = entryFor("tenant-a", "shared-entry-id", 1000L);
        CostLedgerEntry tenantBEntry = entryFor("tenant-b", "shared-entry-id", 2000L);

        store.write(tenantAEntry);
        store.write(tenantBEntry);

        Optional<CostLedgerEntry> readAsA = store.read("tenant-a", "shared-entry-id");
        Optional<CostLedgerEntry> readAsB = store.read("tenant-b", "shared-entry-id");

        assertThat(readAsA).contains(tenantAEntry);
        assertThat(readAsA.get().actualInputTokens()).isEqualTo(1000L);
        assertThat(readAsB).contains(tenantBEntry);
        assertThat(readAsB.get().actualInputTokens()).isEqualTo(2000L);
        // Never each other's data, even though the entryId is identical.
        assertThat(readAsA.get()).isNotEqualTo(readAsB.get());
    }

    @Test
    void crossTenantRead_returnsNotFoundEquivalent_neverAnotherTenantsData() {
        CostLedgerStore store = new CostLedgerStore();
        store.write(entryFor("tenant-a", "entry-1", 500L));

        // This capability's API has no authorization/visibility distinction to express a
        // separate FORBIDDEN status — the structural guarantee is stronger than "visible but
        // denied": the record is not observable at all from tenant-b's own partition. This
        // satisfies docs/execution-plan.md §18.4's Failure-Path Test as its NOT_FOUND variant.
        Optional<CostLedgerEntry> crossTenantAttempt = store.read("tenant-b", "entry-1");

        assertThat(crossTenantAttempt).isEmpty();
    }

    @Test
    void read_entirelyUnknownTenant_returnsEmpty_doesNotThrow() {
        CostLedgerStore store = new CostLedgerStore();
        // No write has ever happened for "tenant-never-seen" — the outer map has no entry for it
        // at all, exercising the null-tenant-partition branch distinctly from the
        // known-tenant/unknown-entryId case CostLedgerStoreTest already covers.
        assertThat(store.read("tenant-never-seen", "entry-1")).isEmpty();
    }

    @Test
    void publicApiSurface_hasNoMethodThatCanReturnDataWithoutATenantId() {
        // Structural verification of "No code path can construct a cross-tenant read": every
        // public method on CostLedgerStore must accept tenantId as a parameter, and no
        // list-all/iterate-all-style method may exist at all. A future accidental addition of an
        // unscoped accessor breaks this test, guarding the invariant going forward rather than
        // only checking today's two methods by name.
        Method[] publicMethods = CostLedgerStore.class.getMethods();
        Set<String> declaredHere = Arrays.stream(publicMethods)
            .filter(m -> m.getDeclaringClass() == CostLedgerStore.class)
            .map(Method::getName)
            .collect(java.util.stream.Collectors.toSet());

        assertThat(declaredHere).containsExactlyInAnyOrder("write", "read");

        for (Method m : publicMethods) {
            if (m.getDeclaringClass() != CostLedgerStore.class) {
                continue;
            }
            if (m.getName().equals("read")) {
                assertThat(m.getParameterTypes()[0])
                    .as("read(...)'s first parameter must be tenantId")
                    .isEqualTo(String.class);
            }
            if (m.getName().equals("write")) {
                // write(CostLedgerEntry) — tenantId is enforced inside the entry itself
                // (CostLedgerEntry's own compact constructor), verified separately in
                // CostLedgerStoreTest's missing/blank-tenantId rejection tests.
                assertThat(m.getParameterTypes()[0]).isEqualTo(CostLedgerEntry.class);
            }
        }
    }

    private static CostLedgerEntry entryFor(String tenantId, String entryId, long inputTokens) {
        return new CostLedgerEntry(
            entryId,
            "request-for-" + entryId,
            tenantId,
            "org-1",
            null,
            null,
            "task-1",
            Instant.parse("2026-09-23T00:00:00Z"),
            inputTokens, 200L, inputTokens, 200L, 0L, 0L, 0L, 0L, 0L,
            0.05, 0.05, 0.0, 0.0, 0.0, 0.0, 0.0,
            Map.of(),
            "USD",
            "v1",
            "model-x",
            "provider-x",
            true);
    }
}
