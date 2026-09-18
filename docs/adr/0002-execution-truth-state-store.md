# ADR-0002: Execution-Truth / Versioned-State Backing Store Selection

| Field | Value |
|---|---|
| ADR ID | `ADR-0002` |
| EAIOC ADR Set | `EAIOC-ADR-001` |
| Status | `PROPOSED` |
| Phase | Level 0 — PRE-IMPLEMENTATION |
| Related Decision | `IMPL-DEC-02` |
| Origin | `implementation-plan.md` §24/§25, item 2 |
| Decision Type | Infrastructure |
| Blocking | No |

## Status

`PROPOSED`. No local evidence exists to justify `ACCEPTED` for any candidate technology.

## Context

`architecture.md` §46 (Dynamic Execution Architecture) defines 13 components — `ESM`, `CVM`, `WVM`, `CPM`, `RE`, `CIG`, `CEC`, `PRV`, `DPE`, `CAR`, `SRP`, `SPM`, `RCO` — that together own *execution truth*: a request is a versioned, resumable, reconcilable lifecycle, not a single atomic transaction. `ESM` alone owns `execution_id`, `execution_version`, `context_version`, `workflow_version`, `policy_version`, `pipeline_stage_status`, `token_ledger`, `reversibility_records`, `quality_gate_results`, and `terminal_state` per execution. None of `architecture.md` §46 specifies the concrete backing technology for this state. `implementation-plan.md` §24 (`IMPL-DEC-02`) names this open; §25 lists it as ADR candidate 2.

## Problem Statement

What concrete storage technology backs `ESM`/`CVM`/`WVM`'s versioned execution state such that `snapshot()`, `apply_mutation()`, `checkpoint()`, `reconcile()`, and `terminal()` (`architecture.md` §46.2.1) can all be implemented with the consistency guarantees the dynamic-execution model requires?

## Decision Drivers / Constraints

