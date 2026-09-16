# Architecture Document
# Enterprise Agent & LLM Inference Optimization Control Plane
---

**Document ID:** EAIOC-ARCH-001
**Revision:** 1.3 — Final Foundational Document Reconciliation (Section 48)
**Status:** PRE-IMPLEMENTATION — Pending stakeholder approval
**Source (authoritative):** `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt` (as hardened 2026-09-15, PS §52; reconciled 2026-09-16, PS §51.13)
**Engineering Spec cross-reference:** `Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md` (EAIOC-SPEC-001 Rev 1.4)
**Date:** 2026-09-08
**Amendment history:**
- 2026-09-10 — Section 46 added: Dynamic Execution Architecture (ESM/CVM/WVM/CPM/RE/CIG/CEC/PRV/DPE/CAR/SRP/SPM/RCO), tracing PS §51 / EAIOC-SPEC-001 §41 (Rev 1.1).
- 2026-09-15 — Section 47 added: Architecture Hardening Amendment — Operating Model, Governance, and Trust Boundaries, tracing PS §52 (H01–H20) / EAIOC-SPEC-001 §42, OBJ-023–035, SEC-011–016, NFR-014, AC-039–053 (Rev 1.2).
- 2026-09-16 — Section 48 added: Final Foundational Document Reconciliation. Added Section 1.6 (architectural scope boundary) and Section 44.12 (canonical OBJ-001–035 registry); resolved `SOURCE-GAP-ARCH-03`; verified no stale internal section references exist in this document (Rev 1.3).

**Consistency Check:** PASSED (14/14 baseline checks per EAIOC-SPEC-001 §40; 10/10 Rev 1.1 checks per §46.6; 20/20 Rev 1.2 checks per §47.17; 8/8 Rev 1.3 checks per §48.3)

---

> [!IMPORTANT]
> This architecture document is a faithful translation of the authoritative problem statement. No requirement has been silently removed, simplified, reinterpreted, or replaced. Every optimization technique carries its full required documentation: **applicability, prerequisites, expected benefit, optimization overhead, quality risks, fallback, evaluation methodology, observability metrics, and production maturity status.**
>
> **Implementation may not begin until this document and EAIOC-SPEC-001 have been explicitly approved.**

---

## Table of Contents

