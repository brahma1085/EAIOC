# `REM-P0.2.B-DESIGN-01` — Evaluation Measurement Acquisition & EvaluationRun Semantics

**Unit:** `REM-P0.2.B-DESIGN-01` (issued by the operator in session on 2026-09-24 as a contract/design remediation for `EXE-P0.2.B`; **not yet registered in `docs/execution-plan.md`** — `REM-P0.1.B-01` was assigned in the plan before execution — so this note is a pre-decision record, not an authoritative unit artifact, until a user-directed correction registers the unit; outside the 48/54 counts).
**Kind:** design artifact only — no contract, plan, code, or test change. Precedent: `control_plane/accounting/ledger/LedgerContractReconciliation.md` (`REM-P0.1.B-01`).
**Status:** `EXE-P0.2.B` remains **BLOCKED**. This note records what the sources establish and the exact human decisions still required; it resolves nothing by invention.
**Sources read (current):** `interfaces.md` v1.2.0 §18, §19.2, §25.2, §26.1, §28.1, §40.2, §43.14; `conventions.md` Rev 1.1.0 header, §2.1, §4.1–§4.5, §5.1–§5.4, §14.1, §22.1; `architecture.md` Rev 1.3 §27.1, §27.3, §32, §35; Problem Statement (baseline definition, cost formulas, PHASE 1 OBSERVE); Engineering Spec Rev 1.4 §23.2, OBJ-012, AC-015, AC-017; `eval.md` §4–§7, §13, §15–§16, §22, §24–§27, §37, §38, §43; `execution-plan.md` v1.0.9 §4, §11, §12, §14, §16–§17, §18.5, §18.8, §18.14.6, §18.15, §18.19, §38; `implementation-plan.md` §8.2; `implementation-readiness-gate.md` (`SOURCE-GAP-IRG-02`); `requirements-traceability.md` (OBJ-012, AC-015, AC-017 — document-level trace only); ADR index (no ADR governs evaluation measurement; all `PROPOSED`); `control_plane/evaluation/EvaluationFramework.md` (`EXE-P0.2.A`, historical, unchanged).

Labels: **SOURCE-DEFINED** (a source states it) · **SOURCE-GAP** (the corpus is silent) · **SOURCE-UNRESOLVED** (a candidate exists but no source establishes it) · **P0-UNAVAILABLE-BY-SCOPE** (the concept applies but the P0 slice excludes the producer) · **CONTRADICTION** · **HUMAN DECISION**.

---

## 1. D-A — measurement acquisition at P0

