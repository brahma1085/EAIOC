# MASTER PROMPT — GENERATE EAIOC IMPLEMENTATION READINESS GATE

## 0. ROLE

You are acting as a **Principal Architect + Staff/Principal Engineer + Documentation Governance Reviewer** for the:

**Enterprise Agent & LLM Inference Optimization Control Plane (EAIOC)**

Your task is to generate the next governance artifact:

`docs/implementation-readiness-gate.md`

This document is the formal checkpoint between:

**LEVEL 0 — RESEARCH / PRE-IMPLEMENTATION**

and the beginning of:

**P0 FOUNDATION IMPLEMENTATION**

The purpose of this artifact is narrow:

> Determine, using independent re-verification of the existing authoritative documentation corpus, whether the documentation baseline is sufficiently complete, internally consistent, traceable, and unblocked for P0 implementation to begin.

This is a **readiness gate**, not a new architecture/design document.

You do NOT own:

* accepting any ADR
* selecting any final technology
* writing implementation code
* scaffolding implementation
* changing architecture
* modifying existing requirements
* resolving source contradictions silently
* closing source gaps without source authority
* declaring the system built
* declaring production readiness

---

# 1. AUTHORITATIVE DOCUMENTATION CORPUS

Treat the current repository documentation as authoritative according to the established EAIOC documentation chain.

The known chain is:

1. `problemStatement.txt`
2. `Engineering_Spec.md`
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
18. `implementation-plan.md`
19. `requirements-traceability.md`
20. `docs/adr/0000-index.md`
21. `docs/adr/0001-cache-backing-store.md`
22. `docs/adr/0002-execution-truth-state-store.md`
23. `docs/adr/0003-ci-cd-platform.md`
24. `docs/adr/0004-telemetry-backend.md`
25. `docs/adr/0005-provider-adapter-implementation-pattern.md`
26. `docs/adr/0006-xec-coordination-mechanism.md`

These filenames represent the expected logical artifacts. If the repository contains versioned/suffixed filenames, identify the actual current authoritative files from the repository.

### Critical rule

Do NOT assume that the list or counts above are still current.

Re-verify the live repository.

If the current authoritative corpus differs, record the discrepancy rather than silently reconciling it.

---

# 2. FILE-SCOPE RULE

This task may create ONLY:

`docs/implementation-readiness-gate.md`

Do NOT modify:

* `CLAUDE.md`
* problem statement
* Engineering Spec
* architecture
* interfaces
* conventions
* edge-cases
* scenario-matrix
* optimization-catalog
* provider-matrix
* cache-strategy
* agent-optimization
* inference-optimization
* quality-gates
* security
* observability
* eval
* SCALING
* implementation-plan
* requirements-traceability
* any ADR
* ADR index

If a problem is discovered, document it inside the readiness gate.

Do not repair the source documents.

At completion, verify with repository status/diff that ONLY the new readiness-gate file was created/changed.

---

# 3. NO IMPLEMENTATION RULE

Do NOT:

* create source code
* create database schemas
* create infrastructure
* create Docker configuration
* create deployment manifests
* create CI/CD pipelines
* select final databases
* select final cache technology
* select final telemetry technology
* select final cloud service
* select final locking technology
* select final provider
* implement adapters
* implement XEC
* implement cache
* implement execution-state persistence

The readiness gate must determine whether implementation MAY BEGIN.

It must not begin implementation.

---

# 4. CORE QUESTION

The document must answer this question:

> **Is the EAIOC documentation baseline sufficiently complete, internally consistent, traceable, and unblocked to begin implementation work, starting with the defined P0 Foundation tier?**

The gate must answer this question using evidence already present in the corpus.

It must NOT simply copy the readiness verdict of another document.

The central value of this document is:

### Independent re-verification.

In particular, independently verify:

1. blocking source-gap status
2. contradiction status
3. `BLOCKED BY ADR` status
4. ADR status
5. ADR dependency status
6. P0 dependency/prerequisite satisfaction
7. P0 implementation starting slice
8. whether ADR-touched P0 capabilities have valid interim implementation paths

---

# 5. DOCUMENT IDENTITY

Use:

**Document ID:** `EAIOC-IRG-001`

**Title:**

`Enterprise Agent & LLM Inference Optimization Control Plane — Implementation Readiness Gate`

**Level:**

`LEVEL 0 — RESEARCH / PRE-IMPLEMENTATION`

