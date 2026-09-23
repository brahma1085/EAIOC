# EAIOC — Execution Plan Surgical Correction Prompt v1.0.5

## 1. Purpose

Perform a **surgical documentation-only correction** of the EAIOC execution-plan artifacts to resolve the remaining internal inconsistencies identified during verification of the current v1.0.4 documents.

This correction is strictly limited to:

1. `docs/execution-plan.md`
2. `docs/execution-plan-p0-steps.md`

Do **not** modify source code, tests, implementation artifacts, upstream architecture/requirements documents, ADR content, repository structure, or any other file.

This correction does not change EAIOC scope, architecture, requirements, technology baseline, P0 capability scope, execution counts, AI verification model, human approval model, remediation model, or sequencing.

---

# 2. Authoritative Baseline

The current documents being corrected are:

- `docs/execution-plan.md` — v1.0.4
- `docs/execution-plan-p0-steps.md` — companion runbook based on v1.0.4

The following v1.0.4 decisions are already correct and MUST be preserved unchanged unless a wording change is required solely to eliminate one of the inconsistencies listed in this prompt:

- 6 P0 capabilities
- 8 lifecycle sub-phases A–H per capability
- 48 atomic `EXE-P0.<n>.<letter>` execution units
- 6 Capability Gates
- 54 total gated checkpoints
- Capability Gate is **not** a ninth implementation sub-phase
- Capability Gate has no `EXE-P0.<n>.<letter>` ID
- Capability Gate has no implementation commit
- strict sequential execution
- one atomic unit at a time
- AI Verification before human approval
- human approval after acceptable AI Verification
- no approval request after `AI VERIFICATION: BLOCKED`
- AI Verification never substitutes for human approval
- Capability Gate AI Verification before human Gate approval
- no automatic chaining
- N/A sub-phases receive `AI VERIFICATION: NOT APPLICABLE`, require explicit human acknowledgment, and receive no fake commit
- `REM-P0.1.A-01` and `REM-P0.1.A-02` remain history-specific remediation units and remain outside the 48/54 counts
- shared-core foundation ownership remains with Capability 1 / `EXE-P0.1.A`
- `SOURCE-GAP-EXECPLAN-04` remains the blocking implementation-side gap until remediation is completed
- `EXE-P0.2.B` remains blocked until its guard passes
- `EXE-P0.3.B` retains the corresponding shared-core precondition
- historical commits remain unchanged
- `c3d6ecf` remains non-implementation history
- all six ADRs remain `PROPOSED`
- Observability remains outside this execution slice
- P1–P5 remain non-atomized in this document
- no implementation code is to be created by this correction

---

# 3. Problems to Correct

The current v1.0.4 execution plan still contains several stale or contradictory statements.

Correct all of the following.

---

## 3.1 Fix §7 Architecture-to-Implementation Mapping

Current wording:

> Every `EXE-P0.<n>.<letter>` unit in §18 below maps 1:1 to a lifecycle sub-phase (A–H + Gate)

This is incorrect because the Capability Gate is not an `EXE-P0.<n>.<letter>` unit and is not a lifecycle sub-phase.

Replace it with wording that states exactly:

> Every `EXE-P0.<n>.<letter>` unit in §18 below maps 1:1 to one lifecycle sub-phase A–H.

Then explicitly preserve the separate Gate distinction:

> The Capability Gate is a separate post-lifecycle review/promotion checkpoint and is not an `EXE-P0.<n>.<letter>` execution unit.

Do not introduce any new execution unit or change the counts.

---

# 4. Fix §18.2 Field-Compaction Wording

The current text says that invariant fields are repeated across:

> all nine of its sub-phase entries

This is incorrect.

Replace the relevant wording so that it clearly states:

> fields that do not vary sub-phase-to-sub-phase are stated once per capability in the capability header rather than repeated across all eight lifecycle sub-phase entries A–H. The Capability Gate is a separate post-lifecycle review/promotion checkpoint and is not one of the eight lifecycle sub-phase entries.

Do not describe the Gate as a ninth sub-phase.

Ensure the resulting section consistently uses:

- **8 lifecycle sub-phases**
- **A–H**
- **separate Capability Gate**
- **48 atomic execution units + 6 Gates = 54 gated checkpoints**

Do not introduce any wording that implies 54 atomic execution units.

---

# 5. Remove Ambiguous Shorthand Execution Semantics

## 5.1 Canonical syntax must be the normative syntax

In §18.11.2, make this command form the sole normative execution syntax:

```text
Execute EXE-P0.<n>.<letter>
```

