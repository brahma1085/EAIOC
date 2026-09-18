# EAIOC — Consolidated Master Generation Prompt for `requirements-traceability.md`

## 0. ROLE

You are the **EAIOC Documentation Engineer** responsible for generating the next and final pre-ADR documentation artifact in the Enterprise Agent & LLM Inference Optimization Control Plane documentation chain.

Generate exactly:

`docs/requirements-traceability.md`

Document ID:

`EAIOC-REQTRACE-001`

Status:

`PRE-IMPLEMENTATION — Level 0 (RESEARCH)`

Version:

`1.0.0`

This document is the **canonical cross-document requirements traceability and documentation-reconciliation layer**.

It does not replace or redefine traceability already owned by earlier documents.

Its primary purpose is to close the traceability gap created because the earlier requirement mappings were created before the complete downstream documentation corpus existed.

---

# 1. DOCUMENTATION CHAIN POSITION

The authoritative documentation chain is:

```text
problemStatement
    ↓
Engineering Spec
    ↓
architecture.md
    ↓
interfaces.md
    ↓
conventions.md
    ↓
edge-cases.md
    ↓
scenario-matrix.md
    ↓
optimization-catalog.md
    ↓
provider-matrix.md
    ↓
cache-strategy.md
    ↓
agent-optimization.md
    ↓
inference-optimization.md
    ↓
quality-gates.md
    ↓
security.md
    ↓
observability.md
    ↓
eval.md
    ↓
SCALING.md
    ↓
implementation-plan.md
    ↓
requirements-traceability.md
    ↓
ADRs
```

`implementation-plan.md` is the immediately preceding approved document.

ADRs are downstream and must not be generated in this task.

---

# 2. CENTRAL PURPOSE

This document exists because:

* `architecture.md §44` already provides requirement → architecture-section traceability;
* `conventions.md §27` already provides requirement → convention-section traceability;
* `scenario-matrix.md §36–39` already provides scenario/interface/edge-case traceability;
* those earlier documents were generated before all downstream optimization, security, evaluation, scaling, and implementation documents existed;
* therefore they could not provide a complete requirement → downstream-document traceability layer.

This document closes that gap.

The primary forward trace is:

```text
Requirement
    ↓
Architecture
    ↓
Convention
    ↓
Downstream Document
    ↓
Capability / Control / Mechanism / Validation
    ↓
Implementation Plan
    ↓
ADR dependency where applicable
```

The second major responsibility is cross-corpus reconciliation:

```text
SOURCE-GAP-* across entire corpus
        +
CONTRA-* across entire corpus
        ↓
single consolidated current-status register
```

A gap opened in one document may later be narrowed or resolved by a later document without modifying the originating document.

This document must expose that relationship.

---

# 3. SCOPE DISCIPLINE

This is a **surgical cross-document reconciliation document**.

It owns:

1. Requirement → downstream-document forward traceability.
2. Cross-document requirement coverage analysis.
3. Consolidated source-gap register.
4. Consolidated contradiction register.
5. Requirement coverage/orphan analysis.
6. Traceability-chain integrity.
7. Documentation-set completeness from a requirements perspective.

It does NOT own:

* requirement → architecture-section mapping;
* requirement → convention-section mapping;
* scenario → requirement mapping already owned by `scenario-matrix.md`;
* scenario → architecture mapping already owned by `scenario-matrix.md`;
* interface → scenario mapping;
* edge-case → scenario mapping;
* individual optimization design;
* provider selection;
* cache architecture;
* agent algorithms;
* inference algorithms;
* security architecture;
* observability architecture;
* evaluation methodology;
* scaling methodology;
* implementation sequencing;
* ADR decisions.

Those documents remain authoritative for their own content.

---

# 4. NON-NEGOTIABLE SOURCE-FIDELITY RULE

Never trust this prompt's factual characterization without verification.

Every:

* requirement ID;
* gap ID;
* contradiction ID;
* count;
* section reference;
* capability ID;
* architecture reference;
* implementation reference;
* security reference;
* quality/evaluation reference;

must be independently verified against the current repository.

The prompt is an execution guide, not an authoritative source of repository facts.

If the live repository differs from this prompt:

**the live repository wins.**

Record material discrepancies rather than silently correcting them.

---

# 5. AUTHORITATIVE INPUTS

Read/inspect the following:

## Root governance

