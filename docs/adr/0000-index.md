# EAIOC — Architectural Decision Record Index

**EAIOC ADR Set ID:** `EAIOC-ADR-001`
**Phase:** Level 0 — PRE-IMPLEMENTATION (RESEARCH)
**Chain position:** Final artifact, generated after `requirements-traceability.md` (12th generated document). No further documentation artifact follows this set.
**Generation prompt:** `docs/prompts/generate-adr-set.prompt.md`

## Candidate-Set Verification

`implementation-plan.md` §24 (Implementation Decision Register) and §25 (ADR Candidates) were independently re-read against the live file before drafting. The current §25 list reads exactly six candidates, matching this set with no addition, omission, or renaming:

1. Cache/key-value backing store selection (`IMPL-DEC-01`)
2. Execution-truth/versioned-state backing store selection (`IMPL-DEC-02`)
3. CI/CD platform and pipeline tooling (`IMPL-DEC-03`)
4. Telemetry/observability backend selection (`IMPL-DEC-04`)
5. Provider-adapter abstraction implementation pattern (root `CLAUDE.md` rule 1)
6. Coordination-mechanism implementation for `XEC` (`architecture.md` §47.3.2)

No discrepancy was found. No seventh ADR was created; none of the six was omitted.

## ADR Candidate ≠ `BLOCKED BY ADR`

Per `requirements-traceability.md` §16's already-established distinction (cited, not re-derived): every requirement whose traceability chain touches one of these six decisions has a documented, usable interim disposition and is not dead-ended pending this ADR set — the corpus's `BLOCKED BY ADR` count is `0`, independently re-confirmed here, not merely carried forward. These six ADRs record decisions that *should eventually* be made, not decisions implementation is currently stalled on.

## ADR Set

| ADR | Title | Status | Origin | Blocking? | Summary |
|---|---|---|---|---|---|
| `ADR-0001` | Cache / Key-Value Backing Store | `PROPOSED` | `implementation-plan.md` §24/§25 | No | Selects the backing technology behind `cache-strategy.md`'s already-fixed `CacheStore` contract; no store yet chosen; P1 proceeds against an in-memory reference implementation regardless. |
| `ADR-0002` | Execution-Truth / Versioned-State Store | `PROPOSED` | `implementation-plan.md` §24/§25 | No | Selects the backing technology for `ESM`/`CVM`/`WVM`'s versioned execution state (`architecture.md` §46); monotonic versioning and audit-immutability are the sharpest constraints. |
| `ADR-0003` | CI/CD Platform / Tooling | `PROPOSED` | `implementation-plan.md` §24/§25 | No | Selects tooling to execute the already-derived 8-step gate progression (`implementation-plan.md` §16); no platform named upstream. |
| `ADR-0004` | Telemetry / Observability Backend | `PROPOSED` | `implementation-plan.md` §24/§25 | No | Selects the backend hosting `interfaces.md` §19's fixed telemetry schemas; `AuditRecord` immutability is the sharpest constraint. |
| `ADR-0005` | Provider-Adapter Implementation Pattern | `PROPOSED` | root `CLAUDE.md` rule 1 | No | Selects the internal structure of `providers/adapters/`; must satisfy provider neutrality across 5 integration modes and the FTR's per-platform feasibility tiers. |
| `ADR-0006` | `XEC` Coordination / Locking Mechanism | `PROPOSED` | `architecture.md` §47.3.2 | No | Selects the coordination substrate for cross-execution conflict detection; the fail-closed-on-coordination-unavailable rule is the sharpest constraint. |

## Cross-ADR Consistency Review

