# `REM-P0.1.B-01` — Token Accounting and Cost Ledger: Contract / Design Reconciliation (Remediation)

Per `docs/execution-plan.md` v1.0.6 §18.14.4. This is a **remediation unit**, not an `EXE-P0` unit. It sits outside the 48/54 counts and does not rewrite historical commit `e353dcf` (`EXE-P0.1.B`). It closes the design half of `CONTRA-EXECPLAN-01`: `CostLedgerEntry` implements the pre-correction `INTF-047` (29 fields + `verified`) and lacks the 58 `architecture.md` §27.1 Standard Ledger Fields that the corrected `interfaces.md` §28.1 now requires.

This is a design note only, with no code. `REM-P0.1.B-02` implements exactly what this note specifies, once it is approved and once §9's source corrections are applied.

**Contract basis (HEAD `4b0ca87`):** corrected `interfaces.md` §28.1 (nine nested canonical groups; 29 retained fields; OD-28.1-A/B), `conventions.md` §3.6, §5.2, §5.4, §14.1, §14.4, and `execution-plan.md` §18.4 (B row items 1–7) and §18.14.4.

---

## 1. Human decisions recorded in this unit

§18.14.4 requires the 22 `SOURCE-UNRESOLVED` types (`SOURCE-GAP-EXECPLAN-05`) and the availability-vs-`unverified` behavior (`SOURCE-GAP-EXECPLAN-06`) to be **decided by the human, not assumed**. The operator decided them in this session, before this note was written:

