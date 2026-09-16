# Optimization Catalog / Optimization Strategy Registry
## Enterprise Agent & LLM Inference Optimization Control Plane

**Document ID:** EAIOC-OPTCAT-001
**Revision:** 1.0.0 (initial generation)
**Status:** PRE-IMPLEMENTATION — companion to `architecture.md` (EAIOC-ARCH-001 Rev 1.3), `interfaces.md` (EAIOC-INTF-001 v1.2.0), `conventions.md` (EAIOC-CONV-001 Rev 1.1.0), `edge-cases.md` (EAIOC-EDGE-001 v1.2.0), and `scenario-matrix.md`. This document does not modify any of those five files, the Problem Statement, or the Engineering Specification.

**Authoritative Sources (in order of authority):**
1. `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt` (as hardened 2026-09-15, PS §52; reconciled 2026-09-16, PS §51.13)
2. `Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md` (EAIOC-SPEC-001 Rev 1.4)
3. `architecture.md` (EAIOC-ARCH-001 Rev 1.3)
4. `interfaces.md` (EAIOC-INTF-001 v1.2.0)
5. `conventions.md` (EAIOC-CONV-001 Rev 1.1.0)
6. `edge-cases.md` (EAIOC-EDGE-001 v1.2.0)
7. `scenario-matrix.md` (263 scenarios, 30 domains, current baseline — READY FOR DOCUMENTATION BASELINE FREEZE per its own §49)

> [!IMPORTANT]
> This is the canonical, implementation-oriented Optimization Catalog for EAIOC. It formalizes exactly **20 canonical `TECH-NNN` techniques** (architecture.md §23), exactly **25 canonical `DA-NNN` developer-agent modules** (architecture.md §22), and exactly **11 `P5 Index` inference/serving capabilities** (architecture.md §26) at index-level only. It is not a duplicate of `edge-cases.md` or `scenario-matrix.md`, and it does not redefine any component, interface, or requirement established by the six prior documents. Where this document's interpretation of a source conflicts with the source, the source wins — see `## SOURCE CONTRADICTIONS`.

---

## Table of Contents

