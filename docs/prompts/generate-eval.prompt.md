# V3 MASTER PROMPT — Generate `docs/eval.md`

## 0. ROLE

You are the **EAIOC Principal Evaluation Architect, ML/LLM Evaluation Engineer, Experimentation Architect, Statistical Methodology Reviewer, Benchmarking Architect, and Documentation Consistency Auditor**.

You are generating:

```text
docs/eval.md
```

for:

> **Enterprise Agent & LLM Inference Optimization Control Plane (EAIOC)**

This is a **documentation-generation task only**.

Do NOT implement application code.

Do NOT modify upstream documents.

Do NOT generate `SCALING.md`, `implementation-plan.md`, `requirements-traceability.md`, ADRs, or any other downstream document.

Work surgically and preserve the established EAIOC documentation chain.

---

# 1. MISSION

Generate `docs/eval.md` as the canonical elaboration of the EAIOC:

### Optimization Experimentation and Learning modules

```text
EL-001 — Shadow Optimization
EL-002 — Controlled Rollout
EL-003 — A/B Testing
EL-004 — Continuous Policy Learning
EL-005 — Regression Detector
```

and the operational evaluation methodology beneath:

```text
architecture.md §32 — Benchmarking Framework
architecture.md §34 — Rollout Strategy
architecture.md §35 — Regression Testing
```

The document must provide the concrete methodology required by future implementers to establish:

* organization-specific benchmarking
* optimization experiments
* baseline/treatment comparison
* shadow evaluation
* controlled rollout
* A/B testing
* statistical analysis
* quality evaluation integration
* cost evaluation
* latency evaluation
* cache-impact evaluation
* agent evaluation
* regression detection
* model/provider update revalidation
* benchmark corpus maintenance
* policy-learning evidence
* experiment reproducibility
* evidence integrity
* rollout gates
* rollback criteria
* evaluation reporting

The fundamental question is:

> **Did the optimization actually improve the system, under the intended workload, relative to a valid baseline, after accounting for quality, cost, optimization overhead, latency, reliability, and uncertainty?**

The document must explicitly prevent the invalid conclusion:

> **Token reduction alone proves optimization success.**

---

# 2. DOCUMENT POSITION IN THE CHAIN

The authoritative documentation chain is:

```text
problemStatement
    ↓
Engineering_Spec
    ↓
architecture
    ↓
interfaces
    ↓
conventions
    ↓
edge-cases
    ↓
scenario-matrix
    ↓
optimization-catalog
    ↓
provider-matrix
    ↓
cache-strategy
    ↓
agent-optimization
    ↓
inference-optimization
    ↓
quality-gates
    ↓
security
    ↓
observability
    ↓
eval                 ← GENERATE THIS DOCUMENT
    ↓
SCALING.md           ← FUTURE — DO NOT GENERATE
    ↓
implementation-plan
    ↓
requirements-traceability
    ↓
ADRs
```

This is the **ninth generated downstream document**.

`observability.md` is the immediate predecessor.

`SCALING.md` is the next document.

Do not cascade into it.

---

# 3. DOCUMENT METADATA

Generate the header using:

```text
Document ID: EAIOC-EVAL-001
Status: PRE-IMPLEMENTATION — Level 0 (RESEARCH)
Version: 1.0.0
```

Include:

* document position in chain
* generation prompt
* sources read
* scope boundary
* status/maturity

Use the same documentation style established by the previous generated documents.

---

# 4. AUTHORITATIVE SOURCE HIERARCHY

Read the actual current repository files.

Do not rely on this prompt's characterization of any source.

## Priority 1 — Problem Statement

Read:

```text
Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt
```

Identify evaluation-relevant:

* objectives
* acceptance criteria
* quality requirements
* optimization economics
* evidence requirements
* hardening requirements
* measurement requirements

Especially verify relevant:

```text
OBJ-*
AC-*
H01–H20
PS §51
PS §52
```

Only cite IDs that actually exist.

---

## Priority 2 — Engineering Specification

Read:

```text
Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md
```

Identify:

* evaluation requirements
* benchmark requirements
* quality validation
* optimization validation
* cost model
* maturity
* rollout
* evidence
* evaluation interfaces

---

## Priority 3 — Architecture

Read the **current**:

```text
architecture.md
```

Start by explicitly grepping:

```text
## 21.
## 32.
## 33.
## 34.
## 35.
```

These sections are the primary source for this document.

### §21

Verify exact current definitions of:

```text
EL-001
EL-002
EL-003
EL-004
EL-005
```

### §32

Verify:

* benchmarking framework
* corpus types
* per-request comparison requirements
* experimental validation matrix
* all 20 techniques if still present
* exact wording
* acceptance criteria

### §33

Cite Mode F / CI/CD/GitHub Action evaluation integration.

Do not re-own deployment architecture.

### §34

Verify the exact six rollout phases:

```text
OBSERVE
BENCHMARK
CONTROLLED PILOT
PRODUCTION
GOVERNANCE
CONTINUOUS OPTIMIZATION
```

Do not invent additional phases.

### §35

Verify regression-testing requirements.

Also verify relevant:

* OBJ-012
* AC-015
* AC-016
* AC-017
* AC-029
* AC-030
* AC-031

Do not trust IDs stated in this prompt without independently verifying them.

---

# 5. INTERFACES

Read:

```text
interfaces.md
```

Especially §18:

> Evaluation Interface

Verify the exact current interface IDs from the file itself.

Inspect:

```text
EvaluationFramework.run_baseline()
EvaluationFramework.run_optimized()
EvaluationFramework.compare()
EvaluationFramework.report_regression()
EvaluationRun
EvaluationComparison
RegressionReport
```

## IMPORTANT SOURCE GAP

Verify whether `RegressionReport` is referenced but not actually defined.

If it remains undefined:

```text
SOURCE-GAP-EVAL-NN
```

must be created.

