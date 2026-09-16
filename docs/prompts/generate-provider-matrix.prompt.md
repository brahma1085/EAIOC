# V2 MASTER PROMPT — GENERATE `provider-matrix.md`

## 1. ROLE

Act as a **Senior Provider-Integration, FinOps, AI Infrastructure, Security, Governance, and Enterprise Architecture engineer** responsible for the:

**Enterprise Agent & LLM Inference Optimization Control Plane (EAIOC)**

You are creating the canonical:

**Provider / Model Capability, Accessibility, Feasibility, Cost, Integration, and Capability-Exposure Matrix**

for EAIOC.

This is **not** a generic LLM provider comparison, vendor-marketing document, pricing blog, or list of model specifications.

The document must be implementation-oriented and must serve as the authoritative provider/model data foundation consumed by:

* `ModelProfile`
* `ModelProfileRegistry`
* `LLMProvider`
* `CapabilityAvailabilityResolver (CAR)`
* `FeasibilityTierRegistry (FTR)`
* model routing/cascade logic
* failover/recovery logic
* provider compatibility checks
* coding-agent feasibility analysis
* P5 inference-capability awareness/routing/negotiation
* cost and latency estimation
* provider/model observability and auditability

The document supplies **facts and configuration data**.

It must NOT redesign or duplicate the optimization, routing, security, cache, agent, inference, or quality mechanisms defined elsewhere.

---

# 2. TASK

Create exactly:

`docs/provider-matrix.md`

This document becomes the canonical reference for:

1. Provider profiles
2. Model profiles
3. Provider/model capabilities
4. Provider/model limits
5. Provider/model pricing snapshots
6. Provider accessibility states
7. Provider/model authorization applicability
8. Provider/model availability information
9. Model routing/cascade data
10. Provider failover characteristics
11. Coding-agent integration feasibility tiers
12. `reachable_modules` declarations where legitimately established
13. P5 capability exposure
14. Provider/model cache characteristics
15. Provider/model reasoning-control capabilities
16. Tool/function-calling capabilities
17. Structured-output capabilities
18. Batch capabilities
19. Context and output limits
20. Data residency/compliance characteristics
21. Provider integration modes
22. Provider/model compatibility and constraints
23. Evidence/provenance
24. Version/freshness information
25. Provider-switch/recovery implications

The document must be sufficiently precise that an engineer can determine exactly what information is required to populate a `ModelProfile`.

---

# 3. AUTHORITATIVE SOURCE DOCUMENTS

Before writing the document, read and understand the latest versions of ALL eight authoritative source documents:

1. `problemStatement.txt`
2. `Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md`
3. `architecture.md`
4. `interfaces.md`
5. `conventions.md`
6. `edge-cases.md`
7. `scenario-matrix.md`
8. `optimization-catalog.md`

Use the actual latest files available in the workspace.

Do NOT rely on memory of earlier versions.

Do NOT silently substitute older versions.

Do NOT assume that a statement in this prompt overrides an authoritative source.

If this prompt and an authoritative source conflict:

* identify the conflict,
* cite both,
* record it under `SOURCE CONTRADICTIONS`,
* follow the authoritative source hierarchy,
* do not modify the source document.

---

# 4. AUTHORITATIVE SOURCE PRIORITY

Use this order:

1. Latest Problem Statement
2. Latest Engineering Specification
3. Latest Architecture
4. Latest Interfaces
5. Latest Conventions
6. Latest Edge Cases
7. Latest Scenario Matrix
8. Latest Optimization Catalog
9. This prompt

`optimization-catalog.md` is now a completed upstream document, but remains subordinate to the seven earlier authoritative documents if an actual contradiction exists.

Never silently reconcile contradictions.

---

# 5. EXISTING FILE HANDLING

First determine whether:

`docs/provider-matrix.md`

already exists.

If it does not exist:

* create it from the authoritative sources.

If it already exists:

* read it completely,
* preserve valid existing IDs/content,
* preserve traceability,
* preserve source gaps,
* preserve contradiction records,
* make only the changes required to reconcile it with the latest authoritative documents.

Do not blindly rewrite a previously valid document.

---

# 6. OUTPUT SCOPE

Modify/create ONLY:

`docs/provider-matrix.md`

Do NOT modify:

* Problem Statement
* Engineering Spec
* Architecture
* Interfaces
* Conventions
* Edge Cases
* Scenario Matrix
* Optimization Catalog

Do NOT create downstream documents such as:

* `cache-strategy.md`
* `agent-optimization.md`
* `inference-optimization.md`
* `quality-gates.md`
* `security.md`
* `observability.md`
* `eval.md`
* `SCALING.md`
* `implementation-plan.md`
* ADRs

Do not create any additional deliverable under `docs/`.

Temporary scratch files, if needed, must remain outside `docs/` and must be deleted afterward.

---

# 7. CURRENT SCENARIO-MATRIX BASELINE

Verify these figures against the latest live `scenario-matrix.md`.

Expected current baseline:

* 263 unique scenarios
* 30 domains A–AD
* 93 compound scenarios
* 71 interfaces
* 213 edge cases
* 177 DIRECT
* 35 PARTIAL
* 0 NOT COVERED
* 1 DUPLICATE/SEMANTIC-DUPLICATE
* 22 dynamic/hardening components

The arithmetic must reconcile:

177 + 35 + 0 + 1 = 213

Do NOT blindly copy these numbers.

Verify the latest final report.

Do not accidentally use historical intermediate coverage figures.

In particular, distinguish the current final validation baseline from historical/pre-fix-pass narrative text.

---

# 8. OPTIMIZATION-CATALOG BASELINE

Verify the current `optimization-catalog.md`.

The current expected baseline is:

* 20 `TECH-NNN` entries
* `TECH-001` through `TECH-020`
* 25 `DA-NNN` entries
* `DA-001` through `DA-025`
* 11 P5 Index entries

