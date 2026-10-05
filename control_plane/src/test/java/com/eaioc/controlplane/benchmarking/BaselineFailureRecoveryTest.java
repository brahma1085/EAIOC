package com.eaioc.controlplane.benchmarking;

import static com.eaioc.controlplane.benchmarking.BaselineTestFixtures.fixtureControlPlaneRequest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.eaioc.controlplane.accounting.ledger.CostLedgerEntry;
import com.eaioc.controlplane.accounting.ledger.CostLedgerStore;
import com.eaioc.controlplane.accounting.ledger.LedgerCostReporter;
import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;
import com.eaioc.controlplane.evaluation.BaselineEvaluationRecord;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * {@code EXE-P0.2.H} (Capability 2 — Baseline Benchmark Harness + Quality Evaluation, Sub-phase H —
 * Failure/Recovery) failure-injection tests, per {@code docs/execution-plan.md} §18.5's H row: "a
 * harness failure must never silently mark an unvalidated technique as validated". Contract-level
 * and fixture-based — not a live Path A measurement.
 *
 * <p>Each test forces {@link BaselineRunner#runBaseline} into a failing state and asserts it never
 * returns a {@link BaselineEvaluationRecord}. A returned record is the only thing a downstream gate
 * could read as "a baseline exists", so a failure that still returned one would be a false positive.
 */
class BaselineFailureRecoveryTest {

    @Test
    void underlyingReadFailure_propagates_andNeverReturnsARecord() {
        LedgerCostReporter failingReporter = new LedgerCostReporter(new CostLedgerStore()) {
            @Override
            public Optional<CostLedgerEntry> getRequestCost(String requestId, String tenantId) {
                throw new IllegalStateException("simulated ledger read failure");
            }
        };
        BaselineRunner runner = new BaselineRunner(failingReporter);
        ControlPlaneRequest request = fixtureControlPlaneRequest("tenant-h", "request-h-1");

        assertThatThrownBy(() -> runner.runBaseline(request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("simulated ledger read failure");
    }

    @Test
    void noValidMeasurement_failsClosed_withNoRecord() {
        BaselineRunner runner = new BaselineRunner(new LedgerCostReporter(new CostLedgerStore()));
        ControlPlaneRequest request = fixtureControlPlaneRequest("tenant-h", "request-h-2");

        assertThatThrownBy(() -> runner.runBaseline(request))
            .isInstanceOf(NoBaselineMeasurementException.class);
    }
}
