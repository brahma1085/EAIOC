package com.eaioc.controlplane.accounting.ledger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Emits one structured log line per {@link CostLedgerStore} write.
 *
 * <p><b>Interim sink, {@code SOURCE-GAP-EXECPLAN-01}:</b> no source document states an interim
 * telemetry backend pending {@code ADR-0004}'s eventual acceptance, mirroring the same gap
 * {@code implementation-readiness-gate.md} §9/§10 already found for the Observability P0 item
 * itself (extended here to every capability, per {@code docs/execution-plan.md} §38). Structured
 * SLF4J log lines are this sub-phase's own explicit, already-decided interim sink — chosen by
 * {@code docs/execution-plan.md} §18.4's own {@code EXE-P0.1.D} row ("emits structured log
 * lines"), not re-decided here.
 *
 * <p><b>Metric names cited, not invented:</b> {@code EXE-P0.1.D}'s row names {@code cost.net_savings}
 * / {@code cost.baseline_estimated} as example metric names to cite — those are actually
 * {@code architecture.md} §27.1's internal ledger *field* names (already used, verbatim, in
 * {@code control_plane/accounting/ledger/LedgerEntry.md}), not {@code observability.md}'s own
 * exported *metric* names. Checked directly against {@code observability.md} §7 (Metric Pipeline):
 * the actual canonical metric names for this data are {@code control_plane.cost.estimated} /
 * {@code control_plane.cost.saved} and {@code control_plane.tokens.input} / {@code .output} /
 * {@code .avoided} (`interfaces.md` §19.2's 13 standard metrics), plus
 * {@code ledger.unverified_estimate.count} (`EC-067`'s own Observability field, listed in
 * {@code observability.md} §7 as an existing per-document metric already named upstream). This
 * class cites those real names — the row's own two examples are a citation-path shorthand for the
 * same underlying ledger data, not a literal metric name to reproduce; using them as literal field
 * names in emitted telemetry would not match {@code observability.md}'s actual contract. Recorded
 * here as a discrepancy noticed, not silently corrected in the execution plan itself (file-scope
 * discipline — only {@code control_plane/} changes this sub-phase).
 *
 * <p>Log-entry shape follows {@code interfaces.md} §19.1's {@code ControlPlaneLogEntry} (
 * {@code INTF-031}) exactly — this class adds no new required field, per
 * {@code observability.md} §8's own restatement of that rule. {@code span_id} is generated per
 * write (no distributed-tracing infrastructure exists yet in this P0 slice, so
 * {@code conventions.md} §3.2's "span_id: ... Generated per stage invocation" rule is applied at
 * the narrowest available scope); {@code correlation_id} defaults to {@code request_id} per
 * {@code conventions.md} §3.2's own stated default; {@code parent_span_id} is {@code null} (no
 * parent span exists yet). {@code component_id} uses a locally-chosen identifier,
 * {@code ACCOUNTING-LEDGER} — {@code conventions.md} §3.1's component-ID scheme does not assign
 * Token Accounting a {@code T/OI/CE/...}-prefixed or bare-acronym ID, so none is invented here.
 */
final class LedgerObservability {

    private static final Logger LOG = LoggerFactory.getLogger(LedgerObservability.class);

    private LedgerObservability() {
    }

    /** Emits exactly one structured INFO log line for a successful ledger write. */
    static void logLedgerWrite(CostLedgerEntry entry) {
        long tokensAvoided = entry.tokensAvoidedByPruning()
            + entry.tokensAvoidedByCache()
            + entry.tokensAvoidedByEarlyExit();

        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("control_plane.tokens.input", entry.actualInputTokens());
        fields.put("control_plane.tokens.output", entry.actualOutputTokens());
        fields.put("control_plane.tokens.avoided", tokensAvoided);
        fields.put("control_plane.cost.estimated", entry.baselineCost());
        fields.put("control_plane.cost.saved", entry.verified() ? entry.netSavings() : 0.0);
        fields.put("ledger.unverified_estimate.count", entry.verified() ? 0 : 1);

        // conventions.md §17.2: INFO — "normal decisions" — a ledger write is exactly that, not
        // a warning/error condition.
        LOG.info(
            "event_type=LEDGER_ENTRY_WRITTEN component_id=ACCOUNTING-LEDGER "
                + "tenant_id={} request_id={} entry_id={} correlation_id={} span_id={} "
                + "parent_span_id=null fields={}",
            entry.tenantId(),
            entry.requestId(),
            entry.entryId(),
            entry.requestId(), // conventions.md §3.2: correlation_id defaults to request_id
            UUID.randomUUID(),
            fields);
    }
}
