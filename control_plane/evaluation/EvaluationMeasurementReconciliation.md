# `REM-P0.2.B-01` — Evaluation Measurement Acquisition & EvaluationRun Semantics

**Unit:** `REM-P0.2.B-01` — Baseline Benchmark Harness + Quality Evaluation: Contract / Design Reconciliation (Remediation). Registered in `docs/execution-plan.md` v1.0.10 §18.14.7 (`877fecc`); it sits outside the 48/54 counts. It supersedes the pre-decision record committed in `8b68239` under the non-canonical name `REM-P0.2.B-DESIGN-01`, which had no status (§18.14.7).
**Kind:** design artifact only — no contract, plan, code, or test change. Precedent: `control_plane/accounting/ledger/LedgerContractReconciliation.md` (`REM-P0.1.B-01`).
**Status:** the human decisions D-A to D-D are recorded (§1). `SOURCE-GAP-EXECPLAN-07`/`-08` remain **OPEN** until a user-directed source-contract correction promotes these decisions (§9). `EXE-P0.2.B` remains **BLOCKED**.
**Sources re-read for this unit (live):**
- `interfaces.md` v1.2.0: §18 (INTF-030 and the P0 realization notes), §19.2, §24.1, §26.1, §28.1, §40.2, §43.14.
- `conventions.md` Rev 1.1.0: §2.1, §2.2, §4.2, §13.2, §22.1.
- `architecture.md`: §27.3, §32.2, §35, §36.
- `eval.md`: §4, §5, §7, §13, §15, §16, §22–§25, §37, §38, §43.
- `execution-plan.md` v1.0.10: §3, §12, §14, §16, §17, §18.5, §18.14.5–§18.14.7, §38.
- `implementation-plan.md` §8.2 and `implementation-readiness-gate.md` (`SOURCE-GAP-IRG-02`).
- `CostLedgerStore.java`: public API `write`, `read(tenantId, entryId)`.

Labels:
- **SOURCE-DEFINED** — a source states it.
- **SOURCE-DERIVED** — a source derives it and labels it so.
- **INFERRED** — reasoned here, not stated by any source.
- **SOURCE-GAP** — the corpus is silent.
- **SOURCE-UNRESOLVED** — a candidate exists, but no source establishes it.
- **HUMAN DECISION** — decided by the operator in this unit.
- **†** — an option or decision that revises an HQ decision (D-B is one; see §1).

---

## 1. Human decisions recorded in this unit (2026-09-24)

These decisions were made by the operator in-session, after the Architect assessment. They are operator decisions, not source-derived.

**Relationship to HQ-1 to HQ-6 (F1, operator-confirmed after the Verifier review):**
- **D-B † revises HQ-2** — specifically its closing clause "INTF-030 is not modified for P0 convenience" (`execution-plan.md` §18.14.6). The rest of HQ-2 stands: no `verified` field, no nullable change, no zero, placeholder or sentinel, and no projection of DB-1/HD-4. The follow-up correction must record this revision in §18.14.6.
- **D-A does not revise HQ-1** (operator confirmation, not a source finding). The CostLedger (INTF-047) stays the only measurement source. The producer is the proving lab already in scope (plan §3, §16) feeding the ledger, not a new source. HQ-1's condition — an explicit mapping **and** a retrieval contract — is to be met by promoting both into the corpus.
- D-C and D-D revise no HQ decision.