| Pair | Finding |
|---|---|
| `0001` ↔ `0002` | Both are backing-store decisions with structurally different consistency needs (cache tolerates staleness/eviction by design; execution-truth state requires monotonic versioning and audit immutability). `ADR-0001` Option D explicitly evaluates reusing `ADR-0002`'s eventual store for cache, and flags the risk that doing so may under-serve one of the two. No inconsistency found — the two ADRs' constraint sets are compatible but distinct; reuse remains an open option, not a conflict. |
| `0001` ↔ `0006` | Cache invalidation under concurrent access (`cache-strategy.md` `CL-003`) and `XEC`'s conflict detection both concern shared-resource mutation, but at different layers (cache-entry invalidation vs. general cross-execution resource conflict). No direct dependency found; noted as a related-but-independent concern in `ADR-0001`'s Related Documents. |
| `0002` ↔ `0006` | Genuine open dependency: both ADRs raise the same unresolved question (should `XEC`'s coordination substrate co-locate with or reuse `ADR-0002`'s execution-truth store, since both concern versioned-state comparison). Recorded identically in both ADRs' Open Questions — this is the single most consequential unresolved cross-ADR relationship in the set, since resolving it well could avoid operating two near-duplicate versioning systems, and resolving it poorly could couple two decisions that should stay independent. |
| `0003` ↔ all | The CI/CD platform (`0003`) must be able to execute tests against whatever infrastructure `0001`, `0002`, `0004`, and `0005` eventually select. No conflict found. `ADR-0003` has compatibility dependencies on the eventual implementation environment, but the corpus does not establish a strict sequencing dependency requiring `ADR-0003` to be decided after `ADR-0001`/`0002`/`0004`/`0005` — see the dependency graph below, which labels this relationship `C` (compatibility), not `H` (hard). |
| `0004` ↔ all | Telemetry (`0004`) must be capable of observing every other ADR's eventual implementation (cache operations, execution-state mutations, CI pipeline results, provider-adapter calls) via the already-fixed `interfaces.md` §19 schemas. No conflict found — `0004`'s schemas are provider/store/mechanism-agnostic by construction, so no other ADR's eventual choice can break this compatibility. |
| `0005` ↔ all | The provider-adapter pattern (`0005`) constrains what `0003`'s CI pipeline must be able to test (multi-mode provider fixtures) and what `0004`'s telemetry must be able to label (`provider_id`-scoped metrics, already fixed by `interfaces.md`). No conflict found; both are downstream-compatible with any of `0005`'s candidate options. |

## ADR Dependency Graph

Formal labeling of the relationships identified in the Cross-ADR Consistency Review above. Every arrow is derived from that review's own findings, not asserted independently — none is invented merely because two ADRs interact.

**Legend:** `H` = hard dependency (one ADR's acceptance is a precondition for the other's). `C` = compatibility dependency (each must remain workable given whatever the other eventually selects, but neither blocks the other's decision timing). `E` = evaluation dependency (deciding one materially changes what's worth evaluating for the other, without making either a precondition). `I` = informational relationship (relevant context, no dependency in either direction).

```text
ADR-0001 ──E── ADR-0002   (Option D in 0001 evaluates reusing 0002's eventual store; neither blocks the other)
ADR-0001 ──I── ADR-0006   (cache invalidation vs. XEC conflict detection: related layers, no direct dependency)
ADR-0002 ──E── ADR-0006   (shared open question: co-locate coordination substrate with the execution-truth store, or keep independent — the single most consequential unresolved relationship in this set, but still evaluation-only: 0006 can be decided with Option A/B regardless of 0002's outcome)
ADR-0003 ──C── ADR-0001, ADR-0002, ADR-0004, ADR-0005   (CI/CD tooling must remain compatible with whatever each eventually selects; no hard sequencing requirement — see correction to the pairwise table above)
ADR-0004 ──I── ADR-0001, ADR-0002, ADR-0003, ADR-0005, ADR-0006   (telemetry schemas are provider/store/mechanism-agnostic by construction; observes every other ADR's eventual implementation without depending on any of them)
ADR-0005 ──I── ADR-0003, ADR-0004   (provider-adapter pattern shapes what 0003's CI must be able to test and what 0004's telemetry labels, but interfaces.md's provider_id-scoped schema is already fixed independent of 0005's eventual choice)
```

No `H` (hard dependency) relationship was found anywhere in this set — every pair either has no dependency, an evaluation-only relationship, a compatibility expectation, or a purely informational one.

## Downstream-Impact Register

No file outside `docs/adr/` was modified to produce this set. The following rows describe what a **future** acceptance of an ADR would need to update — none of these edits was made now, and none is mandatory before implementation planning can continue (per the ADR-candidate-≠-blocking distinction above):

| Document | Section | Required Change (on future acceptance) | Reason | Mandatory Before Implementation? |
|---|---|---|---|---|
| `implementation-plan.md` | §24 (`IMPL-DEC-01`–`04`) | Update the "Blocking?"/status framing once the corresponding ADR reaches `ACCEPTED` | The register currently reflects "open, ADR candidate"; an accepted ADR changes that row's disposition | No |
| `cache-strategy.md` | §31 (`SOURCE-GAP-CACHESTRAT-04`) | Storage-cost model gap could be narrowed once `ADR-0001` is accepted and a real store's cost characteristics are known | The gap is currently open because no deployment context exists; an accepted store choice supplies that context | No |
| `observability.md` | §43 (`SOURCE-GAP-OBS-01`–`03`) | Refresh-interval/alert-threshold/cardinality gaps could be narrowed once `ADR-0004` is accepted and the backend's actual operating limits are known | Same pattern — gaps are open for lack of a concrete technology to measure against | No |
| `SCALING.md` | §48 (`SOURCE-GAP-SCALING-01`/`02`) | `XEC` contention-volume capacity threshold could be narrowed once `ADR-0006` is accepted and a coordination mechanism exists to load-test | Same pattern | No |
| `requirements-traceability.md` | §8 (Master Source-Gap Register) | Would need re-running to reflect any of the above narrowing once any ADR is accepted | That document's own registers are current only as of its own generation date | No |

