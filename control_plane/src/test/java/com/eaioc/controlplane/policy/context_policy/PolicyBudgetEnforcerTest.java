package com.eaioc.controlplane.policy.context_policy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.eaioc.controlplane.accounting.ledger.CostLedgerEntry;
import com.eaioc.controlplane.accounting.ledger.CostLedgerStore;
import com.eaioc.controlplane.accounting.ledger.LedgerCostReporter;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * EXE-P0.4.B and EXE-P0.4.D tests for {@link PolicyBudgetEnforcer} (interfaces.md §20;
 * HD-CG-P0.4-01/-02/-03; execution-plan.md §18.7 B and D rows).
 *
 * <p>Covers: a compliant request under budget; a request over {@code max_cost_per_request}; a
 * {@code null} limit (no cap configured) resolving to compliant; the two distinct conservative
 * failure paths (missing policy, missing ledger measurement) never resolving to compliant; tenant
 * isolation of the underlying ledger lookup; and (EXE-P0.4.D) exactly one structured log line per
 * call, at the level conventions.md §17.2 assigns to each outcome.
 */
class PolicyBudgetEnforcerTest {

    private ListAppender<ILoggingEvent> logs;
    private Logger enforcerLog;

    @BeforeEach
    void attachLogCapture() {
        enforcerLog = (Logger) LoggerFactory.getLogger(PolicyBudgetEnforcer.class);
        logs = new ListAppender<>();
        logs.start();
        enforcerLog.addAppender(logs);
    }

    @AfterEach
    void detachLogCapture() {
        enforcerLog.detachAppender(logs);
    }

    @Test
    void requestUnderBudget_isCompliant() {
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        store.write(entry("tenant-a", "entry-1", "request-1", 5.0));
        PolicyBudgetEnforcer enforcer = new PolicyBudgetEnforcer(reporter);

        BudgetEnforcementResult result =
            enforcer.enforceRequestBudget("request-1", "tenant-a", policy(10.0));

        assertTrue(result.compliant());
        assertTrue(result.violations().isEmpty());
    }

    @Test
    void requestOverMaxCostPerRequest_isNotCompliant() {
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        store.write(entry("tenant-a", "entry-1", "request-1", 15.0));
        PolicyBudgetEnforcer enforcer = new PolicyBudgetEnforcer(reporter);

        BudgetEnforcementResult result =
            enforcer.enforceRequestBudget("request-1", "tenant-a", policy(10.0));

        assertFalse(result.compliant());
        assertEquals(1, result.violations().size());
        assertEquals("MAX_COST_PER_REQUEST_EXCEEDED", result.violations().get(0).ruleId());
        assertEquals(PolicyViolation.Severity.BLOCK, result.violations().get(0).severity());
    }

    @Test
    void requestAtExactlyMaxCostPerRequest_isCompliant() {
        // Pins the `>` (not `>=`) comparison: exactly-at-budget is compliant, not a violation.
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        store.write(entry("tenant-a", "entry-1", "request-1", 10.0));
        PolicyBudgetEnforcer enforcer = new PolicyBudgetEnforcer(reporter);

        BudgetEnforcementResult result =
            enforcer.enforceRequestBudget("request-1", "tenant-a", policy(10.0));

        assertTrue(result.compliant());
    }

