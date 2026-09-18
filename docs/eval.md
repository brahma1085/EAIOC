# Evaluation, Experimentation, Benchmarking, Statistical Evidence, Rollout, and Regression Reference

**Document ID:** EAIOC-EVAL-001
**Status:** PRE-IMPLEMENTATION — Level 0 (RESEARCH)
**Version:** 1.0.0
**Generated:** 2026-09-18 (per repository date)
**Generation prompt:** `docs/prompts/generate-eval.prompt.md` (V3 Master Prompt)

**Position in the documentation chain:** Ninth generated downstream document, following `optimization-catalog.md` → `provider-matrix.md` → `cache-strategy.md` → `agent-optimization.md` → `inference-optimization.md` → `quality-gates.md` → `security.md` → `observability.md`. Gated by each of those eight documents' own passing readiness verdict. Next in chain: `SCALING.md` (not generated here, not cascaded into).

**Sources read in full or by targeted section/ID lookup:** `architecture.md` (§21 EL-001–005, §32 Benchmarking Framework, §33 Enterprise Deployment Model, §34 Rollout Strategy, §35 Regression Testing, §27.2/§27.3 cost model, §1.6 doc-boundary table, AC/OBJ traceability tables); `interfaces.md` (§18 Evaluation Interface / `INTF-030` `EvaluationFramework`, `EvaluationRun`, `EvaluationComparison`, `RegressionReport` — verified undefined); `conventions.md` (§20 Benchmarking and Evaluation Conventions, §20.3 Optimization Maturity Model, §20.4/§20.5 EL-002/EL-004); `edge-cases.md` (§39 — `EC-071`, `EC-072`, both verified directly); `scenario-matrix.md` (all 30 domains A–AD searched; Domain AD compound scenarios `SCN-CMP-051/052/056/057` verified directly, `SCN-CMP-028` verified but found not relevant); `quality-gates.md` (§10 VCL, `SOURCE-GAP-QUALGATE-01/02`, every literal `eval.md` deferral); `security.md` (§10 `GSP-004` HAG, citation only); `observability.md`, `optimization-catalog.md`, `provider-matrix.md`, `cache-strategy.md`, `agent-optimization.md`, `inference-optimization.md` (each consumed for its own already-named metrics/boundaries, not redefined). Root `CLAUDE.md` re-read for scope/ID/process discipline.

**Scope boundary reminder:** This document owns `EL-001`–`005`'s full algorithms, benchmark-corpus construction methodology, A/B statistical methodology, sample-size/confidence/significance methodology, rollout-gating detail, and regression-attribution methodology. It does **not** redefine `quality-gates.md`'s quality methodology or VCL mechanism, `security.md`'s GSP mechanisms, `observability.md`'s telemetry/dashboard implementation, `interfaces.md`'s schemas, or any already-named metric — those are cited throughout, never restated as if newly defined here.

---

## 1. Status and Maturity

This document is **Level 0 — RESEARCH / PRE-IMPLEMENTATION**, consistent with every other document in this chain. No local production traffic, no measured benchmark history, no deployed evaluation pipeline exists. Accordingly:

- No sample size, confidence level, significance threshold, corpus-refresh cadence, or rollout gate in this document is presented as validated or production-proven unless a specific upstream source is cited for it.
- Every number this document proposes that is not directly stated in an upstream source is tagged `PROPOSED DEFAULT — VALIDATE LOCALLY`.
- Every procedure this document proposes that is not directly stated in an upstream source is tagged `PROPOSED METHODOLOGY`.
- Where an upstream source already states a concrete rule (e.g., "Progressive activation: Shadow → 1% → 5% → 25% → 50% → 100%" — `architecture.md` §21.2 / `conventions.md` §20.4), that rule is cited as `SOURCE-DEFINED`, not re-derived.

## 2. How to Read This Document

Every claim carries one of six labels: `SOURCE-DEFINED` (an upstream document states this exactly), `SOURCE-DERIVED` (directly implied by combining multiple source requirements, formulation synthesized here), `PROPOSED METHODOLOGY` (a technique/procedure this document introduces because no source specifies one), `PROPOSED DEFAULT — VALIDATE LOCALLY` (a specific number this document proposes for the same reason), `DERIVED METRIC` (a calculated metric composed from canonical source metrics), or `ILLUSTRATIVE EXAMPLE` (a number used only to make a methodology concrete, never a rule). Do not read any `PROPOSED`/`ILLUSTRATIVE` label as authoritative.

## 3. Scope Boundary

| Concern | Owner |
|---|---|
| `EL-001`–`EL-005` full algorithms | **This document** |
| Benchmark-corpus construction and refresh methodology | **This document** |
| Baseline/treatment comparability and confound control | **This document** |
| A/B statistical methodology, sample size, confidence, significance | **This document** |
| Regression detection/attribution methodology | **This document** |
| Rollout-phase gating criteria (beneath `architecture.md` §34) | **This document** |
| Evaluation-run/comparison operational mechanics beneath `INTF-030` | **This document** |
| Evaluation evidence integrity taxonomy | **This document** |
| Quality-dimension definitions and thresholds | `quality-gates.md` (cited, not redefined) |
| VCL calibration mechanism (`interfaces.md` §43.9) | `quality-gates.md` §10 (cited, not redefined) |
| Governance/approval mechanisms (SGE/DGE/TMG/HAG/CIS) | `security.md` (cited, not redefined) |
| Telemetry pipeline, dashboards, alerting implementation | `observability.md` (cited, not redefined) |
| `EvaluationFramework`/`EvaluationRun`/`EvaluationComparison` schemas | `interfaces.md` §18 (consumed, not redefined) |
| Cache mechanics | `cache-strategy.md` (consumed, not redefined) |
| Agent-loop/sub-agent mechanics | `agent-optimization.md` (consumed, not redefined) |
| Inference-serving (P5) mechanics | `inference-optimization.md` (consumed, not redefined) |
| Provider-specific facts | `provider-matrix.md` (consumed, not redefined) |
| Capacity/throughput targets | `SCALING.md` (not yet generated) |
| Implementation sequencing | `implementation-plan.md` (not yet generated) |

Where an upstream document already defines an exact metric name or field (e.g., `EvaluationRun.net_savings`, `control_plane.cache.hit_rate`), this document consumes that name. No competing metric name is introduced for a concept that already has one.

## 4. Evaluation Invariants

