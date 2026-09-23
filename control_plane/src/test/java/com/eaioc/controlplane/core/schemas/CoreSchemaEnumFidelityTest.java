package com.eaioc.controlplane.core.schemas;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * Enum fidelity for {@code REM-P0.1.A-02} ({@code control_plane/core/CoreFoundation.md} §10): every
 * realized enum's constant set equals the source's set exactly, in source order. Expected values
 * are transcribed from {@code interfaces.md} §2.1, §2.2, §3.2, §3.4, §11 and §43.10.
 */
class CoreSchemaEnumFidelityTest {

    @Test
    void requestType_matchesInterfacesSection2_1() {
        assertConstants(RequestType.class, "GENERIC_LLM", "AGENTIC_WORKFLOW", "DEVELOPER_CODING",
            "MULTI_AGENT", "RAG", "TOOL_ENABLED", "BATCH", "SUB_AGENT");
    }

    @Test
    void section2_2SubTypeEnums_matchSource() {
        assertConstants(InstructionContext.Type.class, "SYSTEM", "DEVELOPER", "USER");
        assertConstants(InstructionContext.Stability.class, "STABLE", "SEMI_STABLE", "VOLATILE");
        assertConstants(QualityRequirements.QualityTier.class, "DRAFT", "STANDARD", "HIGH", "CRITICAL");
        assertConstants(SecurityClassification.Level.class,
            "PUBLIC", "INTERNAL", "CONFIDENTIAL", "RESTRICTED");
        assertConstants(FreshnessRequirements.StalenessAction.class, "REJECT", "WARN", "ACCEPT");
    }

    @Test
    void agentStateStatus_matchesInterfacesSection11() {
        assertConstants(AgentState.Status.class,
            "RUNNING", "PAUSED", "WAITING_FOR_TOOL", "COMPLETED", "FAILED");
    }

    @Test
    void optimizationPlanEnums_matchSection3_2() {
        assertConstants(OptimizationPlan.OptimizationDepth.class, "MINIMAL", "STANDARD", "DEEP");
        assertConstants(OptimizationPlan.CompressionDepth.class, "NONE", "LIGHT", "MODERATE", "AGGRESSIVE");
        assertConstants(OptimizationPlan.ToolSelectionMode.class, "ALL", "SELECTIVE", "NONE");
        assertConstants(OptimizationPlan.ReasoningBudget.class, "LOW", "MEDIUM", "HIGH", "MAX");
    }

    @Test
    void skippedStageReason_matchesSection3_2_plusSection3_4DoNotOptimize() {
        assertConstants(SkippedStage.Reason.class, "COST_EXCEEDS_BENEFIT", "POLICY_DISABLED",
            "NOT_APPLICABLE", "PREREQUISITE_NOT_MET", "CACHE_HIT", "LATENCY_CONSTRAINT",
            "QUALITY_RISK_TOO_HIGH", "DO_NOT_OPTIMIZE");
    }

    @Test
    void section43_10SharedEnums_matchSource() {
        assertConstants(OperatingMode.class, "SYNC", "ASYNC", "HYBRID");
        assertConstants(DecisionOwnership.class, "ADVISORY", "ENFORCEMENT", "EXECUTION_OWNERSHIP");
    }

    private static void assertConstants(Class<? extends Enum<?>> type, String... expected) {
        assertThat(Arrays.stream(type.getEnumConstants()).map(Enum::name).toList())
            .as(type.getName())
            .containsExactly(expected);
    }
}
