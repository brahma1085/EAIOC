package com.eaioc.controlplane.accounting.ledger;

import static com.eaioc.controlplane.accounting.ledger.LedgerFixtures.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@code EXE-P0.1.B} (Capability 1 — Token Accounting and Cost Ledger, Sub-phase B —
 * Minimal Implementation), per {@code docs/execution-plan.md} §18.4's Sub-phase B row: "Unit
 * tests: write, read-back, tenant-scoping enforcement" and its Failure-Path Test: "write with
 * missing tenant_id is rejected, not silently defaulted."
 */
class CostLedgerStoreTest {

    @Test
    void writeThenReadBack_roundTripsCorrectly() {
        CostLedgerStore store = new CostLedgerStore();
        CostLedgerEntry entry = validEntry("tenant-a", "entry-1", "request-1");

        store.write(entry);
        Optional<CostLedgerEntry> readBack = store.read("tenant-a", "entry-1");

        assertThat(readBack).contains(entry);
    }

    @Test
    void read_missingEntry_returnsEmpty() {
        CostLedgerStore store = new CostLedgerStore();

        assertThat(store.read("tenant-a", "does-not-exist")).isEmpty();
    }

    @Test
    void read_wrongTenant_returnsEmpty_tenantScopingEnforced() {
        CostLedgerStore store = new CostLedgerStore();
        store.write(validEntry("tenant-a", "entry-1", "request-1"));

        // tenant-b asking for tenant-a's entryId must not see it — no cross-tenant read is
        // structurally possible (docs/execution-plan.md §18.4, Sub-phase E rationale).
        assertThat(store.read("tenant-b", "entry-1")).isEmpty();
    }

    @Test
    void write_duplicateEntryId_sameTenant_isRejected_appendOnly() {
        CostLedgerStore store = new CostLedgerStore();
        store.write(validEntry("tenant-a", "entry-1", "request-1"));

        assertThatThrownBy(() -> store.write(validEntry("tenant-a", "entry-1", "request-2")))
            .isInstanceOf(DuplicateLedgerEntryException.class);
    }

    @Test
    void constructEntry_nullTenantId_isRejected_notSilentlyDefaulted() {
        assertThatThrownBy(() -> entryBuilderWithTenant(null))
            .isInstanceOf(MissingTenantIdException.class);
    }

    @Test
    void constructEntry_blankTenantId_isRejected_notSilentlyDefaulted() {
        assertThatThrownBy(() -> entryBuilderWithTenant("   "))
            .isInstanceOf(MissingTenantIdException.class);
    }

    @Test
    void savingsByStage_isDefensivelyCopied_notMutableAfterConstruction() {
        Map<String, Double> mutableInput = new HashMap<>();
        mutableInput.put("T1.9-CONTEXT-PRUNER", 12.5);

        CostLedgerEntry entry = entryBuilderWithSavingsByStage(mutableInput);
        mutableInput.put("T1.10-CONTEXT-DEDUPLICATOR", 4.0);

        assertThat(entry.savingsByStage()).containsOnlyKeys("T1.9-CONTEXT-PRUNER");
    }

    private static CostLedgerEntry validEntry(String tenantId, String entryId, String requestId) {
        return new CostLedgerEntry(
            entryId,
            requestId,
            tenantId,
            "org-1",
            null,
            null,
            "task-1",
            Instant.parse("2026-09-23T00:00:00Z"),
            1000L, 200L, 1000L, 200L, 0L, 0L, 0L, 0L, 0L,
            0.05, 0.05, 0.0, 0.0, 0.0, 0.0, 0.0,
            Map.of(),
            "USD",
            "v1",
            "model-x",
            "provider-x",
            INPUT, OUTPUT, CACHE, MODEL, TOOLS, WORKFLOW, COST, PERFORMANCE, QUALITY,
            true);
    }

    private static CostLedgerEntry entryBuilderWithTenant(String tenantId) {
        return validEntry(tenantId, "entry-x", "request-x");
    }

    private static CostLedgerEntry entryBuilderWithSavingsByStage(Map<String, Double> savingsByStage) {
        return new CostLedgerEntry(
            "entry-y",
            "request-y",
            "tenant-a",
            "org-1",
            null,
            null,
            "task-1",
            Instant.parse("2026-09-23T00:00:00Z"),
            1000L, 200L, 900L, 200L, 0L, 100L, 0L, 0L, 0L,
            0.05, 0.045, 0.0, 0.0, 0.005, 0.005, 10.0,
            savingsByStage,
            "USD",
            "v1",
            "model-x",
            "provider-x",
            INPUT, OUTPUT, CACHE, MODEL, TOOLS, WORKFLOW, COST, PERFORMANCE, QUALITY,
            true);
    }
}
