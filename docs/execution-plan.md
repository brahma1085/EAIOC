# Enterprise Agent & LLM Inference Optimization Control Plane — Execution Plan

**Document ID:** EAIOC-EXECPLAN-001
**Status:** PRE-IMPLEMENTATION — Level 0 (RESEARCH). This document plans execution; it does not itself constitute implementation, and its existence does not change the repository's maturity level.
**Version:** 1.0.3
**Generated:** 2026-09-23 (per repository date)
**Generation prompt:** `docs/prompts/execute-p0-foundation.prompt.md` (Master Prompt — Execution Plan Generation + Gated Implementation Execution, Mode A)
**Mode executed:** MODE A — Execution-Plan Generation. No source code was written in producing this document.
**Correction history:** v1.0.0 → v1.0.1 — surgical correction pass per `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.1.md`: removed a Sub-phase A/B shared-commit contradiction (§18.4) and all wording that could be misread as permitting parallel *execution* of Capabilities 1–3 in Mode B (§18.3, §48, §H below), while preserving the true dependency-independence fact those capabilities share. No requirement, scope, technology-baseline, or source-gap content was altered — see this document's own version-1.0.1 self-check at the end. **v1.0.1 → v1.0.2** — surgical correction pass per `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.2.md`: reclassified the Capability Gate as a post-lifecycle review/promotion checkpoint rather than a ninth implementation sub-phase (§18.10) — it receives its own explicit human approval but no implementation commit and no `EXE-P0.<n>.<letter>` ID — and corrected every dependent count reference from "54 atomic execution units" to "48 atomic `EXE-P0.<n>.<letter>` execution units + 6 Capability Gates = 54 total gated checkpoints" (§18.2, §H, and this header). No scope, technology-baseline, source-gap, or sequential-execution content was altered — see this document's own version-1.0.2 self-check at the end. **v1.0.2 → v1.0.3** — surgical correction pass per `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.3.md`: added a new §18.11 "Claude Code Mode B Execution Command Protocol" — the canonical `Execute EXE-P0.<n>.<letter>`/`Approve EXE-P0.<n>.<letter>`/`Approve CAPABILITY-GATE P0.<n>` command syntax, the required per-unit response/reporting format, a command-state table, a prohibited-ambiguous-command list, and the full six-capability copy/paste operator reference sequence. No scope, architecture, technology-baseline, requirements, source-gap, ADR, or sequencing content was changed — see this document's own version-1.0.3 self-check at the end.

**Relationship to the documentation chain:** Not part of the originally-planned chain (2 authoritative sources + 5 baseline documents + 12 generated documents + ADR set + Implementation Readiness Gate). It is a downstream implementation-planning artifact consuming that completed chain — it does not replace `architecture.md`, `implementation-plan.md`, `requirements-traceability.md`, any ADR, `security.md`, `observability.md`, or any other approved authoritative source, and it does not modify any of them.

**Documents read in full or by targeted section for this generation:** root `CLAUDE.md`; `docs/implementation-plan.md` (full); `docs/implementation-readiness-gate.md` (full); `docs/requirements-traceability.md` (full); `docs/adr/0000-index.md` and `0001`–`0006` (full); `docs/conventions.md` §2.1/§2.2/§3.1/§3.2, §13, §16.2, §24, §25; `docs/security.md` (full); `docs/observability.md` (full); `docs/quality-gates.md` (full); `docs/eval.md` (full); `docs/SCALING.md` (full); `docs/provider-matrix.md` §1 (Provider Accessibility Model), §9.1 (FTR); `docs/cache-strategy.md` (key-construction templates, §1–2); `docs/architecture.md` §5, §11.1, §12.2–12.4, §27, §32, §36, §46, §47 (targeted sections, cross-referenced from the documents above); `docs/scratch/EAIOC_Tech_Stack_Brainstorming_Conversation.txt` (full — the technology-baseline source this document validates).

**Path corrections applied against the generation prompt's own file list (repository reorganized earlier in this session, after the prompt was originally drafted):** the ADR index is at `docs/adr/0000-index.md`, not `docs/adr/ADR-0000-index.md`; the tech-stack brainstorming transcript is at `docs/scratch/EAIOC_Tech_Stack_Brainstorming_Conversation.txt`, not at the repository root.

**Scope boundary reminder:** This document owns build-sequencing execution detail (atomic `EXE-P0.<n>.<letter>` units), technology-baseline validation, repository/module structure, request-critical-core classification, and release/deployment sequencing. It does not redefine `architecture.md`'s component contracts, `interfaces.md`'s schemas, `conventions.md`'s package layout (consumed as-is), any `TECH-NNN`/`DA-NNN`/`GSP-NNN`/`QG-NNN`/`SC-NNN` catalog entry, any ADR's decision content, or `implementation-plan.md`'s own P0–P5 sequencing logic (consumed, elaborated into atomic units for P0 only) — every one of those is cited, none is restated as if newly defined here.

---

## 1. Document Control

| Field | Value |
|---|---|
| Document ID | `EAIOC-EXECPLAN-001` |
| Title | Enterprise Agent & LLM Inference Optimization Control Plane — Execution Plan |
| Level | LEVEL 0 — RESEARCH / PRE-IMPLEMENTATION |
| Status | Mode A (planning) output; Mode B (execution) has not yet begun as of this document's generation |
| Version | 1.0.3 (surgical correction — see Correction History above) |
| Owning artifact class | Downstream implementation-planning document, outside the original chain, alongside the ADR set and the Implementation Readiness Gate |
| File-scope this generation | Only `docs/execution-plan.md` created. No upstream, sibling, or ADR document modified. Verified via `git status --short`/`git diff --stat` (§52). |

## 2. Purpose

Answer, with evidence traced to the authoritative corpus rather than invented: **given that `docs/implementation-readiness-gate.md` found P0 startable (7 of 8 items unconditionally `READY`, Observability `READY WITH CONDITION`), what is the concrete, atomic, one-controlled-step-at-a-time execution sequence for actually building the six unconditionally-clear P0 capabilities — and does the brainstormed technology baseline actually fit that work without conflicting with the frozen architecture?** This document is the operational bridge between `implementation-plan.md`'s sequencing logic (which capability before which, and why) and an actual Claude Code session executing one `EXE-P0.<n>.<letter>` unit at a time under explicit human approval gates (Mode B, this document's companion execution mode, triggered separately by the user).

## 3. Scope

**In scope:** the six P0 Foundation capabilities `docs/implementation-readiness-gate.md` §10–§11 found unconditionally ready — Token Accounting and Cost Ledger, Baseline Benchmark Harness + Quality Evaluation, Sanitizer, Context Policy, Prompt Assembler, Output Controls — decomposed into atomic execution units; technology-baseline validation against the frozen architecture; repository/module structure; the first proving-lab measurement slice (Path A baseline vs. Path B EAIOC).

**Out of scope:** implementing Observability (the 8th P0 item — `READY WITH CONDITION`, blocked on a one-line interim-telemetry-path decision this document does not make, per `SOURCE-GAP-IRG-02`); accepting any ADR; selecting a final technology where an ADR remains open (ADR-0001/0002/0003/0004/0005/0006 all stay `PROPOSED`); writing production code beyond what Mode B's gated execution actually produces; declaring any capability production-ready, benchmark-validated, or enterprise-scale-ready.

## 4. Source Hierarchy

Per the generation prompt's §1, applied throughout this document, highest authority first:

1. Approved/frozen EAIOC product and engineering documents (`problemstatement.txt`, Engineering Spec, `architecture.md`, `interfaces.md`, `conventions.md`, `edge-cases.md`, `scenario-matrix.md`)
2. `requirements-traceability.md`
3. The ADR corpus (all six `PROPOSED`)
4. `implementation-readiness-gate.md`
5. `implementation-plan.md`
6. Current repository structure (at generation time: documentation only — no `control_plane/` tree exists yet; this document is the first artifact to propose one)
7. `docs/scratch/EAIOC_Tech_Stack_Brainstorming_Conversation.txt` (proposed technology baseline — validated in §6, never treated as equal-or-higher authority than items 1–5)
8. General engineering knowledge, used only where the authoritative corpus is silent (e.g., which specific test-runner idiom Java/Spring Boot conventionally uses)

No conflict was found between the technology baseline (item 7) and any higher-authority source (§6). Where the baseline proposes something the architecture does not require yet (e.g., Kafka, Kubernetes, a UI), this document classifies it as deferred rather than treating the brainstorm as authorizing early adoption.

## 5. Product Baseline

Restated, not redefined, from root `CLAUDE.md` and `architecture.md` §1: EAIOC is an **AI execution control plane** — a policy-driven optimization/governance layer between AI applications/agents and LLM providers, whose job is to decide the cheapest **safe** way to satisfy every request, including deciding no LLM call is needed at all. It is explicitly **not** a chatbot, replacement agent, prompt-compression utility, general-purpose IDE, model-training system, model-runtime infrastructure platform, provider, or replacement agent orchestrator (`architecture.md` §47.13, H20 — restated, not reopened). Four first-class execution environments (generic LLM apps, autonomous/tool-using agents, developer/coding agents, multi-agent systems) share one optimization core — no per-surface optimizer is ever built. The central objective, unchanged: `MINIMIZE Total Safe Inference Cost SUBJECT TO Quality, Correctness, Security, Reliability, Latency, Freshness, Compliance, Task completion`.

## 6. Technology Baseline (Validation)

The brainstormed baseline (`docs/scratch/EAIOC_Tech_Stack_Brainstorming_Conversation.txt`, §21 "Final technology stack") was checked line-by-line against `architecture.md`'s component contracts, `conventions.md`'s package layout, and every open ADR. **Finding: no conflict was found between the proposed baseline and the frozen architecture** — the architecture is deliberately language/framework-neutral (root `CLAUDE.md`: "do not assume a language or framework... none is specified in these docs"), so a language/runtime choice cannot contradict it; it can only fail to satisfy a structural rule (provider neutrality, package-layout compatibility, fail-open/fail-closed discipline), and none of the choices below does.

Per the generation prompt's own required classification (§5 of that prompt):

| # | Technology | Classification | Rationale |
|---|---|---|---|
| 1 | Java 25 / Spring Boot 4.1.x / Maven — request-critical core | **A — P0 selected baseline** | No upstream document names a language; nothing in `architecture.md`/`conventions.md` conflicts with it; the brainstorm's own final diagram places exactly the six in-scope P0 capabilities (Token Accounting, Policy, Prompt Assembly, Output Controls, plus implicitly Sanitizer and Context Policy) inside this core. Not an ADR candidate — no ADR among the six covers language selection; this is a new, explicit decision this document records (§39) rather than silently assuming. |
| 2 | PostgreSQL 18.x + pgvector — durable store | **B/E — Enterprise target, gated by ADR-0001/0002** | Both PROPOSED, neither `ACCEPTED`. IRG §9 confirms ADR-0002 is moot for P0 (no P0 item depends on it); none of the six in-scope capabilities requires a durable multi-process store to reach their own Capability Gate. P0 build uses an in-memory/embedded reference store, mirroring ADR-0001's own stated interim-path pattern ("P1 can build the behavioral contract against an in-memory reference implementation first," `implementation-plan.md` §24). |
| 3 | Redis 8.x — fast cache/transient state | **B/E — Enterprise target, gated by ADR-0001** | Same reasoning as row 2. None of the six P0 capabilities is a caching capability (caching is P1, `TECH-009`/`010`). Not needed to reach any P0 Capability Gate. |
| 4 | Apache Kafka 4.3.x — eventing | **B — Enterprise target, deferred** | The brainstorm's own text states Kafka "is NOT a mandatory P0 prerequisite unless an authoritative P0 dependency requires it" — verified: none of the six in-scope capabilities is asynchronous/event-driven at the P0 minimal-implementation level. |
| 5 | Python 3.14.x / FastAPI — evaluation/intelligence | **B — Enterprise target, deferred past this P0 slice** | Relevant only to Capability 2 (Baseline Benchmark Harness + Quality Evaluation), whose P0 scope per `implementation-plan.md` §8.2 is deliberately minimal ("no optimization stage exists yet to compare against, so `run_optimized()` is a no-op until P1 ships") — it does not yet need Python's statistical/ML capability. Building it directly in the Java core for P0 avoids a premature two-runtime split, consistent with the brainstorm's own explicit warning: "Python should not enter the request-critical path merely to make the architecture polyglot," and its own Phase 1 (Java core) → Phase 2 (Python) sequencing. **Judgment call, not a source-mandated conclusion — recorded, open to revision at Capability 2's own Capability Gate if the harness's needs outgrow a single-runtime implementation.** |
| 6 | Go 1.27.x — extraction candidate | **C — Future extraction candidate** | Explicit in the brainstorm itself: "an extraction candidate, not a P0 requirement." No P0 capability needs it. |
| 7 | OpenTelemetry / Collector / Prometheus / Grafana / Tempo-Jaeger / Loki | **E — Requires ADR-0004** | PROPOSED, not accepted. Directly relevant only to the Observability P0 item, which is explicitly **out of scope** for this execution slice (§3). The six in-scope capabilities' own Sub-phase D ("cite `observability.md`'s already-named metrics") needs only a minimal interim sink (structured log output is sufficient) — this gap is recorded as `SOURCE-GAP-EXECPLAN-01` (§38), mirroring `SOURCE-GAP-IRG-02`'s own finding that no interim telemetry path is documented anywhere upstream. |
| 8 | OAuth 2.0 / OIDC / JWT / RBAC / Keycloak | **B/E — Enterprise target, deferred** | No in-scope P0 capability stands up authentication/identity; that is `AuthorizationService`/`security.md`'s TMG territory, not P0 build content. Keycloak is explicitly a reference implementation in the brainstorm itself, not an architectural dependency. |
| 9 | React / TypeScript UI | **C — Deferred indefinitely for this slice** | All six in-scope capabilities are backend-only; no UI surface is part of any of their Capability Gates. |
| 10 | Docker / Docker Compose | **A — P0 selected, for the proving-lab environment only** | Low-risk, no architecture conflict, directly supports §16's Path A/Path B proving-lab requirement. Not required by any individual capability's own Minimal Implementation. |
| 11 | Kubernetes / Helm | **B — Enterprise target, deferred** | Brainstorm's own Deployment Evolution places this at Stage 4, well past the reference-environment stage this execution slice targets (§31). |
| 12 | GitHub Actions | **A/E — Pragmatic interim CI runner, not an ADR acceptance** | ADR-0003 (CI/CD platform) is `PROPOSED`. Per that ADR's own finding, "every capability's Capability Gate implicitly depends on this ADR's eventual resolution to actually execute, though no capability's design depends on it" — a low-risk, reversible interim choice lets the §16 gate progression actually run now without prejudging ADR-0003's eventual acceptance. |
| 13 | Provider-neutral adapter layer | **E — Deferred; contract respected now regardless** | ADR-0005 (provider-adapter implementation pattern) is `PROPOSED`. No in-scope capability calls a live provider — Prompt Assembler (Capability 5) only needs to consume the already-fixed `provider_hints` opacity contract (`interfaces.md`, cited), never the adapter's internal structure. The **contract** (no provider-specific field in core schema) is binding now; the **pattern** (how `providers/adapters/` is internally built) is not needed until a real adapter is built, which is outside this slice. |
| 14 | MCP + agent-harness adapters | **C — Deferred to P1+** | No in-scope P0 capability performs tool/MCP work (that is P1's Tool-Output Filtering / P4's Dynamic Tool Loading territory). |

