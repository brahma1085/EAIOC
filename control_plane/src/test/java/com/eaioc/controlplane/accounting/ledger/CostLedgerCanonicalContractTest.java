package com.eaioc.controlplane.accounting.ledger;

import static com.eaioc.controlplane.accounting.ledger.LedgerFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.RecordComponent;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Contract tests for {@code REM-P0.1.B-02} (Minimal Implementation Remediation of
 * {@code CONTRA-EXECPLAN-01}): {@link CostLedgerEntry} must conform to the corrected {@code INTF-047}
 * ({@code interfaces.md} §28.1) exactly as specified by the approved design
 * {@code LedgerContractReconciliation.md} §2–§8. The expected names and types below are transcribed
 * from that design's 58-row table.
 */
class CostLedgerCanonicalContractTest {

    private static final List<String> RETAINED_29 = List.of(
        "entryId", "requestId", "tenantId", "organizationId", "agentId", "sessionId", "taskId",
        "timestamp",
        "baselineInputTokens", "baselineOutputTokens", "actualInputTokens", "actualOutputTokens",
        "cachedTokens", "tokensAvoidedByPruning", "tokensAvoidedByCache",
        "tokensAvoidedByEarlyExit", "optimizerOverheadTokens",
        "baselineCost", "actualCost", "cachedCostSaved", "optimizerCost", "grossSavings",
        "netSavings", "netSavingsPct",
        "savingsByStage", "currency", "pricingVersion", "modelId", "providerId");

    private static final List<String> GROUPS = List.of(
        "input", "output", "cache", "model", "tools", "workflow", "cost", "performance", "quality");

    @Test
    void entry_hasRetained29_thenNineCanonicalGroups_thenVerified_inOrder() {
        List<String> names = componentNames(CostLedgerEntry.class);

        assertThat(names).hasSize(29 + 9 + 1);
        assertThat(names.subList(0, 29)).containsExactlyElementsOf(RETAINED_29);
        assertThat(names.subList(29, 38)).containsExactlyElementsOf(GROUPS);
        assertThat(names.get(38)).isEqualTo("verified");
    }

    @Test
    void retainedFields_keepTheirOriginalTypes() {
        Map<String, Class<?>> types = componentTypes(CostLedgerEntry.class);

        for (String n : List.of("entryId", "requestId", "tenantId", "organizationId", "agentId",
            "sessionId", "taskId", "currency", "pricingVersion", "modelId", "providerId")) {
            assertThat(types.get(n)).as(n).isEqualTo(String.class);
        }
        assertThat(types.get("timestamp")).isEqualTo(Instant.class);
        for (String n : RETAINED_29.subList(8, 17)) {
            assertThat(types.get(n)).as(n).isEqualTo(long.class);
        }
        for (String n : RETAINED_29.subList(17, 24)) {
            assertThat(types.get(n)).as(n).isEqualTo(double.class);
        }
        assertThat(types.get("savingsByStage")).isEqualTo(Map.class);
        assertThat(types.get("verified")).isEqualTo(boolean.class);
    }

    @Test
    void everyCanonicalGroup_matchesTheApprovedDesignExactly_58Members() {
        assertGroup(CostLedgerEntry.Input.class,
            "rawInput", long.class, "sanitized", long.class, "queryCompressed", long.class,
            "context", long.class, "retrieved", long.class, "pruned", long.class,
            "deduplicated", long.class, "contextCompressed", long.class, "cachedInput", long.class,
            "uncachedInput", long.class);
        assertGroup(CostLedgerEntry.Output.class,
            "rawOutput", long.class, "optimizedOutput", long.class, "truncated", long.class,
            "expandedRetry", long.class);
        assertGroup(CostLedgerEntry.Cache.class,
            "exactHits", long.class, "semanticHits", long.class, "misses", long.class,
            "writes", long.class, "reads", long.class, "cacheableTokens", long.class,
            "reusedTokens", long.class);
        assertGroup(CostLedgerEntry.Model.class,
            "selected", String.class, "candidate", String.class, "routingDecision", String.class,
            "escalation", boolean.class, "reasoningBudget", String.class,
            "reasoningTokens", Long.class);
        assertGroup(CostLedgerEntry.Tools.class,
            "callsAttempted", long.class, "callsAvoided", long.class, "outputTokens", long.class,
            "filteredTokens", long.class, "cachedCalls", long.class);
        assertGroup(CostLedgerEntry.Workflow.class,
            "stepsPlanned", long.class, "stepsExecuted", long.class, "stepsSkipped", long.class,
            "earlyExits", long.class, "retries", long.class);
        assertGroup(CostLedgerEntry.Cost.class,
            "input", double.class, "output", double.class, "cache", double.class,
            "compression", double.class, "tool", Double.class, "totalOptimized", double.class,
            "baselineEstimated", double.class, "netSavings", double.class, "savingsPct", double.class);
        assertGroup(CostLedgerEntry.Performance.class,
            "e2eLatencyMs", long.class, "ttftMs", long.class, "modelLatencyMs", long.class,
            "compressionLatencyMs", long.class, "cacheLatencyMs", long.class,
            "toolLatencyMs", long.class);
        assertGroup(CostLedgerEntry.Quality.class,
            "correctnessScore", double.class, "relevanceScore", double.class,
            "schemaCompliance", boolean.class, "semanticPreservation", double.class,
            "userTaskScore", double.class, "safetyValidation", boolean.class);

        int total = List.of(CostLedgerEntry.Input.class, CostLedgerEntry.Output.class,
                CostLedgerEntry.Cache.class, CostLedgerEntry.Model.class, CostLedgerEntry.Tools.class,
                CostLedgerEntry.Workflow.class, CostLedgerEntry.Cost.class,
                CostLedgerEntry.Performance.class, CostLedgerEntry.Quality.class)
            .stream().mapToInt(c -> c.getRecordComponents().length).sum();
        assertThat(total).isEqualTo(58);
    }