Important upstream entries include:

* `TECH-013` Model Routing/Cascade
* `TECH-014` Model Cascade/Escalation
* P5 inference capability index

Do NOT create new `TECH-NNN` or `DA-NNN` entries.

Provider Matrix supplies the data consumed by those mechanisms.

It does not redefine their optimization logic.

---

# 9. CORE EAIOC OBJECTIVE

The provider matrix must support the EAIOC objective:

MINIMIZE:

**Total Safe Inference Cost**

SUBJECT TO:

* quality
* correctness
* security
* authorization
* policy compliance
* reliability
* latency
* freshness
* data governance
* residency
* tenant isolation
* task completion
* operational constraints

Never interpret provider selection as:

`lowest cost wins`

or

`highest capability wins`.

Provider/model facts are inputs.

Runtime selection belongs to the appropriate control-plane mechanisms.

---

# 10. PROVIDER NEUTRALITY DOCTRINE

EAIOC core architecture must remain provider-neutral.

Core control-plane logic must NOT contain load-bearing vendor-specific conditions such as:

`if provider == "X"`

or:

`if model == "Y"`

Provider-specific information belongs in:

* `ModelProfile`
* provider configuration
* `provider_hints`
* provider/model registry data
* capability profiles

This matrix is specifically the **data/configuration source** for that provider-neutral architecture.

Therefore:

Naming real providers/models in this document is permitted and expected when evidence supports them.

But this document must never imply that core EAIOC code should special-case a vendor.

State this distinction explicitly near the beginning of the generated document.

---

# 11. PROVIDER ACCESSIBILITY MODEL

Define and use these six states as independent dimensions:

1. **Discovered**
2. **Accessible**
3. **Available**
4. **Authorized**
5. **Recommended**
6. **Eligible**

Definitions:

### Discovered

EAIOC knows the provider/model exists.

### Accessible

A usable network/API/integration path exists.

### Available

The provider/model is currently operational and usable.

### Authorized

The current tenant, policy, compliance configuration, residency constraints, and `ModelPolicy` permit use.

### Recommended

The routing/optimization layer currently considers it a candidate based on task-specific decision logic.

### Eligible

The provider/model satisfies the task's technical capability requirements.

Never conflate these.

Explicitly state:

`Discovered != Accessible`

`Accessible != Available`

`Available != Authorized`

`Authorized != Eligible`

`Recommended != Eligible`

A globally available provider can still be unauthorized for a tenant.

A discovered model can be inaccessible.

An accessible model can be unavailable.

A technically capable model can be unauthorized.

A recommended model can still become ineligible after state changes.

---

# 12. STATIC VS RUNTIME PROVIDER FACTS

Clearly distinguish facts that can be statically cataloged from facts that must be evaluated dynamically.

Static/reference facts may include:

* model family
* declared capabilities
* published limits
* integration mode
* documented pricing
* documented cache behavior
* documented reasoning controls
* documented tool support
* documented structured-output support
* documented batch support
* residency characteristics
* provider API characteristics

Runtime facts may include:

* current availability
* health
* circuit state
* rate-limit state
* tenant authorization
* current policy eligibility
* temporary degradation
* dynamic quota
* current provider access
* current regional availability

Do not present runtime state as a permanent static guarantee.

---

# 13. MODELPROFILE CANONICAL SCHEMA

Every model entry must cover all applicable `ModelProfile` dimensions established by the latest Architecture and Interfaces.

At minimum verify:

* model/provider identity
* model family
* model/version
* input price
* output price
* cache-write price
* cache-read price
* cache TTL
* published context limit
* effective context budget
* output limit
* reasoning controls
* tool/function calling
* structured output
* batch
* streaming where applicable
* latency characteristics
* cache constraints
* routing constraints
* compliance/data residency
* P5 capability exposure

Cross-reference the exact existing interface field names.

Do NOT invent conflicting fields.

Where the existing interface contract already defines a field name, use that exact name.

---

# 14. LOGICAL CONTEXT VS MODEL-ADMITTED CONTEXT

The provider matrix must distinguish:

### Logical Task Context

All information potentially relevant to completing the task.

### Model-Admitted Context

The subset actually admitted to a particular model invocation.

A provider's published context window is NOT automatically the usable budget.

Effective context budgeting must account for applicable:

* system prompt
* developer instructions
* policy instructions
* safety controls
* tool definitions
* MCP definitions
* protocol overhead
* conversation history
* output reservation
* reasoning reservation
* framework overhead
* safety margin

Provider/model context data must therefore support effective-budget computation.

If CAR switches models/providers, the effective context budget may change.

Any admission plan based on the previous model must be revalidated.

Explicitly address EC-124.

---

# 15. PRICING AND NUMERIC DATA — NO FABRICATION

For every concrete provider/model numeric value:

* price
* context limit
* output limit
* cache price
* cache TTL
* latency
* quota
* rate limit
* throughput
* benchmark

apply strict evidence handling.

If live web access is available:

1. Prefer the provider's official documentation.
2. Verify the current value.
3. Record the source URL.
4. Record fetch date in `DD-MM-YYYY`.
5. Distinguish provider documentation from EAIOC production guarantee.

If live verification is unavailable or fails:

Use:

`ILLUSTRATIVE — NOT VERIFIED`

or:

`SOURCE-GAP`

Never silently use remembered training-data values.

Never invent provider pricing.

Never invent model limits.

Never invent SLAs.

Never invent availability.

Never invent throughput.

Never invent benchmarks.

Every real-world number must have explicit evidence status.

---

# 16. EVIDENCE CLASSIFICATION

Use the EAIOC evidence distinction:

1. Research Evidence
2. Provider Capability
3. Local Benchmark Result
4. Production Policy
5. Production Guarantee

A provider's own documentation establishes, at most:

**Provider Capability**

unless EAIOC local validation establishes stronger evidence.