1. `CLAUDE.md`

## Core requirements/specification sources

2. `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt`
3. `Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md`

## Architecture and baseline traceability

4. `architecture.md`
5. `interfaces.md`
6. `conventions.md`
7. `edge-cases.md`
8. `scenario-matrix.md`

## Downstream corpus

9. `optimization-catalog.md`
10. `provider-matrix.md`
11. `cache-strategy.md`
12. `agent-optimization.md`
13. `inference-optimization.md`
14. `quality-gates.md`
15. `security.md`
16. `observability.md`
17. `eval.md`
18. `SCALING.md`
19. `implementation-plan.md`

---

# 6. EXISTING TRACEABILITY THAT MUST NOT BE RE-DERIVED

## 6.1 Architecture

`architecture.md §44`

This is authoritative for:

```text
OBJ
SEC
NFR
H
AC
→ owning architecture section
```

Cite and extend forward.

Do not recreate its mapping from scratch.

Verify the actual current §44 structure before writing.

---

## 6.2 Conventions

`conventions.md §27`

This is authoritative for requirement → convention traceability.

`conventions.md §27.1`

This is authoritative for the H01–H20 convention coverage.

Cite and extend forward.

Do not re-derive those mappings.

---

## 6.3 Scenario Matrix

`scenario-matrix.md §36–39`

Use this as authoritative for:

* Requirement → Scenario;
* Scenario → Architecture;
* Interface coverage;
* Edge-case coverage.

Do not independently reconstruct those mappings.

This document may connect the requirement to the already-established scenario layer, but it must not replace the scenario matrix's ownership.

---

# 7. PRIMARY REQUIREMENT TAXONOMY

Discover the actual requirement taxonomy from the live files.

At minimum verify whether the following current series exist:

```text
OBJ-*
SEC-*
NFR-*
H*
AC-*
```

Do not assume ranges without verification.

The Claude Code source prompt currently describes:

```text
OBJ-001–035
SEC-001–016
NFR-001–014
H01–H20
AC-001–053
```

These ranges MUST be independently verified.

If a range differs:

* use the live range;
* record the discrepancy if materially relevant;
* do not silently adopt the prompt's range.

---

# 8. REQUIREMENT SOURCE HIERARCHY

Determine where each requirement originates.

Possible sources include:

```text
Problem Statement
Engineering Specification
Architecture
Security
Quality/Evaluation
Scaling
Governance
```

Do not automatically treat downstream implementation decisions as requirements.

A downstream document may elaborate a requirement without becoming its source.

Preserve requirement ownership.

---

# 9. REQUIREMENT STATUS VOCABULARY

Every requirement must have one controlled traceability status:

```text
FULLY TRACED
PARTIALLY TRACED
INDIRECTLY TRACED
UNTRACED
AMBIGUOUS
BLOCKED BY SOURCE GAP
BLOCKED BY ADR
NOT APPLICABLE
```

Definitions must be stated in the document.

Do not use evaluative terms such as:

* good;
* bad;
* strong;
* weak;
* excellent;
* poor.

This is traceability, not document scoring.

---

# 10. EVIDENCE LABELS

Use the established four-label discipline from the repository:

```text
SOURCE-DEFINED
SOURCE-DERIVED
PROPOSED METHODOLOGY
PROPOSED DEFAULT — VALIDATE LOCALLY
```

Additionally, where necessary for a relationship rather than a methodology statement, use:

```text
IMPLEMENTATION-MAPPED
VALIDATION-MAPPED
GAP
```

Do not represent inferred relationships as source-defined facts.

---

# 11. PRIMARY FORWARD-TRACEABILITY MATRIX

Create the authoritative matrix:

```text
Requirement
    →
Downstream document(s)
    →
Actual elaboration
    →
Implementation / validation consequence
```

For every verified requirement in the canonical series, provide one row.

Where several requirements genuinely have an identical downstream disposition, grouping is permitted, but do not group merely for brevity when their traceability differs.

Recommended structure:

| Requirement ID | Requirement | Source | Architecture Ref | Convention Ref | Downstream Document | Downstream Section | Actual Elaboration | Implementation Mapping | Validation Mapping | Status | Evidence | Gap/Notes |
| -------------- | ----------- | ------ | ---------------- | -------------- | ------------------- | ------------------ | ------------------ | ---------------------- | ------------------ | ------ | -------- | --------- |

