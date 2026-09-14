# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository status: pre-implementation, docs only

This repository currently contains **no source code, no build system, no tests, and no package manifest** — not a git repository yet either. It holds two authoritative source documents plus four documents derived from them, for a system that has not been built:

| File | Role |
|---|---|
| `docs/Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt` | **The primary authoritative source** (EAIOC-SPEC-001). If any other doc conflicts with this file, this file wins. |
| `docs/Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md` | Companion engineering specification — authoritative alongside the problem statement for downstream documents (e.g. the scenario matrix, see below), but subordinate to it if the two conflict. |
| `docs/architecture.md` (EAIOC-ARCH-001) | Full architecture: objectives, pipeline stages, component specs, optimization catalog. Status: PRE-IMPLEMENTATION, pending approval. |
| `docs/interfaces.md` (EAIOC-INTF-001) | Contracts/schemas for all 62 interfaces (INTF-001–062) and 230+ types crossing component boundaries. |
| `docs/conventions.md` (EAIOC-CONV-001) | Mandatory engineering conventions (naming, module layout, fail-open/closed rules, anti-patterns) that any implementation must follow. |
| `docs/edge-cases.md` (EAIOC-EDGE-001) | Catalogue of edge cases (EC-001–EC-140), each with detection/expected-behavior/fallback/observability/test requirements. |

These six documents are the stabilized baseline the rest of the documentation set builds on. `docs/scenario-matrix.md` is the next document in the chain: a populated, 200+ scenario cross-validation matrix (currently 203 scenarios across 30 domains) generated from `docs/prompts/generate-scenario-matrix.prompt.md` and reconciled against the baseline above — it has passed its own readiness check (see `## 47. Scenario Matrix Readiness` at the end of the file) and must pass it again before any later optimization-specific document — `optimization-catalog.md`, `provider-matrix.md`, `cache-strategy.md`, `agent-optimization.md`, `inference-optimization.md`, `quality-gates.md`, `security.md`, `observability.md`, `eval.md`, `SCALING.md`, `implementation-plan.md`, ADRs — is generated. None of those later documents exist yet; generate and review them one at a time, in that order.

### Dynamic Execution / Control-Plane hardening amendment (2026-09-10)

The baseline was hardened once, after initial stabilization, to make dynamic/runtime execution semantics explicit (they were previously implicit in a request/response-shaped model). This added 13 components and 13 interfaces without touching or renumbering anything that existed before:

- **What was added:** Execution State Manager (ESM), Context Version Manager (CVM), Workflow Version Manager (WVM), Checkpoint Manager (CPM), Reconciliation Engine (RE), Context Integrity Gate (CIG), Context Expansion Controller (CEC), Permission Revalidation (PRV), Dynamic Policy Evaluation (DPE), Capability/Availability Resolver (CAR), Stale Result Protection (SRP), Supersession Manager (SPM), Recovery Coordinator (RCO).
- **Where it lives:** `architecture.md` §46 (component specs, state machine, integration points), `interfaces.md` §42 / INTF-050–062, Engineering Spec §41 (new objectives OBJ-015–022), `conventions.md` (the `execution/` package, §2.2 dependency rules, §16.2 fail-open/closed table), `edge-cases.md` §43 / EC-080–140, and reconciled into `scenario-matrix.md`.
- **Central invariant this amendment exists to enforce:** these components own *execution truth* over time (a request is a versioned, resumable, reconcilable lifecycle, not a single atomic transaction) — see the pattern below.

Because there is no implementation yet:
- **Do not invent build, lint, or test commands.** None exist. If asked to run tests, say so rather than guessing a stack.
- **Do not assume a language or framework.** None is specified in these docs — the conventions document describes package layout in a language-neutral pseudo-structure (e.g. `control_plane/core/interfaces/`), not an actual scaffolded project.
- When implementation begins, the first real task is almost always to reconcile new code against these documents, not to design from scratch.

## What this project is

