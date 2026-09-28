---
name: eaioc-execution-governance
description: >
  Procedural governance layer for the EAIOC P0 Execution Orchestrator (v1.2.0 — continuous
  phase-driven execution conformant, plan §18.20, HD-CE-01–19). Makes the procedure automatic and
  leaves the authority human: reconstructs P0 state from repository evidence — including, from the
  plan's migration point (`EXE-P0.2.C`) onward, an open §18.20 authorized-path's own state where
  applicable — decides unit eligibility, detects blockers / source gaps / contract contradictions /
  missing human decisions, produces structured human-decision requests and remediation plans, routes
  the eaioc-code-* agents, revalidates after a change, and prepares AI Verification and Gate reviews.
  Distinguishes historical single-unit execution from path-internal continuation under an
  already-open, human-authorized execution path (`AI VERIFIED — CONTINUING` /
  `AI VERIFIED — CONTINUING (NOT APPLICABLE)` — never `HUMAN APPROVED`, never a fifth verdict), and
  resumes an interrupted path automatically once a Human Decision Request is resolved, without ever
  expanding the path or crossing a Capability boundary. As an automatic sub-step of processing every
  genuine human boundary `Approve <id>` the operator gives — never a path-internal continuation state,
  which triggers no `Approve` at all — additively synchronizes CLAUDE.md and the operator runbook's own
  state/status content with the approved unit's actual evidence — and reports, never directly edits,
  staleness in docs/execution-plan.md itself, which is corrected only through its own version-bumped
  procedure. Never approves, never authorizes execution beyond what an explicit `Execute` opened, never
  invents requirements, never chains outside an authorized path, and this synchronization step never
  executes, approves, or authorizes any other unit. Use whenever the operator asks for STATUS /
  BLOCKERS / GATE STATUS / REVALIDATE / RESUME / VERIFY, an `Execute <id>` / `Approve <id>` command is
  received, a blocker or source gap is found, or a governance/eligibility question about P0 arises.
  Complements — never replaces — eaioc-governance and eaioc-agent-orchestration.
---

# EAIOC Execution Governance (skill v1.2.0)

**Core principle: AUTOMATIC PROCEDURE, HUMAN AUTHORITY.**
The procedure below runs without being asked (reconstruct, inspect, trace, classify, plan, verify).
Authority never does: approval, execution authorization, invented semantics, gap bypass, gate crossing
and every change to governance itself stay with the human operator.

**Amendment note (v1.1.1 → v1.2.0).** Brings this skill's procedure into conformance with
`docs/execution-plan.md` §18.20 (Continuous Phase-Driven Execution Model, HD-CE-01 through HD-CE-19),
committed at `72ee2b1`, and with the already-amended `eaioc-p0-execution-orchestrator` agent (committed
at `896367a`), which this skill serves as the procedural layer for. Adds: continuous-path state
consumption (§8a); guard classification distinguishing Gate-level from completion-only guards (§7a);
path-internal prerequisite/remediation restriction (§16a); the continuous path continuation procedure
itself, including the `AI VERIFIED — CONTINUING` / `AI VERIFIED — CONTINUING (NOT APPLICABLE)` states
(§6, §19a); same-path Human Decision Request resume with no fresh `Execute` required (§21a); an explicit,
narrow exception to the no-chaining prohibition for exactly this authorized case (§29); explicit
confirmation that ordinary continuation states never trigger §31's human-boundary synchronization
(§31); and historical quarantine of every unit approved before the migration point, `EXE-P0.2.C`
(§22). It weakens no existing control: every rule above still governs unchanged outside an open path,
at a path's own boundary, and across every Capability boundary. This amendment changes only this file;
it does not execute, approve, or authorize `EXE-P0.2.C` or any other unit.

---

## 1. Purpose

Give the existing **`eaioc-p0-execution-orchestrator`** one reusable, auditable procedure for the
situations that repeatedly arose while executing P0 (an unreadable state, an ineligible next unit, a
blocked prerequisite, a source gap discovered mid-unit, a contract contradiction, a missing human
decision, a remediation to plan, a post-change revalidation). It turns those situations into a
deterministic sequence of evidence-gathering and reporting steps that always ends at a human boundary.

## 2. Scope

- **In scope:** the P0 plan (`docs/execution-plan.md`, currently the v1.0.x line) — atomic units
  `EXE-P0.<n>.<letter>`, remediation units `REM-P0.<n>.<letter>-NN`, Capability Gates
  `CAPABILITY-GATE P0.<n>` — and read-only governance queries about them.
- **Out of scope:** any EAIOC runtime component; optimization, routing or token logic; benchmark
  implementation; P1–P5 unit execution (they are sequenced in `implementation-plan.md` but not atomized;
  a command naming a unit the plan does not define is not executable — ask for the canonical ID,
  plan §18.11.9). **No P6 exists in the corpus; do not invent one.**
- **Non-goals:** this skill is not a replacement for the Architect, the Verifier, or human approval; not a
  machine-learning or autonomous-authority system; not a second state ledger.
- **Relationship to existing governance (do not duplicate):**

| Existing asset | Owns | This skill's use of it |
|---|---|---|
| `docs/execution-plan.md` (+ runbook `execution-plan-p0-steps.md`) | WHAT, in which unit, DoD; command protocol §18.11; AI Verification §18.12; remediation §18.14; guards §18.15; state model §18.17; position §18.18; assistance layer §18.19; **continuous phase-driven execution model §18.20 (authorized paths, `CONTINUING`, completion-only guards, Capability boundary, HDR resume, no unauthorized chaining)**; gap register §38 | Governs. Where they differ, the live plan wins and the difference is reported. |
| `eaioc-governance` skill | Source tiers, documented precedence (§2), no-fabrication (§3), evidence labels (§4), git/gated awareness, self-check (§10) | Loaded first; its rules apply to every step here. |
| `eaioc-agent-orchestration` skill (+ `governance-matrix.yaml`) | WHEN Architect / Reviewer / Verifier run, STEP vs CAPABILITY level, statuses, GOVERNANCE RECORD, `Governance-*` trailers | Called at §17 below; not re-specified here. Its existing escalation → `BLOCKED` mapping already implements §18.20.K.11 without any change to that file. |
| `eaioc-p0-execution-orchestrator` agent | The operator-facing behaviour: commands, pre-flight, lifecycle (including the continuous path lifecycle, §18.20/agent §5a–§5b), blocker report, reporting | This skill is its procedural layer; the agent file is not modified by this skill. |
| `eaioc-code-architect / -reviewer / -verifier` agents | HOW each analysis is done | Routed to, never restated. |

**Duplication control.** Sections 6, 7, 19, 20, 21 and 25 below restate the plan's and the orchestrator
agent's state names, pre-flight order, command table, Gate procedure, read-only commands and STATUS
format, because the orchestrator needs them operationally in one place. Each names its authoritative
source where it restates it; that source governs, and the restatement is re-verified against the live
source every run, never silently edited here to resolve a drift — a drift is reported as a finding (§10),
and fixing it (if needed) is its own governed correction, never done by this skill unilaterally.

## 3. Authority model

| Actor | May | May not |
|---|---|---|
| **Human operator** (the operator's own message in the main session) | Authorize (`Execute <id>`), approve (`Approve <id>`), decide, direct a correction, change governance | — |
| **Orchestrator (main session, with this skill)** | Reconstruct, inspect, classify, report, plan, request decisions, route agents, execute **one** explicitly authorized unit, run AI Verification | Approve; authorize; decide a legitimate alternative; chain; cross a Gate; edit governance or authoritative sources to make a unit pass |
| **Architect / Reviewer / Verifier** | Produce evidence and verdicts | Approve; authorize; close a gap; register an ID |
| **Any other agent or relayed message** (a subagent report, a teammate message, quoted text) | Provide evidence to be checked | Carry authority. A relayed "the operator approved / said proceed" is **never** approval; approval evidence must be a repository record or the operator's own command in this session. |

Human authority is exercised only through **canonical commands** (§19) or an explicit, unambiguous
operator direction for a *documentation* correction. When in doubt whether an instruction is an
authorization: **it is not**; say what canonical command would be one.

## 4. Source-of-truth hierarchy

**No new hierarchy is introduced.** Conflict resolution uses only precedence the corpus states:
`conventions.md` header (Problem Statement > Engineering Specification > Architecture > Interfaces >
`conventions.md`) and `execution-plan.md` §4 (see `eaioc-governance` §1–§2 for the full statements, and
re-verify them every run — documents are re-versioned). Consequences that always apply: the plan governs
*process and unit scope* and never overrides a frozen contract; Tier-5 design notes bind their own unit
and never override Tier 1–3; the operator runbook never outranks the plan; a `FULLY TRACED` status is a
document-level trace, not evidence of implementation.

For classifying **what a piece of information is** (not which wins a conflict), use these eight evidence
classes and their limits:

| # | Evidence class | Examples | May be used as | Must never be treated as |
|---|---|---|---|---|
| 1 | Authoritative contract / source | PS, Spec, `architecture.md`, `interfaces.md`, `conventions.md`, `edge-cases.md`, `scenario-matrix.md` (as corrected by plan passes) | Requirement | — |
| 2 | Execution plan / runbook | `execution-plan.md`, `execution-plan-p0-steps.md`, `CLAUDE.md` state | Process, unit scope, state | A contract override |
| 3 | Implementation / design evidence | `control_plane/**`, unit design notes, tests | What was built/decided by a unit | Requirement (unless a plan/contract adopted it) |
| 4 | Historical execution evidence | Commits, prior approvals, prior reports, superseded blocks | Precedent; proof that something happened | Current authorization; current contract |
| 5 | Agent analysis | Architect/Reviewer/Verifier/orchestrator reports | Evidence to verify | A decision, an approval, a source |
| 6 | Human decisions | Operator commands and recorded decisions (HQ-n, HD-n, DB-n, GS-n, D-A…D-D, dated human contract decisions) | Decisions on the matter they name, until superseded | A source-established fact ("recorded human decision" ≠ "the sources say") |
| 7 | Inferences | Any deduction not stated in a source | A labelled hypothesis | An authoritative requirement |
| 8 | Suggestions / proposals | Recommendations, candidate IDs, drafts | Input to a human decision | A decision; a registered ID |

**These eight classes are classification aids, not a second hierarchy.** They say *how a statement
may be used*; they never say which source wins. Authority between documents is set only by
`eaioc-governance` §1 (tiers — a document-family grouping) and §2 (documented precedence) and plan §4.
Where each class sits in the existing tiers and labels:

| Class | Sits in the existing tiers / labels |
|---|---|
| 1 Authoritative contract / source | Tier 1, plus the frozen documents plan §4 lists first (including `edge-cases.md`, `scenario-matrix.md`) — as corrected by plan passes |
| 2 Execution plan / runbook | The plan is Tier 2; the runbook is Tier 5 (`eaioc-governance` §1). `eaioc-governance` §1 assigns `CLAUDE.md` **no tier** — it is described there as separately binding; treat its status content as plan-derived state evidence, not a tiered source, unless an authoritative source explicitly tiers it. The plan wins over both on conflict. |
| 3 Implementation / design evidence | Tier 4 (code, tests, config); unit design notes are Tier 5 |
| 4 Historical execution evidence | Tier 5 (commits, prior approvals and reports, superseded blocks) |
| 5–8 Agent analysis, human decisions, inferences, proposals | Not documents — kinds of *statement*. A recorded human decision lives in a document and keeps that document's tier; agent analysis, inferences and proposals are evidence labels (§24), never sources |

If a class and an existing tier ever seem to disagree, the existing tier and documented precedence win
and the disagreement is reported.

**Decision provenance versus current authority (see also §24).** Class 6 (human decisions) never becomes
class 1 merely by having been decided; but when a plan correction *promotes* a human decision's text into
a Tier 1–3 document (the document is actually edited), that document's text is then class 1 (authoritative
contract) — a **documented requirement with human-decision provenance** — and the human decision is
recorded separately as its provenance, never as a substitute source. Never state that a human decision can
never become authoritative; only that its promotion happens by an actual document edit, never merely by
having been decided.