No update to `architecture.md`, `interfaces.md`, `conventions.md`, `edge-cases.md`, `scenario-matrix.md`, `provider-matrix.md`, `agent-optimization.md`, `inference-optimization.md`, `quality-gates.md`, `security.md`, or `eval.md` is required during ADR generation, since none of these six ADRs redefines a behavioral contract, interface schema, or requirement — each selects an implementation substrate or pattern behind an already-fixed contract. Acceptance of one or more ADRs may still require controlled downstream synchronization (see the five rows above for `implementation-plan.md`, `cache-strategy.md`, `observability.md`, `SCALING.md`, and `requirements-traceability.md` specifically); no such synchronization has been performed now.

## ADR Decision Matrix

| ADR | Decision Question | Current Decision Status | Candidate Approaches | Key Drivers | Requirements Affected | Major Risks | Open Questions |
|---|---|---|---|---|---|---|---|
| `0001` | What backs the cache `CacheStore` interface? | Deferred | Managed KV store; embedded/in-process store; native-similarity store; reuse of `0002`'s store | Tenant isolation; four-check gate; `CL-003` cascading invalidation | `SEC-*`/tenant isolation, `NFR-007` | Re-platforming if invalidation/similarity search can't scale | One store for all 7 types, or split by cluster? |
| `0002` | What backs `ESM`/`CVM`/`WVM`'s versioned state? | Deferred | Transactional relational store; append-only log; versioned-document store; reuse of `0001`'s store | Monotonic versioning; `AuditRecord` immutability; reconcile-not-replay | `OBJ-015`–`022`, `NFR-011` | Application-layer locking workaround violating reconcile discipline | Split mutable state from immutable audit trail into separate stores? |
| `0003` | What executes the 8-step gate progression? | Deferred | Source-control-native CI; standalone/self-hosted CI; cloud-provider-native pipeline | Fixed gate sequence; deployment-mode-specific step 7; provider neutrality in CI config | `NFR-013`, `AC-015` | Late discovery that platform can't support step 7 | Provisional pick now vs. wait for infra decisions? |
| `0004` | What backend hosts the telemetry schemas? | Deferred | Unified observability platform; separate stores per signal; dedicated immutable audit store | `AuditRecord` immutability; tenant-labeled high cardinality; cross-signal correlation | `NFR-006`, `NFR-010`/H15, `SEC-013` | Governance gap if backend can't enforce audit immutability | Should audit store always be a separate system as policy? |
| `0005` | How is `providers/adapters/` internally structured? | Deferred | Per-provider lookup table; adapter + capability-declaration layer; mode-first adapter families | Provider-hints opacity; 5 integration modes; FTR feasibility tiers; `CAR` integration | `NFR-005`, `OBJ-013`, `H08` | Retrofitting capability declaration after adapters exist | Mandatory declaration layer from day one, or incremental? |
| `0006` | What coordination substrate implements `XEC`? | Deferred | Distributed lock service; optimistic version tokens; hybrid (tokens default, lock scoped to non-idempotent cases) | Fail-closed on coordination unavailable; lock-only-where-required; reconcile-not-replay | `OBJ-032`, `AC-049`, `H12` | Silently converting a fail-closed case into fail-open | Co-locate with `0002`'s store or stay independent? |

No score, ranking, or "best option" label is assigned to any candidate approach in any row.

## Implementation Readiness Boundary

This ADR set answers: *are the six remaining architectural/implementation decisions sufficiently framed, constrained, and documented so that they are explicit rather than hidden?* Yes — each has a stated problem, real cited constraints, a genuine candidate space, and explicit acceptance criteria.

This ADR set does **not** answer: *is the entire EAIOC system ready to be coded?* That is a separate, not-yet-generated Implementation Readiness Gate.

```
Implementation has NOT started.
The next required phase is the Implementation Readiness Gate.
```

## Correction Pass — Final Report (Surgical ADR Correction & Consistency Pass)

