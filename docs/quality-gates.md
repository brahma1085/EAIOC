# Quality Gates

**Status:** PRE-IMPLEMENTATION — Level 0 (RESEARCH), documentation-only
**Version:** 1.0.0
**Generated:** 2026-09-17
**Document role:** Sixth document in the `docs/prompts/generate-*.prompt.md` chain — `optimization-catalog.md` → `provider-matrix.md` → `cache-strategy.md` → `agent-optimization.md` → `inference-optimization.md` → **`quality-gates.md`** → (next) `security.md`.
**Generated from:** `docs/prompts/generate-quality-gates.prompt.md`

**Source documents read in full or by targeted section/ID lookup for this generation:**

| # | Document | Sections/IDs used |
|---|---|---|
| 1 | `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt` (PS) | §9 (Quality Gates), §10 (Security/Governance), §52.6 (H06 — Verifier Confidence and Calibration), OBJ-026/028/029/030 |
| 2 | `Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md` (ES, Rev 1.4) | §42.6 (H06 elaboration, by cross-reference) |
| 3 | `architecture.md` (ARCH, Rev 1.3) | §20 (QO-001–003), §28 (Quality Gates), §29 (SEC-001–016), §32 (Benchmarking Framework), §35 (Regression Testing), §38 (configuration), §42 (Maturity Model), §44.9/§44.10/§44.12 (traceability), §47.4.1–47.4.3 (SPC, Net Optimization Economics, VCL), §47.18 |
| 4 | `interfaces.md` (INTF, v1.2.0) | §17 (`QualityValidator`, INTF-029), §43.9 (`VerifierCalibrationLayer`, INTF-071), §43.10 (Operating Mode / Decision Ownership) |
| 5 | `conventions.md` (CONV, Rev 1.1.0) | §7.10 (VCL/H06), §15.1–15.5 (Quality Gate, Compression Contracts, Delayed-Relevance, Verifier-Guided Escalation), §16 (Fallback conventions), package layout `quality/` |
| 6 | `edge-cases.md` (EDGE, v1.2.0) | EC-020, EC-052, EC-061, EC-062, EC-063, EC-065, EC-069, EC-071, EC-072, EC-076, EC-077, EC-154, EC-162, EC-163, EC-164, EC-165, EC-211 — all verified present at the cited line/anchor before citation |
| 7 | `scenario-matrix.md` (SCN) | `SCN-QUAL-001`–`005` (read in full); the EC↔SCN reconciliation table rows for every EC above |
| 8 | `optimization-catalog.md` | Scope Boundary row deferring "concrete quality-gate thresholds, verifier calibration procedures, and compression-contract test suites" here |
| 9 | `provider-matrix.md` | Consulted; no provider-specific quality constraint was found genuinely necessary to restate here |
| 10 | `cache-strategy.md` | Its own deferral of "quality-gate thresholds/methodology" to this document |
| 11 | `agent-optimization.md` | `AR-004` (explicitly states it "does not redefine VCL's own calibration mechanism") |
| 12 | `inference-optimization.md` | H17/H18 boundary language, cited where relevant to measurement separation |
| — | Root `CLAUDE.md` | Scope, ID-series convention, process discipline |

Every `EC-NNN`, `SCN-QUAL-NNN`, `AC-NNN`, and `OBJ-NNN` cited in this document was independently located and read at its actual current line in the file named above before being cited — none is assumed from a prior summary.

---

## 1. How to Read This Document

- **Evidence tags** used throughout: `PROPOSED DEFAULT — VALIDATE LOCALLY` (a concrete threshold with no local benchmark backing it), `PROPOSED METHODOLOGY` (a procedure not directly specified by any source), `SOURCE-GAP-QUALGATE-NN` (a genuine gap, recorded not invented-around). Nothing in this document may be read as production-validated.
- **Maturity:** every threshold and procedure proposed here is Level 0 — RESEARCH (`architecture.md` §42) until it has actually run through the benchmarking framework (§9 below) against this organization's own workload. A concrete number appearing in this document is a **starting configuration to validate**, never a guarantee.
- **Coverage honesty:** where `scenario-matrix.md` already marks an EC↔SCN mapping `PARTIALLY COVERED`, this document preserves that classification exactly — it does not manufacture a new scenario to make the number look better, and does not claim this document itself is what closes that gap unless it explicitly says so.

---

## 2. Scope and Control-Plane Boundary

`quality-gates.md` owns the **Control-Plane quality decision layer**: how EAIOC resolves applicable quality requirements, determines quality constraints, identifies appropriate evidence, establishes and uses a baseline, evaluates optimization impact, determines evidence sufficiency, evaluates quality risk, allows or rejects an optimization, requires monitoring or revalidation, escalates uncertain quality decisions, detects quality regression, and records the decision and its rationale.

> **Boundary invariant:** EAIOC controls whether an optimization is acceptable from a quality perspective; it does not own the underlying model, inference engine, benchmark infrastructure, application business logic, evaluator internals, or domain-specific truth.

Consistent with that boundary, this document describes the Control Plane's own role as: **detect → classify → evaluate → compare → authorize → reject → escalate → revalidate → record** — never as the implementation owner of an evaluator, a benchmark engine, or model/provider infrastructure.

This document elaborates exactly what `optimization-catalog.md` and `cache-strategy.md` both defer here: **concrete quality-gate thresholds, verifier calibration procedures, and compression-contract test suites.** It does not redefine:

- The `QualityValidator`/`VerifierCalibrationLayer` interface schemas (`interfaces.md` §17/§43.9) — cited, not redefined.
- `AR-004`'s agent-loop escalation-decision logic (`agent-optimization.md`) — cited, not redefined. `AR-004` itself states it "does not redefine VCL's own calibration mechanism"; this document is exactly that deferred mechanism.
- The seven canonical cache types (`cache-strategy.md`) or the P5/H17/H18 boundary (`inference-optimization.md`) — cited where relevant, not restated.
- CIS/TMG content-integrity/tool-trust mechanism detail — that is `security.md`'s future scope. A quality-gate failure (optimization fidelity) and a security-screening failure (content trustworthiness) are adjacent but distinct; this document never conflates them.
- Benchmark-corpus construction and A/B statistical methodology — that is `eval.md`'s future scope.
- Telemetry/dashboard implementation — that is `observability.md`'s future scope.
- Canonical cross-document requirements traceability — that is `requirements-traceability.md`'s future scope, not yet created.

---

## 3. Central Quality Invariant

> **Token reduction shall never be accepted without quality validation** (PS §9; `architecture.md` §28).

Every optimization technique is evaluated **Baseline vs. Optimized** across the 10 dimensions in §5. Each technique has a **configurable quality threshold**. If that threshold is violated:

1. Roll back the optimization.
2. Fall back to the baseline representation.
3. Record the failure.
4. **Do not** report the optimization as a successful saving.

This sequence is stated identically in PS §9, `architecture.md` §28, and `conventions.md` §15.1–15.2. This document does not strengthen, weaken, or reorder it — only makes it concrete per dimension (§6) and adds the configuration-time floor rule the sequence itself does not state (§12).

### 3.1 Mandatory Per-Optimization Validation Invariant

For every optimization application `Oᵢ`:

```text
Baseline State Sᵢ
    ↓
Apply Optimization Oᵢ
    ↓
Optimized State Sᵢ'
    ↓
Quality Validation Qᵢ
    ↓
ALLOW / ALLOW_WITH_MONITORING / REQUIRE_REVALIDATION / ESCALATE / DO_NOT_OPTIMIZE
```

The optimized result `Sᵢ'` **must not** be admitted to the next optimization stage or execution stage unless the applicable quality decision (§6.2) permits it. Concretely, for a pipeline applying more than one optimization:

```text
O1 → Q1 → O2 → Q2 → O3 → Q3
```

is required. The following is **prohibited**:

```text
O1 → O2 → O3 → Q(final)
```

