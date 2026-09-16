# V2 MASTER PROMPT — GENERATE `optimization-catalog.md`

You are a senior Optimization, AI Platform, FinOps, Reliability, Security, and Enterprise Architecture engineer responsible for the:

**Enterprise Agent & LLM Inference Optimization Control Plane (EAIOC)**

You are not writing a marketing document or a generic list of token-saving techniques.

You are creating the **canonical, implementation-oriented Optimization Catalog / Optimization Strategy Registry** for EAIOC.

This document becomes the authoritative optimization reference used by all subsequent optimization-specific documentation.

---

# 1. TASK

Produce:

`docs/optimization-catalog.md`

This must become the **single master reference for EAIOC optimization techniques, developer-agent optimization modules, optimization governance, optimization lifecycle, optimization composition, optimization safety, optimization validation, and optimization traceability.**

The catalog must be precise enough that:

1. An engineer can look up an optimization and understand:

   * what it does,
   * when it applies,
   * when it must not apply,
   * what it requires,
   * what it costs,
   * what it risks,
   * what state it depends on,
   * how it is validated,
   * how it fails,
   * how it falls back,
   * when it becomes stale,
   * and how it is observed.

2. Downstream documents can use this catalog without re-defining or contradicting optimization techniques.

3. Every canonical optimization can be traced back to the authoritative EAIOC source material.

4. Optimization is treated as a **stateful control-plane decision**, not merely as a token-reduction transformation.

5. Security, authorization, policy, governance, correctness, and task success always take precedence over optimization.

---

# 2. AUTHORITATIVE SOURCE HIERARCHY

Before writing anything, read and understand the following **seven authoritative documents completely**:

1. `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt`
2. `Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md`
3. `architecture.md`
4. `interfaces.md`
5. `conventions.md`
6. `edge-cases.md`
7. `scenario-matrix.md`

Use the latest supplied versions.

These seven documents are the authoritative source baseline for this task.

Do not silently substitute older versions.

Do not rely on memory of earlier conversations.

Do not infer that something exists merely because it would be architecturally useful.

Do not silently replace the terminology, identifiers, component names, interface names, or requirement terminology established by these documents.

If a source does not define something required for a complete catalog entry:

* do not invent it,
* document what is established,
* mark the missing portion as `SOURCE-GAP`,
* explain why the gap matters,
* and identify where the missing decision belongs.

---

# 3. AUTHORITATIVE PRIORITY

Use the following precedence when interpreting source material:

1. Latest Problem Statement
2. Latest Engineering Specification
3. Latest Architecture
4. Latest Interfaces
5. Latest Conventions
6. Latest Edge Cases
7. Latest Scenario Matrix

However, **do not silently resolve contradictions**.

If two authoritative documents contain contradictory statements:

* identify the contradiction,
* record both source references,
* identify affected catalog entries,
* document the current treatment,
* and record the contradiction under `SOURCE CONTRADICTIONS`.

Do not rewrite the source documents.

---

# 4. EXISTING OPTIMIZATION CATALOG

If an existing `optimization-catalog.md` exists:

1. Read it completely.
2. Preserve all valid existing content.
3. Preserve valid existing IDs.
4. Preserve useful definitions.
5. Preserve valid terminology.
6. Preserve research references.
7. Preserve source gaps.
8. Preserve traceability.
9. Preserve meaningful optimization strategies.

Do not silently delete valid prior work.

If restructuring is necessary, preserve semantic content.

If something is obsolete or contradictory, explicitly reconcile it rather than silently removing it.

---

# 5. OUTPUT SCOPE

Create/update exactly:

`docs/optimization-catalog.md`

Do not modify the seven authoritative source documents.

Do not create downstream documents.

Do not create:

* `provider-matrix.md`
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

Do not create any other deliverable inside `docs/`.

Temporary scratch/progress files, if required, must live outside `docs/` and should be deleted when finished.

---

# 6. CURRENT SCENARIO-MATRIX BASELINE

The latest authoritative `scenario-matrix.md` establishes:

* **263 unique scenarios**
* **30 domains A–AD**
* **93 compound scenarios**
* **213 edge cases**
* **71 interfaces**
* **22 dynamic/hardening components**
* 35 Positive scenarios
* 78 Negative scenarios
* 115 Boundary scenarios
* 26 Failure scenarios
* 9 Recovery scenarios
* 95 P0
* 115 P1
* 53 P2
* 0 P3
* 177 DIRECT EC coverage
* 35 PARTIAL EC coverage
* 0 NOT COVERED
* 1 DUPLICATE/SEMANTIC-DUPLICATE
* 6 genuine source gaps remain open
* 2 source gaps resolved
* CONTRA-001 resolved.

Use the actual latest scenario matrix as the authoritative source when validating these values.

Do not replace these values with assumptions.

---

# 7. DO NOT LIMIT SCENARIO REVIEW TO OPTIMIZATION DOMAINS

Do NOT review only:

* Domain P — Optimization
* Domain Q — Negative Optimization
* Domain AD — Compound Scenarios

Those domains are especially important, but they are not sufficient.

Review **all 263 scenarios** for optimization relevance.

Other domains may constrain optimization through:

* authorization,
* permissions,
* policy,
* context,
* cache,
* memory,
* workflow,
* model/provider selection,
* tool/MCP,
* concurrency,
* recovery,
* checkpointing,
* spend,
* human approval,
* security,
* content integrity,
* developer-agent execution,
* external state,
* supersession,
* stale state.

The catalog must not miss optimization constraints merely because the relevant scenario is outside Domain P/Q/AD.

---

# 8. EDGE-CASE REVIEW

Review all **213 edge cases**.

Do NOT require every edge case to become an optimization entry.

Classify optimization relevance as one of:

1. `Optimization-Specific`
2. `Optimization-Governance Relevant`
3. `Optimization-Failure/Recovery Relevant`
4. `Another EAIOC Subsystem`
5. `Not Applicable to Optimization Catalog`

Never force-fit an edge case into an optimization.

