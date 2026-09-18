# EAIOC — V2 Consolidated Master Prompt for `implementation-plan.md`

## 0. ROLE

You are the documentation engineer responsible for generating the next downstream artifact for:

**Enterprise Agent & LLM Inference Optimization Control Plane (EAIOC)**

Generate exactly:

`docs/implementation-plan.md`

Repository maturity:

**LEVEL 0 — RESEARCH / PRE-IMPLEMENTATION**

This document must convert the approved EAIOC design/documentation chain into a **deep, dependency-aware, capability-level implementation plan**.

It must be sufficiently detailed that an engineering team can understand:

* what must be implemented;
* why it is implemented in that order;
* what each capability depends upon;
* what implementation steps are required;
* what cross-cutting controls must be applied;
* how each capability is integrated;
* how each capability is validated;
* how failures/recovery are validated;
* what constitutes completion of each capability;
* what gates must pass before moving forward.

This is **not source code** and is not a production implementation.

---

# 1. DOCUMENT-CHAIN POSITION

The approved chain is:

1. Problem Statement
2. Engineering Specification
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
13. `quality-gates.md`
14. `security.md`
15. `observability.md`
16. `eval.md`
17. `SCALING.md`
18. **`implementation-plan.md` ← THIS DOCUMENT**
19. `requirements-traceability.md`
20. ADRs

This document is the **last major planning artifact before requirements traceability and ADRs**.

Do not cascade implementation decisions into those future documents.

---

# 2. PRIMARY SOURCE-DEFINED IMPLEMENTATION AXIS

Unlike `SCALING.md`, this document already has a source-defined implementation sequencing axis.

`architecture.md §36` and `conventions.md §25` define:

* **P0 — Foundation**
* **P1 — High-Confidence Optimization**
* **P2 — Cost Intelligence**
* **P3 — Advanced Optimization**
* **P4 — Control-Plane Intelligence**
* **P5 — Infrastructure-Dependent / Optional**

These six tiers are the canonical build-sequencing axis for this document.

**Do not replace P0–P5 with an invented competing top-level numbering system.**

You may use workstream/capability numbering beneath P0–P5.

---

# 3. CRITICAL DISTINCTION: THREE DIFFERENT PRIORITY CONCEPTS

The document must explicitly distinguish:

### Implementation Priority

`architecture.md §36 / conventions.md §25`

Meaning:

> when a capability should be built relative to other capabilities.

### Maturity Level

`optimization-catalog.md`

Meaning:

> how validated/mature the capability is.

### Scenario/Test Priority

`scenario-matrix.md`

Meaning:

> testing/scenario importance.

These are three independent concepts.

In particular:

**P0 does NOT mean more validated than P4.**

All implementation-plan work remains Level 0 unless actual source evidence establishes otherwise.

---

# 4. SOURCE GAP THAT THIS DOCUMENT MUST RESOLVE

`optimization-catalog.md` contains:

`SOURCE-GAP-OPTCAT-01`

The 25 `DA-NNN` modules do not have individually source-assigned implementation priority tiers.

This implementation plan must explicitly resolve this gap.

For every:

`DA-001` through `DA-025`

provide either:

1. an explicit proposed P0–P5 assignment with rationale; or
2. a documented reason why a defensible assignment cannot yet be made.

If a new assignment is made here and is not source-defined, mark it:

`PROPOSED DEFAULT — VALIDATE LOCALLY`

Do not silently inherit the catalog's best-effort inference and present it as authoritative.

---

# 5. SECOND SOURCE GAP

Review:

`SOURCE-GAP-AGENTOPT-01`

from `agent-optimization.md`.

This concerns concrete per-phase policy values for the six `architecture.md §15.7` session phases.

The implementation plan must explicitly state:

* whether this document resolves/narrows the gap;
* if yes, exactly what is being proposed and why;
* if no, why implementation-plan is not the appropriate place to resolve it;
* what remains unresolved;
* whether the unresolved item blocks any implementation capability.

