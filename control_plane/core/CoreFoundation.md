# `REM-P0.1.A-01` — Shared Core Foundation: Contract & Design (Remediation)

Per `docs/execution-plan.md` v1.0.5 §18.14.2 (`REM-P0.1.A-01` column) and §18.13. This is a **remediation unit**, not an `EXE-P0` unit. It is outside the 48/54 counts and does not rewrite historical commit `a345742` (`EXE-P0.1.A`). It discharges the design half of the shared-core-foundation ownership that §18.13 assigns to `EXE-P0.1.A`, which §18.14.1 found missing (`RECONCILED: GAP FOUND`, `SOURCE-GAP-EXECPLAN-04`).

This is a design note only, with no source code. The Java types are `REM-P0.1.A-02`'s job, and it must implement exactly what this note specifies once it is approved.

**Every schema block below was extracted by line range from the live `docs/interfaces.md`, never retyped.** Line numbers are those of `interfaces.md` at commit `0f1b3bc`.

---

## 1. Sources re-read for this unit

| Source | Section | Used for |
|---|---|---|
| `docs/interfaces.md` | §2.1 (lines 162–219), §2.2 (221–290), §2.3 (292–313) | `ControlPlaneRequest` and its sub-types; REQUIRED/OPTIONAL classification |
| `docs/interfaces.md` | §3.2 (337–422), §3.4 (440–451) | `OptimizationPlan`, `OptimizationStage`, `SkippedStage`, `DecisionRationale`; the §3.4 hardening extension |
| `docs/interfaces.md` | §43.10 (4388–4394) | `OperatingMode` / `DecisionOwnership` shared enums |
| `docs/interfaces.md` | §11 (`AgentState`, 1461–1469); §7 (`LLMToolCall`, 1112–1116) | Closure members defined outside §2/§3 |
| `docs/interfaces.md` | §17 (`FallbackAction`, 2008–2012); §22.1 (`FallbackStrategy`/`FallbackAction`, 2363–2379) | Fallback types (conflict found, §6) |
| `docs/interfaces.md` | §25.1–25.2 (2503–2536) | `ControlPlaneError` and its error-code taxonomy |
| `docs/conventions.md` | §2.1 (126–136), §2.2 (252–256) | Package roles and dependency rules for `core/` |
| `docs/execution-plan.md` | §8, §18.5, §18.13–§18.15 | Ownership, remediation scope, `EXE-P0.2.B` guard |
| `control_plane/accounting/ledger/CostLedgerEntry.java` | compact constructor | Precedent for structural tenant enforcement and defensive copies |

---

## 2. `ControlPlaneRequest` — `interfaces.md` §2.1, verbatim

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

REQUIRED classification, verbatim from §2.3:

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

### 2.1 Field disposition

