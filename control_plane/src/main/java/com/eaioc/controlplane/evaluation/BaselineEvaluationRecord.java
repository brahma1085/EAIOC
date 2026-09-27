package com.eaioc.controlplane.evaluation;

import java.time.Instant;
import java.util.Objects;

/**
 * {@code interfaces.md} §18, {@code BaselineEvaluationRecord} — added by execution-plan.md v1.0.11 /
 * DB-3 (operator decision D-B): the baseline-only result of {@code run_baseline()}. Carries identity
 * and baseline-side fields only; it is not an {@code EvaluationRun} (§18 P0 realization notes).
 *
 * <p>Realized here, evaluation-owned, per D-C/HQ-5 ("the baseline interface and the schemas
 * {@code BaselineEvaluationRecord}, {@code EvaluationRun} and {@code EvaluationComparison} are
 * evaluation-owned (`control_plane/evaluation/`)"); the runner that actually produces one is in
 * {@code control_plane.benchmarking} ({@link com.eaioc.controlplane.benchmarking.BaselineRunner}).
 *
 * <p><b>Field provenance</b> (execution-plan.md §18.14.8, "Provenance of the `BaselineEvaluationRecord`
 * fields"): {@code runId}, {@code requestId}, {@code runType}, {@code timestamp}, {@code baselineCost},
 * {@code latencyMsBaseline} come from {@code EvaluationRun} (§18); {@code tenantId} from
 * {@code conventions.md} §5.1; {@code schemaVersion} from {@code conventions.md} §4.3/§5.1;
 * {@code currency} from {@code interfaces.md} §28.1/`conventions.md` §3.6. {@code schemaVersion}'s
 * initial value {@code "1.0.0"} and the field name/type {@code sourceEntryId: string} are human
 * contract decisions of 2026-09-25, not source-derived (§18.14.8).
 *
 * <p>Immutable by construction (Java {@code record}), matching the tenant-scoping-at-construction
 * pattern already established by {@code CostLedgerEntry} and {@code ControlPlaneRequest}.
 */
public record BaselineEvaluationRecord(
    String runId,
    String requestId,
    String tenantId,
    RunType runType,
    Instant timestamp,
    String schemaVersion,
    String currency,
    String sourceEntryId,
    double baselineCost,
    long latencyMsBaseline
) {

    /**
     * Compact constructor: structural tenant-scoping enforcement and non-null enforcement for every
     * other identity/provenance field. No field here is nullable — INTF-030's schema declares none
     * of {@code BaselineEvaluationRecord}'s members {@code | null}.
     */
    public BaselineEvaluationRecord {
        if (tenantId == null || tenantId.isBlank()) {
            throw new MissingTenantIdException(
                "BaselineEvaluationRecord requires a non-blank tenantId (root CLAUDE.md rule 4); "
                    + "runId=" + runId + ", requestId=" + requestId);
        }
        Objects.requireNonNull(runId, "runId must not be null");
        Objects.requireNonNull(requestId, "requestId must not be null");
        Objects.requireNonNull(runType, "runType must not be null");
        Objects.requireNonNull(timestamp, "timestamp must not be null");
        Objects.requireNonNull(schemaVersion, "schemaVersion must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        Objects.requireNonNull(sourceEntryId, "sourceEntryId must not be null");
    }
}