An **Enterprise Agent & LLM Inference Optimization Control Plane**: a policy-driven optimization/governance layer that sits between AI applications/agents and LLM providers (and optionally the inference-serving layer). It is not a prompt-compression utility — its job is to decide, for every request, the cheapest safe way to satisfy the task, up to and including deciding that **no LLM call is needed at all** (cache hit, programmatic result, early exit).

Central objective (from `architecture.md` §4, restated verbatim in `conventions.md` §1.1):

```
MINIMIZE: Total Safe Inference Cost
SUBJECT TO: Quality, Correctness, Security, Reliability, Latency, Freshness, Compliance, Task completion
```

Token reduction alone is never success. Every optimization must pass a quality gate and net-positive cost accounting (`Net Savings = Inference Cost Avoided - Optimization Compute Cost - ...`) before being counted or enabled.

Four first-class execution environments must all be supported by one common optimization core (not separate optimizers per surface): (A) generic LLM apps, (B) autonomous/tool-using agents, (C) developer/coding agents (Cursor, Claude Code, Copilot, etc. — first-class, not optional), (D) multi-agent/sub-agent systems.

## Architecture at a glance

Three optimization layers governed by a shared **Optimization Intelligence Layer (OI)**:

- **Layer 1 — Agent/Workflow Optimization**: task planning, tool selection/execution, sub-agent economics, agent loops, early exit.
- **Layer 2 — Context Optimization**: retrieval, ranking, pruning, dedup, compression, reordering, budgeting, caching, freshness, dependency graphs.
- **Layer 3 — Inference/Serving Optimization** (optional, P5): model routing/cascade, reasoning budget, output budget, batching, KV cache, speculative decoding, quantization. Must stay measurement-separate from Layers 1/2.
- **OI layer** (OI-001..005) decides per-request which stages run, whether the optimizer's own overhead is worth it, how deep to optimize, context item ROI, and outcome-based cost.

The full per-request pipeline (T0 → T3) is documented stage-by-stage in `architecture.md` §8 (generic) and §9 (developer-agent variant, which runs on *every* model-bound iteration, not just the first request). The canonical 24-stage default execution order is in `conventions.md` §6.1 — read it before proposing a different stage ordering; reordering is allowed only with benchmark evidence.

Component IDs follow `<LAYER>.<SEQUENCE>-<NAME>` (e.g. `T1.3-CONTEXT-PRUNER`, `OI.1-DECISION-ENGINE`, `DA.008-TOOL-SCHEMA-OPTIMIZER`) — see `conventions.md` §3.1. The proposed (not yet scaffolded) package layout mirrors these IDs 1:1 under `control_plane/` — see `conventions.md` §2.1 before creating any new module so its location matches this map.

## Non-negotiable rules for any implementation work here

These are structural, not stylistic — violating them is a spec violation, not a style nit:

1. **Provider neutrality.** Core code must never import or hard-code a specific LLM provider, model name, agent framework, MCP library, or embedding model. All of that goes through adapter interfaces (`providers/adapters/`); provider-specific data goes in an opaque `provider_hints` map, never a first-class schema field.
2. **Fail-open on optimization, fail-closed on security — and these are not the same category of failure.** An optimization stage (pruning, compression, caching, routing, reconciliation retries, ...) that fails must fall back to the unoptimized/original path and let the request continue, *provided the request remains authorized and policy-compliant*. A failure to establish or evaluate authorization/policy/PII-scrubbing itself — as opposed to an optimization stage failing on top of an already-established authorization — must reject the request. Never conflate the two: an innocent compressor bug on an authorized, policy-compliant request must never turn into a full request rejection (see `conventions.md` §16.2, `edge-cases.md` §43.13).
3. **Never reduce reasoning budget solely to hit a token target.** Reasoning cuts must be benchmarked against a quality baseline first (`conventions.md` §10.3).
4. **Tenant isolation is absolute.** Every cache key, memory entry, cost record, and event must be scoped by `tenant_id` as the first namespace component. Cross-tenant lookups, sharing, or cost blending are unconditionally prohibited (`conventions.md` §13.2).
5. **Never fabricate savings.** If token/cost accounting can't be verified, mark it `unverified` and exclude it from savings reporting — don't report a number you can't back.
6. **Security-classified content (SEC-protected instructions, PII policies, auth rules) is never pruned, compressed away, or deduplicated out**, regardless of relevance score or budget pressure.
7. **Research benchmark percentages are not production guarantees.** Any number quoted from a paper (e.g. LLMLingua, RouteLLM, FrugalGPT) in the docs is a validation target to re-measure locally, never a number to hard-code as an expected result.
8. **Full conversation-history replay to a sub-agent is prohibited.** Sub-agent handoffs must go through the compressed `SubAgentContextHandoff` (objective, findings, evidence, decisions, unresolved questions) — see `conventions.md` §12.4.
9. See `conventions.md` §24 for the full anti-pattern list (e.g. fixed Top-K for every query, always using the strongest/cheapest model, indiscriminate compression) — treat it as a code-review checklist.

