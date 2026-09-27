package com.eaioc.controlplane.evaluation;

/**
 * {@code interfaces.md} §18, {@code EvaluationRun.run_type: enum { BASELINE, OPTIMIZED, SHADOW }}
 * — realized here because {@link BaselineEvaluationRecord} carries it (execution-plan.md §18.14.8,
 * "the record carries `run_type`, using the existing `EvaluationRun` semantic"). {@code EvaluationRun}
 * itself is not realized in Java at P0 (no code exists under {@code evaluation/} until this unit,
 * {@code EXE-P0.2.B}); this enum is shared in advance so a future realization of {@code EvaluationRun}
 * reuses it rather than duplicating the three constants.
 */
public enum RunType {
    BASELINE,
    OPTIMIZED,
    SHADOW
}