**Files modified:** `0000-index.md`, `0001-cache-backing-store.md`, `0002-execution-truth-state-store.md`, `0003-ci-cd-platform.md`, `0004-telemetry-backend.md`, `0005-provider-adapter-implementation-pattern.md`, `0006-xec-coordination-mechanism.md` — all 7 files under `docs/adr/`, no others.

**Corrections applied:**

1. ADR-0005 neutralized evaluative wording ("most directly traceable... best-grounded candidate" → "has a direct structural correspondence"; "noted as best-grounded but not accepted" → "has a direct structural correspondence to the cited FTR/CAR model; this remains an evaluation observation rather than an accepted decision") — `APPLIED`.
2. ADR-0006 neutralized evaluative wording ("most directly mirrors" → "mirrors"; "noted as best-aligned... but not accepted" → "mirrors the source's own two-tier framing; this remains an evaluation observation rather than an accepted decision") — `APPLIED`.
3. ADR-0003 sequencing language corrected in both `0003-ci-cd-platform.md` (Decision and Risks sections) and `0000-index.md`'s pairwise table — removed "deciding this ADR only after the deployment-infrastructure decision, not before" and "0003 should generally follow, not precede," replaced with explicit compatibility-not-hard-sequencing wording — `APPLIED`.
4. ADR-0002 ESM/`reversibility_records`/`AuditRecord` distinction corrected (Decision Drivers and Option Analysis sections) — added the explicit "ESM execution state and governance `AuditRecord` are distinct concepts... the corpus does not establish that every ESM `reversibility_records` element is itself an `AuditRecord`" clarification, `AuditRecord`'s own immutability requirement unweakened — `APPLIED`.
5. ADR-0004 administrative enforcement wording corrected (Security/Governance Impact) — replaced the specific "must not permit deletion... even under administrative/operational access" claim with "the exact administrative enforcement mechanism... is not specified upstream and must be validated during implementation evaluation," while preserving the source-defined non-deletability requirement itself — `APPLIED`.
6. ADR-0001 four-check-gate responsibility corrected (Decision Drivers and Security/Governance Impact) — replaced "the store must support efficient similarity/freshness/authorization/policy-version checks" with the combined-implementation framing; backing store no longer required to natively enforce every check — `APPLIED`.
7. ADR-0005 traceability expanded — added a 5-row source-concept-to-relevance table (provider-neutrality rule, `NFR-005`, `OBJ-013`, `H08`, `architecture.md` §46.1/`CAR`), every ID/section independently re-verified against `requirements-traceability.md` and `architecture.md` §46.1 before insertion — `APPLIED`.
8. ADR-0006 coordination/locking distinction clarified — added an explicit "Architectural boundary" paragraph to Context stating `XEC` coordination is not synonymous with an always-on distributed lock service and enumerating its five states, with locking scoped to `LOCK_REQUIRED` only — `APPLIED`.
9. Index source-gap semantics corrected — replaced the ambiguous "Source gaps resolved: 0" line with a 5-row breakdown table distinguishing formally-closed / cited-as-relevant / actually-narrowed-today / new-ADR-specific / open-remaining, with every count independently verified against each ADR's own Source Gaps section and `requirements-traceability.md` §8 (no count invented) — `APPLIED`.
10. Index downstream-synchronization wording corrected — replaced "is anticipated" framing with "is required during ADR generation... may still require controlled downstream synchronization" — `APPLIED`.
11. ADR-0002 P1+ prerequisite wording corrected (Implementation Impact) — replaced the universal "prerequisite for any P1+ capability" claim with the conditional "necessary before any affected capability transitions from a reference/in-memory implementation to a persistent multi-process execution-state implementation" — `APPLIED`.
12. Formal ADR dependency graph added to `0000-index.md` — a new "ADR Dependency Graph" section with H/C/E/I-labeled arrows for all 6 relationships identified in the existing Cross-ADR Consistency Review, every label derived from that review's own findings (result: 0 `H` labels, confirming no hard dependency exists anywhere in the set) — `APPLIED`.

**Validation results** (independently re-verified, not carried forward from the original generation pass):

