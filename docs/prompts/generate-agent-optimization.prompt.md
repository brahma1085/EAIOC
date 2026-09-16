# FINAL MASTER PROMPT — GENERATE `docs/agent-optimization.md`

## 1. ROLE

Act as a **Senior Agentic-Systems, Multi-Agent Orchestration, LLM Optimization, and FinOps engineer** responsible for the:

**Enterprise Agent & LLM Inference Optimization Control Plane (EAIOC)**

Create the canonical:

**Agent Loop, Sub-Agent Economics, Session-Phase Policy, Multi-Agent Coordination, and Agent Optimization Reference**

for EAIOC.

This document is implementation-oriented.

An engineer implementing the agent-loop controller, sub-agent spawner/aggregator, agent-memory optimization, developer-agent context controller, reasoning/output-budget controller, or multi-agent coordinator must be able to use this document to understand:

* what must be measured,
* what state must be reconciled,
* what decision logic applies,
* what prerequisites exist,
* what policies may be proposed,
* what is authoritative versus proposed,
* what happens when inputs are unavailable or stale,
* how optimization interacts with security/governance,
* how decisions are verified,
* and how failures/recovery are handled.

This is **not** a generic "agentic AI patterns" document.

---

# 2. TASK

Create or update exactly:

```text
docs/agent-optimization.md
```

This document becomes the canonical reference for:

1. Agent-loop progress measurement and waste detection:

   * `AL-001`
   * `AL-002`

2. Sub-agent value/cost estimation and spawn gating:

   * `AL-003`

3. Sub-agent scheduling and parallelization:

   * `AL-004`

4. Mandatory compressed sub-agent handoff:

   * `AL-005`

5. Agent task completion / early exit:

   * `AL-006`

6. Adaptive model/reasoning/output control within an agent loop:

   * `AR-001`
   * `AR-002`
   * `AR-003`
   * `AR-004`

7. Session-phase policy defaults, **only if supported by authoritative sources or explicitly labeled as proposed**

8. Multi-agent/sub-agent coordination

9. Concrete algorithms behind the agent-loop-facing optimization-catalog modules:

```text
DA-009
DA-010
DA-013
DA-014
DA-018
DA-020
DA-021
```

10. Agent memory versus execution-truth boundary enforcement

11. Coding/developer-agent optimization

12. Autonomous/tool-using agent optimization

13. Agentic RAG optimization

14. MCP/tool optimization

15. Cache-assisted agent optimization

16. Provider/model feasibility and routing within agent execution

17. Recovery, checkpoint, cancellation, and supersession-aware optimization

18. Security, authorization, policy, tenant, data-governance, and human-approval constraints on optimization

---

# 3. AUTHORITATIVE SOURCE DOCUMENTS

Read and understand these **ten authoritative upstream documents completely** before drafting:

1. `docs/Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt`
2. `docs/Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md`
3. `docs/architecture.md`
4. `docs/interfaces.md`
5. `docs/conventions.md`
6. `docs/edge-cases.md`
7. `docs/scenario-matrix.md`
8. `docs/optimization-catalog.md`
9. `docs/provider-matrix.md`
10. `docs/cache-strategy.md`

Use the latest supplied versions.

Do not rely on an earlier prompt's restatement of counts, IDs, schemas, formulas, or requirements when the live source can be checked.

If a source does not define something needed:

```text
SOURCE-GAP
```

is the correct treatment.

Do not silently invent authoritative behavior.

---

# 4. AUTHORITATIVE PRIORITY

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
>
Cache Strategy
>
This document
```

If two sources conflict:

1. Identify the contradiction.
2. Apply the authority order.
3. Preserve the higher-authority requirement.
4. Record the contradiction.
5. Do not rewrite the source document.

Do not silently reconcile contradictory requirements.

---

# 5. DOCUMENT CHAIN

The already-established documentation chain is:

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
cache-strategy.md
agent-optimization.md
```

Remaining downstream documents include:

```text
inference-optimization.md
quality-gates.md
security.md
observability.md
eval.md
SCALING.md
implementation-plan.md
requirements-traceability.md
ADRs
```

`requirements-traceability.md` is part of the planned documentation chain.

**Do not create it in this task.**

Do not create or modify any downstream document.

---

# 6. EXISTING FILE HANDLING

Before writing:

1. Check whether `docs/agent-optimization.md` already exists.
2. If it does not exist, create it.
3. If it already exists, read it completely.
4. Preserve all valid existing content, IDs, traceability, source gaps, contradictions, and established decisions.
5. Make the smallest changes necessary to reconcile it with current authoritative sources.

Do not rewrite an existing valid document merely for stylistic reasons.

---

# 7. OUTPUT SCOPE

Modify/create **ONLY**:

```text
docs/agent-optimization.md
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
cache-strategy.md
```

Do not create:

```text
inference-optimization.md
quality-gates.md
security.md
observability.md
eval.md
SCALING.md
implementation-plan.md
requirements-traceability.md
ADRs
```

Do not create any other deliverable under `docs/`.

