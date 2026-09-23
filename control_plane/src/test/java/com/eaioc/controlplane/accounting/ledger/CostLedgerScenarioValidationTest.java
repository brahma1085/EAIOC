package com.eaioc.controlplane.accounting.ledger;

import static com.eaioc.controlplane.accounting.ledger.LedgerFixtures.*;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * {@code EXE-P0.1.G} (Capability 1 — Token Accounting and Cost Ledger, Sub-phase G — Scenario
 * Validation).
 *
 * <p><b>Correction to {@code docs/execution-plan.md} §18.4's own Sub-phase G row:</b> that row
 * states "{@code NO DEDICATED SCENARIO} verified — no {@code SCN-*} scenario isolates ledger
 * construction itself." Independent re-verification against the live {@code scenario-matrix.md}
 * (required by this sub-phase's own investigate-before-claiming discipline, not assumed from the
 * plan's citation) found this to be **inaccurate**: two scenarios directly and specifically
 * validate this capability's own behavior:
 *
 * <ul>
 *   <li>{@code SCN-TEN-003} — "Cost Accounting Isolated Per Tenant Under Concurrent Load" (P1,
 *       Multi-Tenancy, Automation Candidate: integration test). Trigger: "Cost ledger entries are
 *       written concurrently for both tenants." Expected: "no aggregation ever blends Tenant A's
 *       and Tenant B's cost even transiently under concurrent write load."</li>
 *   <li>{@code SCN-AUDIT-003} — "UNVERIFIED Ledger Entry Never Surfaces as a Verified Saving" (P0,
 *       Observability). Expected: "The affected ledger entry is marked unverified and is
 *       explicitly excluded from any 'net savings' total ... never silently included as if
 *       verified."</li>
 * </ul>
 *
 * <p>This is not a file-scope violation to fix by editing {@code execution-plan.md} (Mode B's file
 * scope is {@code control_plane/} only) — it is recorded here, in this test's own Javadoc and in
 * this sub-phase's completion report, as a finding for the plan's own maintainers.
 *
 * <p><b>Scope note on {@code SCN-AUDIT-003}:</b> its full acceptance criterion is about a
 * "governance dashboard" / "net savings total" — that reporting/aggregation layer does not exist
 * yet and is not this capability's own responsibility to build (a future capability's job, not
 * invented here to avoid scope creep). What this capability *does* own, and what this test
 * validates, is the ledger-level prerequisite the scenario depends on: a {@code verified=false}
 * entry's flag survives storage and retrieval faithfully, and any correctly-written savings
 * computation built on top of {@code read()} naturally excludes it.
 */
class CostLedgerScenarioValidationTest {

    /**
     * Validates {@code SCN-TEN-003}: concurrent writes from two tenants never blend, even
     * transiently. {@code EXE-P0.1.E}'s own isolation tests were sequential (write, then write,
     * then read) — this test exercises the scenario's actual trigger, real concurrent writes from
     * two threads forced to overlap via a {@link CyclicBarrier}, which is the case the sequential
     * tests could not have caught if the underlying map-of-maps structure had a race condition.
     */
    @Test
    void concurrentWritesFromTwoTenants_neverBlend_SCN_TEN_003() throws InterruptedException {
        CostLedgerStore store = new CostLedgerStore();
        int writesPerTenant = 200;
        CyclicBarrier startBarrier = new CyclicBarrier(2);
        CountDownLatch done = new CountDownLatch(2);

        Thread tenantAWriter = new Thread(() ->
            writeConcurrently(store, "tenant-a", writesPerTenant, startBarrier, done));
        Thread tenantBWriter = new Thread(() ->
            writeConcurrently(store, "tenant-b", writesPerTenant, startBarrier, done));

        tenantAWriter.start();
        tenantBWriter.start();
        boolean completed = done.await(10, TimeUnit.SECONDS);
        assertThat(completed).as("both writer threads completed within timeout").isTrue();

        // "no aggregation ever blends ... even transiently" — every entry written under one
        // tenant must be invisible from the other tenant's partition, for all 200 concurrently
        // written entries, not just a sampled few.
        for (int i = 0; i < writesPerTenant; i++) {
            assertThat(store.read("tenant-a", "tenant-a-entry-" + i)).isPresent();
            assertThat(store.read("tenant-b", "tenant-a-entry-" + i))
                .as("tenant-b must never see tenant-a's entry %d, even under concurrent load", i)
                .isEmpty();

            assertThat(store.read("tenant-b", "tenant-b-entry-" + i)).isPresent();
            assertThat(store.read("tenant-a", "tenant-b-entry-" + i))
                .as("tenant-a must never see tenant-b's entry %d, even under concurrent load", i)
                .isEmpty();
        }
    }

    /**
     * Validates the ledger-level prerequisite of {@code SCN-AUDIT-003}: an unverified entry's flag
     * survives storage/retrieval faithfully, so a savings computation filtering on
     * {@link CostLedgerEntry#verified()} naturally excludes it — never silently included as if
     * verified.
     */
    @Test
    void unverifiedEntry_neverContributesToAVerifiedSavingsSum_SCN_AUDIT_003() {
        CostLedgerStore store = new CostLedgerStore();

        CostLedgerEntry verifiedEntry = entryFor("tenant-audit", "verified-entry", 10.0, true);
        CostLedgerEntry unverifiedEntry = entryFor("tenant-audit", "unverified-entry", 999.0, false);

        store.write(verifiedEntry);
        store.write(unverifiedEntry);

        CostLedgerEntry readVerified = store.read("tenant-audit", "verified-entry").orElseThrow();
        CostLedgerEntry readUnverified = store.read("tenant-audit", "unverified-entry").orElseThrow();

        // The flag itself survives the write/read round-trip unchanged — root CLAUDE.md rule 5's
        // "mark it unverified" requirement is not lost at the storage layer.
        assertThat(readVerified.verified()).isTrue();
        assertThat(readUnverified.verified()).isFalse();

        // A correctly-written savings computation — filter on verified() before summing — never
        // includes the unverified entry's inflated netSavings value, no matter how large it is.
        double verifiedSavingsSum = Stream.of(readVerified, readUnverified)
            .filter(CostLedgerEntry::verified)
            .mapToDouble(CostLedgerEntry::netSavings)
            .sum();

        assertThat(verifiedSavingsSum)
            .as("unverified entry's netSavings (999.0) must never appear in a verified total")
            .isEqualTo(readVerified.netSavings());
    }

    private static void writeConcurrently(
        CostLedgerStore store, String tenantId, int count, CyclicBarrier barrier, CountDownLatch done) {
        try {
            barrier.await(); // force maximum overlap between the two writer threads
            for (int i = 0; i < count; i++) {
                store.write(entryFor(tenantId, tenantId + "-entry-" + i, 1.0, true));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            done.countDown();
        }
    }

    private static CostLedgerEntry entryFor(String tenantId, String entryId, double netSavings, boolean verified) {
        return new CostLedgerEntry(
            entryId,
            "request-for-" + entryId,
            tenantId,
            "org-1",
            null,
            null,
            "task-1",
            Instant.parse("2026-09-23T00:00:00Z"),
            1000L, 200L, 1000L, 200L, 0L, 0L, 0L, 0L, 0L,
            0.10, 0.05, 0.0, 0.0, netSavings, netSavings, 0.0,
            Map.of(),
            "USD",
            "v1",
            "model-x",
            "provider-x",
            INPUT, OUTPUT, CACHE, MODEL, TOOLS, WORKFLOW, COST, PERFORMANCE, QUALITY,
            verified);
    }
}