Never convert a provider marketing claim into an EAIOC production guarantee.

---

# 17. PROVIDER / MODEL ENTRY STATUS

Each provider/model entry must explicitly distinguish:

* `VERIFIED`
* `ILLUSTRATIVE`
* `SOURCE-GAP`
* `UNKNOWN`
* where appropriate `PROPOSED`

Do not use bare `TBD`.

If a field genuinely does not apply:

`N/A — not applicable to this entry.`

---

# 18. PROVIDER CATEGORIES

Use provider categories supported by the architecture.

At minimum consider:

* External Managed Provider
* Cloud Model Platform
* Enterprise AI Gateway
* Internal AI Platform
* Private Model Service
* On-Premises Model Service
* Hybrid Provider
* Local/Developer Environment

Do not imply that EAIOC has direct access to every category.

---

# 19. INTEGRATION MODES

Represent applicable integration modes, such as:

* Direct API
* Enterprise Gateway
* Internal Platform
* Plugin/Extension
* Protocol/Tool-Level
* Observability-only
* Hybrid

Do not assume that EAIOC can intercept a provider simply because the provider supports an API.

The actual reachable integration surface determines feasibility.

---

# 20. PROVIDER ACCESS / AUTHORIZATION

Never assume:

* credentials exist
* API access exists
* network access exists
* tenant authorization exists
* provider agreement exists
* regional availability exists
* model access is enabled
* quota exists

The matrix records what is known.

CAR and policy mechanisms determine runtime selection.

A fallback model/provider must satisfy authorization requirements.

Explicitly preserve EC-110:

If a fallback is available but unauthorized, EAIOC must not silently use it.

---

# 21. MODEL AVAILABILITY / CAR DATA

Document the provider/model data consumed by CAR.

Where supported by interfaces, represent:

* availability
* circuit-open state
* degraded state
* degradation reason
* health
* provider availability
* model availability
* rate-limit conditions
* configured fallback/cascade relationships

Do not reimplement CAR.

Do not redefine its algorithms.

Document the data CAR consumes.

---

# 22. MODEL ROUTING AND CASCADE

`TECH-013` and `TECH-014` already define routing/cascade optimization behavior.

Do not duplicate their full schemas.

Provider Matrix must supply:

* candidate models
* model capabilities
* cost attributes
* capability parity
* compliance parity
* residency compatibility
* reasoning compatibility
* tool compatibility
* structured-output compatibility
* context compatibility
* provider availability
* configured cascade order where applicable
* failover compatibility

A cascade must never bypass:

* authorization
* policy
* security
* residency
* data governance
* tenant isolation

Do not implement "always cheapest" routing.

Do not implement "always strongest" routing.

---

# 23. NEGATIVE OPTIMIZATION

Provider switching/failover is not free.

Account for possible overhead from:

* health checks
* availability checks
* authorization revalidation
* policy re-evaluation
* capability matching
* context reconstruction
* cache loss
* checkpoint translation
* provider-specific serialization
* retries
* latency
* model warm-up
* quality changes
* repeated inference

Do not calculate or redefine the full Net Optimization Value formula here.

Instead provide the provider facts needed by the optimization layer to evaluate whether switching is worthwhile.

---

# 24. PROVIDER SWITCH + CONTEXT INVALIDATION

A provider/model switch may invalidate:

* effective context budget
* prompt assembly
* reasoning budget
* tool compatibility
* structured-output assumptions
* cache assumptions
* token accounting
* model-specific formatting
* model-specific capabilities

Therefore:

`provider/model switch → revalidation`

Where required:

`invalidate → recompute → re-admit`

Never continue using stale admission decisions from the previous model.

---

# 25. PROVIDER SWITCH + CHECKPOINT / RECOVERY

A checkpoint must remain interpretable after provider/model changes where the architecture permits recovery.

Resume is NOT replay.

On recovery, reconcile:

* current request
* current user intent
* context version
* workflow version
* checkpoint
* permissions
* policies
* provider/model availability
* external state
* completed work
* remaining work

Possible outcomes include:

* resume
* recompute
* retrieve
* expand context
* reduce context
* switch provider
* switch model
* invalidate cache
* restore checkpoint
* rollback
* restart
* pause
* ask user
* abort safely

Never blindly replay non-idempotent provider/tool actions.

---

# 26. FTR — FEASIBILITY TIER REGISTRY

Use the exact five-tier terminology defined by the authoritative Architecture/Interfaces/Conventions documents.

Do NOT rename or reorder the tiers.

Expected model:

| Tier | Name                        |
| ---- | --------------------------- |
| 1    | Deep/Native                 |
| 2    | Gateway/Interception        |
| 3    | Plugin/Extension            |
| 4    | Protocol/Tool-Level         |
| 5    | Advisory/Observability-Only |

Verify the exact authoritative wording before finalizing.

For each named developer/coding-agent platform that the authoritative sources actually discuss, document:

* declared tier
* evidence
* access surface
* reachable modules
* limitations
* degradation behavior
* redeclaration behavior
* coverage implications

Critical rule:

Do NOT invent an authoritative tier assignment.

If the sources do not assign a tier:

`PROPOSED — NOT YET AUTHORITATIVELY DECLARED`

or:

`SOURCE-GAP`

Use live platform documentation only as supporting evidence.

---

# 27. CODING / DEVELOPER AGENT COVERAGE

Where authoritative sources name them, assess integration surfaces for:

* Claude Code
* Cursor
* GitHub Copilot
* Antigravity
* Codex
* other explicitly source-supported developer-agent platforms

Do not invent additional platforms merely for completeness.

For each platform, distinguish:

* direct model access
* gateway interception
* extension/plugin surface
* MCP/tool surface
* telemetry-only visibility
* file/repository access
* terminal access
* tool-result visibility
* sub-agent visibility
* context visibility
* transformation ability

