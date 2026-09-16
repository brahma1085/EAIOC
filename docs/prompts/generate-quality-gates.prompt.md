# MASTER PROMPT — Generate `docs/quality-gates.md`

## 1. Mission

Generate the next canonical EAIOC documentation artifact:

```text
docs/quality-gates.md
```

This document is the canonical elaboration of **Quality Gates, quality-preservation methodology, concrete quality validation, verifier calibration, and compression-contract validation** for the Enterprise Agent & LLM Inference Optimization Control Plane (EAIOC).

This is a **documentation-generation task only**.

The document must remain:

```text
Level 0 — RESEARCH / PRE-IMPLEMENTATION
```

Do NOT implement production code.

Do NOT create implementation classes, database schemas, deployment manifests, executable infrastructure, benchmark engines, evaluator implementations, ML models, GPU infrastructure, or provider infrastructure.

Do NOT create downstream documents.

Do NOT modify upstream documents.

---

# 2. STRICT FILE-SCOPE RULE

Create or modify ONLY:

```text
docs/quality-gates.md
```

Do not modify any upstream or downstream document.

Do not create:

```text
security.md
observability.md
eval.md
SCALING.md
implementation-plan.md
requirements-traceability.md
ADRs
```

or any other document.

At the end, inspect the working-tree/file diff and confirm that only the intended target file was modified/created.

---

# 3. READ THE ENTIRE CURRENT AUTHORITATIVE CHAIN

Before writing, read the actual current versions of:

1. `problemStatement.txt`
2. `Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md`
3. `architecture.md`
4. `interfaces.md`
5. `conventions.md`
6. `edge-cases.md`
7. `scenario-matrix.md`
8. `optimization-catalog.md`
9. `provider-matrix.md`
10. `cache-strategy.md`
11. `agent-optimization.md`
12. `inference-optimization.md`
13. root `CLAUDE.md`

Do not rely solely on previous summaries, prompts, memory, snippets, or prior generated documents.

The actual current files are authoritative.

---

# 4. AUTHORITY ORDER

Apply this authority order when sources conflict:

```text
Problem Statement
>
Engineering Specification
>
Architecture
>
Interfaces
>
Conventions
>
Edge Cases
>
Scenario Matrix
>
Optimization Catalog
>
Provider Matrix
>
Cache Strategy
>
Agent Optimization
>
Inference Optimization
>
This document
```

If a contradiction exists:

1. identify it,
2. apply the higher-authority source,
3. preserve the higher-authority rule,
4. document the contradiction,
5. record it under `Source Contradictions`,
6. never silently reconcile it.

Do not invent a resolution unless an authoritative source explicitly provides one.

---

# 5. DOCUMENT PURPOSE AND SCOPE

`quality-gates.md` owns the **Control Plane quality decision layer**.

It must define how EAIOC:

* resolves applicable quality requirements,
* determines quality constraints,
* identifies appropriate evidence,
* establishes/uses a baseline,
* evaluates optimization impact,
* determines evidence sufficiency,
* evaluates quality risk,
* allows or rejects optimization,
* requires monitoring or revalidation,
* escalates uncertain quality decisions,
* detects quality regression,
* records quality decisions and rationale.

Preserve this architectural boundary:

> EAIOC controls whether an optimization is acceptable from a quality perspective; it does not own the underlying model, inference engine, benchmark infrastructure, application business logic, evaluator internals, or domain-specific truth.

Therefore describe EAIOC primarily as:

```text
detect
classify
evaluate
compare
authorize
reject
escalate
revalidate
record
```

not as an implementation owner of evaluators or model infrastructure.

---

# 6. CENTRAL INVARIANT

Restate prominently:

> **Token reduction shall never be accepted without quality validation.**

Reference the authoritative source where verified, especially `architecture.md §28`.

Preserve the established quality rollback principle:

1. evaluate optimization against an appropriate baseline,
2. validate applicable quality requirements,
3. reject/rollback when mandatory quality requirements are violated,
4. fall back to the appropriate baseline representation where applicable,
5. record the quality failure,
6. do not represent the optimization as a successful saving when quality requirements were violated.

Do not strengthen or alter the authoritative rollback sequence without evidence.

---

# 7. QUALITY DIMENSIONS — SOURCE-FIRST

Read `architecture.md §28` and the current authoritative sources before defining the canonical dimensions.

If the current source still defines exactly these ten dimensions, preserve their exact order and names:

```text
1. Accuracy
2. Task completion
3. Retrieval quality
4. Factuality
5. Schema compliance
6. Semantic equivalence
7. Safety
8. User satisfaction
9. Latency
10. Cost
```

If the current authoritative source differs, use the current authoritative taxonomy and explicitly document the discrepancy.

Do not invent a competing quality taxonomy.

---

# 8. DOCUMENT-LOCAL QUALITY-GATE IDS

If the authoritative architecture confirms the ten dimensions above, establish exactly:

```text
QG-001 — Accuracy
QG-002 — Task completion
QG-003 — Retrieval quality
QG-004 — Factuality
QG-005 — Schema compliance
QG-006 — Semantic equivalence
QG-007 — Safety
QG-008 — User satisfaction
QG-009 — Latency
QG-010 — Cost
```

Use the exact authoritative order.

These are document-local Quality Gate IDs.

Do not create or modify:

```text
TECH-NNN
DA-NNN
AL-NNN
AR-NNN
CACHE-NNN
PROV-NNN
INFOPT-NNN
```

Do not invent another quality-dimension numbering scheme.

If current authoritative sources establish a different number of canonical dimensions, follow the source and document why.

---

# 9. REQUIRED `QG-NNN` ENTRY FIELDS

Each canonical quality gate should contain, where supported:

| Field                            | Requirement                                       |
| -------------------------------- | ------------------------------------------------- |
| Quality Gate ID                  | `QG-NNN`                                          |
| Dimension                        | Exact source-defined dimension                    |
| Purpose                          | What the gate protects                            |
| Measurement Methodology          | Baseline vs optimized methodology                 |
| Applicable Validator Type(s)     | Verified interface-supported validator types      |
| Evidence Required                | Evidence necessary to make the decision           |
| Evidence Strength / Limitations  | Applicability, freshness, uncertainty, provenance |
| Threshold / Acceptance Rule      | Source-defined/configurable rule; never invented  |
| Deterministic vs Probabilistic   | Classification and confidence implications        |
| Failure / Fallback Action        | Source-supported behavior                         |
| Compression Contract Interaction | Relevant QO-001 relationship                      |
| Revalidation Triggers            | Relevant state/evidence changes                   |
| Validated By                     | Verified scenario/edge-case references            |
| Traceability                     | Requirements and architectural references         |

Do not force a field where the authoritative source does not support it; explicitly mark the limitation instead.

---

# 10. THRESHOLD AND ANTI-FABRICATION RULE

This is mandatory.

Do NOT invent universal quality thresholds such as:

```text
accuracy >= 95%
factuality >= 90%
quality_score >= 0.85
hallucination_rate <= 2%
```

unless the exact value is explicitly supported by an authoritative source.

If the architecture establishes configurable thresholds but no validated numerical value exists:

```text
Threshold = policy/configuration/workload-defined
```

is acceptable.

Any newly proposed threshold must be explicitly labeled:

```text
PROPOSED DEFAULT — VALIDATE LOCALLY
```

and must never be represented as:

* production validated,
* universally applicable,
* benchmark proven,
* authoritative.

Because EAIOC remains Level 0, literature/vendor/research evidence may inform methodology but cannot by itself establish production safety.

---

# 11. VERIFIER INTERFACE BOUNDARY

Read and use the current `interfaces.md` definitions for:

* `QualityValidator`,
* `ValidationRequest`,
* `ValidatorSpec`,
* `ValidationResult`,
* `ValidationViolation`,
* `FallbackAction`,
* `VerifierCalibrationLayer`,
* `VerifierDescriptor`,
* `VerifierEvidence`,
* `VerificationResult`,
* `CalibrationMetadata`,
* `DriftEvent`.

Do not redefine interface schemas.

Reference actual enum values only after verifying them in the current `interfaces.md`.

If the current interface confirms validator types such as:

```text
SCHEMA
DETERMINISTIC_RULE
UNIT_TEST
INTEGRATION_TEST
TYPE_CHECK
STATIC_ANALYSIS
CITATION_CHECK
BUSINESS_RULE
SECURITY_CHECK
MODEL_BASED_EVAL
EMBEDDING_SIMILARITY
```

use the authoritative values and explain where they apply.

Likewise verify the current `verifier_type` enum before documenting VCL behavior.

---

# 12. DETERMINISTIC VS PROBABILISTIC VERIFICATION

Preserve the authoritative `OBJ-028` / interface invariant if verified:

* deterministic verifier confidence is definitionally `1.0`,
* probabilistic verifier confidence must be `< 1.0` unless appropriately calibrated.

Do not invent confidence values.

Do not treat model-based or probabilistic evaluation as unquestionable truth.

Document:

* calibration status,
* evidence provenance,
* benchmark applicability,
* evaluator limitations,
* drift,
* uncertainty.

---

# 13. VERIFIER CALIBRATION PROCEDURE

This document owns the **procedure**, not the `VerifierCalibrationLayer` interface.

Where supported by the current interfaces, define a Level-0 procedure covering:

### A. Benchmark-set construction

Explain what an appropriate calibration benchmark represents for each verifier category.

Distinguish deterministic verification from probabilistic verification.

### B. Calibration

Define how calibration evidence is collected and interpreted.

### C. Calibration status

Explain conditions under which a probabilistic verifier can be treated as calibrated.

### D. Drift detection

Explain how verifier acceptance behavior can drift independently of the optimization technique.

