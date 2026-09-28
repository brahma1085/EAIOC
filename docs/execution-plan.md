# Enterprise Agent & LLM Inference Optimization Control Plane — Execution Plan

**Document ID:** EAIOC-EXECPLAN-001
**Status:** PRE-IMPLEMENTATION — Level 0 (RESEARCH). This document plans execution; it does not itself constitute implementation, and its existence does not change the repository's maturity level.
**Version:** 1.1.1
**Generated:** 2026-09-23 (per repository date)
**Generation prompt:** `docs/prompts/execute-p0-foundation.prompt.md` (Master Prompt — Execution Plan Generation + Gated Implementation Execution, Mode A)
**Mode executed:** MODE A — Execution-Plan Generation. No source code was written in producing this document.
**Correction history:** v1.0.0 → v1.0.1 — surgical correction pass per `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.1.md`: removed a Sub-phase A/B shared-commit contradiction (§18.4) and all wording that could be misread as permitting parallel *execution* of Capabilities 1–3 in Mode B (§18.3, §48, §H below), while preserving the true dependency-independence fact those capabilities share. No requirement, scope, technology-baseline, or source-gap content was altered — see this document's own version-1.0.1 self-check at the end. **v1.0.1 → v1.0.2** — surgical correction pass per `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.2.md`: reclassified the Capability Gate as a post-lifecycle review/promotion checkpoint rather than a ninth implementation sub-phase (§18.10) — it receives its own explicit human approval but no implementation commit and no `EXE-P0.<n>.<letter>` ID — and corrected every dependent count reference from "54 atomic execution units" to "48 atomic `EXE-P0.<n>.<letter>` execution units + 6 Capability Gates = 54 total gated checkpoints" (§18.2, §H, and this header). No scope, technology-baseline, source-gap, or sequential-execution content was altered — see this document's own version-1.0.2 self-check at the end. **v1.0.2 → v1.0.3** — surgical correction pass per `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.3.md`: added a new §18.11 "Claude Code Mode B Execution Command Protocol" — the canonical `Execute EXE-P0.<n>.<letter>`/`Approve EXE-P0.<n>.<letter>`/`Approve CAPABILITY-GATE P0.<n>` command syntax, the required per-unit response/reporting format, a command-state table, a prohibited-ambiguous-command list, and the full six-capability copy/paste operator reference sequence. No scope, architecture, technology-baseline, requirements, source-gap, ADR, or sequencing content was changed — see this document's own version-1.0.3 self-check at the end. **v1.0.3 → v1.0.4** — surgical correction pass per `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.4.md`, addressing the shared-core-foundation ownership gap exposed by `EXE-P0.2.B` and strengthening Mode B approval into a mandatory two-stage control: Claude Code AI verification first, followed by explicit human approval. The correction assigns the shared `core/interfaces`, `core/schemas`, and `core/errors` foundation ownership to Capability 1 / `EXE-P0.1.A`, adds a post-hoc reconciliation mechanism for already-executed P0.1.A, adds explicit precondition guards for P0.2.B, defines AI verification checklists/reporting/state transitions, separates AI verification from human approval, adds AI Capability Gate verification before human Gate approval, clarifies N/A sub-phase handling, and records the historical `c3d6ecf` out-of-band documentation/editor-settings commit as non-implementation history. No upstream architecture, requirements, ADR, technology-baseline, or P0 capability scope is changed by this correction. New/changed sections: §18.2 (N/A commit exception), §18.4/§18.5/§18.6 (Preconditions and the affected A/B rows), §18.10 (two-layer Gate), §18.11.3/§18.11.5–§18.11.8/§18.11.11 (revised), new §18.12–§18.18, §37, §38 (`SOURCE-GAP-EXECPLAN-04`), §52, and this document's version-1.0.4 self-check at the end. **v1.0.4 → v1.0.5** — surgical correction per `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.5.md`, to eliminate remaining internal inconsistencies after post-v1.0.4 verification. Corrected the architecture-to-implementation mapping so only A–H are lifecycle sub-phases; corrected the §18.2 field-compaction wording from nine entries to eight lifecycle entries plus a separate Gate; retired ambiguous capability-level shorthand execution semantics in favor of the mandatory canonical `Execute EXE-P0.<n>.<letter>` syntax; corrected final validation/readiness language to recognize `SOURCE-GAP-EXECPLAN-04` as the current implementation-side blocker; and aligned the companion operator runbook to v1.0.5. No scope, architecture, requirements, technology baseline, ADR status, P0 capability definition, execution counts, remediation mechanism, or implementation code was changed. Sections touched: §1 (Version row), §7, §18.2, §18.11.2, §18.11.9, §52, Summary K/U, Execution Plan Readiness, and the new version-1.0.5 self-check at the end. **v1.0.5 → v1.0.6** — surgical source-and-plan correction per `docs/prompts/AC005-Contract-Correction-Prompt.md`, applying the human-approved AC-005 / `CostLedgerEntry` contract decisions D-1 to D-4. It records `CONTRA-EXECPLAN-01`: `INTF-047` did not represent the complete `architecture.md` §27.1 ledger model, a frozen-source contract inconsistency resolved by the documented precedence in `conventions.md`'s header. `interfaces.md` §28.1 and `conventions.md` §14.1 were corrected in the same pass. The plan now corrects Capability 1's Sub-phase B Definition of Done (§18.4) without claiming it was met historically by `e353dcf`; adds the reconciliation of `EXE-P0.1.B` (`RECONCILED: GAP FOUND`) and formally assigns the remediation units `REM-P0.1.B-01` (Contract / Design Reconciliation) and `REM-P0.1.B-02` (Minimal Implementation Remediation) in §18.14.4; and records the G/H approval-evidence finding (§18.14.3). It also refreshes the stale current position (§18.18, §52, Readiness) and adds `SOURCE-GAP-EXECPLAN-05`/`-06` (§38). No upstream scope, architecture, technology baseline, ADR status, P0 capability definition, execution counts, or implementation code changed. **v1.0.6 → v1.0.7** — documentation-only decision-promotion correction, **DB-2**. It promotes the explicit human-operator decisions HD-1 to HD-4, made during the approved `REM-P0.1.B-01` (`34dd1f1`), into `interfaces.md` §28.1 (22 former `SOURCE-UNRESOLVED` types resolved; OD-28.1-A/B closed) and `conventions.md` §14.1 (null/`unverified` rule), as §18.14.4 requires before `REM-P0.1.B-02`. It resolves `SOURCE-GAP-EXECPLAN-05`/`-06`; records DB-1, the approved placeholder rule, as unchanged; adds §18.14.5; and refreshes the current position. `REM-P0.1.B-02` remains NOT EXECUTED; Capability 1 Gate and `EXE-P0.2.B` remain BLOCKED. No historical record, `CONTRA-EXECPLAN-01`, count, or implementation code changed. **v1.0.7 → v1.0.8** — documentation-only correction recording the human-operator decisions HQ-1 to HQ-6 and GS-1 (2026-09-24), made after `EXE-P0.2.B` returned `AI VERIFICATION: BLOCKED` on INTF-030 design gaps at P0. It records the decisions (§18.14.6), adds P0 realization notes to `interfaces.md` §18 and a `run_baseline` row to §26.1 (contract text unchanged), registers `SOURCE-GAP-EXECPLAN-07`/`SOURCE-GAP-EXECPLAN-08`/`-09` (§38), annotates the §18.5 B row, and synchronizes the stale Capability Gate P0.1 state with the operator's 2026-09-23 approvals (§1, §18.14.3, §18.14.5, §18.18, §52, Readiness). `SOURCE-GAP-EVAL-01` remains open. `EXE-P0.2.B` remains BLOCKED on `SOURCE-GAP-EXECPLAN-07`/`SOURCE-GAP-EXECPLAN-08`. No source code, test, or historical commit changed. **v1.0.8 → v1.0.9** — documentation-only correction per the operator-supplied v1.0.9 surgical-correction prompt (provided in session; no copy exists under `docs/prompts/`): adds the **Execution Assistance Layer** (§18.19) — the repository's Claude Code skills and subagents as development-workflow assistance inside an authorized unit, never an EAIOC runtime/architecture layer and never an authorization mechanism. It inventories the current `.claude/` agents and skills, adds a routing matrix by capability and sub-phase, a per-unit pre-flight and fast-fail rule, a minimum-sufficient-assistance rule, and the specialist-evidence-versus-verdict rule; adds AI Verification dimension **K** (§18.12.1; J stays the final verdict, so no reference is renumbered) and report line 10 (§18.12.3); annotates the §18.5 B row's no-op wording per HQ-3 and Summary K's gap list. P0 scope, sequencing, unit IDs, counts, Capability Gate semantics, approvals, and all historical evidence are unchanged. `EXE-P0.2.B` remains BLOCKED on `SOURCE-GAP-EXECPLAN-07`/`-08`. **v1.0.9 → v1.0.10** — documentation/governance-only correction per the operator-supplied v1.0.10 surgical-correction prompt (provided in session; no copy exists under `docs/prompts/`). It formally registers the `EXE-P0.2.B` blocker remediation as **`REM-P0.2.B-01`** (§18.14.7), under the existing `REM-P0.<n>.<letter>-<NN>` convention. That section defines the unit's scope (decision areas D-A to D-D, none pre-decided), prerequisites, Definition of Done, review and verification, approval, and its explicit relationship to `SOURCE-GAP-EXECPLAN-07`/`-08`. It extends §18.14's remediation mechanism to this one pre-execution contract/design case. It links the unit in §18.5, §18.14.6, §18.18, §18.19.7, §38, §52, Summary H/K and the readiness conclusion, and synchronizes the operator runbook. `REM-P0.2.B-01` is REGISTERED / NOT STARTED; `SOURCE-GAP-EXECPLAN-07`/`-08` remain OPEN; `EXE-P0.2.B` remains BLOCKED. No contract, source code, test, count, unit ID, or historical record changed. **v1.0.10 → v1.0.11** — source-contract correction **DB-3**, per the operator-directed instruction of 2026-09-25 (given in-session; no copy under `docs/prompts/`). It promotes the operator's `REM-P0.2.B-01` decisions (D-A to D-D and the operator's decisions of 2026-09-25) into `interfaces.md` §18 (new `BaselineEvaluationRecord`; field mappings; retrieval and verified-only rule; no-measurement principle), resolves `SOURCE-GAP-EXECPLAN-07`/`-08` at source and narrows `-09` (additive status updates in §38; the register rows are retained unmodified), records `REM-P0.2.B-01` as executed (`e4cf1f5`), AI-verified and human-approved, registers `REM-P0.2.B-02` (prerequisite of `EXE-P0.2.B`) and `REM-P0.2.B-03` (not a prerequisite) in the new §18.14.8 with an explicit extension of §18.14's pre-execution mechanism, records the explicit deferral of live Path A measurement at Capability Gate P0.2 (§18.10), redefines `EXE-P0.2.B` as contract-level and fixture-based (§18.5), and registers `SOURCE-GAP-EXECPLAN-10` to `-15` (§38). Six contract values and decisions are recorded as human contract decisions of 2026-09-25, not source-derived: the record's initial `schema_version` (`1.0.0`), the field name `source_entry_id`, the carrying of `run_type`, the retrieval count basis (count-all-then-verify), the no-measurement outcome (`run_baseline()` fails without returning a result), and the retention of the `interfaces.md` document Version 1.2.0 with an explicit amendment-history entry. Changed under the operator's file authorization: `docs/interfaces.md`, `docs/eval.md`, `docs/implementation-plan.md`, `docs/execution-plan.md`, `docs/execution-plan-p0-steps.md`, root `CLAUDE.md` (governance state only). `SOURCE-GAP-EVAL-01` and `SOURCE-GAP-IRG-02` remain open. No source code, test, count or existing unit ID changed. **v1.0.11 → v1.0.12** — documentation-only state-refresh correction, synchronizing this document with `REM-P0.2.B-02`'s already-completed execution. It records `REM-P0.2.B-02` as executed (`bf74ac3`), AI-verified (`PASS`), and human-approved (2026-09-27), which clears that named precondition of `EXE-P0.2.B` (§18.14.8, §18.15); `EXE-P0.2.B` remains **not yet executable**, pending its own pre-flight (§18.19.3), a fresh §18.15 guard check against the now-existing retrieval realization, and a Code Architect re-assessment — none of which has run. It refreshes every §18.14.8/§18.18/§52/Summary-K status, readiness and current-position block that referenced `REM-P0.2.B-02`'s prior `REGISTERED / NOT EXECUTED` status, using the established superseding-block pattern (no v1.0.11 text rewritten in place), and updates the next-executable-unit statement from `REM-P0.2.B-02` to `EXE-P0.2.B` (subject to that unit's own pre-flight). No contract, interface, schema, unit definition, count, source-gap definition, or governance semantic changed; `REM-P0.2.B-03` remains registered, not executed, not a prerequisite. **v1.0.12 → v1.0.13** — documentation-only state-refresh correction, synchronizing this document with `EXE-P0.2.B`'s already-completed execution. It records `EXE-P0.2.B` (Sub-phase B — Minimal Implementation) as executed (`a2d022f`), AI-verified (`PASS`), and human-approved (2026-09-27), with the full 92-test suite passing and all three governance agents' reviews (Architect `APPROVED FOR IMPLEMENTATION`, Reviewer `PASS WITH REQUIRED FOLLOW-UP` — one LOW finding, fixed in the same commit — Verifier `CONFORMANT WITH DOCUMENTED GAPS`) recorded, including the disclosed, not concealed, finding that the Architect/Reviewer/Verifier routing ran after implementation rather than before (a documented deviation from §18.19.4/§18.19.5's normal sequence that produced no required design change). It records `EXE-P0.2.C` as NOT EXECUTED and NOT AUTHORIZED, and Capability Gate P0.2 as NOT AUTHORIZED — `Approve EXE-P0.2.B` approved only that one sub-phase and does not chain to either. It refreshes every §18.14.8/§18.18/§52/Summary-K status, readiness and current-position block that referenced `EXE-P0.2.B`'s prior `NOT YET EXECUTABLE` status, using the established superseding-block pattern (no v1.0.12 text rewritten in place), and updates the next-executable-unit statement from `EXE-P0.2.B` to `EXE-P0.2.C`. No contract, interface, schema, unit definition, count, source-gap definition, or governance semantic changed; the documented Architect/Reviewer/Verifier ordering deviation is preserved as historical evidence, not rewritten or concealed. **v1.0.13 → v1.1.0** — **structural execution-authorization-model amendment, not a routine state refresh.** Adopted from the human decision register HD-CE-01 through HD-CE-16 (frozen 2026-09-28, following a dedicated read-only governance-redesign analysis and decision-collection process conducted outside this document). Adds new §18.20 "Continuous Phase-Driven Execution Model" — the authoritative source for continuous execution within an explicitly human-authorized Capability path: "phase" = Capability (§18.20.A); one `Execute <ID>` opens the path for the remaining sub-phases of that unit's Capability through its own Capability Gate, never another Capability (§18.20.B–C); path-internal units continue automatically after a PASS-class AI Verification and their own commit, reporting the new `### AI VERIFIED — CONTINUING` footer/state — never `HUMAN APPROVED`, never itself authorizing anything outside the path (§18.20.D–F, §18.12.4, §18.17); human `Approve …` is required only at the path's boundary, which must explicitly enumerate every unit it covers (§18.20.G); a resolved Human Decision Request auto-resumes the same path, never expanding it (§18.20.H); a prerequisite/remediation unit may execute automatically only when this plan explicitly designates that exact unit as path-internal — `REM-P0.2.B-03` is classified `Optional`, not path-internal (§18.20.I–J); eleven enumerated conditions unconditionally stop a path (§18.20.K); commit granularity is unchanged — one commit per changing unit, never merged (§18.20.L); the Capability boundary is absolute — no authorization ever crosses into the next Capability (§18.20.M); cross-session reconstruction uses repository evidence plus a runbook §20 authorized-path field to be added in a later, separate pass (§18.20.N); the model applies prospectively only, effective from `EXE-P0.2.C` — `EXE-P0.2.A`/`EXE-P0.2.B` remain historical under the prior per-unit model, never relabeled (§18.20.O); and "NO UNAUTHORIZED CHAINING" is established as the one authoritative replacement for the historical "no automatic chaining" principle (§18.20.P), which is retained verbatim at §18.11.12 and annotated `SUPERSEDED BY §18.20` rather than deleted. Cross-references qualifying, not rewriting, the pre-existing model are added at §18.2, §18.10, §18.11.1, §18.11.2, §18.11.3, §18.11.5, §18.11.6, §18.11.7, §18.11.12, §18.12.1, §18.12.4, §18.17, and §18.19.1. This amendment does not modify `.claude/agents/eaioc-p0-execution-orchestrator.md`, `.claude/skills/eaioc-execution-governance/SKILL.md`, `docs/execution-plan-p0-steps.md`, or root `CLAUDE.md` — those artifacts' own required amendments are explicitly deferred to separate, later, explicitly-directed correction passes (§18.20.Q), and until each is made, the orchestrator and skill continue to implement only the pre-existing per-unit model. No requirement, architecture, interface, schema, contract, unit definition, count (48 units + 6 Gates = 54 checkpoints, unchanged), source-gap definition, or historical execution record is changed by this amendment. **v1.1.0 → v1.1.1** — **structural governance correction, not a new decision round and not implementation or a state-changing execution.** Implements the human decision register HD-CE-17, HD-CE-18, and HD-CE-19 (frozen after v1.1.0, following a dedicated read-only follow-up audit and decision-collection process conducted outside this document), and fixes three drafting omissions in the v1.1.0 pass (§18.11.8, §18.12.5, §18.12.6 each lacked the qualifying cross-reference every other command-protocol section received). Adds the new canonical footer/state `### AI VERIFIED — CONTINUING (NOT APPLICABLE)` (§18.12.4) — distinct from ordinary `### AI VERIFIED — CONTINUING`, which remains PASS-class/committed-only — for a path-internal `NOT APPLICABLE` sub-phase: the AI Verification verdict stays `NOT APPLICABLE`; no implementation commit exists or is required; no individual human `Approve` is required; the unit counts as completed for path progression; it is never `HUMAN APPROVED`; it never authorizes anything outside the path; it is not a fifth AI Verification verdict (HD-CE-17). Corrects §18.2/§18.16's previously-unqualified N/A human-approval requirement to state the path-internal exception explicitly. Corrects §18.15's completion-guard semantics (the literal "Capability `<n>`.A has been approved" check): within the same §18.20 authorized path, that completion-only condition is now satisfied by `HUMAN APPROVED` (historical), `AI VERIFIED — CONTINUING` (continuous, PASS-class), or `AI VERIFIED — CONTINUING (NOT APPLICABLE)` (continuous, N/A) — explicitly, in every case, never satisfying human approval, human authorization, Capability Gate approval, boundary approval, or any guard whose purpose specifically requires human review/authorization (HD-CE-18, HD-CE-19); §18.15's existing generalization to later capabilities' own Sub-phase B (at minimum `EXE-P0.3.B`) is preserved and now carries the corrected semantics automatically, with no separate edit needed for Capability 3 onward, and no structurally equivalent guard was found elsewhere (Capabilities 4–6's own preconditions check Capability Gate closure only, already unaffected). Adds a parallel N/A-continuation branch to §18.17's state model, alongside the existing `CONTINUING` branch, both explicitly stated to never satisfy a `HUMAN APPROVED` condition while either may satisfy a completion-only guard within the same path. Corrects §18.18's v1.1.0 worked sequence, whose Sub-phase F step incorrectly resolved to ordinary `CONTINUING` despite Capability 2's F row being `NOT APPLICABLE` — it now resolves to `AI VERIFIED — CONTINUING (NOT APPLICABLE)`. Extends §18.20 with new subsections R (N/A continuation) and S (completion-guard semantics), and amends §18.20.G (boundary-approval enumeration must include N/A-continuation units, never retroactively read as their individual approval) and §18.20.N (cross-session reconstruction recognizes the new N/A state as valid path-completion evidence). This correction does not execute, verify, or approve any unit; `EXE-P0.2.C` remains, as a repository-state fact, `NOT EXECUTED, NOT AUTHORIZED`; the orchestrator, governance skill, `CLAUDE.md`, and runbook remain unmodified and unamended, so continuous execution — now including its corrected N/A and completion-guard semantics — is still not yet operative in practice (§18.20.Q, unchanged).

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
| Status | Mode A (planning) output. Mode B had not begun when this document was first generated; as of v1.0.4 it has begun under the gated protocol (§18.11) — Capability 1 A–E/G/H committed (F `NOT APPLICABLE`, no commit), `EXE-P0.2.A` committed, `EXE-P0.2.B` stopped on an unmet precondition (§18.13–§18.15). **As of v1.0.6:** `REM-P0.1.A-01`/`-02` done and approved (`c05e3e7`, `93c1ba3`, `5e12148`); `EXE-P0.1.F` acknowledged as N/A; Capability Gate P0.1 **BLOCKED** on `CONTRA-EXECPLAN-01` (AC-005 / `INTF-047`), pending `REM-P0.1.B-01`/`-02` (§18.14.4, §18.18). **As of v1.0.8:** `REM-P0.1.B-02` done and approved (`c378dc9`); `EXE-P0.1.G`/`H` approved; Capability Gate P0.1 **APPROVED** (2026-09-23) — Capability 1 closed; `EXE-P0.2.B` **BLOCKED** on `SOURCE-GAP-EXECPLAN-07`/`SOURCE-GAP-EXECPLAN-08` (§18.14.6). **As of v1.0.9:** status unchanged; Execution Assistance Layer added (§18.19). **As of v1.0.10:** remediation unit `REM-P0.2.B-01` registered, not started (§18.14.7); `SOURCE-GAP-EXECPLAN-07`/`-08` open; `EXE-P0.2.B` **BLOCKED**. **As of v1.0.11:** `REM-P0.2.B-01` executed (`e4cf1f5`), AI-verified and human-approved (2026-09-24); its decisions are promoted (DB-3, §18.14.8); `SOURCE-GAP-EXECPLAN-07`/`-08` resolved at source, `-09` narrowed (§38 status updates); `REM-P0.2.B-02` and `REM-P0.2.B-03` registered, not executed; `EXE-P0.2.B` not yet executable (requires `REM-P0.2.B-02`). **As of v1.0.12:** `REM-P0.2.B-02` executed (`bf74ac3`), AI-verified (`PASS`) and human-approved (2026-09-27); `EXE-P0.2.B`'s named precondition is cleared. **As of v1.0.13:** `EXE-P0.2.B` executed (`a2d022f`), AI-verified (`PASS`) and human-approved (2026-09-27), 92/92 tests; `EXE-P0.2.C` and Capability Gate P0.2 **NOT AUTHORIZED**. **As of v1.1.0:** new §18.20 "Continuous Phase-Driven Execution Model" adopted — a **structural execution-authorization amendment**, not a state refresh (see Correction History above); effective prospectively from `EXE-P0.2.C` onward; `EXE-P0.2.A`/`EXE-P0.2.B` remain historical under the prior per-unit model. No unit executed, verified, or approved by this amendment itself; `EXE-P0.2.C` remains **NOT EXECUTED**. **As of v1.1.1:** HD-CE-17/18/19 implemented — the new `### AI VERIFIED — CONTINUING (NOT APPLICABLE)` footer/state (§18.12.4), corrected §18.15 completion-guard semantics, and the three v1.1.0 drafting omissions (§18.11.8, §18.12.5, §18.12.6) fixed — a **further structural governance correction, not a new decision round, not implementation, and not a state-changing execution**. `EXE-P0.2.C` remains **NOT EXECUTED, NOT AUTHORIZED**; the orchestrator, governance skill, `CLAUDE.md`, and runbook remain unmodified, so continuous execution (including its now-corrected N/A/completion-guard semantics) is still not yet operative in practice. |
| Version | 1.1.1 (surgical correction — see Correction History above; the `1.0.11` value previously recorded in this row was stale relative to the header's `1.0.13` and was corrected at v1.1.0 as an incidental fix, not a frozen HD-CE decision) |
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
| Capability Lifecycle Model (`implementation-plan.md` §6) | `implementation-plan.md` | Every `EXE-P0.<n>.<letter>` unit in §18 below maps 1:1 to one lifecycle sub-phase A–H. The Capability Gate is a separate post-lifecycle review/promotion checkpoint and is not an `EXE-P0.<n>.<letter>` execution unit. |
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
| `EvaluationRun` / `BaselineEvaluationRecord` records (Capability 2) | In-memory reference store for P0 | `tenant_id`-scoped (`BaselineEvaluationRecord` carries `tenant_id`; `EvaluationRun` does not — `SOURCE-GAP-EXECPLAN-09`, narrowed) | `run_id`/`timestamp` per `interfaces.md` §18; `BaselineEvaluationRecord` carries `schema_version` (`1.0.0`, a human contract decision of 2026-09-25) | `run_optimized()` is a no-op at P0 scope |
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

This slice's six capabilities are exactly the substrate Path B needs before any optimization technique (P1+) can be meaningfully compared: Sanitizer/Context Policy/Prompt Assembler/Output Controls form the minimal request-transformation path; Token Accounting and the Benchmark Harness are what make Path A vs. Path B actually measurable (§17). Docker Compose (§6, row 10) is the proposed packaging for this reference environment — low-risk, reversible, no architecture conflict. *(v1.0.11: no P0 unit produces the Path A baseline measurement the ledger would hold; `REM-P0.2.B-03` (§18.14.8) is a registered design unit for it. `EXE-P0.2.B` proves only the acquisition contract against a labelled test fixture and claims no live measurement — see the Gate P0.2 deferral, §18.10.)*

## 17. Baseline-vs-EAIOC Measurement

Per `architecture.md` §32.2's required comparison (cited, not redefined): input tokens, output tokens, total tokens, cached tokens, model calls, tool calls, cost, latency, quality, failure rate — captured by Capability 1 (ledger) and Capability 2 (`EvaluationRun`) for both Path A and Path B. At this slice's scope, Path B has no optimization stage active yet (P1 is out of scope), so the first meaningful comparison is Path B's *own added overhead* (Sanitizer/Context Policy/Prompt Assembler/Output Controls latency and token cost) against Path A's baseline — establishing the overhead floor every future optimization must net-exceed (`architecture.md` §27.3's Net Savings formula, cited). No fabricated savings figure is produced at this stage — there is nothing to save yet, only overhead to measure honestly. *(v1.0.11: the Capability 2 baseline result is a `BaselineEvaluationRecord` (`interfaces.md` §18), not an `EvaluationRun`. The comparison of Path B's own overhead against Path A's baseline has no representation at P0 and is deferred (`SOURCE-GAP-EXECPLAN-15`); no overhead or savings figure is claimed.)*

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

**Every `EXE-P0.<n>.<letter>` unit below is a standalone, independently executable and independently reviewable step: one lifecycle sub-phase = one git commit = one explicit human-approval checkpoint, with no exception anywhere in this document.** Sub-phase A's design note is never committed together with Sub-phase B's implementation, nor is any pair of sub-phases ever combined into one commit or one approval round; `EXE-P0.<n>.B` (and every subsequent letter) does not begin until the prior sub-phase's own commit has been reviewed and explicitly approved. This applies uniformly across all six capabilities (§18.4–§18.9) and is restated, not weakened, by the field-compaction note below. Concretely: Mode B is driven only by the canonical `Execute EXE-P0.<n>.<letter>` command, which names exactly one unit (§18.11.2); no capability-level instruction is ever authorization to implement Sub-phases A through H (or the Capability Gate) of a capability in one uninterrupted turn. Claude Code implements exactly the requested unit, reports evidence, and waits; it does not infer permission to continue to the next sub-phase or the next capability from a closed gate, a passing test, or the absence of an objection.

**v1.0.4 clarifications to this boundary (§18.12, §18.16):** (1) every unit's checkpoint is now two-stage — Claude Code's own AI Verification (§18.12) must complete with an acceptable verdict before the human-approval checkpoint is presented, and human approval remains required afterwards; neither substitutes for the other. (2) The "one sub-phase = one git commit" rule applies to every sub-phase that **produces implementation changes**. A sub-phase whose row is genuinely `NOT APPLICABLE` (e.g. every capability's Sub-phase F at P0 scope, §28) produces no source change and therefore **no implementation commit** — it still receives its own AI Verification (verdict `NOT APPLICABLE`) and its own explicit human approval, and is never given a fake or empty commit merely to satisfy sequencing (§18.16). The 48-unit count is unchanged by this exception.

**v1.1.0 continuous-execution qualification (§18.20):** the per-unit commit rule above — "one lifecycle sub-phase = one git commit" — has no exception under §18.20 and is unchanged: continuous execution never merges multiple units into one commit (§18.20.L). The per-unit human-approval-checkpoint rule above is qualified, not repealed: within an execution path explicitly opened under §18.20 by a human `Execute` command, a path-internal unit's own checkpoint is `### AI VERIFIED — CONTINUING` (§18.12.4, §18.17), not an individual `Approve …` request; explicit human approval is required only at that path's boundary (§18.20.G). Outside a §18.20 path — including every unit executed before the migration point recorded at §18.20.O — this section's original per-unit approval-checkpoint rule governs unchanged, with no exception.

**v1.1.1 note on N/A sub-phases (§18.20.R, HD-CE-17):** the N/A commit exception two paragraphs below applies unchanged in every case — an N/A sub-phase never has an implementation commit, inside or outside a §18.20 path. Its *approval* treatment, however, follows the same historical/path-internal split as above: outside a §18.20 path, an N/A sub-phase still receives its own explicit human approval (§18.16, unchanged there). Inside a §18.20 authorized path, an N/A sub-phase instead emits the distinct `### AI VERIFIED — CONTINUING (NOT APPLICABLE)` footer/state (§18.12.4) — never ordinary `CONTINUING`, which remains defined for committed, PASS-class units only — and continues automatically, with no individual `Approve` and no change to `HUMAN APPROVED`'s meaning (§18.17).

**The Capability Gate that follows Sub-phase H (§18.10) is not one of these `EXE-P0.<n>.<letter>` units** — it is a separate post-lifecycle review/promotion checkpoint, not an implementation sub-phase, and receives no `EXE-P0` ID and no implementation commit of its own (§18.10). Across the six capabilities this document therefore defines exactly **48** `EXE-P0.<n>.<letter>` atomic execution units (8 lifecycle sub-phases, A–H, per capability) plus **6** Capability Gates — **54 total gated checkpoints**, never described as "54 atomic execution units" anywhere in this document.

Every field the generation prompt's §10 requires (Objective, Scope, Sources, Requirements, Architecture components, Interfaces, Dependencies, Preconditions, Files/modules, Package responsibility, Data-model impact, API impact, Event impact, Configuration impact, Security impact, Observability impact, Tests, Negative tests, Benchmark requirements, Edge cases, Acceptance criteria, Definition of Done, Evidence, Rollback, Downstream impact, Blocking status, Source gaps, ADR dependency) is present below — but fields that do not vary sub-phase-to-sub-phase (Requirements covered, Architecture components, Interfaces, Dependencies, Security disposition, ADR dependency) are stated once per capability in the capability header rather than repeated across all eight lifecycle sub-phase entries A–H. The Capability Gate is a separate post-lifecycle review/promotion checkpoint and is not one of the eight lifecycle sub-phase entries (its table row points to §18.10, not to per-sub-phase fields). Restating an identical field eight times per capability would pad this document's length without adding sequencing signal beyond what `implementation-plan.md` §8 already established — the same discipline that document itself applied to its own exemplar/sequencing-table split (§6, `implementation-plan.md`). Every sub-phase-varying field (deliverable, files, tests, benchmark requirement, edge cases, Definition of Done, evidence) is given individually, per sub-phase, below.

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

*v1.0.11 note:* the statement above is about capability-level prerequisites and is unchanged. By operator decision D-A, `EXE-P0.2.B` additionally consumes a retrieval realization in Capability 1's `accounting/` package (`REM-P0.2.B-02`, §18.14.8). Capability 1 is already closed (Gate P0.1, 2026-09-23), so the order is unaffected; this is a data dependency of one unit, not a new capability prerequisite.

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
| **Preconditions** | §6's technology baseline confirmed (Java core). **Shared-foundation ownership (v1.0.4, §18.13):** this capability — specifically `EXE-P0.1.A` — **owns** the design and minimal repository-scaffolding definition of the shared zero-dependency substrate `control_plane/core/interfaces/`, `control_plane/core/schemas/`, and `control_plane/core/errors/`, including the minimum shared types the already-defined P0 contracts reference (at minimum `ControlPlaneRequest` and `OptimizationPlan`, required by Capability 2's `INTF-030`, and the `ControlPlaneError` taxonomy, `interfaces.md` §25) — fields verified verbatim against the live `interfaces.md`, never invented. Every later capability *consumes* this foundation and never creates it opportunistically. The already-executed `EXE-P0.1.A` did not produce it; see §18.14's post-hoc reconciliation and remediation units `REM-P0.1.A-01`/`-02` |
| **Package/module responsibility** | `control_plane/accounting/` (§9, direct match) |
| **Security/Governance disposition** | Not directly gated by any `GSP-NNN`, but `security.md`'s SGE (`GSP-001`) consumes this ledger's output — the ledger must exist before SGE's spend-governance logic has anything to evaluate |
| **ADR dependency** | None |
| **Downstream impact** | Every other capability in this slice, and every P1+ technique, depends on this ledger existing first |
| **Blocking status (per IRG)** | Non-blocking — unconditionally `READY` |

#### Sub-phase table

| Sub-phase | Deliverable | Files/Modules | Tests | Failure-Path Test | Benchmark Requirement | Edge Cases | Definition of Done | Expected Evidence |
|---|---|---|---|---|---|---|---|---|
| **A — Contract & Design** | Consume §27.1's ledger field list and §27.3's cost model as-is. **v1.0.4 (§18.13):** additionally owns the shared-core-foundation contract — the design and minimal scaffolding definition of `control_plane/core/interfaces/`, `core/schemas/`, `core/errors/`, enumerating the minimum shared types P0 contracts reference (`ControlPlaneRequest` — `interfaces.md` §2.1; `OptimizationPlan` — §3.2; `ControlPlaneError` — §25.1) with every field re-verified against the live `interfaces.md` rather than this row's text | `control_plane/accounting/ledger/LedgerEntry` (schema note, not yet code); shared-core foundation design/scaffold definition under `control_plane/core/` | Schema-conformance review (ledger fields **and** shared-core types against `interfaces.md`) | N/A (design stage) | None yet | None directly | No new schema invented; every field traced to §27.1; shared-core ownership discharged — foundation contract defined, not deferred to a consumer capability | Design note citing §27.1/27.3 verbatim, committed on its own (`EXE-P0.1.A`, one sub-phase = one commit — §18.2, `EXE-P0.1.B` does not begin until this commit is AI-verified and explicitly approved). **Historical note:** the executed `EXE-P0.1.A` (commit `a345742`) predates this v1.0.4 requirement and did not produce the shared-core foundation — reconciled, not rewritten, per §18.14 |
| **B — Minimal Implementation** | Per-request token/cost recording, tenant-scoped from the first write. **v1.0.6:** the recorded entry conforms to the corrected `INTF-047` contract (`interfaces.md` §28.1) — the 29 retained fields plus the nine nested canonical §27.1 groups | `control_plane/accounting/ledger/` | Unit tests: write, read-back, tenant-scoping enforcement. **v1.0.6:** contract tests proving the canonical nested ledger structure (all nine groups, all 58 §27.1 fields) | Unit test: write with missing `tenant_id` is rejected, not silently defaulted | None yet | `EC-077` (adversarial optimization-cost input) — cross-cutting, cited not re-derived here | Ledger write/read round-trips correctly; every record has `tenant_id` as first namespace component. **v1.0.6 additions** (`CONTRA-EXECPLAN-01`, §18.14.4): (1) conforms to corrected `INTF-047`; (2) represents all 58 §27.1 fields; (3) represents the six AC-005 categories distinctly (`interfaces.md` §28.1 coverage table); (4) tenant isolation preserved; (5) verified/unverified behavior preserved (`conventions.md` §14.1, §14.4); (6) contract tests prove the canonical structure; (7) no retained `INTF-047` field is aliased to a §27.1 field (D-2). **The historical `EXE-P0.1.B` (`e353dcf`) predates these requirements and does not satisfy (1)–(3), (6); it is reconciled, not rewritten (§18.14.4)** | Passing unit-test suite; `git diff` scoped to `control_plane/accounting/` only |
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
| **Objective** | Wire a P0-only path that can run an unoptimized request and record its `EvaluationRun` baseline fields; `run_optimized()` remains a no-op until P1 ships a technique to compare against *(v1.0.11: the baseline result is a `BaselineEvaluationRecord` acquired from a verified ledger measurement of a baseline run that executes outside EAIOC; see the B row and §18.14.8)* |
| **Sources** | `implementation-plan.md` §8.2; `architecture.md` §32.1–32.3; `interfaces.md` §18 (`EvaluationFramework`, `EvaluationRun`, `EvaluationComparison`); `quality-gates.md` (`QG-NNN` methodology, cited); `eval.md` §16 (`EvaluationRun` required fields) |
| **Requirements covered** | `OBJ-012`, `AC-015`, `AC-017` |
| **Architecture components** | Benchmarking Framework (`architecture.md` §32), Quality Evaluation (folded in per `implementation-plan.md` §8.2's own rationale — two distinct §36 items, one shared implementation foundation) |
| **Interfaces/contracts** | `EvaluationFramework.run_baseline()`/`run_optimized()`/`compare()` (`interfaces.md` §18, `INTF-030`); `EvaluationRun`'s fields (`eval.md` §16, cited); **v1.0.11:** `BaselineEvaluationRecord` (`interfaces.md` §18) |
| **Dependencies — v1.0.11 addendum** | The row below ("None (P0, no prerequisite)") is the capability-level dependency-model fact and is unchanged. `EXE-P0.2.B` additionally requires `REM-P0.2.B-02` (accounting retrieval realization; §18.14.8, §18.15). It does **not** require `REM-P0.2.B-03` |
| **Requirements coverage — v1.0.11 note** | Coverage of `OBJ-012`, `AC-015`, `AC-017` at P0 is limited to a contract-level baseline result carrying cost and latency: quality and task-success baselines have no producer (`SOURCE-GAP-EXECPLAN-11`), and live Path A measurement is deferred (§18.10). Verification must not report more than that |
| **Dependencies** | None (P0, no prerequisite) |
| **Preconditions** | `core/interfaces/`/`core/schemas/`/`core/errors/` exist, created under Capability 1's ownership (`EXE-P0.1.A`, or — for this repository's actual history — remediation units `REM-P0.1.A-01`/`-02`, §18.14) and **consumed, never created**, here. `EXE-P0.2.B` carries a hard pre-execution guard on this precondition (§18.15) |
| **Package/module responsibility** | `control_plane/benchmarking/` + `control_plane/evaluation/` (§9, direct match) |
| **Security/Governance disposition** | The harness must run under the same tenant-isolation and authorization rules as a live request — a benchmark is not an exemption from governance (root `CLAUDE.md` rules 2/4) |
| **ADR dependency** | None |
| **Downstream impact** | Every P1–P4 technique's own validation-matrix gate (`conventions.md` §25) calls into this harness |
| **Blocking status (per IRG)** | Non-blocking — unconditionally `READY` (bundled treatment for both distinct §36 items) |

#### Sub-phase table

| Sub-phase | Deliverable | Files/Modules | Tests | Failure-Path Test | Benchmark Requirement | Edge Cases | Definition of Done | Expected Evidence |
|---|---|---|---|---|---|---|---|---|
| **A — Contract & Design** | Cite `EvaluationFramework`'s three defined schemas (`INTF-030`) and `quality-gates.md`'s methodology as-is; this capability is the first *consumer* of both, not a redefinition | `control_plane/evaluation/` schema notes | Schema-conformance review | N/A | None yet | None | No competing schema introduced; `RegressionReport`'s undefined status (`SOURCE-GAP-EVAL-01`) explicitly not resolved here — out of scope | Design note |
| **B — Minimal Implementation** | `run_baseline()` returns a `BaselineEvaluationRecord` (`interfaces.md` §18; v1.0.11 — earlier wording: "`run_baseline()` executes and records `EvaluationRun` baseline fields; `run_optimized()` is an explicit no-op, documented as such, not silently stubbed."); `run_optimized()` is documented forward contract, neither declared nor implemented in Java at P0 (HQ-3; no stub). **Consumes** the shared `ControlPlaneRequest`/`OptimizationPlan` types from `core/schemas/` — never defines its own or a harness-local stand-in. Subject to §18.15's hard pre-execution guard: if any shared-foundation prerequisite is missing, `AI VERIFICATION: BLOCKED` and no code is written. **v1.0.8 (§18.14.6, HQ-1 to HQ-6):** this unit implements the baseline operational path only. `run_optimized()`, `compare()` and `report_regression()` are deferred INTF-030 forward contract, not operationally exercised here, with no invented runtime behavior. `EvaluationRun`/`EvaluationComparison` are owned by `control_plane/evaluation/`. `run_baseline()` is not idempotent by `request_id`. **Currently BLOCKED** on `SOURCE-GAP-EXECPLAN-07`/`SOURCE-GAP-EXECPLAN-08`: no baseline measurement has both a field mapping and a retrieval contract, and the non-nullable comparison-dependent fields have no BASELINE-only representation, so this row's Definition of Done cannot be met honestly. **v1.0.10:** remediation unit `REM-P0.2.B-01` is registered to prepare closure of these gaps (§18.14.7). It is not started, and this row stays BLOCKED until `-07`/`-08` are closed at source and this unit is revalidated. **v1.0.11 (§18.14.8, DB-3) — supersedes the BLOCKED statements above, which describe the v1.0.8–v1.0.10 position:** `SOURCE-GAP-EXECPLAN-07`/`-08` are resolved at source (`interfaces.md` §18, §28.2; §38 status updates). `run_baseline()` returns a `BaselineEvaluationRecord` (`run_id`, `request_id`, `tenant_id`, `run_type` = `BASELINE`, `timestamp`, `schema_version` = `1.0.0` (a human contract decision of 2026-09-25), `currency`, `source_entry_id`, `baseline_cost`, `latency_ms_baseline`; `interfaces.md` §18) built from a `verified = true` ledger entry retrieved through the `REM-P0.2.B-02` contract, under the declared mappings; EAIOC makes no model call (§14) and the baseline run executes outside it (§16). **Prerequisite:** `REM-P0.2.B-02` executed, AI-verified and human-approved (§18.15). **Not a prerequisite:** `REM-P0.2.B-03` (operator decision; §18.14.8). **Contract-level, fixture-based:** this unit uses a clearly labelled test-fixture ledger entry — a `verified = true` `CostLedgerEntry` built only in test sources (never under `src/main`), in a class and with constants named and commented as a fixture; its member values are illustrative, are not measurements, and are never presented as such — and must not claim, in code, tests, commit message or AI Verification report, a live Path A measurement. **Live Path A end-to-end baseline measurement: NOT DEMONSTRATED — explicitly deferred (§18.14.8; Gate P0.2, §18.10).** **Decided (human contract decision of 2026-09-25, §18.14.8):** when a valid baseline measurement cannot be acquired, `run_baseline()` fails without returning a `BaselineEvaluationRecord` (declared return stays non-null; no nullable record, no outcome wrapper, no new `ControlPlaneError` code or `error_class` value, no `PartialSuccess`). **Left to this unit (unit-level, not decided here):** the concrete failure mechanism (`SOURCE-GAP-EXECPLAN-13`, narrowed) — the unit returns `AI VERIFICATION: BLOCKED` if it would need (a) a new public `ControlPlaneError` code or `error_class` contract (§25), or (b) a new schema or return-type contract, because those are frozen-contract changes; any unit-local implementation mechanism that changes no public contract is a unit-level decision and is not by itself a reason to block | `control_plane/benchmarking/`, `control_plane/evaluation/` (no file under `control_plane/core/`) and no file under `control_plane/accounting/` — the retrieval realization is `REM-P0.2.B-02`, §18.14.8 | Unit test (v1.0.11; earlier wording: "`run_baseline()` produces a complete, correctly-typed `EvaluationRun`"): given a labelled test-fixture `verified = true` ledger entry retrieved through the `REM-P0.2.B-02` contract, `run_baseline()` produces a complete, correctly-typed `BaselineEvaluationRecord` with the declared mappings, `tenant_id`, `currency`, `schema_version` (`1.0.0`, a human contract decision of 2026-09-25), `source_entry_id` and `run_type = BASELINE`; the fixture is test-only and no `src/main` code constructs a `verified = true` entry | Unit tests (v1.0.11; earlier wording: "a failed baseline run does not produce a partial/malformed record"): each of no entry, several entries for one `(tenant_id, request_id)`, an entry with `verified = false`, and an entry under another tenant makes `run_baseline()` fail without returning a `BaselineEvaluationRecord` (no partial, malformed, zero, placeholder or sentinel record); "several entries" are counted over all matching entries, so a verified entry plus an unverified one is several | None yet — this capability *is* the benchmark substrate, nothing to benchmark against yet | None | `run_baseline()` round-trips correctly; `run_optimized()`'s no-op status is explicit in code and docs *(v1.0.9, per HQ-3 §18.14.6: "explicit" is satisfied by documentation; no runtime no-op, stub, or exception is implemented for `run_optimized()` in P0 code)* **v1.0.11:** the DoD is contract-level. "Round-trips correctly" means a labelled test-fixture entry is retrieved and mapped into a complete, tenant-scoped (§12) `BaselineEvaluationRecord` and the failure paths hold. It is **not** evidence of a live Path A measurement, a live Path A vs Path B comparison or any savings; no test, commit message, AI Verification report or status document may say otherwise, and the §18.12.3 report must state the fixture-based, contract-level nature. **Live Path A end-to-end baseline measurement: NOT DEMONSTRATED — explicitly deferred (§18.14.8).** The wording "complete, correctly-typed `EvaluationRun`" in earlier versions of this row is superseded (§18.14.8). | Passing unit tests |
| **C — Integration** | Every future P1+ technique's validation-matrix gate will call into this harness — the shared substrate is built once | `control_plane/benchmarking/` public API | Integration test with a stub caller | N/A | None yet | None | Public API stable for future technique gates to call | Integration test passing |
| **D — Observability** | Cite `observability.md`'s existing quality/cost metrics — no competing name introduced | Structured log output (interim sink, `SOURCE-GAP-EXECPLAN-01`) | Log-format check | N/A | None | None | Every `run_baseline()` call emits one structured log line | Sample log output |
| **E — Security/Governance** | Harness runs under the same authorization/tenant rules as a live request | `control_plane/evaluation/` | Unit test: a benchmark run cannot bypass tenant scoping | Unit test: cross-tenant benchmark data leakage is impossible | None | None | No governance bypass path exists | Passing test |
| **F — Quality Validation** | This capability *is* the quality-validation substrate — cite `quality-gates.md`'s ten `QG-NNN` dimensions it must eventually be able to evaluate against, once P1+ techniques exist to measure | — | N/A at P0 scope — no technique exists yet to run a `QG-NNN` gate against | — | — | — | Documented readiness to accept `QG-NNN` evaluation calls once P1 ships | Design note cross-referencing `quality-gates.md` §5 |
| **G — Scenario Validation** | `NO DEDICATED SCENARIO` verified for harness construction itself (`implementation-plan.md` §8.2, restated); individual `SCN-QUAL-*`/`SCN-CMP-05x` scenarios validate specific *uses* of the harness once P1+ techniques exist | — | — | — | — | — | — | — |
| **H — Failure/Recovery** | A harness failure must never silently mark an unvalidated technique as validated — fail closed on the validation *claim*, even though the underlying optimization stage itself would fail open per root `CLAUDE.md` rule 2 | Same as B | Unit test: a harness failure results in "not validated," never a silent pass | Unit test: simulated `run_baseline()` failure does not produce a false-positive `EvaluationRun` *(v1.0.11: the baseline result is a `BaselineEvaluationRecord`, §18.14.8)* | None | None | Failure never masquerades as a valid result | Passing failure-injection test |
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
| **Preconditions** | `core/interfaces/`/`core/schemas/`/`core/errors/` exist under Capability 1's ownership (§18.13–§18.14) and are consumed, never created, here — the same pre-execution check §18.15 makes mandatory for `EXE-P0.2.B` applies to `EXE-P0.3.B` |
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

**v1.0.4 — three-stage model (§18.12, §18.11.6):** the Gate now has two layers of its own, on top of H's own two-stage checkpoint. The authoritative sequence is:

```
Sub-phase H
   ↓
AI Verification of H                 (§18.12 — verdict must be acceptable)
   ↓
Human Approval of H                  (Approve EXE-P0.n.H)
   ↓
Capability Gate Review               (the question set above, against B–H evidence)
   ↓
AI Gate Verification                 (AI CAPABILITY GATE VERIFICATION: PASS / BLOCKED)
   ↓
Human Gate Approval                  (Approve CAPABILITY-GATE P0.n — valid only after an AI PASS; no commit)
   ↓
Next Capability A                    (still requires its own explicit Execute EXE-P0.(n+1).A)
```

Units executed before v1.0.4 (single-stage human approval only) are not retroactively re-executed; the AI Gate Verification for their capability re-examines their evidence as part of the Gate review, and any unit whose evidence does not hold up is treated as a Blocking Condition (§18.14).

**v1.0.11 — Capability Gate P0.2: explicit deferral of live Path A measurement (§18.14.8).** `EXE-P0.2.B` is a contract-level unit that proves acquisition and failure paths against a labelled test-fixture ledger entry (§18.5 B row). No unit in the P0 plan produces a live, `verified = true` Path A ledger entry before Gate P0.2: `REM-P0.2.B-03` is a registered design unit and is not a prerequisite of `EXE-P0.2.B` (`SOURCE-GAP-EXECPLAN-11`, `-12`, `-15`). Therefore, at the Gate P0.2 review: (1) the Gate review and the `AI CAPABILITY GATE VERIFICATION` report must state, in these words, "Live Path A end-to-end baseline measurement: NOT DEMONSTRATED — explicitly deferred (§18.14.8)."; (2) a passing `EXE-P0.2.B`, `EXE-P0.2.H` or Gate P0.2 must not be described, in any report, commit message, runbook or status document, as evidence that a live baseline measurement, a live Path A vs Path B comparison or any savings figure exists; (3) the deferral is an explicit recorded item, not a Blocking Condition (the Blocking Conditions above concern Sub-phase E/F/G/H items): it neither blocks the Gate nor is satisfied by it; (4) only a later, user-directed correction that records a completed live producer, with its approved units, may lift it. Approval of Gate P0.2 therefore approves the contract-level substrate only.

If the Gate finds a Blocking Condition unmet, promotion to the next capability is denied until it is addressed — this is a promotion decision, not a code rollback (§37): every A–H commit remains individually revertible, but the Gate has no commit of its own to revert.

**v1.1.0 clarification (§18.20):** under continuous execution, the Capability Gate remains the sole human boundary separating one Capability from the next — this is unchanged. When a §18.20 authorized path runs through H, the path's boundary approval (§18.20.G) coincides with this Gate's Human Gate Approval, which then reviews every path-internal unit's evidence alongside H's own, extending this section's existing whole-capability review pattern rather than replacing it. Gate approval never itself authorizes the next Capability's Sub-phase A (§18.20.E, §18.20.M) — that still requires its own explicit `Execute EXE-P0.(n+1).A`, unchanged from §18.11.6 below.

### 18.11 Claude Code Mode B Execution Command Protocol

This section makes §18.2's and §18.10's approval discipline operational: the exact commands a human operator issues to a Claude Code session, the exact response format Claude Code returns, and the exact state transitions permitted between them. It adds no new scope, technology, requirement, or sequencing content — it is a command-syntax and reporting-format clarification of the model already established above.

#### 18.11.1 Mode B is strictly user-command-driven

Mode B never begins or advances on its own. The user explicitly issues the command for every atomic unit; Claude Code never infers authorization for the next unit from anything that happened during the current one. In particular, none of the following — individually or in combination — constitute authorization to proceed: all tests passing, a clean `git status`, a successful commit, a closed Capability Gate, or the absence of an objection from the user. The only thing that authorizes starting an execution unit is an explicit user command naming that unit (§18.11.2, §18.11.4).

**v1.1.0 qualification (§18.20):** the explicit human `Execute` command remains the sole event that opens authorization — this is unchanged. What one such command authorizes is redefined, not weakened: per §18.20, naming one unit opens a path-scoped authorization covering that unit and every remaining sub-phase of its Capability, through that Capability's own Gate, never a different Capability. Nothing in this subsection's list of non-authorizing signals (passing tests, clean status, successful commit, closed Gate, absence of objection) is altered — none of them ever expands what is authorized; only an explicit human `Execute` does, exactly as stated above.

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

For approvals:

```
Approve EXE-P0.<n>.<letter>
```

For Capability Gates:

```
Approve CAPABILITY-GATE P0.<n>
```

For Gate review:

```
Review CAPABILITY-GATE P0.<n>
```

(History-specific remediation units use the same pattern with their own IDs — `Execute REM-P0.1.A-0N` / `Approve REM-P0.1.A-0N`, §18.14.)

**The canonical `Execute EXE-P0.<n>.<letter>` syntax is mandatory for Mode B execution. Do not use `Execute P0.<n>` as an execution command.** No capability-only command resolves, dynamically or otherwise, to any sub-phase depending on execution history; Claude Code treats such a command as ambiguous under §18.11.9 and asks for the canonical unit ID instead of executing anything.

*Historical behavior superseded by the canonical command protocol (v1.0.3–v1.0.4 only; not an active rule):* earlier versions allowed a capability-level shorthand (`Execute P0.1`) that resolved to one sub-phase. It was retired in v1.0.5 because its resolution depended on execution history and was therefore ambiguous.

**v1.1.0 authorization-semantics note (§18.20):** the canonical syntax above is unchanged. What issuing one such command authorizes is redefined by §18.20: naming `EXE-P0.<n>.<letter>` opens the bounded execution path consisting of that unit and every remaining not-yet-approved sub-phase of Capability `<n>`, through that Capability's own Gate — never Capability `<n>+1`. This applies from the migration point recorded at §18.20.O onward (`EXE-P0.2.C`); units executed before that point were, and remain, governed by this subsection's original one-unit-per-command meaning.

#### 18.11.3 Per-unit execution lifecycle

**Replaced in v1.0.4** by the two-stage lifecycle below (the v1.0.3 single-stage diagram — execute → commit → `### AWAITING APPROVAL` — is superseded; nothing in it is weakened, AI Verification is inserted before the human checkpoint):

```
USER
  |
  | Execute EXE-P0.n.X
  v
CLAUDE CODE
  |
  |-- Re-read the authoritative source sections
  |-- Verify prerequisite approval state
  |-- Execute ONLY EXE-P0.n.X
  |-- Run required tests/checks
  |-- Perform AI verification against:
  |      - execution-plan row
  |      - authoritative source contracts
  |      - requirements
  |      - architecture
  |      - security/governance
  |      - failure/recovery expectations
  |      - file-scope rules
  |      - git-scope rules
  |-- Produce AI VERIFICATION verdict
  |-- If PASS: create required implementation commit
  |-- Report evidence
  v
### AI VERIFIED — AWAITING HUMAN APPROVAL
  |
  | Claude Code STOPS
  |
USER
  |
  | Approve EXE-P0.n.X
  v
HUMAN APPROVED
  |
  | Claude Code may acknowledge approval
  |
NEXT EXPLICIT Execute EXE-P0.n.next-letter
```

The AI verification is a prerequisite to human approval. "If PASS" includes `PASS WITH DOCUMENTED NON-BLOCKING GAP`; a `NOT APPLICABLE` verdict produces no commit (§18.16); a `BLOCKED` verdict produces no commit and **no approval request** — the report ends with `### AI VERIFICATION BLOCKED` instead (§18.12.4). Where the prerequisite check itself fails (e.g. §18.15's guard), Claude Code writes no code at all and reports `AI VERIFICATION: BLOCKED` naming exactly which prerequisite is missing.

Claude Code stops after exactly one atomic unit every time. It does not continue to the next letter automatically, and it does not perform the Capability Gate automatically after `H` is approved (§18.11.6).

**v1.1.0 note (§18.20):** the diagram above (`Approve EXE-P0.n.X` → `HUMAN APPROVED` → next explicit `Execute`) is this subsection's original, still-governing model for every unit outside a §18.20 authorized path. For a unit inside such a path, this diagram's `### AI VERIFIED — AWAITING HUMAN APPROVAL` / `Approve EXE-P0.n.X` / `HUMAN APPROVED` sequence is replaced, for that unit only, by `### AI VERIFIED — CONTINUING` and automatic continuation, per §18.12.4 and §18.20.D–G; the sequence above still governs the path's boundary unit unchanged.

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

**AI Verification Report (v1.0.4, mandatory)** — the full §18.12.3 report, covering all nine verification dimensions and ending in exactly one `AI VERIFICATION:` verdict. The Verification/Git Evidence/Scope Compliance blocks above feed this report; they do not replace it.

**Approval Checkpoint (v1.0.4 — supersedes the v1.0.3 `### AWAITING APPROVAL` ending)** — only after an acceptable AI verdict, the report ends with exactly:

For A–G:
```
### AI VERIFIED — AWAITING HUMAN APPROVAL

AI VERIFICATION: PASS

Approve EXE-P0.n.X to continue.
```
For H:
```
### AI VERIFIED — AWAITING HUMAN APPROVAL

AI VERIFICATION: PASS

Approve EXE-P0.n.H to proceed to the Capability Gate.
```
(`AI VERIFICATION: PASS` in these blocks is replaced by `PASS WITH DOCUMENTED NON-BLOCKING GAP` or `NOT APPLICABLE` when that is the actual verdict — §18.12.2, §18.16.) If AI verification is blocked, the report instead ends with the §18.12.4 blocked block and **no approval request**. Claude Code does not perform the Capability Gate automatically after any of these.

**v1.1.0 pointer (§18.20):** for a unit inside a §18.20 authorized path (other than the path's boundary unit), the block above is replaced by the `### AI VERIFIED — CONTINUING` block defined at §18.12.4 — this subsection's blocks above are not duplicated here; they remain the governing text for every unit outside a §18.20 path and for a path's boundary unit.

#### 18.11.6 Capability Gate command protocol

After the user issues `Approve EXE-P0.n.H`, Claude Code may prepare and present the Capability Gate review — evaluating the question set already defined in §18.10 (Entry/Implementation/Integration/Security-Governance/Quality/Scenario/Failure-Recovery Completion, Exit Criteria, Blocking Conditions) against the evidence Sub-phases B–H actually produced — but must never treat the gate as automatically approved. If the Gate review is resumed in a later session (i.e. not in the same turn that received `Approve EXE-P0.n.H`), the operator triggers it with `Review CAPABILITY-GATE P0.n` — a review-only trigger that authorizes no implementation and no approval.

**v1.0.4 — two layers (§18.10):** the review now produces an explicit **AI Gate Verification** — Claude Code re-examines Entry Criteria, Implementation Completion, Integration Completion, Security/Governance Completion, Quality Completion, Scenario Completion, Failure/Recovery Completion, Exit Criteria, and Blocking Conditions against the actual commits, tests, and live sources (including any pre-v1.0.4 unit's evidence and any open §18.14 reconciliation finding) — followed by **Human Gate Approval**. The human command is valid only after the AI Gate Verification is satisfactory. The review is reported as:

If AI Gate Verification passes:

```
CAPABILITY GATE REVIEW: P0.n
AI CAPABILITY GATE VERIFICATION: PASS
STATUS: AWAITING HUMAN APPROVAL

Approve CAPABILITY-GATE P0.n
```

If blocked:

```
### CAPABILITY GATE BLOCKED

AI CAPABILITY GATE VERIFICATION: BLOCKED

Reason:
Required remediation:
No next capability may begin.
Human Gate Approval is not requested.
```

If the user then issues `Approve CAPABILITY-GATE P0.n` (§18.11.4) after an AI PASS, Claude Code reports:

```
CAPABILITY GATE APPROVED: P0.n
NEXT AUTHORIZED UNIT: EXE-P0.(n+1).A
```

but still waits for the separate, explicit command `Execute EXE-P0.(n+1).A` before doing anything further — the gate-approval report is informational, not an execution trigger. An `Approve CAPABILITY-GATE P0.n` issued while the AI Gate Verification is `BLOCKED` or has not yet been performed is not acted on; Claude Code reports that the Gate's AI verification is outstanding.

**v1.1.0 note (§18.20):** unchanged, absolutely — this is the one invariant §18.20.M restates rather than qualifies. Where a §18.20 authorized path runs through H, `Approve CAPABILITY-GATE P0.n` is also the path's boundary approval (§18.20.C, §18.20.G) and must enumerate every path-internal unit it covers; it still, in every case, only authorizes the next capability's Sub-phase A informationally, never triggers it.

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

**v1.0.4 reading of this sequence:** between every `Execute` and its `Approve`, Claude Code's AI Verification (§18.12) runs automatically and must return an acceptable verdict — the operator never issues a separate `Verify` command, and never issues `Approve …` in response to a `### AI VERIFICATION BLOCKED` report. Between `Approve EXE-P0.n.H` and `Approve CAPABILITY-GATE P0.n`, the AI Gate Verification (§18.11.6) runs and must return `PASS`. For **this repository's actual execution history**, the idealized sequence above does not describe where execution currently stands; §18.18 gives the authoritative remaining sequence, including the remediation units §18.14 requires before `EXE-P0.2.B`.

**v1.1.0 note (§18.20):** for `EXE-P0.2.C` onward, and for every Capability from Capability 3 forward, this reference sequence's individual per-sub-phase `Execute`/`Approve` pairs are superseded by §18.20's path-scoped model: one `Execute` at a Capability's authorized starting unit opens automatic continuation through that Capability's remaining sub-phases to its Gate (§18.20.C–D), with `Approve` required only at the boundary (§18.20.G). This listing is retained as a reference for canonical command syntax and unit ordering (its own framing above), not as the current authorization model for `EXE-P0.2.C` onward.

#### 18.11.8 Command-state table

| State | Allowed next action |
|---|---|
| No active unit | User may issue `Execute EXE-P0.n.X` if that unit's own prerequisites are approved |
| Unit executing | Claude Code executes only that unit |
| Unit executed, AI verifying (v1.0.4) | Claude Code performs AI Verification (§18.12) — automatic, part of the same `Execute` command; no user command involved |
| AI verification blocked (v1.0.4) | No approval request is made; remediation must occur, then the unit is re-executed/re-verified on a new explicit `Execute` command (§18.17) |
| AI verified, awaiting human approval | User must issue `Approve EXE-P0.n.X` before anything else can happen |
| `H` approved | Capability Gate review and AI Gate Verification may occur (§18.11.6) |
| Gate AI-verified (`PASS`), awaiting human approval | User must issue `Approve CAPABILITY-GATE P0.n` |
| Gate approved | User may issue the next capability's `Execute EXE-P0.(n+1).A` |
| Gate blocked (AI Gate Verification `BLOCKED`) | No next capability may start and no Gate approval is requested until the blocking condition is addressed and the gate is re-reviewed |

**v1.1.1 qualification (§18.20; fixes a v1.1.0 drafting omission — this table did not previously receive the same cross-reference every other command-protocol section did).** The rows above are this subsection's original, still-governing model for every unit outside a §18.20 authorized path. Inside such a path: a PASS-class path-internal unit reaching "AI verified" produces `### AI VERIFIED — CONTINUING` and continues automatically, no `Approve` required; an N/A path-internal unit produces `### AI VERIFIED — CONTINUING (NOT APPLICABLE)` (§18.12.4, §18.20.R) and likewise continues automatically, no `Approve` required. Neither changes what `HUMAN APPROVED` means — a genuine boundary unit (Gate, or a §18.20.K stop) still follows the "AI verified, awaiting human approval → must issue `Approve`" row above exactly as written.

Claude Code never self-authorizes any transition in this table — every row's "allowed next action" column names a user command, never an automatic Claude Code action. The only automatic Claude Code transition is performing AI Verification on the unit or Gate it was just asked to execute or review; AI Verification is evidence, never authorization. The full per-unit state model is §18.17.

#### 18.11.9 Prohibited command interpretation

Claude Code must reject or stop on ambiguous natural-language instructions rather than guessing which unit(s) they mean, including but not limited to:

```
Execute P0.1
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

#### 18.11.11 Commit rule (restated from §18.2/§18.10, clarified in v1.0.4)

```
One executable A–H sub-phase = one isolated implementation change-set.
Where that sub-phase produces implementation changes: one sub-phase = one implementation commit.
Where a sub-phase is genuinely NOT APPLICABLE and produces no implementation changes: no implementation commit.
Capability Gate = no implementation commit (review/promotion checkpoint only).
Remediation unit (§18.14) = one remediation commit per remediation unit that produces changes — never counted among the 48.
```

An implementation commit contains only the files its unit's row names (§18.12.1 G). It never includes personal editor configuration (e.g. `.vscode/`), `CLAUDE.md`, or progress/status documentation unless a source-backed row explicitly authorizes it (§18.16). No N/A checkpoint is ever represented by a fake or empty commit.

#### 18.11.12 No-automatic-chaining rule

Claude Code must stop after the requested atomic unit even when all tests pass, the commit succeeds, no blocking condition is found, or the user previously approved the broader capability in general terms. No completion signal, passing gate, or successful command result is ever interpreted as permission to execute the next unit — the only valid authorization is the next explicit `Execute EXE-P0.<n>.<letter>` (or `Approve …`) command from the user (§18.11.1). **v1.0.4:** this explicitly includes Claude Code's own `AI VERIFICATION: PASS` and `AI CAPABILITY GATE VERIFICATION: PASS` — neither is ever permission to continue (§18.12.5). Approval and the next execution are never combined into one command.

**SUPERSEDED BY §18.20 (v1.1.0), prospectively from `EXE-P0.2.C` onward.** The rule above is retained verbatim as the historical rule and remains the fully governing rule for every unit outside a §18.20 authorized path. §18.20's "NO UNAUTHORIZED CHAINING" (§18.20.P) is the authoritative current statement of this principle: it does not weaken the rule above — no test result, commit, AI Verification PASS, or closed Gate ever expands what is authorized, exactly as required here — it restates that exact boundary in path-scoped terms. The one difference is that a path opened under §18.20 by an explicit human `Execute` permits automatic continuation to the next unit strictly *within* that same path, which this subsection's original wording (predating §18.20) did not contemplate. See §18.20 for the current rule; this subsection's text above is not deleted and governs unchanged everywhere §18.20 does not apply.

### 18.12 AI Verification Protocol (v1.0.4)

From v1.0.4 onward, every atomic execution unit follows:

```
EXECUTE
   ↓
AI VERIFICATION
   ↓
AI VERDICT
   ↓
HUMAN REVIEW
   ↓
HUMAN APPROVAL
   ↓
NEXT EXPLICIT EXECUTE COMMAND
```

Human approval alone is not sufficient. AI verification alone is not sufficient. Both are required. The rationale: the v1.0.3 model relied entirely on the human operator to detect a unit that silently fell short of its row — exactly the failure `EXE-P0.2.B` exposed, where a precondition the plan asserted as satisfied (§18.5) had never been produced by any executed unit. An explicit, checklist-driven AI verification makes such gaps surface as a `BLOCKED` verdict *before* approval is requested, rather than depending on the operator to notice.

#### 18.12.1 AI Verification Checklist

For **every** `EXE-P0.n.X` (and every §18.14 remediation unit), Claude Code verifies all applicable dimensions below.

**A. Scope verification.** Confirm:

```
Only requested EXE-P0.n.X executed.
No later sub-phase executed.
No other capability executed.
No unrelated source files changed.
No upstream documentation modified.
```

**B. Source verification.** Re-read the live authoritative source sections named by that capability's Sources field (§18.4–18.9). Do not rely solely on the execution-plan text — prior units have found row claims inaccurate against the live corpus (e.g. `EXE-P0.1.G`'s "no dedicated scenario" claim). Confirm:

```
Source section exists.
Referenced contract exists.
Implementation matches source.
No invented schema/requirement was introduced.
```

**C. Requirement verification.** Verify each requirement listed for the capability/sub-phase (the capability header's Requirements covered field, plus the row's own Definition of Done). Report:

```
Requirement ID → satisfied / not satisfied / not applicable
```

**D. Architecture verification.** Verify the implementation respects: component boundary; package/module boundary (§8–§9, `conventions.md` §2.1); dependency direction (`conventions.md` §2.2 — `core/` depends on nothing else in `control_plane/`); provider neutrality (root `CLAUDE.md` rule 1); state ownership; tenant isolation (root `CLAUDE.md` rule 4); fail-open/fail-closed disposition (root `CLAUDE.md` rule 2, `conventions.md` §16.2).

**E. Test verification.** Run the tests/checks actually required by the sub-phase's Tests column. Do not claim a test passed unless it actually ran in this session. Report:

```
Command
Result
Relevant evidence
```

**F. Negative/failure-path verification.** Run the failure-path or negative test explicitly identified by the row's Failure-Path Test column, where applicable; state `N/A` with the row's own reason where the row says N/A.

**G. Git verification.** Verify:

```
git status --short
git diff --stat
git diff --name-only
```

and confirm that changed files belong to the requested unit. Where a commit is required:

```
git log -1 --oneline
```

and verify the commit contains only the intended scope (no `.vscode/`, `CLAUDE.md`, or other out-of-unit file — §18.16).

**H. Dependency/precondition verification.** Confirm all prerequisites are actually satisfied — by inspecting the repository and the approval record, not by trusting the plan's Preconditions text. This is especially important for `EXE-P0.2.B`, which must verify the shared core foundation really exists before implementing against it (§18.15).

**I. Source-gap verification.** If an execution exposes a missing contract or source gap:

```
AI VERIFICATION: BLOCKED
```

unless the plan explicitly classifies the gap as non-blocking (e.g. `SOURCE-GAP-EXECPLAN-01`–`03`, §38; `SOURCE-GAP-EVAL-01` for Capability 2) and the unit can proceed without inventing content — in which case the verdict is `PASS WITH DOCUMENTED NON-BLOCKING GAP`, naming the gap ID.

**K. Execution-assistance verification (v1.0.9, §18.19).** Evaluated before the final verdict; lettered K so that J, the final verdict, keeps its letter and no existing reference is renumbered. Confirm:

```
Only applicable skills/agents were used (§18.19.4 routing; minimum sufficient assistance).
No skill/agent modified any file (all are read-only); no out-of-scope file changed.
Specialist output was treated as evidence, not authority (§18.19.6).
No skill/agent authorized progression, approved anything, or invented a requirement/contract.
Every specialist finding is reflected in the report and resolved, documented non-blocking, or BLOCKED.
The final verdict is independently supported by the required evidence (dimensions A–I).
```

**J. Final AI verdict.** Conclude with exactly one of:

```
AI VERIFICATION: PASS
```

```
AI VERIFICATION: BLOCKED
```

```
AI VERIFICATION: NOT APPLICABLE
```

```
AI VERIFICATION: PASS WITH DOCUMENTED NON-BLOCKING GAP
```

**v1.1.0 note (§18.20):** these four verdict values are exhaustive and unchanged — §18.20 adds no fifth verdict. A PASS-class verdict (`PASS` or `PASS WITH DOCUMENTED NON-BLOCKING GAP`) here produces one of two different footers depending on whether the unit lies inside a §18.20 authorized path (§18.12.4): `### AI VERIFIED — AWAITING HUMAN APPROVAL` (unchanged) or, for a path-internal unit short of the path's boundary, the new `### AI VERIFIED — CONTINUING`. The footer choice is downstream of the verdict; it never changes what the verdict itself means. **v1.1.1 note (§18.20.R):** the same downstream-of-the-verdict principle applies to `NOT APPLICABLE` — it remains this list's third value, unchanged; inside a §18.20 path short of the boundary it produces the distinct `### AI VERIFIED — CONTINUING (NOT APPLICABLE)` footer (§18.12.4) rather than either of the PASS-class footers. Still exactly four verdicts; still no fifth.

#### 18.12.2 AI Verification Is Not Human Approval

**AI Verification** — Claude Code answers: *"Based on the authoritative sources, the execution-plan row, the actual changes, tests, and Git evidence, did this atomic unit satisfy its defined requirements?"* Possible outcomes: `AI VERIFICATION: PASS`, `AI VERIFICATION: BLOCKED`, `AI VERIFICATION: NOT APPLICABLE`, `AI VERIFICATION: PASS WITH DOCUMENTED NON-BLOCKING GAP`.

**Human Approval** — the user answers: *"I reviewed the AI verification and the evidence and authorize progression."* Canonical command:

```
Approve EXE-P0.n.X
```

The human approval must never be simulated by the AI. The user issues `Approve EXE-P0.n.X` only after reviewing the AI verification and the actual implementation evidence. Claude Code must not interpret `AI VERIFICATION: PASS` as human approval, and must not interpret a human approval of a previous unit as approval of the next unit.

#### 18.12.3 Mandatory AI Verification Report

For every atomic unit, Claude Code uses:

```
AI VERIFICATION REPORT

Execution Unit: EXE-P0.n.X
Capability: <name>
Sub-phase: <name>

1. Scope Verification:
2. Source Verification:
3. Requirement Verification:
4. Architecture Verification:
5. Test Verification:
6. Negative/Failure-Path Verification:
7. Git Verification:
8. Dependency/Precondition Verification:
9. Source-Gap Verification:
10. Execution-Assistance Verification:   (v1.0.9 — dimension K; assistance used / not applicable, and why)

AI VERIFICATION: PASS / BLOCKED / NOT APPLICABLE / PASS WITH DOCUMENTED NON-BLOCKING GAP

Blocking Issues:
<none or explicit issues>
```

Only after the AI verdict is satisfactory (`PASS`, `PASS WITH DOCUMENTED NON-BLOCKING GAP`, or — for a genuinely N/A row — `NOT APPLICABLE`) may Claude Code present the human approval checkpoint.

#### 18.12.4 Revised Human Approval Checkpoint

For A–G:

```
### AI VERIFIED — AWAITING HUMAN APPROVAL

AI VERIFICATION: PASS

Approve EXE-P0.n.X to continue.
```

For H:

```
### AI VERIFIED — AWAITING HUMAN APPROVAL

AI VERIFICATION: PASS

Approve EXE-P0.n.H to proceed to the Capability Gate.
```

If AI verification is blocked:

```
### AI VERIFICATION BLOCKED

AI VERIFICATION: BLOCKED

Reason:
Required remediation:

Human approval is NOT requested.
The next execution unit is NOT authorized.
```

A blocked AI verification must not be followed by an approval request. An `Approve EXE-P0.n.X` issued for a unit whose AI verification is `BLOCKED` is not acted on as progression; Claude Code reports that the unit is blocked and restates the required remediation.

**v1.1.0 — path-internal continuation footer (§18.20).** For a unit inside an execution path already opened under §18.20 — and only when the next sub-phase remains within that same path, i.e. this is not the path's boundary unit (§18.20.C, §18.20.K) — a PASS-class verdict produces this footer instead of the A–G/H blocks above:

```
### AI VERIFIED — CONTINUING

AI VERIFICATION: PASS

This unit is committed. Continuation to EXE-P0.n.<next-letter> occurs
automatically because this unit lies within the execution path opened by
Execute EXE-P0.n.<start-letter> under §18.20. This is NOT human approval
and does not authorize any unit outside that path.
```

(`AI VERIFICATION: PASS` above is replaced by `PASS WITH DOCUMENTED NON-BLOCKING GAP` when that is the actual verdict, exactly as for the blocks above.) At the path's boundary unit — the last sub-phase before its Capability Gate, or any unit at which a §18.20.K blocking condition applies — the footer reverts to `### AI VERIFIED — AWAITING HUMAN APPROVAL` above, and that approval request explicitly enumerates every unit the path covers since it opened or last resumed (§18.20.G). Outside a §18.20 path, this subsection's A–G/H blocks above apply unchanged to every unit, with no exception.

**v1.1.1 — path-internal N/A continuation footer (§18.20.R, HD-CE-17).** The `### AI VERIFIED — CONTINUING` block above is defined for, and remains limited to, a PASS-class verdict on a *committed* unit. It is never emitted for a `NOT APPLICABLE` verdict. For a unit inside a §18.20 path whose verdict is `AI VERIFICATION: NOT APPLICABLE` — and only when it is not the path's boundary unit — this distinct footer is emitted instead:

```
### AI VERIFIED — CONTINUING (NOT APPLICABLE)

AI VERIFICATION: NOT APPLICABLE

No implementation commit exists or is required for this unit. Continuation
to EXE-P0.n.<next-letter> occurs automatically because this unit lies
within the execution path opened by Execute EXE-P0.n.<start-letter> under
§18.20. This is NOT human approval, does not claim the unit was committed,
and does not authorize any unit outside that path.
```

This is not a fifth AI Verification verdict — the four verdicts of §18.12.1.J are unchanged and exhaustive; this is a footer/state indicating the continuation disposition for an already-reached `NOT APPLICABLE` verdict, exactly as `### AI VERIFIED — CONTINUING` is a footer/state for an already-reached PASS-class verdict. The two footers are never interchangeable: `CONTINUING` always implies a commit exists; `CONTINUING (NOT APPLICABLE)` always implies no commit exists (§18.16, unchanged). At the path's boundary unit, or wherever a §18.20.K condition applies, an N/A unit's footer reverts to `### AI VERIFIED — AWAITING HUMAN APPROVAL` (§18.16) exactly as outside a §18.20 path. Per §18.20.S/HD-CE-18/HD-CE-19, this footer — like ordinary `CONTINUING` — may satisfy a downstream guard whose purpose is only to confirm the predecessor sub-phase completed, but it never satisfies `HUMAN APPROVED`, human authorization, Capability Gate approval, boundary approval, or any guard specifically requiring human review.

#### 18.12.5 Critical Safety Rule for Mode B

> **Claude Code MUST NOT request human approval until its own AI Verification has completed and returned an acceptable verdict. Human approval is a second, independent authorization layer. AI verification is evidence; human approval is authorization. Neither substitutes for the other.**

> **Claude Code MUST NOT treat its own AI Verification PASS as permission to execute the next unit. Only the explicit human `Approve ...` command authorizes progression, and only a subsequent explicit `Execute ...` command authorizes implementation.**

**v1.1.1 qualification (§18.20; fixes a v1.1.0 drafting omission).** Both blockquotes above remain fully true as stated: no signal other than an explicit human `Approve …` ever *authorizes* anything, and only an explicit human `Execute …` ever *opens* authorization to implement. What they do not by themselves describe is continuation already inside an authorization a human has already granted: once a path is opened under §18.20 by such an `Execute` command, a path-internal unit's PASS-class or `NOT APPLICABLE` continuation (`### AI VERIFIED — CONTINUING` / `### AI VERIFIED — CONTINUING (NOT APPLICABLE)`, §18.12.4) is not a new authorization event and is not what these two rules govern — it is the exercise of the authorization the triggering `Execute` already granted (§18.20.D). Neither an AI PASS nor a `NOT APPLICABLE` verdict ever itself grants authorization; only the path's own opening `Execute` did.

#### 18.12.6 Explicit Command Rules

| Step | Command | Issued by |
|---|---|---|
| Start | `Execute EXE-P0.n.X` | User |
| AI verification | *(none — performed by Claude Code automatically after the requested unit's work and tests; the human does not issue a separate `Verify` command)* | Claude Code |
| Human approval | `Approve EXE-P0.n.X` | User |
| Capability Gate review (resume trigger, §18.11.6) | `Review CAPABILITY-GATE P0.n` | User |
| Capability Gate approval | `Approve CAPABILITY-GATE P0.n` | User |
| Remediation (§18.14) | `Execute REM-P0.1.A-0N` / `Approve REM-P0.1.A-0N`; from v1.0.6 also `Execute REM-P0.1.B-0N` / `Approve REM-P0.1.B-0N` (§18.14.4) | User |

**v1.1.1 qualification (§18.20; fixes a v1.1.0 drafting omission).** The table above is this subsection's original, still-governing model for a unit outside a §18.20 path, and for a path's boundary unit. For a path-internal unit (PASS-class or N/A) short of the boundary, there is no "Human approval" step and no user-issued "Next execution" command — continuation is automatic (§18.12.4, §18.20.D). "Human approval" and "Capability Gate approval" remain exactly as shown for every genuine boundary; "Next execution" remains exactly as shown for opening a new path or a new Capability.
| Next execution | `Execute EXE-P0.(n+1).A` (or the next letter) | User |

Never combine the approval and next execution into one command.

#### 18.12.7 Revised Example (illustrative, normative)

```
USER:
Execute EXE-P0.2.B

CLAUDE CODE:
- verifies prerequisites
- executes only EXE-P0.2.B
- runs tests
- performs AI verification

If successful:

AI VERIFICATION: PASS

### AI VERIFIED — AWAITING HUMAN APPROVAL
Approve EXE-P0.2.B to continue.

CLAUDE CODE STOPS.

USER reviews evidence.

USER:
Approve EXE-P0.2.B

CLAUDE CODE:
P0.2.B human approval recorded.

CLAUDE CODE STOPS.

USER:
Execute EXE-P0.2.C
```

And if the unit is blocked:

```
USER:
Execute EXE-P0.2.B

CLAUDE CODE:
AI VERIFICATION: BLOCKED

Reason:
control_plane/core/interfaces/ is missing.
ControlPlaneRequest is missing.

### AI VERIFICATION BLOCKED
Human approval is not requested.
No code committed.
No next unit authorized.
```

The second example is not hypothetical: it is the actual outcome of this repository's first `Execute EXE-P0.2.B` (under v1.0.3, where the operator elected to stop and correct the plan first — §18.14).

### 18.13 Shared Core Foundation Ownership (v1.0.4)

**Problem corrected.** Through v1.0.3, Capability 1's header Preconditions field stated that `core/interfaces/`/`core/schemas/`/`core/errors/` were "scaffolded" and that "its Sub-phase A also establishes the shared zero-dependency substrate", while Capability 2's (§18.5) and Capability 3's (§18.6) Preconditions assumed those packages already existed. But no executable unit row — neither `EXE-P0.1.A`'s nor `EXE-P0.1.B`'s Files/Modules field — actually named the foundation as a deliverable. The ownership existed only in a header field, so the executed units reasonably did not produce it (§18.14), and `EXE-P0.2.B` then had no `ControlPlaneRequest` type for `INTF-030`'s `run_baseline(request: ControlPlaneRequest)` to take. Recorded as `SOURCE-GAP-EXECPLAN-04` (§38).

**Shared Foundation Owner:**

```
Capability 1 — Token Accounting and Cost Ledger
```

specifically:

```
EXE-P0.1.A — Contract & Design
```

**Required `EXE-P0.1.A` scope.** `EXE-P0.1.A` explicitly includes the **design and minimal repository scaffolding definition** for:

```
control_plane/core/interfaces/
control_plane/core/schemas/
control_plane/core/errors/
```

and identifies the minimum shared types required by the already-defined P0 contracts, without inventing unrelated schema. At minimum, the plan accounts for the types Capability 2's `INTF-030` references and which therefore must exist before `EXE-P0.2.B`:

```
ControlPlaneRequest     — interfaces.md §2.1 (INTF-001 entrypoint schema)
OptimizationPlan        — interfaces.md §3.2
```

plus the shared error taxonomy the `core/errors/` leaf exists for (`ControlPlaneError`, `interfaces.md` §25.1–25.2). **This plan deliberately does not reproduce or pre-decide their schemas.** Both `ControlPlaneRequest` and `OptimizationPlan` reference a sizeable transitive closure of sub-types (e.g. `InstructionContext`, `ConversationContext`, `QualityRequirements`, `SecurityClassification`, `OptimizationStage`, `FallbackStrategy`, `DecisionRationale`); Claude Code must enumerate that closure from the live `interfaces.md` at execution time and verify every field verbatim before implementation. Any decision to realize a subset of that closure at P0 (rather than the full closure) is a design decision the owning unit must make explicitly — listing every deferred field/type, why it is deferred, and which consuming capability will add it — and surface for human approval, never make silently.

**Java realization of the paths.** Consistent with Capability 1's existing realization (`conventions.md` §2.1's `accounting/` leaf → `com.eaioc.controlplane.accounting.ledger`), "`control_plane/core/<leaf>/` exists" means the Java package `control_plane/src/main/java/com/eaioc/controlplane/core/<leaf>/` exists with real content (at minimum a `package-info.java` documenting the leaf's contract role, plus the types assigned to it), while Contract & Design notes live beside the future package path under `control_plane/core/` (the same convention `control_plane/accounting/ledger/LedgerEntry.md` and `control_plane/evaluation/EvaluationFramework.md` follow).

**Important boundary.** `EXE-P0.1.A` owns the shared foundation. `EXE-P0.2.B` (and every later capability) must **consume** that foundation, never create it opportunistically — a consumer unit that finds the foundation missing stops with `AI VERIFICATION: BLOCKED` (§18.15), it does not fold the missing work into its own change-set:

```
EXE-P0.1.A
    ↓
shared core foundation contract + required scaffold/design
    ↓
EXE-P0.1.B and later Capability 1 work
    ↓
Capability 1 Gate
    ↓
EXE-P0.2.A
    ↓
EXE-P0.2.B may consume the already-created foundation
```

For a fresh execution of this plan, the above is the normative order. For this repository's actual history, where `EXE-P0.1.A` has already executed without producing the foundation, §18.14 defines how the ownership is discharged after the fact.

### 18.14 Post-Hoc Execution Reconciliation (v1.0.4)

The v1.0.4 correction must not silently pretend that already-completed checkpoints have not happened, and must not rewrite historical commits. Instead it introduces **POST-HOC EXECUTION RECONCILIATION**: for an already-executed unit whose requirement changed after the fact, Claude Code compares

```
HISTORICAL EXECUTION RESULT
vs.
CORRECTED EXECUTION-PLAN REQUIREMENT
```

and records exactly one of:

```
RECONCILED: PASS
```

```
RECONCILED: GAP FOUND
```

A `GAP FOUND` result is never folded into a later unit (in particular, never into `EXE-P0.2.B`). It is closed only by an explicitly identified, separately-committed **remediation unit** that runs before the dependent unit proceeds, follows the same two-stage AI-verification-then-human-approval checkpoint as any `EXE-P0` unit (§18.12), and leaves the historical commit untouched. Remediation units are history-specific corrective checkpoints: they carry a `REM-` ID, never an `EXE-P0` ID, and are **not** counted among the 48 atomic units or the 54 gated checkpoints (§18.11.10 is unchanged).

#### 18.14.1 Reconciliation of `EXE-P0.1.A` — recorded at v1.0.4 correction time from repository evidence

| Field | Finding |
|---|---|
| Unit | `EXE-P0.1.A` — Token Accounting and Cost Ledger, Contract & Design |
| Historical commit | `a345742` ("P0-1.A: Token Accounting and Cost Ledger — Contract & Design") — explicitly approved under v1.0.3 |
| HISTORICAL EXECUTION RESULT | Produced only `control_plane/accounting/ledger/LedgerEntry.md`. That note's own §5 ("Shared-substrate scaffolding — explicitly not performed in this commit") deliberately deferred `core/interfaces/`/`core/schemas/`/`core/errors/` and the Maven scaffold, and flagged the question for human approval. `EXE-P0.1.B` (`e353dcf`) then created `control_plane/pom.xml` and the Maven layout (discharging the Maven-scaffold part) but created no `core/` package — its local exceptions (`MissingTenantIdException`, `DuplicateLedgerEntryException`) were placed in `accounting/ledger/`, explicitly *not* in a shared `core/errors/` taxonomy. As of v1.0.4, no `core/` package exists anywhere under `control_plane/src/`. |
| CORRECTED EXECUTION-PLAN REQUIREMENT | §18.4 A row and §18.13: `EXE-P0.1.A` owns the shared-core-foundation contract and minimal scaffolding definition, including `ControlPlaneRequest`, `OptimizationPlan`, and `ControlPlaneError`. |
| Result | **RECONCILED: GAP FOUND** — the historical unit satisfied its v1.0.3 row, but not the corrected ownership boundary. |
| Disposition | Historical commit and its approval are preserved unchanged. The gap is closed by remediation units `REM-P0.1.A-01` and `REM-P0.1.A-02` (§18.14.2) before Capability 1's Gate review and before `EXE-P0.2.B`. The first remediation unit's own AI Verification re-confirms this finding against the live repository rather than trusting this table. |

#### 18.14.2 Remediation units for the `EXE-P0.1.A` gap

Split into two units for the same reason every capability separates Sub-phase A from Sub-phase B (§18.2): a design decision is never committed together with the implementation that depends on it.

| Field | `REM-P0.1.A-01` — Shared Core Foundation: Contract & Design (Remediation) | `REM-P0.1.A-02` — Shared Core Foundation: Minimal Scaffold (Remediation) |
|---|---|---|
| Commands | `Execute REM-P0.1.A-01` → AI verification → `Approve REM-P0.1.A-01` | `Execute REM-P0.1.A-02` → AI verification → `Approve REM-P0.1.A-02` |
| Precondition | v1.0.4 of this plan in effect | `REM-P0.1.A-01` AI-verified **and** human-approved |
| Deliverable | Design note enumerating, verbatim from the live `interfaces.md` §2.1, §3.2, §25.1–25.2, every field of `ControlPlaneRequest`, `OptimizationPlan`, and `ControlPlaneError` and their full transitive sub-type closure; for each type/field, an explicit disposition — *realized in the P0 foundation* or *deferred to <consuming capability>* with reason; the Java package mapping (`com.eaioc.controlplane.core.interfaces`/`.schemas`/`.errors`); the dependency rule (`core/` imports nothing else from `control_plane/`, `conventions.md` §2.2); how REQUIRED fields (notably `tenant_id`) are enforced structurally at construction (root `CLAUDE.md` rule 4, mirroring `CostLedgerEntry`'s compact-constructor precedent); and the explicit decision that Capability 1's existing local exceptions are **not** migrated by this remediation | Java types exactly as `REM-P0.1.A-01` approved them, under `core/interfaces/`, `core/schemas/`, `core/errors/` (each leaf with a `package-info.java`), plus their unit tests — nothing beyond the approved design |
| Files/Modules | `control_plane/core/CoreFoundation.md` (design note only) | `control_plane/src/main/java/com/eaioc/controlplane/core/**` and `control_plane/src/test/java/com/eaioc/controlplane/core/**` only — no change to `accounting/`, `evaluation/`, `pom.xml`, or any doc |
| Tests | Schema-conformance review: every listed field diffed against the live `interfaces.md` text (not reproduced from memory) | `mvn test` — new unit tests: construction with all REQUIRED fields round-trips; every enum matches `interfaces.md` verbatim; the full pre-existing suite still passes |
| Failure-Path Test | N/A (design stage) | Constructing `ControlPlaneRequest` with a missing/blank `tenant_id` (and each other REQUIRED identity field) is rejected, never silently defaulted |
| Architecture check | Dependency direction stated | `grep` confirms no file under `core/` imports any other `com.eaioc.controlplane.*` package |
| Definition of Done | No invented field or type; every deferral explicit; subset-vs-full-closure decision surfaced for human approval in the report; any sub-type referenced but not defined in `interfaces.md` recorded as a gap (→ `AI VERIFICATION: BLOCKED` unless it can be deferred without inventing content) | Types match the approved design exactly; foundation exists at every path §18.15's guard checks |
| Commit | One remediation commit, title `P0-1.A-REM-01: Shared Core Foundation — Contract & Design (Remediation)` | One remediation commit, title `P0-1.A-REM-02: Shared Core Foundation — Minimal Scaffold (Remediation)` |

#### 18.14.3 Other already-executed units

- **Capability 1, Sub-phases B–E, G, H** (`e353dcf`, `5d8640e`, `e1730ce`, `3987957`, `95a1267`, `0c18174`) — executed under v1.0.3's single-stage model. Their requirements did not change in v1.0.4, so they are not individually reconciled or re-executed; the AI Gate Verification for Capability 1 (§18.11.6) re-examines their evidence as part of the Gate review, and any evidence that does not hold up is a Blocking Condition for the Gate, not a silent pass. **v1.0.6:** Sub-phase B's requirements did change (§18.4 B row) — see §18.14.4.
- **G/H approval evidence (v1.0.6):** the repository does not prove that `Approve EXE-P0.1.G` or `Approve EXE-P0.1.H` were ever issued. The only occurrences of those strings are syntax examples in `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.3.md`; no commit, note, or record evidences either approval. They are **not assumed**. Explicit human confirmation — or fresh `Approve EXE-P0.1.G` / `Approve EXE-P0.1.H` commands after the operator reviews the retrospective AI verification of those units — is required before the Capability 1 Gate may pass (§18.18). **v1.0.8:** resolved — the operator issued `Approve EXE-P0.1.G` and `Approve EXE-P0.1.H` on 2026-09-23 after reviewing the retrospective AI verification (§18.14.6, GS-1).
- **Capability 1, Sub-phase F** — `NOT APPLICABLE` (§18.4 F row), no commit, correctly so (§18.16). Acknowledged: `Execute EXE-P0.1.F` returned `AI VERIFICATION: NOT APPLICABLE` (verified against `quality-gates.md` §2, §3 and the QG-010 entry), and `Approve EXE-P0.1.F` was issued.
- **Capability 2, Sub-phase A** (`0e92261`) — executed and approved. The repository contains no record of `Approve CAPABILITY-GATE P0.1` having been issued, so `EXE-P0.2.A` may have run before Capability 1's Gate closed, which §18.11.8 does not permit. Content reconciliation: **RECONCILED: PASS** — the design note is documentation-only, cites `INTF-030` verbatim (whitespace-insensitive diff against `interfaces.md` §18 shows no difference), and does not depend on the shared foundation existing. Its approval is preserved and it is not re-executed; the sequencing irregularity is instead neutralized by §18.15's guard, which requires the Capability 1 Gate to be approved before `EXE-P0.2.B` may proceed, and requires the remediated `ControlPlaneRequest`/`OptimizationPlan` types to match the parameter types that note cites. **v1.0.8:** `Approve CAPABILITY-GATE P0.1` has since been issued (2026-09-23, §18.14.6); the sequencing irregularity remains recorded as history.

#### 18.14.4 Reconciliation of `EXE-P0.1.B` and the B remediation sequence (v1.0.6)

**Source-contract resolution (recorded):**

```
AC-005 contract direction resolved.
INTF-047 must represent the complete §27.1 standard ledger field model.
Canonical representation: nested/category-preserving.
Unsupported field equivalence is prohibited.
Availability semantics are preserved.
Unresolved types are explicitly identified rather than invented.
```

Basis:
- **The requirement:** PS §8 ("Required fields include"), SPEC §18.1 ("Required Ledger Fields"), and `architecture.md` §27.1 (58 standard fields in nine groups).
- **The binding to the record:** `conventions.md` §14.1 ("Every request must produce a `CostLedgerEntry` carrying all standard ledger fields defined in ARCH §27.1").
- **Precedence:** `conventions.md`'s header — Problem Statement > Engineering Specification > Architecture > Interfaces.

Human-approved decisions applied in `interfaces.md` §28.1 and `conventions.md` §14.1:
- **D-1 — nested groups:** the nine §27.1 groups are nested and keep their categories.
- **D-2 — no inferred equivalence:** no existing `INTF-047` field is aliased or declared equivalent to a §27.1 field; all 29 existing fields are retained.
- **D-3 — availability:** "where available" members are `| null` per `conventions.md` §5.4; the availability/`unverified` interaction is an open decision (`SOURCE-GAP-EXECPLAN-06`).
- **D-4 — types:** types are stated only where `conventions.md` §3.6 / §5.2 establish them; 22 members are typed `SOURCE-UNRESOLVED` (`SOURCE-GAP-EXECPLAN-05`).

**Contradiction recorded — `CONTRA-EXECPLAN-01`:** `INTF-047` did not represent the full §27.1 ledger model. This is a frozen-source contract inconsistency between `interfaces.md` §28.1 and PS §8 / SPEC §18.1 / `architecture.md` §27.1 / `conventions.md` §14.1 — not merely an interface defect. It was resolved under the precedence documented in `conventions.md`'s header, by the v1.0.6 correction of `interfaces.md` §28.1 (§38).

| Field | Finding |
|---|---|
| Unit | `EXE-P0.1.B` — Token Accounting and Cost Ledger, Minimal Implementation |
| Historical commit | `e353dcf` — executed under v1.0.3; its evidence was re-examined retrospectively |
| HISTORICAL EXECUTION RESULT | `CostLedgerEntry` implements the pre-correction `INTF-047` (29 fields + `verified`). Tenant scoping, append-only store, round-trip and missing-tenant failure path all hold (`CostLedgerStoreTest` 7/7) |
| CORRECTED EXECUTION-PLAN REQUIREMENT | §18.4 B row v1.0.6 additions (1)–(7) |
| Result | **RECONCILED: GAP FOUND** — no §27.1 canonical group is represented, so AC-005's compressed, retrieved and reused categories have no field at all |
| Disposition | Historical commit and its evidence are preserved unchanged. The gap is closed only through the two remediation units below, before Capability 1's Gate. |

**Formally assigned remediation units.** These IDs follow the `REM-P0.<n>.<letter>-<NN>` convention of `REM-P0.1.A-01`/`-02`. Any earlier mention of `REM-P0.1.B-01` had no status; the IDs exist from this revision onward. They are outside the 48/54 counts (§18.11.10).

| Field | `REM-P0.1.B-01` — Contract / Design Reconciliation | `REM-P0.1.B-02` — Minimal Implementation Remediation |
|---|---|---|
| Commands | `Execute REM-P0.1.B-01` → AI Verification → `Approve REM-P0.1.B-01` | `Execute REM-P0.1.B-02` → AI Verification → `Approve REM-P0.1.B-02` |
| Precondition | v1.0.6 of this plan accepted; corrected `interfaces.md` §28.1 and `conventions.md` §14.1 committed | `REM-P0.1.B-01` AI-verified **and** human-approved |
| Deliverable | A design artifact based strictly on the corrected `INTF-047`. It specifies: the Java realization of the nine nested groups and all 58 members, with the 29 existing fields retained unchanged; how `verified`/`unverifiedFallback()` extend to the new groups without fabricating values; and the contract tests `REM-P0.1.B-02` must pass. **For each of the 22 `SOURCE-UNRESOLVED` members (`SOURCE-GAP-EXECPLAN-05`) and for `SOURCE-GAP-EXECPLAN-06`, it presents the open question for an explicit human decision and invents nothing.** Any type or behavior the human decides must also be applied to `interfaces.md` §28.1 through a user-directed source correction before `REM-P0.1.B-02` begins | Implement only the approved design: extend `CostLedgerEntry` with the nested groups; update every construction site; add the contract tests. Nothing beyond the approved design |
| Files/Modules | Design note under `control_plane/accounting/ledger/` only | `control_plane/src/main/java/com/eaioc/controlplane/accounting/ledger/**` and `control_plane/src/test/java/com/eaioc/controlplane/accounting/ledger/**` only |
| Tests | Contract-conformance review of every member against corrected `interfaces.md` §28.1 | `mvn test`: new contract tests (nine groups, 58 members, six AC-005 categories distinctly recorded, retained fields unchanged, no alias); all pre-existing tests pass |
| Failure-Path Test | N/A (design stage) | A missing/blank `tenant_id` is still rejected; an accounting-computation failure still yields a `verified=false` fallback entry without fabricated values; a "where available" member left `null` is not converted to zero |
| Blocking rule | `AI VERIFICATION: BLOCKED` if any `SOURCE-UNRESOLVED` type or `SOURCE-GAP-EXECPLAN-06` behavior would have to be assumed rather than decided | `AI VERIFICATION: BLOCKED` if the implementation departs from the approved design or the corrected contract |
| Commit | One remediation commit: `P0-1.B-REM-01: Token Accounting and Cost Ledger — Contract / Design Reconciliation (Remediation)` | One remediation commit: `P0-1.B-REM-02: Token Accounting and Cost Ledger — Minimal Implementation (Remediation)` |

No automatic chaining applies between these units, as everywhere (§18.11.12).

**Gate prerequisites preserved:** Capability 1 Gate = **BLOCKED** and `EXE-P0.2.B` = **BLOCKED** until `REM-P0.1.B-01` and `REM-P0.1.B-02` are AI-verified and human-approved, the G/H approval evidence is resolved (§18.14.3), and the Capability 1 AI Gate Verification passes followed by `Approve CAPABILITY-GATE P0.1`.

#### 18.14.5 Promotion of `REM-P0.1.B-01` decisions into the contract — DB-2 (v1.0.7)

**`REM-P0.1.B-01` status:** completed, AI-verified (`PASS WITH DOCUMENTED NON-BLOCKING GAP`), and human-approved — commit `34dd1f1`, design note `control_plane/accounting/ledger/LedgerContractReconciliation.md`.

**Human-operator decisions made in that unit** (explicit decisions, not source-derived):

| ID | Decision |
|---|---|
| HD-1 | 13 counters (`cache.exact_hits`/`semantic_hits`/`misses`/`writes`/`reads`, `tools.calls_attempted`/`calls_avoided`/`cached_calls`, `workflow.steps_planned`/`steps_executed`/`steps_skipped`/`early_exits`/`retries`) = `integer`; a measured zero is a legitimate measured value |
| HD-2 | `model.selected`, `model.candidate`, `model.routing_decision`, `model.reasoning_budget` = `string` (no enum invented; `OptimizationPlan.reasoning_budget`'s enum not reused); `model.escalation` = `boolean` |
| HD-3 | `performance.ttft_ms` = `integer` ms; `quality.semantic_preservation` = `float` in `[0.0, 1.0]`; `quality.schema_compliance`, `quality.safety_validation` = `boolean` |
| HD-4 | Only `model.reasoning_tokens` and `cost.tool` are nullable; a `null` there ≠ `unverified`. `unverified` is reserved for an accounting failure (`conventions.md` §14.4) or a measurement verification failure (a non-nullable member that cannot be measured) |

**Correction and design identifiers:**
- **DB-1** — the placeholder rule, from the approved design note §4, **unchanged**: an unmeasurable non-nullable member may hold `0`/`0.0`/`false`/`"unknown"` only in an entry marked `verified = false`, and is never presented or counted as a measured value.
- **DB-2** — the promotion of HD-1 to HD-4 (and the DB-1 rule) from `REM-P0.1.B-01` into `interfaces.md` §28.1 and `conventions.md` §14.1. This is the source-contract correction §18.14.4 requires before `REM-P0.1.B-02`.

DB-2 uses a new ID because DB-1 already names the approved placeholder policy (an operator decision in this correction).

**Status after DB-2:** `SOURCE-GAP-EXECPLAN-05` and `-06` are **resolved** (§38).
- **`REM-P0.1.B-02` remains NOT EXECUTED.** It becomes eligible to be re-issued (`Execute REM-P0.1.B-02`) once this correction is committed, subject to the normal protocol: AI verification, then explicit human approval, with no automatic chaining.
- **Capability 1 Gate remains BLOCKED** until `REM-P0.1.B-02` is executed, AI-verified and human-approved, the G/H approval evidence is resolved, and the Gate passes.
- **`EXE-P0.2.B` remains BLOCKED.**

*v1.0.8 note:* the three status bullets above describe the v1.0.7 position and are retained as history. `REM-P0.1.B-02` has since been executed (`c378dc9`), AI-verified and approved, and Capability Gate P0.1 approved (2026-09-23). `EXE-P0.2.B` is now blocked for a different reason: `SOURCE-GAP-EXECPLAN-07`/`SOURCE-GAP-EXECPLAN-08` (§18.14.6).

#### 18.14.6 `EXE-P0.2.B` design-gap reconciliation, HQ decisions, and gate-state synchronization (v1.0.8)

**Unit status.** `EXE-P0.2.B` was issued and returned `AI VERIFICATION: BLOCKED` each time, with no code and no commit. The §18.15 guard passed. The blocker is a design/contract gap in INTF-030 at P0, identified by the read-only Code Architect assessment (verdict `BLOCKED`) and a read-only design-gap analysis. The unit has not run and is not reconciled as executed.

**Human-operator decisions (2026-09-24; explicit decisions, not source-derived):**

| ID | Decision |
|---|---|
| HQ-1 | No new P0 measurement source is created. `run_baseline()` may consume an existing authoritative measurement only where the corpus already defines **both** an explicit field mapping to the `EvaluationRun` field **and** a request-to-measurement retrieval contract. CostLedger (INTF-047) is authoritative, but no field mapping and no request-to-ledger-entry lookup is invented. Observability is **not** a dependency of `EXE-P0.2.B`: the Observability P0 item is outside this execution slice (`SOURCE-GAP-IRG-02`; this document's scope statement). Missing mappings and retrieval contracts are recorded as `SOURCE-GAP-EXECPLAN-07`. |
| HQ-2 | A BASELINE-only P0 run populates only values the existing `EvaluationRun` contract can legitimately represent **and** that have an authoritative measurement source. No `verified` field is added; no field is made nullable for P0; no zero, placeholder, or sentinel stands in for an unavailable value; the ledger's DB-1/HD-4 policy is not projected onto `EvaluationRun`. A non-nullable field that cannot be honestly populated is a contract/design gap, recorded as `SOURCE-GAP-EXECPLAN-08`. INTF-030 is not modified for P0 convenience. |
| HQ-3 | P0 has no optimization stage. `run_optimized()` remains in INTF-030 as the forward contract and is deferred. `EXE-P0.2.B` implements the baseline operational path only and does not operationally exercise `run_optimized()`. No `UnsupportedOperationException` or other invented runtime behavior is added to satisfy the declaration. It becomes active when a later capability provides an optimized execution path. |
| HQ-4 | `compare()` is deferred until an actual optimized execution path exists and both BASELINE and OPTIMIZED runs are available; it is not part of `EXE-P0.2.B`. `report_regression()` is deferred; `RegressionReport` is not invented; `SOURCE-GAP-EVAL-01` remains open. |
| HQ-5 | Narrow ownership clarification: `EvaluationRun` and `EvaluationComparison` are evaluation-owned schemas under `control_plane/evaluation/`, consistent with `conventions.md` §2.1's `evaluation/  # ARCH §32; INTF §18` entry. This is not a global reinterpretation of §2.1 and moves no other capability-owned schema (`CostLedgerEntry` stays in `accounting`). |
| HQ-6 | `run_baseline()` is **not** idempotent by `request_id`. `request_id` identifies the evaluated request; `run_id` identifies an individual evaluation execution; multiple baseline executions for the same `request_id` are permitted and produce distinct `run_id` values. Recorded in `interfaces.md` §26.1, where the `ControlPlaneRequest` row governs request processing, not evaluation runs. |
| GS-1 | Synchronize the stale Capability Gate P0.1 records with the actual approved state from the 2026-09-23 session (below). Historical records are not rewritten. |

**Consequence (recorded, not resolved).** With HQ-1 and HQ-2 applied, none of `EvaluationRun`'s non-nullable baseline measurement fields (`baseline_cost`, `latency_ms_baseline`, `quality_score_baseline`, `task_success_baseline`, `cache_hit_rate`, `tokens_avoided_by_cache`) has both a mapping and a retrieval contract, and the non-nullable comparison-dependent fields (`gross_savings`, `optimizer_overhead`, `net_savings`, `net_savings_pct`, `quality_delta`, `latency_delta_ms`, `regression_detected`, `regression_dimensions`) have no BASELINE-only representation. Only the identity fields (`run_id`, `request_id`, `run_type = BASELINE`, `timestamp`) could be populated. A complete, correctly-typed `EvaluationRun` — the §18.5 B row's Definition of Done — therefore cannot be produced honestly at P0. **`EXE-P0.2.B` remains BLOCKED** on `SOURCE-GAP-EXECPLAN-07`/`SOURCE-GAP-EXECPLAN-08`. No unit is assigned here to close them. Closing them requires a further user-directed source-contract correction that defines the mappings and a retrieval contract, and a BASELINE representation for the non-nullable fields; `EXE-P0.2.B` may then be re-issued.

*v1.0.10 note:* the sentence "No unit is assigned here to close them" describes the v1.0.8 position and is retained as history. The remediation that prepares their closure is now registered as `REM-P0.2.B-01` (§18.14.7). The source-contract correction that actually closes them is still required after that unit.

*v1.0.11 note (DB-3, §18.14.8):* HQ-2's closing clause "INTF-030 is not modified for P0 convenience" is revised by operator decision D-B: `run_baseline()` returns a new `BaselineEvaluationRecord`, and INTF-030's text and inventory count change accordingly (`interfaces.md` §18, §36). The rest of HQ-2 stands. HQ-1 is not revised: the mapping and retrieval contract it requires are now declared by the operator and promoted. HQ-3/HQ-4 stand; the operator's 2026-09-25 decision not to declare `run_optimized()`/`compare()` in Java narrows D-C's "second interface" clause (recorded in `interfaces.md` §18). HQ-5 and HQ-6 stand. The v1.0.8 "Consequence" paragraph and the "No unit is assigned here" sentence above are retained as history.

**Settled by documented precedence (recorded, no decision needed).** `EvaluationRun` carries no `schema_version` or `currency` field, which `conventions.md` §4.3/§5.1/§5.2 would otherwise require. By `conventions.md`'s header precedence (Interfaces > Conventions), INTF-030 applies as written.

**Gate-state synchronization (GS-1).** On 2026-09-23 the operator issued, in order: `Approve REM-P0.1.B-02` (after `AI VERIFICATION: PASS`; commit `c378dc9`), `Approve EXE-P0.1.G`, `Approve EXE-P0.1.H`, `Review CAPABILITY-GATE P0.1` (`AI CAPABILITY GATE VERIFICATION: PASS`), and `Approve CAPABILITY-GATE P0.1`. **Capability 1 is closed.** The first repository record of these approvals was root `CLAUDE.md` (`4db5921`); this correction records them in the plan. Statements in §18.14.3–§18.14.5 and the v1.0.4–v1.0.7 self-checks that describe them as pending are historical and retained.

#### 18.14.7 `EXE-P0.2.B` blocker remediation — `REM-P0.2.B-01` (v1.0.10)

**Why a remediation unit, although `EXE-P0.2.B` never ran.** §18.14.1–§18.14.4 created remediation units to close a `RECONCILED: GAP FOUND` on an already-executed unit. `EXE-P0.2.B` has not run and is not reconciled (§18.14.6). v1.0.10 therefore extends §18.14's remediation mechanism to exactly one further case: a contract/design remediation that prepares closure of a registered **blocking** source gap before a not-yet-executed unit may proceed. Everything else in §18.14 applies unchanged:
- a `REM-` ID, never an `EXE-P0` ID;
- one separately committed change-set;
- the two-stage AI-verification-then-human-approval checkpoint (§18.12);
- no automatic chaining (§18.11.12);
- outside the 48 atomic units and the 54 gated checkpoints (§18.11.10).

This paragraph authorizes no other pre-execution remediation.

**Canonical ID.** The convention is `REM-P0.<n>.<letter>-<NN>` (§18.14.2, §18.14.4): capability `2`, blocked unit's sub-phase `B`, first remediation for that unit `01` → **`REM-P0.2.B-01`**. No `REM-P0.2.*` ID existed in the plan, the runbook, or any other document before this revision. The name `REM-P0.2.B-DESIGN-01`, used in the 2026-09-24 operator session and in the pre-decision record's heading, does not follow the convention and has no status. It is not a valid command (§18.11.9); `REM-P0.2.B-01` is its canonical registration.

**Design seed, not evidence.** `control_plane/evaluation/EvaluationMeasurementReconciliation.md` (commit `8b68239`, a `docs:` commit) is a pre-decision source-analysis record.
- It is not unit evidence (§18.16), and its content is not approved.
- This registration adopts none of its classifications, candidate mappings, or options.
- The unit re-verifies it against the live sources and may revise it. The unit's own commit, not `8b68239`, is its evidence.

| Field | `REM-P0.2.B-01` — Baseline Benchmark Harness + Quality Evaluation: Contract / Design Reconciliation (Remediation) |
|---|---|
| Commands | `Execute REM-P0.2.B-01` → AI Verification → `Approve REM-P0.2.B-01` |
| Precondition | (1) This v1.0.10 correction accepted and committed. (2) Capability Gate P0.1 approved (2026-09-23, §18.14.6 GS-1). (3) `EXE-P0.2.A` approved (`0e92261`, §18.14.3). (4) The operator decisions HQ-1 to HQ-6 recorded (§18.14.6). Each is re-verified in pre-flight (§18.19.3) against the repository and the approval record, not trusted from this row |
| Relationship to gaps | Exists to prepare closure of `SOURCE-GAP-EXECPLAN-07` (baseline measurement acquisition and retrieval) and `SOURCE-GAP-EXECPLAN-08` (BASELINE-only representation of `EvaluationRun`'s non-nullable fields), §38. **It does not close either gap by itself** (see the sequence below) |
| Deliverable | A design artifact that investigates four decision areas from the authoritative sources. **None is pre-decided by this registration.** For each area, the artifact states what the sources establish. Where they do not, it presents the options and the documents each option would change, for an explicit human decision. It invents nothing. Any option that would revise HQ-1 to HQ-6 is presented as a revision of that operator decision, never as a default. **D-A:** how baseline evaluation measurements are acquired, and who owns that responsibility. **D-B:** how `EvaluationRun` represents a BASELINE-only execution without fabricating comparison-derived values. **D-C:** how INTF-030 `EvaluationFramework` is realized in Java where the sources leave implementation ownership ambiguous. **D-D:** any remaining contract decision needed to close the blocking gaps, including the relationship to the current evaluation and observability contracts. The seed record's cache-applicability question belongs here |
| Files/Modules | `control_plane/evaluation/` design note only (the seed record, re-verified and revised). No change to `interfaces.md`, `conventions.md`, `eval.md`, this plan, the runbook, Java source, tests, or `pom.xml` |
| Tests | Contract-conformance review: every `EvaluationRun` field diffed against the live `interfaces.md` §18 (INTF-030) and `eval.md` §16, not reproduced from memory |
| Failure-Path Test | N/A (design stage), as for `REM-P0.1.B-01` |
| Review / verification | Full §18.12.1 checklist (A–K) and the §18.12.3 report. Routing per the `eaioc-agent-orchestration` skill: a remediation design note that defines contract semantics is AUTHORITATIVE-DOC (class 1a). `eaioc-code-architect` and `eaioc-code-verifier` are mandatory at STEP level before the verdict. `eaioc-code-reviewer` runs only if implementation files change, and none may. A GOVERNANCE RECORD annex is required (§18.19) |
| Definition of Done | (1) Every affected `EvaluationRun` field is classified, with a cited source. (2) Every proposed field mapping cites an authoritative source; one without a source stays `SOURCE-UNRESOLVED` and is not adopted. (3) No alias or equivalence is declared on name similarity (the §18.14.4 D-2 rule, applied here). (4) Baseline measurement acquisition responsibility (D-A) is source-defined, or marked `SOURCE-UNRESOLVED` and escalated as a human decision. (5) BASELINE-only `EvaluationRun` semantics (D-B) are source-defined or escalated. (6) `run_optimized()` semantics are identified from authoritative sources (e.g. HQ-3, `implementation-plan.md` §8.2) or escalated. (7) Java interface ownership and realization ambiguity (D-C) is resolved from sources or escalated. (8) No Observability dependency is invented (HQ-1; `SOURCE-GAP-IRG-02`). (9) No placeholder measurement value is invented: no zero, `false`, empty string, or sentinel (HQ-2). (10) `SOURCE-GAP-EVAL-01` stays open unless authoritative evidence independently closes it; `RegressionReport` is not invented. (11) Every human decision taken in the unit is listed with the authoritative documents and sections it requires to change. Those changes are promoted by a separate user-directed source-contract correction (the §18.14.5 DB-2 precedent), not by this unit's commit. (12) The Architect and Verifier reviews are completed, and their findings reflected. (13) AI Verification is `PASS` or `PASS WITH DOCUMENTED NON-BLOCKING GAP`. (14) Human approval is recorded (`Approve REM-P0.2.B-01`) |
| Blocking rule | `AI VERIFICATION: BLOCKED` in any of these cases: a decision would have to be assumed rather than made by the human; a mapping, value, or semantics would be invented; or any file outside the Files/Modules scope changes |
| Commit | One remediation commit: `P0-2.B-REM-01: Baseline Benchmark Harness + Quality Evaluation — Contract / Design Reconciliation (Remediation)` |
| Approval | Explicit `Approve REM-P0.2.B-01`, valid only after a satisfactory AI verdict. It approves this remediation only. It is **not** approval, eligibility, or authorization of `EXE-P0.2.B` |

**Dependency relationship (authoritative):**

```
v1.0.10 correction committed (this registration)
        ↓
Execute REM-P0.2.B-01 → AI Verification (Architect + Verifier) → Approve REM-P0.2.B-01
        ↓   (human decisions D-A..D-D are taken inside the unit, never by the AI)
user-directed source-contract correction promoting those decisions (DB-2 precedent)
        ↓   closes — or narrows and records — SOURCE-GAP-EXECPLAN-07/-08 (§38)
revalidation of EXE-P0.2.B: Execute EXE-P0.2.B → §18.15 guard → pre-flight
        (incl. -07/-08 status) → Code Architect re-assessment → AI Verification → Approve EXE-P0.2.B
```

`EXE-P0.2.B` becomes eligible only if `-07`/`-08` are closed at source **and** every other prerequisite passes at its own pre-flight. If the decisions require a further unit (for example, one that delivers a measurement producer), that unit must be registered by the source-contract correction before `EXE-P0.2.B`. This registration assigns no such unit.

**Status after v1.0.10:**
- `REM-P0.2.B-01`: **REGISTERED / NOT STARTED**. It is eligible to be issued once this correction is committed.
- `SOURCE-GAP-EXECPLAN-07`: **OPEN**.
- `SOURCE-GAP-EXECPLAN-08`: **OPEN**.
- `EXE-P0.2.B`: **BLOCKED**.

#### 18.14.8 Promotion of `REM-P0.2.B-01` decisions into the contract — DB-3; registration of `REM-P0.2.B-02` and `REM-P0.2.B-03` (v1.0.11)

**`REM-P0.2.B-01` status.** Executed and committed as `e4cf1f5` (one file: `control_plane/evaluation/EvaluationMeasurementReconciliation.md`; commit trailers `Governance-Agents: architect=PASS(conditions met) reviewer=NOT_REQUIRED verifier=PASS(documented gaps; F1-F4 resolved)`), AI-verified (`PASS WITH DOCUMENTED NON-BLOCKING GAP`, as recorded in the operator's session and root `CLAUDE.md`), and **human-approved by the operator in-session on 2026-09-24**. The first repository record of that approval was root `CLAUDE.md` (uncommitted at the time of this correction); this section records it in the plan (GS-1 precedent, §18.14.6). It approves the remediation only and does not approve or authorize `EXE-P0.2.B`. The design note is a Tier-5 unit artifact: it binds that unit's decisions, does not override Tier 1–3 documents, and is not rewritten here; two points where this correction differs from it are stated below. The "Status after v1.0.10" bullets in §18.14.7 are the v1.0.10 position, retained as history; "Status after v1.0.11" below supersedes them.

**Decisions recorded (explicit operator decisions; not source-derived).**

| Decision | Recorded decision | Where represented |
|---|---|---|
| D-A (2026-09-24, note §1) | Ledger mapping plus a producer: baseline measurements come from the CostLedger (INTF-047) through declared field mappings and a request-to-entry retrieval contract; only `verified = true` entries are consumed; `accounting/` owns the retrieval realization and a producer unit owns delivering Path A measurements into the ledger; both units are registered. Does not revise HQ-1 | `interfaces.md` §18, §28.1, §28.2, §40.2 notes; this section |
| D-B † (2026-09-24) | A distinct baseline-only record is returned by `run_baseline()`; `EvaluationRun` keeps comparison semantics; revises HQ-2's clause "INTF-030 is not modified for P0 convenience" only | `interfaces.md` §18 schema and notes, §24, §36; §18.14.6 note |
| D-C (2026-09-24) | Baseline interface and schemas in `evaluation/`, runner in `benchmarking/`; `report_regression()` undeclared. **Narrowed** by the 2026-09-25 decision below: no second interface is declared at P0 | `interfaces.md` §18 notes; §18.5 B row |
| D-D (2026-09-24) | Cache effects not applicable to a pure BASELINE run; the §40.2 Observability line is a slice-scope tension under HQ-1; quality/task producer and `SOURCE-GAP-EXECPLAN-09` carried forward | `interfaces.md` §18 notes and §40.2; `eval.md` notes |
| Operator decisions (2026-09-25) | (a) `EXE-P0.2.B` has a contract-level DoD with a clearly labelled test-fixture entry and claims no live measurement; the producer unit is not a prerequisite of it; live measurement is explicitly deferred at Gate P0.2. (b) For one `(tenant_id, request_id)`, exactly one ledger entry is a measurement; zero or several mean no measurement (the count basis is fixed by decision (i-1) below). (c) `baseline_cost` <- the retained ledger `baseline_cost` (human-declared mapping, not source-derived). (d) `latency_ms_baseline` <- `performance.e2e_latency_ms`. (e) Canonical name `BaselineEvaluationRecord`; it carries the identity fields `run_id`, `request_id`, `timestamp` and `run_type`, and `tenant_id`, `currency`, `schema_version`, `source_entry_id`, `baseline_cost` and `latency_ms_baseline`. (f) `run_optimized()`/`compare()` are not declared in Java at P0 and stay documented in INTF-030; the fallback `compare(baselineRecord, EvaluationRun)` was not chosen. (g) Authorized files for this correction: `docs/interfaces.md`, `docs/eval.md`, `docs/implementation-plan.md`, `docs/execution-plan.md`, `docs/execution-plan-p0-steps.md` (the operator runbook), root `CLAUDE.md` (governance state only). (h) **Human contract decisions dated 2026-09-25, not established by any source:** the initial `schema_version` of `BaselineEvaluationRecord` is `1.0.0`; the source-entry field is `source_entry_id: string`, identifying the `CostLedgerEntry` (its `entry_id`) from which the measurement was constructed; the record carries `run_type` with the existing `EvaluationRun` semantic (`BASELINE`, `OPTIMIZED`, `SHADOW`) and the value `BASELINE` for every record produced by `EXE-P0.2.B`. (i) **Human contract decisions dated 2026-09-25 (after the pre-commit review), not established by any source:** (i-1) *count basis, contract semantics — count-all-then-verify:* the retrieval identifies all ledger entries matching `(tenant_id, request_id)` and determines cardinality across all of them; exactly one matching entry is consumed, only when `verified = true`; zero or more than one means no measurement, so a verified entry plus an unverified one is *several* and yields no measurement (this count basis is not established by any frozen source; it is an explicit human contract decision; fail-closed behaviour preserved); (i-2) *no-measurement outcome — operation failure:* when a valid baseline measurement cannot be acquired, `run_baseline()` fails without returning a `BaselineEvaluationRecord` (declared return stays non-null; no nullable record, no outcome wrapper, no new `ControlPlaneError` code or `error_class` value, no `PartialSuccess`, no other error schema); the concrete failure mechanism is a unit-level concern until an authoritative error-transport contract exists; (i-3) *versioning:* the `interfaces.md` document Version stays 1.2.0 with an explicit amendment-history and migration statement; no 1.3.0 or 2.0.0; no INTF-030 schema version is invented; §1.3 and §24.1 are unchanged; "no existing consumer" is an inference, not an authoritative exemption | `interfaces.md` §18, §24; §18.5, §18.10; this section |

**Provenance of the `BaselineEvaluationRecord` fields.** *Authoritative source (name and type):* `run_id`, `request_id`, `run_type`, `timestamp`, `baseline_cost`, `latency_ms_baseline` (`EvaluationRun`, `interfaces.md` §18); `tenant_id` (`conventions.md` §5.1); `schema_version` (`conventions.md` §4.3, §5.1); `currency` (`interfaces.md` §28.1; `conventions.md` §3.6). *Operator decision (mapping or inclusion):* `baseline_cost` and `latency_ms_baseline` mappings; inclusion of the identity fields, `tenant_id`, `currency`, `schema_version` and a field identifying the source ledger entry (whose name, `source_entry_id`, is a human contract decision below). *Human contract decisions of 2026-09-25 (not source-derived):* the `schema_version` value `1.0.0` (the sources establish only the versioning rules, `conventions.md` §4.3/§4.4 and `interfaces.md` §1.3, §24.1–§24.4, and the `1.0.x` compatibility floor), the name and type `source_entry_id: string`, and the carrying of `run_type` (the design note `EvaluationMeasurementReconciliation.md` §3, line 78, already treats `run_type` as an identity field). *Derived consequences:* the record's `request_id`, `tenant_id` and `currency` follow from the retrieval by `(tenant_id, request_id)` and the `baseline_cost` mapping (`interfaces.md` §18 notes); `run_id` and `timestamp` belong to the execution of `run_baseline()` (§26.1; `conventions.md` §5.1).

**Not decided (recorded).** The error class, error code and concrete failure mechanism of the no-measurement outcome — the outcome itself is decided (decision (i-2): the operation fails without a result); the mechanism is a unit-level concern until an authoritative error-transport contract exists (`SOURCE-GAP-EXECPLAN-13`, narrowed); a `verified` field in the INTF-047 schema block (not authorized; `-14`); inapplicable-versus-unmeasurable non-nullable ledger members (`-12`); the quality/task-success producer (`-11`); the Path A capture mechanism and the plan §17 overhead comparison (`-15`); the reconciliation of `compare()`'s `baseline` parameter (`-10`).

**DB-3.** The promotion of the decisions above from `REM-P0.2.B-01` into `interfaces.md` (with notes in `eval.md` and `implementation-plan.md`). DB-1 (the placeholder rule) and DB-2 are unchanged; DB-3 is the next free label. Like DB-2, it is the source-contract correction that §18.14.7 required before `EXE-P0.2.B` (§18.14.7 dependency diagram).

**Extension of §18.14's pre-execution mechanism.** §18.14.7 states that it "authorizes no other pre-execution remediation" and that "that unit must be registered by the source-contract correction before `EXE-P0.2.B`. This registration assigns no such unit." Those sentences describe the v1.0.10 registration and are retained as history. This v1.0.11 correction is the source-contract correction they name, and it extends the §18.14 remediation mechanism, explicitly and only, to two further pre-execution units: `REM-P0.2.B-02` and `REM-P0.2.B-03`. Each is a `REM-` ID under the `REM-P0.<n>.<letter>-<NN>` convention (§18.14.7), outside the 48 units and 54 checkpoints (§18.11.10), with the two-stage AI-verification-then-human-approval checkpoint (§18.12) and no automatic chaining (§18.11.12). Registration is authorized; execution is not. `REM-P0.2.B-02` changes files under `control_plane/accounting/`, part of Capability 1, which closed at Gate P0.1 (2026-09-23). That Gate approval is not revoked or reopened: the unit is a pre-execution remediation for `EXE-P0.2.B`, and its verification must confirm that Capability 1's ledger contract and tests are unchanged and green. This paragraph authorizes no other unit.

**`REM-P0.2.B-02` — Cost Ledger Request-to-Measurement Retrieval — Minimal Implementation (Remediation)**

| Field | Value |
|---|---|
| Commands | `Execute REM-P0.2.B-02` → AI Verification → `Approve REM-P0.2.B-02` |
| Precondition | (1) This v1.0.11 correction committed. (2) `REM-P0.2.B-01` approved (`e4cf1f5`; recorded above). (3) Capability Gate P0.1 approved (2026-09-23). Each is re-verified at pre-flight (§18.19.3) |
| Relationship to gaps | Realizes the retrieval contract whose definition (`interfaces.md` §18, §28.2) resolves `SOURCE-GAP-EXECPLAN-07` at source. Closes no gap by itself |
| Deliverable | In `control_plane/accounting/`, a tenant-first request-to-entry retrieval that realizes only the `get_request_cost(request_id, tenant_id)` subset of INTF-048 (`interfaces.md` §28.2) under the rule in the `interfaces.md` §18 notes: exactly one entry for `(tenant_id, request_id)` is a measurement; none or several is no measurement (the unit never chooses among several and never selects the most recent); **the count is taken over all matching entries, whatever their `verified` value, and the `verified = true` test is applied afterwards to the single matching entry (count-all-then-verify — a human contract decision of 2026-09-25)**; only a single matching `verified = true` entry is yielded to a baseline consumer. The interface's placement and the enforcement point of the verified-only condition are decided in the unit's Architect step within these constraints, without changing INTF-048's `CostLedgerEntry \| null` signature. The other three `CostReporter` methods, and their undefined types (`SessionCostSummary`, `TenantCostSummary`, `DateRange`), are not realized. *No matching entry*, *multiple matching entries* and *a single matching entry that is not `verified = true`* are documented distinctions only: each yields no measurement, and no new public error, error code or schema is introduced; how the unit tells them apart internally is its own enforcement mechanism |
| Files/Modules | `control_plane/src/main/java/com/eaioc/controlplane/accounting/**` and its tests only. Additions only: the existing `CostLedgerStore.write`/`read(tenantId, entryId)` behavior (append-only, tenant-first, duplicate rejection) and every `CostLedgerEntry` component are unchanged. No change to `core/`, `evaluation/`, `benchmarking/`, `pom.xml` or any document |
| Tests | Unit tests: a single matching entry that is `verified = true` is returned; another tenant's key returns none; an entry belonging to another tenant with the same `request_id` does not contribute to the cardinality for the requested tenant (it neither makes one matching entry several nor counts as a match); zero entries return none; two entries for one `(tenant_id, request_id)` return none whatever their `verified` values and are never resolved by picking one, including a `verified = true` entry plus a `verified = false` one (several, so none); a single matching `verified = false` entry is not yielded as a measurement; every pre-existing Capability 1 test is unchanged and green (`mvn clean test`) |
| Failure-Path Test | The zero-entry, several-entry (count-all-then-verify: a verified entry plus an unverified one is several and returns none), single-unverified-entry and cross-tenant cases above |
| Review / verification | Full §18.12.1 checklist and §18.12.3 report. Routing per the `eaioc-agent-orchestration` skill: PRODUCTION-CODE + API/INTERFACE/CONTRACT + DATA/PERSISTENCE + SECURITY (tenant isolation): Architect (new public interface, cross-package dependency), Reviewer and Verifier at STEP level, with a GOVERNANCE RECORD annex |
| Definition of Done | (1) Retrieval exists per the rule. (2) Tenant isolation is structural. (3) No fabricated, placeholder or sentinel value. (4) INTF-048's signature semantics unchanged; no `verified` field added to INTF-047. (5) Existing tests unchanged and green. (6) No claim of a live measurement (the unit retrieves; nothing produces entries). (7) AI Verification `PASS` or `PASS WITH DOCUMENTED NON-BLOCKING GAP`. (8) Human approval |
| Blocking rule | `AI VERIFICATION: BLOCKED` if a representation of "several entries" would need a choice among entries; if `CostLedgerEntry`, INTF-047 or INTF-048 semantics would have to change; or if any file outside scope changes |
| Commit | One remediation commit: `P0-2.B-REM-02: Cost Ledger Request-to-Measurement Retrieval — Minimal Implementation (Remediation)` |
| Approval | Explicit `Approve REM-P0.2.B-02`, valid only after a satisfactory AI verdict. It approves this remediation only; it is not approval, eligibility or authorization of `EXE-P0.2.B` |

**`REM-P0.2.B-03` — Path A Baseline Measurement Producer — Contract / Design (Remediation)**

| Field | Value |
|---|---|
| Commands | `Execute REM-P0.2.B-03` → AI Verification → `Approve REM-P0.2.B-03` |
| Precondition | (1) This v1.0.11 correction committed. (2) `REM-P0.2.B-01` approved. Re-verified at pre-flight. It does not require `REM-P0.2.B-02` |
| Relationship to gaps | Investigates `SOURCE-GAP-EXECPLAN-11`, `-12` and `-15`; closes none by itself. **Not a prerequisite of `EXE-P0.2.B`** (operator decision). Its incompleteness is the explicit deferral recorded at Gate P0.2 (§18.10). It is registered as a design unit because §18.14.2 forbids committing a design decision together with the implementation that depends on it and `-11`, `-12`, `-15` leave a producer's scope unspecified |
| Deliverable | A design artifact that investigates, from the authoritative sources, how a Path A (baseline) measurement of a run executing outside EAIOC (§14, §16) would be delivered into the ledger as a genuine `verified = true` entry: (i) capture mechanism and proving-lab placement (§3, §16; no unit builds them today); (ii) the producer of each non-nullable INTF-047 member (§28.1); (iii) the semantics of an inapplicable-versus-unmeasurable non-nullable member on an unoptimized entry (`-12`); (iv) the quality/task-success producer question (`-11`); (v) whether a `verified = true` Path A entry is honestly attainable at P0, or a recorded finding that it is not. Any option that would change §28.1, HD-4/DB-1 or D-A is presented as a revision for explicit human decision, never a default. Nothing is pre-decided or invented |
| Files/Modules | A design note only, beside the future package path under `control_plane/` (location fixed at pre-flight); no `src/`, `interfaces.md`, plan or runbook change |
| Tests / Failure-Path Test | Contract-conformance review against the live `interfaces.md` §28.1; N/A (design stage) |
| Review / verification | AUTHORITATIVE-DOC: Architect and Verifier at STEP level; GOVERNANCE RECORD annex; Reviewer only if implementation files change (none may) |
| Definition of Done | (1) Each of (i)–(v) is answered from sources or escalated as a human decision. (2) No placeholder or fabricated measurement. (3) No live measurement claimed. (4) Any implementation unit needed is described only; it is registered by a later user-directed correction. (5) `SOURCE-GAP-EVAL-01` and `SOURCE-GAP-IRG-02` remain open unless independently closed. (6) AI Verification `PASS` or `PASS WITH DOCUMENTED NON-BLOCKING GAP`. (7) Human approval |
| Blocking rule | `AI VERIFICATION: BLOCKED` if a decision would have to be assumed, a value or semantics invented, or any file outside scope changed |
| Commit | One remediation commit: `P0-2.B-REM-03: Path A Baseline Measurement Producer — Contract / Design (Remediation)` |
| Approval | Explicit `Approve REM-P0.2.B-03`. It approves this remediation only; it does not authorize `EXE-P0.2.B` and does not lift the Gate P0.2 deferral |

**Prerequisite relationships (derivation).** `REM-P0.2.B-02` precedes `EXE-P0.2.B` as a derived consequence: D-A gives the retrieval realization to `accounting/` (note §1); HQ-1 forbids inventing a request-to-ledger-entry lookup (§18.14.6); `EXE-P0.2.B`'s authorized files exclude `accounting/` (§18.5); `CostLedgerStore` offers only `write` and `read(tenantId, entryId)`; so `EXE-P0.2.B` cannot obtain a measurement unless the realization exists, and §18.15 makes that a hard guard (§18.14.7 requires such a unit to be registered before `EXE-P0.2.B`). `REM-P0.2.B-03` is **not** a prerequisite of `EXE-P0.2.B`, by operator decision; §18.10 records the corresponding explicit deferral at Gate P0.2.

**Status after v1.0.11.**
- `REM-P0.2.B-01`: **EXECUTED (`e4cf1f5`), AI-VERIFIED, HUMAN-APPROVED (2026-09-24)**.
- `REM-P0.2.B-02`: **REGISTERED / NOT EXECUTED**. Eligible once this correction is committed.
- `REM-P0.2.B-03`: **REGISTERED / NOT EXECUTED**. Not a prerequisite of `EXE-P0.2.B`; not scheduled by this plan.
- `SOURCE-GAP-EXECPLAN-07`/`-08`: **RESOLVED AT SOURCE**; `-09`: **NARROWED, still open**; `-10` to `-15`: registered (§38; status updates are additive, the original rows are retained).
- `SOURCE-GAP-EVAL-01`: OPEN (unchanged) — `RegressionReport` remains undefined in `interfaces.md` §18 and `report_regression()` is not declared; not closed by v1.0.11. `SOURCE-GAP-IRG-02`: OPEN (unchanged) — no interim telemetry path is documented for the Observability P0 item; Observability stays outside this execution slice (§3) and no Observability read path is introduced (HQ-1); not closed by v1.0.11.
- `EXE-P0.2.B`: **NOT YET EXECUTABLE**. It requires `REM-P0.2.B-02` executed, AI-verified and approved; then pre-flight (§18.19.3), the §18.15 guard (with its v1.0.11 addition) and a Code Architect re-assessment. At that pre-flight only the concrete failure mechanism of the no-measurement outcome (`-13`, narrowed) is a unit-level matter; the outcome itself is decided. It is contract-level and fixture-based and claims no live measurement.

*(The "Status after v1.0.11" bullets above are the v1.0.11 position, retained as history; "Status after v1.0.12" below supersedes them.)*

**Status after v1.0.12.**
- `REM-P0.2.B-01`: **EXECUTED (`e4cf1f5`), AI-VERIFIED, HUMAN-APPROVED (2026-09-24)** — unchanged.
- `REM-P0.2.B-02`: **EXECUTED (`bf74ac3`), AI-VERIFIED (`PASS`), HUMAN-APPROVED (2026-09-27)**. This clears the named prerequisite of `EXE-P0.2.B` (below); it does not itself authorize, approve, or execute `EXE-P0.2.B`.
- `REM-P0.2.B-03`: **REGISTERED / NOT EXECUTED** — unchanged. Not a prerequisite of `EXE-P0.2.B`; not scheduled by this plan.
- `SOURCE-GAP-EXECPLAN-07`/`-08`/`-09`/`-10` to `-15`: unchanged from the v1.0.11 status above.
- `SOURCE-GAP-EVAL-01`/`SOURCE-GAP-IRG-02`: unchanged, OPEN.
- `EXE-P0.2.B`: **STILL NOT YET EXECUTABLE.** Its `REM-P0.2.B-02` precondition is now cleared, but its own pre-flight (§18.19.3), a fresh §18.15 guard check against the now-existing retrieval realization, and a Code Architect re-assessment have not run — none of those steps is performed by this correction. It remains contract-level and fixture-based and claims no live measurement; the Gate P0.2 deferral (§18.10) is unchanged.

*(The "Status after v1.0.12" bullets above are the v1.0.12 position, retained as history; "Status after v1.0.13" below supersedes them.)*

**Status after v1.0.13.**
- `REM-P0.2.B-01`/`REM-P0.2.B-02`: unchanged from the v1.0.12 status above.
- `REM-P0.2.B-03`: **REGISTERED / NOT EXECUTED** — unchanged. Not a prerequisite of `EXE-P0.2.B`; not scheduled by this plan.
- `SOURCE-GAP-EXECPLAN-07`/`-08`/`-09`/`-10` to `-15`, `SOURCE-GAP-EVAL-01`/`SOURCE-GAP-IRG-02`: unchanged.
- `EXE-P0.2.B`: **EXECUTED (`a2d022f`), AI-VERIFIED (`PASS`), HUMAN-APPROVED (2026-09-27).** Full 92-test suite passing. Reviewed by Architect (`APPROVED FOR IMPLEMENTATION`), Reviewer (`PASS WITH REQUIRED FOLLOW-UP` — one LOW finding, a missing `serialVersionUID`, fixed in the same commit) and Verifier (`CONFORMANT WITH DOCUMENTED GAPS`). **Disclosed, not concealed:** the Architect/Reviewer/Verifier routing ran after implementation rather than before, deviating from §18.19.4/§18.19.5's normal sequence; all three reviews found no required design change because of it. Still contract-level and fixture-based; live Path A end-to-end baseline measurement remains NOT DEMONSTRATED — explicitly deferred (Gate P0.2, §18.10).
- `EXE-P0.2.C`: **NOT EXECUTED, NOT AUTHORIZED.** `Approve EXE-P0.2.B` approved only that one sub-phase; it does not chain to `EXE-P0.2.C` (§18.11.12, no automatic chaining).
- **Capability Gate P0.2: NOT AUTHORIZED.** Not reachable until Sub-phases C–H are each separately executed and approved (§18.10).

```
v1.0.11 correction committed
        ↓
Execute REM-P0.2.B-02 → AI Verification (Architect + Reviewer + Verifier) → Approve REM-P0.2.B-02
        ↓
revalidation: Execute EXE-P0.2.B → §18.15 guard (+ v1.0.11 addition) → pre-flight → Code Architect re-assessment → AI Verification → Approve EXE-P0.2.B
        ↓
… EXE-P0.2.C–H … Capability Gate P0.2 (live Path A measurement: explicitly deferred, §18.10)

REM-P0.2.B-03: registered; run when the operator directs; not a prerequisite of any step above
```

### 18.15 `EXE-P0.2.B` Pre-Execution Guard (v1.0.4)

Before implementing `EXE-P0.2.B`, Claude Code MUST verify:

```
control_plane/core/interfaces/ exists
control_plane/core/schemas/ exists
control_plane/core/errors/ exists
required shared request/plan types exist
those types match the authoritative interfaces
Capability 1's corrected shared-foundation ownership has been satisfied
Capability 1 Gate has been approved
Capability 2.A has been approved
```

Concretely: each `core/<leaf>/` check is against the Java realization defined in §18.13; "required shared request/plan types" means `ControlPlaneRequest` and `OptimizationPlan` in `core/schemas/`; "match the authoritative interfaces" means field-by-field against the live `interfaces.md` §2.1/§3.2, subject only to deferrals `REM-P0.1.A-01` explicitly recorded and the human approved; "ownership satisfied" means §18.14.1's `GAP FOUND` has been closed by `REM-P0.1.A-01` and `-02`, both AI-verified and human-approved; the two approval checks are against the operator's explicit `Approve CAPABILITY-GATE P0.1` and `Approve EXE-P0.2.A` commands.

**v1.1.1 completion-guard semantics (§18.20.S, HD-CE-18, HD-CE-19) — the most important correction in this pass.** The two checks above are two different kinds of condition, and this pass distinguishes them precisely for the first time:

- **"Capability 1 Gate has been approved"** is a Capability-boundary condition. It is satisfied *only* by a genuine `Approve CAPABILITY-GATE P0.1` — never by `AI VERIFIED — CONTINUING`, never by `AI VERIFIED — CONTINUING (NOT APPLICABLE)`, and never by anything else. This check is unaffected by §18.20 and remains exactly as originally written, with no exception.
- **"Capability `<n>`.A has been approved"** is a completion-only condition — its actual purpose, stated precisely for the first time here, is to confirm that predecessor Sub-phase A reached a valid terminal state, not specifically that a human reviewed it. Within the same §18.20 authorized path, this condition is satisfied by any of:
  1. `HUMAN APPROVED` — the historical per-unit model (this is how it was satisfied for `EXE-P0.2.B`, and remains how it is satisfied for any unit outside a §18.20 path);
  2. `AI VERIFIED — CONTINUING` — the continuous model, PASS-class predecessor, same authorized path as the unit whose guard is being checked;
  3. `AI VERIFIED — CONTINUING (NOT APPLICABLE)` — the continuous model, `NOT APPLICABLE` predecessor, same authorized path.

  Options 2 and 3 are valid *only* when the predecessor sub-phase is path-internal to the exact same §18.20 authorized path as the unit performing the check — never across a path boundary, never across a Capability, and never as a substitute for a genuine boundary approval. **None of the three options above ever satisfies:** human approval; human authorization; Capability Gate approval; boundary approval; or any guard whose purpose specifically requires human review, authorization, or approval (which is exactly why "Capability 1 Gate has been approved," above, is not rewritten — a Gate check is precisely such a guard, not a completion-only one). No new global "approval" state is introduced; options 2 and 3 are the same two states §18.12.4/§18.17 already define, merely recognized here as valid completion evidence for this one, already-named kind of check.

  This corrected semantics is preserved for the historical interpretation of every check already satisfied before the migration point (e.g. `EXE-P0.2.B`'s own already-recorded `Approve EXE-P0.2.A`, unaffected and unrelabeled) and carries forward automatically, with no separate edit, through this section's own existing generalization below ("the same guard applies... to every later capability's Sub-phase B, at minimum `EXE-P0.3.B`") — so Capability 3's `EXE-P0.3.B` guard is corrected by this same paragraph, and no structurally equivalent guard requiring a separate correction was found elsewhere in this document (Capabilities 4–6's own Preconditions fields, §18.7–§18.9, check Capability Gate closure only, which is unaffected exactly as "Capability 1 Gate has been approved" above is unaffected).

If any condition fails:

```
AI VERIFICATION: BLOCKED
```

and Claude Code must not write code. It must report exactly what prerequisite is missing, using the §18.12.4 blocked block. The same guard applies, with the capability number substituted, to every later capability's Sub-phase B that consumes `core/` (at minimum `EXE-P0.3.B`, §18.6).

**v1.0.11 addition (§18.14.8).** In addition to the checks above, before implementing `EXE-P0.2.B`: (i) `REM-P0.2.B-02` has been executed, AI-verified and human-approved, and the retrieval realization in `control_plane/accounting/` exists and matches `interfaces.md` §18/§28.2; (ii) the v1.0.11 contract promotion is committed (`SOURCE-GAP-EXECPLAN-07`/`-08` resolved at source, §38). `REM-P0.2.B-03` is not checked (it is not a prerequisite). If either check fails: `AI VERIFICATION: BLOCKED`, no code.

### 18.16 N/A Sub-phases, Commit Discipline, and Out-of-Band Commits (v1.0.4)

**N/A sub-phases.** Several rows are `NOT APPLICABLE` at P0 scope (every capability's Sub-phase F, §28; some G rows state `NO DEDICATED SCENARIO`). They are never silently converted into artificial implementation work. For an N/A unit, Claude Code verifies and reports

```
AI VERIFICATION: NOT APPLICABLE
```

with its evidence/reason (the row's own stated reason, re-checked against the live source it cites), then:

```
### AI VERIFIED — AWAITING HUMAN APPROVAL

AI VERIFICATION: NOT APPLICABLE

Approve EXE-P0.n.X to continue.
```

Human approval remains required so the operator explicitly acknowledges the N/A disposition. No fake implementation commit is created for an N/A sub-phase merely to satisfy sequencing. The plan's count remains **48 atomic `EXE-P0.<n>.<letter>` execution units**, but an N/A unit may have no source-code change and therefore no implementation commit — this document no longer claims every A–H unit necessarily has a code commit (§18.2, §18.11.11). A `NO DEDICATED SCENARIO` G row is *not* automatically N/A: Claude Code first re-verifies the claim against the live `scenario-matrix.md` (as `EXE-P0.1.G` did, finding `SCN-TEN-003`/`SCN-AUDIT-003`), and only a confirmed absence yields `NOT APPLICABLE`.

**v1.1.1 qualification (§18.20.R, HD-CE-17) — historical vs. path-internal.** The "human approval remains required" sentence above is this subsection's original, still-governing rule for every N/A unit outside a §18.20 authorized path, applying unchanged to every N/A unit executed before the migration point recorded at §18.20.O (e.g. `EXE-P0.1.F`, already individually approved). **Inside a §18.20 authorized path**, an N/A sub-phase does not follow the `### AI VERIFIED — AWAITING HUMAN APPROVAL` / `Approve EXE-P0.n.X` sequence above: it emits the distinct `### AI VERIFIED — CONTINUING (NOT APPLICABLE)` footer (§18.12.4) instead, requires no individual `Approve`, counts as completed for path progression, and continues automatically to the next path-internal unit — while still never producing a commit (unchanged, this section, above), never becoming `HUMAN APPROVED`, and never expanding authorization beyond the path (§18.20.R). All other protections in this subsection — no fake commit, the `NO DEDICATED SCENARIO` re-verification requirement, the unchanged 48-unit count — apply identically inside and outside a §18.20 path.

**Commit discipline clarification.** Preserved: `One executable A–H sub-phase = one isolated implementation change-set.` Where that sub-phase produces implementation changes: `one sub-phase = one implementation commit`. Where a sub-phase is genuinely `NOT APPLICABLE` and produces no implementation changes: `no implementation commit`. The Capability Gate remains: `no implementation commit`. An N/A checkpoint is never called, or represented by, a fake commit.

**Out-of-band commit `c3d6ecf`.** The repository contains an additional commit, `c3d6ecf` — "docs: sync CLAUDE.md with P0 implementation progress; add VS Code settings" — which modified `CLAUDE.md` and added `.vscode/settings.json`. This plan records that:

- it was **not** an `EXE-P0.n.X` implementation commit;
- it must **not** be counted as one of the 48 atomic implementation units' commits;
- it must **not** be used as evidence that any execution unit was completed;
- future sub-phase execution must not modify unrelated editor files;
- future progress/status documentation changes must remain outside implementation-unit commits unless explicitly authorized by the execution plan.

The historical commit is not retroactively altered.

**`.vscode/settings.json`.** Not modified by this correction; recorded as existing repository history. Future implementation commands must not add personal editor configuration to an atomic P0 implementation commit unless a future explicit source-backed requirement authorizes it. Whether `.vscode/` should ultimately be ignored is a separate repository-management decision outside this execution plan. It is never counted as a P0 implementation file.

### 18.17 Execution State Model (v1.0.4)

Per unit (and per remediation unit):

```
NOT STARTED
    ↓
EXECUTING
    ↓
EXECUTED
    ↓
AI VERIFYING
    ↓
AI VERIFIED
    ↓
AWAITING HUMAN APPROVAL
    ↓
HUMAN APPROVED
    ↓
NEXT UNIT ELIGIBLE
```

Failure state:

```
AI VERIFYING
    ↓
AI VERIFICATION BLOCKED
    ↓
REMEDIATION
    ↓
RE-EXECUTE / RE-VERIFY
```

No state may bypass AI verification. A precondition failure detected before any work begins (e.g. §18.15) moves the unit directly from `EXECUTING` to `AI VERIFICATION BLOCKED` with no change-set. `RE-EXECUTE / RE-VERIFY` always requires a fresh explicit `Execute` command from the user — Claude Code never re-executes on its own after remediation. `NEXT UNIT ELIGIBLE` means only that the user *may* issue the next `Execute`; it never triggers it.

**v1.1.0 path-internal continuation state (§18.20).** For a unit inside a §18.20-authorized path, when the next sub-phase remains within that same path (i.e. this is not the path's boundary unit), the model above branches after `AI VERIFIED`:

```
AI VERIFIED
    ↓
[within a §18.20 authorized path, not at its boundary]
    ↓
CONTINUING   (committed; NOT `HUMAN APPROVED`)
    ↓
NEXT UNIT ELIGIBLE   (automatic — no human command required)
```

`CONTINUING` is a distinct state from `HUMAN APPROVED` and never *is*, or is treated as, `HUMAN APPROVED` — no guard whose purpose specifically requires human review, human authorization, or human approval (e.g. a Capability Gate approval check, or a boundary-approval check) is ever satisfied by `CONTINUING`. **v1.1.1 correction (§18.20.S, HD-CE-18):** this is narrower than the v1.1.0 wording it replaces — a *completion-only* guard (one whose purpose is only to confirm a predecessor sub-phase reached a valid terminal state, such as the second check in §18.15's guard) may accept `CONTINUING` as valid evidence when the predecessor is path-internal to the same authorized path; see §18.20.S for the exact, narrow scope of this exception and the guards it never applies to. At the path's boundary unit, the model rejoins the original diagram above at `AWAITING HUMAN APPROVAL → HUMAN APPROVED`, and that approval names every unit the path covers since it opened or last resumed (§18.20.G). Outside a §18.20 path, the original diagram above applies unchanged, with `HUMAN APPROVED` required for every unit exactly as before.

**v1.1.1 path-internal N/A continuation state (§18.20.R, HD-CE-17).** For a unit inside a §18.20-authorized path whose verdict is `NOT APPLICABLE` — and, as with `CONTINUING` above, only when it is not the path's boundary unit — the model branches after `AI VERIFIED` along a distinct path:

```
AI VERIFIED   (verdict: NOT APPLICABLE)
    ↓
[within a §18.20 authorized path, not at its boundary]
    ↓
CONTINUING (NOT APPLICABLE)   (no commit; NOT `HUMAN APPROVED`; NOT ordinary `CONTINUING`)
    ↓
NEXT UNIT ELIGIBLE   (automatic — no human command required)
```

`CONTINUING (NOT APPLICABLE)` is a state distinct from both `HUMAN APPROVED` and ordinary `CONTINUING` — it is never substitutable for either, and it never implies a commit exists (§18.16). Like `CONTINUING`, it never satisfies a guard requiring human review/authorization/approval, but, per §18.20.S/HD-CE-19, it may satisfy a completion-only guard on the same narrow terms as `CONTINUING` does. At the path's boundary unit, an N/A unit's state likewise rejoins `AWAITING HUMAN APPROVAL → HUMAN APPROVED`, unchanged from §18.16. Outside a §18.20 path, the original diagram above (§18.16's N/A approval sequence) applies unchanged.

### 18.18 Updated P0 Sequence Semantics and Current Position (v1.0.4)

The overall P0 sequence becomes:

```
Environment Readiness
        ↓
Execute A
        ↓
AI Verification
        ↓
Human Approval
        ↓
Execute B
        ↓
AI Verification
        ↓
Human Approval
        ↓
...
Execute H
        ↓
AI Verification
        ↓
Human Approval
        ↓
AI Capability Gate Verification
        ↓
Human Capability Gate Approval
        ↓
Next Capability A
```

This is the authoritative operator model.

*Historical (at v1.0.4 correction time, superseded by the v1.0.6 position below):* Capability 1 A–E, G, H committed under v1.0.3; F `NOT APPLICABLE`; `REM-P0.1.A-01`/`-02` not started; `EXE-P0.2.B` stopped on a missing prerequisite.

*Historical (at v1.0.6 correction time; superseded by the v1.0.7 update):* `REM-P0.1.B-01`/`-02` not started.

*Historical (at v1.0.7 correction time; superseded by the v1.0.8 position below):* the bullets that follow, as written at v1.0.7 — `REM-P0.1.B-02` not executed, G/H approvals not evidenced, Gate P0.1 BLOCKED, `EXE-P0.2.B` blocked by the §18.15 guard.

**Position at v1.0.7 correction time (historical).**
- **Environment readiness:** passed.
- **Capability 1:**
  - A–E, G, H committed under v1.0.3.
  - F `NOT APPLICABLE`, acknowledged and approved.
  - `REM-P0.1.A-01` (+ Amendment 1) and `REM-P0.1.A-02` committed and approved: `c05e3e7`, `93c1ba3`, `5e12148`. `SOURCE-GAP-EXECPLAN-04` is closed on the implementation side, with the Capability 1 Gate still to confirm it.
  - `EXE-P0.1.B` reconciled `GAP FOUND` (`CONTRA-EXECPLAN-01`, §18.14.4). `REM-P0.1.B-01` is done, AI-verified and human-approved (`34dd1f1`). HD-1 to HD-4 are promoted into the contract (DB-2, §18.14.5; this correction). `REM-P0.1.B-02` is **not executed**; it is eligible once DB-2 is committed.
  - G/H approvals not evidenced (§18.14.3).
  - Gate P0.1 **BLOCKED**.
- **Capability 2:** A committed and approved; B **blocked** by the §18.15 guard until Gate P0.1 is approved.

The authoritative remaining sequence up to `EXE-P0.2.B` is:

```
(done) Execute / Approve REM-P0.1.B-01                    — 34dd1f1
(done at v1.0.7, once committed) DB-2 source-contract promotion of HD-1..HD-4
Execute REM-P0.1.B-02        → AI verification → Approve REM-P0.1.B-02
(G/H evidence) confirm, or issue Approve EXE-P0.1.G / Approve EXE-P0.1.H after review
Review CAPABILITY-GATE P0.1  → AI CAPABILITY GATE VERIFICATION → Approve CAPABILITY-GATE P0.1
Execute EXE-P0.2.B           → §18.15 guard → AI verification → Approve EXE-P0.2.B
```

Each line is issued one command at a time, with no automatic chaining between any of them. After `EXE-P0.2.B`, the idealized §18.11.7 sequence resumes unchanged from `Execute EXE-P0.2.C`.

**Position at v1.0.8 correction time (historical; superseded by the v1.0.10 position below).**
- **Environment readiness:** passed.
- **Capability 1 — CLOSED:**
  - A–E, G, H committed under v1.0.3; F `NOT APPLICABLE`, acknowledged and approved, no commit.
  - `REM-P0.1.A-01` (+ Amendment 1) and `REM-P0.1.A-02` approved (`c05e3e7`, `93c1ba3`, `5e12148`).
  - `REM-P0.1.B-01` approved (`34dd1f1`); DB-2 committed (`197da0a`); `REM-P0.1.B-02` executed, AI-verified (`PASS`) and approved (`c378dc9`).
  - `EXE-P0.1.G`/`H` approved (2026-09-23).
  - Capability Gate P0.1: `AI CAPABILITY GATE VERIFICATION: PASS`; `Approve CAPABILITY-GATE P0.1` issued (2026-09-23).
- **Capability 2:**
  - A committed and approved (`0e92261`).
  - B: the §18.15 guard passes. **BLOCKED** on `SOURCE-GAP-EXECPLAN-07`/`SOURCE-GAP-EXECPLAN-08` (§18.14.6); not executed, no code.

The authoritative remaining sequence up to `EXE-P0.2.B` is:

```
(needed) a user-directed source-contract correction closing SOURCE-GAP-EXECPLAN-07/-08
Execute EXE-P0.2.B           → §18.15 guard → Code Architect re-assessment → AI verification → Approve EXE-P0.2.B
```

No unit ID is assigned to that correction here. The v1.0.7 sequence block above is historical.

**Current position in this repository (updated at v1.0.10 correction time).**
- **Environment readiness:** passed.
- **Capability 1 — CLOSED:** unchanged from the v1.0.8 position above.
- **Capability 2:**
  - A committed and approved (`0e92261`).
  - `REM-P0.2.B-01`: **REGISTERED / NOT STARTED** (§18.14.7).
  - B: the §18.15 guard passes. **BLOCKED** on `SOURCE-GAP-EXECPLAN-07`/`SOURCE-GAP-EXECPLAN-08`, both **OPEN** (§18.14.6, §38). Not executed; no code.
- `control_plane/evaluation/EvaluationMeasurementReconciliation.md` (`8b68239`) is a pre-decision record, not unit evidence (§18.14.7).

The authoritative remaining sequence up to `EXE-P0.2.B` is:

```
(needed) commit this v1.0.10 correction
Execute REM-P0.2.B-01        → AI verification (Architect + Verifier) → Approve REM-P0.2.B-01
(needed) user-directed source-contract correction promoting the REM-P0.2.B-01 decisions, closing SOURCE-GAP-EXECPLAN-07/-08
Execute EXE-P0.2.B           → §18.15 guard → pre-flight → Code Architect re-assessment → AI verification → Approve EXE-P0.2.B
```

Each line is issued one command at a time, with no automatic chaining. `Approve REM-P0.2.B-01` does not authorize `EXE-P0.2.B`. The v1.0.8 sequence block above is historical.

*(The block headed "Current position in this repository (updated at v1.0.10 correction time)" and its sequence above are the v1.0.10 position, retained as history; the v1.0.11 position below supersedes them.)*

**Current position in this repository (updated at v1.0.11 correction time).**
- **Environment readiness:** passed.
- **Capability 1 — CLOSED:** unchanged (Gate P0.1 approved 2026-09-23).
- **Capability 2:**
  - A committed and approved (`0e92261`).
  - `REM-P0.2.B-01`: **executed (`e4cf1f5`), AI-verified, human-approved (2026-09-24)** (§18.14.8).
  - `REM-P0.2.B-02`: **REGISTERED / NOT EXECUTED**; `REM-P0.2.B-03`: **REGISTERED / NOT EXECUTED**, not a prerequisite (§18.14.8).
  - B: `SOURCE-GAP-EXECPLAN-07`/`-08` resolved at source; **not yet executable** until `REM-P0.2.B-02` is approved. Not executed; no code.
  - **Live Path A end-to-end baseline measurement: NOT DEMONSTRATED — explicitly deferred (§18.14.8; Gate P0.2, §18.10).**
- `control_plane/evaluation/EvaluationMeasurementReconciliation.md` (`e4cf1f5`) is the approved design artifact of `REM-P0.2.B-01`; the pre-decision seed `8b68239` remains a `docs:` commit, not unit evidence.

The authoritative remaining sequence up to `EXE-P0.2.B` is:

```
(needed) commit this v1.0.11 correction
Execute REM-P0.2.B-02        → AI verification (Architect + Reviewer + Verifier) → Approve REM-P0.2.B-02
Execute EXE-P0.2.B           → §18.15 guard (+ v1.0.11 addition) → pre-flight → Code Architect re-assessment → AI verification → Approve EXE-P0.2.B
(optional, when directed) Execute REM-P0.2.B-03 → AI verification (Architect + Verifier) → Approve REM-P0.2.B-03
```

Each line is issued one command at a time, with no automatic chaining. `Approve REM-P0.2.B-02` does not authorize `EXE-P0.2.B`. The v1.0.10 sequence block above is historical.

*(The block headed "Current position in this repository (updated at v1.0.11 correction time)" and its sequence above are the v1.0.11 position, retained as history; the v1.0.12 position below supersedes them.)*

**Current position in this repository (updated at v1.0.12 correction time).**
- **Environment readiness:** passed.
- **Capability 1 — CLOSED:** unchanged (Gate P0.1 approved 2026-09-23).
- **Capability 2:**
  - A committed and approved (`0e92261`).
  - `REM-P0.2.B-01`: **executed (`e4cf1f5`), AI-verified, human-approved (2026-09-24)** — unchanged (§18.14.8).
  - `REM-P0.2.B-02`: **executed (`bf74ac3`), AI-verified (`PASS`), human-approved (2026-09-27)** (§18.14.8). `REM-P0.2.B-03`: **REGISTERED / NOT EXECUTED**, not a prerequisite — unchanged.
  - B: `SOURCE-GAP-EXECPLAN-07`/`-08` resolved at source; `REM-P0.2.B-02` executed, AI-verified and approved. **Still not yet executable**: its own pre-flight, a fresh §18.15 guard check, and a Code Architect re-assessment have not run. Not executed; no code.
  - **Live Path A end-to-end baseline measurement: NOT DEMONSTRATED — explicitly deferred (§18.14.8; Gate P0.2, §18.10).** Unchanged.
- `control_plane/evaluation/EvaluationMeasurementReconciliation.md` (`e4cf1f5`) remains the approved design artifact of `REM-P0.2.B-01`; `control_plane/accounting/ledger/{CostLedgerStore.java, LedgerCostReporter.java}` plus their tests (`bf74ac3`) are `REM-P0.2.B-02`'s realization.

The authoritative remaining sequence up to `EXE-P0.2.B` is:

```
DONE: v1.0.11 correction committed (196e112)
DONE: Execute REM-P0.2.B-02 → AI verification (Architect + Reviewer + Verifier) → Approve REM-P0.2.B-02 (bf74ac3, approved 2026-09-27)

Execute EXE-P0.2.B           → §18.15 guard (+ v1.0.11 addition) → pre-flight → Code Architect re-assessment → AI verification → Approve EXE-P0.2.B
(optional, when directed) Execute REM-P0.2.B-03 → AI verification (Architect + Verifier) → Approve REM-P0.2.B-03
```

Each line is issued one command at a time, with no automatic chaining. `Approve REM-P0.2.B-02` did not authorize `EXE-P0.2.B`. The v1.0.11 sequence block above is historical.

*(The block headed "Current position in this repository (updated at v1.0.12 correction time)" and its sequence above are the v1.0.12 position, retained as history; the v1.0.13 position below supersedes them.)*

**Current position in this repository (updated at v1.0.13 correction time).**
- **Environment readiness:** passed.
- **Capability 1 — CLOSED:** unchanged (Gate P0.1 approved 2026-09-23).
- **Capability 2:**
  - A committed and approved (`0e92261`). `REM-P0.2.B-01`/`REM-P0.2.B-02`: unchanged from the v1.0.12 position above.
  - B: **executed (`a2d022f`), AI-verified (`PASS`), human-approved (2026-09-27).** 92/92 tests passing. `run_baseline(request: ControlPlaneRequest) -> BaselineEvaluationRecord` realized in `control_plane/benchmarking/` (`BaselineRunner`) and `control_plane/evaluation/` (`BaselineEvaluationRecord`, `RunType`), per D-C/HQ-5 package placement. Contract-level and fixture-based; live Path A end-to-end baseline measurement remains NOT DEMONSTRATED — explicitly deferred (§18.14.8; Gate P0.2, §18.10).
  - C: **NOT EXECUTED, NOT AUTHORIZED.**
- `control_plane/benchmarking/{BaselineRunner.java, NoBaselineMeasurementException.java}`, `control_plane/evaluation/{BaselineEvaluationRecord.java, RunType.java, MissingTenantIdException.java}` (`a2d022f`) are `EXE-P0.2.B`'s realization, alongside their tests (`BaselineRunnerTest`, `BaselineTestFixtures`).
- **Capability Gate P0.2: NOT AUTHORIZED** — not reachable until Sub-phases C–H are each separately executed and approved.

The authoritative remaining sequence up to Capability Gate P0.2 is:

```
DONE: v1.0.12 correction committed (793ed7d)
DONE: Execute EXE-P0.2.B → AI verification (Architect + Reviewer + Verifier) → Approve EXE-P0.2.B (a2d022f, approved 2026-09-27)

Execute EXE-P0.2.C → AI verification → Approve EXE-P0.2.C
… EXE-P0.2.D–H … Capability Gate P0.2 (live Path A measurement: explicitly deferred, §18.10)

(optional, when directed) Execute REM-P0.2.B-03 → AI verification (Architect + Verifier) → Approve REM-P0.2.B-03
```

Each line is issued one command at a time, with no automatic chaining. `Approve EXE-P0.2.B` did not authorize `EXE-P0.2.C`. The v1.0.12 sequence block above is historical.

**v1.1.0 continuous-execution sequence (§18.20; supersedes the sequence above for `EXE-P0.2.C` onward, which is retained as history describing the pre-§18.20 model).**

```
DONE: v1.0.12 correction committed (793ed7d)
DONE: v1.0.13 correction committed — Execute EXE-P0.2.B → AI verification → Approve EXE-P0.2.B (a2d022f, approved 2026-09-27)
DONE: v1.1.0 correction — adopts §18.20; EXE-P0.2.C is the recorded migration point (§18.20.O); not itself an execution
DONE: v1.1.1 correction — implements HD-CE-17/18/19 (N/A continuation semantics, §18.15 completion-guard semantics); not itself an execution

Execute EXE-P0.2.C
  → opens the authorized path for Capability 2's remaining sub-phases (§18.20.B–C)
  → C: AI verification → PASS-class → commit → ### AI VERIFIED — CONTINUING (auto-continue)
  → D: AI verification → PASS-class → commit → ### AI VERIFIED — CONTINUING (auto-continue)
  → E: AI verification → PASS-class → commit → ### AI VERIFIED — CONTINUING (auto-continue)
  → F: AI verification → NOT APPLICABLE (no commit, §18.16) → ### AI VERIFIED — CONTINUING (NOT APPLICABLE) (auto-continue, §18.12.4/§18.20.R — corrected at v1.1.1; not ordinary CONTINUING)
  → G: AI verification → PASS-class → commit → ### AI VERIFIED — CONTINUING (auto-continue)
  → H: AI verification → PASS-class → commit → ### AI VERIFIED — CONTINUING (auto-continue)
  → Capability Gate P0.2 review → AI Capability Gate Verification
       PASS    → boundary approval requested, enumerating every unit C–H (including F's NOT APPLICABLE disposition, §18.20.G) → Approve CAPABILITY-GATE P0.2
       BLOCKED → path stops (§18.20.K); no boundary approval requested

(optional, when directed) Execute REM-P0.2.B-03 → AI verification → Approve REM-P0.2.B-03 (Optional, §18.20.J; not path-internal, not automatic)
```

This sequence does not itself execute, verify, approve, or commit anything — it is this document's authoritative description of what one `Execute EXE-P0.2.C` will do once issued. Any §18.20.K blocking condition encountered at C, D, E, G, or H stops the path at that unit and requests a normal `Approve EXE-P0.n.X` for the units completed so far (§18.20.G), not the Gate-level approval above. Capability Gate P0.2's live-Path-A-measurement deferral (§18.10, §18.14.8) is unaffected by this amendment.

### 18.19 Execution Assistance Layer (v1.0.9)

#### 18.19.1 Definition and boundary

The **Execution Assistance Layer** is the set of repository-defined Claude Code skills and specialized subagents under `.claude/` that may assist Claude Code **inside an atomic unit the operator has already authorized**. Its purpose is to reduce repeated source reading, improve source traceability, surface the governing contracts before implementation, route specialized verification, detect missing prerequisites early, strengthen test and failure-path coverage, and keep evidence reporting consistent — without weakening any governance control.

It exists only in the development/execution workflow. It is **not** an EAIOC runtime layer, not an EAIOC production component, not part of the three-layer EAIOC architecture (`architecture.md`), and adds no `EXE-P0` unit, ID, lifecycle sub-phase, or gate. **Skills and agents improve execution efficiency; they do not resolve source-contract blockers, and no skill or subagent may authorize execution progression.**

The governance state machine is unchanged (§18.10, §18.11, §18.12):

```
Human: Execute EXE-P0.n.X
  → Claude Code: pre-flight + routing (§18.19.3)
  → applicable skills / subagents (evidence only)
  → unit implementation (only the authorized unit and files)
  → tests + failure path
  → AI Verification (§18.12, incl. dimension K)
       BLOCKED → STOP (no approval request)
       PASS    → ### AI VERIFIED — AWAITING HUMAN APPROVAL
  → Human: Approve EXE-P0.n.X
  → next explicit Execute command (never automatic)
```

The Capability Gate sequence (Review → AI Capability Gate Verification → Human Gate Approval) is likewise unchanged.

**v1.1.0 note (§18.20):** the diagram above is this subsection's original, still-governing model for every unit outside a §18.20 authorized path. For a path-internal unit short of its path's boundary, the `AI Verification` → `PASS` → `### AI VERIFIED — AWAITING HUMAN APPROVAL` → `Human: Approve` step is replaced by `### AI VERIFIED — CONTINUING` and automatic continuation to the next unit's pre-flight and routing (§18.12.4, §18.20.D); the Capability Gate sequence itself is unchanged, and where a path runs through it, serves as that path's boundary approval (§18.20.C, §18.10).

#### 18.19.2 Current inventory (live `.claude/` tree at v1.0.9)

| Name | Path | Kind | Purpose | Tools / write access | Shell | Publishes | Consumes |
|---|---|---|---|---|---|---|---|
| `eaioc-code-architect` | `.claude/agents/eaioc-code-architect.md` | Subagent (verification) | "Is this the correct design?" — ARCHITECTURE ASSESSMENT; verdicts `APPROVED FOR IMPLEMENTATION` / `APPROVED WITH CONDITIONS` / `BLOCKED` / `SOURCE CONFLICT` | Read, Grep, Glob, Bash, PowerShell — **no Edit/Write** | Read-only git; `mvn` | No | Governance skill (preloaded), live docs, code, git |
| `eaioc-code-reviewer` | `.claude/agents/eaioc-code-reviewer.md` | Subagent (verification) | "Is this implementation well-engineered and compliant?" — CODE REVIEW REPORT; `PASS` / `PASS WITH REQUIRED FOLLOW-UP` / `CHANGES REQUIRED` / `BLOCKED BY SOURCE CONFLICT` | Same — **no Edit/Write** | Read-only git; `mvn test` | No | Actual diff, surrounding code, tests, governing docs |
| `eaioc-code-verifier` | `.claude/agents/eaioc-code-verifier.md` | Subagent (verification) | "Does the implementation match the complete documented system?" — traceability matrix; `CONFORMANT` / `CONFORMANT WITH DOCUMENTED GAPS` / `NON-CONFORMANT` / `BLOCKED BY SOURCE CONFLICT` | Same — **no Edit/Write** | Read-only git; `mvn test` | No | Full doc chain, code, tests; other reports as evidence only |
| `eaioc-guide` | `.claude/agents/eaioc-guide.md` | Subagent (advisory) | Conceptual/architectural/technology Q&A and external-claim verification; flags `SOURCE-GAP`/`CONTRA` | Read, Grep, Glob, WebFetch, WebSearch, Skill, Artifact — **no Edit/Write** | No | **Yes — always answers as a published HTML artifact** | Root `CLAUDE.md`, the cited docs, the web |
| `eaioc-governance` | `.claude/skills/eaioc-governance/SKILL.md` | Skill (shared contract) | Source hierarchy and documented precedence, no-fabrication rule, evidence labels, coding-standards sources, coverage chain, git/gated-execution awareness, self-check; preloaded by the three `eaioc-code-*` agents | None of its own | — | No | — |
| `eaioc-agent-orchestration` | `.claude/skills/eaioc-agent-orchestration/` (`SKILL.md` + `governance-matrix.yaml`) | Skill (routing, main session only) | WHEN the three `eaioc-code-*` agents participate: change classification, STEP vs CAPABILITY level, escalation, status consolidation, GOVERNANCE RECORD annex and `Governance-*` commit trailers | None of its own | — | No | Unit row, actual diff |
| `eaioc-dashboard` | `.claude/skills/eaioc-dashboard/SKILL.md` | Skill (reporting) | Adds a small dashboard to a published EAIOC report artifact when it has genuine quantitative content; skipped otherwise | None of its own | — | Only as part of an artifact | Figures the grounded answer already cites |

Not execution assistance: `.claude/rules/prompt-writing.md` (a prompt-authoring rule), `.claude/settings*.json`, `.claude/statusline.ps1`. All marketplace plugins are disabled in `.claude/settings.local.json`; built-in session skills are not repository-defined and are not routed by this plan. **No agent or skill has Write/Edit access, production-code authority, approval authority, or the ability to authorize the next unit.** Only the main Claude Code session writes files, and only within the authorized unit. No agent or skill definition is changed by this plan.

#### 18.19.3 Per-unit pre-flight and fast-fail rule

Before implementing any `EXE-P0.n.X` or remediation unit, Claude Code determines — as a routing decision, not an implementation decision:

1. the exact requested unit (canonical ID, §18.11.2);
2. the authoritative sources governing it (§18.4–§18.9 row, re-verified against the live documents);
3. the prerequisites required, 4. those actually present, and 5. those with explicit approval evidence;
6. applicable skills, 7. applicable subagents, 8. which are mandatory, 9. which optional, 10. which not applicable (§18.19.4–§18.19.5);
11. the evidence to be produced; 12. any unresolved source gap affecting the unit.

**Fast-fail.** If pre-flight finds a missing prerequisite, a missing authoritative contract, an unresolved source gap the unit needs, a contradictory source, a missing required approval, a wrong execution state, a wrong repository branch/state, unauthorized modified files, a skill/agent contract conflict, or a stale plan assumption, Claude Code **stops before writing code** and reports `AI VERIFICATION: BLOCKED`. Implementation never starts "to see if it works", and no skill or agent is used to invent a missing prerequisite.

#### 18.19.4 Routing matrix

The `eaioc-agent-orchestration` skill (with its `governance-matrix.yaml`) is the routing authority for the three `eaioc-code-*` agents; this table restates its sub-phase defaults and does not redefine it. The actual diff decides: a change that turns out bigger than planned escalates (never de-escalates) within the unit.

| Name | Applicable capabilities | Applicable sub-phases | Invocation | Mandatory / optional | Role | Write | Prod-code authority | Can approve | Can authorize next unit | Failure behavior | Evidence produced |
|---|---|---|---|---|---|---|---|---|---|---|---|
| `eaioc-code-architect` | 1–6 | A (design/contract); B and C before code; D, E, G, H when an escalation trigger fires (new package, public contract, dependency, fail-open/closed, tenant-isolation mechanism, metric name, persistence, ADR-worthy choice); Gate only if escalated or drift found | Automatic by Claude Code (Agent tool) | Mandatory when the orchestration matrix says `yes`, conditional when `cond` | Verification | No | No | No | No | `BLOCKED`/`SOURCE CONFLICT` → unit BLOCKED before code | ARCHITECTURE ASSESSMENT |
| `eaioc-code-reviewer` | 1–6 | B–E, G, H whenever code, tests, or behavioral config change; Gate for capability-level-deferred changes | Automatic | Mandatory at STEP level for any non-trivial code/test change | Verification | No | No | No | No | `CHANGES REQUIRED` → fix inside the unit's files and re-run, else BLOCKED; `BLOCKED BY SOURCE CONFLICT` → BLOCKED | CODE REVIEW REPORT |
| `eaioc-code-verifier` | 1–6 | A–E, G, H at STEP level; **every Capability Gate (always, whole capability)** | Automatic | Mandatory at STEP level and at every Gate | Verification | No | No | No | No | `NON-CONFORMANT` → fix inside scope or BLOCKED; `BLOCKED BY SOURCE CONFLICT` → BLOCKED | Traceability matrix + VERIFICATION REPORT |
| `eaioc-governance` | 1–6 | All (preloaded into the three agents) | Automatic (preload) | Mandatory whenever an `eaioc-code-*` agent runs | Execution assistance (shared rules) | No | No | No | No | n/a | Evidence labels used in reports |
| `eaioc-agent-orchestration` | 1–6 | All units and Gate reviews | Automatic, main session | Mandatory for routing | Execution assistance (routing) | No | No | No | No | Unresolved `FAIL`/`BLOCKED`/`PENDING`/`ESCALATED` → unit verdict BLOCKED | GOVERNANCE RECORD annex; `Governance-*` commit trailers |
| `eaioc-guide` | 1–6 | Any, **only on an operator question** | Operator-triggered (a question), never automatic inside a unit | Optional | Advisory | No | No | No | No | Its `SOURCE-GAP`/`CONTRA` findings must be respected; never converted into a contract | Published HTML artifact |
| `eaioc-dashboard` | 1–6 | Only when a report is published as an artifact | With `eaioc-guide` or another published artifact | Optional; skipping it is not a gap | Reporting | No | No | No | No | Never invents numbers; never changes a verdict; never replaces the textual AI Verification report | Dashboard section of an artifact |

**No skill or subagent may authorize execution progression.** No skill or subagent may approve a unit, approve or close a Capability Gate, bypass human approval, turn `BLOCKED` into `PASS`, declare a source gap resolved, invent or change a contract, or start another unit or remediation.

#### 18.19.5 Capability and sub-phase routing guidance

No capability-specific skill or subagent exists in the repository; the three `eaioc-code-*` agents are EAIOC-wide and are given a capability-specific focus in their prompts (scope, governing sections), never a changed role.

| Capability | Focus handed to the agents | Constraints that remain in force |
|---|---|---|
| 1 — Token Accounting and Cost Ledger | Ledger contract (`interfaces.md` §28.1, 58 canonical fields), HD-1–HD-4 / DB-1, tenant-first storage, `verified` handling, accounting tests, failure/recovery | **Closed** (Gate P0.1 approved). Assistance applies only to a future remediation the operator commands |
| 2 — Baseline Benchmark Harness + Quality Evaluation | INTF-030 contract, HQ-1–HQ-6 (§18.14.6), measurement acquisition, BASELINE semantics, tenant-scoped run store, failure path (`eval.md` §37) | Agents cannot close `SOURCE-GAP-EXECPLAN-07`/`-08` or `SOURCE-GAP-EVAL-01` (§18.19.7) |
| 3 — Sanitizer | Security/PII classification, fail-closed on security (`conventions.md` §16.2), negative paths, security traceability, `EC-077` | SEC-protected content never pruned/compressed (root `CLAUDE.md` rule 6) |
| 4 — Context Policy | Policy configuration, classification, prioritization, budget policy, policy validation | Placement per `SOURCE-GAP-EXECPLAN-02` (§38) |
| 5 — Prompt Assembler | Assembly correctness, context ordering, schema conformance, **provider neutrality and `provider_hints` opacity** (§14) | The `EXE-P0.5.A` provider-string check is unchanged |
| 6 — Output Controls | Output budget/constraints, schema compliance, safety validation, negative paths | Placement per `SOURCE-GAP-EXECPLAN-02` |

By sub-phase: **A** — source grounding, contract verification, dependency and gap detection (Architect + Verifier); inventing a contract, silently changing an interface, or writing production code is forbidden. **B** — package placement and contract conformance, then focused review (Architect before code + Reviewer + Verifier); only the requested unit and its authorized files. **C** — dependency/interface compatibility and call paths (all three). **D** — only where the unit's row includes observability; an agent never widens an explicit observability scope boundary (§1 scope; `SOURCE-GAP-IRG-02`). **E** — security/governance review against the authoritative security contracts. **F** — usually `NOT APPLICABLE` at P0 (no agents; the N/A evidence is recorded); tests stay traceable to the unit's Definition of Done. **G** — scenario checks; running an agent is never itself scenario coverage. **H** — failure-path/recovery review; the unit's negative-path test stays mandatory.

**Minimum sufficient assistance.** Use only the assistance the unit actually needs — mandatory, directly relevant, or needed for verification — not every available agent. Each AI Verification report states *Applicable assistance* and *Not applicable assistance (with reason)* on line 10 (§18.12.3).

#### 18.19.6 Specialist evidence is not a verdict

Agent output is evidence for the main session's §18.12 AI Verification, never a substitute for it. The agents report in their own vocabularies (§18.19.2), consolidated by the orchestration skill into `PASS` / `FAIL` / `BLOCKED` / `NOT_REQUIRED` / `ESCALATED` / `PENDING`; a finding of `SOURCE-GAP`, `CONTRA`, or `NOT APPLICABLE` keeps its meaning. No specialist `PASS` overrides a failed test, a missing prerequisite, a source contradiction, an architecture violation, an out-of-scope file change, or an unresolved contract the unit needs. Every specialist warning or finding is either resolved inside the unit's scope, documented as non-blocking with its reason, or turned into `AI VERIFICATION: BLOCKED`.

#### 18.19.7 `EXE-P0.2.B` protection

This correction does not move `EXE-P0.2.B` forward. It remains **BLOCKED** on `SOURCE-GAP-EXECPLAN-07` and `-08` (§18.14.6, §38), with `SOURCE-GAP-EVAL-01` open, until an authorized, user-directed source-contract correction independently closes them. Routing assistance does not make it READY. *(v1.0.10: the remediation that prepares that correction is registered as `REM-P0.2.B-01`, §18.14.7. Its routing is AUTHORITATIVE-DOC — Architect and Verifier at STEP level — and no agent may take its human decisions.)* *(v1.0.11: `SOURCE-GAP-EXECPLAN-07`/`-08` are resolved at source and `EXE-P0.2.B` is now conditioned on `REM-P0.2.B-02` (§18.14.8); `SOURCE-GAP-EVAL-01` stays open; routing assistance still does not make `EXE-P0.2.B` READY.)* *(This §18.19.7 note is unaffected by §18.20 below: `EXE-P0.2.B` is already executed and approved as of v1.0.13, and this paragraph is retained as history.)*

### 18.20 Continuous Phase-Driven Execution Model (v1.1.0)

This section is the authoritative source for continuous, phase-driven execution within an explicitly human-authorized Capability path. It is adopted as a formal amendment following the human decision register HD-CE-01 through HD-CE-16 (frozen 2026-09-28, in a dedicated read-only governance-redesign analysis and decision-collection process conducted outside this document) and does not weaken any invariant listed at §18.20.Q. **It applies prospectively only**, from the migration point recorded at §18.20.O (`EXE-P0.2.C`) onward; every unit executed before that point remains governed exclusively by this document's pre-existing model (§18.2, §18.11.1, §18.11.2, §18.11.12, §18.17), unchanged and never relabeled (§18.20.O, HD-CE-15).

#### A. Phase

"Phase" means **Capability**, exactly as Capability is already used throughout §18.1–§18.9. No new hierarchy level is introduced above or below Capability/Sub-phase (HD-CE-01).

#### B. Human authorization

The explicit human `Execute EXE-P0.<n>.<letter>` command remains the sole event that opens an authorized execution path — unchanged from §18.11.1. What one such command authorizes is redefined: naming a unit opens the path for the remaining sub-phases of that unit's Capability. For example, `Execute EXE-P0.2.C` opens the path for Capability 2's remaining sub-phases (C onward). Authorization is path-scoped, never global: it authorizes nothing in any other Capability, and nothing that precedes the named unit (HD-CE-02).

#### C. Authorized execution path

An authorized execution path:
- starts at the unit explicitly named in the triggering `Execute` command;
- consists of that unit and every remaining not-yet-approved sub-phase of that same Capability, in the existing A–H order;
- ends at that Capability's own Capability Gate (§18.10) — the path's boundary approval (§18.20.G) coincides with, and is satisfied by, that Gate's Human Gate Approval, reusing §18.10's existing pattern of reviewing the whole capability's evidence in one pass, now extended to cover every path-internal unit's evidence as well;
- never crosses into another Capability under any circumstance (§18.20.M).

If a path stops before reaching its Gate (any §18.20.K condition), its boundary is the stopped-at unit, and its boundary approval (once the stop is resolved and the path resumes to completion, or independently if the operator chooses to approve the completed portion) is a normal `Approve EXE-P0.n.X`, not a Gate approval.

#### D. Continuous continuation

After a path-internal unit's work completes:
1. Specialist governance routing (Architect/Reviewer/Verifier, §18.19.4–§18.19.5) occurs exactly as it would for any unit — unchanged.
2. Implementation, testing and evidence-gathering occur exactly as §18.4–§18.9 and §18.12.1 require — unchanged.
3. AI Verification (§18.12) occurs exactly as required — unchanged.
4. On a PASS-class verdict (`PASS` or `PASS WITH DOCUMENTED NON-BLOCKING GAP`), the unit is committed — exactly one commit, unchanged (§18.20.L).
5. If the next sub-phase remains within the same authorized path and no §18.20.K blocking condition applies, execution continues automatically to that next sub-phase, reporting `### AI VERIFIED — CONTINUING` (§18.12.4) rather than requesting an individual `Approve …`.

This continuation is not a new authorization event. It is the exercise of the authorization already granted by the triggering `Execute` command (§18.20.B), never an independent act of judgment beyond that grant.

#### E. No authorization expansion

None of the following ever expands what is authorized, under continuous execution or otherwise:
- a passing test;
- a successful commit;
- an `AI VERIFICATION: PASS` (or `PASS WITH DOCUMENTED NON-BLOCKING GAP`) verdict;
- a closed Capability Gate.

A Capability Gate approval never authorizes the next Capability's Sub-phase A. Only an explicit human `Execute EXE-P0.(n+1).A` does (unchanged from §18.11.6).

#### F. `AI VERIFIED — CONTINUING`

```
### AI VERIFIED — CONTINUING
```

means: the unit passed AI Verification; the unit was committed; continuation to the next unit is permitted only because this unit lies within an execution path already opened by an explicit human `Execute` command (§18.20.B–C); it is not, and must never be treated as, `HUMAN APPROVED`; it authorizes no work outside that path. It is never a fifth AI Verification verdict value (§18.12.1.J is unchanged) — it is a footer/state distinct from, and never substitutable for, `### AI VERIFIED — AWAITING HUMAN APPROVAL` (§18.12.4), which remains the footer for every unit outside a §18.20 path and for a path's own boundary unit.

#### G. Human approval

- No individual `Approve …` is required for a unit that transitions to `AI VERIFIED — CONTINUING`.
- Human `Approve …` is required at the path's boundary (§18.20.C) — ordinarily `Approve CAPABILITY-GATE P0.<n>`, following an `AI CAPABILITY GATE VERIFICATION: PASS` exactly as §18.10/§18.11.6 already require, now reviewing every path-internal unit's evidence alongside H's own.
- **The boundary approval request must explicitly enumerate exactly which units' evidence it covers** — it is never an implicit blanket approval of "everything up to here" (HD-CE-05). **v1.1.1 (HD-CE-17):** this enumeration must name every completed path-internal unit regardless of which continuation state it reached — `AI VERIFIED — CONTINUING` units and `AI VERIFIED — CONTINUING (NOT APPLICABLE)` units alike (§18.20.R) — so an N/A unit's disposition is always part of what the human reviews at the boundary. Naming an N/A unit in this enumeration is never itself, and must never be read as, that unit's own individual approval — the boundary approval is a single act covering the whole named set, not a retroactive collection of per-unit approvals (§18.20.G, above).
- `AI VERIFICATION: PASS` (in any form) is never human approval, under continuous execution or otherwise (unchanged from §18.12.2, §18.12.5).
- If a path stops before reaching its Gate (§18.20.C, §18.20.K), the boundary approval requested is a normal `Approve EXE-P0.n.X` for the stopped-at unit, likewise enumerating every path-internal unit committed since the path opened or last resumed.

#### H. Human Decision Requests (HDR)

- An HDR (a genuine ambiguity, source gap, or undecided contract question surfacing mid-path) causes an immediate stop, exactly as it always has.
- The human decision is recorded, exactly as it always has been (e.g. §18.14.5's HD-1–HD-4, §18.14.6's HQ-1–HQ-6).
- Reconciliation runs against the recorded decision.
- If the same authorized path (§18.20.C) remains valid after reconciliation, execution resumes automatically — no fresh `Execute` is required.
- If reconciliation surfaces a new HDR, or any §18.20.K blocking condition, the path stops again.
- Resolving an HDR never expands the authorized path (HD-CE-07). It never authorizes any unit or Capability outside the path that was already open when the HDR was raised.

#### I. Prerequisites

A prerequisite or remediation unit may execute automatically inside an authorized path **only when this execution plan itself explicitly designates that exact unit as path-internal for that exact path**. Being a "hard prerequisite" of another unit (in the sense already used at §18.15, §18.14.8) does not, by itself, authorize automatic execution — that status and this designation are independent; a unit can be a hard prerequisite without being plan-designated as path-internal (HD-CE-08).

**No currently registered prerequisite or remediation unit is newly designated path-internal by this amendment.** `REM-P0.2.B-01` and `REM-P0.2.B-02` are already executed and approved (historical, unaffected). `REM-P0.2.B-03` is classified at §18.20.J below.

#### J. Remediation classification

Every remediation unit is classified into exactly one of:

1. **Path-Internal** — explicitly designated by this plan as required within a specific authorized path; may execute automatically within that path (§18.20.I).
2. **Optional** — registered but not required by any path; never executes automatically.
3. **Separately Authorized** — requires its own independent human authorization regardless of any open path; never executes automatically.
4. **Unrelated** — out of scope of any current path entirely.

**`REM-P0.2.B-03` = Optional** (HD-CE-09). It remains registered, not executed, not a prerequisite of anything, and does not execute automatically under continuous execution.

#### K. Blocking conditions

Any one of the following unconditionally stops an authorized path (HD-CE-10):

1. `AI VERIFICATION: BLOCKED` on any unit.
2. An unresolved Human Decision Request or `SOURCE-GAP`.
3. A failure requiring human intervention.
4. A security/governance decision requiring human intervention.
5. Discovery of an out-of-scope file or change.
6. Discovery of an unrelated issue.
7. A pre-existing guard failure.
8. Reaching the path's own boundary (§18.20.C).
9. Reaching a Capability Gate.
10. An attempted crossing outside the authorized path (into another Capability, or into a unit the path does not name).
11. A governance-agent (Architect/Reviewer/Verifier) finding that `governance-matrix.yaml`'s escalation rules classify as requiring human escalation, independent of the unit's own AI Verification verdict.

Any one of these causes the path to stop; none is resolved by the orchestrator on its own.

#### L. Commit semantics

Unchanged, absolutely (HD-CE-11): exactly one commit per changing execution unit, scoped only to that unit's own files, carrying its own tests, its own governance-agent routing evidence where applicable, and its own AI Verification report; independently revertible without affecting any other unit's commit (§37). **Continuous execution never merges multiple execution units into one commit.**

#### M. Capability boundary

Absolute, unconditional (HD-CE-14): Capability N's authorization, however granted — including a Capability Gate approval — never automatically authorizes Capability N+1. Every Capability's own starting unit requires its own explicit human `Execute`, under both this document's pre-existing model and this section.

#### N. Cross-session reconstruction

An authorized path's state, after a session or context reset, is reconstructed from:
- git evidence (commit history, commit messages, commit trailers);
- this document's own status/position blocks (§18.14.8, §18.18, §52);
- the operator runbook's evidence (`docs/execution-plan-p0-steps.md` §20);
- root `CLAUDE.md`'s recorded state, as applicable;
- an explicit authorized-path field set in the runbook's §20, **to be added there in a separate, later correction pass** (this amendment does not itself edit the runbook — §18.20.Q), containing:
  - the Capability;
  - the authorized starting unit;
  - the authorized path boundary;
  - the completed units;
  - the current stopped unit, if any;
  - the stop reason, if any;
  - the HDR resolution status, if applicable;
  - a continuation-authorized flag.

Ambiguous reconstruction — where the evidence above does not unambiguously establish one of these eight facts — **fails closed**: it is reported as a `DOCUMENTATION-GAP`, and no automatic continuation occurs until a human resolves it. No separate state-tracking file is introduced anywhere in the repository (HD-CE-12).

**v1.1.1 note (§18.20.R):** "the completed units" above includes units that reached `AI VERIFIED — CONTINUING (NOT APPLICABLE)` on exactly the same footing as units that reached ordinary `AI VERIFIED — CONTINUING` — both are valid path-completion evidence for reconstruction purposes, and the deferred runbook §20 field (above) must be able to record either outcome per unit, once that later pass adds it.

#### O. Historical/non-retroactive rule and migration point

Every unit, remediation, and Gate approved before the migration point below was executed and approved under this document's pre-existing, per-unit model, in force at that time. No historical record is reinterpreted, relabeled, or retroactively described using continuous-execution terminology (HD-CE-15).

```
CONTINUOUS EXECUTION MODEL EFFECTIVE FROM: EXE-P0.2.C
```

- `EXE-P0.2.A` — prior (per-unit) model. Historical; unaffected.
- `EXE-P0.2.B` — prior (per-unit) model. Historical; unaffected.
- `EXE-P0.2.C` onward within Capability 2, and every sub-phase of every Capability from Capability 3 forward — this section's continuous model (HD-CE-03, Option B).

#### P. NO UNAUTHORIZED CHAINING

This is the authoritative current replacement for the historical "no automatic chaining" principle stated at §18.11.12 (retained there verbatim, annotated `SUPERSEDED BY §18.20` — not deleted, per HD-CE-16):

> **NO UNAUTHORIZED CHAINING:** Continuation from one execution unit to another may occur automatically only when the destination lies inside an execution path already opened by an explicit human `Execute` command under this section. Continuation outside that path is forbidden. No test result, commit, AI Verification PASS, or closed Gate ever expands the authorized path.

This restates, in path-scoped terms, the exact principle §18.11.12 has always required — that no success signal ever creates authorization — while additionally permitting automatic continuation strictly *within* an already-authorized path, which §18.11.12's original, pre-§18.20 wording did not contemplate.

#### Q. Preserved invariants

This amendment's only deliberate change is to **reduce the frequency of human-approval checkpoints within an already human-authorized Capability path.** It does not weaken, and this document continues to require in full:
- human authority over all authorization (§18.20.B, §18.20.E, §18.20.M);
- specialist governance-agent routing (§18.19, unchanged);
- Architect/Reviewer/Verifier requirements (§18.19.4–§18.19.5, unchanged);
- AI Verification for every unit (§18.12, unchanged);
- fail-closed behavior on any blocking condition (§18.20.K);
- Capability boundaries (§18.20.M);
- unit-level commit discipline (§18.20.L);
- evidence traceability (every path-internal unit's own AI Verification report and commit, unchanged);
- out-of-scope protections (§18.20.K.5–7);
- remediation/source-gap protections (§18.20.I–J);
- the no-invention/no-assumption rule (§18.19.6, unchanged);
- every existing governance gate (§18.10, §18.11, §18.12, §18.17).

This section does not itself amend `.claude/agents/eaioc-p0-execution-orchestrator.md`, `.claude/skills/eaioc-execution-governance/SKILL.md`, root `CLAUDE.md`, or `docs/execution-plan-p0-steps.md`. Those artifacts' own required amendments — orchestrator command semantics and lifecycle loop; skill eligibility/HDR-resume/§31-trigger wiring; `CLAUDE.md`'s "one rule that matters most" wording; the runbook's §20 authorized-path field (§18.20.N) — are deferred to separate, later, explicitly-directed correction passes. Until each of those passes is made, this document's continuous-execution model is authoritative but **not yet operative in practice**: the orchestrator and skill currently in the repository still implement only the pre-existing per-unit model, and continue to do so until they are separately amended to match this section.

#### R. N/A continuation (v1.1.1, HD-CE-17)

A `NOT APPLICABLE` sub-phase that is path-internal (i.e. not the path's boundary unit, §18.20.C) continues automatically, on the same footing as a PASS-class path-internal unit, subject to all of the following:

- its AI Verification verdict remains exactly `NOT APPLICABLE` — this section introduces no new verdict value (§18.12.1.J is unchanged);
- it produces no implementation commit, and none is ever fabricated to represent it (§18.16, unchanged);
- no individual human `Approve …` is required for it;
- it counts as completed for the purpose of path progression;
- it emits the distinct footer/state `### AI VERIFIED — CONTINUING (NOT APPLICABLE)` (§18.12.4, §18.17) — never ordinary `### AI VERIFIED — CONTINUING`, which remains defined only for a committed, PASS-class unit;
- it is never `HUMAN APPROVED`, and this section grants it no exception from that;
- it never expands the authorized path or authorizes anything outside it.

Outside a §18.20 authorized path, and for every unit executed before the migration point recorded at §18.20.O, §18.2/§18.16's original rule governs unchanged: an N/A unit still receives its own explicit human approval.

#### S. Completion-only guards (v1.1.1, HD-CE-18, HD-CE-19)

A downstream guard whose purpose is **only** to establish that a predecessor sub-phase completed, or reached a valid terminal state — the clearest existing example being §18.15's "Capability `<n>`.A has been approved" check — is, within the same authorized execution path as the unit performing the check, satisfied by any of:

1. `HUMAN APPROVED` — the historical per-unit model;
2. `AI VERIFIED — CONTINUING` — the continuous model, PASS-class predecessor, same authorized path;
3. `AI VERIFIED — CONTINUING (NOT APPLICABLE)` — the continuous model, `NOT APPLICABLE` predecessor, same authorized path (§18.20.R).

Options 2 and 3 apply **only** when the predecessor is path-internal to the exact same authorized path as the unit whose guard is being checked — never across a path boundary, never across a Capability (§18.20.M is unaffected: the "same authorized path" qualifier structurally forecloses this, since no path ever spans two Capabilities). **None of the three options above ever satisfies:** human approval; human authorization; Capability Gate approval; boundary approval; or any guard whose purpose specifically requires human review, authorization, or approval — a guard of that kind (e.g. "Capability 1 Gate has been approved," the other check in §18.15) is never a completion-only guard and is never affected by this subsection. This introduces no new global "approval" state; options 2 and 3 are exactly the two states already defined at §18.12.4/§18.17/§18.20.F/R, merely recognized here as valid evidence for this one, already-existing kind of check.

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

Per capability, at the code level: each Sub-phase that produces implementation changes is one git commit (Mode B's own git discipline, cited from the companion execution prompt; a genuinely `NOT APPLICABLE` sub-phase has no commit and therefore nothing to revert — §18.16); remediation units (§18.14) are likewise one commit each and individually revertible; a failed sub-phase's changes are reverted via ordinary git revert of that single commit, never a broader rollback, since no sub-phase's commit depends on an uncommitted state from a later sub-phase. At the data level: no durable data exists yet to roll back (§36). The Capability Gate itself has no implementation commit and therefore nothing to revert (§18.10) — a failed or blocked Gate means promotion to the next capability is denied until the blocking condition is addressed, not a code rollback; no special "gate commit" or "gate rollback" mechanism exists or is needed.

## 38. Source-Gap Register

This document's own scoped gap series, `SOURCE-GAP-EXECPLAN-NN`, distinct from every upstream/sibling document's own series:

*(v1.0.11: the register rows below are retained unmodified as history. Where a "Status update v1.0.11" row exists for a gap ID in the "Status updates (v1.0.11)" table after the register, it governs that row's "Blocking?" column; search the gap ID to find both the original row and its current status.)*

| Gap ID | Description | Origin | Impact | Blocking? |
|---|---|---|---|---|
| `SOURCE-GAP-EXECPLAN-01` | No interim telemetry sink is documented anywhere upstream for any capability's Sub-phase D beyond "cite `observability.md`'s already-named metrics" — this mirrors `SOURCE-GAP-IRG-02`'s finding for the Observability P0 item itself, but applies to all six in-scope capabilities' own Sub-phase D, not only the Observability item | This document's own re-verification while writing §18/§26 | Non-blocking — this document proposes a minimal structured-log interim sink (§26) as a reasonable, low-risk default, consistent with `ADR-0001`'s own "interim reference implementation" pattern | No |
| `SOURCE-GAP-EXECPLAN-02` | Two of the six in-scope capabilities (Context Policy, Output Controls) have no exact package-leaf match in `conventions.md` §2.1's tree (§9) | This document's own module-boundary verification | Non-blocking — proposed placements given, explicitly flagged for confirmation at each capability's own Sub-phase A rather than assumed final | No |
| `SOURCE-GAP-EXECPLAN-03` | No dedicated `SCN-*` scenario was found isolating Sanitizer, Context Policy, Prompt Assembler, or Output Controls specifically, beyond the cross-cutting `EC-077`/security-adjacent citations already recorded elsewhere in the corpus | This document's own targeted search while writing §18 | Non-blocking — recorded honestly per capability (§18.6–18.9's Sub-phase G entries), not force-fit to an adjacent scenario | No |
| `SOURCE-GAP-EXECPLAN-04` | *(added v1.0.4)* Shared-core-foundation ownership gap: through v1.0.3, `core/interfaces/`/`core/schemas/`/`core/errors/` were assigned to Capability 1 only in a header Preconditions field, never in any executable unit row, while Capabilities 2 and 3 assumed they existed. No executed unit produced them, so `INTF-030`'s `ControlPlaneRequest`/`OptimizationPlan` parameter types had no realization when `EXE-P0.2.B` was invoked. This is an execution-plan ownership gap, not an upstream-corpus gap — `interfaces.md` defines all three types | Exposed by the first `Execute EXE-P0.2.B` (stopped on precondition, no code written) | **Plan side resolved in v1.0.4** — ownership assigned to `EXE-P0.1.A` (§18.13) and a hard guard added (§18.15). **Implementation side open** until remediation units `REM-P0.1.A-01`/`-02` (§18.14.2) are AI-verified and human-approved | **Yes** — for `EXE-P0.2.B` and `EXE-P0.3.B` only, until remediation closes; no other unit or capability gate is blocked by it beyond Capability 1's own Gate review, which must see the remediation complete. *(v1.0.6 status: implementation side closed by `REM-P0.1.A-01`/`-02`, approved; final confirmation at the Capability 1 Gate)* |
| `SOURCE-GAP-EXECPLAN-05` | *(added v1.0.6)* 22 canonical §27.1 members of `CostLedgerEntry` have no type established by any authoritative source (`interfaces.md` §28.1 OD-28.1-A): five `cache` counters, five `model` members, three `tools` call counters, all five `workflow` members, `performance.ttft_ms`, and three `quality` members. `conventions.md` §3.6/§5.2 cover only token counts, monetary/savings amounts, scores and `latency_ms` | Exposed by the AC-005 contract reconciliation (D-4) | Plan side resolved: typed `SOURCE-UNRESOLVED`, not invented. Each type needs an explicit human decision in `REM-P0.1.B-01`, followed by a user-directed `interfaces.md` correction | ~~Yes~~ → **RESOLVED (v1.0.7)** by the explicit human-operator decisions HD-1 to HD-4 in `REM-P0.1.B-01`, promoted into `interfaces.md` §28.1 / `conventions.md` §14.1 by DB-2 (§18.14.5); no longer blocks `REM-P0.1.B-02` |
| `SOURCE-GAP-EXECPLAN-06` | *(added v1.0.6)* No authoritative source states whether a `null` "where available" value (`model.reasoning_tokens`, `cost.tool`; PS §8, SPEC §18.1) makes a ledger entry "incomplete" for `conventions.md` §14.1's `unverified` rule (`interfaces.md` §28.1 OD-28.1-B) | Exposed by the AC-005 contract reconciliation (D-3) | Recorded as an open decision; neither behavior may be assumed | ~~Yes~~ → **RESOLVED (v1.0.7)** by the explicit human-operator decisions HD-1 to HD-4 in `REM-P0.1.B-01`, promoted into `interfaces.md` §28.1 / `conventions.md` §14.1 by DB-2 (§18.14.5); no longer blocks `REM-P0.1.B-02` |
| `SOURCE-GAP-EXECPLAN-07` | *(added v1.0.8)* No authoritative source maps any `EvaluationRun` baseline measurement field (`baseline_cost`, `latency_ms_baseline`, `quality_score_baseline`, `task_success_baseline`, `cache_hit_rate`, `tokens_avoided_by_cache`) to a field of an existing measurement source, and no request-to-measurement retrieval contract exists. `CostLedgerEntry` (INTF-047) has same-looking fields but no declared mapping; `CostLedgerStore` reads only by `(tenant_id, entry_id)`; the Observability P0 item and any latency read path are outside this slice (`SOURCE-GAP-IRG-02`) | `EXE-P0.2.B` design-gap analysis; HQ-1 (§18.14.6) | `run_baseline()` cannot obtain any baseline measurement at P0 | **Blocking** for `EXE-P0.2.B`. *(v1.0.10: remediation unit `REM-P0.2.B-01` assigned, §18.14.7 — not started; gap still **OPEN**. Closure requires a subsequent user-directed source-contract correction)* |
| `SOURCE-GAP-EXECPLAN-08` | *(added v1.0.8)* INTF-030 makes only the four `*_optimized` fields nullable. No source defines BASELINE-only values for the non-nullable comparison-dependent fields (`gross_savings`, `optimizer_overhead`, `net_savings`, `net_savings_pct`, `quality_delta`, `latency_delta_ms`, `regression_detected`, `regression_dimensions`). `EvaluationRun` has no `verified` representation, so the ledger's DB-1/HD-4 placeholder policy cannot apply (HQ-2) | `EXE-P0.2.B` design-gap analysis; HQ-2 (§18.14.6) | A complete, correctly-typed BASELINE `EvaluationRun` cannot be constructed honestly | **Blocking** for `EXE-P0.2.B`. *(v1.0.10: remediation unit `REM-P0.2.B-01` assigned, §18.14.7 — not started; gap still **OPEN**. Closure requires a subsequent user-directed source-contract correction)* |
| `SOURCE-GAP-EXECPLAN-09` | *(added v1.0.8)* A standalone `EvaluationRun` carries no `tenant_id`. Tenant scoping is by store key (§12), but `compare()` receiving two runs cannot itself verify they belong to the same tenant (`conventions.md` §13.2) | `EXE-P0.2.B` Code Architect assessment | Relevant only once `compare()` is realized (HQ-4) | Non-blocking for `EXE-P0.2.B` |
| `SOURCE-GAP-EXECPLAN-10` | *(added v1.0.11)* `interfaces.md` §18 keeps `compare(baseline: EvaluationRun, optimized: EvaluationRun)` as documented forward contract (the fallback `compare(baselineRecord, EvaluationRun)` was not chosen), while `run_baseline()` now returns `BaselineEvaluationRecord`. Reconciling the two is deferred until an optimized execution path exists | Operator decision (2026-09-25); `interfaces.md` §18 | None at P0 (`compare()` is not exercised, HQ-4) | Open — non-blocking |
| `SOURCE-GAP-EXECPLAN-11` | *(added v1.0.11)* No source defines a producer for `quality_score_baseline`/`task_success_baseline`; the ledger's six `quality.*` members are different measures and also have no producer (`quality-gates.md` has none; the names occur only in declarations). The P0 baseline record excludes both fields | Operator decision D-D; HQ-2; `eval.md` §16/§21/§25; design note §3 | No quality or task-success baseline exists at P0 | Open — non-blocking for `EXE-P0.2.B`; open for any live quality baseline |
| `SOURCE-GAP-EXECPLAN-12` | *(added v1.0.11)* No source defines what value, if any, a non-nullable §27.1 member may hold in a `verified = true` entry when it is inapplicable (rather than unmeasurable) on an unoptimized Path A run: HD-1 legitimizes a measured zero for counters only, and HD-4/DB-1 tie `verified = false` to unmeasurable members | `interfaces.md` §28.1 (HD-1, HD-4, DB-1) | A `verified = true` Path A entry may not be attainable at P0 | Open — non-blocking for `EXE-P0.2.B`; blocks a live producer (`REM-P0.2.B-03`) |
| `SOURCE-GAP-EXECPLAN-13` | *(added v1.0.11)* The representation and error class of `run_baseline()`'s no-measurement outcome are not defined: INTF-030's return type is non-null and `interfaces.md` §25.2 gives OPT series-to-class mappings but no enumerated codes | HQ-2; `eval.md` §37; `interfaces.md` §25 | The failure-path test of `EXE-P0.2.B` needs an observable outcome | Deferred decision — to be resolved or escalated at `EXE-P0.2.B`'s pre-flight; no error code may be invented |
| `SOURCE-GAP-EXECPLAN-14` | *(added v1.0.11)* `verified` is defined in prose (`interfaces.md` §28.1; `conventions.md` §14.1 item 4) and realized in the Java `CostLedgerEntry` ("One field beyond INTF-047's literal list"), but is absent from the INTF-047 schema block. Adding it is not authorized by this correction | `interfaces.md` §28.1; `CostLedgerEntry.java` | Consumers of the schema block see no carrier | Open — non-blocking |
| `SOURCE-GAP-EXECPLAN-15` | *(added v1.0.11)* No unit defines the Path A capture mechanism or proving-lab placement (§3, §16; §6 row 10: Docker Compose is "Not required by any individual capability's own Minimal Implementation"), and the plan §17 comparison of Path B's overhead against Path A's baseline has no representation at P0. Assigned to the design of `REM-P0.2.B-03`. Also recorded here (INFERRED by the drafter, not an operator decision): INTF-047 has no Path A / Path B discriminator, so under the one-entry rule (`interfaces.md` §18) a Path A and a Path B entry for one request cannot both be present under the same `(tenant_id, request_id)` and still yield a measurement, and a later capability that ledgers baseline and optimized runs under one `request_id` (HQ-6, `eval.md` §15) would need a distinguishing field (a required-field addition, a MAJOR change under §24.1) | Plan §3, §16, §17; design note §2 | No live Path A measurement exists (deferred at Gate P0.2, §18.10) | Open — non-blocking for `EXE-P0.2.B` |

**Status updates (v1.0.11).** *(Additive; the register rows above are history.)*

| Gap ID | Status update v1.0.11 | Evidence | Remains |
|---|---|---|---|
| `SOURCE-GAP-EXECPLAN-07` | Status update v1.0.11: **RESOLVED AT SOURCE** (DB-3, §18.14.8). Previously *Blocking* for `EXE-P0.2.B` (row above) | `interfaces.md` §18 notes and §28.2 comment define the mappings (`baseline_cost`, `latency_ms_baseline`) and the request-to-entry retrieval rule (all matching entries for `(tenant_id, request_id)` are counted first; exactly one is required and it must then be `verified = true`; zero or several ⇒ no measurement — a human contract decision of 2026-09-25) by explicit operator decisions (D-A; the operator's decisions of 2026-09-25) | The retrieval realization is unit `REM-P0.2.B-02` (prerequisite of `EXE-P0.2.B`); live Path A measurement is `REM-P0.2.B-03` (deferred; `-12`, `-15`); the baseline fields not carried by the record are recorded in `-11` (quality, task success) and by D-D (cache) |
| `SOURCE-GAP-EXECPLAN-08` | Status update v1.0.11: **RESOLVED AT SOURCE** (DB-3, §18.14.8). Previously *Blocking* for `EXE-P0.2.B` (row above) | `BaselineEvaluationRecord` (`interfaces.md` §18) is the baseline-only representation (operator decision D-B); `EvaluationRun` is unchanged; no zero, placeholder or sentinel is used | The `compare()` parameter question (`-10`) |
| `SOURCE-GAP-EXECPLAN-09` | Status update v1.0.11: **NARROWED — still OPEN, non-blocking** | `BaselineEvaluationRecord` carries `tenant_id` (`interfaces.md` §18) | `EvaluationRun` still has none, so `compare()` on two `EvaluationRun`s cannot verify tenant equality; non-blocking until `compare()` is realized (HQ-4) |
| `SOURCE-GAP-EXECPLAN-13` | Status update v1.0.11 (human contract decision 2026-09-25): **NARROWED — still OPEN, non-blocking** | `interfaces.md` §18 notes ("No measurement, no record — the operation fails"): `run_baseline()` fails without returning a `BaselineEvaluationRecord`; no nullable record, outcome wrapper, new `ControlPlaneError` code or `error_class` value, or `PartialSuccess` is introduced | The concrete failure mechanism is a unit-level concern until an authoritative error-transport contract exists; `interfaces.md` §25 still defines no enumerated codes and no conveyance of errors |

**Contradiction register (v1.0.6):**

| ID | Contradiction | Status |
|---|---|---|
| `CONTRA-EXECPLAN-01` | `interfaces.md` §28.1 `INTF-047 CostLedgerEntry` did not represent the full `architecture.md` §27.1 standard ledger model required by PS §8, SPEC §18.1 and `conventions.md` §14.1. This frozen-source inconsistency meant AC-005's compressed, retrieved and reused categories had no field at all | **Resolved at source** by the v1.0.6 correction of `interfaces.md` §28.1 (nested canonical groups) and `conventions.md` §14.1, under the precedence in `conventions.md`'s header. **Implementation remediation pending**: `REM-P0.1.B-01`/`-02` (§18.14.4) |

**Disposition of gaps carried forward from other documents:** `SOURCE-GAP-IRG-02` (Observability interim path) — explicitly out of this slice's scope (§3), not resolved here. `SOURCE-GAP-OPTCAT-01`/`AGENTOPT-01` — not touched, since P1+ `DA-NNN`/session-phase content is out of scope. *v1.0.11 note:* `SOURCE-GAP-EVAL-01`: OPEN (unchanged) — `RegressionReport` remains undefined in `interfaces.md` §18 and `report_regression()` is not declared; not closed by v1.0.11. `SOURCE-GAP-IRG-02`: OPEN (unchanged) — no interim telemetry path is documented for the Observability P0 item; Observability stays outside this execution slice (§3) and no Observability read path is introduced (HQ-1); not closed by v1.0.11. `-07`/`-08` are resolved at source and `-09` narrowed (status updates above); `-10` to `-15` are registered.

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

**v1.0.6 clarification:** `FULLY TRACED` is a document-level trace in `requirements-traceability.md`'s controlled vocabulary (its line 21). It is **not** evidence of implementation. `AC-005` is traced to `architecture.md` §27.1, but the historical Capability 1 implementation did not satisfy it (`CONTRA-EXECPLAN-01`, §18.14.4). `AC-005` is satisfied only after `REM-P0.1.B-01`/`-02`.

## 41. Acceptance-Criteria Traceability

`AC-001`, `AC-002`, `AC-005` (Capability 1, ledger); `AC-015`, `AC-017` (Capability 2, benchmarking) — all `FULLY TRACED` per `requirements-traceability.md` §6.5, independently re-confirmed there, not recomputed here. That is a document-level trace, not implementation evidence: `AC-005` remains unmet by the implementation until `REM-P0.1.B-02` (§40, §18.14.4). No in-scope capability touches a `PARTIALLY TRACED` AC (`AC-035`, `AC-041`, `AC-046`, `AC-049` are all P1+/P4-tier concerns, outside this slice).

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

**v1.0.4 — PLAN READY vs. MODE B READY (distinct, not conflated).** `PLAN READY` means the command and verification protocol is defined (§18.11–§18.18). `MODE B READY` means all of the following hold for the next executable unit: environment readiness has passed; the current execution-plan version is accepted; the AI verification protocol is active; the human approval protocol is active; shared-foundation ownership is resolved; and no known blocking prerequisite remains for the next executable unit. Status at v1.0.4 correction time:

```
PLAN READY            — YES, after the v1.0.10 correction (pending the operator's acceptance of this version)
MODE B READY          — for REM-P0.2.B-01: once this v1.0.10 correction is committed (registered, not
                        started, §18.14.7; its own pre-flight re-checks every precondition)
                      — for EXE-P0.2.B: NO — the §18.15 guard passes (Capability Gate P0.1 approved
                        2026-09-23), but SOURCE-GAP-EXECPLAN-07/-08 are OPEN and block an honest
                        EvaluationRun (§18.14.6, §38); REM-P0.2.B-01 and then a user-directed
                        source-contract correction are required first
CODE COMPLETE         — not reached
BENCHMARK VALIDATED   — not reached
```

**v1.0.11 status (supersedes the v1.0.10 status block above, which is retained as history):**

```text
PLAN READY            — YES, after the v1.0.11 correction (pending the operator's acceptance of this version)
MODE B READY          — for REM-P0.2.B-02: once this v1.0.11 correction is committed (registered, not
                        executed, §18.14.8; its own pre-flight re-checks every precondition)
                      — for REM-P0.2.B-03: registered, not scheduled; NOT a prerequisite of EXE-P0.2.B
                      — for EXE-P0.2.B: NO — the §18.15 guard's original checks pass (Capability Gate P0.1
                        approved 2026-09-23) and SOURCE-GAP-EXECPLAN-07/-08 are resolved at source (v1.0.11),
                        but the retrieval realization REM-P0.2.B-02 is not yet executed and approved
                        (§18.14.8, §18.15). EXE-P0.2.B is contract-level and fixture-based; live Path A
                        end-to-end baseline measurement: NOT DEMONSTRATED — explicitly deferred (Gate P0.2, §18.10)
CODE COMPLETE         — not reached
BENCHMARK VALIDATED   — not reached
```

*(The v1.0.11 status block above is retained as history; the v1.0.12 status block below supersedes it.)*

**v1.0.12 status (supersedes the v1.0.11 status block above, which is retained as history):**

```text
PLAN READY            — YES
MODE B READY          — for REM-P0.2.B-02: DONE. Executed, AI-verified (PASS), and human-approved
                        (bf74ac3, 2026-09-27).
                      — for REM-P0.2.B-03: registered, not scheduled; NOT a prerequisite of EXE-P0.2.B
                      — for EXE-P0.2.B: NO — the §18.15 guard's original checks pass (Capability Gate P0.1
                        approved 2026-09-23), SOURCE-GAP-EXECPLAN-07/-08 are resolved at source (v1.0.11),
                        and the retrieval realization REM-P0.2.B-02 is now executed and approved — but
                        EXE-P0.2.B's own pre-flight (§18.19.3), a fresh §18.15 guard check, and a Code
                        Architect re-assessment have not yet run. EXE-P0.2.B is contract-level and
                        fixture-based; live Path A end-to-end baseline measurement: NOT DEMONSTRATED —
                        explicitly deferred (Gate P0.2, §18.10)
CODE COMPLETE         — not reached
BENCHMARK VALIDATED   — not reached
```

*(The v1.0.12 status block above is retained as history; the v1.0.13 status block below supersedes it.)*

**v1.0.13 status (supersedes the v1.0.12 status block above, which is retained as history):**

```text
PLAN READY            — YES
MODE B READY          — for REM-P0.2.B-02: DONE (unchanged from v1.0.12).
                      — for REM-P0.2.B-03: registered, not scheduled; NOT a prerequisite of EXE-P0.2.B
                      — for EXE-P0.2.B: DONE. Executed, AI-verified (PASS), and human-approved
                        (a2d022f, 2026-09-27). 92/92 tests passing. Contract-level and fixture-based;
                        live Path A end-to-end baseline measurement: NOT DEMONSTRATED — explicitly
                        deferred (Gate P0.2, §18.10)
                      — for EXE-P0.2.C: NO — not executed, not authorized. Approve EXE-P0.2.B covers
                        only Sub-phase B
CODE COMPLETE         — not reached
BENCHMARK VALIDATED   — not reached
```

*(The v1.0.13 status block above is retained as history; the v1.1.0 status block below supersedes it.)*

**v1.1.0 status (supersedes the v1.0.13 status block above, which is retained as history; structural amendment, not a state-refresh — §18.20 adopted, no unit executed by this correction itself):**

```text
PLAN READY            — YES
MODE B READY          — for EXE-P0.2.B: DONE (unchanged from v1.0.13).
                      — for EXE-P0.2.C: NOT EXECUTED. Issuing Execute EXE-P0.2.C now opens the §18.20
                        authorized path for Capability 2's remaining sub-phases (C through H) through
                        Capability Gate P0.2 (§18.20.B–C), per the migration point recorded at §18.20.O.
                        This correction does not itself execute, verify, or approve EXE-P0.2.C.
CODE COMPLETE         — not reached
BENCHMARK VALIDATED   — not reached
```

*(The v1.1.0 status block above is retained as history; the v1.1.1 status block below supersedes it.)*

**v1.1.1 status (supersedes the v1.1.0 status block above, which is retained as history; further structural governance correction — HD-CE-17/18/19 implemented, no unit executed by this correction itself):**

```text
PLAN READY            — YES
MODE B READY          — for EXE-P0.2.B: DONE (unchanged from v1.0.13/v1.1.0).
                      — for EXE-P0.2.C: NOT EXECUTED. Unchanged repository-state fact. What is corrected
                        is the model's own internal consistency: Sub-phase F's NOT APPLICABLE disposition
                        (§18.5) now resolves to the distinct ### AI VERIFIED — CONTINUING (NOT APPLICABLE)
                        footer (§18.12.4, §18.20.R), not ordinary CONTINUING; and §18.15's "Capability
                        2.A has been approved" completion-only check now correctly recognizes CONTINUING/
                        CONTINUING (NOT APPLICABLE) as valid completion evidence within the same path
                        (§18.20.S), while a Gate-level or boundary-approval check is never so satisfied.
CODE COMPLETE         — not reached
BENCHMARK VALIDATED   — not reached
```

*(The v1.0.5 status — `MODE B READY` for `REM-P0.1.A-01` — is superseded: `REM-P0.1.A-01`/`-02` are done and approved.)* *(The v1.0.6 status — `MODE B READY` for `REM-P0.1.B-01` — is superseded: `REM-P0.1.B-01` is done and approved.)* Neither `CODE COMPLETE` nor `BENCHMARK VALIDATED` is stated or implied by v1.0.7. *(The v1.0.7 status — `MODE B READY` for `REM-P0.1.B-02`, and `EXE-P0.2.B` NO on Gate P0.1 — is superseded: both conditions are resolved, and `EXE-P0.2.B` is now blocked on `SOURCE-GAP-EXECPLAN-07`/`-08`.)*

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
Six capabilities, 8 lifecycle sub-phases (A–H) each = 48 atomic `EXE-P0.<n>.<letter>` execution units, plus 6 Capability Gates (review/promotion checkpoints — no ID, no commit) = 54 total gated checkpoints across the P0 tier — §18.4–18.10. From v1.0.4, every unit is AI-verified before human approval and every Gate is AI-verified before human Gate approval (§18.12, §18.11.6); N/A units carry no commit (§18.16); history-specific remediation units (`REM-P0.1.A-01`/`-02`, §18.14) are additional corrective checkpoints outside this count. *(v1.0.10: so are `REM-P0.1.B-01`/`-02` and the pre-execution remediation `REM-P0.2.B-01`, §18.14.4, §18.14.7.)* *(v1.0.11: and `REM-P0.2.B-02`/`-03`, §18.14.8.)* *(v1.1.0: §18.20 adopts continuous execution within an authorized Capability path, effective from `EXE-P0.2.C`; the 48+6=54 count and the one-commit-per-changing-unit rule are unchanged — §18.20.L.)* *(v1.1.1: N/A sub-phases (still no commit, §18.16) that are path-internal now correctly emit `AI VERIFIED — CONTINUING (NOT APPLICABLE)` rather than ordinary `CONTINUING`, §18.20.R; the 48+6=54 count is unaffected.)*

### I. First Proving Slice
Docker Compose reference environment; Path A (baseline) vs. Path B (through the six-capability EAIOC substrate); no optimization savings claimed yet, only overhead measured honestly — §16–17.

### J. ADR Dependency Map
Zero of six ADRs block any in-scope capability; two (0004, 0005) are loosely touched via interim/moot dispositions — §39.

### K. Source-Gap Implementation Register
Four `SOURCE-GAP-EXECPLAN-NN` entries — §38: `SOURCE-GAP-EXECPLAN-01` — non-blocking; `SOURCE-GAP-EXECPLAN-02` — non-blocking; `SOURCE-GAP-EXECPLAN-03` — non-blocking; `SOURCE-GAP-EXECPLAN-04` (shared-core ownership, added v1.0.4) — **blocking implementation-side gap until remediation**: plan side resolved; implementation side open until remediation completes, blocking `EXE-P0.2.B`/`EXE-P0.3.B` — §18.13–§18.15. **v1.0.6:** `-04`'s implementation side is now closed by the approved `REM-P0.1.A-01`/`-02`. It adds `SOURCE-GAP-EXECPLAN-05` (22 `SOURCE-UNRESOLVED` ledger types) and `-06` (availability vs. `unverified`), both blocking `REM-P0.1.B-02` until decided, and the contradiction `CONTRA-EXECPLAN-01` (`INTF-047` vs §27.1; resolved at source, implementation remediation pending). **Current (v1.0.8/v1.0.9):** nine entries — `-01`–`-03` non-blocking; `-04` closed (implementation side, `REM-P0.1.A-01`/`-02`); `-05`/`-06` resolved (v1.0.7, DB-2); `-07`/`-08` **blocking `EXE-P0.2.B`** (added v1.0.8); `-09` non-blocking (added v1.0.8). `CONTRA-EXECPLAN-01` is closed at source and in code (`c378dc9`). **v1.0.10:** unchanged gap list; `-07`/`-08` remain **OPEN** and blocking, with remediation unit `REM-P0.2.B-01` registered (not started, §18.14.7). **v1.0.11:** `REM-P0.2.B-01` executed and approved; `-07`/`-08` resolved at source and `-09` narrowed (§38 status updates), `-10` to `-15` added (fifteen entries); `REM-P0.2.B-02`/`-03` registered, not executed (§18.14.8); `EXE-P0.2.B` not yet executable. **v1.0.12:** `REM-P0.2.B-02` executed, AI-verified (`PASS`) and human-approved (`bf74ac3`, 2026-09-27), clearing that named precondition; `REM-P0.2.B-03` remains registered, not executed, not a prerequisite; `EXE-P0.2.B` still not yet executable pending its own pre-flight, a fresh §18.15 guard check, and a Code Architect re-assessment. Gap list unchanged. **v1.0.13:** `EXE-P0.2.B` executed, AI-verified (`PASS`) and human-approved (`a2d022f`, 2026-09-27; 92/92 tests); `EXE-P0.2.C` not executed, not authorized; Capability Gate P0.2 not authorized. Gap list unchanged. **v1.1.0:** no new source gap; §18.20 (Continuous Phase-Driven Execution Model) adopted, effective prospectively from `EXE-P0.2.C`; `EXE-P0.2.C` remains not executed, not authorized by this correction itself. Gap list unchanged. **v1.1.1:** no new source gap; HD-CE-17/18/19 implemented (N/A continuation footer, §18.15 completion-guard semantics, three v1.1.0 drafting omissions fixed); `EXE-P0.2.C` remains not executed, not authorized by this correction itself. Gap list unchanged.

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
Zero conflicts between the technology baseline and the frozen architecture; zero ADR blockers; zero requirement-traceability blockers for this slice's own requirement set; four execution-plan source gaps recorded, not silently omitted — `SOURCE-GAP-EXECPLAN-01`, `-02`, `-03` non-blocking; `SOURCE-GAP-EXECPLAN-04` a **blocking implementation-side gap until remediation** (plan side resolved; implementation side open until remediation completes), and the current blocker for `EXE-P0.2.B`. **v1.0.6:** `-04` closed on the implementation side by the approved A remediation. `-05` and `-06` block `REM-P0.1.B-02` until decided. `CONTRA-EXECPLAN-01` is resolved at source, with its implementation remediation pending; it is the current Capability 1 Gate blocker.

### V. Readiness Boundary
PLAN READY (this document) ≠ IMPLEMENTATION READY (already true per `implementation-readiness-gate.md`, independently) ≠ CODE COMPLETE / TEST COMPLETE / BENCHMARK VALIDATED / PRODUCTION READY / ENTERPRISE SCALE READY (none reached, none implied) — §52.

---

## Execution Plan Readiness

Per this document's own required self-check: (1) the technology baseline was validated line-by-line against the frozen architecture and every open ADR, with zero conflicts found and every deferred technology explicitly classified (§6). (2) All six in-scope P0 capabilities were decomposed into atomic `EXE-P0.<n>.<letter>` units carrying every field the generation prompt's §10 requires, compacted at the capability level only where a field is genuinely sub-phase-invariant (§18.2, disclosed as a methodology choice, not a silent omission). (3) Module boundaries were checked against `conventions.md` §2.1 directly; two genuine gaps were found and recorded honestly rather than papered over (§9, `SOURCE-GAP-EXECPLAN-02`). (4) No ADR blocks this execution slice (§39, independently re-confirmed against `implementation-readiness-gate.md` §9's own table). (5) Requirements/acceptance-criteria traceability for this slice's specific requirement set is fully clear (§40–41). (6) Three new, non-blocking source gaps were recorded (§38) rather than invented around. (7) P1–P5 are explicitly not atomized, consistent with `implementation-readiness-gate.md` §26's own finding that P0 clearance does not unlock P1 (§19–23). (8) The readiness-boundary distinctions (§52) are stated explicitly, matching this project's own established discipline of never letting a planning artifact imply more than it has actually verified. (9) File-scope discipline was maintained — only `docs/execution-plan.md` was created this generation; no upstream, sibling, or ADR document was modified.

*The paragraph above is the original v1.0.0 generation self-check, retained as history.* Its original closing conclusion — which found no blocking issue and declared the plan ready for Mode B — held at generation time, when no unit had yet run. It is **superseded** by the current readiness conclusion below and must not be read as current guidance.

**Current readiness conclusion (v1.0.10; v1.0.9 added execution assistance, §18.19; v1.0.10 registers `REM-P0.2.B-01`, §18.14.7, without changing `EXE-P0.2.B`'s readiness).** *(Supersedes the v1.0.5 wording of items 8 and 9 and of the readiness statement; items 1–7 and 10 are unchanged in substance.)*

1. **Technology baseline:** its validation is unchanged (§6): zero conflicts, and all six ADRs remain `PROPOSED`.
2. **Counts:** P0 contains **48** atomic `EXE-P0.<n>.<letter>` execution units (8 lifecycle sub-phases A–H × 6 capabilities) plus **6** Capability Gates, for **54** gated checkpoints. The remediation units `REM-P0.1.A-01`/`-02`, `REM-P0.1.B-01`/`-02` and `REM-P0.2.B-01` sit outside these counts.
3. **Gate:** the Capability Gate is separate from A–H — a post-lifecycle review/promotion checkpoint with no `EXE-P0` ID and no implementation commit.
4. **Sequencing:** strict sequential execution is unchanged — one capability and one unit at a time.
5. **AI verification first:** AI Verification precedes human approval for every unit (§18.12). AI Gate Verification precedes human Gate approval (§18.11.6).
6. **Human approval:** it is always explicit (`Approve …`) and never simulated.
7. **No automatic chaining:** no test result, commit, AI PASS, or closed Gate authorizes the next unit (§18.11.12).
8. **Current blocker (v1.0.8):** `SOURCE-GAP-EXECPLAN-07` (no baseline measurement has both a field mapping and a request-to-measurement retrieval contract) and `SOURCE-GAP-EXECPLAN-08` (no BASELINE-only representation for INTF-030's non-nullable comparison-dependent fields), recorded with the operator's HQ-1 to HQ-6 decisions (§18.14.6). `CONTRA-EXECPLAN-01` is closed at source and in code (`REM-P0.1.B-02`, `c378dc9`). `SOURCE-GAP-EVAL-01` remains open.
9. **Repository position:** as recorded in §18.18.
   - Capability 1: **closed** — A–E, G, H executed and approved; F `NOT APPLICABLE`; `REM-P0.1.A-01`/`-02`, `REM-P0.1.B-01`/`-02` done and approved; Capability Gate P0.1 approved (2026-09-23).
   - Capability 2: `EXE-P0.2.A` executed and approved; `REM-P0.2.B-01` registered, not started (v1.0.10, §18.14.7); `EXE-P0.2.B` blocked on `SOURCE-GAP-EXECPLAN-07`/`-08` (open).
10. **Scope of this correction:** documentation/governance only. No source code, test, contract, or `control_plane/` file changed.

> **PLAN READY: YES — after this v1.0.10 correction.**
>
> **MODE B READY for `REM-P0.2.B-01`: once this v1.0.10 correction is committed (subject to its own pre-flight).**
>
> **MODE B READY for `EXE-P0.2.B`: NO — `SOURCE-GAP-EXECPLAN-07`/`-08` are OPEN. They must first be closed by a user-directed source-contract correction that follows `REM-P0.2.B-01` (§18.14.7). Capability Gate P0.1 is approved and the §18.15 guard passes.**

The next executable unit, once this correction is committed, is `REM-P0.2.B-01` (`Execute REM-P0.2.B-01`). `EXE-P0.2.B` may be re-issued only after that unit is approved **and** a user-directed source-contract correction closes `SOURCE-GAP-EXECPLAN-07`/`-08`, with a Code Architect re-assessment before any code. *(The v1.0.9 wording — "No unit is currently executable" — is superseded.)*

*(The conclusion above is the v1.0.10 conclusion, retained as history; the v1.0.11 conclusion below supersedes it.)*

**Current readiness conclusion (v1.0.11; v1.0.10 registered `REM-P0.2.B-01`, §18.14.7; v1.0.11 promotes its decisions (DB-3) and registers `REM-P0.2.B-02`/`-03`, §18.14.8).** *(Items 1 and 3–7 of the v1.0.10 conclusion are unchanged; items 2 and 8–10 are restated.)*

- **Item 2 — counts:** 48 atomic units + 6 Capability Gates = 54 gated checkpoints, unchanged. The remediation units `REM-P0.1.A-01`/`-02`, `REM-P0.1.B-01`/`-02`, `REM-P0.2.B-01`, `REM-P0.2.B-02` and `REM-P0.2.B-03` sit outside these counts.
- **Item 8 — current condition for `EXE-P0.2.B`:** `SOURCE-GAP-EXECPLAN-07`/`-08` are resolved at source (§38 status updates); the retrieval realization `REM-P0.2.B-02` must first be executed, AI-verified and approved (§18.14.8, §18.15). `SOURCE-GAP-EVAL-01`: OPEN (unchanged). `SOURCE-GAP-IRG-02`: OPEN (unchanged).
- **Item 9 — repository position (§18.18):** Capability 1 closed. Capability 2: `EXE-P0.2.A` executed and approved; `REM-P0.2.B-01` executed (`e4cf1f5`), AI-verified and approved; `REM-P0.2.B-02`/`-03` registered, not executed; `EXE-P0.2.B` not yet executable (requires `REM-P0.2.B-02`).
- **Item 10 — scope of this correction:** documentation, governance and source-contract text. Changed: `docs/interfaces.md` (contract text), `docs/eval.md` and `docs/implementation-plan.md` (additive correction notes), `docs/execution-plan.md`, `docs/execution-plan-p0-steps.md` (operator runbook; the plan wins on any conflict), root `CLAUDE.md` (governance state only). No source code, test, `control_plane/` file, count or existing unit ID changed.

> **PLAN READY: YES — after this v1.0.11 correction.**
>
> **MODE B READY for `REM-P0.2.B-02`: once this v1.0.11 correction is committed (subject to its own pre-flight).** `REM-P0.2.B-03` is registered and not scheduled; it is not a prerequisite of `EXE-P0.2.B`.
>
> **MODE B READY for `EXE-P0.2.B`: NOT YET — `SOURCE-GAP-EXECPLAN-07`/`-08` are resolved at source (§38), but the retrieval realization `REM-P0.2.B-02` must first be executed, AI-verified and approved (§18.14.8, §18.15). `EXE-P0.2.B` is a contract-level, fixture-based unit; live Path A end-to-end baseline measurement: NOT DEMONSTRATED — explicitly deferred (§18.14.8; Gate P0.2, §18.10).**

The next executable unit, once this correction is committed, is `REM-P0.2.B-02` (`Execute REM-P0.2.B-02`). `EXE-P0.2.B` may be re-issued only after that unit is approved, with a pre-flight (§18.19.3), the §18.15 guard and a Code Architect re-assessment before any code; only the concrete failure mechanism of the no-measurement outcome (`SOURCE-GAP-EXECPLAN-13`, narrowed) is a unit-level matter at that pre-flight; the outcome itself is decided (operation failure). *(The v1.0.10 wording — `Execute REM-P0.2.B-01` as the next unit — is superseded.)*

*(The conclusion above is the v1.0.11 conclusion, retained as history; the v1.0.12 conclusion below supersedes it.)*

**Current readiness conclusion (v1.0.12; documentation-only state refresh recording `REM-P0.2.B-02`'s completed execution).** *(Items 1–7 and 10 of the v1.0.11 conclusion are unchanged; items 8–9 are restated, and the next-executable-unit statement is updated.)*

- **Item 8 — current condition for `EXE-P0.2.B`:** `SOURCE-GAP-EXECPLAN-07`/`-08` are resolved at source (§38 status updates, unchanged); the retrieval realization `REM-P0.2.B-02` has been executed, AI-verified (`PASS`) and human-approved (`bf74ac3`, 2026-09-27) — that named precondition is cleared. `EXE-P0.2.B` remains not yet executable: its own pre-flight (§18.19.3), a fresh §18.15 guard check, and a Code Architect re-assessment have not yet run. `SOURCE-GAP-EVAL-01`: OPEN (unchanged). `SOURCE-GAP-IRG-02`: OPEN (unchanged).
- **Item 9 — repository position (§18.18):** Capability 1 closed. Capability 2: `EXE-P0.2.A` executed and approved; `REM-P0.2.B-01` executed (`e4cf1f5`), AI-verified and approved; `REM-P0.2.B-02` executed (`bf74ac3`), AI-verified (`PASS`) and approved (2026-09-27); `REM-P0.2.B-03` registered, not executed, not a prerequisite; `EXE-P0.2.B` still not yet executable (its own pre-flight/guard-check/Architect re-assessment pending).

> **PLAN READY: YES.**
>
> **MODE B READY for `REM-P0.2.B-02`: DONE — executed, AI-verified (`PASS`), and human-approved (`bf74ac3`, 2026-09-27).** `REM-P0.2.B-03` is registered and not scheduled; it is not a prerequisite of `EXE-P0.2.B`.
>
> **MODE B READY for `EXE-P0.2.B`: NOT YET — `SOURCE-GAP-EXECPLAN-07`/`-08` are resolved at source (§38), and the retrieval realization `REM-P0.2.B-02` is now executed, AI-verified and approved (§18.14.8, §18.15). What remains is `EXE-P0.2.B`'s own pre-flight (§18.19.3), a fresh §18.15 guard check against the now-existing retrieval realization, and a Code Architect re-assessment — none of which has run. `EXE-P0.2.B` is a contract-level, fixture-based unit; live Path A end-to-end baseline measurement: NOT DEMONSTRATED — explicitly deferred (§18.14.8; Gate P0.2, §18.10).**

The next executable unit, once this correction is committed, is `EXE-P0.2.B` (`Execute EXE-P0.2.B`), subject to its own pre-flight (§18.19.3), the §18.15 guard (with its v1.0.11 addition) and a Code Architect re-assessment before any code; only the concrete failure mechanism of the no-measurement outcome (`SOURCE-GAP-EXECPLAN-13`, narrowed) is a unit-level matter at that pre-flight; the outcome itself is decided (operation failure). `REM-P0.2.B-03` remains registered, optional, and not a prerequisite. *(The v1.0.11 wording — `Execute REM-P0.2.B-02` as the next unit — is superseded.)*

*(The conclusion above is the v1.0.12 conclusion, retained as history; the v1.0.13 conclusion below supersedes it.)*

**Current readiness conclusion (v1.0.13; documentation-only state refresh recording `EXE-P0.2.B`'s completed execution).** *(Items 1–7 and 10 of the v1.0.11 conclusion are unchanged; items 8–9 are restated, and the next-executable-unit statement is updated.)*

- **Item 8 — current condition for `EXE-P0.2.C`:** `EXE-P0.2.B` has been executed, AI-verified (`PASS`) and human-approved (`a2d022f`, 2026-09-27) — 92/92 tests passing. `EXE-P0.2.C` (Integration) has not been executed and is not authorized; `Approve EXE-P0.2.B` covers only Sub-phase B (§18.11.12, no automatic chaining). `SOURCE-GAP-EVAL-01`: OPEN (unchanged). `SOURCE-GAP-IRG-02`: OPEN (unchanged).
- **Item 9 — repository position (§18.18):** Capability 1 closed. Capability 2: `EXE-P0.2.A` executed and approved; `REM-P0.2.B-01`/`REM-P0.2.B-02` executed, AI-verified and approved; `REM-P0.2.B-03` registered, not executed, not a prerequisite; `EXE-P0.2.B` executed (`a2d022f`), AI-verified (`PASS`) and human-approved (2026-09-27); `EXE-P0.2.C` not executed, not authorized; Capability Gate P0.2 not authorized.

> **PLAN READY: YES.**
>
> **MODE B READY for `EXE-P0.2.B`: DONE — executed, AI-verified (`PASS`), and human-approved (`a2d022f`, 2026-09-27).** 92/92 tests passing. Reviewed by Architect (`APPROVED FOR IMPLEMENTATION`), Reviewer (`PASS WITH REQUIRED FOLLOW-UP`, resolved), and Verifier (`CONFORMANT WITH DOCUMENTED GAPS`); the Architect/Reviewer/Verifier routing ran after implementation rather than before, a disclosed deviation from §18.19.4/§18.19.5's normal sequence that produced no required design change. `EXE-P0.2.B` remains a contract-level, fixture-based unit; live Path A end-to-end baseline measurement: NOT DEMONSTRATED — explicitly deferred (§18.14.8; Gate P0.2, §18.10).
>
> **MODE B READY for `EXE-P0.2.C`: NOT YET — not executed, not authorized.** `Approve EXE-P0.2.B` approved only Sub-phase B; `EXE-P0.2.C` needs its own explicit `Execute EXE-P0.2.C`, its own pre-flight, and its own AI Verification before any human approval. Capability Gate P0.2 is reachable only after Sub-phases C–H are each separately executed and approved.

The next executable unit, once this correction is committed, is `EXE-P0.2.C` (`Execute EXE-P0.2.C`), subject to its own pre-flight (§18.19.3) and routing. `REM-P0.2.B-03` remains registered, optional, and not a prerequisite. *(The v1.0.12 wording — `Execute EXE-P0.2.B` as the next unit — is superseded.)*

*(The conclusion above is the v1.0.13 conclusion, retained as history; the v1.1.0 conclusion below supersedes it.)*

**Current readiness conclusion (v1.1.0; structural execution-authorization amendment adopting §18.20, per the frozen HD-CE-01–HD-CE-16 human decision register — not a state refresh; decides no new fact about repository state).** *(Items 1–7 and 10 of the v1.0.11 conclusion are unchanged in substance; item 7's "no automatic chaining" is now qualified, not repealed, by §18.20 — see §18.11.12's own annotation. Items 8–9 are restated below.)*

- **Item 7 — no automatic chaining, qualified:** §18.11.12's rule is retained verbatim and annotated `SUPERSEDED BY §18.20` — it remains fully governing outside a §18.20 authorized path. Within such a path, §18.20.P's "NO UNAUTHORIZED CHAINING" is the current authoritative statement: no success signal ever expands what is authorized; only automatic continuation strictly inside an already-authorized path is newly permitted.
- **Item 8 — current condition for `EXE-P0.2.C`:** unchanged from v1.0.13 as a repository-state fact — `EXE-P0.2.C` has not been executed and is not authorized by anything that has happened so far. What changes is the *meaning* of issuing `Execute EXE-P0.2.C` going forward: per §18.20.B–C, it will open the authorized path for Capability 2's remaining sub-phases (C–H) through Capability Gate P0.2, rather than authorizing only Sub-phase C. `SOURCE-GAP-EVAL-01`: OPEN (unchanged). `SOURCE-GAP-IRG-02`: OPEN (unchanged).
- **Item 9 — repository position (§18.18):** unchanged from v1.0.13 — Capability 1 closed; Capability 2: A and B executed and approved (historical, under the prior model, §18.20.O); `REM-P0.2.B-01`/`REM-P0.2.B-02` executed and approved; `REM-P0.2.B-03` registered, not executed, classified `Optional` (§18.20.J); `EXE-P0.2.C` not executed, not authorized; Capability Gate P0.2 not authorized.
- **Item 11 — new in this correction:** §18.20 is adopted as this document's authoritative continuous-execution model, effective prospectively from `EXE-P0.2.C` (§18.20.O). It is not yet operative in practice: `.claude/agents/eaioc-p0-execution-orchestrator.md` and `.claude/skills/eaioc-execution-governance/SKILL.md` are unchanged by this correction and continue to implement only the pre-existing per-unit model until they are separately amended to match §18.20 (§18.20.Q).

> **PLAN READY: YES.**
>
> **MODE B READY for `EXE-P0.2.B`: DONE (unchanged from v1.0.13).**
>
> **MODE B READY for `EXE-P0.2.C`: NOT YET EXECUTED — not executed, not authorized by this correction.** Once issued, `Execute EXE-P0.2.C` opens the §18.20 authorized path for Capability 2's remaining sub-phases through Capability Gate P0.2 (§18.20.B–C, §18.18's v1.1.0 sequence block) — subject to its own pre-flight (§18.19.3), routing, and every §18.20.K blocking condition.

The next executable unit is unchanged: `EXE-P0.2.C` (`Execute EXE-P0.2.C`), subject to its own pre-flight (§18.19.3) and routing — now understood, per §18.20, as opening the path through Capability Gate P0.2 rather than authorizing only Sub-phase C. `REM-P0.2.B-03` remains registered, `Optional` (§18.20.J), and not a prerequisite. *(The v1.0.13 reading of `Execute EXE-P0.2.C` as authorizing only Sub-phase C is superseded for `EXE-P0.2.C` onward; it remains the accurate historical reading of every unit executed before this correction.)*

*(The conclusion above is the v1.1.0 conclusion, retained as history; the v1.1.1 conclusion below supersedes it.)*

**Current readiness conclusion (v1.1.1; further structural governance correction implementing HD-CE-17/18/19 — not a new decision round, not implementation, decides no new fact about repository state).** *(All items of the v1.1.0 conclusion remain unchanged in substance; this correction fixes the model's own internal consistency, not repository state.)*

- **N/A continuation (HD-CE-17, §18.20.R):** a path-internal `NOT APPLICABLE` sub-phase now correctly emits the distinct `### AI VERIFIED — CONTINUING (NOT APPLICABLE)` footer — never ordinary `CONTINUING`, which the v1.1.0 text incorrectly showed at §18.18's Capability 2 Sub-phase F step (now corrected). No commit, no individual `Approve`, never `HUMAN APPROVED`, never an expansion of authorization — identical in every governance respect to ordinary `CONTINUING` except that it correctly never claims a commit exists.
- **Completion-only guards (HD-CE-18/HD-CE-19, §18.20.S):** §18.15's "Capability `<n>`.A has been approved" check — a completion-only guard, not a human-authorization guard — is now explicitly satisfied, within the same authorized path, by `HUMAN APPROVED`, `AI VERIFIED — CONTINUING`, or `AI VERIFIED — CONTINUING (NOT APPLICABLE)`. The other check in the same guard, "Capability 1 Gate has been approved," is unaffected — a Gate-level check is never a completion-only guard and is never satisfied by anything but a genuine `Approve CAPABILITY-GATE P0.<n>`. §18.15's existing generalization to future capabilities carries this correction forward automatically; no other structurally equivalent guard was found in the document.
- **Three drafting omissions fixed:** §18.11.8, §18.12.5, and §18.12.6 each now carry the same v1.1.0-style qualifying cross-reference that every other command-protocol section already received — these were omitted in the v1.1.0 pass itself, not a new design change.
- **Repository state:** unchanged. `EXE-P0.2.C` remains, as a fact, `NOT EXECUTED, NOT AUTHORIZED`. `SOURCE-GAP-EVAL-01`/`SOURCE-GAP-IRG-02`: OPEN (unchanged).
- **Operative status:** unchanged from v1.1.0 — `.claude/agents/eaioc-p0-execution-orchestrator.md`, `.claude/skills/eaioc-execution-governance/SKILL.md`, `docs/execution-plan-p0-steps.md`, and root `CLAUDE.md` remain unmodified by this correction; continuous execution, including its now-corrected N/A and completion-guard semantics, is still not yet operative in practice (§18.20.Q).

> **PLAN READY: YES.**
>
> **MODE B READY for `EXE-P0.2.B`: DONE (unchanged).**
>
> **MODE B READY for `EXE-P0.2.C`: NOT YET EXECUTED — not executed, not authorized by this correction.** Once issued, `Execute EXE-P0.2.C` opens the §18.20 authorized path for Capability 2's remaining sub-phases through Capability Gate P0.2, now with internally consistent N/A (Sub-phase F) and completion-guard semantics (§18.18's v1.1.1-corrected sequence block, §18.20.R–S).

The next executable unit is unchanged: `EXE-P0.2.C` (`Execute EXE-P0.2.C`), subject to its own pre-flight (§18.19.3) and routing. `REM-P0.2.B-03` remains registered, `Optional` (§18.20.J), and not a prerequisite.

*The per-version surgical-correction self-checks below are historical records of each pass. Each one's own closing sentence (e.g. "ready to freeze") describes that pass only. The current readiness conclusion is the one above, restated in the v1.0.5 self-check.*

**v1.0.1 surgical-correction self-check**, per `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.1.md`: (1) the Sub-phase A/B shared-commit contradiction in Capability 1 (§18.4) is removed — every sub-phase across all six capabilities now has its own standalone commit and approval checkpoint, restated explicitly at §18.2. (2) All "parallel-capable"/"any order" wording (§18.3, §48, §H) is corrected to distinguish dependency independence from execution scheduling — Mode B remains strictly sequential everywhere, with no exception. (3) The Capability Gate → next-capability boundary now states its own separate commit/approval requirement explicitly (§18.10), so a closed gate is never itself sufficient authorization to proceed. (4) The six-capability P0 scope, the validated technology baseline and its classification, all three `SOURCE-GAP-EXECPLAN-01/02/03` entries, the proposed (not source-defined) package placements for Context Policy/Prompt Assembler/Output Controls, and Capability 2's two-distinct-items/one-foundation identity are all unchanged. (5) All six ADRs remain `PROPOSED`; no ADR was accepted or closed. (6) File-scope discipline was maintained — `git status --short`/`git diff --stat`/`git diff -- docs/execution-plan.md` confirm only this file changed. (7) No source code, and no file under `control_plane/`, was created or modified — this correction is planning-document-only, exactly as required.

**v1.0.2 surgical-correction self-check**, per `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.2.md`: (1) the Capability Gate is now described exclusively as a post-lifecycle review/promotion checkpoint — never a ninth implementation sub-phase, never assigned an `EXE-P0.<n>.<letter>` ID, and never given its own implementation commit (§18.2, §18.10). (2) Every count reference is corrected: 8 lifecycle sub-phases (A–H) per capability × 6 capabilities = 48 atomic `EXE-P0.<n>.<letter>` execution units, plus 6 Capability Gates, equal 54 total gated checkpoints — never described as "54 atomic execution units" anywhere in this document (§18.2, §H, header). (3) Sub-phases A–H retain their own individual commit and approval boundary, unchanged from the v1.0.1 correction; the Gate retains its own explicit human-approval requirement, but never a commit (§18.10, §37). (4) Execution remains strictly sequential, per the v1.0.1 correction, unchanged. (5) Observability remains excluded from this execution slice (§3, §38, §41), unchanged. (6) P1–P5 remain non-executable/non-atomized in this document (§19–23), unchanged. (7) All six ADRs remain `PROPOSED` (§39), unchanged. (8) All three `SOURCE-GAP-EXECPLAN-01/02/03` entries are preserved unchanged (§38) — none resolved, none duplicated. (9) File-scope discipline was maintained — `git status --short`/`git diff --stat`/`git diff -- docs/execution-plan.md` confirm only this file changed during this correction; `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.1.md` and `...v1.0.2.md` were read but not modified. (10) No blocking issue remains; this plan is ready to freeze.

**v1.0.3 surgical-correction self-check**, per `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.3.md`: (1) a new §18.11 "Claude Code Mode B Execution Command Protocol" was added immediately after §18.10 and before §19, with no renumbering needed elsewhere since it is a subsection insertion, not a new top-level section. (2) It establishes: user-command-only authorization (§18.11.1); the canonical `Execute EXE-P0.<n>.<letter>` syntax with the preserved `Execute P0.1` shorthand rule (§18.11.2); the exact per-unit execution lifecycle diagram (§18.11.3); the canonical `Approve EXE-P0.<n>.<letter>` / `Approve CAPABILITY-GATE P0.<n>` approval syntax (§18.11.4); the required per-unit response format, including the H-specific approval-checkpoint variant (§18.11.5); the Capability Gate's own command protocol, including its `PASS`/`BLOCKED` review report (§18.11.6); the complete six-capability copy/paste operator reference sequence, explicitly labeled a reference not a batch script (§18.11.7); a command-state table (§18.11.8); a prohibited-ambiguous-command list (§18.11.9); the restated 48+6=54 count (§18.11.10); the restated one-sub-phase-one-commit/no-gate-commit rule (§18.11.11); and a strong no-automatic-chaining rule (§18.11.12). (3) `Execute EXE-P0.1.A`, `Approve EXE-P0.1.A`, and `Approve CAPABILITY-GATE P0.1` all appear explicitly, as required. (4) The full operator reference sequence runs through `Approve CAPABILITY-GATE P0.6`. (5) The document still states 48 atomic execution units + 6 Capability Gates = 54 total gated checkpoints everywhere the count appears (§18.2, §18.11.10, §H) — no regression to "54 atomic units." (6) The Capability Gate still receives no implementation commit anywhere in the new section (§18.11.6, §18.11.11), consistent with the v1.0.2 correction. (7) No source code, and no file under `control_plane/`, was created or changed. (8) No upstream document was changed — `git status --short`/`git diff --stat`/`git diff -- docs/execution-plan.md` confirm only this file changed; all three correction-prompt files (v1.0.1–v1.0.3, all pre-existing and untracked) were read but not modified. (9) The six-capability P0 scope, the validated technology baseline, all three `SOURCE-GAP-EXECPLAN-01/02/03` entries, all six ADRs' `PROPOSED` status, Observability's exclusion, and P1–P5's non-atomized status are all unchanged from the v1.0.2 baseline. No blocking issue remains; this plan is ready to freeze.

**v1.0.4 surgical-correction self-check**, per `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.4.md` (its §27 completion list, in order): (1) **Correct shared-core ownership** — `core/interfaces`, `core/schemas`, `core/errors` explicitly owned by Capability 1 / `EXE-P0.1.A` (§18.4 Preconditions and A row, §18.13), with `ControlPlaneRequest`/`OptimizationPlan` (and `ControlPlaneError`) explicitly handled and their schemas deliberately *not* reproduced here — Claude Code must verify fields against the live `interfaces.md` (§18.13); consumers consume, never create (§18.5 B row, §18.6). (2) **Historical P0.1 reconciliation mechanism** — POST-HOC EXECUTION RECONCILIATION defined (§18.14), `EXE-P0.1.A` reconciled from repository evidence as `RECONCILED: GAP FOUND` without rewriting commit `a345742` (§18.14.1), closed by remediation units `REM-P0.1.A-01`/`-02` outside the 48/54 counts (§18.14.2); `EXE-P0.2.A` reconciled `PASS` with its sequencing irregularity recorded (§18.14.3); the prompt's own state summary omitted Capability 1's G commit (`95a1267`) — recorded accurately here rather than copied. (3) **P0.2.B prerequisite guard** — §18.15, the prompt's exact eight checks plus their concrete meaning, extended to `EXE-P0.3.B`. (4) **AI verification at every atomic step** — §18.12 (checklist A–J, the four verdicts, the mandatory report template), §18.11.3 lifecycle replaced, §18.11.5 response format extended. (5) **Human approval after AI verification** — `### AI VERIFIED — AWAITING HUMAN APPROVAL` checkpoints (§18.12.4), no approval request after a blocked verdict, AI verification never treated as approval (§18.12.2, §18.12.5 exact safety rules). (6) **AI Capability Gate verification** and (7) **Human Capability Gate approval** — three-stage Gate model (§18.10, §18.11.6), `AI CAPABILITY GATE VERIFICATION: PASS / BLOCKED`, `Approve CAPABILITY-GATE P0.n` valid only after an AI PASS; one small operational addition, `Review CAPABILITY-GATE P0.n`, a review-only resume trigger for a Gate review in a later session (no approval, no implementation authority). (8) **N/A handling** — §18.16, §18.2, §18.11.11: `AI VERIFICATION: NOT APPLICABLE`, human acknowledgment still required, no fake commit, 48 count unchanged. (9) **Out-of-band commit handling** — `c3d6ecf` and `.vscode/settings.json` recorded as non-implementation history, not counted, not evidence, not altered (§18.16). (10) **No-automatic-chaining rule** — §18.11.12 extended to AI PASS verdicts, §18.17 state model (no state bypasses AI verification), §18.12.6 command table (approval and next execution never combined). Also: execution state model (§18.17), revised examples including the blocked one (§18.12.7), updated sequence semantics and current position (§18.18), `PLAN READY` vs. `MODE B READY` (§52), `SOURCE-GAP-EXECPLAN-04` (§38), rollback (§37). Counts re-verified: **48** atomic `EXE-P0.<n>.<letter>` units + **6** Capability Gates = **54** total gated checkpoints, unchanged; §18.11 command protocol retained. No upstream architecture, requirements, ADR (all six remain `PROPOSED`), technology-baseline, P0 capability scope, or P1–P5 content changed; `SOURCE-GAP-EXECPLAN-01`–`03` unchanged. No source code, test, or `control_plane/` file was created or modified; `EXE-P0.2.B` was not executed. **File scope:** the correction prompt restricts changes to this file alone; the operator's own instruction for this pass explicitly additionally requested that the companion operator runbook `docs/execution-plan-p0-steps.md` be brought in line with v1.0.4, so exactly those two files changed — verified via `git status --short`/`git diff --stat`. No commit was created by this correction.

**v1.0.5 surgical-correction self-check**, per `docs/prompts/Execution_Plan_Surgical_Correction_Prompt_v1.0.5.md` (its §10 list, in order):
1. The version is `1.0.5` in the header and the §1 Version row, and the correction history has a v1.0.4 → v1.0.5 entry. The v1.0.1–v1.0.4 history is preserved.
2. §7 now states that every `EXE-P0.<n>.<letter>` unit maps 1:1 to one lifecycle sub-phase A–H. The former "A–H + Gate" wording is gone.
3. The Capability Gate is explicitly separate: a post-lifecycle review/promotion checkpoint, not an `EXE-P0` unit and not a ninth sub-phase (§7, §18.2, §18.10).
4. §18.2 now reads "eight lifecycle sub-phase entries A–H" plus a separate Gate. The former "nine" wording is gone.
5. No active command rule uses history-dependent shorthand resolution. The former §18.11.2 shorthand rule and the §18.2 `Execute P0.1` sentence are removed. The shorthand survives only in a paragraph explicitly labeled historical and superseded, and `Execute P0.1` is now listed in §18.11.9's prohibited commands.
6. The canonical syntax is `Execute EXE-P0.<n>.<letter>`, stated as mandatory, together with `Approve EXE-P0.<n>.<letter>`, `Review CAPABILITY-GATE P0.<n>` and `Approve CAPABILITY-GATE P0.<n>` (§18.11.2).
7. The no-automatic-chaining rule (§18.11.12, §18.12.5, §18.17) was not weakened or removed.
8. The count is unchanged: 48 atomic `EXE-P0` units + 6 Capability Gates = 54 gated checkpoints.
9. AI Verification before human approval is unchanged (§18.12).
10. Gate AI Verification before human Gate approval is unchanged (§18.10, §18.11.6).
11. N/A handling is unchanged (§18.16).
12. `REM-P0.1.A-01` and `REM-P0.1.A-02` remain outside the 48/54 counts (§18.14).
13. `SOURCE-GAP-EXECPLAN-04` remains a blocking implementation-side gap until remediation closes it. Its §38 description and disposition are unchanged, and Summary K and U now distinguish it from the non-blocking 01–03.
14. The final readiness conclusion no longer claims no blocking issue exists. The original v1.0.0 conclusion is labeled historical and superseded.
15. The final readiness conclusion now separates `PLAN READY: YES` from `MODE B READY for EXE-P0.2.B: NO`, both there and in §52.
16. No implementation code or unrelated file was modified: no source, test, `control_plane/`, `CLAUDE.md`, `.vscode/`, ADR, or upstream document. This was verified via `git status --short` and `git diff --stat`. Note that `CLAUDE.md` already carried uncommitted edits from an earlier, separate task in this session, which this pass did not touch.
17. The companion runbook `docs/execution-plan-p0-steps.md` was aligned to v1.0.5. Its header, change summary, canonical commands, the full-ID instruction, Rule 7, current position, and next required command (`Execute REM-P0.1.A-01`) were updated. The two-stage checkpoint and separate-Gate models were preserved.

No commit was created by this correction.

**v1.0.6 surgical-correction self-check**, per `docs/prompts/AC005-Contract-Correction-Prompt.md`:
1. The version is `1.0.6`, and a v1.0.5 → v1.0.6 history entry was added; the v1.0.1–v1.0.5 history is preserved.
2. The source-contract resolution is recorded verbatim (§18.14.4).
3. `CONTRA-EXECPLAN-01` is recorded as a frozen-source contract inconsistency resolved by the documented precedence, not as an interface bug (§18.14.4, §38).
4. Capability 1's B Definition of Done now requires items (1)–(7) (§18.4). It explicitly does not claim `e353dcf` satisfied them, and the historical execution is unchanged.
5. The B remediation sequence is formally assigned, with unique IDs `REM-P0.1.B-01` (Contract / Design Reconciliation) and `REM-P0.1.B-02` (Minimal Implementation Remediation). Each is `Execute` → AI Verification → Human Approval, with no automatic chaining. Both sit outside the 48/54 counts, which are unchanged.
6. The gate prerequisites are preserved: Capability 1 Gate = BLOCKED and `EXE-P0.2.B` = BLOCKED until the B remediation completes and the Gate passes.
7. The G/H approval evidence is recorded as not established and not assumed (§18.14.3).
8. The stale current position is corrected (§18.18, §52, Readiness), with the v1.0.4/v1.0.5 positions labeled historical.
9. `SOURCE-GAP-EXECPLAN-05`/`-06` are added for the D-4/D-3 open items.
10. `a764689` remains non-implementation history (§18.16), unchanged.
11. No source code, test, or `control_plane/` file changed. The files changed in this pass are exactly the ones the correction prompt authorizes: `docs/interfaces.md`, `docs/conventions.md`, `docs/execution-plan.md`, `docs/execution-plan-p0-steps.md`, `CLAUDE.md`. This was verified via `git status --short`.

**v1.0.7 surgical-correction self-check** (DB-2 — HD-1 to HD-4 decision promotion):
1. The version is `1.0.7`, with a v1.0.6 → v1.0.7 history entry; v1.0.1–v1.0.6 history is preserved.
2. `REM-P0.1.B-01` is recorded as completed, AI-verified and human-approved (`34dd1f1`).
3. HD-1 to HD-4 are recorded as explicit human-operator decisions (§18.14.5), frozen as stated.
4. `SOURCE-GAP-EXECPLAN-05` and `-06` are marked resolved, with their history kept (§38).
5. DB-1 (the approved placeholder rule) is unchanged, and DB-2 records the promotion. A distinct ID was used because DB-1 already existed (operator decision).
6. `REM-P0.1.B-02` remains NOT EXECUTED, and is eligible only after this correction is committed.
7. Capability 1 Gate remains BLOCKED, and `EXE-P0.2.B` remains BLOCKED.
8. `CONTRA-EXECPLAN-01`, the `e353dcf` reconciliation (`GAP FOUND`), and all historical records are preserved. No claim is made that the historical implementation satisfied the corrected contract.
9. Documentation only: exactly `docs/interfaces.md`, `docs/conventions.md`, `docs/execution-plan.md`, `docs/execution-plan-p0-steps.md` and `CLAUDE.md` changed. No source code or test changed.

**v1.0.8 surgical-correction self-check** (HQ-1 to HQ-6 and GS-1 — `EXE-P0.2.B` design-gap decisions and gate-state synchronization):
1. The version is `1.0.8`, with a v1.0.7 → v1.0.8 history entry; the v1.0.1–v1.0.7 history is preserved.
2. HQ-1 to HQ-6 and GS-1 are recorded as explicit human-operator decisions (§18.14.6) and, where they touch the contract, as `interfaces.md` §18 P0 realization notes and a §26.1 row. INTF-030's schema and signature text is unchanged.
3. No field mapping, retrieval contract, `verified` field, nullable change, zero/placeholder value, sentinel, runtime stub, or `RegressionReport` was introduced.
4. `SOURCE-GAP-EXECPLAN-07`/`-08` (blocking) and `-09` (non-blocking) are registered (§38). `SOURCE-GAP-EVAL-01` remains open.
5. The §18.5 B row distinguishes the baseline operational path from the deferred `run_optimized()`/`compare()`/`report_regression()` forward contract.
6. GS-1: the 2026-09-23 approvals (`REM-P0.1.B-02`, `EXE-P0.1.G`/`H`, Capability Gate P0.1) are recorded in §1, §18.14.3, §18.14.5, §18.14.6, §18.18, §52 and the readiness conclusion. The v1.0.4–v1.0.7 self-checks and historical positions are retained as history.
7. `EXE-P0.2.B` remains BLOCKED and NOT EXECUTED. No approval is requested.
8. Documentation only: `docs/interfaces.md`, `docs/execution-plan.md`, `docs/execution-plan-p0-steps.md` and root `CLAUDE.md` changed. `control_plane/evaluation/EvaluationFramework.md`, `conventions.md`, `eval.md`, all source code and all tests are unchanged.

**v1.0.9 surgical-correction self-check** (Execution Assistance Layer — skills/agents integration):
1. The version is `1.0.9`, with a v1.0.8 → v1.0.9 history entry; the v1.0.1–v1.0.8 history is preserved.
2. §18.19 adds the Execution Assistance Layer, defined as development-workflow assistance only — not an EAIOC runtime or architecture layer and not an authorization mechanism.
3. The skill/agent inventory in §18.19.2 is taken from the live `.claude/` tree: 4 agents (`eaioc-code-architect`, `eaioc-code-reviewer`, `eaioc-code-verifier`, `eaioc-guide`) and 3 skills (`eaioc-governance`, `eaioc-agent-orchestration`, `eaioc-dashboard`); no name is invented, and no agent or skill definition was changed.
4. Routing (§18.19.4–§18.19.5), pre-flight and fast-fail (§18.19.3), minimum sufficient assistance, and specialist-evidence-versus-verdict (§18.19.6) are added; dimension K (§18.12.1) and report line 10 (§18.12.3) are added without renumbering.
5. Counts, unit IDs, sequencing, Capability Gate semantics, AI Verification and human approval requirements are unchanged: 48 units + 6 Gates = 54 checkpoints; no skill/agent unit, ID, or gate is created.
6. `EXE-P0.2.B` remains BLOCKED on `SOURCE-GAP-EXECPLAN-07`/`-08`; `SOURCE-GAP-EVAL-01` remains open (§18.19.7).
7. Stale current-state statements corrected: version lines, readiness/§52 plan-ready lines, Summary K's gap list, and the §18.5 B row no-op wording (annotated per HQ-3). Historical statements are unchanged.
8. Documentation only: exactly `docs/execution-plan.md` and `docs/execution-plan-p0-steps.md` changed. No source code, test, authoritative document, agent, skill, or `CLAUDE.md` changed.

**v1.0.10 surgical-correction self-check** (registration of the `EXE-P0.2.B` blocker remediation):
1. **Version and history.** The version is `1.0.10`, with a v1.0.9 → v1.0.10 history entry; the v1.0.1–v1.0.9 history is preserved.
2. **Canonical ID.** `REM-P0.2.B-01` follows the existing `REM-P0.<n>.<letter>-<NN>` convention (§18.14.2, §18.14.4). No `REM-P0.2.*` ID was reserved anywhere before. The non-canonical session name `REM-P0.2.B-DESIGN-01` is recorded as having no status.
3. **Registration content.** §18.14.7 defines the unit's scope, prerequisites, Definition of Done (14 items), review and verification routing (Architect + Verifier, AUTHORITATIVE-DOC), approval, blocking rule, commit title, and dependency sequence. The extension of §18.14's remediation mechanism to this pre-execution case is stated explicitly and limited to it.
4. **No decision invented.** D-A to D-D are registered as open decision areas. No field mapping, nullability, `verified` semantics, placeholder value, `run_optimized()` behavior, `RegressionReport` schema, or Observability dependency was introduced. The seed record `EvaluationMeasurementReconciliation.md` is referenced as pre-decision material only.
5. **Resulting state.** `SOURCE-GAP-EXECPLAN-07`/`-08` remain OPEN (§38); `SOURCE-GAP-EVAL-01` remains open. `REM-P0.2.B-01` is REGISTERED / NOT STARTED; `EXE-P0.2.B` remains BLOCKED. Remediation approval is stated not to authorize `EXE-P0.2.B`.
6. **Counts and structure.** Counts are unchanged: 48 units + 6 Gates = 54 checkpoints. The remediation is outside them — not a sub-phase, capability, Gate, or implementation phase.
7. **Historical records.** The v1.0.8 position and sequence in §18.18, and the "No unit is assigned" sentence in §18.14.6, are relabeled historical, not rewritten.
8. **Scope of the change.** Documentation only: exactly `docs/execution-plan.md` and `docs/execution-plan-p0-steps.md` changed. The runbook was synchronized per the operator's standing instruction. No source code, test, contract document, agent, skill, `control_plane/` file, or `CLAUDE.md` changed.
9. **Recorded, not changed.** The §18.19.2 inventory predates the repository's `eaioc-p0-execution-orchestrator` agent (`15614bd`). That is outside this correction's scope, is non-blocking, and is left for a future correction.

**v1.0.11 surgical-correction self-check** (DB-3 — promotion of the `REM-P0.2.B-01` decisions; registration of `REM-P0.2.B-02`/`-03`):
1. **Version and history.** The version is `1.0.11`, with a v1.0.10 → v1.0.11 history entry; the v1.0.1–v1.0.10 history and self-checks are preserved.
2. **Identifiers.** DB-3 is the next free label (DB-1, DB-2 used). `REM-P0.2.B-02`/`-03` follow the `REM-P0.<n>.<letter>-<NN>` convention (§18.14.7); no such IDs existed before. `SOURCE-GAP-EXECPLAN-10` to `-15` are the next free gap IDs.
3. **Promotion.** The operator's decisions D-A to D-D and the operator's decisions of 2026-09-25 are recorded (§18.14.8) and promoted into `interfaces.md` §18 (schema and notes), §24, §28.1, §28.2, §36 and §40.2; `eval.md` (§5, §7, §16, §24, §37, §38) and `implementation-plan.md` (§8.2) carry additive correction notes only. `conventions.md` is unchanged.
4. **Mechanism.** §18.14's pre-execution remediation mechanism is extended, explicitly and only, to `REM-P0.2.B-02` and `-03`; Gate P0.1's approval is not reopened. Registration is authorized; execution is not.
5. **Gaps.** `SOURCE-GAP-EXECPLAN-07`/`-08` are resolved at source and `-09` is narrowed, recorded additively in the §38 status updates (the register rows are unmodified); `-10` to `-15` are registered with sources and explicit dispositions (`-10`, `-11`, `-12`, `-14`, `-15` open source-supported gaps; `-13` a deferred decision, since narrowed by the human contract decision of 2026-09-25: the outcome is an operation failure; only the error mechanism, code and class stay open). `SOURCE-GAP-EVAL-01`: OPEN (unchanged). `SOURCE-GAP-IRG-02`: OPEN (unchanged).
6. **`EXE-P0.2.B`.** Its Deliverable, Tests, Failure-Path Test and DoD are contract-level and fixture-based; no live Path A measurement is claimed; the Gate P0.2 deferral is stated (§18.10, §52). `REM-P0.2.B-02` is a hard prerequisite (§18.15, derived consequence of D-A and HQ-1); `REM-P0.2.B-03` is not.
7. **Human contract decisions of 2026-09-25 (not source-derived; recorded as such):** `BaselineEvaluationRecord.schema_version` is `1.0.0` (the sources establish only the versioning rules and the `1.0.x` compatibility floor); the source-entry field is `source_entry_id: string`; the record carries `run_type` (existing `EvaluationRun` semantic; `BASELINE` for every record produced by `EXE-P0.2.B`); the retrieval count basis is count-all-then-verify; a failed baseline measurement makes `run_baseline()` fail without returning a result (no nullable record, wrapper, new `ControlPlaneError` code or `error_class` value, or `PartialSuccess`); and the `interfaces.md` document Version stays 1.2.0 with an explicit amendment-history entry (no 1.3.0/2.0.0, no invented INTF-030 version; the breaking classification and the "no existing consumer" reasoning are inference, not authority). No placeholder remains. Deferred: `-10` to `-15`.
8. **Counts and structure.** Counts are unchanged: 48 units + 6 Gates = 54 checkpoints; the remediation units are outside them.
9. **Historical records.** The design note, commit facts, the v1.0.x history entries, the GS-1 and HQ rows, the earlier self-checks and the §38 rows for `-05` to `-09` are unchanged. The v1.0.10 status, position and readiness blocks are retained and marked superseded by new v1.0.11 blocks; requirement wording in the B row is superseded with the earlier wording quoted.
10. **Scope of the change.** Documentation and source-contract text only: `docs/interfaces.md`, `docs/eval.md`, `docs/implementation-plan.md`, `docs/execution-plan.md`, `docs/execution-plan-p0-steps.md` (operator runbook; the plan wins on any conflict), root `CLAUDE.md` (governance state only). No source code, test or `control_plane/` file changed.
11. **Field provenance.** Each `BaselineEvaluationRecord` field is classified as authoritative source, operator decision, human contract decision or derived consequence (§18.14.8); no human decision is described as source-established.
12. **Recorded, not changed.** The §18.19.2 inventory still predates the `eaioc-p0-execution-orchestrator` agent (`15614bd`); the design note's §10.6/§11 wording about ordering and its reading of `interfaces.md` §40.2 are corrected in this plan and in `interfaces.md` §40.2, not in the note.

**v1.0.12 surgical-correction self-check** (documentation-only state refresh: records `REM-P0.2.B-02`'s completed execution, AI verification and human approval; decides nothing new):
1. **Version and history.** The version is `1.0.12`, with a v1.0.11 → v1.0.12 history entry (document header); the v1.0.1–v1.0.11 history and self-checks are preserved unchanged.
2. **Scope.** Documentation-only. This correction records already-completed execution/verification/approval evidence (`bf74ac3`; `AI VERIFICATION: PASS`; human-approved 2026-09-27); it promotes no new human decision, decides no open question, and assigns no new ID (no new `DB-N`, `HD-n`, `HQ-n`, `SOURCE-GAP-*`, or `REM-*`/`EXE-*` ID).
3. **Sections touched.** §18.14.8 (new "Status after v1.0.12" bullets, superseding "Status after v1.0.11" as history); §18.18 (new "Current position... updated at v1.0.12 correction time" block, superseding the v1.0.11 block as history); the readiness/status block after §18.18 (new v1.0.12 status block, superseding the v1.0.11 block as history); the "Current readiness conclusion" (new v1.0.12 conclusion — Items 8–9 restated, `MODE B READY` lines restated, next-executable-unit line updated from `REM-P0.2.B-02` to `EXE-P0.2.B`); Summary K; this self-check; and the terminal verdict line.
4. **Historical preservation.** No v1.0.11 (or earlier) text was edited or deleted. Every updated block is a new, explicitly-labeled block placed after its predecessor with a superseding pointer sentence, exactly as every prior correction pass (v1.0.5 onward) has done — the sole documented exception is this document's own terminal verdict line, which by established practice (restated in root `CLAUDE.md`) supersedes rather than duplicates the prior version's line as the document's one true final line.
5. **No contract/code/governance change.** `interfaces.md`, `conventions.md`, `architecture.md`, `eval.md`, `implementation-plan.md`, any INTF/schema, any unit Deliverable/Tests/DoD, any count (48 units + 6 Gates = 54 checkpoints, unchanged), any `SOURCE-GAP-EXECPLAN-*`/`SOURCE-GAP-EVAL-01`/`SOURCE-GAP-IRG-02` definition, and the orchestrator agent, `eaioc-execution-governance` skill, and `eaioc-agent-orchestration` skill are all unchanged by this correction.
6. **`REM-P0.2.B-02`.** Recorded as executed (`bf74ac3`), AI-verified (`PASS`), human-approved (2026-09-27). This clears the named prerequisite of `EXE-P0.2.B` (§18.14.8, §18.15); it does not itself authorize, approve, or execute `EXE-P0.2.B`.
7. **`EXE-P0.2.B`.** Still `NOT YET EXECUTABLE`: its own pre-flight (§18.19.3), a fresh §18.15 guard check against the now-existing retrieval realization, and a Code Architect re-assessment have not run. No automatic chaining applies (§18.11.12) — this correction does not execute, AI-verify, or approve `EXE-P0.2.B`, and does not alter its Deliverable, Tests, Failure-Path Test, or Definition of Done.
8. **`REM-P0.2.B-03`.** Unchanged: registered, not executed, not a prerequisite, not scheduled by this plan.
9. **File scope.** `git status --short`/`git diff --stat`/`git diff -- docs/execution-plan.md` confirm only this file changed during this correction.
10. No blocking issue remains for this correction itself; the document now reflects `REM-P0.2.B-02`'s completed state.

**v1.0.13 surgical-correction self-check** (documentation-only state refresh: records `EXE-P0.2.B`'s completed execution, AI verification and human approval; decides nothing new):
1. **Version and history.** The version is `1.0.13`, with a v1.0.12 → v1.0.13 history entry (document header); the v1.0.1–v1.0.12 history and self-checks are preserved unchanged.
2. **Scope.** Documentation-only. This correction records already-completed execution/verification/approval evidence (`a2d022f`; `AI VERIFICATION: PASS`; human-approved 2026-09-27; 92/92 tests); it promotes no new human decision, decides no open question, and assigns no new ID.
3. **Sections touched.** §18.14.8 (new "Status after v1.0.13" bullets, superseding "Status after v1.0.12" as history); §18.18 (new "Current position... updated at v1.0.13 correction time" block, superseding the v1.0.12 block as history); the readiness/status block (new v1.0.13 status block, superseding the v1.0.12 block as history); the "Current readiness conclusion" (new v1.0.13 conclusion — Items 8–9 restated, `MODE B READY` lines restated, next-executable-unit line updated from `EXE-P0.2.B` to `EXE-P0.2.C`); Summary K; this self-check; and the terminal verdict line.
4. **Historical preservation.** No v1.0.12 (or earlier) text was edited or deleted. Every updated block is a new, explicitly-labeled block placed after its predecessor with a superseding pointer sentence. The documented Architect/Reviewer/Verifier ordering deviation for `EXE-P0.2.B` (routing ran after implementation rather than before) is recorded in the new "Status after v1.0.13" bullets and in `EXE-P0.2.B`'s own commit message (`a2d022f`) as historical evidence — preserved, not rewritten or concealed.
5. **No contract/code/governance change.** `interfaces.md`, `conventions.md`, `architecture.md`, `eval.md`, `implementation-plan.md`, any INTF/schema, any unit Deliverable/Tests/DoD, any count (48 units + 6 Gates = 54 checkpoints, unchanged), any `SOURCE-GAP-EXECPLAN-*`/`SOURCE-GAP-EVAL-01`/`SOURCE-GAP-IRG-02` definition, and the orchestrator agent, `eaioc-execution-governance` skill, and `eaioc-agent-orchestration` skill are all unchanged by this correction.
6. **`EXE-P0.2.B`.** Recorded as executed (`a2d022f`), AI-verified (`PASS`), human-approved (2026-09-27), 92/92 tests passing. This approval covers only Sub-phase B.
7. **`EXE-P0.2.C`.** `NOT EXECUTED, NOT AUTHORIZED`. No automatic chaining applies (§18.11.12) — this correction does not execute, AI-verify, or approve `EXE-P0.2.C`, and does not alter its Deliverable, Tests, Failure-Path Test, or Definition of Done. Capability Gate P0.2 likewise `NOT AUTHORIZED`.
8. **`REM-P0.2.B-03`.** Unchanged: registered, not executed, not a prerequisite, not scheduled by this plan.
9. **File scope.** `git status --short`/`git diff --stat`/`git diff -- docs/execution-plan.md` confirm only this file changed during this correction.
10. No blocking issue remains for this correction itself; the document now reflects `EXE-P0.2.B`'s completed state.

**v1.1.0 surgical-correction self-check** (structural execution-authorization-model amendment — adopts §18.20 per the frozen HD-CE-01–HD-CE-16 human decision register; not a state refresh; decides no new fact about repository state):
1. **Version and history.** The version is `1.1.0`, with a v1.0.13 → v1.1.0 history entry (document header) explicitly stating this is a structural amendment, not a routine v1.0.x state refresh (HD-CE-04); the v1.0.1–v1.0.13 history and self-checks are preserved unchanged. The §1 Document Control table's `Version` row, found stale at `1.0.11` (an oversight predating this correction, not itself a frozen HD-CE decision), is corrected to `1.1.0` in the same edit, disclosed here rather than silently fixed.
2. **New §18.20.** Added immediately after §18.19.7, before `## 19. P1 Execution Plan`, with subsections A–Q covering: phase = Capability (A, HD-CE-01); path-scoped human authorization (B, HD-CE-02); the authorized execution path's start/contents/end (C); continuous continuation's five-step sequence (D); no-authorization-expansion (E); the `### AI VERIFIED — CONTINUING` footer/state definition (F, HD-CE-06); the human-approval boundary model, including the mandatory per-unit enumeration requirement (G, HD-CE-05); HDR auto-resume (H, HD-CE-07); the prerequisite automatic-execution rule (I, HD-CE-08); the four-way remediation classification with `REM-P0.2.B-03 = Optional` (J, HD-CE-09); the eleven blocking conditions (K, HD-CE-10); unchanged commit semantics (L, HD-CE-11); the absolute Capability boundary (M, HD-CE-14); cross-session reconstruction via evidence plus a deferred runbook §20 field (N, HD-CE-12); the historical/non-retroactive rule and the explicit migration point `EXE-P0.2.C` (O, HD-CE-03 Option B, HD-CE-15); "NO UNAUTHORIZED CHAINING" (P, HD-CE-16); and the preserved-invariants/deferred-artifacts closing statement (Q).
3. **Cross-references amended (qualifying, not rewriting, pre-existing text — the established superseding-block pattern, extended to inline pointers for this pass).** New paragraphs added, with no prior text edited or deleted, at: §18.2 (commit rule unchanged, approval-checkpoint rule qualified); §18.10 (Gate boundary clarified); §18.11.1 (authorization scope redefined); §18.11.2 (command-syntax authorization scope redefined); §18.11.3 (diagram pointer); §18.11.5 (approval-checkpoint block pointer); §18.11.6 (Gate-as-boundary-approval note); §18.11.7 (reference-sequence pointer); §18.11.12 (`SUPERSEDED BY §18.20` annotation, historical wording retained verbatim per HD-CE-16); §18.12.1.J (verdict-count clarification — no fifth verdict); §18.12.4 (the new `### AI VERIFIED — CONTINUING` block, full text); §18.17 (new `CONTINUING` state branch, explicitly never satisfying a `HUMAN APPROVED` condition); §18.18 (new v1.1.0 continuous-execution sequence for `EXE-P0.2.C` onward, old sequence retained as history); §18.19.1 (governance-diagram pointer); §18.19.7 (cross-reference note, no substantive change — `EXE-P0.2.B` is already historical).
4. **Historical preservation.** No v1.0.13 (or earlier) text was edited or deleted anywhere in this correction. §18.11.12's historical "no automatic chaining" wording is retained verbatim and annotated, never rewritten (HD-CE-16). Every updated status/readiness/position block is a new, explicitly-labeled block placed after its predecessor with a superseding pointer sentence, exactly as every prior correction pass has done — the sole documented exception remains this document's own terminal verdict line, which supersedes rather than duplicates the prior version's line.
5. **No requirement/architecture/interface/contract/code change.** `interfaces.md`, `conventions.md`, `architecture.md`, `eval.md`, `implementation-plan.md`, any INTF/schema, any unit Deliverable/Tests/DoD, any count (48 units + 6 Gates = 54 checkpoints, unchanged, §18.20.L note in Summary H), any `SOURCE-GAP-EXECPLAN-*`/`SOURCE-GAP-EVAL-01`/`SOURCE-GAP-IRG-02` definition, and every historical execution/approval record are all unchanged by this correction.
6. **Deferred artifacts, not modified.** `.claude/agents/eaioc-p0-execution-orchestrator.md`, `.claude/skills/eaioc-execution-governance/SKILL.md`, root `CLAUDE.md`, and `docs/execution-plan-p0-steps.md` are explicitly NOT modified by this correction (§18.20.Q). This document's continuous-execution model is authoritative as of this version but not yet operative in practice until those artifacts are separately, explicitly amended to match it.
7. **`EXE-P0.2.C`.** Remains `NOT EXECUTED`, `NOT AUTHORIZED` as a repository-state fact — this correction executes, verifies, or approves nothing. What changes is only what issuing `Execute EXE-P0.2.C` will mean going forward (§18.20.B–C, §18.18's new sequence block).
8. **`REM-P0.2.B-03`.** Formally classified `Optional` under §18.20.J's four-way taxonomy (HD-CE-09) — registered, not executed, not a prerequisite, not automatic under continuous execution, consistent with its existing informal description elsewhere in this document.
9. **Migration point.** Explicitly recorded at §18.20.O: `CONTINUOUS EXECUTION MODEL EFFECTIVE FROM: EXE-P0.2.C`. `EXE-P0.2.A` and `EXE-P0.2.B` are explicitly recorded as historical, under the prior per-unit model, never relabeled (HD-CE-15).
10. **File scope.** `git status --short`/`git diff --stat`/`git diff -- docs/execution-plan.md` confirm only this file changed during this correction; `docs/execution-plan-p0-steps.md`, `CLAUDE.md`, `.claude/agents/**`, and `.claude/skills/**` were not touched by this correction (they carry their own, separately-tracked uncommitted or unrelated state).
11. No blocking issue remains for this correction itself; the document now carries an authoritative, not-yet-operative continuous-execution model, pending the deferred orchestrator/skill/CLAUDE.md/runbook amendments named at §18.20.Q.

**v1.1.1 surgical-correction self-check** (further structural governance correction — implements HD-CE-17, HD-CE-18, HD-CE-19, frozen after v1.1.0; fixes three v1.1.0 drafting omissions; not a new decision round, not implementation, decides no new fact about repository state):
1. **Version and history.** The version is `1.1.1`, with a v1.0.13 → v1.1.0 → v1.1.1 correction-history chain (document header) explicitly stating this is a further structural governance correction, not a new decision round and not implementation; the v1.0.1–v1.1.0 history and self-checks are preserved unchanged. The §1 Document Control table's `Version` row is updated to `1.1.1`.
2. **New N/A footer/state.** `### AI VERIFIED — CONTINUING (NOT APPLICABLE)` is defined at §18.12.4, distinct from ordinary `### AI VERIFIED — CONTINUING` (PASS-class/committed-only, unchanged) — verdict `NOT APPLICABLE` (unchanged, no fifth verdict, §18.12.1.J note); no commit; no individual `Approve`; path-internal only; never `HUMAN APPROVED`; never an authorization expansion (HD-CE-17).
3. **§18.20 extended.** New subsections R (N/A continuation) and S (completion-only guards) added after Q, before the `---`/`## 19.` boundary; §18.20.G amended (boundary-approval enumeration must name N/A-continuation units, never read as their individual approval); §18.20.N amended (N/A-continuation is valid path-completion evidence for cross-session reconstruction).
4. **§18.15 completion-guard semantics corrected (the most substantive fix in this pass).** The guard's two checks are now explicitly distinguished: "Capability 1 Gate has been approved" (a Gate-level, human-authorization check, unaffected, never satisfied by `CONTINUING`/N/A) versus "Capability `<n>`.A has been approved" (a completion-only check, now explicitly satisfied within the same authorized path by `HUMAN APPROVED`, `AI VERIFIED — CONTINUING`, or `AI VERIFIED — CONTINUING (NOT APPLICABLE)` — HD-CE-18, HD-CE-19). §18.15's existing generalization to later capabilities (at minimum `EXE-P0.3.B`) carries this correction forward automatically; no other structurally equivalent guard exists in the document (Capabilities 4–6's own preconditions are Gate-level only, confirmed unaffected).
5. **Three drafting omissions fixed.** §18.11.8, §18.12.5, and §18.12.6 each now carry the same v1.1.0-style qualifying cross-reference every other command-protocol section already received in the v1.1.0 pass — these were omissions in that pass, identified by a dedicated follow-up audit, not new design changes.
6. **§18.2, §18.16, §18.17, §18.18 corrected for internal consistency.** §18.2/§18.16's N/A approval requirement is now explicitly scoped to outside-a-§18.20-path/pre-migration units only, with the path-internal exception stated. §18.17 gains a parallel N/A-continuation state branch and a corrected statement that a completion-only guard (not a human-approval guard) may accept either continuation state. §18.18's v1.1.0 worked sequence — whose Sub-phase F step incorrectly resolved to ordinary `CONTINUING` despite Capability 2's F row being `NOT APPLICABLE` (§18.5) — is corrected to `AI VERIFIED — CONTINUING (NOT APPLICABLE)`.
7. **Historical preservation.** No v1.1.0 (or earlier) text was edited or deleted anywhere in this correction; every fix is an additive paragraph, a new subsection, or (for §18.15's guard explanation, §18.2/§18.16/§18.17's qualifying sentences, and the worked-sequence line) an additive clarification placed immediately after the text it qualifies. The sole documented exception remains this document's own terminal verdict line.
8. **No requirement/architecture/interface/contract/code change.** `interfaces.md`, `conventions.md`, `architecture.md`, `eval.md`, `implementation-plan.md`, any INTF/schema, any unit Deliverable/Tests/DoD, any count (48 units + 6 Gates = 54 checkpoints, unchanged), any `SOURCE-GAP-*` definition, and every historical execution/approval record are unchanged by this correction. No new canonical AI Verification verdict is introduced (still exactly four).
9. **Deferred artifacts, not modified.** `.claude/agents/eaioc-p0-execution-orchestrator.md`, `.claude/skills/eaioc-execution-governance/SKILL.md`, root `CLAUDE.md`, and `docs/execution-plan-p0-steps.md` are explicitly NOT modified by this correction. Continuous execution — including its now-corrected N/A and completion-guard semantics — remains not yet operative in practice until those artifacts are separately amended (§18.20.Q, unchanged).
10. **`EXE-P0.2.C`.** Remains `NOT EXECUTED`, `NOT AUTHORIZED` as a repository-state fact — this correction executes, verifies, or approves nothing.
11. **File scope.** `git status --short`/`git diff --stat`/`git diff -- docs/execution-plan.md` confirm only this file changed during this correction; `docs/execution-plan-p0-steps.md`, `CLAUDE.md`, `.claude/agents/**`, and `.claude/skills/**` were not touched.
12. No blocking issue remains for this correction itself; the document's continuous-execution model is now internally consistent with respect to N/A sub-phases and completion-only guards, pending the same deferred orchestrator/skill/CLAUDE.md/runbook amendments as before.

FINAL EXECUTION PLAN v1.1.1 CORRECTION COMPLETE — HD-CE-17/18/19 IMPLEMENTED: NEW ### AI VERIFIED — CONTINUING (NOT APPLICABLE) FOOTER (§18.12.4, §18.20.R) FOR PATH-INTERNAL N/A SUB-PHASES; §18.15 COMPLETION-GUARD SEMANTICS CORRECTED TO ACCEPT HUMAN APPROVED / CONTINUING / CONTINUING (NOT APPLICABLE) WITHIN THE SAME PATH, NEVER SUBSTITUTING FOR GATE OR BOUNDARY APPROVAL (§18.20.S); THREE v1.1.0 DRAFTING OMISSIONS FIXED (§18.11.8, §18.12.5, §18.12.6); NO NEW VERDICT, NO NEW AUTHORIZATION MECHANISM; EXE-P0.2.C REMAINS NOT EXECUTED, NOT AUTHORIZED; ORCHESTRATOR/SKILL/CLAUDE.MD/RUNBOOK NOT YET AMENDED — MODEL STILL NOT YET OPERATIVE IN PRACTICE; COUNTS UNCHANGED (48+6=54) — NEXT: Execute EXE-P0.2.C (subject to its own pre-flight; now opens the §18.20 authorized path through Capability Gate P0.2 with internally consistent N/A and completion-guard semantics)
