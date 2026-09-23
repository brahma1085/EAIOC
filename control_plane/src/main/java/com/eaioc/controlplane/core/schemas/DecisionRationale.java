package com.eaioc.controlplane.core.schemas;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code interfaces.md} §3.2 {@code DecisionRationale}, realized field-for-field.
 *
 * <p>All collections are non-nullable in the source: {@code null} becomes empty and each is
 * defensively copied (design note D3). {@code keySignals} ({@code map<string, any>}) preserves
 * {@code null} values.
 */
public record DecisionRationale(
    String summary,
    Map<String, Object> keySignals,
    List<String> policyReferences,
    List<String> modelReferences,
    List<String> historyReferences
) {

    public DecisionRationale {
        keySignals = keySignals == null
            ? Map.of()
            : Collections.unmodifiableMap(new LinkedHashMap<>(keySignals));
        policyReferences = policyReferences == null ? List.of() : List.copyOf(policyReferences);
        modelReferences = modelReferences == null ? List.of() : List.copyOf(modelReferences);
        historyReferences = historyReferences == null ? List.of() : List.copyOf(historyReferences);
    }
}
