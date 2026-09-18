# ADR-0001: Cache / Key-Value Backing Store Selection

| Field | Value |
|---|---|
| ADR ID | `ADR-0001` |
| EAIOC ADR Set | `EAIOC-ADR-001` |
| Status | `PROPOSED` |
| Phase | Level 0 — PRE-IMPLEMENTATION |
| Related Decision | `IMPL-DEC-01` |
| Origin | `implementation-plan.md` §24/§25, item 1 |
| Decision Type | Infrastructure |
| Blocking | No |

## Status

`PROPOSED`. Not `ACCEPTED`: this repository has no local benchmark, prototype, or production evidence for any backing-store candidate. Marking this `ACCEPTED` today would fabricate an approval event the corpus does not support (root `CLAUDE.md` rule 5).

## Context

`cache-strategy.md` specifies a complete, provider-neutral behavioral contract for caching — the `CacheStore` interface (`interfaces.md` §6.1: `lookup`/`write`/`invalidate`/`refresh`/`warm`/`inspect`/`explain_miss`), the seven canonical cache types (`CACHE-001`–`007`), tenant-scoped key construction, the four-check gate, and dependency-aware invalidation — but never specifies *what technology implements the store behind that interface*. `implementation-plan.md` §24 (`IMPL-DEC-01`) names this an open, non-blocking implementation decision and §25 lists it as ADR candidate 1.

## Problem Statement

What concrete key-value backing technology satisfies `CacheStore`'s behavioral contract for all seven cache types (`EXACT`, `SEMANTIC`, `TOOL_RESULT`, `EMBEDDING`, `PROVIDER_NATIVE`, `APPLICATION`, `CONTEXT`) while preserving tenant isolation, the four-check gate, and dependency-aware invalidation?

## Decision Drivers / Constraints

- **Tenant isolation is absolute** (root `CLAUDE.md` rule 4; `conventions.md` §13.2): every cache key must be scoped by `tenant_id` as the first namespace component; cross-tenant lookups/sharing are unconditionally prohibited. Any candidate store must support this as a structural property of key construction, not an application-level convention alone.
- **The `CacheStore` interface contract is fixed** (`interfaces.md` §6.1, restated in `cache-strategy.md` §1) — `lookup`/`write`/`invalidate`/`refresh`/`warm`/`inspect`/`explain_miss` must all be implementable against the candidate.
- **The four-check gate is mandatory for every non-`EXACT` type** (`cache-strategy.md` §5) — the combined cache implementation must support the four-check gate (similarity/freshness/authorization/policy-version) efficiently at lookup time. The backing store need not natively implement every check; application-layer mechanisms are acceptable provided the complete `CacheStore` behavior satisfies the source-defined contract.
- **Dependency-aware invalidation (`CL-003`)** (`cache-strategy.md` §10) requires cascading invalidation across related keys — the store must support either native cascade primitives or an efficient enough scan/index to implement it at the application layer without violating latency budgets.
- **`PROVIDER_NATIVE` writes are plausibly provider-adapter-internal, never core-EAIOC-written** (`cache-strategy.md` §31, `SOURCE-GAP-CACHESTRAT-03`) — the backing-store choice for the other six types need not accommodate whatever a provider's own native cache mechanism uses internally.
- **No storage-cost model exists upstream** (`SOURCE-GAP-CACHESTRAT-04`) — the backing-store decision cannot yet be evaluated on cost, only on functional/behavioral fit.

## Source Basis

`interfaces.md` §6.1–6.5 (schema, restated verbatim in `cache-strategy.md` Appendix A); `cache-strategy.md` §1–§12; `conventions.md` §13.2; root `CLAUDE.md` rule 4; `implementation-plan.md` §24 (`IMPL-DEC-01`).

## Options Considered

- **Option A — Managed distributed key-value store** (e.g., a hosted Redis-compatible or DynamoDB-style service). Structural fit: native TTL support (useful for `EXACT`/`TOOL_RESULT`'s TTL-bounded types), typically strong single-key latency, horizontal scalability. Trade-off: cascading invalidation (`CL-003`) and similarity search (`SEMANTIC`/`EMBEDDING`'s four-check gate) are not native primitives in a pure KV store and would need an application-layer index.
- **Option B — Embedded/in-process store** (e.g., an in-memory reference implementation). Structural fit: simplest to stand up first, matches `implementation-plan.md` §24's own stated interim path ("P1 can build the behavioral contract against an in-memory reference implementation first"). Trade-off: no cross-process sharing, no durability, not viable beyond a single-instance reference/test implementation.
- **Option C — Store with native vector/similarity support** (relevant specifically to `SEMANTIC`/`EMBEDDING`). Structural fit: the four-check gate's similarity-threshold check (`cache-strategy.md` §7) is a first-class capability rather than an add-on. Trade-off: may not be the best fit for the other five cache types, raising the possibility of more than one backing technology (one per cache-type cluster) rather than a single uniform store.
- **Option D — Relational database with a KV-shaped schema.** Structural fit: strong transactional/consistency guarantees, native support for structured invalidation queries (helps `CL-003`). Trade-off: typically higher single-key lookup latency than a purpose-built KV store, which matters given `cache-strategy.md`'s own latency-sensitive lookup path.