Do not claim full-pipeline optimization when only a Tier 4/5 surface is reachable.

---

# 28. REACHABLE_MODULES

Where FTR is applicable, declare:

`reachable_modules`

using only valid `DA-001`–`DA-025` IDs.

Do not claim a DA module is reachable merely because it is theoretically useful.

Reachability must follow the actual integration surface.

If not established:

`SOURCE-GAP`

or:

`PROPOSED — NOT YET AUTHORITATIVELY DECLARED`

Do not silently infer reachability.

---

# 29. FTR DEGRADATION

Explicitly cover the runtime condition:

**Platform's actual access degrades below its declared tier**

from EC-188.

Also cover:

* tier downgrade
* loss of interception
* loss of tool visibility
* loss of transformation ability
* loss of context visibility

The system must not continue reporting full optimization coverage after access has degraded.

---

# 30. TIER 4/5 COVERAGE HONESTY

Explicitly enforce EC-189.

Tier 4/5 integrations must never report:

* full-pipeline coverage
* full-context savings
* complete optimization control

unless the actual integration surface supports that claim.

Observability is not transformation.

Advice is not enforcement.

Tool-level interception is not necessarily model-level interception.

---

# 31. OUT-OF-TIER MODULE INVOCATION

Explicitly address EC-190.

If an optimization module is outside the platform's declared `reachable_modules`:

* reject it,
* downgrade/defer appropriately,
* or use an explicitly supported fallback.

Do not silently execute an unreachable module.

---

# 32. P5 CAPABILITY EXPOSURE

Architecture defines 11 P5 capabilities.

Verify the exact current list from the authoritative Architecture.

The expected capabilities include:

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

For each provider/model where evidence exists, document whether EAIOC can detect, negotiate, configure, or otherwise observe the capability through the exposed interface.

Use:

* Yes
* No
* Conditional
* Unknown
* Not exposed
* SOURCE-GAP

Do NOT explain the internal implementation of the inference mechanism.

That belongs in:

`inference-optimization.md`

---

# 33. EXTERNAL API VS SELF-HOSTED INFERENCE

Clearly distinguish:

### External Managed API

EAIOC may only have access to exposed API/gateway capabilities.

Do not claim control over:

* internal KV cache
* GPU scheduling
* batching
* replica selection
* prefill
* decode
* internal queue
* quantization

unless the provider explicitly exposes an interface for it.

### Self-hosted / Internal / On-prem

EAIOC may potentially integrate with infrastructure-level optimization mechanisms.

Still do not claim support unless established.

This distinction is essential for P5.

---

# 34. CACHE DISTINCTION

Provider Matrix must distinguish:

### Provider Prompt/Prefix Cache

A provider-exposed inference capability.

from:

### EAIOC Response/Semantic Cache

A control-plane cache mechanism.

Do not merge the two.

Provider Matrix may document:

* provider cache support
* cache read/write pricing
* TTL
* documented constraints
* provider-side eligibility

Detailed cache key design, invalidation mechanics, poisoning defenses, retention policy and cache architecture belong to:

`cache-strategy.md`

---

# 35. TOKEN ACCOUNTING

Provider-specific token accounting must be documented where known.

Distinguish:

* input tokens
* output tokens
* cached tokens
* cache read/write tokens if applicable
* reasoning tokens where separately exposed
* tool/MCP overhead
* provider-specific usage reporting

Do not assume token definitions are identical across providers.

Cross-provider cost comparisons must account for differences in accounting semantics.

---

# 36. LATENCY

Document latency characteristics only when evidence exists.

Distinguish:

* provider-reported latency
* local benchmark
* observed runtime telemetry
* qualitative characteristic

Never fabricate latency numbers.

Do not convert anecdotal performance into an EAIOC guarantee.

---

# 37. QUALITY COMPATIBILITY

Provider/model capability must include relevant quality compatibility information where evidence exists.

Potential dimensions include:

* reasoning capability
* structured output reliability
* tool-use compatibility
* coding capability
* context handling
* multimodal capability
* task-specific quality

Do not assign unsupported quality scores.

Do not rank providers/models overall.

Do not claim one provider is "best".

---

# 38. SECURITY AND GOVERNANCE

Provider selection must remain subordinate to:

* authentication
* authorization
* policy
* security
* data classification
* residency
* tenant isolation
* compliance
* approval requirements

A cheaper provider must never bypass a security or governance requirement.

A provider being available does not make it authorized.

A model supporting a capability does not make it policy-compatible.

---

# 39. DATA GOVERNANCE

Where applicable, document:

* data residency
* region
* retention characteristics
* training/use-of-data characteristics if authoritative
* encryption requirements
* tenant isolation
* sensitive-data restrictions
* provider data handling
* compliance applicability

Do not invent legal conclusions.

Where deployment-specific regulatory requirements are not defined:

record a `SOURCE-GAP`.

Do not silently determine the regulatory regime.

---

# 40. TENANT ISOLATION

Provider/model data must never imply cross-tenant sharing.

Tenant-sensitive information includes:

* authorization
* quota
* pricing contract
* cache eligibility
* residency
* provider access
* policy
* model eligibility

If a provider/model is available globally but not authorized for a tenant, the tenant must remain excluded.

---

# 41. TOOL / MCP CAPABILITY

Where relevant, document provider/model support for:

* tool/function calling
* structured tool results
* MCP-related integration
* tool schema limits
* tool-call constraints
* tool-result handling

Do not redefine the Tool/MCP Trust Gate.

Do not treat tool capability as equivalent to tool authorization.

Trust and authorization remain separate controls.

---

# 42. PROVIDER / TOOL TRUST

Provider capability does not imply:

* tool trust
* MCP trust
* authorization
* content integrity
* safety

Malicious or compromised provider/tool output must not bypass EAIOC security controls.

---

# 43. CONTENT INTEGRITY / PROMPT INJECTION

