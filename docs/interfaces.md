# Interfaces Specification
# Enterprise Agent & LLM Inference Optimization Control Plane

---

**Document ID:** EAIOC-INTF-001
**Status:** PRE-IMPLEMENTATION — Pending stakeholder approval
**Source (authoritative):** `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt` (as hardened 2026-09-15, PS §52; reconciled 2026-09-16, PS §51.13)
**Engineering Spec cross-reference:** `Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md` (EAIOC-SPEC-001 Rev 1.4)
**Architecture cross-reference:** `architecture.md` (EAIOC-ARCH-001 Rev 1.3)
**Date:** 2026-09-08
**Version:** 1.2.0
**Amendment history:**
- 1.0.0 (2026-09-08) — Baseline: INTF-001–049 across §1–41, drawn directly from the Problem Statement and an early draft of `architecture.md`.
- 1.1.0 (2026-09-10) — §42 added: Dynamic Execution Interfaces (INTF-050–062), propagating ARCH §46 / SPEC §41 / PS §51.
- 1.2.0 (2026-09-16) — §43 added: Hardening Interfaces (INTF-063–071), propagating ARCH §47 / SPEC §42 / PS §52 (H01–H20, OBJ-023–035, SEC-011–016, NFR-014, AC-039–053). TOC corrected (§42 was missing from the Table of Contents; §43 added). §41's "Recommended Next Documentation Artifact" corrected — it previously recommended generating the Engineering Specification, which already exists as EAIOC-SPEC-001 and precedes this document in the authoritative chain (PS → Engineering Spec → Architecture → Interfaces); it now correctly points to `conventions.md`.

---

> **IMPORTANT:** This document defines **contracts and integration boundaries** between every major component of the Enterprise Agent & LLM Inference Optimization Control Plane. It does **not** contain implementation code.

> **CAUTION:** No interface defined here may introduce provider-specific assumptions into the core system. All provider differences must be encapsulated in adapter layers.

---

## Table of Contents

1. Interface Design Principles
2. External Entrypoint Interface
3. Optimization Decision Interface
4. Optimization Module Contract
5. Context Engine Interfaces
6. Cache Interfaces
7. LLM Provider Interface
8. Model Profile Interface
9. Model Routing Interface
10. Tool Interfaces
11. Agent Interface
12. Sub-Agent Interface
13. Developer / Coding Agent Interface
14. MCP / Tool Ecosystem Interface
15. RAG Interface
16. Memory Interface
17. Quality Validation Interface
18. Evaluation Interface
19. Observability Interface
20. Policy / Governance Interface
21. Security Interfaces
22. Fallback / Recovery Interface
23. Event Interface
24. Versioning and Compatibility
25. Failure Semantics
26. Idempotency / Retry / Cancellation
27. Tenancy and Isolation
28. Cost Accounting Interface
29. Decision Explanation Interface
30. Contract Testing
31. Non-Functional Interface Requirements
32. Reference Data Flows
33. Interface Anti-Patterns
34. Traceability
35. Implementation Boundary
36. Interface Inventory
37. Cross-Reference Matrix
38. Open Architectural Questions
39. Assumptions
40. Dependencies
41. Recommended Next Documentation Artifact
42. Dynamic Execution Interfaces
43. Hardening Interfaces — Operating Model, Governance, and Trust Boundaries (2026-09-15)


---

## 1. Interface Design Principles

*Traceability: PS §1, §24 (NFR-001–013); ARCH §1, §29*

### 1.1 Provider Neutrality

Every interface in the core system **must not** contain provider-specific logic or fields. Provider differences are encapsulated exclusively in adapter implementations.

**Rule:** If a field only applies to one provider, it belongs in a `provider_hints` opaque map, not in the core schema.

### 1.2 Agent / Framework Neutrality

The Control Plane must accept requests from any agent framework: LangChain, AutoGen, CrewAI, custom frameworks, Cursor, Claude Code, GitHub Copilot, Antigravity, and any future agent.

### 1.3 Versioned Contracts

Every interface carries a `schema_version` field. Breaking changes require a version increment. Format: `MAJOR.MINOR.PATCH`.

### 1.4 Backward Compatibility

A consumer compiled against `v1.x` must continue to work with any `v1.y` producer where `y >= x`. Additive fields are never breaking. Removed or renamed fields are always breaking.

### 1.5 Idempotency

Optimization decisions and cache writes must be idempotent with respect to the same `request_id`.

- `request_id`: top-level requests
- `task_id`: sub-tasks
- `tool_call_id`: tool invocations

### 1.6 Determinism Where Required

Policy-driven optimization must produce the same decision for the same input and the same policy version. Stochastic decisions must record their seed or similarity score for audit.

### 1.7 Fail-Open / Fail-Safe Behavior

Any optimization stage that fails **must not** block request completion. Every stage has an explicit fallback (see §22). The Control Plane is an accelerator, not a gatekeeper.

**Exception:** Security-critical optimizations (e.g., PII scrubbing required by policy) may be fail-closed. This must be explicitly configured.

### 1.8 Timeout and Cancellation

Every interface that performs I/O must support:
- `timeout_ms`: maximum time before abort
- `cancellation_token`: cooperative cancellation signal

### 1.9 Retry Semantics

Retries are permitted only for idempotent operations. Non-idempotent operations must not be retried automatically without explicit idempotency confirmation. Retry policy includes: `max_attempts`, `initial_delay_ms`, `backoff_multiplier`, `max_delay_ms`, `retryable_error_codes`.

### 1.10 Correlation IDs

Every interface request and response carries:
- `request_id`: globally unique identifier
- `correlation_id`: externally supplied trace anchor
- `span_id`: current span within the request
- `parent_span_id`: parent span for distributed tracing

### 1.11 Trace Propagation

The Control Plane propagates W3C Trace Context (`traceparent`, `tracestate`) across all internal and external calls.

### 1.12 Tenant Isolation

Every interface schema includes `tenant_id` and `organization_id`. No interface result may be shared across tenant boundaries.

### 1.13 Security Boundaries

Interfaces do not transmit raw credentials. Credentials are referenced by secret handles. PII is classified at ingress and must not cross security boundaries without explicit policy authorization.

### 1.14 Policy Enforcement

Every interface that produces a decision must identify the policy version that governed it. Policy changes must be versioned and auditable.

### 1.15 Observability Requirements

Every interface call must produce: a structured log entry, a metric count, a latency measurement, and an event emission. No interface may fail silently.


---

## 2. External Entrypoint Interface

*Traceability: PS §2, §5, §32; ARCH §5, §8*

The `ControlPlaneRequest` is the canonical normalized representation of any request entering the Control Plane.

### 2.1 ControlPlaneRequest Schema

```
ControlPlaneRequest {

  // --- IDENTITY (all REQUIRED) ---
  schema_version:    string        // e.g., "1.0.0"
  request_id:        string        // UUID v4; globally unique; idempotency key
  correlation_id:    string        // External trace anchor
  tenant_id:         string        // REQUIRED; tenant isolation boundary
  organization_id:   string        // REQUIRED; organizational scope

  // --- ROUTING SCOPE ---
  application_id:    string
  agent_id:          string | null
  session_id:        string | null
  workflow_id:       string | null
  task_id:           string        // REQUIRED
  parent_task_id:    string | null
  iteration_number:  integer       // 0 for first call

  // --- REQUEST CLASSIFICATION (REQUIRED) ---
  request_type:      enum {
                       GENERIC_LLM, AGENTIC_WORKFLOW, DEVELOPER_CODING,
                       MULTI_AGENT, RAG, TOOL_ENABLED, BATCH, SUB_AGENT
                     }

  // --- CONTENT ---
  user_input:           string | null
  system_context:       InstructionContext | null
  developer_context:    InstructionContext | null
  conversation_history: ConversationContext | null
  retrieved_context:    RetrievalContext | null
  memory_references:    MemoryReference[] | null
  available_tools:      ToolContext | null
  agent_state:          AgentState | null

  // --- REQUIREMENTS ---
  model_preferences:    ModelPreferences | null
  quality_requirements: QualityRequirements       // REQUIRED
  latency_requirements: LatencyRequirements       // REQUIRED
  budget_constraints:   BudgetConstraints | null

  // --- GOVERNANCE ---
  security_classification:  SecurityClassification  // REQUIRED
  compliance_requirements:  string[]
  freshness_requirements:   FreshnessRequirements | null

  // --- OPTIMIZATION HINTS ---
  optimization_hints:   OptimizationHints | null
  bypass_optimization:  boolean                   // Default false

  // --- METADATA ---
  metadata:   map<string, string>
  timestamp:  ISO8601 string
  ttl_ms:     integer | null
}
```

### 2.2 Sub-Type Schemas

```
InstructionContext {
  content:        string
  type:           enum { SYSTEM, DEVELOPER, USER }
  stability:      enum { STABLE, SEMI_STABLE, VOLATILE }
  token_estimate: integer
  version:        string | null
}

ConversationContext {
  turns:          ConversationTurn[]
  total_turns:    integer
  token_estimate: integer
  oldest_turn_at: ISO8601 string | null
}

ConversationTurn {
  turn_id:     string
  role:        enum { USER, ASSISTANT, TOOL, SYSTEM }
  content:     string
  timestamp:   ISO8601 string
  token_count: integer | null
  tool_calls:  ToolCall[] | null
}

ContextSource {
  source_id: string
  type:      enum { DOCUMENT, CODE_FILE, DATABASE, WEB, TOOL_RESULT, MEMORY, SUB_AGENT }
  uri:       string | null
  version:   string | null
  commit:    string | null
  branch:    string | null
  timestamp: ISO8601 string | null
}

QualityRequirements {
  min_quality_score:   float           // 0.0-1.0
  quality_tier:        enum { DRAFT, STANDARD, HIGH, CRITICAL }
  allow_degraded:      boolean
  validation_required: boolean
}

LatencyRequirements {
  max_e2e_latency_ms: integer | null
  max_ttft_ms:        integer | null
  interactive:        boolean
}

BudgetConstraints {
  max_input_tokens:     integer | null
  max_output_tokens:    integer | null
  max_cost_usd:         float | null
  max_reasoning_tokens: integer | null
}

SecurityClassification {
  level:          enum { PUBLIC, INTERNAL, CONFIDENTIAL, RESTRICTED }
  contains_pii:   boolean | null
  pii_categories: string[] | null
  data_residency: string[] | null
}

FreshnessRequirements {
  max_age_seconds:  integer | null
  require_live_data: boolean
  staleness_action:  enum { REJECT, WARN, ACCEPT }
}
```

### 2.3 Field Classification Table

| Field | Classification | Notes |
|---|---|---|
| `request_id` | REQUIRED | Idempotency key |
| `tenant_id` | REQUIRED | Isolation boundary |
| `organization_id` | REQUIRED | Policy scope |
| `task_id` | REQUIRED | Unique per task |
| `request_type` | REQUIRED | Drives optimization path |
| `quality_requirements` | REQUIRED | Never omitted |
| `latency_requirements` | REQUIRED | Drives interactive vs. batch |
| `security_classification` | REQUIRED | Governs cache safety |
| `user_input` | REQUIRED (non-batch) | SENSITIVE if contains PII |
| `agent_id` | OPTIONAL | Null for non-agentic |
| `session_id` | OPTIONAL | Null for stateless |
| `parent_task_id` | OPTIONAL | Set for sub-agents |
| `conversation_history` | OPTIONAL | SENSITIVE |
| `available_tools` | OPTIONAL | Present for tool-enabled agents |
| `model_preferences` | OPTIONAL | Caller hints only |
| `budget_constraints` | OPTIONAL | Defaults from policy |
| `metadata` | OPTIONAL | Opaque extension |
| `optimization_hints` | OPTIONAL | Best-effort; not binding |


---

## 3. Optimization Decision Interface

*Traceability: PS §33 (OI-001–005); ARCH §14*

### 3.1 OptimizationDecisionRequest

```
OptimizationDecisionRequest {
  schema_version:    string
  request_id:        string
  request:           ControlPlaneRequest
  complexity_result: TaskComplexityResult | null
  available_stages:  string[]
  policy:            OptimizationPolicy
  provider_profiles: ModelProfile[]
  timestamp:         ISO8601 string
}
```

### 3.2 OptimizationPlan (Decision Output)

```
OptimizationPlan {
  schema_version: string
  request_id:     string
  plan_id:        string

  // EXECUTION PLAN
  selected_stages:    OptimizationStage[]
  skipped_stages:     SkippedStage[]
  optimization_depth: enum { MINIMAL, STANDARD, DEEP }

  // DECISIONS
  serve_from_exact_cache:    boolean
  serve_from_semantic_cache: boolean
  execute_retrieval:         boolean
  execute_compression:       boolean
  compression_depth:         enum { NONE, LIGHT, MODERATE, AGGRESSIVE } | null
  execute_deduplication:     boolean
  execute_pruning:           boolean
  execute_reordering:        boolean
  tool_selection_mode:       enum { ALL, SELECTIVE, NONE }
  allow_sub_agent_creation:  boolean
  model_routing_required:    boolean
  batch_eligible:            boolean

  // BUDGETS
  context_token_budget:   integer
  output_token_budget:    integer
  reasoning_budget:       enum { LOW, MEDIUM, HIGH, MAX }
  optimizer_cost_budget:  float | null

  // ECONOMICS
  expected_inference_cost_avoided: float
  expected_optimization_cost:      float
  expected_net_savings:            float
  expected_quality_impact:         float
  expected_latency_delta_ms:       integer
  optimization_confidence:         float

  // FALLBACK
  fallback_strategy: FallbackStrategy
  escalation_policy: EscalationPolicy

  // GOVERNANCE
  policy_version:              string
  policy_constraints_applied:  string[]
  decision_rationale:          DecisionRationale

  // TRACING
  span_id:   string
  timestamp: ISO8601 string
}

OptimizationStage {
  stage_id:          string
  stage_version:     string
  execution_order:   integer
  required:          boolean
  timeout_ms:        integer
  fallback_on_failure: FallbackAction
  config_overrides:  map<string, any>
}

SkippedStage {
  stage_id:          string
  reason:            enum {
                       COST_EXCEEDS_BENEFIT, POLICY_DISABLED, NOT_APPLICABLE,
                       PREREQUISITE_NOT_MET, CACHE_HIT, LATENCY_CONSTRAINT,
                       QUALITY_RISK_TOO_HIGH
                     }
  expected_savings:  float
  optimization_cost: float
  net_value:         float
  explanation:       string
}

DecisionRationale {
  summary:            string
  key_signals:        map<string, any>
  policy_references:  string[]
  model_references:   string[]
  history_references: string[]
}
```

### 3.3 Decision Types Supported

| Decision | When Applied |
|---|---|
| `serve_from_exact_cache` | Request matches exact cache entry; all checks pass |
| `serve_from_semantic_cache` | Semantically similar; freshness + auth checks pass |
| `execute_retrieval` | Retrieved context present or needed |
| `execute_compression` | Context exceeds budget after pruning |
| `execute_deduplication` | Multiple sources with potential overlap |
| `allow_sub_agent_creation` | Sub-agent expected value > cost |
| `model_routing_required` | Default model is not optimal for this task |
| `batch_eligible` | Non-interactive; can be deferred for batch pricing |
| `reasoning_budget` | Controls depth of model reasoning |
| `output_token_budget` | Caps generated output |
| `tool_selection_mode` | SELECTIVE = dynamic tool loading; NONE = no tools |

### 3.4 Hardening Extension (2026-09-15): Operating Mode and Decision Ownership

*Traceability: ARCH §47.5, §47.6; SPEC §42.1, §42.2; PS §52.1, §52.2; OBJ-023, OBJ-024; AC-039, AC-040*

`OptimizationPlan` (§3.2) and every per-stage decision it carries are extended with two additional fields, defined once here and reused by every interface in §43:

```
// Added to OptimizationPlan (§3.2), and to OptimizationStage / SkippedStage:
operating_mode:      OperatingMode          // See §43.10 for the shared enum definition
decision_ownership:  DecisionOwnership      // See §43.10 for the shared enum definition
```

