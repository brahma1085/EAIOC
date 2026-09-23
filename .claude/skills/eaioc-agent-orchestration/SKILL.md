---
name: eaioc-agent-orchestration
description: >
  Decides WHEN the EAIOC governance agents (eaioc-code-architect, eaioc-code-reviewer,
  eaioc-code-verifier) must participate in an EAIOC execution unit, remediation unit, or Capability
  Gate review, at what granularity (STEP or CAPABILITY), and what governance evidence must be
  recorded — for any phase P0–P5 and any artifact type or language. Use in the main session whenever
  executing an `Execute EXE-*`/`Execute REM-*` unit or a `Review CAPABILITY-GATE` review, and when
  asked what governance applies to a change. Never authorizes execution.
---

# EAIOC Agent Orchestration

**What this skill is.** A decision layer that sits *inside* the existing Mode B protocol
(`docs/execution-plan.md` §18.11–§18.18). It decides which of the three governance agents a unit
needs, when they run, and what evidence goes into the unit's AI Verification report.

**What it is not.** It is not an execution model, and it grants no authorization. The separation:

| Layer | Defines | Where |
|---|---|---|
| Execution plan | WHAT must be done, in which unit, with which Definition of Done | `docs/execution-plan.md` (+ `implementation-plan.md` for tiers not yet atomized) |
| This skill | WHEN Architect / Reviewer / Verifier participate, at what granularity, with what evidence | here + `governance-matrix.yaml` |
| The agents | HOW each performs its analysis | `.claude/agents/eaioc-code-{architect,reviewer,verifier}.md`, sharing `.claude/skills/eaioc-governance/SKILL.md` |

Do not restate agent instructions here or in agent prompts — delegate by `subagent_type` and give
each agent only scope and context. Main session only: a governance subagent never runs this skill.

---

## 1. Invariants (never weakened)

1. One atomic unit per explicit user command; stop after its report. No automatic chaining — an agent
   PASS, a governance PASS, a passing suite, or a commit never authorizes the next unit (§18.11.12,
   §18.12.5).
2. The main session's §18.12 AI Verification of every unit is **always** performed, whatever the
   governance level. Agent reports are *evidence* for it, never a substitute for it and never human
   approval.
3. Human approval (`Approve <unit>`, `Approve CAPABILITY-GATE P0.<n>`) is the only authorization.
   No agent, and not this skill, may claim, infer, or simulate it.
4. A governance `BLOCKED` or `FAIL` that is not resolved inside the same unit's authorized scope makes
   the unit's verdict `AI VERIFICATION: BLOCKED` — no commit, no approval request.
5. `NOT_REQUIRED` is not `PASS`; `BLOCKED` is not `PASS`; `PENDING`/`ESCALATED` block any PASS verdict.
6. Capability-level deferral never skips a unit's §18.12 verification and never hides a meaningful
   change: the triviality test (§4) is strict, and anything failing it is STEP-level.
7. P0 rules are unchanged: remediation sequencing, the §18.15 guard, contract-correction-before-code,
   documentation authority (governance skill §1–§2), and "a blocked prerequisite is never bypassed
   because an agent says the code looks right".
8. Already-executed, approved units are never re-governed or re-executed; their evidence is examined
   only through the Capability Gate's Verifier pass.

---

## 2. Resolving the unit (phase-independent)

Read the live plan, never a cached assumption:

1. **Tier/phase.** Implementation priority tiers **P0–P5** are defined (`conventions.md` §25,
   `implementation-plan.md` §8–§13). **No P6 / "Phase 6" exists anywhere in the corpus** — if one is
   named, report it as undefined; do not invent it.
2. **Unit.** Only units defined in `execution-plan.md` exist. Today that is P0 only
   (`EXE-P0.<n>.<letter>`, `REM-P0.<n>.<letter>-NN`, 6 Capability Gates). P1+ are sequenced in
   `implementation-plan.md` but explicitly not atomized (`execution-plan.md` §19 onward). A command
   naming a unit the plan does not define is not executable — ask for the canonical ID (§18.11.9).
