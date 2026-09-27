package com.eaioc.controlplane.benchmarking;

import static com.eaioc.controlplane.benchmarking.BaselineTestFixtures.fixtureControlPlaneRequest;
import static com.eaioc.controlplane.benchmarking.BaselineTestFixtures.fixtureLedgerEntry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.eaioc.controlplane.accounting.ledger.CostLedgerStore;
import com.eaioc.controlplane.accounting.ledger.LedgerCostReporter;
import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;
import com.eaioc.controlplane.evaluation.BaselineEvaluationRecord;
import com.eaioc.controlplane.evaluation.RunType;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@code EXE-P0.2.B} (Capability 2 — Baseline Benchmark Harness + Quality Evaluation,
 * Sub-phase B — Minimal Implementation), per {@code docs/execution-plan.md} §18.5's B row: given a
 * labelled test-fixture {@code verified = true} ledger entry retrieved through the
 * {@code REM-P0.2.B-02} contract, {@code run_baseline()} produces a complete, correctly-typed
 * {@code BaselineEvaluationRecord}; its Failure-Path Test: no entry, several entries (count-all-then-
 * verify), an unverified entry, and a cross-tenant entry each make {@code run_baseline()} fail without
 * returning a record — never a partial, malformed, zero, placeholder or sentinel record. Uses only the
 * clearly-labelled {@link BaselineTestFixtures}; no {@code src/main} code constructs a
 * {@code verified = true} entry, and this test never claims a live Path A measurement.
 */
class BaselineRunnerTest {

    @Test
    void runBaseline_singleVerifiedFixtureEntry_producesCompleteRecord() {
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        BaselineRunner runner = new BaselineRunner(reporter);
        store.write(fixtureLedgerEntry("tenant-a", "entry-1", "request-1", true));
        ControlPlaneRequest request = fixtureControlPlaneRequest("tenant-a", "request-1");

        BaselineEvaluationRecord record = runner.runBaseline(request);

        assertThat(record.requestId()).isEqualTo("request-1");
        assertThat(record.tenantId()).isEqualTo("tenant-a");
        assertThat(record.runType()).isEqualTo(RunType.BASELINE);
        assertThat(record.schemaVersion()).isEqualTo("1.0.0");
        assertThat(record.currency()).isEqualTo("USD");
        assertThat(record.sourceEntryId()).isEqualTo("entry-1");
        assertThat(record.baselineCost()).isEqualTo(0.05);
        assertThat(record.latencyMsBaseline()).isEqualTo(900L);
        assertThat(record.runId()).isNotBlank();
        assertThat(record.timestamp()).isNotNull();
    }

    @Test
    void runBaseline_noMatchingEntry_throwsWithoutRecord() {
        CostLedgerStore store = new CostLedgerStore();
        BaselineRunner runner = new BaselineRunner(new LedgerCostReporter(store));
        ControlPlaneRequest request = fixtureControlPlaneRequest("tenant-a", "request-1");

        assertThatThrownBy(() -> runner.runBaseline(request))
            .isInstanceOf(NoBaselineMeasurementException.class);
    }

    @Test
    void runBaseline_severalMatchingEntries_verifiedPlusUnverified_throwsWithoutRecord() {
        // Count-all-then-verify: a verified entry plus an unverified one is "several", not "one".
        CostLedgerStore store = new CostLedgerStore();
        BaselineRunner runner = new BaselineRunner(new LedgerCostReporter(store));
        store.write(fixtureLedgerEntry("tenant-a", "entry-1", "request-1", true));
        store.write(fixtureLedgerEntry("tenant-a", "entry-2", "request-1", false));
        ControlPlaneRequest request = fixtureControlPlaneRequest("tenant-a", "request-1");

        assertThatThrownBy(() -> runner.runBaseline(request))
            .isInstanceOf(NoBaselineMeasurementException.class);
    }

    @Test
    void runBaseline_singleUnverifiedEntry_throwsWithoutRecord() {
        CostLedgerStore store = new CostLedgerStore();
        BaselineRunner runner = new BaselineRunner(new LedgerCostReporter(store));
        store.write(fixtureLedgerEntry("tenant-a", "entry-1", "request-1", false));
        ControlPlaneRequest request = fixtureControlPlaneRequest("tenant-a", "request-1");

        assertThatThrownBy(() -> runner.runBaseline(request))
            .isInstanceOf(NoBaselineMeasurementException.class);
    }

    @Test
    void runBaseline_entryUnderAnotherTenant_throwsWithoutRecord_crossTenantNeverConsumed() {
        CostLedgerStore store = new CostLedgerStore();
        BaselineRunner runner = new BaselineRunner(new LedgerCostReporter(store));
        // Entry exists, verified=true, but under a different tenant than the request names.
        store.write(fixtureLedgerEntry("tenant-b", "entry-1", "request-1", true));
        ControlPlaneRequest request = fixtureControlPlaneRequest("tenant-a", "request-1");

        assertThatThrownBy(() -> runner.runBaseline(request))
            .isInstanceOf(NoBaselineMeasurementException.class);
    }
}
