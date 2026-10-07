package com.eaioc.controlplane.prompt_assembler.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.eaioc.controlplane.accounting.ledger.CostLedgerEntry;
import com.eaioc.controlplane.accounting.ledger.CostLedgerStore;
import com.eaioc.controlplane.accounting.ledger.LedgerCostReporter;
import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;
import com.eaioc.controlplane.core.schemas.InstructionContext;
import com.eaioc.controlplane.core.schemas.LatencyRequirements;
import com.eaioc.controlplane.core.schemas.QualityRequirements;
import com.eaioc.controlplane.core.schemas.RequestType;
import com.eaioc.controlplane.core.schemas.SecurityClassification;
import com.eaioc.controlplane.pipeline.t1_context.sanitizer.Sanitizer;
import com.eaioc.controlplane.pipeline.t1_context.sanitizer.SanitizerOutput;
import com.eaioc.controlplane.policy.context_policy.BudgetEnforcementResult;
import com.eaioc.controlplane.policy.context_policy.OptimizationPolicy;
import com.eaioc.controlplane.policy.context_policy.PolicyBudgetEnforcer;
import com.eaioc.controlplane.prompt_assembler.AssembledPrompt;
import com.eaioc.controlplane.prompt_assembler.PromptAssembler;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * EXE-P0.5 Sub-phase C (Integration), realized as the C row of {@code execution-plan.md} §18.8:
 * {@code PromptAssembler} wired to consume Capability 3's and Capability 4's <em>real</em> public
 * APIs end-to-end — not stubs, since both already exist by this point in the sequence (unlike the
 * Capability 3/4 C-row precedent, which stubbed a not-yet-built downstream consumer; here the two
 * <em>upstream</em> capabilities this unit itself depends on both already exist).
 *
 * <p>This is a real end-to-end wiring test, not a re-test of either upstream capability's own
 * internals (those remain covered by {@code SanitizerTest}/{@code PolicyBudgetEnforcerTest}). It
 * calls only each capability's existing public surface: {@link Sanitizer#sanitize}, {@link
 * PolicyBudgetEnforcer#enforceRequestBudget}, {@link PromptAssembler#assemble}. No main-code change
 * in this unit — test-tree only, matching the C row's own Failure-Path cell ({@code N/A}).
 *
 * <p>Per HD-CG-P0.5-02/-03, {@code PromptAssembler} itself never calls {@code
 * PolicyBudgetEnforcer} — this test demonstrates the correct <em>caller-side</em> sequencing
 * (Sanitizer, then PolicyBudgetEnforcer, then PromptAssembler), not a change to that design.
 */
class PromptAssemblerIntegrationTest {

    @Test
    void endToEndAssemblyFromRealSanitizerAndRealCompliantBudgetEnforcer() {
        String secret = "preserve-this-security-instruction-through-the-full-pipeline";
        ControlPlaneRequest request = request(secret, "what is the weather today?");

        SanitizerOutput sanitized = new Sanitizer().sanitize(request);

        CostLedgerStore store = new CostLedgerStore();
        store.write(entry(request.tenantId(), "entry-c5c-1", request.requestId(), 5.0));
        PolicyBudgetEnforcer enforcer = new PolicyBudgetEnforcer(new LedgerCostReporter(store));
        BudgetEnforcementResult budgetResult =
            enforcer.enforceRequestBudget(request.requestId(), request.tenantId(), policy(10.0));
        assertTrue(budgetResult.compliant());

        AssembledPrompt result = new PromptAssembler().assemble(request, sanitized, budgetResult);

        assertEquals(secret, result.cacheablePrefix());
        assertEquals("what is the weather today?", result.volatileSuffix());
        assertTrue(result.finalTokenCount() > 0);
    }

    @Test
    void endToEndAssemblyStillSucceedsWhenRealBudgetEnforcerReturnsNonCompliant() {
        // HD-CG-P0.5-03, demonstrated end-to-end with the real PolicyBudgetEnforcer (not a stub):
        // a genuine over-budget verdict does not block PromptAssembler.assemble().
        String secret = "security instruction present regardless of budget outcome";
        ControlPlaneRequest request = request(secret, "a question");

        SanitizerOutput sanitized = new Sanitizer().sanitize(request);

        CostLedgerStore store = new CostLedgerStore();
        store.write(entry(request.tenantId(), "entry-c5c-2", request.requestId(), 15.0));
        PolicyBudgetEnforcer enforcer = new PolicyBudgetEnforcer(new LedgerCostReporter(store));
        BudgetEnforcementResult budgetResult =
            enforcer.enforceRequestBudget(request.requestId(), request.tenantId(), policy(10.0));
        assertFalse(budgetResult.compliant());
        assertEquals("MAX_COST_PER_REQUEST_EXCEEDED", budgetResult.violations().get(0).ruleId());

        AssembledPrompt result = new PromptAssembler().assemble(request, sanitized, budgetResult);

        assertEquals(secret, result.cacheablePrefix());
        assertEquals("a question", result.volatileSuffix());
    }

    /** TEST FIXTURE: a schema-valid request carrying {@code systemContent} (STABLE) and {@code userInput}. */
    private static ControlPlaneRequest request(String systemContent, String userInput) {
        InstructionContext systemContext =
            new InstructionContext(systemContent, InstructionContext.Type.SYSTEM, InstructionContext.Stability.STABLE, 8, null);
        return new ControlPlaneRequest(
            "1.0.0", "request-c5c", "corr-c5c", "tenant-c5c", "org-c5c",
            "app-c5c", null, null, null, "task-c5c", null, 0,
            RequestType.GENERIC_LLM,
            userInput, systemContext, null, null,
            new QualityRequirements(0.9, QualityRequirements.QualityTier.STANDARD, false, true),
            new LatencyRequirements(2000, 500, true),
            null,
            new SecurityClassification(SecurityClassification.Level.INTERNAL, false, null, null),
            null, null,
            false,
            Map.of(), Instant.parse("2026-10-07T00:00:00Z"), null);
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
            "policy-c5c", "1.0.0", "1.0.0", "tenant-c5c", "org-c5c",
            Instant.parse("2026-01-01T00:00:00Z"), null,
            java.util.List.of(), java.util.List.of(), OptimizationPolicy.OptimizationDepth.STANDARD, false,
            java.util.List.of(), java.util.List.of(), "model-1", false,
            null, null, maxCostPerRequest, null,
            null, null,
            0.8, 0.8, 0.1,
            false, false, 0.9, false,
            OptimizationPolicy.PiiHandling.SCRUB, null,
            java.util.List.of(), 1.0, false,
            "test-author", null, "test fixture — EXE-P0.5.C integration");
    }
}