Do not implement the CIS mechanism here.

However, provider/model integration must not bypass content-integrity controls.

Where provider switching changes:

* context handling
* tool behavior
* system prompt semantics
* structured output
* safety controls

the relevant security controls must be revalidated.

The concrete CIS mechanism remains in:

`security.md`

---

# 44. HUMAN APPROVAL

If a provider/model supports actions with consequential or irreversible effects, provider capability must not be interpreted as authorization to execute.

Human Approval Gate requirements remain authoritative.

This matrix records provider/model capability.

It does not grant approval.

---

# 45. SELF-PROTECTION CONTROLLER

Explicitly account for EC-151 / `SCN-STATE-005`.

If Self-Protection Controller logic fails:

* use deterministic safe fallback,
* do not bypass security,
* do not bypass authorization,
* do not bypass governance,
* do not bypass approval,
* do not bypass tenant isolation,
* do not bypass data governance.

Provider failover must never become a security bypass during SPC failure.

---

# 46. EDGE-CASE REVIEW

Review all 213 edge cases against the latest live `edge-cases.md`.

Classify each as:

1. `Provider-Specific`
2. `Provider-Governance Relevant`
3. `Provider-Failure/Recovery Relevant`
4. `Another EAIOC Subsystem`
5. `Not Applicable to Provider Matrix`

Do NOT force every edge case into a provider/model entry.

At minimum verify the latest definitions of:

* EC-037
* EC-038
* EC-043
* EC-063
* EC-064
* EC-076
* EC-109
* EC-110
* EC-116
* EC-124
* EC-188
* EC-189
* EC-190
* EC-200
* EC-151

Do not trust this prompt's remembered wording over the live edge-case source.

---

# 47. SCENARIO REVIEW

Review all 263 scenarios.

Do not restrict analysis to Model Selection / Provider domains.

Primary relevance includes:

* model selection
* provider access
* enterprise gateway
* CAR
* FTR

But also inspect scenarios involving:

* checkpoint
* recovery
* context budgeting
* authorization
* policy
* cache
* security
* tenant isolation
* data governance
* developer agents
* concurrency
* provider switching
* stale state
* supersession
* non-idempotent effects

Every scenario must be reviewed for provider-matrix relevance.

Not every scenario requires a provider-matrix entry.

---

# 48. COMPOUND SCENARIO ANALYSIS

Review all 93 compound scenarios.

At minimum consider combinations involving:

* CAR × authorization
* CAR × availability
* CAR × context budgeting
* CAR × optimization
* CAR × checkpoint/recovery
* FTR × developer-agent optimization
* FTR × reachable modules
* provider switch × stale state
* provider switch × cache
* provider switch × checkpoint
* provider switch × authorization
* provider switch × data residency
* provider switch × concurrency
* provider switch × non-idempotent actions
* spend governance × provider concurrency
* security × provider failure
* tenant isolation × cache/provider selection
* verifier escalation × provider capability
* self-protection × governance

Use compounds to derive:

* compatibility
* ordering
* invalidation
* revalidation
* failover constraints
* recovery constraints

Do NOT create artificial optimization techniques from compound scenarios.

---

# 49. 22 EAIOC COMPONENTS

Ensure provider-matrix implications are considered for all relevant dynamic/hardening components.

The current architecture contains 22 dynamic/hardening components.

At minimum account for provider interactions with:

* ESM
* CVM
* WVM
* CPM
* Reconciliation Engine
* CIG
* Context Expansion Controller
* PRV
* DPE
* CAR
* SRP
* SPM
* RCO
* SGE
* DGE
* TMG
* HAG
* CIS
* FTR
* XEC
* SPC
* VCL

Do not duplicate their full implementation.

Document only provider-matrix-relevant dependencies.

---

# 50. CANONICAL PROVIDER ENTRY

Use a consistent structure such as:

## PROV-NNN — Provider Name

**Provider ID:** opaque identifier

**Evidence Status:** VERIFIED / ILLUSTRATIVE / SOURCE-GAP

**Integration Mode(s):** ...

**Accessibility Characteristics:** ...

**Authorization Characteristics:** ...

**Residency / Governance:** ...

### Models

#### MODEL-NNN — Model Name

**Model ID:** ...

**Model Family:** ...

**Version:** ...

**ModelProfile:**

* Input Price
* Output Price
* Cache Write Price
* Cache Read Price
* Cache TTL
* Published Context Limit
* Effective Context Budget
* Output Limit
* Reasoning Controls
* Tool/Function Calling
* Structured Output
* Batch
* Streaming
* Latency Characteristics
* Cache Constraints
* Routing Constraints
* Compliance/Residency
* P5 Exposure

For every field indicate evidence status.

Then include:

* Accessibility applicability
* Known routing constraints
* Known cache constraints
* Data-governance constraints
* P5 exposure
* CAR relevance
* Cascade position if applicable
* FTR relevance
* Coding-agent applicability if applicable
* Anti-pattern check
* Evaluation/reverification methodology
* Traceability

---

# 51. PROVIDER COVERAGE SCOPE

Do NOT attempt to enumerate every LLM provider in existence.

First determine whether authoritative EAIOC documents define a provider roster.

If they do:

* include that roster.

If they do not:

* do not invent an authoritative roster.

Instead:

1. Define the complete provider/model schema.
2. Include a small number of clearly labeled illustrative provider patterns where useful.
3. Label all unsupported real-world facts appropriately.
4. Record the absence of an authoritative roster as a `SOURCE-GAP`.

Illustrative entries must never be mistaken for an EAIOC-supported provider list.

---

# 52. REAL PROVIDER DATA

If real providers/models are included:

* verify that they actually exist,
* verify current names/versions,
* prefer official documentation,
* cite sources,
* include verification/fetch dates,
* label evidence,
* never imply EAIOC has access unless access is actually established.

Do not fabricate provider/model support.

---

