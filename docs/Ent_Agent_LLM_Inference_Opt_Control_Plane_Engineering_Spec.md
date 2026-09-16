# Engineering Specification
# Enterprise Agent & LLM Inference Optimization Control Plane

---

**Document ID:** EAIOC-SPEC-001  
**Revision:** 1.4 — Final Foundational Document Reconciliation  
**Status:** PRE-APPROVAL — Do Not Implement  
**Source:** `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt` (as hardened 2026-09-15, PS §52, reconciled 2026-09-16, PS §51.13)  
**Date:** 2026-09-08  
**Amendment history:**
- 2026-09-08 — Section 1.3 updated with Central design & final architectural principle (Rev 1.1).
- 2026-09-10 — Section 41 added: Dynamic Execution State & Context Lifecycle Management hardening pass, OBJ-015–022 (Rev 1.2).
- 2026-09-15 — Section 42 added: Operating Model, Governance, and Trust Boundaries hardening pass, propagating PS §52 (H01–H20), OBJ-023–035, SEC-011–016, NFR-014, AC-039–053 (Rev 1.3).
- 2026-09-16 — Final Foundational Document Reconciliation: corrected stale "Section 42.26" references (now Section 42.22/42.23) and a stale "Section 42.25/25" reference (Section 21); resolved SOURCE-GAP-ES-03 (OBJ-015–022 now canonically labeled at PS §51.13; see Section 41.11 and the registry at EAIOC-ARCH-001 §44.12) (Rev 1.4).

---

> [!IMPORTANT]
> This specification is a faithful, complete engineering translation of the problem statement.
> No requirement has been silently removed, simplified, reinterpreted, or replaced.
> An **Internal Consistency Check** is included in Section 40, updated for the Rev 1.3 hardening pass in Section 42.23, and re-verified for the Rev 1.4 reconciliation pass in Section 42.24.
> Implementation may not begin until the consistency check passes and this document is explicitly approved.

---

## Table of Contents

