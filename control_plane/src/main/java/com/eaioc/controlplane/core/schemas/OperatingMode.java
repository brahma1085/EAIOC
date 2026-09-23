package com.eaioc.controlplane.core.schemas;

/**
 * Whether a decision was computed synchronously, asynchronously, or in a hybrid manner.
 *
 * <p>Source: {@code interfaces.md} §43.10 ({@code OperatingMode: enum { SYNC, ASYNC, HYBRID }}),
 * carried on {@link OptimizationPlan}, {@link OptimizationStage} and {@link SkippedStage} via the
 * §3.4 hardening extension.
 */
public enum OperatingMode {
    SYNC,
    ASYNC,
    HYBRID
}