Use the authoritative drift interfaces and scenario references where verified.

### E. Recalibration triggers

Potential triggers include:

* evaluator/model change,
* provider change,
* evidence drift,
* workload drift,
* observed verifier drift,
* configuration change.

If cadence is not source-defined, label any proposed cadence:

```text
PROPOSED METHODOLOGY
```

### F. Threshold interaction

Explain how calibrated verifier evidence interacts with configurable quality requirements.

### G. Failure

Define behavior when calibration evidence is unavailable, stale, invalid, or insufficient.

Do not redefine VCL's interface schemas.

---

# 14. COMPRESSION-CONTRACT TEST SUITES

Read the authoritative QO-001 definitions in `architecture.md` and `conventions.md`.

Do not redefine the existing invariant rows.

Where the current source defines:

| Context | Required preservation                                                                                                 |
| ------- | --------------------------------------------------------------------------------------------------------------------- |
| Coding  | Function signatures, imports, types, API contracts, security constraints, relevant test expectations, error locations |
| RAG     | Entities, numbers, dates, citations, source attribution                                                               |
| Agent   | Objective, constraints, decisions, completed actions, pending actions, unresolved failures                            |

elaborate a concrete **validation/test-suite outline** for each.

The test-suite layer should identify appropriate `ValidatorSpec` types and checks.

### Coding

Cover, where applicable:

* signatures,
* imports,
* types,
* API contracts,
* security constraints,
* test expectations,
* error locations,
* repository consistency.

Reference `SEC-005` if verified.

### RAG

Cover:

* entities,
* numeric values,
* dates,
* citations,
* source attribution,
* grounding,
* retrieval correctness,
* freshness where applicable.

If `EC-063` is confirmed, preserve its key lesson:

> embedding similarity alone is insufficient for factuality.

### Agent

Cover:

* objective,
* constraints,
* decisions,
* completed actions,
* pending actions,
* unresolved failures.

Do not silently equate semantic similarity with preservation of agent execution truth.

---

# 15. ZERO-THRESHOLD / QUALITY-GATE BYPASS ANTI-PATTERN

If current `EC-069` confirms the zero-threshold anti-pattern, explicitly document it.

A configuration that effectively disables quality validation must not be treated as a functioning quality gate.

However:

* do not invent an implementation mechanism,
* do not invent a numeric universal floor,
* distinguish source-defined behavior from proposed configuration-validation methodology.

If a concrete configuration-time rule is proposed, label it:

```text
PROPOSED METHODOLOGY
```

unless the authoritative source already establishes it.

Preserve the distinction between:

```text
source-supported rule
```

and:

```text
new proposed hardening
```

---

# 16. QUALITY DECISION MODEL

Define the Control-Plane decision flow:

```text
1. Receive quality requirements
2. Resolve applicable quality policy
3. Identify mandatory quality constraints
4. Determine available quality evidence
5. Establish/validate the appropriate baseline
6. Evaluate proposed optimization impact
7. Determine evidence sufficiency and confidence
8. Compare against applicable quality requirements
9. Determine the appropriate outcome
10. Record decision and rationale
11. Revalidate when relevant state/evidence changes
```

Potential outcomes may include:

```text
ALLOW
ALLOW_WITH_MONITORING
REQUIRE_REVALIDATION
ESCALATE
DO_NOT_OPTIMIZE
```

Use these as canonical outcomes only if supported by authoritative sources; otherwise clearly label the outcome taxonomy as proposed methodology.

Do not collapse:

```text
quality failure
evaluation failure
security failure
optimization failure
```

into one generic status.

---

# 17. QUALITY PRECEDENCE

Preserve the established precedence:

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

Do not introduce a competing precedence model.

An optimization must not be selected merely because it saves:

* tokens,
* money,
* latency,
* compute,

when it violates a mandatory quality requirement.

---

# 18. QUALITY EVIDENCE

Define evidence using source-supported terminology.

Potential evidence includes:

* prior validated evaluations,
* workload benchmarks,
* regression suites,
* reference answers,
* deterministic validators,
* human evaluation,
* model-based evaluation,
* application success,
* tool-call correctness,
* structured-output validation,
* provider/model evidence,
* historical execution evidence.

For every evidence type, consider:

```text
provenance
freshness
workload applicability
evaluator version
model/provider version
state/version compatibility
limitations
uncertainty
```

Do not invent sample sizes, confidence values, statistical significance, or reliability percentages.

Do not assume all evidence sources have equal authority.

---

# 19. QUALITY BASELINE

Define a workload-appropriate baseline.

Preserve the principle:

> A source-reported percentage improvement is not a guarantee of EAIOC quality preservation.

Baseline comparison must account for relevant:

* workload,
* task,
* model,
* provider,
* context,
* workflow,
* optimization,
* evaluation criteria,
* evidence limitations.

Do not claim generalization beyond available evidence.

---

# 20. QUALITY GATE TYPES

Where supported, distinguish:

* admission-time gate,
* optimization-time gate,
* post-optimization validation gate,
* runtime/result gate,
* escalation gate,
* recovery/revalidation gate,
* benchmark/rollout gate.

Do not create arbitrary taxonomy merely for completeness.

Clearly distinguish authoritative taxonomy from:

```text
PROPOSED METHODOLOGY
```

---

# 21. QUALITY + TOKEN / CONTEXT OPTIMIZATION

Cover:

* context pruning,
* compression,
* summarization,
* deduplication,
* retrieval reduction,
* progressive compaction,
* prompt transformation.

Preserve mandatory-context protection.

If quality preservation cannot be demonstrated for a transformation, EAIOC must not claim that quality was preserved.

---

# 22. QUALITY + CACHE

`cache-strategy.md` remains authoritative for cache architecture.

Do not redefine the seven canonical cache types.

Quality-related cache reuse must consider, where relevant:

* tenant,
* authorization,
* policy,
* context,
* workflow,
* model,
* provider,
* evaluator,
* evidence version,
* freshness.

Explain stale or invalid quality evidence without inventing a new cache architecture.

---

# 23. QUALITY + INFERENCE OPTIMIZATION

Use the verified `inference-optimization.md`.

Preserve:

* P5 Layer 3 scope,
* H17 integration-not-implementation,
* H18 measurement separation,
* separate inference-serving measurement,
* capability availability boundaries,
* fail-open behavior for unavailable optional P5 capabilities where authoritative.

Quality gates may condition or reject inference-serving optimization when quality requirements are not satisfied.

Do not redesign P5 capabilities.

---

# 24. QUALITY + AGENT OPTIMIZATION

Use `agent-optimization.md`.

Cover quality implications of:

* early exit,
* unnecessary continuation,
* sub-agent delegation,
* compressed handoffs,
* reasoning budget,
* output budget,
* verifier-guided escalation,
* tool selection,
* agentic RAG,
* coding agents,
* multi-agent workflows.

Do not redefine `AL-*` or `AR-*`.

In particular, preserve the boundary:

> `AR-004` owns agent-loop escalation decision logic; this document owns quality validation/calibration that informs the decision.

---

# 25. QUALITY + MODEL / PROVIDER ROUTING

Use `provider-matrix.md` and `agent-optimization.md`.

Quality validation must consider:

* model capability,
* provider capability,
* model/provider change,
* quality-evidence validity,
* evaluator compatibility,
* fallback model behavior.

Never assume a provider/model is:

```text
accessible
authorized
available
eligible
```

without authoritative evidence.

Do not invent provider capabilities.

---

# 26. QUALITY + STATE RECONCILIATION

Quality decisions are state-dependent.

Relevant state may include:

* user intent,
* context version,
* workflow version,
* model,
* provider,
* policy,
* authorization,
* tool availability,
* memory,
* repository state,
* external state,
* evaluator version,
* benchmark/evaluation configuration.

Conceptually link:

```text
Quality Decision
+
Relevant State Version
+
Evidence Version
```

Do not invent schema fields unless supported by existing interfaces.

When relevant state changes, prior quality evidence may require revalidation.

---

# 27. QUALITY + RECOVERY

Preserve:

> **Resume != replay.**

On recovery, reassess whether prior quality evidence remains valid.

Potential invalidation triggers include:

* model switch,
* provider switch,
* context change,
* workflow change,
* policy change,
* evaluator change,
* tool change,
* external-state change.

Recovery outcomes must remain consistent with the authoritative recovery architecture.

Do not blindly reuse stale quality decisions.

---

# 28. QUALITY + SUPERSESSION

A quality decision belonging to a superseded execution must not authorize unwanted work in a new execution.

Supersession must prevent stale:

* quality evidence,
* optimization decisions,
* authorization context,
* evaluation state,

from leaking into a new execution.

Use the authoritative supersession terminology.

---

# 29. NEGATIVE OPTIMIZATION

Treat:

```text
DO_NOT_OPTIMIZE
```

as a legitimate Control-Plane outcome when quality risk, evidence insufficiency, or governing constraints make optimization unacceptable.

Do not treat every skipped optimization as an error.

Verify before citing any specific:

```text
EC-154
SCN-NOPT-001
```

or other IDs.

---

# 30. NET OPTIMIZATION VALUE

Quality participates in the existing:

```text
Net Optimization Value
```

framework.

Do not create a competing economic model.

Token reduction alone is not success.

If quality degradation invalidates the optimization, the optimization must not be represented as positive net value merely because token/cost savings occurred.

Quality-evaluation overhead must also be treated consistently with the existing economics model.

---

# 31. QUALITY FAILURE MODES

Cover relevant source-supported failures such as:

