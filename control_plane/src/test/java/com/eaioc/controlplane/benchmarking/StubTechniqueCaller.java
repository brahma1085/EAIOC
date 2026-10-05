package com.eaioc.controlplane.benchmarking;

import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;
import com.eaioc.controlplane.evaluation.BaselineEvaluationRecord;

/**
 * <b>TEST-ONLY — not a live Path A caller.</b> Stand-in for a future P1+ technique's
 * validation-matrix gate — none of which exist yet in this execution slice
 * ({@code docs/execution-plan.md} §18.5's {@code EXE-P0.2.C} row: "Every future P1+ technique's
 * validation-matrix gate will call into this harness — the shared substrate is built once").
 *
 * <p>Deliberately kept as a test-support class, not a production {@code src/main/java} component,
 * mirroring {@code EXE-P0.1.C}'s {@code StubPipelineStageCaller}: inventing a real P1+ technique
 * class now, before any P1 technique actually exists, would be scope creep beyond this sub-phase's
 * own Files/Modules field ({@code control_plane/benchmarking/} public API). Its only job is to
 * prove — via constructor injection, wired through a real Spring {@code ApplicationContext} in
 * {@link BaselineRunnerIntegrationTest} — that {@link BaselineRunner}'s public
 * {@code runBaseline()} API is callable by an externally-wired caller, satisfying this sub-phase's
 * own Integration Test requirement ("Integration test with a stub caller") and its Definition of
 * Done ("Public API stable for future technique gates to call").
 *
 * <p>Calling this class successfully is evidence only that the public API is Spring-wireable and
 * callable end-to-end by an external consumer. It is never evidence of a live Path A measurement —
 * that remains explicitly deferred to Capability Gate P0.2 ({@code docs/execution-plan.md} §18.10,
 * §18.5's B row) — and whatever {@link BaselineEvaluationRecord} this returns in a test is built
 * only from the clearly-labelled {@link BaselineTestFixtures} fixture data, never a measurement.
 */
class StubTechniqueCaller {

    private final BaselineRunner baselineRunner;

    StubTechniqueCaller(BaselineRunner baselineRunner) {
        this.baselineRunner = baselineRunner;
    }

    /** Calls {@code run_baseline()} through the public API exactly as a future validation-matrix gate would. */
    BaselineEvaluationRecord requestBaseline(ControlPlaneRequest request) {
        return baselineRunner.runBaseline(request);
    }
}
