# EAIOC — P0 Step-by-Step Claude Code Execution Runbook

**Based on:** `docs/execution-plan.md` v1.0.11 (aligned with the v1.0.11 source-contract correction (DB-3); first reworked for the v1.0.4 correction)  
**Purpose:** Human/operator runbook for executing EAIOC P0 implementation one atomic step at a time.  
**Scope:** P0 only — 6 capabilities, 48 atomic execution units, 6 Capability Gates (54 gated checkpoints), plus the remediation units `REM-P0.1.A-01`/`-02`, `REM-P0.1.B-01`/`-02`, `REM-P0.2.B-01`, `REM-P0.2.B-02` and `REM-P0.2.B-03` (outside those counts).  
**Execution model:** One command at a time. Every atomic unit is first **AI-verified by Claude Code** and only then presented for **explicit human approval** — both are required, neither substitutes for the other (`execution-plan.md` §18.12).  
**Important:** This file is an operator runbook. It does not replace or modify `docs/execution-plan.md`; where the two ever differ, `execution-plan.md` wins.

**What changed in v1.0.4 (summary):**
- Two-stage checkpoint: `Execute` → Claude Code's AI Verification report → `### AI VERIFIED — AWAITING HUMAN APPROVAL` → your `Approve`. A `### AI VERIFICATION BLOCKED` report never asks for approval.
- Capability Gate is now AI Gate Verification first, then your Gate approval.
- The shared `core/interfaces`, `core/schemas`, `core/errors` foundation is owned by `EXE-P0.1.A`. Because the already-executed `EXE-P0.1.A` did not create it, two remediation units (`REM-P0.1.A-01`/`-02`) must run before `EXE-P0.2.B` — see §7a and §20.
- N/A sub-phases (e.g. every F) get `AI VERIFICATION: NOT APPLICABLE`, still need your approval, and never get a fake commit.
- Commit `c3d6ecf` (CLAUDE.md + `.vscode/settings.json`) is not an implementation commit and never counts as evidence of a completed unit.

**What changed in v1.0.5 (summary):** consistency fixes only — no change to the process above.
- Always use the full unit ID (`Execute EXE-P0.1.B`, never `Execute P0.1`); the capability-level shorthand is retired.
- Only A–H are lifecycle sub-phases; the Capability Gate is a separate checkpoint (unchanged in practice, now worded consistently).
- The plan no longer claims "no blocking issue": `SOURCE-GAP-EXECPLAN-04` is recorded as the current blocker, so `EXE-P0.2.B` cannot run until the remediation units and the Capability 1 Gate are approved.
- *(Historical: at v1.0.5 the next command was `Execute REM-P0.1.A-01`; both A remediation units are now done and approved.)*

**What changed in v1.0.6 (summary):** AC-005 source-and-plan correction.
- **The ledger contract.** `interfaces.md` §28.1 (`CostLedgerEntry`) now carries all 58 `architecture.md` §27.1 fields as nine nested groups (`input` … `quality`). Its 29 existing fields are retained, and none is aliased to a §27.1 field. `conventions.md` §14.1 now lists all nine groups as the complete contract.
- **Gaps and contradictions.**
  - `CONTRA-EXECPLAN-01`: the old `INTF-047` didn't represent §27.1. It's resolved at source; the code still needs remediation.
  - `SOURCE-GAP-EXECPLAN-05`: 22 ledger field types aren't established by any source.
  - `SOURCE-GAP-EXECPLAN-06`: whether a missing "where available" value makes an entry `unverified` is undecided.
- **New remediation units.** `REM-P0.1.B-01` (Contract / Design Reconciliation) and `REM-P0.1.B-02` (Minimal Implementation Remediation) are formally authorized — see §7b and §20.
- **G/H approvals.** `Approve EXE-P0.1.G` / `Approve EXE-P0.1.H` are **not evidenced** in the repository; explicit confirmation is required before the Capability 1 Gate.
- **Status.** Capability 1 Gate: **BLOCKED**. `EXE-P0.2.B`: **BLOCKED**.
- *(Historical: at v1.0.6 the next command was `Execute REM-P0.1.B-01`; it is now done and approved — `34dd1f1`.)*

**What changed in v1.0.7 (summary):** DB-2 decision promotion, documentation only.
- **Decisions promoted into the contract.** Your `REM-P0.1.B-01` decisions HD-1 to HD-4 are now written into `interfaces.md` §28.1 and `conventions.md` §14.1:
  - 13 counters are `integer`;
  - the `model` strings, plus `escalation` as a boolean;
  - `ttft_ms` is `integer`, `semantic_preservation` a `float` score, `schema_compliance` and `safety_validation` booleans;
  - only `reasoning_tokens` and `cost.tool` are nullable, and a null there does not make an entry `unverified`;
  - a required field that can't be measured makes the entry `unverified`.
- **Gaps closed.** `SOURCE-GAP-EXECPLAN-05`/`-06` are resolved.
- **Placeholder rule unchanged.** DB-1 is as approved: placeholders only in `verified=false` entries, never counted as measured values.
- **Status.**
  - `REM-P0.1.B-02` is **not executed**. It is eligible once this correction is committed.
  - Capability 1 Gate and `EXE-P0.2.B` remain **BLOCKED**.
- *(Historical: at v1.0.7 the next command was `Execute REM-P0.1.B-02`; it was executed and approved — `c378dc9`.)*

**What changed in v1.0.8 (summary):** `EXE-P0.2.B` design-gap decisions and gate-state sync, documentation only.
- **Gate state synchronized.** On 2026-09-23 you approved `REM-P0.1.B-02`, `EXE-P0.1.G`, `EXE-P0.1.H` and Capability Gate P0.1 (after its AI PASS). Capability 1 is **closed**.
- **Your decisions HQ-1 to HQ-6** are recorded in `execution-plan.md` §18.14.6 and, for the contract, as notes in `interfaces.md` §18 and a row in §26.1 (INTF-030 text unchanged):
  - HQ-1: no new measurement source; a baseline field needs an existing mapping **and** a retrieval contract. Observability is not a `EXE-P0.2.B` dependency.
  - HQ-2: no `verified` field, no nullable change, no zeros or sentinels for unavailable values.
  - HQ-3/HQ-4: `run_optimized()`, `compare()` and `report_regression()` are deferred forward contract; no stub; `RegressionReport` not invented.
  - HQ-5: `EvaluationRun`/`EvaluationComparison` live in `control_plane/evaluation/`.
  - HQ-6: `run_baseline()` is not idempotent by `request_id`.
- **New gaps.** `SOURCE-GAP-EXECPLAN-07` (no mapping/retrieval contract for any baseline measurement) and `-08` (no BASELINE-only representation for the non-nullable comparison fields) are **blocking**; `-09` (tenant check inside `compare()`) is not.
- **Status.** `EXE-P0.2.B` is **BLOCKED** and not executed. No unit is executable until a source-contract correction closes `-07`/`-08`.

**What changed in v1.0.9 (summary):** execution assistance, documentation only.
- Claude Code now routes the repository's own skills and subagents inside the unit you authorized — see **Execution Assistance — Automatic Routing** below. You don't run them yourself.
- The AI Verification report gains line 10, *Execution-Assistance Verification*.
- Nothing else changes: same commands, same one-unit-at-a-time rule, same approvals, same counts. `EXE-P0.2.B` stays **BLOCKED** — assistance does not resolve source-contract gaps.

**What changed in v1.0.10 (summary):** remediation registration, documentation only.
- **New remediation unit `REM-P0.2.B-01`** (Baseline Benchmark Harness + Quality Evaluation — Contract / Design Reconciliation), registered in `execution-plan.md` §18.14.7. It investigates decision areas D-A to D-D for `SOURCE-GAP-EXECPLAN-07`/`-08` and brings every open question to you. It decides nothing itself and changes no contract or code.
- **The session name `REM-P0.2.B-DESIGN-01` is not a valid command.** Use `Execute REM-P0.2.B-01`. *(v1.0.11: that unit has since been executed and approved; do not send it again.)*
- **Status.** `REM-P0.2.B-01` is **registered / not started**. It is eligible once this correction is committed. `SOURCE-GAP-EXECPLAN-07`/`-08` stay **OPEN**, and `EXE-P0.2.B` stays **BLOCKED**.
- **Your approval of `REM-P0.2.B-01` does not unblock `EXE-P0.2.B`.** A separate source-contract correction that you direct must still promote your decisions and close `-07`/`-08`. See §8 Step 1a and §20.

