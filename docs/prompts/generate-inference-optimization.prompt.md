# V3 CONSOLIDATED MASTER PROMPT — Generate `docs/inference-optimization.md`

## 0. Mission

Generate the next authoritative downstream document:

`docs/inference-optimization.md`

This document is the canonical **Control-Plane elaboration of the 11 P5 (Layer 3) inference/serving capabilities** that `optimization-catalog.md` intentionally leaves at index level.

The document must go deep on **Control-Plane responsibility** for these capabilities:

1. Prefix/KV-cache reuse
2. KV-cache compression
3. Continuous batching
4. Request scheduling
5. Prefill optimization
6. Decode optimization
7. Speculative decoding
8. Quantization
9. Model replica selection
10. GPU utilization optimization
11. Inference queue management

The Control Plane owns **detection, routing/selection, negotiation, and measurement** of these capabilities.

It does **not** implement the underlying inference infrastructure.

The most important scope rule is:

> **Integration, not implementation.**

Do not turn this document into an inference-server implementation specification.

---

# 1. ABSOLUTE FILE-SCOPE RULE

Modify/create ONLY:

`docs/inference-optimization.md`

Do not modify:

* `problemStatement.txt`
* Engineering Specification
* `architecture.md`
* `interfaces.md`
* `conventions.md`
* `edge-cases.md`
* `scenario-matrix.md`
* `optimization-catalog.md`
* `provider-matrix.md`
* `cache-strategy.md`
* `agent-optimization.md`
* Any later downstream document
* `requirements-traceability.md`

Do not generate `quality-gates.md` or any other downstream document.

Do not silently modify upstream documents to resolve gaps or contradictions.

---

# 2. READ ALL AUTHORITATIVE UPSTREAM DOCUMENTS FIRST

Before generating the document, read the latest actual repository versions of:

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

Also read the repository's root `CLAUDE.md` if present.

Do not rely solely on previous summaries.

If filenames differ slightly in the actual repository, locate the current authoritative equivalent rather than inventing a new file.

---

# 3. AUTHORITY ORDER

Use this authority order:

1. Problem Statement
2. Engineering Specification
3. Architecture
4. Interfaces
5. Conventions
6. Edge Cases
7. Scenario Matrix
8. Optimization Catalog
9. Provider Matrix
10. Cache Strategy
11. Agent Optimization

Higher-authority material wins conflicts.

Do not silently reconcile contradictions.

Record genuine contradictions in a dedicated `SOURCE CONTRADICTIONS` section.

For unsupported formulas, thresholds, percentages, defaults, classifiers, or policies:

* `PROPOSED METHODOLOGY`
* `PROPOSED DEFAULT — VALIDATE LOCALLY`
* `ILLUSTRATIVE — NOT VERIFIED`
* or `SOURCE-GAP`

must be used as appropriate.

---

# 4. PRIMARY SCOPE — P5 INFERENCE/SERVING

The document must elaborate exactly these 11 P5 capabilities and must preserve their authoritative names and order from the current architecture/P5 index.

Create exactly:

* `INFOPT-001` Prefix/KV-cache reuse
* `INFOPT-002` KV-cache compression
* `INFOPT-003` Continuous batching
* `INFOPT-004` Request scheduling
* `INFOPT-005` Prefill optimization
* `INFOPT-006` Decode optimization
* `INFOPT-007` Speculative decoding
* `INFOPT-008` Quantization
* `INFOPT-009` Model replica selection
* `INFOPT-010` GPU utilization optimization
* `INFOPT-011` Inference queue management

Do not create additional `INFOPT` entries.

Do not create new `TECH`, `DA`, `AL`, or `AR` optimization IDs.

The `INFOPT-NNN` series is document-local to this P5 elaboration.

---

# 5. CRITICAL H17/H18 OWNERSHIP BOUNDARY

For every one of the 11 capabilities, the Control Plane may:

1. Detect whether the capability is exposed.
2. Route/select toward it when appropriate.
3. Negotiate capability-related parameters/hints through the provider adapter.
4. Measure the resulting effect independently.

The Control Plane must NOT implement the underlying infrastructure mechanism.

Examples of content that is OUT OF SCOPE:

* KV-cache engine internals
* KV-cache eviction algorithms
* GPU memory-management algorithms
* Continuous-batching queueing algorithms
* Provider scheduler implementation
* Speculative-decoding draft-model mechanics
* Speculative verification engine implementation
* Quantization algorithms
* Quantization bit-width optimization formulas
* GPU-kernel optimization
* GPU-utilization formulas
* Inference-server queue implementation
* Prefill/decode kernel implementation
* Replica infrastructure orchestration internals

If such implementation detail is required to explain a capability, describe it only at the conceptual integration boundary and explicitly defer implementation to the provider/inference-serving infrastructure.

Never claim EAIOC implements these mechanisms.

---

# 6. P5 FAIL-OPEN RULE

P5 capability unavailability must not unnecessarily block the request.

If:

* capability detection fails,
* capability is unavailable,
* capability is not exposed,
* negotiation fails,
* provider capability information is unknown,
* capability parameters cannot be negotiated,

the request should proceed without that P5 optimization where safe and permitted.

Preserve all higher-priority security, authorization, policy, correctness, safety, and execution-integrity requirements.

Do not interpret fail-open as bypassing security or policy.

---

# 7. DISTINGUISH THIS DOCUMENT FROM MODEL ROUTING

Do NOT redefine existing model-routing logic.

`agent-optimization.md` already owns agent-loop-facing routing/cascade decisions such as:

* `AR-001`
* `AR-004`

Architecture components such as:

* Model Router
* Task/Complexity Analyzer
* Reasoning Budget Controller
* Model Cascade/Escalation
* Batch/Async Optimizer

must be referenced rather than reimplemented.

This document owns the **P5 capability negotiation layer beneath or alongside an already-established provider/model decision**.

Example:

Correct:

> The Model Router selects the provider/model according to existing routing policy. The P5 layer then determines whether prefix-cache reuse is exposed for that selected provider/model and whether negotiating it has positive net value.

Incorrect:

> Define a new model-routing algorithm inside `INFOPT-001`.

Do not duplicate `AR-001` or `AR-004`.

---

# 8. REQUIRED STATE-AWARE CONTROL FLOW

Even though this document is narrower than general inference optimization, every P5 decision must respect the established EAIOC stateful execution model.

The conceptual flow should be:

Receive current execution state
→ Reconcile relevant state
→ Validate authorization/policy/governance
→ Identify selected provider/model and capabilities
→ Detect P5 capability
→ Determine applicability
→ Estimate expected benefit
→ Estimate Control-Plane negotiation/measurement overhead
→ Evaluate policy/security/tenant constraints
→ Decide whether to use/negotiate capability
→ Negotiate through provider adapter
→ Execute request through provider/infrastructure
→ Measure P5 effect separately
→ Verify result/attribution
→ Continue / fallback / recover as required

Do not redesign the architecture.

---

# 9. REQUIRED ENTRY STRUCTURE

Every `INFOPT-NNN` entry must contain the following fields.

## Capability

Use the authoritative capability name verbatim.

## Control-Plane Role

Explicitly state:

* awareness
* detection
* routing/selection
* negotiation
* measurement

Never claim infrastructure implementation ownership.

## Detection Mechanism

Explain how EAIOC determines whether the currently relevant provider/model/infrastructure exposes the capability.

Use:

* Capability/Availability Resolver
* ModelProfile
* provider/model capability metadata
* runtime capability/availability state

Reference `provider-matrix.md` rather than independently rebuilding provider facts.

## Applicability Conditions

Explain the conditions under which the capability may be relevant.

Do not create unsupported universal rules.

## Routing/Negotiation Policy

Describe when the Control Plane should route toward or negotiate the capability.

The decision must consider:

* expected benefit
* negotiation overhead
* latency
* cost
* quality
* policy
* authorization
* tenant constraints
* capability availability
* current execution state

Use existing Net Optimization Value methodology where applicable.

Do not invent a competing economic framework.

## Provider Adapter Negotiation

Describe only the Control-Plane-to-provider interaction.

Examples may include:

* capability hints
* cache-affinity hints
* batching eligibility hints
* scheduling preferences
* replica-selection preferences
* supported optimization flags

Do not turn these into new core EAIOC schema fields unless authoritative upstream documentation already defines them.

