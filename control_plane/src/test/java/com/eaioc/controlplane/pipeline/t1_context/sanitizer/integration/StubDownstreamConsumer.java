package com.eaioc.controlplane.pipeline.t1_context.sanitizer.integration;

import com.eaioc.controlplane.pipeline.t1_context.sanitizer.Sanitizer;
import com.eaioc.controlplane.pipeline.t1_context.sanitizer.SanitizerOutput;
import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;

/**
 * TEST STUB: a minimal downstream consumer of the T1.1 public API, standing in for the first
 * T1-series consumer (Prompt Assembler, Capability 5). It is test-tree code only.
 *
 * <p>It uses only the public surface: {@link Sanitizer#sanitize(ControlPlaneRequest)} and the
 * {@link SanitizerOutput} accessors. It reads the documented fields (interfaces.md §44.2) and does
 * not inspect anything else. It is deliberately in a separate package, so it cannot reach
 * package-private seams.
 *
 * <p>This is a consumer stub, not pipeline composition. It does not implement EC-114 continuation,
 * CIS, or Capability 5 behaviour (HD-CG-P0.3-19).
 */
public final class StubDownstreamConsumer {

    /** The subset of {@link SanitizerOutput} this consumer reads. */
    public record Consumed(String text, int tokensBefore, int tokensAfter, String counterId, String counterVersion) {
    }

    private final Sanitizer sanitizer;

    public StubDownstreamConsumer(Sanitizer sanitizer) {
        this.sanitizer = sanitizer;
    }

    public Consumed consume(ControlPlaneRequest request) {
        SanitizerOutput out = sanitizer.sanitize(request);
        return new Consumed(
            out.sanitizedInput(),
            out.tokenCountPre(),
            out.tokenCountPost(),
            out.counter().counterId(),
            out.counter().counterVersion());
    }
}