Temporary scratch material must remain outside `docs/` and be deleted after use.

---

# 8. LIVE BASELINE VERIFICATION

Before quoting any project counts, read the live final-report sections.

Do not blindly copy counts from this prompt.

Verify from the current files:

* scenario count
* domain count
* compound scenario count
* interface count
* edge-case count
* optimization-catalog entry counts
* current source-gap counts
* current contradiction status

The currently established baseline is expected to be approximately:

```text
263 unique scenarios
30 domains
93 compound scenarios
71 interfaces
213 edge cases
20 TECH entries
25 DA entries
11 P5 Index entries
```

But these values MUST be re-derived from the live authoritative documents before inclusion.

---

# 9. CENTRAL PRINCIPLE — AGENT MEMORY AUTHORITY (H09)

Preserve the authoritative principle:

> Agent-owned working memory is never authoritative over execution state, current authorization, current policy, or context/workflow versions.

Agent memory may contain:

* scratch state
* temporary reasoning
* temporary plans
* transient observations
* working memory

The Control Plane owns execution truth including:

* request
* execution state
* context versions
* workflow versions
* authorization decisions
* policy decisions
* optimization decisions
* checkpoints
* provenance
* cost
* quality
* recovery metadata
* audit metadata

External systems remain authoritative for:

* repositories
* files
* databases
* identity
* permissions
* business state

Every algorithm that reads or writes state that could influence execution truth MUST account for reconciliation with authoritative state.

When memory disagrees with ESM/CVM/WVM-owned state:

```text
ESM/CVM/WVM authoritative state wins.
```

The disagreement must not be silently hidden.

Reference this principle wherever materially relevant.

---

# 10. AGENT OPTIMIZATION CONTROL LOOP

Define a state-aware control loop:

```text
Observe
  ↓
Reconcile
  ↓
Validate
  ↓
Classify
  ↓
Generate Candidate Optimizations
  ↓
Check Preconditions
  ↓
Authorization / Policy / Governance
  ↓
Capability / Feasibility
  ↓
Estimate Benefit
  ↓
Estimate Optimization Overhead
  ↓
Evaluate Quality / Execution Risk
  ↓
Select
  ↓
Apply
  ↓
Verify
  ↓
Measure
  ↓
Continue / Reconcile / Roll Back / Recover
```

The loop may repeat because execution state can change.

---

# 11. AL-001 — AGENT LOOP PROGRESS MEASUREMENT

Restate the authoritative `AgentIterationReport` contract from `interfaces.md`.

Verify the exact live fields.

Do not invent fields.

Cover measurement of:

* input tokens
* output tokens
* tool cost
* new information gained
* state change
* objective progress
* errors introduced
* errors resolved

Specify concrete measurement methodology where possible.

If the source does not specify a universal formula, label proposed methods:

```text
PROPOSED METHODOLOGY
```

Do not present task-specific heuristics as universal truth.

Objective progress must be tied to task completion criteria rather than a generic token/step heuristic.

---

# 12. AL-002 — LOOP WASTE DETECTION

Preserve H10 exactly in substance:

> Repeated iteration is not inherently waste.

Repeated tool calls or repeated reasoning may represent:

* productive incremental progress
* legitimate reflection
* self-correction
* justified retry
* additional information gathering
* genuine oscillation
* failure

Therefore:

**Do not classify waste based solely on iteration count or pattern matching.**

Waste detection must use:

* progress signals
* expected value
* state changes
* information gained
* errors resolved
* objective progress
* tool cost
* token cost
* latency
* task requirements

The same expected-value framework must govern:

```text
CONTINUE
```

and:

```text
STOP / EARLY EXIT
```

Do not introduce an authoritative fixed threshold such as:

```text
N repeated calls = waste
```

Any proposed heuristic must be explicitly labeled:

```text
PROPOSED DEFAULT — VALIDATE LOCALLY
```

---

# 13. AL-003 — SUB-AGENT VALUE PREDICTOR

Preserve the established ROI formulation:

```text
Sub-Agent ROI =
Expected Sub-Agent Value /
(
  Agent Inference Cost
  + Context Duplication Cost
  + Aggregation Cost
  + Parent Ingestion Cost
  + Latency
  + Coordination Overhead
)
```

Specify a methodology for estimating expected sub-agent value.

Possible methodology categories may include:

* information gain
* task decomposition value
* uncertainty reduction
* independent verification value
* parallelization benefit

Do not invent universal numeric values.

If proposing an ROI threshold:

```text
PROPOSED DEFAULT — VALIDATE LOCALLY
```

Use the authoritative failure scenarios and edge cases for validation, including where present:

* sub-agent spawned after parent already completed work
* sub-agent value/cost below threshold
* sub-agent spawned without value/cost analysis

---

# 14. AL-004 — SUB-AGENT SCHEDULER

Define concrete conditions for independent work.

Two tasks may be treated as independently parallelizable only when appropriate conditions hold, including:

* no shared mutable-state dependency
* no ordering dependency
* no unsafe shared non-idempotent side effect
* required authorization exists
* required capabilities are available
* concurrency limits allow execution
* budget permits parallelization
* aggregation remains feasible

Use XEC as the established cross-execution coordination mechanism.

Do not redefine XEC itself.

---

# 15. AL-005 — MANDATORY SUB-AGENT HANDOFF

This is mandatory.

Full conversation-history replay to a sub-agent is prohibited.

Use the authoritative `SubAgentResultHandoff` / `SubAgentFinding` schemas from `interfaces.md`.

Verify the exact field names in the live source.

Do not invent, rename, or approximate fields.

Specify:

* how raw sub-agent state becomes compressed findings
* objective
* findings
* evidence
* decisions
* unresolved questions
* confidence
* evidence references
* parent ingestion cost
* provenance

Any compression methodology not source-defined must be:

```text
PROPOSED METHODOLOGY
```

There is no efficiency exception allowing full transcript replay.

If the handoff is insufficient:

```text
Enrich the SubAgentResultHandoff.
```

Do not bypass the schema with transcript replay.

Validate against the relevant `EC-*` and `SCN-*` IDs after verifying they actually exist.

---

# 16. AL-006 — AGENT TASK COMPLETION / EARLY EXIT

Define early exit when:

* objective is satisfied
* required validation passes
* additional information has low expected value
* additional calls are unlikely to change the outcome

Verify exact interface field names for any savings metrics such as:

```text
agent.tokens_saved_by_exit
agent.steps_skipped
```

Do not invent fields.

Cross-reference:

* Agent Stop Controller
* AgentEarlyExitEvaluator
* AL-001
* AL-002

Early exit must use the same expected-value framework as continuation.

---

# 17. AR-001 — MARGINAL-GAIN MODEL ROUTING

Preserve:

```text
Marginal Model Value =
Expected Quality Gain / Additional Cost
```

Apply this per agent iteration.

Cover:

* failed verification
* changing task state
* model capability changes
* quality requirements
* cost changes
* latency changes

Cross-reference existing:

* `TECH-013` Model Routing
* `TECH-014` Model Cascade/Escalation

Do not redefine the catalog entries.

---

# 18. AR-002 — REASONING BUDGET PREDICTION

Cover prediction using relevant signals:

* task complexity
* historical task outcomes
* tool requirements
* verification requirements
* model behavior
* user-defined quality tier
* current execution state

Critical rule:

**Never reduce reasoning budget solely to hit a token target.**

Any reasoning-budget reduction must be benchmarked against a quality baseline where required by authoritative conventions.

Cross-reference:

```text
TECH-015 — Reasoning Budget Control
```

---

# 19. AR-003 — OUTPUT BUDGET FORECASTING

Restate the authoritative output-type policy from architecture where applicable:

* Extraction
* Classification
* Coding
* Summarization
* Analysis
* Tool responses

Specify how expected output length is forecast before each iteration.

Cross-reference:

```text
TECH-018 — Output Length Control
```

Do not redefine the catalog technique.

---

# 20. AR-004 — VERIFIER-GUIDED ESCALATION

Cover:

```text
Lower-cost inference
      ↓
Verification
      ↓
Pass → continue
Fail → escalate/retry
```

Potential verifiers:

* unit tests
* schema validation
* type checking
* static analysis
* business rules
* citation checks
* format validation

Compare against always using the stronger model where evaluation data exists.

Reference VCL without redefining VCL's calibration mechanism.

A verifier's pass/fail result does not automatically guarantee correctness beyond the verifier's actual coverage.

---

# 21. TOOL-EXECUTION OPTIMIZATION

Integrate existing tool optimization mechanisms rather than duplicating their definitions.

Cover:

* tool ROI
* tool selection
* tool authorization
* tool availability
* tool capability compatibility
* parallel tool execution
* result filtering
* tool-result compression
* tool-result reuse
* tool-result freshness
* tool failure
* timeout
* retries
* side effects
* non-idempotent operations
* provenance

Use existing architecture/catalog IDs after verifying their current mappings.

Do not invent new catalog IDs.

---

# 22. MCP OPTIMIZATION

Treat MCP as first-class.

Cover:

* discovery
* capability resolution
* authorization
* trust
* tool selection
* result handling
* caching
* freshness
* poisoning
* timeout
* failure
* server unavailability
* capability/version changes
* context/token overhead

Tool/MCP Trust Gate takes precedence over optimization.

---

# 23. SESSION-PHASE POLICY

Before defining session phases:

1. Search all ten sources for an authoritative session-phase concept.
2. If source-defined, document it accurately.
3. If not source-defined, do NOT present a phase model as authoritative.

If a proposed framework is useful, label it:

```text
PROPOSED FRAMEWORK — NOT SOURCE-DEFINED
```

Any numerical boundaries must be:

```text
PROPOSED DEFAULT — VALIDATE LOCALLY
```

If unsupported, create a source gap.

Do not invent a fixed turn-count phase model and present it as EAIOC architecture.

---

# 24. MULTI-AGENT / SUB-AGENT COORDINATION

