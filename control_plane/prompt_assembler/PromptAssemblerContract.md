# Capability 5 — Prompt Assembler: Contract & Design Note (EXE-P0.5.A)

**Status:** Design note. No Java code and no new schema are introduced by this sub-phase.
**Source of the row:** `docs/execution-plan.md` §18.8, Sub-phase A row ("Cite §12.2's assembly contract and the `provider_hints` opacity rule as-is; confirm package placement (§9)").
**Precondition check:** Capability 3 (Sanitizer) Capability Gate P0.3 is **APPROVED** (human approval recorded 2026-10-07) and Capability 4 (Context Policy) Capability Gate P0.4 is **APPROVED** (human approval recorded 2026-10-07) — the row's stated precondition ("Capabilities 3 and 4's Capability Gates closed") is satisfied.

## 1. What Capability 5 is

Capability 5 assembles the final prompt that later P1+ techniques will modify/compress, with cache-stable prefixes and provider-neutral construction (`execution-plan.md` §18.8, "Objective"). Its declared dependencies are Context Policy (Capability 4) and Sanitizer (Capability 3), both now Gate-closed; its declared downstream impact is "None further in this slice (last request-path capability before the model call, which is out of scope)."

`architecture.md` groups it under `## 12. Component Specifications: T2, Assembler, Output Series`, as `### 12.2 Prompt Assembler` — unlike its sibling `### 12.1 T2.1 — Query Compressor`, **the Prompt Assembler itself carries no `<LAYER>.<SEQUENCE>-<NAME>` component ID anywhere in the corpus** (verified by exhaustive grep across `architecture.md`, `conventions.md`, `optimization-catalog.md`, `implementation-plan.md`: no `T2.2`, no `PROMPT-ASSEMBLER` identifier exists). This is a pre-existing, honestly-disclosed documentation gap, not something this sub-phase invents a filler for — see §7.

## 2. The contract this sub-phase cites (verified against the live files, not assumed)

### 2.1 `architecture.md` §12.2 — Prompt Assembler (verbatim)

> **Purpose:** Build a final prompt that contains only the information needed for the current task.
>
> **Responsibilities:**
> - Select intent-specific instructions
> - Include only approved context
> - Preserve required security/policy instructions
> - Maintain cache-stable prefixes
> - Add dynamic content after stable prefixes where possible
> - Produce final token counts
> - Produce a complete assembly ledger
>
> **Explicit structural separation:**
>
> | Section | Contents |
> |---|---|
> | **CACHEABLE PREFIX** | System instructions, stable developer policy, tool definitions, stable examples, stable reference material |
> | **SEMI-STABLE REGION** | Project documentation, memory, slowly changing context |
> | **VOLATILE / DYNAMIC SUFFIX** | Current user request, current retrieved context, current tool results, current task state |

No field, type, or schema is invented here beyond what §12.2 states. "Produce a complete assembly ledger" is a stated responsibility with **no corresponding schema anywhere in `interfaces.md`** (verified by exhaustive grep: no `AssemblyLedger` type, no `INTF-NNN` cites §12.2) — registered as an open item, §7.

### 2.2 `provider_hints` opacity contract (`interfaces.md`, cited verbatim)

`interfaces.md` line 87's rule: "If a field only applies to one provider, it belongs in a `provider_hints` opaque map, not in the core schema." The concrete realization this capability's eventual output feeds toward is `interfaces.md` §7.2's `LLMInvocationRequest` (line 1054: `provider_hints: map<string, any> // Opaque; adapter-specific`) and `PromptCachePolicy` (line 1074–1078: `enable_cache`, `stable_prefix_end`, `provider_hints`) — both already-defined, Capability-5-adjacent schemas this capability's later sub-phases may need to populate, but **does not define or modify here**. `interfaces.md` line 3102 restates the same rule in its anti-pattern table: "Provider field in core schema | PROHIBITED | Use `provider_hints: map<string, any>`." Root `CLAUDE.md` rule 1 restates this identically ("provider-specific data goes in an opaque `provider_hints` map, never a first-class schema field") — this sub-phase does not design any schema, so there is nothing yet to check against this rule; it is cited here as the binding constraint future sub-phases (especially B) must honor.

