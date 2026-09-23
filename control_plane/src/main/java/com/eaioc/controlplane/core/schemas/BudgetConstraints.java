package com.eaioc.controlplane.core.schemas;

/**
 * {@code interfaces.md} §2.2 {@code BudgetConstraints}, realized field-for-field. Optional on
 * {@link ControlPlaneRequest} ("Defaults from policy", §2.3). Every field is nullable in the source.
 */
public record BudgetConstraints(
    Integer maxInputTokens,
    Integer maxOutputTokens,
    Double maxCostUsd,
    Integer maxReasoningTokens
) {
}