## Existing Component Interaction

Explain interaction with relevant existing components such as:

* Model Router
* Task/Complexity Analyzer
* Reasoning Budget Controller
* Model Cascade/Escalation
* Batch/Async Optimizer
* Capability/Availability Resolver
* Self-Protection Controller
* Net Optimization Economics

Reference existing definitions rather than duplicating them.

## Measurement Separation Requirement

P5 effects must be measured independently from Layer 1/Layer 2 optimization effects.

Do not combine P5 infrastructure savings with prompt/context optimization savings without explicit attribution.

Reference the authoritative measurement-separation requirements and validating edge/scenario cases.

## Fallback Behavior

Describe behavior when:

* capability unavailable
* capability unknown
* negotiation fails
* provider does not support capability
* capability becomes unavailable at runtime
* optimization overhead exceeds expected benefit

Preserve fail-open behavior where authorized and safe.

## Cross-Provider Variance

Never assume that a capability available from one provider/model is available from another.

Use `provider-matrix.md` as the source for provider-specific exposure facts.

Do not invent availability.

## Security / Authorization / Policy Constraints

Explain relevant constraints.

Optimization must never:

* bypass authorization
* bypass policy
* violate tenant isolation
* expose restricted data
* use unauthorized provider/model infrastructure
* bypass security controls
* weaken required execution integrity

Security and governance take precedence over optimization.

## Data Governance Constraints

Where applicable, address:

* sensitive data
* cache eligibility
* data classification
* retention
* deletion
* provenance

Do not duplicate `security.md` or `cache-strategy.md`.

Reference them conceptually where necessary.

## Non-Goals

Explicitly state the provider/infrastructure implementation mechanisms that are excluded from this entry.

This field is mandatory for every `INFOPT` entry.

## Validated By

Use only actual authoritative validation artifacts.

Do not fabricate capability-specific scenarios.

If an existing scenario validates the collective measurement-separation requirement, state that honestly.

## Traceability

Trace to relevant:

* Problem Statement sections
* Architecture sections
* Interface sections
* Convention sections
* Edge Cases
* Scenario Matrix
* Optimization Catalog P5 index
* Provider Matrix
* Cache Strategy
* Agent Optimization

Do not invent requirement IDs.

---

# 10. THE 11 CAPABILITIES — SPECIFIC BOUNDARIES

For each capability, address its Control-Plane integration without implementing its underlying engine.

### INFOPT-001 — Prefix/KV-cache reuse

Focus on:

* capability detection
* prefix/cache applicability
* provider/model negotiation
* cache-affinity hints
* expected benefit vs overhead
* tenant/security constraints
* measurement attribution

Reference `CACHE-005` / `PROVIDER_NATIVE` where applicable.

Do not redefine cache key, TTL, invalidation, or cache economics already owned by `cache-strategy.md`.

Do not design KV-cache internals.

### INFOPT-002 — KV-cache compression

Focus on:

* capability detection
* whether provider/infrastructure exposes compression
* compatibility implications
* negotiation
* quality/integrity considerations
* measurement

Do not define compression algorithms, ratios, bit widths, or GPU memory algorithms unless explicitly sourced.

### INFOPT-003 — Continuous batching

Focus on:

* capability detection
* request eligibility
* batching hints
* latency/throughput trade-off
* interaction with Batch/Async Optimizer
* measurement attribution

Do not design the batching scheduler.

### INFOPT-004 — Request scheduling

Focus on:

* scheduling capability awareness
* priority/eligibility signals
* policy constraints
* latency/cost considerations
* provider adapter negotiation
* measurement

Do not design provider queue/scheduler internals.

### INFOPT-005 — Prefill optimization

Focus on:

* capability awareness
* request characteristics relevant to prefill
* provider/infrastructure negotiation
* measurement

Do not design prefill kernels or engine algorithms.

### INFOPT-006 — Decode optimization

Focus on:

* capability awareness
* decode-related hints
* output characteristics
* latency/quality implications
* measurement

Do not design decode kernels or serving algorithms.

### INFOPT-007 — Speculative decoding

Focus on:

* capability detection
* model/provider compatibility
* eligibility
* negotiation
* quality/correctness measurement
* fallback

