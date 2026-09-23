package com.eaioc.controlplane.accounting.ledger;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Integration test for {@code EXE-P0.1.C} (Capability 1 — Token Accounting and Cost Ledger,
 * Sub-phase C — Integration), per {@code docs/execution-plan.md} §18.4's Sub-phase C row:
 * "Integration test: a second in-scope capability (once built) can call the write API." Since no
 * other in-scope capability exists yet, this exercises the write API through
 * {@link StubPipelineStageCaller} instead — an externally-wired caller, not the direct
 * constructor-call pattern the {@code EXE-P0.1.B} unit tests already use.
 *
 * <p>Deliberately does not use {@code @SpringBootTest}: that annotation requires a
 * {@code @SpringBootApplication}-annotated class to discover, and creating one is not this
 * capability's job (no capability in this P0 slice is scoped to application bootstrap — see
 * {@code docs/execution-plan.md} §13, "no external-facing API is built in this slice"). A plain
 * {@link AnnotationConfigApplicationContext} with explicit bean registration is the minimal way to
 * prove Spring wiring works without inventing that out-of-scope bootstrap class.
 */
class CostLedgerIntegrationTest {

    @Test
    void stubPipelineStageCaller_writesThroughSpringWiredLedgerStore() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(CostLedgerStore.class, StubPipelineStageCaller.class);
            context.refresh();

            CostLedgerStore ledgerStore = context.getBean(CostLedgerStore.class);
            StubPipelineStageCaller stubCaller = context.getBean(StubPipelineStageCaller.class);

            stubCaller.recordStubRequestCost("tenant-integration", "entry-integration-1", "request-integration-1");

            assertThat(ledgerStore.read("tenant-integration", "entry-integration-1")).isPresent();
        }
    }
}