**What changed in v1.0.11 (summary):** promotion of your `REM-P0.2.B-01` decisions and registration of two units, documentation and contract text only. Where this runbook and `execution-plan.md` differ, the plan wins.
- **`REM-P0.2.B-01` is done.** Executed (`e4cf1f5`), AI-verified and approved by you on 2026-09-24; the plan now records it. Do not send `Execute REM-P0.2.B-01` again.
- **Your decisions are promoted** into `interfaces.md` §18 (new `BaselineEvaluationRecord`, field mappings, and the retrieval rule: all matching entries for a tenant and request are counted first, exactly one is required, and it must then be verified) with notes in `eval.md` and `implementation-plan.md` (plan §18.14.8, label DB-3). `SOURCE-GAP-EXECPLAN-07`/`-08` are resolved at source (additive status updates in the plan's §38); `-09` is narrowed; `-10` to `-15` are new gaps. Six contract decisions of 2026-09-25 are recorded as human contract decisions, not source-derived: `schema_version` `1.0.0`, `source_entry_id`, the carrying of `run_type`, the count-all-then-verify count basis, `run_baseline()` failing without a result when no valid measurement exists, and the retention of the `interfaces.md` document Version 1.2.0 with an amendment-history entry.
- **Two units are registered, not executed:** `REM-P0.2.B-02` (accounting retrieval; **must be approved before `EXE-P0.2.B`**) and `REM-P0.2.B-03` (Path A producer design; **not** a prerequisite). *(Update, 2026-09-27: `REM-P0.2.B-02` has since been executed — `bf74ac3` — AI-verified `PASS`, and approved by you. Do not send `Execute REM-P0.2.B-02` again; see §20.)*
- **`EXE-P0.2.B` is now contract-level.** It uses a labelled test-fixture entry and claims no live measurement; live Path A measurement is explicitly deferred at Capability Gate P0.2 (plan §18.10).
- **Status.** `EXE-P0.2.B` is not yet executable: it needs `REM-P0.2.B-02` first. Each unit needs its own explicit `Execute` and `Approve`; nothing in this runbook authorizes or chains any of them. *(Update, 2026-09-27: `REM-P0.2.B-02` and `EXE-P0.2.B` are both now done — executed, AI-verified `PASS`, and approved (`bf74ac3`, `a2d022f`). `EXE-P0.2.C` is NOT executed and NOT authorized; see §20.)*

---

# 1. How to Use This Runbook

You do **not** paste this entire document into Claude Code.

At each interval:

1. Copy only the next `Execute ...` command.
2. Send it to Claude Code.
3. Let Claude Code perform only that one atomic unit.
4. Claude Code runs its own **AI Verification** automatically (you do not send a `Verify` command) and ends with an `AI VERIFICATION REPORT` and one verdict.
5. If the report ends `### AI VERIFICATION BLOCKED`: do **not** approve. Resolve the stated remediation, then re-issue the `Execute` command.
6. If it ends `### AI VERIFIED — AWAITING HUMAN APPROVAL`: review the AI report **and** the actual evidence (diff, tests, commit).
7. Send the corresponding `Approve ...` command.
8. Continue only when you are ready.

Before resuming a paused session, check §20 ("Current Position") so the next command is the right one.

You can stop for hours or days after any approval checkpoint. Nothing in this runbook assumes continuous execution.

---

# 2. Canonical Commands

**Always type the full canonical unit ID.** A command that names only a capability (for example `Execute P0.1`) is not a valid execution command — Claude Code will ask for the full ID instead of guessing which sub-phase you mean.

## Atomic execution

```text
Execute EXE-P0.<capability>.<sub-phase>
```

Examples:

```text
Execute EXE-P0.1.A
Execute EXE-P0.1.B
Execute EXE-P0.2.A
Execute EXE-P0.6.H
```

## Atomic approval

```text
Approve EXE-P0.<capability>.<sub-phase>
```

Examples:

```text
Approve EXE-P0.1.A
Approve EXE-P0.1.B
Approve EXE-P0.6.H
```

## Capability Gate approval

```text
Approve CAPABILITY-GATE P0.<capability>
```

Examples:

```text
Approve CAPABILITY-GATE P0.1
Approve CAPABILITY-GATE P0.6
```

Valid only after Claude Code has reported `AI CAPABILITY GATE VERIFICATION: PASS` for that Gate.

## Capability Gate review trigger (resume in a later session)

```text
Review CAPABILITY-GATE P0.<capability>
```

Asks Claude Code to perform the Gate review and AI Gate Verification. It authorizes no implementation and no approval.

## Remediation units (history-specific, v1.0.4)

```text
Execute REM-P0.1.A-01
Approve REM-P0.1.A-01
Execute REM-P0.1.A-02
Approve REM-P0.1.A-02
```

From v1.0.6 (`execution-plan.md` §18.14.4):

```text
Execute REM-P0.1.B-01
Approve REM-P0.1.B-01
Execute REM-P0.1.B-02
Approve REM-P0.1.B-02
```

From v1.0.10 (`execution-plan.md` §18.14.7): *(v1.0.11: both commands below were executed and approved — `e4cf1f5` — and must not be sent again; retained as history.)*

```text
Execute REM-P0.2.B-01
Approve REM-P0.2.B-01
```

From v1.0.11 (`execution-plan.md` §18.14.8): *(update, 2026-09-27: the two commands below for `REM-P0.2.B-02` were executed and approved — `bf74ac3` — and must not be sent again; retained as history. `REM-P0.2.B-03` remains registered, not executed; send a command only when you decide to run that unit.)*

```text
Execute REM-P0.2.B-02
Approve REM-P0.2.B-02
Execute REM-P0.2.B-03
Approve REM-P0.2.B-03
```

Same two-stage checkpoint as any `EXE-P0` unit. Not counted among the 48 units or 54 checkpoints (`execution-plan.md` §18.14).

## AI verification

There is **no** operator command for AI verification — Claude Code performs it automatically inside every `Execute` / `Review` command. Its possible verdicts:

```text
AI VERIFICATION: PASS
AI VERIFICATION: BLOCKED
AI VERIFICATION: NOT APPLICABLE
AI VERIFICATION: PASS WITH DOCUMENTED NON-BLOCKING GAP
```

Never combine an approval and the next execution into one message.

---

# 3. Mandatory Execution Rules

## Rule 1 — One atomic unit only

Every `Execute EXE-P0.n.X` command authorizes exactly one sub-phase.

Claude Code must not execute:

- the next sub-phase
- another sub-phase
- another capability
- the Capability Gate

unless separately authorized.

## Rule 2 — Stop after every atomic unit

After completing the requested unit, Claude Code must:

- re-read the authoritative source sections (not just the plan row)
- verify prerequisites actually exist and were approved
- run required tests/checks, including the failure-path test
- perform AI Verification (checklist A–J, `execution-plan.md` §18.12.1)
- inspect git scope
- if the verdict is PASS (or PASS WITH DOCUMENTED NON-BLOCKING GAP): create exactly one commit for the A–H sub-phase
- if NOT APPLICABLE: create no commit
- if BLOCKED: create no commit and write no further code
- report evidence
- stop

It must end at either `### AI VERIFIED — AWAITING HUMAN APPROVAL` or `### AI VERIFICATION BLOCKED`.

## Rule 3 — Approval is explicit

A passing test does not authorize the next unit.

A successful commit does not authorize the next unit.

A clean git status does not authorize the next unit.

Claude Code's own `AI VERIFICATION: PASS` does not authorize the next unit — it is evidence, not authorization.

A successful Capability Gate review (`AI CAPABILITY GATE VERIFICATION: PASS`) does not automatically authorize the next `Execute` command.

## Rule 4 — One commit per A–H sub-phase that produces changes

```text
One executable A–H sub-phase = one isolated implementation change-set.
Sub-phase with implementation changes = one implementation commit.
Genuinely NOT APPLICABLE sub-phase = no implementation commit (never a fake commit).
Capability Gate = no implementation commit.
Remediation unit = one remediation commit (not counted among the 48).
```

An implementation commit contains only the files its unit names — never `.vscode/`, `CLAUDE.md`, or progress/status docs.

## Rule 5 — Capability Gate is separate

After `Approve EXE-P0.n.H`, the Capability Gate is reviewed separately.

The Gate is not a ninth implementation sub-phase.

## Rule 6 — No automatic chaining

Claude Code must never interpret:

```text
Approve EXE-P0.1.A
```

as permission to automatically execute:

```text
EXE-P0.1.B
```

The next command must be explicitly issued by the human operator.

## Rule 7 — Ambiguous commands are not used

Do not use:

```text
Execute P0.1
Implement P0.1
Complete Capability 1
Continue with P0
Finish P0.1
Implement everything ready
Proceed with the remaining steps
```

Use only the canonical commands in this runbook.

## Rule 8 — AI verification comes before approval (v1.0.4)

Claude Code must not ask for your approval until its own AI Verification has finished with an acceptable verdict. After a `BLOCKED` verdict it must not ask for approval at all. If you send `Approve ...` for a blocked unit, Claude Code should refuse to treat it as progression.

> AI verification is evidence; human approval is authorization. Neither substitutes for the other.

## Rule 9 — Consumers never create shared foundations (v1.0.4)

The shared `core/interfaces`, `core/schemas`, `core/errors` packages belong to Capability 1 (`EXE-P0.1.A`, or in this repository's history, `REM-P0.1.A-01`/`-02`). A later unit that finds them missing must stop with `AI VERIFICATION: BLOCKED` — it must never create them "on the way".

## Rule 10 — No out-of-band files in unit commits (v1.0.4)

Editor settings, `CLAUDE.md`, and status/progress docs stay out of `EXE-P0` commits. Commit `c3d6ecf` is recorded history, not an implementation commit, and not evidence that any unit was completed.

## Execution Assistance — Automatic Routing (v1.0.9)

You keep using only the canonical commands: `Execute EXE-P0.n.X`, then (after the AI Verification report) `Approve EXE-P0.n.X`; `Review CAPABILITY-GATE P0.n`, then `Approve CAPABILITY-GATE P0.n`. **You never need to type "run skill X" or "verify with agent Y".**

Inside the unit you authorized, Claude Code decides which repository-defined assistance applies (`execution-plan.md` §18.19), using the `eaioc-agent-orchestration` skill:

- **`eaioc-code-architect`** — design check, usually before code (design units, new packages/contracts).
- **`eaioc-code-reviewer`** and **`eaioc-code-verifier`** — review of the actual change and requirement-by-requirement conformance, before the verdict. At a Capability Gate the Verifier always checks the whole capability.
- All three are read-only; they can't change files, commit, or approve.
- **`eaioc-guide`** (Q&A, publishes an HTML report) and **`eaioc-dashboard`** (charts for such reports) are *not* used automatically inside a unit — ask a question when you want them.

The report says which assistance was used and which was not applicable, and why (line 10). Agent results are evidence only: an agent PASS never overrides a failed test, a missing prerequisite, or an open source gap, and nothing an agent says counts as your approval. If a prerequisite or contract is missing, Claude Code stops before writing code and reports `AI VERIFICATION: BLOCKED`.

---

# 4. Claude Code Response Expected After Every Execute Command

Claude Code should report:

```text
EXECUTED: EXE-P0.n.X
CAPABILITY: <name>
SUB-PHASE: <name>
```

Then:

- Implementation / Design Summary
- Source Traceability
- Verification
- Test Results
- Git Evidence
- Changed Files
- Scope Compliance

It must confirm:

```text
Only the requested atomic unit was executed.
No later sub-phase was executed.
No other capability was executed.
No upstream document was modified.
```

Then the mandatory AI Verification Report:

```text
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

AI VERIFICATION: PASS / BLOCKED / NOT APPLICABLE / PASS WITH DOCUMENTED NON-BLOCKING GAP

Blocking Issues:
<none or explicit issues>
```

Then stop.

For A–G (when the verdict is acceptable):

```text
### AI VERIFIED — AWAITING HUMAN APPROVAL

AI VERIFICATION: PASS

Approve EXE-P0.n.X to continue.
```

For H:

```text
### AI VERIFIED — AWAITING HUMAN APPROVAL

AI VERIFICATION: PASS

Approve EXE-P0.n.H to proceed to the Capability Gate.
```

For an N/A sub-phase (no commit):

```text
### AI VERIFIED — AWAITING HUMAN APPROVAL

AI VERIFICATION: NOT APPLICABLE

Approve EXE-P0.n.X to continue.
```

If blocked (no approval request, no commit):

```text
### AI VERIFICATION BLOCKED

AI VERIFICATION: BLOCKED

Reason:
Required remediation:

Human approval is NOT requested.
The next execution unit is NOT authorized.
```

**What to check before you approve:** the AI report's nine sections are all filled in with evidence (not just "OK"); the tests it names actually ran; `git log -1 --stat` shows only the unit's own files; any "non-blocking gap" it cites is a gap the plan already classifies as non-blocking.

---

# 5. Capability Gate Protocol

After the H approval, the Capability Gate is reviewed in three stages (`execution-plan.md` §18.10):

```text
Human Approval of H
   ↓
Capability Gate Review
   ↓
AI Gate Verification
   ↓
Human Gate Approval
   ↓
Next Capability A (still needs its own Execute command)
```

If you are resuming in a new session, trigger the review with:

```text
Review CAPABILITY-GATE P0.n
```

The Gate must consider:

- Entry Criteria
- Implementation Completion
- Integration Completion
- Security/Governance Completion
- Quality Completion
- Scenario Completion
- Failure/Recovery Completion
- Exit Criteria
- Blocking Conditions

For capabilities whose units ran before v1.0.4, the AI Gate Verification also re-examines those units' evidence, and any open reconciliation finding (`execution-plan.md` §18.14) is a Blocking Condition.

If AI Gate Verification passes, the review must produce:

```text
CAPABILITY GATE REVIEW: P0.n
AI CAPABILITY GATE VERIFICATION: PASS
STATUS: AWAITING HUMAN APPROVAL

Approve CAPABILITY-GATE P0.n
```

If blocked:

```text
### CAPABILITY GATE BLOCKED

AI CAPABILITY GATE VERIFICATION: BLOCKED

Reason:
Required remediation:
No next capability may begin.
Human Gate Approval is not requested.
```

If the AI Gate Verification passed and you explicitly approve it:

```text
Approve CAPABILITY-GATE P0.n
```

Claude Code may then report:

```text
CAPABILITY GATE APPROVED: P0.n
NEXT AUTHORIZED UNIT: EXE-P0.(n+1).A
```

It must still wait.

The operator must separately issue:

```text
Execute EXE-P0.(n+1).A
```

---

# 6. P0 Capability Order

The required execution order is:

```text
Capability 1 — Token Accounting and Cost Ledger
Capability 2 — Baseline Benchmark Harness + Quality Evaluation
Capability 3 — Sanitizer
Capability 4 — Context Policy
Capability 5 — Prompt Assembler
Capability 6 — Output Controls
```

This order is followed strictly even when the dependency model says some early capabilities have no cross-capability prerequisite.

---

# 7. P0 Execution Checklist

## CAPABILITY 1 — Token Accounting and Cost Ledger

**Working label:** `EXE-P0.1`

**Dependency:** None

**Package:** `control_plane/accounting/`

**Goal:** Establish per-request token/cost recording and tenant-scoped ledger behavior.

### Step 1 — Contract & Design

```text
Execute EXE-P0.1.A
```

Claude Code performs:

- contract/design review
- verification against `architecture.md` §27.1–27.3
- **(v1.0.4)** the shared-core-foundation contract: design and minimal scaffolding definition of `control_plane/core/interfaces/`, `core/schemas/`, `core/errors/`, including `ControlPlaneRequest`, `OptimizationPlan`, `ControlPlaneError` — fields verified against the live `interfaces.md` §2.1/§3.2/§25, never invented
- no implementation beyond the design deliverable
- AI verification
- one commit
- stops

Then:

```text
Approve EXE-P0.1.A
```

> **This repository's history:** `EXE-P0.1.A` was already executed (commit `a345742`) under v1.0.3 and did **not** produce the shared-core foundation. It is not re-executed; the gap is closed by the remediation units in §7a below.

### Step 2 — Minimal Implementation

```text
Execute EXE-P0.1.B
```

Then:

```text
Approve EXE-P0.1.B
```

### Step 3 — Integration

```text
Execute EXE-P0.1.C
```

Then:

```text
Approve EXE-P0.1.C
```

### Step 4 — Observability

```text
Execute EXE-P0.1.D
```

Then:

```text
Approve EXE-P0.1.D
```

### Step 5 — Security/Governance

```text
Execute EXE-P0.1.E
```

Then:

```text
Approve EXE-P0.1.E
```

### Step 6 — Quality Validation

```text
Execute EXE-P0.1.F
```

Expected: `AI VERIFICATION: NOT APPLICABLE` (a ledger has no `QG-NNN` quality dimension) — **no commit**.

Then:

```text
Approve EXE-P0.1.F
```

> This acknowledges the N/A disposition. In this repository's history F has no commit (correct). If you never explicitly acknowledged F's N/A status, do it before the Capability 1 Gate (see §20).

### Step 7 — Scenario Validation

```text
Execute EXE-P0.1.G
```

Then:

```text
Approve EXE-P0.1.G
```

### Step 8 — Failure/Recovery

```text
Execute EXE-P0.1.H
```

Then:

```text
Approve EXE-P0.1.H
```

### Step 7a — Shared Core Foundation Remediation (history-specific, v1.0.4)

Needed because the executed `EXE-P0.1.A` did not create the shared `core/` foundation (`execution-plan.md` §18.14 — `RECONCILED: GAP FOUND`). These run **before** the Capability 1 Gate and **before** `EXE-P0.2.B`. They do not rewrite any earlier commit.

**Remediation 1 — Contract & Design**

```text
Execute REM-P0.1.A-01
```

Claude Code produces `control_plane/core/CoreFoundation.md` only:

- every field of `ControlPlaneRequest` (§2.1), `OptimizationPlan` (§3.2), `ControlPlaneError` (§25.1–25.2), and their sub-types, copied from the live `interfaces.md`
- for each type/field: included in the P0 foundation, or deferred to a named later capability with a reason
- Java package mapping `com.eaioc.controlplane.core.interfaces` / `.schemas` / `.errors`
- rule: `core/` imports nothing else from `control_plane/`
- how required fields like `tenant_id` are enforced at construction
- Capability 1's existing local exceptions are **not** moved

Review carefully: the "full set vs. subset" decision is yours to approve.

```text
Approve REM-P0.1.A-01
```

**Remediation 2 — Minimal Scaffold**

```text
Execute REM-P0.1.A-02
```

Claude Code creates exactly the approved Java types under `src/main/java/com/eaioc/controlplane/core/**` plus tests under `src/test/java/com/eaioc/controlplane/core/**` — nothing else. Expect: `mvn test` passes (existing suite plus new tests); a missing/blank `tenant_id` is rejected; grep shows no `core/` import of other control-plane packages.

```text
Approve REM-P0.1.A-02
```

*(Status: both A remediation units are done and approved — `c05e3e7`, `93c1ba3`, `5e12148`.)*

### Step 7b — Ledger Contract (AC-005) Remediation (history-specific, v1.0.6)

**Why this is needed.** The executed `EXE-P0.1.B` implements the old `INTF-047`, which had no fields for AC-005's compressed, retrieved or reused tokens and did not represent `architecture.md` §27.1 (`execution-plan.md` §18.14.4, `CONTRA-EXECPLAN-01`, reconciled `GAP FOUND`). The contract is now corrected in `interfaces.md` §28.1. These two units bring the code in line. They do not rewrite any earlier commit.

**Remediation 1 — Contract / Design Reconciliation**

```text
Execute REM-P0.1.B-01
```

Claude Code writes a design note only, strictly from the corrected `interfaces.md` §28.1:
- the Java form of the nine nested groups (all 58 §27.1 members), with the 29 existing fields kept unchanged and **not** aliased;
- how `verified` and `unverifiedFallback()` cover the new groups without fabricating values;
- the contract tests.

It will ask **you** to decide:
- the 22 `SOURCE-UNRESOLVED` types (`SOURCE-GAP-EXECPLAN-05`);
- whether a `null` "where available" value makes an entry `unverified` (`SOURCE-GAP-EXECPLAN-06`).

It must not invent those. If one would have to be assumed, the verdict is BLOCKED.

```text
Approve REM-P0.1.B-01
```

Then, if you decided any types or behavior, those must also be written into `interfaces.md` §28.1 through a correction you direct, before Remediation 2.

*(Status v1.0.7: `REM-P0.1.B-01` is done, AI-verified and human-approved (`34dd1f1`). Its decisions HD-1 to HD-4 are promoted into the contract by the DB-2 correction, `execution-plan.md` §18.14.5.)*

**Remediation 2 — Minimal Implementation Remediation**

```text
Execute REM-P0.1.B-02
```

Claude Code implements only the approved design in `accounting/ledger/**` (main and test).

Expect:
- `mvn test` passes;
- new contract tests show all nine groups and 58 members, with the six AC-005 categories recorded separately;
- the existing 29 fields are unchanged;
- a missing `tenant_id` is still rejected, and a failed accounting computation still yields a `verified=false` fallback.

```text
Approve REM-P0.1.B-02
```

### G/H approval evidence (v1.0.6)

*v1.0.8: resolved — you issued `Approve EXE-P0.1.G` and `Approve EXE-P0.1.H` on 2026-09-23. The text below is kept as history.*

The repository does not show that `Approve EXE-P0.1.G` or `Approve EXE-P0.1.H` was ever issued. Before the Gate review, either confirm explicitly that you approved them earlier, or review their retrospective AI verification and send:

```text
Approve EXE-P0.1.G
Approve EXE-P0.1.H
```

### Capability 1 Gate

After `Approve EXE-P0.1.H` (and, in this repository, after the §7a and §7b remediation units are approved and the G/H approvals are evidenced), trigger the review:

```text
Review CAPABILITY-GATE P0.1
```

Claude Code reports `AI CAPABILITY GATE VERIFICATION: PASS` or `BLOCKED`. Only after PASS:

```text
Approve CAPABILITY-GATE P0.1
```

Then wait.

Next execution command:

```text
Execute EXE-P0.2.A
```

> In this repository `EXE-P0.2.A` is already executed and approved (commit `0e92261`, reconciled `PASS`). The next command after the Gate is therefore `Execute EXE-P0.2.B` — see §20.

---

# 8. CAPABILITY 2 — Baseline Benchmark Harness + Quality Evaluation

**Working label:** `EXE-P0.2`

**Dependency:** Capability 1 Gate approved

**Packages:**
- `control_plane/benchmarking/`
- `control_plane/evaluation/`

**Goal:** Establish the P0 baseline/evaluation substrate.

### Step 1 — Contract & Design

```text
Execute EXE-P0.2.A
```

Then:

```text
Approve EXE-P0.2.A
```

### Step 1a — Evaluation Measurement Contract Remediation (v1.0.10)

**Unit:** `REM-P0.2.B-01` (`execution-plan.md` §18.14.7). It is a remediation unit, outside the 48 units and 54 checkpoints, and runs before `EXE-P0.2.B`. Status: **registered / not started** (v1.0.10); **v1.0.11: executed (`e4cf1f5`), AI-verified and human-approved (2026-09-24); done.** This step is complete: do not send the `Execute`/`Approve REM-P0.2.B-01` commands shown below again; they are retained as history.

```text
Execute REM-P0.2.B-01
```

What Claude Code does:
- Produces a design note under `control_plane/evaluation/` only, re-verifying and revising the pre-decision record `EvaluationMeasurementReconciliation.md`. No contract, plan, code or test change.
- Investigates D-A (how baseline measurements are acquired, and by whom), D-B (a BASELINE-only `EvaluationRun` without fabricated values), D-C (the Java realization of INTF-030) and D-D (any remaining contract decision, including the evaluation/observability relationship).
- Where the sources don't settle a question, it presents the options to you. It never decides for you, and never invents a mapping, nullability, `verified` field, placeholder value, `RegressionReport`, or Observability dependency.
- Runs the Architect and Verifier agents automatically, then gives the AI Verification report.

It returns `AI VERIFICATION: BLOCKED` if any decision would have to be assumed rather than made by you.

Then, after reviewing:

```text
Approve REM-P0.2.B-01
```

This approves the remediation only. It does **not** unblock `EXE-P0.2.B`. Next, you direct a source-contract correction that writes your decisions into the contract and closes `SOURCE-GAP-EXECPLAN-07`/`-08`. Only after that is committed may `Execute EXE-P0.2.B` be sent.

*(v1.0.11: that source-contract correction is DB-3, `execution-plan.md` §18.14.8. The next step is Step 1b.)*

### Step 1b — Retrieval Realization (v1.0.11)

**Unit:** `REM-P0.2.B-02` (`execution-plan.md` §18.14.8). Remediation unit, outside the 48 units and 54 checkpoints. Status: **DONE — executed (`bf74ac3`), AI-verified (`PASS`), human-approved (2026-09-27).** Do not send `Execute REM-P0.2.B-02` again.

```text
Execute REM-P0.2.B-02
```

What Claude Code did: implemented only the `get_request_cost` subset in `control_plane/accounting/` — exactly one matching ledger entry per tenant and request, counted over all matching entries and consumed only when `verified`, is a measurement; none or several (including a verified entry plus an unverified one) is no measurement — and ran the Architect, Reviewer and Verifier agents (all satisfactory). It changed nothing in `evaluation/`, `benchmarking/`, `core/` or any document. `LedgerCostReporter` lives in `accounting.ledger` (same package as `CostLedgerStore`), not a separate `accounting.reporting` package, because Capability 1's Gate-P0.1-approved tenant-isolation guard test structurally limits `CostLedgerStore`'s public surface to exactly `{write, read}`; the new enumeration helper is package-private instead. Gate P0.1 is not reopened.

Then, after reviewing:

```text
Approve REM-P0.2.B-02
```

This approved the remediation only; it did not itself authorize `EXE-P0.2.B`. Both commands above are done — do not resend either.

### Step 1c — Path A Producer Design (v1.0.11, optional; not a prerequisite)

**Unit:** `REM-P0.2.B-03` (`execution-plan.md` §18.14.8). Registered, **not executed**. **Not** a prerequisite of `EXE-P0.2.B`; not required before Capability Gate P0.2, where live Path A measurement is explicitly deferred (plan §18.10). Run it only when you decide to.

```text
Execute REM-P0.2.B-03
Approve REM-P0.2.B-03
```

### Step 2 — Minimal Implementation

```text
Execute EXE-P0.2.B
```

**Hard pre-execution guard (`execution-plan.md` §18.15).** Before writing any code, Claude Code checks:

```text
control_plane/core/interfaces/ exists
control_plane/core/schemas/ exists
control_plane/core/errors/ exists
required shared request/plan types exist
those types match the authoritative interfaces
Capability 1's corrected shared-foundation ownership has been satisfied
Capability 1 Gate has been approved
Capability 2.A has been approved
```

If any check fails → `AI VERIFICATION: BLOCKED`, no code, no commit, and it names exactly what is missing. If it passes, `run_baseline()` is built against the shared `ControlPlaneRequest` from `core/schemas/` (never a harness-local stand-in); `run_optimized()` is an explicit no-op.

**v1.0.8 — currently blocked.** The guard passes, but `SOURCE-GAP-EXECPLAN-07`/`-08` (`execution-plan.md` §18.14.6) mean an honest `EvaluationRun` can't be built yet. Only the baseline path is in scope; `run_optimized()`, `compare()` and `report_regression()` are deferred with no stub. Don't send this command until a source-contract correction has closed `-07`/`-08`.

**v1.0.10.** The remediation that prepares that correction is `REM-P0.2.B-01` (Step 1a above). This step stays blocked until `REM-P0.2.B-01` is approved **and** the follow-up source-contract correction closes `-07`/`-08`.

**v1.0.11.** The source-contract correction is done (DB-3): `-07`/`-08` are resolved at source. This step is conditioned on `REM-P0.2.B-02` being executed, AI-verified and approved (Step 1b), then pre-flight, the §18.15 guard (with its v1.0.11 addition) and a Code Architect re-assessment. It is contract-level and fixture-based (a labelled test-fixture ledger entry) and claims no live Path A measurement. `Execute REM-P0.2.B-03` is not needed first.

*(Update, 2026-09-27: `REM-P0.2.B-02` is now done — executed, AI-verified `PASS`, and approved (`bf74ac3`) — so that precondition is cleared.)*

*(Update, 2026-09-27: this step is now DONE — executed, AI-verified `PASS`, and **approved** (`a2d022f`; 92/92 tests passing). Reviewed by all three governance agents (Architect `APPROVED FOR IMPLEMENTATION`, Reviewer `PASS WITH REQUIRED FOLLOW-UP` — one LOW finding, fixed in the same commit, Verifier `CONFORMANT WITH DOCUMENTED GAPS`); the Architect/Reviewer/Verifier routing ran after implementation rather than before, a documented deviation from the normal sequence, disclosed and not concealed — see `CLAUDE.md`'s "Current position" for the full account. Do not send `Execute EXE-P0.2.B` or `Approve EXE-P0.2.B` again. This approval covers only Sub-phase B — it does **not** authorize Step 3 below.)*

Then:

```text
Approve EXE-P0.2.B
```

### Step 3 — Integration

```text
Execute EXE-P0.2.C
```

Then:

```text
Approve EXE-P0.2.C
```

### Step 4 — Observability

```text
Execute EXE-P0.2.D
```

Then:

```text
Approve EXE-P0.2.D
```

### Step 5 — Security/Governance

```text
Execute EXE-P0.2.E
```

Then:

```text
Approve EXE-P0.2.E
```

### Step 6 — Quality Validation

```text
Execute EXE-P0.2.F
```

Expected: `AI VERIFICATION: NOT APPLICABLE` (no `QG-NNN` gate at P0 scope) — **no commit**. Your approval acknowledges the N/A disposition.

Then:

```text
Approve EXE-P0.2.F
```

### Step 7 — Scenario Validation

```text
Execute EXE-P0.2.G
```

Then:

```text
Approve EXE-P0.2.G
```

### Step 8 — Failure/Recovery

```text
Execute EXE-P0.2.H
```

Then:

```text
Approve EXE-P0.2.H
```

### Capability 2 Gate

**v1.0.11 — explicit deferral.** The Gate P0.2 review and its AI Gate Verification must state: "Live Path A end-to-end baseline measurement: NOT DEMONSTRATED — explicitly deferred (§18.14.8)." (`execution-plan.md` §18.10). A passing `EXE-P0.2.B` or Gate P0.2 is not evidence of a live measurement; your Gate approval approves the contract-level substrate only.

After `Approve EXE-P0.2.H`, Claude Code performs the Gate review and AI Gate Verification (in a new session, trigger it with `Review CAPABILITY-GATE P0.2`). Only after `AI CAPABILITY GATE VERIFICATION: PASS`:

```text
Approve CAPABILITY-GATE P0.2
```

Then wait.

Next execution command:

```text
Execute EXE-P0.3.A
```

---

# 9. CAPABILITY 3 — Sanitizer

**Working label:** `EXE-P0.3`

**Dependency:** None in the dependency model, but execution remains strictly sequential. **Precondition (v1.0.4):** the shared `core/` foundation exists — `EXE-P0.3.B` gets the same guard as `EXE-P0.2.B` (blocked if `core/` is missing).

**Package:** `control_plane/pipeline/t1_context/sanitizer/`

**Goal:** Establish the first T1 sanitization stage.

### Step 1 — Contract & Design

```text
Execute EXE-P0.3.A
```

Then:

```text
Approve EXE-P0.3.A
```

### Step 2 — Minimal Implementation

```text
Execute EXE-P0.3.B
```

Then:

```text
Approve EXE-P0.3.B
```

### Step 3 — Integration

```text
Execute EXE-P0.3.C
```

Then:

```text
Approve EXE-P0.3.C
```

### Step 4 — Observability

```text
Execute EXE-P0.3.D
```

Then:

```text
Approve EXE-P0.3.D
```

### Step 5 — Security/Governance

```text
Execute EXE-P0.3.E
```

Then:

```text
Approve EXE-P0.3.E
```

### Step 6 — Quality Validation

```text
Execute EXE-P0.3.F
```

Expected: `AI VERIFICATION: NOT APPLICABLE` (no `QG-NNN` gate at P0 scope) — **no commit**. Your approval acknowledges the N/A disposition.

Then:

```text
Approve EXE-P0.3.F
```

### Step 7 — Scenario Validation

```text
Execute EXE-P0.3.G
```

Then:

```text
Approve EXE-P0.3.G
```

### Step 8 — Failure/Recovery

```text
Execute EXE-P0.3.H
```

Then:

```text
Approve EXE-P0.3.H
```

### Capability 3 Gate

After `Approve EXE-P0.3.H`, Claude Code performs the Gate review and AI Gate Verification (in a new session, trigger it with `Review CAPABILITY-GATE P0.3`). Only after `AI CAPABILITY GATE VERIFICATION: PASS`:

```text
Approve CAPABILITY-GATE P0.3
```

Then wait.

Next execution command:

```text
Execute EXE-P0.4.A
```

---

# 10. CAPABILITY 4 — Context Policy

**Working label:** `EXE-P0.4`

**Dependency:** Capability 1 Gate approved

**Package:** Proposed `control_plane/policy/context_policy/`

**Important:** Package placement must be confirmed during Sub-phase A.

### Step 1 — Contract & Design

```text
Execute EXE-P0.4.A
```

Then:

```text
Approve EXE-P0.4.A
```

### Step 2 — Minimal Implementation

```text
Execute EXE-P0.4.B
```

Then:

```text
Approve EXE-P0.4.B
```

### Step 3 — Integration

```text
Execute EXE-P0.4.C
```

Then:

```text
Approve EXE-P0.4.C
```

### Step 4 — Observability

```text
Execute EXE-P0.4.D
```

Then:

```text
Approve EXE-P0.4.D
```

### Step 5 — Security/Governance

```text
Execute EXE-P0.4.E
```

Then:

```text
Approve EXE-P0.4.E
```

### Step 6 — Quality Validation

```text
Execute EXE-P0.4.F
```

Expected: `AI VERIFICATION: NOT APPLICABLE` (no `QG-NNN` gate at P0 scope) — **no commit**. Your approval acknowledges the N/A disposition.

Then:

```text
Approve EXE-P0.4.F
```

### Step 7 — Scenario Validation

```text
Execute EXE-P0.4.G
```

Then:

```text
Approve EXE-P0.4.G
```

### Step 8 — Failure/Recovery

```text
Execute EXE-P0.4.H
```

Then:

```text
Approve EXE-P0.4.H
```

### Capability 4 Gate

After `Approve EXE-P0.4.H`, Claude Code performs the Gate review and AI Gate Verification (in a new session, trigger it with `Review CAPABILITY-GATE P0.4`). Only after `AI CAPABILITY GATE VERIFICATION: PASS`:

```text
Approve CAPABILITY-GATE P0.4
```

Then wait.

Next execution command:

```text
Execute EXE-P0.5.A
```

---

# 11. CAPABILITY 5 — Prompt Assembler

**Working label:** `EXE-P0.5`

**Dependencies:** Capability 3 Gate + Capability 4 Gate

**Package:** Proposed `control_plane/prompt_assembler/`

**Important:** Package placement must be confirmed during Sub-phase A.

### Step 1 — Contract & Design

```text
Execute EXE-P0.5.A
```

Then:

```text
Approve EXE-P0.5.A
```

### Step 2 — Minimal Implementation

```text
Execute EXE-P0.5.B
```

Then:

```text
Approve EXE-P0.5.B
```

### Step 3 — Integration

```text
Execute EXE-P0.5.C
```

Then:

```text
Approve EXE-P0.5.C
```

### Step 4 — Observability

```text
Execute EXE-P0.5.D
```

Then:

```text
Approve EXE-P0.5.D
```

### Step 5 — Security/Governance

```text
Execute EXE-P0.5.E
```

Then:

```text
Approve EXE-P0.5.E
```

### Step 6 — Quality Validation

```text
Execute EXE-P0.5.F
```

Expected: `AI VERIFICATION: NOT APPLICABLE` (no `QG-NNN` gate at P0 scope) — **no commit**. Your approval acknowledges the N/A disposition.

Then:

```text
Approve EXE-P0.5.F
```

### Step 7 — Scenario Validation

```text
Execute EXE-P0.5.G
```

Then:

```text
Approve EXE-P0.5.G
```

### Step 8 — Failure/Recovery

```text
Execute EXE-P0.5.H
```

Then:

```text
Approve EXE-P0.5.H
```

### Capability 5 Gate

After `Approve EXE-P0.5.H`, Claude Code performs the Gate review and AI Gate Verification (in a new session, trigger it with `Review CAPABILITY-GATE P0.5`). Only after `AI CAPABILITY GATE VERIFICATION: PASS`:

```text
Approve CAPABILITY-GATE P0.5
```

Then wait.

Next execution command:

```text
Execute EXE-P0.6.A
```

---

# 12. CAPABILITY 6 — Output Controls

**Working label:** `EXE-P0.6`

**Dependency:** Capability 1 Gate approved

**Package:** Proposed `control_plane/output_controls/`

**Important:** Package placement must be confirmed during Sub-phase A.

### Step 1 — Contract & Design

```text
Execute EXE-P0.6.A
```

Then:

```text
Approve EXE-P0.6.A
```

### Step 2 — Minimal Implementation

```text
Execute EXE-P0.6.B
```

Then:

```text
Approve EXE-P0.6.B
```

### Step 3 — Integration

```text
Execute EXE-P0.6.C
```

Then:

```text
Approve EXE-P0.6.C
```

### Step 4 — Observability

```text
Execute EXE-P0.6.D
```

Then:

```text
Approve EXE-P0.6.D
```

### Step 5 — Security/Governance

```text
Execute EXE-P0.6.E
```

Then:

```text
Approve EXE-P0.6.E
```

### Step 6 — Quality Validation

```text
Execute EXE-P0.6.F
```

Expected: `AI VERIFICATION: NOT APPLICABLE` (no `QG-NNN` gate at P0 scope) — **no commit**. Your approval acknowledges the N/A disposition.

Then:

```text
Approve EXE-P0.6.F
```

### Step 7 — Scenario Validation

```text
Execute EXE-P0.6.G
```

Then:

```text
Approve EXE-P0.6.G
```

### Step 8 — Failure/Recovery

```text
Execute EXE-P0.6.H
```

Then:

```text
Approve EXE-P0.6.H
```

### Capability 6 Gate

After `Approve EXE-P0.6.H`, Claude Code performs the Gate review and AI Gate Verification (in a new session, trigger it with `Review CAPABILITY-GATE P0.6`). Only after `AI CAPABILITY GATE VERIFICATION: PASS`:

```text
Approve CAPABILITY-GATE P0.6
```

This closes the P0 capability sequence.

---

# 13. Complete P0 Command Sequence

The following is the complete idealized operator sequence for a fresh run.

**Never paste all of this into Claude Code at once.**

**v1.0.4 reading:** between every `Execute` and its `Approve`, Claude Code's AI Verification runs automatically; never send the `Approve` line after a `### AI VERIFICATION BLOCKED` report. Before each `Approve CAPABILITY-GATE`, Claude Code's AI Gate Verification must have reported `PASS` (in a new session, send `Review CAPABILITY-GATE P0.n` first). **For this repository's actual history, use §20 instead** — it inserts the remediation units `REM-P0.1.A-01`/`-02` before the Capability 1 Gate and skips units already completed.

```text
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
Execute EXE-P0.2.B
Approve EXE-P0.2.B
Execute EXE-P0.2.C
Approve EXE-P0.2.C
Execute EXE-P0.2.D
Approve EXE-P0.2.D
Execute EXE-P0.2.E
Approve EXE-P0.2.E
Execute EXE-P0.2.F
Approve EXE-P0.2.F
Execute EXE-P0.2.G
Approve EXE-P0.2.G
Execute EXE-P0.2.H
Approve EXE-P0.2.H
Approve CAPABILITY-GATE P0.2

Execute EXE-P0.3.A
Approve EXE-P0.3.A
Execute EXE-P0.3.B
Approve EXE-P0.3.B
Execute EXE-P0.3.C
Approve EXE-P0.3.C
Execute EXE-P0.3.D
Approve EXE-P0.3.D
Execute EXE-P0.3.E
Approve EXE-P0.3.E
Execute EXE-P0.3.F
Approve EXE-P0.3.F
Execute EXE-P0.3.G
Approve EXE-P0.3.G
Execute EXE-P0.3.H
Approve EXE-P0.3.H
Approve CAPABILITY-GATE P0.3

Execute EXE-P0.4.A
Approve EXE-P0.4.A
Execute EXE-P0.4.B
Approve EXE-P0.4.B
Execute EXE-P0.4.C
Approve EXE-P0.4.C
Execute EXE-P0.4.D
Approve EXE-P0.4.D
Execute EXE-P0.4.E
Approve EXE-P0.4.E
Execute EXE-P0.4.F
Approve EXE-P0.4.F
Execute EXE-P0.4.G
Approve EXE-P0.4.G
Execute EXE-P0.4.H
Approve EXE-P0.4.H
Approve CAPABILITY-GATE P0.4

Execute EXE-P0.5.A
Approve EXE-P0.5.A
Execute EXE-P0.5.B
Approve EXE-P0.5.B
Execute EXE-P0.5.C
Approve EXE-P0.5.C
Execute EXE-P0.5.D
Approve EXE-P0.5.D
Execute EXE-P0.5.E
Approve EXE-P0.5.E
Execute EXE-P0.5.F
Approve EXE-P0.5.F
Execute EXE-P0.5.G
Approve EXE-P0.5.G
Execute EXE-P0.5.H
Approve EXE-P0.5.H
Approve CAPABILITY-GATE P0.5

Execute EXE-P0.6.A
Approve EXE-P0.6.A
Execute EXE-P0.6.B
Approve EXE-P0.6.B
Execute EXE-P0.6.C
Approve EXE-P0.6.C
Execute EXE-P0.6.D
Approve EXE-P0.6.D
Execute EXE-P0.6.E
Approve EXE-P0.6.E
Execute EXE-P0.6.F
Approve EXE-P0.6.F
Execute EXE-P0.6.G
Approve EXE-P0.6.G
Execute EXE-P0.6.H
Approve EXE-P0.6.H
Approve CAPABILITY-GATE P0.6
```

---

# 14. Operator Pause/Resume Method

You can stop after any of these:

```text
### AI VERIFIED — AWAITING HUMAN APPROVAL

AI VERIFICATION: PASS

Approve EXE-P0.1.A to continue.
```

or:

```text
### AI VERIFIED — AWAITING HUMAN APPROVAL

AI VERIFICATION: PASS

Approve EXE-P0.1.H to proceed to the Capability Gate.
```

or:

```text
### AI VERIFICATION BLOCKED
```

(nothing to approve — fix the stated remediation first)

or after:

```text
CAPABILITY GATE REVIEW: P0.1
AI CAPABILITY GATE VERIFICATION: PASS
STATUS: AWAITING HUMAN APPROVAL
```

At that point, no further implementation is authorized until you send the next explicit command.

For example, after several days:

```text
Execute EXE-P0.1.B
```

is sufficient to resume, provided `EXE-P0.1.A` was previously AI-verified and approved. Claude Code re-checks that itself as part of its prerequisite verification.

---

# 15. If Claude Code Tries to Continue Automatically

Stop it and verify that it did not implement additional scope.

The expected boundary is:

```text
One Execute command
        ↓
One sub-phase
        ↓
Tests/checks
        ↓
One AI Verification (report + verdict)
        ↓
One commit (only if PASS and the unit produced changes)
        ↓
One report
        ↓
AI VERIFIED — AWAITING HUMAN APPROVAL   (or AI VERIFICATION BLOCKED)
```

Nothing beyond that boundary should occur. Also stop it if it asks for approval without an AI Verification Report, asks for approval after a BLOCKED verdict, or creates/changes shared `core/` files while executing a unit from another capability.

---

# 16. P0 Progress Tracker

Use this section as a manual checklist. From v1.0.4 each unit has three ticks: executed, AI verified (write the verdict — PASS, NOT APPLICABLE, or PASS WITH DOCUMENTED NON-BLOCKING GAP), and approved. The actual position of this repository at the v1.0.4 correction is recorded in §20.

## Capability 1 — Token Accounting

```text
[ ] EXE-P0.1.A executed
[ ] EXE-P0.1.A AI verified (verdict: ______)
[ ] EXE-P0.1.A approved
[ ] EXE-P0.1.B executed
[ ] EXE-P0.1.B AI verified (verdict: ______)
[ ] EXE-P0.1.B approved
[ ] EXE-P0.1.C executed
[ ] EXE-P0.1.C AI verified (verdict: ______)
[ ] EXE-P0.1.C approved
[ ] EXE-P0.1.D executed
[ ] EXE-P0.1.D AI verified (verdict: ______)
[ ] EXE-P0.1.D approved
[ ] EXE-P0.1.E executed
[ ] EXE-P0.1.E AI verified (verdict: ______)
[ ] EXE-P0.1.E approved
[ ] EXE-P0.1.F executed
[ ] EXE-P0.1.F AI verified (verdict: ______)
[ ] EXE-P0.1.F approved
[ ] EXE-P0.1.G executed
[ ] EXE-P0.1.G AI verified (verdict: ______)
[ ] EXE-P0.1.G approved
[ ] EXE-P0.1.H executed
[ ] EXE-P0.1.H AI verified (verdict: ______)
[ ] EXE-P0.1.H approved
[ ] REM-P0.1.A-01 executed
[ ] REM-P0.1.A-01 AI verified (verdict: ______)
[ ] REM-P0.1.A-01 approved
[ ] REM-P0.1.A-02 executed
[ ] REM-P0.1.A-02 AI verified (verdict: ______)
[ ] REM-P0.1.A-02 approved
[ ] REM-P0.1.B-01 executed
[ ] REM-P0.1.B-01 AI verified (verdict: ______)
[ ] REM-P0.1.B-01 approved
[ ] REM-P0.1.B-02 executed
[ ] REM-P0.1.B-02 AI verified (verdict: ______)
[ ] REM-P0.1.B-02 approved
[ ] EXE-P0.1.G / EXE-P0.1.H approvals evidenced (confirmed or re-issued)
[ ] Capability Gate P0.1 AI verified (PASS)
[ ] Capability Gate P0.1 approved
```

## Capability 2 — Benchmark + Quality Evaluation

```text
[ ] EXE-P0.2.A executed
[ ] EXE-P0.2.A AI verified (verdict: ______)
[ ] EXE-P0.2.A approved
[x] REM-P0.2.B-01 executed (e4cf1f5)
[x] REM-P0.2.B-01 AI verified (verdict: PASS WITH DOCUMENTED NON-BLOCKING GAP)
[x] REM-P0.2.B-01 approved (2026-09-24)
[x] Source-contract correction closing SOURCE-GAP-EXECPLAN-07/-08 committed (196e112, v1.0.11 / DB-3)
[x] REM-P0.2.B-02 executed (bf74ac3)
[x] REM-P0.2.B-02 AI verified (verdict: PASS)
[x] REM-P0.2.B-02 approved (2026-09-27)
[ ] REM-P0.2.B-03 (registered, not executed; not a prerequisite; run when directed) executed / AI verified / approved
[x] EXE-P0.2.B executed (a2d022f)
[x] EXE-P0.2.B AI verified (verdict: PASS)
[x] EXE-P0.2.B approved (2026-09-27)
[ ] EXE-P0.2.C executed
[ ] EXE-P0.2.C AI verified (verdict: ______)
[ ] EXE-P0.2.C approved
[ ] EXE-P0.2.D executed
[ ] EXE-P0.2.D AI verified (verdict: ______)
[ ] EXE-P0.2.D approved
[ ] EXE-P0.2.E executed
[ ] EXE-P0.2.E AI verified (verdict: ______)
[ ] EXE-P0.2.E approved
[ ] EXE-P0.2.F executed
[ ] EXE-P0.2.F AI verified (verdict: ______)
[ ] EXE-P0.2.F approved
[ ] EXE-P0.2.G executed
[ ] EXE-P0.2.G AI verified (verdict: ______)
[ ] EXE-P0.2.G approved
[ ] EXE-P0.2.H executed
[ ] EXE-P0.2.H AI verified (verdict: ______)
[ ] EXE-P0.2.H approved
[ ] Capability Gate P0.2 AI verified (PASS)
[ ] Capability Gate P0.2 approved
```

## Capability 3 — Sanitizer

```text
[ ] EXE-P0.3.A executed
[ ] EXE-P0.3.A AI verified (verdict: ______)
[ ] EXE-P0.3.A approved
[ ] EXE-P0.3.B executed
[ ] EXE-P0.3.B AI verified (verdict: ______)
[ ] EXE-P0.3.B approved
[ ] EXE-P0.3.C executed
[ ] EXE-P0.3.C AI verified (verdict: ______)
[ ] EXE-P0.3.C approved
[ ] EXE-P0.3.D executed
[ ] EXE-P0.3.D AI verified (verdict: ______)
[ ] EXE-P0.3.D approved
[ ] EXE-P0.3.E executed
[ ] EXE-P0.3.E AI verified (verdict: ______)
[ ] EXE-P0.3.E approved
[ ] EXE-P0.3.F executed
[ ] EXE-P0.3.F AI verified (verdict: ______)
[ ] EXE-P0.3.F approved
[ ] EXE-P0.3.G executed
[ ] EXE-P0.3.G AI verified (verdict: ______)
[ ] EXE-P0.3.G approved
[ ] EXE-P0.3.H executed
[ ] EXE-P0.3.H AI verified (verdict: ______)
[ ] EXE-P0.3.H approved
[ ] Capability Gate P0.3 AI verified (PASS)
[ ] Capability Gate P0.3 approved
```

## Capability 4 — Context Policy

```text
[ ] EXE-P0.4.A executed
[ ] EXE-P0.4.A AI verified (verdict: ______)
[ ] EXE-P0.4.A approved
[ ] EXE-P0.4.B executed
[ ] EXE-P0.4.B AI verified (verdict: ______)
[ ] EXE-P0.4.B approved
[ ] EXE-P0.4.C executed
[ ] EXE-P0.4.C AI verified (verdict: ______)
[ ] EXE-P0.4.C approved
[ ] EXE-P0.4.D executed
[ ] EXE-P0.4.D AI verified (verdict: ______)
[ ] EXE-P0.4.D approved
[ ] EXE-P0.4.E executed
[ ] EXE-P0.4.E AI verified (verdict: ______)
[ ] EXE-P0.4.E approved
[ ] EXE-P0.4.F executed
[ ] EXE-P0.4.F AI verified (verdict: ______)
[ ] EXE-P0.4.F approved
[ ] EXE-P0.4.G executed
[ ] EXE-P0.4.G AI verified (verdict: ______)
[ ] EXE-P0.4.G approved
[ ] EXE-P0.4.H executed
[ ] EXE-P0.4.H AI verified (verdict: ______)
[ ] EXE-P0.4.H approved
[ ] Capability Gate P0.4 AI verified (PASS)
[ ] Capability Gate P0.4 approved
```

## Capability 5 — Prompt Assembler

```text
[ ] EXE-P0.5.A executed
[ ] EXE-P0.5.A AI verified (verdict: ______)
[ ] EXE-P0.5.A approved
[ ] EXE-P0.5.B executed
[ ] EXE-P0.5.B AI verified (verdict: ______)
[ ] EXE-P0.5.B approved
[ ] EXE-P0.5.C executed
[ ] EXE-P0.5.C AI verified (verdict: ______)
[ ] EXE-P0.5.C approved
[ ] EXE-P0.5.D executed
[ ] EXE-P0.5.D AI verified (verdict: ______)
[ ] EXE-P0.5.D approved
[ ] EXE-P0.5.E executed
[ ] EXE-P0.5.E AI verified (verdict: ______)
[ ] EXE-P0.5.E approved
[ ] EXE-P0.5.F executed
[ ] EXE-P0.5.F AI verified (verdict: ______)
[ ] EXE-P0.5.F approved
[ ] EXE-P0.5.G executed
[ ] EXE-P0.5.G AI verified (verdict: ______)
[ ] EXE-P0.5.G approved
[ ] EXE-P0.5.H executed
[ ] EXE-P0.5.H AI verified (verdict: ______)
[ ] EXE-P0.5.H approved
[ ] Capability Gate P0.5 AI verified (PASS)
[ ] Capability Gate P0.5 approved
```

## Capability 6 — Output Controls

```text
[ ] EXE-P0.6.A executed
[ ] EXE-P0.6.A AI verified (verdict: ______)
[ ] EXE-P0.6.A approved
[ ] EXE-P0.6.B executed
[ ] EXE-P0.6.B AI verified (verdict: ______)
[ ] EXE-P0.6.B approved
[ ] EXE-P0.6.C executed
[ ] EXE-P0.6.C AI verified (verdict: ______)
[ ] EXE-P0.6.C approved
[ ] EXE-P0.6.D executed
[ ] EXE-P0.6.D AI verified (verdict: ______)
[ ] EXE-P0.6.D approved
[ ] EXE-P0.6.E executed
[ ] EXE-P0.6.E AI verified (verdict: ______)
[ ] EXE-P0.6.E approved
[ ] EXE-P0.6.F executed
[ ] EXE-P0.6.F AI verified (verdict: ______)
[ ] EXE-P0.6.F approved
[ ] EXE-P0.6.G executed
[ ] EXE-P0.6.G AI verified (verdict: ______)
[ ] EXE-P0.6.G approved
[ ] EXE-P0.6.H executed
[ ] EXE-P0.6.H AI verified (verdict: ______)
[ ] EXE-P0.6.H approved
[ ] Capability Gate P0.6 AI verified (PASS)
[ ] Capability Gate P0.6 approved
```

---

# 17. Final P0 Boundary

Completion of:

```text
Approve CAPABILITY-GATE P0.6
```

means the six P0 capabilities in this execution slice have passed their defined Capability Gates.

It does **not** by itself mean:

- production ready
- enterprise-scale ready
- benchmark validated for optimization savings
- P1 ready
- P2 ready
- P3 ready
- P4 ready
- P5 ready
- any ADR accepted
- final infrastructure selected

The readiness boundary remains the one defined by `docs/execution-plan.md`.

---

# 18. First Command to Execute

For a brand-new run of this plan from scratch, the first command is:

```text
Execute EXE-P0.1.A
```

**For this repository, the next command is not `EXE-P0.1.A`** — do not restart it: Capability 1 is closed (Gate P0.1 approved 2026-09-23). `EXE-P0.2.B` is done (executed, AI-verified `PASS`, approved — `a2d022f`, 2026-09-27; 92/92 tests passing). The next command — sent only when you choose to proceed — is:

```text
Execute EXE-P0.2.C
```

*(Historical, 2026-09-27 pre-`a2d022f`: the next command was `Execute EXE-P0.2.B`, shown in the block below; it has since been executed and approved — `a2d022f` — and must not be sent again.)*

```text
Execute EXE-P0.2.B
```

*(Historical, v1.0.11 pre-2026-09-27: the next command was `Execute REM-P0.2.B-02`, shown in the block below; it has since been executed and approved — `bf74ac3` — and must not be sent again.)*

```text
Execute REM-P0.2.B-02
```

*(Historical, v1.0.10: the next command was `Execute REM-P0.2.B-01`, shown in the block below; it has since been executed and approved — `e4cf1f5` — and must not be sent again.)*

```text
Execute REM-P0.2.B-01
```

*(Historical: at v1.0.8/v1.0.9 this section said no unit was executable and named a source-contract correction, then `Execute EXE-P0.2.B`.)*

*(Historical: at v1.0.7 this section named `Execute REM-P0.1.B-02`, since executed and approved — `c378dc9`.)*

See §20 for the full remaining sequence.

Do not start with a broad instruction such as "implement P0".

---

# 19. Quick Reference

```text
START (Environment Readiness passed)
  ↓
Execute EXE-P0.1.A
  ↓
Claude Code: AI Verification → AI VERIFIED — AWAITING HUMAN APPROVAL
  ↓
Review Claude Code report + evidence
  ↓
Approve EXE-P0.1.A
  ↓
Execute EXE-P0.1.B
  ↓
Claude Code: AI Verification
  ↓
Review
  ↓
Approve EXE-P0.1.B
  ↓
... repeat A–H (N/A units: AI VERIFICATION: NOT APPLICABLE, no commit, still approve)
  ↓
Approve EXE-P0.1.H
  ↓
Capability Gate P0.1 review → AI CAPABILITY GATE VERIFICATION: PASS
  ↓
Approve CAPABILITY-GATE P0.1
  ↓
Execute EXE-P0.2.A
  ↓
... repeat
  ↓
Approve CAPABILITY-GATE P0.6
  ↓
P0 execution sequence complete

Any AI VERIFICATION BLOCKED → no approval → remediation → re-Execute
```

**Total:** 48 atomic execution units + 6 Capability Gates = 54 gated checkpoints (remediation units `REM-P0.1.A-01`/`-02`, `REM-P0.1.B-01`/`-02`, `REM-P0.2.B-01`, `REM-P0.2.B-02` and `REM-P0.2.B-03` are extra and not counted).

---

# 20. Current Position (governing baseline: `execution-plan.md` v1.0.12)

Taken from the git history and `execution-plan.md` §18.14 / §18.18 (v1.0.12). Check `git log --oneline` for anything newer. Where this runbook and the plan differ, the plan wins.

```text
Position updated after EXE-P0.2.H (2026-10-05) — supersedes the "after EXE-P0.2.B" block below.
Documentation-only reconciliation under human decisions HD-CG-P0.2-01 to -05. No approval is recorded
here; the Capability Gate is not approved.
EXE-P0.2.C:               COMMITTED dd1e4c2 (Integration). Executed under the authorized path opened by
                          `Execute EXE-P0.2.C` (plan §18.20). Formal §18.12.3 revalidation record: NOT YET
                          PRODUCED — no continuation state is recorded for C as formally verified.
                          Not HUMAN APPROVED.
EXE-P0.2.D:               COMMITTED 5a576c5 (original unit commit), plus f75d8f7 — a documented
                          post-boundary corrective commit, NOT a second unit. Recorded as a governance
                          deviation (one-commit-per-changing-unit rule, §18.2/§18.20.L) under
                          HD-CG-P0.2-01; both commits preserved, history not rewritten.
                          **B-R1 (blocking, from f75d8f7's Reviewer FAIL) — CORRECTED under HD-CG-P0.2-06**
                          by `0a3a2f7`, a post-boundary corrective commit (NOT a unit commit; not ordinary
                          §18.20.L compliance). Post-read mapping failures now emit the one WARN line and
                          rethrow the same exception. Reviewer on the corrective diff: PASS WITH REQUIRED
                          FOLLOW-UP — no blocking finding. D's commit history: 5a576c5 (unit), f75d8f7
                          (post-boundary), 0a3a2f7 (post-boundary, HD-CG-P0.2-06). 103/103 tests passing.
                          **OPEN HUMAN DECISION (HDR, stops the path):** CostLedgerEntry accepts a
                          `verified=true` entry with null `currency`, which HD-4 does not permit. Options:
                          (a) reject null currency at ledger write (a Capability 1 scope change, separate
                          unit); (b) accept as a documented contract gap. Until decided, D approval and the
                          Gate cannot proceed.
EXE-P0.2.E:               COMMITTED 86c5964 (Security/Governance). Tenant-scoping realization accepted by
                          HD-CG-P0.2-03; the broader authorization-equivalence requirement is NOT
                          implemented by P0 (documented non-blocking limitation for Capability 2).
EXE-P0.2.F:               COMMITTED 8a840ab — design-note realization (not N/A).
EXE-P0.2.G:               NO COMMIT. AI VERIFICATION: NOT APPLICABLE (HD-CG-P0.2-02, reason: NO DEDICATED
                          SCENARIO for harness construction; P1+ scenarios validate future uses).
                          Continuation state: AI VERIFIED — CONTINUING (NOT APPLICABLE). Not HUMAN APPROVED.
EXE-P0.2.H:               COMMITTED 8c5e0db (Failure/Recovery). Formal revalidation record: NOT YET PRODUCED.
Capability Gate P0.2:     BLOCKED — not approved. The reconciliation stopped at the D blocker (B-R1) above.
                          Gate AI Verification must be re-run only after B-R1 is resolved by a human decision.
                          `Approve CAPABILITY-GATE P0.2` has not been issued and must not be issued while
                          this blocker stands.
Capability 3:             NOT STARTED.
SOURCE-GAP-EVAL-01, SOURCE-GAP-IRG-02: OPEN (unchanged). Live Path A measurement: NOT DEMONSTRATED (deferred §18.10).

Position updated after EXE-P0.2.B (2026-09-27) — superseded by the block above:
REM-P0.2.B-02:            DONE + AI VERIFIED (PASS) + HUMAN APPROVED (bf74ac3; approved 2026-09-27)
REM-P0.2.B-03:            REGISTERED / NOT EXECUTED — NOT a prerequisite
EXE-P0.2.B:               DONE + AI VERIFIED (PASS) + HUMAN APPROVED (a2d022f; approved 2026-09-27).
                          92/92 tests passing. Reviewed by Architect (APPROVED FOR IMPLEMENTATION),
                          Reviewer (PASS WITH REQUIRED FOLLOW-UP — one LOW finding, fixed in the same
                          commit) and Verifier (CONFORMANT WITH DOCUMENTED GAPS). Disclosed, not
                          concealed: the Architect/Reviewer/Verifier routing ran after implementation
                          rather than before, a documented deviation from the normal sequence.
                          Contract-level, fixture-based; live Path A end-to-end baseline measurement:
                          NOT DEMONSTRATED — explicitly deferred (Gate P0.2, §18.10).
EXE-P0.2.C:               NOT EXECUTED, NOT AUTHORIZED. Approve EXE-P0.2.B covers only that one
                          sub-phase; it does not chain to this one.
Capability Gate P0.2:     NOT AUTHORIZED. Not reachable until Sub-phases C-H are each separately
                          executed and approved.
SOURCE-GAP-EVAL-01, SOURCE-GAP-IRG-02: OPEN (unchanged)
```

```text
NEXT STEP (sent only when you choose to proceed)

Execute EXE-P0.2.C
```

*(The "v1.0.11 position, updated after REM-P0.2.B-02" block that previously stood here — EXE-P0.2.B STILL NOT YET EXECUTABLE, next step "Execute EXE-P0.2.B" — is now historical: EXE-P0.2.B is done. The status block, the "NEXT REQUIRED STEP" block and the "Do **not** restart ..." paragraph below are the v1.0.10 position, retained as history; the position above governs.)*

```text
EXE-P0.1.A–H:
Historical execution completed as previously recorded,
with F = NOT APPLICABLE (acknowledged and approved) and no commit.
EXE-P0.1.B reconciled: GAP FOUND (CONTRA-EXECPLAN-01 — AC-005 / INTF-047).

REM-P0.1.A-01 (+ Amendment 1):
DONE + APPROVED (c05e3e7, 93c1ba3)

REM-P0.1.A-02:
DONE + APPROVED (5e12148)

REM-P0.1.B-01:
DONE + AI VERIFIED + HUMAN APPROVED (34dd1f1)

HD-1 through HD-4:
DECIDED (by the operator in REM-P0.1.B-01)

Source-contract promotion (DB-2, execution-plan v1.0.7):
COMMITTED (197da0a)

REM-P0.1.B-02:
DONE + AI VERIFIED + HUMAN APPROVED (c378dc9)

EXE-P0.1.G / EXE-P0.1.H approvals:
APPROVED (2026-09-23)

Capability Gate P0.1:
AI GATE VERIFICATION PASS + APPROVED (2026-09-23) — Capability 1 CLOSED

EXE-P0.2.A:
EXECUTED + APPROVED

REM-P0.2.B-01 (v1.0.10, §18.14.7):
REGISTERED / NOT STARTED

SOURCE-GAP-EXECPLAN-07 / -08:
OPEN

EXE-P0.2.B:
BLOCKED — §18.15 guard passes; SOURCE-GAP-EXECPLAN-07/-08 open (§18.14.6).
Not executed; no code.
```

```text
NEXT REQUIRED STEP

First: commit the v1.0.10 correction (registers REM-P0.2.B-01).
Then: Execute REM-P0.2.B-01
```

Do **not** restart `EXE-P0.1.A` or `EXE-P0.1.B`. Do **not** send `Execute REM-P0.2.B-DESIGN-01`, which is not a canonical ID. Do **not** re-send `Execute EXE-P0.2.B` until `REM-P0.2.B-01` is approved **and** a source-contract correction closing `-07`/`-08` is committed.

Detail per unit:

| Unit | Commit | Status |
|---|---|---|
| `EXE-P0.1.A` | `a345742` | Executed + approved (v1.0.3). Reconciled **GAP FOUND** (shared `core/`), closed by the REM-A units |
| `EXE-P0.1.B` | `e353dcf` | Executed + approved (v1.0.3). Reconciled **GAP FOUND** (AC-005 / `INTF-047`, `CONTRA-EXECPLAN-01`); closed by the REM-B units |
| `EXE-P0.1.C` | `5d8640e` | Executed + approved (v1.0.3) |
| `EXE-P0.1.D` | `e1730ce` | Executed + approved (v1.0.3) |
| `EXE-P0.1.E` | `3987957` | Executed + approved (v1.0.3) |
| `EXE-P0.1.F` | — (none, correct) | `NOT APPLICABLE` — acknowledged and approved |
| `EXE-P0.1.G` | `95a1267` | Executed (v1.0.3). Approved 2026-09-23 |
| `EXE-P0.1.H` | `0c18174` | Executed (v1.0.3). Approved 2026-09-23 |
| `REM-P0.1.A-01` | `c05e3e7`, `93c1ba3` | Done + approved (design + Amendment 1) |
| `REM-P0.1.A-02` | `5e12148` | Done + approved (shared `core/` types) |
| `REM-P0.1.B-01` | `34dd1f1` | Done + AI verified + human approved (Contract / Design Reconciliation; HD-1 to HD-4) |
| DB-2 (plan v1.0.7) | `197da0a` | HD-1 to HD-4 promoted into `interfaces.md` §28.1 / `conventions.md` §14.1 |
| `REM-P0.1.B-02` | `c378dc9` | Done + AI verified + human approved (Minimal Implementation Remediation) |
| Capability Gate P0.1 | — (never a commit) | AI PASS + **approved** 2026-09-23 — Capability 1 closed |
| `EXE-P0.2.A` | `0e92261` | Executed + approved. Reconciled: **PASS** |
| `EXE-P0.2.B` | `a2d022f` | Done + AI verified (`PASS`) + human approved (2026-09-27; 92/92 tests). Architect/Reviewer/Verifier routed after implementation, not before — disclosed deviation, no required design change |
| `8b68239` | — | Pre-decision record `EvaluationMeasurementReconciliation.md` (`docs:` commit) — not unit evidence, not approved |
| `REM-P0.2.B-01` | `e4cf1f5` | Done + AI verified + human approved (2026-09-24; plan §18.14.7, §18.14.8) |
| `REM-P0.2.B-02` | `bf74ac3` | Done + AI verified (`PASS`) + human approved (2026-09-27; plan §18.14.8) |
| `REM-P0.2.B-03` | — | **Registered / not executed** (plan v1.0.11 §18.14.8) — not a prerequisite |
| `EXE-P0.2.C` | — | **Not executed, not authorized** — `Approve EXE-P0.2.B` covers only that sub-phase |
| `c3d6ecf`, `a764689` | — | Not implementation commits (`CLAUDE.md` / `.vscode/`) — not counted |

**Remaining sequence up to `EXE-P0.2.B` (v1.0.11, updated after `REM-P0.2.B-02`)** — one line at a time, with Claude Code stopping after every step:

```text
DONE: v1.0.11 correction committed (196e112)
DONE: Execute REM-P0.2.B-02 / Approve REM-P0.2.B-02 (bf74ac3, approved 2026-09-27)

Execute EXE-P0.2.B
Approve EXE-P0.2.B

(optional, when directed) Execute REM-P0.2.B-03 / Approve REM-P0.2.B-03
```

*(Historical v1.0.10 sequence, retained below: it ran `REM-P0.2.B-01` and then a source-contract correction.)*

```text
(needed) commit the v1.0.10 correction

Execute REM-P0.2.B-01
Approve REM-P0.2.B-01

(needed) user-directed source-contract correction promoting your REM-P0.2.B-01 decisions,
         closing SOURCE-GAP-EXECPLAN-07/-08

Execute EXE-P0.2.B
Approve EXE-P0.2.B
```

*(Historical v1.0.8/v1.0.9 sequence: a source-contract correction, then `Execute EXE-P0.2.B`, with no unit registered.)*

*(Historical v1.0.7 sequence — `REM-P0.1.B-02`, the G/H approvals and the Capability 1 Gate — is complete: all issued and approved on 2026-09-23.)*

After `EXE-P0.2.B`, continue with §8's Capability 2 steps from `Execute EXE-P0.2.C`.

---

### Authorized Path State (plan §18.20.N — Continuous Phase-Driven Execution, effective from `EXE-P0.2.C` onward)

Plan §18.20.N names this runbook's §20 as the place a deferred authorized-path field would eventually
live, once a separate correction pass added it. This subsection is that pass. It supplements, and never
replaces, the "Current Position" block above — where the two disagree, `git log` and the most recently
synced block above win, and the disagreement is a `DOCUMENTATION-GAP` (plan §18.20.N; the term itself is
defined at `.claude/skills/eaioc-execution-governance/SKILL.md` §10, "Blocker detection"), never resolved
by guessing.

**This field is not:**

- a second state ledger (plan §18.20.N, HD-CE-12) — it never substitutes for git evidence; a
  disagreement between this field and git history is resolved in git's favor and reported as a
  `DOCUMENTATION-GAP`;
- a new authorization mechanism — nothing recorded here ever itself authorizes a unit; only the
  operator's own explicit `Execute <id>` does;
- an implicit approval record — an entry here for a `CONTINUING` / `CONTINUING (NOT APPLICABLE)` unit
  is never read as that unit's own `Approve`;
- a source of authorization for any unit or Capability outside the path it describes.

**A hard prerequisite is not automatically path-internal (plan §18.20.I).** A remediation or prerequisite
unit being a *hard prerequisite* of a path-internal unit (in the sense already used at §18.15, §18.14.8)
never by itself makes that prerequisite eligible to auto-run inside an open path — only the live plan's
own explicit designation of that exact unit as path-internal for that exact path does. As of this
writing no currently registered remediation is plan-designated path-internal; `REM-P0.2.B-03` in
particular is Optional (§18.20.J, HD-CE-09) and is never auto-run regardless of any path being open.

**Template — the eight facts plan §18.20.N requires, kept current only while a path is actually open:**

```text
AUTHORIZED PATH STATE (plan §18.20.N)
Capability:                    <n>
Authorized starting unit:      EXE-P0.<n>.<letter>      (the unit named in the triggering Execute)
Authorized path boundary:      Capability Gate P0.<n>  |  EXE-P0.<n>.<letter> (if stopped short of the Gate)
Completed units:
  EXE-P0.<n>.<letter> — HUMAN APPROVED                             (approved before this path opened)
  EXE-P0.<n>.<letter> — AI VERIFIED — CONTINUING                    (committed, PASS-class, path-internal)
  EXE-P0.<n>.<letter> — AI VERIFIED — CONTINUING (NOT APPLICABLE)   (no commit, N/A, path-internal)
Current stopped unit:          <id> | none (path complete or not yet opened)
Stop reason:                   <cite the exact §18.20.K condition> | none
HDR resolution status:         OPEN | RESOLVED (date, decision reference) | not applicable
Continuation-authorized:       true | false
```

**Terminology — three states, never conflated:**

| State | Meaning | Commit? | Individual `Approve`? | Is it `HUMAN APPROVED`? |
|---|---|---|---|---|
| `HUMAN APPROVED` | The historical, per-unit model — the operator's own explicit `Approve <id>` | (per the unit) | Yes — this *is* the approval | Yes |
| `AI VERIFIED — CONTINUING` | Path-internal, PASS-class unit; continuation permitted only because it lies inside the path an earlier `Execute` opened (plan §18.20.F) | Yes | No — none is requested | **No — never** |
| `AI VERIFIED — CONTINUING (NOT APPLICABLE)` | Path-internal, `NOT APPLICABLE` unit (plan §18.20.R) | **No — never** | No — none is requested | **No — never** |

Both continuation states authorize only the automatic move to the next path-internal unit of the *same*
Capability; neither authorizes anything outside that path, and neither is ever read as, or reported as,
`HUMAN APPROVED` anywhere in this runbook.

**Resume semantics after a Human Decision Request (plan §18.20.H).** When a path stops on an HDR or
blocker, this field's "Current stopped unit" / "Stop reason" / "HDR resolution status" rows are the
record of that stop. Once the operator resolves it:

1. Reconstruct state from this field, git, and the live plan.
2. Reconcile the resolution against the authoritative documents — a decision existing is not proof it
   was correctly promoted into a source document.
3. Confirm `Continuation-authorized` remains true **for this same Capability and boundary only** — an
   HDR resolution never expands the path (plan §18.20.H).
4. If the same path remains valid, execution resumes automatically at the stopped-at unit — **no fresh
   `Execute` is required** solely because the HDR was resolved.
5. If reconciliation surfaces a new HDR or blocker, the path stops again and this field is updated again.
6. If reconstruction is ambiguous (this field, git and the plan do not agree, or a fact is missing), that
   is a `DOCUMENTATION-GAP` — no automatic resume; the operator sends a fresh `Execute <id>` naming where
   to resume, which itself validly re-opens the path from that unit (plan §18.20.B).

**An explicit operator `STOP` ends the open path's automatic-continuation privilege immediately** —
resuming afterward always requires a fresh, explicit `Execute <id>`, never an automatic resume, regardless
of what this field otherwise records.

**Boundary approval (plan §18.20.G).** When the path reaches its boundary — ordinarily the Capability
Gate, or an earlier unit if the path is closed out without reaching the Gate — the resulting
`Approve EXE-P0.n.X` or `Approve CAPABILITY-GATE P0.<n>` request must **enumerate every path-internal
unit this field lists as completed since the path opened or last resumed, naming which of the two
continuation states each one reached.** This enumeration is never an implicit "everything up to here,"
and naming an N/A unit in it is never that unit's own individual approval — the boundary approval is one
act covering the whole named set. At the Capability Gate specifically: the Gate review remains its own
separate step (`Review CAPABILITY-GATE P0.<n>`, unchanged); Gate approval remains entirely
human-controlled; Gate approval may serve as the path's own boundary approval when the path runs through
H; and — absolutely, with no exception — **Gate approval for Capability N never starts Capability N+1.**
Capability N+1's Sub-phase A always needs its own separate, explicit `Execute EXE-P0.(N+1).A`.

**Historical compatibility (plan §18.20.O).** `EXE-P0.2.A` and `EXE-P0.2.B` were executed and approved
under the prior, per-unit model, before this continuous model existed. They are never re-run, never
re-verified under this procedure, and never relabeled as `CONTINUING` or `CONTINUING (NOT APPLICABLE)` —
their `HUMAN APPROVED` record in the "Current Position" block and the "Detail per unit" table above
stays exactly as it is. The continuous model this subsection documents is effective only from
`EXE-P0.2.C` onward (plan §18.20.O); as of this writing `EXE-P0.2.C` is **not executed, not approved,
not authorized**, so no path is currently open and every field in the template above is currently
empty / `none` / `false`.

---

### Capability Gate P0.2 — Reconciliation Record (2026-10-05; supersedes the statement above that `EXE-P0.2.C` is not executed)

This record is documentation only. It does not approve anything. It records the state of Capability 2 after the human decisions HD-CG-P0.2-01 to -07. Where it disagrees with an older block, this record governs; git history governs over both.

**Human decisions in force**

- **HD-CG-P0.2-01:** `f75d8f7` is accepted as a documented post-boundary corrective change to `EXE-P0.2.D`. It is not presented as compliance with the one-commit-per-changing-unit rule (§18.2, §18.20.L). Documented governance deviation.
- **HD-CG-P0.2-02:** `EXE-P0.2.G` is NOT APPLICABLE for the P0 harness-construction scope. Reason: the Capability 2 G row records NO DEDICATED SCENARIO for harness construction itself. Dedicated scenarios validate future P1+ uses, not construction.
- **HD-CG-P0.2-03:** the existing tenant-scoping implementation is accepted as the P0 realization of the tenant-isolation portion of `EXE-P0.2.E`. The broader authorization-equivalence requirement for the live request path is NOT implemented by P0. It is a documented non-blocking limitation for Capability 2. The enterprise authorization layer remains deferred to its authoritative security/authorization scope.
- **HD-CG-P0.2-04:** the boundary-approval scope is exactly `EXE-P0.2.C` through `EXE-P0.2.H`.
- **HD-CG-P0.2-05:** documentation reconciliation only. No implementation change is made under it.
- **HD-CG-P0.2-06:** corrective change for blocking Reviewer finding B-R1, realized by `0a3a2f7`.
- **HD-CG-P0.2-07:** the null-currency condition is accepted as a documented Capability 1 implementation and enforcement gap, for later separately authorized remediation. HD-4 is unchanged: `currency` remains non-nullable, and `verified=true` with `currency=null` remains CONTRACT-INVALID. This decision does not make `currency` nullable, does not authorize a Capability 1 code change, and does not create a remediation unit.

**Commit history of `EXE-P0.2.D` (preserved; no history rewritten)**

| Commit | Role |
|---|---|
| `5a576c5` | Original `EXE-P0.2.D` unit commit |
| `f75d8f7` | Post-boundary corrective commit: fix for the Gate's B-1 (read-failure logging) |
| `0a3a2f7` | Post-boundary corrective commit under HD-CG-P0.2-06: fix for B-R1 (mapping-failure logging) |

Documented governance deviation: `D` has three commits where the one-commit-per-changing-unit rule expects one. Recording it as a deviation does not make it compliant.

**Formal AI Verification reports (§18.12.3), produced 2026-10-05 against current evidence**

Each report is a reconciliation record. Where a unit's routing ran after implementation or was not run at all, the report says so. None of these verdicts is a human approval.

*EXE-P0.2.C — commit `dd1e4c2`*
1. Scope: integration test with a stub caller, `control_plane/benchmarking/` (test sources only).
2. Source: `docs/execution-plan.md` §18.5 C row.
3. Requirement: "Integration test with a stub caller"; DoD: public API stable for technique gates to call.
4. Architecture: no `src/main` change; wiring via `AnnotationConfigApplicationContext` with explicit registration.
5. Tests: `BaselineRunnerIntegrationTest` (1 test), passing in the full suite (103/103).
6. Failure path: N/A per the row.
7. Git: `dd1e4c2` contains only the two test files.
8. Prerequisites: `EXE-P0.2.B` approved (`a2d022f`).
9. Source gaps: none new. The Verifier's Javadoc overstatement finding was fixed before commit.
10. Execution assistance: Architect (pre-implementation, APPROVED WITH CONDITIONS, earlier session context); Reviewer PASS (earlier context); Verifier CONFORMANT WITH DOCUMENTED GAPS (2026-10-05).
- **Verdict: PASS WITH DOCUMENTED NON-BLOCKING GAP.** The integration test does not exercise component scanning.
- **Continuation state: `### AI VERIFIED — CONTINUING`.** Not `HUMAN APPROVED`.

*EXE-P0.2.D — commits `5a576c5`, `f75d8f7`, `0a3a2f7`*
1. Scope: structured log output for `run_baseline()`, `benchmarking/` (`BaselineObservability` plus `BaselineRunner` changes).
2. Source: `docs/execution-plan.md` §18.5 D row ("every `run_baseline()` call emits one structured log line").
3. Requirement: one line per call, INFO on success, WARNING on failure, same exception rethrown.
4. Architecture: mirrors `LedgerObservability`; no public API change; no tenant leakage in the log line.
5. Tests: `BaselineObservabilityTest` (5 tests, including read-failure, mapping-failure and success/failure cases); full suite 103/103.
6. Failure path: covered by read-failure and mapping-failure tests (`isSameAs` identity on the read failure).
7. Git: three commits, see the table above. The documented deviation is preserved.
8. Prerequisites: `EXE-P0.2.B`, `REM-P0.2.B-02`.
9. Source gaps: `SOURCE-GAP-EXECPLAN-01` (interim sink, referenced, unchanged); HD-07 null-currency gap (Capability 1, documented non-blocking).
10. Execution assistance: Reviewer on `5a576c5` PASS WITH REQUIRED FOLLOW-UP (required changes applied); Reviewer on `f75d8f7` FAIL (B-R1); Reviewer on `0a3a2f7` PASS WITH REQUIRED FOLLOW-UP, no blocking finding. Gate Verifier on `f75d8f7` (B-1 resolved).
- **Verdict: PASS WITH DOCUMENTED NON-BLOCKING GAP.** Documented governance deviation (commit count) and a documented non-blocking contract-enforcement gap (HD-07).
- **Continuation state: `### AI VERIFIED — CONTINUING`**, recorded with the deviation. Not ordinary §18.20.L compliance.

*EXE-P0.2.E — commit `86c5964`*
1. Scope: tenant-scoping tests for the benchmark harness, `benchmarking/` (test only).
2. Source: `docs/execution-plan.md` §18.5 E row; plan line 77 deferral of identity and authorization to the enterprise scope.
3. Requirement: no cross-tenant consumption; no cross-tenant verified-entry reuse; tenant identity enforced through the harness path (HD-03 realization).
4. Architecture: no `src/main` change in this commit; tenant-first partitioning in `CostLedgerStore`.
5. Tests: `BaselineTenantScopingTest` (4 tests), passing in the full suite.
6. Failure path: cross-tenant read and consumption attempts fail closed (no record returned).
7. Git: `86c5964` contains only the test file.
8. Prerequisites: `EXE-P0.2.B`.
9. Source gaps: broader authorization-equivalence NOT implemented by P0 (HD-03; documented non-blocking limitation).
10. Execution assistance: no reviewer run (test-only unit, recorded in the commit trailer).
- **Verdict: PASS WITH DOCUMENTED NON-BLOCKING GAP.** The tenant-isolation realization is proven. The authorization-equivalence clause is not, and is deferred under HD-03.
- **Continuation state: `### AI VERIFIED — CONTINUING`.**

*EXE-P0.2.F — commit `8a840ab`*
1. Scope: design note `control_plane/evaluation/QualityValidationReadiness.md`, documentation only.
2. Source: `docs/execution-plan.md` §18.5 F row; `docs/quality-gates.md` §5.
3. Requirement: documented readiness to accept `QG-NNN` calls once P1 ships; design note cross-references §5.
4. Architecture: no code; no implementation claim.
5. Tests: N/A per the row (no code).
6. Failure path: N/A per the row.
7. Git: `8a840ab` contains only the design note.
8. Prerequisites: `EXE-P0.2.B`.
9. Source gaps: none new.
10. Execution assistance: the Gate Verifier confirmed the QG list matches `quality-gates.md` §5 (QG-001 to QG-010).
- **Verdict: PASS.** Design-note realization, not N/A (HD-CG-P0.2-04).
- **Continuation state: `### AI VERIFIED — CONTINUING`.**

*EXE-P0.2.G — NO COMMIT*
1. Scope: scenario validation for harness construction.
2. Source: `docs/execution-plan.md` §18.5 G row ("NO DEDICATED SCENARIO … individual scenarios validate specific uses once P1+ techniques exist").
3. Requirement: none defined by the row beyond the finding.
4. Architecture: none.
5. Tests: none.
6. Failure path: none.
7. Git: no commit, by design.
8. Prerequisites: none.
9. Source gaps: none new; the scenario-matrix search is the basis for the decision.
10. Execution assistance: none.
- **Verdict: AI VERIFICATION: NOT APPLICABLE.** Reason (HD-CG-P0.2-02): no dedicated scenario validates harness construction itself; dedicated scenarios validate future P1+ uses.
- **Continuation state: `### AI VERIFIED — CONTINUING (NOT APPLICABLE)`.** No commit exists or is required. Not `HUMAN APPROVED`.

*EXE-P0.2.H — commit `8c5e0db`*
1. Scope: failure and recovery tests for the harness, `benchmarking/` (test only).
2. Source: `docs/execution-plan.md` §18.5 H row (a harness failure never marks a technique validated).
3. Requirement: failure surfaces as an exception, never as a record.
4. Architecture: `runBaseline` never returns a record on any failure path (confirmed through the B-R1 corrective change).
5. Tests: `BaselineFailureRecoveryTest` (2 tests: read-failure propagation; no-valid-measurement fail-closed); full suite 103/103 on the final D behavior.
6. Failure path: covered by the two tests above, plus the mapping-failure test in `BaselineObservabilityTest` added under B-R1.
7. Git: `8c5e0db` contains only the test file.
8. Prerequisites: `EXE-P0.2.B`, `EXE-P0.2.D`.
9. Source gaps: none new.
10. Execution assistance: no reviewer run (test-only unit, recorded in the commit trailer); the Gate Verifier checked the failure-injection paths.
- **Verdict: PASS.** Revalidated against the final D behavior (`0a3a2f7`).
- **Continuation state: `### AI VERIFIED — CONTINUING`.**

**Capability Gate P0.2 status**

- Gate AI Verification: recorded below once run on the current evidence.
- Gate: NOT APPROVED by Claude Code. Human approval is required.
- Capability 3: NOT STARTED.
- Live Path A end-to-end baseline measurement: NOT DEMONSTRATED. Explicitly deferred (§18.10, §18.14.8).
- Open source gaps: `SOURCE-GAP-EVAL-01`, `SOURCE-GAP-IRG-02` (unchanged). Capability 1 null-currency contract-enforcement gap (HD-07, documented non-blocking).

**Final AI Capability Gate Verification (2026-10-05, HEAD `0a3a2f7`): `PASS WITH DOCUMENTED GAPS`. No blocking finding.** Test result: `Tests run: 103, Failures: 0, Errors: 0, Skipped: 0`, BUILD SUCCESS. Non-blocking items recorded for the human approver: (a) `CLAUDE.md` stale blocker text, corrected in the same pass; (b) HD-CG-P0.2-03 authorization-equivalence not implemented (documented limitation); (c) HD-CG-P0.2-07 null-currency gap (CostLedgerEntry accepts null currency; BaselineEvaluationRecord rejects it); (d) null-argument `run_baseline(null)` throws NullPointerException before any log line (caller-programming error, untested, human decision only if the strict reading is intended); (e) the D commit-count deviation (HD-CG-P0.2-01); (f) the HD-CG-P0.2-* decisions are recorded in this runbook only, not yet in `execution-plan.md`. This verdict is governance evidence, not a human approval.

**HUMAN APPROVAL RECORDED (2026-10-05): `Approve CAPABILITY-GATE P0.2` issued by the operator.** Boundary approval covers exactly `EXE-P0.2.C`, `D`, `E`, `F`, `G`, `H` with the continuation states recorded above: C `AI VERIFIED — CONTINUING`; D `AI VERIFIED — CONTINUING` (recorded with the post-boundary deviation, commits `5a576c5`, `f75d8f7`, `0a3a2f7`); E `AI VERIFIED — CONTINUING` (tenant-isolation realization, HD-03 limitation); F `AI VERIFIED — CONTINUING` (design note); G `AI VERIFIED — CONTINUING (NOT APPLICABLE)`, no commit; H `AI VERIFIED — CONTINUING`. Capability Gate P0.2 is APPROVED. Live Path A end-to-end baseline measurement remains NOT DEMONSTRATED (deferred §18.10, §18.14.8). Capability 3 is NOT STARTED and requires its own explicit `Execute EXE-P0.3.A`. Uncommitted documentation changes: `CLAUDE.md` and this runbook, with the operator's pre-existing edits.

---

### Capability 3 — position update (plan v1.1.2, 2026-10-05)

Documentation sync only. Plan v1.1.2 is the governing version (`docs/execution-plan.md` header and correction history).

- Human decisions in force: HD-CG-P0.3-01 (Option 3: T1.1 is fail-open RESTORE_ORIGINAL; CIS is a separate fail-closed component and is not part of EXE-P0.3), HD-CG-P0.3-02 (register `SOURCE-GAP-EXECPLAN-16` and `-17`), HD-CG-P0.3-03 (plan v1.1.2), HD-CG-P0.3-04 (no invented T1.1 types; B stays blocked until a source contract is approved).
- Plan correction commit: `73ae247` (documentation only, not a unit commit).
- `EXE-P0.3.A`: commit `e4519d7` (design note only). Formal verdict `AI VERIFICATION: PASS WITH DOCUMENTED NON-BLOCKING GAP`; continuation `### AI VERIFIED — CONTINUING`; not individually approved. The A-row checklist boxes below remain unticked on purpose, because human approval of A has not been given.
- [SUPERSEDED by the later block "Capability 3 — final pre-authorization reconciliation"; kept as history.] The Capability 3 path has stopped before `EXE-P0.3.B`. `EXE-P0.3.B` is BLOCKED on `SOURCE-GAP-EXECPLAN-16` (no approved counter-identity values for the T1.1 contract (the input, output and counting shape are now in `interfaces.md` §44; see the counter bullet there)).
- Capability 4 has NOT started. The Capability 3 Gate has NOT been approved.

**Capability 3 — source-contract state (plan v1.1.2; HD-CG-P0.3-05 to -08, 2026-10-05).** Documentation only.

- `interfaces.md` v1.3.0 adds §44, T1.1 Sanitizer Contract, `INTF-072` (§37.1 row and index row added). Input: `ControlPlaneRequest.user_input`. Output: `sanitized_input`, `token_count_pre`, `token_count_post`, `counter`. Counting: both counts use the same counter (HD-CG-P0.3-06).
- `optimization-catalog.md` `TECH-001` now cites `INTF-072` for T1.1. The earlier `INTF-001` citation is kept only as the `user_input` carrier.
- `SOURCE-GAP-EXECPLAN-17`: resolved at source. `SOURCE-GAP-EXECPLAN-16`: narrowed, still OPEN and BLOCKING for `EXE-P0.3.B`. The open item is the values of `counter_id` and `counter_version`, which no frozen source names.
- `EXE-P0.3.A` remains `AI VERIFIED — CONTINUING` (commit `e4519d7`). It is not individually approved.
- `EXE-P0.3.B` remains BLOCKED. Capability 4 has NOT started. The Capability 3 Gate has NOT been approved.

**Capability 3 — T1.1 contract state (plan v1.1.2 partial; HD-CG-P0.3-09 to -11, 2026-10-05).** Documentation only.

- Null `user_input` (HD-CG-P0.3-10): resolved at contract level as `SOURCE-GAP-EXECPLAN-18`. T1.1 is not invoked for null input; no conversion to empty text; no new error code.
- EC-014 `content_type` routing (HD-CG-P0.3-11): documented P0 applicability gap, NOT resolved. No content type is added to `user_input`.
- Counter identity (HD-CG-P0.3-09): NOT APPLIED. The concrete `counter_id` and `counter_version` values have not been supplied. `SOURCE-GAP-EXECPLAN-16` remains OPEN and BLOCKING for `EXE-P0.3.B`.
- `EXE-P0.3.A` remains `AI VERIFIED — CONTINUING` (commit `e4519d7`). Its design note carries a documentation-synchronization note; the original text is preserved.
- `EXE-P0.3.B` remains BLOCKED. Capability 4 has NOT started. The Capability 3 Gate has NOT been approved.

**Capability 3 — T1.1 contract reconciliation (plan v1.1.2; HD-CG-P0.3-09 to -11, reconciled 2026-10-05).** Documentation only. This block supersedes the earlier Capability 3 state blocks above it, which are kept as history.

- Counter (HD-CG-P0.3-09): applied. `counter_id` = `JTOKKIT_CL100K_BASE`, `counter_version` = `1.1.0`, the EAIOC P0 local estimator for T1.1 (basis `com.knuddels:jtokkit:1.1.0`, encoding `CL100K_BASE`). Not the provider billing tokenizer. Replaceable behind the T1.1 counting abstraction.
- Null `user_input` (HD-CG-P0.3-10): partially applied. `SOURCE-GAP-EXECPLAN-18` remains open for the numeric validation error code; the corpus defines only `OPT-1xxx` and no specific code. No code is invented.
- EC-014 `content_type` routing (HD-CG-P0.3-11): OPEN as `SOURCE-GAP-EXECPLAN-19`. Non-blocking unless a specific implementation step requires EC-014 routing.
- Generic RESTORE_ORIGINAL fallback metric: documented non-blocking gap `SOURCE-GAP-EXECPLAN-20`. No metric invented.
- `SOURCE-GAP-EXECPLAN-16`: resolved at contract level. `SOURCE-GAP-EXECPLAN-17`: resolved at source.
- `EXE-P0.3.A`: committed (`e4519d7`), `AI VERIFIED — CONTINUING`, not individually approved.
- `EXE-P0.3.B`: NOT YET EXECUTABLE. Not authorized by this reconciliation; it needs its own explicit `Execute EXE-P0.3.B` and its own AI verification.
- Capability 3 Gate: not approved. Capability 4: not started.

**Capability 3 — final pre-authorization reconciliation (plan v1.1.2; HD-CG-P0.3-12 to -15, 2026-10-05).** Documentation only. This block supersedes the earlier Capability 3 blocks above it, which are kept as history.

- `SOURCE-GAP-EXECPLAN-18` (null `user_input`, numeric validation code): NON-BLOCKING for `EXE-P0.3.B` (HD-CG-P0.3-12). Still OPEN for the broader request-validation contract. No numeric code is invented.
- `SOURCE-GAP-EXECPLAN-19` (EC-014 `content_type` routing): OPEN and OUT OF SCOPE for `EXE-P0.3.B` (HD-CG-P0.3-13).
- `SOURCE-GAP-EXECPLAN-20` (RESTORE_ORIGINAL recording): RESOLVED AT CONTRACT LEVEL FOR P0 (HD-CG-P0.3-15). The fallback is recorded as one structured WARNING with `event_type = SANITIZER_FALLBACK_RESTORE_ORIGINAL`.
- `EXE-P0.3.B` may add `com.knuddels:jtokkit:1.1.0` to the existing `control_plane` Maven declaration, and only that (HD-CG-P0.3-14).
- `EXE-P0.3.B`: NOT YET EXECUTABLE. Source-level blockers are closed for the D0 no-op realization (HD-CG-P0.3-16); `SOURCE-GAP-EXECPLAN-21` to `-26` are DEFERRED and do not block it. It still needs its own explicit `Execute EXE-P0.3.B` and its own AI verification. This reconciliation does not authorize execution.
- Capability 3 Gate: not approved. Capability 4: not started.

**Capability 3 — D0 no-op transform scope (HD-CG-P0.3-16, 2026-10-05).** Documentation only.
- P0 T1.1 realization: safe no-op normalization. `sanitized_input` equals `user_input` unchanged; `token_count_pre == token_count_post` with the approved local counter (`JTOKKIT_CL100K_BASE`, 1.1.0). No content reduction, and the 2–15% benefit is not claimed.
- Deferred operations, each registered: `SOURCE-GAP-EXECPLAN-21` (umbrella), `-22` (whitespace and formatting), `-23` (irrelevant formatting), `-24` (duplicated content), `-25` (UI noise), `-26` (Unicode normalization form). All OPEN, none implemented.
- `SOURCE-GAP-EXECPLAN-18` OPEN and non-blocking for B; `-19` OPEN and out of scope for B; `-20` resolved for P0 (structured WARNING).
- `EXE-P0.3.B`: NOT YET EXECUTABLE. Path-internal to the Capability 3 path (opened by `Execute EXE-P0.3.A`); resumes automatically under §18.20.H (HD-CG-P0.3-17 Q1), no fresh Execute required. This block does not authorize execution. Capability 3 Gate and Capability 4 are not approved or started.

**Capability 3 — HD-CG-P0.3-17 authorized-path state (2026-10-05).** Documentation only.
- Q1: same-path resume under §18.20.H. `EXE-P0.3.B` is path-internal to the Capability 3 path opened by `Execute EXE-P0.3.A`. No fresh `Execute EXE-P0.3.B` is required after the HDR is resolved and reconciliation succeeds. The path does not expand and does not cross the Capability 3 Gate.
- Q2: §18.20.K.2 stops the path only on an unresolved HDR or a BLOCKING source gap. `SOURCE-GAP-EXECPLAN-21` to `-26` are OPEN/DEFERRED and non-blocking for the D0 no-op realization.
- Q3: interfaces.md 1.3.0 is a recorded human decision. Q4: JTOKKIT_CL100K_BASE / 1.1.0 is the P0 local estimator only. Q5: stale version references outside T1.1 scope are a non-blocking documentation-synchronization gap; frozen conventions.md is not edited.
- Reviews: fresh Architect and Verifier verdicts on the current tree are required before the documentation commit and before any B execution. The previous BLOCKED verdict is superseded but not declared cleared until these fresh verdicts exist.

**Capability 3 — HD-CG-P0.3-18 counter-failure decision (2026-10-06).** Documentation only.
- T1.1 token-counter failure propagates: no partial `SanitizerOutput`, no fabricated, `0`, `null`, stale, estimated or fallback count, no fallback counter, no nullable or `verified` field, no new error code or schema, never mapped to RESTORE_ORIGINAL. Normalization-failure RESTORE_ORIGINAL is unchanged.
- `SOURCE-GAP-EXECPLAN-27`: RESOLVED BY HUMAN DECISION (contract level), recorded in `interfaces.md` §44.4.
- HD-CG-P0.3-19 (2026-10-06): HD-18 stays valid at the T1.1 contract layer. Pipeline handling of a failed optional T1.1 stage follows frozen EC-114 / `architecture.md` §30 / `conventions.md` §16.1–§16.2: discard the failed result, continue with the pre-stage representation, do not reject the request solely for T1.1 failure, log, exclude from savings. "Propagates" in HD-18 means no contract result, not request rejection. `SOURCE-GAP-EXECPLAN-28` RESOLVED BY HUMAN DECISION (layer/ownership clarification). EC-114 is not marked resolved or changed. Frozen sources are not edited.
- Stage ordering in this scope: T1.1 counter failure → no `SanitizerOutput` → EC-114 continuation with pre-stage representation.
- JTokkit: `com.knuddels:jtokkit:1.1.0` verified resolvable in this environment; no downgrade to 1.0.0.
- This decision does not execute or approve `EXE-P0.3.B`. Commit only after both fresh Architect and Verifier reviews are clean. Then `EXE-P0.3.B` may resume under §18.20.H with no fresh `Execute`, only if no §18.20.K condition exists. The Capability 3 Gate and Capability 4 are not approved or started.

**Capability 3 — authorized-path state at EXE-P0.3.C (2026-10-07). Documentation only.**
- Path: Capability 3, opened by `Execute EXE-P0.3.A`; continued by HD-CG-P0.3-17 / -19. No fresh `Execute` for B, C or later units inside this path.
- `EXE-P0.3.A` — `e4519d7` — AI VERIFIED — CONTINUING (design only).
- `EXE-P0.3.B` — `1ad6bf8` — AI VERIFIED — CONTINUING (Architect APPROVED WITH FOLLOW-UP, addressed; Reviewer PASS; Verifier CONFORMANT WITH DOCUMENTED GAPS). Post-B integrity check (2026-10-07): commit contents match the verified tree; `.vscode/settings.json` is not in the commit.
- `EXE-P0.3.C` — **HELD: HDR raised, no implementation, no commit.** Reason: the C row (plan §18.6) requires a stub downstream consumer and a "public API stable for Capability 5 to call". The corpus does not define the downstream consumer contract: `architecture.md` §12.2 gives the Prompt Assembler's responsibilities, but no interface in `interfaces.md` names it as a consumer of T1.1 output or defines what it receives from T1.1. Per the path brief, the stub's expectations cannot be invented. Decision needed: confirm that the consumer contract is the existing INTF-072 `SanitizerOutput` (read-only; the stub calls `Sanitizer.sanitize(ControlPlaneRequest)` from a separate package and asserts the documented result only (the C row's failure-path cell is N/A; counter and normalization failure are covered by EXE-P0.3.B's SanitizerTest, not by C)), or define a different contract. Until answered, C does not proceed.
- Not started: `EXE-P0.3.D`–`H`; Capability 3 Gate (not approved); Capability 4 (not started).
- **`EXE-P0.3.C` HDR RESOLVED (2026-10-07, human answer in session):** the stub downstream consumer consumes the existing INTF-072 `SanitizerOutput` as it stands. The stub calls the public `Sanitizer.sanitize(ControlPlaneRequest)` from a separate package and reads the documented result. No new contract, wrapper, public surface or Capability 5 work. Not an approval of C, and not a Gate approval. C proceeds under the same path, pending its own pre-flight, implementation and AI verification.
- **`EXE-P0.3.C` — COMMITTED `01ca90c` — AI VERIFIED — CONTINUING (test-only; Architect CONFORMANT, Verifier CONFORMANT WITH DOCUMENTED GAPS, Reviewer PASS WITH REQUIRED FOLLOW-UP → fixed, 121/121). Realized as "consumable through the public API, demonstrated by a test-tree stub downstream consumer"; not pipeline wiring.**
- **`EXE-P0.3.D` — COMMITTED `994b6f6` — AI VERIFIED — CONTINUING (per the in-session 2026-10-07 decision: one INFO `SANITIZER_COMPLETED` per successful call (local name); one WARNING only on normalization fallback; no T1.1 log on counter failure, a documented P0 exception). Architect CONFORMANT WITH FOLLOW-UP (F1 OptimizationSpan disclosure applied in-commit), Reviewer PASS, Verifier CONFORMANT WITH DOCUMENTED GAPS. Tests 127/127 on `mvn clean test` at commit time. Not a human approval.**
- **Plan D row corrected (uncommitted reconciliation):** the `tokens.*` names are cited to `architecture.md` §23 and §27.1 (not `observability.md`); the fields-only rule is a P0 decision; OptimizationSpan is registered as `SOURCE-GAP-EXECPLAN-29` (documented non-blocking P0 gap). Not a human approval.
- **`EXE-P0.3.D` — HDR RAISED (2026-10-07), path HELD.** Verifier (final, 2026-10-07): BLOCKED, scoped to the D observability contract. `architecture.md` §23 (Sanitization row, line ~1959) lists `tokens.raw_input`, `tokens.sanitized`, `tokens.removed_by_sanitizer` and `sanitizer.false_removal_rate` under "Observability Metrics". `optimization-catalog.md` TECH-001 repeats this. The D implementation logs them as fields only, following the in-session decision. Architecture is Tier 1 and outranks the plan, so the plan decision cannot settle the conflict. Human decision needed: (A) register it as a documented non-blocking P0 gap, keeping fields-only emission and leaving the frozen wording unchanged for now; (B) with explicit human authority, correct the frozen architecture §23 wording so these names are ledger fields, not metrics; (C) change D to emit them as metrics (new metric plumbing; out of D's row). Reviewer (final): PASS WITH REQUIRED FOLLOW-UP; its LOW items were applied in the working tree (test precision and INFERRED labelling; 127/127). Path remains open; E–H and the Capability 3 Gate are not started.

**Capability 3 — `EXE-P0.3.D` implementation commit and governance reconciliation (2026-10-07). Documentation only.**
- D implementation commit: **`994b6f6`** (`P0-3.D: Sanitizer — Observability`). Not rewritten, amended or reset.
- Subsequent docs/governance reconciliation required after final review: committed separately as a **human-authorized documentation follow-up** (human decision, in session, 2026-10-07). No D implementation was changed by this follow-up. Its scope is the D row in `docs/execution-plan.md`, `SOURCE-GAP-EXECPLAN-29` and `-30`, and this runbook block.
- **Deviation, not hidden:** the plan's one-commit-per-changing-unit rule was not followed for D. D has two commits: `994b6f6` (implementation) and the documentation follow-up. This does NOT establish a general permission for multiple implementation commits per execution unit.
- Semantics preserved (no change): success → one INFO `SANITIZER_COMPLETED` (local name; `tokens.raw_input`, `tokens.sanitized` as fields; no user text); normalization fallback → one WARNING `SANITIZER_FALLBACK_RESTORE_ORIGINAL`, no INFO; token-counter failure → no SanitizerOutput and no T1.1 log, accepted as a documented P0 exception, no counter-failure event invented.
- Gaps kept open: `SOURCE-GAP-EXECPLAN-29` (OptimizationSpan not implemented in P0; `span_id` is a per-line UUID, not a span); `SOURCE-GAP-EXECPLAN-30` (`architecture.md` §23 lists `tokens.*` as "Observability Metrics"; P0 realizes them as fields only, human decision option A). `architecture.md` §23 is frozen and unchanged.
- **Reviewer LOW items NOT applied (scope):** the Reviewer's final LOW items (INFERRED wording for the retained-WARNING attribution in `Sanitizer.java` and `SanitizerTest.java`; positive-control assertions in `removedBySanitizerIsNeverEmitted` and `counterFailureEmitsNoT11LinesAtAll`; rename of `sharedCounterGivesSameCountsUnderConcurrentUse`) were written to the working tree, then removed from it because this follow-up may not modify `Sanitizer.java` or `SanitizerTest.java`. They are preserved as a patch outside the repo (scratchpad `D-reviewer-low-followups.patch`) and remain OPEN, non-blocking, for a future separately authorized change.
- Verification (on the committed tree at `994b6f6` plus this docs follow-up): full suite 127/127 on `mvn clean test` (`SanitizerTest` 20, `SanitizerIntegrationTest` 4).
- `.vscode/settings.json` is an unrelated working-tree change and is excluded from all EAIOC commits.
- **`EXE-P0.3.D` — FINAL VERIFICATION COMPLETE; PATH HELD for human registry/plan decisions (2026-10-07).** Architect CONFORMANT WITH FOLLOW-UP; Reviewer PASS WITH REQUIRED FOLLOW-UP (LOW items open, non-blocking); Verifier CONFORMANT WITH DOCUMENTED GAPS. Governance findings requiring human escalation (§18.20.K governance-agent escalation), none blocking D's behaviour: (1) counter-failure silence is accepted by the human for P0, but `interfaces.md` §44.4 (EC-114 continuation) says the failure "is logged"; no SOURCE-GAP-EXECPLAN ID covers this silence yet (candidate `SOURCE-GAP-EXECPLAN-31`, not assigned); (2) the one-commit deviation is recorded only in this runbook and the commit message, not in `execution-plan.md` (lines ~218 and ~1961 still state the rule without an exception clause); (3) the plan version header reads 1.1.2 with no bump for the D-row reconciliation. None is decided here. `EXE-P0.3.E`–`H` and the Capability 3 Gate are NOT started.
- **`EXE-P0.3.D` governance HOLD RESOLVED (2026-10-07, human decisions, in session).** (1) `SOURCE-GAP-EXECPLAN-31` registered: documented non-blocking P0 gap (frozen `interfaces.md` §44.4 "is logged" under EC-114 versus the P0 no-log counter-failure behaviour; pipeline-level logging owned by the future composition/EC-114 layer). `interfaces.md` §44.4 NOT modified. `SOURCE-GAP-EXECPLAN-28` preserved separately. (2) Plan-level record of the D historical exception added to `docs/execution-plan.md` (correction-history entry, pointers at the one-commit rules). (3) Plan version bumped 1.1.2 → 1.1.3 (patch-level documentation revision; execution model unchanged). Code is byte-identical to `994b6f6`; `baccd17` contains no D implementation change. This is not an approval of D, E, F, G, H or any Gate.
- **`EXE-P0.3.D` — AI VERIFICATION: PASS (final tree, 2026-10-07) → AI VERIFIED — CONTINUING.** Implementation `994b6f6` (unchanged); governance follow-up `baccd17`; plan v1.1.3 and this runbook block are uncommitted documentation. Final tree: `mvn test` 127/127 (SanitizerTest 20, SanitizerIntegrationTest 4). Architect CONFORMANT WITH FOLLOW-UP (the §18.20 per-unit checkpoint line ~224 still reads "no exception"; qualification noted, not changed). Reviewer PASS. Verifier CONFORMANT WITH DOCUMENTED GAPS (SOURCE-GAP-EXECPLAN-29, -30, -31 open, non-blocking). Not HUMAN APPROVED. Continuation to `EXE-P0.3.E` (pre-flight pending) under the same Capability 3 path.

**Capability 3 — PATH STATE AT THE CAPABILITY 3 BOUNDARY (2026-10-07). Documentation only.**
- `EXE-P0.3.E` — **AI VERIFIED — CONTINUING (verification only; NO commit).** Architect pre-flight APPROVED WITH FOLLOW-UP; operator decision (2026-10-07): verification only, no commit. The E-row tests already exist in `SanitizerTest` (`normalizationFailureRestoresOriginalWithExactlyOneWarning`, `normalizerReturningNullIsTreatedAsNormalizationFailure`, `normalizationFailureThenCounterFailureStillPropagatesWithNoOutput`, `completedLineIsNotEmittedOnNormalizationFallback`, `counterFailureOnPreCountPropagatesWithNoOutput`). No CIS, no tenant test, no error code, no schema. Suite 127/127. EC-014 out of scope (`SOURCE-GAP-EXECPLAN-19`).
- `EXE-P0.3.F` — **AI VERIFIED — CONTINUING (NOT APPLICABLE).** Capability 3 §18.6 F row: `NOT APPLICABLE` at this sub-phase; no `QG-NNN` gates raw normalization. No commit.
- `EXE-P0.3.G` — **AI VERIFIED — CONTINUING (NOT APPLICABLE).** §18.6 G row: `NO DEDICATED SCENARIO` for the Sanitizer; no scenario invented. No commit.
- `EXE-P0.3.H` — **AI VERIFIED — CONTINUING (verification only; NO commit).** The H row states "Unit test above (E) doubles as this test"; the E-row tests above are the H evidence. Failure never produces altered output; CIS is not implied. No new code.
- **Note (procedure, SUPERSEDED 2026-10-07):** this line originally recorded that no separate Architect pre-flight was run for H. A retroactive pre-flight was subsequently run, per human decision, against the then-current final tree: verdict **NOT APPLICABLE / VERIFICATION-ONLY CONFIRMED**, no blocker (the E-row tests legitimately satisfy H's acceptance text; no new implementation required; CIS is not implied). The governance deviation (no prior pre-flight) is disclosed as cured, not hidden; this line is kept, superseded, rather than deleted, per the plan's own correction-history discipline.
- **CAPABILITY 3 PATH COMPLETE — GATE AWAITING HUMAN APPROVAL.** Commits: A `e4519d7`; B `1ad6bf8`; C `01ca90c`; D implementation `994b6f6` + documented governance follow-up `baccd17` (human-authorized exception; not a general permission). Uncommitted, documentation only: plan v1.1.3 and this runbook. Full suite 127/127. AI verdicts are evidence, not human approval. Capability 3 Gate and Capability 4: NOT approved, NOT started.
- Open, non-blocking gaps: `SOURCE-GAP-EXECPLAN-18` (null `user_input`, validation code), `-19` (EC-014, out of scope), `-21`–`-26` (deferred transforms), `-29` (OptimizationSpan not implemented), `-30` (`tokens.*` metrics vs fields, architecture §23), `-31` (counter-failure silence vs §44.4 "is logged"). Resolved: `-16`, `-17`, `-20`, `-27`, `-28`.
- Accepted P0 exceptions: counter-failure produces no T1.1 log (documented, `-31`); D one-commit exception (`baccd17`).
- Open follow-ups from reviews (non-blocking): Reviewer LOW test-precision items (preserved as a scratchpad patch; not applied); the §18.20 checkpoint line ~224 wording; plan version-header bump is recorded (1.1.3).

**Capability Gate P0.3 — AI CAPABILITY GATE VERIFICATION and human approval (2026-10-07).**
- **AI CAPABILITY GATE VERIFICATION: PASS WITH DOCUMENTED GAPS.** Verifier (holistic, HEAD `4dc3f44`): PASS WITH DOCUMENTED GAPS; full traceability matrix A–H against INTF-072 and §18.6; all open gaps individually reasoned as non-blocking for this Gate, with `SOURCE-GAP-EXECPLAN-31` flagged for explicit human attention before any future pipeline-composition work. Architect (holistic, same HEAD): CONFORMANT WITH DOCUMENTED GAPS — READY FOR GATE; package placement, dependency direction, fail-open/fail-closed discipline, provider neutrality and scope discipline all confirmed with no violation; both previously-flagged documentation contradictions independently re-verified as fixed in `4dc3f44`. `mvn test` run independently by both agents: 127/127.
- **`Approve CAPABILITY-GATE P0.3` — HUMAN APPROVED (2026-10-07).** Covers, by continuation state: `EXE-P0.3.A` (`e4519d7`, AI VERIFIED — CONTINUING), `EXE-P0.3.B` (`1ad6bf8`, AI VERIFIED — CONTINUING), `EXE-P0.3.C` (`01ca90c`, AI VERIFIED — CONTINUING), `EXE-P0.3.D` (`994b6f6` implementation + `baccd17` human-authorized documentation follow-up, AI VERIFIED — CONTINUING), `EXE-P0.3.E` (verification only, no commit, AI VERIFIED — CONTINUING), `EXE-P0.3.F` (AI VERIFIED — CONTINUING (NOT APPLICABLE)), `EXE-P0.3.G` (AI VERIFIED — CONTINUING (NOT APPLICABLE)), `EXE-P0.3.H` (verification only, no commit, retroactive Architect pre-flight, AI VERIFIED — CONTINUING). Governance/documentation commits `85b156c` and `4dc3f44` covered as part of this Gate's own record.
- Open, non-blocking at Gate: `SOURCE-GAP-EXECPLAN-18, -19, -21`–`-26, -29, -30, -31` remain open past this Gate; none is closed by this approval. `SOURCE-GAP-EXECPLAN-31` carries forward as a named item for human attention before Capability 5 or any pipeline-composition unit claims to satisfy `interfaces.md` §44.4's "is logged" clause.
- **Capability 4 is NOT started and NOT authorized by this approval.** It requires its own explicit `Execute EXE-P0.4.A`.

**Capability 4 — Context Policy. Authorized path opened by `Execute EXE-P0.4.A` (2026-10-07).**
- `EXE-P0.4.A` — COMMITTED `775ce56` — AI VERIFIED — CONTINUING (design note; Architect CONFORMANT, Verifier CONFORMANT, both pre-commit).
- `EXE-P0.4.B` pre-flight found two real blockers, resolved by human decision (2026-10-07), not invented:
  - **HD-CG-P0.4-01:** `edge-cases.md` EC-060 (frozen) says a policy fetch failure defaults permissively; Capability 4's own B/H rows (already internally consistent) say conservative/fail-closed. Resolved: conservative/fail-closed governs, consistent with root `CLAUDE.md` rule 2. `edge-cases.md` is NOT modified; recorded as `CONTRA-EXECPLAN-02`, an accepted documented exception for Capability 4 only.
  - **HD-CG-P0.4-02:** no source defines a policy storage/retrieval contract (`interfaces.md` §20 defines only the schema and an enforcer interface that takes a policy as a parameter; §39 names no policy persistent store). Resolved: `EXE-P0.4.B` accepts an already-resolved `OptimizationPolicy` as a parameter; no storage/lookup component is built; persistence/retrieval deferred (`SOURCE-GAP-EXECPLAN-32`, open, non-blocking for this narrowed B).
  - `docs/execution-plan.md` v1.1.4 records both; B and H rows updated; register rows added.
- `EXE-P0.4.B` is now path-internal, pending its own pre-flight, implementation, tests, and governance review — not yet executed.
- `EXE-P0.4.B` implemented (uncommitted): `OptimizationPolicy`, `PolicyViolation`, `BudgetEnforcementResult`, `PolicyBudgetEnforcer` in `control_plane/policy/context_policy/`, per the Architect pre-flight's APPROVED WITH CONDITIONS verdict (all 5 conditions applied: narrowed non-`PolicyEnforcer` naming; only `max_cost_per_request` enforced; `max_monthly_cost` attributed to `LedgerCostReporter`'s own pre-existing gap; both conservative failure paths — missing policy, missing ledger measurement — stated explicitly; caller-supplied `tenantId`, never `OptimizationPolicy.tenantId()`, governs the ledger lookup). `SOURCE-GAP-EXECPLAN-33` registered (`PolicyOverride` undefined). `EXE-P0.4.A`'s own §7 corrected to distinguish `CacheEntry` (defined, out of scope) from `PolicyOverride`/`AuthorizationContext` (undefined). Tests: 6 new (`PolicyBudgetEnforcerTest`); full suite 133/133 on `mvn clean test`. Not yet reviewed by Architect/Reviewer/Verifier on the final tree; not yet committed.
- `EXE-P0.4.B` — COMMITTED `7fb15a9` (code) + `6ec5849` (docs) — AI VERIFIED — CONTINUING. Architect CONFORMANT WITH FOLLOW-UP (addressed in the docs commit); Reviewer PASS (one HIGH finding on an earlier draft, resolved by HD-CG-P0.4-03 redesign before commit); Verifier CONFORMANT. Full suite 135/135. A follow-up docs-only commit `0ae11f7` corrected a stale terminal-line self-reference left by `6ec5849` (plan v1.1.5's own "Not yet committed"/"Verifier pending" text, superseded, never edited in place).
- `EXE-P0.4.C` — COMMITTED `45fd270` — AI VERIFIED — CONTINUING. Test-tree-only (no main-code change): `StubDownstreamConsumer` + `PolicyBudgetEnforcerIntegrationTest` (4 tests) in `control_plane/policy/context_policy/integration/`, demonstrating `PolicyBudgetEnforcer`'s existing public read API is consumable by a stand-in downstream consumer (Capability 5/6, neither built), mirroring the `EXE-P0.3.C` precedent (`01ca90c`) exactly — not pipeline wiring. Architect pre-flight APPROVED FOR IMPLEMENTATION (before code was written); Reviewer PASS; Verifier CONFORMANT WITH DOCUMENTED GAPS (all pre-existing from B, none new). Full suite 139/139. `SOURCE-GAP-EXECPLAN-32`/`-33` unchanged, neither closed nor worsened.
- `EXE-P0.4.D` — COMMITTED `b474ede` (code) + `0140737` (docs) — AI VERIFIED — CONTINUING. `PolicyBudgetEnforcer` now emits exactly one structured log line per call (INFO compliant / ERROR violation / WARNING no-policy-fallback), per conventions.md §17.2's level table. First-round Architect/Reviewer/Verifier all independently found the same issue: the `POLICY_VIOLATION` event name is interfaces.md §23.2's own Standard Event Type, not locally invented, with an unmet durability requirement (§23.3/§17.4) and a `component_id` that didn't match `conventions.md` §3.1 or the documented `POLICY-ENFORCER` source. Fixed before commit: Javadoc corrected, `component_id` changed to `CONTEXT-POLICY` (matching the `ACCOUNTING-LEDGER`/`EVALUATION-BASELINE` no-canonical-ID precedent), `SOURCE-GAP-EXECPLAN-35` registered (durability unmet, narrows pre-existing `SOURCE-GAP-EXECPLAN-01`). A confirmation-only Reviewer pass on the final tree returned PASS. Full suite 143/143.
- `EXE-P0.4.E` — COMMITTED `d728a08` — AI VERIFIED — CONTINUING. Test-tree-only (no main-code change, plan row's own "Files: Same as B"): `enforceRequestBudget`'s own signature accepts no optimization-signal parameter at all, so there is no injection point to force favorable. `favorableOptimizationSavingsNeverSuppressesAGenuinePolicyViolation` proves a ledger entry with every savings-shaped `CostLedgerEntry` field (both the retained top-level group and the `Cost` group) set maximally favorable still reports the real `MAX_COST_PER_REQUEST_EXCEEDED` violation; `enforceRequestBudgetSignatureAcceptsNoOptimizationSignalParameter` is a reflection-based structural guard (recommended, not required, by the Architect pre-flight). Architect pre-flight APPROVED WITH CONDITIONS (all applied); Reviewer PASS WITH REQUIRED FOLLOW-UP on the first pass (1 MEDIUM — fixture Javadoc overclaimed "the only reachable" savings field, fixed to cover both `CostLedgerEntry` savings-field groups; 2 LOW — reflection test strengthened to assert exactly one overload, missing `violations().size()` assertion added) — all fixed, confirmation-only re-review returned PASS; Verifier CONFORMANT WITH DOCUMENTED GAPS (all gaps pre-existing from B/D, none new). Full suite 145/145.
- `EXE-P0.4.F` — NO COMMIT — AI VERIFIED — CONTINUING (NOT APPLICABLE). Re-verified against the live `quality-gates.md`: no `QG-NNN` dimension targets policy resolution/budget enforcement — every "policy" mention there is `OptimizationPolicy` acting as a quality-gate *configuration source* (e.g. `min_quality_score`), never Context Policy as the *subject* of a gate. Disposition unchanged from the plan row.
- `EXE-P0.4.G` — NO COMMIT — AI VERIFIED — CONTINUING (NOT APPLICABLE). Re-verified against the live `scenario-matrix.md`: the only `SCN-*` entries citing `INTF-036 PolicyEnforcer` (`SCN-CMP-029`, `SCN-CMP-043`, `SCN-CODE-009`, `SCN-POL-004`, etc.) target methods this capability does not realize (`check_tool_allowed`, `enforce` with compression/routing); `SCN-POL-001`–`003` target the unrelated Dynamic Policy Evaluation (DPE) runtime component. No scenario isolates Context Policy's actual `max_cost_per_request` budget-enforcement scope. Disposition unchanged from the plan row.
- `EXE-P0.4.H` — NO COMMIT — AI VERIFIED — CONTINUING. Re-verified: Sub-phase E is now executed; H's row text (which predated E's execution) is confirmed unaffected — E and H characterize different invariants (optimization-signal override-independence vs. fail-closed-on-missing-policy), so B's `nullPolicy_isConservativelyDenied_notPermissive` test remains the correct, sole evidence for this row. `execution-plan.md` v1.1.6 → v1.1.7 records this and the F/G re-verification.
- **All of Capability 4's A–H sub-phases are now complete.**

**Capability Gate P0.4 — AI CAPABILITY GATE VERIFICATION (2026-10-07).**
- **AI CAPABILITY GATE VERIFICATION: PASS WITH DOCUMENTED GAPS.** Verifier (holistic, independent re-derivation, HEAD `d75212a`): PASS WITH DOCUMENTED GAPS — re-verified every sub-phase A–H against the live `interfaces.md` §20/§23, `edge-cases.md` EC-060, `conventions.md`, `quality-gates.md`, and `scenario-matrix.md` directly (not taken on faith from CLAUDE.md/the commit narrative); confirmed frozen-source integrity via `git show --stat` on every Capability 4 commit (no frozen document ever touched); confirmed cross-capability invariants (tenant isolation, fail-closed-on-missing-policy, no-fabricated-savings) hold across the whole capability, not just per-unit; ran `mvn clean test` independently (first run hit a transient `NoClassDefFoundError` classpath-materialization flake on this machine, immediately clean on retry — 145/145, flagged as an environmental observation, not a code defect). Architect (holistic, same HEAD): CONFORMANT WITH DOCUMENTED GAPS — READY FOR GATE — independently confirmed package placement, the single declared dependency (Capability 1 only, verified by import-statement grep across the whole capability), consistent realization of HD-CG-P0.4-01/-02/-03 in the actual code, and the F/G dispositions against the live downstream docs; ran `mvn clean test` independently, 145/145. Two non-blocking conditions carried forward explicitly: (a) `SOURCE-GAP-EXECPLAN-32/-33/-35` remain open past this Gate, not silently closed by it; (b) `edge-cases.md` EC-060's tenant_id+organization_id co-scoping requirement is unaddressed by this capability's no-fetch scope and must be revisited by the first future unit that introduces a policy-store/fetch path (flagged, not registered as a new gap ID by either agent, per the no-new-ID-assignment rule for governance agents).
- Open, non-blocking at Gate: `SOURCE-GAP-EXECPLAN-32` (no policy storage/retrieval), `-33` (`PolicyOverride` undefined), `-35` (`POLICY_VIOLATION` durability unmet). None is closed by this verification. `SOURCE-GAP-EXECPLAN-34` is RESOLVED (HD-CG-P0.4-03 redesign, independently re-confirmed in the live code by both agents).
- **`Approve CAPABILITY-GATE P0.4` — HUMAN APPROVED (2026-10-07).** Covers, by continuation state: `EXE-P0.4.A` (`775ce56`, AI VERIFIED — CONTINUING), `EXE-P0.4.B` (`7fb15a9` code + `6ec5849` docs, AI VERIFIED — CONTINUING; follow-up correction `0ae11f7`), `EXE-P0.4.C` (`45fd270`, AI VERIFIED — CONTINUING), `EXE-P0.4.D` (`b474ede` code + `0140737` docs, AI VERIFIED — CONTINUING), `EXE-P0.4.E` (`d728a08`, AI VERIFIED — CONTINUING), `EXE-P0.4.F` (no commit, AI VERIFIED — CONTINUING (NOT APPLICABLE)), `EXE-P0.4.G` (no commit, AI VERIFIED — CONTINUING (NOT APPLICABLE)), `EXE-P0.4.H` (no commit, AI VERIFIED — CONTINUING). Governance/documentation commits `3ee4a5d`, `46b1a84`, `3d3b0be`, `11e2020`, `d42860f`, `d75212a`, `dc08b1e` covered as part of this Gate's own record.
- Open, non-blocking past this Gate (none closed by this approval): `SOURCE-GAP-EXECPLAN-32` (no policy storage/retrieval), `-33` (`PolicyOverride` undefined), `-35` (`POLICY_VIOLATION` durability unmet). `edge-cases.md` EC-060's tenant/organization co-scoping requirement carries forward as a named item for human attention before any future policy-store/fetch unit claims to satisfy it.
- **Capability 5 is NOT started and NOT authorized by this approval.** It requires its own explicit `Execute EXE-P0.5.A`.

**Capability 5 — Prompt Assembler. Authorized path opened by `Execute EXE-P0.5.A` (2026-10-07).**
- `EXE-P0.5.A` — COMMITTED `a579e72` — AI VERIFIED — CONTINUING. Design note only (`control_plane/prompt_assembler/PromptAssemblerContract.md`): cites `architecture.md` §12.2's Prompt Assembler contract and the `provider_hints` opacity rule verbatim; confirms `control_plane/prompt_assembler/` as a new top-level package (no `conventions.md` §2.1 leaf matches it, bare or nested). Two pre-existing corpus gaps honestly disclosed, not filled: no component ID exists for the Prompt Assembler anywhere in the corpus; "produce a complete assembly ledger" (`architecture.md` §12.2) has no corresponding schema in `interfaces.md`. Preconditions (Capability 3 Gate P0.3, Capability 4 Gate P0.4, both approved 2026-10-07) satisfied. Architect APPROVED WITH CONDITIONS (both applied: disclosed the `execution-plan.md` §8-vs-§9 repository-structure self-contradiction found during pre-flight — fixed separately, `b42fdcd`, v1.1.8; confirmed `Execute EXE-P0.5.A` was explicitly issued). Verifier CONFORMANT. Capability 4's own Gate approval and the git history above are the record of preconditions; this is a path-internal continuation, not a human approval. Capability 4 Gate remains the last human approval; Capability 5's own Gate is NOT approved.
- Pre-B blockers: `EXE-P0.5.B`'s Architect pre-flight returned `BLOCKED` — three deliverable terms ("policy-bounded context," "final prompt," "assembly ledger") were genuinely undetermined by the frozen sources. Four human decisions resolved this (`f412f62`, v1.1.9): `HD-CG-P0.5-01` (minimal `AssembledPrompt` output type, ledger deferred as `SOURCE-GAP-EXECPLAN-36`), `HD-CG-P0.5-02` (accept `BudgetEnforcementResult` as a parameter, never call `PolicyBudgetEnforcer`), `HD-CG-P0.5-03` (non-compliant budget result not enforced here), `HD-CG-P0.5-04` ("approved context" vacuously satisfied at P0 — `CORE-GAP-01`).
- `EXE-P0.5.B` — COMMITTED `f197fc7` (code) + `768d3a1` (docs correction) — AI VERIFIED — CONTINUING. `AssembledPrompt`/`PromptAssembler` per the four HDs above. Architect APPROVED WITH CONDITIONS; Reviewer PASS WITH REQUIRED FOLLOW-UP on the first pass (2 MEDIUM — VOLATILE-region append order contradicted `architecture.md` §12.2's own documented order, and two further §12.2 responsibilities — "select intent-specific instructions," "tool definitions" — were undisclosed; 1 MEDIUM — security test used containment not exact equality; 1 LOW — missing combined-three-region test) — all four fixed, confirmation-only re-review returned PASS; Verifier CONFORMANT WITH DOCUMENTED GAPS. Full suite 155/155. Capability 4's own Gate approval remains the last human approval; this is a path-internal continuation. Capability 5 path continues to `EXE-P0.5.C` (not yet started) under the same authorization; no fresh `Execute` required. Capability 5 Gate NOT approved.
- `EXE-P0.5.C` — COMMITTED `81e0475` — AI VERIFIED — CONTINUING. Test-tree-only (no main-code change, plan row's own explicit "not stubs" instruction honored): `PromptAssemblerIntegrationTest` (2 tests) wires real `Sanitizer`, real `PolicyBudgetEnforcer` (via real `LedgerCostReporter`/`CostLedgerStore`), and real `PromptAssembler` end to end — a budget-compliant case and a budget-non-compliant case, both asserting the security-classified system instruction survives verbatim through the full real chain. Architect APPROVED FOR IMPLEMENTATION; Reviewer PASS (no blocking findings); Verifier CONFORMANT. Full suite 157/157. Capability 5 path continues to `EXE-P0.5.D` (not yet started) under the same authorization; no fresh `Execute` required. Capability 5 Gate NOT approved.
- `EXE-P0.5.D` — COMMITTED `c11e38d` — AI VERIFIED — CONTINUING. `PromptAssembler.assemble()` emits exactly one INFO log line per successful call (`event_type=PROMPT_ASSEMBLED`, `component_id=PROMPT-ASSEMBLER`, both confirmed genuinely local by all three agents independently re-grepping `interfaces.md` §23.2 — applying the `EXE-P0.4.D` `POLICY_VIOLATION` lesson); no region content (which may carry SEC-001 security instructions) is ever logged. `SOURCE-GAP-EXECPLAN-29` explicitly disclosed. Architect APPROVED FOR IMPLEMENTATION; Reviewer PASS WITH REQUIRED FOLLOW-UP and Verifier CONFORMANT WITH DOCUMENTED GAPS, both independently finding the same MEDIUM issue (missing `SOURCE-GAP-EXECPLAN-29` disclosure vs. the Sanitizer/PolicyBudgetEnforcer precedent) — fixed, confirmation-only re-review returned PASS. Full suite 159/159. Capability 5 path continues to `EXE-P0.5.E` (not yet started) under the same authorization; no fresh `Execute` required. Capability 5 Gate NOT approved.
- `EXE-P0.5.E` — COMMITTED `9462714` — AI VERIFIED — CONTINUING. Test-tree-only (no main-code change, plan row's own Files cell "Same as B"): the row's "cache-stable-prefix reordering" language presupposes a mechanism `PromptAssembler` doesn't have (fixed stability-based routing, not tunable); resolved the same way `EXE-P0.4.E` resolved an identical presupposition — a functional test against the one real contention point (two STABLE instructions sharing a region, proving neither is dropped/truncated/overwritten) plus a structural reflection test proving no reordering/priority injection point exists. Independently confirmed by the Verifier, not merely asserted: `architecture.md` §24/§37 list "Context reordering" as its own distinct optimization technique at maturity tier P3, categorically outside this P0 capability's scope. Architect pre-flight APPROVED WITH CONDITIONS (all applied); Reviewer PASS (one optional LOW finding — comment precision — applied); Verifier CONFORMANT WITH DOCUMENTED GAPS. Full suite 161/161. Capability 5 path continues to `EXE-P0.5.F` (not yet started) under the same authorization; no fresh `Execute` required. Capability 5 Gate NOT approved.
- `EXE-P0.5.F` — NO COMMIT — AI VERIFIED — CONTINUING (NOT APPLICABLE). Re-verified against the live `quality-gates.md`: zero mentions of "Prompt Assembler"/"prompt assembl*" anywhere in the document — no `QG-NNN` dimension gates raw assembly at P0 (compression/semantic-equivalence concerns are P1+). Disposition unchanged from the plan row.
- `EXE-P0.5.G` — NO COMMIT — AI VERIFIED — CONTINUING (NOT APPLICABLE). Re-verified against the live `scenario-matrix.md`: the only two hits for "architecture.md §12.2" (lines 7097, 8955) are both compound security-boundary scenarios (prompt-injection defense via structural separation; prompt-injection + cache-poisoning compound case) validating the general SEC-001 instruction-preservation invariant, not Prompt Assembler construction correctness itself. No scenario isolates this capability's actual construction scope. Disposition unchanged from the plan row.
- `EXE-P0.5.H` — NO COMMIT — AI VERIFIED — CONTINUING. Architect pre-flight (APPROVED FOR IMPLEMENTATION) confirmed the row's two clauses ("ordering optimization is fail-open"; "falls back to the simplest correct assembly") describe one coherent, vacuously-satisfied scenario — no ordering/reordering mechanism exists anywhere in `PromptAssembler` (confirmed at E; `architecture.md` §24/§37 place "Context reordering" at P3, outside this P0 capability). The real failure path (a missing required `SanitizerOutput`/`BudgetEnforcementResult` throws `NullPointerException`, B's own test) is strictly more conservative than a "fallback": it never produces any assembly output on a missing required input, so it cannot drop a security-classified segment through that path — the fallback's underlying intent is met a fortiori, not violated. The one substantively testable clause ("never drops a security-classified segment") is directly covered by E's own `securityClassifiedContentSurvivesVerbatimWhenSharingARegionWithAnotherInstruction` test, exactly matching the row's own "Tests: Same tests as B/E cover this" cell. No new test, no source correction, no human decision required.
- **All of Capability 5's A–H sub-phases are now complete.**

**Capability Gate P0.5 — AI CAPABILITY GATE VERIFICATION (2026-10-08).**
- **AI CAPABILITY GATE VERIFICATION: PASS WITH DOCUMENTED GAPS.** Verifier (holistic, independent re-derivation, HEAD `a2dd37c`): PASS WITH DOCUMENTED GAPS — re-verified every sub-phase A–H against the live `architecture.md` §12.2/§24/§37, `quality-gates.md`, `scenario-matrix.md`, and `conventions.md` §2.1 directly (not taken on faith from CLAUDE.md/the commit narrative); confirmed no frozen source (`architecture.md`/`interfaces.md`/`conventions.md`) was touched by any Capability 5 commit; confirmed `.vscode/settings.json` was never part of any EAIOC commit; ran `mvn clean test` independently (161/161). Found one genuine, previously-undisclosed documentation-sync miss: `CLAUDE.md`'s header line still read "`EXE-P0.5.A`/`B`/`C`/`D`/`E` committed" after F/G/H closed, out of sync with its own body text — corrected in this same pass (both lines already agreed the Gate itself was not approved, so this did not affect the Gate decision). Architect (holistic, same HEAD): CONFORMANT WITH DOCUMENTED GAPS — READY FOR GATE — independently re-verified package placement (including the §8/§9 diagram self-contradiction found and corrected earlier in this path, `b42fdcd`), the three-capability dependency set (1 indirectly via 4, 3, 4 — all already Gate-approved), and the F/G dispositions against the live downstream docs rather than trusting the plan row's own text; ran `mvn clean test` independently, 161/161.
- Open, non-blocking at Gate: `SOURCE-GAP-EXECPLAN-36` (no assembly-ledger schema), `SOURCE-GAP-EXECPLAN-29` (no `OptimizationSpan`, carried forward), and the pre-existing absence of a `<LAYER>.<SEQUENCE>-<NAME>` component ID for the Prompt Assembler anywhere in the corpus. None is closed by this verification.
- **`Approve CAPABILITY-GATE P0.5` — HUMAN APPROVED (2026-10-08).** Covers, by continuation state: `EXE-P0.5.A` (`a579e72`, AI VERIFIED — CONTINUING; separate diagram-fix commit `b42fdcd`), `EXE-P0.5.B` (`f197fc7` code + `768d3a1` docs correction, AI VERIFIED — CONTINUING; preceded by decision commit `f412f62` for `HD-CG-P0.5-01`–`04`), `EXE-P0.5.C` (`81e0475`, AI VERIFIED — CONTINUING), `EXE-P0.5.D` (`c11e38d`, AI VERIFIED — CONTINUING), `EXE-P0.5.E` (`9462714`, AI VERIFIED — CONTINUING), `EXE-P0.5.F` (no commit, AI VERIFIED — CONTINUING (NOT APPLICABLE)), `EXE-P0.5.G` (no commit, AI VERIFIED — CONTINUING (NOT APPLICABLE)), `EXE-P0.5.H` (no commit, AI VERIFIED — CONTINUING). Governance/documentation commits `cedda95`, `5e5cf6e`, `ef8282a`, `c888df9`, `77024cd`, `a2dd37c`, `bbd8c32` covered as part of this Gate's own record.
- Open, non-blocking past this Gate (none closed by this approval): `SOURCE-GAP-EXECPLAN-36` (no assembly-ledger schema), `SOURCE-GAP-EXECPLAN-29` (no `OptimizationSpan`, carried forward), and the pre-existing absence of a `<LAYER>.<SEQUENCE>-<NAME>` component ID for the Prompt Assembler anywhere in the corpus.
- **Capability 6 is NOT started and NOT authorized by this approval.** It requires its own explicit `Execute EXE-P0.6.A`.

**Capability 6 — Output Controls. Authorized path opened by `Execute EXE-P0.6.A` (2026-10-08).**
- `EXE-P0.6.A` — COMMITTED `c61c803` — AI VERIFIED — CONTINUING. Design note only (`control_plane/output_controls/OutputControlsContract.md`): cites `architecture.md` §12.3 (Output Schema Selector) and §12.4 (Output Length Controller) verbatim, cross-checked word-for-word against Engineering Spec §7.18/§7.19 (identical); confirms `control_plane/output_controls/` as a new top-level package (no `conventions.md` §2.1 leaf matches either component specifically — one unrelated bare substring match, `model_control/output_forecaster/` for AR-003, is a different component and does not change the placement conclusion). Two pre-existing corpus gaps honestly disclosed, not filled: no component ID exists for either component anywhere in the corpus (`QV.1-SCHEMA-VALIDATOR` in `conventions.md` §3.1 confirmed to be only an illustrative example, never assigned); no `ControlPlaneResponse`/output-validation-target schema exists anywhere in `interfaces.md` or `core/schemas/` (the plausible candidate, `LLMInvocationResult.content`, is named but not adopted). Confirmed `SEC-001` traces only to Capability 5 (Prompt Assembler), not this capability — root `CLAUDE.md` rule 6 is the correct citation, matching the plan row exactly. Confirmed the P0 "Output controls" item (`architecture.md` §36) is distinct from the P1 "Output schema/length control" item (`TECH-017`/`018`, both `optimization-catalog.md`), not conflated. Confirmed no §8-vs-§9 self-contradiction exists for this capability, unlike the Capability 5 bug found and fixed at `EXE-P0.5.A` (`b42fdcd`). Preconditions (Capability 1 Gate P0.1, approved 2026-09-23) satisfied — no other Capability Gate is a declared dependency. Architect pre-flight APPROVED FOR IMPLEMENTATION. Verifier CONFORMANT WITH DOCUMENTED GAPS — independently caught one overstated sub-claim (the `output_forecaster/` substring match, §3 above) and flagged one non-blocking disclosure gap (`EC-050`/`EC-051`, directly on-point edge cases cited by `TECH-017`/`018` but out of this sub-phase's own "Edge Cases: None" scoping) — both corrected/disclosed in the note before this commit. Full suite unaffected (no code): 161/161. Capability 5's own Gate approval remains the last human approval; this is a path-internal continuation. Capability 6 path continues to `EXE-P0.6.B` (not yet started) under the same authorization; no fresh `Execute` required. Capability 6 Gate NOT approved.
- Pre-B blockers: `EXE-P0.6.B`'s Architect pre-flight returned `BLOCKED` — no `interfaces.md`/`core/schemas/` type represents "a response" for this capability, and `architecture.md` §12.3's intent-based selection has no realizable basis (`ControlPlaneRequest` has no intent-classification field). Five human decisions resolved this (v1.1.10): `HD-CG-P0.6-01` (minimal local output-controls type, not `LLMInvocationResult`), `HD-CG-P0.6-02` (accept an already-resolved schema as a parameter; intent-based selection deferred, `SOURCE-GAP-EXECPLAN-37`), `HD-CG-P0.6-03` (length enforcement narrowed to `BudgetConstraints.maxOutputTokens`; field-level granularity deferred, `SOURCE-GAP-EXECPLAN-38`), `HD-CG-P0.6-04` (unit-local exception, no new `ControlPlaneError` code), `HD-CG-P0.6-05` (schema parameter as a required-field + type-check list, not a full JSON Schema validator).
- `EXE-P0.6.B` — COMMITTED `ae92e97` (code) + `d81e1c8` (docs, `SOURCE-GAP-EXECPLAN-39`) — AI VERIFIED — CONTINUING. `CandidateOutput`/`OutputSchema`/`MalformedOutputException`/`ValidatedOutput`/`OutputController` per the five HDs above. `OutputController.enforce()` validates schema first (throwing on any missing/mistyped required field), then enforces whole-response length against `BudgetConstraints.maxOutputTokens` via an independently-instantiated JTokkit `CL100K_BASE` encoding — ordering matches `optimization-catalog.md` `TECH-017`/`018`'s own "Composition / Ordering" field verbatim. Architect pre-flight (on the implementation) APPROVED WITH CONDITIONS — all three applied (cite `EC-050`/`EC-051` explicitly; fix a `TokenCounter`-vs-`TokenCounterIdentity` citation misattribution; disclose the undeclared tenant_id/request_id signature gap for Sub-phase D). Reviewer first pass CHANGES REQUIRED — 2 HIGH (a zero/negative `maxOutputTokens` boundary defect, reproduced directly against the project's own jtokkit 1.1.0 jar, that silently reported false budget compliance; `EC-050`/`EC-051` — both HIGH-severity, both flagged forward by the approved `EXE-P0.6.A` note's own §7 item 7 — undisclosed by ID anywhere in the implementation) and 1 MEDIUM/1 LOW (`ValidatedOutput` silently discarding `CandidateOutput.fields()`; a missing `JTokkitTokenCounter`-precedent concurrency disclosure) — all four fixed (an explicit non-positive-budget branch that bypasses the buggy JTokkit boundary entirely rather than interpreting its unreliable output; `EC-050`/`EC-051` cited by ID and registered as `SOURCE-GAP-EXECPLAN-39`; the `fields()`-drop disclosed as an open item for Sub-phase C; the concurrency disclosure added) plus three new boundary tests; confirmation-only re-review returned PASS. Full suite 177/177 (16/16 for `OutputControllerTest`). Capability 5's own Gate approval remains the last human approval; this is a path-internal continuation. Capability 6 path continues to `EXE-P0.6.C` (not yet started) under the same authorization; no fresh `Execute` required. Capability 6 Gate NOT approved.