* evaluator unavailable,
* evaluator timeout,
* insufficient evidence,
* stale evidence,
* evaluator version mismatch,
* benchmark mismatch,
* quality requirement unavailable,
* ambiguous quality requirement,
* false-positive pass,
* false-negative failure,
* evaluator disagreement,
* model/provider change,
* context change,
* concurrent optimization,
* adversarial input,
* quality regression,
* measurement contamination,
* unrepresentative benchmark,
* quality-gate bypass,
* hidden degradation.

Use existing EC IDs only after verification.

---

# 32. QUALITY FAILURE BEHAVIOR

Distinguish:

### Security/policy failure

Follow the higher-order security/policy architecture.

### Quality requirement failure

Reject, rollback, escalate, or otherwise follow authoritative quality behavior.

### Evaluation infrastructure failure

Do not automatically treat evaluation failure as quality PASS.

### Optimization failure

Follow optimization/recovery behavior.

Do not automatically apply the P5 fail-open rule to quality gates.

The failure behavior must be derived from the authoritative sources.

---

# 33. QUALITY GATE COMPOSITION

Explain, where supported:

* mandatory gates,
* conditional gates,
* AND composition,
* OR composition,
* precedence,
* insufficient evidence,
* conflicting evidence,
* short-circuit behavior,
* escalation,
* revalidation.

If composition logic is newly proposed, label it:

```text
PROPOSED METHODOLOGY
```

Do not invent a new executable boolean language.

---

# 34. APPLICATION-CLASS COVERAGE

Address quality applicability to:

### Generic LLM applications

### Autonomous agents

### Developer / coding agents

### Multi-agent / sub-agent systems

### Agentic RAG

### Tool/MCP-enabled systems

Use the same EAIOC Control Plane principles rather than inventing separate architectures.

---

# 35. CODING-AGENT QUALITY

Treat coding/developer agents as first-class.

Where applicable cover:

* compilation/build validation,
* tests,
* static analysis,
* repository consistency,
* API compatibility,
* regression detection,
* patch correctness,
* security constraints,
* unresolved failures,
* tool-call correctness.

Do not assume every repository has a build/test system.

Do not invent concrete commands.

---

# 36. AGENTIC-RAG QUALITY

Where supported, cover:

* retrieval correctness,
* relevance,
* grounding,
* factuality,
* citation integrity,
* provenance,
* freshness,
* context-transformation impact,
* retrieval-reduction impact.

Do not invent universal retrieval metrics.

---

# 37. HUMAN-IN-THE-LOOP

Where authoritative architecture supports human approval/escalation:

* explain when quality uncertainty may require human involvement,
* preserve existing HAG terminology,
* do not invent a new approval workflow.

---

# 38. MULTI-TENANCY AND DATA GOVERNANCE

Quality evidence and decisions must respect:

* tenant isolation,
* authorization,
* policy,
* data governance,
* sensitivity classification,
* provenance.

Never allow:

```text
Tenant A evidence
        ↓
Tenant B decision
```

unless an authoritative policy explicitly permits it.

Do not invent storage schemas.

---

# 39. OBSERVABILITY AND AUDIT BOUNDARY

Quality decisions must be observable conceptually.

Record, at the conceptual level:

* gate evaluated,
* requirement,
* evidence,
* evidence provenance,
* decision,
* rationale,
* optimization affected,
* model/provider,
* relevant state/evidence versions,
* revalidation requirement,
* escalation outcome.

Do not create the final telemetry architecture.

That belongs to `observability.md`.

The quality document should make decisions explainable:

```text
Why was optimization allowed?
Why was it rejected?
Which requirement applied?
What evidence was used?
Was the evidence current?
What changed?
Why was escalation triggered?
Why was DO_NOT_OPTIMIZE selected?
```

---

# 40. H18 MEASUREMENT SEPARATION

Preserve H18.

Quality measurement must remain distinguishable from:

* token savings,
* context savings,
* cache savings,
* inference-serving optimization,
* agent optimization,
* application business metrics.

Quality can act as a gate/constraint on those decisions, but must remain independently attributable.

---

# 41. QUALITY REGRESSION

Define quality regression conceptually as deterioration relative to the applicable baseline or requirement.

Cover:

* detection,
* evidence sufficiency,
* uncertainty,
* rollback/rejection,
* escalation,
* revalidation.

Do not invent statistical guarantees.

Reference the authoritative regression architecture rather than redefining it.

---

# 42. QUALITY EVALUATION ECONOMICS

Quality evaluation itself has cost and latency.

Within the existing Net Optimization Value framework, recognize that:

```text
evaluation overhead
```

may affect whether an optimization is economically worthwhile.

Do not create a competing formula.

Do not reuse stale evidence merely to avoid evaluation cost.

---

# 43. QUALITY EVIDENCE REUSE

Quality evidence may conceptually be reusable only when the owning evidence/cache policy permits it.

Consider:

```text
tenant
authorization
policy
workload
context
model
provider
evaluator
evidence version
freshness
workflow
```

Do not invent a new cache type or redefine the canonical cache architecture.

---

# 44. REQUIRED CONTROL-PLANE ALGORITHMS