# 53. CASCADE DATA

Where configured cascades are defined by authoritative sources, document:

* primary
* fallback sequence
* capability parity
* compliance parity
* residency parity
* authorization dependency
* availability dependency

If the source documents do not define actual cascade members:

record the absence rather than inventing a production cascade.

---

# 54. PROVIDER COMPATIBILITY

Document compatibility across providers/models where relevant:

* context requirements
* tool support
* structured output
* reasoning controls
* token accounting
* cache behavior
* output formats
* checkpoint portability
* prompt formatting
* model-specific semantics

Do not claim complete interchangeability merely because two models expose similar APIs.

---

# 55. CACHE PORTABILITY

Provider switching may invalidate provider-specific cache assumptions.

Document:

* cache portability
* provider-specific cache semantics
* cache invalidation implications
* cache pricing differences
* cache eligibility constraints

Do not define the complete cache architecture here.

---

# 56. CHECKPOINT PORTABILITY

Where provider/model switches occur during recovery:

document whether state/checkpoint interpretation is affected.

If not established:

`SOURCE-GAP`

Do not claim portability merely because the checkpoint format is provider-neutral.

---

# 57. VERSIONING / DEPRECATION

Provider/model information changes.

Where supported, document:

* model version
* lifecycle status
* deprecation information
* successor relationship
* compatibility impact

Never assume a model remains available indefinitely.

A model deprecation must be capable of invalidating stale routing/profile assumptions.

---

# 58. FRESHNESS

Every mutable external-provider fact should have an evidence/freshness concept.

At minimum consider:

* source
* source date
* verification date
* version
* confidence/evidence class
* refresh requirement

Do not represent dynamic provider facts as permanent architecture guarantees.

---

# 59. OBSERVABILITY

Provider/model integration must support observability of relevant facts where possible:

* provider
* model
* model version
* request outcome
* latency
* usage
* tokens
* cache behavior
* failover
* retry
* circuit state
* routing decision input
* availability
* capability mismatch

Do not invent telemetry fields that conflict with `interfaces.md`.

---

# 60. AUDITABILITY / PROVENANCE

Every provider/model claim must be traceable to:

* source document
* provider documentation where applicable
* verification date
* relevant section/ID
* scenario
* edge case
* interface
* architecture requirement

Avoid:

`see the docs`

as a traceability mechanism.

Use precise references.

---

# 61. ANTI-PATTERN CHECK

Every provider/model entry must check for applicable anti-patterns.

At minimum:

1. Always using strongest/most expensive model
2. Always using cheapest model
3. Treating provider-specific behavior as universal
4. Adding provider fields to prohibited core schemas
5. Treating availability as authorization
6. Treating recommendation as eligibility
7. Treating provider capability as production guarantee
8. Assuming provider interchangeability
9. Assuming provider access exists
10. Assuming provider-side optimization is controllable

---

# 62. IMPLEMENTATION PRIORITY VS MATURITY

Keep these separate.

### Implementation Priority

How important provider-matrix support is to EAIOC implementation.

### Maturity

How established the underlying provider capability/integration is.

Do not collapse them into one rating.

Do not use unsupported overall rankings.

---

# 63. SCOPE BOUNDARY

Include a `## Scope Boundary` section.

Clearly state:

| Content                             | Lives In                    |
| ----------------------------------- | --------------------------- |
| Optimization techniques             | `optimization-catalog.md`   |
| DA modules                          | `optimization-catalog.md`   |
| P5 index definitions                | `optimization-catalog.md`   |
| Provider/model capability facts     | `provider-matrix.md`        |
| Provider/model pricing snapshots    | `provider-matrix.md`        |
| Cache architecture                  | `cache-strategy.md`         |
| Agent-loop optimization             | `agent-optimization.md`     |
| Inference implementation mechanisms | `inference-optimization.md` |
| Security/CIS mechanism              | `security.md`               |
| Quality-gate methodology            | `quality-gates.md`          |

Do not duplicate downstream documents.

---

# 64. SOURCE GAPS

Include:

`## SOURCE GAPS DISCOVERED`

Existing source gaps must retain their existing IDs.

Do not renumber existing gaps.

New provider-matrix-specific gaps may use:

`SOURCE-GAP-PROVMTX-NN`

At minimum evaluate:

* `SOURCE-GAP-ARCH-02` FTR mechanism half
* absence of an authoritative provider roster
* provider-specific capability gaps
* provider-specific pricing gaps
* platform integration gaps
* runtime accessibility gaps
* deployment-specific regulatory requirements

Do not artificially close a source gap.

---

# 65. SOURCE CONTRADICTIONS

Include:

`## SOURCE CONTRADICTIONS`

If no new contradictions exist:

explicitly state that.

If one exists:

* identify both sources,
* identify conflicting statements,
* identify affected matrix fields,
* identify current treatment,
* do not silently choose one.

Historical/resolved contradictions must not be presented as current unresolved contradictions.

---

# 66. TRACEABILITY

Every meaningful provider/model entry must trace to exact:

* Problem Statement IDs
* Engineering Specification sections
* Architecture sections
* Interface IDs
* Convention sections
* Edge Case IDs
* Scenario IDs
* Optimization Catalog IDs

Never invent IDs.

Verify every referenced ID against the live files.

---

# 67. VALIDATED BY

Use concrete scenario IDs.

Examples may include:

* `SCN-MODEL-*`
* `SCN-PROV-*`
* `SCN-CODE-*`
* relevant compound scenarios
* recovery scenarios

But verify every actual ID before using it.

Do not write only:

`Validated by Domain K`

Use actual scenario identifiers.

---

# 68. CROSS-DOCUMENT COVERAGE MATRIX

Include:

`## Cross-Document Coverage Matrix`

Cover all eight authoritative documents.

Show:

* relevance
* coverage
* key sections
* thin areas
* source gaps
* provider-specific information absent from the source

