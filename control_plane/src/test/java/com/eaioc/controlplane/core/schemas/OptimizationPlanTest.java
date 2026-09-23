package com.eaioc.controlplane.core.schemas;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@code REM-P0.1.A-02} covering {@link OptimizationPlan} and its realized sub-types, per
 * {@code control_plane/core/CoreFoundation.md} §10 (round-trip, defensive copies — D3) and D2 (the
 * explicitly incomplete realization must not carry the deferred fields).
 */
class OptimizationPlanTest {

    @Test
    void constructWithAllRealizedFields_roundTripsUnchanged() {
        OptimizationStage stage = new OptimizationStage(
            "T1.3-CONTEXT-PRUNER", "1.0", 1, true, 250, Map.of("threshold", 0.4),
            OperatingMode.SYNC, DecisionOwnership.ENFORCEMENT);
        SkippedStage skipped = new SkippedStage(
            "T1.5-CONTEXT-COMPRESSOR", SkippedStage.Reason.DO_NOT_OPTIMIZE, 0.01, 0.02, -0.01,
            "net-negative", OperatingMode.SYNC, DecisionOwnership.ADVISORY);
        DecisionRationale rationale = new DecisionRationale(
            "summary", Map.of("signal", 1), List.of("policy-1"), List.of(), List.of());

        OptimizationPlan plan = plan(List.of(stage), List.of(skipped), List.of("constraint-1"), rationale);

        assertThat(plan.requestId()).isEqualTo("request-1");
        assertThat(plan.planId()).isEqualTo("plan-1");
        assertThat(plan.selectedStages()).containsExactly(stage);
        assertThat(plan.skippedStages()).containsExactly(skipped);
        assertThat(plan.optimizationDepth()).isEqualTo(OptimizationPlan.OptimizationDepth.STANDARD);
        assertThat(plan.compressionDepth()).isNull();
        assertThat(plan.reasoningBudget()).isEqualTo(OptimizationPlan.ReasoningBudget.MEDIUM);
        assertThat(plan.optimizerCostBudget()).isNull();
        assertThat(plan.policyConstraintsApplied()).containsExactly("constraint-1");
        assertThat(plan.decisionRationale()).isEqualTo(rationale);
        assertThat(plan.operatingMode()).isEqualTo(OperatingMode.HYBRID);
        assertThat(plan.decisionOwnership()).isEqualTo(DecisionOwnership.ADVISORY);
    }

    @Test
    void deferredFields_areAbsent_explicitlyIncompleteRealization() {
        // D2: fallback_strategy / escalation_policy (CORE-GAP-03/04) and
        // OptimizationStage.fallback_on_failure (CORE-GAP-04) must not exist as record components.
        assertThat(componentNames(OptimizationPlan.class))
            .doesNotContain("fallbackStrategy", "escalationPolicy");
        assertThat(componentNames(OptimizationStage.class)).doesNotContain("fallbackOnFailure");
    }

    @Test
    void lists_areDefensivelyCopied_andNullBecomesEmpty() {
        List<String> constraints = new ArrayList<>(List.of("c1"));
        OptimizationPlan plan = plan(null, null, constraints, null);

        constraints.add("c2");

        assertThat(plan.policyConstraintsApplied()).containsExactly("c1");
        assertThat(plan.selectedStages()).isEmpty();
        assertThat(plan.skippedStages()).isEmpty();
    }

    @Test
    void anyTypedMaps_areCopied_preserveNullValues_andNullBecomesEmpty() {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("nullable", null);
        OptimizationStage stage = new OptimizationStage(
            "s", "1", 0, false, 10, overrides, OperatingMode.ASYNC, DecisionOwnership.ADVISORY);
        overrides.put("added-later", 1);

        assertThat(stage.configOverrides()).containsOnlyKeys("nullable");
        assertThat(stage.configOverrides().get("nullable")).isNull();

        DecisionRationale rationale = new DecisionRationale("s", null, null, null, null);
        assertThat(rationale.keySignals()).isEmpty();
        assertThat(rationale.policyReferences()).isEmpty();
        assertThat(rationale.modelReferences()).isEmpty();
        assertThat(rationale.historyReferences()).isEmpty();
    }

    private static List<String> componentNames(Class<? extends Record> type) {
        return Arrays.stream(type.getRecordComponents()).map(c -> c.getName()).toList();
    }

    private static OptimizationPlan plan(
        List<OptimizationStage> selected, List<SkippedStage> skipped,
        List<String> constraints, DecisionRationale rationale) {
        return new OptimizationPlan(
            "1.0.0", "request-1", "plan-1",
            selected, skipped, OptimizationPlan.OptimizationDepth.STANDARD,
            false, false, true, false, null, false, true, false,
            OptimizationPlan.ToolSelectionMode.SELECTIVE, false, false, false,
            8000, 1000, OptimizationPlan.ReasoningBudget.MEDIUM, null,
            0.10, 0.01, 0.09, 0.0, 5, 0.8,
            "policy-v1", constraints, rationale,
            "span-1", Instant.parse("2026-09-23T00:00:00Z"),
            OperatingMode.HYBRID, DecisionOwnership.ADVISORY);
    }
}
