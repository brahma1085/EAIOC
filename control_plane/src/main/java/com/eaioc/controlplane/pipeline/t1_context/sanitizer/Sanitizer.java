package com.eaioc.controlplane.pipeline.t1_context.sanitizer;

import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * T1.1 Sanitizer, Capability 3 Sub-phase B (EXE-P0.3.B): safe no-op normalization with deterministic
 * token counting. Governed by interfaces.md §44 (INTF-072) and the human decisions HD-CG-P0.3-05 to
 * -19.
 *
 * <p>Three distinct failure classes (interfaces.md §44.5):
 * <ol>
 *   <li><b>Normalization failure</b> → RESTORE_ORIGINAL: {@code sanitized_input} is the original
 *       {@code user_input} verbatim, and exactly one structured WARNING with
 *       {@code event_type = SANITIZER_FALLBACK_RESTORE_ORIGINAL} is logged.</li>
 *   <li><b>Token-counter failure</b> → no {@link SanitizerOutput}: the counter's exception propagates
 *       unchanged. No fabricated, {@code 0}, {@code null}, stale or fallback count is produced, and
 *       the failure is never converted to RESTORE_ORIGINAL (HD-CG-P0.3-18).</li>
 *   <li><b>Pipeline handling of the failed optional stage</b> is not this class's job: it is EC-114
 *       stage-level fallback, which continues with the pre-stage representation (HD-CG-P0.3-19).
 *       "Propagates" never means rejecting the request.</li>
 * </ol>
 *
 * <p>Order of operations: {@code token_count_pre} over the input, then normalization, then
 * {@code token_count_post} over {@code sanitized_input}. A counter failure on the first count occurs
 * before any normalization and therefore emits no fallback WARNING. A counter failure on the second
 * count occurs after a normalization fallback has already logged its WARNING; the WARNING is not
 * retracted, and no output is returned.
 *
 * <p>Precondition: {@code request.userInput()} must be non-null. T1.1 is not invoked for a null input
 * (HD-CG-P0.3-10); a null here is a caller error, reported as a programming-error guard rather than a
 * T1.1 contract error code, which the contract does not define.
 */
public final class Sanitizer {

    private static final Logger LOG = LoggerFactory.getLogger(Sanitizer.class);

    private final TokenCounter counter;
    private final Normalizer normalizer;

    /** The P0 realization: JTokkit {@code CL100K_BASE} counting and no-op normalization. */
    public Sanitizer() {
        this(new JTokkitTokenCounter(), Normalizer.NO_OP);
    }

    /**
     * Injection point for tests; production code uses the no-arg constructor. Package-private so that
     * the normalization seam is not a public extension point (no transform is authorized in P0).
     */
    Sanitizer(TokenCounter counter, Normalizer normalizer) {
        this.counter = Objects.requireNonNull(counter, "counter");
        this.normalizer = Objects.requireNonNull(normalizer, "normalizer");
    }

    public SanitizerOutput sanitize(ControlPlaneRequest request) {
        Objects.requireNonNull(request, "request");
        String input = Objects.requireNonNull(request.userInput(), "request.userInput");

        // Pre-count first. A counter failure here propagates before any normalization happens.
        int tokenCountPre = counter.countTokens(input);

        String sanitized;
        try {
            sanitized = normalizer.normalize(input);
            if (sanitized == null) {
                throw new IllegalStateException("normalizer returned null");
            }
        } catch (RuntimeException normalizationFailure) {
            // RESTORE_ORIGINAL (interfaces.md §44.5): the original input verbatim, one WARNING.
            logRestoreOriginal(request, normalizationFailure);
            sanitized = input;
        }

        // Post-count over sanitized_input with the SAME counter (interfaces.md §44.3 rule 1).
        // A counter failure here propagates; it is not a second RESTORE_ORIGINAL (HD-CG-P0.3-18).
        int tokenCountPost = counter.countTokens(sanitized);

        return new SanitizerOutput(sanitized, tokenCountPre, tokenCountPost, counter.identity());
    }

    /**
     * Emits exactly one structured WARNING for a RESTORE_ORIGINAL fallback (interfaces.md §44.5,
     * HD-CG-P0.3-15). Same line shape as the other EAIOC structured log lines.
     */
    private static void logRestoreOriginal(ControlPlaneRequest request, RuntimeException cause) {
        // conventions.md §17.2: WARNING for a fallback outcome.
        // correlation_id: the caller-supplied value; defaults to request_id only when absent
        // (conventions.md §3.2).
        LOG.warn(
            "event_type=SANITIZER_FALLBACK_RESTORE_ORIGINAL component_id=T1.1-SANITIZER "
                + "message=\"normalization failed; original input restored\" "
                + "tenant_id={} request_id={} correlation_id={} span_id={} parent_span_id=null "
                + "reason=normalization_failure cause={} fields={}",
            request.tenantId(),
            request.requestId(),
            Objects.requireNonNullElse(request.correlationId(), request.requestId()),
            UUID.randomUUID(),
            cause.getClass().getName(),
            "{}");
    }
}