## Working with these documents

- The problem statement (`.txt`) is authoritative; `architecture.md`, `interfaces.md`, and `conventions.md` are its derived, cross-referenced elaborations and must not silently drop, simplify, or reinterpret anything in it. If you're asked to change one of the derived docs, verify consistency against the problem statement rather than editing in isolation.
- Every requirement in these docs is traceable via IDs: `OBJ-NNN` (22 objectives — OBJ-001–014 original, OBJ-015–022 from the hardening amendment), `SEC-NNN` (10 security requirements), `NFR-NNN` (13 non-functional requirements), `AC-NNN` (38 acceptance criteria), `EC-NNN` (140 edge cases — EC-001–079 original, EC-080–140 from the hardening amendment), `DA-NNN` (25 developer-agent modules), `OI/CL/CE/TE/AL/AR/QO/EL-NNN` (intelligence/lifecycle/economics modules), `SCN-<CODE>-NNN` (scenario matrix, 30 domain codes A–AD). When adding new material, give it an ID in the matching series and cross-reference it the way existing entries do, rather than inventing a new numbering scheme.
- These files are large (architecture.md ~3300 lines, interfaces.md ~3900 lines, conventions.md ~1400 lines, edge-cases.md ~5500 lines, scenario-matrix.md ~10,400 lines). Prefer `Grep` for a known section/ID over reading a whole file, and when reading, target the specific numbered section (table of contents at the top of each doc) rather than paging through sequentially. For `scenario-matrix.md` specifically, delegating a research/cross-reference pass to a forked subagent (rather than reading the whole file into your own context) is usually the right call.
- The edge-case doc format is fixed (Domain/Objective/Severity/Likelihood/Reversible fields + Scenario/Trigger/Why It Matters/Detection/Expected Behavior/Fallback/Safety Implications/Observability/Testing Requirements). Any new edge case you document must follow this exact structure so it stays consistent with the other entries.
- **Source gaps and contradictions are tracked, not resolved by inventing content.** When a document is genuinely underspecified by its sources, or two documents disagree, record it as `SOURCE-GAP-NNN` or `CONTRA-NNN` (see `scenario-matrix.md`'s own gap/contradiction sections, and the `> **Source Gap:**` callouts in `edge-cases.md` §43) rather than guessing at normative behavior. A gap only gets marked resolved when a source document is actually updated to close it — never by removing the gap record to make coverage numbers look better.
- **Reconciliation passes are single-file-scoped.** The established pattern in this repo (used for the `conventions.md`, `edge-cases.md`, and `scenario-matrix.md` hardening passes) is: read all the source documents needed to reconcile, but write to only the one named target file; verify afterward (via `git diff` if a git repo exists, or file-modification-time comparison otherwise) that no other file changed; and stop for the next instruction rather than cascading into the next document in the chain unprompted.

## Prompt-writing rule (existing project rule)

`.claude/rules/prompt-writing.md` applies whenever asked to create/write/draft/review a prompt for a fresh Claude/Claude Code session, for paths matching `outputs/*prompt*.md`, `outputs/**/prompt*.md`, or `prompts/**`. Key points: deliver the finished prompt only in chat inside one fenced code block (never write a file to those paths), then copy it to the clipboard via a quoted `pbcopy` heredoc as the last step. Target a >95/100 self-score against the rubric in that file before delivering.
