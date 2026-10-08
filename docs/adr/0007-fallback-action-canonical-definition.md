# ADR-0007: `FallbackAction` Canonical Definition

| Field | Value |
|---|---|
| ADR ID | `ADR-0007` |
| EAIOC ADR Set | `EAIOC-ADR-001` |
| Status | `ACCEPTED` |
| Phase | Level 0 — PRE-IMPLEMENTATION (RESEARCH) |
| Related Decision | HDR-3, EAIOC P0→P1 Architecture Readiness Review (2026-10-08) |
| Origin | `control_plane/core/CoreFoundation.md` §6, `CORE-GAP-04` (discovered during `REM-P0.1.A-01`, 2026-09-23) |
| Decision Type | Documentation / source-contract disambiguation — **not** an infrastructure or technology selection |
| Blocking | No (both P0 fields that would carry this type, `OptimizationPlan.fallback_strategy` and `OptimizationStage.fallback_on_failure`, remain deferred in P0 for the independent reason `CORE-GAP-03`) |

## Status

`ACCEPTED`. Unlike `ADR-0001`–`ADR-0006` (all `PROPOSED`, each selecting an unvalidated infrastructure/technology substrate pending local benchmark evidence), this ADR resolves a pure documentation contradiction between two already-fully-specified definitions within `interfaces.md` itself. No technology selection, no infrastructure evaluation, and no local benchmark evidence is required to decide which of two existing, equally-authoritative text blocks is canonical — that decision is made here, by explicit human authorization (HDR-3), and recorded as accepted.

## Context

`docs/interfaces.md` defines a type named `FallbackAction` in two places with conflicting content:

- **§17 (Quality Validation Interface), as a field of `ValidationResult`:**
  ```
  FallbackAction {
    action:     enum { RESTORE_ORIGINAL, INCREASE_CONTEXT, INCREASE_REASONING,
                       ESCALATE_MODEL, DISABLE_OPTIMIZATION, RETRY }
    parameters: map<string, any>
  }
  ```
- **§22.1 (Fallback Strategy), the type `FallbackStrategy` is built from:**
  ```
  FallbackStrategy {
    strategy_id:     string
    default_action:  FallbackAction
    stage_fallbacks: map<string, FallbackAction>
    max_fallbacks:   integer
    alert_on_fallback: boolean
  }

  FallbackAction {
    action:     enum { RESTORE_ORIGINAL, INCREASE_CONTEXT, INCREASE_REASONING,
                       ESCALATE_MODEL, DISABLE_STAGE, RETRY_WITH_PARAMS, REJECT }
    parameters: map<string, any>
    max_cost:   float | null
  }
  ```

The two blocks share four enum values (`RESTORE_ORIGINAL`, `INCREASE_CONTEXT`, `INCREASE_REASONING`, `ESCALATE_MODEL`) but diverge on the rest (`DISABLE_OPTIMIZATION`/`RETRY` vs. `DISABLE_STAGE`/`RETRY_WITH_PARAMS`/`REJECT`), and only §22.1's block carries a `max_cost` field. `quality-gates.md` cites the §17 variant when discussing `ValidationResult.recommended_fallback`. `OptimizationPlan.fallback_strategy` (§3.2) and `OptimizationStage.fallback_on_failure` (§3.2) both reference `FallbackAction` through `FallbackStrategy`, i.e., through §22.1's block. No source document states which block `OptimizationStage.fallback_on_failure` is meant to use.

This was discovered during `REM-P0.1.A-01` (the shared `core/` foundation design note, `control_plane/core/CoreFoundation.md` §6), recorded as `CORE-GAP-04`, and deliberately not resolved there — `OptimizationPlan` was realized as explicitly incomplete, deferring both `fallback_strategy` and `escalation_policy` (the latter for the separate reason `CORE-GAP-03`), rather than guessing which `FallbackAction` block applied. It surfaced again during the EAIOC P0→P1 Architecture Readiness Review (2026-10-08) as HDR-3, a genuine same-document, same-tier contradiction with no documented precedence rule.

## Problem Statement

Which of the two conflicting `FallbackAction` definitions in `interfaces.md` is canonical, and how should the other be treated, without inventing any semantics beyond what the two existing definitions already state?

## Decision Drivers / Constraints

