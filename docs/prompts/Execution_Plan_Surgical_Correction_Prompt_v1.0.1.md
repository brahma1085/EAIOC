# EAIOC — SURGICAL CORRECTION PROMPT
## execution-plan.md v1.0.0 → v1.0.1
### Planning Artifact Correction Only — NO CODE IMPLEMENTATION

You are performing a **surgical correction pass** on the existing:

    docs/execution-plan.md

Do NOT generate source code.
Do NOT execute any P0 implementation unit.
Do NOT modify any authoritative EAIOC document other than the execution-plan artifact itself.
Do NOT redesign the EAIOC architecture.
Do NOT reopen already-settled brainstorming decisions unless the validation below finds a direct contradiction.

The sole purpose of this task is to correct and revalidate the generated execution plan so that it is internally consistent with the gated execution model established by the EAIOC documentation corpus and the consolidated master prompt.

---

# 1. AUTHORITATIVE BASELINE

Treat these as authoritative in this order:

1. Approved/frozen EAIOC documentation corpus
2. requirements-traceability.md
3. ADR corpus
4. implementation-readiness-gate.md
5. implementation-plan.md
6. Current docs/execution-plan.md
7. EAIOC technology-stack brainstorming baseline

The current execution-plan.md is a downstream planning artifact. Correct it only where required to bring it into alignment with the higher-authority sources and the execution protocol.

The existing execution plan explicitly states that it is a downstream implementation-planning document and does not replace the authoritative corpus. Preserve that boundary.

---

# 2. CURRENT FILE TO CORRECT

File:

    docs/execution-plan.md

Current version:

    1.0.0

Target version:

    1.0.1

The correction must be surgical.

Do not rewrite the entire document merely for stylistic reasons.
Do not shorten or remove validated traceability.
Do not introduce unrelated improvements.

---

# 3. VERIFIED ISSUES TO CORRECT

## ISSUE 1 — Sub-phase A/B commit-boundary contradiction

Current execution-plan.md contains a contradiction in Capability 1, Sub-phase A.

The Sub-phase A row says the design note is:

    "committed alongside Sub-phase B"

This violates the execution protocol:

    ONE lifecycle sub-phase
        =
    ONE commit
        =
    ONE approval checkpoint

The execution model requires:

    A → verify → commit → AWAITING APPROVAL
    B → verify → commit → AWAITING APPROVAL
    C → verify → commit → AWAITING APPROVAL
    ...
    H → verify → commit → Capability Gate

### Required correction

Change the Capability 1 Sub-phase A definition so that:

- Sub-phase A has its own standalone deliverable.
- Sub-phase A has its own verification/evidence.
- Sub-phase A has its own commit.
- Sub-phase A does NOT say it is committed alongside B.
- Sub-phase B starts only after explicit human approval of A.

Apply the same validation principle to ALL six P0 capabilities.

Search every Sub-phase A–H definition and ensure no wording implies that two lifecycle sub-phases share:

- one commit
- one approval checkpoint
- one implementation unit
- one validation checkpoint

If any such wording is found, correct it.

Do not invent new lifecycle stages.

---

# 4. ISSUE 2 — "Parallel-capable" wording conflicts with execution protocol

The execution plan currently contains wording stating that some capabilities, especially Capabilities 1–3, are "parallel-capable" or may proceed in any equivalent order.

The dependency model may legitimately say that certain capabilities have no cross-capability prerequisite.

However, the actual execution protocol is intentionally sequential:

- no parallel capability execution
- no parallel lifecycle sub-phases
- one active capability at a time
- one active sub-phase at a time
- explicit human approval between every sub-phase

Therefore distinguish:

### Dependency independence

from:

### Execution parallelism

A capability can have:

    No technical prerequisite

while still being:

    Sequentially scheduled by the controlled execution protocol

### Required correction

Replace any wording that could reasonably imply that Claude Code may execute Capabilities 1, 2, or 3 in parallel.

