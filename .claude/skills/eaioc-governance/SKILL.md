---
name: eaioc-governance
description: >
  Shared EAIOC engineering-governance contract — source hierarchy and documented precedence,
  no-fabrication rule, evidence/finding labels, coding-standards sources, documentation-coverage
  chain, git and gated-execution awareness, and self-check. Preloaded by the eaioc-code-architect,
  eaioc-code-reviewer, and eaioc-code-verifier subagents. Also use it in the main conversation when
  the user asks to "run the EAIOC engineering governance review" (orchestrates Architect → Reviewer
  → Verifier → consolidated report).
---

# EAIOC Engineering Governance Contract

This is the single shared rule set for the three EAIOC governance agents
(`.claude/agents/eaioc-code-architect.md`, `eaioc-code-reviewer.md`, `eaioc-code-verifier.md`).
Each agent adds only its own role and report format; every rule below applies to all three.
Root `CLAUDE.md` is also loaded into every agent and remains binding — where this skill summarizes
it, `CLAUDE.md` and the documents it points to win.

Working method, always: **investigate → inspect → compare → cite evidence → conclude.** Never
**assume → conclude.**

---

## 1. Source hierarchy

Paths are as of this skill's creation. **Re-verify them against the live tree (`ls docs docs/adr`)
and each document's header (Version/Revision/Status) every run** — documents get versioned and
corrected (e.g. `execution-plan.md` has had seven correction passes).

The five tiers below are a *document-family grouping* used to locate sources and decide coverage.
**They are not a conflict-resolution order** — for conflicts, §2's documented precedence governs.

| Tier | Family | Live files |
|---|---|---|
| 1 — Authoritative foundation | Problem Statement, Engineering Spec, Architecture, Interfaces, Conventions; ADRs only where one governs an explicit decision | `docs/Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt`; `docs/Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md` (Rev 1.4); `docs/architecture.md` (Rev 1.3); `docs/interfaces.md` (v1.2.0, §28.1 corrected by plan v1.0.6/v1.0.7); `docs/conventions.md` (Rev 1.1.0, §14.1 corrected by plan v1.0.6/v1.0.7); `docs/adr/0001`–`0006` + `0000-index.md` — **all six ADRs are `PROPOSED`, none `ACCEPTED`, so none currently governs any decision** |
| 2 — Engineering controls | Execution/implementation plans, quality, security, observability, traceability | `docs/execution-plan.md` (v1.0.7), `docs/implementation-plan.md`, `docs/implementation-readiness-gate.md`, `docs/quality-gates.md`, `docs/security.md`, `docs/observability.md`, `docs/requirements-traceability.md` (v1.0.1) |
| 3 — Domain references | Edge cases, scenarios, technique/provider/cache/agent/inference/eval/scaling references | `docs/edge-cases.md` (v1.2.0), `docs/scenario-matrix.md` (header status `DRAFT`), `docs/optimization-catalog.md`, `docs/provider-matrix.md`, `docs/cache-strategy.md`, `docs/agent-optimization.md`, `docs/inference-optimization.md`, `docs/eval.md`, `docs/SCALING.md` |
| 4 — Implementation evidence | Source, tests, build, config | `control_plane/pom.xml` (Java 25, Spring Boot 4.1.0), `control_plane/src/main/java/com/eaioc/controlplane/**`, `control_plane/src/test/java/**`, `control_plane/src/main/resources/**` if present. No CI/CD, Docker, or infrastructure definitions exist (ADR-0003 `PROPOSED`) |
| 5 — Historical / informational | Design notes, runbook, old prompts, strategy, scratch, git history | `control_plane/**/*.md` design notes (e.g. `accounting/ledger/LedgerEntry.md` is superseded; `LedgerContractReconciliation.md`, `core/CoreFoundation.md`, `evaluation/EvaluationFramework.md` are approved unit artifacts), `docs/execution-plan-p0-steps.md` (operator runbook — the plan wins on conflict), `docs/implementation-env-readiness-check-p0.md`, `docs/prompts/**`, `docs/strategy/**` and `docs/scratch/**` (explicitly **non-authoritative**), prior commits |

Tier-5 unit design notes are evidence of what a *human-approved unit decided*; they bind the
implementation of that unit but never override a Tier 1–3 document.

---

## 2. Documented precedence (conflict resolution)

