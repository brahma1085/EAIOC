package com.eaioc.controlplane.pipeline.t1_context.sanitizer.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;
import com.eaioc.controlplane.core.schemas.LatencyRequirements;
import com.eaioc.controlplane.core.schemas.QualityRequirements;
import com.eaioc.controlplane.core.schemas.RequestType;
import com.eaioc.controlplane.core.schemas.SecurityClassification;
import com.eaioc.controlplane.pipeline.t1_context.sanitizer.Sanitizer;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * EXE-P0.3 Sub-phase C (Integration), realized as the C row of execution-plan.md §18.6: the T1.1
 * Sanitizer is consumable through its public API, demonstrated by a test-tree stub downstream
 * consumer in a separate package. This is NOT pipeline wiring; EC-114 continuation and Capability 5
 * are out of scope.
 *
 * <p>Failure-path cell of the C row is N/A. Counter and normalization failure cannot be injected
 * through the public surface, so those semantics are covered by SanitizerTest (EXE-P0.3.B), not here.
 *
 * <p>Only non-null, non-batch input is used. The null-input response is SOURCE-GAP-EXECPLAN-18 and
 * is not encoded here.
 *
 * <p>The verbatim-output and equal-count assertions are properties of the P0 D0 no-op realization
 * (HD-CG-P0.3-16, plan §18.6 B row), not guarantees of the frozen INTF-072 contract. A future authorized
 * transform must revisit them.
 */
class SanitizerIntegrationTest {

    @Test
    void consumerReceivesSanitizedInputVerbatimThroughPublicApi() {
        String input = "  def f():\n\t\treturn 'x'   \n\né 🙂 https://e.example/a?b=1  ";
        StubDownstreamConsumer consumer = new StubDownstreamConsumer(new Sanitizer());

        StubDownstreamConsumer.Consumed consumed = consumer.consume(request(input));

        assertEquals(input, consumed.text());
    }

    @Test
    void consumerReceivesEqualCountsUnderNoOpNormalization() {
        StubDownstreamConsumer consumer = new StubDownstreamConsumer(new Sanitizer());

        StubDownstreamConsumer.Consumed consumed = consumer.consume(request("Count me the same way twice."));

        // Valid only under the D0 no-op normalization (HD-CG-P0.3-16): both counts are over the same text.
        // If a transform is ever authorized, this assertion must be revisited, not silently relaxed.
        assertEquals(consumed.tokensBefore(), consumed.tokensAfter());
        assertTrue(consumed.tokensBefore() > 0);
    }

    @Test
    void consumerReceivesTheDocumentedCounterIdentity() {
        StubDownstreamConsumer consumer = new StubDownstreamConsumer(new Sanitizer());

        StubDownstreamConsumer.Consumed consumed = consumer.consume(request("identity"));

        assertEquals("JTOKKIT_CL100K_BASE", consumed.counterId());
        assertEquals("1.1.0", consumed.counterVersion());
    }

    @Test
    void consumerGetsTheSameResultForRepeatedCallsOnOneSanitizer() {
        StubDownstreamConsumer consumer = new StubDownstreamConsumer(new Sanitizer());
        String input = "repeatable through the public seam";

        StubDownstreamConsumer.Consumed first = consumer.consume(request(input));
        StubDownstreamConsumer.Consumed second = consumer.consume(request(input));
        StubDownstreamConsumer.Consumed freshInstance =
            new StubDownstreamConsumer(new Sanitizer()).consume(request(input));

        assertEquals(first, second);
        assertEquals(first, freshInstance);
    }

    /** TEST FIXTURE: a schema-valid, non-batch request carrying the given user input. */
    private static ControlPlaneRequest request(String userInput) {
        return new ControlPlaneRequest(
            "1.0.0", "req-t11-c", "corr-t11-c", "tenant-t11-c", "org-t11-c",
            "app-t11-c", null, null, null, "task-t11-c", null, 0,
            RequestType.GENERIC_LLM,
            userInput, null, null, null,
            new QualityRequirements(0.9, QualityRequirements.QualityTier.STANDARD, false, true),
            new LatencyRequirements(2000, 500, true),
            null,
            new SecurityClassification(SecurityClassification.Level.INTERNAL, false, null, null),
            null, null,
            false,
            Map.of(), Instant.parse("2026-10-07T00:00:00Z"), null);
    }
}
