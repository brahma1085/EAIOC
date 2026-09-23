package com.eaioc.controlplane.accounting.ledger;

/**
 * Shared, fully-populated canonical §27.1 groups for tests that construct a {@link CostLedgerEntry}
 * but are not themselves about the canonical groups ({@code REM-P0.1.B-02}; design note
 * {@code LedgerContractReconciliation.md} §7). Records are immutable, so sharing these constants is
 * safe. Values are arbitrary but plausible measured values; the "where available" members are
 * populated here (not {@code null}) — tests about nullability build their own groups.
 */
final class LedgerFixtures {

    static final CostLedgerEntry.Input INPUT =
        new CostLedgerEntry.Input(1200L, 1150L, 0L, 900L, 300L, 100L, 50L, 0L, 200L, 800L);
    static final CostLedgerEntry.Output OUTPUT =
        new CostLedgerEntry.Output(220L, 200L, 20L, 0L);
    static final CostLedgerEntry.Cache CACHE =
        new CostLedgerEntry.Cache(1L, 0L, 1L, 1L, 1L, 400L, 200L);
    static final CostLedgerEntry.Model MODEL =
        new CostLedgerEntry.Model("model-x", "model-y", "route-default", false, "medium", 0L);
    static final CostLedgerEntry.Tools TOOLS =
        new CostLedgerEntry.Tools(2L, 1L, 150L, 50L, 0L);
    static final CostLedgerEntry.Workflow WORKFLOW =
        new CostLedgerEntry.Workflow(3L, 3L, 0L, 0L, 0L);
    static final CostLedgerEntry.Cost COST =
        new CostLedgerEntry.Cost(0.04, 0.01, 0.002, 0.0, 0.0, 0.052, 0.06, 0.008, 13.3);
    static final CostLedgerEntry.Performance PERFORMANCE =
        new CostLedgerEntry.Performance(900L, 250L, 700L, 0L, 5L, 40L);
    static final CostLedgerEntry.Quality QUALITY =
        new CostLedgerEntry.Quality(0.95, 0.9, true, 0.97, 0.92, true);

    private LedgerFixtures() {
    }
}
