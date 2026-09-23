# EAIOC — Execution Plan Surgical Correction Prompt v1.0.4

## Purpose

Correct the current EAIOC execution process before continuing P0 implementation.

This correction addresses **two execution-plan issues exposed during `EXE-P0.2.B`**:

1. The P0.2.B prerequisite `control_plane/core/interfaces/`, `control_plane/core/schemas/`, and `control_plane/core/errors/` does not exist even though the execution plan says those shared foundations were built during Capability 1.
2. The approval model currently has only a human approval checkpoint. The execution process must be strengthened so that **every atomic execution step first passes an explicit Claude Code / AI verification gate and only then becomes eligible for explicit human approval**.

This correction is **planning/protocol only**. It must not implement the missing code itself.

---

# 1. Current State

Current execution plan:

`docs/execution-plan.md`

Current version:

`1.0.3`

Target version:

`1.0.4`

Current implementation state observed:

- Capability 1 A–E and H have implementation commits.
- Capability 1 F was treated as not applicable and has no dedicated commit.
- Capability 2 A was implemented and approved.
- Capability 2 B correctly stopped because required shared core types/packages do not exist.
- An additional non-subphase commit `c3d6ecf` modified `CLAUDE.md` and added `.vscode/settings.json`.
- `main` and `origin/main` are currently reported as synchronized.

---

# 2. Mandatory File Scope

## Allowed file modification

Modify only:

```text
docs/execution-plan.md
```

## Forbidden modifications

Do NOT modify:

- `CLAUDE.md`
- `docs/implementation-plan.md`
- `docs/implementation-readiness-gate.md`
- `docs/requirements-traceability.md`
- `docs/architecture.md`
- `docs/interfaces.md`
- `docs/conventions.md`
- `docs/security.md`
- `docs/observability.md`
- `docs/quality-gates.md`
- `docs/eval.md`
- `docs/SCALING.md`
- any ADR
- any source file
- any test file
- any `control_plane/` implementation file

Do not create implementation code.

Do not execute `EXE-P0.2.B`.

Do not create any new Git commit as part of this correction unless the repository's existing document-correction workflow explicitly requires the execution-plan correction commit; if a commit is made, it must contain only `docs/execution-plan.md`.

---

# 3. Issue A — Shared Core Foundation Ownership

## Problem

The current execution plan states under Capability 1 Preconditions that:

```text
core/interfaces/
core/schemas/
core/errors/
```

are scaffolded and states that Capability 1 Sub-phase A also establishes the shared zero-dependency substrate.

However, the actual repository does not contain that shared foundation.

Capability 2.A then identifies those packages as a prerequisite for `EXE-P0.2.B`.

Therefore the current plan has an ownership gap: the plan says the shared foundation is created during Capability 1, but the P0.1 execution rows do not unambiguously assign the complete foundation creation to a concrete executable unit.

## Required correction

The v1.0.4 execution plan must explicitly define:

### Shared Foundation Owner

The shared foundation belongs to:

```text
Capability 1 — Token Accounting and Cost Ledger
```

and specifically to:

```text
EXE-P0.1.A — Contract & Design
```

### Required P0.1.A scope

`EXE-P0.1.A` must explicitly include the **design and minimal repository scaffolding definition** for:

```text
control_plane/core/interfaces/
control_plane/core/schemas/
control_plane/core/errors/
```

and identify the minimum shared types required by the already-defined P0 contracts, without inventing unrelated schema.

At minimum, the plan must explicitly account for the missing types referenced by Capability 2:

```text
ControlPlaneRequest
OptimizationPlan
```

The plan must not invent their entire schema. It must require Claude Code to verify the exact authoritative source fields against the live `interfaces.md` before implementation.

### Important boundary

`EXE-P0.1.A` owns the shared foundation.

`EXE-P0.2.B` must **consume** that foundation, not create it opportunistically.

Therefore:

```text
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

The v1.0.4 correction must make this ownership explicit.

---

# 4. Handle the Already-Executed P0.1 State Carefully

The current repository has already passed P0.1.A and P0.1.A was explicitly approved.

The correction must **not silently pretend that P0.1.A has not happened**.

The execution plan must introduce an explicit remediation procedure for the already-completed checkpoint.

Use a concept such as:

```text
POST-HOC EXECUTION RECONCILIATION
```

The purpose is not to alter the historical commit.

It is to determine whether the already-completed `EXE-P0.1.A` actually satisfied the corrected ownership boundary.

The reconciliation must distinguish:

```text
HISTORICAL EXECUTION RESULT
vs.
CORRECTED EXECUTION-PLAN REQUIREMENT
```

Possible result:

```text
RECONCILED: PASS
```

or:

```text
RECONCILED: GAP FOUND
```

If the corrected requirement is not satisfied, do NOT silently fold the missing foundation into P0.2.B.

Instead, the plan must require the missing work to be performed through an explicitly identified remediation step before `EXE-P0.2.B` proceeds.

The remediation must remain auditable and must not rewrite historical commit content.

---

# 5. Issue B — Two-Stage Approval Model

The current model must be strengthened.

From v1.0.4 onward, every atomic execution unit follows:

```text
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

