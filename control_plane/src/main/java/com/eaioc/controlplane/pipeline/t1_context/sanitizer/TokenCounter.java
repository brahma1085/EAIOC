package com.eaioc.controlplane.pipeline.t1_context.sanitizer;

/**
 * The T1.1 counting abstraction (interfaces.md §44.3: "The counter is replaceable behind the T1.1
 * counting abstraction").
 *
 * <p>Contract (HD-CG-P0.3-18): if counting fails, the implementation throws. It must never return a
 * fabricated, {@code 0}, stale or estimated count, and no fallback counter may be substituted.
 */
public interface TokenCounter {

    /** The identity carried on every count this counter produces. */
    TokenCounterIdentity identity();

    /** Deterministic token count of {@code text}. Throws if the count cannot be produced. */
    int countTokens(String text);
}
