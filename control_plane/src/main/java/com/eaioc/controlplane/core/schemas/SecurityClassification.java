package com.eaioc.controlplane.core.schemas;

import java.util.List;

/**
 * {@code interfaces.md} §2.2 {@code SecurityClassification}, realized field-for-field. REQUIRED on
 * {@link ControlPlaneRequest} ("Governs cache safety", §2.3).
 *
 * <p>{@code piiCategories}/{@code dataResidency} are nullable in the source ({@code string[] | null})
 * so {@code null} is preserved; a non-null list is defensively copied (design note D3).
 */
public record SecurityClassification(
    Level level,
    Boolean containsPii,            // nullable
    List<String> piiCategories,     // nullable
    List<String> dataResidency      // nullable
) {

    public SecurityClassification {
        piiCategories = piiCategories == null ? null : List.copyOf(piiCategories);
        dataResidency = dataResidency == null ? null : List.copyOf(dataResidency);
    }

    /** {@code level: enum { PUBLIC, INTERNAL, CONFIDENTIAL, RESTRICTED }} */
    public enum Level {
        PUBLIC,
        INTERNAL,
        CONFIDENTIAL,
        RESTRICTED
    }
}