> **An optimization is successful only when its measured benefit is evaluated against its cost, latency, quality impact, reliability, fallback behavior, workload applicability, and statistical/evidentiary uncertainty** (`SOURCE-DERIVED`, synthesized from `architecture.md` §4's central objective, §32, `AC-017`, `AC-018`, `OBJ-012`).

This decomposes into the discipline every section below depends on:

1. **Token reduction alone is never success** (root `CLAUDE.md`, restated verbatim from `architecture.md` §4/§28's `[!CAUTION]`) — every measured effect must clear a quality gate (`quality-gates.md`, cited) and a net-positive economic accounting (§22) before being counted.
2. **Research benchmark percentages are not production guarantees** (`conventions.md` §20.2, root `CLAUDE.md` rule 7, `AC-018`) — every externally-sourced percentage is a re-measurement target on the organization's own workload (`AC-017`), never a hard-coded expectation.
3. **Every optimization must be independently measurable** (`AC-015`) and **must have a fallback** (`AC-016`) — this document evaluates against both, never assumes either.
4. This document's own EAIOC-wide invariant it consumes rather than redefines (`observability.md` §4, `architecture.md` §31/NFR-006, §47.16/NFR-010): *"every optimization decision must be measurable, and every governed decision must produce a retrievable explanation."* Evaluation results are one of the measurements that invariant requires — this document defines how those measurements are constructed and interpreted, not how they are logged or displayed (`observability.md`'s scope).
5. **Gross measured effect and net evaluated value are not the same thing** — a technique that reduces tokens but costs more in optimizer overhead, degrades quality, or cannot be statistically distinguished from noise has not been evaluated as successful merely because a raw percentage looks favorable.

## 5. Evaluation Architecture

The evaluation layer sits beneath `architecture.md` §21 (Optimization Experimentation and Learning) and consumes, without redefining: cost/quality/latency measurements produced by the optimization pipeline itself (`optimization-catalog.md`, `agent-optimization.md`, `inference-optimization.md`, `cache-strategy.md`), quality verdicts (`quality-gates.md`), governance/approval outcomes (`security.md`), and raw telemetry (`observability.md`). Its own operational surface is `interfaces.md` §18's `EvaluationFramework` (`INTF-030`):

```
EvaluationFramework {
  run_baseline(request)                    -> EvaluationRun
  run_optimized(request, plan)             -> EvaluationRun
  compare(baseline, optimized)             -> EvaluationComparison
  report_regression(comparison)            -> RegressionReport   // undefined — see SOURCE-GAP-EVAL-01
}
```

`SOURCE-DEFINED` — verified directly against `interfaces.md` §18 (`INTF-030`, "3 schemas": the interface itself, `EvaluationRun`, `EvaluationComparison`). `RegressionReport` is referenced by `report_regression()`'s return type but is not one of the 3 schemas actually defined in that section — confirmed still true; disposed at §43 (`SOURCE-GAP-EVAL-01`).

## 6. Evaluation Lifecycle

The five `EL-NNN` modules interact but are not required by any source to run in one fixed sequence for every optimization — each module's `architecture.md` §21 stub is independently triggerable. A common (not mandatory) lifecycle observed across the sources is:

```
EL-001 Shadow Optimization
      |  (accumulates baseline-vs-simulated-optimized evidence)
      v
EL-003 A/B Testing  <---------------------+  (may run instead of, or in parallel with, EL-002)
      |                                    |
      v                                    |
EL-002 Controlled Rollout  ----------------+
      |
      v
EL-005 Regression Detector  (continuous, throughout and after rollout)
      |
      v
EL-004 Continuous Policy Learning  ---> Governance Review / HAG Approval (security.md, cited) ---> back to EL-001/EL-002 for the next cycle
```

`PROPOSED METHODOLOGY` — this exact cyclical diagram is not stated verbatim by any source; it is synthesized from the five independent stubs plus `SCN-CMP-051/052/056/057`'s evidence that these modules do interact pairwise. Do not treat this as a mandatory pipeline: a technique may go straight from `EL-001` to `EL-002` without a formal `EL-003` A/B test if the shadow evidence is sufficient (no source requires an A/B test for every optimization — `architecture.md` §21.3 says A/B testing "should" be *available*, not that it is *mandatory* for every technique).

---

## 7. `EL-001` — Shadow Optimization

**Purpose (`SOURCE-DEFINED`, `architecture.md` §21.1):** Run optimization decisions in shadow mode without modifying production requests. Compare: baseline tokens vs. simulated optimized tokens, predicted cost, predicted quality, cacheability, routing decisions, expected savings.

**Applicability.** Any `TECH-NNN`/`DA-NNN`/`P5 Index` technique at Optimization Maturity Level 0 (RESEARCH) or 1 (EXPERIMENTAL) seeking promotion to Level 2 (SHADOW) — `conventions.md` §20.3, `SOURCE-DEFINED`.

**Mechanics.** For each real incoming request: (1) execute the actual, user-facing baseline pipeline unmodified; (2) internally simulate the candidate-optimized pipeline against the same request without surfacing its output anywhere the caller or downstream system can observe; (3) call `EvaluationFramework.run_baseline()` and `run_optimized()` with `run_type = SHADOW` (`INTF-030`, `SOURCE-DEFINED`); (4) accumulate the resulting `EvaluationRun` records as evidence.

**Decision logic / promotion criteria.** `PROPOSED DEFAULT — VALIDATE LOCALLY`: promote a technique from shadow to `EL-002` controlled rollout only once its accumulated shadow evidence shows `net_savings > 0` and a quality delta within the bound `quality-gates.md` establishes (cited, not redefined) across a minimum evidence volume — illustratively, on the order of hundreds of shadow comparisons per representative workload segment (`ILLUSTRATIVE EXAMPLE` only; the actual bar is a sample-size question, see §18).

**Failure behavior.** A failure in the shadow-simulation path itself (e.g., the simulated pipeline errors) has, by construction, zero blast radius on the production request — the user-facing baseline path is entirely untouched. This is logged as an evaluation-integrity event (§32), not a request-level failure, and is distinct from an optimization stage failing *in the live path* (which is `conventions.md` §16.2's fail-open case, not this one).

**Interaction with other EL modules.** Feeds evidence into `EL-002`'s promotion gate and `EL-004`'s candidate-generation input. Can run concurrently with an `EL-003` A/B test, but `SCN-CMP-051` requires that any single shadow-vs-baseline comparison pair use the same policy version — a live policy change mid-experiment must not silently mix pre-change and post-change comparisons. `EL-005` also monitors shadow-evidence trend, not only live-production trend, to catch degradation before rollout begins.

**Validated By:** `SCN-CMP-051` (verified directly — "Shadow Optimization Running Alongside a Live Policy Change," `Source Requirements: EL-001`).

---

## 8. `EL-002` — Controlled Rollout

**Purpose (`SOURCE-DEFINED`, `architecture.md` §21.2 / `conventions.md` §20.4):** Support progressive activation — Shadow → 1% → 5% → 25% → 50% → 100% — with automatic rollback on quality/cost regressions.

**Eligibility to enter.** Requires `EL-001`'s shadow-evidence bar to have been cleared (§7) — corresponds to advancing from Maturity Level 2 (SHADOW) to Level 3 (CONTROLLED PILOT: "Limited production traffic; baseline fallback preserved" — `conventions.md` §20.3, `SOURCE-DEFINED`).

**Exposure control.** `PROPOSED METHODOLOGY` — no source specifies the exact cohort-selection mechanism. A deterministic, tenant-boundary-respecting hash of a stable request/tenant identifier is proposed to select the active percentage cohort, so that: (a) the same tenant/workflow is consistently bucketed for the duration of one rollout step (avoiding flicker between optimized/unoptimized behavior for the same caller within a step), and (b) tenant isolation (root `CLAUDE.md` rule 4) is preserved — bucketing never blends cost/quality data across tenants.

**Promotion between steps.** `PROPOSED DEFAULT — VALIDATE LOCALLY`: each of the five percentage steps (1/5/25/50/100%) requires a minimum dwell time and minimum sample count with no active rollback trigger before advancing to the next step — no source specifies concrete numbers for either.

**Automatic rollback triggers.** `architecture.md` §21.2 states rollback occurs "on quality/cost regressions" but names no numeric trigger. `PROPOSED DEFAULT — VALIDATE LOCALLY`: automatic rollback fires when `EvaluationComparison.recommendation = REJECT` (`INTF-030`, `SOURCE-DEFINED` field) is produced by `EL-005`'s continuous monitoring during the rollout window, with `rejection_reasons` populated from the comparison.

**Manual rollback.** Always available to an operator regardless of automatic-trigger state — this is a distinct, superseding control path, not a fallback of last resort.

**Evaluation evidence vs. production authorization.** A rollout step advancing on positive `EvaluationComparison` evidence is a statement about *measured net value*, never a statement about *authorization*. Whether the technique is permitted to run against a given tenant/request at all remains a separate, independently-owned decision under `security.md`'s governance plane (the same `WITHIN_BUDGET`-never-implies-authorized pattern `security.md` and `observability.md` already establish, applied here to rollout evidence specifically).

**Interaction with other EL modules.** `EL-005` is the primary automatic-rollback trigger source. `EL-004`'s learned-policy deployments route through this same controlled-rollout mechanism after governance approval (§10).

**Validated By:** `NO DEDICATED SCENARIO`. `SCN-CMP-051` (`EL-001`'s scenario) touches shadow/policy interaction but does not exercise the percentage-progression mechanic itself; `AC-029`'s traceability entry in `scenario-matrix.md` §36 also points only to `SCN-CMP-051`. Recorded honestly rather than force-fit — see `SOURCE-GAP-EVAL-05` (§43).

---

## 9. `EL-003` — Optimization A/B Testing

**Purpose (`SOURCE-DEFINED`, `architecture.md` §21.3):** Every optimization should be independently testable against a control group.

**Design.** Control = the baseline pipeline; treatment = the candidate optimization. `PROPOSED METHODOLOGY` (no source specifies the assignment mechanism): stratified randomization by tenant and workload-type segment, preserving tenant boundaries absolutely — `SCN-CMP-052` requires results be segmented per-tenant, never blended in a way that lets one tenant's traffic shift silently bias the aggregate conclusion.

**Statistical treatment.** Full methodology in §17–§20; this section only fixes the experimental design (control/treatment/assignment), not the analysis.

**Stopping rules.** `PROPOSED METHODOLOGY`: a fixed-horizon design (pre-registered sample size or duration, analyzed once at completion) is proposed as the simpler default over a sequential/alpha-spending design, since no source mandates either — `VALIDATE LOCALLY` before adopting for a specific experiment where early stopping has material cost/quality risk implications.

**Contamination.** Cross-referenced in full at §30; `SCN-CMP-052`'s tenant-composition-drift scenario is the primary validating evidence for this specific failure mode.

**Interaction with other EL modules.** May run concurrently with `EL-001` shadow evaluation of a different technique. `EL-005`'s regression detector must be able to distinguish a regression caused by the A/B treatment itself from an unrelated cause occurring during the test window (`SCN-CMP-056`, §11/§34). A learned-policy proposal from `EL-004` that would affect the same population as an active A/B test must not be silently auto-applied (`SCN-CMP-057`, §10).

**Validated By:** `SCN-CMP-052` (verified directly — "A/B Test Control and Treatment Groups Diverge in Tenant Composition Mid-Test," `Source Requirements: EL-003`).

---

## 10. `EL-004` — Continuous Policy Learning

**Purpose (`SOURCE-DEFINED`, `architecture.md` §21.4):** Learn from production outcomes — which optimization pays off, which workload benefits, which model is sufficient, which compression ratio is safe, which tools are actually needed, which agent loops tend to fail.

**CRITICAL governance boundary (`SOURCE-DEFINED`, `architecture.md` §21.4 `[!CAUTION]`, restated `conventions.md` §20.5):** *"Learning must be bounded by governance and may not silently change production policy without configured approval."* This document must not, and does not, describe `EL-004` as capable of autonomous production-policy mutation. Every learned-policy candidate that would change production behavior must route through `security.md`'s Human Approval Gate (`GSP-004` / `SEC-015` / `INTF-066`, §10 of that document — cited, not redefined here) before deployment.

**Lifecycle (`PROPOSED METHODOLOGY` — the loop itself is not source-defined verbatim; each bookend constraint is):** Observe production outcomes → generate a learned-policy candidate → offline-evaluate it (§17–§20's methodology) → benchmark it (§13–§14) → run it through `EL-005`'s regression check → submit for governance review → **hold for `GSP-004` human approval** (mandatory gate, not optional) → deploy via `EL-002`'s controlled-rollout mechanism → resume observation.

**Conflict with a concurrent experiment.** `SCN-CMP-057` requires that a learned-policy proposal conflicting with an active `EL-003` A/B test over the same population be held for governance approval with the conflict explicitly flagged for the approver, and never silently auto-applied while the conflicting test is active — applying it mid-test would corrupt that test's validity.

**Evidence retention.** `PROPOSED METHODOLOGY` — no source specifies a retention policy for learned-policy candidates and their supporting evidence; recommended practice is to retain the full evaluation trail (shadow/benchmark/regression evidence) for every candidate through its approval or rejection, for audit purposes consistent with `observability.md`'s retrievable-explanation invariant (cited, not redefined).

**Interaction with other EL modules.** Consumes `EL-001`/`EL-003` evidence as candidate-generation input; deploys via `EL-002`; is itself subject to `EL-005`'s regression monitoring post-deployment like any other change.

**Validated By:** `SCN-CMP-057` (verified directly — "Continuous Policy Learning Proposes a Change That Would Conflict With an Active A/B Test," `Source Requirements: EL-004`). Note: this scenario was found through independent verification of the full scenario matrix, not assumed from any prior characterization — the compound-scenario candidate list a prior draft of this document's generation prompt proposed (`SCN-CMP-028/051/052/056`) did not include it.

---

## 11. `EL-005` — Optimization Regression Detector

**Purpose (`SOURCE-DEFINED`, `architecture.md` §21.5):** Detect when a previously successful optimization stops producing positive net value after: model changes, provider pricing changes, prompt changes, tool changes, repository changes, traffic-pattern changes, cache behavior changes.

**Extension already claimed by an upstream document.** `quality-gates.md` §10.D explicitly states its `DriftEvent`/verifier-drift detection "extends `EL-005` (Optimization Regression Detector) to verifier drift specifically" and cites this as `architecture.md` §47.4.3's own stated extension. This document does not redefine that extension — it is `quality-gates.md`'s to own — but records it here as part of `EL-005`'s full interaction surface: `EL-005` covers technique-level net-value regression (this document's scope) and, by that upstream extension, verifier-behavior drift (`quality-gates.md`'s scope).

**`EC-071` full coverage (verified directly against `edge-cases.md` §39):** After every model or provider update, re-run the experimental validation matrix (§13) for all enabled P1/P2/P3 techniques; if a technique's quality metric drops below its validated baseline, disable it automatically and alert (`regression.technique_quality_regression.count`, `regression.technique_disabled_after_regression`).

**Attribution logic.** See §34. When a regression is detected during a concurrent `EL-003` A/B test, the detector's analysis must be segmented by comparison group (`SCN-CMP-056`) rather than raised as one undifferentiated alert that could misattribute the cause.

**Interaction with other EL modules.** Primary trigger source for `EL-002`'s automatic rollback; monitors `EL-001` shadow evidence trend as well as live-production trend; gates `EL-004` deployments post-hoc.

**Validated By:** `SCN-CMP-056` (verified directly — "Regression Detector Flags a Technique Just as It's Being A/B Tested for an Unrelated Reason," `Source Requirements: EL-005 combined with EL-003`) and `EC-071`.

---

## 12. Cross-`EL` Interaction

See §6 for the full lifecycle diagram and its caveats. Additional pairwise notes not already covered in §7–§11:

- `EL-001` and `EL-003` can run on the same technique simultaneously (shadow evaluation continuing while a formal A/B test also runs) provided policy-version pinning (`SCN-CMP-051`) is respected for each independently.
- `EL-002` and `EL-004` share the same deployment mechanism (controlled percentage rollout) — a learned-policy deployment is not a special-cased instant flip to 100%.
- `EL-005` is the one module that is continuously active across every other module's operation, not a discrete phase of its own.

---

## 13. Benchmarking Framework

**Required corpus types (`SOURCE-DEFINED`, `architecture.md` §32.1, verbatim):** Simple prompts, Long prompts, Long-context tasks, RAG tasks, Coding tasks, Debugging tasks, Tool-heavy agent tasks, Multi-turn conversations, Structured extraction, Comparative queries, Procedural queries, High-risk/safety-sensitive workflows where permitted.

**Required comparison per request (`SOURCE-DEFINED`, `architecture.md` §32.2):** BASELINE (original application behavior without optimization) vs. OPTIMIZED (full optimization pipeline), comparing: input tokens, output tokens, total tokens, cached tokens, model calls, tool calls, cost, latency, quality, failure rate.

**Experimental Validation Matrix (`SOURCE-DEFINED`, `architecture.md` §32.3):** all 20 techniques and their primary metrics are unchanged from the source table (Sanitization, Query compression, Context pruning, Context deduplication, Adaptive Top-K, Token-aware ranking, Context compression, Context reordering, Prompt caching, Semantic caching, Tool output filtering, Tool result caching, Model routing, Model cascade, Reasoning budget control, Agent early exit, Output schema, Output length control, Batch optimization, Soft reset) — each validated **independently** before permanent pipeline activation; cited by reference to `architecture.md` §32.3 rather than reproduced in full here to avoid a second copy drifting from the source.

**Multiple-comparisons consideration.** `PROPOSED METHODOLOGY`: when several of the 20 techniques are evaluated simultaneously against the same corpus, a multiple-comparisons correction (e.g., false-discovery-rate control) should be applied before declaring any individual technique's improvement statistically significant — no source mandates a specific correction method.

## 14. Benchmark Corpus Construction

`PROPOSED METHODOLOGY` for corpus composition and lifecycle (no source specifies these mechanics beyond the required types in §13):

- **Composition:** a mix of synthetic (constructed to exercise a specific corpus type), curated (hand-selected representative examples), and — where permitted by data-governance policy (`security.md`'s DGE, cited not redefined) — replayed/anonymized production-derived requests, plus a dedicated edge-case slice drawn from `edge-cases.md`'s own scenarios.
- **Holdout discipline:** a held-out validation slice never used for threshold-tuning, to prevent overfitting the benchmark corpus to the techniques being validated against it.
- **Provenance/versioning:** each corpus item tagged with a source-type label (`SYNTHETIC`/`CURATED`/`REPLAYED`/`PRODUCTION-DERIVED`) and the corpus itself versioned (`corpus_id`, `corpus_version`) so a given evaluation run can cite exactly which corpus snapshot produced its evidence.

**`EC-072` full coverage (verified directly):** Benchmark corpus distribution divergence from production traffic distribution must be tracked (`benchmark.corpus_age_days` gauge; alert on divergence exceeding threshold). `EC-072`'s own text offers "(e.g., monthly)" as an illustrative refresh cadence, not a mandated one — this document treats that as an `ILLUSTRATIVE EXAMPLE` anchor and proposes, `PROPOSED DEFAULT — VALIDATE LOCALLY`, a monthly refresh as the starting default, revised sooner if divergence-tracking flags drift earlier. Fallback per `EC-072`: use `EL-001` production shadow traffic for validation while the corpus is refreshed.

## 15. Baseline vs. Treatment Comparability

Comparison dimensions are `SOURCE-DEFINED` (§13). What must be held constant between a baseline run and its paired treatment run to make that comparison valid is `PROPOSED METHODOLOGY`: workload, tenant, application/workflow, agent configuration, model/provider identity and version, prompt template, tool configuration, cache state (or explicit cache-state accounting, §24), policy version, and evaluator version. Any of these that differs between the baseline and treatment run for a single comparison pair is a confounder and must be identified as such in the resulting `EvaluationComparison`, not silently absorbed into the reported delta.

## 16. Required Evaluation Measurements

`SOURCE-DEFINED` directly from `interfaces.md` §18's `EvaluationRun` schema (`INTF-030`) — no competing metric names introduced:

`baseline_cost`, `optimized_cost`, `gross_savings`, `optimizer_overhead`, `net_savings`, `net_savings_pct`, `quality_delta`, `quality_score_baseline`, `quality_score_optimized`, `latency_delta_ms`, `latency_ms_baseline`, `latency_ms_optimized`, `cache_hit_rate`, `tokens_avoided_by_cache`, `task_success_baseline`, `task_success_optimized`, `regression_detected`, `regression_dimensions`.

These map directly onto `architecture.md` §32.2's required comparison list and §35's regression dimensions — this document does not introduce parallel field names for the same concepts.

---

## 17. Statistical Methodology

This document owns the statistical methodology every upstream document in this chain explicitly defers here (§41, full deferral audit). All content in §17–§20 is `PROPOSED METHODOLOGY` unless otherwise cited — no source specifies statistical procedure or numeric defaults for evaluation.

## 18. Sample Size and Power

`PROPOSED METHODOLOGY`: required sample size for a given comparison depends on the expected effect size, the outcome metric's variance, the desired confidence level, the acceptable false-positive rate (Type I), and the acceptable false-negative rate (Type II/power) — standard comparison-of-means power-analysis reasoning applies conceptually. No single universal sample size is prescribed, consistent with the instruction not to state one. `ILLUSTRATIVE EXAMPLE` only, not a rule: a pilot A/B comparison targeting a medium effect size at 80% power and 95% confidence might, as a rough starting point, require on the order of a few hundred paired samples per arm for a moderately variable cost/latency metric — this must be recalculated per workload and per metric, never treated as a fixed requirement.

**Paired vs. unpaired design.** `PROPOSED METHODOLOGY`: prefer a paired comparison (the same request replayed through both baseline and treatment) wherever feasible — `EL-001`'s shadow mode naturally produces paired data. Fall back to an unpaired/cohort design only when true replay isn't feasible (e.g., a live `EL-003` A/B test where each request is served by exactly one arm).

## 19. Confidence and Uncertainty

`PROPOSED DEFAULT — VALIDATE LOCALLY`: report a 95% confidence interval on `net_savings_pct` and `quality_delta` as the default reporting convention for any `EvaluationComparison` used to justify a promotion decision (§7–§11). No source specifies a confidence level.

## 20. Statistical vs. Practical Significance

These are explicitly distinct and must not be collapsed:

- **Statistical significance** — evidence against the selected null hypothesis under the chosen statistical model (a function of effect size, variance, and sample size).
- **Practical significance** — whether the effect magnitude is operationally meaningful once optimizer overhead, quality risk, and operational complexity are accounted for.

A statistically significant `net_savings_pct` of 0.3% may not be worth the ongoing maintenance cost of a new technique; a practically meaningful-looking 8% improvement measured on 12 samples may lack the statistical evidence to be trusted. `PROPOSED DEFAULT — VALIDATE LOCALLY`: treat a technique as practically significant only when its `net_savings_pct` confidence interval's lower bound remains positive by a margin (illustratively, several percentage points) large enough to justify the operational cost of maintaining it — the exact margin is workload-specific and not source-defined.

---

## 21. Quality Evaluation Boundary

`quality-gates.md` owns quality-dimension definitions, thresholds, and the VCL calibration mechanism — not redefined here. This document consumes `EvaluationRun.quality_delta`/`quality_score_baseline`/`quality_score_optimized` and any `QG-NNN` pass/fail verdict as input evidence to the promotion/rollback/regression decisions in §7–§11 and §33–§37. `quality-gates.md`'s Coding/RAG/Agent-specific quality invariants and its mandatory per-optimization validation invariant (every optimization gets its own checkpoint, batch-after-all validation prohibited) are preserved and assumed, not re-derived.

## 22. Cost and Net-Value Evaluation

`architecture.md` §27.3's Cost Model is `SOURCE-DEFINED` and authoritative:

```
Baseline Cost  = Baseline Input Tokens x Input Rate + Baseline Output Tokens x Output Rate + Tool Costs + Other Provider Charges
Optimized Cost = Uncached Input Tokens x Input Rate + Cache Read/Write Cost + Compression Cost
               + Optimized Output Tokens x Output Rate + Tool Costs + Routing/Cascade Costs + Other Provider Charges
Net Savings    = Baseline Cost - Optimized Cost
Savings %      = Net Savings / Baseline Cost x 100
```

`SOURCE-DERIVED` reconciliation: `EvaluationRun`'s `gross_savings`/`optimizer_overhead`/`net_savings` fields (§16) decompose this same formula for reporting purposes — `optimizer_overhead` corresponds to the Cache Read/Write Cost, Compression Cost, and Routing/Cascade Cost line items that `architecture.md` §27.3 folds directly into Optimized Cost; `gross_savings` is the reduction before those overhead costs are subtracted; `net_savings = gross_savings - optimizer_overhead`, which is arithmetically the same quantity as `Baseline Cost - Optimized Cost`. This is not a second, competing formula — it is the same Cost Model exposed with overhead broken out for evaluation reporting. Root `CLAUDE.md`'s own restated formula (`Net Savings = Inference Cost Avoided - Optimization Compute Cost - ...`) is the same decomposition in different words, not a third formula.

Never equate observed token reduction with net economic success (§4, item 5) — a technique's `net_savings` must be positive **after** overhead, and its quality/latency/reliability impact must independently clear its gates, before it is counted as successful.

## 23. Latency Evaluation

`SOURCE-DEFINED` fields (`EvaluationRun.latency_delta_ms`, `latency_ms_baseline`, `latency_ms_optimized`) map directly onto `architecture.md` §32.2's required latency comparison. No additional latency methodology is proposed beyond what §17–§20's general statistical treatment already supplies when applied to this metric instead of cost/quality.

## 24. Cache Evaluation

`cache-strategy.md` owns cache mechanics — not redefined here. This document evaluates cache **effect**, distinguishing EAIOC application-managed cache types (`CACHE-001`–`004`/`006`–`007`) from `PROVIDER_NATIVE` cache (`CACHE-005`) per that document's own boundary. Provider-native cache savings must never be attributed to an EAIOC optimization technique without evidence that the technique itself, rather than the provider's own caching behavior, produced the observed reduction (root `CLAUDE.md` rule 5 — never fabricate savings). `EvaluationRun.cache_hit_rate`/`tokens_avoided_by_cache` are the measurement surface; which cache type produced them is a `cache-strategy.md` attribution question this document consumes rather than re-derives.

## 25. Agent and Multi-Agent Evaluation

`agent-optimization.md` owns agent-loop/sub-agent mechanics — not redefined here. This document evaluates: iteration count, tool-call count and avoided-tool-calls, early-exit behavior, sub-agent overhead, and — critically — `task_success_baseline`/`task_success_optimized` (`EvaluationRun`, `SOURCE-DEFINED`). Fewer iterations or tool calls alone do not establish success; a reduction in either metric that coincides with a drop in task-success rate is a regression, not an improvement, regardless of how favorable the iteration-count delta looks in isolation.

## 26. P5 / Inference-Serving Evaluation Separation

`inference-optimization.md` owns P5 mechanics under the `inference_serving.*` measurement namespace, kept measurement-separate from Layer 1/2 optimization effects per `architecture.md` §26/H17/H18 — not redefined here. This document preserves that separation: a combined cost/quality/latency report may present both effects together only if the P5 contribution remains explicitly decomposed and separately attributable, never silently merged into one undifferentiated Layer-1/2 optimization figure.

## 27. Evaluator Methodology

`quality-gates.md` §10 owns the VCL calibration mechanism and defines the 9 `VerifierDescriptor.verifier_type` values this document consumes without redefining: `UNIT_TEST`, `SCHEMA_VALIDATION`, `TYPE_CHECK`, `STATIC_ANALYSIS`, `BUSINESS_RULE`, `CITATION_CHECK`, `FORMAT_VALIDATION` (deterministic, confidence definitionally `1.0`), and `LLM_JUDGE_SEMANTIC_EQUIVALENCE`, `TASK_SUCCESS_CHECK` (probabilistic, require calibration). This document's role is limited to how evaluation *consumes* a verifier's `passed`/`confidence` output as evidence in an `EvaluationRun`/`EvaluationComparison` — never as unconditional ground truth for a probabilistic verifier below its calibrated confidence threshold (`OBJ-028`, cited).

## 28. Evaluator Calibration — Disposition of `SOURCE-GAP-QUALGATE-01`/`02`

`quality-gates.md` names this document as the expected authoritative source for two open gaps. Both are addressed here with a `PROPOSED DEFAULT`, and both remain formally open (per root `CLAUDE.md`'s own resolution discipline: a gap is only marked resolved when a source document is actually updated to close it — this document proposing a number narrows, but does not close, either gap, since `quality-gates.md` itself was not modified):

- **`SOURCE-GAP-QUALGATE-01`** (minimum observed-accuracy bar for "calibrated" status): `PROPOSED DEFAULT — VALIDATE LOCALLY` — a probabilistic verifier should not be set `calibrated = true` until its `observed_accuracy` on the labeled benchmark set (§13–§14) reaches at least 85%, illustratively, against a benchmark set of sufficient size (§18) to make that observed accuracy statistically meaningful rather than a small-sample artifact. This is a starting proposal, not an authoritative bar — `quality-gates.md`'s own gap record stands until a source document adopts a specific value.
- **`SOURCE-GAP-QUALGATE-02`** (verifier recalibration cadence): `PROPOSED DEFAULT — VALIDATE LOCALLY` — recalibrate on whichever of the following occurs first: a `DriftEvent` (`quality-gates.md` §10.D, event-triggered), an evaluator/model/provider change, or a fixed 90-day periodic ceiling if none of the event-triggered conditions has occurred sooner. `quality-gates.md` §10.E already offers these same trigger *categories* as options without a cadence number; this document supplies the illustrative number, still non-authoritative.

## 29. Workload Representativeness

`PROPOSED METHODOLOGY`: representativeness is assessed along the corpus-vs-production divergence tracked for `EC-072` (§14), decomposed where feasible by traffic/task-type distribution, tenant distribution, prompt-type distribution, context-size distribution, and model/provider distribution. This document does not invent or assume any organization's actual distribution values — representativeness is a measurement to perform locally, not a number this document can supply.

## 30. Experiment Contamination

Real, verified contamination scenarios already recognized in the sources: policy-version drift mid-shadow-comparison (`SCN-CMP-051`), tenant-composition drift mid-A/B-test (`SCN-CMP-052`), and regression-vs-A/B-treatment misattribution (`SCN-CMP-056`). `PROPOSED METHODOLOGY` general framework beyond these three specific, already-covered cases: pin comparison pairs to a single policy/model/provider version for the duration of one comparison window; isolate concurrent experiments' cache state where the same cache keys could otherwise be touched by both an experiment's control and treatment arms; and treat provider-side rate-limit throttling during an experiment as a confound to flag, not silently absorb into the measured latency delta.

## 31. Reproducibility

`PROPOSED METHODOLOGY` — this is a conceptual checklist of what an implementer should ensure is captured for any evaluation run to be reproducible, **not** a proposal to add new fields to `interfaces.md`'s `EvaluationRun` schema (that remains `interfaces.md`'s decision): beyond `EvaluationRun`'s existing `run_id`/`timestamp` (`SOURCE-DEFINED`), an implementer should be able to reconstruct the optimization-configuration version, policy version, model/provider identity and version, evaluator version, benchmark-corpus version, and environment that produced a given run. Where a random assignment mechanism is used (§9), the seed should be recorded.

## 32. Evidence Integrity

`PROPOSED METHODOLOGY` taxonomy, consistent with (not redefining) `observability.md`'s `OBS-001` verified-vs-estimated distinction (`EC-067`, cited) and root `CLAUDE.md` rule 5 (never fabricate savings — mark unverified and exclude from reporting): every evaluation result should be labeled as one of `OBSERVED`, `ESTIMATED`, `INFERRED`, `SIMULATED` (this is exactly what `EL-001` shadow evidence is), `BENCHMARK-DERIVED`, `PRODUCTION-DERIVED`, `VERIFIED`, or `UNVERIFIED`. An estimate must never be presented as an observation; an external research benchmark must never be presented as EAIOC-local validation (§4, item 2); incomplete data must never be presented as a verified result.

---

## 33. Regression Testing

Elaborating `architecture.md` §35 (`SOURCE-DEFINED`: "Every model, prompt, retrieval, or orchestration change shall be evaluated against the benchmark corpus... The system must detect when an optimization that previously produced savings stops producing net savings"), `PROPOSED METHODOLOGY` post-update revalidation procedure:

```
Detect Change (model/provider/policy/pricing/workload/corpus)
     -> Identify Affected Evaluation Scope (which techniques/EL-NNN runs are implicated)
     -> Freeze/Select Reference (the last-known-good baseline to compare against)
     -> Run Benchmark (§13)
     -> Run Quality Validation (quality-gates.md, cited)
     -> Compare Baseline/Treatment (§15-16)
     -> Analyze Cost/Latency (§22-23)
     -> Analyze Regression (§34)
     -> Attribute Cause (§34)
     -> Escalate / Roll Back (EL-002) / Accept
```

This directly implements `EC-071`'s expected behavior: re-run the full experimental validation matrix for every enabled P1/P2/P3 technique after every model or provider update; disable automatically and alert on any technique whose quality metric drops below its validated baseline.

## 34. Regression Attribution

`PROPOSED METHODOLOGY`: distinguishing which of several possible causes (optimization/technique change, model update, provider change, pricing change, workload drift, policy change, cache-behavior change, evaluator change) produced an observed regression requires segmenting the comparison by whichever dimension is under independent variation at the time. `SCN-CMP-056` supplies the canonical case: when a technique is simultaneously under `EL-003` A/B test and flagged by `EL-005`, segment by A/B group — degradation appearing equally in **both** control and treatment groups points to an unrelated cause (e.g., provider pricing); degradation isolated to the treatment group points to the technique itself. Do not claim causal attribution beyond what the experimental design actually supports — an unsegmented, single-group regression signal with multiple simultaneous changes cannot be causally attributed without further isolation.

## 35. Rollout Strategy

`architecture.md` §34's six phases and `conventions.md` §20.3's six maturity levels are two real, `SOURCE-DEFINED` sources that describe overlapping but not identically-ordered concepts — reconciled here rather than silently forced into a false one-to-one mapping:

| `architecture.md` §34 Phase | Description (`SOURCE-DEFINED`) | Related `conventions.md` §20.3 Level |
|---|---|---|
| PHASE 1 — OBSERVE | Run in shadow mode; do not modify production requests; record baseline token/cost/quality | Precedes Level 1; establishes the baseline `EL-001` shadow evidence is compared against |
| PHASE 2 — BENCHMARK | Run optimization offline; compare techniques independently; identify high-value workflows | Level 1 — EXPERIMENTAL ("Local benchmark; offline validation") |
| PHASE 3 — CONTROLLED PILOT | Enable optimization for selected workflows; preserve baseline fallback | Level 3 — CONTROLLED PILOT (same name; "Limited production traffic; baseline fallback preserved") |
| PHASE 4 — PRODUCTION | Enable validated optimizations; monitor cost, quality, latency, failures | Level 4 — PRODUCTION (same name; "Validated policy; monitoring and rollback in place") |
| PHASE 5 — GOVERNANCE | Establish token budgets, cost thresholds, quality SLOs; review optimization drift | No direct Level equivalent — an orthogonal governance activity, not itself a maturity gate |
| PHASE 6 — CONTINUOUS OPTIMIZATION | Re-run benchmark after model/provider changes; re-evaluate routing/compression/cache/templates; detect regressions | Level 5 — AUTO-TUNED ("Continuous evidence-based adjustment; governance approval; automatic regression rollback") |

`SOURCE-DERIVED` clarification: `architecture.md`'s PHASE 1 "shadow mode" (pure baseline recording) is broader than `EL-001`'s "Shadow Optimization" module proper (which additionally simulates the optimized path) — `EL-001` most naturally begins within PHASE 1 and continues into PHASE 2, since `conventions.md` §20.3 places offline benchmarking (Level 1) before production-like shadow traffic (Level 2). This is a terminology-scope nuance, not a normative conflict between the two sources — both describe the same underlying progression; neither Level 2 (SHADOW) nor `EL-001` is contradicted by PHASE 2 preceding full shadow-optimization maturity in the numbered levels.

**PHASE 5 GOVERNANCE detail (`SOURCE-DEFINED`, `architecture.md` §34):** token budgets, cost thresholds (`security.md`'s SGE, cited not redefined, is the enforcement mechanism), quality SLOs (`quality-gates.md`, cited not redefined), and optimization-drift review (`EL-005`, §11).

## 36. Evaluation Gates

`PROPOSED METHODOLOGY` taxonomy (not a redefinition of `QG-NNN`, which remains `quality-gates.md`'s): a promotion decision at any point in §7–§11 or §35 should be able to answer, in order — Data Validity (§14–§15) → Baseline Validity (§15) → Treatment Validity (§15) → Evaluator Validity (§27–§28) → Quality Evidence (§21, cited) → Economic Evidence (§22) → Latency Evidence (§23) → Reliability Evidence (§25, `task_success`) → Statistical Evidence (§17–§20) → Regression Evidence (§33–§34) → Rollout Evidence (§35, if applicable). Each gate's underlying requirement is drawn from a section elaborated above; this taxonomy is an organizing checklist, not a new normative requirement.

## 37. Failure and Rollback Matrix

| Failure | Detection | Evidence Impact | Evaluation Impact | Recovery/Disposition |
|---|---|---|---|---|
| Missing baseline run | `EvaluationRun` absent for `run_type=BASELINE` | Comparison cannot be constructed | `EvaluationComparison` withheld | Re-run baseline; production request itself is unaffected (fail-open on the request, fail-closed on the *evaluation verdict* — the technique cannot be counted as validated until re-run) |
| Missing treatment run | Same, for `OPTIMIZED`/`SHADOW` | Same | Same | Same disposition |
| Invalid/uncalibrated evaluator | `calibrated = false` or missing `CalibrationMetadata` (`quality-gates.md` §10, cited) | `quality_delta` treated as low-confidence | Comparison flagged `INVESTIGATE`, not `APPROVE` | Escalate for evaluator recalibration before promotion |
| Stale benchmark corpus | `EC-072` divergence alert (§14) | Benchmark results flagged potentially unrepresentative | Pending promotions on that corpus paused | Refresh corpus; use `EL-001` shadow traffic as interim validation (`EC-072` fallback) |
| Model/provider mismatch between baseline and treatment | §15 confound check | Comparison invalid — confounded | Comparison discarded, not silently reported | Re-run with matched model/provider identity |
| Cache-state contamination | §30 | Cache-attributed savings unreliable | `cache_hit_rate`/`tokens_avoided_by_cache` flagged | Isolate cache state per experiment; re-run if already contaminated |
| Insufficient sample size | §18 power check fails | Result underpowered | Promotion withheld, not force-approved | Continue accumulating evidence |
| Quality regression detected | `EC-071`/`quality-gates.md`, cited | `quality_delta` negative beyond bound | `EL-005` fires; `EL-002` automatic rollback | Disable technique; use fallback path (`EC-071`) |
| Cost regression detected | `net_savings` trends negative | Net-value evidence reversed | Same as above | Same as above |
| Latency regression detected | `latency_delta_ms` trends negative | Latency evidence reversed | Flagged for `EL-005` | Escalate; rollback if it breaches the technique's own SLO (`quality-gates.md`, cited) |
| Workload/benchmark drift | `EC-072` | Representativeness in question | Results held pending refresh | Refresh corpus (§14) |
| Evaluator drift | `quality-gates.md` §10.D `DriftEvent` (cited, extends `EL-005`) | Verifier acceptance behavior suspect | Flagged, not silently trusted | Recalibrate (§28) |
| P5 contamination | §26 | Layer-1/2 and P5 effects conflated | Report rejected as ambiguous | Re-decompose before reporting |
| Concurrent experiment interference | `SCN-CMP-051/052/056/057` | Comparison pair confounded | Segmented analysis required (§34) | Segment by group; do not merge |

No mandatory recovery behavior is invented beyond what §7–§11, §33, and root `CLAUDE.md` rule 2 (fail-open on optimization, fail-closed on security) already establish, applied here as: an evaluation-pipeline failure never blocks the underlying production request, but it does prevent that evaluation's conclusion from being counted as validated evidence until resolved.

---

## 38. Control-Plane Evaluation Algorithms

`PROPOSED METHODOLOGY` unless a step cites a specific source. Lettered per this chain's established convention.

**A — Baseline Selection.** Given a request, select the most recent `EvaluationRun` with `run_type = BASELINE` matching the request's workload/model/provider/policy-version signature (§15); if none exists, execute a fresh baseline run before proceeding.

**B — Treatment Construction.** Apply the candidate technique's plan to the same request signature used for the selected baseline (`A`); execute under `run_type = SHADOW` (`EL-001`) or `OPTIMIZED` (`EL-003`/production) as appropriate to the calling context.

**C — Benchmark Corpus Selection.** Select corpus items matching the target workload's required corpus type (§13.1); verify corpus freshness (`K`, below) before use; exclude the held-out validation slice (§14) from threshold-tuning use.

**D — Experiment Validity Check.** Before accepting any `EvaluationComparison` as evidence: confirm no confound is present (§15); confirm evaluator calibration status (§27–§28); confirm sample size clears the power threshold appropriate to the claimed effect size (§18). Reject the comparison as evidence (not as a request failure) if any check fails.

**E — Statistical Comparison.** Compute the effect estimate and confidence interval (§19) for the target metric(s); classify statistical vs. practical significance (§20) independently; do not conflate the two in the output `EvaluationComparison.recommendation`.

**F — Net-Value Evaluation.** Compute `gross_savings`, `optimizer_overhead`, `net_savings`, `net_savings_pct` (§22, `SOURCE-DEFINED` fields); reject any evaluation that reports token/context reduction without also reporting overhead-adjusted net value.

**G — Quality Evidence Aggregation.** Aggregate `quality_delta`/verifier verdicts (`quality-gates.md`, cited) across all comparisons contributing to a promotion decision; a single favorable comparison never overrides an aggregate unfavorable trend.

**H — Regression Attribution.** Per §34: segment by independently-varying dimension (A/B group, model-version epoch, pricing-change epoch) before attributing cause; if segmentation cannot isolate a cause, report as `UNATTRIBUTED` rather than guessing.

**I — Rollout Promotion Decision.** Advance a technique to its next `EL-002` percentage step only when: (1) no active `EL-005` rollback trigger; (2) minimum dwell time/sample count at the current step is met (§8); (3) `EvaluationComparison.recommendation != REJECT`.

**J — Rollback Decision.** Trigger automatic rollback when `EL-005` reports `regression_detected = true` with `regression_dimensions` including quality or net-value degradation beyond the technique's own validated baseline (`EC-071`); manual rollback always available independent of this check (§8).

**K — Benchmark Corpus Freshness Check.** Compare `benchmark.corpus_age_days` and distribution-divergence signal against the refresh policy (§14, `EC-072`); flag results as potentially unrepresentative rather than silently trusting a stale corpus.

**L — Evaluation Evidence Integrity Check.** Before any evaluation result is used to justify a promotion/rollback/governance decision, confirm its evidence-integrity label (§32) is `VERIFIED`/`OBSERVED`/`BENCHMARK-DERIVED`/`PRODUCTION-DERIVED` as appropriate to the decision's stakes; an `ESTIMATED`/`SIMULATED`/`UNVERIFIED` result may inform but never alone justify a PRODUCTION-phase promotion.

---

## 39. Scenario Coverage

**No dedicated evaluation/benchmarking/experimentation scenario domain exists** among `scenario-matrix.md`'s 30 domains (A–AD) — independently verified by enumerating all 30 domain headers directly; none is named for evaluation, benchmarking, experimentation, or regression. All validating evidence for the `EL-NNN` modules exists as compound scenarios within Domain AD.

| `EL-NNN` | Validating Scenario(s) | Status |
|---|---|---|
| `EL-001` — Shadow Optimization | `SCN-CMP-051` | DIRECTLY COVERED |
| `EL-002` — Controlled Rollout | — | `NO DEDICATED SCENARIO` (see `SOURCE-GAP-EVAL-05`) |
| `EL-003` — A/B Testing | `SCN-CMP-052` | DIRECTLY COVERED |
| `EL-004` — Continuous Policy Learning | `SCN-CMP-057` | DIRECTLY COVERED |
| `EL-005` — Regression Detector | `SCN-CMP-056`, `EC-071` | DIRECTLY COVERED |

**Correction to this document's own generation prompt:** the candidate scenario list the generation prompt supplied as a working hypothesis (`SCN-CMP-028/051/052/056`) included `SCN-CMP-028` ("Workflow × Permission × Context: Step Reordering Exposes a Step to Context It Was Never Authorized to See"). Verified directly: this scenario is about workflow-reordering security (a permission/context-admission concern), not experimentation/evaluation — its `Source Requirements` field cites this document's own Process step 4 for Workflow×Permission/Workflow×Context, not any `EL-NNN` module. It is **not** relevant to `EL-NNN` coverage and is excluded from the table above. Independent verification also found `SCN-CMP-057`, which the prompt's hypothesis list did not include.

## 40. Edge-Case Coverage

| `EC-NNN` | Title | Validating Scenario | Status |
|---|---|---|---|
| `EC-071` | Optimization Previously Producing Savings Stops Producing Net Savings | `SCN-CMP-056` (segmentation logic), full behavior elaborated §11/§33 | DIRECTLY COVERED |
| `EC-072` | Benchmark Corpus Becomes Unrepresentative of Production Traffic | No dedicated scenario found; behavior fully elaborated §14 from the edge-case text itself | `PARTIALLY COVERED` — the edge case's detection/fallback behavior is elaborated in full, but no scenario-matrix entry exercises it directly; recorded honestly rather than upgraded |

## 41. Cross-Document Boundary Matrix

| Concern | Source/Owner | `eval.md` Treatment |
|---|---|---|
| `EL-001`–`005` modules | `architecture.md` §21 | Elaborated (§7–§12) |
| Benchmarking Framework | `architecture.md` §32 | Elaborated (§13–§16) |
| Rollout phases | `architecture.md` §34 | Elaborated + reconciled with `conventions.md` §20.3 (§35) |
| Regression Testing | `architecture.md` §35 | Elaborated (§33–§34) |
| Evaluation Interface | `interfaces.md` §18 (`INTF-030`) | Operationally consumed (§5, §16) |
| `RegressionReport` schema | `interfaces.md` §18 | Gap recorded, not defined (`SOURCE-GAP-EVAL-01`, §43) |
| Quality methodology / VCL | `quality-gates.md` | Cited, not redefined (§21, §27–§28) |
| Security approval (HAG/`GSP-004`) | `security.md` §10 | Cited, not redefined (§10) |
| Observability / telemetry | `observability.md` | Consumed, not redefined (§5, §32) |
| Cache mechanics | `cache-strategy.md` | Consumed, not redefined (§24) |
| Agent mechanics | `agent-optimization.md` | Consumed, not redefined (§25) |
| P5 / inference-serving | `inference-optimization.md` | Separated, not merged (§26) |
| Provider facts | `provider-matrix.md` | Consumed where cited |
| Capacity/throughput | `SCALING.md` (not yet generated) | Deferred |
| Implementation sequencing | `implementation-plan.md` (not yet generated) | Deferred |

**Deferral audit (every literal `eval.md` reference found across the chain, confirmed addressed):** `agent-optimization.md` (Evaluation framework — §5/§16 of this document), `inference-optimization.md` (Evaluation — §26), `optimization-catalog.md` (benchmark-corpus/A-B statistical methodology — §13–§20), `security.md` (benchmark-corpus/A-B statistical methodology including any future CIS-classifier benchmark — §13–§20, the CIS-classifier-specific instance remains `security.md`'s to specify when it arises), `observability.md` (benchmark/evaluation methodology, and its §36's baseline-context-for-dashboards deferral — §14–§16, §22), `quality-gates.md` (benchmark-corpus/A-B statistical methodology, `SOURCE-GAP-QUALGATE-01`/`02` — §13–§20, §28), `architecture.md` §1.6's own doc-boundary row ("Evaluation methodology beneath Section 32" — this entire document).

## 42. Requirement / Traceability Matrix

**`EL` Catalog:**

| `EL` ID | Name | Source | Status |
|---|---|---|---|
| `EL-001` | Shadow Optimization | `architecture.md` §21.1 | Elaborated |
| `EL-002` | Controlled Rollout | `architecture.md` §21.2 / `conventions.md` §20.4 | Elaborated |
| `EL-003` | Optimization A/B Testing | `architecture.md` §21.3 | Elaborated |
| `EL-004` | Continuous Policy Learning | `architecture.md` §21.4 / `conventions.md` §20.5 | Elaborated |
| `EL-005` | Optimization Regression Detector | `architecture.md` §21.5 | Elaborated |

**`EL` × `AC`/`OBJ`:**

| `EL` | `AC`/`OBJ` |
|---|---|
| `EL-001`, `EL-002` | `AC-029` |
| `EL-003` | `AC-030` |
| `EL-005` | `AC-031` |
| All | `OBJ-012` (org-specific benchmark evidence), `AC-015` (independently measurable), `AC-016` (fallback required), `AC-017` (own-workload trade-off), `AC-018` (no source percentage as production guarantee) |
| `EL-004` (cost/net-value evidence it produces) | `OBJ-011` (auditable token/cost ledger — `EvaluationRun`'s cost fields feed this) |

**Entry count summary:** 5 `EL-NNN` entries fully elaborated; 12 Control-Plane algorithms (A–L); 2 edge cases addressed: `EC-071` DIRECTLY COVERED; `EC-072` PARTIALLY COVERED. 4 scenarios used for direct/compound validation (`SCN-CMP-051/052/056/057`); 1 additional scenario (`SCN-CMP-028`) independently verified but excluded as not relevant. 5 source gaps recorded; 0 contradictions.

## 43. Source Gaps

- **`SOURCE-GAP-EVAL-01`** — **`RegressionReport` schema undefined.** `interfaces.md` §18's `EvaluationFramework.report_regression()` (`INTF-030`) returns `RegressionReport`, but this type is not among the section's 3 defined schemas (verified directly). *Why it matters:* implementers have no canonical shape for a regression report. *Disposition:* not silently defined as authoritative here. A conceptual sketch of what it would need — `comparison_id`, `regression_dimensions`, `severity`, `attributed_cause` (§34), `confidence`, `recommended_action` — is offered as `PROPOSED METHODOLOGY` only; the actual interface schema remains `interfaces.md`'s to define. *Blocking:* No.
- **`SOURCE-GAP-EVAL-02`** — **No source-defined sample-size/power methodology.** §18's guidance is entirely `PROPOSED METHODOLOGY`; no source specifies parameters. *Blocking:* No — the conceptual methodology is supplied; only the exact numeric defaults are unvalidated.
- **`SOURCE-GAP-EVAL-03`** — **No source-defined statistical-significance/confidence-level default.** §19's 95% default is `PROPOSED`. *Blocking:* No.
- **`SOURCE-GAP-EVAL-04`** — **No source-defined benchmark-corpus refresh cadence beyond `EC-072`'s own illustrative example.** §14's monthly default is `PROPOSED`, anchored to but not identical with `EC-072`'s "(e.g., monthly)" phrasing. *Blocking:* No.
- **`SOURCE-GAP-EVAL-05`** — **`EL-002`'s percentage-rollout progression has no dedicated validating scenario.** `AC-029` traces only to `SCN-CMP-051`, which validates `EL-001`'s shadow/policy interaction, not the 1%→5%→25%→50%→100% progression mechanic itself. *Disposition:* recorded as `NO DEDICATED SCENARIO` in §39, not force-fit to `SCN-CMP-051`. *Blocking:* No.
- **Carried-forward disposition of `SOURCE-GAP-QUALGATE-01`/`02`:** narrowed by this document's `PROPOSED DEFAULT` values (§28) but **not closed** — `quality-gates.md` itself was not modified by this pass, consistent with root `CLAUDE.md`'s resolution discipline.

## 44. Source Contradictions

None found. `architecture.md` §34's rollout phases and `conventions.md` §20.3's maturity levels initially appeared potentially misaligned in ordering (§35) but were reconciled as a terminology-scope difference, not a normative conflict, upon direct verification of both sources' exact text.

**Source Contradictions: 0**

---

## Final Report

- **Document ID:** EAIOC-EVAL-001, Version 1.0.0, Status PRE-IMPLEMENTATION — Level 0 (RESEARCH).
- **`EL` entries:** 5 of 5 (`EL-001`–`EL-005`), fully elaborated at algorithm/decision-logic level, not restated stubs.
- **Control-Plane algorithms:** 12 (A–L).
- **Benchmarking/rollout/regression coverage:** §13–§16 (benchmarking), §35 (rollout, reconciled against two real sources), §33–§34 (regression testing/attribution).
- **Statistical methodology coverage:** §17–§20, fully addressing every upstream `eval.md` deferral for sample size, confidence, significance, and practical-vs-statistical distinction.
- **Scenario coverage:** 4 of 5 `EL` modules directly validated by a real compound scenario (`SCN-CMP-051/052/056/057`); `EL-002` honestly recorded as `NO DEDICATED SCENARIO`. `SCN-CMP-028` verified but excluded as not relevant, correcting this document's own generation prompt's hypothesis.
- **Edge-case coverage:** `EC-071` DIRECTLY COVERED; `EC-072` PARTIALLY COVERED (no dedicated scenario, but full behavioral elaboration).
- **Requirement coverage:** `AC-015`–`018`, `AC-029`–`031`, `OBJ-011`, `OBJ-012` all traced (§42).
- **Source-gap count:** 5 (`SOURCE-GAP-EVAL-01`–`05`), all non-blocking, plus the carried-forward, still-open disposition of `SOURCE-GAP-QUALGATE-01`/`02`.
- **Contradiction count:** 0.
- **`PROPOSED METHODOLOGY` count:** 8 major procedures (corpus construction, sample-size/power reasoning, confidence-reporting convention, practical-significance margin, regression-attribution segmentation, reproducibility checklist, evidence-integrity taxonomy, cross-EL lifecycle diagram).
- **`PROPOSED DEFAULT — VALIDATE LOCALLY` count:** 7 concrete numbers (shadow-evidence promotion volume [illustrative only], rollout dwell-time/sample-count, 95% confidence level, practical-significance margin, monthly corpus refresh, 85% calibration accuracy bar, 90-day recalibration ceiling).
- **Unresolved limitations:** `RegressionReport`'s actual schema remains undefined pending an `interfaces.md` update; `EL-002` lacks dedicated scenario evidence; all statistical defaults are starting points, not validated production thresholds.
- **`RegressionReport` status:** Confirmed still referenced but undefined; `SOURCE-GAP-EVAL-01` recorded; conceptual sketch offered, not asserted as authoritative.
- **`SOURCE-GAP-QUALGATE-01`/`02` status:** Narrowed with `PROPOSED DEFAULT` values; not closed.
- **File-scope confirmation:** Only `docs/eval.md` was created. No upstream document (`problemStatement.txt`, Engineering Spec, `architecture.md`, `interfaces.md`, `conventions.md`, `edge-cases.md`, `scenario-matrix.md`) was modified. No sibling downstream document (`optimization-catalog.md`, `provider-matrix.md`, `cache-strategy.md`, `agent-optimization.md`, `inference-optimization.md`, `quality-gates.md`, `security.md`, `observability.md`) was modified. `SCALING.md`, `implementation-plan.md`, `requirements-traceability.md`, and ADRs were not generated or cascaded into.
- **Next document:** `SCALING.md` (not generated here).

## Eval Readiness

This document elaborates all 5 `EL-NNN` modules at full algorithm depth, supplies the statistical/benchmarking methodology every upstream document in this chain explicitly deferred here, reconciles two real sources' rollout-phase language without inventing a false contradiction, corrects its own generation prompt's scenario hypothesis through independent verification (finding `SCN-CMP-057`, excluding `SCN-CMP-028`), and records every gap and limitation honestly without closing anything it did not actually resolve. No blocking issue was found.

READY FOR NEXT DOCUMENTATION PHASE
