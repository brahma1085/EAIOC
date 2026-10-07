# Capability 6 — Output Controls: Contract & Design Note (EXE-P0.6.A)

**Status:** Design note. No Java code and no new schema are introduced by this sub-phase.
**Source of the row:** `docs/execution-plan.md` §18.9, Sub-phase A row ("Cite §12.3–12.4's output schema/length contract as-is; confirm package placement (§9)").
**Precondition check:** Capability 1 (Token Accounting and Cost Ledger) Capability Gate P0.1 is **APPROVED** (human approval recorded 2026-09-23) — the row's stated precondition ("Capability 1's Capability Gate closed") is satisfied. No other Capability Gate is a precondition for this capability (§18.9's own "Dependencies" row names only Capability 1).

## 1. What Capability 6 is

Capability 6 is "symmetric to Prompt Assembler on the output side — enforce output schema/length controls before a response returns to the caller" (`execution-plan.md` §18.9, "Objective"). Its declared dependency is Token Accounting (Capability 1), already Gate-closed; its declared downstream impact is "None further in this slice."

`architecture.md` groups it under the same `## 12. Component Specifications: T2, Assembler, Output Series` heading as the Prompt Assembler, as two separate subsections: `### 12.3 Output Schema Selector` and `### 12.4 Output Length Controller`. **Neither carries a `<LAYER>.<SEQUENCE>-<NAME>` component ID anywhere in the corpus** (verified by exhaustive grep across `architecture.md`, `conventions.md`, `optimization-catalog.md`, `implementation-plan.md`: no ID string resolves to either). The only `QV.{N}-{NAME}`-shaped string in the whole corpus is `conventions.md` §3.1's own illustrative example, `QV.1-SCHEMA-VALIDATOR` — it is never actually assigned to this or any other component anywhere else in the corpus, so it cannot be read as this capability's canonical ID. This is a pre-existing, honestly-disclosed documentation gap (the same shape as Capability 5's own missing component ID, `control_plane/prompt_assembler/PromptAssemblerContract.md` §1/§7.1), not something this sub-phase invents a filler for — see §7.

## 2. The contract this sub-phase cites (verified against the live files, not assumed)

### 2.1 `architecture.md` §12.3 — Output Schema Selector (verbatim)

> **Purpose:** Select output structure based on intent.
>
> | Intent | Output Structure |
> |---|---|
> | Factual | answer, confidence, source |
> | Comparative | comparison table, summary |
> | Procedural | ordered steps, bounded explanation |
> | Extraction | strict structured schema |
> | Coding | required code artifact, bounded explanation |
> | Agent/tool | machine-readable action/result structure |

### 2.2 `architecture.md` §12.4 — Output Length Controller (verbatim)

> **Purpose:** Apply field-level token budgets.
>
> **Examples:** Maximum explanation length; Maximum summary length; Maximum number of steps; Maximum number of returned records; Maximum justification length.
>
> **Controls where supported:** max output tokens; Structured output; JSON schema enforcement; Function calling; Grammar constraints; Stop sequences.
>
> **Truncation handling:** Prefer semantic boundaries; Mark truncation explicitly; Preserve machine-readable validity where possible; Permit controlled expansion if quality gate requires it.

Both subsections are reproduced identically in the Engineering Spec (`Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md` §7.18/§7.19) — verified directly, word-for-word. No field, type, or schema is invented here beyond what §12.3–12.4 state.

### 2.3 P0 scope vs. the P1-tier full elaboration (disambiguated, not conflated)

`architecture.md` §36's P0 item list names **"Output controls"** as one P0 item (line 2656); its P1 item list separately names **"Output schema/length control"** (line 2660) as a *different* item. `optimization-catalog.md` elaborates the latter as `TECH-017` (Output Schema Enforcement) and `TECH-018` (Output Length Control), both explicitly `Implementation Priority: P1 — High-Confidence Optimization`, `Maturity Level: 0 — RESEARCH`. `execution-plan.md` §18.9's own "Requirements covered" row already draws this distinction ("`TECH-017`/`018` are the P1-tier full elaboration... this capability's own P0 scope is the minimal enforcement substrate those later techniques build on") — this note confirms that distinction against the live sources rather than re-deciding it. `TECH-017`'s "Governing Component(s)" cell names `INTF-029 QualityValidator` as a supporting interface; `INTF-029` (`interfaces.md` §17) is a generic, cross-cutting original-vs-optimized-content validation interface used by *any* `QV.x`-class optimization stage — it is not itself a schema for this capability's own output, and this sub-phase does not adopt it as one.

### 2.4 Security/Governance disposition (restated for traceability; not implemented by this sub-phase)

`execution-plan.md` §18.9 states the capability's disposition directly: "Output validation is not itself a security gate, but malformed/unvalidated output must never be treated as if it passed schema validation (fail-closed on the *validation claim*, symmetric to Capability 2's Sub-phase H disposition)." Verified against the live Capability 2 row (`execution-plan.md` §18.5, Sub-phase H): "A harness failure must never silently mark an unvalidated technique as validated — fail closed on the validation *claim*" — the same shape, confirmed, not merely asserted. Separately, the E/H rows cite root `CLAUDE.md` rule 6 ("Security-classified content... is never pruned, compressed away, or deduplicated out, regardless of relevance score or budget pressure"), "applied symmetrically to output as it is to input in Capability 5." **Unlike Capability 5, this capability's E row does not cite `SEC-001` directly, and this note does not add that citation**: the Engineering Spec's own `SEC-001` traceability line (line 2585) traces it specifically to "Sec 7.17 Prompt Assembler preserves security instructions" — Capability 5's own section, not §7.18/§7.19. Root `CLAUDE.md` rule 6 is the correct, broader citation for this capability, exactly as the plan row already uses it; this note does not fabricate a `SEC-001` link the sources don't state. Realizing either disposition in code is Sub-phase E's/H's job, not A's.