3. **Capability + sub-phase.** From the unit ID and its §18.4–§18.9 row (or §18.14 remediation table):
   capability, lifecycle sub-phase (A–H per `implementation-plan.md` §6), deliverable, files/modules,
   tests, failure-path test, Definition of Done, preconditions.
4. **Step.** The atomic unit *is* the step. Mode B defines no sub-steps inside a unit. If a unit's row
   lists several deliverables, they are parts of one step — classify them together (usually MIXED).
   If a future plan defines sub-steps, classify each, but still produce one verdict per unit.
5. **Repository state.** `git status --short`, `git diff`, `git diff --staged`, `git log --oneline -15`.
   Record pre-existing and unrelated changes (e.g. `.vscode/`, `CLAUDE.md`, status docs) *before*
   implementing so they are never attributed to the unit. Never reset, stash, or commit them.

---

## 3. Change classes and impact

Classify every changed path (from the actual diff after implementation; from the row's
Files/Modules beforehand). Class definitions, file patterns, and default agent requirements are in
`governance-matrix.yaml` (machine-readable, authoritative for this skill); the summary:

| # | Class | Recognized by (language-agnostic) |
|---|---|---|
| 1 | DOCUMENTATION-ONLY | Prose (`*.md`, `*.txt`, `*.html` docs, comments) that is **not** in the authoritative set |
| 1a | AUTHORITATIVE-DOC | Any Tier 1–2 document (governance skill §1), an ADR, or a unit design note that defines a contract (e.g. a Sub-phase A deliverable) — treated as API/INTERFACE/CONTRACT or ARCHITECTURAL |
| 2 | TEST-ONLY | Files under a test tree or test naming convention (`src/test/**`, `*Test.*`, `test_*.py`, `*_test.go`, `*.spec.ts`, fixtures, test config) |
| 3 | PRODUCTION-CODE | Executable/behavioral source in any language outside test trees (`.java .py .go .js .ts .kt .cs .rs .rb …`) |
| 4 | UI-CODE | Front-end artifacts: `.html .css .scss .jsx .tsx .vue .svelte`, front-end routing/state/API-client modules |
| 5 | CONFIGURATION | Runtime-read config (`application*.yml/.properties`, `.json/.yaml/.toml` read by code, env files) |
| 6 | INFRASTRUCTURE | `Dockerfile`, compose, Kubernetes manifests, Terraform/Pulumi/Bicep, Helm |
| 7 | SECURITY | Any change touching tenant isolation, authz/authn, fail-closed paths, PII/sensitive handling, secrets, `governance/` (SGE/DGE/TMG/HAG/CIS) |
| 8 | OBSERVABILITY | Logging, metrics, tracing, audit emission, metric names, telemetry sinks |
| 9 | API/INTERFACE/CONTRACT | Public types/signatures, schemas, DTOs, enums, error taxonomy, `core/interfaces|schemas|errors`, `interfaces.md` |
| 10 | ARCHITECTURAL | New package/module/component, dependency-direction change, cross-component flow, pipeline stage order, technology choice |
| 11 | DATA/PERSISTENCE | `*.sql`, migrations, store/repository implementations, key layouts, retention/deletion |
| 12 | BUILD/CI-CD | `pom.xml`, `build.gradle*`, `package.json`/lockfiles, `go.mod`, `pyproject.toml`, CI workflows, build scripts (`*.sh`, `*.ps1` used by build) |
| 13 | GENERATED ARTIFACT | Output of a generator (codegen, compiled assets, lockfile regeneration); `target/`, `build/`, `dist/` are never committed |
| 14 | MIXED | More than one class in one unit (the common case) |
| 15 | TRIVIAL/MECHANICAL | Only if the triviality test (§4) passes |

A path can carry several classes (e.g. a Java file changing a public record is PRODUCTION-CODE +
API/INTERFACE/CONTRACT; a store change enforcing `tenant_id` is PRODUCTION-CODE + SECURITY +
DATA/PERSISTENCE). Always take the union.

**Impact** (deterministic, highest rule that matches wins — no numeric scores):

