package com.eaioc.controlplane.pipeline.t1_context.sanitizer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;
import com.eaioc.controlplane.core.schemas.LatencyRequirements;
import com.eaioc.controlplane.core.schemas.QualityRequirements;
import com.eaioc.controlplane.core.schemas.RequestType;
import com.eaioc.controlplane.core.schemas.SecurityClassification;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * EXE-P0.3.B and EXE-P0.3.D tests for the T1.1 Sanitizer (interfaces.md §44; HD-CG-P0.3-14 to -19).
 *
 * <p>Covers: byte-equal no-op output; equal pre/post counts from the same counter; counter identity on
 * every output; counter failure propagates with no output and is never RESTORE_ORIGINAL; normalization
 * failure gives RESTORE_ORIGINAL with exactly one structured WARNING; the null-input precondition guard.
 */
class SanitizerTest {

    private static final String FALLBACK_EVENT = "event_type=SANITIZER_FALLBACK_RESTORE_ORIGINAL";

    private final JTokkitTokenCounter jtokkit = new JTokkitTokenCounter();
    private ListAppender<ILoggingEvent> logs;
    private Logger sanitizerLog;

    @BeforeEach
    void attachLogCapture() {
        sanitizerLog = (Logger) LoggerFactory.getLogger(Sanitizer.class);
        logs = new ListAppender<>();
        logs.start();
        sanitizerLog.addAppender(logs);
    }

    @AfterEach
    void detachLogCapture() {
        sanitizerLog.detachAppender(logs);
    }