Aggregate, post-hoc validation of only the final result cannot substitute for validating each individual optimization application: a single end-of-pipeline check has no way to attribute a detected defect back to the specific transformation that introduced it, and cannot prevent an already-degraded intermediate state from having fed forward into a subsequent optimization before anyone checked it. This is additive to, not a replacement for, the rollback sequence above — it specifies *when* that sequence runs (after every `Oᵢ`, before `Oᵢ`'s result is used by anything downstream), not a new sequence.

> **Scope clarification — "every optimization" vs. "every quality dimension":** every optimization *application* gets a validation checkpoint (§3.2) — this does **not** mean every one of the 10 `QG-NNN` dimensions (§5) runs at every checkpoint. Only the dimensions applicable to that specific optimization/content type/workload run, per Algorithm A/B's own resolution step (§6.3). Unconditionally running all 10 dimensions on every optimization would itself violate this document's economics discipline (§17) by imposing unjustified evaluation overhead that Algorithm H's `DO_NOT_OPTIMIZE`/overhead-vs-benefit logic exists to prevent.

This applies uniformly to every optimization category listed in §13 — token, context, compression, pruning, dedup, summarization, retrieval, cache-transformation, inference/serving, agent, model/provider routing, tool/MCP, multi-agent/sub-agent, coding-agent, agentic-RAG, and any future technique admitted through the optimization framework. No optimization category is exempt from having a checkpoint; categories differ only in *which* dimensions apply at that checkpoint (§13's table).

### 3.2 Optimization Validation Checkpoint (conceptual, not a new interface)

**Optimization Validation Checkpoint** is a document-local *conceptual* term for the Control-Plane relationship §3.1 requires — it is **not** a new API, schema, or interface. `QualityValidator` and `VerifierCalibrationLayer` (`interfaces.md` §17/§43.9) remain the only interfaces involved; they are cited, not redefined, and nothing here adds a field to either. A checkpoint conceptually associates:

- the optimization being evaluated (`Oᵢ`),
- pre-optimization state/version (`Sᵢ`),
- post-optimization state/version (`Sᵢ'`),
- applicable quality requirements/gates (Algorithm A/B, §6.3),
- evidence (Algorithm C),
- validation result (Algorithm D),
- quality decision (Algorithm E),
- fallback/recovery action taken, where applicable (Algorithm G),
- rationale,
- provenance,
- revalidation requirement (Algorithm F).

Every `Oᵢ` in a pipeline has exactly one such checkpoint before its result is admitted downstream — this is the concrete meaning of §3.1's diagram, and the reason §6.1's decision flow (below) is understood to run once per optimization application, not once per request pipeline.

---

## 4. Quality Dimensions

The current authoritative source (`architecture.md` §28, verbatim-identical to PS §9) defines exactly these 10 dimensions, in this order. No discrepancy was found between PS §9 and `architecture.md` §28 — both were read directly and match verbatim, so the taxonomy below is used as-is with no documented deviation:

| # | Dimension |
|---|---|
| 1 | Accuracy |
| 2 | Task completion |
| 3 | Retrieval quality |
| 4 | Factuality |
| 5 | Schema compliance |
| 6 | Semantic equivalence |
| 7 | Safety |
| 8 | User satisfaction |
| 9 | Latency |
| 10 | Cost |

---

## 5. `QG-001`–`QG-010`: Canonical Quality Gate Entries

New document-scoped ID series **`QG-NNN`**, exactly 10, one per dimension above, matching the ID-series convention established by `cache-strategy.md`'s `CACHE-NNN` (7) and `inference-optimization.md`'s `INFOPT-NNN` (11) — per root `CLAUDE.md`'s "give it an ID in the matching series" rule. No `TECH`, `DA`, `AL`, `AR`, `CACHE`, `PROV`, or `INFOPT` ID is created or modified.

### QG-001 — Accuracy

| Field | Content |
|---|---|
| **Dimension** | Accuracy |
| **Purpose** | Protects against an optimization silently changing the correctness of the model's output relative to the unoptimized baseline. |
| **Measurement Methodology** | Baseline vs. Optimized comparison on a task-appropriate correctness metric (exact-match, reference-answer comparison, deterministic rule check) — the specific metric is workload-defined, not fixed by this document. |
| **Applicable Validator Type(s)** | `DETERMINISTIC_RULE`, `UNIT_TEST`, `BUSINESS_RULE` where a ground-truth answer or rule exists; `MODEL_BASED_EVAL` where it does not (INTF §17). |
| **Evidence Required** | A reference answer, deterministic rule, or calibrated model-based judgment (§10 governs calibration). |
| **Evidence Strength/Limitations** | Deterministic checks are strongest; `MODEL_BASED_EVAL` is only as strong as its calibration status (§10) — an uncalibrated model-based accuracy check is `Unknown` strength, not `Strong`. |
| **Threshold/Acceptance Rule** | Configurable per `OptimizationPolicy.min_quality_score` — no authoritative numeric floor exists in any source. `Threshold = policy/configuration/workload-defined`. Any concrete starting value used in local configuration is `PROPOSED DEFAULT — VALIDATE LOCALLY`. |
| **Deterministic vs. Probabilistic** | Both apply, depending on task type; a probabilistic accuracy verifier's confidence must be `< 1.0` unless `calibrated = true` (OBJ-028). |
| **Failure/Fallback Action** | `RESTORE_ORIGINAL` (primary), `ESCALATE_MODEL` or `RETRY` where the fallback policy permits (INTF §17 `FallbackAction`). |
| **Compression Contract Interaction** | Directly interacts with `QO-001`'s invariant-preservation goal (§9) — an accuracy failure after compression is frequently a compression-contract violation. |
| **Revalidation Triggers** | Model/provider change, technique change, benchmark-corpus refresh (§72's staleness concern, cross-referenced in §14). |
| **Validated By** | `SCN-QUAL-001` (rollback on threshold failure) — collective, not accuracy-specific; no dedicated accuracy-only scenario exists. |
| **Traceability** | PS §9; ARCH §28; INTF §17; CONV §15.1–15.2. |

### QG-002 — Task completion

| Field | Content |
|---|---|
| **Dimension** | Task completion |
| **Purpose** | Protects against an optimization (early exit, context pruning, tool-output filtering) causing the agent/application to stop before the task's actual completion criterion is met. |
| **Measurement Methodology** | Baseline vs. Optimized comparison of whether the stated completion criterion was actually satisfied (e.g., re-running a test suite, checking a structured task-success signal). |
| **Applicable Validator Type(s)** | `TASK_SUCCESS_CHECK` (INTF §43.9 `verifier_type`), `INTEGRATION_TEST`, `BUSINESS_RULE`. |
| **Evidence Required** | An explicit, task-defined completion criterion and a way to check it (this document does not invent one where none exists — a genuinely ambiguous completion criterion is a `SOURCE-GAP`-worthy condition at the application layer, out of this document's scope to resolve). |
| **Evidence Strength/Limitations** | Strong where a deterministic completion check exists (e.g., a build/test pass); weak/`Unknown` where completion is judged subjectively. |
| **Threshold/Acceptance Rule** | Binary in most cases (criterion met / not met) rather than a continuous score; `Threshold = policy/configuration/workload-defined` where a partial-completion scoring scheme is used. |
| **Deterministic vs. Probabilistic** | Predominantly deterministic (`TASK_SUCCESS_CHECK`/`INTEGRATION_TEST`); probabilistic only where completion is judged by `MODEL_BASED_EVAL`. |
| **Failure/Fallback Action** | `INCREASE_CONTEXT`, `INCREASE_REASONING`, `RETRY`, or `ESCALATE_MODEL` — this is exactly the set `conventions.md` §15.5/AR-004 already names as escalation triggers; cited, not redefined. |
| **Compression Contract Interaction** | Directly ties to the Agent context type's invariants (§9): "pending actions" and "unresolved failures" surviving compression is a precondition for correctly judging task completion afterward. |
| **Revalidation Triggers** | Workflow-state change, tool availability change, policy change affecting the completion criterion itself. |
| **Validated By** | `SCN-QUAL-003` (token savings achieved but task quality fails — not counted as a win) — this is the directly-covering scenario per its own title and `EC-063`'s DIRECTLY COVERED mapping to it. |
| **Traceability** | PS §9; ARCH §28; CONV §15.5 (AR-004 cross-reference); EC-063; SCN-QUAL-003. |

### QG-003 — Retrieval quality

| Field | Content |
|---|---|
| **Dimension** | Retrieval quality |
| **Purpose** | Protects against Adaptive Top-K, context pruning, or reordering degrading what was actually retrieved for a RAG-shaped task. |
| **Measurement Methodology** | Baseline vs. Optimized comparison of recall/precision against the retrieval set that would have been used without the optimization. |
| **Applicable Validator Type(s)** | `CITATION_CHECK`, `EMBEDDING_SIMILARITY`, `BUSINESS_RULE` (for required-source-presence rules). |
| **Evidence Required** | The pre-optimization retrieval set (or a proxy for it) to compare against; source/citation metadata. |
| **Evidence Strength/Limitations** | `EMBEDDING_SIMILARITY` alone is explicitly insufficient for factual specificity per `EC-063` — retrieval-quality measurement must not rely on embedding similarity in isolation where factual/citation correctness matters (see `QG-004` below, which states this most directly). |
| **Threshold/Acceptance Rule** | `Threshold = policy/configuration/workload-defined`; no authoritative numeric recall/precision floor exists in any source. |
| **Deterministic vs. Probabilistic** | Citation-presence checks are deterministic; semantic-relevance judgments are probabilistic and subject to §10's calibration discipline. |
| **Failure/Fallback Action** | `INCREASE_CONTEXT` (widen retrieval depth), `RESTORE_ORIGINAL`, `RETRY`. |
| **Compression Contract Interaction** | Directly ties to the RAG context type's invariants (§9): entities, numbers, dates, citations, source attribution. |
| **Revalidation Triggers** | Source-corpus update, embedding-model change, retrieval-depth policy change. |
| **Validated By** | No dedicated `SCN-QUAL-NNN` isolates retrieval quality specifically; `SCN-QUAL-002` (semantic drift in compressed output) is the closest, thematically-related scenario — recorded as thin/analogous coverage per the prompt's §46 discipline, not force-fit as direct. |
| **Traceability** | PS §9; ARCH §28; CONV §15.3 (RAG invariants); EC-063 (indirectly, via the shared factuality lesson). |

### QG-004 — Factuality

| Field | Content |
|---|---|
| **Dimension** | Factuality |
| **Purpose** | Protects against a compressed/optimized answer being semantically plausible but factually wrong — the exact failure `EC-063` documents. |
| **Measurement Methodology** | **Multi-signal, not embedding-similarity-alone**: (a) embedding/semantic similarity between baseline and optimized content, **and** (b) entity-preservation rate, **and** (c) numeric-value-preservation rate. `EC-063`'s own finding is explicit: a document stating "184°C" compressed to "a high temperature" scores 0.92 on embedding similarity (both discuss temperature) yet loses the exact factual answer — embedding similarity captures semantic theme, not factual specificity. Any `QG-004` implementation that uses `EMBEDDING_SIMILARITY` alone is non-compliant with this entry. |
| **Applicable Validator Type(s)** | `EMBEDDING_SIMILARITY` **combined with** `CITATION_CHECK`/`BUSINESS_RULE`-style entity/numeric-extraction checks — never `EMBEDDING_SIMILARITY` in isolation for factuality-sensitive content. |
| **Evidence Required** | The set of entities/numeric values present in the baseline content, to check for survival in the optimized content. |
| **Evidence Strength/Limitations** | Embedding similarity alone: weak for factuality (per `EC-063`). Combined multi-signal check: strong, but still Level 0 — no local benchmark has validated any specific entity/numeric-extraction implementation. |
| **Threshold/Acceptance Rule** | `PROPOSED METHODOLOGY`: if any named entity or numeric value present in the baseline is absent from the optimized content, the gate fails — a binary preservation check rather than a continuous similarity score, because `EC-063` shows a continuous similarity score alone is exactly what fails to catch this class of defect. This rule is proposed by this document (no upstream source states it as a numeric formula), but it directly operationalizes `EC-063`'s own stated remediation ("quality gate for fact-intensive content includes: (a) embedding similarity AND (b) entity/numeric value preservation rate... if numeric values or named entities are absent in the compressed version: quality gate fails"). |
| **Deterministic vs. Probabilistic** | The entity/numeric-preservation check is effectively deterministic (presence/absence); the embedding-similarity component remains probabilistic and subject to §10. |
| **Failure/Fallback Action** | `RESTORE_ORIGINAL` — factuality failures on fact-intensive content should not be silently escalated to a stronger model without also restoring the lost specificity, since the defect is information loss, not model capability. |
| **Compression Contract Interaction** | This is the concrete test-suite content for the RAG row of `QO-001` (§9) — entity/numeric/citation preservation. |
| **Revalidation Triggers** | Source-content update, compression-technique change. |
| **Validated By** | `SCN-QUAL-003` (`EC-063`'s own DIRECTLY COVERED mapping). |
| **Traceability** | PS §9; ARCH §28; EDGE EC-063 (verified at line 2440); SCN-QUAL-003 (verified at line 5575); CONV §15.3. |

### QG-005 — Schema compliance

| Field | Content |
|---|---|
| **Dimension** | Schema compliance |
| **Purpose** | Protects against an optimized (e.g., output-schema-enforced, output-length-controlled) response failing to conform to its declared structured-output contract. |
| **Measurement Methodology** | Direct schema validation of the optimized output against its declared schema. |
| **Applicable Validator Type(s)** | `SCHEMA` (INTF §17's own first-listed enum value — the most direct fit of any dimension to a single validator type). |
| **Evidence Required** | The declared output schema itself. |
| **Evidence Strength/Limitations** | Strong and deterministic — schema validation is a binary pass/fail with no calibration question, `confidence` definitionally `1.0` per OBJ-028. |
| **Threshold/Acceptance Rule** | Binary (valid/invalid) — no continuous threshold applies. |
| **Deterministic vs. Probabilistic** | Deterministic only. |
| **Failure/Fallback Action** | `RETRY` (regenerate against the schema), `RESTORE_ORIGINAL`, `DISABLE_OPTIMIZATION` (for the specific output-control technique responsible). |
| **Compression Contract Interaction** | Ties to the Coding context type's "API contracts" invariant (§9) where the optimized artifact is itself a structured interface. |
| **Revalidation Triggers** | Schema-version change. |
| **Validated By** | No dedicated `SCN-QUAL-NNN` isolates schema compliance specifically — `NO DEDICATED SCENARIO`, recorded honestly rather than force-fit. |
| **Traceability** | ARCH §28; INTF §17 (`ValidatorSpec.validator_type = SCHEMA`); ARCH §12.3 (Output Schema Selector, by architectural cross-reference). |

### QG-006 — Semantic equivalence

| Field | Content |
|---|---|
| **Dimension** | Semantic equivalence |
| **Purpose** | Protects against a compressed/reworded representation changing meaning even where surface-level checks (schema, entity presence) pass. |
| **Measurement Methodology** | Baseline vs. Optimized semantic-equivalence check — most commonly `LLM_JUDGE_SEMANTIC_EQUIVALENCE` (INTF §43.9's own named enum value), never treated as ground truth per OBJ-028. |
| **Applicable Validator Type(s)** | `EMBEDDING_SIMILARITY`, `MODEL_BASED_EVAL`, and INTF §43.9's dedicated `LLM_JUDGE_SEMANTIC_EQUIVALENCE` verifier type. |
| **Evidence Required** | Calibration evidence for the LLM-judge verifier (§10) before its output is trusted to gate a decision. |
| **Evidence Strength/Limitations** | Probabilistic by nature — this dimension is the canonical example PS §52.6/OBJ-028 uses for "accepting a compressed context as semantically-equivalent" requiring calibration before production trust. |
| **Threshold/Acceptance Rule** | `acceptance_threshold` from `VerificationResult` (INTF §43.9) — configurable per task/intent/tenant (ARCH §38); no universal numeric value is authoritative. |
| **Deterministic vs. Probabilistic** | Probabilistic — confidence must be `< 1.0` unless `calibrated = true` (OBJ-028, the exact invariant this dimension exists to enforce). |
| **Failure/Fallback Action** | Below threshold: `escalation_required = true` → apply `FallbackAction` (`RESTORE_ORIGINAL`, `ESCALATE_MODEL`, `DISABLE_OPTIMIZATION`) — silent acceptance is prohibited (`conventions.md` §7.10). |
| **Compression Contract Interaction** | This dimension is effectively the general-purpose backstop for all three `QO-001` context types when a specific per-invariant check (entity/schema/objective) is not itself sufficient. |
| **Revalidation Triggers** | Verifier-model change, drift signal (§10.D). |
| **Validated By** | `SCN-QUAL-004` (probabilistic verifier confidence below calibrated threshold triggers fallback), `SCN-QUAL-005` (verifier drift). |
| **Traceability** | PS §52.6 (verified at line 3901); OBJ-028; INTF §43.9; SCN-QUAL-004/005; EC-162/163/164/165/211 (see §11). |

### QG-007 — Safety

| Field | Content |
|---|---|
| **Dimension** | Safety |
| **Purpose** | Protects against an optimization removing or weakening a safety-relevant instruction, constraint, or output property. |
| **Measurement Methodology** | `SECURITY_CHECK`-type validation confirming safety-relevant constraints declared in the compression contract (§9's Coding-row "security constraints" and the general `SEC-005` requirement) survived the transformation. |
| **Applicable Validator Type(s)** | `SECURITY_CHECK` (INTF §17). |
| **Evidence Required** | The declared set of safety/security constraints applicable to the request. |
| **Evidence Strength/Limitations** | This dimension must never be weakened for optimization purposes — root `CLAUDE.md` rule 6 (security-classified content is never pruned/compressed/deduplicated) and `SEC-005` ("prompt compression must preserve security-relevant constraints") apply directly and are non-negotiable, not subject to a configurable threshold in the same sense as the other nine dimensions. |
| **Threshold/Acceptance Rule** | Binary — a safety-constraint violation is not "below threshold," it is a hard failure regardless of any configured `min_quality_score`. This is the one `QG-NNN` entry where "Threshold = policy/configuration/workload-defined" does **not** apply; the floor is fixed by `SEC-005`, not by local policy. |
| **Deterministic vs. Probabilistic** | Deterministic where the constraint is checkable directly (e.g., a required disclaimer, a blocked action); may need `MODEL_BASED_EVAL` for subtler safety properties, subject to §10's calibration discipline in that case. |
| **Failure/Fallback Action** | `RESTORE_ORIGINAL` and `DISABLE_OPTIMIZATION` for the offending technique — never `RETRY`-with-same-technique, since the technique itself is what caused the violation. |
| **Compression Contract Interaction** | Directly the Coding context type's "security constraints" invariant (§9); cross-reference `SEC-005` explicitly. |
| **Revalidation Triggers** | Policy update tightening a safety constraint (cross-reference EC-related DPE mid-computation policy-version concerns, outside this document's scope to redefine). |
| **Validated By** | No dedicated `SCN-QUAL-NNN` isolates Safety specifically among the quality-gate scenario domain — `NO DEDICATED SCENARIO` within `SCN-QUAL-*`; the safety invariant is instead validated structurally by `SEC-005` and the anti-pattern checklist (`conventions.md` §24), recorded honestly rather than force-fit to a quality scenario that doesn't exist. |
| **Traceability** | ARCH §28, §29 (SEC-005); PS §10; CONV §15.3; root `CLAUDE.md` rule 6. |

### QG-008 — User satisfaction

| Field | Content |
|---|---|
| **Dimension** | User satisfaction |
| **Purpose** | Protects against an optimization technically passing every other dimension while still degrading the end user's actual experience (tone, completeness, responsiveness perception). |
| **Measurement Methodology** | Baseline vs. Optimized comparison via application-level signal (explicit feedback, implicit engagement signal, or `MODEL_BASED_EVAL` proxy) — this document does not invent a specific application-level UX metric, since that is workload/application-owned, not Control-Plane-owned. |
| **Applicable Validator Type(s)** | `MODEL_BASED_EVAL` as a proxy; direct application-supplied signal where available (outside `ValidatorSpec`'s enum — recorded as a genuine boundary, see §16 gap discussion). |
| **Evidence Required** | An application-supplied satisfaction signal or a calibrated proxy. |
| **Evidence Strength/Limitations** | Weakest of the 10 dimensions to measure directly from the Control Plane's own vantage point — it is inherently application/end-user-owned. This document's honest position: the Control Plane can gate on a proxy signal the application provides, but cannot itself observe "the user was satisfied" without that signal being supplied. |
| **Threshold/Acceptance Rule** | `Threshold = policy/configuration/workload-defined`, entirely application-supplied. |
| **Deterministic vs. Probabilistic** | Probabilistic in essentially all cases. |
| **Failure/Fallback Action** | `RESTORE_ORIGINAL`, `DISABLE_OPTIMIZATION` — escalation to a stronger model is a plausible but not source-mandated response. |
| **Compression Contract Interaction** | Indirect — a compression-contract violation on any other dimension is a plausible proximate cause of a user-satisfaction regression, but this dimension does not have its own separate contract. |
| **Revalidation Triggers** | Application-level UX-metric definition change. |
| **Validated By** | No dedicated `SCN-QUAL-NNN` — `NO DEDICATED SCENARIO`. |
| **Traceability** | ARCH §28; PS §9. |

### QG-009 — Latency

| Field | Content |
|---|---|
| **Dimension** | Latency |
| **Purpose** | Protects against an optimization technique's own overhead (compression compute, an extra validation pass, a cascade's second-tier call) making the end-to-end request slower than the baseline, net of any inference-time latency the optimization saved. |
| **Measurement Methodology** | Baseline vs. Optimized wall-clock comparison, decomposed per §17's Net Optimization Value accounting (optimization compute overhead vs. inference-time latency saved). |
| **Applicable Validator Type(s)** | N/A in the `ValidatorSpec` sense — latency is measured directly (`ValidationResult.latency_ms` already exists in the schema, INTF §17), not verified via a pass/fail validator. |
| **Evidence Required** | Per-stage timing instrumentation. |
| **Evidence Strength/Limitations** | Direct measurement, strong; the risk is attribution, not measurement — a latency regression must be attributed to the correct stage (H18 measurement separation, §22), not blamed on "optimization" generically. |
| **Threshold/Acceptance Rule** | `Threshold = policy/configuration/workload-defined`; distinct SLA-style latency budgets are plausible but not source-mandated as a specific number. |
| **Deterministic vs. Probabilistic** | Deterministic (a direct measurement, not a judgment). |
| **Failure/Fallback Action** | `DISABLE_OPTIMIZATION` for the specific technique whose overhead exceeded its benefit (cross-reference `EC-065`: total pipeline overhead exceeding inference savings). |
| **Compression Contract Interaction** | None directly — latency is a cost dimension, not a content-preservation dimension. |
| **Revalidation Triggers** | Infrastructure/provider latency-characteristic change. |
| **Validated By** | `EC-065` (DIRECTLY COVERED by `SCN-CMP-060`/`SCN-COST-004`, not a `SCN-QUAL-*` scenario — recorded accurately as a cost-domain scenario, not a quality-domain one, since that is what the source itself uses). |
| **Traceability** | ARCH §28; ARCH §27 (ledger fields); EC-065; EC-061. |

### QG-010 — Cost

| Field | Content |
|---|---|
| **Dimension** | Cost |
| **Purpose** | Protects against an optimization being reported as a saving when its own overhead (compute, retries, escalation) exceeds the inference cost it avoided — the exact discipline OBJ-026/`AC-002` require. |
| **Measurement Methodology** | Net Optimization Value accounting (§17) — token/inference cost avoided minus optimization compute cost, additional provider cost, additional tool cost, additional latency cost, and quality-degradation cost. |
| **Applicable Validator Type(s)** | N/A in the `ValidatorSpec` sense — cost is computed from the ledger (ARCH §27), not verified via a pass/fail validator. |
| **Evidence Required** | Complete per-request token/cost ledger entries; where a term is unmeasurable, `verification_status = UNVERIFIED` and the result is excluded from reported savings (ARCH §47.4.2's own invariant, cited not redefined). |
| **Evidence Strength/Limitations** | As strong as the ledger's own instrumentation; an `UNVERIFIED` term must never be assumed favorable. |
| **Threshold/Acceptance Rule** | Binary net-positive/net-negative, not a continuous quality score — `net_benefit > 0` is the gate. |
| **Deterministic vs. Probabilistic** | Deterministic (arithmetic over measured/estimated ledger terms), though individual terms (e.g., "quality degradation cost") may themselves derive from a probabilistic dimension above. |
| **Failure/Fallback Action** | Do not report as a saving; `DISABLE_OPTIMIZATION` for a technique that persistently fails to net-positive (cross-reference `EC-071`: an optimization previously producing savings stops producing net savings — the regression-detection case). |
| **Compression Contract Interaction** | None directly. |
| **Revalidation Triggers** | Provider pricing change, technique-overhead change, regression signal. |
| **Validated By** | `EC-061`/`EC-071` (cost-domain `SCN-COST-*`/`SCN-CMP-*` scenarios, not `SCN-QUAL-*` — recorded accurately). `SCN-QUAL-003`'s title ("token savings achieved but task quality fails — not counted as a win") is the point where Cost and Task Completion (`QG-002`) intersect most directly. |
| **Traceability** | OBJ-026; AC-002 (root `CLAUDE.md` rule 5); ARCH §27, §47.4.2; EC-061, EC-071. |

---

## 6. Quality Decision Model and Control-Plane Algorithms

### 6.1 Decision flow

```text
1.  Receive quality requirements (which QG-NNN dimensions apply to this request/technique)
2.  Resolve applicable quality policy (OptimizationPolicy.min_quality_score and any per-dimension overrides)
3.  Identify mandatory quality constraints (Safety/QG-007 is never optional; SEC-005 always applies)
4.  Determine available quality evidence (which validators/verifiers can actually run for this content type)
5.  Establish/validate the appropriate baseline (unoptimized representation to compare against)
6.  Evaluate proposed optimization impact (run ValidatorSpec[] via QualityValidator.validate())
7.  Determine evidence sufficiency and confidence (deterministic vs. probabilistic, calibration status per §10)
8.  Compare against applicable quality requirements (per-dimension threshold check)
9.  Determine the outcome (§6.2)
10. Record decision and rationale (AuditRecord, per §19)
11. Revalidate when relevant state/evidence changes (§13, §14)
```

This flow does not redefine `QualityValidator.validate()`'s own signature (INTF §17) — it is the Control-Plane-level sequencing around that call.

**This flow executes once per optimization application (§3.1), not once per request pipeline.** A pipeline applying three optimizations runs this sequence three times — once per `Oᵢ` — never once at the end over the aggregate result:

```text
Resolve quality requirements
        ↓
Capture optimization baseline (Sᵢ)
        ↓
Apply ONE optimization (Oᵢ)
        ↓
Capture optimized state (Sᵢ')
        ↓
Run applicable Quality Gates (Algorithm B/D)
        ↓
Quality Decision (Algorithm E)
        ↓
 ┌──────────────┴─────────────────────┐
 │ ACCEPT                              │ REJECT/FAIL
 ↓                                     ↓
Admit Sᵢ' downstream                   Rollback to Sᵢ (Algorithm G)
 ↓                                     ↓
Proceed to next optimization/stage     Record failure; do not admit Sᵢ'
```

A rejected `Sᵢ'` is never passed to the next optimization stage under any circumstance — the "proceed" branch is reachable only through ACCEPT.

### 6.2 Outcomes

`PROPOSED METHODOLOGY` (no single source enumerates this exact outcome set, though every individual outcome is source-grounded):

| Outcome | Grounding |
|---|---|
| `ALLOW` | Implicit in ARCH §28's "if not violated, proceed" |
| `ALLOW_WITH_MONITORING` | Implicit in ARCH §35 regression-detection requirement — an allowed optimization is still watched |
| `REQUIRE_REVALIDATION` | Implicit in the state-dependency of quality evidence (§13) |
| `ESCALATE` | `FallbackAction.ESCALATE_MODEL` (INTF §17); `escalation_required` (INTF §43.9) |
| `DO_NOT_OPTIMIZE` | `EC-154`/`SCN-NOPT-001` (verified — both exist; see §15) |

A quality failure, an evaluation-infrastructure failure, a security failure, and an optimization failure are never collapsed into one generic status (§16).

### 6.3 Control-Plane Algorithms A–J

All algorithms below are **Control-Plane decision logic only** — none implements an evaluator's internals, an ML model, GPU infrastructure, or a benchmark engine.

**Algorithm A — Resolve Quality Requirements**
```text
Input: request/technique context, OptimizationPolicy
Flow: identify which of QG-001..010 apply to this content type and technique;
      identify which are mandatory (QG-007/Safety always is) vs. workload-configured.
Output: applicable QG-NNN set, mandatory subset
```

**Algorithm B — Determine Applicable Quality Gates**
```text
Input: applicable QG-NNN set, content type (Coding/RAG/Agent/generic)
Flow: map each QG-NNN to its ValidatorSpec/verifier_type per §5's table;
      resolve any compression-contract-specific test (§9) for this content type.
Output: ValidatorSpec[] to run
```

**Algorithm C — Establish/Validate Quality Evidence**
```text
Input: ValidatorSpec[], baseline content, optimized content
Flow: confirm baseline representation is actually available (not itself stale/missing);
      confirm evidence provenance, freshness, and evaluator-version compatibility (§8).
Output: evidence bundle or SOURCE-GAP-style "evidence insufficient" signal
```

**Algorithm D — Evaluate Proposed Optimization Against Quality**
```text
Input: ONE optimization application Oi, its evidence bundle (Sᵢ baseline, Sᵢ' optimized)
Flow: call QualityValidator.validate() semantics (INTF §17) — never reimplemented here;
      for probabilistic verifiers, apply calibration status (§10) before trusting the result.
      Operates on exactly one optimization application at a time (§3.1) — never on an
      aggregate/final result standing in for several unvalidated intermediate applications.
Output: Per-Optimization ValidationResult (pass, score, confidence, violations), scoped to Oi
```

**Algorithm E — Quality Decision**
```text
Input: Per-Optimization ValidationResult for Oi, applicable thresholds
Flow: compare score/pass against threshold per QG-NNN, for this specific Oi;
      Safety (QG-007) violation short-circuits to hard failure regardless of aggregate score.
      The decision belongs to Oi alone and determines whether Oi's result Sᵢ' may be
      admitted to the next stage. On failure: Rollback/Restore Original (Algorithm G) MUST
      complete — and Sᵢ' MUST be excluded from what any subsequent optimization consumes —
      before control passes to the next optimization stage. "Continue with the optimized
      state now, validate later" is not a permitted path: a quality failure is resolved at
      its own checkpoint, not deferred to a later one.
Output: outcome per §6.2, scoped to Oi; Sᵢ' admitted downstream only on ALLOW/ALLOW_WITH_MONITORING
```

**Algorithm F — Quality Revalidation**
```text
Input: prior quality decision, current state
Flow: on model/provider/policy/context/workflow change (§13), treat prior decision as
      provisional; re-run Algorithm C-E rather than reusing the stale result.
Output: revalidated decision or REQUIRE_REVALIDATION outcome
```

**Algorithm G — Quality Failure/Escalation**
```text
Input: failed ValidationResult
Flow: select FallbackAction (RESTORE_ORIGINAL / INCREASE_CONTEXT / INCREASE_REASONING /
      ESCALATE_MODEL / DISABLE_OPTIMIZATION / RETRY) per §5's per-QG-NNN guidance;
      never silently convert a failure to PASS.
Output: fallback action taken, failure recorded
```

**Algorithm H — Quality-Aware `DO_NOT_OPTIMIZE`**
```text
Input: evidence sufficiency, risk assessment
Flow: if evidence is insufficient, risk is unacceptable, or overhead (§17) exceeds
      expected benefit, select DO_NOT_OPTIMIZE rather than forcing a decision.
Output: DO_NOT_OPTIMIZE (a legitimate outcome, not an error — EC-154/SCN-NOPT-001)
```

**Algorithm I — Quality Regression Detection**
```text
Input: historical quality-decision record for a technique
Flow: extend EL-005 (Optimization Regression Detector, cited not redefined) to detect
      when a technique's pass rate degrades, or a probabilistic verifier's acceptance
      rate drifts (§10.D) without a corresponding technique change.
Output: regression/drift signal
```

**Algorithm J — Quality Decision Audit Record**
```text
Input: full decision trail (Algorithms A-I outputs)
Flow: record gate evaluated, requirement, evidence, evidence provenance, decision,
      rationale, optimization affected, model/provider, relevant state/evidence
      versions, revalidation requirement, escalation outcome (§19).
Output: AuditRecord entry (immutable, per conventions.md's audit-record rule)
```

### 6.4 Optimization Chain / Composition Validation

Every optimization has an individual quality checkpoint (§3.2). A chain of optimizations composes checkpoints; it never defers them:

```text
Baseline
   │
   ├── Optimization O1
   │       ↓
   │   Quality Q1
   │       ↓ PASS
   │
   ├── Optimization O2
   │       ↓
   │   Quality Q2
   │       ↓ PASS
   │
   ├── Optimization O3
   │       ↓
   │   Quality Q3
   │       ↓ PASS
   │
   └── Execution
```

This composition is governed by the following rules, each extending an existing mechanism rather than introducing a new one:

1. **Every optimization has an individual quality checkpoint** (§3.1/§3.2) — there is no optimization category (§13) exempt from this.
2. **A later checkpoint cannot retroactively authorize an earlier failed/unvalidated optimization.** `Q3` passing says nothing about whether `O1`'s result was ever actually checked; each `Qᵢ` stands on its own.
3. **A failed optimization must not feed its transformed state into subsequent optimizations.** If `Q1` fails, `O2` never receives `S1'` as its baseline — Algorithm G's rollback (to `S1`, or the last-known-good state) completes first.
4. **If `O2` is rejected, the system continues from the appropriate valid state per the existing recovery/rollback architecture** (`ReversibilityRecord`, `conventions.md` §16; Algorithm G, §6.3) — this document does not invent new recovery semantics, only requires that the existing ones are invoked per-optimization rather than only at the end of a chain.
5. **A later optimization must not conceal degradation introduced by an earlier optimization.** Because each `Oᵢ` is checked against its own immediately-prior baseline `Sᵢ`, a defect `O1` introduces is caught at `Q1`, before `O2` (and any of `O2`'s own quality signals) has a chance to average it away or mask it.
6. **Quality evidence from `O1` must not automatically be reused for `O2`** unless that evidence remains valid under this document's existing evidence-reuse rules (§8, §14) — a fresh optimization application gets its own evidence unless reuse is explicitly justified, not by default.

**Sequential composition:**

```text
O1 → Q1 → O2 → Q2
```

**Nested composition** (an optimization category that internally invokes another):

```text
Agent optimization
    └── Context compression
           └── Retrieval optimization
```

Each actual optimization application in the nesting — the agent-level decision, the compression, and the retrieval step — is still subject to its own checkpoint; nesting does not collapse three applications into one validation.

**Multi-agent composition:**

```text
Agent A optimization
      ↓ Q
Sub-agent delegation
      ↓ Q
Compressed handoff
      ↓ Q
Agent B optimization
      ↓ Q
```

This is the same checkpoint discipline already required for the compressed-handoff boundary in §13/§9.3 (the Agent context type's invariants must survive the handoff) — no separate multi-agent validation architecture is invented here.

**Optimization rejection.** If `Qᵢ` fails:

```text
Qᵢ fails
    ↓
Sᵢ' is not admitted
    ↓
restore/revert to the appropriate valid state (Algorithm G; ReversibilityRecord)
    ↓
record failure (Algorithm J)
    ↓
determine fallback/recovery (§6.2 outcomes)
```

An optimization is never permitted to remain active merely because a later, aggregate quality evaluation might pass — that is precisely the "batch-after-all" anti-pattern §23 now names explicitly.

**Rollback and recovery.** `Resume != Replay` (§15) applies identically at the per-optimization level: if an already-accepted `Oᵢ` later becomes invalid due to a state change, the existing revalidation/recovery architecture (Algorithm F; §14; §15) governs — this section does not invent new recovery semantics, only confirms they apply per-`Oᵢ`, not only at the chain's end.

**Validated by — honestly stated.** `SCN-QUAL-001`/`002`/`003` each demonstrate a single optimization application receiving its own quality checkpoint (rollback-on-threshold-failure, semantic-drift-on-one-technique, and outcome-based quality respectively) — read together, they are consistent with, but do not directly test, the *composition*-level claim that a multi-optimization chain enforces a checkpoint at every stage rather than only at the end. No scenario in `scenario-matrix.md` constructs a multi-step optimization chain and asserts that an intermediate stage's failure is caught before a later stage runs. This composition-level invariant is therefore recorded as **`NO DEDICATED SCENARIO`** — genuinely uncovered, not force-fit to `SCN-QUAL-001`–`005`, which each validate a single-optimization case.

### 6.5 Per-Optimization Quality Validation Checklist

```text
[ ] Every optimization application has an associated quality checkpoint.
[ ] Validation occurs after the optimization transformation.
[ ] Validation occurs before the transformed result is admitted downstream.
[ ] Applicable quality gates are resolved per optimization.
[ ] A failed optimization result is not propagated.
[ ] Rollback/fallback occurs before downstream admission.
[ ] Sequential optimization chains validate each stage independently.
[ ] Nested/multi-agent optimization boundaries are covered.
[ ] Later validation cannot retroactively authorize earlier unvalidated optimization.
[ ] Quality evidence is not reused across optimizations unless still valid.
[ ] State changes trigger existing revalidation behavior.
[ ] No fabricated scenario/edge-case IDs are introduced.
```

Every item above is satisfied by §3.1, §3.2, §6.1, §6.3 (Algorithms D/E/F/G), and §6.4 as edited in this pass; the last item is satisfied by §6.4's honest `NO DEDICATED SCENARIO` labeling rather than a fabricated composition-level scenario.

---

## 7. Quality Gate Types

`PROPOSED METHODOLOGY` (a useful taxonomy for organizing the QG-NNN gates operationally; not itself a named taxonomy in any single source, though every category is grounded in an existing mechanism):

| Gate type | When it runs | Grounding |
|---|---|---|
| Admission-time gate | Before a technique is even attempted (e.g., Safety/`QG-007` constraint check) | SEC-005, root `CLAUDE.md` rule 6 |
| Optimization-time gate | During the pipeline stage itself | ARCH §28's Baseline-vs-Optimized model |
| Post-optimization validation gate | Immediately after the transformation, before the result is used | `QualityValidator.validate()` (INTF §17) |
| Runtime/result gate | After the LLM call, on the final response | `conventions.md` §15's "quality gate after response generation" language |
| Escalation gate | On verifier-confidence-below-threshold | AR-004, VCL (§10) |
| Recovery/revalidation gate | On resume/recovery from a checkpoint | §14 below |
| Benchmark/rollout gate | Before a technique is promoted to a higher maturity level | ARCH §42 |

---

## 8. Quality Evidence and Baselines

**Evidence types** (source-supported terminology, not an invented taxonomy): prior validated evaluations, workload benchmarks, regression suites, reference answers, deterministic validators, human evaluation, model-based evaluation, application success signals, tool-call correctness, structured-output validation, provider/model evidence, historical execution evidence.

For every evidence type, this document requires consideration of: provenance, freshness, workload applicability, evaluator version, model/provider version, state/version compatibility, limitations, and uncertainty — `PROPOSED METHODOLOGY` at the level of naming these dimensions explicitly (no single source enumerates exactly this list), though each individual concern is grounded (freshness → CL-002 by architectural cross-reference; evaluator version → VCL's `CalibrationMetadata.calibrated_at`).

**Baseline principle:** a source-reported percentage improvement (e.g., an LLMLingua/RouteLLM/FrugalGPT benchmark) is never a guarantee of EAIOC quality preservation on this organization's own workload — root `CLAUDE.md` rule 7, restated here in the quality-evidence context specifically. Baseline comparison must account for the actual workload, task, model, provider, context, workflow, optimization, and evaluation criteria in use — generalization beyond available evidence is not claimed.

This document does not invent sample sizes, confidence intervals, statistical-significance thresholds, or reliability percentages; that is `eval.md`'s scope.

---

## 9. Compression-Contract Test Suites (QO-001)

`architecture.md` §20.1 / `conventions.md` §15.3 define exactly these three context types and their invariants — restated verbatim, not redefined:

| Context type | Invariants that must survive |
|---|---|
| Coding | Function signatures, imports, types, API contracts, security constraints, relevant test expectations, error locations |
| RAG | Entities, numbers, dates, citations, source attribution |
| Agent | Objective, constraints, decisions, completed actions, pending actions, unresolved failures |

Below is this document's concrete test-suite outline for each — `PROPOSED METHODOLOGY` at the level of the specific `ValidatorSpec` combination, grounded in each invariant individually.

### 9.1 Coding

| Invariant | Suggested `ValidatorSpec` |
|---|---|
| Function signatures, types | `TYPE_CHECK`, `STATIC_ANALYSIS` |
| Imports | `STATIC_ANALYSIS` |
| API contracts | `SCHEMA`, `INTEGRATION_TEST` |
| Security constraints | `SECURITY_CHECK` — cross-reference `SEC-005` explicitly; this is where root `CLAUDE.md` rule 6 becomes testable, not merely stated |
| Relevant test expectations | `UNIT_TEST`, `INTEGRATION_TEST` |
| Error locations | `STATIC_ANALYSIS` (location metadata preserved, not just error presence) |

Repository-consistency checks (e.g., a symbol rename invalidating multiple admitted context items — cross-reference `SCN-CMP-025` by architectural analogy, not re-verified line-by-line here since it is outside this document's own scope) are `PROPOSED METHODOLOGY` beyond what QO-001 states directly.

### 9.2 RAG

| Invariant | Suggested `ValidatorSpec` |
|---|---|
| Entities, numbers, dates | Entity/numeric-preservation check (`QG-004`'s own methodology) |
| Citations, source attribution | `CITATION_CHECK` |
| Grounding | `EMBEDDING_SIMILARITY` **combined with** the above — never alone, per `EC-063` |

`EC-063`'s key lesson is preserved verbatim: **embedding similarity alone is insufficient for factual correctness where entity/numeric preservation matters.**

### 9.3 Agent

| Invariant | Suggested `ValidatorSpec` |
|---|---|
| Objective, constraints | `BUSINESS_RULE` (objective/constraint text unchanged) |
| Decisions, completed/pending actions | `TASK_SUCCESS_CHECK`, `BUSINESS_RULE` |
| Unresolved failures | `BUSINESS_RULE` (must not be silently dropped) |

Semantic similarity is never treated as equivalent to preservation of agent execution truth — a paraphrased objective that changes scope must fail this check even at high embedding similarity, consistent with `QG-004`/`QG-006`'s general lesson.

---

## 10. Verifier Calibration Procedure (VCL)

This document owns the **procedure**; `interfaces.md` §43.9 (`VerifierCalibrationLayer`) owns the **interface** — not redefined here.

**A. Benchmark-set construction.** For each of the 9 `VerifierDescriptor.verifier_type` values (INTF §43.9): `UNIT_TEST`, `SCHEMA_VALIDATION`, `TYPE_CHECK`, `STATIC_ANALYSIS`, `BUSINESS_RULE`, `CITATION_CHECK`, `FORMAT_VALIDATION` are **deterministic** — confidence is definitionally `1.0`, no calibration benchmark set is needed. `LLM_JUDGE_SEMANTIC_EQUIVALENCE` and `TASK_SUCCESS_CHECK` are **probabilistic** where the "success" judgment itself is model-based — these require a labeled benchmark set (`CalibrationMetadata.benchmark_set_ref`) of known-correct/known-incorrect examples before `calibrated = true`.

**B. Calibration.** Run the probabilistic verifier against the labeled benchmark set; record `observed_accuracy` (`CalibrationMetadata`). `PROPOSED METHODOLOGY`: calibration is considered complete when observed accuracy on the benchmark set has been measured and the resulting confidence-to-threshold mapping is recorded — no source specifies a minimum observed-accuracy bar for "calibrated" status, which is `SOURCE-GAP-QUALGATE-01` (§20).

**C. Calibration status.** A probabilistic verifier is treated as calibrated only when `calibrated = true` in its `CalibrationMetadata` — until then, per OBJ-028, its confidence must be treated as `< 1.0` and its `passed = true` output is never unconditional ground truth (`conventions.md` §7.10).

**D. Drift detection.** `report_drift(verifier_id, observed_delta)` (INTF §43.9) surfaces a `DriftEvent` when a verifier's acceptance rate changes without a corresponding technique change. This explicitly extends `EL-005` (Optimization Regression Detector) to verifier drift specifically — `architecture.md` §47.4.3's own stated extension, cited not redefined. Validating scenario: `SCN-QUAL-005` (verified at line 5673).

**E. Recalibration triggers** (`PROPOSED METHODOLOGY` — no source specifies an exact cadence, which is `SOURCE-GAP-QUALGATE-02`, §20): evaluator/model change, provider change, a `DriftEvent`, workload drift, or a fixed periodic schedule (the fixed-schedule option is proposed here only as one option among several, not asserted as the correct one).

**F. Threshold interaction.** `acceptance_threshold` (INTF §43.9 `VerificationResult`) is configurable per task/intent/tenant (`architecture.md` §38), consistent with every other `QG-NNN` threshold in this document.

**G. Failure.** When calibration evidence is unavailable, stale, invalid, or insufficient, the verifier is treated as uncalibrated (confidence `< 1.0` by default, per OBJ-028) — never defaulted to `calibrated = true` in the absence of evidence.

---

## 11. Zero-Threshold / Quality-Gate Bypass Anti-Pattern (EC-069)

`EC-069` (verified at line 2656) documents exactly this failure: an operator sets `min_quality_score = 0.0` "to speed up testing," which causes every quality gate to pass regardless of actual output quality — a zero threshold is equivalent to having no quality gate, which categorically violates the §3 invariant.

`scenario-matrix.md`'s own reconciliation marks `EC-069`↔`SCN-QUAL-001` as `PARTIALLY COVERED`, with the explicit note that `SCN-QUAL-001` assumes a functioning, non-zero threshold is being enforced and does not itself address the threshold being misconfigured to zero — a configuration-validation concern, not a runtime-behavior concern. This document preserves that classification honestly rather than upgrading it.

**Rule (`PROPOSED METHODOLOGY` — this document is the first in the chain to state it as a named rule, though it directly operationalizes ARCH §28's invariant):** a configured `min_quality_score` (or any per-`QG-NNN` threshold) of `0.0`, or any other value that would cause the gate to always pass regardless of input, must itself be rejected at **configuration-validation time** — before the policy is ever applied to a live request — not merely detected after the fact by observing that no rollback ever occurred. This is a configuration-time validation rule, not a new runtime detection mechanism; no specific numeric non-zero floor is asserted (that remains workload-defined), only that zero (or an equivalent always-pass value) is categorically rejected.

---

## 12. Quality Precedence

The established precedence, preserved without a competing model:

```text
Security / Authorization / Policy
        >
Quality Requirements
        >
Correctness / Integrity Constraints
        >
Optimization Benefit
        >
Cost / Latency Preference
```

An optimization must never be selected merely because it saves tokens, money, latency, or compute when it violates a mandatory quality requirement — and `QG-007` (Safety) itself sits above the other nine dimensions within "Quality Requirements," per §5's own note that its floor is fixed by `SEC-005`, not by local policy.

---

## 13. Quality + Optimization Interactions

| Optimization category | Quality implication | Per-Optimization Validation Required (§3.1) |
|---|---|---|
| Token optimization (sanitization, query compression) | `QG-001`/`QG-004`/`QG-006` apply directly | YES |
| Context optimization (pruning, dedup, compression, reordering, progressive compaction, summarization, retrieval) | All three `QO-001` context types (§9) apply; mandatory-context protection (Safety-classified content) is absolute | YES |
| Cache optimization (where a transformation occurs, e.g. cache-aware assembly) | Cited from `cache-strategy.md`, not redefined — a cache hit must still satisfy the freshness/authorization conditions `cache-strategy.md` already states; this document does not add a new cache-specific quality rule | YES |
| Inference/serving optimization (P5) | Quality gates may condition or reject a P5 capability's use when quality requirements are not satisfied, but this document does not redesign any `INFOPT-NNN` capability or its H17/H18 boundary | YES |
| Agent optimization (early exit, sub-agent delegation, compressed handoffs, reasoning/output budget, verifier-guided escalation) | `QG-002` (Task completion) is the primary gate; `AR-004`'s escalation-decision logic is cited, not redefined — this document owns the calibration (§10) that informs, not replaces, that decision | YES |
| Model/provider routing | Quality evidence validity must consider model/provider capability and change, per `provider-matrix.md` (cited, not restated); no provider capability is assumed without authoritative evidence | YES |
| Tool/MCP-enabled systems | `QG-002`/`QG-005` apply to tool-call correctness and structured tool output | YES |
| Multi-agent/sub-agent systems | Compressed handoffs (`SubAgentContextHandoff`) must preserve the Agent-context-type invariants (§9.3) across the handoff boundary | YES |
| Coding/developer agents | §9.1's test-suite outline applies directly; this document does not assume every repository has a build/test system, and does not invent concrete commands | YES |
| Agentic RAG | §9.2's test-suite outline applies directly | YES |

**"YES" means every optimization application in that category receives a validation checkpoint (§3.2) — it does not mean every one of the 10 `QG-NNN` dimensions runs at that checkpoint.** The applicable gate(s) depend on optimization type, content type, workload, policy, and available evidence (Algorithm A/B, §6.3); running irrelevant dimensions unconditionally is not required and is discouraged by §17's economics discipline.

Same EAIOC Control-Plane principles apply across all application classes (generic LLM apps, autonomous agents, developer/coding agents, multi-agent systems) — no separate quality architecture is invented per class.

---

## 14. Quality + State/Revalidation

Quality decisions are state-dependent. Relevant state includes: user intent, context version, workflow version, model, provider, policy, authorization, tool availability, memory, repository state, external state, evaluator version, benchmark/evaluation configuration. Conceptually:

```text
Quality Decision + Relevant State Version + Evidence Version
```

When relevant state changes, prior quality evidence may require revalidation (Algorithm F, §6.3) — this document does not invent new schema fields beyond what `interfaces.md` already defines for state versioning (CVM/WVM, cited not redefined).

---

## 15. Quality + Recovery/Supersession

> **Resume != Replay.**

On recovery, prior quality evidence is reassessed for continued validity — a model switch, provider switch, context change, workflow change, policy change, evaluator change, tool change, or external-state change are all plausible invalidation triggers. A quality decision belonging to a superseded execution must not authorize unwanted work in a new execution; supersession must prevent stale quality evidence, optimization decisions, authorization context, and evaluation state from leaking into a new execution (cited from the existing supersession architecture — SPM — not redefined here).

`EC-154`/`SCN-NOPT-001` (both verified: EC-154 at line 5725, SCN-NOPT-001 at line 5283) establish `DO_NOT_OPTIMIZE` as a legitimate, proactive Control-Plane outcome, not an error — this applies equally to a recovery path that determines re-optimizing is not worthwhile.

---

## 16. Quality + Security/Governance and Multi-Tenancy

Quality evidence and decisions must respect tenant isolation, authorization, policy, data governance, and sensitivity classification. Tenant A's quality evidence must never inform a Tenant B decision unless an authoritative policy explicitly permits it (root `CLAUDE.md` rule 4, restated in this document's context). This document does not invent a storage schema for quality evidence beyond what `interfaces.md`/`cache-strategy.md` already define.

**Genuine boundary note (not a gap, a scope observation):** `QG-008` (User Satisfaction)'s reliance on an application-supplied signal, outside `ValidatorSpec`'s own enum, is the one dimension where this document cannot fully specify a Control-Plane-native measurement path — recorded honestly in §5 rather than papered over.

---

## 17. Quality + Economics / Net Optimization Value

Quality participates in the existing Net Optimization Value framework (ARCH §47.4.2, cited not redefined) — no competing economic model is created here. Token reduction alone is never success (root `CLAUDE.md`, restated): if quality degradation invalidates an optimization, it must not be represented as positive net value merely because token/cost savings occurred. Quality-evaluation overhead itself (the compute cost of running `ValidatorSpec[]`) is an `Optimization Compute Cost` term in the same accounting, not a separate framework.

---

## 18. Quality Failure Modes and Recovery Matrix

| Failure | Detection | Quality Impact | Control-Plane Decision | Recovery |
|---|---|---|---|---|
| Evaluator unavailable | Validator call fails/times out | Cannot confirm pass/fail | Do not default to PASS — treat as insufficient evidence | Retry, or `DO_NOT_OPTIMIZE` if unresolved |
| Evaluator timeout | Latency budget exceeded | Same as above | Fail open on the *optimization*, not on the quality question itself | Escalate/retry per policy |
| Insufficient evidence | No baseline or reference available | Cannot compare | `DO_NOT_OPTIMIZE` or escalate | Acquire evidence, retry |
| Stale evidence | Evidence provenance/freshness check fails | Decision may be unsound | Revalidate (Algorithm F) | Re-run evaluation |
| Evaluator version mismatch | `CalibrationMetadata`/version check | Calibration may not transfer | Treat as uncalibrated until re-verified | Recalibrate (§10.E) |
| Benchmark mismatch | Corpus applicability check | Threshold may not generalize | Flag, do not silently apply | Escalate to policy owner |
| Quality requirement unavailable/ambiguous | Requirement-resolution step (Algorithm A) fails | Cannot determine applicable gate | `DO_NOT_OPTIMIZE` rather than guessing | Resolve requirement upstream |
| False-positive pass | Detected via later regression/drift signal | Undetected degradation already occurred | Regression detection (Algorithm I) | Rollback technique, investigate |
| False-negative failure | Detected via manual/audit review | Optimization needlessly blocked | Investigate verifier calibration | Recalibrate |
| Evaluator disagreement | Multiple validators conflict | Ambiguous outcome | Escalate, do not average away disagreement | Human review (§20.7) where policy designates |
| Model/provider change | CAR/provider-profile event | Prior evidence may not transfer | Revalidate | Re-run evaluation against new model/provider |
| Adversarial input | `EC-077`-style detection (cited, not redefined here) | Quality evaluation itself may be targeted | Apply existing scope-limit/cost-halt behavior | Per existing adversarial-input handling |
| Quality regression | Algorithm I | Previously-passing technique now failing | `DISABLE_OPTIMIZATION`, alert | Investigate, re-benchmark |
| Measurement contamination | Cross-check against independent signal | Reported quality may be inflated | Flag, exclude from savings | Re-measure cleanly |
| Unrepresentative benchmark | `EC-072` | Threshold validated against stale traffic pattern | Flag benchmark for refresh | `eval.md`'s scope to remediate corpus |
| Quality-gate bypass (`EC-069`) | Configuration-validation rule (§11) | All gates silently pass | Reject the configuration | Configuration-time rejection, not runtime |
| Hidden degradation | Multi-signal check (`QG-004`) catches what similarity-alone misses | Otherwise undetected | N/A — this is the defense, not a failure mode | N/A |

Security/policy failures follow the higher-order security/policy architecture, not this document's quality-failure behavior. Evaluation-infrastructure failure is never automatically converted to a quality PASS. Optimization failure follows the existing fail-open-on-optimization discipline (root `CLAUDE.md` rule 2) — but this fail-open rule applies to the *optimization path*, not to skipping the quality question itself; a quality gate that cannot run is insufficient evidence, not an automatic pass. The P5 fail-open rule (`inference-optimization.md`) is not automatically applied to quality gates — they are governed by this document's own failure behavior, not P5's.

---

## 19. Quality Gate Composition

`PROPOSED METHODOLOGY` (composition logic is newly proposed at this level of detail; no source defines an executable boolean language, and none is invented here):

- **Mandatory gates**: `QG-007` (Safety) and any gate covering a `SEC-NNN`-protected invariant are always evaluated, never conditionally skipped.
- **Conditional gates**: the remaining `QG-NNN` entries apply based on content type/technique (§9's mapping).
- **Composition**: mandatory gates are AND-composed with each other (all must pass); conditional gates are evaluated per applicable dimension, with `QG-007`'s failure short-circuiting the others (a safety violation is not "averaged out" by high scores elsewhere).
- **Insufficient/conflicting evidence**: escalate rather than resolve by majority vote or silent tie-breaking.
- **Revalidation**: composition is re-evaluated whenever any input evidence is revalidated (§14).

---

## 20. Application-Class Quality Considerations, Coding-Agent/RAG Detail, and Human-in-the-Loop

**Generic LLM applications, autonomous agents, developer/coding agents, multi-agent/sub-agent systems, agentic RAG, tool/MCP-enabled systems** all use the same EAIOC Control-Plane principles from §2 — no separate per-class quality architecture exists.

**Coding-agent quality** (first-class, not optional, per root `CLAUDE.md`): compilation/build validation, tests, static analysis, repository consistency, API compatibility, regression detection, patch correctness, security constraints, unresolved failures, tool-call correctness — per §9.1. This document does not assume every repository has a build/test system, and does not invent concrete commands (consistent with root `CLAUDE.md`'s own "do not invent build/lint/test commands" instruction for this repository itself).

**Agentic-RAG quality**: retrieval correctness, relevance, grounding, factuality, citation integrity, provenance, freshness, context-transformation impact, retrieval-reduction impact — per §9.2. No universal retrieval metric is invented.

**Human-in-the-loop**: where the existing Human Approval Gate (HAG) architecture designates approval for a consequential/irreversible action, quality uncertainty that rises to that designation may require human involvement — this document does not invent a new approval workflow, only notes the interaction point with the existing HAG terminology (cited, not redefined; HAG's own mechanism belongs to `architecture.md` §47.2 / `security.md`'s future elaboration).

---

## 21. Observability and Audit Boundary

Quality decisions must be conceptually observable — recording, at minimum: gate evaluated, requirement, evidence, evidence provenance, decision, rationale, optimization affected, model/provider, relevant state/evidence versions, revalidation requirement, and escalation outcome (Algorithm J, §6.3). This document does not build the final telemetry/dashboard architecture — that is `observability.md`'s scope — but requires that decisions remain explainable: why was an optimization allowed or rejected, which requirement applied, what evidence was used and was it current, what changed, why was escalation triggered, why was `DO_NOT_OPTIMIZE` selected.

---

## 22. Quality Measurement Separation (H18)

Quality measurement remains distinguishable from token savings, context savings, cache savings, inference-serving optimization (P5), agent optimization, and application business metrics — the same H18 discipline `inference-optimization.md` already applies to P5 specifically (cited, not redefined). Quality acts as a **gate/constraint** on those other measurements; it is not itself merged into any of them. A measurement that conflates a quality-gate outcome with a savings figure from a different category is invalid, per the same principle `AC-036` states for context-vs-inference-serving measurement.

---

## 23. Anti-Patterns

| Anti-pattern | Why it's prohibited |
|---|---|
| Threshold bypass / zero-threshold configuration | `EC-069`, §11 |
| Optimizing before resolving quality requirements | Violates Algorithm A's ordering (§6.3) |
| Treating token reduction as quality | Root `CLAUDE.md` rule 5; ARCH §28's central invariant |
| Treating evaluator output as unquestionable truth | OBJ-028; `conventions.md` §7.10 |
| Reusing stale quality evidence to avoid evaluation cost | §14; §17 |
| Cross-tenant evidence leakage | Root `CLAUDE.md` rule 4; §16 |
| Benchmark overgeneralization | §8's baseline principle; `EC-072` |
| Hidden quality degradation (embedding-similarity-alone) | `EC-063`; `QG-004` |
| Evaluation-infrastructure failure silently converted to PASS | §18 |
| Quality gate implemented as if it were itself an optimization technique | Conflates §2's boundary |
| Double-counting quality and optimization metrics | §22 (H18) |
| Treating `DO_NOT_OPTIMIZE` as an error | `EC-154`; §6.2 |
| Replaying stale quality decisions after state changes | §14; §15 |
| Decisions recorded without sufficient audit evidence | Algorithm J; §21 |
| Batch-after-all optimization validation (validate only after several optimizations have already executed) | §3.1, §6.4 — the prohibited `O1 → O2 → O3 → Q(final)` shape |
| Post-hoc quality validation (using final-result validation to authorize earlier unvalidated transformations) | §6.4, rule 2 — a later checkpoint cannot retroactively authorize an earlier one |
| Failed-state propagation (passing a quality-failed optimized state to the next optimization) | §6.3 Algorithm E; §6.4, rule 3 |
| Optimization validation omission (performing an optimization without an applicable quality checkpoint) | §3.1 — no optimization category is exempt |
| Cross-optimization evidence misuse (reusing `O1`'s evidence for `O2` without validating applicability) | §6.4, rule 6; §8 |
| Hidden intermediate degradation (a later optimization masking degradation introduced by an earlier one) | §6.4, rule 5 |

---

## 24. Test/Validation Strategy

Because this document remains Level 0, no implementation test code is provided. Validation categories, mapped to source references where available:

| Category | Reference |
|---|---|
| Contract validation | QO-001 (§9) |
| Scenario validation | `SCN-QUAL-001`–`005` |
| Edge-case validation | `EC-020`/`052`/`061`–`065`/`069`/`071`/`072`/`076`/`077`/`154`/`162`–`165`/`211` |
| Compression-contract validation | §9 |
| Verifier calibration validation | §10 |
| Benchmark validation | ARCH §32 (cited, not redefined — `eval.md`'s deeper scope) |
| Regression validation | ARCH §35; Algorithm I |
| Provider/model variation | `provider-matrix.md` (cited) |
| State-change validation | §14 |
| Recovery validation | §15 |
| Adversarial validation | `EC-077` |

---

## 25. Cross-Document Boundary

| Concern | Owner |
|---|---|
| Quality-dimension taxonomy and abstract rollback architecture | `architecture.md` / `conventions.md` |
| `QualityValidator` interface | `interfaces.md` |
| `VerifierCalibrationLayer` interface | `interfaces.md` |
| Agent-loop escalation decision | `agent-optimization.md` |
| Concrete quality-gate methodology, thresholds | **this document** |
| Concrete verifier calibration procedure | **this document** |
| Compression-contract validation suites | **this document** |
| Content integrity / prompt injection / tool trust mechanism | `security.md` (not yet generated) |
| Benchmark corpus/statistical evaluation strategy | `eval.md` (not yet generated) |
| Detailed telemetry architecture | `observability.md` (not yet generated) |
| Cache architecture | `cache-strategy.md` |
| P5 capability negotiation | `inference-optimization.md` |
| Provider facts/capabilities | `provider-matrix.md` |
| Canonical cross-document requirements traceability | `requirements-traceability.md` (not yet created) |

---

## 26. Coverage Matrix

### 26.1 Matrix 1 — Quality Gate Catalog

| Gate ID | Gate Name | Applicable Optimization Types | Trigger | Required Evidence | Decision | Failure Behavior | Revalidation Trigger |
|---|---|---|---|---|---|---|---|
| QG-001 | Accuracy | Token/context/model routing | Any transformation | Reference/rule/calibrated judgment | ALLOW/ESCALATE/RESTORE | RESTORE_ORIGINAL, ESCALATE_MODEL | Model/provider/technique change |
| QG-002 | Task completion | Agent/early-exit/tool | Agent-loop decision point | Completion criterion check | ALLOW/DO_NOT_OPTIMIZE | INCREASE_CONTEXT/REASONING, ESCALATE_MODEL | Workflow/tool-availability change |
| QG-003 | Retrieval quality | Retrieval/Top-K/reordering | Retrieval-affecting technique | Pre-optimization retrieval set | ALLOW/RESTORE | INCREASE_CONTEXT, RESTORE_ORIGINAL | Corpus/embedding-model change |
| QG-004 | Factuality | Compression/dedup/summarization | Fact-intensive content | Entity/numeric preservation set | ALLOW/RESTORE | RESTORE_ORIGINAL | Source-content/technique change |
| QG-005 | Schema compliance | Output-schema/length control | Structured output | Declared schema | ALLOW/RETRY | RETRY, DISABLE_OPTIMIZATION | Schema-version change |
| QG-006 | Semantic equivalence | Compression/cascade | Meaning-preserving claim | Calibrated LLM-judge evidence | ALLOW/ESCALATE | ESCALATE_MODEL, DISABLE_OPTIMIZATION | Verifier drift |
| QG-007 | Safety | All | Always mandatory | Declared safety constraints | ALLOW/HARD-FAIL | RESTORE_ORIGINAL, DISABLE_OPTIMIZATION | Policy tightening |
| QG-008 | User satisfaction | All (application-supplied) | Application signal available | Application/proxy signal | ALLOW/RESTORE | RESTORE_ORIGINAL | UX-metric definition change |
| QG-009 | Latency | All | Overhead measurement point | Per-stage timing | ALLOW/DISABLE | DISABLE_OPTIMIZATION | Infra/provider latency change |
| QG-010 | Cost | All | Net-value computation point | Ledger entries | ALLOW/DISABLE | DISABLE_OPTIMIZATION | Pricing/overhead/regression change |

### 26.2 Matrix 2 — Quality Dimension Matrix

| Dimension | Applicable Workload | Evidence Type | Affected Optimizations | Failure Consequence | Fallback/Escalation |
|---|---|---|---|---|---|
| Accuracy | All | Reference/rule/judgment | Token, context, routing | Wrong output reported as success | RESTORE_ORIGINAL, ESCALATE_MODEL |
| Task completion | Agentic | Completion-criterion check | Early exit, sub-agent delegation | Premature termination | INCREASE_CONTEXT/REASONING |
| Retrieval quality | RAG | Recall/precision proxy | Top-K, pruning, reordering | Missing relevant content | INCREASE_CONTEXT |
| Factuality | RAG/fact-intensive | Entity/numeric preservation | Compression, dedup | Plausible-but-wrong answer | RESTORE_ORIGINAL |
| Schema compliance | Structured output | Schema validation | Output schema/length control | Malformed output | RETRY |
| Semantic equivalence | Compression/cascade | Calibrated judge | Compression, cascade | Meaning drift | ESCALATE_MODEL |
| Safety | All | Declared constraints | All | Constraint violation | DISABLE_OPTIMIZATION (hard) |
| User satisfaction | Application-facing | Application signal | All | UX regression | RESTORE_ORIGINAL |
| Latency | All | Timing instrumentation | Overhead-bearing techniques | Net slower request | DISABLE_OPTIMIZATION |
| Cost | All | Ledger accounting | All | False savings claim | Exclude from reporting |

### 26.3 Matrix 3 — Optimization × Quality Matrix

See §13's table — reused here by reference rather than duplicated verbatim, per this document's own anti-duplication discipline.

### 26.4 Matrix 4 — Quality Failure/Recovery Matrix

See §18's table.

### 26.5 Matrix 5 — Quality Evidence Matrix

| Evidence Type | Strength/Applicability | Freshness | Limitations | Reuse Conditions | Invalidation Conditions |
|---|---|---|---|---|---|
| Deterministic validator result | Strong | High (re-run cheaply) | Only covers what the rule checks | Reusable if content unchanged | Content/rule change |
| Prior validated evaluation | Moderate–Strong | Decays with content drift | Point-in-time | Reusable within freshness window | State/content change (§14) |
| Model-based/LLM-judge evaluation | Weak unless calibrated | Decays with verifier-model change | Calibration-dependent | Reusable only if `calibrated = true` | Verifier-model change, drift (§10.D) |
| Workload benchmark | Moderate | Decays as traffic patterns shift | `EC-072` unrepresentative-corpus risk | Reusable until corpus refresh | Corpus staleness |
| Application-supplied signal (satisfaction) | Application-defined | Application-defined | Outside Control-Plane's own measurement | Per application policy | Application-defined |
| Historical execution evidence | Moderate | Decays over time | Survivorship bias risk | Reusable with recency weighting | Significant technique/model change |

### 26.6 Matrix 6 — Quality Traceability Matrix

| Requirement/Source | QG-NNN | Scenario | Edge Case | Expected Behavior | Coverage |
|---|---|---|---|---|---|
| PS §9 / ARCH §28 | QG-001–010 | SCN-QUAL-001 | EC-020 | Rollback on threshold failure | DIRECTLY COVERED |
| ARCH §20.1 (QO-001) | QG-004 (RAG row) | SCN-QUAL-002 | EC-052 | Semantic drift detected | DIRECTLY COVERED (QG-004 role); THIN/ANALOGOUS for QG-003 specifically |
| ARCH §28 | QG-002, QG-010 | SCN-QUAL-003 | EC-063 | Token savings without task quality not counted as a win | DIRECTLY COVERED |
| PS §52.6 / OBJ-028 | QG-006 | SCN-QUAL-004 | EC-164 | Probabilistic verifier below threshold triggers fallback | DIRECTLY COVERED |
| PS §52.6 / OBJ-028 | QG-006 | SCN-QUAL-005 | EC-163 | Verifier drift detected | DIRECTLY COVERED |
| ARCH §28 (this document, §11) | QG-001–010 (all thresholds) | SCN-QUAL-001 | EC-069 | Zero-threshold configuration rejected | PARTIALLY COVERED (preserved from `scenario-matrix.md`, not upgraded) |
| OBJ-028 | QG-006 | SCN-QUAL-004 | EC-162 | Verifier calibration check must actually run | PARTIALLY COVERED (preserved, not upgraded) |
| OBJ-028 | QG-006 | SCN-QUAL-004, SCN-QUAL-005 | EC-165 | Deterministic/probabilistic confidence not conflated | PARTIALLY COVERED (preserved, not upgraded) |
| OBJ-028 | QG-006 | SCN-QUAL-004, SCN-CMP-088 | EC-211 | Verifier uncertainty + model-downgrade | PARTIALLY COVERED (preserved, not upgraded; SCN-CMP-088 is outside this document's own scope to re-verify) |
| ARCH §28 | QG-005 | NO DEDICATED SCENARIO | — | Schema validation | NOT AVAILABLE — no dedicated SCN-QUAL-* scenario |
| ARCH §28 | QG-007 | NO DEDICATED SCENARIO | — | Safety constraint preserved | NOT AVAILABLE within SCN-QUAL-*; structurally validated via SEC-005/conventions §24 instead |
| ARCH §28 | QG-008 | NO DEDICATED SCENARIO | — | User-satisfaction proxy | NOT AVAILABLE |
| ARCH §27, §65 | QG-009 | (SCN-CMP-060, SCN-COST-004 — cost domain, not SCN-QUAL-*) | EC-065 | Overhead exceeds savings | DIRECTLY COVERED (cost-domain scenario, accurately labeled as such) |
| OBJ-026, AC-002 | QG-010 | (SCN-COST-002/003/006, SCN-CMP-056 — cost domain) | EC-061, EC-062, EC-071, EC-077 | Cost accounting/regression discipline | DIRECTLY COVERED (cost-domain scenarios, accurately labeled) |

### 26.7 Cross-Document Coverage Matrix (chain convention)

| Source Document | Entries Citing It | Coverage |
|---|---|---|
| Problem Statement (PS) | 10/10 QG entries cite PS §9; §52.6 cited for QG-006 | Comprehensive |
| Engineering Specification (ES) | Cited by cross-reference (§42.6) for H06; no independent ES-specific content found beyond what ARCH/PS already state | Adequate — genuinely thin, recorded honestly rather than force-fit |
| Architecture (ARCH) | 10/10 | Comprehensive |
| Interfaces (INTF) | 10/10 cite INTF §17 and/or §43.9 | Comprehensive |
| Conventions (CONV) | 10/10 cite CONV §15 and/or §7.10 | Comprehensive |
| Edge Cases (EDGE) | 17 distinct EC-NNN IDs cited, all verified present | Comprehensive for the candidate list given |
| Scenario Matrix (SCN) | All 5 `SCN-QUAL-NNN` read and cited; 4 `PARTIALLY COVERED`/`NO DEDICATED SCENARIO` cases preserved honestly | Strong, with honest gaps |
| Optimization Catalog | Scope-boundary deferral cited | Comprehensive (the deferral itself is the citation) |
| Provider Matrix | Consulted; no independent content required | N/A — genuinely not applicable |
| Cache Strategy | Deferral cited | Comprehensive (deferral) |
| Agent Optimization | AR-004 boundary statement cited | Comprehensive |
| Inference Optimization | H17/H18 measurement-separation pattern cited by analogy for §22 | Adequate |

### 26.8 Entry Count Summary

`QG-NNN` entries: **10/10**. Algorithms: **10/10 (A–J)**. Required matrices: **6/6**. Source gaps: **2** (§20). Contradictions: **0** (§20).

---

## 27. Source Gaps

`SOURCE-GAP-QUALGATE-01` — **Minimum observed-accuracy bar for "calibrated" status.** No source specifies a numeric minimum observed accuracy (or equivalent statistical bar) a probabilistic verifier must clear against its benchmark set before `calibrated = true` is set. *Affected section:* §10.B. *Why it matters:* without this, "calibrated" risks becoming a status set once and never revisited against a bar. *Expected authoritative source:* `eval.md` (statistical methodology) or a future `architecture.md` amendment. *Disposition:* recorded, not invented around. *Blocking:* No — non-blocking; the interface (`calibrated: boolean`) and the invariant (OBJ-028) are both already fully specified upstream; only the specific acceptance bar is missing.

`SOURCE-GAP-QUALGATE-02` — **Verifier recalibration cadence.** No source specifies how often a probabilistic verifier should be re-calibrated absent a drift signal (fixed schedule, event-triggered only, or both). *Affected section:* §10.E. *Why it matters:* affects operational cost and staleness risk of calibration status. *Expected authoritative source:* `eval.md` or an operational runbook, neither of which exists yet. *Disposition:* recorded; §10.E's candidate triggers are offered as options, not asserted as the answer. *Blocking:* No.

No other genuine gap was identified beyond what upstream documents already carry forward (e.g., `SOURCE-GAP-ARCH-02`/`SOURCE-GAP-AGENTOPT-01` remain that document's own concern, not re-opened here).

---

## 28. Source Contradictions

None found. PS §9 and `architecture.md` §28 were compared directly for the quality-dimension taxonomy and match verbatim (§4). PS §52.6 and `architecture.md` §47.4.3/`conventions.md` §7.10 were compared for the verifier-calibration invariant and are consistent. No contradiction between this document's proposed methodology and any higher-authority source was identified — every `PROPOSED METHODOLOGY`/`PROPOSED DEFAULT` item in this document fills a genuine absence (§27) rather than conflicting with an existing rule.

---

## 29. Final Report

- **Document generated:** `docs/quality-gates.md`, this pass.
- **Maturity level:** Level 0 — RESEARCH, consistent with every other document in this chain.
- **Source documents actually read:** all 12 listed in the header table, plus root `CLAUDE.md` — every cited ID was located and read at its actual current line before citation (line numbers given inline throughout for the highest-risk citations).
- **Canonical quality-gate count:** 10/10 `QG-001`–`QG-010`, matching `architecture.md` §28 / PS §9 verbatim, no discrepancy found.
- **Quality-dimension coverage:** 10/10.
- **Compression-contract coverage:** 3/3 context types (Coding, RAG, Agent), test-suite outline added for each without redefining the invariant rows.
- **Verifier-calibration coverage:** full procedure (A–G) added without redefining `VerifierCalibrationLayer`'s interface schema.
- **Optimization coverage:** token, context, cache, inference/P5, agent, model/provider routing, tool/MCP, multi-agent, coding-agent, agentic RAG — all addressed in §13/§20, each by citation rather than redefinition of the owning document.
- **Application-class coverage:** generic, autonomous agent, developer/coding agent, multi-agent — all addressed in §20, same EAIOC principles, no separate architecture invented.
- **Algorithm coverage:** 10/10 (Algorithms A–J, §6.3), Control-Plane decision logic only — no evaluator/ML/GPU/benchmark-engine/database/scheduler implementation.
- **Matrix coverage:** 6/6 required matrices (§26.1–26.6), plus the chain's standard Cross-Document Coverage Matrix (§26.7).
- **Scenario coverage:** all 5 `SCN-QUAL-NNN` cited; `SCN-NOPT-001` cited for `DO_NOT_OPTIMIZE`.
- **Edge-case coverage:** 17 candidate EC-NNN IDs from the generation prompt were checked against the actual current `edge-cases.md` — **all 17 were found to exist exactly as named** (EC-020, 052, 061, 062, 063, 065, 069, 071, 072, 076, 077, 154, 162, 163, 164, 165, 211); none was absent or renamed, so no candidate reference required substitution or omission.
- **Source gaps:** 2 (`SOURCE-GAP-QUALGATE-01`, `SOURCE-GAP-QUALGATE-02`), both non-blocking.
- **Contradictions:** 0.
- **Proposed methodology/threshold limitations:** every numeric threshold in this document is `Threshold = policy/configuration/workload-defined` or explicitly `PROPOSED DEFAULT — VALIDATE LOCALLY`/`PROPOSED METHODOLOGY` — no threshold is presented as production-validated, benchmark-proven, or universally applicable. The one exception to "threshold is workload-defined" is `QG-007` (Safety), whose floor is fixed by `SEC-005`, not by local policy — stated explicitly in §5, not left ambiguous.
- **Coverage honesty confirmation:** the four existing `PARTIALLY COVERED` classifications this document touches (`EC-069`, `EC-162`, `EC-165`, `EC-211`) were preserved exactly as `scenario-matrix.md` states them (§26.6) — none was silently upgraded to `DIRECTLY COVERED`.
- **Per-Optimization Quality Validation:** **MANDATORY** — every optimization application is individually validated (§3.1/§3.2) after its transformation and before its result is admitted to the next optimization stage or execution stage; a rejected result is never propagated (§6.4). This applies uniformly to every optimization category in §13 (all marked "Per-Optimization Validation Required: YES"), with the dimensions actually run at each checkpoint scoped to what's applicable, not all 10 unconditionally.
- **Dedicated scenario coverage for the composition-level invariant:** does **not** exist. `SCN-QUAL-001`/`002`/`003` each validate a single optimization application receiving a checkpoint; no scenario in `scenario-matrix.md` constructs a multi-step optimization chain and asserts an intermediate stage's failure is caught before a later stage runs. Recorded honestly as `NO DEDICATED SCENARIO` in §6.4 — not claimed as covered.
- **Deferred implementation areas:** benchmark-corpus construction/A-B methodology (`eval.md`), telemetry/dashboard implementation (`observability.md`), CIS/TMG mechanism detail (`security.md`), cross-document requirements traceability (`requirements-traceability.md`) — none built here.
- **Security/policy precedence confirmation:** §12 preserves Security/Authorization/Policy above Quality Requirements above Correctness/Integrity above Optimization Benefit above Cost/Latency Preference, with no competing model introduced.
- **State/revalidation confirmation:** §14 confirmed; no new schema field invented.
- **Recovery/supersession confirmation:** §15 confirmed; "Resume != Replay" preserved verbatim.
- **H18 measurement-separation confirmation:** §22 confirmed; quality measurement kept distinguishable from token/context/cache/P5/agent/business-metric savings.
- **File-scope confirmation:** only `docs/quality-gates.md` was created in this pass. No upstream document (`problemStatement.txt`, Engineering Spec, `architecture.md`, `interfaces.md`, `conventions.md`, `edge-cases.md`, `scenario-matrix.md`, `optimization-catalog.md`, `provider-matrix.md`, `cache-strategy.md`, `agent-optimization.md`, `inference-optimization.md`) was modified. No downstream document (`security.md`, `observability.md`, `eval.md`, `SCALING.md`, `implementation-plan.md`, `requirements-traceability.md`, ADRs) was created.

All required sections (§1–§29 above, 29 of the 30 minimum structure items, the 30th being this Readiness section itself) exist; all 10 `QG-NNN` entries exist in verified authoritative order; all 10 algorithms exist; all 6 matrices exist; every reference was verified against the current source file before citation; no fabricated ID exists; no blocking contradiction remains; no implementation leakage exists (no evaluator/ML/GPU/benchmark-engine/scheduler code); proposed methodology is labeled throughout; unsupported thresholds are not presented as authoritative; limitations are honestly disclosed; file scope is correct.

## Quality Gates Readiness

READY FOR NEXT DOCUMENTATION PHASE