1. [Purpose and Scope](#1-purpose-and-scope)
2. [Problem Definition](#2-problem-definition)
3. [Enterprise Objectives](#3-enterprise-objectives)
4. [Product Scope and Execution Environments](#4-product-scope-and-execution-environments)
5. [Optimization Domains](#5-optimization-domains)
6. [System Architecture](#6-system-architecture)
7. [Core Pipeline Components](#7-core-pipeline-components)
8. [Optimization Intelligence Layer](#8-optimization-intelligence-layer)
9. [Context Lifecycle Intelligence](#9-context-lifecycle-intelligence)
10. [Cache Economics and Cache-Aware Assembly](#10-cache-economics-and-cache-aware-assembly)
11. [Tool Execution Optimization](#11-tool-execution-optimization)
12. [Agent Loop and Multi-Agent Economics](#12-agent-loop-and-multi-agent-economics)
13. [Adaptive Model, Reasoning, and Output Control](#13-adaptive-model-reasoning-and-output-control)
14. [Quality-Constrained Optimization](#14-quality-constrained-optimization)
15. [Optimization Experimentation and Learning](#15-optimization-experimentation-and-learning)
16. [Provider / Model Optimization Profiles](#16-provider--model-optimization-profiles)
17. [Inference / Serving Optimization Layer (P5 — Optional)](#17-inference--serving-optimization-layer-p5--optional)
18. [Token and Cost Accounting](#18-token-and-cost-accounting)
19. [Quality Gates](#19-quality-gates)
20. [Security and Governance Requirements](#20-security-and-governance-requirements)
21. [Failure and Fallback Model](#21-failure-and-fallback-model)
22. [Observability and Governance](#22-observability-and-governance)
23. [Benchmarking Framework](#23-benchmarking-framework)
24. [Experimental Validation Matrix](#24-experimental-validation-matrix)
25. [Non-Functional Requirements](#25-non-functional-requirements)
26. [Configuration Requirements](#26-configuration-requirements)
27. [Enterprise Deployment Model](#27-enterprise-deployment-model)
28. [Rollout Strategy](#28-rollout-strategy)
29. [Regression Testing](#29-regression-testing)
30. [Implementation Priority](#30-implementation-priority)
31. [Acceptance Criteria](#31-acceptance-criteria)
32. [Success Metrics](#32-success-metrics)
33. [Anti-Patterns to Avoid](#33-anti-patterns-to-avoid)
34. [Optimization Objective Function](#34-optimization-objective-function)
35. [Enterprise Optimization Maturity Model](#35-enterprise-optimization-maturity-model)
36. [Reference Research and Validation Notes](#36-reference-research-and-validation-notes)
37. [Cost Model](#37-cost-model)
38. [Performance Model](#38-performance-model)
39. [Final Product Requirements Checklist](#39-final-product-requirements-checklist)
40. [Internal Consistency Check](#40-internal-consistency-check)
41. [Dynamic Execution State & Context Lifecycle Management](#41-dynamic-execution-state--context-lifecycle-management)
42. [Architecture Hardening Amendment — Operating Model, Governance, and Trust Boundaries](#42-architecture-hardening-amendment--operating-model-governance-and-trust-boundaries)

---

## 1. Purpose and Scope

### 1.1 Product Title

**Enterprise Agent & LLM Inference Optimization Control Plane**

### 1.2 Executive Purpose

Build a production-ready, enterprise-grade optimization control plane for LLM-powered applications, autonomous agents, developer/coding agents, and multi-agent/sub-agent systems.

The framework shall reduce unnecessary:
- Inference cost
- Token consumption
- Latency
- Context growth
- Redundant model/tool calls
- Operational waste

While **preserving**:
- Correctness
- Semantic fidelity
- Retrieval relevance
- Output quality
- Security and tenant isolation
- Auditability
- Predictable behavior
- Reliability
- Model/provider portability

### 1.3 Foundational Constraints

The framework **must not** be treated as a simple prompt-compression utility.

It shall be an **adaptive, policy-driven optimization and governance control plane** that can sit between an AI application/agent orchestrator and one or more LLM providers, and where supported can operate at the agent/tool and inference-serving layers.

**Central design principle:**

> "Make tokens count instead of merely counting them."

**Central design & final architectural principle:**

> The optimizer should be allowed to conclude that the best LLM call is no LLM call at all.

The framework shall optimize both:

| Dimension | Description |
|---|---|
| **TOKEN VOLUME** | Reduce tokens that need to be generated or processed |
| **TOKEN ECONOMICS** | Reduce the effective cost of processing tokens through caching, routing, batching, reuse, model selection, and conditional inference |

> [!CAUTION]
> No optimization shall be considered successful solely because it reduces token count. It must demonstrate acceptable quality and measurable net value.

**Hardening Pass amendment (2026-09-15, PS §52.18, H18):** the engineering system must treat prompt/input token optimization, output/reasoning token optimization, context optimization, cache optimization, model/provider routing, and inference-runtime optimization as six separately measured categories with separate ownership boundaries. The Control Plane implements the first five directly; for inference-runtime optimization (Layer 3, Section 17) it owns awareness, integration, routing, and policy only — implementation is infrastructure/provider-owned. See Section 42.17/42.18.

**Hardening Pass amendment (2026-09-15, PS §52.2/§52.20, H02/H20):** the Control Plane's relationship to any decision or side effect is exactly one of ADVISORY, ENFORCEMENT, or EXECUTION-OWNERSHIP (Section 42.2), and it does not assume ownership of agent planning, IDE behavior, or external side effects absent an explicit integration surface. It is not a replacement agent orchestrator, model-training system, model-runtime infrastructure platform, provider, general-purpose IDE, or coding-agent UX (Section 42.20).

### 1.4 Scope Statement

The target product is an **Enterprise Agent & LLM Inference Optimization Control Plane**. Its purpose is not to execute every optimization technique on every request. It must **continuously decide the cheapest safe execution strategy** that satisfies the required quality, latency, security, and reliability constraints.

---

## 2. Problem Definition

Modern LLM applications frequently send much more information to models than the user-visible request suggests.

### 2.1 Sources of Context Inflation

A single request can contain any of the following:

- System instructions
- Developer instructions
- User prompt
- Conversation history
- Retrieved RAG chunks
- Search results
- Policies and documentation
- Tool definitions
- Tool outputs
- API responses
- Logs
- Code
- Test output
- Previous agent plans
- Previous agent responses
- Intermediate reasoning/task state
- Repeated instructions
- Duplicated documents or excerpts

In agentic workflows, this context may be transmitted **repeatedly** across multiple model calls.

### 2.2 Root Problem

The resulting problem is not simply "large prompts". It is **uncontrolled inference consumption** caused by unnecessary, duplicated, stale, low-value, poorly positioned, or repeatedly processed information.

### 2.3 Required Decisions

The framework must answer for every request:

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

---

## 3. Enterprise Objectives

The framework shall achieve the following strategic objectives (all are mandatory):

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

> [!NOTE]
> OBJ-015 through OBJ-022 (versioned execution state, checkpointing, mutation reconciliation, Logical/Model-Admitted Context separation, tiered eviction, interruption handling, stale-result rejection, scenario-completeness gating) are specified in Section 41.11, tied to the 2026-09-10 hardening pass (PS §51). They are listed there rather than merged into this table to preserve that amendment's original traceability structure. **They are unambiguously mandatory**, on equal footing with every other objective in this document: as of the 2026-09-16 reconciliation pass they are canonically labeled at their source in PS §51.13, and the full OBJ-001–035 cross-document registry (ID, statement, Problem Statement section, this document's section, Architecture section, status) is maintained at EAIOC-ARCH-001 §44.12. Former `SOURCE-GAP-ES-03` is RESOLVED — see Section 42.22.

**Added by the 2026-09-15 Architecture Hardening Pass (PS §52, traced individually in Section 42):**

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

## 4. Product Scope and Execution Environments

### 4.1 First-Class Execution Environments

The framework shall support four primary execution environments through **one common optimization engine**:

| Environment | Description |
|---|---|
| **A** | Generic LLM Applications |
| **B** | Autonomous / Tool-Using Agents |
| **C** | Developer / Coding Agents (Cursor, Claude Code, GitHub Copilot, Antigravity, Codex, and comparable platforms, subject to available integration surfaces) |
| **D** | Multi-Agent / Sub-Agent Systems |

> [!IMPORTANT]
> Developer/coding-agent optimization is a **FIRST-CLASS PRODUCT REQUIREMENT**. It is not an optional use case.

The product shall use a **common optimization core** with integration adapters, not a separate optimizer for every agent product. It shall optimize every model-bound context generated by an agent, not merely the original user prompt.

### 4.2 Integration Modes

| # | Mode |
|---|---|
| 1 | API / LLM Gateway Proxy |
| 2 | SDK / Middleware integration |
| 3 | MCP-based optimization services |
| 4 | Agent-harness integration for controllable/open implementations |
| 5 | Provider/platform enterprise integrations where supported |

> [!IMPORTANT]
> **2026-09-15 Hardening Pass (H08, PS §52.8):** these integration modes do not imply uniform depth of access. Every coding-agent integration must declare an **integration feasibility tier** — see Section 42.8 for the tier definitions, the required capability-declaration mechanism, and the constraint that DA-001 through DA-025 module applicability is bounded by the declared tier for that platform.

### 4.3 Common Representation Layer

The adapter layer shall normalize different agent/provider interfaces into a common representation:

```
AgentRequest
AgentContext
InstructionContext
RepositoryContext
ToolContext
RetrievalContext
ConversationContext
MemoryContext
AgentState
ModelPolicy
OptimizationPolicy
```

### 4.4 Developer-Agent Optimization Modules (DA-001 to DA-025)

All 25 developer-agent modules are **mandatory first-class requirements**:

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

### 4.5 Developer-Agent Model-Bound Pipeline

The full pipeline for every model-bound iteration (not only the first user request):

```
Developer Request
 -> Agent Context Construction
 -> Context Adapter/Interception
 -> Task + Complexity Analysis
 -> Repository Map / Memory Lookup
 -> Dynamic Tool + MCP Selection
 -> Error-Driven / Symbol-Aware Retrieval
 -> Search Result Ranking
 -> Code-Aware Context Pruning
 -> Deduplication
 -> Terminal/Test/Linter Output Optimization
 -> Sub-Agent Result Compression
 -> Context Ranking
 -> Context Budget Allocation
 -> Context Compression
 -> Context Reordering
 -> Cache-Aware Prompt Assembly
 -> Model Routing
 -> Reasoning Budget Selection
 -> LLM
 -> Output Validation
 -> Tool / File / Patch Action
 -> Tool-Result Optimization
 -> Agent Loop Controller
 -> Repeat only when additional work has sufficient expected value
 -> Final Result
```

### 4.6 Developer-Agent Success Metrics

Measure **all** of the following:
- Tokens per coding task / iteration / tool call / test-debug cycle
- Repository / code / terminal / MCP / sub-agent / memory / history / diff tokens
- Model and tool calls per completed task
- Agent iterations
- Files inspected
- Relevant-vs-irrelevant context ratio
- Cache hit rate
- Early-exit rate
- Task completion
- Patch correctness
- Test pass rate
- Regression rate
- Developer acceptance
- Cost per successfully completed coding task

> [!CAUTION]
> A coding-agent optimization is successful **only when** it reduces net inference cost and/or latency without materially degrading patch correctness, test success, task completion, security, or developer experience.

---

## 5. Optimization Domains

The framework shall optimize across all of the following domains (all are mandatory):

| ID | Domain |
|---|---|
| A | Input Optimization |
| B | Context Optimization |
| C | Retrieval Optimization |
| D | Prompt Caching and Reuse |
| E | Semantic Caching |
| F | Model Routing |
| G | Reasoning-Budget Optimization |
| H | Agent Workflow Optimization |
| I | Tool-Call and Tool-Output Optimization |
| J | Output Optimization |
| K | Batch/Asynchronous Optimization |
| L | Agent / Workflow Optimization |
| M | Context Lifecycle Optimization |
| N | Optimization Decision Intelligence |
| O | Cache Economics and Cache-Aware Assembly |
| P | Tool Execution Optimization |
| Q | Outcome-Based Cost Optimization |
| R | Inference / Serving Optimization (where infrastructure access permits) |
| S | Observability, Governance, and Continuous Learning |

---

## 6. System Architecture

### 6.1 Three-Layer Control-Plane Architecture

The logical architecture shall be organized into three optimization layers governed by a shared **Optimization Intelligence Layer**:

```
LAYER 1 - AGENT / WORKFLOW OPTIMIZATION
  - Task planning
  - Tool selection
  - Tool execution
  - Sub-agent economics
  - Agent loops
  - Session phases
  - Memory
  - Early exit
  - Outcome validation

LAYER 2 - CONTEXT OPTIMIZATION
  - Retrieval
  - Ranking
  - Pruning
  - Deduplication
  - Compression
  - Reordering
  - Context budgeting
  - Caching
  - Context freshness
  - Dependency graphs
  - Code-aware context optimization

LAYER 3 - INFERENCE / SERVING OPTIMIZATION
  - Model routing
  - Model cascade
  - Reasoning budget
  - Output budget
  - Batch processing
  - KV cache
  - Speculative decoding
  - Quantization
  - Serving/scheduling

[OPTIMIZATION INTELLIGENCE LAYER governs all three]
```

### 6.2 Full Request-Response Pipeline

```
USER / APPLICATION
        |
        v
+--------------------+
| T0.1 Model Router  |
+--------------------+
        |
        v
+--------------------+
| T0.2 Task /        |
| Complexity Analyzer|
+--------------------+
        |
        v
+--------------------+
| T0.3 Reasoning     |
| Budget Controller  |
+--------------------+
        |
        v
+--------------------+
| T1.1 Sanitizer     |
+--------------------+
        |
        v
+--------------------+
| T1.2 Intent        |
| Classifier         |
+--------------------+
        |
   +---------+---------+
   |                   |
   v                   v
+----------+    +-------------+
| T1.6     |    | T1.7        |
| Prompt   |    | Semantic    |
| Cache    |    | Cache       |
+----------+    +-------------+
        |
        v
+--------------------+
| T1.3 Entity        |
| Extraction         |
+--------------------+
        |
        v
+--------------------+
| T1.9 Context       |
| Pruner             |
+--------------------+
        |
        v
+--------------------+
| T1.10 Context      |
| Deduplicator       |
+--------------------+
        |
        v
+--------------------+
| Relevance +        |
| Token-Aware Ranker |
+--------------------+
        |
        v
+--------------------+
| T1.12 Adaptive     |
| Retrieval / Top-K  |
+--------------------+
        |
        v
+--------------------+
| T1.8 Context       |
| Compressor         |
+--------------------+
        |
        v
+--------------------+
| T1.11 Context      |
| Reorderer          |
+--------------------+
        |
        v
+--------------------+
| T1.5 Soft Reset /  |
| Context Budgeter   |
+--------------------+
        |
        v
+--------------------+
| T2.1 Query         |
| Compressor         |
+--------------------+
        |
        v
+--------------------+
| Prompt Assembler   |
| + Cache-Aware      |
+--------------------+
        |
        v
+--------------------+
| Target LLM /       |
| Agent / Cascade    |
+--------------------+
        |
        v
+--------------------+
| Output Gate        |
| + Schema + Length  |
+--------------------+
        |
        v
+--------------------+
| T3.1 Agent Stop    |
| Controller         |
+--------------------+
        |
        v
   FINAL RESPONSE

Parallel components:
+---------------------+
| T3.2 Tool Output    |
| Filter / Compressor |
+---------------------+

+---------------------+
| T3.3 Tool Result    |
| Cache               |
+---------------------+

+--------------------------------+
| OBSERVABILITY / TOKEN LEDGER   |
| Cost | Tokens | Latency | Quality|
| Cache | Routing | Tools | Errors |
+--------------------------------+
```

> [!NOTE]
> **2026-09-15 Hardening Pass (H01, PS §52.1):** this diagram is the logical decision sequence for a request requiring the full pipeline. It is not a requirement that every stage execute synchronously in the critical request path for every request. Section 42.1 specifies which stages support synchronous, asynchronous/precomputed, or hybrid evaluation.

### 6.3 Control Plane Lifecycle

The control plane shall observe and optimize the complete lifecycle:

```
Request
 -> Agent/workflow decision
 -> Context construction
 -> Retrieval
 -> Tool selection/execution
 -> Context transformation
 -> Cache/reuse
 -> Model selection
 -> Reasoning/output budgeting
 -> LLM inference
 -> Validation
 -> Tool/result processing
 -> Agent continuation/termination
 -> Outcome measurement
 -> Policy learning
```

---

## 7. Core Pipeline Components

### 7.1 T0.1 - Model Router

**Purpose:** Select the least expensive model that can satisfy the task's required quality, capability, latency, and compliance constraints.

**Requirements:**
- Support configurable model tiers
- Consider: task intent, complexity, historical quality, model price, latency requirements, context-window requirements, tool/function support, provider availability
- Support deterministic rules initially
- Support learned routing as an optional advanced capability
- Support automatic escalation to a stronger model when confidence/quality is insufficient
- Must NOT optimize cost by routing safety-critical or complex requests to incapable models

**Research basis:** RouteLLM — demonstrates model routing as a cost/quality trade-off (benchmark evidence, not production guarantee).

---

### 7.2 T0.2 - Task / Complexity Analyzer

**Purpose:** Determine an approximate task complexity before expensive inference.

**Signals:**

| Signal |
|---|
| Intent |
| Input size |
| Number of entities |
| Number of required operations |
| Number of tools required |
| Retrieval depth |
| Historical task complexity |
| Expected output structure |
| Need for reasoning |
| Confidence requirements |

**Output:**
- Complexity class
- Recommended model tier
- Recommended context budget
- Recommended reasoning budget
- Recommended output budget

---

### 7.3 T0.3 - Reasoning Budget Controller

**Purpose:** Dynamically select a reasoning budget based on task complexity.

**Mapping:**

| Complexity | Reasoning Budget |
|---|---|
| Simple extraction | Low |
| Moderate analysis | Medium |
| Complex multi-step reasoning | High |

**Requirements:**
- Support model/provider-specific reasoning controls where available
- Never reduce reasoning solely to meet a token target
- Compare quality against baseline
- Support configurable minimum quality thresholds
- Record allocated and consumed reasoning tokens where the provider exposes them
- Support fallback escalation when the initial reasoning budget is insufficient

---

### 7.4 T1.1 - Sanitizer

**Purpose:** Normalize raw input before downstream processing.

**Capabilities:**
- Normalize whitespace
- Remove irrelevant formatting
- Remove duplicated content
- Strip known UI noise
- Normalize equivalent representations
- Preserve code, structured data, identifiers, URLs, and user-intent information
- Produce baseline token count
- Produce post-sanitization token count

> [!CAUTION]
> The sanitizer must be content-aware and must not blindly remove syntax that is meaningful to the task.

---

### 7.5 T1.2 - Intent Classifier

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

**Downstream policy selection:**
- Prompt template
- Retrieval strategy
- Context budget
- Output schema
- Model tier
- Reasoning budget
- Compression strategy

---

### 7.6 T1.3 - Entity Extraction

**Purpose:** Extract structured information before downstream retrieval/model execution.

**Examples:**
- Named entities
- Numeric values
- Dates / date ranges
- Product identifiers
- Customer identifiers
- Categories
- Geographic constraints
- Filters

**Entity metadata availability:** Retrieval, tool calls, ranking, prompt assembly, validation.

**Rationale:** Reduces the need for downstream models to repeatedly perform basic information extraction.

---

### 7.7 T1.6 - Prompt Cache

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

The Prompt Assembler shall intentionally construct cache-stable prefixes.

**Note:** Provider cache semantics differ (e.g., Anthropic supports automatic and explicit prefix caching, TTL controls, cache read/write isolation). The implementation must remain provider-neutral.

---

### 7.8 T1.7 - Semantic Cache

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

| Classification |
|---|
| Exact cache hit |
| Semantic cache hit |
| Unsafe/invalid cache candidate |
| Cache miss |

> [!CAUTION]
> Semantic caching must never return a stale or unauthorized result merely because the query is similar.

---

### 7.9 T1.9 - Context Pruner

**Purpose:** Remove low-value context before expensive compression or inference.

**Techniques:**
- Sentence-level relevance
- Paragraph-level relevance
- Embedding similarity
- Lexical similarity
- Entity overlap
- Metadata filtering
- Recency
- Source authority
- Task-specific rules

The pruning stage should be **primarily deterministic or lightweight**.

**Measurements required:**
- Tokens before pruning
- Tokens after pruning
- Information/quality impact
- False-pruning rate

---

### 7.10 T1.10 - Context Deduplicator

**Purpose:** Detect and remove duplicate or near-duplicate information.

**Scope:**
- Conversation history
- RAG chunks
- Tool results
- Uploaded documents
- Search results
- Previous agent outputs
- Code and logs

**Supported levels:**
- Exact duplicate detection
- Near-duplicate detection
- Paragraph-level duplicate detection
- Code duplicate detection
- Repeated tool-output detection

The system shall preserve one canonical copy and record removed tokens.

---

### 7.11 Token-Aware Ranker

**Purpose:** Rank context using more than relevance alone.

**Ranking signals:**

| Signal |
|---|
| Relevance |
| Recency |
| Information density |
| Token cost |
| Source authority |
| Diversity |
| Duplication |
| Task importance |

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
> The exact formula shall be benchmarked rather than assumed to be universally optimal.

---

### 7.12 Adaptive Retrieval / Top-K

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
> More retrieved context must not automatically be considered better.

---

### 7.13 T1.8 - Context Compressor

**Purpose:** Compress retrieved/contextual material while preserving task-relevant information.

**Supported compression modes:**
- Extractive compression
- Model-based compression
- Query-aware compression
- Coarse-to-fine compression
- Dynamic compression ratios
- Entity preservation
- Subsequence/sentence recovery
- Code-aware preservation where required

Selection among strategies is based on: context size, task type, quality risk, and cost.

**Research basis:**
- LLMLingua: coarse-to-fine prompt compression with budget control
- LongLLMLingua: question-aware compression, document reordering, dynamic compression ratios, subsequence recovery
- LLMLingua-2: token classification formulation, faster compression

> [!IMPORTANT]
> Reported research numbers are benchmark results, not guaranteed production targets. The framework must validate compression against its own workloads.

---

### 7.14 T1.11 - Context Reorderer

**Purpose:** After selecting context, optimize its ordering.

**Requirements:**
- Place highest-value information in strategically useful positions
- Keep user query and task instructions clearly identifiable
- Preserve source boundaries
- Avoid accidental instruction precedence changes
- Support model-specific ordering policies
- Measure quality before and after reordering
- Specifically evaluate the effect of "lost in the middle"/position bias

---

### 7.15 T1.5 - Soft Reset / Context Budgeter

**Purpose:** Maintain a hard or configurable context ceiling.

**Default reference budget:** 500K tokens (from source whitepaper).

**Budget configurability dimensions:**
- Model
- Provider
- Workflow
- Tenant
- User
- Intent
- Task complexity

**When context exceeds budget (in order):**
1. Rank context
2. Remove low-value context
3. Deduplicate
4. Compress remaining context if required
5. Summarize older history where appropriate
6. Preserve key facts and decisions
7. Record a reset report

> [!CAUTION]
> The system must not assume that a larger context window is always beneficial.

---

### 7.16 T2.1 - Query Compressor

**Purpose:** Compress the user's request while preserving functional equivalence.

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

**Validation target:** 30-60% query reduction (source whitepaper figure -- treated as validation target, not guarantee).

---

### 7.17 Prompt Assembler

**Purpose:** Build a final prompt that contains only the information needed for the current task.

**Responsibilities:**
- Select intent-specific instructions
- Include only approved context
- Preserve required security/policy instructions
- Maintain cache-stable prefixes
- Add dynamic content after stable prefixes where possible
- Produce final token counts
- Produce a complete assembly ledger

**Explicit separation:**

| Section | Contents |
|---|---|
| **CACHEABLE PREFIX** | System instructions, stable developer policy, tool definitions, stable examples, stable reference material |
| **DYNAMIC SUFFIX** | Current user request, current retrieved context, current tool results, current task state |

---

### 7.18 Output Schema Selector

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

### 7.19 Output Length Controller

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
- JSON schema
- Function calling
- Grammar constraints
- Stop sequences

**Truncation handling:**
- Prefer semantic boundaries
- Mark truncation
- Preserve machine-readable validity where possible
- Permit controlled expansion

---

### 7.20 T3.1 - Agent Stop Controller

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

### 7.21 T3.2 - Tool Output Filter / Compressor

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

The system should pass the 1,200-token representation while retaining the original result for audit where policy permits.

---

### 7.22 T3.3 - Tool Result Cache

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

### 7.23 Model Cascade / Escalation

**Purpose:** Optional cascade execution for cost/quality optimization.

**Flow:**
1. Start with a lower-cost model
2. Evaluate confidence/quality
3. If acceptable, return
4. Otherwise escalate to a stronger model

**Escalation triggers:**
- Low confidence
- Schema failure
- Validation failure
- Retrieval uncertainty
- User-defined quality requirements
- Safety/policy requirements
- Complexity threshold

**Research basis:** FrugalGPT -- explores cascades and cost-aware combinations of LLMs (benchmark evidence, not production guarantee).

---

### 7.24 Batch / Asynchronous Optimizer

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

### 7.25 Optimization Order (Default Policy)

The default optimization strategy follows the lowest-risk and lowest-cost transformations first:

| # | Stage |
|---|---|
| 1 | Exact cache lookup |
| 2 | Semantic cache lookup where safe |
| 3 | Sanitization |
| 4 | Duplicate removal |
| 5 | Metadata/filter pruning |
| 6 | Entity extraction |
| 7 | Retrieval filtering |
| 8 | Adaptive Top-K |
| 9 | Context ranking |
| 10 | Context pruning |
| 11 | Context deduplication |
| 12 | Tool-output filtering |
| 13 | Context reordering |
| 14 | Context compression |
| 15 | Soft reset |
| 16 | Query compression |
| 17 | Cache-aware prompt assembly |
| 18 | Model routing |
| 19 | Reasoning-budget selection |
| 20 | LLM execution |
| 21 | Output schema enforcement |
| 22 | Output length enforcement |
| 23 | Agent stop/early exit |
| 24 | Fallback/escalation when quality is insufficient |

> [!NOTE]
> This ordering is a policy, not a rigid implementation constraint. The system shall be able to reorder stages when benchmark evidence demonstrates a better cost/quality/latency trade-off.

---

## 8. Optimization Intelligence Layer

A central **Optimization Intelligence Layer** shall sit above the individual optimization mechanisms and govern all three layers.

### 8.1 OI-001 - Optimization Decision Engine

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

### 8.2 OI-002 - Cost-of-Optimization Controller

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

### 8.3 OI-003 - Adaptive Optimization Depth Controller

**Purpose:** Select optimization depth based on task complexity, context size, expected value, latency requirements, and historical workload behavior.

| Complexity | Optimization Depth |
|---|---|
| LOW | Exact cache, deterministic hygiene, basic routing |
| MEDIUM | Retrieval optimization, pruning, deduplication, compression, routing |
| HIGH | Dependency-aware retrieval, context graph analysis, advanced compression, multi-agent economics, deeper quality validation, iterative optimization |

> [!IMPORTANT]
> The system shall not perform expensive optimization on requests where simple optimization is sufficient.

---

### 8.4 OI-004 - Context Utility / ROI Scorer

**Purpose:** Assign a utility score to each context item.

**Candidate signals:**
- Task relevance
- Information density
- Source authority
- Recency
- Freshness
- Dependency importance
- Diversity
- Historical usefulness
- Confidence
- Token cost

**Conceptual score:**

```
Context ROI = Context Utility / Context Cost
```

> [!NOTE]
> The exact formula must remain configurable and benchmark-driven.

---

### 8.5 OI-005 - Outcome-Based Optimization Engine

**Purpose:** Optimize for successful outcomes rather than token reduction alone.

**Required metrics:**
- Cost per successful answer
- Cost per successfully completed workflow
- Cost per successfully resolved task
- Cost per successfully completed coding task
- Tokens per successful outcome
- Model/tool calls per successful outcome

> [!CAUTION]
> A token reduction that lowers task success must not be considered a win.

---

## 9. Context Lifecycle Intelligence

### 9.1 CL-001 - Context Dependency Graph

**Purpose:** Represent context as a dependency graph rather than a flat collection.

**For developer agents, the graph may include:**
- User request
- Files
- Symbols
- Functions
- Classes
- Imports
- Callers/callees
- Tests
- Git changes
- Errors
- Tools
- Agent decisions

**Graph capabilities:**
- Targeted retrieval
- Dependency-aware pruning
- Change-impact analysis
- Cache invalidation
- Freshness evaluation
- Recovery of previously removed context

---

### 9.2 CL-002 - Context Freshness Scoring

**Per context item, carry where available:**
- Source
- Version
- Commit
- Branch
- Timestamp
- Last verification time
- Freshness status

Freshness shall influence: ranking, caching, retrieval, and validation.

---

### 9.3 CL-003 - Dependency-Aware Cache Invalidation

**When a source item changes, invalidate or revalidate dependent cached representations.**

| Trigger | Action |
|---|---|
| File changed | Invalidate symbol summaries |
| Symbol changed | Invalidate dependent context |
| Branch changed | Invalidate branch-specific cache |
| Permission changed | Invalidate affected cache entries |
| External data changed | Invalidate freshness-sensitive semantic cache |

---

### 9.4 CL-004 - Reversible Optimization

**Where practical, destructive context reduction must remain recoverable.**

**Every transformed item should record:**
- Original source
- Transformation type
- Removed range/content reference
- Reason
- Confidence
- Recovery pointer
- Expiration/freshness metadata

If later evidence indicates that removed information is required, the optimizer shall be able to retrieve it without reconstructing the original request manually.

---

### 9.5 CL-005 - Progressive Context Compaction

**Compaction shall be proactive and multi-stage.**

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

### 9.6 CL-006 - Hierarchical Memory

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
- Retention
- Freshness
- Compression
- Retrieval
- Token budget
- Invalidation policy

---

### 9.7 CL-007 - Session Phase Management

**Long-running tasks shall support explicit phases:**

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

## 10. Cache Economics and Cache-Aware Assembly

### 10.1 CE-001 - Cache Economics Engine

**Purpose:** Make economically informed caching decisions.

**Expected Cache Benefit:**
```
Future Reuse Probability
x Tokens Avoided
x Effective Input Price
```

**Cache Cost:**
```
Write Cost
+ Storage Cost
+ Invalidation Cost
+ Freshness Risk
+ Cache Miss/Break Risk
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

### 10.2 CE-002 - Cache-Aware Context Construction

**Preferred prompt structure:**

| Region | Contents |
|---|---|
| **STABLE / CACHEABLE PREFIX** | System instructions, stable developer policies, stable tool schemas, stable project rules, stable examples, stable repository metadata |
| **SEMI-STABLE REGION** | Project documentation, memory, slowly changing context |
| **VOLATILE REGION** | Current user request, current files, current errors, current tool results, current task state |

The optimizer shall minimize unnecessary changes to cacheable prefixes.

---

### 10.3 CE-003 - Cache Fragmentation Detector

**Detect likely cache misses caused by:**
- Dynamic timestamps
- Random identifiers
- Tool ordering
- Serialization changes
- Whitespace/format changes
- Dynamic metadata
- Changing system instructions
- Changing tool definitions
- Unstable context ordering

The system shall produce a cache-miss diagnosis and estimate the potential recoverable cache value.

---

### 10.4 CE-004 - Cache Pre-Warming Policy

Where the provider supports it, allow pre-warming of high-value stable prefixes when the expected reduction in first-request latency/cost justifies the pre-warm cost.

---

### 10.5 CE-005 - Cache Breakpoint Optimizer

Where provider semantics permit multiple cache boundaries, choose cache breakpoints based on context stability and expected reuse frequency.

---

## 11. Tool Execution Optimization

### 11.1 TE-001 - Tool Call ROI Predictor

**Before executing a tool, estimate:**

```
Expected Information Gain
/
Expected Tool Cost + Token Cost + Latency Cost
```

Skip or defer tool calls whose expected value is insufficient.

---

### 11.2 TE-002 - Tool Argument Optimizer

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

### 11.3 TE-003 - Dynamic Tool Loading

**When supported, do not expose large tool catalogs upfront.**

```
Task
 -> Tool index/search
 -> Relevant tool discovery
 -> Minimal tool schemas
 -> Execution
```

Especially important for large MCP/tool ecosystems.

---

### 11.4 TE-004 - Programmatic Tool Execution

Where supported, collapse multiple independent or sequential tool calls into a programmatic execution unit so intermediate results do not all need to enter the model conversation history.

The framework shall benchmark:
- Traditional tool round trips vs.
- Programmatic/aggregated execution

---

### 11.5 TE-005 - Parallel Tool Execution

Identify independent tool calls and execute them concurrently where:
- Dependencies permit
- Side effects are safe
- Provider/agent policies permit

---

### 11.6 TE-006 - Tool Result Value Filter

**Retain only:**
- Required fields
- Relevant records
- Relevant errors
- Evidence
- Provenance
- Required authorization/audit fields

---

### 11.7 TE-007 - Tool Result Recovery

Large filtered results shall remain recoverable where policy permits.

---

## 12. Agent Loop and Multi-Agent Economics

### 12.1 AL-001 - Agent Loop Progress Meter

**Measure for each agent iteration:**
- Input tokens
- Output tokens
- Tool cost
- New information gained
- State change
- Objective progress
- Errors introduced/resolved

---

### 12.2 AL-002 - Loop Waste Detector

**Detect:**
- Repeated searches
- Repeated tool calls
- Repeated reasoning over unchanged state
- Oscillation between states
- No-progress iterations
- Dead-end loops

> [!IMPORTANT]
> **2026-09-15 Hardening Pass (H10, PS §52.10):** repeated iteration is not inherently waste. The same surface pattern may be productive progress, legitimate reflection/self-correction, a justified retry, or genuine waste/oscillation/failure. The detector must classify against the AL-001 signals (state change, objective progress, information gain, errors introduced/resolved) and expected task value, not against iteration count or pattern-match alone. The same expected-value framework governs both the CONTINUE decision (AL-001/AL-002) and the STOP decision (Section 7.20, AL-006) — see Section 42.10.

---

### 12.3 AL-003 - Sub-Agent Value Predictor

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

### 12.4 AL-004 - Sub-Agent Scheduler

**For independent tasks:**
- Identify parallelizable work
- Execute concurrently where safe
- Aggregate results compactly
- Avoid duplicated repository exploration

---

### 12.5 AL-005 - Sub-Agent Handoff Compression

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

### 12.6 AL-006 - Agent Task Value / Early Exit

**Stop an agent when:**
- Objective is satisfied
- Required validation passes
- Additional information has low expected value
- Additional model/tool calls are unlikely to change the outcome

Record the avoided work.

---

## 13. Adaptive Model, Reasoning, and Output Control

### 13.1 AR-001 - Marginal-Gain Model Routing

```
Marginal Model Value = Expected Quality Gain / Additional Cost
```

Prefer escalation only when the expected marginal value is positive and quality constraints justify it.

---

### 13.2 AR-002 - Reasoning Budget Prediction

**Predict required reasoning depth from:**
- Task complexity
- Historical task outcomes
- Tool requirements
- Verification requirements
- Model behavior
- User-defined quality tier

Start with an appropriate budget and escalate when validation indicates insufficient reasoning.

---

### 13.3 AR-003 - Output Budget Forecasting

**Predict expected output length before generation and set the smallest safe output budget.**

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

### 13.4 AR-004 - Verifier-Guided Escalation

**Where a deterministic or inexpensive verifier exists:**
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

## 14. Quality-Constrained Optimization

### 14.1 QO-001 - Compression Contracts

**Every aggressive transformation may define invariants that must survive.**

| Context type | Invariants |
|---|---|
| **Coding** | Function signatures, imports, types, API contracts, security constraints, relevant test expectations, error locations |
| **RAG** | Entities, numbers, dates, citations, source attribution |
| **Agent** | Objective, constraints, decisions, completed actions, pending actions, unresolved failures |

---

### 14.2 QO-002 - Quality-Aware Fallback

**If a transformed request fails validation:**
- Restore the previous representation
- Increase context budget
- Increase retrieval depth
- Increase reasoning budget
- Escalate model
- Disable the offending optimization

---

### 14.3 QO-003 - Delayed-Relevance Protection

**The framework shall recognize that information appearing irrelevant at one step may become relevant later in an agent workflow.**

**Use:**
- Recovery pointers
- Dependency graph
- Freshness metadata
- Source references
- Re-fetch mechanisms

---

## 15. Optimization Experimentation and Learning

### 15.1 EL-001 - Shadow Optimization

**Run optimization decisions in shadow mode without modifying production requests.**

**Compare:**
- Baseline tokens
- Simulated optimized tokens
- Predicted cost
- Predicted quality
- Cacheability
- Routing decisions
- Expected savings

---

### 15.2 EL-002 - Controlled Rollout

**Support progressive activation:** Shadow -> 1% -> 5% -> 25% -> 50% -> 100%

With automatic rollback on quality/cost regressions.

---

### 15.3 EL-003 - Optimization A/B Testing

Every optimization should be independently testable against a control group.

---

### 15.4 EL-004 - Continuous Policy Learning

**Learn from production outcomes:**
- Which optimization pays off
- Which workload benefits
- Which model is sufficient
- Which compression ratio is safe
- Which tools are actually needed
- Which agent loops tend to fail

> [!CAUTION]
> Learning must be bounded by governance and may not silently change production policy without configured approval.

---

### 15.5 EL-005 - Optimization Regression Detector

**Detect when a previously successful optimization stops producing positive net value after:**
- Model changes
- Provider pricing changes
- Prompt changes
- Tool changes
- Repository changes
- Traffic-pattern changes
- Cache behavior changes

---

## 16. Provider / Model Optimization Profiles

**Maintain a versioned capability and economics profile for every supported provider/model.**

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
> The optimizer shall not assume that an optimization valid for one provider or model is valid for another.

---

## 17. Inference / Serving Optimization Layer (P5 - Optional)

> [!IMPORTANT]
> This layer is **OPTIONAL and infrastructure-dependent**. It must remain separate from prompt/context optimization and must be enabled only after workload-specific validation.

> [!CAUTION]
> **2026-09-15 Hardening Pass (H17, PS §52.17):** the Control Plane's role at this layer is **awareness, integration, routing, policy, and capability negotiation** with infrastructure/providers. It must **NOT** implement its own KV-cache engine, continuous-batching scheduler, speculative-decoding mechanism, quantization pipeline, or prefill/decode disaggregation. These remain infrastructure/provider-owned mechanisms the Control Plane integrates with — see Section 42.17.

**Capabilities the Control Plane is AWARE OF and may ROUTE/NEGOTIATE against (implemented by infrastructure/providers, not by the Control Plane):**
- Prefix/KV-cache reuse
- KV-cache compression
- Continuous batching
- Request scheduling
- Prefill optimization
- Decode optimization
- Speculative decoding
- Quantization
- Model replica selection
- GPU utilization optimization
- Inference queue management

For each capability, the Control Plane's own responsibility is limited to: detecting whether the provider/infrastructure exposes it, routing to take advantage of it when net-beneficial and policy-compliant, negotiating capability parameters through the provider adapter, and measuring the effect separately from Layer 1/2 measurement.

---

## 18. Token and Cost Accounting

The framework shall maintain a detailed per-request token ledger.

### 18.1 Required Ledger Fields

**INPUT:** Raw input tokens, Sanitized tokens, Compressed query tokens, Context tokens, Retrieved tokens, Pruned tokens, Deduplicated tokens, Compressed context tokens, Cached input tokens, Uncached input tokens

**OUTPUT:** Raw output tokens, Optimized output tokens, Truncated tokens, Expanded/retry tokens

**CACHE:** Exact cache hits, Semantic cache hits, Cache misses, Cache writes, Cache reads, Cacheable tokens, Reused tokens

**MODEL:** Model selected, Candidate model, Routing decision, Escalation, Reasoning budget, Reasoning tokens where available

**TOOLS:** Tool calls attempted, Tool calls avoided, Tool output tokens, Filtered tool tokens, Cached tool calls

**WORKFLOW:** Agent steps planned, Agent steps executed, Agent steps skipped, Early exits, Retries

**COST:** Input cost, Output cost, Cache cost, Compression cost, Tool cost where available, Total optimized cost, Estimated baseline cost, Net savings, Savings percentage

**PERFORMANCE:** End-to-end latency, Time to first token, Model latency, Compression latency, Cache latency, Tool latency

**QUALITY:** Correctness score, Relevance score, Schema compliance, Semantic preservation, User/task quality score, Safety/policy validation

### 18.2 Extended Ledger - Optimization Economics

**OPTIMIZATION ECONOMICS:** Optimization compute cost, Optimization latency, Optimization token usage, Optimization calls, Gross inference savings, Net inference savings, Net savings percentage, Break-even reuse count, Break-even cache hit count

**OUTCOME ECONOMICS:** Cost per successful outcome, Tokens per successful outcome, Cost per completed coding task, Cost per successful agent workflow, Cost per verified answer

**CONTEXT ECONOMICS:** Utility per token, Freshness score, Context ROI, Cacheability score, Reuse probability, Compression benefit, Recovery cost

**AGENT ECONOMICS:** Cost per iteration, Information gain per iteration, State-change per iteration, Progress per token, Tool-call ROI, Sub-agent ROI, Avoided model calls, Avoided tool calls

**CACHE ECONOMICS:** Cacheable tokens, Cached tokens, Cache hit rate, Cache miss causes, Cache write cost, Cache read cost, Cache break rate, Reuse count, Cache ROI

### 18.3 Cost Model

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

## 19. Quality Gates

> [!CAUTION]
> Token reduction shall never be accepted without quality validation.

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
4. Do NOT report the optimization as a successful saving

---

## 20. Security and Governance Requirements

All security requirements are mandatory and non-negotiable:

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

**Added by the 2026-09-15 Architecture Hardening Pass (PS §52, traced individually in Section 42):**

| ID | Requirement |
|---|---|
| **SEC-011** | Spend limits, circuit breakers, and budget-aware routing/execution must never bypass or substitute for authorization, policy, or security enforcement; a budget check and a security check are evaluated independently |
| **SEC-012** | Sensitive data (including PII/PHI/PCI/secrets where applicable) must be classified before admission to any optimization stage, and classification must determine encryption, retention, and caching eligibility; no specific regulatory regime is assumed unless the deployment's own scope establishes it |
| **SEC-013** | Deletion/erasure must propagate to every surface the Control Plane persisted data to — exact and semantic caches, agent/session memory, cost/token ledgers that retain content, and logs/traces — not only the primary store |
| **SEC-014** | Tool and MCP metadata (identity, capability declarations, schemas, versions) are part of the security/control boundary and must be authenticated, authorization-checked, and validated for staleness/integrity before being trusted; a tool result is not safe merely because the call was authorized |
| **SEC-015** | Consequential or irreversible actions designated by policy/risk classification must receive explicit human approval before execution; this applies only to the designated subset, not every action |
| **SEC-016** | Retrieved content, tool output, and other externally-sourced context are untrusted by default and must pass prompt-injection/content-integrity screening before any optimization stage acts on them |

---

## 21. Failure and Fallback Model

**Every optimization stage must have an explicit fallback.**

| Failure Condition | Fallback |
|---|---|
| Cache unavailable | Continue without cache |
| Semantic match uncertain | Cache miss |
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
| Token accounting fails | Mark usage as unverified; never fabricate savings |

> [!IMPORTANT]
> Optimization failure must not become application failure unless the optimization itself is explicitly required by policy.

---

## 22. Observability and Governance

### 22.1 Dashboard and Report Requirements

**Executive:** Total AI spend, Cost saved, Savings percentage, Model mix, Cache utilization, Token growth, Quality impact

**Engineering:** Tokens by stage, Context size, Compression ratio, Model routing, Tool usage, Cache hit rate, Latency, Failures, Fallbacks

**Product/Operations:** Cost per workflow, Cost per user/team, Cost per business transaction, Quality trends, Adoption, ROI

### 22.2 Required Dimensions

Tenant, Application, Workflow, Agent, Model, Provider, User/team where permitted, Prompt type, Intent, Date/time

---

## 23. Benchmarking Framework

Before production rollout, run the optimization framework against a representative corpus of real prompts.

### 23.1 Required Benchmark Corpus Types

Simple prompts, Long prompts, Long-context tasks, RAG tasks, Coding tasks, Debugging tasks, Tool-heavy agent tasks, Multi-turn conversations, Structured extraction, Comparative queries, Procedural queries, High-risk/safety-sensitive workflows where permitted

### 23.2 For Each Request Run

**BASELINE** -- Original application behavior without optimization.

**OPTIMIZED** -- Full optimization pipeline.

**Compare:** Input tokens, Output tokens, Total tokens, Cached tokens, Model calls, Tool calls, Cost, Latency, Quality, Failure rate

---

## 24. Experimental Validation Matrix

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

## 25. Non-Functional Requirements

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
| **NFR-010** | **Explainability** -- The system should explain why a context item was removed, compressed, cached, routed, or retained where practical. **Strengthened 2026-09-15 (H15, PS §52.15):** for context admission/pruning, model/provider selection, cache reuse/rejection, optimization skip, execution block/recovery/supersession, and fallback decisions, an explanation must be retrievable for audit, not merely offered where convenient — see Section 42.15 |
| **NFR-011** | **Determinism** -- Policy-based optimization should be deterministic where feasible |
| **NFR-012** | **Configurability** -- Organizations must be able to tune policies per workflow |
| **NFR-013** | **Testability** -- Every optimization stage must be independently testable |
| **NFR-014** | **Control-Plane Self-Protection** (added 2026-09-15, H03, PS §52.3) -- The Control Plane must enforce its own latency and processing/cost budgets, with backpressure, and must degrade gracefully to a deterministic safe fallback under overload rather than degrading the application it serves. Distinct from NFR-009: NFR-009 governs whether an optimization is worth doing; NFR-014 governs what the Control Plane does to itself when it cannot keep up — see Section 42.3 |

---

## 26. Configuration Requirements

All major optimization behavior shall be configurable. Minimum configurable parameters:

Maximum context tokens, Per-intent budgets, Compression thresholds, Minimum quality thresholds, Cache TTL, Semantic similarity threshold, Retrieval Top-K limits, Model routing policy, Model escalation policy, Reasoning budgets, Output token limits, Tool-output field policies, Early-exit thresholds, Tenant policies, Data retention policies

---

## 27. Enterprise Deployment Model

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

## 28. Rollout Strategy

| Phase | Description |
|---|---|
| **PHASE 1 - OBSERVE** | Run in shadow mode. Do not modify production requests. Record baseline token/cost/quality. |
| **PHASE 2 - BENCHMARK** | Run optimization offline. Compare techniques independently. Identify high-value workflows. |
| **PHASE 3 - CONTROLLED PILOT** | Enable optimization for selected workflows. Preserve baseline fallback. |
| **PHASE 4 - PRODUCTION** | Enable validated optimizations. Monitor cost, quality, latency, and failures. |
| **PHASE 5 - GOVERNANCE** | Establish token budgets. Establish cost thresholds. Establish quality SLOs. Review optimization drift. |
| **PHASE 6 - CONTINUOUS OPTIMIZATION** | Re-run benchmark after model/provider changes. Re-evaluate routing, compression, cache performance, and prompt templates. Detect regressions. |

---

## 29. Regression Testing

Every model, prompt, retrieval, or orchestration change shall be evaluated against the benchmark corpus.

**Regression dimensions:** Token usage, Cost, Latency, Quality, Retrieval recall, Cache hit rate, Model routing accuracy, Tool-call count, Agent step count

The system must detect when an optimization that previously produced savings stops producing net savings.

---

## 30. Implementation Priority

### P0 - Foundation (Unblocked)
Token accounting, Baseline benchmark harness, Sanitizer, Context policy, Prompt assembler, Output controls, Observability, Quality evaluation

### P1 - High-Confidence Optimization
Exact prompt caching, Context deduplication, Context pruning, Adaptive Top-K, Tool-output filtering, Query compression, Context compression, Output schema/length control, Soft reset

### P2 - Cost Intelligence
Model routing, Model cascade, Semantic caching, Tool-result caching, Reasoning-budget controller, Agent early exit

### P3 - Advanced Optimization
Token-aware ranking, Context reordering, Learned routing, Advanced compression, Dynamic budget allocation, Batch optimization

### P4 - Control-Plane Intelligence
Optimization Decision Engine, Cost-of-Optimization Controller, Adaptive Optimization Depth, Context Utility / ROI scoring, Outcome-based optimization, Context dependency graph, Freshness-aware context management, Dependency-aware cache invalidation, Reversible optimization, Progressive context compaction, Cache economics, Cache fragmentation diagnosis, Cache-aware context construction, Tool-call ROI, Tool argument optimization, Dynamic tool loading, Programmatic tool execution, Agent loop progress measurement, Sub-agent economics, Marginal-gain model routing, Verifier-guided escalation, Shadow optimization, A/B experimentation, Continuous policy learning

### P5 - Infrastructure-Dependent Inference Optimization (Optional)
KV/prefix cache optimization, Continuous batching, Speculative decoding, Quantization, Prefill/decode optimization, Inference scheduling, GPU utilization optimization

> [!IMPORTANT]
> Each P1/P2/P3 feature must pass the validation matrix before being enabled by default.

---

## 31. Acceptance Criteria

All 38 acceptance criteria are mandatory:

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

**Added by the 2026-09-15 Architecture Hardening Pass (PS §52.21, traced individually in Section 42):**

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

## 32. Success Metrics

**TOKEN EFFICIENCY:** Input token reduction, Output token reduction, Context token reduction, Duplicate-token reduction, Tool-output reduction, Tokens reused through caching

**INFERENCE EFFICIENCY:** Model calls avoided, Tool calls avoided, Agent steps avoided, Early exits, Routing to lower-cost models, Reasoning budget reduction

**COST EFFICIENCY:** Cost/request, Cost/workflow, Cost/user/team, Cost/business transaction, Total monthly cost, Total annual cost, Net savings, ROI

**PERFORMANCE:** Latency, TTFT (time to first token), Throughput

**QUALITY:** Accuracy, Factuality, Relevance, Task completion, Schema compliance, User satisfaction

**GOVERNANCE:** Policy violations, Cache isolation failures, Optimization failures, Fallback rate, Unverified accounting, Regression rate

**CONTROL-PLANE EFFECTIVENESS:** Optimization decision accuracy, Optimization overhead, Net optimization savings, Cost per successful outcome, Context utility/token, Tool-call ROI, Sub-agent ROI, Cache ROI, Cache miss diagnosis rate, Reversible-recovery success rate, Policy adaptation success rate, Quality-constrained cost frontier

---

## 33. Anti-Patterns to Avoid

The implementation must avoid **all** of the following:

**Primary anti-patterns:**
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

**Extended anti-patterns:**
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

## 34. Optimization Objective Function

### 34.1 Objective

```
MINIMIZE:
    Total Safe Inference Cost

SUBJECT TO:
    Quality     >= configured minimum
    Correctness >= configured minimum
    Security    >= required policy
    Reliability >= configured SLO
    Freshness   >= required threshold
    Latency     <= configured maximum
    Compliance   = satisfied
    Task completion = satisfied
```

The system should expose the resulting operating point as a **cost/quality/latency frontier**.

> [!NOTE]
> **2026-09-15 Hardening Pass (H04, PS §52.4):** this objective/constraint formulation is a **conceptual and heuristic decision framework** that guides policy design, cost accounting, and evaluation — it is not a specification for a literal real-time mathematical optimizer or constraint solver executed synchronously on every request. Individual decisions are made by policy rules, heuristics, or learned models operating within this framework, at whichever operating mode (Section 42.1) is appropriate. See Section 42.4 for the full net-optimization-value accounting this framework requires.

### 34.2 Final Design Principle

```
OBSERVE + MEASURE + UNDERSTAND + DECIDE + REDUCE + REUSE
+ RETRIEVE + COMPRESS + ROUTE + CONSTRAIN + VERIFY + STOP
+ LEARN + GOVERN
```

**Central question for every request:**

> "What is the cheapest safe execution strategy that can achieve the required outcome at the required quality, latency, freshness, security, and reliability?"

### 34.3 Ultimate Objective

> "The objective is to deliver the required business outcome with the LOWEST SAFE TOTAL INFERENCE COST and the HIGHEST PRACTICAL QUALITY, while maintaining enterprise security, observability, reliability, auditability, and scalability."

---

## 35. Enterprise Optimization Maturity Model

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

## 36. Reference Research and Validation Notes

### 36.1 Validated References

| # | Title | Source |
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

### 36.2 Reference Corrections (from Problem Statement)

- arXiv ID `2501.09136` was identified as an incorrect pairing in an earlier version. It must **not** be retained without correction.
- The OpenAPI paper: corrected identifier is `2405.15729` (not `2405.15728`).

### 36.3 Source Whitepaper Benchmarks (Reference Only -- Not Production Guarantees)

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
| Saved tokens/call | ~119.4M tokens |
| Net annual savings | $253.70 (Sonnet) to $1,666.34 (Opus) per workload |

### 36.4 Evidence Classification Policy

| Classification | Meaning |
|---|---|
| Research evidence | Supports validation as a candidate |
| Provider capability | Supported but semantics vary |
| Local benchmark result | Measured on own workload |
| Production policy | Deployed based on local evidence |
| Production guarantee | Only established by local production evidence |

---

## 37. Cost Model

See Section 18.3 for the full cost model specification.

---

## 38. Performance Model

**Measure all of the following:**

End-to-end latency, Time to first token, Input processing latency, Compression latency, Cache lookup latency, Retrieval latency, Tool latency, Model latency, Output generation latency

An optimization is successful only if the **overall cost/quality/latency trade-off is favorable** for the target workflow.

---

## 39. Final Product Requirements Checklist

The final implementation shall provide all 18 engines/modules:

| # | Module |
|---|---|
| 1 | A policy engine |
| 2 | A token/cost accounting engine |
| 3 | A context management engine |
| 4 | A retrieval optimization engine |
| 5 | A caching/reuse engine |
| 6 | A model routing engine |
| 7 | A reasoning-budget engine |
| 8 | An agent workflow optimization engine |
| 9 | A tool optimization engine |
| 10 | An output optimization engine |
| 11 | A benchmark/evaluation harness |
| 12 | A quality gate |
| 13 | A fallback mechanism |
| 14 | Security and tenant isolation |
| 15 | Audit logging |
| 16 | Dashboards/reports |
| 17 | Regression testing |
| 18 | Provider/model adapters |

**The framework must be capable of answering for every production request:**

"What did we send?" / "What did we remove?" / "What did we compress?" / "What did we reuse?" / "What did we cache?" / "Which model did we use and why?" / "How many tool/model calls did we avoid?" / "How much did optimization cost?" / "How much did it save?" / "Did quality remain acceptable?" / "Can we prove the result?"

---

## 40. Internal Consistency Check

> [!IMPORTANT]
> This section documents the internal consistency review. Implementation may not begin until all checks pass.

### 40.1 Objectives Coverage

| Objective | Covered By |
|---|---|
| OBJ-001 (Reduce input tokens) | Sec 7.4 Sanitizer, 7.9 Context Pruner, 7.10 Deduplicator, 7.13 Compressor, 7.16 Query Compressor |
| OBJ-002 (Reduce output tokens) | Sec 7.19 Output Length Controller, 7.18 Output Schema Selector, 13.3 AR-003 |
| OBJ-003 (Bound context growth) | Sec 7.15 Soft Reset/Budgeter, 9.5 Progressive Compaction, 9.6 Hierarchical Memory |
| OBJ-004 (Reduce redundant propagation) | Sec 7.10 Deduplicator, 12.5 Handoff Compression, 9.1 Dependency Graph |
| OBJ-005 (Reduce model calls) | Sec 7.23 Cascade, 7.7 Semantic Cache, 7.8 Prompt Cache, 12.6 Early Exit |
| OBJ-006 (Reduce tool calls/tokens) | Sec 11.1 TE-001, 7.21 Tool Output Filter, 11.2 TE-002, 7.22 Tool Result Cache |
| OBJ-007 (Reuse results) | Sec 7.7 Prompt Cache, 7.8 Semantic Cache, 7.22 Tool Result Cache, 4.4 DA-022 |
| OBJ-008 (Route cheapest capable model) | Sec 7.1 Model Router, 7.23 Cascade, 13.1 AR-001 |
| OBJ-009 (Dynamic budgets) | Sec 7.3 Reasoning Budget, 7.15 Context Budgeter, 8.3 OI-003 |
| OBJ-010 (Stop agentic workflows) | Sec 7.20 Agent Stop Controller, 12.6 AL-006, 12.2 AL-002 |
| OBJ-011 (Auditable ledger) | Sec 18 Token and Cost Accounting, 22 Observability |
| OBJ-012 (Org-specific benchmarks) | Sec 23 Benchmarking Framework, 28 Rollout Strategy |
| OBJ-013 (Multi-provider) | Sec 4.3 Common Representation, 16 Provider Profiles, 27 Deployment |
| OBJ-014 (Security/tenant boundaries) | Sec 20 Security Requirements, 7.7/7.8 cache isolation/auth |

**All 14 objectives covered. PASS**

---

### 40.2 Acceptance Criteria Coverage

All 38 ACs (AC-001 through AC-038) are covered by corresponding sections as follows:

AC-001 -> Sec 18 ledger; AC-002 -> Sec 18.3 cost model; AC-003 -> Sec 19 + 26; AC-004 -> Sec 16 + 18; AC-005 -> Sec 18.1 ledger fields; AC-006 -> Sec 7.7; AC-007 -> Sec 7.8; AC-008 -> Sec 7.12; AC-009 -> Sec 7.9 + 7.13; AC-010 -> Sec 7.1 + 7.23; AC-011 -> Sec 7.18 + 7.19; AC-012 -> Sec 7.20 + 12.2; AC-013 -> Sec 7.21 + 11.6; AC-014 -> Sec 20; AC-015 -> Sec 24; AC-016 -> Sec 21; AC-017 -> Sec 23; AC-018 -> Sec 36.3 + 36.4; AC-019 -> Sec 8.2; AC-020 -> Sec 18.2; AC-021 -> Sec 8.4; AC-022 -> Sec 9.2; AC-023 -> Sec 9.3; AC-024 -> Sec 9.4; AC-025 -> Sec 9.5; AC-026 -> Sec 11.3; AC-027 -> Sec 11.1; AC-028 -> Sec 12.3; AC-029 -> Sec 15.1 + 15.2; AC-030 -> Sec 15.3; AC-031 -> Sec 15.5; AC-032 -> Sec 16; AC-033 -> Sec 8.5; AC-034 -> Sec 14.1; AC-035 -> Sec 9.7; AC-036 -> Sec 6.1 three-layer architecture; AC-037 -> Sec 8.1 + 22; AC-038 -> Sec 21.

**All 38 ACs covered. PASS**

---

### 40.3 Security Requirements Coverage

SEC-001 -> Sec 7.17 Prompt Assembler preserves security instructions; SEC-002 -> Sec 7.7/7.8 authorization-aware keys; SEC-003 -> Sec 7.7 tenant/workspace isolation; SEC-004 -> Sec 26 data retention + 7.7 "no prohibited caching"; SEC-005 -> Sec 7.16 preserve safety/security + 14.1 Compression Contracts; SEC-006 -> Sec 11.6 TE-006 retain auth/audit fields; SEC-007 -> Sec 7.8 authorization checks + freshness; SEC-008 -> Sec 18 ledger + 22 observability + 9.4 provenance; SEC-009 -> Sec 26 config; SEC-010 -> Sec 16 provider profiles.

**All 10 SEC requirements covered. PASS**

---

### 40.4 Developer-Agent Module Coverage

All 25 DA modules (DA-001 through DA-025) are specified in Section 4.4.

**All 25 DA modules present. PASS**

---

### 40.5 Non-Functional Requirements Coverage

NFR-001 -> Sec 19 + 14; NFR-002 -> Sec 20; NFR-003 -> Sec 20 + 26; NFR-004 -> Sec 20 + 7.7; NFR-005 -> Sec 4.3 + 16; NFR-006 -> Sec 22 + 18; NFR-007 -> Sec 21; NFR-008 -> Sec 27 + 22; NFR-009 -> Sec 8.2; NFR-010 -> Sec 8.1; NFR-011 -> Sec 7.25; NFR-012 -> Sec 26; NFR-013 -> Sec 24 + 30.

**All 13 NFRs covered. PASS**

---

### 40.6 Optimization Domains Coverage

All 19 domains (A through S) are covered by corresponding pipeline components in Sections 7-15.

**All 19 optimization domains covered. PASS**

---

### 40.7 Anti-Pattern Coverage

All 13 primary anti-patterns and all 15 extended anti-patterns are listed in Section 33.

**All 28 anti-patterns documented. PASS**

---

### 40.8 Pipeline Stage Consistency

All 24 stages in Section 7.25 are cross-referenced to architecture components in Section 6.2 with no gaps or conflicts.

**Pipeline stages consistent with architecture. PASS**

---

### 40.9 Known Open Items (Design-Time Decisions, Not Gaps)

| # | Item | Resolution |
|---|---|---|
| 1 | Exact Token-Aware Ranker utility formula | Configurable, benchmark-driven |
| 2 | Exact Context ROI formula | Configurable, benchmark-driven |
| 3 | Default semantic cache similarity threshold | Configurable per tenant/workflow |
| 4 | Provider-specific cache minimum token counts | Resolved by provider profile abstraction (Sec 16) |
| 5 | Exact compaction threshold values | Configurable and model-specific (Sec 9.5) |
| 6 | "Where supported" qualifier on DA-007, DA-022, TE-003, TE-004 | Integration-surface-dependent; requirement is valid |
| 7 | Exact "sufficient" definition for agent early exit | Policy-driven, configurable threshold |

**Open items are design-time configuration decisions. PASS**

---

### 40.10 Architectural Principle Consistency Check (Rev 1.1 Amendment)

**New principle added in Sec 1.3:**

> The optimizer should be allowed to conclude that the best LLM call is no LLM call at all.

**Traceability — components that give effect to this principle:**

| Component | How it implements "no LLM call" |
|---|---|
| Sec 7.7 T1.6 Prompt Cache | Exact cache hit returns a prior result without any model call |
| Sec 7.8 T1.7 Semantic Cache | Semantic hit returns a reused result without any model call |
| Sec 7.22 T3.3 Tool Result Cache | Deterministic tool result served from cache, bypassing inference |
| Sec 8.1 OI-001 Optimization Decision Engine | Explicitly decides whether inference should occur at all for each request |
| Sec 8.2 OI-002 Cost-of-Optimization Controller | Skips inference when expected net value is negative or insufficient |
| Sec 7.20 T3.1 Agent Stop Controller | Terminates agent loop before further LLM calls when objective is satisfied |
| Sec 12.6 AL-006 Agent Task Value / Early Exit | Stops the agent (no further LLM calls) when additional calls have low expected value |
| Sec 13.4 AR-004 Verifier-Guided Escalation | Deterministic verifiers (tests, schemas, rules) may confirm correctness without re-inference |
| Sec 7.25 Optimization Order Stage 1-2 | Exact and semantic cache checks execute before any LLM is reached |
| Sec 21 Failure and Fallback Model | Fallbacks may return policy-safe results without LLM calls |

**Anti-pattern alignment:** The principle is consistent with Sec 33 anti-patterns:
- "Always using the cheapest model" is already prohibited -- this principle extends that to "always calling a model".
- "Running every optimization on every request" is already prohibited -- this principle extends that to "always running inference on every request".

**OBJ alignment:** Directly supports OBJ-005 (reduce unnecessary model calls) and OBJ-007 (reuse results where safe).

**No conflict with existing requirements:** The principle does not contradict any existing OBJ, AC, SEC, or NFR. It strengthens OBJ-005 and OBJ-007 at the architectural level and is fully consistent with the optimizer objective function in Sec 34 (minimize total safe inference cost).

**Result: New architectural principle is internally consistent and fully traceable. PASS**

---

### 40.11 Final Consistency Check Result (Rev 1.1)

| Check | Result |
|---|---|
| All 14 OBJs covered | PASS |
| All 38 ACs covered | PASS |
| All 10 SEC requirements covered | PASS |
| All 25 DA modules specified | PASS |
| All 13 NFRs covered | PASS |
| All 19 optimization domains covered | PASS |
| All 28 anti-patterns documented | PASS |
| Pipeline stage/architecture consistency | PASS |
| Three-layer architecture integrity | PASS |
| Cost model completeness | PASS |
| Fallback for every stage | PASS |
| No requirement silently removed | PASS |
| Open items are design-time decisions | PASS |
| Sec 1.3 architectural principle traceable and consistent | PASS |

> [!IMPORTANT]
> **INTERNAL CONSISTENCY CHECK: PASSED (14/14)**
>
> All requirements from the problem statement are represented in this specification.
> The Rev 1.1 amendment (Central design & final architectural principle, Sec 1.3) is internally consistent,
> fully traceable to existing components, and introduces no conflicts with any OBJ, AC, SEC, or NFR.
> No requirement has been silently removed, simplified, reinterpreted, or replaced.
> Open items are design-time configuration decisions, not specification gaps.
>
> This document is ready for stakeholder review and approval.
> **Implementation must not begin until this document has been explicitly approved.**

---

## 41. Dynamic Execution State & Context Lifecycle Management

**Amendment:** EAIOC-SPEC-001 Rev 1.2 — Hardening Pass, 2026-09-10
**Traceability:** PS §51; ARCH §46 (EAIOC-ARCH-001 Rev 1.1); INTF §42 (INTF-050–062)

> [!IMPORTANT]
> This section is additive. It introduces no conflicts with Sections 1–40.
> All existing requirements remain valid and unchanged.
> This section hardens the specification against abnormal enterprise execution conditions
> that are implicit in the static pipeline model but were not previously made explicit.

---

### 41.1 Foundational Requirement: Versioned Stateful Execution

**Requirement:** The Control Plane must treat every execution as a versioned, stateful lifecycle, not as a single immutable request/response transaction.

An execution lifecycle spans from request admission to terminal state commitment. Between these two points, the following entities may mutate and require explicit handling:

| Mutable Entity | Mutation Events |
|---|---|
| Context | Tokens added, removed, compressed, reordered, pruned |
| Workflow | Steps added, removed, reordered, paused, superseded |
| Permissions | User role changed, token revoked, scope reduced |
| Optimization Policy | Hot-reload, security policy update |
| Model Availability | Provider outage, rate-limit activation, capability downgrade |
| Resource Budgets | Token ceiling reduced, cost cap adjusted |
| External State | Tool outputs changed, files modified, knowledge base updated |

**Every mutation must be:**
1. Detected and logged
2. Version-stamped (monotonically increasing version number)
3. Assessed for impact on in-flight work
4. Reconciled before the next optimization decision is made

**Invariant:** The system must NEVER make an optimization decision using a stale snapshot of context, policy, permissions, or resource state.

---

### 41.2 Execution State Components

#### 41.2.1 Execution State Manager (ESM)

The ESM is responsible for owning and persisting all mutable execution state throughout the lifecycle.

**Owned State:**

| Domain | Items |
|---|---|
| Control Plane State | OptimizationPlan version, pipeline stage status, ReversibilityRecord set, token ledger, policy snapshot, quality gate results |
| Context State | Logical Task Context (full), Model-Admitted Context (per-call subset), context version sequence, dependency graph, provenance records |
| Workflow State | Step index, step graph, completed_actions set, unresolved_questions set, checkpoint registry, sub-agent registry, suspension markers |

**Operations:**
- `snapshot(execution_id)` → current state snapshot
- `apply_mutation(execution_id, mutation)` → version-bumps and reconciles
- `checkpoint(execution_id)` → persists a resumable checkpoint
- `reconcile(execution_id)` → verifies state consistency before next step
- `terminal(execution_id, terminal_state)` → closes the execution lifecycle

#### 41.2.2 Context Version Manager (CVM)

Extends CL-001 and CL-002 with versioned mutation awareness.

**Requirements:**
- Every context mutation increments `context_version` (monotonically, per execution).
- Mutations are recorded as `ContextMutation` records with: `mutation_type`, `affected_item_ids`, `version_before`, `version_after`, `timestamp`, `triggered_by`.
- `model_admitted_context` is always assembled from the current `logical_task_context` at inference time — it is never a cached copy of a prior assembly.
- `logical_task_context` is persistent across all steps of the execution.
- `model_admitted_context` is ephemeral — constructed fresh for each inference call.

#### 41.2.3 Workflow Version Manager (WVM)

**Requirements:**
- Every step addition, removal, or reordering increments `workflow_version`.
- `completed_actions` is an append-only set — once an action is recorded as complete, it is never removed.
- The `completed_actions` set is consulted before every sub-agent spawn and before every tool call to prevent re-execution.
- `unresolved_questions` is updated after each step; drives agent continuation decisions.

---

### 41.3 Logical Task Context vs. Model-Admitted Context

This distinction is architecturally mandatory and must be enforced at implementation boundaries.

| Property | Logical Task Context | Model-Admitted Context |
|---|---|---|
| Scope | All information relevant to the task | Subset admitted to one inference call |
| Persistence | Persists across all steps | Ephemeral — rebuilt per inference call |
| Size constraint | May exceed any single model's window | Must fit within the model's context window |
| Eviction semantics | Only via explicit eviction with ReversibilityRecord | Assembly pruning is non-destructive |
| Dependency tracking | Full dependency graph maintained | Dependency graph informs assembly |

**Consequence for pruning implementation:**
- T1.9 (Context Pruner) operating on Logical Task Context must produce explicit `ReversibilityRecord` entries for every eviction.
- T1.9 operating at assembly time (to fit Model-Admitted Context) is non-destructive — it does not modify Logical Task Context.

---

### 41.4 Context Overflow Management

When Logical Task Context exceeds the configured ceiling, the following tiered eviction protocol is mandatory:

**Protection Tiers:**

| Tier | Content | Eviction Policy |
|---|---|---|
| Tier 0 (Immutable) | SEC instructions, safety constraints, audit-required fields | NEVER evictable |
| Tier 1 (Protected) | Developer instructions, decisions, commitments, constraints | Only on task terminal state |
| Tier 2 (High-Value) | Recent conversation turns, active plan, unresolved questions | Compressible; not evictable while active |
| Tier 3 (Standard) | Retrieved context, tool outputs, rolling history | Standard pruning and compression |
| Tier 4 (Evictable) | Redundant, stale, or low-relevance items | Evicted first |

**Eviction Protocol:**
1. Identify Tier 4 candidates via `ContextDependencyGraph.impact_of_removal()`.
2. Evict dependency-free candidates.
3. If overflow persists: compress Tier 3.
4. If overflow persists: compress Tier 2 (never evict while active).
5. If overflow persists after Tier 2 compression: return `BUDGET_OVERFLOW` with section breakdown.
6. **Never proceed to inference with silent truncation.**

**Context Expansion:**
When a previously evicted item is found necessary:
1. Raise `ContextExpansionRequest`.
2. Evaluate budget impact.
3. If budget permits: re-admit with freshness validation.
4. If budget insufficient: suspend workflow → surface `EXPANSION_BUDGET_EXCEEDED` to caller.

---

### 41.5 Interruption and Partial Execution

Multi-step executions must handle all of the following interruption causes:

| Cause | Required Behavior |
|---|---|
| User cancellation | Checkpoint immediately; complete current atomic unit; return PARTIAL; preserve checkpoint |
| Timeout | Checkpoint; return PARTIAL; mark audit record accurately |
| Provider outage | Activate circuit breaker; checkpoint; attempt failover; if unavailable return PARTIAL |
| Policy change | Policy pinned at ingress — no mid-execution change; security policy exceptions force suspend+revalidate |
| Budget exhaustion | Return PARTIAL with per-section budget breakdown; never silently overspend |
| Security event | Fail-closed immediately; log; reject |

**Partial execution result requirements:**
- `status` must accurately reflect the interruption cause (not `COMPLETED`)
- `completion_pct` must reflect actual measured progress
- `completed_actions` must be recorded in the result
- `unresolved_questions` must be listed
- The result must be usable for resume

---

### 41.6 Checkpointing and Resumability

Any multi-step workflow of 3 or more steps **MUST** support checkpointing.

**Checkpoint Record (minimum required fields):**
```
execution_id         (immutable — never changes)
execution_version    (at checkpoint time)
context_version      (at checkpoint time)
workflow_version     (at checkpoint time)
completed_actions    (append-only set)
unresolved_questions (current set)
token_ledger_snapshot
policy_version       (at checkpoint time)
model_selected       (at checkpoint time)
reversibility_records (all records written so far)
checkpoint_timestamp
```

**Resume Protocol:**
1. Load checkpoint.
2. Re-validate permissions against current authorization state.
3. Re-validate policy version — if changed, apply only if additive; if restrictive, revalidate plan.
4. Re-validate model availability.
5. Consult `completed_actions` — skip all completed steps.
6. Validate freshness of external context items.
7. Reconcile: if objective is already satisfied by completed actions, return result without re-execution.
8. Continue from first unresolved step.

If steps 2–6 produce failures: surface `RESUME_PRECONDITION_FAILED` with the specific failed precondition.

**Requirement:** Resume must NEVER blindly replay completed steps.

---

### 41.7 Supersession

A supersession occurs when a new request makes an in-flight execution obsolete.

**Supersession handling requirements:**
1. Mark the superseded execution as `SUPERSEDED` in the audit record.
2. Finalize the superseded token and cost accounting separately.
3. Issue a fresh `execution_id` for the new execution.
4. Re-validate freshness of any shared context items before admitting them to the new execution.
5. Surface `completed_actions` from the superseded execution to the new execution (after freshness validation) to prevent redundant re-work.

---

### 41.8 Dynamic Permission and Policy Changes

**Permission changes:**
- Cache entries (prompt cache, semantic cache, tool result cache) for the affected user/role must be invalidated on scope reduction.
- In-flight tool executions must be revalidated before results are admitted to context.
- Agent steps depending on now-inaccessible data must be marked `BLOCKED` and surfaced.

**Policy hot-reload:**
- Optimization policy changes take effect on the NEXT admitted request — never mid-execution.
- `policy_version` is snapshotted at ingress and stored in `OptimizationPlan.policy_version`.
- **Exception:** SEC-001–010 security policy upgrades (increases in restriction) may force an in-flight suspend+revalidate+resume-or-cancel cycle.

---

### 41.9 Stale Result Protection

Stale results must be **rejected** (not served) in all of the following cases:

| Case | Detection | Required Action |
|---|---|---|
| Semantic cache hit with `freshness_valid = false` | Freshness check on every hit | Re-execute; do not serve |
| Tool result cache hit after underlying data changed | Dependency-aware invalidation | Re-execute tool |
| Prompt cache hit after `policy_version` changed | Policy version comparison on every hit | Re-fetch prompt |
| Resumed execution with stale external context | Freshness validation on resume | Re-fetch items |
| Sub-agent result referencing changed files/APIs | Context invalidation events (DA-024) | Re-run sub-agent or targeted steps |

---

### 41.10 Fail-Safe Classification

| Failure Domain | Mode | Behavior |
|---|---|---|
| Optimization stage failure | Fail-Open | Pipeline continues with unoptimized content; failure is logged |
| Security check failure (SEC-001–010) | Fail-Closed | Request rejected; security event logged; never pass-through |
| PII detection failure | Fail-Closed | Request rejected or classified CONFIDENTIAL conservatively |
| Authorization check failure | Fail-Closed | Request rejected; access violation logged |
| Mandatory audit record write failure | Fail-Closed | Optimization stage blocked; unoptimized content used |
| Reversibility record write failure | Fail-Closed | Destructive transformation blocked |

---

### 41.11 New Enterprise Objectives (OBJ-015 to OBJ-022)

The following objectives extend the 14 mandatory objectives of Section 3:

| ID | Objective |
|---|---|
| **OBJ-015** | Maintain versioned, mutable execution state across the full request lifecycle |
| **OBJ-016** | Support checkpointing and resumability for all multi-step workflows (≥ 3 steps) |
| **OBJ-017** | Detect and reconcile context, permission, policy, and model-availability mutations in-flight |
| **OBJ-018** | Distinguish Logical Task Context from Model-Admitted Context and maintain both separately |
| **OBJ-019** | Apply a tiered eviction protocol for context overflow with no silent truncation |
| **OBJ-020** | Handle all six interruption causes (cancellation, timeout, outage, policy change, budget exhaustion, security event) with structured partial results |
| **OBJ-021** | Enforce stale result rejection at all cache types and at execution resume |
| **OBJ-022** | Validate scenario completeness (normal and abnormal) as a first-class production gate |

---

### 41.12 Scenario Validation Requirements

Before any pipeline stage is declared production-ready, both scenario categories must be verified:

**Normal scenarios:** End-to-end pipeline execution; quality gates pass; ledger balances; fallbacks inactive.

**Abnormal scenarios (minimum mandatory coverage):**

| Scenario | Primary EC Reference |
|---|---|
| Context overflow at each tier boundary | EC-011, EC-055 |
| Context expansion after eviction | §51.4 |
| Interruption at each pipeline stage | EC-038, EC-064 |
| Permission change during execution | EC-034, §51.8 |
| Policy hot-reload during execution | EC-068 |
| Provider outage at routing, cascade, retry | EC-038, EC-064 |
| Partial execution resume | §51.6 |
| Supersession during multi-step execution | §51.7 |
| Stale result rejection at each cache type | EC-027, EC-030, EC-031, EC-034 |
| Dependency-cascade failure in context eviction | EC-022, EC-023 |
| Budget exhaustion at each section | EC-055 |
| Adversarial injection through compressed context | EC-057 |
| Cross-tenant isolation under concurrent load | EC-028, EC-060 |
| Loop detection and graceful loop-break | EC-045 |
| Sub-agent handoff with oversized context | EC-049 |
| Quality gate failure after optimization | EC-020, EC-063 |
| Optimization overhead inversion | EC-065 |
| Policy violation detected at resume | §51.8 |

---

### 41.13 Internal Consistency Check Update

The following check rows are added to the Section 40 consistency table:

| Check | Status |
|---|---|
| OBJ-015 to OBJ-022 traceable to PS §51 | PASS |
| Tiered eviction protocol consistent with SEC-001, §51.4 Tier 0 | PASS |
| Policy snapshot requirement consistent with EC-068 | PASS |
| Fail-closed classification consistent with SEC-001–010 | PASS |
| Logical/Model-Admitted Context distinction consistent with CL-001, CL-002 | PASS |
| Checkpoint requirement consistent with AL-001 to AL-006 | PASS |
| Stale result protection consistent with EC-027, EC-030, EC-031, EC-034 | PASS |
| Scenario validation requirements consistent with edge-cases.md §40 DoD | PASS |
| No existing requirement (OBJ-001–014, AC-001–038, SEC-001–010, NFR-001–013) modified | PASS |

---

### 41.14 Relationship to Agent-Owned Memory and Cross-Execution Shared State

**Added by the 2026-09-15 Architecture Hardening Pass (PS §51.12).**

Sections 41.1–41.13 define execution truth for a single execution. Two extensions are required and are specified normatively in Section 42:

- An agent may maintain its own working memory (short-term task state, scratch reasoning, local caches). That memory is never authoritative over the ESM/CVM/WVM-owned execution state, authorization state, policy state, or context/workflow version defined in Section 41.2. See Section 42.9.
- Multiple agents, sub-agents, or workflow executions may read or mutate shared resources (files, tickets, shared memory, external systems) concurrently. The single-execution reconciliation model in Sections 41.1 and 41.7–41.9 extends to cross-execution concurrency, staleness, and idempotency. See Section 42.12.

---

*Section 41 continues into Section 42, which propagates the 2026-09-15 Architecture Hardening Pass. Section 41 itself remains unmodified except for this cross-reference addendum.*

---

## 42. Architecture Hardening Amendment — Operating Model, Governance, and Trust Boundaries

**Amendment:** EAIOC-SPEC-001 Rev 1.3 — Hardening Pass, 2026-09-15
**Traceability:** PS §52 (H01–H20), OBJ-023–035, SEC-011–016, NFR-014, AC-039–053

> [!IMPORTANT]
> This section is additive. It introduces no conflicts with Sections 1–41.
> All existing requirements remain valid and unchanged except where a direct amendment is
> called out inline in Sections 1.3, 3, 4.2, 6.2, 12.2, 17, 20, 25, 31, 33, 34.1, and 41.14 above.
> This section translates the twenty hardening requirements of PS §52 (H01–H20) from
> product-level requirements into engineering behavior: state, contracts, decision logic,
> failure behavior, and measurement.

Every subsection below follows the same structure: **Requirement**, **Engineering Behavior**,
**Failure Behavior**, **Observability**, **Traceability**.

---

### 42.1 Control Plane Operating Model (H01)

**Requirement:** Every optimization/decision class must declare which of three operating modes it supports — SYNCHRONOUS (request-path), ASYNCHRONOUS/PRECOMPUTED, or HYBRID (precomputed lookup + synchronous revalidation) — and Control Plane decision latency/compute overhead is itself an input to net-value accounting (Section 42.4), not an externality.

**Engineering Behavior:**
- Each pipeline component specification (Sections 7, 8, 9, 10, 11, 12, 13, 16) must carry a declared `operating_mode ∈ {SYNC, ASYNC, HYBRID}` attribute.
- For `ASYNC` and `HYBRID` components, the artifact they consult (provider profile, repository map, cache pre-warm state, routing policy compilation, pricing table, cache-fragmentation diagnosis, regression-detection state) carries its own freshness/version metadata per Section 42.16's stale-state discipline.
- For `HYBRID` components, a synchronous freshness/confidence check (per Section 41.9 Stale Result Protection and Section 42.6's calibration requirement) determines whether the precomputed artifact is used as-is or triggers synchronous recomputation.
- The mode assignment for a given component is itself a policy-driven decision subject to the same net-value accounting as any other optimization choice (Section 42.4) — choosing SYNC where HYBRID would achieve equivalent safety/quality at lower latency/cost is an instance of the "expensive optimizer" anti-pattern (Section 33).

| Mode | Applies when | Example components |
|---|---|---|
| SYNC | Decision depends on request-time-only information (actual prompt, actual retrieved context, actual permission state) | T1.1 Sanitizer, T1.9/T1.10 Pruner/Deduplicator (assembly-time), T3.1 Agent Stop Controller |
| ASYNC | Underlying information changes slowly relative to request volume | Section 16 Provider/Model Profiles, DA-003 Repository Map, CE-004 Cache Pre-Warming, EL-005 Regression Detector |
| HYBRID | A cached/precomputed artifact is consulted synchronously with a freshness/confidence gate | T1.6/T1.7 Prompt/Semantic Cache, CE-003 Cache Fragmentation Detector, AR-004 Verifier-Guided Escalation |

**Failure Behavior:** if a HYBRID component's synchronous validation step cannot complete within its latency budget (Section 42.3), the component fails open per Section 21 (falls back to SYNC recomputation or the unoptimized path), not to an unvalidated precomputed result.

**Observability:** the ledger (Section 18, extended in Section 42.21) records, per decision, which operating mode was used and whether a HYBRID decision triggered synchronous revalidation.

**Traceability:** PS §52.1; OBJ-023; AC-039.

---

### 42.2 Advisor, Enforcer, and Execution-Owner Boundary (H02)

**Requirement:** Every Control-Plane decision or side effect must be classified as exactly one of ADVISORY, ENFORCEMENT, or EXECUTION-OWNERSHIP, and the classification must be recorded independently per decision.

**Engineering Behavior:**

| Category | Definition | Who performs the resulting action |
|---|---|---|
| ADVISORY | Control Plane recommends (model choice, context reduction, cache reuse, stop/continue signal) | Calling agent/application/workflow decides and acts |
| ENFORCEMENT | Control Plane blocks, requires, rewrites, or gates within policy authority (security rejection, output-budget cap, unauthorized-context refusal) | Calling agent/application still performs the (now-constrained) action |
| EXECUTION-OWNERSHIP | Control Plane itself performs an action with an external side effect (cache write, TE-004 programmatic tool execution, checkpoint commit) | Control Plane is directly responsible for correctness/reversibility |

- A component crossing from ADVISORY/ENFORCEMENT into EXECUTION-OWNERSHIP (a programmatic tool-execution aggregator, an automatic cache write, an automatic checkpoint commit) inherits the reversibility (CL-004), audit (Section 42.15), and fail-safe classification (Section 41.10) requirements applicable to that action.
- Task planning, business logic, and the decision to perform an irreversible external action belong to the Agent/Workflow/External System by default; the Control Plane advises or enforces around that planning and does not silently assume ownership of it.
- A single request may pass through all three categories at different pipeline stages (enforcement at the security gate, advisory at model routing, execution-ownership at a tool-result cache write); each is recorded independently.

**Failure Behavior:** if a component's ownership category cannot be determined at configuration time, it defaults to ADVISORY (the least-authority category) until explicitly declared otherwise — the system must never default to EXECUTION-OWNERSHIP by omission.

**Observability:** the decision-explanation record (Section 42.15) includes the ownership category for every logged decision.

**Traceability:** PS §52.2; OBJ-024; AC-040.

---

### 42.3 Control Plane Self-Protection (H03)

**Requirement:** The Control Plane enforces its own latency and processing/cost budgets, detects its own overload, sheds optimization depth before correctness or security, and degrades to a deterministic safe fallback.

**Engineering Behavior:**
- **Latency budget:** every SYNC/HYBRID decision (Section 42.1) has a configurable maximum added latency; on budget exhaustion the stage fails open per Section 21 rather than blocking the request.
- **Processing/cost budget:** Control Plane compute and provider-API usage (e.g., an LLM-based compressor/classifier) is metered and bounded per request and per tenant; this consumption is the `Optimization Compute Cost` term in Section 42.4's net-benefit accounting.
- **Backpressure / optimization-depth shedding:** OI-003 (Adaptive Optimization Depth Controller, Section 8.3) is the primary shedding mechanism; under sustained overload it must be able to step the policy tier down to LOW even for requests that would otherwise warrant MEDIUM/HIGH depth.
- **Overload detection:** queue depth, latency percentile, and error rate are monitored; detected overload triggers load-shedding of optional stages, never silent degradation of quality or security checks.
- **Deterministic safe fallback:** when the Control Plane itself cannot complete processing, the fallback is the same deterministic, previously-defined safe path as Section 21's failure/fallback model, extended to cover Control-Plane self-failure (not only individual optimization-stage failure).

**Failure Behavior:** self-protection failures are optimization failures (fail-open, Section 41.10) unless the overload condition would otherwise cause a security/authorization/PII check to be skipped — in that specific case the affected request fails closed (security and policy enforcement are never sacrificed to relieve Control-Plane load; see `conventions.md` §16.2 for the general fail-open/fail-closed rule this restates for self-protection specifically) rather than proceeding unchecked under load.

**Observability:** queue depth, per-stage latency percentiles, overload events, and depth-shedding events are logged dimensions in Section 22.

**Traceability:** PS §52.3; OBJ-025; NFR-014; AC-041.

---

### 42.4 Verified Net Optimization Economics (H04)

**Requirement:** Net Optimization Value accounting must include all listed cost/benefit terms; a technique is counted as a saving only when the full accounting is net-positive and quality gates (Section 19) pass; unmeasurable terms are marked `UNVERIFIED` and excluded from reported savings.

**Engineering Behavior — required terms:**

| Category | Terms |
|---|---|
| Benefit | Tokens saved, model/inference cost saved |
| Overhead | Optimization compute overhead (Section 42.3), retrieval cost, routing cost, cache overhead (write/storage/invalidation, CE-001) |
| Cost | Additional latency introduced by the optimization itself, retries/escalation cost, downstream/tool cost where the optimization changes tool usage |
| Risk | Quality degradation weighted by the applicable quality gate, task failure/regression attributable to the optimization |

- Derived metrics: gross savings, optimization overhead, net savings, net optimization value, cost per successful outcome, tokens per successful outcome, break-even reuse/cache-hit count (extends Section 18.2/18.3 and Section 8.2 OI-002).
- Where any term cannot be measured, the result state is `UNVERIFIED` per AC-002/Section 18.1 and is excluded from reported savings — never assumed favorable.
- This accounting is a conceptual/heuristic decision framework (Section 34.1 amendment): it specifies what must be measured and how a technique is judged, not a literal per-request constraint-solver computation. Individual net-value estimates may be produced synchronously, asynchronously, or via precomputed heuristics per Section 42.1.

**Failure Behavior:** an accounting-pipeline failure (unable to compute one or more terms) marks the affected request's savings `UNVERIFIED`; it does not block the request itself (this is an optimization-measurement failure, not a security/authorization failure).

**Observability:** Section 18.2's extended ledger is the record of truth; every reported saving must be traceable to a fully-measured or explicitly `UNVERIFIED` accounting row.

**Traceability:** PS §52.4; OBJ-026; AC-042.

---

### 42.5 Enterprise Spend Governance (H05)

**Requirement:** Independently configurable spend controls at tenant, organization, user, and application scope, with runaway-cost detection, circuit breakers, budget-aware routing, and safe exhaustion behavior — never substituting for security/authorization/policy enforcement.

**Engineering Behavior:**
- **Scope model:** each of {tenant, organization, user, application} may define an absolute and/or rate-based spend limit, independently.
- **Runaway-cost detection:** cost-acceleration anomaly detection (e.g., an agent loop or sub-agent fan-out driving spend far above historical baseline) must act *before* the configured limit is exhausted, not only after.
- **Circuit breakers:** on limit breach or detected runaway pattern, spend for the affected scope is halted/throttled while other scopes remain unaffected (tenant isolation boundary, `conventions.md` §13.2).
- **Budget-aware routing/execution:** T0.1 Model Router, T0.3 Reasoning Budget Controller, and T3.1 Agent Stop Controller may consume remaining-budget as an input signal.
- **Exhaustion behavior:** identical to Section 41.5's BUDGET EXHAUSTION handling — return PARTIAL, `remaining_budget = 0`, never silently overspend, per-section consumption breakdown.
- **Independence from security:** per SEC-011, a budget check and a security/authorization/policy check are evaluated independently; a within-budget-but-unauthorized request is still rejected, and an authorized-but-over-budget request is still halted.

**Failure Behavior:** a failure in the budget-evaluation path itself (unable to determine remaining budget) is treated conservatively — the system must not assume budget is available; it falls back to a configured safe default (deny or degrade, per policy), never to unconstrained spend.

**Observability:** budget-consumption events, circuit-breaker activations, and runaway-detection triggers are audit records (Section 42.15) at every affected scope.

**Traceability:** PS §52.5; OBJ-027; SEC-011; AC-043.

---

### 42.6 Verifier Confidence and Calibration (H06)

**Requirement:** Every verifier exposes a confidence signal (not only binary pass/fail) wherever the verifier type supports one; a verifier's output is never automatically ground truth; calibration is validated against ground-truth outcomes before production use; below-threshold results follow policy-defined safe behavior.

**Engineering Behavior:**
- Deterministic verifiers (unit tests, schema/type checks, static analysis) are treated as higher-confidence than probabilistic verifiers (LLM-judge semantic-equivalence checks); acceptance thresholds must reflect that difference (extends AR-004, QO-002).
- Calibration is validated against a benchmark set before a probabilistic verifier is trusted to gate an optimization decision (e.g., accepting compressed context as semantically equivalent, or accepting a cascade's low-cost-model output); Section 35's maturity model (LEVEL 0–5) applies to verifiers themselves, not only to the techniques they gate.
- Verifier acceptance behavior is monitored over time; a verifier that starts accepting results it previously rejected (or vice versa) without a corresponding technique change is a drift signal requiring investigation — extends EL-005 (Optimization Regression Detector) to verifier drift specifically.
- Acceptance thresholds are configurable per task/intent/tenant (Section 26).

**Failure Behavior:** below the configured confidence/acceptance threshold, the system applies QO-002's Quality-Aware Fallback (restore previous representation, increase context/reasoning budget, escalate model, or disable the offending optimization) — never silent acceptance.

**Observability:** verifier confidence scores, calibration drift events, and threshold-breach fallbacks are logged per Section 19 (Quality Gates) and Section 22.

**Traceability:** PS §52.6; OBJ-028; AC-044.

---

### 42.7 Data Governance (H07)

**Requirement:** Sensitivity-aware classification, retention, deletion/erasure propagation, and residency handling across every Control Plane surface (request context, optimization artifacts, cache, semantic cache, memory, checkpoint, ledger, logs, traces, audit records, derived artifacts).

**Engineering Behavior:**
- **Classification:** content is classified sensitive (which may include PII/PHI/PCI/secrets where applicable to the deployment) versus non-sensitive before it is admitted to any optimization stage; classification is a Tier 0 (SEC-protected, Section 41.4) concern and is never pruned/compressed/deduplicated out on relevance score or budget pressure alone.
- **Encryption:** sensitive data at rest and in transit follows the organization's configured policy (no encryption algorithm/standard is mandated here beyond what the deployment's policy specifies).
- **Retention:** every cache, memory layer (CL-006), ledger, and log/trace has a configurable retention period; default is the organization's policy, never unbounded retention.
- **Deletion/erasure propagation (SEC-013):** a deletion/erasure request propagates to every surface listed above; the system must be able to report which surfaces still hold data derived from a given source after a deletion request, so propagation can be verified rather than merely asserted.
- **Residency:** where a provider/model profile (Section 16) declares a compliance/data-residency constraint, routing and caching honor it; no specific residency requirement is assumed absent deployment configuration.
- **Subprocessors/external systems:** the same classification/retention/deletion obligations apply to data the Control Plane sends to an external system (provider, tool, MCP server) to the extent the Control Plane controls that data's lifecycle.

> [!CAUTION]
> No specific regulation (e.g., GDPR, HIPAA, PCI-DSS) is asserted to apply by this document. Applicability is established by the deployment's own scope and jurisdiction. This is intentional — see `SOURCE-GAP-ES-01` in Section 42.22.

**Failure Behavior:** a classification failure (unable to determine sensitivity) is treated as a security/integrity failure and fails closed (content is treated as sensitive by default until classified) — this is distinct from an optimization-stage failure and does not fall back to unoptimized processing of unclassified content.

**Observability:** classification decisions, retention-expiry events, and deletion-propagation verification results are auditable (SEC-013, Section 42.15).

**Traceability:** PS §52.7; OBJ-029; SEC-012; SEC-013; AC-045.

---

### 42.8 Coding-Agent Integration Feasibility Tiers (H08)

**Requirement:** Every coding-agent integration declares one of five feasibility tiers; DA-001–DA-025 module applicability is bounded by the declared tier; the exact per-platform mechanism is an architecture decision.

**Engineering Behavior — Tier Model:**

| Tier | Name | Access Characteristics |
|---|---|---|
| 1 | Deep/Native | Embedded/tightly coupled; full model-bound context observable/transformable pre-inference |
| 2 | Gateway/Interception | API/LLM gateway proxy boundary (Integration Mode 1); request/response observable/transformable at that boundary |
| 3 | Plugin/Extension | Runs inside the platform's own extensibility surface; access bounded by that surface |
| 4 | Protocol/Tool-Level | Participates as an MCP server/tool; optimizes only what passes through that protocol surface |
| 5 | Advisory/Observability-Only | Observes only exposed telemetry (logs, usage metrics); can advise, cannot transform/block |

- A **capability-declaration mechanism** (a per-integration manifest/config declaring the tier and, within it, which specific DA-001–DA-025 modules and which pipeline stages are actually reachable) is required at the adapter boundary (Section 4.3 Common Representation Layer).
- The framework must not report or imply full-pipeline optimization coverage for a platform where only Tier 3–5 access exists.

**Failure Behavior:** if a platform's actual access degrades below its declared tier at runtime (e.g., an extensibility API is deprecated), the integration must re-declare a lower tier rather than silently continuing to claim the higher one.

**Observability:** the declared tier and reachable-module set are part of the integration's configuration record and are reportable per integration.

**Traceability:** PS §52.8; OBJ-030; AC-046.

---

### 42.9 Memory Authority (H09)

**Requirement:** Agent-owned working memory is advisory; Control-Plane-owned execution truth (ESM/CVM/WVM, Section 41.2) is authoritative; conflicts are detected and surfaced, never silently resolved in memory's favor.

**Engineering Behavior:**
- Agent memory may hold scratch state, temporary reasoning, working plans, transient observations, and local caches — this is expected and unrestricted.
- Agent memory is never authoritative over: Control Plane execution state (Section 41.2 Domain 1), authorization state, policy state (the pinned `policy_version` of Section 41.8), authoritative external state, or context/workflow version (Section 41.2 Domains 2–3).
- **Conflict detection:** every agent-memory read that could inform a decision affecting execution state carries a `memory_version`/provenance tag; the engine compares it against current ESM-owned state before trusting the memory-derived value.
- **Conflict resolution:** on disagreement (e.g., agent memory says a step is complete but `completed_actions` says otherwise; agent memory holds a permission grant since revoked in current authorization state), the ESM/CVM/WVM-owned state wins, and the disagreement is surfaced (not silently dropped), following the same reconciliation model as resume (Section 41.6): re-validate against current authorization, policy, model availability, and completed-actions state before trusting agent-reported memory.

**Failure Behavior:** an unresolvable conflict (e.g., ambiguous provenance) defaults to treating agent memory as stale/untrusted for that decision, never as authoritative by default.

**Observability:** memory/execution-truth conflicts are logged as a distinct event type in Section 22, separate from ordinary stale-cache events.

**Traceability:** PS §52.9; OBJ-031; AC-047.

---

### 42.10 Reflection and Loop Awareness (H10)

**Requirement:** Loop-waste classification (AL-002) uses expected task value and progress evidence, not iteration count; the same expected-value framework governs both CONTINUE and STOP decisions.

**Engineering Behavior:** see the direct amendment to Section 12.2 (AL-002). AL-001's per-iteration signals (state change, objective progress, information gain, errors introduced/resolved, input/output tokens, tool cost) feed a classifier that distinguishes: productive progress, reflection/self-correction, justified retry, redundant work, oscillation, and failure loop. The Agent Stop Controller (Section 7.20, T3.1) and AL-006 (Early Exit) consult the same classifier output for the STOP decision that AL-002 consults for the CONTINUE/flag decision — there is one expected-value model, not two independent ones.

**Failure Behavior:** if the classifier cannot produce a confident category (insufficient signal), the system defaults to treating the iteration as unclassified/continuing under existing budget constraints (Sections 42.3/42.5) rather than either force-stopping or force-continuing based on an unsupported classification.

**Observability:** iteration classification (progress / reflection / retry / redundant / oscillation / failure-loop) is a ledger field per agent iteration (Section 18.2 extension).

**Traceability:** PS §52.10; AL-001; AL-002 (Section 12.2 amendment).

---

### 42.11 Tool / MCP Trust Boundary (H11)

**Requirement:** Tool/MCP identity, capability metadata, schema integrity, versioning, authorization, availability, and result provenance are security/control-boundary concerns, not merely functional integration concerns.

**Engineering Behavior:**
- **Identity:** a tool/MCP server's identity is authenticated before its capability metadata is trusted.
- **Capability metadata/schema integrity:** schemas consumed by DA-008 (Tool Schema Optimizer) and TE-002 (Tool Argument Optimizer) are validated for integrity; an unexpected schema change between calls is a staleness/trust event, not silently accepted.
- **Authorization:** a tool call's authorization is evaluated independently of TE-001's ROI predictor — cost/token efficiency never substitutes for an authorization check.
- **Versioning:** tool/MCP versions are tracked so that a cached tool result (T3.3) or cached schema is invalidated when the underlying version changes — extends CL-003 (Dependency-Aware Cache Invalidation) to tool/MCP version changes specifically.
- **Availability/staleness:** a tool index used for DA-007/TE-003 dynamic discovery is revalidated on a policy-defined interval; discovered availability is not assumed to persist indefinitely.
- **Malicious/mutated results:** a tool result is untrusted content by default and is subject to the Section 42.14 (prompt-injection/content-integrity) screening in addition to T3.2 (Tool Output Filter) and TE-006 (Tool Result Value Filter).

**Failure Behavior:** an identity/authorization/schema-integrity check failure is a security failure and fails closed (the tool call is refused or the result is quarantined), not an optimization failure that falls back to using the untrusted tool/result anyway.

**Observability:** tool/MCP identity verification, schema-integrity checks, and staleness detections are logged per call.

**Traceability:** PS §52.11; SEC-014; AC-048.

---

### 42.12 Shared State and Cross-Execution Concurrency (H12)

**Requirement:** Concurrent mutation, stale decisions, race conditions, and version conflicts across multiple agents/sub-agents/executions sharing a resource are detected, reconciled, and coordinated; non-idempotent operations are never blindly replayed across executions.

**Engineering Behavior:**
- Shared resources (files, tickets, shared memory per CL-006, external systems) participating in cross-execution coordination carry a version or equivalent conflict-detection token.
- **Concurrent mutation detection:** when two executions mutate the same resource, the second write is not allowed to silently overwrite context the first write depended on — a conflict is raised.
- **Stale decisions:** a decision made from a shared-state snapshot that has since changed is treated as a stale-result case under Section 41.9 and revalidated before another execution relies on it.
- **Version conflicts/reconciliation:** an execution acting on an outdated shared-resource version reconciles before proceeding, using the same reconcile-not-replay discipline as Section 41.6; where two executions' completed actions overlap, the framework reconciles to a single consistent outcome rather than applying both.
- **Locking/coordination:** required only where the shared resource/action is non-idempotent and concurrently reachable — not universally.
- **Idempotency:** non-idempotent operations are not blindly replayed across executions any more than within a single execution's resume path (Section 41.6).

**Failure Behavior:** an unresolvable concurrency conflict (coordination unavailable) blocks the conflicting write/action for the losing execution and surfaces a conflict result — it does not proceed with an unreconciled dual-write.

**Observability:** concurrency conflicts, reconciliation outcomes, and coordination-lock acquisitions/timeouts are logged events distinct from single-execution stale-result events (Section 41.9).

**Traceability:** PS §52.12; OBJ-032; AC-049.

---

### 42.13 Human Approval for Consequential Actions (H13)

**Requirement:** A policy/risk-designated subset of actions requires explicit human approval before execution; not every action is human-approved.

**Engineering Behavior:**
- The designation of which actions require approval is a policy decision, configurable per tenant/workflow (Section 26) — not a fixed universal list.
- An action pending approval follows the same interruption/partial-execution handling as any other suspension cause (Section 41.5): checkpoint, return PARTIAL or an explicit `AWAITING_APPROVAL` status, and resume (Section 41.6) once approval is granted or denied.
- Approval/denial is recorded in the audit record alongside the decision it gates (Section 42.15).
- Approval requests support a configurable timeout; a timed-out approval request follows the policy-defined default (deny, or escalate to a different approver), never silently proceeding as approved.

**Failure Behavior:** if the approval mechanism itself is unavailable, the gated action is blocked (fails closed) rather than proceeding without approval — approval-gating is a security/policy concern, not an optimization stage.

**Observability:** approval requests, grants, denials, and timeouts are first-class audit-record entries (Section 42.15).

**Traceability:** PS §52.13; OBJ-033; SEC-015; AC-050.

---

### 42.14 Prompt Injection and Malicious Content (H14)

**Requirement:** Externally-sourced content (RAG results, search results, tool/MCP results, sub-agent results, retrieved documents/code) is untrusted by default; content-integrity/prompt-injection screening runs before any optimization stage may admit, rank, compress, cache, or use the content to authorize an action; screening failure fails closed.

**Engineering Behavior:**
- Screening precedes admission for every content source listed above, uniformly — not only the end-user's direct input (already covered by T1.1 Sanitizer).
- This ordering resolves any apparent tension with Section 42.1's operating-mode preference for hybrid/precomputed evaluation: precomputation may speed up the screening mechanism itself (e.g., a precomputed classifier), but the screening step itself may not be skipped or deferred until after admission in the interest of latency.
- Scope explicitly includes sub-agent handoffs (AL-005) and MCP tool results (Section 42.11), not only RAG/search content.

**Failure Behavior:** a screening failure (the check itself cannot complete) is a security/integrity failure per Section 41.10 and the fail-open/fail-closed distinction in `conventions.md` — the content is rejected or quarantined, never silently admitted because the check failed.

**Observability:** screening outcomes (pass/reject/quarantine) and screening-unavailable events are logged per content item admitted.

**Traceability:** PS §52.14; OBJ-034; SEC-016; AC-051.

---

### 42.15 Decision Explainability and Audit (H15)

**Requirement:** Context admission/pruning, model/provider selection, cache reuse/rejection, optimization-stage skip, execution block/recovery/supersession, fallback, budget, and human-approval decisions must produce a retrievable explanation, not merely a best-effort one.

**Engineering Behavior:**
- Each of the decision categories above must log, at minimum: the inputs considered, the rule/model/threshold applied, and the resulting action — sufficient to reconstruct the reasoning during audit/debugging, consistent with SEC-008 (all optimization transformations auditable).
- This is a retrievability requirement, not a UI/presentation requirement — surfacing the explanation to an end user by default is an interface/architecture decision, out of scope here.
- The explanation record includes the Section 42.2 ownership category (ADVISORY/ENFORCEMENT/EXECUTION-OWNERSHIP) for the decision it documents.

**Failure Behavior:** a failure to write the explanation/audit record for a decision that requires one is itself a mandatory-audit-record-write failure and is handled per Section 41.10 (fail-closed: the optimization stage is blocked and unoptimized content is used) — an unexplainable decision must not silently execute.

**Observability:** the explanation record is queried by audit/debugging tooling (Section 22); coverage of the required decision categories is a measurable completeness metric.

**Traceability:** PS §52.15; NFR-010 (strengthened); AC-052.

---

### 42.16 Execution State Portability (H16)

**Requirement:** Checkpoint/resume state (Section 41.6) remains interpretable for reconciliation independent of a specific model/provider/session mechanism; resume reconciles across model, provider, permission, policy, context, workflow, external-state, and tool-state changes; RESUME ≠ REPLAY.

**Engineering Behavior:**
- The checkpoint schema of Section 41.6 (`execution_id`, `execution_version`, `context_version`, `workflow_version`, `completed_actions`, `unresolved_questions`, `token_ledger_snapshot`, `policy_version`, `model_selected`, `reversibility_records`, `checkpoint_timestamp`) is defined in provider/session-neutral terms — none of these fields is, or requires, a specific provider's session object or proprietary state format.
- A checkpoint produced under one model/provider selection remains interpretable for reconciliation (Section 41.6 steps 1–8) even if `model_selected` changes on resume (e.g., failover per Section 41.5).
- Where a provider exposes native resumability (e.g., a provider-side conversation/session ID), the Control Plane may use it as an optimization (avoiding re-transmission of cached context) but correctness of resume/reconciliation must not depend on that mechanism being present.
- The exact serialization/storage format is an architecture/interface decision, not specified here.

**Failure Behavior:** identical to Section 41.6's `RESUME_PRECONDITION_FAILED` path — if re-validation (permissions, policy, model availability, completed-actions, freshness) fails, resume is refused with the specific failed precondition rather than proceeding on stale/incompatible state.

**Observability:** checkpoint schema-version compatibility and cross-provider resume success/failure are logged distinctly from same-provider resume events.

**Traceability:** PS §52.16; extends Section 41.6; AC-053.

---

### 42.17 Layer 3 Boundary: Integration, Not Implementation (H17)

**Requirement:** see the direct amendment to Section 17. The Control Plane's role at the inference/serving layer is awareness, integration, routing, policy, and capability negotiation; it does not implement KV-cache engines, continuous batching, speculative decoding, quantization, or prefill/decode disaggregation.

**Engineering Behavior:** for each Layer 3 capability, the Control Plane's own responsibility is limited to: (a) detecting whether the provider/infrastructure exposes the capability (via the provider/model profile, Section 16), (b) routing/selecting to take advantage of it when net-beneficial and policy-compliant (Section 42.4 accounting applies), (c) negotiating capability parameters through the provider adapter (e.g., batching eligibility hints, cache-affinity hints), and (d) measuring the resulting effect kept separate from Layer 1/2 measurement (AC-036).

**Failure Behavior:** if a Layer 3 capability is unavailable or negotiation fails, the request proceeds without that capability (fail-open, optimization-only) — Layer 3 unavailability never blocks a request.

**Observability:** Layer 3 capability detection, negotiation outcomes, and isolated Layer 3 measurement are reported separately from Layer 1/2 metrics per AC-036.

**Traceability:** PS §52.17; Section 17 amendment.

---

### 42.18 Token, Context, Cache, and Inference Optimization Ownership Boundaries (H18)

**Requirement:** six optimization categories are separately owned and separately measured.

| Category | Sections | Ownership |
|---|---|---|
| Prompt/input token optimization | 7.4 (Sanitizer), 7.16 (Query Compressor), 7.6 (Entity Extraction) | Owned and implemented directly |
| Output/reasoning token optimization | 7.3 (Reasoning Budget), 7.19 (Output Length Controller), 7.18 (Output Schema Selector) | Owned and implemented directly |
| Context optimization | Sections 7.9–7.15, 9 (Layer 2) | Owned and implemented directly |
| Cache optimization | 7.7, 7.8, 7.22, Section 10 | Owned and implemented directly, built on provider-exposed cache primitives where available |
| Model/provider routing | 7.1, 7.23, 13.1 | Owned and implemented directly; execution occurs on provider infrastructure |
| Inference-runtime optimization | Section 17 (Layer 3) | Awareness, integration, routing, policy only (Section 42.17) |

**Engineering Behavior:** measurement pipelines (Section 18) must attribute a saving or regression to exactly one of these six categories; a measurement that conflates categories (e.g., reporting a KV-cache provider improvement as a context-optimization saving) is invalid per AC-036.

**Failure Behavior:** N/A (this is a measurement-attribution requirement, not a runtime decision path).

**Observability:** per-category savings/overhead are separately reportable dimensions in Section 18.2/22.

**Traceability:** PS §52.18; Section 1.3 amendment.

---

### 42.19 Research Claims as Evidence, Not Guarantees (H19)

**Requirement:** reaffirmation only — no new engineering obligation is introduced.

This principle is already fully specified in Sections 1.3, 23–24, 35, and 36 (Reference Research and Validation Notes, including the corrected arXiv identifiers) and in AC-018. The 2026-09-15 hardening pass adds no new mechanism: no benchmark percentage from any cited source (LLMLingua, LongLLMLingua, LLMLingua-2, RouteLLM, FrugalGPT, or any future citation) may be hard-coded as an expected production result, and Section 35's maturity model (LEVEL 0 RESEARCH → LEVEL 5 AUTO-TUNED) remains the required path from citation to production claim.

**Traceability:** PS §52.19; no new ID (reaffirmation).

---

### 42.20 Anti-Scope: What the Control Plane Is Not (H20)

**Requirement:** see the direct amendments to Sections 1.3 and 33. The Control Plane is not a replacement agent orchestrator, model-training system, model-runtime infrastructure platform, provider infrastructure, general-purpose developer IDE, or coding-agent UX replacement; it integrates with these systems through the adapter/integration-mode layer (Section 4).

**Engineering Behavior:** any engineering proposal that would require the Control Plane to reimplement one of these adjacent systems, rather than integrate with it through Section 4's adapter layer or Section 42.8's feasibility-tier model, is out of scope for this specification and must be flagged at design-review time, not built silently.

**Failure Behavior:** N/A (this is a scope boundary, not a runtime behavior).

**Observability:** N/A.

**Traceability:** PS §52.20; OBJ-035.

---

### 42.21 Hardening Traceability Matrix

| Hardening | Engineering Spec Coverage | Source Anchor |
|---|---|---|
| H01 | Section 42.1 — Control Plane Operating Model | PS §52.1 / OBJ-023 / AC-039 |
| H02 | Section 42.2 — Advisor/Enforcer/Execution-Owner Boundary | PS §52.2 / OBJ-024 / AC-040 |
| H03 | Section 42.3 — Control Plane Self-Protection | PS §52.3 / OBJ-025 / NFR-014 / AC-041 |
| H04 | Section 42.4 — Verified Net Optimization Economics | PS §52.4 / OBJ-026 / AC-042 |
| H05 | Section 42.5 — Enterprise Spend Governance | PS §52.5 / OBJ-027 / SEC-011 / AC-043 |
| H06 | Section 42.6 — Verifier Confidence and Calibration | PS §52.6 / OBJ-028 / AC-044 |
| H07 | Section 42.7 — Data Governance | PS §52.7 / OBJ-029 / SEC-012 / SEC-013 / AC-045 |
| H08 | Section 42.8 — Coding-Agent Integration Feasibility Tiers | PS §52.8 / OBJ-030 / AC-046 |
| H09 | Section 42.9 — Memory Authority | PS §52.9 / OBJ-031 / AC-047 |
| H10 | Section 42.10 — Reflection and Loop Awareness (+ Sec 12.2 amendment) | PS §52.10 / AL-002 |
| H11 | Section 42.11 — Tool / MCP Trust Boundary | PS §52.11 / SEC-014 / AC-048 |
| H12 | Section 42.12 — Shared State and Cross-Execution Concurrency | PS §52.12 / OBJ-032 / AC-049 |
| H13 | Section 42.13 — Human Approval for Consequential Actions | PS §52.13 / OBJ-033 / SEC-015 / AC-050 |
| H14 | Section 42.14 — Prompt Injection and Malicious Content | PS §52.14 / OBJ-034 / SEC-016 / AC-051 |
| H15 | Section 42.15 — Decision Explainability and Audit (+ NFR-010 amendment) | PS §52.15 / NFR-010 / AC-052 |
| H16 | Section 42.16 — Execution State Portability | PS §52.16 / AC-053 |
| H17 | Section 42.17 — Layer 3 Boundary (+ Sec 17 amendment) | PS §52.17 |
| H18 | Section 42.18 — Ownership Boundaries (+ Sec 1.3 amendment) | PS §52.18 |
| H19 | Section 42.19 — Research Claims Reaffirmation | PS §52.19 |
| H20 | Section 42.20 — Anti-Scope (+ Sec 1.3, 33 amendments) | PS §52.20 / OBJ-035 |

**Result:** 20/20 hardening requirements (H01–H20) traced. 13/13 new objectives (OBJ-023–035) traced. 6/6 new security requirements (SEC-011–016) traced. 1/1 new NFR (NFR-014) traced. 15/15 new acceptance criteria (AC-039–053) traced.

---

### 42.22 Known Source Gaps (Hardening Pass, 2026-09-15; reclassified 2026-09-16)

| ID | Gap | Classification | Disposition |
|---|---|---|---|
| `SOURCE-GAP-ES-01` | PS §52.7 (H07) deliberately does not assert any specific regulatory regime (GDPR/HIPAA/PCI-DSS) applies. This specification does not invent one. | DEPLOYMENT-SPECIFIC / CONFIGURATION-SPECIFIC | Preserved as an open scope question for the deployment's own configuration; not resolved here. Mirrored in `architecture.md` `SOURCE-GAP-ARCH-01`. |
| `SOURCE-GAP-ES-02` | PS §52.5 (H05), §52.13 (H13) leave exact budget thresholds and the "consequential action" designation list as configuration/policy decisions rather than fixed values. | DEPLOYMENT-SPECIFIC / CONFIGURATION-SPECIFIC | Consistent with PS's own instruction that these are policy-configurable; not a specification defect and requires no further resolution. |
| `SOURCE-GAP-ES-03` | The pre-existing OBJ-015–022 (Section 41.11, tied to PS §51 / the 2026-09-10 amendment) were confirmed present only in this Engineering Specification and not as literally labeled IDs in the Problem Statement text of Section 51. | **RESOLVED (2026-09-16)** | OBJ-015 through OBJ-022 are now canonically labeled at the source in PS §51.13 (Final Foundational Document Reconciliation), and cross-document traceability is maintained in the consolidated registry at `architecture.md` §44.12. See the updated note preceding the Section 3 objectives table. |
| `SOURCE-GAP-ES-04` | PS §52.11/§52.14 do not specify a concrete prompt-injection/content-integrity screening mechanism (e.g., classifier type, rule engine). This specification defines only the ordering/fail-closed requirement (Sections 42.11, 42.14), consistent with the instruction to remain implementation-neutral where the source does not prescribe a mechanism. | DEFERRED TO DOWNSTREAM DOCUMENT | Deferred to `architecture.md` (which defers the concrete mechanism further, to `security.md` and `provider-matrix.md` once generated); not a gap in this document. |

No `SOURCE-CONTRADICTION` was found between PS §52 (H01–H20) and the pre-existing Sections 1–41 of this specification; every apparent tension (Section 17 vs. H17, Section 34.1 vs. H04, Section 6.2 vs. H01, Section 4.2 vs. H08) was resolved by direct amendment at the point of tension, listed in the Section 42 header.

---

### 42.23 Internal Consistency Check Update (Rev 1.3)

The following check rows are added to the Section 40 consistency table:

| Check | Status |
|---|---|
| H01–H20 each traced to a Section 42 subsection | PASS (Section 42.21) |
| OBJ-023–035 traceable to PS §52 and merged into Section 3 | PASS |
| SEC-011–016 traceable to PS §52 and merged into Section 20 | PASS |
| NFR-014 traceable to PS §52.3 and merged into Section 25 | PASS |
| AC-039–053 traceable to PS §52.21 and merged into Section 31 | PASS |
| Section 17 (Layer 3) reframed consistent with H17; no implementation-ownership claim remains | PASS |
| Section 34.1 (Objective Function) reframed as conceptual/heuristic consistent with H04 | PASS |
| Section 6.2 pipeline diagram annotated consistent with H01 (no synchronous-everywhere mandate) | PASS |
| Section 4.2 (Integration Modes) annotated consistent with H08 feasibility tiers | PASS |
| Section 12.2 (AL-002) amended consistent with H10 | PASS |
| Section 1.3, 33 amended consistent with H02/H18/H20 | PASS |
| Section 41.14 cross-references H09/H12 extensions without modifying Section 41.1–41.13 | PASS |
| No existing requirement (OBJ-001–022, AC-001–038, SEC-001–010, NFR-001–013) modified in meaning, only annotated/strengthened where explicitly cited | PASS |
| No SOURCE-CONTRADICTION between PS §52 and Sections 1–41 left unresolved | PASS |

> [!IMPORTANT]
> **INTERNAL CONSISTENCY CHECK (REV 1.3): PASSED (14/14 new checks; 14/14 prior checks from Rev 1.1/1.2 remain valid)**
>
> All twenty hardening requirements (H01–H20) from the 2026-09-15 Architecture Hardening Pass are represented in this specification, traced to the hardened Problem Statement (PS §52), and introduce no unresolved contradiction with Sections 1–41. As of this Rev 1.3 check, four `SOURCE-GAP` items were recorded (Section 42.22); see Section 42.24 for their Rev 1.4 disposition.
>
> This document is ready for stakeholder review and approval.
> **Implementation must not begin until this document has been explicitly approved.**

---

### 42.24 Rev 1.4 Consistency Check Update (Final Foundational Document Reconciliation, 2026-09-16)

This subsection records the Final Foundational Document Reconciliation pass across the Problem Statement, this Engineering Specification, and `architecture.md`. It corrects stale internal references and resolves one traceability gap; it introduces no new normative requirement and removes none.

| Check | Status |
|---|---|
| Stale "Section 42.26" references corrected to their actual targets (Section 21 header note -> 42.23; Section 42.7 callout -> 42.22) | PASS |
| Stale "Section 42.25/25" reference in Section 42.3 corrected to a direct statement (no such subsections exist) | PASS |
| `SOURCE-GAP-ES-03` (OBJ-015–022 traceability) resolved via PS §51.13 and the registry at `architecture.md` §44.12 | PASS |
| `SOURCE-GAP-ES-01`, `SOURCE-GAP-ES-02`, `SOURCE-GAP-ES-04` re-classified (DEPLOYMENT-SPECIFIC / CONFIGURATION-SPECIFIC / DEFERRED TO DOWNSTREAM DOCUMENT) without being silently closed | PASS |
| OBJ-001 through OBJ-035 form one continuous, unambiguous objective sequence across PS, this document, and `architecture.md` | PASS |
| No normative requirement (OBJ, SEC, NFR, AC) added, removed, or reinterpreted by this pass | PASS |
| No downstream document (`interfaces.md`, `conventions.md`, `edge-cases.md`, `scenario-matrix.md`, or later) modified by this pass | PASS |

> [!IMPORTANT]
> **INTERNAL CONSISTENCY CHECK (REV 1.4): PASSED (7/7 new checks; 14/14 Rev 1.1 checks, OBJ-015–022 traceability check, and 14/14 Rev 1.3 checks all remain valid)**
>
> This reconciliation pass is editorial/traceability-only: it corrects stale cross-references and resolves `SOURCE-GAP-ES-03`. No requirement was added, removed, weakened, or reinterpreted.
>
> This document is ready for stakeholder review and approval.
> **Implementation must not begin until this document has been explicitly approved.**

---

*End of Engineering Specification -- EAIOC-SPEC-001 Rev 1.4*
