package com.eaioc.controlplane.prompt_assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.eaioc.controlplane.core.schemas.ControlPlaneRequest;
import com.eaioc.controlplane.core.schemas.InstructionContext;
import com.eaioc.controlplane.core.schemas.LatencyRequirements;
import com.eaioc.controlplane.core.schemas.QualityRequirements;
import com.eaioc.controlplane.core.schemas.RequestType;
import com.eaioc.controlplane.core.schemas.SecurityClassification;
import com.eaioc.controlplane.pipeline.t1_context.sanitizer.SanitizerOutput;
import com.eaioc.controlplane.pipeline.t1_context.sanitizer.TokenCounterIdentity;
import com.eaioc.controlplane.policy.context_policy.BudgetEnforcementResult;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * EXE-P0.5.B, EXE-P0.5.D, and EXE-P0.5.E tests for {@link PromptAssembler} (architecture.md §12.2;
 * HD-CG-P0.5-01 to -04; execution-plan.md §18.8 B, D, and E rows).
 *
 * <p>Covers: assembly correctness for a representative input (system/developer/user content routed
 * to the correct region by {@code stability}); security-instruction preservation (system/developer
 * content survives verbatim); cache-stable-prefix ordering (regions stay structurally separate);
 * the vacuous SEMI-STABLE REGION at P0 scope; the final-token-count sum; the one conservative
 * failure path (missing {@link SanitizerOutput}); that a non-compliant {@link
 * BudgetEnforcementResult} does not block assembly (HD-CG-P0.5-03); (EXE-P0.5.D) exactly one
 * structured INFO log line per successful call, never carrying region content; and (EXE-P0.5.E)
 * that security-classified content is never dropped, truncated, or overwritten even when another
 * instruction contends for the same region — the only real contention point this schema admits,
 * since {@code PromptAssembler} has no reordering/prioritization mechanism to "force" at all (the
 * row's own "cache-stable-prefix reordering" language presupposes one that B's approved minimal
 * design never built; resolved the same way {@code EXE-P0.4.E} resolved an identical presupposition
 * for {@code PolicyBudgetEnforcer} — a functional test against the closest real stress point, plus
 * a structural test proving no such injection point exists). Independently confirmed, not merely
 * asserted (Verifier finding, 2026-10-08): {@code architecture.md} §24/§37 list "Context reordering"
 * as its own distinct optimization technique at maturity tier <b>P3</b> ("Advanced Optimization") —
 * categorically outside this P0 capability's approved scope, not something this unit's own design
 * omits or evades.
 */
class PromptAssemblerTest {

    private final PromptAssembler assembler = new PromptAssembler();
    private ListAppender<ILoggingEvent> logs;
    private Logger assemblerLog;

    @BeforeEach
    void attachLogCapture() {
        assemblerLog = (Logger) LoggerFactory.getLogger(PromptAssembler.class);
        logs = new ListAppender<>();
        logs.start();
        assemblerLog.addAppender(logs);
    }

    @AfterEach
    void detachLogCapture() {
        assemblerLog.detachAppender(logs);
    }

    @Test
    void assemblesStableSystemAndDeveloperContextIntoCacheablePrefixInOrder() {
        ControlPlaneRequest request = request(
            instruction("system instructions", InstructionContext.Type.SYSTEM, InstructionContext.Stability.STABLE, 10),
            instruction("developer policy", InstructionContext.Type.DEVELOPER, InstructionContext.Stability.STABLE, 5));

        AssembledPrompt result = assembler.assemble(request, sanitized("user request text", 7), BudgetEnforcementResult.ok());

        assertEquals("system instructions\n\ndeveloper policy", result.cacheablePrefix());
        assertEquals("", result.semiStableRegion());
        assertEquals("user request text", result.volatileSuffix());
    }

    @Test
    void semiStableInstructionRoutesToSemiStableRegion() {
        ControlPlaneRequest request = request(
            instruction("project doc snapshot", InstructionContext.Type.DEVELOPER, InstructionContext.Stability.SEMI_STABLE, 3),
            null);

        AssembledPrompt result = assembler.assemble(request, sanitized("hi", 1), BudgetEnforcementResult.ok());

        assertEquals("project doc snapshot", result.semiStableRegion());
        assertEquals("", result.cacheablePrefix());
    }

    @Test
    void volatileInstructionRoutesToVolatileSuffixAfterSanitizedInput() {
        // architecture.md §12.2's own VOLATILE/DYNAMIC SUFFIX order: "current user request, current
        // retrieved context, current tool results, current task state" -- the user request leads.
        ControlPlaneRequest request = request(
            instruction("current task state", InstructionContext.Type.SYSTEM, InstructionContext.Stability.VOLATILE, 4),
            null);

        AssembledPrompt result = assembler.assemble(request, sanitized("the user's question", 6), BudgetEnforcementResult.ok());

        assertEquals("the user's question\n\ncurrent task state", result.volatileSuffix());
    }

