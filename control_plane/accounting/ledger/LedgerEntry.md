# LedgerEntry — Contract & Design Note

**Execution unit:** `EXE-P0.1.A` (Capability 1 — Token Accounting and Cost Ledger, Sub-phase A — Contract & Design)
**Status:** Design note only — **not yet code**. No Java source, no Maven project, and no other file exists in `control_plane/` as of this commit.
**Sources:** `architecture.md` §27.1 (Standard Ledger Fields), §27.3 (Cost Model) — consumed as-is, per `docs/execution-plan.md` §18.4's `EXE-P0.1.A` row. `implementation-plan.md` §8.1 (exemplar capability treatment, cited). `docs/execution-plan.md` §18.4 (this capability's atomic-unit definition).
**Requirements covered:** `OBJ-011`, `AC-001`, `AC-002`, `AC-005` (per `docs/execution-plan.md` §18.4's capability header).

---

## 1. Scope

This note consumes `architecture.md` §27.1's ledger field list and §27.3's cost model **as-is**. No new schema is invented here — every field below is traced directly to §27.1; none is added, renamed, reorganized, or reinterpreted. This satisfies `EXE-P0.1.A`'s stated Definition of Done: *"No new schema invented; every field traced to §27.1."*

**Explicitly out of scope for this note:** `architecture.md` §27.2 (Extended Ledger — Optimization/Outcome/Context/Agent/Cache Economics fields). `docs/execution-plan.md` §18.4's `EXE-P0.1.A` row and its capability-header "Interfaces/contracts" field both cite only §27.1 and §27.3 — §27.2 is not named. Including it here would be inventing scope this sub-phase's own source citation does not grant.

## 2. Ledger field categories (verbatim from `architecture.md` §27.1)

**INPUT:**
`tokens.raw_input`, `tokens.sanitized`, `tokens.query_compressed`, `tokens.context`, `tokens.retrieved`, `tokens.pruned`, `tokens.deduplicated`, `tokens.context_compressed`, `tokens.cached_input`, `tokens.uncached_input`

**OUTPUT:**
`tokens.raw_output`, `tokens.optimized_output`, `tokens.truncated`, `tokens.expanded_retry`

**CACHE:**
`cache.exact_hits`, `cache.semantic_hits`, `cache.misses`, `cache.writes`, `cache.reads`, `cache.cacheable_tokens`, `cache.reused_tokens`

**MODEL:**
`model.selected`, `model.candidate`, `model.routing_decision`, `model.escalation`, `model.reasoning_budget`, `model.reasoning_tokens`

**TOOLS:**
`tools.calls_attempted`, `tools.calls_avoided`, `tools.output_tokens`, `tools.filtered_tokens`, `tools.cached_calls`

**WORKFLOW:**
`workflow.steps_planned`, `workflow.steps_executed`, `workflow.steps_skipped`, `workflow.early_exits`, `workflow.retries`

**COST:**
`cost.input`, `cost.output`, `cost.cache`, `cost.compression`, `cost.tool`, `cost.total_optimized`, `cost.baseline_estimated`, `cost.net_savings`, `cost.savings_pct`

**PERFORMANCE:**
`perf.e2e_latency_ms`, `perf.ttft_ms`, `perf.model_latency_ms`, `perf.compression_latency_ms`, `perf.cache_latency_ms`, `perf.tool_latency_ms`

**QUALITY:**
`quality.correctness_score`, `quality.relevance_score`, `quality.schema_compliance`, `quality.semantic_preservation`, `quality.user_task_score`, `quality.safety_validation`

Every tenant-scoped record built from this field list must carry `tenant_id` as its first namespace component, with no exception (root `CLAUDE.md` rule 4; restated in `docs/execution-plan.md` §18.4's capability header and its Sub-phase E row).

## 3. Cost model (verbatim from `architecture.md` §27.3)

```
Baseline Cost =
    Baseline Input Tokens x Input Rate
  + Baseline Output Tokens x Output Rate
  + Tool Costs
  + Other Provider Charges

Optimized Cost =
    Uncached Input Tokens x Input Rate
  + Cache Read/Write Cost
  + Compression Cost
  + Optimized Output Tokens x Output Rate
  + Tool Costs
  + Routing/Cascade Costs
  + Other Provider Charges

Net Savings = Baseline Cost - Optimized Cost

Savings % = Net Savings / Baseline Cost x 100
```

`architecture.md` §27.3 additionally requires: *"The system shall support configurable pricing rather than hard-coded prices, and provider-specific pricing dimensions."* No price is hard-coded in this note or implied by it.

Per root `CLAUDE.md` rule 5 (never fabricate savings): any term in this model that cannot be verified for a given request must be marked `unverified` and excluded from any savings computation, rather than guessed. This is a **Sub-phase H (Failure/Recovery)** concern per `docs/execution-plan.md` §18.4's own sub-phase table, not implemented in this design-only note — recorded here only so Sub-phase H's own work has this note's cost-model formulas to enforce against.

## 4. Open design question — flagged for Sub-phase B, not resolved here

`interfaces.md` §28.1 already defines an existing, different concrete schema for the same conceptual entity: `INTF-047 CostLedgerEntry`.

```
CostLedgerEntry {
  entry_id, request_id, tenant_id, organization_id, agent_id, session_id, task_id, timestamp

  // TOKEN ECONOMY
  baseline_input_tokens, baseline_output_tokens, actual_input_tokens, actual_output_tokens,
  cached_tokens, tokens_avoided_by_pruning, tokens_avoided_by_cache, tokens_avoided_by_early_exit,
  optimizer_overhead_tokens

  // COST
  baseline_cost, actual_cost, cached_cost_saved, optimizer_cost, gross_savings, net_savings, net_savings_pct

  // ATTRIBUTION
  savings_by_stage, currency, pricing_version, model_id, provider_id
}
```

**This does not match §27.1's field list field-for-field.** Different names (snake_case single terms vs. §27.1's `category.field` dot notation), different granularity (§27.1 has 7 distinct `cache.*` fields — exact hits, semantic hits, misses, writes, reads, cacheable tokens, reused tokens; `CostLedgerEntry` has none beyond `tokens_avoided_by_cache`/`cached_tokens`), and different scope (`CostLedgerEntry` has no `MODEL`, `WORKFLOW`, `PERFORMANCE`, or `QUALITY` category fields at all).

Per this sub-phase's own instruction — *"consume §27.1's ledger field list... as-is"*, *"no new schema invented"* — this note does **not** attempt to reconcile the two schemas, merge them, or pick one as authoritative over the other. That is a genuine open question this note surfaces rather than silently resolves, consistent with this repository's established `SOURCE-GAP` discipline (root `CLAUDE.md`, "Working with these documents"). Reconciling it is necessarily **Sub-phase B's problem** (Minimal Implementation), since B is the first point an actual Java class with one concrete field set must exist. This note records the discrepancy so B does not have to rediscover it from scratch.

## 5. Shared-substrate scaffolding — explicitly not performed in this commit

`docs/execution-plan.md` §18.4's capability-header "Preconditions" field states that Capability 1, as the first consumer, is where `core/interfaces/`, `core/schemas/`, `core/errors/` (`conventions.md` §2.1) and the Java 25/Spring Boot 4.1.x/Maven project scaffold (`docs/execution-plan.md` §6) get established. **This design note does not scaffold any of that.** No `pom.xml`, no Maven directory layout, no `core/` package directories, and no `package-info.java` exist in this repository as of this commit — the only file this commit creates is this design note.

This is a deliberate scope decision, not an oversight: `EXE-P0.1.A`'s own Files/Modules field names only `control_plane/accounting/ledger/LedgerEntry` (this note), and the master execution prompt's Constraint 7 (no speculative work beyond the current sub-phase) argues against pre-building infrastructure this specific unit's own row does not name. Whether the Maven/core scaffolding belongs in `EXE-P0.1.B` (Minimal Implementation) or should instead have been part of this unit is flagged explicitly in this sub-phase's completion report for human approval before `EXE-P0.1.B` begins.

## 6. Definition of Done — self-check

- [x] No new schema invented
- [x] Every field traced verbatim to §27.1 (copy-checked against the live `architecture.md` text, not reproduced from memory)
- [x] §27.2 (Extended Ledger) explicitly excluded as out of scope, not silently included
- [x] §27.3 cost model reproduced verbatim, including the configurable-pricing requirement
- [x] `INTF-047` discrepancy flagged as an open question, not silently resolved or merged
- [x] Shared-substrate scaffolding question flagged explicitly, not silently decided
