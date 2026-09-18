# ADR-0006: `XEC` Coordination / Locking Mechanism

| Field | Value |
|---|---|
| ADR ID | `ADR-0006` |
| EAIOC ADR Set | `EAIOC-ADR-001` |
| Status | `PROPOSED` |
| Phase | Level 0 — PRE-IMPLEMENTATION |
| Related Decision | N/A (no `IMPL-DEC` row — origin is `architecture.md` §47.3.2) |
| Origin | `implementation-plan.md` §25, item 6 |
| Decision Type | Infrastructure / Architectural |
| Blocking | No |

## Status

`PROPOSED`. `architecture.md` §47.3.2 fully specifies `XEC`'s behavioral contract but not its coordination substrate.

## Context

`architecture.md` §47.3.2 defines `XEC` (Cross-Execution Coordinator) as extending `RE` (Reconciliation Engine) and `SPM` (Supersession Manager) from single-execution reconciliation to concurrent mutation, staleness, and version-conflict detection across multiple agents/sub-agents/workflow executions sharing a resource. Its output type, `ConcurrencyConflictResult`, has five states: `NO_CONFLICT`, `STALE_SNAPSHOT`, `VERSION_CONFLICT`, `RECONCILED`, `LOCK_REQUIRED`. Its failure behavior is explicit and fail-closed: "an unresolvable conflict (coordination unavailable) blocks the losing execution's conflicting write/action and surfaces a conflict result — it does not proceed with an unreconciled dual-write." None of this specifies what concrete coordination/locking substrate implements conflict detection, version-token comparison, or lock acquisition. `implementation-plan.md` §25 lists this as ADR candidate 6, citing `architecture.md` §47.3.2 directly.

**Architectural boundary (explicit):** `XEC` coordination is not synonymous with an always-on distributed lock service. Per `architecture.md` §47.3.2, `XEC` coordination encompasses four distinct concerns — version/conflict detection (`VERSION_CONFLICT`), stale-snapshot detection (`STALE_SNAPSHOT`), reconciliation to a single consistent outcome (`RECONCILED`), and locking *only* where the shared resource/action is non-idempotent and concurrently reachable (`LOCK_REQUIRED`). Locking is the narrowest, conditionally-invoked subset of coordination, not the definition of coordination itself.

## Problem Statement

What concrete coordination mechanism implements `XEC`'s conflict-detection, version-token comparison, and (where the resource is non-idempotent and concurrently reachable) locking requirements, while preserving the mandatory fail-closed behavior when coordination itself is unavailable?

## Decision Drivers / Constraints