    @Test
    void sanitizedInputIsByteEqualToUserInput() {
        String input = "  def f():\n\t\treturn 'x'   \n\né́ 🙂 https://e.example/a?b=1 {\"k\": [1, 2]}  ";

        SanitizerOutput out = new Sanitizer().sanitize(request(input));

        assertEquals(input, out.sanitizedInput());
        assertArrayEquals(
            input.getBytes(StandardCharsets.UTF_8), out.sanitizedInput().getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void preAndPostCountsAreEqualAndMatchTheCounter() {
        String input = "Hello, EAIOC token estimation.";

        SanitizerOutput out = new Sanitizer().sanitize(request(input));

        assertEquals(out.tokenCountPre(), out.tokenCountPost());
        assertEquals(jtokkit.countTokens(input), out.tokenCountPre());
        assertTrue(out.tokenCountPre() > 0);
    }

    /**
     * Golden value: the CL100K_BASE count of {@link #GOLDEN_INPUT}, pinned from the approved encoding.
     * If the encoding is swapped, this fails even though the identity constant might not change.
     */
    static final String GOLDEN_INPUT = "The quick brown fox jumps over the lazy dog. EAIOC T1.1.";
    static final int GOLDEN_COUNT = 17;

    @Test
    void goldenCountPinsTheCl100kEncoding() {
        assertEquals(GOLDEN_COUNT, jtokkit.countTokens(GOLDEN_INPUT));
    }

    @Test
    void logLineCarriesCallerCorrelationTenantAndRequestIdentifiers() {
        Normalizer failing = in -> {
            throw new IllegalArgumentException("normalizer broke");
        };

        new Sanitizer(jtokkit, failing).sanitize(request("identifiers"));

        String line = logs.list.stream()
            .filter(e -> e.getFormattedMessage().contains(FALLBACK_EVENT))
            .findFirst()
            .orElseThrow()
            .getFormattedMessage();
        assertTrue(line.contains("correlation_id=corr-t11"), line);
        assertTrue(line.contains("tenant_id=tenant-t11"), line);
        assertTrue(line.contains("request_id=req-t11"), line);
        assertTrue(line.contains("component_id=T1.1-SANITIZER"), line);
        assertTrue(line.contains("message=\"normalization failed; original input restored\""), line);
    }

    @Test
    void noNormalizationRunsWhenThePreCountFails() {
        AtomicInteger normalizerCalls = new AtomicInteger();
        Normalizer counting = in -> {
            normalizerCalls.incrementAndGet();
            return in;
        };

        assertThrows(IllegalStateException.class,
            () -> new Sanitizer(new StubCounter(1), counting).sanitize(request("x")));
        assertEquals(0, normalizerCalls.get());
    }

    @Test
    void sharedCounterGivesSameCountsUnderConcurrentUse() throws Exception {
        // Smoke evidence only: JTokkit Encoding thread-safety is NOT VERIFIED (see JTokkitTokenCounter).
        String input = "concurrent counting smoke input with some words";
        int expected = jtokkit.countTokens(input);
        java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(8);
        try {
            java.util.List<java.util.concurrent.Future<Integer>> results = new java.util.ArrayList<>();
            for (int i = 0; i < 200; i++) {
                results.add(pool.submit(() -> new JTokkitTokenCounter().countTokens(input)));
            }
            for (java.util.concurrent.Future<Integer> f : results) {
                assertEquals(expected, f.get());
            }
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void counterIdentityIsCarriedOnTheOutput() {
        SanitizerOutput out = new Sanitizer().sanitize(request("identity check"));

        assertEquals("JTOKKIT_CL100K_BASE", out.counter().counterId());
        assertEquals("1.1.0", out.counter().counterVersion());
        assertEquals(TokenCounterIdentity.JTOKKIT_CL100K_BASE_1_1_0, out.counter());
    }

    @Test
    void countingIsDeterministic() {
        Sanitizer sanitizer = new Sanitizer();

        SanitizerOutput first = sanitizer.sanitize(request("repeatable input"));
        SanitizerOutput second = sanitizer.sanitize(request("repeatable input"));

        assertEquals(first, second);
    }

    @Test
    void counterFailureOnPreCountPropagatesWithNoOutput() {
        TokenCounter failing = new StubCounter(1);

        assertThrows(IllegalStateException.class,
            () -> new Sanitizer(failing, Normalizer.NO_OP).sanitize(request("x")));
        assertEquals(0, fallbackWarnings());
    }

    @Test
    void counterFailureOnPostCountPropagatesAndIsNotRestoreOriginal() {
        TokenCounter failsSecondCall = new StubCounter(2);

        assertThrows(IllegalStateException.class,
            () -> new Sanitizer(failsSecondCall, Normalizer.NO_OP).sanitize(request("x")));
        // No normalization failed, so no RESTORE_ORIGINAL WARNING: a counter failure is not a fallback.
        assertEquals(0, fallbackWarnings());
    }

    @Test
    void normalizationFailureRestoresOriginalWithExactlyOneWarning() {
        String input = "  keep   this\n  verbatim  ";
        Normalizer failing = in -> {
            throw new IllegalArgumentException("normalizer broke");
        };

        SanitizerOutput out = new Sanitizer(jtokkit, failing).sanitize(request(input));

        assertEquals(input, out.sanitizedInput());
        assertEquals(jtokkit.countTokens(input), out.tokenCountPre());
        assertEquals(out.tokenCountPre(), out.tokenCountPost());
        assertEquals(1, fallbackWarnings());
        ILoggingEvent warning = logs.list.stream()
            .filter(e -> e.getFormattedMessage().contains(FALLBACK_EVENT))
            .findFirst()
            .orElseThrow();
        assertEquals(Level.WARN, warning.getLevel());
    }

    @Test
    void normalizerReturningNullIsTreatedAsNormalizationFailure() {
        String input = "null result case";

        SanitizerOutput out = new Sanitizer(jtokkit, in -> null).sanitize(request(input));

        assertEquals(input, out.sanitizedInput());
        assertEquals(1, fallbackWarnings());
    }

    @Test
    void normalizationFailureThenCounterFailureStillPropagatesWithNoOutput() {
        TokenCounter failsOnPost = new StubCounter(2);
        Normalizer failing = in -> {
            throw new IllegalArgumentException("normalizer broke");
        };

        assertThrows(IllegalStateException.class,
            () -> new Sanitizer(failsOnPost, failing).sanitize(request("x")));
        // Ordering (documented on Sanitizer): the normalization WARNING was already emitted before the
        // post-count failed. It is not retracted, and no output is returned.
        assertEquals(1, fallbackWarnings());
    }

    @Test
    void nullUserInputIsRejectedAsCallerPrecondition() {
        ControlPlaneRequest batch = request(null, RequestType.BATCH);

        assertThrows(NullPointerException.class, () -> new Sanitizer().sanitize(batch));
    }

    // EXE-P0.3.D: one structured line per call (plan §18.6 D row; human decision 2026-10-07).

    @Test
    void successEmitsExactlyOneCompletedLineWithTokenFieldsAndCounterIdentity() {
        String input = "observable input text";

        SanitizerOutput out = new Sanitizer().sanitize(request(input));

        List<String> completed = completedLines();
        assertEquals(1, completed.size());
        String line = completed.get(0);
        assertTrue(line.contains("tokens.raw_input=" + out.tokenCountPre()), line);
        assertTrue(line.contains("tokens.sanitized=" + out.tokenCountPost()), line);
        assertTrue(line.contains("counter_id=JTOKKIT_CL100K_BASE"), line);
        assertTrue(line.contains("counter_version=1.1.0"), line);
        assertTrue(line.contains("component_id=T1.1-SANITIZER"), line);
        assertTrue(line.contains("tenant_id=tenant-t11"), line);
        assertTrue(line.contains("request_id=req-t11"), line);
        assertTrue(line.contains("correlation_id=corr-t11"), line);
        assertTrue(line.contains("span_id="), line);
        assertTrue(line.contains("parent_span_id=null"), line);
        assertEquals(Level.INFO, completedEvent().getLevel());
    }

    @Test
    void completedLineNeverCarriesUserInputText() {
        String secret = "sensitive-user-text-do-not-log-4242";

        new Sanitizer().sanitize(request(secret));

        assertTrue(completedLines().stream().noneMatch(l -> l.contains(secret)));
    }

    @Test
    void completedLineIsNotEmittedOnNormalizationFallback() {
        Normalizer failing = in -> {
            throw new IllegalArgumentException("normalizer broke");
        };

        new Sanitizer(jtokkit, failing).sanitize(request("fallback input"));

        assertEquals(0, completedLines().size());
        assertEquals(1, fallbackWarnings());
    }

    @Test
    void completedLineIsNotEmittedWhenPostCountFailsAfterFallback() {
        Normalizer failing = in -> {
            throw new IllegalArgumentException("normalizer broke");
        };

        assertThrows(IllegalStateException.class,
            () -> new Sanitizer(new StubCounter(2), failing).sanitize(request("x")));
        assertEquals(0, completedLines().size());
        // The fallback WARNING from before the failed post-count is retained (interfaces.md §44.4).
        assertEquals(1, fallbackWarnings());
    }

    @Test
    void removedBySanitizerIsNeverEmitted() {
        new Sanitizer().sanitize(request("no removed-count field expected"));

        assertTrue(t11Lines().stream().noneMatch(l -> l.contains("tokens.removed_by_sanitizer")));
    }

    @Test
    void counterFailureEmitsNoT11LinesAtAll() {
        assertThrows(IllegalStateException.class,
            () -> new Sanitizer(new StubCounter(1), Normalizer.NO_OP).sanitize(request("x")));
        assertThrows(IllegalStateException.class,
            () -> new Sanitizer(new StubCounter(2), Normalizer.NO_OP).sanitize(request("x")));

        assertEquals(0, t11Lines().size());
    }

    private List<ILoggingEvent> completedEvents() {
        return logs.list.stream()
            .filter(e -> e.getFormattedMessage().contains("event_type=SANITIZER_COMPLETED"))
            .toList();
    }

    private ILoggingEvent completedEvent() {
        return completedEvents().get(0);
    }

    private List<String> completedLines() {
        return completedEvents().stream().map(ILoggingEvent::getFormattedMessage).toList();
    }

    private List<String> t11Lines() {
        return logs.list.stream()
            .map(ILoggingEvent::getFormattedMessage)
            .filter(m -> m.contains("component_id=T1.1-SANITIZER"))
            .toList();
    }

    private long fallbackWarnings() {
        return logs.list.stream()
            .filter(e -> e.getLevel() == Level.WARN)
            .filter(e -> e.getFormattedMessage().contains(FALLBACK_EVENT))
            .count();
    }

    private static ControlPlaneRequest request(String userInput) {
        return request(userInput, RequestType.GENERIC_LLM);
    }

    /** TEST FIXTURE: a schema-valid request carrying the given user input (mirrors BaselineTestFixtures). */
    private static ControlPlaneRequest request(String userInput, RequestType type) {
        return new ControlPlaneRequest(
            "1.0.0", "req-t11", "corr-t11", "tenant-t11", "org-t11",
            "app-t11", null, null, null, "task-t11", null, 0,
            type,
            userInput, null, null, null,
            new QualityRequirements(0.9, QualityRequirements.QualityTier.STANDARD, false, true),
            new LatencyRequirements(2000, 500, true),
            null,
            new SecurityClassification(SecurityClassification.Level.INTERNAL, false, null, null),
            null, null,
            false,
            Map.of(), Instant.parse("2026-10-06T00:00:00Z"), null);
    }

    /** A counter that returns 1 for every call except the {@code failOnCall}-th, which throws. */
    private static final class StubCounter implements TokenCounter {

        private final int failOnCall;
        private final AtomicInteger calls = new AtomicInteger();

        StubCounter(int failOnCall) {
            this.failOnCall = failOnCall;
        }

        @Override
        public TokenCounterIdentity identity() {
            return TokenCounterIdentity.JTOKKIT_CL100K_BASE_1_1_0;
        }

        @Override
        public int countTokens(String text) {
            if (calls.incrementAndGet() == failOnCall) {
                throw new IllegalStateException("counter failed");
            }
            return 1;
        }
    }
}
