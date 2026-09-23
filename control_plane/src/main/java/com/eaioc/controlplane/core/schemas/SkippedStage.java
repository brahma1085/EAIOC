package com.eaioc.controlplane.core.schemas;

/**
 * {@code interfaces.md} §3.2 {@code SkippedStage} plus the §3.4 extension fields
 * ({@code operating_mode}, {@code decision_ownership}), realized field-for-field.
 */
public record SkippedStage(
    String stageId,
    Reason reason,
    double expectedSavings,
    double optimizationCost,
    double netValue,
    String explanation,

    // §3.4 extension
    OperatingMode operatingMode,
    DecisionOwnership decisionOwnership
) {

    /**
     * {@code reason} — the seven §3.2 values plus {@code DO_NOT_OPTIMIZE}, which §3.4 adds
     * ("net-negative expected value … no existing value is removed or renumbered").
     */
    public enum Reason {
        COST_EXCEEDS_BENEFIT,
        POLICY_DISABLED,
        NOT_APPLICABLE,
        PREREQUISITE_NOT_MET,
        CACHE_HIT,
        LATENCY_CONSTRAINT,
        QUALITY_RISK_TOO_HIGH,
        DO_NOT_OPTIMIZE
    }
}