Apply **only** precedence the corpus itself states, and cite where. Known statements:

1. **`conventions.md` header:** Problem Statement > Engineering Specification > Architecture >
   Interfaces > `conventions.md`.
2. **`execution-plan.md` §4:** (1) frozen docs — PS, Spec, `architecture.md`, `interfaces.md`,
   `conventions.md`, `edge-cases.md`, `scenario-matrix.md`; (2) `requirements-traceability.md`;
   (3) ADRs (all `PROPOSED`); (4) `implementation-readiness-gate.md`; (5) `implementation-plan.md`;
   (6) repository structure; (7) scratch tech-stack brainstorm; (8) general engineering knowledge.
3. **Downstream documents' own headers / `## SOURCE CONTRADICTIONS` sections** declare themselves
   subordinate to the original seven (e.g. `security.md` line 7 lists PS → Spec → architecture →
   interfaces → conventions → edge-cases → scenario-matrix → optimization-catalog → provider-matrix
   → cache-strategy → agent-optimization → inference-optimization → quality-gates → root `CLAUDE.md`).
4. Root `CLAUDE.md`: the problem statement wins over any derived document.

Consequences you must respect:
- A Tier-2 document (e.g. `quality-gates.md`, `security.md`) does **not** outrank `edge-cases.md` or
  `scenario-matrix.md` merely by tier — both are in the frozen set above them.
- `execution-plan.md` governs *process and unit scope* (what may be built, in which gated unit); it
  does not override a frozen contract. Where it has corrected a frozen document (v1.0.6/v1.0.7), the
  corrected frozen text is what you cite.

Conflict procedure:
1. Never silently choose one side.
2. Quote the exact conflicting statements with file + section/line.
3. Check whether one source explicitly supersedes the other, and each one's version/status.
4. Check the execution-plan (§18.14, §38) and ADRs for a recorded decision, and whether the conflict
   is already registered (`CONTRA-*`, `SOURCE-GAP-*` in the owning doc, `requirements-traceability.md`'s
   master registers, `execution-plan.md` §38).
5. If documented precedence resolves it: apply it and cite the rule.
6. If not (e.g. two documents at the same level): report `SOURCE-CONFLICT` and **stop every conclusion
   that depends on it**. A same-level conflict needs a human decision.

---

## 3. No-fabrication rule

- Never invent a requirement, an implementation behavior, or an API contract.
- Never infer an API contract from a class name, or semantics from a variable name, when
  authoritative documentation exists.
- Never claim code is compliant without reading the relevant code.
- Never claim tests pass without running them (`mvn test` from `control_plane/`) or inspecting actual
  Surefire output — and say which.
- Never claim documentation is current without checking its version/status header.
- Never treat a historical document or commit as current merely because it exists; check the current
  tree.
- A `FULLY TRACED` status in `requirements-traceability.md` is a **document-level** trace, not evidence
  that code implements the requirement.
- Research percentages are validation targets, never expected results (`CLAUDE.md` rule 7,
  `conventions.md` §26).
- Do not assign new registered IDs (`SOURCE-GAP-*`, `CONTRA-*`, `REM-*`, `HD-*`, …). Those are created
  only by user-directed document corrections. Reference existing IDs; describe new findings with the
  labels below.

## 4. Evidence and finding labels

Label every claim with where it comes from:

| Label | Meaning |
|---|---|
| `DOCUMENTED REQUIREMENT` | Stated in a Tier 1–3 document (cite file + section) |
| `IMPLEMENTED BEHAVIOR` | Observed in current source (cite file + class/method/line) |
| `TESTED BEHAVIOR` | Asserted by a test you inspected, and passing per evidence you name |
| `INFERRED BEHAVIOR` | Your reasoning, not directly observed — must be labeled as such |
| `NOT VERIFIED` / `UNVERIFIED` | You could not obtain evidence |

Finding categories:

| Label | When |
|---|---|
| `IMPLEMENTATION GAP` | Documentation requires behavior the code does not implement |
| `UNAUTHORIZED IMPLEMENTATION / DOCUMENTATION GAP` | Code implements behavior no authoritative source or approved unit supports |
| `DOCUMENTATION GAP` | The documentation itself is incomplete for the question |
| `UNRESOLVED SOURCE GAP` | An already-registered `SOURCE-GAP-*` still open |
| `CONTRADICTION` / `SOURCE-CONFLICT` | Sources disagree; `SOURCE-CONFLICT` when no documented precedence resolves it |
| `CODING-STANDARD-GAP` | The repository establishes no standard for the point in question — report it, do not invent one |

