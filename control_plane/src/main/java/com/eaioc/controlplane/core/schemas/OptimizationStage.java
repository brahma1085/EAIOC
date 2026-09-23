package com.eaioc.controlplane.core.schemas;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@code interfaces.md} §3.2 {@code OptimizationStage} plus the §3.4 extension fields
 * ({@code operating_mode}, {@code decision_ownership}).
 *
 * <p><b>Explicitly incomplete (design note D2):</b> {@code fallback_on_failure: FallbackAction} is
 * absent — {@code FallbackAction} has two conflicting definitions ({@code interfaces.md} §17 vs.
 * §22.1, {@code CORE-GAP-04}) and no source says which one applies here.
 *
 * <p>{@code configOverrides} ({@code map<string, any>}) is copied into an unmodifiable map that
 * preserves {@code null} values; {@code null} becomes empty (D3).
 */
public record OptimizationStage(
    String stageId,
    String stageVersion,
    int executionOrder,
    boolean required,
    int timeoutMs,
    Map<String, Object> configOverrides,

    // §3.4 extension
    OperatingMode operatingMode,
    DecisionOwnership decisionOwnership
) {

    public OptimizationStage {
        configOverrides = configOverrides == null
            ? Map.of()
            : Collections.unmodifiableMap(new LinkedHashMap<>(configOverrides));
    }
}