1. [Foundational Design Principles](#1-foundational-design-principles)
2. [Central Design Principle and Architectural Axiom](#2-central-design-principle-and-architectural-axiom)
3. [Enterprise Objectives (OBJ-001 to OBJ-014)](#3-enterprise-objectives)
4. [Optimization Objective Function](#4-optimization-objective-function)
5. [Product Scope: Four First-Class Execution Environments](#5-product-scope)
6. [Optimization Domains (A through S)](#6-optimization-domains)
7. [Three-Layer Control-Plane Architecture](#7-three-layer-control-plane-architecture)
8. [Full Request Response Pipeline](#8-full-requestresponse-pipeline)
9. [Developer-Agent Model-Bound Pipeline](#9-developer-agent-model-bound-pipeline)
10. [Component Specifications: T0 Series (Pre-Processing)](#10-component-specifications-t0-series)
11. [Component Specifications: T1 Series (Context Optimization)](#11-component-specifications-t1-series)
12. [Component Specifications: T2/Assembler/Output Series](#12-component-specifications-t2-assembler-output-series)
13. [Component Specifications: T3 Series (Post-Inference)](#13-component-specifications-t3-series)
14. [Optimization Intelligence Layer (OI-001 to OI-005)](#14-optimization-intelligence-layer)
15. [Context Lifecycle Intelligence (CL-001 to CL-007)](#15-context-lifecycle-intelligence)
16. [Cache Economics and Cache-Aware Assembly (CE-001 to CE-005)](#16-cache-economics-and-cache-aware-assembly)
17. [Tool Execution Optimization (TE-001 to TE-007)](#17-tool-execution-optimization)
18. [Agent Loop and Multi-Agent Economics (AL-001 to AL-006)](#18-agent-loop-and-multi-agent-economics)
19. [Adaptive Model, Reasoning, and Output Control (AR-001 to AR-004)](#19-adaptive-model-reasoning-and-output-control)
20. [Quality-Constrained Optimization (QO-001 to QO-003)](#20-quality-constrained-optimization)
21. [Optimization Experimentation and Learning (EL-001 to EL-005)](#21-optimization-experimentation-and-learning)
22. [Developer-Agent Optimization Modules (DA-001 to DA-025)](#22-developer-agent-optimization-modules)
23. [Optimization Technique Catalog (Full Documentation)](#23-optimization-technique-catalog)
24. [Default Optimization Order (24-Stage Policy)](#24-default-optimization-order)
25. [Provider / Model Optimization Profiles](#25-provider--model-optimization-profiles)
26. [Inference / Serving Optimization Layer (P5 Optional)](#26-inference--serving-optimization-layer-p5--optional)
27. [Token and Cost Accounting Ledger](#27-token-and-cost-accounting-ledger)
28. [Quality Gates](#28-quality-gates)
29. [Security and Governance Requirements](#29-security-and-governance-requirements)
30. [Failure and Fallback Model](#30-failure-and-fallback-model)
31. [Observability and Governance](#31-observability-and-governance)
32. [Benchmarking Framework](#32-benchmarking-framework)
33. [Enterprise Deployment Model](#33-enterprise-deployment-model)
34. [Rollout Strategy](#34-rollout-strategy)
35. [Regression Testing](#35-regression-testing)
36. [Implementation Priority (P0 to P5)](#36-implementation-priority)
37. [Non-Functional Requirements (NFR-001 to NFR-013)](#37-non-functional-requirements)
38. [Configuration Requirements](#38-configuration-requirements)
39. [Acceptance Criteria (AC-001 to AC-038)](#39-acceptance-criteria)
40. [Success Metrics](#40-success-metrics)
41. [Anti-Patterns to Avoid](#41-anti-patterns-to-avoid)
42. [Enterprise Optimization Maturity Model](#42-enterprise-optimization-maturity-model)
43. [Reference Research and Validation Notes](#43-reference-research-and-validation-notes)
44. [Requirements Traceability Matrix](#44-requirements-traceability-matrix)
45. [Internal Consistency Check Summary](#45-internal-consistency-check-summary)
46. [Dynamic Execution Architecture](#46-dynamic-execution-architecture)
47. [Architecture Hardening Amendment — Operating Model, Governance, and Trust Boundaries (2026-09-15)](#47-architecture-hardening-amendment--operating-model-governance-and-trust-boundaries-2026-09-15)
48. [Final Foundational Document Reconciliation (2026-09-16)](#48-final-foundational-document-reconciliation-2026-09-16)

---

## 1. Foundational Design Principles

### 1.1 Product Identity

The product is an **Enterprise Agent & LLM Inference Optimization Control Plane** (hereafter "the Control Plane" or "the framework").

It is **not** a prompt-compression utility. It is an **adaptive, policy-driven optimization and governance control plane** that sits between an AI application/agent orchestrator and one or more LLM providers, and where supported can operate at the agent/tool and inference-serving layers.

### 1.2 The Problem It Solves

Modern LLM applications send far more information to models than user-visible requests suggest. A single request may contain:

| Context Source | Description |
|---|---|
| System & developer instructions | Stable policy and role definitions |
| User prompt | The actual user request |
| Conversation history | Prior turns accumulated over time |
| RAG chunks & search results | Retrieved documents and excerpts |
| Tool definitions | Available tools and their schemas |
| Tool outputs & API responses | Results from tool invocations |
| Logs, code, test output | Developer-agent artifacts |
| Previous agent plans/responses | Accumulated agentic state |
| Intermediate reasoning/task state | Mid-workflow scratchpad |
| Repeated instructions | Duplicated across turns |
| Duplicated documents or excerpts | Redundant retrieval overlap |

In agentic workflows, this context is transmitted **repeatedly** across multiple model calls, leading to **uncontrolled inference consumption** — not merely "large prompts."

### 1.3 What the Framework Must Answer

For every request, the framework must answer:

| Question |
|---|
| What information is actually required? |
| Which information can be removed? |
| Which information can be compressed? |
| Which information can be reused? |
| Which information should be cached? |
| Which model should process the request? |
| How much reasoning is required? |
| How much context is required? |
| When should an agent stop? |
| How much output is actually required? |
| What is the cheapest safe way to satisfy the task? |
| What quality was preserved after optimization? |

### 1.4 Dual Optimization Axes

| Axis | Description |
|---|---|
| **TOKEN VOLUME** | Reduce tokens that need to be generated or processed |
| **TOKEN ECONOMICS** | Reduce the effective cost of processing tokens through caching, routing, batching, reuse, model selection, and conditional inference |

> [!CAUTION]
> No optimization shall be considered successful solely because it reduces token count. It must demonstrate acceptable quality and measurable net value.

### 1.5 What the Framework Preserves

All of the following must be preserved through every optimization:

- Correctness
- Semantic fidelity
- Retrieval relevance
- Output quality
- Security and tenant isolation
- Auditability
- Predictable behavior
- Reliability
- Model/provider portability

### 1.6 Architectural Scope Boundary: What This Document Owns vs. Downstream Documents

> [!NOTE]
> **Added by the Final Foundational Document Reconciliation of 2026-09-16 (Issue #3).** This document has grown, across the baseline and two hardening passes, to include extensive component specifications, an optimization technique catalog, quality gates, benchmarking, deployment, regression, acceptance criteria, research references, a maturity model, cost/performance models, and dynamic-execution/hardening detail. This is intentional and none of it is removed or relocated by this note — it establishes the boundary going forward so that later, more detailed downstream documents extend rather than duplicate or contradict this one.

**This architecture document (EAIOC-ARCH-001) owns:**

- Architectural principles (Sections 1–2)
- System boundaries and anti-scope (Section 41, Section 47.13)
- Component boundaries and responsibilities (Sections 10–22, Section 46, Section 47)
- Ownership (advisory/enforcement/execution-ownership, Section 47.6) and plane placement (Governance/Safety, Execution Truth, Optimization Intelligence, Execution Adapter — Section 47.1)
- Control/data flows (Sections 7, 8, 9)
- State authority (Section 46's ESM/CVM/WVM state model, Section 47.3's memory-authority and cross-execution extensions)
- Trust boundaries (Section 47.2's Governance/Safety Plane components; Section 47.9's optimization-ownership taxonomy)
- Integration boundaries (Section 5, Section 47.10's feasibility tiers)
- Operating modes (Section 47.5)
- Architectural decisions and open items (Section 45's Open Items; Section 47.18)
- Architectural invariants (Section 47.14)
- High-level quality/security/reliability constraints (Sections 28, 29, 30, 37)
- Traceability from the Problem Statement and Engineering Specification down to this document (Section 44)

**Downstream documents own deeper implementation-level detail**, and extend this document rather than restate it:

| Downstream document | Owns |
|---|---|
| `interfaces.md` | Contracts/interfaces for the components this document defines |
| `conventions.md` | Implementation conventions (naming, module layout, fail-open/closed enforcement mechanics) |
| `edge-cases.md` | The edge-case catalog (EC-001 onward) |
| `scenario-matrix.md` | Scenario-based verification against the requirements this document traces |
| `optimization-catalog.md` | Detailed optimization technique implementation guidance (Section 23's catalog here remains the architectural-level summary) |
| `provider-matrix.md` | Provider/model capability detail (Section 25 here remains the architectural profile-dimension list) |
| `cache-strategy.md` | Cache mechanics beneath Section 16's cache-economics architecture |
| `agent-optimization.md` | Agent optimization detail beneath Section 18's architecture |
| `inference-optimization.md` | Inference-layer detail beneath Section 26/47.8's integration boundary |
| `quality-gates.md` | Quality validation detail beneath Section 28's architectural gate definitions |
| `security.md` | Detailed security controls beneath Section 29 and Section 47.2's Governance/Safety Plane components |
| `observability.md` | Detailed observability beneath Section 31 and Section 47.16 |
| `eval.md` | Evaluation methodology beneath Section 32 |
| `implementation-plan.md` | Implementation sequencing beneath Section 36 |

**The distinguishing test** between an ARCHITECTURAL REQUIREMENT (belongs here) and DETAILED ENGINEERING/IMPLEMENTATION DETAIL (belongs downstream): if a statement defines *what a component is responsible for, who owns a decision, what plane it lives in, what state it depends on, or what invariant it must never violate*, it is architectural. If a statement defines *the exact algorithm, data structure, schema, provider-specific parameter, test case, or step-by-step procedure* used to satisfy that responsibility, it is implementation detail and belongs in the corresponding downstream document once generated. This document retains existing technique-level material (e.g., Section 23's catalog, Section 27's ledger field lists) where it is required for architectural traceability today; it is not deleted or moved by this boundary clarification, and downstream documents are expected to reference rather than duplicate it once they exist.

### 1.7 Hardening Amendment: Ownership, Trust, and Anti-Scope (2026-09-15)

> [!NOTE]
> **Added by the Architecture Hardening Pass of 2026-09-15 (PS §52.2/§52.18/§52.20, H02/H18/H20).** Six activities are frequently conflated under "optimization" and must be treated as separately owned, separately measured categories: prompt/input token optimization, output/reasoning token optimization, context optimization, cache optimization, model/provider routing, and inference-runtime optimization. The Control Plane builds and directly owns the first five; for inference-runtime optimization (Layer 3, Section 26) it owns awareness, integration, routing, and policy only — implementation is infrastructure/provider-owned. See Section 47.9.
>
> The Control Plane's relationship to any decision or side effect is exactly one of ADVISORY, ENFORCEMENT, or EXECUTION-OWNERSHIP (Section 47.6), and it never silently assumes ownership of agent planning, IDE behavior, or an external side effect absent an explicit integration surface. It is not a replacement agent orchestrator, model-training system, model-runtime infrastructure platform, provider, general-purpose IDE, or coding-agent UX — see Section 47.13's anti-scope boundary.

---

## 2. Central Design Principle and Architectural Axiom

> **Central design principle:**
> "Make tokens count instead of merely counting them."

> **Central design & final architectural principle:**
> The optimizer should be allowed to conclude that the best LLM call is no LLM call at all.

**What the second principle means architecturally:**

| Component | How it implements "no LLM call" |
|---|---|
| T1.6 Prompt Cache | Exact cache hit returns a prior result without any model call |
| T1.7 Semantic Cache | Semantic hit returns a reused result without any model call |
| T3.3 Tool Result Cache | Deterministic tool result served from cache, bypassing inference |
| OI-001 Optimization Decision Engine | Explicitly decides whether inference should occur at all |
| OI-002 Cost-of-Optimization Controller | Skips inference when expected net value is negative or insufficient |
| T3.1 Agent Stop Controller | Terminates agent loop before further LLM calls when objective is satisfied |
| AL-006 Agent Task Value / Early Exit | Stops agent when additional calls have low expected value |
| AR-004 Verifier-Guided Escalation | Deterministic verifiers may confirm correctness without re-inference |
| Optimization Order Stages 1-2 | Exact and semantic cache checks execute before any LLM is reached |
| Failure and Fallback Model | Fallbacks may return policy-safe results without LLM calls |

**The ultimate objective:**

> "Deliver the required business outcome with the LOWEST SAFE TOTAL INFERENCE COST and the HIGHEST PRACTICAL QUALITY, while maintaining enterprise security, observability, reliability, auditability, and scalability."

---

## 3. Enterprise Objectives

All 14 objectives are **mandatory**. None may be deferred.

| ID | Objective |
|---|---|
| **OBJ-001** | Reduce unnecessary input tokens |
| **OBJ-002** | Reduce unnecessary output tokens |
| **OBJ-003** | Bound conversation and context growth |
| **OBJ-004** | Reduce redundant context propagation across agentic steps |
| **OBJ-005** | Reduce unnecessary model calls |
| **OBJ-006** | Reduce unnecessary tool calls and tool-output tokens |
| **OBJ-007** | Reuse previously processed prompts, context, tool results, and semantically equivalent responses where safe |
| **OBJ-008** | Route requests to the least expensive model capable of satisfying the quality requirement |
| **OBJ-009** | Dynamically allocate token/reasoning budgets according to task complexity |
| **OBJ-010** | Stop agentic workflows as soon as the objective has been sufficiently satisfied |
| **OBJ-011** | Produce an auditable token and cost ledger for every optimized request |
| **OBJ-012** | Provide organization-specific benchmark evidence before production rollout |
| **OBJ-013** | Support multiple LLM providers and model families through a provider-neutral abstraction layer |
| **OBJ-014** | Preserve security, privacy, authorization, and tenant boundaries while optimizing |

**Added by the 2026-09-10 Dynamic Execution Hardening Pass (PS §51, structurally realized in Section 46; canonically labeled at the source in PS §51.13 as of the 2026-09-16 reconciliation):**

| ID | Objective |
|---|---|
| **OBJ-015** | Maintain versioned, mutable execution state across the full request lifecycle |
| **OBJ-016** | Support checkpointing and resumability for all multi-step workflows (three or more steps) |
| **OBJ-017** | Detect and reconcile context, permission, policy, and model-availability mutations in-flight |
| **OBJ-018** | Distinguish Logical Task Context from Model-Admitted Context and maintain both separately |
| **OBJ-019** | Apply a tiered eviction protocol for context overflow with no silent truncation |
| **OBJ-020** | Handle all six interruption causes (cancellation, timeout, outage, policy change, budget exhaustion, security event) with structured partial results |
| **OBJ-021** | Enforce stale result rejection at all cache types and at execution resume |
| **OBJ-022** | Validate scenario completeness (normal and abnormal) as a first-class production gate |

> [!NOTE]
> **Resolved by the Final Foundational Document Reconciliation of 2026-09-16.** OBJ-015 through OBJ-022 are now labeled at the source in PS §51.13, structurally realized by the Section 46 components (ESM, CVM, WVM, CPM, RE, CIG, CEC, PRV, DPE, CAR, SRP, SPM, RCO), and are **unambiguously mandatory**, on equal footing with every other objective in this document. Former `SOURCE-GAP-ARCH-03` is RESOLVED — see Section 47.18. Section 44.12 provides the consolidated OBJ-001–035 cross-document traceability registry (ID, statement, Problem Statement section, Engineering Specification section, Architecture section, status).

**Added by the 2026-09-15 Architecture Hardening Pass (PS §52, traced individually in Section 47):**

| ID | Objective |
|---|---|
| **OBJ-023** | Support synchronous, asynchronous/precomputed, and hybrid Control Plane operating modes, and treat Control Plane decision latency/overhead as part of the optimization problem |
| **OBJ-024** | Distinguish advisory, enforcement, and execution/side-effect-owning decisions; never assume ownership of agent planning or side effects without explicit integration |
| **OBJ-025** | Bound the Control Plane's own latency, cost, and resource consumption with backpressure, overload protection, and a deterministic safe fallback |
| **OBJ-026** | Compute optimization value as verified net benefit across tokens, inference cost, optimization overhead, retrieval/routing/cache overhead, latency, retries, quality degradation, and downstream/tool cost — never tokens alone |
| **OBJ-027** | Enforce tenant/org/user/application spend budgets and runaway-cost protection without budget logic bypassing security, authorization, or policy |
| **OBJ-028** | Treat verifier output as calibrated evidence with a confidence score and acceptance threshold, never unconditional ground truth |
| **OBJ-029** | Classify, protect, and propagate deletion/retention decisions for sensitive data across every cache, memory, log, and trace surface |
| **OBJ-030** | Declare an explicit integration feasibility tier for every coding-agent integration rather than assuming complete interception |
| **OBJ-031** | Preserve Control Plane execution truth, authorization state, policy state, and context/workflow version as authoritative over agent-owned working memory, with explicit conflict resolution |
| **OBJ-032** | Account for concurrent mutation, staleness, and version conflict across multiple agents/sub-agents/workflows sharing resources, with reconciliation, coordination, and idempotency |
| **OBJ-033** | Require explicit human approval for designated consequential/irreversible actions, selected by policy/risk rather than applied universally |
| **OBJ-034** | Treat prompt injection and malicious/mutated retrieved/tool/context content as a security concern screened before any optimization stage may admit or act on it |
| **OBJ-035** | Maintain anti-scope boundaries distinguishing what the Control Plane builds from what it integrates with |

---

## 4. Optimization Objective Function

```
MINIMIZE:
    Total Safe Inference Cost

SUBJECT TO:
    Quality      >= configured minimum
    Correctness  >= configured minimum
    Security     >= required policy
    Reliability  >= configured SLO
    Freshness    >= required threshold
    Latency      <= configured maximum
    Compliance    = satisfied
    Task completion = satisfied
```

The system shall expose the resulting operating point as a **cost/quality/latency frontier**.

**Final Design Principle (from problem statement section 50):**

```
OBSERVE + MEASURE + UNDERSTAND + DECIDE + REDUCE + REUSE
+ RETRIEVE + COMPRESS + ROUTE + CONSTRAIN + VERIFY + STOP
+ LEARN + GOVERN
```

**Central question for every request:**
> "What is the cheapest safe execution strategy that can achieve the required outcome at the required quality, latency, freshness, security, and reliability?"

> [!NOTE]
> **2026-09-15 Hardening Pass (H04, PS §52.4):** this objective/constraint formulation is a **conceptual and heuristic decision framework** that guides policy design, cost accounting, and evaluation — it is not a specification for a literal real-time mathematical optimizer or constraint solver executed synchronously on every request. Individual decisions (routing, caching, pruning, escalation) are made by policy rules, heuristics, or learned models operating within this framework, at whichever operating mode (Section 47.5) is appropriate for that decision. See Section 47.4 for the full net-optimization-value accounting this framework requires.

---

## 5. Product Scope

### 5.1 Four First-Class Execution Environments

> [!IMPORTANT]
> Developer/coding-agent optimization is a **FIRST-CLASS PRODUCT REQUIREMENT**. It is not an optional use case.

| Environment | Description |
|---|---|
| **A** | Generic LLM Applications |
| **B** | Autonomous / Tool-Using Agents |
| **C** | Developer / Coding Agents (Cursor, Claude Code, GitHub Copilot, Antigravity, Codex, and comparable platforms, subject to available integration surfaces) |
| **D** | Multi-Agent / Sub-Agent Systems |

The product shall use a **common optimization core** with integration adapters, not a separate optimizer for every agent product. It shall optimize **every model-bound context generated by an agent**, not merely the original user prompt.

### 5.2 Integration Modes

| # | Mode |
|---|---|
| 1 | API / LLM Gateway Proxy |
| 2 | SDK / Middleware integration |
| 3 | MCP-based optimization services |
| 4 | Agent-harness integration for controllable/open implementations |
| 5 | Provider/platform enterprise integrations where supported |

> [!IMPORTANT]
> **2026-09-15 Hardening Pass (H08, PS §52.8):** these integration modes do not imply uniform depth of access across coding-agent platforms. Every coding-agent integration must declare an **integration feasibility tier** (deep/native, gateway/interception, plugin/extension, protocol/tool-level, or advisory/observability-only) — see Section 47.10 for the tier model and the constraint that Section 22's DA-001 through DA-025 module applicability is bounded by the declared tier for that platform.

### 5.3 Common Representation Layer

The adapter layer shall normalize different agent/provider interfaces into a common representation:

```
AgentRequest        -- the incoming request from any agent type
AgentContext        -- aggregated context for the agent
InstructionContext  -- system/developer/user instructions
RepositoryContext   -- code repository state and metadata
ToolContext         -- available tools and their schemas
RetrievalContext    -- retrieved documents and chunks
ConversationContext -- conversation history
MemoryContext       -- short-term and long-term memory
AgentState          -- current agent task state
ModelPolicy         -- model selection and routing policy
OptimizationPolicy  -- optimization rules, budgets, thresholds
```

---

## 6. Optimization Domains

All 19 domains are **mandatory**. None may be omitted.

| ID | Domain |
|---|---|
| **A** | Input Optimization |
| **B** | Context Optimization |
| **C** | Retrieval Optimization |
| **D** | Prompt Caching and Reuse |
| **E** | Semantic Caching |
| **F** | Model Routing |
| **G** | Reasoning-Budget Optimization |
| **H** | Agent Workflow Optimization |
| **I** | Tool-Call and Tool-Output Optimization |
| **J** | Output Optimization |
| **K** | Batch/Asynchronous Optimization |
| **L** | Agent / Workflow Optimization |
| **M** | Context Lifecycle Optimization |
| **N** | Optimization Decision Intelligence |
| **O** | Cache Economics and Cache-Aware Assembly |
| **P** | Tool Execution Optimization |
| **Q** | Outcome-Based Cost Optimization |
| **R** | Inference / Serving Optimization (where infrastructure access permits) |
| **S** | Observability, Governance, and Continuous Learning |

---

## 7. Three-Layer Control-Plane Architecture

The logical architecture is organized into **three optimization layers** governed by a shared **Optimization Intelligence Layer**.

```
+------------------------------------------------------------------+
|         OPTIMIZATION INTELLIGENCE LAYER (governs all three)     |
|  OI-001 Decision Engine  OI-002 Cost Controller  OI-003 Depth   |
|  OI-004 Context ROI Scorer   OI-005 Outcome-Based Engine        |
+------------------------------------------------------------------+
         |                      |                      |
         v                      v                      v
+----------------+    +----------------+    +--------------------+
|   LAYER 1      |    |   LAYER 2      |    |   LAYER 3          |
| AGENT/WORKFLOW |    | CONTEXT        |    | INFERENCE/SERVING  |
| OPTIMIZATION   |    | OPTIMIZATION   |    | OPTIMIZATION       |
|                |    |                |    | (Optional, P5)     |
| Task planning  |    | Retrieval      |    | Model routing      |
| Tool selection |    | Ranking        |    | Model cascade      |
| Tool execution |    | Pruning        |    | Reasoning budget   |
| Sub-agent econ |    | Deduplication  |    | Output budget      |
| Agent loops    |    | Compression    |    | Batch processing   |
| Session phases |    | Reordering     |    | KV cache           |
| Memory         |    | Context budget |    | Speculative decode |
| Early exit     |    | Caching        |    | Quantization       |
| Outcome valid. |    | Freshness      |    | Serving/scheduling |
|                |    | Dep. graphs    |    |                    |
|                |    | Code-aware ctx |    |                    |
+----------------+    +----------------+    +--------------------+
```

---

## 8. Full Request-Response Pipeline

```
USER / APPLICATION
        |
        v
+--------------------+
| T0.1 Model Router  |  <- Select least expensive capable model
+--------+-----------+
         |
         v
+--------------------+
| T0.2 Task/         |  <- Determine task complexity before inference
| Complexity Analyzer|
+--------+-----------+
         |
         v
+--------------------+
| T0.3 Reasoning     |  <- Dynamically select reasoning budget
| Budget Controller  |
+--------+-----------+
         |
         v
+--------------------+
| T1.1 Sanitizer     |  <- Normalize raw input; produce token count
+--------+-----------+
         |
         v
+--------------------+
| T1.2 Intent        |  <- Classify intent; select downstream policies
| Classifier         |
+---+----------------+
    |
    +------------------------------+
    v                              v
+----------+               +-------------+
| T1.6     |               | T1.7        |
| Prompt   |               | Semantic    |
| Cache    |               | Cache       |
+----------+               +-------------+
       +----------+-----------------+
                  |  (cache miss -> continue pipeline)
                  v
+--------------------+
| T1.3 Entity        |  <- Extract structured entities
| Extraction         |
+--------+-----------+
         |
         v
+--------------------+
| T1.9 Context       |  <- Remove low-value context
| Pruner             |
+--------+-----------+
         |
         v
+--------------------+
| T1.10 Context      |  <- Remove duplicate / near-duplicate context
| Deduplicator       |
+--------+-----------+
         |
         v
+--------------------+
| Relevance +        |  <- Score Utility = Relevance x Density x Authority / Cost
| Token-Aware Ranker |
+--------+-----------+
         |
         v
+--------------------+
| T1.12 Adaptive     |  <- Dynamic Top-K retrieval based on complexity
| Retrieval / Top-K  |
+--------+-----------+
         |
         v
+--------------------+
| T1.8 Context       |  <- Compress: extractive / model-based / query-aware
| Compressor         |
+--------+-----------+
         |
         v
+--------------------+
| T1.11 Context      |  <- Optimize context ordering; address position bias
| Reorderer          |
+--------+-----------+
         |
         v
+--------------------+
| T1.5 Soft Reset /  |  <- Enforce context budget ceiling; staged compaction
| Context Budgeter   |
+--------+-----------+
         |
         v
+--------------------+
| T2.1 Query         |  <- Compress user request; preserve intent/constraints
| Compressor         |
+--------+-----------+
         |
         v
+--------------------+
| Prompt Assembler   |  <- Assemble final prompt; cache-stable prefixes first
| + Cache-Aware      |
+--------+-----------+
         |
         v
+--------------------+
| Target LLM /       |  <- Execute inference (may be skipped entirely)
| Agent / Cascade    |
+--------+-----------+
         |
         v
+--------------------+
| Output Gate        |  <- Enforce schema + length budget
| + Schema + Length  |
+--------+-----------+
         |
         v
+--------------------+
| T3.1 Agent Stop    |  <- Evaluate: continue or stop the agent loop
| Controller         |
+--------+-----------+
         |
         v
     FINAL RESPONSE

Parallel / side-channel components:
+---------------------+
| T3.2 Tool Output    |  <- Filter, compress, enforce schema on tool results
| Filter / Compressor |
+---------------------+

+---------------------+
| T3.3 Tool Result    |  <- Cache deterministic / stable tool results
| Cache               |
+---------------------+

+--------------------------------------+
| OBSERVABILITY / TOKEN LEDGER         |
| Cost  Tokens  Latency  Quality       |
| Cache  Routing  Tools  Errors        |
+--------------------------------------+
```

> [!NOTE]
> **2026-09-15 Hardening Pass (H01, PS §52.1):** this diagram is the logical decision sequence for a request requiring the full pipeline. It is not a requirement that every stage execute synchronously in the critical request path for every request. Section 47.5 specifies which stages support synchronous, asynchronous/precomputed, or hybrid evaluation, and treats Control Plane decision latency/overhead itself as part of the optimization problem.

---

## 9. Developer-Agent Model-Bound Pipeline

The following pipeline executes for **every model-bound iteration**, not only the first user request.

```
Developer Request
 -> Agent Context Construction
 -> Context Adapter/Interception
 -> Task + Complexity Analysis         (T0.2 + DA-013)
 -> Repository Map / Memory Lookup     (DA-003 + DA-010)
 -> Dynamic Tool + MCP Selection       (DA-007 + DA-008)
 -> Error-Driven / Symbol-Aware Retrieval (DA-012 + DA-002)
 -> Search Result Ranking              (DA-017)
 -> Code-Aware Context Pruning         (DA-001)
 -> Deduplication                      (DA-011 + T1.10)
 -> Terminal/Test/Linter Output Optim. (DA-005 + DA-006)
 -> Sub-Agent Result Compression       (DA-009)
 -> Context Ranking                    (Token-Aware Ranker)
 -> Context Budget Allocation          (DA-013 + T1.5)
 -> Context Compression                (T1.8)
 -> Context Reordering                 (DA-015 + T1.11)
 -> Cache-Aware Prompt Assembly        (DA-022 + Prompt Assembler)
 -> Model Routing                      (T0.1)
 -> Reasoning Budget Selection         (T0.3)
 -> LLM                                (Target LLM / Cascade)
 -> Output Validation                  (Output Gate)
 -> Tool / File / Patch Action
 -> Tool-Result Optimization           (T3.2 + TE-006)
 -> Agent Loop Controller              (T3.1 + DA-014 + AL-001 to AL-006)
 -> Repeat ONLY when additional work has sufficient expected value
 -> Final Result
```

**Developer-Agent Success Metrics (all mandatory):**

| Metric Category | Metrics |
|---|---|
| Token measurement | Tokens per coding task / iteration / tool call / test-debug cycle; repository / code / terminal / MCP / sub-agent / memory / history / diff tokens |
| Efficiency | Model and tool calls per completed task; agent iterations; files inspected; relevant-vs-irrelevant context ratio |
| Cache & Control | Cache hit rate; early-exit rate |
| Quality | Task completion; patch correctness; test pass rate; regression rate; developer acceptance |
| Economics | Cost per successfully completed coding task |

> [!CAUTION]
> A coding-agent optimization is successful **only when** it reduces net inference cost and/or latency without materially degrading patch correctness, test success, task completion, security, or developer experience.

---

## 10. Component Specifications: T0 Series

### 10.1 T0.1 — Model Router

**Purpose:** Select the least expensive model that can satisfy the task's required quality, capability, latency, and compliance constraints.

**Inputs:** AgentRequest, TaskComplexityResult, ModelPolicy, ProviderProfiles
**Outputs:** Selected model, routing decision record, candidate model list

**Selection signals:**

| Signal | Weight |
|---|---|
| Task intent and complexity | High |
| Historical quality for this task type | High |
| Model price (input + output + cache rates) | High |
| Latency requirements | High |
| Context-window requirements | High |
| Tool/function support | High |
| Provider availability | Medium |

**Routing modes:**
- Deterministic rules (required for P0/P1)
- Learned routing (optional advanced capability, P3+)
- Automatic escalation when confidence/quality is insufficient

> [!CAUTION]
> The router must NOT optimize cost by routing safety-critical or complex requests to incapable models.

**Research basis:** RouteLLM (arXiv:2406.18665) — demonstrates model routing as a cost/quality trade-off in evaluated settings. Not a production guarantee.

---

### 10.2 T0.2 — Task / Complexity Analyzer

**Purpose:** Determine an approximate task complexity before expensive inference.

**Signals analyzed:**

| Signal |
|---|
| Intent (from T1.2) |
| Input size (tokens) |
| Number of entities |
| Number of required operations |
| Number of tools required |
| Retrieval depth required |
| Historical task complexity for similar tasks |
| Expected output structure |
| Need for reasoning |
| Confidence requirements |

**Outputs:**

| Output | Description |
|---|---|
| Complexity class | LOW / MEDIUM / HIGH |
| Recommended model tier | Maps to provider/model selection |
| Recommended context budget | Token ceiling for context assembly |
| Recommended reasoning budget | LOW / MEDIUM / HIGH reasoning depth |
| Recommended output budget | Maximum output tokens |

---

### 10.3 T0.3 — Reasoning Budget Controller

**Purpose:** Dynamically select a reasoning budget based on task complexity.

**Budget mapping:**

| Complexity | Reasoning Budget |
|---|---|
| Simple extraction | Low |
| Moderate analysis | Medium |
| Complex multi-step reasoning | High |

**Requirements:**
- Support model/provider-specific reasoning controls where available
- **Never reduce reasoning solely to meet a token target**
- Compare quality against baseline for any reasoning reduction
- Support configurable minimum quality thresholds
- Record allocated and consumed reasoning tokens where the provider exposes them
- Support fallback escalation when the initial reasoning budget is insufficient

---

## 11. Component Specifications: T1 Series

### 11.1 T1.1 — Sanitizer

**Purpose:** Normalize raw input before downstream processing.

**Capabilities:**
- Normalize whitespace and formatting
- Remove irrelevant formatting
- Remove duplicated content within the input
- Strip known UI noise (timestamps, progress bars, decorative separators)
- Normalize equivalent representations (Unicode normalization)
- **Preserve:** code, structured data, identifiers, URLs, and user-intent information

**Outputs:**
- Sanitized input
- Baseline token count (pre-sanitization)
- Post-sanitization token count

> [!CAUTION]
> The sanitizer must be content-aware and must **not** blindly remove syntax that is meaningful to the task.

---

### 11.2 T1.2 — Intent Classifier

**Purpose:** Classify request intent to drive downstream policy selection.

**Minimum supported intents:**

| Intent |
|---|
| Factual |
| Comparative |
| Procedural |
| Summarization |
| Extraction |
| Classification |
| Coding |
| Debugging |
| Retrieval |
| Open-ended |
| Agentic workflow |

**Downstream policy selections driven by this component:**

| Policy | Description |
|---|---|
| Prompt template | Intent-specific instruction set |
| Retrieval strategy | Search type, depth, and ranking |
| Context budget | Token ceiling for this intent |
| Output schema | Structure appropriate for this intent |
| Model tier | Minimum capability requirement |
| Reasoning budget | Depth of reasoning required |
| Compression strategy | Aggressiveness and type |

---

### 11.3 T1.3 — Entity Extraction

**Purpose:** Extract structured information before downstream retrieval/model execution.

**Examples of extracted entity types:**
- Named entities (persons, organizations, locations)
- Numeric values
- Dates and date ranges
- Product identifiers
- Customer identifiers
- Categories
- Geographic constraints
- Filters

**Entity metadata availability:** Retrieval, tool calls, ranking, prompt assembly, validation.

---

### 11.4 T1.6 — Prompt Cache

**Purpose:** Avoid repeatedly processing identical cacheable prompt prefixes.

**Cache candidates:**
- System instructions
- Developer instructions
- Tool definitions
- Few-shot examples
- Stable policy content
- Stable documentation
- Stable conversation prefixes

**Requirements:**

| Requirement |
|---|
| Exact-match cache semantics by default |
| Configurable TTL |
| Tenant/workspace isolation |
| Permission-aware cache keys |
| Model/version-aware cache keys |
| Provider-aware cache abstraction |
| Cache hit/miss measurement |
| Cache write/read accounting |
| Cache invalidation |
| No cross-tenant leakage |
| No caching of content prohibited by policy |

The **Prompt Assembler** shall intentionally construct cache-stable prefixes.

> [!NOTE]
> Provider cache semantics differ. The implementation must remain provider-neutral.

---

### 11.5 T1.7 — Semantic Cache

**Purpose:** Reuse results for semantically equivalent or sufficiently similar requests.

**Requirements:**

| Requirement |
|---|
| Semantic similarity detection |
| Configurable similarity threshold |
| TTL |
| Data freshness validation |
| Tenant/user authorization checks |
| Source/version checks |
| Model/version compatibility checks |
| Tool-state compatibility checks |
| Explicit invalidation for mutable data |
| Ability to bypass cache for time-sensitive queries |

**Cache result classifications:**

| Classification | Meaning |
|---|---|
| Exact cache hit | Identical request found |
| Semantic cache hit | Semantically equivalent request found within threshold |
| Unsafe/invalid cache candidate | Similar but authorization/freshness checks fail |
| Cache miss | No reusable result found |

> [!CAUTION]
> Semantic caching must **never** return a stale or unauthorized result merely because the query is similar.

---

### 11.6 T1.9 — Context Pruner

**Purpose:** Remove low-value context before expensive compression or inference.

**Techniques:**
- Sentence-level relevance scoring
- Paragraph-level relevance scoring
- Embedding similarity to query
- Lexical similarity to query
- Entity overlap with extracted entities
- Metadata filtering (source, date, authority)
- Recency scoring
- Source authority scoring
- Task-specific rule-based filtering

The pruning stage shall be **primarily deterministic or lightweight**.

**Measurements required:**
- Tokens before pruning
- Tokens after pruning
- Information/quality impact (via quality gate)
- False-pruning rate (tracked in benchmarking)

---

### 11.7 T1.10 — Context Deduplicator

**Purpose:** Detect and remove duplicate or near-duplicate information.

**Scope:**
- Conversation history
- RAG chunks
- Tool results
- Uploaded documents
- Search results
- Previous agent outputs
- Code and logs

**Supported detection levels:**
- Exact duplicate detection (hash-based)
- Near-duplicate detection (similarity threshold)
- Paragraph-level duplicate detection
- Code duplicate detection (AST-aware or hash-based)
- Repeated tool-output detection

The system shall preserve one canonical copy and record removed tokens.

---

### 11.8 Token-Aware Ranker

**Purpose:** Rank context using more than relevance alone.

**Ranking signals:**

| Signal | Role |
|---|---|
| Relevance | Primary signal |
| Recency | Prefer newer information |
| Information density | Prefer information-dense content |
| Token cost | Penalize high-cost context |
| Source authority | Prefer authoritative sources |
| Diversity | Prefer unique coverage |
| Duplication | Penalize redundant content |
| Task importance | Boost task-critical content |

**Conceptual utility function:**

```
Utility =
    Relevance
    x Information Density
    x Authority
    x Diversity
    / Token Cost
```

> [!NOTE]
> The exact formula shall be **benchmarked** rather than assumed to be universally optimal.

---

### 11.9 T1.12 — Adaptive Retrieval / Top-K

**Purpose:** Dynamic K selection rather than a fixed chunk count.

**Dynamic K signals:**
- Query complexity
- Relevance scores
- Confidence
- Token budget
- Information coverage
- Redundancy

**Example mapping:**

| Query type | K |
|---|---|
| Simple | 2 |
| Normal | 4 |
| Complex | 8 |

The framework must compare adaptive retrieval against fixed-K baselines.

> [!CAUTION]
> More retrieved context must **not** automatically be considered better.

---

### 11.10 T1.8 — Context Compressor

**Purpose:** Compress retrieved/contextual material while preserving task-relevant information.

**Supported compression modes:**
- Extractive compression (extract key sentences/spans)
- Model-based compression (use a smaller LM to summarize)
- Query-aware compression (compress relative to query)
- Coarse-to-fine compression (rough pass then fine pass)
- Dynamic compression ratios (adjust per budget)
- Entity preservation (ensure critical entities survive)
- Subsequence/sentence recovery (LongLLMLingua-style recovery)
- Code-aware preservation (preserve function signatures, imports, contracts)

Selection among strategies is based on: context size, task type, quality risk, and cost.

**Research basis:**
- LLMLingua (ACL 2023, EMNLP): coarse-to-fine prompt compression with budget control
- LongLLMLingua (ACL 2024): question-aware compression, document reordering, dynamic ratios, subsequence recovery
- LLMLingua-2 (Findings ACL 2024): token classification formulation, faster compression

> [!IMPORTANT]
> Reported research numbers are **benchmark results**, not guaranteed production targets.

---

### 11.11 T1.11 — Context Reorderer

**Purpose:** After selecting context, optimize its ordering.

**Requirements:**
- Place highest-value information in strategically useful positions
- Keep user query and task instructions clearly identifiable
- Preserve source boundaries
- Avoid accidental instruction precedence changes
- Support model-specific ordering policies
- Measure quality before and after reordering
- Specifically evaluate the effect of **"lost in the middle" / position bias**

---

### 11.12 T1.5 — Soft Reset / Context Budgeter

**Purpose:** Maintain a hard or configurable context ceiling.

**Default reference budget:** 500K tokens (from source whitepaper — validated, not guaranteed).

**Budget configurability dimensions:**

| Dimension |
|---|
| Model |
| Provider |
| Workflow |
| Tenant |
| User |
| Intent |
| Task complexity |

**When context exceeds budget (in order):**

1. Rank context (apply Token-Aware Ranker)
2. Remove low-value context (apply Context Pruner)
3. Deduplicate
4. Compress remaining context if required
5. Summarize older history where appropriate
6. Preserve key facts and decisions
7. Record a reset report

> [!CAUTION]
> The system must not assume that a larger context window is always beneficial.

---

## 12. Component Specifications: T2, Assembler, Output Series

### 12.1 T2.1 — Query Compressor

**Purpose:** Compress the user request while preserving functional equivalence.

**Remove:**
- Repetition
- Filler
- Unnecessary politeness
- Redundant explanations
- Duplicate instructions

**Preserve (never remove):**
- Intent
- Constraints
- Entities
- Numbers
- Dates
- Exceptions
- Required output format
- Safety/security requirements

The original request must remain available for audit, evaluation, and fallback.

**Validation target:** 30-60% query reduction (source whitepaper figure — treated as validation target, not guarantee).

---

### 12.2 Prompt Assembler

**Purpose:** Build a final prompt that contains only the information needed for the current task.

**Responsibilities:**
- Select intent-specific instructions
- Include only approved context
- Preserve required security/policy instructions
- Maintain cache-stable prefixes
- Add dynamic content after stable prefixes where possible
- Produce final token counts
- Produce a complete assembly ledger

**Explicit structural separation:**

| Section | Contents |
|---|---|
| **CACHEABLE PREFIX** | System instructions, stable developer policy, tool definitions, stable examples, stable reference material |
| **SEMI-STABLE REGION** | Project documentation, memory, slowly changing context |
| **VOLATILE / DYNAMIC SUFFIX** | Current user request, current retrieved context, current tool results, current task state |

---

### 12.3 Output Schema Selector

**Purpose:** Select output structure based on intent.

| Intent | Output Structure |
|---|---|
| Factual | answer, confidence, source |
| Comparative | comparison table, summary |
| Procedural | ordered steps, bounded explanation |
| Extraction | strict structured schema |
| Coding | required code artifact, bounded explanation |
| Agent/tool | machine-readable action/result structure |

---

### 12.4 Output Length Controller

**Purpose:** Apply field-level token budgets.

**Examples:**
- Maximum explanation length
- Maximum summary length
- Maximum number of steps
- Maximum number of returned records
- Maximum justification length

**Controls where supported:**
- max output tokens
- Structured output
- JSON schema enforcement
- Function calling
- Grammar constraints
- Stop sequences

**Truncation handling:**
- Prefer semantic boundaries
- Mark truncation explicitly
- Preserve machine-readable validity where possible
- Permit controlled expansion if quality gate requires it

---

## 13. Component Specifications: T3 Series

### 13.1 T3.1 — Agent Stop Controller

**Purpose:** Prevent unnecessary continuation of agentic workflows.

**After each meaningful step, evaluate:**
- Has the objective been satisfied?
- Is the result valid?
- Does the result meet quality thresholds?
- Are remaining steps necessary?
- Would another tool/model call materially improve the result?

**If sufficient: STOP.**

**Recording requirements:**
- Planned steps
- Executed steps
- Skipped steps
- Early-termination reason
- Tokens saved
- Tool calls avoided
- Model calls avoided

---

### 13.2 T3.2 — Tool Output Filter / Compressor

**Purpose:** Tool outputs must not automatically be passed in full to the next LLM.

**Capabilities:**
- Select required fields
- Remove metadata not required by the task
- Remove debug information unless requested
- Remove duplicate content
- Compress large textual results
- Enforce schemas
- Apply token budgets

**Example:**

| Stage | Tokens |
|---|---|
| Raw tool output | 10,000 |
| Required agent context | 1,200 |

The system passes the 1,200-token representation while retaining the original for audit where policy permits.

---

### 13.3 T3.3 — Tool Result Cache

**Purpose:** Cache deterministic or sufficiently stable tool results.

**Candidate tools:**
- Search
- Documentation retrieval
- Database queries
- Repository metadata
- File metadata
- External APIs
- Repeated calculations

**Requirements:**

| Requirement |
|---|
| Tool-specific cache policy |
| TTL |
| Parameter normalization |
| Authorization-aware cache keys |
| Freshness policy |
| Explicit invalidation |
| Cache hit/miss accounting |

---

### 13.4 Model Cascade / Escalation

**Purpose:** Optional cascade execution for cost/quality optimization.

**Flow:**
1. Start with a lower-cost model
2. Evaluate confidence/quality
3. If acceptable, return result
4. Otherwise, escalate to a stronger model

**Escalation triggers:**
- Low confidence
- Schema failure
- Validation failure
- Retrieval uncertainty
- User-defined quality requirements
- Safety/policy requirements
- Complexity threshold

**Research basis:** FrugalGPT (arXiv:2305.05176) — explores cascades and cost-aware combinations of LLMs. Benchmark evidence, not production guarantee.

---

### 13.5 Batch / Asynchronous Optimizer

**Separation:**

| Type | Priority |
|---|---|
| Interactive | Latency |
| Asynchronous | Cost efficiency, batch compatible requests, schedule offline workloads, bulk extraction/classification/summarization |

**The framework shall distinguish:**
- Token reduction
- Price-per-token reduction
- Latency reduction
- Throughput improvement

> [!CAUTION]
> Batching must not be used where freshness or interactive latency makes it inappropriate.

---

## 14. Optimization Intelligence Layer

A central **Optimization Intelligence Layer** governs all three architectural layers.

### 14.1 OI-001 — Optimization Decision Engine

**Purpose:** For every request, decide which optimization stages should execute.

**Required decisions:**

| Decision |
|---|
| Should the request be served from an exact cache? |
| Is semantic reuse safe? |
| Should retrieval occur? |
| How much context is needed? |
| Which context should be retained? |
| Should compression run? |
| What compression depth is justified? |
| Which tools should be exposed? |
| Should a tool call happen at all? |
| Should a sub-agent be created? |
| Which model should execute? |
| How much reasoning should be allocated? |
| What output budget is required? |
| Should the workflow continue? |
| Should the request escalate or terminate? |

The engine shall be **policy-driven, measurable, explainable**, and capable of reordering optimization stages when evidence shows that another order gives better net value.

---

### 14.2 OI-002 — Cost-of-Optimization Controller

**Purpose:** Account for the optimizer's own computational and financial overhead.

**For every optimization decision, estimate:**

```
Expected Net Value =
    Expected Inference Cost Avoided
  - Optimization Compute Cost
  - Additional Provider Cost
  - Additional Tool Cost
  - Additional Latency Cost
  - Expected Quality/Risk Cost
```

An optimization shall be **skipped** when its expected benefit is not sufficient to justify its overhead.

> [!CAUTION]
> This prevents the optimizer from spending more money than it saves.

---

### 14.3 OI-003 — Adaptive Optimization Depth Controller

**Purpose:** Select optimization depth based on task complexity, context size, expected value, latency requirements, and historical workload behavior.

| Complexity | Optimization Depth |
|---|---|
| **LOW** | Exact cache, deterministic hygiene, basic routing |
| **MEDIUM** | Retrieval optimization, pruning, deduplication, compression, routing |
| **HIGH** | Dependency-aware retrieval, context graph analysis, advanced compression, multi-agent economics, deeper quality validation, iterative optimization |

> [!IMPORTANT]
> The system shall **not** perform expensive optimization on requests where simple optimization is sufficient.

---

### 14.4 OI-004 — Context Utility / ROI Scorer

**Purpose:** Assign a utility score to each context item.

**Candidate signals:**

| Signal |
|---|
| Task relevance |
| Information density |
| Source authority |
| Recency |
| Freshness |
| Dependency importance |
| Diversity |
| Historical usefulness |
| Confidence |
| Token cost |

**Conceptual score:**

```
Context ROI = Context Utility / Context Cost
```

> [!NOTE]
> The exact formula must remain configurable and benchmark-driven.

---

### 14.5 OI-005 — Outcome-Based Optimization Engine

**Purpose:** Optimize for successful outcomes rather than token reduction alone.

**Required metrics:**
- Cost per successful answer
- Cost per successfully completed workflow
- Cost per successfully resolved task
- Cost per successfully completed coding task
- Tokens per successful outcome
- Model/tool calls per successful outcome

> [!CAUTION]
> A token reduction that lowers task success must **not** be considered a win.

---

## 15. Context Lifecycle Intelligence

### 15.1 CL-001 — Context Dependency Graph

**Purpose:** Represent context as a dependency graph rather than a flat collection.

**For developer agents, the graph may include:**
- User request
- Files, symbols, functions, classes
- Imports, callers/callees
- Tests, git changes, errors
- Tools, agent decisions

**Graph capabilities:**
- Targeted retrieval
- Dependency-aware pruning
- Change-impact analysis
- Cache invalidation
- Freshness evaluation
- Recovery of previously removed context

---

### 15.2 CL-002 — Context Freshness Scoring

**Per context item, carry where available:**

| Field |
|---|
| Source |
| Version |
| Commit |
| Branch |
| Timestamp |
| Last verification time |
| Freshness status |

Freshness shall influence: ranking, caching, retrieval, and validation.

---

### 15.3 CL-003 — Dependency-Aware Cache Invalidation

When a source item changes, invalidate or revalidate dependent cached representations.

| Trigger | Action |
|---|---|
| File changed | Invalidate symbol summaries |
| Symbol changed | Invalidate dependent context |
| Branch changed | Invalidate branch-specific cache |
| Permission changed | Invalidate affected cache entries |
| External data changed | Invalidate freshness-sensitive semantic cache |

---

### 15.4 CL-004 — Reversible Optimization

**Where practical, destructive context reduction must remain recoverable.**

Every transformed item should record:
- Original source
- Transformation type
- Removed range/content reference
- Reason
- Confidence
- Recovery pointer
- Expiration/freshness metadata

If later evidence indicates that removed information is required, the optimizer shall be able to retrieve it without reconstructing the original request manually.

---

### 15.5 CL-005 — Progressive Context Compaction

Compaction shall be **proactive and multi-stage**.

| Budget Usage | Policy |
|---|---|
| 0-40% | Full fidelity |
| 40-60% | Deduplicate |
| 60-70% | Remove low-value content |
| 70-80% | Compress historical/secondary context |
| 80-90% | Aggressive selective loading |
| 90%+ | Controlled session reset or phase transition |

> [!IMPORTANT]
> Thresholds must be configurable and model-specific.

---

### 15.6 CL-006 — Hierarchical Memory

**Memory layers (each independent):**

| Layer |
|---|
| Current turn |
| Current task |
| Session |
| Project/repository |
| Organization |
| Historical knowledge |

**Each layer shall have independent:**
- Retention policy
- Freshness policy
- Compression policy
- Retrieval policy
- Token budget
- Invalidation policy

---

### 15.7 CL-007 — Session Phase Management

Long-running tasks shall support explicit phases:

| Phase |
|---|
| Discovery |
| Planning |
| Implementation |
| Testing |
| Review |
| Completion |

Each phase may use different context, tool, model, reasoning, and memory policies.

---

## 16. Cache Economics and Cache-Aware Assembly

### 16.1 CE-001 — Cache Economics Engine

**Purpose:** Make economically informed caching decisions.

**Expected Cache Benefit:**
```
Future Reuse Probability
x Tokens Avoided
x Effective Input Price
```

**Cache Cost:**
```
Write Cost + Storage Cost + Invalidation Cost + Freshness Risk + Cache Miss/Break Risk
```

**Cache strategy choices:**
- No cache
- Short-lived cache
- Long-lived cache
- Exact cache
- Semantic cache
- Provider-native cache
- Application-side cache

Selection based on expected net value.

> [!NOTE]
> Provider-specific cache economics differ (write/read prices, TTLs, semantics). The control plane must model provider-specific cache economics rather than assuming one universal rule.

---

### 16.2 CE-002 — Cache-Aware Context Construction

**Preferred prompt structure:**

| Region | Contents |
|---|---|
| **STABLE / CACHEABLE PREFIX** | System instructions, stable developer policies, stable tool schemas, stable project rules, stable examples, stable repository metadata |
| **SEMI-STABLE REGION** | Project documentation, memory, slowly changing context |
| **VOLATILE REGION** | Current user request, current files, current errors, current tool results, current task state |

The optimizer shall minimize unnecessary changes to cacheable prefixes.

---

### 16.3 CE-003 — Cache Fragmentation Detector

**Detect likely cache misses caused by:**
- Dynamic timestamps
- Random identifiers
- Tool ordering changes
- Serialization changes
- Whitespace/format changes
- Dynamic metadata
- Changing system instructions
- Changing tool definitions
- Unstable context ordering

The system shall produce a cache-miss diagnosis and estimate the potential recoverable cache value.

---

### 16.4 CE-004 — Cache Pre-Warming Policy

Where the provider supports it, allow pre-warming of high-value stable prefixes when the expected reduction in first-request latency/cost justifies the pre-warm cost.

---

### 16.5 CE-005 — Cache Breakpoint Optimizer

Where provider semantics permit multiple cache boundaries, choose cache breakpoints based on context stability and expected reuse frequency.

---

## 17. Tool Execution Optimization

### 17.1 TE-001 — Tool Call ROI Predictor

**Before executing a tool, estimate:**

```
Expected Information Gain
/
Expected Tool Cost + Token Cost + Latency Cost
```

Skip or defer tool calls whose expected value is insufficient.

---

### 17.2 TE-002 — Tool Argument Optimizer

**Capabilities:**
- Narrow search scope
- Add relevant filters
- Restrict paths
- Restrict result count
- Request only required fields
- Normalize parameters
- Eliminate redundant arguments

Goal: reduce unnecessary output before it enters the LLM context.

---

### 17.3 TE-003 — Dynamic Tool Loading

When supported, do not expose large tool catalogs upfront.

```
Task -> Tool index/search -> Relevant tool discovery -> Minimal tool schemas -> Execution
```

Especially important for large MCP/tool ecosystems.

---

### 17.4 TE-004 — Programmatic Tool Execution

Where supported, collapse multiple independent or sequential tool calls into a programmatic execution unit so intermediate results do not all need to enter the model conversation history.

The framework shall benchmark:
- Traditional tool round trips vs.
- Programmatic/aggregated execution

---

### 17.5 TE-005 — Parallel Tool Execution

Identify independent tool calls and execute them concurrently where:
- Dependencies permit
- Side effects are safe
- Provider/agent policies permit

---

### 17.6 TE-006 — Tool Result Value Filter

**Retain only:**
- Required fields
- Relevant records
- Relevant errors
- Evidence
- Provenance
- Required authorization/audit fields

---

### 17.7 TE-007 — Tool Result Recovery

Large filtered results shall remain recoverable where policy permits.

---

## 18. Agent Loop and Multi-Agent Economics

### 18.1 AL-001 — Agent Loop Progress Meter

**Measure for each agent iteration:**
- Input tokens
- Output tokens
- Tool cost
- New information gained
- State change
- Objective progress
- Errors introduced/resolved

---

### 18.2 AL-002 — Loop Waste Detector

**Detect:**
- Repeated searches
- Repeated tool calls
- Repeated reasoning over unchanged state
- Oscillation between states
- No-progress iterations
- Dead-end loops

> [!IMPORTANT]
> **2026-09-15 Hardening Pass (H10, PS §52.10):** repeated iteration is not inherently waste. The same surface pattern (repeated tool calls, repeated reasoning over similar state) may represent productive incremental progress, legitimate reflection/self-correction, a justified retry, or genuine oscillation/failure. The detector classifies against AL-001's per-iteration signals (state change, objective progress, information gain, errors introduced/resolved) and expected task value, never against iteration count or pattern-match alone. The same expected-value framework governs both the CONTINUE decision (AL-001/AL-002) and the STOP decision (Section 13.1 T3.1, AL-006) — see Section 47.7.

---

### 18.3 AL-003 — Sub-Agent Value Predictor

**Before spawning a sub-agent, estimate:**

```
Expected Sub-Agent Value / Sub-Agent Cost
```

**Cost components:**
- Agent inference cost
- Context duplication cost
- Aggregation cost
- Parent ingestion cost
- Latency
- Coordination overhead

> [!CAUTION]
> Do not spawn a sub-agent when the expected value is insufficient.

---

### 18.4 AL-004 — Sub-Agent Scheduler

**For independent tasks:**
- Identify parallelizable work
- Execute concurrently where safe
- Aggregate results compactly
- Avoid duplicated repository exploration

---

### 18.5 AL-005 — Sub-Agent Handoff Compression

**A sub-agent handoff shall preserve:**
- Objective
- Findings
- Evidence
- Files/symbols
- Confidence
- Decisions
- Unresolved questions
- Recommended next actions

It shall **not** require replaying the full sub-agent transcript.

---

### 18.6 AL-006 — Agent Task Value / Early Exit

**Stop an agent when:**
- Objective is satisfied
- Required validation passes
- Additional information has low expected value
- Additional model/tool calls are unlikely to change the outcome

Record the avoided work.

---

## 19. Adaptive Model, Reasoning, and Output Control

### 19.1 AR-001 — Marginal-Gain Model Routing

```
Marginal Model Value = Expected Quality Gain / Additional Cost
```

Prefer escalation only when the expected marginal value is positive and quality constraints justify it.

---

### 19.2 AR-002 — Reasoning Budget Prediction

**Predict required reasoning depth from:**
- Task complexity
- Historical task outcomes
- Tool requirements
- Verification requirements
- Model behavior
- User-defined quality tier

Start with an appropriate budget and escalate when validation indicates insufficient reasoning.

---

### 19.3 AR-003 — Output Budget Forecasting

Predict expected output length before generation and set the smallest safe output budget.

**Policy per output type:**

| Output type | Policy |
|---|---|
| Extraction | Minimal schema |
| Classification | Categorical |
| Coding | Code + bounded explanation |
| Summarization | Bounded length |
| Analysis | Structured, bounded |
| Tool/action responses | Machine-readable, minimal |

---

### 19.4 AR-004 — Verifier-Guided Escalation

Where a deterministic or inexpensive verifier exists:
1. Run a lower-cost inference
2. Verify output
3. Escalate/retry only failures

**Candidate verifiers:**
- Unit tests
- Schema validation
- Type checking
- Static analysis
- Business rules
- Citation checks
- Format validation

This shall be benchmarked against always using the stronger model.

---

## 20. Quality-Constrained Optimization

### 20.1 QO-001 — Compression Contracts

Every aggressive transformation may define invariants that must survive.

| Context type | Invariants |
|---|---|
| **Coding** | Function signatures, imports, types, API contracts, security constraints, relevant test expectations, error locations |
| **RAG** | Entities, numbers, dates, citations, source attribution |
| **Agent** | Objective, constraints, decisions, completed actions, pending actions, unresolved failures |

---

### 20.2 QO-002 — Quality-Aware Fallback

If a transformed request fails validation:
- Restore the previous representation
- Increase context budget
- Increase retrieval depth
- Increase reasoning budget
- Escalate model
- Disable the offending optimization

---

### 20.3 QO-003 — Delayed-Relevance Protection

The framework shall recognize that information appearing irrelevant at one step may become relevant later in an agent workflow.

**Use:**
- Recovery pointers (CL-004)
- Dependency graph (CL-001)
- Freshness metadata (CL-002)
- Source references
- Re-fetch mechanisms

---

## 21. Optimization Experimentation and Learning

### 21.1 EL-001 — Shadow Optimization

Run optimization decisions in shadow mode without modifying production requests.

**Compare:**
- Baseline tokens vs. simulated optimized tokens
- Predicted cost
- Predicted quality
- Cacheability
- Routing decisions
- Expected savings

---

### 21.2 EL-002 — Controlled Rollout

Support progressive activation: Shadow -> 1% -> 5% -> 25% -> 50% -> 100%

With automatic rollback on quality/cost regressions.

---

### 21.3 EL-003 — Optimization A/B Testing

Every optimization should be independently testable against a control group.

---

### 21.4 EL-004 — Continuous Policy Learning

**Learn from production outcomes:**
- Which optimization pays off
- Which workload benefits
- Which model is sufficient
- Which compression ratio is safe
- Which tools are actually needed
- Which agent loops tend to fail

> [!CAUTION]
> Learning must be **bounded by governance** and may not silently change production policy without configured approval.

---

### 21.5 EL-005 — Optimization Regression Detector

Detect when a previously successful optimization stops producing positive net value after:
- Model changes
- Provider pricing changes
- Prompt changes
- Tool changes
- Repository changes
- Traffic-pattern changes
- Cache behavior changes

---

## 22. Developer-Agent Optimization Modules

All 25 DA modules are **mandatory first-class requirements**.

| Module | Name | Description |
|---|---|---|
| **DA-001** | CODE-AWARE CONTEXT PRUNER | Remove low-value code context while preserving task-critical dependencies |
| **DA-002** | SYMBOL-LEVEL CONTEXT SELECTOR | Select relevant functions, methods, classes, interfaces, types, imports, callers, callees, references, and surrounding code instead of whole files where possible |
| **DA-003** | REPOSITORY MAP GENERATOR AND CACHE | Maintain a compact repository structure and reuse it instead of repeatedly rediscovering the codebase |
| **DA-004** | GIT-DIFF OPTIMIZER | Summarize large diffs and expose detailed changed sections selectively |
| **DA-005** | TERMINAL OUTPUT OPTIMIZER | Filter, summarize, structure, and deduplicate large terminal outputs |
| **DA-006** | COMPILER / TEST / LINTER ERROR EXTRACTOR | Extract actionable failures, locations, stack traces, expected/actual values, and relevant context from noisy output |
| **DA-007** | DYNAMIC MCP / TOOL SELECTOR | Expose only MCP tools/resources/capabilities relevant to the current task where supported |
| **DA-008** | TOOL SCHEMA OPTIMIZER | Minimize unnecessary tool-definition context while preserving invocation semantics, constraints, authorization, and parameters |
| **DA-009** | SUB-AGENT RESULT COMPRESSOR | Convert verbose sub-agent results into compact structured findings while retaining evidence, locations, confidence, unresolved questions, and recommendations |
| **DA-010** | AGENT MEMORY OPTIMIZER | Separate short-term task state from durable project/task memory and retain only useful information |
| **DA-011** | CODE-AWARE SEMANTIC DEDUPLICATOR | Detect duplicate/equivalent code, stack traces, errors, files, search results, diffs, and findings |
| **DA-012** | ERROR-DRIVEN CONTEXT RETRIEVAL | Use compiler/test/runtime errors and source locations to drive targeted retrieval |
| **DA-013** | DEVELOPER-AGENT CONTEXT BUDGET MANAGER | Dynamically allocate budgets across repository, conversation, tools, MCP, memory, search results, terminal output, and agent state |
| **DA-014** | AGENT LOOP COST / TOKEN CONTROLLER | Track cumulative cost of reasoning-tool-reasoning cycles and stop low-value loops |
| **DA-015** | CODE CONTEXT REORDERER | Optimize ordering of code, instructions, errors, tests, and evidence while preserving instruction precedence |
| **DA-016** | FILE-SECTION LAZY LOADER | Prefer metadata, signatures, symbol summaries, and targeted sections before loading complete files |
| **DA-017** | REPOSITORY SEARCH RESULT OPTIMIZER | Rank and deduplicate code-search results and return the smallest useful set |
| **DA-018** | AGENT PLAN CONTEXT OPTIMIZER | Retain active plan state, decisions, milestones, and unresolved work instead of full planning history |
| **DA-019** | CODE EDIT CONTEXT OPTIMIZER | Provide the smallest safe context required for a correct patch while preserving interfaces, dependencies, tests, and formatting constraints |
| **DA-020** | DEVELOPER-AGENT OUTPUT CONTROLLER | Bound verbose explanations, patches, summaries, and machine-readable outputs without harming correctness |
| **DA-021** | AGENT TASK COMPLETION / EARLY-EXIT CONTROLLER | Prevent unnecessary searches, model calls, tests, and tool loops after sufficient completion |
| **DA-022** | DEVELOPER-AGENT CACHE LAYER | Support exact, semantic, repository-aware, tool-result, search-result, repository-map, and stable-context caching with freshness and authorization controls |
| **DA-023** | CONTEXT PROVENANCE TRACKER | Record the origin of retained context (file/path/line, tool, search, sub-agent, conversation turn, or memory) |
| **DA-024** | CONTEXT INVALIDATION ENGINE | Invalidate repository/search/tool/memory caches when files, branches, dependencies, permissions, or external state change |
| **DA-025** | PATCH / CHANGE IMPACT ANALYZER | Estimate affected files, symbols, tests, and dependencies and prioritize relevant context |

---

## 23. Optimization Technique Catalog

Every optimization technique carries the full required documentation as specified by the problem statement.

---

### Technique: Sanitization

| Field | Value |
|---|---|
| **Applicability** | All request types; first stage in every pipeline invocation |
| **Prerequisites** | Raw input available; content-type awareness for code vs. prose detection |
| **Expected Benefit** | 2-15% token reduction from whitespace, formatting, and UI noise removal; normalizes input for downstream stages |
| **Optimization Overhead** | Deterministic, near-zero latency; negligible compute cost |
| **Quality Risks** | Risk of removing syntax meaningful to the task (indentation in Python, whitespace in diffs); mitigated by content-aware logic |
| **Fallback** | If sanitization fails: use original input verbatim |
| **Evaluation Methodology** | Measure token count before vs. after; run quality gate on sanitized vs. original output; track false-removal rate on benchmark corpus |
| **Observability Metrics** | tokens.raw_input, tokens.sanitized, tokens.removed_by_sanitizer, sanitizer.false_removal_rate |
| **Production Maturity** | P0 -- Required for foundation; no benchmark gate before enabling |

---

### Technique: Query Compression

| Field | Value |
|---|---|
| **Applicability** | Verbose user requests; requests with filler, politeness, or redundant explanations; not applicable to short/terse queries where compression yield is near zero |
| **Prerequisites** | T1.2 Intent Classifier output; entity extraction complete; original request preserved for audit and fallback |
| **Expected Benefit** | Source whitepaper reports 30-60% query reduction depending on verbosity; treat as validation target, not guarantee |
| **Optimization Overhead** | Lightweight LLM call or rule-based compression; token cost of compression call must be included in net-savings accounting |
| **Quality Risks** | Risk of losing constraints, exceptions, or required output format; mitigated by mandatory preservation list |
| **Fallback** | If compressed query fails validation: use original query |
| **Evaluation Methodology** | Semantic fidelity check (embedding similarity of compressed vs. original intent); downstream answer quality comparison; validate 30-60% target on own corpus |
| **Observability Metrics** | tokens.query_raw, tokens.query_compressed, tokens.query_removed, query_compressor.fidelity_score |
| **Production Maturity** | P1 -- Requires passing experimental validation matrix before enabling |

---

### Technique: Context Pruning

| Field | Value |
|---|---|
| **Applicability** | Any request with retrieved context, conversation history, tool output, or documents exceeding relevance threshold |
| **Prerequisites** | T1.3 Entity Extraction; T1.2 Intent Classifier; query/task defined; pruning techniques calibrated per content type |
| **Expected Benefit** | 20-60% context reduction depending on content quality; primary signal is relevance, not compression |
| **Optimization Overhead** | Embedding computation (if used); lightweight for lexical/metadata approaches; moderate for embedding-based approaches |
| **Quality Risks** | False pruning of relevant context (delayed-relevance problem); mitigated by CL-004 reversibility and QO-003 delayed-relevance protection |
| **Fallback** | If pruning fails: retain bounded original context |
| **Evaluation Methodology** | Context reduction %; retrieval recall@K before vs. after; answer quality baseline vs. optimized; false-pruning rate tracked in benchmark |
| **Observability Metrics** | tokens.context_before_pruning, tokens.context_after_pruning, pruner.false_pruning_rate, pruner.quality_impact_score |
| **Production Maturity** | P1 -- Requires passing experimental validation matrix |

---

### Technique: Context Deduplication

| Field | Value |
|---|---|
| **Applicability** | Multi-turn conversations; RAG with overlapping chunk sources; tool outputs with repeated fields; agent pipelines with repeated context propagation |
| **Prerequisites** | Context collected from all sources; canonical representation defined |
| **Expected Benefit** | 5-40% context reduction in document-heavy or multi-source workloads |
| **Optimization Overhead** | Hash-based exact deduplication is near-zero; near-duplicate detection (embedding comparison) is moderate |
| **Quality Risks** | Removal of near-duplicates that have slight differences; mitigated by canonical copy preservation and audit record |
| **Fallback** | Retain all context if deduplication cannot run |
| **Evaluation Methodology** | Duplicate token removal %; recall@K; answer quality comparison |
| **Observability Metrics** | tokens.removed_by_deduplicator, deduplicator.duplicate_count, deduplicator.near_duplicate_count |
| **Production Maturity** | P1 -- High confidence, enable after validation |

---

### Technique: Adaptive Top-K Retrieval

| Field | Value |
|---|---|
| **Applicability** | All RAG workloads; replaces fixed-K retrieval |
| **Prerequisites** | Query complexity assessment (T0.2); relevance scoring; token budget available |
| **Expected Benefit** | Reduces unnecessary context for simple queries; improves relevance for complex queries; avoids token waste from over-retrieval |
| **Optimization Overhead** | Requires complexity scoring before retrieval; negligible additional latency |
| **Quality Risks** | Under-retrieval for complex queries; mitigated by escalation path and quality gate |
| **Fallback** | Use fixed-K (e.g., K=4) as safe fallback |
| **Evaluation Methodology** | Compare adaptive K vs. fixed K on: retrieved tokens, retrieval quality, answer quality, cost |
| **Observability Metrics** | retrieval.k_selected, retrieval.k_fixed_baseline, retrieval.tokens_retrieved, retrieval.quality_score |
| **Production Maturity** | P1 -- Requires comparison against fixed-K baseline |

---

### Technique: Token-Aware Ranking

| Field | Value |
|---|---|
| **Applicability** | All retrieval-augmented workloads; any context assembly with multiple candidate chunks |
| **Prerequisites** | Relevance scores; token counts per chunk; source authority metadata; entity overlap data |
| **Expected Benefit** | Improves the utility-per-token ratio of the context window; reduces token waste from low-value high-cost content |
| **Optimization Overhead** | Scoring computation per chunk; configurable formula adds overhead proportional to signals used |
| **Quality Risks** | Over-penalizing token cost may exclude relevant long documents; formula must be benchmarked |
| **Fallback** | Use relevance-only ranking if token-aware scoring fails |
| **Evaluation Methodology** | Utility/token before vs. after; answer quality comparison; benchmark formula variants |
| **Observability Metrics** | ranker.utility_per_token, ranker.avg_relevance_score, ranker.tokens_ranked, ranker.formula_version |
| **Production Maturity** | P3 -- Advanced; requires benchmarked formula before production |

---

### Technique: Context Compression

| Field | Value |
|---|---|
| **Applicability** | Long-context workloads; RAG with verbose documents; agent histories; when context exceeds budget after pruning and deduplication |
| **Prerequisites** | Context pruned and deduplicated; compression invariants defined (QO-001); quality threshold configured |
| **Expected Benefit** | Source whitepaper: 44.1% input reduction in evaluated settings; treat as benchmark reference |
| **Optimization Overhead** | Model-based compression requires inference (compute + token cost); must be included in net-savings calculation |
| **Quality Risks** | Compression may lose factual detail, citations, or code structure; mitigated by compression contracts (QO-001) |
| **Fallback** | If compression fails: use uncompressed context |
| **Evaluation Methodology** | Compression ratio; answer quality before vs. after; entity/citation preservation rate; validate against own corpus |
| **Observability Metrics** | compressor.ratio, compressor.tokens_in, compressor.tokens_out, compressor.quality_delta, compressor.latency_ms |
| **Production Maturity** | P1 -- Requires passing validation matrix |

---

### Technique: Context Reordering

| Field | Value |
|---|---|
| **Applicability** | Long-context workloads where position bias (lost-in-the-middle) is a known risk |
| **Prerequisites** | Context selected and ranked; model-specific ordering policy defined |
| **Expected Benefit** | Mitigates position bias; positions most-relevant content at start/end; measurable quality improvement in long-context settings |
| **Optimization Overhead** | Sorting/reordering only; negligible compute cost |
| **Quality Risks** | Risk of accidentally changing instruction precedence; source boundary confusion |
| **Fallback** | Use original context order |
| **Evaluation Methodology** | Answer quality comparison with position bias tests; measure lost-in-the-middle effect |
| **Observability Metrics** | reorderer.position_bias_score, reorderer.quality_delta, reorderer.model_policy |
| **Production Maturity** | P3 -- Advanced; requires quality measurement before and after |

---

### Technique: Prompt Caching

| Field | Value |
|---|---|
| **Applicability** | Any request with stable system instructions, tool definitions, or policy content that repeats across requests |
| **Prerequisites** | Cache-stable prefix identified; provider supports prefix caching; tenant isolation configured |
| **Expected Benefit** | Material reduction in repeated-input cost and latency for cached prefix tokens; provider-specific |
| **Optimization Overhead** | Cache write cost (first occurrence); cache read pricing (subsequent occurrences); net benefit depends on reuse frequency |
| **Quality Risks** | Stale cache returning outdated instructions; mitigated by TTL and explicit invalidation |
| **Fallback** | If cache unavailable: continue without cache; count as cache miss |
| **Evaluation Methodology** | Cache hit rate; cost per request with vs. without cache; latency comparison; break-even reuse count |
| **Observability Metrics** | cache.prompt.hit_rate, cache.prompt.miss_rate, cache.prompt.write_cost, cache.prompt.read_cost, cache.prompt.tokens_reused |
| **Production Maturity** | P1 -- High confidence; provider-specific setup required |

---

### Technique: Semantic Caching

| Field | Value |
|---|---|
| **Applicability** | Workloads with semantically repetitive queries; FAQ-style questions; repeated analysis tasks |
| **Prerequisites** | Semantic similarity model; freshness validation; authorization check mechanism; configurable similarity threshold |
| **Expected Benefit** | Avoid full inference for semantically equivalent requests; cost savings proportional to cache hit rate |
| **Optimization Overhead** | Embedding computation per request for similarity matching; authorization and freshness checks |
| **Quality Risks** | Returning stale or unauthorized results; treating similar-but-different queries as equivalent |
| **Fallback** | If semantic match is uncertain: treat as cache miss and proceed to full pipeline |
| **Evaluation Methodology** | Hit rate; correctness rate of served results; freshness validation pass rate; authorization check pass rate |
| **Observability Metrics** | cache.semantic.hit_rate, cache.semantic.similarity_score, cache.semantic.freshness_check_pass_rate, cache.semantic.auth_check_pass_rate |
| **Production Maturity** | P2 -- Requires freshness/authorization infrastructure before enabling |

---

### Technique: Tool Output Filtering

| Field | Value |
|---|---|
| **Applicability** | All tool calls that return responses larger than required by the task |
| **Prerequisites** | Tool response schema defined; required fields identified per task type |
| **Expected Benefit** | Source whitepaper example: 10,000 to 1,200 tokens (88% reduction); workload-specific |
| **Optimization Overhead** | Schema-based field selection is negligible; model-based filtering has moderate overhead |
| **Quality Risks** | Removal of fields needed for downstream validation or audit; mitigated by TE-006 mandatory field preservation |
| **Fallback** | Pass full tool output if filtering cannot run |
| **Evaluation Methodology** | Tool output tokens before vs. after; task completion rate; audit field preservation rate |
| **Observability Metrics** | tools.output_tokens_raw, tools.output_tokens_filtered, tools.filter_ratio, tools.task_completion_rate |
| **Production Maturity** | P1 -- High confidence |

---

### Technique: Tool Result Caching

| Field | Value |
|---|---|
| **Applicability** | Deterministic or sufficiently stable tool calls (search, documentation retrieval, database queries, repository metadata, external APIs) |
| **Prerequisites** | Tool-specific TTL and freshness policy; authorization-aware cache keys; parameter normalization |
| **Expected Benefit** | Avoid repeated tool execution cost and latency; proportional to tool call repetition rate |
| **Optimization Overhead** | Cache write/read overhead; freshness check overhead |
| **Quality Risks** | Returning stale tool results; mitigated by TTL and explicit invalidation |
| **Fallback** | Execute tool fresh if cache miss or freshness check fails |
| **Evaluation Methodology** | Tool calls avoided %; task completion with cached results; freshness violation rate |
| **Observability Metrics** | cache.tool.hit_rate, cache.tool.miss_rate, cache.tool.calls_avoided, cache.tool.freshness_violations |
| **Production Maturity** | P2 -- Requires per-tool freshness policy |

---

### Technique: Model Routing

| Field | Value |
|---|---|
| **Applicability** | Any request that can be satisfied by a lower-cost model without quality degradation |
| **Prerequisites** | Model tier registry; task complexity assessment; historical quality data per model/intent |
| **Expected Benefit** | Substantial cost reduction proportional to tier price differential; RouteLLM research demonstrates the concept (benchmark-specific) |
| **Optimization Overhead** | Routing decision overhead; complexity scoring; quality monitoring required |
| **Quality Risks** | Routing safety-critical or complex requests to incapable models; **explicitly prohibited by architectural requirement** |
| **Fallback** | Use configured default model if routing fails |
| **Evaluation Methodology** | Cost per request by routing decision; quality score by routed model; routing accuracy vs. gold standard |
| **Observability Metrics** | routing.model_selected, routing.candidate_model, routing.decision_reason, routing.quality_score_by_model, routing.escalation_rate |
| **Production Maturity** | P2 -- Requires quality baseline per model before enabling |

---

### Technique: Model Cascade / Escalation

| Field | Value |
|---|---|
| **Applicability** | Workloads where task difficulty varies; cost-sensitive with quality SLO |
| **Prerequisites** | Model routing enabled; quality evaluator available; escalation trigger conditions defined |
| **Expected Benefit** | FrugalGPT research supports the concept; workload-specific; depends on escalation rate |
| **Optimization Overhead** | First-tier inference cost paid even on escalation; quality evaluation cost |
| **Quality Risks** | Quality evaluator itself may miss escalation need; evaluation cost may exceed savings for low-escalation workloads |
| **Fallback** | Default to full-capability model if cascade evaluation fails |
| **Evaluation Methodology** | Escalation rate; cost savings vs. always-strong-model; quality parity on escalated requests |
| **Observability Metrics** | cascade.first_tier_model, cascade.escalation_triggered, cascade.escalation_reason, cascade.cost_vs_baseline |
| **Production Maturity** | P2 -- Requires quality evaluator and escalation rate benchmarking |

---

### Technique: Reasoning Budget Control

| Field | Value |
|---|---|
| **Applicability** | Models with configurable reasoning/thinking budget (provider-specific); tasks that vary in required reasoning depth |
| **Prerequisites** | Provider supports reasoning controls; task complexity scoring available; quality threshold configured |
| **Expected Benefit** | Reduced reasoning token cost for simple tasks; proportional to reasoning budget reduction |
| **Optimization Overhead** | Complexity scoring overhead; quality comparison overhead |
| **Quality Risks** | **Explicitly prohibited: never reduce reasoning solely to meet a token target** -- must compare quality against baseline |
| **Fallback** | Use safe default reasoning budget if control fails |
| **Evaluation Methodology** | Reasoning tokens allocated vs. consumed; correctness before vs. after; quality gate pass rate |
| **Observability Metrics** | reasoning.budget_allocated, reasoning.tokens_consumed, reasoning.quality_score, reasoning.escalation_count |
| **Production Maturity** | P2 -- Provider-specific; requires quality validation per model |

---

### Technique: Agent Early Exit

| Field | Value |
|---|---|
| **Applicability** | Agentic workflows with multiple steps; loops where objective can be verified before completion |
| **Prerequisites** | Objective definition; quality thresholds; completion verification mechanism |
| **Expected Benefit** | Avoids unnecessary model and tool calls after objective is satisfied; token savings proportional to loop count avoided |
| **Optimization Overhead** | Completion evaluation overhead per step |
| **Quality Risks** | Premature termination before objective is truly complete; mitigated by quality gate on exit decision |
| **Fallback** | Continue according to safe workflow policy if stop decision fails |
| **Evaluation Methodology** | Early-exit rate; task completion rate; tokens and calls avoided; regression rate |
| **Observability Metrics** | agent.early_exit_triggered, agent.steps_planned, agent.steps_executed, agent.steps_skipped, agent.tokens_saved_by_exit |
| **Production Maturity** | P2 -- Requires completion-verification mechanism |

---

### Technique: Output Schema Enforcement

| Field | Value |
|---|---|
| **Applicability** | All structured output tasks; extraction, classification, coding, tool responses |
| **Prerequisites** | Intent classification; output schema defined per intent |
| **Expected Benefit** | Source whitepaper: 40-70% output reduction (treat as validation target); eliminates verbose prose in structured responses |
| **Optimization Overhead** | Schema validation overhead; re-prompt on schema failure |
| **Quality Risks** | Over-constraining schema may truncate required information |
| **Fallback** | Use fallback validation/re-prompt if schema fails |
| **Evaluation Methodology** | Output tokens before vs. after; schema compliance rate; completeness score |
| **Observability Metrics** | output.tokens_raw, output.tokens_schema_constrained, output.schema_compliance_rate |
| **Production Maturity** | P1 -- High confidence |

---

### Technique: Output Length Control

| Field | Value |
|---|---|
| **Applicability** | All requests where output length can be bounded without harming completeness |
| **Prerequisites** | AR-003 Output Budget Forecasting; intent classification; field-level budget policy |
| **Expected Benefit** | 40-70% output reduction (source whitepaper); proportional to verbosity of unconstrained output |
| **Optimization Overhead** | Budget enforcement negligible; truncation detection overhead |
| **Quality Risks** | Truncating required information; mitigated by semantic boundary truncation and controlled expansion |
| **Fallback** | Remove output length constraint if quality gate fails |
| **Evaluation Methodology** | Output tokens before vs. after; completeness score; quality gate pass rate |
| **Observability Metrics** | output.tokens_target, output.tokens_actual, output.truncations, output.expansions, output.completeness_score |
| **Production Maturity** | P1 -- High confidence |

---

### Technique: Batch Optimization

| Field | Value |
|---|---|
| **Applicability** | Asynchronous, non-interactive workloads; bulk extraction, classification, summarization |
| **Prerequisites** | Workload classified as async-eligible; freshness requirements permit batching; latency SLO is not interactive |
| **Expected Benefit** | Reduced per-token cost via provider batch pricing; improved throughput |
| **Optimization Overhead** | Batching coordination overhead; increased latency for individual requests |
| **Quality Risks** | Inappropriate batching of interactive or time-sensitive requests |
| **Fallback** | Process requests individually if batching fails |
| **Evaluation Methodology** | Cost per token batch vs. individual; throughput comparison; latency impact assessment |
| **Observability Metrics** | batch.requests_batched, batch.cost_per_token_batch_vs_individual, batch.throughput, batch.latency_p95 |
| **Production Maturity** | P3 -- Advanced; requires workload classification infrastructure |

---

### Technique: Soft Reset / Context Budgeting

| Field | Value |
|---|---|
| **Applicability** | Long-running conversations or agent sessions approaching context window limits |
| **Prerequisites** | Context budget configured per model/tenant/workflow; staged compaction policy (CL-005) defined |
| **Expected Benefit** | Prevents context overflow; maintains quality over long sessions; controls token growth |
| **Optimization Overhead** | Budget tracking; compaction steps at thresholds |
| **Quality Risks** | Loss of important historical context; mitigated by key-fact preservation and reset report |
| **Fallback** | Retain most-recent bounded context if compaction fails |
| **Evaluation Methodology** | Context size over session length; quality at various compaction thresholds; key-fact retention rate |
| **Observability Metrics** | budget.tokens_used, budget.ceiling, budget.compaction_events, budget.reset_events, budget.key_facts_preserved |
| **Production Maturity** | P1 -- High confidence |

---

## 24. Default Optimization Order

The default optimization strategy follows **lowest-risk and lowest-cost transformations first**. This is a policy, not a rigid implementation constraint. The system shall be able to reorder stages when benchmark evidence demonstrates a better cost/quality/latency trade-off.

| # | Stage | Rationale |
|---|---|---|
| 1 | Exact cache lookup | Zero inference cost; highest ROI |
| 2 | Semantic cache lookup where safe | High ROI; requires auth/freshness checks |
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
| 14 | Context compression | Higher-cost transformation; after cheaper stages |
| 15 | Soft reset | Budget enforcement |
| 16 | Query compression | User request optimization |
| 17 | Cache-aware prompt assembly | Optimize cache prefix structure |
| 18 | Model routing | Select least expensive capable model |
| 19 | Reasoning-budget selection | Allocate reasoning depth |
| 20 | LLM execution | Inference (may be skipped entirely per architectural axiom) |
| 21 | Output schema enforcement | Constrain output structure |
| 22 | Output length enforcement | Constrain output tokens |
| 23 | Agent stop/early exit | Evaluate continuation necessity |
| 24 | Fallback/escalation when quality is insufficient | Quality recovery |

---

## 25. Provider / Model Optimization Profiles

Maintain a **versioned capability and economics profile** for every supported provider/model.

**Profile dimensions:**

| Dimension |
|---|
| Input price |
| Output price |
| Cache write price |
| Cache read price |
| Cache TTL |
| Context limit |
| Output limit |
| Reasoning controls |
| Tool support |
| Structured output support |
| Batch support |
| Latency characteristics |
| Known cache constraints |
| Known routing constraints |
| Compliance/data-residency requirements |

> [!CAUTION]
> The optimizer shall **not** assume that an optimization valid for one provider or model is valid for another.

---

## 26. Inference / Serving Optimization Layer (P5 -- Optional)

> [!IMPORTANT]
> This layer is **OPTIONAL and infrastructure-dependent**. It must remain **separate** from prompt/context optimization and must be enabled only after workload-specific validation.

> [!CAUTION]
> **2026-09-15 Hardening Pass (H17, PS §52.17):** the Control Plane's role at this layer is **awareness, integration, routing, policy, and capability negotiation** with infrastructure/providers. It must **NOT** implement its own KV-cache engine, continuous-batching scheduler, speculative-decoding mechanism, quantization pipeline, or prefill/decode disaggregation. These remain infrastructure/provider-owned mechanisms the Control Plane integrates with — see Section 47.8. This resolves any ambiguity in the capability list below, which is a list of things the Control Plane is *aware of and may route/negotiate against*, not a build list.

**Capabilities the Control Plane is AWARE OF and may ROUTE/NEGOTIATE against (implemented by infrastructure/providers, not by the Control Plane):**

| Capability |
|---|
| Prefix/KV-cache reuse |
| KV-cache compression |
| Continuous batching |
| Request scheduling |
| Prefill optimization |
| Decode optimization |
| Speculative decoding |
| Quantization |
| Model replica selection |
| GPU utilization optimization |
| Inference queue management |

For each capability, the Control Plane's own responsibility is limited to: detecting whether the provider/infrastructure exposes it (via Section 25's provider/model profile), routing/selecting to take advantage of it when net-beneficial and policy-compliant (Section 47.4's net-value accounting applies), negotiating capability parameters through the provider adapter (e.g., batching eligibility hints, cache-affinity hints), and measuring the resulting effect kept separate from Layer 1/2 measurement (AC-036).

---

## 27. Token and Cost Accounting Ledger

The framework shall maintain a detailed **per-request token ledger**.

### 27.1 Standard Ledger Fields

**INPUT:**
tokens.raw_input, tokens.sanitized, tokens.query_compressed, tokens.context, tokens.retrieved, tokens.pruned, tokens.deduplicated, tokens.context_compressed, tokens.cached_input, tokens.uncached_input

**OUTPUT:**
tokens.raw_output, tokens.optimized_output, tokens.truncated, tokens.expanded_retry

**CACHE:**
cache.exact_hits, cache.semantic_hits, cache.misses, cache.writes, cache.reads, cache.cacheable_tokens, cache.reused_tokens

**MODEL:**
model.selected, model.candidate, model.routing_decision, model.escalation, model.reasoning_budget, model.reasoning_tokens

**TOOLS:**
tools.calls_attempted, tools.calls_avoided, tools.output_tokens, tools.filtered_tokens, tools.cached_calls

**WORKFLOW:**
workflow.steps_planned, workflow.steps_executed, workflow.steps_skipped, workflow.early_exits, workflow.retries

**COST:**
cost.input, cost.output, cost.cache, cost.compression, cost.tool, cost.total_optimized, cost.baseline_estimated, cost.net_savings, cost.savings_pct

**PERFORMANCE:**
perf.e2e_latency_ms, perf.ttft_ms, perf.model_latency_ms, perf.compression_latency_ms, perf.cache_latency_ms, perf.tool_latency_ms

**QUALITY:**
quality.correctness_score, quality.relevance_score, quality.schema_compliance, quality.semantic_preservation, quality.user_task_score, quality.safety_validation

### 27.2 Extended Ledger -- Optimization Economics

**OPTIMIZATION ECONOMICS:**
optim.compute_cost, optim.latency_ms, optim.token_usage, optim.calls, optim.gross_inference_savings, optim.net_inference_savings, optim.net_savings_pct, optim.breakeven_reuse_count, optim.breakeven_cache_hit_count

**OUTCOME ECONOMICS:**
outcome.cost_per_successful_outcome, outcome.tokens_per_successful_outcome, outcome.cost_per_coding_task, outcome.cost_per_agent_workflow, outcome.cost_per_verified_answer

**CONTEXT ECONOMICS:**
context.utility_per_token, context.freshness_score, context.roi, context.cacheability_score, context.reuse_probability, context.compression_benefit, context.recovery_cost

**AGENT ECONOMICS:**
agent.cost_per_iteration, agent.info_gain_per_iteration, agent.state_change_per_iteration, agent.progress_per_token, agent.tool_call_roi, agent.subagent_roi, agent.avoided_model_calls, agent.avoided_tool_calls

**CACHE ECONOMICS:**
cache.cacheable_tokens, cache.cached_tokens, cache.hit_rate, cache.miss_causes, cache.write_cost, cache.read_cost, cache.break_rate, cache.reuse_count, cache.roi

### 27.3 Cost Model

```
Baseline Cost =
    Baseline Input Tokens x Input Rate
  + Baseline Output Tokens x Output Rate
  + Tool Costs
  + Other Provider Charges

Optimized Cost =
    Uncached Input Tokens x Input Rate
  + Cache Read/Write Cost
  + Compression Cost
  + Optimized Output Tokens x Output Rate
  + Tool Costs
  + Routing/Cascade Costs
  + Other Provider Charges

Net Savings = Baseline Cost - Optimized Cost

Savings % = Net Savings / Baseline Cost x 100
```

The system shall support **configurable pricing** rather than hard-coded prices, and provider-specific pricing dimensions.

---

## 28. Quality Gates

> [!CAUTION]
> Token reduction shall **never** be accepted without quality validation.

Every optimization technique must be evaluated using **Baseline vs. Optimized** on:

| Dimension |
|---|
| Accuracy |
| Task completion |
| Retrieval quality |
| Factuality |
| Schema compliance |
| Semantic equivalence |
| Safety |
| User satisfaction |
| Latency |
| Cost |

Each technique shall have a **configurable quality threshold**.

**If the quality threshold is violated:**
1. Roll back the optimization
2. Fall back to the baseline representation
3. Record the failure
4. **Do NOT** report the optimization as a successful saving

---

## 29. Security and Governance Requirements

All security requirements are **mandatory and non-negotiable**.

| ID | Requirement |
|---|---|
| **SEC-001** | No optimization may bypass system/developer security instructions |
| **SEC-002** | Authorization boundaries must be preserved through caching, retrieval, compression, routing, and tool execution |
| **SEC-003** | Cache keys must be tenant/user/workspace aware where required |
| **SEC-004** | Sensitive information must not be placed in a cache unless permitted by the organization's security and retention policy |
| **SEC-005** | Prompt compression must preserve security-relevant constraints |
| **SEC-006** | Tool-result filtering must not remove fields required for authorization, validation, or audit |
| **SEC-007** | Semantic cache reuse must verify authorization and data freshness |
| **SEC-008** | All optimization transformations must be auditable |
| **SEC-009** | The framework shall support configurable data-retention policies |
| **SEC-010** | Provider-specific caching must not silently weaken organizational security requirements |

**Added by the 2026-09-15 Architecture Hardening Pass (PS §52, traced individually in Section 47):**

| ID | Requirement |
|---|---|
| **SEC-011** | Spend limits, circuit breakers, and budget-aware routing/execution must never bypass or substitute for authorization, policy, or security enforcement; a budget check and a security check are evaluated independently |
| **SEC-012** | Sensitive data (including PII/PHI/PCI/secrets where applicable) must be classified before admission to any optimization stage, and classification must determine encryption, retention, and caching eligibility; no specific regulatory regime is assumed unless the deployment's own scope establishes it |
| **SEC-013** | Deletion/erasure must propagate to every surface the Control Plane persisted data to — exact and semantic caches, agent/session memory, cost/token ledgers that retain content, and logs/traces — not only the primary store |
| **SEC-014** | Tool and MCP metadata (identity, capability declarations, schemas, versions) are part of the security/control boundary and must be authenticated, authorization-checked, and validated for staleness/integrity before being trusted; a tool result is not safe merely because the call was authorized |
| **SEC-015** | Consequential or irreversible actions designated by policy/risk classification must receive explicit human approval before execution; this applies only to the designated subset, not every action |
| **SEC-016** | Retrieved content, tool output, and other externally-sourced context are untrusted by default and must pass prompt-injection/content-integrity screening before any optimization stage acts on them |

These six requirements are architecturally realized by the Governance/Safety Plane components specified in Section 47.2 (SGE, DGE, TMG, HAG, CIS) and are traced individually in Section 47.18.

---

## 30. Failure and Fallback Model

**Every optimization stage must have an explicit fallback.**

| Failure Condition | Fallback |
|---|---|
| Cache unavailable | Continue without cache |
| Semantic match uncertain | Treat as cache miss |
| Sanitization fails | Use original input |
| Classification fails | Use safe general policy |
| Entity extraction fails | Use unstructured retrieval path |
| Ranking fails | Use bounded recent/relevance fallback |
| Pruning fails | Retain bounded original context |
| Compression fails | Use uncompressed context |
| Model routing fails | Use configured default model |
| Reasoning budget fails | Use safe default budget |
| Output schema fails | Use fallback validation/re-prompt |
| Agent stop decision fails | Continue only according to safe workflow policy |
| Token accounting fails | Mark usage as unverified; **never fabricate savings** |

> [!IMPORTANT]
> Optimization failure must **not** become application failure unless the optimization itself is explicitly required by policy.

---

## 31. Observability and Governance

### 31.1 Dashboard Requirements

**Executive dashboard:**
Total AI spend, Cost saved, Savings percentage, Model mix, Cache utilization, Token growth, Quality impact

**Engineering dashboard:**
Tokens by stage, Context size, Compression ratio, Model routing, Tool usage, Cache hit rate, Latency, Failures, Fallbacks

**Product/Operations dashboard:**
Cost per workflow, Cost per user/team, Cost per business transaction, Quality trends, Adoption, ROI

### 31.2 Required Dimensions

| Dimension |
|---|
| Tenant |
| Application |
| Workflow |
| Agent |
| Model |
| Provider |
| User/team where permitted |
| Prompt type |
| Intent |
| Date/time |

### 31.3 Governance Requirements

The framework must expose for audit/debugging, for every production request:

| Question |
|---|
| What did we send? |
| What did we remove? |
| What did we compress? |
| What did we reuse? |
| What did we cache? |
| Which model did we use and why? |
| How many tool/model calls did we avoid? |
| How much did optimization cost? |
| How much did it save? |
| Did quality remain acceptable? |
| Can we prove the result? |

---

## 32. Benchmarking Framework

Before production rollout, run the optimization framework against a **representative corpus of real prompts**.

### 32.1 Required Corpus Types

Simple prompts, Long prompts, Long-context tasks, RAG tasks, Coding tasks, Debugging tasks, Tool-heavy agent tasks, Multi-turn conversations, Structured extraction, Comparative queries, Procedural queries, High-risk/safety-sensitive workflows where permitted

### 32.2 Required Comparison Per Request

**BASELINE:** Original application behavior without optimization.
**OPTIMIZED:** Full optimization pipeline.

**Compare:** Input tokens, Output tokens, Total tokens, Cached tokens, Model calls, Tool calls, Cost, Latency, Quality, Failure rate

### 32.3 Experimental Validation Matrix

Each technique must be **validated independently** before permanent pipeline activation:

| Technique | Primary Metric |
|---|---|
| Sanitization | Token reduction + quality |
| Query compression | Token reduction + semantic fidelity |
| Context pruning | Context reduction + recall |
| Context deduplication | Duplicate removal + recall |
| Adaptive Top-K | Tokens + retrieval quality |
| Token-aware ranking | Utility/token + answer quality |
| Context compression | Compression + quality |
| Context reordering | Position + answer quality |
| Prompt caching | Cache hit + cost/latency |
| Semantic caching | Hit rate + correctness/freshness |
| Tool output filtering | Tool tokens + task completion |
| Tool result caching | Calls avoided + freshness |
| Model routing | Cost + quality |
| Model cascade | Cost + quality |
| Reasoning budget control | Reasoning tokens + correctness |
| Agent early exit | Calls avoided + task completion |
| Output schema | Output tokens + correctness |
| Output length control | Output tokens + completeness |
| Batch optimization | Cost/throughput + freshness |
| Soft reset | Context size + quality |

---

## 33. Enterprise Deployment Model

| Mode | Description |
|---|---|
| **A** | Python SDK/library |
| **B** | CLI |
| **C** | API/middleware service |
| **D** | Drop-in integration at prompt assembly |
| **E** | Agent-orchestration middleware |
| **F** | CI/CD or GitHub Action for benchmark/regression testing |

The design shall minimize changes to existing applications.

---

## 34. Rollout Strategy

| Phase | Description |
|---|---|
| **PHASE 1 -- OBSERVE** | Run in shadow mode. Do not modify production requests. Record baseline token/cost/quality. |
| **PHASE 2 -- BENCHMARK** | Run optimization offline. Compare techniques independently. Identify high-value workflows. |
| **PHASE 3 -- CONTROLLED PILOT** | Enable optimization for selected workflows. Preserve baseline fallback. |
| **PHASE 4 -- PRODUCTION** | Enable validated optimizations. Monitor cost, quality, latency, and failures. |
| **PHASE 5 -- GOVERNANCE** | Establish token budgets. Establish cost thresholds. Establish quality SLOs. Review optimization drift. |
| **PHASE 6 -- CONTINUOUS OPTIMIZATION** | Re-run benchmark after model/provider changes. Re-evaluate routing, compression, cache performance, and prompt templates. Detect regressions. |

---

## 35. Regression Testing

Every model, prompt, retrieval, or orchestration change shall be evaluated against the benchmark corpus.

**Regression dimensions:** Token usage, Cost, Latency, Quality, Retrieval recall, Cache hit rate, Model routing accuracy, Tool-call count, Agent step count

The system must detect when an optimization that previously produced savings **stops producing net savings**.

---

## 36. Implementation Priority

### P0 -- Foundation (Unblocked)

Token accounting, Baseline benchmark harness, Sanitizer, Context policy, Prompt assembler, Output controls, Observability, Quality evaluation

### P1 -- High-Confidence Optimization

Exact prompt caching, Context deduplication, Context pruning, Adaptive Top-K, Tool-output filtering, Query compression, Context compression, Output schema/length control, Soft reset

> [!IMPORTANT]
> Each P1/P2/P3 feature must pass the validation matrix before being enabled by default.

### P2 -- Cost Intelligence

Model routing, Model cascade, Semantic caching, Tool-result caching, Reasoning-budget controller, Agent early exit

### P3 -- Advanced Optimization

Token-aware ranking, Context reordering, Learned routing, Advanced compression, Dynamic budget allocation, Batch optimization

### P4 -- Control-Plane Intelligence

Optimization Decision Engine, Cost-of-Optimization Controller, Adaptive Optimization Depth, Context Utility / ROI scoring, Outcome-based optimization, Context dependency graph, Freshness-aware context management, Dependency-aware cache invalidation, Reversible optimization, Progressive context compaction, Cache economics, Cache fragmentation diagnosis, Cache-aware context construction, Tool-call ROI, Tool argument optimization, Dynamic tool loading, Programmatic tool execution, Agent loop progress measurement, Sub-agent economics, Marginal-gain model routing, Verifier-guided escalation, Shadow optimization, A/B experimentation, Continuous policy learning

### P5 -- Infrastructure-Dependent Inference Optimization (Optional)

KV/prefix cache optimization, Continuous batching, Speculative decoding, Quantization, Prefill/decode optimization, Inference scheduling, GPU utilization optimization

---

## 37. Non-Functional Requirements

All 13 NFRs are **mandatory**.

| ID | Requirement |
|---|---|
| **NFR-001** | **Correctness** -- Optimization must preserve task correctness |
| **NFR-002** | **Security** -- Optimization must not weaken security controls |
| **NFR-003** | **Privacy** -- Optimization and caching must comply with configured data-retention and privacy requirements |
| **NFR-004** | **Multi-tenancy** -- Tenant data and cache entries must remain isolated |
| **NFR-005** | **Provider Independence** -- The core framework should not be tied to a single provider |
| **NFR-006** | **Observability** -- Every optimization decision must be measurable |
| **NFR-007** | **Reliability** -- Optimization failures must have safe fallbacks |
| **NFR-008** | **Scalability** -- The framework must support high-volume enterprise traffic |
| **NFR-009** | **Low Overhead** -- Optimization must not consume more cost than it saves |
| **NFR-010** | **Explainability** -- The system should explain why a context item was removed, compressed, cached, routed, or retained where practical. **Strengthened 2026-09-15 (H15, PS §52.15):** for context admission/pruning, model/provider selection, cache reuse/rejection, optimization skip, execution block/recovery/supersession, and fallback decisions, an explanation must be retrievable for audit, not merely offered where convenient — see Section 47.16 |
| **NFR-011** | **Determinism** -- Policy-based optimization should be deterministic where feasible |
| **NFR-012** | **Configurability** -- Organizations must be able to tune policies per workflow |
| **NFR-013** | **Testability** -- Every optimization stage must be independently testable |
| **NFR-014** | **Control-Plane Self-Protection** (added 2026-09-15, H03, PS §52.3) -- The Control Plane must enforce its own latency and processing/cost budgets, with backpressure, and must degrade gracefully to a deterministic safe fallback under overload rather than degrading the application it serves. Distinct from NFR-009: NFR-009 governs whether an optimization is worth doing; NFR-014 governs what the Control Plane does to itself when it cannot keep up — see Section 47.4 |

---

## 38. Configuration Requirements

All major optimization behavior shall be configurable. Minimum configurable parameters:

Maximum context tokens, Per-intent budgets, Compression thresholds, Minimum quality thresholds, Cache TTL, Semantic similarity threshold, Retrieval Top-K limits, Model routing policy, Model escalation policy, Reasoning budgets, Output token limits, Tool-output field policies, Early-exit thresholds, Tenant policies, Data retention policies

---

## 39. Acceptance Criteria

All 38 acceptance criteria are **mandatory**.

| ID | Criterion |
|---|---|
| **AC-001** | The framework must provide measurable baseline and optimized token counts |
| **AC-002** | The framework must report net cost rather than only token reduction |
| **AC-003** | The framework must preserve configurable quality thresholds |
| **AC-004** | The framework must support provider/model-specific token accounting |
| **AC-005** | The framework must distinguish cached, compressed, removed, generated, retrieved, and reused tokens |
| **AC-006** | The framework must support safe exact caching |
| **AC-007** | The framework must support optional semantic caching with freshness and authorization validation |
| **AC-008** | The framework must support adaptive retrieval |
| **AC-009** | The framework must support context pruning and compression |
| **AC-010** | The framework must support model routing and optional escalation |
| **AC-011** | The framework must support output constraints |
| **AC-012** | The framework must detect unnecessary agent continuation |
| **AC-013** | The framework must reduce unnecessary tool-output propagation |
| **AC-014** | The framework must preserve tenant/security boundaries |
| **AC-015** | Every optimization must be independently measurable |
| **AC-016** | Every optimization must have a fallback |
| **AC-017** | The benchmark must demonstrate the actual cost/quality trade-off on the organization's own workload |
| **AC-018** | No source-reported percentage may be treated as a production guarantee without local validation |
| **AC-019** | The control plane must be able to skip an optimization when its expected benefit does not exceed its overhead |
| **AC-020** | The framework must report optimization overhead separately from inference savings |
| **AC-021** | The framework must support utility-per-token context scoring |
| **AC-022** | The framework must support freshness/version-aware context selection |
| **AC-023** | The framework must support dependency-aware cache invalidation |
| **AC-024** | The framework must support reversible context transformations where practical |
| **AC-025** | The framework must support progressive context compaction |
| **AC-026** | The framework must support dynamic tool discovery/loading where the integration surface permits |
| **AC-027** | The framework must estimate tool-call ROI before execution where practical |
| **AC-028** | The framework must support sub-agent cost/value evaluation before delegation |
| **AC-029** | The framework must support shadow optimization and controlled rollout |
| **AC-030** | The framework must support optimization A/B testing |
| **AC-031** | The framework must detect optimization regressions automatically |
| **AC-032** | The framework must support provider/model-specific optimization profiles |
| **AC-033** | The framework must optimize cost per successful outcome, not only cost per request |
| **AC-034** | The framework must support quality-preserving compression contracts |
| **AC-035** | The framework must support agent phase/session policies |
| **AC-036** | The framework must distinguish context optimization from inference-serving optimization |
| **AC-037** | The framework must expose optimization decisions and their rationale for audit/debugging |
| **AC-038** | The framework must support a safe fallback for every adaptive decision |

**Added by the 2026-09-15 Architecture Hardening Pass (PS §52.21, traced individually in Section 47):**

| ID | Criterion |
|---|---|
| **AC-039** | The framework must declare, per decision type, which operating mode (synchronous, asynchronous/precomputed, or hybrid) it uses, and account for its own decision latency/overhead in net-value accounting |
| **AC-040** | The framework must record, for every advisory, enforcement, or execution-ownership decision, which category applied |
| **AC-041** | The framework must enforce its own latency and cost budgets and degrade to a deterministic safe fallback under overload rather than degrading application correctness or security |
| **AC-042** | The framework's net-optimization-value accounting must include optimization overhead, retrieval/routing/cache overhead, added latency, retries, quality degradation, and task failure/regression, not tokens alone |
| **AC-043** | The framework must support spend limits and circuit breakers at tenant, organization, user, and application scope, and must never allow a budget decision to substitute for a security/authorization/policy decision |
| **AC-044** | The framework must expose a calibrated confidence score for probabilistic verifiers and a configurable acceptance threshold before that verifier's output gates an optimization decision |
| **AC-045** | The framework must support sensitivity classification prior to optimization admission and must be able to report which surfaces still hold data derived from a given source after a deletion request |
| **AC-046** | Every coding-agent integration must declare its integration feasibility tier |
| **AC-047** | The framework must detect and surface, not silently resolve, a conflict between agent-owned working memory and Control-Plane-owned execution/authorization/policy state |
| **AC-048** | The framework must authenticate tool/MCP identity and validate schema/capability metadata integrity and staleness before trusting a tool result |
| **AC-049** | The framework must detect concurrent mutation, stale decisions, and version conflicts across multiple agents/sub-agents/executions sharing a resource, and must not blindly replay non-idempotent operations across executions |
| **AC-050** | The framework must support a policy-configurable human-approval gate for designated consequential/irreversible actions, without requiring approval for every action |
| **AC-051** | The framework must run prompt-injection/content-integrity screening on externally-sourced content before any optimization stage admits, ranks, compresses, caches, or acts on it, and must fail closed if the screening itself cannot complete |
| **AC-052** | The framework must retain a retrievable explanation for context admission/pruning, model/provider selection, cache reuse/rejection, optimization skip, execution block/recovery/supersession, and fallback decisions |
| **AC-053** | Checkpoint/resume state must remain interpretable for reconciliation across a change in selected model/provider and must not require a specific provider's native session mechanism to be correct |

---

## 40. Success Metrics

**TOKEN EFFICIENCY:** Input token reduction, Output token reduction, Context token reduction, Duplicate-token reduction, Tool-output reduction, Tokens reused through caching

**INFERENCE EFFICIENCY:** Model calls avoided, Tool calls avoided, Agent steps avoided, Early exits, Routing to lower-cost models, Reasoning budget reduction

**COST EFFICIENCY:** Cost/request, Cost/workflow, Cost/user/team, Cost/business transaction, Total monthly cost, Total annual cost, Net savings, ROI

**PERFORMANCE:** Latency, TTFT (time to first token), Throughput

**QUALITY:** Accuracy, Factuality, Relevance, Task completion, Schema compliance, User satisfaction

**GOVERNANCE:** Policy violations, Cache isolation failures, Optimization failures, Fallback rate, Unverified accounting, Regression rate

**CONTROL-PLANE EFFECTIVENESS:** Optimization decision accuracy, Optimization overhead, Net optimization savings, Cost per successful outcome, Context utility/token, Tool-call ROI, Sub-agent ROI, Cache ROI, Cache miss diagnosis rate, Reversible-recovery success rate, Policy adaptation success rate, Quality-constrained cost frontier

---

## 41. Anti-Patterns to Avoid

The implementation must avoid **all** of the following.

**Primary anti-patterns (from problem statement section 25):**
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

**Extended anti-patterns (from problem statement section 48):**
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

**Added by the 2026-09-15 Architecture Hardening Pass (PS §52.20/§25/§48):**
- Building the Control Plane as a replacement agent orchestrator, model-training system, model-runtime infrastructure, provider infrastructure, general-purpose developer IDE, or coding-agent UX replacement instead of integrating with those systems
- Assuming a coding-agent integration provides complete request interception when only a partial or observability-only feasibility tier is actually available
- Treating a verifier's pass/fail output as ground truth without a calibrated confidence score and acceptance threshold
- Allowing agent-owned working memory to override Control Plane execution state, authorization state, policy state, or context/workflow version
- Admitting retrieved, tool, or other externally-sourced content into an optimization stage before it has passed prompt-injection/content-integrity screening
- Allowing the Control Plane's own latency, compute, or cost overhead to exceed the value of the optimization it enables

---

## 42. Enterprise Optimization Maturity Model

Every optimization technique shall have a production lifecycle level.

| Level | Name | Description |
|---|---|---|
| **0** | RESEARCH | Literature/provider evidence only |
| **1** | EXPERIMENTAL | Local benchmark, offline validation |
| **2** | SHADOW | Production-like traffic, no user-visible modification |
| **3** | CONTROLLED PILOT | Limited production traffic, baseline fallback |
| **4** | PRODUCTION | Validated policy, monitoring and rollback |
| **5** | AUTO-TUNED | Continuous evidence-based policy adjustment, governance approval boundaries, automatic regression rollback |

> [!CAUTION]
> No technique shall be classified as production-safe solely because a research paper or vendor reports a benchmark improvement.

---

## 43. Reference Research and Validation Notes

### 43.1 Validated References

| # | Title | Identifier |
|---|---|---|
| 1 | Tokenomics: Quantifying Where Tokens Are Used in Agentic Software Engineering | arXiv:2601.14470 |
| 2 | Optimizing Token Consumption in LLMs: A Nano Surge Approach for Code Reasoning Efficiency | arXiv:2504.15989 |
| 3 | Optimizing Large Language Models for OpenAPI Code Completion | arXiv:2405.15729 |
| 4 | LLMLingua: Compressing Prompts for Accelerated Inference of Large Language Models | ACL 2023 (EMNLP) |
| 5 | LongLLMLingua: Accelerating and Enhancing LLMs in Long Context Scenarios via Prompt Compression | ACL 2024 |
| 6 | LLMLingua-2: Data Distillation for Efficient and Faithful Task-Agnostic Prompt Compression | Findings of ACL 2024 |
| 7 | RouteLLM: Learning to Route LLMs with Preference Data | arXiv:2406.18665 |
| 8 | FrugalGPT: How to Use Large Language Models While Reducing Cost and Improving Performance | arXiv:2305.05176 |
| 9 | Anthropic Prompt Caching Documentation | platform.claude.com |

### 43.2 Reference Corrections

- arXiv ID `2501.09136` was identified as an incorrect pairing in earlier drafts. It must **not** be retained without correction.
- The OpenAPI paper: corrected identifier is `2405.15729` (not `2405.15728`).

### 43.3 Source Whitepaper Benchmarks (Reference Only -- Not Production Guarantees)

| Metric | Value |
|---|---|
| Input reduction | 44.1% |
| Output reduction | 67.4% average |
| Representative input saving | 327 tokens/call |
| Query compression | 30-60% |
| Output optimization | 40-70% |
| Default soft-reset budget | 500K tokens |
| Representative agentic task | 52K tokens |
| Scale: 1,000 developers x 5 tasks/day x 50K tokens | 250M tokens |
| Saved tokens/call (estimated) | ~119.4M tokens |
| Net annual savings | $253.70 (Sonnet) to $1,666.34 (Opus) per workload |

### 43.4 Evidence Classification Policy

| Classification | Meaning |
|---|---|
| Research evidence | Supports validation as a candidate technique |
| Provider capability | Supported by provider but semantics vary |
| Local benchmark result | Measured on organization's own workload |
| Production policy | Deployed based on local evidence |
| Production guarantee | Only established by local production evidence |

> [!CAUTION]
> Only local production evidence may establish a production guarantee. No source-reported percentage is a production guarantee.

---

## 44. Requirements Traceability Matrix

### 44.1 Objectives Coverage

| Objective | Covered By |
|---|---|
| OBJ-001 (Reduce input tokens) | Section 11.1 Sanitizer, 11.6 Context Pruner, 11.7 Deduplicator, 11.10 Compressor, 12.1 Query Compressor |
| OBJ-002 (Reduce output tokens) | Section 12.4 Output Length Controller, 12.3 Output Schema Selector, 19.3 AR-003 |
| OBJ-003 (Bound context growth) | Section 11.12 Soft Reset/Budgeter, 15.5 Progressive Compaction, 15.6 Hierarchical Memory |
| OBJ-004 (Reduce redundant propagation) | Section 11.7 Deduplicator, 18.5 Handoff Compression, 15.1 Dependency Graph |
| OBJ-005 (Reduce model calls) | Section 13.4 Cascade, 11.5 Semantic Cache, 11.4 Prompt Cache, 18.6 Early Exit, Section 2 Architectural Axiom |
| OBJ-006 (Reduce tool calls/tokens) | Section 17.1 TE-001, 13.2 Tool Output Filter, 17.2 TE-002, 13.3 Tool Result Cache |
| OBJ-007 (Reuse results) | Section 11.4 Prompt Cache, 11.5 Semantic Cache, 13.3 Tool Result Cache, 22 DA-022 |
| OBJ-008 (Route cheapest capable model) | Section 10.1 Model Router, 13.4 Cascade, 19.1 AR-001 |
| OBJ-009 (Dynamic budgets) | Section 10.3 Reasoning Budget, 11.12 Context Budgeter, 14.3 OI-003 |
| OBJ-010 (Stop agentic workflows) | Section 13.1 Agent Stop Controller, 18.6 AL-006, 18.2 AL-002 |
| OBJ-011 (Auditable ledger) | Section 27 Token and Cost Accounting, 31 Observability |
| OBJ-012 (Org-specific benchmarks) | Section 32 Benchmarking Framework, 34 Rollout Strategy |
| OBJ-013 (Multi-provider) | Section 5.3 Common Representation, 25 Provider Profiles, 33 Deployment |
| OBJ-014 (Security/tenant boundaries) | Section 29 Security Requirements, 11.4/11.5 cache isolation/auth |

**All 14 objectives covered. PASS**

### 44.2 Security Requirements Coverage

| SEC ID | Covered By |
|---|---|
| SEC-001 | Section 12.2 Prompt Assembler preserves security instructions |
| SEC-002 | Section 11.4/11.5 authorization-aware keys |
| SEC-003 | Section 11.4 tenant/workspace isolation |
| SEC-004 | Section 38 data retention + 11.4 "no prohibited caching" |
| SEC-005 | Section 12.1 preserve safety/security + 20.1 Compression Contracts |
| SEC-006 | Section 17.6 TE-006 retain auth/audit fields |
| SEC-007 | Section 11.5 authorization checks + freshness |
| SEC-008 | Section 27 ledger + 31 observability + 15.4 provenance |
| SEC-009 | Section 38 configuration |
| SEC-010 | Section 25 provider profiles |

**All 10 SEC requirements covered. PASS**

### 44.3 NFR Coverage

| NFR ID | Covered By |
|---|---|
| NFR-001 | Section 28 Quality Gates + 20 |
| NFR-002 | Section 29 Security Requirements |
| NFR-003 | Section 29 + 38 |
| NFR-004 | Section 29 + 11.4/11.5 |
| NFR-005 | Section 5.3 + 25 |
| NFR-006 | Section 31 + 27 |
| NFR-007 | Section 30 Failure and Fallback |
| NFR-008 | Section 33 + 31 |
| NFR-009 | Section 14.2 OI-002 |
| NFR-010 | Section 14.1 OI-001 |
| NFR-011 | Section 24 Optimization Order |
| NFR-012 | Section 38 Configuration |
| NFR-013 | Section 32.3 Experimental Validation + 36 |

**All 13 NFRs covered. PASS**

### 44.4 Optimization Domains Coverage

| Domain | Covered By |
|---|---|
| A -- Input Optimization | Section 11.1 Sanitizer, 12.1 Query Compressor |
| B -- Context Optimization | Section 11 T1 series, 15 CL series |
| C -- Retrieval Optimization | Section 11.9 Adaptive Top-K, 11.8 Token-Aware Ranker |
| D -- Prompt Caching and Reuse | Section 11.4 Prompt Cache, 16 CE series |
| E -- Semantic Caching | Section 11.5 Semantic Cache |
| F -- Model Routing | Section 10.1 Model Router, 19.1 AR-001 |
| G -- Reasoning-Budget Optimization | Section 10.3 Reasoning Budget, 19.2 AR-002 |
| H -- Agent Workflow Optimization | Section 18 AL series |
| I -- Tool-Call and Tool-Output Optimization | Section 17 TE series, 13.2/13.3 |
| J -- Output Optimization | Section 12.3/12.4, 19.3 AR-003 |
| K -- Batch/Asynchronous Optimization | Section 13.5 Batch Optimizer |
| L -- Agent / Workflow Optimization | Section 18 AL series, 13.1 Agent Stop |
| M -- Context Lifecycle Optimization | Section 15 CL series |
| N -- Optimization Decision Intelligence | Section 14 OI series |
| O -- Cache Economics and Cache-Aware Assembly | Section 16 CE series |
| P -- Tool Execution Optimization | Section 17 TE series |
| Q -- Outcome-Based Cost Optimization | Section 14.5 OI-005 |
| R -- Inference / Serving Optimization | Section 26 P5 Layer |
| S -- Observability, Governance, and Learning | Section 21 EL series, 31, 27 |

**All 19 optimization domains covered. PASS**

### 44.5 Developer-Agent Module Coverage

All 25 DA modules (DA-001 through DA-025) are specified in Section 22.

**All 25 DA modules present. PASS**

### 44.6 Acceptance Criteria Coverage

All 38 ACs (AC-001 through AC-038) are covered:

AC-001 -> Section 27 ledger; AC-002 -> Section 27.3 cost model; AC-003 -> Section 28 + 30; AC-004 -> Section 25 + 27; AC-005 -> Section 27.1 ledger fields; AC-006 -> Section 11.4; AC-007 -> Section 11.5; AC-008 -> Section 11.9; AC-009 -> Section 11.6 + 11.10; AC-010 -> Section 10.1 + 13.4; AC-011 -> Section 12.3 + 12.4; AC-012 -> Section 13.1 + 18.2; AC-013 -> Section 13.2 + 17.6; AC-014 -> Section 29; AC-015 -> Section 32.3; AC-016 -> Section 30; AC-017 -> Section 32; AC-018 -> Section 43.4; AC-019 -> Section 14.2; AC-020 -> Section 27.2; AC-021 -> Section 14.4; AC-022 -> Section 15.2; AC-023 -> Section 15.3; AC-024 -> Section 15.4; AC-025 -> Section 15.5; AC-026 -> Section 17.3; AC-027 -> Section 17.1; AC-028 -> Section 18.3; AC-029 -> Section 21.1 + 21.2; AC-030 -> Section 21.3; AC-031 -> Section 21.5; AC-032 -> Section 25; AC-033 -> Section 14.5; AC-034 -> Section 20.1; AC-035 -> Section 15.7; AC-036 -> Section 7 three-layer architecture; AC-037 -> Section 14.1 + 31; AC-038 -> Section 30.

**All 38 ACs covered. PASS**

### 44.7 Hardening Pass Objectives Coverage (OBJ-023–035)

| Objective | Covered By |
|---|---|
| OBJ-023 (Operating modes) | Section 47.5 Control Plane Operating Model |
| OBJ-024 (Advisor/enforcer/execution-owner) | Section 47.6 Advisory/Enforcement/Execution-Ownership Boundary |
| OBJ-025 (Self-protection) | Section 47.4 Self-Protection Controller (SPC) |
| OBJ-026 (Verified net economics) | Section 47.4 Net Optimization Economics; Section 27.2 Extended Ledger |
| OBJ-027 (Spend governance) | Section 47.2 Spend Governance Engine (SGE) |
| OBJ-028 (Verifier calibration) | Section 47.4 Verifier Calibration Layer (VCL) |
| OBJ-029 (Data governance) | Section 47.2 Data Governance Engine (DGE) |
| OBJ-030 (Feasibility tiers) | Section 47.10 Coding-Agent Integration Feasibility Tiers |
| OBJ-031 (Memory authority) | Section 47.3 Memory Authority (ESM extension) |
| OBJ-032 (Cross-execution concurrency) | Section 47.3 Cross-Execution Coordinator (XEC) |
| OBJ-033 (Human approval) | Section 47.2 Human Approval Gate (HAG) |
| OBJ-034 (Prompt injection/malicious content) | Section 47.2 Content Integrity Screen (CIS) |
| OBJ-035 (Anti-scope) | Section 47.13 Anti-Scope Boundary |

**All 13 hardening-pass objectives covered. PASS**

### 44.8 Hardening Pass Security Requirements Coverage (SEC-011–016)

| SEC ID | Covered By |
|---|---|
| SEC-011 | Section 47.2 SGE — budget checks evaluated independently of security/authorization/policy |
| SEC-012 | Section 47.2 DGE — classification precedes optimization admission |
| SEC-013 | Section 47.2 DGE — deletion/erasure propagation across cache/memory/ledger/log surfaces |
| SEC-014 | Section 47.2 TMG — tool/MCP identity, schema, and staleness validation |
| SEC-015 | Section 47.2 HAG — human approval for designated consequential actions |
| SEC-016 | Section 47.2 CIS — prompt-injection/content-integrity screening before admission |

**All 6 hardening-pass SEC requirements covered. PASS**

### 44.9 NFR-014 Coverage

| NFR ID | Covered By |
|---|---|
| NFR-014 | Section 47.4 Self-Protection Controller (SPC); Section 47.5 Operating Model latency/cost budgets |

**PASS**

### 44.10 Hardening Pass Acceptance Criteria Coverage (AC-039–053)

AC-039 -> Section 47.5; AC-040 -> Section 47.6; AC-041 -> Section 47.4 SPC; AC-042 -> Section 47.4 Net Optimization Economics; AC-043 -> Section 47.2 SGE; AC-044 -> Section 47.4 VCL; AC-045 -> Section 47.2 DGE; AC-046 -> Section 47.10; AC-047 -> Section 47.3 Memory Authority; AC-048 -> Section 47.2 TMG; AC-049 -> Section 47.3 XEC; AC-050 -> Section 47.2 HAG; AC-051 -> Section 47.2 CIS; AC-052 -> Section 47.16; AC-053 -> Section 47.11.

**All 15 hardening-pass ACs covered. PASS**

### 44.11 Hardening Requirements (H01–H20) Coverage

See Section 47.18 for the complete H01–H20 traceability matrix, mirroring EAIOC-SPEC-001 §42.21.

**20/20 hardening requirements traced. PASS**

### 44.12 Canonical Objective Registry (OBJ-001–035)

**Added by the Final Foundational Document Reconciliation of 2026-09-16 (Issue #2).** This is the single canonical cross-document registry for every objective. It resolves the ambiguity previously tracked as `SOURCE-GAP-ES-03` / `SOURCE-GAP-ARCH-03`: every objective below is mandatory, and every objective has an identifiable statement, Problem Statement anchor, Engineering Specification section, and Architecture section. Full statements remain in Section 3 (this document), EAIOC-SPEC-001 §3/§41.11, and PS §3/§51.13/§52; this table intentionally uses short labels rather than duplicating them.

| ID | Objective (short) | PS Section | ES Section | Arch Section | Status |
|---|---|---|---|---|---|
| OBJ-001 | Reduce input tokens | §3 | §3 | §3 | Mandatory |
| OBJ-002 | Reduce output tokens | §3 | §3 | §3 | Mandatory |
| OBJ-003 | Bound context growth | §3 | §3 | §3 | Mandatory |
| OBJ-004 | Reduce redundant propagation | §3 | §3 | §3 | Mandatory |
| OBJ-005 | Reduce model calls | §3 | §3 | §3 | Mandatory |
| OBJ-006 | Reduce tool calls/tokens | §3 | §3 | §3 | Mandatory |
| OBJ-007 | Reuse prior results safely | §3 | §3 | §3 | Mandatory |
| OBJ-008 | Route to cheapest capable model | §3 | §3 | §3 | Mandatory |
| OBJ-009 | Dynamic token/reasoning budgets | §3 | §3 | §3 | Mandatory |
| OBJ-010 | Stop agentic workflows early | §3 | §3 | §3 | Mandatory |
| OBJ-011 | Auditable token/cost ledger | §3 | §3 | §3 | Mandatory |
| OBJ-012 | Org-specific benchmark evidence | §3 | §3 | §3 | Mandatory |
| OBJ-013 | Multi-provider abstraction | §3 | §3 | §3 | Mandatory |
| OBJ-014 | Preserve security/tenant boundaries | §3 | §3 | §3 | Mandatory |
| OBJ-015 | Versioned mutable execution state | §51.1–51.2, §51.13 | §41.11 | §3, §46.2.1 (ESM) | Mandatory |
| OBJ-016 | Checkpointing/resumability | §51.6, §51.13 | §41.11 | §3, §46.2.4 (CPM) | Mandatory |
| OBJ-017 | Reconcile in-flight mutations | §51.2, 51.5, 51.8, §51.13 | §41.11 | §3, §46.2.5 (RE) | Mandatory |
| OBJ-018 | Logical vs Model-Admitted Context | §51.3, §51.13 | §41.11 | §3, §46.2.2 (CVM) | Mandatory |
| OBJ-019 | Tiered eviction, no silent truncation | §51.4, §51.13 | §41.11 | §3, §46.2.6 (CIG) | Mandatory |
| OBJ-020 | Six interruption causes handled | §51.5, §51.13 | §41.11 | §3, §46.3 | Mandatory |
| OBJ-021 | Stale-result rejection | §51.9, §51.13 | §41.11 | §3, §46.2.11 (SRP) | Mandatory |
| OBJ-022 | Scenario-completeness gate | §51.11, §51.13 | §41.11 | §3, `scenario-matrix.md` | Mandatory |
| OBJ-023 | Operating modes (sync/async/hybrid) | §52.1 | §3, §42.1 | §3, §47.5 | Mandatory |
| OBJ-024 | Advisor/enforcer/execution-owner | §52.2 | §3, §42.2 | §3, §47.6 | Mandatory |
| OBJ-025 | Control Plane self-protection | §52.3 | §3, §42.3 | §3, §47.4.1 (SPC) | Mandatory |
| OBJ-026 | Verified net optimization value | §52.4 | §3, §42.4 | §3, §47.4.2 | Mandatory |
| OBJ-027 | Enterprise spend governance | §52.5 | §3, §42.5 | §3, §47.2.1 (SGE) | Mandatory |
| OBJ-028 | Verifier confidence/calibration | §52.6 | §3, §42.6 | §3, §47.4.3 (VCL) | Mandatory |
| OBJ-029 | Data governance/deletion propagation | §52.7 | §3, §42.7 | §3, §47.2.2 (DGE) | Mandatory |
| OBJ-030 | Coding-agent feasibility tiers | §52.8 | §3, §42.8 | §3, §47.10 (FTR) | Mandatory |
| OBJ-031 | Memory authority | §52.9 | §3, §42.9 | §3, §47.3.1 | Mandatory |
| OBJ-032 | Cross-execution concurrency | §52.12 | §3, §42.12 | §3, §47.3.2 (XEC) | Mandatory |
| OBJ-033 | Human approval for consequential actions | §52.13 | §3, §42.13 | §3, §47.2.4 (HAG) | Mandatory |
| OBJ-034 | Prompt-injection/malicious content screening | §52.14 | §3, §42.14 | §3, §47.2.5 (CIS) | Mandatory |
| OBJ-035 | Anti-scope boundaries | §52.20 | §3, §42.20 | §3, §47.13 | Mandatory |

**All 35 objectives (OBJ-001–035) registered and traced across all three foundational documents. PASS. No objective is orphaned, undefined, or ambiguous as to mandatory status.**

---

## 45. Internal Consistency Check Summary

This architecture document has been cross-referenced against the authoritative problem statement and EAIOC-SPEC-001 Rev 1.1. All consistency checks pass.

| Check | Result |
|---|---|
| All 14 OBJs covered | PASS |
| All 38 ACs covered | PASS |
| All 10 SEC requirements covered | PASS |
| All 25 DA modules specified | PASS |
| All 13 NFRs covered | PASS |
| All 19 optimization domains covered | PASS |
| All 28 anti-patterns documented | PASS |
| Pipeline stages consistent with architecture | PASS |
| Three-layer architecture integrity | PASS |
| Cost model completeness | PASS |
| Fallback for every stage | PASS |
| No requirement silently removed | PASS |
| Open items are design-time decisions | PASS |
| Architectural axiom "no LLM call" traceable and consistent | PASS |

**Every optimization technique has documented:**
- Applicability
- Prerequisites
- Expected benefit
- Optimization overhead
- Quality risks
- Fallback
- Evaluation methodology
- Observability metrics
- Production maturity status

> [!IMPORTANT]
> **ARCHITECTURE INTERNAL CONSISTENCY CHECK: PASSED (14/14)**
>
> All requirements from the problem statement are represented in this architecture document.
> No requirement has been silently removed, simplified, reinterpreted, or replaced.
> Open items are design-time configuration decisions, not specification gaps.
>
> **Implementation must not begin until this architecture document and EAIOC-SPEC-001 Rev 1.1 have been explicitly approved.**

---

### Open Items (Design-Time Decisions, Not Gaps)

| # | Item | Resolution |
|---|---|---|
| 1 | Exact Token-Aware Ranker utility formula | Configurable, benchmark-driven per Section 11.8 |
| 2 | Exact Context ROI formula | Configurable, benchmark-driven per Section 14.4 |
| 3 | Default semantic cache similarity threshold | Configurable per tenant/workflow per Section 38 |
| 4 | Provider-specific cache minimum token counts | Resolved by provider profile abstraction per Section 25 |
| 5 | Exact compaction threshold values | Configurable and model-specific per Section 15.5 |
| 6 | "Where supported" qualifier on DA-007, DA-022, TE-003, TE-004 | Integration-surface-dependent; requirement is valid |
| 7 | Exact "sufficient" definition for agent early exit | Policy-driven, configurable threshold per Section 38 |

---

## 46. Dynamic Execution Architecture

**Amendment:** EAIOC-ARCH-001 Rev 1.1 — Hardening Pass, 2026-09-10
**Traceability:** PS §51; EAIOC-SPEC-001 §41; INTF §42 (INTF-050–062)

> [!IMPORTANT]
> This section is additive. Sections 1–45 and all Open Items are unchanged.
> The 13 new components defined below extend existing components — they do not replace them.
> Implementation may not begin until EAIOC-SPEC-001 Rev 1.2 and EAIOC-ARCH-001 Rev 1.1 are explicitly approved.

---

### 46.1 New Component Overview

| Component ID | Name | Extends / Integrates With |
|---|---|---|
| **ESM** | Execution State Manager | All pipeline stages; new coordinator |
| **CVM** | Context Version Manager | CL-001, CL-002 |
| **WVM** | Workflow Version Manager | AL-001 to AL-006 |
| **CPM** | Checkpoint Manager | ESM, WVM |
| **RE** | Reconciliation Engine | ESM, CVM, WVM |
| **CIG** | Context Integrity Gate | T1.9 Context Pruner |
| **CEC** | Context Expansion Controller | DA-012, CL-004 |
| **PRV** | Permission Revalidation | SEC-001–010 |
| **DPE** | Dynamic Policy Evaluation | OI-001, OI-002 |
| **CAR** | Capability/Availability Resolver | T0.1 Model Router |
| **SRP** | Stale Result Protection | T1.6, T1.7, T3.3 caches |
| **SPM** | Supersession Manager | ESM |
| **RCO** | Recovery Coordinator | CPM, ESM, CAR |

---

### 46.2 Component Specifications

#### 46.2.1 ESM — Execution State Manager

**Purpose:** Own and persist all mutable execution state throughout the lifecycle for every active execution.

**Inputs:** Any pipeline event that mutates context, workflow, policy, permissions, or model state.
**Outputs:** Versioned state snapshots, mutation records, checkpoint records, terminal state records.

**State owned:**
- `execution_id` (immutable UUID, issued at admission)
- `execution_version` (monotonically increasing integer)
- `context_version` (owned by CVM, surfaced via ESM)
- `workflow_version` (owned by WVM, surfaced via ESM)
- `policy_version` (snapshotted at ingress)
- `pipeline_stage_status` (per-stage completion flags)
- `token_ledger` (running balance, per section)
- `reversibility_records` (append-only)
- `quality_gate_results` (per gate, per stage)
- `terminal_state` (COMPLETED | FAILED | CANCELLED | SUPERSEDED | EXPIRED)

**Operations:**
```
snapshot(execution_id) → ExecutionStateSnapshot
apply_mutation(execution_id, mutation) → ExecutionStateSnapshot
checkpoint(execution_id) → CheckpointRecord
reconcile(execution_id) → ReconciliationResult
terminal(execution_id, terminal_state, reason) → AuditRecord
```

**Invariant:** No optimization decision may proceed without calling `reconcile()` first.

---

#### 46.2.2 CVM — Context Version Manager

**Purpose:** Track all mutations to Logical Task Context with versioned, auditable mutation records.

**Inputs:** Context mutations from T1.x stages, DA modules, tool outputs, retrieval results.
**Outputs:** `ContextMutation` records; versioned `LogicalTaskContext` snapshots.

**Requirements:**
- `context_version` increments on every mutation (add, remove, compress, reorder, prune).
- `LogicalTaskContext` is the authoritative context store — persists across all steps.
- `ModelAdmittedContext` is assembled fresh per inference call from `LogicalTaskContext`.
- Pruning of `LogicalTaskContext` requires a `ReversibilityRecord`.
- Assembly-time filtering (for `ModelAdmittedContext`) is non-destructive.

**Tier enforcement:** CVM enforces the Tier 0–4 protection tiers defined in SPEC §41.4. Any eviction that violates Tier 0 or Tier 1 rules returns `EVICTION_PROHIBITED`.

---

#### 46.2.3 WVM — Workflow Version Manager

**Purpose:** Track workflow step graph mutations and maintain idempotency state.

**Inputs:** Step additions, removals, reorderings, and step completion events.
**Outputs:** `WorkflowVersion` records; `completed_actions` set; `unresolved_questions` set.

**Requirements:**
- `workflow_version` increments on every step mutation.
- `completed_actions` is append-only — never modified after a step is recorded as complete.
- `unresolved_questions` is the live driver of agent continuation.

---

#### 46.2.4 CPM — Checkpoint Manager

**Purpose:** Persist and retrieve resumable checkpoints for multi-step executions (≥ 3 steps).

**Inputs:** `CheckpointRequest` (from ESM).
**Outputs:** `CheckpointRecord`; `ResumeDecision`.

**Checkpoint trigger policy:** Checkpoint is written:
- After every 3 completed steps (configurable).
- Before any potentially long-running tool call.
- On any interruption event.
- On explicit client `CheckpointRequest`.

**Resume semantics:** CPM does not resume — it supplies the checkpoint to RCO. RCO executes the resume protocol.

---

#### 46.2.5 RE — Reconciliation Engine

**Purpose:** Verify execution state consistency before each step proceeds.

**Inputs:** `ExecutionStateSnapshot`.
**Outputs:** `ReconciliationResult` (CONSISTENT | MUTATION_DETECTED | STALE_DETECTED | PRECONDITION_FAILED).

**Reconciliation checks:**
1. `context_version` matches CVM current.
2. `policy_version` matches policy store (no restrictive change since ingress).
3. `workflow_version` matches WVM current.
4. `completed_actions` consistency (no step marked both complete and unresolved).
5. `token_ledger` is within budget.
6. Model still available (defers to CAR).

If any check fails: block the next step; surface the specific failure to the caller.

---

#### 46.2.6 CIG — Context Integrity Gate

**Purpose:** Enforce that context leaving the Logical Task Context layer is never silently truncated.

**Inputs:** Pre-assembly context from T1.5 (Context Budgeter).
**Outputs:** `ContextIntegrityResult` (VALID | OVERFLOW_EVICTION_REQUIRED | TIER_VIOLATION).

**Rules:**
- If assembled context fits within budget: VALID.
- If over budget: evaluate eviction tiers; return `OVERFLOW_EVICTION_REQUIRED` with candidates.
- If any proposed eviction violates Tier 0 or Tier 1: return `TIER_VIOLATION` (fail-closed).
- `TIER_VIOLATION` blocks inference until budget is explicitly raised or the caller accepts a PARTIAL result.

---

#### 46.2.7 CEC — Context Expansion Controller

**Purpose:** Handle re-admission of previously evicted context items when found necessary for task completion.

**Inputs:** `ContextExpansionRequest` from any pipeline stage or sub-agent.
**Outputs:** `ContextExpansionResponse` (ADMITTED | FRESHNESS_INVALID | BUDGET_EXCEEDED | SUSPENDED).

**Protocol:**
1. Validate freshness of the requested item (if external).
2. Estimate token cost of re-admission.
3. If cost fits within remaining budget: re-admit and increment `context_version`.
4. If cost exceeds budget: suspend workflow; surface `EXPANSION_BUDGET_EXCEEDED` to caller.
5. If freshness invalid: re-fetch from source before re-admission.

---

#### 46.2.8 PRV — Permission Revalidation

**Purpose:** Revalidate authorization scope when permissions change during execution.

**Inputs:** `PermissionChangeEvent`; current `execution_id`.
**Outputs:** `PermissionValidationResult` (VALID | SCOPE_REDUCED | BLOCKED).

**Requirements:**
- On `SCOPE_REDUCED`: invalidate cache entries for affected user/role across all cache types.
- In-flight tool executions revalidated before results are admitted.
- Agent steps depending on now-inaccessible data marked `BLOCKED` and surfaced.
- Fail-closed: any PRV failure that cannot be resolved blocks the affected step.

---

#### 46.2.9 DPE — Dynamic Policy Evaluation

**Purpose:** Evaluate policy changes against in-flight executions to determine suspend/resume/cancel.

**Inputs:** `PolicyChangeEvent`; `OptimizationPlan.policy_version`.
**Outputs:** `PolicyEvaluationResult` (NO_ACTION | SUSPEND_REQUIRED | CANCEL_REQUIRED).

**Rules:**
- Additive policy changes (more permissive): `NO_ACTION` — takes effect on next request.
- Restrictive optimization policy changes: `NO_ACTION` — pinned policy applies to current execution.
- Restrictive security policy changes (SEC-001–010): `SUSPEND_REQUIRED` → revalidate → resume or `CANCEL_REQUIRED`.

---

#### 46.2.10 CAR — Capability/Availability Resolver

**Purpose:** Extend T0.1 (Model Router) to detect real-time capability and availability changes.

**Inputs:** `CapabilityChangeEvent`; `ModelAvailabilityEvent`; current `ModelPolicy`.
**Outputs:** Updated `ModelAvailability` record; routing decision for failover.

**Requirements:**
- Activates circuit breaker on provider outage.
- Selects the next available model from the configured cascade.
- Validates that the failover model meets context window, tool support, and compliance constraints.
- Updates the running `execution_version` when a model change is made mid-execution.

---

#### 46.2.11 SRP — Stale Result Protection

**Purpose:** Detect and reject stale results at all cache types and at resume.

**Inputs:** Cache hit events (T1.6, T1.7, T3.3); `CheckpointRecord` items at resume.
**Outputs:** `StaleResultEvent` (FRESH | STALE_REJECTED | STALE_REFETCHED).

**Detection methods:**
- Semantic cache: `freshness_valid` flag per entry; time-to-live check.
- Tool result cache: dependency-change event subscription (DA-024).
- Prompt cache: `policy_version` comparison on every hit.
- Resume items: freshness validation against external source timestamp.
- Sub-agent results: context invalidation event from DA-024.

**Action on STALE:** Re-fetch or re-execute; emit `StaleResultEvent` to observability; never serve stale silently.

---

#### 46.2.12 SPM — Supersession Manager

**Purpose:** Handle supersession of in-flight executions by new requests.

**Inputs:** New `AgentRequest` that supersedes an in-flight `execution_id`.
**Outputs:** `SupersessionRecord`; updated audit entries for both executions.

**Protocol:**
1. Mark old execution as `SUPERSEDED`.
2. Finalize old token/cost accounting.
3. Issue new `execution_id` for new execution.
4. Pass `completed_actions` (after freshness validation) to new execution's WVM.
5. Trigger SRP validation on any shared context items before re-use.

---

#### 46.2.13 RCO — Recovery Coordinator

**Purpose:** Execute the resume protocol for interrupted or checkpointed executions.

**Inputs:** `ResumeRequest`; `CheckpointRecord` (from CPM).
**Outputs:** `ResumeDecision` (RESUME | RESULT_ALREADY_AVAILABLE | PRECONDITION_FAILED).

**Resume protocol:**
1. Load checkpoint from CPM.
2. Run PRV to revalidate permissions.
3. Run DPE to revalidate policy.
4. Run CAR to revalidate model availability.
5. Consult WVM `completed_actions` — skip all completed steps.
6. Run SRP on all external context items.
7. Run RE to confirm full state consistency.
8. If all pass: `RESUME` from the first unresolved step.
9. If objective already satisfied: `RESULT_ALREADY_AVAILABLE`.
10. If any step 2–7 fails: `PRECONDITION_FAILED` with specific cause.

**Invariant:** Resume NEVER blindly replays completed steps.

---

### 46.3 Execution State Machine

The ESM enforces the following state transitions. States are terminal once reached unless noted.

```
ADMITTED
  |
  v
PLANNING          (optimization plan assembled)
  |
  v
EXECUTING         (pipeline stages running)
  |  \
  |   \--> SUSPENDED ----+
  |                      | (resume)
  |                      v
  |              RESUMING
  |                      |
  |<---------------------+
  |
  +-------> COMPLETED    (terminal)
  |
  +-------> FAILED       (terminal)
  |
  +-------> CANCELLED    (terminal)
  |
  +-------> SUPERSEDED   (terminal)
  |
  +-------> EXPIRED      (terminal)

SUSPENDED substates:
  SUSPENDED_WAITING_USER        (user cancellation with potential resume)
  SUSPENDED_PROVIDER_UNAVAILABLE (circuit breaker; awaiting failover)
  SUSPENDED_BUDGET_EXCEEDED     (context expansion request pending)
  SUSPENDED_POLICY_REVALIDATION (security policy change; revalidating)
  SUSPENDED_PERMISSION_BLOCKED  (permission scope reduced; awaiting resolution)
```

Valid transitions:
- `ADMITTED` → `PLANNING`
- `PLANNING` → `EXECUTING`
- `EXECUTING` → `SUSPENDED_*`
- `EXECUTING` → `COMPLETED | FAILED | CANCELLED | SUPERSEDED | EXPIRED`
- `SUSPENDED_*` → `RESUMING`
- `RESUMING` → `EXECUTING`
- `RESUMING` → `FAILED | CANCELLED` (if resume preconditions fail)
- All terminal states are final

---

### 46.4 Integration with Existing Components

The new components integrate with existing pipeline stages without modifying them:

| Existing Component | Integration Point | How New Component Extends |
|---|---|---|
| T0.1 Model Router | After routing decision | CAR monitors availability; updates routing if provider fails |
| T1.9 Context Pruner | Before pruning Logical Task Context | CIG validates tier before any eviction |
| T1.5 Context Budgeter | After budget check | CEC handles expansion if eviction is insufficient |
| T1.6 Prompt Cache | On every cache hit | SRP validates freshness before returning hit |
| T1.7 Semantic Cache | On every cache hit | SRP validates freshness before returning hit |
| T3.3 Tool Result Cache | On every cache hit | SRP validates data-source freshness |
| T3.1 Agent Stop Controller | Before loop continuation | WVM `unresolved_questions` feeds continuation signal |
| AL-001 to AL-006 | Agent loop decisions | WVM tracks completed actions; prevents re-execution |
| DA-024 | Cache invalidation | Feeds SRP and CVM with change events |
| OI-001 Decision Engine | Per optimization decision | RE must pass before OI-001 can issue a decision |

---

### 46.5 Deployment Pattern Integration

The 13 new components are deployed as internal services within the Control Plane — not as external APIs. They are invisible to callers unless a condition requires surfacing a structured partial result or a precondition failure.

| Deployment Mode | Dynamic Execution Behavior |
|---|---|
| A — Python SDK | ESM, CVM, WVM, CPM run in-process; state is in-memory with optional persistent backend |
| B — CLI | Same as A; checkpoint files written to local disk |
| C — API / Middleware Service | ESM state is persisted in a shared store; CPM uses durable storage |
| D — Drop-in Prompt Assembly | CIG and CVM run inline; ESM is scoped to single requests |
| E — Agent Orchestration Middleware | Full ESM, CVM, WVM, CPM, RCO deployment with durable state |

---

### 46.6 Consistency Check Update

The following checks extend the Internal Consistency Check Summary (Section 45):

| Check | Status |
|---|---|
| 13 new components are additive — no existing component removed or modified | PASS |
| ESM state machine covers all 5 terminal states | PASS |
| CIG tier enforcement consistent with SPEC §41.4 Tier 0 (immutable) | PASS |
| SRP covers all 5 cache types defined in SPEC §41.9 | PASS |
| RCO resume protocol consistent with SPEC §41.6 | PASS |
| SPM protocol consistent with SPEC §41.7 | PASS |
| DPE policy evaluation consistent with SPEC §41.8 | PASS |
| PRV fail-closed consistent with SPEC §41.10 and SEC-001–010 | PASS |
| CAR extends T0.1 without replacing it | PASS |
| All new components traceable to PS §51 | PASS |

---

## 47. Architecture Hardening Amendment — Operating Model, Governance, and Trust Boundaries (2026-09-15)

**Amendment:** EAIOC-ARCH-001 Rev 1.2 — Hardening Pass, 2026-09-15
**Traceability:** PS §52 (H01–H20); EAIOC-SPEC-001 §42; OBJ-023–035; SEC-011–016; NFR-014; AC-039–053

> [!IMPORTANT]
> This section is additive. Sections 1–46 and all prior Open Items remain unchanged except for the inline notes/table rows called out in Sections 1.6, 3, 4, 5.2, 8, 18.2, 26, 29, 37, 39, 41, and 44.7–44.11 above, each of which cross-references a subsection below rather than altering prior normative text. Nine new/extended components are defined below; none replaces an existing Section 10–22 or Section 46 component. Implementation may not begin until EAIOC-SPEC-001 Rev 1.3 and EAIOC-ARCH-001 Rev 1.2 are explicitly approved.

This section translates the twenty hardening requirements of PS §52 (H01–H20) from engineering behavior (EAIOC-SPEC-001 §42) into architecture: component ownership, plane placement, sync/async operating mode, advisory/enforcement/execution-ownership classification, state, failure behavior, and invariants.

---

### 47.1 New/Extended Component Overview

| Component ID | Name | Plane | Extends / Integrates With |
|---|---|---|---|
| **SGE** | Spend Governance Engine | Governance/Safety | T0.1 Model Router, T0.3 Reasoning Budget, T3.1 Agent Stop Controller |
| **DGE** | Data Governance Engine | Governance/Safety | T1.9 Context Pruner (Tier 0), all cache/memory/ledger/log surfaces |
| **TMG** | Tool/MCP Trust Gate | Governance/Safety | DA-007/DA-008 (tool/MCP selection), TE-001–TE-007 |
| **HAG** | Human Approval Gate | Governance/Safety | ESM (Section 46), T3.1 Agent Stop Controller |
| **CIS** | Content Integrity Screen | Governance/Safety | T1.1 Sanitizer, all retrieval/tool/sub-agent content sources |
| **FTR** | Feasibility Tier Registry | Execution Adapter | Section 5.3 Common Representation Layer, Section 22 DA modules |
| **XEC** | Cross-Execution Coordinator | Execution Truth | RE (Reconciliation Engine), SPM (Supersession Manager), CL-006 |
| **SPC** | Self-Protection Controller | Optimization Intelligence | OI-003 Adaptive Optimization Depth Controller |
| **VCL** | Verifier Calibration Layer | Optimization Intelligence | AR-004 Verifier-Guided Escalation, QO-002 Quality-Aware Fallback |

Placement follows the plane taxonomy implied by PS §52 and used consistently across this section: **Governance/Safety Plane** (decisions that can block or gate independent of optimization value), **Execution Truth Plane** (Section 46's ESM/CVM/WVM state authority, extended here to memory and cross-execution scope), **Optimization Intelligence Plane** (Section 14 OI series, extended here with self-protection and verifier calibration), and **Execution Adapter Plane** (Section 5.3, extended here with the feasibility-tier registry).

---

### 47.2 Governance / Safety Plane Components (H05, H07, H11, H13, H14)

#### 47.2.1 SGE — Spend Governance Engine

**Purpose:** Enforce independently configurable spend limits and runaway-cost protection at tenant, organization, user, and application scope.

**Inputs:** Real-time cost-ledger events (Section 27), configured budget policy per scope.
**Outputs:** `BudgetEvaluationResult` (WITHIN_BUDGET | THROTTLED | HALTED); circuit-breaker activation records.

**Requirements:**
- Each of {tenant, organization, user, application} may define an absolute and/or rate-based limit, independently, with tenant isolation preserved across scopes.
- Runaway-cost detection (cost-acceleration anomaly, e.g. an agent loop or sub-agent fan-out) acts before the configured limit is exhausted, not only after.
- On breach: halt/throttle spend for the affected scope only; other scopes are unaffected.
- T0.1 Model Router, T0.3 Reasoning Budget Controller, and T3.1 Agent Stop Controller may consume SGE's remaining-budget signal as an input.
- On exhaustion mid-execution: identical to Section 46's `SUSPENDED_BUDGET_EXCEEDED` handling — return PARTIAL, remaining budget = 0, per-section consumption breakdown, never silent overspend.

**Invariant (SEC-011):** A budget evaluation is independent of, and never a substitute for, a security/authorization/policy evaluation. `WITHIN_BUDGET` never implies authorized; `HALTED` is never inferred from an authorization failure.

**Failure Behavior:** if SGE itself cannot determine remaining budget, it defaults to the policy-configured safe default (deny or degrade), never to unconstrained spend.

---

#### 47.2.2 DGE — Data Governance Engine

**Purpose:** Classify content sensitivity before optimization admission and propagate retention/deletion decisions across every persistent surface.

**Inputs:** Raw content entering any T0/T1 stage; deletion/erasure requests; retention policy.
**Outputs:** `SensitivityClassification` (SENSITIVE | NON_SENSITIVE | UNKNOWN); `DeletionPropagationReport`.

**Requirements:**
- Classification runs before caching, compression, or retrieval decisions; classification is a Tier 0 (SEC-protected, Section 46.2.2 CVM tier enforcement) concern and is never pruned/compressed/deduplicated out on relevance score or budget pressure.
- Sensitive data at rest and in transit is encrypted per the organization's configured policy.
- Every cache, memory layer (CL-006), ledger, and log/trace carries a configurable retention period; default is the organization's policy, never unbounded.
- On a deletion/erasure request (SEC-013), DGE propagates to every surface it persisted data to (exact/semantic caches, agent/session memory, cost/token ledgers that retain content, logs/traces) and can report which surfaces still hold derived data, so propagation is verifiable rather than merely asserted.
- Where a provider/model profile (Section 25) declares a data-residency constraint, T0.1 routing and T1.6/T1.7 caching honor it.
- No specific regulation (GDPR, HIPAA, PCI-DSS, etc.) is asserted to apply by this document; applicability is established by deployment configuration (`SOURCE-GAP-ARCH-01`, Section 47.18).

**Failure Behavior:** a classification failure (sensitivity cannot be determined) fails closed — content is treated as sensitive by default until classified. This is a security/integrity failure, not an optimization-stage failure, and does not fall back to unoptimized processing of unclassified content.

---

#### 47.2.3 TMG — Tool/MCP Trust Gate

**Purpose:** Authenticate tool/MCP identity and validate capability metadata, schema integrity, and staleness before a tool result is trusted.

**Inputs:** Tool/MCP server identity assertions; tool/function schemas (consumed by DA-008, TE-002); tool call results.
**Outputs:** `ToolTrustResult` (TRUSTED | SCHEMA_STALE | UNAUTHORIZED | QUARANTINED).

**Requirements:**
- A tool/MCP server's identity is authenticated before its capability metadata is trusted.
- Schema changes between calls are treated as a staleness/trust event, not silently accepted.
- Tool call authorization is evaluated independently of TE-001's ROI predictor — cost/token efficiency never substitutes for an authorization check.
- Tool/MCP versions are tracked so a cached tool result (T3.3) or cached schema is invalidated on version change — extends CL-003 Dependency-Aware Cache Invalidation.
- A tool index used for DA-007/TE-003 dynamic discovery is revalidated on a policy-defined interval; discovered availability is not assumed to persist indefinitely.
- A tool result is untrusted content by default and additionally passes through CIS (Section 47.2.5) before admission, in addition to T3.2 Tool Output Filter and TE-006 Tool Result Value Filter.

**Failure Behavior:** an identity/authorization/schema-integrity failure is a security failure and fails closed (the call is refused or the result quarantined) — not an optimization failure that falls back to using the untrusted tool/result anyway.

---

#### 47.2.4 HAG — Human Approval Gate

**Purpose:** Require explicit human approval for a policy/risk-designated subset of consequential or irreversible actions before execution.

**Inputs:** `ApprovalRequest` (action, risk classification, policy/version snapshot); approver identity and decision.
**Outputs:** `ApprovalResult` (APPROVED | DENIED | AWAITING_APPROVAL | EXPIRED).

**Requirements:**
- The designated-action list is a policy decision, configurable per tenant/workflow — not a fixed universal list.
- Not every action is gated: routine, reversible, low-risk actions proceed under the Section 47.6 advisory/enforcement/execution-ownership model without a human-approval gate.
- An action pending approval follows the same suspension handling as Section 46.3's `SUSPENDED_*` states: checkpoint via CPM, return PARTIAL or `AWAITING_APPROVAL`, resume via RCO once approval is granted or denied.
- Approval requests support a configurable timeout; a timeout follows the policy-defined default (deny, or escalate to a different approver) — never silently proceeds as approved.
- Approval/denial is recorded in the audit record (Section 47.16) alongside the decision it gates.

**Failure Behavior:** if the approval mechanism itself is unavailable, the gated action is blocked (fails closed) rather than proceeding without approval.

---

#### 47.2.5 CIS — Content Integrity Screen

**Purpose:** Screen externally-sourced content (RAG chunks, search results, tool/MCP results, sub-agent handoffs) for prompt injection and content-integrity violations before any optimization stage admits, ranks, compresses, caches, or authorizes an action based on it.

**Inputs:** Any content entering the pipeline from a source other than the end user's direct input (already covered by T1.1 Sanitizer).
**Outputs:** `ScreeningResult` (PASS | REJECT | QUARANTINE | SCREENING_UNAVAILABLE).

**Requirements:**
- Screening precedes admission uniformly for RAG chunks, search results, tool outputs, sub-agent handoffs (AL-005), and MCP tool results (TMG, Section 47.2.3) — not only end-user input.
- This ordering constraint takes precedence over Section 47.5's hybrid/precomputed preference: precomputation may speed up the screening mechanism itself, but the screening step may not be skipped or deferred until after admission in the interest of latency.

**Invariant (SEC-016):** content is untrusted by default until CIS returns PASS.

**Failure Behavior:** `SCREENING_UNAVAILABLE` is a security/integrity failure per Section 46.2.8 PRV's fail-closed pattern and the fail-open/fail-closed distinction in `conventions.md` — the content is rejected or quarantined, never silently admitted because the check itself failed.

---

### 47.3 Execution Truth Plane Extensions (H09, H12)

#### 47.3.1 Memory Authority (ESM extension)

**Purpose:** Establish that agent-owned working memory (scratch state, transient observations, local plans) is never authoritative over ESM/CVM/WVM-owned execution state, authorization state, policy state, or context/workflow version, and surface — never silently resolve — a conflict between them.

**Mechanism:** every agent-memory read that could inform a decision affecting execution state carries a `memory_version`/provenance tag, compared against current ESM-owned state before the memory-derived value is trusted. On disagreement (e.g., agent memory says a step is complete but WVM's `completed_actions` says otherwise; agent memory holds a permission grant PRV has since revoked), ESM/CVM/WVM state wins and the disagreement is surfaced, following the same reconciliation model as RCO's resume protocol (Section 46.2.13): re-validate against current authorization, policy, model availability, and completed-actions state before trusting agent-reported memory.

**Failure Behavior:** an unresolvable conflict (ambiguous provenance) defaults to treating agent memory as stale/untrusted for that decision, never as authoritative by default.

**Observability:** memory/execution-truth conflicts are logged as a distinct event type, separate from ordinary SRP stale-cache events (Section 46.2.11).

---

#### 47.3.2 XEC — Cross-Execution Coordinator

**Purpose:** Extend RE (Reconciliation Engine) and SPM (Supersession Manager) from single-execution reconciliation to concurrent mutation, staleness, and version-conflict detection across multiple agents, sub-agents, or workflow executions sharing a resource (files, tickets, shared memory per CL-006, external systems).

**Inputs:** Concurrent read/write events against a shared resource carrying a version or equivalent conflict-detection token.
**Outputs:** `ConcurrencyConflictResult` (NO_CONFLICT | STALE_SNAPSHOT | VERSION_CONFLICT | RECONCILED | LOCK_REQUIRED).

**Requirements:**
- When two executions mutate the same resource, the second write does not silently overwrite context the first write depended on — XEC raises a conflict.
- A decision made from a shared-state snapshot that has since changed is a stale-result case under SRP (Section 46.2.11) and is revalidated before another execution relies on it.
- An execution acting on an outdated shared-resource version reconciles before proceeding, using RE's reconcile-not-replay discipline (Section 46.2.5); where two executions' completed actions overlap, XEC reconciles to a single consistent outcome rather than applying both.
- Locking/coordination is required only where the shared resource/action is non-idempotent and concurrently reachable — not universally.
- Non-idempotent operations are never blindly replayed across executions, extending RCO's resume-time invariant (Section 46.2.13) to cross-execution scope.

**Failure Behavior:** an unresolvable conflict (coordination unavailable) blocks the losing execution's conflicting write/action and surfaces a conflict result — it does not proceed with an unreconciled dual-write.

---

### 47.4 Optimization Intelligence Plane Extensions (H03, H04, H06)

#### 47.4.1 SPC — Self-Protection Controller

**Purpose:** Extend OI-003 (Adaptive Optimization Depth Controller) so the Control Plane enforces its own latency and processing/cost budgets, detects its own overload, and sheds optimization depth before correctness or security.

**Requirements:**
- **Latency budget:** every SYNC/HYBRID decision (Section 47.5) has a configurable maximum added latency; on exhaustion the stage fails open (falls back to the unoptimized path) rather than blocking the request.
- **Processing/cost budget:** Control Plane compute and provider-API usage (e.g., an LLM-based compressor/classifier) is metered and bounded per request and per tenant; this consumption is the `Optimization Compute Cost` term in Section 47.4.3's net-benefit accounting.
- **Backpressure:** under sustained overload, OI-003 steps the policy tier down to LOW even for requests that would otherwise warrant MEDIUM/HIGH optimization depth.
- **Overload detection:** queue depth, latency percentile, and error rate are monitored; detected overload sheds optional stages, never silently degrades quality or security checks.
- **Deterministic safe fallback:** when SPC itself cannot complete processing, the fallback is the same deterministic path as Section 30's failure/fallback model, extended to Control-Plane self-failure (not only individual optimization-stage failure).

**Failure Behavior (precedence rule):** self-protection failures are optimization failures (fail-open) *unless* the overload condition would otherwise cause a security/authorization/PII check (SGE, DGE, TMG, HAG, CIS) to be skipped — in that specific case the affected request fails closed rather than proceeding unchecked under load.

**Traceability:** OBJ-025; NFR-014; AC-041.

---

#### 47.4.2 Net Optimization Economics (accounting extension, not a new component)

Extends Section 27's ledger and OI-002's Cost-of-Optimization Controller. Net Optimization Value must account for:

| Category | Terms |
|---|---|
| Benefit | Tokens saved, model/inference cost saved |
| Overhead | Optimization compute overhead (SPC), retrieval cost, routing cost, cache overhead (write/storage/invalidation, CE-001) |
| Cost | Additional latency from the optimization itself, retries/escalation cost, downstream/tool cost where the optimization changes tool usage |
| Risk | Quality degradation weighted by the applicable quality gate (Section 28), task failure/regression attributable to the optimization |

A technique is counted as a saving only when this full accounting is net-positive and quality gates (Section 28) pass. Where a term cannot be measured, the result is marked `UNVERIFIED` per AC-002 and excluded from reported savings — never assumed favorable. This is a conceptual/heuristic accounting framework (Section 4's amendment), not a literal per-request constraint-solver computation; individual net-value estimates may be produced synchronously, asynchronously, or via precomputed heuristics per Section 47.5.

**Traceability:** OBJ-026; AC-042.

---

#### 47.4.3 VCL — Verifier Calibration Layer

**Purpose:** Extend AR-004 (Verifier-Guided Escalation) and QO-002 (Quality-Aware Fallback) so every verifier exposes a confidence signal, not only binary pass/fail, and that signal is calibrated before it is trusted to gate an optimization decision.

**Requirements:**
- Deterministic verifiers (unit tests, schema/type checks, static analysis) are higher-confidence than probabilistic verifiers (LLM-judge semantic-equivalence checks); acceptance thresholds reflect that difference.
- Calibration against a benchmark set is required before a probabilistic verifier gates an optimization decision (e.g., accepting compressed context as semantically equivalent, or accepting a cascade's low-cost-model output); Section 42's maturity model (LEVEL 0–5) applies to verifiers themselves, not only the techniques they gate.
- Verifier acceptance behavior is monitored over time; a verifier that starts accepting results it previously rejected (or vice versa) without a corresponding technique change is a drift signal requiring investigation — extends EL-005 to verifier drift specifically.
- Acceptance thresholds are configurable per task/intent/tenant (Section 38).

**Failure Behavior:** below threshold, QO-002's fallback applies (restore previous representation, increase context/reasoning budget, escalate model, or disable the offending optimization) — never silent acceptance.

**Traceability:** OBJ-028; AC-044.

---

### 47.5 Control Plane Operating Model (H01)

Every optimization/decision-class component declares exactly one of three operating modes. This is a per-decision-type declaration, not a single global setting.

| Mode | Applies when | Example components |
|---|---|---|
| **SYNC** | Decision depends on request-time-only information (actual prompt, actual retrieved context, actual permission state) and cannot be safely precomputed | T1.1 Sanitizer, T1.9/T1.10 assembly-time pruning/dedup, T3.1 Agent Stop Controller, CIS |
| **ASYNC** | Underlying information changes slowly relative to request volume | Section 25 provider/model profiles, DA-003 repository map, CE-004 cache pre-warming, EL-005 regression detector |
| **HYBRID** | A precomputed/cached artifact is consulted synchronously, revalidated or recomputed inline when a freshness check (SRP, Section 46.2.11) or confidence check (VCL) indicates it may be stale or below threshold | T1.6/T1.7 caches, CE-003 cache fragmentation detector, AR-004 verifier-guided escalation |

**Requirements:**
- Section 8's pipeline diagram and Section 9's developer-agent pipeline describe a logical decision sequence; they do not mandate synchronous execution for every stage on every request.
- Mode assignment is itself a policy-driven decision subject to the same net-value accounting as any other optimization choice (Section 47.4.2); choosing SYNC where HYBRID would achieve equivalent safety/quality at lower latency/cost is an instance of the "expensive optimizer" anti-pattern (Section 41).
- Control Plane decision latency and compute overhead are themselves part of the optimization problem, not externalities (SPC, Section 47.4.1).
- Precomputed/cached decisions are subject to the same staleness, versioning, and reconciliation requirements as any other cached artifact (Section 46.2.5, 46.2.11).

**Failure Behavior:** if a HYBRID component's synchronous validation step cannot complete within its SPC latency budget, the component fails open to SYNC recomputation or the unoptimized path — never to an unvalidated precomputed result.

**Traceability:** OBJ-023; AC-039.

---

### 47.6 Advisory, Enforcement, and Execution-Ownership Boundary (H02)

Every Control-Plane decision or side effect is classified as exactly one of three categories, declared rather than inferred, and recorded independently per decision.

| Category | Definition | Who performs the resulting action |
|---|---|---|
| **ADVISORY** | Recommends a course of action (model choice, context reduction, cache reuse, stop/continue signal) | Calling agent/application/workflow decides and acts; Control Plane owns no side effect |
| **ENFORCEMENT** | Blocks, requires, rewrites, or gates within policy authority (security rejection, output-budget cap, unauthorized-context refusal) | Calling agent/application still performs the (now-constrained) action |
| **EXECUTION-OWNERSHIP** | Control Plane itself performs an action with an external side effect (cache write, TE-004 programmatic tool execution, CPM checkpoint commit) | Control Plane is directly responsible for correctness/reversibility |

**Requirements:**
- Task planning, business logic, and the decision to perform an irreversible external action belong to the Agent/Workflow/External System by default; the Control Plane advises or enforces around that planning and does not silently assume ownership of it.
- A component crossing from ADVISORY/ENFORCEMENT into EXECUTION-OWNERSHIP inherits CL-004's reversibility requirements and Section 47.16's audit requirements for that action.
- A single request may pass through all three categories at different pipeline stages (enforcement at CIS/TMG, advisory at T0.1 model routing, execution-ownership at a T3.3 tool-result cache write); each is recorded independently.
- If a component's ownership category cannot be determined at configuration time, it defaults to ADVISORY (least authority) until explicitly declared otherwise — the system never defaults to EXECUTION-OWNERSHIP by omission.

**Traceability:** OBJ-024; AC-040.

---

### 47.7 Reflection and Loop Awareness (H10)

See the direct amendment to Section 18.2 (AL-002). AL-001's per-iteration signals (state change, objective progress, information gain, errors introduced/resolved) feed a classifier distinguishing productive progress, reflection/self-correction, justified retry, redundant work, oscillation, and failure loop. T3.1 (Agent Stop Controller) and AL-006 (Early Exit) consult the same classifier output for the STOP decision that AL-002 consults for the CONTINUE/flag decision — one expected-value model, not two independent ones. If the classifier cannot produce a confident category, the iteration is treated as unclassified/continuing under existing SGE/SPC budget constraints, rather than force-stopped or force-continued on an unsupported classification.

---

### 47.8 Layer 3 Boundary: Integration, Not Implementation (H17)

See the direct amendment to Section 26. For each Layer 3 capability, the Control Plane's own responsibility is limited to: (a) detecting whether the provider/infrastructure exposes it (Section 25 profile), (b) routing/selecting to take advantage of it when net-beneficial and policy-compliant (Section 47.4.2), (c) negotiating capability parameters through the provider adapter, and (d) measuring the effect separately from Layer 1/2 measurement (AC-036). If a Layer 3 capability is unavailable or negotiation fails, the request proceeds without it — Layer 3 unavailability never blocks a request.

---

### 47.9 Token, Context, Cache, and Inference Optimization: Ownership Boundaries (H18)

| Category | Sections | Ownership |
|---|---|---|
| Prompt/input token optimization | 11.1 Sanitizer, 12.1 Query Compressor, 11.3 Entity Extraction | Owned and implemented directly |
| Output/reasoning token optimization | 10.3 Reasoning Budget, 12.4 Output Length Controller, 12.3 Output Schema Selector | Owned and implemented directly |
| Context optimization | Sections 11, 15 (Layer 2) | Owned and implemented directly |
| Cache optimization | 11.4, 11.5, 13.3, Section 16 | Owned and implemented directly, built on provider-exposed cache primitives where available |
| Model/provider routing | 10.1, 13.4, 19.1 | Owned and implemented directly; execution occurs on provider infrastructure |
| Inference-runtime optimization | Section 26 (Layer 3) | Awareness, integration, routing, policy only (Section 47.8) |

Measurement pipelines (Section 27) attribute a saving or regression to exactly one of these six categories; a measurement that conflates categories (e.g., reporting a KV-cache provider improvement as a context-optimization saving) is invalid per AC-036.

---

### 47.10 Coding-Agent Integration Feasibility Tiers (H08)

**FTR — Feasibility Tier Registry** extends Section 5.3's Common Representation Layer with a per-integration manifest declaring tier and reachable module set.

| Tier | Name | Access Characteristics |
|---|---|---|
| 1 | Deep/Native | Embedded/tightly coupled; full model-bound context observable/transformable pre-inference |
| 2 | Gateway/Interception | API/LLM gateway proxy boundary (Integration Mode 1); request/response observable/transformable at that boundary |
| 3 | Plugin/Extension | Runs inside the platform's own extensibility surface; access bounded by that surface |
| 4 | Protocol/Tool-Level | Participates as an MCP server/tool; optimizes only what passes through that protocol surface |
| 5 | Advisory/Observability-Only | Observes only exposed telemetry; can advise, cannot transform/block |

**Requirements:**
- Every coding-agent integration (Cursor, Claude Code, GitHub Copilot, Antigravity, Codex, and comparable platforms) declares which tier applies; the declared tier bounds which of Section 22's DA-001–DA-025 modules can actually be applied.
- The exact per-platform mechanism to achieve a given tier is left as an architecture decision (Section 47.18).
- The framework does not report or imply full-pipeline optimization coverage for a platform where only Tier 3–5 access exists.

**Failure Behavior:** if a platform's actual access degrades below its declared tier at runtime, FTR requires the integration to re-declare a lower tier rather than silently continuing to claim the higher one.

**Traceability:** OBJ-030; AC-046.

---

### 47.11 Execution State Portability (H16)

Extends Section 46.2.4 (CPM) and 46.2.13 (RCO). The checkpoint schema (`execution_id`, `execution_version`, `context_version`, `workflow_version`, `completed_actions`, `unresolved_questions`, `token_ledger_snapshot`, `policy_version`, `model_selected`, `reversibility_records`) is provider/session-neutral: no field is, or requires, a specific provider's session object or proprietary state format. A checkpoint produced under one model/provider selection remains interpretable for RCO's reconciliation steps even if `model_selected` changes on resume (e.g., CAR-driven failover, Section 46.2.10). A provider's native resumability (e.g., a conversation/session ID) may be used as an optimization to avoid re-transmitting cached context, but resume correctness never depends on it being present. Failure behavior is identical to RCO's existing `PRECONDITION_FAILED` path (Section 46.2.13).

**Traceability:** AC-053.

---

### 47.12 Research Claims as Evidence, Not Guarantees (H19)

Reaffirmation only; no new architectural mechanism. This principle is already established in Sections 1.4/2, 32, 42, and 43. No benchmark percentage from any cited source (LLMLingua, LongLLMLingua, LLMLingua-2, RouteLLM, FrugalGPT, or any future citation) may be hard-coded as an expected production result; Section 42's maturity model (LEVEL 0 RESEARCH → LEVEL 5 AUTO-TUNED) remains the required path from citation to production claim.

---

### 47.13 Anti-Scope Boundary (H20)

See the direct amendment to Section 41. The Control Plane is not a replacement agent orchestrator, model-training system, model-runtime infrastructure platform, provider infrastructure, general-purpose developer IDE, or coding-agent UX replacement; it integrates with these systems through the Section 5 adapter/integration-mode layer and Section 47.10's feasibility-tier model. Any engineering proposal that would require the Control Plane to reimplement one of these adjacent systems rather than integrate with it is out of scope and must be flagged at design-review time, not built silently.

**Traceability:** OBJ-035.

---

### 47.14 Architectural Invariants Addendum

These invariants are introduced or sharpened by the 2026-09-15 hardening pass. They supplement, and do not replace, the fail-open/fail-closed distinction already governing Sections 30 and 46.

1. A budget decision (SGE) never substitutes for a security/authorization/policy decision, and vice versa (SEC-011).
2. Sensitive content is classified before it is admitted to any optimization stage; an unclassified item is treated as sensitive by default (SEC-012, DGE).
3. Externally-sourced content is untrusted until CIS returns PASS; a screening failure fails closed, never open (SEC-016).
4. Tool/MCP identity and schema integrity are validated before a tool result is trusted, independent of the call's cost efficiency (SEC-014, TMG).
5. Agent-owned working memory is never authoritative over ESM/CVM/WVM-owned execution truth; disagreement is surfaced, never silently resolved in memory's favor (OBJ-031).
6. Non-idempotent operations are never blindly replayed, within a single execution's resume path or across concurrent executions (XEC, RCO).
7. A human-approval gate, once configured for an action class, cannot be bypassed by an optimization decision, a budget decision, or an unavailable approval mechanism (fails closed) (SEC-015, HAG).
8. Control Plane self-protection (SPC) sheds optimization depth before it ever silently skips a security/authorization/PII check under load.
9. Every decision's advisory/enforcement/execution-ownership category is recorded; an undeclared category defaults to the least-authority category (ADVISORY), never to execution-ownership by omission.
10. A verifier's pass/fail output is never treated as ground truth without a calibrated confidence score and threshold (VCL).

---

### 47.15 Integration with Existing Components

| Existing Component | Integration Point | How New Component Extends |
|---|---|---|
| T0.1 Model Router | Before routing decision | SGE remaining-budget signal; DGE residency constraint |
| T0.3 Reasoning Budget Controller | Before budget allocation | SGE remaining-budget signal |
| T1.1 Sanitizer | Before admission | CIS screens non-user-input content sources in parallel |
| T1.9 Context Pruner | Before eviction | DGE Tier-0 sensitivity classification precedes CIG tier check (Section 46.2.6) |
| T3.1 Agent Stop Controller | Before continue/stop | HAG pending-approval state; AL-002/AL-006 loop classification (Section 47.7) |
| DA-007/DA-008 | Tool/MCP selection | TMG identity/schema/staleness validation precedes selection |
| TE-001 through TE-007 | Tool execution | TMG authorization check independent of TE-001 ROI signal |
| AR-004 Verifier-Guided Escalation | Verification step | VCL supplies calibrated confidence, not raw pass/fail |
| OI-003 Adaptive Optimization Depth Controller | Depth selection | SPC backpressure can force LOW tier under overload |
| ESM (Section 46.2.1) | State reads | Memory Authority conflict check (Section 47.3.1) before trusting agent memory |
| RE / SPM (Section 46.2.5 / 46.2.12) | Reconciliation | XEC extends reconciliation to cross-execution shared resources |
| CPM / RCO (Section 46.2.4 / 46.2.13) | Checkpoint/resume | Execution State Portability constraint (Section 47.11) |
| Section 5.3 Common Representation Layer | Adapter configuration | FTR feasibility-tier manifest |

---

### 47.16 Decision Explainability and Audit (H15)

Extends Section 27 (Ledger) and Section 31 (Observability and Governance). The following decision categories must produce a retrievable explanation, not merely a best-effort one:

- Why context was admitted or pruned (T1.9/T1.10; CL-004 provenance)
- Why a model/provider was selected (T0.1, AR-001)
- Why a cache entry was reused or rejected (T1.6/T1.7; SRP rejection, Section 46.2.11)
- Why an optimization stage was skipped (OI-002, OI-003/SPC)
- Why an execution was blocked, recovered, or superseded (Section 46.3)
- Why a fallback occurred (Section 30)

Each logged decision includes: the inputs considered, the rule/model/threshold applied, the resulting action, and the Section 47.6 ownership category — sufficient to reconstruct the reasoning during audit/debugging, consistent with SEC-008. This is a retrievability requirement, not a UI/presentation requirement; surfacing an explanation to an end user by default is a separate interface decision. A failure to write a required explanation/audit record is handled as fail-closed (the optimization stage is blocked and unoptimized content is used) per Section 46.2.8's PRV pattern — an unexplainable decision must not silently execute.

**Traceability:** NFR-010 (strengthened); AC-052.

---

### 47.17 Consistency Check Update (Rev 1.2)

The following checks extend the Internal Consistency Check Summary (Section 45) and Section 46.6:

| Check | Status |
|---|---|
| 9 new/extended components (SGE, DGE, TMG, HAG, CIS, FTR, XEC, SPC, VCL) are additive — no existing component removed or modified | PASS |
| H01–H20 each traced to a Section 47 subsection | PASS (Section 47.18) |
| OBJ-023–035 traceable to PS §52 and merged into Section 3 | PASS |
| SEC-011–016 traceable to PS §52 and merged into Section 29 | PASS |
| NFR-014 traceable to PS §52.3 and merged into Section 37 | PASS |
| AC-039–053 traceable to PS §52.21 and merged into Section 39 | PASS |
| Section 26 (Layer 3) reframed consistent with H17; no implementation-ownership claim remains | PASS |
| Section 4 (Objective Function) reframed as conceptual/heuristic consistent with H04 | PASS |
| Section 8 pipeline diagram annotated consistent with H01 (no synchronous-everywhere mandate) | PASS |
| Section 5.2 (Integration Modes) annotated consistent with H08 feasibility tiers | PASS |
| Section 18.2 (AL-002) annotated consistent with H10 | PASS |
| Section 1.7, 41 amended consistent with H02/H18/H20 | PASS |
| SGE independence from security/authorization/policy (SEC-011) does not weaken any Section 29 requirement | PASS |
| DGE Tier-0 classification does not conflict with Section 46.2.2 CVM tier enforcement | PASS |
| CIS ordering requirement does not conflict with Section 47.5's hybrid/precomputed preference (precomputation may speed up screening, never skip it) | PASS |
| Memory Authority (47.3.1) restates rather than contradicts Section 46's ESM/CVM/WVM authority model | PASS |
| XEC extends RE/SPM without altering single-execution reconciliation semantics (Section 46.2.5, 46.2.12) | PASS |
| No SOURCE-CONTRADICTION between PS §52 and Sections 1–46 left unresolved | PASS |
| No existing requirement (OBJ-001–014, AC-001–038, SEC-001–010, NFR-001–013, the Section 46 component set) modified in meaning, only annotated/strengthened where explicitly cited | PASS |
| Open Items list (Section 45) remains valid; no item resolved or invalidated by this pass | PASS |

> [!IMPORTANT]
> **ARCHITECTURE INTERNAL CONSISTENCY CHECK (REV 1.2): PASSED (20/20 new checks; 14/14 baseline checks and 10/10 Rev 1.1 checks remain valid)**
>
> All twenty hardening requirements (H01–H20) from the 2026-09-15 Architecture Hardening Pass are represented in this architecture document, traced to the hardened Problem Statement (PS §52) and EAIOC-SPEC-001 §42, and introduce no unresolved contradiction with Sections 1–46. Three `SOURCE-GAP` items are recorded (Section 47.18) and preserved for controlled resolution in later documents, not silently resolved here.
>
> **Implementation must not begin until this architecture document and EAIOC-SPEC-001 Rev 1.3 have been explicitly approved.**

---

### 47.18 Hardening Traceability Matrix and Known Source Gaps

| Hardening | Architecture Coverage | Source Anchor |
|---|---|---|
| H01 | Section 47.5 — Control Plane Operating Model | PS §52.1 / OBJ-023 / AC-039 |
| H02 | Section 47.6 — Advisory/Enforcement/Execution-Ownership Boundary | PS §52.2 / OBJ-024 / AC-040 |
| H03 | Section 47.4.1 — Self-Protection Controller (SPC) | PS §52.3 / OBJ-025 / NFR-014 / AC-041 |
| H04 | Section 47.4.2 — Net Optimization Economics | PS §52.4 / OBJ-026 / AC-042 |
| H05 | Section 47.2.1 — Spend Governance Engine (SGE) | PS §52.5 / OBJ-027 / SEC-011 / AC-043 |
| H06 | Section 47.4.3 — Verifier Calibration Layer (VCL) | PS §52.6 / OBJ-028 / AC-044 |
| H07 | Section 47.2.2 — Data Governance Engine (DGE) | PS §52.7 / OBJ-029 / SEC-012 / SEC-013 / AC-045 |
| H08 | Section 47.10 — Feasibility Tier Registry (FTR) | PS §52.8 / OBJ-030 / AC-046 |
| H09 | Section 47.3.1 — Memory Authority (ESM extension) | PS §52.9 / OBJ-031 / AC-047 |
| H10 | Section 47.7 — Reflection and Loop Awareness (+ Section 18.2 amendment) | PS §52.10 / AL-002 |
| H11 | Section 47.2.3 — Tool/MCP Trust Gate (TMG) | PS §52.11 / SEC-014 / AC-048 |
| H12 | Section 47.3.2 — Cross-Execution Coordinator (XEC) | PS §52.12 / OBJ-032 / AC-049 |
| H13 | Section 47.2.4 — Human Approval Gate (HAG) | PS §52.13 / OBJ-033 / SEC-015 / AC-050 |
| H14 | Section 47.2.5 — Content Integrity Screen (CIS) | PS §52.14 / OBJ-034 / SEC-016 / AC-051 |
| H15 | Section 47.16 — Decision Explainability and Audit (+ NFR-010 amendment) | PS §52.15 / NFR-010 / AC-052 |
| H16 | Section 47.11 — Execution State Portability | PS §52.16 / AC-053 |
| H17 | Section 47.8 — Layer 3 Boundary (+ Section 26 amendment) | PS §52.17 |
| H18 | Section 47.9 — Ownership Boundaries (+ Section 1.7 amendment) | PS §52.18 |
| H19 | Section 47.12 — Research Claims Reaffirmation | PS §52.19 |
| H20 | Section 47.13 — Anti-Scope Boundary (+ Section 41 amendment) | PS §52.20 / OBJ-035 |

**Result:** 20/20 hardening requirements (H01–H20) traced. 13/13 new objectives (OBJ-023–035) traced. 6/6 new security requirements (SEC-011–016) traced. 1/1 new NFR (NFR-014) traced. 15/15 new acceptance criteria (AC-039–053) traced.

**Known Source Gaps (reclassified 2026-09-16, Final Foundational Document Reconciliation):**

| ID | Gap | Classification | Disposition |
|---|---|---|---|
| `SOURCE-GAP-ARCH-01` | PS §52.7 (H07) deliberately does not assert any specific regulatory regime (GDPR/HIPAA/PCI-DSS) applies; this document does not invent one. | DEPLOYMENT-SPECIFIC / CONFIGURATION-SPECIFIC | Preserved as an open scope question for deployment configuration; mirrors EAIOC-SPEC-001 `SOURCE-GAP-ES-01`. |
| `SOURCE-GAP-ARCH-02` | The exact mechanism for CIS (prompt-injection/content-integrity screening, H14) and for achieving each FTR feasibility tier per platform (H08) is left as a downstream architecture decision, consistent with PS §52.11/§52.14/§52.8 not prescribing a mechanism. | DEFERRED TO DOWNSTREAM DOCUMENT | Deferred to `optimization-catalog.md` / `provider-matrix.md` / `security.md` (not yet generated per the controlled document chain). Mirrors EAIOC-SPEC-001 `SOURCE-GAP-ES-04`. |
| `SOURCE-GAP-ARCH-03` | OBJ-015–022 (2026-09-10 hardening pass, PS §51) were represented structurally in Section 46 but were not restated as a table in Section 3, and PS §51 did not literally label them with OBJ IDs. | **RESOLVED (2026-09-16)** | OBJ-015–022 are now tabled in Section 3, canonically labeled at the source in PS §51.13, and cross-document traceability is maintained in the consolidated registry at Section 44.12. Mirrors resolution of EAIOC-SPEC-001 `SOURCE-GAP-ES-03`. |

No `SOURCE-CONTRADICTION` was found between PS §52 (H01–H20) and the pre-existing Sections 1–46 of this architecture document; every apparent tension (Section 26 vs. H17, Section 4 vs. H04, Section 8 vs. H01, Section 5.2 vs. H08, Section 47.5's hybrid preference vs. H14's screening-ordering requirement) was resolved by direct amendment or explicit precedence rule at the point of tension.

---

## 48. Final Foundational Document Reconciliation (2026-09-16)

**Amendment:** EAIOC-ARCH-001 Rev 1.3 — Final Foundational Document Reconciliation
**Traceability:** Cross-document reconciliation of `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt`, EAIOC-SPEC-001 Rev 1.4, and this document.

> [!IMPORTANT]
> This section is editorial/traceability-only. No requirement (OBJ, SEC, NFR, AC, H01–H20) was added, removed, weakened, or reinterpreted. No downstream document (`interfaces.md`, `conventions.md`, `edge-cases.md`, `scenario-matrix.md`, or any later document) was read for modification purposes beyond terminology/ID consistency checks, and none was modified.

### 48.1 Changes Made in This Pass

| Issue | Change | Sections Affected |
|---|---|---|
| #2 — OBJ-015–022 traceability | Added the OBJ-015–022 table to Section 3; added the Section 44.12 canonical OBJ-001–035 registry; resolved `SOURCE-GAP-ARCH-03` | Section 3, Section 44.12, Section 47.18 |
| #3 — Architecture scope boundary | Added Section 1.6, stating what this document owns vs. what each downstream document owns, and the architectural-requirement-vs-implementation-detail test | Section 1.6 (new); Section 1's former 1.6 renumbered to 1.7 |
| #4 — Metadata consistency | Header/footer revision bumped to Rev 1.3; Engineering Spec cross-reference bumped to Rev 1.4; amendment history extended | Document header, this section, footer |
| Cross-reference audit | Verified no stale "Section 42.x"-style or duplicate-ID issues exist in this document (none found — see Section 48.2) | N/A (verification only) |

### 48.2 Cross-Reference Audit Result

A document-wide search for stale/broken subsection references (the pattern that produced the Engineering Specification's "Section 42.26" defect) found **no equivalent defect in this document**: every `Section NN.N` reference checked against this document's actual headings resolves to a real subsection. The Engineering Specification's own defect (a "Section 42.26" reference where the actual maximum was Section 42.23, and a stray "Section 42.25/25" reference) was corrected in EAIOC-SPEC-001 directly (Rev 1.4, Section 42.24) — not in this document, since this document does not reproduce that numbering.

### 48.3 Consistency Check Update (Rev 1.3)

| Check | Status |
|---|---|
| Section 1.6 (scope boundary) does not relocate or delete any existing content | PASS |
| Section 1's former 1.6 correctly renumbered to 1.7, with both internal cross-references (Section 44.7's coverage row, Section 47.18's H18 row) updated | PASS |
| OBJ-015–022 now tabled in Section 3, consistent with EAIOC-SPEC-001 §41.11's note and PS §51.13 | PASS |
| Section 44.12 registry covers all 35 objectives with PS/ES/Arch section references | PASS |
| `SOURCE-GAP-ARCH-03` correctly marked RESOLVED with a citable resolution section; `SOURCE-GAP-ARCH-01`/`02` correctly reclassified without being closed | PASS |
| No new component, plane, or requirement introduced | PASS |
| No existing component, plane, or requirement removed or contradicted | PASS |
| Header/footer/TOC revision references internally consistent (Rev 1.3; ES cross-reference Rev 1.4) | PASS |

> [!IMPORTANT]
> **ARCHITECTURE INTERNAL CONSISTENCY CHECK (REV 1.3): PASSED (8/8 new checks; all prior baseline, Rev 1.1, and Rev 1.2 checks remain valid)**
>
> This reconciliation pass resolves `SOURCE-GAP-ARCH-03`, adds an explicit architectural scope boundary (Section 1.6), and establishes the canonical OBJ-001–035 registry (Section 44.12). It introduces no new normative requirement and removes none.
>
> **Implementation must not begin until this architecture document and EAIOC-SPEC-001 Rev 1.4 have been explicitly approved.**

---

*End of Architecture Document -- EAIOC-ARCH-001 Rev 1.3*
*Source authority: `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt` (as hardened 2026-09-15, PS §52; reconciled 2026-09-16, PS §51.13)*
*Engineering specification cross-reference: EAIOC-SPEC-001 Rev 1.4*