**Status:**

Use the repository's established status convention. The document is a readiness assessment and must not imply that implementation has started.

Use version `1.0.0` unless repository conventions require another initial version.

Clearly state that this document is the formal readiness checkpoint created after the ADR set.

If root `CLAUDE.md` does not yet list this artifact, state that this is a documentation-governance observation only.

Do NOT modify `CLAUDE.md` in this task.

---

# 6. DOCUMENT DISCIPLINE

Use the established EAIOC source-classification vocabulary where applicable:

* `SOURCE-DEFINED`
* `SOURCE-DERIVED`
* `PROPOSED METHODOLOGY`
* `PROPOSED DEFAULT — VALIDATE LOCALLY`

Never present a proposed methodology as a source-defined requirement.

Never convert an inference into a source fact.

Never silently close a source gap.

---

# 7. REQUIRED DOCUMENT STRUCTURE

Generate the following sections in this order.

---

## 1. Status, Maturity, and How to Read

Include:

* Document ID
* Title
* Level
* Lifecycle phase
* Status
* Version
* Purpose
* Source classification discipline
* File-scope boundary
* Relationship to the completed ADR documentation phase

Explicitly state:

**This document determines P0 implementation readiness; it does not declare the EAIOC system implemented or production-ready.**

---

# 8. PURPOSE AND SCOPE BOUNDARY

State the exact readiness question being answered.

Explicitly state that this document does NOT:

* accept any ADR
* reject any ADR
* select any technology
* write code
* scaffold code
* modify existing architecture
* modify requirements
* modify interfaces
* modify ADRs
* modify `CLAUDE.md`
* declare the system built
* declare the system tested
* declare the system secure
* declare production readiness

All six ADRs must remain:

`PROPOSED`

---

# 9. DOCUMENTATION BASELINE COMPLETENESS

Identify the current documentation corpus.

Do not blindly assume a stale document count.

Verify that the expected authoritative artifacts exist.

For generated artifacts that have explicit readiness verdicts, verify that their expected terminal/readiness markers exist according to their own conventions.

For the ADR set, verify:

* index exists
* ADR-0001 through ADR-0006 exist
* all six remain `PROPOSED`

If a prior artifact's terminal verdict is missing or malformed, record it as a readiness finding.

Do not silently fix it.

---

# 10. MASTER REGISTER RE-VERIFICATION

Use:

`requirements-traceability.md`

as the authoritative traceability baseline.

Independently re-derive the following from the actual file:

### Requirements

Expected baseline:

* 35 OBJ
* 16 SEC
* 14 NFR
* 20 H
* 53 AC
* total 138 requirements

### Coverage

Expected baseline:

* 97 FULLY TRACED
* 11 PARTIALLY TRACED
* 7 INDIRECTLY TRACED
* 23 NO DOWNSTREAM
* 0 UNTRACED
* 0 AMBIGUOUS

Do NOT merely repeat these numbers from a summary section.

Validate them against the underlying registers/tables.

If current values differ:

1. report the actual value
2. report the previously documented value
3. identify the discrepancy
4. determine whether it affects the gate
5. do not silently reconcile the difference

---

# 11. SOURCE-GAP RE-VERIFICATION

Independently inspect the source-gap register.

The previous approved baseline recorded:

* 54 physical/literal SOURCE-GAP occurrences
* 50 distinct semantic gaps
* 1 inherited/repeated reference
* 42 OPEN gaps
* no blocking source gaps

Re-derive the current values.

Most importantly, independently determine:

> **How many currently open source gaps are actually blocking P0?**

Do not assume:

`OPEN = BLOCKING`

and do not assume:

`OPEN = NON-BLOCKING`

Classify each load-bearing gap as appropriate:

* P0 blocker
* P0 condition
* later-phase blocker
* implementation-validation item
* non-blocking documentation limitation

If the gate discovers a genuinely new discrepancy created by this re-verification, use:

`SOURCE-GAP-IRG-NN`

Do not invent a source gap merely because a detail is not specified.

---

# 12. CONTRADICTION RE-VERIFICATION

Independently verify the current contradiction register.

The approved baseline recorded:

* 4 distinct contradictions
* all resolved/historical
* 0 open contradictions

Re-check the live corpus.

If a new contradiction is found, record:

* `CONTRA-IRG-NN`
* source documents
* conflicting statements
* impact
* affected phase
* whether P0 is blocked
* required resolution