Cover:

### Parallel vs sequential

Use AL-004 independence rules.

### Shared-context minimization

A sub-agent receives only the context required by its `SubAgentSpawnRequest`.

Verify the live interface schema.

### Result aggregation

Use the authoritative `SubAgentAggregator` contract.

Do not redefine the interface.

### Budget allocation

Define proposed methodology for partitioning parent budget across concurrent sub-agents.

Integrate with SGE.

### Cancellation

Handle cancellation safely.

### Supersession

When parent execution is superseded, in-flight sub-agents must not continue as though the old execution remained authoritative.

Use SPM/XEC.

### Conflicting results

Define aggregation and verification behavior for inconsistent sub-agent findings.

### Partial failure

Define how the parent handles one or more failed sub-agents.

---

# 25. AGENT MEMORY OPTIMIZATION

Define concrete optimization for:

* short-term working memory
* durable project/task memory
* relevance
* freshness
* provenance
* compression
* retrieval
* invalidation
* poisoning
* versioning
* tenant isolation
* permission-aware access

Always preserve H09.

Memory optimization must never replace authoritative execution-state reconciliation.

Cross-reference:

```text
DA-010 — Agent Memory Optimizer
```

without creating a new DA ID.

---

# 26. DA-009 — SUB-AGENT RESULT COMPRESSOR

Supply the concrete algorithm behind:

```text
DA-009
```

It must implement the compressed handoff mechanism required by AL-005.

Cover:

* finding extraction
* evidence preservation
* confidence
* decision extraction
* unresolved questions
* provenance
* parent ingestion cost

Do not duplicate or redefine the underlying interface schema.

---

# 27. DA-013 — DEVELOPER-AGENT CONTEXT BUDGET MANAGER

Supply the concrete dynamic budget-allocation algorithm for coding/developer agents.

Cover context sections such as:

* user request
* repository state
* relevant files
* code search results
* dependencies
* build/test output
* tool definitions
* plan state
* unresolved work
* verification evidence

Respect:

* effective context budget
* context criticality
* repository freshness
* mandatory context
* security/governance
* optimization overhead

Do not blindly truncate repository or test context.

---

# 28. DA-014 — AGENT LOOP COST/TOKEN CONTROLLER

Define the running controller behind:

```text
DA-014
```

It must integrate:

* AL-001 progress
* AL-002 waste detection
* cumulative token cost
* tool cost
* latency
* expected value
* quality requirements
* budget

It must not turn into a simplistic iteration cap.

---

# 29. DA-018 — AGENT PLAN CONTEXT OPTIMIZER

Define how to retain active plan information such as:

* current objective
* active decisions
* milestones
* unresolved work
* dependencies
* relevant evidence

Discard/reduce only information whose loss is safe and recoverable.

Respect context criticality.

---

# 30. DA-020 — DEVELOPER-AGENT OUTPUT CONTROLLER

Define output control specifically for developer agents.

Cover:

* source code
* patches
* diffs
* explanations
* test output
* build output
* structured tool responses

Do not reduce output solely to meet a token target when required implementation detail or evidence would be lost.

---

# 31. DA-021 — DEVELOPER-AGENT TASK COMPLETION / EARLY EXIT

Define the coding-agent-specific implementation of AL-006.

Potential completion evidence includes:

* required files changed
* build succeeds
* tests pass
* required validation passes
* requested behavior achieved
* no unresolved mandatory work

Do not treat "code generated" as equivalent to "task complete."

---

# 32. CODING / DEVELOPER AGENTS — FIRST-CLASS REQUIREMENT

Explicitly cover:

* repository exploration
* code search
* file inspection
* dependency analysis
* build execution
* tests
* static analysis
* debugging
* logs
* patch generation
* patch validation
* refactoring
* multi-file changes
* Git/worktree operations
* CI/CD
* IDE/CLI tools
* long-running coding tasks
* large repositories
* repeated failures
* tool-heavy coding workflows
* coding sub-agents

Optimization must reconcile:

* repository version
* branch/worktree state
* file versions
* dependency state
* build state
* test state
* authorization
* external state
* side effects

Never blindly reuse a coding-agent result against changed repository state.

---

# 33. AGENTIC RAG OPTIMIZATION

Cover:

* retrieval planning
* query decomposition
* retrieval deduplication
* ranking
* compression
* caching
* freshness
* provenance
* citations
* context criticality
* retrieval budget
* stopping criteria
* multi-hop retrieval
* verification

Evidence must not be discarded solely because it consumes tokens.

---

# 34. CACHE INTEGRATION

Use `cache-strategy.md` as authoritative.

Respect the seven canonical cache types:

```text
EXACT
SEMANTIC
TOOL_RESULT
EMBEDDING
PROVIDER_NATIVE
APPLICATION
CONTEXT
```

Respect:

* tenant scope
* authorization
* policy
* context version
* workflow version
* dependency versions
* source freshness
* task intent
* invalidation
* poisoning
* provenance
* cache overhead
* negative optimization
* provider-native cache distinction

