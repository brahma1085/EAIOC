# Engineering Conventions
## Enterprise Agent & LLM Inference Optimization Control Plane

**Document ID:** EAIOC-CONV-001  
**Revision:** 1.1.0 — Hardening Conventions Propagated (2026-09-15 amendment)  
**Status:** Authoritative — PRE-IMPLEMENTATION (foundational documents remain PRE-APPROVAL; see below)  
**Authoritative Sources (in order of authority):**
- `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt` (as hardened 2026-09-15, PS §52; reconciled 2026-09-16, PS §51.13) — highest authority
- `Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md` (EAIOC-SPEC-001 Rev 1.4)
- `architecture.md` (EAIOC-ARCH-001 Rev 1.3)
- `interfaces.md` (EAIOC-INTF-001 v1.2.0)

**Amendment history:**
- 1.0.0 (2026-09-08 baseline, extended 2026-09-10) — Baseline conventions across §1–27, including the Dynamic Execution package structure (`execution/`: ESM, CVM, WVM, CPM, RE, CIG, CEC, PRV, DPE, CAR, SRP, SPM, RCO — ARCH §46; INTF §42).
- 1.1.0 (2026-09-16) — Hardening conventions propagated for ARCH §47 / INTF §43 / PS §52 (H01–H20): new `governance/` package (SGE, DGE, TMG, HAG, CIS), `execution/cross_execution/` (XEC), `intelligence/self_protection/` (SPC), `intelligence/verifier_calibration/` (VCL), `providers/feasibility_tier/` (FTR); operating-mode, decision-ownership, net-economics, spend-governance, data-governance, tool/MCP-trust, human-approval, content-integrity, memory-authority, and cross-execution-concurrency conventions added to the relevant topical sections (§7, §12, §13, §16, §22, §23, §24, §27). Corrected: the Engineering Specification (EAIOC-SPEC-001) was previously absent from the Authoritative Sources list and the Problem Statement was mislabeled with the Engineering Specification's document ID; both are corrected above.

> [!IMPORTANT]
> This document defines the engineering conventions that ALL future implementation must follow.
> It does not redesign the architecture, introduce new implementation choices that contradict the authoritative documents, or silence, simplify, or reinterpret any requirement.
> When in doubt, the authoritative source documents take precedence over any convention stated here, in the order listed above: Problem Statement > Engineering Specification > Architecture > Interfaces > this document.
> This document's own "Authoritative" status governs implementation conventions only; it does not imply the foundational documents (PS, Engineering Spec, Architecture) have exited PRE-APPROVAL status — implementation may not begin until they are explicitly approved.

---

## Table of Contents

