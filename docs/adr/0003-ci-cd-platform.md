# ADR-0003: CI/CD Platform and Pipeline Tooling

| Field | Value |
|---|---|
| ADR ID | `ADR-0003` |
| EAIOC ADR Set | `EAIOC-ADR-001` |
| Status | `PROPOSED` |
| Phase | Level 0 — PRE-IMPLEMENTATION |
| Related Decision | `IMPL-DEC-03` |
| Origin | `implementation-plan.md` §24/§25, item 3 |
| Decision Type | Infrastructure |
| Blocking | No |

## Status

`PROPOSED`. No CI/CD platform is named or endorsed by any source document; root `CLAUDE.md` explicitly warns against inventing build/lint/test commands or assuming a framework.

## Context

`implementation-plan.md` §16 already derives a concrete, `SOURCE-DERIVED` gate progression a capability must pass before merge/promotion: (1) static/contract validation against `interfaces.md`'s schemas, (2) unit tests, (3) security/governance tests (`security.md`-cited), (4) quality tests (`quality-gates.md`-cited), (5) scenario tests (`scenario-matrix.md`-cited), (6) regression/evaluation run (`eval.md`'s `EL-005` Regression Detector), (7) capacity/deployment-mode check (`SCALING.md`'s per-deployment-mode profiles, `architecture.md` §33 Modes A–F), (8) Capability Gate promotion (§18). §16 explicitly states this progression names no platform: `PROPOSED IMPLEMENTATION throughout — no source names a specific CI platform, and none is invented here`. `implementation-plan.md` §24 (`IMPL-DEC-03`) leaves the platform open; §25 lists it as ADR candidate 3.

## Problem Statement

What CI/CD platform and pipeline tooling can execute `implementation-plan.md` §16's 8-step gate progression for every capability, across the deployment-mode-specific variation `SCALING.md` introduces at step 7?

## Decision Drivers / Constraints

- **The gate progression itself is fixed and must not be redesigned** (`implementation-plan.md` §16) — this ADR selects tooling to execute an already-specified sequence, it does not reorder or reduce it.
- **Step 7 is not identical for every capability** (`implementation-plan.md` §16, citing `SCALING.md`'s per-deployment-mode profiles: Mode B CLI is single-invocation, Mode C API/middleware is sustained-load, Mode F CI/CD itself has burst-not-sustained characteristics) — the chosen tooling must support per-capability/per-deployment-mode pipeline variation, not a single uniform pipeline shape.
- **Provider neutrality must not leak into CI configuration** (root `CLAUDE.md` rule 1) — no pipeline step may hard-code a specific LLM provider; any provider-specific test fixture must go through the same `providers/adapters/` abstraction ADR-0005 addresses.
- **No source specifies a language or framework** (root `CLAUDE.md`, Repository status section) — the CI platform choice cannot be scoped to a specific language runtime's ecosystem today, since none has been chosen.

## Source Basis

`implementation-plan.md` §16, §24 (`IMPL-DEC-03`); `quality-gates.md` (cited for step 4); `security.md` (cited for step 3); `eval.md`'s `EL-005` (cited for step 6); `SCALING.md`'s per-deployment-mode profiles (cited for step 7); root `CLAUDE.md`.

## Options Considered

- **Option A — Hosted CI/CD tightly integrated with the source-control platform** (e.g., a GitHub/GitLab-native pipeline system). Structural fit: lowest integration friction for PR-gated promotion, natural fit for step 1 (static/contract validation) as a required-check. Trade-off: portability across source-control platforms is lower if the organization ever migrates hosting.
- **Option B — Standalone/self-hosted CI/CD system** (e.g., a dedicated build-server product). Structural fit: full control over runner environment, useful if step 7's deployment-mode-specific checks need bespoke infrastructure access. Trade-off: higher operational ownership burden (patching, scaling the runner fleet).
- **Option C — Cloud-provider-native pipeline service.** Structural fit: can integrate directly with the same cloud environment a deployment target runs in, simplifying step 7's capacity/deployment-mode check. Trade-off: risks coupling the build pipeline to a single cloud provider, which is a different (but related) concern from the provider-neutrality rule governing LLM providers specifically.

No specific product's marketing claims (speed, cost, uptime) are asserted as fact for any option.

## Option Analysis

Because step 7 (capacity/deployment-mode check) is the only step in the progression with genuinely infrastructure-dependent behavior, the deciding factor between these options is likely to be how directly each integrates with whatever deployment infrastructure is eventually chosen for `SCALING.md`'s deployment modes — a decision this ADR does not make.

## Decision

No decision made — deferred pending local evaluation of pipeline-tooling fit against the 8-step progression, including compatibility with whatever deployment infrastructure is eventually chosen. This ADR has compatibility dependencies on the eventual implementation environment, but the corpus does not establish a strict sequencing dependency requiring this ADR to be decided only after ADR-0001/0002/0004/0005.

## Rationale

Selecting a CI platform today, with no language/framework/deployment-infrastructure decision yet made, would require guessing at compatibility constraints that do not yet exist — a clear case of root `CLAUDE.md`'s "do not invent build, lint, or test commands" guidance applying at the tooling-selection level, not just the command level.

## Consequences

### Positive Consequences

The 8-step gate progression is tooling-agnostic by construction (`implementation-plan.md` §16 derives it purely from existing requirements, not from any platform's feature set), so deferring this decision blocks nothing else in the design.

### Negative Consequences

None yet observable.

### Risks

Delaying this decision until late in implementation risks discovering a step (most likely step 7) that the chosen platform cannot easily support, requiring pipeline rework. Mitigated by evaluating pipeline-tooling fit against step 7's deployment-mode-specific variation before acceptance, rather than assuming compatibility without checking it.

### Operational Consequences

`No material impact identified from the authoritative corpus`.

## Rejected / Deferred Alternatives

None rejected.

## Requirements Traceability

`NFR-013` (Testability — "every optimization stage must be independently testable," cited); `AC-015` (`architecture.md` §32.3, benchmark/validation matrix, cited via `implementation-plan.md` §16 step 6).

## Architecture Traceability

`architecture.md` §32–§35 (Benchmarking Framework, Rollout Strategy, Regression Testing — cited); `architecture.md` §33 (Enterprise Deployment Model Modes A–F, cited via `SCALING.md`).

## Interface / Contract Impact

None.

## Security / Governance Impact

Whatever platform is chosen must be capable of running step 3's security/governance tests as a required, non-bypassable gate (`security.md`'s fail-closed-on-governance discipline, cited) — CI configuration must not allow a capability to merge with this step skipped.

## Observability / Evaluation Impact

Pipeline run results (pass/fail per step) should themselves be observable consistently with `observability.md`'s telemetry model (cited) — this ADR does not define a new telemetry schema for CI results.

## Scaling Impact

Directly tied to `SCALING.md`'s per-deployment-mode profiles (cited) at step 7; no scaling requirement is introduced by this ADR beyond executing that existing check.

## Implementation Impact

Every capability's Capability Gate (`implementation-plan.md` §18) implicitly depends on this ADR's eventual resolution to actually execute, though no capability's *design* depends on it — the gate model is defined independent of tooling.

## Migration / Rollback Considerations

Not applicable yet — no platform selected.

## Source Gaps / Assumptions

No existing `SOURCE-GAP-*` covers this question directly (it is an `IMPL-DEC` item, not a documentation gap). **Assumption made for this ADR's analysis only:** that a single CI/CD platform can execute all 8 steps; a future acceptance decision could instead split tooling (e.g., one system for steps 1–2, another for step 7) if evaluation shows that's a better fit — not assumed here as the final shape.

## Open Questions

Should CI/CD platform selection wait entirely until after the language/framework and deployment-infrastructure decisions, or can a provisional choice be made now and revisited? This ADR takes no position.

## Related Documents / ADRs

`implementation-plan.md` §16, §24; ADR-0002 (execution-truth store, ADR-0004 (telemetry backend), and ADR-0005 (provider-adapter pattern) are all downstream-impacted by whatever infrastructure environment the eventual CI/CD choice assumes — see cross-ADR review in `0000-index.md`.

## Validation / Acceptance Criteria

A pipeline configuration demonstrating all 8 steps executable, including at least one deployment-mode-specific variant of step 7, before any status change to `ACCEPTED`.