The Optimization Catalog is not a duplicate of `edge-cases.md`.

---

# 9. PARTIAL EDGE-CASE COVERAGE

The latest matrix contains **35 PARTIAL edge-case coverages**.

Treat them as known boundaries.

Do not:

* upgrade PARTIAL to DIRECT without source evidence,
* invent missing behavior,
* fabricate optimization strategies to improve coverage,
* claim complete behavior where only partial coverage exists.

Where a PARTIAL EC materially affects an optimization, document precisely what is covered and what remains unresolved.

---

# 10. COMPOUND SCENARIO ANALYSIS

Review all **93 compound scenarios**.

Use them to derive optimization:

* composition,
* ordering,
* compatibility,
* conflicts,
* invalidation,
* revalidation,
* security constraints,
* authorization constraints,
* policy constraints,
* cache interactions,
* model/provider interactions,
* tool/MCP interactions,
* memory interactions,
* concurrency behavior,
* checkpoint/recovery behavior,
* spend governance behavior,
* approval behavior,
* content-integrity behavior.

Do not create a new optimization simply because multiple subsystems interact.

Create an optimization entry only when there is a real optimization mechanism or canonical optimization capability.

---

# 11. REVIEW ALL 22 DYNAMIC/HARDENING COMPONENTS

Review all 22 dynamic-execution and hardening components.

For each component, determine whether it:

* decides optimization,
* constrains optimization,
* executes optimization,
* validates optimization,
* invalidates optimization,
* observes optimization,
* recovers optimization,
* governs optimization.

Do not invent relationships.

---

# 12. CORE EAIOC OBJECTIVE

The central objective is:

`MINIMIZE: Total Safe Inference Cost SUBJECT TO: Quality, Correctness, Security, Reliability, Latency, Freshness, Compliance, Task completion`

Therefore:

**Token reduction alone is never sufficient evidence of optimization success.**

The catalog must treat total safe execution value as the objective.

---

# 13. LOGICAL TASK CONTEXT VS MODEL-ADMITTED CONTEXT

The catalog MUST explicitly distinguish:

### Logical Task Context

All information potentially relevant to successful execution.

This can include:

* user request,
* system instructions,
* developer instructions,
* policies,
* authorization,
* conversation history,
* retrieved information,
* files,
* repository state,
* code,
* tools,
* MCP,
* tool results,
* agent memory,
* checkpoints,
* workflow state,
* external state,
* model/provider information,
* task metadata,
* security metadata,
* data classification,
* residency,
* budget,
* quality requirements.

### Model-Admitted Context

The subset of logical context actually admitted into one model invocation.

The conceptual flow is:

`Logical Context → Candidate Context → Ranked Context → Admitted Context`

Logical Task Context may exceed the model's context window.

Never assume that everything logically relevant should be sent to the model.

---

# 14. CONTEXT CRITICALITY

Use the established context criticality model:

### Tier 0 — Mandatory

Must not be silently removed.

### Tier 1 — High Value

Strongly relevant to task success.

### Tier 2 — Conditional

Relevant under particular conditions.

### Tier 3 — Recoverable

Can safely be retrieved again.

### Tier 4 — Redundant

Duplicated or unnecessary.

### Tier 5 — Stale / Invalid

Must not be admitted.

Mandatory information must never be silently discarded merely to satisfy a token budget.

---

# 15. EFFECTIVE CONTEXT BUDGET

Do not equate:

`Published Model Context Window = Usable Context Budget`

Account conceptually for:

* system prompt,
* developer instructions,
* policies,
* tool definitions,
* protocol overhead,
* conversation structure,
* output reservation,
* reasoning reservation,
* safety margin,
* provider constraints,
* application constraints.

Optimization should operate against the effective usable budget.

---

# 16. OPTIMIZATION PRECEDENCE

The catalog MUST establish this precedence:

**Security / Authorization / Policy / Data Governance
→ Safety / Integrity / Approval
→ Correctness / Task Requirements
→ Reliability / Recovery
→ Quality
→ Latency
→ Cost / Token Optimization**

Optimization must never bypass:

* authorization,
* security,
* policy,
* data governance,
* approval,
* integrity checks,
* residency,
* retention,
* tool trust,
* spend governance.

---

# 17. OPTIMIZATION OUTCOMES

The catalog must support controlled optimization decisions including:

* `OPTIMIZE`
* `NO_OP`
* `DO_NOT_OPTIMIZE`
* `BASELINE_EXECUTION`
* `REQUIRE_REVALIDATION`
* `DEFER`
* `FALLBACK`
* `ESCALATE`
* `ABORT`
* `ASK_USER` where applicable.

Explain the circumstances for each.

---

# 18. NEGATIVE OPTIMIZATION

Optimization itself can have negative value.

Explicitly account for:

* compression overhead,
* retrieval overhead,
* ranking overhead,
* routing overhead,
* cache overhead,
* evaluation overhead,
* extra model calls,
* extra tool calls,
* additional agent iterations,
* retries,
* latency degradation,
* quality degradation,
* cheaper model failure,
* stale-result effects,
* cache invalidation cost.

Optimization value must be evaluated as:

**Verified Net Optimization Value**

rather than raw token reduction.

Conceptually:

`Net Optimization Value = Optimization Benefit − Optimization Overhead − Quality/Reliability Cost − Additional Execution Cost`

Do not invent a mandatory numerical formula where the authoritative documents do not define one.

---

# 19. DO-NOT-OPTIMIZE CONDITIONS

Every optimization must identify when it should not be applied.

Examples include:

* mandatory information would be removed,
* security validation is unavailable,
* authorization is unknown,
* policy state is unknown,
* data governance cannot be satisfied,
* expected value is negative,
* optimization overhead exceeds expected savings,
* quality risk exceeds allowed limits,
* state is stale,
* provider capability is unavailable,
* required approval would be bypassed,
* optimization would create unsafe side effects.

---

# 20. DYNAMIC EXECUTION

EAIOC is a **stateful execution control plane**.

