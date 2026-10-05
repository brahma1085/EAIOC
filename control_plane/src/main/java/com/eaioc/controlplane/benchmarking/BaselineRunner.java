package com.eaioc.controlplane.benchmarking;

import com.eaioc.controlplane.accounting.ledger.CostLedgerEntry;
import com.eaioc.controlplane.accounting.ledger.LedgerCostReporter;
import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;
import com.eaioc.controlplane.evaluation.BaselineEvaluationRecord;
import com.eaioc.controlplane.evaluation.RunType;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Realizes {@code run_baseline(request: ControlPlaneRequest) -> BaselineEvaluationRecord}, the one
 * {@code EvaluationFramework} (INTF-030, {@code interfaces.md} §18) method this unit implements —
 * {@code EXE-P0.2.B}, {@code execution-plan.md} §18.5/§18.14.8.
 *
 * <p><b>Only {@code run_baseline} is realized.</b> {@code run_optimized}, {@code compare} and
 * {@code report_regression} are deferred INTF-030 forward contract at P0 (HQ-3, §18.14.6/§18.14.8) —
 * neither declared nor implemented, no stub, no exception standing in for them. As with
 * {@link com.eaioc.controlplane.accounting.ledger.LedgerCostReporter}'s realization of one quarter of
 * {@code CostReporter}, this class is deliberately a concrete runner rather than a Java
 * {@code EvaluationFramework} interface type: declaring an interface for one-quarter of a contract
 * whose other three members have no declared Java signature would misrepresent how much of INTF-030
 * exists.
 *
 * <p><b>Package placement (D-C, HQ-5).</b> The baseline schema, {@link BaselineEvaluationRecord}, is
 * evaluation-owned (`control_plane/evaluation/`); this runner — the class that actually executes
 * {@code run_baseline()} — is in `control_plane/benchmarking/`, per {@code interfaces.md} §18's P0
 * realization notes ("Realization and placement"). It depends on
 * {@link com.eaioc.controlplane.accounting.ledger.LedgerCostReporter} (`REM-P0.2.B-02`'s retrieval
 * realization, `accounting/`) and on the shared {@link ControlPlaneRequest} from {@code core/schemas/}
 * — never a harness-local stand-in type (this unit's own Deliverable text).
 *
 * <p><b>Contract-level, fixture-based — not a live Path A measurement.</b> No P0 unit produces a live
 * {@code verified = true} Path A ledger entry (execution-plan.md §18, "Producer and Observability
 * (D-A, D-D)" — that is `REM-P0.2.B-03`'s separately-registered, not-yet-run scope). This class only
 * retrieves and maps whatever entry {@link LedgerCostReporter} returns; test sources supply a clearly
 * labelled test-fixture entry (never `src/main`). Nothing here claims, and nothing here must ever be
 * read as claiming, that a live Path A end-to-end baseline measurement has been demonstrated
 * (explicitly deferred to Capability Gate P0.2, §18.10).
 *
 * <p><b>Failure semantics (human contract decision of 2026-09-25).</b> When
 * {@link LedgerCostReporter#getRequestCost} returns {@link Optional#empty()} — zero matching entries,
 * several matching entries (count-all-then-verify), or a single matching entry that is not
 * {@code verified = true} — {@link #runBaseline} fails without returning a
 * {@link BaselineEvaluationRecord}: it throws {@link NoBaselineMeasurementException}, a unit-local
 * mechanism that changes no public contract (see that class's own Javadoc). The declared return type
 * of {@link #runBaseline} stays non-null; there is no nullable-record, wrapper, or new
 * {@code ControlPlaneError} alternative.
 */
@Component
public class BaselineRunner {

    /** Human contract decision of 2026-09-25 (execution-plan.md §18.14.8) — not source-derived. */
    private static final String SCHEMA_VERSION = "1.0.0";

    private final LedgerCostReporter ledgerCostReporter;

    public BaselineRunner(LedgerCostReporter ledgerCostReporter) {
        this.ledgerCostReporter = ledgerCostReporter;
    }

    /**
     * Retrieves the single {@code verified = true} ledger entry for {@code request}'s
     * {@code (tenantId, requestId)} and maps it into a {@link BaselineEvaluationRecord}.
     *
     * <p>{@code runId} and {@code timestamp} belong to this execution of {@code run_baseline()}
     * (derived consequence, {@code interfaces.md} §18 P0 realization notes) — generated fresh here,
     * never read from the ledger entry. Every other field is the declared mapping: {@code requestId}
     * and {@code tenantId} from {@code request} (the same pair the entry was retrieved by);
     * {@code currency} and {@code sourceEntryId} (the entry's own {@code entryId}) from the entry
     * itself; {@code baselineCost} from {@code CostLedgerEntry.baseline_cost}; {@code latencyMsBaseline}
     * from {@code CostLedgerEntry.performance.e2e_latency_ms} (both operator-declared mappings, §18.14.8).
     *
     * @throws NoBaselineMeasurementException if no valid baseline measurement can be identified
     */
    public BaselineEvaluationRecord runBaseline(ControlPlaneRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        Optional<CostLedgerEntry> measurement =
            ledgerCostReporter.getRequestCost(request.requestId(), request.tenantId());
        if (measurement.isEmpty()) {
            // EXE-P0.2.D: every run_baseline() call logs one line, including this failure path.
            String reason = "no valid baseline measurement (zero, several, or unverified matching ledger entries)";
            BaselineObservability.logBaselineFailure(request, reason);
            throw new NoBaselineMeasurementException(
                "No " + reason + " for requestId=" + request.requestId()
                    + ", tenantId=" + request.tenantId());
        }
        CostLedgerEntry entry = measurement.get();

        BaselineEvaluationRecord record = new BaselineEvaluationRecord(
            UUID.randomUUID().toString(),
            request.requestId(),
            request.tenantId(),
            RunType.BASELINE,
            Instant.now(),
            SCHEMA_VERSION,
            entry.currency(),
            entry.entryId(),
            entry.baselineCost(),
            entry.performance().e2eLatencyMs());

        // EXE-P0.2.D: one structured log line per successful run (see BaselineObservability).
        BaselineObservability.logBaselineRun(record);
        return record;
    }
}