    @Test
    void semiStableRegionIsEmptyWhenNoInstructionIsMarkedSemiStable() {
        // HD-CG-P0.5-04: no retrieval/memory layer exists in P0 (CORE-GAP-01); vacuously empty.
        ControlPlaneRequest request = request(null, null);

        AssembledPrompt result = assembler.assemble(request, sanitized("only the user input", 3), BudgetEnforcementResult.ok());

        assertEquals("", result.semiStableRegion());
    }

    @Test
    void securityInstructionContentSurvivesAssemblyVerbatim() {
        String secret = "do-not-bypass-this-security-instruction-4242";
        ControlPlaneRequest request = request(
            instruction(secret, InstructionContext.Type.SYSTEM, InstructionContext.Stability.STABLE, 9), null);

        AssembledPrompt result = assembler.assemble(request, sanitized("input", 1), BudgetEnforcementResult.ok());

        // Exact equality, not mere containment: the region's full content must be exactly the
        // secret, unwrapped, unprefixed, unsuffixed -- a containment check alone would still pass
        // under a future refactor that wraps or duplicates the content (Reviewer finding).
        assertEquals(secret, result.cacheablePrefix());
        assertTrue(result.cacheablePrefix().contains(secret));
    }

    @Test
    void allThreeRegionsStayIsolatedWhenAllAreSimultaneouslyPopulated() {
        ControlPlaneRequest request = request(
            instruction("stable system prompt", InstructionContext.Type.SYSTEM, InstructionContext.Stability.STABLE, 3),
            instruction("semi-stable developer note", InstructionContext.Type.DEVELOPER, InstructionContext.Stability.SEMI_STABLE, 2));

        AssembledPrompt result = assembler.assemble(request, sanitized("the volatile user request", 4), BudgetEnforcementResult.ok());

        assertEquals("stable system prompt", result.cacheablePrefix());
        assertEquals("semi-stable developer note", result.semiStableRegion());
        assertEquals("the volatile user request", result.volatileSuffix());
    }

    @Test
    void finalTokenCountSumsInstructionEstimatesAndSanitizerPostCount() {
        ControlPlaneRequest request = request(
            instruction("sys", InstructionContext.Type.SYSTEM, InstructionContext.Stability.STABLE, 10),
            instruction("dev", InstructionContext.Type.DEVELOPER, InstructionContext.Stability.STABLE, 5));

        AssembledPrompt result = assembler.assemble(request, sanitized("input", 7), BudgetEnforcementResult.ok());

        assertEquals(22, result.finalTokenCount());
    }

    @Test
    void nullSanitizerOutputBlocksAssembly() {
        ControlPlaneRequest request = request(null, null);

        assertThrows(NullPointerException.class,
            () -> assembler.assemble(request, null, BudgetEnforcementResult.ok()));
    }

    @Test
    void nullBudgetResultBlocksAssembly() {
        ControlPlaneRequest request = request(null, null);

        assertThrows(NullPointerException.class,
            () -> assembler.assemble(request, sanitized("input", 1), null));
    }

    @Test
    void nonCompliantBudgetResultDoesNotBlockAssembly() {
        // HD-CG-P0.5-03: budget enforcement is Capability 4's own gate; not re-enforced here.
        ControlPlaneRequest request = request(null, null);
        BudgetEnforcementResult nonCompliant = new BudgetEnforcementResult(false, List.of());

        AssembledPrompt result = assembler.assemble(request, sanitized("input", 1), nonCompliant);

        assertEquals("input", result.volatileSuffix());
    }

    // EXE-P0.5.D: exactly one structured line per successful call (plan §18.8 D row).

    @Test
    void successfulAssemblyEmitsExactlyOneInfoLine() {
        ControlPlaneRequest request = request(
            instruction("sys", InstructionContext.Type.SYSTEM, InstructionContext.Stability.STABLE, 3), null);

        AssembledPrompt result = assembler.assemble(request, sanitized("input", 4), BudgetEnforcementResult.ok());

        List<String> lines = linesContaining("event_type=PROMPT_ASSEMBLED");
        assertEquals(1, lines.size());
        assertEquals(1, allLines().size());
        assertEquals(Level.INFO, logs.list.get(0).getLevel());
        String line = lines.get(0);
        assertTrue(line.contains("component_id=PROMPT-ASSEMBLER"), line);
        assertTrue(line.contains("tenant_id=tenant-pa"), line);
        assertTrue(line.contains("request_id=req-pa-1"), line);
        assertTrue(line.contains("final_token_count=" + result.finalTokenCount()), line);
    }