| Field | Type (source) | P0 foundation | Reason |
|---|---|---|---|
| `schema_version`, `request_id`, `correlation_id`, `tenant_id`, `organization_id` | string | **Realized — REQUIRED** | IDENTITY block, "all REQUIRED" (§2.1) |
| `application_id` | string | Realized | Defined scalar |
| `agent_id`, `session_id`, `workflow_id`, `parent_task_id` | string \| null | Realized (nullable) | Defined scalars |
| `task_id` | string | **Realized — REQUIRED** | §2.1 comment, §2.3 |
| `iteration_number` | integer | Realized | Defined scalar ("0 for first call") |
| `request_type` | enum (8 values) | **Realized — REQUIRED** | §2.1, §2.3 |
| `user_input` | string \| null | Realized; **conditionally required** — see decision D4 | §2.3: "REQUIRED (non-batch)" |
| `system_context`, `developer_context` | `InstructionContext` \| null | Realized (nullable) | `InstructionContext` fully defined, §2.2 |
| `conversation_history` | `ConversationContext` \| null | **Deferred** | Its closure reaches `ToolCall`, which is undefined (`CORE-GAP-02`) |
| `retrieved_context` | `RetrievalContext` \| null | **Deferred** | Type undefined (`CORE-GAP-01`) |
| `memory_references` | `MemoryReference[]` \| null | **Deferred** | Type undefined (`CORE-GAP-01`) |
| `available_tools` | `ToolContext` \| null | **Deferred** | Type undefined (`CORE-GAP-01`) |
| `agent_state` | `AgentState` \| null | Realized (nullable) | `AgentState` fully defined, §11 |
| `model_preferences` | `ModelPreferences` \| null | **Deferred** | Type undefined (`CORE-GAP-01`) |
| `quality_requirements` | `QualityRequirements` | **Realized — REQUIRED** | Fully defined, §2.2 |
| `latency_requirements` | `LatencyRequirements` | **Realized — REQUIRED** | Fully defined, §2.2 |
| `budget_constraints` | `BudgetConstraints` \| null | Realized (nullable) | Fully defined, §2.2 |
| `security_classification` | `SecurityClassification` | **Realized — REQUIRED** | Fully defined, §2.2 |
| `compliance_requirements` | string[] | Realized — see decision D3 | Non-nullable collection |
| `freshness_requirements` | `FreshnessRequirements` \| null | Realized (nullable) | Fully defined, §2.2 |
| `optimization_hints` | `OptimizationHints` \| null | **Deferred** | Type undefined (`CORE-GAP-01`) |
| `bypass_optimization` | boolean | Realized | "Default false" (§2.1) |
| `metadata` | map<string,string> | Realized — see decision D3 | Non-nullable map |
| `timestamp` | ISO8601 string | Realized | — |
| `ttl_ms` | integer \| null | Realized (nullable) | — |

**Every deferred field is nullable in the source.** Leaving it out of the P0 type drops an optional input; it never drops a required one. Each deferred field is added later by whichever capability first consumes it, once its type is defined upstream. None of the six P0 capabilities in `execution-plan.md` §18.4–§18.9 reads any deferred field. (Capability 5, the Prompt Assembler, reads instruction/system content, which `InstructionContext` realizes.)

### 2.2 Realized sub-types — verbatim

`InstructionContext`, `QualityRequirements`, `LatencyRequirements`, `BudgetConstraints`, `SecurityClassification`, `FreshnessRequirements` (§2.2):

```
InstructionContext {
  content:        string
  type:           enum { SYSTEM, DEVELOPER, USER }
  stability:      enum { STABLE, SEMI_STABLE, VOLATILE }
  token_estimate: integer
  version:        string | null
}
```