The correct meaning should be:

- Capabilities 1, 2 and 3 have no cross-capability prerequisite.
- The plan nevertheless preserves a deterministic sequential order.
- No parallel execution is permitted in Mode B.
- No next capability begins until the previous capability's Capability Gate is reviewed and explicitly approved.

Update all relevant sections consistently, including:

- P0 execution order
- release sequencing
- final report / summary
- any other statement that could imply permitted parallel execution

Do NOT change the dependency facts themselves.

---

# 5. ISSUE 3 — Validate the atomic execution-unit boundary

The execution plan defines:

    EXE-P0.<capability>.<sub-phase>

and the lifecycle:

    A
    B
    C
    D
    E
    F
    G
    H
    Capability Gate

The document must make the atomic boundary unambiguous.

For every one of the six capabilities:

1. Confirm A is independently executable and reviewable.
2. Confirm B is independently executable and reviewable.
3. Confirm C is independently executable and reviewable.
4. Confirm D is independently executable and reviewable.
5. Confirm E is independently executable and reviewable.
6. Confirm F is independently executable and reviewable.
7. Confirm G is independently executable and reviewable.
8. Confirm H is independently executable and reviewable.
9. Confirm Capability Gate occurs only after H is separately completed/approved.

There must be no hidden batching.

The following pattern is REQUIRED:

    EXE-P0.1.A
        ↓
    verification
        ↓
    commit
        ↓
    AWAITING APPROVAL
        ↓
    EXE-P0.1.B
        ↓
    verification
        ↓
    commit
        ↓
    AWAITING APPROVAL
        ↓
    ...
        ↓
    EXE-P0.1.H
        ↓
    verification
        ↓
    commit
        ↓
    Capability Gate
        ↓
    AWAITING APPROVAL
        ↓
    next capability

Repeat this logical pattern for P0.2 through P0.6.

Do not add a new ID series.

Do not change the lifecycle model.

---

# 6. ISSUE 4 — Validate "one commit per sub-phase"

The execution plan must consistently state:

    one lifecycle sub-phase = one git commit

and NOT:

    one capability = one commit

or:

    multiple sub-phases = one commit

Verify all references to:

- git commits
- approval checkpoints
- execution units
- rollback

Correct any wording that conflicts with the one-sub-phase/one-commit rule.

Rollback language should remain compatible with this discipline:

- each sub-phase can be reverted through its own commit
- no broad rollback should be implied for unrelated sub-phases
- no later sub-phase may depend on uncommitted work from an earlier sub-phase

Do not invent a more complex release branching strategy.

---

# 7. ISSUE 5 — Validate "stop after every sub-phase"

The execution plan must make it impossible to interpret the plan as:

    "Execute all of P0.1"

in one Claude Code turn.

The intended command boundary is:

    Execute EXE-P0.1.A

then stop.

After human approval:

    Execute EXE-P0.1.B

then stop.

And so on.

Ensure the final document clearly states:

- Claude Code must implement only the requested execution unit.
- Claude Code must not automatically continue to the next sub-phase.
- Claude Code must not automatically continue to the next capability.
- A Capability Gate does not imply automatic authorization for the next capability.
- Explicit human approval is required after every sub-phase and after each Capability Gate.

Do NOT remove support for the convenient capability-level shorthand if it exists, but make the true atomic boundary explicit.

---

# 8. ISSUE 6 — Validate the six-capability scope

The execution plan must remain limited to these six currently clear P0 capabilities:

1. Token Accounting and Cost Ledger
2. Baseline Benchmark Harness + Quality Evaluation
3. Sanitizer
4. Context Policy
5. Prompt Assembler
6. Output Controls

Observability must remain outside this execution slice because it is still:

    READY WITH CONDITION

and the implementation-readiness-gate condition has not been silently resolved.

Do not add Observability implementation tasks.

Do not add P1 implementation tasks.

Do not atomize P1–P5 during this correction.