Human approval alone is not sufficient.

AI verification alone is not sufficient.

Both are required.

---

# 6. Canonical Step Lifecycle

Replace/extend the previous execution lifecycle with:

```text
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

The AI verification is a prerequisite to human approval.

---

# 7. AI Verification Is Not Human Approval

Make this distinction explicit.

### AI Verification

Claude Code answers:

> "Based on the authoritative sources, the execution-plan row, the actual changes, tests, and Git evidence, did this atomic unit satisfy its defined requirements?"

Possible outcomes:

```text
AI VERIFICATION: PASS
AI VERIFICATION: BLOCKED
AI VERIFICATION: NOT APPLICABLE
AI VERIFICATION: PASS WITH DOCUMENTED NON-BLOCKING GAP
```

### Human Approval

The user answers:

> "I reviewed the AI verification and the evidence and authorize progression."

Canonical command:

```text
Approve EXE-P0.n.X
```

The human approval must never be simulated by the AI.

---

# 8. AI Verification Checklist

For EVERY `EXE-P0.n.X`, Claude Code must verify all applicable dimensions below.

## A. Scope verification

Confirm:

```text
Only requested EXE-P0.n.X executed.
No later sub-phase executed.
No other capability executed.
No unrelated source files changed.
No upstream documentation modified.
```

## B. Source verification

Re-read the live authoritative source sections named by that capability.

Do not rely solely on the execution-plan text.

Confirm:

```text
Source section exists.
Referenced contract exists.
Implementation matches source.
No invented schema/requirement was introduced.
```

## C. Requirement verification

Verify each requirement listed for the capability/sub-phase.

Report:

```text
Requirement ID → satisfied / not satisfied / not applicable
```

## D. Architecture verification

Verify implementation respects:

- component boundary
- package/module boundary
- dependency direction
- provider neutrality
- state ownership
- tenant isolation
- fail-open/fail-closed disposition

## E. Test verification

Run the tests/checks actually required by the sub-phase.

Do not claim a test passed unless it actually ran.

Report:

```text
Command
Result
Relevant evidence
```

## F. Negative/failure-path verification

Run the failure-path or negative test explicitly identified by the execution-plan row where applicable.

## G. Git verification

Verify:

```text
git status --short
git diff --stat
git diff --name-only
```

and confirm that changed files belong to the requested unit.

Where a commit is required:

```text
git log -1 --oneline
```

and verify the commit contains only the intended scope.

## H. Dependency/precondition verification

Confirm all prerequisites are actually satisfied.

This is especially important for:

```text
EXE-P0.2.B
```

It must verify the shared core foundation really exists before implementing against it.

## I. Source-gap verification

If an execution exposes a missing contract or source gap:

```text
AI VERIFICATION: BLOCKED
```

unless the plan explicitly classifies the gap as non-blocking and the unit can proceed without inventing content.

## J. Final AI verdict

Conclude with exactly one:

```text
AI VERIFICATION: PASS
```

or:

```text
AI VERIFICATION: BLOCKED
```

or:

```text
AI VERIFICATION: NOT APPLICABLE
```

or:

```text
AI VERIFICATION: PASS WITH DOCUMENTED NON-BLOCKING GAP
```

---

# 9. Mandatory AI Verification Report

For every atomic unit, use:

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

Only after the AI verdict is satisfactory may Claude Code present the human approval checkpoint.

---

# 10. Revised Human Approval Checkpoint

For A–G:

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

If AI verification is blocked:

```text
### AI VERIFICATION BLOCKED

AI VERIFICATION: BLOCKED

Reason:
Required remediation:

Human approval is NOT requested.
The next execution unit is NOT authorized.
```

This is important:

> A blocked AI verification must not be followed by an approval request.

---

# 11. Human Approval Must Be Explicit

The user must issue:

```text
Approve EXE-P0.n.X
```

only after reviewing the AI verification and the actual implementation evidence.

Claude Code must not interpret:

```text
AI VERIFICATION: PASS
```

as human approval.

Likewise, Claude Code must not interpret a human approval of a previous unit as approval of the next unit.

---

# 12. Capability Gate — Three-Stage Model

The Capability Gate now becomes:

```text
Sub-phase H
   ↓