Do not invent policy values simply to make the implementation plan appear complete.

---

# 6. REQUIRED HEADER

Use:

```text
# Enterprise Agent & LLM Inference Optimization Control Plane — Implementation Plan

**Document ID:** EAIOC-IMPL-001
**Status:** PRE-IMPLEMENTATION — Level 0 (RESEARCH)
**Version:** 1.0.0
**Generated:** <repository date>
**Generation prompt:** `docs/prompts/generate-implementation-plan.prompt.md`
```

Then state:

* documentation-chain position;
* upstream readiness assumption;
* sources read;
* scope boundary.

---

# 7. EVIDENCE LABELS

Use the established evidence vocabulary:

* `SOURCE-DEFINED`
* `SOURCE-DERIVED`
* `PROPOSED METHODOLOGY`
* `PROPOSED IMPLEMENTATION`
* `PROPOSED DEFAULT — VALIDATE LOCALLY`
* `DERIVED`
* `ILLUSTRATIVE EXAMPLE`
* `UNVERIFIED`
* `SOURCE-GAP`

Never present a proposed implementation decision as source-defined.

---

# 8. SCOPE BOUNDARY

This document owns:

* P0–P5 build sequencing;
* inter-tier dependency ordering;
* intra-tier dependency ordering;
* capability-level implementation decomposition;
* implementation prerequisites;
* implementation gates;
* CI/CD/build-pipeline sequencing;
* `DA-NNN` priority-resolution;
* implementation-specific risk/dependency analysis.

This document does NOT own:

* technique schemas;
* `TECH-NNN` schemas;
* `DA-NNN` schemas;
* P5-Index schemas;
* optimization mechanics;
* rollout-phase mechanics;
* deployment-mode capacity;
* scaling methodology;
* validation/statistical methodology;
* observability taxonomy;
* requirements-traceability ownership;
* ADR ownership.

Cite the owning document instead of redefining it.

---

# 9. MANDATORY IMPLEMENTATION DEPTH

This is the most important requirement.

Do NOT produce a shallow roadmap of:

`Phase → component → bullets`.

The implementation plan must use:

```text
P0–P5 Phase
    ↓
Workstream
    ↓
Capability
    ↓
Capability Implementation Lifecycle
    ↓
Capability Gate
    ↓
Next Capability
```

Every independently implementable capability must be decomposed to sufficient depth to explain its actual implementation path.

---

# 10. MANDATORY CAPABILITY LIFECYCLE

For each major independently implementable capability, use this lifecycle:

```text
Capability
    ↓
Sub-phase A — Contract & Design
    ↓
Sub-phase B — Minimal Implementation
    ↓
Sub-phase C — Integration
    ↓
Sub-phase D — Observability
    ↓
Sub-phase E — Security / Governance
    ↓
Sub-phase F — Quality Validation
    ↓
Sub-phase G — Scenario Validation
    ↓
Sub-phase H — Failure / Recovery Validation
    ↓
Capability Gate
    ↓
Next Capability
```

This lifecycle is the **required minimum conceptual structure**, not a requirement that every tiny technical task contain all eight sub-phases independently.

Where a sub-phase is genuinely not applicable:

* explicitly state `NOT APPLICABLE`;
* give the reason;
* do not silently omit it.

---

# 11. CAPABILITY IMPLEMENTATION TEMPLATE

For every substantial capability, use a structure equivalent to:

```text
### X.Y Capability Name

Evidence: SOURCE-DEFINED / SOURCE-DERIVED / PROPOSED IMPLEMENTATION

Objective:
...

Source Requirements:
...

Architecture Components:
...

Interface Dependencies:
...

Upstream Mechanism:
...

Prerequisites:
...

Dependencies:
...

#### X.Y.1 Contract & Design
- ...

#### X.Y.2 Minimal Implementation
- Step 1
- Step 2
- Step 3
- ...
- Step N

#### X.Y.3 Integration
- ...

#### X.Y.4 Observability
- ...

#### X.Y.5 Security / Governance
- ...

#### X.Y.6 Quality Validation
- ...

#### X.Y.7 Scenario Validation
- ...

#### X.Y.8 Failure / Recovery Validation
- ...

#### X.Y.9 Capability Gate

Entry Criteria:
...

Exit Criteria:
...

Blocking Conditions:
...

Evidence:
...
```

The actual implementation steps must be sufficiently concrete to communicate engineering sequencing.

---

# 12. MINIMAL IMPLEMENTATION MUST BE REAL

Avoid meaningless steps such as:

* "implement feature";
* "write code";
* "integrate component";
* "test feature".

Instead decompose the capability into meaningful engineering units.

For example:

```text
4.2 Semantic Cache

4.2.1 Contract & Design
4.2.2 Minimal Implementation
    4.2.2.1 Tenant-scoped key construction
    4.2.2.2 Cache namespace resolution
    4.2.2.3 Similarity-index adapter
    4.2.2.4 Retrieval path
    4.2.2.5 Freshness evaluation
    4.2.2.6 Invalidation path
    4.2.2.7 Miss fallback
4.2.3 Integration
4.2.4 Observability
4.2.5 Security / Governance
4.2.6 Quality Validation
4.2.7 Scenario Validation
4.2.8 Failure / Recovery Validation
4.2.9 Capability Gate
```

This example is illustrative only.

Do not invent unsupported implementation details.

---

# 13. PHASE STRUCTURE

Use the source-defined P0–P5 tiers as the top-level build sequence.

The document must contain:

```text
P0 — Foundation
P1 — High-Confidence Optimization
P2 — Cost Intelligence
P3 — Advanced Optimization
P4 — Control-Plane Intelligence
P5 — Infrastructure-Dependent / Optional
```

For each tier include:

* exact source-defined item list;
* tier objective;
* entry criteria;
* prerequisites;
* intra-tier ordering;
* inter-tier dependencies;
* capability breakdown;
* validation expectations;
* exit gate;
* blocking conditions.

Do not merely copy §36.

Explain **why** the order exists.

---

# 14. P0 — FOUNDATION

Determine the exact current P0 items from `architecture.md §36` and `conventions.md §25`.

For every substantial P0 capability:

* create a capability-level breakdown;
* define contracts/design;
* define minimal implementation;
* define integration;
* define observability;
* define security/governance;
* define quality;
* define scenario validation;
* define failure/recovery;
* define capability gate.

P0 must establish the prerequisites required by later tiers.

Do not automatically call every P0 item "implemented first" without documenting dependency rationale.

---

# 15. P1 — HIGH-CONFIDENCE OPTIMIZATION

Read every relevant `TECH-NNN` implementation priority and dependency note from `optimization-catalog.md`.

Do not simply reproduce the catalog.

Construct an actual implementation order based on dependencies.

For example, where a technique requires:

* token accounting;
* authorization;
* freshness;
* baseline quality;
* cache infrastructure;
* provider abstraction;

those prerequisites must appear earlier in the implementation sequence.

Every substantial P1 capability must receive the deep lifecycle.

Respect:

`conventions.md §25`

that P1 features must pass the validation matrix before being enabled by default.

---

# 16. P2 — COST INTELLIGENCE

Determine the exact source-defined P2 items.

Explicitly model dependencies on:

* cost accounting;
* model/provider information;
* quality baselines;
* optimization overhead;
* cache state;
* provider limits;
* evaluation infrastructure.

Do not assume "cost optimization" is implementable before the measurement/accounting infrastructure it depends upon.

Every substantial P2 capability receives the capability lifecycle.

---

# 17. P3 — ADVANCED OPTIMIZATION

Determine the exact source-defined P3 items.

Model dependencies on:

* P0 foundation;
* P1 capabilities;
* P2 cost/benefit intelligence;
* evaluation;
* quality gates;
* provider/model capabilities;
* agent/context infrastructure.

Explicitly identify capabilities that require evidence from earlier optimization tiers.

Do not treat P3 as merely "more optimization."

---

# 18. P4 — CONTROL-PLANE INTELLIGENCE

Determine the exact source-defined P4 items.

Pay special attention to:

* adaptive optimization;
* policy intelligence;
* learned decisioning;
* feedback loops;
* dynamic budgets;
* control-plane self-protection;
* intelligence that depends on accumulated evaluation evidence.

Do not allow P4 intelligence to bypass:

* security;
* governance;
* quality gates;
* tenant isolation;
* deterministic fallback.

Every substantial P4 capability receives the deep lifecycle.

---

# 19. P5 — INFRASTRUCTURE-DEPENDENT / OPTIONAL

P5 must remain separate from P0–P4 measurement and validation.

Respect:

`conventions.md §25`

and the source-defined P5 measurement-separation rule.

For every P5 item:

* identify infrastructure dependency;
* identify prerequisite P0–P4 capabilities;
* identify additional validation requirements;
* identify optionality;
* identify whether it is currently implementable;
* identify what external infrastructure is required.

Do not merge P5 performance measurements with P0–P4 optimization measurements.

---

# 20. `DA-NNN` PRIORITY RESOLUTION

Explicitly cover all 25:

`DA-001` through `DA-025`.

For each:

```text
DA-NNN
Current Source Position:
Proposed Priority:
Evidence:
Rationale:
Dependencies:
Required Validation:
Blocking Status:
```

If proposed:

`Evidence: PROPOSED DEFAULT — VALIDATE LOCALLY`

Do not assign a tier solely because another technique "looks similar."

Use:

* dependencies;
* prerequisites;
* architecture;
* technique behavior;
* implementation readiness;
* validation requirements;

as the rationale.

If some cannot be defensibly assigned, explicitly explain why.

---

# 21. CAPABILITY GATE

Every substantial capability must end with a Capability Gate.

The gate must answer:

### Entry Criteria

What must exist before implementation begins?

### Implementation Completion

What must be built?

### Integration Completion

What must integrate successfully?

### Security/Governance Completion

What must be proven?

### Quality Completion

What quality requirements must pass?

### Scenario Completion

Which scenarios validate it?

### Failure/Recovery Completion

Which failure modes must be validated?

### Exit Criteria

What allows the next dependent capability to begin?

### Blocking Conditions

What prevents promotion?

A capability gate is a **build/validation gate**, not a claim that the capability is production-ready.

---

# 22. CROSS-CUTTING CAPABILITY CHECKLIST

For each capability, inspect applicability of:

* tenant isolation;
* authorization;
* PII;
* governance;
* spend;
* observability;
* quality;
* evaluation;
* scaling;
* concurrency;
* caching;
* provider behavior;
* agent behavior;
* rollback;
* failure recovery.

Do not mechanically duplicate irrelevant controls.

---

# 23. DEPENDENCY GRAPH

Construct:

### Inter-tier dependencies

Example:

```text
P0 Foundation
    ↓
P1 Optimization Infrastructure
    ↓
P2 Cost Intelligence
    ↓
P3 Advanced Optimization
    ↓
P4 Control-Plane Intelligence
```

But derive the actual dependency graph from the repository.

### Intra-tier dependencies

For each tier, show which capabilities must precede others.

### Cross-domain dependencies

Explicitly identify dependencies such as:

```text
Provider abstraction
        ↓
Model routing
        ↓
Cost intelligence
        ↓
Learned routing
```

or:

```text
Tenant identity
        ↓
Tenant-scoped cache
        ↓
Semantic cache
```

where supported by source evidence.

---

# 24. CI/CD AND BUILD PIPELINE