| ID | Decision area | Decision | Effect |
|---|---|---|---|
| **D-A** | Baseline measurement acquisition and ownership | **Ledger mapping plus a producer.** Baseline measurements are acquired from the CostLedger (INTF-047) through declared field mappings and a request-to-entry retrieval contract. **Only entries with `verified = true` may be consumed.** Ownership is split: Capability 1 (`accounting/`) owns the retrieval contract's realization; a producer unit owns delivering Path A measurements into the ledger. Both units must be registered. | Uses the dependency that §40.2 already defines ("EL.x requires: CostLedger (INTF-047)"). Does not revise HQ-1 (operator-confirmed). Once promoted into the corpus, it meets HQ-1's "mapping **and** retrieval contract" condition. |
| **D-B †** | BASELINE-only representation | **Separate baseline record.** `run_baseline()` returns a distinct, baseline-only record carrying identity and baseline-side fields only. `EvaluationRun` keeps comparison semantics. | INTF-030 change: a new schema and a changed `run_baseline()` return type. **Revises HQ-2's "INTF-030 is not modified" clause** (operator-confirmed). It adds no nullable field and no `verified` field, and invents no zero, so the rest of HQ-2 stands. |
| **D-C** | Java realization and package ownership | **Segregated interfaces, split packages.** A narrow baseline interface is implemented at P0. A second interface declares `run_optimized()`/`compare()` with no P0 implementer. Interfaces and schemas live in `evaluation/`; the baseline runner/harness lives in `benchmarking/`. `report_regression()` is not declared (`SOURCE-GAP-EVAL-01`). | Honors HQ-3 (no stub or runtime behavior for `run_optimized()`) and HQ-5 (evaluation-owned schemas). Matches §18.5's two packages and `conventions.md` §2.1's separate `benchmarking/  # ARCH §32` entry. |
| **D-D** | Remaining contract items | **Cache effects are not applicable to a pure BASELINE run** (HUMAN DECISION — `eval.md` §24 only forbids attributing provider-native cache savings to EAIOC; it does not itself make the fields inapplicable). The baseline record (D-B) therefore excludes `cache_hit_rate`/`tokens_avoided_by_cache`. The §40.2 "ALL requires: … Observability" line is recorded as a **slice-scope tension** under HQ-1, not a conflict. The missing quality/task-success producer and `SOURCE-GAP-EXECPLAN-09` are carried to the follow-up correction; no producer is invented. | See §7 and §10. |

**Options not chosen:**
- **D-A:** narrow `EXE-P0.2.B`; † caller-supplied input (would have revised HQ-1); leave unresolved.
- **D-B:** not needed (depends on narrowing); † nullable on BASELINE and † `verification_status` (both would have revised other HQ-2 clauses).
- **D-C:** segregated interfaces in `evaluation/` only; partial interface; leave unresolved.
- **D-D:** cache effects apply; leave unresolved.

## 2. D-A — measurement acquisition: source analysis

- **SOURCE-DEFINED (intent):**
  - Capability 2's B row: "wire a P0-only path that can run an unoptimized request and record its `EvaluationRun` baseline fields" (`execution-plan.md` §18.5; `implementation-plan.md` §8.2 B).
  - A baseline is "original application behavior without optimization" (`architecture.md` §32.2).
  - `eval.md` §7 (EL-001) executes the baseline pipeline, then calls `run_baseline()`.
- **SOURCE-DEFINED (scope):**
  - EAIOC makes no model call in P0: "No live provider adapter is built in this slice" (§14).
  - The baseline is `Path A — Reference Application/Agent -> LLM`, which runs outside EAIOC (§16). Plan §3 puts the "first proving-lab measurement slice (Path A baseline vs. Path B EAIOC)" in scope.
  - §17: measurements are "captured by Capability 1 (ledger) and Capability 2 (`EvaluationRun`) for both Path A and Path B".
  - Observability is outside this slice (`SOURCE-GAP-IRG-02`).
- **SOURCE-DEFINED (dependency) — narrows `SOURCE-GAP-EXECPLAN-07`:**
  - `interfaces.md` §40.2: "EL.x requires: CostLedger (INTF-047), EvaluationRun (INTF-030)". The direction evaluation → accounting is established at source.
  - Still undefined, and still the open part of -07: the field mapping and the request-to-entry retrieval contract.
- **Verified-only condition (D-A, from the Architect assessment):**
  - Implementation evidence, not a source statement: no P0 producer measures the §27.1 fields, so every real P0 ledger entry is `verified = false`, holding the DB-1 placeholders that `interfaces.md` §28.1 permits only in such entries (`CostLedgerEntry.unverifiedFallback()`). HQ-2 forbids projecting DB-1/HD-4 onto evaluation records.
  - Mapping therefore consumes `verified = true` entries only, and **none exists at P0 until a producer yields verified measurements.** That is why D-A requires a producer unit.
- **Unregistered deliverable, now owned by D-A:** no §18.4–§18.9 unit builds the proving lab or Path A. D-A requires the follow-up correction to register one. No ID is assigned here.

