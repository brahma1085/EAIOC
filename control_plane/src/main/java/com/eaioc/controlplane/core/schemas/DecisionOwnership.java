package com.eaioc.controlplane.core.schemas;

/**
 * Whether the Control Plane is advising, enforcing, or directly owning the resulting action.
 *
 * <p>Source: {@code interfaces.md} §43.10
 * ({@code DecisionOwnership: enum { ADVISORY, ENFORCEMENT, EXECUTION_OWNERSHIP }}), carried on
 * {@link OptimizationPlan}, {@link OptimizationStage} and {@link SkippedStage} via the §3.4
 * hardening extension.
 */
public enum DecisionOwnership {
    ADVISORY,
    ENFORCEMENT,
    EXECUTION_OWNERSHIP
}