Optimization decisions must be treated as state-dependent.

Relevant state includes:

* user intent,
* request,
* context,
* files,
* repository,
* workflow,
* permissions,
* authorization,
* policies,
* model,
* provider,
* tools,
* MCP,
* memory,
* cache,
* budget,
* external state,
* task progress,
* security state.

An optimization decision made against state N must not automatically be trusted against state N+1.

---

# 21. OPTIMIZATION DECISION VS OPTIMIZATION RESULT STALENESS

Explicitly distinguish:

### Optimization Decision Staleness

The decision to apply an optimization is no longer valid.

### Optimization Result Staleness

The transformed result was valid when produced but is no longer valid for current execution state.

Both require appropriate revalidation/invalidation.

---

# 22. STATE/VERSION DEPENDENCIES

Where applicable, optimization entries should identify dependencies on:

* context version,
* workflow version,
* authorization version,
* policy version,
* external-state version,
* model/provider capability version,
* tool/MCP schema version,
* memory version,
* cache version,
* budget state,
* security state,
* optimizer/version.

---

# 23. OPTIMIZATION INVALIDATION

Identify state changes that invalidate optimization decisions/results.

Examples:

* context mutation,
* permission revocation,
* policy change,
* repository change,
* workflow mutation,
* model change,
* provider change,
* tool schema change,
* memory mutation,
* cache invalidation,
* budget exhaustion,
* security event,
* supersession,
* external state mutation.

---

# 24. OPTIMIZATION COMPOSITION

Document:

* compatible optimizations,
* conflicting optimizations,
* mutually exclusive optimizations,
* prerequisites,
* ordering,
* postconditions,
* rollback,
* reversibility,
* validation,
* invalidation propagation.

Do not assume a universal optimization order.

Document ordering only when supported by the authoritative architecture, interfaces, scenarios, edge cases, or credible research evidence.

---

# 25. CORE TECHNIQUE COUNT

Formalize **exactly one canonical TECH entry for each of the 20 core techniques defined in `architecture.md` §23**.

Do not automatically create additional canonical TECH IDs for every research mechanism or supporting implementation.

Additional techniques should only receive canonical IDs if an authoritative EAIOC source explicitly establishes them as separate canonical optimization techniques.

Supporting mechanisms and research techniques should normally be associated with the appropriate canonical technique.

---

# 26. TECHNIQUE ID SYSTEM

Use:

`TECH-<NNN>`

Three-digit zero-padded IDs.

Assign them in the exact order the 20 techniques appear in `architecture.md` §23.

Example:

`TECH-001`

Do not confuse:

* `TECH-NNN`
* `DA-NNN`
* `SCN-OPT-NNN`
* `SCN-NOPT-NNN`
* `EC-NNN`.

Cross-reference them explicitly.

---

# 27. DEVELOPER-AGENT MODULES

Formalize all **25 DA-NNN modules** from `architecture.md` §22.

Reuse their existing IDs exactly.

Do not renumber them.

Do not merge them with generic TECH entries.

For example:

* generic context pruning technique
* developer-agent code-aware context pruning module

may cover related ground but remain distinct.

The generic technique is pipeline-wide.

The DA module is a developer-agent specialization.

---

# 28. DERIVING DA MODULE DETAILS

`architecture.md` §22 may provide only names/descriptions for DA modules.

For the full schema, derive details from:

* architecture component specifications,
* edge cases,
* scenario matrix,
* related interfaces,
* requirements,
* conventions.

Never invent unsupported behavior.

Where insufficient information exists:

`SOURCE-GAP`

---

# 29. P5 INFERENCE/SERVING CAPABILITIES

Record the **11 P5 inference/serving capabilities** from `architecture.md` §26.

These must be **index-level only** in this document.

Do not provide full implementation treatment.

For each:

```text
### P5 Index — <Capability Name>

**Control-Plane Role:** awareness / routing / negotiation only
**Deferred To:** inference-optimization.md
**Source:** ARCH §26
```

Use the exact source terminology.

---

# 30. PROVIDER NEUTRALITY

No optimization may hard-code a specific provider, model, or vendor API as load-bearing logic.

Provider-specific behavior must be represented as:

* provider capability,
* provider hint,
* provider dependency,
* deployment capability.

Do not assume a third-party API exposes an inference-level optimization.

---

# 31. PROVIDER ACCESSIBILITY MODEL

Distinguish:

* discovered,
* recommended,
* eligible,
* authorized,
* accessible,
* available,
* capability-compatible.

Never assume:

`Discovered = Accessible`

or:

`Available = Authorized`

or:

`Recommended = Eligible`.

Never route to an inaccessible or unauthorized model/provider.

---

# 32. FAIL-OPEN OPTIMIZATION VS FAIL-CLOSED SECURITY

Optimization failure and security failure are different categories.

For an optimization technique failure on an already authorized, policy-compliant request:

→ normally fall back safely to the unoptimized path.

For:

* authorization failure,
* security failure,
* policy failure,
* data-governance failure,
* trust failure,

→ follow the appropriate fail-closed behavior.

Never use optimization fallback as a mechanism for bypassing security/governance.

---

# 33. SECURITY-PROTECTED CONTENT

Context Pruning, Context Deduplication, Context Compression, and developer-agent equivalents must explicitly account for protected content.

Security-protected or mandatory information must not be removed, compressed, summarized, or deduplicated in a manner that violates:

* security,
* authorization,
* integrity,
* policy,
* task correctness.

Authorized minimization/redaction is allowed only where explicitly supported by the applicable policy and authorization decision.

---

# 34. TENANT ISOLATION

Tenant isolation applies to **all optimization artifacts**, not only caches.

Consider tenant isolation for:

* context,
* cache,
* memory,
* optimization results,
* semantic indexes,
* tool results,
* repository context,
* checkpoints,
* provenance,
* metrics where applicable,
* derived artifacts.

Cross-tenant reuse must never occur unless explicitly authorized by the architecture/policy.

Caching entries must explicitly establish tenant-scoped cache keys.