| Bucket | Fields | Evidence |
|---|---|---|
| Available directly from a P0 baseline execution | None of the measurement fields. Only identity fields: `run_id`, `request_id`, `run_type = BASELINE`, `timestamp` | §14, §16; no producer unit exists |
| Available from the CostLedger once mapped (D-A) | Candidates only, none adopted (§4) | §40.2 dependency; §28.1 fields; mapping undefined |
| Would require telemetry out of scope | `latency_ms_baseline` if taken from Observability (§19.2 defines emission only, no read contract) | `SOURCE-GAP-IRG-02`; HQ-1; D-A routes latency through the ledger instead |
| No authoritative producer anywhere | `quality_score_baseline`, `task_success_baseline` — no evaluator or task verifier producing them at P0 is defined in `quality-gates.md`, `observability.md`, `cache-strategy.md` or the ledger (§27.1 `quality.*` members are different measures) | SOURCE-GAP, carried to §10 |

## 3. Field classification (`EvaluationRun`, INTF-030 — contract unchanged in this unit)

| Field | Classification | Source | In the D-B baseline record? |
|---|---|---|---|
| `run_id`, `request_id`, `run_type`, `timestamp` | Identity (SOURCE-DEFINED) | INTF-030; §26.1 HQ-6 row | Yes (shape to be defined by the correction) |
| `baseline_cost` | Baseline execution measurement | `architecture.md` §27.3, §32.2 | Yes — via D-A mapping (SOURCE-UNRESOLVED until promoted) |
| `latency_ms_baseline` | Baseline execution measurement | §32.2; `eval.md` §23 | Yes — via D-A mapping (SOURCE-UNRESOLVED until promoted) |
| `quality_score_baseline` | Baseline measurement — **SOURCE-GAP** (no producer) | §32.2; `eval.md` §21 | Open (§10, item 3) |
| `task_success_baseline` | Baseline measurement — **SOURCE-GAP** (no producer) | `eval.md` §25 | Open (§10, item 3) |
| `cache_hit_rate`, `tokens_avoided_by_cache` | Cache effect — **not applicable to BASELINE (D-D)** | `eval.md` §24 | No |
| `optimized_cost`, `quality_score_optimized`, `latency_ms_optimized`, `task_success_optimized` | Optimized execution measurement (`\| null` in INTF-030) | INTF-030 | No |
| `gross_savings`, `net_savings`, `net_savings_pct` | Comparison-derived | PS cost model; `architecture.md` §27.3; `eval.md` §22 | No |
| `optimizer_overhead` | **Optimized-side derived** (SOURCE-DERIVED: `eval.md` §22 maps it to the Cache Read/Write, Compression and Routing/Cascade line items of Optimized Cost) | `eval.md` §22 | No |
| `quality_delta`, `latency_delta_ms` | Comparison-derived | `eval.md` §23, §27, §37 | No |
| `regression_detected`, `regression_dimensions` | Comparison-derived | `architecture.md` §35 | No |

**Counts (re-verified against the live INTF-030):**
- 22 fields in total; 4 identity fields; 18 non-identity fields.
- Of the 18 non-identity fields, 14 are non-nullable. The 4 nullable ones are the `*_optimized` fields.
- **Governing rule:** `conventions.md` §4.2 — "Every field marked as non-null … must be present in every production invocation."

## 4. Candidate mappings (none adopted — the correction decides)

The D-2 rule (`execution-plan.md` §18.14.4) applies: no equivalence is declared on name similarity.

| Target field | Candidate ledger field(s) (§28.1) | Justification in corpus | Status |
|---|---|---|---|
| `baseline_cost` | retained `baseline_cost`, or `cost.baseline_estimated` | None beyond name; the ledger keeps the two distinct | SOURCE-UNRESOLVED — the correction must pick one or define a derivation, citing a source |
| `latency_ms_baseline` | `performance.e2e_latency_ms` | `eval.md` §23 maps the concept to §32.2, not to a producer | SOURCE-UNRESOLVED |
| `quality_score_baseline`, `task_success_baseline` | none | — | SOURCE-GAP (§10, item 3) |

