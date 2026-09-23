package com.eaioc.controlplane.core.schemas;

/**
 * {@code interfaces.md} §2.2 {@code FreshnessRequirements}, realized field-for-field. Optional on
 * {@link ControlPlaneRequest}.
 */
public record FreshnessRequirements(
    Integer maxAgeSeconds,          // nullable
    boolean requireLiveData,
    StalenessAction stalenessAction
) {

    /** {@code staleness_action: enum { REJECT, WARN, ACCEPT }} */
    public enum StalenessAction {
        REJECT,
        WARN,
        ACCEPT
    }
}
