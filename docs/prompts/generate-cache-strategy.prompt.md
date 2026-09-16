# MASTER PROMPT V3 — GENERATE `cache-strategy.md`

## Enterprise Agent & LLM Inference Optimization Control Plane

You are working on the:

**Enterprise Agent & LLM Inference Optimization Control Plane (EAIOC)**

Create the canonical downstream document:

```text
docs/cache-strategy.md
```

This document must become the authoritative implementation-oriented reference for EAIOC caching policy, cache mechanics, key construction, TTL governance, cache economics, invalidation, freshness, poisoning defense, tenant isolation, provider-native cache boundaries, and cache behavior across generic LLM applications, autonomous agents, developer/coding agents, and multi-agent/sub-agent systems.

---

# 1. PRIMARY OBJECTIVE

Create an enterprise-grade, implementation-oriented **Cache Strategy Reference**.

This is NOT a generic caching best-practices document.

It must define the concrete policy that an engineer implementing the EAIOC `CacheStore` can follow for:

```text
lookup()
write()
invalidate()
refresh()
warm()
inspect()
explain_miss()
```

The document must establish concrete policy for the seven cache types already defined by the authoritative `interfaces.md`.

The strategy must cover:

1. Cache-key construction
2. Namespacing
3. Tenant isolation
4. Cache eligibility
5. TTL policy
6. Semantic similarity governance
7. Cache economics
8. Cache admission
9. Cache lookup
10. Cache validation
11. Cache freshness
12. Cache invalidation
13. Dependency-aware invalidation
14. Cache fragmentation
15. Cache warming
16. Cache breakpoint optimization
17. Cache poisoning defense
18. PII/sensitive-data gating
19. Authorization-aware caching
20. Policy-aware caching
21. Provider-native cache boundary
22. EAIOC-owned cache boundary
23. Cache portability
24. Developer-agent caching
25. Agent/sub-agent caching
26. Tool/MCP caching
27. Recovery/checkpoint interaction
28. Negative optimization
29. Cache failure handling
30. Observability
31. Provenance
32. Auditability
33. Testing
34. Full source/scenario/interface traceability.

---

# 2. CRITICAL PRINCIPLE

EAIOC caching is NOT merely:

```text
request → cache lookup → cached response
```

EAIOC is a **stateful execution control plane**.

Cache reuse must therefore reconcile the cache candidate against relevant current execution state.

Potential state dependencies include:

* tenant;
* user/principal;
* authorization;
* policy;
* task intent;
* request;
* logical task context;
* model-admitted context;
* context version;
* workflow version;
* execution state;
* checkpoint;
* repository state;
* files;
* external state;
* tools;
* MCP;
* provider;
* model;
* model version;
* optimization configuration;
* data classification;
* freshness;
* provenance;
* quality requirements;
* latency requirements;
* budget;
* security state.

A cache hit is a **candidate for reuse**, not proof that reuse is valid.

---

# 3. AUTHORITATIVE SOURCE DOCUMENTS

Read and understand these documents completely before writing:

1. `problemStatement.txt`
2. `Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md`
3. `architecture.md`
4. `interfaces.md`
5. `conventions.md`
6. `edge-cases.md`
7. `scenario-matrix.md`
8. `optimization-catalog.md`
9. `provider-matrix.md`