Important:

`NO DOWNSTREAM ELABORATION`

is a valid and often correct result.

Do not force-fit a downstream document onto every requirement.

---

# 12. WHAT COUNTS AS DOWNSTREAM ELABORATION

A downstream document counts only when it actually adds requirement-relevant detail.

Examples:

```text
Requirement
→ optimization-catalog capability
```

counts when the catalog materially elaborates the requirement.

Merely mentioning the requirement ID does not automatically constitute elaboration.

Similarly:

```text
Requirement
→ security.md
```

counts as elaboration only when security.md adds actual security/control/enforcement detail relevant to the requirement.

A citation-only occurrence should be distinguished from substantive elaboration.

Use a distinction such as:

```text
DIRECT ELABORATION
REFERENCE ONLY
NO DOWNSTREAM ELABORATION
```

where useful.

---

# 13. DOWNSTREAM DOCUMENT INVENTORY

For each of the eleven downstream documents:

```text
optimization-catalog.md
provider-matrix.md
cache-strategy.md
agent-optimization.md
inference-optimization.md
quality-gates.md
security.md
observability.md
eval.md
SCALING.md
implementation-plan.md
```

inspect:

1. Scope boundary.
2. Requirement references.
3. Requirement IDs.
4. Source-gap register.
5. Contradiction register.
6. Major capabilities.
7. Traceability sections.
8. Implementation mappings.
9. Validation mappings.

Determine which requirement categories the document actually elaborates.

Do not infer scope merely from its filename.

---

# 14. SCOPE-BOUNDARY MATRIX

Create a cross-document boundary matrix:

| Document | Owns | Cites | Elaborates Requirements? | Requirement Categories | Does Not Own |
| -------- | ---- | ----- | ------------------------ | ---------------------- | ------------ |

Explicitly show that this document owns:

```text
requirement → downstream-document forward trace
cross-corpus gap reconciliation
cross-corpus contradiction reconciliation
traceability completeness
```

and does not take ownership away from upstream/downstream documents.

---

# 15. MASTER SOURCE-GAP REGISTER

Build one consolidated register containing every verified:

```text
SOURCE-GAP-*
```

across the relevant corpus.

Search the entire repository rather than relying on the prompt.

At minimum inspect:

```text
architecture
Engineering Spec
scenario-matrix
optimization-catalog
provider-matrix
cache-strategy
agent-optimization
inference-optimization
quality-gates
security
observability
eval
SCALING
implementation-plan
```

Also inspect `CLAUDE.md` for the canonical gap-prefix taxonomy.

---

# 16. GAP REGISTER FIELDS

Every consolidated gap entry should contain:

```text
Gap ID:
Origin Document:
Origin Section:
Description:
Original Status:
Later Evidence:
Current Status:
Blocking / Non-Blocking:
Traceability Impact:
Resolution Owner:
Evidence Classification:
```

Current status must use controlled values:

```text
OPEN
NARROWED
RESOLVED
RESOLVED-IN-PLACE-DOWNSTREAM
```

Do not call a gap resolved unless the live source provides sufficient evidence.

---

# 17. CROSS-DOCUMENT GAP RECONCILIATION

Specifically investigate whether later documents changed the effective status of earlier gaps.

The current execution prompt identifies these as examples to verify, NOT facts to blindly accept:

```text
SOURCE-GAP-OPTCAT-01
SOURCE-GAP-QUALGATE-01
SOURCE-GAP-QUALGATE-02
SOURCE-GAP-ARCH-02
```

Verify each against live files.

Then search for additional relationships.

Examples:

```text
Earlier document:
SOURCE-GAP-X

Later document:
provides narrowing evidence

Result:
NARROWED
```

or:

```text
Earlier document:
SOURCE-GAP-X

Later document:
provides actual resolution

Result:
RESOLVED-IN-PLACE-DOWNSTREAM
```

The originating document may remain unchanged.

That is exactly why this master register exists.

---

# 18. ROOT-LEVEL GAP VERIFICATION

Explicitly inspect all root-level architecture/engineering gaps.

Do not assume that only:

```text
SOURCE-GAP-ARCH-01
SOURCE-GAP-ARCH-02
SOURCE-GAP-ARCH-03
SOURCE-GAP-ES-*
```

exist.

Search the live files.

If `ARCH-03` does not exist, do not include it simply because this prompt mentions it.

