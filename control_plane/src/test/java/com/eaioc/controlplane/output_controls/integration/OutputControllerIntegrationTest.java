package com.eaioc.controlplane.output_controls.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.eaioc.controlplane.accounting.ledger.CostLedgerEntry;
import com.eaioc.controlplane.accounting.ledger.CostLedgerStore;
import com.eaioc.controlplane.accounting.ledger.LedgerCostReporter;
import com.eaioc.controlplane.core.schemas.BudgetConstraints;
import com.eaioc.controlplane.output_controls.CandidateOutput;
import com.eaioc.controlplane.output_controls.OutputController;
import com.eaioc.controlplane.output_controls.OutputSchema;
import com.eaioc.controlplane.output_controls.ValidatedOutput;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingType;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * EXE-P0.6 Sub-phase C (Integration), realized as the C row of {@code execution-plan.md} §18.9:
 * {@link OutputController} wired to consume Capability 1's <em>real</em> ledger infrastructure for
 * cost/length accounting — not a stub, since Capability 1 already exists and is Gate-approved
 * (mirroring the {@code EXE-P0.5.C} precedent). Test-tree only, no main-code change, matching the C
 * row's own Files/Modules cell ("Integration test with a stub or real upstream ledger") and
 * Failure-Path cell ({@code N/A}).
 *
 * <p><b>Direction-of-composition (Architect pre-flight finding, 2026-10-08, disclosed as a
 * unit-level implementation decision — not a new human decision gate, per the {@code EXE-P0.2.B}
 * precedent "any unit-local implementation mechanism that changes no public contract is a
 * unit-level decision"):</b> neither {@code architecture.md} §12.3/§12.4 nor {@code interfaces.md}
 * defines a mapping between a {@link CostLedgerEntry} and a {@link BudgetConstraints}/{@link
 * ValidatedOutput} value in either direction. Per the Architect's pre-flight, this test covers both
 * grounded directions rather than inventing a single undocumented one:
 * <ul>
 *   <li>{@link #ledgerSuppliedBudgetConstrainsOutputControllerEnforcement} — a real ledger entry's
 *       existing {@code Output.optimizedOutput} field feeds {@code BudgetConstraints.maxOutputTokens}
 *       into a real {@link OutputController#enforce} call (the literal "consume... for...
 *       enforcement" reading). (Reviewer finding, 2026-10-08: an earlier draft of this Javadoc
 *       misnamed the field as {@code rawOutput}; corrected here to match the code, which uses
 *       {@code optimizedOutput} deliberately — a fixture-recorded {@code rawOutput} comparable in
 *       size to the candidate's own token count would not reliably force truncation.)
 *   <li>{@link #outputControllerResultRoundTripsThroughTheRealLedger} — {@link OutputController}'s
 *       own result ({@link ValidatedOutput#finalTokenCount()}/{@link ValidatedOutput#truncated()})
 *       is composed into a {@link CostLedgerEntry.Output} and round-tripped through the real {@link
 *       CostLedgerStore}/{@link LedgerCostReporter} (the "accounting" reading).
 * </ul>
 * {@code OutputController} itself never calls into the ledger — exactly as {@code PromptAssembler}
 * never calls {@code PolicyBudgetEnforcer} (HD-CG-P0.5-02's precedent): this test demonstrates the
 * correct caller-side sequencing, not a change to {@code OutputController}'s committed signature.
 *
 * <p><b>Independent re-tokenization (Architect pre-flight disclosure):</b> {@link ValidatedOutput}
 * exposes no pre-truncation original token count, so {@link
 * #outputControllerResultRoundTripsThroughTheRealLedger} independently re-tokenizes the candidate
 * with the same JTokkit {@code CL100K_BASE} encoding {@code OutputController} itself uses, to
 * compute the ledger's {@code Output.truncated} quantity. This is a legitimate, deterministic
 * recomputation (both call sites hit the same fixed, non-random vocabulary) — not a fabricated
 * value — but is flagged forward for any future unit that wires this path in production, where
 * {@code ValidatedOutput} may need to carry the original count as a first-class field instead.
 */
class OutputControllerIntegrationTest {

    private static final Encoding CL100K_BASE =
        Encodings.newDefaultEncodingRegistry().getEncoding(EncodingType.CL100K_BASE);

    private final OutputController controller = new OutputController();

    private static OutputSchema factualSchema() {
        return new OutputSchema(List.of(
            new OutputSchema.RequiredField("answer", String.class),
            new OutputSchema.RequiredField("confidence", Double.class)));
    }

    @Test
    void ledgerSuppliedBudgetConstrainsOutputControllerEnforcement() {
        CostLedgerStore store = new CostLedgerStore();
        store.write(entry("tenant-c6c", "entry-c6c-1", "request-c6c-1",
            /* rawOutput */ 500, /* optimizedOutput */ 5));
        LedgerCostReporter reporter = new LedgerCostReporter(store);

        Optional<CostLedgerEntry> ledgerEntry = reporter.getRequestCost("request-c6c-1", "tenant-c6c");
        assertTrue(ledgerEntry.isPresent());
        long realBudget = ledgerEntry.get().output().optimizedOutput();

        String longContent = "word ".repeat(500);
        CandidateOutput candidate =
            new CandidateOutput(longContent, Map.of("answer", longContent, "confidence", 0.5));
        BudgetConstraints budgetFromLedger = new BudgetConstraints(null, (int) realBudget, null, null);

        ValidatedOutput result =
            controller.enforce("request-c6c-1", "tenant-c6c", candidate, factualSchema(), budgetFromLedger);

        assertTrue(result.truncated());
        assertEquals(realBudget, result.finalTokenCount());
    }

    @Test
    void outputControllerResultRoundTripsThroughTheRealLedger() {
        String longContent = "word ".repeat(500);
        CandidateOutput candidate =
            new CandidateOutput(longContent, Map.of("answer", longContent, "confidence", 0.5));
        BudgetConstraints budget = new BudgetConstraints(null, 5, null, null);

        ValidatedOutput result =
            controller.enforce("request-c6c-2", "tenant-c6c", candidate, factualSchema(), budget);
        assertTrue(result.truncated());

        int originalTokenCount = CL100K_BASE.countTokens(longContent);
        long truncatedTokenCount = originalTokenCount - result.finalTokenCount();

        CostLedgerStore store = new CostLedgerStore();
        store.write(entry("tenant-c6c", "entry-c6c-2", "request-c6c-2",
            originalTokenCount, result.finalTokenCount()));
        LedgerCostReporter reporter = new LedgerCostReporter(store);

        Optional<CostLedgerEntry> readBack = reporter.getRequestCost("request-c6c-2", "tenant-c6c");
        assertTrue(readBack.isPresent());
        assertEquals(result.finalTokenCount(), readBack.get().output().optimizedOutput());
        assertEquals(originalTokenCount, readBack.get().output().rawOutput());
        assertTrue(truncatedTokenCount > 0);
    }

    /** TEST FIXTURE: a minimal, schema-valid, verified=true CostLedgerEntry recording output tokens. */
    private static CostLedgerEntry entry(
            String tenantId, String entryId, String requestId, long rawOutput, long optimizedOutput) {
        return new CostLedgerEntry(
            entryId, requestId, tenantId, "org-1", null, null, "task-1",
            Instant.parse("2026-10-08T00:00:00Z"),
            100, rawOutput, 100, optimizedOutput, 0, 0, 0, 0, 0,
            5.0, 5.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            Map.of(), "USD", "v1", "model-1", "provider-1",
            new CostLedgerEntry.Input(100, 100, 0, 0, 0, 0, 0, 0, 0, 100),
            new CostLedgerEntry.Output(rawOutput, optimizedOutput, rawOutput - optimizedOutput, 0),
            new CostLedgerEntry.Cache(0, 0, 0, 0, 0, 0, 0),
            new CostLedgerEntry.Model("model-1", "model-1", "DIRECT", false, "MEDIUM", null),
            new CostLedgerEntry.Tools(0, 0, 0, 0, 0),
            new CostLedgerEntry.Workflow(0, 0, 0, 0, 0),
            new CostLedgerEntry.Cost(5.0, 0.0, 0.0, 0.0, 0.0, 5.0, 0.0, 0.0, 0.0),
            new CostLedgerEntry.Performance(100, 50, 50, 0, 0, 0),
            new CostLedgerEntry.Quality(1.0, 1.0, true, 1.0, 1.0, true),
            true);
    }
}