1. [Foundational Principles](#1-foundational-principles)
2. [Module and Package Structure](#2-module-and-package-structure)
3. [Naming Conventions](#3-naming-conventions)
4. [Interface Contracts](#4-interface-contracts)
5. [Schema and Type Conventions](#5-schema-and-type-conventions)
6. [Optimization Pipeline Conventions](#6-optimization-pipeline-conventions)
7. [Optimization Decision and Intelligence Layer](#7-optimization-decision-and-intelligence-layer)
8. [Context Management Conventions](#8-context-management-conventions)
9. [Caching Conventions](#9-caching-conventions)
10. [Model Routing and Cascade Conventions](#10-model-routing-and-cascade-conventions)
11. [Tool Execution Conventions](#11-tool-execution-conventions)
12. [Agent Loop and Sub-agent Conventions](#12-agent-loop-and-sub-agent-conventions)
13. [Security and Tenant Isolation Conventions](#13-security-and-tenant-isolation-conventions)
14. [Token and Cost Accounting Conventions](#14-token-and-cost-accounting-conventions)
15. [Quality Validation Conventions](#15-quality-validation-conventions)
16. [Fallback and Failure Conventions](#16-fallback-and-failure-conventions)
17. [Observability Conventions](#17-observability-conventions)
18. [Configuration Conventions](#18-configuration-conventions)
19. [Testing Conventions](#19-testing-conventions)
20. [Benchmarking and Evaluation Conventions](#20-benchmarking-and-evaluation-conventions)
21. [Provider and Model Profile Conventions](#21-provider-and-model-profile-conventions)
22. [Versioning and Compatibility Conventions](#22-versioning-and-compatibility-conventions)
23. [Developer-Agent Module Conventions](#23-developer-agent-module-conventions)
24. [Anti-Patterns (Prohibited Practices)](#24-anti-patterns-prohibited-practices)
25. [Implementation Priority Conventions](#25-implementation-priority-conventions)
26. [Evidence Classification Policy](#26-evidence-classification-policy)
27. [Requirements Traceability](#27-requirements-traceability)

---

## 1. Foundational Principles

*Traceability: PS §50, §44, §31; ARCH §2; INTF §1*

### 1.1 Primary Objective Function

The control plane has one primary objective:

```
MINIMIZE: Total Safe Inference Cost

SUBJECT TO:
  Quality      >= configured minimum
  Correctness  >= configured minimum
  Security     >= required policy
  Reliability  >= configured SLO
  Latency      <= configured maximum
  Freshness    >= required threshold
  Compliance   = satisfied
  Task completion = satisfied
```

> [!CAUTION]
> The target is **NOT maximum token reduction**. The target is the best **cost / quality / latency / reliability** operating point for the actual workload. Any implementation decision that maximizes token reduction at the expense of task correctness violates this objective.

### 1.2 Architectural Axiom — No Mandatory LLM Call

> [!IMPORTANT]
> An LLM call is **never required if the objective can be satisfied without it**. The control plane must evaluate whether a cache hit, a programmatic result, or a prior answer can serve the request before invoking inference.

Every design decision must ask: *Can we achieve the required outcome without a model call?*

### 1.3 Three-Layer Architecture (Mandatory Separation)

All optimization logic must be partitioned into exactly three layers, governed by a shared Optimization Intelligence Layer. No cross-layer dependencies that bypass the OI layer are permitted.

| Layer | ID | Scope |
|---|---|---|
| Agent / Workflow Optimization | Layer 1 | Task planning, tool selection/execution, sub-agent economics, agent loops, session phases, memory, early exit, outcome validation |
| Context Optimization | Layer 2 | Retrieval, ranking, pruning, deduplication, compression, reordering, context budgeting, caching, freshness, dependency graphs, code-aware context |
| Inference / Serving Optimization | Layer 3 | Model routing, model cascade, reasoning budget, output budget, batch processing, KV cache, speculative decoding, quantization, serving/scheduling |
| Optimization Intelligence | OI | Governs all three layers; makes per-request optimization decisions |

> [!CAUTION]
> Layer 3 (Inference/Serving) must remain **separate** from prompt/context optimization. Never conflate or merge measurements from different layers.

### 1.4 Provider Neutrality (Mandatory)

The core optimizer must never import, depend on, or hard-code the name of any specific LLM provider, model, agent framework, MCP library, embedding model, or transport technology. All such dependencies are resolved through adapter interfaces defined in EAIOC-INTF-001.

### 1.5 Fail-Open by Default (Except Security)

Every optimization stage must be **fail-open**: if the optimization stage fails, the pipeline continues with the original unoptimized content. Security stages (PII scrubbing, authorization) are **fail-closed**: a failure in a security stage must reject the request.

### 1.6 Explicit Over Implicit

All optimization decisions must be:
- **Policy-driven**: traceable to an `OptimizationPolicy` record
- **Measurable**: every decision emits an `OptimizationSpan` and contributes to the token ledger
- **Explainable**: every decision must be reconstructable from the `ExplanationRecord`
- **Reversible where practical**: every destructive transformation must emit a `ReversibilityRecord`
- **Auditable**: every decision is recorded in the `AuditRecord`

---

## 2. Module and Package Structure

*Traceability: ARCH §5–18, §22, §46; INTF §36, §42*

### 2.1 Top-Level Package Layout

```
control_plane/
├── core/
│   ├── interfaces/          # All 71 interface definitions, INTF-001–INTF-071 (INTF §36)
│   ├── schemas/             # All 260+ schema types (INTF §1–43)
│   └── errors/              # ControlPlaneError taxonomy (INTF §25)
│
├── execution/               # Dynamic Execution Layer — ESM, CVM, WVM, CPM, RE, CIG, CEC, PRV, DPE, CAR, SRP, SPM, RCO (ARCH §46; INTF §42, INTF-050–062)
│   ├── state/               # ESM — Execution State Manager (INTF-050)
│   ├── context_version/     # CVM — Context Version Manager (INTF-051)
│   ├── workflow_version/    # WVM — Workflow Version Manager (INTF-052)
│   ├── checkpoint/          # CPM — Checkpoint Manager (INTF-053)
│   ├── reconciliation/      # RE — Reconciliation Engine (INTF-054)
│   ├── context_integrity/   # CIG — Context Integrity Gate (INTF-055)
│   ├── context_expansion/   # CEC — Context Expansion Controller (INTF-056)
│   ├── permission_revalidation/  # PRV — Permission Revalidation (INTF-057)
│   ├── dynamic_policy/      # DPE — Dynamic Policy Evaluation (INTF-058)
│   ├── capability_resolver/ # CAR — Capability/Availability Resolver (INTF-059)
│   ├── stale_result/        # SRP — Stale Result Protection (INTF-060)
│   ├── supersession/        # SPM — Supersession Manager (INTF-061)
│   ├── recovery/            # RCO — Recovery Coordinator (INTF-062)
│   └── cross_execution/     # XEC — Cross-Execution Coordinator (ARCH §47.3.2; INTF-069)
│
├── governance/               # Governance/Safety Plane — SGE, DGE, TMG, HAG, CIS (ARCH §47.1–47.2; INTF §43.1–43.5, INTF-063–067)
│   ├── spend/                # SGE — Spend Governance Engine (INTF-063)
│   ├── data/                 # DGE — Data Governance Engine (INTF-064)
│   ├── tool_trust/            # TMG — Tool/MCP Trust Gate (INTF-065)
│   ├── human_approval/        # HAG — Human Approval Gate (INTF-066)
│   └── content_integrity/     # CIS — Content Integrity Screen (INTF-067)
│
├── intelligence/            # OI-001 to OI-005 (ARCH §14); self-protection and verifier calibration (ARCH §47.4)
│   ├── decision_engine/     # OI-001: Optimization Decision Engine
│   ├── cost_controller/     # OI-002: Cost-of-Optimization Controller
│   ├── depth_controller/    # OI-003: Adaptive Optimization Depth
│   ├── utility_scorer/      # OI-004: Context Utility / ROI Scorer
│   ├── outcome_engine/      # OI-005: Outcome-Based Optimization
│   ├── self_protection/     # SPC — Self-Protection Controller (ARCH §47.4.1; INTF-070)
│   └── verifier_calibration/ # VCL — Verifier Calibration Layer (ARCH §47.4.3; INTF-071)
│
├── pipeline/                # T0.x → T3.x stages (ARCH §11–13)
│   ├── t0_normalization/    # T0.x: Request Normalization & Gateway
│   ├── t1_context/          # T1.x: Context Operations
│   │   ├── sanitizer/
│   │   ├── pruner/
│   │   ├── deduplicator/
│   │   ├── compressor/
│   │   ├── reorderer/
│   │   └── budgeter/
│   ├── t2_cache/            # T2.x: Caching
│   │   ├── exact_cache/
│   │   └── semantic_cache/
│   └── t3_inference/        # T3.x: Model Routing & Inference
│       ├── router/
│       ├── cascade/
│       └── reasoning_budget/
│
├── context_engine/          # CE.x: Context Engine (ARCH §15)
│   ├── dependency_graph/    # CL-001
│   ├── freshness/           # CL-002
│   ├── invalidation/        # CL-003
│   ├── reversibility/       # CL-004
│   ├── compaction/          # CL-005
│   ├── memory/              # CL-006
│   └── phases/              # CL-007
│
├── cache_economics/         # CE-001 to CE-005 (ARCH §16)
│   ├── economics_engine/
│   ├── context_construction/
│   ├── fragmentation_detector/
│   ├── prewarmer/
│   └── breakpoint_optimizer/
│
├── tool_execution/          # TE-001 to TE-007 (ARCH §17)
│   ├── roi_predictor/       # TE-001
│   ├── argument_optimizer/  # TE-002
│   ├── dynamic_loader/      # TE-003
│   ├── programmatic_exec/   # TE-004
│   ├── parallel_exec/       # TE-005
│   ├── result_filter/       # TE-006
│   └── result_recovery/     # TE-007
│
├── agent_loop/              # AL-001 to AL-006 (ARCH §18)
│   ├── progress_meter/      # AL-001
│   ├── waste_detector/      # AL-002
│   ├── subagent_predictor/  # AL-003
│   ├── scheduler/           # AL-004
│   ├── handoff_compressor/  # AL-005
│   └── early_exit/          # AL-006
│
├── developer_agent/         # DA-001 to DA-025 (ARCH §22)
│   └── [25 modules — see §23]
│
├── model_control/           # AR-001 to AR-004 (ARCH §19)
│   ├── marginal_routing/    # AR-001
│   ├── reasoning_predictor/ # AR-002
│   ├── output_forecaster/   # AR-003
│   └── verifier_escalation/ # AR-004
│
├── quality/                 # QO-001 to QO-003 (ARCH §20); QV (INTF §17)
│   ├── compression_contracts/
│   ├── aware_fallback/
│   └── delayed_relevance/
│
├── experimentation/         # EL-001 to EL-005 (ARCH §21)
│   ├── shadow/
│   ├── rollout/
│   ├── ab_testing/
│   ├── policy_learning/
│   └── regression_detector/
│
├── providers/               # ARCH §25; INTF §7, §8
│   ├── adapters/            # One adapter per provider; never imported by core
│   ├── profiles/            # Versioned provider/model profiles
│   └── feasibility_tier/    # FTR — Feasibility Tier Registry (ARCH §47.10; INTF-068); coding-agent integration tiers
│
├── policy/                  # INTF §20
├── security/                # INTF §21; ARCH §29
├── accounting/              # ARCH §27; INTF §28
├── observability/           # ARCH §31; INTF §19
├── evaluation/              # ARCH §32; INTF §18
├── benchmarking/            # ARCH §32
└── deployment/              # ARCH §33; Integration modes A–F
```

### 2.2 Module Dependency Rules

- `core/interfaces/` has zero external dependencies — it is the universal contract layer.
- `core/schemas/` depends only on `core/interfaces/`.
- No module in `core/` may import from `providers/adapters/`.
- No module in `intelligence/` may import a provider-specific type.
- Every module must expose its behavior through the interface it implements, not by exposing its internal types.
- Cross-module communication happens through interface contracts only.
- `execution/state/` (ESM) is the sole owner of execution truth. No other module — including agent-owned working memory, `intelligence/`, or any `developer_agent/` module — may treat its own local state as authoritative execution state (ARCH §46.2.1).
- `intelligence/decision_engine/` (OI-001) must invoke `execution/reconciliation/` (RE) and receive a `CONSISTENT` result before issuing any optimization decision (ARCH §46.4).
- `execution/permission_revalidation/` (PRV) and `execution/dynamic_policy/` (DPE) revalidate in-flight executions against existing `security/` (SEC-001–010) and `policy/` rules; they never redefine or relax them — authorization and policy are not subordinate to optimization.
- Executions marked `SUPERSEDED` (`execution/supersession/`) or results marked `STALE_REJECTED` (`execution/stale_result/`) must not be permitted to continue producing side effects or be served as authoritative.
- Resume and recovery of any execution (`execution/recovery/`, RCO) is exclusively a Control Plane responsibility; no external caller or agent may resume an execution directly.
- `governance/spend/` (SGE) evaluates budget independently of `security/` and `policy/`; no module may treat a `governance/spend/` result as a substitute for an authorization or policy check, and vice versa (SEC-011; ARCH §47.2.1).
- `governance/data/` (DGE) classification runs before any module in `pipeline/`, `context_engine/`, or `cache_economics/` admits, caches, or compresses content; an unclassified item is treated as sensitive by default (SEC-012; ARCH §47.2.2).
- `governance/content_integrity/` (CIS) screening is invoked by every module that admits externally-sourced content (`context_engine/`, `tool_execution/`, `agent_loop/` sub-agent handoff, `developer_agent/` MCP results) before that content reaches `intelligence/decision_engine/` (SEC-016; ARCH §47.2.5).
- `governance/tool_trust/` (TMG) is consulted by `tool_execution/` before a tool/MCP result is admitted, independently of `tool_execution/roi_predictor/` (TE-001)'s cost signal (SEC-014; ARCH §47.2.3).
- `governance/human_approval/` (HAG) is consulted only for actions a policy has designated as consequential/irreversible; no module may route every action through it by default (SEC-015; ARCH §47.2.4).
- `intelligence/self_protection/` (SPC) may shed optimization depth in `pipeline/`, `context_engine/`, `cache_economics/`, or `tool_execution/` under backpressure, but must never cause `governance/` modules to be skipped (NFR-014; ARCH §47.4.1).
- `execution/cross_execution/` (XEC) extends `execution/reconciliation/` (RE) and `execution/supersession/` (SPM) to resources shared across multiple `execution_id`s; single-execution modules never implement their own cross-execution locking (ARCH §47.3.2).

---

## 3. Naming Conventions

*Traceability: ARCH §11–22, §46.1; INTF §36, §42*

### 3.1 Component IDs

Every component has a canonical ID that appears in logs, spans, metrics, and error messages. The form is:

```
<LAYER>.<SEQUENCE>-<SHORT_NAME>
```

| Component | ID Pattern | Example |
|---|---|---|
| T0 — Request Normalization | `T0.{N}-{NAME}` | `T0.1-NORMALIZER` |
| T1 — Context Ops | `T1.{N}-{NAME}` | `T1.3-CONTEXT-PRUNER` |
| T2 — Cache | `T2.{N}-{NAME}` | `T2.1-EXACT-CACHE` |
| T3 — Inference | `T3.{N}-{NAME}` | `T3.1-MODEL-ROUTER` |
| OI — Optimization Intelligence | `OI.{N}-{NAME}` | `OI.1-DECISION-ENGINE` |
| CE — Context Engine | `CE.{N}-{NAME}` | `CE.1-DEPENDENCY-GRAPH` |
| TE — Tool Execution | `TE.{N}-{NAME}` | `TE.1-ROI-PREDICTOR` |
| AL — Agent Loop | `AL.{N}-{NAME}` | `AL.1-PROGRESS-METER` |
| DA — Developer Agent | `DA.{N}-{NAME}` | `DA.001-CODE-PRUNER` |
| QV — Quality Validation | `QV.{N}-{NAME}` | `QV.1-SCHEMA-VALIDATOR` |
| EL — Experimentation/Learning | `EL.{N}-{NAME}` | `EL.1-SHADOW` |
| AR — Adaptive Model/Reasoning | `AR.{N}-{NAME}` | `AR.1-MARGINAL-ROUTING` |
| Dynamic Execution | Bare acronym, no `<LAYER>.{N}-` prefix (ARCH §46.1) | `ESM`, `CVM`, `WVM`, `CPM`, `RE`, `CIG`, `CEC`, `PRV`, `DPE`, `CAR`, `SRP`, `SPM`, `RCO` |
| Hardening (2026-09-15) | Bare acronym, no `<LAYER>.{N}-` prefix, same convention as Dynamic Execution (ARCH §47.1) | `SGE`, `DGE`, `TMG`, `HAG`, `CIS`, `FTR`, `XEC`, `SPC`, `VCL` |

### 3.2 Request and Correlation IDs

| Field | Type | Rule |
|---|---|---|
| `request_id` | UUID v4 | Generated at T0 gateway; propagated unchanged through all stages and responses |
| `correlation_id` | UUID v4 | External caller-supplied; if absent, set equal to `request_id` |
| `span_id` | UUID v4 | Generated per stage invocation |
| `parent_span_id` | UUID v4 or null | Points to the invoking stage's `span_id` |
| `trace_id` | UUID v4 | Shared across all spans for one `request_id` |
| `tenant_id` | string | Externally provisioned; immutable within a session |
| `session_id` | UUID v4 | Identifies a multi-turn session |
| `task_id` | UUID v4 | Identifies a single logical task within a session |
| `plan_id` | UUID v4 | Identifies the `OptimizationPlan` for a request |
| `execution_id` | UUID v4 | Immutable, issued once by ESM at admission (INTF-050); never reused, including on resume or supersession |
| `checkpoint_id` | UUID v4 | Issued by CPM (INTF-053) per checkpoint write; scoped to one `execution_id` |
| `decision_id` | UUID v4 | Issued per `OptimizationPlan`/`OptimizationDecisionOutcome` (INTF-002, §43.14 of INTF); immutable once issued |
| `deletion_id` | UUID v4 | Issued by DGE (INTF-064) per erasure request; used to query `DeletionPropagationReport` |
| `approval_id` | UUID v4 | Issued by HAG (INTF-066) per approval request; immutable once issued |
| `context_version`, `workflow_version`, `execution_version` | monotonically increasing integer | Owned by CVM, WVM, ESM respectively; never decremented; never reused after supersession |
| `policy_version` | string (semver or content hash) | Snapshotted at execution admission; pinned for the lifetime of that `execution_id` (ARCH §46.2.9 policy-pinning rule) |

None of the identifiers above may encode tenant identity, user identity, or content directly (no sensitive information in identifiers, per §13).

### 3.3 Metric Naming

All metrics follow the pattern:

```
control_plane.<subsystem>.<measure>
```

Examples from INTF §19.2:
- `control_plane.request.count`
- `control_plane.tokens.input`
- `control_plane.cost.saved`
- `control_plane.cache.hit_rate`
- `control_plane.stage.fallback_rate`

Metric labels are always from INTF §19.2: `tenant_id`, `request_type`, `stage_id`, `technique`, `model_id`, `provider_id`. No additional label dimensions without architecture review.

### 3.4 Event Type Naming

Event types use `UPPER_SNAKE_CASE` noun-verb form. Canonical events are defined in INTF §23.2. New event types must follow the same form and be registered in the interface inventory before use.

### 3.5 Error Code Naming

Error codes follow the taxonomy in INTF §25.2:

| Series | Class |
|---|---|
| `OPT-1xxx` | VALIDATION |
| `OPT-2xxx` | POLICY |
| `OPT-3xxx` | SECURITY |
| `OPT-4xxx` | PROVIDER |
| `OPT-5xxx` | TIMEOUT |
| `OPT-6xxx` | CAPACITY |
| `OPT-7xxx` | INTERNAL |
| `OPT-8xxx` | CONFIGURATION |

### 3.6 Schema Field Naming

- Field names: `lower_snake_case`
- Enum values: `UPPER_SNAKE_CASE`
- Timestamps: ISO 8601 UTC strings (`ISO8601 string` type in all schemas)
- All monetary amounts: `float` denominated in the `currency` field (never hard-coded currency)
- All token counts: `integer` (never float)

---

## 4. Interface Contracts

*Traceability: INTF §1–42; ARCH §5–18, §46*

### 4.1 Interface as the Only Contract Boundary

Every component exposes behavior exclusively through the interface it implements. Internal data structures, storage engines, and algorithms are private implementation details.

> [!IMPORTANT]
> **The core optimizer must never import a specific MCP library version.** All MCP communication goes through `MCPAdapter` (INTF §14). This prohibition extends to all provider libraries, agent framework libraries, embedding clients, and transport clients.

### 4.2 Required Fields Are Non-Negotiable

Every field marked as non-null in INTF §1–42 must be present in every production invocation. Optional fields (typed as `T | null`) may be omitted only when semantically absent — never as a shortcut.

### 4.3 Schema Versioning on Every Request/Response

Every schema that crosses a component boundary must carry `schema_version` in `MAJOR.MINOR.PATCH` format. A response missing `schema_version` is a contract violation.

### 4.4 Propagation Rules

| Field | Rule |
|---|---|
| `request_id` | Propagated from incoming request to every downstream response, span, log entry, audit record, and cost ledger entry without modification |
| `tenant_id` | Propagated from incoming request to every data access, cache operation, memory operation, cost record, and audit record |
| `schema_version` | The producing component sets this; the consuming component must validate it before processing |
| `correlation_id` | Propagated to all outbound calls (provider APIs, tool calls, sub-agents) |

### 4.5 Idempotency Requirements

| Interface | Idempotency Key | Behavior |
|---|---|---|
| `ControlPlaneRequest` | `request_id` | Same request_id must yield same result or graceful dedup |
| `CacheWriteRequest` | `idempotency_key` | Deduplicated within TTL |
| `ToolInvocationRequest` | `tool_call_id` | Non-idempotent tools must set `idempotent: false` |
| `SubAgentSpawnRequest` | `spawn_id` | Duplicate spawn is ignored |
| `MemoryEntry` write | `entry_id` | Upsert semantics |
| `OptimizationDecisionRequest` | `request_id` + `plan_id` | Same input must yield same plan |

### 4.6 Cancellation

Every async operation must accept a `cancellation_token`. On cancellation:
1. In-flight LLM calls are cancelled at the provider (if supported).
2. All in-flight stages are terminated.
3. `ReversibilityRecord` entries are retained until their configured expiry.
4. Partial cost is recorded in the cost ledger.
5. A `CANCELLED` event is emitted on the event bus.

---

## 5. Schema and Type Conventions

*Traceability: INTF §1–42; ARCH §27*

### 5.1 Universal Required Fields

Every request schema must carry:

```
request_id:     UUID v4 (non-null)
tenant_id:      string (non-null)
schema_version: string (MAJOR.MINOR.PATCH, non-null)
timestamp:      ISO8601 string (non-null)
```

Every response schema must echo:

```
request_id:     (unchanged from request)
schema_version: (producing component's version)
timestamp:      ISO8601 string (processing completion time)
```

### 5.2 Token Count Types

| Field Category | Type Rule |
|---|---|
| All `token_count`, `tokens_*` fields | `integer` (never float) |
| All `cost_*`, `*_cost`, `*_savings` fields | `float` with `currency` context |
| All `score`, `ratio`, `confidence`, `similarity` fields | `float` in `[0.0, 1.0]` unless documented otherwise |
| `latency_ms` | `integer` (milliseconds) |

### 5.3 Enum Completeness

Every enumeration must define an `OTHER` or catch-all value unless the enum is explicitly closed by design. Closed enums must be documented as such. New enum values are a MINOR version change; removing values is a MAJOR version change.

### 5.4 Null vs. Absent

- `null` means "the field is applicable but has no value in this context."
- Absent (omitted) means "this field does not apply to this schema variant."
- Never omit a non-optional field. Use `null` when the value is not yet known.

### 5.5 Sensitive Field Handling

Fields tagged as sensitive in INTF §21.4 must never appear in log entries, audit records, event payloads, or metric labels. Replace with `[REDACTED-{CATEGORY}]` before any output. Raw credentials must never appear in any interface field — use secret handle references only.

---

## 6. Optimization Pipeline Conventions

*Traceability: ARCH §24; INTF §3–4*

### 6.1 Default Optimization Order

The default order is **lowest-risk and lowest-cost transformations first**. This is policy, not a rigid constraint. The system may reorder stages when benchmark evidence demonstrates a better cost/quality/latency trade-off, subject to governance approval.

| # | Stage | Rationale |
|---|---|---|
| 1 | Exact cache lookup | Zero inference cost; highest ROI |
| 2 | Semantic cache lookup (where safe) | High ROI; requires auth/freshness checks |
| 3 | Sanitization | Deterministic; near-zero overhead |
| 4 | Duplicate removal | Deterministic; high value |
| 5 | Metadata/filter pruning | Deterministic; low risk |
| 6 | Entity extraction | Lightweight; enables downstream optimization |
| 7 | Retrieval filtering | Reduces input before ranking |
| 8 | Adaptive Top-K | Reduces retrieval waste |
| 9 | Context ranking | Improves selection quality |
| 10 | Context pruning | Removes low-value context |
| 11 | Context deduplication | Removes redundancy |
| 12 | Tool-output filtering | Reduces tool context |
| 13 | Context reordering | Mitigates position bias |
| 14 | Context compression | Higher-cost; after cheaper stages |
| 15 | Soft reset | Budget enforcement |
| 16 | Query compression | User request optimization |
| 17 | Cache-aware prompt assembly | Optimize cache prefix structure |
| 18 | Model routing | Select least expensive capable model |
| 19 | Reasoning-budget selection | Allocate reasoning depth |
| 20 | LLM execution | Inference (may be skipped entirely) |
| 21 | Output schema enforcement | Constrain output structure |
| 22 | Output length enforcement | Constrain output tokens |
| 23 | Agent stop / early exit | Evaluate continuation necessity |
| 24 | Fallback / escalation on quality failure | Quality recovery |

### 6.2 Per-Stage Interface Convention

Every optimization stage must implement the `OptimizationModule` interface (INTF §3):

```
interface OptimizationModule {
  id:             string        // Component ID (§3.1)
  version:        string        // MAJOR.MINOR.PATCH
  stage_type:     StageType
  is_enabled:     boolean       // Controlled by OptimizationPolicy

  can_apply(request, context) -> boolean
  apply(request, context)     -> StageResult
  estimate_benefit(request)   -> BenefitEstimate
  get_fallback(failure)       -> FallbackAction
}
```

A stage must:
1. Emit a `ReversibilityRecord` before modifying any content.
2. Emit an `OptimizationSpan` covering its execution.
3. Report `tokens_before` and `tokens_after` in its `StageResult`.
4. Report `optimization_cost` in its `StageResult`.
5. Execute its configured fallback and set `fallback_triggered: true` when it fails.
6. Never swallow exceptions silently — always emit an error event.

### 6.3 Optimization Must-Not Rules

> [!CAUTION]
> The following are unconditionally prohibited regardless of performance pressure:

- A stage must not remove or alter content it has not been authorized to handle.
- A stage must not proceed if the preceding quality gate has not passed.
- A stage must not claim savings if accounting is unverified — mark as `unverified`.
- A stage must not bypass the `PolicyEnforcer` for any operation on cached, memory, or tool data.
- A stage must not apply an optimization whose expected net benefit is <= 0.

### 6.4 Cost-of-Optimization Accounting

Every stage must report its own compute cost as `optimization_cost` in `StageResult`. Net savings are only claimed when:

```
Net Savings = Inference Cost Avoided
            - Optimization Compute Cost
            - Additional Provider Cost
            - Additional Tool Cost
            - Additional Latency Cost
            - Expected Quality/Risk Cost
```

An optimization with `Net Savings <= 0` must not be enabled in production unless it serves a non-cost purpose (e.g., latency reduction) and the rationale is documented in the `ExplanationRecord`.

---

## 7. Optimization Decision and Intelligence Layer

*Traceability: ARCH §14; PS §33; INTF §3*

### 7.1 OI-001 — Decision Engine Requirements

For every request, the Optimization Decision Engine must produce an `OptimizationPlan` that answers all decisions listed in PS §33 OI-001, including: serve from exact cache? semantic reuse safe? which retrieval depth? which context budget? run compression? which tools? skip a tool call? spawn a sub-agent? which model? reasoning budget? output budget? continue workflow? escalate or terminate?

The plan must be policy-driven, measurable, explainable, and capable of stage reordering when evidence shows better results.

### 7.2 OI-002 — Cost-of-Optimization Controller

Before committing to any optimization path, the controller must estimate:

```
Expected Net Value =
    Expected Inference Cost Avoided
  - Optimization Compute Cost
  - Additional Provider Cost
  - Additional Tool Cost
  - Additional Latency Cost
  - Expected Quality/Risk Cost
```

Optimizations with `Expected Net Value <= 0` are skipped. The decision and rationale are recorded in the `ExplanationRecord`.

### 7.3 OI-003 — Adaptive Depth Controller

| Complexity | Optimization Depth |
|---|---|
| LOW | Exact cache + deterministic hygiene + basic routing |
| MEDIUM | Retrieval optimization + pruning + deduplication + compression + routing |
| HIGH | Dependency-aware retrieval + context graph + advanced compression + multi-agent economics + deeper validation + iterative optimization |

The system must not apply expensive optimizations to requests where simple optimization is sufficient.

### 7.4 OI-004 — Context Utility / ROI Scorer

Every context item must receive a utility score. The formula is configurable and benchmark-driven:

```
Context ROI = Context Utility / Context Cost (tokens)
```

Signals: task relevance, information density, source authority, recency, freshness, dependency importance, diversity, historical usefulness, confidence, token cost.

The exact formula is specified in `OptimizationPolicy` per workflow and must be re-benchmarked after model/provider changes.

### 7.5 OI-005 — Outcome-Based Engine

The primary cost metric is **cost per successful outcome**, not cost per token or cost per request. A token reduction that lowers task success is not a win.

Required outcome metrics: cost per successful answer, cost per completed workflow, cost per resolved task, cost per completed coding task, tokens per successful outcome.

### 7.6 Operating Mode Declaration (H01)

*Traceability: ARCH §47.5; INTF §43.10; PS §52.1; OBJ-023*

Every decision-producing component declares exactly one of `SYNC`, `ASYNC`, or `HYBRID` (INTF `OperatingMode`, §43.10) as its operating mode. This is a per-component-type declaration, not a global setting:

- Use `SYNC` only when the decision depends on request-time-only information (the actual prompt, the actual retrieved context, the actual permission state) that cannot be safely precomputed.
- Use `ASYNC` for information that changes slowly relative to request volume (provider/model profiles, repository maps, cache pre-warming, routing-policy compilation, pricing tables).
- Use `HYBRID` for a precomputed artifact consulted synchronously with a freshness/confidence gate (SRP, VCL). Most production decisions are expected to be `HYBRID`.

> [!CAUTION]
> Choosing `SYNC` where `HYBRID` would achieve equivalent safety/quality at lower latency/cost is the "expensive optimizer" anti-pattern (§24.2). Control Plane decision latency and compute overhead are themselves part of the optimization problem (§7.8), not externalities.

### 7.7 Advisory / Enforcement / Execution-Ownership Declaration (H02)

*Traceability: ARCH §47.6; INTF §43.10; PS §52.2; OBJ-024*

Every decision or side effect a component produces is classified as exactly one of `ADVISORY`, `ENFORCEMENT`, or `EXECUTION_OWNERSHIP` (INTF `DecisionOwnership`, §43.10), recorded independently per decision in the `ExplanationRecord` (§36).

- **ADVISORY**: the component recommends; the calling agent/application decides and acts. No side effect is owned here.
- **ENFORCEMENT**: the component blocks/requires/rewrites within its policy authority; the calling agent/application still performs the (now-constrained) action.
- **EXECUTION_OWNERSHIP**: the component itself performs an action with an external side effect (a cache write, a programmatic tool execution, a checkpoint commit). Only this category makes the component directly responsible for the side effect's correctness and reversibility, and it inherits CL-004's reversibility requirements.

> [!IMPORTANT]
> A component whose ownership category is not explicitly configured defaults to `ADVISORY` (least authority). Implementations must never default to `EXECUTION_OWNERSHIP` by omission. No implementation may use a `governance/` module (§7.9, §13.5–13.9) to silently assume ownership of agent planning, IDE behavior, or an external side effect the Control Plane has not been explicitly integrated to own (§50 anti-pattern equivalent — see §24.4).

### 7.8 Verified Net Optimization Economics (H04)

*Traceability: ARCH §47.4.2; INTF §43.14; PS §52.4; OBJ-026; AC-042*

§7.2's `Expected Net Value` formula is extended with the full accounting required by the hardening pass. Every reported saving must be computed from `NetOptimizationValue` (INTF §43.14):

```
Net Optimization Value =
    Tokens Saved + Inference Cost Saved
  - Optimization Compute Cost      (metered by SPC, §7.9)
  - Retrieval Overhead
  - Routing Overhead
  - Cache Overhead (write + storage + invalidation, CE-001)
  - Added Latency Cost
  - Retry / Escalation Cost
  - Quality Degradation Cost (weighted by applicable quality gate, §15)
  - Downstream / Tool Cost
```

A technique is counted as a saving only when `Net Optimization Value > 0` **and** applicable quality gates pass. Where any term cannot be measured, the result's `verification_status` is `UNVERIFIED` (AC-002) and it is excluded from reported savings — never assumed favorable. `TOKEN REDUCTION != VERIFIED NET SAVINGS`.

Every optimization decision uses one of the six `OptimizationDecisionOutcome` values (INTF §43.14): `APPLY`, `SKIP`, `DO_NOT_OPTIMIZE`, `FALLBACK`, `REJECT`, `REQUIRE_REVALIDATION`. `DO_NOT_OPTIMIZE` is used when expected net value is negative or the quality/security risk exceeds expected benefit — this is a first-class, expected outcome, not an error.

### 7.9 Control Plane Self-Protection (SPC) (H03)

*Traceability: ARCH §47.4.1; INTF §43.8; PS §52.3; OBJ-025; NFR-014; AC-041*

`intelligence/self_protection/` (SPC) enforces, for every `SYNC`/`HYBRID` component (§7.6):

- A configurable maximum added latency per decision (Appendix A budgets apply); on exhaustion the component fails open to the unoptimized path.
- A metered, bounded compute/provider-API budget per request and per tenant, counted as `Optimization Compute Cost` in §7.8's accounting.
- Backpressure: under sustained overload, OI-003 (§7.3) steps the depth tier down to `LOW` even for requests that would otherwise warrant `MEDIUM`/`HIGH`.
- Overload detection (queue depth, latency p99, error rate) that sheds optional stages — never silently degrades a quality or security check.

> [!CAUTION]
> **Non-negotiable precedence:** SPC may shed any `pipeline/`, `context_engine/`, or `cache_economics/` stage under backpressure, but it must never cause a `governance/` module (SGE, DGE, TMG, HAG, CIS) to be skipped. If shedding would otherwise skip a mandatory security/authorization/PII check, the affected request fails **closed**, not open (§16.2 precedence).

### 7.10 Verifier Calibration (VCL) (H06)

*Traceability: ARCH §47.4.3; INTF §43.9; PS §52.6; OBJ-028; AC-044*

`intelligence/verifier_calibration/` (VCL) governs every verifier consumed by AR-004 (§10) and QO-002 (§15): deterministic verifiers (unit tests, schema/type checks, static analysis) are treated as higher-confidence than probabilistic verifiers (LLM-judge semantic-equivalence checks). A probabilistic verifier's confidence score must be calibrated against a benchmark set (§20.3 maturity model applies to verifiers themselves) before it is trusted to gate an optimization decision. A verifier's acceptance behavior is monitored over time; drift (accepting results it previously rejected, or vice versa, without a corresponding technique change) is surfaced as a `VERIFIER_DRIFT_DETECTED` event.

> [!CAUTION]
> A verifier's `passed = true` output is never treated as unconditional ground truth. Below the configured acceptance threshold, apply the existing Quality-Aware Fallback (§15.2) — restore, escalate, or disable. Silent acceptance is prohibited.

---

## 8. Context Management Conventions

*Traceability: ARCH §15; PS §34; INTF §5, §16*

### 8.1 Context Lifecycle Rules (CL-001 to CL-007)

**CL-001 — Context Dependency Graph**: Context is modeled as a directed dependency graph. Pruning a node requires propagating the impact to its dependents. Maintained as a persistent index with dependency-aware invalidation.

**CL-002 — Context Freshness**: Every retrievable context item carries: source, version, commit, branch, timestamp, last verification time, freshness status. Freshness influences ranking, caching, retrieval, and validation.

**CL-003 — Dependency-Aware Cache Invalidation**: When a source item changes, dependent cached representations must be invalidated or revalidated (file changed → invalidate symbol summaries; permission changed → invalidate affected cache entries).

**CL-004 — Reversible Optimization**: Every destructive transformation emits a `ReversibilityRecord` containing: original source, transformation type, removed range/content reference, reason, confidence, recovery pointer, expiration/freshness metadata. Minimum retention: 24 hours.

**CL-005 — Progressive Context Compaction** (all thresholds configurable and model-specific):

| Budget Used | Action |
|---|---|
| 0–40% | Full fidelity |
| 40–60% | Deduplicate |
| 60–70% | Remove low-value content |
| 70–80% | Compress historical/secondary context |
| 80–90% | Aggressive selective loading |
| 90%+ | Controlled session reset or phase transition |

**CL-006 — Hierarchical Memory**: Layers are: TURN, TASK, SESSION, PROJECT, ORGANIZATION, HISTORICAL. Each layer has independent retention, freshness, compression, retrieval, token budget, and invalidation policies.

**CL-007 — Session Phase Management**: Long-running tasks support explicit phases (Discovery, Planning, Implementation, Testing, Review, Completion). Each phase uses different context, tool, model, reasoning, and memory policies.

### 8.2 Context Priority Order (Developer/Coding Agents)

| Priority | Section | Rule |
|---|---|---|
| 1 (highest) | SYSTEM | Security instructions; **never pruned** |
| 2 | DEVELOPER_INSTRUCTIONS | Coding-agent system prompt |
| 3 | COMPILER_ERRORS | Actionable failures only (DA-006) |
| 4 | SYMBOL_CONTEXT | Relevant symbols (DA-002) |
| 5 | GIT_DIFF | Relevant changes (DA-004) |
| 6 | FILE_CONTENT | Lazy-loaded files (DA-016) |
| 7 | CONVERSATION | Pruned history |
| 8 | REPOSITORY_MAP | Compact map (DA-003) |
| 9 | TERMINAL_OUTPUT | Filtered output (DA-005) |
| 10 | MCP_TOOLS | Minimized schemas (DA-008) |
| 11 | MEMORY | Relevant memory (DA-010) |
| 12 | TEST_OUTPUT | Relevant failures only |
| 13 | AGENT_STATE | Active plan only (DA-018) |

### 8.3 Cache-Aware Context Construction (CE-002)

```
STABLE / CACHEABLE PREFIX     <- Never inject dynamic content here
├── System instructions
├── Stable developer policies
├── Stable tool schemas
├── Stable project rules
├── Stable examples
└── Stable repository metadata

SEMI-STABLE REGION
├── Project documentation
├── Memory
└── Slowly changing context

VOLATILE REGION               <- Never cached
├── Current user request
├── Current files
├── Current errors
├── Current tool results
└── Current task state
```

Never introduce dynamic content (timestamps, random IDs, dynamic metadata) into the stable/cacheable prefix region.

---

## 9. Caching Conventions

*Traceability: ARCH §11.4, §11.5, §16; INTF §6*

### 9.1 Cache Key Construction

All cache keys must include `tenant_id` as the first namespace component. Cross-tenant cache serving is unconditionally prohibited.

For semantic cache, a match must pass **all four checks** before serving:
1. Authorization check (`auth_check_passed: true`)
2. Freshness check (`freshness_valid: true`)
3. Tenant isolation check (`entry.tenant_id == request.tenant_id`)
4. Policy check (`cache_allowed: true`)

### 9.2 Cache Semantics by Type

| Cache Type | TTL Required | Auth Check | Freshness Check | Similarity Threshold |
|---|---|---|---|---|
| Exact cache | Yes | Yes | Yes | N/A |
| Semantic cache | Yes | Yes | Yes | Configurable per tenant; global floor = 0.92 |
| Tool result cache | Yes (per-tool policy) | Yes | Yes | N/A |
| Prompt cache (provider-native) | Provider TTL | Provider contract | Provider contract | N/A |

### 9.3 PII and Sensitive Data in Cache

> [!CAUTION]
> Sensitive information must not be placed in a cache unless explicitly permitted by the organization's security and data-retention policy. Every cache entry must be checked by `PIIClassifier` before writing. If `is_safe_to_cache: false`, the entry must not be cached.

### 9.4 Cache Economics (CE-001)

```
Expected Cache Benefit =
    Future Reuse Probability × Tokens Avoided × Effective Input Price

Cache Cost =
    Write Cost + Storage Cost + Invalidation Cost + Freshness Risk + Cache Miss/Break Risk
```

A cache write is executed only when `Expected Cache Benefit > Cache Cost`.

### 9.5 Cache Fragmentation Prevention (CE-003)

The following patterns are prohibited in cacheable prompt sections:
- Dynamic timestamps in system instructions
- Random identifiers in tool schemas
- Dynamic metadata in stable prefixes
- Changing tool definition ordering
- Unstable context ordering

---

## 10. Model Routing and Cascade Conventions

*Traceability: ARCH §10.1, §13.4, §19.1; INTF §9*

### 10.1 Model Routing Rules

- Model selection is always based on `ModelProfileRegistry` — never on hard-coded model names.
- Routing decision must record: `routing.model_selected`, `routing.decision_reason`, `routing.quality_score_by_model`.
- Safety-critical or complex requests must never be routed to incapable models (explicit architectural prohibition).
- On routing failure: use `OptimizationPolicy.default_model_id`.

### 10.2 Model Cascade Rules

- Cascade is only permitted when `OptimizationPolicy.cascade_enabled: true`.
- Quality evaluator cost must be included in cost-of-optimization calculation.
- On escalation, emit `MODEL_ESCALATED` event with `from_model`, `to_model`, `trigger`.
- On cascade quality evaluator failure: default to full-capability model (fail-safe).

### 10.3 Reasoning Budget Rules (AR-002)

> [!CAUTION]
> **Never reduce reasoning budget solely to meet a token target.** This is an explicit architectural prohibition. Reasoning budget reduction must be compared against quality baseline before being applied.

### 10.4 Marginal-Gain Routing (AR-001)

```
Marginal Model Value = Expected Quality Gain / Additional Cost
```

Escalate to a stronger model only when marginal value is positive **and** quality constraints justify it.

---

## 11. Tool Execution Conventions

*Traceability: ARCH §17; PS §36; INTF §10*

### 11.1 Tool ROI Gate (TE-001)

Before executing any tool call, estimate:

```
Tool ROI = Expected Information Gain / (Tool Cost + Token Cost + Latency Cost)
```

Skip or defer tool calls whose `Tool ROI` is below the configured threshold. Record skipped tools with `TOOL_SKIPPED` event including `reason` and `expected_gain`.

### 11.2 Tool Schema Minimization (DA-008)

Expose only the tool definitions required for the current task. The tool schema budget is controlled by `MCPToolFilter.max_schema_tokens`. Load full schemas on demand only.

### 11.3 Tool Result Filtering (TE-006)

Never automatically propagate the full tool result. Retain only: required fields for task completion, relevant records/errors, evidence and provenance, required authorization/audit fields.

> [!CAUTION]
> Tool-result filtering must never remove fields required for authorization, validation, or audit (SEC-006).

### 11.4 Parallel Tool Execution (TE-005)

Independent tool calls must be executed concurrently where: no inter-call data dependency exists, side effects are safe, and provider/agent policy permits.

### 11.5 Non-Idempotent Tools

Tools with `idempotent: false` must explicitly set this flag. The executor must not retry non-idempotent tools on failure without explicit policy authorization.

---

## 12. Agent Loop and Sub-agent Conventions

*Traceability: ARCH §18; PS §37; INTF §11, §12*

### 12.1 Agent Loop Progress Measurement (AL-001)

Every agent iteration must report an `AgentIterationReport` containing: input tokens, output tokens, tool cost, new information gained, state change, objective progress, errors introduced/resolved.

### 12.2 Loop Waste Detection (AL-002)

The loop controller must detect and terminate on: repeated searches, repeated tool calls, repeated reasoning over unchanged state, oscillation, no-progress iterations, dead-end loops.

### 12.3 Sub-agent Value Gate (AL-003)

Before spawning a sub-agent, estimate:

```
Sub-Agent ROI =
    Expected Sub-Agent Value /
    (Agent Inference Cost + Context Duplication Cost
     + Aggregation Cost + Parent Ingestion Cost + Latency + Coordination Overhead)
```

Do not spawn a sub-agent when `Sub-Agent ROI` is below the configured threshold.

### 12.4 Sub-agent Handoff Compression (AL-005)

A sub-agent handoff must preserve: objective, findings, evidence, files/symbols, confidence, decisions, unresolved questions, recommended next actions.

> [!CAUTION]
> Full conversation transcript replay to a sub-agent is **unconditionally prohibited** (INTF §33). Use `SubAgentContextHandoff`.

### 12.5 Agent Early Exit (AL-006)

Stop an agent when: objective is satisfied, required validation passes, additional information has low expected value, or additional model/tool calls are unlikely to change the outcome. Record avoided work in `agent.tokens_saved_by_exit` and `agent.steps_skipped`.

### 12.6 Agent Memory Authority (H09)

*Traceability: ARCH §47.3.1; INTF §43.13; PS §52.9; OBJ-031; AC-047*

Agent-owned working memory (`MemoryStore`, INTF §16) — scratch state, transient observations, local plans, temporary working information — is expected and unrestricted, but is **never authoritative** over:

- `execution/state/` (ESM) execution state
- Current authorization state
- Current policy state (the `policy_version` pinned at admission, §18.2)
- `execution/context_version/` (CVM) / `execution/workflow_version/` (WVM) versions

Every read from `MemoryStore` that could inform a decision affecting execution truth must carry a `memory_version`/provenance tag, compared against current ESM/CVM/WVM state before being trusted (`MemoryAuthorityCheck`, INTF §43.13). On disagreement — e.g., agent memory says a step is complete but WVM's `completed_actions` says otherwise, or agent memory holds a permission grant PRV has since revoked — the ESM/CVM/WVM-owned value wins and the disagreement is surfaced as an event, never silently resolved in the agent memory's favor. An unresolvable conflict (ambiguous provenance) defaults to treating the memory claim as stale/untrusted for that decision.

> [!CAUTION]
> No module — including `developer_agent/` and `agent_loop/` — may treat agent-owned memory as a substitute for a `reconcile()` call against ESM (§2.2). This restates, for agent memory specifically, the general principle that no optimization decision may proceed on a stale snapshot of context, policy, permissions, or resource state.

---

## 13. Security and Tenant Isolation Conventions

*Traceability: ARCH §29; PS §10; INTF §21, §27*

### 13.1 Security Requirements (SEC-001 to SEC-010) — All Mandatory

| ID | Requirement | Convention |
|---|---|---|
| SEC-001 | No optimization may bypass security instructions | System instructions are priority 1; never pruned under any budget condition |
| SEC-002 | Authorization boundaries preserved through all stages | Every cache/retrieval/compression/routing/tool operation checks authorization |
| SEC-003 | Cache keys must be tenant/user/workspace aware | `tenant_id` is always the first key namespace component |
| SEC-004 | No sensitive data cached unless policy permits | PIIClassifier runs before every cache write |
| SEC-005 | Compression must preserve security-relevant constraints | Security constraints are defined in compression contract invariants |
| SEC-006 | Tool-result filtering must not remove auth/audit fields | Required fields list is defined per-tool before filtering runs |
| SEC-007 | Semantic cache reuse must verify authorization and freshness | Four-check gate (§9.1) is mandatory |
| SEC-008 | All optimization transformations must be auditable | AuditRecord emitted for every transformation |
| SEC-009 | Configurable data-retention policies | Enforced via `MemoryRetentionPolicy` and `CacheWriteRequest.ttl` |
| SEC-010 | Provider-specific caching must not weaken security | Provider profiles specify security constraints; never assumed generic |

### 13.2 Tenant Isolation Rules (Non-Negotiable)

The following operations are **unconditionally prohibited** (INTF §27.2):
- Serving a cache entry where `entry.tenant_id != request.tenant_id`
- Serving a memory entry where `entry.tenant_id != request.tenant_id`
- Including cost from one tenant in another tenant's cost report
- Propagating events across `tenant_id` boundaries
- Sharing policy objects across `tenant_id` boundaries without explicit multi-tenant policy

### 13.3 PII Handling

Before any content is logged, cached, emitted to the event bus, or included in an audit record, it must pass through `PIIClassifier`. Redacted content replaces PII with `[REDACTED-{CATEGORY}]`. Fields tagged `is_safe_to_log: false` must not appear in any log output.

### 13.4 Audit Record Immutability

> [!CAUTION]
> Audit records are immutable and may not be deleted (INTF §19.5). Minimum retention: 7 years (INTF §31.4). Any code that attempts to delete or overwrite an audit record is a security violation.

### 13.5 Security Requirements (SEC-011 to SEC-016) — Added 2026-09-15, All Mandatory

*Traceability: ARCH §47.2; INTF §43.1–43.5; PS §52*

| ID | Requirement | Convention |
|---|---|---|
| SEC-011 | Budget controls must never bypass or substitute for authorization/policy/security | `governance/spend/` (SGE, §7.8) is evaluated independently; `WITHIN_BUDGET` never implies authorized, `HALTED` is never inferred from a failed authorization check |
| SEC-012 | Sensitive data must be classified before optimization admission | `governance/data/` (DGE) classification runs before every `pipeline/`, `context_engine/`, or `cache_economics/` admission; `UNKNOWN` defaults to `SENSITIVE` |
| SEC-013 | Deletion/erasure must propagate to every persisted surface | DGE's `DeletionPropagationReport` (INTF §43.2) covers cache (exact/semantic), memory, cost ledger, logs, traces, checkpoints |
| SEC-014 | Tool/MCP metadata is part of the security boundary | `governance/tool_trust/` (TMG) authenticates identity and validates schema/staleness/integrity before a tool result is trusted, independent of TE-001's ROI signal |
| SEC-015 | Consequential/irreversible actions require human approval | `governance/human_approval/` (HAG) gates only the policy/risk-designated subset; never every action |
| SEC-016 | Externally-sourced content is untrusted by default | `governance/content_integrity/` (CIS) screens content before any optimization stage admits, ranks, compresses, caches, or acts on it |

### 13.6 Spend Governance (SGE) (H05)

*Traceability: ARCH §47.2.1; INTF §43.1; PS §52.5; OBJ-027; SEC-011; AC-043*

`governance/spend/` supports independently configurable spend limits (absolute and/or rate-based) at tenant, organization, user, and application scope. Runaway-cost detection acts before a configured limit is exhausted, not only after. On breach, spend for the affected scope is halted/throttled while other scopes remain unaffected (§13.2 tenant isolation applies to budget scoping too). `T0.1`/`AR-002`/`T3.1` may consume remaining-budget as an input signal. On exhaustion mid-execution, follow the existing `SUSPENDED_BUDGET_EXCEEDED` handling (§16.1): return `PARTIAL`, remaining budget = 0, never silently overspend.

### 13.7 Data Governance (DGE) (H07)

*Traceability: ARCH §47.2.2; INTF §43.2; PS §52.7; OBJ-029; SEC-012, SEC-013; AC-045*

`governance/data/` classification determines encryption, retention, and caching eligibility, and runs before the `PIIClassifier` check already required at every cache write (§9.3, §13.3) — DGE's `SensitivityClassificationResult` wraps and extends `PIIClassificationResult`, it does not replace it. Every cache, memory layer, ledger, and log/trace has a configurable retention period (Appendix C). A deletion/erasure request must be verifiable — DGE must be able to report which surfaces still hold data derived from a given source after a deletion request, not merely assert propagation. No specific regulatory regime (GDPR, HIPAA, PCI-DSS) is hard-coded; applicability is deployment configuration (`SOURCE-GAP`, consistent with the foundational documents — see §53-equivalent gap discipline in the authoritative chain).

### 13.8 Tool / MCP Trust (TMG) (H11)

*Traceability: ARCH §47.2.3; INTF §43.3; PS §52.11; SEC-014; AC-048*

Before a tool/MCP result is admitted (in addition to §11's TE-006 result filtering), `governance/tool_trust/` must: authenticate the tool/MCP server's identity; validate the tool schema for integrity (a schema change between calls is a staleness/trust event, never silently accepted); validate authorization independently of `TE-001`'s ROI signal (cost efficiency never substitutes for a trust decision); and revalidate discovered availability on a policy-defined interval rather than assuming it persists indefinitely. An identity or schema-integrity failure is fail-closed (`QUARANTINED`), never an optimization fallback that uses the untrusted tool anyway.

### 13.9 Human Approval (HAG) (H13)

*Traceability: ARCH §47.2.4; INTF §43.4; PS §52.13; SEC-015; OBJ-033; AC-050*

The designated-action list requiring human approval is a policy decision, configurable per tenant/workflow — never a fixed list in code, and never applied to every action (routine, reversible, low-risk actions proceed under the §7.7 ownership model without a gate). A pending approval follows the same suspension handling as any other interruption cause (§16.1): checkpoint, return `PARTIAL`/`AWAITING_APPROVAL`, resume once approval is granted or denied. Approval requests carry a configurable timeout; on timeout, apply the policy-defined default (deny, or escalate) — never silently `APPROVED`. If the approval mechanism itself is unavailable, the gated action is blocked (fail-closed).

### 13.10 Content Integrity / Prompt-Injection Screening (CIS) (H14)

*Traceability: ARCH §47.2.5; INTF §43.5; PS §52.14; SEC-016; OBJ-034; AC-051*

`governance/content_integrity/` screens RAG chunks, search results, tool outputs, sub-agent handoffs (§12.4), and MCP tool results (§13.8) before any optimization stage admits, ranks, compresses, caches, or authorizes an action based on that content — not only end-user input (already covered by the Sanitizer, §6). This ordering requirement takes precedence over §7.6's `HYBRID` preference: precomputation may speed up the screening mechanism, but the screening step itself may never be skipped or deferred until after admission.

> [!CAUTION]
> A screening failure (the check cannot complete) is fail-closed — reject or quarantine the content. It is never treated as an optimization failure that falls back to admitting the unscreened content.

---

## 14. Token and Cost Accounting Conventions

*Traceability: ARCH §27; INTF §28; PS §46*

### 14.1 Token Ledger Fields — Mandatory Reporting

Every request must produce a `CostLedgerEntry` carrying all standard ledger fields defined in ARCH §27.1. Incomplete ledger entries are marked `unverified`. Never fabricate savings figures.

**Mandatory input fields:** `tokens.raw_input`, `tokens.sanitized`, `tokens.query_compressed`, `tokens.context`, `tokens.retrieved`, `tokens.pruned`, `tokens.deduplicated`, `tokens.context_compressed`, `tokens.cached_input`, `tokens.uncached_input`

**Mandatory output fields:** `tokens.raw_output`, `tokens.optimized_output`, `tokens.truncated`, `tokens.expanded_retry`

**Mandatory cost fields:** `cost.input`, `cost.output`, `cost.cache`, `cost.compression`, `cost.tool`, `cost.total_optimized`, `cost.baseline_estimated`, `cost.net_savings`, `cost.savings_pct`

**Mandatory performance fields:** `perf.e2e_latency_ms`, `perf.ttft_ms`, `perf.model_latency_ms`, `perf.compression_latency_ms`, `perf.cache_latency_ms`, `perf.tool_latency_ms`

### 14.2 Net Savings Formula (Canonical)

```
Baseline Cost =
    Baseline Input Tokens  × Input Rate
  + Baseline Output Tokens × Output Rate
  + Tool Costs
  + Other Provider Charges

Optimized Cost =
    Uncached Input Tokens  × Input Rate
  + Cache Read/Write Cost
  + Compression Cost
  + Optimized Output Tokens × Output Rate
  + Tool Costs
  + Routing/Cascade Costs
  + Other Provider Charges

Net Savings = Baseline Cost - Optimized Cost
Savings %   = Net Savings / Baseline Cost × 100
```

### 14.3 Pricing Configuration

- Pricing is configurable, not hard-coded (ARCH §27.3).
- `pricing_version` must be included in every `CostLedgerEntry`.

### 14.4 Unverified Accounting

If token accounting fails: mark as `unverified`. Never fabricate savings. Never report an optimization as successful if savings are unverified.

### 14.5 Outcome-Based Metrics

The accounting engine must maintain: `outcome.cost_per_successful_outcome`, `outcome.tokens_per_successful_outcome`, `outcome.cost_per_coding_task`, `outcome.cost_per_agent_workflow`, `outcome.cost_per_verified_answer`.

---

## 15. Quality Validation Conventions

*Traceability: ARCH §28; INTF §17; PS §9, §39*

### 15.1 Quality Gate — Mandatory

> [!CAUTION]
> Token reduction must **never** be accepted without quality validation. Every optimization technique must be evaluated using **Baseline vs. Optimized** comparison across all dimensions in ARCH §28: accuracy, task completion, retrieval quality, factuality, schema compliance, semantic equivalence, safety, user satisfaction, latency, cost.

### 15.2 Quality Threshold Enforcement

Every technique has a configurable quality threshold in `OptimizationPolicy.min_quality_score`. If violated:

1. Roll back using the `ReversibilityRecord`.
2. Fall back to the baseline representation.
3. Record failure in the `AuditRecord`.
4. Do **not** report as a successful saving.
5. Emit a `QUALITY_VIOLATION` event.

### 15.3 Compression Contracts (QO-001)

Every aggressive transformation must define invariants that must survive:

| Context Type | Invariants That Must Survive |
|---|---|
| **Coding** | Function signatures, imports, types, API contracts, security constraints, relevant test expectations, error locations |
| **RAG** | Entities, numbers, dates, citations, source attribution |
| **Agent** | Objective, constraints, decisions, completed actions, pending actions, unresolved failures |

### 15.4 Delayed-Relevance Protection (QO-003)

The framework must recognize that information appearing irrelevant at one step may become critical later. Mitigation: use `ReversibilityRecord` (CL-004), context dependency graph (CL-001), freshness metadata (CL-002), source references, and re-fetch mechanisms.

### 15.5 Verifier-Guided Escalation (AR-004)

Where a deterministic or inexpensive verifier exists:
1. Run lower-cost inference.
2. Verify output with deterministic verifier.
3. Escalate/retry only on verifier failure.

Candidate verifiers: unit tests, schema validation, type checking, static analysis, business rules, citation checks, format validation.

---

## 16. Fallback and Failure Conventions

*Traceability: ARCH §30, §46; INTF §22, §25, §42*

### 16.1 Every Stage Has a Fallback (Mandatory)

| Failure Condition | Fallback |
|---|---|
| Cache unavailable | Continue without cache; count as miss |
| Semantic match uncertain | Treat as cache miss; proceed to full pipeline |
| Sanitization fails | Use original input verbatim |
| Classification fails | Use safe general policy |
| Entity extraction fails | Use unstructured retrieval path |
| Ranking fails | Use bounded recent/relevance fallback |
| Pruning fails | Retain bounded original context |
| Compression fails | Use uncompressed context |
| Model routing fails | Use `OptimizationPolicy.default_model_id` |
| Reasoning budget fails | Use safe default budget |
| Output schema fails | Use fallback validation/re-prompt |
| Agent stop decision fails | Continue per safe workflow policy only |
| Token accounting fails | Mark as unverified; never fabricate savings |
| PII scrubbing required, fails | REJECT (fail-closed) |

### 16.2 Fail-Open vs. Fail-Closed

| Stage | Mode |
|---|---|
| T0.x — Normalization (always required) | Fail-closed: REJECT |
| T1.x — Context ops | Fail-open: RESTORE_ORIGINAL |
| T2.x — Cache lookup | Fail-open: MISS, proceed |
| T3.x — Model routing | Fail-open: use default model |
| OI.x — Decision | Fail-open: PROCEED_WITHOUT_OPTIMIZATION |
| TE.x — Tool executor | Fail-open: SKIP_TOOL |
| AL.x — Agent loop | Fail-open: EXIT_LOOP |
| QV.x — Quality validation | Configurable: WARN_ONLY default |
| SEC.x — PII scrubbing (required) | Fail-closed: REJECT |
| RE — Reconciliation Engine | Fail-closed: BLOCK_NEXT_STEP until `CONSISTENT` (`MUTATION_DETECTED` / `STALE_DETECTED` / `PRECONDITION_FAILED` all block) |
| CIG — Context Integrity Gate | Fail-closed on Tier 0/1 violation: `TIER_VIOLATION` blocks inference |
| PRV — Permission Revalidation | Fail-closed: unresolved revalidation failure blocks the affected step |
| DPE — Dynamic Policy Evaluation | Fail-closed only for restrictive security policy changes: `SUSPEND_REQUIRED` / `CANCEL_REQUIRED`; optimization-policy changes are `NO_ACTION` |
| CAR — Capability/Availability Resolver | Fail-open: failover to next available model in the configured cascade |
| CEC — Context Expansion Controller | Fail-open: `SUSPENDED_BUDGET_EXCEEDED`, never a silent truncation |
| SRP — Stale Result Protection | Fail-open: re-fetch or re-execute; never serve a stale result silently |
| SPM — Supersession Manager | Lifecycle control, not a request-path stage: old execution is marked `SUPERSEDED`, finalized, and never resumed |
| SGE — Spend Governance Engine | Conservative, not fail-open: budget-unavailable defaults to policy-configured `THROTTLED`/`HALTED`, never `WITHIN_BUDGET` |
| DGE — Data Governance Engine | Fail-closed: classification-unavailable defaults to `SENSITIVE`, never `NON_SENSITIVE` |
| TMG — Tool/MCP Trust Gate | Fail-closed: identity/schema-integrity failure returns `QUARANTINED` |
| HAG — Human Approval Gate | Fail-closed: approval-mechanism-unavailable blocks the gated action; timeout applies the policy default, never silent `APPROVED` |
| CIS — Content Integrity Screen | Fail-closed: `SCREENING_UNAVAILABLE` rejects or quarantines the content |
| FTR — Feasibility Tier Registry | Fail-open to reduced capability: actual access degrading below the declared tier requires redeclaring a lower tier, never silently overclaiming |
| XEC — Cross-Execution Coordinator | Fail-closed for the losing side of a conflict: an unresolvable conflict blocks the losing execution's write/action; never an unreconciled dual-write |
| SPC — Self-Protection Controller | Fail-open to the unoptimized path on latency/compute budget exhaustion — **unless** doing so would skip a `governance/` check, in which case fail closed (§7.9) |
| VCL — Verifier Calibration Layer | Fail-open, conservative: below-threshold result applies the existing Quality-Aware Fallback (§15.2) — escalate, restore, or disable |

> [!IMPORTANT]
> Optimization failure must **not** become application failure unless the optimization itself is explicitly required by policy.
> This extends to the Dynamic Execution components: a stale, superseded, or unreconciled result must never silently become the authoritative result of a request (ARCH §46.2.5, §46.2.11, §46.2.12).
> It extends further to the Governance/Safety Plane (SGE, DGE, TMG, HAG, CIS): these are security/policy-adjacent controls, and their unresolved failure defaults are conservative (deny/quarantine/classify-as-sensitive), never permissive — a `governance/` failure must never be treated as an ordinary optimization failure that falls back to the unoptimized-but-otherwise-unchecked path.

### 16.3 Cross-Execution Concurrency (XEC) (H12)

*Traceability: ARCH §47.3.2; INTF §43.7; PS §52.12; OBJ-032; AC-049*

`execution/cross_execution/` (XEC) extends `execution/reconciliation/` (RE) and `execution/supersession/` (SPM) from single-execution to multi-execution scope for shared resources (files, tickets, shared memory per CL-006, external systems). Shared resources participating in cross-execution coordination carry a version or equivalent conflict-detection token. When two executions mutate the same resource, the second write must not silently overwrite context the first write depended on — XEC raises a conflict. A decision made from a shared-state snapshot that has since changed is a stale-result case under SRP (§16.2) and must be revalidated before another execution relies on it. Locking is required only where the shared resource/action is non-idempotent and concurrently reachable — not universally. Non-idempotent operations are never blindly replayed across executions, extending RCO's resume-time invariant (§14 equivalent, INTF §42.13) to cross-execution scope.

---

## 17. Observability Conventions

*Traceability: ARCH §31; INTF §19; PS §12*

### 17.1 Distributed Tracing — Mandatory

Every component invocation must emit an `OptimizationSpan`. The end-to-end trace chain (INTF §19.4) must be navigable via `request_id` + `span_id` with no gaps. Breaks in the chain are a convention violation.

### 17.2 Log Entry Conventions

Every log entry must carry: `timestamp`, `level`, `request_id`, `correlation_id`, `span_id`, `parent_span_id`, `tenant_id`, `component_id`, `event_type`, `message`, `fields`.

| Level | Use |
|---|---|
| DEBUG | Internal state; off in production by default |
| INFO | Normal optimization decisions, cache hits, model selections |
| WARNING | Fallback triggered, quality degraded, near-threshold behavior |
| ERROR | Stage failure, policy violation, security check failure |
| CRITICAL | Security breach, irrecoverable failure, data integrity issue |

### 17.3 Three Dashboard Tiers (Mandatory)

| Dashboard | Key Metrics |
|---|---|
| Executive | Total AI spend, cost saved, savings %, model mix, cache utilization, token growth, quality impact |
| Engineering | Tokens by stage, context size, compression ratio, model routing, tool usage, cache hit rate, latency, failures, fallbacks |
| Product / Operations | Cost/workflow, cost/user/team, cost/business transaction, quality trends, adoption, ROI |

### 17.4 Event Bus Requirements (INTF §23.3)

AUDIT and POLICY_VIOLATION events must be durably written. Audit events must be replayable. Events must not be delivered across `tenant_id` boundaries. Event payloads must not contain raw PII.

---

## 18. Configuration Conventions

*Traceability: ARCH §38; PS §23; INTF §20*

### 18.1 All Optimization Behavior Is Configurable

Every optimization parameter must be configurable without code changes. Minimum configurable parameters (ARCH §38): maximum context tokens, per-intent budgets, compression thresholds, minimum quality thresholds, cache TTL, semantic similarity threshold, retrieval Top-K limits, model routing policy, model escalation policy, reasoning budgets, output token limits, tool-output field policies, early-exit thresholds, tenant policies, data retention policies.

### 18.2 Policy Versioning

Every `OptimizationPolicy` carries `policy_version` and `schema_version`. Policy versions are immutable once approved — only supersession is permitted. Every policy update requires `change_reason` and (for production policies) `approved_by`.

### 18.3 Policy Hot-Reload

The policy engine must support real-time policy updates without requiring a service restart. Active requests in-flight complete under the policy active at request admission time.

### 18.4 Rollout Configuration

`OptimizationPolicy.rollout_pct` controls the fraction of traffic covered. `shadow_mode: true` applies the policy without modifying production responses.

---

## 19. Testing Conventions

*Traceability: ARCH §32, §36; INTF §30; PS §24 NFR-013*

### 19.1 Every Optimization Stage Is Independently Testable

No stage may be implemented as a side effect of another. Each stage must be testable in isolation by providing a `StageContext` and verifying the `StageResult`.

### 19.2 Contract Test Requirements (INTF §30)

Every interface in EAIOC-INTF-001 must have a contract test suite that validates:
1. All REQUIRED fields present and correctly typed
2. Type constraints and enum values
3. Backward compatibility with the previous MINOR version
4. Idempotency (same input → same output, or graceful dedup)
5. Fallback behavior (force stage failure; verify fallback path)
6. Tenant isolation (cross-tenant access attempt must be rejected)
7. Policy enforcement (blocked operation returns `OPT-2xxx`)

### 19.3 Consumer-Driven Contracts

Every consumer of an interface must publish a pact. The producer must satisfy all published pacts before a version is promoted.

### 19.4 Benchmark Corpus Requirements (ARCH §32.1)

Test corpus must include: simple prompts, long prompts, long-context tasks, RAG tasks, coding tasks, debugging tasks, tool-heavy agent tasks, multi-turn conversations, structured extraction, comparative queries, procedural queries, high-risk/safety-sensitive workflows where permitted.

### 19.5 Per-Technique Validation Matrix

Each optimization technique must pass its validation matrix (ARCH §32.3) before production activation. No technique may be enabled by default without this validation.

### 19.6 Regression Test Dimensions (ARCH §35)

Every model, prompt, retrieval, or orchestration change must be evaluated against the benchmark corpus across: token usage, cost, latency, quality, retrieval recall, cache hit rate, model routing accuracy, tool-call count, agent step count.

---

## 20. Benchmarking and Evaluation Conventions

*Traceability: ARCH §32, §42; PS §14, §21*

### 20.1 Baseline vs. Optimized

Every benchmark compares: **BASELINE** (original behavior without optimization) against **OPTIMIZED** (full pipeline). Comparison dimensions: input tokens, output tokens, total tokens, cached tokens, model calls, tool calls, cost, latency, quality, failure rate.

### 20.2 Benchmark Percentages Are Not Production Guarantees

> [!CAUTION]
> Research benchmark percentages (e.g., 44.1% input reduction, 67.4% output reduction) are **reference targets for local validation, not production guarantees**. Every percentage must be validated on the organization's own workload.

### 20.3 Optimization Maturity Model (ARCH §42)

| Level | Name | Required Evidence Before Promotion |
|---|---|---|
| 0 | RESEARCH | Literature/provider evidence |
| 1 | EXPERIMENTAL | Local benchmark; offline validation |
| 2 | SHADOW | Production-like traffic; no user-visible modification |
| 3 | CONTROLLED PILOT | Limited production traffic; baseline fallback preserved |
| 4 | PRODUCTION | Validated policy; monitoring and rollback in place |
| 5 | AUTO-TUNED | Continuous evidence-based adjustment; governance approval; automatic regression rollback |

No technique may reach PRODUCTION (Level 4) without passing Levels 1 and 2.

### 20.4 Controlled Rollout (EL-002)

Progressive activation: Shadow → 1% → 5% → 25% → 50% → 100%. Automatic rollback triggers on quality/cost regressions.

### 20.5 Continuous Policy Learning (EL-004)

> [!CAUTION]
> Learned policies may not silently change production behavior without configured human approval.

---

## 21. Provider and Model Profile Conventions

*Traceability: ARCH §25; INTF §8; PS §41*

### 21.1 Versioned Model Profiles Are Mandatory

A versioned `ModelProfile` must exist for every supported provider/model before that model can be referenced in any routing decision. The profile must specify all dimensions listed in ARCH §25: input price, output price, cache write/read price, cache TTL, context limit, output limit, reasoning controls, tool support, structured output support, batch support, latency characteristics, known cache constraints, known routing constraints, compliance/data-residency requirements.

### 21.2 Provider-Specific Assumptions Are Prohibited

> [!CAUTION]
> The optimizer must **not** assume that an optimization valid for one provider or model is valid for another. All provider-specific behavior is resolved through `ModelProfile` only.

### 21.3 Pricing Is Fetched, Not Hard-Coded

Pricing values must be fetched from provider APIs or a configurable pricing store. Hard-coded pricing constants are a convention violation.

---

## 22. Versioning and Compatibility Conventions

*Traceability: INTF §24*

### 22.1 Schema Version Policy

```
MAJOR.MINOR.PATCH

MAJOR: Breaking change (field removal, type change, required field added)
MINOR: Additive change (new optional field, new enum value)
PATCH: Non-functional (description, documentation only)
```

### 22.2 Deprecation Protocol

1. Field is marked `deprecated_in: "<version>"` and `removal_planned: "<next_major>"`.
2. Producer continues to emit the deprecated field for one full MAJOR version.
3. New consumer code must not rely on deprecated fields.
4. Breaking change must appear in CHANGELOG with a migration guide.

### 22.3 API Version Negotiation

Clients send `Accept-Version: "1.*"`. The control plane responds with `Schema-Version: "1.3.0"`. If the version is unsupported: HTTP 406 + `{"min_version": "1.0.0", "max_version": "2.1.0"}`.

### 22.4 Checkpoint Conventions and Execution State Portability (H16)

*Traceability: ARCH §47.11; INTF §42.4, §42.13; PS §52.16; AC-053*

> [!IMPORTANT]
> **RESUME != REPLAY.** Resuming an execution from a checkpoint (`execution/checkpoint/`, CPM) always re-validates current state via `execution/permission_revalidation/` (PRV), `execution/dynamic_policy/` (DPE), and `execution/capability_resolver/` (CAR) before continuing — it never simply replays the original sequence of actions from the beginning.

Checkpoint records (`CheckpointRecord`, INTF §42.4) are durable where required, versioned (`execution_version`, `context_version`, `workflow_version`), provenance-aware, authorization-aware, and policy-aware. They must be defined in **provider/session-neutral terms**: no checkpoint field may be, or require, a specific provider's session object, conversation-thread ID, or proprietary state format. A checkpoint produced under one model/provider selection must remain interpretable for RCO's reconciliation steps even if `model_selected` changes on resume (e.g., CAR-driven failover). A provider's native resumability (e.g., a conversation/session ID) may be used as an optimization to avoid re-transmitting cached context, but correctness of resume/reconciliation must never depend on that provider-specific mechanism being present. Non-idempotent side effects recorded in `completed_actions` are never blindly replayed — RCO consults `WVM.completed_actions` and skips every completed step before resuming from the first unresolved one (§16.2).

---

## 23. Developer-Agent Module Conventions

*Traceability: ARCH §22; INTF §13; PS §4C*

All 25 DA modules are **mandatory first-class requirements**. Each module must be implemented as an independently testable optimization stage.

| Module | ID | Key Convention |
|---|---|---|
| Code-Aware Context Pruner | DA-001 | Must preserve task-critical code dependencies per dependency graph (CL-001) |
| Symbol-Level Context Selector | DA-002 | Prefer `content_type: SIGNATURE_ONLY` when body is not required |
| Repository Map Generator/Cache | DA-003 | Repository map cached with commit-based invalidation; compact tree format |
| Git-Diff Optimizer | DA-004 | Expose `optimized_diff`; retain `raw_diff` only for audit |
| Terminal Output Optimizer | DA-005 | Filter, summarize, structure, and deduplicate; never pass raw terminal output |
| Compiler/Test/Linter Error Extractor | DA-006 | Extract actionable failures only; include `file_path`, `line_number`, `expected_value`, `actual_value` |
| Dynamic MCP / Tool Selector | DA-007 | Expose only tools relevant to current task context |
| Tool Schema Optimizer | DA-008 | Minimize schema tokens; preserve invocation semantics, constraints, authorization |
| Sub-Agent Result Compressor | DA-009 | Compact structured findings; retain evidence, locations, confidence |
| Agent Memory Optimizer | DA-010 | Separate short-term task state from durable project/task memory |
| Code-Aware Semantic Deduplicator | DA-011 | Detect duplicate code, stack traces, errors, files, search results, diffs, and findings |
| Error-Driven Context Retrieval | DA-012 | Use error source locations to drive targeted retrieval |
| Developer-Agent Context Budget Manager | DA-013 | Dynamically allocate budgets across all sections per priority order (§8.2) |
| Agent Loop Cost/Token Controller | DA-014 | Track cumulative cost of reasoning-tool-reasoning cycles; stop low-value loops |
| Code Context Reorderer | DA-015 | Optimize ordering while preserving instruction precedence |
| File-Section Lazy Loader | DA-016 | Prefer metadata/signatures before loading complete files |
| Repository Search Result Optimizer | DA-017 | Rank and deduplicate; return smallest useful set |
| Agent Plan Context Optimizer | DA-018 | Retain active plan state, decisions, milestones, and unresolved work only |
| Code Edit Context Optimizer | DA-019 | Smallest safe context for a correct patch; preserve interfaces, dependencies, tests |
| Developer-Agent Output Controller | DA-020 | Bound verbose explanations, patches, summaries, machine-readable outputs |
| Agent Task Completion / Early-Exit | DA-021 | Prevent unnecessary work after sufficient completion |
| Developer-Agent Cache Layer | DA-022 | Exact, semantic, repository-aware, tool-result, search-result, map, and stable-context caching with freshness and authorization controls |
| Context Provenance Tracker | DA-023 | Record origin of every retained context item: file/path/line, tool, search, sub-agent, conversation turn, or memory |
| Context Invalidation Engine | DA-024 | Invalidate caches on file, branch, dependency, permission, or external state change |
| Patch / Change Impact Analyzer | DA-025 | Estimate affected files, symbols, tests, and dependencies; prioritize relevant context |

### 23.1 Coding-Agent Integration Feasibility Tiers (H08)

*Traceability: ARCH §47.10; INTF §43.6; PS §52.8; OBJ-030; AC-046*

Developer/coding-agent optimization remains a **first-class product requirement** (§5.1 of the authoritative Architecture) — this convention bounds *how deep* a given integration can reach, never *whether* coding-agent support is a priority. `providers/feasibility_tier/` (FTR) requires every coding-agent integration (Cursor, Claude Code, GitHub Copilot, Antigravity, Codex, and comparable platforms) to declare exactly one of five tiers before any DA-001–DA-025 module is wired to it:

| Tier | Access |
|---|---|
| Deep/Native | Full model-bound context observable/transformable pre-inference |
| Gateway/Interception | Request/response observable/transformable at the API/LLM gateway boundary |
| Plugin/Extension | Bounded by the platform's own extensibility surface |
| Protocol/Tool-Level | Optimizes only what passes through an MCP/tool protocol surface |
| Advisory/Observability-Only | Observes exposed telemetry only; can advise, cannot transform/block |

The declared tier bounds which DA-001–DA-025 modules can actually be applied for that platform (`TierDeclaration.reachable_modules`, INTF §43.6). Implementations must not report or imply full-pipeline optimization coverage for a platform whose declared tier is Protocol/Tool-Level or Advisory/Observability-Only. If a platform's actual access degrades below its declared tier at runtime (e.g., an extensibility API is deprecated), the integration must redeclare a lower tier — silently continuing to claim the stale, higher tier is prohibited (§24.4).

---

## 24. Anti-Patterns (Prohibited Practices)

*Traceability: ARCH §41; PS §25, §48; INTF §33*

> [!CAUTION]
> The following practices are **explicitly prohibited**. Code that implements these patterns must be rejected in code review.

### 24.1 Primary Anti-Patterns (PS §25)

- Compressing everything indiscriminately
- Sending every retrieved chunk to the LLM
- Using a fixed Top-K for every query
- Always using the strongest/most expensive model
- Always using the cheapest model
- Passing complete tool responses to agents
- Repeating the same system instructions unnecessarily
- Carrying complete conversation history indefinitely
- Re-running completed agent steps
- Using semantic cache without freshness/authorization checks
- Claiming savings without accounting for optimization overhead
- Optimizing token count at the expense of correctness
- Treating research benchmark percentages as guaranteed production savings

### 24.2 Extended Anti-Patterns (PS §48)

- Running every optimization on every request
- Using an expensive optimizer to save fewer tokens than the optimizer costs
- Treating semantic similarity as sufficient proof of cache safety
- Caching mutable/permission-sensitive data without validation
- Optimizing context without considering delayed relevance
- Spawning sub-agents without value/cost analysis
- Executing tools without considering expected information gain
- Exposing large tool catalogs when on-demand discovery is possible
- Assuming larger context windows eliminate context-management problems
- Treating a cache hit as a quality guarantee
- Treating token reduction as equivalent to business value
- Allowing learned policies to change production behavior without governance
- Mixing inference-serving optimizations with prompt optimization without separate measurement
- Removing context without provenance or recovery information where recovery is required
- Using provider-specific behavior as a universal architectural assumption

### 24.3 Interface-Level Prohibited Patterns (INTF §33)

| Anti-Pattern | Prohibition | Correct Approach |
|---|---|---|
| Provider field in core schema | PROHIBITED | Use `provider_hints: map<string, any>` |
| Framework-specific import in optimizer | PROHIBITED | Use adapter interface |
| PII in log fields | PROHIBITED | Redact via `PIIClassifier` before logging |
| Cross-tenant cache key | PROHIBITED | Always include `tenant_id` in cache key |
| Blocking optimization stage | PROHIBITED | All stages must be fail-open (except security) |
| Non-idempotent cache write | PROHIBITED | Always use `idempotency_key` |
| Missing `tokens_avoided` metric | PROHIBITED | Report 0 if none |
| Missing `request_id` in any response | PROHIBITED | Propagate from request |
| Hard-coded model name in optimizer | PROHIBITED | Read from `ModelProfileRegistry` |
| Serving memory cross-tenant | PROHIBITED | `tenant_id` filter enforced at storage layer |
| Deleting audit records | PROHIBITED | Audit records are immutable |
| Raw credentials in interface fields | PROHIBITED | Reference by secret handle only |
| Caching mutable data without TTL | PROHIBITED | TTL required for all cache entries |
| Full conversation replay to sub-agent | PROHIBITED | Use `SubAgentContextHandoff` |

### 24.4 Hardening Anti-Patterns (2026-09-15) (H20, Anti-Scope Boundary)

*Traceability: ARCH §47.13, §41; INTF §43.15; PS §52.20, §25, §48; OBJ-035*

- Building the Control Plane as a replacement agent orchestrator, model-training system, model-runtime infrastructure, provider infrastructure, general-purpose developer IDE, or coding-agent UX replacement instead of integrating with those systems through the adapter layer (§2.1) and feasibility-tier model (§23.1)
- Assuming a coding-agent integration provides complete request interception when only a Plugin/Extension, Protocol/Tool-Level, or Advisory/Observability-Only tier is actually declared (§23.1)
- Treating a verifier's `passed = true` output as ground truth without a calibrated confidence score and acceptance threshold (§7.10)
- Allowing agent-owned working memory to override Control Plane execution state, authorization state, policy state, or context/workflow version (§12.6)
- Admitting retrieved, tool, or other externally-sourced content into an optimization stage before it has passed content-integrity/prompt-injection screening (§13.10)
- Allowing the Control Plane's own latency, compute, or cost overhead to exceed the value of the optimization it enables (§7.9)
- Routing every action through the Human Approval Gate rather than only the policy/risk-designated subset (§13.9) — this defeats the Control Plane's own value proposition
- Treating a `governance/` module's (SGE, DGE, TMG, HAG, CIS) fail-safe default as an ordinary fail-open optimization fallback (§16.2)
- Defaulting a component's `decision_ownership` (§7.7) to `EXECUTION_OWNERSHIP` by omission instead of `ADVISORY`
- Choosing `SYNC` operating mode (§7.6) where `HYBRID` would achieve equivalent safety/quality at lower latency/cost

---

## 25. Implementation Priority Conventions

*Traceability: ARCH §36; PS §27*

P1/P2/P3 features must each pass the validation matrix before being enabled by default. P5 is optional and infrastructure-dependent.

### P0 — Foundation (Unblocked; No Validation Gate Required)

Token accounting, Baseline benchmark harness, Sanitizer, Context policy, Prompt assembler, Output controls, Observability, Quality evaluation

### P1 — High-Confidence Optimization (Validation Matrix Required)

Exact prompt caching, Context deduplication, Context pruning, Adaptive Top-K, Tool-output filtering, Query compression, Context compression, Output schema/length control, Soft reset

### P2 — Cost Intelligence (Validation Matrix + Quality Baseline Per Model Required)

Model routing, Model cascade, Semantic caching, Tool-result caching, Reasoning-budget controller, Agent early exit

### P3 — Advanced Optimization (Benchmarked Formula Required)

Token-aware ranking, Context reordering, Learned routing, Advanced compression, Dynamic budget allocation, Batch optimization

### P4 — Control-Plane Intelligence

Optimization Decision Engine, Cost-of-Optimization Controller, Adaptive Optimization Depth, Context Utility/ROI scoring, Outcome-based optimization, Context dependency graph, Freshness-aware context management, Dependency-aware cache invalidation, Reversible optimization, Progressive context compaction, Cache economics, Cache fragmentation diagnosis, Cache-aware context construction, Tool-call ROI, Tool argument optimization, Dynamic tool loading, Programmatic tool execution, Agent loop progress measurement, Sub-agent economics, Marginal-gain model routing, Verifier-guided escalation, Shadow optimization, A/B experimentation, Continuous policy learning

### P5 — Infrastructure-Dependent Inference Optimization (Optional)

> [!IMPORTANT]
> P5 must remain **separate** from prompt/context optimization (P0–P4). Never combine P5 measurements with P0–P4 savings reports without explicit decomposition.

KV/prefix cache optimization, Continuous batching, Speculative decoding, Quantization, Prefill/decode optimization, Inference scheduling, GPU utilization optimization

---

## 26. Evidence Classification Policy

*Traceability: ARCH §43.4; PS §49*

| Classification | Meaning | Usage |
|---|---|---|
| Research evidence | Literature or vendor benchmark | Supports validation as a candidate technique |
| Provider capability | Supported by provider; semantics vary | Requires local verification before reliance |
| Local benchmark result | Measured on organization's own workload | Required before P1 production activation |
| Production policy | Deployed based on local evidence | Requires governance approval |
| Production guarantee | Only established by local production evidence | Cannot be established by research alone |

> [!CAUTION]
> Only **local production evidence** may establish a production guarantee. No source-reported percentage constitutes a production guarantee without local validation on the organization's workload.

---

## 27. Requirements Traceability

*Traceability: ARCH §44; INTF §34*

Every implementation artifact must be traceable to at least one authoritative requirement. Artifacts that cannot be traced must not be introduced without architecture review.

| Requirement Area | Count | Status |
|---|---|---|
| Objectives (OBJ-001–014) | 14 | All covered — ARCH §44.1 |
| Objectives (OBJ-015–022, Dynamic Execution) | 8 | All covered — canonically labeled PS §51.13; ARCH §44.12 registry |
| Objectives (OBJ-023–035, Hardening) | 13 | All covered — ARCH §3, §44.7; conventions §7.6–7.10, §12.6, §13.5–13.10, §23.1 |
| Acceptance Criteria (AC-001–038) | 38 | All covered — ARCH §44.6 |
| Acceptance Criteria (AC-039–053, Hardening) | 15 | All covered — ARCH §44.10; INTF §43 |
| Security Requirements (SEC-001–010) | 10 | All covered — ARCH §44.2 |
| Security Requirements (SEC-011–016, Hardening) | 6 | All covered — §13.5 |
| Non-Functional Requirements (NFR-001–013) | 13 | All covered — ARCH §44.3 |
| Non-Functional Requirements (NFR-014, Self-Protection) | 1 | Covered — §7.9 |
| Hardening Requirements (H01–H20) | 20 | All covered — see §27.1 below |
| Optimization Domains (A–S) | 19 | All covered — ARCH §44.4 |
| Developer-Agent Modules (DA-001–025) | 25 | All present — ARCH §44.5 |
| Dynamic Execution Components (ESM, CVM, WVM, CPM, RE, CIG, CEC, PRV, DPE, CAR, SRP, SPM, RCO) | 13 | All covered — ARCH §46; §2.1 package layout |
| Hardening Components (SGE, DGE, TMG, HAG, CIS, FTR, XEC, SPC, VCL) | 9 | All covered — ARCH §47; §2.1 package layout |
| Interface Definitions | 71 (INTF-001–INTF-071) | Fully specified in EAIOC-INTF-001, incl. §42 Dynamic Execution Interfaces (INTF-050–062) and §43 Hardening Interfaces (INTF-063–071) |
| Schema Types | 260+ | Fully specified in EAIOC-INTF-001 |

### 27.1 Hardening Requirements (H01–H20) Convention Coverage

| H# | Requirement | Convention Section |
|---|---|---|
| H01 | Control Plane Operating Model | §7.6 |
| H02 | Advisor/Enforcer/Execution-Owner Boundary | §7.7 |
| H03 | Control Plane Self-Protection | §7.9 |
| H04 | Verified Net Optimization Economics | §7.8 |
| H05 | Enterprise Spend Governance | §13.6 |
| H06 | Verifier Confidence and Calibration | §7.10 |
| H07 | Data Governance | §13.7 |
| H08 | Coding-Agent Integration Feasibility Tiers | §23.1 |
| H09 | Memory Authority | §12.6 |
| H10 | Reflection and Loop Awareness | §12.2 (AL-002, unchanged) — no new convention required; existing waste-detection convention already applies expected-value classification, not iteration-count matching |
| H11 | Tool / MCP Trust Boundary | §13.8 |
| H12 | Shared State and Cross-Execution Concurrency | §16.3 |
| H13 | Human Approval for Consequential Actions | §13.9 |
| H14 | Prompt Injection and Malicious Content | §13.10 |
| H15 | Decision Explainability and Audit | §1.6, §17.1 (unchanged) — explanation retrievability already required for every decision category by the existing `ExplanationRecord` convention |
| H16 | Execution State Portability | §22.4 |
| H17 | Layer 3 Boundary: Integration, Not Implementation | §1.3 (unchanged) — Layer 3 separation already mandatory; no core-optimizer implementation of KV-cache/batching/speculative-decoding/quantization is permitted under §1.4 provider neutrality |
| H18 | Ownership Boundaries (token/context/cache/inference) | §1.3, §6 (unchanged) — three-layer separation already enforces this; Layer 3 remains awareness/integration/routing/policy only |
| H19 | Research Claims as Evidence, Not Guarantees | §20.2, §26 (unchanged) — reaffirmation only, no new convention |
| H20 | Anti-Scope: What the Control Plane Is Not | §24.4 |

**20/20 hardening requirements have an identifiable convention. PASS.**

---

## Appendix A — Performance Budgets per Stage

*Traceability: INTF §31.1*

| Stage Class | Max Overhead Latency | Max Overhead Tokens |
|---|---|---|
| T0.x — Normalization | < 10 ms | 0 |
| T1.x — Context Ops | < 50 ms | < 500 |
| T2.x — Cache Lookup (exact) | < 20 ms | 0 |
| T2.x — Semantic Cache | < 100 ms | 0 |
| OI.x — Decision | < 30 ms | < 200 |
| CE.x — Reordering | < 30 ms | 0 |
| T3.x — Routing | < 20 ms | 0 |
| TE.x — Tool Selection | < 50 ms | 0 |
| QV.x — Validation | < 200 ms | < 1000 |
| AL.x — Loop Control | < 10 ms | 0 |
| SGE — Spend Governance (budget check) | < 10 ms | 0 |
| DGE — Data Governance (classification) | < 30 ms | 0 |
| TMG — Tool/MCP Trust (identity/schema check) | < 20 ms | 0 |
| CIS — Content Integrity Screen | < 100 ms | 0 |
| VCL — Verifier Calibration | < 50 ms | 0 |
| **Total (end-to-end optimizer)** | **< 500 ms** | **< 2000** |

> [!NOTE]
> The above per-stage budgets are enforced by `intelligence/self_protection/` (SPC, §7.9). HAG's approval wait time is explicitly excluded from this budget — it is a suspension, not an optimization-stage latency, and is tracked separately per §13.9/§16.1's `AWAITING_APPROVAL` handling. FTR tier declaration/redeclaration is an `ASYNC` operation (§7.6) and is not counted against the synchronous request-path budget.

---

## Appendix B — Availability Requirements

*Traceability: INTF §31.2*

| Tier | Component | SLA |
|---|---|---|
| Critical | Core request path (T0.x → T3.x) | 99.9% |
| High | Cache (T2.x), Memory (CL-006) | 99.5% |
| Standard | Evaluation (EL.x), Reporting (AC.x) | 99.0% |
| Best-effort | Policy Learning (EL-004) | 95.0% |

---

## Appendix C — Data Retention Requirements

*Traceability: INTF §31.4*

| Data Type | Minimum Retention | Maximum Retention |
|---|---|---|
| Audit records | 7 years | Unlimited (immutable) |
| Cost ledger | 3 years | Unlimited |
| Optimization trace | 90 days | Configurable |
| Cache entries | Per TTL | Per policy |
| Memory (TURN layer) | Session lifetime | Configurable |
| Reversibility records | 24 hours | Configurable |
| Policy version history | 5 years | Unlimited |
| Deletion propagation reports (DGE) | 3 years | Unlimited (needed to prove erasure was honored) |
| Approval records (HAG) | 7 years | Unlimited (audit-equivalent; §13.4 immutability applies) |
| Checkpoint records (CPM) | 24 hours (default TTL) | Configurable per workflow criticality |

---

## Appendix D — Integration Modes

*Traceability: ARCH §33*

| Mode | Description |
|---|---|
| A | Python SDK / library |
| B | CLI |
| C | API / middleware service |
| D | Drop-in integration at prompt assembly |
| E | Agent-orchestration middleware |
| F | CI/CD or GitHub Action for benchmark / regression testing |

All integration modes use the same underlying interface contracts. Mode-specific implementations are adapters over the core interfaces — never forks of the core.

---

*End of EAIOC-CONV-001 — Engineering Conventions v1.0.0*

*Authoritative sources:*
- *`Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt` (EAIOC-SPEC-001)*
- *`architecture.md` (EAIOC-ARCH-001)*
- *`interfaces.md` (EAIOC-INTF-001)*
