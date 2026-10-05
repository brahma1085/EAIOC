package com.eaioc.controlplane.benchmarking;

import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;
import com.eaioc.controlplane.evaluation.BaselineEvaluationRecord;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Emits one structured log line per {@link BaselineRunner#runBaseline} call — INFO on a completed
 * run, WARNING on a run that could not establish a baseline.
 *
 * <p>Added by {@code EXE-P0.2.D} ({@code docs/execution-plan.md} §18.5's D row: "Every
 * {@code run_baseline()} call emits one structured log line"). Mirrors
 * {@code accounting.ledger.LedgerObservability} (Capability 1, {@code EXE-P0.1.D}) so both
 * capabilities' structured log lines share one shape.
 *
 * <p><b>Failure-path level and event name (chosen by the EXE-P0.2.D implementer, not stated in any
 * source document):</b> a failed call is logged at WARNING, not ERROR, because {@code conventions.md}
 * §17.2 reserves ERROR for stage failures and a missing baseline is a "no measurement available"
 * outcome rather than a crashed stage. The event name {@code BASELINE_RUN_FAILED} is a local choice;
 * a human reviewer may rename it, and that is the only thing a reviewer would need to change.
 *
 * <p><b>Interim sink, {@code SOURCE-GAP-EXECPLAN-01}:</b> structured SLF4J log lines are the
 * already-decided interim sink for P0 ({@code EXE-P0.1.D} precedent); no telemetry backend is
 * chosen here, pending {@code ADR-0004}.
 *
 * <p><b>Metric names:</b> the only metric emitted is {@code control_plane.cost.estimated}, reused by
 * name from {@code interfaces.md} §19.2 following the same convention {@code LedgerObservability}
 * uses. §19.2 defines that metric as a Counter with labels {@code tenant_id}, {@code model_id},
 * {@code provider_id} and gives it no baseline meaning. This log line carries no {@code model_id} or
 * {@code provider_id}, and the value is a counterfactual baseline estimate, not realized spend. A
 * downstream log-to-metric consumer must not sum this field into the Counter as spend.
 * {@code control_plane.request.latency_ms} is not emitted: it is a request-level histogram labelled
 * with {@code stage_id} and {@code request_type}, and a baseline run carries neither. The baseline
 * latency is carried in the record only.
 * {@code run_type} and {@code source_entry_id} are plain text in the message, not metric fields.
 */
final class BaselineObservability {

    private static final Logger LOG = LoggerFactory.getLogger(BaselineObservability.class);

    private BaselineObservability() {
    }

    /** Emits exactly one structured INFO log line for a completed baseline run. */
    static void logBaselineRun(BaselineEvaluationRecord record) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("control_plane.cost.estimated", record.baselineCost());

        // conventions.md §17.2: INFO — normal decisions. A completed baseline run is a normal event.
        // correlation_id defaults to request_id (conventions.md §3.2); span_id is generated per call,
        // the same narrowest-available-scope choice LedgerObservability makes.
        LOG.info(
            "event_type=BASELINE_RUN_COMPLETED component_id=EVALUATION-BASELINE "
                + "tenant_id={} request_id={} run_id={} correlation_id={} span_id={} "
                + "parent_span_id=null run_type={} source_entry_id={} fields={}",
            record.tenantId(),
            record.requestId(),
            record.runId(),
            record.requestId(),
            UUID.randomUUID(),
            record.runType(),
            record.sourceEntryId(),
            fields);
    }

    /**
     * Emits exactly one structured WARNING log line for a baseline run that could not establish a
     * valid measurement. No metric field is emitted: there is no measured value to report.
     */
    static void logBaselineFailure(ControlPlaneRequest request, String reason) {
        // conventions.md §17.2: WARNING — a fallback outcome (no baseline available to the caller).
        LOG.warn(
            "event_type=BASELINE_RUN_FAILED component_id=EVALUATION-BASELINE "
                + "tenant_id={} request_id={} correlation_id={} span_id={} "
                + "parent_span_id=null reason={} fields={}",
            request.tenantId(),
            request.requestId(),
            request.requestId(),
            UUID.randomUUID(),
            reason,
            "{}");
    }
}