**No genuinely unresolved technology choice blocks this execution slice.** Per the generation prompt's own §14 instruction ("if validation finds a genuinely unresolved technology choice that only the user can decide, STOP and ask; otherwise proceed without unnecessary clarification"), row 1 (Java/Spring Boot as the language for the six in-scope capabilities) is the one choice this document treats as settled by the already-completed brainstorming session and the technology baseline the user supplied via this generation prompt — it is not re-asked here. Mode B's own execution prompt (the prior `execute-p0-foundation.prompt.md` draft's Step 0) is superseded by this validated baseline for the six in-scope capabilities; Mode B does not need to re-open the language question.

## 7. Architecture-to-Implementation Mapping

| Architecture Concept | Owning Document | Implementation Consequence (this document) |
|---|---|---|
| T0–T3 pipeline stages (`architecture.md` §7–§13) | `architecture.md` | Capabilities 3 (Sanitizer, T1.1), 5 (Prompt Assembler, §12.2), 6 (Output Controls, §12.3–12.4) map directly to named pipeline stages; Capabilities 1, 2, 4 (Token Accounting, Benchmark Harness, Context Policy) are cross-cutting substrate the pipeline stages consume, not pipeline stages themselves |
| `control_plane/` package layout (`conventions.md` §2.1) | `conventions.md` | §9 below maps each of the six capabilities to its exact subpackage |
| Module dependency rules (`conventions.md` §2.2) | `conventions.md` | `core/interfaces/`/`core/schemas/` built first, zero dependencies; no capability imports `providers/adapters/` |
| Capability Lifecycle Model (`implementation-plan.md` §6) | `implementation-plan.md` | Every `EXE-P0.<n>.<letter>` unit in §18 below maps 1:1 to a lifecycle sub-phase (A–H + Gate) |
| Fail-open/fail-closed (root `CLAUDE.md` rule 2; `conventions.md` §16.2) | root `CLAUDE.md` | Every capability's Sub-phase H states its own disposition explicitly (§18) |
| Tenant isolation (root `CLAUDE.md` rule 4) | root `CLAUDE.md` | §25 |

## 8. Repository Structure

No `control_plane/` tree exists in the repository at this document's generation time — this is the first artifact to propose populating it. Per `conventions.md` §2.1, only the subpackages the six in-scope capabilities actually touch are created in Mode B; no P1+ subpackage is pre-scaffolded:

```
control_plane/
├── core/
│   ├── interfaces/     # zero-dependency contract layer — built first, shared by all six capabilities
│   ├── schemas/        # depends only on core/interfaces/
│   └── errors/         # ControlPlaneError taxonomy
├── accounting/         # Capability 1 — Token Accounting and Cost Ledger (ARCH §27; conventions.md §2.1)
├── benchmarking/        # Capability 2 (benchmark-harness half) (ARCH §32)
├── evaluation/          # Capability 2 (quality-evaluation half) (INTF §18)
├── pipeline/
│   └── t1_context/
│       └── sanitizer/  # Capability 3 — Sanitizer, T1.1 (ARCH §11.1)
├── policy/              # Capability 4 — Context Policy (INTF §20 — see §9's honest note below)
│   └── prompt_assembler/  # Capability 5 — Prompt Assembler, ARCH §12.2 (nested here per §9's rationale)
└── (output controls)   # Capability 6 — see §9's honest note; no pre-existing conventions.md leaf matches "Output Controls" exactly
```

## 9. Module Boundaries

Per capability, verified against `conventions.md` §2.1 rather than assumed — two capabilities (Context Policy, Output Controls) have **no dedicated leaf directory named in the existing package tree**, and this document does not invent one that conflicts with the established layout; instead it records the honest finding and proposes the most structurally consistent placement, flagged for confirmation at that capability's own Sub-phase A (Contract & Design), not silently assumed as final:

| Capability | Package | Confidence |
|---|---|---|
| 1. Token Accounting and Cost Ledger | `control_plane/accounting/` | **Direct match** — `conventions.md` §2.1 names this exact leaf, cross-referenced from `architecture.md` §27 |
| 2. Baseline Benchmark Harness + Quality Evaluation | `control_plane/benchmarking/` + `control_plane/evaluation/` | **Direct match** — both leaves exist in `conventions.md` §2.1, cross-referenced from `architecture.md` §32/`interfaces.md` §18 |
| 3. Sanitizer | `control_plane/pipeline/t1_context/sanitizer/` | **Direct match** — `conventions.md` §2.1 names this exact leaf, cross-referenced from `architecture.md` §11.1 (T1.1) |
| 4. Context Policy | `control_plane/policy/` (proposed) | **No exact match found.** `conventions.md` §2.1 has a `policy/` top-level leaf citing `interfaces.md` §20 with no further subdivision; `architecture.md` has no dedicated subsection for "Context policy" beyond its §36 P0-list mention (verified directly, §6 of the companion execution prompt already recorded this). Proposed placement: `control_plane/policy/context_policy/`, to be confirmed (not assumed) at `EXE-P0.4.A`. |
| 5. Prompt Assembler | `control_plane/prompt_assembler/` (proposed) | **No exact match found.** Not listed as its own leaf in `conventions.md` §2.1's tree despite being a named, elaborated component (`architecture.md` §12.2). Proposed top-level placement mirroring `architecture.md` §12's pipeline-adjacent position, to be confirmed at `EXE-P0.5.A`. |
| 6. Output Controls | `control_plane/output_controls/` (proposed) | **No exact match found.** `architecture.md` §12.3 (Output Schema Selector) and §12.4 (Output Length Controller) are two named components; `conventions.md` §2.1 has no combined "Output Controls" leaf. Proposed placement groups both under one package since `architecture.md` §36's P0 item list treats them as one item ("Output controls"), to be confirmed at `EXE-P0.6.A`. |

This is recorded honestly rather than silently assumed, per this repository's own established discipline of not inventing a package location not present in its conventions.

## 10. Runtime Allocation

All six in-scope capabilities run in the Java 25 / Spring Boot 4.1.x request-critical core (§6, row 1; §11 below). No capability in this execution slice is allocated to Python, Go, or a separate service — that split is deferred past this slice (§32, Polyglot Evolution).

## 11. Request-Critical Core

Per the generation prompt's own required classification, independently checked against `implementation-plan.md`'s dependency model rather than assumed from the brainstorm's diagram alone:

| Capability | Hot/Warm/Cold | Sync/Async | Stateful/Stateless | Latency-Sensitive | Runtime | Reason | Future Extraction Candidacy |
|---|---|---|---|---|---|---|---|
| 1. Token Accounting | Hot | Sync (write path), async-tolerant (aggregation) | Stateful (ledger) | Yes — every request writes to it | Java core | Every T0–T3 stage and every later technique depends on it existing first (`implementation-plan.md` §8.1) | Low — foundational, unlikely to extract |
| 2. Benchmark Harness + Quality Evaluation | Warm | Mostly sync for the P0 minimal path (`run_optimized()` is a no-op) | Stateful (EvaluationRun records) | No — not on the live request's critical latency path at P0 scope | Java core (P0); Python candidate once statistical methodology (`eval.md` §17–20) actually activates in P1+ | P0 scope is minimal recording only | **High** — natural Python extraction point once P1's EL-NNN modules activate |
| 3. Sanitizer | Hot | Sync | Stateless | Yes — first content-safety touchpoint every later stage assumes ran | Java core | T1.1, first pipeline stage | Low |
| 4. Context Policy | Hot | Sync | Stateless (reads config) | Yes — every T1-series consumer reads it | Java core | Budget/policy substrate | Low |
| 5. Prompt Assembler | Hot | Sync | Stateless | Yes — final assembly before the model call | Java core | ARCH §12.2, directly on the request path | Low |
| 6. Output Controls | Hot | Sync | Stateless | Yes — post-response, before the response returns to the caller | Java core | ARCH §12.3–12.4 | Low |

No capability is placed in the hot path merely because it exists in the architecture — each row's Hot/Warm classification is independently justified above, per the generation prompt's own instruction (§15).

## 12. Data Architecture

`cache-strategy.md`'s key-construction template (cited, not redefined) is the pattern every capability's own keyed state follows: `{tenant_id}:{type}:{namespace}:{artifact_identity}[:{model_id}][:{provider_id}]`. None of the six in-scope capabilities is a cache type itself, but the tenant-first-namespace-component discipline (root `CLAUDE.md` rule 4) applies identically to Capability 1's ledger records and Capability 2's `EvaluationRun` records — both are the two stateful surfaces in this slice.

| Domain | Durable vs. Transient (this slice) | Tenant Scoping | Versioning | Notes |
|---|---|---|---|---|
| Token/cost ledger entries (Capability 1) | In-memory reference store for P0 (§6, row 2); durable store gated by ADR-0001/0002 | `tenant_id` first field, no exceptions | Append-only per request | `unverified` flag on any term that cannot be verified (root `CLAUDE.md` rule 5) |
| `EvaluationRun` records (Capability 2) | In-memory reference store for P0 | `tenant_id`-scoped | `run_id`/`timestamp` per `interfaces.md` §18 | `run_optimized()` is a no-op at P0 scope |
| Context Policy configuration (Capability 4) | In-memory/config-file for P0 | Tenant-overridable, not itself keyed data | `policy_version` (`conventions.md` §3.2) | Read-only from the other five capabilities' perspective |
| Sanitizer, Prompt Assembler, Output Controls | Stateless — no persisted data model of their own at P0 scope | N/A | N/A | Pure transformation stages |

**Preserved distinction (root `CLAUDE.md`, `architecture.md` §46):** none of the state above is `ESM` execution state or a governance `AuditRecord` — this document does not conflate a capability-local ledger/evaluation record with execution truth or audit records, both of which remain owned by components explicitly out of this slice's scope (ESM/CVM/WVM, `security.md`'s GSP entries).

## 13. API/Contract Implementation

No external-facing API is built in this slice — all six capabilities are internal Java-core modules consumed by other in-process modules, not exposed as a standalone service endpoint yet (that begins at Deployment Evolution Stage 2, §31). Each capability's own contract is whatever `interfaces.md`/`architecture.md` schema it consumes (§7); no new contract is invented here beyond the package-boundary interfaces the six modules expose to each other (Token Accounting exposes a write API every other module calls; Context Policy exposes a read API Prompt Assembler and Output Controls consume; Sanitizer exposes a transform API upstream of Prompt Assembler).

