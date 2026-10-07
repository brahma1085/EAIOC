package com.eaioc.controlplane.output_controls;

/**
 * Thrown when a {@link CandidateOutput} does not conform to an {@link OutputSchema}'s required
 * fields.
 *
 * <p><b>HD-CG-P0.6-04 (2026-10-08):</b> a schema-fail/malformed-response result raises this
 * unit-local exception, never a new {@code core.errors.ControlPlaneError} code — mirroring the
 * Sanitizer/Baseline-Harness precedent ({@code NoBaselineMeasurementException}) of not stretching
 * the {@code ControlPlaneError} {@code VALIDATION} series' documented scope ("Request schema
 * invalid" — request-side, per {@code interfaces.md} §25.1's own table — not response-side).
 * {@link OutputController} never silently passes a malformed response through as valid; this
 * exception is the rejection.
 */
public final class MalformedOutputException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MalformedOutputException(String message) {
        super(message);
    }
}
