package com.eaioc.controlplane.output_controls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.eaioc.controlplane.core.schemas.BudgetConstraints;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * {@code EXE-P0.6.E} tests (execution-plan.md §18.9 E row; root {@code CLAUDE.md} rule 6, applied
 * symmetrically to output as it is to input in Capability 5): no security-classified content is
 * stripped by length truncation without an explicit, auditable rule.
 *
 * <p><b>Interpretive disclosure (Architect pre-flight finding, 2026-10-08, not silently
 * assumed):</b> the row's Deliverable, Tests, and Definition of Done cells all use a qualified
 * formulation — "without an explicit, auditable rule" / "never silently truncated away" / "no
 * truncation path drops security-classified content silently" — but the Failure-Path Test cell's
 * own wording ("does not drop a security-classified segment") drops the qualifier if read in
 * isolation. There is no field, convention, or marker anywhere in the corpus for a per-content
 * security classification on {@link CandidateOutput} (unlike Capability 5, where
 * {@code InstructionContext.Type.SYSTEM}/{@code DEVELOPER} already gave root rule 6 a real
 * structural anchor) — a literal, unqualified "never drop it at all, regardless of budget" reading
 * is not implementable without inventing one. This class resolves the tension the way three of the
 * four cells, {@code edge-cases.md} {@code EC-050}'s own Expected Behavior item 1 ("Output schema
 * truncation must be detected and logged"), and {@code architecture.md} §12.4's own text (which
 * permits marked truncation: "Mark truncation explicitly... permit controlled expansion if
 * quality gate requires it") all converge on: a security-classified-shaped segment may be
 * truncated away under budget pressure, but only ever through the single, already-built,
 * already-logged ({@code OUTPUT_TRUNCATED}, {@code EXE-P0.6.D}), {@code truncated=true}-marked
 * path — never silently. Full, unconditional preservation under any budget would need a
 * presently-nonexistent per-content classification marker and is registered, not invented, as the
 * already-open {@code SOURCE-GAP-EXECPLAN-39} (full {@code EC-050}/{@code EC-051} remediation is
 * P1-tier {@code TECH-017}/{@code TECH-018} territory).
 *
 * <p><b>{@code EC-050}/{@code EC-051} citation (Architect pre-flight finding):</b> the live
 * {@code execution-plan.md} §18.9 E row's own "Edge Cases: None" cell is stale — {@code EC-050}'s
 * own worked scenario is literally "a critical security warning" removed by truncation, the exact
 * scenario this sub-phase's Deliverable describes. Recorded here and in the commit message, not
 * silently carried forward, mirroring {@code CLAUDE.md}'s own precedent for a stale row citation
 * (e.g. {@code EXE-P0.1.G}'s "NO DEDICATED SCENARIO" row, corrected when {@code SCN-TEN-003}/
 * {@code SCN-AUDIT-003} were found to apply directly).
 *
 * <p>Files/Modules: "Same as B" (plan row) — test-tree-only; no change to {@code OutputController},
 * {@code CandidateOutput}, {@code OutputSchema}, or {@code ValidatedOutput} production code, since
 * the required invariant is already structurally true of the already-committed B/D code (verified
 * directly: every {@code enforceLength} branch that shortens content sets {@code truncated=true}
 * and triggers the D-row log line; no branch shortens content while reporting
 * {@code truncated=false}).
 */
class OutputControllerSecurityTest {

    private static final String REQUEST_ID = "request-c6e";
    private static final String TENANT_ID = "tenant-c6e";
    private static final String SECURITY_MARKER = "SEC-001-security-classified-marker-8675309";

    // Independent of OutputController's own Holder (never reused directly, matching the same
    // unit-level-decision rationale in OutputController's own Javadoc) -- used here only to assert
    // this test's own budget-margin claim, not to duplicate production behavior.
    private static final Encoding MARKER_ENCODING =
        Encodings.newDefaultEncodingRegistry().getEncoding(EncodingType.CL100K_BASE);

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

    private static OutputSchema openSchema() {
        return new OutputSchema(List.of(new OutputSchema.RequiredField("answer", String.class)));
    }

    // --- Tests cell: a security-classified segment is never SILENTLY truncated away ---

    @Test
    void securityClassifiedSegmentThatFitsWithinBudgetSurvivesVerbatim() {
        String content = SECURITY_MARKER + " plus a short trailing remark.";
        CandidateOutput candidate = new CandidateOutput(content, Map.of("answer", content));
        BudgetConstraints generousBudget = new BudgetConstraints(null, 1000, null, null);

        ValidatedOutput result =
            controller.enforce(REQUEST_ID, TENANT_ID, candidate, openSchema(), generousBudget);

        assertEquals(content, result.content());
        assertFalse(result.truncated());
        assertTrue(result.content().contains(SECURITY_MARKER));
    }