Do NOT silently invent a canonical interface schema.

You may provide a:

```text
PROPOSED METHODOLOGY
```

for what information a regression report would conceptually require, but clearly state that the actual interface schema remains owned by `interfaces.md`.

---

# 6. CONVENTIONS

Read:

```text
conventions.md
```

Consume:

* evaluation conventions
* benchmark conventions
* baseline/optimized conventions
* production rollout conventions
* maturity conventions
* quality conventions
* observability conventions
* audit conventions

Do not redefine conventions.

---

# 7. EDGE CASES

Read:

```text
edge-cases.md
```

Directly verify:

```text
EC-071
EC-072
```

Read each in full.

At minimum cover:

### EC-071

Optimization stops producing net savings after a model/provider update.

### EC-072

Benchmark corpus becomes unrepresentative of production traffic.

Do not trust the descriptions above until verified against the current file.

Also search for other evaluation-relevant edge cases.

---

# 8. SCENARIO MATRIX

Read:

```text
scenario-matrix.md
```

Search thoroughly for evaluation/benchmarking/experimentation coverage.

Do NOT assume a dedicated evaluation domain exists or does not exist.

The prompt's current hypothesis is that a dedicated domain may not literally be named "evaluation", but this must be independently verified.

Specifically verify whether the following actually exist and actually mean what is claimed:

```text
SCN-CMP-028
SCN-CMP-051
SCN-CMP-052
SCN-CMP-056
```

Potential relevance includes:

* shadow optimization vs live policy change
* A/B tenant-composition drift
* regression detector vs concurrent A/B test
* other compound comparison scenarios

If a dedicated evaluation domain exists under a different name, use the actual domain.

If no dedicated evaluation domain exists, state:

```text
NO DEDICATED EVALUATION SCENARIO DOMAIN FOUND
```

and use only verified compound scenarios.

Never fabricate:

```text
SCN-EVAL-*
SCN-BENCH-*
SCN-AB-*
```

---

# 9. QUALITY GATES

Read:

```text
quality-gates.md
```

Especially:

```text
§10 — Verifier Calibration Procedure
SOURCE-GAP-QUALGATE-01
SOURCE-GAP-QUALGATE-02
```

Verify the current wording.

The quality-gates document explicitly defers relevant benchmark/A-B statistical methodology to `eval.md`.

This document must address those deferred responsibilities.

## IMPORTANT BOUNDARY

`quality-gates.md` owns:

* quality methodology
* quality dimensions
* quality gate definitions
* VCL calibration mechanism

`eval.md` owns:

* experimental methodology
* benchmark methodology
* statistical methodology
* sample-size methodology
* A/B methodology
* evaluation evidence

Do NOT redefine VCL.

Do NOT redefine quality dimensions.

---

# 10. SEARCH ALL `eval.md` DEFERRALS

Before writing the document, search these files for the literal string:

```text
eval.md
```

At minimum:

```text
quality-gates.md
optimization-catalog.md
security.md
observability.md
```

Identify every explicit statement that delegates responsibility to `eval.md`.

Create an internal checklist.

Every such delegation must either:

1. be addressed in this document, or
2. be explicitly recorded as an unresolved source gap.

Do not leave upstream deferrals dangling.

---

# 11. SECURITY

Read:

```text
security.md
```

Use only what is required for evaluation.

Especially cite:

```text
GSP-004 / Human Approval Gate
```

for `EL-004`.

Do NOT redefine GSP mechanisms.

---

# 12. OBSERVABILITY

Read:

```text
observability.md
```

This is the immediate upstream document.

It owns:

* telemetry pipeline
* metrics
* logs
* tracing
* dashboards
* SLO/SLI
* alerting
* telemetry governance
* measurement integrity

`eval.md` consumes those measurements.

Do NOT redefine:

* metric schemas
* telemetry schemas
* dashboard architecture
* observability pipeline
* observability SLOs

Instead explain:

> how evaluation uses observability evidence.

---

# 13. OTHER UPSTREAM/DOWNSTREAM SOURCES

Read and consume, without redefining:

### `optimization-catalog.md`

Use:

* evaluation methodology
* expected benefits
* quality risks
* overhead
* maturity
* optimization-specific metrics

### `provider-matrix.md`

Use:

* provider/model behavior
* provider-specific capabilities
* pricing
* provider limitations

Do not generalize provider-specific results.

### `cache-strategy.md`

Use:

* cache evaluation
* cache quality impact
* cache savings distinction

Keep provider-native cache savings separate.

### `agent-optimization.md`

Use:

* agent iteration
* tool calls
* early exit
* verifier behavior
* sub-agent behavior

### `inference-optimization.md`

Use:

```text
inference_serving.*
```

and preserve P5 separation.

### `CLAUDE.md`

Read the full root file.

It is authoritative for:

* anti-fabrication
* source hierarchy
* ID conventions
* tenant isolation
* fail-open/fail-closed rules
* documentation process
* readiness gating

---

# 14. CORE EVALUATION INVARIANT

Use:

> **An optimization is successful only when its measured benefit is evaluated against its cost, latency, quality impact, reliability, fallback behavior, workload applicability, and statistical/evidentiary uncertainty.**

Also preserve the EAIOC-wide invariant:

> **Every optimization decision must be measurable, and every governed decision must produce a retrievable explanation.**

---

# 15. REQUIRED SCOPE

`eval.md` owns:

| Concern                              | Owner     |
| ------------------------------------ | --------- |
| EL-001–EL-005 algorithms             | `eval.md` |
| Benchmark corpus methodology         | `eval.md` |
| Benchmark execution methodology      | `eval.md` |
| Baseline/treatment comparison        | `eval.md` |
| A/B statistical methodology          | `eval.md` |
| Experiment design                    | `eval.md` |
| Sample-size methodology              | `eval.md` |
| Confidence/uncertainty methodology   | `eval.md` |
| Regression evaluation methodology    | `eval.md` |
| Rollout evidence/gating methodology  | `eval.md` |
| Evaluation-run operational mechanics | `eval.md` |
| Evaluation evidence integrity        | `eval.md` |

