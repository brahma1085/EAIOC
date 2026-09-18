# Enterprise Agent & LLM Inference Optimization Control Plane — Requirements Traceability

**Document ID:** EAIOC-REQTRACE-001
**Status:** PRE-IMPLEMENTATION — Level 0 (RESEARCH)
**Version:** 1.0.1 (surgical correction pass applied — see §17 Final Report)
**Generated:** 2026-09-18 (per repository date)
**Generation prompt:** `docs/prompts/generate-requirements-traceability.prompt.md` (Consolidated Master Generation Prompt)

**Position in the documentation chain:** Twelfth and final generated downstream document, following `optimization-catalog.md` → `provider-matrix.md` → `cache-strategy.md` → `agent-optimization.md` → `inference-optimization.md` → `quality-gates.md` → `security.md` → `observability.md` → `eval.md` → `SCALING.md` → `implementation-plan.md`. Gated by each of those eleven documents' own passing readiness verdict, assumed here rather than re-verified. Next: ADRs (not generated here, not cascaded into).

**Sources read in full or by targeted section/ID lookup:** root `CLAUDE.md`; `architecture.md` §44 (Requirements Traceability Matrix, §44.1–§44.12) and §47.18 (Hardening Traceability Matrix and Known Source Gaps); `conventions.md` §27/§27.1; `Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md` §42.22 (`SOURCE-GAP-ES-01`–`04`); `scenario-matrix.md` §32–§33 (Source Gaps/Contradictions) and §35–§39 (cited, not re-derived); every one of the eleven downstream documents' own scope-boundary tables and `SOURCE-GAP-*`/`CONTRA-*` registers, located by direct grep of each file rather than trusted from any prior summary.

**Scope boundary reminder:** This document owns exactly two things: (1) the one forward-traceability hop from `OBJ`/`SEC`/`NFR`/`H`/`AC` requirements into whichever of the eleven downstream documents actually elaborates them — a hop `architecture.md` §44 and `conventions.md` §27 could not make because both predate the downstream corpus; (2) a master, cross-corpus `SOURCE-GAP-*`/`CONTRA-*` register that exposes narrowing/resolution relationships invisible from any single document. It does **not** re-derive `architecture.md` §44's requirement-to-architecture-section mapping, `conventions.md` §27's requirement-to-convention-section mapping, or `scenario-matrix.md` §36–39's `EC`/`SCN`/`INTF` traceability — all three are cited, none redefined. It does not re-litigate any downstream document's own subject-matter content.

---

## 1. Status, Maturity, and How to Read

This document is **Level 0 — RESEARCH / PRE-IMPLEMENTATION**, consistent with every other document in this chain. Four evidence labels are used throughout: `SOURCE-DEFINED` (an upstream document states this exactly), `SOURCE-DERIVED` (directly implied by combining source requirements, formulated here), `PROPOSED METHODOLOGY` (a technique this document introduces because no source specifies one), `PROPOSED DEFAULT — VALIDATE LOCALLY` (a specific number this document proposes for the same reason). Two additional relationship labels are used where a traceability *link*, not a methodology, is being asserted: `DIRECT ELABORATION` (the downstream document adds substantive, requirement-relevant detail) vs. `REFERENCE ONLY` (the requirement ID or its owning component is merely cited/mentioned without new elaboration) vs. `NO DOWNSTREAM ELABORATION` (neither occurs — a valid and often-correct result, not a defect).

Traceability status per requirement uses a controlled vocabulary, not evaluative language: `FULLY TRACED`, `PARTIALLY TRACED`, `INDIRECTLY TRACED`, `UNTRACED`, `AMBIGUOUS`, `BLOCKED BY SOURCE GAP`, `BLOCKED BY ADR`, `NOT APPLICABLE`.

## 2. Purpose

`architecture.md` §44 traces every `OBJ`/`SEC`/`NFR`/`AC` and (via §47.18) every `H01`–`H20` to its owning architecture section. `conventions.md` §27 traces the same requirements forward to their owning convention section, and §27.1 traces `H01`–`H20` specifically. Both were written before `optimization-catalog.md` through `implementation-plan.md` existed, so neither could trace a requirement into the downstream corpus. `scenario-matrix.md` §36–39 independently and exhaustively traces `EC`/`SCN`/`INTF` coverage, also cited here rather than rebuilt. This document makes the one additional hop none of them could: **requirement → downstream document**, and separately consolidates every `SOURCE-GAP-*`/`CONTRA-*` ID discovered across the entire corpus (root sources + all eleven downstream documents) into one register with a freshly re-verified current status.

## 3. Scope and Ownership

| Concern | Owner |
|---|---|
| Requirement → architecture-section mapping | `architecture.md` §44 (cited, not redefined) |
| Requirement → convention-section mapping | `conventions.md` §27/§27.1 (cited, not redefined) |
| `EC`/`SCN`/`INTF` traceability | `scenario-matrix.md` §36–39 (cited, not redefined) |
| Requirement → downstream-document forward trace | **This document** |
| Cross-corpus `SOURCE-GAP`/`CONTRA` consolidated register | **This document** |
| Requirement/artifact orphan and broken-chain analysis | **This document** |
| Individual downstream document's own subject-matter content (algorithms, thresholds, schemas) | Each of the eleven downstream documents (cited, none redefined) |
| `DA-NNN` priority disposition | `implementation-plan.md` (authoritative; not reopened here) |
| ADR decisions | ADRs (not yet generated; only `BLOCKED BY ADR` dependencies identified here, no decision made) |

## 4. Requirement Taxonomy and Freshly-Verified Counts

Every count below was independently recalculated against the live files rather than assumed from this document's own generation prompt or from `conventions.md` §27's cited summary.

| Category | Cited count (`conventions.md` §27) | Freshly verified count | Verification source | Match? |
|---|---|---|---|---|
| `OBJ-NNN` | 35 | 35 (14 original `architecture.md` §44.1 + 8 Dynamic Execution `OBJ-015`–`022` + 13 Hardening `OBJ-023`–`035` §44.7) | `architecture.md` §44.1, §44.7, §44.12 registry | Yes |
| `SEC-NNN` | 16 | 16 (10 original §44.2 + 6 Hardening §44.8) | `architecture.md` §44.2, §44.8 | Yes |
| `NFR-NNN` | 14 | 14 (13 original §44.3 + 1 Hardening `NFR-014` §44.9) | `architecture.md` §44.3, §44.9 | Yes |
| `H01`–`H20` | 20 | 20 | `architecture.md` §47.18 | Yes |
| `AC-NNN` | 53 | 53 (38 original §44.6 + 15 Hardening §44.10) | `architecture.md` §44.6, §44.10 | Yes |
| `DA-NNN` | 25 | 25 | `architecture.md` §44.5; `implementation-plan.md` §14 (all 25 dispositioned) | Yes |
| `INTF-NNN` | 71 | 71 | `interfaces.md` §36 index; `scenario-matrix.md` §33 (`CONTRA-001` follow-up confirms 71 as the current baseline) | Yes |

**No count discrepancy was found.** No new `SOURCE-GAP-REQTRACE-NN` is required for this section.

## 5. Existing Upstream Traceability — Cited, Not Re-Derived

`architecture.md` §44.1–§44.12 (Objectives/SEC/NFR/Optimization-Domains/DA-Module/AC coverage, both original and Hardening-pass extensions, plus the §44.12 canonical `OBJ-001`–`035` registry) and §47.18 (the H01–H20 traceability matrix) are the authoritative requirement-to-architecture-section mapping. `conventions.md` §27/§27.1 is the authoritative requirement-to-convention-section mapping. Both report **PASS** for full coverage at their own layer, independently re-confirmed by the counts in §4 above. `scenario-matrix.md` §36 (Requirement → Scenario), §37 (Scenario → Architecture), §38 (Interface Coverage: 69 DIRECT / 2 THIN / 0 NOT COVERED), and §39 (Edge-Case Coverage: 177 DIRECT / 35 PARTIAL / 0 NOT COVERED / 1 DUPLICATE, current per its own 2026-09-16 Targeted Fix Pass) remain the authoritative `EC`/`SCN`/`INTF` traceability layer. None of this is recomputed below.

## 6. Forward-Traceability Matrix — Requirement → Downstream Document