Where applicable, `tenant_id` must be treated as a mandatory namespace boundary.

---

# 35. DATA GOVERNANCE

Optimization must account for:

* classification,
* sensitivity,
* encryption,
* retention,
* deletion,
* residency,
* logging,
* checkpointing,
* cache eligibility,
* memory eligibility.

If classification fails, follow the authoritative fail-safe behavior.

Deletion/erasure must propagate across relevant optimization artifacts.

---

# 36. CONTENT INTEGRITY

Optimization must not weaken:

* prompt-injection screening,
* malicious-content detection,
* RAG integrity,
* tool/MCP result screening.

Specifically cover:

* indirect prompt injection,
* malicious content surviving compression,
* malicious tool results,
* poisoned semantic cache,
* screening skipped due to authorization,
* compression preserving adversarial instructions.

---

# 37. HUMAN APPROVAL

Optimization must never bypass human approval.

For consequential or irreversible actions:

* approval remains mandatory,
* compressed context must not remove approval requirements,
* cached decisions cannot substitute for required current approval,
* approval must be revalidated after relevant state changes,
* approval must become invalid after supersession/cancellation where required.

---

# 38. SPEND GOVERNANCE

Integrate optimization with:

* request budgets,
* execution budgets,
* tenant budgets,
* concurrency budgets,
* runaway cost protection,
* budget evaluation failures,
* concurrent budget races,
* in-flight side effects,
* retry/cache/optimization storms.

Important:

**WITHIN_BUDGET is not authorization.**

Budget status must never override security or policy.

---

# 39. SELF-PROTECTION CONTROLLER

Explicitly address:

**EC-151 — Self-Protection Controller failure.**

If SPC fails:

* deterministic safe fallback is required,
* authorization remains active,
* security remains active,
* policy remains active,
* approval remains active,
* data governance remains active,
* spend governance remains active.

SPC failure must never become an optimization bypass.

---

# 40. MEMORY

Cover optimization of:

* working memory,
* memory relevance,
* stale memory,
* redundant memory,
* memory summarization,
* memory retrieval,
* memory cache,
* memory provenance,
* memory invalidation.

Respect:

> Agent may own working memory, but the Control Plane owns execution truth.

Current authorization, policy, workflow, and external state override stale or conflicting memory.

---

# 41. CACHE

Cover:

### EAIOC/Application Cache

* semantic cache,
* retrieval cache,
* context cache,
* tool-result cache,
* repository map cache,
* optimization-result cache,
* memory cache.

### Provider Prompt Cache

Treat provider prompt caching separately.

Cache eligibility must consider:

* tenant,
* authorization,
* policy,
* context version,
* source freshness,
* dependency versions,
* workflow,
* task intent,
* model/provider,
* tool/schema version,
* sensitivity,
* residency,
* retention.

Cover:

* hit,
* miss,
* partial hit,
* stale hit,
* invalid hit,
* poisoned cache,
* tenant mismatch,
* authorization mismatch,
* policy mismatch,
* version mismatch,
* unavailable cache,
* invalidation,
* cache overhead exceeding benefit.

---

# 42. GENERIC LLM OPTIMIZATIONS

Cover applicable categories including:

* context selection,
* relevance ranking,
* context pruning,
* context compression,
* semantic deduplication,
* redundancy elimination,
* context reordering,
* history reduction,
* stale-context removal,
* conditional context admission,
* lazy context loading,
* prompt minimization,
* prompt assembly,
* token budgeting,
* output budgeting,
* retrieval optimization,
* response optimization.

Do not create duplicate canonical TECH IDs merely for variants.

---

# 43. RAG OPTIMIZATION

Cover applicable mechanisms for:

* query optimization,
* retrieval filtering,
* retrieval depth,
* top-k selection,
* reranking,
* duplicate removal,
* chunk selection,
* adaptive retrieval,
* retrieval stopping,
* context compression,
* source freshness,
* provenance,
* citations,
* retrieval caching,
* cache invalidation,
* poisoning protection,
* prompt injection protection.

Never remove RAG information solely because it is long.

---

# 44. AGENT OPTIMIZATION

Cover:

* agent loop optimization,
* unnecessary iteration elimination,
* tool-call reduction,
* observation compression,
* state summarization,
* memory optimization,
* plan optimization,
* stopping criteria,
* early exit,
* retry optimization,
* duplicate action prevention,
* sub-agent delegation,
* sub-agent result compression,
* agent context budgeting.

Distinguish:

* tokens per iteration,
* number of iterations,
* tool calls,
* model calls.

---

# 45. MULTI-AGENT OPTIMIZATION

Cover:

* delegation,
* sub-agent selection,
* unnecessary sub-agent elimination,
* parallel vs sequential execution,
* shared-context minimization,
* result aggregation,
* result compression,
* duplicate work prevention,
* cross-agent cache reuse,
* context isolation,
* budget allocation,
* early completion,
* cancellation,
* supersession,
* concurrency coordination.

Never introduce cross-tenant leakage.

---

# 46. DEVELOPER/CODING-AGENT OPTIMIZATION

Cover the 25 DA modules and relevant techniques including:

* code-aware context pruning,
* symbol-level context selection,
* repository maps,
* repository-map caching,
* Git-diff optimization,
* changed-file prioritization,
* dependency-aware context selection,
* terminal-output optimization,
* compiler-error extraction,
* test-error extraction,
* linter-error extraction,
* error-driven retrieval,
* code-aware deduplication,
* file-section lazy loading,
* repository-search optimization,
* code context reordering,
* patch context optimization,
* sub-agent result compression,
* agent memory optimization,
* MCP/tool selection,
* tool schema optimization,
* developer-agent context budgets,
* loop-cost control,
* early exit,
* task completion detection,
* coding-agent cache,
* provenance,
* invalidation,
* change-impact analysis.

Do not invent numerical coding-agent token savings.

---

# 47. TOOL/MCP OPTIMIZATION

Cover:

* dynamic tool selection,
* tool relevance filtering,
* tool discovery,
* schema optimization,
* result compression,
* result filtering,
* duplicate call prevention,
* batching where supported,
* result caching where safe,
* availability resolution.

Integrate:

* authorization,
* trust,
* identity,
* schema version,
* policy,
* approval,
* content integrity.

A favorable tool ROI does not override trust failure.

---

# 48. MODEL ROUTING

Cover:

* complexity routing,
* quality-aware routing,
* latency-aware routing,
* cost-aware routing,
* reasoning-budget selection,
* escalation,
* downgrade/upgrade,
* fallback models,
* provider selection.

Never assume the cheapest model is appropriate.

Never assume the strongest model is appropriate.

Routing must be conditional and evidence-driven.

---

# 49. REASONING BUDGET

The Reasoning Budget Control entry must explicitly state:

> Never reduce reasoning budget solely to hit a token target.

Evaluation must include a quality baseline.

Token reduction alone cannot justify reducing reasoning budget.

---

# 50. INFERENCE OPTIMIZATION

Cover inference-level optimization at an index/awareness level where appropriate.

Potential areas:

* continuous batching,
* KV-cache optimization,
* PagedAttention,
* speculative decoding,
* quantization,
* prefix/prompt caching,
* prefill optimization,
* decode optimization,
* prefill/decode disaggregation,
* request scheduling,
* memory management.

Clearly distinguish:

**Control Plane Optimization**

from:

**Inference Infrastructure Optimization**

Do not assume third-party hosted APIs expose infrastructure-level controls.

Full treatment belongs to `inference-optimization.md`.

---

# 51. RECOVERY

Optimization recovery must distinguish:

**Resume != Replay**

Recovery must reconcile:

* checkpoint,
* current intent,
* context,
* workflow,
* permissions,
* policy,
* external state,
* completed work,
* remaining work,
* optimization state.

Possible recovery outcomes include:

* resume,
* recompute,
* retrieve,
* expand context,
* reduce context,
* invalidate cache,
* switch model,
* switch provider,
* restore checkpoint,
* rollback,
* restart,
* pause,
* ask user,
* safe abort.

Never blindly replay non-idempotent operations.

---

# 52. SUPERSESSION

When user intent or execution becomes superseded:

* stale optimization decisions must not continue,
* stale optimization results must not drive new execution,
* non-idempotent side effects must not be replayed,
* current state must be reconciled,
* new optimization decisions must be based on current state.

---

# 53. CONCURRENCY

Account for:

* concurrent executions,
* shared caches,
* shared memory,
* shared repositories,
* concurrent optimization decisions,
* budget races,
* state mutation,
* stale snapshots,
* non-idempotent actions.

Newer state must not be overwritten by stale optimization output.

---

# 54. OPTIMIZATION FAILURE CLASSIFICATION

Classify failures such as:

* transient,
* deterministic,
* stale-state,
* security-critical,
* governance-critical,
* quality-critical,
* infrastructure,
* provider,
* configuration,
* unsupported capability,
* unmeasurable value.

Distinguish:

**Optimization Failure**

from:

**Task Failure**

Non-critical optimization failure should normally permit safe baseline execution when allowed.

Security/governance failures may require fail-closed behavior.

---

# 55. CANONICAL VS SUPPORTING VS RESEARCH

Distinguish:

### Canonical EAIOC Optimization

Explicitly established as a core TECH or DA module.

### Supporting Mechanism

Implementation mechanism used by a canonical optimization.

### Research Technique

External technique informing EAIOC implementation.

### Provider Capability

Capability exposed by a model/provider.

### Deployment Capability

Infrastructure-specific capability.

Do not represent a research technique as a guaranteed EAIOC capability.

---

# 56. RESEARCH EVIDENCE

Relevant established research may include:

* LLMLingua
* LongLLMLingua
* LLMLingua-2
* Selective Context
* Lost in the Middle
* RAGCache
* FrugalGPT
* RouteLLM
* Agentic RAG research
* tokenomics research
* coding-agent token optimization research
* prompt caching
* tool search
* programmatic tool calling
* context editing
* continuous batching
* PagedAttention
* speculative decoding
* quantization
* KV-cache optimization
* other credible techniques supported by evidence.

Research evidence is not EAIOC authority.

---

# 57. NO FABRICATED BENCHMARKS

Never fabricate:

* token savings,
* cost savings,
* latency reduction,
* quality improvements,
* speedups,
* accuracy,
* success rates.

Every numeric benefit must identify an evidence classification.

Use the source's evidence classification scheme:

* Research evidence
* Provider capability
* Local benchmark result
* Production policy
* Production guarantee

Research numbers must be presented as reference/validation targets, not automatic production outcomes.

Production guarantees require actual local evidence.

---

# 58. CANONICAL CATALOG ENTRY SCHEMA

Every TECH and DA entry must use exactly this schema:

```text
### <TECH-NNN or DA-NNN> — <Technique/Module Name>

**Category:** ...
**Governing Component(s):** ...

**Applicability:** ...
**Explicitly Not Applicable When:** ...

**Prerequisites:** ...

**Expected Benefit:** ...

**Optimization Overhead:** ...

**Quality Risks:** ...

**Fallback:** ...

**Evaluation Methodology:** ...

**Observability Metrics:** ...

**Implementation Priority:** P0 | P1 | P2 | P3 | P4 | P5

**Maturity Level:** 0 RESEARCH | 1 EXPERIMENTAL | 2 SHADOW | 3 CONTROLLED PILOT | 4 PRODUCTION | 5 AUTO-TUNED

**Anti-Pattern Check:** ...

**State Dependencies:** ...

**Invalidation / Revalidation:** ...

**Security / Authorization / Policy Constraints:** ...

**Data Governance Constraints:** ...

**Provider / Model / Tool Dependencies:** ...

**Composition / Ordering / Conflicts:** ...

**Reversibility / Side Effects:** ...

**Validated By:** ...

**Traceability:**
- Problem Statement: ...
- Engineering Specification: ...
- Architecture: ...
- Interfaces: ...
- Conventions: ...
- Edge Cases: ...
```