Do not inflate coverage simply by citing a document.

---

# 69. EDGE-CASE COVERAGE MATRIX

Include a coverage summary for all 213 edge cases.

The matrix may classify them by relevance rather than forcing a provider entry.

The final document must make clear that:

**reviewing an edge case does not mean the edge case requires a provider-specific record.**

---

# 70. SCENARIO COVERAGE MATRIX

Include provider-relevant scenario coverage.

Review all 263 scenarios, but do not manufacture provider-specific content for unrelated scenarios.

---

# 71. P5 COVERAGE MATRIX

Include all 11 P5 capabilities.

For each capability:

* definition
* provider/model exposure
* detection/negotiation availability
* evidence
* verification date
* limitation
* downstream mechanism

Do not duplicate inference optimization mechanics.

---

# 72. FTR COVERAGE MATRIX

Include:

* five authoritative tiers
* platform
* declared/proposed status
* evidence
* access surface
* reachable modules
* limitations
* degradation behavior
* coverage implications

Do not present proposed assignments as authoritative.

---

# 73. PROVIDER DATA QUALITY

Before finalizing, independently validate:

1. Every model has the complete required ModelProfile schema.
2. Every numeric provider claim has evidence status.
3. No unverified current number is presented as fact.
4. Every named provider/model is real or explicitly labeled illustrative.
5. No provider access is assumed.
6. Availability and authorization are not conflated.
7. Eligibility and recommendation are not conflated.
8. Context limit and effective budget are distinguished.
9. Provider switching triggers appropriate revalidation.
10. FTR tiers exactly match authoritative definitions.
11. No unsupported coding-agent tier is presented as authoritative.
12. No new TECH/DA entries were created.
13. P5 capabilities are all covered.
14. Cache and inference mechanics were not duplicated.
15. Security/governance controls remain authoritative.
16. EC references resolve.
17. Scenario references resolve.
18. Interface references resolve.
19. Requirement references resolve.
20. No source document was modified.

---

# 74. FAILURE MODES

Defend explicitly against:

1. Fabricated pricing
2. Stale pricing
3. Fabricated model limits
4. Fabricated capabilities
5. Fabricated provider access
6. Fabricated FTR assignments
7. Treating provider documentation as EAIOC guarantee
8. Treating availability as authorization
9. Treating recommendation as eligibility
10. Assuming direct provider access
11. Assuming provider interchangeability
12. Treating P5 awareness as infrastructure control
13. Reporting Tier 4/5 as full-pipeline coverage
14. Executing out-of-tier DA modules
15. Losing checkpoint portability during provider switch
16. Replaying non-idempotent actions
17. Bypassing authorization during failover
18. Bypassing security during SPC failure
19. Tenant leakage
20. Scope creep
21. Inventing provider roster
22. Inventing source IDs
23. Silently resolving contradictions
24. Declaring readiness without validation

---

# 75. NO-HALLUCINATION RULE

Never invent:

* provider
* model
* model version
* capability
* pricing
* context limit
* output limit
* cache behavior
* latency
* SLA
* availability
* quota
* integration surface
* FTR tier
* reachable module
* scenario
* edge case
* interface
* requirement
* source gap

If information is missing:

`SOURCE-GAP`

is preferable to an invented answer.

---

# 76. NO SOURCE REWRITES

If a contradiction or missing definition is discovered:

Do NOT modify the source documents.

Record the issue in:

* SOURCE GAPS
* SOURCE CONTRADICTIONS

and document the current treatment.

---

# 77. DOCUMENT QUALITY

The finished document must be:

* enterprise-grade
* technically rigorous
* implementation-oriented
* provider-neutral in architecture
* concrete where evidence exists
* explicit about uncertainty
* traceable
* testable
* auditable
* extensible
* suitable as a canonical provider/model registry reference

Avoid:

* marketing language
* vendor hype
* unsupported rankings
* "best provider" claims
* generic comparison-blog language
* fabricated completeness

---

# 78. EXECUTION ORDER

Execute in this order:

### Step 1

Confirm whether `docs/provider-matrix.md` exists.

### Step 2

Read all eight authoritative documents.

### Step 3

Verify the live scenario baseline.

### Step 4

Verify the live edge-case baseline.

### Step 5

Verify the current optimization-catalog baseline.

### Step 6

Extract the authoritative `ModelProfile` schema.

### Step 7

Extract CAR requirements.

### Step 8

Extract FTR requirements.

### Step 9

Extract P5 capabilities.

### Step 10

Review all 263 scenarios for provider relevance.

### Step 11

Review all 213 edge cases.

### Step 12

Review all 93 compound scenarios.

### Step 13

Review provider/model requirements across all relevant interfaces/components.

### Step 14

Determine whether the source documents define an authoritative provider roster.

### Step 15

If real-world provider data is required, perform live verification where tools permit.

### Step 16

Create the canonical provider/model schema.

### Step 17

Populate supported provider/model entries.

### Step 18

Create FTR matrix.

### Step 19

Create P5 exposure matrix.

### Step 20

Create provider accessibility matrix.

### Step 21

Create cross-document coverage.

### Step 22

Create source gaps.

### Step 23

Create source contradictions.

### Step 24

Create traceability.

### Step 25

Create final report.

### Step 26

Run independent validation.

### Step 27

Only then determine readiness.

---

# 79. FINAL REPORT

Include:

`## Final Report`

At minimum report:

* provider count
* model count
* provider categories
* integration modes
* ModelProfile completeness
* accessibility model coverage
* CAR data coverage
* FTR coverage
* P5 coverage
* coding-agent coverage
* scenario coverage
* edge-case coverage
* compound-scenario coverage
* interface coverage
* component coverage
* requirement coverage
* source-gap count
* contradiction count
* evidence quality
* unverified/illustrative data count
* limitations
* whether `SOURCE-GAP-ARCH-02` FTR half was resolved or carried forward

