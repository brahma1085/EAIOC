# `EXE-P0.2.A` — Capability 2 (Baseline Benchmark Harness + Quality Evaluation), Sub-phase A: Contract & Design

Per `docs/execution-plan.md` §18.5's Sub-phase A row: "Cite `EvaluationFramework`'s three defined schemas (`INTF-030`) and `quality-gates.md`'s methodology as-is; this capability is the first *consumer* of both, not a redefinition." Design note only — no source code in this commit, matching `EXE-P0.1.A`'s own precedent.

## 1. `INTF-030 EvaluationFramework` — cited verbatim from `interfaces.md` §18

```
interface EvaluationFramework {
  run_baseline(request: ControlPlaneRequest)                              -> EvaluationRun
  run_optimized(request: ControlPlaneRequest, plan: OptimizationPlan)    -> EvaluationRun
  compare(baseline: EvaluationRun, optimized: EvaluationRun)             -> EvaluationComparison
  report_regression(comparison: EvaluationComparison)                    -> RegressionReport
}

EvaluationRun {
  run_id:      string
  request_id:  string
  run_type:    enum { BASELINE, OPTIMIZED, SHADOW }
  timestamp:   ISO8601 string

  baseline_cost:      float
  optimized_cost:     float | null
  gross_savings:      float
  optimizer_overhead: float
  net_savings:        float
  net_savings_pct:    float

  quality_delta:           float
  quality_score_baseline:  float
  quality_score_optimized: float | null

  latency_delta_ms:     integer
  latency_ms_baseline:  integer
  latency_ms_optimized: integer | null

  cache_hit_rate:          float
  tokens_avoided_by_cache: integer

  task_success_baseline:   boolean
  task_success_optimized:  boolean | null

  regression_detected:   boolean
  regression_dimensions: string[]
}

EvaluationComparison {
  comparison_id:      string
  baseline_run_id:    string
  optimized_run_id:   string
  net_savings:        float
  quality_delta:      float
  latency_delta_ms:   integer
  task_success_delta: float
  recommendation:     enum { APPROVE, REJECT, INVESTIGATE }
  rejection_reasons:  string[]
}
```

**"3 schemas" reconciled.** `interfaces.md` §18 catalogs `INTF-030` as "3 schemas" (§18's own cross-reference table, line 3068). Verified directly against the live section text: the three are the `EvaluationFramework` interface itself, `EvaluationRun`, and `EvaluationComparison`. `RegressionReport` is referenced only as `report_regression()`'s return type — it is **not** one of the three defined schemas in this section, and no body for it exists anywhere in `interfaces.md`. This matches `eval.md` §15's own independent finding (line 81: "`RegressionReport` is referenced by `report_regression()`'s return type but is not one of the 3 schemas actually defined in that section"), which disposes it as `SOURCE-GAP-EVAL-01`.

**Disposition for this capability's P0 scope:** `SOURCE-GAP-EVAL-01` (the undefined `RegressionReport` schema) is explicitly **not resolved here**, per `docs/execution-plan.md` §18.5's own Sub-phase A Definition of Done ("`RegressionReport`'s undefined status ... explicitly not resolved here — out of scope"). `report_regression()` is therefore out of this capability's P0 build scope entirely — only `run_baseline()`, `run_optimized()` (explicit no-op per Sub-phase B), and `compare()` (used by later capabilities' quality gates, not built here) are in scope for the six atomic units that follow. No placeholder `RegressionReport` type is invented to fill the gap; inventing one would violate root `CLAUDE.md`'s "source gaps are tracked, not resolved by inventing content" rule.

## 2. `quality-gates.md` methodology — cited, not redefined

This capability is the **first consumer** of `quality-gates.md`'s methodology, not a redefinition of it. Cited as-is, without re-deriving:

- §4 **Quality Dimensions** — the 10 dimensions (Accuracy, Task completion, Retrieval quality, Factuality, Schema compliance, Semantic equivalence, Safety, User satisfaction, Latency, Cost) that the `QG-001`–`QG-010` series (§5) elaborates one-to-one.
- §5 **`QG-001`–`QG-010`: Canonical Quality Gate Entries** — the concrete per-dimension threshold/methodology entries a future P1+ technique's validation-matrix gate will call into. None of the 10 entries are re-specified, narrowed, or extended by this design note.
- §6 **Quality Decision Model and Control-Plane Algorithms** — the general flow (receive applicable `QG-NNN` set → resolve validator/verifier → compare score against threshold → select fallback action) that this capability's harness exists to eventually be called by, once a P1+ technique exists to validate.

Per `docs/execution-plan.md` §18.5's Sub-phase F row (restated from its own table): at P0 scope, **no `QG-NNN` gate actually runs** — there is no optimized-path technique yet to evaluate against a baseline. This capability builds the substrate (`run_baseline()` recording real `EvaluationRun` fields; `run_optimized()` as an explicit no-op) that a P1+ technique's validation call will later invoke `compare()` against.

## 3. Package placement

Per `docs/execution-plan.md` §18.5's "Package/module responsibility" field: `control_plane/benchmarking/` + `control_plane/evaluation/` (§9, direct match — two distinct `architecture.md` §36 P0 items sharing one implementation foundation, per `implementation-plan.md` §8.2's own rationale). This design note lives under `control_plane/evaluation/`, matching the Files/Modules field of the Sub-phase A row itself. Sub-phase B (Minimal Implementation) is where both `control_plane/benchmarking/` and `control_plane/evaluation/` Java packages will actually be scaffolded under `src/main/java/com/eaioc/controlplane/` — not performed in this commit, consistent with `EXE-P0.1.A`'s own precedent of deferring Maven/`src/` scaffolding to Sub-phase B.

## 4. No competing schema introduced

Every field above is quoted verbatim from `interfaces.md` §18. No new field, no renamed field, and no schema shape beyond what `EvaluationRun`/`EvaluationComparison` already define is proposed. `quality-gates.md`'s `QG-NNN` catalog and Control-Plane algorithms are likewise cited, not modified.
