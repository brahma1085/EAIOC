package com.eaioc.controlplane.policy.context_policy.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.eaioc.controlplane.accounting.ledger.CostLedgerEntry;
import com.eaioc.controlplane.accounting.ledger.CostLedgerStore;
import com.eaioc.controlplane.accounting.ledger.LedgerCostReporter;
import com.eaioc.controlplane.policy.context_policy.BudgetEnforcementResult;
import com.eaioc.controlplane.policy.context_policy.OptimizationPolicy;
import com.eaioc.controlplane.policy.context_policy.PolicyBudgetEnforcer;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * EXE-P0.4 Sub-phase C (Integration), realized as the C row of {@code execution-plan.md} §18.7:
 * Capability 4's public read API ({@link PolicyBudgetEnforcer#enforceRequestBudget}) is consumable
 * by a stub downstream consumer in a separate package, standing in for Prompt Assembler (Capability
 * 5) / Output Controls (Capability 6) — neither built yet. This is NOT pipeline wiring: the stub
 * constructs its own {@link OptimizationPolicy}, since no storage/lookup component exists
 * ({@code SOURCE-GAP-EXECPLAN-32}).
 *
 * <p>Failure-path cell of the C row is N/A (plan §18.7). The two conservative failure paths
 * (missing policy, missing ledger measurement) are already covered as failure-path evidence of
 * record by {@code PolicyBudgetEnforcerTest} (EXE-P0.4.B); both are additionally exercised here,
 * through the public seam only, to demonstrate full parity of "Public API stable" (the C row's own
 * Definition of Done) rather than as a second formal failure-path test.
 */
class PolicyBudgetEnforcerIntegrationTest {

    @Test
    void consumerObservesCompliantRequestThroughPublicApi() {
        CostLedgerStore store = new CostLedgerStore();
        store.write(entry("tenant-c4c", "entry-c4c-1", "request-c4c-1", 5.0));
        StubDownstreamConsumer consumer =
            new StubDownstreamConsumer(new PolicyBudgetEnforcer(new LedgerCostReporter(store)));

        BudgetEnforcementResult result = consumer.checkBudget("request-c4c-1", "tenant-c4c", policy(10.0));

        assertTrue(result.compliant());
        assertTrue(result.violations().isEmpty());
    }

    @Test
    void consumerObservesMaxCostPerRequestViolationThroughPublicApi() {
        CostLedgerStore store = new CostLedgerStore();
        store.write(entry("tenant-c4c", "entry-c4c-2", "request-c4c-2", 15.0));
        StubDownstreamConsumer consumer =
            new StubDownstreamConsumer(new PolicyBudgetEnforcer(new LedgerCostReporter(store)));

        BudgetEnforcementResult result = consumer.checkBudget("request-c4c-2", "tenant-c4c", policy(10.0));

        assertFalse(result.compliant());
        assertEquals(1, result.violations().size());
        assertEquals("MAX_COST_PER_REQUEST_EXCEEDED", result.violations().get(0).ruleId());
    }

    @Test
    void consumerObservesConservativeDenialOnMissingPolicyThroughPublicApi() {
        CostLedgerStore store = new CostLedgerStore();
        StubDownstreamConsumer consumer =
            new StubDownstreamConsumer(new PolicyBudgetEnforcer(new LedgerCostReporter(store)));

        BudgetEnforcementResult result = consumer.checkBudget("request-c4c-3", "tenant-c4c", null);

        // HD-CG-P0.4-01/-03: conservative denial, no PolicyViolation manufactured.
        assertFalse(result.compliant());
        assertTrue(result.violations().isEmpty());
    }

    @Test
    void consumerObservesConservativeDenialOnMissingLedgerMeasurementThroughPublicApi() {
        CostLedgerStore store = new CostLedgerStore();
        StubDownstreamConsumer consumer =
            new StubDownstreamConsumer(new PolicyBudgetEnforcer(new LedgerCostReporter(store)));

        BudgetEnforcementResult result = consumer.checkBudget("request-c4c-missing", "tenant-c4c", policy(10.0));

        assertFalse(result.compliant());
        assertEquals("LEDGER_MEASUREMENT_UNAVAILABLE", result.violations().get(0).ruleId());
    }

    /** TEST FIXTURE: a minimal, schema-valid, verified=true CostLedgerEntry with the given total cost. */
    private static CostLedgerEntry entry(String tenantId, String entryId, String requestId, double totalOptimizedCost) {
        return new CostLedgerEntry(
            entryId, requestId, tenantId, "org-1", null, null, "task-1", Instant.parse("2026-10-07T00:00:00Z"),
            100, 50, 100, 50, 0, 0, 0, 0, 0,
            totalOptimizedCost, totalOptimizedCost, 0.0, 0.0, 0.0, 0.0, 0.0,
            Map.of(), "USD", "v1", "model-1", "provider-1",
            new CostLedgerEntry.Input(100, 100, 0, 0, 0, 0, 0, 0, 0, 100),
            new CostLedgerEntry.Output(50, 50, 0, 0),
            new CostLedgerEntry.Cache(0, 0, 0, 0, 0, 0, 0),
            new CostLedgerEntry.Model("model-1", "model-1", "DIRECT", false, "MEDIUM", null),
            new CostLedgerEntry.Tools(0, 0, 0, 0, 0),
            new CostLedgerEntry.Workflow(0, 0, 0, 0, 0),
            new CostLedgerEntry.Cost(totalOptimizedCost, 0.0, 0.0, 0.0, 0.0, totalOptimizedCost, 0.0, 0.0, 0.0),
            new CostLedgerEntry.Performance(100, 50, 50, 0, 0, 0),
            new CostLedgerEntry.Quality(1.0, 1.0, true, 1.0, 1.0, true),
            true);
    }

    /** TEST FIXTURE: a minimal, schema-valid OptimizationPolicy with the given max_cost_per_request. */
    private static OptimizationPolicy policy(Double maxCostPerRequest) {
        return new OptimizationPolicy(
            "policy-c4c", "1.0.0", "1.0.0", "tenant-c4c", "org-1",
            Instant.parse("2026-01-01T00:00:00Z"), null,
            java.util.List.of(), java.util.List.of(), OptimizationPolicy.OptimizationDepth.STANDARD, false,
            java.util.List.of(), java.util.List.of(), "model-1", false,
            null, null, maxCostPerRequest, null,
            null, null,
            0.8, 0.8, 0.1,
            false, false, 0.9, false,
            OptimizationPolicy.PiiHandling.SCRUB, null,
            java.util.List.of(), 1.0, false,
            "test-author", null, "test fixture — EXE-P0.4.C integration");
    }
}
