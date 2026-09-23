package com.eaioc.controlplane.accounting.ledger;

import static com.eaioc.controlplane.accounting.ledger.LedgerFixtures.*;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * {@code EXE-P0.1.D} (Capability 1 — Token Accounting and Cost Ledger, Sub-phase D —
 * Observability) "Log-output format check" test, per {@code docs/execution-plan.md} §18.4's
 * Sub-phase D row. Captures the structured log line {@link CostLedgerStore#write} emits and checks
 * it cites {@code observability.md} §7's actual metric names — not the row's own two shorthand
 * examples, which this sub-phase's own {@link LedgerObservability} Javadoc explains do not match
 * {@code observability.md}'s real metric namespace.
 */
class LedgerObservabilityTest {

    private ListAppender<ILoggingEvent> appender;
    private Logger observedLogger;

    @BeforeEach
    void attachAppender() {
        observedLogger = (Logger) LoggerFactory.getLogger(LedgerObservability.class);
        appender = new ListAppender<>();
        appender.start();
        observedLogger.addAppender(appender);
    }

    @AfterEach
    void detachAppender() {
        observedLogger.detachAppender(appender);
    }

    @Test
    void write_emitsExactlyOneStructuredInfoLogLine_citingObservabilityMdMetricNames() {
        CostLedgerStore store = new CostLedgerStore();
        CostLedgerEntry entry = new CostLedgerEntry(
            "entry-obs-1",
            "request-obs-1",
            "tenant-obs",
            "org-1",
            null,
            null,
            "task-1",
            Instant.parse("2026-09-23T00:00:00Z"),
            1000L, 200L, 800L, 200L, 0L, 150L, 50L, 0L, 0L,
            0.10, 0.08, 0.0, 0.0, 0.02, 0.02, 20.0,
            Map.of(),
            "USD",
            "v1",
            "model-x",
            "provider-x",
            INPUT, OUTPUT, CACHE, MODEL, TOOLS, WORKFLOW, COST, PERFORMANCE, QUALITY,
            true);

        store.write(entry);

        assertThat(appender.list).hasSize(1);
        ILoggingEvent event = appender.list.get(0);
        String formatted = event.getFormattedMessage();

        assertThat(event.getLevel()).isEqualTo(Level.INFO);
        assertThat(formatted)
            .contains("event_type=LEDGER_ENTRY_WRITTEN")
            .contains("tenant_id=tenant-obs")
            .contains("request_id=request-obs-1")
            .contains("entry_id=entry-obs-1")
            // observability.md §7's actual canonical metric names (interfaces.md §19.2) —
            // not the execution-plan row's own "cost.net_savings"/"cost.baseline_estimated"
            // shorthand, which are architecture.md §27.1 ledger-field names, not metric names.
            .contains("control_plane.tokens.input=800")
            .contains("control_plane.tokens.output=200")
            .contains("control_plane.tokens.avoided=200") // 150 pruning + 50 cache + 0 early-exit
            .contains("control_plane.cost.estimated=0.1")
            .contains("control_plane.cost.saved=0.02")
            .contains("ledger.unverified_estimate.count=0");
    }

    @Test
    void write_unverifiedEntry_reportsZeroSavingsAndIncrementsUnverifiedCount() {
        CostLedgerStore store = new CostLedgerStore();
        CostLedgerEntry unverifiedEntry = new CostLedgerEntry(
            "entry-obs-2",
            "request-obs-2",
            "tenant-obs",
            "org-1",
            null,
            null,
            "task-1",
            Instant.parse("2026-09-23T00:00:00Z"),
            1000L, 200L, 800L, 200L, 0L, 0L, 0L, 0L, 0L,
            0.10, 0.08, 0.0, 0.0, 0.02, 0.02, 20.0,
            Map.of(),
            "USD",
            "v1",
            "model-x",
            "provider-x",
            INPUT, OUTPUT, CACHE, MODEL, TOOLS, WORKFLOW, COST, PERFORMANCE, QUALITY,
            false); // verified = false — root CLAUDE.md rule 5: never fabricate savings

        store.write(unverifiedEntry);

        String formatted = appender.list.get(0).getFormattedMessage();
        assertThat(formatted)
            .contains("control_plane.cost.saved=0.0")
            .contains("ledger.unverified_estimate.count=1");
    }
}