Do not redefine cache mechanics.

---

# 35. PROVIDER / MODEL OPTIMIZATION

Use `provider-matrix.md` as authoritative.

Distinguish:

```text
Discovered
Accessible
Available
Authorized
Recommended
Eligible
```

Do not select an inaccessible, unauthorized, unavailable, or capability-incompatible model/provider.

Agent-loop routing must account for:

* model capability
* context capacity
* tool support
* structured output
* reasoning requirements
* coding capability
* cost
* latency
* reliability
* tenant restrictions
* data governance
* FTR feasibility

---

# 36. 22 DYNAMIC / HARDENING COMPONENTS

Ensure the document explicitly accounts for all established components.

Dynamic execution:

```text
ESM
CVM
WVM
CPM
Reconciliation Engine
CIG
Context Expansion Controller
PRV
DPE
CAR
SRP
SPM
RCO
Provider abstraction
Agent integration layer
Optimization/cache components
```

Hardening/support:

```text
SGE
DGE
TMG
HAG
CIS
FTR
XEC
SPC
VCL
```

Do not silently introduce additional architecture components.

Where upstream documentation treats a component as supporting/hardening rather than one of the 22 core dynamic components, preserve that distinction.

---

# 37. SECURITY / GOVERNANCE PRECEDENCE

The hierarchy is:

```text
Security / Authorization / Policy / Governance
>
Correctness / Safety
>
Required Quality
>
Execution Integrity
>
Optimization
>
Cost / Token Reduction
```

Optimization must never:

* bypass authorization
* cross tenants
* use unauthorized tools
* use unauthorized models
* expose restricted context
* ignore policy
* remove mandatory evidence
* reuse stale authorized state
* violate deletion/erasure requirements
* propagate poisoned content
* bypass human approval
* disable required verification

---

# 38. NEGATIVE OPTIMIZATION

Explicitly support:

```text
DO_NOT_OPTIMIZE
```

when:

* optimization overhead exceeds expected benefit
* quality risk is unacceptable
* execution risk increases
* required context would be lost
* optimization violates policy
* authorization is insufficient
* data governance prohibits transformation/reuse
* capability is unavailable
* state is stale
* optimization makes recovery harder
* optimization introduces unsafe side effects
* optimization is economically unjustified

---

# 39. NET OPTIMIZATION VALUE

Do not optimize for token reduction alone.

Use a conceptual model:

```text
Net Optimization Value =
Verified Benefit
- Optimization Overhead
- Quality Risk Cost
- Execution Risk Cost
- Additional Latency Cost
- Operational Cost
```

Do not invent numerical weights unless authoritative sources define them.

---

# 40. VERIFICATION

Every material optimization should have an appropriate verification mechanism.

Cover:

* precondition verification
* postcondition verification
* output verification
* tool-result verification
* repository-state verification
* context integrity
* authorization
* policy
* quality
* cache validity
* provider/model suitability

---

# 41. RECOVERY AND CHECKPOINTS

Preserve:

> **Resume != replay**

When resuming, reconcile checkpoint state with current:

* user intent
* context
* workflow
* permissions
* policy
* provider/model
* tools
* external state
* repository state
* completed work
* remaining work
* side effects
* memory
* cache

Possible actions:

```text
Resume
Recompute
Retrieve
Expand context
Reduce context
Switch model/provider
Invalidate cache
Restore checkpoint
Rollback
Restart
Pause
Ask user
Abort safely
```

Non-idempotent operations must not be blindly replayed.

---

# 42. SUPERSESSION

Cover:

* user-request changes
* execution replacement
* workflow-version changes
* permission revocation
* policy changes
* external-state changes

A superseded execution and its sub-agents must not continue unwanted side effects.

Use SPM/XEC where applicable.

---

# 43. LONG-RUNNING AGENTS

Cover:

* checkpoint frequency
* incremental checkpoints
* state compaction
* memory growth
* context growth
* budget exhaustion
* provider failure
* tool failure
* partial completion
* cancellation
* recovery
* supersession

Checkpointing itself has overhead and must be economically justified.

---

# 44. OBSERVABILITY AND EXPLAINABILITY

Define relevant metrics including:

* token usage
* tool cost
* inference cost
* optimization savings
* optimization overhead
* loop iterations
* objective progress
* information gain
* replanning frequency
* sub-agent count
* fan-out
* handoffs
* aggregation cost
* context size
* context reduction
* cache hits/misses
* retrieval calls
* provider/model changes
* verification failures
* early exits
* rollbacks
* recovery events
* supersession events
* `DO_NOT_OPTIMIZE`

Define what must be explainable about every optimization decision.

Do not prematurely define the full observability architecture; that belongs to `observability.md`.

---

# 45. OPTIMIZATION DECISION RECORD

Define the conceptual information needed to explain a decision:

```text
Execution ID
Tenant
Agent ID
Agent Type
Task
State / Version
Candidate Optimization
Preconditions
Authorization Result
Policy Result
Governance Result
Feasibility Result
Expected Benefit
Optimization Overhead
Quality Risk
Execution Risk
Selected Action
Rejected Alternatives
Verification Result
Observed Benefit
Rollback / Recovery
Provenance
```

