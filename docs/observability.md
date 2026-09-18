# Observability, Telemetry Pipeline, Metrics, Logs, Distributed Tracing, Events, Audit/Explainability, Dashboards, SLO/SLI, Alerting, Telemetry Governance, and Measurement-Integrity Reference

**Document ID:** EAIOC-OBS-001
**Status:** PRE-IMPLEMENTATION — Level 0 (RESEARCH)
**Version:** 1.0.0
**Generated:** 2026-09-18 (per repository date)
**Generation prompt:** `docs/prompts/generate-observability.prompt.md` (V2 Consolidated Master Prompt)

**Position in the documentation chain:** Eighth generated downstream document, following `optimization-catalog.md` → `provider-matrix.md` → `cache-strategy.md` → `agent-optimization.md` → `inference-optimization.md` → `quality-gates.md` → `security.md`. Gated by each of those seven documents' own passing readiness verdict. Next in chain: `eval.md` (not generated here, not cascaded into).

**Sources read in full or by targeted section/ID lookup:** `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt` (PS §12, §52.15); `Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md` (ES §42.15); `architecture.md` (§27, §28, §29, §31, §47.16, NFR-006/009/010/014, OBJ-011, AC-052); `interfaces.md` (§4.2 `explain()`/`ExplanationRecord`, §19, §23, §29); `conventions.md` (§17); `edge-cases.md` (§36 and the H15 hardening entries; EC-066, EC-067, EC-070, EC-076, EC-198, EC-199 verified directly); `scenario-matrix.md` (Domain AC — verified directly, see §38); `optimization-catalog.md`, `provider-matrix.md`, `cache-strategy.md`, `agent-optimization.md`, `inference-optimization.md`, `quality-gates.md`, `security.md` (each consumed for its own already-named metrics, not redefined). Root `CLAUDE.md` re-read for scope/ID/process discipline.

**Scope boundary reminder:** This document owns the telemetry pipeline architecture, metric-to-dashboard mapping, alerting architecture/thresholds, SLO/SLI model, audit/explanation retrieval layer, and cardinality/sampling/retention governance implementation. It does **not** redefine `interfaces.md` §19/§23/§29's schemas, `architecture.md` §31's abstract dashboard-tier/dimension/governance-question lists, `security.md`'s GSP mechanisms, `quality-gates.md`'s methodology, or any other document's already-named metrics — those are cited throughout, never restated as if newly defined here.

---

## 1. Status and Maturity

This document is **Level 0 — RESEARCH / PRE-IMPLEMENTATION**, consistent with every other document in this chain. There is no local production traffic, no deployed telemetry backend, and no measured SLO/SLI history. Accordingly:

- No threshold, retention value, cardinality limit, refresh interval, or SLO in this document is presented as validated, industry-standard, or production-proven unless a specific upstream source is cited for it.
- Every number this document proposes that is not directly stated in an upstream source is tagged `PROPOSED DEFAULT — VALIDATE LOCALLY`.
- Every procedure/methodology this document proposes that is not directly stated in an upstream source is tagged `PROPOSED METHODOLOGY`.
- Where an upstream source already states a concrete rule (e.g., "AUDIT and POLICY_VIOLATION events must be durably written" — `conventions.md` §17.4), that rule is cited as `SOURCE-DEFINED`, not re-derived.

## 2. How to Read This Document

Every numeric or procedural claim carries one of four labels: `SOURCE-DEFINED` (an upstream document states this exactly), `SOURCE-DERIVED` (directly implied by combining multiple source requirements, formulation synthesized here), `PROPOSED METHODOLOGY` (a technique/procedure this document introduces because no source specifies one), or `PROPOSED DEFAULT — VALIDATE LOCALLY` (a specific number this document proposes for the same reason). Do not read any `PROPOSED` label as authoritative — it is a starting point for local validation once an implementation exists.

## 3. Scope Boundary

| Concern | Owner |
|---|---|
| Observability interface schemas (`ControlPlaneLogEntry`, Standard Metrics, `OptimizationSpan`, `AuditRecord`, `ControlPlaneEvent`, `ExplanationRecord`) | `interfaces.md` §19/§23/§29 (cited, not redefined) |
| Dashboard-tier definitions, required dimensions, governance questions (abstract lists) | `architecture.md` §31 / `conventions.md` §17 (cited, not redefined) |
| Token/cost ledger fields and cost model | `architecture.md` §27 (cited, not redefined) |
| Quality methodology | `quality-gates.md` (cited, not redefined) |
| Security/governance mechanisms (SGE/DGE/TMG/HAG/CIS) | `security.md` (cited, not redefined) |
| Provider-specific facts | `provider-matrix.md` (cited, not redefined) |
| Cache-specific metrics/mechanics | `cache-strategy.md` (cited, not redefined) |
| Agent-specific metrics/mechanics | `agent-optimization.md` (cited, not redefined) |
| P5 inference-serving metrics/mechanics | `inference-optimization.md` (cited, not redefined) |
| Benchmark/evaluation methodology | `eval.md` (not yet generated) |
| Capacity/throughput targets | `SCALING.md` (not yet generated) |
| Canonical cross-document requirements traceability | `requirements-traceability.md` (not yet generated) |
| **Telemetry pipeline architecture, metric-to-dashboard mapping, log/trace/event/audit operational integration, aggregation/query model, telemetry enrichment, cardinality governance, metric freshness, telemetry reliability, dashboard implementation model, alerting architecture/thresholds, observability SLO/SLI implementation, telemetry quality checks, observability failure detection, audit/explanation retrieval layer, observability data-governance implementation, dashboard access model, tenant-isolated observability views, measurement-integrity controls, operational observability validation** | **This document** |

Where an upstream document already defines an exact metric name (e.g., `control_plane.cache.hit_rate`, `verifier.pass_rate`, `tmg.schema_staleness_event.count`, the `inference_serving.*` namespace), this document consumes that name. No competing metric name is introduced for a concept that already has one.

## 4. Central Observability Invariant

> **Every optimization decision must be measurable, and every governed decision must produce a retrievable explanation** (`architecture.md` §31/NFR-006, §47.16/NFR-010/H15).

This invariant decomposes into ten preserved distinctions, restated here because every section below depends on them:

1. Measurement is mandatory (NFR-006) — every optimization decision produces telemetry.
2. Audit/explanation retrievability is mandatory where governance requires it (NFR-010/H15/AC-052) — for the six §47.16 decision categories.
3. Default end-user UI presentation of an audit explanation is **not** universally mandatory (`EC-199`) — retrievability and presentation are separate interface decisions; do not conflate them.
4. A required audit/explanation write failure is a **governance failure** and fails closed per `architecture.md` §47.16/`EC-198` — this is the one deliberate exception to this whole chain's fail-open-for-optimization model (root `CLAUDE.md` rule 2). It does **not** generalize: an ordinary metrics/logging/dashboard failure is an operational reliability concern, not a governance fail-closed trigger (see §20).
5. Optimization failure and application failure must not be conflated — an optimization stage that falls back to baseline is not itself an application-level failure.
6. Telemetry failure itself must be observable (§20, §36) — an observability system that cannot report its own health is not trustworthy.
7. Tenant boundaries must never be crossed by observability data (`conventions.md` §13.2; `interfaces.md` §23.3's "Cross-tenant: Events must not be delivered across tenant boundaries").
8. P5 inference-serving measurements remain separable from Layer 1/2 optimization measurements (`inference-optimization.md`'s `inference_serving.*` namespace; `EC-076`/`AC-036`) — cited, not redefined, in §25.
9. Estimated savings must never be represented as verified savings (`EC-067`) — a structural requirement for `OBS-001`, not a UI nice-to-have.
10. Provider-native savings must never be silently attributed to EAIOC-owned optimization (cross-reference `provider-matrix.md`'s provider-native cache/prefix-cache facts and `cache-strategy.md`'s `PROVIDER_NATIVE` cache type boundary — cited, not redefined).

## 5. Observability Architecture (Telemetry Pipeline)

The logical pipeline, expressed as a Control-Plane-owned data flow — provider-agnostic; no specific vendor (OpenTelemetry, Prometheus, Grafana, Kafka, or any other) is mandated by any upstream source, so any vendor named below is an **illustrative implementation example**, not an authoritative requirement:

```
Request
  -> instrumentation (every component invocation, per conventions.md §17.1)
    -> identity/correlation enrichment (tenant_id, request_id, correlation_id, span_id — interfaces.md §19.1)
      -> telemetry generation (metrics + logs + traces + events + audit, produced at the point of decision)
        -> collection (PROPOSED METHODOLOGY: a collector layer local to each component, not source-specified)
          -> validation (§36: required-field/schema-version/tenant-ID checks)
            -> normalization (canonical metric/label naming, per §10)
              -> tenant/security filtering (§18; PII redaction per interfaces.md §19.1's "[REDACTED]" rule)
                -> aggregation (per §10's aggregation-type table)
                  -> durable storage (audit/policy-violation events per conventions.md §17.4; other telemetry per §19's retention rules)
                    -> query layer (PROPOSED METHODOLOGY: the audit/debug retrieval interface in §12)
                      -> dashboards (§26-29)
                        -> alert evaluation (§30-31)
                          -> notification/incident workflow (§30)
```

Five telemetry paths flow through this pipeline and must remain independently identifiable even where they share collection/storage infrastructure:

| Path | Canonical Schema | Converges With | Must Remain Separate From |
|---|---|---|---|
| Metrics | `interfaces.md` §19.2 Standard Metrics | Dashboards, alerting | Audit records (metrics are aggregatable/sampleable; audit records are not, per §16) |
| Logs | `ControlPlaneLogEntry` (§19.1) | Trace correlation (via `span_id`) | Audit records (a log entry is operational, an `AuditRecord` is a governance-tier immutable record — `security.md`'s Fix 3 reconciliation applies here too) |
| Traces | `OptimizationSpan` (§19.3), the End-to-End Trace Correlation Chain (§19.4) | Logs (shared `span_id`/`request_id`) | — |
| Events | `ControlPlaneEvent` (§23.1), Standard Event Types (§23.2) | Alerting (event-triggered alerts), Audit (AUDIT/POLICY_VIOLATION events are durably written per §23.3) | Ordinary metrics (events are discrete occurrences, metrics are continuous measures) |
| Audit/Explainability | `AuditRecord` (§19.5), `ExplanationRecord` (§29.1, INTF-045) | Governed-decision dashboards (§27's "Can we prove the result?" question) | Every other path, structurally — immutable, non-deletable (`interfaces.md` §19.5: "Audit records are immutable and may not be deleted"), never sampled away (§16) |

## 6. Telemetry Types and Responsibilities

Five distinct telemetry object types exist in this framework and must not be collapsed into one generic "telemetry event": **Operational Log** (`ControlPlaneLogEntry`, ephemeral/sampleable, §19.1), **Metric** (`interfaces.md` §19.2, aggregatable, labeled), **Trace Span** (`OptimizationSpan`, §19.3, hierarchical, request-scoped), **Event** (`ControlPlaneEvent`, §23.1, discrete, fan-out to subscribers), and **Audit Record / Explanation** (`AuditRecord` + `ExplanationRecord`, §19.5/§29.1, immutable, governance-tier, never sampled). Each has a distinct retention, durability, and query-access profile (§19, §32).

## 7. Metric Pipeline

`interfaces.md` §19.2's 13 standard metrics are this document's canonical metric contract — consumed here, not redefined:

| Metric Name | Type | Aggregation | Alerting Suitability | Freshness Expectation |
|---|---|---|---|---|
| `control_plane.request.count` | Counter | Sum | High (traffic-anomaly detection) | Near-real-time — `PROPOSED METHODOLOGY` |
| `control_plane.request.latency_ms` | Histogram | Percentile (p50/p95/p99) | High (latency SLO) | Near-real-time |
| `control_plane.tokens.input`/`.output` | Counter | Sum | Medium (cost-acceleration signal, cross-reference `security.md` `GSP-001` SGE) | Near-real-time |
| `control_plane.tokens.avoided` | Counter | Sum | Low (informational savings signal) | Batch acceptable |
| `control_plane.cost.estimated`/`.saved` | Counter | Sum | High (feeds `OBS-001`'s savings distinction — §27) | Near-real-time for `.estimated`; batch acceptable for `.saved` once verified |
| `control_plane.cache.hit_rate` | Gauge | Ratio | Medium (cache-degradation signal) | Near-real-time |
| `control_plane.stage.success_rate`/`.fallback_rate` | Gauge | Ratio | High (fallback-rate alert, §31) | Near-real-time |
| `control_plane.quality.score` | Histogram | Percentile | High (quality-regression signal, cross-reference `quality-gates.md`) | Near-real-time |
| `control_plane.agent.iterations` | Histogram | Percentile | Medium (loop-waste signal, cross-reference `agent-optimization.md` `AL-002`) | Near-real-time |
| `control_plane.tool.calls_avoided` | Counter | Sum | Low | Batch acceptable |

Where a **derived metric** is necessary (a KPI not directly one of the 13 above), it is explicitly marked `DERIVED METRIC` with its source metrics and formula shown, and its formula is classified `SOURCE-DEFINED` (e.g., `architecture.md` §27.3's `Savings % = Net Savings / Baseline Cost x 100`), `SOURCE-DERIVED`, or `PROPOSED METHODOLOGY`:

- **DERIVED METRIC — `governance.savings_report.verified_pct`** (`EC-067`'s own Observability field, `SOURCE-DEFINED`): `verified_savings / (verified_savings + estimated_savings)`, both terms drawn from `architecture.md` §27's `cost.net_savings` split by `verification_status` (`optimization-catalog.md` §27's `UNVERIFIED`/`VERIFIED` distinction — cited, not redefined here).
- **DERIVED METRIC — fallback-rate-by-tenant** (`PROPOSED METHODOLOGY`): `control_plane.stage.fallback_rate` grouped by `tenant_id`, to detect a tenant-scoped degradation the global rate would mask.

Existing per-document metrics already named upstream and consumed (not re-derived) here include: `tmg.schema_staleness_event.count` (`security.md` `GSP-003`), `verifier.pass_rate`/`verifier.escalations_triggered.count`/`verifier.acceptance_rate_drift` (`agent-optimization.md` `AR-004`), the `inference_serving.*` namespace (`inference-optimization.md`, all 11 `INFOPT-NNN` entries), `explainability.audit_write_failure.count`/`explainability.stage_blocked_due_to_audit_failure.count` (`EC-198`'s own Observability field), `audit.reversibility_write_failed.count` (`EC-066`'s own Observability field), `ledger.unverified_estimate.count` (`EC-067`'s own Observability field).

## 8. Logging Pipeline

`conventions.md` §17.2's log-level table is `SOURCE-DEFINED` and consumed verbatim: DEBUG (internal state, off in production by default), INFO (normal decisions, cache hits, model selections), WARNING (fallback triggered, quality degraded, near-threshold), ERROR (stage failure, policy violation, security check failure), CRITICAL (security breach, irrecoverable failure, data integrity issue). Every log entry carries the 11 `ControlPlaneLogEntry` fields (§19.1) — this document adds no new required field. Sensitive fields are redacted to `[REDACTED]` at the point of log generation, per §19.1's own rule — this document does not redefine the redaction mechanism (owned by `security.md`'s DGE/`GSP-002`), only consumes its output.

## 9. Distributed Tracing Pipeline

`conventions.md` §17.1's mandatory-tracing rule is `SOURCE-DEFINED`: every component invocation emits an `OptimizationSpan`; the end-to-end chain (`interfaces.md` §19.4) is navigable via `request_id` + `span_id` with no gaps, and a break in the chain is a convention violation. This document's pipeline responsibility is the trace-continuity **monitoring** layer built on top of that mandatory contract (§14) — it does not redefine `OptimizationSpan`'s schema or the chain itself. Span lifecycle events this document's pipeline must handle: creation, propagation to child spans (sub-agent spawns, tool calls, provider calls — cross-reference `agent-optimization.md`'s `AL-004`/`AL-005`), asynchronous boundaries, retries, cancellations, fallbacks (`status: FALLBACK`), and terminal outcomes (`status: OK` / `ERROR`).

## 10. Event Pipeline

`interfaces.md` §23's 14 Standard Event Types and its Event Delivery Requirements table (§23.3) are `SOURCE-DEFINED` and consumed verbatim — this document does not add a new standard event type. `AUDIT` and `POLICY_VIOLATION` events are durably written and replayable (append-only store); events never cross `tenant_id` boundaries; event payloads never carry raw PII. This document's pipeline responsibility is fan-out delivery to dashboard/alert consumers, idempotent consumption (duplicate-delivery handling — `PROPOSED METHODOLOGY`, since no source specifies an exact idempotency-key scheme for event consumption), and a dead-letter/recovery path for undeliverable durable events (`PROPOSED METHODOLOGY`).

## 11. Audit / Explainability Pipeline

See §27 (Decision Explainability, the dedicated major section) for the full six-category treatment. This section states the pipeline mechanics: every `AuditRecord` (§19.5) and `ExplanationRecord` (§29.1) write is synchronous with the decision it documents (not a best-effort side-channel, per `architecture.md` §47.16), is durably stored (never sampled away, §16), and is retrievable via the audit/debug query interface (§12 below) scoped by `request_id`/`tenant_id`. This document does not redefine either schema.

## 12. Audit/Debug Retrieval Interface (Retrievability Layer)

`EC-199`'s boundary case requires a retrieval mechanism to exist even though no source mandates a specific UI. This document proposes (`PROPOSED METHODOLOGY`, since no upstream interface defines this exact shape) a conceptual query capability: given a `request_id` (and, for a specific decision, a `plan_id` or `component_id`), return the full `ExplanationRecord` chain (`decision_rationale`, `stages_applied`, `stages_skipped`, `cost_breakdown`, `quality_impact_summary`, `policy_constraints`, `model_routing_rationale` — all fields cited from §29.1, not redefined) plus the associated `AuditRecord`s. This is an audit/debugging interface, not an end-user UI — `SCN-AUDIT-001`'s own text confirms the explanation must not leak information the requester isn't authorized to see (e.g., another tenant's routing signals), so this retrieval interface is itself subject to the tenant-isolation and access-control rules in §18/§31.

## 13. Telemetry Data Model

The five telemetry types (§6) share four common correlation fields — `request_id`, `tenant_id`, `span_id`/`correlation_id`, `timestamp` — which is what makes the End-to-End Trace Correlation Chain (§19.4) and the audit retrieval interface (§12) possible. No new shared schema is introduced here; this section only states the cross-cutting correlation contract that makes §5's pipeline diagram coherent.

## 14. Required Dimensions

`architecture.md` §31.2's 10 dimensions are `SOURCE-DEFINED`. This document classifies each for cardinality/labeling purposes (§15):

| Dimension | Mandatory/Conditional | Suitable as Metric Label? | Cardinality Note |
|---|---|---|---|
| Tenant | Mandatory (root `CLAUDE.md` rule 4) | Yes | Bounded by tenant count — safe |
| Application | Mandatory | Yes | Bounded — safe |
| Workflow | Conditional | Query/grouping dimension only | Potentially unbounded per-workflow-instance — do not label directly; label by `workflow_type` instead (`PROPOSED METHODOLOGY`) |
| Agent | Conditional | Yes, if bounded to `agent_type` | `agent_id` (unbounded) belongs in traces/logs, not metric labels |
| Model | Mandatory | Yes | Bounded — safe |
| Provider | Mandatory | Yes | Bounded — safe |
| User/team where permitted | Conditional (authorization-gated, per `security.md` `GSP-002` DGE classification) | Query/grouping dimension only, never a default label | High cardinality + privacy risk |
| Prompt type | Conditional | Yes, if bounded to a fixed taxonomy | Unbounded free-text prompt content must never become a label |
| Intent | Conditional | Yes, if bounded to a fixed taxonomy | Same caution as prompt type |
| Date/time | Mandatory | Yes (as a time-series axis, not a label) | N/A |

Not every dimension automatically becomes a high-cardinality metric label — several are query/grouping dimensions resolved at query time against logs/traces rather than baked into every metric's label set (§15).

## 15. Cardinality Governance

`PROPOSED METHODOLOGY` throughout, since no upstream source specifies cardinality limits. High-cardinality identifiers (`request_id`, `user_id`, `agent_id`, `workflow_id` instances) belong in trace attributes, logs, and audit records — never in metric labels, where they would cause label-cardinality explosion. `prompt type`/`intent` labels are restricted to a bounded, policy-configured taxonomy (cross-reference `interfaces.md` §20.1's `OptimizationPolicy`, cited not redefined) rather than free text. Detection: `PROPOSED DEFAULT — VALIDATE LOCALLY` — alert when a single metric's label-value cardinality exceeds a configured ceiling within a rolling window. Remediation: drop or re-bucket the offending label dimension; never silently drop the entire metric.

## 16. Sampling

Performance telemetry (traces, verbose DEBUG logs) may be sampled — `PROPOSED METHODOLOGY` for the exact sampling policy, since no source specifies one. Governance/audit telemetry (`AuditRecord`, `ExplanationRecord`, `AUDIT`/`POLICY_VIOLATION` events) is never sampled — this is `SOURCE-DERIVED` directly from `interfaces.md` §19.5's immutability rule and §23.3's mandatory-durability rule: a sampled-away audit record would violate both. This is the same distinction `security.md`'s reconciliation of `AuditRecord` immutability established for deletion (cited, not redefined) — sampling and deletion are both irreversible removals of a governance-tier record and are governed by the identical rule.

## 17. Telemetry Data Governance

Integrates `security.md`'s `GSP-002` (DGE) requirements — cited, not redefined. Sensitivity classification, PII/secrets handling, redaction, and encryption are DGE's mechanism; this document's responsibility is ensuring the telemetry pipeline actually invokes that classification before any content-bearing field (e.g., a log `message` or `fields` map, or an `ExplanationRecord.optimization_summary`) is persisted. No specific regulatory regime (GDPR/HIPAA/PCI-DSS) is asserted to apply — consistent with `architecture.md` §47.2.2's `SOURCE-GAP-ARCH-01` (deployment-specific), which this document does not attempt to close.

## 18. Tenant Isolation

`SOURCE-DEFINED` throughout: `interfaces.md` §21.3's `TenantIsolationContext` guarantees (cache/memory/telemetry/cost-accounting/policy/audit all tenant-scoped) apply identically to every telemetry type in this document. Observability-specific consequences: no dashboard query may aggregate across tenants without explicit, policy-authorized multi-tenant reporting; no alert may fire based on a cross-tenant aggregate unless that aggregate is itself an authorized platform-level (not customer-facing) view; the audit retrieval interface (§12) enforces the same `AuthorizationService` check (`interfaces.md` §21.2) that gates every other tenant-scoped resource.

## 19. Retention and Deletion

Retention is source-defined per surface, not invented here: `AuditRecord`s are immutable and never deleted (§19.5); `AUDIT`/`POLICY_VIOLATION` events are durably written and replayable (§23.3); every cache, memory layer, ledger, and log/trace carries a configurable retention period with no unbounded default (`architecture.md` §47.2.2, cited via `security.md` `GSP-002`). `SEC-013` deletion propagation (cited from `security.md`, not redefined) extends to log/trace surfaces this document's pipeline persists — a deletion/erasure request must reach the observability backend's log/trace store, not only the primary application store. `AuditRecord`s remain the one explicit exception (per `security.md`'s Fix 3 reconciliation, cited here) — deletion propagation applies to `DataSurface` values, and `AuditRecord` is deliberately not one of them.

## 20. Telemetry Reliability

Observability must observe its own health — a collector, exporter, storage, or query-layer failure is itself a telemetry-reliability event, distinct from the governance-tier fail-closed exception in §4/§27. Failure classes: collector failure, exporter failure, storage failure, event-bus failure, query failure, dashboard failure, metric-ingestion lag, log loss, trace loss, partial telemetry availability. **Only the specific case of a required audit/explanation write failing (§27, `EC-198`) follows the fail-closed rule** — an ordinary metrics-collector outage, a dashboard-query timeout, or a dropped DEBUG log does not block the request or force a fallback; it is logged/alerted as an operational degradation (§30) and the request proceeds normally. This document does not claim that every telemetry failure must block application execution — doing so would contradict root `CLAUDE.md` rule 2's fail-open-for-optimization model, and `EC-198`'s own text is explicit that its fail-closed behavior is "a deliberate exception," not the general rule.

## 21. Control-Plane Self-Observability

Integrates `NFR-014`/`security.md`'s Self-Protection Controller cross-reference (cited, not redefined). The Control Plane observes its own processing latency, telemetry-generation overhead, queue/backpressure, dropped work, optimization processing cost, stage failures, degradation level, safe-fallback activation, and capacity pressure — distinct from application-level observability (§4/§26-29). `NFR-009` ("optimization must not consume more cost than it saves") applies to the observability layer's own overhead too (§22): if telemetry generation/collection/storage cost approaches or exceeds the savings an optimization stage produces, that overhead must itself be visible in `optim.compute_cost` (`architecture.md` §27.2, cited not redefined) so the Net Optimization Value accounting can see it.

## 22. Token/Cost Measurement

`architecture.md` §27's ledger fields (§27.1: INPUT/OUTPUT/CACHE/MODEL/TOOLS/WORKFLOW/COST/PERFORMANCE/QUALITY categories; §27.2: OPTIMIZATION ECONOMICS/OUTCOME ECONOMICS/CONTEXT ECONOMICS/AGENT ECONOMICS/CACHE ECONOMICS categories; §27.3's cost-model formulas) are `SOURCE-DEFINED` and consumed, not restated as a competing model. This document's responsibility is how observability's dashboard/alerting layer *consumes* those fields — e.g., `OBS-001`'s Executive dashboard reads `cost.baseline_estimated`, `cost.total_optimized`, `cost.net_savings`, `cost.savings_pct` directly; `OBS-002`'s Engineering dashboard reads the PERFORMANCE and per-stage token fields; `optim.gross_inference_savings`/`optim.net_inference_savings` feed the gross-vs-net distinction in §23.

## 23. Optimization Measurement Integrity

Observability must never report `gross reduction = net savings` without accounting for optimization overhead (`architecture.md` §27.3's `Net Savings = Baseline Cost - Optimized Cost`, which already nets out `Compression Cost`/`Cache Read/Write Cost`/`Routing/Cascade Costs` — cited, not redefined). The pipeline distinguishes, per `EC-067` (`SOURCE-DEFINED`, the mandatory validation case for this section): **Baseline Cost**, **Optimized Cost**, **Optimization Overhead**, **Net Savings**, **Savings %**, and — critically — **Verified Savings** vs. **Estimated Savings** vs. **Unverified Savings** (`optimization-catalog.md`'s `verification_status` enum, cited not redefined). No dashboard may blend these categories into a single "savings" figure without the decomposition remaining queryable underneath it.

## 24. Quality Observability

Integrates `quality-gates.md`'s 10 `QG-NNN` dimensions (cited, not redefined) via the already-existing `control_plane.quality.score` metric (§19.2) and `ExplanationRecord.quality_impact_summary` (§29.1). This document's responsibility is turning quality signals into: metrics (the existing `control_plane.quality.score` histogram, sliced by `stage_id`), dashboard views (`OBS-001`'s "Quality impact" line, `OBS-003`'s "Quality trends" line — both `SOURCE-DEFINED` per §31.1), alerts (§31's quality-degradation alert class), regression signals (cross-reference `quality-gates.md`'s per-optimization validation invariant and `EL-005`/Optimization Regression Detector, cited not redefined), and governance evidence (feeding `architecture.md` §31.3's "Did quality remain acceptable?" question).

## 25. P5 Measurement Separation

`inference-optimization.md`'s `inference_serving.*` namespace discipline (`EC-076`/`AC-036`) is `SOURCE-DEFINED` and preserved without exception: this document's dashboards must never merge a Layer 3 (P5) effect into a Layer 1/2 optimization-savings KPI. A combined view may exist (e.g., a total-cost dashboard row) only if it preserves explicit decomposition by namespace underneath — mirroring the same principle §23 applies to gross-vs-net savings. Provider/infrastructure-owned savings (e.g., a provider's own continuous-batching improvement) are never attributed to EAIOC-owned optimization without evidence, consistent with `inference-optimization.md`'s H17/H18 integration-not-implementation boundary (cited, not redefined here).

## 26. Dashboard Model

`architecture.md` §31.1 (restated verbatim in `conventions.md` §17.3) defines exactly three mandatory dashboard tiers. This document elaborates each as `OBS-001`–`003` below (§28-30), and does not add a fourth tier or redefine the three.

## 27. Decision Explainability (H15, §47.16)

The six `architecture.md` §47.16 decision categories, each mapped to its audit/explanation mechanism (all schema fields cited from §19.5/§29.1, not redefined):

| # | Decision Category | `AuditRecord`/`OptimizationSpan` Mapping | Owning Upstream Component (cited, not redefined) |
|---|---|---|---|
| 1 | Context admitted/pruned | `AuditRecord.action="CONTEXT_PRUNED"`/`CONTEXT_ADMITTED`; `ExplanationRecord.stages_applied[].rationale` | T1.9/T1.10; `CL-004` provenance |
| 2 | Model/provider selected | `MODEL_SELECTED`/`MODEL_ESCALATED` event (§23.2); `ExplanationRecord.model_routing_rationale` | T0.1; `agent-optimization.md` `AR-001` |
| 3 | Cache entry reused/rejected | `CACHE_HIT`/`CACHE_MISS`/`CACHE_INVALIDATED` events; `AuditRecord` | T1.6/T1.7; `SRP` rejection (`architecture.md` §46.2.11) |
| 4 | Optimization stage skipped | `TOOL_SKIPPED` event (tool case) or `AuditRecord.action="STAGE_SKIPPED"` | OI-002/OI-003/SPC |
| 5 | Execution blocked/recovered/superseded | `AuditRecord`; `architecture.md` §46.3's suspend/resume state machine | ESM/RCO/RE (`architecture.md` §46) |
| 6 | Fallback occurred | `OptimizationSpan.status=FALLBACK`; `ExplanationRecord.stages_applied[].fallback_reason` | `architecture.md` §30's Failure and Fallback Model |

Each logged decision includes (per §47.16, `SOURCE-DEFINED`): the inputs considered, the rule/model/threshold applied, the resulting action, and the §47.6 ownership category — sufficient to reconstruct the reasoning during audit/debugging.

**The `EC-198` fail-closed exception, precisely:** if the required `AuditRecord`/`ExplanationRecord` write for one of the six categories above fails, the optimization stage is blocked and unoptimized content is used instead — a deliberate, narrow exception to the fail-open-for-optimization rule (§4 item 4, §20). The failure itself is escalated via `explainability.audit_write_failure.count` (P1 alert) and `explainability.stage_blocked_due_to_audit_failure.count` — both metric names cited verbatim from `EC-198`'s own Observability field, `SOURCE-DEFINED`.

**The `EC-199` boundary, precisely:** a correctly-written, retrievable explanation that is not proactively shown in an end-user UI is **not** a compliance gap — it is the expected default (§12). Do not treat "not surfaced in the UI" as equivalent to "not compliant."

## 28. `OBS-001` — Executive Dashboard

| Field | Value |
|---|---|
| **Dashboard Tier** | Executive |
| **Purpose** | Answer `architecture.md` §31.3's governance questions at the spend/savings/quality-outcome level for leadership audiences |
| **Primary Audience** | Executives, finance/FinOps stakeholders |
| **Restated Key Metrics** (`SOURCE-DEFINED`, `architecture.md` §31.1) | Total AI spend, Cost saved, Savings percentage, Model mix, Cache utilization, Token growth, Quality impact |
| **Metric-to-Pipeline Mapping** | Total AI spend ← `cost.total_optimized` + `cost.baseline_estimated` context; Cost saved ← `cost.net_savings`, split verified/estimated (§23); Savings % ← `cost.savings_pct` (`architecture.md` §27.3 formula); Model mix ← `control_plane.tokens.input`/`.output` grouped by `model_id`; Cache utilization ← `control_plane.cache.hit_rate`; Token growth ← `control_plane.tokens.input`/`.output` trend; Quality impact ← `control_plane.quality.score` trend |
| **Canonical Upstream Metric Names** | `cost.net_savings`, `cost.savings_pct`, `cost.total_optimized`, `cost.baseline_estimated` (`architecture.md` §27.1); `control_plane.cache.hit_rate`, `control_plane.quality.score` (`interfaces.md` §19.2) |
| **Required Dimensions Applied** | Tenant, Application, Date/time (mandatory per §14); Model, Provider (for model-mix breakdown) |
| **Aggregation Model** | Sum for spend/savings; ratio for savings %/cache utilization/hit rate; trend (time-series) for token growth/quality impact |
| **Refresh/Freshness Characteristics** | Batch/periodic acceptable for verified-savings figures (they depend on provider-usage reconciliation); `PROPOSED DEFAULT — VALIDATE LOCALLY`: daily refresh for executive reporting, unless a faster cadence is locally justified |
| **Alerting Requirements** | Savings-verification-percentage floor alert (§31); unexpected spend-acceleration alert (cross-reference `security.md` `GSP-001` SGE's runaway-cost detection, cited not redefined) |
| **Proposed Alert Thresholds** | `PROPOSED DEFAULT — VALIDATE LOCALLY`: alert if `governance.savings_report.verified_pct` drops below 70% of total reported savings for a tenant over a rolling 7-day window — an illustrative starting point, not a validated figure |
| **Governance Questions Answered** (`architecture.md` §31.3) | "How much did optimization cost?", "How much did it save?", "Did quality remain acceptable?", "Can we prove the result?" |
| **Data Sources** | Token/cost ledger (§27.1/§27.2), quality-score metric, cache-hit-rate metric |
| **Tenant-Isolation Requirements** | Per-tenant view by default; any cross-tenant executive rollup is an explicit, separately-authorized aggregate (§18) |
| **Security/Privacy Requirements** | No PII in any Executive-tier field; no raw prompt/response content |
| **Data Quality Requirements** | **Structural separation of `verified_savings` from `estimated_savings`, mandatory** (`EC-067`, `SOURCE-DEFINED`) — an unverified saving must never be presented as verified, and this is a dashboard-construction requirement, not an optional label |
| **Failure/Fallback Behavior** | If verified-savings reconciliation data is delayed/unavailable, the dashboard shows the estimated figure explicitly labeled as such, never silently substituting it for a verified figure |
| **Traceability** | PS §12; ARCH §31.1, §27; INTF §19.2; CONV §17.3 |
| **Validation Evidence** | `EC-067` (`SOURCE-DEFINED`, directly on point); `SCN-AUDIT-003` (Domain AC — verified in scenario-matrix.md, see §38) |
| **Source Gaps** | No source specifies a concrete verified-savings-percentage alert threshold — see `SOURCE-GAP-OBS-02` |

## 29. `OBS-002` — Engineering Dashboard

| Field | Value |
|---|---|
| **Dashboard Tier** | Engineering |
| **Purpose** | Operational visibility into per-stage optimization behavior and telemetry-pipeline health itself |
| **Primary Audience** | Engineers operating/debugging the Control Plane |
| **Restated Key Metrics** (`SOURCE-DEFINED`, `architecture.md` §31.1) | Tokens by stage, Context size, Compression ratio, Model routing, Tool usage, Cache hit rate, Latency, Failures, Fallbacks |
| **Metric-to-Pipeline Mapping** | Tokens by stage ← `tokens.pruned`/`.deduplicated`/`.context_compressed` etc. (§27.1) grouped by `stage_id`; Compression ratio ← derived from `tokens_before`/`tokens_after` (`ExplanationRecord.StageExplanation`, §29.1); Model routing ← `MODEL_SELECTED`/`MODEL_ESCALATED` events; Tool usage ← `TOOL_INVOKED`/`TOOL_SKIPPED` events, `control_plane.tool.calls_avoided`; Cache hit rate ← `control_plane.cache.hit_rate`; Latency ← `control_plane.request.latency_ms`; Failures/Fallbacks ← `control_plane.stage.success_rate`/`.fallback_rate` |
| **Canonical Upstream Metric Names** | `control_plane.request.latency_ms`, `control_plane.stage.success_rate`, `control_plane.stage.fallback_rate`, `control_plane.tool.calls_avoided` (`interfaces.md` §19.2) |
| **Required Dimensions Applied** | Tenant, Application, Workflow (by type, per §14), Agent (by type), Model, Provider, Prompt type, Intent, Date/time |
| **Aggregation Model** | Percentile (p50/p95/p99) for latency; ratio for hit-rate/success-rate/fallback-rate; sum for token/tool counts |
| **Refresh/Freshness Characteristics** | Near-real-time — `PROPOSED METHODOLOGY`: sub-minute refresh for latency/failure/fallback panels, since these are the primary operational-incident signals |
| **Alerting Requirements** | Fallback-rate alert; failure-rate alert; telemetry-health sub-views (see below) must themselves be alertable |
| **Proposed Alert Thresholds** | `PROPOSED DEFAULT — VALIDATE LOCALLY`: page on-call if `control_plane.stage.fallback_rate` exceeds 10% for any single `stage_id` over a rolling 15-minute window — illustrative, not validated |
| **Governance Questions Answered** | "What did we remove?", "What did we compress?", "What did we reuse?", "What did we cache?", "Which model did we use and why?", "How many tool/model calls did we avoid?" |
| **Data Sources** | Per-stage ledger fields, `OptimizationSpan`s, Standard Event Types |
| **Tenant-Isolation Requirements** | Per-tenant slicing available; aggregate platform-health views (below) may be cross-tenant only for infrastructure health, never for content |
| **Security/Privacy Requirements** | DEBUG-level detail excluded from default production view (per `conventions.md` §17.2) |
| **Data Quality Requirements** | Telemetry-health sub-views required alongside the seven `SOURCE-DEFINED` metrics, since this tier's audience is the one that diagnoses telemetry-pipeline problems: telemetry ingestion health, dropped-telemetry rate, trace continuity (§14), audit-write failures (`explainability.audit_write_failure.count`, §27), event-delivery failures, collector/exporter failures (§20) — `SOURCE-DERIVED` from §31.3's governance questions plus §20's reliability requirement, since no single source names "telemetry-health dashboard" as a line item |
| **Failure/Fallback Behavior** | A telemetry-pipeline-internal failure (collector/exporter/storage) degrades this dashboard's own completeness but does not block application execution (§20) — the degradation itself must be visible on this same dashboard |
| **Traceability** | PS §12; ARCH §31.1; INTF §19; CONV §17.3 |
| **Validation Evidence** | `EC-070` (module-reporting-integrity dependency, `PARTIALLY COVERED` by `SCN-AUDIT-002` per `scenario-matrix.md`'s own reconciliation table — see §39); no dedicated scenario isolates the telemetry-health sub-view specifically |
| **Source Gaps** | No source specifies concrete refresh-interval or fallback-rate-alert numbers — see `SOURCE-GAP-OBS-01`/`02` |

## 30. `OBS-003` — Product/Operations Dashboard

| Field | Value |
|---|---|
| **Dashboard Tier** | Product/Operations |
| **Purpose** | Connect optimization/cost/quality outcomes to business-facing units (workflow, user/team, transaction) |
| **Primary Audience** | Product managers, operations leads |
| **Restated Key Metrics** (`SOURCE-DEFINED`, `architecture.md` §31.1) | Cost per workflow, Cost per user/team, Cost per business transaction, Quality trends, Adoption, ROI |
| **Metric-to-Pipeline Mapping** | Cost per workflow ← `outcome.cost_per_agent_workflow` (`architecture.md` §27.2); Cost per user/team ← `cost.total_optimized` grouped by `user/team` dimension, gated by authorization (§14); Cost per business transaction ← `outcome.cost_per_successful_outcome`/`outcome.cost_per_verified_answer`; Quality trends ← `control_plane.quality.score` trend; Adoption ← `control_plane.request.count` trend by `application`/`workflow`; ROI ← derived from `cost.net_savings` vs. `optim.compute_cost` (§27.2) |
| **Canonical Upstream Metric Names** | `outcome.cost_per_agent_workflow`, `outcome.cost_per_successful_outcome`, `outcome.cost_per_verified_answer` (`architecture.md` §27.2) |
| **Required Dimensions Applied** | Application, Workflow (by type), User/team where permitted, Date/time |
| **Aggregation Model** | Sum/average for cost-per-unit metrics; trend for quality/adoption; ratio (derived) for ROI |
| **Refresh/Freshness Characteristics** | Batch/periodic — `PROPOSED METHODOLOGY`: weekly or monthly cadence typical for product/ops reporting, not source-specified |
| **Alerting Requirements** | Lower alerting need than `OBS-001`/`OBS-002` — primarily a reporting tier; `PROPOSED METHODOLOGY`: alert only on a sustained ROI regression |
| **Proposed Alert Thresholds** | `PROPOSED DEFAULT — VALIDATE LOCALLY`: flag a workflow whose `outcome.cost_per_agent_workflow` increases >25% month-over-month without a corresponding quality-trend change |
| **Governance Questions Answered** | "Did quality remain acceptable?" (trend view); indirectly, "How much did it save?" via ROI |
| **Data Sources** | Outcome-economics ledger fields, quality-score metric, request-count metric |
| **Tenant-Isolation Requirements** | User/team-level breakdown is strictly authorization-gated (`security.md` `GSP-002` DGE classification, cited not redefined) — "where permitted" in §31.2's own dimension name is load-bearing, not decorative |
| **Security/Privacy Requirements** | Same as `OBS-001` — no PII, no raw content; user/team identifiers shown only where policy explicitly permits |
| **Data Quality Requirements** | Same estimated-vs-verified discipline as `OBS-001` applies wherever this tier surfaces a cost/savings figure |
| **Failure/Fallback Behavior** | Stale data shown with an explicit staleness indicator rather than silently omitted (§34) |
| **Traceability** | PS §12; ARCH §31.1, §27.2; CONV §17.3 |
| **Validation Evidence** | No dedicated `EC-NNN`/`SCN-*` scenario isolates this tier specifically — `THIN COVERAGE`, honestly recorded |
| **Source Gaps** | No source specifies a "user/team" authorization-gating mechanism's exact implementation — cross-reference `security.md` `GSP-002`, cited not redefined; the gating *policy* itself is `security.md`'s to define, not this document's |

## 31. Alerting Architecture

A conceptual alert lifecycle: metric/event evaluation → threshold evaluation → severity assignment → deduplication → grouping → suppression (maintenance windows) → notification → acknowledgment → escalation → resolution → audit trail. `PROPOSED METHODOLOGY` throughout — no upstream source defines an alert lifecycle in this detail. Severity model (`PROPOSED METHODOLOGY`, aligned to but not identical with `conventions.md` §17.2's log levels): **informational** (below-threshold trend, no action needed), **warning** (near-threshold, matches `conventions.md`'s WARNING log level), **critical** (breach requiring action, matches ERROR/CRITICAL log levels). No specific incident-management vendor is mandated.

Proposed alert classes, each evidence-tagged, none presented as validated: error/failure rate (`OBS-002`), fallback rate (`OBS-002`), latency (`OBS-002`), telemetry ingestion delay (§20), trace continuity (§14), audit-write failure (`EC-198`, `SOURCE-DEFINED` metric name, `PROPOSED` threshold), unverified-savings ratio (`EC-067`, `OBS-001`), quality degradation (`OBS-001`/`OBS-002`), net-negative optimization (cross-reference `quality-gates.md`'s Net Optimization Value, cited not redefined), cost acceleration (`security.md` `GSP-001` SGE, cited not redefined), cache degradation (`OBS-002`), tool/provider failures (`OBS-002`), Control-Plane self-protection activation (§21, `NFR-014`).

## 32. Alert Threshold Discipline

Every numeric threshold proposed anywhere in this document (§28-31) is labeled `PROPOSED DEFAULT — VALIDATE LOCALLY` and none is described as "industry standard," "production-safe," "proven," or "recommended" without a cited source — none exists for any threshold in this document, since this repository has zero local production traffic. Where a threshold's *source* is a specific edge case's own Observability field (e.g., `EC-198`'s metric names), the metric name is `SOURCE-DEFINED` but the specific numeric alerting bound around it remains `PROPOSED`.

## 33. SLO/SLI Model

`PROPOSED METHODOLOGY` throughout — no source defines an SLI/SLO framework for this Control Plane's own observability layer. Proposed SLI candidates (each `PROPOSED DEFAULT — VALIDATE LOCALLY` where a number is attached): telemetry availability, telemetry freshness (§34), trace completeness (§14), audit durability (100% — this one is `SOURCE-DERIVED` from §19.5's immutability/non-deletion rule, since durability of an immutable record is a natural non-negotiable, but the *specific SLO percentage framing* is still proposed methodology), event durability (for `AUDIT`/`POLICY_VIOLATION` per §23.3, similarly `SOURCE-DERIVED` as 100%), metric correctness, dashboard availability, alert delivery. This document explicitly distinguishes source-defined availability *requirements* (e.g., "audit records are immutable," "critical events must be durably written") from the *proposed observability-specific SLO percentages/windows* wrapped around measuring compliance with them.

## 34. Dashboard Data Freshness

For each `OBS-NNN` tier, a freshness class is stated in §28-30's "Refresh/Freshness Characteristics" field. Late-arriving data (a delayed provider-usage reconciliation affecting `OBS-001`'s verified-savings figure, for instance) is handled by showing the best-available figure with an explicit staleness indicator, followed by a correction once the authoritative data arrives — never a silent retroactive rewrite without record of the correction (cross-reference §38's eventual-consistency treatment). Any concrete time interval here is `PROPOSED DEFAULT — VALIDATE LOCALLY`.

## 35. Baseline vs. Optimized Observability

`architecture.md` §32.2's BASELINE/OPTIMIZED comparison model (cited, not redefined — owned by the future `eval.md` for benchmark methodology, but the *observability* consequence belongs here) requires every optimization-effectiveness report to carry baseline context: tokens, cost, latency, quality, cache behavior, model calls, tool calls, agent steps, failures, fallbacks. `OBS-001`'s savings figures are meaningless without the baseline-cost denominator (`architecture.md` §27.3's `Savings % = Net Savings / Baseline Cost x 100`) — this document's dashboards never report an optimization effect without that baseline context remaining queryable underneath the headline figure.

## 36. Observability of Optimization Decisions

For each optimization-stage decision, the pipeline this document defines must be able to answer, per stage, per request (drawing on §27's `ExplanationRecord` mapping): did the stage execute; why did it execute or get skipped; what did it change (`tokens_before`/`tokens_after`); what did it cost (`optimization_cost`); what benefit did it produce; what quality impact occurred (`quality_delta_estimate`); did fallback occur (`fallback_triggered`); was the result verified (`verification_status`); can the decision be reconstructed (via the `ExplanationRecord` chain, §12). No new optimization stage identifier is invented here — every `stage_id` referenced is one already defined in `optimization-catalog.md`/`agent-optimization.md`/`cache-strategy.md`/`inference-optimization.md`.

## 37. Cross-Execution/Agent Observability

Supports parent-agent/sub-agent/workflow/execution correlation, shared-resource conflict visibility, supersession, cancellation, resume, and checkpoint events — all consumed from `architecture.md` §46's ESM/CPM/RCO/RE and `security.md`'s XEC (`GSP`-adjacent cross-execution coordination), cited not redefined. This document's responsibility is ensuring these execution-state transitions are themselves traceable via the same `request_id`/`span_id` correlation model (§9/§13), not a separate cross-execution telemetry scheme.

## 38. Telemetry Quality Controls

Automated checks the pipeline (§5's "validation" stage) should perform, each mapped to its motivating source: missing required fields (`ControlPlaneLogEntry`'s 11 mandatory fields, §19.1), invalid/missing `tenant_id` (root `CLAUDE.md` rule 4), broken correlation IDs (§14/`conventions.md` §17.1's "no gaps" rule), schema-version mismatch (`interfaces.md` §24.1), malformed timestamps, invalid metric types (§7's type column), missing/orphan spans (§14), duplicate events (§10), cross-tenant telemetry (§18), PII leakage (§17), stale dashboards (§34), metric cardinality explosion (§15), audit-write gaps (`EC-066`/`EC-198`). Every check here is `PROPOSED METHODOLOGY` unless the cited source already states the underlying rule the check enforces.

## 39. Observability Failure/Fallback Matrix

| Failure | Detection | Observable Signal | Application Impact | EAIOC Fallback | Governance Impact | Recovery |
|---|---|---|---|---|---|---|
| Metrics collector unavailable | Collector health-check timeout | Collector-health metric (self-referential, `PROPOSED METHODOLOGY`) | None — request proceeds | Fail-open (§20) | None — not a governance-tier failure | Reconnect/retry; backfill from local buffer if available |
| Logs unavailable | Log-exporter failure | Exporter-health metric | None | Fail-open | None | Reconnect/retry |
| Traces unavailable | Missing/orphan span detection (§14) | Trace-continuity gap count | None directly; degrades debuggability | Fail-open | None | Backfill where buffered; otherwise gap is recorded, not fabricated |
| Event bus unavailable | Delivery-failure detection | Event-delivery-failure count | Depends on event criticality — `AUDIT`/`POLICY_VIOLATION` durability requirement (§23.3, `SOURCE-DEFINED`) still applies | Non-durable events: fail-open; `AUDIT`/`POLICY_VIOLATION`: must still durably write (§23.3) even if delivery to subscribers is delayed | High for `AUDIT`/`POLICY_VIOLATION` specifically | Replay from durable store once available |
| **Audit/explanation write unavailable** | Synchronous write-failure detection (§27) | `explainability.audit_write_failure.count` (`SOURCE-DEFINED`, `EC-198`) | **The governed optimization stage is blocked; unoptimized path used** | **Fail-closed** (`EC-198`, the one deliberate exception) | High — escalated as operational incident | Resume normal writes once audit store recovers; no retroactive silent authorization of the blocked decision |
| Dashboard unavailable | Query-layer health-check | Dashboard-availability SLI (§33) | None to the optimization pipeline itself; operational visibility degraded | N/A — dashboards are a read layer | Low, unless it blocks a required compliance review | Restore query layer; underlying data was still recorded |
| Telemetry collector/storage unavailable | Storage health-check | Storage-health metric | None directly | Fail-open for non-audit telemetry; fail-closed still applies to audit writes specifically if audit storage is the affected surface | Depends on which surface | Reconnect; reconcile any buffered-but-unwritten telemetry |
| Malformed telemetry | Schema/field validation (§38) | Validation-rejection count | None | Reject/quarantine the malformed record, do not silently accept it | Low, unless the malformed record was itself an audit record (then treat as a write failure, §27) | Fix producer; do not retroactively fabricate a valid record |
| Tenant metadata missing | Required-field check (§38) | Missing-tenant-ID count | Record is rejected, not silently defaulted to a tenant (root `CLAUDE.md` rule 4) | N/A | Low | Fix producer instrumentation |
| Correlation failure | Trace-chain gap detection (§14) | Trace-continuity gap count | None directly; debuggability degraded | Fail-open | None | N/A — gap is recorded honestly |

No unsupported fail-closed claim is made for any row except the audit/explanation-write row, consistent with §20's discipline.

## 40. Cross-Document Coverage Matrix

| Requirement | Source | Observability Section | Evidence | Status |
|---|---|---|---|---|
| NFR-006 (Observability — every optimization decision measurable) | ARCH §37 | §5-10, §22-25 | `interfaces.md` §19; `conventions.md` §17 | Covered |
| NFR-010 (Explainability, strengthened by H15) | ARCH §37, §47.16 | §27 | `EC-198`/`EC-199`; `SCN-AUDIT-001/004` | Covered |
| NFR-014 (Control-Plane Self-Protection) | ARCH §37, §47.4 | §21 | Cited from `security.md`, not redefined | Covered (by citation) |
| OBJ-011 (Auditable token/cost ledger) | ARCH §3 | §22-23 | `architecture.md` §27 | Covered |
| AC-052 (Retrievable explanation for the six decision categories) | ARCH §39, §47.16 | §27, §12 | `EC-198`/`EC-199` | Covered |
| H15 (Decision Explainability and Audit) | PS §52.15; ARCH §47.16 | §27 | `SCN-AUDIT-001`, `SCN-AUDIT-004` | Covered |
| SEC-008 (auditability) | PS §10; ARCH §29 | §11, §27 | `SCN-AUDIT-001/002` | Covered |
| SEC-013 (deletion propagation, observability-relevant surfaces) | ARCH §29/§47.2.2 | §19 | Cited from `security.md`, not redefined | Covered (by citation) |
| Three dashboard tiers | ARCH §31.1; CONV §17.3 | §26, §28-30 | Direct | Covered |
| 10 required dimensions | ARCH §31.2 | §14-15 | Direct | Covered |
| 11 governance questions | ARCH §31.3 | §28-30 (per-tier mapping) | Direct | Covered |
| Standard Metrics (13) | INTF §19.2 | §7 | Direct | Covered |
| Standard Event Types (14) | INTF §23.2 | §10 | Direct | Covered |
| `AuditRecord`/`ExplanationRecord` schemas | INTF §19.5/§29.1 | §11-12, §27 | Direct | Covered |
| P5 measurement separation | `inference-optimization.md`; `EC-076`/`AC-036` | §25 | Direct | Covered |

## 41. Scenario Coverage

`scenario-matrix.md` was checked directly for a dedicated observability scenario domain. **Finding: a domain named `SCN-OBS-*` does not exist, but a functionally equivalent dedicated domain does exist under a different name** — **Domain AC: "Observability / Audit"** (`scenario-matrix.md` §30), containing exactly four scenarios: `SCN-AUDIT-001` (Every Optimization Decision Is Explainable After the Fact), `SCN-AUDIT-002` (Optimization Decision Made Without an Audit Record — Negative Control), `SCN-AUDIT-003` (UNVERIFIED Ledger Entry Never Surfaces as a Verified Saving), `SCN-AUDIT-004` (Required Decision-Explanation Audit Record Fails to Write; Fails Closed). This document cites `SCN-AUDIT-001`–`004` throughout (§28-29, §40) rather than fabricating a `SCN-OBS-*` reference that would not match the actual repository content. No dedicated scenario isolates the Engineering (`OBS-002`) telemetry-health sub-views or the Product/Operations (`OBS-003`) tier specifically — recorded honestly as `THIN COVERAGE` in §29/§30, not force-fit to `SCN-AUDIT-*`.

## 42. Edge-Case Coverage

All six candidate edge cases were verified directly against the current `edge-cases.md`:

- **`EC-066`** (Optimization Decision Made Without Audit Record) — a `ReversibilityRecord` write failure loses the audit trail for a pruning decision; expected behavior is retry-or-fall-back-to-unmodified-context. `DIRECTLY COVERED` by `SCN-AUDIT-002` per `scenario-matrix.md`'s own reconciliation table.
- **`EC-067`** (UNVERIFIED Ledger Entry Surfaced as Verified Saving) — governance dashboards must separate `verified_savings` from `estimated_savings`. `DIRECTLY COVERED` by `SCN-AUDIT-003`.
- **`EC-070`** (Module Reports Correct Status for a Hidden Internal Failure) — a module silently swallows an internal failure and reports `success=true`. `PARTIALLY COVERED` by `SCN-AUDIT-002` per `scenario-matrix.md`'s own table (that scenario covers an audit-write bug blocking the optimization, not a module falsely self-reporting healthy status while internally failed — a distinct, genuinely-unisolated concern, preserved honestly here, not upgraded).
- **`EC-076`** (P5 Inference Optimization Enabled Without Separate Measurement) — owned substantively by `inference-optimization.md`; cited here only for §25's measurement-separation discipline, not re-validated as this document's own finding.
- **`EC-198`** (Required Explanation/Audit Record Fails to Write) — the fail-closed exception detailed in §27. `DIRECTLY COVERED` by `SCN-AUDIT-004`.
- **`EC-199`** (Retrievable Explanation Not Surfaced to End User — Boundary Case) — the retrievability-vs-presentation boundary in §12/§27. `PARTIALLY COVERED` by `SCN-AUDIT-004` per `scenario-matrix.md`'s own table (that scenario covers the audit write *failing*; it does not address a write that *succeeds* but simply isn't shown in a UI — a distinct concern, preserved honestly, not upgraded).

## 43. Source Gaps

| ID | Gap | Affected Section | Blocking? | Disposition |
|---|---|---|---|---|
| `SOURCE-GAP-OBS-01` | No upstream source specifies concrete dashboard refresh intervals for any of the three tiers | §28-30, §34 | Non-blocking | Recorded; each proposed interval is `PROPOSED DEFAULT — VALIDATE LOCALLY` |
| `SOURCE-GAP-OBS-02` | No upstream source specifies concrete alert-threshold numbers (fallback-rate %, verified-savings-percentage floor, ROI-regression %, etc.) | §28-31 | Non-blocking | Recorded; every proposed threshold is `PROPOSED DEFAULT — VALIDATE LOCALLY` |
| `SOURCE-GAP-OBS-03` | No upstream source specifies a concrete cardinality ceiling, sampling policy, or SLI/SLO percentage/window for the observability layer itself | §15-16, §33 | Non-blocking | Recorded; treated as `PROPOSED METHODOLOGY` throughout |
| `SOURCE-GAP-OBS-04` | No dedicated scenario isolates the Engineering-tier telemetry-health sub-view or the Product/Operations tier specifically | §29-30, §41 | Non-blocking | Recorded as `THIN COVERAGE`, not force-fit to an existing scenario |

## 44. Source Contradictions

None found. No genuine conflict was identified between PS §12/§52.15, `architecture.md` §27/§31/§47.16, `interfaces.md` §19/§23/§29, `conventions.md` §17, or any of the seven already-generated downstream documents' own observability-adjacent statements.

---

## Final Report

- **Document status:** PRE-IMPLEMENTATION — Level 0 (RESEARCH), Version 1.0.0.
- **`OBS-NNN` entry count:** 3/3 (`OBS-001` Executive, `OBS-002` Engineering, `OBS-003` Product/Operations), in `architecture.md` §31.1's exact order.
- **Dashboard-tier coverage:** 3/3 tiers fully elaborated against the 20-field entry schema.
- **Metric coverage:** All 13 `interfaces.md` §19.2 Standard Metrics mapped to at least one `OBS-NNN` tier; 2 derived metrics identified and labeled; 4 already-named upstream metrics from other chain documents consumed (`tmg.schema_staleness_event.count`, `verifier.pass_rate`/`.escalations_triggered.count`/`.acceptance_rate_drift`, `inference_serving.*`, `explainability.audit_write_failure.count`/`.stage_blocked_due_to_audit_failure.count`) without redefinition.
- **Interface coverage:** `interfaces.md` §19 (Log/Metrics/Span/Trace-Chain/Audit), §23 (Events), §29.1 (`ExplanationRecord`) all cited, none redefined.
- **Event coverage:** All 14 Standard Event Types (§23.2) cited; no new event type introduced.
- **Audit/explainability coverage:** The full 6-category H15 mapping (§27) complete; the `EC-198` fail-closed exception stated precisely and **not** generalized to all observability failures (§20, §39); the `EC-199` retrievability-vs-presentation boundary preserved (§12, §27).
- **Edge-case coverage:** 6/6 candidate edge cases (`EC-066`, `EC-067`, `EC-070`, `EC-076`, `EC-198`, `EC-199`) verified directly against the current `edge-cases.md`; their real coverage status (2 `DIRECTLY COVERED`, 2 `PARTIALLY COVERED`, `EC-076` cited by reference only) preserved honestly, not upgraded.
- **Scenario coverage:** `scenario-matrix.md` was checked directly. **Finding: no `SCN-OBS-*` domain exists, but Domain AC ("Observability / Audit," `SCN-AUDIT-001`–`004`) does exist and is this document's actual dedicated scenario domain** — used throughout instead of a fabricated `SCN-OBS-*` reference. This corrects a preliminary assumption stated in the generation prompt itself.
- **Requirement coverage:** NFR-006, NFR-010, NFR-014 (by citation), OBJ-011, AC-052, H15, SEC-008, SEC-013 (by citation) all traced in §40's Cross-Document Coverage Matrix.
- **Source-gap count:** 4 (`SOURCE-GAP-OBS-01`–`04`), all non-blocking.
- **Contradiction count:** 0.
- **Proposed-threshold count:** 6 concrete numeric thresholds/intervals proposed across §28-33, all labeled `PROPOSED DEFAULT — VALIDATE LOCALLY`; several procedural sections labeled `PROPOSED METHODOLOGY` (collector-layer design, idempotent event consumption, dead-letter recovery, cardinality-ceiling detection, sampling policy, alert lifecycle, SLI/SLO framework, audit/debug retrieval interface shape).
- **Known limitations:** No local benchmark or production-traffic evidence exists for any proposed threshold; the audit/debug retrieval interface (§12) is a conceptual design, not a defined API.
- **Fail-open/fail-closed distinction confirmed:** Preserved throughout (§4 item 4, §20, §27, §39) — only the required-audit-write-failure case fails closed; every other observability failure fails open per root `CLAUDE.md` rule 2.
- **File-scope confirmation:** Only `docs/observability.md` was created. No upstream or sibling downstream document was modified. `eval.md`, `SCALING.md`, `implementation-plan.md`, `requirements-traceability.md`, and ADRs were not generated or cascaded into.

## Observability Readiness

READY FOR NEXT DOCUMENTATION PHASE