Examples remain:

```text
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

```text
Approve EXE-P0.<n>.<letter>
```

For Capability Gates:

```text
Approve CAPABILITY-GATE P0.<n>
```

For Gate review:

```text
Review CAPABILITY-GATE P0.<n>
```

## 5.2 Remove the ambiguous shorthand rule

The current wording:

> the shorthand remains valid but always resolves to the capability's next unexecuted `.A`-equivalent unit

is ambiguous and must be removed.

Do not define any context-sensitive resolution such as "next unexecuted", "current step", ".A-equivalent", or similar behavior.

The document must instead state:

> The canonical `Execute EXE-P0.<n>.<letter>` syntax is mandatory for Mode B execution. Do not use `Execute P0.<n>` as an execution command.

Similarly, do not retain any statement implying that a capability-only command can dynamically resolve to a different sub-phase depending on execution history.

If the historical v1.0.3 shorthand discussion must be retained for correction-history purposes, clearly label it as **historical behavior superseded by the canonical command protocol**. It must not remain an active command rule.

---

# 6. Correct the Final Readiness Contradiction

This is the most important correction.

The body of the plan correctly records:

`SOURCE-GAP-EXECPLAN-04`

as an implementation-side blocker for `EXE-P0.2.B` and `EXE-P0.3.B` until the remediation units are completed.

The final readiness section still incorrectly says:

> No blocking issue was found. This plan is ready for Mode B...

That statement must be removed/replaced.

The final readiness conclusion must distinguish:

### PLAN READY

The execution-plan document itself is structurally ready as a planning artifact after this v1.0.5 correction.

### MODE B READY

The repository is **not currently clear to proceed to `EXE-P0.2.B`**, because:

1. `REM-P0.1.A-01` is not completed and approved.
2. `REM-P0.1.A-02` is not completed and approved.
3. Capability 1 Gate approval is not recorded.
4. `EXE-P0.2.B` remains blocked by the shared-core precondition until the above remediation and Gate conditions are satisfied.

Use an explicit statement equivalent to:

> **PLAN READY: YES — after this v1.0.5 correction.**
>
> **MODE B READY for `EXE-P0.2.B`: NO — `SOURCE-GAP-EXECPLAN-04` remains open on the implementation side until `REM-P0.1.A-01` and `REM-P0.1.A-02` are AI-verified and human-approved and Capability Gate P0.1 is approved.**

Do not write any unconditional statement that the plan is "ready for Mode B" while this blocker remains.

Do not reinterpret the blocker as an upstream architecture problem; the plan already correctly identifies it as an execution-plan ownership gap exposed by implementation history.

---

# 7. Correct Final Validation Summary

Update the final validation summary so it reflects the fourth source-gap entry.

The current summary incorrectly says, in effect:

> three new non-blocking source gaps recorded

That is no longer sufficient.

The corrected summary must distinguish:

- `SOURCE-GAP-EXECPLAN-01` — non-blocking
- `SOURCE-GAP-EXECPLAN-02` — non-blocking
- `SOURCE-GAP-EXECPLAN-03` — non-blocking
- `SOURCE-GAP-EXECPLAN-04` — **blocking implementation-side gap until remediation**

Do not change the descriptions or dispositions of gaps 01–03.

Do not mark gap 04 resolved merely because the execution plan now correctly assigns ownership and adds a guard.

Its correct status remains:

> **Plan side resolved; implementation side open until remediation completes.**

---

# 8. Correct the Final Execution Plan Readiness Section

Rewrite only the final readiness conclusion necessary to make the document internally consistent.

It must explicitly confirm:

1. technology baseline validation remains unchanged;
2. P0 contains 48 atomic execution units plus 6 Capability Gates;
3. the Gate is separate from A–H;
4. strict sequential execution remains unchanged;
5. AI Verification precedes human approval;
6. human approval is explicit;
7. no automatic chaining exists;
8. `SOURCE-GAP-EXECPLAN-04` remains the current implementation blocker;
9. the current repository position is preserved;
10. this correction is documentation-only.

The final readiness language must **not** imply that implementation can immediately resume at `EXE-P0.2.B`.

---

# 9. Add Version 1.0.5 Correction History

Update the document header:

```text
Version: 1.0.5
```

Extend the correction history with a new entry:

> **v1.0.4 → v1.0.5** — surgical correction to eliminate remaining internal inconsistencies after post-v1.0.4 verification. Corrected the architecture-to-implementation mapping so only A–H are lifecycle sub-phases; corrected the §18.2 field-compaction wording from nine entries to eight lifecycle entries plus a separate Gate; retired ambiguous capability-level shorthand execution semantics in favor of the mandatory canonical `Execute EXE-P0.<n>.<letter>` syntax; corrected final validation/readiness language to recognize `SOURCE-GAP-EXECPLAN-04` as the current implementation-side blocker; and aligned the companion operator runbook to v1.0.5. No scope, architecture, requirements, technology baseline, ADR status, P0 capability definition, execution counts, remediation mechanism, or implementation code was changed.

Do not delete the historical v1.0.1–v1.0.4 correction history.

Historical self-checks may retain their original historical conclusions, but they must not be misrepresented as the current v1.0.5 readiness conclusion.

---

# 10. Add a v1.0.5 Self-Check

At the end of `docs/execution-plan.md`, add a new:

```text
v1.0.5 surgical-correction self-check
```

It must verify all of the following:

1. Version updated to `1.0.5`.
2. §7 states that `EXE-P0.<n>.<letter>` maps only to lifecycle A–H.
3. Capability Gate is explicitly separate and not a ninth sub-phase.
4. §18.2 states eight lifecycle sub-phase entries A–H plus separate Gate.
5. No active command rule uses "next unexecuted `.A`-equivalent" semantics.
6. Canonical execution syntax is `Execute EXE-P0.<n>.<letter>`.
7. No automatic chaining was weakened or removed.
8. 48 atomic `EXE-P0` units + 6 Capability Gates = 54 gated checkpoints remains unchanged.
9. AI Verification before human approval remains unchanged.
10. Gate AI Verification before human Gate approval remains unchanged.
11. N/A handling remains unchanged.
12. `REM-P0.1.A-01` / `REM-P0.1.A-02` remain outside the 48/54 counts.
13. `SOURCE-GAP-EXECPLAN-04` remains a blocking implementation-side gap until remediation closes it.
14. Final readiness no longer says "No blocking issue remains."
15. Final readiness clearly distinguishes PLAN READY from current MODE B readiness for `EXE-P0.2.B`.
16. No implementation code or unrelated files were modified.
17. The companion runbook was aligned to v1.0.5.

Finish with an explicit v1.0.5 completion statement reflecting the actual state, not an unconditional "ready for Mode B" statement.

---

# 11. Update the Companion Runbook

Modify:

```text
docs/execution-plan-p0-steps.md
```

to align it with `execution-plan.md` v1.0.5.

## 11.1 Header

Change:

> Based on: `docs/execution-plan.md` v1.0.4

to:

> Based on: `docs/execution-plan.md` v1.0.5

Update the change summary to include the v1.0.5 correction.

---

## 11.2 Canonical Commands

The runbook must state that the canonical execution command is:

```text
Execute EXE-P0.<capability>.<sub-phase>
```

and must explicitly instruct the operator to use the full canonical ID.

Do not retain an active shorthand such as:

```text
Execute P0.1
```

as a dynamically resolved execution command.

Use canonical IDs throughout the operational sequence.

---

## 11.3 Preserve the Two-Stage Checkpoint

Do not weaken this existing model:

```text
Execute
   ↓
