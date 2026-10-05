package com.eaioc.controlplane.benchmarking;

import static com.eaioc.controlplane.benchmarking.BaselineTestFixtures.fixtureControlPlaneRequest;
import static com.eaioc.controlplane.benchmarking.BaselineTestFixtures.fixtureLedgerEntry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.eaioc.controlplane.accounting.ledger.CostLedgerEntry;
import com.eaioc.controlplane.accounting.ledger.CostLedgerStore;
import com.eaioc.controlplane.accounting.ledger.LedgerCostReporter;
import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;
import com.eaioc.controlplane.evaluation.BaselineEvaluationRecord;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * {@code EXE-P0.2.D} (Capability 2 — Baseline Benchmark Harness + Quality Evaluation, Sub-phase D —
 * Observability) log-format check, per {@code docs/execution-plan.md} §18.5's D row ("Every
 * {@code run_baseline()} call emits one structured log line"). Contract-level and fixture-based —
 * not a live Path A measurement.
 */
class BaselineObservabilityTest {

    private ListAppender<ILoggingEvent> appender;
    private Logger observedLogger;

    @BeforeEach
    void attachAppender() {
        observedLogger = (Logger) LoggerFactory.getLogger(BaselineObservability.class);
        appender = new ListAppender<>();
        appender.start();
        observedLogger.addAppender(appender);
    }

    @AfterEach
    void detachAppender() {
        observedLogger.detachAppender(appender);
    }

    @Test
    void runBaseline_success_emitsOneInfoLinePerCall_withMatchingIdentity() {
        CostLedgerStore store = new CostLedgerStore();
        store.write(fixtureLedgerEntry("tenant-obs-d", "entry-obs-d-1", "request-obs-d-1", true));
        BaselineRunner runner = new BaselineRunner(new LedgerCostReporter(store));
        ControlPlaneRequest request = fixtureControlPlaneRequest("tenant-obs-d", "request-obs-d-1");

        BaselineEvaluationRecord first = runner.runBaseline(request);
        BaselineEvaluationRecord second = runner.runBaseline(request);

        assertThat(appender.list).hasSize(2);
        ILoggingEvent event = appender.list.get(0);
        String formatted = event.getFormattedMessage();

        assertThat(event.getLevel()).isEqualTo(Level.INFO);
        assertThat(formatted)
            .contains("event_type=BASELINE_RUN_COMPLETED")
            .contains("component_id=EVALUATION-BASELINE")
            .contains("tenant_id=tenant-obs-d")
            .contains("request_id=request-obs-d-1")
            .contains("correlation_id=request-obs-d-1")
            .contains("run_id=" + first.runId())
            .contains("run_type=BASELINE")
            .contains("source_entry_id=entry-obs-d-1")
            .contains("control_plane.cost.estimated=0.05");

        // Each call gets its own span_id; the two calls are distinguishable.
        assertThat(spanIdOf(appender.list.get(0))).isNotEqualTo(spanIdOf(appender.list.get(1)));
        assertThat(appender.list.get(1).getFormattedMessage()).contains("run_id=" + second.runId());
    }

    @Test
    void runBaseline_noMeasurement_emitsExactlyOneWarningLine_andThrows() {
        CostLedgerStore store = new CostLedgerStore();
        BaselineRunner runner = new BaselineRunner(new LedgerCostReporter(store));
        ControlPlaneRequest request = fixtureControlPlaneRequest("tenant-obs-d", "request-missing");

        assertThatThrownBy(() -> runner.runBaseline(request))
            .isInstanceOf(NoBaselineMeasurementException.class);

        assertThat(appender.list).hasSize(1);
        ILoggingEvent event = appender.list.get(0);
        assertThat(event.getLevel()).isEqualTo(Level.WARN);
        assertThat(event.getFormattedMessage())
            .contains("event_type=BASELINE_RUN_FAILED")
            .contains("tenant_id=tenant-obs-d")
            .contains("request_id=request-missing")
            .doesNotContain("control_plane.cost.estimated");
    }

    @Test
    void runBaseline_ledgerReadFailure_emitsOneWarningLine_andRethrows() {
        IllegalStateException simulatedFailure = new IllegalStateException("simulated ledger read failure");
        LedgerCostReporter failingReporter = new LedgerCostReporter(new CostLedgerStore()) {
            @Override
            public java.util.Optional<com.eaioc.controlplane.accounting.ledger.CostLedgerEntry> getRequestCost(
                    String requestId, String tenantId) {
                throw simulatedFailure;
            }
        };
        BaselineRunner runner = new BaselineRunner(failingReporter);
        ControlPlaneRequest request = fixtureControlPlaneRequest("tenant-obs-d", "request-read-fail");

        // HD-CG-P0.2-06 (B-R1): the original exception must come back by identity, not merely by type.
        assertThatThrownBy(() -> runner.runBaseline(request))
            .isSameAs(simulatedFailure);

        assertThat(appender.list).hasSize(1);
        ILoggingEvent event = appender.list.get(0);
        assertThat(event.getLevel()).isEqualTo(Level.WARN);
        assertThat(event.getFormattedMessage())
            .contains("event_type=BASELINE_RUN_FAILED")
            .contains("reason=ledger read failure")
            .contains("request_id=request-read-fail");
    }

    @Test
    void runBaseline_postReadMappingFailure_nullCurrency_emitsOneWarningLine_andRethrows() {
        // HD-CG-P0.2-06 (B-R1): the ledger accepts an entry with null currency, but mapping it into
        // BaselineEvaluationRecord fails after the read. That failure must also emit the one line.
        // NOTE: this entry is contract-INVALID — HD-4 (LedgerContractReconciliation.md §4) does not make
        // currency nullable, so a verified=true entry with null currency should not exist. The ledger does
        // not yet reject it (a Capability 1 contract question, open for a human decision). This test
        // exercises only the defensive mapping path; it does not endorse the state.
        CostLedgerEntry nullCurrencyEntry = BaselineTestFixtures.fixtureLedgerEntryWithCurrency(
            "tenant-obs-d", "entry-null-currency", "request-null-currency", true, null);
        LedgerCostReporter reportingNullCurrency = new LedgerCostReporter(new CostLedgerStore()) {
            @Override
            public Optional<CostLedgerEntry> getRequestCost(String requestId, String tenantId) {
                return Optional.of(nullCurrencyEntry);
            }
        };
        BaselineRunner runner = new BaselineRunner(reportingNullCurrency);
        ControlPlaneRequest request =
            fixtureControlPlaneRequest("tenant-obs-d", "request-null-currency");

        Throwable thrown = catchThrowable(() -> runner.runBaseline(request));

        assertThat(thrown).isInstanceOf(NullPointerException.class)
            .hasMessageContaining("currency must not be null");
        assertThat(appender.list).hasSize(1);
        ILoggingEvent event = appender.list.get(0);
        assertThat(event.getLevel()).isEqualTo(Level.WARN);
        assertThat(event.getFormattedMessage())
            .contains("event_type=BASELINE_RUN_FAILED")
            .contains("reason=ledger entry mapping failure")
            .contains("request_id=request-null-currency")
            .contains("tenant_id=tenant-obs-d");
    }

    /** Extracts the {@code span_id=} value from a formatted log line. */
    private static String spanIdOf(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        int start = message.indexOf("span_id=") + "span_id=".length();
        return message.substring(start, message.indexOf(' ', start));
    }
}