## 3. Package placement (verified, not assumed)

`conventions.md` §2.1's top-level package tree (read in full) has **no leaf, bare or nested, matching either §12.3 or §12.4 specifically** — the closest neighboring leaves are `pipeline/t3_inference/` (router, cascade, reasoning_budget, none of which is an output-schema/length concern) and the already-realized `prompt_assembler/` (input-side, Capability 5). **Correction (caught by independent Verifier re-check, not self-found):** a bare substring match on "output" does exist — `model_control/output_forecaster/` — but it realizes **AR-003 Output Budget Forecasting** (`architecture.md` §19), a distinct Layer-3 model-control reasoning component that `TECH-018` merely consumes as a *prerequisite*, not the Output Schema Selector or Output Length Controller themselves (§12.3/§12.4). Nesting Capability 6 under `model_control/` would therefore be the wrong choice regardless of the string match; this does not change the package-placement conclusion below, only the precision of this claim. Neither §12.3's nor §12.4's own missing ID (§1 above) permits placement by ID-derived convention either.

`execution-plan.md` §9's own module-boundary table and §18.9's row both already propose `control_plane/output_controls/` ("proposed... to be confirmed at `EXE-P0.6.A`"), grouping both §12.3 and §12.4 under one package since `architecture.md` §36's P0 item list treats them as the single item "Output controls." §8's repository-structure diagram already lists this capability only as the placeholder comment `(output controls)` — it does not draw a nested or contradicting placement the way it once (erroneously) drew `prompt_assembler/` as a child of `policy/` (the `EXE-P0.5.A` finding corrected in `b42fdcd`); there is no §8-vs-§9 self-contradiction to correct here.

This sub-phase confirms the proposal:
- It is the smallest, non-colliding extension of the package tree — `control_plane/` currently contains `accounting/`, `core/`, `evaluation/`, `pipeline/`, `policy/`, `pom.xml`, `prompt_assembler/`, `src/`, `target/` (checked directly); no `output_controls/` exists yet, and no sibling package collides with it.
- Nesting it under `pipeline/` would require inventing a `pipeline/`-internal structure `conventions.md` does not define (neither component is a T0/T1/T2/T3-numbered stage per its own missing ID), so a new top-level leaf — matching the already-established pattern for `policy/`/`prompt_assembler/`/`accounting/` — is the correct, non-fabricating choice.
- Grouping both §12.3 and §12.4 under the single `output_controls/` package (rather than two separate packages) mirrors `architecture.md` §36's own single-item treatment and `execution-plan.md` §18.9's single sub-phase table covering both; this sub-phase does not split them into two packages, since nothing in the corpus names them as independently-scoped capabilities.

Design note and all future Capability 6 code live under `control_plane/output_controls/`, mirroring the package-per-capability pattern already used for Capabilities 3–5.

## 4. Dependencies and preconditions

- **Dependencies (declared, `execution-plan.md` §18.9):** Token Accounting (Capability 1). Capability Gate P0.1 is **APPROVED** (2026-09-23) — `control_plane.accounting.ledger` is consumed, never recreated, by any future Capability 6 sub-phase that needs it (e.g. for the "wired to consume Capability 1's ledger for cost/length accounting" deliverable named at Sub-phase C).
- **Precondition (declared):** Capability 1's Capability Gate closed. Satisfied.
- **Downstream impact (declared):** "None further in this slice." No Capability-7-or-later dependency is anticipated or implemented here, and this capability is the plan's last of the six P0 capabilities (`execution-plan.md` §18.3).
- **ADR dependency:** `execution-plan.md` §18.9 states "None" directly — no ADR gates this capability's build, and this sub-phase does not introduce one.

## 5. Scope boundary for this sub-phase

