package com.eaioc.controlplane.pipeline.t1_context.sanitizer;

import java.util.Objects;

/**
 * Identifies the deterministic token counter that produced both {@code token_count_pre} and
 * {@code token_count_post} (interfaces.md §44.2 {@code TokenCounterIdentity}; HD-CG-P0.3-09).
 *
 * <p>This is the EAIOC P0 local estimator for T1.1 only. It is NOT the provider billing tokenizer.
 */
public record TokenCounterIdentity(String counterId, String counterVersion) {

    /** The approved P0 counter identity (HD-CG-P0.3-09). */
    public static final TokenCounterIdentity JTOKKIT_CL100K_BASE_1_1_0 =
        new TokenCounterIdentity("JTOKKIT_CL100K_BASE", "1.1.0");

    public TokenCounterIdentity {
        Objects.requireNonNull(counterId, "counterId");
        Objects.requireNonNull(counterVersion, "counterVersion");
    }
}