The execution plan may continue to describe P1–P5 as future/deferred planning boundaries, but must not turn them into current executable units.

---

# 9. ISSUE 7 — Preserve the technology baseline exactly as validated

Do not change the validated technology baseline during this correction.

Preserve:

### P0 request-critical core
- Java 25
- Spring Boot 4.1.x
- Maven

### P0 proving environment
- Docker
- Docker Compose

### Future/enterprise
- PostgreSQL 18.x + pgvector
- Redis 8.x
- Kafka 4.3.x
- Python 3.14.x + FastAPI
- Go 1.27.x
- OpenTelemetry stack
- React + TypeScript
- Kubernetes + Helm
- GitHub Actions
- Keycloak/reference OIDC
- provider-neutral adapters
- MCP / agent-harness adapters

Preserve the classification already present in the execution plan:

- P0 selected baseline
- enterprise target
- future extraction candidate
- reference implementation
- ADR/benchmark-gated decision

Do not turn proposed technologies into accepted ADR decisions.

---

# 10. ISSUE 8 — Preserve all source-gap discipline

Keep all currently documented execution-plan source gaps.

In particular preserve:

- SOURCE-GAP-EXECPLAN-01
- SOURCE-GAP-EXECPLAN-02
- SOURCE-GAP-EXECPLAN-03

Do not mark them resolved unless the authoritative corpus explicitly supports closure.

Do not create additional source gaps merely because you are editing wording.

If a correction reveals a genuinely new source gap, record it explicitly with a new ID rather than silently filling it.

However, do NOT manufacture a new gap simply because a wording change is needed.

---

# 11. ISSUE 9 — Preserve module-placement honesty

Keep the explicit uncertainty for:

- Context Policy package placement
- Prompt Assembler package placement
- Output Controls package placement

The current document correctly identifies these as proposed placements to be confirmed during their own Sub-phase A.

Do not convert:

    proposed

into:

    source-defined

Do not change the package layout in conventions.md.

Do not modify conventions.md.

---

# 12. ISSUE 10 — Validate source references and citations

Because this is a correction pass, re-verify every section that you touch.

At minimum re-check against the live repository:

- implementation-plan.md §6
- implementation-plan.md §7
- implementation-plan.md §8
- implementation-plan.md §18
- implementation-readiness-gate.md §10
- implementation-readiness-gate.md §11
- conventions.md §2.1
- conventions.md §2.2
- relevant architecture.md sections
- relevant interfaces.md sections
- root CLAUDE.md rules governing execution

Do not trust the current execution-plan.md's citation alone if the underlying source is available.

Do not introduce fabricated section numbers.

If the current document contains a citation that is accurate, preserve it.

---

# 13. ISSUE 11 — Capability Gate sequencing

Ensure the plan cannot be interpreted as allowing:

    Sub-phase H complete
        ↓
    immediately start next capability

without:

    Capability Gate review
        ↓
    explicit human approval
        ↓
    next capability's Sub-phase A

The actual rule must be:

    A
    → approval

    B
    → approval

    C
    → approval

    D
    → approval

    E
    → approval

    F
    → approval

    G
    → approval

    H
    → approval

    Capability Gate
    → approval

    Next capability A

If the existing plan places the Capability Gate after H, preserve that model while making the approval boundary explicit.

---

# 14. ISSUE 12 — Capability 2 distinction must remain intact

Do not merge the identities of:

- Baseline Benchmark Harness
- Quality Evaluation

They share one implementation foundation, but remain two distinct P0 items.

Preserve the current traceability and wording that distinguishes them.

Do not create separate independent capability gates unless the authoritative implementation-plan.md requires that.

The current implementation-plan structure treats them as one shared implementation foundation with two distinct P0 items.

---

# 15. ISSUE 13 — No implementation work in this correction

This task is DOCUMENT-ONLY.

Allowed:

    docs/execution-plan.md

Potentially allowed only if required by the repository's own current prompt/document convention:

    a prompt artifact specifically requested for this correction