Claude Code implementation
   ↓
AI Verification
   ↓
AI VERIFIED — AWAITING HUMAN APPROVAL
   ↓
Human reviews evidence
   ↓
Approve
```

For blocked work:

```text
Execute
   ↓
AI Verification
   ↓
AI VERIFICATION: BLOCKED
   ↓
No approval requested
   ↓
Remediation
   ↓
New explicit Execute command
```

Keep this unchanged.

---

## 11.4 Preserve the Separate Capability Gate

Keep:

```text
Human Approval of H
   ↓
Capability Gate Review
   ↓
AI Gate Verification
   ↓
Human Gate Approval
   ↓
Next capability requires its own Execute command
```

Do not convert the Gate into a ninth sub-phase.

---

## 11.5 Update Runbook Current Version References

The runbook may continue to describe historical repository state as originating under v1.0.4 where that is factually correct, but its governing baseline must clearly be:

> `execution-plan.md` v1.0.5

Do not rewrite historical commit provenance merely to make the dates look newer.

---

## 11.6 Current Position Must Remain Explicit

The operational current position is:

```text
EXE-P0.1.A–H:
Historical execution completed as previously recorded,
with F = NOT APPLICABLE and no commit.

REM-P0.1.A-01:
NOT STARTED

REM-P0.1.A-02:
NOT STARTED

Capability Gate P0.1:
NOT APPROVED / approval not recorded

EXE-P0.2.A:
EXECUTED + APPROVED