AI Verification of H
   ↓
Human Approval of H
   ↓
Capability Gate Review
   ↓
AI Gate Verification
   ↓
Human Gate Approval
   ↓
Next Capability A
```

The Gate itself must therefore have two layers:

### AI Gate Verification

Claude Code reviews:

- Entry Criteria
- Implementation Completion
- Integration Completion
- Security/Governance Completion
- Quality Completion
- Scenario Completion
- Failure/Recovery Completion
- Exit Criteria
- Blocking Conditions

and reports:

```text
AI CAPABILITY GATE VERIFICATION: PASS / BLOCKED
```

### Human Gate Approval

The user issues:

```text
Approve CAPABILITY-GATE P0.n
```

The command is valid only after AI Gate Verification is satisfactory.

---

# 13. Capability Gate Reporting

If AI Gate Verification passes:

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

After human approval:

```text
CAPABILITY GATE APPROVED: P0.n
NEXT AUTHORIZED UNIT: EXE-P0.(n+1).A
```

Still wait for:

```text
Execute EXE-P0.(n+1).A
```

---

# 14. N/A Sub-phases

The existing P0 plan contains sub-phases marked `NOT APPLICABLE`.

Do not silently convert those into artificial implementation work.

For an N/A unit:

Claude Code must verify:

```text
AI VERIFICATION: NOT APPLICABLE
```

and provide the evidence/reason.

Then:

```text
### AI VERIFIED — AWAITING HUMAN APPROVAL

AI VERIFICATION: NOT APPLICABLE

Approve EXE-P0.n.X to continue.
```

Human approval remains required so the operator explicitly acknowledges the N/A disposition.

Do not invent a fake implementation commit for an N/A sub-phase merely to satisfy sequencing.

The plan's count remains:

```text
48 atomic EXE-P0.<n>.<letter> execution units
```

but an N/A unit may have no source-code change and therefore no implementation commit. The execution plan must make this exception explicit rather than claiming every A–H unit necessarily has a code commit.

---

# 15. Commit Discipline Clarification

Preserve:

```text
One executable A–H sub-phase = one isolated implementation change-set.
```

Where that sub-phase produces implementation changes:

```text
one sub-phase = one implementation commit
```

Where a sub-phase is genuinely `NOT APPLICABLE` and produces no implementation changes:

```text
no implementation commit
```

The Capability Gate remains:

```text
no implementation commit
```

Do not call an N/A checkpoint a fake commit.

---

# 16. Out-of-Band Commit Handling

The repository contains an additional commit:

```text
c3d6ecf
docs: sync CLAUDE.md with P0 implementation progress; add VS Code settings
```

This commit modified:

```text
CLAUDE.md
.vscode/settings.json
```

The v1.0.4 correction must explicitly state:

- this was not an `EXE-P0.n.X` implementation commit
- it must not be counted as one of the 48 atomic implementation commits
- it must not be used as evidence that an execution unit was completed
- future sub-phase execution must not modify unrelated editor files
- future progress/status documentation changes must remain outside implementation-unit commits unless explicitly authorized by the execution plan

Do not retroactively alter the historical commit.

The execution protocol should establish a clean rule for future commits.

---

# 17. `.vscode/settings.json` Handling

Do not modify it during this correction.

Record it as existing repository history.

Future implementation commands must not add personal editor configuration to an atomic P0 implementation commit unless a future explicit source-backed requirement authorizes it.

Whether `.vscode/` should ultimately be ignored is a separate repository-management decision and is outside this surgical execution-plan correction.

---

# 18. P0.2.B Specific Guard

Add an explicit pre-execution guard for:

```text
EXE-P0.2.B
```

Before implementing it, Claude Code MUST verify:

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

If any condition fails:

```text
AI VERIFICATION: BLOCKED
```

and Claude Code must not write code.

It must report exactly what prerequisite is missing.

---

# 19. Required State Model

Add this execution state model:

```text
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

```text
AI VERIFYING
    ↓
AI VERIFICATION BLOCKED
    ↓
REMEDIATION
    ↓
RE-EXECUTE / RE-VERIFY
```

No state may bypass AI verification.

---

# 20. Explicit Command Rules

## Start

```text
Execute EXE-P0.n.X
```

## AI verification

This is performed by Claude Code automatically after the requested unit's work and tests.

The human does not issue a separate `Verify` command.

## Human approval

```text
Approve EXE-P0.n.X
```

## Capability Gate approval

```text
Approve CAPABILITY-GATE P0.n
```

## Next execution

```text
Execute EXE-P0.(n+1).A
```