Use:

`N/A — not applicable to this entry.`

when a field genuinely does not apply.

Do not leave required fields blank.

Do not use meaningless `TBD`.

---

# 59. IMPLEMENTATION PRIORITY VS MATURITY

Explicitly separate:

### Implementation Priority

P0–P5 from `architecture.md` §36.

Meaning:

> When should this capability be built relative to others?

### Maturity Level

0–5 from `architecture.md` §42.

Meaning:

> How mature/validated is this capability?

Do not combine them.

Document the original §23 conflation under `SOURCE CONTRADICTIONS`.

If corrected in this catalog, mark it as resolved **in this document**, without modifying the source.

---

# 60. ANTI-PATTERN CHECK

Every entry must contain a meaningful `Anti-Pattern Check`.

Use the exact relevant anti-patterns from `architecture.md` §41.

Do not use generic text repeatedly.

Examples:

Model Routing must guard against:

* always selecting strongest/most expensive model,
* always selecting cheapest model.

Context pruning must guard against indiscriminate compression/pruning.

The Applicability and Explicitly Not Applicable When fields must enforce the boundary.

---

# 61. FALLBACK CONTRACT

Every optimization's fallback must distinguish:

### Optimization Failure

→ safe unoptimized/baseline path where permitted.

from:

### Security / Authorization / Policy Failure

→ fail closed or otherwise follow authoritative governance behavior.

Do not allow an optimization fallback to bypass authorization or policy.

---

# 62. TRACEABILITY

Every entry must be traceable to the seven authoritative documents.

Use exact:

* section,
* subsection,
* requirement ID,
* interface ID,
* component ID,
* edge-case ID,
* scenario ID.

Never invent IDs.

---

# 63. VALIDATED BY

Use specific scenario IDs.

Examples:

* `SCN-OPT-NNN`
* `SCN-NOPT-NNN`
* `SCN-CMP-NNN`
* other actual scenario IDs when materially applicable.

Do not write:

> "Validated by Domain P."

Use exact scenario identifiers.

Never create dangling references.

---

# 64. REQUIREMENT TRACEABILITY

Where applicable trace:

* OBJ-001–OBJ-035
* AC-001–AC-053
* SEC-001–SEC-016
* NFR-001–NFR-014
* H01–H20.

Do not force requirements into entries when they are not materially relevant.

---

# 65. SCOPE BOUNDARIES

Include:

`## Scope Boundary`

Explain what this catalog covers and does not cover for downstream documents:

* provider-matrix,
* cache-strategy,
* agent-optimization,
* inference-optimization,
* quality-gates,
* security,
* observability,
* eval,
* SCALING,
* implementation-plan,
* ADRs.

Prevent downstream duplication and contradiction.

---

# 66. CROSS-DOCUMENT COVERAGE

Include:

`## Cross-Document Coverage Matrix`

Show how catalog content relates to the seven source documents.

Identify thin coverage where appropriate.

---

# 67. COVERAGE MATRIX

Include:

`## Coverage Matrix`

Include:

* category counts,
* TECH count,
* DA count,
* P5 count,
* Implementation Priority distribution,
* Maturity Level distribution.

---

# 68. ANTI-PATTERN COVERAGE

Include:

`## Anti-Pattern Coverage`

Reverse map:

`ARCH §41 anti-pattern → catalog entries guarding against it`

Flag any anti-pattern with no meaningful catalog-level guard.

---

# 69. REQUIREMENT → CATALOG TRACEABILITY

Include:

`## Requirement → Catalog Traceability`

Map:

* OBJ,
* SEC,
* NFR,
* AC

to catalog entries where applicable.

---

# 70. SCENARIO VALIDATION CROSS-REFERENCE

Include:

`## Scenario Validation Cross-Reference`

Map relevant optimization scenarios to catalog entries.

At minimum:

* every Domain P optimization scenario,
* every Domain Q negative-optimization scenario,
* every relevant Domain AD compound scenario.

Also include relevant scenarios from other domains.

Do not claim that every one of the 263 scenarios must map to a catalog entry.

---

# 71. EDGE-CASE RELEVANCE SUMMARY

Include a concise summary of the 213 EC classification:

* optimization-specific,
* optimization-governance,
* optimization-failure/recovery,
* another subsystem,
* N/A.

Explicitly identify the 35 PARTIAL cases where they materially affect optimization.

---

# 72. SOURCE GAPS

Include:

`## SOURCE GAPS DISCOVERED`

For each:

* Gap ID
* affected entry
* missing information
* why it matters
* authoritative source that should eventually define it
* recommended disposition
* blocking/non-blocking status.

Do not invent a resolution.

---

# 73. SOURCE CONTRADICTIONS

Include:

`## SOURCE CONTRADICTIONS`

For each:

* contradiction ID,
* source documents,
* source sections,
* conflicting statements,
* affected catalog entries,
* current treatment,
* recommended resolution,
* status.

The Implementation-Priority/Maturity conflation must be recorded here.

---

# 74. READINESS INTERPRETATION

Do not treat every source gap as blocking.

Use:

`READY FOR NEXT DOCUMENTATION PHASE`

when:

* canonical techniques are fully cataloged,
* canonical DA modules are fully cataloged,
* required traceability exists,
* safety/security constraints are clear,
* remaining source gaps are explicitly bounded,
* remaining gaps are non-blocking or appropriately deferred.

Use:

`NOT READY — CATALOG GAPS MUST BE RESOLVED`

only when an unresolved gap prevents correct interpretation, safe behavior, traceability, or implementation of a canonical catalog capability.

---

# 75. FINAL REPORT

Include:

`## Final Report`

Summarize:

* TECH count,
* DA count,
* P5 count,
* category distribution,
* priority distribution,
* maturity distribution,
* source gaps,
* contradictions,
* anti-pattern coverage,
* requirement coverage,
* scenario coverage,
* edge-case classification,
* compound-scenario considerations,
* governing components,
* production-guarantee claims,
* research evidence,
* unresolved limitations.