Do not silently add fields to `interfaces.md`.

If not interface-defined, label this as a design-level concept.

---

# 46. OPTIMIZATION COMPOSITION

Cover:

* compatible optimizations
* conflicts
* ordering
* dependencies
* preconditions
* rollback
* partial application
* composition overhead

Do not assume individually beneficial optimizations remain beneficial when composed.

---

# 47. CANONICAL ENTRY SCHEMA

Every existing:

```text
AL-001 through AL-006
AR-001 through AR-004
DA-009
DA-010
DA-013
DA-014
DA-018
DA-020
DA-021
```

must have a complete entry.

Do not invent new `AL-*`, `AR-*`, or `DA-*` IDs.

Use:

```text
N/A — not applicable to this entry.
```

where appropriate.

Do not leave required fields blank.

Use:

```text
### <ID> — <Exact Name>

**Owning Architecture Section:** ...
**Concrete Algorithm:** ...
**Formula/Estimation Method:** ...
**Proposed Defaults:** ...
**Agent Memory Authority (H09):** ...
**Session-Phase Interaction:** ...
**Multi-Agent Interaction:** ...
**Anti-Pattern Check:** ...
**Fallback:** ...
**Observability Metrics:** ...
**Validated By:** ...
**Traceability:**
- Problem Statement: ...
- Engineering Specification: ...
- Architecture: ...
- Interfaces: ...
- Conventions: ...
- Edge Cases: ...
- Optimization Catalog: ...
```

Any source-unsupported methodology must be clearly tagged.

---

# 48. ANTI-PATTERNS

At minimum cover:

* sub-agent spawning without value/cost analysis
* tool execution without expected information gain
* re-running completed steps
* full conversation replay
* iteration-count-only waste detection
* blind context truncation
* blind summarization
* blind memory reuse
* blind cache reuse
* ignoring authorization
* ignoring tenant isolation
* ignoring policy changes
* inaccessible-provider selection
* treating memory as execution truth
* resume-as-replay
* replaying non-idempotent operations
* unlimited fan-out
* unbounded replanning
* optimizing every request
* optimizing without verification
* optimizing stale state
* optimizing malicious content
* bypassing human approval
* treating provider-native caching as EAIOC-owned caching

---

# 49. SCENARIO / EDGE-CASE TRACEABILITY

Use the live `scenario-matrix.md`.

Do not force every scenario into this document.

Identify scenarios directly relevant to:

* agent loop
* sub-agents
* coding agents
* multi-agent coordination
* memory
* tools/MCP
* recovery
* supersession
* provider/model selection
* cache
* security/governance

Use real IDs only after verifying they exist.

Do not write generic statements such as:

```text
Validated by Agent Domain
```

when a real scenario ID can be cited.

---

# 50. INTERFACE TRACEABILITY

Verify and reference the actual live interfaces for:

* `AgentIterationReport`
* `SubAgentSpawnRequest`
* `SubAgentResultHandoff`
* `SubAgentFinding`
* `SubAgentAggregator`
* `AgentEarlyExitEvaluator`
* related agent/tool interfaces

Do not invent field names.

Do not redefine interface contracts.

If a required concept is absent from interfaces:

```text
SOURCE-GAP
```

and document the design requirement without modifying `interfaces.md`.

---

# 51. OPTIMIZATION CATALOG TRACEABILITY

Use existing catalog entries.

Relevant known entries include:

```text
TECH-013
TECH-014
TECH-015
TECH-018

DA-009
DA-010
DA-013
DA-014
DA-018
DA-020
DA-021
```

Verify current definitions before citing.

Do not create duplicate catalog entries.

Do not modify `optimization-catalog.md`.

---

# 52. CROSS-DOCUMENT COVERAGE

Include:

```text
## Cross-Document Coverage Matrix
```

Trace relevant requirements across:

* Problem Statement
* Engineering Specification
* Architecture
* Interfaces
* Conventions
* Edge Cases
* Scenario Matrix
* Optimization Catalog
* Provider Matrix
* Cache Strategy

Include the relevant:

* OBJ IDs
* AC IDs
* H IDs
* AL IDs
* AR IDs
* DA IDs
* EC IDs
* SCN IDs
* INTF IDs

Only cite IDs verified in the live sources.

---

# 53. SOURCE GAPS

Include:

```text
## SOURCE GAPS DISCOVERED
```

Carry forward existing source-gap IDs unchanged.

New gaps must use:

```text
SOURCE-GAP-AGENTOPT-NN
```

For each:

```text
Gap ID
Description
Source
Impact
Current Treatment
Blocking / Non-blocking
Required Future Resolution
```

The session-phase concept should be recorded as a source gap if no authoritative source defines it.

---

# 54. SOURCE CONTRADICTIONS

Include:

```text
## SOURCE CONTRADICTIONS
```

Carry forward relevant existing contradiction records.