Do not implement draft-model selection or speculative decoding mechanics.

### INFOPT-008 — Quantization

Focus on:

* capability awareness
* model/provider compatibility
* quality implications
* policy/security constraints
* negotiated serving option
* measurement

Do not invent quantization formulas, bit-width heuristics, or accuracy guarantees.

### INFOPT-009 — Model replica selection

Focus on:

* replica capability/availability awareness
* latency/cost/availability signals
* authorization and tenant constraints
* routing integration
* provider adapter interaction
* failover/recovery

Do not implement infrastructure replica orchestration.

### INFOPT-010 — GPU utilization optimization

Focus on:

* awareness of infrastructure capability
* utilization-related signals exposed by provider
* routing/negotiation
* measurement

Do not invent GPU-utilization formulas, kernel algorithms, or infrastructure control logic.

### INFOPT-011 — Inference queue management

Focus on:

* queue capability awareness
* scheduling/priority hints
* latency/cost implications
* overload behavior
* fail-open/fallback behavior
* measurement

Do not design queue-management infrastructure.

---

# 11. NET OPTIMIZATION VALUE

Create a dedicated section explaining how existing EAIOC Net Optimization Value accounting applies to P5.

Do not invent a new economics framework.

Consider existing dimensions such as:

* expected inference benefit
* token/cost impact where applicable
* latency
* optimization overhead
* negotiation overhead
* retry cost
* escalation cost
* verification cost
* operational constraints

The key principle is:

> A P5 capability is valuable only when its verified or measurable benefit exceeds its relevant overhead and remains policy-compliant.

Do not claim that a P5 capability always saves money, latency, or tokens.

Distinguish:

* theoretical benefit
* estimated benefit
* measured benefit
* verified benefit

---

# 12. MEASUREMENT SEPARATION — CENTRAL REQUIREMENT

Create a dedicated section for measurement separation.

P5 metrics must remain distinguishable from Layer 1/Layer 2 optimization metrics.

Address:

* capability selected
* capability negotiated
* capability actually used
* capability unavailable
* negotiation failure
* expected benefit
* actual benefit
* optimization overhead
* added latency
* provider/infrastructure effect
* attribution confidence

Do not fabricate capability-specific validation scenarios.

If existing scenarios validate the separation requirement collectively, explicitly say so.

Do not artificially make coverage appear more granular than the authoritative scenario matrix supports.

---

# 13. PROVIDER-MATRIX INTEGRATION

Use `provider-matrix.md` as the authority for provider/model exposure facts.

Respect the distinction among:

* Discovered
* Accessible
* Available
* Authorized
* Recommended
* Eligible

Do not assume:

* discovered = accessible
* accessible = authorized
* authorized = available
* available = eligible

Runtime capability and availability may differ from static catalog information.

If provider information is unknown, say unknown.

Do not invent:

* pricing
* availability
* capability exposure
* latency
* throughput
* GPU behavior

Provider-specific facts should be cited/referenced rather than silently duplicated as if this document were the provider catalog.

---

# 14. CACHE-STRATEGY INTEGRATION

Use `cache-strategy.md` as the authority for cache mechanics.

This document may explain P5-facing interaction with caching but must not redefine:

* seven canonical cache types
* cache keys
* TTL rules
* invalidation rules
* semantic cache policy
* poisoning controls
* cache economics

For `INFOPT-001`, explicitly respect the distinction between:

* EAIOC-owned caching
* provider-native prompt/prefix caching
* underlying KV-cache infrastructure

Provider-native cache mechanics remain outside this document.

---

# 15. AGENT-OPTIMIZATION INTEGRATION

Use `agent-optimization.md` as the sibling authority for agent-loop optimization.

Do not duplicate:

* AL algorithms
* AR routing decisions
* agent iteration policies
* sub-agent orchestration
* agent memory ownership
* coding-agent optimization algorithms

Where necessary, explain how P5 serving capabilities support those decisions.

Preserve:

> Agent may own working memory, but the Control Plane owns execution truth.

For coding/developer agents and multi-agent systems, discuss only the P5 inference/serving implications.

---

# 16. STATE, RECOVERY AND SUPERSESSION

P5 decisions occur within dynamic execution state.

Handle changes such as:

* provider availability
* capability availability
* authorization
* policy
* tenant context
* workflow state
* context state
* model selection
* execution supersession

If a previously negotiated P5 capability becomes invalid:

* revalidate
* fall back
* renegotiate
* switch provider/model where authorized
* recover safely

Do not blindly replay non-idempotent operations.

Preserve:

> Resume != Replay.

Do not allow superseded execution to continue unwanted side effects.

---

# 17. NEGATIVE OPTIMIZATION

Explicitly document cases where EAIOC should choose:

`DO_NOT_OPTIMIZE`

Examples:

* capability unavailable
* capability unknown and risk is material
* policy restriction
* authorization mismatch
* tenant isolation risk
* quality regression
* excessive latency
* negotiation overhead exceeds expected benefit
* measurement cannot reliably attribute benefit
* capability causes unsafe behavior
* provider/model compatibility is uncertain
* stale capability state
* recovery complexity exceeds value

Do not force an optimization simply because a capability exists.

---

# 18. SECURITY AND GOVERNANCE PRECEDENCE

Preserve the established precedence:

**Security / Authorization / Policy / Governance**

>

**Correctness / Safety**

>

**Required Quality**

>

**Execution Integrity**

>

**Optimization**

>

**Cost / Token Reduction**

Do not use P5 optimization to bypass any higher-priority requirement.

Do not move restricted information into a provider/infrastructure path merely because that path appears cheaper or faster.

---

# 19. OBSERVABILITY

Include P5-specific observability requirements without duplicating `observability.md`.

Potential measurements include:

* selected provider/model
* P5 capability considered
* capability detected
* capability eligible
* capability negotiated
* capability accepted/rejected
* negotiation latency
* request latency
* expected benefit
* actual benefit
* optimization overhead
* provider/infrastructure attribution
* fallback
* capability failure
* retry
* provider switch
* optimization skipped
* `DO_NOT_OPTIMIZE`
* measurement confidence

Defer canonical observability schema to `observability.md` if that document defines it later.

---

# 20. FAILURE AND RECOVERY MATRIX

Include failure handling for:

* unknown capability
* unavailable capability
* negotiation failure
* provider degradation
* provider timeout
* request queue overload
* capability mismatch
* model/provider incompatibility
* stale capability metadata
* authorization change
* policy change
* tenant mismatch
* measurement failure
* infrastructure optimization failure

Distinguish:

* retry
* fallback
* renegotiation
* provider switch
* model switch
* context recomputation
* cache invalidation
* checkpoint restoration
* safe abort

Do not imply that every failure requires retry.

---

# 21. REQUIRED ALGORITHMIC ARTIFACTS

Include Control-Plane-level pseudocode or structured algorithms for:

1. P5 capability detection
2. P5 capability eligibility
3. P5 net-value decision
4. Provider capability negotiation
5. Measurement attribution
6. P5 fallback
7. P5 revalidation after state change
8. Negative optimization / `DO_NOT_OPTIMIZE`

Do NOT include pseudocode for:

* KV-cache eviction
* continuous-batching scheduler
* speculative-decoding engine
* quantization engine
* GPU kernel optimization
* inference queue implementation
* prefill/decode engine internals

The pseudocode must enforce:

* state reconciliation
* security/policy
* authorization
* tenant isolation
* capability validation
* measurement
* fallback
* recovery

---

# 22. REQUIRED MATRICES

Include implementation-oriented matrices for:

### P5 Capability Matrix

Capability → Detection → Eligibility → Negotiation → Measurement → Fallback

### Provider Exposure Reference

Capability → Provider/Model Reference → Source → Exposure State

Do not invent provider facts.

### Net Optimization Decision Matrix

Condition → Expected Benefit → Overhead → Policy → Decision

### Failure/Recovery Matrix

Failure → Detection → Safe Response → Recovery

### Cross-Document Ownership Matrix

Concern → Owning Document → How This Document Interacts

### Validation Coverage Matrix

INFOPT → Edge Case → Scenario → Validation Type → Coverage Status

Use honest coverage labels.

---

# 23. TEST / VALIDATION REQUIREMENTS

Build a validation matrix covering:

* positive cases
* negative cases
* boundary cases
* failure cases
* recovery cases

Map:

