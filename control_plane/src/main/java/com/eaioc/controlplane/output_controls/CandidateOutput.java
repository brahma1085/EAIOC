package com.eaioc.controlplane.output_controls;

import java.util.Map;
import java.util.Objects;

/**
 * The response operand {@link OutputController} validates and enforces length on.
 *
 * <p><b>HD-CG-P0.6-01 (2026-10-08):</b> no schema anywhere in {@code interfaces.md} or
 * {@code core/schemas/} represents "a response" for this capability (confirmed by exhaustive grep,
 * independently repeated by the Architect pre-flight for {@code EXE-P0.6.B}); the only plausible
 * candidate, {@code interfaces.md} §7.2's {@code LLMInvocationResult}, is a provider-adapter-boundary
 * type (§7, LLM Provider Interface) this capability does not adopt, to avoid blurring that boundary
 * with the T2/Assembler/Output Series this capability belongs to. This is a minimal, locally-scoped
 * type instead, mirroring the precedent set by {@code AssembledPrompt} (Capability 5).
 *
 * @param content the raw response text, subject to {@link OutputController}'s whole-response length
 *     enforcement (HD-CG-P0.6-03)
 * @param fields the structured, named output fields (e.g. {@code answer}, {@code confidence},
 *     {@code source} per {@code architecture.md} §12.3's Factual row) {@link OutputController}
 *     validates against an {@link OutputSchema}'s required fields
 */
public record CandidateOutput(String content, Map<String, Object> fields) {

    public CandidateOutput {
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(fields, "fields");
        fields = Map.copyOf(fields);
    }
}