### 2.3 `SEC-001` (requirement covered, cited verbatim)

Engineering Spec: `SEC-001`: "No optimization may bypass system/developer security instructions," traced explicitly to "Sec 7.17 Prompt Assembler preserves security instructions." Root `CLAUDE.md` rule 6 restates the governing invariant: "Security-classified content (SEC-protected instructions, PII policies, auth rules) is never pruned, compressed away, or deduplicated out, regardless of relevance score or budget pressure." Realizing this invariant in code (preserving security instructions through assembly) is Sub-phase B's job ("security-instruction preservation" — B's own Tests cell), not A's; this note restates the source so the eventual B test is traceable to the same requirement.

## 3. Package placement (verified, not assumed)

`conventions.md` §2.1's top-level package tree (read in full) has **no leaf, bare or nested, for "Prompt Assembler," "assembler," or anything under `pipeline/`** matching it — `pipeline/` lists only `t0_normalization/`, `t1_context/` (sanitizer, pruner, deduplicator, compressor, reorderer, budgeter), `t2_cache/` (exact_cache, semantic_cache), `t3_inference/` (router, cascade, reasoning_budget). The Prompt Assembler's own missing component ID (§1 above) means it cannot be placed by ID-derived convention either.

`execution-plan.md` §18.8's own row already proposes `control_plane/prompt_assembler/` ("proposed, §9 — confirm at Sub-phase A"), as a new top-level package sibling to the tree's existing bare, single-word leaves (`policy/`, `security/`, `accounting/`, `observability/`, `evaluation/`, `benchmarking/`, `deployment/` — none of which is nested under another package either). This sub-phase confirms that proposal:
- It is the smallest, non-colliding extension of the package tree — `control_plane/` currently contains `accounting/`, `core/`, `evaluation/`, `pipeline/`, `policy/`, `pom.xml`, `src/`, `target/` (checked directly); no `prompt_assembler/` exists yet, and no sibling package collides with it.
- Nesting it under `pipeline/` would require inventing a `pipeline/`-internal structure `conventions.md` does not define (the Prompt Assembler is not a T0/T1/T2/T3-numbered stage per its own missing ID), so a new top-level leaf — matching the already-established pattern for `policy/`/`accounting/`/etc. — is the correct, non-fabricating choice.

Design note and all future Capability 5 code live under `control_plane/prompt_assembler/`, mirroring the package-per-capability pattern already used for Capability 3 (`pipeline/t1_context/sanitizer/`) and Capability 4 (`policy/context_policy/`).

## 4. Dependencies and preconditions

- **Dependencies (declared, `execution-plan.md` §18.8):** Context Policy (Capability 4) and Sanitizer (Capability 3). Both Capability Gates are **APPROVED** (P0.3 and P0.4, both 2026-10-07) — `control_plane.pipeline.t1_context.sanitizer` and `control_plane.policy.context_policy` are consumed, never recreated, by any future Capability 5 sub-phase that needs them.
- **Precondition (declared):** Capabilities 3 and 4's Capability Gates closed. Satisfied.
- **Downstream impact (declared):** "None further in this slice (last request-path capability before the model call, which is out of scope)." No Capability 6-or-later dependency is anticipated or implemented here.
- **ADR dependency:** `ADR-0005` (provider-adapter implementation pattern) is cited by the plan row but is **moot for this capability's own build**, per `implementation-readiness-gate.md` §9's own finding (line 108/140/155): this capability depends only on the already-fixed `provider_hints` interface contract, not on ADR-0005's internal-pattern choice (which remains `PROPOSED`, not `ACCEPTED`). This sub-phase does not revisit that finding, only restates it for traceability.

## 5. Security/Governance disposition (restated for traceability; not implemented by this sub-phase)

`execution-plan.md` §18.8 states: "Must preserve security-classified instructions through assembly without exception (`SEC-001`, root `CLAUDE.md` rule 6) — this is a hard, non-negotiable invariant, not a configurable policy." This sub-phase does not implement assembly; realizing this invariant in code, with its own test ("security-instruction preservation"), is Sub-phase B's job. This note restates the source only so the contract and the eventual B test are traceable to the same requirement, mirroring the pattern used in Capability 4's own A note (§5) for its own deferred security disposition.

