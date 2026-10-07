# Capability 4 — Context Policy: Contract & Design Note (EXE-P0.4.A)

**Status:** Design note. No Java code and no new schema are introduced by this sub-phase.
**Source of the row:** `docs/execution-plan.md` §18.7, Sub-phase A row ("Grep `interfaces.md` §20 directly for `OptimizationPolicy`'s exact fields before writing any code; confirm package placement (§9)").
**Precondition check:** Capability 1 (Token Accounting and Cost Ledger) Capability Gate P0.1 is **APPROVED** (human approval recorded 2026-09-23) — the row's stated precondition is satisfied. No other capability gate is a precondition for A.

## 1. What Capability 4 is

Capability 4 defines the budget/policy substrate that later T1-series and downstream consumers read (`execution-plan.md` §18.7, "Objective"). In this P0 slice, its declared downstream consumers are Prompt Assembler (Capability 5) and Output Controls (Capability 6); Capability 1's cost ledger is its own declared dependency, for budget enforcement. Neither Capability 5 nor Capability 6 exists yet, and this sub-phase does not implement anything for them — it only confirms the contract this capability will expose.

There is no dedicated architecture subsection for "Context Policy": `architecture.md` §36 names it only in the P0 item list, and `execution-plan.md` §9 already records this honestly ("this capability's shape is derived, not directly specified"). This sub-phase does not invent one.

## 2. The contract this sub-phase cites (verified against the live file, not assumed)

The A row requires grepping `interfaces.md` §20 directly rather than trusting this document's own citation of it. Reproduced verbatim from `docs/interfaces.md` §20 (`## 20. Policy / Governance Interface`, traceability: PS §10, §23; ARCH §29; SEC-001–010):

### 2.1 `OptimizationPolicy` (`interfaces.md` §20.1)

```
OptimizationPolicy {
  policy_id:       string
  policy_version:  string
  schema_version:  string
  tenant_id:       string
  organization_id: string
  effective_from:  ISO8601 string
  effective_until: ISO8601 string | null

  enabled_stages:      string[]
  disabled_stages:     string[]
  optimization_depth:  enum { MINIMAL, STANDARD, DEEP }
  require_net_savings: boolean

  model_allowlist:  string[]
  model_denylist:   string[]
  default_model_id: string
  cascade_enabled:  boolean

  max_input_tokens:     integer | null
  max_output_tokens:    integer | null
  max_cost_per_request: float | null
  max_monthly_cost:     float | null

  max_e2e_latency_ms:           integer | null
  max_optimization_overhead_ms: integer | null

  min_quality_score:     float
  min_task_success_rate: float
  max_regression_delta:  float

  exact_cache_enabled:           boolean
  semantic_cache_enabled:        boolean
  semantic_similarity_threshold: float
  cache_pii_prohibited:          boolean

  pii_handling:   enum { SCRUB, BLOCK, PASSTHROUGH_WITH_AUDIT }
  data_residency: string[] | null

  compliance_frameworks: string[]
  rollout_pct:           float
  shadow_mode:           boolean

  per_workflow_overrides: map<string, PolicyOverride>
  per_intent_overrides:   map<string, PolicyOverride>

  created_by:    string
  approved_by:   string | null
  change_reason: string
}
```

### 2.2 `PolicyEnforcer` (`interfaces.md` §20.2)

```
interface PolicyEnforcer {
  enforce(plan: OptimizationPlan, policy: OptimizationPolicy) -> EnforcementResult
  check_model_allowed(model_id: string, policy: OptimizationPolicy) -> boolean
  check_cache_allowed(entry: CacheEntry, policy: OptimizationPolicy) -> boolean
  check_tool_allowed(tool_id: string, auth: AuthorizationContext, policy: OptimizationPolicy) -> boolean
}

EnforcementResult {
  compliant:     boolean
  violations:    PolicyViolation[]
  modified_plan: OptimizationPlan
}

PolicyViolation {
  policy_id:   string
  rule_id:     string
  severity:    enum { WARNING, BLOCK }
  message:     string
  remediation: string
}
```

No field, type, enum, or schema is defined here beyond what `interfaces.md` §20 states. `PolicyOverride` (referenced by `per_workflow_overrides`/`per_intent_overrides`), `CacheEntry` and `AuthorizationContext` are not reproduced here: this capability's P0 B row ("Policy read API, budget-enforcement hook consuming Capability 1's ledger") does not require `check_cache_allowed` or `check_tool_allowed`, so those types are out of scope for now, not silently dropped — a future sub-phase that needs them must look them up directly rather than assume this note covers them.

`PolicyEnforcer.enforce`'s `plan: OptimizationPlan` parameter consumes the existing `core.schemas.OptimizationPlan` type (built under Capability 1's shared `core/` foundation, `REM-P0.1.A-01`/`-02`). That type is already documented as explicitly incomplete (`CORE-GAP-03`, `CORE-GAP-04`, recorded in its own Javadoc). This sub-phase does not resolve those gaps; they remain Capability-1-foundation-owned. A future sub-phase that calls `enforce()` must report, not silently work around, any field `OptimizationPlan` is still missing.