Include structured pseudocode or decision logic for:

### Algorithm A — Resolve Quality Requirements

### Algorithm B — Determine Applicable Quality Gates

### Algorithm C — Establish / Validate Quality Evidence

### Algorithm D — Evaluate Proposed Optimization Against Quality

### Algorithm E — Quality Decision

### Algorithm F — Quality Revalidation

### Algorithm G — Quality Failure / Escalation

### Algorithm H — Quality-Aware `DO_NOT_OPTIMIZE`

### Algorithm I — Quality Regression Detection

### Algorithm J — Quality Decision Audit Record

All algorithms must remain Control Plane algorithms.

Do not implement:

* evaluator internals,
* ML models,
* GPU systems,
* provider infrastructure,
* benchmark engines,
* databases,
* distributed schedulers.

---

# 45. REQUIRED MATRICES

Include all six:

## Matrix 1 — Quality Gate Catalog

At minimum:

```text
Gate ID
Gate Name
Purpose
Applicable Optimization Types
Trigger
Required Evidence
Decision
Failure Behavior
Revalidation Trigger
Traceability
```

## Matrix 2 — Quality Dimension Matrix

```text
Quality Dimension
Applicable Workload
Evidence Type
Affected Optimizations
Failure Consequence
Fallback / Escalation
```

## Matrix 3 — Optimization × Quality Matrix

Cover at minimum:

```text
Token optimization
Context optimization
Cache optimization
Inference optimization
Agent optimization
Model/provider routing
Tool/MCP optimization
Multi-agent optimization
Coding-agent optimization
Agentic RAG
```

## Matrix 4 — Quality Failure / Recovery Matrix

```text
Failure
Detection
Quality Impact
Control Plane Decision
Recovery
Revalidation
Audit
```

## Matrix 5 — Quality Evidence Matrix

```text
Evidence Type
Strength / Applicability
Freshness
Limitations
Reuse Conditions
Invalidation Conditions
```

Use qualitative language unless authoritative numeric evidence exists.

## Matrix 6 — Quality Traceability Matrix

```text
Requirement
Quality Gate
Scenario
Edge Case
Expected Behavior
Coverage
```

Use only verified IDs.

---

# 46. SCENARIO / EDGE-CASE / REQUIREMENT DISCIPLINE

Never fabricate:

```text
SCN-XXX
EC-XXX
AC-XXX
NFR-XXX
OBJ-XXX
```

Verify every referenced ID against the current authoritative files.

Where dedicated coverage does not exist, use:

```text
NO DEDICATED SCENARIO
```

Where only analogous coverage exists:

```text
THIN / ANALOGOUS COVERAGE
```

Do not convert collective coverage into capability-specific coverage.

Preserve any existing:

```text
PARTIALLY COVERED
```

classification honestly.

Do not silently upgrade partial coverage.

---

# 47. REQUIRED QUALITY REFERENCES TO INVESTIGATE

Verify current versions before using any of these:

### Acceptance Criteria

```text
AC-003
AC-015
AC-017
AC-018
AC-019
AC-020
AC-031
AC-033
AC-034
AC-036
AC-037
AC-038
AC-044
```

### Edge Cases

```text
EC-020
EC-052
EC-061
EC-062
EC-063
EC-065
EC-069
EC-071
EC-072
EC-076
EC-077
EC-154
EC-162
EC-163
EC-164
EC-165
EC-211
```

### Objectives / Requirements

```text
OBJ-026
OBJ-028
OBJ-029
OBJ-030
```

### Scenarios

Investigate all current quality-related scenarios, especially any current equivalents of:

```text
SCN-QUAL-001
SCN-QUAL-002
SCN-QUAL-003
SCN-QUAL-004
SCN-QUAL-005
```

IMPORTANT:

These are **candidate references to verify**, not assumed facts.

If any ID is absent, renamed, or semantically different, do not invent a replacement.

---

# 48. SPECIFIC QUALITY CASES

If verified in the current authoritative files, explicitly preserve:

### EC-063

Embedding similarity alone must not be treated as sufficient for factual correctness where entity/numeric preservation matters.

### EC-069

A zero/effectively disabled quality threshold is a quality-gate bypass/misconfiguration risk.

### Verifier drift

Verifier acceptance behavior can drift independently of optimization-technique changes.

### Compression contracts

Coding, RAG, and Agent context invariants require concrete validation.

### H06 / OBJ-028

Verifier confidence/calibration must distinguish deterministic and probabilistic verification.

---

# 49. CROSS-DOCUMENT BOUNDARY

Explicitly document:

| Concern                                                       | Owner                                |
| ------------------------------------------------------------- | ------------------------------------ |
| Quality-dimension taxonomy and abstract rollback architecture | `architecture.md` / `conventions.md` |
| QualityValidator interface                                    | `interfaces.md`                      |
| VerifierCalibrationLayer interface                            | `interfaces.md`                      |
| Agent-loop escalation decision                                | `agent-optimization.md`              |
| Concrete quality-gate methodology                             | **this document**                    |
| Concrete verifier calibration procedure                       | **this document**                    |
| Compression-contract validation suites                        | **this document**                    |
| Content integrity / prompt injection                          | `security.md`                        |
| Detailed security architecture                                | `security.md`                        |
| Benchmark corpus/statistical evaluation strategy              | `eval.md`                            |
| Detailed telemetry architecture                               | `observability.md`                   |
| Cache architecture                                            | `cache-strategy.md`                  |
| P5 capability negotiation                                     | `inference-optimization.md`          |
| Provider facts/capabilities                                   | `provider-matrix.md`                 |
| Canonical cross-document requirements traceability            | `requirements-traceability.md`       |

Do not generate any downstream document.

---

# 50. MATURITY MODEL

Relate Quality Gates to the authoritative maturity model if verified.

Preserve:

```text
Level 0 — RESEARCH
through
Level 5 — AUTO-TUNED
```

Any newly proposed threshold, calibration procedure, or quality methodology remains Level 0 evidence unless locally validated.

Do not claim production safety merely because:

* a paper reports an improvement,
* a vendor reports a benchmark,
* a theoretical method appears sound.

Promotion requires actual evidence through the authoritative benchmarking/evaluation framework.

---

# 51. BENCHMARKING AND REGRESSION BOUNDARY

Reference the authoritative benchmarking and regression sections.

Explain that:

* baseline vs optimized measurements feed quality decisions,
* quality dimensions participate in validation,
* regression re-runs appropriate measurements over time.

Do not consume the future `eval.md` scope.

`eval.md` will own deeper:

* benchmark corpus construction,
* evaluation strategy,
* A/B methodology,
* statistical methodology,

when generated.

---

# 52. ANTI-PATTERNS

Include a dedicated section covering relevant anti-patterns:

* threshold bypass,
* optimizing before resolving quality requirements,
* treating token reduction as quality,
* evaluator output treated as unquestionable truth,
* stale quality evidence,
* cross-tenant evidence leakage,
* benchmark overgeneralization,
* hidden quality degradation,
* quality-gate bypass,
* evaluation failure silently converted to PASS,
* quality gate implemented as optimization logic,
* double-counting quality and optimization metrics,
* treating `DO_NOT_OPTIMIZE` as an error,
* replaying stale quality decisions after state changes,
* decisions without sufficient audit evidence.

Only include items consistent with authoritative architecture.

---

# 53. TEST / VALIDATION STRATEGY

Because this is Level 0:

Do NOT provide implementation test code.

Define validation categories such as:

* contract validation,
* scenario validation,
* edge-case validation,
* compression-contract validation,
* verifier calibration validation,
* benchmark validation,
* regression validation,
* provider/model variation,
* state-change validation,
* recovery validation,
* adversarial validation.

Map them to actual source references where available.

---

# 54. SOURCE GAPS

Create:

```text
## Source Gaps
```

Use:

```text
SOURCE-GAP-QUALGATE-NN
```

for each meaningful missing authoritative fact.

Record:

* missing information,
* affected section,
* why it matters,
* expected authoritative source,
* disposition,
* blocking/non-blocking.

Potential areas to investigate include:

* calibration cadence,
* concrete threshold definitions,
* configuration-time zero-threshold enforcement,
* evidence freshness requirements,

but do not assume these are gaps until the actual current sources are checked.

---

# 55. SOURCE CONTRADICTIONS

Create:

```text
## Source Contradictions
```

Use:

```text
CONTRA-QUALGATE-NN
```

If none exist after checking:

```text
None found.
```

Do not silently reconcile contradictions.

---

# 56. PROPOSED METHODOLOGY DISCIPLINE

Any newly introduced methodology without authoritative support must be explicitly labeled:

```text
PROPOSED METHODOLOGY
```

Any newly introduced numeric default must be labeled:

```text
PROPOSED DEFAULT — VALIDATE LOCALLY
```

Examples include:

* evidence-confidence ladders,
* calibration cadence,
* quality-evidence freshness windows,
* gate composition rules,
* proposed default thresholds,
* evidence scoring.

Never present proposed methodology as established architecture.

---

# 57. REQUIRED DOCUMENT STRUCTURE

The final `quality-gates.md` should contain, at minimum:

1. Header / status / version / generation date / source list
2. How to Read This Document
3. Scope and Control-Plane Boundary
4. Central Quality Invariant
5. Quality Dimensions
6. `QG-001` through `QG-010` or the verified authoritative equivalent
7. Quality Decision Model
8. Quality Gate Types
9. Quality Evidence and Baselines
10. Compression-Contract Test Suites
11. Verifier Calibration Procedure
12. Zero-Threshold / Gate-Bypass Rule
13. Quality + Optimization Interactions
14. Quality + State/Revalidation
15. Quality + Recovery/Supersession
16. Quality + Security/Governance
17. Quality + Economics / Net Optimization Value
18. Quality Failure Modes and Recovery
19. Quality Gate Composition
20. Application-Class Quality Considerations
21. Observability / Audit Boundary
22. Quality Measurement Separation
23. Anti-Patterns
24. Test / Validation Strategy
25. Cross-Document Boundary
26. Coverage Matrix
27. Source Gaps
28. Source Contradictions
29. Final Report
30. `## Quality Gates Readiness`