    @Test
    void securityClassifiedSegmentPlacedFirstSurvivesEvenWhenBudgetForcesTailTruncation() {
        // The marker leads content, so a token-boundary truncation that keeps the FIRST N tokens
        // preserves it even though the budget is too small for the whole response -- demonstrating
        // truncation drops only what doesn't fit, never a segment that does, regardless of position.
        String content = SECURITY_MARKER + " " + "word ".repeat(500);
        CandidateOutput candidate = new CandidateOutput(content, Map.of("answer", content));
        // Reviewer finding (2026-10-08): the budget margin claim is now asserted inline, not left
        // as an unverifiable comment. The marker's own token count (via the same CL100K_BASE
        // encoding OutputController uses) must be comfortably below the 30-token budget, which
        // must itself be comfortably below the full content's token count, or this test would be a
        // flake-prone boundary case rather than a genuine margin.
        int markerTokenCount = MARKER_ENCODING.countTokens(SECURITY_MARKER);
        int fullContentTokenCount = MARKER_ENCODING.countTokens(content);
        assertTrue(markerTokenCount < 30, "marker token count=" + markerTokenCount);
        assertTrue(fullContentTokenCount > 30, "full content token count=" + fullContentTokenCount);
        BudgetConstraints tightBudget = new BudgetConstraints(null, 30, null, null);

        ValidatedOutput result =
            controller.enforce(REQUEST_ID, TENANT_ID, candidate, openSchema(), tightBudget);

        assertTrue(result.truncated());
        assertTrue(result.content().contains(SECURITY_MARKER), result.content());
    }

    // --- Failure-Path Test cell: "forcing aggressive length control" -- the most aggressive
    // setting this contract admits (maxOutputTokens <= 0) -- never drops a segment SILENTLY ---

    @Test
    void segmentFallingInTheTruncatedTailIsLostOnlyThroughTheExplicitLoggedMarkedPath() {
        String content = "word ".repeat(500) + " " + SECURITY_MARKER; // marker in the discarded tail
        CandidateOutput candidate = new CandidateOutput(content, Map.of("answer", content));
        BudgetConstraints tightBudget = new BudgetConstraints(null, 5, null, null);

        ValidatedOutput result =
            controller.enforce(REQUEST_ID, TENANT_ID, candidate, openSchema(), tightBudget);

        // The marker IS lost here -- but only through the single path that is always explicit
        // (truncated()==true) and auditable (an OUTPUT_TRUNCATED line was emitted by D). This is
        // the row's own "explicit, auditable rule," satisfied, not evaded.
        assertFalse(result.content().contains(SECURITY_MARKER));
        assertTrue(result.truncated());
        List<String> truncatedLines = logs.list.stream()
            .map(ILoggingEvent::getFormattedMessage)
            .filter(line -> line.contains("event_type=OUTPUT_TRUNCATED"))
            .toList();
        assertEquals(1, truncatedLines.size());
    }

    @Test
    void mostAggressiveLengthControlSettingStillMarksAndLogsEveryDrop() {
        // "Forcing aggressive length control" at one representative member of the <=0 class
        // (maxOutputTokens=0, HD-CG-P0.6-03/the zero-budget-boundary fix) -- OutputControllerTest's
        // own negativeMaxOutputTokensIsTreatedTheSameAsZero already establishes that a negative
        // value behaves identically, so this one value exercises the whole class, not just itself.
        // The marker is lost here, but again only through the explicit truncated=true, logged
        // OUTPUT_TRUNCATED path.
        String content = SECURITY_MARKER;
        CandidateOutput candidate = new CandidateOutput(content, Map.of("answer", content));
        BudgetConstraints zeroBudget = new BudgetConstraints(null, 0, null, null);

        ValidatedOutput result =
            controller.enforce(REQUEST_ID, TENANT_ID, candidate, openSchema(), zeroBudget);

        assertFalse(result.content().contains(SECURITY_MARKER));
        assertTrue(result.truncated());
        assertTrue(logs.list.stream()
            .anyMatch(e -> e.getFormattedMessage().contains("event_type=OUTPUT_TRUNCATED")));
    }

    // --- No silent-drop path exists: content shortening always coincides with truncated()==true ---

    @Test
    void contentIsNeverShortenedWithoutTruncatedBeingMarkedTrue() {
        String content = SECURITY_MARKER + " " + "word ".repeat(200);
        CandidateOutput candidate = new CandidateOutput(content, Map.of("answer", content));

        for (Integer maxOutputTokens : java.util.Arrays.asList(null, -1, 0, 1, 5, 50, 1000, 100_000)) {
            BudgetConstraints budget = maxOutputTokens == null
                ? null
                : new BudgetConstraints(null, maxOutputTokens, null, null);
            ValidatedOutput result =
                controller.enforce(REQUEST_ID, TENANT_ID, candidate, openSchema(), budget);

            boolean contentWasShortened = result.content().length() < content.length();
            assertEquals(contentWasShortened, result.truncated(),
                "maxOutputTokens=" + maxOutputTokens + " content=" + result.content());
        }
    }

    // --- Never logs the marker itself, mirroring PromptAssemblerTest.logLineNeverCarriesRegionContent ---

    @Test
    void securityMarkerNeverAppearsInAnyLogLineEvenWhenTruncated() {
        String content = "word ".repeat(500) + " " + SECURITY_MARKER;
        CandidateOutput candidate = new CandidateOutput(content, Map.of("answer", content));
        BudgetConstraints tightBudget = new BudgetConstraints(null, 5, null, null);

        controller.enforce(REQUEST_ID, TENANT_ID, candidate, openSchema(), tightBudget);

        assertFalse(logs.list.stream()
            .anyMatch(e -> e.getFormattedMessage().contains(SECURITY_MARKER)));
    }
}