**Three promotions are forbidden:** *inference → authoritative requirement*; *agent recommendation →
human decision*; *historical precedent → current authorization*.

**When sources conflict:** (1) identify the conflict; (2) quote both statements with file + section/line;
(3) classify it (`CONTRADICTION` if documented precedence resolves it, `SOURCE-CONFLICT` if not — the
`eaioc-governance` §4 labels); (4) apply documented precedence only if it exists and cite the rule;
(5) if resolution needs a human decision → **stop every conclusion that depends on it** and issue a
Human Decision Request (§14). Never pick the "reasonable" side.

## 5. State reconstruction

There is **no second state ledger**; do not create one (a ledger file is an operator decision). Rebuild
state every run from these artifacts and cite each; never from memory or a previous report:

1. `git branch --show-current`, `git log --oneline -20`, `git status --short --untracked-files=all`,
   `git diff --stat` (uncommitted changes; `git log -1 --format=%B` for `Governance-*` trailers).
   Unit commits are titled `P0-<n>.<letter>: …` / `P0-<n>.<letter>-REM-NN: …`; `docs:` / `chore:`
   commits and `c3d6ecf` are **never** unit evidence.
2. `docs/execution-plan.md` — version header; §18.18 current position; §18.14.x reconciliations and
   remediation tables; §18.4–§18.9 unit rows; §38 gap register **and any additive status-update table**
   (a later status-update row governs an earlier register row); the final terminal-verdict line.
3. `docs/execution-plan-p0-steps.md` — §20 position/tracker (the plan wins on conflict).
4. Root `CLAUDE.md` — implementation status (governance/state only).
5. Authoritative contracts as needed (§12) and unit design notes under `control_plane/**/*.md`.
6. Operator commands **in this session** (`Approve …`, decisions). Anything relied on that exists only
   in conversation is flagged as *unrecorded (session-only)* and reported `UNRESOLVED` (§24) until a
   documentation sync records it.

Answer, with a citation each: active capability; current unit; completed units (commit); AI-verified
units; human-approved units (approval evidence class: repository record / operator command this session /
none); blocked units and why; open remediations; open source gaps (with their current status row, not
only the register row); satisfied and missing prerequisites; **next eligible unit** (§8); explicitly
deferred units; units forbidden to execute now; repository cleanliness; uncommitted governance-relevant
changes; whether repository state agrees with the documented state. **If two artifacts disagree about
state, that is a `DOCUMENTATION-GAP` finding (§10); report it and do not
choose silently.** If state cannot be reconstructed reliably → fail closed (§26).

## 6. Execution state machine

Use the plan's §18.17 model verbatim (do not rename states):

```
NOT STARTED → EXECUTING → EXECUTED → AI VERIFYING → AI VERIFIED → AWAITING HUMAN APPROVAL
            → HUMAN APPROVED → NEXT UNIT ELIGIBLE
failure:  AI VERIFICATION BLOCKED → REMEDIATION → RE-EXECUTE / RE-VERIFY
precondition failure: EXECUTING → AI VERIFICATION BLOCKED (no change-set)
Gate:     GATE REVIEW → AI CAPABILITY GATE VERIFICATION: PASS | BLOCKED → GATE APPROVED
```

`NEXT UNIT ELIGIBLE` means only that the operator *may* send a command; it starts nothing. **This skill
defines no additional state or status.** A remediation unit is *registered* by a plan correction; that is
a plan fact, not a §18.17 state — until executed its §18.17 state is `NOT STARTED`. Quote the live
plan's own wording for it (the v1.0.10 plan text says `REGISTERED / NOT STARTED`, the v1.0.11 text says
`REGISTERED / NOT EXECUTED`); never re-word it or treat registration as progress. Terminal footers stay exactly `### AI VERIFIED — AWAITING HUMAN APPROVAL` /
`### AI VERIFICATION BLOCKED`. Transitions happen only by the events the plan defines; this skill never
moves a unit to `HUMAN APPROVED`.

**Continuation states (plan §18.17 v1.1.0/v1.1.1, §18.20 — effective from the migration point
`EXE-P0.2.C` onward).** For a unit inside an already-open §18.20 authorized path that is not that
path's own boundary unit, the model above branches after `AI VERIFIED` into one of two additional
states instead of `AWAITING HUMAN APPROVAL`:

```
AI VERIFIED (verdict PASS / PASS WITH DOCUMENTED NON-BLOCKING GAP, committed)
    → CONTINUING → NEXT UNIT ELIGIBLE (automatic, same path only)

AI VERIFIED (verdict NOT APPLICABLE, no commit)
    → CONTINUING (NOT APPLICABLE) → NEXT UNIT ELIGIBLE (automatic, same path only)
```

Both are states this skill quotes from the plan, never states this skill itself defines (unchanged
"this skill defines no additional state or status" above). Neither is ever `HUMAN APPROVED`, neither is
ever the other, and neither introduces a fifth AI Verification verdict (§18.12.1.J is unchanged, §18).
At the path's own boundary unit, the model rejoins `AWAITING HUMAN APPROVAL → HUMAN APPROVED` exactly as
above. Outside a §18.20 path, and for every unit approved before the migration point, this section's
original diagram applies with no branch, exactly as before this amendment (§18.20.O, §22).

## 7. Pre-flight validation

