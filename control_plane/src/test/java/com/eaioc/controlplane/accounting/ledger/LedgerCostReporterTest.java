package com.eaioc.controlplane.accounting.ledger;

import static com.eaioc.controlplane.accounting.ledger.LedgerFixtures.*;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@code REM-P0.2.B-02} (Cost Ledger Request-to-Measurement Retrieval — Minimal
 * Implementation), per {@code docs/execution-plan.md} §18.14.8's Tests and Failure-Path Test
 * columns: a single {@code verified = true} match is returned; a wrong tenant, an unrelated tenant's
 * same-{@code request_id} entry, zero matches, several matches (whatever their {@code verified}
 * values, including one verified plus one unverified), and a single unverified match all yield no
 * measurement — the retrieval never resolves "several" by picking one.
 */
class LedgerCostReporterTest {

    @Test
    void getRequestCost_singleVerifiedMatch_returnsEntry() {
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        CostLedgerEntry entry = entry("tenant-a", "entry-1", "request-1", true);
        store.write(entry);

        assertThat(reporter.getRequestCost("request-1", "tenant-a")).contains(entry);
    }

    @Test
    void getRequestCost_wrongTenant_returnsEmpty() {
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        store.write(entry("tenant-a", "entry-1", "request-1", true));

        assertThat(reporter.getRequestCost("request-1", "tenant-b")).isEmpty();
    }

    @Test
    void getRequestCost_otherTenantSameRequestId_doesNotInflateCardinality_forRequestedTenant() {
        // If tenant isolation were broken, tenant-b's entry would make tenant-a's count "several"
        // (turning a correct single match into a wrongly-suppressed "no measurement"). It must not.
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        CostLedgerEntry tenantAEntry = entry("tenant-a", "entry-1", "request-1", true);
        store.write(tenantAEntry);
        store.write(entry("tenant-b", "entry-2", "request-1", true));

        assertThat(reporter.getRequestCost("request-1", "tenant-a")).contains(tenantAEntry);
    }

    @Test
    void getRequestCost_noMatchingEntries_returnsEmpty() {
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);

        assertThat(reporter.getRequestCost("request-1", "tenant-a")).isEmpty();
    }

    @Test
    void getRequestCost_twoVerifiedMatches_returnsEmpty_neverPicksOne() {
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        store.write(entry("tenant-a", "entry-1", "request-1", true));
        store.write(entry("tenant-a", "entry-2", "request-1", true));

        assertThat(reporter.getRequestCost("request-1", "tenant-a")).isEmpty();
    }

    @Test
    void getRequestCost_verifiedPlusUnverifiedMatch_isSeveral_returnsEmpty_countAllThenVerify() {
        // Count-all-then-verify (human contract decision, 2026-09-25): the count is taken over every
        // matching entry regardless of verified(); one verified + one unverified is "several", not
        // "one", so it yields no measurement even though a verified entry exists among them.
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        store.write(entry("tenant-a", "entry-1", "request-1", true));
        store.write(entry("tenant-a", "entry-2", "request-1", false));

        assertThat(reporter.getRequestCost("request-1", "tenant-a")).isEmpty();
    }

    @Test
    void getRequestCost_singleUnverifiedMatch_returnsEmpty() {
        CostLedgerStore store = new CostLedgerStore();
        LedgerCostReporter reporter = new LedgerCostReporter(store);
        store.write(entry("tenant-a", "entry-1", "request-1", false));

        assertThat(reporter.getRequestCost("request-1", "tenant-a")).isEmpty();
    }

    private static CostLedgerEntry entry(
            String tenantId, String entryId, String requestId, boolean verified) {
        return new CostLedgerEntry(
            entryId,
            requestId,
            tenantId,
            "org-1",
            null,
            null,
            "task-1",
            Instant.parse("2026-09-25T00:00:00Z"),
            1000L, 200L, 1000L, 200L, 0L, 0L, 0L, 0L, 0L,
            0.05, 0.05, 0.0, 0.0, 0.0, 0.0, 0.0,
            Map.of(),
            "USD",
            "v1",
            "model-x",
            "provider-x",
            INPUT, OUTPUT, CACHE, MODEL, TOOLS, WORKFLOW, COST, PERFORMANCE, QUALITY,
            verified);
    }
}