Never combine the approval and next execution into one command.

---

# 21. Revised Example

The document must include this exact illustrative sequence:

```text
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

```text
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

This example must be present so there is no ambiguity.

---

# 22. Updated P0 Sequence Semantics

The overall P0 sequence becomes:

```text
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

---

# 23. Required Update to the Execution-Plan Summary

Update the plan's summary/readiness language so that:

```text
PLAN READY
```

means the command and verification protocol is defined.

And:

```text
MODE B READY
```

means:

- environment readiness has passed
- the current execution-plan version is accepted
- AI verification protocol is active
- human approval protocol is active
- shared-foundation ownership is resolved
- no known blocking prerequisite remains for the next executable unit

Do not state `CODE COMPLETE` or `BENCHMARK VALIDATED`.

---

# 24. Version and Correction History

Update:

```text
Version: 1.0.4
```

Add to correction history:

> v1.0.3 → v1.0.4 — surgical correction pass addressing the shared-core-foundation ownership gap exposed by `EXE-P0.2.B` and strengthening Mode B approval into a mandatory two-stage control: Claude Code AI verification first, followed by explicit human approval. The correction assigns the shared `core/interfaces`, `core/schemas`, and `core/errors` foundation ownership to Capability 1 / `EXE-P0.1.A`, adds a post-hoc reconciliation mechanism for already-executed P0.1.A, adds explicit precondition guards for P0.2.B, defines AI verification checklists/reporting/state transitions, separates AI verification from human approval, adds AI Capability Gate verification before human Gate approval, clarifies N/A sub-phase handling, and records the historical `c3d6ecf` out-of-band documentation/editor-settings commit as non-implementation history. No upstream architecture, requirements, ADR, technology-baseline, or P0 capability scope is changed by this correction.

---

# 25. Verification Requirements

After editing `docs/execution-plan.md`, verify:

### Plan structure

- version = `1.0.4`
- §18.11 command protocol remains present
- new AI verification protocol exists
- human approval remains explicit
- Capability Gate remains separate from implementation sub-phases
- 48 atomic execution units remain
- 6 Capability Gates remain
- 54 total gated checkpoints remain

### Shared foundation

- `core/interfaces` ownership explicitly assigned to P0.1.A
- `core/schemas` ownership explicitly assigned to P0.1.A
- `core/errors` ownership explicitly assigned to P0.1.A
- missing `ControlPlaneRequest` / `OptimizationPlan` is explicitly handled
- P0.2.B has a hard pre-execution guard for those prerequisites
- historical P0.1.A completion is reconciled rather than rewritten

### Approval protocol

- `Execute EXE-P0.n.X`
- AI verification
- `AI VERIFICATION: PASS / BLOCKED / NOT APPLICABLE / PASS WITH DOCUMENTED NON-BLOCKING GAP`
- `### AI VERIFIED — AWAITING HUMAN APPROVAL`
- `Approve EXE-P0.n.X`
- `AI CAPABILITY GATE VERIFICATION`
- `Approve CAPABILITY-GATE P0.n`
- no automatic chaining
- no approval request after blocked AI verification

### Git

- historical `c3d6ecf` is explicitly treated as non-subphase history
- `.vscode/settings.json` is not counted as a P0 implementation file
- no source code is created by this correction
- no upstream file is modified

### File scope

Only:

```text
docs/execution-plan.md
```

may change.

---

# 26. Critical Safety Rule for Mode B

Add this exact rule:

> **Claude Code MUST NOT request human approval until its own AI Verification has completed and returned an acceptable verdict. Human approval is a second, independent authorization layer. AI verification is evidence; human approval is authorization. Neither substitutes for the other.**

Also add:

> **Claude Code MUST NOT treat its own AI Verification PASS as permission to execute the next unit. Only the explicit human `Approve ...` command authorizes progression, and only a subsequent explicit `Execute ...` command authorizes implementation.**

---

# 27. Final Correction Boundary

This v1.0.4 correction is complete when:

```text
docs/execution-plan.md
```

contains:

```text
1. Correct shared-core ownership
2. Historical P0.1 reconciliation mechanism
3. P0.2.B prerequisite guard
4. AI verification at every atomic step
5. Human approval after AI verification
6. AI Capability Gate verification
7. Human Capability Gate approval
8. N/A handling
9. Out-of-band commit handling
10. No-automatic-chaining rule
```

No implementation should be performed as part of this correction.

---

# Final Response Requirement

After the document-only correction is complete, report the evidence and end exactly with:

```text
FINAL EXECUTION PLAN v1.0.4 CORRECTION COMPLETE — READY FOR FREEZE
```