    @Test
    void nullRequestIdOrTenantId_throwsNullPointerException() {
        PolicyBudgetEnforcer enforcer = new PolicyBudgetEnforcer(new LedgerCostReporter(new CostLedgerStore()));

        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class,
            () -> enforcer.enforceRequestBudget(null, "tenant-a", policy(10.0)));
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class,
            () -> enforcer.enforceRequestBudget("request-1", null, policy(10.0)));
    }

    @Test
    void nullMaxCostPerRequest_noCapConfigured_isCompliant() {
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        store.write(entry("tenant-a", "entry-1", "request-1", 999.0));
        PolicyBudgetEnforcer enforcer = new PolicyBudgetEnforcer(reporter);

        BudgetEnforcementResult result =
            enforcer.enforceRequestBudget("request-1", "tenant-a", policy(null));

        assertTrue(result.compliant());
    }

    @Test
    void nullPolicy_isConservativelyDenied_notPermissive_andProducesNoPolicyViolation() {
        // Redesign (2026-10-07, reviewer finding): no policy supplied is not a policy-content
        // violation, so no PolicyViolation is manufactured; conservative denial is compliant=false
        // with an empty violations list, keeping PolicyViolation.policyId exactly INTF-036-conformant
        // (required, never null).
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        PolicyBudgetEnforcer enforcer = new PolicyBudgetEnforcer(reporter);

        BudgetEnforcementResult result = enforcer.enforceRequestBudget("request-1", "tenant-a", null);

        assertFalse(result.compliant());
        assertTrue(result.violations().isEmpty());
    }

    @Test
    void missingLedgerMeasurement_isConservativelyDenied_notCompliant() {
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        PolicyBudgetEnforcer enforcer = new PolicyBudgetEnforcer(reporter);

        BudgetEnforcementResult result =
            enforcer.enforceRequestBudget("request-missing", "tenant-a", policy(10.0));

        assertFalse(result.compliant());
        assertEquals("LEDGER_MEASUREMENT_UNAVAILABLE", result.violations().get(0).ruleId());
    }

    @Test
    void otherTenantsEntry_neverLeaksIntoThisTenantsDecision() {
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        // Same request_id, a different tenant, with a cost that would violate tenant-a's budget.
        store.write(entry("tenant-b", "entry-2", "request-1", 999.0));
        PolicyBudgetEnforcer enforcer = new PolicyBudgetEnforcer(reporter);

        BudgetEnforcementResult result =
            enforcer.enforceRequestBudget("request-1", "tenant-a", policy(10.0));

        // tenant-a has no matching entry of its own -> conservative denial, never tenant-b's result.
        assertFalse(result.compliant());
        assertEquals("LEDGER_MEASUREMENT_UNAVAILABLE", result.violations().get(0).ruleId());
    }

    // EXE-P0.4.D: exactly one structured line per call (plan §18.7 D row).

    @Test
    void compliantResolutionEmitsExactlyOneInfoLine() {
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        store.write(entry("tenant-d", "entry-d-1", "request-d-1", 5.0));
        PolicyBudgetEnforcer enforcer = new PolicyBudgetEnforcer(reporter);

        enforcer.enforceRequestBudget("request-d-1", "tenant-d", policy(10.0));

        List<String> lines = linesContaining("event_type=POLICY_ENFORCEMENT_COMPLETED");
        assertEquals(1, lines.size());
        assertEquals(1, allLines().size());
        assertEquals(Level.INFO, allEvents().get(0).getLevel());
        String line = lines.get(0);
        assertTrue(line.contains("component_id=CONTEXT-POLICY"), line);
        assertTrue(line.contains("tenant_id=tenant-d"), line);
        assertTrue(line.contains("request_id=request-d-1"), line);
        assertTrue(line.contains("policy_id=policy-1"), line);
        assertTrue(line.contains("span_id="), line);
        assertTrue(line.contains("parent_span_id=null"), line);
    }

    @Test
    void maxCostPerRequestViolationEmitsExactlyOneErrorLineWithRuleIdAndSeverity() {
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        store.write(entry("tenant-d", "entry-d-2", "request-d-2", 15.0));
        PolicyBudgetEnforcer enforcer = new PolicyBudgetEnforcer(reporter);

        enforcer.enforceRequestBudget("request-d-2", "tenant-d", policy(10.0));

        List<String> lines = linesContaining("event_type=POLICY_VIOLATION");
        assertEquals(1, lines.size());
        assertEquals(1, allLines().size());
        assertEquals(Level.ERROR, allEvents().get(0).getLevel());
        String line = lines.get(0);
        assertTrue(line.contains("rule_id=MAX_COST_PER_REQUEST_EXCEEDED"), line);
        assertTrue(line.contains("severity=BLOCK"), line);
        assertTrue(line.contains("policy_id=policy-1"), line);
    }

    @Test
    void missingLedgerMeasurementEmitsExactlyOneErrorLine() {
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        PolicyBudgetEnforcer enforcer = new PolicyBudgetEnforcer(reporter);

        enforcer.enforceRequestBudget("request-d-3", "tenant-d", policy(10.0));

        List<String> lines = linesContaining("event_type=POLICY_VIOLATION");
        assertEquals(1, lines.size());
        assertEquals(1, allLines().size());
        assertTrue(lines.get(0).contains("rule_id=LEDGER_MEASUREMENT_UNAVAILABLE"), lines.get(0));
        assertTrue(lines.get(0).contains("severity=BLOCK"), lines.get(0));
    }

    @Test
    void nullPolicyEmitsExactlyOneWarningLineWithNoPolicyId() {
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        PolicyBudgetEnforcer enforcer = new PolicyBudgetEnforcer(reporter);

        enforcer.enforceRequestBudget("request-d-4", "tenant-d", null);

        List<String> lines = linesContaining("event_type=POLICY_ENFORCEMENT_FALLBACK_NO_POLICY");
        assertEquals(1, lines.size());
        assertEquals(1, allLines().size());
        assertEquals(Level.WARN, allEvents().get(0).getLevel());
        assertTrue(lines.get(0).contains("reason=no_policy_supplied"), lines.get(0));
        assertFalse(lines.get(0).contains("policy_id="), lines.get(0));
    }

    private List<ILoggingEvent> allEvents() {
        return logs.list;
    }

    private List<String> allLines() {
        return logs.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
    }

    private List<String> linesContaining(String substring) {
        return allLines().stream().filter(l -> l.contains(substring)).toList();
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
            "policy-1", "1.0.0", "1.0.0", "tenant-a", "org-1",
            Instant.parse("2026-01-01T00:00:00Z"), null,
            java.util.List.of(), java.util.List.of(), OptimizationPolicy.OptimizationDepth.STANDARD, false,
            java.util.List.of(), java.util.List.of(), "model-1", false,
            null, null, maxCostPerRequest, null,
            null, null,
            0.8, 0.8, 0.1,
            false, false, 0.9, false,
            OptimizationPolicy.PiiHandling.SCRUB, null,
            java.util.List.of(), 1.0, false,
            "test-author", null, "test fixture");
    }
}