```
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

`AgentState` (§11):

```
AgentState {
  status:               enum { RUNNING, PAUSED, WAITING_FOR_TOOL, COMPLETED, FAILED }
  current_objective:    string
  completed_actions:    string[]
  pending_actions:      string[]
  unresolved_questions: string[]
  key_decisions:        string[]
  checkpointed_at:      ISO8601 string | null
}
```

### 2.3 Defined in §2.2 but not realized at P0

`ConversationContext` / `ConversationTurn` are fully defined **except** for `ConversationTurn.tool_calls: ToolCall[] | null`, which is not. They are quoted here for completeness and deferred together with `conversation_history`:

```
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
```

`ContextSource` (§2.2, lines 248–256) is defined, but no `ControlPlaneRequest` field references it directly. It belongs to the undefined `RetrievalContext`'s area and is not realized.

---

## 3. `OptimizationPlan` — `interfaces.md` §3.2 and §3.4, verbatim

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

§3.4 hardening extension:

```
// Added to OptimizationPlan (§3.2), and to OptimizationStage / SkippedStage:
operating_mode:      OperatingMode          // See §43.10 for the shared enum definition
decision_ownership:  DecisionOwnership      // See §43.10 for the shared enum definition
```

§3.4 also states: "`SkippedStage.reason` additionally accepts `DO_NOT_OPTIMIZE` (net-negative expected value; see §43's Verified Net Optimization Economics types) alongside the existing enum values — no existing value is removed or renumbered."

§43.10 shared enums:

```
OperatingMode:     enum { SYNC, ASYNC, HYBRID }
DecisionOwnership: enum { ADVISORY, ENFORCEMENT, EXECUTION_OWNERSHIP }
```

### 3.1 Field disposition

| Field / type | P0 foundation | Reason |
|---|---|---|
| All `OptimizationPlan` scalar, enum, boolean and number fields (`schema_version` … `optimization_confidence`, `policy_version`, `policy_constraints_applied`, `span_id`, `timestamp`) | Realized | Fully defined, §3.2 |
| `selected_stages: OptimizationStage[]` | Realized | `OptimizationStage` realized **without** `fallback_on_failure` (below) |
| `skipped_stages: SkippedStage[]` | Realized | `SkippedStage.reason` includes the §3.4 `DO_NOT_OPTIMIZE` value |
| `decision_rationale: DecisionRationale` | Realized | Fully defined |
| `operating_mode`, `decision_ownership` (§3.4) on `OptimizationPlan`, `OptimizationStage`, `SkippedStage` | Realized | Enums fully defined, §43.10 |
| `fallback_strategy: FallbackStrategy` | **Deferred** | `FallbackStrategy` depends on `FallbackAction`, which has two conflicting definitions (`CORE-GAP-04`) |
| `OptimizationStage.fallback_on_failure: FallbackAction` | **Deferred** | Same (`CORE-GAP-04`) |
| `escalation_policy: EscalationPolicy` | **Deferred** | Type undefined anywhere in the corpus (`CORE-GAP-03`) |

**Unlike `ControlPlaneRequest`, two of these deferrals remove fields that are not nullable in the source** (`fallback_strategy`, `escalation_policy`). The P0 `OptimizationPlan` is therefore an **explicitly incomplete** realization of INTF §3.2. It must say so in its own Javadoc, and nothing may treat it as a conformant decision output until the gaps close. This is acceptable at P0 only because no P0 capability *produces* an `OptimizationPlan` (the decision engine OI-001 is P4). The sole P0 consumer is `EvaluationFramework.run_optimized()` in `EXE-P0.2.B`, which `execution-plan.md` §18.5 makes an explicit no-op. See decision D2 for the alternatives.

---

## 4. `ControlPlaneError` — `interfaces.md` §25.1–25.2, verbatim

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

§25.2 table, verbatim:

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

### 4.1 Disposition

Fully realized: every field and all eight `error_class` values. The §25.2 series table defines, in the source itself, which code prefix belongs to which class. Decision D5 proposes enforcing that consistency at construction. `PartialSuccess` (§25.3) is **not** realized: no P0 capability returns partial results, and its `result: any` field has no P0 consumer.

---

## 5. Transitive type closure — complete list

| Type | Defined at | Reached via | Disposition |
|---|---|---|---|
| `ControlPlaneRequest` | §2.1 | root | Realized (subset, §2.1 table) |
| `InstructionContext` | §2.2 | `system_context`, `developer_context` | Realized |
| `QualityRequirements` | §2.2 | `quality_requirements` | Realized |
| `LatencyRequirements` | §2.2 | `latency_requirements` | Realized |
| `BudgetConstraints` | §2.2 | `budget_constraints` | Realized |
| `SecurityClassification` | §2.2 | `security_classification` | Realized |
| `FreshnessRequirements` | §2.2 | `freshness_requirements` | Realized |
| `AgentState` | §11 | `agent_state` | Realized |
| `ConversationContext`, `ConversationTurn` | §2.2 | `conversation_history` | Deferred (`CORE-GAP-02`) |
| `ToolCall` | **undefined** | `ConversationTurn.tool_calls` | Deferred (`CORE-GAP-02`) |
| `RetrievalContext`, `MemoryReference`, `ToolContext`, `ModelPreferences`, `OptimizationHints` | **undefined** | `ControlPlaneRequest` optional fields | Deferred (`CORE-GAP-01`) |
| `OptimizationPlan` | §3.2 + §3.4 | root | Realized (explicitly incomplete, §3.1) |
| `OptimizationStage` | §3.2 + §3.4 | `selected_stages` | Realized minus `fallback_on_failure` |
| `SkippedStage` | §3.2 + §3.4 | `skipped_stages` | Realized (incl. `DO_NOT_OPTIMIZE`) |
| `DecisionRationale` | §3.2 | `decision_rationale` | Realized |
| `OperatingMode`, `DecisionOwnership` | §43.10 | §3.4 extension | Realized |
| `FallbackStrategy` | §22.1 | `fallback_strategy` | Deferred (`CORE-GAP-04`) |
| `FallbackAction` | §17 **and** §22.1 (conflicting) | `FallbackStrategy`, `OptimizationStage.fallback_on_failure` | Deferred (`CORE-GAP-04`) |
| `EscalationPolicy` | **undefined** | `escalation_policy` | Deferred (`CORE-GAP-03`) |
| `ControlPlaneError`, error class enum | §25.1–25.2 | root | Realized |

Search method, recorded so it can be repeated: `grep -n "<Type> *{" docs/*.md` for each referenced type name, plus a whole-corpus search for any mention. `RetrievalContext`, `ToolContext`, `MemoryReference`, `ModelPreferences`, `OptimizationHints` and `EscalationPolicy` each appear exactly once in `interfaces.md`, as a field type, with no body. `architecture.md` (line 361–362) and the Engineering Spec (line 275–276) mention `ToolContext`/`RetrievalContext` by name only. `ToolCall` has no body; `LLMToolCall` (§7, line 1112) exists under a different name and is **not** assumed to be the same type.

---

## 6. Gaps found — recorded, not resolved by invention

These IDs are local to this note because this unit's scope does not include `docs/execution-plan.md` (§18.14.2 Files/Modules). They are candidates for the plan's §38 register in a future, user-directed correction pass. None was previously recorded anywhere in the corpus.

| ID | Gap | Blocks this unit? | Blocks `EXE-P0.2.B`? |
|---|---|---|---|
| `CORE-GAP-01` | `RetrievalContext`, `MemoryReference`, `ToolContext`, `ModelPreferences`, `OptimizationHints` are referenced by `ControlPlaneRequest` (§2.1) but defined nowhere | No — all five fields are nullable; deferred without inventing content | No — `run_baseline()` consumes only realized fields |
| `CORE-GAP-02` | `ToolCall` (referenced by `ConversationTurn.tool_calls`, §2.2) is defined nowhere; `LLMToolCall` (§7) exists under a different name, and equivalence is not established by any source | No — `conversation_history` is nullable; deferred | No |
| `CORE-GAP-03` | `EscalationPolicy` (a **non-nullable** field of `OptimizationPlan`, §3.2) is defined nowhere | No — deferred, with `OptimizationPlan` marked explicitly incomplete (D2) | No — `run_optimized()` is a no-op at P0 |
| `CORE-GAP-04` | **Contradiction:** `FallbackAction` is defined twice. §17 (line 2008) has `action ∈ {RESTORE_ORIGINAL, INCREASE_CONTEXT, INCREASE_REASONING, ESCALATE_MODEL, DISABLE_OPTIMIZATION, RETRY}` and no `max_cost`. §22.1 (line 2373) has `action ∈ {RESTORE_ORIGINAL, INCREASE_CONTEXT, INCREASE_REASONING, ESCALATE_MODEL, DISABLE_STAGE, RETRY_WITH_PARAMS, REJECT}` plus `max_cost`. `quality-gates.md` cites the §17 variant, and `FallbackStrategy` (§22.1) is co-located with the §22.1 variant. No source says which one `OptimizationStage.fallback_on_failure` means. | No — both dependent fields deferred; this note does not pick a variant | No |

---

## 7. Decisions that need human approval

Each is stated with a recommendation. `REM-P0.1.A-02` implements whichever option is approved and nothing else.

**D1 — Scope of the P0 foundation.** Realize the subset in §2.1/§3.1/§4.1: every fully-defined type in the closure, and every field whose type is defined, with each deferral listed. *Alternatives:* (b) realize only identity fields plus REQUIRED fields; (c) block until `CORE-GAP-01`–`04` are fixed upstream. **Recommended: D1 as stated.** It is the largest faithful realization possible without inventing content, and it gives later P0 capabilities the REQUIRED fields they need.

**D2 — `OptimizationPlan` with two non-nullable fields missing.** (a) Realize it per §3.1, with Javadoc stating it is explicitly incomplete and naming `CORE-GAP-03`/`04`. (b) Realize only its identity fields (`schema_version`, `request_id`, `plan_id`) as a placeholder until OI-001 (P4) needs it. (c) Do not realize it, and block `EXE-P0.2.B` until the gaps close. **Recommended: (a).** Unlike (b), it carries every field that is defined, so later work only adds fields. Unlike (c), it unblocks `EXE-P0.2.B`, whose `run_optimized()` is a documented no-op.

**D3 — Non-nullable collections (`compliance_requirements`, `metadata`; `OptimizationPlan`'s arrays and maps).** A `null` argument is copied as an empty immutable collection, and every collection is defensively copied. This follows `CostLedgerEntry.savingsByStage`'s precedent, so a caller's mutable collection cannot change a constructed instance. *Alternative:* reject `null`. **Recommended: empty-and-copy.** An empty collection is a faithful value for these fields, while rejecting `null` would push boilerplate onto every caller for no safety gain. This never applies to REQUIRED scalar or object fields (D4).

**D4 — Structural enforcement of REQUIRED fields.** The compact constructors reject `null` or blank values for every REQUIRED field in §2.1/§2.3:
- the identity block;
- `task_id`, `request_type`, `quality_requirements`, `latency_requirements`, `security_classification`;
- `user_input`, whenever `request_type != BATCH` (§2.3: "REQUIRED (non-batch)").

`tenant_id` gets its own dedicated exception type (in `core/errors/`), matching the ledger's `MissingTenantIdException` semantics (root `CLAUDE.md` rule 4). None of these is ever silently defaulted. **Recommended as stated.** *Alternative:* enforce only `tenant_id`, and leave the rest to a future validation stage (T0 request normalization). That is weaker, because an unvalidated request could then reach a consumer.

**D5 — Error-code/class consistency.** `ControlPlaneError`'s constructor rejects an `error_code` that is not of the form `OPT-Nxxx`, or whose series digit disagrees with `error_class` under the §25.2 table (for example `OPT-4001` with `VALIDATION`). **Recommended.** The mapping is defined by the source, so enforcing it invents nothing.

**D6 — `core/interfaces/` at P0.** `conventions.md` §2.1 says it will eventually hold all 71 INTF definitions. P0 realizes **no** interface there, only a `package-info.java` documenting its role and dependency rule. Capability 2's `EvaluationFramework` (INTF-030) stays in `control_plane/evaluation/` per `execution-plan.md` §9. **Recommended.** Moving INTF-030 into `core/interfaces/` would move Capability 2 work into this remediation. Placement of each interface is decided by the unit that implements it.

**D7 — Capability 1's local exceptions are not migrated.** `MissingTenantIdException` and `DuplicateLedgerEntryException` stay in `accounting/ledger/`. Changing Capability 1 code is outside `REM-P0.1.A-02`'s file scope (`core/**` only, §18.14.2). Any migration needs its own explicit authorization.

---

## 8. Java realization (for `REM-P0.1.A-02`)

**Packages** (§18.13's Java mapping of the `conventions.md` §2.1 leaves):

| Leaf | Java package | Contents at P0 |
|---|---|---|
| `core/interfaces/` | `com.eaioc.controlplane.core.interfaces` | `package-info.java` only (D6) |
| `core/schemas/` | `com.eaioc.controlplane.core.schemas` | `ControlPlaneRequest`, `RequestType`, `InstructionContext`, `QualityRequirements`, `LatencyRequirements`, `BudgetConstraints`, `SecurityClassification`, `FreshnessRequirements`, `AgentState`, `OptimizationPlan`, `OptimizationStage`, `SkippedStage`, `DecisionRationale`, `OperatingMode`, `DecisionOwnership`, plus each inline enum as a nested enum of its owning record, and `package-info.java` |
| `core/errors/` | `com.eaioc.controlplane.core.errors` | `ControlPlaneError`, `ErrorClass`, the tenant-missing exception (D4), `package-info.java` |

**Type mapping** (following the existing ledger code):

| Source | Java |
|---|---|
| record | Java `record` (immutable) |
| `string` | `String` |
| `integer` | `int`; `Integer` when `\| null` |
| `float` | `double`; `Double` when `\| null` |
| `boolean` | `boolean`; `Boolean` when `\| null` |
| `ISO8601 string` | `java.time.Instant` (as `CostLedgerEntry.timestamp`) |
| `T[]` | `List<T>` (immutable copy) |
| `map<string, string>` | `Map<String, String>` |
| `map<string, any>` | `Map<String, Object>` (immutable copy) |
| inline `enum { … }` | Java enum, constants spelled exactly as in the source |

Field names are the camelCase of the source snake_case names, with no renaming beyond that. No field, enum constant, or type beyond §2–§4 of this note is added. No provider-, model- or framework-specific type is used anywhere (root `CLAUDE.md` rule 1).

---

## 9. Dependency rule (`conventions.md` §2.2), as it applies here

- `core/interfaces/` has zero dependencies.
- `core/schemas/` may depend only on `core/interfaces/`.
- `core/errors/` may depend only on `core/interfaces/` and `core/schemas/`. `conventions.md` states no explicit rule for `errors/`; this is the most restrictive reading consistent with §2.2, and is recorded here rather than assumed silently.
- Nothing in `core/` imports any other `com.eaioc.controlplane.*` package, or anything outside the JDK. No Spring annotations: these are plain data types, not components.

`REM-P0.1.A-02` verification: `grep -rn "import com.eaioc.controlplane" src/main/java/com/eaioc/controlplane/core/` must return only `core.*` imports that respect the rules above. `grep -rn "springframework" …/core/` must return nothing.

---

## 10. What `REM-P0.1.A-02` must prove (tests)

- **Round-trip:** each realized record, constructed with all REQUIRED fields, returns them unchanged.
- **Enum fidelity:** every enum's constant set equals the source's set exactly, including `SkippedStage.Reason.DO_NOT_OPTIMIZE`.
- **Failure path:** a missing or blank `tenant_id` is rejected with the dedicated exception, and each other REQUIRED field (D4) is rejected, never defaulted. `user_input == null` is accepted only for `BATCH`.
- **Defensive copies (D3):** mutating a caller's list or map after construction does not change the instance.
- **`ControlPlaneError` (D5):** a matching code/class is accepted; a mismatched or malformed code is rejected.
- **Existing suite:** `mvn test` passes the 19 existing Capability 1 tests plus the new ones.

---

## 11. Definition of Done — self-check

- [x] Every field and type traced to a live `interfaces.md` line range. Schema blocks are extracted, not retyped.
- [x] Full transitive closure enumerated (§5), with a repeatable search method.
- [x] No invented field, type, or enum value. Undefined types are deferred, not approximated; `LLMToolCall` is not assumed to be `ToolCall`.
- [x] Every deferral is explicit, with a reason and a gap ID (§2.1, §3.1, §5, §6).
- [x] The subset decision (D1) and the incomplete-`OptimizationPlan` decision (D2) are surfaced for human approval, not decided silently.
- [x] Four previously unrecorded gaps (`CORE-GAP-01`–`04`, one of them a contradiction) are recorded, with blocking impact stated.
- [x] Java package and type mapping, dependency rule, and required-field enforcement specified for `REM-P0.1.A-02`.
- [x] Capability 1's existing exceptions explicitly not migrated (D7).
- [x] No source code, no change to `accounting/`, `evaluation/`, `pom.xml`, or any doc.
