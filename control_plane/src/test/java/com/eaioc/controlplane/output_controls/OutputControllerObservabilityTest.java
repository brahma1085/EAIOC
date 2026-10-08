package com.eaioc.controlplane.output_controls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.eaioc.controlplane.core.schemas.BudgetConstraints;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * {@code EXE-P0.6.D} observability tests (execution-plan.md §18.9 D row, human decisions
 * HD-CG-P0.6-06a/b/d): exactly one structured log line per {@link OutputController#enforce} call —
 * {@code OUTPUT_VALIDATED} (INFO) when unmodified, {@code OUTPUT_TRUNCATED} (WARNING) when
 * truncated — and the never-log-response-content rule.
 */
class OutputControllerObservabilityTest {

    private static final String REQUEST_ID = "request-c6d";
    private static final String TENANT_ID = "tenant-c6d";

    private final OutputController controller = new OutputController();

    private ListAppender<ILoggingEvent> logs;
    private Logger controllerLog;

    @BeforeEach
    void attachLogCapture() {
        controllerLog = (Logger) LoggerFactory.getLogger(OutputController.class);
        logs = new ListAppender<>();
        logs.start();
        controllerLog.addAppender(logs);
    }

    @AfterEach
    void detachLogCapture() {
        controllerLog.detachAppender(logs);
    }

    private static OutputSchema factualSchema() {
        return new OutputSchema(List.of(
            new OutputSchema.RequiredField("answer", String.class),
            new OutputSchema.RequiredField("confidence", Double.class)));
    }

    @Test
    void successfulValidationEmitsExactlyOneInfoLine() {
        CandidateOutput candidate =
            new CandidateOutput("Paris is the capital of France.",
                Map.of("answer", "Paris", "confidence", 0.97));

        ValidatedOutput result =
            controller.enforce(REQUEST_ID, TENANT_ID, candidate, factualSchema(), null);

        List<String> lines = linesContaining("event_type=OUTPUT_VALIDATED");
        assertEquals(1, lines.size());
        assertEquals(1, allLines().size());
        assertEquals(Level.INFO, logs.list.get(0).getLevel());
        String line = lines.get(0);
        assertTrue(line.contains("component_id=OUTPUT-CONTROLLER"), line);
        assertTrue(line.contains("tenant_id=" + TENANT_ID), line);
        assertTrue(line.contains("request_id=" + REQUEST_ID), line);
        assertTrue(line.contains("correlation_id=" + REQUEST_ID), line);
        assertTrue(line.contains("tokens.optimized_output=" + result.finalTokenCount()), line);
        assertTrue(line.contains("tokens.raw_output=" + result.finalTokenCount()), line);
    }

    @Test
    void truncatedValidationEmitsExactlyOneWarningLine() {
        String longContent = "word ".repeat(500);
        CandidateOutput candidate =
            new CandidateOutput(longContent, Map.of("answer", longContent, "confidence", 0.5));
        BudgetConstraints budget = new BudgetConstraints(null, 5, null, null);

        ValidatedOutput result =
            controller.enforce(REQUEST_ID, TENANT_ID, candidate, factualSchema(), budget);

        List<String> lines = linesContaining("event_type=OUTPUT_TRUNCATED");
        assertEquals(1, lines.size());
        assertEquals(1, allLines().size());
        assertEquals(Level.WARN, logs.list.get(0).getLevel());
        String line = lines.get(0);
        assertTrue(line.contains("component_id=OUTPUT-CONTROLLER"), line);
        assertTrue(line.contains("tokens.optimized_output=" + result.finalTokenCount()), line);

        // raw_output (pre-truncation) must exceed optimized_output (post-truncation) when truncated.
        java.util.regex.Matcher matcher =
            java.util.regex.Pattern.compile("tokens\\.raw_output=(\\d+)").matcher(line);
        assertTrue(matcher.find(), line);
        int rawOutput = Integer.parseInt(matcher.group(1));
        assertTrue(rawOutput > result.finalTokenCount(), line);
    }

    @Test
    void nonPositiveBudgetTruncationEmitsExactlyOneWarningLineWithCorrectCounts() {
        // Reviewer finding (2026-10-08): the maxOutputTokens <= 0 branch computes rawOutputTokens
        // via a different code path than the positive-budget encode branch above, and was
        // previously untested for its logging output specifically (the functional outcome was
        // already covered by OutputControllerTest's own zero/negative-budget tests).
        CandidateOutput candidate =
            new CandidateOutput("Paris", Map.of("answer", "Paris", "confidence", 0.9));
        BudgetConstraints zeroBudget = new BudgetConstraints(null, 0, null, null);

        ValidatedOutput result =
            controller.enforce(REQUEST_ID, TENANT_ID, candidate, factualSchema(), zeroBudget);

        assertTrue(result.truncated());
        List<String> lines = linesContaining("event_type=OUTPUT_TRUNCATED");
        assertEquals(1, lines.size());
        assertEquals(1, allLines().size());
        assertEquals(Level.WARN, logs.list.get(0).getLevel());
        String line = lines.get(0);
        assertTrue(line.contains("tokens.optimized_output=0"), line);

        java.util.regex.Matcher matcher =
            java.util.regex.Pattern.compile("tokens\\.raw_output=(\\d+)").matcher(line);
        assertTrue(matcher.find(), line);
        int rawOutput = Integer.parseInt(matcher.group(1));
        assertTrue(rawOutput > 0, line);
    }

    @Test
    void noPolicyFallbackPathIsNotApplicableHereButMalformedResponseEmitsNoLogLine() {
        // Mirrors Sanitizer/PromptAssembler/PolicyBudgetEnforcer's own no-log-on-thrown-exception
        // precedent: a schema-fail result throws before any log line is emitted.
        CandidateOutput malformed = new CandidateOutput("incomplete", Map.of());

        org.junit.jupiter.api.Assertions.assertThrows(MalformedOutputException.class,
            () -> controller.enforce(REQUEST_ID, TENANT_ID, malformed, factualSchema(), null));

        assertTrue(allLines().isEmpty());
    }

    @Test
    void logLineNeverCarriesResponseContentOrFields() {
        String secret = "do-not-log-this-response-content-8675309";
        CandidateOutput candidate =
            new CandidateOutput(secret, Map.of("answer", secret, "confidence", 0.42));

        controller.enforce(REQUEST_ID, TENANT_ID, candidate, factualSchema(), null);

        assertFalse(allLines().stream().anyMatch(l -> l.contains(secret)));
    }

    @Test
    void logLineNeverCarriesResponseContentEvenWhenTruncated() {
        String secret = "do-not-log-this-truncated-content-either-1234567890".repeat(20);
        CandidateOutput candidate =
            new CandidateOutput(secret, Map.of("answer", secret, "confidence", 0.42));
        BudgetConstraints budget = new BudgetConstraints(null, 3, null, null);

        controller.enforce(REQUEST_ID, TENANT_ID, candidate, factualSchema(), budget);

        assertFalse(allLines().stream().anyMatch(l -> l.contains("do-not-log-this-truncated")));
    }

    private List<String> allLines() {
        return logs.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
    }

    private List<String> linesContaining(String substring) {
        return allLines().stream().filter(l -> l.contains(substring)).toList();
    }
}