In scope for A: citing `architecture.md` §12.3–12.4's contract as-is; confirming package placement. Out of scope for A: any schema-validation or length-enforcement implementation, any test, any log line, any security/governance realization, any new schema (including a formal representation of "a response" to validate — see §7 item 2) — all of those are B, D, and E/H respectively, each requiring this already-open path to continue sub-phase by sub-phase (`execution-plan.md` §18.20.D), never a separate human `Execute` within this Capability.

## 6. Traceability

| Item | Reference |
|---|---|
| Capability 6 definition | `execution-plan.md` §18.9 |
| Output Schema Selector contract | `architecture.md` §12.3; Engineering Spec §7.18 (identical, verified) |
| Output Length Controller contract | `architecture.md` §12.4; Engineering Spec §7.19 (identical, verified) |
| P0-vs-P1 distinction | `architecture.md` §36 (P0 "Output controls" vs. P1 "Output schema/length control"); `optimization-catalog.md` `TECH-017`/`TECH-018` |
| Package placement precedent | `conventions.md` §2.1 (no literal leaf; new top-level package, matching `policy/`/`prompt_assembler/`'s own bare-leaf pattern); `execution-plan.md` §9, §18.9 |
| Dependency (Capability 1) | `accounting.ledger` (Gate P0.1 approved 2026-09-23) |
| Security/Governance disposition (restated, not implemented) | root `CLAUDE.md` rule 6; `execution-plan.md` §18.5 Sub-phase H (symmetry precedent) |
| `SEC-001` traceability (confirmed NOT applicable here) | Engineering Spec line 2585 (traced to §7.17 Prompt Assembler only) |

## 7. Open items (none blocks this design note)

1. **No component ID exists for the Output Schema Selector or the Output Length Controller** (§1 above) — `architecture.md` §12.3/§12.4 have no `<LAYER>.<SEQUENCE>-<NAME>` identifier. This is a pre-existing corpus gap, not invented or filled here; any future sub-phase's structured log line (`component_id=…`) must either cite a plain, non-numbered descriptive identifier — mirroring `PROMPT-ASSEMBLER`/`CONTEXT-POLICY`'s own precedent for capabilities with no dedicated architecture component ID — or raise this gap explicitly, not assume an ID.
2. **No schema anywhere in `interfaces.md` represents "a response" for this capability to validate or truncate.** Exhaustive grep confirms: no `ControlPlaneResponse` type exists in `core/schemas/`; `interfaces.md`'s only response-shaped candidate is `LLMInvocationResult` (§7.2, `content: string | ContentBlock[]`, the raw provider output flowing out of the LLM Provider Interface) — a plausible input to this capability's eventual validation/truncation logic, but not itself declared as this capability's own contract by any source. This is registered here as an open item for whichever future sub-phase (most likely B) first needs to decide what concrete type Output Controls operates on; no type is invented or adopted by this note.
3. `INTF-029 QualityValidator` (cited in §2.3 above) is a generic, cross-cutting validation interface shared by any `QV.x`-class stage — it is **not** adopted here as this capability's own schema, and a future sub-phase that wants to use it must say so explicitly, not assume this note already decided it.
4. No `SOURCE-GAP`/`CONTRA` ID is newly registered by this sub-phase: items 1–2 above are pre-existing conditions of the corpus this note discovers and discloses, not new contract deviations this unit introduces. Whether either merits a `SOURCE-GAP-EXECPLAN-NN` registration is left to whichever future sub-phase (B, most likely) first needs the missing information and cannot proceed without it.
5. **No §8-vs-§9 self-contradiction exists for this capability** (unlike the `EXE-P0.5.A` finding for Capability 5, corrected in `b42fdcd`) — checked directly: §8's diagram carries only the honest placeholder comment `(output controls)`, drawing no nested or conflicting placement to correct.
6. **A corrected, more precise package-tree claim** (caught by independent Verifier re-check, §3 above): `conventions.md` §2.1 does contain one leaf whose name bare-substring-matches "output" — `model_control/output_forecaster/` (AR-003 Output Budget Forecasting) — but it realizes a different component than either §12.3 or §12.4; this does not change the package-placement conclusion.
7. **Flagged for the first sub-phase that implements truncation behavior (likely B or E), not resolved here:** `optimization-catalog.md`'s own `TECH-017`/`TECH-018` traceability rows cite two directly on-point, HIGH-severity edge cases this note does not itself cite, since `execution-plan.md` §18.9's own Sub-phase A row scopes "Edge Cases: None" for this sub-phase (the same precedent `PromptAssemblerContract.md` follows) — `EC-050` ("Output Schema Truncates Required Information") and `EC-051` ("Output Length Control Cuts Code Mid-Statement"), both in `edge-cases.md`. Whichever sub-phase first implements truncation/schema-enforcement behavior must read these directly rather than relying on this note's summary.
