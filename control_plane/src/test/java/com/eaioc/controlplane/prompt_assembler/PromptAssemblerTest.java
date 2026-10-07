package com.eaioc.controlplane.prompt_assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.junit.jupiter.api.Test;

/**
 * EXE-P0.5.B tests for {@link PromptAssembler} (architecture.md §12.2; HD-CG-P0.5-01 to -04;
 * execution-plan.md §18.8 B row).
 *
 * <p>Covers: assembly correctness for a representative input (system/developer/user content routed
 * to the correct region by {@code stability}); security-instruction preservation (system/developer
 * content survives verbatim); cache-stable-prefix ordering (regions stay structurally separate);
 * the vacuous SEMI-STABLE REGION at P0 scope; the final-token-count sum; the one conservative
 * failure path (missing {@link SanitizerOutput}); and that a non-compliant {@link
 * BudgetEnforcementResult} does not block assembly (HD-CG-P0.5-03).
 */
class PromptAssemblerTest {

    private final PromptAssembler assembler = new PromptAssembler();

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