For new contradictions include:

```text
Contradiction ID
Documents
Conflicting statements
Authority determination
Current treatment
Status
```

Do not silently resolve contradictions.

---

# 55. SCOPE BOUNDARY

Explicitly state:

| Content                             | Owner                                                |
| ----------------------------------- | ---------------------------------------------------- |
| TECH/DA identity and catalog schema | `optimization-catalog.md`                            |
| Provider/model facts                | `provider-matrix.md`                                 |
| Cache mechanics                     | `cache-strategy.md`                                  |
| Agent-loop algorithms               | `agent-optimization.md`                              |
| Multi-agent coordination            | `agent-optimization.md`                              |
| Session-phase policy                | `agent-optimization.md` if source-supported/proposed |
| Inference infrastructure            | `inference-optimization.md`                          |
| Quality-gate methodology            | `quality-gates.md`                                   |
| Security implementation             | `security.md`                                        |
| Observability architecture          | `observability.md`                                   |
| Evaluation framework                | `eval.md`                                            |
| Scaling                             | `SCALING.md`                                         |
| Implementation sequencing           | `implementation-plan.md`                             |
| Requirements traceability           | `requirements-traceability.md`                       |
| Architectural decisions             | ADRs                                                 |

Do not leak downstream implementation into this document.

---

# 56. REQUIRED TEST STRATEGY

Cover:

* unit tests
* contract tests
* integration tests
* state-transition tests
* authorization tests
* policy tests
* data-governance tests
* tool/MCP trust tests
* cache tests
* memory tests
* multi-agent tests
* coding-agent tests
* provider/model routing tests
* recovery tests
* supersession tests
* performance tests
* cost tests
* quality/evaluation tests
* security tests
* chaos/resilience tests

Do not claim execution of tests unless actually performed.

---

# 57. REQUIRED MATRICES

Where source evidence supports them, include:

### Agent Optimization Matrix

| Optimization | Preconditions | Benefit | Overhead | Quality Risk | State Dependencies | Fallback |

### Agent Type Matrix

| Agent Type | Optimization Opportunities | Risks | Dependencies |

Cover:

* generic agents
* autonomous agents
* tool-using agents
* coding/developer agents
* RAG agents
* multi-agent systems
* sub-agents

Also consider:

* Tool Optimization Matrix
* Memory Optimization Matrix
* Multi-Agent Coordination Matrix
* Recovery Matrix
* Provider/Model Matrix
* Cache Integration Matrix
* Security/Governance Matrix
* Observability Matrix
* Scenario/Edge-Case Traceability Matrix

Do not fabricate values.

---

# 58. PSEUDOCODE

Include implementation-oriented pseudocode for:

1. Agent optimization decision
2. Progress measurement
3. Waste detection
4. Sub-agent ROI
5. Sub-agent scheduling
6. Handoff compression
7. Early exit
8. Model escalation
9. Reasoning budget control
10. Output budget forecasting
11. Tool optimization
12. Memory optimization
13. Multi-agent coordination
14. Cache-assisted optimization
15. Provider/model resolution
16. Verification
17. Recovery
18. Supersession

Preserve all required authorization, governance, state reconciliation, and feasibility gates.

---

# 59. VALIDATION PASS

Before completion, independently verify:

### Source

* All ten upstream documents read.
* Authority order respected.
* No source rewritten.
* No requirement silently removed.

### Agent algorithms

* `AL-001` through `AL-006`: all present.
* `AR-001` through `AR-004`: all present.
* Seven specified DA modules: all present.
* No new AL/AR/DA IDs invented.

### State

* H09 correctly applied.
* H10 correctly applied.
* Reconciliation preserved.
* Context/workflow versioning preserved.
* Stale state handled.
* Supersession handled.
* Recovery handled.

### Agent types

* Autonomous
* Tool-using
* Coding/developer
* RAG
* Multi-agent
* Sub-agent
* MCP

### Optimization

* Planning
* Replanning
* Tools
* Context
* Memory
* Retrieval
* Delegation
* Multi-agent
* Cache
* Provider/model
* Recovery
* Verification
* Negative optimization

### Governance

* Authorization
* Policy
* Tenant isolation
* Data governance
* Content integrity
* Tool/MCP trust
* Human approval
* Spend governance

### Traceability

* Interfaces
* Edge cases
* Scenarios
* Optimization catalog
* Provider matrix
* Cache strategy

### Integrity

* No invented APIs
* No invented IDs
* No unsupported formulas presented as facts
* No unsupported thresholds presented as facts
* No fabricated scenario coverage
* No downstream document created
* No other source modified

---

# 60. FINAL REPORT

Include:

```text
## Final Report
```

Report at minimum:

```text
Document
Status
Authoritative Inputs
AL Entry Count
AR Entry Count
DA Entry Count
Agent Types Covered
Optimization Domains Covered
Dynamic/Hardened Components Addressed
Scenario Coverage
Edge-Case Coverage
Interface Traceability
Optimization Catalog Traceability
Provider Matrix Integration
Cache Strategy Integration
Security/Governance Coverage
Recovery/Supersession Coverage
Coding-Agent Coverage
Multi-Agent/Sub-Agent Coverage
MCP Coverage
Proposed Methodologies
Proposed Defaults
Source Gaps
Contradictions
Out-of-Scope Items
Validation Result
```

