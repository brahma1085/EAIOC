package com.eaioc.controlplane.output_controls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.eaioc.controlplane.core.schemas.BudgetConstraints;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * {@code EXE-P0.6.B} unit tests (execution-plan.md §18.9 B row): schema-pass case, schema-fail
 * case, length-truncation case, and the mandatory failure-path test (a malformed response is
 * rejected, never silently passed through as valid).
 */
class OutputControllerTest {

    private final OutputController controller = new OutputController();

    private static OutputSchema factualSchema() {
        return new OutputSchema(List.of(
            new OutputSchema.RequiredField("answer", String.class),
            new OutputSchema.RequiredField("confidence", Double.class)));
    }

    // --- schema-pass case ---

    @Test
    void conformantCandidateValidatesAndPassesThroughUnmodifiedWhenNoBudgetIsSet() {
        CandidateOutput candidate =
            new CandidateOutput("Paris is the capital of France.",
                Map.of("answer", "Paris", "confidence", 0.97));

        ValidatedOutput result = controller.enforce(candidate, factualSchema(), null);

        assertEquals("Paris is the capital of France.", result.content());
        assertFalse(result.truncated());
        assertTrue(result.finalTokenCount() > 0);
    }

    @Test
    void conformantCandidateWithinBudgetPassesThroughUnmodified() {
        CandidateOutput candidate =
            new CandidateOutput("Short answer.", Map.of("answer", "Short answer.", "confidence", 0.8));
        BudgetConstraints budget = new BudgetConstraints(null, 1000, null, null);

        ValidatedOutput result = controller.enforce(candidate, factualSchema(), budget);

        assertEquals("Short answer.", result.content());
        assertFalse(result.truncated());
    }

    // --- schema-fail case ---

    @Test
    void missingRequiredFieldThrowsMalformedOutputException() {
        CandidateOutput candidate =
            new CandidateOutput("Paris.", Map.of("answer", "Paris")); // missing "confidence"

        MalformedOutputException thrown = assertThrows(
            MalformedOutputException.class,
            () -> controller.enforce(candidate, factualSchema(), null));

        assertTrue(thrown.getMessage().contains("confidence"));
    }

    @Test
    void wrongTypeOnRequiredFieldThrowsMalformedOutputException() {
        CandidateOutput candidate =
            new CandidateOutput("Paris.", Map.of("answer", "Paris", "confidence", "high")); // String, not Double

        MalformedOutputException thrown = assertThrows(
            MalformedOutputException.class,
            () -> controller.enforce(candidate, factualSchema(), null));

        assertTrue(thrown.getMessage().contains("confidence"));
    }

    // --- length-truncation case ---

    @Test
    void contentExceedingMaxOutputTokensIsTruncatedAndMarkedExplicitly() {
        String longContent = "word ".repeat(500); // far more than a tiny token budget
        CandidateOutput candidate =
            new CandidateOutput(longContent, Map.of("answer", longContent, "confidence", 0.5));
        BudgetConstraints budget = new BudgetConstraints(null, 5, null, null);

        ValidatedOutput result = controller.enforce(candidate, factualSchema(), budget);

        assertTrue(result.truncated());
        assertEquals(5, result.finalTokenCount());
        assertTrue(result.content().length() < longContent.length());
    }

    @Test
    void zeroMaxOutputTokensTruncatesNonEmptyContentToEmptyAndMarksTruncated() {
        // Reviewer finding (2026-10-08): reproduced directly against this project's jtokkit jar,
        // Encoding.encode(text, 0) does not reliably bound output or set isTruncated() -- the
        // zero-budget boundary must be handled explicitly before calling into JTokkit.
        CandidateOutput candidate =
            new CandidateOutput("Paris", Map.of("answer", "Paris", "confidence", 0.9));
        BudgetConstraints zeroBudget = new BudgetConstraints(null, 0, null, null);

        ValidatedOutput result = controller.enforce(candidate, factualSchema(), zeroBudget);

        assertTrue(result.truncated());
        assertEquals("", result.content());
        assertEquals(0, result.finalTokenCount());
    }