- **No invention.** The resolution must select between the two existing definitions (or a subset relationship between them), never author a new enum value or field neither block already has.
- **Consumer alignment.** `OptimizationPlan.fallback_strategy` and `OptimizationStage.fallback_on_failure` (§3.2) reach `FallbackAction` through `FallbackStrategy`, which is itself defined in §22.1 alongside its own `FallbackAction`. The type that the dedicated Fallback/Recovery Interface section (§22) both names and consumes is the stronger candidate for canonical status.
- **Completeness.** §22.1's block is a strict superset in information content: it adds `max_cost` (absent from §17's block) and a `REJECT` outcome with no analogue in §17. §17's block adds nothing §22.1 lacks.
- **Minimal disruption to existing citations.** `quality-gates.md` cites the §17 variant for `ValidationResult.recommended_fallback`; the resolution must not silently invalidate that citation, only redirect what it points to.

## Source Basis

`interfaces.md` §17 (`ValidationResult`, `FallbackAction`), §22.1 (`FallbackStrategy`, `FallbackAction`), §3.2 (`OptimizationPlan.fallback_strategy`, `OptimizationStage.fallback_on_failure`); `control_plane/core/CoreFoundation.md` §3.1, §5, §6 (`CORE-GAP-04`, the transitive-closure table); `quality-gates.md` (cites the §17 variant, confirmed by the original discovery note).

## Options Considered

- **Option A — §17's definition is canonical.** Rejected: it is the narrower of the two (no `max_cost`, fewer `action` values), and it is not the type actually consumed by `OptimizationPlan`'s own fields, which reach `FallbackAction` via `FallbackStrategy` in §22.1.
- **Option B — §22.1's definition is canonical.** §22.1 is the dedicated Fallback/Recovery Interface section; its `FallbackAction` is the type `FallbackStrategy` (also defined there) actually uses, and it is a strict superset of §17's block. `ValidationResult.recommended_fallback` is redirected to reference this definition — no information `quality-gates.md` relies on is lost, since every §17 enum value except `DISABLE_OPTIMIZATION`/`RETRY` already exists in §22.1 as `DISABLE_STAGE`/`RETRY_WITH_PARAMS` (closely analogous, not identical — this ADR does not assert they mean exactly the same thing, only that §22.1 is now the sole definition going forward).
- **Option C — Merge both enums into a new unified set.** Rejected per the "no invention" constraint: a merged enum (e.g., keeping both `DISABLE_OPTIMIZATION` and `DISABLE_STAGE` as distinct values) would require deciding whether they are the same concept or two, which neither source definition answers. HDR-3 explicitly disallows inventing semantics beyond what the existing two definitions support.

## Decision

**Option B.** `interfaces.md` §22.1's `FallbackAction` (`action` ∈ `{RESTORE_ORIGINAL, INCREASE_CONTEXT, INCREASE_REASONING, ESCALATE_MODEL, DISABLE_STAGE, RETRY_WITH_PARAMS, REJECT}`, `parameters: map<string, any>`, `max_cost: float | null`) is the sole canonical definition. §17's local `FallbackAction` block is marked deprecated in place (not deleted — retained as historical record of the pre-correction text) and now refers to the §22.1 definition. `ValidationResult.recommended_fallback` (§17) is reinterpreted as referencing the canonical §22.1 type. No enum value, field, or semantic beyond what §22.1 already specified was added.

This decision is **accepted immediately** (not deferred) because it requires no infrastructure evaluation or local benchmark evidence — both candidate definitions already fully exist in the frozen corpus; selecting between them is a documentation-consistency decision, which the governance protocol's human-decision mechanism (HDR) is sufficient to make.

## Rationale

§22.1 is the dedicated section for this concept (Fallback/Recovery Interface) and is the definition `OptimizationPlan`'s own fields actually reach through `FallbackStrategy`, which is defined in the same section. Choosing the section that both names and consumes the type, over a narrower definition that appears only as a field of an unrelated `ValidationResult` type in §17, avoids introducing a new, third interpretation and keeps the resolution traceable to which existing text block is more structurally central to the type's own use.

## Consequences

### Positive Consequences

`CORE-GAP-04` is resolved at source. Any future P1 work on `OptimizationPlan.fallback_strategy`, `OptimizationStage.fallback_on_failure`, or `ValidationResult.recommended_fallback` now has exactly one `FallbackAction` definition to implement against, eliminating a contradiction that would otherwise have had to be re-litigated by whichever implementer reached it first.

### Negative Consequences

`quality-gates.md`'s existing citation of the §17 variant is now citing a deprecated, redirected block rather than the literal text it originally pointed to. This is a citation-currency issue, not a semantic one (see Rejected/Deferred Alternatives) — `quality-gates.md` itself is not edited by this ADR, consistent with the ADR set's established practice of not modifying every document an ADR's selection touches (see `docs/adr/0000-index.md`'s Downstream-Impact Register pattern for the original six ADRs).

