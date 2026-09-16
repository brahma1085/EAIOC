# V2 CONSOLIDATED MASTER PROMPT — Generate `docs/security.md`

## 0. Mission

Generate **only**:

`docs/security.md`

This document is the canonical **Level 0 — RESEARCH / PRE-IMPLEMENTATION** elaboration of EAIOC security, governance, safety, authorization, tenant isolation, data protection, content integrity, tool/MCP trust, human approval, and security-aware optimization controls.

This document must elaborate the concrete Control-Plane mechanisms that are currently abstract or deferred in the upstream architecture while preserving all existing architectural boundaries.

The document must particularly provide concrete, evidence-tagged elaboration for:

* SGE — Spend Governance Engine
* DGE — Data Governance Engine
* TMG — Tool/MCP Trust Gate
* HAG — Human Approval Gate
* CIS — Content Integrity Screen

The deepest mechanism elaboration must be for:

1. **CIS — Content Integrity Screen**, particularly prompt-injection/content-integrity screening.
2. **TMG — Tool/MCP Trust Gate**, particularly tool identity, schema integrity, trust and staleness.

The document must also provide the broader security control-plane model needed to govern:

* optimization admission;
* context and data transformation;
* retrieval;
* caching;
* agent memory;
* sub-agents and multi-agent workflows;
* coding/developer agents;
* RAG;
* tools and MCP;
* providers and models;
* quality gates;
* recovery and checkpoint restoration;
* supersession;
* deletion/erasure;
* auditability;
* cross-tenant isolation;
* consequential actions.

Do not implement the system.

Do not create application code.

Do not create production security infrastructure.

Do not silently modify any upstream or downstream document.

---

# 1. Authoritative Sources

Before writing `security.md`, read the **actual current repository versions** of all applicable sources.

Use this authority order:

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
13. `quality-gates.md`
14. `CLAUDE.md`

If another current repository document is explicitly referenced by the above documents and materially affects security, inspect it as supporting evidence without changing the authority order.

### Required source responsibilities

### 1. Problem Statement

Extract and preserve:

* original security requirements;
* SEC requirements;
* security-related objectives;
* H05/H07/H11/H13/H14 or their actual current identifiers;
* governance/safety requirements;
* original security assumptions and constraints.

### 2. Engineering Specification

Verify all security requirements and their relationship to the Problem Statement.

### 3. `architecture.md`

Read and verify:

* Security and Governance Requirements;
* SEC-001 through current SEC requirements;
* Governance/Safety Plane;
* SGE;
* DGE;
* TMG;
* HAG;
* CIS;
* SPC;
* NFR-014;
* Decision Explainability/Audit;
* security precedence;
* state/revalidation requirements;
* `SOURCE-GAP-ARCH-02`;
* all security-related architecture invariants.

Do not assume section numbers are unchanged. Verify them in the actual file.

### 4. `interfaces.md`

Read and preserve the actual security-related schemas and interfaces, especially:

* authentication;
* authorization;
* tenant isolation;
* PII/data classification;
* SGE;
* DGE;
* TMG;
* HAG;
* CIS;
* audit records.

Do not redefine interface schemas owned by `interfaces.md`.

### 5. `conventions.md`

Verify:

* governance package structure;
* security/governance ordering;
* non-shedding behavior;
* naming/ID conventions;
* failure-handling conventions.

### 6. `edge-cases.md`

Search the actual current file for:

* prompt injection;
* content integrity;
* compressed context;
* retrieval poisoning;
* tool/MCP trust;
* schema changes;
* staleness;
* authorization;
* tenant leakage;
* PII;
* deletion;
* security failures;
* recovery;
* security-aware optimization.

Do **not** hardcode EC numbers from this prompt.

### 7. `scenario-matrix.md`

Verify actual current scenarios covering:

* prompt injection;
* malicious tool output;
* PII;
* privilege escalation;
* classification failure;
* deletion propagation;
* retrieved-content injection;
* screening unavailable;
* MCP results;
* authorization;
* tenant isolation;
* security-aware optimization;
* recovery.

Candidate IDs may include security-domain IDs such as `SCN-SEC-*`, but **every ID must be verified against the current file before citation**.

### 8. `optimization-catalog.md`

Preserve:

* security precedence;
* data-governance constraints;
* authorization constraints;
* tenant isolation;
* negative optimization;
* `DO_NOT_OPTIMIZE`;
* source-gap/deferred security mechanisms;
* optimization-security boundaries.

Do not duplicate the optimization catalog.

### 9. `provider-matrix.md`

Use only for provider/model security facts and eligibility/accessibility.

Preserve the distinction between:

* Discovered;
* Accessible;
* Available;
* Authorized;
* Recommended;
* Eligible.

Never treat provider accessibility as authorization.

### 10. `cache-strategy.md`

Preserve the **exact seven canonical cache types**:

1. EXACT
2. SEMANTIC
3. TOOL_RESULT
4. EMBEDDING
5. PROVIDER_NATIVE
6. APPLICATION
7. CONTEXT

Do not redefine cache architecture, cache economics, TTL policy or canonical cache types.

Security must govern cache reuse.

### 11. `agent-optimization.md`

Preserve:

> Agent may own working memory, but the Control Plane owns execution truth.

Use the actual current sub-agent handoff schemas and security implications.

Do not redefine agent optimization.

### 12. `inference-optimization.md`

Use only where security affects Layer 3 inference/serving negotiation.

Do not redefine P5 mechanics.

### 13. `quality-gates.md`

Preserve:

* quality/security distinction;
* quality validation;
* per-optimization validation;
* verifier calibration;
* quality evidence.

Security must take precedence over quality acceptance.

A quality PASS must never authorize an otherwise unauthorized, unsafe, integrity-compromised, or policy-violating operation.

### 14. `CLAUDE.md`

Re-read before generation.

Follow all repository-specific:

* scope rules;
* ID conventions;
* evidence discipline;
* maturity-level requirements;
* file-generation rules;
* fail-open/fail-closed rules;
* validation rules.

---

# 2. Central Security Invariant

Place this prominently near the beginning of the document:

> **No optimization or execution decision may weaken, bypass, invalidate, silently reinterpret, or override a security, authorization, policy, tenant-isolation, data-governance, integrity, or human-approval requirement.**

Also state explicitly:

> **Fail-open on optimization, fail-closed on security — these are different categories of failure.**

An optimization failure may fall back to an unoptimized representation **only if the request remains authorized, policy-compliant, security-valid and otherwise safe to continue**.

A failure to establish or validate:

* authorization;
* policy;
* tenant isolation;
* data classification;
* content integrity;
* tool/MCP trust;
* required human approval;
* security-critical state;

must not silently fall back to ordinary processing.

The appropriate response is a security-safe outcome such as:

* deny;
* reject;
* quarantine;
* pause;
* require revalidation;
* require human approval;
* restore a known-safe state;
* abort safely.

Never convert a security failure into an optimization fallback.

---

# 3. Security Precedence

Use this precedence consistently:

**Security / Authorization / Policy

> Quality Requirements
> Correctness / Integrity
> Optimization Benefit
> Cost / Latency Preference**

Optimization may improve cost, latency or token consumption only when higher-order constraints remain satisfied.

---

# 4. Stateful Security Model

EAIOC is a stateful execution Control Plane.

Security decisions must not be treated as permanently valid merely because they were valid at execution start.

Relevant state can change during execution:

* user intent;
* identity;
* permissions;
* tenant;
* policy;
* context;
* files;
* repository;
* workflow;
* tools;
* MCP servers;
* models;
* providers;
* memory;
* cache;
* external state;
* data classification;
* deletion state;
* quality requirements;
* budget;
* execution phase.

Security-sensitive decisions must therefore carry sufficient conceptual linkage to the state under which they were made.

When relevant state changes, security must be re-evaluated.

---

# 5. Canonical Security Entry IDs

Introduce exactly five document-local canonical entries:

| ID        | Component                      |
| --------- | ------------------------------ |
| `GSP-001` | SGE — Spend Governance Engine  |
| `GSP-002` | DGE — Data Governance Engine   |
| `GSP-003` | TMG — Tool/MCP Trust Gate      |
| `GSP-004` | HAG — Human Approval Gate      |
| `GSP-005` | CIS — Content Integrity Screen |

Use the exact current component names from `architecture.md`.

Do not create new `SEC-NNN` IDs.

Do not rename or renumber existing:

* SEC-NNN;
* INTF-NNN;
* TECH-NNN;
* DA-NNN;
* AL-NNN;
* AR-NNN;
* CACHE-NNN;
* PROV-NNN;
* INFOPT-NNN;
* QG-NNN.

`GSP-NNN` is document-local and must not replace upstream identifiers.

---

# 6. Required Fields for Every GSP Entry

Each of the five entries must contain:

1. Component
2. Restated Purpose
3. Restated Failure Behavior
4. Security Responsibility
5. Concrete Control-Plane Mechanism
6. Preconditions
7. Inputs
8. Relevant Security State
9. Interface Cross-Reference
10. Independence Invariant
11. Fail-Closed Behavior
12. Anti-Shedding Guarantee
13. Revalidation Trigger
14. Recovery Behavior
15. Audit/Provenance Requirement
16. Tenant-Isolation Requirement
17. Data-Governance Requirement
18. Optimization Interaction
19. Quality Interaction
20. Validated By
21. Traceability
22. Evidence / Maturity Label

Do not claim implementation-level validation.

---

# 7. GSP-001 — SGE

Elaborate the Control Plane's security/governance responsibility for spend.

Cover:

