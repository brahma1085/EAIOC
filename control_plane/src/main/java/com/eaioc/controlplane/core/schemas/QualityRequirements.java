package com.eaioc.controlplane.core.schemas;

/**
 * {@code interfaces.md} §2.2 {@code QualityRequirements}, realized field-for-field. REQUIRED on
 * {@link ControlPlaneRequest} ("Never omitted", §2.3).
 */
public record QualityRequirements(
    double minQualityScore,     // source comment: 0.0-1.0
    QualityTier qualityTier,
    boolean allowDegraded,
    boolean validationRequired
) {

    /** {@code quality_tier: enum { DRAFT, STANDARD, HIGH, CRITICAL }} */
    public enum QualityTier {
        DRAFT,
        STANDARD,
        HIGH,
        CRITICAL
    }
}