Grouped by identical disposition where genuinely identical (per the generation prompt's own permission); not grouped where treatment differs even among adjacent IDs.

### 6.1 Objectives (`OBJ-001`–`035`)

| Requirement | Downstream Document(s) | Elaboration | Status |
|---|---|---|---|
| `OBJ-001`–`004`, `OBJ-006`, `OBJ-007` (input/redundancy/tool-token reduction, reuse) | `optimization-catalog.md` (`TECH-NNN` schemas), `cache-strategy.md` (`CACHE-NNN`), `agent-optimization.md` (7 `DA-NNN`) | `DIRECT ELABORATION` | FULLY TRACED |
| `OBJ-005` (reduce model calls) | `optimization-catalog.md`, `cache-strategy.md`, `agent-optimization.md` (`AL-006` early exit), `inference-optimization.md` (P5 layer, cited boundary only) | `DIRECT ELABORATION` (Layer 1/2); `REFERENCE ONLY` (P5) | FULLY TRACED |
| `OBJ-008` (route cheapest capable model) | `optimization-catalog.md` (`TECH-013`), `provider-matrix.md` (model capability facts) | `DIRECT ELABORATION` | FULLY TRACED |
| `OBJ-009` (dynamic budgets) | `optimization-catalog.md`, `agent-optimization.md` (`AR-002`) | `DIRECT ELABORATION` | FULLY TRACED |
| `OBJ-010` (stop agentic workflows) | `agent-optimization.md` (`AL-002`/`AL-006`) | `DIRECT ELABORATION` | FULLY TRACED |
| `OBJ-011` (auditable ledger) | `observability.md` (audit/explanation retrieval layer) | `DIRECT ELABORATION` | FULLY TRACED |
| `OBJ-012` (org-specific benchmark evidence) | `eval.md` (benchmarking/statistical methodology, entirely) | `DIRECT ELABORATION` | FULLY TRACED |
| `OBJ-013` (multi-provider abstraction) | `provider-matrix.md` (entirely) | `DIRECT ELABORATION` | FULLY TRACED |
| `OBJ-014` (security/tenant boundaries) | `security.md`, `cache-strategy.md` (tenant-scoped keys) | `DIRECT ELABORATION` | FULLY TRACED |
| `OBJ-015` (versioned mutable execution state, primary owner `ESM` per `architecture.md` §44.12) | cited by name in 9 of 11 downstream documents (verified by direct grep) but owned as subject matter by none | `REFERENCE ONLY` | **NO DOWNSTREAM ELABORATION** — remains owned by `architecture.md` §46.2.1/`interfaces.md` §42 |
| `OBJ-016` (checkpointing/resumability, primary owner `CPM`) | cited (`security.md`, `observability.md`) | `REFERENCE ONLY` | **NO DOWNSTREAM ELABORATION** — remains owned by `architecture.md` §46.2.4 |
| `OBJ-017` (reconcile in-flight mutations, primary owner `RE`) | cited (`observability.md`, `inference-optimization.md`) | `REFERENCE ONLY` | **NO DOWNSTREAM ELABORATION** — remains owned by `architecture.md` §46.2.5 |
| `OBJ-018` (Logical vs. Model-Admitted Context, primary owner `CVM`) | cited in 8 of 11 downstream documents | `REFERENCE ONLY` | **NO DOWNSTREAM ELABORATION** — remains owned by `architecture.md` §46.2.2 |
| `OBJ-019` (tiered eviction, no silent truncation, primary owner `CIG`) | cited (`optimization-catalog.md`, `security.md`) | `REFERENCE ONLY` | **NO DOWNSTREAM ELABORATION** — remains owned by `architecture.md` §46.2.6 |
| `OBJ-020` (six interruption causes handled, §46.3 — no single named primary component; collectively involves `WVM`/`CEC`/`PRV`/`DPE`/`CAR`) | cited (most frequently `WVM`/`CAR`, across `optimization-catalog.md`, `agent-optimization.md`, `provider-matrix.md`, `inference-optimization.md`) | `REFERENCE ONLY` | **NO DOWNSTREAM ELABORATION** — remains owned by `architecture.md` §46.3 |
| `OBJ-021` (stale-result rejection, primary owner `SRP`) | cited (`optimization-catalog.md`, `cache-strategy.md`, `agent-optimization.md`) | `REFERENCE ONLY` | **NO DOWNSTREAM ELABORATION** — remains owned by `architecture.md` §46.2.11 |
| `OBJ-022` (scenario-completeness production gate; owning artifact is `scenario-matrix.md` itself, not an architecture component) | `scenario-matrix.md` (the gate's own existence) | `REFERENCE ONLY` at architecture-component level | **NO DOWNSTREAM ELABORATION** among the eleven downstream documents specifically — its true owner (`scenario-matrix.md`) sits one layer earlier in the chain, not among the eleven |

**Correction note (this pass):** the prior draft grouped `OBJ-015`–`022` (8 objective IDs) under one row listing all 13 Dynamic Execution component names (`ESM`/`CVM`/`WVM`/`CPM`/`RE`/`CIG`/`CEC`/`PRV`/`DPE`/`CAR`/`SRP`/`SPM`/`RCO`), which conflated an 8-ID range with a 13-component list. Verified directly against `architecture.md` §44.12's canonical registry: only 6 of the 13 components (`ESM`, `CPM`, `RE`, `CVM`, `CIG`, `SRP`) are each a single objective's *named* primary owner; `OBJ-020` maps to §46.3 generically rather than one named component; `OBJ-022` maps to `scenario-matrix.md`, not an architecture component at all; and `SPM`/`RCO` appear in §46 as supporting mechanisms but are not the *primary* owner of any single one of `OBJ-015`–`022` in the §44.12 registry. Expanded into 8 individual rows above so cardinality is unambiguous. Disposition (`NO DOWNSTREAM ELABORATION` among the eleven downstream documents) is unchanged from the prior draft's conclusion — only the per-ID attribution was corrected.
| `OBJ-023` (operating modes) | none | — | NO DOWNSTREAM ELABORATION |
| `OBJ-024` (advisor/enforcer/execution-owner) | none | — | NO DOWNSTREAM ELABORATION |
| `OBJ-025` (Control-Plane self-protection, `SPC`) | `SCALING.md` (`SC-001`, operational capacity/backpressure layer atop `SPC`) | `DIRECT ELABORATION` (capacity layer only; `SPC`'s own mechanism cited, not redefined) | PARTIALLY TRACED |
| `OBJ-026` (verified net optimization economics) | `eval.md` (net-savings accounting consumed, not redefined) | `REFERENCE ONLY` | INDIRECTLY TRACED |
| `OBJ-027` (spend governance, `SGE`) | `security.md` (`GSP-001`) | `DIRECT ELABORATION` | FULLY TRACED |
| `OBJ-028` (verifier calibration, `VCL`) | `quality-gates.md` (§10 Verifier Calibration Procedure); `eval.md` (narrows `SOURCE-GAP-QUALGATE-01`/`02`) | `DIRECT ELABORATION` | FULLY TRACED |
| `OBJ-029` (data governance, `DGE`) | `security.md` (`GSP-002`) | `DIRECT ELABORATION` | FULLY TRACED |
| `OBJ-030` (feasibility tiers, `FTR`) | `provider-matrix.md` (§9.1, tier assignments — narrows only the naming/evidence half, FTR-mechanism half remains open, see §8) | `DIRECT ELABORATION` (partial) | PARTIALLY TRACED — `BLOCKED BY SOURCE GAP` (`SOURCE-GAP-ARCH-02`, FTR half) |
| `OBJ-031` (memory authority) | none | — | NO DOWNSTREAM ELABORATION (owned by `architecture.md` §47.3.1/`interfaces.md` §43.13) |
| `OBJ-032` (cross-execution concurrency, `XEC`) | `SCALING.md` (`SC-002`, contention/capacity layer atop `XEC`) | `DIRECT ELABORATION` (capacity layer only; `XEC`'s own mechanism cited, not redefined) | PARTIALLY TRACED |
| `OBJ-033` (human approval, `HAG`) | `security.md` (`GSP-004`); `eval.md` (`EL-004`'s governance routing, cited) | `DIRECT ELABORATION` | FULLY TRACED |
| `OBJ-034` (prompt-injection/content-integrity screening, `CIS`) | `security.md` (`GSP-005`) | `DIRECT ELABORATION` | FULLY TRACED |
| `OBJ-035` (anti-scope boundaries) | none | — | NO DOWNSTREAM ELABORATION (scope statement, consistent with `architecture.md` §47.18's own treatment of it as non-scenario-bearing) |

### 6.2 Security Requirements (`SEC-001`–`016`)

| Requirement | Downstream Document(s) | Elaboration | Status |
|---|---|---|---|
| `SEC-001`, `SEC-005` (preserve security instructions/compression contracts) | `quality-gates.md` (compression-contract test suites, `QO-001`) | `DIRECT ELABORATION` | FULLY TRACED |
| `SEC-002`, `SEC-003`, `SEC-004`, `SEC-007` (cache authorization/isolation/retention/freshness) | `cache-strategy.md` | `DIRECT ELABORATION` | FULLY TRACED |
| `SEC-006` (retain auth/audit fields) | `observability.md` | `DIRECT ELABORATION` | FULLY TRACED |
| `SEC-008` (ledger/observability/provenance) | `observability.md` | `DIRECT ELABORATION` | FULLY TRACED |
| `SEC-009`, `SEC-010` (configuration, provider profiles) | `provider-matrix.md` | `REFERENCE ONLY` | INDIRECTLY TRACED |
| `SEC-011`–`016` (Hardening: `SGE`/`DGE`/`TMG`/`HAG`/`CIS` independence and mechanism) | `security.md` (entirely) | `DIRECT ELABORATION` | FULLY TRACED |

### 6.3 Non-Functional Requirements (`NFR-001`–`014`)

| Requirement | Downstream Document(s) | Elaboration | Status |
|---|---|---|---|
| `NFR-001` (correctness/quality gates) | `quality-gates.md` (entirely) | `DIRECT ELABORATION` | FULLY TRACED |
| `NFR-002`–`004` (security/privacy/multi-tenancy) | `security.md`, `cache-strategy.md` | `DIRECT ELABORATION` | FULLY TRACED |
| `NFR-005` (provider independence) | `provider-matrix.md`; `inference-optimization.md` (P5 boundary) | `DIRECT ELABORATION` | FULLY TRACED |
| `NFR-006` (observability) | `observability.md` (entirely) | `DIRECT ELABORATION` | FULLY TRACED |
| `NFR-007` (reliability/safe fallback) | `implementation-plan.md` (failure/recovery treatment per capability); `SCALING.md` (load-shedding) | `DIRECT ELABORATION` | FULLY TRACED |
| `NFR-008` (scalability) | `SCALING.md` (entirely) | `DIRECT ELABORATION` | FULLY TRACED |
| `NFR-009` (low overhead) | `eval.md` (net-savings/overhead accounting, consumed) | `REFERENCE ONLY` | INDIRECTLY TRACED |
| `NFR-010` (explainability) | `observability.md` (audit/explanation retrieval layer) | `DIRECT ELABORATION` | FULLY TRACED |
| `NFR-011` (determinism) | none | — | NO DOWNSTREAM ELABORATION |
| `NFR-012` (configurability) | `implementation-plan.md` (CI/CD sequencing, partial) | `REFERENCE ONLY` | INDIRECTLY TRACED |
| `NFR-013` (testability) | `eval.md`, `implementation-plan.md` (per-capability validation sub-phases) | `DIRECT ELABORATION` | FULLY TRACED |
| `NFR-014` (Control-Plane self-protection, `SPC`) | `SCALING.md` (`SC-001`) | `DIRECT ELABORATION` (capacity layer; `SPC` mechanism cited) | PARTIALLY TRACED |

### 6.4 Hardening Requirements (`H01`–`H20`)

| Requirement | Downstream Document(s) | Elaboration | Status |
|---|---|---|---|
| `H01` (operating modes), `H02` (advisor/enforcer boundary) | none | — | NO DOWNSTREAM ELABORATION |
| `H03` (self-protection, `SPC`) | `SCALING.md` | `DIRECT ELABORATION` (capacity layer only) | PARTIALLY TRACED |
| `H04` (net optimization economics) | `eval.md` (consumed) | `REFERENCE ONLY` | INDIRECTLY TRACED |
| `H05` (spend governance) | `security.md` | `DIRECT ELABORATION` | FULLY TRACED |
| `H06` (verifier calibration) | `quality-gates.md`, `eval.md` | `DIRECT ELABORATION` | FULLY TRACED |
| `H07` (data governance) | `security.md` | `DIRECT ELABORATION` | FULLY TRACED |
| `H08` (feasibility tiers) | `optimization-catalog.md` (catalog-level, deferred FTR mechanism forward), `provider-matrix.md` (partial) | `DIRECT ELABORATION` (partial) | PARTIALLY TRACED — `BLOCKED BY SOURCE GAP` |
| `H09` (memory authority) | none | — | NO DOWNSTREAM ELABORATION |
| `H10` (reflection/loop awareness) | `agent-optimization.md` (`AL-002`) | `DIRECT ELABORATION` | FULLY TRACED |
| `H11` (tool/MCP trust gate) | `security.md` (`TMG`) | `DIRECT ELABORATION` | FULLY TRACED |
| `H12` (cross-execution concurrency, `XEC`) | `SCALING.md` (`SC-002`) | `DIRECT ELABORATION` (capacity layer only) | PARTIALLY TRACED |
| `H13` (human approval) | `security.md` (`HAG`) | `DIRECT ELABORATION` | FULLY TRACED |
| `H14` (content integrity, `CIS`) | `security.md` (`CIS`) | `DIRECT ELABORATION` | FULLY TRACED |
| `H15` (decision explainability/audit) | `observability.md` (entirely) | `DIRECT ELABORATION` | FULLY TRACED |
| `H16` (execution-state portability) | none | — | NO DOWNSTREAM ELABORATION |
| `H17` (Layer 3 boundary: integration not implementation) | `inference-optimization.md` (entirely, this is its central invariant) | `DIRECT ELABORATION` | FULLY TRACED |
| `H18` (ownership boundaries) | `inference-optimization.md` | `DIRECT ELABORATION` | FULLY TRACED |
| `H19` (research claims as evidence, not guarantees) | every downstream document (each applies the "PROPOSED DEFAULT — VALIDATE LOCALLY" discipline) | `DIRECT ELABORATION` (as a cross-cutting discipline, not a single owning document) | FULLY TRACED |
| `H20` (anti-scope) | none | — | NO DOWNSTREAM ELABORATION |

### 6.5 Acceptance Criteria (`AC-001`–`053`)

**Correction note (this pass):** the prior draft grouped all 53 ACs by parent-requirement disposition without individually enumerating each ID, which does not let an auditor locate a single AC (e.g. "where is `AC-037` traced?") without inferring it. Every AC below is independently identified, using its exact architecture-section citation from `architecture.md` §44.6 ("AC-NNN -> Section X") and §44.10, matched to whichever downstream document (if any) elaborates that section's subject matter — cited, not re-derived. Grouped only where the underlying section citations are contiguous and the downstream disposition is genuinely identical.

| AC | Parent Requirement (by architecture section) | Downstream Elaboration | Evidence / Reference | Status |
|---|---|---|---|---|
| `AC-001` | §27 ledger (`OBJ-011`) | `observability.md` | Audit/ledger layer | FULLY TRACED |
| `AC-002` | §27.3 cost model (`OBJ-011`) | `observability.md` | Cost-model fields | FULLY TRACED |
| `AC-003` | §28 quality gates + §30 failure/fallback (`NFR-001`/`NFR-007`) | `quality-gates.md` (primary); `implementation-plan.md`/`SCALING.md` (§30 half) | Quality-gate decision model | FULLY TRACED |
| `AC-004` | §25 provider profiles + §27 ledger (`OBJ-013`) | `provider-matrix.md` | Provider capability matrix | FULLY TRACED |
| `AC-005` | §27.1 ledger fields (`OBJ-011`) | `observability.md` | Ledger field schema | FULLY TRACED |
| `AC-006` | §11.4 prompt cache (`OBJ-007`) | `cache-strategy.md` | `CACHE-001` | FULLY TRACED |
| `AC-007` | §11.5 semantic cache (`OBJ-007`) | `cache-strategy.md` | `CACHE-002` | FULLY TRACED |
| `AC-008` | §11.9 T1.12 Adaptive Retrieval/Top-K (`OBJ-001` family) | `optimization-catalog.md` | `TECH-NNN` adaptive Top-K entry | FULLY TRACED |
| `AC-009` | §11.6 pruner + §11.10 compressor (`OBJ-001`) | `optimization-catalog.md` | `TECH-NNN` entries | FULLY TRACED |
| `AC-010` | §10.1 model router + §13.4 cascade (`OBJ-008`) | `optimization-catalog.md`; `provider-matrix.md` (capability facts) | `TECH-013` | FULLY TRACED |
| `AC-011` | §12.3 output schema + §12.4 output length (`OBJ-002`) | `optimization-catalog.md` | `TECH-NNN` entries | FULLY TRACED |
| `AC-012` | §13.1 agent stop + §18.2 `AL-002` (`OBJ-010`) | `agent-optimization.md` | `AL-002`/`AL-006` | FULLY TRACED |
| `AC-013` | §13.2 tool output filter + §17.6 `TE-006` (`OBJ-006`) | `optimization-catalog.md` | `TECH-011`/`TE-006` | FULLY TRACED |
| `AC-014` | §29 security (`OBJ-014`) | `security.md` | GSP entries | FULLY TRACED |
| `AC-015` | §32.3 experimental validation matrix (`OBJ-012`) | `eval.md` | Statistical methodology | FULLY TRACED |
| `AC-016` | §30 failure/fallback (`NFR-007`) | `implementation-plan.md`; `SCALING.md` | Failure/recovery treatment; load-shedding | FULLY TRACED |
| `AC-017` | §32 benchmarking framework (`OBJ-012`) | `eval.md` | Benchmark-corpus methodology | FULLY TRACED |
| `AC-018` | §43.4 Evidence Classification Policy (cross-cutting, no single owner — same pattern as `H19`) | every downstream document (each applies the `PROPOSED DEFAULT — VALIDATE LOCALLY` discipline) | `conventions.md` §26 (cited) | FULLY TRACED (cross-cutting) |
| `AC-019` | §14.2 `OI-002` Cost-of-Optimization Controller (`OBJ-009`) | `optimization-catalog.md` | `OI-002` | FULLY TRACED |
| `AC-020` | §27.2 extended ledger (`OBJ-011`/`OBJ-026`) | `observability.md` | Extended ledger fields | FULLY TRACED |
| `AC-021` | §14.4 `OI-004` Context Utility/ROI Scorer | `optimization-catalog.md` | `OI-004` | FULLY TRACED |
| `AC-022` | §15.2 `CL-002` Context Freshness Scoring | `cache-strategy.md` | `CL-002` | FULLY TRACED |
| `AC-023` | §15.3 `CL-003` Dependency-Aware Cache Invalidation | `cache-strategy.md` | `CL-003` | FULLY TRACED |
| `AC-024` | §15.4 `CL-004` Reversible Optimization | `agent-optimization.md`; `implementation-plan.md` (`DA-023`/`024` cite `CL-004` as prerequisite) | `CL-004` | FULLY TRACED |
| `AC-025` | §15.5 `CL-005` Progressive Context Compaction (`OBJ-003`) | `optimization-catalog.md` | `CL-005` | FULLY TRACED |
| `AC-026` | §17.3 `TE-003` Dynamic Tool Loading | `optimization-catalog.md` | `TE-003` | FULLY TRACED |
| `AC-027` | §17.1 `TE-001` Tool Call ROI Predictor (`OBJ-006`) | `optimization-catalog.md` | `TE-001` | FULLY TRACED |
| `AC-028` | §18.3 `AL-003` Sub-Agent Value Predictor | `agent-optimization.md` | `AL-003` | FULLY TRACED |
| `AC-029` | §21.1/21.2 `EL-001`/`EL-002` | `eval.md` | `EL-001`/`EL-002` | FULLY TRACED |
| `AC-030` | §21.3 `EL-003` | `eval.md` | `EL-003` | FULLY TRACED |
| `AC-031` | §21.5 `EL-005` | `eval.md` | `EL-005` | FULLY TRACED |
| `AC-032` | §25 provider profiles (`OBJ-013`) | `provider-matrix.md` | Provider profile matrix | FULLY TRACED |
| `AC-033` | §14.5 `OI-005` Outcome-Based Optimization Engine | `optimization-catalog.md` | `OI-005` | FULLY TRACED |
| `AC-034` | §20.1 Compression Contracts | `quality-gates.md` | `QO-001` compression-contract test suites | FULLY TRACED |
| `AC-035` | §15.7 `CL-007` Session Phase Management | `agent-optimization.md` | `CL-007` §8 table | **PARTIALLY TRACED — `BLOCKED BY SOURCE GAP`** (`SOURCE-GAP-AGENTOPT-01`, no concrete per-phase values; explicitly not narrowed by `implementation-plan.md` §15) |
| `AC-036` | §7 three-layer architecture (cross-cutting architectural axiom, no single owning document) | none | — | **NO DOWNSTREAM ELABORATION** |
| `AC-037` | §14.1 `OI-001` + §31 observability | `observability.md` | `OI-001` decision explainability | FULLY TRACED |
| `AC-038` | §30 failure/fallback (`NFR-007`) | `implementation-plan.md`; `SCALING.md` | Failure/recovery treatment | FULLY TRACED |
| `AC-039` | §47.5 Operating Model (`OBJ-023`) | none | — | **NO DOWNSTREAM ELABORATION** |
| `AC-040` | §47.6 Advisor/Enforcer/Execution-Owner Boundary (`OBJ-024`) | none | — | **NO DOWNSTREAM ELABORATION** |
| `AC-041` | §47.4 `SPC` (`OBJ-025`/`NFR-014`) | `SCALING.md` (`SC-001`) | Capacity/backpressure layer atop `SPC` | PARTIALLY TRACED (capacity layer only; `SPC` mechanism cited) |
| `AC-042` | §47.4 Net Optimization Economics (`OBJ-026`) | `eval.md` (consumed) | Net-savings accounting | INDIRECTLY TRACED (`REFERENCE ONLY`) |
| `AC-043` | §47.2 `SGE` (`OBJ-027`/`SEC-011`) | `security.md` (`GSP-001`) | `GSP-001` | FULLY TRACED |
| `AC-044` | §47.4 `VCL` (`OBJ-028`) | `quality-gates.md`; `eval.md` | `VCL` procedure; `SOURCE-GAP-QUALGATE-01`/`02` narrowing | FULLY TRACED |
| `AC-045` | §47.2 `DGE` (`OBJ-029`/`SEC-012`/`013`) | `security.md` (`GSP-002`) | `GSP-002` | FULLY TRACED |
| `AC-046` | §47.10 `FTR` (`OBJ-030`) | `provider-matrix.md` (§9.1, naming/evidence half only) | `FTR` tier assignments | PARTIALLY TRACED — `BLOCKED BY SOURCE GAP` (`SOURCE-GAP-ARCH-02` FTR half, open) |
| `AC-047` | §47.3 Memory Authority (`OBJ-031`) | none | — | **NO DOWNSTREAM ELABORATION** |
| `AC-048` | §47.2 `TMG` (`SEC-014`) | `security.md` | `TMG` | FULLY TRACED |
| `AC-049` | §47.3 `XEC` (`OBJ-032`) | `SCALING.md` (`SC-002`) | Contention/capacity layer atop `XEC` | PARTIALLY TRACED (capacity layer only; `XEC` mechanism cited) |
| `AC-050` | §47.2 `HAG` (`OBJ-033`/`SEC-015`) | `security.md` (`GSP-004`) | `GSP-004` | FULLY TRACED |
| `AC-051` | §47.2 `CIS` (`OBJ-034`/`SEC-016`) | `security.md` (`GSP-005`) | `GSP-005` | FULLY TRACED |
| `AC-052` | §47.16 (`H15`) | `observability.md` | Audit/explanation retrieval layer | FULLY TRACED |
| `AC-053` | §47.11 Execution State Portability (`H16`) | none | — | **NO DOWNSTREAM ELABORATION** |

**AC status tally (independently recomputed from the 53 rows above):** 43 FULLY TRACED, 4 PARTIALLY TRACED (`AC-035`, `AC-041`, `AC-046`, `AC-049`), 1 INDIRECTLY TRACED (`AC-042`), 5 NO DOWNSTREAM ELABORATION (`AC-036`, `AC-039`, `AC-040`, `AC-047`, `AC-053`), 0 UNTRACED, 0 AMBIGUOUS. Sum: 43+4+1+5 = 53.

### 6.6 `FULLY TRACED` Audit (this correction pass)

Every row classified `FULLY TRACED` in §6.1–6.5 was individually re-audited against the live downstream documents, with particular scrutiny on `OBJ-005`, `OBJ-025`, `OBJ-030`, `OBJ-032`, `NFR-014`, `H03`, `H08` as named by this correction's own instructions. Result: `OBJ-025`, `OBJ-030`, `OBJ-032`, `NFR-014`, `H03`, and `H08` were **already** correctly classified `PARTIALLY TRACED` (not `FULLY TRACED`) in the prior draft — the audit confirms these were right the first time and required no change. `OBJ-005` remains `FULLY TRACED`: its core Layer 1/2 model-call-reduction mechanisms (caching, cascade, early exit) are each directly and completely elaborated in `optimization-catalog.md`/`cache-strategy.md`/`agent-optimization.md`; the `inference-optimization.md` P5 citation is correctly `REFERENCE ONLY` because P5 does not itself reduce model calls (it optimizes serving of calls already decided) and so is not required to complete this requirement's chain. **No `FULLY TRACED` classification was downgraded or upgraded by this audit** — the corrected coverage counts in §14 changed only because of the structural corrections in §6.1 and §6.5 (Corrections 1 and 2), not because any individual status was found to be evidentially wrong.

## 7. Reverse Traceability (Summary)

- **Security control → requirement:** every `GSP-NNN` in `security.md` traces to at least one `SEC-011`–`016`/`OBJ-027/029/033/034` — verified, no orphan `GSP-NNN`.
- **Quality gate → requirement:** every `QG-NNN` traces to `NFR-001`/`architecture.md` §28 — verified via `quality-gates.md` §26.2, cited not rebuilt.
- **Evaluation mechanism → requirement:** every `EL-NNN` traces to `OBJ-012`/`AC-015/017/029-031` — verified via `eval.md` §41's own traceability, cited.
- **Observability → requirement:** every `OBS-NNN` traces to `NFR-006/010`/`H15` — verified via `observability.md` §40, cited.
- **Scaling → requirement:** every `SC-NNN` traces to `NFR-008/014`/`OBJ-025/032`/`H03/H12` — verified via `SCALING.md` §48, cited.

## 8. Master Source-Gap Register

Consolidated across root-level (`ES`/`ARCH`), `scenario-matrix.md`, and all eleven downstream documents. Every entry below was independently located by grepping its origin file directly, not trusted from any prior summary or from this document's own generation prompt.

| Gap ID | Origin | Description (one line) | Current Status | Evidence |
|---|---|---|---|---|
| `SOURCE-GAP-ES-01` / `SOURCE-GAP-ARCH-01` | Engineering Spec §42.22; `architecture.md` §47.18 | No specific regulatory regime (GDPR/HIPAA/PCI-DSS) asserted — deployment-specific | **OPEN** — inherited without narrowing by `security.md` (`SOURCE-GAP-SECURITY-04`) and `observability.md`, both explicitly declining to close it | `security.md` §46; `observability.md` §21 |
| `SOURCE-GAP-ES-02` | Engineering Spec §42.22 | Exact budget thresholds/"consequential action" list left as policy config | **NOT A GAP REQUIRING RESOLUTION** — explicitly framed as intentionally deployment-configurable, not a specification defect | Engineering Spec §42.22 |
| `SOURCE-GAP-ES-03` / `SOURCE-GAP-ARCH-03` | Engineering Spec §42.22; `architecture.md` §47.18 | `OBJ-015`–`022` not canonically labeled at PS source | **RESOLVED** (2026-09-16 Final Foundational Document Reconciliation) | `architecture.md` §44.12 registry |
| `SOURCE-GAP-ES-04` / `SOURCE-GAP-ARCH-02` | Engineering Spec §42.22; `architecture.md` §47.18 | Exact `CIS`/`FTR` mechanism left as downstream decision | **NARROWED (CIS half) / OPEN (FTR half)** — `security.md` supplies a concrete `PROPOSED METHODOLOGY` CIS mechanism (§12); `provider-matrix.md` §9.1 explicitly states the FTR per-platform-tier half "remains genuinely open" and does not close it | `security.md` §12, §46; `provider-matrix.md` §9.1, §18 |
| `SOURCE-GAP-001` | `scenario-matrix.md` §32 | Client-truncated `user_input` behavior unspecified | **OPEN** — re-verified 2026-09-16, not touched by any downstream document | `scenario-matrix.md` §32 |
| `SOURCE-GAP-002` | `scenario-matrix.md` §32 | Memory-vs-execution-truth authority undefined at interface level | **RESOLVED** (2026-09-16, `interfaces.md` §43.13 `MemoryAuthorityCheck`) | `scenario-matrix.md` §32 |
| `SOURCE-GAP-003` | `scenario-matrix.md` §32 | Memory retention-expiry vs. active-reference interaction unspecified | **OPEN** | `scenario-matrix.md` §32 |
| `SOURCE-GAP-004` | `scenario-matrix.md` §32 | Workflow-template-level vs. per-execution mutation versioning unaddressed | **OPEN** | `scenario-matrix.md` §32 |
| `SOURCE-GAP-005` | `scenario-matrix.md` §32 | Workflow-reordering optimization vs. per-step authorization scope unaddressed | **OPEN** — independently confirmed by 3 sources (`EC-140`, `SCN-CMP-028`, this matrix) | `scenario-matrix.md` §32 |
| `SOURCE-GAP-006` | `scenario-matrix.md` §32 | Verifier-guided escalation (`AR-004`) vs. already-triggered non-idempotent side effect | **OPEN** — `VCL` (H06) formalizes confidence, not this side-effect question; orthogonal | `scenario-matrix.md` §32 |
| `SOURCE-GAP-007` | `scenario-matrix.md` §32 | Tool-argument optimization (`TE-002`) aggressiveness vs. task security-sensitivity | **OPEN** — `TMG` (H11) governs identity/schema trust, not this concern | `scenario-matrix.md` §32 |
| `SOURCE-GAP-008` | `scenario-matrix.md` §32 | `edge-cases.md` v1.0.0 had zero Dynamic-Execution edge cases | **RESOLVED** (2026-09-14) | `scenario-matrix.md` §32 |
| `SOURCE-GAP-009` | `scenario-matrix.md` §32 | No specific regulatory regime asserted for DGE (mirrors `ARCH-01`) | **DEPLOYMENT-SPECIFIC / non-blocking**, mirrors `ARCH-01` disposition | `scenario-matrix.md` §32 |
| `SOURCE-GAP-OPTCAT-01` | `optimization-catalog.md` | 25 `DA-NNN` modules lack individually-sourced priority tiers | **RESOLVED-IN-PLACE-DOWNSTREAM** — `implementation-plan.md` §14 dispositions all 25 (20 P1/3 P2/1 P3/1 P4); `optimization-catalog.md` itself unmodified | `implementation-plan.md` §14, §31 |
| `SOURCE-GAP-OPTCAT-02` | `optimization-catalog.md` | ES has no dedicated per-`DA-NNN` subsection (documentation-depth asymmetry vs. `TECH-NNN`) | **OPEN**, non-blocking, no downstream elaboration found | `optimization-catalog.md` §27 (gap table) |
| `SOURCE-GAP-PROVMTX-01`–`03` (3 distinct semantic gaps) | `provider-matrix.md` | No authoritative provider/model roster; unknown per-platform `reachable_modules`; context windows not machine-extractable | **OPEN**, non-blocking, no downstream elaboration found | `provider-matrix.md` §18 |
| `SOURCE-GAP-PROVMTX-04` | `provider-matrix.md` | Azure OpenAI pricing/prompt-cache-parity unverified at fetch time | **OPEN** — cited (`REFERENCE ONLY`, not narrowed) by `inference-optimization.md` (`INFOPT-001`) | `provider-matrix.md` §18; `inference-optimization.md` §12 |
| `SOURCE-GAP-CACHESTRAT-01`–`05` (5 distinct semantic gaps) | `cache-strategy.md` | No aggregate write-time cache-eligibility field; auth-in-key question; enum gaps for `PROVIDER_NATIVE`/`APPLICATION`; no storage-cost model; no dedicated `SCN-CACHE-*` scenario for 3 cache types | **OPEN**, non-blocking, no cross-document narrowing found | `cache-strategy.md` §41 (gap table) |
| `SOURCE-GAP-AGENTOPT-01` | `agent-optimization.md` | Six §15.7 session phases lack concrete per-phase policy values | **OPEN** — cited (`REFERENCE ONLY`) by `quality-gates.md`; explicitly considered and **declined to narrow** by `implementation-plan.md` §15 (reasoned that a second competing proposed-values table would be worse than deferring to the existing one) | `quality-gates.md` §29; `implementation-plan.md` §15 |
| `SOURCE-GAP-AGENTOPT-02`/`03` (2 distinct semantic gaps) | `agent-optimization.md` | No max sub-agent fan-out/budget-partitioning formula; no conflicting-sub-agent-findings aggregation rule | **OPEN**, non-blocking, no downstream elaboration found | `agent-optimization.md` §55 (gap table) |
| `SOURCE-GAP-INFOPT-01`/`02` (2 distinct semantic gaps) | `inference-optimization.md` | No source-defined provider-adapter negotiation-parameter schema; single-source exposure facts for most P5 capabilities | **OPEN**, non-blocking, no downstream elaboration found | `inference-optimization.md` §43 (gap table) |
| `SOURCE-GAP-QUALGATE-01`/`02` (2 distinct semantic gaps, identical disposition) | `quality-gates.md` | No minimum calibrated-verifier accuracy bar; no recalibration cadence | **NARROWED** (both) — `eval.md` §32 proposes 85% accuracy floor and a 90-day-or-drift-triggered cadence, both `PROPOSED DEFAULT — VALIDATE LOCALLY`; `quality-gates.md` itself unmodified | `eval.md` §32, §43 |
| `SOURCE-GAP-SECURITY-01`–`03` (3 distinct semantic gaps) | `security.md` | `CIS` layered detection has no local classifier/benchmark; `TMG` staleness interval has no default; no exhaustive content-rewriting-triggers-rescreening rule | **OPEN**, non-blocking, no downstream elaboration found | `security.md` §46 (gap table) |
| `SOURCE-GAP-SECURITY-04` | `security.md` | Literal ID inherits/repeats `ARCH-01`/`ES-01` | **INHERITED — not a distinct semantic gap; folded into the `ARCH-01`/`ES-01` root-level entry above, not counted separately in the total below** | `security.md` §46 |
| `SOURCE-GAP-OBS-01`–`04` (4 distinct semantic gaps) | `observability.md` | No sourced dashboard refresh intervals; no sourced alert thresholds; no sourced cardinality/sampling/SLO numbers; no dedicated scenario for two of the three dashboard tiers | **OPEN**, non-blocking, no downstream elaboration found | `observability.md` §43 (gap table) |
| `SOURCE-GAP-EVAL-01`–`05` (5 distinct semantic gaps) | `eval.md` | `RegressionReport` schema undefined; no sourced sample-size/power methodology; no sourced significance-level default; no sourced corpus-refresh cadence beyond `EC-072`'s illustrative example; `EL-002` has no dedicated scenario | **OPEN**, non-blocking, no downstream elaboration found (postdates `eval.md`; not addressed by `SCALING.md` or `implementation-plan.md`) | `eval.md` §43 (gap table) |
| `SOURCE-GAP-SCALING-01`–`05` (5 distinct semantic gaps) | `SCALING.md` | No scenario validates throughput/scale-out triggers; `XEC`'s correctness edge cases don't specify a contention-volume capacity threshold; no sourced numeric throughput/trigger/headroom/cooldown/replica values anywhere upstream; no scenario/edge-case for multi-tenant fairness; no canonical scaling catalog exists upstream (this document's own `SC-NNN` is `SOURCE-DERIVED`) | **OPEN**, non-blocking, not addressed by `implementation-plan.md` | `SCALING.md` §48 (gap table) |
| `SOURCE-GAP-IMPL-01`/`02` (2 distinct semantic gaps) | `implementation-plan.md` | 3 §36 P3 item names have no dedicated `TECH-NNN` ID; no sourced backing-technology requirement for the 4 named state classes (intentional — deferred to ADRs) | **OPEN**, non-blocking | `implementation-plan.md` §31 (gap table) |

**Correction note (this pass):** the prior draft's summary ("4 root + 9 scenario + 32 downstream = 45") did not mathematically reconcile with what the table above actually displays — the "32" undercounted by treating several grouped rows (e.g. `CACHESTRAT-01`–`05`, `EVAL-01`–`05`, `SCALING-01`–`05`) as one gap each instead of exploding them into their actual distinct semantic count, and it double-counted `SECURITY-04` as an independent downstream-native gap when it is in fact a literal-ID repeat of the already-counted root-level `ARCH-01`/`ES-01` entry.

**Reconciled counts, distinguishing physical IDs from distinct semantic gaps from inherited references, per this correction's own requirement:**
- **Physical/literal `SOURCE-GAP-*` ID occurrences across the corpus:** 7 root-level (`ES-01`–`04`, `ARCH-01`–`03`) + 9 `scenario-matrix.md`-native (`SOURCE-GAP-001`–`009`) + 38 downstream-document-native (including `SECURITY-04`) = **54 physical IDs**.
- **Distinct semantic gaps** (the underlying unique issue; `SECURITY-04` excluded here as inherited, not independent): 4 root-level + 9 `scenario-matrix.md`-native + 37 downstream-document-native = **50 distinct semantic gaps**.
- **Inherited/repeated references** (a downstream document citing an upstream gap without creating a new one, not counted toward the 50): `SECURITY-04` (1) — the only such case found; every other citation of `ARCH-01`/`ARCH-02` elsewhere (e.g. by `observability.md`, `provider-matrix.md`, `quality-gates.md`) is a `REFERENCE ONLY` mention of the same already-counted root gap, not a re-declared ID, so no further double-counting risk exists.

**Status breakdown of the 50 distinct semantic gaps** (verified by direct enumeration, not carried forward from the prior draft): 3 `RESOLVED` (`ES-03`/`ARCH-03`, `SOURCE-GAP-002`, `SOURCE-GAP-008`) + 1 `RESOLVED-IN-PLACE-DOWNSTREAM` (`OPTCAT-01`) + 2 `NARROWED` (`QUALGATE-01`, `QUALGATE-02`) + 1 split `NARROWED`/`OPEN` (`ARCH-02`/`ES-04`, counted once within the 4 root-level entries) + 1 `NOT-A-GAP` (`ES-02`) + 42 fully `OPEN`/non-blocking. Sum: 3+1+2+1+1+42 = 50.

`SOURCE-GAP-REQTRACE-01` — **Prior draft's downstream gap sub-count (32) did not reconcile with the actual distinct-semantic-gap count (37) when grouped rows were exploded.** *Origin:* this document's own §8, prior version. *Why it matters:* the summary total (45) was presented as reconciled but the arithmetic did not actually sum from the displayed rows. *Disposition:* corrected in this pass to 50 distinct semantic gaps (54 physical IDs, 1 inherited reference), per the reconciliation above; this document's live file is now the corrected source, not `optimization-catalog.md` or any other origin document. *Blocking:* No — a documentation-accuracy correction, not a new upstream requirement gap.

## 9. Master Contradiction Register

| Contradiction ID | Origin | Statement | Resolution Status |
|---|---|---|---|
| `CONTRA-001` (+ 2026-09-16 follow-up) | `scenario-matrix.md` §33 | Interface count baseline (62 → 71) inconsistency between `interfaces.md` and `conventions.md` | **RESOLVED** (2026-09-16) — both documents confirmed at 71 |
| `CONTRA-OPTCAT-01` | `optimization-catalog.md` §59 | `architecture.md` §23's "Production Maturity" field conflates Implementation Priority (P0–P5) with Maturity Level | **Resolved in `optimization-catalog.md` only** — `architecture.md` §23 itself left as-is, per the no-source-rewrites rule. The same Priority/Maturity separation discipline is subsequently *applied consistently* (not independently re-resolved) by `provider-matrix.md` §18, `cache-strategy.md` §46, and `implementation-plan.md` §4 — each explicitly states it inherits, rather than re-derives, this resolution |
| `CONTRA-OPTCAT-02` | `optimization-catalog.md` §59 | Apparent PARTIAL-edge-case-count mismatch (35 vs. 48) between generation-prompt baseline and `scenario-matrix.md` §39's narrative | **RESOLVED / HISTORICAL** — self-contained; a before/after artifact of `scenario-matrix.md`'s own 2026-09-16 Targeted Fix Pass, not a live cross-document disagreement |
| `CONTRA-OPTCAT-03` | `optimization-catalog.md` §59 | `EC-151` table-vs-narrative apparent inconsistency within `scenario-matrix.md` §39 | **RESOLVED / HISTORICAL** — same before/after pattern as `CONTRA-OPTCAT-02` |
| *(no `CONTRA-*` found)* | `provider-matrix.md`, `cache-strategy.md`, `agent-optimization.md`, `inference-optimization.md`, `quality-gates.md`, `security.md`, `observability.md`, `eval.md`, `SCALING.md`, `implementation-plan.md` | Each of these 10 documents' own Source Contradictions section independently reports `0` | Confirmed by direct grep of each file; this document's corpus-wide search found no contradiction any of them missed |

**Total:** 4 distinct contradiction IDs across the corpus (1 root-level, 3 `optimization-catalog.md`-native), all `RESOLVED` or `RESOLVED/HISTORICAL`; 0 currently open. No new `CONTRA-REQTRACE-NN` was found — no genuine new cross-document disagreement was discovered during this reconciliation; every apparent tension checked (e.g., the Priority-vs-Maturity pattern's repeated *application* across four documents) is refinement/consistent-application, not a new contradiction.

## 10. Orphan Requirements

**Correction note (this pass):** the prior draft stated "12 IDs/groups" here while separately stating "33 requirements have `NO DOWNSTREAM ELABORATION`" in §14 — neither number reconciled with what was actually listed, nor with each other. Both are corrected below by independent recount, now including the 5 `AC` IDs (`AC-036`, `AC-039`, `AC-040`, `AC-047`, `AC-053`) that inherit `NO DOWNSTREAM ELABORATION` from their parent requirement, which the prior draft's un-enumerated §6.5 could not surface individually.

**Individual orphan requirement IDs: 23.** Full enumeration: `OBJ-015`, `OBJ-016`, `OBJ-017`, `OBJ-018`, `OBJ-019`, `OBJ-020`, `OBJ-021`, `OBJ-022` (8 — Dynamic Execution components, individually listed per the Correction 1 expansion above), `OBJ-023`, `OBJ-024`, `OBJ-031`, `OBJ-035` (4), `NFR-011` (1), `H01`, `H02`, `H09`, `H16`, `H20` (5), `AC-036`, `AC-039`, `AC-040`, `AC-047`, `AC-053` (5). 8+4+1+5+5 = 23.

**Grouped/range display entries used for presentation: 16** (`OBJ-015`–`022` shown as one range; the remaining 15 IDs shown individually — 1+4+1+5+5 = 16). The relationship: 23 individual IDs collapse to 16 presentation entries only because 8 of them (`OBJ-015`–`022`) share one contiguous range and an identical disposition; every other orphan is already a singleton in both counts.

This reconciles exactly with §14's corrected coverage table (`NO DOWNSTREAM ELABORATION` = 23).

Classification for all 23: **EXPECTED — BASELINE/SCOPE REQUIREMENT.** Each remains fully and adequately owned at the `architecture.md`/`interfaces.md`/`conventions.md` layer, or (for the 5 `AC` IDs) inherits that same disposition from its parent `OBJ`/`H` requirement; none of the eleven downstream documents was ever scoped to elaborate Dynamic Execution mechanics, Control-Plane operating-mode taxonomy, memory authority, execution-state portability, cross-layer architectural axioms, or anti-scope/advisor-boundary statements — these are architectural invariants and scope boundaries, not optimization/security/quality/observability/eval/scaling/implementation subject matter. This is not a traceability defect.

## 11. Orphan Artifacts

No major downstream artifact was found with **NO DIRECT REQUIREMENT IDENTIFIED**. Every `TECH-NNN`, `DA-NNN`, `PROV-NNN`, `CACHE-NNN`, `GSP-NNN`, `OBS-NNN`, `EL-NNN`, `SC-NNN`, `QG-NNN`, and `INFOPT-NNN` entry traces to at least one `OBJ`/`SEC`/`NFR`/`H` per each document's own traceability section (cited, not rebuilt here). The `SC-NNN` catalog in `SCALING.md` is the one series introduced without an upstream-enumerated source list, but it is not orphaned — it traces to `NFR-008`/`014`/`OBJ-025`/`032` and is honestly labeled `SOURCE-DERIVED`, not presented as requirement-less.

## 12. Broken Traceability Chains

One systematic (not per-requirement) broken-chain pattern was found: `OBJ-025`/`032`/`NFR-014`/`H03`/`H12` (Control-Plane self-protection and cross-execution concurrency) each terminate their downstream chain at `SCALING.md`'s **capacity layer only** — `SPC`'s and `XEC`'s own correctness mechanisms remain `architecture.md`'s, and no scenario or edge case validates the capacity/throughput dimension `SCALING.md` itself adds (`SOURCE-GAP-SCALING-01`/`02`, §8 above). Chain: `Requirement → Architecture (SPC/XEC mechanism) → SCALING.md (capacity layer) → no validation`. This is recorded, not fabricated around — `SCALING.md`'s own readiness verdict already carries this honestly as a non-blocking gap.

## 13. Cross-Document Boundary Matrix

| Document | Owns (for this document's purposes) | Cites | Elaborates Requirements? |
|---|---|---|---|
| `architecture.md` §44 | Requirement → architecture-section mapping | — | Yes (source layer) |
| `conventions.md` §27 | Requirement → convention-section mapping | — | Yes (source layer) |
| `scenario-matrix.md` §36–39 | `EC`/`SCN`/`INTF` traceability | — | Yes (source layer) |
| Eleven downstream documents | Their own named subject matter (§13 of each's own scope table) | Requirements they don't own | Yes, per §6 above |
| **This document** | Forward hop + master gap/contradiction register + orphan/broken-chain analysis | Everything above | No new requirement content — reconciliation only |

## 14. Coverage Analysis

Denominator: 138 requirement IDs (35 `OBJ` + 16 `SEC` + 14 `NFR` + 20 `H` + 53 `AC`), each individually classified in §6 (including the now-individually-enumerated 53 `AC` rows in §6.5, corrected this pass).

**Correction note (this pass):** the prior draft's counts (91/8/6/33/0/0) were computed before `OBJ-015`–`022` were expanded into individual rows (§6.1, Correction 1) and before all 53 `AC`s were individually enumerated (§6.5, Correction 2). Recomputing from the corrected, fully-enumerated rows:

| Status | Count | % (of 138) |
|---|---|---|
| FULLY TRACED | 97 | 70% |
| PARTIALLY TRACED | 11 | 8% |
| INDIRECTLY TRACED | 7 | 5% |
| NO DOWNSTREAM ELABORATION (expected/scope) | 23 | 17% |
| UNTRACED | 0 | 0% |
| AMBIGUOUS | 0 | 0% |

Sum check: 97+11+7+23 = 138. Breakdown by category (independently recomputed from the individual rows in §6.1–6.5, not carried forward): `OBJ` (35) = 19 FULLY / 3 PARTIALLY / 1 INDIRECTLY / 12 NO DOWNSTREAM; `SEC` (16) = 14 FULLY / 0 / 2 INDIRECTLY / 0; `NFR` (14) = 10 FULLY / 1 PARTIALLY / 2 INDIRECTLY / 1 NO DOWNSTREAM; `H` (20) = 11 FULLY / 3 PARTIALLY / 1 INDIRECTLY / 5 NO DOWNSTREAM; `AC` (53) = 43 FULLY / 4 PARTIALLY / 1 INDIRECTLY / 5 NO DOWNSTREAM (§6.5's own tally). No requirement is `UNTRACED` or `AMBIGUOUS` — every one is either elaborated, cited-only, or an expected scope statement with no downstream elaboration needed. This reconciles exactly with §10's corrected orphan count (23 individual IDs).

## 15. Documentation-Set Completeness

- **Authoritative source documents:** 2 (`problemstatement.txt`, Engineering Spec).
- **Baseline documents:** 4 (`architecture.md`, `interfaces.md`, `conventions.md`, `edge-cases.md`) + `scenario-matrix.md` = 5.
- **Generated downstream documents:** 11 prior + this document = 12.
- **ADRs:** still pending — not generated, not decided here.

This repository remains **PRE-IMPLEMENTATION — Level 0 (RESEARCH)**. This document does not claim the documentation set, the system, or any capability is complete, production-ready, or implementation-ready — only that the requirement corpus has now been traced one hop further than any single prior document could reach, and that the corpus's open gaps/contradictions are now visible in one place rather than scattered across twelve files.

## 16. Traceability Risks / Remaining Open Areas

42 non-blocking `SOURCE-GAP-*` entries remain genuinely open (§8, corrected count); most concrete numeric thresholds across `eval.md`/`SCALING.md`/`security.md`/`observability.md` remain `PROPOSED DEFAULT — VALIDATE LOCALLY`, unvalidated by local production evidence (expected, given Level 0 status); `SOURCE-GAP-ARCH-02`'s FTR half and `SOURCE-GAP-OPTCAT-02`/`PROVMTX-01`–`03`/`CACHESTRAT-01`–`05`/`AGENTOPT-02`/`03`/`INFOPT-01`/`02` have no cross-document narrowing at all — any of these would be reasonable candidates for a future ADR or architecture-owner attention, but none is decided here.

**ADR candidate ≠ `BLOCKED BY ADR` (clarification added this correction pass).** An item named above as a "future ADR candidate" is one where a design/architecture decision should eventually be recorded, but requirements traceability and implementation planning can still proceed on the current documented baseline without it — this is the status of every item listed in this section. `BLOCKED BY ADR` is reserved for a requirement whose traceability or implementation genuinely cannot proceed at all without that decision first being made. Re-verified against the full corpus: no requirement in §6 was found to require this stronger status — every `PARTIALLY TRACED`/`BLOCKED BY SOURCE GAP` requirement (`OBJ-025`/`030`/`032`, `NFR-014`, `H03`/`H08`, `AC-035`/`041`/`046`/`049`) has a documented, usable interim disposition (a capacity layer, a narrowed evidence half, an explicit non-narrowing decision) that lets implementation planning continue; none is dead-ended pending an ADR. The `0` count for `BLOCKED BY ADR` in §17/§14 is retained as independently confirmed, not merely carried forward.

## 17. Final Report

**Document ID:** `EAIOC-REQTRACE-001`. **Version:** 1.0.1 (surgical correction pass applied — see correction notes in §6.1, §6.5, §6.6, §8, §10, §14, §16). **Status:** PRE-IMPLEMENTATION — Level 0 (RESEARCH). **Chain position:** 12th generated document, last before ADRs.

**What was corrected in this pass:** (1) `OBJ-015`–`022`'s grouped row, which mismatched 8 objective IDs against 13 component names, expanded into 8 individually-attributed rows per `architecture.md` §44.12's canonical registry; (2) all 53 `AC` IDs individually enumerated in §6.5 (previously grouped by parent-disposition only); (3) the master source-gap total corrected from 45 to 50 distinct semantic gaps (54 physical IDs, 1 inherited reference not double-counted), with a new `SOURCE-GAP-REQTRACE-01` recorded for the prior arithmetic error; (4) orphan-requirement counts corrected from an internally-inconsistent "12 IDs/groups" / "33 no-downstream" pair to a reconciled 23 individual IDs / 16 grouped-display entries; (5) every `FULLY TRACED` row re-audited — 0 downgrades or upgrades required, all were already evidentially correct; (6) an explicit ADR-candidate-vs-`BLOCKED BY ADR` distinction added, with the `0` `BLOCKED BY ADR` count independently reconfirmed.

**Requirement counts (freshly re-verified):** 35 `OBJ`, 16 `SEC`, 14 `NFR`, 20 `H`, 53 `AC`, 25 `DA`, 71 `INTF` — all match cited baselines; 0 discrepancies.
**Forward-trace rows:** 138 requirement IDs individually classified (§6, corrected); **97 FULLY TRACED, 11 PARTIALLY TRACED, 7 INDIRECTLY TRACED, 23 NO DOWNSTREAM ELABORATION (expected)**, 0 UNTRACED, 0 AMBIGUOUS, 0 BLOCKED BY ADR (independently reconfirmed — see §16).
**Master source-gap register:** **50 distinct semantic gaps** consolidated (§8, corrected) — 3 RESOLVED, 1 RESOLVED-IN-PLACE-DOWNSTREAM, 2 NARROWED, 1 split NARROWED/OPEN, 1 not-a-gap, 42 OPEN/non-blocking. 1 new `SOURCE-GAP-REQTRACE-01` (the arithmetic-reconciliation finding itself).
**Master contradiction register:** 4 distinct entries (§9, unchanged — re-verified, no error found here), all RESOLVED or RESOLVED/HISTORICAL, 0 currently open. 0 new `CONTRA-REQTRACE-NN`.
**Orphan requirements:** **23 individual IDs / 16 grouped-display entries** (§10, corrected), all classified EXPECTED — BASELINE/SCOPE REQUIREMENT, not a defect. **Orphan artifacts:** 0 found (§11, unchanged). **Broken chains:** 1 systematic pattern found and recorded, not fabricated around (§12, unchanged).
**File-scope confirmation:** only `docs/requirements-traceability.md` was modified in this correction pass. No upstream document (`problemStatement.txt`, Engineering Spec, `architecture.md`, `interfaces.md`, `conventions.md`, `edge-cases.md`, `scenario-matrix.md`) was modified. No sibling downstream document (`optimization-catalog.md`, `provider-matrix.md`, `cache-strategy.md`, `agent-optimization.md`, `inference-optimization.md`, `quality-gates.md`, `security.md`, `observability.md`, `eval.md`, `SCALING.md`, `implementation-plan.md`) was modified. `CLAUDE.md` was not modified. No ADR was created or decided.
**Next document:** ADRs (not generated here).

## Requirements Traceability Readiness

1. All requirement IDs and counts independently verified against live files — Yes (§4). 2. `architecture.md` §44 cited, not re-derived — Yes. 3. `conventions.md` §27 cited, not re-derived — Yes. 4. `scenario-matrix.md` §36–39 preserved as owner — Yes. 5. Forward downstream trace completed for all 138 requirement IDs, individually (§6, corrected — `OBJ-015`–`022` expanded, all 53 `AC` enumerated) — Yes. 6. `NO DOWNSTREAM ELABORATION` cases preserved honestly (23 individual IDs, corrected from an internally-inconsistent "33") — Yes. 7. All source gaps consolidated and mathematically reconciled (50 distinct semantic gaps / 54 physical IDs / 1 inherited reference, corrected from an unreconciled "45") — Yes (§8). 8. All contradictions consolidated (4, re-verified unchanged) — Yes (§9). 9. Cross-document gap-narrowing/resolution relationships verified beyond the 4 named candidates (found 2 additional: `OBJ-030`/`H08` FTR-half-open distinction, and the Priority/Maturity pattern's consistent application across 3 further documents) — Yes. 10. Root-level gaps independently verified — Yes. 11. `implementation-plan.md`'s `P0`–`P5`/`DA-NNN` dispositions preserved as authoritative, not reopened — Yes. 12. Security/validation/observability/scaling-sensitive requirements traced — Yes. 13. Every `FULLY TRACED` row individually re-audited, with 0 downgrades/upgrades required (§6.6) — Yes. 14. ADR candidate explicitly distinguished from `BLOCKED BY ADR`, with the `0` count independently reconfirmed rather than merely carried forward (§16) — Yes. 15. No upstream document modified — Yes. 16. Document remains clearly Level 0/pre-implementation — Yes (§15).

All applicable readiness criteria are satisfied, including the 6 corrections applied in this pass. No blocking gap prevents this document from serving its stated purpose.

READY FOR NEXT DOCUMENTATION PHASE