Implementation-plan owns CI/CD/build sequencing.

Define the required progression for:

* source validation;
* formatting/static analysis;
* unit tests;
* contract tests;
* integration tests;
* security tests;
* quality tests;
* scenario tests;
* regression/evaluation;
* performance/capacity tests;
* packaging;
* deployment validation.

Do not invent a specific CI platform unless already source-defined.

Explain what gates must exist before a capability can merge/promote.

---

# 25. ROLLOUT VS IMPLEMENTATION PRIORITY

Explicitly distinguish:

### Build axis

`P0 → P1 → P2 → P3 → P4 → P5`

from:

### Rollout/maturity axis

`OBSERVE → BENCHMARK → CONTROLLED PILOT → PRODUCTION → GOVERNANCE → CONTINUOUS OPTIMIZATION`

These are orthogonal.

A P1 capability can be built early but still progress through the rollout lifecycle later.

Do not duplicate `eval.md` rollout mechanics; cite them.

---

# 26. SCENARIO-MATRIX P0–P3 COLLISION

Explicitly verify and document that:

`scenario-matrix.md` P0–P3

is a **scenario/test-priority scale**.

It is NOT the same as:

`architecture.md §36` P0–P5

which is the **implementation/build-sequencing scale**.

Search the scenario matrix and determine whether any scenario actually validates implementation sequencing itself.

If none exists, state that honestly.

Do not force runtime scenarios onto this planning artifact.

---

# 27. EDGE-CASE COVERAGE

Search the complete edge-case catalog.

For each implementation-relevant edge case:

* identify affected capability;
* implementation obligation;
* validation obligation;
* failure/recovery expectation;
* blocking status.

At minimum investigate:

* `EC-077`;
* `EC-191`;
* `EC-192`;
* `EC-193`;
* `EC-194`;

but do not limit the search to those.

---

# 28. QUALITY / EVALUATION GATES

Consume:

* `quality-gates.md`;
* `eval.md`.

Do not redefine their methodologies.

For each optimization capability, identify:

* quality baseline dependency;
* validation dependency;
* regression dependency;
* promotion gate.

Respect the principle:

> an optimization is not considered successfully implemented merely because it reduces tokens/cost; required quality behavior must also pass.

---

# 29. SECURITY / GOVERNANCE GATES

Consume `security.md`.

No capability may be promoted if it can:

* bypass authorization;
* skip PII protection;
* bypass governance;
* violate tenant isolation;
* disable mandatory security stages.

The implementation sequence must never create an architectural path where optimization happens outside mandatory governance boundaries.

---

# 30. OBSERVABILITY

Consume `observability.md`.

Every substantial capability must identify:

* telemetry requirement;
* important events;
* metrics;
* tracing;
* audit requirements;
* tenant dimensions where applicable.

Do not create a competing observability catalog.

---

# 31. SCALING

Consume `SCALING.md`.

Identify implementation dependencies for:

* capacity instrumentation;
* queue behavior;
* backpressure;
* load shedding;
* admission control;
* horizontal scaling;
* tenant fairness;
* recovery;
* capacity validation.

Do not redefine SCALING methodology.

Do not turn proposed thresholds into source-defined requirements.

---

# 32. FAILURE / RECOVERY

Every substantial capability must identify:

* expected failure modes;
* deterministic fallback;
* dependency failure;
* timeout/cancellation;
* rollback;
* recovery;
* interaction with governance/security.

Preserve:

**fail-open for optimization where permitted; fail-closed where continuing would bypass mandatory security/authorization/PII controls.**

---

# 33. STATE / DATA DEPENDENCIES

Identify required state for:

* execution;
* context;
* cache;
* policy;
* governance;
* XEC;
* evaluation;
* observability;
* tenant isolation.

Do not invent storage technologies.

Describe required behavior and dependency only.

---

# 34. IMPLEMENTATION DECISION REGISTER

Identify unresolved implementation choices.