    @Test
    void onlyTheTwoWhereAvailableMembers_areNullableTypes() {
        List<String> boxed = new java.util.ArrayList<>();
        for (Class<?> g : List.of(CostLedgerEntry.Input.class, CostLedgerEntry.Output.class,
            CostLedgerEntry.Cache.class, CostLedgerEntry.Model.class, CostLedgerEntry.Tools.class,
            CostLedgerEntry.Workflow.class, CostLedgerEntry.Cost.class,
            CostLedgerEntry.Performance.class, CostLedgerEntry.Quality.class)) {
            for (RecordComponent c : g.getRecordComponents()) {
                if (c.getType() == Long.class || c.getType() == Double.class) {
                    boxed.add(g.getSimpleName() + "." + c.getName());
                }
            }
        }
        assertThat(boxed).containsExactlyInAnyOrder("Model.reasoningTokens", "Cost.tool");
    }

    @Test
    void ac005_sixCategories_areRecordedDistinctly_andRoundTripThroughTheStore() {
        CostLedgerEntry.Input input = new CostLedgerEntry.Input(
            1000L, 990L, 11L, 700L, 13L, 17L, 19L, 23L, 29L, 971L);  // compressed 11/23, retrieved 13,
                                                                  // removed 17/19, cached 29
        CostLedgerEntry.Output output = new CostLedgerEntry.Output(31L, 37L, 41L, 0L); // generated 31/37,
                                                                                    // removed 41
        CostLedgerEntry.Cache cache = new CostLedgerEntry.Cache(1L, 0L, 0L, 0L, 1L, 60L, 43L); // reused 43
        CostLedgerStore store = new CostLedgerStore();

        store.write(entry("t-ac5", "e-ac5", input, output, cache, MODEL, COST, true));
        CostLedgerEntry read = store.read("t-ac5", "e-ac5").orElseThrow();

        assertThat(read.input().cachedInput()).isEqualTo(29L);                 // cached
        assertThat(read.input().queryCompressed()).isEqualTo(11L);             // compressed
        assertThat(read.input().contextCompressed()).isEqualTo(23L);           // compressed
        assertThat(read.input().pruned()).isEqualTo(17L);                      // removed
        assertThat(read.input().deduplicated()).isEqualTo(19L);                // removed
        assertThat(read.output().truncated()).isEqualTo(41L);                  // removed
        assertThat(read.output().rawOutput()).isEqualTo(31L);                  // generated
        assertThat(read.output().optimizedOutput()).isEqualTo(37L);            // generated
        assertThat(read.input().retrieved()).isEqualTo(13L);                   // retrieved
        assertThat(read.cache().reusedTokens()).isEqualTo(43L);                // reused
    }

    @Test
    void whereAvailableMembers_acceptNull_andStayNull_neverZero_whileEntryStaysVerified() {
        CostLedgerEntry.Model model =
            new CostLedgerEntry.Model("m", "m", "route", false, "medium", null);
        CostLedgerEntry.Cost cost =
            new CostLedgerEntry.Cost(0.01, 0.01, 0.0, 0.0, null, 0.02, 0.03, 0.01, 33.3);

        CostLedgerEntry entry = entry("t", "e", INPUT, OUTPUT, CACHE, model, cost, true);

        assertThat(entry.model().reasoningTokens()).isNull();
        assertThat(entry.cost().tool()).isNull();
        assertThat(entry.verified()).isTrue();   // HD-4: a where-available null is not "unverified"
    }

