package com.eaioc.controlplane.benchmarking;

import com.eaioc.controlplane.accounting.ledger.CostLedgerEntry;
import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;
import com.eaioc.controlplane.core.schemas.LatencyRequirements;
import com.eaioc.controlplane.core.schemas.QualityRequirements;
import com.eaioc.controlplane.core.schemas.RequestType;
import com.eaioc.controlplane.core.schemas.SecurityClassification;
import java.time.Instant;
import java.util.Map;

/**
 * <b>TEST FIXTURE — not a real measurement.</b> Every {@link CostLedgerEntry} this class builds is a
 * clearly-labelled, illustrative stand-in for {@code BaselineRunnerTest}, never a live Path A
 * measurement (execution-plan.md §18.5/§18.14.8, "a clearly labelled test-fixture ledger entry — a
 * {@code verified = true} `CostLedgerEntry` built only in test sources ... in a class and with
 * constants named and commented as a fixture"). This class exists only under
 * {@code src/test/java}; no {@code src/main} code may construct a {@code verified = true} entry.
 */
final class BaselineTestFixtures {

    // FIXTURE VALUES ONLY — arbitrary but plausible; never a measured or measurable quantity.
    static final CostLedgerEntry.Input FIXTURE_INPUT =
        new CostLedgerEntry.Input(1200L, 1150L, 0L, 900L, 300L, 100L, 50L, 0L, 200L, 800L);
    static final CostLedgerEntry.Output FIXTURE_OUTPUT =
        new CostLedgerEntry.Output(220L, 200L, 20L, 0L);
    static final CostLedgerEntry.Cache FIXTURE_CACHE =
        new CostLedgerEntry.Cache(1L, 0L, 1L, 1L, 1L, 400L, 200L);
    static final CostLedgerEntry.Model FIXTURE_MODEL =
        new CostLedgerEntry.Model("model-x", "model-y", "route-default", false, "medium", 0L);
    static final CostLedgerEntry.Tools FIXTURE_TOOLS =
        new CostLedgerEntry.Tools(2L, 1L, 150L, 50L, 0L);
    static final CostLedgerEntry.Workflow FIXTURE_WORKFLOW =
        new CostLedgerEntry.Workflow(3L, 3L, 0L, 0L, 0L);
    static final CostLedgerEntry.Cost FIXTURE_COST =
        new CostLedgerEntry.Cost(0.04, 0.01, 0.002, 0.0, 0.0, 0.052, 0.06, 0.008, 13.3);
    static final CostLedgerEntry.Performance FIXTURE_PERFORMANCE =
        new CostLedgerEntry.Performance(900L, 250L, 700L, 0L, 5L, 40L);
    static final CostLedgerEntry.Quality FIXTURE_QUALITY =
        new CostLedgerEntry.Quality(0.95, 0.9, true, 0.97, 0.92, true);

    /** TEST FIXTURE: builds an illustrative {@link CostLedgerEntry}, {@code verified} as given. */
    static CostLedgerEntry fixtureLedgerEntry(
            String tenantId, String entryId, String requestId, boolean verified) {
        return fixtureLedgerEntryWithCurrency(tenantId, entryId, requestId, verified, "USD");
    }

    /**
     * TEST FIXTURE: as {@link #fixtureLedgerEntry}, but with the given {@code currency}. Passing
     * {@code null} builds an entry that the ledger accepts but that {@code BaselineEvaluationRecord}
     * rejects during mapping — used only to exercise the post-read failure path (HD-CG-P0.2-06, B-R1).
     */
    static CostLedgerEntry fixtureLedgerEntryWithCurrency(
            String tenantId, String entryId, String requestId, boolean verified, String currency) {
        return new CostLedgerEntry(
            entryId,
            requestId,
            tenantId,
            "org-1",
            null,
            null,
            "task-1",
            Instant.parse("2026-09-27T00:00:00Z"),
            1000L, 200L, 1000L, 200L, 0L, 0L, 0L, 0L, 0L,
            0.05, 0.05, 0.0, 0.0, 0.0, 0.0, 0.0,
            Map.of(),
            currency,
            "v1",
            "model-x",
            "provider-x",
            FIXTURE_INPUT, FIXTURE_OUTPUT, FIXTURE_CACHE, FIXTURE_MODEL, FIXTURE_TOOLS,
            FIXTURE_WORKFLOW, FIXTURE_COST, FIXTURE_PERFORMANCE, FIXTURE_QUALITY,
            verified);
    }

    /**
     * TEST FIXTURE: a minimal, valid {@link ControlPlaneRequest} carrying the given identity pair —
     * every other REQUIRED field is populated with an arbitrary but schema-valid value.
     */
    static ControlPlaneRequest fixtureControlPlaneRequest(String tenantId, String requestId) {
        return new ControlPlaneRequest(
            "1.0.0", requestId, "corr-1", tenantId, "org-1",
            "app-1", null, null, null, "task-1", null, 0,
            RequestType.GENERIC_LLM,
            "hello", null, null, null,
            new QualityRequirements(0.9, QualityRequirements.QualityTier.STANDARD, false, true),
            new LatencyRequirements(2000, 500, true),
            null,
            new SecurityClassification(SecurityClassification.Level.INTERNAL, false, null, null),
            null, null,
            false,
            Map.of(), Instant.parse("2026-09-27T00:00:00Z"), null);
    }

    private BaselineTestFixtures() {
    }
}