If additional root gaps exist, include them.

For each root-level gap:

```text
Current Status
Later Narrowing/Resolution
Blocking Impact
ADR Relevance
```

must be explicit.

---

# 19. MASTER CONTRADICTION REGISTER

Search the complete corpus for:

```text
CONTRA-*
```

Build one consolidated register.

At minimum verify any known candidates such as:

```text
CONTRA-001
CONTRA-OPTCAT-01
```

and verify whether other documents explicitly report zero contradictions.

Do not assume that a document's statement of "0 contradictions" means there are no contradictions anywhere in the corpus.

This document's register is corpus-wide.

---

# 20. CONTRADICTION FIELDS

For every contradiction:

```text
Contradiction ID:
Origin Document:
Origin Section:
Statement:
Documents Involved:
Current Interpretation:
Resolution Status:
Resolution Evidence:
Remaining Impact:
```

Use controlled disposition:

```text
OPEN
NARROWED
RESOLVED
NOT A CONTRADICTION — REFINEMENT
NOT A CONTRADICTION — SCOPE DIFFERENCE
```

Do not silently resolve contradictions.

Distinguish:

```text
Contradiction
vs.
Refinement
vs.
Specialization
vs.
Different abstraction level
vs.
Scope difference
```

---

# 21. REQUIREMENT CATEGORY COUNT RECONCILIATION

Use `conventions.md §27` as the cited baseline.

The current prompt identifies these reported counts:

```text
35 OBJ
53 AC
16 SEC
14 NFR
20 H
25 DA
71 INTF
260+ schema types
```

These MUST be freshly verified.

Do not blindly reproduce them.

If a cited count differs from the live count:

create:

`SOURCE-GAP-REQTRACE-NN`

and record:

```text
Expected / cited count
Freshly verified count
Source
Reason for discrepancy if determinable
Impact
```

Never silently correct the source document.

---

# 22. DA REQUIREMENT / MODULE TRACEABILITY

Where `DA-NNN` modules appear, preserve the distinction between:

```text
DA module
≠ requirement
```

Map DA modules to requirements only where authoritative evidence exists.

Use `implementation-plan.md` as authoritative for the final implementation priority disposition:

```text
P0
P1
P2
P3
P4
P5
```

Do not redefine DA priority in this document.

Remember:

```text
Implementation Priority P0–P5
≠
Scenario Priority P0–P3
≠
Maturity Level
```

---

# 23. IMPLEMENTATION-PLAN TRACEABILITY

`implementation-plan.md` is authoritative for implementation sequencing.

Map requirements to implementation capabilities where the implementation plan provides evidence.

Do not create new implementation decisions.

Do not change P0–P5 assignments.

Where a requirement has no implementation mapping:

```text
NO IMPLEMENTATION MAPPING
```

Where implementation depends on unresolved architectural decisions:

```text
BLOCKED BY ADR
```

---

# 24. CAPABILITY TRACEABILITY

For substantial capabilities, establish:

```text
Requirement
→ Capability
→ Implementation-plan item
→ Validation
```

Do not reproduce the full capability lifecycle from `implementation-plan.md`.

Reference the implementation-plan capability and its relevant section.

The traceability document should answer:

> Which requirement justifies this capability?

and:

> Which capability implements this requirement?

---

# 25. REVERSE TRACEABILITY

Forward traceability is necessary but insufficient.

Add reverse analysis:

## Architecture → Requirements

Which major architecture elements have explicit requirement ownership?

## Capability → Requirements

Which major capabilities have identifiable requirement justification?

## Security Control → Requirements

Which security/governance controls protect which requirements?

## Quality Gate → Requirements

Which quality gates validate which requirements?

## Evaluation → Requirements

Which evaluation mechanisms validate which requirements?

## Scenario → Requirements

Reference `scenario-matrix.md` rather than recreating its mappings.

## Observability → Requirements

Which measurable requirements have existing observability evidence?

## Scaling → Requirements

Which requirements have explicit scaling implications?

---

# 26. ORPHAN REQUIREMENTS

Identify:

```text
Requirement exists
but no downstream elaboration
```

This is not automatically a defect.

Classify:

```text
EXPECTED — BASELINE/SCOPE REQUIREMENT
NO DOWNSTREAM ELABORATION
TRACEABILITY GAP
ADR-DEPENDENT
```

