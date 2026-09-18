# V2 CONSOLIDATED MASTER PROMPT — GENERATE `docs/observability.md`

## 0. MISSION

Act as a **Senior Observability, FinOps, Distributed-Systems, Governance, and LLM Platform engineer** responsible for the:

**Enterprise Agent & LLM Inference Optimization Control Plane (EAIOC)**

Create the canonical:

**Observability, Telemetry Pipeline, Metrics, Logs, Distributed Tracing, Events, Audit/Explainability, Dashboards, SLO/SLI, Alerting, Telemetry Governance, and Measurement-Integrity Reference**

for EAIOC.

Produce exactly:

`docs/observability.md`

This document is the downstream implementation-oriented elaboration of the observability responsibilities already established by the authoritative EAIOC documents.

This is **not** a generic observability guide, OpenTelemetry tutorial, Prometheus guide, Grafana guide, or dashboard-design article.

The document must explain precisely how EAIOC's already-defined observability contracts are turned into an implementable telemetry pipeline, operational dashboards, alerting model, audit/explainability retrieval model, telemetry-governance model, and validation framework.

---

# 1. DOCUMENT STATUS AND MATURITY

This document is:

**Level 0 — RESEARCH / PRE-IMPLEMENTATION**

Therefore:

* Do not claim production validation where none exists.
* Do not claim a threshold is industry-standard unless a source explicitly supports that claim.
* Do not invent production measurements.
* Do not fabricate benchmark results.
* Do not fabricate scenarios.
* Do not silently convert a proposed methodology into an authoritative requirement.
* Every newly proposed numerical threshold, interval, capacity, cardinality limit, or retention value must be explicitly tagged:

`PROPOSED DEFAULT — VALIDATE LOCALLY`

or, where appropriate:

`PROPOSED METHODOLOGY`

Source-defined requirements must remain clearly distinguishable from proposed implementation defaults.

---

# 2. AUTHORITATIVE SOURCE HIERARCHY

Before writing anything, read and understand the following documents completely.

Use the latest available version in the repository/library.

## Tier 1 — Foundational Authority

1. `docs/Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt`

   * EAIOC-SPEC-001
   * Highest authority
   * Especially PS §12 and PS §52.15/H15

2. `docs/Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md`

   * EAIOC-SPEC-001 Rev 1.4
   * Especially observability and H15 elaboration

3. `docs/architecture.md`

   * EAIOC-ARCH-001 Rev 1.3
   * Especially:

     * §31 Observability and Governance
     * §31.1 Dashboard Requirements
     * §31.2 Required Dimensions
     * §31.3 Governance Requirements
     * §27 Token and Cost Accounting Ledger
     * §28 Quality Gates
     * §29 Security/Governance
     * §47.16 Decision Explainability and Audit
     * NFR-006
     * NFR-010
     * OBJ-011
     * AC-052

4. `docs/interfaces.md`

   * EAIOC-INTF-001 v1.2.0
   * Especially:

     * §19 Observability Interface
     * `ControlPlaneLogEntry`
     * Standard Metrics
     * `OptimizationSpan`
     * End-to-End Trace Correlation Chain
     * `AuditRecord`
     * §23 `ControlPlaneEvent`
     * Standard Event Types
     * Event Delivery Requirements

5. `docs/conventions.md`

   * EAIOC-CONV-001 Rev 1.1.0
   * Especially:

     * §17 Observability Conventions
     * mandatory distributed tracing
     * log conventions
     * dashboard tiers
     * event-bus requirements
     * metric naming
     * tenant/correlation propagation
     * retention conventions

6. `docs/edge-cases.md`

   * EAIOC-EDGE-001 v1.2.0
   * Verify all observability/audit-related edge cases directly.
   * At minimum inspect:

     * EC-066
     * EC-067
     * EC-070
     * EC-076
     * EC-198
     * EC-199
   * Also identify any additional observability-related edge cases introduced by later amendments.

7. `docs/scenario-matrix.md`

   * Verify whether a dedicated `SCN-OBS-*` domain exists.
   * Do not assume that it does not exist.
   * If absent, explicitly state that finding.
   * Identify actual scenarios that exercise observability, audit, telemetry, measurement separation, governance, and failure reporting.

## Tier 2 — Completed Upstream Downstream Documents

8. `docs/optimization-catalog.md`

9. `docs/provider-matrix.md`

10. `docs/cache-strategy.md`

11. `docs/agent-optimization.md`

12. `docs/inference-optimization.md`

13. `docs/quality-gates.md`

14. `docs/security.md`

These documents are subordinate to Tier 1 if a real contradiction exists.