## 6. Scope boundary for this sub-phase

In scope for A: citing `architecture.md` §12.2's contract and the `provider_hints` opacity rule as-is; confirming package placement. Out of scope for A: an assembly implementation, any test, any log line, any security/governance realization, any new schema (including the undefined "assembly ledger") — all of those are B, D, and E respectively (or remain undefined pending a future human decision), each requiring its own `Execute` (per-unit before this capability's own continuous `§18.20` path opens on `Execute EXE-P0.5.A`; this command opens that path for the remainder of Capability 5 only, never before, never into Capability 6).

## 7. Open items (none blocks this design note)

1. **No component ID exists for the Prompt Assembler** (§1 above) — `architecture.md` §12.2 has no `<LAYER>.<SEQUENCE>-<NAME>` identifier, unlike its sibling §12.1 (`T2.1`). This is a pre-existing corpus gap, not invented or filled here; any future sub-phase's structured log line (`component_id=…`) must either cite a plain, non-numbered descriptive identifier — mirroring `ACCOUNTING-LEDGER`/`EVALUATION-BASELINE`/`CONTEXT-POLICY`'s own precedent for capabilities with no dedicated architecture component — or raise this gap explicitly, not assume an ID.
2. **"Produce a complete assembly ledger" (`architecture.md` §12.2) has no corresponding schema anywhere in `interfaces.md`** — confirmed by exhaustive grep (no `AssemblyLedger` type, no INTF section citing §12.2). Registered here as an open item for whichever sub-phase (likely B or D) first needs to represent assembly provenance; no schema is invented by this note.
3. `interfaces.md` §7.2's `LLMInvocationRequest`/`PromptCachePolicy` (cited in §2.2 above) are **not** this capability's own output schema — they belong to the LLM Provider Interface (§7), consumed later in the pipeline, out of scope here. A future sub-phase that needs Capability 5's own output schema must define or locate one explicitly, not assume these satisfy that need.
4. No `SOURCE-GAP`/`CONTRA` ID is newly registered by this sub-phase: items 1–2 above are pre-existing conditions of the corpus this note discovers and discloses, not new contract deviations this unit introduces. Whether either merits a `SOURCE-GAP-EXECPLAN-NN` registration is left to whichever future sub-phase (B, most likely) first needs the missing information and cannot proceed without it.
5. **`execution-plan.md` §8 and §9 self-contradict on this capability's own placement (Architect pre-flight finding).** §8's ASCII repository-structure diagram draws `prompt_assembler/` as a *child* of `policy/` ("nested here per §9's rationale"), but §9's own table (and the §18.8 capability row) both say "**top-level** placement," and the live `control_plane/` tree (confirmed directly: `accounting/, core/, evaluation/, pipeline/, policy/, pom.xml, prompt_assembler/, src/, target/`) already realizes it as a top-level sibling, not nested under `policy/`. This note follows §9/§18.8 and the realized directory (the more specific, and now factual, sources) — §8's diagram is stale/incorrect and is corrected in a separate documentation-only commit, not silently resolved here.

## 8. Traceability

| Item | Reference |
|---|---|
| Capability 5 definition | `execution-plan.md` §18.8 |
| Prompt Assembler contract | `architecture.md` §12.2 |
| `provider_hints` opacity rule | `interfaces.md` line 87, §7.2 (`LLMInvocationRequest`, `PromptCachePolicy`), line 3102; root `CLAUDE.md` rule 1 |
| `SEC-001` | Engineering Spec line 2057, 2585; root `CLAUDE.md` rule 6 |
| Package placement precedent | `conventions.md` §2.1 (no literal leaf; new top-level package, matching `policy/`/`accounting/`'s own bare-leaf pattern); `execution-plan.md` §18.8 |
| Dependencies (Capability 3, Capability 4) | `pipeline.t1_context.sanitizer` (Gate P0.3 approved 2026-10-07); `policy.context_policy` (Gate P0.4 approved 2026-10-07) |
| ADR dependency (moot) | `implementation-readiness-gate.md` §9 (lines 108, 140, 155) |
| Security/Governance disposition (restated, not implemented) | `SEC-001`; root `CLAUDE.md` rule 6 |