* budget authorization;
* spend limits;
* per-tenant constraints;
* execution-level limits;
* provider/model cost constraints;
* optimization economics;
* circuit breakers;
* excessive-cost handling;
* budget state changes;
* stale budget decisions;
* concurrent execution effects;
* recovery;
* audit.

Critical invariant:

> A request being within budget does not imply that it is authorized, safe, policy-compliant or trustworthy.

SGE must never substitute for:

* authorization;
* tenant isolation;
* CIS;
* TMG;
* HAG.

Preserve SEC-011 if present in the authoritative sources.

---

# 8. GSP-002 — DGE

Elaborate:

* data classification;
* sensitivity;
* PII;
* confidential data;
* minimization;
* allowed transformations;
* cache eligibility;
* embedding eligibility;
* provider transmission constraints;
* tool/MCP transmission constraints;
* memory eligibility;
* logging eligibility;
* checkpoint eligibility;
* retention;
* deletion/erasure;
* deletion propagation.

### Mandatory rule

> **Data classification must occur before security-sensitive optimization decisions that could transform, transmit, cache, embed, summarize, compress, log or persist the data.**

Classification must influence whether a transformation is:

* allowed;
* restricted;
* prohibited;
* requires revalidation;
* requires approval.

Do not invent a universal classification taxonomy if the authoritative sources define a different one.

Any proposed taxonomy must be clearly labeled as proposed methodology.

---

# 9. GSP-003 — TMG

TMG requires deep mechanism elaboration.

Cover:

### 9.1 Identity Authentication

Explain how tool/server identity is established before trusting capability metadata.

### 9.2 Authorization

Verify:

* caller;
* tenant;
* requested capability;
* target resource;
* current policy;
* current authorization.

### 9.3 Schema Integrity

Provide a concrete proposed mechanism for:

`validate_schema()`

Potential approaches may include:

* schema hash comparison;
* structural diff;
* canonical serialization;
* version comparison.

Any specific hashing algorithm, canonicalization method or diff granularity not directly supported by sources must be labeled:

**PROPOSED METHODOLOGY — VALIDATE LOCALLY**

A schema change must be treated as a trust/staleness event unless explicitly revalidated.

### 9.4 Staleness

Explain how:

`check_staleness()`

operates.

Any concrete interval must be:

**PROPOSED DEFAULT — VALIDATE LOCALLY**

Do not invent a production-approved interval.

### 9.5 Trust Decision

Explain:

`trust_decision()`

and distinguish:

* identity trust;
* schema integrity;
* authorization;
* freshness;
* policy;
* capability compatibility;
* runtime availability.

### 9.6 ROI Independence

A tool being cheap or token-efficient must never imply that it is trusted.

TE-001 or equivalent optimization ROI information cannot substitute for TMG.

### 9.7 CIS Independence

A `TRUSTED` tool does **not** imply that the tool's result content is trusted.

Tool trust and content trust are separate security decisions.

A trusted MCP server can still return content originating from an untrusted upstream source.

Therefore:

**TMG does not replace CIS.**

---

# 10. GSP-004 — HAG

Cover:

* consequential actions;
* irreversible actions;
* designated high-risk operations;
* approval requirements;
* approval state;
* approver identity;
* approval expiry/staleness;
* policy-driven approval;
* tenant-specific approval;
* denied approval;
* unavailable approval service;
* approval revocation;
* recovery.

A cost-efficient operation must not bypass required approval.

A quality PASS must not bypass required approval.

An optimization result must not be used to hide or alter an approval-required action.

Human approval is not equivalent to authorization; preserve the distinction if the sources support it.

---

# 11. GSP-005 — CIS

This is the deepest security mechanism section.

## 11.1 Purpose

CIS protects EAIOC from untrusted or adversarial content that may influence downstream model/agent/tool behavior.

The mechanism must cover content originating from all applicable external-content sources.

Verify the actual current `ExternalContentItem.source` enum in `interfaces.md`.

If the current schema contains the five sources below, cover all five:

* `RAG_CHUNK`
* `SEARCH_RESULT`
* `TOOL_RESULT`
* `SUBAGENT_HANDOFF`
* `MCP_RESULT`

Do not assume these identifiers exist without verification.

---

## 11.2 Layered Detection

Describe a layered mechanism such as:

1. deterministic/heuristic screening;
2. normalization/decoding checks;
3. structural/instruction-boundary analysis;
4. model-based injection assessment;
5. policy decision;
6. quarantine/rejection.

Any concrete detector design not source-defined must be labeled:

**PROPOSED METHODOLOGY — VALIDATE LOCALLY**

Do not claim benchmark accuracy.

---

## 11.3 Injection Assessment

Use the actual current interface schema.

If the interface contains fields such as:

* technique detected;
* confidence;
* severity;

explain how these contribute to the security decision without redefining the schema.

Any numerical confidence threshold must be:

**PROPOSED DEFAULT — VALIDATE LOCALLY**

---

## 11.4 Status Mapping

Explain the mapping to actual current screening outcomes, such as:

* PASS;
* REJECT;
* QUARANTINE.

Verify actual enum values first.

Do not invent status values.

---

## 11.5 Screening Ordering

Security screening must occur before content is allowed to influence security-sensitive downstream processing.

Explicitly address ordering relative to:

* admission;
* ranking;
* retrieval;
* compression;
* summarization;
* caching;
* embedding;
* tool execution;
* model invocation;
* agent planning.

Do not claim that parallel execution is automatically safe merely because screening is technically concurrent.

---

## 11.6 Transformation and Re-Screening

Explicitly address:

**screen → transform → use**

and:

**screen → transform → re-screen → use**

A transformation that can materially alter the effective instruction-following surface must trigger appropriate re-screening.

A transformation that can only remove already-screened content may have a different treatment, but this must be justified by the security model.

Do not make blanket assumptions.

---

## 11.7 Five Content Sources

Explain security handling for every applicable external-content source.

Particularly:

### Tool Result

TMG trust does not imply result-content trust.

### MCP Result

MCP server trust does not imply that the returned content is safe.

### Sub-Agent Handoff

Sub-agent output must be treated according to the actual security/content-origin model.

Do not assume that an internal sub-agent is automatically a trusted-content boundary.

---

## 11.8 Prompt Injection Through Compressed Context

Explicitly address the established edge case in which compression can preserve malicious instruction content.

Security screening must not be bypassed merely because content was:

* summarized;
* compressed;
* ranked;
* transformed;
* deduplicated.

Verify the actual EC identifier before citing it.

---

# 12. Source-Gap-ARCH-02

Explicitly investigate the current `architecture.md` status of:

`SOURCE-GAP-ARCH-02`

Do not assume it has the same status as when this prompt was written.

If the current architecture says the CIS/TMG mechanism is deferred here, provide the concrete mechanism.

However:

> This document must **not silently modify `architecture.md` or claim that an upstream gap has been resolved merely because this document proposes a mechanism.**

For CIS specifically:

* if no local benchmark exists;
* if no local classifier validation exists;
* if no measured false-positive/false-negative evidence exists;

state that the document **narrows the architectural design gap by providing a concrete testable design**, but does **not claim empirical validation or production closure**.

---

# 13. Optimization × Security

Security must be integrated into optimization admission.

Required conceptual flow:

```text
Resolve Identity / Tenant / Authorization / Policy
        ↓
Resolve Data Classification
        ↓
Validate Content Integrity
        ↓
Validate Tool / MCP Trust if applicable
        ↓
Validate Provider / Model Security Eligibility
        ↓
Resolve Required Human Approval
        ↓
Establish Security Preconditions
        ↓
Apply ONE Optimization
        ↓
Evaluate Security Impact of Transformation
        ↓
Revalidate if required
        ↓
Security ALLOW / REJECT / QUARANTINE / ESCALATE
        ↓
Only then admit result downstream
```

Security validation must never be replaced by token savings.

---

# 14. Per-Optimization Security Validation

Preserve the quality-gates requirement that each optimization is individually validated.

For security-sensitive transformations:

```text
Sᵢ
 ↓
Optimization Oᵢ
 ↓
Sᵢ'
 ↓
Security Validation
 ↓
ALLOW / REJECT / QUARANTINE / REVALIDATE / ESCALATE
 ↓
Only permitted state continues
```

Do not allow:

```text
O1 → O2 → O3 → Security Check
```

when an intermediate optimization can affect:

* authorization;
* content integrity;
* data classification;
* tenant isolation;
* tool trust;
* policy;
* security-sensitive context.

The security model must coexist with the Quality Gate model.

---

# 15. Tenant Isolation

Security must be tenant-scoped across all applicable execution artifacts.

Explicitly cover:

* request;
* identity;
* authorization;
* context;
* files;
* retrieval;
* embeddings;
* tool results;
* MCP results;
* model/provider interactions;
* cache;
* agent memory;
* sub-agent handoffs;
* checkpoints;
* workflow state;
* optimization evidence;
* quality evidence;
* security evidence;
* provenance;
* audit records;
* logs;
* metrics.

A cross-tenant cache hit must never become a security-valid reuse merely because the content is semantically similar.

Tenant isolation must remain independent of optimization economics.

---

# 16. Cache Security

Do not redefine `cache-strategy.md`.

Instead explain security constraints for its seven canonical cache types.

Security must consider:

* tenant;
* authorization;
* policy;
* data classification;
* context version;
* workflow version;
* source freshness;
* deletion state;
* provenance;
* poisoning;
* authorization revocation.

Cover:

* cache poisoning;
* cross-tenant leakage;
* stale authorization;
* deleted data;
* sensitive-data persistence;
* unauthorized reuse.