Do not silently resolve contradictions.

Record them under:

`SOURCE CONTRADICTIONS`

using:

`CONTRA-OBS-NN`

---

# 3. SOURCE OWNERSHIP RULE

Never duplicate or redefine an upstream contract merely because this document consumes it.

## Owned upstream

| Concern                                | Authority                   |
| -------------------------------------- | --------------------------- |
| Observability interface schemas        | `interfaces.md` §19         |
| Event schemas and standard event types | `interfaces.md` §23         |
| Dashboard tier definitions             | `architecture.md` §31.1     |
| Required observability dimensions      | `architecture.md` §31.2     |
| Governance questions                   | `architecture.md` §31.3     |
| Token/cost ledger fields               | `architecture.md` §27       |
| Quality methodology                    | `quality-gates.md`          |
| Security/Governance mechanisms         | `security.md`               |
| Provider-specific metrics              | `provider-matrix.md`        |
| Cache-specific metrics                 | `cache-strategy.md`         |
| Agent-specific metrics                 | `agent-optimization.md`     |
| P5 inference-serving metrics           | `inference-optimization.md` |
| Benchmark/evaluation methodology       | `eval.md` when generated    |
| Capacity/throughput targets            | `SCALING.md` when generated |

## Owned by `observability.md`

This document owns:

* telemetry pipeline architecture
* telemetry collection flow
* metric-to-dashboard mapping
* log/trace/event/audit operational integration
* aggregation and query model
* telemetry enrichment
* cardinality governance
* metric freshness
* telemetry reliability
* dashboard implementation model
* alerting architecture
* alert thresholds
* alert severity
* alert lifecycle
* observability SLO/SLI implementation
* telemetry quality checks
* observability failure detection
* audit/explanation retrieval layer
* observability data governance implementation
* dashboard access model
* tenant-isolated observability views
* measurement-integrity controls
* operational observability validation

Where upstream documents already define exact metric names, consume those names.

Do not create duplicate names.

---

# 4. CENTRAL EAIOC OBSERVABILITY INVARIANT

Place this prominently near the beginning of the document:

> **Every optimization decision must be measurable, and every governed decision must produce a retrievable explanation.**

The document must preserve these distinctions:

1. Measurement is mandatory.
2. Audit/explanation retrievability is mandatory where governance requires it.
3. Default end-user UI presentation of audit explanations is not universally mandatory.
4. A required audit/explanation write failure is a governance failure and follows the explicit fail-closed behavior defined by the authoritative source.
5. Optimization failure and application failure must not be conflated.
6. Telemetry failure itself must be observable.
7. Tenant boundaries must never be crossed by observability data.
8. P5 inference-serving measurements must remain separable from Layer 1/2 optimization measurements.
9. Estimated savings must never be represented as verified savings.
10. Provider-native savings must never be silently attributed to EAIOC-owned optimization.

---

# 5. DOCUMENT-SCOPED ID SERIES

Introduce exactly:

`OBS-NNN`

Use:

| ID      | Dashboard Tier       |
| ------- | -------------------- |
| OBS-001 | Executive            |
| OBS-002 | Engineering          |
| OBS-003 | Product / Operations |

Do not create additional `OBS-NNN` entries.

Do not create new:

* TECH
* DA
* AL
* AR
* CACHE
* PROV
* INFOPT
* QG
* GSP

IDs.

Cross-cutting sections do not require additional OBS IDs.

---

# 6. REQUIRED OBS-NNN ENTRY SCHEMA

Each of the three entries must contain:

1. Dashboard Tier
2. Purpose
3. Primary Audience
4. Restated Key Metrics
5. Metric-to-Pipeline Mapping
6. Canonical Upstream Metric Names
7. Required Dimensions Applied
8. Aggregation Model
9. Refresh/Freshness Characteristics
10. Alerting Requirements
11. Proposed Alert Thresholds
12. Governance Questions Answered
13. Data Sources
14. Tenant-Isolation Requirements
15. Security/Privacy Requirements
16. Data Quality Requirements
17. Failure/Fallback Behavior
18. Traceability
19. Validation Evidence
20. Source Gaps

The three entries must remain in exact order:

`OBS-001`
`OBS-002`
`OBS-003`

---

# 7. DASHBOARD REQUIREMENTS

Preserve the three architecture-defined dashboard tiers exactly.

## OBS-001 — Executive

Cover:

* Total AI spend
* Cost saved
* Savings percentage
* Model mix
* Cache utilization
* Token growth
* Quality impact

Also explain how these are calculated from existing canonical telemetry/ledger data without redefining the ledger itself.

The dashboard must distinguish:

* baseline cost
* optimized cost
* gross savings
* net savings
* estimated savings
* verified savings

when those concepts exist in upstream sources.

Never present an unverified saving as a verified saving.

## OBS-002 — Engineering

Cover:

* Tokens by stage
* Context size
* Compression ratio
* Model routing
* Tool usage
* Cache hit rate
* Latency
* Failures
* Fallbacks

Also include operational telemetry-health views where justified:

* telemetry ingestion health
* dropped telemetry
* trace continuity
* metric freshness
* audit-write failures
* event delivery failures
* collector/exporter failures

Do not invent canonical metric names where upstream names already exist.

## OBS-003 — Product / Operations

Cover:

* Cost per workflow
* Cost per user/team where permitted
* Cost per business transaction
* Quality trends
* Adoption
* ROI

Respect tenant, authorization, privacy, and data-classification constraints.

---

# 8. REQUIRED OBSERVABILITY DIMENSIONS

The architecture-defined dimensions are:

* Tenant
* Application
* Workflow
* Agent
* Model
* Provider
* User/team where permitted
* Prompt type
* Intent
* Date/time

Explain for each dimension:

* whether it is mandatory or conditional
* where it originates
* how it propagates
* whether it is suitable as a metric label
* whether it should instead be a query/grouping dimension
* privacy/security restrictions
* cardinality implications

Do not automatically turn every dimension into a high-cardinality metric label.

---

# 9. TELEMETRY PIPELINE ARCHITECTURE

Define an implementation-oriented logical pipeline.

At minimum cover:

`Request`

→ instrumentation

→ identity/correlation enrichment

→ telemetry generation

→ collection

→ validation

→ normalization

→ tenant/security filtering

→ aggregation

→ durable storage

→ query layer

→ dashboards

→ alert evaluation

→ notification/incident workflow

Also separately show:

* metrics path
* logs path
* traces path
* events path
* audit path

Explain where the paths converge and where they must remain separate.

The architecture must remain provider-agnostic.

Do not mandate a particular vendor unless explicitly supported by an authoritative source.

OpenTelemetry, Prometheus, Grafana, Kafka, etc. may be discussed only as implementation examples and must be clearly labeled non-authoritative if not source-defined.

---

# 10. METRICS MODEL

Use `interfaces.md` §19 as the canonical metric contract.

For every existing canonical metric:

* identify its producer
* identify its semantic meaning
* identify aggregation type
* identify valid dimensions
* identify dashboard consumers
* identify alerting suitability
* identify freshness expectations
* identify whether it is a counter, gauge, histogram/distribution, ratio, or derived KPI

Do not redefine the upstream metric schema.

Where a derived metric is necessary, explicitly state:

`DERIVED METRIC`

and show its source metrics and calculation.

Every formula must distinguish:

* source-defined formula
* architecture-defined formula
* proposed implementation formula

---

# 11. TOKEN / COST OBSERVABILITY

Integrate the canonical ledger defined by architecture §27.

Cover:

* raw input tokens
* sanitized tokens
* query-compressed tokens
* context tokens
* retrieved tokens
* pruned tokens
* deduplicated tokens
* compressed-context tokens
* cached/uncached tokens
* raw/optimized output
* truncated/expanded-retry output
* cache hits/misses
* model routing
* tool calls
* workflow steps
* baseline cost
* optimized cost
* optimization cost
* net savings
* savings percentage
* quality outcomes

Also cover the extended optimization economics:

* compute cost
* latency
* token usage
* calls
* gross inference savings
* net inference savings
* outcome economics
* context economics
* agent economics
* cache economics

Do not duplicate the cost model from `architecture.md`; explain how observability consumes it.

---

# 12. NET-SAVINGS / MEASUREMENT INTEGRITY

Observability must never report:

`gross reduction = net savings`

without accounting for optimization overhead.

Ensure the pipeline can distinguish:

`Baseline Cost`

`Optimized Cost`

`Optimization Overhead`

`Net Savings`

`Savings %`

and, where applicable:

`Verified Savings`

`Estimated Savings`

`Unverified Savings`

Use `EC-067` as a mandatory validation case.

---

# 13. DISTRIBUTED TRACING

Preserve the mandatory tracing requirement from `conventions.md`.

Every component invocation must produce an `OptimizationSpan`.

The trace must remain navigable through:

`request_id + span_id`

and preserve:

* parent span
* correlation ID
* tenant ID
* component identity
* event type
* timestamps
* stage identity
* outcome

Cover the canonical end-to-end chain defined by `interfaces.md`.

Explain:

* trace creation
* propagation
* span lifecycle
* child spans
* asynchronous boundaries
* retries
* cancellations
* fallbacks
* sub-agents
* tool calls
* provider calls
* terminal outcomes

---

# 14. TRACE-CONTINUITY MONITORING

Observability must monitor its own trace integrity.

Define detection for:

* missing spans
* orphan spans
* broken parent-child relationships
* missing request IDs
* cross-tenant trace contamination
* duplicate spans
* late spans
* incomplete terminal traces

Any threshold not source-defined must be:

`PROPOSED DEFAULT — VALIDATE LOCALLY`

---

# 15. LOGGING

Use the canonical log contract from `interfaces.md` and `conventions.md`.

Cover:

* required fields
* log levels
* structured logging
* correlation
* tenant propagation
* sensitive-data handling
* redaction
* sampling
* retention
* queryability
* log integrity
* audit distinction

Clearly distinguish:

**Operational Log**

from

**Audit Record**

from

**Event**

from

**Trace Span**

from

**Metric**

Do not collapse them into one generic telemetry object.

---

# 16. EVENTS

Use `interfaces.md` §23 as the canonical event contract.

Cover:

* event production
* event delivery
* durable events
* replay
* tenant isolation
* ordering where required
* duplicate delivery
* idempotency
* event retention
* event failure
* dead-letter/recovery methodology if proposed

Preserve the mandatory durable treatment of:

* AUDIT
* POLICY_VIOLATION

Do not invent additional standard event types.

---

# 17. AUDIT AND DECISION EXPLAINABILITY

Create a dedicated major section for H15 / architecture §47.16.

Cover the six mandatory decision categories:

1. Context admission/pruning
2. Model/provider selection
3. Cache reuse/rejection
4. Optimization-stage skip
5. Execution block/recovery/supersession
6. Fallback

For each explain:

* decision source
* input evidence
* rule/model/threshold considered
* resulting decision
* ownership category
* `AuditRecord` relationship
* `OptimizationSpan` relationship
* retrieval mechanism
* security/access controls
* retention
* deletion behavior

Do not redefine `AuditRecord`.

Use `interfaces.md` as the schema authority.

---

# 18. AUDIT WRITE FAILURE — EC-198

Explicitly implement the observability behavior required by EC-198.

The document must explain:

* detection
* metric emission
* log/event emission
* escalation
* effect on optimization execution
* fallback to baseline/unoptimized behavior where required
* audit trail of the failure
* alerting

Preserve the authoritative distinction:

**required audit/explanation write failure = fail-closed governance behavior**

Do not generalize this into:

"all observability failures fail closed."

That would contradict EAIOC's broader optimization fallback model.

---

# 19. RETRIEVABILITY VS UI PRESENTATION — EC-199

Explicitly preserve:

**Retrievable explanation ≠ mandatory default end-user UI presentation**

Define a proposed audit/debug query capability for retrieving a decision explanation.

Mark the exact query/API/UI shape:

`PROPOSED METHODOLOGY`

unless an authoritative interface already defines it.

---

# 20. GOVERNANCE REPORTING INTEGRITY

Explicitly address:

## EC-066

Reversibility-record write failure must remain visible.

Do not bury the condition in generic error counts.

## EC-067

Unverified savings must never appear as verified savings.

Executive reporting must structurally preserve the distinction.

## EC-070

Hidden internal module failure must be surfaced accurately.

Observability cannot compensate for upstream reporting contracts that fail silently; therefore the document must identify the dependency and validation control.

---

# 21. P5 MEASUREMENT SEPARATION

Preserve the `inference-optimization.md` requirement:

P5/inference-serving measurements use their own namespace and attribution model.

Do not silently merge:

`inference_serving.*`

with Layer 1/2 optimization savings.

A combined dashboard may exist only if it preserves explicit decomposition.

Never attribute provider/infrastructure-owned savings to EAIOC without evidence.

---

# 22. CACHE / PROVIDER / AGENT OBSERVABILITY

Consume downstream metrics from:

* `cache-strategy.md`
* `provider-matrix.md`
* `agent-optimization.md`
* `inference-optimization.md`

Do not redefine their mechanisms.

Explain how their existing metrics enter the centralized observability pipeline.

Examples include:

* cache hits/misses
* cache reuse
* cache ROI
* cache fragmentation
* provider-native cache effects
* tool ROI
* sub-agent economics
* agent progress
* inference-serving utilization

Maintain ownership boundaries.

---

# 23. QUALITY OBSERVABILITY

Integrate `quality-gates.md`.

Observability must connect optimization measurements with quality outcomes.

At minimum support:

* correctness
* task completion
* retrieval quality
* factuality
* schema compliance
* semantic equivalence
* safety
* user satisfaction
* latency
* cost

Do not redefine quality methodology.

Instead explain how quality signals become:

* metrics
* dashboard views
* alerts
* regression signals
* governance evidence

---

# 24. ALERTING ARCHITECTURE

Create a complete alerting model.

Cover:

1. metric evaluation
2. threshold evaluation
3. severity
4. deduplication
5. grouping
6. suppression
7. maintenance windows
8. notification
9. acknowledgment
10. escalation
11. resolution
12. audit trail

Distinguish:

* informational
* warning
* critical

or another severity model only if clearly marked proposed.

Do not invent a mandatory incident-management vendor.

---

# 25. ALERTING THRESHOLD DISCIPLINE

For every numeric alert threshold:

* identify source
* identify evidence
* identify whether authoritative or proposed
* identify validation method

If no authoritative threshold exists:

`PROPOSED DEFAULT — VALIDATE LOCALLY`

Never write:

* "industry standard"
* "production-safe"
* "proven threshold"
* "recommended threshold"

unless directly supported by a source.

At minimum consider proposed alert classes for:

* error/failure rate
* fallback rate
* latency
* telemetry ingestion delay
* trace continuity
* audit-write failure
* unverified savings
* quality degradation
* net-negative optimization
* cost acceleration
* cache degradation
* tool failures
* provider failures
* control-plane self-protection

---

# 26. CONTROL-PLANE SELF-OBSERVABILITY

Integrate NFR-014 / SPC requirements.

EAIOC must observe its own:

* processing latency
* telemetry overhead
* queue/backpressure
* dropped work
* optimization processing cost
* stage failures
* degradation level
* safe fallback activation
* capacity pressure

Distinguish:

**application observability**

from

**Control Plane observability**

The Control Plane must not consume so much telemetry/processing overhead that its own observability becomes an application-performance problem.

---

# 27. OBSERVABILITY OVERHEAD

Measure the cost of observability itself where practical:

* telemetry CPU
* telemetry memory
* storage cost
* network overhead
* instrumentation latency
* aggregation cost
* query cost

Any concrete threshold must be evidence-tagged.

The observability layer must not violate NFR-009:

**optimization must not consume more cost than it saves**

and must also respect NFR-014's control-plane self-protection boundary.

---

# 28. CARDINALITY GOVERNANCE

Create a dedicated section for metric cardinality.

Cover:

* high-cardinality dimensions
* tenant IDs
* user IDs
* request IDs
* agent IDs
* workflow IDs
* prompt/intent dimensions
* model/provider dimensions
* time dimensions

Define which identifiers belong in:

* metric labels
* trace attributes
* logs
* audit records
* query-time dimensions

Never use unrestricted `request_id` or similarly unique values as a metric label unless explicitly justified and marked proposed.

Define detection and remediation for cardinality explosions.

---

# 29. SAMPLING

Define the conceptual policy for:

* trace sampling
* log sampling
* metric aggregation
* audit non-sampling requirement where applicable

Do not allow sampling to destroy mandatory auditability.

Distinguish:

**performance telemetry**

from

**governance/audit telemetry**

The latter must remain complete where the authoritative requirements demand completeness.

---

# 30. TELEMETRY DATA GOVERNANCE

Integrate `security.md` and DGE requirements.

Cover:

* sensitivity classification
* PII
* secrets
* PHI/PCI where deployment scope requires them
* redaction
* encryption
* tenant isolation
* access control
* retention
* deletion/erasure
* derived telemetry
* cache/ledger/log/trace deletion propagation

Do not assert that GDPR/HIPAA/PCI-DSS universally applies.

Respect the upstream deployment-specific source gap.

---

# 31. OBSERVABILITY ACCESS CONTROL

Define the conceptual access model for:

* executive users
* engineering users
* product/operations
* security/audit users
* tenant administrators

Ensure:

* tenant isolation
* least privilege
* sensitive-data restrictions
* audit-log access auditing

Do not invent a specific IAM implementation unless explicitly supported.

---

# 32. RETENTION AND DELETION

Consume retention requirements from authoritative sources.

Do not silently replace source-defined retention periods.

For every telemetry surface explain:

* retention source
* storage class
* expiration
* deletion
* legal/governance hold where applicable
* derived-data deletion
* tenant-scoped deletion
* erasure verification

Integrate SEC-013:

deletion must propagate to every surface where persisted data exists, including logs/traces and content-bearing ledgers.

---

# 33. TELEMETRY RELIABILITY

Observability itself must be observable.

Cover:

* collector failure
* exporter failure
* storage failure
* event-bus failure
* query failure
* dashboard failure
* metric ingestion lag
* log loss
* trace loss
* audit write failure
* partial telemetry availability

Define:

* detection
* degradation behavior
* recovery
* reconciliation
* evidence of recovery

Do not claim that every telemetry failure must block application execution.

Only governance-critical failures such as required audit/explanation writes follow the explicitly defined fail-closed rule.

---

# 34. DASHBOARD DATA FRESHNESS

For each dashboard define:

* expected freshness class
* data latency
* aggregation window
* late-arriving data handling
* correction/reconciliation
* stale-data indication

Any concrete time interval not source-defined must be:

`PROPOSED DEFAULT — VALIDATE LOCALLY`

---

# 35. BASELINE VS OPTIMIZED OBSERVABILITY

Preserve the EAIOC benchmark model:

**BASELINE**

versus

**OPTIMIZED**

Observability must make it possible to compare:

* tokens
* cost
* latency
* quality
* cache behavior
* model calls
* tool calls
* agent steps
* failures
* fallbacks

Do not report optimization effectiveness without baseline context.

---

# 36. OBSERVABILITY OF OPTIMIZATION DECISIONS

For each optimization stage, answer:

* Did the stage execute?
* Why did it execute?
* Why was it skipped?
* What did it change?
* What did it cost?
* What benefit did it produce?
* What quality impact occurred?
* Did fallback occur?
* Was the result verified?
* Can the decision be reconstructed?

Do not invent new optimization stages.

Use existing EAIOC catalog identifiers.

---

# 37. CROSS-EXECUTION / AGENT OBSERVABILITY

Support:

* parent agent
* sub-agent
* workflow
* execution
* shared resource
* correlation
* supersession
* cancellation
* resume
* checkpoint
* cross-execution conflicts

Integrate XEC, ESM, CPM, RCO and related architecture components where applicable.

Do not redefine their execution-state semantics.

---

# 38. EVENTUAL CONSISTENCY AND LATE DATA

Define how dashboards and reports handle:

* delayed events
* out-of-order events
* duplicate events
* replayed events
* late traces
* corrected ledger entries
* superseded decisions

Ensure historical reports can distinguish:

* original observation
* corrected observation
* superseded observation

where required.

---

# 39. TELEMETRY QUALITY CONTROLS

Define automated checks for:

* missing required fields
* invalid tenant IDs
* broken correlation IDs
* schema-version mismatch
* malformed timestamps
* invalid metric types
* missing spans
* orphan spans
* duplicate events
* cross-tenant telemetry
* PII leakage
* stale dashboards
* metric cardinality explosion
* audit-write gaps

Map these checks to actual edge cases and interfaces.

---

# 40. OBSERVABILITY FAILURE / FALLBACK MATRIX

Create a matrix:

| Failure | Detection | Observable Signal | Application Impact | EAIOC Fallback | Governance Impact | Recovery |
| ------- | --------- | ----------------- | ------------------ | -------------- | ----------------- | -------- |

Include at minimum:

* metrics unavailable
* logs unavailable
* traces unavailable
* event bus unavailable
* audit write unavailable
* dashboard unavailable
* telemetry collector unavailable
* storage unavailable
* malformed telemetry
* tenant metadata missing
* correlation failure

Do not make unsupported fail-closed claims.

---

# 41. OBSERVABILITY SLI / SLO MODEL

Define a proposed SLI/SLO framework for:

* telemetry availability
* telemetry freshness
* trace completeness
* audit durability
* event durability
* metric correctness
* dashboard availability
* alert delivery

Every numeric SLO that is not source-defined must be:

`PROPOSED DEFAULT — VALIDATE LOCALLY`

The document must explicitly distinguish:

**source-defined availability requirements**

from

**proposed observability-specific SLOs**.

---

# 42. DASHBOARD / ALERT TRACEABILITY

Every important dashboard KPI and alert must trace to:

* source metric
* interface
* architecture requirement
* edge case where applicable
* scenario where applicable
* owner
* validation method

No unexplained KPI should appear in the canonical dashboard specification.

---

# 43. SECURITY INTEGRATION

The approved `security.md` is authoritative.

Do not redefine:

* GSP components
* CIS
* TMG
* DGE
* HAG
* SGE

Instead explain how their security/governance decisions become observable.

Security-sensitive observability must preserve:

* tenant isolation
* classification
* redaction
* access control
* retention
* deletion
* auditability

---

# 44. SOURCE GAPS

Use:

`SOURCE-GAP-OBS-NN`

For every missing authoritative fact that materially affects implementation.

Examples may include:

* absence of a dedicated observability scenario domain
* unspecified alert threshold
* unspecified telemetry backend
* unspecified cardinality limits
* unspecified dashboard refresh intervals
* unspecified observability SLOs

Do not silently invent missing facts.

---

# 45. SOURCE CONTRADICTIONS

Use:

`CONTRA-OBS-NN`

Every contradiction must contain:

1. contradiction ID
2. source A
3. source B
4. exact affected requirement
5. why they conflict
6. current treatment
7. whether downstream implementation is blocked

Never rewrite upstream documents.

---

# 46. SCENARIO COVERAGE

Actually inspect `scenario-matrix.md`.

Determine whether:

`SCN-OBS-*`

exists.

If it exists:

* enumerate the relevant scenarios
* map them to OBS sections

If it does not exist:

* state explicitly that no dedicated observability scenario domain exists
* do not fabricate one
* use actual observability-related scenarios from other domains
* record the finding appropriately

---

# 47. EDGE-CASE COVERAGE

At minimum verify:

* EC-066
* EC-067
* EC-070
* EC-076
* EC-198
* EC-199

Also search for all other observability/audit/telemetry-related edge cases.

Do not rely solely on this prompt's summary.

---

# 48. REQUIRED DOCUMENT STRUCTURE

Generate the document with this structure:

1. Header / Document Metadata
2. Status and Maturity
3. How to Read This Document
4. Scope Boundary
5. Central Observability Invariant
6. Observability Architecture
7. Telemetry Types and Responsibilities
8. Metric Pipeline
9. Logging Pipeline
10. Distributed Tracing Pipeline
11. Event Pipeline
12. Audit / Explainability Pipeline
13. Telemetry Data Model
14. Required Dimensions
15. Cardinality Governance
16. Sampling
17. Telemetry Data Governance
18. Tenant Isolation
19. Retention and Deletion
20. Telemetry Reliability
21. Control-Plane Self-Observability
22. Token / Cost Measurement
23. Optimization Measurement Integrity
24. Quality Observability
25. P5 Measurement Separation
26. Dashboard Model
27. OBS-001 Executive Dashboard
28. OBS-002 Engineering Dashboard
29. OBS-003 Product / Operations Dashboard
30. Alerting Architecture
31. Alert Thresholds
32. SLO / SLI Model
33. Decision Explainability
34. Governance Reporting Integrity
35. Failure / Fallback Matrix
36. Telemetry Quality Validation
37. Cross-Document Boundary Discipline
38. Scenario Coverage
39. Edge-Case Coverage
40. Cross-Document Coverage Matrix
41. Source Gaps
42. Source Contradictions
43. Final Report
44. Observability Readiness

---

# 49. CROSS-DOCUMENT COVERAGE MATRIX

Create a comprehensive matrix covering at minimum:

| Requirement | Source | Observability Section | Evidence | Status |
| ----------- | ------ | --------------------- | -------- | ------ |

Cover:

* NFR-006
* NFR-010
* NFR-014
* OBJ-011
* AC-052
* H15
* SEC-011–016 where observability is affected
* relevant ECs
* relevant scenarios
* relevant interface contracts

Do not claim coverage merely because a term appears.

The observability document must actually explain the implementation responsibility.

---

# 50. DOCUMENT QUALITY REQUIREMENTS

The final document must be:

* enterprise-grade
* technically rigorous
* implementation-oriented
* security-aware
* tenant-isolation-aware
* observable
* testable
* traceable
* provider-neutral
* evidence-aware
* explicit about uncertainty
* compatible with the existing EAIOC architecture

Avoid generic statements such as:

"Use monitoring."

"Create dashboards."

"Set alerts."

"Use distributed tracing."

Every such statement must become an EAIOC-specific implementation requirement or proposed methodology.

---

# 51. NO SCOPE CREEP

Do not:

* redefine interfaces
* redesign security
* redesign quality gates
* redesign caching
* redesign agent optimization
* redesign inference optimization
* create new optimization techniques
* create new DA modules
* create new architecture components
* invent provider capabilities
* invent regulatory requirements
* generate `eval.md`
* generate `SCALING.md`
* modify upstream documents

Only create:

`docs/observability.md`

and, if necessary under the repository workflow, the prompt file itself.

---

# 52. VALIDATION CHECKLIST

Before declaring completion, verify all of the following.