**Investigated direction: acquire baseline measurements from the baseline execution path itself.**
- **SOURCE-DEFINED (intent):** Capability 2's Sub-phase B is to "wire a P0-only path that can run an unoptimized request and record its `EvaluationRun` baseline fields" (`execution-plan.md` §18.5 Objective; `implementation-plan.md` §8.2 B). A baseline is "original application behavior without optimization" (PS; SPEC §23.2; `architecture.md` §32.2), and PHASE 1 OBSERVE says "Record baseline token/cost/quality". `eval.md` §7 (EL-001) describes executing "the actual, user-facing baseline pipeline unmodified" and then calling `run_baseline()`.
- **SOURCE-DEFINED (scope):** the model call is outside this P0 slice — "No live provider adapter is built in this slice" (`execution-plan.md` §14); Prompt Assembler is the "last request-path capability before the model call, which is out of scope" (§18.8). The proving-lab baseline, `Path A — Reference Application/Agent -> LLM`, runs outside EAIOC (§16). The Observability P0 item is outside the slice (`SOURCE-GAP-IRG-02`).
- **Not a contradiction — a GAP plus an unowned deliverable (corrected after the Verifier's review).** The plan itself reconciles the two statements: §3 puts "the first proving-lab measurement slice (Path A baseline vs. Path B EAIOC)" **in scope**; §16 defines the baseline as `Path A — Reference Application/Agent -> LLM`, outside EAIOC; §17 says the measurements are "captured by Capability 1 (ledger) and Capability 2 (`EvaluationRun`) for both Path A and Path B"; PS PHASE 1 pairs "Record baseline token/cost/quality" with "Run in shadow mode". So the "unoptimized request" is Path A, executed outside EAIOC, and §14/§18.8 exclude only EAIOC's own adapter and model call. What is missing is (1) any contract by which Path A's measurements reach `run_baseline(request: ControlPlaneRequest)` — the registered `SOURCE-GAP-EXECPLAN-07` — and (2) any §18.4–§18.9 unit that builds the in-scope reference application / proving lab (**unregistered finding**; no ID assigned here). → **HUMAN DECISION** (D-A below).

**The four buckets you asked for:**

| Bucket | Fields | Evidence |
|---|---|---|
| 1. Available directly from a P0 baseline execution | **None** of the measurement fields. Only identity fields (`run_id`, `request_id`, `run_type = BASELINE`, `timestamp`) | EAIOC executes no model call in this slice (§14, §18.8); the baseline (Path A) runs in the proving lab, and no unit builds it or delivers its measurements (§1) |
| 2. Available from the existing CostLedger contract | **None by an established mapping.** Same-named fields exist — `CostLedgerEntry.baseline_cost` (INTF-047 retained), `tokens_avoided_by_cache` (retained), `cost.baseline_estimated`, `cache.*`, `perf.e2e_latency_ms` (§28.1) — but no source declares any equivalence to an `EvaluationRun` field, and `CostLedgerStore` exposes no request-to-entry retrieval (`write`, `read(tenantId, entryId)` only). The ledger itself forbids undeclared equivalence (D-2) | `SOURCE-GAP-EXECPLAN-07` |
| 3. Requiring telemetry that is out of P0 scope | `latency_ms_baseline` if taken from Observability — `interfaces.md` §19.2 defines only emission of `control_plane.request.latency_ms`; no read contract; Observability item out of slice | `SOURCE-GAP-IRG-02`; HQ-1 (no Observability dependency) |
| 4. No authoritative acquisition mechanism | `quality_score_baseline`, `task_success_baseline` (no evaluator or task verifier is defined for P0 — `execution-plan.md` §18.5 F row covers only QG gating); `cache_hit_rate`, `tokens_avoided_by_cache` (no definition, and whether they apply to a run "without optimization" is undefined — `eval.md` §24) | `SOURCE-GAP-EXECPLAN-07`/`-08` |

## 2. Field classification (`EvaluationRun`, INTF-030 — contract unchanged)

| Field | Classification | Source | Available at P0? | Honest on a BASELINE-only run? |
|---|---|---|---|---|
| `run_id`, `request_id`, `run_type`, `timestamp` | Identity (SOURCE-DEFINED) | INTF-030; §26.1 HQ-6 row | Yes | Yes |
| `baseline_cost` | BASELINE_EXECUTION_MEASUREMENT | PS cost model; `architecture.md` §27.3, §32.2 | No (no producer in slice) | Yes, if measured |
| `latency_ms_baseline` | BASELINE_EXECUTION_MEASUREMENT | `architecture.md` §32.2; `eval.md` §23 | No | Yes, if measured |
| `quality_score_baseline` | SOURCE-GAP (under -07) — quality evaluation is itself a P0 item folded into this capability (`architecture.md` §36; `implementation-plan.md` §8.2); what is missing is a defined evaluator/mechanism, not scope | §32.2; plan §18.5 F row (QG gating N/A at P0) | No | — |
| `task_success_baseline` | SOURCE-GAP (under -07) — no task verifier is defined for P0 | `eval.md` §25; plan F row | No | — |
| `cache_hit_rate` | SOURCE-UNRESOLVED | no definition; baseline applicability undefined (`eval.md` §24) | No | Undefined |
| `tokens_avoided_by_cache` | SOURCE-UNRESOLVED | same | No | Undefined |
| `optimized_cost`, `quality_score_optimized`, `latency_ms_optimized`, `task_success_optimized` | OPTIMIZED_EXECUTION_MEASUREMENT | INTF-030 (`\| null`) | N/A | `null` by contract — not applicable |
| `gross_savings`, `optimizer_overhead` | COMPARISON_DERIVED | `eval.md` §22 | No | No (non-nullable) |
| `net_savings`, `net_savings_pct` | COMPARISON_DERIVED | PS cost model; `architecture.md` §27.3 | No | No (non-nullable) |
| `quality_delta`, `latency_delta_ms` | COMPARISON_DERIVED | `eval.md` §23, §27, §37 | No | No (non-nullable) |
| `regression_detected`, `regression_dimensions` | COMPARISON_DERIVED | `architecture.md` §35 | No | No (non-nullable) |

## 3. Candidate mappings (none adopted)

| EvaluationRun field | Candidate source | Candidate field | Semantic justification in the corpus | Type | Available at P0 | Status |
|---|---|---|---|---|---|---|
| `baseline_cost` | CostLedger (INTF-047) | `baseline_cost` (retained) or `cost.baseline_estimated` (§27.1) | None — identical name only; the ledger keeps its two candidates distinct (D-2) | float / float | No retrieval by request | SOURCE-UNRESOLVED |
| `tokens_avoided_by_cache` | CostLedger | `tokens_avoided_by_cache` (retained) or `cache.reused_tokens` | None | integer / integer | No | SOURCE-UNRESOLVED |
| `cache_hit_rate` | CostLedger | a formula over `cache.exact_hits`/`semantic_hits`/`misses` | None — no formula defined; `da003.map_cache_hit_rate` is a different metric | float | No | SOURCE-UNRESOLVED |
| `latency_ms_baseline` | CostLedger or Observability | `performance.e2e_latency_ms` or `control_plane.request.latency_ms` | `eval.md` §23 maps the concept to §32.2, not to a producer | integer | No (ledger: no retrieval; Observability: out of slice) | SOURCE-UNRESOLVED |

## 4. D-B — what a valid `EvaluationRun(BASELINE)` means at P0

**SOURCE-DEFINED:** a BASELINE run records one baseline execution of one request (`architecture.md` §32.2; `eval.md` §7, §38 A); `request_id` names the request and `run_id` the execution, and runs are not idempotent by `request_id` (`interfaces.md` §26.1, HQ-6); a failed baseline yields no run (plan §18.5 B and H failure-path tests; `eval.md` §37 adds "re-run baseline"); runs are stored `tenant_id`-first (plan §12).

**The governing rule for non-nullable fields** is `conventions.md` §4.2: "Every field marked as non-null … must be present in every production invocation." **No source defines how a non-nullable `EvaluationRun` field is represented when it cannot be measured.** HQ-2 (v1.0.8) forbids zeros, sentinels, a `verified` field, and a nullable change for P0 convenience. Under those constraints a BASELINE `EvaluationRun` cannot be constructed at P0 at all: fourteen of its eighteen non-identity fields are non-nullable, none has a measurement or representation, and the four nullable ones are the optimized side. → **SOURCE-GAP-EXECPLAN-08**, **HUMAN DECISION** (D-B).

## 5. D-C — `run_optimized()` at P0

**SOURCE-DEFINED:** `run_optimized()` "is a no-op until P1 ships" (`implementation-plan.md` §8.2; plan §11, §12, §18.5); HQ-3 (v1.0.8) records it as deferred forward contract with no runtime no-op, stub, or exception, and the v1.0.9 annotation reads the B row's "explicit" as satisfied by documentation. INTF-030's return type is non-null `EvaluationRun`. This establishes interpretation **B — unavailable/not applicable until an optimization capability exists**. A fake optimized run is excluded by HQ-2/HQ-3 and `CLAUDE.md` rule 5.

**Consequence, recorded not decided:** a Java class must give every method of an interface it implements a body, so HQ-3's "no runtime behavior" rules out a P0 class that implements a full four-method interface. Two realizations honor it (INFERRED, Java semantics): (i) the P0 Java `EvaluationFramework` declares only `run_baseline()`, realizing INTF-030 partially — the `OptimizationPlan` "explicitly incomplete" precedent (`CoreFoundation.md`, CORE-GAP-03/04) needed operator approval; or (ii) a segregated realization — a narrow baseline interface implemented at P0, plus an interface declaring `run_optimized(ControlPlaneRequest, OptimizationPlan)` and `compare(...)` with no P0 implementer (both parameter/return types exist or are fully defined). Either way **`report_regression()` cannot be declared at all**, because its return type `RegressionReport` is undefined (`SOURCE-GAP-EVAL-01`). No preference is expressed here. → **HUMAN DECISION** (D-C).

## 6. HQ-4, HQ-5, HQ-6

- **HQ-4 (SOURCE-DEFINED + HQ-4 decision):** INTF-030 owns `compare()`/`report_regression()`; no sub-phase owns their implementation; neither is in `EXE-P0.2.B` (HQ-4). `EvaluationComparison` is fully defined; `RegressionReport` is not (`eval.md` §43) — **`SOURCE-GAP-EVAL-01` stays open**, and because `report_regression()` is out of B's scope it **does not block `EXE-P0.2.B`**.
- **HQ-5 (SOURCE-DEFINED + HQ-5 decision):** `conventions.md` §2.1 lists `evaluation/  # ARCH §32; INTF §18`; `EvaluationRun`/`EvaluationComparison` are evaluation-owned (`interfaces.md` §18 notes). The concrete Java package follows the repository's existing realization of §2.1 (`com.eaioc.controlplane.<area>`, per `CoreFoundation.md` D6: "Placement of each interface is decided by the unit that implements it"). Sufficiently clear.
- **HQ-6 (SOURCE-DEFINED):** `interfaces.md` §26.1 row: not idempotent by `request_id`; distinct `run_id` per execution; consistent with `eval.md` §37 ("re-run baseline") and §38 A ("most recent" BASELINE run). Defined.

## 7. Human decisions still required

| ID | Question | Options (those marked † would revise an existing operator decision) | Documents that would change |
|---|---|---|---|
| **D-A** | Close the gap (§1): how are baseline measurements acquired at P0, and who builds Path A? | (a) Keep the model call out of P0 and narrow `EXE-P0.2.B` so no measured `EvaluationRun` is produced at P0 (e.g. tenant-scoped store and type validation only), deferring baseline runs to the capability that brings an execution path — consequence: the B row's Definition of Done and the §18.5 H failure test lose their subject and must be rewritten, and P0's evidence for AC-015/AC-017 is postponed; (b)† define a measurement input — values supplied by the caller, including the §16/§17 proving-lab Path A as that caller (an INTF-030 change; revises HQ-1's "no new P0 measurement source"), or a declared CostLedger mapping plus a retrieval contract (a new unit for any `accounting/` change); (c) assign a unit that builds the already in-scope proving lab (§3, §16) and, with (b), delivers Path A's measurements | `execution-plan.md` §18.5 (and §14 for c); `interfaces.md` §18 for b |
| **D-B** | How does a BASELINE run represent its 8 comparison-derived and 6 unavailable non-nullable fields? | (a)† nullable on BASELINE — MAJOR under `conventions.md` §22.1, and **supersedes HQ-2** ("no field is made nullable"); (b)† an availability representation (a `verified` flag or `verification_status`, cf. `NetOptimizationValue` §43.14) — **supersedes HQ-2** ("no `verified` field is added"); (c) `run_baseline()` returns a partial/different record — INTF-030 change; (d) none needed if D-A (a) is chosen, because no `EvaluationRun` is produced at P0 | `interfaces.md` §18; `eval.md` §16/§22 for a–c |
| **D-C** | How does the P0 Java code realize INTF-030 while honoring HQ-3 ("no runtime behavior")? | (i) partial interface — `run_baseline()` only; (ii) segregated interfaces — baseline interface implemented, `run_optimized()`/`compare()` declared without a P0 implementer; in both, `report_regression()` is not declared (`SOURCE-GAP-EVAL-01`) | `execution-plan.md` §18.5 B row |
| **D-D** | Do cache effects (`cache_hit_rate`, `tokens_avoided_by_cache`) apply to a BASELINE run ("without optimization"; provider-native caching may still occur, `eval.md` §24)? | applicable (then define the measurement) / not applicable (then D-B governs representation) | `interfaces.md` §18 notes |

## 8. Unblocking checklist (`EXE-P0.2.B`)

| # | Condition | Status |
|---|---|---|
| 1 | D-A has an authoritative acquisition contract | **No** — gap -07; proving-lab deliverable unowned |
| 2 | D-B has authoritative BASELINE semantics | **No** — gap (-08) |
| 3 | D-C has authoritative `run_optimized()` semantics | Mostly — deferred (HQ-3); the Java realization (i or ii) needs a decision |
| 4 | HQ-4 scope sufficient for B | Yes |
| 5 | HQ-5 ownership sufficient | Yes |
| 6 | HQ-6 identity defined | Yes |
| 7 | No invented values or equivalences | Yes — none introduced |
| 8 | `SOURCE-GAP-EVAL-01` closed or proven non-blocking | Proven non-blocking for B (open) |
| 9 | No unresolved implementation-affecting contradiction | Yes — no contradiction (the D-A issue is a gap, §1) |
| 10 | Plan/runbook agree with the contract | Yes for the recorded decisions; F1 annotated at v1.0.9 |

**Result: `EXE-P0.2.B` — BLOCKED** (conditions 1 and 2 unmet; 3 partial). Once D-A to D-D are decided, a user-directed correction (the v1.0.6/v1.0.7 DB-2 precedent) would record them in `interfaces.md` §18 and `execution-plan.md` §18.5/§18.14, register the unowned proving-lab deliverable, and close or narrow `SOURCE-GAP-EXECPLAN-07`/`-08`.
