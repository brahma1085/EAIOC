package com.eaioc.controlplane.core.schemas;

import java.time.Instant;
import java.util.List;

/**
 * Optimization decision output — {@code interfaces.md} §3.2 {@code OptimizationPlan} plus the §3.4
 * extension fields ({@code operating_mode}, {@code decision_ownership}).
 *
 * <p><b>EXPLICITLY INCOMPLETE REALIZATION (design note D2) — not a conformant INTF §3.2 decision
 * output.</b> Two source fields that are <em>not</em> nullable are absent because their types
 * cannot be realized without inventing content:
 * <ul>
 *   <li>{@code fallback_strategy: FallbackStrategy} — depends on {@code FallbackAction}, which has
 *       two conflicting definitions ({@code interfaces.md} §17 vs. §22.1, {@code CORE-GAP-04});</li>
 *   <li>{@code escalation_policy: EscalationPolicy} — the type is defined nowhere in the corpus
 *       ({@code CORE-GAP-03}).</li>
 * </ul>
 * Acceptable at P0 only because no P0 capability <em>produces</em> an {@code OptimizationPlan}
 * (the decision engine, OI-001, is P4); its sole P0 consumer, {@code run_optimized()} in
 * {@code EXE-P0.2.B}, is an explicit no-op ({@code docs/execution-plan.md} §18.5). Nothing may
 * treat an instance of this type as a complete decision until both gaps close upstream and the
 * fields are added.
 *
 * <p>Every other field is realized in source order. List fields are non-nullable in the source:
 * {@code null} becomes empty and each is defensively copied (D3).
 */
public record OptimizationPlan(
    String schemaVersion,
    String requestId,
    String planId,

    // EXECUTION PLAN
    List<OptimizationStage> selectedStages,
    List<SkippedStage> skippedStages,
    OptimizationDepth optimizationDepth,

    // DECISIONS
    boolean serveFromExactCache,
    boolean serveFromSemanticCache,
    boolean executeRetrieval,
    boolean executeCompression,
    CompressionDepth compressionDepth,      // nullable
    boolean executeDeduplication,
    boolean executePruning,
    boolean executeReordering,
    ToolSelectionMode toolSelectionMode,
    boolean allowSubAgentCreation,
    boolean modelRoutingRequired,
    boolean batchEligible,

    // BUDGETS
    int contextTokenBudget,
    int outputTokenBudget,
    ReasoningBudget reasoningBudget,
    Double optimizerCostBudget,             // nullable

    // ECONOMICS
    double expectedInferenceCostAvoided,
    double expectedOptimizationCost,
    double expectedNetSavings,
    double expectedQualityImpact,
    int expectedLatencyDeltaMs,
    double optimizationConfidence,

    // FALLBACK — fallback_strategy and escalation_policy deferred (see class Javadoc)

    // GOVERNANCE
    String policyVersion,
    List<String> policyConstraintsApplied,
    DecisionRationale decisionRationale,

    // TRACING
    String spanId,
    Instant timestamp,

    // §3.4 extension
    OperatingMode operatingMode,
    DecisionOwnership decisionOwnership
) {

    public OptimizationPlan {
        selectedStages = selectedStages == null ? List.of() : List.copyOf(selectedStages);
        skippedStages = skippedStages == null ? List.of() : List.copyOf(skippedStages);
        policyConstraintsApplied =
            policyConstraintsApplied == null ? List.of() : List.copyOf(policyConstraintsApplied);
    }

    /** {@code optimization_depth: enum { MINIMAL, STANDARD, DEEP }} */
    public enum OptimizationDepth {
        MINIMAL,
        STANDARD,
        DEEP
    }

    /** {@code compression_depth: enum { NONE, LIGHT, MODERATE, AGGRESSIVE } | null} */
    public enum CompressionDepth {
        NONE,
        LIGHT,
        MODERATE,
        AGGRESSIVE
    }

    /** {@code tool_selection_mode: enum { ALL, SELECTIVE, NONE }} */
    public enum ToolSelectionMode {
        ALL,
        SELECTIVE,
        NONE
    }

    /** {@code reasoning_budget: enum { LOW, MEDIUM, HIGH, MAX }} */
    public enum ReasoningBudget {
        LOW,
        MEDIUM,
        HIGH,
        MAX
    }
}