Do not silently resolve the contradiction.

---

# 13. ADR SET READINESS RE-VERIFICATION

Independently inspect all six ADRs.

Verify:

| ADR | Subject | Current Status | Blocking P0? | Interim Path |
| --- | ------- | -------------- | ------------ | ------------ |

Required ADRs:

* ADR-0001 — Cache/KV backing store
* ADR-0002 — Execution-truth/versioned-state store
* ADR-0003 — CI/CD platform/tooling
* ADR-0004 — Telemetry/observability backend
* ADR-0005 — Provider-adapter implementation pattern
* ADR-0006 — XEC coordination mechanism

Verify:

* all six exist
* all six are `PROPOSED`
* zero are `ACCEPTED`
* no final technology choice has been fabricated
* `BLOCKED BY ADR` remains zero unless evidence shows otherwise

Do not equate:

`ADR CANDIDATE`

with:

`BLOCKED BY ADR`

---

# 14. ADR DEPENDENCY RE-VERIFICATION

Independently validate the ADR dependency graph.

The previous ADR index recorded:

`0 HARD DEPENDENCIES`

Re-check the actual ADRs.

Distinguish:

* hard dependency
* conditional dependency
* evaluation dependency
* compatibility dependency
* sequencing consideration

Do not treat every relationship as a hard dependency.

If the dependency status has changed, record the discrepancy and evaluate its effect on P0.

---

# 15. ADR INTERIM-PATH VERIFICATION

This is a critical readiness check.

For every ADR that touches a P0 or P1 capability, verify that the ADR itself explicitly provides an interim implementation path allowing relevant work to proceed while the ADR remains `PROPOSED`.

Examples may include:

* in-memory reference implementation
* behavioral contract first
* adapter abstraction before final provider selection
* implementation evaluation before technology acceptance

Do NOT infer an interim path that is not documented.

Create a table:

| ADR | Affected Capability | Interim Path Explicitly Stated? | Evidence | Blocks P0? |
| --- | ------------------- | ------------------------------- | -------- | ---------- |

If no interim path exists for a P0 capability and the capability genuinely depends on the ADR decision, that may be a blocker.

---

# 16. P0 FOUNDATION READINESS

Use:

`implementation-plan.md`

as the primary implementation sequencing source.

Specifically inspect:

* §7 Dependency Model
* §8 P0 Foundation
* §8.1 Token Accounting
* §8.2 Baseline Benchmark Harness + Quality Evaluation
* §18 Capability Gate Model
* relevant implementation decision / ADR sections
* P0 verification gates

Do NOT assume that a P0 item is ready merely because it appears under P0.

For EVERY P0 item:

1. identify its prerequisites
2. identify its upstream sources
3. identify dependencies
4. verify those prerequisites exist
5. determine whether any prerequisite is unresolved
6. determine whether an ADR blocks it
7. determine whether an interim path exists
8. classify it as:

   * READY
   * READY WITH CONDITION
   * BLOCKED

Create a P0 readiness matrix:

| P0 Item | Prerequisites | Prerequisites Satisfied? | ADR Dependency | Interim Path | Readiness | Evidence |
| ------- | ------------- | ------------------------ | -------------- | ------------ | --------- | -------- |

This is one of the most important sections of the document.

---

# 17. FIRST IMPLEMENTABLE SLICE

Identify the concrete first implementation slice supported by the documentation.

Do not merely say:

`P0 is ready.`

Identify which P0 capabilities can genuinely begin.

Use:

* implementation-plan dependency graph
* P0 item definitions
* capability gate model
* ADR interim paths
* requirements traceability
* architecture constraints

The known full-lifecycle exemplars include:

### P0.1 Token Accounting

### P0.2 Baseline Benchmark Harness + Quality Evaluation

Validate the actual current P0 list before writing the final finding.

If additional P0 items have genuinely satisfied prerequisites, identify them.

If an item is not ready, explain why.

Do not artificially force all P0 items into the first slice.

---

# 18. REQUIREMENTS READINESS

Evaluate whether the requirements baseline is sufficiently actionable for P0.

Assess:

* objectives
* security requirements
* NFRs
* hypotheses
* acceptance criteria
* traceability
* downstream mapping
* implementation relevance

Explicitly distinguish:

* fully traced
* partially traced
* indirectly traced
* no downstream

A `NO DOWNSTREAM` requirement is NOT automatically a blocker.