Do NOT own:

| Concern                      | Owner                                |
| ---------------------------- | ------------------------------------ |
| Quality-gate definitions     | `quality-gates.md`                   |
| VCL mechanism                | `quality-gates.md`                   |
| Security/GSP mechanisms      | `security.md`                        |
| Telemetry implementation     | `observability.md`                   |
| Metric schema definitions    | `interfaces.md` / `observability.md` |
| Cache mechanics              | `cache-strategy.md`                  |
| Agent optimization mechanics | `agent-optimization.md`              |
| Inference-serving mechanics  | `inference-optimization.md`          |
| Provider facts               | `provider-matrix.md`                 |
| Capacity/throughput          | `SCALING.md` — future                |
| Implementation sequencing    | `implementation-plan.md` — future    |

---

# 16. SOURCE LABEL DISCIPLINE

Every methodological statement must be classified where appropriate as:

### `SOURCE-DEFINED`

Explicitly stated in an authoritative source.

### `SOURCE-DERIVED`

Directly derived from multiple source requirements.

### `PROPOSED METHODOLOGY`

A methodology introduced because the sources do not specify one.

### `PROPOSED DEFAULT — VALIDATE LOCALLY`

A concrete number/default introduced by this document.

### `DERIVED METRIC`

A calculated metric based on canonical source metrics.

Do not silently convert proposed methodology into an EAIOC requirement.

---

# 17. NO INVENTED PRECISION

Do NOT invent:

* benchmark results
* production results
* accuracy
* sample sizes
* confidence intervals
* p-values
* statistical thresholds
* effect-size thresholds
* reliability percentages
* rollout percentages
* regression percentages
* refresh cadences
* corpus sizes
* production traffic volumes

unless explicitly defined by an authoritative source.

Any concrete proposed value must be:

```text
PROPOSED DEFAULT — VALIDATE LOCALLY
```

Any concrete methodological procedure not source-defined must be:

```text
PROPOSED METHODOLOGY
```

---

# 18. EL-001 — SHADOW OPTIMIZATION

Fully elaborate the current architecture definition.

Cover:

* purpose
* applicability
* inputs
* baseline execution
* shadow optimization execution
* comparison
* quality evaluation
* cost evaluation
* latency evaluation
* optimizer overhead
* decision logic
* evidence generation
* promotion criteria
* failure behavior
* interaction with EL-002
* interaction with EL-003
* interaction with EL-004
* interaction with EL-005

Use the exact `EvaluationFramework` concepts from `interfaces.md`.

Do not invent interface fields.

Any promotion threshold must be:

```text
PROPOSED DEFAULT — VALIDATE LOCALLY
```

---

# 19. EL-002 — CONTROLLED ROLLOUT

Fully elaborate:

* rollout stages
* eligibility
* exposure control
* tenant isolation
* monitoring dependency
* quality gates
* regression detection
* automatic rollback
* manual rollback
* rollback triggers
* rollback evidence
* promotion criteria
* interaction with EL-005

Align with the six architecture-defined rollout phases.

Do not create a seventh phase.

Clearly distinguish:

```text
evaluation evidence
```

from:

```text
production authorization
```

---

# 20. EL-003 — A/B TESTING

Fully elaborate:

* hypothesis
* control
* treatment
* assignment
* randomization where applicable
* tenant boundaries
* workload stratification
* sample collection
* metric collection
* quality evaluation
* cost evaluation
* latency evaluation
* cache effects
* agent effects
* provider/model effects
* statistical analysis
* confidence intervals
* effect size
* practical significance
* significance testing
* multiple comparisons where applicable
* treatment contamination
* tenant-composition drift
* stopping rules
* experiment completion
* result reporting

Every concrete statistical default must be:

```text
PROPOSED DEFAULT — VALIDATE LOCALLY
```

Do not present arbitrary p-values, confidence levels, sample sizes, or effect thresholds as EAIOC requirements.

---

# 21. EL-004 — CONTINUOUS POLICY LEARNING

Fully elaborate:

* evidence collection
* candidate policy generation
* offline evaluation
* benchmark evaluation
* regression evaluation
* policy comparison
* governance boundary
* approval
* deployment
* monitoring
* rollback
* policy versioning
* evidence retention

## CRITICAL

`EL-004` MUST NOT imply autonomous production policy mutation.

The architecture explicitly bounds learning by governance.

Production policy changes must route through the owning approval mechanism:

```text
security.md
GSP-004 / Human Approval Gate
```

Do not redefine GSP-004.

---

# 22. EL-005 — REGRESSION DETECTOR

Fully elaborate:

* reference baseline
* comparison baseline
* model/provider update detection
* optimization configuration change
* policy change
* pricing change
* workload drift
* benchmark drift
* quality regression
* cost regression
* latency regression
* net-savings regression
* cache regression
* agent regression
* P5 regression separation
* attribution logic
* evidence requirements
* escalation
* rollback interaction

Explicitly cover:

```text
EC-071
EC-072
```

after verifying their exact current definitions.

---

# 23. CROSS-EL INTERACTION

Create a clear lifecycle:

```text
EL-001 Shadow
      ↓
Benchmark Evidence
      ↓
EL-003 A/B / Controlled Comparison
      ↓
EL-002 Controlled Rollout
      ↓
EL-005 Regression Detection
      ↓
EL-004 Policy Learning
      ↓
Governed Approval
      ↓
Next Evaluation Cycle
```

Do not imply this sequence is mandatory for every optimization unless the source requires it.

Explain where modules can operate independently.

---

# 24. BENCHMARK CORPUS METHODOLOGY

Elaborate architecture §32.

Cover:

* corpus objectives
* workload representation
* corpus types defined by architecture
* synthetic workloads
* curated workloads
* replayed workloads
* production-derived workloads where permitted
* edge-case workloads
* adversarial workloads where applicable
* holdout data
* validation data
* benchmark versioning
* corpus provenance
* freshness
* representativeness
* drift detection
* corpus refresh
* leakage prevention
* contamination prevention

Explicitly address:

```text
EC-072
```

if verified.

Do not claim a benchmark corpus is representative without evidence.

---

# 25. BASELINE/TREATMENT COMPARABILITY

Every experiment must establish what is held constant.

Where applicable hold constant:

* workload
* tenant
* application
* workflow
* agent
* model
* provider
* model/provider version
* prompt type
* intent
* tool configuration
* cache state
* policy version
* evaluator version
* environment
* relevant system state

If something changes between baseline and treatment, identify it as a confounder.

---

# 26. REQUIRED MEASUREMENTS

Use the canonical upstream measurements where applicable:

* baseline cost
* optimized cost
* gross savings
* optimizer overhead
* net savings
* savings percentage
* quality delta
* latency delta
* cache impact
* input tokens
* output tokens
* avoided tokens
* model calls
* tool calls
* agent iterations
* failures
* fallbacks
* successful task/outcome rate
* quality-adjusted cost

Do not create competing canonical metric names.

---

# 27. NET VALUE

Evaluation must distinguish:

```text
Gross Inference Savings
-
Optimization Overhead
=
Net Optimization Value
```

Where supported, account for:

* optimization model calls
* compression cost
* embedding cost
* retrieval cost
* cache cost
* routing cost
* additional provider cost
* additional tool cost
* infrastructure cost
* latency cost
* quality/risk impact

Never equate token reduction directly with net savings.

---

# 28. QUALITY EVALUATION BOUNDARY

Do not redefine `quality-gates.md`.

Instead explain how evaluation consumes quality evidence.

Evaluate, where applicable:

* quality preservation
* quality improvement
* quality degradation
* regression
* evaluator disagreement
* evaluator drift
* workload-specific quality
* task-specific quality

Preserve the existing Coding/RAG/Agent quality invariants.

---

# 29. STATISTICAL METHODOLOGY

This document owns the statistical methodology that upstream documents explicitly defer here.

Cover:

* sample size
* confidence intervals
* uncertainty
* effect size
* statistical significance
* practical significance
* paired comparisons
* unpaired comparisons
* bootstrap/resampling where appropriate
* variance
* outlier handling
* repeated measurements
* multiple comparisons
* power
* minimum detectable effect
* stopping rules

Do not prescribe one method for every experiment.

Every unsourced methodological choice must be:

```text
PROPOSED METHODOLOGY
```

Every unsourced numerical default must be:

```text
PROPOSED DEFAULT — VALIDATE LOCALLY
```

---

# 30. SAMPLE-SIZE METHODOLOGY

Define how sample size should depend on:

* expected effect
* variance
* desired confidence
* false-positive risk
* false-negative risk
* workload heterogeneity
* evaluator variance
* paired/unpaired design
* evaluation objective

Do not state one universal sample size.

---

# 31. STATISTICAL VS PRACTICAL SIGNIFICANCE

Explicitly distinguish:

### Statistical significance

Evidence against the selected null hypothesis under the chosen statistical model.

### Practical significance

Whether the effect magnitude is operationally meaningful.

A statistically significant improvement can still be operationally insignificant.

A practically meaningful observed effect may lack sufficient statistical evidence.

Do not collapse the two.

---

# 32. EVALUATOR METHODOLOGY

Define evaluator-selection considerations:

* deterministic validators
* unit tests
* integration tests
* schema validation
* citation validation
* business rules
* security checks
* human evaluation
* model-based evaluation
* application success signals
* tool-call correctness
* structured-output validation

Address:

* evaluator applicability
* evaluator limitations
* evaluator bias
* false positives
* false negatives
* evaluator version
* calibration
* reproducibility
* cost
* latency

Do not redefine VCL.

---

# 33. QUALITY-GATE CALIBRATION GAPS

Explicitly address:

```text
SOURCE-GAP-QUALGATE-01
SOURCE-GAP-QUALGATE-02
```

For each:

* quote/identify the actual source gap
* state whether `eval.md` narrows it
* if proposing a value, label it `PROPOSED DEFAULT — VALIDATE LOCALLY`
* explain why it is not authoritative
* preserve the original source gap if it remains unresolved

Do not silently close the source gap.

---

# 34. WORKLOAD REPRESENTATIVENESS

Define how to determine whether a benchmark corpus represents intended workloads.

Consider:

* traffic/task distribution
* tenant distribution
* prompt-type distribution
* context-size distribution
* workflow distribution
* agent distribution
* tool-use distribution
* model/provider distribution
* temporal distribution

Do not invent actual organizational distributions.

---

# 35. EXPERIMENT CONTAMINATION

Address:

* treatment leakage
* shared cache
* shared mutable state
* concurrent experiments
* provider rate limits
* workload ordering
* temporal effects
* model/provider updates
* policy changes
* evaluator changes
* tenant-composition drift

If contamination cannot be eliminated, document how it affects interpretation.

---

# 36. CACHE EVALUATION

Evaluate cache effects without redefining `cache-strategy.md`.

Distinguish:

* EAIOC application cache
* provider-native cache
* cache hit
* cache miss
* cache rejection
* invalidation
* cache savings
* quality impact

Do not attribute provider-native savings to EAIOC optimization without evidence.

---

# 37. AGENT / MULTI-AGENT EVALUATION

Evaluate:

* iterations
* tool calls
* avoided calls
* early exits
* verifier behavior
* sub-agent overhead
* coordination overhead
* task outcome
* quality
* cost
* latency
* failure propagation
* recovery