`Inference/P5 Requirement → INFOPT Entry → Scenario → Edge Case → Expected Behavior`

Do not fabricate scenario IDs.

If coverage is collective rather than capability-specific, state that explicitly.

Do not claim 100% coverage unless the authoritative artifacts actually establish it.

---

# 24. ANTI-FABRICATION DISCIPLINE

Any unsupported concrete number must be clearly labeled.

Examples:

* throughput improvement
* latency reduction
* GPU utilization target
* compression ratio
* token savings
* cost savings
* batching gain
* speculative decoding gain

Use:

`PROPOSED DEFAULT — VALIDATE LOCALLY`

or:

`PROPOSED METHODOLOGY`

or:

`ILLUSTRATIVE — NOT VERIFIED`

or:

`SOURCE-GAP`

Never present research benchmarks as EAIOC production guarantees.

If external research is referenced, clearly distinguish research evidence from EAIOC-validated production evidence.

---

# 25. SOURCE GAPS

Create document-local source gaps using:

`SOURCE-GAP-INFOPT-NN`

For every gap record:

* ID
* Missing information
* Affected capability/section
* Why it matters
* Expected authoritative source
* Current disposition
* Blocking/non-blocking status

Potential areas to check include:

* concrete provider negotiation-parameter schema
* capability-specific runtime telemetry
* provider exposure details
* unresolved upstream gaps

Do not resolve a source gap by inventing a value.

---

# 26. SOURCE CONTRADICTIONS

Create:

`CONTRA-INFOPT-NN`

for genuine contradictions.

Each contradiction must identify:

* Source A
* Source B
* conflicting statements
* authority resolution
* impact
* disposition

Do not manufacture contradictions.

Do not silently overwrite authoritative content.

---

# 27. CROSS-DOCUMENT OWNERSHIP BOUNDARY

Include a clear ownership table.

At minimum:

| Concern                              | Owning Document                             |
| ------------------------------------ | ------------------------------------------- |
| P5 capability index/names            | `optimization-catalog.md`                   |
| P5 Control-Plane elaboration         | `inference-optimization.md`                 |
| Provider/model capability exposure   | `provider-matrix.md`                        |
| Provider-native cache facts          | `provider-matrix.md`                        |
| Cache key/TTL/invalidation/economics | `cache-strategy.md`                         |
| Model routing                        | `architecture.md` / `agent-optimization.md` |
| Agent-loop optimization              | `agent-optimization.md`                     |
| Security                             | `security.md`                               |
| Observability                        | `observability.md`                          |
| Evaluation                           | `eval.md`                                   |
| Scaling                              | `SCALING.md`                                |
| Implementation                       | `implementation-plan.md`                    |
| Requirements traceability            | `requirements-traceability.md`              |

Do not recreate these downstream documents.

---

# 28. REQUIRED DOCUMENT STRUCTURE

Use this structure:

1. Header / Metadata
2. Purpose and Scope
3. How to Read This Document
4. H17/H18 Integration-vs-Implementation Boundary
5. P5 Control-Plane Decision Model
6. `INFOPT-001` Prefix/KV-cache reuse
7. `INFOPT-002` KV-cache compression
8. `INFOPT-003` Continuous batching
9. `INFOPT-004` Request scheduling
10. `INFOPT-005` Prefill optimization
11. `INFOPT-006` Decode optimization
12. `INFOPT-007` Speculative decoding
13. `INFOPT-008` Quantization
14. `INFOPT-009` Model replica selection
15. `INFOPT-010` GPU utilization optimization
16. `INFOPT-011` Inference queue management
17. Net Optimization Value for P5
18. Measurement Separation
19. Provider/Model Integration
20. Cache Integration
21. Agent Integration
22. State/Revalidation/Recovery
23. Negative Optimization
24. Security/Governance Constraints
25. Observability
26. Failure/Recovery Matrix
27. Cross-Document Ownership Boundary
28. Coverage Matrix
29. Source Gaps
30. Source Contradictions
31. Final Report
32. `## Inference Optimization Readiness`

The readiness section must be the final section.

---

# 29. FINAL SELF-VERIFICATION

Before declaring readiness, verify:

### Scope

