# ADR-0004: Telemetry / Observability Backend Selection

| Field | Value |
|---|---|
| ADR ID | `ADR-0004` |
| EAIOC ADR Set | `EAIOC-ADR-001` |
| Status | `PROPOSED` |
| Phase | Level 0 — PRE-IMPLEMENTATION |
| Related Decision | `IMPL-DEC-04` |
| Origin | `implementation-plan.md` §24/§25, item 4 |
| Decision Type | Infrastructure |
| Blocking | No |

## Status

`PROPOSED`. No backend is named or benchmarked anywhere in the corpus.

## Context

`observability.md` fully specifies the telemetry pipeline architecture, metric-to-dashboard mapping, and audit/explanation retrieval layer on top of `interfaces.md`'s already-fixed schemas (`ControlPlaneLogEntry` §19.1, Standard Metrics §19.2, `OptimizationSpan` §19.3, `AuditRecord` §19.5, `ControlPlaneEvent` §22 — cited, not redefined). What `observability.md` does not specify, and explicitly records as open (`SOURCE-GAP-OBS-01`–`04`), is any concrete backend technology or its numeric operating parameters (refresh intervals, alert thresholds, cardinality ceilings). `implementation-plan.md` §24 (`IMPL-DEC-04`) cites `observability.md`'s own gap series and leaves the backend open; §25 lists it as ADR candidate 4.

## Problem Statement

What telemetry backend (log/metrics/trace storage and query system) can host `ControlPlaneLogEntry`, the Standard Metrics set, `OptimizationSpan`, `AuditRecord`, and `ControlPlaneEvent` while satisfying `AuditRecord`'s immutability requirement and the multi-tenant cardinality this schema set implies?

## Decision Drivers / Constraints

- **`AuditRecord` immutability is non-negotiable** (`interfaces.md` §19.5: "Audit records are immutable and may not be deleted") — the backend must support write-once, non-deletable storage for this record type specifically, distinct from ordinary logs/metrics which may have normal retention/deletion policies.
- **Every metric/log/span carries a `tenant_id` label** (`interfaces.md` §19.1's `ControlPlaneLogEntry`, §19.2's Standard Metrics table — every row labels `tenant_id`) — the backend must support per-tenant query isolation and must not blend tenant data (root `CLAUDE.md` rule 4).
- **High-cardinality labeling is structural, not incidental**: metrics are labeled by combinations of `tenant_id` × `model_id`/`provider_id`/`stage_id`/`technique`/`cache_type` (`interfaces.md` §19.2) — the backend's cardinality ceiling (itself unspecified, `SOURCE-GAP-OBS-03`) is a first-class sizing question for this ADR, not an afterthought.
- **End-to-end trace correlation must be navigable** (`interfaces.md` §19.4's chain: `REQUEST_RECEIVED → OPTIMIZATION_DECISION → CONTEXT_OPERATION → CACHE_OPERATION → TOOL_OPERATION → MODEL_ROUTING_DECISION → LLM_CALL → VALIDATION → FINAL_OUTCOME`, "every link must be navigable via `request_id` + `span_id`") — the backend must support distributed-trace-style correlation, not just flat log search.
- **Sensitive-field redaction happens before storage** (`interfaces.md` §19.1: "SENSITIVE fields are redacted; replaced with `[REDACTED]`") — this is an application-layer concern the backend must not undermine (e.g., no backend feature that could re-surface pre-redaction data via a debug/replay capability).

## Source Basis

`interfaces.md` §19.1–§19.5, §22; `observability.md` (full document, cited); `SOURCE-GAP-OBS-01`–`04`; root `CLAUDE.md` rule 4; `implementation-plan.md` §24 (`IMPL-DEC-04`).

## Options Considered

- **Option A — Unified observability platform** (combined logs/metrics/traces in one system). Structural fit: naturally supports §19.4's cross-signal correlation chain in one query surface. Trade-off: may impose a specific data model that doesn't map 1:1 onto `AuditRecord`'s stricter immutability requirement, likely requiring a separate audit-specific store alongside it.
- **Option B — Separate specialized stores per signal type** (a metrics time-series store, a log-search store, a trace store, wired together via `request_id`/`span_id`). Structural fit: each store can be chosen for its signal type's specific access pattern (e.g., a time-series store optimized for the Standard Metrics table's counters/gauges/histograms). Trade-off: cross-signal correlation (§19.4) becomes an integration responsibility across systems rather than a single query.
- **Option C — Append-only/immutable-by-default store dedicated to `AuditRecord`, separate from the general telemetry backend.** Structural fit: directly satisfies the immutability requirement without relying on a general-purpose backend's retention configuration to enforce it. Trade-off: introduces a second backend to operate, on top of whichever choice is made for logs/metrics/traces generally.

No vendor performance/cost claims are asserted as fact.

## Option Analysis