Do not force every requirement to have an optimization or implementation artifact.

---

# 27. ORPHAN ARTIFACTS

Identify major downstream artifacts/capabilities with no identifiable requirement justification.

Possible explanations:

```text
Cross-cutting infrastructure
Safety/governance mechanism
Measurement infrastructure
Implementation support
Architectural constraint
Explicit design choice
```

Do not automatically label an artifact orphan as a defect.

Record:

`NO DIRECT REQUIREMENT IDENTIFIED`

where appropriate.

---

# 28. BROKEN TRACEABILITY CHAINS

Identify chains such as:

```text
Requirement
→ Architecture
→ no implementation mapping
```

```text
Requirement
→ Capability
→ no validation
```

```text
Requirement
→ Security concern
→ no security control
```

```text
Requirement
→ measurable outcome
→ no observability evidence
```

```text
Requirement
→ implementation capability
→ ADR required
```

Each broken chain must identify exactly where the chain terminates.

---

# 29. VALIDATION TRACEABILITY

For requirements that are testable or measurable, trace available validation evidence:

```text
Quality Gate
Evaluation
Scenario
Edge Case
Benchmark
Regression Detector
Observability
```

Do not invent validation scenarios.

If none exists:

`VALIDATION GAP`

If the validation is indirect:

`INDIRECT VALIDATION`

---

# 30. SECURITY TRACEABILITY

For security-sensitive requirements:

```text
Requirement
→ Security Control
→ Enforcement
→ Validation
```

Reference `security.md`.

Do not redesign security architecture.

Do not invent controls.

If security disposition is absent:

`SECURITY TRACEABILITY GAP`

where evidence supports that finding.

---

# 31. OBSERVABILITY TRACEABILITY

For measurable requirements:

```text
Requirement
→ Measurement
→ Metric/Event/Trace/Audit/Evaluation Evidence
```

Reference `observability.md`.

Do not invent metric names.

If no evidence exists:

`OBSERVABILITY GAP`

---

# 32. SCALING TRACEABILITY

Where requirements affect:

* throughput;
* latency;
* concurrency;
* tenant scale;
* provider scale;
* deployment mode;
* resource utilization;
* infrastructure;

map them to `SCALING.md`.

Do not reproduce the scaling design.

---

# 33. ADR TRACEABILITY

Identify requirements whose downstream implementation depends on an unresolved architectural decision.

Use only actual ADR candidates/evidence.

Do not create ADRs.

Do not pre-decide ADR outcomes.

Use:

`BLOCKED BY ADR`

only where the requirement cannot be implementation-decided without the ADR.

---

# 34. MULTI-TENANCY TRACEABILITY

Explicitly inspect requirement implications for:

```text
Tenant identity
Tenant isolation
Tenant-scoped state
Tenant-scoped cache
Tenant authorization
Tenant cost attribution
Tenant observability
Cross-tenant leakage prevention
```

Do not invent tenant requirements.

Distinguish:

```text
Explicit Requirement
vs.
Architectural Invariant
vs.
Derived Constraint
```

---

# 35. AGENT / SUB-AGENT TRACEABILITY

Inspect requirement relationships involving:

```text
Agents
Sub-agents
Agent loops
Tool/MCP use
Memory
RAG
Coding agents
Multi-agent systems
Agent economics
Context management
```

Map to `agent-optimization.md` where actual elaboration exists.

Do not duplicate the agent implementation.

---

# 36. INFERENCE TRACEABILITY

Inspect requirement relationships involving:

```text
Inference optimization
Model/provider routing
Batching
KV/prefix cache
Speculative decoding
Quantization
Prefill/decode
Scheduling
GPU utilization
```

Map to `inference-optimization.md` where actual elaboration exists.

Preserve the P5 ownership boundary established in `implementation-plan.md`.

Do not redefine P5.

---

# 37. END-TO-END TRACEABILITY CHAINS

Provide representative complete chains where evidence exists:

```text
Requirement
→ Architecture
→ Convention
→ Downstream Capability
→ Implementation Plan
→ Security/Governance
→ Quality/Evaluation
→ Scenario
→ Observability
→ Scaling
```

Not every chain will contain every dimension.

Do not fabricate missing links.

Explicitly show where the chain stops.

---

# 38. COVERAGE ANALYSIS

Report verified counts for:

```text
Total Requirements
Fully Traced
Partially Traced
Indirectly Traced
Untraced
Ambiguous
Blocked by Source Gap
Blocked by ADR
Not Applicable
```

Do not manufacture percentages.

If percentages are shown:

```text
percentage = category count / verified total × 100
```

Show the denominator.

---

# 39. REQUIREMENT-CLASS COVERAGE

Where supported by the repository, analyze:

```text
Objectives
Security
NFR
Hardening
Acceptance Criteria
Optimization
Agent
Inference
Quality/Evaluation
Observability
Scaling
Governance
Multi-Tenancy
```

Do not create unsupported requirement categories.

---

# 40. SOURCE-GAP-REQTRACE

This document may discover new traceability-specific gaps.

Use:

```text
SOURCE-GAP-REQTRACE-NN
```

Examples:

* requirement count discrepancy;
* missing requirement ID;
* missing gap prefix;
* broken cross-document trace;
* unresolved requirement ownership;
* missing validation mapping;
* missing implementation mapping where required.

Do not use this prefix to relabel an existing upstream gap.

---

# 41. CONTRA-REQTRACE

Only create:

```text
CONTRA-REQTRACE-NN
```

if this document genuinely discovers a new contradiction affecting traceability.

Do not create contradictions merely because:

* a document does not mention a requirement;
* a requirement has no downstream elaboration;
* two documents operate at different abstraction levels.

---

# 42. EXISTING GAP/CONTRADICTION OWNERSHIP

Never modify an upstream gap or contradiction.

If an upstream register is inaccurate or incomplete:

record the finding here.

Example:

```text
Origin:
quality-gates.md

Finding:
Current downstream evidence changes the effective status.

Action:
Recorded in requirements-traceability.md.

Upstream file:
NOT MODIFIED.
```

---

# 43. FOUR-LABEL HOW-TO-READ DISCIPLINE

Include:

### SOURCE-DEFINED

Directly stated by an authoritative source.

### SOURCE-DERIVED

Directly derived from authoritative relationships without inventing requirements.

### PROPOSED METHODOLOGY

A methodology introduced by this document.

### PROPOSED DEFAULT — VALIDATE LOCALLY

A suggested interpretation that requires local validation.

Traceability facts must not be mislabeled as proposals.

---

# 44. REQUIRED DOCUMENT STRUCTURE

Use this structure unless live repository conventions require minor numbering adjustment:

```text
# Enterprise Agent & LLM Inference Optimization Control Plane
# Requirements Traceability

Document Metadata

1. Status / Maturity / How to Read
2. Purpose
3. Scope and Ownership
4. Source Corpus
5. Traceability Methodology
6. Requirement Taxonomy
7. Existing Upstream Traceability Ownership
8. Requirement Source Inventory
9. Forward Traceability Matrix
10. Downstream Document Coverage
11. Architecture → Requirement Traceability
12. Convention → Requirement Traceability
13. Optimization → Requirement Traceability
14. Agent → Requirement Traceability
15. Inference → Requirement Traceability
16. Security/Governance → Requirement Traceability
17. Quality/Evaluation → Requirement Traceability
18. Scenario / Edge-Case Traceability
19. Observability → Requirement Traceability
20. Scaling → Requirement Traceability
21. Implementation-Plan → Requirement Traceability
22. ADR Dependency Traceability
23. End-to-End Traceability Chains
24. Requirement Orphans
25. Artifact Orphans
26. Broken Traceability Chains
27. Coverage Analysis
28. Requirement-Class Coverage
29. Master Source-Gap Register
30. Master Contradiction Register
31. Traceability-Specific Source Gaps
32. Traceability-Specific Contradictions
33. Cross-Document Boundary Matrix
34. Documentation-Set Completeness
35. Traceability Risks / Remaining Open Areas
36. Final Report
37. Requirements Traceability Readiness
```

Do not create unnecessary duplicated subject-matter content.

---

# 45. STRUCTURED ENTRY FORMAT

Where fixed structured entries are used, keep fields consistent.

Example:

```text
Requirement ID:
Source:
Requirement:
Architecture:
Convention:
Downstream:
Implementation:
Security:
Validation:
Scenario:
Observability:
Scaling:
ADR:
Status:
Evidence:
Gap/Notes:
```

One labeled field per line.

Do not vary field order arbitrarily.

---

# 46. DOCUMENTATION-SET COMPLETENESS