- **Monotonic versioning is a correctness invariant, not a convenience** (`architecture.md` §46.2.1: `execution_version` is "monotonically increasing"; §46's `CVM` must guarantee `context_version` "increments monotonically with no two mutations claiming the same version number" per `SCN-CONC-001`). The store must support atomic version-increment-on-write or an equivalent optimistic-concurrency primitive.
- **Concurrent mutation across sub-agents must serialize correctly** (`scenario-matrix.md` `SCN-CONC-001`, cited) — two near-simultaneous mutations against the same execution's context must never both claim the same version.
- **Reconciliation, not replay** (`architecture.md` §46.2.5, `RE`'s discipline, cited) — the store must expose enough history/audit trail to support reconciling an execution's state rather than blindly replaying mutations.
- **`AuditRecord` immutability** (`interfaces.md` §19.5: "Audit records are immutable and may not be deleted") — ESM execution state and governance `AuditRecord` are distinct concepts. If `reversibility_records` or terminal-state entries are represented as or materialized into `AuditRecord`, the source-defined immutability requirements apply to those audit records; the corpus does not establish that every ESM `reversibility_records` element is itself an `AuditRecord`. Where such materialization occurs, whatever stores that audit-record subset must support append-only, non-deletable writes for it.
- **Tenant isolation** (root `CLAUDE.md` rule 4) applies to execution state exactly as to cache/memory — every execution record is scoped by `tenant_id`.

## Source Basis

`architecture.md` §46.1–§46.2 (component specs); `interfaces.md` §19.5 (`AuditRecord`); `scenario-matrix.md` `SCN-CONC-001`; root `CLAUDE.md` rule 4; `implementation-plan.md` §24 (`IMPL-DEC-02`).

## Options Considered

- **Option A — Transactional relational store.** Structural fit: native optimistic-concurrency support via row versioning/`WHERE version = N` update patterns maps directly onto `CVM`'s monotonic-version requirement; strong consistency for `RE`'s reconciliation reads. Trade-off: horizontal write-scaling for very high concurrent-execution volume is a known relational-store limitation (cited generically, not as a benchmarked EAIOC finding).
- **Option B — Append-only event/log store.** Structural fit: naturally satisfies `AuditRecord`'s immutability requirement and `RE`'s reconcile-from-history discipline (current state is a fold over the log). Trade-off: `snapshot()`'s read path may need a separate materialized-view layer to avoid replaying the full log on every read.
- **Option C — Document/versioned-key store with optimistic-concurrency tokens.** Structural fit: many such stores expose a version/ETag primitive directly usable for `CVM`'s conflict detection. Trade-off: cross-execution reconciliation (`XEC`, ADR-0006) queries spanning multiple executions' state may be less natural than in a relational model.
- **Option D — Same backing technology as ADR-0001's cache store, reused for execution state.** Structural fit: reduces operational surface area. Trade-off: execution state's consistency/immutability requirements (versioned, auditable) are stricter than cache's (which explicitly tolerates staleness/eviction) — reuse risks under-serving one of the two.

## Option Analysis

The immutability requirement for any portion of execution state that is represented or materialized as `AuditRecord` is the sharpest constraint distinguishing these options. The corpus does not establish that `reversibility_records` or terminal-state records are themselves `AuditRecord` objects; where such materialization occurs, the `AuditRecord` immutability requirement applies — Option B satisfies it natively; Options A/C/D would need an explicit append-only sub-schema or a separate audit store layered on top.

## Decision

No decision made — deferred pending local evaluation. Acceptance criteria: a candidate demonstrates atomic monotonic versioning under concurrent writes (reproducing `SCN-CONC-001`'s scenario), append-only/non-deletable storage for the audit-trail subset, and a reconciliation query pattern usable by `RE`.

## Rationale

As with ADR-0001, no benchmark or production evidence exists for any candidate in this pre-implementation repository (root `CLAUDE.md` rules 5, 7). This ADR's value is making the concurrency/immutability constraints explicit before a team picks a technology under time pressure and discovers the constraint too late.

## Consequences

### Positive Consequences

`architecture.md` §46's component contracts are already fully specified independent of backing technology, so deferring this decision does not block any other design work.

### Negative Consequences

None yet observable.

### Risks

A store that cannot cheaply provide atomic optimistic-concurrency control could force an application-layer locking workaround, which risks violating `RE`'s "reconcile, not replay" discipline if implemented naively (e.g., by dropping concurrent writes instead of reconciling them). Mitigated by testing the versioning primitive against `SCN-CONC-001` before acceptance.

### Operational Consequences

`No material impact identified from the authoritative corpus` — no operational model exists yet.

## Rejected / Deferred Alternatives

None rejected; all four remain open.

## Requirements Traceability

`OBJ-015`–`022` (Dynamic Execution objectives, `architecture.md` §46, cited — `requirements-traceability.md` records these as `NO DOWNSTREAM ELABORATION` from any of the eleven prior documents, making this ADR the first place any of them gets implementation-facing treatment); `NFR-011` (Determinism, cited).

## Architecture Traceability

`architecture.md` §46.1–§46.2 (cited, not redefined); `interfaces.md` §42/`INTF-050`–`062` (cited).

## Interface / Contract Impact

None. This ADR selects a backing technology for the existing `ESM`/`CVM`/`WVM` operations; it does not change their signatures.

## Security / Governance Impact

Audit-trail immutability (`interfaces.md` §19.5) must be enforced by the chosen store's access model, not merely by application-code convention, to satisfy the fail-closed discipline `security.md` applies to governance-relevant records generally (cited, not redefined).

## Observability / Evaluation Impact

Execution-state mutations should be observable via the existing `ControlPlaneEvent`/`OptimizationSpan` telemetry (`interfaces.md` §19, cited) — this ADR does not introduce a competing schema.

## Scaling Impact

Cited from `SCALING.md`'s `SC-001` (Control-Plane self-protection/compute capacity surface) and `SC-002` (cross-execution coordination capacity, shared with ADR-0006) — both `SOURCE-DERIVED`, not redefined here.

## Implementation Impact

`implementation-plan.md` does not sequence a full P0-tier capability specifically for this store (the 13 §46 components are cited throughout the plan as already-specified, cf. §24's own table row for `OBJ-025`/`OBJ-032`). Resolution becomes necessary before any affected capability transitions from a reference/in-memory implementation to a persistent multi-process execution-state implementation — this is a conditional dependency scoped to that transition, not a universal prerequisite for all P1+ work.

## Migration / Rollback Considerations

Not applicable yet — no store selected.

## Source Gaps / Assumptions

No existing `SOURCE-GAP-*` specifically names this technology question (it is `IMPL-DEC-02`, an implementation-plan-level open decision, not a documentation source gap). **Assumption made for this ADR's analysis only:** that a single backing technology can serve `ESM`/`CVM`/`WVM` jointly, rather than requiring separate stores per component — not verified against any source, since none addresses implementation technology at all.

## Open Questions

Should the audit-trail-immutability subset of `ESM`'s state be split into a separate store from the mutable, frequently-versioned subset (`context_version`, `pipeline_stage_status`), given their different consistency/access patterns? Not answered here.

## Related Documents / ADRs

`architecture.md` §46; `implementation-plan.md` §24; ADR-0001 (Option D above directly compares reuse against this store); ADR-0006 (`XEC` extends `RE`/`SPM` to cross-execution scope — the state store choice here directly affects what's feasible for `XEC`'s coordination mechanism).

## Validation / Acceptance Criteria

A prototype reproducing `SCN-CONC-001` (two near-simultaneous mutations serialize correctly, no lost update) and demonstrating append-only storage for the audit-trail subset, before any status change to `ACCEPTED`.