`AuditRecord`'s immutability requirement is strict enough (root `CLAUDE.md` rule 2's fail-closed-on-governance discipline extends to audit-record integrity) that Option C's dedicated-store approach, or an Option A/B choice paired with an audit-specific sub-configuration, both look more defensible than relying on a general telemetry backend's default retention behavior alone — this is a structural observation, not a decision.

## Decision

No decision made — deferred pending local evaluation. Acceptance criteria: a candidate demonstrates non-deletable storage for `AuditRecord`, correlated cross-signal query support reproducing §19.4's chain for at least one end-to-end request, and a cardinality plan addressing `SOURCE-GAP-OBS-03`'s open ceiling question.

## Rationale

As with the other infrastructure ADRs, no local evidence exists to justify a specific pick (root `CLAUDE.md` rules 5, 7). This ADR's value is surfacing that the audit-immutability requirement is the sharpest, most security-relevant constraint on this decision — a constraint easy to overlook if a team picks "whatever observability platform we already use" without checking it against `interfaces.md` §19.5 specifically.

## Consequences

### Positive Consequences

`observability.md`'s architecture and `interfaces.md`'s schemas are fully specified independent of backend technology, so deferring this decision blocks nothing else.

### Negative Consequences

None yet observable.

### Risks

A backend chosen primarily for cost/familiarity that cannot cleanly enforce `AuditRecord` immutability risks a governance/compliance gap discovered late. Mitigated by testing this specific requirement before acceptance, not assuming a general-purpose backend satisfies it by default.

### Operational Consequences

`No material impact identified from the authoritative corpus`.

## Rejected / Deferred Alternatives

None rejected.

## Requirements Traceability

`NFR-006` (Observability — "every optimization decision must be measurable," cited); `NFR-010`/H15 (Explainability/audit retrievability, cited); `SEC-013` (deletion-propagation, reconciled against `AuditRecord` immutability by `security.md`, cited not redefined here).

## Architecture Traceability

`architecture.md` §31 (Observability and Governance, cited); §47.16 (Decision Explainability, cited).

## Interface / Contract Impact

None. This ADR selects a backend for schemas `interfaces.md` already fixes; it does not modify them.

## Security / Governance Impact

Directly load-bearing: the selected implementation must provide an enforcement mechanism that preserves the source-defined non-deletability of `AuditRecord` (`interfaces.md` §19.5), consistent with `security.md`'s SEC-013 reconciliation (cited, not redefined) that audit records are the one deliberate exception to this repository's otherwise-permitted deletion-propagation model. The exact administrative enforcement mechanism — e.g., whether it blocks deletion even under administrative/operational access — is not specified upstream and must be validated during implementation evaluation.

## Observability / Evaluation Impact

This ADR *is* the observability-backend decision; `observability.md`'s dashboard/alerting architecture (cited) is what runs on top of whatever is chosen here.

## Scaling Impact

Cited from `SCALING.md`'s `SC-008` (observability capacity surface, `SOURCE-DERIVED`) and `SOURCE-GAP-OBS-03` (no sourced cardinality ceiling) — this ADR's eventual resolution directly narrows that gap's practical answer, without closing the gap itself (the gap is about the absence of a sourced number, not about which backend is chosen).

## Implementation Impact

`implementation-plan.md` §8.1/§8.2's exemplar capabilities (Token Accounting/Cost Ledger, Baseline Benchmark Harness) both depend on observable telemetry existing, per their Sub-phase D (Observability) requirements — cited, not redefined here.

## Migration / Rollback Considerations

Not applicable yet — no backend selected.

## Source Gaps / Assumptions

**Existing source gaps directly relevant** (not resolved by this ADR): `SOURCE-GAP-OBS-01` (no sourced refresh intervals), `SOURCE-GAP-OBS-02` (no sourced alert thresholds), `SOURCE-GAP-OBS-03` (no sourced cardinality/sampling/SLO numbers). This ADR's eventual acceptance will supply the technology that makes those numbers measurable, but does not itself supply the numbers. **Assumption made for this ADR's analysis only:** that a single backend (or backend-plus-dedicated-audit-store pairing) can serve all of logs/metrics/traces/audit jointly, rather than requiring four fully separate systems.

## Open Questions

Should the audit-record store be a fully separate system from the general telemetry backend as a matter of policy (regardless of which general backend is chosen), given the immutability requirement's security significance? This ADR raises but does not resolve it.

## Related Documents / ADRs

`observability.md`; `interfaces.md` §19, §22; `implementation-plan.md` §24; ADR-0002 (both ADRs touch audit/immutability requirements — `ESM`'s `reversibility_records` vs. `AuditRecord` — see cross-ADR review in `0000-index.md`).

## Validation / Acceptance Criteria

A prototype demonstrating non-deletable `AuditRecord` storage, a reproduced §19.4 correlation chain for one end-to-end request, and a documented cardinality-management plan, before any status change to `ACCEPTED`.