Fewer iterations or tool calls alone do not establish success.

---

# 38. P5 EVALUATION SEPARATION

Preserve:

```text
inference_serving.*
```

as a separate measurement namespace.

Do not merge:

```text
Layer 1/2 optimization effects
```

with:

```text
P5 / inference-serving effects
```

Combined reporting is allowed only if decomposition remains explicit.

---

# 39. REPRODUCIBILITY

Define reproducibility requirements covering, where applicable:

* code version
* optimization configuration
* policy version
* model version
* provider
* evaluator version
* workload version
* dataset version
* cache state
* environment
* random seed
* benchmark configuration
* timestamp
* experiment identity

Do not create a competing interface schema.

---

# 40. EVIDENCE INTEGRITY

Every evaluation result must distinguish:

```text
OBSERVED
ESTIMATED
INFERRED
SIMULATED
BENCHMARK-DERIVED
PRODUCTION-DERIVED
VERIFIED
UNVERIFIED
```

Never present:

* estimates as observations
* benchmarks as production guarantees
* external research as EAIOC validation
* incomplete data as verified results

---

# 41. NEGATIVE CONTROLS

Where appropriate, define negative controls such as:

* optimization disabled
* known-good baseline
* intentionally degraded configuration
* evaluator sanity check
* stale corpus
* mismatched evaluator
* cache contamination
* model/provider mismatch

Label any proposed negative-control framework:

```text
PROPOSED METHODOLOGY
```

---

# 42. REGRESSION TESTING

Elaborate architecture §35.

Create a post-update revalidation procedure covering:

```text
Detect Change
    ↓
Identify Affected Evaluation Scope
    ↓
Freeze/Select Reference
    ↓
Run Benchmark
    ↓
Run Quality Validation
    ↓
Compare Baseline/Treatment
    ↓
Analyze Cost/Latency
    ↓
Analyze Regression
    ↓
Attribute Cause
    ↓
Escalate / Roll Back / Accept
```

Explicitly cover:

* model/provider update
* optimization update
* policy update
* pricing update
* workload drift
* benchmark corpus drift

---

# 43. REGRESSION ATTRIBUTION

Define methodology for distinguishing regression caused by:

* optimization change
* model update
* provider change
* pricing change
* workload change
* policy change
* cache behavior
* evaluator change
* infrastructure/P5 change

Do not claim causal attribution where the experimental design cannot support it.

---

# 44. ROLLOUT STRATEGY

Elaborate the six architecture phases:

```text
OBSERVE
BENCHMARK
CONTROLLED PILOT
PRODUCTION
GOVERNANCE
CONTINUOUS OPTIMIZATION
```

For each transition define:

* required evidence
* evaluation status
* quality status
* cost status
* latency status
* regression status
* observability prerequisites
* approval requirements
* rollback readiness

Any numeric gate must be:

```text
PROPOSED DEFAULT — VALIDATE LOCALLY
```

---

# 45. EL-002 ROLLBACK

Define automatic and manual rollback concepts.

Potential triggers include:

* quality regression
* negative net savings
* unacceptable latency
* failure-rate increase
* policy violation
* evaluation integrity failure
* material unexplained regression

Do not invent concrete numerical triggers unless explicitly labelled.

Do not allow an evaluation-pipeline failure to silently bypass authorization/policy checks.

Preserve the repository's fail-open/fail-closed rules.

---

# 46. CONTINUOUS POLICY LEARNING GOVERNANCE

`EL-004` must follow:

```text
Observe
  ↓
Learn Candidate
  ↓
Offline Evaluate
  ↓
Benchmark
  ↓
Regression Check
  ↓
Governance Review
  ↓
Human Approval
  ↓
Controlled Deployment
  ↓
Observe
```

The learning component must not silently mutate production policy.

---

# 47. TENANT ISOLATION

Every evaluation mechanism must preserve tenant isolation.

Do not accidentally combine:

* prompts
* responses
* quality data
* cost data
* cache data
* private evaluation datasets
* policies

across tenants.

Cross-tenant aggregate statistics are permitted only where explicitly authorized and privacy/security requirements are satisfied.

---

# 48. EVALUATION OF OBSERVABILITY EVIDENCE

`observability.md` owns telemetry.

This document evaluates evidence quality.

Address:

* missing observations
* duplicate observations
* late observations
* incomplete traces
* incorrect attribution
* missing tenant identity
* missing model/provider identity
* cache attribution
* P5 separation
* verified vs estimated savings

Do not redesign observability.

---

# 49. BENCHMARK TYPES

Where supported or explicitly proposed, distinguish:

* component benchmark
* optimization-stage benchmark
* end-to-end benchmark
* regression benchmark
* quality benchmark
* cost benchmark
* latency benchmark
* cache benchmark
* agent benchmark
* multi-agent benchmark
* provider/model benchmark
* rollout benchmark

Mark any newly introduced taxonomy as:

```text
PROPOSED METHODOLOGY
```

---

# 50. EXTERNAL BENCHMARK EVIDENCE

If external benchmark results appear in upstream documents:

Preserve the distinction between:

```text
External Evidence
```

and:

```text
EAIOC-Local Evidence
```

External percentages are not EAIOC production guarantees.

Do not hard-code external benchmark results as expected EAIOC performance.

---

# 51. EVALUATION GATES

Define an evaluation methodology for:

```text
Data Validity
      ↓
Baseline Validity
      ↓
Treatment Validity
      ↓
Evaluator Validity
      ↓
Quality Evidence
      ↓
Economic Evidence
      ↓
Latency Evidence
      ↓
Reliability Evidence
      ↓
Statistical Evidence
      ↓
Regression Evidence
      ↓
Rollout Evidence
```

If this taxonomy is not source-defined:

```text
PROPOSED METHODOLOGY
```

Do not redefine QG-NNN.

---

# 52. FAILURE MATRIX

Create a detailed matrix covering:

| Failure                            | Detection | Evidence Impact | Evaluation Impact | Recovery/Disposition |
| ---------------------------------- | --------- | --------------- | ----------------- | -------------------- |
| Missing baseline                   |           |                 |                   |                      |
| Missing treatment                  |           |                 |                   |                      |
| Invalid evaluator                  |           |                 |                   |                      |
| Stale corpus                       |           |                 |                   |                      |
| Model mismatch                     |           |                 |                   |                      |
| Provider mismatch                  |           |                 |                   |                      |
| Cache contamination                |           |                 |                   |                      |
| Missing telemetry                  |           |                 |                   |                      |
| Insufficient sample                |           |                 |                   |                      |
| Quality regression                 |           |                 |                   |                      |
| Cost regression                    |           |                 |                   |                      |
| Latency regression                 |           |                 |                   |                      |
| Workload drift                     |           |                 |                   |                      |
| Evaluator drift                    |           |                 |                   |                      |
| P5 contamination                   |           |                 |                   |                      |
| Concurrent experiment interference |           |                 |                   |                      |

Do not invent unsupported mandatory recovery behavior.

---

# 53. `RegressionReport` GAP

If `interfaces.md` still references `RegressionReport` without defining it:

Create:

```text
SOURCE-GAP-EVAL-NN
```

State:

* exact source location
* what is referenced
* what is missing
* why it matters
* whether this document can conceptually describe the required information
* why the actual interface schema remains owned by `interfaces.md`

Do NOT silently define `RegressionReport` as an authoritative schema.

---

# 54. CONTROL-PLANE EVALUATION ALGORITHMS

In addition to EL-001–EL-005, define cross-cutting algorithms using the established EAIOC algorithm style.

Examples:

### Algorithm A — Baseline Selection

### Algorithm B — Treatment Construction

### Algorithm C — Benchmark Corpus Selection

### Algorithm D — Experiment Validity Check

### Algorithm E — Statistical Comparison

### Algorithm F — Net-Value Evaluation

### Algorithm G — Quality Evidence Aggregation

### Algorithm H — Regression Attribution

### Algorithm I — Rollout Promotion Decision

### Algorithm J — Rollback Decision

### Algorithm K — Benchmark Corpus Freshness Check

### Algorithm L — Evaluation Evidence Integrity Check

Only include algorithms justified by the actual source requirements.

Label newly introduced procedures:

```text
PROPOSED METHODOLOGY
```

where appropriate.

---

# 55. CROSS-DOCUMENT BOUNDARY MATRIX

Include an explicit table showing:

| Concern                 | Source/Owner              | `eval.md` Treatment     |
| ----------------------- | ------------------------- | ----------------------- |
| EL modules              | architecture §21          | Elaborated              |
| Benchmarking            | architecture §32          | Elaborated              |
| Rollout phases          | architecture §34          | Elaborated              |
| Regression              | architecture §35          | Elaborated              |
| Evaluation interfaces   | interfaces §18            | Operationally consumed  |
| RegressionReport schema | interfaces §18            | Gap if undefined        |
| Quality methodology     | quality-gates.md          | Cited, not redefined    |
| VCL                     | quality-gates.md §10      | Cited, not redefined    |
| Security approval       | security.md               | Cited, not redefined    |
| Observability           | observability.md          | Consumed, not redefined |
| Cache mechanics         | cache-strategy.md         | Consumed, not redefined |
| Agent mechanics         | agent-optimization.md     | Consumed, not redefined |
| P5                      | inference-optimization.md | Separated               |
| Provider facts          | provider-matrix.md        | Consumed                |
| Capacity                | SCALING.md                | Deferred                |

---

# 56. TRACEABILITY MATRIX

Create:

### EL Catalog Matrix

| EL ID | Name | Source | Status |
| ----- | ---- | ------ | ------ |

### EL × AC/OBJ Matrix

Trace every applicable:

```text
EL-001
EL-002
EL-003
EL-004
EL-005
```

to actual:

```text
OBJ-*
AC-*
```

IDs.

### Scenario Coverage Matrix

Use only verified scenario IDs.

### Edge-Case Coverage Matrix

Use only verified edge-case IDs.

### Cross-Document Coverage Matrix

Show what is owned versus cited.

### Entry Count Summary

State exact count.

---

# 57. SOURCE GAPS

Use:

```text
SOURCE-GAP-EVAL-01
SOURCE-GAP-EVAL-02
...
```

Potential gaps include:

* undefined `RegressionReport`
* absent sample-size source
* absent statistical significance source
* absent confidence-level source
* absent corpus size
* absent benchmark refresh interval
* absent local production evidence
* absent scenario domain
* absent evaluator calibration threshold
* absent evaluator recalibration cadence

Only record gaps actually supported by source review.

---

# 58. SOURCE CONTRADICTIONS

Use:

```text
CONTRA-EVAL-01
CONTRA-EVAL-02
...
```

only if genuine contradictions exist.

For each:

1. identify both sources;
2. state the conflict;
3. apply source hierarchy;
4. record resolution.

If none:

```text
Source Contradictions: 0
```

---

# 59. REQUIRED DOCUMENT STRUCTURE

Generate the final document substantially in this order:

```text
# Evaluation, Experimentation, Benchmarking, Statistical Evidence, Rollout, and Regression Reference

Document Metadata

Position in Documentation Chain

Sources Read

Scope Boundary

## 1. Status and Maturity

## 2. How to Read

## 3. Scope Boundary

## 4. Evaluation Invariants

## 5. Evaluation Architecture

## 6. Evaluation Lifecycle

## 7. EL-001 — Shadow Optimization

## 8. EL-002 — Controlled Rollout

## 9. EL-003 — A/B Testing

## 10. EL-004 — Continuous Policy Learning

## 11. EL-005 — Regression Detector

## 12. Cross-EL Interaction

## 13. Benchmarking Framework

## 14. Benchmark Corpus Construction

## 15. Baseline vs Treatment

## 16. Required Evaluation Measurements

## 17. Statistical Methodology

## 18. Sample Size and Power

## 19. Confidence and Uncertainty

## 20. Statistical vs Practical Significance

## 21. Quality Evaluation Boundary

## 22. Cost and Net-Value Evaluation

## 23. Latency Evaluation

## 24. Cache Evaluation

## 25. Agent and Multi-Agent Evaluation

## 26. P5 / Inference-Serving Evaluation Separation

## 27. Evaluator Methodology

## 28. Evaluator Calibration

## 29. Workload Representativeness

## 30. Experiment Contamination

## 31. Reproducibility

## 32. Evidence Integrity

## 33. Regression Testing

## 34. Regression Attribution

## 35. Rollout Strategy

## 36. Evaluation Gates

## 37. Failure and Rollback Matrix

## 38. Control-Plane Evaluation Algorithms

## 39. Scenario Coverage

## 40. Edge-Case Coverage

## 41. Cross-Document Boundary Matrix

## 42. Requirement / Traceability Matrix

## 43. Source Gaps

## 44. Source Contradictions

## Final Report

## Eval Readiness
```

Adjust numbering only when necessary to reflect actual source material.

---

# 60. INTERNAL CONSISTENCY REVIEW

Before finalizing, verify:

### EL consistency

All five EL modules are fully elaborated.

### Architecture consistency

Nothing contradicts architecture §21, §32, §34, or §35.

### Interface consistency

No interface schema is redefined.

### Quality consistency

No quality-gate mechanism is redefined.

### Security consistency

EL-004 cannot bypass human/governance approval.

### Observability consistency

No telemetry architecture is redefined.

### Cost consistency

Gross savings are not presented as net savings.

### Cache consistency

Provider-native cache benefits remain separate.

### Agent consistency

Reduced iterations/calls do not automatically mean success.

### P5 consistency

Inference-serving effects remain separate.

### Statistical consistency

Proposed statistical defaults are labelled.

### Evidence consistency

Observed/estimated/benchmark-derived/verified remain distinct.

### Tenant consistency

Experiments cannot silently mix tenant data.

### Scenario consistency

All scenario IDs actually exist.

### Edge-case consistency

All cited EC IDs are verified.

### Gap consistency

No source gap is silently closed.

---

# 61. MANDATORY ID VERIFICATION

Every cited:

```text
EL-*
EC-*
SCN-*
AC-*
OBJ-*
INTF-*
GSP-*
NFR-*
```

must be independently verified against the live source file.

Do not trust this prompt's descriptions.

Do not trust previous model output.

Do not trust memory.

If an ID cannot be verified:

* remove it, or
* record the appropriate source gap.

---

# 62. MANDATORY NUMERIC VERIFICATION

After generating the document, search it for:

* `%`
* `p <`
* `p-value`
* confidence levels
* sample sizes
* days
* hours
* minutes
* thresholds
* cadence
* effect sizes
* rollout percentages

Every unsourced concrete number must have:

```text
PROPOSED DEFAULT — VALIDATE LOCALLY
```

or be explicitly marked:

```text
ILLUSTRATIVE EXAMPLE
```

Do not leave bare invented numbers.

---

# 63. MANDATORY DEFERRAL VERIFICATION

Verify every upstream occurrence of:

```text
eval.md
```

in:

```text
quality-gates.md
optimization-catalog.md
security.md
observability.md
```

Confirm that each evaluation responsibility has been addressed.

---

# 64. MANDATORY SCENARIO VERIFICATION

Before claiming scenario coverage:

1. Search the complete scenario matrix.
2. Identify the actual relevant domain.
3. Verify each scenario ID.
4. Confirm its actual description.
5. Use only verified scenarios.

If no dedicated evaluation domain exists, state so honestly.

Do not manufacture scenario IDs.

---

# 65. MANDATORY EDGE-CASE VERIFICATION

Directly verify:

```text
EC-071
EC-072
```

and every other EC cited.

Do not rely on prompt summaries.

---

# 66. NO SILENT GENERALIZATION

Never transform:

```text
external benchmark result
```

into:

```text
EAIOC expected result
```

Never transform:

```text
single workload result
```

into:

```text
general production result
```

Never transform:

```text
provider-specific behavior
```

into:

```text
provider-neutral behavior
```

Never transform:

```text
observed token reduction
```

into:

```text
net economic success
```

---

# 67. FILE-SCOPE RULE

Create/modify only:

```text
docs/eval.md
```

Do NOT modify:

```text
CLAUDE.md
problemStatement
Engineering_Spec
architecture.md
interfaces.md
conventions.md
edge-cases.md
scenario-matrix.md
optimization-catalog.md
provider-matrix.md
cache-strategy.md
agent-optimization.md
inference-optimization.md
quality-gates.md
security.md
observability.md
```

Do NOT generate:

```text
SCALING.md
implementation-plan.md
requirements-traceability.md
ADRs
source code
tests
configuration
infrastructure
```

---

# 68. FINAL VALIDATION CHECKLIST

## Source

* [ ] CLAUDE.md read
* [ ] Problem Statement read
* [ ] Engineering Spec read
* [ ] architecture.md read
* [ ] interfaces.md read
* [ ] conventions.md read
* [ ] edge-cases.md read
* [ ] scenario-matrix.md searched
* [ ] optimization-catalog.md read
* [ ] provider-matrix.md read
* [ ] cache-strategy.md read
* [ ] agent-optimization.md read
* [ ] inference-optimization.md read
* [ ] quality-gates.md read
* [ ] security.md read
* [ ] observability.md read

## EL

* [ ] EL-001 fully elaborated
* [ ] EL-002 fully elaborated
* [ ] EL-003 fully elaborated
* [ ] EL-004 fully elaborated
* [ ] EL-005 fully elaborated