Provider-native caching must remain distinct from EAIOC-owned cache governance.

---

# 17. Agent Memory Security

Preserve:

> **Agent may own working memory, but the Control Plane owns execution truth.**

Cover security of:

* working memory;
* persistent memory;
* scratch state;
* sub-agent handoffs;
* tool observations;
* retrieved content;
* memory-derived context;
* memory poisoning;
* stale authorization;
* cross-tenant contamination.

Security-sensitive memory reuse must be revalidated against current execution state.

---

# 18. Multi-Agent / Sub-Agent Security

Cover:

* parent/child authorization;
* tenant propagation;
* security-context propagation;
* least privilege;
* tool permissions;
* data boundaries;
* handoff integrity;
* untrusted child output;
* child failure;
* child compromise;
* supersession;
* cancellation;
* result admission.

Do not assume that parent authorization automatically authorizes every child action.

Do not assume that a sub-agent result is trusted merely because the sub-agent belongs to the same EAIOC execution.

---

# 19. Coding / Developer Agent Security

Cover:

* repository access;
* file access;
* secrets;
* command execution;
* tool access;
* external network access;
* generated code;
* patches;
* destructive operations;
* branch/worktree state;
* dependency changes;
* approval requirements;
* untrusted repository content.

Do not implement sandbox infrastructure here.

Define the Control Plane security decisions and boundaries.

---

# 20. RAG Security

Cover:

* malicious retrieved content;
* retrieval poisoning;
* untrusted source content;
* citation/content provenance;
* tenant boundaries;
* stale permissions;
* data classification;
* content-integrity screening;
* retrieval-result admission;
* cache reuse.

Retrieved content must not become trusted merely because retrieval itself was authorized.

---

# 21. Provider / Model Security

Use `provider-matrix.md` as the authority for provider/model eligibility.

Security must distinguish:

* discovered;
* accessible;
* available;
* authorized;
* recommended;
* eligible.

Do not route to an unauthorized or security-incompatible provider/model.

Security constraints must survive provider switching and recovery.

Provider-native security controls may be complementary, but they must not automatically replace EAIOC-owned security controls unless the authoritative architecture explicitly says so.

---

# 22. Recovery and Supersession

Security must participate in recovery.

Preserve:

> **Resume != replay.**

On recovery, reconcile:

* identity;
* tenant;
* authorization;
* policy;
* data classification;
* tool trust;
* content integrity;
* approval state;
* provider/model eligibility;
* checkpoint state;
* external state.

Do not blindly replay security-sensitive operations.

If authorization was revoked after checkpoint creation, the checkpoint cannot simply be resumed unchanged.

If an execution becomes superseded, it must not continue unwanted security-sensitive side effects.

---

# 23. Deletion / Erasure

Cover propagation of deletion/erasure across applicable EAIOC-controlled artifacts:

* cache;
* memory;
* optimization evidence;
* quality evidence;
* checkpoints;
* provenance;
* audit/logging where applicable;
* persisted context.

Distinguish:

1. EAIOC-controlled storage;
2. external/provider-controlled storage.

Do not claim EAIOC can delete data from an external provider unless the actual integration contract supports it.

---

# 24. Security + Quality

Explicitly define the boundary:

* Quality Gate determines whether an optimization meets quality requirements.
* Security determines whether the operation/content/state is security-valid.
* Quality PASS does not override security failure.
* Security PASS does not replace quality validation.

Both gates may be required.

---

# 25. Human Approval + Security

Human approval must be resolved according to policy.

Do not treat:

* low cost;
* high quality;
* low latency;
* model confidence;
* tool trust;

as substitutes for mandatory approval.

Approval must be linked to the relevant action/state sufficiently to avoid stale approval reuse.

---

# 26. Security + DO_NOT_OPTIMIZE

Security can require:

`DO_NOT_OPTIMIZE`

when an optimization would:

* expose sensitive information;
* weaken integrity;
* alter security-critical instructions;
* cross a tenant boundary;
* invalidate authorization;
* prevent required auditability;
* bypass approval;
* create unacceptable security uncertainty.

This is a valid negative optimization outcome.

Do not treat every skipped optimization as a failure.

---

# 27. Observability and Audit Boundary

Reference, but do not redefine, `observability.md` when it becomes authoritative.

This document owns conceptual security audit requirements.

Each security decision should be explainable through the existing audit/provenance model, including where supported:

* decision;
* reason;
* security state/version;
* tenant;
* policy version;
* authorization context;
* data classification;
* tool/provider identity;
* content-screening result;
* approval state;
* revalidation reason;
* recovery action.

Detailed telemetry/dashboard implementation belongs to `observability.md`.

---

# 28. Security Control Algorithms

Include conceptual Level-0 algorithms / structured pseudocode for:

### Algorithm A — Resolve Security Context