Use this authority order:

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
```

Use the latest live versions of these files.

Do not rely on this prompt's restated numbers if the live files differ.

If a contradiction exists:

1. identify it;
2. preserve the higher-authority source;
3. record the contradiction;
4. do not silently rewrite the source;
5. do not invent a reconciliation.

Do not modify any authoritative source document.

---

# 4. EXISTING `cache-strategy.md`

First determine whether:

```text
docs/cache-strategy.md
```

already exists.

If it does not exist, create it.

If it already exists:

* read it completely;
* preserve valid existing content;
* preserve existing IDs;
* preserve source gaps;
* preserve contradictions;
* preserve valid policy decisions;
* make the smallest changes necessary to reconcile it with the latest authoritative sources.

Never overwrite valid downstream work merely to restructure it.

---

# 5. CURRENT BASELINE — VERIFY FROM LIVE FILES

Before writing the document, verify the current baseline from the live authoritative files.

The latest known baseline is:

* 263 scenarios
* 30 domains
* 93 compound scenarios
* 71 interfaces
* 213 edge cases
* 177 DIRECT
* 35 PARTIAL
* 0 NOT COVERED
* 1 DUPLICATE / SEMANTIC-DUPLICATE
* 20 TECH entries
* 25 DA entries
* 11 P5 Index entries
* 22 dynamic/hardening components

But **verify these values from the live documents rather than copying this prompt blindly**.

The latest scenario matrix must remain authoritative for scenario counts.

The latest optimization catalog must remain authoritative for optimization counts.

The latest provider matrix must remain authoritative for provider/model facts.

Do not create new TECH or DA entries in this document.

---

# 6. SEVEN CANONICAL CACHE TYPES

The authoritative `interfaces.md` defines seven cache types.

Use exactly these seven:

1. `EXACT`
2. `SEMANTIC`
3. `TOOL_RESULT`
4. `EMBEDDING`
5. `PROVIDER_NATIVE`
6. `APPLICATION`
7. `CONTEXT`

Do not:

* invent an eighth cache type;
* rename these;
* merge them;
* split them into new canonical types.

Use these seven as the primary cache-policy structure.

Before relying on exact subsection numbers, verify the live `interfaces.md`.

---

# 7. CACHESTORE CONTRACT

Read the actual `CacheStore` interface in `interfaces.md`.

Document the policy supplied by this document for:

```text
lookup
write
invalidate
refresh
warm
inspect
explain_miss
```

Also inspect and accurately reference:

* `CacheLookupRequest`
* `CacheLookupResult`
* `CacheMissReason`
* `CacheEntry`
* `CacheWriteRequest`
* `CacheInvalidationRequest`

Do NOT invent field names.

Do NOT redefine the interface.

This document supplies the concrete policy that the interface consumes.

Where the interface already defines:

```text
write_policy
IF_ABSENT
ALWAYS
IF_FRESHER
```

explain when each should be used.

---

# 8. CACHE OBJECT MODEL

Define the logical cache object model using the actual interface schema.

At minimum distinguish:

```text
CacheEntry
CacheKey
CacheValue
CacheMetadata
CachePolicy
CacheEligibility
CacheValidation
CacheProvenance
CacheDependency
CacheVersion
CacheScope
CacheFreshness
CacheSecurityMetadata
CacheAuthorizationMetadata
CacheInvalidationMetadata
CacheCostMetadata
CacheQualityMetadata
```

Do not invent interface fields merely to represent these concepts.

If a concept exists logically but is not represented by the current interface, identify the appropriate source gap or downstream implementation concern.

---

# 9. CACHE KEY CONSTRUCTION

This is mandatory.

Every cache key must be tenant-scoped.

The tenant boundary must be structural, not merely an optional policy check.

The canonical key must begin with:

```text
tenant_id
```

or the exact tenant identity field defined by the authoritative interface/conventions.

Cross-tenant cache serving is prohibited.

For every cache type define its key construction policy.

A conceptual structure may be:

```text
{tenant_id}:{cache_type}:{namespace}:{artifact_identity}
```

but the actual template must be grounded in the real `CacheEntry`, `CacheLookupRequest`, and related interface fields.

Do not invent unsupported fields.

---

# 10. CACHE KEY DIMENSIONS

Evaluate cache identity dimensions appropriate to each cache type:

* tenant;
* principal/user where required;
* authorization state;
* policy version;
* task intent;
* normalized request;
* context version;
* workflow version;
* execution state;
* repository;
* branch;
* commit;
* working tree;
* file version;
* tool;
* MCP server;
* tool version;
* provider;
* model;
* model version;
* tokenizer;
* output schema;
* optimization configuration;
* retrieval configuration;
* security classification;
* environment;
* region;
* deployment;
* relevant external-state version.

Do not force every dimension into every cache key.

Explicitly explain:

> Over-keying reduces reuse; under-keying creates unsafe reuse.

---

# 11. TENANT ISOLATION

Every cache type must enforce tenant isolation.

Explicitly defend against:

* cross-tenant key collision;
* cross-tenant semantic similarity;
* shared vector cache leakage;
* shared metadata leakage;
* cache enumeration;
* tenant migration;
* tenant deletion;
* shared provider cache ambiguity.

A cache implementation must fail closed if tenant isolation cannot be established.

Reference the real scenario/edge-case IDs only after verifying them in the authoritative files.

---

# 12. CACHE ELIGIBILITY

Before cache admission, evaluate:

```text
Tenant
↓
Authorization
↓
Policy
↓
Data Governance
↓
Security / Integrity
↓
Freshness model
↓
Cache Type
↓
Expected Reuse
↓
Economics
↓
Write Decision
```

Do not write a cache entry simply because the result is reusable.

---

# 13. AUTHORIZATION-AWARE CACHE REUSE

A cache candidate must be checked against current authorization.

Handle:

* permission revocation;
* role change;
* repository access change;
* file access change;
* tool access change;
* MCP authorization change;
* model/provider authorization change;
* delegated authority expiration.

A previously authorized cache entry may become invalid.

Cache reuse must not bypass current authorization.

---

# 14. POLICY-AWARE CACHE REUSE

Cache validity must account for current policy.

Handle:

* policy version changes;
* caching prohibited by policy;
* model restrictions;
* provider restrictions;
* data-retention policy;
* residency restrictions;
* security policy;
* output policy;
* tool policy.

Optimization savings never override policy.

---

# 15. DATA GOVERNANCE / PII

Before cache write, apply the authoritative data-classification/cache-eligibility mechanism.

Where the source defines `PIIClassifier` / equivalent:

* use it;
* do not redefine it;
* document the policy input it supplies.

If content is not safe to cache:

```text
DO_NOT_CACHE
```

regardless of economics.

Cover:

* PII;
* credentials;
* secrets;
* tokens;
* confidential source code;
* regulated information;
* proprietary data;
* sensitive tool output.

Do not unnecessarily duplicate sensitive content into cache metadata.

---

# 16. DELETION / ERASURE

Cache strategy must participate in data deletion.

Consider derived artifacts:

* semantic entries;
* embeddings;
* summaries;
* compressed context;
* tool-result cache;
* repository maps;
* checkpoints;
* optimization artifacts;
* memory-derived artifacts;
* logs;
* audit metadata.

Deleting a source must not be assumed to automatically delete all derived cache representations.

Define dependency-aware deletion propagation.

---

# 17. CACHE LIFECYCLE

Define:

```text
Candidate
→ Eligibility
→ Key Construction
→ Lookup
→ Candidate Validation
→ Freshness Check
→ Authorization Check
→ Policy Check
→ Integrity Check
→ Dependency Check
→ Compatibility Check
→ Economics Evaluation
→ Reuse / Partial Reuse / Revalidate / Bypass
→ Execution
→ Validation
→ Cache Write / Update / Invalidate
→ Observability
→ Eviction / Deletion
```

---

# 18. CACHE STATES

Define/cache the semantics of:

* `MISS`
* `HIT`
* `PARTIAL_HIT`
* `STALE`
* `INVALID`
* `POISONED`
* `UNAUTHORIZED`
* `POLICY_MISMATCH`
* `TENANT_MISMATCH`
* `VERSION_MISMATCH`
* `DEPENDENCY_MISMATCH`
* `CORRUPTED`
* `EXPIRED`
* `INELIGIBLE`
* `BYPASSED`
* `DISABLED`
* `VALIDATION_FAILED`
* `COST_NEGATIVE`

Use exact authoritative terminology where available.

---

# 19. TTL POLICY

The authoritative source may establish that particular cache classes require TTL but may not define concrete durations.

If concrete TTL values are not authoritative:

you MAY propose engineering defaults, but every value MUST be labeled:

```text
PROPOSED DEFAULT — VALIDATE LOCALLY BEFORE PRODUCTION USE
```

Never present proposed TTLs as authoritative requirements.

Do not invent provider-native TTLs.

Provider-native TTL remains provider-controlled.

Create a TTL matrix for all seven cache types:

| Cache Type | TTL Requirement | Proposed EAIOC Default | Evidence | Freshness Mechanism |
| ---------- | --------------- | ---------------------- | -------- | ------------------- |

If a cache type is version-invalidated rather than TTL-driven, explicitly state that.

---

# 20. SEMANTIC CACHE SIMILARITY

Use the authoritative similarity threshold/floor from `conventions.md` if it exists.

The latest known requirement is a global floor of:

```text
0.92
```

but verify the live source before asserting it.

If the floor is authoritative, preserve it.

Per-tenant configurability must never permit a value below the authoritative floor.

Explain:

```text
similarity threshold
≠
semantic equivalence
≠
authorization
≠
freshness
≠
policy validity
```

A similarity score alone can never justify serving a cache hit.

---

# 21. SEMANTIC CACHE FOUR-CHECK GATE

If the authoritative conventions specify the four-check gate, preserve it exactly.

All required checks must pass before serving a semantic cache hit:

1. Authorization
2. Freshness
3. Tenant isolation
4. Policy

Then additionally validate any other applicable:

* context;
* workflow;
* dependency;
* provider/model;
* integrity;
* data governance;
* task intent;
* quality constraints.

---

# 22. CACHE ECONOMICS

Preserve the authoritative economics model.

If the authoritative architecture defines:

```text
Expected Cache Benefit
=
Future Reuse Probability
× Tokens Avoided
× Effective Input Price
```

and:

```text
Cache Cost
=
Write Cost
+
Storage Cost
+
Invalidation Cost
+
Freshness Risk
+
Cache Miss/Break Risk
```

preserve it.

If the source says:

```text
Expected Cache Benefit > Cache Cost
```

is the admission criterion, preserve that.

Do NOT fabricate universal probability or cost values.

Instead define how to estimate:

* future reuse probability;
* tokens avoided;
* effective input price;
* write cost;
* storage cost;
* invalidation cost;
* freshness risk;
* cache miss/break risk;
* lookup cost;
* validation cost;
* latency overhead;
* quality risk.

Mark new estimation methods:

```text
PROPOSED METHODOLOGY
```

unless source-authoritative.

---

# 23. NEGATIVE OPTIMIZATION

Explicitly support:

```text
DO_NOT_CACHE
DO_NOT_REUSE
BYPASS_CACHE
REVALIDATE
```

when cache overhead exceeds expected benefit.

Examples:

* expensive semantic lookup;
* low reuse probability;
* stale result risk;
* high validation cost;
* cache storage overhead;
* cache-induced latency;
* cache-induced quality degradation;
* invalidation overhead;
* provider-switch incompatibility;
* excessive reconciliation.

A cache hit is NOT automatically an optimization success.

---

# 24. SEVEN CACHE POLICY ENTRIES

Create exactly seven canonical policy entries.

Use:

```text
CACHE-001 — EXACT
CACHE-002 — SEMANTIC
CACHE-003 — TOOL_RESULT
CACHE-004 — EMBEDDING
CACHE-005 — PROVIDER_NATIVE
CACHE-006 — APPLICATION
CACHE-007 — CONTEXT
```

If existing authoritative IDs already exist in an existing `cache-strategy.md`, preserve them instead.

Each entry must contain:

```text
Cache Type
Owning Architecture Section(s)
Interface Contract
Key Construction Template
Namespace
TTL Policy
Auth Check
Freshness Check
Similarity Threshold
Write Policy
Invalidation Triggers
Dependency Rules
Poisoning / Integrity Considerations
PII / Sensitive Data Gating
Economics Estimation
Fragmentation Risks
Provider / Model Dependencies
Composition / Ordering
Revalidation
Observability
Anti-Pattern Check
Validated By
Traceability
```

Use:

```text
N/A — not applicable to this entry.
```

when a field genuinely does not apply.

Never leave a required field blank.

---

# 25. EXACT CACHE

Define:

* deterministic identity;
* exact-key construction;
* tenant namespace;
* freshness;
* authorization;
* write policy;
* invalidation;
* version dependencies;
* economics.

Do not make exact cache reusable merely because the request string matches if relevant authorization/context/policy state differs.

---

# 26. SEMANTIC CACHE

Define:

* canonicalization;
* embedding/similarity mechanism;
* similarity floor;
* tenant boundary;
* intent compatibility;
* authorization;
* freshness;
* policy;
* provenance;
* quality validation;
* semantic false-positive protection.

Similarity is necessary but not sufficient.

---

# 27. TOOL_RESULT CACHE

Cover:

* read-only tools;
* state-changing tools;
* side effects;
* freshness;
* authorization;
* tool version;
* MCP server;
* dependency state;
* idempotency;
* replay safety.

Critical rule:

> Cached result reuse is not equivalent to replaying a side effect.

Never blindly replay:

* payment;
* email;
* deployment;
* database mutation;
* ticket creation;
* infrastructure change;
* file mutation.

---

# 28. EMBEDDING CACHE

Define:

* embedding model identity;
* model version;
* tokenizer/model compatibility;
* source content hash;
* tenant scope;
* data classification;
* deletion propagation;
* invalidation;
* model migration;
* portability.

Do not assume embeddings from one model are interchangeable with another.

---

# 29. PROVIDER_NATIVE CACHE

Clearly distinguish provider-native caching from EAIOC-owned caching.

Provider-native cache may include:

* prompt-prefix caching;
* provider-managed cached input;
* provider-specific cache pricing;
* provider-specific cache lifetime;
* provider-specific cache behavior.

Do not duplicate provider pricing/capability facts already owned by `provider-matrix.md`.

Cross-reference the provider matrix.

Do not assume:

* every provider supports prompt caching;
* every model supports it;
* provider caches are portable;
* EAIOC controls provider cache invalidation;
* provider cache survives provider switching.

---

# 30. APPLICATION CACHE

Define application-side reusable artifacts that are not necessarily direct model responses.

Examples:

* normalized application data;
* derived results;
* reusable task artifacts;
* domain computations.

Apply:

* tenant;
* authorization;
* freshness;
* policy;
* provenance;
* dependency validation.

---

# 31. CONTEXT CACHE

Cover:

* context fragments;
* summaries;
* stable context;
* repository maps;
* project rules;
* retrieval artifacts;
* compressed context;
* reusable context sections.

Respect:

* logical task context;
* model-admitted context;
* context criticality;
* mandatory information.

Never use cache reuse to silently remove Tier 0 Mandatory information.

---

# 32. CACHE-AWARE CONTEXT CONSTRUCTION

Preserve the authoritative three-region structure where defined:

```text
STABLE / CACHEABLE PREFIX
SEMI-STABLE REGION
VOLATILE REGION
```

Typical stable content:

* system instructions;
* stable developer policies;
* stable tool schemas;
* project rules;
* stable examples;
* stable repository metadata.

Semi-stable:

* documentation;
* memory;
* slowly changing context.

Volatile:

* current request;
* current files;
* current errors;
* current tool results;
* current execution state.

The document must explain how to minimize unnecessary stable-prefix changes.

Provider-specific breakpoint capability belongs to `provider-matrix.md`.

---

# 33. CACHE FRAGMENTATION

Define detection and remediation.

Cover:

* timestamps;
* random identifiers;
* unstable tool ordering;
* serialization differences;
* whitespace differences;
* formatting differences;
* dynamic metadata;
* changing system instructions;
* changing tool definitions;
* unstable context ordering.

Provide a concrete remediation playbook for each.

Examples:

```text
deterministic serialization
stable tool ordering
canonical normalization
stable metadata extraction
versioned configuration
```

Do not optimize away semantically meaningful differences.

---

# 34. CACHE PRE-WARMING

Define pre-warming policy.

Pre-warming is allowed only when:

1. the cache is eligible;
2. provider/model capabilities permit it;
3. expected reuse is sufficient;
4. cost/latency savings justify warming cost;
5. security/policy/data-governance requirements are satisfied.

Pre-warming must itself be evaluated for net value.

Do not indiscriminately warm every cache.

---

# 35. CACHE BREAKPOINT OPTIMIZATION

Define how EAIOC selects stable prefix/breakpoint boundaries when provider semantics support them.

Consider:

* context stability;
* expected reuse frequency;
* provider cache semantics;
* model/provider capability;
* prefix volatility;
* cache invalidation frequency.

Do not assume multiple breakpoints are supported by every provider.

---

# 36. DEPENDENCY-AWARE INVALIDATION

Preserve the authoritative invalidation rules from architecture/interfaces.

If the authoritative architecture contains the following trigger/action mapping, preserve its semantics:

| Trigger               | Action                                        |
| --------------------- | --------------------------------------------- |
| File changed          | Invalidate symbol summaries                   |
| Symbol changed        | Invalidate dependent context                  |
| Branch changed        | Invalidate branch-specific cache              |
| Permission changed    | Invalidate affected cache entries             |
| External data changed | Invalidate freshness-sensitive semantic cache |

Verify the live architecture before claiming this is exact.

Extend this into a full dependency-aware invalidation model using the actual `CacheInvalidationRequest` fields.

Cover:

* explicit invalidation;
* staleness;
* permission change;
* dependency change;
* branch change;
* policy change;
* source update;
* context version;
* workflow version;
* provider/model change;
* tool/MCP change;
* repository change;
* deletion.

---

# 37. STALE RESULT PROTECTION

Do not redefine the authoritative SRP algorithm.

Reference it.

This document defines the cache-layer data SRP needs, such as:

* freshness;
* TTL;
* dependency version;
* policy version;
* authorization version;
* context version;
* workflow version.

Never silently serve a stale result when the authoritative policy requires protection.

Possible actions:

```text
REVALIDATE
REFETCH
RECOMPUTE
BYPASS
INVALIDATE
```

Where appropriate, emit the authoritative stale-result observability event.

---

# 38. CACHE POISONING

Explicitly cover cache poisoning.

Threats include:

* malicious user input;
* prompt injection;
* malicious retrieved content;
* malicious tool output;
* compromised MCP server;
* adversarial model output;
* poisoned embeddings;
* manipulated metadata;
* manipulated cache keys;
* cross-tenant contamination.

The cache write path must apply the authoritative integrity/security gates before admission.

A favorable economics calculation must NEVER override:

* security;
* authorization;
* integrity;
* policy;
* data governance.

---

# 39. CONTENT-INTEGRITY SCREEN

Do not redefine the future `security.md` mechanism.

This document only defines the cache ordering dependency:

```text
Content Integrity Screening
→ Cache Admission
```

If integrity screening fails:

```text
DO_NOT_CACHE
```

Do not let cache economics override the integrity result.

---

# 40. CACHE KEY POISONING

A cache key itself may be manipulated.

Do not derive cache identity directly from unscreened model output or untrusted content.

Canonicalize and validate key material.

Separate:

```text
trusted identity inputs
```

from:

```text
untrusted content
```

---

# 41. AGENT MEMORY VS CACHE

Preserve:

> Agent may own working memory, but the Control Plane owns execution truth.

Agent memory may contain:

* scratch state;
* temporary plans;
* transient observations;
* intermediate reasoning.

EAIOC cache contains:

* controlled reusable artifacts;
* validated results;
* provenance;
* authorization metadata;
* policy metadata;
* freshness/version state.

Do not automatically treat agent memory as authoritative cache state.

---

# 42. DEVELOPER / CODING AGENT CACHE

Provide a dedicated section.

Cover caching of:

* repository maps;
* symbol indexes;
* AST-derived information;
* file summaries;
* search results;
* Git diffs;
* dependency graphs;
* compiler errors;
* test failures;
* linter output;
* terminal output;
* code analysis;
* sub-agent results;
* patch impact analysis;
* embeddings;
* file-section retrieval;
* MCP/tool capability information.

Cache identity must consider:

* repository;
* branch;
* commit;
* working tree;
* uncommitted changes;
* file version;
* dependency lockfile;
* build configuration;
* test configuration.

A repository-map cache from commit A must not automatically be valid for incompatible working-tree state B.

---

# 43. MULTI-AGENT / SUB-AGENT CACHE

Cover:

* sub-agent result reuse;
* parent task compatibility;
* authorization;
* tenant;
* context version;
* workflow version;
* tool dependencies;
* provider/model;
* freshness;
* provenance.

A sub-agent result is reusable only when its assumptions remain valid.

---

# 44. TOOL / MCP CACHE

Distinguish:

### Capability metadata

Potentially longer-lived, subject to version/availability/authorization.

### Read-only tool results

Potentially cacheable subject to freshness and authorization.

### Side-effecting operations

Do not treat cached results as permission to replay side effects.

Integrate:

* TMG;
* PRV;
* DPE;
* HAG;
* SRP;
* recovery.

---

# 45. CACHE PORTABILITY

Classify artifacts as:

```text
PORTABLE
CONDITIONALLY PORTABLE
NON-PORTABLE
```

Across:

* model;
* model version;
* provider;
* region;
* deployment;
* environment.

Examples:

* raw source documents may be portable;
* provider-native prompt cache state is generally provider-specific;
* model-specific embeddings may be conditionally portable;
* tokenizer-dependent artifacts may not be portable.

Validate compatibility before reuse.

---

# 46. RECOVERY / CHECKPOINT INTERACTION

Integrate with:

* Execution State Manager;
* Checkpoint Manager;
* Recovery Coordinator;
* Supersession Manager;
* Stale Result Protection.

Preserve:

> Resume != replay.

A cache artifact may assist recovery, but must be reconciled against current state.

Cover:

* stale checkpoint;
* stale cache;
* changed permissions;
* changed workflow;
* changed repository;
* changed external state;
* superseded execution;
* non-idempotent side effects.

---

# 47. CACHE CONSISTENCY

Define consistency models appropriate to each cache class.

Possible models:

* strongly validated;
* version-consistent;
* event-invalidated;
* TTL-based;
* eventually consistent;
* best-effort.

Do not impose one consistency model on every cache.

---

# 48. CACHE STAMPEDE

Cover:

* request coalescing;
* single-flight;
* leases;
* bounded refresh;
* backpressure;
* stale-while-revalidate where safe;
* hot-key management.

Tenant isolation must remain intact while preventing duplicate expensive execution.

---

# 49. CACHE FAILURE MODES

Cover:

* cache unavailable;
* cache timeout;
* corrupted entry;
* invalid key;
* storage exhaustion;
* index failure;
* semantic-search failure;
* invalidation failure;
* validation failure;
* authorization service unavailable;
* policy service unavailable;
* cache poisoning;
* provider-native cache unavailable.

Principle:

> Non-critical cache failure does not automatically become task failure.

However:

> Security-critical cache validation failure may require fail-closed behavior.

---

# 50. CACHE OBSERVABILITY

Define metrics including:

* lookups;
* hits;
* misses;
* partial hits;
* stale hits;
* invalidations;
* revalidations;
* bypasses;
* poisoning detections;
* authorization failures;
* policy failures;
* tenant mismatches;
* version mismatches;
* lookup latency;
* validation latency;
* storage cost;
* tokens avoided;
* model calls avoided;
* cost avoided;
* cache overhead;
* quality impact;
* retries;
* cache-induced failures;
* net optimization value.

Distinguish:

```text
cache hit
```

from:

```text
successful optimization
```

---

# 51. CACHE MISS DIAGNOSIS

Use the authoritative `CacheMissReason` if defined.

The document must define how `explain_miss()` diagnoses:

* key mismatch;
* tenant mismatch;
* authorization failure;
* policy mismatch;
* freshness failure;
* dependency mismatch;
* version mismatch;
* provider/model mismatch;
* integrity failure;
* cache disabled;
* cache ineligible;
* cost-negative decision;
* unavailable storage;
* expired entry.

The explanation must not expose protected information unnecessarily.

---

# 52. CACHE PROVENANCE

For materially reused cache artifacts preserve sufficient provenance to determine:

* origin;
* creation time;
* tenant;
* authorization state;
* policy;
* context version;
* workflow version;
* provider/model;
* tool/source;
* repository/version;
* transformations;
* dependencies;
* last validation;
* reuse decision.

Do not log sensitive payloads unnecessarily.

---

# 53. SECURITY / GOVERNANCE PRECEDENCE

Use the authoritative precedence defined by the upstream documents.

At minimum, caching must never override:

```text
Security
Authorization
Policy
Data Governance
Integrity / Correctness
Task Success
Reliability
Quality
Latency
Cost / Token Optimization
```

If the authoritative documents specify a different formal ordering, use that exact ordering.

---

# 54. INTERFACE TRACEABILITY

Map the cache strategy to real interface IDs.

Do not invent IDs.

Trace where relevant to:

* CacheStore;
* cache lookup;
* cache result;
* cache write;
* cache invalidation;
* authorization;
* policy;
* context version;
* workflow version;
* provenance;
* provider/model resolution;
* checkpoint;
* recovery;
* tool/MCP trust;
* data governance;
* observability.

If a required interface is missing, record a source gap instead of inventing one.

---

# 55. EDGE-CASE TRACEABILITY

Review all **213 current edge cases**.

Do not force all 213 into cache behavior.

Classify relevance honestly:

```text
DIRECT
PARTIAL
INDIRECT
NOT CACHE-SPECIFIC
SOURCE-GAP
```

Pay particular attention to:

* authorization;
* tenant isolation;
* stale state;
* cache poisoning;
* provider switching;
* tool/MCP;
* recovery;
* checkpoint;
* deletion;
* context changes;
* workflow changes;
* coding agents.

Verify every cited EC ID exists.

---

# 56. SCENARIO TRACEABILITY

Use the current **263-scenario baseline**.

Review relevant scenarios for:

* cache lookup;
* cache hit;
* cache miss;
* semantic cache;
* cache economics;
* tenant isolation;
* permission changes;
* stale results;
* tool result caching;
* provider switching;
* recovery;
* developer-agent caching;
* compound cache interactions.

Do not invent scenario IDs.

Do not force all 263 scenarios to become cache-specific.

Use compound scenarios to validate interactions, ordering, and invalidation.

---

# 57. OPTIMIZATION CATALOG TRACEABILITY

Reference existing:

```text
TECH-001 through TECH-020
DA-001 through DA-025
```

where applicable.

Especially investigate cache-related entries such as:

* prompt caching;
* semantic caching;
* tool-result caching;
* developer-agent cache layer;
* context invalidation.

Do not create new optimization entries.

This document defines concrete cache policy; the optimization catalog defines the optimization techniques.

---

# 58. SCOPE BOUNDARY

Include:

## Scope Boundary

| Concern                    | Authoritative Document      |
| -------------------------- | --------------------------- |
| Problem definition         | `problemStatement.txt`      |
| Architecture               | `architecture.md`           |
| Interfaces                 | `interfaces.md`             |
| Conventions                | `conventions.md`            |
| Edge cases                 | `edge-cases.md`             |
| Scenario validation        | `scenario-matrix.md`        |
| Optimization techniques    | `optimization-catalog.md`   |
| Provider/model facts       | `provider-matrix.md`        |
| Cache mechanics/policy     | `cache-strategy.md`         |
| Agent-loop optimization    | `agent-optimization.md`     |
| Inference infrastructure   | `inference-optimization.md` |
| Quality methodology        | `quality-gates.md`          |
| Security mechanisms        | `security.md`               |
| Observability architecture | `observability.md`          |

Do not duplicate downstream documents.

---

# 59. SOURCE GAPS

Include:

## SOURCE GAPS DISCOVERED

Carry forward relevant existing source-gap IDs unchanged.

Create new IDs only when genuinely necessary:

```text
SOURCE-GAP-CACHESTRAT-001
SOURCE-GAP-CACHESTRAT-002
...
```

For every gap state:

* what is missing;
* which source was checked;
* why it matters;
* whether it blocks this document;
* whether it is deployment-specific;
* downstream owner if applicable.

Do not manufacture gaps merely to make the document appear comprehensive.

---

# 60. SOURCE CONTRADICTIONS

Include:

## SOURCE CONTRADICTIONS

If none are found:

```text
No new source contradictions identified.
```

Do not reintroduce previously resolved contradictions as active.

For a genuine contradiction record:

* source A;
* source B;
* conflicting statements;
* authority level;
* affected cache policy;
* current treatment;
* whether downstream resolution is required.

---

# 61. ANTI-PATTERNS

Every cache type must have an explicit anti-pattern check.

At minimum cover:

1. Semantic cache without freshness.
2. Semantic cache without authorization.
3. Treating similarity as equivalence.
4. Caching mutable permission-sensitive data without validation.
5. Treating cache hit as quality guarantee.
6. Claiming savings without cache overhead.
7. Cross-tenant cache reuse.
8. Caching secrets.
9. Caching untrusted tool output.
10. Ignoring repository/worktree state.
11. Replaying side effects from cache.
12. Treating provider cache as EAIOC cache.
13. Ignoring deletion propagation.
14. Ignoring policy changes.
15. Ignoring context/workflow versions.
16. Over-keying.
17. Under-keying.
18. TTL as the only freshness mechanism.
19. Indiscriminate pre-warming.
20. Cache lookup slower than fresh execution.
21. Cache poisoning.
22. Cache metadata leakage.
23. Treating agent memory as execution truth.
24. Reusing stale checkpoint artifacts.
25. Fail-open security validation.

Reference actual `conventions.md` anti-pattern IDs/sections where available.

---

# 62. REQUIRED MATRICES

Include at least:

### Cache Type Matrix

| Cache Type | Purpose | Key | Scope | TTL | Freshness | Auth | Policy | Provider Bound | Partial Reuse |
| ---------- | ------- | --- | ----- | --- | --------- | ---- | ------ | -------------- | ------------- |

### Cache Decision Matrix

| Condition                                     | Decision                   |
| --------------------------------------------- | -------------------------- |
| Valid + fresh + authorized + policy compliant | Reuse                      |
| Stale                                         | Revalidate / bypass        |
| Authorization changed                         | Reject                     |
| Tenant mismatch                               | Reject                     |
| Policy mismatch                               | Reject                     |
| Dependency mismatch                           | Reject/recompute           |
| Provider/model incompatible                   | Reject/recompute           |
| Integrity failure                             | Reject + invalidate        |
| Negative economics                            | Bypass                     |
| Cache unavailable                             | Baseline execution if safe |

### Invalidation Matrix

| Trigger | Cache Types | Action |
| ------- | ----------- | ------ |

### Security Matrix

| Threat | Risk | Control | Failure Behavior |
| ------ | ---- | ------- | ---------------- |

### Provider Cache Boundary Matrix

| Dimension | EAIOC Cache | Provider-Native Cache |
| --------- | ----------- | --------------------- |

---

# 63. IMPLEMENTATION-NEUTRAL PSEUDOCODE

Provide implementation-neutral logic for:

* admission;
* key construction;
* lookup;
* validation;
* semantic reuse;
* partial reuse;
* invalidation;
* revalidation;
* negative optimization;
* cache write;
* recovery.

Do not invent APIs that conflict with `interfaces.md`.

---

# 64. TEST STRATEGY

Include tests for:

### Functional

* hit;
* miss;
* partial hit;
* stale;
* invalid;
* expiry;
* invalidation;
* refresh.

### Security

* tenant mismatch;
* authorization revocation;
* policy change;
* sensitive data;
* poisoning;
* prompt injection;
* malicious tool output.

### State

* context version change;
* workflow version change;
* repository change;
* file change;
* external dependency change.

### Provider

* model switch;
* provider switch;
* provider-native cache unavailable;
* capability change.

### Agents

* multi-step agent;
* sub-agent result;
* agent memory;
* developer-agent repository changes.

### Recovery

* checkpoint resume;
* stale checkpoint;
* superseded execution;
* non-idempotent operation.

### Performance

* cache stampede;
* hot key;
* high concurrency;
* storage exhaustion;
* invalidation storm.

---

# 65. FINAL REPORT

Include:

## Final Report

Report:

* number of canonical cache types;
* number of complete cache-policy entries;
* TTL policy count;
* proposed vs authoritative TTL distribution;
* similarity-threshold governance;
* economics methodology coverage;
* invalidation coverage;
* poisoning-defense coverage;
* tenant-isolation coverage;
* authorization coverage;
* data-governance coverage;
* provider-native boundary;
* developer-agent coverage;
* multi-agent coverage;
* recovery coverage;
* relevant scenario coverage;
* relevant edge-case coverage;
* interface coverage;
* optimization-catalog traceability;
* source-gap count;
* contradiction count;
* unresolved limitations.

Do not claim 100% coverage unless the validation actually proves it.

---

# 66. FINAL VALIDATION

Before declaring readiness, independently verify:

### Source

* [ ] All nine authoritative documents reviewed.
* [ ] Latest versions used.
* [ ] Authority order respected.
* [ ] No source modified.

### Cache Types

* [ ] Exactly seven cache types.
* [ ] All seven have complete policy entries.
* [ ] No eighth type invented.

### Key Strategy

* [ ] Every cache type is tenant-scoped.
* [ ] Key templates use actual supported fields.
* [ ] Over-keying and under-keying addressed.

### Economics

* [ ] Cache benefit methodology defined.
* [ ] Cache cost methodology defined.
* [ ] Lookup overhead included.
* [ ] Negative optimization supported.
* [ ] Proposed values clearly labeled.

### Security

* [ ] Tenant isolation.
* [ ] Authorization.
* [ ] Policy.
* [ ] PII/data governance.
* [ ] Content integrity.
* [ ] Cache poisoning.
* [ ] Deletion propagation.

### Freshness

* [ ] TTL.
* [ ] Version-based freshness.
* [ ] Dependency invalidation.
* [ ] Revalidation.
* [ ] Stale result protection.

### Provider

* [ ] Provider-native cache distinguished.
* [ ] Provider facts sourced from provider matrix.
* [ ] No fabricated provider claims.
* [ ] Provider portability addressed.

### Agents

* [ ] Agent memory boundary.
* [ ] Developer-agent caching.
* [ ] Repository/worktree state.
* [ ] Sub-agent caching.
* [ ] Tool/MCP caching.

### Recovery

* [ ] Checkpoint interaction.
* [ ] Resume != replay.
* [ ] Supersession.
* [ ] Side-effect safety.

### Traceability

* [ ] Interface IDs verified.
* [ ] Scenario IDs verified.
* [ ] Edge-case IDs verified.
* [ ] Optimization IDs verified.
* [ ] Objective/AC/SEC IDs verified.
* [ ] No fabricated identifiers.

### Scope

* [ ] No downstream document generated.
* [ ] No optimization catalog modification.
* [ ] No provider matrix modification.
* [ ] No source document modification.

---

# 67. FAILURE MODES THIS PROMPT MUST PREVENT

Do not:

1. Invent an eighth cache type.
2. Invent unsupported interface fields.
3. Present proposed TTLs as authoritative.
4. Present proposed economics constants as facts.
5. Treat similarity as semantic equivalence.
6. Treat cache hit as optimization success.
7. Ignore tenant isolation.
8. Ignore authorization.
9. Ignore policy.
10. Ignore data governance.
11. Cache secrets.
12. Cache poisoned content.
13. Allow economics to override security.
14. Replay side effects blindly.
15. Treat provider cache as EAIOC cache.
16. Assume provider cache portability.
17. Treat agent memory as execution truth.
18. Ignore repository/worktree state.
19. Use TTL as the only invalidation mechanism.
20. Force all 213 edge cases into cache-specific mechanisms.
21. Force all 263 scenarios into cache-specific mechanisms.
22. Create new TECH/DA entries.
23. Invent provider facts.
24. Invent scenario/interface IDs.
25. Modify upstream documents.
26. Generate downstream documents.
27. Declare readiness without validation.

---

# 68. EXECUTION ORDER

Follow this order:

1. Check whether `docs/cache-strategy.md` exists.
2. Read all nine authoritative documents.
3. Verify the live scenario/optimization/provider baselines.
4. Extract the actual `CacheStore` interface.
5. Extract the seven actual cache types.
6. Extract cache-related conventions.
7. Extract architecture cache mechanics.
8. Extract relevant edge cases.
9. Extract relevant scenarios.
10. Extract cache-related optimization catalog entries.
11. Extract provider-native cache boundary.
12. Build cache-key policy.
13. Build TTL policy.
14. Build semantic threshold governance.
15. Build cache economics.
16. Build prefix/breakpoint strategy.
17. Build fragmentation remediation.
18. Build invalidation cascade.
19. Build SRP integration.
20. Build tenant/security/PII controls.
21. Build poisoning defenses.
22. Build provider-native boundary.
23. Build developer-agent cache strategy.
24. Build multi-agent/sub-agent strategy.
25. Build recovery/checkpoint strategy.
26. Populate all seven cache-policy entries.
27. Build matrices.
28. Build traceability.
29. Record source gaps/contradictions.
30. Run complete validation.
31. Produce exactly one readiness verdict.

---

# 69. PRESERVATION REQUIREMENT

Do not lose or weaken any existing EAIOC architectural principle.

In particular preserve:

* stateful execution;
* current-state reconciliation;
* logical task context;
* model-admitted context;
* context criticality;
* tenant isolation;
* authorization;
* policy;
* data governance;
* provider accessibility;
* provider feasibility;
* agent memory boundary;
* checkpoint/recovery;
* supersession;
* stale result protection;
* negative optimization;
* security precedence;
* coding-agent state;
* multi-agent behavior;
* MCP/tool behavior;
* provenance;
* observability;
* auditability.

`cache-strategy.md` must refine cache behavior without redefining upstream architecture.

---

# 70. DO NOT FABRICATE

Never fabricate:

* providers;
* models;
* prices;
* cache TTLs;
* context limits;
* similarity thresholds;
* benchmarks;
* SLAs;
* interfaces;
* scenario IDs;
* edge-case IDs;
* optimization IDs;
* security controls;
* regulatory requirements;
* FTR assignments;
* production thresholds.

When unsupported:

```text
SOURCE-GAP
```

or:

```text
PROPOSED DEFAULT — VALIDATE LOCALLY BEFORE PRODUCTION USE
```

or:

```text
PROPOSED METHODOLOGY
```

as appropriate.

---

# 71. FINAL READINESS

End the document with:

## Cache Strategy Readiness

and exactly ONE of:

```text
READY FOR NEXT DOCUMENTATION PHASE
```

or:

```text
NOT READY — CACHE STRATEGY GAPS MUST BE RESOLVED
```

Do not include multiple readiness verdicts.

Do not declare readiness merely because all sections exist.

Readiness requires successful validation.

---

# 72. OUTPUT CONSTRAINT

Create/update ONLY:

```text
docs/cache-strategy.md
```

Do not modify:

```text
problemStatement.txt
Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md
architecture.md
interfaces.md
conventions.md
edge-cases.md
scenario-matrix.md
optimization-catalog.md
provider-matrix.md
```

Do not create:

```text
agent-optimization.md
inference-optimization.md
quality-gates.md
security.md
observability.md
eval.md
SCALING.md
implementation-plan.md
ADRs
```

Do not create any additional deliverable inside `docs/`.

Temporary scratch files, if required, must be outside `docs/` and removed afterward.

---

# 73. FINAL DONE CONDITION

The task is complete only when:

* all nine authoritative documents have been reviewed;
* the live interface contract has been verified;
* all seven canonical cache types have complete policy entries;
* tenant-scoped key construction is explicit;
* TTL policies are evidence-tagged;
* semantic threshold governance is explicit;
* cache economics are defined;
* negative optimization is supported;
* dependency-aware invalidation is specified;
* stale-result protection integration is specified;
* cache poisoning defense is specified;
* PII/data-governance gating is specified;
* provider-native cache boundary is explicit;
* developer-agent cache behavior is covered;
* multi-agent/sub-agent caching is covered;
* recovery/checkpoint interaction is covered;
* all cited IDs resolve to real authoritative IDs;
* no new optimization technique was invented;
* no upstream document was modified;
* no downstream document was created;
* source gaps/contradictions are honestly recorded;
* final validation passes;
* exactly one readiness verdict is present.

# END V3 MASTER PROMPT