For each:

```text
Decision ID:
Topic:
Source Position:
Open Question:
Current Options:
Proposed Direction:
Evidence:
Blocking:
ADR Candidate:
```

Do not invent technology selections.

---

# 35. ADR HANDOFF

Identify implementation/architecture questions that should become ADRs.

Do not create ADR files.

Examples may include:

* storage;
* messaging;
* provider abstraction;
* cache infrastructure;
* deployment;
* coordination;
* observability infrastructure;
* scaling infrastructure.

---

# 36. IMPLEMENTATION RISK REGISTER

For each risk:

```text
Risk ID:
Description:
Affected Capability:
Source:
Dependency:
Impact:
Mitigation:
Validation:
Blocking:
Status:
```

Include risks from:

* source gaps;
* interface instability;
* provider dependencies;
* scaling;
* concurrency;
* tenant isolation;
* security;
* evaluation;
* observability;
* quality;
* implementation sequencing.

---

# 37. REQUIREMENT → IMPLEMENTATION TRACEABILITY

Within this document, provide a planning-level matrix:

```text
Requirement
    ↓
Architecture
    ↓
Interface
    ↓
Mechanism
    ↓
Implementation Capability
    ↓
Validation
    ↓
Capability Gate
```

Include relevant:

* NFR;
* OBJ;
* AC;
* INTF;
* EC;
* SCN;
* TECH;
* DA;
* P5 IDs.

Do not pretend this replaces the future `requirements-traceability.md`.

---

# 38. IMPLEMENTATION COMPLETENESS

Evaluate completeness across:

* specification;
* architecture;
* interfaces;
* foundation;
* optimization;
* security;
* governance;
* quality;
* evaluation;
* observability;
* concurrency;
* scaling;
* tenant isolation;
* resilience;
* deployment;
* rollback.

A capability is not complete merely because implementation steps exist.

It needs its validation and capability gate.

---

# 39. SOURCE GAPS

Create:

`SOURCE-GAP-IMPL-NN`

only for genuine implementation-plan gaps.

Explicitly disposition:

* `SOURCE-GAP-OPTCAT-01`;
* `SOURCE-GAP-AGENTOPT-01`.

Do not duplicate them without explaining their implementation-plan disposition.

---

# 40. SOURCE CONTRADICTIONS

Search for genuine contradictions.

If found:

`CONTRA-IMPL-NN`

If none:

`Source Contradictions: 0`

Do not treat different abstraction levels as contradictions.

---

# 41. CROSS-DOCUMENT BOUNDARY MATRIX

Include:

| Document | Consumed | Implementation Impact | Not Redefined |
| -------- | -------- | --------------------- | ------------- |

At minimum:

* architecture.md
* interfaces.md
* conventions.md
* edge-cases.md
* scenario-matrix.md
* optimization-catalog.md
* provider-matrix.md
* cache-strategy.md
* agent-optimization.md
* inference-optimization.md
* quality-gates.md
* security.md
* observability.md
* eval.md
* SCALING.md

---

# 42. IMPLEMENTATION PLAN MASTER STRUCTURE

The final document should approximately follow:

```text
1. Status / Maturity
2. How to Read
3. Scope Boundary
4. Implementation Priority vs Maturity vs Scenario Priority
5. Implementation Principles
6. Implementation Readiness Model
7. Canonical P0–P5 Decision
8. Dependency Model
9. P0 — Foundation
10. P1 — High-Confidence Optimization
11. P2 — Cost Intelligence
12. P3 — Advanced Optimization
13. P4 — Control-Plane Intelligence
14. P5 — Infrastructure-Dependent / Optional
15. DA-NNN Priority Resolution
16. Session-Phase Values Disposition
17. CI/CD / Build Pipeline
18. Rollout vs Build-Priority Boundary
19. Capability Gate Model
20. Quality / Evaluation Gates
21. Security / Governance Gates
22. Observability Dependencies
23. Scaling Dependencies
24. Failure / Recovery
25. Scenario Coverage
26. Edge-Case Coverage
27. Requirement-to-Implementation Traceability
28. Implementation Decision Register
29. ADR Candidates
30. Risk Register
31. Source Gaps
32. Source Contradictions
33. Cross-Document Boundary Matrix
34. Final Report
35. Implementation Plan Readiness
```