---

# 76. OPTIMIZATION CATALOG READINESS

End the document with:

`## Optimization Catalog Readiness`

The final readiness verdict must be exactly one of:

`READY FOR NEXT DOCUMENTATION PHASE`

or

`NOT READY — CATALOG GAPS MUST BE RESOLVED`

Do not append additional text to the verdict line.

---

# 77. REQUIRED 20 TECHNIQUE VALIDATION

Run:

```text
Grep ^### TECH-
```

Expected:

**20**

Confirm the IDs are unique and correspond to the 20 architecture §23 techniques.

---

# 78. REQUIRED DA VALIDATION

Run:

```text
Grep ^### DA-
```

Expected:

**25**

Confirm exactly:

`DA-001` through `DA-025`

with no missing or duplicate IDs.

---

# 79. REQUIRED P5 VALIDATION

Run:

```text
Grep ^### P5 Index
```

Expected:

**11**

---

# 80. FIELD COMPLETENESS VALIDATION

Every TECH/DA entry must contain:

* Category
* Governing Component(s)
* Applicability
* Explicitly Not Applicable When
* Prerequisites
* Expected Benefit
* Optimization Overhead
* Quality Risks
* Fallback
* Evaluation Methodology
* Observability Metrics
* Implementation Priority
* Maturity Level
* Anti-Pattern Check
* State Dependencies
* Invalidation / Revalidation
* Security / Authorization / Policy Constraints
* Data Governance Constraints
* Provider / Model / Tool Dependencies
* Composition / Ordering / Conflicts
* Reversibility / Side Effects
* Validated By
* Traceability.

No field may be missing.

---

# 81. PLACEHOLDER VALIDATION

Do not allow:

* blank fields,
* `TBD`,
* `TODO`,
* `...`,
* meaningless `N/A`.

Use:

`N/A — not applicable to this entry.`

when appropriate.

---

# 82. NUMERIC BENEFIT VALIDATION

Every numeric Expected Benefit must have evidence classification.

Do not allow unsupported production guarantees.

Research numbers must be clearly framed as reference evidence or local revalidation targets.

---

# 83. SECURITY VALIDATION

Explicitly verify:

* protected content handling,
* authorization,
* policy,
* tenant isolation,
* cache isolation,
* tool trust,
* MCP integrity,
* human approval,
* prompt injection,
* data governance,
* stale authorization,
* security-event handling.

---

# 84. NEGATIVE OPTIMIZATION VALIDATION

Explicitly verify:

* `DO_NOT_OPTIMIZE`,
* `NO_OP`,
* baseline execution,
* optimization overhead,
* net-value assessment,
* quality degradation,
* latency degradation,
* retry amplification,
* retrieval overhead,
* cache overhead.

---

# 85. DYNAMIC STATE VALIDATION

Explicitly verify:

* stale optimization decisions,
* stale optimization results,
* state mutation,
* version mismatch,
* permission revocation,
* policy changes,
* workflow mutation,
* repository mutation,
* provider/model changes,
* tool schema changes,
* memory mutation,
* cache invalidation,
* supersession.

---

# 86. RECOVERY VALIDATION

Verify:

* Resume != Replay
* checkpoint compatibility,
* optimization state recovery,
* revalidation,
* rollback,
* recomputation,
* retrieval,
* model/provider switching,
* non-idempotent side-effect protection.

---

# 87. EC-151 VALIDATION

Explicitly verify EC-151.

The final catalog must demonstrate that SPC self-failure cannot bypass:

* security,
* authorization,
* policy,
* approval,
* data governance,
* spend governance.

---

# 88. AUTOMATED STRUCTURAL VALIDATION

Run scripted checks across the finished file.

Verify:

1. Exactly 20 TECH entries.
2. Exactly 25 DA entries.
3. Exactly 11 P5 entries.
4. No duplicate IDs.
5. Every entry has every required field.
6. Fields appear in the correct order.
7. No blank required fields.
8. No invalid placeholders.
9. Every summary reference resolves to a real catalog entry.
10. Every scenario reference exists.
11. No dangling SCN references.
12. No dangling TECH/DA references.
13. No orphan catalog entries.
14. No vendor hard-coding.
15. Tenant-isolation requirements are present.
16. Reasoning-budget prohibition is present.
17. Security-protected-content exceptions are present.
18. DA-009 compressed-handoff rule is present.
19. EC-151 is explicitly addressed.
20. Source files were not modified.

---

# 89. ANTI-PATTERN VALIDATION

Verify every entry has:

* concrete applicability,
* concrete non-applicability conditions,
* meaningful anti-pattern references.

Cross-check against `architecture.md` §41.

The Anti-Pattern Coverage section must show no unexplained uncovered anti-pattern.

---

# 90. TARGETED SEMANTIC SAMPLING

After automated validation:

Open and verify:

* at least 5 TECH entries,
* at least 5 DA entries,
* all 11 P5 entries.

For each sampled entry verify:

* applicability is concrete,
* non-applicability is concrete,
* traceability references actually exist,
* scenario IDs actually exercise the entry,
* edge-case references actually exist,
* governing components actually exist,
* evidence claims are supported.

---

# 91. SPECIAL REQUIRED VALIDATIONS

Explicitly validate these known high-risk areas:

### Reasoning

Never reduce reasoning solely to meet token targets.

### Caching

Tenant isolation, authorization, policy, freshness, versioning.

### Context compression

Do not remove mandatory/protected information.

### Developer agents

Do not replay full history to sub-agents.

### DA-009

Use the compressed `SubAgentContextHandoff` structure where established rather than replaying complete history.

### Model routing

Do not always select strongest or cheapest model.

### Tool/MCP

Trust and authorization override optimization.

### Memory

Current execution truth overrides stale memory.

### Checkpoint

Resume != replay.

### Spend

Budget != authorization.

### SPC

Self-failure must have deterministic safe fallback.