A caller reading `OptimizationPlan.operating_mode` and `.decision_ownership` (or the per-stage equivalents on `OptimizationStage`) can always determine, for any decision, whether it was computed synchronously/asynchronously/hybrid and whether the Control Plane is advising, enforcing, or directly owning the resulting action — without inferring it from stage identity. `SkippedStage.reason` additionally accepts `DO_NOT_OPTIMIZE` (net-negative expected value; see §43's Verified Net Optimization Economics types) alongside the existing enum values — no existing value is removed or renumbered.

---

## 4. Optimization Module Contract

*Traceability: PS §7, §14; ARCH §23, §24*

Every optimization module (T0.x, T1.x, T2.x, T3.x, OI.x, CL.x, CE.x, TE.x, AL.x, AR.x, DA.x) must implement the following contract.

### 4.1 OptimizationModule Interface

```
interface OptimizationModule {
  module_id:      string
  module_version: string
  display_name:   string
  capability:     ModuleCapability
  schema_version: string

  applicability(context: ModuleContext) -> ApplicabilityResult
  estimate(context: ModuleContext)      -> ModuleEstimate
  optimize(context: ModuleContext)      -> OptimizationResult
  validate(original: ModuleContext, result: OptimizationResult) -> ValidationResult
  rollback(result: OptimizationResult) -> RollbackResult
  explain(result: OptimizationResult)  -> ExplanationRecord
  measure(result: OptimizationResult)  -> ModuleMeasurement
  health_check()                        -> HealthStatus
}
```

### 4.2 Supporting Types

```
ModuleCapability {
  type:                    enum { PRUNING, COMPRESSION, DEDUPLICATION, CACHING,
                                  ROUTING, RETRIEVAL, REORDERING, TOOL_OPTIMIZATION,
                                  AGENT_CONTROL, MEMORY, VALIDATION, EVALUATION }
  supported_request_types: RequestType[]
  requires_llm_call:       boolean
  is_reversible:           boolean
  is_deterministic:        boolean
  max_latency_ms:          integer
}

ApplicabilityResult {
  applicable:            boolean
  confidence:            float
  reason:                string
  prerequisites_met:     boolean
  missing_prerequisites: string[]
}

ModuleEstimate {
  applicable:              boolean
  expected_tokens_avoided: integer
  expected_cost_avoided:   float
  optimization_cost:       float
  expected_net_savings:    float
  expected_quality_impact: float
  expected_latency_ms:     integer
  confidence:              float
}

OptimizationResult {
  module_id:            string
  module_version:       string
  request_id:           string
  span_id:              string
  success:              boolean
  fallback_triggered:   boolean
  fallback_reason:      string | null
  output:               any
  measurement:          ModuleMeasurement
  reversibility_record: ReversibilityRecord | null
  timestamp:            ISO8601 string
}

ModuleMeasurement {
  input_tokens:                 integer
  output_tokens:                integer
  tokens_avoided:               integer
  optimization_overhead_tokens: integer
  latency_overhead_ms:          integer
  estimated_cost:               float
  actual_cost:                  float | null
  quality_impact:               float | null
  errors:                       ModuleError[]
  fallback_triggered:           boolean
}

ReversibilityRecord {
  original_content_ref: string
  transformation_type:  string
  removed_ranges:       ContentRange[]
  reason:               string
  confidence:           float
  recovery_pointer:     string
  expires_at:           ISO8601 string | null
}

ContentRange {
  start_token:  integer
  end_token:    integer
  content_hash: string
}
```

### 4.3 Module Lifecycle

```
DISCOVER  -> Module self-registers; declares capability, prerequisites, version
EVALUATE  -> applicability() + estimate() determine if module should run
DECIDE    -> OI-001 decides whether to include in OptimizationPlan
EXECUTE   -> optimize() runs against ModuleContext
VALIDATE  -> validate() runs quality gate
ACCEPT    -> measurement recorded; result passed to next stage
  OR
REJECT    -> rollback() called; fallback representation used
MEASURE   -> measure() records token/cost/quality delta
LEARN     -> Measurements feed EL-004 Continuous Policy Learning
```

### 4.4 Mandatory Module Reporting

| Field | Requirement |
|---|---|
| `input_tokens` | REQUIRED |
| `output_tokens` | REQUIRED |
| `tokens_avoided` | REQUIRED (0 if none) |
| `optimization_overhead_tokens` | REQUIRED |
| `latency_overhead_ms` | REQUIRED |
| `estimated_cost` | REQUIRED |
| `actual_cost` | REQUIRED when known; null otherwise |
| `quality_impact` | REQUIRED after validate() |
| `errors` | REQUIRED (empty array if none) |
| `fallback_triggered` | REQUIRED |


---

## 5. Context Engine Interfaces

*Traceability: PS §6.9–6.16, §34 (CL-001–007); ARCH §11, §15*

### 5.1 ContextItem — Universal Context Atom

```
ContextItem {
  item_id:             string
  content:             string
  content_type:        enum { TEXT, CODE, MARKDOWN, JSON, DIFF, LOG, TEST_OUTPUT,
                              TOOL_RESULT, MEMORY, SEARCH_RESULT, PLAN }
  token_count:         integer

  // PROVENANCE
  source:              ContextSource
  source_version:      string | null
  commit:              string | null
  branch:              string | null
  timestamp:           ISO8601 string | null
  last_verified_at:    ISO8601 string | null

  // SCORING
  freshness_score:     float | null    // 0.0-1.0
  relevance_score:     float | null    // 0.0-1.0
  utility_score:       float | null    // Composite: relevance x density x authority / cost
  information_density: float | null
  source_authority:    float | null

  // OPTIMIZATION STATE
  is_retained:         boolean
  is_compressed:       boolean
  is_deduplicated:     boolean
  compression_ratio:   float | null
  recovery_reference:  string | null   // CL-004: pointer for restoration
  dependency_refs:     string[]        // CL-001: IDs of dependencies
  confidence:          float

  // SECURITY
  security_classification: SecurityClassification
  requires_authorization:  boolean
}
```

### 5.2 Context Retrieval Interface

```
interface ContextRetriever {
  retrieve(request: ContextRetrievalRequest) -> ContextRetrievalResult
  rerank(items: ContextItem[], query: string, budget: integer) -> ContextItem[]
  estimate_retrieval(request: ContextRetrievalRequest) -> RetrievalEstimate
}

ContextRetrievalRequest {
  request_id:            string
  query:                 string
  query_entities:        Entity[]
  retrieval_strategy:    enum { SEMANTIC, LEXICAL, HYBRID, DEPENDENCY_AWARE, ERROR_DRIVEN }
  max_tokens:            integer
  dynamic_k:             boolean     // Use adaptive Top-K (T1.12)
  max_k:                 integer
  freshness_requirement: FreshnessRequirements | null
  security_context:      SecurityContext
  tenant_id:             string
  filters:               map<string, any>
}

ContextRetrievalResult {
  retrieval_id:     string
  items:            ContextItem[]
  total_tokens:     integer
  k_selected:       integer
  k_fixed_baseline: integer
  quality_score:    float | null
  latency_ms:       integer
}
```

### 5.3 Context Pruning Interface

```
interface ContextPruner {
  prune(context: ContextItem[], request: PruningRequest) -> PruningResult
  estimate_pruning(context: ContextItem[], request: PruningRequest) -> PruningEstimate
}

PruningRequest {
  query:               string
  entities:            Entity[]
  token_budget:        integer
  technique:           enum { SENTENCE, PARAGRAPH, EMBEDDING, LEXICAL,
                              METADATA, RECENCY, AUTHORITY, RULE_BASED, COMPOSITE }
  min_relevance_score: float
  preserve_invariants: CompressionInvariant[]
  reversible:          boolean
}

PruningResult {
  retained:                ContextItem[]
  removed:                 ContextItem[]
  tokens_before:           integer
  tokens_after:            integer
  tokens_removed:          integer
  false_pruning_risk:      float
  quality_impact_estimate: float
  reversibility_records:   ReversibilityRecord[]
}
```

### 5.4 Context Deduplication Interface

```
interface ContextDeduplicator {
  deduplicate(context: ContextItem[], request: DeduplicationRequest) -> DeduplicationResult
}

DeduplicationRequest {
  technique:           enum { EXACT_HASH, NEAR_DUPLICATE, PARAGRAPH, CODE_AST, TOOL_OUTPUT }
  similarity_threshold: float
  preserve_canonical:  enum { FIRST, HIGHEST_AUTHORITY, MOST_RECENT }
}

DeduplicationResult {
  unique_items:    ContextItem[]
  removed_items:   ContextItem[]
  duplicate_pairs: DuplicatePair[]
  tokens_removed:  integer
}

DuplicatePair {
  original_item_id:  string
  duplicate_item_id: string
  similarity_score:  float
  technique_used:    string
}
```

### 5.5 Context Compression Interface

```
interface ContextCompressor {
  compress(context: ContextItem[], request: CompressionRequest) -> CompressionResult
  estimate_compression(context: ContextItem[], request: CompressionRequest) -> CompressionEstimate
}

CompressionRequest {
  query:                    string
  token_budget:             integer
  strategy:                 enum { EXTRACTIVE, MODEL_BASED, QUERY_AWARE,
                                   COARSE_TO_FINE, CODE_AWARE, ADAPTIVE }
  compression_ratio_target: float | null
  preserve_invariants:      CompressionInvariant[]
  quality_threshold:        float
  reversible:               boolean
}

CompressionResult {
  compressed_items:       ContextItem[]
  tokens_in:              integer
  tokens_out:             integer
  compression_ratio:      float
  quality_delta_estimate: float
  strategy_used:          string
  latency_ms:             integer
  reversibility_records:  ReversibilityRecord[]
  fallback_triggered:     boolean
}

CompressionInvariant {
  invariant_type: enum { ENTITY, CITATION, CODE_SIGNATURE, SECURITY_CONSTRAINT,
                         API_CONTRACT, ERROR_LOCATION, TEST_EXPECTATION }
  pattern:        string | null
  description:    string
}
```

### 5.6 Context Reordering Interface

```
interface ContextReorderer {
  reorder(context: ContextItem[], request: ReorderingRequest) -> ReorderingResult
}

ReorderingRequest {
  query:                      string
  model_id:                   string
  strategy:                   enum { RECENCY, RELEVANCE, AUTHORITY, ANTI_LOST_IN_MIDDLE,
                                     CODE_DEPENDENCY, INSTRUCTION_PRECEDENCE }
  preserve_source_boundaries: boolean
}

ReorderingResult {
  reordered_items:        ContextItem[]
  original_order:         string[]
  position_bias_score:    float | null
  quality_delta_estimate: float
}
```

### 5.7 Context Budget Interface

```
interface ContextBudgeter {
  allocate(request: BudgetAllocationRequest) -> BudgetAllocation
  check_overflow(tokens_used: integer, budget: BudgetAllocation) -> OverflowAction
}

BudgetAllocationRequest {
  total_budget:     integer
  context_sections: ContextSection[]
  complexity:       TaskComplexity
  model_id:         string
  tenant_id:        string
  workflow_id:      string | null
}

ContextSection {
  section_id:   string
  section_type: enum { SYSTEM, DEVELOPER, CONVERSATION, RETRIEVED,
                       TOOLS, MEMORY, AGENT_STATE, USER_INPUT }
  min_tokens:   integer
  max_tokens:   integer
  priority:     integer    // Lower = higher priority
}

BudgetAllocation {
  total_budget:      integer
  allocations:       map<string, integer>   // section_id -> token budget
  compaction_policy: CompactionPolicy
}

OverflowAction {
  action:                 enum { PROCEED, COMPACT, REJECT }
  compaction_steps:       string[]
  estimated_tokens_freed: integer
}
```

### 5.8 Dependency Graph Interface

```
interface ContextDependencyGraph {
  build(items: ContextItem[])           -> DependencyGraph
  get_dependencies(item_id: string)     -> string[]
  get_dependents(item_id: string)       -> string[]
  impact_of_removal(item_id: string)    -> ImpactAnalysis
  invalidate(change_event: ChangeEvent) -> InvalidationResult
}

DependencyGraph {
  nodes:         map<string, GraphNode>
  edges:         GraphEdge[]
  graph_version: string
}

GraphNode {
  item_id:   string
  node_type: enum { FILE, SYMBOL, FUNCTION, CLASS, IMPORT, TEST,
                    GIT_CHANGE, ERROR, TOOL, DECISION, MEMORY }
  metadata:  map<string, any>
}

GraphEdge {
  from_id:   string
  to_id:     string
  edge_type: enum { IMPORTS, CALLS, TESTS, CHANGES, DEPENDS_ON,
                    PRODUCES, REFERENCES, INVALIDATES }
}

ImpactAnalysis {
  affected_items:    string[]
  severity:          enum { LOW, MEDIUM, HIGH, CRITICAL }
  recovery_required: boolean
}
```


---

## 6. Cache Interfaces

*Traceability: PS §6.7, §6.8, §6.22, §10; ARCH §11.4, §11.5, §13.3, §16*

### 6.1 Provider-Neutral Cache Abstraction

```
interface CacheStore {
  lookup(request: CacheLookupRequest)           -> CacheLookupResult
  write(entry: CacheWriteRequest)               -> CacheWriteResult
  invalidate(request: CacheInvalidationRequest) -> CacheInvalidationResult
  refresh(key: string, namespace: string)       -> CacheRefreshResult
  warm(entries: CacheWarmRequest[])             -> CacheWarmResult
  inspect(key: string, namespace: string)       -> CacheEntry | null
  explain_miss(request: CacheLookupRequest)     -> CacheMissExplanation
}
```

### 6.2 Cache Lookup

```
CacheLookupRequest {
  cache_type:            enum { EXACT, SEMANTIC, TOOL_RESULT, EMBEDDING,
                                PROVIDER_NATIVE, APPLICATION, CONTEXT }
  key:                   string | null
  query_embedding:       float[] | null
  namespace:             string
  tenant_id:             string        // REQUIRED; isolation
  model_id:              string
  provider_id:           string
  security_context:      SecurityContext
  freshness_requirement: FreshnessRequirements | null
  similarity_threshold:  float | null
}

CacheLookupResult {
  hit:               boolean
  hit_type:          enum { EXACT, SEMANTIC, NONE }
  entry:             CacheEntry | null
  similarity_score:  float | null
  freshness_valid:   boolean
  auth_check_passed: boolean
  miss_reason:       CacheMissReason | null
  latency_ms:        integer
}

CacheMissReason {
  reason:      enum { NOT_FOUND, EXPIRED, STALE, AUTH_FAILED, POLICY_PROHIBITED,
                      SIMILARITY_BELOW_THRESHOLD, MUTABLE_DATA, PROVIDER_UNAVAILABLE }
  explanation: string
}
```

### 6.3 Cache Entry

```
CacheEntry {
  key:                     string
  namespace:               string
  tenant_id:               string
  organization_id:         string
  version:                 string
  content:                 any
  content_type:            string
  token_count:             integer
  security_classification: SecurityClassification

  ttl_seconds:       integer
  created_at:        ISO8601 string
  expires_at:        ISO8601 string
  last_accessed_at:  ISO8601 string
  access_count:      integer
  reuse_probability: float
  write_cost:        float
  read_cost:         float

  freshness_score:  float
  source_version:   string | null
  source_commit:    string | null
  source_branch:    string | null
  data_timestamp:   ISO8601 string | null

  dependency_refs: string[]    // CL-003: invalidation triggers
  model_id:        string
  provider_id:     string
}
```

### 6.4 Cache Write / Invalidation

```
CacheWriteRequest {
  cache_type:      enum { EXACT, SEMANTIC, TOOL_RESULT, EMBEDDING, CONTEXT }
  key:             string
  namespace:       string
  tenant_id:       string
  entry:           CacheEntry
  idempotency_key: string
  write_policy:    enum { IF_ABSENT, ALWAYS, IF_FRESHER }
}

CacheInvalidationRequest {
  keys:      string[] | null
  namespace: string
  tenant_id: string
  pattern:   string | null
  reason:    enum { EXPLICIT, STALENESS, PERMISSION_CHANGE,
                    DEPENDENCY_CHANGE, BRANCH_CHANGE, POLICY_CHANGE }
  cascade:   boolean    // Invalidate dependents via CL-003
}
```

### 6.5 Cache Safety Requirements

| Data Category | Cache Policy |
|---|---|
| Mutable data (live DB results) | Cache only with explicit freshness policy; TTL required |
| Permission-sensitive data | Cache key must include permission context hash |
| Tenant-specific data | Cache namespace must be tenant-scoped; no cross-tenant lookup |
| Branch-specific coding context | Cache key must include branch and commit |
| Provider-native cache | Provider adapter manages; core treats as opaque |
| PII-containing data | Cache prohibited unless explicitly permitted by tenant policy |
| Security instructions | Never serve from cache without auth check |

---

## 7. LLM Provider Interface

*Traceability: PS §13, §17; ARCH §25; OBJ-013*

### 7.1 LLMProvider Interface

```
interface LLMProvider {
  provider_id:     string
  adapter_version: string

  get_capability_profile(model_id: string) -> ModelProfile

  invoke(request: LLMInvocationRequest)          -> LLMInvocationResult
  invoke_streaming(request: LLMInvocationRequest) -> AsyncStream<LLMStreamChunk>
  invoke_batch(requests: LLMInvocationRequest[])  -> AsyncJob<LLMBatchResult>

  embed(request: EmbeddingRequest) -> EmbeddingResult
  health_check()                   -> ProviderHealthStatus
  get_availability()               -> ProviderAvailability
}
```

### 7.2 LLM Invocation

```
LLMInvocationRequest {
  schema_version:     string
  request_id:         string
  idempotency_key:    string
  model_id:           string
  provider_id:        string
  tenant_id:          string

  messages:           LLMMessage[]
  system_prompt:      string | null
  prompt_cache_policy: PromptCachePolicy | null

  max_output_tokens:  integer | null
  output_schema:      JSONSchema | null
  stop_sequences:     string[] | null

  reasoning_config:   ReasoningConfig | null

  tools:              ToolDefinition[] | null
  tool_choice:        enum { AUTO, REQUIRED, NONE } | string | null

  timeout_ms:         integer
  cancellation_token: string | null
  retry_policy:       RetryPolicy

  cost_tracking_id:   string
  provider_hints:     map<string, any>    // Opaque; adapter-specific
}

LLMMessage {
  role:        enum { SYSTEM, USER, ASSISTANT, TOOL }
  content:     string | ContentBlock[]
  tool_call_id: string | null
  tool_calls:   LLMToolCall[] | null
}

ContentBlock {
  type:          enum { TEXT, IMAGE, DOCUMENT }
  content:       any
  cache_control: CacheControl | null   // Provider abstracted
}

CacheControl {
  type: enum { EPHEMERAL, PERSISTENT }
}

PromptCachePolicy {
  enable_cache:      boolean
  stable_prefix_end: integer | null
  provider_hints:    map<string, any>
}

ReasoningConfig {
  budget_level: enum { LOW, MEDIUM, HIGH, MAX }
  // Adapter translates to provider-specific parameters
}

LLMInvocationResult {
  request_id:    string
  provider_id:   string
  model_id:      string
  model_version: string | null
  content:       string | ContentBlock[]
  tool_calls:    LLMToolCall[] | null
  finish_reason: enum { STOP, MAX_TOKENS, TOOL_CALLS, CONTENT_FILTER, ERROR }

  usage: LLMUsage

  latency_ms:             integer
  time_to_first_token_ms: integer | null

  cache_read_tokens:  integer | null
  cache_write_tokens: integer | null
  cache_hit:          boolean

  estimated_cost:  float
  currency:        string
  pricing_version: string
}

LLMUsage {
  input_tokens:        integer
  output_tokens:       integer
  cached_input_tokens: integer | null
  reasoning_tokens:    integer | null
  total_tokens:        integer
}

LLMToolCall {
  tool_call_id: string
  tool_name:    string
  arguments:    map<string, any>
}
```

---

## 8. Model Profile Interface

*Traceability: PS §18; ARCH §25; OBJ-008, OBJ-013*

### 8.1 ModelProfile Schema

```
ModelProfile {
  schema_version: string
  profile_id:     string
  model_id:       string        // e.g., "claude-sonnet-4-5"
  display_name:   string
  provider_id:    string        // e.g., "anthropic"
  model_family:   string

  context_limit_tokens: integer
  output_limit_tokens:  integer
  min_cacheable_tokens: integer | null

  input_price_per_1k:        float
  output_price_per_1k:       float
  cached_input_price_per_1k: float | null
  cache_write_price_per_1k:  float | null
  reasoning_price_per_1k:    float | null
  batch_input_discount:      float | null

  supports_tool_calling:       boolean
  supports_structured_output:  boolean
  supports_streaming:          boolean
  supports_batch:              boolean
  supports_reasoning_controls: boolean
  supports_embeddings:         boolean
  supports_vision:             boolean
  supports_prefix_caching:     boolean

  cache_ttl_seconds:       integer | null
  cache_semantics:         string | null
  known_cache_constraints: string[]

  typical_latency_p50_ms: integer | null
  typical_latency_p99_ms: integer | null
  typical_ttft_ms:        integer | null

  data_residency_regions:    string[]
  compliance_certifications: string[]
  is_enterprise_available:   boolean

  capability_tags: string[]    // e.g., ["long-context", "code", "reasoning"]

  version:          string
  effective_date:   ISO8601 string
  deprecation_date: ISO8601 string | null
}
```

### 8.2 Model Profile Registry Interface

```
interface ModelProfileRegistry {
  get_profile(model_id: string, provider_id: string) -> ModelProfile | null
  list_profiles(filters: ProfileFilters)             -> ModelProfile[]
  update_profile(profile: ModelProfile)              -> void
  get_profile_history(model_id: string)              -> ModelProfile[]
}

ProfileFilters {
  providers:            string[] | null
  min_context_window:   integer | null
  supports_tools:       boolean | null
  supports_batch:       boolean | null
  max_input_price:      float | null
  compliance_required:  string[] | null
  data_residency:       string[] | null
  capability_tags:      string[] | null
}
```


---

## 9. Model Routing Interface

*Traceability: PS §6.1, §6.23; ARCH §10.1, §13.4, §19.1; OBJ-008*

### 9.1 ModelRoutingRequest

```
ModelRoutingRequest {
  schema_version:       string
  request_id:           string
  tenant_id:            string

  task_complexity:      TaskComplexityResult
  quality_requirement:  QualityRequirements
  latency_requirement:  LatencyRequirements
  budget_constraint:    BudgetConstraints | null
  context_token_count:  integer
  tool_requirements:    ToolRequirements | null
  reasoning_requirement: ReasoningRequirement | null

  historical_quality:   map<string, float> | null   // model_id -> quality_score
  historical_cost:      map<string, float> | null   // model_id -> cost_per_request

  model_policy:          ModelPolicy
  provider_availability: map<string, ProviderAvailability>
}

TaskComplexityResult {
  complexity_class:             enum { LOW, MEDIUM, HIGH }
  intent:                       string
  input_size_tokens:            integer
  entity_count:                 integer
  tool_count_required:          integer
  retrieval_depth:              enum { SHALLOW, MEDIUM, DEEP }
  reasoning_required:           boolean
  confidence_requirement:       float
  recommended_model_tier:       string
  recommended_context_budget:   integer
  recommended_reasoning_budget: enum { LOW, MEDIUM, HIGH }
  recommended_output_budget:    integer
}

ModelPolicy {
  allowed_models:          string[] | null
  denied_models:           string[] | null
  allowed_providers:       string[] | null
  denied_providers:        string[] | null
  default_model_id:        string
  cascade_enabled:         boolean
  max_model_cost_per_1k:   float | null
  compliance_requirements: string[]
  data_residency:          string[] | null
}
```

### 9.2 ModelRoutingResult

```
ModelRoutingResult {
  selected_model_id:     string
  selected_provider_id:  string
  fallback_models:       FallbackModel[]
  routing_confidence:    float

  estimated_cost:         float
  estimated_latency_ms:   integer
  expected_quality_score: float

  routing_rationale:    RoutingRationale
  marginal_model_value: float         // AR-001: expected quality gain / additional cost
  cascade_plan:         CascadePlan | null

  routing_decision_id: string
  policy_version:      string
  signals_used:        map<string, any>
}

FallbackModel {
  model_id:        string
  provider_id:     string
  fallback_trigger: enum { QUALITY_FAILURE, AVAILABILITY_FAILURE,
                           ESCALATION_REQUIRED, COST_CONSTRAINT }
  priority:        integer
}

CascadePlan {
  first_tier_model:   string
  escalation_trigger: CascadeEscalationTrigger
  second_tier_model:  string
  max_escalations:    integer
}

CascadeEscalationTrigger {
  min_confidence_required: float
  schema_must_pass:        boolean
  validation_must_pass:    boolean
  custom_conditions:       string[]
}
```

---

## 10. Tool Interfaces

*Traceability: PS §6.21, §6.22; ARCH §17 (TE-001–007); OBJ-006*

### 10.1 ToolDefinition

```
ToolDefinition {
  tool_id:               string
  tool_name:             string
  version:               string
  description:           string
  input_schema:          JSONSchema
  output_schema:         JSONSchema
  authorization_required: boolean
  authorization_scopes:  string[]
  is_deterministic:      boolean
  supports_caching:      boolean
  estimated_latency_ms:  integer
  estimated_cost:        float | null
  side_effects:          boolean
  idempotent:            boolean
  capability_tags:       string[]
}
```

### 10.2 Tool Discovery Interface

```
interface ToolRegistry {
  discover_tools(request: ToolDiscoveryRequest)                     -> ToolDiscoveryResult
  get_tool(tool_id: string, version: string | null)                 -> ToolDefinition | null
  select_relevant(task_context: TaskContext, budget: ToolBudget)    -> ToolDefinition[]
  minimize_schema(tool: ToolDefinition, task_context: TaskContext)  -> ToolDefinition
  load_dynamic(tool_id: string, task_context: TaskContext)          -> ToolDefinition
}

ToolDiscoveryRequest {
  tenant_id:             string
  task_intent:           string
  capability_tags:       string[]
  max_tools:             integer
  authorization_context: AuthorizationContext
}
```

### 10.3 Tool Invocation Interface

```
interface ToolExecutor {
  invoke(request: ToolInvocationRequest)              -> ToolInvocationResult
  invoke_parallel(requests: ToolInvocationRequest[])  -> ToolInvocationResult[]
  estimate_invocation(request: ToolInvocationRequest) -> ToolInvocationEstimate
}

ToolInvocationRequest {
  schema_version:       string
  tool_call_id:         string         // Idempotency key
  tool_id:              string
  version:              string
  arguments:            map<string, any>
  authorization_context: AuthorizationContext
  timeout_ms:           integer
  retry_policy:         RetryPolicy
  idempotency_key:      string
  cache_policy:         ToolCachePolicy | null
  filter_policy:        ToolResultFilterPolicy | null
  request_id:           string
  span_id:              string
  tenant_id:            string
}

ToolInvocationResult {
  tool_call_id:   string
  tool_id:        string
  success:        boolean
  result:         any
  filtered_result: any | null
  raw_tokens:     integer
  filtered_tokens: integer | null
  cache_hit:      boolean
  latency_ms:     integer
  estimated_cost: float
  error:          ToolError | null
  audit_fields:   map<string, any>    // Always preserved; never filtered
}

ToolInvocationEstimate {
  expected_information_gain: float
  expected_cost:             float
  expected_latency_ms:       integer
  roi:                       float    // gain / (cost + latency_cost)
  recommendation:            enum { EXECUTE, SKIP, DEFER }
  skip_reason:               string | null
}
```

---

## 11. Agent Interface

*Traceability: PS §4, §6.20; ARCH §18 (AL-001–006); OBJ-010*

### 11.1 Agent Registration

```
AgentRegistration {
  agent_id:         string
  agent_type:       enum { GENERIC, CODING, MULTI_AGENT, RAG, TOOL_ENABLED }
  framework:        string
  framework_version: string | null
  capabilities:     AgentCapabilities
  tenant_id:        string
  registered_at:    ISO8601 string
}

AgentCapabilities {
  supports_tool_calling:   boolean
  supports_sub_agents:     boolean
  supports_checkpointing:  boolean
  supports_early_exit:     boolean
  supports_streaming:      boolean
  max_iterations:          integer | null
}
```

### 11.2 Agent Task Interface

```
AgentTaskSubmission {
  task_id:            string
  agent_id:           string
  parent_task_id:     string | null
  objective:          string
  initial_context:    ControlPlaneRequest
  max_iterations:     integer
  iteration_budget:   BudgetConstraints
  quality_threshold:  float
  early_exit_enabled: boolean
}

AgentIterationReport {
  task_id:                  string
  agent_id:                 string
  iteration:                integer
  objective:                string
  state:                    AgentState
  progress:                 AgentProgress
  tools_called:             ToolInvocationResult[]
  tokens_consumed:          LLMUsage
  cost_this_iteration:      float
  cumulative_cost:          float
  information_gained:       float
  state_change:             float
  failures_this_iteration:  AgentFailure[]
  next_action:              AgentNextAction
}

AgentState {
  status:               enum { RUNNING, PAUSED, WAITING_FOR_TOOL, COMPLETED, FAILED }
  current_objective:    string
  completed_actions:    string[]
  pending_actions:      string[]
  unresolved_questions: string[]
  key_decisions:        string[]
  checkpointed_at:      ISO8601 string | null
}

AgentProgress {
  objective_completion_pct: float
  steps_planned:       integer
  steps_executed:      integer
  steps_skipped:       integer
  early_exit_eligible: boolean
  early_exit_reason:   string | null
}

AgentNextAction {
  action_type:         enum { CONTINUE, STOP, ESCALATE, CHECKPOINT, SPAWN_SUBAGENT }
  reason:              string
  expected_value:      float
  stop_recommendation: boolean
}
```

### 11.3 Agent Early Exit Interface

```
interface AgentEarlyExitEvaluator {
  should_stop(report: AgentIterationReport, policy: EarlyExitPolicy) -> EarlyExitDecision
}

EarlyExitPolicy {
  min_quality_threshold:      float
  max_iterations:             integer
  min_information_gain:       float
  min_state_change:           float
  max_no_progress_iterations: integer
  max_cost:                   float | null
}

EarlyExitDecision {
  stop:                boolean
  reason:              enum { OBJECTIVE_SATISFIED, QUALITY_THRESHOLD_MET, NO_PROGRESS,
                              MAX_ITERATIONS, BUDGET_EXHAUSTED, LOOP_DETECTED, POLICY_REQUIRED }
  tokens_saved:        integer
  tool_calls_avoided:  integer
  model_calls_avoided: integer
  explanation:         string
}
```


---

## 12. Sub-Agent Interface

*Traceability: PS §4D; ARCH §18.3–18.5 (AL-003–AL-005)*

### 12.1 Sub-Agent Spawn Interface

```
SubAgentSpawnRequest {
  schema_version:      string
  spawn_id:            string          // Idempotency key
  parent_task_id:      string
  parent_agent_id:     string
  tenant_id:           string

  delegated_objective: string
  delegated_context:   SubAgentContextHandoff
  max_iterations:      integer
  quality_requirement: QualityRequirements
  latency_requirement: LatencyRequirements
  budget:              BudgetConstraints

  expected_value:  float    // AL-003: estimated value
  expected_cost:   float
  spawn_decision:  SubAgentSpawnDecision
}

SubAgentSpawnDecision {
  spawn:                  boolean
  reason:                 string
  value_estimate:         float
  cost_estimate:          float
  value_to_cost_ratio:    float
  alternatives_considered: string[]
}

SubAgentContextHandoff {
  objective:                 string
  key_findings:              string[]
  evidence:                  ContextItem[]
  files_and_symbols:         string[]
  confidence:                float
  decisions_made:            string[]
  unresolved_questions:      string[]
  recommended_next_actions:  string[]
  context_token_count:       integer
  full_transcript_excluded:  boolean    // Must be true; AL-005: no full replay
}
```

### 12.2 Sub-Agent Result Handoff

```
SubAgentResultHandoff {
  spawn_id:       string
  child_agent_id: string
  parent_task_id: string
  status:         enum { SUCCESS, PARTIAL, FAILED, CANCELLED }

  findings:             SubAgentFinding[]
  unresolved_questions: string[]
  recommended_actions:  string[]
  confidence:           float

  parent_cost:             float
  child_cost:              float
  duplicated_context_cost: float
  aggregation_cost:        float
  coordination_latency_ms: integer
  total_cost:              float
  roi:                     float

  child_tokens_consumed:  LLMUsage
  context_tokens_sent:    integer
  result_tokens_returned: integer
}

SubAgentFinding {
  finding_id:  string
  type:        enum { FACT, DECISION, CODE_CHANGE, ERROR, RECOMMENDATION }
  content:     string
  evidence_refs: string[]
  confidence:  float
  locations:   CodeLocation[] | null
}

CodeLocation {
  file:       string
  line_start: integer | null
  line_end:   integer | null
  symbol:     string | null
}
```

### 12.3 Sub-Agent Aggregation Interface

```
interface SubAgentAggregator {
  aggregate(results: SubAgentResultHandoff[]) -> AggregatedResult
  compress_for_parent(result: AggregatedResult, budget: integer) -> CompressedAgentResult
}

AggregatedResult {
  aggregation_id: string
  child_results:  SubAgentResultHandoff[]
  combined_findings: SubAgentFinding[]
  conflicts:      FindingConflict[]
  total_cost:     float
  aggregation_cost: float
  total_latency_ms: integer
}

FindingConflict {
  finding_a_id: string
  finding_b_id: string
  conflict_type: string
  resolution:   string | null
}
```

---

## 13. Developer / Coding Agent Interface

*Traceability: PS §4C, DA-001–DA-025; ARCH §9, §22; OBJ-001–014*

### 13.1 Developer Agent Request Extension

```
DeveloperAgentRequest extends ControlPlaneRequest {
  // request_type = DEVELOPER_CODING

  repository_context: RepositoryContext
  coding_task_type:   enum { IMPLEMENT, DEBUG, REFACTOR, EXPLAIN, TEST,
                             REVIEW, FIX_ERROR, PATCH, SEARCH }

  git_context:     GitContext | null
  compiler_output: CompilerOutput | null
  test_output:     TestOutput | null
  terminal_output: TerminalOutput | null
  build_output:    BuildOutput | null
  issue_context:   IssueContext | null
}

RepositoryContext {
  repository_id:   string
  repository_uri:  string | null
  branch:          string
  commit:          string | null
  language:        string[]
  build_system:    string | null
  test_framework:  string | null

  selected_files:   FileContext[]
  repository_map:   RepositoryMap | null
  selected_symbols: SymbolContext[]
  dependencies:     DependencyContext | null
}

FileContext {
  file_path:      string
  content_type:   enum { FULL, PARTIAL, SIGNATURE_ONLY, METADATA_ONLY }
  content:        string
  token_count:    integer
  relevance_score: float
  last_modified:  ISO8601 string | null
  commit:         string | null
  is_modified:    boolean
}

RepositoryMap {
  repository_id: string
  branch:        string
  commit:        string
  structure:     map<string, any>    // Compact tree; DA-003
  cached_at:     ISO8601 string
  cache_key:     string
}

SymbolContext {
  symbol_id:    string
  symbol_type:  enum { FUNCTION, CLASS, METHOD, INTERFACE, TYPE,
                       IMPORT, VARIABLE, CONSTANT }
  name:         string
  file_path:    string
  line_start:   integer
  line_end:     integer
  signature:    string
  body:         string | null    // null = signature-only mode
  callers:      string[]
  callees:      string[]
  dependencies: string[]
  token_count:  integer
}

GitContext {
  diff:           GitDiff | null
  changed_files:  string[]
  change_summary: string | null
  commit_message: string | null
  pr_description: string | null
}

GitDiff {
  raw_diff:             string | null
  optimized_diff:       string            // DA-004: selective exposure
  changed_symbols:      string[]
  token_count_raw:      integer
  token_count_optimized: integer
}

CompilerOutput {
  raw_output:       string | null
  extracted_errors: CompilerError[]    // DA-006: actionable failures only
  token_count_raw:  integer
  token_count_extracted: integer
}

CompilerError {
  error_code:      string | null
  message:         string
  file_path:       string | null
  line_number:     integer | null
  column:          integer | null
  stack_trace:     string | null
  expected_value:  string | null
  actual_value:    string | null
  relevant_context: string | null    // DA-012: error-driven retrieval
}

TestOutput {
  raw_output:   string | null
  test_summary: TestSummary
  failed_tests: TestFailure[]
  token_count_raw:      integer
  token_count_extracted: integer
}

TestSummary {
  total:   integer
  passed:  integer
  failed:  integer
  skipped: integer
}

TestFailure {
  test_name:       string
  file_path:       string | null
  line_number:     integer | null
  failure_message: string
  stack_trace:     string | null
}
```

### 13.2 Developer Agent Context Budget Priorities

| Section | Priority | Notes |
|---|---|---|
| `SYSTEM` | 1 (highest) | Security instructions; never pruned |
| `DEVELOPER_INSTRUCTIONS` | 2 | Coding-agent system prompt |
| `COMPILER_ERRORS` | 3 | DA-006: actionable failures |
| `SYMBOL_CONTEXT` | 4 | DA-002: relevant symbols |
| `GIT_DIFF` | 5 | DA-004: relevant changes |
| `FILE_CONTENT` | 6 | DA-016: lazy-loaded files |
| `CONVERSATION` | 7 | Pruned history |
| `REPOSITORY_MAP` | 8 | DA-003: compact map |
| `TERMINAL_OUTPUT` | 9 | DA-005: filtered output |
| `MCP_TOOLS` | 10 | DA-008: minimized schemas |
| `MEMORY` | 11 | DA-010: relevant memory |
| `TEST_OUTPUT` | 12 | Relevant failures only |
| `AGENT_STATE` | 13 | DA-018: active plan only |

### 13.3 Coding Agent Loop Controller

```
interface CodingAgentLoopController {
  track_iteration(report: AgentIterationReport) -> LoopDecision
}

LoopDecision {
  continue_loop:          boolean
  reason:                 enum { TASK_COMPLETE, QUALITY_ACHIEVED, NO_PROGRESS,
                                 BUDGET_EXHAUSTED, MAX_ITERATIONS, LOOP_DETECTED }
  cumulative_cost:        float
  cumulative_tokens:      LLMUsage
  information_gain_trend: float    // DA-014: trend over last N iterations
  recommendation:         string
  tokens_saved_by_exit:   integer
}
```

---

## 14. MCP / Tool Ecosystem Interface

*Traceability: PS §4 Integration Mode 3, DA-007, DA-008; ARCH §17.3*

```
interface MCPAdapter {
  discover_servers(config: MCPDiscoveryConfig)                         -> MCPServer[]
  get_server_capabilities(server_id: string)                           -> MCPServerCapabilities
  discover_tools(server_id: string, filter: MCPToolFilter)             -> ToolDefinition[]
  discover_resources(server_id: string, filter: MCPResourceFilter)     -> MCPResource[]
  invoke_tool(server_id: string, request: ToolInvocationRequest)       -> ToolInvocationResult
  apply_security_policy(server_id: string, policy: MCPSecurityPolicy)  -> void
}

MCPServer {
  server_id:        string
  server_name:      string
  protocol_version: string
  transport:        enum { STDIO, HTTP, WEBSOCKET }
  security_policy:  MCPSecurityPolicy
}

MCPToolFilter {
  task_context:          TaskContext
  max_tools:             integer
  max_schema_tokens:     integer       // DA-008: schema budget
  required_capabilities: string[]
}

MCPSecurityPolicy {
  allowed_operations: string[]
  denied_operations:  string[]
  require_authorization: boolean
  audit_all_calls:    boolean
}
```

> **IMPORTANT:** The core optimizer must never import or depend on a specific MCP library version. All MCP communication must go through the `MCPAdapter` interface.


---

## 15. RAG Interface

*Traceability: PS §6.9, §6.12; ARCH §11.6, §11.9; Domain C*

```
interface RAGPipeline {
  rewrite_query(query: string, context: QueryRewriteContext)           -> RewrittenQuery
  retrieve(request: ContextRetrievalRequest)                           -> ContextRetrievalResult
  rerank(items: ContextItem[], query: string, budget: integer)         -> RerankResult
  select_chunks(items: ContextItem[], policy: ChunkSelectionPolicy)    -> ContextItem[]
  assemble_context(chunks: ContextItem[], policy: AssemblyPolicy)      -> AssembledContext
  validate_freshness(items: ContextItem[], req: FreshnessRequirements) -> FreshnessValidationResult
  compress_context(request: CompressionRequest)                        -> CompressionResult
}

RewrittenQuery {
  original_query:      string
  rewritten_query:     string
  semantic_equivalence: float
  rewrite_reason:      string
  tokens_saved:        integer
}

AssembledContext {
  items:                   ContextItem[]
  total_tokens:            integer
  cacheable_prefix_tokens: integer
  volatile_suffix_tokens:  integer
  provenance_map:          map<string, ContextSource>
  citation_map:            map<string, string>
}

FreshnessValidationResult {
  all_fresh:   boolean
  stale_items: FreshnessViolation[]
  action:      enum { PROCEED, WARN, REJECT, REFETCH }
}

FreshnessViolation {
  item_id:             string
  source:              ContextSource
  age_seconds:         integer
  max_allowed_seconds: integer
  action:              enum { EXCLUDE, WARN, REFETCH }
}
```

---

## 16. Memory Interface

*Traceability: PS §34 (CL-006); ARCH §15.6; DA-010*

```
interface MemoryStore {
  store(entry: MemoryEntry)                                      -> MemoryStoreResult
  retrieve(request: MemoryRetrievalRequest)                      -> MemoryRetrievalResult
  update(entry_id: string, updates: MemoryUpdate)                -> MemoryUpdateResult
  invalidate(request: MemoryInvalidationRequest)                 -> MemoryInvalidationResult
  compress(layer: MemoryLayer, policy: MemoryCompressionPolicy)  -> MemoryCompressionResult
  summarize(entries: MemoryEntry[], policy: SummarizationPolicy) -> MemorySummary
  expire(layer: MemoryLayer, before: ISO8601 string)             -> ExpireResult
  restore(entry_id: string)                                      -> MemoryEntry | null
}

MemoryEntry {
  entry_id:     string
  layer:        enum { TURN, TASK, SESSION, PROJECT, ORGANIZATION, HISTORICAL }
  tenant_id:    string
  agent_id:     string | null
  session_id:   string | null
  task_id:      string | null
  content:      string
  content_type: enum { FACT, DECISION, CODE_FINDING, CONVERSATION_SUMMARY,
                       OBJECTIVE, PLAN, PREFERENCE, ERROR_PATTERN }
  tags:         string[]
  source_ids:   string[]
  created_at:   ISO8601 string
  updated_at:   ISO8601 string
  expires_at:   ISO8601 string | null
  token_count:  integer
  confidence:   float
  freshness_policy:    MemoryFreshnessPolicy
  compression_policy:  MemoryCompressionPolicy
  retention_policy:    MemoryRetentionPolicy
  invalidation_policy: MemoryInvalidationPolicy
}

MemoryRetrievalRequest {
  layer:                 enum { TURN, TASK, SESSION, PROJECT, ORGANIZATION, HISTORICAL }
  tenant_id:             string
  agent_id:              string | null
  session_id:            string | null
  query:                 string
  max_tokens:            integer
  max_entries:           integer
  min_confidence:        float
  freshness_requirement: FreshnessRequirements | null
  security_context:      SecurityContext
}
```

---

## 17. Quality Validation Interface

*Traceability: PS §9, §14; ARCH §20, §28; AC-003*

```
interface QualityValidator {
  validate(request: ValidationRequest)                 -> ValidationResult
  estimate_validation_cost(request: ValidationRequest) -> ValidationEstimate
}

ValidationRequest {
  schema_version:    string
  validation_id:     string
  request_id:        string
  original_content:  any
  optimized_content: any
  validators:        ValidatorSpec[]
  quality_threshold: float
  tenant_id:         string
}

ValidatorSpec {
  validator_id:   string
  validator_type: enum { SCHEMA, DETERMINISTIC_RULE, UNIT_TEST, INTEGRATION_TEST,
                         TYPE_CHECK, STATIC_ANALYSIS, CITATION_CHECK, BUSINESS_RULE,
                         SECURITY_CHECK, MODEL_BASED_EVAL, EMBEDDING_SIMILARITY }
  is_required:    boolean
  weight:         float
  config:         map<string, any>
}

ValidationResult {
  validation_id:       string
  request_id:          string
  pass:                boolean
  score:               float
  confidence:          float
  violations:          ValidationViolation[]
  evidence:            map<string, any>
  recommended_fallback: FallbackAction
  validators_run:      string[]
  latency_ms:          integer
}

ValidationViolation {
  validator_id: string
  severity:     enum { INFO, WARNING, ERROR, CRITICAL }
  message:      string
  location:     string | null
  recoverable:  boolean
}

FallbackAction {
  action:     enum { RESTORE_ORIGINAL, INCREASE_CONTEXT, INCREASE_REASONING,
                     ESCALATE_MODEL, DISABLE_OPTIMIZATION, RETRY }
  parameters: map<string, any>
}
```

---

## 18. Evaluation Interface

*Traceability: PS §14; ARCH §21 (EL-001–005); AC-015, AC-017*

```
interface EvaluationFramework {
  run_baseline(request: ControlPlaneRequest)                              -> EvaluationRun
  run_optimized(request: ControlPlaneRequest, plan: OptimizationPlan)    -> EvaluationRun
  compare(baseline: EvaluationRun, optimized: EvaluationRun)             -> EvaluationComparison
  report_regression(comparison: EvaluationComparison)                    -> RegressionReport
}

EvaluationRun {
  run_id:      string
  request_id:  string
  run_type:    enum { BASELINE, OPTIMIZED, SHADOW }
  timestamp:   ISO8601 string

  baseline_cost:      float
  optimized_cost:     float | null
  gross_savings:      float
  optimizer_overhead: float
  net_savings:        float
  net_savings_pct:    float

  quality_delta:           float
  quality_score_baseline:  float
  quality_score_optimized: float | null

  latency_delta_ms:     integer
  latency_ms_baseline:  integer
  latency_ms_optimized: integer | null

  cache_hit_rate:          float
  tokens_avoided_by_cache: integer

  task_success_baseline:   boolean
  task_success_optimized:  boolean | null

  regression_detected:   boolean
  regression_dimensions: string[]
}

EvaluationComparison {
  comparison_id:      string
  baseline_run_id:    string
  optimized_run_id:   string
  net_savings:        float
  quality_delta:      float
  latency_delta_ms:   integer
  task_success_delta: float
  recommendation:     enum { APPROVE, REJECT, INVESTIGATE }
  rejection_reasons:  string[]
}
```


---

## 19. Observability Interface

*Traceability: PS §12; ARCH §31; OBJ-011, NFR-006*

### 19.1 Standard Log Entry

```
ControlPlaneLogEntry {
  timestamp:      ISO8601 string
  level:          enum { DEBUG, INFO, WARNING, ERROR, CRITICAL }
  request_id:     string
  correlation_id: string
  span_id:        string
  parent_span_id: string | null
  tenant_id:      string
  component_id:   string     // e.g., "T1.9-CONTEXT-PRUNER"
  event_type:     string     // e.g., "OPTIMIZATION_APPLIED"
  message:        string
  fields:         map<string, any>
  // SENSITIVE fields are redacted; replaced with "[REDACTED]"
}
```

### 19.2 Standard Metrics

| Metric Name | Type | Labels |
|---|---|---|
| `control_plane.request.count` | Counter | `tenant_id`, `request_type`, `status` |
| `control_plane.request.latency_ms` | Histogram | `tenant_id`, `request_type`, `stage_id` |
| `control_plane.tokens.input` | Counter | `tenant_id`, `model_id`, `provider_id` |
| `control_plane.tokens.output` | Counter | `tenant_id`, `model_id`, `provider_id` |
| `control_plane.tokens.avoided` | Counter | `tenant_id`, `stage_id`, `technique` |
| `control_plane.cost.estimated` | Counter | `tenant_id`, `model_id`, `provider_id` |
| `control_plane.cost.saved` | Counter | `tenant_id`, `stage_id`, `technique` |
| `control_plane.cache.hit_rate` | Gauge | `tenant_id`, `cache_type` |
| `control_plane.stage.success_rate` | Gauge | `tenant_id`, `stage_id` |
| `control_plane.stage.fallback_rate` | Gauge | `tenant_id`, `stage_id` |
| `control_plane.quality.score` | Histogram | `tenant_id`, `stage_id` |
| `control_plane.agent.iterations` | Histogram | `tenant_id`, `agent_type` |
| `control_plane.tool.calls_avoided` | Counter | `tenant_id`, `tool_id` |

### 19.3 Distributed Trace Span

```
OptimizationSpan {
  trace_id:       string
  span_id:        string
  parent_span_id: string | null
  operation_name: string
  component_id:   string
  start_time:     ISO8601 string
  end_time:       ISO8601 string
  duration_ms:    integer
  status:         enum { OK, FALLBACK, ERROR }
  tags:           map<string, any>
  events:         SpanEvent[]
}
```

### 19.4 End-to-End Trace Correlation Chain

```
REQUEST_RECEIVED
  -> OPTIMIZATION_DECISION (plan_id)
    -> CONTEXT_OPERATION (prune / compress / dedup)
      -> CACHE_OPERATION (lookup / write / invalidate)
        -> TOOL_OPERATION (invoke / filter / cache)
          -> MODEL_ROUTING_DECISION (routing_decision_id)
            -> LLM_CALL (provider, model, usage)
              -> VALIDATION (validation_id)
                -> FINAL_OUTCOME (success / fallback / failure)
```

Every link must be navigable via `request_id` + `span_id`.

### 19.5 Audit Record

```
AuditRecord {
  audit_id:        string
  request_id:      string
  tenant_id:       string
  organization_id: string
  timestamp:       ISO8601 string
  event_type:      string
  actor:           string         // component_id or user_id
  resource_id:     string
  action:          string
  outcome:         enum { SUCCESS, FAILURE, FALLBACK }
  policy_version:  string
  details:         map<string, any>   // Non-sensitive; PII excluded
  // Audit records are immutable and may not be deleted
}
```

---

## 20. Policy / Governance Interface

*Traceability: PS §10, §23; ARCH §29; SEC-001–010*

### 20.1 Policy Schema

```
OptimizationPolicy {
  policy_id:       string
  policy_version:  string
  schema_version:  string
  tenant_id:       string
  organization_id: string
  effective_from:  ISO8601 string
  effective_until: ISO8601 string | null

  enabled_stages:      string[]
  disabled_stages:     string[]
  optimization_depth:  enum { MINIMAL, STANDARD, DEEP }
  require_net_savings: boolean

  model_allowlist:  string[]
  model_denylist:   string[]
  default_model_id: string
  cascade_enabled:  boolean

  max_input_tokens:     integer | null
  max_output_tokens:    integer | null
  max_cost_per_request: float | null
  max_monthly_cost:     float | null

  max_e2e_latency_ms:           integer | null
  max_optimization_overhead_ms: integer | null

  min_quality_score:     float
  min_task_success_rate: float
  max_regression_delta:  float

  exact_cache_enabled:           boolean
  semantic_cache_enabled:        boolean
  semantic_similarity_threshold: float
  cache_pii_prohibited:          boolean

  pii_handling:   enum { SCRUB, BLOCK, PASSTHROUGH_WITH_AUDIT }
  data_residency: string[] | null

  compliance_frameworks: string[]
  rollout_pct:           float
  shadow_mode:           boolean

  per_workflow_overrides: map<string, PolicyOverride>
  per_intent_overrides:   map<string, PolicyOverride>

  created_by:    string
  approved_by:   string | null
  change_reason: string
}
```

### 20.2 Policy Enforcement Interface

```
interface PolicyEnforcer {
  enforce(plan: OptimizationPlan, policy: OptimizationPolicy) -> EnforcementResult
  check_model_allowed(model_id: string, policy: OptimizationPolicy) -> boolean
  check_cache_allowed(entry: CacheEntry, policy: OptimizationPolicy) -> boolean
  check_tool_allowed(tool_id: string, auth: AuthorizationContext, policy: OptimizationPolicy) -> boolean
}

EnforcementResult {
  compliant:     boolean
  violations:    PolicyViolation[]
  modified_plan: OptimizationPlan
}

PolicyViolation {
  policy_id:   string
  rule_id:     string
  severity:    enum { WARNING, BLOCK }
  message:     string
  remediation: string
}
```

---

## 21. Security Interfaces

*Traceability: PS §10 (SEC-001–010); ARCH §29; OBJ-014*

### 21.1 Authentication Context

```
AuthenticationContext {
  auth_method:     enum { API_KEY, OAUTH2, SERVICE_ACCOUNT, MUTUAL_TLS }
  identity_id:     string
  tenant_id:       string
  organization_id: string
  scopes:          string[]
  issued_at:       ISO8601 string
  expires_at:      ISO8601 string | null
  // Raw credentials NEVER appear in interfaces; referenced by handle only
}
```

### 21.2 Authorization Interface

```
interface AuthorizationService {
  authorize(request: AuthorizationRequest)                              -> AuthorizationDecision
  check_cache_access(tenant_id: string, cache_key: string, identity: string) -> boolean
  check_tool_access(tool_id: string, auth: AuthenticationContext)       -> ToolAccessDecision
  check_model_access(model_id: string, auth: AuthenticationContext)     -> boolean
  check_data_access(source: ContextSource, auth: AuthenticationContext) -> boolean
}

AuthorizationRequest {
  resource_type: enum { CACHE, TOOL, MODEL, DATA_SOURCE, MEMORY, AGENT }
  resource_id:   string
  action:        enum { READ, WRITE, EXECUTE, DELETE, ADMIN }
  auth_context:  AuthenticationContext
  tenant_id:     string
}

AuthorizationDecision {
  authorized:        boolean
  reason:            string | null
  applicable_policy: string | null
  audit_required:    boolean
}
```

### 21.3 Tenant Isolation Contract

```
TenantIsolationContext {
  tenant_id:       string
  organization_id: string
  user_id:         string | null
  agent_id:        string | null
  session_id:      string | null
}

// Guaranteed by ALL interfaces:
// 1. Cache entries never served cross-tenant
// 2. Memory entries never retrieved cross-tenant
// 3. Telemetry segmented by tenant_id; no cross-tenant aggregation
// 4. Cost accounting isolated per tenant
// 5. Policy lookup scoped to tenant
// 6. Provider credentials isolated per tenant where supported
// 7. Audit logs carry tenant_id; queries scoped to tenant
```

### 21.4 PII / Sensitive Data Interface

```
interface PIIClassifier {
  classify(content: string, context: ClassificationContext) -> PIIClassificationResult
  redact(content: string, result: PIIClassificationResult) -> RedactedContent
}

PIIClassificationResult {
  contains_pii:              boolean
  pii_categories:            PIICategory[]
  classification_confidence: float
}

PIICategory {
  category:   enum { NAME, EMAIL, PHONE, ADDRESS, SSN, CREDIT_CARD,
                     HEALTH_INFO, FINANCIAL_INFO, CREDENTIALS, CUSTOM }
  spans:      ContentSpan[]
  confidence: float
}

RedactedContent {
  content:          string    // PII replaced with [REDACTED-{CATEGORY}]
  redaction_map:    ContentSpan[]
  is_safe_to_cache: boolean
  is_safe_to_log:   boolean
}
```


---

## 22. Fallback / Recovery Interface

*Traceability: PS §11; ARCH §27; NFR-011, NFR-012*

### 22.1 Fallback Strategy

```
FallbackStrategy {
  strategy_id:     string
  default_action:  FallbackAction
  stage_fallbacks: map<string, FallbackAction>   // stage_id -> action
  max_fallbacks:   integer
  alert_on_fallback: boolean
}

FallbackAction {
  action:     enum { RESTORE_ORIGINAL, INCREASE_CONTEXT, INCREASE_REASONING,
                     ESCALATE_MODEL, DISABLE_STAGE, RETRY_WITH_PARAMS, REJECT }
  parameters: map<string, any>
  max_cost:   float | null
}
```

### 22.2 Per-Stage Fallback Requirements

| Stage | Default Fallback | Fail-Safe? |
|---|---|---|
| T0.x — Request normalization | REJECT (always required) | No |
| T1.x — Context pruning | RESTORE_ORIGINAL | Yes (fail-open) |
| T1.x — Context compression | RESTORE_ORIGINAL | Yes (fail-open) |
| T1.x — Context deduplication | RESTORE_ORIGINAL | Yes (fail-open) |
| T2.x — Exact cache lookup | MISS (proceed) | Yes |
| T2.x — Semantic cache lookup | MISS (proceed) | Yes |
| T3.x — Model routing | DEFAULT_MODEL | Yes |
| T3.x — LLM invocation | Provider cascade | Yes |
| OI.x — Optimization decision | PROCEED_WITHOUT_OPTIMIZATION | Yes |
| CE.x — Context Engine | PASSTHROUGH | Yes |
| TE.x — Tool executor | SKIP_TOOL | Yes |
| AL.x — Agent loop | EXIT_LOOP | Yes |
| QV.x — Quality validation | WARN_ONLY (configurable) | Configurable |
| SEC.x — PII scrubbing required | REJECT (fail-closed) | No |

### 22.3 ReversibilityRecord (Global)

When any optimization stage modifies or removes content, it MUST emit a `ReversibilityRecord` that allows the original content to be restored if subsequent quality validation fails.

```
// Already defined in §4 — referenced here for cross-cutting context
// ReversibilityRecord is the universal undo mechanism for optimization stages
// Recovery Pointer Protocol:
//   - recovery_pointer: URI or content hash identifying the original
//   - expires_at: How long the record is retained
//   - All reversibility records share a namespace keyed by request_id
```

---

## 23. Event Interface

*Traceability: ARCH §33 (Event Bus)*

### 23.1 ControlPlaneEvent

```
ControlPlaneEvent {
  event_id:     string
  event_type:   string     // e.g., "CACHE_INVALIDATED", "MODEL_ROUTED"
  event_source: string     // component_id
  timestamp:    ISO8601 string
  request_id:   string
  tenant_id:    string
  payload:      map<string, any>
  schema_version: string
}
```

### 23.2 Standard Event Types

| Event Type | Source Component | Payload (key fields) |
|---|---|---|
| `CACHE_HIT` | T2.x-CACHE | `cache_type`, `similarity_score` |
| `CACHE_MISS` | T2.x-CACHE | `miss_reason`, `cache_type` |
| `CACHE_INVALIDATED` | T2.x-CACHE | `key`, `reason`, `cascade_count` |
| `CONTEXT_PRUNED` | T1.x-PRUNER | `tokens_removed`, `quality_impact` |
| `CONTEXT_COMPRESSED` | T1.x-COMPRESSOR | `tokens_before`, `tokens_after`, `ratio` |
| `MODEL_SELECTED` | T3.x-ROUTER | `model_id`, `reason`, `confidence` |
| `MODEL_ESCALATED` | T3.x-CASCADE | `from_model`, `to_model`, `trigger` |
| `TOOL_INVOKED` | TE.x-EXECUTOR | `tool_id`, `cached`, `filtered` |
| `TOOL_SKIPPED` | TE.x-EXECUTOR | `tool_id`, `reason`, `expected_gain` |
| `AGENT_EARLY_EXIT` | AL.x-LOOP | `reason`, `tokens_saved`, `iteration` |
| `SUB_AGENT_SPAWNED` | AL.x-SPAWN | `spawn_id`, `objective`, `cost_estimate` |
| `QUALITY_VIOLATION` | QV.x-VALIDATOR | `violation_type`, `fallback_action` |
| `POLICY_VIOLATION` | POLICY-ENFORCER | `policy_id`, `rule_id`, `severity` |
| `PROVIDER_UNAVAILABLE` | T3.x-ROUTER | `provider_id`, `fallback_triggered` |

### 23.3 Event Delivery Requirements

| Requirement | Specification |
|---|---|
| Ordering | Best-effort within `request_id` scope |
| Durability | Critical events (AUDIT, POLICY_VIOLATION) must be durably written |
| Fan-out | Multiple subscribers per event type |
| Replay | Audit events must be replayable (append-only store) |
| Cross-tenant | Events must not be delivered across tenant boundaries |
| PII | Event payloads must not include raw PII |

---

## 24. Versioning and Compatibility

### 24.1 Schema Version Policy

```
MAJOR.MINOR.PATCH

MAJOR: Breaking change (field removal, type change, required field added)
MINOR: Additive change (new optional field, new enum value)
PATCH: Non-functional (description, documentation)
```

### 24.2 Version Compatibility Matrix

| Schema Version | Compatible With | Migration Required |
|---|---|---|
| 1.0.x | 1.0.y (y >= x) | No |
| 1.0.x | 2.0.x | Yes — major version migration |
| 1.0.x | 0.9.x | Not supported |

### 24.3 Deprecation Protocol

1. Field marked `deprecated_in: "1.2.0"` and `removal_planned: "2.0.0"`
2. Producer continues to emit deprecated field for one MAJOR version
3. Consumer must not rely on deprecated fields in new code
4. Breaking change in changelog with migration guide

### 24.4 API Version Negotiation

```
// Clients send: Accept-Version: "1.*"
// Control Plane responds: Schema-Version: "1.3.0"
// If version unsupported: HTTP 406 + {"min_version": "1.0.0", "max_version": "2.1.0"}
```

---

## 25. Failure Semantics

### 25.1 Error Schema

```
ControlPlaneError {
  error_id:         string
  error_code:       string   // e.g., "OPT-4001"
  error_class:      enum { VALIDATION, POLICY, PROVIDER, TIMEOUT, CAPACITY,
                            INTERNAL, SECURITY, CONFIGURATION }
  message:          string
  retryable:        boolean
  retry_after_ms:   integer | null
  fallback_applied: boolean
  fallback_action:  string | null
  request_id:       string
  span_id:          string
  timestamp:        ISO8601 string
  details:          map<string, any>
}
```

### 25.2 Error Code Taxonomy

| Code Series | Class | Notes |
|---|---|---|
| OPT-1xxx | VALIDATION | Request schema invalid |
| OPT-2xxx | POLICY | Request blocked by policy |
| OPT-3xxx | SECURITY | Auth/PII failure |
| OPT-4xxx | PROVIDER | LLM provider unavailable |
| OPT-5xxx | TIMEOUT | Stage exceeded timeout |
| OPT-6xxx | CAPACITY | Throttling / rate limit |
| OPT-7xxx | INTERNAL | Unexpected internal error |
| OPT-8xxx | CONFIGURATION | Bad policy or config |

### 25.3 Partial Success

```
PartialSuccess {
  status:           enum { PARTIAL }
  successful_stages: string[]
  failed_stages:     string[]
  fallback_stages:   string[]
  result:            any         // Best available result
  quality_degraded:  boolean
  quality_delta:     float | null
  errors:            ControlPlaneError[]
}
```

---

## 26. Idempotency / Retry / Cancellation

### 26.1 Idempotency Requirements

| Interface | Idempotency Key | Notes |
|---|---|---|
| `ControlPlaneRequest` | `request_id` | Same result for same key |
| `CacheWriteRequest` | `idempotency_key` | Deduped within TTL |
| `ToolInvocationRequest` | `tool_call_id` | Non-idempotent tools: `idempotent: false` flag must be set |
| `SubAgentSpawnRequest` | `spawn_id` | Duplicate spawn ignored |
| `MemoryEntry` write | `entry_id` | Upsert semantics |
| `OptimizationDecisionRequest` | `request_id` + `plan_id` | Same input = same plan |

### 26.2 Retry Policy

```
RetryPolicy {
  max_attempts:        integer    // Default: 3
  initial_delay_ms:    integer    // Default: 100
  backoff_multiplier:  float      // Default: 2.0
  max_delay_ms:        integer    // Default: 5000
  jitter:              boolean    // Default: true
  retryable_codes:     string[]   // e.g., ["OPT-4001", "OPT-5001", "OPT-6001"]
}
```

### 26.3 Cancellation

Every async operation accepts a `cancellation_token`. On cancellation:
1. Ongoing LLM call is cancelled at the provider (if provider supports it)
2. All stages in-flight are terminated
3. Reversibility records are retained until expiry
4. Partial cost is recorded
5. A `CANCELLED` event is emitted

---

## 27. Tenancy and Isolation

### 27.1 Isolation Boundaries

| Resource | Isolation Scope | Enforcement Method |
|---|---|---|
| Cache namespace | `tenant_id` | Cache key prefix; query filter |
| Memory store | `tenant_id` | Row-level filter; namespace prefix |
| Cost accounting | `tenant_id` + `organization_id` | Per-tenant ledger |
| Policy store | `tenant_id` | Policy lookup always tenant-scoped |
| Audit log | `tenant_id` | Write path includes `tenant_id`; read requires `tenant_id` filter |
| Telemetry | `tenant_id` | Label on all metrics; no cross-tenant aggregation |
| Secrets / credentials | Provider contract | No cross-tenant credential sharing |
| Model access | Policy + auth | Model allowlist per tenant |

### 27.2 Cross-Tenant Prohibition

The following operations are **unconditionally prohibited** at the interface level:

- Serving a cache entry where `entry.tenant_id != request.tenant_id`
- Serving a memory entry where `entry.tenant_id != request.tenant_id`
- Including cost from one tenant in another tenant's report
- Propagating events across `tenant_id` boundaries
- Sharing policy objects across `tenant_id` boundaries without explicit multi-tenant policy

---

## 28. Cost Accounting Interface

*Traceability: PS §8 (Token and Cost Accounting); PS §11 (AC-001–022, incl. AC-005); ARCH §27, §27.1; ARCH §32; CONV §14.1; OBJ-009; OBJ-011*

### 28.1 CostLedgerEntry

> **Correction (2026-09-23, AC-005 contract reconciliation):** `CostLedgerEntry` previously did not represent the complete ARCH §27.1 Standard Ledger Field model that PS §8 ("Required fields include"), SPEC §18.1 ("Required Ledger Fields") and CONV §14.1 ("carrying all standard ledger fields defined in ARCH §27.1") require. This was a frozen-source contract inconsistency, resolved by the documented precedence in CONV's header (Problem Statement > Engineering Specification > Architecture > Interfaces). The correction is additive:
> - **Canonical ledger representation:** the nine nested groups below (`input` … `quality`) are the canonical representation of the 58 ARCH §27.1 Standard Ledger Fields. Each group corresponds to one §27.1 group (INPUT, OUTPUT, CACHE, MODEL, TOOLS, WORKFLOW, COST, PERFORMANCE, QUALITY). Each member is the lower_snake_case suffix of its §27.1 `category.field` identifier; the logical §27.1 name is recorded beside every member.
> - **Existing fields retained unchanged:** all 29 pre-existing fields (identity, TOKEN ECONOMY, COST, ATTRIBUTION) are retained for interface compatibility. **None of them is declared equivalent to, an alias of, or a replacement for any §27.1 field**, because no authoritative source establishes such a correspondence. Similar names (e.g. `net_savings` / `cost.net_savings`) and ARCH §27.3 formula terms do not establish equivalence.
> - **Types:** CONV §3.6 (token counts `integer`; monetary amounts `float` in `currency`) and CONV §5.2 (`tokens_*` → `integer`; `cost_*`/`*_cost`/`*_savings` → `float` with `currency` context; `score` → `float` in `[0.0, 1.0]`; `latency_ms` → `integer` milliseconds) type 36 members. §5.2's rules are applied to the §27.1 logical name, and the `latency_ms` rule is read as applying to fields whose name ends in `latency_ms`. The other 22 members, originally `SOURCE-UNRESOLVED`, are typed by explicit human operator decisions **HD-1 to HD-3** (marked `[HD-n]` in the schema; see "Resolved decisions" below). No enum, object, or domain type is invented; in particular, `model.reasoning_budget` is a `string` and does **not** reuse `OptimizationPlan.reasoning_budget`'s enum.
> - **Availability and `unverified` (HD-4):** PS §8 and SPEC §18.1 qualify two fields as "where available": "Reasoning tokens where available" and "Tool cost where available". **Only** those two members, `model.reasoning_tokens` and `cost.tool`, are nullable (`| null`, per CONV §5.4: "`null` means the field is applicable but has no value in this context"). A `null` in either of them does **not**, by itself, make the entry `unverified`. Every other member is non-nullable and must carry a measured value. A non-nullable member that cannot be measured is a **measurement verification failure**: the entry is marked `unverified` (`verified = false`), alongside accounting failures under CONV §14.4. An unavailable measurement is never presented or counted as a measured value.

```
CostLedgerEntry {
  entry_id:        string
  request_id:      string
  tenant_id:       string
  organization_id: string
  agent_id:        string | null
  session_id:      string | null
  task_id:         string
  timestamp:       ISO8601 string

  // TOKEN ECONOMY
  baseline_input_tokens:      integer
  baseline_output_tokens:     integer
  actual_input_tokens:        integer
  actual_output_tokens:       integer
  cached_tokens:              integer
  tokens_avoided_by_pruning:  integer
  tokens_avoided_by_cache:    integer
  tokens_avoided_by_early_exit: integer
  optimizer_overhead_tokens:  integer

  // COST
  baseline_cost:      float
  actual_cost:        float
  cached_cost_saved:  float
  optimizer_cost:     float
  gross_savings:      float
  net_savings:        float
  net_savings_pct:    float

  // ATTRIBUTION
  savings_by_stage: map<string, float>    // stage_id -> savings
  currency:         string
  pricing_version:  string
  model_id:         string
  provider_id:      string

  // ---- CANONICAL ARCH §27.1 STANDARD LEDGER FIELDS (nested, category-preserving) ----
  // Member name = lower_snake_case suffix of the §27.1 identifier shown in each comment.

  input: {                                   // §27.1 INPUT
    raw_input:          integer              // tokens.raw_input
    sanitized:          integer              // tokens.sanitized
    query_compressed:   integer              // tokens.query_compressed      (AC-005: compressed)
    context:            integer              // tokens.context
    retrieved:          integer              // tokens.retrieved             (AC-005: retrieved)
    pruned:             integer              // tokens.pruned
    deduplicated:       integer              // tokens.deduplicated
    context_compressed: integer              // tokens.context_compressed    (AC-005: compressed)
    cached_input:       integer              // tokens.cached_input          (AC-005: cached)
    uncached_input:     integer              // tokens.uncached_input
  }

  output: {                                  // §27.1 OUTPUT
    raw_output:         integer              // tokens.raw_output
    optimized_output:   integer              // tokens.optimized_output
    truncated:          integer              // tokens.truncated
    expanded_retry:     integer              // tokens.expanded_retry
  }

  cache: {                                   // §27.1 CACHE
    exact_hits:         integer              // cache.exact_hits  [HD-1]
    semantic_hits:      integer              // cache.semantic_hits  [HD-1]
    misses:             integer              // cache.misses  [HD-1]
    writes:             integer              // cache.writes  [HD-1]
    reads:              integer              // cache.reads  [HD-1]
    cacheable_tokens:   integer              // cache.cacheable_tokens
    reused_tokens:      integer              // cache.reused_tokens          (AC-005: reused)
  }

  model: {                                   // §27.1 MODEL
    selected:           string               // model.selected  [HD-2]
    candidate:          string               // model.candidate  [HD-2]
    routing_decision:   string               // model.routing_decision  [HD-2]
    escalation:         boolean              // model.escalation  [HD-2]
    reasoning_budget:   string               // model.reasoning_budget  [HD-2]
    reasoning_tokens:   integer | null       // model.reasoning_tokens       (PS §8: "where available")
  }

  tools: {                                   // §27.1 TOOLS
    calls_attempted:    integer              // tools.calls_attempted  [HD-1]
    calls_avoided:      integer              // tools.calls_avoided  [HD-1]
    output_tokens:      integer              // tools.output_tokens
    filtered_tokens:    integer              // tools.filtered_tokens
    cached_calls:       integer              // tools.cached_calls  [HD-1]
  }

  workflow: {                                // §27.1 WORKFLOW
    steps_planned:      integer              // workflow.steps_planned  [HD-1]
    steps_executed:     integer              // workflow.steps_executed  [HD-1]
    steps_skipped:      integer              // workflow.steps_skipped  [HD-1]
    early_exits:        integer              // workflow.early_exits  [HD-1]
    retries:            integer              // workflow.retries  [HD-1]
  }

  cost: {                                    // §27.1 COST  (float amounts in `currency`, CONV §3.6/§5.2)
    input:              float                // cost.input
    output:             float                // cost.output
    cache:              float                // cost.cache
    compression:        float                // cost.compression
    tool:               float | null         // cost.tool                    (PS §8: "where available")
    total_optimized:    float                // cost.total_optimized
    baseline_estimated: float                // cost.baseline_estimated
    net_savings:        float                // cost.net_savings
    savings_pct:        float                // cost.savings_pct  (ARCH §27.3: Net Savings / Baseline Cost x 100 — a percentage, no currency)
  }

  performance: {                             // §27.1 PERFORMANCE
    e2e_latency_ms:         integer          // perf.e2e_latency_ms
    ttft_ms:                integer           // perf.ttft_ms  (milliseconds)  [HD-3]
    model_latency_ms:       integer          // perf.model_latency_ms
    compression_latency_ms: integer          // perf.compression_latency_ms
    cache_latency_ms:       integer          // perf.cache_latency_ms
    tool_latency_ms:        integer          // perf.tool_latency_ms
  }

  quality: {                                 // §27.1 QUALITY
    correctness_score:      float            // quality.correctness_score    ([0.0, 1.0], CONV §5.2)
    relevance_score:        float            // quality.relevance_score      ([0.0, 1.0], CONV §5.2)
    schema_compliance:      boolean           // quality.schema_compliance  [HD-3]
    semantic_preservation:  float             // quality.semantic_preservation  ([0.0, 1.0])  [HD-3]
    user_task_score:        float            // quality.user_task_score      ([0.0, 1.0], CONV §5.2)
    safety_validation:      boolean           // quality.safety_validation  [HD-3]
  }
}
```

**AC-005 coverage:** every AC-005 category is distinctly representable through the canonical groups.

| AC-005 category | Representation | Basis |
|---|---|---|
| cached | `input.cached_input` | Explicit — §27.1 `tokens.cached_input` |
| compressed | `input.query_compressed`, `input.context_compressed` | Explicit — §27.1 names contain "compressed" |
| retrieved | `input.retrieved` | Explicit — §27.1 `tokens.retrieved` |
| reused | `cache.reused_tokens` | Explicit — §27.1 `cache.reused_tokens` |
| removed | `input.pruned`, `input.deduplicated`, `output.truncated` | Semantic — no §27.1 field is named "removed" |
| generated | `output.raw_output`, `output.optimized_output` | Semantic — no §27.1 field is named "generated" |

**Resolved decisions** *(originally recorded as open decisions; resolved 2026-09-23 by explicit human operator decisions HD-1 to HD-4 during `REM-P0.1.B-01`, promoted into this contract as DB-2)*:
- **OD-28.1-A — unresolved types → CLOSED.** The 22 members no authoritative source typed are now typed as follows. CONV §3.6/§5.2 still cover only token counts, monetary/savings amounts, scores and `latency_ms`, and same-named fields in other schemas were not used as type evidence.
  - **HD-1:** the 13 counters are `integer` non-negative event/step counts — `cache`: `exact_hits`, `semantic_hits`, `misses`, `writes`, `reads`; `tools`: `calls_attempted`, `calls_avoided`, `cached_calls`; `workflow`: `steps_planned`, `steps_executed`, `steps_skipped`, `early_exits`, `retries`. A measured zero is a legitimate measured value.
  - **HD-2:** `model.selected`, `model.candidate`, `model.routing_decision` (a provider/model-neutral decision label; no routing enum) and `model.reasoning_budget` are `string`; `model.escalation` is `boolean` (whether escalation occurred). The `OptimizationPlan.reasoning_budget` enum `{LOW, MEDIUM, HIGH, MAX}` is **not** reused.
  - **HD-3:** `performance.ttft_ms` is `integer` milliseconds; `quality.semantic_preservation` is a `float` score in `[0.0, 1.0]`; `quality.schema_compliance` and `quality.safety_validation` are `boolean` validation outcomes, not scores.
- **OD-28.1-B — availability vs. `unverified` → CLOSED (HD-4).** Only `model.reasoning_tokens` and `cost.tool` are nullable, and a `null` there does not by itself make the entry `unverified` ("measurement not available for a where-available field" ≠ "accounting verification failed"). `unverified` / `verified = false` is reserved for an actual accounting failure (CONV §14.4) or a measurement verification failure, meaning a non-nullable member that cannot be measured.
- **Unmeasurable non-nullable members (DB-1, the approved `REM-P0.1.B-01` design).** Such a member may hold a placeholder (`0` / `0.0` / `false` / `"unknown"`) **only** in an entry marked `verified = false`. The placeholder is never presented, reported, or counted as a measured value (root rule 5; CONV §14.4).

### 28.2 Cost Reporting Interface

```
interface CostReporter {
  get_request_cost(request_id: string, tenant_id: string)           -> CostLedgerEntry | null
  get_session_cost(session_id: string, tenant_id: string)           -> SessionCostSummary
  get_tenant_cost(tenant_id: string, period: DateRange)             -> TenantCostSummary
  get_cost_breakdown_by_stage(request_id: string, tenant_id: string) -> map<string, float>
}
```


---

## 29. Decision Explanation Interface

*Traceability: PS §33 (OI-001–005); ARCH §14; OBJ-012*

### 29.1 ExplanationRecord

```
ExplanationRecord {
  explanation_id: string
  request_id:     string
  plan_id:        string
  tenant_id:      string
  timestamp:      ISO8601 string

  optimization_summary:  string
  decision_rationale:    DecisionRationale
  stages_applied:        StageExplanation[]
  stages_skipped:        SkippedStage[]
  cost_breakdown:        CostLedgerEntry
  quality_impact_summary: QualityImpactSummary
  policy_constraints:    string[]
  model_routing_rationale: RoutingRationale
}

StageExplanation {
  stage_id:                string
  stage_display_name:      string
  technique:               string
  tokens_before:           integer
  tokens_after:            integer
  tokens_removed:          integer
  optimization_cost:       float
  quality_delta_estimate:  float
  confidence:              float
  fallback_triggered:      boolean
  fallback_reason:         string | null
  rationale:               string
}

QualityImpactSummary {
  overall_quality_delta:  float
  degradation_detected:   boolean
  degradation_stages:     string[]
  validation_passed:      boolean
  validators_run:         string[]
  confidence:             float
}

RoutingRationale {
  selected_model_id:       string
  selected_provider_id:    string
  evaluated_models:        ModelEvaluationSummary[]
  routing_signals_used:    map<string, any>
  estimated_cost_delta:    float
  estimated_quality_delta: float
}

ModelEvaluationSummary {
  model_id:           string
  provider_id:        string
  score:              float
  rejection_reason:   string | null
  estimated_cost:     float
  estimated_quality:  float
}
```

---

## 30. Contract Testing

*Traceability: PS §24 (NFR-010); ARCH §34*

### 30.1 Contract Test Requirements

Every interface defined in this document MUST have a corresponding contract test suite that:
1. Validates all REQUIRED fields
2. Validates type constraints
3. Validates enum values
4. Tests backward compatibility with previous MINOR version
5. Tests idempotency (same input = same output, or graceful dedup)
6. Tests fallback behavior (force stage failure; verify fallback)
7. Tests tenant isolation (cross-tenant access attempt must fail)
8. Tests policy enforcement (blocked operation returns OPT-2xxx)

### 30.2 Contract Test Interface

```
interface ContractTestRunner {
  run_schema_tests(interface_id: string, schema_version: string)    -> ContractTestResult
  run_compatibility_tests(from_version: string, to_version: string) -> CompatibilityTestResult
  run_isolation_tests(tenant_a: string, tenant_b: string)           -> IsolationTestResult
  run_idempotency_tests(interface_id: string)                       -> IdempotencyTestResult
  run_fallback_tests(stage_id: string)                              -> FallbackTestResult
}

ContractTestResult {
  interface_id:     string
  schema_version:   string
  tests_run:        integer
  tests_passed:     integer
  tests_failed:     integer
  failures:         ContractTestFailure[]
  timestamp:        ISO8601 string
}

ContractTestFailure {
  test_id:       string
  test_name:     string
  failure_type:  enum { SCHEMA, COMPATIBILITY, ISOLATION, IDEMPOTENCY, FALLBACK }
  message:       string
  expected:      any
  actual:        any
}
```

### 30.3 Consumer-Driven Contract Tests

Every consumer of an interface (e.g., adapter, optimizer module, agent framework adapter) MUST publish a pact describing its expectations. The producer MUST satisfy all published consumer pacts before a version is promoted.

---

## 31. Non-Functional Interface Requirements

*Traceability: PS §24 (NFR-001–013); ARCH §35*

### 31.1 Performance Budget per Stage

| Stage Class | Max Overhead Latency | Max Overhead Tokens | Notes |
|---|---|---|---|
| T0.x — Normalization | < 10 ms | 0 | Pure parsing; no LLM |
| T1.x — Context Ops | < 50 ms | < 500 | LLM-free by default |
| T2.x — Cache Lookup | < 20 ms | 0 | Must not slow hot path |
| T2.x — Semantic Cache | < 100 ms | 0 | Embedding lookup |
| OI.x — Decision | < 30 ms | < 200 | Policy evaluation |
| CE.x — Reordering | < 30 ms | 0 | Sort only |
| T3.x — Routing | < 20 ms | 0 | Profile lookup |
| TE.x — Tool Selection | < 50 ms | 0 | Registry lookup |
| QV.x — Validation | < 200 ms | < 1000 | Configurable |
| AL.x — Loop control | < 10 ms | 0 | Pure evaluation |
| Total budget | < 500 ms | < 2000 | End-to-end optimizer |

### 31.2 Availability Requirements

| Tier | Component | SLA |
|---|---|---|
| Critical | Core request path (T0.x → T3.x) | 99.9% |
| High | Cache (T2.x), Memory (CL-006) | 99.5% |
| Standard | Evaluation (EL.x), Reporting (AC.x) | 99.0% |
| Best-effort | Policy Learning (EL-004) | 95.0% |

### 31.3 Throughput Requirements

| Workload | Minimum Throughput |
|---|---|
| Interactive single-tenant | 100 req/s |
| Batch multi-tenant | 1000 req/s |
| Telemetry event bus | 50,000 events/s |
| Audit log write | 10,000 records/s |

### 31.4 Data Retention

| Data Type | Minimum Retention | Maximum Retention |
|---|---|---|
| Audit records | 7 years | Unlimited |
| Cost ledger | 3 years | Unlimited |
| Optimization trace | 90 days | Configurable |
| Cache entries | Per TTL | Per policy |
| Memory (TURN layer) | Session lifetime | Configurable |
| Reversibility records | 24 hours | Configurable |
| Policy version history | 5 years | Unlimited |

---

## 32. Reference Data Flows

### 32.1 Happy Path: Coding Agent Request

```
[Coding Agent]
    |-- DeveloperAgentRequest -->
[T0: Normalization]
    |-- ControlPlaneRequest -->
[OI: Optimization Decision]
    |-- OptimizationPlan -->
[T2: Cache Lookup]
    |-- CacheLookupResult (MISS) -->
[CE: Context Engine]
    |-- Dep Graph + Budget Allocation -->
[T1: Context Ops]
    |-- Prune(compiler errors, symbols) -->
    |-- Dedup(files, tool outputs) -->
    |-- Reorder(anti-lost-in-middle) -->
    |-- CompressionResult -->
[TE: Tool Selection]
    |-- relevant tools only (minimized schemas) -->
[T3: Model Routing]
    |-- ModelRoutingResult -->
[LLM Provider]
    |-- LLMInvocationResult -->
[QV: Validation]
    |-- ValidationResult (PASS) -->
[T2: Cache Write]
    |-- CacheWriteResult -->
[AC: Cost Accounting]
    |-- CostLedgerEntry -->
[OBS: Telemetry]
    |-- Spans + Metrics + Events -->
[Coding Agent]
    <-- OptimizedResponse
```

### 32.2 Semantic Cache Hit Flow

```
[Agent] --> ControlPlaneRequest
[T2: Semantic Cache] --> CacheLookupResult (HIT, similarity=0.97)
  AUTH CHECK: auth_check_passed=true
  FRESHNESS CHECK: freshness_valid=true
  TENANT CHECK: tenant_id matches
  POLICY CHECK: cache_allowed=true
[T2] --> CacheEntry.content returned directly
[AC] --> CostLedgerEntry (tokens_avoided_by_cache recorded)
[OBS] --> CACHE_HIT event emitted
[Agent] <-- cached response (no LLM call)
```

### 32.3 Sub-Agent Delegation Flow

```
[Parent Agent] --> AgentIterationReport (next_action = SPAWN_SUBAGENT)
[AL: Loop Controller]
  SubAgentSpawnDecision: value_to_cost_ratio > threshold
[AL] --> SubAgentSpawnRequest (delegated_context = SubAgentContextHandoff)
  // CRITICAL: full_transcript_excluded = true; only key findings passed
[Child Agent] --> optimize independently (new ControlPlaneRequest)
[Child Agent] --> SubAgentResultHandoff
[AL: Aggregator] --> AggregatedResult --> CompressedAgentResult
[Parent Agent] receives compressed findings; continues
[AC] records: parent_cost + child_cost + coordination_latency
```

### 32.4 Model Cascade Flow

```
[T3: Routing] --> first_tier_model (claude-haiku-3-5)
[LLM] --> LLMInvocationResult
[QV: Validation] --> confidence < cascade_trigger.min_confidence_required
[T3: Cascade] --> MODEL_ESCALATED event
  from_model: claude-haiku-3-5
  to_model: claude-sonnet-4-5
[LLM] --> LLMInvocationResult (second tier)
[QV: Validation] --> PASS
[AC] records marginal_model_value = quality_gain / additional_cost
```

---

## 33. Interface Anti-Patterns

The following patterns are **explicitly prohibited** in any interface implementation:

| Anti-Pattern | Prohibition | Correct Approach |
|---|---|---|
| Provider field in core schema | PROHIBITED | Use `provider_hints: map<string, any>` |
| Framework-specific import in optimizer | PROHIBITED | Use adapter interface |
| PII in log fields | PROHIBITED | Redact via `PIIClassifier` before logging |
| Cross-tenant cache key | PROHIBITED | Always include `tenant_id` in cache key |
| Blocking optimization stage | PROHIBITED | All stages must be fail-open (except security) |
| Non-idempotent cache write | PROHIBITED | Always use `idempotency_key` |
| Missing `tokens_avoided` metric | PROHIBITED | Always report 0 if none |
| Missing `request_id` in any response | PROHIBITED | Propagate from request |
| Hard-coded model name in optimizer | PROHIBITED | Read from `ModelProfileRegistry` |
| Serving memory cross-tenant | PROHIBITED | `tenant_id` filter enforced at storage |
| Deleting audit records | PROHIBITED | Audit records are immutable |
| Raw credentials in interface fields | PROHIBITED | Reference by secret handle only |
| Caching mutable data without TTL | PROHIBITED | TTL required for all cache entries |
| Full conversation replay to sub-agent | PROHIBITED | Use `SubAgentContextHandoff` |

---

## 34. Traceability

### 34.1 Problem Statement Traceability

| PS Section / Requirement | Interface Section(s) |
|---|---|
| PS §1 — Core objectives | §1, §31 |
| PS §2 — Optimization domains | §2, §4, §5 |
| PS §4 — Integration modes | §2.1, §11, §13, §14 |
| PS §4A — Generic LLM | §2, §3, §7 |
| PS §4B — Agentic / multi-agent | §11, §12 |
| PS §4C — Dev / coding agents | §13 |
| PS §4D — Sub-agents | §12 |
| PS §4E — RAG | §15 |
| PS §4F — MCP / tool-enabled | §10, §14 |
| PS §6.1 — Model routing | §9 |
| PS §6.7 — Exact cache | §6 |
| PS §6.8 — Semantic cache | §6 |
| PS §6.9 — Retrieval-augmented context | §5.2, §15 |
| PS §6.14 — Deduplication | §5.4 |
| PS §6.15 — Pruning | §5.3 |
| PS §6.16 — Compression | §5.5 |
| PS §6.20 — Agent loop control | §11.3 |
| PS §6.21 — Tool selection | §10 |
| PS §6.22 — Tool result caching | §6, §10.2 |
| PS §6.23 — Model cascade | §9.2 |
| PS §9 — Quality validation | §17 |
| PS §10 — Security / governance | §20, §21 |
| PS §11 — Cost accounting | §28 |
| PS §12 — Observability | §19 |
| PS §13 — Provider adapters | §7, §8 |
| PS §14 — Evaluation | §18 |
| PS §17 — Provider neutrality | §1.1, §7, §8 |
| PS §18 — Model profiles | §8 |
| PS §23 — Compliance | §20, §21, §27 |
| PS §24 — NFRs | §31 |
| PS §32 — Multi-tenancy | §27 |
| PS §33 — OI requirements | §3, §29 |
| PS §34 — CL requirements | §5, §16 |
| PS §51 — Dynamic execution state | §42 |
| PS §52 — Operating model, governance, and trust boundaries (H01–H20) | §43 |

### 34.2 Engineering Specification and Architecture Traceability (Hardening Pass)

*Added 2026-09-16. §1–§42 were drawn directly from the Problem Statement and an early draft of `architecture.md`, predating the three-tier PS → Engineering Spec → Architecture → Interfaces authority chain now established (see the foundational documents' Final Foundational Document Reconciliation, 2026-09-16); their PS-only traceability format in §34.1 is preserved as-is rather than retrofitted, since every PS section they cite is itself faithfully carried into EAIOC-SPEC-001 and `architecture.md` without renumbering. §43 (this amendment) is traced directly to all three:*

| Component | PS | EAIOC-SPEC-001 | architecture.md |
|---|---|---|---|
| SGE | §52.5 | §42.5 | §47.2.1 |
| DGE | §52.7 | §42.7 | §47.2.2 |
| TMG | §52.11 | §42.11 | §47.2.3 |
| HAG | §52.13 | §42.13 | §47.2.4 |
| CIS | §52.14 | §42.14 | §47.2.5 |
| FTR | §52.8 | §42.8 | §47.10 |
| XEC | §52.12 | §42.12 | §47.3.2 |
| SPC | §52.3 | §42.3 | §47.4.1 |
| VCL | §52.6 | §42.6 | §47.4.3 |

---

## 35. Implementation Boundary

This document defines **what** each component exposes and **what contracts** it guarantees. It does **not** define:

- Internal algorithms or data structures within components
- Specific storage engines, databases, or caching technologies
- Network transport protocols (HTTP/2, gRPC, NATS, etc.)
- Language-specific type bindings
- Deployment topology (containers, serverless, etc.)
- CI/CD pipelines or release processes

These concerns belong in downstream documents — primarily `conventions.md` (naming, module layout, package structure) and `implementation-plan.md` (deployment topology, CI/CD, sequencing) — not in this document or in the already-existing, deliberately implementation-neutral Engineering Specification (EAIOC-SPEC-001). *(Corrected 2026-09-16: this line previously cited a document ID, "EAIOC-ENGR-001," that does not exist; see §41 for the full correction.)*

---

## 36. Interface Inventory

| Interface ID | Interface Name | Section | Defined Types |
|---|---|---|---|
| INTF-001 | ControlPlaneRequest | §2 | 15 schemas |
| INTF-002 | OptimizationDecisionRequest / OptimizationPlan | §3 | 8 schemas |
| INTF-003 | OptimizationModule | §4 | 9 schemas |
| INTF-004 | ContextItem | §5.1 | 1 schema |
| INTF-005 | ContextRetriever | §5.2 | 3 schemas |
| INTF-006 | ContextPruner | §5.3 | 3 schemas |
| INTF-007 | ContextDeduplicator | §5.4 | 3 schemas |
| INTF-008 | ContextCompressor | §5.5 | 4 schemas |
| INTF-009 | ContextReorderer | §5.6 | 2 schemas |
| INTF-010 | ContextBudgeter | §5.7 | 4 schemas |
| INTF-011 | ContextDependencyGraph | §5.8 | 5 schemas |
| INTF-012 | CacheStore | §6 | 7 schemas |
| INTF-013 | LLMProvider | §7 | 12 schemas |
| INTF-014 | ModelProfile | §8.1 | 1 schema |
| INTF-015 | ModelProfileRegistry | §8.2 | 2 schemas |
| INTF-016 | ModelRoutingRequest / Result | §9 | 7 schemas |
| INTF-017 | ToolDefinition | §10.1 | 1 schema |
| INTF-018 | ToolRegistry | §10.2 | 2 schemas |
| INTF-019 | ToolExecutor | §10.3 | 4 schemas |
| INTF-020 | AgentRegistration | §11.1 | 2 schemas |
| INTF-021 | AgentTaskSubmission / Report | §11.2 | 5 schemas |
| INTF-022 | AgentEarlyExitEvaluator | §11.3 | 2 schemas |
| INTF-023 | SubAgentSpawnRequest / Handoff | §12 | 6 schemas |
| INTF-024 | DeveloperAgentRequest extension | §13.1 | 12 schemas |
| INTF-025 | CodingAgentLoopController | §13.3 | 2 schemas |
| INTF-026 | MCPAdapter | §14 | 4 schemas |
| INTF-027 | RAGPipeline | §15 | 4 schemas |
| INTF-028 | MemoryStore / MemoryEntry | §16 | 3 schemas |
| INTF-029 | QualityValidator | §17 | 5 schemas |
| INTF-030 | EvaluationFramework | §18 | 3 schemas |
| INTF-031 | ControlPlaneLogEntry | §19.1 | 1 schema |
| INTF-032 | Standard Metrics | §19.2 | 13 metrics |
| INTF-033 | OptimizationSpan | §19.3 | 2 schemas |
| INTF-034 | AuditRecord | §19.5 | 1 schema |
| INTF-035 | OptimizationPolicy | §20 | 2 schemas |
| INTF-036 | PolicyEnforcer | §20.2 | 3 schemas |
| INTF-037 | AuthenticationContext | §21.1 | 1 schema |
| INTF-038 | AuthorizationService | §21.2 | 3 schemas |
| INTF-039 | TenantIsolationContext | §21.3 | 1 schema |
| INTF-040 | PIIClassifier | §21.4 | 4 schemas |
| INTF-041 | FallbackStrategy | §22 | 2 schemas |
| INTF-042 | ControlPlaneEvent | §23 | 1 schema |
| INTF-043 | ControlPlaneError / PartialSuccess | §25 | 3 schemas |
| INTF-044 | RetryPolicy | §26.2 | 1 schema |
| INTF-045 | ExplanationRecord | §29 | 5 schemas |
| INTF-046 | ContractTestRunner | §30 | 3 schemas |
| INTF-047 | CostLedgerEntry | §28 | 1 schema |
| INTF-048 | CostReporter | §28.2 | 1 schema |
| INTF-049 | TenantIsolationBoundary | §27 | Invariants |
| INTF-050 | ExecutionStateManager (ESM) | §42.1 | 6 schemas |
| INTF-051 | ContextVersionManager (CVM) | §42.2 | 6 schemas |
| INTF-052 | WorkflowVersionManager (WVM) | §42.3 | 7 schemas |
| INTF-053 | CheckpointManager (CPM) | §42.4 | 4 schemas |
| INTF-054 | ReconciliationEngine (RE) | §42.5 | 3 schemas |
| INTF-055 | ContextIntegrityGate (CIG) | §42.6 | 5 schemas |
| INTF-056 | ContextExpansionController (CEC) | §42.7 | 4 schemas |
| INTF-057 | PermissionRevalidation (PRV) | §42.8 | 3 schemas |
| INTF-058 | DynamicPolicyEvaluation (DPE) | §42.9 | 3 schemas |
| INTF-059 | CapabilityAvailabilityResolver (CAR) | §42.10 | 4 schemas |
| INTF-060 | StaleResultProtection (SRP) | §42.11 | 3 schemas |
| INTF-061 | SupersessionManager (SPM) | §42.12 | 2 schemas |
| INTF-062 | RecoveryCoordinator (RCO) | §42.13 | 3 schemas |
| INTF-063 | SpendGovernanceEngine (SGE) | §43.1 | 4 schemas |
| INTF-064 | DataGovernanceEngine (DGE) | §43.2 | 6 schemas |
| INTF-065 | ToolMCPTrustGate (TMG) | §43.3 | 4 schemas |
| INTF-066 | HumanApprovalGate (HAG) | §43.4 | 6 schemas |
| INTF-067 | ContentIntegrityScreen (CIS) | §43.5 | 3 schemas |
| INTF-068 | FeasibilityTierRegistry (FTR) | §43.6 | 2 schemas |
| INTF-069 | CrossExecutionCoordinator (XEC) | §43.7 | 4 schemas |
| INTF-070 | SelfProtectionController (SPC) | §43.8 | 5 schemas |
| INTF-071 | VerifierCalibrationLayer (VCL) | §43.9 | 5 schemas |

**Total: 71 interface definitions, 260+ schema types** (INTF-001–062 unchanged; INTF-063–071 added 2026-09-16. §43.10's `OperatingMode`/`DecisionOwnership` shared enums and §3.4's `OptimizationPlan` extension fields are counted within the above interfaces' schema counts, not as a separate INTF ID, since they are cross-cutting type extensions rather than a component-owned interface.)


---

## 37. Cross-Reference Matrix

### 37.1 Component → Interface Dependency

| Component (ARCH ID) | Consumes | Produces |
|---|---|---|
| T0.x — Request Gateway | — | INTF-001 |
| OI.x — Optimization Intelligence | INTF-001, INTF-014, INTF-035 | INTF-002, INTF-045 |
| T1.x — Context Pruner | INTF-001, INTF-004 | INTF-006 result |
| T1.x — Context Deduplicator | INTF-004 | INTF-007 result |
| T1.x — Context Compressor | INTF-004 | INTF-008 result |
| T1.x — Context Reorderer | INTF-004 | INTF-009 result |
| CE.x — Context Engine | INTF-001, INTF-005–011 | AssembledContext |
| T2.x — Cache (Exact) | INTF-001 | INTF-012 result |
| T2.x — Cache (Semantic) | INTF-001, embeddings | INTF-012 result |
| T3.x — Model Router | INTF-002, INTF-014–015 | INTF-016 |
| T3.x — LLM Adapter | INTF-016 | INTF-013 result |
| TE.x — Tool Registry | INTF-001 | INTF-017–018 |
| TE.x — Tool Executor | INTF-019 | INTF-019 result |
| AL.x — Agent Loop | INTF-021, INTF-022 | INTF-021 report |
| AL.x — Sub-agent Spawn | INTF-021 | INTF-023 |
| DA.x — Coding Adapter | INTF-001 | INTF-024 |
| MCP Adapter | INTF-019 | INTF-026 |
| RAG Pipeline | INTF-005, INTF-004 | INTF-027 |
| Memory Store | — | INTF-028 |
| QV.x — Validator | INTF-029 | INTF-029 result |
| EL.x — Evaluator | INTF-002, INTF-029 | INTF-030 |
| OBS — Observability | All | INTF-031–034 |
| POLICY — Enforcer | INTF-035 | INTF-036 result |
| SEC — Auth | — | INTF-037–040 |
| AC.x — Cost Accounting | All | INTF-047–048 |
| ESM — Execution State Manager | INTF-001, INTF-035 | INTF-050 |
| CVM — Context Version Manager | INTF-004, INTF-050 | INTF-051 |
| WVM — Workflow Version Manager | INTF-021, INTF-050 | INTF-052 |
| CPM — Checkpoint Manager | INTF-050, INTF-051, INTF-052 | INTF-053 |
| RE — Reconciliation Engine | INTF-050 | INTF-054 |
| CIG — Context Integrity Gate | INTF-051 | INTF-055 |
| CEC — Context Expansion Controller | INTF-051 | INTF-056 |
| PRV — Permission Revalidation | INTF-037, INTF-053 | INTF-057 |
| DPE — Dynamic Policy Evaluation | INTF-035, INTF-050 | INTF-058 |
| CAR — Capability/Availability Resolver | INTF-014–015, INTF-050 | INTF-059 |
| SRP — Stale Result Protection | INTF-012, INTF-053 | INTF-060 |
| SPM — Supersession Manager | INTF-050, INTF-052 | INTF-061 |
| RCO — Recovery Coordinator | INTF-053–060 | INTF-062 |
| SGE — Spend Governance Engine | INTF-047 (Cost Ledger) | INTF-063 |
| DGE — Data Governance Engine | INTF-040 (PIIClassifier) | INTF-064 |
| TMG — Tool/MCP Trust Gate | INTF-017–018, INTF-038 | INTF-065 |
| HAG — Human Approval Gate | INTF-052 (WVM), INTF-050 (ESM) | INTF-066 |
| CIS — Content Integrity Screen | INTF-004, INTF-023, INTF-026 | INTF-067 |
| FTR — Feasibility Tier Registry | INTF-024 (DeveloperAgentRequest) | INTF-068 |
| XEC — Cross-Execution Coordinator | INTF-054 (RE), INTF-061 (SPM) | INTF-069 |
| SPC — Self-Protection Controller | INTF-002 (OptimizationPlan) | INTF-070, `DepthSheddingDecision` |
| VCL — Verifier Calibration Layer | INTF-029 (QualityValidator) | INTF-071 |

### 37.2 Data Flow Dependencies

```
INTF-001 (Request)
  --> INTF-002 (Decision) needs INTF-014 (Model Profiles) + INTF-035 (Policy)
  --> INTF-004 (ContextItem) flows through INTF-005–011 (Context Engine)
  --> INTF-012 (Cache) consumes INTF-001 key fields
  --> INTF-016 (Routing) consumes INTF-002 + INTF-014
  --> INTF-013 (LLM Call) consumes INTF-016 output
  --> INTF-029 (Validation) consumes INTF-013 output
  --> INTF-047 (Cost Ledger) consumes all token counts
  --> INTF-031–034 (Telemetry) consumes all interface results
```

---

## 38. Open Architectural Questions

The following questions require resolution before the Engineering Specification can be finalized:

| Q# | Question | Impact | Recommended Default |
|---|---|---|---|
| Q1 | Should semantic cache similarity threshold be per-tenant or global? | Cache safety | Per-tenant; global floor = 0.92 |
| Q2 | Which embedding model is used for semantic cache and RAG? Must be provider-neutral | Cache hit rate, latency | Pluggable; adapter interface |
| Q3 | Is the `ReversibilityRecord` retained in-memory or persisted? | Recovery reliability | Persist with 24h TTL |
| Q4 | What is the canonical transport protocol for the event bus? | Telemetry architecture | Pluggable; default NATS |
| Q5 | Should the policy engine support real-time updates without restart? | Operations flexibility | Yes; hot-reload required |
| Q6 | Is `ContextDependencyGraph` built per-request or maintained as a persistent index? | Performance vs. freshness | Persistent with invalidation |
| Q7 | Should `EL-004 Policy Learning` auto-apply updates or require human approval? | Governance risk | Human approval required |
| Q8 | Is batch invocation (`invoke_batch`) synchronous or async-job based? | Client API design | Async-job; poll or webhook |
| Q9 | How is cross-region data residency enforced at the cache layer? | Compliance | Region-tagged cache namespace |
| Q10 | Should sub-agent cost be attributed to parent task or billed separately? | Cost accounting | Parent task attribution; breakout in ledger |

---

## 39. Assumptions

The following assumptions underlie this specification. If any assumption is invalidated, affected interface sections must be reviewed:

| # | Assumption | Affected Sections |
|---|---|---|
| A1 | All LLM providers support request-level cancellation | §7, §26.3 |
| A2 | All providers expose token usage in their API response | §7.2, §28 |
| A3 | Semantic similarity search returns results in < 100 ms at p95 | §6.2, §31.1 |
| A4 | Reversibility records are sufficient for single-hop rollback; multi-hop not required | §22.3 |
| A5 | Tenant IDs are externally provisioned and immutable within a session | §27 |
| A6 | MCP servers are reachable within the same network boundary as the Control Plane | §14 |
| A7 | Policy versions are immutable once approved; only supersession allowed | §20, §24 |
| A8 | Cost pricing is fetched from provider APIs; the Control Plane does not hard-code prices | §8.1, §28 |
| A9 | Sub-agent coordination overhead is < 10% of child task cost on average | §12.3 |
| A10 | All interfaces will be implemented in the same runtime process (monolith first) before decomposition | §35 |

---

## 40. Dependencies

### 40.1 External Dependencies (Interface Level)

| Dependency | Interface Boundary | Notes |
|---|---|---|
| LLM Provider APIs (Anthropic, OpenAI, Google, etc.) | INTF-013 (LLMProvider) | Never directly imported by optimizer core |
| Vector Database (for semantic cache + RAG) | INTF-012, INTF-005 | Accessed via adapter |
| Embedding Model | INTF-005.2 (ContextRetriever) | Pluggable; must be swappable |
| Secret Manager (cloud) | INTF-037 (Auth) | Credentials never in-process |
| Event Bus (NATS / Kafka / Pub/Sub) | INTF-042 | Pluggable transport |
| Persistent Store (for audit, cost, policy) | INTF-034, INTF-047 | Immutability required for audit |
| MCP Protocol | INTF-026 | Version-pinned per server |
| Agent Frameworks (LangChain, AutoGen, etc.) | INTF-020 (Agent Registration) | Adapter per framework |

### 40.2 Internal Component Dependencies

```
OI.x requires: ModelProfileRegistry (INTF-015), PolicyStore (INTF-035)
T2.x requires: VectorDB adapter (INTF-012), PIIClassifier (INTF-040)
T3.x requires: ModelProfileRegistry (INTF-015), ProviderAvailability (INTF-013)
CE.x requires: ContextRetriever (INTF-005), DependencyGraph (INTF-011)
TE.x requires: ToolRegistry (INTF-018), AuthorizationService (INTF-038)
AL.x requires: AgentEarlyExitEvaluator (INTF-022), SubAgentAggregator (INTF-023)
QV.x requires: ValidationSpec per stage (INTF-029)
EL.x requires: CostLedger (INTF-047), EvaluationRun (INTF-030)
ALL requires: PolicyEnforcer (INTF-036), Observability (INTF-031–034)
```

---

## 41. Recommended Next Documentation Artifact

> [!NOTE]
> **Corrected 2026-09-16 (Final Foundational Document Reconciliation follow-through).** This section previously recommended generating "Engineering Specification — EAIOC-ENGR-001" as the next artifact. That recommendation predates the now-established authoritative document chain: the Engineering Specification (EAIOC-SPEC-001) and the Architecture document (EAIOC-ARCH-001) already exist and precede this document — **Problem Statement → Engineering Specification → Architecture → Interfaces** (see this document's own header, and CLAUDE.md's documented chain). The concrete technology choices, module layout, and conventions this section originally deferred to a not-yet-existing Engineering Specification are the responsibility of `conventions.md`, the next document in the chain, per the Engineering Specification's own scope statement (it is intentionally implementation-neutral) and `architecture.md` §1.6's build-vs-integrate boundary.

**Document:** `conventions.md`

**Contents (per `architecture.md` §1.6's downstream-ownership table):**
- Naming conventions, module/package layout mirroring the `<LAYER>.<SEQUENCE>-<NAME>` component ID scheme (`architecture.md` §1's conventions cross-reference)
- Language-neutral pseudo-structure for the interfaces defined here (§1–§43), not a specific language binding
- Fail-open/fail-closed enforcement mechanics implementing the precedence established in §25 and reaffirmed by §43.12
- Anti-pattern checklist (extending `architecture.md` §41)
- The dependency rules and package-layout constraints referenced by `architecture.md`'s component ID scheme

**Prerequisite for `conventions.md`:**
- Resolve Open Questions Q1–Q10 (§38)
- Confirm Assumptions A1–A10 (§39)
- Stakeholder review and sign-off on this interfaces document (EAIOC-INTF-001), including the §43 hardening amendment

Note: this document deliberately does **not** prescribe storage engines, transport protocols, database schemas, deployment topology, or a specific embedding model (§35, §37); those remain downstream, implementation-level decisions for `conventions.md` and later documents to make, consistent with §41's original intent even though its target document name was stale.

---

---

## 42. Dynamic Execution Interfaces

**Amendment:** EAIOC-INTF-001 Rev 1.1 — Hardening Pass, 2026-09-10
**Traceability:** PS §51; ARCH §46; SPEC §41

> [!IMPORTANT]
> This section is additive. All interfaces in §1–41 remain unchanged.
> These interfaces give concrete contract shape to the 13 components introduced in ARCH §46 and SPEC §41.
> All new interfaces follow the same schema conventions as §1–41: provider-neutral, tenant-scoped, fail-open for optimization, fail-closed for security.

---

### 42.1 Execution State Manager (ESM)

*Traceability: ARCH §46.2.1; SPEC §41.2.1; OBJ-015*

```
interface ExecutionStateManager {
  admit(request: ControlPlaneRequest)                            -> ExecutionHandle
  snapshot(execution_id: string)                                 -> ExecutionStateSnapshot
  apply_mutation(execution_id: string, mutation: StateMutation)  -> ExecutionStateSnapshot
  checkpoint(execution_id: string)                               -> CheckpointRecord
  reconcile(execution_id: string)                                -> ReconciliationResult
  terminal(execution_id: string, state: TerminalState,
           reason: string)                                       -> AuditRecord
}

ExecutionHandle {
  execution_id:    string    // Immutable UUID; issued at admit()
  tenant_id:       string
  request_id:      string
  admitted_at:     ISO8601 string
}

ExecutionStateSnapshot {
  execution_id:        string
  execution_version:   integer    // Monotonically increasing
  context_version:     integer    // Sourced from CVM
  workflow_version:    integer    // Sourced from WVM
  policy_version:      string     // Snapshotted at ingress; immutable mid-execution
  pipeline_stage_status: map<string, StageStatus>
  token_ledger:        TokenLedgerSnapshot
  reversibility_records: ReversibilityRecord[]
  quality_gate_results:  map<string, QualityGateResult>
  terminal_state:      TerminalState | null
  snapshot_timestamp:  ISO8601 string
}

StageStatus {
  stage_id:    string
  status:      enum { PENDING, RUNNING, COMPLETED, FAILED, SKIPPED, FALLBACK }
  completed_at: ISO8601 string | null
  fallback_applied: boolean
}

StateMutation {
  mutation_type: enum { CONTEXT_CHANGED, WORKFLOW_CHANGED, PERMISSION_CHANGED,
                        POLICY_CHANGED, MODEL_CHANGED, BUDGET_CHANGED, STAGE_COMPLETED }
  affected_domain: string
  version_before:  integer
  version_after:   integer
  triggered_by:    string    // component_id
  timestamp:       ISO8601 string
}

TerminalState {
  state:  enum { COMPLETED, FAILED, CANCELLED, SUPERSEDED, EXPIRED }
  reason: string
  timestamp: ISO8601 string
}
```

> **Invariant:** No optimization decision may proceed without a prior successful `reconcile()` call on the current `execution_id`.

---

### 42.2 Context Version Manager (CVM)

*Traceability: ARCH §46.2.2; SPEC §41.2.2, §41.3; OBJ-018*

```
interface ContextVersionManager {
  get_logical_context(execution_id: string)                       -> LogicalTaskContextSnapshot
  apply_context_mutation(execution_id: string,
                         mutation: ContextMutation)               -> ContextMutationResult
  assemble_model_admitted(execution_id: string,
                          budget: integer,
                          assembly_policy: AssemblyPolicy)        -> ModelAdmittedContext
  get_context_version(execution_id: string)                       -> integer
  check_eviction(execution_id: string, item_id: string)           -> EvictionCheckResult
}

ContextMutation {
  mutation_id:      string
  execution_id:     string
  mutation_type:    enum { ADD, REMOVE, COMPRESS, REORDER, PRUNE }
  affected_item_ids: string[]
  version_before:   integer
  version_after:    integer    // Incremented by CVM
  triggered_by:     string     // component_id
  reversibility_record: ReversibilityRecord | null    // Required for REMOVE and PRUNE
  timestamp:        ISO8601 string
}

ContextMutationResult {
  success:         boolean
  new_version:     integer
  reversibility_id: string | null
  rejection_reason: enum { EVICTION_PROHIBITED, TIER_VIOLATION, BUDGET_EXCEEDED } | null
}

LogicalTaskContextSnapshot {
  execution_id:    string
  context_version: integer
  items:           ContextItem[]
  total_tokens:    integer
  dependency_graph_version: string
  snapshot_timestamp: ISO8601 string
}

ModelAdmittedContext {
  execution_id:       string
  context_version:    integer    // Must match current CVM version at assembly time
  admitted_items:     ContextItem[]
  excluded_items:     string[]   // item_ids excluded for budget; non-destructive
  total_tokens:       integer
  budget_used_pct:    float
  assembly_timestamp: ISO8601 string
}

EvictionCheckResult {
  item_id:      string
  tier:         enum { TIER_0, TIER_1, TIER_2, TIER_3, TIER_4 }
  evictable:    boolean
  rejection_reason: enum { IMMUTABLE, ACTIVE_DEPENDENCY, TIER_PROTECTED } | null
  impact:       ImpactAnalysis | null
}
```

> **Tier Invariant (from SPEC §41.4):**
> - Tier 0 items (SEC instructions, audit-required fields): `evictable = false` unconditionally
> - Tier 1 items (developer instructions, decisions): `evictable = false` until terminal state
> - Any eviction attempt that violates these tiers returns `ContextMutationResult.rejection_reason = TIER_VIOLATION`

---

### 42.3 Workflow Version Manager (WVM)

*Traceability: ARCH §46.2.3; SPEC §41.2.3; OBJ-015, OBJ-016*

```
interface WorkflowVersionManager {
  get_workflow_state(execution_id: string)              -> WorkflowState
  record_step_completion(execution_id: string,
                         step: CompletedStep)           -> WorkflowVersionResult
  add_step(execution_id: string, step: WorkflowStep)   -> WorkflowVersionResult
  remove_step(execution_id: string, step_id: string)   -> WorkflowVersionResult
  resolve_question(execution_id: string,
                   question_id: string,
                   resolution: string)                  -> WorkflowVersionResult
  get_workflow_version(execution_id: string)            -> integer
}

WorkflowState {
  execution_id:         string
  workflow_version:     integer
  completed_actions:    CompletedStep[]    // Append-only; never modified after entry
  pending_steps:        WorkflowStep[]
  unresolved_questions: UnresolvedQuestion[]
  suspension_markers:   SuspensionMarker[]
  sub_agent_registry:   SubAgentRecord[]
}

CompletedStep {
  step_id:       string
  step_type:     string
  completed_at:  ISO8601 string
  result_summary: string | null
}

WorkflowStep {
  step_id:    string
  step_type:  string
  description: string
  depends_on:  string[]   // step_ids
}

UnresolvedQuestion {
  question_id: string
  description: string
  raised_at:   ISO8601 string
  raised_by:   string    // component_id
}

SuspensionMarker {
  suspended_at: ISO8601 string
  cause:        enum { USER_CANCELLATION, TIMEOUT, PROVIDER_OUTAGE,
                       BUDGET_EXHAUSTION, SECURITY_EVENT, POLICY_REVALIDATION }
  context:      string
}

WorkflowVersionResult {
  success:          boolean
  new_version:      integer
  conflict_detected: boolean    // True if step was already completed (idempotency)
}
```

> **Invariant:** `completed_actions` is append-only. A step_id that appears in `completed_actions` must never be re-executed. WVM must be consulted before every tool call and sub-agent spawn.

---

### 42.4 Checkpoint Manager (CPM)

*Traceability: ARCH §46.2.4; SPEC §41.6; OBJ-016, AC-016*

```
interface CheckpointManager {
  write_checkpoint(execution_id: string)           -> CheckpointRecord
  read_checkpoint(execution_id: string)            -> CheckpointRecord | null
  list_checkpoints(execution_id: string)           -> CheckpointSummary[]
  delete_checkpoint(execution_id: string,
                    checkpoint_id: string)          -> void    // TTL-based; not manual
}

CheckpointRecord {
  checkpoint_id:        string
  execution_id:         string    // Immutable; same across all checkpoints for an execution
  execution_version:    integer
  context_version:      integer
  workflow_version:     integer
  policy_version:       string
  model_selected:       string
  completed_actions:    CompletedStep[]
  unresolved_questions: UnresolvedQuestion[]
  token_ledger_snapshot: TokenLedgerSnapshot
  reversibility_records: ReversibilityRecord[]
  checkpoint_timestamp:  ISO8601 string
  expires_at:            ISO8601 string    // Default: 24h TTL (§31.4)
}

CheckpointSummary {
  checkpoint_id:    string
  execution_version: integer
  checkpoint_timestamp: ISO8601 string
  steps_completed:  integer
}

TokenLedgerSnapshot {
  input_tokens_consumed:  integer
  output_tokens_consumed: integer
  tokens_avoided:         integer
  optimizer_overhead:     integer
  cost_accrued:           float
  snapshot_timestamp:     ISO8601 string
}
```

> **Trigger Policy:** CPM writes a checkpoint: after every 3 completed steps (configurable); before any potentially long-running tool call; on any interruption event; on explicit client `CheckpointRequest`. CPM does not resume executions — it supplies checkpoint records to RCO.

---

### 42.5 Reconciliation Engine (RE)

*Traceability: ARCH §46.2.5; SPEC §41.1; OBJ-017*

```
interface ReconciliationEngine {
  reconcile(snapshot: ExecutionStateSnapshot) -> ReconciliationResult
}

ReconciliationResult {
  status:          enum { CONSISTENT, MUTATION_DETECTED, STALE_DETECTED, PRECONDITION_FAILED }
  checks_run:      ReconciliationCheck[]
  blocking_checks: ReconciliationCheck[]    // Non-empty if status != CONSISTENT
  reconciled_at:   ISO8601 string
}

ReconciliationCheck {
  check_id:    string
  check_name:  enum { CONTEXT_VERSION, POLICY_VERSION, WORKFLOW_VERSION,
                      COMPLETED_ACTIONS_CONSISTENCY, TOKEN_BUDGET, MODEL_AVAILABLE }
  passed:      boolean
  detail:      string | null
}
```

> **Invariant:** All six checks must pass before any optimization decision proceeds. If any check fails, the next pipeline step is blocked and the specific failure is surfaced to the caller.

---

### 42.6 Context Integrity Gate (CIG)

*Traceability: ARCH §46.2.6; SPEC §41.4; OBJ-019*

```
interface ContextIntegrityGate {
  validate(assembled: ModelAdmittedContext,
           budget: integer,
           policy: ContextEvictionPolicy)  -> ContextIntegrityResult
}

ContextIntegrityResult {
  status:           enum { VALID, OVERFLOW_EVICTION_REQUIRED, TIER_VIOLATION }
  tokens_over_budget: integer | null
  eviction_candidates: EvictionCandidate[]
  tier_violations:   TierViolation[]
}

ContextEvictionPolicy {
  tier_0_immutable: boolean    // Always true; cannot be overridden
  tier_1_protected_until_terminal: boolean   // Always true
  tier_2_compressible_not_evictable: boolean
  tier_3_standard_pruning:  boolean
  tier_4_evict_first:       boolean
  require_reversibility_record: boolean    // Always true for Tier 3 and 4
}

EvictionCandidate {
  item_id:     string
  tier:        enum { TIER_3, TIER_4 }
  token_count: integer
  impact:      ImpactAnalysis
}

TierViolation {
  item_id:         string
  tier:            enum { TIER_0, TIER_1 }
  requested_action: string
  reason:          string
}
```

> **Invariant:** `TIER_VIOLATION` blocks inference unconditionally. The caller must either raise the budget or accept a `PARTIAL` result. Silent truncation is prohibited.

---

### 42.7 Context Expansion Controller (CEC)

*Traceability: ARCH §46.2.7; SPEC §41.4 (Context Expansion); OBJ-019*

```
interface ContextExpansionController {
  request_expansion(request: ContextExpansionRequest) -> ContextExpansionResponse
}

ContextExpansionRequest {
  execution_id:       string
  item_id:            string    // Item to re-admit
  required_by:        string    // component_id raising the request
  freshness_required: boolean
}

ContextExpansionResponse {
  status:          enum { ADMITTED, FRESHNESS_INVALID, BUDGET_EXCEEDED, SUSPENDED }
  new_token_cost:  integer | null
  new_context_version: integer | null    // Incremented on ADMITTED
  suspension_event: SuspensionEvent | null    // Populated on SUSPENDED
}

SuspensionEvent {
  execution_id:   string
  cause:          enum { EXPANSION_BUDGET_EXCEEDED }
  tokens_needed:  integer
  tokens_available: integer
  surfaced_to_caller_at: ISO8601 string
}
```

---

### 42.8 Permission Revalidation (PRV)

*Traceability: ARCH §46.2.8; SPEC §41.8; SEC-002, SEC-007*

```
interface PermissionRevalidation {
  revalidate(execution_id: string,
             event: PermissionChangeEvent)    -> PermissionValidationResult
  revalidate_for_resume(execution_id: string,
                        checkpoint: CheckpointRecord) -> PermissionValidationResult
}

PermissionChangeEvent {
  event_id:       string
  identity_id:    string
  tenant_id:      string
  change_type:    enum { SCOPE_ADDED, SCOPE_REDUCED, TOKEN_REVOKED, ROLE_CHANGED }
  affected_scopes: string[]
  timestamp:      ISO8601 string
}

PermissionValidationResult {
  status:              enum { VALID, SCOPE_REDUCED, BLOCKED }
  invalidated_cache_keys: string[]    // Cache entries invalidated on SCOPE_REDUCED
  blocked_steps:       string[]       // step_ids blocked on BLOCKED
  revalidation_timestamp: ISO8601 string
}
```

> **Fail-closed:** Any PRV failure that cannot be resolved blocks the affected steps. Blocked steps are never silently skipped.

---

### 42.9 Dynamic Policy Evaluation (DPE)

*Traceability: ARCH §46.2.9; SPEC §41.8 (Policy hot-reload); SEC-001*

```
interface DynamicPolicyEvaluation {
  evaluate(execution_id: string,
           event: PolicyChangeEvent,
           pinned_version: string)   -> PolicyEvaluationResult
}

PolicyChangeEvent {
  event_id:        string
  policy_id:       string
  from_version:    string
  to_version:      string
  change_type:     enum { ADDITIVE, RESTRICTIVE_OPTIMIZATION, RESTRICTIVE_SECURITY }
  effective_at:    ISO8601 string
}

PolicyEvaluationResult {
  action:            enum { NO_ACTION, SUSPEND_REQUIRED, CANCEL_REQUIRED }
  rationale:         string
  // NO_ACTION:          Additive change or restrictive optimization change;
  //                     pinned policy covers current execution.
  // SUSPEND_REQUIRED:   Restrictive security change; suspend + revalidate + resume-or-cancel.
  // CANCEL_REQUIRED:    Security change that makes current execution irreconcilable.
}
```

> **Policy Pinning Rule:** Optimization policy changes take effect on the NEXT admitted request — never mid-execution. The sole exception is SEC-001–010 security upgrades (increased restriction), which may force `SUSPEND_REQUIRED`.

---

### 42.10 Capability/Availability Resolver (CAR)

*Traceability: ARCH §46.2.10; SPEC §41.5 (Provider outage); OBJ-017*

```
interface CapabilityAvailabilityResolver {
  check_availability(model_id: string, provider_id: string) -> ModelAvailabilityStatus
  on_availability_event(event: ModelAvailabilityEvent)      -> AvailabilityRoutingDecision
  activate_circuit_breaker(provider_id: string)             -> void
  reset_circuit_breaker(provider_id: string)                -> void
}

ModelAvailabilityEvent {
  event_id:     string
  provider_id:  string
  model_id:     string | null
  event_type:   enum { OUTAGE_DETECTED, RATE_LIMIT_ACTIVATED, CAPABILITY_DOWNGRADED,
                       RESTORED }
  detected_at:  ISO8601 string
}

ModelAvailabilityStatus {
  provider_id:     string
  model_id:        string
  available:       boolean
  circuit_open:    boolean
  degraded:        boolean
  degradation_reason: string | null
  checked_at:      ISO8601 string
}

AvailabilityRoutingDecision {
  selected_model_id:    string
  selected_provider_id: string
  failover_applied:     boolean
  failover_reason:      string | null
  context_window_validated: boolean
  tool_support_validated:   boolean
  compliance_validated:     boolean
  execution_version_bumped: boolean    // True when model changes mid-execution
}
```

---

### 42.11 Stale Result Protection (SRP)

*Traceability: ARCH §46.2.11; SPEC §41.9; OBJ-021; EC-027, EC-030, EC-031, EC-034*

```
interface StaleResultProtection {
  validate_cache_hit(hit: CacheLookupResult,
                     policy_version: string)    -> StaleCheckResult
  validate_resume_items(items: ContextItem[],
                        checkpoint: CheckpointRecord) -> StaleCheckResult
  validate_subagent_result(result: SubAgentResultHandoff,
                            invalidation_events: ChangeEvent[]) -> StaleCheckResult
}

StaleCheckResult {
  status:          enum { FRESH, STALE_REJECTED, STALE_REFETCHED }
  stale_item_ids:  string[]
  rejection_reasons: StaleRejectionReason[]
  refetch_required: boolean
  action_taken:    enum { SERVED, REJECTED, REFETCHED }
}

StaleRejectionReason {
  item_id:     string
  cache_type:  enum { EXACT, SEMANTIC, TOOL_RESULT, RESUME_ITEM, SUBAGENT_RESULT }
  reason:      enum { FRESHNESS_FLAG_FALSE, TTL_EXPIRED, POLICY_VERSION_MISMATCH,
                      DEPENDENCY_CHANGED, SOURCE_TIMESTAMP_NEWER }
}
```

> **Action on STALE:** Re-fetch or re-execute. Never serve stale silently. Always emit `StaleResultEvent` to observability (§23.2).

---

### 42.12 Supersession Manager (SPM)

*Traceability: ARCH §46.2.12; SPEC §41.7; OBJ-015*

```
interface SupersessionManager {
  supersede(old_execution_id: string,
            new_request: ControlPlaneRequest)  -> SupersessionRecord
}

SupersessionRecord {
  supersession_id:      string
  old_execution_id:     string
  new_execution_id:     string
  superseded_at:        ISO8601 string
  final_token_ledger:   TokenLedgerSnapshot    // Old execution accounting closed
  completed_actions_transferred: CompletedStep[]    // After freshness validation
  audit_entry_old:      AuditRecord
  audit_entry_new:      AuditRecord
}
```

> **Protocol:** (1) Mark old execution `SUPERSEDED`; (2) Finalize old token/cost accounting; (3) Issue new `execution_id`; (4) SRP-validate any shared context before re-use; (5) Transfer freshness-validated `completed_actions` to new execution's WVM.

---

### 42.13 Recovery Coordinator (RCO)

*Traceability: ARCH §46.2.13; SPEC §41.6 (Resume Protocol); OBJ-016*

```
interface RecoveryCoordinator {
  resume(request: ResumeRequest) -> ResumeDecision
}

ResumeRequest {
  execution_id:  string
  tenant_id:     string
  requested_by:  string    // component_id or user_id
  requested_at:  ISO8601 string
}

ResumeDecision {
  status:             enum { RESUME, RESULT_ALREADY_AVAILABLE, PRECONDITION_FAILED }
  resume_from_step:   string | null        // First unresolved step; populated on RESUME
  available_result:   any | null           // Populated on RESULT_ALREADY_AVAILABLE
  failed_precondition: ResumePreconditionFailure | null   // Populated on PRECONDITION_FAILED
  steps_to_skip:      CompletedStep[]      // Completed actions; never re-executed
}

ResumePreconditionFailure {
  step:    enum { PERMISSION_REVALIDATION, POLICY_REVALIDATION, MODEL_AVAILABILITY,
                  CONTEXT_FRESHNESS, STATE_RECONCILIATION }
  detail:  string
}
```

> **Resume Protocol (SPEC §41.6):**
> 1. Load `CheckpointRecord` from CPM.
> 2. Run PRV — revalidate permissions against current authorization state.
> 3. Run DPE — revalidate policy; additive changes: `NO_ACTION`; restrictive security: suspend+revalidate.
> 4. Run CAR — revalidate model availability; activate failover if needed.
> 5. Consult WVM `completed_actions` — skip all completed steps.
> 6. Run SRP on all external context items.
> 7. Run RE — confirm full state consistency.
> 8. If objective already satisfied by `completed_actions`: `RESULT_ALREADY_AVAILABLE`.
> 9. If all pass: `RESUME` from the first unresolved step.
> 10. If any step 2–7 fails: `PRECONDITION_FAILED` with specific cause.
>
> **Invariant:** Resume NEVER blindly replays completed steps.

---

### 42.14 New Event Types (Extension to §23.2)

The following event types are added to the Standard Event Types table defined in §23.2:

| Event Type | Source Component | Payload (key fields) |
|---|---|---|
| `EXECUTION_ADMITTED` | ESM | `execution_id`, `request_id`, `tenant_id` |
| `EXECUTION_SUPERSEDED` | SPM | `old_execution_id`, `new_execution_id`, `reason` |
| `EXECUTION_SUSPENDED` | ESM | `execution_id`, `cause`, `suspension_substate` |
| `EXECUTION_RESUMED` | RCO | `execution_id`, `resume_from_step`, `steps_skipped` |
| `EXECUTION_TERMINAL` | ESM | `execution_id`, `terminal_state`, `reason` |
| `CONTEXT_TIER_VIOLATION` | CIG | `execution_id`, `item_id`, `tier`, `action_blocked` |
| `CONTEXT_EXPANDED` | CEC | `execution_id`, `item_id`, `new_token_cost` |
| `CONTEXT_EXPANSION_BLOCKED` | CEC | `execution_id`, `tokens_needed`, `tokens_available` |
| `PERMISSION_SCOPE_REDUCED` | PRV | `execution_id`, `identity_id`, `invalidated_cache_count` |
| `POLICY_REVALIDATION_REQUIRED` | DPE | `execution_id`, `from_version`, `to_version`, `action` |
| `MODEL_FAILOVER_ACTIVATED` | CAR | `execution_id`, `from_model`, `to_model`, `reason` |
| `STALE_RESULT_REJECTED` | SRP | `execution_id`, `cache_type`, `item_ids`, `reason` |
| `CHECKPOINT_WRITTEN` | CPM | `execution_id`, `checkpoint_id`, `steps_completed` |
| `RECONCILIATION_FAILED` | RE | `execution_id`, `failed_checks`, `stage_blocked` |

---

### 42.15 Fail-Safe Classification (Extension to §22.2)

The following rows extend the Per-Stage Fallback Requirements table in §22.2:

| Stage | Default Fallback | Fail-Safe? |
|---|---|---|
| ESM — `admit()` | REJECT (invalid request) | No |
| ESM — `reconcile()` | BLOCK next stage; surface failure | No |
| CVM — tier violation | EVICTION_PROHIBITED; block stage | No (fail-closed) |
| CIG — `TIER_VIOLATION` | Block inference; surface to caller | No (fail-closed) |
| CEC — `BUDGET_EXCEEDED` | Suspend workflow; surface event | Yes (structured partial) |
| PRV — authorization failure | Block affected steps; fail-closed | No |
| DPE — `SUSPEND_REQUIRED` | Suspend + revalidate; fail-closed | No |
| CAR — provider outage | Activate failover cascade | Yes (fail-open to next model) |
| SRP — stale result | Reject; re-fetch or re-execute | Yes (fail-open) |
| CPM — checkpoint write failure | Warn; continue without checkpoint | Yes (fail-open) |
| RCO — `PRECONDITION_FAILED` | Surface specific failure; no resume | No |

---

### 42.16 Consistency Check (§42)

| Check | Status |
|---|---|
| All 13 new interfaces are additive — no existing interface modified | PASS |
| ESM `ExecutionStateSnapshot` fields consistent with SPEC §41.2.1 state table | PASS |
| CVM tier enforcement consistent with SPEC §41.4 Tier 0–4 definitions | PASS |
| WVM `completed_actions` append-only invariant consistent with SPEC §41.2.3 | PASS |
| CPM `CheckpointRecord` minimum fields consistent with SPEC §41.6 | PASS |
| RE six-check list consistent with ARCH §46.2.5 reconciliation checks | PASS |
| CIG `TIER_VIOLATION` fail-closed consistent with SPEC §41.10 and SEC-001–010 | PASS |
| CEC expansion protocol consistent with SPEC §41.4 Context Expansion section | PASS |
| PRV fail-closed consistent with SEC-002, SEC-007 | PASS |
| DPE policy pinning rule consistent with SPEC §41.8 | PASS |
| CAR failover requirements consistent with ARCH §46.2.10 | PASS |
| SRP detection methods consistent with SPEC §41.9 case table | PASS |
| SPM supersession protocol consistent with SPEC §41.7 | PASS |
| RCO 10-step resume protocol consistent with SPEC §41.6 | PASS |
| New event types do not conflict with §23.2 standard event types | PASS |
| New fallback rows do not conflict with §22.2 existing stage fallbacks | PASS |
| All new interfaces carry `tenant_id`; tenant isolation invariants maintained | PASS |
| No raw credentials in any new interface field | PASS |
| No provider-specific field in any new core schema | PASS |

> [!IMPORTANT]
> **SECTION 42 CONSISTENCY CHECK: PASSED (19/19)**
>
> All 13 Dynamic Execution interface definitions are internally consistent,
> fully traceable to ARCH §46, SPEC §41, and PS §51.
> No existing interface (INTF-001–049) has been modified.
> All security invariants are preserved or extended.

---

## 43. Hardening Interfaces — Operating Model, Governance, and Trust Boundaries (2026-09-15)

**Amendment:** EAIOC-INTF-001 v1.2.0 — Hardening Pass, 2026-09-15 (propagated 2026-09-16)
**Traceability:** PS §52 (H01–H20); ARCH §47; SPEC §42; OBJ-023–035; SEC-011–016; NFR-014; AC-039–053

> [!IMPORTANT]
> This section is additive. All interfaces in §1–42 remain unchanged. These interfaces give concrete contract shape to the 9 new/extended components introduced in ARCH §47 (SGE, DGE, TMG, HAG, CIS, FTR, XEC, SPC, VCL). All new interfaces follow the same schema conventions as §1–42: provider-neutral, tenant-scoped, fail-open for optimization, fail-closed for security. Where a new interface's responsibility overlaps an existing one (e.g., DGE and `PIIClassifier`, TMG and `AuthorizationService`), the new interface **extends and references** the existing one rather than duplicating its contract.

---

### 43.1 Spend Governance Engine (SGE)

*Traceability: ARCH §47.2.1; SPEC §42.5; PS §52.5; OBJ-027; SEC-011; AC-043*

```
interface SpendGovernanceEngine {
  evaluate_budget(execution_id: string, scope: BudgetScope,
                  projected_cost: float)                 -> BudgetEvaluationResult
  record_spend(execution_id: string, actual_cost: float)  -> SpendLedgerUpdate
  detect_runaway(scope: BudgetScope)                      -> RunawayDetectionResult
  activate_circuit_breaker(scope: BudgetScope)             -> void
  reset_circuit_breaker(scope: BudgetScope)                -> void
}

BudgetScope {
  scope_type: enum { TENANT, ORGANIZATION, USER, APPLICATION }
  scope_id:   string
}

BudgetEvaluationResult {
  status:                  enum { WITHIN_BUDGET, THROTTLED, HALTED }
  scope:                   BudgetScope
  remaining_budget:        float
  projected_cost:          float
  circuit_breaker_active:  boolean
  evaluated_at:            ISO8601 string
}

RunawayDetectionResult {
  detected:            boolean
  scope:               BudgetScope
  acceleration_factor: float    // vs. historical baseline
  action_recommended:  enum { NONE, THROTTLE, HALT }
}

SpendLedgerUpdate {
  scope:           BudgetScope
  cost_recorded:   float
  cumulative_spend: float
  recorded_at:     ISO8601 string
}
```

> **Invariant (SEC-011):** `BudgetEvaluationResult` is never consulted in place of `AuthorizationDecision` (INTF-038, §21.2) or `EnforcementResult` (INTF-036, §20.2). `WITHIN_BUDGET` never implies authorized; `HALTED` is never inferred from a failed authorization check. A caller that receives both must apply both independently.
>
> **Failure Behavior:** if `evaluate_budget()` cannot determine remaining budget, `status` defaults to the tenant's policy-configured safe default (`THROTTLED` or `HALTED`) — never `WITHIN_BUDGET` by default.

---

### 43.2 Data Governance Engine (DGE)

*Traceability: ARCH §47.2.2; SPEC §42.7; PS §52.7; OBJ-029; SEC-012, SEC-013; AC-045*

```
interface DataGovernanceEngine {
  classify(content_ref: string, context: ClassificationContext)  -> SensitivityClassificationResult
  get_retention_policy(surface: DataSurface, tenant_id: string)  -> RetentionPolicy
  request_deletion(subject_ref: string, tenant_id: string)       -> DeletionRequestRecord
  get_deletion_propagation_status(deletion_id: string)           -> DeletionPropagationReport
}

SensitivityClassificationResult {
  classification:        enum { SENSITIVE, NON_SENSITIVE, UNKNOWN }
  pii_result:             PIIClassificationResult | null    // INTF-040 (§21.4); reused, not duplicated
  encryption_required:    boolean
  caching_eligible:       boolean
  residency_constraint:   string | null
  classified_at:          ISO8601 string
}

DataSurface: enum { CACHE_EXACT, CACHE_SEMANTIC, MEMORY, LEDGER, LOG, TRACE, CHECKPOINT }

RetentionPolicy {
  surface:         DataSurface
  tenant_id:       string
  retention_days:  integer
}

DeletionRequestRecord {
  deletion_id:   string
  subject_ref:   string
  tenant_id:     string
  requested_at:  ISO8601 string
}

DeletionPropagationReport {
  deletion_id:          string
  surfaces_cleared:     DataSurface[]
  surfaces_pending:     DataSurface[]
  surfaces_holding_data: SurfaceHoldRecord[]    // Still holds data derived from the source
  verified:             boolean
}

SurfaceHoldRecord {
  surface:      DataSurface
  item_refs:    string[]
  reason:       string
}
```

> **Invariant (SEC-012):** `UNKNOWN` classification is treated as `SENSITIVE` for admission, caching, and retention purposes until resolved. This is a Tier 0 (SEC-protected) concern per ARCH §46's tier model and is never overridden by relevance score or budget pressure.
>
> **Failure Behavior:** a `classify()` failure defaults `classification` to `SENSITIVE` — never `NON_SENSITIVE`.

---

### 43.3 Tool/MCP Trust Gate (TMG)

*Traceability: ARCH §47.2.3; SPEC §42.11; PS §52.11; SEC-014; AC-048*

```
interface ToolMCPTrustGate {
  authenticate_identity(tool_id: string, mcp_server_id: string | null) -> IdentityVerificationResult
  validate_schema(tool_id: string, schema: ToolDefinition,
                  previous_schema_hash: string | null)                 -> SchemaValidationResult
  check_staleness(tool_id: string)                                     -> ToolFreshnessResult
  trust_decision(tool_id: string, call_context: ToolCallContext)       -> ToolTrustResult
}

IdentityVerificationResult {
  authenticated: boolean
  identity_id:   string
  verified_at:   ISO8601 string
}

SchemaValidationResult {
  valid:                     boolean
  schema_hash:               string
  changed_since_last_call:   boolean
  integrity_event_ref:       string | null
}

ToolFreshnessResult {
  fresh:                  boolean
  last_revalidated_at:    ISO8601 string
  revalidation_interval_s: integer
}

ToolTrustResult {
  status: enum { TRUSTED, SCHEMA_STALE, UNAUTHORIZED, QUARANTINED }
  reason: string | null
}
```

> **Note:** Authorization itself is delegated to `AuthorizationService.check_tool_access` (INTF-038, §21.2); TMG's trust decision is evaluated independently of TE-x's cost/ROI signal (INTF-019, §10.3) — efficiency never substitutes for a trust decision. TMG extends `ToolDefinition` (INTF-017, §10.1) and `CL-003`-style dependency invalidation (ARCH §15.3) to tool/MCP version changes specifically.
>
> **Failure Behavior:** an identity or schema-integrity failure returns `QUARANTINED` (fail-closed) — not an optimization fallback to using the untrusted tool anyway.

---

### 43.4 Human Approval Gate (HAG)

*Traceability: ARCH §47.2.4; SPEC §42.13; PS §52.13; SEC-015; OBJ-033; AC-050*

```
interface HumanApprovalGate {
  requires_approval(action: ProposedAction, policy: OptimizationPolicy) -> ApprovalRequirement
  request_approval(action: ProposedAction, risk: RiskClassification)    -> ApprovalRequest
  get_approval_status(approval_id: string)                              -> ApprovalStatus
  resolve_approval(approval_id: string, decision: enum { APPROVE, DENY },
                    approver_id: string)                                -> ApprovalResolution
}

ProposedAction {
  action_id:     string
  execution_id:  string
  action_type:   string
  reversible:    boolean
  description:   string
}

RiskClassification {
  risk_tier:              enum { LOW, MEDIUM, HIGH, CRITICAL }
  designated_for_approval: boolean
  policy_id:              string
}

ApprovalRequirement {
  required: boolean
  risk:     RiskClassification
}

ApprovalRequest {
  approval_id:    string
  action_id:      string
  requested_at:   ISO8601 string
  timeout_at:     ISO8601 string
  policy_version: string
}

ApprovalStatus {
  approval_id: string
  status:      enum { AWAITING_APPROVAL, APPROVED, DENIED, EXPIRED }
}

ApprovalResolution {
  approval_id:  string
  status:       enum { APPROVED, DENIED, EXPIRED }
  approver_id:  string | null
  resolved_at:  ISO8601 string
  audit_ref:    string
}
```

> **Note:** `requires_approval()` is policy/risk-driven per action — not every action routes through HAG. A non-designated action proceeds under the ownership model of §3.4/§43.10 without an approval gate. A pending approval follows the same suspension handling as any other interruption cause (§43.11's `SuspensionMarker`, §42.3 WVM).
>
> **Failure Behavior:** if the approval mechanism itself is unavailable, the gated action is blocked (fail-closed). On `timeout_at` expiry, the policy-defined default (deny, or escalate to a different approver) applies — never silent `APPROVED`.

---

### 43.5 Content Integrity Screen (CIS)

*Traceability: ARCH §47.2.5; SPEC §42.14; PS §52.14; SEC-016; OBJ-034; AC-051*

```
interface ContentIntegrityScreen {
  screen(content: ExternalContentItem) -> ScreeningResult
}

ExternalContentItem {
  item_id:      string
  execution_id: string
  source:       enum { RAG_CHUNK, SEARCH_RESULT, TOOL_RESULT, SUBAGENT_HANDOFF, MCP_RESULT }
  content_ref:  string
}

ScreeningResult {
  status:                enum { PASS, REJECT, QUARANTINE, SCREENING_UNAVAILABLE }
  injection_assessment:  InjectionAssessment | null
  confidence:            float | null
  security_event_ref:    string | null
  screened_at:           ISO8601 string
}

InjectionAssessment {
  technique_detected: string | null
  severity:           enum { NONE, LOW, MEDIUM, HIGH }
}
```

> **Invariant (SEC-016):** `screen()` MUST be called, and MUST return `PASS`, before an `ExternalContentItem` may be admitted, ranked, compressed, cached, or used to authorize a tool/action — for every `source` value, not only end-user input (already covered by the Sanitizer, §1's admission stage). This ordering requirement takes precedence over any Operating Mode preference (§43.10): precomputation may speed up the screening mechanism, but the screening step itself may not be skipped or deferred until after admission.
>
> **Failure Behavior:** `SCREENING_UNAVAILABLE` is fail-closed — the content is `REJECT`ed or `QUARANTINE`d, never silently admitted because the check itself failed.

---

### 43.6 Feasibility Tier Registry (FTR)

*Traceability: ARCH §47.10; SPEC §42.8; PS §52.8; OBJ-030; AC-046*

```
interface FeasibilityTierRegistry {
  declare_tier(platform_id: string, tier: FeasibilityTier,
               reachable_modules: string[])          -> TierDeclaration
  get_tier(platform_id: string)                       -> TierDeclaration | null
  redeclare_tier(platform_id: string, new_tier: FeasibilityTier,
                 reason: string)                      -> TierDeclaration
}

FeasibilityTier: enum {
  DEEP_NATIVE, GATEWAY_INTERCEPTION, PLUGIN_EXTENSION,
  PROTOCOL_TOOL_LEVEL, ADVISORY_OBSERVABILITY_ONLY
}

TierDeclaration {
  platform_id:               string    // e.g., "claude-code", "cursor", "github-copilot"
  tier:                      FeasibilityTier
  reachable_modules:         string[]    // Subset of DA-001–DA-025 module IDs (§13)
  unsupported_capabilities:  string[]
  declared_at:               ISO8601 string
}
```

> **Note:** `reachable_modules` bounds which fields of the `DeveloperAgentRequest` extension (INTF-024, §13.1) are actually populated for a given platform. A caller MUST NOT infer full-pipeline optimization coverage for a platform whose declared tier is `PROTOCOL_TOOL_LEVEL` or `ADVISORY_OBSERVABILITY_ONLY`.
>
> **Failure Behavior:** if actual platform access degrades below the declared tier at runtime (e.g., an extensibility API is deprecated), `redeclare_tier()` to a lower tier is required — silently continuing to claim the stale (higher) tier is prohibited.

---

### 43.7 Cross-Execution Coordinator (XEC)

*Traceability: ARCH §47.3.2; SPEC §42.12; PS §52.12; OBJ-032; AC-049*

```
interface CrossExecutionCoordinator {
  check_conflict(resource: SharedResourceRef, execution_id: string) -> ConcurrencyConflictResult
  reconcile(resource: SharedResourceRef,
            competing_execution_ids: string[])                      -> CrossExecutionReconciliationResult
  acquire_lock(resource: SharedResourceRef, execution_id: string,
               reason: string)                                       -> LockResult
  release_lock(resource: SharedResourceRef, execution_id: string)    -> void
}

SharedResourceRef {
  resource_type:    enum { FILE, TICKET, MEMORY_ENTRY, EXTERNAL_RESOURCE }
  resource_id:      string
  resource_version: string
}

ConcurrencyConflictResult {
  status:             enum { NO_CONFLICT, STALE_SNAPSHOT, VERSION_CONFLICT, LOCK_REQUIRED }
  current_version:    string
  requested_version:  string
}

CrossExecutionReconciliationResult {
  status:               enum { RECONCILED, BLOCKED }
  winning_execution_id: string
  losing_execution_id:  string | null
  action:               string
}

LockResult {
  acquired:            boolean
  lock_id:             string | null
  holder_execution_id: string
  expires_at:          ISO8601 string | null
}
```

> **Note:** XEC extends `ReconciliationEngine` (INTF-054, §42.5) and `SupersessionManager` (INTF-061, §42.12) from single-execution to cross-execution scope; it does not replace either. Locking (`acquire_lock`) is required only where the shared resource/action is non-idempotent and concurrently reachable — not universally.
>
> **Failure Behavior:** an unresolvable conflict blocks the losing execution's write/action (`status = BLOCKED`) — never an unreconciled dual-write. Non-idempotent operations are never blindly replayed across executions, extending RCO's (INTF-062) resume-time invariant to cross-execution scope.

---

### 43.8 Self-Protection Controller (SPC)

*Traceability: ARCH §47.4.1; SPEC §42.3; PS §52.3; OBJ-025; NFR-014; AC-041*

```
interface SelfProtectionController {
  check_latency_budget(stage_id: string, elapsed_ms: integer)   -> LatencyBudgetStatus
  check_compute_budget(tenant_id: string, consumed_units: float) -> ComputeBudgetStatus
  report_overload_signal(signal: OverloadSignal)                 -> DepthSheddingDecision
  get_current_depth_tier(tenant_id: string)                      -> OptimizationDepthTier
}

LatencyBudgetStatus {
  within_budget: boolean
  elapsed_ms:    integer
  budget_ms:     integer
  action:        enum { CONTINUE, FAIL_OPEN }
}

ComputeBudgetStatus {
  within_budget:  boolean
  consumed_units: float
  budget_units:   float
}

OverloadSignal {
  queue_depth:     integer
  latency_p99_ms:  integer
  error_rate:      float
}

DepthSheddingDecision {
  new_depth_tier:              enum { LOW, MEDIUM, HIGH }
  shed_stages:                 string[]
  security_stages_preserved:   boolean    // MUST always be true
}

OptimizationDepthTier {
  tenant_id: string
  tier:      enum { LOW, MEDIUM, HIGH }
  reason:    string
}
```

> **Invariant (NFR-014):** `DepthSheddingDecision.security_stages_preserved` is always `true`. SPC may shed §5/§6-class optimization stages under backpressure but MUST NOT shed SGE (§43.1), DGE (§43.2), TMG (§43.3), HAG (§43.4), or CIS (§43.5) evaluation.
>
> **Failure Behavior:** on latency-budget exhaustion, `action = FAIL_OPEN` to the unoptimized path (never blocks the request) — *unless* doing so would skip a security/authorization/PII check, in which case the request fails closed per §25's precedence.

---

### 43.9 Verifier Calibration Layer (VCL)

*Traceability: ARCH §47.4.3; SPEC §42.6; PS §52.6; OBJ-028; AC-044*

```
interface VerifierCalibrationLayer {
  register_verifier(verifier: VerifierDescriptor)                        -> void
  verify(verifier_id: string, output: any, evidence: VerifierEvidence)   -> VerificationResult
  get_calibration(verifier_id: string)                                   -> CalibrationMetadata
  report_drift(verifier_id: string, observed_delta: float)               -> DriftEvent | null
}

VerifierDescriptor {
  verifier_id:    string
  verifier_type:  enum { UNIT_TEST, SCHEMA_VALIDATION, TYPE_CHECK, STATIC_ANALYSIS,
                         BUSINESS_RULE, CITATION_CHECK, FORMAT_VALIDATION,
                         LLM_JUDGE_SEMANTIC_EQUIVALENCE, TASK_SUCCESS_CHECK }
  deterministic:  boolean
}

VerifierEvidence {
  inputs_considered: map<string, any>
  method:            string
}

VerificationResult {
  passed:                boolean
  confidence:            float
  calibrated:            boolean
  acceptance_threshold:  float
  escalation_required:   boolean
}

CalibrationMetadata {
  verifier_id:        string
  benchmark_set_ref:  string
  calibrated_at:      ISO8601 string
  observed_accuracy:  float
}

DriftEvent {
  verifier_id:              string
  previous_acceptance_rate: float
  current_acceptance_rate:  float
  flagged_at:               ISO8601 string
}
```

> **Invariant (OBJ-028):** `VerificationResult.passed` is never exposed or consumed as unconditional ground truth. A deterministic verifier's `confidence` is definitionally `1.0`; a probabilistic verifier's `confidence` MUST be `< 1.0` unless `calibrated = true` against `CalibrationMetadata` from a benchmark set.
>
> **Failure Behavior:** below `acceptance_threshold`, `escalation_required = true` and the caller applies `FallbackStrategy` (INTF-041, §22.1) — restore the prior representation, escalate the model, or disable the technique. Silent acceptance is prohibited.

---

### 43.10 Shared Types: Operating Mode and Decision Ownership

*Traceability: ARCH §47.5, §47.6; SPEC §42.1, §42.2; PS §52.1, §52.2; OBJ-023, OBJ-024; AC-039, AC-040*

```
OperatingMode:     enum { SYNC, ASYNC, HYBRID }
DecisionOwnership: enum { ADVISORY, ENFORCEMENT, EXECUTION_OWNERSHIP }
```

These two enums are referenced — not redefined — by every interface in §1–§43 whose decision output carries the §3.4 extension fields (`OptimizationPlan.operating_mode` / `.decision_ownership`) or an equivalent per-decision field, including every interface newly defined in §43.1–43.9.

| Mode | Applies when | Example interfaces |
|---|---|---|
| `SYNC` | Decision depends on request-time-only information | INTF-002 (per-request stages), CIS (§43.5), HAG pending-decision path |
| `ASYNC` | Underlying information changes slowly relative to request volume | INTF-014/015 (Model Profiles), FTR (§43.6) declarations |
| `HYBRID` | A precomputed artifact is consulted synchronously with a freshness/confidence gate | INTF-012 (Cache), VCL (§43.9) |

> **Default rule:** an interface implementation that has not explicitly declared a `decision_ownership` value for a given decision defaults to `ADVISORY` (least authority) — never to `EXECUTION_OWNERSHIP` by omission.

---

### 43.11 New Event Types (Extension to §23.2)

The following event types are added to the Standard Event Types table defined in §23.2:

| Event Type | Source Component | Payload (key fields) |
|---|---|---|
| `BUDGET_THROTTLED` | SGE | `scope_type`, `scope_id`, `remaining_budget` |
| `BUDGET_HALTED` | SGE | `scope_type`, `scope_id`, `circuit_breaker_id` |
| `RUNAWAY_COST_DETECTED` | SGE | `scope_id`, `acceleration_factor` |
| `DATA_CLASSIFIED_SENSITIVE` | DGE | `item_id`, `classification` |
| `DELETION_PROPAGATED` | DGE | `deletion_id`, `surfaces_cleared` |
| `TOOL_TRUST_QUARANTINED` | TMG | `tool_id`, `reason` |
| `APPROVAL_REQUESTED` | HAG | `approval_id`, `action_id`, `risk_tier` |
| `APPROVAL_RESOLVED` | HAG | `approval_id`, `status`, `approver_id` |
| `CONTENT_SCREENING_REJECTED` | CIS | `item_id`, `source`, `severity` |
| `CONTENT_SCREENING_UNAVAILABLE` | CIS | `item_id`, `source` |
| `FEASIBILITY_TIER_DECLARED` | FTR | `platform_id`, `tier` |
| `FEASIBILITY_TIER_DOWNGRADED` | FTR | `platform_id`, `from_tier`, `to_tier` |
| `CROSS_EXECUTION_CONFLICT` | XEC | `resource_id`, `conflicting_execution_ids` |
| `OPTIMIZATION_DEPTH_SHED` | SPC | `tenant_id`, `from_tier`, `to_tier`, `cause` |
| `VERIFIER_DRIFT_DETECTED` | VCL | `verifier_id`, `delta` |

---

### 43.12 Fail-Safe Classification (Extension to §22.2)

The following rows extend the Per-Stage Fallback Requirements table in §22.2:

| Stage | Default Fallback | Fail-Safe? |
|---|---|---|
| SGE — budget evaluation unavailable | Policy-configured default (THROTTLED/HALTED) | No (conservative — never defaults to unconstrained spend) |
| SGE — spend vs. security independence | Budget decision never substitutes for a security/authorization/policy decision | No |
| DGE — classification unavailable | Treat as SENSITIVE | No (fail-closed) |
| TMG — identity/schema-integrity failure | QUARANTINED | No (fail-closed) |
| HAG — approval mechanism unavailable | Block gated action | No (fail-closed) |
| HAG — approval timeout | Policy default (deny/escalate) | No |
| CIS — screening unavailable | REJECT/QUARANTINE | No (fail-closed) |
| FTR — actual access degrades below declared tier | Redeclare lower tier | Yes (fail-open to reduced capability, never silent overclaim) |
| XEC — unresolvable conflict | Block losing execution's action | No |
| SPC — latency/compute budget exceeded | Fail open to unoptimized path | Yes — unless a security check would be skipped, then fail closed |
| VCL — result below acceptance threshold | Quality-aware fallback (escalate/restore/disable) | Yes (fail-open, conservative) |

---

### 43.13 Memory Authority (ESM Extension) (H09)

*Traceability: ARCH §47.3.1; SPEC §42.9; PS §52.9; OBJ-031; AC-047*

```
interface MemoryAuthorityCheck {   // Extension consumed by ESM (INTF-050); not a standalone store
  check_conflict(execution_id: string, memory_claim: MemoryClaim) -> MemoryConflictResult
}

MemoryClaim {
  entry_ref:      string    // References a MemoryEntry (INTF-028, §16)
  claimed_state:  map<string, any>
  memory_version: string    // Provenance tag on the agent-owned memory read
}

MemoryConflictResult {
  status:              enum { CONSISTENT, CONFLICT_DETECTED, UNRESOLVABLE }
  authoritative_source: enum { ESM, CVM, WVM, AUTHORIZATION }
  authoritative_value:  any
  claimed_value:        any
  surfaced_at:          ISO8601 string
}
```

> **Invariant (OBJ-031):** Agent-owned memory (INTF-028 `MemoryStore`, §16) is never authoritative over `ExecutionStateSnapshot` (INTF-050), `ContextVersionManager` state (INTF-051), `WorkflowVersionManager` state (INTF-052), or current authorization state (INTF-038). On `CONFLICT_DETECTED`, `authoritative_value` (from ESM/CVM/WVM/authorization) wins and the conflict is surfaced as an event — never silently resolved in the agent memory's favor. An `UNRESOLVABLE` conflict (ambiguous provenance) defaults to treating the memory claim as stale/untrusted for that decision.

---

### 43.14 Verified Net Optimization Economics (Accounting Extension) (H04)

*Traceability: ARCH §47.4.2; SPEC §42.4; PS §52.4; OBJ-026; AC-042*

This is an accounting extension to `OptimizationPlan` (INTF-002, §3.2) and `CostLedgerEntry` (INTF-047, §28), not a new component interface.

```
NetOptimizationValue {    // Extends OptimizationPlan's ECONOMICS block (§3.2)
  baseline_cost:              float
  optimized_cost:             float
  tokens_saved:                integer
  inference_cost_saved:        float
  optimization_compute_cost:   float    // From SPC (§43.8) budget metering
  retrieval_overhead:          float
  routing_overhead:            float
  cache_overhead:              float
  added_latency_ms:            integer
  retry_cost:                  float
  quality_degradation_cost:    float | null
  downstream_tool_cost:        float
  net_benefit:                 float
  net_benefit_confidence:      float
  verification_status:         enum { VERIFIED, UNVERIFIED }
}

OptimizationDecisionOutcome: enum { APPLY, SKIP, DO_NOT_OPTIMIZE, FALLBACK, REJECT, REQUIRE_REVALIDATION }
```

> **Invariant (OBJ-026, AC-002):** a technique is counted as a saving only when `NetOptimizationValue.net_benefit > 0` and applicable quality gates (§17) pass. Where any input term cannot be measured, `verification_status = UNVERIFIED` and the result is excluded from reported savings — never assumed favorable. `TOKEN REDUCTION != VERIFIED NET SAVINGS`: `tokens_saved` alone never sets `verification_status = VERIFIED`.

---

### 43.15 Anti-Scope Boundary (No Interface) (H20)

*Traceability: ARCH §47.13; SPEC §42.20; PS §52.20; OBJ-035*

No interface is defined for this requirement — it is a scope boundary, not a runtime contract, consistent with its treatment in `architecture.md` §47.13 (no component, no failure behavior, no observability). Per §35 (Implementation Boundary) and `architecture.md` §1.6: no interface in this document may be implemented in a way that makes the Control Plane itself the agent orchestrator, the IDE, the coding-agent UX, the model-training system, the provider, or the inference-runtime infrastructure. Any interface proposal that would require this is out of scope and must be flagged at design-review time, not built silently.

---

### 43.16 Consistency Check (§43)

| Check | Status |
|---|---|
| All 9 new interfaces are additive — no existing interface (INTF-001–062) modified | PASS |
| SGE independence from `AuthorizationService`/`PolicyEnforcer` (SEC-011) preserved — no shared mutable state | PASS |
| DGE `SensitivityClassificationResult` extends rather than duplicates `PIIClassificationResult` (INTF-040) | PASS |
| TMG trust decision independent of TE-x ROI signal (INTF-019) | PASS |
| HAG does not require approval for every action (policy/risk-driven only) | PASS |
| CIS ordering requirement (screen before admission) does not conflict with §43.10's HYBRID preference | PASS |
| FTR tier model consistent with ARCH §47.10's five declared tiers | PASS |
| XEC extends INTF-054/INTF-061 without altering single-execution reconciliation semantics | PASS |
| SPC `security_stages_preserved` invariant consistent with NFR-014 and the fail-open/fail-closed precedence in §25 | PASS |
| VCL never exposes `passed` as unconditional ground truth | PASS |
| New event types (§43.11) do not conflict with §23.2 or §42.14 existing event types | PASS |
| New fallback rows (§43.12) do not conflict with §22.2 or §42.15 existing stage fallbacks | PASS |
| All new interfaces carry `tenant_id` or `execution_id` sufficient for tenant scoping; tenant isolation invariants (§27) maintained | PASS |
| No raw credentials in any new interface field | PASS |
| No provider-specific field in any new core schema | PASS |
| §3.4 `OperatingMode`/`DecisionOwnership` extension fields correctly cross-referenced from §43.10 rather than redefined | PASS |
| Interface IDs INTF-063–071 do not collide with INTF-001–062 | PASS |
| Memory Authority (§43.13) references INTF-028/INTF-050–052/INTF-038 without redefining them | PASS |
| Net Optimization Economics (§43.14) extends INTF-002/INTF-047 rather than duplicating them | PASS |
| Anti-Scope Boundary (§43.15) correctly defines no interface, consistent with H20's nature as a scope boundary rather than a runtime contract | PASS |
| All of H01–H20 have an identifiable §43 representation (interface, extension, or explicit no-interface boundary statement) | PASS |
| OBJ-023–035 each traceable to a §43 subsection (§34.2 table plus §43.13/43.14) | PASS |

> [!IMPORTANT]
> **SECTION 43 CONSISTENCY CHECK: PASSED (20/20)**
>
> All 9 Hardening component interface definitions (INTF-063–071) plus the Memory Authority (§43.13), Net Optimization Economics (§43.14), and Anti-Scope (§43.15) extensions are internally consistent, fully traceable to ARCH §47, SPEC §42, and PS §52 (H01–H20). No existing interface (INTF-001–062) has been modified. All security invariants (fail-closed for security/authorization/PII, fail-open for optimization only) are preserved or extended.

---

*Total interface definitions: 71 | Total schema types: 260+ | Total sections: 43*
*Authoritative source: `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt` (as hardened 2026-09-15, PS §52; reconciled 2026-09-16, PS §51.13)*
*Architecture cross-reference: `architecture.md` (EAIOC-ARCH-001 Rev 1.3)*
*Engineering specification cross-reference: EAIOC-SPEC-001 Rev 1.4*