The exact numbering may change if necessary, but the substantive content must remain.

---

# 43. FINAL REPORT

End with:

* Document ID;
* version;
* maturity;
* P0–P5 item counts;
* workstream count;
* capability count;
* implementation-unit count;
* capability-gate count;
* `DA-NNN` resolution status;
* scenarios consumed;
* edge cases consumed;
* source gaps;
* contradictions;
* open implementation decisions;
* ADR candidates;
* CI/CD sequencing status;
* file-scope confirmation;
* next document.

Do not inflate counts.

---

# 44. READINESS VERDICT

The final readiness section must answer:

1. Is the P0–P5 sequencing sufficiently defined?
2. Are intra-tier dependencies sufficiently defined?
3. Are major capabilities decomposed to implementation depth?
4. Does every substantial capability have a capability gate?
5. Are security/governance gates explicit?
6. Are quality/evaluation gates explicit?
7. Are scenario/failure/recovery validations explicit?
8. Were all 25 `DA-NNN` modules dispositioned?
9. Was `SOURCE-GAP-AGENTOPT-01` explicitly dispositioned?
10. Are unresolved implementation decisions clearly identified?
11. Are any actual blockers present?

Do not claim implementation readiness where source evidence does not support it.

---

# 45. NO FABRICATION

Never invent:

* production implementation;
* production performance;
* benchmark results;
* staffing;
* timelines;
* sprint numbers;
* delivery dates;
* technology selections;
* cloud infrastructure;
* provider limits;
* database choices;
* message brokers;
* deployment tooling;
* test results.

If implementation detail is required but unspecified upstream:

`PROPOSED IMPLEMENTATION`

or:

`SOURCE-GAP`

depending on whether a reasonable proposal can be made.

---

# 46. FILE-SCOPE RULE

Only:

`docs/implementation-plan.md`

may be created or modified.

Do not modify:

* `CLAUDE.md`;
* `architecture.md`;
* `optimization-catalog.md`;
* `agent-optimization.md`;
* `interfaces.md`;
* any other document.

Even when this document resolves a gap opened by another document, resolve it **here**, not by changing the source document.

---

# 47. ID VERIFICATION

Every ID must be independently verified against its live source.

Verify:

* NFR;
* OBJ;
* AC;
* INTF;
* EC;
* SCN;
* TECH;
* DA;
* P5;
* SOURCE-GAP;
* CONTRA.

Do not trust copied IDs from this prompt.

---

# 48. FINAL SELF-AUDIT

Before delivery:

### Source

* all mandatory sources reviewed;
* all important IDs verified;
* current P0–P5 lists verified.

### Sequencing

* P0–P5 retained;
* inter-tier dependencies documented;
* intra-tier dependencies documented.

### Depth

* major capabilities decomposed;
* meaningful implementation steps exist;
* integration exists;
* observability exists;
* security/governance exists;
* quality exists;
* scenario validation exists;
* failure/recovery exists;
* capability gates exist.

### Gap Resolution

* all 25 DA modules dispositioned;
* session-phase gap explicitly dispositioned.

### Boundaries

* rollout ≠ implementation priority;
* scenario priority ≠ implementation priority;
* maturity ≠ implementation priority;
* no ownership boundary violated.

### Safety

* governance cannot be bypassed;
* tenant isolation preserved;
* fail-open/fail-closed discipline preserved.

### Maturity

* no production evidence invented;
* proposed implementation decisions labeled.

### File Scope

* only `docs/implementation-plan.md` changed.

