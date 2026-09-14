# Architecture Document
# Enterprise Agent & LLM Inference Optimization Control Plane
---

**Document ID:** EAIOC-ARCH-001
**Status:** PRE-IMPLEMENTATION — Pending stakeholder approval
**Source (authoritative):** `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt`
**Engineering Spec cross-reference:** `Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md` (EAIOC-SPEC-001 Rev 1.1)
**Date:** 2026-09-08
**Consistency Check:** PASSED (14/14 checks per EAIOC-SPEC-001 §40)

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

**Capabilities to evaluate:**

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
| **NFR-010** | **Explainability** -- The system should explain why a context item was removed, compressed, cached, routed, or retained where practical |
| **NFR-011** | **Determinism** -- Policy-based optimization should be deterministic where feasible |
| **NFR-012** | **Configurability** -- Organizations must be able to tune policies per workflow |
| **NFR-013** | **Testability** -- Every optimization stage must be independently testable |

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

*End of Architecture Document -- EAIOC-ARCH-001 Rev 1.1*
*Source authority: `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt`*
*Engineering specification cross-reference: EAIOC-SPEC-001 Rev 1.2*