Do not fabricate validation results.

---

# 80. PROVIDER MATRIX READINESS

End with:

`## Provider Matrix Readiness`

Use exactly one of:

`READY FOR NEXT DOCUMENTATION PHASE`

or:

`NOT READY — MATRIX GAPS MUST BE RESOLVED`

Use `NOT READY` only where a gap prevents correct interpretation, safe behavior, traceability, or implementation of the provider-matrix schema.

A thin provider roster by itself is NOT a blocker when the authoritative sources do not define a provider roster.

---

# 81. FINAL VALIDATION CHECKLIST

Before completion, verify:

### Source integrity

* [ ] All eight authoritative documents read
* [ ] Latest versions used
* [ ] No source modified
* [ ] No contradiction silently resolved

### Scenario integrity

* [ ] 263 scenarios reviewed
* [ ] 93 compound scenarios reviewed
* [ ] 213 edge cases reviewed
* [ ] Current 177/35/0/1 baseline verified

### Interface integrity

* [ ] 71 interfaces verified
* [ ] All references resolve

### Optimization integrity

* [ ] 20 TECH entries preserved
* [ ] 25 DA entries preserved
* [ ] 11 P5 entries preserved
* [ ] No new TECH/DA entries created

### Provider integrity

* [ ] No provider invented
* [ ] No model invented
* [ ] No access assumed
* [ ] No unauthorized fallback allowed
* [ ] Availability/authorization separated
* [ ] Eligibility/recommendation separated

### ModelProfile integrity

* [ ] All required fields present
* [ ] Context limit/effective budget separated
* [ ] Pricing evidence present
* [ ] Cache data separated
* [ ] Reasoning controls represented
* [ ] Tool support represented
* [ ] Structured output represented
* [ ] Batch represented
* [ ] Residency/compliance represented
* [ ] P5 exposure represented

### FTR integrity

* [ ] Five tiers match authoritative definitions
* [ ] Platform assignments are evidence-labeled
* [ ] Proposed assignments are not presented as authoritative
* [ ] reachable_modules validated
* [ ] Tier degradation handled
* [ ] Tier 4/5 coverage claims bounded

### Security/governance integrity

* [ ] Authorization preserved
* [ ] Security preserved
* [ ] Policy preserved
* [ ] Tenant isolation preserved
* [ ] Data governance preserved
* [ ] Residency preserved
* [ ] HAG preserved
* [ ] CIS boundary preserved
* [ ] SPC failure safe fallback preserved

### Recovery integrity

* [ ] Provider switch revalidation addressed
* [ ] Context invalidation addressed
* [ ] Checkpoint portability addressed
* [ ] Resume != replay preserved
* [ ] Non-idempotent actions protected
* [ ] Stale state addressed

### P5 integrity

* [ ] All 11 capabilities covered
* [ ] Exposure separated from mechanism
* [ ] External API vs self-hosted boundary preserved

### Quality integrity

* [ ] No fabricated benchmarks
* [ ] No unsupported quality ranking
* [ ] Provider capability != production guarantee
* [ ] Evidence classes used

### Scope integrity

* [ ] Only provider-matrix.md modified
* [ ] No downstream documents created
* [ ] Cache mechanics not duplicated
* [ ] Agent optimization not duplicated
* [ ] Inference mechanics not duplicated
* [ ] Security mechanism not duplicated
* [ ] Quality-gate mechanism not duplicated

### Final integrity

* [ ] Source gaps documented
* [ ] Contradictions documented
* [ ] Traceability complete
* [ ] Final report complete
* [ ] Independent validation completed
* [ ] Exactly one readiness verdict present

---

# 82. FINAL DONE CONDITION

The task is complete only when:

1. `provider-matrix.md` exists.
2. The canonical ModelProfile schema is complete.
3. Provider accessibility states are explicitly separated.
4. CAR data requirements are documented.
5. FTR is documented using the exact authoritative five-tier model.
6. Named platform tier assignments are evidence-labeled.
7. P5 capability exposure is documented for all 11 capabilities.
8. Provider/model data is clearly distinguished from runtime decisions.
9. Pricing/limits/capabilities are never fabricated.
10. Real-world provider data is evidence-labeled.
11. All 263 scenarios have been reviewed for relevance.
12. All 213 edge cases have been reviewed/classified.
13. All 93 compound scenarios have been reviewed.
14. All relevant interfaces have been traced.
15. All relevant components have been considered.
16. Provider/model switching and recovery are addressed.
17. Security and governance remain authoritative.
18. Tenant isolation remains authoritative.
19. EC-151/SPC safe fallback remains protected.
20. No unsupported provider/model/FTR claims exist.
21. No new TECH/DA optimization entries were invented.
22. No source document was modified.
23. No downstream document was created.
24. Source gaps are documented.
25. Contradictions are documented.
26. Independent validation passes.
27. Final readiness verdict is present.

---

# 83. FINAL INSTRUCTION

Create or update ONLY:

`docs/provider-matrix.md`

Treat the eight authoritative EAIOC documents as the source of truth.

Do not invent missing information.

Do not silently resolve contradictions.

Do not fabricate provider/model facts.

Do not assume provider access.

Do not confuse availability with authorization.

Do not confuse recommendation with eligibility.

Do not claim a coding-agent feasibility tier without evidence.

Do not claim full-pipeline optimization for a restricted integration surface.

Do not bypass security, policy, authorization, governance, tenant isolation, residency, approval, or data controls for provider failover or optimization.

Do not redesign mechanisms already defined in the Architecture, Interfaces, Optimization Catalog, or downstream documents.

The Provider Matrix is the **canonical provider/model data foundation** consumed by EAIOC's neutral control-plane mechanisms.

Begin by verifying the existence/status of `docs/provider-matrix.md`, then read all eight authoritative documents before drafting.

# END V2 MASTER PROMPT
