package com.eaioc.controlplane.benchmarking;

import static com.eaioc.controlplane.benchmarking.BaselineTestFixtures.fixtureControlPlaneRequest;
import static com.eaioc.controlplane.benchmarking.BaselineTestFixtures.fixtureLedgerEntry;

import static org.assertj.core.api.Assertions.assertThat;

import com.eaioc.controlplane.accounting.ledger.CostLedgerStore;
import com.eaioc.controlplane.accounting.ledger.LedgerCostReporter;
import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;
import com.eaioc.controlplane.evaluation.BaselineEvaluationRecord;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Integration test for {@code EXE-P0.2.C} (Capability 2 — Baseline Benchmark Harness + Quality
 * Evaluation, Sub-phase C — Integration), per {@code docs/execution-plan.md} §18.5's Sub-phase C
 * row: "Integration test with a stub caller." Since no P1+ technique exists yet to call into this
 * harness for real, this exercises {@link BaselineRunner#runBaseline} through
 * {@link StubTechniqueCaller} instead — an externally-wired caller resolved via a real Spring
 * {@link AnnotationConfigApplicationContext}, not the direct constructor-call pattern
 * {@code EXE-P0.2.B}'s own unit tests ({@link BaselineRunnerTest}) already use. Mirrors
 * {@code EXE-P0.1.C}'s {@code CostLedgerIntegrationTest} pattern exactly, adapted for the fact
 * that, unlike that precedent, no {@code @Component} annotation needs to be added here — the full
 * {@link BaselineRunner} → {@link LedgerCostReporter} → {@link CostLedgerStore} chain is already
 * annotated {@code @Component} from {@code EXE-P0.2.B} and {@code REM-P0.2.B-02}. This test
 * registers those beans explicitly and does not exercise component scanning, so it does not by
 * itself prove the annotations.
 *
 * <p>Deliberately does not use {@code @SpringBootTest}: that annotation requires a
 * {@code @SpringBootApplication}-annotated class to discover, and creating one is not this
 * capability's job (no capability in this P0 slice is scoped to application bootstrap — see
 * {@code docs/execution-plan.md} §13, "no external-facing API is built in this slice"). A plain
 * {@link AnnotationConfigApplicationContext} with explicit bean registration is the minimal way to
 * prove Spring wiring works without inventing that out-of-scope bootstrap class.
 *
 * <p><b>Contract-level, fixture-based — not a live Path A measurement.</b> This test proves only
 * that {@link BaselineRunner}'s public API is Spring-wireable and callable end-to-end by an
 * externally-wired caller ("Public API stable for future technique gates to call," this
 * sub-phase's own Definition of Done). It uses the same clearly-labelled
 * {@link BaselineTestFixtures} test-fixture ledger entry {@link BaselineRunnerTest} already uses,
 * never a live measurement, and proves nothing about, and never claims, a live Path A end-to-end
 * baseline measurement — that remains explicitly deferred to Capability Gate P0.2
 * ({@code docs/execution-plan.md} §18.10, §18.5's B row).
 */
class BaselineRunnerIntegrationTest {

    @Test
    void stubTechniqueCaller_callsRunBaselineThroughSpringWiredHarness() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(
                CostLedgerStore.class, LedgerCostReporter.class, BaselineRunner.class,
                StubTechniqueCaller.class);
            context.refresh();

            CostLedgerStore ledgerStore = context.getBean(CostLedgerStore.class);
            StubTechniqueCaller stubCaller = context.getBean(StubTechniqueCaller.class);

            ledgerStore.write(fixtureLedgerEntry(
                "tenant-integration", "entry-integration-1", "request-integration-1", true));
            ControlPlaneRequest request =
                fixtureControlPlaneRequest("tenant-integration", "request-integration-1");

            BaselineEvaluationRecord record = stubCaller.requestBaseline(request);

            assertThat(record.requestId()).isEqualTo("request-integration-1");
            assertThat(record.tenantId()).isEqualTo("tenant-integration");
            assertThat(record.runId()).isNotBlank();
        }
    }
}