    @Test
    void logLineNeverCarriesRegionContent() {
        String secret = "do-not-log-this-security-instruction-8675309";
        ControlPlaneRequest request = request(
            instruction(secret, InstructionContext.Type.SYSTEM, InstructionContext.Stability.STABLE, 5), null);

        assembler.assemble(request, sanitized("also do not log this user input", 2), BudgetEnforcementResult.ok());

        assertFalse(allLines().stream().anyMatch(l -> l.contains(secret)));
        assertFalse(allLines().stream().anyMatch(l -> l.contains("also do not log this user input")));
    }

    private List<String> allLines() {
        return logs.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
    }

    private List<String> linesContaining(String substring) {
        return allLines().stream().filter(l -> l.contains(substring)).toList();
    }

    // EXE-P0.5.E: security-classified content is never dropped, truncated, or overwritten (plan
    // §18.8 E row; root CLAUDE.md rule 6; SEC-001).

    @Test
    void securityClassifiedContentSurvivesVerbatimWhenSharingARegionWithAnotherInstruction() {
        // The row's own "cache-stable-prefix reordering" language presupposes a reordering/
        // optimization mechanism that PromptAssembler does not have (see the structural test
        // below) -- its region placement is a fixed rule, not a tunable one. The only real
        // contention point this schema admits is two STABLE instructions (systemContext and
        // developerContext) sharing the CACHEABLE PREFIX region; this test proves neither is
        // dropped, truncated, or overwritten by the other.
        String secret = "security-classified-instruction-must-survive-intact-13579";
        String contending = "a second, unrelated developer instruction occupying the same region";
        ControlPlaneRequest request = request(
            instruction(secret, InstructionContext.Type.SYSTEM, InstructionContext.Stability.STABLE, 9),
            instruction(contending, InstructionContext.Type.DEVELOPER, InstructionContext.Stability.STABLE, 6));

        AssembledPrompt result = assembler.assemble(request, sanitized("input", 1), BudgetEnforcementResult.ok());

        // Exact equality: both contents present in full, in the documented system-before-developer
        // order, with nothing dropped or altered in between.
        assertEquals(secret + "\n\n" + contending, result.cacheablePrefix());
    }

    @Test
    void promptAssemblerHasNoReorderingOrPrioritizationInjectionPoint() {
        // Structural proof, not merely behavioral: no "cache-stable-prefix reordering" mechanism
        // is reachable through this class's public surface -- exactly one public method is
        // declared, and none of its parameters is ordering/priority/Comparator-shaped. (This
        // checks the public, caller-reachable surface specifically -- the DoD's own "assembly
        // path" language is necessarily public-API-reachable -- not every private helper.)
        long publicMethodCount = java.util.Arrays.stream(PromptAssembler.class.getMethods())
            .filter(m -> m.getDeclaringClass() == PromptAssembler.class)
            .count();
        assertEquals(1, publicMethodCount, "expected exactly one public method declared on PromptAssembler");

        java.lang.reflect.Method assemble;
        try {
            assemble = PromptAssembler.class.getMethod(
                "assemble", ControlPlaneRequest.class, SanitizerOutput.class, BudgetEnforcementResult.class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("assemble signature changed", e);
        }
        for (Class<?> paramType : assemble.getParameterTypes()) {
            String name = paramType.getSimpleName().toLowerCase();
            assertFalse(name.contains("comparator") || name.contains("order") || name.contains("priority"),
                "unexpected ordering-shaped parameter: " + paramType);
        }
    }

    private static InstructionContext instruction(
            String content, InstructionContext.Type type, InstructionContext.Stability stability, int tokenEstimate) {
        return new InstructionContext(content, type, stability, tokenEstimate, null);
    }

    private static SanitizerOutput sanitized(String text, int tokenCountPost) {
        return new SanitizerOutput(text, tokenCountPost, tokenCountPost, TokenCounterIdentity.JTOKKIT_CL100K_BASE_1_1_0);
    }

    /** TEST FIXTURE: a schema-valid request carrying the given system/developer instruction contexts. */
    private static ControlPlaneRequest request(InstructionContext systemContext, InstructionContext developerContext) {
        return new ControlPlaneRequest(
            "1.0.0", "req-pa-1", "corr-pa-1", "tenant-pa", "org-pa",
            "app-pa", null, null, null, "task-pa", null, 0,
            RequestType.GENERIC_LLM,
            "ignored user input field", systemContext, developerContext, null,
            new QualityRequirements(0.9, QualityRequirements.QualityTier.STANDARD, false, true),
            new LatencyRequirements(2000, 500, true),
            null,
            new SecurityClassification(SecurityClassification.Level.INTERNAL, false, null, null),
            null, null,
            false,
            Map.of(), Instant.parse("2026-10-07T00:00:00Z"), null);
    }
}