Provide an honest accounting of:

```text
Authoritative source documents
Baseline documents
Generated downstream documents
This document
ADRs still pending
```

Do not claim:

```text
System complete
Documentation complete
Production ready
Implementation ready
```

unless the authoritative corpus explicitly supports such a claim.

The repository remains:

`PRE-IMPLEMENTATION — Level 0 (RESEARCH)`

---

# 47. TRACEABILITY READINESS

The final readiness section must explicitly answer:

1. Were all requirement IDs independently verified?
2. Were all requirement counts independently verified?
3. Was `architecture.md §44` cited rather than re-derived?
4. Was `conventions.md §27` cited rather than re-derived?
5. Was `scenario-matrix.md §36–39` preserved as the owner of its mappings?
6. Was the forward downstream trace completed?
7. Were `NO DOWNSTREAM ELABORATION` cases preserved honestly?
8. Were all source gaps consolidated?
9. Were all contradictions consolidated?
10. Were cross-document gap narrowing/resolution relationships verified?
11. Were root-level gaps independently verified?
12. Were implementation-plan mappings preserved?
13. Were security-sensitive requirements traced?
14. Were validation-sensitive requirements traced?
15. Were observability-relevant requirements traced?
16. Were scaling-relevant requirements traced?
17. Were ADR dependencies identified without making ADR decisions?
18. Were orphan requirements identified?
19. Were orphan artifacts identified?
20. Were broken traceability chains identified?
21. Were all new traceability gaps explicitly registered?
22. Were all new contradictions explicitly registered?
23. Was no upstream document modified?
24. Was no ADR created?
25. Is the document internally consistent?
26. Is the document still clearly Level 0 / pre-implementation?

Only after satisfying all applicable criteria may the document be marked ready.

---

# 48. FINAL REPORT

Include at minimum:

```text
Document ID
Version
Status
Chain Position

Requirement counts:
OBJ
SEC
NFR
H
AC
DA
INTF where applicable

Forward-trace matrix row count

Fully traced count
Partially traced count
Indirectly traced count
Untraced count
Ambiguous count
ADR-blocked count
Source-gap-blocked count

Master source-gap count
New REQTRACE source-gap count

Master contradiction count
New REQTRACE contradiction count

Orphan requirement count
Orphan artifact count
Broken-chain count

Major unresolved traceability areas

File-scope confirmation
```

Every reported count must be verified.

---

# 49. FILE-SCOPE DISCIPLINE

Modify/create ONLY:

`docs/requirements-traceability.md`

Do not modify:

```text
CLAUDE.md
problemStatement
Engineering Spec
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
eval.md
SCALING.md
implementation-plan.md
```

Do not create ADRs.

---

# 50. TOOL / SEARCH STRATEGY

Use repository search aggressively.

Start with:

```text
grep/search architecture.md for "## 44."
grep/search conventions.md for "## 27."
```

Then search the full corpus for:

```text
SOURCE-GAP-
CONTRA-
OBJ-
SEC-
NFR-
H
AC-
DA-
ADR
```

Inspect each downstream document's:

* scope boundary;
* requirement references;
* gap register;
* contradiction register;
* traceability sections.

For very large files, use targeted search instead of blindly loading the entire file.

A subagent may be used for independent gap/contradiction inventory if repository tooling supports it.

The final document must still be independently verified.

---

# 51. VERIFICATION COMMANDS

Before delivery:

### Requirement verification

Verify every requirement ID cited.

### Gap verification

Verify every `SOURCE-GAP-*` entry against its source.

### Contradiction verification

Verify every `CONTRA-*` entry against its source.

### Cross-document verification

Verify every claimed narrowing/resolution relationship.

### Reference verification

Verify cited:

```text
section numbers
document names
DA-NNN
QG-NNN
SCN-*
EC-*
INTF-*
ADR candidates
```

actually exist.

### Count verification

Recalculate all reported counts.

### Final marker verification

Search:

```text
READY FOR NEXT DOCUMENTATION PHASE
```

The phrase must occur:

**exactly once**

and must be the literal final line.

---

# 52. GIT / FILE-SCOPE VERIFICATION

Run:

```text
git status --short
git diff --stat
git diff -- docs/requirements-traceability.md
```

Confirm:

```text
ONLY docs/requirements-traceability.md
```

was created/modified in this pass.

If any other file changed:

do not silently revert or modify it unless repository instructions explicitly authorize doing so.

Report the scope violation.

---

# 53. FAILURE MODES TO DEFEND AGAINST

Prevent all of the following:

### Failure 1 — Re-deriving existing traceability

Do not recreate architecture §44 or conventions §27.

### Failure 2 — Rebuilding scenario traceability

Do not recreate scenario-matrix §36–39.

### Failure 3 — Force-fitting downstream coverage

Use:

`NO DOWNSTREAM ELABORATION`

when appropriate.

### Failure 4 — Treating references as elaboration

A requirement ID appearing in a document does not automatically mean the document elaborates it.

### Failure 5 — Missing cross-document gap resolution

Search the whole corpus for relationships.

### Failure 6 — Trusting the prompt's counts

Recalculate.

### Failure 7 — Silently correcting source discrepancies

Create `SOURCE-GAP-REQTRACE-NN`.

### Failure 8 — Silently resolving contradictions

Record the contradiction and disposition.

### Failure 9 — Treating every artifact as a requirement

Preserve requirement ownership.

### Failure 10 — Treating every requirement as an implementation item

Some requirements legitimately have no downstream implementation elaboration.

### Failure 11 — Confusing implementation priority and scenario priority

Preserve:

```text
P0–P5 implementation priority
P0–P3 scenario priority
Maturity level
```

as distinct concepts.

### Failure 12 — Reopening approved implementation-plan decisions

Use `implementation-plan.md` as authoritative.

### Failure 13 — Modifying upstream documents

Strictly prohibited.

### Failure 14 — Creating ADR decisions

ADRs are downstream.

### Failure 15 — Claiming documentation completeness means system completeness

The repository remains pre-implementation.

### Failure 16 — Fabricating validation

If no validation exists, record:

`VALIDATION GAP`.

### Failure 17 — Fabricating observability

If no observability evidence exists, record:

`OBSERVABILITY GAP`.

### Failure 18 — Fabricating security controls

Reference `security.md`; do not invent controls.

### Failure 19 — Treating refinement as contradiction

Distinguish abstraction, refinement, specialization, and scope differences.

### Failure 20 — Incorrect final readiness marker

The exact phrase:

`READY FOR NEXT DOCUMENTATION PHASE`

must occur once and only once, as the final line.

---

# 54. FINAL EXECUTION ORDER

Execute in this order:

```text
1. Read CLAUDE.md
2. Verify architecture §44
3. Verify conventions §27
4. Verify scenario-matrix §36–39 ownership
5. Discover actual requirement IDs
6. Discover actual requirement counts
7. Inventory all downstream documents
8. Inventory all SOURCE-GAP-* IDs
9. Inventory all CONTRA-* IDs
10. Determine cross-document gap narrowing/resolution
11. Determine actual downstream requirement elaboration
12. Build forward requirement matrix
13. Build reverse traceability
14. Build orphan analysis
15. Build broken-chain analysis
16. Build consolidated gap register
17. Build consolidated contradiction register
18. Build coverage analysis
19. Build boundary matrix
20. Build final report
21. Perform full self-audit
22. Verify file scope
23. Verify final readiness marker
24. Only then finalize the document
```

---

# 55. CORE PRINCIPLE

The purpose of `requirements-traceability.md` is not to make the EAIOC documentation set appear complete.

It is to make the following question answerable for every important requirement:

> **Where did this requirement originate, where is it architecturally owned, where is it elaborated downstream, what capability or control addresses it, how is it implemented, how is it validated, what evidence demonstrates coverage, and what remains unresolved?**

When the evidence exists:

**trace it.**

When the evidence is indirect:

**label it.**

When the requirement has no downstream elaboration:

**say so.**

When a gap remains:

**preserve it.**

When a later document narrows or resolves an earlier gap:

**show the relationship.**

When a contradiction exists:

**expose it without silently rewriting history.**

When a downstream artifact has no identifiable requirement:

**make that visible.**

When an ADR is required:

**identify the dependency without making the decision.**

The final artifact must optimize for:

```text
SOURCE FIDELITY
+
AUDITABILITY
+
TRACEABILITY
+
CROSS-DOCUMENT CONSISTENCY
+
ENGINEERING DECISION SUPPORT
```

—not appearance of completeness.

The final line of the file must be exactly:

`READY FOR NEXT DOCUMENTATION PHASE`