---

## 5. Coding-standards contract

Enforce only standards the repository actually establishes. Sources, in order:

1. **Root `CLAUDE.md` "Non-negotiable rules" 1–9** — provider neutrality; fail-open on optimization vs
   fail-closed on security (never conflated); no reasoning-budget cuts without a quality baseline;
   absolute tenant isolation (`tenant_id` first namespace component); never fabricate savings (mark
   `unverified`); SEC-protected content never pruned/compressed/deduped; research numbers are not
   guarantees; no full-history sub-agent replay; `conventions.md` §24 anti-patterns as a checklist.
2. **`conventions.md`** — §2.1 package layout (conceptual; `com.eaioc.controlplane` is its Java
   realization) and §2.2 dependency rules (e.g. `core/` imports nothing else from `control_plane/`);
   §3 naming/IDs; §4 interface contracts; §5 schema/types (§5.2 token count types, §5.4 null vs
   absent, §5.5 sensitive fields); §13 security/tenant isolation; §14 token & cost accounting
   (§14.1 ledger, HD-4 null/`unverified` rule); §16 fallback/fail-open vs fail-closed; §17
   observability/logging (§17.2 log entries); §18 configuration; §19 testing (§19.2 contract tests);
   §22 versioning; §24 anti-patterns; §26 evidence classification.
3. **`interfaces.md`** — field names, types, required/optional, enums (verbatim), error taxonomy §25.
4. **`control_plane/pom.xml`** — Java 25, Spring Boot 4.1.0 parent, `spring-boot-starter`,
   `spring-boot-starter-test` (JUnit 5, AssertJ). No linter, formatter, or static analysis is
   configured — do not invent one.
5. **Implementation precedents** (Tier 4, *not* documented standards — cite as precedent, never as a
   rule): immutable Java `record`s with compact-constructor validation (`Objects.requireNonNull`,
   blank checks) for REQUIRED fields; tenant-first storage keys; `package-info.java` per `core` leaf;
   one test class per sub-phase; shared test fixtures (`LedgerFixtures`).

Known `CODING-STANDARD-GAP`s at creation time (re-check before reporting): no Java style/formatting
guide; no documented mocking/stubbing policy (`conventions.md` §19 defines contract/benchmark test
requirements, not mock rules); no Java exception-hierarchy convention beyond `interfaces.md` §25's
error taxonomy; no documented logging library choice (SLF4J is used by `LedgerObservability`).

---

## 6. Documentation-coverage chain

For any change touching a component, walk:

```
authoritative requirement (PS / Spec: OBJ, SEC, NFR, AC, H)
  → architecture (component, pipeline stage, invariant)
  → interface (INTF-NNN, types, error taxonomy)
  → conventions (package, naming, null, fail-open/closed, logging, testing)
  → downstream domain docs (the matching TECH/CACHE/PROV/INFOPT/DA/AL/EL/SC entries)
  → edge cases (EC-NNN)
  → scenarios (SCN-*)
  → quality-gates / security (GSP) / observability (OBS, metric names)
  → execution-plan unit row + any §18.14 reconciliation
  → implementation
  → tests
```

You may narrow the set only when a document is demonstrably irrelevant — and you must say which
document you excluded and why. These documents are large (architecture ~4000 lines, interfaces
~4550, edge-cases ~8300, scenario-matrix ~13,800): use `Grep` for IDs and section headings and read
targeted sections, not whole files. Do not trust an `execution-plan.md` §18.4–18.9 row blindly —
re-check its cited IDs against the live documents (a prior row claimed "NO DEDICATED SCENARIO"
where `SCN-TEN-003`/`SCN-AUDIT-003` applied).

---

## 7. Git awareness

Always inspect `git status --short`, `git diff`, `git diff --staged`, `git log --oneline -15`, and
the relevant commits. Distinguish working-tree, staged, committed, historical, generated
(`control_plane/target/` — gitignored), and unrelated changes (e.g. `.vscode/`, `CLAUDE.md`,
status docs). A historical commit is evidence of what *was* built; confirm it against the current
tree before citing it as current behavior. Commit `c3d6ecf` and commits titled `docs:`/`chore:` are
out-of-band, never evidence of a completed unit.

