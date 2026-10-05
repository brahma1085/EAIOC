# Quality Validation Readiness — Capability 2, Sub-phase F (EXE-P0.2.F)

**Status:** Design note. No Java code is added by this sub-phase.
**Source:** `docs/execution-plan.md` §18.5, Capability 2, Sub-phase F row. That row's own Deliverable is "documented readiness to accept `QG-NNN` evaluation calls once P1 ships", with Expected Evidence "design note cross-referencing `quality-gates.md` §5".
**Cross-reference target:** `docs/quality-gates.md` §5 (`QG-001` to `QG-010`).

## 1. What this capability is, relative to the quality gates

This capability is the baseline harness: `run_baseline()` returns a `BaselineEvaluationRecord` built from a single `verified = true` ledger entry (`EXE-P0.2.B`). It is the substrate that later quality gates will be measured against. It does not evaluate any `QG-NNN` dimension itself.

No P1+ technique exists yet to run a `QG-NNN` gate against. So at P0 scope this sub-phase delivers only documented readiness, not a gate implementation.

## 2. The ten quality dimensions, and how this harness relates to each

`quality-gates.md` §5 defines `QG-001` to `QG-010`. The harness does not implement any of them. The table records only what the baseline record already carries, so a future gate knows what it can and cannot reuse.

| Gate | Dimension (`quality-gates.md` §5) | Relation to the P0 baseline record |
|---|---|---|
| `QG-001` | Accuracy | None. The record carries no accuracy measurement. |
| `QG-002` | Task completion | None. |
| `QG-003` | Retrieval quality | None. |
| `QG-004` | Factuality | None. |
| `QG-005` | Schema compliance | None. |
| `QG-006` | Semantic equivalence | None. |
| `QG-007` | Safety | None. |
| `QG-008` | User satisfaction | None. |
| `QG-009` | Latency | Partial. The record carries `latencyMsBaseline`, mapped from the ledger's `performance.e2e_latency_ms`. Whether that is the right input for `QG-009` is for the gate's own design to decide. |
| `QG-010` | Cost | Partial. The record carries `baselineCost` and `currency`, mapped from the ledger's `baseline_cost`. It is a counterfactual baseline, not realized spend. |

Only the last two rows are partial, and both are inputs a future gate may consume. Neither is a gate result.

## 3. What a future `QG-NNN` evaluation call would need from this harness

- A baseline record for the same `(tenant_id, request_id)` the optimized run uses. `run_baseline()` already provides that.
- The source entry identity (`sourceEntryId`), so the baseline can be traced to one verified ledger entry.
- Tenant scoping. A gate must never compare against another tenant's baseline. The tenant-scoping guarantees are covered by `EXE-P0.2.E`.

## 4. Explicitly out of scope at P0

- `run_optimized()`, `compare()` and `report_regression()`. These are deferred INTF-030 forward contract (HQ-3). Neither is declared nor stubbed in Java.
- `RegressionReport`, which remains undefined (`SOURCE-GAP-EVAL-01`).
- Any live Path A measurement. Live measurement is deferred to Capability Gate P0.2 (`execution-plan.md` §18.10).

## 5. Readiness statement

The baseline harness can accept `QG-NNN` evaluation calls once a P1+ technique exists, in the sense that its record carries the inputs listed in §3. No `QG-NNN` gate is implemented, measured, or validated by this sub-phase.