EXE-P0.2.B:
BLOCKED — shared-core prerequisite not satisfied
```

The runbook must clearly identify:

```text
NEXT REQUIRED COMMAND

Execute REM-P0.1.A-01
```

Do not instruct the operator to restart `EXE-P0.1.A`.

Do not instruct the operator to execute `EXE-P0.2.B` before remediation and Capability 1 Gate approval.

---

# 12. Do Not Change the Following

Do not alter:

- P0 capability names
- P0 capability order
- lifecycle A–H definitions
- 48 + 6 = 54 counts
- AI Verification checklist
- AI Verification verdict vocabulary
- human approval requirement
- Capability Gate protocol
- remediation IDs
- `SOURCE-GAP-EXECPLAN-04` description
- ADR statuses
- technology baseline
- architecture mapping other than the exact §7 correction
- package placement findings
- tenant isolation rules
- security rules
- observability exclusion
- P1–P5 disposition
- historical commit IDs
- implementation source code
- test code
- `CLAUDE.md`
- `.vscode/`
- any unrelated documentation

No implementation commit is to be created by this correction.

---

# 13. Mandatory File-Scope Verification

Before completing the correction, verify:

```text
git status --short
git diff --stat
git diff -- docs/execution-plan.md
git diff -- docs/execution-plan-p0-steps.md
```

The only intended modified files are:

```text
docs/execution-plan.md
docs/execution-plan-p0-steps.md
```

No source code may be changed.

No file under `control_plane/` may be created or modified.

No upstream architecture, requirements, ADR, or other authoritative document may be modified.

---

# 14. Mandatory Text Consistency Verification

After editing, search the two corrected files for these stale phrases/concepts:

```text
A–H + Gate
nine of its sub-phase entries
next unexecuted `.A`-equivalent
No blocking issue remains; this plan is ready for Mode B
No blocking issue was found
54 atomic execution units
Execute P0.1
based on execution-plan.md v1.0.4
```

For each match:

- remove it if it represents active current guidance;
- preserve it only when it is clearly historical correction-history text and cannot be mistaken for the current rule.

Also verify that the current operational guidance consistently uses:

```text
Execute EXE-P0.<n>.<letter>
Approve EXE-P0.<n>.<letter>
Review CAPABILITY-GATE P0.<n>
Approve CAPABILITY-GATE P0.<n>
```

---

# 15. Final AI Verification Required

After completing the correction, perform a documentation-level AI verification before reporting completion.

The verification must confirm:

### A. Scope Verification
Only the two authorized files changed.

### B. Version Verification
Both artifacts correctly reference execution-plan v1.0.5 as the current baseline.

### C. Lifecycle Verification
A–H are the only lifecycle sub-phases.

### D. Gate Verification
Capability Gate is separate from A–H and has no `EXE-P0` ID or implementation commit.

### E. Command Verification
Canonical commands are explicit and unambiguous.

### F. Approval Verification
AI Verification precedes human approval.

### G. Blocking Verification
`SOURCE-GAP-EXECPLAN-04` remains correctly marked as an implementation-side blocker.

### H. Current-State Verification
The next required action is remediation, not `EXE-P0.2.B`.

### I. Count Verification
48 atomic execution units + 6 Gates = 54 gated checkpoints.

### J. Historical Verification
Historical commits, remediation history, and earlier correction records remain intact.

Report the result as:

```text
AI VERIFICATION: PASS
```

or

```text
AI VERIFICATION: BLOCKED
```

Do not report PASS if any of the above checks fails.

---

# 16. Required Final Response

After the documentation correction and verification, respond with:

```text
CORRECTION EXECUTED: v1.0.5

Files Modified:
- docs/execution-plan.md
- docs/execution-plan-p0-steps.md

AI VERIFICATION REPORT

1. Scope Verification:
2. Version Verification:
3. Lifecycle Verification:
4. Capability Gate Verification:
5. Command Verification:
6. Approval Workflow Verification:
7. Blocking-State Verification:
8. Current-Position Verification:
9. Count Verification:
10. Historical-Integrity Verification:

AI VERIFICATION: PASS / BLOCKED

Blocking Issues:
<none or explicit issue>

Current execution position:
NEXT REQUIRED COMMAND: Execute REM-P0.1.A-01
```

Then stop.

Do not create an implementation commit.

Do not execute any `EXE-P0` or `REM-P0` implementation unit as part of this correction.

Do not ask for approval to continue implementation merely because this documentation correction passes.

Human approval of the documentation correction and execution of remediation remain separate actions.