Do not add unrelated sections merely to increase document size.

---

# 58. FINAL REPORT

End with:

```text
## Final Report
```

Include:

* document generated,
* maturity level,
* source documents actually read,
* canonical quality-gate count,
* quality-dimension coverage,
* compression-contract coverage,
* verifier-calibration coverage,
* optimization coverage,
* application-class coverage,
* algorithm coverage,
* matrix coverage,
* scenario coverage,
* edge-case coverage,
* source gaps,
* contradictions,
* proposed methodology,
* threshold limitations,
* deferred implementation areas,
* security/policy precedence confirmation,
* state/revalidation confirmation,
* recovery/supersession confirmation,
* H18 measurement-separation confirmation,
* file-scope confirmation.

Do not claim 100% coverage unless actually demonstrated.

---

# 59. FINAL READINESS RULE

Declare:

```text
READY FOR NEXT DOCUMENTATION PHASE
```

ONLY if:

* all required sections exist,
* required `QG-NNN` entries exist in verified authoritative order,
* all required algorithms exist,
* all six matrices exist,
* references are verified,
* no fabricated IDs exist,
* no blocking contradiction remains unresolved,
* no implementation leakage exists,
* proposed methodology is clearly labeled,
* unsupported thresholds are not presented as authoritative,
* limitations are honestly disclosed,
* file scope is correct.

If a blocking issue remains:

```text
NOT READY — QUALITY-GATE GAPS MUST BE RESOLVED
```

Use this instead.

The readiness verdict must appear **exactly once**.

It must be the **true final line of the file**.

Nothing may follow it.

Before declaring readiness, directly inspect the final ~20 lines of the generated file.

---

# 60. CONCISE EXECUTION CHECKLIST

Before finalizing:

* [ ] Read all current authoritative sources.
* [ ] Apply the authority order.
* [ ] Modify only `docs/quality-gates.md`.
* [ ] Keep Level 0 / pre-implementation scope.
* [ ] Preserve the central quality invariant.
* [ ] Verify the authoritative quality-dimension taxonomy.
* [ ] Verify/create `QG-001`–`QG-010` only if the current architecture confirms the ten dimensions.
* [ ] Do not invent universal numeric thresholds.
* [ ] Label proposed thresholds `PROPOSED DEFAULT — VALIDATE LOCALLY`.
* [ ] Label unsupported procedures `PROPOSED METHODOLOGY`.
* [ ] Preserve deterministic/probabilistic verifier semantics.
* [ ] Cover VCL calibration without redefining its interface.
* [ ] Cover Coding/RAG/Agent compression-contract validation.
* [ ] Preserve EC-063 factuality lesson if verified.
* [ ] Preserve EC-069 zero-threshold lesson if verified.
* [ ] Preserve security/policy precedence.
* [ ] Cover token/context/cache/inference/agent/provider/tool/MCP/multi-agent/coding/RAG.
* [ ] Preserve state reconciliation, revalidation, recovery, and supersession.
* [ ] Preserve negative optimization and Net Optimization Value.
* [ ] Preserve tenant/data-governance boundaries.
* [ ] Preserve H18 measurement separation.
* [ ] Include Algorithms A–J.
* [ ] Include all six required matrices.
* [ ] Verify every scenario/edge-case/AC/NFR/OBJ reference.
* [ ] Preserve partial/thin/analogous coverage honestly.
* [ ] Record source gaps and contradictions.
* [ ] Respect `eval.md`, `security.md`, `observability.md`, and `requirements-traceability.md` boundaries.
* [ ] Perform independent final verification against the prompt and current upstream files.
* [ ] Verify the final readiness line is exactly once and is the true final line.
* [ ] Stop after `quality-gates.md`.

---

# 61. IMPORTANT FINAL INSTRUCTION

Do not stop after producing an outline.

Produce the complete, detailed, internally consistent:

```text
docs/quality-gates.md
```

Preserve all useful authoritative constraints.

Do not silently remove important requirements to reduce document size.

Do not invent unsupported facts, thresholds, IDs, provider capabilities, evaluator behavior, or implementation details.

Do not modify any other file.

After generation, independently verify the document against:

1. this master prompt,
2. the actual current authoritative upstream documents,
3. the scenario/edge-case references,
4. the required algorithms,
5. the required matrices,
6. the file-scope rule,
7. the final readiness rule.

Only after that verification may the document be marked ready.

STOP after completing and verifying:

```text
docs/quality-gates.md
```