No vendor-specific performance, cost, or reliability claim is asserted as fact for any option; any such claim from a vendor's own materials would need local verification before being relied upon (root `CLAUDE.md` rule 7).

## Option Analysis

Each option's fit varies by cache type rather than uniformly — Options A and C in particular suggest the real decision may not be "one store for all seven types" but "how many distinct backing technologies, and along what cache-type boundary." This is itself an open question this ADR surfaces rather than resolves (see Open Questions).

## Decision

No decision made — deferred pending local evaluation. Acceptance criteria for moving to `ACCEPTED`: a candidate (or small set of candidates, one per cache-type cluster) is prototyped against `CacheStore`'s full interface contract, the four-check gate, and `CL-003`'s cascading-invalidation behavior, with tenant-scoped key isolation verified structurally, before any production traffic is routed through it.

## Rationale

Per root `CLAUDE.md` rule 5 and rule 7, no percentage, latency figure, or vendor claim in this corpus may be treated as a production guarantee without local validation — and none exists for this decision. Recording the decision's shape now (constraints, real candidate space, the per-cache-type-boundary question) lets a future team with actual evaluation evidence resolve this quickly rather than rediscovering the constraints from scratch.

## Consequences

### Positive Consequences

The behavioral contract (`cache-strategy.md`) is already fully specified and store-agnostic, so this decision can be deferred without blocking any other part of the design — the contract itself does not change based on which store implements it.

### Negative Consequences

None yet observable — no implementation exists to be affected.

### Risks

If the eventual store choice cannot efficiently support `CL-003`'s cascading invalidation or the four-check gate's similarity search, a costly re-platforming could be needed after initial build. Mitigated by prototyping against the full contract (see Decision) before committing.

### Operational Consequences

`No material impact identified from the authoritative corpus` — no operational model exists yet since no store has been selected.

## Rejected / Deferred Alternatives

None rejected; all four options remain open candidates pending evaluation.

## Requirements Traceability

`SEC-*`/tenant-isolation requirements (`conventions.md` §13.2); `NFR-007` (Reliability — fallback behavior on cache-store failure is already specified in `cache-strategy.md` independent of this ADR).

## Architecture Traceability

`architecture.md` §16 (Cache Economics and Cache-Aware Assembly, cited); `interfaces.md` §6.1–6.5 (cited, not redefined by this ADR).

## Interface / Contract Impact

None. This ADR selects an implementation substrate behind the existing `CacheStore` interface; it does not change the interface itself.

## Security / Governance Impact

The backing store must structurally support `tenant_id`-first key namespacing (root `CLAUDE.md` rule 4). The combined cache implementation (backing store plus application layer) must never allow a lookup to bypass the four-check gate's authorization re-validation (`cache-strategy.md` §5, echoing `EC-078`'s discipline that similarity alone is never sufficient authorization) — the backing store itself need not natively enforce this check, per the implementation-boundary clarification in Decision Drivers above.

## Observability / Evaluation Impact

Whatever store is chosen must expose enough operational telemetry to populate `control_plane.cache.hit_rate` (`interfaces.md` §19.2, cited) — this ADR does not define a competing metric.

## Scaling Impact

Cited from `SCALING.md`'s `SC-004` (cache capacity surface, `SOURCE-DERIVED`) — the eventual store choice is one of the inputs to that capacity model, not redefined here.

## Implementation Impact

`implementation-plan.md` §9 sequences the P1 cache-related capabilities to build against the behavioral contract using an in-memory reference implementation first (§24's stated interim path), independent of this ADR's eventual resolution.

## Migration / Rollback Considerations

Not applicable yet — no store has been selected, so there is no migration path to define. A future acceptance of this ADR should include a migration plan if a reference implementation's data needs to move to the accepted store.

## Source Gaps / Assumptions

**Existing source gaps** (not resolved by this ADR): `SOURCE-GAP-CACHESTRAT-01` (no single field aggregates cache eligibility), `SOURCE-GAP-CACHESTRAT-03` (`PROVIDER_NATIVE`/`APPLICATION` missing from `CacheWriteRequest`'s enum), `SOURCE-GAP-CACHESTRAT-04` (no storage-cost model). **Assumption made for this ADR's analysis only:** that a single backing-store family can plausibly serve all six non-`PROVIDER_NATIVE` cache types, pending the per-cache-type-boundary question raised in Open Questions.

## Open Questions

Should the eventual decision be one backing technology for all six core cache types, or a small set (e.g., a KV store for `EXACT`/`TOOL_RESULT`/`CONTEXT`/`APPLICATION` plus a vector-capable store for `SEMANTIC`/`EMBEDDING`)? This ADR does not answer it — it surfaces it for the eventual acceptance decision.

## Related Documents / ADRs

`cache-strategy.md`; `implementation-plan.md` §9, §24; ADR-0002 (shares the "what backs versioned/execution state" question, see cross-ADR review in `0000-index.md`); ADR-0006 (`XEC` coordination may interact with cache invalidation under concurrent access).

## Validation / Acceptance Criteria

A prototype demonstrating: (1) tenant-scoped key isolation enforced structurally; (2) all seven `CacheStore` methods implementable; (3) the four-check gate executable within `cache-strategy.md`'s latency expectations; (4) `CL-003` cascading invalidation demonstrated for at least one dependency chain.