### Risks

None identified beyond the citation-currency issue above. This ADR selects between two already-fully-specified, mutually exclusive text blocks; it does not introduce new unproven behavior.

### Operational Consequences

None — `OptimizationPlan.fallback_strategy` and `OptimizationStage.fallback_on_failure` remain deferred in the P0 core foundation (`core/schemas/OptimizationPlan.java`) for the separate, still-open reason `CORE-GAP-03` (`EscalationPolicy` undefined). This ADR removes one of the two reasons those fields are deferred, but does not itself realize them; no P0 code changes as a result of this ADR.

## Rejected / Deferred Alternatives

Option A (§17 canonical) and Option C (merge into a new unified enum) were considered and rejected for the reasons stated above. Neither is deferred for future reconsideration — Option B is the final, accepted resolution of this specific contradiction.

## Requirements Traceability

No specific `OBJ`/`SEC`/`NFR`/`H`/`AC` ID governs this particular contradiction directly; it is a source-internal consistency issue within `interfaces.md`, not a requirements gap. `conventions.md`'s general fail-open/fail-closed and anti-fabrication disciplines (root `CLAUDE.md` rules 2 and 9) are respected by this resolution's "no invention" constraint.

## Architecture Traceability

`architecture.md` does not itself define `FallbackAction` (it is an `interfaces.md`-only contract); no architecture section is redefined by this ADR.

## Interface / Contract Impact

`interfaces.md` §17 and §22.1 are both annotated (not restructured) to record the canonical selection and the deprecation, per the edits accompanying this ADR. No `INTF-NNN` numbering changes; `INTF-041` (`FallbackStrategy`, §22) continues to refer to the same section.

## Security / Governance Impact

None. This ADR does not touch any fail-open/fail-closed, tenant-isolation, or authorization behavior — `OptimizationStage.fallback_on_failure`'s eventual realization (deferred, `CORE-GAP-03`) is unaffected by which `FallbackAction` shape it will eventually carry.

## Observability / Evaluation Impact

None at P0 — no P0 capability emits a `FallbackAction` value; this is purely a forward-looking disambiguation for whichever future unit first realizes `OptimizationPlan.fallback_strategy` or `OptimizationStage.fallback_on_failure`.

## Scaling Impact

None.

## Implementation Impact

Whichever future unit first realizes `OptimizationStage.fallback_on_failure` or `OptimizationPlan.fallback_strategy` (deferred pending `CORE-GAP-03`'s resolution) must use the canonical §22.1 `FallbackAction` shape (including `max_cost`), not the now-deprecated §17 shape. No implementation exists yet to migrate.

## Migration / Rollback Considerations

Not applicable — no code consumes either `FallbackAction` block today. If `CORE-GAP-03` (`EscalationPolicy`) is resolved in a future pass, `OptimizationPlan.fallback_strategy` can be realized directly against this ADR's canonical definition with no further disambiguation needed.

## Source Gaps / Assumptions

**Resolves:** `CORE-GAP-04` (`FallbackAction` dual, contradictory definition), promoted from `control_plane/core/CoreFoundation.md` §6 into `docs/execution-plan.md` §38 by the same governance pass that produced this ADR. **Does not resolve:** `CORE-GAP-03` (`EscalationPolicy` undefined), which independently keeps `OptimizationPlan.fallback_strategy` and `OptimizationStage.fallback_on_failure` deferred in P0 regardless of this ADR. **Assumption:** none beyond the two existing source definitions; no new semantics invented.

## Open Questions

None remaining for this specific contradiction. `CORE-GAP-03` remains a separate open question, not addressed here.

## Related Documents / ADRs

`interfaces.md` §17, §22.1, §3.2; `control_plane/core/CoreFoundation.md` §3.1, §5, §6; `quality-gates.md` (citing §17's now-deprecated block); `docs/execution-plan.md` §38 (`CORE-GAP-04` promotion and resolution record). No relationship to `ADR-0001`–`ADR-0006` (those select unproven infrastructure substrates; this ADR disambiguates existing, fully-specified text).

## Validation / Acceptance Criteria

Already met at acceptance: both candidate definitions pre-existed in the frozen corpus in full; no local benchmark, prototype, or production evidence is required for a documentation-disambiguation decision. Confirmed by direct re-read of both `interfaces.md` blocks before this ADR was drafted.