Only classify it as blocking when the evidence shows it is required for the affected P0 capability.

---

# 19. ACCEPTANCE-CRITERIA READINESS

Verify all 53 acceptance criteria from the current authoritative traceability source.

For each relevant P0 AC, determine:

* implementation relevance
* validation mechanism
* measurable semantics where defined
* dependency on later decisions
* blocking status

Do not invent thresholds.

If a threshold is absent:

`Threshold not established in authoritative corpus; implementation-time acceptance criteria required.`

---

# 20. ARCHITECTURE READINESS

Evaluate whether the architecture is sufficiently defined for P0 implementation.

Assess:

* component boundaries
* responsibilities
* control-plane/runtime boundaries
* data flows
* state flows
* provider abstraction
* cache
* execution state
* XEC
* telemetry
* evaluation
* security/governance
* failure handling
* sequencing

Do not introduce new architecture.

Do not treat architecture constraints as final technology selections.

---

# 21. INTERFACE / CONTRACT READINESS

Use `interfaces.md`.

Assess whether P0 implementation has sufficient contracts for:

* APIs
* request/response semantics
* schemas
* errors
* versioning
* idempotency
* concurrency/conflict handling
* provider abstraction
* cache behavior
* execution-state behavior
* XEC behavior
* telemetry/evaluation

Identify missing or partially defined contracts.

Do not invent fields.

---

# 22. SECURITY / GOVERNANCE READINESS

Evaluate:

* authentication
* authorization
* tenant isolation
* secrets
* auditability
* data handling
* immutability
* retention
* non-deletability
* provider isolation
* administrative controls
* security-sensitive telemetry

Preserve the distinction:

`Execution State`

versus:

`AuditRecord`

Do not imply that every execution-state element is an `AuditRecord`.

Where administrative enforcement of AuditRecord non-deletability is unspecified, record:

`Implementation validation required`

rather than inventing an enforcement mechanism.

---

# 23. TENANCY READINESS

Explicitly assess whether the documented design adequately establishes:

* tenant identity
* tenant-scoped configuration
* tenant-scoped state
* tenant-scoped cache
* tenant-scoped telemetry
* authorization boundaries
* cross-tenant isolation
* failure containment

Do not invent additional tenancy architecture.

---

# 24. CACHE READINESS

Use:

* `cache-strategy.md`
* `optimization-catalog.md`
* `architecture.md`
* `interfaces.md`
* ADR-0001

Preserve the four-check cache gate.

The gate must retain this distinction:

> The combined cache implementation must support the four-check gate efficiently; the backing store does not need to natively implement every check. Application-layer mechanisms are acceptable where supported by the architecture and contracts.

Do not select a final cache technology.

---

# 25. EXECUTION-TRUTH / STATE-STORE READINESS

Use:

* architecture
* interfaces
* agent-optimization
* inference-optimization
* ADR-0002

Assess:

* execution truth
* versioned state
* conflict handling
* recovery
* resumability
* reversibility
* persistence
* multi-process implications

Preserve:

`ESM execution state ≠ automatically AuditRecord`

Where execution state is represented/materialized as `AuditRecord`, the AuditRecord immutability requirement applies.

Do not convert conditional P1+ requirements into unconditional P0 requirements.

---

# 26. PROVIDER-ADAPTER READINESS

Use:

* provider-matrix
* architecture
* interfaces
* ADR-0005

Verify provider neutrality.

Preserve traceability to:

* provider-neutrality rule
* NFR-005
* OBJ-013
* H08
* CAR
* relevant architecture sections

Do not select a provider.

Do not introduce provider-specific implementation assumptions.

---

# 27. XEC READINESS

Use:

* architecture
* interfaces
* edge-cases
* conventions
* ADR-0006

Preserve the distinction:

> XEC coordination is not synonymous with a distributed lock service.

Validate the five `ConcurrencyConflictResult` states:

1. `NO_CONFLICT`
2. `STALE_SNAPSHOT`
3. `VERSION_CONFLICT`
4. `RECONCILED`
5. `LOCK_REQUIRED`

Do not reduce this to four states.

Do not select a final locking technology.

---

# 28. TELEMETRY / OBSERVABILITY READINESS

Use:

* observability
* eval
* architecture
* interfaces
* ADR-0004

Assess:

* metrics
* traces
* logs
* auditability
* token/cost observability
* optimization observability
* provider observability
* agent observability
* inference observability
* evaluation integration

