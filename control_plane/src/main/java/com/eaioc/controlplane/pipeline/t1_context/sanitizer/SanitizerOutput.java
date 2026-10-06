package com.eaioc.controlplane.pipeline.t1_context.sanitizer;

import java.util.Objects;

/**
 * T1.1 stage output (interfaces.md §44.2, INTF-072). Holds no verified, nullable or error field:
 * a counter failure produces no {@code SanitizerOutput} at all (HD-CG-P0.3-18).
 */
public record SanitizerOutput(
    String sanitizedInput,
    int tokenCountPre,
    int tokenCountPost,
    TokenCounterIdentity counter) {

    public SanitizerOutput {
        Objects.requireNonNull(sanitizedInput, "sanitizedInput");
        Objects.requireNonNull(counter, "counter");
    }
}