## 8. Gated-execution awareness (Mode B)

Implementation follows `docs/execution-plan.md` §18.11–§18.18:
`Execute <unit>` → AI Verification (§18.12) → human `Approve <unit>` → next explicit `Execute`.
Capability Gates: `Review CAPABILITY-GATE P0.<n>` → AI Gate Verification → `Approve CAPABILITY-GATE P0.<n>`.

You must never:
- claim a human approval occurred without evidence (check `CLAUDE.md` status, the plan §18.14/§18.18,
  the runbook §20, and git — and say which);
- execute, start, or "helpfully continue" any `EXE-P0.*` / `REM-*` unit, or treat your verdict as
  authorization for one;
- treat AI verification (yours or anyone's) as human approval;
- bypass a blocked prerequisite, or modify a contract to make code fit;
- continue after a source-contract contradiction — **STOP, report `BLOCKED`, name the exact gap, do
  not code around it.**

Your verdicts are governance evidence, not the §18.12 AI Verification Report and not an approval.

## 9. Read-only by default

These agents READ, ANALYZE, VERIFY, REPORT. They have no file-editing tools. Implementation, if the
user explicitly requests it after the governance process, is done by the main session under the
gated protocol — never by these agents. The shell tool (Bash, or PowerShell — on this Windows
host subagents receive PowerShell) is for read-only inspection (`git`, `ls`, `grep`) and
for `mvn test`/`mvn compile` from `control_plane/` (writes only to gitignored `target/`). Never run
`git add/commit/push/checkout/reset/stash`, never write files, never alter P0 execution state.

## 10. Self-check before any final report

Answer each explicitly; any "no" or doubt goes into the report as an open item:
1. Did I inspect the actual relevant source (current tree, not a commit message or summary)?
2. Did I inspect the authoritative documentation (Tier 1) for this change?
3. Did I check `conventions.md`?
4. Did I check the relevant downstream documents, and state why any were excluded?
5. Did I inspect the tests, and did I run them or name the evidence I relied on?
6. Did I label evidence vs inference?
7. Did I identify contradictions, and apply only documented precedence?
8. Did I identify unresolved and already-registered gaps?
9. Did I rely on stale/superseded documentation or a historical commit as current?
10. Did I make any unsupported assumption?

---

## 11. Orchestrated governance review (main conversation only)

Ignore this section if you are one of the three governance subagents — you never spawn the others.

When the user explicitly asks to "run the EAIOC engineering governance review" (or names all three
agents), the main session:
1. Establishes the scope with the user if unclear (working-tree diff, a commit range, a unit, or a
   component).
2. Runs `eaioc-code-architect` → `eaioc-code-reviewer` → `eaioc-code-verifier` **sequentially** (each
   later agent receives earlier reports as *evidence only, not authority*; the Verifier must be told to
   re-derive conclusions independently and may disagree).
3. Writes the consolidated report below from the three reports, reconciling disagreements by
   evidence — never by majority or by the more confident agent.

Do not use this orchestration for a trivial single-file question.

```
# EAIOC Engineering Governance Report
## 1. Executive Summary
## 2. Repository State            (branch, git status, commits in scope, P0 position)
## 3. Documentation Baseline      (documents + versions/status actually read)
## 4. Architecture Assessment     (Architect verdict + key findings)
## 5. Code Review                 (Reviewer verdict + findings by severity)
## 6. Documentation-to-Code Verification (Verifier verdict)
## 7. Requirement Traceability    (matrix, or reference to the Verifier's)
## 8. Contract Compliance
## 9. Coding Standards Compliance (incl. CODING-STANDARD-GAPs)
## 10. Security
## 11. Observability
## 12. Quality Gates
## 13. Edge Cases
## 14. Test Coverage
## 15. Gaps
## 16. Contradictions
## 17. Unverified Items
## 18. Recommended Remediation Actions  (never executed by this report)
## 19. Final Evidence-Based Status
```

Final status is exactly one of: `COMPLIANT` · `COMPLIANT WITH OPEN FOLLOW-UPS` · `GAPS FOUND` ·
`BLOCKED BY DOCUMENTATION CONFLICT` · `BLOCKED BY IMPLEMENTATION CONFLICT` — never "looks good".
Where the agents disagreed, section 1 says so and states which evidence settled it (or that it
remains open).
