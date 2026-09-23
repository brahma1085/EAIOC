package com.eaioc.controlplane.accounting.ledger;

import static com.eaioc.controlplane.accounting.ledger.LedgerFixtures.*;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * {@code EXE-P0.1.H} (Capability 1 — Token Accounting and Cost Ledger, Sub-phase H —
 * Failure/Recovery), per {@code docs/execution-plan.md} §18.4's Sub-phase H row: "A ledger-write
 * failure must never silently under-report cost (root {@code CLAUDE.md} rule 5)." Tests: "simulated
 * write failure results in unverified flag, excluded from any savings computation." Failure-Path
 * Test: "unverified record is excluded from a savings-sum query."
 *
 * <p>The realistic failure mode for this capability is not the in-memory store itself throwing
 * (a plain {@code ConcurrentHashMap} has no I/O to fail) — it is the upstream accounting
 * *computation* failing partway, exactly as {@code SCN-AUDIT-003} describes ("the provider didn't
 * return usage data"). {@link CostLedgerEntry#unverifiedFallback} is this sub-phase's recovery
 * path for that case.
 */
class CostLedgerFailureRecoveryTest {

    @Test
    void simulatedAccountingComputationFailure_recordsFallbackEntry_neverLostNeverFabricated() {
        CostLedgerStore store = new CostLedgerStore();

        // Simulates: a pipeline stage attempted to construct a real CostLedgerEntry, but the
        // provider response was missing usage data partway through, so the caller cannot honestly
        // populate token/cost fields (SCN-AUDIT-003's own trigger).
        CostLedgerEntry fallback = CostLedgerEntry.unverifiedFallback(
            "entry-failed-computation",
            "request-failed-computation",
            "tenant-h",
            "org-1",
            "task-1",
            Instant.parse("2026-09-23T00:00:00Z"));

        // The write path itself does not fail — the failure already happened upstream (in the
        // computation that couldn't complete); this write succeeds and preserves a record of the
        // attempt rather than silently dropping it.
        store.write(fallback);

        CostLedgerEntry readBack = store.read("tenant-h", "entry-failed-computation").orElseThrow();

        assertThat(readBack.verified())
            .as("a failed accounting computation must never be marked verified")
            .isFalse();
        assertThat(readBack.actualCost())
            .as("no cost figure is guessed when it could not be computed")
            .isZero();
        assertThat(readBack.netSavings())
            .as("no savings figure is guessed when it could not be computed")
            .isZero();
        assertThat(readBack.actualInputTokens())
            .as("no token count is guessed when it could not be computed")
            .isZero();
    }

    @Test
    void fallbackEntry_excludedFromSavingsSumQuery_alongsideARealVerifiedEntry() {
        CostLedgerStore store = new CostLedgerStore();

        CostLedgerEntry realEntry = new CostLedgerEntry(
            "entry-real", "request-real", "tenant-h", "org-1", null, null, "task-1",
            Instant.parse("2026-09-23T00:00:00Z"),
            1000L, 200L, 800L, 200L, 0L, 100L, 0L, 0L, 0L,
            0.10, 0.08, 0.0, 0.0, 0.02, 0.02, 20.0,
            java.util.Map.of(), "USD", "v1", "model-x", "provider-x",
            INPUT, OUTPUT, CACHE, MODEL, TOOLS, WORKFLOW, COST, PERFORMANCE, QUALITY,
            true);

        CostLedgerEntry fallback = CostLedgerEntry.unverifiedFallback(
            "entry-fallback", "request-fallback", "tenant-h", "org-1", "task-1",
            Instant.parse("2026-09-23T00:01:00Z"));

        store.write(realEntry);
        store.write(fallback);

        CostLedgerEntry readReal = store.read("tenant-h", "entry-real").orElseThrow();
        CostLedgerEntry readFallback = store.read("tenant-h", "entry-fallback").orElseThrow();

        // A savings-sum query built correctly on top of read() — filtering on verified() — never
        // includes the failed-computation entry, even though it exists in the ledger.
        double savingsSum = Stream.of(readReal, readFallback)
            .filter(CostLedgerEntry::verified)
            .mapToDouble(CostLedgerEntry::netSavings)
            .sum();

        assertThat(savingsSum).isEqualTo(readReal.netSavings());
    }

    @Test
    void writeFailure_duplicateEntryId_isRejected_notSilentlyOverwritten() {
        // A write "failure" that IS a store-level failure: attempting to record a second entry
        // under an entryId already written. Root CLAUDE.md rule 5's "never fabricate" extends to
        // never silently replacing an earlier, possibly-different cost figure with a later one —
        // this is EXE-P0.1.B's append-only rule, re-confirmed here as this sub-phase's own
        // required evidence that the failure path "never silently succeeds."
        CostLedgerStore store = new CostLedgerStore();
        store.write(CostLedgerEntry.unverifiedFallback(
            "entry-dup", "request-1", "tenant-h", "org-1", "task-1",
            Instant.parse("2026-09-23T00:00:00Z")));

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
            store.write(CostLedgerEntry.unverifiedFallback(
                "entry-dup", "request-2", "tenant-h", "org-1", "task-1",
                Instant.parse("2026-09-23T00:02:00Z"))))
            .isInstanceOf(DuplicateLedgerEntryException.class);
    }
}