## 3. Package placement (verified, not assumed)

`conventions.md` §2.1 defines only a top-level `policy/` leaf, citing `interfaces.md` §20 with no further subdivision (`conventions.md` line 243: `├── policy/  # INTF §20`). There is no conventions-literal `context_policy/` subdivision — unlike, for example, T1.1 Sanitizer's `pipeline/t1_context/sanitizer/`, which matched a conventions.md leaf exactly. `execution-plan.md` §9 already records this as "no exact match found" and proposes `control_plane/policy/context_policy/`, "to be confirmed (not assumed) at `EXE-P0.4.A`" (§18.7's own Package/module responsibility row says the same).

This sub-phase confirms that proposal is the correct realization: it is the smallest, non-colliding extension of the `policy/` leaf that distinguishes this capability's own policy-resolution scope from any future unrelated governance code that might also live under `policy/` (none exists yet). No sibling package under `policy/` exists to collide with. The design note and all future Capability 4 code live under `control_plane/policy/context_policy/`, mirroring the pattern already used for Capability 3 (`control_plane/pipeline/t1_context/sanitizer/`).

## 4. Dependencies and preconditions

- **Dependency (declared, `execution-plan.md` §18.7):** Capability 1 (Token Accounting and Cost Ledger), for budget enforcement. Capability 1 is CLOSED (Capability Gate P0.1 approved 2026-09-23); its `accounting.ledger` package is consumed, never recreated, by any future Capability 4 sub-phase that needs it.
- **Precondition (declared):** Capability 1's Capability Gate closed. Satisfied.
- **Downstream impact (declared):** Prompt Assembler (Capability 5) depends on this capability. Capability 5 does not exist yet; nothing here implements or anticipates its consumption beyond citing that the dependency exists.

## 5. Security/Governance disposition (restated for traceability; not implemented by this sub-phase)

`execution-plan.md` §18.7 states: "Policy reads must never be overridden by an optimization decision — `security.md`'s precedence model (Security/Authorization/Policy above Optimization Benefit) applies even though this capability itself is not a `GSP-NNN` component." This is the plan's own paraphrase of `security.md`'s §4 precedence diagram (Security/Authorization/Policy → Quality Requirements → Correctness/Integrity → Optimization Benefit → Cost/Latency Preference), restated at `security.md` line 730 ("Security/optimization/quality precedence: preserved"), not a verbatim quotation of either. Realizing this invariant in code is Sub-phase E's job ("Policy is never overridden by an optimization signal"), not A's. This note restates it only so the contract and the eventual E-row test are traceable to the same source.

## 6. Scope boundary for this sub-phase

In scope for A: citing `interfaces.md` §20's contract as-is; confirming package placement. Out of scope for A: a policy read API, a budget-enforcement hook, any test, any log line, any security/governance realization — all of those are B, D, E, and H respectively, each requiring its own `Execute` (per-unit; Capability 4 has not opened a continuous `§18.20` path and `Execute EXE-P0.4.A` opens one only for the remainder of Capability 4, not before).

## 7. Open items (none blocks this design note)

1. `PolicyOverride`, `CacheEntry`, and `AuthorizationContext` are referenced by §20 but not reproduced here, since B's declared scope does not need them (§2 above). A future sub-phase needing them must look them up directly.
2. `OptimizationPlan`'s `CORE-GAP-03`/`CORE-GAP-04` (Capability-1-foundation-owned, pre-existing) are not resolved here and are not this capability's gap to close.
3. No dedicated architecture subsection exists for "Context Policy" (`execution-plan.md` §9's own honest note, restated in §1 above) — this is a pre-existing, already-recorded condition of the capability's own definition, not a new gap this note introduces.
4. No `SOURCE-GAP`/`CONTRA` ID is newly registered by this sub-phase: nothing here is underspecified relative to what A requires (cite the contract, confirm placement); both were verified directly against the live files.

## 8. Traceability

| Item | Reference |
|---|---|
| Capability 4 definition | `execution-plan.md` §18.7 |
| `OptimizationPolicy` | `interfaces.md` §20.1 |
| `PolicyEnforcer` / `EnforcementResult` / `PolicyViolation` | `interfaces.md` §20.2 |
| Package placement precedent | `conventions.md` §2.1 (line 243, `policy/`); `execution-plan.md` §9 |
| Dependency (Capability 1) | `accounting.ledger` (Capability Gate P0.1, approved 2026-09-23) |
| `OptimizationPlan` (consumed, not redefined) | `core.schemas.OptimizationPlan` (`CORE-GAP-03`/`-04`, pre-existing) |
| Security/Governance disposition (restated, not implemented) | `security.md` precedence/independence invariants (plan's own paraphrase) |