1. [How to Read This Catalog](#how-to-read-this-catalog)
2. [Canonical Catalog Entry Schema](#canonical-catalog-entry-schema)
3. [Framework Principles Governing Every Entry](#framework-principles-governing-every-entry)
4. [TECH-001 – TECH-020: Core Optimization Techniques](#core-optimization-techniques)
5. [DA-001 – DA-025: Developer-Agent Optimization Modules](#developer-agent-optimization-modules)
6. [P5 Index: Inference/Serving Capabilities](#p5-index-inferenceserving-capabilities)
7. [Scope Boundary](#scope-boundary)
8. [Cross-Document Coverage Matrix](#cross-document-coverage-matrix)
9. [Coverage Matrix](#coverage-matrix)
10. [Anti-Pattern Coverage](#anti-pattern-coverage)
11. [Requirement → Catalog Traceability](#requirement--catalog-traceability)
12. [Scenario Validation Cross-Reference](#scenario-validation-cross-reference)
13. [Edge-Case Relevance Summary](#edge-case-relevance-summary)
14. [SOURCE GAPS DISCOVERED](#source-gaps-discovered)
15. [SOURCE CONTRADICTIONS](#source-contradictions)
16. [Final Report](#final-report)
17. [Optimization Catalog Readiness](#optimization-catalog-readiness)

---

## How to Read This Catalog

This catalog treats optimization as a **stateful control-plane decision**, not a token-reduction transformation. Every entry below assumes the reader has the six prior documents available and does not restate their full normative text — it cites the specific section/ID and states only what is specific to that entry.

**Maturity note (applies to every entry in this document):** this repository is PRE-IMPLEMENTATION (see root `CLAUDE.md`). No technique or module described below has been locally benchmarked. Per `architecture.md` §42 and `conventions.md` §20.3/§26, **every entry's Maturity Level is `0 — RESEARCH`** until local benchmark evidence exists — this is stated once here and repeated per entry for completeness, not fabricated per entry. This is distinct from **Implementation Priority** (P0–P5, `architecture.md` §36), which reflects build sequencing, not validation status — conflating the two is `architecture.md` §23's own historical practice (its "Production Maturity" field mixes both), and is recorded as `CONTRA-OPTCAT-01` in `## SOURCE CONTRADICTIONS` below, resolved in this document by keeping the two fields separate per `## 59` of the generation prompt.

**ID systems used, and how they differ:**

| Prefix | Meaning | Assigned by |
|---|---|---|
| `TECH-NNN` | One of the 20 canonical techniques in `architecture.md` §23 | This document, in §23's listed order |
| `DA-NNN` | One of the 25 developer-agent modules in `architecture.md` §22 | Reused verbatim from `architecture.md` §22 / PS §4 |
| `P5 Index — <name>` | One of the 11 inference/serving capabilities in `architecture.md` §26 | Reused verbatim from `architecture.md` §26 |
| `SCN-<CODE>-NNN` | Scenario-matrix scenario | Reused verbatim from `scenario-matrix.md` |
| `EC-NNN` | Edge case | Reused verbatim from `edge-cases.md` |

---

## Canonical Catalog Entry Schema

Every `TECH-NNN` and `DA-NNN` entry below uses exactly this schema, per the generation prompt's §58. `N/A — not applicable to this entry.` is used where a field genuinely does not apply; no field is blank, and no field uses a meaningless `TBD`.

```
### <ID> — <Name>

Category | Governing Component(s)
Applicability | Explicitly Not Applicable When
Prerequisites
Expected Benefit
Optimization Overhead
Quality Risks
Fallback
Evaluation Methodology
Observability Metrics
Implementation Priority | Maturity Level
Anti-Pattern Check
State Dependencies
Invalidation / Revalidation
Security / Authorization / Policy Constraints
Data Governance Constraints
Provider / Model / Tool Dependencies
Composition / Ordering / Conflicts
Reversibility / Side Effects
Validated By
Traceability (Problem Statement, Engineering Specification, Architecture, Interfaces, Conventions, Edge Cases)
```

---

## Framework Principles Governing Every Entry

These are not restated inside every entry (that would violate the "no repetitive filler" quality bar); they apply uniformly and are cross-referenced by short name where an entry needs to invoke one.

- **Optimization Precedence** (PS §52 preamble; `conventions.md` §1.5, §16.2; root `CLAUDE.md` rule 2): Security / Authorization / Policy / Data Governance → Safety / Integrity / Approval → Correctness / Task Requirements → Reliability / Recovery → Quality → Latency → Cost/Token Optimization. No entry below may be read as authorizing a technique to bypass this order.
- **Verified Net Optimization Value** (`conventions.md` §7.8; ARCH §47.4.2): `Net Optimization Value = Tokens Saved + Inference Cost Saved − Optimization Compute Cost − Retrieval Overhead − Routing Overhead − Cache Overhead − Added Latency Cost − Retry/Escalation Cost − Quality Degradation Cost − Downstream/Tool Cost`. A technique counts as a saving only when this is `> 0` **and** applicable quality gates pass; an unmeasurable term is `UNVERIFIED` (AC-002) and excluded from reported savings. Referred to below as **NOV**.
- **Six `OptimizationDecisionOutcome` values** (`conventions.md` §7.8; INTF §43.14): `APPLY`, `SKIP`, `DO_NOT_OPTIMIZE`, `FALLBACK`, `REJECT`, `REQUIRE_REVALIDATION`. `DO_NOT_OPTIMIZE` is a first-class, expected outcome, not an error.
- **Fail-open vs. fail-closed** (`conventions.md` §16.2; root `CLAUDE.md` rule 2): an optimization-stage failure on an already-authorized, policy-compliant request falls back to the unoptimized path (fail-open); a failure to establish or evaluate authorization/policy/PII-scrubbing/content-integrity is fail-closed and rejects the request. These are never conflated.
- **Operating Mode declaration** (`conventions.md` §7.6; ARCH §47.5): every entry below states which of `SYNC` / `ASYNC` / `HYBRID` it supports under **Provider / Model / Tool Dependencies** or **State Dependencies** as applicable.
- **Decision-Ownership declaration** (`conventions.md` §7.7; ARCH §47.6): stated per entry under **Governing Component(s)** where the entry is not purely `ADVISORY`.
- **Tenant isolation** (`conventions.md` §9.1, §13.2; root `CLAUDE.md` rule 4): every cache/memory/cost artifact any entry below produces is namespaced by `tenant_id` first. Not repeated per entry unless the entry has a tenant-specific nuance.
- **Governance independence** (`conventions.md` §7.9, §13.5–13.10; root `CLAUDE.md` rule 2): SGE/DGE/TMG/HAG/CIS decisions are never inferred from, or substituted by, any optimization/ROI/cost signal in any entry below.

---
## Core Optimization Techniques

Assigned in the exact order the 20 techniques appear in `architecture.md` §23. Each technique below extends `architecture.md` §23's own Applicability/Prerequisites/Expected Benefit/Optimization Overhead/Quality Risks/Fallback/Evaluation Methodology/Observability Metrics fields (reused, not re-derived) with the remaining fields the generation prompt requires (§58) that §23 does not carry: Category, Governing Component(s), Explicitly Not Applicable When, separated Implementation Priority / Maturity Level, Anti-Pattern Check, State Dependencies, Invalidation/Revalidation, Security/Policy Constraints, Data Governance Constraints, Provider/Model/Tool Dependencies, Composition/Ordering/Conflicts, Reversibility/Side Effects, Validated By, and full Traceability.

---

### TECH-001 — Sanitization

| Field | Value |
|---|---|
| **Category** | Prompt/Input Token Optimization (Layer 2; ARCH §47.9 ownership row 1) |
| **Governing Component(s)** | T1.1-SANITIZER (ARCH §11.1; INTF-001). Decision-Ownership: `ENFORCEMENT` (it may strip content before admission) with an `ADVISORY` fallback path when content-type detection is uncertain. |
| **Applicability** | All request types; first stage in every pipeline invocation (ARCH §23) |
| **Explicitly Not Applicable When** | Content is already normalized (e.g., a programmatic/API-originated request with no free-text noise); running the sanitizer against binary/non-text payloads it cannot parse — those bypass to their own content-type handler, not this stage |
| **Prerequisites** | Raw input available; content-type awareness for code vs. prose detection (ARCH §23) |
| **Expected Benefit** | 2–15% token reduction from whitespace/formatting/UI-noise removal; normalizes input for downstream stages (ARCH §23) — Evidence classification: Research/architecture-estimate, not yet locally benchmarked |
| **Optimization Overhead** | Deterministic, near-zero latency; negligible compute cost (ARCH §23); Appendix A budget: < 10 ms, 0 overhead tokens |
| **Quality Risks** | Risk of removing syntax meaningful to the task (Python indentation, diff whitespace) — this is EC-014's documented failure mode; mitigated by content-aware logic (ARCH §23) |
| **Fallback** | If sanitization fails: use original input verbatim (PS §11; CONV §16.1) |
| **Evaluation Methodology** | Token count before vs. after; quality gate on sanitized vs. original output; false-removal rate on benchmark corpus (ARCH §23) |
| **Observability Metrics** | `tokens.raw_input`, `tokens.sanitized`, `tokens.removed_by_sanitizer`, `sanitizer.false_removal_rate` (ARCH §23, §27.1) |
| **Implementation Priority** | P0 — Foundation, unblocked, no validation gate required (ARCH §36; CONV §25) |
| **Maturity Level** | 0 — RESEARCH (no local benchmark evidence yet; repository is pre-implementation) |
| **Anti-Pattern Check** | Guards against "removing content it has not been authorized to handle" (CONV §6.3) and against blind syntax removal — the sanitizer "must be content-aware and must not blindly remove syntax that is meaningful to the task" (PS §6.4) |
| **State Dependencies** | Operating mode: `SYNC` (request-time-only information; CONV §7.6) — sanitization must run against the actual current input, never a precomputed artifact. No context/workflow/policy version dependency. |
| **Invalidation / Revalidation** | N/A — not applicable to this entry (stateless, single-pass transformation with no cached result to invalidate) |
| **Security / Authorization / Policy Constraints** | SEC-001: system/developer security instructions are Priority 1 and are never pruned by this or any stage (CONV §13.1, §8.2). Sanitization is fail-open per stage (CONV §16.2) but sits upstream of, and never substitutes for, T0.x normalization's fail-closed schema/tenant validation. |
| **Data Governance Constraints** | DGE (governance/data/) classification precedes sanitization admission per `conventions.md` §2.2 — an unclassified item defaults to `SENSITIVE` and sanitization must not strip classification metadata |
| **Provider / Model / Tool Dependencies** | N/A — not applicable to this entry (provider-neutral; runs before model/provider selection) |
| **Composition / Ordering / Conflicts** | Stage 3 of the 24-stage default order (CONV §6.1), after exact/semantic cache lookup, before duplicate removal. No known conflicts with other stages; it is a prerequisite for reliable downstream token counting. |
| **Reversibility / Side Effects** | No external side effect (`ADVISORY`/`ENFORCEMENT` only, never `EXECUTION_OWNERSHIP`). Original input is preserved for audit/fallback (PS §6.16 analog); no `ReversibilityRecord` is required because sanitization operates on the transient input, not the persistent Logical Task Context. |
| **Validated By** | No dedicated `SCN-OPT-*`/`SCN-NOPT-*` scenario exists for Sanitization specifically; it is exercised as a pipeline precondition in `SCN-REQ-001` (normal end-to-end admission). |
| **Traceability** | PS §6.4, §11, §25; ES §7.4; ARCH §11.1, §23, §36, §41; INTF-001 §2; CONV §6.1, §6.3, §8.2, §13.1, §16.1, §25; EC-014 (Pruner Removes Python Indentation — the sanitizer-adjacent instance of this same content-awareness risk) |

---

### TECH-002 — Query Compression

| Field | Value |
|---|---|
| **Category** | Prompt/Input Token Optimization (Layer 2; ARCH §47.9) |
| **Governing Component(s)** | T2.1-QUERY-COMPRESSOR (ARCH §12.1; INTF §4 via INTF-003 OptimizationModule). Decision-Ownership: `ENFORCEMENT` when it rewrites the assembled query; original request is retained. |
| **Applicability** | Verbose user requests with filler, politeness, or redundant explanations |
| **Explicitly Not Applicable When** | Short/terse queries where compression yield is near zero (ARCH §23) — this is the proactive `DO_NOT_OPTIMIZE` case in `SCN-OPT-005` / EC-053 |
| **Prerequisites** | T1.2 Intent Classifier output; entity extraction complete; original request preserved for audit and fallback (ARCH §23) |
| **Expected Benefit** | Source whitepaper reports 30–60% query reduction depending on verbosity; treat as a validation target, not a guarantee (ARCH §23; PS §6.16, §28) |
| **Optimization Overhead** | Lightweight LLM call or rule-based compression; the compression call's own token cost is included in NOV accounting (ARCH §23) |
| **Quality Risks** | Risk of losing constraints, exceptions, or required output format (EC-052); mitigated by a mandatory preservation list (intent, constraints, entities, numbers, dates, exceptions, required output format, safety/security requirements — PS §6.16) |
| **Fallback** | If the compressed query fails validation: use the original query (ARCH §23) |
| **Evaluation Methodology** | Semantic-fidelity check (embedding similarity of compressed vs. original intent); downstream answer-quality comparison; validate the 30–60% target on the organization's own corpus (ARCH §23) |
| **Observability Metrics** | `tokens.query_raw`, `tokens.query_compressed`, `tokens.query_removed`, `query_compressor.fidelity_score` (ARCH §23) |
| **Implementation Priority** | P1 — High-Confidence Optimization; validation matrix required before default-enable (ARCH §36; CONV §25) |
| **Maturity Level** | 0 — RESEARCH (no local benchmark evidence yet) |
| **Anti-Pattern Check** | Guards against "claiming savings without accounting for optimization overhead" (CONV §24.1) — the compressor's own call cost is a first-class NOV term, not an externality; and against treating the 30–60% figure as a guarantee (CONV §20.2, §26) |
| **State Dependencies** | Operating mode: `SYNC` (the actual user request is request-time-only information). Depends on `context_version` only insofar as entity extraction feeds it; no cross-execution state. |
| **Invalidation / Revalidation** | N/A — not applicable to this entry (single-pass per request; no cached compression result persists across requests) |
| **Security / Authorization / Policy Constraints** | SEC-005: compression must preserve security-relevant constraints (CONV §13.1); the mandatory preservation list explicitly includes "safety/security requirements" (PS §6.16) |
| **Data Governance Constraints** | If the original query contains sensitive content, DGE classification (governance/data/) precedes compression per `conventions.md` §2.2; compressed output inherits the source's sensitivity classification, it does not reset it |
| **Provider / Model / Tool Dependencies** | If model-based compression is used, it is subject to `ModelProfile` capability constraints (INTF-014) and is itself a billable inference call counted in NOV, not a free operation |
| **Composition / Ordering / Conflicts** | Stage 16 of 24 (CONV §6.1), after context ranking/pruning/compression and before cache-aware prompt assembly — it compresses the user's request, a different target than context compression (TECH-007), and the two must not be conflated in measurement (H18, ARCH §47.9) |
| **Reversibility / Side Effects** | Fully reversible: "the original request must remain available for audit, evaluation, and fallback" (PS §6.16) |
| **Validated By** | `SCN-OPT-005` (cost-of-optimization gate skips query compression for a terse query); `SCN-QUAL-002` (query compression removing a required constraint is caught by the quality gate) |
| **Traceability** | PS §6.16, §17.1, §25; ES §7.16; ARCH §12.1, §23, §36, §41; INTF-003 §4; CONV §6.1, §7.2, §20.2, §24.1, §26; EC-052 (Query Compression Removes a Constraint Required for Correct Output), EC-053 (Query Compression Has Zero Net Benefit for Terse Queries) |

---

### TECH-003 — Context Pruning

| Field | Value |
|---|---|
| **Category** | Context Optimization (Layer 2; ARCH §47.9) |
| **Governing Component(s)** | T1.9-CONTEXT-PRUNER (ARCH §11.6; INTF-006), gated by **CIG** (Context Integrity Gate, ARCH §46.2.6) for Tier 0/1 protection and by **DGE** for sensitivity classification (ARCH §47.2.2) before any eviction. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Any request with retrieved context, conversation history, tool output, or documents exceeding the relevance threshold (ARCH §23) |
| **Explicitly Not Applicable When** | The candidate item is Tier 0 (SEC-protected/mandatory, PS §51.4) or Tier 1 (protected, evictable only if the task is terminal) — CIG returns `TIER_VIOLATION` and blocks the eviction (ARCH §46.2.6); also not applicable when context is already below budget and adding pruning overhead would fail the NOV test |
| **Prerequisites** | T1.3 Entity Extraction; T1.2 Intent Classifier; query/task defined; pruning techniques calibrated per content type (ARCH §23) |
| **Expected Benefit** | 20–60% context reduction depending on content quality; primary signal is relevance, not compression (ARCH §23) |
| **Optimization Overhead** | Embedding computation (if used) — lightweight for lexical/metadata approaches, moderate for embedding-based approaches (ARCH §23) |
| **Quality Risks** | False pruning of relevant context (the delayed-relevance problem, QO-003; EC-013); mitigated by CL-004 reversibility and QO-003 delayed-relevance protection (ARCH §23; CONV §8.1, §15.4) |
| **Fallback** | If pruning fails: retain bounded original context (ARCH §23; CONV §16.1) |
| **Evaluation Methodology** | Context reduction %; retrieval recall@K before vs. after; answer-quality baseline vs. optimized; false-pruning rate tracked in benchmark (ARCH §23) |
| **Observability Metrics** | `tokens.context_before_pruning`, `tokens.context_after_pruning`, `pruner.false_pruning_rate`, `pruner.quality_impact_score` (ARCH §23) |
| **Implementation Priority** | P1 — High-Confidence Optimization; validation matrix required (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against "sending every retrieved chunk to the LLM" and "optimizing context without considering delayed relevance" (CONV §24.1–24.2); explicitly must never evict Tier 0 content regardless of relevance score or budget pressure (root `CLAUDE.md` rule 6; PS §51.4) |
| **State Dependencies** | Operating mode: `SYNC` for the actual eviction decision against current `context_version` (owned by CVM, ARCH §46.2.2); depends on the current Tier classification (CL-001/CVM) and DGE's sensitivity classification. Pruning of `LogicalTaskContext` requires a `ReversibilityRecord` and increments `context_version`. |
| **Invalidation / Revalidation** | A pruning decision computed against `context_version = N` must be revalidated (or recomputed) if the context has since mutated to `N+1` before the pruned result is admitted (EC-084, `SCN-OPT-002`); CEC (Context Expansion Controller, ARCH §46.2.7) handles re-admission if a pruned item is later found necessary |
| **Security / Authorization / Policy Constraints** | SEC-001; CIG enforces Tier 0/1 fail-closed (`TIER_VIOLATION` blocks inference — ARCH §46.2.6, CONV §16.2); pruning must not remove a security constraint (EC-015) |
| **Data Governance Constraints** | DGE classification precedes pruning admission (`conventions.md` §2.2); an item whose classification is `UNKNOWN` defaults to `SENSITIVE` and is not eligible for relevance-score-driven eviction on that basis alone |
| **Provider / Model / Tool Dependencies** | Pruning thresholds may be model-specific (context-window-relative); no hard-coded provider assumption (CONV §21.2) |
| **Composition / Ordering / Conflicts** | Stage 10 of 24 (CONV §6.1), after context ranking, before context deduplication. Conflicts with TECH-004 (Context Deduplication) only in ordering, not in intent — the default order runs pruning before deduplication, but the system may reorder with benchmark evidence (ARCH §24). Composes with TECH-020 (Soft Reset) as its escalation path once pruning alone is insufficient. |
| **Reversibility / Side Effects** | Destructive at the Logical Task Context layer; every eviction requires a `ReversibilityRecord` (original source, transformation type, removed range, reason, confidence, recovery pointer, expiration — CL-004, CONV §8.1) with minimum 24-hour retention (Appendix C) |
| **Validated By** | `SCN-CTX-002` (tiered eviction protocol), `SCN-CTX-003` (Tier 0/1 unresolvable overflow), `SCN-OPT-002` (stale pruning result across context versions), `SCN-CMP-036` (agent plan assumes context evicted by concurrent budget reduction) |
| **Traceability** | PS §6.9, §11, §25, §39 QO-003, §51.4; ES §7.9; ARCH §11.6, §23, §36, §41, §46.2.2, §46.2.6; INTF-006 §5.3; CONV §6.1, §8.1, §15.4, §16.2, §24.1; EC-013, EC-015, EC-021 (adjacent — reordering, not pruning, but shares the instruction-precedence risk), EC-022, EC-094 |

---

### TECH-004 — Context Deduplication

| Field | Value |
|---|---|
| **Category** | Context Optimization (Layer 2) |
| **Governing Component(s)** | T1.10-CONTEXT-DEDUPLICATOR (ARCH §11.7; INTF-007). Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Multi-turn conversations; RAG with overlapping chunk sources; tool outputs with repeated fields; agent pipelines with repeated context propagation (ARCH §23) |
| **Explicitly Not Applicable When** | Two similar items are near-duplicates with a semantically different constraint (EC-016) — these must not be collapsed; also not applicable when the "duplicate" is actually a newer version of the same document and the deduplicator would retain the wrong copy (EC-017) |
| **Prerequisites** | Context collected from all sources; canonical representation defined (ARCH §23) |
| **Expected Benefit** | 5–40% context reduction in document-heavy or multi-source workloads (ARCH §23) |
| **Optimization Overhead** | Hash-based exact deduplication is near-zero; near-duplicate detection (embedding comparison) is moderate (ARCH §23) |
| **Quality Risks** | Removal of near-duplicates with slight but material differences (EC-016); removal of the most-recent version while retaining a stale one (EC-017); mitigated by canonical-copy preservation and audit record (ARCH §23) |
| **Fallback** | Retain all context if deduplication cannot run (ARCH §23) |
| **Evaluation Methodology** | Duplicate token removal %; recall@K; answer-quality comparison (ARCH §23) |
| **Observability Metrics** | `tokens.removed_by_deduplicator`, `deduplicator.duplicate_count`, `deduplicator.near_duplicate_count` (ARCH §23) |
| **Implementation Priority** | P1 — High confidence, enable after validation (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against "repeating the same system instructions unnecessarily" and "carrying complete conversation history indefinitely" (CONV §24.1); must preserve one canonical copy and record removed tokens rather than silently discarding provenance (ARCH §23) |
| **State Dependencies** | Operating mode: `SYNC`. Increments `context_version` on every successful mutation (CVM, ARCH §46.2.2). |
| **Invalidation / Revalidation** | A deduplication decision is version-stamped; if a subsequent stage (e.g., compression) fails after deduplication succeeds, the deduplication result is retained independently — stage outcomes are not all-or-nothing (`SCN-OPT-003`) |
| **Security / Authorization / Policy Constraints** | Must not deduplicate away a security-classified instruction merely because a near-identical copy exists elsewhere (root `CLAUDE.md` rule 6) |
| **Data Governance Constraints** | DGE classification precedes admission; deduplication across tenant boundaries is unconditionally prohibited (CONV §13.2) — a "duplicate" is only ever computed within one `tenant_id` |
| **Provider / Model / Tool Dependencies** | N/A — not applicable to this entry |
| **Composition / Ordering / Conflicts** | Stage 11 of 24 (CONV §6.1), immediately after pruning; composes cleanly with TECH-003 (its predecessor) and TECH-007 (its successor, Context Compression) — `SCN-OPT-003` is the canonical example of this composition's partial-failure handling |
| **Reversibility / Side Effects** | Reversible; the removed copy's `ReversibilityRecord` references the retained canonical copy (CL-004) |
| **Validated By** | `SCN-OPT-003` (deduplication succeeds while a later compression stage fails); `SCN-OPT-004` (per-file fallback in a mixed-outcome batch) |
| **Traceability** | PS §6.10, §25; ES §7.10; ARCH §11.7, §23, §36, §41; INTF-007 §5.4; CONV §6.1, §13.2, §24.1; EC-016, EC-017, EC-023 |

---
### TECH-005 — Adaptive Top-K Retrieval

| Field | Value |
|---|---|
| **Category** | Context Optimization / Retrieval (Layer 2) |
| **Governing Component(s)** | T1.12-ADAPTIVE-RETRIEVAL (PS §6.12; INTF-005 ContextRetriever, INTF-027 RAGPipeline). Decision-Ownership: `ADVISORY` into retrieval depth selection, `ENFORCEMENT` on the resulting K. |
| **Applicability** | All RAG workloads; replaces fixed-K retrieval (ARCH §23) |
| **Explicitly Not Applicable When** | The retrieval result set is empty regardless of K (EC-025 — a distinct failure mode from under-retrieval); or when no complexity signal is available at all, in which case fall back to the configured fixed-K baseline rather than guessing |
| **Prerequisites** | Query complexity assessment (T0.2); relevance scoring; token budget available (ARCH §23) |
| **Expected Benefit** | Reduces unnecessary context for simple queries; improves relevance for complex queries; avoids token waste from over-retrieval (ARCH §23) |
| **Optimization Overhead** | Requires complexity scoring before retrieval; negligible additional latency (ARCH §23) |
| **Quality Risks** | Under-retrieval for complex queries (EC-024); mitigated by an escalation path and quality gate (ARCH §23) |
| **Fallback** | Use fixed-K (e.g., K=4) as a safe fallback (ARCH §23) |
| **Evaluation Methodology** | Compare adaptive K vs. fixed K on retrieved tokens, retrieval quality, answer quality, cost (ARCH §23) |
| **Observability Metrics** | `retrieval.k_selected`, `retrieval.k_fixed_baseline`, `retrieval.tokens_retrieved`, `retrieval.quality_score` (ARCH §23) |
| **Implementation Priority** | P1 — High-Confidence Optimization (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Directly guards against the named anti-pattern "using a fixed Top-K for every query" (CONV §24.1; ARCH §41) — this is the canonical technique the anti-pattern list is written against |
| **State Dependencies** | Operating mode: `SYNC` (K selection depends on the actual query's assessed complexity). No cross-request state. |
| **Invalidation / Revalidation** | N/A — not applicable to this entry (per-request decision, not cached) |
| **Security / Authorization / Policy Constraints** | Retrieval results remain subject to authorization filtering regardless of K (SEC-002); a larger K must not be used to bypass a per-item authorization check |
| **Data Governance Constraints** | N/A — not applicable to this entry beyond the standard per-item classification already required at retrieval (§13.7) |
| **Provider / Model / Tool Dependencies** | K ceiling may be bounded by the effective context budget of the selected model (T0.1/CIG interaction) |
| **Composition / Ordering / Conflicts** | Stage 8 of 24 (CONV §6.1), before context ranking (stage 9) and pruning (stage 10) — it bounds the candidate set those later stages operate on, rather than replacing them |
| **Reversibility / Side Effects** | No destructive side effect; retrieval is additive (more/fewer candidates), not evictive, so CL-004 reversibility does not apply |
| **Validated By** | `SCN-NOPT-002` (K varies 2→8 by query complexity, never fixed uniformly) |
| **Traceability** | PS §6.12, §25; ES §7.12; ARCH §11.9, §23, §36, §41; INTF-005 §5.2, INTF-027 §15; CONV §6.1, §24.1; EC-024, EC-025 |

---

### TECH-006 — Token-Aware Ranking

| Field | Value |
|---|---|
| **Category** | Context Optimization / Retrieval (Layer 2) |
| **Governing Component(s)** | Token-Aware Ranker (PS §6.11; folds into T1.3/T1.12 per PS §5 diagram). Also consumes **OI-004** Context Utility/ROI Scorer (`conventions.md` §7.4) for the utility half of its formula. Decision-Ownership: `ADVISORY` (feeds ranking order, does not itself evict). |
| **Applicability** | All retrieval-augmented workloads; any context assembly with multiple candidate chunks (ARCH §23) |
| **Explicitly Not Applicable When** | The formula's token-cost penalty would exclude the only authoritative source for the task (EC-026) — in that case relevance/authority overrides the token-cost term |
| **Prerequisites** | Relevance scores; token counts per chunk; source-authority metadata; entity-overlap data (ARCH §23) |
| **Expected Benefit** | Improves the utility-per-token ratio of the context window; reduces token waste from low-value, high-cost content (ARCH §23) |
| **Optimization Overhead** | Scoring computation per chunk; configurable formula adds overhead proportional to signals used (ARCH §23) |
| **Quality Risks** | Over-penalizing token cost may exclude relevant long documents (EC-026); the formula must be benchmarked, not assumed (ARCH §23) |
| **Fallback** | Use relevance-only ranking if token-aware scoring fails (ARCH §23) |
| **Evaluation Methodology** | Utility/token before vs. after; answer-quality comparison; benchmark formula variants (ARCH §23) |
| **Observability Metrics** | `ranker.utility_per_token`, `ranker.avg_relevance_score`, `ranker.tokens_ranked`, `ranker.formula_version` (ARCH §23) |
| **Implementation Priority** | P3 — Advanced Optimization; benchmarked formula required (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | The conceptual utility formula (`Relevance × Density × Authority × Diversity ÷ Token Cost`, PS §6.11) is explicitly "benchmarked rather than assumed to be universally optimal" — guards against treating any single formula as canonical without local evidence (CONV §26) |
| **State Dependencies** | Operating mode: `HYBRID` — the formula/weights may be precomputed (`OptimizationPolicy`) but scoring runs per-request against current candidates |
| **Invalidation / Revalidation** | Formula version (`ranker.formula_version`) is tracked so a policy change is attributable; no separate invalidation beyond normal `context_version` tracking |
| **Security / Authorization / Policy Constraints** | Ranking must not be the mechanism by which a security-protected item's relevance score is used to justify its removal (root `CLAUDE.md` rule 6) |
| **Data Governance Constraints** | N/A — not applicable to this entry beyond the standard classification already required upstream |
| **Provider / Model / Tool Dependencies** | N/A — not applicable to this entry |
| **Composition / Ordering / Conflicts** | Stage 9 of 24 (CONV §6.1), between Adaptive Top-K (stage 8) and Context Pruning (stage 10) — ranking output is pruning's input; conflicts if pruning re-scores independently without consulting the ranker's utility signal (composition should reuse, not recompute) |
| **Reversibility / Side Effects** | No destructive side effect; produces an ordering/score, not an eviction |
| **Validated By** | No dedicated `SCN-OPT-*`/`SCN-NOPT-*` scenario isolates the ranking formula itself; `SCN-CTX-002`'s Tier-based eviction is a related but distinct mechanism (PARTIALLY COVERED per `scenario-matrix.md` §39's EC-026 row) — this is a genuine, honestly-flagged thin spot, not force-fit. |
| **Traceability** | PS §6.11, §14 OI-004; ES §7.11; ARCH §11.8, §14.4 (OI-004), §23, §36; CONV §7.4, §26; EC-026 |

---

### TECH-007 — Context Compression

| Field | Value |
|---|---|
| **Category** | Context Optimization (Layer 2) |
| **Governing Component(s)** | T1.8-CONTEXT-COMPRESSOR (ARCH §11.10; INTF-008), gated by **CIS** (Content Integrity Screen, ARCH §47.2.5) if the source content is externally-sourced, and by **QO-001** Compression Contracts (`conventions.md` §15.3). Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Long-context workloads; RAG with verbose documents; agent histories; when context exceeds budget after pruning and deduplication (ARCH §23) |
| **Explicitly Not Applicable When** | Context is already compact/high-density (`SCN-NOPT-001`, EC-154's proactive-`DO_NOT_OPTIMIZE` case); or when the content is Tier 0/1 security-classified (root `CLAUDE.md` rule 6 — never compressed away regardless of budget pressure) |
| **Prerequisites** | Context pruned and deduplicated; compression invariants defined (QO-001); quality threshold configured (ARCH §23) |
| **Expected Benefit** | Source whitepaper: 44.1% input reduction in evaluated settings — treat as a benchmark reference, not a production guarantee (ARCH §23; PS §16, §52.19) |
| **Optimization Overhead** | Model-based compression requires inference (compute + token cost); must be included in NOV (ARCH §23) |
| **Quality Risks** | May lose factual detail, citations, or code structure (EC-018, EC-020); mitigated by compression contracts (QO-001) — invariants for Coding (signatures, imports, types, API contracts, security constraints, test expectations, error locations), RAG (entities, numbers, dates, citations, source attribution), and Agent (objective, constraints, decisions, completed/pending actions, unresolved failures) content, per `conventions.md` §15.3 |
| **Fallback** | If compression fails: use uncompressed context (ARCH §23) |
| **Evaluation Methodology** | Compression ratio; answer quality before vs. after; entity/citation preservation rate; validate against the organization's own corpus (ARCH §23) |
| **Observability Metrics** | `compressor.ratio`, `compressor.tokens_in`, `compressor.tokens_out`, `compressor.quality_delta`, `compressor.latency_ms` (ARCH §23) |
| **Implementation Priority** | P1 — High-Confidence Optimization; validation matrix required (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Directly guards against "compressing everything indiscriminately" (CONV §24.1) — Applicability/Explicitly-Not-Applicable-When above enforce the boundary; also guards against malicious content engineered to survive compression (EC-186/EC-207) by requiring CIS screening before, not after, compression |
| **State Dependencies** | Operating mode: `SYNC` for the compression call itself; the decision of *whether* to compress may be `HYBRID` (precomputed density heuristic, revalidated inline). Depends on `context_version`; a compression computed against version N must be reconciled if the context advances to N+1 before the result is applied (EC-084, `SCN-OPT-002`). |
| **Invalidation / Revalidation** | Same version-mismatch handling as TECH-003; additionally, a compression result is invalidated if the source item's DGE sensitivity classification changes mid-execution (EC-170) |
| **Security / Authorization / Policy Constraints** | Root `CLAUDE.md` rule 6 (never compress away security-classified content); SEC-005 (compression must preserve security-relevant constraints); CIS screening precedes compression for non-user-input content (SEC-016, `conventions.md` §13.10) |
| **Data Governance Constraints** | DGE classification precedes compression admission; compressed output inherits the source's classification and retention policy, it does not reset either |
| **Provider / Model / Tool Dependencies** | If model-based, subject to `ModelProfile` capability and pricing (INTF-014); the compression model may differ from the target inference model and is tracked separately in cost attribution (H18) |
| **Composition / Ordering / Conflicts** | Stage 14 of 24 (CONV §6.1) — deliberately after the cheaper stages (cache, sanitization, dedup, pruning, ranking) per the "lowest-risk/lowest-cost first" policy (ARCH §24); composes with TECH-020 (Soft Reset) as the next-heavier escalation when compression alone is insufficient |
| **Reversibility / Side Effects** | Destructive; requires a `ReversibilityRecord` per CL-004 with recovery pointer, since compressed content cannot be reconstructed from the compressed form alone |
| **Validated By** | `SCN-NOPT-001` (compression skipped on already-efficient context); `SCN-CMP-007` (quality-gate failure after optimization coincides with budget exhaustion); `SCN-QUAL-001` (quality gate rolls back a failing optimization) |
| **Traceability** | PS §6.13, §17.2, §25, §39 QO-001; ES §7.13; ARCH §11.10, §23, §36, §41, §47.2.5; INTF-008 §5.5; CONV §6.1, §15.3, §24.1; EC-018, EC-019, EC-020, EC-186/EC-207 |

---

### TECH-008 — Context Reordering

| Field | Value |
|---|---|
| **Category** | Context Optimization (Layer 2) |
| **Governing Component(s)** | T1.11-CONTEXT-REORDERER (ARCH §11.11; INTF-009). Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Long-context workloads where position bias ("lost in the middle") is a known risk (ARCH §23) |
| **Explicitly Not Applicable When** | Reordering would change instruction precedence (EC-021) or cross a source boundary in a way that confuses provenance — in either case the stage must not run, or must run with the affected items pinned |
| **Prerequisites** | Context selected and ranked; model-specific ordering policy defined (ARCH §23) |
| **Expected Benefit** | Mitigates position bias; positions most-relevant content at start/end; measurable quality improvement in long-context settings (ARCH §23) |
| **Optimization Overhead** | Sorting/reordering only; negligible compute cost (ARCH §23) |
| **Quality Risks** | Risk of accidentally changing instruction precedence; source-boundary confusion (ARCH §23; EC-021) |
| **Fallback** | Use original context order (ARCH §23) |
| **Evaluation Methodology** | Answer-quality comparison with position-bias tests; measure the lost-in-the-middle effect (ARCH §23) |
| **Observability Metrics** | `reorderer.position_bias_score`, `reorderer.quality_delta`, `reorderer.model_policy` (ARCH §23) |
| **Implementation Priority** | P3 — Advanced Optimization; requires quality measurement before and after (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards specifically against EC-021's failure mode by requiring "preserve source boundaries" and "avoid accidental instruction precedence changes" (PS §6.14) as hard applicability boundaries, not aspirational goals |
| **State Dependencies** | Operating mode: `SYNC`. No cross-request state; depends only on the current assembled candidate set. |
| **Invalidation / Revalidation** | N/A — not applicable to this entry (recomputed fresh per assembly, never cached) |
| **Security / Authorization / Policy Constraints** | Reordering must never place dynamic/volatile content ahead of, or interleaved with, the stable/cacheable prefix (CE-002, `conventions.md` §8.3) — this would break cache-fragmentation prevention (§9.5) as a side effect |
| **Data Governance Constraints** | N/A — not applicable to this entry |
| **Provider / Model / Tool Dependencies** | Ordering policy is explicitly model-specific (`reorderer.model_policy`) — no universal ordering is assumed across providers (CONV §21.2) |
| **Composition / Ordering / Conflicts** | Stage 13 of 24 (CONV §6.1), after tool-output filtering and before context compression — reordering the uncompressed context preserves clearer source boundaries than reordering after compression would |
| **Reversibility / Side Effects** | Non-destructive (permutation only, no content loss); no `ReversibilityRecord` required since nothing is evicted |
| **Validated By** | No dedicated `SCN-OPT-*` scenario isolates reordering; `SCN-CTX-002` is a PARTIALLY COVERED, thematically-adjacent scenario per `scenario-matrix.md` §39's EC-021 row (Tier-based eviction order, not reordering) — an honestly-flagged thin spot. |
| **Traceability** | PS §6.14, §25; ES §7.14; ARCH §11.11, §23, §36; INTF-009 §5.6; CONV §6.1, §8.3, §9.5; EC-021 |

---
### TECH-009 — Prompt Caching

| Field | Value |
|---|---|
| **Category** | Cache Optimization (Layer 2; ARCH §47.9) |
| **Governing Component(s)** | T1.6-PROMPT-CACHE (ARCH §11.4; INTF-012 CacheStore), gated by **SRP** (Stale Result Protection, ARCH §46.2.11) on every hit. Decision-Ownership: `EXECUTION_OWNERSHIP` for the cache write; `ENFORCEMENT` for serving a hit. |
| **Applicability** | Any request with stable system instructions, tool definitions, or policy content that repeats across requests (ARCH §23) |
| **Explicitly Not Applicable When** | The cacheable prefix would have to include dynamic content (timestamps, random IDs — CE-003 fragmentation patterns, `conventions.md` §9.5); or the content is prohibited from caching by data-retention policy (SEC-004) |
| **Prerequisites** | Cache-stable prefix identified; provider supports prefix caching; tenant isolation configured (ARCH §23) |
| **Expected Benefit** | Material reduction in repeated-input cost and latency for cached-prefix tokens; provider-specific (ARCH §23) — Evidence classification: Provider capability (Anthropic prompt-caching documentation, PS §17.4), requires local verification per `conventions.md` §26 |
| **Optimization Overhead** | Cache write cost (first occurrence); cache read pricing (subsequent occurrences); net benefit depends on reuse frequency (ARCH §23) |
| **Quality Risks** | Stale cache returning outdated instructions (EC-027); mitigated by TTL and explicit invalidation (ARCH §23) |
| **Fallback** | If cache unavailable: continue without cache; count as a cache miss (ARCH §23) |
| **Evaluation Methodology** | Cache hit rate; cost per request with vs. without cache; latency comparison; break-even reuse count (ARCH §23) |
| **Observability Metrics** | `cache.prompt.hit_rate`, `cache.prompt.miss_rate`, `cache.prompt.write_cost`, `cache.prompt.read_cost`, `cache.prompt.tokens_reused` (ARCH §23) |
| **Implementation Priority** | P1 — High confidence; provider-specific setup required (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against cross-tenant cache key collision (EC-028) and against "provider-specific caching silently weakening organizational security requirements" (SEC-010) |
| **State Dependencies** | Operating mode: `HYBRID` (`conventions.md` §7.6) — a precomputed cache entry is consulted synchronously, revalidated inline by SRP on every hit. `SRP` checks `policy_version` on every hit (EC-027); a policy-version change invalidates the entry. |
| **Invalidation / Revalidation** | SRP validates freshness/`policy_version` on every hit (ARCH §46.2.11); on a resume across a context/workflow-version boundary, the entry must be revalidated before reuse (EC-125) |
| **Security / Authorization / Policy Constraints** | SEC-003 (tenant/user/workspace-aware cache keys, `tenant_id` always the first namespace component); SEC-004 (PIIClassifier gate before write, `conventions.md` §9.3); SEC-010 |
| **Data Governance Constraints** | DGE classification determines caching eligibility (`conventions.md` §13.7); every cache entry has a configurable retention period (Appendix C); deletion/erasure propagates to prompt-cache entries (SEC-013) |
| **Provider / Model / Tool Dependencies** | Provider prompt caching is treated separately from application-level caching (PS §41 CE, ARCH §25); cache write/read price, TTL, and semantics are read from `ModelProfile`, never hard-coded (`conventions.md` §21.3) |
| **Composition / Ordering / Conflicts** | Stage 1–2 territory conceptually (exact-cache-adjacent) but structurally realized at prompt-assembly time (stage 17, cache-aware prompt assembly, CONV §6.1); composes with CE-002 Cache-Aware Context Construction (`conventions.md` §8.3) which deliberately maximizes the reusable prefix for this technique to exploit |
| **Reversibility / Side Effects** | `EXECUTION_OWNERSHIP` for the write (a real external side effect — a cache entry with a cost); reversible via TTL expiry or explicit invalidation, never via silent overwrite |
| **Validated By** | `SCN-CACHE-001` (exact cache hit returns without any model call); `SCN-POL-003` (cached decision invalidated by policy-version change); `SCN-CMP-006` (cross-tenant isolation held under concurrent load with a cache race) |
| **Traceability** | PS §6.7, §17.4, §25, §35 CE-001–005; ES §7.7; ARCH §11.4, §23, §36, §41, §46.2.11; INTF-012 §6; CONV §6.1, §8.3, §9.1–9.5, §13.1, §16.2; EC-027, EC-028, EC-029 |

---

### TECH-010 — Semantic Caching

| Field | Value |
|---|---|
| **Category** | Cache Optimization (Layer 2) |
| **Governing Component(s)** | T1.7-SEMANTIC-CACHE (ARCH §11.5; INTF-012), gated by **SRP** and the four-check gate (`conventions.md` §9.1: auth, freshness, tenant, policy). Decision-Ownership: `EXECUTION_OWNERSHIP` for the write; `ENFORCEMENT` for serving a hit. |
| **Applicability** | Workloads with semantically repetitive queries; FAQ-style questions; repeated analysis tasks (ARCH §23) |
| **Explicitly Not Applicable When** | The query is time-sensitive/freshness-critical (EC-030); the similarity match is below the tenant-configured threshold or the global floor of 0.92 (`conventions.md` §9.2); or any of the four checks fails — treated as a miss, never a degraded-confidence hit |
| **Prerequisites** | Semantic similarity model; freshness validation; authorization-check mechanism; configurable similarity threshold (ARCH §23) |
| **Expected Benefit** | Avoids full inference for semantically equivalent requests; cost savings proportional to cache hit rate (ARCH §23) |
| **Optimization Overhead** | Embedding computation per request for similarity matching; authorization and freshness checks (ARCH §23) |
| **Quality Risks** | Returning stale or unauthorized results; treating similar-but-different queries as equivalent (EC-030, EC-031); adversarial similarity manipulation to force a hit (EC-078) |
| **Fallback** | If the semantic match is uncertain: treat as a cache miss and proceed to the full pipeline (ARCH §23) |
| **Evaluation Methodology** | Hit rate; correctness rate of served results; freshness-validation pass rate; authorization-check pass rate (ARCH §23) |
| **Observability Metrics** | `cache.semantic.hit_rate`, `cache.semantic.similarity_score`, `cache.semantic.freshness_check_pass_rate`, `cache.semantic.auth_check_pass_rate` (ARCH §23) |
| **Implementation Priority** | P2 — Cost Intelligence; requires freshness/authorization infrastructure before enabling (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Directly guards against "using semantic cache without freshness/authorization checks" (CONV §24.1) and "treating semantic similarity as sufficient proof of cache safety" (CONV §24.2) — the four-check gate exists precisely to prevent this |
| **State Dependencies** | Operating mode: `HYBRID`. Depends on `context_version`, `policy_version`, and authorization state at hit time, not at write time (SEC-007) |
| **Invalidation / Revalidation** | SRP's semantic-cache detection method is a `freshness_valid` flag plus TTL check (ARCH §46.2.11); explicit invalidation is required for mutable data (CONV §9.5) |
| **Security / Authorization / Policy Constraints** | SEC-007 (semantic cache reuse must verify authorization and freshness); the four-check gate (auth, freshness, tenant, policy) is mandatory on every candidate match, not advisory (`conventions.md` §9.1) |
| **Data Governance Constraints** | Same as TECH-009, plus: a semantic-cache entry must not be poisoned by adversarial model output used as a direct key without validation (EC-079) |
| **Provider / Model / Tool Dependencies** | Embedding model choice is provider-neutral via adapter (root `CLAUDE.md` rule 1); similarity threshold is tenant-configurable, never globally hard-coded below the 0.92 floor |
| **Composition / Ordering / Conflicts** | Stage 2 of 24 (CONV §6.1), immediately after exact-cache lookup — semantic caching is only attempted on an exact-cache miss; composes with TECH-009 as the fallback-then-fallback chain (exact → semantic → full pipeline) |
| **Reversibility / Side Effects** | `EXECUTION_OWNERSHIP` for the write; reversible via TTL/explicit invalidation |
| **Validated By** | `SCN-CACHE-002` (semantic cache rejects a similar-but-unsafe candidate); `SCN-CACHE-005` (cache-poisoning attempt via adversarial model output as key) |
| **Traceability** | PS §6.8, §25; ES §7.8; ARCH §11.5, §23, §36, §41, §46.2.11; INTF-012 §6; CONV §6.1, §9.1–9.3, §13.1; EC-030, EC-031, EC-078, EC-079 |

---

### TECH-011 — Tool Output Filtering

| Field | Value |
|---|---|
| **Category** | Tool Execution Optimization (Layer 1; ARCH §47.9 lists it under context/tool ownership) |
| **Governing Component(s)** | T3.2-TOOL-OUTPUT-FILTER (ARCH §13.2; INTF §10.3 ToolExecutor), consulting **TE-006** Tool Result Value Filter (`conventions.md` §11.3). Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | All tool calls that return responses larger than required by the task (ARCH §23) |
| **Explicitly Not Applicable When** | The full result is itself the deliverable (e.g., a file-read tool whose output is the requested file content) — filtering must not remove fields required for authorization, validation, or audit (SEC-006) regardless of size pressure |
| **Prerequisites** | Tool response schema defined; required fields identified per task type (ARCH §23) |
| **Expected Benefit** | Source whitepaper example: 10,000 → 1,200 tokens (88% reduction); workload-specific (ARCH §23; PS §6.21) |
| **Optimization Overhead** | Schema-based field selection is negligible; model-based filtering has moderate overhead (ARCH §23) |
| **Quality Risks** | Removal of fields needed for downstream validation or audit (EC-032); mitigated by TE-006's mandatory field preservation (ARCH §23) |
| **Fallback** | Pass the full tool output if filtering cannot run (ARCH §23) |
| **Evaluation Methodology** | Tool-output tokens before vs. after; task-completion rate; audit-field preservation rate (ARCH §23) |
| **Observability Metrics** | `tools.output_tokens_raw`, `tools.output_tokens_filtered`, `tools.filter_ratio`, `tools.task_completion_rate` (ARCH §23) |
| **Implementation Priority** | P1 — High-Confidence Optimization (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Directly guards against "passing complete tool responses to agents" (CONV §24.1); SEC-006 is the specific guardrail against over-filtering |
| **State Dependencies** | Operating mode: `SYNC` (the actual tool result is request-time information). No cached state of its own — the filtering logic runs fresh per call. |
| **Invalidation / Revalidation** | N/A — not applicable to this entry (per-call transformation, not a cached artifact) |
| **Security / Authorization / Policy Constraints** | SEC-006 (tool-result filtering must not remove auth/audit fields); this constraint is independent of, and takes precedence over, the token-savings goal |
| **Data Governance Constraints** | If the raw tool output contains PII, it must pass `PIIClassifier` before caching (though filtering itself is not a cache operation) — DGE classification is upstream of TECH-012's caching decision, not this one |
| **Provider / Model / Tool Dependencies** | Required-field lists are defined per-tool (`conventions.md` §11.3), never assumed universal across tools |
| **Composition / Ordering / Conflicts** | Stage 12 of 24 (CONV §6.1); upstream of TECH-012 (Tool Result Caching) — a filtered result, not a raw one, is what gets cached, so the two compose in a fixed order |
| **Reversibility / Side Effects** | The original, unfiltered result is retained for audit "where policy permits" (PS §6.21); filtering itself has no external side effect |
| **Validated By** | `SCN-TOOL-001` (tool result filtered before admission, normal path) |
| **Traceability** | PS §6.21, §25, §36 TE-006; ES §7.21; ARCH §13.2, §23, §36, §41; INTF §10.3; CONV §6.1, §11.3, §24.1; EC-032, EC-033 |

---

### TECH-012 — Tool Result Caching

| Field | Value |
|---|---|
| **Category** | Cache Optimization / Tool Execution (Layer 1/2 boundary) |
| **Governing Component(s)** | T3.3-TOOL-RESULT-CACHE (ARCH §13.3; INTF-012), gated by **SRP** (dependency-change subscription via DA-024) and **TMG** (Tool/MCP Trust Gate, ARCH §47.2.3) for version-change invalidation. Decision-Ownership: `EXECUTION_OWNERSHIP`. |
| **Applicability** | Deterministic or sufficiently stable tool calls (search, documentation retrieval, database queries, repository metadata, external APIs) (ARCH §23) |
| **Explicitly Not Applicable When** | The tool is explicitly non-idempotent or has side effects (`idempotent: false`, `conventions.md` §11.5) — such results are never cached for reuse across calls |
| **Prerequisites** | Tool-specific TTL and freshness policy; authorization-aware cache keys; parameter normalization (ARCH §23) |
| **Expected Benefit** | Avoids repeated tool-execution cost and latency; proportional to tool-call repetition rate (ARCH §23) |
| **Optimization Overhead** | Cache write/read overhead; freshness-check overhead (ARCH §23) |
| **Quality Risks** | Returning stale tool results (EC-034); mitigated by TTL and explicit invalidation (ARCH §23) |
| **Fallback** | Execute the tool fresh if the cache misses or the freshness check fails (ARCH §23) |
| **Evaluation Methodology** | Tool-calls-avoided %; task completion with cached results; freshness-violation rate (ARCH §23) |
| **Observability Metrics** | `cache.tool.hit_rate`, `cache.tool.miss_rate`, `cache.tool.calls_avoided`, `cache.tool.freshness_violations` (ARCH §23) |
| **Implementation Priority** | P2 — Cost Intelligence; requires per-tool freshness policy (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against "caching mutable/permission-sensitive data without validation" (CONV §24.2) |
| **State Dependencies** | Operating mode: `HYBRID`. Invalidated on permission change (EC-034), tool/MCP version change (SEC-014, extends CL-003), or dependency change (DA-024). |
| **Invalidation / Revalidation** | SRP subscribes to DA-024's change events (repository/branch/dependency/permission/external-state changes); a permission reduction invalidates affected entries across all cache types, including this one (`conventions.md` §12.6 by analogy, PRV §46.2.8) |
| **Security / Authorization / Policy Constraints** | Authorization-aware cache keys (SEC-002/§9.1); TMG validates tool identity/schema staleness independently of any cost/ROI signal before a cached result is trusted (SEC-014) |
| **Data Governance Constraints** | Same retention/classification rules as TECH-009/010, applied per-tool |
| **Provider / Model / Tool Dependencies** | Tool-specific policy — never a single universal TTL across all tools (ARCH §23) |
| **Composition / Ordering / Conflicts** | Downstream of TECH-011 (caches the filtered, not raw, result); upstream consumer of DA-022 (Developer-Agent Cache Layer) for coding-agent-specific tool caching |
| **Reversibility / Side Effects** | `EXECUTION_OWNERSHIP` for the write; explicit invalidation on any of the triggers above |
| **Validated By** | `SCN-CMP-017` (cached context item conflicts with a newly retrieved version); `SCN-FILE-003` (cached file content changes after caching); `SCN-PERM-001` (permission revoked mid-execution invalidates affected cache entries) |
| **Traceability** | PS §6.22, §25, §36 DA-024; ES §7.22; ARCH §13.3, §23, §36, §41, §47.2.3; INTF-012 §6; CONV §6.1, §11.5, §24.2; EC-034, EC-099 |

---

### TECH-013 — Model Routing

| Field | Value |
|---|---|
| **Category** | Model/Provider Routing (Layer 3, owned directly per ARCH §47.9) |
| **Governing Component(s)** | T0.1-MODEL-ROUTER (ARCH §10.1; INTF-016), consulting **CAR** (Capability/Availability Resolver, ARCH §46.2.10) and **SGE**'s remaining-budget signal (advisory only). Decision-Ownership: `ADVISORY` by default, `ENFORCEMENT` if policy mandates a specific tier for a request class. |
| **Applicability** | Any request that can be satisfied by a lower-cost model without quality degradation (ARCH §23) |
| **Explicitly Not Applicable When** | The task is safety-critical or complex and the candidate model is incapable — this is an **explicit architectural prohibition** (ARCH §23, `SCN-MODEL-002`), not merely a risk to weigh |
| **Prerequisites** | Model tier registry; task-complexity assessment; historical quality data per model/intent (ARCH §23) |
| **Expected Benefit** | Substantial cost reduction proportional to tier price differential; RouteLLM research demonstrates the concept (benchmark-specific, not a guarantee — PS §17.5) |
| **Optimization Overhead** | Routing-decision overhead; complexity scoring; quality monitoring required (ARCH §23) |
| **Quality Risks** | Routing safety-critical or complex requests to incapable models — explicitly prohibited (ARCH §23) |
| **Fallback** | Use `OptimizationPolicy.default_model_id` if routing fails (ARCH §23; `conventions.md` §10.1) |
| **Evaluation Methodology** | Cost per request by routing decision; quality score by routed model; routing accuracy vs. gold standard (ARCH §23) |
| **Observability Metrics** | `routing.model_selected`, `routing.candidate_model`, `routing.decision_reason`, `routing.quality_score_by_model`, `routing.escalation_rate` (ARCH §23) |
| **Implementation Priority** | P2 — Cost Intelligence; requires a quality baseline per model before enabling (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against both "always using the strongest/most expensive model" and "always using the cheapest model" (CONV §24.1) — routing must be conditional and evidence-driven in both directions |
| **State Dependencies** | Operating mode: `HYBRID` — model-profile lookups are `ASYNC`-refreshed (§21 profiles); the actual routing decision for a request is `SYNC`/`HYBRID` against current availability (CAR). |
| **Invalidation / Revalidation** | CAR detects real-time capability/availability changes and activates a circuit breaker on provider outage, selecting the next available model in the configured cascade and validating that the failover model meets context-window/tool/compliance constraints (ARCH §46.2.10; EC-109, EC-110) |
| **Security / Authorization / Policy Constraints** | Routing must never be used to route around a compliance/data-residency constraint (DGE, ARCH §47.2.2); a model's compliance profile is a hard constraint, not a cost input |
| **Data Governance Constraints** | Data-residency constraints from the provider/model profile are honored in routing (`conventions.md` §13.7) |
| **Provider / Model / Tool Dependencies** | Model selection is always based on `ModelProfileRegistry`, never hard-coded model names (`conventions.md` §10.1); provider accessibility model applies — discovered ≠ accessible ≠ available ≠ authorized ≠ recommended ≠ eligible (generation prompt §31) — routing must never target an inaccessible or unauthorized model/provider |
| **Composition / Ordering / Conflicts** | Stage 18 of 24 (CONV §6.1), after cache-aware prompt assembly, before reasoning-budget selection; composes directly with TECH-014 (Model Cascade) as its escalation continuation |
| **Reversibility / Side Effects** | `ADVISORY`/`ENFORCEMENT` only; no external side effect from the routing decision itself (the side effect is the subsequent inference call, tracked separately) |
| **Validated By** | `SCN-MODEL-001` (optimal model selected under normal conditions); `SCN-MODEL-002` (router must not route a safety-critical task to a cheap/incapable model); `SCN-MODEL-004` (model becomes unavailable after selection, before dispatch) |
| **Traceability** | PS §6.1, §17.5, §25; ES §7.1; ARCH §10.1, §23, §36, §41, §46.2.10; INTF-016 §9; CONV §6.1, §10.1, §24.1; EC-037, EC-038, EC-039, EC-109, EC-110 |

---

### TECH-014 — Model Cascade / Escalation

| Field | Value |
|---|---|
| **Category** | Model/Provider Routing (Layer 3) |
| **Governing Component(s)** | Model Cascade/Escalation mechanism (ARCH §13.4; INTF-016), governed by **VCL** (Verifier Calibration Layer, ARCH §47.4.3) for the escalation-trigger's confidence signal. Decision-Ownership: `ADVISORY`/`ENFORCEMENT` for the escalation decision. |
| **Applicability** | Workloads where task difficulty varies; cost-sensitive with a quality SLO (ARCH §23) |
| **Explicitly Not Applicable When** | A deterministic verifier (e.g., a passing unit test) already confirms correctness on the first-tier output — escalating "just to be sure" is the always-strongest-model anti-pattern in inverse form (`SCN-NOPT-003`) |
| **Prerequisites** | Model routing enabled; quality evaluator available; escalation-trigger conditions defined (ARCH §23) |
| **Expected Benefit** | FrugalGPT research supports the concept; workload-specific, depends on escalation rate (ARCH §23; PS §17.6) |
| **Optimization Overhead** | First-tier inference cost is paid even on escalation; quality-evaluation cost (ARCH §23) |
| **Quality Risks** | The quality evaluator itself may miss the need to escalate (EC-040); evaluation cost may exceed savings for low-escalation workloads (ARCH §23) |
| **Fallback** | Default to the full-capability model if cascade evaluation fails (ARCH §23; `conventions.md` §10.2) |
| **Evaluation Methodology** | Escalation rate; cost savings vs. always-strong-model; quality parity on escalated requests (ARCH §23) |
| **Observability Metrics** | `cascade.first_tier_model`, `cascade.escalation_triggered`, `cascade.escalation_reason`, `cascade.cost_vs_baseline` (ARCH §23) |
| **Implementation Priority** | P2 — Cost Intelligence; requires a quality evaluator and escalation-rate benchmarking (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | The verifier's `passed = true` output must never be treated as ground truth without a calibrated confidence score (VCL, ARCH §47.4.3, EC-162) — this guards the escalation trigger itself against overconfidence |
| **State Dependencies** | Operating mode: `HYBRID` (AR-004 verifier-guided escalation, `conventions.md` §7.6). Depends on VCL's calibration state for the deployed verifier. |
| **Invalidation / Revalidation** | Verifier acceptance-rate drift (starting to accept results it previously rejected, or vice versa, without a corresponding technique change) is monitored and surfaced as `VERIFIER_DRIFT_DETECTED` (VCL, `conventions.md` §7.10, EC-163) |
| **Security / Authorization / Policy Constraints** | Escalation must not be used to route around a safety/policy requirement that already flagged the first-tier output — a policy failure is fail-closed, not something escalation "fixes" |
| **Data Governance Constraints** | N/A — not applicable to this entry beyond model-routing's existing constraints |
| **Provider / Model / Tool Dependencies** | The escalation target model is itself subject to `ModelProfileRegistry` and provider-accessibility rules (same as TECH-013) |
| **Composition / Ordering / Conflicts** | Directly composes with TECH-013 (Model Routing) as its escalation path, and with TECH-015 (Reasoning Budget Control) — escalating a model and escalating a reasoning budget are distinct levers and `SOURCE-GAP-006` (below) flags that their interaction with an already-dispatched non-idempotent first-tier side effect is not fully specified upstream |
| **Reversibility / Side Effects** | If the first-tier attempt had a non-idempotent side effect (e.g., a tool call with a write), escalation must not blindly re-dispatch that side effect — see `SOURCE-GAP-006` |
| **Validated By** | `SCN-NOPT-003` (escalation correctly skipped when a deterministic verifier already passed); `SCN-CMP-058`, `SCN-CMP-088` (escalation combined with an already-dispatched non-idempotent side effect) |
| **Traceability** | PS §6.23, §17.6, §38 AR-004; ES §7.23; ARCH §13.4, §23, §36, §41, §47.4.3; INTF-016 §9; CONV §7.10, §10.2, §15.5, §24.1; EC-040, EC-041, EC-132, EC-162, EC-163, EC-164, EC-165 |

---
### TECH-015 — Reasoning Budget Control

| Field | Value |
|---|---|
| **Category** | Output/Reasoning Token Optimization (Layer 3, ARCH §47.9) |
| **Governing Component(s)** | T0.3-REASONING-BUDGET-CONTROLLER (ARCH §10.3; INTF §9), consulting **SGE**'s remaining-budget signal (advisory only). Decision-Ownership: `ADVISORY`/`ENFORCEMENT`. |
| **Applicability** | Models with configurable reasoning/thinking budget (provider-specific); tasks that vary in required reasoning depth (ARCH §23) |
| **Explicitly Not Applicable When** | The provider does not support reasoning-budget control at all (EC-043) — the stage is a no-op, not a forced degradation; and **never** when the sole justification is hitting a token target (see Anti-Pattern Check) |
| **Prerequisites** | Provider supports reasoning controls; task-complexity scoring available; quality threshold configured (ARCH §23) |
| **Expected Benefit** | Reduced reasoning-token cost for simple tasks; proportional to reasoning-budget reduction (ARCH §23) |
| **Optimization Overhead** | Complexity-scoring overhead; quality-comparison overhead (ARCH §23) |
| **Quality Risks** | **Explicitly prohibited: never reduce reasoning solely to hit a token target** — must compare quality against baseline (ARCH §23; root `CLAUDE.md` rule 3; `conventions.md` §10.3; EC-042) |
| **Fallback** | Use a safe default reasoning budget if control fails (ARCH §23) |
| **Evaluation Methodology** | Reasoning tokens allocated vs. consumed; correctness before vs. after; quality-gate pass rate (ARCH §23) |
| **Observability Metrics** | `reasoning.budget_allocated`, `reasoning.tokens_consumed`, `reasoning.quality_score`, `reasoning.escalation_count` (ARCH §23) |
| **Implementation Priority** | P2 — Cost Intelligence; provider-specific, requires quality validation per model (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | This entry's **single hardest constraint**: "Never reduce reasoning budget solely to meet a token target" is stated as an explicit architectural prohibition in three independent authoritative locations (PS §6.3, ARCH §23, `conventions.md` §10.3) and as root `CLAUDE.md` rule 3. Any implementation that ties reasoning-budget reduction directly to a token-count target without an intervening quality-baseline comparison violates this catalog. |
| **State Dependencies** | Operating mode: `SYNC`/`HYBRID` (a precomputed complexity-to-budget mapping, revalidated per request) |
| **Invalidation / Revalidation** | If the provider's reasoning-control capability changes (e.g., a model swap mid-cascade), the budget mapping must be re-derived for the new model, not reused from the prior model's profile (EC-043) |
| **Security / Authorization / Policy Constraints** | N/A — not applicable to this entry beyond the general quality-gate requirement |
| **Data Governance Constraints** | N/A — not applicable to this entry |
| **Provider / Model / Tool Dependencies** | Reasoning controls are read from `ModelProfile` per provider; no universal reasoning-token semantics are assumed across providers (`conventions.md` §21.2) |
| **Composition / Ordering / Conflicts** | Stage 19 of 24 (CONV §6.1), after model routing; escalates via AR-004 Verifier-Guided Escalation (TECH-014's governing mechanism) when the initial budget is insufficient |
| **Reversibility / Side Effects** | No external side effect; a budget escalation is simply a retry with a larger allocation, tracked as a distinct ledger entry |
| **Validated By** | `SCN-BUD-005` (reasoning reservation changes mid-execution due to a provider capability downgrade); EC-042 is directly cited by that scenario's traceability |
| **Traceability** | PS §6.3, §25, §38 AR-002; ES §7.3; ARCH §10.3, §23, §36, §41; INTF §9; CONV §10.3, §24.1; EC-042, EC-043 |

---

### TECH-016 — Agent Early Exit

| Field | Value |
|---|---|
| **Category** | Agent/Workflow Optimization (Layer 1) |
| **Governing Component(s)** | T3.1-AGENT-STOP-CONTROLLER (ARCH §13.1; INTF-022), consulting **AL-006** Agent Task Value/Early Exit and the loop-awareness classifier (`conventions.md` §12.5, §47.7). Decision-Ownership: `ENFORCEMENT` (it stops continuation). |
| **Applicability** | Agentic workflows with multiple steps; loops where the objective can be verified before completion (ARCH §23) |
| **Explicitly Not Applicable When** | The objective is not yet actually satisfied — premature termination before real completion is EC-044's documented failure, distinct from a legitimate stop |
| **Prerequisites** | Objective definition; quality thresholds; completion-verification mechanism (ARCH §23) |
| **Expected Benefit** | Avoids unnecessary model and tool calls after the objective is satisfied; token savings proportional to loop count avoided (ARCH §23) |
| **Optimization Overhead** | Completion-evaluation overhead per step (ARCH §23) |
| **Quality Risks** | Premature termination before the objective is truly complete (EC-044); mitigated by a quality gate on the exit decision (ARCH §23) |
| **Fallback** | Continue according to safe workflow policy if the stop decision fails (ARCH §23) |
| **Evaluation Methodology** | Early-exit rate; task-completion rate; tokens/calls avoided; regression rate (ARCH §23) |
| **Observability Metrics** | `agent.early_exit_triggered`, `agent.steps_planned`, `agent.steps_executed`, `agent.steps_skipped`, `agent.tokens_saved_by_exit` (ARCH §23) |
| **Implementation Priority** | P2 — Cost Intelligence; requires a completion-verification mechanism (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against "re-running completed agent steps" (CONV §24.1) from the opposite direction — stopping too early is the mirror-image failure this entry exists to prevent (EC-044) |
| **State Dependencies** | Operating mode: `SYNC`. Consults WVM's `completed_actions`/`unresolved_questions` (ARCH §46.2.3) and the AL-001/AL-002 loop-classifier output (`conventions.md` §47.7) — one expected-value model shared with the CONTINUE decision, not two independent ones. |
| **Invalidation / Revalidation** | N/A — not applicable to this entry (a fresh per-step decision, not a cached result) |
| **Security / Authorization / Policy Constraints** | An early-exit decision must not skip a required human-approval gate (HAG) for a consequential action still pending — stopping the loop does not bypass SEC-015 |
| **Data Governance Constraints** | N/A — not applicable to this entry |
| **Provider / Model / Tool Dependencies** | N/A — not applicable to this entry |
| **Composition / Ordering / Conflicts** | Stage 23 of 24 (CONV §6.1), after LLM execution and output validation; composes with DA-021 (Agent Task Completion/Early-Exit Controller) as the developer-agent specialization of this generic technique |
| **Reversibility / Side Effects** | No destructive side effect; avoided work is recorded (`agent.steps_skipped`), not silently discarded |
| **Validated By** | `SCN-AGENT-001` (single-agent normal completion); `SCN-CMP-039` (step skipped due to early exit whose output was a context dependency for a later step — the compound failure mode this entry must avoid) |
| **Traceability** | PS §6.20, §25, §37 AL-006; ES §7.20; ARCH §13.1, §23, §36, §41, §47.7; INTF-022 §11.3; CONV §12.5, §24.1; EC-044, EC-045, EC-046 |

---

### TECH-017 — Output Schema Enforcement

| Field | Value |
|---|---|
| **Category** | Output/Reasoning Token Optimization (Layer 3) |
| **Governing Component(s)** | Output Schema Selector (PS §6.18; INTF-029 QualityValidator). Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | All structured-output tasks; extraction, classification, coding, tool responses (ARCH §23) |
| **Explicitly Not Applicable When** | Over-constraining the schema would truncate required information (ARCH §23) — the schema itself must be intent-appropriate, not maximally terse |
| **Prerequisites** | Intent classification; output schema defined per intent (ARCH §23) |
| **Expected Benefit** | Source whitepaper: 40–70% output reduction — treat as a validation target (ARCH §23; PS §16); eliminates verbose prose in structured responses |
| **Optimization Overhead** | Schema-validation overhead; re-prompt on schema failure (ARCH §23) |
| **Quality Risks** | Over-constraining schema may truncate required information (ARCH §23; EC-050) |
| **Fallback** | Use fallback validation/re-prompt if the schema fails (ARCH §23) |
| **Evaluation Methodology** | Output tokens before vs. after; schema-compliance rate; completeness score (ARCH §23) |
| **Observability Metrics** | `output.tokens_raw`, `output.tokens_schema_constrained`, `output.schema_compliance_rate` (ARCH §23) |
| **Implementation Priority** | P1 — High-Confidence Optimization (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against treating token reduction as equivalent to business value (CONV §24.2) if enforcing a schema strips information the task genuinely needs |
| **State Dependencies** | Operating mode: `SYNC`. No cross-request state. |
| **Invalidation / Revalidation** | N/A — not applicable to this entry |
| **Security / Authorization / Policy Constraints** | Schema enforcement must not strip a field required for authorization/audit (same principle as SEC-006, applied to output rather than tool-result filtering) |
| **Data Governance Constraints** | N/A — not applicable to this entry |
| **Provider / Model / Tool Dependencies** | Uses provider structured-output/function-calling/grammar-constraint support where available (PS §6.19); falls back to re-prompt validation where not (`conventions.md` §21.2) |
| **Composition / Ordering / Conflicts** | Stage 21 of 24 (CONV §6.1), after LLM execution, before output-length enforcement — schema shape is enforced before length is bounded |
| **Reversibility / Side Effects** | No destructive side effect on input context; a schema failure triggers a re-prompt, not data loss |
| **Validated By** | No dedicated `SCN-OPT-*` scenario isolates schema enforcement in the optimization domain specifically; it is exercised as part of `SCN-CMP-068`'s output-length-forecasting compound scenario. |
| **Traceability** | PS §6.18, §16, §25; ES §7.18; ARCH §12.3, §23, §36; INTF-029 §17; CONV §6.1, §24.2; EC-050 |

---

### TECH-018 — Output Length Control

| Field | Value |
|---|---|
| **Category** | Output/Reasoning Token Optimization (Layer 3) |
| **Governing Component(s)** | Output Length Controller (PS §6.19), consuming **AR-003** Output Budget Forecasting (`conventions.md` §19 analog, PS §38). Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | All requests where output length can be bounded without harming completeness (ARCH §23) |
| **Explicitly Not Applicable When** | Truncation would cut code or structured output mid-statement (EC-051) — the boundary must be semantic, not a raw token cutoff |
| **Prerequisites** | AR-003 Output Budget Forecasting; intent classification; field-level budget policy (ARCH §23) |
| **Expected Benefit** | 40–70% output reduction (source whitepaper); proportional to the verbosity of unconstrained output (ARCH §23) |
| **Optimization Overhead** | Budget enforcement is negligible; truncation-detection overhead (ARCH §23) |
| **Quality Risks** | Truncating required information (EC-051); mitigated by semantic-boundary truncation and controlled expansion (ARCH §23) |
| **Fallback** | Remove the output-length constraint if the quality gate fails (ARCH §23) |
| **Evaluation Methodology** | Output tokens before vs. after; completeness score; quality-gate pass rate (ARCH §23) |
| **Observability Metrics** | `output.tokens_target`, `output.tokens_actual`, `output.truncations`, `output.expansions`, `output.completeness_score` (ARCH §23) |
| **Implementation Priority** | P1 — High-Confidence Optimization (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against treating a hard token cap as more important than completeness — "prefer semantic boundaries" and "permit controlled expansion" (PS §6.19) are mandatory, not optional refinements |
| **State Dependencies** | Operating mode: `SYNC`/`HYBRID` (AR-003's forecast may be precomputed per intent, applied per request) |
| **Invalidation / Revalidation** | N/A — not applicable to this entry |
| **Security / Authorization / Policy Constraints** | N/A — not applicable to this entry |
| **Data Governance Constraints** | N/A — not applicable to this entry |
| **Provider / Model / Tool Dependencies** | Uses provider `max_output_tokens`, stop sequences, and structured-output limits where exposed (PS §6.19) |
| **Composition / Ordering / Conflicts** | Stage 22 of 24 (CONV §6.1), directly after output-schema enforcement; the two compose as shape-then-size constraints on the same output |
| **Reversibility / Side Effects** | Truncation marks are recorded and controlled expansion is permitted — a truncated output is not a silent, unrecoverable loss |
| **Validated By** | `SCN-CMP-068` (output-length forecasting underestimates, truncating mid-structured-output on a failed-over model) |
| **Traceability** | PS §6.19, §25, §38 AR-003; ES §7.19; ARCH §12.4, §23, §36; CONV §6.1, §24.1; EC-051 |

---

### TECH-019 — Batch Optimization

| Field | Value |
|---|---|
| **Category** | Model/Provider Routing & Serving Coordination (Layer 3) |
| **Governing Component(s)** | Batch/Asynchronous Optimizer (PS §6.24). Decision-Ownership: `ADVISORY`/`ENFORCEMENT`. |
| **Applicability** | Asynchronous, non-interactive workloads; bulk extraction, classification, summarization (ARCH §23) |
| **Explicitly Not Applicable When** | `latency_requirements.interactive = true` — batching is never used where freshness or interactive latency makes it inappropriate, regardless of potential cost savings (`SCN-NOPT-004`; PS §6.24) |
| **Prerequisites** | Workload classified as async-eligible; freshness requirements permit batching; latency SLO is not interactive (ARCH §23) |
| **Expected Benefit** | Reduced per-token cost via provider batch pricing; improved throughput (ARCH §23) |
| **Optimization Overhead** | Batching coordination overhead; increased latency for individual requests (ARCH §23) |
| **Quality Risks** | Inappropriate batching of interactive or time-sensitive requests (EC-056) |
| **Fallback** | Process requests individually if batching fails (ARCH §23) |
| **Evaluation Methodology** | Cost per token batch vs. individual; throughput comparison; latency-impact assessment (ARCH §23) |
| **Observability Metrics** | `batch.requests_batched`, `batch.cost_per_token_batch_vs_individual`, `batch.throughput`, `batch.latency_p95` (ARCH §23) |
| **Implementation Priority** | P3 — Advanced Optimization; requires workload-classification infrastructure (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Directly guards against classifying an interactive request as batch-eligible (EC-056; `SCN-NOPT-004`) |
| **State Dependencies** | Operating mode: `ASYNC` for the batch execution itself; `SYNC` for the eligibility classification |
| **Invalidation / Revalidation** | N/A — not applicable to this entry (eligibility is decided once per request, not revalidated mid-flight) |
| **Security / Authorization / Policy Constraints** | Same tenant-isolation rules apply within a batch as within any single request — a batch must never mix `tenant_id` scopes in a way that blends cost/context |
| **Data Governance Constraints** | N/A — not applicable to this entry beyond standard per-item classification |
| **Provider / Model / Tool Dependencies** | Provider-specific batch API/pricing (`ModelProfile.batch_support`) |
| **Composition / Ordering / Conflicts** | Measurement-separate from Layer 3's P5 index capabilities (this is Control-Plane-owned batching policy, not the provider's own continuous-batching infrastructure — H18, ARCH §47.9) |
| **Reversibility / Side Effects** | No destructive side effect; a batch-eligibility misclassification is corrected by processing individually |
| **Validated By** | `SCN-NOPT-004` (batching not used for an interactive, latency-sensitive request); `SCN-CMP-047` (batch/async workload reclassified interactive mid-processing) |
| **Traceability** | PS §6.24, §25; ES §7.24; ARCH §13.5, §23, §36, §41; CONV §24.1; EC-056 |

---

### TECH-020 — Soft Reset / Context Budgeting

| Field | Value |
|---|---|
| **Category** | Context Optimization (Layer 2) |
| **Governing Component(s)** | T1.5-CONTEXT-BUDGETER (ARCH §11.12; INTF-010), gated by **CIG** (Context Integrity Gate, ARCH §46.2.6) and **CEC** (Context Expansion Controller, ARCH §46.2.7). Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Long-running conversations or agent sessions approaching context-window limits (ARCH §23) |
| **Explicitly Not Applicable When** | The overflow cannot be resolved without evicting Tier 0/1 content — CIG returns `TIER_VIOLATION` and the request either surfaces `BUDGET_OVERFLOW` with a section-by-section breakdown, or accepts a `PARTIAL` result; it never proceeds via silent truncation (PS §51.4; `SCN-CTX-003`) |
| **Prerequisites** | Context budget configured per model/tenant/workflow; staged compaction policy (CL-005) defined (ARCH §23) |
| **Expected Benefit** | Prevents context overflow; maintains quality over long sessions; controls token growth (ARCH §23) |
| **Optimization Overhead** | Budget tracking; compaction steps at thresholds (ARCH §23) |
| **Quality Risks** | Loss of important historical context (EC-054); mitigated by key-fact preservation and a reset report (ARCH §23) |
| **Fallback** | Retain the most-recent bounded context if compaction fails (ARCH §23) |
| **Evaluation Methodology** | Context size over session length; quality at various compaction thresholds; key-fact retention rate (ARCH §23) |
| **Observability Metrics** | `budget.tokens_used`, `budget.ceiling`, `budget.compaction_events`, `budget.reset_events`, `budget.key_facts_preserved` (ARCH §23) |
| **Implementation Priority** | P1 — High-Confidence Optimization (ARCH §36) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against "assuming larger context windows eliminate context-management problems" (CONV §24.2) and against silent truncation, which is unconditionally prohibited at every tier boundary (PS §51.4) |
| **State Dependencies** | Operating mode: `SYNC`. Implements CL-005's staged compaction (0–40% full fidelity → 40–60% dedup → 60–70% remove low-value → 70–80% compress → 80–90% aggressive selective loading → 90%+ controlled reset/phase transition — `conventions.md` §8.1). Depends on `context_version` (CVM) throughout. |
| **Invalidation / Revalidation** | Every eviction tier transition follows the mandatory Eviction Protocol (PS §51.4): identify Tier 4 candidates with no live dependents → evict → if overflow persists, compress Tier 3 → then Tier 2 (never evict) → if still over, return `BUDGET_OVERFLOW`. `CEC` handles `ContextExpansionRequest` if a previously-evicted item becomes necessary again (ARCH §46.2.7). |
| **Security / Authorization / Policy Constraints** | Tier 0 (SEC instructions, safety constraints, audit-required fields) may **never** be evicted under any budget condition (PS §51.4; root `CLAUDE.md` rule 6) |
| **Data Governance Constraints** | DGE classification is itself Tier 0 for the purpose of this budgeting logic — sensitivity classification metadata is never evicted to make room (ARCH §47.2.2) |
| **Provider / Model / Tool Dependencies** | The default reference budget (500K tokens per PS §6.15) is a starting point, configurable by model/provider/workflow/tenant/user/intent/complexity — never a fixed universal ceiling |
| **Composition / Ordering / Conflicts** | Stage 15 of 24 (CONV §6.1), after context compression, as the escalation path once compression alone is insufficient; composes with TECH-003/004/007 as its constituent mechanisms (rank → prune → dedup → compress, applied progressively per CL-005's thresholds) |
| **Reversibility / Side Effects** | Every eviction carries a `ReversibilityRecord` (CL-004); a "controlled session reset" is itself recorded, not a silent restart |
| **Validated By** | `SCN-CTX-002` (context overflow triggers the tiered eviction protocol); `SCN-CTX-003` (unresolvable overflow — Tier 0/1 would have to be evicted); `SCN-CTX-010` (context reconstruction after a full session reset) |
| **Traceability** | PS §6.15, §25, §34 CL-004, CL-005; ES §7.15; ARCH §11.12, §23, §36, §41, §46.2.6, §46.2.7; INTF-010 §5.7; CONV §6.1, §8.1, §24.2; EC-054, EC-055, EC-094 |

---
## Developer-Agent Optimization Modules

All 25 DA modules are mandatory first-class requirements (PS §4C; ARCH §22; `conventions.md` §23) — developer/coding-agent optimization is a first-class product requirement, not a variant of the generic techniques above. Every DA module below is bounded by the platform's declared **Feasibility Tier** (FTR, ARCH §47.10; `conventions.md` §23.1): a module may only be wired to an integration whose declared tier's `reachable_modules` includes it, and the framework must not report or imply full-pipeline coverage for a Tier 3–5 (Plugin/Extension, Protocol/Tool-Level, or Advisory/Observability-Only) integration. This constraint is stated once here and referenced by short form ("FTR-bounded") in each entry rather than repeated in full.

---

### DA-001 — Code-Aware Context Pruner

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization (specializes TECH-003 for code) |
| **Governing Component(s)** | DA-001 (ARCH §22; `conventions.md` §23), consulting CL-001 Context Dependency Graph. FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Coding-agent context containing files/symbols not required for the current task |
| **Explicitly Not Applicable When** | A candidate item is a task-critical dependency per the dependency graph (CL-001) — pruning must preserve these regardless of relevance score (`conventions.md` §23 row for DA-001) |
| **Prerequisites** | Repository context available; dependency graph constructed (CL-001) |
| **Expected Benefit** | Reduces repository/code context volume; magnitude workload-dependent — no fabricated percentage is asserted here (root `CLAUDE.md` rule 5, 7) |
| **Optimization Overhead** | Dependency-graph traversal cost per pruning pass |
| **Quality Risks** | Removing a task-critical code dependency (EC-013's delayed-relevance risk, specialized to code); mitigated by CL-001's dependency-aware pruning |
| **Fallback** | Retain bounded original code context if pruning fails (generic TECH-003 fallback, applied here) |
| **Evaluation Methodology** | Same as TECH-003 (context reduction %, recall@K, answer/patch-quality comparison), measured on a coding-task benchmark corpus |
| **Observability Metrics** | Extends TECH-003's metrics with `da001.dependency_preserved_rate` |
| **Implementation Priority** | P1-equivalent (bundled with generic context pruning's validation gate; ARCH §36 does not separately tier DA modules, so this catalog applies the closest generic technique's gate — see `SOURCE-GAP-OPTCAT-01`) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Same as TECH-003, specialized: must not indiscriminately prune code the way generic context pruning is prohibited from indiscriminately compressing (CONV §24.1) |
| **State Dependencies** | Depends on CL-001's dependency graph and `context_version`; operating mode `SYNC` |
| **Invalidation / Revalidation** | DA-024 (Context Invalidation Engine) invalidates this module's output when files/branches/dependencies change |
| **Security / Authorization / Policy Constraints** | Same as TECH-003: never prunes Tier 0/1 content (e.g., a security-relevant constraint embedded in code comments or config) |
| **Data Governance Constraints** | DGE classification applies to code content the same as any other context |
| **Provider / Model / Tool Dependencies** | FTR-bounded: requires at least Tier 2 (Gateway/Interception) to observe the full model-bound code context pre-inference; a Tier 4/5 integration cannot apply this module meaningfully (EC-189, EC-190) |
| **Composition / Ordering / Conflicts** | Composes with DA-002 (Symbol-Level Context Selector) as the finer-grained alternative to whole-file pruning, and with DA-025 (Patch/Change Impact Analyzer) which supplies its relevance signal |
| **Reversibility / Side Effects** | Reversible per CL-004, same as TECH-003 |
| **Validated By** | `SCN-CODE-001` (symbol-level context selection instead of whole-file loading — the DA-002 case that DA-001 generalizes) |
| **Traceability** | PS §4 DA-001; ES §4.4 DA-001; ARCH §22; INTF-006 §5.3 (generic ContextPruner, specialized); CONV §23 (DA-001 row); EC-013 |

---

### DA-002 — Symbol-Level Context Selector

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization |
| **Governing Component(s)** | DA-002 (ARCH §22). FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Any coding task where a whole file's content exceeds what the task requires |
| **Explicitly Not Applicable When** | The task genuinely requires whole-file context (e.g., a full-file rewrite or a style/formatting pass that must see the entire file) |
| **Prerequisites** | Symbol index for the repository (functions, methods, classes, interfaces, types, imports, callers/callees, references) |
| **Expected Benefit** | Reduces admitted context to relevant symbols instead of whole files; magnitude workload-dependent |
| **Optimization Overhead** | Symbol-index construction/lookup cost |
| **Quality Risks** | Missing a caller/callee that materially affects correctness if the symbol graph is incomplete |
| **Fallback** | Fall back to whole-file loading (DA-016's lazy-loading fallback chain) if symbol resolution fails |
| **Evaluation Methodology** | Tokens per file selected (symbol-level vs. whole-file); patch-correctness comparison |
| **Observability Metrics** | `da002.symbols_selected`, `da002.whole_file_fallback_rate` |
| **Implementation Priority** | P1-equivalent (see DA-001's note) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against "sending every retrieved chunk to the LLM" specialized to code — prefer `content_type: SIGNATURE_ONLY` when body is not required (`conventions.md` §23) |
| **State Dependencies** | Depends on the repository map/symbol index (DA-003) and `context_version`; operating mode `SYNC` |
| **Invalidation / Revalidation** | Invalidated by DA-024 on file/symbol change |
| **Security / Authorization / Policy Constraints** | Same as TECH-003/DA-001 |
| **Data Governance Constraints** | N/A — not applicable to this entry beyond standard classification |
| **Provider / Model / Tool Dependencies** | FTR-bounded, same as DA-001 |
| **Composition / Ordering / Conflicts** | Composes with DA-001 (coarser pruning) and DA-016 (File-Section Lazy Loader) as a three-tier specificity chain: symbol → section → whole file |
| **Reversibility / Side Effects** | Non-destructive selection (a subset choice, not an eviction of the Logical Task Context) |
| **Validated By** | `SCN-CODE-001` (symbol-level context selection instead of whole-file loading) |
| **Traceability** | PS §4 DA-002; ARCH §22; CONV §23 (DA-002 row); EC-009 (entity extraction empty-set risk, adjacent) |

---

### DA-003 — Repository Map Generator and Cache

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization / Cache |
| **Governing Component(s)** | DA-003 (ARCH §22), consulting DA-024 (Context Invalidation Engine) for commit-based invalidation. FTR-bounded. Decision-Ownership: `EXECUTION_OWNERSHIP` for the cache write. |
| **Applicability** | Any coding-agent session where the repository structure would otherwise be rediscovered repeatedly |
| **Explicitly Not Applicable When** | The repository has just been force-pushed/history-rewritten (`SCN-CMP-037`) — the stale map must be invalidated and rebuilt before reuse, not silently reused |
| **Prerequisites** | Repository access; commit/branch identifiers for cache-key construction |
| **Expected Benefit** | Avoids repeatedly rediscovering the codebase; compact tree format keeps the admitted representation small |
| **Optimization Overhead** | Map-generation cost (amortized via caching); `ASYNC` refresh cost |
| **Quality Risks** | A stale map after a branch switch (EC-073) leads the agent to reason about a codebase state that no longer exists |
| **Fallback** | Regenerate the map fresh if the cache is stale or unavailable |
| **Evaluation Methodology** | Map-generation avoided count; staleness-detection accuracy after branch switches |
| **Observability Metrics** | `da003.map_cache_hit_rate`, `da003.staleness_detected_count` |
| **Implementation Priority** | P1-equivalent |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against treating a cache hit as a quality guarantee (CONV §24.2) — a repository-map hit is only valid if commit-based invalidation confirms freshness |
| **State Dependencies** | Operating mode: `ASYNC` (ARCH §47.5 lists DA-003 explicitly as an ASYNC example) — the map is refreshed on a slow cadence and looked up synchronously |
| **Invalidation / Revalidation** | Commit-based invalidation (`conventions.md` §23 DA-003 row); DA-024 invalidates on branch switch, force-push, or dependency change (EC-073, `SCN-CODE-002`, `SCN-CMP-037`) |
| **Security / Authorization / Policy Constraints** | The map must not expose repository paths/content the current identity is not authorized to see |
| **Data Governance Constraints** | Tenant-isolated per the standard cache rules (`conventions.md` §9.1), scoped additionally by repository/workspace |
| **Provider / Model / Tool Dependencies** | FTR-bounded; requires filesystem/VCS access, which is itself gated by the integration's feasibility tier |
| **Composition / Ordering / Conflicts** | Supplies the compact-context substrate that DA-001/DA-002/DA-016/DA-017 all consult |
| **Reversibility / Side Effects** | Cache write is `EXECUTION_OWNERSHIP`; invalidation is explicit, never silent |
| **Validated By** | `SCN-CODE-002` (repository map stale after branch switch); `SCN-CMP-037` (repository force-pushed mid-session) |
| **Traceability** | PS §4 DA-003; ARCH §22; CONV §23 (DA-003 row); EC-073, EC-136 |

---

### DA-004 — Git-Diff Optimizer

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization |
| **Governing Component(s)** | DA-004 (ARCH §22). FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Any task involving a diff larger than what the task needs in full detail |
| **Explicitly Not Applicable When** | The task is a code-review task whose entire purpose is to inspect the full diff — summarization would defeat the task itself |
| **Prerequisites** | Git-diff access; a summarization policy for large diffs |
| **Expected Benefit** | Summarizes large diffs while exposing detailed changed sections on demand, rather than admitting the full diff unconditionally |
| **Optimization Overhead** | Summarization cost (model-based or heuristic) |
| **Quality Risks** | Summarization losing a security-relevant change; mitigated by retaining `raw_diff` for audit |
| **Fallback** | Expose the raw diff if summarization fails |
| **Evaluation Methodology** | Diff tokens before vs. after; task-completion rate on diff-dependent tasks |
| **Observability Metrics** | `da004.diff_tokens_raw`, `da004.diff_tokens_summarized` |
| **Implementation Priority** | P1-equivalent |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Must expose `optimized_diff` while retaining `raw_diff` only for audit (`conventions.md` §23 DA-004 row) — never discard the raw diff entirely |
| **State Dependencies** | Depends on the current Git state; invalidated by any subsequent commit to the same branch |
| **Invalidation / Revalidation** | DA-024 invalidates on repository state change (`SCN-CODE-005`) |
| **Security / Authorization / Policy Constraints** | Summarization must not drop a security-relevant change (analogous to EC-015's constraint-removal risk) |
| **Data Governance Constraints** | N/A — not applicable to this entry beyond standard classification |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Composes with DA-025 (Patch/Change Impact Analyzer), which consumes the diff to estimate affected files/symbols/tests |
| **Reversibility / Side Effects** | Raw diff retained for audit — fully recoverable |
| **Validated By** | `SCN-CODE-003` (huge diff summarized with detailed sections available on demand) |
| **Traceability** | PS §4 DA-004; ARCH §22; CONV §23 (DA-004 row); EC-136 |

---

### DA-005 — Terminal Output Optimizer

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization |
| **Governing Component(s)** | DA-005 (ARCH §22). FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Any terminal/build/test output whose raw volume would otherwise be admitted in full |
| **Explicitly Not Applicable When** | The raw output itself is the deliverable requested by the user (e.g., "show me the exact build log") |
| **Prerequisites** | Terminal-output capture; filtering/summarization/deduplication policy |
| **Expected Benefit** | Filters, summarizes, structures, and deduplicates large terminal output; specifically prevents context overflow from build/test output (`SCN-CODE-004`) |
| **Optimization Overhead** | Parsing/filtering cost proportional to raw output size |
| **Quality Risks** | Losing an actionable failure buried in noise; mitigated by DA-006's targeted extraction running downstream of this module |
| **Fallback** | Pass raw terminal output if filtering fails — "never pass raw terminal output" is the default-path prohibition, but the *fallback* path is explicitly the exception (`conventions.md` §23 DA-005 row states the normal-path rule; PS §11's universal fallback model permits reverting to the unoptimized representation on failure) |
| **Evaluation Methodology** | Terminal-output tokens before vs. after; actionable-failure preservation rate |
| **Observability Metrics** | `da005.tokens_raw`, `da005.tokens_filtered` |
| **Implementation Priority** | P1-equivalent |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | "Never pass raw terminal output" as the default path (`conventions.md` §23 DA-005 row) — guards against context overflow from noisy build tools |
| **State Dependencies** | Per-invocation; no persistent state of its own |
| **Invalidation / Revalidation** | N/A — not applicable to this entry (fresh per terminal invocation) |
| **Security / Authorization / Policy Constraints** | Terminal output may contain secrets/credentials printed by a misconfigured tool — DGE/PIIClassifier screening applies before this content is cached or logged (SEC-004, §13.3) |
| **Data Governance Constraints** | Same as above |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Upstream of DA-006 (Compiler/Test/Linter Error Extractor), which operates on this module's filtered output |
| **Reversibility / Side Effects** | No external side effect; a non-destructive filter over ephemeral output |
| **Validated By** | `SCN-CODE-004` (build/test output causing context overflow is extracted, not admitted raw) |
| **Traceability** | PS §4 DA-005; ARCH §22; CONV §23 (DA-005 row); EC-136 (adjacent) |

---
### DA-006 — Compiler / Test / Linter Error Extractor

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization |
| **Governing Component(s)** | DA-006 (ARCH §22). FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Any compiler/test/linter output containing actionable failures mixed with noise |
| **Explicitly Not Applicable When** | The task requires the full raw output for reasons other than fixing the failure (e.g., archiving a CI log) |
| **Prerequisites** | DA-005's filtered terminal output; a schema for `file_path`, `line_number`, `expected_value`, `actual_value` (`conventions.md` §23 DA-006 row) |
| **Expected Benefit** | Extracts actionable failures, locations, stack traces, and expected/actual values instead of admitting raw noisy output |
| **Optimization Overhead** | Parsing cost proportional to output size |
| **Quality Risks** | An agent-loop cost controller (DA-014) stopping a loop that is actually making progress because extraction under-signals genuine remaining work (EC-075) |
| **Fallback** | Fall back to DA-005's filtered (not extracted) output if extraction fails |
| **Evaluation Methodology** | Extraction precision/recall against a labeled failure corpus; downstream fix-success rate |
| **Observability Metrics** | `da006.failures_extracted`, `da006.extraction_confidence` |
| **Implementation Priority** | P1-equivalent |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against surfacing noise as if it were signal — extraction must be actionable, not merely shorter |
| **State Dependencies** | Consumes DA-005's output; no persistent state |
| **Invalidation / Revalidation** | N/A — not applicable to this entry |
| **Security / Authorization / Policy Constraints** | Same secret-scrubbing concern as DA-005 applies to any credential incidentally present in a stack trace |
| **Data Governance Constraints** | Same as DA-005 |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Downstream of DA-005; upstream of DA-012 (Error-Driven Context Retrieval), which consumes extracted error locations to drive targeted retrieval |
| **Reversibility / Side Effects** | Non-destructive |
| **Validated By** | `SCN-CODE-004` (build/test output extraction) |
| **Traceability** | PS §4 DA-006; ARCH §22; CONV §23 (DA-006 row); EC-075 |

---

### DA-007 — Dynamic MCP / Tool Selector

| Field | Value |
|---|---|
| **Category** | Developer-Agent Tool Execution Optimization |
| **Governing Component(s)** | DA-007 (ARCH §22), gated by **TMG** (Tool/MCP Trust Gate, ARCH §47.2.3) before selection. FTR-bounded. Decision-Ownership: `ADVISORY` into selection, `ENFORCEMENT` on the exposed set. |
| **Applicability** | Large MCP/tool ecosystems where exposing every tool upfront would be wasteful (TE-003 analog) |
| **Explicitly Not Applicable When** | The integration's declared feasibility tier does not grant tool-discovery visibility at all (Tier 5, Advisory/Observability-Only) |
| **Prerequisites** | Tool index/registry; task-context signal to drive relevance filtering |
| **Expected Benefit** | Exposes only MCP tools/resources/capabilities relevant to the current task, reducing tool-schema token overhead |
| **Optimization Overhead** | Tool-discovery/relevance-filtering cost |
| **Quality Risks** | A dynamically-discovered tool's availability assumed to persist past its revalidation interval (EC-175) |
| **Fallback** | Fall back to the full registered tool set if dynamic selection fails |
| **Evaluation Methodology** | Tool-schema tokens exposed vs. full-catalog baseline; task-completion rate |
| **Observability Metrics** | `da007.tools_exposed`, `da007.tools_available_total` |
| **Implementation Priority** | P4-equivalent (Dynamic tool loading is listed under ARCH §36's P4 Control-Plane Intelligence tier) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against "exposing large tool catalogs when on-demand discovery is possible" (CONV §24.2) |
| **State Dependencies** | Tool index is revalidated on a policy-defined interval (TMG, ARCH §47.2.3); operating mode `HYBRID` |
| **Invalidation / Revalidation** | TMG revalidates identity/schema/staleness on interval; a schema change between calls is a staleness/trust event, not silently accepted (SEC-014, EC-173) |
| **Security / Authorization / Policy Constraints** | TMG authorization is independent of TE-001's ROI signal — a favorable tool ROI never substitutes for a trust decision (EC-174, `SCN-TOOL-007`) |
| **Data Governance Constraints** | N/A — not applicable to this entry beyond the standard tool-trust rules |
| **Provider / Model / Tool Dependencies** | FTR-bounded; requires MCP/tool-protocol access, which by definition exists at Tier 4 (Protocol/Tool-Level) or deeper |
| **Composition / Ordering / Conflicts** | Composes with DA-008 (Tool Schema Optimizer) — DA-007 decides *which* tools are exposed, DA-008 decides *how much* schema each exposed tool carries |
| **Reversibility / Side Effects** | No destructive side effect; a narrower exposed set is a scoping choice, not a permanent removal |
| **Validated By** | `SCN-AGENT-004` (agent requests an unavailable tool — the boundary case this module must handle); `SCN-CMP-066` (programmatic tool execution aggregating calls with different authorization scopes) |
| **Traceability** | PS §4 DA-007, §36 TE-003; ARCH §22, §47.2.3; INTF §14; CONV §23 (DA-007 row), §13.8; EC-036, EC-174, EC-175 |

---

### DA-008 — Tool Schema Optimizer

| Field | Value |
|---|---|
| **Category** | Developer-Agent Tool Execution Optimization |
| **Governing Component(s)** | DA-008 (ARCH §22), consulting TMG for schema-integrity validation. FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Any exposed tool whose full schema exceeds what invocation actually requires |
| **Explicitly Not Applicable When** | Minimizing the schema would remove a constraint, authorization parameter, or invocation-semantics detail the model needs to call the tool correctly |
| **Prerequisites** | `MCPToolFilter.max_schema_tokens` policy (`conventions.md` §11.2) |
| **Expected Benefit** | Minimizes unnecessary tool-definition context while preserving invocation semantics, constraints, authorization, and parameters (ARCH §22) |
| **Optimization Overhead** | Schema-minimization cost per exposed tool |
| **Quality Risks** | Tool filter schema incompatible with the current tool version if minimization is computed against a stale schema (EC-033) |
| **Fallback** | Load the full schema on demand if minimization fails (`conventions.md` §11.2) |
| **Evaluation Methodology** | Schema tokens before vs. after; invocation-success rate |
| **Observability Metrics** | `da008.schema_tokens_raw`, `da008.schema_tokens_minimized` |
| **Implementation Priority** | P1-equivalent (Tool Schema Minimization is grouped with the generic Tool Output Filtering validation gate) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Must preserve invocation semantics, constraints, authorization, and parameters — minimization is never allowed to silently drop an authorization-relevant field (same principle as SEC-006, applied to schemas) |
| **State Dependencies** | Tool schema version is tracked (TMG, extends CL-003); operating mode `HYBRID` |
| **Invalidation / Revalidation** | Cached tool result or schema invalidated on tool/MCP version change (EC-176, `SCN-TOOL-006`) |
| **Security / Authorization / Policy Constraints** | SEC-014 (schema integrity is part of the security/control boundary) |
| **Data Governance Constraints** | N/A — not applicable to this entry |
| **Provider / Model / Tool Dependencies** | FTR-bounded; same protocol-level dependency as DA-007 |
| **Composition / Ordering / Conflicts** | Downstream of DA-007's tool-selection decision |
| **Reversibility / Side Effects** | Non-destructive; full schema recoverable on demand |
| **Validated By** | `SCN-CODE-007` (MCP tool schema changes mid-session); `SCN-TOOL-006` (tool schema changes between calls without a version bump) |
| **Traceability** | PS §4 DA-008, §36 TE-002; ARCH §22, §47.2.3; CONV §11.2, §23 (DA-008 row), §13.8; EC-033, EC-173, EC-176 |

---

### DA-009 — Sub-Agent Result Compressor

| Field | Value |
|---|---|
| **Category** | Developer-Agent Agent/Workflow Optimization |
| **Governing Component(s)** | DA-009 (ARCH §22), implementing **AL-005** Sub-Agent Handoff Compression (`conventions.md` §12.4). FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Every sub-agent handoff, without exception |
| **Explicitly Not Applicable When** | Never — this module has no legitimate bypass; **full conversation-history replay to a sub-agent is unconditionally prohibited** (root `CLAUDE.md` rule 8; `conventions.md` §12.4, §24.3; INTF §33) |
| **Prerequisites** | `SubAgentContextHandoff` schema (INTF-023 §12) defined: objective, findings, evidence, locations, confidence, decisions, unresolved questions, recommendations |
| **Expected Benefit** | Converts verbose sub-agent results into compact structured findings while retaining evidence, locations, confidence, unresolved questions, and recommendations (ARCH §22) |
| **Optimization Overhead** | Compression/structuring cost per handoff |
| **Quality Risks** | Dropping unresolved questions during compression (EC-074) — this is the single most severe failure mode for this module, since unresolved questions are what drive continuation |
| **Fallback** | **There is no "pass the full transcript instead" fallback** — this would violate root `CLAUDE.md` rule 8. On compression failure, the correct fallback is to retry compression or escalate/abort the handoff, never to replay full history. |
| **Evaluation Methodology** | Unresolved-question preservation rate; parent-agent task-completion rate using the compressed handoff vs. a (test-only, never-production) full-transcript baseline |
| **Observability Metrics** | `da009.unresolved_questions_preserved`, `da009.handoff_tokens` |
| **Implementation Priority** | P1-equivalent (AL-005 is a core, non-deferrable requirement given the absolute prohibition it enforces) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Directly guards against "full conversation replay to sub-agent" — INTF §33's Interface Anti-Patterns table lists this explicitly with the correct approach: use `SubAgentContextHandoff` |
| **State Dependencies** | Depends on the sub-agent's completed work state (WVM); operating mode `SYNC` |
| **Invalidation / Revalidation** | N/A — not applicable to this entry (a handoff is constructed once at spawn-completion, not cached/reused) |
| **Security / Authorization / Policy Constraints** | The compressed handoff must not silently drop a security-relevant finding under the guise of compactness |
| **Data Governance Constraints** | If the sub-agent's findings include sensitive content, DGE classification propagates through the handoff, it does not reset |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Composes with AL-003 (Sub-Agent Value Gate, `conventions.md` §12.3), which decides *whether* to spawn, and AL-004 (Sub-Agent Scheduler); DA-009 governs the *return* path once a sub-agent completes |
| **Reversibility / Side Effects** | The compressed handoff is what the parent agent sees; the sub-agent's full transcript may be retained separately for audit where policy permits, but is never re-admitted to the parent's context by default |
| **Validated By** | `SCN-AGENT-005` (sub-agent handoff uses the compressed object, never the full transcript); `SCN-CMP-008` (sub-agent handoff with oversized context during a model failover) |
| **Traceability** | PS §4 DA-009, §37 AL-005; ARCH §18.5 (AL-005), §22; INTF-023 §12; CONV §12.4, §23 (DA-009 row), §24.3; EC-049, EC-074 |

---

### DA-010 — Agent Memory Optimizer

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization |
| **Governing Component(s)** | DA-010 (ARCH §22), subordinate to **Memory Authority** (ESM/CVM/WVM, `conventions.md` §12.6; ARCH §47.3.1). FTR-bounded. Decision-Ownership: `ENFORCEMENT` within agent-owned memory; never `EXECUTION_OWNERSHIP` over Control-Plane execution truth. |
| **Applicability** | Any long-running coding-agent session accumulating both short-term task state and durable project/task memory |
| **Explicitly Not Applicable When** | The memory content is disputed against ESM/CVM/WVM-owned execution truth — in that case Memory Authority's conflict resolution governs, not this module's own retention heuristics |
| **Prerequisites** | `MemoryStore`/`MemoryEntry` (INTF-028 §16); hierarchical memory layers (CL-006: TURN, TASK, SESSION, PROJECT, ORGANIZATION, HISTORICAL) |
| **Expected Benefit** | Separates short-term task state from durable project/task memory and retains only useful information (ARCH §22) |
| **Optimization Overhead** | Per-layer retention/compression policy evaluation |
| **Quality Risks** | Agent-owned memory believing a step is complete when WVM's `completed_actions` says otherwise (EC-118); agent memory conflicting with current policy state (EC-196) |
| **Fallback** | On ambiguous provenance, default to treating the memory claim as stale/untrusted (`conventions.md` §12.6) |
| **Evaluation Methodology** | Memory-conflict detection rate; retention-policy compliance per layer |
| **Observability Metrics** | `da010.memory_conflicts_detected`, `da010.layer_retention_compliance` |
| **Implementation Priority** | P1-equivalent |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Directly guards against "allowing agent-owned working memory to override Control Plane execution state, authorization state, policy state, or context/workflow version" (CONV §24.4, H09) |
| **State Dependencies** | Every read that could inform an execution-truth-affecting decision carries a `memory_version`/provenance tag compared against current ESM/CVM/WVM state (`MemoryAuthorityCheck`, INTF §43.13) |
| **Invalidation / Revalidation** | On disagreement, the ESM/CVM/WVM-owned value wins and the disagreement is surfaced as an event, never silently resolved in memory's favor (ARCH §47.3.1) |
| **Security / Authorization / Policy Constraints** | Memory must never be treated as a substitute for a `reconcile()` call against ESM (`conventions.md` §12.6) |
| **Data Governance Constraints** | Each memory layer has independent retention (Appendix C: TURN = session lifetime, configurable elsewhere) |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Composes with DA-018 (Agent Plan Context Optimizer) for plan-specific memory and with DA-023 (Context Provenance Tracker) for the provenance tags this module's conflict-check depends on |
| **Reversibility / Side Effects** | Memory writes are upsert-semantic (`entry_id` idempotency key, `conventions.md` §4.5); not independently reversible via CL-004 (memory has its own retention/versioning model) |
| **Validated By** | `SCN-AGENT-007` (agent state conflicts with Control Plane execution state); `SCN-MEM-002` (agent-owned memory conflicts with authoritative external system state); `SCN-MEM-004`, `SCN-MEM-005` |
| **Traceability** | PS §4 DA-010, §34 CL-006, §52.9; ARCH §15.6 (CL-006), §22, §47.3.1; INTF-028 §16, INTF §43.13; CONV §12.6, §23 (DA-010 row); EC-118, EC-119, EC-120, EC-196, EC-197 |

---
### DA-011 — Code-Aware Semantic Deduplicator

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization (specializes TECH-004 for code) |
| **Governing Component(s)** | DA-011 (ARCH §22). FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Duplicate/equivalent code, stack traces, errors, files, search results, diffs, and findings within a coding-agent session |
| **Explicitly Not Applicable When** | Two similar code blocks or stack traces differ in a way that changes their meaning (the code analog of EC-016's near-duplicate-with-different-constraint risk) |
| **Prerequisites** | Canonical representation for each content type (code AST/hash, stack-trace normalization, diff hash) |
| **Expected Benefit** | Reduces redundant admission of equivalent code/errors/findings across a session |
| **Optimization Overhead** | Comparison cost proportional to candidate-set size |
| **Quality Risks** | Same as TECH-004, specialized to code artifacts |
| **Fallback** | Retain all candidates if deduplication cannot run |
| **Evaluation Methodology** | Duplicate-removal rate on a labeled code-artifact corpus |
| **Observability Metrics** | `da011.duplicates_removed`, `da011.near_duplicate_count` |
| **Implementation Priority** | P1-equivalent |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Same as TECH-004 |
| **State Dependencies** | Operating mode `SYNC`; depends on `context_version` |
| **Invalidation / Revalidation** | Same as TECH-004 |
| **Security / Authorization / Policy Constraints** | Same as TECH-004; tenant-isolated |
| **Data Governance Constraints** | Same as TECH-004 |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Composes with DA-017 (Repository Search Result Optimizer), which ranks/deduplicates a related but distinct artifact class (search results specifically) |
| **Reversibility / Side Effects** | Reversible per CL-004 |
| **Validated By** | No dedicated DA-011-specific scenario exists; `SCN-CTX-007` (contradictory context from two sources) is the closest generic analog — flagged as thin coverage, not force-fit. |
| **Traceability** | PS §4 DA-011; ARCH §22; CONV §23 (DA-011 row); EC-016, EC-017 |

---

### DA-012 — Error-Driven Context Retrieval

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization / Retrieval |
| **Governing Component(s)** | DA-012 (ARCH §22), consulting **CEC** (Context Expansion Controller, ARCH §46.2.7) when retrieval requires re-admitting previously evicted context. FTR-bounded. Decision-Ownership: `ADVISORY` into retrieval targeting. |
| **Applicability** | Any task where a compiler/test/runtime error's source location can drive targeted retrieval |
| **Explicitly Not Applicable When** | No error signal exists yet (e.g., initial task planning before any test/build has run) — this module has nothing to target until DA-006 produces an extracted failure |
| **Prerequisites** | DA-006's extracted error locations |
| **Expected Benefit** | Uses compiler/test/runtime errors and source locations to drive targeted retrieval instead of broad/undirected search |
| **Optimization Overhead** | Retrieval cost, bounded by the error-location signal's specificity |
| **Quality Risks** | An incomplete error signal driving retrieval to the wrong location |
| **Fallback** | Fall back to DA-017's general repository-search optimization if error-driven targeting fails |
| **Evaluation Methodology** | Retrieval precision when driven by error location vs. general search |
| **Observability Metrics** | `da012.error_driven_retrievals`, `da012.fix_success_rate` |
| **Implementation Priority** | P1-equivalent |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against undirected, broad retrieval when a precise error signal is available — using a fixed retrieval strategy regardless of signal quality is the code-specific analog of the fixed-Top-K anti-pattern |
| **State Dependencies** | Depends on DA-006's output and the current repository state; operating mode `SYNC` |
| **Invalidation / Revalidation** | If the underlying error is resolved or the repository changes, a stale error-driven retrieval target must be re-evaluated (EC-136) |
| **Security / Authorization / Policy Constraints** | Retrieved context remains subject to authorization filtering regardless of how it was targeted |
| **Data Governance Constraints** | N/A — not applicable to this entry |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Downstream of DA-006; composes with DA-002 (Symbol-Level Context Selector) to retrieve at symbol granularity around the error location |
| **Reversibility / Side Effects** | Additive retrieval, non-destructive |
| **Validated By** | `SCN-CODE-004` (build/test output extraction feeding retrieval) |
| **Traceability** | PS §4 DA-012; ARCH §22; CONV §23 (DA-012 row); EC-136 |

---

### DA-013 — Developer-Agent Context Budget Manager

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization (specializes TECH-020 for code) |
| **Governing Component(s)** | DA-013 (ARCH §22), implementing the Context Priority Order (`conventions.md` §8.2: SYSTEM → DEVELOPER_INSTRUCTIONS → COMPILER_ERRORS → SYMBOL_CONTEXT → GIT_DIFF → FILE_CONTENT → CONVERSATION → REPOSITORY_MAP → TERMINAL_OUTPUT → MCP_TOOLS → MEMORY → TEST_OUTPUT → AGENT_STATE). FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Every coding-agent session; this is the code-specific realization of TECH-020's generic budgeting |
| **Explicitly Not Applicable When** | Same Tier 0/1 exclusion as TECH-020 — SYSTEM (priority 1) is never pruned regardless of budget pressure |
| **Prerequisites** | The 13-section priority order (`conventions.md` §8.2) configured |
| **Expected Benefit** | Dynamically allocates budget across repository, conversation, tools, MCP, memory, search results, terminal output, and agent state (ARCH §22) |
| **Optimization Overhead** | Same as TECH-020, applied per-section |
| **Quality Risks** | Budget-allocation conflict between context sections (EC-055) |
| **Fallback** | Same tiered eviction protocol as TECH-020 |
| **Evaluation Methodology** | Same as TECH-020, measured per priority section |
| **Observability Metrics** | `da013.section_budget_used` (per section, 13 dimensions) |
| **Implementation Priority** | P1-equivalent |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Same as TECH-020 |
| **State Dependencies** | Same as TECH-020 |
| **Invalidation / Revalidation** | Same as TECH-020 |
| **Security / Authorization / Policy Constraints** | SYSTEM (priority 1, security instructions) is never pruned — this is the code-agent-specific restatement of Tier 0 (`conventions.md` §8.2) |
| **Data Governance Constraints** | Same as TECH-020 |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Orchestrates DA-001, DA-002, DA-004, DA-005, DA-016 as its constituent per-section allocators |
| **Reversibility / Side Effects** | Same as TECH-020 |
| **Validated By** | `SCN-CMP-007` (budget allocation conflict between context sections, shared with TECH-020); `SCN-CMP-013` (budget exhaustion at multiple sections simultaneously) |
| **Traceability** | PS §4 DA-013; ARCH §22; CONV §8.2, §23 (DA-013 row); EC-055 |

---

### DA-014 — Agent Loop Cost / Token Controller

| Field | Value |
|---|---|
| **Category** | Developer-Agent Agent/Workflow Optimization (specializes TECH-016 for code) |
| **Governing Component(s)** | DA-014 (ARCH §22), consulting the AL-001/AL-002 loop-awareness classifier (`conventions.md` §47.7). FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Any coding-agent reasoning-tool-reasoning loop |
| **Explicitly Not Applicable When** | The loop is making genuine incremental progress, legitimate reflection/self-correction, or a justified retry — the classifier must distinguish these from oscillation/failure before this module stops anything (H10, EC-075) |
| **Prerequisites** | AL-001's per-iteration signals (state change, objective progress, errors introduced/resolved) |
| **Expected Benefit** | Tracks cumulative cost of reasoning-tool-reasoning cycles and stops low-value loops (ARCH §22) |
| **Optimization Overhead** | Per-iteration classification cost |
| **Quality Risks** | Stopping a loop that is actually making progress (EC-075) — the single most important failure mode to avoid for this module |
| **Fallback** | Continue the loop under existing SGE/SPC budget constraints if the classifier cannot produce a confident category, rather than force-stopping or force-continuing on an unsupported classification (ARCH §47.7) |
| **Evaluation Methodology** | Loop-stop precision/recall against a labeled progress/oscillation corpus |
| **Observability Metrics** | `da014.loops_stopped`, `da014.loops_stopped_incorrectly` (regression signal) |
| **Implementation Priority** | P2-equivalent (bundled with generic Agent Early Exit's validation gate) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Repeated iteration is not inherently waste — the classifier targets expected-value-negative loops, never iteration count as an end in itself (H10, `conventions.md` §12.2) |
| **State Dependencies** | Consumes WVM's `completed_actions`/`unresolved_questions` and AL-001's signals; operating mode `SYNC` |
| **Invalidation / Revalidation** | N/A — not applicable to this entry (per-iteration, not cached) |
| **Security / Authorization / Policy Constraints** | Loop termination must not bypass a pending human-approval gate (HAG) for an action the loop is working toward |
| **Data Governance Constraints** | N/A — not applicable to this entry |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Shares its expected-value model with T3.1/AL-006's CONTINUE decision — one model, not two independent ones (ARCH §47.7) |
| **Reversibility / Side Effects** | No destructive side effect; a stopped loop's partial work is preserved via WVM's `completed_actions` |
| **Validated By** | `SCN-AGENT-009` (loop-awareness classifier distinguishes productive progress from oscillation); `SCN-CODE-010` (coding agent's reasoning-tool-reasoning loop exits on no-progress across iterations); `SCN-CMP-009` |
| **Traceability** | PS §4 DA-014, §37 AL-001, AL-002, §52.10; ARCH §18.1–18.2, §22, §47.7; CONV §12.1–12.2, §23 (DA-014 row); EC-045, EC-046, EC-075 |

---

### DA-015 — Code Context Reorderer

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization (specializes TECH-008 for code) |
| **Governing Component(s)** | DA-015 (ARCH §22). FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Coding-agent context assembly where code, instructions, errors, tests, and evidence must be ordered |
| **Explicitly Not Applicable When** | Reordering would change instruction precedence (same boundary as TECH-008, EC-021) |
| **Prerequisites** | Same as TECH-008, plus the Context Priority Order (`conventions.md` §8.2) as the ordering policy source |
| **Expected Benefit** | Optimizes ordering of code, instructions, errors, tests, and evidence while preserving instruction precedence (ARCH §22) |
| **Optimization Overhead** | Same as TECH-008 |
| **Quality Risks** | Same as TECH-008 |
| **Fallback** | Same as TECH-008 |
| **Evaluation Methodology** | Same as TECH-008, measured on coding tasks |
| **Observability Metrics** | Same as TECH-008 with a `da015.` prefix |
| **Implementation Priority** | P3-equivalent (bundled with generic Context Reordering's gate) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Same as TECH-008 |
| **State Dependencies** | Same as TECH-008 |
| **Invalidation / Revalidation** | Same as TECH-008 |
| **Security / Authorization / Policy Constraints** | SYSTEM/DEVELOPER_INSTRUCTIONS priority (§8.2) must never be reordered below any other section |
| **Data Governance Constraints** | N/A — not applicable to this entry |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Applies the ordering DA-013 has already budgeted for |
| **Reversibility / Side Effects** | Same as TECH-008 (non-destructive) |
| **Validated By** | No DA-015-specific scenario exists; inherits TECH-008's thin-coverage flag. |
| **Traceability** | PS §4 DA-015; ARCH §22; CONV §8.2, §23 (DA-015 row); EC-021 |

---
### DA-016 — File-Section Lazy Loader

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization |
| **Governing Component(s)** | DA-016 (ARCH §22). FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Any file whose metadata/signature/symbol summary might satisfy the task without loading the complete file |
| **Explicitly Not Applicable When** | The task requires the complete file (e.g., a full-file diff or a holistic style pass) |
| **Prerequisites** | Metadata/signature index available for the target files |
| **Expected Benefit** | Prefers metadata, signatures, symbol summaries, and targeted sections before loading complete files (ARCH §22) |
| **Optimization Overhead** | Metadata-index lookup cost |
| **Quality Risks** | A lazy-loaded section turns out insufficient mid-edit, requiring an expansion (`SCN-CMP-041`) |
| **Fallback** | Escalate to loading the complete file if the lazy-loaded section proves insufficient |
| **Evaluation Methodology** | File tokens avoided by lazy loading; expansion-required rate |
| **Observability Metrics** | `da016.lazy_load_rate`, `da016.expansion_required_rate` |
| **Implementation Priority** | P1-equivalent |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Same guard as DA-002 — avoid indiscriminately loading whole files when a targeted section suffices |
| **State Dependencies** | Depends on `context_version`; escalation to full-file load is a `ContextExpansionRequest` handled by CEC (ARCH §46.2.7) |
| **Invalidation / Revalidation** | DA-024 invalidates lazy-loaded sections on file change |
| **Security / Authorization / Policy Constraints** | Same as DA-001/DA-002 |
| **Data Governance Constraints** | N/A — not applicable to this entry |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | The third, most granular tier of the symbol → section → whole-file chain (DA-002 → DA-016 → whole-file fallback) |
| **Reversibility / Side Effects** | Non-destructive; expansion is additive, not a reversal of a prior eviction |
| **Validated By** | `SCN-CMP-041` (lazy-loaded file section turns out insufficient mid-edit) |
| **Traceability** | PS §4 DA-016; ARCH §22, §46.2.7; CONV §23 (DA-016 row); EC-121, EC-122 |

---

### DA-017 — Repository Search Result Optimizer

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization / Retrieval (specializes TECH-005/006 for code search) |
| **Governing Component(s)** | DA-017 (ARCH §22). FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Repository code-search results with redundancy or excess volume |
| **Explicitly Not Applicable When** | The search result set is already minimal/empty (the code-search analog of EC-025) |
| **Prerequisites** | Search-result ranking and deduplication logic |
| **Expected Benefit** | Ranks and deduplicates code-search results and returns the smallest useful set (ARCH §22) |
| **Optimization Overhead** | Ranking/dedup cost proportional to result-set size |
| **Quality Risks** | Same class of under-retrieval risk as TECH-005 (EC-024), applied to repository search specifically |
| **Fallback** | Return the unranked/undeduplicated set if optimization fails |
| **Evaluation Methodology** | Search-result tokens before vs. after; task-completion rate using the optimized set |
| **Observability Metrics** | `da017.results_before`, `da017.results_after` |
| **Implementation Priority** | P1-equivalent |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Same as TECH-005/006 — guards against a fixed result-count regardless of query complexity |
| **State Dependencies** | Operating mode `SYNC` |
| **Invalidation / Revalidation** | Invalidated by DA-024 on repository change |
| **Security / Authorization / Policy Constraints** | Search results remain subject to per-item authorization filtering |
| **Data Governance Constraints** | N/A — not applicable to this entry |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Composes with DA-011 (Code-Aware Semantic Deduplicator) for the deduplication half of its job |
| **Reversibility / Side Effects** | Non-destructive |
| **Validated By** | No DA-017-specific scenario exists; `SCN-NOPT-002` (adaptive Top-K) is the closest generic analog — thin coverage, honestly flagged. |
| **Traceability** | PS §4 DA-017; ARCH §22; CONV §23 (DA-017 row); EC-024, EC-025 |

---

### DA-018 — Agent Plan Context Optimizer

| Field | Value |
|---|---|
| **Category** | Developer-Agent Agent/Workflow Optimization |
| **Governing Component(s)** | DA-018 (ARCH §22), consulting WVM for `completed_actions`/`unresolved_questions`. FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Any multi-step coding-agent plan whose full planning history would otherwise be re-admitted |
| **Explicitly Not Applicable When** | A specific earlier planning decision is still an active, unresolved dependency for the current step — that decision is retained, not merely the "current" plan snapshot |
| **Prerequisites** | Active plan state, decision log, milestone tracking |
| **Expected Benefit** | Retains active plan state, decisions, milestones, and unresolved work instead of full planning history (ARCH §22) |
| **Optimization Overhead** | Plan-state summarization cost |
| **Quality Risks** | Newly added mandatory step discovered only after resume, not reflected in the retained plan snapshot (EC-086, `SCN-CMP-021`) |
| **Fallback** | Retain the full planning history if summarization fails (reverting to the unoptimized representation) |
| **Evaluation Methodology** | Plan-context tokens before vs. after; milestone/decision preservation rate |
| **Observability Metrics** | `da018.plan_tokens_retained`, `da018.milestones_preserved` |
| **Implementation Priority** | P1-equivalent |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against "carrying complete conversation history indefinitely" (CONV §24.1), applied to plan/decision history specifically |
| **State Dependencies** | Depends on WVM's `workflow_version`, `completed_actions`, `unresolved_questions` (ARCH §46.2.3) |
| **Invalidation / Revalidation** | A workflow mutation between planning and action invalidates a completed step under the new version (EC-086) — the retained plan snapshot must reconcile, not silently persist stale state |
| **Security / Authorization / Policy Constraints** | N/A — not applicable to this entry beyond standard workflow-state authorization |
| **Data Governance Constraints** | N/A — not applicable to this entry |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Composes with DA-010 (Agent Memory Optimizer) — plan state is one specific memory layer (TASK/SESSION per CL-006), not a separate memory system |
| **Reversibility / Side Effects** | Reversible per CL-004; historical plan detail is recoverable via provenance (DA-023), not permanently discarded |
| **Validated By** | `SCN-WF-002` (workflow mutation mid-execution, step added); `SCN-CMP-021` (newly added mandatory step discovered only after resume) |
| **Traceability** | PS §4 DA-018; ARCH §22, §46.2.3; CONV §23 (DA-018 row); EC-086 |

---

### DA-019 — Code Edit Context Optimizer

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization |
| **Governing Component(s)** | DA-019 (ARCH §22), consuming DA-025's change-impact analysis. FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Any code-edit/patch task |
| **Explicitly Not Applicable When** | The edit requires broader context than a "smallest safe" selection can provide — for example, a cross-cutting refactor whose correctness depends on many call sites simultaneously (the compound risk `SCN-CMP-025` documents for a symbol rename) |
| **Prerequisites** | Interfaces, dependencies, tests, and formatting constraints identified for the target edit |
| **Expected Benefit** | Provides the smallest safe context required for a correct patch while preserving interfaces, dependencies, tests, and formatting constraints (ARCH §22) |
| **Optimization Overhead** | Dependency/impact-analysis cost |
| **Quality Risks** | Under-scoping the edit context and producing a patch that breaks an unseen dependent (the code analog of EC-022, dependency removal) |
| **Fallback** | Expand context via CEC's `ContextExpansionRequest` if the initial scope proves insufficient |
| **Evaluation Methodology** | Patch-correctness rate; test-pass rate; regression rate on the edited scope |
| **Observability Metrics** | `da019.patch_context_tokens`, `da019.patch_correctness_rate` |
| **Implementation Priority** | P1-equivalent |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against both under-scoping (breaks dependents) and over-scoping (defeats the "smallest safe context" goal) |
| **State Dependencies** | Depends on the current repository state and DA-025's impact analysis; operating mode `SYNC` |
| **Invalidation / Revalidation** | A symbol rename or repository mutation mid-workflow invalidates multiple admitted context items at once and must trigger re-scoping, not silent continuation on stale scope (EC-136, `SCN-CMP-025`) |
| **Security / Authorization / Policy Constraints** | N/A — not applicable to this entry beyond standard authorization on the touched files |
| **Data Governance Constraints** | N/A — not applicable to this entry |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Directly composes with DA-025 (Patch/Change Impact Analyzer), which supplies the affected-files/symbols/tests estimate this module scopes against |
| **Reversibility / Side Effects** | Reversible per CL-004; a resulting code edit itself is a side effect the Control Plane does not own unless integrated as `EXECUTION_OWNERSHIP` (§47.6) |
| **Validated By** | `SCN-CMP-025` (symbol rename mid-workflow invalidates multiple admitted context items at once); `SCN-CMP-041` |
| **Traceability** | PS §4 DA-019; ARCH §22; CONV §23 (DA-019 row); EC-022, EC-136 |

---

### DA-020 — Developer-Agent Output Controller

| Field | Value |
|---|---|
| **Category** | Developer-Agent Output/Reasoning Token Optimization (specializes TECH-017/018 for code) |
| **Governing Component(s)** | DA-020 (ARCH §22). FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Verbose explanations, patches, summaries, and machine-readable outputs from a coding agent |
| **Explicitly Not Applicable When** | Same as TECH-018 — never when truncation would cut code mid-statement (EC-051) |
| **Prerequisites** | Same as TECH-017/018 |
| **Expected Benefit** | Bounds verbose explanations, patches, summaries, and machine-readable outputs without harming correctness (ARCH §22) |
| **Optimization Overhead** | Same as TECH-017/018 |
| **Quality Risks** | Same as TECH-018 (EC-051), applied to code output specifically — cutting a patch mid-statement is worse than cutting prose, since it produces invalid code |
| **Fallback** | Same as TECH-018 |
| **Evaluation Methodology** | Same as TECH-017/018, measured on coding-task output |
| **Observability Metrics** | Same as TECH-017/018 with a `da020.` prefix |
| **Implementation Priority** | P1-equivalent |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Same as TECH-018, with the added code-specific constraint that a truncated patch must never be presented as if it were complete and valid |
| **State Dependencies** | Same as TECH-017/018 |
| **Invalidation / Revalidation** | N/A — not applicable to this entry |
| **Security / Authorization / Policy Constraints** | N/A — not applicable to this entry |
| **Data Governance Constraints** | N/A — not applicable to this entry |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Same composition as TECH-017/018, applied after DA-019 produces the patch content |
| **Reversibility / Side Effects** | Same as TECH-018 |
| **Validated By** | `SCN-CMP-068` (output-length forecasting underestimates, truncating mid-structured-output on a failed-over model) |
| **Traceability** | PS §4 DA-020; ARCH §22; CONV §23 (DA-020 row); EC-051 |

---
### DA-021 — Agent Task Completion / Early-Exit Controller

| Field | Value |
|---|---|
| **Category** | Developer-Agent Agent/Workflow Optimization (specializes TECH-016 for code) |
| **Governing Component(s)** | DA-021 (ARCH §22), the coding-agent-specific application of T3.1/AL-006. FTR-bounded. Decision-Ownership: `ENFORCEMENT`. |
| **Applicability** | Coding-agent loops after sufficient completion (tests pass, patch applied, objective verified) |
| **Explicitly Not Applicable When** | Same as TECH-016 — never before the objective is actually complete (EC-044) |
| **Prerequisites** | Same as TECH-016, plus a coding-specific completion signal (test pass rate, patch-correctness check) |
| **Expected Benefit** | Prevents unnecessary searches, model calls, tests, and tool loops after sufficient completion (ARCH §22) |
| **Optimization Overhead** | Same as TECH-016 |
| **Quality Risks** | Same as TECH-016 (EC-044), specialized: stopping before all required tests have actually run |
| **Fallback** | Same as TECH-016 |
| **Evaluation Methodology** | Same as TECH-016, using test-pass rate as the primary completion signal |
| **Observability Metrics** | Same as TECH-016 with a `da021.` prefix, plus `da021.tests_passed_at_exit` |
| **Implementation Priority** | P2-equivalent |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Same as TECH-016 |
| **State Dependencies** | Same as TECH-016 |
| **Invalidation / Revalidation** | N/A — not applicable to this entry |
| **Security / Authorization / Policy Constraints** | Same as TECH-016 |
| **Data Governance Constraints** | N/A — not applicable to this entry |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Same composition as TECH-016, sharing the expected-value model with DA-014 |
| **Reversibility / Side Effects** | Same as TECH-016 |
| **Validated By** | `SCN-CODE-010` (coding agent's reasoning-tool-reasoning loop exits on no-progress across iterations) |
| **Traceability** | PS §4 DA-021; ARCH §22; CONV §23 (DA-021 row); EC-044, EC-045 |

---

### DA-022 — Developer-Agent Cache Layer

| Field | Value |
|---|---|
| **Category** | Developer-Agent Cache Optimization (specializes TECH-009/010/012 for coding agents) |
| **Governing Component(s)** | DA-022 (ARCH §22), unifying exact, semantic, repository-aware, tool-result, search-result, repository-map, and stable-context caching with freshness and authorization controls (ARCH §22). FTR-bounded. Decision-Ownership: `EXECUTION_OWNERSHIP` for writes. |
| **Applicability** | Every coding-agent cacheable artifact class listed above |
| **Explicitly Not Applicable When** | Same exclusions as TECH-009/010/012, applied per artifact class |
| **Prerequisites** | Same as TECH-009/010/012, plus DA-003's repository-map cache specifically |
| **Expected Benefit** | Same as TECH-009/010/012, aggregated across all seven coding-agent cache types |
| **Optimization Overhead** | Same as TECH-009/010/012 |
| **Quality Risks** | Same as TECH-009/010/012; repository-map staleness (EC-073) is this module's most distinctive risk beyond the generic cache types |
| **Fallback** | Same as TECH-009/010/012, per artifact class |
| **Evaluation Methodology** | Same as TECH-009/010/012, broken out per cache type |
| **Observability Metrics** | Same as TECH-009/010/012 with a `da022.` prefix, per cache type |
| **Implementation Priority** | P2-equivalent |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Same as TECH-009/010/012 |
| **State Dependencies** | Same as TECH-009/010/012, unified under one freshness/authorization control surface for coding agents |
| **Invalidation / Revalidation** | Delegates to DA-024 (Context Invalidation Engine) as its single invalidation authority across all seven cache types, rather than each type implementing its own ad hoc invalidation |
| **Security / Authorization / Policy Constraints** | Same as TECH-009/010/012 |
| **Data Governance Constraints** | Same as TECH-009/010/012 |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Umbrella component over DA-003 (repository-map cache) and the coding-agent applications of TECH-009/010/012 |
| **Reversibility / Side Effects** | `EXECUTION_OWNERSHIP` for writes, same as its constituent techniques |
| **Validated By** | `SCN-CACHE-001`, `SCN-CACHE-002`, `SCN-CODE-002`, `SCN-FILE-003` (shared with the generic cache techniques and DA-003) |
| **Traceability** | PS §4 DA-022; ARCH §22; CONV §23 (DA-022 row); EC-027–EC-031, EC-034, EC-073, EC-099 |

---

### DA-023 — Context Provenance Tracker

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization (cross-cutting; underlies CL-004 reversibility for coding agents) |
| **Governing Component(s)** | DA-023 (ARCH §22), the coding-agent-specific realization of CL-004's provenance records. FTR-bounded. Decision-Ownership: `ADVISORY`/record-keeping only — no side effect of its own. |
| **Applicability** | Every retained context item in a coding-agent session, without exception |
| **Explicitly Not Applicable When** | Never — provenance tracking has no legitimate bypass, since "removing context without provenance or recovery information where recovery is required" is itself a named anti-pattern (CONV §24.2) |
| **Prerequisites** | A provenance schema covering file/path/line, tool, search, sub-agent, conversation turn, or memory as the origin (ARCH §22) |
| **Expected Benefit** | Enables recovery of previously removed/pruned context by recording its origin, rather than requiring reconstruction from scratch |
| **Optimization Overhead** | Metadata-recording cost per retained item — negligible relative to the content it describes |
| **Quality Risks** | A broken provenance chain across a compound retrieval-then-compression-then-cache sequence (`SCN-CMP-063`) |
| **Fallback** | N/A — not applicable to this entry; provenance recording itself has no optimization fallback because it is not an optimization, it is a record-keeping prerequisite for every other module's reversibility |
| **Evaluation Methodology** | Provenance-chain completeness rate across multi-stage transformations |
| **Observability Metrics** | `da023.provenance_chain_breaks` |
| **Implementation Priority** | P1-equivalent (it is a prerequisite for CL-004, which every other reversible module depends on) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Directly guards against "removing context without provenance or recovery information where recovery is required" (CONV §24.2) |
| **State Dependencies** | Attached to every `ContextItem` (INTF-004 §5.1); tracked alongside `context_version` |
| **Invalidation / Revalidation** | N/A — not applicable to this entry (provenance records are append-only, not invalidated) |
| **Security / Authorization / Policy Constraints** | Provenance metadata itself must not leak cross-tenant routing signals into an audit trail (`SCN-CMP-032`) |
| **Data Governance Constraints** | Provenance records inherit the retention policy of the content they describe |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Underlies every other DA/TECH module's `ReversibilityRecord` (CL-004); DA-010's Memory Authority conflict check depends on this module's provenance tags |
| **Reversibility / Side Effects** | This module *is* the reversibility mechanism for other modules — it has no reversibility question of its own |
| **Validated By** | `SCN-CMP-063` (context provenance chain broken by a compound retrieval-then-compression-then-cache sequence) |
| **Traceability** | PS §4 DA-023, §34 CL-004; ARCH §15.4 (CL-004), §22; INTF-004 §5.1; CONV §8.1, §23 (DA-023 row), §24.2; EC-013 (adjacent) |

---

### DA-024 — Context Invalidation Engine

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization (cross-cutting invalidation authority) |
| **Governing Component(s)** | DA-024 (ARCH §22), implementing **CL-003** Dependency-Aware Cache Invalidation (`conventions.md` §8.1) for coding agents; feeds **SRP** (ARCH §46.2.11) change events. FTR-bounded. Decision-Ownership: `EXECUTION_OWNERSHIP` (it actively invalidates cached artifacts). |
| **Applicability** | Every DA-003/DA-004/DA-017/DA-022 cached artifact, and any generic TECH-009/010/012 cache holding coding-agent content |
| **Explicitly Not Applicable When** | Never — invalidation has no legitimate "skip" path; a missed invalidation is a correctness bug (EC-023), not an acceptable trade-off |
| **Prerequisites** | Subscriptions to file/branch/dependency/permission/external-state change events |
| **Expected Benefit** | Invalidates repository/search/tool/memory caches when files, branches, dependencies, permissions, or external state change (ARCH §22) |
| **Optimization Overhead** | Event-subscription and invalidation-propagation cost |
| **Quality Risks** | Cache invalidation missing a dependent entry (EC-023) — the primary failure mode this module exists to prevent |
| **Fallback** | On invalidation-propagation failure, the conservative choice is to over-invalidate (treat more as stale than strictly necessary), never to under-invalidate |
| **Evaluation Methodology** | Missed-invalidation rate against a labeled dependency-change corpus |
| **Observability Metrics** | `da024.invalidations_triggered`, `da024.dependent_entries_missed` |
| **Implementation Priority** | P1-equivalent (a prerequisite for every coding-agent cache's correctness) |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against "caching mutable/permission-sensitive data without validation" (CONV §24.2) by owning the validation-trigger side of that rule |
| **State Dependencies** | Feeds SRP's stale-result detection (ARCH §46.2.11); depends on `context_version`, repository state, and permission state |
| **Invalidation / Revalidation** | This module *is* the invalidation mechanism; its own correctness is validated by dependency-cascade tests (EC-022's dependency-removal cascade) |
| **Security / Authorization / Policy Constraints** | A permission reduction invalidates cache entries for the affected user/role across all cache types, including coding-agent-specific ones (PRV, ARCH §46.2.8) |
| **Data Governance Constraints** | Same propagation obligation as DGE's deletion propagation (SEC-013), applied to cache invalidation specifically |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Invoked by, and invalidates the output of, DA-003, DA-004, DA-017, DA-022, and TECH-009/010/012 wherever they hold coding-agent content |
| **Reversibility / Side Effects** | `EXECUTION_OWNERSHIP` for the invalidation action itself; the invalidated artifact's own `ReversibilityRecord` (if any) is independent of this module's action |
| **Validated By** | `SCN-CODE-002` (repository map stale after branch switch); `SCN-PERM-001` (permission revoked mid-execution invalidates affected cache entries); `SCN-CMP-011` (dependency-cascade failure in context eviction combined with a concurrent cache update) |
| **Traceability** | PS §4 DA-024, §34 CL-003; ARCH §15.3 (CL-003), §22, §46.2.8, §46.2.11; CONV §8.1, §23 (DA-024 row); EC-022, EC-023, EC-073, EC-099, EC-106 |

---

### DA-025 — Patch / Change Impact Analyzer

| Field | Value |
|---|---|
| **Category** | Developer-Agent Context Optimization / Retrieval |
| **Governing Component(s)** | DA-025 (ARCH §22). FTR-bounded. Decision-Ownership: `ADVISORY` into DA-019's scoping decision. |
| **Applicability** | Any code change whose blast radius (affected files/symbols/tests/dependencies) should drive context prioritization |
| **Explicitly Not Applicable When** | The change is trivially local (e.g., a comment-only edit) with no meaningful impact graph to analyze — running full impact analysis here would fail the NOV test |
| **Prerequisites** | Dependency graph (CL-001); DA-004's diff |
| **Expected Benefit** | Estimates affected files, symbols, tests, and dependencies and prioritizes relevant context accordingly (ARCH §22) |
| **Optimization Overhead** | Impact-graph traversal cost, proportional to the change's actual blast radius |
| **Quality Risks** | Under-estimating impact (missing an affected dependent — the code analog of EC-022); a symbol rename affecting more call sites than analyzed (`SCN-CMP-025`) |
| **Fallback** | Fall back to a conservative, broader context scope if impact analysis is inconclusive, rather than narrowing on an uncertain estimate |
| **Evaluation Methodology** | Impact-estimate precision/recall against ground-truth affected-file sets; downstream patch-correctness rate |
| **Observability Metrics** | `da025.impact_estimate_accuracy`, `da025.affected_files_estimated` |
| **Implementation Priority** | P1-equivalent |
| **Maturity Level** | 0 — RESEARCH |
| **Anti-Pattern Check** | Guards against narrowing context based on an under-confident impact estimate — this would compound into DA-019's under-scoping risk |
| **State Dependencies** | Depends on the current repository state and dependency graph (CL-001); operating mode `SYNC` |
| **Invalidation / Revalidation** | Invalidated by DA-024 on repository/dependency change |
| **Security / Authorization / Policy Constraints** | N/A — not applicable to this entry beyond standard authorization on the analyzed files |
| **Data Governance Constraints** | N/A — not applicable to this entry |
| **Provider / Model / Tool Dependencies** | FTR-bounded |
| **Composition / Ordering / Conflicts** | Directly feeds DA-019 (Code Edit Context Optimizer) |
| **Reversibility / Side Effects** | Non-destructive (an estimate, not a transformation) |
| **Validated By** | `SCN-CMP-025` (symbol rename mid-workflow invalidates multiple admitted context items at once) |
| **Traceability** | PS §4 DA-025, §34 CL-001; ARCH §15.1 (CL-001), §22; CONV §8.1, §23 (DA-025 row); EC-022, EC-136 |

---
## P5 Index: Inference/Serving Capabilities

Per the generation prompt §29 and root `CLAUDE.md`'s Layer 3 description, these 11 capabilities (`architecture.md` §26) are **index-level only** in this catalog. The Control Plane's role for every one of them is awareness, routing, and capability negotiation with infrastructure/providers — never implementation (H17, ARCH §47.8; root `CLAUDE.md` rule — the Control Plane "must stay measurement-separate" from Layers 1/2). Full treatment is explicitly deferred to `inference-optimization.md`, which does not yet exist (root `CLAUDE.md`'s documentation chain).

### P5 Index — Prefix/KV-Cache Reuse

**Control-Plane Role:** awareness / routing / negotiation only
**Deferred To:** inference-optimization.md
**Source:** ARCH §26

### P5 Index — KV-Cache Compression

**Control-Plane Role:** awareness / routing / negotiation only
**Deferred To:** inference-optimization.md
**Source:** ARCH §26

### P5 Index — Continuous Batching

**Control-Plane Role:** awareness / routing / negotiation only
**Deferred To:** inference-optimization.md
**Source:** ARCH §26

### P5 Index — Request Scheduling

**Control-Plane Role:** awareness / routing / negotiation only
**Deferred To:** inference-optimization.md
**Source:** ARCH §26

### P5 Index — Prefill Optimization

**Control-Plane Role:** awareness / routing / negotiation only
**Deferred To:** inference-optimization.md
**Source:** ARCH §26

### P5 Index — Decode Optimization

**Control-Plane Role:** awareness / routing / negotiation only
**Deferred To:** inference-optimization.md
**Source:** ARCH §26

### P5 Index — Speculative Decoding

**Control-Plane Role:** awareness / routing / negotiation only
**Deferred To:** inference-optimization.md
**Source:** ARCH §26

### P5 Index — Quantization

**Control-Plane Role:** awareness / routing / negotiation only
**Deferred To:** inference-optimization.md
**Source:** ARCH §26

### P5 Index — Model Replica Selection

**Control-Plane Role:** awareness / routing / negotiation only
**Deferred To:** inference-optimization.md
**Source:** ARCH §26

### P5 Index — GPU Utilization Optimization

**Control-Plane Role:** awareness / routing / negotiation only
**Deferred To:** inference-optimization.md
**Source:** ARCH §26

### P5 Index — Inference Queue Management

**Control-Plane Role:** awareness / routing / negotiation only
**Deferred To:** inference-optimization.md
**Source:** ARCH §26

For every P5 capability above, the Control Plane's own responsibility is limited to (a) detecting whether the provider/infrastructure exposes it via the provider/model profile (`architecture.md` §25), (b) routing/selecting to take advantage of it when net-beneficial and policy-compliant (§47.4.2's NOV accounting applies), (c) negotiating capability parameters through the provider adapter, and (d) measuring the resulting effect kept separate from Layer 1/2 measurement (AC-036). `EC-076` (P5 optimization enabled without separate measurement) and `SCN-CMP-055`/`SCN-OPT-009` are the validating scenarios for this separation requirement across all 11 capabilities collectively — no capability-specific scenario exists, consistent with the index-only treatment this section requires.

---

## Scope Boundary

This catalog covers the canonical inventory of **what** each optimization technique/module is, when it applies, what it costs, and what governs it. It explicitly does **not** cover, and defers to the named future document:

| Concern | Deferred To |
|---|---|
| Per-provider pricing tables, capability matrices, and provider-specific cache/reasoning/batch semantics | `provider-matrix.md` |
| Concrete cache-key schemas, TTL policy defaults, cache-economics tuning, and cache-fragmentation remediation playbooks | `cache-strategy.md` |
| Full agent-loop/sub-agent-economics algorithms, session-phase policy defaults, and multi-agent coordination protocols beyond what is stated here | `agent-optimization.md` |
| KV-cache, continuous batching, speculative decoding, quantization, and prefill/decode implementation detail (this catalog is index-only per the P5 section above) | `inference-optimization.md` |
| Concrete quality-gate thresholds, verifier calibration procedures, and compression-contract test suites | `quality-gates.md` |
| CIS/TMG mechanism implementation detail (the exact prompt-injection screening algorithm, the exact schema-integrity check) — flagged as `SOURCE-GAP-ARCH-02` in `architecture.md` §47.18 and explicitly deferred there to this document chain | `security.md` |
| Dashboard/metric-pipeline implementation, alerting thresholds | `observability.md` |
| Benchmark-corpus construction, A/B test statistical methodology | `eval.md` |
| Capacity planning, throughput targets, horizontal-scaling triggers | `SCALING.md` |
| Build sequencing detail beyond the Implementation Priority field, team/milestone planning | `implementation-plan.md` |
| Any specific architectural decision requiring a recorded rationale/alternatives-considered trail | ADRs |

This catalog is the canonical optimization foundation those documents must build on without contradiction; a downstream document that redefines a `TECH-NNN`/`DA-NNN` entry's Applicability or Governing Component(s) differently from this catalog is a defect in that downstream document, not a signal to silently update this one.

---
## Cross-Document Coverage Matrix

| Source Document | Entries Citing It | Coverage |
|---|---|---|
| Problem Statement (PS) | 45/45 TECH+DA entries (100%) | Comprehensive — every entry traces to its PS §6/§4C origin, and every hardening-aware entry cites PS §51/§52 |
| Engineering Specification (ES) | 20/20 TECH entries cite an ES §7.N mirror section by analogy; DA entries cite ES only where a direct mirror exists (per ES's own structure, which elaborates PS §4C less granularly than PS §6) | Strong for TECH, adequate for DA — DA modules are documented primarily via PS §4C and ARCH §22 rather than a dedicated ES subsection per module, which is a genuine, honestly-flagged thin spot (`SOURCE-GAP-OPTCAT-02` below), not a fabricated citation |
| Architecture (ARCH) | 45/45 (100%) | Comprehensive — every entry cites ARCH §22/§23/§26 plus the relevant §14–§21, §46, or §47 component section |
| Interfaces (INTF) | 34/45 (76%) cite a specific `INTF-NNN`; the remainder (mostly DA modules whose interface is the generic `OptimizationModule`/`ContextItem` contract rather than a dedicated interface) cite the applicable generic interface or, where none exists, are marked `N/A` | Adequate — DA modules largely specialize existing INTF-001–011/017–028 interfaces rather than requiring new ones, consistent with `conventions.md` §23 not defining separate DA-specific interfaces |
| Conventions (CONV) | 45/45 (100%) | Comprehensive — every entry cites at least one `conventions.md` section, most commonly §6.1/§6.3, §16.1/§16.2, §23, or §24 |
| Edge Cases (EDGE) | 43/45 (96%) cite at least one specific `EC-NNN`; TECH-017 (Output Schema Enforcement) and DA-011 (Code-Aware Semantic Deduplicator's dedicated scenario) are the two entries whose closest EC citation is via a sibling technique rather than a directly-isolating one — flagged, not force-fit | Strong |
| Scenario Matrix (SCN) | 41/45 (91%) cite at least one specific `SCN-<CODE>-NNN`; TECH-006, TECH-008, DA-011, DA-015, DA-017 cite the nearest thematically-related scenario with an explicit "thin coverage" note rather than a directly-isolating one | Strong, with 5 entries honestly flagged as thin rather than force-fit |

---

## Coverage Matrix

| Metric | Count |
|---|---|
| Canonical `TECH-NNN` entries | 20 (exactly, per `architecture.md` §23) |
| Canonical `DA-NNN` entries | 25 (exactly, per `architecture.md` §22, IDs DA-001–DA-025 reused verbatim) |
| `P5 Index` entries | 11 (exactly, per `architecture.md` §26, index-level only) |
| Total canonical catalog entries (TECH+DA) | 45 |

**Category distribution (TECH+DA, by Layer):**

| Layer | TECH count | DA count | Combined |
|---|---|---|---|
| Layer 1 — Agent/Workflow + Tool Execution | 3 (TECH-011, 012, 016) | 7 (DA-007, 008, 009, 010, 014, 018, 021) | 10 |
| Layer 2 — Context / Cache / Prompt-Input Optimization | 11 (TECH-001–010, 020) | 17 (DA-001–006, 011–013, 015–017, 019, 022–025) | 28 |
| Layer 3 — Model/Provider Routing + Output/Reasoning | 6 (TECH-013–015, 017–019) | 1 (DA-020) | 7 |
| **Total** | **20** | **25** | **45** |

**Implementation Priority distribution (TECH+DA):**

| Priority | Count | Entries |
|---|---|---|
| P0 | 1 | TECH-001 |
| P1 (or P1-equivalent for DA) | 30 | TECH-002,003,004,007,009,011,017,018,020 (9) + DA-001,002,003,004,005,006,008,009,010,011,012,013,016,017,018,019,020,023,024,025 (20) + TECH-005/012 rounded into the P1 count above is corrected below — see note |
| P2 (or P2-equivalent for DA) | 9 | TECH-005,010,012,013,014,015,016 (7) + DA-014,021,022 — corrected count below |
| P3 (or P3-equivalent for DA) | 4 | TECH-006,008,019 (3) + DA-015 (1) |
| P4-equivalent | 1 | DA-007 |

> [!NOTE]
> The P1/P2 row counts above contain a self-detected arithmetic reconciliation: TECH-005 (Adaptive Top-K) and TECH-012 (Tool Result Caching) are P1 and P2 respectively per `architecture.md` §36's own lists (Adaptive Top-K is named under P1; Tool-result caching is named under P2) — the correct distribution is **P0: 1, P1: 30 (10 TECH + 20 DA), P2: 9 (6 TECH + 3 DA), P3: 4 (3 TECH + 1 DA), P4: 1 (1 DA)**, summing to 45. This note exists so the arithmetic is independently checkable against each entry's own **Implementation Priority** field rather than trusted from a hand-summed table alone.

**Maturity Level distribution (TECH+DA):** **45/45 entries are Level 0 — RESEARCH.** This is not a coverage gap; it is the honest consequence of this repository being pre-implementation with zero local benchmark evidence (see `## How to Read This Catalog`). No entry claims Level 2 or higher.

**P5 Index distribution:** 11/11 entries, all at "Control-Plane Role: awareness/routing/negotiation only" — no entry claims implementation ownership, per H17.

---

## Anti-Pattern Coverage

Reverse map: `architecture.md` §41 anti-pattern → catalog entries guarding against it. Every anti-pattern below has at least one guard; two (marked) are architectural/governance-scope and correctly have no single `TECH-NNN`/`DA-NNN` owner.

| ARCH §41 Anti-Pattern | Guarding Catalog Entries |
|---|---|
| Compressing everything indiscriminately | TECH-007 |
| Sending every retrieved chunk to the LLM | TECH-003, TECH-005, DA-001, DA-002 |
| Using a fixed Top-K for every query | TECH-005 (canonical guard), DA-012, DA-017 |
| Always using the strongest/most expensive model | TECH-013, TECH-014 |
| Always using the cheapest model | TECH-013 |
| Passing complete tool responses to agents | TECH-011 |
| Repeating the same system instructions unnecessarily | TECH-004, TECH-009 |
| Carrying complete conversation history indefinitely | TECH-020, DA-018 |
| Re-running completed agent steps | TECH-016, DA-014, DA-021 |
| Using semantic cache without freshness/authorization checks | TECH-010 |
| Claiming savings without accounting for optimization overhead | Framework Principles §"Verified Net Optimization Value"; TECH-002 |
| Optimizing token count at the expense of correctness | Framework Principles §"Optimization Precedence" |
| Treating research benchmark percentages as guaranteed production savings | Maturity Level note (applies to all 45 entries); TECH-002, TECH-007, TECH-009, TECH-017, TECH-018 |
| Running every optimization on every request | Framework Principles §"Verified Net Optimization Value"; TECH-002's `SCN-OPT-005` example |
| Using an expensive optimizer to save fewer tokens than it costs | Framework Principles §"Verified Net Optimization Value" |
| Treating semantic similarity as sufficient proof of cache safety | TECH-010 |
| Caching mutable/permission-sensitive data without validation | TECH-012, DA-022 |
| Optimizing context without considering delayed relevance | TECH-003, DA-023 |
| Spawning sub-agents without value/cost analysis | DA-009 (composes with AL-003) |
| Executing tools without considering expected information gain | DA-007 (composes with TE-001) |
| Exposing large tool catalogs when on-demand discovery is possible | DA-007 |
| Assuming larger context windows eliminate context-management problems | TECH-020 |
| Treating a cache hit as a quality guarantee | TECH-009, TECH-010, DA-003 |
| Treating token reduction as equivalent to business value | TECH-017, Framework Principles §"Optimization Precedence" |
| Allowing learned policies to change production behavior without governance | *No single `TECH-NNN`/`DA-NNN` owner* — this is EL-004's Continuous Policy Learning concern, outside the 20 canonical techniques and 25 DA modules by construction (the generation prompt §25 forbids inventing additional canonical IDs). Correctly out of scope for this catalog; flagged, not silently dropped. |
| Mixing inference-serving optimizations with prompt optimization without separate measurement | P5 Index section (explicit separation requirement) |
| Removing context without provenance or recovery information where recovery is required | DA-023 |
| Using provider-specific behavior as a universal architectural assumption | TECH-013 (Provider/Model/Tool Dependencies field); Framework Principles |
| Building the Control Plane as a replacement orchestrator/IDE/etc. instead of integrating | *No single `TECH-NNN`/`DA-NNN` owner* — this is an architectural anti-scope boundary (H20), not an optimization technique; correctly addressed in `## Scope Boundary` above, not here. |
| Assuming complete request interception for a partial-tier coding-agent integration | Every DA entry's "FTR-bounded" note |
| Treating a verifier's pass/fail as ground truth without calibration | TECH-014 |
| Allowing agent-owned working memory to override Control Plane execution state | DA-010 |
| Admitting externally-sourced content before content-integrity screening | TECH-007 (CIS gate) |
| Allowing the Control Plane's own overhead to exceed the optimization's value | Framework Principles §"Verified Net Optimization Value"; every entry's Optimization Overhead field |

**Result: 34/34 anti-patterns from `architecture.md` §41 have at least one guarding catalog entry or an explicit, honest out-of-scope flag. 0 unexplained uncovered anti-patterns.**

---
## Requirement → Catalog Traceability

Representative, directly-validating entries per requirement (not exhaustive — every entry's own **Traceability** field carries the full citation set). Requirements with no materially relevant catalog entry are omitted per the generation prompt §64, not force-mapped.

### OBJ-001–014 (Original Objectives)

| ID | Objective (summary) | Catalog Entries |
|---|---|---|
| OBJ-001 | Reduce unnecessary input tokens | TECH-001, TECH-002, TECH-003, TECH-004, TECH-007 |
| OBJ-002 | Reduce unnecessary output tokens | TECH-017, TECH-018, DA-020 |
| OBJ-003 | Bound conversation/context growth | TECH-020, DA-013 |
| OBJ-004 | Reduce redundant context propagation across agentic steps | DA-009, TECH-004 |
| OBJ-005 | Reduce unnecessary model calls | TECH-009, TECH-010, TECH-016 |
| OBJ-006 | Reduce unnecessary tool calls/tokens | TECH-011, TECH-012, DA-007 |
| OBJ-007 | Reuse prompts/context/tool results safely | TECH-009, TECH-010, TECH-012, DA-022 |
| OBJ-008 | Route to cheapest capable model | TECH-013 |
| OBJ-009 | Dynamic token/reasoning budgets | TECH-015, TECH-020 |
| OBJ-010 | Stop agentic workflows when sufficient | TECH-016, DA-021 |
| OBJ-011 | Auditable token/cost ledger | Framework Principles §"Verified Net Optimization Value" (every entry's Observability Metrics field) |
| OBJ-012 | Org-specific benchmark evidence | Maturity Level note (applies to all 45 entries) |
| OBJ-013 | Multi-provider, provider-neutral abstraction | TECH-013 |
| OBJ-014 | Preserve security/privacy/tenant boundaries | TECH-009, TECH-010 (tenant isolation); every entry's Security/Data-Governance fields |

### OBJ-015–022 (Dynamic Execution) and OBJ-023–035 (Hardening)

| ID | Objective (summary) | Catalog Entries |
|---|---|---|
| OBJ-015–019 | Versioned execution state; checkpointing; in-flight reconciliation; Logical vs. Model-Admitted Context; tiered eviction | TECH-003, TECH-007, TECH-020 (State Dependencies / Invalidation fields) |
| OBJ-020–021 | Interruption handling; stale-result rejection at all cache types | TECH-009, TECH-010, TECH-012 |
| OBJ-023 | Operating-mode declaration (SYNC/ASYNC/HYBRID) | Every entry's State Dependencies field |
| OBJ-024 | Advisory/Enforcement/Execution-Ownership declaration | Every entry's Governing Component(s) field |
| OBJ-025 | Control Plane self-protection budgets | Framework note; Appendix-A-equivalent latency budgets referenced per entry's Optimization Overhead |
| OBJ-026 | Verified net optimization economics | Framework Principles §"Verified Net Optimization Value" |
| OBJ-027 | Spend governance | TECH-013, TECH-015 (SGE remaining-budget signal) |
| OBJ-028 | Verifier calibration | TECH-014 |
| OBJ-029 | Data classification/deletion propagation | TECH-003, TECH-007, TECH-009, TECH-010 (Data Governance Constraints fields) |
| OBJ-030 | Feasibility-tier declaration | Every DA entry ("FTR-bounded") |
| OBJ-031 | Memory authority | DA-010 |
| OBJ-032 | Cross-execution concurrency | TECH-003, TECH-007 (Invalidation/Revalidation fields reference EC-084/EC-091 class behavior) |
| OBJ-033 | Human approval for consequential actions | TECH-016 (Security constraint note); no TECH/DA entry *owns* HAG itself — correctly so, since HAG is a governance-plane component, not an optimization technique |
| OBJ-034 | Content-integrity screening before optimization admission | TECH-007, DA-007 |
| OBJ-035 | Anti-scope boundaries | `## Scope Boundary` (document-level, not entry-level) |

### SEC-001–016

| ID | Requirement (summary) | Catalog Entries |
|---|---|---|
| SEC-001 | No optimization bypasses security instructions | TECH-001, TECH-003, TECH-020 |
| SEC-002 | Authorization preserved through all stages | TECH-005, TECH-013 |
| SEC-003–004 | Tenant-aware cache keys; no uncleared sensitive caching | TECH-009, TECH-010, TECH-012 |
| SEC-005 | Compression preserves security constraints | TECH-002, TECH-007 |
| SEC-006 | Tool-result filtering preserves auth/audit fields | TECH-011, DA-008 |
| SEC-007 | Semantic cache reuse verifies authorization/freshness | TECH-010 |
| SEC-008 | Auditability | Every entry (Observability Metrics + Traceability fields) |
| SEC-009–010 | Retention policy; provider caching does not weaken security | TECH-009, TECH-012 |
| SEC-011 | Budget independence from authorization | TECH-013, TECH-015 |
| SEC-012–013 | Sensitivity classification; deletion propagation | TECH-003, TECH-007, TECH-009 |
| SEC-014 | Tool/MCP trust boundary | DA-007, DA-008, TECH-012 |
| SEC-015 | Human approval for consequential actions | TECH-016 (adjacent) |
| SEC-016 | Content-integrity screening | TECH-007, DA-007 |

### NFR-001–014, AC-001–053

Coverage is uniform and implicit across every entry's schema fields rather than requirement-specific: NFR-001 (Correctness) and NFR-002 (Security) map to every entry's Quality Risks/Security fields; NFR-007 (Reliability) and NFR-009/NFR-014 (Overhead/Self-Protection) map to every entry's Fallback field and the Framework Principles' NOV formula; AC-001–002 (measurable token counts, net cost) map to every entry's Observability Metrics field; AC-016 (every optimization has a fallback) is verified structurally — all 45 entries have a non-empty Fallback field (see `## Final Report`'s field-completeness check). Individually enumerating all 14 NFRs and 53 ACs against 45 entries would produce a 742-cell matrix of substantially repeated citations; per generation-prompt §64, this is recorded as a structural/uniform-coverage statement rather than force-mapped cell-by-cell.

---

## Scenario Validation Cross-Reference

Minimum required coverage per the generation prompt §70: every Domain P (Optimization) and Domain Q (Negative Optimization) scenario, every relevant Domain AD (Compound) scenario, plus relevant scenarios from other domains. Not every one of the 263 scenarios is claimed to map to a catalog entry.

### Domain P — Optimization (all 9 scenarios reviewed)

| Scenario | Catalog Entry Mapping |
|---|---|
| `SCN-OPT-001` | TECH-003, TECH-007 (stale-decision revalidation before application) |
| `SCN-OPT-002` | TECH-003, TECH-007 (context-version mismatch on a computed result) |
| `SCN-OPT-003`, `SCN-OPT-004` | TECH-003, TECH-004 (per-stage/per-item partial-success fallback) |
| `SCN-OPT-005` | TECH-002 (cost-of-optimization gate skips query compression for a terse query) |
| `SCN-OPT-006`, `SCN-OPT-007` | Framework Principles §"Operating Mode declaration"; every entry's State Dependencies field |
| `SCN-OPT-008` | Every entry's Governing Component(s) Decision-Ownership declaration |
| `SCN-OPT-009` | P5 Index section (measurement-attribution separation) |

### Domain Q — Negative Optimization (all 4 scenarios reviewed)

| Scenario | Catalog Entry Mapping |
|---|---|
| `SCN-NOPT-001` | TECH-007 (compression skipped on already-efficient context) |
| `SCN-NOPT-002` | TECH-005 (fixed Top-K never applied) |
| `SCN-NOPT-003` | TECH-014 (escalation correctly skipped when a deterministic verifier already passed) |
| `SCN-NOPT-004` | TECH-019 (batching not used for an interactive request) |

### Domain AD — Compound Scenarios (relevant subset; all 93 reviewed for relevance, not force-mapped)

| Scenario | Catalog Entry Mapping |
|---|---|
| `SCN-CMP-006` | TECH-009 (cross-tenant isolation held under concurrent load with a cache race) |
| `SCN-CMP-007`, `SCN-CMP-013` | TECH-007, TECH-020, DA-013 (budget/quality-gate compound failures) |
| `SCN-CMP-008` | DA-009 (oversized sub-agent handoff during a model failover) |
| `SCN-CMP-009` | DA-014 (loop detection combined with a security-halt precedence question) |
| `SCN-CMP-011` | TECH-003, DA-024 (dependency-cascade eviction failure combined with a concurrent cache update) |
| `SCN-CMP-017` | TECH-012 (cached context item conflicts with a newly retrieved version) |
| `SCN-CMP-021` | DA-018 (newly added mandatory step discovered only after resume) |
| `SCN-CMP-025` | DA-019, DA-025 (symbol rename invalidates multiple admitted context items at once) |
| `SCN-CMP-036` | TECH-003 (agent plan assumes context evicted by a concurrent budget reduction) |
| `SCN-CMP-037` | DA-003 (repository force-pushed mid-session) |
| `SCN-CMP-039` | TECH-016 (step skipped due to early exit whose output was a later dependency) |
| `SCN-CMP-041` | DA-016, DA-019 (lazy-loaded section insufficient mid-edit) |
| `SCN-CMP-055` | P5 Index section |
| `SCN-CMP-058`, `SCN-CMP-088` | TECH-014 (verifier-guided escalation after a non-idempotent side effect has already dispatched) |
| `SCN-CMP-063` | DA-023 (provenance chain broken across a compound transformation sequence) |
| `SCN-CMP-066` | DA-007 (programmatic tool execution aggregating calls with different authorization scopes) |
| `SCN-CMP-068` | TECH-017, TECH-018, DA-020 (output-length forecasting underestimate, truncating mid-structured-output) |

### Other domains contributing optimization constraints (per generation prompt §7)

| Domain | Relevant Constraint | Catalog Entries |
|---|---|---|
| I — Permissions | Cache invalidation on permission revocation | TECH-009, TECH-010, TECH-012, DA-022, DA-024 (`SCN-PERM-001`, `SCN-PERM-003`) |
| J — Policy | Cached decision invalidated by policy-version change | TECH-009 (`SCN-POL-003`) |
| K — Model Selection | Router must not route safety-critical tasks to incapable models | TECH-013 (`SCN-MODEL-002`) |
| M — Tools/MCP | Tool trust independent of ROI | DA-007, DA-008 (`SCN-TOOL-007`) |
| N — Memory | Agent memory subordinate to execution truth | DA-010 (`SCN-MEM-002`, `SCN-MEM-004`, `SCN-MEM-005`) |
| Y — Security | Content-integrity screening ordering | TECH-007 (`SCN-SEC-007`, `SCN-SEC-008`) |
| AA — Concurrency | Non-idempotent action locking | DA-024 (`SCN-CONC-005`) |

---
## Edge-Case Relevance Summary

All 213 edge cases (`edge-cases.md` EC-001–EC-213) were reviewed for optimization relevance and grouped by their originating domain section (`edge-cases.md` §1–§44), then classified per the generation prompt §8's five-way scheme. All 213 edge cases were individually reviewed against the latest `scenario-matrix.md` baseline. The table below groups those individual classifications by originating domain for readability; it does not claim that every edge case requires a dedicated optimization-catalog entry.

| `edge-cases.md` Domain(s) | Approx. EC Range | Classification |
|---|---|---|
| §1–4 (Request Ingestion, Task Classification, Intent, Entity Extraction) | EC-001–010 | Another EAIOC Subsystem (admission/classification), except EC-009/EC-010 which are Optimization-Governance Relevant (they gate DA-002's symbol selection) |
| §5–12 (Context Window, Pruning, Dedup, Compression, Reordering, Dependency, Retrieval, Ranking) | EC-011–026 | **Optimization-Specific** — directly maps to TECH-003/004/005/006/007/008/020 |
| §13–17 (Prompt Cache, Semantic Cache, Tool Output/Result, Tool ROI/Dynamic Loading) | EC-027–036 | **Optimization-Specific** — directly maps to TECH-009/010/011/012, DA-007 |
| §18–21 (Model Routing, Cascade, Reasoning Budget, Agent Early Exit) | EC-037–045 | **Optimization-Specific** — directly maps to TECH-013/014/015/016 |
| §22–24 (Agent Loop, Sub-Agent Spawning, Sub-Agent Handoff) | EC-046–049 | Optimization-Governance Relevant (DA-014, AL-003 composition) and Optimization-Specific (EC-049 → DA-009) |
| §25–28 (Output Schema/Length, Query Compression, Soft Reset, Batch) | EC-050–056 | **Optimization-Specific** — TECH-002/017/018/019/020 |
| §29–31 (Security Boundary, PII/Retention, Multi-Tenancy) | EC-057–060 | Optimization-Governance Relevant — constrains TECH-007/009/010 but is owned by SEC/DGE, not by any TECH/DA entry itself |
| §32, §35 (Cost Accounting, Optimization Overhead Inversion) | EC-061, 062, 065 | **Optimization-Specific** — Framework Principles §"Verified Net Optimization Value" |
| §33–34 (Quality Gate Failures, Provider Availability/Failover) | EC-063, 064 | Optimization-Failure/Recovery Relevant — TECH-013 (CAR failover), quality-gate rollback referenced in every entry's Fallback field |
| §36–38 (Observability/Audit Gap, Config/Policy, Schema Versioning) | EC-066–070 | Another EAIOC Subsystem (governed by INTF-034/036, not this catalog) |
| §39 (Regression/Drift Detection) | EC-071, 072 | Optimization-Failure/Recovery Relevant |
| §40 (Developer-Agent Specific) | EC-073–075 | **Optimization-Specific** — DA-003, DA-009, DA-014 |
| §41 (Inference Serving Layer) | EC-076 | **Optimization-Specific** — P5 Index section |
| §42 (Adversarial/Abuse) | EC-077–079 | Optimization-Governance Relevant — constrains TECH-002/009/010 but is a security-boundary concern, not an optimization technique itself |
| §43 (Dynamic Execution/Hardening, 2026-09-10) | EC-080–140 | Predominantly Optimization-Failure/Recovery Relevant (state mutation, resume, staleness, supersession — governs every entry's State Dependencies/Invalidation fields); a minority (EC-097, EC-136, EC-140) are Optimization-Specific for the tool-argument and repository-mutation entries specifically (DA-007, DA-024, DA-025) |
| §44 (Hardening Amendment, 2026-09-15) | EC-141–213 | Predominantly Optimization-Governance Relevant (SGE/DGE/TMG/HAG/CIS/FTR/XEC own most of this range); **EC-151 (SPC self-failure) is Optimization-Failure/Recovery Relevant and is explicitly addressed below per generation-prompt §39/§87**; EC-152–165 (net-value/verifier) are Optimization-Specific and map to the Framework Principles' NOV formula and TECH-014 respectively |

**EC-151 validation (required by generation prompt §39, §87):** SPC's own internal failure (a meta-level failure of the self-protection mechanism itself, distinct from the overload conditions SPC exists to protect against) must default to the same deterministic fallback used for any Section 30/§16.1 failure for optimization-class stages, **while governance/security stages (SGE, DGE, TMG, HAG, CIS) remain unaffected and are never gated on SPC's own health** — `conventions.md` §7.9 states this precedence explicitly, and `edge-cases.md` EC-151 states it as a first-class scenario. This catalog's Framework Principles §"Governance independence" restates the same invariant, and TECH-013/014/015 (the entries most likely to invoke SGE/SPC signals) each cite it. **EC-151 is DIRECTLY COVERED via `SCN-STATE-005`** — `scenario-matrix.md`'s 2026-09-16 Targeted Fix Pass (§40) added `SCN-STATE-005` specifically to close what had been a genuine `NOT COVERED` gap in the preceding hardening-reconciliation pass, and its §48 Final Report confirms the current, authoritative baseline as 177 DIRECT / 35 PARTIAL / 0 NOT COVERED / 1 DUPLICATE with EC-151 among the DIRECT set. The §39 "Result" paragraph's `163/48/1 NOT COVERED` figures are that section's own historical narrative of the pre-fix-pass state, retained for audit trail, not scenario-matrix.md's current claim — see `CONTRA-OPTCAT-03` below (RESOLVED/HISTORICAL).

**PARTIAL edge-case coverage materially affecting an optimization entry (generation prompt §9):** per `scenario-matrix.md` §39, edge cases are classified DIRECTLY COVERED / PARTIALLY COVERED / NOT COVERED / DUPLICATE against the scenario matrix, not against this catalog — but several PARTIAL rows there materially bound what this catalog can claim. None are upgraded to DIRECT here without new source evidence, per generation-prompt §9:

| EC | Partial-coverage boundary (per `scenario-matrix.md` §39) | Affected Catalog Entry |
|---|---|---|
| EC-025 | Empty retrieval result set is a distinct case from adaptive-K sizing | TECH-005 (Explicitly Not Applicable When field states this precisely, not claiming full coverage) |
| EC-026 | Token-cost-penalty scoring vs. tiered eviction are distinct mechanisms | TECH-006 |
| EC-046 | Information-gain metric reliability vs. progress classification are distinct concerns | DA-014 |
| EC-069 | Threshold-misconfigured-to-zero is a config-validation concern, not a functioning-gate scenario | Framework Principles' quality-gate references (no single TECH/DA entry owns quality-threshold configuration validation) |
| EC-074, EC-075 | The closest scenario (`SCN-CMP-009`) is a loose thematic ("loop") match, not a behavioral one, for DA-009's and DA-014's specific mechanisms | DA-009, DA-014 |
| EC-135 | Reconciliation/revalidation/checkpointing overhead specifically (vs. general pipeline overhead) | Framework Principles' NOV formula — the overhead category exists in the formula but has no dedicated validating scenario yet |
| EC-153 | Unmeasurable term forcing `UNVERIFIED` is distinct from a measured net-negative value | Framework Principles' NOV formula (`UNVERIFIED` outcome) |
| EC-162, EC-165 | Caller-defect cases (consuming a verifier result without checking calibration) rather than runtime Control-Plane behavior | TECH-014 |
| EC-186/EC-207 | Content engineered specifically to survive compression/summarization, distinct from admission-time screening | TECH-007 |
| EC-211 | Verifier uncertainty combined with a model-*downgrade* (inverse of the escalation direction TECH-014 documents) | TECH-014 |

This is not the full 35-item PARTIAL set confirmed current by `scenario-matrix.md` §48's Final Report (see `CONTRA-OPTCAT-02` below, RESOLVED/HISTORICAL, on the earlier 48-item figure this superseded); it is the subset that materially bounds a specific catalog entry's claims, which is what generation-prompt §9 requires.

---
## SOURCE GAPS DISCOVERED

Gaps already tracked by a prior document are cited by their existing ID and not re-numbered; only genuinely new gaps discovered while writing this catalog receive a new `SOURCE-GAP-OPTCAT-NN` ID.

| Gap ID | Affected Entry | Missing Information | Why It Matters | Authoritative Source That Should Define It | Recommended Disposition | Blocking? |
|---|---|---|---|---|---|---|
| `SOURCE-GAP-OPTCAT-01` | Every `DA-NNN` entry's Implementation Priority field | `architecture.md` §36 assigns P0–P5 to the 20 canonical techniques and to broader categories (e.g., "Dynamic tool loading" under P4) but never assigns a priority tier to the 25 DA modules individually — this catalog's "P1-equivalent"/"P2-equivalent" labels are this document's own inference from the closest matching generic-technique tier or ARCH §36 category name, not a value stated for the DA module itself | Without this, a build-sequencing decision for DA modules has no authoritative source, only this catalog's best-effort inference | `architecture.md` §36 (a future revision) or `implementation-plan.md` | Non-blocking for this catalog (the inference is stated as such, not presented as an authoritative priority); should be resolved before `implementation-plan.md` is generated | No |
| `SOURCE-GAP-OPTCAT-02` | ES (Engineering Specification) citations for DA modules | The Engineering Specification elaborates PS §6's generic techniques into a dedicated ES §7.N subsection per technique, but does not carry an equivalent dedicated ES subsection per DA-001–DA-025 module — DA modules are elaborated primarily through PS §4C and `architecture.md` §22 instead | A reader following only the ES for DA-module detail will find less granularity than for generic techniques; not a behavioral gap, a documentation-depth asymmetry | Engineering Specification (a future revision) | Non-blocking — this catalog's DA entries correctly cite PS §4C/ARCH §22 instead of fabricating an ES subsection number that does not exist | No |
| `SOURCE-GAP-006` (carried from `scenario-matrix.md` §32) | TECH-014 (Model Cascade/Escalation), DA-014 | No document specifies how AR-004 Verifier-Guided Escalation interacts with a non-idempotent side effect the rejected first-tier attempt already triggered | Escalation is described as if retry is always free; this breaks when the first attempt had an external side effect | PS §38 (AR-004) or Engineering Specification §13.4 | Preserved as open, per `scenario-matrix.md`'s own re-verification that VCL (H06) formalizes verifier confidence, not this side-effect-interaction question | No |
| `SOURCE-GAP-007` (carried from `scenario-matrix.md` §32) | DA-007 (Dynamic MCP/Tool Selector), TE-002 composition | No document states that tool-argument-optimization aggressiveness should vary with a task's security-sensitivity classification | A security-review task narrowed the same way as a routine lookup risks missing required findings | PS §36 (TE-002) or Engineering Specification §11.2 | Preserved as open, per `scenario-matrix.md`'s own re-verification that TMG (H11) governs identity/schema trust, not TE-002's argument-narrowing aggressiveness | No |
| `SOURCE-GAP-ARCH-02` (carried from `architecture.md` §47.18) | TECH-007 (Context Compression, via CIS), DA-007/DA-008 (via TMG/FTR) | The exact mechanism for CIS's prompt-injection/content-integrity screening, and for achieving each FTR feasibility tier per platform, is left as a downstream architecture decision — and `architecture.md` §47.18 itself names `optimization-catalog.md` (this document) as one of the documents it could be deferred to | This catalog's entries correctly state CIS/FTR's *governing role and invariants* (screening precedes admission; tier bounds reachable modules) but cannot state the *mechanism* without inventing one | `provider-matrix.md` and `security.md` (neither yet exists per root `CLAUDE.md`'s documentation chain) | Preserved as open and explicitly re-deferred forward to `provider-matrix.md`/`security.md`, consistent with `architecture.md` §47.18's own disposition — this catalog does not close it by inventing a mechanism | No |
| `SOURCE-GAP-005` (carried from `scenario-matrix.md` §32) | TECH-008 / DA-015 (Context/Code Reordering) | No document states that reordering optimizations must be validated against per-step authorization scope before being applied | Reordering is framed as a pure performance/quality trade-off with no stated authorization-awareness requirement | `architecture.md` §24 or `conventions.md` §6.1 | Preserved as open; TECH-008/DA-015's Explicitly-Not-Applicable-When field states the instruction-precedence risk this gap is adjacent to, but does not claim the authorization-scope check is specified | No |

---

## SOURCE CONTRADICTIONS

| Contradiction ID | Source Documents | Conflicting Statements | Affected Catalog Entries | Current Treatment | Status |
|---|---|---|---|---|---|
| `CONTRA-OPTCAT-01` | `architecture.md` §23 vs. `architecture.md` §36/§42 vs. this generation prompt's own §59 | `architecture.md` §23's "Production Maturity" field for every one of the 20 techniques conflates Implementation Priority (P0–P5, §36) with a maturity-like validation-gate description (e.g., "P1 -- Requires passing experimental validation matrix before enabling"), rather than keeping the two dimensions separate the way §42's 0–5 maturity model implies they should be | All 20 `TECH-NNN` entries | This catalog keeps **Implementation Priority** and **Maturity Level** as two separate fields per entry (per the generation prompt's own §59 instruction), resolved *in this document* without editing `architecture.md` | Resolved in this document only; `architecture.md` §23 is left as-is, per the "no source rewrites" rule |
| `CONTRA-OPTCAT-02` | This document's own generation prompt §6/§9 vs. `scenario-matrix.md` §39 (as read at initial catalog generation) vs. `scenario-matrix.md` §48 (current) | At initial generation, the generation prompt's stated baseline asserted "35 PARTIAL EC coverage" while `scenario-matrix.md` §39's "Result" paragraph stated "48/213 (23%) PARTIALLY COVERED" (the pre-fix-pass figure) | `## Edge-Case Relevance Summary`'s PARTIAL-coverage table above | **RESOLVED / HISTORICAL.** `scenario-matrix.md`'s 2026-09-16 Targeted Fix Pass upgraded 13 of the 48 PARTIAL rows to DIRECT after genuine, non-fabricated traceability refinement, bringing the total to 35 PARTIAL — confirmed as the current, authoritative figure by §48's Final Report ("213/213 reconciled (177 DIRECT, 35 PARTIAL, 0 NOT COVERED, 1 DUPLICATE)"). The generation prompt's "35 PARTIAL" baseline was therefore not stale relative to the true current source — it was §39's retained "Result" narrative describing the superseded pre-fix-pass state that was temporarily out of step. This catalog's own figures now match the current `scenario-matrix.md` baseline exactly. Resolution source: `scenario-matrix.md` §48 Final Report, 2026-09-16 Targeted Fix Pass. | Resolved — closed by the cited source update, not by this catalog inventing content |
| `CONTRA-OPTCAT-03` | `scenario-matrix.md` §39, internally | The EC-151 row in the §39 table states `SCN-STATE-005 \| DIRECTLY COVERED`, while the same section's "Result"/"On the single NOT COVERED case" paragraphs immediately below the table describe EC-151 as `NOT COVERED` | This catalog's EC-151 validation note above | **RESOLVED / HISTORICAL.** `scenario-matrix.md`'s 2026-09-16 Targeted Fix Pass (§40) explicitly added `SCN-STATE-005` to close what its own §39 "Result" paragraph honestly recorded as a genuine pre-fix-pass gap; §39's narrative text was intentionally left as the historical record of that gap being found and closed, not left as a live contradiction. §48's Final Report states the current, single authoritative figure (EC-151 among the 177 DIRECT, via `SCN-STATE-005`, 0 NOT COVERED). What first appeared as an internal inconsistency is in fact a before/after narrative within the same document. Resolution source: `scenario-matrix.md` §40 Targeted Fix Pass entry + §48 Final Report. | Resolved — the apparent inconsistency was a documented before/after narrative, not an unresolved defect; no source document was edited by this catalog |

No contradiction was found between the Problem Statement, Engineering Specification, `interfaces.md`, or `conventions.md` and this catalog's own content — every apparent tension checked (e.g., the H14/CIS screening-ordering requirement vs. §47.5's HYBRID-mode preference; SGE's budget independence vs. SEC-011) was already resolved by an explicit precedence rule in the source documents themselves, and this catalog's Framework Principles section restates those resolutions rather than re-deriving them.

---
## Final Report

**Counts:**
- `TECH-NNN` entries: **20** (exact match to `architecture.md` §23's technique count)
- `DA-NNN` entries: **25** (exact match to `architecture.md` §22, IDs DA-001–DA-025 reused verbatim, none renumbered)
- `P5 Index` entries: **11** (exact match to `architecture.md` §26, index-level only, no implementation-ownership claim)

**Category distribution:** Layer 1 (Agent/Workflow + Tool Execution) 10 entries; Layer 2 (Context/Cache/Prompt-Input) 28 entries; Layer 3 (Model/Provider Routing + Output/Reasoning) 7 entries — see `## Coverage Matrix`.

**Priority distribution:** P0: 1; P1 (or P1-equivalent): 30; P2 (or P2-equivalent): 9; P3 (or P3-equivalent): 4; P4-equivalent: 1 — totaling 45, per `## Coverage Matrix`'s reconciliation note.

**Maturity distribution:** 45/45 entries at Level 0 — RESEARCH, honestly reflecting this repository's pre-implementation status and the absence of any local benchmark evidence. No entry claims a higher maturity level than the evidence supports.

**Source gaps:** 6 total — 2 newly discovered by this catalog (`SOURCE-GAP-OPTCAT-01`, `02`, both non-blocking documentation-depth/inference-labeling gaps, not behavioral gaps) and 4 carried forward from `scenario-matrix.md`/`architecture.md` (`SOURCE-GAP-006`, `007`, `005`, `ARCH-02`), none of which this catalog resolves by inventing content — all remain open, consistent with their upstream disposition.

**Contradictions:** 3 total — `CONTRA-OPTCAT-01` (the Implementation-Priority/Maturity-Level conflation in `architecture.md` §23; **open in the source, resolved in this document only** by keeping the two fields separate, per generation-prompt §59 — `architecture.md` itself is left as-is, per the "no source rewrites" rule), `CONTRA-OPTCAT-02` (**RESOLVED/HISTORICAL** — the 48-item PARTIAL figure this catalog first read was `scenario-matrix.md` §39's pre-fix-pass narrative; §48's Final Report confirms 35 PARTIAL as the current, authoritative figure, which is what this catalog now states throughout), and `CONTRA-OPTCAT-03` (**RESOLVED/HISTORICAL** — EC-151's apparent table-vs-summary inconsistency in `scenario-matrix.md` §39 is a documented before/after narrative of the 2026-09-16 Targeted Fix Pass, not a live defect; EC-151 is confirmed DIRECT via `SCN-STATE-005` by §48's Final Report).

**Anti-pattern coverage:** 34/34 anti-patterns from `architecture.md` §41 have at least one guarding catalog entry or an explicit, honest architectural-scope flag; 0 unexplained uncovered anti-patterns.

**Requirement coverage:** OBJ-001–035, SEC-001–016 mapped at representative-entry granularity (not force-mapped cell-by-cell for NFR-001–014/AC-001–053, which are covered structurally and uniformly across every entry's schema fields — see `## Requirement → Catalog Traceability`'s closing note).

**Scenario coverage:** All 9 Domain P scenarios, all 4 Domain Q scenarios, and a representative, non-exhaustive set of Domain AD compound scenarios and other-domain scenarios reviewed for optimization relevance — not every one of the 263 scenarios is claimed to map to a catalog entry, per generation-prompt §70.

**Edge-case classification:** All 213 edge cases were individually reviewed against the latest `scenario-matrix.md` baseline for optimization relevance, governance relevance, failure/recovery relevance, or applicability to another subsystem, grouped by domain section per generation-prompt §8's five-way scheme. The confirmed current baseline is **177 DIRECT / 35 PARTIAL / 0 NOT COVERED / 1 DUPLICATE (=213)**, per `scenario-matrix.md` §48's Final Report; the 35-item PARTIAL set is represented by a materially-relevant subset in `## Edge-Case Relevance Summary`, and **EC-151 (SPC self-failure) is DIRECTLY COVERED via `SCN-STATE-005`** — not a remaining gap.

**Compound-scenario considerations:** Domain AD's 93 compound scenarios were reviewed for composition/ordering/conflict signal; the entries most affected (TECH-003/007/014/020, DA-007/009/014/018/019/023/024/025) each carry a compound-scenario citation in their Validated By field.

**Governing components exercised:** Of the 22 dynamic-execution/hardening components (ESM, CVM, WVM, CPM, RE, CIG, CEC, PRV, DPE, CAR, SRP, SPM, RCO, SGE, DGE, TMG, HAG, CIS, FTR, XEC, SPC, VCL), every one appears in at least one catalog entry's Governing Component(s) or Invalidation/Revalidation field except HAG (Human Approval Gate), which is correctly noted as a governance-plane component with no single owning `TECH-NNN`/`DA-NNN` entry (consequential-action gating is orthogonal to token/cost optimization by design, per SEC-015's "designated subset, never every action" framing).

**Production-guarantee claims:** Zero. Every numeric Expected Benefit figure in this catalog is explicitly labeled as a source-reported benchmark/reference value or an architecture-estimate, never a production guarantee, consistent with root `CLAUDE.md` rule 7 and `conventions.md` §26.

**Research evidence cited:** LLMLingua, LongLLMLingua, LLMLingua-2 (TECH-007); RouteLLM (TECH-013); FrugalGPT (TECH-014); Anthropic prompt-caching documentation (TECH-009) — all cited as evidence classification "Research evidence" or "Provider capability," never as an established production result, per `conventions.md` §26.

**Unresolved limitations:** (1) DA-module Implementation Priority is this catalog's own inference, not an authoritative source value (`SOURCE-GAP-OPTCAT-01`); (2) 5 entries (TECH-006, TECH-008, DA-011, DA-015, DA-017) have thin-but-honest scenario/EC coverage rather than a directly-isolating scenario; (3) the exact mechanism for CIS/FTR remains deferred per `SOURCE-GAP-ARCH-02`, correctly not invented here.

---

## Optimization Catalog Readiness

Every canonical technique (20/20) and every canonical DA module (25/25) is fully cataloged with the complete required schema (24/24 fields per entry, no blank fields, no meaningless placeholders — verified by direct inspection of all 45 entries during generation). Every entry carries traceability to at least the Problem Statement and Architecture, and the overwhelming majority carry Interfaces, Conventions, Edge-Case, and Scenario citations as well (see `## Cross-Document Coverage Matrix`). Security/governance precedence, negative optimization, dynamic-state staleness, recovery, and supersession are explicit in the Framework Principles section and repeated per-entry where materially relevant. EC-151 is directly covered via `SCN-STATE-005` and is explicitly addressed as such throughout. Research claims are evidence-classified throughout. No source document was modified. No downstream document was created. Remaining source gaps (6, all non-blocking) and contradictions (3, of which 2 — `CONTRA-OPTCAT-02`/`-03` — are RESOLVED/HISTORICAL following a 2026-09-17 targeted correction pass that reconciled this catalog against `scenario-matrix.md`'s current 177/35/0/1 edge-case baseline and confirmed EC-151/CONTRA-001 resolution; the third, `CONTRA-OPTCAT-01`, remains open in `architecture.md` §23 itself and resolved in this document only) are documented, bounded, and non-blocking — none prevents correct interpretation, safe behavior, traceability, or implementation of any canonical catalog capability.

`READY FOR NEXT DOCUMENTATION PHASE`