Resolve identity, tenant, authorization and applicable policy.

### Algorithm B — Classify Data Before Optimization

Determine applicable data sensitivity/classification before security-sensitive transformation.

### Algorithm C — Security Admission

Determine whether execution/optimization may proceed.

### Algorithm D — Validate Content Integrity

Screen external/untrusted content and determine PASS/REJECT/QUARANTINE or actual current equivalents.

### Algorithm E — Validate Tool/MCP Trust

Authenticate identity, validate schema/integrity, freshness, authorization and trust.

### Algorithm F — Validate Provider/Model Security Eligibility

Resolve security eligibility independently from recommendation/economics.

### Algorithm G — Security-Aware Cache Reuse

Validate tenant, authorization, policy, freshness, classification, deletion state and provenance before reuse.

### Algorithm H — Security Revalidation

Re-evaluate security-sensitive decisions after relevant state changes.

### Algorithm I — Security Failure / Recovery

Deny, quarantine, pause, restore, escalate or abort safely according to the failure.

### Algorithm J — Security Audit Record

Create the conceptual security decision/audit record using the existing audit contract.

### Algorithm K — Human Approval Resolution

Determine whether approval is required, valid, stale, denied or unavailable.

### Algorithm L — Security-Aware Deletion Propagation

Propagate deletion/erasure across EAIOC-controlled artifacts and record external-system boundaries.

Algorithms must remain conceptual.

Do not introduce implementation-specific classes, APIs, databases or deployment assumptions.

---

# 29. Required Security Matrices

Include at minimum:

## Matrix 1 — Security Control Catalog

Cover `GSP-001` through `GSP-005`.

## Matrix 2 — Threat × Security Control

Include threats such as:

* prompt injection;
* indirect injection;
* malicious retrieved content;
* malicious tool output;
* MCP content;
* schema tampering;
* stale tool metadata;
* privilege escalation;
* authorization revocation;
* tenant leakage;
* cache poisoning;
* memory poisoning;
* sensitive-data leakage;
* deletion failure;
* stale checkpoint;
* stale approval;
* provider/model security mismatch.

Only label a threat as source-defined if the sources actually define it.

## Matrix 3 — Optimization × Security

Cover:

* context optimization;
* compression;
* summarization;
* truncation;
* retrieval optimization;
* caching;
* tool optimization;
* MCP optimization;
* agent optimization;
* sub-agent optimization;
* model/provider routing;
* reasoning/output optimization;
* inference/serving optimization.

## Matrix 4 — Data Classification × Allowed Transformation

Use the actual authoritative classification model.

If the model is incomplete, clearly mark proposed methodology.

## Matrix 5 — Security Failure / Recovery

Include:

* security service unavailable;
* authorization unavailable;
* classification unavailable;
* CIS unavailable;
* TMG unavailable;
* HAG unavailable;
* stale state;
* policy change;
* authorization revocation;
* tenant mismatch;
* deletion request;
* checkpoint security mismatch.

## Matrix 6 — Security State / Revalidation

Show security state changes and required revalidation.

## Matrix 7 — Tenant Isolation

Map tenant boundaries across:

* context;
* files;
* retrieval;
* cache;
* memory;
* tools;
* providers;
* checkpoints;
* logs;
* evidence;
* audit.

## Matrix 8 — Security Traceability

Map:

`SEC → GSP → Interface → Edge Case → Scenario → Objective / Acceptance Criterion`

Use only verified identifiers.

---

# 30. Security Control Catalog

Create a document-local security-control catalog if useful, using a new document-local ID series such as:

`SC-001`, `SC-002`, ...

Do not confuse these with existing `SEC-NNN` architecture requirements.

Each control should identify:

* control;
* purpose;
* applicable component;
* trigger;
* prerequisite;
* decision;
* failure behavior;
* revalidation;
* audit;
* validated-by references;
* maturity/evidence status.

Do not invent controls as if they were source-defined requirements.

Clearly distinguish:

**SOURCE-DEFINED CONTROL**

from:

**PROPOSED METHODOLOGY**

---

# 31. Security Requirements SEC-011 Through SEC-016

Verify the actual current architecture/source definitions.

Where applicable, explicitly preserve and elaborate:

* SEC-011 — spend limits / circuit breakers must never bypass authorization, policy or security;
* SEC-012 — sensitive-data classification before optimization;
* SEC-013 — deletion/erasure propagation;
* SEC-014 — tool/MCP identity, integrity and trust;
* SEC-015 — human approval for designated consequential/irreversible actions;
* SEC-016 — prompt-injection/content-integrity screening before optimization admission/action.

Do not assume these exact descriptions remain unchanged.

Read the authoritative sources and use their actual wording.

---

# 32. Security Threat / Risk Methodology

A threat/risk catalog may be included.

However, clearly distinguish:

* source-defined threat;
* derived threat from source requirements;
* proposed methodology.