    @Test
    void nullGroup_isRejected() {
        assertThatThrownBy(() -> new CostLedgerEntry(
            "e", "r", "t", "o", null, null, "task", Instant.parse("2026-09-23T00:00:00Z"),
            0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            Map.of(), "USD", "v1", "m", "p",
            INPUT, OUTPUT, null, MODEL, TOOLS, WORKFLOW, COST, PERFORMANCE, QUALITY, true))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("cache");
    }

    @Test
    void nullNonNullableModelString_isRejected() {
        assertThatThrownBy(() -> new CostLedgerEntry.Model(null, "c", "r", false, "b", 1L))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new CostLedgerEntry.Model("s", null, "r", false, "b", 1L))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new CostLedgerEntry.Model("s", "c", null, false, "b", 1L))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new CostLedgerEntry.Model("s", "c", "r", false, null, 1L))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void retainedFields_areNotAliasesOfCanonicalMembers() {
        // D-2: retained netSavings / modelId and canonical cost.netSavings / model.selected are
        // independent values — setting them differently must survive a store round-trip unchanged.
        CostLedgerEntry.Model model =
            new CostLedgerEntry.Model("canonical-model", "cand", "route", false, "low", 5L);
        CostLedgerEntry.Cost cost =
            new CostLedgerEntry.Cost(0.01, 0.01, 0.0, 0.0, 0.0, 0.02, 0.05, 0.777, 10.0);
        CostLedgerStore store = new CostLedgerStore();

        store.write(entry("t-alias", "e-alias", INPUT, OUTPUT, CACHE, model, cost, true));
        CostLedgerEntry read = store.read("t-alias", "e-alias").orElseThrow();

        assertThat(read.netSavings()).isEqualTo(0.5);           // retained field (see entry())
        assertThat(read.cost().netSavings()).isEqualTo(0.777);  // canonical member
        assertThat(read.modelId()).isEqualTo("retained-model");
        assertThat(read.model().selected()).isEqualTo("canonical-model");
    }

    @Test
    void unverifiedFallback_usesDb1Placeholders_nullWhereAvailable_andIsUnverified() {
        CostLedgerEntry fb = CostLedgerEntry.unverifiedFallback(
            "e-fb", "r-fb", "t-fb", "o", "task", Instant.parse("2026-09-23T00:00:00Z"));

        assertThat(fb.verified()).isFalse();
        assertThat(fb.model().reasoningTokens()).isNull();
        assertThat(fb.cost().tool()).isNull();
        assertThat(fb.model().selected()).isEqualTo("unknown");
        assertThat(fb.model().escalation()).isFalse();
        assertThat(fb.quality().safetyValidation()).isFalse();
        assertThat(fb.input().retrieved()).isZero();
        assertThat(fb.cost().netSavings()).isZero();
        assertThat(fb.performance().ttftMs()).isZero();
    }

    private static CostLedgerEntry entry(
        String tenantId, String entryId,
        CostLedgerEntry.Input input, CostLedgerEntry.Output output, CostLedgerEntry.Cache cache,
        CostLedgerEntry.Model model, CostLedgerEntry.Cost cost, boolean verified) {
        return new CostLedgerEntry(
            entryId, "req-" + entryId, tenantId, "org-1", null, null, "task-1",
            Instant.parse("2026-09-23T00:00:00Z"),
            1000L, 200L, 900L, 200L, 0L, 100L, 0L, 0L, 0L,
            0.10, 0.09, 0.0, 0.0, 0.5, 0.5, 5.0,
            Map.of(), "USD", "v1", "retained-model", "provider-x",
            input, output, cache, model, TOOLS, WORKFLOW, cost, PERFORMANCE, QUALITY,
            verified);
    }

    private static List<String> componentNames(Class<? extends Record> type) {
        return Arrays.stream(type.getRecordComponents()).map(RecordComponent::getName).toList();
    }

    private static Map<String, Class<?>> componentTypes(Class<? extends Record> type) {
        Map<String, Class<?>> m = new LinkedHashMap<>();
        for (RecordComponent c : type.getRecordComponents()) {
            m.put(c.getName(), c.getType());
        }
        return m;
    }

    private static void assertGroup(Class<? extends Record> group, Object... nameTypePairs) {
        List<String> expectedNames = new java.util.ArrayList<>();
        Map<String, Class<?>> expectedTypes = new LinkedHashMap<>();
        for (int i = 0; i < nameTypePairs.length; i += 2) {
            expectedNames.add((String) nameTypePairs[i]);
            expectedTypes.put((String) nameTypePairs[i], (Class<?>) nameTypePairs[i + 1]);
        }
        assertThat(componentNames(group)).as(group.getSimpleName()).containsExactlyElementsOf(expectedNames);
        assertThat(componentTypes(group)).as(group.getSimpleName()).isEqualTo(expectedTypes);
    }
}