Preserve AuditRecord non-deletability.

Do not invent administrative enforcement mechanisms.

---

# 29. QUALITY / EVALUATION READINESS

Use:

* quality-gates
* eval
* scenario-matrix
* implementation-plan
* requirements-traceability

Evaluate:

* unit testing
* contract testing
* integration testing
* scenario validation
* quality gates
* benchmark harness
* quality evaluation
* regression validation
* optimization validation
* agent validation
* inference validation

Preserve the distinction between:

`Benchmark Harness`

and

`Quality Evaluation`

where the implementation plan defines them as separate source items.

Do not invent performance thresholds.

---

# 30. SCALING READINESS

Use `SCALING.md`.

Determine what is:

* established
* deferred
* conditional
* implementation-validation work

Do not treat production-scale readiness as equivalent to P0 implementation readiness.

Explicitly separate:

`P0 implementation readiness`

from:

`production scaling readiness`

---

# 31. IMPLEMENTATION-PLAN READINESS

Validate the approved implementation plan.

Assess:

* phase sequencing
* P0
* P1
* P2
* P3
* P4
* P5
* dependencies
* deliverables
* verification gates
* ownership boundaries

Preserve the P5 boundary:

* sequencing/dependencies remain in `implementation-plan.md`
* detailed P5 capability elaboration remains in `inference-optimization.md`

Do not rewrite the implementation plan.

---

# 32. P1–P5 READINESS BOUNDARY

Explicitly state which later phases are:

* ready in principle
* conditional
* dependent on P0
* dependent on ADR decisions
* dependent on implementation evidence
* not yet production-ready

Do not claim the entire implementation roadmap is unlocked merely because P0 can begin.

---

# 33. OUTSTANDING RISKS / WATCH ITEMS

Carry forward only genuinely load-bearing items.

In particular, inspect the ADR set's open question involving:

`ADR-0002 ↔ ADR-0006`

and the shared coordination-substrate question.

Do not turn an open question into a blocker unless evidence establishes that it blocks P0.

Also identify any new load-bearing item discovered during the independent re-verification.

---

# 34. IMPLEMENTATION BLOCKER REGISTER

Create:

| Blocker ID | Issue | Evidence | Affected Capability | Phase | Severity | Required Resolution |
| ---------- | ----- | -------- | ------------------- | ----- | -------- | ------------------- |

Only include genuine blockers.

If none exist, explicitly state:

`P0 implementation blockers identified: 0`

provided the independent verification supports that conclusion.

---

# 35. P0 ENTRY CRITERIA

Define explicit entry criteria derived from existing documentation.

Each criterion must contain:

* ID
* criterion
* source basis
* verification method
* status
* blocking classification

Potential categories:

* documentation baseline available
* architecture baseline available
* interfaces available
* requirements traceability established
* quality/evaluation baseline available
* security baseline available
* observability baseline available
* implementation plan available
* ADR candidates documented
* no blocking contradiction
* no blocking source gap
* ADR interim paths verified where required
* P0 prerequisites satisfied

Do not invent arbitrary acceptance thresholds.

---

# 36. P0 EXIT / VERIFICATION CRITERIA

Define the verification expectations for completing P0.

Derive them from:

* implementation plan
* quality gates
* acceptance criteria
* architecture
* interfaces
* security
* observability
* evaluation
* scenario matrix

Do not invent new numerical thresholds.

If an exact threshold does not exist:

`Threshold not established in authoritative corpus; implementation-time acceptance criteria required.`

---

# 37. WHAT A PASS DOES AND DOES NOT MEAN

This section is mandatory.

## PASS means

A passing gate means:

* the documentation baseline has passed the specified readiness checks
* no verified blocker prevents the identified P0 starting slice
* P0 can begin against the documented baseline
* remaining uncertainties are explicitly classified and controlled

## PASS does NOT mean

A passing gate does NOT mean:

* any ADR is accepted
* any technology is finally selected
* any code exists
* any component is implemented
* benchmarks have passed
* the system is production-ready
* the system is fully secure
* all future requirements are closed
* all source gaps are eliminated
* P1–P5 are automatically ready
* operational readiness has been established

Anchor this distinction to the repository's implementation-governance framing.

---

# 38. TRACEABILITY TO DOCUMENTATION CHAIN

Create a concise table:

| Artifact | Role | Readiness Contribution | Current Status | Gate Impact |
| -------- | ---- | ---------------------- | -------------- | ----------- |

Include the actual authoritative artifacts in the current repository.

Do not invent readiness status.

---

# 39. SOURCE GAPS CREATED BY THIS GATE

Create a section:

`## Source Gaps`

Only create `SOURCE-GAP-IRG-NN` entries when THIS readiness-gate verification genuinely discovers a new discrepancy.

Do not duplicate existing source gaps merely for completeness.

For every new gap include:

* ID
* description
* source
* impact
* affected phase
* blocking status
* required resolution

---

# 40. CONTRADICTIONS CREATED BY THIS GATE

Create:

`## Source Contradictions`

Only create `CONTRA-IRG-NN` if the independent verification discovers a genuinely new contradiction.

Do not create artificial contradictions from differences in terminology that the source already explains.

---

# 41. FINAL REPORT

Create:

`## Final Report`

Include:

* document created
* document ID
* current documentation baseline verified
* requirements baseline verification
* source-gap verification
* contradiction verification
* ADR verification
* ADR dependency verification
* P0 prerequisite verification
* first implementable slice
* P0 blockers
* P0 conditions
* P1+ boundary
* file-scope validation

Every numerical claim must be independently derived or directly verified from the live corpus.

---

# 42. FINAL GATE DECISION

Choose the actual result based on evidence.

Use exactly one of these:

### PASS

`IMPLEMENTATION READINESS GATE: PASS — P0 MAY BEGIN`

### CONDITIONAL

`IMPLEMENTATION READINESS GATE: PASS WITH CONDITIONS — P0 MAY BEGIN AFTER CONDITIONS ARE SATISFIED`

### BLOCKED

`IMPLEMENTATION READINESS GATE: NOT PASSED — <blocking finding>`

Do NOT force PASS.

Do NOT force CONDITIONAL.

If the corpus supports P0 beginning, say so.

If a genuine blocker exists, say so.

---

# 43. TERMINAL VERDICT RULE

The selected terminal verdict must occur:

* exactly once
* as the final substantive line
* as the literal final line of the file

Nothing may appear after it.

Do not use:

`READY FOR NEXT DOCUMENTATION PHASE`

because this document is the readiness gate itself.

Do not use:

`ADR SET COMPLETE — READY FOR IMPLEMENTATION READINESS GATE`

because that belongs to the previous artifact.

---

# 44. CRITICAL EAIOC SEMANTIC PRESERVATION

Before finalizing, verify these known corrections from the frozen ADR set.

## ADR-0002

Preserve:

> The immutability requirement for any portion of execution state that is represented or materialized as `AuditRecord` is the relevant governance constraint. The corpus does not establish that `reversibility_records` or terminal-state records are themselves `AuditRecord` objects; where such materialization occurs, the `AuditRecord` immutability requirement applies.

Do not collapse ESM state into AuditRecord.

---

## ADR-0006

Preserve the five-state model:

1. NO_CONFLICT
2. STALE_SNAPSHOT
3. VERSION_CONFLICT
4. RECONCILED
5. LOCK_REQUIRED

Do not describe it as four states.

---

## XEC

Preserve:

`XEC coordination ≠ distributed lock service`

XEC includes:

* version/conflict detection
* stale-snapshot detection
* reconciliation
* locking where `LOCK_REQUIRED`

---

## ADR-0001

Preserve:

> The combined cache implementation must support the four-check gate efficiently; the backing store need not natively implement every check. Application-layer mechanisms are acceptable.

Do not equate cache readiness with selection of a backing technology.

---

## ADR-0003

Do not introduce a strict sequencing dependency among ADR-0003 and ADR-0001/0002/0004/0005 unless the current corpus explicitly establishes one.

---

## ADR-0004

Preserve:

> The selected implementation must provide an enforcement mechanism preserving the source-defined non-deletability of `AuditRecord`; the exact administrative enforcement mechanism is not specified upstream and must be validated during implementation evaluation.

Do not invent the enforcement mechanism.

---

## ADR-0005

Preserve provider neutrality and its traceability to:

* NFR-005
* OBJ-013
* H08
* CAR
* architecture provider/CAR sections

Do not select a provider.

---

# 45. NO-FABRICATION RULE

Never invent:

* technology selections
* vendor selections
* cloud services
* databases
* cache products
* telemetry products
* locking products
* provider choices
* benchmark results
* performance numbers
* latency targets
* throughput targets
* cost
* staffing
* schedules
* SLAs
* certifications
* production readiness
* approvals
* ADR acceptance
* implementation completion

When information is missing:

`Not established in authoritative corpus.`

or:

`Implementation validation required.`

---

# 46. REQUIRED RE-VERIFICATION METHOD

For the critical gate claims, do not rely solely on prose summary sections.

Use repository inspection/search/count methods against the live files.

At minimum independently verify:

1. source-gap status
2. contradiction status
3. `BLOCKED BY ADR`
4. six ADR statuses
5. ADR dependency graph
6. P0 item list
7. P0 prerequisites
8. ADR interim paths
9. terminal/readiness markers where applicable
10. final file scope

Where a count is reported as independently verified, retain enough evidence in the document to show how it was derived.

Do not expose tool-internal details unnecessarily, but document the verification basis/method.

---

# 47. FINAL SELF-AUDIT

Before delivery, perform all of the following.

### Corpus

* [ ] Current authoritative corpus identified.
* [ ] Expected artifacts exist.
* [ ] No stale artifact was silently substituted.

### Requirements

* [ ] Requirements counts independently checked.
* [ ] Traceability coverage checked.
* [ ] AC coverage checked.

### Source gaps

* [ ] Blocking status independently verified.
* [ ] Open gaps not treated automatically as blockers.
* [ ] New discrepancies recorded if found.

### Contradictions

* [ ] Current contradiction state independently checked.
* [ ] No contradiction silently resolved.

### ADRs

* [ ] Six ADRs exist.
* [ ] Six ADRs remain PROPOSED.
* [ ] Zero accepted ADRs unless live corpus explicitly says otherwise.
* [ ] `BLOCKED BY ADR` independently verified.
* [ ] Dependency graph independently verified.
* [ ] Interim paths verified for ADR-touched P0/P1 capabilities.

### P0

* [ ] Every P0 item identified.
* [ ] Every P0 item's prerequisites examined.
* [ ] P0 blockers explicitly identified.
* [ ] P0 conditions explicitly identified.
* [ ] First implementable slice explicitly identified.

### Semantics

* [ ] AuditRecord distinction preserved.
* [ ] Five XEC states preserved.
* [ ] XEC ≠ distributed locking preserved.
* [ ] Cache four-check distinction preserved.
* [ ] Provider neutrality preserved.
* [ ] P0 ≠ production readiness preserved.

### Scope

* [ ] Only implementation-readiness-gate.md created/changed.
* [ ] No upstream document changed.
* [ ] No ADR changed.
* [ ] No code created.

### Verdict

* [ ] Verdict supported by evidence.
* [ ] Verdict occurs exactly once.
* [ ] Verdict is final line.

---

# 48. FAILURE MODES TO DEFEND AGAINST

Do NOT:

* copy readiness numbers without re-verification
* declare PASS because previous documents said PASS
* treat open source gaps as automatically blocking
* treat no-downstream requirements as automatically blocking
* treat proposed ADRs as accepted
* treat proposed ADRs as automatically blocking
* infer interim paths that are not documented
* infer technology selections
* claim all P0 items are ready without checking prerequisites
* claim P1–P5 are ready because P0 is ready
* claim implementation has begun
* claim production readiness
* modify upstream documents
* silently reconcile contradictory source material
* reduce the XEC five-state model to four states
* collapse execution state into AuditRecord
* equate XEC coordination with locking
* equate cache readiness with a selected backing store

---

# 49. COMPLETION REPORT

After generating the file, provide a concise execution report outside the artifact containing:

* file created
* document ID
* overall gate result
* P0 result
* number of verified P0 blockers
* number of P0 conditions
* ADR count/status
* blocking source-gap result
* contradiction result
* upstream files modified
* implementation files modified

Only report values actually verified from the repository.

---

# 50. FINAL INSTRUCTION

Start by reading the current:

1. ADR index — especially its Implementation Readiness Boundary
2. requirements-traceability.md
3. implementation-plan.md
4. all six ADRs
5. root CLAUDE.md
6. the relevant P0/source artifacts required to verify P0 prerequisites

Then independently re-derive the critical gate facts.

Only after the verification is complete, generate:

`docs/implementation-readiness-gate.md`

Create no other file.

Do not modify any existing file.

The document must end with exactly one evidence-supported terminal verdict.

# END MASTER PROMPT
