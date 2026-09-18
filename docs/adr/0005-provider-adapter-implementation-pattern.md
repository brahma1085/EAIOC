# ADR-0005: Provider-Adapter Implementation Pattern

| Field | Value |
|---|---|
| ADR ID | `ADR-0005` |
| EAIOC ADR Set | `EAIOC-ADR-001` |
| Status | `PROPOSED` |
| Phase | Level 0 — PRE-IMPLEMENTATION |
| Related Decision | N/A (no `IMPL-DEC` row — origin is root `CLAUDE.md` rule 1) |
| Origin | `implementation-plan.md` §25, item 5 |
| Decision Type | Architectural / Implementation |
| Blocking | No |

## Status

`PROPOSED`. This ADR concerns an implementation *pattern*, not a technology purchase decision, but no source document specifies the concrete internal structure of `providers/adapters/` beyond the constraint that it must exist and be opaque to core code.

## Context

Root `CLAUDE.md` rule 1 (Provider neutrality) states: "Core code must never import or hard-code a specific LLM provider, model name, agent framework, MCP library, or embedding model. All of that goes through adapter interfaces (`providers/adapters/`); provider-specific data goes in an opaque `provider_hints` map, never a first-class schema field." `architecture.md` §5.3 (Common Representation Layer) and §5.2 (five Integration Modes: API/Gateway proxy, SDK/middleware, MCP-based services, agent-harness integration, provider/platform enterprise integrations) establish *what* must be normalized, and `provider-matrix.md`'s FTR (Feasibility Tier Registry, §9) establishes that integration depth varies by platform. None of these specifies *how* `providers/adapters/` is concretely structured internally. `implementation-plan.md` §25 lists this as ADR candidate 5, citing root `CLAUDE.md` rule 1 directly (there is no `IMPL-DEC-NN` row for it, since it was identified as an architectural pattern question rather than a technology-selection gap in the Implementation Decision Register).

## Problem Statement

What internal implementation pattern for `providers/adapters/` satisfies provider neutrality (rule 1) across all five integration modes (`architecture.md` §5.2) and the FTR's declared feasibility tiers (`provider-matrix.md` §9), while keeping `provider_hints` genuinely opaque to core code?

## Decision Drivers / Constraints