| ID | Scope | Decision |
|---|---|---|
| HD-1 | 13 counters: `cache.exact_hits`, `cache.semantic_hits`, `cache.misses`, `cache.writes`, `cache.reads`; `tools.calls_attempted`, `tools.calls_avoided`, `tools.cached_calls`; `workflow.steps_planned`, `workflow.steps_executed`, `workflow.steps_skipped`, `workflow.early_exits`, `workflow.retries` | `integer`. A true zero (no hits, no retries) is a real measured value |
| HD-2 | 5 MODEL members | `model.selected` = `string`; `model.candidate` = `string`; `model.routing_decision` = `string` (a decision label; **no enum invented**); `model.escalation` = `boolean` (whether escalation occurred); `model.reasoning_budget` = `string` (**no enum assumed**; `OptimizationPlan.reasoning_budget`'s enum is not reused) |
| HD-3 | 4 remaining members | `performance.ttft_ms` = `integer` (milliseconds); `quality.semantic_preservation` = `float` in `[0.0, 1.0]`; `quality.schema_compliance` = `boolean` (pass/fail); `quality.safety_validation` = `boolean` (pass/fail) |
| HD-4 | Nullability / `unverified` (`SOURCE-GAP-EXECPLAN-06`) | **Only** the two PS §8 "where available" members, `model.reasoning_tokens` and `cost.tool`, may be `null`. A `null` there does **not** by itself make the entry `unverified`. Every other canonical member must carry a measured value; **if it cannot be measured, the entry is marked `unverified`** |

These are operator decisions. They are not claimed as source-derived.

---

## 2. Complete canonical member specification (58 members)

Java component name = camelCase of the `interfaces.md` §28.1 member. Java types follow the existing `CostLedgerEntry` precedent:
- `integer` → `long`, and `integer | null` → `Long` (the ledger already uses `long` for token counts);
- `float` → `double`, and `float | null` → `Double`;
- `boolean` → `boolean`; `string` → `String`.

| Group | §27.1 field | INTF member | Type (basis) | Java component | Java type | Nullable |
|---|---|---|---|---|---|---|
| input | `tokens.raw_input` | `raw_input` | integer (CONV §5.2) | `rawInput` | `long` | No |
| input | `tokens.sanitized` | `sanitized` | integer (CONV §5.2) | `sanitized` | `long` | No |
| input | `tokens.query_compressed` | `query_compressed` | integer (CONV §5.2) | `queryCompressed` | `long` | No |
| input | `tokens.context` | `context` | integer (CONV §5.2) | `context` | `long` | No |
| input | `tokens.retrieved` | `retrieved` | integer (CONV §5.2) | `retrieved` | `long` | No |
| input | `tokens.pruned` | `pruned` | integer (CONV §5.2) | `pruned` | `long` | No |
| input | `tokens.deduplicated` | `deduplicated` | integer (CONV §5.2) | `deduplicated` | `long` | No |
| input | `tokens.context_compressed` | `context_compressed` | integer (CONV §5.2) | `contextCompressed` | `long` | No |
| input | `tokens.cached_input` | `cached_input` | integer (CONV §5.2) | `cachedInput` | `long` | No |
| input | `tokens.uncached_input` | `uncached_input` | integer (CONV §5.2) | `uncachedInput` | `long` | No |
| output | `tokens.raw_output` | `raw_output` | integer (CONV §5.2) | `rawOutput` | `long` | No |
| output | `tokens.optimized_output` | `optimized_output` | integer (CONV §5.2) | `optimizedOutput` | `long` | No |
| output | `tokens.truncated` | `truncated` | integer (CONV §5.2) | `truncated` | `long` | No |
| output | `tokens.expanded_retry` | `expanded_retry` | integer (CONV §5.2) | `expandedRetry` | `long` | No |
| cache | `cache.exact_hits` | `exact_hits` | integer (HD-1) | `exactHits` | `long` | No |
| cache | `cache.semantic_hits` | `semantic_hits` | integer (HD-1) | `semanticHits` | `long` | No |
| cache | `cache.misses` | `misses` | integer (HD-1) | `misses` | `long` | No |
| cache | `cache.writes` | `writes` | integer (HD-1) | `writes` | `long` | No |
| cache | `cache.reads` | `reads` | integer (HD-1) | `reads` | `long` | No |
| cache | `cache.cacheable_tokens` | `cacheable_tokens` | integer (CONV §3.6) | `cacheableTokens` | `long` | No |
| cache | `cache.reused_tokens` | `reused_tokens` | integer (CONV §3.6) | `reusedTokens` | `long` | No |
| model | `model.selected` | `selected` | string (HD-2) | `selected` | `String` | No |
| model | `model.candidate` | `candidate` | string (HD-2) | `candidate` | `String` | No |
| model | `model.routing_decision` | `routing_decision` | string (HD-2) | `routingDecision` | `String` | No |
| model | `model.escalation` | `escalation` | boolean (HD-2) | `escalation` | `boolean` | No |
| model | `model.reasoning_budget` | `reasoning_budget` | string (HD-2) | `reasoningBudget` | `String` | No |
| model | `model.reasoning_tokens` | `reasoning_tokens` | integer \| null (CONV §3.6, §5.4; PS §8 "where available") | `reasoningTokens` | `Long` | **Yes** |
| tools | `tools.calls_attempted` | `calls_attempted` | integer (HD-1) | `callsAttempted` | `long` | No |
| tools | `tools.calls_avoided` | `calls_avoided` | integer (HD-1) | `callsAvoided` | `long` | No |
| tools | `tools.output_tokens` | `output_tokens` | integer (CONV §3.6) | `outputTokens` | `long` | No |
| tools | `tools.filtered_tokens` | `filtered_tokens` | integer (CONV §3.6) | `filteredTokens` | `long` | No |
| tools | `tools.cached_calls` | `cached_calls` | integer (HD-1) | `cachedCalls` | `long` | No |
| workflow | `workflow.steps_planned` | `steps_planned` | integer (HD-1) | `stepsPlanned` | `long` | No |
| workflow | `workflow.steps_executed` | `steps_executed` | integer (HD-1) | `stepsExecuted` | `long` | No |
| workflow | `workflow.steps_skipped` | `steps_skipped` | integer (HD-1) | `stepsSkipped` | `long` | No |
| workflow | `workflow.early_exits` | `early_exits` | integer (HD-1) | `earlyExits` | `long` | No |
| workflow | `workflow.retries` | `retries` | integer (HD-1) | `retries` | `long` | No |
| cost | `cost.input` | `input` | float (CONV §3.6, §5.2) | `input` | `double` | No |
| cost | `cost.output` | `output` | float (CONV §3.6, §5.2) | `output` | `double` | No |
| cost | `cost.cache` | `cache` | float (CONV §3.6, §5.2) | `cache` | `double` | No |
| cost | `cost.compression` | `compression` | float (CONV §3.6, §5.2) | `compression` | `double` | No |
| cost | `cost.tool` | `tool` | float \| null (CONV §5.4; PS §8 "where available") | `tool` | `Double` | **Yes** |
| cost | `cost.total_optimized` | `total_optimized` | float (CONV §3.6, §5.2) | `totalOptimized` | `double` | No |
| cost | `cost.baseline_estimated` | `baseline_estimated` | float (CONV §3.6, §5.2) | `baselineEstimated` | `double` | No |
| cost | `cost.net_savings` | `net_savings` | float (CONV §5.2 `*_savings`) | `netSavings` | `double` | No |
| cost | `cost.savings_pct` | `savings_pct` | float (ARCH §27.3 formula) | `savingsPct` | `double` | No |
| performance | `perf.e2e_latency_ms` | `e2e_latency_ms` | integer (CONV §5.2 `latency_ms`) | `e2eLatencyMs` | `long` | No |
| performance | `perf.ttft_ms` | `ttft_ms` | integer (HD-3) | `ttftMs` | `long` | No |
| performance | `perf.model_latency_ms` | `model_latency_ms` | integer (CONV §5.2) | `modelLatencyMs` | `long` | No |
| performance | `perf.compression_latency_ms` | `compression_latency_ms` | integer (CONV §5.2) | `compressionLatencyMs` | `long` | No |
| performance | `perf.cache_latency_ms` | `cache_latency_ms` | integer (CONV §5.2) | `cacheLatencyMs` | `long` | No |
| performance | `perf.tool_latency_ms` | `tool_latency_ms` | integer (CONV §5.2) | `toolLatencyMs` | `long` | No |
| quality | `quality.correctness_score` | `correctness_score` | float [0.0, 1.0] (CONV §5.2) | `correctnessScore` | `double` | No |
| quality | `quality.relevance_score` | `relevance_score` | float [0.0, 1.0] (CONV §5.2) | `relevanceScore` | `double` | No |
| quality | `quality.schema_compliance` | `schema_compliance` | boolean (HD-3) | `schemaCompliance` | `boolean` | No |
| quality | `quality.semantic_preservation` | `semantic_preservation` | float [0.0, 1.0] (HD-3) | `semanticPreservation` | `double` | No |
| quality | `quality.user_task_score` | `user_task_score` | float [0.0, 1.0] (CONV §5.2) | `userTaskScore` | `double` | No |
| quality | `quality.safety_validation` | `safety_validation` | boolean (HD-3) | `safetyValidation` | `boolean` | No |

Totals: 58 members — 36 typed from the corpus and 22 typed by HD-1 to HD-3; 2 nullable (HD-4). None remains `SOURCE-UNRESOLVED` after §9's source correction.

---

## 3. Java realization of `CostLedgerEntry`

- **Nine nested records**, declared inside `CostLedgerEntry` and named after the groups: `CostLedgerEntry.Input`, `.Output`, `.Cache`, `.Model`, `.Tools`, `.Workflow`, `.Cost`, `.Performance`, `.Quality`. Each has exactly the components in §2, in §27.1 source order. They are immutable records with no Spring and no provider-specific type (root `CLAUDE.md` rule 1).
- **Component order of `CostLedgerEntry`:** the 29 retained `INTF-047` components come first, **unchanged in name, type and order**. They are followed by nine group components (`input`, `output`, `cache`, `model`, `tools`, `workflow`, `cost`, `performance`, `quality`), then `verified`, which stays last. All nine group names are legal Java identifiers, so none needs renaming.
- **No alias (D-2):** no retained field is derived from, copied into, or validated against any group member. `netSavings` (retained) and `cost().netSavings()` (canonical) are independent values, and so are `modelId` and `model().selected()`. No constructor, factory or test may assert they are equal.
- **Compact-constructor rules,** in addition to the existing tenant check, `entryId`/`requestId`/`timestamp` checks and the `savingsByStage` copy, which are unchanged:
  - each of the nine group components must be non-null (`Objects.requireNonNull`), because every group is part of the complete contract (CONV §14.1);
  - in `Model`, the four non-nullable `String` members must be non-null, because HD-4 forbids nulls outside the two "where available" members;
  - no further value validation (ranges, non-negativity) is added, because no source or decision specifies it.

---

## 4. `verified` semantics and unmeasurable members (HD-4)

- **What `verified = true` asserts:** every non-nullable canonical member carries a measured value, and the accounting computation succeeded (CONV §14.1, §14.4, root rule 5).
- **A `null` in a "where available" member** (`model.reasoningTokens`, `cost.tool`) is compatible with `verified = true` (HD-4).
- **When a non-nullable member cannot be measured,** the producer must set `verified = false`. The ledger cannot tell a measured value from a placeholder, so this responsibility sits with the caller that builds the entry, exactly as it already does for the retained cost fields.

**DB-1 — placeholder values for unmeasurable non-nullable members (design decision, needs your approval).** A non-nullable Java component must hold *some* value even when it can't be measured. This design follows the existing `unverifiedFallback()` precedent:

| Java type | Placeholder |
|---|---|
| numeric | `0` / `0.0` |
| `String` | `"unknown"` |
| `boolean` | `false` |

The entry is always marked `verified = false` when any placeholder is used, so the placeholder can never be counted as a real figure (rule 5; `SCN-AUDIT-003`).

**Risk to weigh before approving:**
- **Misleading booleans:** a placeholder `false` in `quality.schemaCompliance`, `quality.safetyValidation` or `model.escalation` could be misread as a real "failed" or "not escalated" if someone ignores `verified`.
- **Why it stays safe:** all verified-only consumers (`LedgerObservability`'s `cost.saved`, savings sums) already gate on `verified`.

**Consequence of HD-4 at P0, stated explicitly:**
- **Quality group:** no P0 capability runs a quality-gate measurement (`execution-plan.md` §28; every Sub-phase F is `NOT APPLICABLE`). So `quality.correctnessScore`, `relevanceScore`, `semanticPreservation`, `userTaskScore`, `schemaCompliance` and `safetyValidation` can't be measured for a real P0 request.
- **Other groups:** the same applies to `model.candidate` when no routing happens.
- **Result:** under HD-4, **every realistic P0 ledger entry will be `verified = false`**, and verified savings reporting will read zero at P0.
- **Is that acceptable?** It is conservative and consistent with rule 5, not a defect. But it is a direct effect of choosing HD-4 over "all members nullable". If that effect is not intended, HD-4 should be revisited now, before `REM-P0.1.B-02`, rather than discovered later.

---

## 5. `unverifiedFallback()` extension

`CostLedgerEntry.unverifiedFallback(...)` keeps its signature and its guarantees: record preserved, nothing guessed, `verified` forced `false`. It additionally populates all nine groups:
- every non-nullable member with its DB-1 placeholder;
- `model.reasoningTokens` and `cost.tool` with `null`, not zero (D-3/HD-4).

---

## 6. Unchanged components

- **`CostLedgerStore`:** unchanged. It is append-only and tenant-first, and stores the whole record.
- **`LedgerObservability`:** unchanged. It keeps emitting its sourced metric names from the retained fields (`actualInputTokens`, `actualOutputTokens`, `tokensAvoidedBy*`, `actualCost`, `netSavings`, `verified`). Re-pointing any metric at a canonical member would amount to declaring an equivalence (D-2), which no source supports, so it is out of scope.
- **Tenant isolation:** unchanged and structural (root rule 4).

---

## 7. Construction sites `REM-P0.1.B-02` must update

There are 9 `new CostLedgerEntry(` sites:
- 1 in `CostLedgerEntry.unverifiedFallback`;
- 8 in test code: `CostLedgerStoreTest` ×2, `LedgerObservabilityTest` ×2, `CostLedgerFailureRecoveryTest`, `CostLedgerScenarioValidationTest`, `CostLedgerTenantIsolationTest`, `StubPipelineStageCaller`.

The unit may add one package-private test fixture in `src/test/java/.../accounting/ledger/` that builds a fully-populated set of nine groups, so the existing tests don't each repeat 58 values. No existing test's assertions may be weakened.

---

## 8. Contract tests `REM-P0.1.B-02` must add

- **Structure:** `CostLedgerEntry` has exactly the nine group components, with the names in §3, between `providerId` and `verified`. Each group record's component names and Java types equal §2's table exactly (58 members; checked by reflection).
- **Retained fields:** the 29 retained components keep their names and types, and there is still exactly one `verified`.
- **AC-005:** one entry with distinct non-zero values for `input.cachedInput`, `input.queryCompressed` / `input.contextCompressed`, `input.retrieved`, `cache.reusedTokens`, `input.pruned`/`deduplicated` / `output.truncated`, and `output.rawOutput`/`optimizedOutput` round-trips through `CostLedgerStore` with every value readable separately.
- **Nullability:** `model.reasoningTokens` and `cost.tool` accept `null` and stay `null` (never zero). A null group, or a null non-nullable `Model` string, is rejected.
- **No alias:** setting `netSavings` and `cost.netSavings` (and `modelId` and `model.selected`) to different values keeps both unchanged after a round-trip.
- **Fallback:** `unverifiedFallback()` yields `verified = false`, DB-1 placeholders, and `null` for the two nullable members.
- **Regression:** all pre-existing tests (70 at HEAD) pass unchanged in intent.

---

## 9. Source corrections required before `REM-P0.1.B-02`

Per `execution-plan.md` §18.14.4 ("Any type or behavior the human decides must also be applied to `interfaces.md` §28.1 through a user-directed source correction before `REM-P0.1.B-02` begins"). These are **not** made by this unit, whose file scope is this note only:

1. **`interfaces.md` §28.1:** replace the 22 `SOURCE-UNRESOLVED` types with HD-1 to HD-3's types. Resolve OD-28.1-A (by the operator's decision) and OD-28.1-B (HD-4).
2. **`conventions.md` §14.1:** replace the "not established … must not be assumed" availability sentence with HD-4's rule.
3. **`execution-plan.md` (and runbook):** mark `SOURCE-GAP-EXECPLAN-05`/`-06` as resolved by the operator's decisions, and record HD-1 to HD-4 (a patch revision).

If DB-1 or HD-4 is changed at approval, these corrections change with it.

---

## 10. Definition of Done — self-check

- [x] Design based strictly on the corrected `interfaces.md` §28.1: all nine groups and all 58 members, in §27.1 order.
- [x] The 29 retained `INTF-047` fields are unchanged, and none is aliased (D-2).
- [x] Every `SOURCE-UNRESOLVED` type (22) and the `SOURCE-GAP-EXECPLAN-06` behavior were decided by the operator (HD-1 to HD-4). None was assumed.
- [x] `verified` / `unverifiedFallback()` cover the new groups without fabricated values. The one new design choice (DB-1 placeholders) and HD-4's P0 consequence are surfaced for approval.
- [x] Contract tests, construction sites and file scope for `REM-P0.1.B-02` are specified.
- [x] Required follow-up source corrections are listed (§9) and were not performed here.
- [x] No code, test or other file was changed.