**Retrieval contract:** the ledger store's public API is `write` plus `read(tenantId, entryId)`, with no request-keyed lookup. The request-to-entry retrieval contract is SOURCE-UNRESOLVED, and its realization belongs to a new `accounting/` unit (D-A). It must be tenant-first (`conventions.md` §13.2) and must return only `verified = true` entries.

## 5. D-B — the baseline record

- **SOURCE-DEFINED:**
  - A BASELINE run records one baseline execution of one request (`architecture.md` §32.2; `eval.md` §7).
  - `request_id` names the request and `run_id` the execution, and runs are not idempotent by `request_id` (§26.1, HQ-6).
  - A failed baseline must not produce a partial, malformed or false-positive record (plan §18.5 B and H failure-path tests), and a missing baseline withholds the comparison, failing closed on the evaluation verdict (`eval.md` §37).
  - Records are stored tenant-first (plan §12).
- **INFERRED:** an *unmeasurable* baseline therefore yields no record rather than a record with invented values. (`eval.md` §38 A's "most recent BASELINE run" selection is `PROPOSED METHODOLOGY`, not SOURCE-DEFINED.)
- **Decision (D-B †, revises HQ-2's "INTF-030 is not modified" clause):**
  - A distinct baseline-only record replaces the requirement to populate the eight comparison-derived fields and the four optimized fields on a BASELINE run.
  - It adds no nullable field, `verified` field, placeholder, or structural zero (the rest of HQ-2; `CLAUDE.md` rule 5).
  - `EvaluationRun` stays the comparison-bearing record, for runs of type `OPTIMIZED`/`SHADOW` and the `compare()` path, both deferred under HQ-3/HQ-4.
- **Open, for the correction:**
  - The new schema's name and exact field set.
  - Whether `EvaluationComparison`/`compare()` take the baseline record as input.
  - Whether `EvaluationRun`'s `run_type = BASELINE` value stays or is deprecated.
  - Schema versioning. INFERRED: a return-type change is a breaking change under `conventions.md` §22.1 / `interfaces.md` §24.1 ("type change"). `EvaluationRun` carries no `schema_version` (§18.14.6), so versioning applies at the document level.
- **Plan §17's "Path B overhead vs. Path A baseline" comparison — open, no decision taken.**
  - INFERRED: a P0 Path-B run has no optimization stage, so recording it as `run_type = OPTIMIZED` would misclassify it.
  - The correction must either define its representation or record it as deferred.

## 6. D-C — Java realization

- **SOURCE-DEFINED:**
  - `run_optimized()` "is a no-op until P1 ships" (`implementation-plan.md` §8.2; plan §12, §18.5).
  - HQ-3 forbids any runtime stub or exception, and plan v1.0.9 reads the B row's "explicit" as satisfied by documentation.
  - INTF-030 declares four methods.
- **Decision (D-C), segregated and split:**
  - `evaluation/` holds the baseline interface (`run_baseline()`, returning the D-B record), a second interface declaring `run_optimized(ControlPlaneRequest, OptimizationPlan)` and `compare(...)` with **no P0 implementer**, and the evaluation-owned schemas (HQ-5).
  - `benchmarking/` holds the P0 implementation of the baseline interface: the runner/harness.
  - `report_regression()` is **not declared**, because `RegressionReport` is undefined (`SOURCE-GAP-EVAL-01`, which stays open).
- **INFERRED dependency direction:**
  - `benchmarking/` → `evaluation/` (implements its interface), and → the `accounting/` retrieval contract (D-A), in both cases through interface contracts only (`conventions.md` §2.2).
  - `evaluation/` depends on neither `benchmarking/` nor `accounting/` internals.
  - The correction must state this in §18.5.

## 7. D-D — remaining contract items

1. **Cache effects:** not applicable to BASELINE (HUMAN DECISION). They are measured only on optimized or shadow runs, with provider-native effects unattributed to EAIOC (`eval.md` §24; `CLAUDE.md` rule 5).
2. **The §40.2 "ALL requires: … Observability (INTF-031–034)" line versus HQ-1** (INFERRED: tension, not a SOURCE-CONFLICT). HQ-1 is a slice-scope decision (plan §3 excludes the Observability P0 item), not a contract override. The correction records this beside §40.2 or in the §18 notes; no Observability read path is introduced.
3. **Quality/task-success producer:** no source defines one (SOURCE-GAP). Carried to §10; none is invented.
4. **`SOURCE-GAP-EXECPLAN-09`** (no `tenant_id` on `EvaluationRun`, plan §38; non-blocking). The D-B record and the D-A retrieval contract must both be tenant-first scoped (`conventions.md` §13.2). The correction decides whether the D-B record carries `tenant_id` or relies on store keying (plan §12).

## 8. HQ-4, HQ-5, HQ-6 (unchanged)

- **HQ-4:** `compare()` and `report_regression()` are not in `EXE-P0.2.B`. `SOURCE-GAP-EVAL-01` stays open and does not block B.
- **HQ-5:** schemas are evaluation-owned. D-C adds only the runner's placement in `benchmarking/`.
- **HQ-6:** runs are not idempotent by `request_id` (§26.1). This applies equally to the D-B record.

## 9. Source-contract promotion required (DB-2 precedent — not performed by this unit)

A separate user-directed source-contract correction (`execution-plan.md` §18.14.5 precedent; §18.14.7 sequence) must record these decisions:

| Decision | Documents / sections to change |
|---|---|
| D-A | `interfaces.md` §18 (the P0 notes: mapping and retrieval contract, verified-only condition) and §28.1 (the ledger side of the mapping); `execution-plan.md` §18.5 B/H, §16/§17, §38 (`-07` closure or narrowing); registration of two new units — an `accounting/` retrieval unit and a proving-lab / Path A producer unit; the runbook |
| D-B † | `execution-plan.md` §18.14.6 (record the HQ-2 revision); `interfaces.md` §18 (new baseline record schema; `run_baseline()` return type) and §24 (version); `eval.md` §16/§22 (and §37 if the failure disposition wording changes); `execution-plan.md` §18.5 B Definition of Done, §38 (`-08` closure) |
| D-C | `execution-plan.md` §18.5 (package placement and dependency direction in the B row); `interfaces.md` §18 notes (the segregated realization; `report_regression()` undeclared) |
| D-D | `interfaces.md` §18 notes (cache non-applicability to BASELINE; the §40.2 Observability tension); `eval.md` §24; `execution-plan.md` §38 (`-09` cross-reference) |

## 10. Open items the correction must decide or record (none decided here)

1. **Mapping choice:** `baseline_cost` ← which ledger field or derivation, and `latency_ms_baseline` ← `performance.e2e_latency_ms` or otherwise, each with a cited source (§4).
2. **Retrieval contract:** its shape, request keying, tenant-first scoping, and the verified-only filter (§4).
3. **Quality/task-success:** a producer for `quality_score_baseline`/`task_success_baseline`, or their exclusion from the P0 baseline record (§3, §7.3).
4. **Baseline record:** name, field set, `tenant_id` (§7.4), versioning, and its relationship to `compare()` (§5).
5. **Plan §17's overhead comparison:** representation, or explicit deferral (§5).
6. **Unit registration:** IDs, scopes and sequence for the `accounting/` retrieval unit and the producer unit (D-A). Whether `EXE-P0.2.B`'s Definition of Done then requires a live verified entry, or only the contract and failure path, is part of this.

## 11. Unblocking checklist (`EXE-P0.2.B`)

| # | Condition | Status |
|---|---|---|
| 1 | D-A acquisition contract authoritative | Decided (§1); **not yet promoted** — `-07` OPEN |
| 2 | D-B baseline semantics authoritative | Decided (§1); **not yet promoted** — `-08` OPEN |
| 3 | D-C Java realization defined | Decided (§1); to be recorded in §18.5 |
| 4 | HQ-4/HQ-5/HQ-6 sufficient | Yes |
| 5 | No invented values or equivalences | Yes — none introduced |
| 6 | `SOURCE-GAP-EVAL-01` closed or non-blocking | Non-blocking for B (open) |
| 7 | Producer and retrieval units registered and executed | No — they are required by D-A |
| 8 | §10 open items decided | No |

**Result: `EXE-P0.2.B` remains BLOCKED.** It becomes eligible only after the §9 correction is committed, the D-A units it registers are complete, and `EXE-P0.2.B`'s own pre-flight passes.
