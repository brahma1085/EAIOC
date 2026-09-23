# EAIOC — P0 Step-by-Step Claude Code Execution Runbook

**Based on:** `docs/execution-plan.md` v1.0.7 (aligned with the v1.0.7 DB-2 decision-promotion correction; first reworked for the v1.0.4 correction)  
**Purpose:** Human/operator runbook for executing EAIOC P0 implementation one atomic step at a time.  
**Scope:** P0 only — 6 capabilities, 48 atomic execution units, 6 Capability Gates (54 gated checkpoints), plus the history-specific remediation units `REM-P0.1.A-01`/`-02` and `REM-P0.1.B-01`/`-02` (outside those counts).  
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
- **Next required command** (after this correction is committed and verified): `Execute REM-P0.1.B-02`.

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
[ ] EXE-P0.2.B executed
[ ] EXE-P0.2.B AI verified (verdict: ______)
[ ] EXE-P0.2.B approved
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

**For this repository, the next command is not `EXE-P0.1.A`** — do not restart it: Capability 1 is already largely executed. Once the v1.0.7 DB-2 correction is committed and you have accepted it, the next required command is:

```text
Execute REM-P0.1.B-02
```

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

**Total:** 48 atomic execution units + 6 Capability Gates = 54 gated checkpoints (remediation units `REM-P0.1.A-01`/`-02` are extra, history-specific, and not counted).

---

# 20. Current Position (governing baseline: `execution-plan.md` v1.0.7)

Taken from the git history and `execution-plan.md` §18.14 / §18.18 (v1.0.7). Check `git log --oneline` for anything newer.

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
CURRENT DOCUMENTATION CORRECTION

REM-P0.1.B-02:
NOT EXECUTED — BLOCKED until the DB-2 correction is committed

EXE-P0.1.G / EXE-P0.1.H approvals:
NOT EVIDENCED — explicit human confirmation required

Capability Gate P0.1:
BLOCKED (AC-005 / CONTRA-EXECPLAN-01)

EXE-P0.2.A:
EXECUTED + APPROVED

EXE-P0.2.B:
BLOCKED — requires Capability Gate P0.1 approval (§18.15 guard)
```

```text
NEXT REQUIRED COMMAND (only after the DB-2 correction is committed and verified)

Execute REM-P0.1.B-02
```

Do **not** restart `EXE-P0.1.A` or `EXE-P0.1.B`. Do **not** execute `EXE-P0.2.B` before `REM-P0.1.B-02` is approved and `Approve CAPABILITY-GATE P0.1` has been given.

Detail per unit:

| Unit | Commit | Status |
|---|---|---|
| `EXE-P0.1.A` | `a345742` | Executed + approved (v1.0.3). Reconciled **GAP FOUND** (shared `core/`), closed by the REM-A units |
| `EXE-P0.1.B` | `e353dcf` | Executed + approved (v1.0.3). Reconciled **GAP FOUND** (AC-005 / `INTF-047`, `CONTRA-EXECPLAN-01`); to be closed by the REM-B units |
| `EXE-P0.1.C` | `5d8640e` | Executed + approved (v1.0.3) |
| `EXE-P0.1.D` | `e1730ce` | Executed + approved (v1.0.3) |
| `EXE-P0.1.E` | `3987957` | Executed + approved (v1.0.3) |
| `EXE-P0.1.F` | — (none, correct) | `NOT APPLICABLE` — acknowledged and approved |
| `EXE-P0.1.G` | `95a1267` | Executed (v1.0.3). **Approval not evidenced** |
| `EXE-P0.1.H` | `0c18174` | Executed (v1.0.3). **Approval not evidenced** |
| `REM-P0.1.A-01` | `c05e3e7`, `93c1ba3` | Done + approved (design + Amendment 1) |
| `REM-P0.1.A-02` | `5e12148` | Done + approved (shared `core/` types) |
| `REM-P0.1.B-01` | `34dd1f1` | Done + AI verified + human approved (Contract / Design Reconciliation; HD-1 to HD-4) |
| DB-2 (plan v1.0.7) | — (this correction) | HD-1 to HD-4 promoted into `interfaces.md` §28.1 / `conventions.md` §14.1 |
| `REM-P0.1.B-02` | — | **Not executed — next, once DB-2 is committed** (Minimal Implementation Remediation) |
| Capability Gate P0.1 | — (never a commit) | **BLOCKED** |
| `EXE-P0.2.A` | `0e92261` | Executed + approved. Reconciled: **PASS** |
| `EXE-P0.2.B` | — | Blocked — needs Gate P0.1 approval |
| `c3d6ecf`, `a764689` | — | Not implementation commits (`CLAUDE.md` / `.vscode/`) — not counted |

**Remaining sequence up to `EXE-P0.2.B`** — one line at a time, with Claude Code stopping after every step:

```text
(commit the DB-2 / v1.0.7 documentation correction)

Execute REM-P0.1.B-02
Approve REM-P0.1.B-02

(G/H evidence) confirm the earlier approvals explicitly, or issue after review:
Approve EXE-P0.1.G
Approve EXE-P0.1.H

Review CAPABILITY-GATE P0.1
Approve CAPABILITY-GATE P0.1

Execute EXE-P0.2.B
Approve EXE-P0.2.B
```

After `EXE-P0.2.B`, continue with §8's Capability 2 steps from `Execute EXE-P0.2.C`.