- **Provider-specific fields are structurally prohibited from the core schema** (`interfaces.md` line 2943: "Provider field in core schema — PROHIBITED; Use `provider_hints: map<string, any>`") — the adapter pattern must make it structurally awkward, not merely discouraged by convention, for a provider-specific field to leak into a first-class schema field.
- **Five distinct integration modes must all be supported by a common core** (`architecture.md` §5.1: "The product shall use a common optimization core with integration adapters, not a separate optimizer for every agent product"; §5.2's five modes) — the pattern cannot be one adapter shape per mode with duplicated core logic; it must be one core against N adapters.
- **Integration feasibility tiers are not uniform across platforms** (`architecture.md` §5.2's `[!IMPORTANT]` H08 note: every coding-agent integration must declare a tier — deep/native, gateway/interception, plugin/extension, protocol/tool-level, or advisory/observability-only — and `DA-001`–`DA-025` module applicability is bounded by the declared tier for that platform) — the adapter pattern must be able to express "this adapter supports a reduced module set" rather than assuming uniform capability.
- **`CAR` (Capability/Availability Resolver, `architecture.md` §46.1) already owns runtime capability resolution** for `T0.1` Model Router — the adapter pattern must integrate with `CAR`'s existing resolution role rather than duplicating it.
- **Static vs. runtime provider facts are distinguished** (`provider-matrix.md` §2, cited) — the adapter pattern should not conflate a provider's static capability declaration with a runtime-verified fact, consistent with `provider-matrix.md`'s own anti-fabrication discipline (§3).

## Source Basis

Root `CLAUDE.md` rule 1; `architecture.md` §5.1–§5.3, §46.1 (`CAR`), §47.10 (FTR tier model, cited); `interfaces.md` (`provider_hints` schema rule); `provider-matrix.md` §1–§9; `inference-optimization.md` (P5 boundary, cited — provider-serving-layer awareness is Control-Plane-owned per H17/H18, never implemented by the adapter).

## Options Considered

- **Option A — One adapter interface, one implementation class per provider, registered in a lookup table.** Structural fit: simplest mental model, directly maps "provider" to "adapter instance." Trade-off: does not natively express the FTR's per-platform feasibility-tier variation without an additional capability-declaration layer per adapter.
- **Option B — Adapter interface plus a separate capability-declaration object per adapter instance** (the adapter declares its FTR tier and supported `DA-NNN` module subset at registration time, consumed by `CAR`). Structural fit: directly satisfies the feasibility-tier constraint and integrates cleanly with `CAR`'s existing resolution role. Trade-off: more upfront design work than Option A's simpler lookup table.
- **Option C — Adapter pattern built around the five Integration Modes as the primary abstraction axis** (one adapter family per mode — gateway-proxy adapters, SDK adapters, MCP-service adapters, etc.), with providers as a secondary dimension within each mode. Structural fit: may better reflect that integration *mode* often constrains capability more than the specific provider does. Trade-off: providers that support multiple modes (e.g., both API-gateway and SDK integration) would need adapter instances in more than one family, risking duplicated provider-specific logic across families.

No provider or vendor is named as a required or preferred adapter target by this ADR.

## Option Analysis

Option B's explicit capability-declaration layer has a direct structural correspondence to the FTR tier model and `CAR`'s resolution role — but this ADR does not accept it, since no local implementation attempt exists to validate that the declaration layer's design is actually workable in practice.

## Decision

No decision made — deferred pending a prototype against at least two genuinely different integration modes (e.g., one gateway-proxy-mode provider and one SDK-mode provider) to verify the chosen pattern actually keeps `provider_hints` opaque and core logic provider-free in practice, not just on paper.

## Rationale

Unlike ADRs 1–4, this is a pattern question rather than a technology-selection question, so "no local benchmark evidence" is a less direct concern — but root `CLAUDE.md` rule 1 is one of the nine non-negotiable structural rules in this repository, and getting the adapter pattern wrong risks violating it silently later (a provider-specific field creeping into a core schema because the adapter boundary was drawn in the wrong place). Deferring until a two-mode prototype exists is the safer discipline.

## Consequences

### Positive Consequences

`architecture.md` §5's Common Representation Layer and the FTR tier model are both already fully specified independent of the adapter's internal pattern, so this decision can be deferred without blocking other design work.

### Negative Consequences

None yet observable.

### Risks

Choosing a pattern that doesn't cleanly express per-platform feasibility-tier variation (a risk specific to Option A) could force retrofitting a capability-declaration layer later, after adapters already exist — a costly rework. Mitigated by evaluating Option B specifically against this risk before acceptance.

### Operational Consequences

`No material impact identified from the authoritative corpus`.

## Rejected / Deferred Alternatives

None formally rejected. Option B has a direct structural correspondence to the cited FTR/CAR model; this remains an evaluation observation rather than an accepted decision.

## Requirements Traceability

`NFR-005` (Provider Independence, cited); `OBJ-013` (Multi-provider, cited); `H08` (Coding-Agent Integration Feasibility Tiers, cited).

| Source concept | Relevance to ADR-0005 |
|---|---|
| Provider-neutrality rule (root `CLAUDE.md` rule 1) | Prevents provider-specific leakage into the common optimization core; the adapter pattern is the structural mechanism that enforces this rule at implementation time. |
| `NFR-005` (`architecture.md` §37, "the core framework should not be tied to a single provider") | Establishes provider independence/neutrality as a mandatory non-functional requirement, not a stylistic preference — verified `FULLY TRACED` in `requirements-traceability.md`. |
| `OBJ-013` (`architecture.md` §3/§44.1, multi-provider abstraction) | Establishes multi-provider support as an objective; `provider-matrix.md` is its primary downstream elaboration — verified `FULLY TRACED` in `requirements-traceability.md`. |
| `H08` (`architecture.md` §52.8/conventions.md §23.1, Coding-Agent Integration Feasibility Tiers) | Establishes the FTR applicability boundary this ADR's Option B is built around; `requirements-traceability.md` records `H08` as `PARTIALLY TRACED — BLOCKED BY SOURCE GAP` (the FTR mechanism itself is deferred forward, cited not redefined here). |
| `architecture.md` §46.1 (`CAR` — Capability/Availability Resolver, §46.2.10) | Establishes `CAR`'s ownership of runtime capability resolution for `T0.1` Model Router; the adapter pattern must integrate with this existing role rather than duplicating it. |

## Architecture Traceability

`architecture.md` §5.1–§5.3, §46.1 (`CAR`), §47.10 (FTR tiers) — all cited, none redefined.

## Interface / Contract Impact

None directly. The `provider_hints: map<string, any>` field and the prohibition on provider-specific core-schema fields (`interfaces.md`, cited) are unaffected by which internal adapter pattern is eventually chosen.

## Security / Governance Impact

The adapter pattern must not become a channel for bypassing `TMG` (Tool/MCP Trust Gate, `security.md`, cited) — any MCP-based integration mode (`architecture.md` §5.2, mode 3) routes through the same governance checks as any other tool/MCP surface, regardless of adapter pattern.

## Observability / Evaluation Impact

Adapter-level telemetry should use the existing `provider_id`-labeled metrics (`interfaces.md` §19.2, cited) rather than introducing a competing per-adapter metric namespace.

## Scaling Impact

Cited from `SCALING.md`'s `SC-005` (provider/model concurrency capacity surface, `SOURCE-DERIVED`) — the adapter pattern's concurrency model (how many in-flight calls per adapter instance) is an input to that capacity model, not redefined here.

## Implementation Impact

`optimization-catalog.md`'s `DA-NNN` module applicability-by-feasibility-tier discipline (cited) depends on whichever capability-declaration mechanism this ADR eventually accepts.

## Migration / Rollback Considerations

Not applicable yet — no pattern accepted.

## Source Gaps / Assumptions

No existing `SOURCE-GAP-*` covers this pattern question directly. `SOURCE-GAP-PROVMTX-01`–`03` (no authoritative provider/model roster, unknown per-platform `reachable_modules`, cited) are related but distinct — they concern which providers/platforms exist and what they support, not how the adapter code implementing support for any of them should be structured. **Assumption made for this ADR's analysis only:** that a single adapter pattern can serve all five integration modes; Option C's mode-first structure is evidence this assumption itself warrants testing before acceptance.

## Open Questions

Should feasibility-tier declaration (Option B) be mandatory for every adapter from day one, or can it be added incrementally starting with a simpler Option-A-style lookup table for the first one or two providers integrated? Not answered here.

## Related Documents / ADRs

Root `CLAUDE.md` rule 1; `architecture.md` §5, §46.1, §47.10; `provider-matrix.md`; `inference-optimization.md` (P5 boundary — this ADR's pattern must not extend into inference-serving implementation, per H17/H18, cited).

## Validation / Acceptance Criteria

A prototype implementing the chosen pattern for at least two providers spanning two different integration modes, demonstrating zero provider-specific fields in any core schema and correct FTR-tier-bounded module applicability, before any status change to `ACCEPTED`.
