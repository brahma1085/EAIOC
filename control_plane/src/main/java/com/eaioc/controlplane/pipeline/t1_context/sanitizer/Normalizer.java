package com.eaioc.controlplane.pipeline.t1_context.sanitizer;

/**
 * The T1.1 normalization operation. A failure (a thrown exception or a {@code null} result) makes
 * {@link Sanitizer} apply RESTORE_ORIGINAL (interfaces.md §44.5).
 *
 * <p>P0 realization is a safe no-op (HD-CG-P0.3-16, D0): {@link #NO_OP} returns its input unchanged.
 * No transform is implemented; the five §11.1 operations are deferred (SOURCE-GAP-EXECPLAN-21 to -26).
 */
@FunctionalInterface
interface Normalizer {

    /** The P0 no-op normalization (D0). */
    Normalizer NO_OP = input -> input;

    String normalize(String input);
}