---

# 49. VERIFICATION COMMANDS

Before delivery:

```text
grep -n "READY FOR NEXT DOCUMENTATION PHASE" docs/implementation-plan.md
```

Confirm exactly one occurrence and that it is the literal final line.

Verify all:

```text
DA-001 ... DA-025
```

have explicit dispositions.

Verify all referenced:

```text
TECH-NNN
DA-NNN
P5-*
NFR-*
OBJ-*
AC-*
INTF-*
EC-*
SCN-*
SOURCE-GAP-*
CONTRA-*
```

against their live source files.

Verify:

```text
git status --short
git diff --stat
```

shows only:

`docs/implementation-plan.md`

---

# 50. FAILURE MODES

Do not:

1. merely restate architecture §36;
2. create an arbitrary new phase numbering system;
3. skip intra-tier dependency ordering;
4. skip capability-level decomposition;
5. omit capability gates;
6. make "implementation" a single undetailed task;
7. silently skip any DA module;
8. present proposed DA priority as source-defined;
9. conflate maturity and implementation priority;
10. conflate scenario priority and implementation priority;
11. redefine rollout mechanics;
12. redefine scaling;
13. redefine evaluation;
14. redefine security;
15. redefine observability;
16. claim scenario coverage that does not exist;
17. invent production evidence;
18. invent timelines/staffing;
19. invent technology decisions;
20. modify upstream documents;
21. create ADRs;
22. hide source gaps;
23. hide contradictions;
24. create a shallow roadmap instead of an implementation plan.

---

# 51. EXECUTION ORDER

Execute in this order:

### Step 1

Read `CLAUDE.md`.

### Step 2

Verify `architecture.md §36` and `conventions.md §25`.

### Step 3

Extract the exact P0–P5 source-defined lists.

### Step 4

Inspect every `TECH-NNN` priority and dependency note.

### Step 5

Inspect all 25 `DA-NNN` modules.

### Step 6

Inspect `SOURCE-GAP-OPTCAT-01`.

### Step 7

Inspect `SOURCE-GAP-AGENTOPT-01`.

### Step 8

Inspect the scenario-matrix P0–P3 terminology.

### Step 9

Inspect relevant edge cases.

### Step 10

Consume security, quality, evaluation, observability, and scaling boundaries.

### Step 11

Build the P0–P5 dependency graph.

### Step 12

Build workstreams.

### Step 13

Build capabilities.

### Step 14

Decompose each substantial capability to implementation depth.

### Step 15

Create capability gates.

### Step 16

Resolve all 25 DA modules.

### Step 17

Construct CI/CD sequencing.

### Step 18

Construct traceability.

### Step 19

Construct risks, gaps, contradictions, and ADR candidates.

### Step 20

Perform complete ID verification.

### Step 21

Perform maturity/no-fabrication audit.

### Step 22

Write only:

`docs/implementation-plan.md`

### Step 23

Run file-scope verification.

### Step 24

Run final readiness audit.

### Step 25

Ensure the final line is exactly:

`READY FOR NEXT DOCUMENTATION PHASE`

---

# 52. CORE PRINCIPLE

The implementation plan must be **deep enough to guide engineering implementation**, but **honest enough to remain a Level-0 research artifact**.

The required hierarchy is:

```text
P0–P5
   ↓
Workstream
   ↓
Capability
   ↓
Contract & Design
   ↓
Minimal Implementation
   ↓
Integration
   ↓
Observability
   ↓
Security / Governance
   ↓
Quality Validation
   ↓
Scenario Validation
   ↓
Failure / Recovery Validation
   ↓
Capability Gate
   ↓
Next Capability
```

Use detailed technical substeps wherever the capability warrants them.

Do not flatten this into a simple phase checklist.

Generate only:

`docs/implementation-plan.md`

The document must end with exactly:

`READY FOR NEXT DOCUMENTATION PHASE`