But do NOT modify:

- source code
- control_plane/
- tests
- architecture.md
- implementation-plan.md
- requirements-traceability.md
- implementation-readiness-gate.md
- ADRs
- security.md
- observability.md
- conventions.md
- interfaces.md
- edge-cases.md
- scenario-matrix.md
- any other authoritative EAIOC source

If a discrepancy is found in an authoritative document:

REPORT IT.

Do not repair it during this task.

---

# 16. REQUIRED VERSION UPDATE

Change:

    Version: 1.0.0

to:

    Version: 1.0.1

Update any internal wording that identifies the version of the execution plan if present.

Do not create a new document ID.

Keep:

    EAIOC-EXECPLAN-001

---

# 17. REQUIRED POST-CORRECTION VALIDATION

After editing, perform all of the following.

## A. Structural validation

Confirm:

- six current P0 capabilities only
- each capability has A–H + Capability Gate
- all atomic execution units remain uniquely identifiable
- no duplicate execution-unit IDs
- no missing lifecycle sub-phase
- no hidden batching language

## B. Sequential-execution validation

Search the entire execution plan for wording such as:

- parallel
- parallel-capable
- concurrent capability execution
- run in parallel
- execute simultaneously
- equivalent ordering

Review every match.

Dependency independence may remain documented.

Execution permission must remain:

    STRICTLY SEQUENTIAL

## C. Commit-boundary validation

Search for:

- commit
- checkpoint
- approval
- sub-phase

Confirm that every lifecycle sub-phase has its own commit/approval boundary.

There must be no statement that two sub-phases share one commit.

## D. Scope validation

Confirm:

- no source code created
- no control_plane implementation created
- Observability not added to current execution scope
- P1–P5 not atomized into executable units

## E. Traceability validation

Confirm all touched source references resolve against the live files.

Confirm no requirement/architecture/interface IDs were invented.

## F. File-scope validation

Run:

    git status --short
    git diff --stat
    git diff -- docs/execution-plan.md

Confirm only:

    docs/execution-plan.md

changed during this correction.

If another file changed unexpectedly, STOP and report it.

## G. Final read validation

Read the final relevant sections directly after editing, especially:

- P0 execution order
- Capability 1 sub-phase table
- Capability 2 sub-phase table
- Capability 3 sub-phase table
- Capability 4 sub-phase table
- Capability 5 sub-phase table
- Capability 6 sub-phase table
- release sequencing
- final report
- readiness boundary

Do not rely only on grep output.

---

# 18. REQUIRED FINAL REPORT

At the end of the correction task, report:

1. File modified
2. Version before → after
3. Exact correction categories applied
4. Number of atomic P0 execution units
5. Confirmation that each sub-phase has its own commit/approval boundary
6. Confirmation that execution is sequential
7. Confirmation that Observability remains excluded
8. Confirmation that P1–P5 remain non-executable in this plan
9. Source gaps preserved
10. ADR statuses preserved
11. Files outside docs/execution-plan.md modified? — must be NO
12. Whether the corrected plan is ready for freeze
13. Whether any blocking issue remains

Do not implement anything.

Do not proceed to Mode B.

End with exactly:

    EXECUTION PLAN CORRECTION COMPLETE — READY FOR FREEZE REVIEW

Do not add a second readiness verdict after that line.

---

# 19. CRITICAL NON-NEGOTIABLE RULE

This is a SURGICAL CORRECTION.

Preserve everything that is already correct.

Only change what is necessary to remove the identified contradictions and enforce the intended gated execution semantics.

Do NOT:

- rewrite the architecture
- change technology selections
- change P0 scope
- change capability dependencies
- change source-of-truth hierarchy
- accept ADRs
- close source gaps
- implement code
- create implementation artifacts outside the requested plan
- invent requirements
- invent performance targets
- invent benchmark results

END OF SURGICAL CORRECTION PROMPT