| Impact | Rule |
|---|---|
| ARCHITECTURAL | Class 10, or any change to a Tier-1 document, dependency direction, component/package ownership, pipeline order, or a technology/ADR-governed choice (all ADRs are `PROPOSED`) |
| HIGH | Class 7, 9, or 11; any public-surface or contract change; fail-open/fail-closed behavior; tenant isolation; savings/`verified` accounting; a Tier-2 document |
| MODERATE | New or changed behavior in classes 3, 4, 5, 6, 8, 12; new/changed test assertions |
| LOW | Non-behavioral changes that still fail the triviality test (e.g. test fixture values, private refactor that changes structure) |
| NONE | Passes the triviality test |

---

## 4. Governance level: STEP vs CAPABILITY

**Triviality test** — a change is TRIVIAL/MECHANICAL (impact NONE) only if **all** hold, checked
against the actual diff:
1. It changes only whitespace/formatting, comments/Javadoc prose, import order, or typos in
   non-authoritative prose.
2. No public or package-visible signature, type, field, enum constant, annotation, or serialized name
   changes; no runtime-read configuration value changes.
3. No test assertion, test input that an assertion depends on, or test selection changes.
4. No file in the authoritative set (class 1a) changes.
5. The build compiles and the full test suite result is unchanged.

**Rules:**
- **STEP** level (agents run within the unit, before the unit's verdict) whenever impact is LOW or
  higher. This is the default for every Sub-phase B–H unit that produces code or tests, and for any
  Sub-phase A unit producing a contract/design artifact.
- **CAPABILITY** level (agents deferred to the capability's Gate review) only when impact is NONE.
  The unit still gets its full §18.12 AI Verification by the main session; the governance record
  lists the deferral and why.
- The planned level is re-evaluated against the actual diff before the verdict (§6). A deferral that
  no longer passes the triviality test is escalated to STEP immediately.

---

## 5. Agent invocation matrix

`YES` = required at the stated level. `COND` = required when any listed trigger in
`governance-matrix.yaml` holds, otherwise `NOT_REQUIRED` with the reason recorded. `CAP` = deferred
to the Capability Gate. Required evidence per row is in the YAML.

| Class (impact) | Architect | Reviewer | Verifier | Level |
|---|---|---|---|---|
| TRIVIAL/MECHANICAL (NONE) | no | CAP | CAP | CAPABILITY |
| DOCUMENTATION-ONLY, non-authoritative (NONE/LOW) | no | no | CAP | CAPABILITY |
| AUTHORITATIVE-DOC (HIGH/ARCHITECTURAL) | YES | COND (if implementation also changes) | YES | STEP |
| TEST-ONLY, fixtures/data only (LOW) | no | YES | YES | STEP |
| TEST-ONLY, new/changed assertions (MODERATE) | COND (test establishes a contract) | YES | YES | STEP |
| PRODUCTION-CODE (MODERATE+) | COND | YES | YES | STEP |
| UI-CODE (MODERATE+) | COND | YES | YES | STEP |
| CONFIGURATION, behavioral (MODERATE+) | COND | YES | YES | STEP |
| CONFIGURATION, non-behavioral (NONE) | no | CAP | CAP | CAPABILITY |
| INFRASTRUCTURE (MODERATE+) | COND (topology / ADR-governed) | YES | YES | STEP |
| SECURITY (HIGH) | COND (security architecture) | YES | YES | STEP |
| OBSERVABILITY (MODERATE+) | COND (new metric name, sink, or log contract) | YES | YES | STEP |
| API/INTERFACE/CONTRACT (HIGH) | YES | YES (if code) | YES | STEP |
| ARCHITECTURAL (ARCHITECTURAL) | YES | YES (if code) | YES | STEP |
| DATA/PERSISTENCE (HIGH) | YES | YES | YES | STEP |
| BUILD/CI-CD (MODERATE+) | COND (new dependency, plugin, platform — ADR-0003) | YES | YES | STEP |
| GENERATED ARTIFACT | COND | YES (on the generator/source) | YES (reproducibility + contract) | STEP |
| MIXED | union of the rows above | one pass across the whole change | one pass across the whole change | highest of the parts |

Architect `COND` triggers (any one): new package/module/component; new public type or public
signature; new cross-package dependency or dependency direction change; new third-party dependency;
provider/model/framework name in core; change to fail-open/fail-closed behavior or tenant-isolation
mechanism; new metric name or telemetry sink; persistence or key-layout change; anything the plan
row marks as design or contract; any decision that would need an ADR.

**Sub-phase defaults** (starting point only — the diff decides): A → AUTHORITATIVE-DOC (Architect +
Verifier); B → PRODUCTION-CODE + API/CONTRACT + TEST-ONLY (all three); C → PRODUCTION-CODE +
ARCHITECTURAL (all three); D → OBSERVABILITY + TEST-ONLY (Reviewer + Verifier, Architect COND);
E → SECURITY + TEST-ONLY (Reviewer + Verifier, Architect COND); F → often `NOT APPLICABLE` (no agents;
record NOT_REQUIRED with the N/A evidence); G/H → TEST-ONLY or PRODUCTION-CODE (Reviewer + Verifier).

---

## 6. Workflow inside a unit

```
Execute <unit>                         (explicit user command — the only trigger)
  1. Resolve unit (§2); record pre-existing git state.
  2. Provisional classification from the plan row (§3) → level + agents (§4, §5).
  3. If Architect required: run eaioc-code-architect BEFORE implementing, on the planned design.
       BLOCKED / SOURCE CONFLICT → stop; unit verdict BLOCKED.
  4. Implement exactly the unit (normal Mode B rules).
  5. Final classification from the actual diff (only files this unit changed).
       Anything higher than provisional → ESCALATE (§7): add the missing agents.
  6. Run required Reviewer and Verifier — in parallel (independent; neither receives the other's
     report). An Architect report may be passed to them as evidence only.
  7. Consolidate (§8). FAIL findings that are within the unit's authorized files may be fixed and the
     failing agent re-run; anything else → BLOCKED.
  8. Main session writes the §18.12 AI VERIFICATION REPORT (unchanged format) + GOVERNANCE RECORD annex.
  9. Commit only if the verdict is PASS / PASS WITH DOCUMENTED NON-BLOCKING GAP.
 10. End with the §18.12.4 checkpoint and STOP.
```

Prompts to agents contain: unit ID, scope (exact files/commits), the plan row reference, the
provisional/final classification, and any prior agent report marked "evidence only — re-derive".
They never contain "approved", "already verified", or instructions that override the agent's own
definition.

**Capability Gate (`Review CAPABILITY-GATE P0.<n>`)** — in addition to the §18.10 questions:
- **Verifier — always**, over the complete capability: every file changed by the capability's
  committed units (first..last commit), full requirement coverage, tests, docs, contracts, security,
  observability, applicable quality gates. Independent; prior reports are evidence only.
- **Reviewer** over every change that was deferred at CAPABILITY level, plus the final state of files
  touched by more than one unit.
- **Architect** only if a deferred change was escalated or the final state shows architecture drift.
- Any agent BLOCKED/FAIL → `AI CAPABILITY GATE VERIFICATION: BLOCKED`.

**Phase level.** No phase gate exists in the plan; do not invent one. When asked for a phase
report, reconstruct Phase → Capabilities → Units → governance from the commit `Governance-*`
trailers (§9), the Gate reviews, and `git log`.

---

## 7. Escalation

Escalation is one-way (never de-escalate within a unit) and is triggered by the diff or by any agent
finding. Set the affected agent's status to `ESCALATED`, run it, and resolve before the verdict.

| Discovered | Escalate to |
|---|---|
| "Minor"/trivial change fails the triviality test | STEP level; Reviewer + Verifier |
| Public API, schema, enum, error, or serialized name changed | + Architect (class 9) |
| Authoritative document text changed, or code now contradicts one | + Architect + Verifier (1a); if a contract must change → STOP: contract corrections happen only through user-directed documentation passes |
| Tenant isolation, authz, fail-closed, PII, secrets touched | + Architect if the mechanism changed (class 7) |
| New metric/log field/sink or audit behavior | + Architect if a name or sink is new (class 8) |
| New dependency, store, key layout, technology, cross-package import | + Architect (classes 10/11/12) |
| Test change reveals a requirement or design gap | + Verifier (and Architect if design) — report the gap; do not code around it |
| Agent reports a source conflict | governance BLOCKED — `SOURCE-CONFLICT` stops dependent conclusions |

---

## 8. Statuses and consolidation

Per agent: `PASS` · `FAIL` · `BLOCKED` · `NOT_REQUIRED` (reason mandatory) · `ESCALATED` · `PENDING`.

Agent verdict mapping:

| Agent verdict | Status |
|---|---|
| Architect `APPROVED FOR IMPLEMENTATION` | PASS |
| Architect `APPROVED WITH CONDITIONS` | PASS only once each condition is shown met in the final diff (Verifier/main session); otherwise FAIL |
| Architect `BLOCKED` / `SOURCE CONFLICT` | BLOCKED |
| Reviewer `PASS` | PASS |
| Reviewer `PASS WITH REQUIRED FOLLOW-UP` | PASS if every follow-up is LOW/INFORMATIONAL and recorded; any MEDIUM+ follow-up → FAIL |
| Reviewer `CHANGES REQUIRED` | FAIL |
| Reviewer `BLOCKED BY SOURCE CONFLICT` | BLOCKED |
| Verifier `CONFORMANT` | PASS |
| Verifier `CONFORMANT WITH DOCUMENTED GAPS` | PASS with gaps (each gap must be a registered/deferred, non-blocking item) |
| Verifier `NON-CONFORMANT` | FAIL |
| Verifier `BLOCKED BY SOURCE CONFLICT` | BLOCKED |

Consolidated governance status: any BLOCKED → **BLOCKED**; else any FAIL → **FAIL**; else any
PENDING/ESCALATED → **PENDING**; else **PASS**. Mapping to the §18.12 verdict:
BLOCKED/FAIL/PENDING → `AI VERIFICATION: BLOCKED`; PASS with recorded gaps → at best
`PASS WITH DOCUMENTED NON-BLOCKING GAP`; PASS → the main session's own verdict (which may still be
BLOCKED for its own reasons). Every BLOCKED/FAIL names: finding, artifact, requirement, source
document + section, severity, remediation. Never downgrade a blocker.

---

## 9. Evidence record and traceability

Append this annex to the §18.12 report (the report's own format is unchanged):

```
GOVERNANCE RECORD
Phase/Tier: P<k>   Capability: <n — name>   Unit: <ID>   Step: <unit | named sub-step>
Classes: <list; MIXED if >1>   Impact: <NONE|LOW|MODERATE|HIGH|ARCHITECTURAL>
Level: STEP | CAPABILITY — reason: <rule applied>
Files (this unit): <paths>   Excluded (pre-existing/unrelated/generated): <paths>
Architect: <status> — <verdict or NOT_REQUIRED reason>
Reviewer:  <status> — <verdict; BLOCKER/HIGH count>
Verifier:  <status> — <verdict; matrix rows / statuses summary>
Escalations: <from → to, trigger>
Findings carried: <IDs/titles, severity, disposition>
Consolidated governance: PASS | FAIL | BLOCKED | PENDING
Gate status: <unit verdict>; human approval: PENDING (never set by AI)
```

When the unit is committed, add trailers to the commit body (the title convention is unchanged):

```
Governance-Level: STEP
Governance-Classes: PRODUCTION-CODE, API/INTERFACE/CONTRACT, TEST-ONLY
Governance-Agents: architect=PASS reviewer=PASS verifier=PASS
```

Traceability uses the existing model, not a new one: requirement IDs (`OBJ/SEC/NFR/AC/H`) →
`architecture.md` → `INTF-NNN` → capability/unit (`execution-plan.md`) → commit → tests → the
Verifier's matrix rows (which cite those IDs) → the `Governance-*` trailers → `Approve` commands
recorded in `CLAUDE.md`/runbook status. `requirements-traceability.md` remains the requirement-level
register; this skill adds no new ID series.

Units with no commit (N/A sub-phases, Gate reviews) have their record only in the report; the
runbook/`CLAUDE.md` status update is where it persists, when the user asks for it.