```text
ADR count: 6
PROPOSED ADR count: 6 (grep-confirmed: every "| Status | `PROPOSED` |" row, zero "ACCEPTED" status rows — every "ACCEPTED" occurrence in the corpus is either explanatory ("not ACCEPTED because...") or a forward-looking acceptance criterion)
BLOCKED BY ADR count: 0 (independently re-confirmed against requirements-traceability.md, consistent with ADR-candidate ≠ implementation-blocker semantics)
Upstream files modified: 0
New source gaps introduced: 0
Existing source gaps formally closed: 0
Cross-ADR consistency: PASS — Status/decision/blocker semantics consistent across all 7 files; AuditRecord/execution-state/reversibility_records distinction now applied consistently in ADR-0002 and cross-referenced correctly from ADR-0004; XEC coordination-vs-locking distinction now explicit in ADR-0006 and consistent with ADR-0001's/ADR-0002's references to it; cache backing-store-vs-application-layer boundary consistent across ADR-0001's Decision Drivers and Security/Governance Impact; ADR-0005 introduces no provider leakage; ADR-0003 has no unsupported sequencing dependency after correction; ADR-0004 preserves audit immutability, fail-closed-where-source-defined, and ordinary-telemetry-non-blocking-where-source-defined; every OBJ/SEC/NFR/H/AC ID spot-checked against requirements-traceability.md and security.md resolved to a real, correctly-described requirement (OBJ-032, AC-049, H12, NFR-006/007/010/011/013, SEC-013 all confirmed)
File-scope validation: PASS — git status confirms only the 7 docs/adr/ files changed
```

## ADR Set Readiness

- ADR candidates identified: `6`
- ADRs generated: `6`
- ADRs with `PROPOSED` status: `6`
- ADRs with accepted decisions: `0`
- Requirements `BLOCKED BY ADR`: `0` (independently re-confirmed against `requirements-traceability.md` §16/§17, not merely carried forward)
- Source-gap disposition (verified against each ADR's own "Source Gaps / Assumptions" section and `requirements-traceability.md` §8's master register — "resolved" below means formally closed, not merely discussed):

  | Metric | Count | Basis |
  |---|---|---|
  | Existing source gaps formally closed | `0` | No ADR in `PROPOSED` status supplies the accepted evidence a closure requires. |
  | Existing source gaps cited as relevant by an ADR's analysis | `8` | `SOURCE-GAP-CACHESTRAT-01`/`03`/`04` (ADR-0001); `SOURCE-GAP-OBS-01`/`02`/`03` (ADR-0004); `SOURCE-GAP-SCALING-01`/`02` (ADR-0006). ADR-0002 and ADR-0003 cite none; ADR-0005 explicitly names `SOURCE-GAP-PROVMTX-01`–`03` as "related but distinct," not relevant, and they are excluded from this count. |
  | Existing source gaps actually narrowed by this ADR set today | `0` | Every ADR's own wording is forward/conditional ("this ADR's *eventual* resolution... narrows," "is a prerequisite for *ever* closing") — narrowing requires accepted evidence, which does not yet exist for any of the six. |
  | New ADR-specific source gaps | `0` | This set raises Open Questions per ADR but records none of them as a new `SOURCE-GAP-ADR-NN` — each is a decision to be made, not a documentation gap. |
  | Open source gaps remaining (corpus-wide, unaffected by this ADR set) | `50` distinct semantic gaps total | Per `requirements-traceability.md` §8 (authoritative, re-cited not recomputed): 3 `RESOLVED`, 1 `RESOLVED-IN-PLACE-DOWNSTREAM`, 2 `NARROWED`, 1 split `NARROWED`/`OPEN`, 1 `NOT-A-GAP`, 42 fully `OPEN`/non-blocking. |
- Cross-ADR conflicts: `0` (one genuine open dependency recorded — `0002`↔`0006`'s shared co-location question — but this is an unresolved question, not a conflict between the two ADRs' content)
- Downstream documents requiring updates: `0` mandatory now; `5` rows recorded for future reference upon eventual acceptance (see Downstream-Impact Register)
- Implementation blockers introduced: `0`
- Implementation status: `NOT STARTED`
- Next phase: `Implementation Readiness Gate`

```
The ADR documentation phase is complete, but the EAIOC system itself remains PRE-IMPLEMENTATION — Level 0 (RESEARCH).

The six ADRs are architectural decision records and must not be interpreted as implementation completion.

Implementation has NOT started.

The next required phase is the Implementation Readiness Gate.
```

**File-scope confirmation:** only the 7 files under `docs/adr/` (this index plus `0001`–`0006`) were created in this pass. No upstream document (`problemStatement.txt`, Engineering Spec, `architecture.md`, `interfaces.md`, `conventions.md`, `edge-cases.md`, `scenario-matrix.md`) or any of the twelve generated downstream documents was modified. `CLAUDE.md` was not modified as part of ADR generation itself (its own sync happens separately, on request, per this repository's established pattern).

ADR SET COMPLETE — READY FOR IMPLEMENTATION READINESS GATE