* [ ] Only `docs/inference-optimization.md` was modified/created.
* [ ] No downstream document was created.
* [ ] `requirements-traceability.md` was not created.

### IDs

* [ ] Exactly 11 `INFOPT` entries.
* [ ] `INFOPT-001` through `INFOPT-011`.
* [ ] Correct authoritative order.
* [ ] No invented TECH/DA/AL/AR IDs.

### Architecture

* [ ] H17/H18 boundary preserved.
* [ ] Control Plane owns detection/routing/negotiation/measurement.
* [ ] Infrastructure implementation remains out of scope.

### Routing

* [ ] AR-001 and AR-004 are referenced, not redefined.
* [ ] Existing Model Router/Cascade logic is referenced, not duplicated.

### Provider

* [ ] Provider Matrix is authoritative for exposure.
* [ ] No invented provider capabilities.
* [ ] Accessibility states are respected.

### Cache

* [ ] Cache Strategy is authoritative.
* [ ] `CACHE-005` is referenced rather than redefined.
* [ ] Provider-native cache is distinguished from EAIOC-owned cache.

### State

* [ ] Dynamic state is respected.
* [ ] Capability state can be revalidated.
* [ ] Supersession is handled.
* [ ] Resume != Replay is preserved.

### Security

* [ ] Authorization/policy/security take precedence.
* [ ] Tenant isolation preserved.
* [ ] No optimization bypasses governance.

### Economics

* [ ] Net value considers benefit and overhead.
* [ ] No unsupported savings guarantee.
* [ ] Estimated vs measured vs verified benefits are distinguished.

### Measurement

* [ ] P5 measurement is separately attributable.
* [ ] No fabricated capability-specific validation scenarios.

### Validation

* [ ] Positive, negative, boundary, failure, recovery cases considered.
* [ ] Coverage is honest.
* [ ] Source gaps are recorded.
* [ ] Contradictions are recorded where real.

### Final line

Read the final ~20 lines of the actual generated file.

`READY FOR NEXT DOCUMENTATION PHASE`

must occur exactly once if and only if the document is genuinely ready, and must be the **true final line of the file**.

If a blocking issue exists, do not use the readiness phrase.

---

# 30. FINAL REPORT

Before the readiness section, report:

* document generated
* exact INFOPT count
* all 11 capabilities covered
* source documents actually read
* key architecture boundaries preserved
* provider/cache/agent integration
* validation coverage
* source gaps and IDs
* contradictions and IDs
* proposed/illustrative items
* deferred implementation areas
* limitations
* confirmation that no provider/infrastructure implementation ownership was claimed
* confirmation that no other document was modified

The H17/H18 ownership confirmation is mandatory.

---

# 31. CRITICAL ANTI-PATTERNS

Do NOT:

* invent optimization IDs
* invent provider capabilities
* invent provider pricing
* assume provider accessibility
* implement inference infrastructure
* design KV-cache algorithms
* design continuous-batching algorithms
* design speculative-decoding mechanics
* design quantization algorithms
* design GPU optimization formulas
* design provider queueing algorithms
* redefine model routing
* redefine agent cascade logic
* duplicate cache mechanics
* combine P5 savings with Layer 1/2 savings without attribution
* optimize solely for token reduction
* bypass authorization/policy/security
* violate tenant isolation
* silently discard mandatory information
* silently resolve source contradictions
* fabricate validation scenarios
* claim unsupported economic benefits
* create requirements-traceability.md
* modify upstream documents
* cascade into the next downstream document

---

# 32. FINAL QUALITY BAR

The completed `inference-optimization.md` must be:

* implementation-oriented at the **Control-Plane integration layer**
* provider-neutral
* state-aware
* security-aware
* tenant-safe
* economically disciplined
* measurement-attributable
* compatible with cache strategy
* compatible with agent optimization
* traceable to authoritative sources
* explicit about uncertainty
* honest about coverage
* strict about H17/H18 ownership
* suitable as the authoritative P5 inference/serving reference for EAIOC

The document must be **deep about Control-Plane decisions and shallow about infrastructure implementation**.

Do not optimize for brevity at the expense of correctness.

Do not silently fill source gaps.

Do not modify anything outside:

`docs/inference-optimization.md`

Stop after generating this document and its final readiness verdict.
