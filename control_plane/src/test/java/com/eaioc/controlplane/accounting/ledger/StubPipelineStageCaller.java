package com.eaioc.controlplane.accounting.ledger;

import static com.eaioc.controlplane.accounting.ledger.LedgerFixtures.*;

import java.time.Instant;
import java.util.Map;

/**
 * Stand-in for a future T0–T3 pipeline stage or optimization technique — none of which exist yet
 * in this execution slice ({@code docs/execution-plan.md} §18.4's {@code EXE-P0.1.C} row: "no
 * other in-scope capability's Sub-phase C depends on this yet, since none is a technique that
 * spends tokens").
 *
 * <p>Deliberately kept as a test-support class, not a production {@code src/main/java} component:
 * inventing a real pipeline-stage production class now, before any of Capabilities 2–6 actually
 * exist, would be scope creep beyond {@code EXE-P0.1.C}'s own Files/Modules field (
 * {@code control_plane/accounting/} public write API). Its only job is to prove — via constructor
 * injection, wired through a real Spring {@code ApplicationContext} in
 * {@link CostLedgerIntegrationTest} — that {@link CostLedgerStore}'s public write API is callable
 * by an externally-wired caller, satisfying this sub-phase's own Integration Test requirement ("a
 * second in-scope capability (once built) can call the write API... at least a stub caller").
 */
class StubPipelineStageCaller {

    private final CostLedgerStore ledgerStore;

    StubPipelineStageCaller(CostLedgerStore ledgerStore) {
        this.ledgerStore = ledgerStore;
    }

    /** Records a minimal, valid cost entry through the public write API — nothing more. */
    void recordStubRequestCost(String tenantId, String entryId, String requestId) {
        ledgerStore.write(new CostLedgerEntry(
            entryId,
            requestId,
            tenantId,
            "org-stub",
            null,
            null,
            "task-stub",
            Instant.parse("2026-09-23T00:00:00Z"),
            10L, 5L, 10L, 5L, 0L, 0L, 0L, 0L, 0L,
            0.001, 0.001, 0.0, 0.0, 0.0, 0.0, 0.0,
            Map.of(),
            "USD",
            "v1",
            "model-stub",
            "provider-stub",
            INPUT, OUTPUT, CACHE, MODEL, TOOLS, WORKFLOW, COST, PERFORMANCE, QUALITY,
            true));
    }
}
