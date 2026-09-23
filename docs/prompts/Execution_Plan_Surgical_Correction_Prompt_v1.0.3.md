# EAIOC — Execution Plan Surgical Correction Prompt v1.0.3

## Purpose

Update the existing `docs/execution-plan.md` so that the document is not only a plan of atomic execution units, but also contains an explicit, copy/paste-ready **Claude Code Mode B Execution Command Protocol**.

The current execution plan already defines:

- `EXE-P0.<n>.<letter>` atomic execution-unit IDs
- one lifecycle sub-phase = one commit = one explicit human approval
- strict sequential execution
- the rule that `Execute P0.1` means `Execute EXE-P0.1.A`
- a separate Capability Gate after Sub-phase H

However, the plan currently does not provide a dedicated operational section showing the exact commands the user should issue to Claude Code, the required response/checkpoint format, and the exact transition from one approved unit to the next.

This correction adds that operational protocol **without changing the scope, architecture, technology baseline, requirements, dependency model, or any upstream document**.

---

## Current State

Current file:

`docs/execution-plan.md`

Current version:

`1.0.2`

Target version:

`1.0.3`

This is a **document-only surgical correction**.

---

## Mandatory Scope

### Allowed file modification

Modify only:

`docs/execution-plan.md`

### Forbidden modifications

Do NOT modify:

- `architecture.md`
- `implementation-plan.md`
- `implementation-readiness-gate.md`
- `requirements-traceability.md`
- any ADR
- `security.md`
- `observability.md`
- `quality-gates.md`
- `eval.md`
- `SCALING.md`
- `scenario-matrix.md`
- `conventions.md`
- any other upstream/sibling document
- any `control_plane/` source file
- any test file
- any application code

Do not create source code.

Do not execute implementation work.

---

# Required Correction

Add a new dedicated section to `docs/execution-plan.md` titled approximately:

## `18.11 Claude Code Mode B Execution Command Protocol`

Place it immediately after the Capability Gate model (`§18.10`) and before the P1 Execution Plan (`§19`), so the atomic execution plan is followed immediately by the operational command protocol.

Renumber later sections/subsections only if necessary to preserve coherent heading numbering. Do not alter their substantive content.

---

# Required Content of the New Section

The new section must make the following explicit.

## 1. Mode B is user-command driven

State clearly:

- Mode B does not begin automatically.
- The user explicitly starts each atomic unit.
- Claude Code must never infer authorization for the next unit.
- Passing tests, clean status, a successful commit, or a closed Capability Gate do not authorize the next execution unit.
- The only authorization to begin an execution unit is an explicit user command.

---

## 2. Exact execution command syntax

Define the canonical command:

```text
Execute EXE-P0.<n>.<letter>
```

Examples:

```text
Execute EXE-P0.1.A
Execute EXE-P0.1.B
Execute EXE-P0.1.C
...
Execute EXE-P0.1.H

Execute EXE-P0.2.A
...
Execute EXE-P0.6.H
```

Also preserve the existing shorthand rule:

```text
Execute P0.1
```

means:

```text
Execute EXE-P0.1.A
```

and MUST NOT mean execute all of Capability 1.

State that the canonical form is preferred because it identifies exactly one atomic execution unit.

---

## 3. Exact lifecycle for every atomic unit

Define this mandatory sequence:

```text
USER
  |
  |  Execute EXE-P0.n.X
  v
CLAUDE CODE
  |
  |-- Re-read required source sections
  |-- Verify prerequisite approval state
  |-- Execute ONLY EXE-P0.n.X
  |-- Run required verification/tests
  |-- Inspect git diff/status
  |-- Create exactly ONE commit for that sub-phase
  |-- Report evidence
  v
### AWAITING APPROVAL
  |
  |  (Claude stops)
  |
USER
  |
  |  explicit approval
  v
NEXT EXPLICIT Execute EXE-P0.n.next-letter
```

Important:

Claude Code must stop after one atomic unit.

Claude Code must not continue to the next letter automatically.

Claude Code must not execute the Capability Gate automatically after H.

---

## 4. Exact user approval syntax

Define a canonical approval command.

Use:

```text
Approve EXE-P0.<n>.<letter>
```

Examples:

```text
Approve EXE-P0.1.A
Approve EXE-P0.1.B
Approve EXE-P0.1.C
```

For the final Sub-phase H:

```text
Approve EXE-P0.1.H
```

This approval authorizes movement to the **Capability Gate**, but does NOT itself close the Capability Gate.

For the Capability Gate define a separate command:

```text
Approve CAPABILITY-GATE P0.1
```

This closes the promotion gate and authorizes the next capability's Sub-phase A.

Example:

```text
Approve CAPABILITY-GATE P0.1
```

then the next implementation command is explicitly:

```text
Execute EXE-P0.2.A
```

Do not require Claude Code to infer this transition.

---

## 5. Required Claude Code response protocol

For every executed `EXE-P0.n.<letter>`, Claude Code must report:

### Execution Header

```text
EXECUTED: EXE-P0.n.X
CAPABILITY: <name>
SUB-PHASE: <name>
```

### Implementation / Design Summary

What was changed for this atomic unit.

### Source Traceability

Exact source documents and section references consulted.

### Verification

Commands/tests/checks actually executed and their results.

### Git Evidence

- changed files
- `git diff --stat`
- relevant `git status --short`
- commit hash
- commit message

### Scope Compliance

Explicitly confirm:

- only the requested atomic unit was executed
- no later sub-phase was executed
- no other capability was executed
- no upstream document was modified

### Approval Checkpoint

End with exactly:

```text
### AWAITING APPROVAL
Approve EXE-P0.n.X to continue.
```

For Sub-phase H, the ending must instead be:

```text
### AWAITING APPROVAL
Approve EXE-P0.n.H to proceed to the Capability Gate.
```

Do not automatically perform the Capability Gate.

---

## 6. Capability Gate command protocol

Define the Gate as a separate review action.

After:

```text
Approve EXE-P0.n.H
```

Claude Code may prepare/present the Capability Gate review evidence, but must not treat the gate as automatically approved.

The Gate review must verify the shared questions already defined in §18.10:

- Entry Criteria
- Implementation Completion
- Integration Completion
- Security/Governance Completion
- Quality Completion
- Scenario Completion
- Failure/Recovery Completion
- Exit Criteria
- Blocking Conditions

The Gate produces:

```text
CAPABILITY GATE REVIEW: P0.n
STATUS: PASS / BLOCKED
```

If blocked:

```text
### CAPABILITY GATE BLOCKED
Reason:
Required remediation:
No next capability may begin.
```

If the user approves:

```text
Approve CAPABILITY-GATE P0.n
```

then Claude Code reports:

```text
CAPABILITY GATE APPROVED: P0.n
NEXT AUTHORIZED UNIT: EXE-P0.(n+1).A
```

but MUST still wait for the separate command:

```text
Execute EXE-P0.(n+1).A
```

---

## 7. Full P0 command sequence

Include one complete copy/paste sequence so a human can run the entire P0 tier without ambiguity.

Use exactly this structure:

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
...
```

Continue this pattern through:

```text
Approve CAPABILITY-GATE P0.6
```

Do NOT imply that the whole sequence should be pasted into Claude Code at once.

Explicitly state:

> The sequence is a human/operator reference sequence. Commands are issued one at a time, only after the preceding checkpoint has been reviewed and approved.

---

## 8. Command-state rules

Add a concise state table:

| State | Allowed next action |
|---|---|
| No active unit | User may issue `Execute EXE-P0.n.X` if prerequisites are approved |
| Unit executing | Claude Code must execute only that unit |
| Unit complete, awaiting approval | User must issue `Approve EXE-P0.n.X` |
| H approved | Capability Gate review may occur |
| Gate awaiting approval | User must issue `Approve CAPABILITY-GATE P0.n` |
| Gate approved | User may issue next capability's `.A` command |
| Gate blocked | No next capability may start |

Make clear that Claude Code itself does not self-authorize state transitions.

---

## 9. Prohibited command interpretation

Add an explicit section stating Claude Code must reject or stop on ambiguous commands such as:

```text
Implement P0.1
Complete Capability 1
Continue with P0
Finish P0.1
Implement everything ready
Proceed with the remaining steps
```

unless the user explicitly maps the request to a single canonical execution unit.

The preferred operator command is always:

```text
Execute EXE-P0.n.X
```

Do not treat a broad natural-language command as authorization for multiple units.

---

## 10. Capability numbering and counts

Preserve exactly:

- 6 capabilities
- 8 lifecycle sub-phases A–H per capability
- 48 atomic `EXE-P0.<n>.<letter>` execution units
- 6 separate Capability Gates
- 54 total gated checkpoints

Do not call the Capability Gates "atomic execution units".

---

## 11. Commit rule

Restate:

```text
One A–H lifecycle sub-phase = one implementation commit.
Capability Gate = no implementation commit.
```

The Gate is a review/promotion checkpoint only.

---

## 12. No automatic chaining

Add a strong rule:

> Claude Code MUST stop after the requested atomic unit, even when all tests pass, the commit succeeds, no blockers are found, or the user previously approved the broader capability. No completion signal, passing gate, or successful command can be interpreted as permission to execute the next unit.

---

# Header / Version Update

Change:

```text
Version: 1.0.2
```

to:

```text
Version: 1.0.3
```

Update the correction history with a new entry:

> v1.0.2 → v1.0.3 — surgical correction pass adding the explicit Claude Code Mode B Execution Command Protocol, including canonical `Execute EXE-P0.<n>.<letter>` commands, explicit approval commands, Capability Gate command handling, required response/reporting format, state transitions, and the complete operator reference sequence. No scope, architecture, technology baseline, requirements, source-gap, ADR, or sequencing content was changed.

Update any summary/version references necessary to make the document internally consistent.

---

# Important Preservation Rules

Do NOT change:

- the six P0 capabilities
- their order
- A–H lifecycle structure
- the 48-unit count
- the six Capability Gates
- the 54 gated-checkpoint count
- the distinction between Gate and sub-phase
- the Java/Spring Boot technology baseline
- the package-placement gap statements
- the source-gap register
- ADR status
- Observability exclusion
- P1–P5 non-atomized status
- readiness-boundary language
- architecture contracts
- requirements traceability
- any upstream document

This is an operational-command clarification only.

---

# Verification Requirements

After editing:

1. Verify only `docs/execution-plan.md` changed.
2. Verify version is `1.0.3`.
3. Verify the new Mode B protocol exists.
4. Verify `Execute EXE-P0.1.A` is shown explicitly.
5. Verify `Approve EXE-P0.1.A` is shown explicitly.
6. Verify `Approve CAPABILITY-GATE P0.1` is shown explicitly.
7. Verify the complete P0 operator reference sequence exists.
8. Verify the document still states:
   - 48 atomic execution units
   - 6 Capability Gates
   - 54 total gated checkpoints
9. Verify Capability Gate has no commit.
10. Verify no source code was created or changed.
11. Verify no upstream document was changed.

Do NOT perform any implementation command such as `Execute EXE-P0.1.A`. This session is correction-only.

---

# Final Response Requirement

After the document-only correction is complete, report the evidence and end exactly with:

```text
FINAL EXECUTION PLAN v1.0.3 CORRECTION COMPLETE — READY FOR FREEZE
```