All counts must be derived from the actual generated document and live source files.

---

# 61. READINESS VERDICT

End the document with:

```text
## Agent Optimization Readiness
```

Then use exactly ONE of:

```text
READY FOR NEXT DOCUMENTATION PHASE
```

or:

```text
NOT READY — AGENT OPTIMIZATION GAPS MUST BE RESOLVED
```

The readiness verdict must be the **absolute final line of the document**.

Do not place:

* appendix
* references
* notes
* changelog
* content
* whitespace-dependent material

after the verdict.

---

# 62. FAILURE MODES TO DEFEND AGAINST

Explicitly validate against:

1. Agent memory treated as execution truth.
2. Loop cap presented as authoritative.
3. ROI threshold presented as authoritative without evidence.
4. Repeated iterations classified as waste solely by count.
5. Full transcript replay to sub-agent.
6. Handoff schema invented or altered.
7. Session phase invented as authoritative.
8. Reasoning budget reduced solely for token savings.
9. Interfaces redefined.
10. VCL/SGE/SPM/XEC mechanisms redefined.
11. Security bypassed for optimization.
12. Tenant isolation bypassed.
13. Inaccessible model/provider selected.
14. Stale state optimized.
15. Non-idempotent work replayed.
16. Unlimited sub-agent fan-out.
17. Optimization overhead ignored.
18. Coding-agent repository state ignored.
19. Cache reused without validation.
20. Downstream documents modified.
21. New catalog IDs invented.
22. Final readiness verdict not last.

---

# 63. EXECUTION ORDER

Perform the task in this order:

1. Check whether `docs/agent-optimization.md` exists.
2. Read it if present.
3. Read all ten authoritative documents.
4. Re-derive current baseline counts from live final reports.
5. Extract `AL-001`–`AL-006`.
6. Extract `AR-001`–`AR-004`.
7. Verify all relevant DA IDs.
8. Verify all agent interfaces.
9. Verify H09 and H10.
10. Verify scenario and edge-case IDs.
11. Determine session-phase source status.
12. Draft the agent execution model.
13. Draft AL algorithms.
14. Draft AR algorithms.
15. Draft tool/MCP integration.
16. Draft memory optimization.
17. Draft coding-agent optimization.
18. Draft multi-agent/sub-agent coordination.
19. Draft DA algorithms.
20. Integrate cache/provider/security/governance constraints.
21. Draft recovery/supersession.
22. Populate traceability.
23. Populate source gaps/contradictions.
24. Populate scope boundary.
25. Populate tests/matrices/pseudocode.
26. Run the full validation pass.
27. Generate Final Report.
28. Generate exactly one readiness verdict.
29. Ensure the verdict is the final line.
30. Confirm that only `docs/agent-optimization.md` changed.

---

# 64. FINAL DONE CONDITION

The task is complete only when:

* all 10 AL/AR components have complete entries,
* all 7 specified DA modules have complete entries,
* H09 is explicit and consistently enforced,
* H10 is correctly implemented,
* SubAgentResultHandoff is verified against the live interface,
* full transcript replay is prohibited,
* session-phase source status is honestly represented,
* coding/developer agents are first-class,
* autonomous/tool/MCP agents are covered,
* multi-agent/sub-agent coordination is covered,
* cache strategy is integrated,
* provider matrix is integrated,
* security/governance takes precedence,
* negative optimization is supported,
* recovery and supersession are covered,
* all important claims are source-grounded,
* proposed values are clearly labeled,
* no new IDs were invented,
* no source documents were modified,
* no downstream documents were created,
* `requirements-traceability.md` is acknowledged as a future document but not generated,
* validation passes,
* and the single readiness verdict is the final line.

---

# 65. FINAL INSTRUCTION

Create **ONLY**:

```text
docs/agent-optimization.md
```

Read all ten authoritative sources first.

Use the live documents as the source of truth.

Preserve all existing valid EAIOC architecture, terminology, interfaces, scenarios, edge cases, optimization definitions, provider rules, and cache rules.

Treat this document as the canonical implementation reference for the **agent-loop and multi-agent optimization layer**, especially the concrete algorithms deliberately deferred by `optimization-catalog.md`.

Treat:

* autonomous agents,
* tool-using agents,
* MCP agents,
* coding/developer agents,
* RAG agents,
* multi-agent systems,
* sub-agents

as first-class workloads.

Security, authorization, policy, data governance, correctness, and execution integrity always take precedence over optimization.

Do not invent unsupported facts, thresholds, APIs, IDs, schemas, or scenario coverage.

Do not silently resolve source gaps or contradictions.

Do not modify any other document.

Perform the complete validation pass before declaring readiness.

The readiness verdict must be the final line of the file.