    @Test
    void negativeMaxOutputTokensIsTreatedTheSameAsZero() {
        CandidateOutput candidate =
            new CandidateOutput("Paris", Map.of("answer", "Paris", "confidence", 0.9));
        BudgetConstraints negativeBudget = new BudgetConstraints(null, -5, null, null);

        ValidatedOutput result = controller.enforce(candidate, factualSchema(), negativeBudget);

        assertTrue(result.truncated());
        assertEquals("", result.content());
        assertEquals(0, result.finalTokenCount());
    }

    @Test
    void zeroMaxOutputTokensOnAlreadyEmptyContentIsNotMarkedTruncated() {
        CandidateOutput candidate = new CandidateOutput("", Map.of("answer", "", "confidence", 0.0));
        BudgetConstraints zeroBudget = new BudgetConstraints(null, 0, null, null);

        ValidatedOutput result = controller.enforce(candidate, factualSchema(), zeroBudget);

        assertFalse(result.truncated());
        assertEquals("", result.content());
    }

    @Test
    void contentExactlyAtBudgetIsNotMarkedTruncated() {
        CandidateOutput candidate =
            new CandidateOutput("Paris", Map.of("answer", "Paris", "confidence", 0.9));
        int exactTokenCount = new OutputController()
            .enforce(candidate, factualSchema(), null)
            .finalTokenCount();
        BudgetConstraints budget = new BudgetConstraints(null, exactTokenCount, null, null);

        ValidatedOutput result = controller.enforce(candidate, factualSchema(), budget);

        assertFalse(result.truncated());
        assertEquals("Paris", result.content());
    }

    // --- failure-path test: a malformed response is rejected, never silently passed through ---

    @Test
    void malformedResponseIsRejectedNotSilentlyPassedThroughAsValid() {
        CandidateOutput malformed = new CandidateOutput("incomplete", Map.of()); // no fields at all

        // The only outcome is an exception -- there is no code path that returns a ValidatedOutput
        // for a schema-non-conformant CandidateOutput.
        assertThrows(
            MalformedOutputException.class,
            () -> controller.enforce(malformed, factualSchema(), null));
    }

    @Test
    void schemaValidationRunsBeforeLengthEnforcement() {
        // A malformed candidate under extreme budget pressure still fails on schema, not length --
        // proving schema-then-length ordering (optimization-catalog.md TECH-017/018 composition
        // field: schema is "Stage 21 of 24 ... before output-length enforcement").
        CandidateOutput malformed = new CandidateOutput("x".repeat(10_000), Map.of());
        BudgetConstraints tinyBudget = new BudgetConstraints(null, 1, null, null);

        assertThrows(
            MalformedOutputException.class,
            () -> controller.enforce(malformed, factualSchema(), tinyBudget));
    }

    // --- null-parameter failure paths ---

    @Test
    void nullCandidateThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
            () -> controller.enforce(null, factualSchema(), null));
    }

    @Test
    void nullSchemaThrowsNullPointerException() {
        CandidateOutput candidate = new CandidateOutput("x", Map.of());
        assertThrows(NullPointerException.class,
            () -> controller.enforce(candidate, null, null));
    }

    @Test
    void nullBudgetConstraintsIsTreatedAsNoLengthLimit() {
        CandidateOutput candidate =
            new CandidateOutput("No limit here.", Map.of("answer", "No limit here.", "confidence", 0.6));

        ValidatedOutput result = controller.enforce(candidate, factualSchema(), null);

        assertFalse(result.truncated());
    }

    @Test
    void nullMaxOutputTokensOnBudgetConstraintsIsTreatedAsNoLengthLimit() {
        CandidateOutput candidate =
            new CandidateOutput("No limit here either.",
                Map.of("answer", "No limit here either.", "confidence", 0.6));
        BudgetConstraints budgetWithNoOutputLimit = new BudgetConstraints(100, null, 1.0, 50);

        ValidatedOutput result = controller.enforce(candidate, factualSchema(), budgetWithNoOutputLimit);

        assertFalse(result.truncated());
    }

    @Test
    void emptySchemaRequiredFieldsAlwaysValidates() {
        CandidateOutput candidate = new CandidateOutput("anything", Map.of());

        ValidatedOutput result = controller.enforce(candidate, new OutputSchema(List.of()), null);

        assertEquals("anything", result.content());
    }
}