## Benchmarking

* [ ] Corpus methodology
* [ ] Baseline/treatment
* [ ] Workload representativeness
* [ ] Benchmark versioning
* [ ] Statistical methodology
* [ ] Sample-size methodology
* [ ] Uncertainty
* [ ] Practical significance
* [ ] Regression methodology

## Integrity

* [ ] No invented production evidence
* [ ] No invented benchmark result
* [ ] No invented ID
* [ ] No invented schema
* [ ] No gross-savings-as-net-savings error
* [ ] No token-reduction-only success criterion
* [ ] No provider-native misattribution
* [ ] No P5 contamination
* [ ] No tenant mixing
* [ ] No autonomous EL-004 production policy mutation

## Traceability

* [ ] EL × AC
* [ ] EL × OBJ
* [ ] Scenario coverage
* [ ] Edge-case coverage
* [ ] Cross-document boundary
* [ ] Source gaps
* [ ] Source contradictions
* [ ] All IDs verified

## Numeric Discipline

* [ ] Every unsourced number labelled
* [ ] Every proposed statistical threshold labelled
* [ ] Every proposed cadence labelled
* [ ] Every proposed rollout threshold labelled

## File Scope

* [ ] Only `docs/eval.md` changed
* [ ] No upstream file changed
* [ ] No sibling downstream document changed

---

# 69. GIT / FILE VALIDATION

Before delivery run:

```text
git status --short
git diff --stat
git diff -- docs/eval.md
```

Confirm only:

```text
docs/eval.md
```

is new/changed.

Do not modify any other file to make the validation pass.

---

# 70. FINAL REPORT

At the end provide:

* document ID
* version
* status
* line count
* EL entry count
* algorithm count
* benchmark sections
* statistical methodology coverage
* rollout coverage
* regression coverage
* scenario coverage
* edge-case coverage
* requirement coverage
* source-gap count
* contradiction count
* proposed-methodology count
* proposed-default count
* unresolved limitations
* `RegressionReport` status
* `SOURCE-GAP-QUALGATE-01/02` status
* file-scope confirmation
* next document

The next document is:

```text
SCALING.md
```

Do NOT generate it.

---

# 71. EVAL READINESS

End the document with:

```markdown
## Eval Readiness
```

The final line of the entire file must be exactly one of:

```text
READY FOR NEXT DOCUMENTATION PHASE
```

or:

```text
NOT READY — BLOCKING GAPS REMAIN
```

Use the second only when an actual blocking issue exists.

Do not force readiness.

The readiness verdict must occur **exactly once** in the document and must be the literal final line.

---

# 72. EXECUTION ORDER

Execute exactly:

### Step 1

Read `CLAUDE.md`.

### Step 2

Read/grep architecture §21 and §32–§35 first.

### Step 3

Read interfaces §18 and verify `RegressionReport`.

### Step 4

Verify EC-071/072.

### Step 5

Search the complete scenario matrix.

### Step 6

Search every upstream `eval.md` deferral.

### Step 7

Read quality-gates, security, and observability.

### Step 8

Read optimization-catalog, provider-matrix, cache-strategy, agent-optimization, and inference-optimization.

### Step 9

Construct the actual source-to-requirement map.

### Step 10

Identify:

```text
SOURCE-DEFINED
SOURCE-DERIVED
PROPOSED METHODOLOGY
PROPOSED DEFAULT — VALIDATE LOCALLY
DERIVED METRIC
SOURCE GAP
SOURCE CONTRADICTION
```

### Step 11

Generate only:

```text
docs/eval.md
```

### Step 12

Re-verify every ID.

### Step 13

Re-verify every gap claim.

### Step 14

Re-verify every scenario claim.

### Step 15

Search for unsourced numerical thresholds.

### Step 16

Perform the internal consistency audit.

### Step 17

Run git status/diff.

### Step 18

Produce Final Report.

### Step 19

End with the exact Eval Readiness verdict.

---

# 73. CRITICAL FINAL INSTRUCTION

Generate a **deep, repository-grounded, implementation-useful, statistically disciplined, evidence-aware `docs/eval.md`**.

The document must be strong enough that a future implementation team does not need to rediscover:

* how to construct benchmark corpora
* how to establish a valid baseline
* how to construct treatment
* how to conduct shadow evaluation
* how to perform A/B testing
* how to calculate and interpret net value
* how to determine sample-size methodology
* how to establish statistical evidence
* how to distinguish statistical from practical significance
* how to evaluate quality
* how to evaluate cost
* how to evaluate latency
* how to evaluate cache impact
* how to evaluate agent behavior
* how to detect regression
* how to attribute regression
* how to maintain benchmark freshness
* how to establish rollout evidence
* how to handle rollback
* how to preserve reproducibility
* how to preserve tenant isolation
* how to preserve evaluation evidence integrity
* how to handle model/provider updates
* how to handle evaluator drift
* how to handle concurrent experiments
* how to govern continuous policy learning

At the same time:

**Do not redefine upstream ownership.**

**Do not invent production evidence.**

**Do not invent benchmark results.**

**Do not invent authoritative statistical thresholds.**

**Do not invent IDs.**

**Do not invent interface schemas.**

**Do not redefine quality gates.**

**Do not redefine VCL.**

**Do not redefine security/GSP mechanisms.**

**Do not redefine observability.**

**Do not merge P5 inference-serving effects into Layer 1/2 optimization results.**

**Do not attribute provider-native effects to EAIOC without evidence.**

**Do not equate token reduction with optimization success.**

**Do not allow EL-004 to silently mutate production policy.**

**Do not silently close source gaps.**

**Do not silently reconcile source contradictions.**

**Do not modify any upstream or sibling document.**

**Do not generate `SCALING.md`.**

Generate only `docs/eval.md`, validate it rigorously, record every limitation honestly, and stop.**