* [ ] `OBS-001` exists exactly once
* [ ] `OBS-002` exists exactly once
* [ ] `OBS-003` exists exactly once
* [ ] Three dashboard tiers match architecture §31.1
* [ ] Required dimensions match architecture §31.2
* [ ] Governance questions match architecture §31.3
* [ ] Interfaces §19 schemas were consumed, not redefined
* [ ] Interfaces §23 events were consumed, not redefined
* [ ] Mandatory distributed tracing is preserved
* [ ] `request_id + span_id` trace navigation is preserved
* [ ] Tenant isolation is addressed throughout
* [ ] PII/sensitive-data handling is addressed
* [ ] Retention/deletion is addressed
* [ ] EC-066 verified directly
* [ ] EC-067 verified directly
* [ ] EC-070 verified directly
* [ ] EC-076 verified directly
* [ ] EC-198 verified directly
* [ ] EC-199 verified directly
* [ ] Scenario-matrix was actually checked for `SCN-OBS-*`
* [ ] No scenario was fabricated
* [ ] P5 measurement separation is preserved
* [ ] Estimated vs verified savings is preserved
* [ ] Gross vs net savings is preserved
* [ ] Control-plane self-observability is covered
* [ ] Cardinality governance is covered
* [ ] Telemetry reliability is covered
* [ ] Alert lifecycle is covered
* [ ] All proposed numerical thresholds are evidence-tagged
* [ ] No unsupported threshold is called validated
* [ ] Source gaps are explicitly recorded
* [ ] Source contradictions are explicitly recorded
* [ ] Cross-document ownership is respected
* [ ] No upstream document was modified
* [ ] No downstream document was generated
* [ ] Final report is present
* [ ] `## Observability Readiness` is present
* [ ] Readiness verdict occurs exactly once
* [ ] Readiness verdict is the true final line of the file

---

# 53. FINAL REPORT

The final report must include:

* document status
* version
* maturity level
* OBS entry count
* dashboard coverage
* metric coverage
* interface coverage
* event coverage
* audit/explainability coverage
* edge-case coverage
* scenario coverage
* requirement coverage
* source-gap count
* contradiction count
* proposed-threshold count
* validation result
* known limitations
* explicit `SCN-OBS-*` existence/non-existence finding

Do not hide gaps.

---

# 54. READINESS VERDICT

The document must end with:

`## Observability Readiness`

State whether the document is ready for the next documentation phase.

Use:

`READY FOR NEXT DOCUMENTATION PHASE`

only when all validation criteria pass and no blocking source gap/contradiction remains.

If not ready, provide the honest blocking reason and use an appropriate alternative verdict.

The readiness verdict must occur **exactly once**.

The readiness verdict must be the **true final line of the file**.

Nothing may follow it.

After generating the file, directly inspect the final lines of `docs/observability.md` to verify this condition.

---

# 55. EXECUTION ORDER

Execute in this exact order:

1. Confirm the current repository state.
2. Confirm whether `docs/observability.md` already exists.
3. Read all authoritative sources.
4. Verify actual document versions.
5. Extract observability requirements and ownership boundaries.
6. Extract all relevant interface contracts.
7. Extract all observability/audit edge cases.
8. Search the scenario matrix for `SCN-OBS-*`.
9. Extract existing metrics from downstream completed documents.
10. Build the telemetry ownership map.
11. Build the metric-to-dashboard mapping.
12. Build the telemetry pipeline architecture.
13. Build the audit/explainability pipeline.
14. Build governance-reporting integrity controls.
15. Build the alerting architecture.
16. Define proposed thresholds only where necessary.
17. Build SLI/SLO methodology.
18. Build failure/fallback matrix.
19. Build telemetry-quality validation.
20. Build coverage matrix.
21. Record source gaps.
22. Record source contradictions.
23. Write `docs/observability.md`.
24. Validate all requirements.
25. Inspect the actual final lines.
26. Confirm no upstream/downstream document was modified.
27. Produce the Final Report.
28. End with the single readiness verdict.

---

# 56. FINAL INSTRUCTION

Create **ONLY**:

`docs/observability.md`

Do not modify any authoritative source document.

Do not create `eval.md`.

Do not create `SCALING.md`.

Do not invent missing authoritative facts.

Do not redefine upstream interfaces, security controls, quality methodology, cache mechanics, agent mechanics, or inference mechanics.

Use the latest authoritative EAIOC documents.

When sources disagree:

**record the contradiction; do not silently resolve it.**

When the repository does not specify a threshold:

**propose it only as `PROPOSED DEFAULT — VALIDATE LOCALLY`.**

When a scenario does not exist:

**say so; never fabricate one.**

When a metric already exists upstream:

**consume and reference it; do not create a competing metric name.**

The final document must be implementation-oriented while remaining faithful to the EAIOC architecture and documentation chain.

**Begin by verifying the current `docs/` state, then read the authoritative documents before writing `docs/observability.md`.**
