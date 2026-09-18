# Enterprise Agent & LLM Inference Optimization Control Plane — Implementation Plan

**Document ID:** EAIOC-IMPL-001
**Status:** PRE-IMPLEMENTATION — Level 0 (RESEARCH)
**Version:** 1.0.0
**Generated:** 2026-09-18 (per repository date)
**Generation prompt:** `docs/prompts/generate-implementation-plan.prompt.md` (V2 Consolidated Master Prompt)

**Position in the documentation chain:** Eleventh generated downstream document, following `optimization-catalog.md` → `provider-matrix.md` → `cache-strategy.md` → `agent-optimization.md` → `inference-optimization.md` → `quality-gates.md` → `security.md` → `observability.md` → `eval.md` → `SCALING.md`. Gated by each of those ten documents' own passing readiness verdict, assumed here rather than re-verified. This is the last major planning artifact before `requirements-traceability.md` and ADRs — neither is generated or cascaded into here.

**Sources read in full or by targeted section/ID lookup:** root `CLAUDE.md`; `architecture.md` §36 (Implementation Priority — exact current P0–P5 lists), §33/§34/§44 (cited only); `conventions.md` §25 (Implementation Priority Conventions — verified identical to §36), §27 (cited); `optimization-catalog.md` (all 20 `TECH-NNN` Implementation Priority fields, all 25 `DA-NNN` Implementation Priority fields, `SOURCE-GAP-OPTCAT-01`); `agent-optimization.md` (§8 CL-007 Session-Phase Policy table, `SOURCE-GAP-AGENTOPT-01`); `scenario-matrix.md` (§1 Scenario ID System, §48 Final Report's P0–P3 risk-priority distribution — verified as a distinct scale from `architecture.md` §36); `edge-cases.md` (`EC-077`, `EC-191`–`194` verified directly; searched for build-sequencing coverage — none found); `security.md`, `quality-gates.md`, `eval.md`, `observability.md`, `SCALING.md` (each consumed for its own already-owned boundary, not redefined).

**Scope boundary reminder:** This document owns build sequencing across the six `architecture.md` §36 Implementation Priority tiers (P0–P5), inter/intra-tier dependency ordering, capability-level decomposition, CI/CD/build-pipeline sequencing, and the explicit resolution of the two source gaps named above. It does **not** own `TECH-NNN`/`DA-NNN`/P5-Index schemas (`optimization-catalog.md`), rollout-phase mechanics (`eval.md`), deployment-mode capacity/scaling (`SCALING.md`), validation-matrix/statistical methodology (`eval.md`/`quality-gates.md`), security/governance mechanisms (`security.md`), observability implementation (`observability.md`), cross-document requirements traceability (`requirements-traceability.md`, not yet generated), or architectural decision rationale (ADRs, not yet generated) — all cited, none redefined.

---

## 1. Status and Maturity

This document is **Level 0 — RESEARCH / PRE-IMPLEMENTATION**, consistent with every other document in this chain. No line of source code exists. Accordingly:

- Every capability-decomposition step, dependency claim, or gate criterion not directly stated by an upstream source is tagged `PROPOSED IMPLEMENTATION` (a decomposition/sequencing choice) or `PROPOSED DEFAULT — VALIDATE LOCALLY` (a concrete number/threshold).
- No source code, staffing plan, timeline, sprint number, delivery date, technology/vendor selection, cloud infrastructure, or database/broker choice is stated anywhere in this document. Where a technology decision is genuinely required before implementation can proceed, it is recorded in §24 (Implementation Decision Register) or §25 (ADR Candidates), never invented here.
- **Implementation Priority (this document's subject) is never a validation or maturity claim.** Every `TECH-NNN`/`DA-NNN`/`P5 Index` entry's Maturity Level remains `0 — RESEARCH` (per `optimization-catalog.md`'s own maturity note) regardless of its P0–P5 tier. P0 means "buildable first, no prerequisite," not "proven" or "safe to trust."

## 2. How to Read This Document

Every claim carries one of: `SOURCE-DEFINED` (an upstream document states this exactly), `SOURCE-DERIVED` (directly implied by combining source requirements), `PROPOSED IMPLEMENTATION` (a decomposition/sequencing choice this document introduces because no source specifies one), or `PROPOSED DEFAULT — VALIDATE LOCALLY` (a specific number this document proposes for the same reason). None of the `PROPOSED` labels are authoritative — they are starting points for engineering review once implementation actually begins.

## 3. Scope Boundary

| Concern | Owner |
|---|---|
| `TECH-NNN`/`DA-NNN`/`P5 Index` schemas, applicability, algorithms | `optimization-catalog.md`/`agent-optimization.md`/`inference-optimization.md` (cited, not redefined) |
| Rollout-phase mechanics (OBSERVE→...→CONTINUOUS OPTIMIZATION), statistical/benchmarking methodology | `eval.md` (cited, not redefined) |
| Deployment-mode capacity, throughput targets, horizontal-scaling triggers | `SCALING.md` (cited, not redefined) |
| Quality dimension methodology, VCL | `quality-gates.md` (cited, not redefined) |
| Security/governance mechanisms (SGE/DGE/TMG/HAG/CIS) | `security.md` (cited, not redefined) |
| Telemetry/dashboard implementation | `observability.md` (cited, not redefined) |
| Cross-document requirements traceability | `requirements-traceability.md` (not yet generated — acknowledged as planned) |
| Architectural decision rationale | ADRs (not yet generated — acknowledged as planned, candidates listed in §25) |
| **Build sequencing across P0–P5, inter/intra-tier dependency ordering, capability-level decomposition, CI/CD sequencing, `DA-NNN` priority resolution** | **This document** |

## 4. Three Distinct Priority/Maturity Concepts — Do Not Conflate

Three different scales in this repository use similar-looking labels for unrelated purposes:

| Scale | Source | Meaning | Values |
|---|---|---|---|
| **Implementation Priority** | `architecture.md` §36 / `conventions.md` §25 (this document's subject) | When a capability should be *built* relative to others | P0–P5 |
| **Maturity Level** | `optimization-catalog.md` / `conventions.md` §20.3 | How *validated* a capability is against local benchmark evidence | 0 (Research) – 5 |
| **Scenario/Test Priority** | `scenario-matrix.md` §1/§48 | How *risk-critical* a test scenario is (verified directly: §48's Final Report states "Highest-Risk (P0) Scenarios: 95 scenarios... reflecting their security-criticality") | P0–P3 |

`CONTRA-OPTCAT-01` already resolved the first collision (Priority vs. Maturity) for `optimization-catalog.md` by keeping the two fields separate; this document inherits that discipline. The second collision (`scenario-matrix.md`'s P0–P3 test-risk scale vs. `architecture.md` §36's P0–P5 build-sequencing scale) is verified here directly: `scenario-matrix.md`'s P0 means "highest security/risk criticality for testing," has no P4/P5 tier at all, and is assigned per-scenario — it has no relationship to which *tier of the catalog* a technique belongs to. A P0 test-priority scenario can validate a P2 or P4 build-priority technique (e.g., the governance-critical SGE/HAG/TMG/CIS scenarios cited in §48 are all P0 test-priority, while their owning components — SGE, HAG, TMG, CIS — are not part of `architecture.md` §36's numbered tiers at all, since governance components are mandatory regardless of optimization build order). Readers must not assume "P1" in one document means anything about "P1" in another.

## 5. Implementation Principles

1. **Fail-open on optimization, fail-closed on security** (root `CLAUDE.md` rule 2) applies to every capability gate below exactly as it does everywhere else in this chain — no sequencing decision may create a path where an optimization capability ships ahead of, or bypasses, an already-mandatory governance check.
2. **Governance components (SGE/DGE/TMG/HAG/CIS) are not part of the P0–P5 tiering at all.** `architecture.md` §36 tiers *optimization* techniques; governance/safety components are mandatory infrastructure independent of optimization value (root `CLAUDE.md`'s central governance invariant) and are treated in this plan as cross-cutting prerequisites available to every tier, not as a tier of their own.
3. **A tier's item list is exact and closed** (`architecture.md` §36, `conventions.md` §25 — both verified identical). This document never adds, removes, or re-tiers a §36 item; it only supplies the sequencing logic beneath it and resolves the two named `DA-NNN`/session-phase gaps.
4. **P5 stays measurement-separate from P0–P4** (`conventions.md` §25's own `[!IMPORTANT]` callout, restated verbatim): P5 capability build sequencing is tracked independently and never combined with P0–P4 savings/build reporting without explicit decomposition.

## 6. Capability Lifecycle Model

Every capability substantial enough to warrant full decomposition (§8–§13 mark which ones receive it) follows this required minimum conceptual structure. Where a sub-phase is genuinely not applicable to a given capability, that is stated explicitly (`NOT APPLICABLE` + reason), never silently omitted.

```
Capability
  → A. Contract & Design      (interface/schema already fixed by interfaces.md — cited, not re-designed)
  → B. Minimal Implementation (smallest correct implementation of the owning TECH-NNN/DA-NNN/component)
  → C. Integration            (wiring into the T0–T3 pipeline / OI layer per architecture.md §7–§14)
  → D. Observability          (metrics/logs/traces per observability.md — cited, not re-specified)
  → E. Security/Governance    (which GSP-NNN component(s) this capability must pass through, cited)
  → F. Quality Validation     (which QG-NNN dimension(s) gate this capability, cited)
  → G. Scenario Validation    (which SCN-* scenario(s) validate it, verified — or NO DEDICATED SCENARIO, stated honestly)
  → H. Failure/Recovery       (fail-open/fail-closed disposition per root CLAUDE.md rule 2)
  → Capability Gate           (Entry/Exit/Blocking criteria — §18)
```

This is a **build/validation gate model**, not a claim of production readiness — passing a Capability Gate means "safe and coherent to proceed to the next dependent capability," never "proven in production."

Not every one of the ~56 catalog entries plus 6 foundation items receives the full nine-part treatment in this document — doing so for every entry would pad length without adding sequencing signal beyond what `optimization-catalog.md`/`agent-optimization.md` already provide per-entry. Instead, §8–§13 give full lifecycle depth to one or two **exemplar capabilities per tier** (chosen because they are either the tier's structural prerequisite for everything else in that tier, or because they best illustrate a genuine cross-tier dependency), and a **Sequencing Table** for every remaining item in that tier, carrying real prerequisite/rationale content rather than a bare restatement of §36's list.

---

## 7. Dependency Model

**Inter-tier dependency graph** (`SOURCE-DERIVED` from §36's own tier descriptions plus `optimization-catalog.md`'s per-entry dependency notes — no source states this graph explicitly as a single diagram):

```
P0 Foundation (token accounting, benchmark harness, sanitizer, context policy,
               prompt assembler, output controls, observability, quality evaluation)
   ↓ (every later tier's validation gate consumes P0's benchmark harness + quality evaluation)
P1 High-Confidence Optimization (validation-matrix gate required, per CONV §25)
   ↓ (P2's per-model quality baseline requires P1's validation-matrix infrastructure to exist)
P2 Cost Intelligence (validation matrix + per-model quality baseline required)
   ↓ (P3's benchmarked-formula requirement builds on P1/P2's validation evidence)
P3 Advanced Optimization (benchmarked formula required)
   ↓ (P4's adaptive/learned intelligence requires accumulated evidence from P1-P3's own
      validation history — it has nothing to learn from otherwise)
P4 Control-Plane Intelligence
   ⊥ (P5 is explicitly measurement-separate, not a downstream dependent of P4)
P5 Infrastructure-Dependent / Optional (independent axis, own prerequisite chain — §13)
```

This is a **default sequencing preference**, not a hard law: `architecture.md` §36 does not state that P2 cannot begin until every P1 item ships, only that each tier's items carry their own tier-appropriate validation gate. Where `optimization-catalog.md`'s own per-entry notes name a specific narrower prerequisite (e.g., TECH-012 Tool Result Caching "requires per-tool freshness policy," not "all of P1"), that narrower dependency is used in §8–§13 rather than the blunt inter-tier arrow above.

**Cross-domain dependency chains** (`SOURCE-DERIVED`, verified against the cited entries' own dependency notes):
```
Provider abstraction (P0 Prompt Assembler's provider-neutral contract, cited from provider-matrix.md)
   → Model Routing (TECH-013, P2)
      → Model Cascade (TECH-014, P2)
         → Marginal-Gain Model Routing (P4)

Tenant identity (root CLAUDE.md rule 4, cross-cutting, not tiered)
   → Prompt Caching (TECH-009, P1) — tenant-scoped key construction
      → Semantic Caching (TECH-010, P2) — requires P1's tenant-scoped cache infrastructure first
```

---

## 8. P0 — Foundation

**Exact current item list** (`architecture.md` §36 / `conventions.md` §25, verified identical): Token accounting, Baseline benchmark harness, Sanitizer, Context policy, Prompt assembler, Output controls, Observability, Quality evaluation.

**Tier objective:** establish the measurement, safety, and structural substrate every other tier's validation gate depends on. `CONV §25`: "No Validation Gate Required" — P0 items are unblocked because nothing exists yet to gate them against; they *are* the thing later gates measure against.

**Why this order exists:** without Token Accounting and the Baseline Benchmark Harness, no later tier's "validation matrix" (`CONV §25`'s P1/P2/P3 requirement) has anything to compare against — this is not a stylistic ordering choice, it is a hard logical prerequisite (`SOURCE-DERIVED`).

### 8.1 Exemplar Capability — Token Accounting and Cost Ledger

**Evidence:** `SOURCE-DEFINED` (schema) / `PROPOSED IMPLEMENTATION` (build steps). **Architecture Components:** `architecture.md` §27 Token and Cost Accounting Ledger. **Interface Dependencies:** the ledger fields §27.1 already defines — cited, not redesigned.

- **A. Contract & Design:** consume §27's ledger field list and cost model (§27.3) as-is; no new schema.
- **B. Minimal Implementation:** per-request token/cost recording (input/output/cached tokens, model calls); tenant-scoped from the first line of code (root `CLAUDE.md` rule 4) — there is no "add tenant scoping later" step, since retrofitting isolation onto an already-running ledger is a materially higher-risk change than building it in from the start.
- **C. Integration:** every T0–T3 pipeline stage and every later optimization technique writes to this ledger; it has no dependents until it exists.
- **D. Observability:** cite `observability.md`'s already-named cost metrics; do not invent a competing metric name.
- **E. Security/Governance:** none of SGE/DGE/TMG/HAG/CIS gate this capability directly, but SGE (`security.md`) consumes its output — the ledger must exist before SGE's spend-governance logic has anything to evaluate.
- **F. Quality Validation:** `NOT APPLICABLE` — a ledger has no "quality" dimension in `quality-gates.md`'s sense; it has correctness requirements, verified by unit/contract tests (§17).
- **G. Scenario Validation:** `NO DEDICATED SCENARIO` verified — no `SCN-*` scenario isolates ledger construction itself; ledger *correctness under concurrency* is partially covered by `SCN-CONC-001` (context mutation serialization, a related but not identical concern, cited from `SCALING.md`'s own honest disposition of that scenario's scope).
- **H. Failure/Recovery:** a ledger-write failure must never silently under-report cost (root `CLAUDE.md` rule 5) — treat an unverifiable cost record as `unverified` and exclude it from savings reporting rather than guessing.

### 8.2 Exemplar Capability — Baseline Benchmark Harness + Quality Evaluation

**Evidence:** `PROPOSED IMPLEMENTATION`. **Rationale for treating as one capability:** Baseline Benchmark Harness and Quality Evaluation **remain two distinct P0 items** in `architecture.md` §36's exact item list (§8's own "Exact current item list" above) — this document does not merge or re-tier them. This subsection combines them only for *implementation* purposes, because they are inseparable in practice: a benchmark harness with no quality evaluator cannot produce the "quality" half of any later validation-matrix comparison (`architecture.md` §32.2's BASELINE/OPTIMIZED comparison requires both). They are therefore implemented through one shared validation foundation rather than two independent build tracks, while remaining two distinct §36 items for tiering and traceability purposes.

- **A. Contract & Design:** cite `eval.md`'s `EvaluationFramework` (`run_baseline`/`run_optimized`/`compare`) and `quality-gates.md`'s `QG-NNN` methodology — this capability is the first *consumer* of both interfaces, not a redefinition of either.
- **B. Minimal Implementation:** wire a P0-only path that can run an unoptimized request and record its `EvaluationRun` baseline fields; no optimization stage exists yet to compare against, so `run_optimized()` is a no-op until P1 ships.
- **C. Integration:** every P1–P4 technique's own validation-matrix gate (§9–§12) calls into this harness; it is the shared substrate, built once.
- **D. Observability:** cite `observability.md`.
- **E. Security/Governance:** the harness must run under the same tenant-isolation and authorization rules as a live request (root `CLAUDE.md` rules 2/4) — a benchmark is not an exemption from governance.
- **F. Quality Validation:** this capability *is* the quality-validation substrate; cite `quality-gates.md` for the ten `QG-NNN` dimensions it must be able to evaluate against once techniques exist to measure.
- **G. Scenario Validation:** `NO DEDICATED SCENARIO` verified for harness construction itself; individual `SCN-QUAL-*`/`SCN-CMP-05x` scenarios (cited in `eval.md`/`quality-gates.md`) validate specific *uses* of the harness once P1+ techniques exist.
- **H. Failure/Recovery:** a harness failure must never silently mark an unvalidated technique as validated — fail closed on the validation *claim*, even though the underlying optimization stage itself fails open per root `CLAUDE.md` rule 2.

### 8.3 P0 Sequencing Table (remaining items)

| Item | Prerequisite(s) | Validation Gate (CONV §25) | Rationale |
|---|---|---|---|
| Sanitizer (`TECH-001`) | None (P0) | None required | First content-safety touchpoint; every later stage assumes sanitized input exists |
| Context policy | Token accounting (for budget enforcement) | None required | Defines the budget/policy substrate `T1`-series consumers read |
| Prompt assembler | Context policy, Sanitizer | None required | Assembles the final prompt other P1+ techniques modify/compress |
| Output controls | Token accounting | None required | Symmetric to prompt assembler on the output side |
| Observability | None (P0) — but practically co-built with Token accounting | None required | `observability.md`'s own scope; cited here only for sequencing, not redefined |
| Quality evaluation | Baseline benchmark harness (§8.2) | None required | Folded into §8.2's exemplar treatment above; listed separately here only because §36 lists it separately |

---

## 9. P1 — High-Confidence Optimization

**Exact current item list:** Exact prompt caching (`TECH-009`), Context deduplication (`TECH-004`), Context pruning (`TECH-003`), Adaptive Top-K (`TECH-005`), Tool-output filtering (`TECH-011`), Query compression (`TECH-002`), Context compression (`TECH-007`), Output schema/length control (`TECH-017`/`018`), Soft reset (`TECH-020`). **Validation Gate (CONV §25, `SOURCE-DEFINED`):** validation matrix required before default-enable for every item in this tier.

**Also folds in** (per `optimization-catalog.md`'s own `P1-equivalent` inference, promoted to this document's own `PROPOSED DEFAULT — VALIDATE LOCALLY` disposition — see §14): the 20 `DA-NNN` modules independently assigned a P1-equivalent tier (of the full 25-module distribution resolved in §14: 20 P1-equivalent, 3 P2-equivalent, 1 P3-equivalent, 1 P4-equivalent).

### 9.1 Exemplar Capability — Context Pruning (`TECH-003`) + `DA-001` Code-Aware Context Pruner

**Evidence:** `SOURCE-DEFINED` (TECH-003 schema) / `PROPOSED IMPLEMENTATION` (sequencing). **Rationale for pairing:** `DA-001` is `optimization-catalog.md`'s own explicit extension of `TECH-003` for coding agents ("bundled with generic context pruning's validation gate"), giving a concrete worked example of a P1 generic technique plus its developer-agent-specific counterpart sharing one validation gate rather than two.

- **A. Contract & Design:** cite `optimization-catalog.md`'s `TECH-003`/`DA-001` schemas.
- **B. Minimal Implementation:** generic relevance-scored pruning first (any application class); coding-specific symbol/AST-aware pruning (`DA-001`) only after the generic path passes its own validation matrix — building the specific case atop an unvalidated generic case would mean re-deriving the same quality-regression risk twice.
- **C. Integration:** T1-series context pipeline (per `architecture.md` §11).
- **D. Observability:** cite `observability.md`'s pruning metrics.
- **E. Security/Governance:** security-classified content is never pruned regardless of relevance score (root `CLAUDE.md` rule 6) — this constraint is checked in this capability's own unit tests, not deferred to a later governance pass.
- **F. Quality Validation:** cite `quality-gates.md`'s Retrieval Quality (`QG-003`) dimension and the compression-contract test suite (`QO-001`) — this capability's exit criterion is passing that suite, not an ad hoc check invented here.
- **G. Scenario Validation:** cite the relevant `SCN-CTX-*`/`SCN-CODE-*` scenarios already reconciled in `scenario-matrix.md` (not re-enumerated here to avoid duplicating that file's own traceability tables).
- **H. Failure/Recovery:** a pruning-stage failure falls back to unpruned context and the request continues (fail-open, root `CLAUDE.md` rule 2) — it never becomes a request rejection.

### 9.2 P1 Sequencing Table (remaining generic techniques)

| Item | Prerequisite(s) | Rationale |
|---|---|---|
| Exact prompt caching (`TECH-009`) | Token accounting (P0), tenant-scoped key infrastructure | First cache type — every later cache technique (`TECH-010`/`012`, P2) builds on its key/tenant-scoping pattern, per `cache-strategy.md` |
| Context deduplication (`TECH-004`) | Context policy (P0) | Independent of pruning; can build in parallel with §9.1 |
| Adaptive Top-K (`TECH-005`) | Context policy (P0) | `optimization-catalog.md` notes P1-tier, no cross-technique prerequisite beyond P0 |
| Tool-output filtering (`TECH-011`) | Output controls (P0) | Required before `DA-005`/`DA-008`'s developer-agent-specific tool-output work (below) |
| Query compression (`TECH-002`) | Prompt assembler (P0) | Independent P1 item |
| Context compression (`TECH-007`) | Context deduplication, Context pruning (both above) — `optimization-catalog.md` notes P3 for *advanced* compression, but base P1 compression has no such prerequisite beyond P0 | Avoids compressing content a cheaper dedup/pruning pass would have removed anyway |
| Output schema/length control (`TECH-017`/`018`) | Output controls (P0) | Independent P1 item |
| Soft reset / context budgeting (`TECH-020`) | Context policy (P0) | Independent P1 item |

### 9.3 P1-Equivalent `DA-NNN` Modules (sequencing summary; full disposition in §14)

`DA-002`, `003`, `004`, `005`, `006`, `008`, `009`, `010`, `011`, `012`, `013`, `016`, `017`, `018`, `019`, `020`, `023`, `024`, `025` all carry a `P1-equivalent` disposition (§14). Two intra-P1 orderings are worth calling out because `optimization-catalog.md` states them explicitly as prerequisite relationships, not merely tier co-membership: `DA-023` (Context Provenance Tracker) is "a prerequisite for `CL-004`, which every other reversible module depends on," and `DA-024` (Context Invalidation Engine) is "a prerequisite for every coding-agent cache's correctness" — both should be sequenced early within P1, before `DA-022` (Developer-Agent Cache Layer, P2-equivalent) can safely build on them.

---

## 10. P2 — Cost Intelligence

**Exact current item list:** Model routing (`TECH-013`), Model cascade (`TECH-014`), Semantic caching (`TECH-010`), Tool-result caching (`TECH-012`), Reasoning-budget controller (`TECH-015`), Agent early exit (`TECH-016`). **Validation Gate (CONV §25, `SOURCE-DEFINED`):** validation matrix + quality baseline per model required.

### 10.1 Exemplar Capability — Model Routing (`TECH-013`)

**Evidence:** `SOURCE-DEFINED` (schema) / `PROPOSED IMPLEMENTATION` (sequencing).

- **A. Contract & Design:** cite `optimization-catalog.md`'s `TECH-013` and `provider-matrix.md`'s Provider Accessibility Model — routing decisions consult provider facts, never hard-code them (root `CLAUDE.md` rule 1).
- **B. Minimal Implementation:** requires a per-model quality baseline (CONV §25's own gate condition) — this cannot be built before P0's benchmark harness (§8.2) has run at least once per candidate model.
- **C. Integration:** OI layer (`architecture.md` §14) consumes routing decisions.
- **D. Observability:** cite `observability.md`.
- **E. Security/Governance:** routing must never let cost-efficiency substitute for an authorization check (root `CLAUDE.md`'s governance invariant, restated in `security.md`) — a cheap model is never selected for a task the governance plane has not cleared.
- **F. Quality Validation:** explicit architectural prohibition (`optimization-catalog.md`, `SCN-MODEL-002`) against routing a safety-critical task to an incapable model — cited, not re-derived.
- **G. Scenario Validation:** `SCN-MODEL-001`/`SCN-MODEL-002`/`SCN-MODEL-004` (verified real in `optimization-catalog.md`'s own `Validated By` field for `TECH-013`).
- **H. Failure/Recovery:** a routing-stage failure falls back to the default/unrouted model choice (fail-open) — never to an unauthorized model.

### 10.2 P2 Sequencing Table (remaining items)

| Item | Prerequisite(s) | Rationale |
|---|---|---|
| Model cascade (`TECH-014`) | Model routing (§10.1) | `optimization-catalog.md`: "requires a quality evaluator and escalation-rate benchmarking" — the evaluator is P0's harness, escalation-rate benchmarking presumes routing already exists |
| Semantic caching (`TECH-010`) | Exact prompt caching (P1, `TECH-009`) | Builds on P1's tenant-scoped cache key infrastructure; adds similarity/freshness logic (`cache-strategy.md`, cited) |
| Tool-result caching (`TECH-012`) | Tool-output filtering (P1, `TECH-011`) | `optimization-catalog.md`: "requires per-tool freshness policy" |
| Reasoning-budget controller (`TECH-015`) | Baseline benchmark harness (P0, §8.2) | Root `CLAUDE.md` rule 3: reasoning cuts must be benchmarked against a quality baseline *first* — this is a hard sequencing constraint, not a preference |
| Agent early exit (`TECH-016`) | Baseline benchmark harness (P0); interacts with `AL-006`/`DA-021` (`agent-optimization.md`, cited) | `optimization-catalog.md`: "requires a completion-verification mechanism" |

---

## 11. P3 — Advanced Optimization

**Exact current item list:** Token-aware ranking (`TECH-006`), Context reordering (`TECH-008`), Learned routing, Advanced compression, Dynamic budget allocation, Batch optimization (`TECH-019`). **Validation Gate (CONV §25, `SOURCE-DEFINED`):** benchmarked formula required.

**Sequencing note:** three of these six item names (Learned routing, Advanced compression, Dynamic budget allocation) do not map to a distinct `TECH-NNN` ID in `optimization-catalog.md`'s 20-entry catalog — verified directly rather than assumed; they are `architecture.md` §36's own category names without a dedicated catalog elaboration yet. This document does not invent a `TECH-NNN` ID for them (that would be `optimization-catalog.md`'s prerogative, not this document's), and records the absence as `SOURCE-GAP-IMPL-01` (§26).

| Item | Prerequisite(s) | Rationale |
|---|---|---|
| Token-aware ranking (`TECH-006`) | Adaptive Top-K (P1, `TECH-005`) | `optimization-catalog.md`: "benchmarked formula required" — `architecture.md` §23 itself warns the token-cost/authority weighting "must be benchmarked" before production use |
| Context reordering (`TECH-008`) | Context pruning, Context deduplication (P1) | `optimization-catalog.md`: "requires quality measurement before and after" |
| Batch optimization (`TECH-019`) | Token accounting (P0); interacts with `SCALING.md`'s deployment-mode capacity work (cited) | Batch economics depend on stable per-request cost accounting existing first |
| Learned routing | Model routing, Model cascade (P2) | `SOURCE-DERIVED`: cannot learn a routing policy before a routing mechanism to learn over exists |
| Advanced compression | Context compression (P1, `TECH-007`) | `SOURCE-DERIVED`: "advanced" presumes the base technique already validated |
| Dynamic budget allocation | Context policy (P0), Soft reset (P1, `TECH-020`) | `SOURCE-DERIVED`: dynamic allocation extends the static budgeting substrate |

---

## 12. P4 — Control-Plane Intelligence

**Exact current item list** (24 items, verified): Optimization Decision Engine, Cost-of-Optimization Controller, Adaptive Optimization Depth, Context Utility/ROI scoring, Outcome-based optimization, Context dependency graph, Freshness-aware context management, Dependency-aware cache invalidation, Reversible optimization, Progressive context compaction, Cache economics, Cache fragmentation diagnosis, Cache-aware context construction, Tool-call ROI, Tool argument optimization, Dynamic tool loading, Programmatic tool execution, Agent loop progress measurement, Sub-agent economics, Marginal-gain model routing, Verifier-guided escalation, Shadow optimization, A/B experimentation, Continuous policy learning.

**Tier objective:** intelligence that requires accumulated evidence from P1–P3's own validation history to have anything to act on — `SOURCE-DERIVED`, not stated as a single rule anywhere, but implied by every P4 item's dependence on prior-tier measurement (Cost-of-Optimization Controller needs a cost ledger with history; Verifier-guided escalation needs a calibrated verifier per `quality-gates.md` §10 VCL, cited).

### 12.1 Exemplar Capability — Shadow Optimization, A/B Experimentation, Continuous Policy Learning

**Evidence:** `SOURCE-DEFINED` — these three §36 item names map directly and exactly to `eval.md`'s `EL-001` (Shadow Optimization), `EL-003` (A/B Testing), and `EL-004` (Continuous Policy Learning), verified directly against `eval.md`'s own entry headers.

- **A. Contract & Design:** cite `eval.md`'s full algorithm-level elaboration of `EL-001`/`003`/`004` — not redefined here.
- **B. Minimal Implementation:** cite `eval.md`; this document's only addition is sequencing: all three require the P0 benchmark harness and at least one already-P1/P2-validated technique to shadow/test/learn from, so they cannot start before P1 has at least one validated item.
- **E. Security/Governance:** `EL-004`'s `[!CAUTION]` (learning must be bounded by governance, no silent production-policy mutation) routes through `security.md`'s HAG (`GSP-004`) — cited exactly as `eval.md` itself already established; this document adds no new governance requirement.
- **G. Scenario Validation:** cite `eval.md`'s own verified `SCN-CMP-051/052/056/057`.
- **H. Failure/Recovery:** cite `eval.md`'s own EL-002 rollback model.

### 12.2 Exemplar Capability — Adaptive Optimization Depth (OI-003 / SPC interaction)

**Evidence:** `SOURCE-DEFINED`. This P4 item is `architecture.md` §47.4.1's `SPC` backpressure mechanism acting on `OI-003`, both already fully specified — cited from `architecture.md` and `SCALING.md`, not redefined.

- **E. Security/Governance:** `DepthSheddingDecision.security_stages_preserved` must remain `true` (verified in `SCALING.md`'s own citation of this invariant) — this P4 capability may shed *its own* optimization depth under load, never a governance/security stage.
- **H. Failure/Recovery:** self-protection failures are optimization failures (fail-open) *unless* the overload condition would otherwise skip a governance check, in which case the request fails closed — the one precedence exception already established in `SCALING.md`/`architecture.md` §47.4.1, restated here only for this capability's own gate, not re-derived.

### 12.3 P4 Sequencing Table (remaining items, grouped by dependency cluster)

| Cluster | Items | Prerequisite(s) |
|---|---|---|
| Cost/ROI intelligence | Cost-of-Optimization Controller, Context Utility/ROI scoring, Outcome-based optimization, Tool-call ROI, Marginal-Gain Model Routing | Token accounting (P0), Model routing/cascade (P2) |
| Decision orchestration | Optimization Decision Engine, Adaptive Optimization Depth (§12.2) | Every P1–P3 validated technique it can choose to enable/disable |
| Cache intelligence | Dependency-aware cache invalidation, Cache economics, Cache fragmentation diagnosis, Cache-aware context construction | Prompt/Semantic/Tool-result caching (P1/P2), `cache-strategy.md` (cited) |
| Context lifecycle | Context dependency graph, Freshness-aware context management, Reversible optimization, Progressive context compaction | Context pruning/dedup/compression (P1) |
| Tool intelligence | Tool argument optimization, Dynamic tool loading, Programmatic tool execution | Tool-output filtering (P1), Tool-result caching (P2) |
| Agent/verifier intelligence | Agent loop progress measurement, Sub-agent economics, Verifier-guided escalation | `agent-optimization.md`'s `AL-NNN`/`AR-NNN` (cited, fully elaborated there — not redefined), `quality-gates.md` §10 VCL (cited) |
| Experimentation | Shadow optimization, A/B experimentation, Continuous policy learning (§12.1) | P0 harness + ≥1 validated P1/P2 technique |

---

## 13. P5 — Infrastructure-Dependent / Optional

**Exact current item list:** KV/prefix cache optimization, Continuous batching, Speculative decoding, Quantization, Prefill/decode optimization, Inference scheduling, GPU utilization optimization. **Measurement-separation rule (`CONV §25`, `SOURCE-DEFINED`, restated verbatim):** "P5 must remain separate from prompt/context optimization (P0–P4). Never combine P5 measurements with P0–P4 savings reports without explicit decomposition."

**Sequencing disposition:** `inference-optimization.md` already fully owns the Control-Plane elaboration of these capabilities as `INFOPT-001`–`011` — strictly detection/routing/negotiation/measurement, never infrastructure implementation (H17/H18, cited). This document does not redefine that boundary or re-decompose these seven items into a capability lifecycle; doing so would duplicate `inference-optimization.md`'s own 732-line elaboration. The only sequencing fact this document adds: **P5 has its own independent prerequisite** — an actual inference-serving layer with the relevant infrastructure capability (KV cache, continuous-batching scheduler, etc.) must exist and be *negotiable* before any `INFOPT-NNN` Control-Plane capability has anything to detect/route against — this is infrastructure-external to the Control Plane itself and is explicitly out of this document's scope to plan (it is "Infrastructure-Dependent" by `architecture.md` §36's own naming).

**Clarification (intentional ownership, not omission):** P5 receives implementation-sequencing and dependency treatment in this document (the prerequisite fact above), but deliberately does not receive a duplicate full nine-subsection capability decomposition the way §8.1/§8.2/§9.1/§10.1/§12.1/§12.2 do, because `inference-optimization.md` already owns the detailed P5 Control-Plane capability elaboration in full. This is a documentation-ownership boundary this document respects, not a gap in this document's coverage.

---

## 14. `DA-NNN` Priority Resolution (`SOURCE-GAP-OPTCAT-01`)

`optimization-catalog.md`'s own `SOURCE-GAP-OPTCAT-01` states its 25 `DA-NNN` Implementation Priority fields are "this catalog's own inference from the closest matching generic-technique tier," not a source-stated value, and that this "should be resolved before `implementation-plan.md` is generated." Independent verification of every `DA-001`–`DA-025` entry's own dependency note (grepped directly from `optimization-catalog.md`, not assumed) confirms each inference is grounded in a stated dependency argument, not a bare guess. This document's resolution: **adopt each catalog inference as this document's own explicit build-sequencing disposition**, tagged `PROPOSED DEFAULT — VALIDATE LOCALLY` (promoted from a catalog aside to an actual planning decision), rather than silently leaving it a footnote. This does not modify `optimization-catalog.md` (file-scope discipline, §46) — it is a citation-plus-ratification, recorded here.

| DA-NNN | Name | Proposed Priority | Rationale (from `optimization-catalog.md`, independently verified) | Blocking? |
|---|---|---|---|---|
| `DA-001` | Code-Aware Context Pruner | P1-equivalent | Bundled with generic Context Pruning's (`TECH-003`) validation gate | No |
| `DA-002` | Symbol-Level Context Selector | P1-equivalent | Same gate as `DA-001` | No |
| `DA-003` | Repository Map Generator and Cache | P1-equivalent | No cross-technique prerequisite beyond P0 | No |
| `DA-004` | Git-Diff Optimizer | P1-equivalent | No cross-technique prerequisite beyond P0 | No |
| `DA-005` | Terminal Output Optimizer | P1-equivalent | Shares Tool-output filtering's (`TECH-011`) gate | No |
| `DA-006` | Compiler/Test/Linter Error Extractor | P1-equivalent | No cross-technique prerequisite beyond P0 | No |
| `DA-007` | Dynamic MCP/Tool Selector | P4-equivalent | "Dynamic tool loading" is explicitly a §36 P4 category | No |
| `DA-008` | Tool Schema Optimizer | P1-equivalent | Grouped with Tool-Output Filtering's validation gate | No |
| `DA-009` | Sub-Agent Result Compressor | P1-equivalent | `AL-005`'s absolute prohibition makes this non-deferrable (`agent-optimization.md`, cited) | No |
| `DA-010` | Agent Memory Optimizer | P1-equivalent | No cross-technique prerequisite beyond P0 | No |
| `DA-011` | Code-Aware Semantic Deduplicator | P1-equivalent | Shares Context Deduplication's (`TECH-004`) gate | No |
| `DA-012` | Error-Driven Context Retrieval | P1-equivalent | No cross-technique prerequisite beyond P0 | No |
| `DA-013` | Developer-Agent Context Budget Manager | P1-equivalent | Shares Soft Reset's (`TECH-020`) budgeting substrate | No |
| `DA-014` | Agent Loop Cost/Token Controller | P2-equivalent | Bundled with Agent Early Exit's (`TECH-016`) gate | No |
| `DA-015` | Code Context Reorderer | P3-equivalent | Bundled with Context Reordering's (`TECH-008`) gate | No |
| `DA-016` | File-Section Lazy Loader | P1-equivalent | No cross-technique prerequisite beyond P0 | No |
| `DA-017` | Repository Search Result Optimizer | P1-equivalent | No cross-technique prerequisite beyond P0 | No |
| `DA-018` | Agent Plan Context Optimizer | P1-equivalent | No cross-technique prerequisite beyond P0 | No |
| `DA-019` | Code Edit Context Optimizer | P1-equivalent | No cross-technique prerequisite beyond P0 | No |
| `DA-020` | Developer-Agent Output Controller | P1-equivalent | No cross-technique prerequisite beyond P0 | No |
| `DA-021` | Agent Task Completion/Early-Exit Controller | P2-equivalent | Same tier as generic Agent Early Exit | No |
| `DA-022` | Developer-Agent Cache Layer | P2-equivalent | Depends on `DA-023`/`DA-024` (below) existing first | No — but sequenced *after* `DA-023`/`024` within P1/P2 |
| `DA-023` | Context Provenance Tracker | P1-equivalent | **Prerequisite for `CL-004`**, which every other reversible module depends on — sequence early within P1 | No |
| `DA-024` | Context Invalidation Engine | P1-equivalent | **Prerequisite for every coding-agent cache's correctness** — sequence early within P1, before `DA-022` | No |
| `DA-025` | Patch/Change Impact Analyzer | P1-equivalent | No cross-technique prerequisite beyond P0 | No |

**Disposition:** all 25 modules resolved with an explicit, rationale-backed priority tier — none left unassigned. `SOURCE-GAP-OPTCAT-01` is narrowed to "resolved for build-sequencing purposes by `implementation-plan.md`, as it itself anticipated" — it is not marked closed in `optimization-catalog.md` (this document does not edit that file), so the original gap record there remains, correctly, as a historical trace of what was originally missing.

## 15. Session-Phase Values Disposition (`SOURCE-GAP-AGENTOPT-01`)

`agent-optimization.md` §8's CL-007 Session-Phase Policy table already proposes concrete per-phase values (six session phases from `architecture.md` §15.7's taxonomy), explicitly labeled `PROPOSED DEFAULT` and gap-tracked as `SOURCE-GAP-AGENTOPT-01` because the *taxonomy* is source-defined but the *values* are that document's own engineering proposal. This document's disposition, per the master prompt's own permitted options: **this document does not narrow or resolve the gap further** — it is not the appropriate place to invent new per-phase values on top of an already-existing, already-labeled proposal, since doing so would create two competing unauthoritative tables rather than one. Instead: this document confirms the gap is **non-blocking for build sequencing** — the session-management capability (folded into P1's `DA-013`/`DA-018` cluster, cited) can be built using `agent-optimization.md`'s existing proposed values as its Sub-phase B minimal implementation, with local-benchmark validation against those values deferred to that capability's own Quality Validation gate (Sub-phase F), exactly like any other `PROPOSED DEFAULT` in this chain. No new value is invented here.

---

## 16. CI/CD and Build-Pipeline Sequencing

`PROPOSED IMPLEMENTATION` throughout — no source names a specific CI platform, and none is invented here (root `CLAUDE.md`: do not invent build/lint/test commands or assume a framework). The required *gate progression* before a capability may merge/promote, derived from this chain's own already-established requirements:

```
1. Static/contract validation   — interface conformance against interfaces.md's schemas (cited)
2. Unit tests                   — capability-local correctness
3. Security/governance tests    — the capability's §14/§8-13 Sub-phase E checks (cited from security.md)
4. Quality tests                — the capability's Sub-phase F checks (cited from quality-gates.md)
5. Scenario tests                — the capability's Sub-phase G checks (cited from scenario-matrix.md)
6. Regression/evaluation run    — cite eval.md's EL-005 Regression Detector; required before any tier's
                                   validation-matrix gate (CONV §25) is considered satisfied
7. Capacity/deployment-mode check — cite SCALING.md's per-deployment-mode profiles, where the capability's
                                     target deployment mode (architecture.md §33 A-F) has a distinct profile
8. Capability Gate promotion    — §18 above
```

This progression is `SOURCE-DERIVED` from the individual gate requirements each cited document already states; it does not add a new requirement, only sequences the existing ones into one build-pipeline shape. `SCALING.md`'s own per-deployment-mode profiles (Mode B CLI single-invocation vs. Mode C API/middleware sustained load vs. Mode F CI/CD burst) mean step 7 is not identical for every capability — cited, not redefined here.

## 17. Rollout Priority vs. Build Priority — Orthogonal Axes

The **build axis** (`P0 → P1 → P2 → P3 → P4 → P5`, this document's subject) answers "in what order is a capability constructed." The **rollout/maturity axis** (`eval.md`'s `OBSERVE → BENCHMARK → CONTROLLED PILOT → PRODUCTION → GOVERNANCE → CONTINUOUS OPTIMIZATION`, cited not redefined) answers "how does an already-built capability mature into production use." These are orthogonal — a P1 item still passes through all six rollout phases once built.

**Worked example:** Exact Prompt Caching (`TECH-009`, P1) is built early in the build sequence (§9) because nothing else depends on caching existing before caching itself can be built. Once built, it still starts in `eval.md`'s `OBSERVE` phase (shadow mode, no production requests modified), then `BENCHMARK`, then `CONTROLLED PILOT` (1%→5%→25%→50%→100% per `EL-002`), then `PRODUCTION`, then `GOVERNANCE`, then `CONTINUOUS OPTIMIZATION` — a P1 build-priority item can sit in `OBSERVE` rollout-phase for an arbitrarily long time if its benchmark results don't clear the validation matrix; being "built early" never means "deployed to production early" or "validated." Conversely, a P4 item is built *late* in the sequence but, once built and validated, follows the identical six-phase rollout — P4 does not skip rollout phases just because it was built later.

---

## 18. Capability Gate Model (Recap)

Every Capability Gate (introduced in §6, applied throughout §8–§13) answers exactly these questions, per the master prompt's own required framing:

| Question | Answered By |
|---|---|
| Entry Criteria — what must exist before implementation begins? | The capability's own Prerequisites (§8–§13 tables) |
| Implementation Completion — what must be built? | Sub-phase B (Minimal Implementation) |
| Integration Completion — what must integrate successfully? | Sub-phase C |
| Security/Governance Completion — what must be proven? | Sub-phase E, citing `security.md` |
| Quality Completion — what quality requirements must pass? | Sub-phase F, citing `quality-gates.md` |
| Scenario Completion — which scenarios validate it? | Sub-phase G, citing `scenario-matrix.md` |
| Failure/Recovery Completion — which failure modes must be validated? | Sub-phase H |
| Exit Criteria — what allows the next dependent capability to begin? | The *next* capability's own Prerequisites row naming this one |
| Blocking Conditions — what prevents promotion? | Any Sub-phase E/F/G/H item stated but not yet satisfied |

A Capability Gate is a **build/validation gate**, never a claim that the capability is production-ready — restated here because it is the single most consequential distinction in this entire document (§4, §5.1).

---

## 19. Failure/Recovery, Security, Quality, Observability, Scaling — Cross-Cutting Boundaries

These five concerns apply to every capability above via each capability's own Sub-phase E/F/D/H, and are cited rather than re-specified per-capability beyond what §8–§13 already show:

- **Failure/Recovery:** fail-open for optimization stages, fail-closed where continuing would bypass a mandatory security/authorization/PII control (root `CLAUDE.md` rule 2) — no capability in this plan is exempt.
- **Security/Governance:** no capability may be promoted through its gate while it can bypass authorization, skip PII protection, bypass governance, violate tenant isolation, or disable a mandatory security stage (`security.md`, cited). The build sequence in §8–§13 never creates an architectural path where an optimization capability ships ahead of a governance component it depends on for safety.
- **Quality:** an optimization capability's gate is never satisfied by token/cost reduction alone (root `CLAUDE.md` central objective) — its Sub-phase F must pass the relevant `QG-NNN` dimension(s).
- **Observability:** every capability's telemetry requirement is satisfied by citing `observability.md`'s already-named metrics/events; this document introduces no competing metric name anywhere in §8–§13.
- **Scaling:** capacity/throughput/horizontal-scaling implications for a given capability are cited from `SCALING.md`, not redefined — most directly relevant to Batch Optimization (§11), Model Cascade (§10.2), and the Cross-Execution Coordination items folded into P4 (§12.3).

## 20. State/Data Dependencies

Required state per capability class, described at the behavior level only — no storage technology is named or invented (root `CLAUDE.md`: do not assume a language/framework):

| Capability Class | Required State (behavior only) |
|---|---|
| Caching (P1/P2) | Tenant-scoped key/value store with TTL and freshness metadata (`cache-strategy.md`, cited) |
| Execution/agent loop | Execution-truth state per `architecture.md` §46 (ESM/CVM/WVM, cited) |
| Governance (cross-cutting) | Policy/authorization state per `security.md` (cited) |
| Cross-execution coordination (P4) | `XEC`'s conflict-detection state (`architecture.md` §47.3.2, cited) |
| Evaluation (P4 experimentation cluster) | `eval.md`'s `EvaluationRun`/`EvaluationComparison` records (cited) |
| Observability | `observability.md`'s telemetry store (cited) |
| Tenant isolation (cross-cutting) | `tenant_id`-scoped namespace as the first component of every above state class (root `CLAUDE.md` rule 4) |

---

## 21. Scenario Coverage

Verified directly against `scenario-matrix.md`: **no scenario domain, and no individual scenario across all 30 domains, validates build sequencing or implementation-tier ordering itself** — this is a planning artifact describing construction order, not a runtime component with observable behavior a scenario could exercise. This mirrors how `scenario-matrix.md` §36.8 itself treats `architecture.md`'s H19/H20 hardening requirements: "principle/scope statements validated by reaffirmation... rather than a fabricated runtime scenario." No scenario is force-fit here to manufacture coverage that doesn't exist.

Individual capabilities decomposed in §8–§13 each cite their *own* real scenario coverage where one exists (e.g., `SCN-MODEL-001/002/004` for Model Routing, §10.1) — that coverage belongs to the capability's underlying `TECH-NNN`/`DA-NNN`/`EL-NNN` entry, already reconciled in its owning document, and is cited here rather than re-verified from scratch.

## 22. Edge-Case Coverage

| Edge Case | Verified Content | Implementation Obligation | Blocking? |
|---|---|---|---|
| `EC-077` | Adversarial input designed to trigger maximum optimization cost | Every optimization-stage capability (§9–§12) must respect `OI-002`'s per-request overhead-budget cap (cited from `SCALING.md`'s own operationalization) — this is a cross-cutting build obligation, not one capability's alone | No |
| `EC-191` | Concurrent modification of a shared resource, second write silently overwrites first | `XEC`-dependent P4 capabilities (§12.3 cross-execution cluster) must not be promoted through their gate without `XEC`'s conflict-detection wired in first | No |
| `EC-192` | Non-idempotent, concurrently-reachable action proceeds without an acquired lock | Same cluster as `EC-191` | No |
| `EC-193` | Two executions' completed actions overlap on the same resource | Same cluster | No |
| `EC-194` | Cross-execution coordination mechanism itself unavailable | Same cluster; capability gate for the P4 cross-execution items must include a fail-closed-on-coordination-unavailable check, cited from `architecture.md` §47.3.2 | No |

A broader search of `edge-cases.md` for other implementation-sequencing-relevant cases found none beyond the above and the ones already cited per-capability in §8–§13 (e.g., root `CLAUDE.md` rule 3's reasoning-budget-benchmarking requirement, §10.2).

---

## 23. Requirement → Implementation Traceability (Planning-Level)

This is a planning-level summary, not a substitute for the future `requirements-traceability.md` (acknowledged as planned, not created here, consistent with every prior document's own treatment).

| Requirement | Architecture | Mechanism | Implementation Capability (this document) | Validation |
|---|---|---|---|---|
| `NFR-008`/`NFR-014` | §37, §47.4.1 | SPC | §12.2 Adaptive Optimization Depth | `SCALING.md` (cited) |
| `OBJ-012`/`AC-017` | §32 | Benchmarking Framework | §8.2 Baseline Benchmark Harness | `eval.md` (cited) |
| Root `CLAUDE.md` rule 3 | `conventions.md` §10.3 | Reasoning-budget benchmarking | §10.2 Reasoning-Budget Controller | `eval.md`/`quality-gates.md` (cited) |
| `SEC-*` (GSP-NNN) | §47.2 | SGE/DGE/TMG/HAG/CIS | Cross-cutting, §19 | `security.md` (cited) |
| `OBJ-025`/`OBJ-032` | §47.4/§47.3 | SPC/XEC | §12.2, §22 (EC-191–194 cluster) | `SCALING.md` (cited) |
| `EL-001`/`003`/`004` | §21 | Shadow/A-B/Continuous Learning | §12.1 | `eval.md` (cited) |

---

## 24. Implementation Decision Register

Unresolved implementation choices this plan surfaces but does not decide — each is a genuine open question, not a placeholder invented to look thorough:

| Decision ID | Topic | Open Question | Current Options | Proposed Direction | Blocking? | ADR Candidate? |
|---|---|---|---|---|---|---|
| `IMPL-DEC-01` | Cache backing store | What technology backs the tenant-scoped key/value store `cache-strategy.md` specifies behaviorally? | Unspecified upstream | None proposed — genuinely open | No (P1 can build the behavioral contract against an in-memory reference implementation first) | Yes — §25 |
| `IMPL-DEC-02` | Execution-truth state store | What technology backs ESM/CVM/WVM's versioned state (`architecture.md` §46)? | Unspecified upstream | None proposed | No | Yes — §25 |
| `IMPL-DEC-03` | CI platform | Which CI/CD system executes the §16 gate progression? | Unspecified upstream | None proposed | No | Yes — §25 |
| `IMPL-DEC-04` | Telemetry backend | What system backs `observability.md`'s telemetry pipeline? | Unspecified upstream (`observability.md`'s own `SOURCE-GAP-OBS-*` series, cited) | None proposed | No | Yes — §25 |

## 25. ADR Candidates

Identified, not created (root `CLAUDE.md`: ADRs are a separate, not-yet-generated chain document):

1. Cache/key-value backing store selection (`IMPL-DEC-01`).
2. Execution-truth/versioned-state backing store selection (`IMPL-DEC-02`).
3. CI/CD platform and pipeline tooling (`IMPL-DEC-03`).
4. Telemetry/observability backend selection (`IMPL-DEC-04`).
5. Provider-adapter abstraction implementation pattern (root `CLAUDE.md` rule 1 — how `providers/adapters/` is concretely structured).
6. Coordination-mechanism implementation for `XEC` (§47.3.2's `ConcurrencyConflictResult` needs a concrete locking/coordination substrate).

## 26. Source Gaps

| Gap ID | Location | Missing Information | Blocking? |
|---|---|---|---|
| `SOURCE-GAP-IMPL-01` | §11 (P3 tier) | Three §36 P3 item names ("Learned routing," "Advanced compression," "Dynamic budget allocation") have no dedicated `TECH-NNN` ID in `optimization-catalog.md`'s 20-entry catalog — verified directly | No — sequencing logic is still derivable from the generic techniques each extends |
| `SOURCE-GAP-IMPL-02` | §20 | No source specifies a concrete backing-technology requirement for any of the four state classes named in §20 — intentionally so, since this repository specifies behavior, not infrastructure (root `CLAUDE.md`) | No — resolved at the ADR stage (§25), not here |

**Disposition of gaps carried forward from other documents:** `SOURCE-GAP-OPTCAT-01` — resolved for build-sequencing purposes in §14 (not closed in `optimization-catalog.md` itself; that file is unmodified). `SOURCE-GAP-AGENTOPT-01` — explicitly dispositioned as non-blocking and not further narrowed in §15, for the stated reason.

## 27. Source Contradictions

**Source Contradictions: 0.** One near-miss was checked and resolved as a scope/abstraction-level difference, not a real contradiction: `architecture.md` §36's flat P0–P5 item lists could be read as implying every item in a tier must complete before the next tier starts, while `optimization-catalog.md`'s per-entry dependency notes (e.g., `TECH-012`'s "requires per-tool freshness policy," not "requires all of P1") imply a narrower, item-specific dependency. This document resolves the reading by using the narrower, source-stated per-entry dependency wherever `optimization-catalog.md` provides one (§7's own stated preference), and the blunt inter-tier default only where no narrower dependency is stated — not a contradiction between two sources, but this document's own sequencing-precision choice.

## 28. Cross-Document Boundary Matrix

| Document | Consumed | Implementation Impact | Not Redefined |
|---|---|---|---|
| `architecture.md` | §36, §33, §34, §44 | P0–P5 item lists, tier objectives | Component specs, requirements |
| `interfaces.md` | Cited throughout §8–§13 | Contract references only | Schemas |
| `conventions.md` | §25, §27 | Validation-gate requirements | Package layout, naming |
| `edge-cases.md` | `EC-077`, `191`–`194` (§22) | Cross-cutting build obligations | Detection/fallback mechanics |
| `scenario-matrix.md` | §1, §48 (§4, §21) | P0–P3 collision documented | Scenario definitions |
| `optimization-catalog.md` | All `TECH-NNN`/`DA-NNN` priority fields, `SOURCE-GAP-OPTCAT-01` | §9–§14 sequencing | Schemas, algorithms |
| `provider-matrix.md` | Cited (§10.1) | Routing dependency only | Provider facts |
| `cache-strategy.md` | Cited (§9.1, §10.2) | Cache-tier sequencing only | Key/TTL mechanics |
| `agent-optimization.md` | §8, `SOURCE-GAP-AGENTOPT-01` | §12.3, §15 | `AL-NNN`/`AR-NNN` algorithms |
| `inference-optimization.md` | Cited (§13) | P5 prerequisite note only | `INFOPT-NNN` elaboration |
| `quality-gates.md` | Cited (§8.2, §9.1, §18) | Sub-phase F citations | `QG-NNN` methodology |
| `security.md` | Cited (§12.1, §19) | Sub-phase E citations | `GSP-NNN` mechanisms |
| `observability.md` | Cited (§19) | Sub-phase D citations | Telemetry implementation |
| `eval.md` | Cited (§8.2, §12.1, §16, §17) | Sub-phase F/G citations, rollout-axis boundary | Rollout mechanics, statistical methodology |
| `SCALING.md` | Cited (§12.2, §16, §22) | Capacity dependency notes | Capacity/scaling methodology |

---

## 29. Final Report

**Document ID:** `EAIOC-IMPL-001`. **Version:** 1.0.0. **Status:** PRE-IMPLEMENTATION — Level 0 (RESEARCH). **Line count:** see file. **Tiers elaborated:** 6/6 (P0–P5). **Exemplar capabilities given full lifecycle depth:** 6 full-lifecycle exemplars (§8.1, §8.2, §9.1, §10.1, §12.1, §12.2) + 1 P5 boundary/sequencing treatment (§13) — the P5 treatment is a sequencing/boundary note, not a full nine-subsection lifecycle exemplar, and is not counted among the six. **Sequencing-table rows (remaining items, all tiers combined):** 20+ items across P0–P3, 24 items grouped into 7 dependency clusters for P4, 7 items for P5. **`DA-NNN` resolution status:** 25/25 dispositioned, 0 left unassigned. **`SOURCE-GAP-AGENTOPT-01` disposition:** explicitly addressed, not further narrowed, non-blocking. **Scenarios consumed (cited from owning documents):** `SCN-MODEL-001/002/004`, `SCN-CMP-051/052/056/057`, `SCN-CONC-001` (referenced). **Edge cases consumed:** `EC-077`, `EC-191`–`194`. **Source gaps:** 2 new (`SOURCE-GAP-IMPL-01`/`02`), both non-blocking; 2 carried-forward gaps explicitly dispositioned (`SOURCE-GAP-OPTCAT-01` resolved-here-not-closed-upstream, `SOURCE-GAP-AGENTOPT-01` explicitly non-narrowed). **Contradictions:** 0. **Open implementation decisions:** 4 (`IMPL-DEC-01`–`04`). **ADR candidates:** 6. **CI/CD sequencing status:** proposed gate progression defined (§16), no platform selected (deferred to ADR). **File-scope confirmation:** only `docs/implementation-plan.md` was created in this pass. No upstream document (`problemStatement.txt`, Engineering Spec, `architecture.md`, `interfaces.md`, `conventions.md`, `edge-cases.md`, `scenario-matrix.md`) was modified. No sibling downstream document (`optimization-catalog.md`, `provider-matrix.md`, `cache-strategy.md`, `agent-optimization.md`, `inference-optimization.md`, `quality-gates.md`, `security.md`, `observability.md`, `eval.md`, `SCALING.md`) was modified — every gap this document resolves or dispositions is resolved *here*, not by editing the document that opened it. `requirements-traceability.md` and ADRs were not generated or cascaded into. **Next document:** `requirements-traceability.md`.

## Implementation Plan Readiness

Per the required readiness questions: (1) P0–P5 sequencing is defined with inter-tier and intra-tier dependency logic, not a bare restatement of §36. (2) Inter-tier and intra-tier dependencies are documented per-item in §7's dependency model and §8–§13's tables, using `optimization-catalog.md`'s own narrower dependency notes wherever one exists. (3) Capability implementation depth is established through the lifecycle model (§6) and its 6 full-lifecycle exemplar capabilities (§8.1/§8.2/§9.1/§10.1/§12.1/§12.2), plus §13's P5 boundary/sequencing treatment (not a full lifecycle exemplar); remaining capabilities receive compact sequencing/dependency records in §8–§13's tables rather than full nine-subsection decomposition. (4) Every substantially decomposed capability is governed by the Capability Gate model defined in §18 — full lifecycle detail is provided for the six exemplar capabilities, while remaining capabilities use compact sequencing/dependency records and inherit the applicable gate requirements; Capability Gate ownership is correctly and exclusively referenced as §18 throughout this document. (5) Security/governance gates are explicit and never bypassed (§19). (6) Quality/evaluation gates are explicit (§19, cited). (7) Scenario/failure/recovery validations are stated honestly, including where they don't exist (§21, §22). (8) All 25 `DA-001`–`DA-025` modules were dispositioned with an explicit priority tier and rationale (§14). (9) `SOURCE-GAP-AGENTOPT-01` was explicitly dispositioned (§15). (10) P5 capability-elaboration ownership remains with `inference-optimization.md` (§13) — this document adds only sequencing/dependency treatment for P5, intentionally, not by omission. (11) Unresolved implementation decisions are clearly identified (§24) rather than silently decided. (12) No upstream document was modified in producing or correcting this document, and no content unrelated to this correction pass was changed. (13) No actual blocker was found — every open item recorded is non-blocking by its own stated disposition. The document is ready for the next documentation phase.

READY FOR NEXT DOCUMENTATION PHASE