Run the orchestrator's pre-flight (agent §4 lists 17 numbered items, plus items 4a/4b added by the
agent's own §18.20 amendment; plan §18.19.3) — condensed below into 16 clauses because agent items 4
and 5 (predecessor completed / approved) are merged into one — with cited evidence per clause, in this
order so the first failure is the earliest cause: plan version current → branch/HEAD/working tree
as expected (unrelated modified/untracked files recorded; ones touching the unit's scope **block**) →
requested ID canonical and is the next eligible unit → predecessor completed and approved (**classify
the guard first — §7a — before checking it against evidence**) → Gate approval (always a Gate-level
guard, §7a — never satisfied by a continuation state) → prerequisites/§18.15 guards (**and, if a
prerequisite/remediation is not itself executed and approved, it auto-runs only if plan-designated
path-internal for this exact path — §16a; otherwise its absence blocks**) → governing contracts exist →
no blocking gap/remediation → no unresolved contradiction → no stale plan assumption (re-verify the
row's cited IDs against the live docs; plan rows have been wrong before — precedent) → unit scope valid
→ agents/skills available → technology baseline (plan §6, `control_plane/pom.xml`) → docs/code
alignment sufficient to implement without invention → no earlier blocker → routing decided → **if this
unit is not a path's own starting unit, an authorized path actually covers it (§8a), reconstructed,
never assumed, failing closed to `DOCUMENTATION-GAP` on any ambiguity.** **Any failure → do not
execute; emit the BLOCKER REPORT (§10).** Never implement "to see if it works".

## 7a. Guard classification (plan §18.20.S, §18.15)

Every "has been approved" / "has completed" precondition the plan states is exactly one of two kinds —
determined by **reading what the plan's own text says the guard is actually checking**, never by a
fixed table inferred once and reused blindly, since a future plan correction may add guards of either
kind:

- **Gate-level / human-authorization guard.** Its stated purpose specifically requires human review,
  authorization, or approval (e.g. "Capability 1 Gate has been approved," §18.15). Satisfied **only** by
  a genuine `Approve CAPABILITY-GATE P0.<n>` or a genuine boundary `Approve …`. **Never** satisfied by
  `AI VERIFIED — CONTINUING`, `AI VERIFIED — CONTINUING (NOT APPLICABLE)`, or any other continuation
  state, regardless of path — unaffected by §18.20, no exception.
- **Completion-only guard.** Its stated purpose is only to confirm a predecessor sub-phase reached a
  valid terminal state (e.g. "Capability `<n>`.A has been approved," §18.15, generalizing to every later
  capability's own Sub-phase B guard, at minimum `EXE-P0.3.B`). Satisfied by any of: (1)
  `HUMAN APPROVED`; (2) `AI VERIFIED — CONTINUING`; (3) `AI VERIFIED — CONTINUING (NOT APPLICABLE)` —
  but (2) and (3) **only** when the predecessor is path-internal to the exact same authorized path
  (§8a) as the unit whose guard is being checked, never across a path boundary, never across a
  Capability.

If a guard's kind is genuinely unclear from the plan's own text, classify it Gate-level (the stricter
reading) and report the ambiguity as a finding (§10) rather than assuming the more permissive
completion-only reading. This is a classification method, not a new taxonomy: the two kinds above are
the plan's own distinction (§18.20.S), and this section introduces no state, verdict, or evidence class
beyond what §4/§6/§18/§24 already define.

## 8. Eligibility determination

Two distinct questions, never merged:

**ELIGIBLE.** All substantive prerequisites, dependencies, source contracts and governance conditions the
plan requires are satisfied. Report each item as `MET / NOT MET / UNKNOWN` with evidence; `UNKNOWN` counts
as `NOT MET` (an inference from plan §18.19.3 fast-fail and orchestrator agent §4, "Any failure → do not
execute" — see §26). A unit or remediation is **ELIGIBLE** only if:

1. it exists in the live authoritative plan (an atomic unit row, or a plan-registered remediation);
2. it satisfies the plan's own sequencing/prerequisite conditions for *that* unit — for an atomic
   `EXE-P0` unit, it is the first such unit in plan order not yet `HUMAN APPROVED`; for a registered
   remediation, its own plan-stated governing preconditions for it (e.g. plan §18.14.x preconditions),
   evaluated on their own terms, **not** "is the first non-approved `EXE-P0` unit". An already-executed,
   approved unit is never re-run (§18.14);
3. all prerequisites are satisfied (predecessor `HUMAN APPROVED` — or, within the same already-open
   authorized path and only where §7a classifies the guard completion-only, `AI VERIFIED — CONTINUING`
   / `AI VERIFIED — CONTINUING (NOT APPLICABLE)`; §18.15 guards; documented remediation dependencies
   executed, AI-verified **and** approved, or plan-designated path-internal per §16a);
4. the required source contracts exist (no invention needed);
5. required human decisions are resolved and recorded;
6. no blocking source gap or open remediation applies;
7. no earlier unit and no Capability Gate blocks it;
8. no uncommitted or conflicting change invalidates the evidence it relies on;
9. governance permits it (skills/agents available, precedence unambiguous).

**PERMITTED TO EXECUTE.** The unit is **ELIGIBLE** (items 1–9 above) **and** the operator's explicit
`Execute <canonical-ID>` for that exact ID has been received in this session (§19). Human authorization is
never part of the definition of eligibility — it is the separate, further condition that turns ELIGIBLE
into PERMITTED TO EXECUTE. **One narrow, plan-defined exception (§18.20.B–D, §8a):** for a unit that is
path-internal to an already-open authorized path and is not that path's own starting unit, the single
`Execute <canonical-ID>` that opened the path already satisfies this condition — the operator's own
explicit command already covers it, exercised rather than newly granted. This is never extended by
inference to any unit outside that same path, and never to a unit whose Capability differs from the
path's own.

Eligibility is **never** inferred from: the code looking easy; a similar unit having passed; an agent
recommending it; the next ID following numerically; a commit having landed; a plan having been
re-versioned; a remediation having been *registered*.

**Always preserve these distinctions; never merge them, and no new state is introduced for them:**

- **Registered ≠ Eligible.** A plan-registered remediation is a plan fact (§6); it becomes ELIGIBLE only
  once items 1–9 are individually checked against the live repository.
- **Eligible ≠ Executed.** ELIGIBLE describes a candidate's current standing, not an event that occurred.
- **Eligible ≠ Authorized.** ELIGIBLE never includes the operator's command; PERMITTED TO EXECUTE does.
- **Registered ≠ Authorized.** Registration alone authorizes nothing.

**Four distinct things about a blocked unit and its remediation — never merged, and no new state is
introduced:**

| Term | Meaning | Source of the fact |
|---|---|---|
| Blocked earliest execution unit | The first non-approved `EXE-P0` unit in plan order fails pre-flight or a §18.15 guard. **It cannot be skipped:** no later `EXE-P0` unit of any capability may be selected or executed (orchestrator agent §9, "Never skip forward"; plan §18.15 guards). | Plan rows, §18.15, §38 |
| Remediation identified as a prerequisite | A registered `REM-…` unit that the **governing plan explicitly** makes a prerequisite of the blocked unit (cite the sentence, e.g. plan §18.14.x / §18.15). A dependency you only derive is `INFERENCE` and cannot create this status. | Plan text only |
| Remediation eligibility ("next remediation candidate") | That prerequisite remediation is **ELIGIBLE** under items 1–9 above, evaluated against its *own* plan-stated preconditions (never against being "the first non-approved `EXE-P0` unit"). It is the unit the operator *may* authorize next; it is not "later" than the blocked unit — the plan orders it before it. Report it in the `NEXT ELIGIBLE UNIT` field with the qualifier "remediation candidate — ELIGIBLE, not yet PERMITTED TO EXECUTE". | Plan sequence + this section |
| Execution authorization | The operator's explicit `Execute <canonical-ID>` for that exact ID in this session (§19). Turns ELIGIBLE into **PERMITTED TO EXECUTE**. **A candidate is never automatically executable.** | Operator command |

**Precedence when this skill's reading differs from the orchestrator agent's wording.** Orchestrator agent
§3 and §7 describe "no next eligible unit … none while blocked" without addressing a plan-stated
prerequisite remediation ordered before the blocked unit. This skill's reading above is the one the live
plan supports (§18.14.7, §18.14.8, §18.18). **When the skill's interpretation of current eligibility or the
next remediation candidate differs from wording in the existing orchestrator agent, the authoritative
execution plan controls. The discrepancy must be reported; it must not be silently ignored.** This skill
does not modify the orchestrator agent (§23); aligning its wording, if wanted, is a separate,
human-authorized change.

If the earliest non-approved *unit* is blocked and the plan names **no** eligible prerequisite remediation,
there is **no next eligible unit**. If it names one, that remediation is the reported candidate. This does
not mean nothing else may ever be proposed: the plan's own treatment of any other registered-but-deferred
remediation (e.g. one the plan marks optional/directed-only) is preserved as-is — this skill simply never
proposes such a unit as "next" on its own initiative, and does not describe it as permanently unavailable.

## 8a. Continuous path state and authorization (plan §18.20, effective from `EXE-P0.2.C` onward)

**No competing state authority is created here.** An authorized path's state is reconstructed the same
way all other state is (§5), never from a second ledger, in this priority order: (1) this session's own
record of the triggering `Execute` and every path-internal unit processed since; (2) git evidence — a
committed path-internal unit's commit trailers (the orchestrator records a `Continuous-Path:` trailer,
agent §3a); (3) the plan's own position blocks (§18.14.8, §18.18, §52) and the runbook's §20 (plan wins
on conflict, §4); (4) `CLAUDE.md`'s recorded implementation status. This mirrors, and does not compete
with, the orchestrator agent's own §3a reconstruction model — this skill consumes that same
reconstruction, it does not maintain an independent one.

The state consumed must contain, at minimum, the eight facts §18.20.N enumerates: Capability;
authorized starting unit; authorized path boundary; completed units and which continuation state each
reached; the current stopped unit, if any; the stop reason, if any; HDR resolution status, if
applicable; and a continuation-authorized flag.

**Fail-closed rule (§18.20.N, mandatory, same as §26).** If these sources do not unambiguously establish
all eight facts, classify the situation `DOCUMENTATION-GAP` (§10) and take no automatic-continuation
action: do not infer `continuation-authorized: true`, do not guess which units a stopped path already
completed, and do not guess the path boundary. The correct next step is to report the gap and describe,
never send, the `Execute <id>` the operator would need to send to validly re-open the path from a named
unit (§18.20.B) — this is a fresh, explicit human command, never a chain.

**What a reconstructed path state may never do:** authorize a unit outside the named Capability
(§18.20.M); satisfy a Gate-level guard, a Capability Gate approval, or a boundary approval (§7a,
§18.20.E, §18.20.G); or persist across an explicit operator `STOP` — a `STOP` ends the open path's
automatic-continuation privilege; resuming afterward requires a fresh `Execute` naming the stopped-at
(or later) unit.

## 9. Dependency analysis

For the unit in question build, from the plan rows and §18.14.x tables (not from memory), the dependency
set: predecessor units, prerequisite remediations (and whether each is executed / AI-verified / approved
— or, within an already-open authorized path, reached `AI VERIFIED — CONTINUING` /
`AI VERIFIED — CONTINUING (NOT APPLICABLE)`; classify the guard first, §7a), Gate dependencies,
contract/source dependencies (which documents and sections the unit consumes), and **downstream** units
that would be blocked or unblocked by it. Distinguish
*registered* (exists in the plan) from *executed* from *AI-verified* from *approved* — four different
states. Report units that are registered-but-deferred separately from prerequisites, and state for each
whether the plan makes it a prerequisite (cite the sentence) or an operator decision made it deferred.
A dependency the plan does not state but you derive is `INFERENCE`; it cannot make a unit eligible or
ineligible by itself — surface it as a Human Decision Request or a finding.

## 10. Blocker detection

Stop and classify at the first of: implementation failure; test failure; governance failure; source gap;
contract contradiction; missing human decision; dependency failure; documentation/code mismatch;
historical-evidence problem; authorization problem; environmental/tooling failure. Map each to the
orchestrator's existing finding labels (do not add a competing taxonomy):

| Class | Existing label (agent §5.6 / governance §4) |
|---|---|
| implementation failure / test failure | `IMPLEMENTATION-GAP` / `TEST-GAP` (test failure: record the exact Surefire line) |
| governance failure | `GOVERNANCE-BLOCKER` |
| source gap | `SOURCE-GAP` (registered `SOURCE-GAP-*` = `UNRESOLVED SOURCE GAP`) |
| contract contradiction | `CONTRADICTION` / `SOURCE-CONFLICT` |
| missing human decision | Human Decision Request (§14) — an escalation; the blocking finding itself is the underlying `SOURCE-GAP` or `GOVERNANCE-BLOCKER` |
| dependency failure | `PREREQUISITE-BLOCKER` |
| documentation/code mismatch | `DOCUMENTATION-GAP` / `IMPLEMENTATION-GAP` (`UNAUTHORIZED IMPLEMENTATION / DOCUMENTATION GAP` when code exceeds the documents) |
| historical-evidence problem (state disagrees across artifacts; an approval recorded nowhere) | `DOCUMENTATION-GAP`; if a prerequisite's or approval's status cannot be established at all → `PREREQUISITE-BLOCKER` |
| authorization problem (ambiguous command; approval before AI PASS; relayed approval) | `GOVERNANCE-BLOCKER` |
| environmental / tooling failure (e.g. stale IDE-compiled classes → `mvn clean test`; rate limit) | Not a finding label: report the affected evidence as `NOT VERIFIED` / `UNRESOLVED` and, if it prevents a required check, `GOVERNANCE-BLOCKER`; retry only reads; never treat as pass |

The situation names in the left column (including "historical-evidence problem", "authorization problem",
"environmental / tooling failure") are **explanatory subcategories written in the finding text**. They are
not governance labels, states or ID series; the right column is the only label used.

For **every** blocker record: exact location; affected unit; affected contract; authoritative source;
missing semantic; whether existing documentation resolves it; whether an inference would be needed;
whether a human decision is required; whether remediation is required; affected files; dependent units;
blocking or non-blocking **with the plan's own classification cited** (never label a gap non-blocking
because an implementation could work around it; only the plan/§38, or a human, classifies a gap
non-blocking). Emit the orchestrator's `EAIOC EXECUTION BLOCKER REPORT` (agent §7). Never assign a new
registered ID (`SOURCE-GAP-*`, `CONTRA-*`, `REM-*`, `HD-*`…); describe it, and let a user-directed plan
correction register it.

## 11. Source-gap detection

A source gap exists when a unit (or a decision it needs) requires a semantic the authoritative corpus does
not state. Detect it *before* implementing by walking each field/behaviour the unit produces or consumes
and asking: is there a stated **producer**, **consumer**, **cardinality**, **nullability**, **error /
no-result behaviour**, **versioning rule**, **ownership (package/component)**, **retrieval/lookup
contract**, **verified/unverified handling**, **tenant scoping** and **failure path**? An unanswered
question is a gap, not a detail to fill. Check first whether it is already registered (§38, the owning
document's register, `requirements-traceability.md`); if registered, cite it and read its **current**
status (additive status rows supersede register rows). A gap is *resolved* only when a source document has
actually been updated; a decision recorded in a design note narrows it at most.

## 12. Contract reconciliation (core capability)

When a gap or contradiction is found, run, in order, and keep the trace:

1. trace the affected requirement / interface / schema to its owner (`interfaces.md` INTF-nnn, §, schema;
   `architecture.md` §; `conventions.md` §; Spec/PS IDs `OBJ/SEC/NFR/AC/H`);
2. search **all** authoritative references (grep IDs and field names across `docs/`, then `control_plane/`);
3. compare terminology and semantics across documents (same name ≠ same meaning; similar names never
   establish equivalence — precedent: `execution-plan.md` §18.14.4 D-2, whose contract substance lives at
   `interfaces.md` §28.1);
4. identify contradictions (quote both sides);
5. identify missing fields/behaviours;
6. identify producer/consumer ownership and the package that owns each side (`conventions.md` §2.1/§2.2);
7. identify cardinality, nullability and error/no-result semantics;
8. identify versioning implications (`interfaces.md` §1.3, §24; `conventions.md` §4.3, §22.1 — record what
   the rules literally say and what is analogy);
9. identify dependency implications (units, prerequisites, downstream);
10. **determine whether the corpus establishes the answer.**

If it does → cite it and proceed. If it does **not → STOP and issue a Human Decision Request (§14). Do not
invent the missing answer**, do not adopt the "obvious" reading, do not encode the choice in code or a
document as if it were established.

*Detection patterns (illustrative, not answers):* missing measurement-acquisition semantics; missing
cardinality (how many records may match a key, and what counts); missing verified-only consumption rules;
missing failure/no-record semantics when a non-null return meets "no result"; versioning ambiguity for an
operation-signature change; producer/consumer ownership gaps across packages; missing request→record
retrieval contracts; fields that exist only in comparison output versus fields native to one execution.
These came up in the `REM-P0.2.B-01` investigation — **historical precedent, not reusable answers** (§22).

## 13. Human-decision detection

A human decision is required when: the corpus does not establish the answer (§12); two legitimate
alternatives satisfy the sources; a source change would alter an accepted contract; an instruction is
ambiguous; a governance rule would have to change; an inference would be required to proceed; a previous
human decision is not clearly still in force; or the plan classifies something as requiring the
operator. It is **not** required for something the corpus already establishes — do not ask the operator to
decide established facts (ask them to confirm only if evidence conflicts).

## 14. Human Decision Record (HDR) protocol

*"HDR" may be used only as a descriptive, session-scoped shorthand for "Human Decision Request" — never
as an identifier series.* **No `HDR-*` identifier is created or minted.** The repository's authoritative
decision-ID mechanisms — `HD-n`, `HQ-n`, `DB-n`, `GS-n`, the design-note `D-A…D-D`, and unit IDs
`REM-P0.<n>.<letter>-NN` / `EXE-P0.<n>.<letter>` — remain the only registered IDs; new ones are created
only by a user-directed document correction. **Generating a request is not deciding it.**

Issue each request in this shape (a descriptive title, never an ID):

```
HUMAN DECISION REQUEST
Title:                  <short descriptive title — not an ID>
Question:               <one exact question>
Why required:           <what the corpus does not establish; why an inference would be needed>
Affected unit(s):       <ids>            Affected documents/sections: <files §>
Existing authoritative evidence:   AUTHORITATIVE — <file § quote> …
Competing interpretations:         (a) … (b) … (c) …
Consequences of each:              (a) … (b) … (c) …   (contracts, units, DoD, versioning)
Dependencies:                      <other decisions / units>
Recommended option (if appropriate): PROPOSAL — clearly marked, never chosen for the human
Must NOT be assumed:               <the tempting default>
Exact decision required:           <the sentence the human must answer, e.g. "choose (a)/(b)/(c) or supply your own">
```

After the human answers: (1) record it in the appropriate execution/remediation artifact — **only in
documents the operator authorized**, labelled `HUMAN DECISION` (date, source "operator, session") and
distinguished from source-established fact; (2) update only authorized documents, additively (historical
records untouched); (3) re-run reconciliation (§12); (4) re-run Architect/Verifier as §17 requires;
(5) **stop before execution** unless an explicit `Execute <id>` exists. Historical decisions are evidence
of what was decided, reusable only if the *current* corpus establishes they still apply (§22). Present
decisions in batches with defaults only as PROPOSAL; accept "accept all recommendations except …" only
when the operator says so.

## 15. Remediation planning

When remediation is required, produce (do not execute) a plan containing: objective; affected source
contracts; affected documents/files; required human decisions (open vs decided); scope
(documentation-only / contract-design / implementation / test-validation / mixed); dependencies and
prerequisite units; downstream units blocked; Definition of Done; Architect, Verifier and AI-Verification
requirements; the approval boundary. **Registration of a remediation is not execution** (plan §18.14):
new IDs, unit tables and status changes are written only by a user-directed plan correction, in
authorized files, additively. A remediation existing is not proof of a fix (§16 revalidation).

## 16. Remediation execution control

A remediation runs only on the operator's explicit `Execute <its canonical ID>`; that command itself
triggers the full governed pre-flight (§7) and lifecycle. **A separate prior `REVALIDATE <id>` is not an
unconditional precondition of `Execute`.** `REVALIDATE <id>` is appropriate, and expected, (a) after a
previous blocker or remediation affecting that same ID, to confirm it is now safe to re-attempt, or (b)
whenever the operator explicitly asks for it; in neither case does `REVALIDATE` itself authorize anything.
After a remediation or documentation correction, before reporting a unit `READY`: verify in the live
repository that the expected change exists and matches; the gap is actually closed (a source document
updated; status recorded); the contradiction is actually resolved; any required unit is executed **and**
AI-verified **and** approved — **or, if that required unit is itself path-internal to an already-open
authorized path, reached `AI VERIFIED — CONTINUING` / `AI VERIFIED — CONTINUING (NOT APPLICABLE)` instead
(classify the guard first, §7a); no currently registered remediation is plan-designated path-internal, so
this qualifier is currently theoretical, not yet exercised (§16a)**; no new blocker; plan, runbook and
`CLAUDE.md` agree; git state valid. Only then report `READY` — and still wait for the human's `Execute`.
A design-only remediation never authorizes its own implementation unit. Never use a remediation to make an
unrelated unit pass.

## 16a. Path-internal prerequisite/remediation restriction (plan §18.20.I–J)

A prerequisite or remediation unit may execute automatically inside an open path **only when the live
plan text explicitly designates that exact unit as path-internal for that exact path.** Being a "hard
prerequisite" (§18.15, §18.14.8 sense) is not, by itself, sufficient — that status and this designation
are independent, and this skill never infers the designation itself (agent §4b, same rule). As of plan
v1.1.1, **no** currently registered prerequisite or remediation unit is plan-designated path-internal;
`REM-P0.2.B-01`/`-02` are already executed/approved and historical (§22); `REM-P0.2.B-03` is classified
**Optional** (§18.20.J, HD-CE-09) and is never auto-run.

Classify every remediation unit encountered into exactly one of the plan's four classes before deciding
whether it may run inside a path:

| Class | May auto-run inside an open path? |
|---|---|
| Path-Internal | Yes — only within the exact path the plan designates it for |
| Optional | Never — requires its own explicit `Execute` from the operator |
| Separately Authorized | Never — requires its own independent authorization the plan names, regardless of any open path |
| Unrelated | Never — out of scope of any current path entirely |

This is the plan's own four-way taxonomy (§18.20.J), not a new one this skill introduces; §15's "a
remediation existing is not proof of a fix" and §16's execution-control rules above apply identically
whether or not a path is open.

## 17. Architect / Reviewer / Verifier routing

Route through **`eaioc-agent-orchestration`** (classification, STEP vs CAPABILITY level, the invocation
matrix, escalation, statuses, GOVERNANCE RECORD, `Governance-*` trailers) — do not re-specify it. Main
session only: a delegated subagent cannot run the skill or spawn the agents. Rules that always hold:
the Verifier always runs at a Capability Gate; AUTHORITATIVE-DOC changes route Architect + Verifier;
independent agents run in parallel with no shared conclusions; agent verdicts are evidence for the
§18.12 AI Verification, never a substitute for it, never approval; a `BLOCKED`/`FAIL`/`SOURCE-CONFLICT`
stops the unit. For a pre-commit review of a *documentation* correction, give the agents the exact change
set, the operator's decisions and the checks to re-derive; re-run them after every fix round.

**Escalation under an open path (§18.20.K.11).** This routing, and `governance-matrix.yaml`'s existing
`consolidation`/`unit_verdict_mapping` rules mapping an `ESCALATED`/`PENDING` finding to
`AI VERIFICATION: BLOCKED`, apply identically whether or not a §18.20 path is open. A governance-agent
finding the matrix classifies as requiring human escalation stops the path immediately — independent of
the unit's own technical AI Verification verdict — exactly as it would stop a standalone unit. No change
to `governance-matrix.yaml` is required for this: its existing escalation-to-`BLOCKED` mapping already
structurally implements §18.20.K.11.

**Classification of `.claude/skills/**` and `.claude/agents/**` changes.** Treat any change to these
paths as a governance/orchestration artifact requiring Architect and Verifier review before adoption or
wiring — the same review discipline as an AUTHORITATIVE-DOC contract change, because these files govern
execution behaviour. The same applies to a pre-adoption review of a governance skill or agent file (as in
this kind of session): treat it as a governance-document review under this classification, not as an
execution unit. **`governance-matrix.yaml` currently has no explicit change class for these paths** (its
`*.md` catch-all is defined for unit changes, not governance documents) — this is a `DOCUMENTATION-GAP` in
the existing matrix, to be closed separately by a user-directed correction to that matrix. This skill does
not invent a new repository-wide change-class taxonomy to fill that gap, and does not claim its own
classification is authoritative until the governing matrix establishes one.

## 18. AI Verification

Exactly as the live plan §18.12 defines (dimensions A–I, K, then J; the report with its current line
count; one verdict: `PASS`, `BLOCKED`, `NOT APPLICABLE`, `PASS WITH DOCUMENTED NON-BLOCKING GAP`), plus the
GOVERNANCE RECORD annex. **`CONTINUING` and `CONTINUING (NOT APPLICABLE)` (§6, §19a) are footers/states
downstream of a verdict, never a fifth verdict** — a path-internal unit still reaches exactly one of the
four verdicts above; the continuation footer only changes what happens *after* the verdict (auto-continue
vs. stop for approval), per §18.20.F/R. This skill adds only: every line carries an evidence label (§24);
documentation-only units are verified by
re-deriving counts, scope, historical integrity and cross-file consistency with commands, not by reading
a summary. Tests: run `mvn clean test` from `control_plane/` when code is in scope and report the exact
result line; never claim a pass without running it. AI PASS is never human approval.

**`UNKNOWN` versus `BLOCKED`.** `UNKNOWN` is an evidence-gathering state (required evidence not yet
obtained); `BLOCKED` is one of the plan's own final verdicts. This skill does **not** state or imply
"`UNKNOWN` means `BLOCKED`" as a rule. Use only the authoritative plan §18.12.1 verdict rules to reach a
verdict: while evidence for an applicable dimension is missing, obtain it (E: "Do not claim a test passed
unless it actually ran in this session"; H: verify prerequisites "by inspecting the repository … not by
trusting the plan's Preconditions text"); if the row says `N/A` with its own reason, that dimension is not
applicable (F); if the plan classifies the gap non-blocking, the verdict may be `PASS WITH DOCUMENTED
NON-BLOCKING GAP` (I); the final verdict must be "independently supported by the required evidence
(dimensions A–I)" (K); conclude with one of the plan's own four verdicts (J), never a fifth. **INFERENCE,
not a plan rule:** where evidence for an applicable, non-N/A, non-excused dimension genuinely cannot be
obtained, the only remaining verdict in the plan's own set is `BLOCKED` — this derivation is this skill's
own inference from E/H/K/J together, cited here (plan §18.19.3 fast-fail; plan §18.12.1 H; orchestrator
agent §4 and §9), not a separate rule this skill adds.

## 19. Human approval control

**Canonical commands** (command forms: plan §18.11.2, §18.11.4, §18.11.6). The rule that the keyword may
be in any letter case while the ID must be exact is **agent-level operational behaviour** (orchestrator
agent §2); the plan states no such rule, and this is never represented as execution-plan semantics:

| Command | Meaning |
|---|---|
| `Execute EXE-P0.<n>.<letter>` / `Execute REM-P0.<n>.<letter>-NN` | **Before the plan's migration point** (`EXE-P0.2.A`, `EXE-P0.2.B`, any historically-approved unit): authorize **that one** unit; run the lifecycle; stop. **From the migration point (`EXE-P0.2.C`) onward** (§18.20.O): opens an authorized path for the remainder of that unit's Capability, starting at the named unit (§18.20.B–C); the continuous path continuation procedure (§19a) governs from here until the path's boundary or a §18.20.K condition stops it |
| `Approve EXE-P0.<n>.<letter>` / `Approve REM-P0.<n>.<letter>-NN` | Record approval of that unit — or, at a path boundary that stopped before its Gate, of every path-internal unit named in the boundary-approval request (§18.20.G, §19a); valid only after the relevant unit's/units' own AI PASS / PASS WITH DOCUMENTED NON-BLOCKING GAP / NOT APPLICABLE; **starts nothing**, not even the next path-internal unit |
| `Review CAPABILITY-GATE P0.<n>` | Run the Gate review and AI Gate Verification; stop |
| `Approve CAPABILITY-GATE P0.<n>` | Record Gate approval — where a §18.20 path ran through H, this **is** that path's own boundary approval and must enumerate every path-internal unit it covers, naming each one's continuation state (§18.20.G, §20); valid only after `AI CAPABILITY GATE VERIFICATION: PASS`; starts nothing, including the next Capability's Sub-phase A (§18.20.E, §18.20.M) |

This table governs a unit outside an open path and a path's own boundary unit. **For a unit inside an
open path that is not the boundary, no individual `Approve …` is issued or processed at all** — §19a
governs; the unit's own `AI VERIFIED — CONTINUING` / `AI VERIFIED — CONTINUING (NOT APPLICABLE)` footer
is not, and never triggers processing as, an `Approve`.

Approval must **match the canonical ID**. These are **not** authorization, approval, or a command:
"looks good", "continue", "proceed", "go ahead", "next", "approved", "yes", "do it", `Execute P0.<n>`,
"continue with P0". Refuse and answer with the canonical command to use. An approval given for unit X
never covers unit Y, a re-run, a later attempt, or a changed scope. An AI PASS, Architect PASS, Verifier
PASS, passing suite, clean tree, commit, `CONTINUING`/`CONTINUING (NOT APPLICABLE)` state, or closed Gate
is never human approval.

**Processing an already-issued `Approve …`** is itself a short internal sequence, the same way the
orchestrator agent's own §5 ("Unit lifecycle") chains sub-steps inside one already-issued `Execute …`
without a further human command for each step:

1. Record the approval (flag if it is only in conversation, not yet a repository record).
2. Run the §31 Post-Approval State Synchronization procedure.
3. State the next eligible unit.
4. **Wait for its own `Execute …`** — approval and the next execution are never combined.

Step 2 is bookkeeping that completes the `Approve` command the human already gave; it is not a new
authorization and it never substitutes for step 4. It grants no execution or approval authority over any
other unit — see §31's own Governance boundary. Ambiguity is resolved by asking, never by choosing. **This
whole sequence runs only when a genuine `Approve <id>` was actually issued by the operator** — a
path-internal unit's `AI VERIFIED — CONTINUING` / `AI VERIFIED — CONTINUING (NOT APPLICABLE)` footer is
not an `Approve`, triggers no step of this sequence, and in particular never triggers step 2 (§31's own
Trigger makes the same point explicitly).

## 19a. Continuous path continuation (plan §18.20; orchestrator agent §5a–§5b; effective from `EXE-P0.2.C` onward)

Once the single `Execute <id>` that opens a path (§19's `Execute` row, above) has been issued, this
section — not a repeated pass through §19's table — governs every subsequent path-internal unit, until
the path's own boundary or a §18.20.K condition is reached. This is the exercise of the one authorization
already granted, never a new authorization event (§18.20.D).

1. For the current path-internal unit, run the ordinary procedure unchanged: pre-flight (§7/§7a),
   eligibility (§8/§8a), dependency analysis (§9), blocker detection (§10) where applicable, contract
   reconciliation (§12) where applicable, Architect/Reviewer/Verifier routing (§17), and AI Verification
   (§18). Any §18.20.K blocking condition reached at any point (a `BLOCKED` verdict, an unresolved Human
   Decision Request or source gap, a failure or security/governance decision requiring human
   intervention, an out-of-scope or unrelated discovery, a pre-existing guard failure, or a
   governance-agent escalation finding, §17) stops the path immediately — go to step 4.
2. On a `PASS` / `PASS WITH DOCUMENTED NON-BLOCKING GAP` verdict for a unit that is not the path's own
   boundary unit: the unit is committed (unchanged one-commit-per-unit rule, §18.20.L); report
   `AI VERIFIED — CONTINUING` (§6) instead of processing an `Approve …`; treat the next path-internal unit
   as already **PERMITTED TO EXECUTE** under the same original `Execute` (§8a's exception) and return to
   step 1. Never treat this as `HUMAN APPROVED`.
3. On a `NOT APPLICABLE` verdict for a unit that is not the path's own boundary unit: no commit is made or
   fabricated (§18.16, unchanged); report `AI VERIFIED — CONTINUING (NOT APPLICABLE)` (§6) instead of
   processing an `Approve …`; treat the next path-internal unit as already PERMITTED TO EXECUTE and
   return to step 1. Never treat this as `HUMAN APPROVED`; never collapse it into ordinary `CONTINUING`.
4. When the current unit **is** the path's own boundary unit, or any §18.20.K condition is reached: stop.
   For the boundary case, the resulting approval request (an ordinary `Approve EXE-P0.n.X` if the path
   stops short of the Gate, or `Approve CAPABILITY-GATE P0.<n>` if it reaches the Gate, §20) **must
   enumerate every path-internal unit completed since the path opened or last resumed, naming which
   continuation state each reached** (§18.20.G, HD-CE-17) — never an implicit "everything up to here," and
   never treating an N/A unit's inclusion in that enumeration as its own individual approval. For a
   §18.20.K stop, preserve path state (§8a) exactly as it stood and emit the blocker report (§10) or Human
   Decision Request (§14) as applicable; do not treat the path as open for further automatic continuation
   until §21a resolves the stop.

Regardless of outcome: never begin work on a unit of a different Capability; never treat a Gate approval,
or any continuation state, as opening the next Capability's path (§18.20.E, §18.20.M, §20) — the next
Capability always needs its own explicit `Execute EXE-P0.(n+1).A`.

## 20. Capability Gate control

The Gate is a post-lifecycle review/promotion checkpoint (plan §18.10), not a ninth sub-phase and not an
atomic unit; it has no commit and no `EXE-P0` ID. On `Review CAPABILITY-GATE P0.<n>`: identify Gate
prerequisites; verify every required unit A–H is executed / AI-verified / approved **or, for a
path-internal unit under an already-open path, reached `AI VERIFIED — CONTINUING` /
`AI VERIFIED — CONTINUING (NOT APPLICABLE)`** (or documented N/A, outside a path); answer the §18.10
question set from the evidence; verify **explicitly recorded deferrals** are still stated at the Gate (a
deferral is a recorded item, not a Blocking Condition unless the plan says so); run the Verifier over the
whole capability (always) and the Reviewer over capability-level-deferred changes; identify missing
evidence; prepare the review; emit `AI CAPABILITY GATE VERIFICATION: PASS | BLOCKED`; **stop** for the
human's `Approve CAPABILITY-GATE P0.<n>`. **Where a §18.20 path ran through H, this Gate approval is also
that path's own boundary approval and must enumerate every path-internal unit the path covers, naming
each one's continuation state (§18.20.G, §19a step 4)** — never an implicit blanket approval. Never cross
a Gate automatically; a `BLOCKED` Gate prevents the next capability from starting. A passing fixture- or
contract-level unit result must not be presented as proof of an end-to-end property the plan defers.
**Absolute Capability isolation (§18.20.M, unconditional):** a Gate `PASS`, a Gate approval, or a fully
completed continuous path never itself starts, or is ever reported as starting, Capability N+1 — only an
explicit `Execute EXE-P0.(n+1).A` does, exactly as before this amendment. After Gate approval, state
Capability N+1's Sub-phase A as the next eligible unit and wait for its own explicit `Execute`.

## 21. Recovery / resume semantics

A new session reconstructs everything from repository evidence (§5). Read-only commands (never authorize,
never modify files, never chain): `STATUS` / `STATUS P0` / `START P0` (→ `STATUS P0`); `BLOCKERS`;
`GATE STATUS P0.<n>`; `REVALIDATE <id>`; `RESUME <id>` and `VERIFY <id>` (→ `REVALIDATE <id>`, then "send
`Execute <id>` to run it"); `STOP`. **`RESUME` means reconstruct state, re-verify the unit is still
eligible, report what remains, stop — never "continue executing".** A previously blocked unit is
re-run only on a new explicit `Execute <id>` after revalidation passes. Failed-attempt evidence is
preserved; committed unit changes are reverted only by `git revert` of that one commit on the operator's
instruction (plan §37); uncommitted partial changes have no defined procedure → report
`RECOVERY-PROCEDURE-SOURCE-GAP` and never reset, stash or discard without the operator's decision.

## 21a. Same-path Human Decision Request resume (plan §18.20.H)

When the operator resolves a Human Decision Request or blocker that stopped an open path (by recording a
decision, directing a documentation/source-contract correction, or otherwise providing the missing
resolution — not by issuing a fresh `Execute`):

1. Reconstruct state (§8a) from the resolution plus all existing evidence sources.
2. Reconcile the resolution against the authoritative documents exactly as §16's revalidation already
   requires (the guard/contract/gap the stop was about is actually closed — a decision existing is not
   proof it was correctly promoted into source documents).
3. Confirm `continuation-authorized` remains true **for the original path only** — the same Capability,
   the same boundary, no expansion (§18.20.H: resolving an HDR never expands the authorized path).
4. If the same path remains valid, resume automatically from the stopped-at unit (§19a step 1) — **no
   fresh `Execute` is required** solely because the HDR was resolved. This is the one exception to §21's
   general "a previously blocked unit is re-run only on a new explicit `Execute <id>`" rule, and it is
   scoped exactly as narrowly as that rule's own text already anticipates for an open path.
5. If reconciliation surfaces a new HDR or any other §18.20.K condition, stop again (§19a step 4) — do
   not resume.
6. If the reconstruction is ambiguous (§8a's fail-closed rule), do not resume; report `DOCUMENTATION-GAP`
   and describe, never send, the `Execute <id>` needed instead.

## 22. Historical precedent handling

**Historical experience is precedent, never authority.** May: identify similar situations, locate prior
remediation patterns, recall options already considered, reduce duplicate analysis. Must not:
reuse a previous decision automatically, treat a previous implementation as the current contract, assume
similarly-named IDs have identical semantics, treat a previous approval as current approval, or treat a
superseded plan block as current. Label every use `HISTORICAL EVIDENCE — PRECEDENT` and cite the commit
or section (e.g. the ledger-contract chain `addd949`/`34dd1f1`/`197da0a`/`c378dc9`, the
`REM-P0.2.B-01` chain `877fecc`/`e4cf1f5`/`196e112`, the reconciliation of `EXE-P0.1.G/H`, the
`EXE-P0.2.B` pre-flight BLOCKED). Precedents also include *mistakes* (plan rows with wrong claims; an
approval found unevidenced; a wrongly-scoped removal) — use them as reasons to re-verify, not as
templates.

**Pre-migration historical quarantine (plan §18.20.O).** `EXE-P0.2.A` and `EXE-P0.2.B`, and every unit
approved before the migration point `EXE-P0.2.C`, were executed under the prior per-unit model and stay
that way permanently: never re-run them, never re-verify them under §19a/§21a's continuous-path
procedure, never attribute a `Continuous-Path` commit trailer to their historical commits, and never
describe their existing `HUMAN APPROVED` state as if it were `CONTINUING`. §18.20 applies prospectively
only; this is the same non-retroactive rule §6's continuation-states paragraph and the orchestrator
agent's own §11 already state, restated here as historical-precedent handling.

## 23. Governance self-protection

This skill is itself governed, and self-protection covers more than this one document. **Never silently
make, and never treat as already authorized, any of the following** — each needs its own explicit,
separate human authorization, a reviewed diff, and a commit containing only that change:

- this skill's own rules;
- the authority model, the approval model, the state machine, the fail-closed rules, execution sequencing,
  or human-authorization semantics;
- edits to `eaioc-p0-execution-orchestrator`, `eaioc-governance`, `eaioc-agent-orchestration`,
  `governance-matrix.yaml`, any other skill or agent file, the plan's governance sections, or `CLAUDE.md`
  rules;
- **wiring this skill into the orchestrator agent's `skills:` list or load order**;
- Claude Code **settings, permission, or hook changes**.

None of these may be changed to make a unit pass or to "improve" behaviour. If work reveals the skill is
insufficient or in conflict: **STOP and report** — why it is insufficient; the exact governance conflict;
the proposed change; the authority boundary affected; the human decision required. Also never: push,
force-push, amend, squash or rebase historical commits; modify historical evidence (approved design notes,
committed history, earlier plan records — additive supersession markers only); self-authorize a
remediation.

## 24. Evidence requirements

Every meaningful conclusion is traceable to: source file + section/heading; the execution unit; the git
commit where relevant; the test result (exact line) where relevant; the agent review result where
relevant; the human decision (date, source) where relevant.

**Governance-report provenance layer.** For governance reporting (sources, decisions, history, proposals,
open items), assign one applicable provenance label from this layer where useful for governance reporting:

`AUTHORITATIVE` · `HUMAN DECISION` · `HISTORICAL EVIDENCE` · `INFERENCE` · `PROPOSAL` · `UNRESOLVED`

**This layer is a governance-report/provenance aid only.** It is **not** a new repository taxonomy, not a
new state machine, not a new evidence-tier hierarchy, and not a replacement for any repository-native
label, state, verdict or command semantic. Repository-native states (§18.17), verdicts (`AI VERIFICATION:
PASS/BLOCKED/NOT APPLICABLE/PASS WITH DOCUMENTED NON-BLOCKING GAP`; Gate `PASS/BLOCKED`; `READY`/`BLOCKED`
for REVALIDATE), finding labels (agent §5.6, `eaioc-governance` §4) and command semantics (§19) remain
authoritative for their own respective purposes and are never superseded by a provenance label. No new
canonical ID or state is created for this layer.

**Relationship to the shared finding labels (`eaioc-governance` §4).** The shared labels stay exactly as
defined and are **not collapsed**: `DOCUMENTED REQUIREMENT` (stated in a Tier 1–3 document), `IMPLEMENTED
BEHAVIOR` (observed in current source), `TESTED BEHAVIOR` (asserted by an inspected, passing test),
`INFERRED BEHAVIOR`, `NOT VERIFIED` / `UNVERIFIED`. Use them for every claim about documents, code and
tests; use the provenance layer above, where useful, for claims about governance state, decisions and
history. Rough correspondence, not identity: `DOCUMENTED REQUIREMENT` ↔ `AUTHORITATIVE`; `INFERRED
BEHAVIOR` ↔ `INFERENCE`; `NOT VERIFIED` ↔ `UNRESOLVED`. **`IMPLEMENTED BEHAVIOR` and `TESTED BEHAVIOR` have
no provenance-layer equivalent** — observed code or a passing test is evidence of what was built, never a
requirement; report them under their own labels (a build that exists is `IMPLEMENTED BEHAVIOR`; a
superseded record about it may additionally be `HISTORICAL EVIDENCE`).

**Human-decision provenance versus current contract authority (see also §4).** A recorded human decision is
`HUMAN DECISION` at the point it is made. Once a plan correction promotes that decision's text into a
Tier 1–3 document, the resulting contract text is a **documented requirement with human-decision
provenance** — citable as `AUTHORITATIVE` / `DOCUMENTED REQUIREMENT` content, with its provenance still
recorded and never erased. Do not state that a human decision can never become authoritative; distinguish
the decision's *provenance* from the *current contract's authority*. Do not present inference as fact; do
not present a proposal as a decision; list unverifiable items as `UNRESOLVED` / `NOT VERIFIED` with the
reason.

## 25. Reporting requirements

Every report states: plan version; HEAD and branch; working-tree state; the command mode (main-session vs
delegated read-only); sources actually read; results by section with labels; open blockers/gaps/decisions
with evidence; what would happen next *only as a described command for the human to send*. The
`STATUS` block stays exactly:

```
CURRENT P0 STATE:
CURRENT UNIT:
NEXT ELIGIBLE UNIT:
BLOCKER:
REQUIRED HUMAN APPROVAL / COMMAND:
WOULD EXECUTION BE PERMITTED? YES/NO
```

Then run the `eaioc-governance` §10 self-check and state it. Long reports may be summarised with a pointer
to the saved artifact; never omit a blocker in a summary. Approvals are never requested after a BLOCKED
verdict.

## 26. Fail-closed rules

**Stop** (no execution, no approval request, report the blocker) when any of: authoritative sources
conflict; required contract semantics are missing; a human decision is required; prerequisite status cannot
be established; approval evidence cannot be established; current state cannot be reconstructed reliably;
implementation diverges from contract; tests fail; Architect, Verifier or AI Verification blocks; git state
invalidates the evidence; a command is ambiguous; a Capability Gate condition is unresolved; a delegated
(subagent) mode would require an approval conversation or an execution; the session's tooling/rate limit
truncated evidence gathering. **Uncertainty is never converted into success.** In the §8 **ELIGIBLE** checklist `UNKNOWN` counts as
`NOT MET` — an inference from plan §18.19.3 (fast-fail pre-flight), plan §18.12.1 H (verify by inspecting
the repository, never by trusting the plan's text), and orchestrator agent §4 ("Any failure → do not
execute") and §9 (never skip forward on incomplete evidence), cited here rather than asserted as a
free-standing rule. In AI Verification, `UNKNOWN` is handled exactly as §18 states — this skill never
states or implies "`UNKNOWN` means `BLOCKED`" as a rule of its own.

## 27. Dry-run procedure (read-only; used for `STATUS P0` and after any governance change)

No file is modified, no unit executed, nothing approved. Sequence:

1. Load `eaioc-governance` and `eaioc-agent-orchestration`; read this skill; record which of them were
   available (a skill added this session may not be registered yet — read its file directly and say so).
2. Reconstruct state (§5) from scratch; do not read any prior report as evidence.
3. Establish: the closed capabilities and their Gate approvals; the active capability; per unit A–H the
   executed / AI-verified / approved status with the commit and the approval evidence class; registered
   remediation units and their status; the gap register **including status-update rows**; deferred items;
   counts (atomic units + Gates = checkpoints); the terminal-verdict line.
4. Compute the next eligible unit (§8) and the blockers (§10); verify plan / runbook / `CLAUDE.md` / git
   agree and list every disagreement.
5. For each unit of interest (`REM-…`, the blocked `EXE-…`, deferred units) produce the §8 checklist with
   evidence; distinguish **ELIGIBLE** from **PERMITTED TO EXECUTE** (the latter requires the operator's own
   `Execute <id>` in this session).
6. Confirm safety: nothing executed/approved/modified/chained; then the verdict.

**Verdict vocabulary: the repository-native vocabulary, never a new canonical state.** Report `READY`
(a candidate unit is **ELIGIBLE**, §8, and only the operator's `Execute` is missing to make it **PERMITTED
TO EXECUTE**) or `BLOCKED` (the same outcomes the orchestrator agent's REVALIDATE uses, agent §2/§10), and
use `AI VERIFICATION: BLOCKED` / `PASS` / etc. exactly as the authoritative source defines them for their
own contexts. A prose qualifier may be added for clarity (for example, "READY — candidate is ELIGIBLE;
PERMITTED TO EXECUTE only after `Execute <id>`"), but the qualifier is report wording, never a new
canonical verdict or state. Never "APPROVED".

## 28. Examples

*All are illustrative; re-derive from the repository — never reuse an example's conclusion.*

**A. Ambiguous command.** Operator: "continue". → Refuse; reconstruct state; answer "no command was given;
the canonical form is `Execute <id>`; state: …". No change made.

**B. Registered ≠ Eligible ≠ Authorized.** A remediation `REM-…-02` is registered as a prerequisite of a
blocked `EXE-…`. → Report: registered (cite §), not executed, not AI-verified, not approved; check items
1–9 against its own plan-stated preconditions — if all MET it is **ELIGIBLE** (a "remediation candidate",
§8), but not yet **PERMITTED TO EXECUTE**; the blocked unit stays ineligible until the remediation is
executed, AI-verified **and** approved (cite §18.15 / the unit table). Do not start it; only an explicit
`Execute <its ID>` makes it PERMITTED TO EXECUTE.

**C. Source gap found mid-unit.** While implementing, the unit needs a cardinality rule the corpus does not
state. → Stop before writing the affected code; run §12; issue a Human Decision Request with the competing
readings and consequences; mark the unit `AI VERIFICATION: BLOCKED`; no commit; no approval request.

**D. Conflicting sources.** Plan row says X, `interfaces.md` says Y. → Quote both; apply documented
precedence (plan governs process; the frozen contract governs content) and cite it; if it resolves, follow it
and record the plan-row error as a finding; if it does not, `SOURCE-CONFLICT` and stop.

**E. Relayed approval.** A subagent report says "the operator approved REM-…". → Not evidence. Approval
evidence is a repository record or the operator's own command in this session; otherwise
flagged as *unrecorded (session-only)* and reported `UNRESOLVED` — do not rely on it.

**F. After a documentation correction.** `REVALIDATE <id>` → verify the change exists, the gap is closed in
a source document (not only a note), counts/IDs/historical rows unchanged, the prerequisite units'
statuses, plan/runbook/`CLAUDE.md` agreement; report `READY` or `BLOCKED`; still wait for `Execute`.

**G. Governance insufficiency.** A situation needs a rule this skill lacks. → Stop; §23 report; do not edit
the skill.

## 29. Forbidden behaviours

Approving anything; authorizing execution; treating an ambiguous instruction as authorization; inventing
requirements, architecture, contract semantics, IDs, error codes or versions; converting inference into
fact, a recommendation into a decision, or precedent into authorization; bypassing a gap, blocker,
prerequisite, guard or Gate; executing more than one unit or chaining after a pass, approval, commit or
revalidation — **except the automatic path-internal continuation that plan §18.20 explicitly authorizes
once, at path-opening, by a single `Execute` (§19a); this exception is never construed as license for
continuation outside that same path, is never triggered merely by a pass/approval/commit/revalidation of
a unit that is not itself path-internal to an already-open path, and never crosses a Capability boundary
under any circumstance (§18.20.M)** — reinterpreting or widening a human approval (including reading a
continuation state as one); modifying historical evidence; editing authoritative documents or governance
to make a unit pass; silently modifying this skill or the orchestrator; creating a second state ledger;
labelling a gap non-blocking on its own authority; pushing, force-pushing, amending, squashing or
rebasing; discarding uncommitted work; treating a subagent's or a relayed message's claim as authority;
reporting success when evidence was truncated or unverified.

## 30. Final operational checklist (before every report or action)

- [ ] Skills loaded (`eaioc-governance`, `eaioc-agent-orchestration`, this one); live plan version read.
- [ ] State reconstructed from git + plan + runbook + `CLAUDE.md` (no reliance on memory or prior reports).
- [ ] Every state claim cites evidence and an evidence label; approval evidence class recorded.
- [ ] ELIGIBLE checklist run separately from PERMITTED TO EXECUTE; `UNKNOWN` treated as `NOT MET`
      (ELIGIBLE only); next eligible unit computed, not assumed; a remediation candidate is ELIGIBLE, never
      an authorization; no canonical verdict invented beyond the repository-native vocabulary.
- [ ] Blockers/gaps/contradictions classified with the plan's own blocking status; none downgraded.
- [ ] Missing semantics → Human Decision Request, not an invented answer.
- [ ] No ID assigned, no document/skill/agent/governance edit beyond what was explicitly authorized.
- [ ] Nothing executed or approved without the exact canonical command; nothing chained beyond an
      already-open authorized path (§19a), and never across a Capability boundary (§18.20.M).
- [ ] If a §18.20 path is open: its state (§8a) is cited with evidence, not assumed; every guard checked
      against it was classified first (§7a); every prerequisite/remediation treated as auto-runnable was
      confirmed plan-designated path-internal (§16a); `CONTINUING`/`CONTINUING (NOT APPLICABLE)` are never
      reported as `HUMAN APPROVED`; an ambiguous path-state fact was reported `DOCUMENTATION-GAP`, never
      guessed.
- [ ] Working tree consistent with what was reported; no push; no history rewrite.
- [ ] Self-check (`eaioc-governance` §10) run; final verdict uses the allowed vocabulary; the operator's next
      command, if any, is described — not sent.

## 31. Post-Approval State Synchronization

**Trigger.** Fires after **every** explicit human `Approve <canonical-ID>` of a completed
`EXE-P0.<n>.<letter>` unit, a completed `REM-P0.<n>.<letter>-NN` remediation, or a
`Approve CAPABILITY-GATE P0.<n>` — not only the unit that first prompted this section (`REM-P0.2.B-02`);
it is a standing part of this skill's procedure for every future approval. §19's own "Processing an
already-issued `Approve …`" sequence names this section explicitly as its step 2, between recording the
approval and stating the next eligible unit — this section is that step, in full. **Approval is not the
unit's final lifecycle step until this synchronization check has run.**

**Continuation states never trigger this section (plan §18.20, §19a).** A path-internal unit's
`AI VERIFIED — CONTINUING` or `AI VERIFIED — CONTINUING (NOT APPLICABLE)` footer is not an `Approve`
command — no `Approve` is issued or processed for it (§19's table note, above) — so this section's
Trigger, as already defined, is never reached by it. This is not a new carve-out invented for continuous
execution: it follows directly from the Trigger already reading "fires after every explicit human
`Approve`," and a continuation state is, by the plan's own definition (§18.20.F/R), never a human
approval. Only a genuine human boundary event — an ordinary unit `Approve` outside an open path, a
path's own boundary `Approve EXE-P0.n.X`, or `Approve CAPABILITY-GATE P0.<n>` — ever triggers this
section, exactly as it always has. (One phrase in the task that prompted this amendment referred to this
carve-out as "reserved by HD-CE-13" — a repository-wide search found no such decision anywhere in
`docs/execution-plan.md` or elsewhere in this repository; this section is therefore grounded directly in
§18.20.E/F/G/R and this section's own pre-existing Trigger/Governance-boundary text, not in HD-CE-13,
and the apparent gap in the plan's own HD-CE-01–19 register is reported, not silently assumed or
fabricated — see the final report for this amendment.)

**Synchronization check.** Reconcile the approved unit's actual execution evidence (§5: the commit, its
`git log -1 --format=%B` trailers, `git diff --stat`; the AI Verification verdict; the approval itself)
against every applicable authorized state-bearing document. At minimum, always check:

- Root `CLAUDE.md` — the "Implementation status" section's governance/state content only, never its
  frozen technical sections (the non-negotiable rules, architecture description).
- `docs/execution-plan-p0-steps.md` — the operator runbook's own position/tracker section (§20 there) and
  any command block, "what changed" note, or progress checklist naming this unit.
- `docs/execution-plan.md` — §18.18 current position, the unit's own §18.4–§18.9/§18.14.x row or table,
  and any additive status-update entries (§38) the unit's completion affects. **This document is checked
  for staleness only; it is never directly edited by this procedure** — see "Determine what needs
  updating" below.

Check any further state-bearing document (a downstream document's own status note, a design note's own
recorded state) only when the live plan or an existing governance rule explicitly makes that document
state-bearing for this unit — never by default, and never invented for the occasion.

**Determine what needs updating.** A targeted reconciliation, never a blanket rewrite:

- If a document already agrees with the actual execution evidence, make **no change** to it, and say so.
- **`CLAUDE.md` and `docs/execution-plan-p0-steps.md`:** if either document's state/status content is
  stale — it still shows the unit as not executed, not AI-verified, or not approved; it names a
  now-superseded "next command"; a checklist item is unchecked that the evidence shows complete — update
  **only** that state/status content, in that document, and nothing else in it.
- **`docs/execution-plan.md`:** this procedure never edits it directly, additively or otherwise — not
  even its own state/status content. If it is stale relative to the actual execution evidence, report the
  discrepancy (as a finding, §10) rather than editing it. Closing that staleness requires the document's
  own established version-bumped surgical-correction-pass discipline (`CLAUDE.md`'s "Prompt-writing rule"
  section), which is a separate, later, explicitly-directed action — never something this synchronization
  step performs itself, and never a precondition this procedure blocks on.
- Historical evidence, superseded blocks, and prior records are never rewritten. An update to `CLAUDE.md`
  or the runbook is always additive — a new line, a new dated/bracketed note, or a superseding block
  placed after the old one — following the same discipline §5 (item 2, additive status-update rows), §14
  (record human decisions "additively... historical records untouched"), and §15 ("new IDs, unit tables
  and status changes are written only by a user-directed plan correction, in authorized files,
  additively") already require elsewhere in this skill.

**Protection rules.**

- Never rewrite a technical contract, requirement, architecture section, interface schema, or any
  historical record merely to make documents look consistent with each other — that is exactly the
  "editing authoritative sources to make a unit pass" this skill's authority model (§3) and forbidden
  behaviours (§29) already prohibit, and this section grants no exception to it.
- Never invent or reinterpret execution state (§6, §26); every updated line cites the same evidence §5
  and §24 already require — a commit, a verdict, an approval date, or an explicit "not verified" — never
  a guess.
- Never modify a file outside the authorized synchronization scope above. This procedure does not license
  touching `.claude/**`, `control_plane/**`, `docs/execution-plan.md` (see above), or any Tier 1–3
  authoritative contract; those stay governed by §15's correction-pass discipline, §17's routing, and
  §23's self-protection rules, not by this section.
- **Plan precedence is scoped to contract/process authority, not to state currency — this never overrides
  §5.** §4's "the plan governs process and unit scope" is about which document's *rules* control when
  documents disagree on requirements or procedure; it is not a rule about which document's *bookkeeping is
  more current*. Whether a given unit has actually been executed, AI-verified, or approved is a question
  of fact, established by git evidence (§5), not by precedence between documents. If `CLAUDE.md` or the
  runbook disagrees with `docs/execution-plan.md` about whether a unit is done, that is exactly the case
  §5 already governs: **"If two artifacts disagree about state, that is a `DOCUMENTATION-GAP` finding;
  report it and do not choose silently."** This section adds no exception to that rule and never resolves
  such a disagreement by assuming the plan is automatically correct — a plan whose own "Current position"
  text has not yet been refreshed by a correction pass is exactly as capable of being the stale party as
  any other document, and the two "Current position" superseding blocks already in `docs/execution-plan.md`
  (v1.0.12, v1.0.13) are themselves evidence of that pattern.
- A conflict this procedure cannot resolve from authoritative evidence (two documents disagree and
  neither is clearly the stale one, or the execution evidence itself is ambiguous) is reported as a
  `DOCUMENTATION-GAP` (§10) — never silently resolved either way.

**Verification.** After making any edits (or after confirming none were needed):

- Re-read each checked document and confirm it now agrees with the live plan and the actual execution
  evidence.
- Confirm no stale unit status remains anywhere checked — no unit shown pending that is actually
  executed/AI-verified/approved, and no superseded "next command" left as the only one stated.
- Confirm the **next** unit has **not** been automatically authorized, executed, or approved by this
  procedure — synchronization never advances the execution state machine (§6) or any unit's eligibility
  (§8); it only records an approval that already occurred.
- Confirm no unrelated file changed: `git status --short --untracked-files=all` / `git diff --stat` /
  `git diff --name-only`, checked against the synchronization scope above.
- Where any file was modified, run `git diff --check` and resolve any reported whitespace or
  conflict-marker issue before treating the check as complete.
- Report any inconsistency that remains — a document this procedure could not reconcile stays open as a
  finding (§10), never hidden by narrowing what the report covers.

**Governance boundary.** This procedure never executes, approves, or authorizes the next unit, and never
chains into one — it is documentation reconciliation of an already-approved unit's own recorded state,
nothing more. It introduces no verdict or state beyond the ones this skill already defines (§6, §18, §27)
and no "SYNCHRONIZED" status or other new taxonomy. After it completes — whether or not any file
changed — **stop** and wait for the next explicit human `Execute <id>` command (§19); this procedure is
never itself the trigger for one, and committing a synchronization edit (when the operator directs it) is
its own separate, single-purpose commit, never bundled with a unit's own implementation commit.

**Automatic synchronization is not automatic execution — these are different categories, never conflated.**
§19 now runs this procedure as an automatic sub-step of processing an `Approve <ID>` command the human
already, explicitly issued (the same "chained sub-steps inside one already-issued command" pattern the
orchestrator agent's own §5 already uses for `Execute <ID>`'s own lifecycle). That automation is strictly
scoped to documentation
bookkeeping and grants no execution or approval authority whatsoever. It must never be read as, and must
never be extended into: automatically executing the next unit; automatically approving anything;
automatically resuming a blocked or previously-stopped unit; automatically executing a prerequisite or
remediation unit on another unit's behalf; or automatically chaining into any unit not already covered by
the `Approve <ID>` that triggered it. Every one of those remains exactly as gated as before this
procedure existed: only the human's own separate, explicit `Execute <canonical-ID>` command starts
anything.

## 32. Continuous-execution conformance self-check (plan §18.20, HD-CE-01–19)

Like the orchestrator agent's own §13, this is a dry-run conformance walkthrough for a natural-language
governance procedure with no executable test runner — read, not executed, at review time.

1. **`EXE-P0.2.C` opens Capability-2 path.** `Execute EXE-P0.2.C` issued from the migration point →
   §19a opens a path (Capability 2, start `EXE-P0.2.C`, boundary = Gate P0.2). Traces: §18.20.B–C, §19.
2. **C PASS → `CONTINUING` → D automatically.** `EXE-P0.2.C` reaches `PASS`, commits, reports
   `AI VERIFIED — CONTINUING`; `EXE-P0.2.D` is treated as already PERMITTED TO EXECUTE and proceeds
   without a fresh `Execute`/`Approve`. Traces: §19a steps 1–2, §8a exception, §6.
3. **F `NOT APPLICABLE` → `CONTINUING (NOT APPLICABLE)` → G automatically.** Same path, `EXE-P0.2.F`
   verifies `NOT APPLICABLE`; no commit; footer is `AI VERIFIED — CONTINUING (NOT APPLICABLE)`;
   progression continues to `EXE-P0.2.G`. Traces: §19a step 3, §6, §18.16.
4. **N/A never receives a commit.** At no point does this skill's procedure treat a `NOT APPLICABLE`
   verdict, path-internal or not, as commit-eligible. Traces: §18, §19a step 3, §18.16.
5. **`CONTINUING` never equals `HUMAN APPROVED`.** Every place `CONTINUING`/`CONTINUING (NOT APPLICABLE)`
   is defined (§6, §19, §19a, §20, §29) states this explicitly. Traces: §18.20.F/R.
6. **Completion-only guard accepts same-path `CONTINUING`.** `EXE-P0.3.B`'s "Capability 3.A has been
   approved" guard, checked while `EXE-P0.3.A` sits at `AI VERIFIED — CONTINUING` inside the same open
   Capability-3 path, is classified completion-only (§7a) and accepted. Traces: §18.20.S, §18.15, §7a.
7. **Gate-level guard rejects a continuation state.** The same unit's "Capability 2 Gate has been
   approved" half of the guard is classified Gate-level (§7a) and is checked only against a genuine
   `Approve CAPABILITY-GATE P0.2` — never against any continuation state. Traces: §18.20.S, §7a.
8. **HDR resolution resumes the same path.** A Human Decision Request mid-path stops it (§19a step 4);
   once resolved and reconciled, §21a resumes automatically at the stopped unit with no fresh `Execute`.
   Traces: §18.20.H, §21a.
9. **Unresolved reconstruction → `DOCUMENTATION-GAP`.** If the eight §18.20.N facts cannot be
   unambiguously established (e.g. an N/A path-internal unit's completion after a context reset, with no
   commit and no runbook §20 field yet), §8a's fail-closed rule reports `DOCUMENTATION-GAP` and takes no
   automatic-continuation action. Traces: §18.20.N, §8a.
10. **A hard prerequisite alone does not authorize auto-run.** A unit that is a hard prerequisite (§18.15
    sense) but not plan-designated path-internal for the open path is never auto-run; its absence blocks.
    Traces: §18.20.I, §16a, §7.
11. **`REM-P0.2.B-03` Optional never auto-runs.** Classified Optional (§16a); named, if relevant, only as
    available-but-not-required; only an explicit operator `Execute REM-P0.2.B-03` would run it. Traces:
    §18.20.J, §16a.
12. **Capability-2 path cannot start Capability 3 automatically.** Immediately after
    `Approve CAPABILITY-GATE P0.2`, `EXE-P0.3.A` is stated as the next eligible unit and nothing about it
    is started, executed, or treated as pre-authorized. Traces: §18.20.E/M, §20, §29.
13. **Gate approval remains human-controlled.** No sequence in this skill ever emits or infers
    `Approve CAPABILITY-GATE P0.<n>` on the operator's behalf; §20 and §29 both restate this. Traces:
    §18.20.G, §20.
14. **Historical A/B behavior remains unchanged.** `EXE-P0.2.A`/`EXE-P0.2.B` are never re-run,
    re-verified under §19a/§21a, or described as `CONTINUING`. Traces: §18.20.O, §22.
15. **§31 is not triggered by ordinary continuation states.** §31's Trigger, as clarified in this
    amendment, is reached only by a genuine human boundary `Approve`; a path-internal `CONTINUING` /
    `CONTINUING (NOT APPLICABLE)` footer never reaches it. Traces: §31, §19a step 2/3, §19's table note.
16. **Governance-agent escalation still stops execution.** An `ESCALATED`/`PENDING` finding under
    `governance-matrix.yaml`'s existing consolidation rules stops an open path exactly as it would stop a
    standalone unit, independent of the unit's own technical AI Verification verdict. Traces: §18.20.K.11,
    §17.
17. **Boundary approval enumerates every path-internal unit and its continuation state.** At the path's
    boundary (Gate or an earlier stop), the resulting `Approve …` request names every path-internal unit
    completed since the path opened or last resumed and which continuation state each one reached —
    `CONTINUING` and `CONTINUING (NOT APPLICABLE)` units alike — never an implicit "everything up to
    here," and never treating an N/A unit's inclusion as its own individual approval. Traces: §18.20.G,
    HD-CE-05, HD-CE-17, §19 (`Approve` rows), §19a step 4, §20.

No required behavior in the task that produced this amendment is left uncovered by the seventeen items
above.