## 14. Provider Adapter Strategy

No live provider adapter is built in this slice (§6, row 13). Prompt Assembler (Capability 5) is the one in-scope capability that touches provider-facing structure at all — it must consume the `provider_hints: map<string, any>` opacity contract (`interfaces.md`, cited) without importing or hard-coding any provider name, satisfying root `CLAUDE.md` rule 1 now even though ADR-0005's internal adapter pattern remains undecided. `EXE-P0.5.A` explicitly checks new Prompt Assembler code for provider-specific strings before every commit (Constraint, §18).

## 15. Agent Integration Strategy

Not applicable to this execution slice — none of the six in-scope P0 capabilities is agent-loop or multi-agent facing (that is `agent-optimization.md`'s domain, entirely P1+/P4). Recorded here only to confirm the scope boundary explicitly rather than silently omitting the section.

## 16. Proving Laboratory

Per the generation prompt's §4/§30, the first proving environment is a controlled reference agentic application with a tool-using agent, comparing:

```
Path A — Baseline:  Reference Application/Agent -> LLM
Path B — EAIOC:     Reference Application/Agent -> EAIOC -> LLM
```

This slice's six capabilities are exactly the substrate Path B needs before any optimization technique (P1+) can be meaningfully compared: Sanitizer/Context Policy/Prompt Assembler/Output Controls form the minimal request-transformation path; Token Accounting and the Benchmark Harness are what make Path A vs. Path B actually measurable (§17). Docker Compose (§6, row 10) is the proposed packaging for this reference environment — low-risk, reversible, no architecture conflict.

## 17. Baseline-vs-EAIOC Measurement

Per `architecture.md` §32.2's required comparison (cited, not redefined): input tokens, output tokens, total tokens, cached tokens, model calls, tool calls, cost, latency, quality, failure rate — captured by Capability 1 (ledger) and Capability 2 (`EvaluationRun`) for both Path A and Path B. At this slice's scope, Path B has no optimization stage active yet (P1 is out of scope), so the first meaningful comparison is Path B's *own added overhead* (Sanitizer/Context Policy/Prompt Assembler/Output Controls latency and token cost) against Path A's baseline — establishing the overhead floor every future optimization must net-exceed (`architecture.md` §27.3's Net Savings formula, cited). No fabricated savings figure is produced at this stage — there is nothing to save yet, only overhead to measure honestly.

---

## 18. P0 Execution Plan

### 18.1 Capability Lifecycle Model (cited, not redefined)

Per `implementation-plan.md` §6, restated for this document's atomic-unit numbering:

```
Capability
  A. Contract & Design
  B. Minimal Implementation
  C. Integration
  D. Observability
  E. Security/Governance
  F. Quality Validation
  G. Scenario Validation
  H. Failure/Recovery
  Capability Gate
```

Where a sub-phase is genuinely not applicable, this document states `NOT APPLICABLE — <reason>`, never a silent omission.

### 18.2 Atomic Unit Granularity — a compaction note, and the commit/approval boundary

**Every `EXE-P0.<n>.<letter>` unit below is a standalone, independently executable and independently reviewable step: one lifecycle sub-phase = one git commit = one explicit human-approval checkpoint, with no exception anywhere in this document.** Sub-phase A's design note is never committed together with Sub-phase B's implementation, nor is any pair of sub-phases ever combined into one commit or one approval round; `EXE-P0.<n>.B` (and every subsequent letter) does not begin until the prior sub-phase's own commit has been reviewed and explicitly approved. This applies uniformly across all six capabilities (§18.4–§18.9) and is restated, not weakened, by the field-compaction note below. Concretely: a Mode B instruction of "Execute P0.1" must be read as "Execute `EXE-P0.1.A`, then stop" — never as authorization to implement Sub-phases A through H (or the Capability Gate) of Capability 1 in one uninterrupted turn. Claude Code implements exactly the requested unit, reports evidence, and waits; it does not infer permission to continue to the next sub-phase or the next capability from a closed gate, a passing test, or the absence of an objection.

**The Capability Gate that follows Sub-phase H (§18.10) is not one of these `EXE-P0.<n>.<letter>` units** — it is a separate post-lifecycle review/promotion checkpoint, not an implementation sub-phase, and receives no `EXE-P0` ID and no implementation commit of its own (§18.10). Across the six capabilities this document therefore defines exactly **48** `EXE-P0.<n>.<letter>` atomic execution units (8 lifecycle sub-phases, A–H, per capability) plus **6** Capability Gates — **54 total gated checkpoints**, never described as "54 atomic execution units" anywhere in this document.

Every field the generation prompt's §10 requires (Objective, Scope, Sources, Requirements, Architecture components, Interfaces, Dependencies, Preconditions, Files/modules, Package responsibility, Data-model impact, API impact, Event impact, Configuration impact, Security impact, Observability impact, Tests, Negative tests, Benchmark requirements, Edge cases, Acceptance criteria, Definition of Done, Evidence, Rollback, Downstream impact, Blocking status, Source gaps, ADR dependency) is present below — but fields that do not vary sub-phase-to-sub-phase (Requirements covered, Architecture components, Interfaces, Dependencies, Security disposition, ADR dependency) are stated once per capability, in that capability's header block, rather than repeated verbatim across all nine of its sub-phase entries. Restating an identical field nine times per capability would pad this document's length without adding sequencing signal beyond what `implementation-plan.md` §8 already established — the same discipline that document itself applied to its own exemplar/sequencing-table split (§6, `implementation-plan.md`). Every sub-phase-varying field (deliverable, files, tests, benchmark requirement, edge cases, Definition of Done, evidence) is given individually, per sub-phase, below.

### 18.3 P0 Execution Order

Per `implementation-plan.md` §7/§8's dependency model, independently re-confirmed against `implementation-readiness-gate.md` §11's "First Implementable Slice":

```
Capability 1 — Token Accounting and Cost Ledger        (no prerequisite)
Capability 2 — Baseline Benchmark Harness + Quality Eval (no prerequisite)
Capability 3 — Sanitizer                                 (no prerequisite)
Capability 4 — Context Policy                             (requires Capability 1)
Capability 5 — Prompt Assembler                            (requires Capabilities 3, 4)
Capability 6 — Output Controls                             (requires Capability 1)
```

Capabilities 1, 2, and 3 have no cross-capability *prerequisite* — this is a dependency-model fact, not an execution-scheduling permission. Mode B's execution protocol is always strictly sequential regardless of dependency independence: one capability active at a time, one sub-phase active at a time, no parallel capability execution and no parallel sub-phase execution ever (§18.2). This document preserves `implementation-plan.md` §8's own listed order (1 → 2 → 3) as the deterministic sequence Mode B follows for these three capabilities, precisely because their dependency independence gives no other basis to pick an order — it does not authorize running them out of this order, interleaved, or concurrently.

---

### 18.4 Capability 1 — Token Accounting and Cost Ledger

| Field | Value |
|---|---|
| **Working label** | `EXE-P0.1` (session-local; not a new authoritative ID series) |
| **Objective** | Establish per-request token/cost recording, tenant-scoped from the first line of code, that every other capability and every later optimization technique writes to |
| **Sources** | `implementation-plan.md` §8.1; `architecture.md` §27.1–27.3; `interfaces.md` (ledger fields, cited); `implementation-readiness-gate.md` §10 (`READY`, no prerequisite) |
| **Requirements covered** | `OBJ-011`, `AC-001`, `AC-002`, `AC-005` |
| **Architecture components** | Token and Cost Accounting Ledger, `architecture.md` §27 |
| **Interfaces/contracts** | §27.1's ledger field categories (INPUT/OUTPUT/CACHE/MODEL/TOOLS/WORKFLOW/COST/PERFORMANCE/QUALITY); §27.3's cost model formulas — consumed as-is, no new schema |
| **Dependencies** | None (P0, no prerequisite) |
| **Preconditions** | §6's technology baseline confirmed (Java core); `core/interfaces/`/`core/schemas/`/`core/errors/` scaffolded (this capability is the first consumer, so its Sub-phase A also establishes the shared zero-dependency substrate) |
| **Package/module responsibility** | `control_plane/accounting/` (§9, direct match) |
| **Security/Governance disposition** | Not directly gated by any `GSP-NNN`, but `security.md`'s SGE (`GSP-001`) consumes this ledger's output — the ledger must exist before SGE's spend-governance logic has anything to evaluate |
| **ADR dependency** | None |
| **Downstream impact** | Every other capability in this slice, and every P1+ technique, depends on this ledger existing first |
| **Blocking status (per IRG)** | Non-blocking — unconditionally `READY` |

#### Sub-phase table

| Sub-phase | Deliverable | Files/Modules | Tests | Failure-Path Test | Benchmark Requirement | Edge Cases | Definition of Done | Expected Evidence |
|---|---|---|---|---|---|---|---|---|
| **A — Contract & Design** | Consume §27.1's ledger field list and §27.3's cost model as-is | `control_plane/accounting/ledger/LedgerEntry` (schema note, not yet code) | Schema-conformance review | N/A (design stage) | None yet | None directly | No new schema invented; every field traced to §27.1 | Design note citing §27.1/27.3 verbatim, committed on its own (`EXE-P0.1.A`, one sub-phase = one commit — §18.2, `EXE-P0.1.B` does not begin until this commit is reviewed and explicitly approved) |
| **B — Minimal Implementation** | Per-request token/cost recording, tenant-scoped from the first write | `control_plane/accounting/ledger/` | Unit tests: write, read-back, tenant-scoping enforcement | Unit test: write with missing `tenant_id` is rejected, not silently defaulted | None yet | `EC-077` (adversarial optimization-cost input) — cross-cutting, cited not re-derived here | Ledger write/read round-trips correctly; every record has `tenant_id` as first namespace component | Passing unit-test suite; `git diff` scoped to `control_plane/accounting/` only |
| **C — Integration** | Wire the ledger as the substrate every T0–T3 stage/technique will eventually write to (no other in-scope capability's Sub-phase C depends on this yet, since none is a technique that spends tokens) | `control_plane/accounting/` public write API | Integration test: a second in-scope capability (once built) can call the write API | N/A | None yet | None | Write API is stable and documented for other capabilities to call | Integration test passing against at least a stub caller |
| **D — Observability** | Cite `observability.md`'s already-named ledger-consuming metrics (`cost.net_savings`, `cost.baseline_estimated`, etc.) — do not invent a competing metric name | `control_plane/accounting/` emits structured log lines (interim sink, `SOURCE-GAP-EXECPLAN-01`, §38) | Log-output format check | N/A | None | None | Every ledger write emits one structured log line citing the existing metric names | Sample log output reviewed against `observability.md` §7's metric table |
| **E — Security/Governance** | Tenant isolation enforced structurally, not by convention alone | Same as B | Unit test: cross-tenant read is rejected | Unit test: cross-tenant read attempt returns `NOT_FOUND`/`FORBIDDEN`, never another tenant's data | None | None | No code path can construct a cross-tenant read | Passing isolation test |
| **F — Quality Validation** | `NOT APPLICABLE — a ledger has no quality dimension in `quality-gates.md`'s sense (§8.1 of `implementation-plan.md`, restated); correctness is verified by B/E's own tests, not a `QG-NNN` gate | — | — | — | — | — | — | — |
| **G — Scenario Validation** | `NO DEDICATED SCENARIO` verified — no `SCN-*` scenario isolates ledger construction itself (`implementation-plan.md` §8.1, restated); `SCN-CONC-001` is related but not identical (context mutation serialization, cited from `SCALING.md`'s own scope disposition) | — | — | — | — | — | — | — |
| **H — Failure/Recovery** | A ledger-write failure must never silently under-report cost (root `CLAUDE.md` rule 5) | Same as B | Unit test: simulated write failure results in `unverified` flag, excluded from any savings computation | Unit test: unverified record is excluded from a savings-sum query | None | None | Failure path never silently succeeds or fabricates a value | Passing failure-injection test |
| **Capability Gate** | See §18.10 for the shared gate-question format, answered per capability | — | — | — | — | — | — | — |

---

### 18.5 Capability 2 — Baseline Benchmark Harness + Quality Evaluation

| Field | Value |
|---|---|
| **Working label** | `EXE-P0.2` |
| **Objective** | Wire a P0-only path that can run an unoptimized request and record its `EvaluationRun` baseline fields; `run_optimized()` remains a no-op until P1 ships a technique to compare against |
| **Sources** | `implementation-plan.md` §8.2; `architecture.md` §32.1–32.3; `interfaces.md` §18 (`EvaluationFramework`, `EvaluationRun`, `EvaluationComparison`); `quality-gates.md` (`QG-NNN` methodology, cited); `eval.md` §16 (`EvaluationRun` required fields) |
| **Requirements covered** | `OBJ-012`, `AC-015`, `AC-017` |
| **Architecture components** | Benchmarking Framework (`architecture.md` §32), Quality Evaluation (folded in per `implementation-plan.md` §8.2's own rationale — two distinct §36 items, one shared implementation foundation) |
| **Interfaces/contracts** | `EvaluationFramework.run_baseline()`/`run_optimized()`/`compare()` (`interfaces.md` §18, `INTF-030`); `EvaluationRun`'s fields (`eval.md` §16, cited) |
| **Dependencies** | None (P0, no prerequisite) |
| **Preconditions** | `core/interfaces/`/`core/schemas/` exist (built during Capability 1, reused here) |
| **Package/module responsibility** | `control_plane/benchmarking/` + `control_plane/evaluation/` (§9, direct match) |
| **Security/Governance disposition** | The harness must run under the same tenant-isolation and authorization rules as a live request — a benchmark is not an exemption from governance (root `CLAUDE.md` rules 2/4) |
| **ADR dependency** | None |
| **Downstream impact** | Every P1–P4 technique's own validation-matrix gate (`conventions.md` §25) calls into this harness |
| **Blocking status (per IRG)** | Non-blocking — unconditionally `READY` (bundled treatment for both distinct §36 items) |

#### Sub-phase table

| Sub-phase | Deliverable | Files/Modules | Tests | Failure-Path Test | Benchmark Requirement | Edge Cases | Definition of Done | Expected Evidence |
|---|---|---|---|---|---|---|---|---|
| **A — Contract & Design** | Cite `EvaluationFramework`'s three defined schemas (`INTF-030`) and `quality-gates.md`'s methodology as-is; this capability is the first *consumer* of both, not a redefinition | `control_plane/evaluation/` schema notes | Schema-conformance review | N/A | None yet | None | No competing schema introduced; `RegressionReport`'s undefined status (`SOURCE-GAP-EVAL-01`) explicitly not resolved here — out of scope | Design note |
| **B — Minimal Implementation** | `run_baseline()` executes and records `EvaluationRun` baseline fields; `run_optimized()` is an explicit no-op, documented as such, not silently stubbed | `control_plane/benchmarking/`, `control_plane/evaluation/` | Unit test: `run_baseline()` produces a complete, correctly-typed `EvaluationRun` | Unit test: a failed baseline run does not produce a partial/malformed record | None yet — this capability *is* the benchmark substrate, nothing to benchmark against yet | None | `run_baseline()` round-trips correctly; `run_optimized()`'s no-op status is explicit in code and docs | Passing unit tests |
| **C — Integration** | Every future P1+ technique's validation-matrix gate will call into this harness — the shared substrate is built once | `control_plane/benchmarking/` public API | Integration test with a stub caller | N/A | None yet | None | Public API stable for future technique gates to call | Integration test passing |
| **D — Observability** | Cite `observability.md`'s existing quality/cost metrics — no competing name introduced | Structured log output (interim sink, `SOURCE-GAP-EXECPLAN-01`) | Log-format check | N/A | None | None | Every `run_baseline()` call emits one structured log line | Sample log output |
| **E — Security/Governance** | Harness runs under the same authorization/tenant rules as a live request | `control_plane/evaluation/` | Unit test: a benchmark run cannot bypass tenant scoping | Unit test: cross-tenant benchmark data leakage is impossible | None | None | No governance bypass path exists | Passing test |
| **F — Quality Validation** | This capability *is* the quality-validation substrate — cite `quality-gates.md`'s ten `QG-NNN` dimensions it must eventually be able to evaluate against, once P1+ techniques exist to measure | — | N/A at P0 scope — no technique exists yet to run a `QG-NNN` gate against | — | — | — | Documented readiness to accept `QG-NNN` evaluation calls once P1 ships | Design note cross-referencing `quality-gates.md` §5 |
| **G — Scenario Validation** | `NO DEDICATED SCENARIO` verified for harness construction itself (`implementation-plan.md` §8.2, restated); individual `SCN-QUAL-*`/`SCN-CMP-05x` scenarios validate specific *uses* of the harness once P1+ techniques exist | — | — | — | — | — | — | — |
| **H — Failure/Recovery** | A harness failure must never silently mark an unvalidated technique as validated — fail closed on the validation *claim*, even though the underlying optimization stage itself would fail open per root `CLAUDE.md` rule 2 | Same as B | Unit test: a harness failure results in "not validated," never a silent pass | Unit test: simulated `run_baseline()` failure does not produce a false-positive `EvaluationRun` | None | None | Failure never masquerades as a valid result | Passing failure-injection test |
| **Capability Gate** | §18.10 | — | — | — | — | — | — | — |

---

### 18.6 Capability 3 — Sanitizer

| Field | Value |
|---|---|
| **Working label** | `EXE-P0.3` |
| **Objective** | First content-safety touchpoint; every later stage assumes sanitized input exists |
| **Sources** | `implementation-plan.md` §8.3 (sequencing table row); `architecture.md` §11.1 (T1.1); `conventions.md` §3.1 (component ID `T1.1-SANITIZER`); `optimization-catalog.md`'s `TECH-001` schema (cited) |
| **Requirements covered** | `TECH-001` (P0, per `architecture.md` §36) |
| **Architecture components** | T1.1 — Sanitizer, `architecture.md` §11.1 |
| **Interfaces/contracts** | T1.1's normalization/token-count contract (`architecture.md` §11.1, cited) |
| **Dependencies** | None (P0, no prerequisite) |
| **Preconditions** | `core/interfaces/`/`core/schemas/` exist |
| **Package/module responsibility** | `control_plane/pipeline/t1_context/sanitizer/` (§9, direct match) |
| **Security/Governance disposition** | Sanitization failure is treated as a **security-adjacent** failure, not a pure optimization-stage failure, since it is the first content-safety gate — a sanitizer that cannot run must not silently pass unsanitized content downstream (fail-closed for the sanitization decision itself, distinct from — and stricter than — a later optimization stage's ordinary fail-open behavior) |
| **ADR dependency** | None |
| **Downstream impact** | Prompt Assembler (Capability 5) depends on Sanitizer having run |
| **Blocking status (per IRG)** | Non-blocking — unconditionally `READY` |

#### Sub-phase table

| Sub-phase | Deliverable | Files/Modules | Tests | Failure-Path Test | Benchmark Requirement | Edge Cases | Definition of Done | Expected Evidence |
|---|---|---|---|---|---|---|---|---|
| **A — Contract & Design** | Cite T1.1's normalization/token-count contract as-is | `control_plane/pipeline/t1_context/sanitizer/` schema notes | Schema-conformance review | N/A | None | None found directly (§43 honest finding) | No new schema invented | Design note |
| **B — Minimal Implementation** | Input normalization + token count | `control_plane/pipeline/t1_context/sanitizer/` | Unit tests: normalization correctness, token-count accuracy | Unit test: malformed input is rejected/flagged, not silently passed through | None yet | None | Normalized output + accurate token count for a representative input set | Passing unit tests |
| **C — Integration** | Wired as the first T1-series stage Prompt Assembler (Capability 5) will consume | `control_plane/pipeline/t1_context/sanitizer/` public API | Integration test with a stub downstream consumer | N/A | None | None | Public API stable for Capability 5 to call | Integration test passing |
| **D — Observability** | Cite `observability.md`'s existing stage-level metrics (`tokens.*` fields, §27.1) | Structured log output (interim sink) | Log-format check | N/A | None | None | One structured log line per sanitization call | Sample log output |
| **E — Security/Governance** | Sanitization failure fails closed for the sanitization decision itself (see capability-level disposition above) | Same as B | Unit test: a sanitizer-internal failure blocks downstream admission rather than passing raw input through | Unit test: simulated internal failure results in rejection, never silent pass-through | None | None | No path exists where a failed sanitization pass still reaches Prompt Assembler | Passing test |
| **F — Quality Validation** | `NOT APPLICABLE` at this sub-phase — no `QG-NNN` dimension gates raw normalization; correctness is verified by B's own tests | — | — | — | — | — | — | — |
| **G — Scenario Validation** | No dedicated `SCN-*` scenario was found isolating the Sanitizer specifically during this document's own targeted search of `edge-cases.md` — recorded as `NO DEDICATED SCENARIO`, honestly, not force-fit to `EC-057`/CIS-adjacent entries which govern *externally-sourced* content, a distinct concern from end-user input sanitization (`security.md` §12.2, cited: "already separately covered by the Sanitizer") | — | — | — | — | — | — | — |
| **H — Failure/Recovery** | Fail-closed on the sanitization decision itself (capability-level disposition, restated) | Same as B | Unit test above (E) doubles as this test | — | None | None | — | — |
| **Capability Gate** | §18.10 | — | — | — | — | — | — | — |

---

### 18.7 Capability 4 — Context Policy

| Field | Value |
|---|---|
| **Working label** | `EXE-P0.4` |
| **Objective** | Define the budget/policy substrate that T1-series consumers (and, in this slice, Prompt Assembler and Output Controls) read |
| **Sources** | `implementation-plan.md` §8.3 (sequencing table row); `architecture.md` §36 (named in the P0 list, no dedicated subsection — verified directly, §9's honest note); `conventions.md` §2.1's `policy/` package (INTF §20, cited) |
| **Requirements covered** | Cited only via the P0 item list (`architecture.md` §36) — no dedicated `OBJ`/`AC` traces to "Context Policy" by name in `requirements-traceability.md` beyond the general P0-substrate framing |
| **Architecture components** | None dedicated — this capability's shape is derived, not directly specified (§9's honest note, restated) |
| **Interfaces/contracts** | `interfaces.md` §20's `OptimizationPolicy` (cited, exact fields to be verified at Sub-phase A against the live file, not assumed here) |
| **Dependencies** | Token Accounting (Capability 1) — for budget enforcement |
| **Preconditions** | Capability 1's Capability Gate closed |
| **Package/module responsibility** | `control_plane/policy/context_policy/` (proposed, §9 — confirm at Sub-phase A) |
| **Security/Governance disposition** | Policy reads must never be overridden by an optimization decision — `security.md`'s precedence model (Security/Authorization/Policy above Optimization Benefit) applies even though this capability itself is not a `GSP-NNN` component |
| **ADR dependency** | None |
| **Downstream impact** | Prompt Assembler (Capability 5) depends on this capability |
| **Blocking status (per IRG)** | Non-blocking — unconditionally `READY` |

#### Sub-phase table

| Sub-phase | Deliverable | Files/Modules | Tests | Failure-Path Test | Benchmark Requirement | Edge Cases | Definition of Done | Expected Evidence |
|---|---|---|---|---|---|---|---|---|
| **A — Contract & Design** | Grep `interfaces.md` §20 directly for `OptimizationPolicy`'s exact fields before writing any code; confirm package placement (§9) | `control_plane/policy/context_policy/` schema notes | Schema-conformance review against the live `interfaces.md` file | N/A | None | None | Package location and schema fields verified, not assumed from this document's own citation | Design note quoting the verified `interfaces.md` §20 fields |
| **B — Minimal Implementation** | Policy read API, budget-enforcement hook consuming Capability 1's ledger | `control_plane/policy/context_policy/` | Unit tests: policy read correctness, budget-limit enforcement | Unit test: an unreadable/missing policy defaults to the most conservative configured behavior, never an unconstrained default | None yet | None | Policy substrate readable and budget-aware | Passing unit tests |
| **C — Integration** | Wired for Prompt Assembler (Capability 5) and Output Controls (Capability 6) to consume | Public read API | Integration test with a stub consumer | N/A | None | None | Public API stable | Integration test passing |
| **D — Observability** | Cite existing policy-adjacent metrics where named; otherwise structured log output only | Structured log output (interim sink) | Log-format check | N/A | None | None | One log line per policy resolution | Sample log output |
| **E — Security/Governance** | Policy is never overridden by an optimization signal (capability-level disposition) | Same as B | Unit test: an optimization-favorable signal cannot alter a policy decision | Unit test: forcing a favorable optimization signal does not change the policy outcome | None | None | No override path exists | Passing test |
| **F — Quality Validation** | `NOT APPLICABLE` — policy resolution has no `QG-NNN` dimension at this sub-phase; correctness is B's own test scope | — | — | — | — | — | — | — |
| **G — Scenario Validation** | `NO DEDICATED SCENARIO` — no `SCN-*` entry isolates "Context Policy" as this document's own targeted search found; recorded honestly | — | — | — | — | — | — | — |
| **H — Failure/Recovery** | An unreadable policy defaults conservatively, never permissively (fail-closed for the policy-resolution decision, consistent with §3's precedence discipline) | Same as B | Same as E's test | — | None | None | — | — |
| **Capability Gate** | §18.10 | — | — | — | — | — | — | — |

---

### 18.8 Capability 5 — Prompt Assembler

| Field | Value |
|---|---|
| **Working label** | `EXE-P0.5` |
| **Objective** | Assemble the final prompt other P1+ techniques will later modify/compress, with cache-stable prefixes and provider-neutral construction |
| **Sources** | `implementation-plan.md` §8.3 (sequencing table row); `architecture.md` §12.2 (Prompt Assembler); `security.md` (§2941-adjacent citation: "Section 12.2 Prompt Assembler preserves security instructions," `SEC-001`) |
| **Requirements covered** | `SEC-001` (preserve security instructions through assembly) |
| **Architecture components** | Prompt Assembler, `architecture.md` §12.2 |
| **Interfaces/contracts** | `provider_hints: map<string, any>` opacity contract (`interfaces.md`, cited); no provider-specific field permitted in any schema this capability touches (root `CLAUDE.md` rule 1) |
| **Dependencies** | Context Policy (Capability 4), Sanitizer (Capability 3) |
| **Preconditions** | Capabilities 3 and 4's Capability Gates closed |
| **Package/module responsibility** | `control_plane/prompt_assembler/` (proposed, §9 — confirm at Sub-phase A) |
| **Security/Governance disposition** | Must preserve security-classified instructions through assembly without exception (`SEC-001`, root `CLAUDE.md` rule 6) — this is a hard, non-negotiable invariant, not a configurable policy |
| **ADR dependency** | `ADR-0005` (provider-adapter pattern) cited only for the eventual internal adapter structure, not the fixed `provider_hints` contract this capability actually consumes — **moot for this capability's own build**, per `implementation-readiness-gate.md` §9's own finding |
| **Downstream impact** | None further in this slice (last request-path capability before the model call, which is out of scope) |
| **Blocking status (per IRG)** | Non-blocking — unconditionally `READY` |

#### Sub-phase table

| Sub-phase | Deliverable | Files/Modules | Tests | Failure-Path Test | Benchmark Requirement | Edge Cases | Definition of Done | Expected Evidence |
|---|---|---|---|---|---|---|---|---|
| **A — Contract & Design** | Cite §12.2's assembly contract and the `provider_hints` opacity rule as-is; confirm package placement (§9) | `control_plane/prompt_assembler/` schema notes | Schema-conformance review | N/A | None | None | No provider-specific field designed into any schema | Design note |
| **B — Minimal Implementation** | Assemble sanitized input + policy-bounded context into a final prompt, cache-stable-prefix-first (`architecture.md` §12.2's own stated intent, cited) | `control_plane/prompt_assembler/` | Unit tests: assembly correctness, security-instruction preservation, cache-stable-prefix ordering | Unit test: a missing/failed upstream input (Sanitizer or Context Policy) blocks assembly rather than assembling with a silently-defaulted gap | None yet | None | Assembled prompt correct for a representative input set; security instructions verifiably present in output | Passing unit tests |
| **C — Integration** | Wired to consume Capability 3's and Capability 4's public APIs | Integration test | Integration test with both real upstream capabilities (not stubs, since both already exist by this point in the sequence) | N/A | None | None | End-to-end assembly from sanitized input + policy through to final prompt | Integration test passing |
| **D — Observability** | Cite existing pipeline stage-level metrics | Structured log output (interim sink) | Log-format check | N/A | None | None | One log line per assembly call | Sample log output |
| **E — Security/Governance** | Security-classified content never dropped/reordered out of the assembled prompt (root `CLAUDE.md` rule 6, `SEC-001`) | Same as B | Unit test: a security-classified test fixture survives assembly verbatim, regardless of assembly-ordering optimization | Unit test: forcing a cache-stable-prefix reordering does not alter or drop the security-classified segment's content | None | None | No assembly path can drop or reorder-away a security-classified segment's content | Passing test |
| **F — Quality Validation** | `NOT APPLICABLE` at P0 scope — no `QG-NNN` dimension gates raw assembly yet (compression/semantic-equivalence concerns are P1+); correctness verified by B/E's own tests | — | — | — | — | — | — | — |
| **G — Scenario Validation** | `NO DEDICATED SCENARIO` for Prompt Assembler construction itself — this document's own targeted search found none; `SEC-001`'s own validating evidence (cited in `security.md`) concerns the general instruction-preservation invariant, not this capability's construction specifically | — | — | — | — | — | — | — |
| **H — Failure/Recovery** | An assembly failure falls back to the unoptimized/simplest correct assembly (ordering optimization is fail-open) but never drops a security-classified segment (fail-closed for that specific case, per E) | Same as B/E | Same tests as B/E cover this | — | None | None | — | — |
| **Capability Gate** | §18.10 | — | — | — | — | — | — | — |

---

### 18.9 Capability 6 — Output Controls

| Field | Value |
|---|---|
| **Working label** | `EXE-P0.6` |
| **Objective** | Symmetric to Prompt Assembler on the output side — enforce output schema/length controls before a response returns to the caller |
| **Sources** | `implementation-plan.md` §8.3 (sequencing table row); `architecture.md` §12.3 (Output Schema Selector), §12.4 (Output Length Controller) |
| **Requirements covered** | Cited via the P0 item list (`architecture.md` §36); `TECH-017`/`018` are the P1-tier full elaboration of output schema/length control — this capability's own P0 scope is the minimal enforcement substrate those later techniques build on |
| **Architecture components** | Output Schema Selector (§12.3), Output Length Controller (§12.4) |
| **Interfaces/contracts** | Output schema/length contract (`architecture.md` §12.3–12.4, cited) |
| **Dependencies** | Token Accounting (Capability 1) |
| **Preconditions** | Capability 1's Capability Gate closed |
| **Package/module responsibility** | `control_plane/output_controls/` (proposed, §9 — confirm at Sub-phase A) |
| **Security/Governance disposition** | Output validation is not itself a security gate, but malformed/unvalidated output must never be treated as if it passed schema validation (fail-closed on the *validation claim*, symmetric to Capability 2's Sub-phase H disposition) |
| **ADR dependency** | None |
| **Downstream impact** | None further in this slice |
| **Blocking status (per IRG)** | Non-blocking — unconditionally `READY` |

#### Sub-phase table

| Sub-phase | Deliverable | Files/Modules | Tests | Failure-Path Test | Benchmark Requirement | Edge Cases | Definition of Done | Expected Evidence |
|---|---|---|---|---|---|---|---|---|
| **A — Contract & Design** | Cite §12.3–12.4's output schema/length contract as-is; confirm package placement (§9) | `control_plane/output_controls/` schema notes | Schema-conformance review | N/A | None | None | No new schema invented | Design note |
| **B — Minimal Implementation** | Schema validation + length enforcement on a response | `control_plane/output_controls/` | Unit tests: schema-pass case, schema-fail case, length-truncation case | Unit test: a malformed response is rejected/flagged, never silently passed through as valid | None yet | None | Correct pass/fail/truncation behavior for a representative response set | Passing unit tests |
| **C — Integration** | Wired to consume Capability 1's ledger for cost/length accounting | Integration test with a stub or real upstream ledger | N/A | None | None | Public API stable | Integration test passing |
| **D — Observability** | Cite existing output-related metrics (token output fields, §27.1) | Structured log output (interim sink) | Log-format check | N/A | None | None | One log line per validation call | Sample log output |
| **E — Security/Governance** | No security-classified content is stripped by length truncation without an explicit, auditable rule (root `CLAUDE.md` rule 6, applied symmetrically to output as it is to input in Capability 5) | Same as B | Unit test: a security-classified segment in a test response is never silently truncated away | Unit test: forcing aggressive length control does not drop a security-classified segment | None | None | No truncation path drops security-classified content silently | Passing test |
| **F — Quality Validation** | `NOT APPLICABLE` at P0 scope — schema/length enforcement is deterministic, not a `QG-NNN`-gated judgment call at this sub-phase (full `QG-005` Schema Compliance elaboration is P1+'s `TECH-017`/`018` territory) | — | — | — | — | — | — | — |
| **G — Scenario Validation** | `NO DEDICATED SCENARIO` for this capability's P0 construction — `quality-gates.md` §5 itself records `QG-005` as having no dedicated `SCN-QUAL-*` scenario either, consistent with this finding | — | — | — | — | — | — | — |
| **H — Failure/Recovery** | A validation-infrastructure failure never defaults to "passed" (fail-closed on the validation claim, symmetric to Capability 2) | Same as B/E | Same tests cover this | — | None | None | — | — |
| **Capability Gate** | §18.10 | — | — | — | — | — | — | — |

---

### 18.10 Capability Gate Model (shared question set, answered per capability at Mode B execution time)

Per `implementation-plan.md` §18, restated as the exact question set every capability's Capability Gate must answer when Mode B reaches it — not pre-answered here, since the answers depend on actual Sub-phase B–H evidence Mode B produces:

| Question | Answered by |
|---|---|
| Entry Criteria — what must exist before implementation begins? | The capability's own Dependencies/Preconditions fields above |
| Implementation Completion — what must be built? | Sub-phase B |
| Integration Completion — what must integrate successfully? | Sub-phase C |
| Security/Governance Completion — what must be proven? | Sub-phase E |
| Quality Completion — what quality requirements must pass? | Sub-phase F |
| Scenario Completion — which scenarios validate it? | Sub-phase G |
| Failure/Recovery Completion — which failure modes must be validated? | Sub-phase H |
| Exit Criteria — what allows the next dependent capability to begin? | The next capability's own Dependencies field naming this one |
| Blocking Conditions — what prevents promotion? | Any Sub-phase E/F/G/H item stated but not yet satisfied at Mode B execution time |

A Capability Gate is a build/validation gate, never a claim of production readiness — restated here because it is the single most consequential distinction in this document, per `implementation-plan.md`'s own emphasis.

**The Capability Gate is a separate post-lifecycle review and promotion gate, not a lifecycle sub-phase.** Sub-phase H is the final implementation lifecycle sub-phase — it is independently verified, committed, and approved exactly like every other A–H sub-phase (§18.2). Only after that approval is the Capability Gate conducted: a review of the question set above against the evidence Sub-phases B–H actually produced. The Gate does not implement code, does not produce a new `EXE-P0.<n>.<letter>` unit, and receives no implementation commit of its own (§18.2, restated: 48 such units exist across the six capabilities, not 54). Explicit human approval of the Gate is nonetheless required, as its own distinct checkpoint, before Mode B may begin the next capability's Sub-phase A — passing H is necessary but not sufficient, and a closed gate is a promotion decision, not an automatic pass-through:

```
... → H → verify → commit → AWAITING APPROVAL (for H)
        → Capability Gate → review gate criteria → AWAITING APPROVAL (for the Gate; no commit)
        → next capability's Sub-phase A
```

If the Gate finds a Blocking Condition unmet, promotion to the next capability is denied until it is addressed — this is a promotion decision, not a code rollback (§37): every A–H commit remains individually revertible, but the Gate has no commit of its own to revert.

### 18.11 Claude Code Mode B Execution Command Protocol

This section makes §18.2's and §18.10's approval discipline operational: the exact commands a human operator issues to a Claude Code session, the exact response format Claude Code returns, and the exact state transitions permitted between them. It adds no new scope, technology, requirement, or sequencing content — it is a command-syntax and reporting-format clarification of the model already established above.

#### 18.11.1 Mode B is strictly user-command-driven

Mode B never begins or advances on its own. The user explicitly issues the command for every atomic unit; Claude Code never infers authorization for the next unit from anything that happened during the current one. In particular, none of the following — individually or in combination — constitute authorization to proceed: all tests passing, a clean `git status`, a successful commit, a closed Capability Gate, or the absence of an objection from the user. The only thing that authorizes starting an execution unit is an explicit user command naming that unit (§18.11.2, §18.11.4).

#### 18.11.2 Canonical execution command syntax

```
Execute EXE-P0.<n>.<letter>
```

Examples for Capability 1:

```
Execute EXE-P0.1.A
Execute EXE-P0.1.B
Execute EXE-P0.1.C
Execute EXE-P0.1.D
Execute EXE-P0.1.E
Execute EXE-P0.1.F
Execute EXE-P0.1.G
Execute EXE-P0.1.H
```

The same pattern applies unchanged for Capabilities 2 through 6 (`EXE-P0.2.A`…`EXE-P0.6.H`).

The existing shorthand rule (§18.2) is preserved exactly and restated here as a command-syntax matter: `Execute P0.1` means `Execute EXE-P0.1.A` — the first sub-phase only — and must never be read as authorization to implement Sub-phases A through H of Capability 1 in one turn. The canonical `EXE-P0.<n>.<letter>` form is preferred over the shorthand because it names exactly one atomic unit with no ambiguity; the shorthand remains valid but always resolves to the capability's next unexecuted `.A`-equivalent unit, never to "the whole capability."

#### 18.11.3 Per-unit execution lifecycle

```
USER
  |
  |  Execute EXE-P0.n.X
  v
CLAUDE CODE
  |-- Re-read the required source sections for this unit (§18.4–18.9's Sources fields)
  |-- Verify the prerequisite approval state (has the prior sub-phase, or the
  |     capability's own Dependencies/Preconditions, actually been approved?)
  |-- Execute ONLY EXE-P0.n.X — no other sub-phase, no other capability
  |-- Run the required verification/tests for this unit (§18.4–18.9's Tests/
  |     Failure-Path Test columns)
  |-- Inspect git diff/status to confirm scope
  |-- Create exactly ONE commit for this sub-phase
  |-- Report evidence per §18.11.5
  v
### AWAITING APPROVAL
  |
  |  (Claude Code stops here and takes no further action)
  |
USER
  |
  |  Approve EXE-P0.n.X  (§18.11.4)
  v
NEXT EXPLICIT Execute EXE-P0.n.<next-letter> command
```

Claude Code stops after exactly one atomic unit every time. It does not continue to the next letter automatically, and it does not perform the Capability Gate automatically after `H` is approved (§18.11.6).

#### 18.11.4 Canonical approval command syntax

```
Approve EXE-P0.<n>.<letter>
```

Examples: `Approve EXE-P0.1.A`, `Approve EXE-P0.1.B`, … `Approve EXE-P0.1.H`.

Approving `H` authorizes moving to that capability's Capability Gate review — it does **not** itself close the gate. The gate has its own separate approval command:

```
Approve CAPABILITY-GATE P0.<n>
```

Example: `Approve CAPABILITY-GATE P0.1`. This closes the promotion gate for Capability 1 and authorizes (but does not itself trigger) the next capability's Sub-phase A — the next implementation command is still the separate, explicit `Execute EXE-P0.2.A` (§18.11.6). Claude Code never infers this transition.

#### 18.11.5 Required Claude Code response format

For every executed `EXE-P0.n.<letter>` unit, Claude Code's report must contain:

**Execution Header**
```
EXECUTED: EXE-P0.n.X
CAPABILITY: <name>
SUB-PHASE: <name>
```

**Implementation / Design Summary** — what was changed for this atomic unit, and nothing beyond it.

**Source Traceability** — the exact source documents and section references consulted (drawn from that capability's Sources field, §18.4–18.9, plus this unit's own row).

**Verification** — the commands/tests/checks actually executed and their results (§18.4–18.9's Tests/Failure-Path Test/Benchmark Requirement columns for this sub-phase).

**Git Evidence** — changed files; `git diff --stat`; the relevant `git status --short`; the commit hash; the commit message.

**Scope Compliance** — an explicit confirmation that: only the requested atomic unit was executed; no later sub-phase was executed; no other capability was executed; no upstream document was modified.

**Approval Checkpoint** — the report ends with exactly:
```
### AWAITING APPROVAL
Approve EXE-P0.n.X to continue.
```
except for Sub-phase `H`, whose report ends instead with:
```
### AWAITING APPROVAL
Approve EXE-P0.n.H to proceed to the Capability Gate.
```
Claude Code does not perform the Capability Gate automatically after this.

#### 18.11.6 Capability Gate command protocol

After the user issues `Approve EXE-P0.n.H`, Claude Code may prepare and present the Capability Gate review — evaluating the question set already defined in §18.10 (Entry/Implementation/Integration/Security-Governance/Quality/Scenario/Failure-Recovery Completion, Exit Criteria, Blocking Conditions) against the evidence Sub-phases B–H actually produced — but must never treat the gate as automatically approved. The review is reported as:

```
CAPABILITY GATE REVIEW: P0.n
STATUS: PASS / BLOCKED
```

If blocked:

```
### CAPABILITY GATE BLOCKED
Reason:
Required remediation:
No next capability may begin.
```

If the user then issues `Approve CAPABILITY-GATE P0.n` (§18.11.4), Claude Code reports:

```
CAPABILITY GATE APPROVED: P0.n
NEXT AUTHORIZED UNIT: EXE-P0.(n+1).A
```

but still waits for the separate, explicit command `Execute EXE-P0.(n+1).A` before doing anything further — the gate-approval report is informational, not an execution trigger.

#### 18.11.7 Full P0 operator reference sequence

The following is a complete human/operator reference sequence covering every atomic unit and every Capability Gate across all six in-scope capabilities. **This sequence is a reference for what commands exist and in what order they become valid — it is not a script to paste into Claude Code as a batch.** Commands are issued one at a time, only after the preceding checkpoint has actually been reviewed and approved; nothing here overrides §18.11.1's user-command-driven rule.

```
Execute EXE-P0.1.A
Approve EXE-P0.1.A

Execute EXE-P0.1.B
Approve EXE-P0.1.B

Execute EXE-P0.1.C
Approve EXE-P0.1.C

Execute EXE-P0.1.D
Approve EXE-P0.1.D

Execute EXE-P0.1.E
Approve EXE-P0.1.E

Execute EXE-P0.1.F
Approve EXE-P0.1.F

Execute EXE-P0.1.G
Approve EXE-P0.1.G

Execute EXE-P0.1.H
Approve EXE-P0.1.H

Approve CAPABILITY-GATE P0.1

Execute EXE-P0.2.A
Approve EXE-P0.2.A
... (same A–H, Gate pattern through EXE-P0.2.H)
Approve CAPABILITY-GATE P0.2

Execute EXE-P0.3.A
... (same pattern through EXE-P0.3.H)
Approve CAPABILITY-GATE P0.3

Execute EXE-P0.4.A
... (same pattern through EXE-P0.4.H)
Approve CAPABILITY-GATE P0.4

Execute EXE-P0.5.A
... (same pattern through EXE-P0.5.H)
Approve CAPABILITY-GATE P0.5

Execute EXE-P0.6.A
... (same pattern through EXE-P0.6.H)
Approve CAPABILITY-GATE P0.6
```

#### 18.11.8 Command-state table

| State | Allowed next action |
|---|---|
| No active unit | User may issue `Execute EXE-P0.n.X` if that unit's own prerequisites are approved |
| Unit executing | Claude Code executes only that unit |
| Unit complete, awaiting approval | User must issue `Approve EXE-P0.n.X` before anything else can happen |
| `H` approved | Capability Gate review may occur (§18.11.6) |
| Gate awaiting approval | User must issue `Approve CAPABILITY-GATE P0.n` |
| Gate approved | User may issue the next capability's `Execute EXE-P0.(n+1).A` |
| Gate blocked | No next capability may start until the blocking condition is addressed and the gate is re-reviewed |

Claude Code never self-authorizes any transition in this table — every row's "allowed next action" column names a user command, never an automatic Claude Code action.

#### 18.11.9 Prohibited command interpretation

Claude Code must reject or stop on ambiguous natural-language instructions rather than guessing which unit(s) they mean, including but not limited to:

```
Implement P0.1
Complete Capability 1
Continue with P0
Finish P0.1
Implement everything ready
Proceed with the remaining steps
```

unless the user explicitly maps the request to one canonical `EXE-P0.<n>.<letter>` unit in the same instruction. None of these phrasings, on their own, authorizes executing more than one unit. The preferred operator command is always the canonical form (§18.11.2): `Execute EXE-P0.n.X`.

#### 18.11.10 Counts (restated, unchanged from §18.2)

6 capabilities × 8 lifecycle sub-phases (A–H) = **48** atomic `EXE-P0.<n>.<letter>` execution units, plus **6** Capability Gates (review/promotion checkpoints, no ID, no commit) = **54** total gated checkpoints. The Capability Gates are never described as atomic execution units.

#### 18.11.11 Commit rule (restated, unchanged from §18.2/§18.10)

```
One A–H lifecycle sub-phase = one implementation commit.
Capability Gate = no implementation commit (review/promotion checkpoint only).
```

#### 18.11.12 No-automatic-chaining rule

Claude Code must stop after the requested atomic unit even when all tests pass, the commit succeeds, no blocking condition is found, or the user previously approved the broader capability in general terms. No completion signal, passing gate, or successful command result is ever interpreted as permission to execute the next unit — the only valid authorization is the next explicit `Execute EXE-P0.<n>.<letter>` (or `Approve …`) command from the user (§18.11.1).

---

## 19. P1 Execution Plan

Not atomized into `EXE-P1.<n>.<letter>` units in this document — `implementation-readiness-gate.md` §26 explicitly makes no finding about P1's readiness ("P0 being clear to begin does not unlock P1"), and `conventions.md` §25's validation-matrix gate requirement for P1 cannot be satisfied until P0's Benchmark Harness (Capability 2 above) actually exists and has run. This document instead cites `implementation-plan.md` §9's own P1 sequencing (exemplar: Context Pruning `TECH-003`+`DA-001`; sequencing table for the remaining eight generic techniques; `DA-NNN` P1-equivalent disposition, §14 of that document) as the authoritative P1 plan, unmodified. A future `execution-plan.md` revision — or a fresh Mode A regeneration once P0's six capabilities have cleared their Capability Gates — is the appropriate place to atomize P1 into `EXE-P1.*` units, once P0's benchmark substrate exists to validate against.

## 20. P2 Execution Plan

Same disposition as §19 — cites `implementation-plan.md` §10 (Model Routing exemplar, remaining P2 sequencing table) unmodified. P2's own gate (`conventions.md` §25: validation matrix + quality baseline per model) additionally requires a per-model quality baseline that cannot exist before P0's harness and at least one P1 technique are validated.

## 21. P3 Execution Plan

Same disposition — cites `implementation-plan.md` §11 (sequencing table only, no full-lifecycle exemplar in that document either) unmodified, including that document's own honestly-recorded `SOURCE-GAP-IMPL-01` (three §36 P3 item names with no dedicated `TECH-NNN` ID).

## 22. P4 Execution Plan

Same disposition — cites `implementation-plan.md` §12 (Shadow/A-B/Continuous-Learning and Adaptive-Depth exemplars, remaining P4 sequencing table grouped by dependency cluster) unmodified. P4 items require accumulated P1–P3 validation history, per that document's own stated tier objective — nothing in this execution slice produces that history.

## 23. P5 Execution Plan

Same disposition — cites `implementation-plan.md` §13's own explicit ownership-boundary statement unmodified: P5 capability elaboration remains `inference-optimization.md`'s (its 11 `INFOPT-NNN` entries, detection/routing/negotiation/measurement only, never infrastructure implementation per H17/H18). This document adds no P5 content beyond restating that P5 requires an actual inference-serving layer to exist and be negotiable first — external to the Control Plane, out of scope for any Mode B session this document's P0 plan could trigger.

---

## 24. Security Implementation

None of the six in-scope P0 capabilities implements a `GSP-NNN` governance component itself (SGE/DGE/TMG/HAG/CIS remain entirely out of this slice's build scope — they are cross-cutting infrastructure `implementation-plan.md` §5's own principle 2 places outside the P0–P5 tiering). What this slice *does* implement is each capability's own Sub-phase E disposition (§18.4–18.9) consistent with `security.md`'s central invariant (§2 of that document, cited): no optimization or execution decision may weaken, bypass, or silently reinterpret a security requirement. Fail-open/fail-closed is stated explicitly per capability, never left implicit.

## 25. Tenant Isolation

Mandatory from Capability 1's first line of code (root `CLAUDE.md` rule 4; `implementation-plan.md` §8.1, restated: "there is no add-tenant-scoping-later step"). Verified per capability: Capability 1's ledger and Capability 2's `EvaluationRun` records are the two stateful surfaces in this slice and both carry `tenant_id` as the first namespace component (§12); Capabilities 3, 4, 5, 6 are stateless transformation stages that do not themselves persist tenant-keyed data but must not leak one tenant's context into another's request path — validated by each capability's own Sub-phase E test.

## 26. Observability Implementation

No full telemetry backend is stood up in this slice (§6, row 7; `SOURCE-GAP-EXECPLAN-01`, §38). Every capability's Sub-phase D uses a minimal interim sink — structured log output citing `observability.md`'s already-named metrics/fields — sufficient to satisfy each capability's own "cite, do not invent a competing metric name" discipline without requiring ADR-0004's eventual backend selection.

## 27. Benchmarking

Owned substantively by Capability 2 (§18.5). No other in-scope capability performs benchmarking itself; each is a *subject* the harness will eventually measure once P1+ techniques exist to compare against.

## 28. Quality Evaluation

No `QG-NNN` dimension is actively gated by any of the six in-scope capabilities at P0 scope — each capability's own Sub-phase F states `NOT APPLICABLE` with a specific reason (§18.4–18.9), consistent with `quality-gates.md`'s own scope: the ten quality dimensions gate *optimization techniques*, and P0's six capabilities are foundational substrate, not optimization techniques themselves (`implementation-plan.md` §8, restated).

## 29. Failure/Recovery/Reconciliation

Per capability, per §18.4–18.9's Sub-phase H entries: fail-open for ordinary optimization-adjacent behavior (e.g., Prompt Assembler's cache-stable-prefix ordering), fail-closed where continuing would bypass a mandatory constraint (ledger unverifiable-cost exclusion, Sanitizer's own internal-failure block, security-classified-content preservation in both Prompt Assembler and Output Controls, validation-claim integrity in Capabilities 2 and 6). No capability in this slice touches ESM/CVM/WVM reconciliation directly — that remains entirely out of scope (§46's Dynamic Execution components are not part of this execution slice).

## 30. Performance Engineering

No performance target is asserted as a production fact (root `CLAUDE.md` rule 5). §17's Baseline-vs-EAIOC measurement is the mechanism by which this slice's own added latency/token overhead becomes visible, once built — not before.

## 31. Testing Strategy

Per capability (§18.4–18.9): unit tests (correctness), failure-path/negative tests (the specific failure-mode column), and integration tests at Sub-phase C. No scenario-test or security-test infrastructure beyond each capability's own Sub-phase E/G entries exists in this slice — full scenario/security test suites (`scenario-matrix.md`-driven, `security.md`-driven) are P1+ territory once real optimization techniques exist to validate against. Contract validation against `interfaces.md`'s schemas happens at every capability's Sub-phase A. No CI platform is invented (ADR-0003 remains `PROPOSED`) — §6 row 12 records GitHub Actions as a pragmatic interim runner for these tests, not an ADR acceptance.

## 32. Docker/Compose

Proposed for the proving-lab reference environment (§16) only, not for any individual capability's own build — §6, row 10.

## 33. Kubernetes/Helm

Deferred past this execution slice — §6, row 11; Deployment Evolution Stage 4, restated from the brainstorm's own phasing (`docs/scratch/EAIOC_Tech_Stack_Brainstorming_Conversation.txt` §31).

## 34. CI/CD

Per §6 row 12 and §31: a pragmatic, low-risk, reversible interim GitHub Actions pipeline running each capability's own Sub-phase-level tests, explicitly not an ADR-0003 acceptance. `implementation-plan.md` §16's 8-step gate progression is cited as the eventual target shape; this slice's own six capabilities exercise steps 1–2 (static/contract validation, unit tests) fully, step 3 (security/governance tests) via each Sub-phase E, and steps 4–8 only partially or not at all (no `quality-gates.md`/`scenario-matrix.md`/`eval.md`-driven test suite exists yet for foundational, non-technique capabilities, consistent with §28's finding).

## 35. Configuration/Secrets

No secret-bearing configuration is introduced by any of the six in-scope capabilities — none calls a live provider, external service, or credential-bearing system in this slice (§6, rows 8/13; §14). Context Policy's configuration (Capability 4) is the one configuration-bearing surface in this slice and carries no secret material.

## 36. Data Migration/Versioning

Not applicable — no durable store exists yet in this slice (§6, rows 2–3; §12's in-memory reference stores). Migration/versioning becomes relevant only once ADR-0001/0002 are accepted and a real backing store is adopted, outside this document's scope to plan.

## 37. Rollback

Per capability, at the code level: each Sub-phase is one git commit (Mode B's own git discipline, cited from the companion execution prompt); a failed sub-phase's changes are reverted via ordinary git revert of that single commit, never a broader rollback, since no sub-phase's commit depends on an uncommitted state from a later sub-phase. At the data level: no durable data exists yet to roll back (§36). The Capability Gate itself has no implementation commit and therefore nothing to revert (§18.10) — a failed or blocked Gate means promotion to the next capability is denied until the blocking condition is addressed, not a code rollback; no special "gate commit" or "gate rollback" mechanism exists or is needed.

## 38. Source-Gap Register

This document's own scoped gap series, `SOURCE-GAP-EXECPLAN-NN`, distinct from every upstream/sibling document's own series:

| Gap ID | Description | Origin | Impact | Blocking? |
|---|---|---|---|---|
| `SOURCE-GAP-EXECPLAN-01` | No interim telemetry sink is documented anywhere upstream for any capability's Sub-phase D beyond "cite `observability.md`'s already-named metrics" — this mirrors `SOURCE-GAP-IRG-02`'s finding for the Observability P0 item itself, but applies to all six in-scope capabilities' own Sub-phase D, not only the Observability item | This document's own re-verification while writing §18/§26 | Non-blocking — this document proposes a minimal structured-log interim sink (§26) as a reasonable, low-risk default, consistent with `ADR-0001`'s own "interim reference implementation" pattern | No |
| `SOURCE-GAP-EXECPLAN-02` | Two of the six in-scope capabilities (Context Policy, Output Controls) have no exact package-leaf match in `conventions.md` §2.1's tree (§9) | This document's own module-boundary verification | Non-blocking — proposed placements given, explicitly flagged for confirmation at each capability's own Sub-phase A rather than assumed final | No |
| `SOURCE-GAP-EXECPLAN-03` | No dedicated `SCN-*` scenario was found isolating Sanitizer, Context Policy, Prompt Assembler, or Output Controls specifically, beyond the cross-cutting `EC-077`/security-adjacent citations already recorded elsewhere in the corpus | This document's own targeted search while writing §18 | Non-blocking — recorded honestly per capability (§18.6–18.9's Sub-phase G entries), not force-fit to an adjacent scenario | No |

**Disposition of gaps carried forward from other documents:** `SOURCE-GAP-IRG-02` (Observability interim path) — explicitly out of this slice's scope (§3), not resolved here. `SOURCE-GAP-OPTCAT-01`/`AGENTOPT-01` — not touched, since P1+ `DA-NNN`/session-phase content is out of scope.

## 39. ADR Dependency Gates

None of the six in-scope P0 capabilities is blocked by any ADR (independently re-confirmed against `implementation-readiness-gate.md` §9's own per-ADR interim-path table, consistent with §6 above):

| ADR | Touches this slice? | Blocking? | Disposition |
|---|---|---|---|
| `ADR-0001` (cache backing store) | No — no in-scope capability is a cache type | No | Not applicable to this slice |
| `ADR-0002` (execution-truth state store) | No — IRG §9 confirms moot for P0 | No | Not applicable |
| `ADR-0003` (CI/CD platform) | Loosely — gates actual pipeline *execution*, not any capability's design | No | Interim GitHub Actions runner proposed (§34), not an acceptance |
| `ADR-0004` (telemetry backend) | Loosely — Sub-phase D across all six capabilities | No | Interim structured-log sink proposed (§26, `SOURCE-GAP-EXECPLAN-01`) |
| `ADR-0005` (provider-adapter pattern) | Yes, but moot — Prompt Assembler (Capability 5) consumes only the fixed `provider_hints` contract, not the internal pattern | No | Confirmed moot per IRG §9's own finding |
| `ADR-0006` (`XEC` coordination) | No — no P0 item touches `XEC` (P4-only concern) | No | Not applicable |

**A new, non-ADR technology decision this document itself surfaces and records rather than silently assuming:** the Java/Spring Boot language choice (§6, row 1) is not one of the six named ADR candidates. It is recorded here as an explicit, user-supplied (via the technology-baseline brainstorm) decision, validated against the architecture and found non-conflicting — not an open item requiring further gating before Mode B may proceed.

## 40. Requirements Traceability

Per `requirements-traceability.md` (cited, independently re-verified counts, not recomputed): of the 138 total requirement IDs, this execution slice's six capabilities directly touch `OBJ-011`, `AC-001`, `AC-002`, `AC-005` (Capability 1), `OBJ-012`, `AC-015`, `AC-017` (Capability 2), `TECH-001` (Capability 3, via `optimization-catalog.md`'s P0 disposition), `SEC-001` (Capability 5). None of these is `PARTIALLY TRACED`/`BLOCKED BY SOURCE GAP`/`BLOCKED BY ADR` in `requirements-traceability.md`'s own §6 — all are `FULLY TRACED`. This document does not replace `requirements-traceability.md`'s own forward-trace matrix; it only confirms this slice's specific requirement set is unblocked.

## 41. Acceptance-Criteria Traceability

`AC-001`, `AC-002`, `AC-005` (Capability 1, ledger); `AC-015`, `AC-017` (Capability 2, benchmarking) — all `FULLY TRACED` per `requirements-traceability.md` §6.5, independently re-confirmed there, not recomputed here. No in-scope capability touches a `PARTIALLY TRACED` AC (`AC-035`, `AC-041`, `AC-046`, `AC-049` are all P1+/P4-tier concerns, outside this slice).

## 42. Edge-Case Coverage

| Edge Case | Relevance to this slice | Capability | Status |
|---|---|---|---|
| `EC-077` | Adversarial optimization-cost input — cross-cutting, applies to every optimization-stage capability's own overhead budget | All six (cited, not re-derived per capability) | Cross-cutting, cited from `implementation-plan.md` §22 |
| No dedicated EC found for Sanitizer, Context Policy, Prompt Assembler, or Output Controls specifically | — | Capabilities 3–6 | Recorded honestly as `SOURCE-GAP-EXECPLAN-03` (§38), not force-fit |

## 43. Scenario Coverage

Per `implementation-plan.md` §21's own finding, restated and independently re-confirmed for this slice specifically: no scenario domain across all 30 of `scenario-matrix.md`'s domains validates build sequencing or capability construction itself — this document, like `implementation-plan.md`, is a planning artifact describing construction order, not a runtime component a scenario could exercise. Individual capabilities' own Sub-phase G entries (§18.4–18.9) each honestly state `NO DEDICATED SCENARIO` where that is the verified finding, rather than fabricating coverage.

## 44. Security Coverage

Per §24, restated: no `GSP-NNN` component is built in this slice; each capability's own Sub-phase E is checked against `security.md`'s central invariant and fail-open/fail-closed discipline (§3 of that document, cited). No capability in this slice can bypass authorization, skip PII protection, bypass governance, violate tenant isolation, or disable a mandatory security stage — verified per capability's own Sub-phase E test (§18.4–18.9).

## 45. Scaling

Not applicable at this slice's scope — `SCALING.md`'s own honest disposition (cited): P0 implementation readiness is not equivalent to production-scaling readiness. None of `SCALING.md`'s eight `SC-NNN` surfaces requires action to close any of this slice's six Capability Gates. `SC-008` (observability/telemetry ingestion) is the only surface even loosely touched, via §26's interim log sink, and at this slice's scale carries no capacity-planning consequence worth elaborating.

## 46. Performance Exit Criteria

No numeric performance threshold is asserted (root `CLAUDE.md` rule 5; §30, restated). Each capability's Sub-phase B/C tests establish functional correctness; §17's Baseline-vs-EAIOC measurement is the mechanism that will eventually produce a real overhead figure, once all six capabilities are built and the proving-lab environment (§16) is stood up — not before, and not invented here.

## 47. Quality Gates

Per §28, restated: no `QG-NNN` dimension is actively gated by any in-scope capability. This section exists to confirm that finding explicitly per the master prompt's own required-section list, not to introduce new quality-gate content.

## 48. Release Sequencing

1. Capability 1 (Token Accounting) → 2. Capability 2 (Benchmark Harness + Quality Eval) → 3. Capability 3 (Sanitizer) — these three have no cross-capability *dependency* between them, but Mode B still executes them strictly one at a time in this fixed order, never in parallel or interleaved (§18.3) — → 4. Capability 4 (Context Policy, depends on 1) → 5. Capability 5 (Prompt Assembler, depends on 3 and 4) → 6. Capability 6 (Output Controls, depends on 1). Each capability's Capability Gate must close **and be explicitly approved by the user** before Mode B begins the next capability's Sub-phase A — a closed gate alone is not sufficient authorization to proceed (§18.2, §18.10, `implementation-plan.md` §7's dependency model, restated).

## 49. Risks

| Risk | Affected Capability | Mitigation |
|---|---|---|
| Package placement for Context Policy/Output Controls is proposed, not confirmed (§9) | 4, 6 | Confirm at each capability's own Sub-phase A before committing code |
| Interim telemetry sink may need revision once ADR-0004 is accepted | All six | Structured-log sink is deliberately minimal and swappable, not deeply embedded |
| Java/Spring Boot as sole P0 runtime may not suit Capability 2's eventual statistical needs (P1+) | 2 | Explicitly flagged as a judgment call open to revision at that capability's own gate (§6, row 5) |
| No dedicated scenario validates Sanitizer/Context Policy/Prompt Assembler/Output Controls individually | 3, 4, 5, 6 | Recorded honestly (`SOURCE-GAP-EXECPLAN-03`); unit/integration tests at Sub-phase B/C/E remain the primary correctness evidence for this slice |

## 50. Assumptions

A single backing technology decision (§6) can defer ADR-0001/0002/0004/0005 without blocking any of the six in-scope capabilities' own Capability Gates — verified, not merely assumed, against `implementation-readiness-gate.md` §9's own per-ADR table. No organization-specific traffic volume, cost figure, or performance number is assumed anywhere in this document (root `CLAUDE.md` rule 5).

## 51. Deferred Decisions

Final backing-store technology (ADR-0001/0002), CI/CD platform acceptance (ADR-0003), telemetry backend acceptance (ADR-0004), provider-adapter internal pattern (ADR-0005), `XEC` coordination mechanism (ADR-0006) — all remain `PROPOSED`, none decided by this document. Exact package placement for Context Policy and Output Controls (§9) — deferred to each capability's own Sub-phase A. P1–P5 atomic execution-unit decomposition (§19–23) — deferred to a future execution-plan revision once P0 clears.

## 52. Final Readiness Boundary

Per the generation prompt's §37, explicitly distinguished, none conflated:

```
PLAN READY            — this document, as of this generation
IMPLEMENTATION READY  — per implementation-readiness-gate.md: P0 tier startable, 6 of 8 items unconditionally clear
CODE COMPLETE         — not reached; no code exists yet
TEST COMPLETE         — not reached
BENCHMARK VALIDATED   — not reached; no local benchmark evidence exists anywhere in this repository
PRODUCTION READY      — not reached, not implied by any statement in this document
ENTERPRISE SCALE READY — not reached; explicitly out of scope (§45)
```

P0 completion, once Mode B finishes all six Capability Gates, does **not** automatically imply: production readiness, enterprise-scale readiness, P1–P5 readiness, any ADR's acceptance, final infrastructure selection, or proven business savings — restated verbatim from the generation prompt's own §37, since this is the single most consequential boundary statement in the entire execution-planning exercise.

---

## Final Report — Summary Sections (A–V)

### A. Technology Baseline Summary
Fourteen technology areas validated against the frozen architecture; zero conflicts found; one new, non-ADR, explicitly-recorded decision (Java/Spring Boot as the P0 language, §6/§39); one judgment call flagged as open to revision (Python deferral for Capability 2, §6 row 5). Full classification table: §6.

### B. Runtime Allocation Summary
All six in-scope capabilities run in the Java 25/Spring Boot 4.1.x core (§10). No polyglot split in this slice.

### C. Request-Critical Core Summary
All six capabilities classified Hot except Capability 2 (Warm at P0 scope, high future Python-extraction candidacy) — §11.

### D. Data Architecture Summary
Two stateful surfaces (Token Ledger, `EvaluationRun`), both tenant-scoped in-memory reference stores for this slice, gated toward a durable store only by ADR-0001/0002's eventual acceptance — §12.

### E. Deployment Evolution
Stage 1 (Docker Compose reference environment) is this slice's own target; Stages 2–6 (standalone service → containerized → Kubernetes/Helm → enterprise → embedded/SDK) are explicitly deferred — §16, §33.

### F. Polyglot Evolution
Phase 1 (Java core) is this slice's own scope; Phases 2–4 (Python, Go, specialized runtimes) are explicitly deferred, never treated as an objective in themselves — §10, §32 (brainstorm-cited).

### G. P0 Critical Path
Capability 1 → 2 → 3 (no cross-dependency among these three, but executed strictly sequentially, never in parallel — §18.2–§18.3) → 4 (needs 1) → 5 (needs 3, 4) → 6 (needs 1) — §18.3, §48.

### H. P0 Execution Sequence
Six capabilities, 8 lifecycle sub-phases (A–H) each = 48 atomic `EXE-P0.<n>.<letter>` execution units, plus 6 Capability Gates (review/promotion checkpoints — no ID, no commit) = 54 total gated checkpoints across the P0 tier — §18.4–18.10.

### I. First Proving Slice
Docker Compose reference environment; Path A (baseline) vs. Path B (through the six-capability EAIOC substrate); no optimization savings claimed yet, only overhead measured honestly — §16–17.

### J. ADR Dependency Map
Zero of six ADRs block any in-scope capability; two (0004, 0005) are loosely touched via interim/moot dispositions — §39.

### K. Source-Gap Implementation Register
Three new `SOURCE-GAP-EXECPLAN-NN` entries, all non-blocking — §38.

### L. Requirement Coverage Summary
Nine distinct requirement IDs directly touched (`OBJ-011/012`, `AC-001/002/005/015/017`, `SEC-001`, `TECH-001`), all `FULLY TRACED` per `requirements-traceability.md` — §40.

### M. Acceptance-Criteria Coverage Summary
Five ACs directly touched, all `FULLY TRACED`, none `PARTIALLY TRACED`/blocked — §41.

### N. Edge-Case Coverage
One cross-cutting edge case (`EC-077`); no dedicated edge case found for four of the six capabilities, recorded honestly — §42.

### O. Scenario Coverage
No scenario validates build sequencing itself (consistent with `implementation-plan.md`'s own finding); per-capability `NO DEDICATED SCENARIO` findings recorded honestly, not fabricated — §43.

### P. Security Coverage
No governance component built; every capability's own fail-open/fail-closed disposition checked against `security.md`'s invariants — §24, §44.

### Q. Performance/Benchmark Plan
No numeric target asserted; §17's measurement mechanism is the path to an honest first figure, once built — §30, §46.

### R. Known Open Questions
Package placement for two capabilities (§9); Capability 2's eventual runtime split (§6 row 5); interim telemetry-sink durability once ADR-0004 resolves (§38).

### S. Deferred Decisions
Five ADRs; exact package placement for two capabilities; P1–P5 atomic decomposition — §51.

### T. Implementation Risks
Four risks recorded with mitigations, none blocking — §49.

### U. Final Validation
Zero conflicts between the technology baseline and the frozen architecture; zero ADR blockers; zero requirement-traceability blockers for this slice's own requirement set; three new non-blocking source gaps recorded, not silently omitted.

### V. Readiness Boundary
PLAN READY (this document) ≠ IMPLEMENTATION READY (already true per `implementation-readiness-gate.md`, independently) ≠ CODE COMPLETE / TEST COMPLETE / BENCHMARK VALIDATED / PRODUCTION READY / ENTERPRISE SCALE READY (none reached, none implied) — §52.

---

## Execution Plan Readiness

Per this document's own required self-check: (1) the technology baseline was validated line-by-line against the frozen architecture and every open ADR, with zero conflicts found and every deferred technology explicitly classified (§6). (2) All six in-scope P0 capabilities were decomposed into atomic `EXE-P0.<n>.<letter>` units carrying every field the generation prompt's §10 requires, compacted at the capability level only where a field is genuinely sub-phase-invariant (§18.2, disclosed as a methodology choice, not a silent omission). (3) Module boundaries were checked against `conventions.md` §2.1 directly; two genuine gaps were found and recorded honestly rather than papered over (§9, `SOURCE-GAP-EXECPLAN-02`). (4) No ADR blocks this execution slice (§39, independently re-confirmed against `implementation-readiness-gate.md` §9's own table). (5) Requirements/acceptance-criteria traceability for this slice's specific requirement set is fully clear (§40–41). (6) Three new, non-blocking source gaps were recorded (§38) rather than invented around. (7) P1–P5 are explicitly not atomized, consistent with `implementation-readiness-gate.md` §26's own finding that P0 clearance does not unlock P1 (§19–23). (8) The readiness-boundary distinctions (§52) are stated explicitly, matching this project's own established discipline of never letting a planning artifact imply more than it has actually verified. (9) File-scope discipline was maintained — only `docs/execution-plan.md` was created this generation; no upstream, sibling, or ADR document was modified.

No blocking issue was found. This plan is ready for Mode B (gated implementation execution) to begin, one atomic unit at a time, under explicit human approval at every checkpoint.

**v1.0.1 surgical-correction self-check**, per `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.1.md`: (1) the Sub-phase A/B shared-commit contradiction in Capability 1 (§18.4) is removed — every sub-phase across all six capabilities now has its own standalone commit and approval checkpoint, restated explicitly at §18.2. (2) All "parallel-capable"/"any order" wording (§18.3, §48, §H) is corrected to distinguish dependency independence from execution scheduling — Mode B remains strictly sequential everywhere, with no exception. (3) The Capability Gate → next-capability boundary now states its own separate commit/approval requirement explicitly (§18.10), so a closed gate is never itself sufficient authorization to proceed. (4) The six-capability P0 scope, the validated technology baseline and its classification, all three `SOURCE-GAP-EXECPLAN-01/02/03` entries, the proposed (not source-defined) package placements for Context Policy/Prompt Assembler/Output Controls, and Capability 2's two-distinct-items/one-foundation identity are all unchanged. (5) All six ADRs remain `PROPOSED`; no ADR was accepted or closed. (6) File-scope discipline was maintained — `git status --short`/`git diff --stat`/`git diff -- docs/execution-plan.md` confirm only this file changed. (7) No source code, and no file under `control_plane/`, was created or modified — this correction is planning-document-only, exactly as required.

**v1.0.2 surgical-correction self-check**, per `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.2.md`: (1) the Capability Gate is now described exclusively as a post-lifecycle review/promotion checkpoint — never a ninth implementation sub-phase, never assigned an `EXE-P0.<n>.<letter>` ID, and never given its own implementation commit (§18.2, §18.10). (2) Every count reference is corrected: 8 lifecycle sub-phases (A–H) per capability × 6 capabilities = 48 atomic `EXE-P0.<n>.<letter>` execution units, plus 6 Capability Gates, equal 54 total gated checkpoints — never described as "54 atomic execution units" anywhere in this document (§18.2, §H, header). (3) Sub-phases A–H retain their own individual commit and approval boundary, unchanged from the v1.0.1 correction; the Gate retains its own explicit human-approval requirement, but never a commit (§18.10, §37). (4) Execution remains strictly sequential, per the v1.0.1 correction, unchanged. (5) Observability remains excluded from this execution slice (§3, §38, §41), unchanged. (6) P1–P5 remain non-executable/non-atomized in this document (§19–23), unchanged. (7) All six ADRs remain `PROPOSED` (§39), unchanged. (8) All three `SOURCE-GAP-EXECPLAN-01/02/03` entries are preserved unchanged (§38) — none resolved, none duplicated. (9) File-scope discipline was maintained — `git status --short`/`git diff --stat`/`git diff -- docs/execution-plan.md` confirm only this file changed during this correction; `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.1.md` and `...v1.0.2.md` were read but not modified. (10) No blocking issue remains; this plan is ready to freeze.

**v1.0.3 surgical-correction self-check**, per `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.3.md`: (1) a new §18.11 "Claude Code Mode B Execution Command Protocol" was added immediately after §18.10 and before §19, with no renumbering needed elsewhere since it is a subsection insertion, not a new top-level section. (2) It establishes: user-command-only authorization (§18.11.1); the canonical `Execute EXE-P0.<n>.<letter>` syntax with the preserved `Execute P0.1` shorthand rule (§18.11.2); the exact per-unit execution lifecycle diagram (§18.11.3); the canonical `Approve EXE-P0.<n>.<letter>` / `Approve CAPABILITY-GATE P0.<n>` approval syntax (§18.11.4); the required per-unit response format, including the H-specific approval-checkpoint variant (§18.11.5); the Capability Gate's own command protocol, including its `PASS`/`BLOCKED` review report (§18.11.6); the complete six-capability copy/paste operator reference sequence, explicitly labeled a reference not a batch script (§18.11.7); a command-state table (§18.11.8); a prohibited-ambiguous-command list (§18.11.9); the restated 48+6=54 count (§18.11.10); the restated one-sub-phase-one-commit/no-gate-commit rule (§18.11.11); and a strong no-automatic-chaining rule (§18.11.12). (3) `Execute EXE-P0.1.A`, `Approve EXE-P0.1.A`, and `Approve CAPABILITY-GATE P0.1` all appear explicitly, as required. (4) The full operator reference sequence runs through `Approve CAPABILITY-GATE P0.6`. (5) The document still states 48 atomic execution units + 6 Capability Gates = 54 total gated checkpoints everywhere the count appears (§18.2, §18.11.10, §H) — no regression to "54 atomic units." (6) The Capability Gate still receives no implementation commit anywhere in the new section (§18.11.6, §18.11.11), consistent with the v1.0.2 correction. (7) No source code, and no file under `control_plane/`, was created or changed. (8) No upstream document was changed — `git status --short`/`git diff --stat`/`git diff -- docs/execution-plan.md` confirm only this file changed; all three correction-prompt files (v1.0.1–v1.0.3, all pre-existing and untracked) were read but not modified. (9) The six-capability P0 scope, the validated technology baseline, all three `SOURCE-GAP-EXECPLAN-01/02/03` entries, all six ADRs' `PROPOSED` status, Observability's exclusion, and P1–P5's non-atomized status are all unchanged from the v1.0.2 baseline. No blocking issue remains; this plan is ready to freeze.

FINAL EXECUTION PLAN v1.0.3 CORRECTION COMPLETE — READY FOR FREEZE