Do not present a model-generated threat taxonomy as authoritative.

Do not invent probability, severity or risk scores unless the source defines them.

---

# 33. Evidence Discipline

Every concrete detail must be classified appropriately.

Use labels such as:

* `SOURCE-DEFINED`
* `SOURCE-DERIVED`
* `PROPOSED METHODOLOGY`
* `PROPOSED DEFAULT — VALIDATE LOCALLY`
* `REFERENCE-ONLY`
* `NOT YET VALIDATED`

Never present:

* invented thresholds;
* invented intervals;
* invented classifier accuracy;
* invented false-positive rates;
* invented provider guarantees;
* invented security guarantees;

as validated facts.

---

# 34. Scenario / Edge-Case Discipline

Every cited:

* `EC-NNN`;
* `SCN-NNN`;
* `SCN-SEC-NNN`;
* `AC-NNN`;
* `OBJ-NNN`;
* `SEC-NNN`;
* `INTF-NNN`;

must be verified against the actual current repository.

Never fabricate IDs.

If a mechanism has no dedicated scenario, state honestly:

* `NO DEDICATED SCENARIO`
* `THIN COVERAGE`
* `ANALOGOUS COVERAGE`

as appropriate.

Do not convert conceptual coverage into false direct coverage.

---

# 35. Cross-Document Boundary Discipline

Use this boundary model:

| Concern                                 | Owner                                      |
| --------------------------------------- | ------------------------------------------ |
| Security requirements                   | `problemStatement.txt` / `architecture.md` |
| Security interfaces                     | `interfaces.md`                            |
| Security conventions                    | `conventions.md`                           |
| Concrete SGE/DGE/TMG/HAG/CIS mechanisms | **`security.md`**                          |
| Provider/model facts                    | `provider-matrix.md`                       |
| Cache architecture/economics            | `cache-strategy.md`                        |
| Agent optimization                      | `agent-optimization.md`                    |
| P5 inference optimization               | `inference-optimization.md`                |
| Quality methodology                     | `quality-gates.md`                         |
| Evaluation methodology                  | `eval.md`                                  |
| Telemetry/dashboard implementation      | `observability.md`                         |
| Scaling                                 | `SCALING.md`                               |
| Requirements traceability               | `requirements-traceability.md`             |
| Architectural decisions                 | ADRs                                       |

Do not duplicate downstream documents.

Do not prematurely create downstream content.

---

# 36. Anti-Patterns

Include security anti-patterns, including at minimum:

1. Security fallback treated as optimization fallback.
2. Security failure converted to PASS.
3. Authorization inferred from budget approval.
4. Trust inferred from token efficiency.
5. Provider accessibility treated as authorization.
6. Quality PASS overriding security failure.
7. Human approval bypassed because cost is low.
8. TMG trust replacing CIS.
9. CIS replacing TMG.
10. Screening only direct user input.
11. Retrieved content treated as trusted.
12. Tool output automatically treated as trusted.
13. MCP output exempted from screening.
14. Sub-agent output automatically trusted.
15. Compression assumed to remove injection.
16. Screening performed only after optimization.
17. Batch validation after multiple security-sensitive transformations.
18. Failed security state propagated downstream.
19. Stale authorization reused.
20. Stale approval reused.
21. Cross-tenant cache reuse.
22. Sensitive data cached without authorization/classification.
23. Deleted data remaining in derived artifacts.
24. Memory treated as execution truth.
25. Checkpoint resumed without security reconciliation.
26. Superseded execution continuing side effects.
27. Security controls shed under backpressure.
28. Provider-native safety treated as the sole EAIOC security layer.
29. Security thresholds presented as validated without evidence.
30. External research presented as local validation.
31. Invented EC/SCN/SEC/INTF IDs.
32. Security decisions without auditable rationale.

---

# 37. Level 0 Validation Strategy

This document is **RESEARCH / PRE-IMPLEMENTATION**.

Validation must therefore focus on:

* source consistency;
* architectural consistency;
* scenario coverage;
* edge-case coverage;
* requirement traceability;
* contradiction detection;
* mechanism completeness;
* state/revalidation completeness;
* failure-mode completeness;
* tenant-isolation completeness;
* cross-document boundary integrity.

Do not claim:

* production validation;
* classifier accuracy;
* benchmark performance;
* penetration-test results;
* measured false-positive/false-negative rates;
* production security effectiveness;

unless actual authoritative evidence exists.

---

# 38. Source Gaps

Use document-local identifiers:

`SOURCE-GAP-SECURITY-NN`

Explicitly record:

* missing security requirements;
* missing interface fields;
* missing scenario coverage;
* missing edge-case coverage;
* undefined thresholds;
* undefined classifier methodology;
* undefined staleness intervals;
* provider-security evidence gaps;
* deletion-boundary gaps;
* tenant-isolation gaps.

