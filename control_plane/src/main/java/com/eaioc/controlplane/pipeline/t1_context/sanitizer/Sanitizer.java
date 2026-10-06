package com.eaioc.controlplane.pipeline.t1_context.sanitizer;

import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * T1.1 Sanitizer, Capability 3 Sub-phases B and D (EXE-P0.3.B, EXE-P0.3.D): safe no-op normalization with deterministic
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
        boolean fallbackApplied = false;
        try {
            sanitized = normalizer.normalize(input);
            if (sanitized == null) {
                throw new IllegalStateException("normalizer returned null");
            }
        } catch (RuntimeException normalizationFailure) {
            // RESTORE_ORIGINAL (interfaces.md §44.5): the original input verbatim, one WARNING.
            logRestoreOriginal(request, normalizationFailure);
            sanitized = input;
            fallbackApplied = true;
        }

        // Post-count over sanitized_input with the SAME counter (interfaces.md §44.3 rule 1).
        // A counter failure here propagates; it is not a second RESTORE_ORIGINAL (HD-CG-P0.3-18).
        int tokenCountPost = counter.countTokens(sanitized);

        // One line per call (plan §18.6 D row, human decision 2026-10-07): a fallback call is already
        // covered by its WARNING, so the success line is emitted only when no fallback was applied.
        // It follows the post-count, so a counter failure never logs a success for a call that returns nothing.
        if (!fallbackApplied) {
            logCompleted(request, tokenCountPre, tokenCountPost, counter.identity());
        }

        return new SanitizerOutput(sanitized, tokenCountPre, tokenCountPost, counter.identity());
    }

    /**
     * Emits exactly one structured INFO line for a successful sanitization call (plan §18.6 D row).
     * Carries the token counts under the §27.1 field names {@code tokens.raw_input} and
     * {@code tokens.sanitized}, and the counter identity. Never carries user input text.
     *
     * <p>{@code SANITIZER_COMPLETED} is a local event name chosen for this unit. No source document
     * defines a T1.1 success event; precedent is {@code BASELINE_RUN_COMPLETED}.
     *
     * <p>{@code tokens.raw_input} and {@code tokens.sanitized} are INFERRED mappings from the §11.1
     * labels (token_count_pre, token_count_post). {@code tokens.removed_by_sanitizer} is intentionally
     * not emitted: its derivation is not defined in the corpus.
     *
     * <p>The {@code tokens.*} names are logged as <b>fields only, not emitted as metrics</b>; no metric
     * is created here (interfaces.md §44.5).
     *
     * <p><b>IMPLEMENTATION GAP, not claimed as realized:</b> {@code conventions.md} §17.1 requires every
     * component invocation to emit an {@code OptimizationSpan}. No span exists in P0. {@code span_id} is a
     * fresh per-line UUID that links to no span, the same pattern the Capability 1 and 2 log lines use.
     */
    private static void logCompleted(
            ControlPlaneRequest request, int tokenCountPre, int tokenCountPost, TokenCounterIdentity counter) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("tokens.raw_input", tokenCountPre);
        fields.put("tokens.sanitized", tokenCountPost);
        // conventions.md §17.2: INFO for a normal decision.
        LOG.info(
            "event_type=SANITIZER_COMPLETED component_id=T1.1-SANITIZER "
                + "message=\"sanitization completed\" "
                + "tenant_id={} request_id={} correlation_id={} span_id={} parent_span_id=null "
                + "counter_id={} counter_version={} fields={}",
            request.tenantId(),
            request.requestId(),
            Objects.requireNonNullElse(request.correlationId(), request.requestId()),
            UUID.randomUUID(),
            counter.counterId(),
            counter.counterVersion(),
            fields);
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