- **Fail-closed on coordination unavailability is explicit and non-negotiable** (`architecture.md` §47.3.2's Failure Behavior, echoed by `EC-194`/`SCN-CONC-006`, cited) — this is a case where root `CLAUDE.md` rule 2's fail-open-for-optimization default does *not* apply; an unresolvable conflict must block the losing write, never proceed with an unreconciled dual-write. Any coordination substrate must make "coordination unavailable" a detectable, distinguishable state from "no conflict," not silently degrade to one or the other.
- **Locking is required only where the resource is non-idempotent and concurrently reachable — not universally** (`architecture.md` §47.3.2) — the mechanism must support the lighter-weight `VERSION_CONFLICT`/`STALE_SNAPSHOT` detection path (comparison only, no lock) as a distinct, cheaper operation from the `LOCK_REQUIRED` path, rather than always acquiring a lock.
- **Reconcile, not replay** (`RE`'s discipline, `architecture.md` §46.2.5, extended by `XEC` to cross-execution scope) — where two executions' completed actions overlap, the mechanism must support reconciling to a single consistent outcome, not simply rejecting one side.
- **Non-idempotent operations must never be blindly replayed across executions** (extending `RCO`'s resume-time invariant, §46.2.13, to cross-execution scope, cited) — whatever mechanism tracks in-flight/completed non-idempotent actions must persist that state reliably enough to prevent a retry from re-executing them.
- **Tenant isolation applies to coordination state exactly as to cache/execution state** (root `CLAUDE.md` rule 4) — a coordination conflict must never be detected or resolved across tenant boundaries.

## Source Basis

`architecture.md` §47.3.2 (`XEC`); §46.2.5 (`RE`); §46.2.13 (`RCO`); `interfaces.md` `INTF-069` (`CrossExecutionCoordinator`, §43.7, cited); `edge-cases.md` `EC-191`/`192`/`193`/`194`; `scenario-matrix.md` `SCN-CONC-004`/`005`/`006`; root `CLAUDE.md` rules 2, 4; `implementation-plan.md` §25.

## Options Considered

- **Option A — Distributed lock service** (a dedicated locking/coordination system providing mutual exclusion primitives). Structural fit: directly supports the `LOCK_REQUIRED` state for genuinely non-idempotent, concurrently-reachable resources. Trade-off: introduces a coordination-availability dependency for every conflict check, even the cheaper version-comparison-only path, unless the version-check path is implemented separately.
- **Option B — Optimistic-concurrency version tokens stored alongside the resource itself** (no separate lock service; conflict detection is a compare-and-swap against a version field on the resource's own store — likely the same store ADR-0002 selects for execution-truth state). Structural fit: naturally serves `NO_CONFLICT`/`STALE_SNAPSHOT`/`VERSION_CONFLICT` without a separate system; reduces operational surface area if it can reuse ADR-0002's store. Trade-off: does not natively provide `LOCK_REQUIRED`'s mutual-exclusion guarantee for genuinely non-idempotent actions — would need a separate, narrower locking mechanism layered on top only for that subset of cases.
- **Option C — Hybrid: optimistic version tokens as the default path (Option B), with a dedicated lock service invoked only for the subset of actions architecture explicitly marks non-idempotent-and-concurrently-reachable (Option A, scoped narrowly).** Structural fit: matches §47.3.2's own stated principle that "locking/coordination is required only where the shared resource/action is non-idempotent and concurrently reachable — not universally" more precisely than either pure option. Trade-off: two mechanisms to build and operate instead of one, though each is individually simpler in scope.

No specific locking-service or database product's marketing claims are asserted as fact.

## Option Analysis

Option C mirrors the source's own two-tier framing (`architecture.md` §47.3.2 distinguishes "locking is required only where... not universally" from the default comparison-based conflict detection) — but committing to it now would still be picking a shape before any local evidence exists that a two-mechanism design is operationally simpler than a single one, so this ADR records the observation without accepting it.

## Decision

No decision made — deferred pending local evaluation. Acceptance criteria: a prototype reproducing `SCN-CONC-004` (two executions concurrently modify the same shared resource — second write must not silently overwrite), `SCN-CONC-005` (non-idempotent, concurrently-reachable action requires an acquired lock), and `SCN-CONC-006` (coordination mechanism itself unavailable — the losing execution's write is blocked, not allowed through), before any status change to `ACCEPTED`.

## Rationale

This is the one ADR in the set whose incorrect resolution could most directly violate a security-adjacent invariant (the fail-closed-on-coordination-unavailable rule) rather than merely a performance/cost concern — root `CLAUDE.md` rule 2 treats fail-closed failures as a different category from fail-open ones, and `XEC`'s failure mode is explicitly in the fail-closed category. This raises the bar for what "local evaluation" must demonstrate before acceptance: not just that the mechanism works under normal conditions, but that it fails closed correctly when coordination itself is down.

## Consequences

### Positive Consequences

`architecture.md` §47.3.2's behavioral contract, including its fail-closed failure mode, is already fully specified independent of the coordination mechanism, so deferring this decision blocks nothing else in the design.

### Negative Consequences

None yet observable.

### Risks

A mechanism that cannot cleanly distinguish "coordination unavailable" from "no conflict detected" risks silently converting a fail-closed case into a fail-open one — the single most consequential possible defect for this ADR. Mitigated by making `SCN-CONC-006` reproduction a mandatory acceptance criterion, not an optional nice-to-have test.

### Operational Consequences

`No material impact identified from the authoritative corpus`.

## Rejected / Deferred Alternatives

None formally rejected. Option C mirrors the source's own two-tier framing; this remains an evaluation observation rather than an accepted decision.

## Requirements Traceability

`OBJ-032` (Cross-execution concurrency, cited); `AC-049` (concurrent mutation/stale-decision/version-conflict detection across agents/sub-agents/executions, cited); `H12` (Shared State and Cross-Execution Concurrency, cited).

## Architecture Traceability

`architecture.md` §47.3.2 (`XEC`), §46.2.5 (`RE`), §46.2.13 (`RCO`) — all cited, none redefined.

## Interface / Contract Impact

None. `INTF-069`'s `ConcurrencyConflictResult` states are fixed by `interfaces.md`; this ADR selects a mechanism producing them, not new states.

## Security / Governance Impact

Directly load-bearing: the fail-closed behavior on coordination unavailability is a governance-adjacent guarantee (it prevents an unreconciled dual-write, which could otherwise let an unauthorized or stale action's effects persist) — `security.md`'s general fail-closed-on-governance-failure discipline is the same category of rule, cited not redefined here.

## Observability / Evaluation Impact

Conflict events (`ConcurrencyConflictResult` outcomes) should be observable as a distinct event type from ordinary optimization events, consistent with `architecture.md` §47.3.1's existing note that "memory/execution-truth conflicts are logged as a distinct event type, separate from ordinary SRP stale-cache events" (cited by analogy, not redefined).

## Scaling Impact

Directly tied to `SCALING.md`'s `SC-002` (cross-execution coordination capacity surface, `SOURCE-DERIVED`) and its own recorded gap that no scenario validates a contention-volume capacity threshold for `XEC` (`SOURCE-GAP-SCALING-01`/`02`, cited) — this ADR's eventual resolution is a prerequisite for ever closing that gap with real evidence, though it does not close it itself.

## Implementation Impact

`implementation-plan.md` §12.3's cross-execution capability cluster (cited) cannot be promoted through its Capability Gate without this ADR's resolution wired in, per that section's own stated dependency.

## Migration / Rollback Considerations

Not applicable yet — no mechanism selected.

## Source Gaps / Assumptions

**Existing source gaps directly relevant** (not resolved by this ADR): `SOURCE-GAP-SCALING-01`/`02` (no scenario validates throughput/scale-out for `XEC`'s coordination mechanism, no sourced contention-volume capacity threshold). This ADR's eventual acceptance supplies the mechanism whose capacity could then be measured, but does not itself supply the missing scenario or threshold. **Assumption made for this ADR's analysis only:** that the same mechanism can serve both the version-comparison path and the locking path (Option C's premise); a future evaluation could instead find two fully independent mechanisms preferable.

## Open Questions

Should the coordination mechanism be co-located with (or reuse) ADR-0002's execution-truth state store, given both concern versioned state comparison, or should they remain fully independent systems? Not answered here.

## Related Documents / ADRs

`architecture.md` §46, §47.3.2; `implementation-plan.md` §12.3, §25; ADR-0002 (execution-truth state store — directly related per the Open Question above); ADR-0001 (cache invalidation under concurrent access may interact with `XEC`'s conflict detection for shared cached resources).

## Validation / Acceptance Criteria

A prototype reproducing `SCN-CONC-004`, `SCN-CONC-005`, and `SCN-CONC-006` (concurrent modification, required-lock, and coordination-unavailable-fails-closed, respectively), before any status change to `ACCEPTED`.