---

# 92. FAILURE MODES TO DEFEND AGAINST

The final document must explicitly defend against:

1. Drafting from memory.
2. Partial source reading.
3. Hallucinated traceability.
4. Invented optimization techniques.
5. Invented component IDs.
6. Invented benefit numbers.
7. Research benchmark treated as production guarantee.
8. Implementation Priority/Maturity conflation.
9. DA modules receiving weaker treatment than core techniques.
10. Scope creep into downstream documents.
11. Security being weakened by optimization fallback.
12. Tenant leakage.
13. Stale optimization decisions.
14. Stale optimization results.
15. Negative optimization.
16. Optimization overhead exceeding benefit.
17. Compound optimization conflicts.
18. Concurrency races.
19. Superseded execution continuing.
20. Recovery replaying non-idempotent actions.
21. Provider accessibility assumptions.
22. SPC self-failure bypassing governance.
23. PARTIAL ECs being incorrectly promoted to DIRECT.
24. Every EC being artificially mapped to an optimization.
25. Every scenario being artificially mapped to an optimization.
26. Research techniques being mistaken for canonical EAIOC techniques.

---

# 93. NO HALLUCINATION RULE

Do not create:

* unsupported optimization techniques,
* unsupported module behavior,
* unsupported component IDs,
* unsupported scenario IDs,
* unsupported EC IDs,
* unsupported benefit numbers,
* unsupported provider capabilities,
* unsupported guarantees.

If information is missing:

`SOURCE-GAP`

is the correct answer.

---

# 94. NO SOURCE REWRITES

Do not modify source documents to resolve an issue discovered during catalog creation.

For example, if `architecture.md` contains the Implementation Priority/Maturity conflation:

* fix the interpretation in the catalog,
* document the contradiction,
* do not edit architecture.md.

---

# 95. DOCUMENT QUALITY

The final document must be:

* enterprise-grade,
* technically rigorous,
* implementation-oriented,
* state-aware,
* security-aware,
* governance-aware,
* provider-neutral,
* testable,
* observable,
* recoverable,
* traceable,
* extensible.

Avoid:

* marketing language,
* generic AI explanations,
* shallow token-saving advice,
* unsupported claims,
* repetitive filler,
* artificial complexity.

---

# 96. EXECUTION ORDER

Execute in this order:

1. Read all seven source documents completely.
2. Read the existing optimization catalog completely.
3. Extract canonical techniques.
4. Extract DA modules.
5. Extract P5 capabilities.
6. Extract optimization-related components.
7. Review all 263 scenarios.
8. Review all 213 edge cases.
9. Classify EC relevance.
10. Review all 93 compound scenarios.
11. Review all 71 interfaces.
12. Review all 22 dynamic/hardening components.
13. Review OBJ-001–035.
14. Review AC-001–053.
15. Review SEC-001–016.
16. Review NFR-001–014.
17. Review H01–H20.
18. Build the optimization inventory.
19. Preserve valid existing catalog information.
20. Assign TECH IDs.
21. Formalize the 20 core techniques.
22. Formalize DA-001–DA-025.
23. Add the 11 P5 index entries.
24. Define dynamic-state dependencies.
25. Define invalidation/revalidation.
26. Define negative optimization behavior.
27. Define composition and ordering.
28. Define security/governance constraints.
29. Define failure/fallback/recovery.
30. Add research evidence.
31. Add traceability.
32. Add cross-reference matrices.
33. Add source gaps.
34. Add source contradictions.
35. Add scope boundaries.
36. Add final report.
37. Run automated validation.
38. Run semantic sampling.
39. Fix only issues supported by source evidence.
40. Re-run validation.
41. Produce the final readiness verdict.

---

# 97. IMPORTANT — DO NOT STOP EARLY

This is a large, long-horizon documentation task.

Do not:

* stop after creating the first few entries,
* summarize instead of completing entries,
* omit DA modules,
* omit P5 entries,
* omit traceability,
* omit validation,
* truncate the final report,
* reduce schema depth merely to finish faster.

Every canonical TECH and DA entry must receive the full schema.

---

# 98. FINAL DONE CONDITION

The task is complete only when:

* 20 TECH entries exist,
* 25 DA entries exist,
* 11 P5 index entries exist,
* every TECH/DA entry follows the full schema,
* Implementation Priority and Maturity Level are separate,
* all 263 scenarios have been reviewed for relevance,
* all 213 ECs have been reviewed/classified,
* all 35 PARTIAL EC boundaries are respected,
* all 93 compound scenarios have been reviewed,
* all 71 interfaces have been reviewed,
* all 22 dynamic/hardening components have been reviewed,
* security/governance precedence is explicit,
* negative optimization is explicit,
* dynamic-state staleness is explicit,
* recovery is explicit,
* supersession is explicit,
* EC-151 is explicitly addressed,
* research claims are evidence-classified,
* no unsupported benchmark is presented as a guarantee,
* no source document is modified,
* no downstream document is created,
* source gaps are documented,
* contradictions are documented,
* automated validation passes,
* semantic sampling passes,
* final readiness verdict is present.

---

# 99. FINAL INSTRUCTION

Create/update **ONLY**:

`docs/optimization-catalog.md`

Do not modify any other source or downstream document.

Do not invent missing information.

Do not silently resolve contradictions.

Do not force-fit scenarios or edge cases into optimization entries.

Do not confuse optimization activity with optimization value.

Do not confuse token reduction with task success.

Do not allow optimization to bypass security, authorization, policy, governance, correctness, approval, or reliability.

Treat the latest seven authoritative documents as the source of truth.

Treat the latest scenario matrix as the validation baseline.

Treat this catalog as the canonical optimization foundation for every subsequent EAIOC documentation phase.

**Begin by reading all seven authoritative documents completely.**

After reading them, establish the TECH-ID assignment order and maintain a scratch progress plan outside `docs/`.

Then create the catalog incrementally and perform all required validation before declaring readiness.

# END V2 MASTER PROMPT
