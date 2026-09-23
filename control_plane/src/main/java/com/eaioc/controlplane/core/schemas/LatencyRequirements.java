package com.eaioc.controlplane.core.schemas;

/**
 * {@code interfaces.md} §2.2 {@code LatencyRequirements}, realized field-for-field. REQUIRED on
 * {@link ControlPlaneRequest} ("Drives interactive vs. batch", §2.3).
 */
public record LatencyRequirements(
    Integer maxE2eLatencyMs,    // nullable
    Integer maxTtftMs,          // nullable
    boolean interactive
) {
}