Clearly distinguish:

* blocking;
* non-blocking;
* inherited;
* proposed resolution.

Do not silently close upstream gaps.

---

# 39. Contradictions

Use:

`CONTRA-SECURITY-NN`

For every contradiction:

* identify both sources;
* quote/paraphrase the conflicting requirements accurately;
* apply the established authority order;
* explain the resolution;
* do not silently rewrite upstream documents.

---

# 40. Required Document Structure

Generate approximately this structure, adapting numbering only where necessary:

1. Header / Document Status
2. How to Read This Document
3. Security Scope and Central Invariant
4. Fail-Open Optimization vs Fail-Closed Security
5. Security Precedence
6. Stateful Security Model
7. `GSP-001` — SGE
8. `GSP-002` — DGE
9. `GSP-003` — TMG
10. `GSP-004` — HAG
11. `GSP-005` — CIS
12. CIS Mechanism Deep-Dive
13. TMG Mechanism Deep-Dive
14. Security-Aware Optimization Admission
15. Per-Optimization Security Validation
16. Tenant Isolation
17. Cache Security
18. Agent Memory Security
19. Multi-Agent/Sub-Agent Security
20. Coding-Agent Security
21. RAG Security
22. Provider/Model Security
23. Human Approval and Security
24. Quality and Security Boundary
25. Recovery / Supersession
26. Deletion / Erasure Propagation
27. Observability / Audit Boundary
28. Security Control Catalog
29. Security Algorithms
30. Required Security Matrices
31. Threat / Risk Methodology
32. Anti-Patterns
33. Cross-Document Boundary
34. Scenario / Edge-Case Coverage
35. Source Gaps and Contradictions
36. Validation Strategy
37. Final Report
38. `## Security Readiness`

---

# 41. Security Readiness

The document may conclude with:

`READY FOR NEXT DOCUMENTATION PHASE`

only if all of the following are satisfied:

* exactly five `GSP-NNN` entries;
* all five components covered;
* CIS mechanism concretely elaborated;
* TMG mechanism concretely elaborated;
* fail-closed behavior explicit;
* optimization fail-open distinction preserved;
* state/revalidation addressed;
* tenant isolation addressed;
* data governance addressed;
* deletion propagation addressed;
* tool/MCP trust addressed;
* CIS/TMG independence preserved;
* quality/security boundary preserved;
* cache security boundary preserved;
* agent/multi-agent/coding/RAG security addressed;
* recovery/supersession addressed;
* HAG addressed;
* required algorithms A–L present;
* required security matrices present;
* all cited identifiers verified;
* no fabricated IDs;
* source gaps documented;
* contradictions documented;
* proposed methodology explicitly labeled;
* `SOURCE-GAP-ARCH-02` honestly disposed;
* no implementation leakage;
* no upstream documents modified.

If blocking gaps remain, use:

`NOT READY — SECURITY GAPS MUST BE RESOLVED`

---

# 42. Final Report

The final report must include:

* document status;
* maturity level;
* GSP entry count;
* security-control count;
* algorithm count;
* matrix count;
* scenario coverage;
* edge-case coverage;
* requirement coverage;
* source-gap count;
* contradiction count;
* proposed-methodology count where useful;
* blocking vs non-blocking gaps;
* security/quality boundary confirmation;
* security/optimization precedence confirmation;
* tenant-isolation confirmation;
* CIS mechanism status;
* TMG mechanism status;
* `SOURCE-GAP-ARCH-02` disposition.

Explicitly state whether the document:

* closes;
* narrows;
* or leaves open

each relevant source gap.

Do not claim closure without authoritative evidence.

---

# 43. Final-File Integrity

Before declaring readiness:

1. Verify `docs/security.md` exists.
2. Read the actual generated file.
3. Verify all required sections.
4. Verify the five GSP entries.
5. Verify all cited IDs.
6. Verify source-gap/contradiction records.
7. Verify no implementation leakage.
8. Verify no upstream document changed.
9. Verify the final ~20 lines directly.
10. Ensure the readiness verdict occurs exactly once.
11. Ensure the readiness verdict is the **true final line of the file**.
12. Nothing may follow the final readiness verdict.

---

# 44. Repository Modification Discipline

Create/modify only:

`docs/security.md`

and the security-generation prompt if the repository process explicitly requires that prompt to be persisted.

Do not modify:

* problemStatement;
* Engineering Spec;
* architecture;
* interfaces;
* conventions;
* edge-cases;
* scenario-matrix;
* optimization-catalog;
* provider-matrix;
* cache-strategy;
* agent-optimization;
* inference-optimization;
* quality-gates;
* observability;
* eval;
* SCALING;
* requirements-traceability;
* ADRs.

Do not cascade into `observability.md`.

Stop after generating `security.md` and its final readiness report.

The next step is independent verification of the generated `security.md`.
