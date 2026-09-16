# Security

**Status:** PRE-IMPLEMENTATION — Level 0 (RESEARCH). **Version:** 1.0.0. **Generated:** 2026-09-17 (per repository date). **Generation prompt:** `docs/prompts/generate-security.prompt.md` (V2 Consolidated Master Prompt).

**Position in the documentation chain:** Seventh generated downstream document, following `optimization-catalog.md` → `provider-matrix.md` → `cache-strategy.md` → `agent-optimization.md` → `inference-optimization.md` → `quality-gates.md`. Gated by each of those six documents' own passing readiness verdict. Next in chain: `observability.md` (not generated here, not cascaded into).

**Authoritative sources read for this generation** (authority order, highest first): `problemStatement.txt` (EAIOC-SPEC-001) → `Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md` (Rev 1.4) → `architecture.md` (Rev 1.3) → `interfaces.md` (v1.2.0) → `conventions.md` (Rev 1.1.0) → `edge-cases.md` (v1.2.0) → `scenario-matrix.md` → `optimization-catalog.md` → `provider-matrix.md` → `cache-strategy.md` → `agent-optimization.md` → `inference-optimization.md` → `quality-gates.md` → root `CLAUDE.md`.

**Scope boundary reminder:** this document elaborates the *concrete* Control-Plane mechanisms for the Governance/Safety Plane (SGE, DGE, TMG, HAG, CIS) that upstream architecture left as component specs plus, for CIS/TMG specifically, an explicitly named downstream design decision (`SOURCE-GAP-ARCH-02`). It does not redefine any upstream interface schema, any canonical cache type, any agent-loop algorithm, any P5 capability, or any quality-gate methodology — each of those remains owned by its existing document and is cited here, not restated.

---

## 1. How to Read This Document

- **Maturity:** every mechanism proposed here is Level 0 — RESEARCH per `architecture.md` §42's maturity model. This repository has zero local benchmark evidence (pre-implementation). No mechanism in this document is presented as production-validated.
- **Evidence labels used throughout:**
  - `SOURCE-DEFINED` — stated directly by an upstream authoritative document; restated here, not invented.
  - `SOURCE-DERIVED` — a direct, non-creative consequence of a source-defined requirement (e.g., applying an existing invariant to a new concrete case).
  - `PROPOSED METHODOLOGY` — a concrete approach/algorithm/taxonomy this document introduces because no source specifies one; a design to validate, not a validated design.
  - `PROPOSED DEFAULT — VALIDATE LOCALLY` — a concrete numeric value (threshold, interval, confidence cutoff) this document proposes; must be locally benchmarked before any production reliance.
  - `REFERENCE-ONLY` — an external research/industry pattern cited for context; never a hard-coded expected result (root `CLAUDE.md` rule 7; `architecture.md` §47.12, H19).
  - `NOT YET VALIDATED` — an explicit statement that a proposed mechanism has no local accuracy/false-positive/false-negative evidence.
- **ID scheme introduced by this document:** `GSP-NNN` (exactly 5, one per Governance/Safety Plane component — §5 below) and, as a document-local security-control catalog, `SC-NNN` (§28). Neither series renames, renumbers, or replaces any existing `SEC-NNN`, `INTF-NNN`, `EC-NNN`, `SCN-SEC-NNN`, `TECH-NNN`, `DA-NNN`, `AL-NNN`, `AR-NNN`, `CACHE-NNN`, `PROV-NNN`, `INFOPT-NNN`, or `QG-NNN` identifier.
- Every `EC-NNN`/`SCN-SEC-NNN`/`AC-NNN`/`OBJ-NNN`/`SEC-NNN`/`INTF-NNN` cited below was verified against the actual current repository files during this generation, not carried over unverified from any prior draft or prompt text.

---

## 2. Security Scope and Central Invariant

> **No optimization or execution decision may weaken, bypass, invalidate, silently reinterpret, or override a security, authorization, policy, tenant-isolation, data-governance, integrity, or human-approval requirement.** (`SOURCE-DERIVED` from `architecture.md` §29's "all security requirements are mandatory and non-negotiable," restated as this document's own governing statement.)

This document's job is to make that invariant concrete for the five Governance/Safety Plane components `architecture.md` §47.2 introduced: **SGE** (spend), **DGE** (data sensitivity), **TMG** (tool/MCP trust), **HAG** (human approval), **CIS** (content integrity). It particularly closes in on the mechanism-level detail two upstream documents have already deferred here:

- `architecture.md` §47.18's `SOURCE-GAP-ARCH-02`: *"The exact mechanism for CIS (prompt-injection/content-integrity screening, H14) and for achieving each FTR feasibility tier per platform (H08) is left as a downstream architecture decision... Deferred to `optimization-catalog.md` / `provider-matrix.md` / `security.md`."* The FTR half was addressed in `provider-matrix.md` (evidence-labeled tier assignments); **this document addresses the CIS half** (§12 below), and additionally elaborates TMG's schema-integrity mechanism (§13) since `optimization-catalog.md`'s own Scope Boundary row names both: *"CIS/TMG mechanism implementation detail (the exact prompt-injection screening algorithm, the exact schema-integrity check)."*
- `cache-strategy.md`'s deferral of "CIS content-integrity screening mechanism" here.
- `quality-gates.md`'s deferral of "content integrity / prompt injection" and "detailed security architecture" here, alongside its own boundary statement that a quality-gate failure (optimization fidelity) is distinct from a security-screening failure (content trustworthiness) — that distinction is preserved throughout this document, not conflated.

---

## 3. Fail-Open Optimization vs. Fail-Closed Security

> **Fail-open on optimization, fail-closed on security — these are different categories of failure.** (`SOURCE-DEFINED`, root `CLAUDE.md` rule 2, `conventions.md` §16.2, `edge-cases.md` §43.13.)

| | Optimization-stage failure | Security/governance failure |
|---|---|---|
| **Example** | A compressor bug, a cache-write error, a routing-signal timeout | An authorization check cannot complete; a `classify()` call fails; CIS screening is unavailable; TMG cannot validate a schema; a required approval is not resolved |
| **Correct response** | Fall back to the unoptimized/original representation; the request continues | Reject, quarantine, deny, or pause; the request does **not** proceed on the unvalidated path |
| **Precondition for fallback** | The request must already be authorized and policy-compliant | None — a security failure is the precondition failing, not a downstream stage on top of it |
| **Governing components** | Any `T0`–`T3`, `OI`, `CL`, `CE`, `TE`, `AL`, `AR`, `QO`, `EL` stage | SGE, DGE, TMG, HAG, CIS (this document); also `AuthorizationService`, `PIIClassifier` (§21) |

An innocent optimization-stage bug on an already-authorized, policy-compliant request must never turn into a full request rejection; conversely, a security/governance evaluation that cannot complete must never silently degrade into "proceed as if it had passed." Every `GSP-NNN` entry below (§5–9) restates its own Failure Behavior in exactly these terms.

---

## 4. Security Precedence

`SOURCE-DEFINED` / `SOURCE-DERIVED`, consistent with `quality-gates.md` §17 (its own precedence list) and root `CLAUDE.md`:

```
Security / Authorization / Policy
        ↓
Quality Requirements
        ↓
Correctness / Integrity
        ↓
Optimization Benefit
        ↓
Cost / Latency Preference
```

Optimization may improve cost, latency, or token consumption **only when every higher-order constraint remains satisfied**. A quality `PASS` never overrides a security failure; a security `PASS` never substitutes for quality validation — both gates can be required simultaneously, and neither implies the other (§24).

---

## 5. Stateful Security Model

EAIOC is a stateful, resumable execution Control Plane (`architecture.md` §46). A security decision is not permanently valid merely because it was valid at execution start. Relevant state that can change mid-execution and that must trigger security revalidation when it does: identity, permissions, tenant, policy, context version, workflow version, file/repository state, tool/MCP availability, model/provider selection, memory, cache freshness, data-classification status, deletion state, budget/spend state, and approval state.

**Conceptual linkage requirement** (`PROPOSED METHODOLOGY` — no interface field is invented; this is a statement of what must be true, not a new schema): a security-sensitive decision must be traceable to the state version(s) it was evaluated against, so that a later state change can be recognized as invalidating it. This mirrors the existing `CVM`/`WVM` versioning pattern (`architecture.md` §46) rather than introducing a parallel one.

---

## 6. Canonical Governance/Safety Plane Entries — `GSP-001`–`005`

Introduced as this document's own ID series, exactly 5, one per `architecture.md` §47.2 component, in that section's order. This is a document-scoped series consistent with `cache-strategy.md`'s `CACHE-NNN`, `inference-optimization.md`'s `INFOPT-NNN`, and `quality-gates.md`'s `QG-NNN` — it does not rename or renumber SGE/DGE/TMG/HAG/CIS, `SEC-NNN`, or `INTF-06N`.

| ID | Component |
|---|---|
| `GSP-001` | SGE — Spend Governance Engine |
| `GSP-002` | DGE — Data Governance Engine |
| `GSP-003` | TMG — Tool/MCP Trust Gate |
| `GSP-004` | HAG — Human Approval Gate |
| `GSP-005` | CIS — Content Integrity Screen |

`GSP-003` and `GSP-005` carry the deepest elaboration (§9, §11, plus dedicated deep-dive sections §12–13), since they are where `SOURCE-GAP-ARCH-02` and the `optimization-catalog.md` deferral point most directly.

### 7. GSP-001 — SGE (Spend Governance Engine)

| Field | Content |
|---|---|
| **Component** | SGE — Spend Governance Engine |
| **Restated Purpose** | Enforce independently configurable spend limits and runaway-cost protection at tenant/organization/user/application scope (`SOURCE-DEFINED`, `architecture.md` §47.2.1). |
| **Restated Failure Behavior** | If `evaluate_budget()` cannot determine remaining budget, `status` defaults to the tenant's policy-configured safe default (`THROTTLED` or `HALTED`) — never `WITHIN_BUDGET` by default (`SOURCE-DEFINED`, `interfaces.md` §43.1). |
| **Security Responsibility** | Prevent runaway cost from becoming an availability/cost-denial risk, without ever being consulted as a proxy for authorization. |
| **Concrete Control-Plane Mechanism** | `PROPOSED METHODOLOGY`: (1) `evaluate_budget()` is called before any provider-cost-incurring action, comparing `projected_cost` against the scope's remaining budget; (2) `detect_runaway()` runs continuously against a rolling historical baseline per scope, independent of the absolute-limit check, so a cost-acceleration anomaly (e.g., an unbounded sub-agent fan-out) is caught before the configured limit is exhausted — this is `SOURCE-DEFINED` as a requirement (§47.2.1) but the acceleration-factor computation itself is `PROPOSED METHODOLOGY`; (3) on breach, `activate_circuit_breaker()` halts/throttles only the affected scope, never other scopes (tenant isolation, §16). |
| **Preconditions** | A resolved `BudgetScope` (tenant/org/user/application) and a `projected_cost` estimate from the cost-accounting ledger (`architecture.md` §27). |
| **Inputs** | Real-time cost-ledger events; configured budget policy per scope (`SOURCE-DEFINED`, §47.2.1). |
| **Relevant Security State** | Current cumulative spend per scope; circuit-breaker state; whether the request's authorization/policy evaluation has already independently completed. |
| **Interface Cross-Reference** | `SpendGovernanceEngine.evaluate_budget/record_spend/detect_runaway/activate_circuit_breaker/reset_circuit_breaker`; `BudgetScope`, `BudgetEvaluationResult`, `RunawayDetectionResult`, `SpendLedgerUpdate` (`interfaces.md` §43.1, INTF-063 — cited, not redefined). |
| **Independence Invariant** | `BudgetEvaluationResult` is never consulted in place of `AuthorizationDecision` (INTF-038) or `EnforcementResult` (INTF-036). `WITHIN_BUDGET` never implies authorized; `HALTED` is never inferred from a failed authorization check (SEC-011, `SOURCE-DEFINED`). A caller receiving both must apply both independently. |
| **Fail-Closed Behavior** | Budget-indeterminate → default to the policy-configured safe default (`THROTTLED`/`HALTED`), never `WITHIN_BUDGET`. This is fail-closed *for spend*, not a substitute for the separate authorization fail-closed path. |
| **Anti-Shedding Guarantee** | SGE is never included in a `shed_stages` list under backpressure, regardless of severity (NFR-014, SPC §47.4.1, `SOURCE-DEFINED`). |
| **Revalidation Trigger** | Policy/budget-limit change; scope reclassification (e.g., a user reassigned between organizations); circuit-breaker reset. |
| **Recovery Behavior** | On mid-execution exhaustion: identical to `architecture.md` §46's `SUSPENDED_BUDGET_EXCEEDED` handling — return `PARTIAL`, remaining budget 0, per-section consumption breakdown, never silent overspend (`SOURCE-DEFINED`). |
| **Audit/Provenance Requirement** | `BUDGET_THROTTLED` event (`scope_type`, `scope_id`, `remaining_budget` — `interfaces.md` §43.11, `SOURCE-DEFINED`); every `activate_circuit_breaker()` call recorded in an `AuditRecord` (§27). |
| **Tenant-Isolation Requirement** | `BudgetScope.scope_id` isolates evaluation and circuit-breaker activation to exactly the affected scope; no cross-scope blending (root `CLAUDE.md` rule 4). |
| **Data-Governance Requirement** | N/A — SGE operates on cost figures, not content; no sensitivity classification applies to spend data itself beyond standard tenant scoping. |
| **Optimization Interaction** | T0.1 Model Router, T0.3 Reasoning Budget Controller, and T3.1 Agent Stop Controller may consume SGE's remaining-budget signal as an input (`SOURCE-DEFINED`, §47.2.1) — SGE informs these optimization decisions but never substitutes for their own logic. |
| **Quality Interaction** | None directly; a `HALTED` scope may force a lower-cost/lower-quality path, but that trade-off is evaluated by the optimization/quality layers, not by SGE itself. |
| **Validated By** | `EC-156` (Request-Level Budget Exhausted Mid-Execution), `EC-157` (Runaway-Cost Acceleration Detected Before the Configured Limit Is Exhausted), `EC-158` (Budget Race Between Two Concurrent Executions), `EC-159` (`evaluate_budget()` Cannot Determine Remaining Budget), `EC-160` (`WITHIN_BUDGET` Status Mistakenly Consulted as an Authorization Signal — the direct SEC-011 violation case), `EC-161` (Budget Threshold Crossed Mid-Flight While a Non-Idempotent Side Effect Is Already Dispatched). No dedicated `SCN-SEC-NNN` scenario isolates SGE specifically — `SCN-SEC-004`'s authorization focus is adjacent but not SGE-specific; **`NO DEDICATED SCENARIO`** for SGE in the security domain (the six `EC-15N` cases above are `scenario-matrix.md`'s own hardening-pass validation for SGE, filed outside the `SCN-SEC-*` prefix). |
| **Traceability** | PS §52.5 (H05); `architecture.md` §29 (SEC-011), §47.2.1; `interfaces.md` §43.1 (INTF-063); `conventions.md` governance package (`governance/spend/`, §7.9); OBJ-027; AC-043. |

### 8. GSP-002 — DGE (Data Governance Engine)

| Field | Content |
|---|---|
| **Component** | DGE — Data Governance Engine |
| **Restated Purpose** | Classify content sensitivity before optimization admission and propagate retention/deletion decisions across every persistent surface (`SOURCE-DEFINED`, §47.2.2). |
| **Restated Failure Behavior** | A `classify()` failure defaults `classification` to `SENSITIVE` — never `NON_SENSITIVE` (`SOURCE-DEFINED`, `interfaces.md` §43.2). This is a security/integrity failure, not an optimization-stage failure, and does not fall back to unoptimized processing of unclassified content — the content is excluded from caching/compression/retrieval admission until validly classified. |
| **Security Responsibility** | Ensure sensitive content (PII/PHI/PCI/secrets where applicable) is identified *before* any transformation, caching, or persistence decision is made about it, and that deletion requests are honored across every surface that ever held the data. |
| **Concrete Control-Plane Mechanism** | `PROPOSED METHODOLOGY`: (1) `classify()` runs as a gate at T0/T1 admission, before T1.6/T1.7 caching, T1.9/T1.10 pruning/compression decisions, or any embedding step; (2) classification composes `PIIClassifier.classify` (INTF-040, §21.4, reused not duplicated per `interfaces.md` §43.2's `SensitivityClassificationResult.pii_result` field) with any additional non-PII sensitivity signal (e.g., confidential-business-data markers) the deployment's own policy defines — this document does not invent a universal taxonomy beyond `SENSITIVE`/`NON_SENSITIVE`/`UNKNOWN`, consistent with `SOURCE-GAP-ARCH-01`'s deliberate non-assertion of any specific regulatory regime; (3) `get_retention_policy()`/`request_deletion()`/`get_deletion_propagation_status()` are called against the seven `DataSurface` enum values (`CACHE_EXACT`, `CACHE_SEMANTIC`, `MEMORY`, `LEDGER`, `LOG`, `TRACE`, `CHECKPOINT` — verified against `interfaces.md` §43.2's actual current enum, seven values not six as this prompt's own prose estimated; use the verified seven). |
| **Preconditions** | Content entering any T0/T1 admission point; a `ClassificationContext` sufficient to disambiguate content type. |
| **Inputs** | Raw content entering T0/T1; deletion/erasure requests; retention policy (`SOURCE-DEFINED`, §47.2.2). |
| **Relevant Security State** | Current classification status; residency constraint (if any); retention window; deletion-request status. |
| **Interface Cross-Reference** | `DataGovernanceEngine.classify/get_retention_policy/request_deletion/get_deletion_propagation_status`; `SensitivityClassificationResult`, `DataSurface`, `RetentionPolicy`, `DeletionRequestRecord`, `DeletionPropagationReport`, `SurfaceHoldRecord` (`interfaces.md` §43.2, INTF-064 — cited, not redefined). |
| **Independence Invariant** | Classification is never inferred from optimization/ROI signals — a content item is not treated as non-sensitive merely because treating it as sensitive would be more expensive to handle (SEC-012). |
| **Fail-Closed Behavior** | Classification-indeterminate → `SENSITIVE` by default; `encryption_required = true`, `caching_eligible = false` until validly classified (`SOURCE-DEFINED`). This is a Tier 0 (SEC-protected, `architecture.md` §46's tier model) concern, never overridden by relevance score or budget pressure — consistent with root `CLAUDE.md` rule 6. |
| **Anti-Shedding Guarantee** | DGE is never included in a `shed_stages` list under backpressure, regardless of severity (NFR-014, `SOURCE-DEFINED`). |
| **Revalidation Trigger** | Content's sensitivity classification changing mid-execution (`EC-170`); a residency constraint newly applying to an already-cached item (`EC-212`); a deletion request arriving after data was already persisted into a checkpoint (`EC-171`). |
| **Recovery Behavior** | On deletion request: propagate to every surface previously holding the data; a surface that cannot be immediately cleared (e.g., an active checkpoint) is reported pending with a reason, never silently marked cleared (`SOURCE-DEFINED`, `SCN-SEC-006`). |
| **Audit/Provenance Requirement** | `dge.classification_failure.count`, `dge.default_sensitive_applied.count` (`SCN-SEC-005`); `DELETION_PROPAGATED` event with `deletion_id`, `surfaces_cleared` (`SCN-SEC-006`). |
| **Tenant-Isolation Requirement** | `RetentionPolicy`/deletion requests are scoped by `tenant_id`; no cross-tenant deletion or retention-policy blending. |
| **Data-Governance Requirement** | This *is* the data-governance mechanism; see Concrete Mechanism above. |
| **Optimization Interaction** | Classification gates T1.6/T1.7 caching eligibility and T1.9/T1.10 pruning/compression admission — an optimization stage never processes unclassified content by treating "unknown" as "safe to optimize." |
| **Quality Interaction** | None directly — classification is a security gate prior to any quality evaluation of the resulting transformation. |
| **Validated By** | `SCN-SEC-005` (Data Classification Fails; Content Defaults to SENSITIVE — `EC-166`), `SCN-SEC-006` (Deletion/Erasure Request Propagates Across All Data Surfaces — `EC-167`). Also `EC-168` (Data-Residency Constraint Conflicts With a Routing Decision), `EC-169` (Retention Period Unconfigured Defaults to Unbounded), `EC-170` (Sensitivity Classification Changes Mid-Execution), `EC-171` (Deletion Request After Checkpoint Persistence), `EC-212` (Residency Change Combined With Already-Cached Sensitive Data) — these four are `THIN COVERAGE` in the `SCN-SEC-*` domain specifically (no dedicated `SCN-SEC-NNN` isolates each individually), validated instead by their own `EC-NNN` entries directly per `scenario-matrix.md`'s hardening-pass reconciliation. |
| **Traceability** | PS §52.7 (H07); `architecture.md` §29 (SEC-012, SEC-013), §47.2.2; `interfaces.md` §43.2 (INTF-064); `conventions.md` `governance/data/` (§7.9, §13.7); OBJ-029; AC-045. |

### 9. GSP-003 — TMG (Tool/MCP Trust Gate)

| Field | Content |
|---|---|
| **Component** | TMG — Tool/MCP Trust Gate |
| **Restated Purpose** | Authenticate tool/MCP identity and validate capability metadata, schema integrity, and staleness before a tool result is trusted (`SOURCE-DEFINED`, §47.2.3). |
| **Restated Failure Behavior** | An identity or schema-integrity failure returns `QUARANTINED` (fail-closed) — not an optimization fallback to using the untrusted tool anyway (`SOURCE-DEFINED`). |
| **Security Responsibility** | Ensure a tool/MCP server is who it claims to be, that its declared schema has not silently drifted, and that its result is not trusted merely because the call itself was authorized. |
| **Concrete Control-Plane Mechanism** | See §13 (TMG Mechanism Deep-Dive) for the full elaboration — the component-level summary here is deliberately brief since §13 is this document's designated deep-dive location. |
| **Preconditions** | A tool/MCP call about to be made or a result about to be admitted. |
| **Inputs** | Tool/MCP server identity assertions; tool/function schemas (consumed by DA-008, TE-002 per `architecture.md` §47.2.3); tool call results (`SOURCE-DEFINED`). |
| **Relevant Security State** | Last-known `schema_hash`; `last_revalidated_at`; current authorization scope for the tool. |
| **Interface Cross-Reference** | `ToolMCPTrustGate.authenticate_identity/validate_schema/check_staleness/trust_decision`; `IdentityVerificationResult`, `SchemaValidationResult`, `ToolFreshnessResult`, `ToolTrustResult` (`interfaces.md` §43.3, INTF-065 — cited, not redefined). |
| **Independence Invariant** | Tool call authorization is evaluated independently of TE-001's ROI predictor — cost/token efficiency never substitutes for an authorization or trust check (SEC-014). Authorization itself is delegated to `AuthorizationService.check_tool_access` (INTF-038, §21.2); TMG's trust decision is a distinct evaluation layered on top, not a replacement for it. |
| **Fail-Closed Behavior** | Identity/schema-integrity failure → `QUARANTINED`, never a silent fallback to using the untrusted tool. |
| **Anti-Shedding Guarantee** | TMG is never included in a `shed_stages` list under backpressure, regardless of severity (NFR-014, `SOURCE-DEFINED`). |
| **Revalidation Trigger** | Schema-hash change between calls (`EC-173`); revalidation-interval expiry for a dynamically-discovered tool (`EC-175`); tool/MCP version change (extends CL-003 dependency-aware cache invalidation, `EC-176`). |
| **Recovery Behavior** | On `SCHEMA_STALE`: treat as a staleness/trust event, never silently accept; on `UNAUTHORIZED`/`QUARANTINED`: refuse the call or quarantine the result, no fallback execution. |
| **Audit/Provenance Requirement** | `tmg.schema_staleness_event.count` (tool_id) (`SOURCE-DEFINED`, `edge-cases.md` §44's TMG entries). |
| **Tenant-Isolation Requirement** | Tool/MCP trust state is not shared across tenants where per-tenant tool registration differs. |
| **Data-Governance Requirement** | N/A directly — TMG governs tool/server trust, not content sensitivity (that is DGE's and, for the result's *content*, CIS's responsibility — see the CIS-Independence field below). |
| **Optimization Interaction** | TE-001's ROI predictor and TE-003's dynamic tool discovery both consume TMG's trust state as an input but never override it. |
| **Quality Interaction** | None directly. |
| **Validated By** | `SCN-SEC-009` (MCP Result Screened for Content Integrity Independent of Tool Trust — compound with CIS, `EC-187`). Component-specific: `EC-172` (Identity Authentication Fails), `EC-173` (Schema Changes Without a Version Bump), `EC-174` (Tool Authorized via Favorable ROI but TMG's Independent Check Fails — the direct SEC-014 case), `EC-175` (Discovered Tool's Availability Assumed to Persist Past Its Revalidation Interval), `EC-176` (Cached Tool Result/Schema Not Invalidated on Version Change). Also `EC-097` (Security-Sensitive Tool Call Argument Minimized Without a Governing Redaction Rule) and `SCN-SEC-004` (Privilege Escalation via Crafted Tool Argument) are adjacent authorization-layer validations, not TMG-specific. **`THIN COVERAGE`** in the `SCN-SEC-*` domain specifically for TMG's identity/schema mechanics alone (only the compound CIS×TMG scenario is dedicated); the five `EC-17N` cases carry the primary validation weight. |
| **Traceability** | PS §52.11 (H11); `architecture.md` §29 (SEC-014), §47.2.3; `interfaces.md` §43.3 (INTF-065); `conventions.md` `governance/tool_trust/` (§7.9, §13.8); AC-048. |

### 10. GSP-004 — HAG (Human Approval Gate)

| Field | Content |
|---|---|
| **Component** | HAG — Human Approval Gate |
| **Restated Purpose** | Require explicit human approval for a policy/risk-designated subset of consequential or irreversible actions before execution (`SOURCE-DEFINED`, §47.2.4). |
| **Restated Failure Behavior** | If the approval mechanism itself is unavailable, the gated action is blocked (fail-closed) rather than proceeding without approval. On timeout, the policy-defined default (deny, or escalate to a different approver) applies — never silent `APPROVED` (`SOURCE-DEFINED`). |
| **Security Responsibility** | Ensure a policy-designated action never executes without a resolved, current, non-stale approval decision. |
| **Concrete Control-Plane Mechanism** | `PROPOSED METHODOLOGY`: (1) `requires_approval()` is evaluated per action against the policy/risk classification — this is a per-tenant/per-workflow policy decision, not a fixed universal action list (`SOURCE-DEFINED`); (2) a designated action's execution is suspended (identical to `architecture.md` §46.3's `SUSPENDED_*` state handling: checkpoint via CPM, return `PARTIAL` or `AWAITING_APPROVAL`, resume via RCO once resolved); (3) `resolve_approval()`'s outcome is checked against the *current* policy/context version at execution time, not only at approval-request time — an approval granted under a now-superseded policy or context version must be re-evaluated (`EC-181`), not blindly honored. |
| **Preconditions** | A `ProposedAction` with a `RiskClassification` indicating `designated_for_approval = true`. |
| **Inputs** | `ApprovalRequest` (action, risk classification, policy/version snapshot); approver identity and decision (`SOURCE-DEFINED`). |
| **Relevant Security State** | Current `ApprovalStatus`; `policy_version` at grant time vs. at execution time; whether the gated execution has since been superseded/cancelled. |
| **Interface Cross-Reference** | `HumanApprovalGate.requires_approval/request_approval/get_approval_status/resolve_approval`; `ProposedAction`, `RiskClassification`, `ApprovalRequirement`, `ApprovalRequest`, `ApprovalStatus`, `ApprovalResolution` (`interfaces.md` §43.4, INTF-066 — cited, not redefined). |
| **Independence Invariant** | Low cost, high quality-gate score, low latency, model confidence, or tool trust are never substitutes for a required approval (§23). |
| **Fail-Closed Behavior** | Approval-mechanism-unavailable → block the gated action; timeout → policy-defined deny/escalate default, never silent approve. |
| **Anti-Shedding Guarantee** | HAG is never included in a `shed_stages` list under backpressure, regardless of severity (NFR-014, `SOURCE-DEFINED`). |
| **Revalidation Trigger** | Policy or context version changes between approval grant and execution (`EC-181`); approval revoked after being granted but before execution (`EC-182`); the gated execution becomes superseded or cancelled after approval resolves (`EC-180`). |
| **Recovery Behavior** | A pending approval follows the same suspension handling as any other interruption cause (`§43.11`'s `SuspensionMarker`, WVM) — resumed via RCO once resolved, never silently discarded or silently auto-approved on resume. |
| **Audit/Provenance Requirement** | Approval/denial recorded in the `AuditRecord` alongside the decision it gates, including `policy_version` and `audit_ref` (`SOURCE-DEFINED`, `interfaces.md` §43.4). |
| **Tenant-Isolation Requirement** | Approval requests/approvers scoped per tenant; an approver in one tenant cannot resolve an approval for another. |
| **Data-Governance Requirement** | N/A directly, unless the gated action itself involves sensitive-data handling, in which case DGE's classification is a separate, co-required gate. |
| **Optimization Interaction** | An optimization decision (cheaper model, faster path) never bypasses a required approval merely because it would be more efficient to do so. |
| **Quality Interaction** | A quality-gate `PASS` never substitutes for a required approval (§24). |
| **Validated By** | `EC-177` (Action Executes Before Its Required Approval Resolves), `EC-178` (Approval Mechanism Itself Unavailable), `EC-179` (Approval Request Times Out), `EC-180` (Approval Resolves After the Gated Execution Was Superseded/Cancelled), `EC-181` (Context/Policy Version Changes Between Grant and Execution), `EC-182` (Approval Revoked After Being Granted but Before Execution). **`NO DEDICATED SCENARIO`** in the `SCN-SEC-*` domain — HAG has no dedicated `SCN-SEC-NNN` entry; the six `EC-17N`/`EC-18N` cases above are `scenario-matrix.md`'s hardening-pass validation for HAG, filed outside the `SCN-SEC-*` prefix. |
| **Traceability** | PS §52.13 (H13); `architecture.md` §29 (SEC-015), §47.2.4; `interfaces.md` §43.4 (INTF-066); `conventions.md` `governance/human_approval/` (§7.9); OBJ-033; AC-050. |

### 11. GSP-005 — CIS (Content Integrity Screen)

| Field | Content |
|---|---|
| **Component** | CIS — Content Integrity Screen |
| **Restated Purpose** | Screen externally-sourced content (RAG chunks, search results, tool/MCP results, sub-agent handoffs) for prompt injection and content-integrity violations before any optimization stage admits, ranks, compresses, caches, or authorizes an action based on it (`SOURCE-DEFINED`, §47.2.5). |
| **Restated Failure Behavior** | `SCREENING_UNAVAILABLE` is a security/integrity failure — the content is rejected or quarantined, never silently admitted because the check itself failed (`SOURCE-DEFINED`). |
| **Security Responsibility** | Ensure every piece of non-user-input content is untrusted by default until it explicitly passes screening, uniformly across all five source types, regardless of whether an upstream component (e.g., TMG) already trusted the *channel* the content arrived through. |
| **Concrete Control-Plane Mechanism** | See §12 (CIS Mechanism Deep-Dive) for the full elaboration — this is the document's most load-bearing section. |
| **Preconditions** | Any `ExternalContentItem` about to be admitted, ranked, compressed, cached, or used to authorize an action. |
| **Inputs** | `ExternalContentItem` (`item_id`, `execution_id`, `source`, `content_ref`) — `source` verified against `interfaces.md` §43.5's actual current enum: `RAG_CHUNK`, `SEARCH_RESULT`, `TOOL_RESULT`, `SUBAGENT_HANDOFF`, `MCP_RESULT` (five values, confirmed, `SOURCE-DEFINED`). |
| **Relevant Security State** | Whether the content's originating channel (tool/MCP call) already passed TMG — irrelevant to whether CIS screening is still required (§9's TMG-CIS Independence). |
| **Interface Cross-Reference** | `ContentIntegrityScreen.screen`; `ExternalContentItem`, `ScreeningResult`, `InjectionAssessment` (`interfaces.md` §43.5, INTF-067 — cited, not redefined). |
| **Independence Invariant** | Screening precedes admission uniformly for all five source types — not only end-user input (already covered separately by T1.1 Sanitizer). This ordering constraint takes precedence over any HYBRID/precomputed operating-mode preference (§43.10): precomputation may speed up the screening mechanism itself, but the screening step may not be skipped or deferred until after admission (SEC-016). |
| **Fail-Closed Behavior** | `SCREENING_UNAVAILABLE` → `REJECT`/`QUARANTINE`, never silently admitted. |
| **Anti-Shedding Guarantee** | CIS is never included in a `shed_stages` list under backpressure, regardless of severity (NFR-014, `SOURCE-DEFINED`). |
| **Revalidation Trigger** | A transformation (compression/summarization) that could materially alter the content's effective instruction-following surface (§12.6) — this is `PROPOSED METHODOLOGY`, since no source specifies an exhaustive re-screening trigger list. |
| **Recovery Behavior** | Request proceeds without the unscreened content if it was optional; if mandatory and no substitute exists, the request itself is rejected rather than proceeding with unscreened content (`SOURCE-DEFINED`, `SCN-SEC-008`). |
| **Audit/Provenance Requirement** | `CONTENT_SCREENING_REJECTED` (`item_id`, `source`, `severity`); `CONTENT_SCREENING_UNAVAILABLE` (`item_id`, `source`) (`SOURCE-DEFINED`, `SCN-SEC-007`/`008`). |
| **Tenant-Isolation Requirement** | Screening results/events carry `tenant_id` per the standard event contract (`interfaces.md` §23.1); no cross-tenant screening-history reuse. |
| **Data-Governance Requirement** | Distinct from DGE's sensitivity classification — CIS assesses *trustworthiness/integrity*, DGE assesses *sensitivity*; both may independently gate the same content item. |
| **Optimization Interaction** | Ranking, compression, caching, and embedding of externally-sourced content are all downstream of CIS's `PASS` — none may proceed on content CIS has not screened (§14). |
| **Quality Interaction** | A quality-gate evaluation of a *compressed/transformed* content item never substitutes for CIS's own integrity screening of that item (§24) — a semantically-faithful compression of malicious content is still malicious content. |
| **Validated By** | `SCN-SEC-007` (Prompt Injection in Retrieved Content Screened Before Admission — `EC-185`), `SCN-SEC-008` (Content-Integrity Screening Unavailable; Content Rejected/Quarantined — `EC-183`), `SCN-SEC-009` (MCP Result Screened Independent of Tool Trust — `EC-187`, compound with TMG). Also `EC-057` (original: Prompt Injection Through Compressed Context), `EC-184` (Content Admitted Before Screening Completes — ordering violation), `EC-186` (Malicious Content Engineered to Survive Compression/Summarization — **`PARTIALLY COVERED`** by `SCN-CMP-083`/`SCN-SEC-007` per `scenario-matrix.md`'s own reconciliation table; neither scenario specifically isolates the survive-compression case), `EC-207` (`edge-cases.md`'s own self-identified **duplicate/restatement of `EC-186`** for compound-listing completeness, not a distinct or dedicated validating scenario — see §12.5's honest treatment), `EC-208` (Malicious Tool Result Poisons the Semantic Cache). |
| **Traceability** | PS §52.14 (H14); `architecture.md` §29 (SEC-016), §47.2.5; `interfaces.md` §43.5 (INTF-067); `conventions.md` `governance/content_integrity/` (§7.9, §13.10); OBJ-034; AC-051. |

---

## 12. CIS Mechanism Deep-Dive (`SOURCE-GAP-ARCH-02` — primary closure target)

This section is the document's most load-bearing content — the concrete mechanism `architecture.md` §47.18 named as an open downstream design decision.

### 12.1 Layered detection approach (`PROPOSED METHODOLOGY`)

A single-technique screen is more brittle than a layered one. Proposed layering, each stage narrowing what reaches the next:

1. **Normalization/decoding** — canonicalize encodings (e.g., Unicode homoglyph normalization, HTML-entity/URL-decoding, whitespace normalization) before pattern matching, since injection payloads are frequently obfuscated via encoding tricks (`EC-185`'s "evade a simple keyword-based or low-effort screening pass").
2. **Deterministic/heuristic pattern check** — fast first pass against known injection phrasings and instruction-override markers ("ignore previous instructions," role-reassignment phrasings, delimiter-escape attempts). Cheap, high-precision, low-recall — catches the unsophisticated case (`EC-057`'s original scenario) at negligible cost.
3. **Structural/instruction-boundary analysis** — checks whether the content, once assembled into the prompt, could plausibly be interpreted by the model as an instruction rather than data (e.g., presence of role markers, system-prompt-style formatting mimicry).
4. **Model-based injection assessment** — for content that passes stages 1–3 without a clear verdict, a classifier populates `InjectionAssessment.technique_detected`/`severity` (INTF-067). This is the layer intended to catch indirect/evasive injection (`EC-185`).
5. **Policy decision** — maps the accumulated evidence to `ScreeningResult.status` (§12.3).

Every stage above beyond restating the interface's own fields is `PROPOSED METHODOLOGY — NOT YET VALIDATED`; no source specifies this exact pipeline, and this repository has no local classifier to measure.

### 12.2 All five `ExternalContentItem.source` values

The mechanism in §12.1 applies uniformly to `RAG_CHUNK`, `SEARCH_RESULT`, `TOOL_RESULT`, `SUBAGENT_HANDOFF`, and `MCP_RESULT` (verified enum, `interfaces.md` §43.5). Two are worth calling out explicitly because they are the most common site of a mistaken exemption:

- **`TOOL_RESULT`/`MCP_RESULT`**: a `TRUSTED` `ToolTrustResult` from TMG (GSP-003) establishes that the *tool/server* is who it claims to be and its schema is current — it says nothing about whether the *content* that trusted tool returns is itself safe, since the tool may have fetched that content from its own untrusted upstream source (`EC-187`, `SCN-SEC-009`). TMG and CIS are independent checks; neither substitutes for the other (§9's Independence Invariant, restated here as CIS's own).
- **`SUBAGENT_HANDOFF`**: a sub-agent belonging to the same EAIOC execution is not automatically a trusted-content boundary merely by virtue of being internal — its `SubAgentResultHandoff` (`agent-optimization.md` `AL-005`, cited not redefined) still passes through CIS like any other external-content source, since the sub-agent's own findings may themselves have ingested untrusted content the parent never saw directly.

### 12.3 Confidence-to-status mapping (`PROPOSED DEFAULT — VALIDATE LOCALLY`)

| `InjectionAssessment.severity` | Proposed `ScreeningResult.status` |
|---|---|
| `NONE` | `PASS` |
| `LOW` | `PASS` with the assessment retained for audit (not silently discarded) |
| `MEDIUM` | `QUARANTINE` (held pending a stricter secondary check or human review, per policy) |
| `HIGH` | `REJECT` |

No source specifies these exact mappings or a numeric `confidence` cutoff distinct from the categorical `severity` field; this table is a starting proposal to validate locally. Cross-reference `quality-gates.md`'s VCL calibration procedure (cited, not redefined) for how a probabilistic classifier's confidence would actually be calibrated against a benchmark set over time, once one exists — CIS's classifier is exactly the kind of probabilistic verifier that procedure is designed to calibrate.

### 12.4 Screening ordering discipline

Screening must precede — never run in parallel with an admission path that could act before screening completes — ranking, compression, caching, embedding, tool execution based on the content, model invocation using the content, and agent planning based on the content. `EC-184`'s exact failure mode is content admitted/ranked/compressed/cached *before* screening completes; this is a defect regardless of whether the screening backend would eventually have returned `PASS`, because the harm (an already-cached poisoned entry, an already-executed plan) can occur before the verdict arrives. Precomputation of the screening step itself (running it speculatively, ahead of when its result is strictly needed) is permitted and does not violate this ordering — only *admission ahead of the result* does (`SOURCE-DEFINED`, §43.10's HYBRID-mode carve-out).

### 12.5 Screen → transform → (re-)screen

- **Screen → transform (truncation/pruning only) → use**: a pure truncation/pruning operation can only *remove* content, never introduce new instruction-following surface. Re-screening is not required for this class, since it cannot make already-screened content more dangerous than it was (`PROPOSED METHODOLOGY`, but a direct, low-risk consequence of the fact that removal cannot add content).
- **Screen → transform (compression/summarization) → re-screen → use**: a transformation that *rewrites* content (compression, summarization) can alter its effective instruction-following surface — it could paraphrase away the "obviously suspicious" framing that made the original detectable while preserving the underlying instruction (`EC-186`'s exact scenario: "malicious content engineered specifically to survive compression/summarization"). **Coverage honesty check:** `EC-186` itself is recorded as **`PARTIALLY COVERED`** in `scenario-matrix.md`'s own reconciliation table — `SCN-CMP-083`/`SCN-SEC-007` cover admission-time screening detecting injection *before* admission, but neither directly isolates content specifically engineered to *survive* a downstream compression/summarization step. `EC-207` ("Prompt Injection Interacting With the Compression Pipeline") is not independent corroborating evidence — `edge-cases.md` itself documents `EC-207` as a restatement/cross-reference of `EC-186` for compound-listing completeness, the same semantic gap, not a distinct or dedicated validating scenario. Consequently, the re-screening-after-rewriting rule proposed here has **no dedicated end-to-end scenario validating it** and remains squarely `PROPOSED METHODOLOGY` — not a source-validated mechanism — and this document does not claim otherwise. This document does **not** claim a validated boundary for exactly which transformations qualify as "rewriting" vs. "pure removal" beyond the truncation/pruning vs. compression/summarization distinction above — a genuinely ambiguous transformation (e.g., aggressive reordering) should default to re-screening rather than being assumed safe, per the fail-closed discipline of §3. `PROPOSED METHODOLOGY`.

### 12.6 Honest disposition of `SOURCE-GAP-ARCH-02` (CIS half)

This section proposes a concrete, testable design for CIS's screening mechanism — a layered detection pipeline, a source-uniform application rule, a confidence-to-status mapping, an ordering discipline, and a re-screening trigger rule. **This narrows the architectural design gap by providing a design that can be implemented and benchmarked. It does not close the gap**, because:

- No local classifier exists to measure accuracy, false-positive rate, or false-negative rate.
- No local benchmark corpus of injection/non-injection content exists to validate the confidence-to-status thresholds in §12.3.
- The re-screening boundary in §12.5 is a reasoned proposal, not an empirically validated rule.

This document does not modify `architecture.md` or claim `SOURCE-GAP-ARCH-02` resolved — only `architecture.md` itself, updated with actual implementation/benchmark evidence, could do that. §35's Source Gaps section records this document's own gap (`SOURCE-GAP-SECURITY-01`) tracking exactly this residual empirical-validation need, distinct from and downstream of the upstream `SOURCE-GAP-ARCH-02` it narrows.

---

## 13. TMG Mechanism Deep-Dive

### 13.1 Identity authentication

`authenticate_identity(tool_id, mcp_server_id)` establishes trust in the tool/server's claimed identity before any capability metadata from it is trusted at all — capability declarations from an unauthenticated identity are not evaluated, they are refused outright (`SOURCE-DEFINED` ordering; the exact authentication protocol — API key, mTLS, OAuth2 — is `PROPOSED METHODOLOGY`, deployment-specific, and not prescribed by any source).

### 13.2 Schema-integrity check (`PROPOSED METHODOLOGY — VALIDATE LOCALLY`)

`validate_schema(tool_id, schema, previous_schema_hash)` needs a concrete comparison mechanism. Proposed approach: canonicalize the `ToolDefinition` (stable field ordering, consistent type/format representation — mirroring `cache-strategy.md`'s own canonical-serialization guidance for stable cache prefixes, cited not redefined) and compute a content hash (`schema_hash`) over the canonical form; compare against `previous_schema_hash`. Any hash mismatch is `changed_since_last_call = true`, treated as a staleness/trust event, never silently accepted (`SOURCE-DEFINED` requirement; the specific hash algorithm is unspecified by any source and left `PROPOSED METHODOLOGY` — e.g., any collision-resistant hash function is sufficient for a change-detection use case, no cryptographic-signature strength is implied or required here).

### 13.3 Staleness interval (`PROPOSED DEFAULT — VALIDATE LOCALLY`)

`check_staleness()`'s `revalidation_interval_s` has no source-specified value. A dynamically-discovered tool (DA-007/TE-003) should not be assumed available indefinitely past its last revalidation (`EC-175`). No default interval is proposed as a specific number here, since any number would be pure invention without a workload to validate it against — the correct disposition is `revalidation_interval_s` remains a required, policy-configurable field with no universal default, and this is recorded as `SOURCE-GAP-SECURITY-02` (§35).

### 13.4 Trust decision composition

`trust_decision()` composes, and must not conflate: identity trust (§13.1), schema integrity (§13.2), authorization (delegated to `AuthorizationService.check_tool_access`, INTF-038 — cited not redefined), freshness (§13.3), and runtime capability compatibility. A `TRUSTED` result requires all of these independently, not a weighted average that could let a strong signal on one dimension compensate for a failure on another — this mirrors DGE's `UNKNOWN → SENSITIVE` conservative-default pattern (§8) and SGE's `WITHIN_BUDGET` independence pattern (§7): governance decisions in this Control Plane are conjunctive gates, not blended scores.

### 13.5 ROI independence

TE-001's cost/token-efficiency ROI predictor is evaluated for a *different* question (is this call worth its cost) than TMG's trust decision (is this call/result safe to act on). `EC-174`'s exact scenario — a tool authorized via a favorable ROI signal but TMG's independent check fails — must resolve in TMG's favor: efficiency never substitutes for trust (SEC-014).

### 13.6 TMG does not replace CIS

Restated from §9/§12.2: a `TRUSTED` `ToolTrustResult` establishes the *channel's* trustworthiness, not the *content's*. `GSP-005`/§12 governs the content; `GSP-003`/this section governs the channel. Both are required, independently, for the same tool-result admission decision.

---

## 14. Security-Aware Optimization Admission

`PROPOSED METHODOLOGY`, extending the existing per-request pipeline (`architecture.md` §8) with the security gates this document elaborates, without redesigning the pipeline itself:

```
Resolve Identity / Tenant / Authorization / Policy
        ↓
Resolve Data Classification (DGE)
        ↓
Validate Content Integrity (CIS) — for externally-sourced content
        ↓
Validate Tool / MCP Trust (TMG) — where a tool/MCP call is involved
        ↓
Validate Provider / Model Security Eligibility (§22)
        ↓
Resolve Required Human Approval (HAG) — where policy-designated
        ↓
Establish Security Preconditions
        ↓
Apply ONE Optimization
        ↓
Evaluate Security Impact of the Transformation
        ↓
Revalidate if relevant state changed
        ↓
Security ALLOW / REJECT / QUARANTINE / ESCALATE
        ↓
Only then admit the result downstream
```

Security validation is never replaced by, or averaged against, token/cost savings — a highly efficient optimization that fails a security gate is still rejected.

---

## 15. Per-Optimization Security Validation

Extending `quality-gates.md`'s established per-optimization validation invariant (cited, not redefined — that document already prohibits batch-after-all *quality* validation; this section applies the identical discipline to *security*):

```
Sᵢ
 ↓
Optimization Oᵢ
 ↓
Sᵢ'
 ↓
Security Validation
 ↓
ALLOW / REJECT / QUARANTINE / REVALIDATE / ESCALATE
 ↓
Only the permitted state continues
```

**Prohibited:** `O1 → O2 → O3 → Security Check`, whenever an intermediate optimization could affect authorization, content integrity, data classification, tenant isolation, tool trust, or policy state. A later optimization must not be allowed to mask or launder a security-relevant change an earlier one introduced (mirrors §12.5's re-screening-after-transformation logic, generalized to all security dimensions, not only CIS). The security model and the quality-gate model co-exist as two independently-required checkpoints on the same per-optimization boundary — neither subsumes the other (§24).

---

## 16. Tenant Isolation

`SOURCE-DEFINED` (root `CLAUDE.md` rule 4; `interfaces.md` §21.3's `TenantIsolationContext` — cited, not redefined) applied across every artifact this document's mechanisms touch: request, identity, authorization, context, retrieval results, embeddings, tool/MCP results, model/provider interactions, cache entries, agent memory, sub-agent handoffs, checkpoints, workflow state, optimization/quality/security evidence, provenance, audit records, logs, and metrics.

A cross-tenant cache hit is never a security-valid reuse merely because the cached content is semantically similar to the requesting tenant's query — semantic-similarity cache reuse (`SEMANTIC` cache type, `cache-strategy.md` `CACHE-002`, cited not redefined) requires the same tenant scoping as an exact-match lookup; similarity is a relevance signal, not an authorization signal. Tenant isolation is independent of optimization economics — it is never relaxed because doing so would improve a hit rate or reduce cost.

---

## 17. Cache Security

`cache-strategy.md` remains the sole authority for the seven canonical cache types' key/TTL/economics (`EXACT`, `SEMANTIC`, `TOOL_RESULT`, `EMBEDDING`, `PROVIDER_NATIVE`, `APPLICATION`, `CONTEXT` — cited, not redefined). This section states the security *constraints* that govern reuse across all seven, without touching their architecture:

| Constraint | Requirement |
|---|---|
| Tenant | Cache key includes `tenant_id` as first namespace component (root `CLAUDE.md` rule 4) |
| Authorization | A cache hit is only served if the requester's current authorization still covers the cached content (`SEC-007`) |
| Data classification | `SENSITIVE`/`UNKNOWN`-classified content is not cache-eligible unless policy explicitly permits it (`SEC-004`, DGE's `caching_eligible` field) |
| Freshness/version | A cache entry is invalidated on the policy-version/context-version change that produced it (`CACHE-001`'s `EXACT` type invalidation triggers, cited not redefined) |
| Deletion state | A deletion-requested item's cache entries are surfaces DGE must propagate to (`SEC-013`, `SurfaceHoldRecord`) |
| Provenance | A cache entry's origin (which content, which classification, which screening result) must remain traceable for audit |
| Poisoning | A malicious tool result must not enter the semantic cache un-screened — `EC-208`'s exact scenario, resolved by requiring CIS `PASS` before any cache write for externally-sourced content, not only before context admission |
| Authorization revocation | A revoked authorization invalidates already-cached results derived under the prior authorization, per `EC-202` |

`PROVIDER_NATIVE` caching (`CACHE-005`) remains distinct from EAIOC-owned cache governance — a provider's own prompt-cache infrastructure does not inherit EAIOC's security gates automatically; the Control Plane's own admission decision (§14) still applies before content reaches the point where a provider might cache it.

---

## 18. Agent Memory Security

> **Agent may own working memory, but the Control Plane owns execution truth.** (`SOURCE-DEFINED`, `agent-optimization.md`, cited not redefined — restating `architecture.md` §47.3.1's Memory Authority invariant, H09.)

Security implications: working memory (scratch state, transient observations, local plans), persistent memory, sub-agent handoffs, tool observations, and memory-derived context are never treated as authoritative over ESM/CVM/WVM-owned execution state, authorization state, or policy state. A conflict between agent-memory-claimed state and Control-Plane-owned execution truth is surfaced, never silently resolved in memory's favor (`EC-196`, `EC-197` — ambiguous-provenance memory claims default to stale/untrusted). Security-sensitive memory reuse (e.g., an agent recalling "I was authorized to do X") must be revalidated against current execution state, not trusted from memory alone — this is the same discipline as §5's stateful revalidation requirement, applied specifically to agent memory.

---

## 19. Multi-Agent / Sub-Agent Security

Parent authorization does not automatically authorize every child action — a sub-agent's tool/data access is scoped to what the parent's delegation explicitly grants, least-privilege by default. A sub-agent's result is not trusted merely because the sub-agent belongs to the same EAIOC execution (§12.2's `SUBAGENT_HANDOFF` treatment) — its `SubAgentResultHandoff` (`agent-optimization.md` `AL-005`, cited not redefined) still passes through CIS. A compromised or failed child's partial results are subject to the same admission gates as any other untrusted content before the parent incorporates them. Supersession/cancellation of a child execution must not allow that child's already-dispatched security-sensitive side effects to continue unchecked (§20's Recovery/Supersession discipline, applied here).

---

## 20. Coding/Developer-Agent Security

Repository/file access, secrets handling, command execution, external network access, generated code/patches, and destructive operations (branch/worktree state, dependency changes) are Control-Plane security *decisions and boundaries* this document defines conceptually — not a sandbox implementation, which belongs to the actual runtime, not to any EAIOC documentation artifact. Untrusted repository content (e.g., a fetched dependency, an untrusted file read into context) is subject to the same CIS screening as any other externally-sourced content (§12.2) before it influences agent planning. Destructive or irreversible repository operations (force-push, hard reset, branch deletion) are natural `HAG`-designated-action candidates (§10) at the deployment's own policy discretion — this document does not assert a universal designated-action list, consistent with §10's own statement that the list is a policy decision.

---

## 21. RAG Security

Retrieved content is not trusted merely because the retrieval operation itself was authorized — authorization to *search* a data source is a different question from whether a specific *retrieved chunk* is safe to admit (§12.2's `RAG_CHUNK` treatment). Retrieval poisoning (a malicious document deliberately placed to be retrieved and to carry an injection payload) is exactly the threat `EC-185`/`SCN-SEC-007` validate against. Citation/content provenance, stale permissions on a previously-indexed source, and cache reuse of retrieval results all inherit the same tenant/classification/screening gates as any other content admission (§14, §16–17).

---

## 22. Provider/Model Security

`provider-matrix.md` remains the sole authority for provider/model capability and accessibility facts (cited, not redefined), including its six-state Provider Accessibility Model: **Discovered ≠ Accessible ≠ Available ≠ Authorized ≠ Recommended ≠ Eligible**. Security-relevant consequence of that model: a provider being *discovered* or *accessible* never implies it is *authorized* for a given tenant's data-residency or compliance requirements — routing must confirm `Authorized`/`Eligible` status specifically, not infer it from a weaker state in the chain. Security constraints (residency, authorization) must survive a provider/model switch during recovery (`EC-168`, `EC-200`, `EC-205`) — a fallback provider is not automatically eligible merely because the primary one failed. A provider's own native content-safety filter (where one exists) is complementary to, and never a substitute for, CIS's independent screening (§12) unless a future authoritative document explicitly declares equivalence — this document does not make that declaration.

---

## 23. Human Approval and Security

Restated from §10/§4: low cost, high quality-gate score, low latency, high model confidence, or established tool trust are never substitutes for a policy-designated required approval. An approval is linked to the specific action/state version it was granted for (§10's Revalidation Trigger) precisely to prevent stale-approval reuse — an approval for "delete file X under policy version 3" does not carry over to executing that deletion under policy version 4 without re-evaluation.

---

## 24. Quality and Security Boundary

Restated explicitly, since it is the boundary most at risk of accidental conflation:

- The **Quality Gate** (`quality-gates.md`, cited not redefined) determines whether an optimization's *result* meets quality requirements (accuracy, factuality, schema compliance, etc.).
- **Security** (this document) determines whether the *operation, content, or state* is security-valid (authorized, correctly classified, integrity-screened, approved where required).
- A quality `PASS` never overrides a security failure — a factually-perfect compressed answer derived from unauthorized data is still rejected.
- A security `PASS` never replaces quality validation — an authorized, correctly-classified, integrity-screened optimization can still fail its quality gate on factuality/schema/other grounds.
- Both gates may be required simultaneously on the same optimization application, per §15's per-optimization validation boundary; neither is a superset of the other.

---

## 25. Recovery and Supersession

> **Resume != Replay.** (`SOURCE-DEFINED`, cited from the established Dynamic Execution Architecture invariant, `architecture.md` §46.)

On recovery, security-relevant state is reconciled, not blindly replayed: identity, tenant, authorization, policy, data classification, tool trust, content-integrity screening results, approval state, and provider/model eligibility are all re-checked against their *current* values, not assumed unchanged since checkpoint creation. If authorization was revoked after checkpoint creation, the checkpoint cannot simply resume unchanged (`EC-181`-adjacent reasoning applied to the general recovery case). If an execution becomes superseded, it must not continue unwanted security-sensitive side effects — a superseded execution's pending tool calls, pending approvals, or pending cache writes must be cancelled or revalidated, not silently completed (`EC-210`, `EC-213`).

---

## 26. Deletion/Erasure Propagation

Restated concretely from `GSP-002`/§8: deletion/erasure propagates across every EAIOC-controlled surface the data was persisted to — the seven `DataSurface` values (`CACHE_EXACT`, `CACHE_SEMANTIC`, `MEMORY`, `LEDGER`, `LOG`, `TRACE`, `CHECKPOINT`) — and propagation status is independently verifiable via `get_deletion_propagation_status()`, not merely asserted (`SCN-SEC-006`). A critical boundary this document makes explicit: EAIOC-controlled storage is distinguishable from external/provider-controlled storage, and this document does not claim EAIOC can delete data from an external provider's own systems unless the actual integration contract with that provider explicitly supports it — that is a `provider-matrix.md`-adjacent fact this document does not assert without evidence.

**Audit-record exception (reconciling with §27/§29 Algorithm J):** `AuditRecord` (`interfaces.md` §19.5) is an immutable audit surface under the existing interface/convention contract — "Audit records are immutable and may not be deleted" — and is deliberately *not* one of the seven `DataSurface` values above; `DataSurface` is not extended with an `AUDIT` member by this document. SEC-013 deletion propagation therefore applies to the seven DGE-defined `DataSurface` values, while audit records retain their mandated immutability and configured retention behavior independently of any deletion/erasure request. This is not a contradiction: `AuditRecord.details` is itself specified as non-sensitive with PII already excluded (`interfaces.md` §19.5), so sensitive content is kept out of the audit surface by the existing PII/data-safety controls (DGE classification, §8; `PIIClassifier` redaction, §21.4) *before* it would ever be recorded, rather than being deleted from the audit surface after the fact.

---

## 27. Observability and Audit Boundary

Cited, not built: `interfaces.md` §19.5's `AuditRecord` and `architecture.md` §47.16's Decision Explainability requirement (H15). Every `GSP-NNN` component's decision (screen/trust/approve/classify/throttle) must be recordable in an `AuditRecord` with `policy_version` and rationale, so that "why was this optimization/action allowed or rejected, which requirement applied, what evidence was used, was it current, what changed, why was escalation/approval triggered" is answerable after the fact. Detailed telemetry/dashboard implementation belongs to `observability.md` (not yet generated) — this document does not build that.

---

## 28. Security Control Catalog (`SC-NNN`)

A document-local, non-authoritative catalog distinct from the canonical `SEC-NNN` requirements — each `SC-NNN` row operationalizes one or more `SEC-NNN`/`GSP-NNN` combinations into a named, checkable control. Distinguished explicitly as `SOURCE-DERIVED` (a direct restatement of an upstream requirement as a control) vs. `PROPOSED METHODOLOGY` (a new mechanism this document introduces).

| ID | Control | Applicable `GSP` | Trigger | Failure Behavior | Status |
|---|---|---|---|---|---|
| `SC-001` | Budget-evaluation independence from authorization | `GSP-001` | Every cost-incurring action | Deny/degrade to safe default, never `WITHIN_BUDGET` by default | `SOURCE-DERIVED` |
| `SC-002` | Runaway-cost pre-limit detection | `GSP-001` | Continuous, per scope | Throttle/halt affected scope only | `SOURCE-DERIVED` |
| `SC-003` | Default-sensitive classification on failure | `GSP-002` | `classify()` failure/timeout | Exclude from caching/compression until classified | `SOURCE-DERIVED` |
| `SC-004` | Verifiable deletion propagation | `GSP-002` | Deletion/erasure request | Report pending surfaces with reason, never silently cleared | `SOURCE-DERIVED` |
| `SC-005` | Schema-hash integrity check | `GSP-003` | Every tool/MCP call | `SCHEMA_STALE` → staleness/trust event | `PROPOSED METHODOLOGY` (hash mechanism) |
| `SC-006` | ROI-independent trust decision | `GSP-003` | Every tool call evaluation | Trust failure blocks call regardless of ROI | `SOURCE-DERIVED` |
| `SC-007` | Approval-mechanism-unavailable fail-closed | `GSP-004` | Approval check on a designated action | Block the gated action | `SOURCE-DERIVED` |
| `SC-008` | Stale-approval revalidation on state change | `GSP-004` | Policy/context version change post-grant | Re-evaluate before honoring the approval | `SOURCE-DERIVED` |
| `SC-009` | Layered injection detection | `GSP-005` | Every `ExternalContentItem` | `REJECT`/`QUARANTINE` per §12.3 mapping | `PROPOSED METHODOLOGY` |
| `SC-010` | Screening-unavailable fail-closed | `GSP-005` | `screen()` cannot complete | `REJECT`/`QUARANTINE`, never silent admission | `SOURCE-DERIVED` |
| `SC-011` | Screening-ordering enforcement | `GSP-005` | Any admission/rank/compress/cache action | Block admission until `PASS` | `SOURCE-DERIVED` |
| `SC-012` | Re-screening after rewriting transformation | `GSP-005` | Compression/summarization of screened content | Re-screen before further use | `PROPOSED METHODOLOGY` |
| `SC-013` | TMG-CIS independence (dual gate) | `GSP-003` + `GSP-005` | Any tool/MCP result admission | Both checks required; neither substitutes for the other | `SOURCE-DERIVED` |
| `SC-014` | Cross-tenant cache-reuse denial | All | Every cache lookup | Deny reuse outside requesting tenant | `SOURCE-DERIVED` |
| `SC-015` | Quality/security dual-gate independence | `GSP-002`/`003`/`005` interacting with quality-gates.md | Any optimization with both gates applicable | Neither `PASS` substitutes for the other | `SOURCE-DERIVED` |

---

## 29. Security Algorithms (Control-Plane, conceptual)

All algorithms below are Level 0 conceptual decision logic — no implementation classes, APIs, databases, or deployment assumptions.

**Algorithm A — Resolve Security Context.** Resolve identity (`AuthenticationContext`), tenant, and applicable policy (`OptimizationPolicy`) before any further security evaluation proceeds. Output: a resolved security context or an explicit resolution failure (fail-closed).

**Algorithm B — Classify Data Before Optimization.** For content entering T0/T1: call DGE `classify()`; on success, gate caching/compression/retrieval eligibility per `SensitivityClassificationResult`; on failure, default `SENSITIVE` (§8).

**Algorithm C — Security Admission.** Given a resolved security context and classification, determine ALLOW/REJECT/QUARANTINE/ESCALATE for the pending execution/optimization step, composing (not replacing) Algorithms D–F/K as applicable.

**Algorithm D — Validate Content Integrity.** For each `ExternalContentItem`: run the §12.1 layered pipeline; map to `ScreeningResult.status` per §12.3; on `SCREENING_UNAVAILABLE`, fail closed per §11's Restated Failure Behavior.

**Algorithm E — Validate Tool/MCP Trust.** For each tool/MCP call: `authenticate_identity` → `validate_schema` → `check_staleness` → `trust_decision`, conjunctively (§13.4); independent of TE-001's ROI signal (§13.5). **SOURCE-DEFINED REQUIREMENT:** all four checks are required and none may be substituted or averaged away (§13.4, SEC-014). **PROPOSED METHODOLOGY:** the specific left-to-right calling sequence shown above is this document's proposed realization — `interfaces.md` §43.3 defines the four methods but does not itself mandate this exact invocation order.

**Algorithm F — Validate Provider/Model Security Eligibility.** Resolve provider/model eligibility (residency, authorization) independently from `provider-matrix.md`'s Recommended/economics dimensions (§22); a routing decision must confirm `Authorized`/`Eligible`, not infer it from `Discovered`/`Accessible`.

**Algorithm G — Security-Aware Cache Reuse.** Before serving any cache hit: validate tenant match, current authorization, current policy, freshness/version, classification eligibility, deletion state, and provenance (§17); any failure denies the reuse and falls through to the uncached path (not a security failure of the request itself, since the request can still proceed without the cache hit).

**Algorithm H — Security Revalidation.** Triggered by any state change in §5's list: re-run the applicable subset of Algorithms A–G against current state; a previously-ALLOW decision made under now-stale state is not honored until revalidated.

**Algorithm I — Security Failure/Recovery.** Given a security failure classification: deny, quarantine, pause (awaiting revalidation/approval), restore to last known-safe state, escalate, or abort safely — selection governed by the specific `GSP-NNN`'s own Fail-Closed Behavior and Recovery Behavior fields (§7–11), never a generic "retry and hope."

**Algorithm J — Security Audit Record.** For every Algorithm C/D/E/F/G/H/I decision: emit an `AuditRecord` (§27) with `policy_version`, `outcome`, and enough context to answer "why" — immutable, never deleted (`interfaces.md` §19.5).

**Algorithm K — Human Approval Resolution.** For an action with `requires_approval() = true`: suspend pending resolution (checkpoint via CPM); on resolution, re-check current policy/context version before honoring it (§10); on timeout/unavailability, apply the policy-configured deny/escalate default.

**Algorithm L — Security-Aware Deletion Propagation.** For a deletion/erasure request: propagate across all seven `DataSurface` values; report `surfaces_cleared`/`surfaces_pending`/`surfaces_holding_data` with reasons; distinguish EAIOC-controlled from external/provider-controlled surfaces (§26); mark `verified = true` only when propagation is independently confirmed, not merely dispatched. `AuditRecord` is out of scope for this algorithm's propagation target set — it is not a `DataSurface` value and remains immutable regardless of any deletion/erasure request (§26's Audit-record exception; Algorithm J).

---

## 30. Required Security Matrices

### Matrix 1 — Security Control Catalog

See §28 (`SC-001`–`015`).

### Matrix 2 — Threat × Security Control

| Threat | Primary Control(s) | Source-Defined? |
|---|---|---|
| Prompt injection (direct) | `SC-009`, `SC-011` | `SOURCE-DEFINED` (EC-057, SEC-016) |
| Indirect/evasive prompt injection | `SC-009` (layered detection) | `SOURCE-DEFINED` threat (EC-185); detection layering `PROPOSED METHODOLOGY` |
| Malicious retrieved content (RAG poisoning) | `SC-009`, `SC-011` | `SOURCE-DEFINED` (SCN-SEC-007) |
| Malicious tool output | `SC-009`, `SC-013` | `SOURCE-DEFINED` (EC-057 analogue, SCN-SEC-002) |
| MCP content carrying injection despite trusted server | `SC-013` | `SOURCE-DEFINED` (EC-187, SCN-SEC-009) |
| Schema tampering / stale tool metadata | `SC-005` | `SOURCE-DEFINED` (EC-173, EC-176) |
| Privilege escalation via crafted argument | Authorization layer (`AuthorizationService`, cited) | `SOURCE-DEFINED` (SCN-SEC-004) |
| Authorization revocation mid-flight | `SC-008`, Algorithm H | `SOURCE-DEFINED` (EC-202-adjacent) |
| Tenant leakage | `SC-014` | `SOURCE-DEFINED` (root `CLAUDE.md` rule 4) |
| Cache poisoning | `SC-009` (pre-write screening), §17 | `SOURCE-DEFINED` (EC-208) |
| Memory poisoning / stale memory claims | §18 (Memory Authority) | `SOURCE-DEFINED` (EC-196/197) |
| Sensitive-data leakage | `SC-003`, §16–17 | `SOURCE-DEFINED` (EC-058/059) |
| Deletion failure/incomplete propagation | `SC-004` | `SOURCE-DEFINED` (EC-167/171) |
| Stale checkpoint / resume-as-replay | Algorithm H, §25 | `SOURCE-DEFINED` |
| Stale approval reuse | `SC-008` | `SOURCE-DEFINED` (EC-181/182) |
| Provider/model security mismatch on failover | Algorithm F, §22 | `SOURCE-DEFINED` (EC-168/200/205) |

### Matrix 3 — Optimization × Security

| Optimization category | Security gate(s) applicable |
|---|---|
| Context optimization (pruning/dedup/compression/reorder) | DGE classification precedes; CIS re-screening rule (§12.5) where the transformation rewrites |
| Retrieval optimization | CIS screening of retrieved chunks (§21) |
| Caching (all 7 canonical types) | §17's cache-security constraint table |
| Tool/MCP optimization | TMG + CIS, independently (§13.6) |
| Agent/sub-agent optimization | §18–19 (memory authority, sub-agent handoff screening) |
| Model/provider routing | Algorithm F (§22) |
| Reasoning/output optimization | No direct security gate beyond the standard admission chain (§14) — reasoning/output budgets are quality/cost concerns, not security-differentiated |
| Inference/serving (P5) optimization | No direct security gate beyond standard admission — P5 is a Control-Plane awareness/negotiation layer (`inference-optimization.md`, cited), not a new security surface |

### Matrix 4 — Data Classification × Allowed Transformation

`SOURCE-DEFINED` core rule, `PROPOSED METHODOLOGY` for the full transformation-by-transformation breakdown (no source enumerates this exhaustively):

| Classification | Caching | Compression/Pruning | Embedding | Provider Transmission |
|---|---|---|---|---|
| `NON_SENSITIVE` | Eligible per policy | Permitted | Permitted | Permitted per provider eligibility (§22) |
| `SENSITIVE` | Only if policy explicitly permits (`SEC-004`) | Permitted subject to compression-contract security-invariant preservation (`quality-gates.md`'s Coding-row `SEC-005` cross-reference, cited) | Only if policy explicitly permits | Only to providers meeting residency/eligibility requirements |
| `UNKNOWN` | Not eligible (defaults to `SENSITIVE`'s restrictions) | Not eligible until classified | Not eligible until classified | Not eligible until classified |

### Matrix 5 — Security Failure/Recovery

| Failure | Detection | Safe Response | Recovery |
|---|---|---|---|
| SGE indeterminate | `evaluate_budget()` cannot resolve | `THROTTLED`/`HALTED` default | Retry; escalate to policy-configured fallback |
| Authorization unavailable | `AuthorizationService` timeout/error | Deny (fail-closed) | Retry; queue for manual review if persistent |
| DGE classification unavailable | `classify()` failure | Default `SENSITIVE` | Retry classification; content remains restricted until resolved |
| TMG identity/schema failure | `authenticate_identity`/`validate_schema` failure | `QUARANTINED` | Re-authenticate; re-validate schema; no fallback execution |
| HAG unavailable | Approval mechanism unreachable | Block gated action | Retry; escalate to alternate approver per policy |
| CIS unavailable | `screen()` timeout/error | `REJECT`/`QUARANTINE` | Retry; proceed without optional content, reject request if content mandatory |
| Stale security state at resume | Version mismatch detected (Algorithm H) | Revalidate before honoring prior decision | Re-run applicable Algorithm A–G subset |
| Policy change mid-flight | Policy-version mismatch | Re-evaluate against new policy version | Apply new policy; do not grandfather the in-flight decision |
| Authorization revocation mid-flight | Revocation event | Invalidate dependent cached/approved decisions | Re-authorize before continuing |
| Tenant mismatch detected | Cross-tenant reference found | Deny | Investigate as a defect, not a recoverable runtime condition |
| Deletion request during active checkpoint | Checkpoint holds the subject's data | Report `surfaces_pending` with reason | Complete propagation once checkpoint clears or is itself deleted |
| Checkpoint security mismatch on resume | Authorization/policy differs from checkpoint time | Re-evaluate before resuming | Resume only after revalidation passes |

### Matrix 6 — Security State/Revalidation

| State change | Revalidation required for |
|---|---|
| Identity/permission change | Algorithm A, all downstream gates |
| Tenant reassignment | `SC-014`, all tenant-scoped decisions |
| Policy version change | Algorithm H generally; `SC-008` specifically for pending approvals |
| Context/workflow version change | CIG/CVM/WVM-level reconciliation (`architecture.md` §46, cited), which this document's security gates consume as an input |
| Tool/MCP schema/version change | `SC-005`, `SC-006` |
| Model/provider change | Algorithm F |
| Data-classification change | `SC-003`-adjacent re-classification |
| Deletion state change | `SC-004`, §26 |
| Approval state change (revoked/expired) | `SC-008` |

### Matrix 7 — Tenant Isolation

| Artifact | Isolation requirement |
|---|---|
| Context/retrieval results | `tenant_id`-scoped lookup, never cross-tenant blending |
| Cache (all 7 types) | `tenant_id` first namespace component (root `CLAUDE.md` rule 4) |
| Agent/session memory | Never retrieved cross-tenant |
| Tool/provider credentials | Isolated per tenant where supported |
| Checkpoints | Scoped per tenant/execution |
| Logs/metrics/audit | Segmented by `tenant_id`; no cross-tenant aggregation |
| Evidence (quality/security) | Never reused across tenants without explicit, policy-permitted sharing (not asserted to exist by default) |

### Matrix 8 — Security Traceability

| `SEC-NNN` | `GSP-NNN` | Interface | Edge Case | Scenario | Objective/AC |
|---|---|---|---|---|---|
| SEC-011 | GSP-001 | INTF-063 | EC-156–161 | `NO DEDICATED SCENARIO` (see §7) | OBJ-027 / AC-043 |
| SEC-012 | GSP-002 | INTF-064 | EC-166, EC-170, EC-212 | SCN-SEC-005 | OBJ-029 / AC-045 |
| SEC-013 | GSP-002 | INTF-064 | EC-167, EC-171 | SCN-SEC-006 | OBJ-029 / AC-045 |
| SEC-014 | GSP-003 | INTF-065 | EC-172–176 | `THIN COVERAGE` (see §9) | AC-048 |
| SEC-015 | GSP-004 | INTF-066 | EC-177–182 | `NO DEDICATED SCENARIO` (see §10) | OBJ-033 / AC-050 |
| SEC-016 | GSP-005 | INTF-067 | EC-057, EC-183–187, EC-207, EC-208 | SCN-SEC-007, SCN-SEC-008, SCN-SEC-009 | OBJ-034 / AC-051 |

---

## 31. Threat/Risk Methodology

Threats in Matrix 2 (§30) are either `SOURCE-DEFINED` (named directly by an `EC-NNN`/`SCN-SEC-NNN`) or `SOURCE-DERIVED` (a direct logical consequence of a named `SEC-NNN` requirement, e.g., "tenant leakage" derived from root `CLAUDE.md` rule 4's absolute-isolation requirement). No probability, severity, or risk score is invented for any threat beyond the qualitative Severity/Likelihood fields already present in `edge-cases.md`'s own entries (cited, not restated). This document does not introduce a numeric risk-scoring model, since none is source-defined and inventing one would misrepresent unvalidated judgment as a measured risk figure.

---

## 32. Anti-Patterns

`SOURCE-DERIVED` list, consistent with `conventions.md` §24's existing anti-pattern discipline and this document's own precedence/independence invariants:

1. Security fallback treated as an optimization fallback (conflating §3's two failure categories).
2. Security failure silently converted to `PASS`.
3. Authorization inferred from budget approval (`WITHIN_BUDGET` ⇒ authorized).
4. Trust inferred from token efficiency (low-cost tool call ⇒ trusted).
5. Provider accessibility treated as authorization (`Accessible` ⇒ `Authorized`).
6. Quality `PASS` overriding a security failure.
7. Human approval bypassed because cost/latency is favorable.
8. TMG trust treated as replacing CIS screening.
9. CIS screening treated as replacing TMG trust.
10. Screening applied only to direct end-user input, exempting the other four `ExternalContentItem.source` values.
11. Retrieved content treated as trusted merely because retrieval itself was authorized.
12. Tool output automatically treated as trusted because the call was authorized.
13. MCP output exempted from screening because the server is `TRUSTED`.
14. Sub-agent output automatically trusted because it is internal to the same execution.
15. Compression/summarization assumed to remove injected instructions without re-screening.
16. Screening performed only after optimization/admission rather than before.
17. Batch validation after several security-sensitive transformations (§15's prohibited flow).
18. A failed/quarantined security state propagated downstream anyway.
19. Stale authorization reused across a state change without revalidation.
20. Stale approval reused across a policy/context-version change.
21. Cross-tenant cache reuse.
22. Sensitive data cached without classification/policy permission.
23. Deleted data remaining reachable in a derived artifact (cache, memory, checkpoint) without being reported as `surfaces_holding_data`.
24. Agent-owned memory treated as execution truth.
25. Checkpoint resumed without security reconciliation (treating Resume as Replay).
26. A superseded execution continuing unwanted security-sensitive side effects.
27. Security controls (SGE/DGE/TMG/HAG/CIS) shed under backpressure.
28. Provider-native safety filtering treated as the sole security layer, replacing CIS.
29. A proposed security threshold/interval presented as validated without local evidence.
30. External research presented as local validation (violates root `CLAUDE.md` rule 7/H19).
31. An invented `EC`/`SCN`/`SEC`/`INTF` identifier.
32. A security decision made without a recorded, auditable rationale.

---

## 33. Level 0 Validation Strategy

Because this document is Level 0 — RESEARCH/PRE-IMPLEMENTATION, validation here means: source consistency (every restated requirement matches its source verbatim or is marked as a derived consequence), architectural consistency (no `GSP-NNN` mechanism contradicts its `architecture.md` §47.2 origin), scenario/edge-case coverage (verified IDs only, honest `THIN`/`NO DEDICATED SCENARIO` labeling where coverage is genuinely thin), requirement traceability (Matrix 8), contradiction detection (§35), mechanism completeness (all 5 `GSP-NNN` entries, CIS/TMG deep-dives), state/revalidation completeness (§5, §25), failure-mode completeness (Matrix 5), tenant-isolation completeness (Matrix 7), and cross-document boundary integrity (§34). This document does not and cannot claim production validation, classifier accuracy, benchmark performance, penetration-test results, or measured false-positive/false-negative rates — none of that evidence exists in this repository.

---

## 34. Cross-Document Boundary Discipline

| Concern | Owner |
|---|---|
| Security requirements (`SEC-NNN`) and abstract component purpose/failure behavior | `problemStatement.txt` / `architecture.md` §29, §47.2 |
| Security/governance interface schemas | `interfaces.md` §21, §43.1–43.5 |
| Security/governance conventions and package layout | `conventions.md` |
| Concrete SGE/DGE/TMG/HAG/CIS mechanisms (CIS/TMG deepest) | **This document** |
| Provider/model facts, accessibility model, FTR tier assignments | `provider-matrix.md` |
| Cache architecture/key/TTL/economics (seven canonical types) | `cache-strategy.md` |
| Agent-loop algorithms, sub-agent handoff schema | `agent-optimization.md` |
| P5/Layer-3 capability negotiation | `inference-optimization.md` |
| Quality-gate methodology, verifier calibration | `quality-gates.md` |
| Benchmark-corpus construction, A/B statistical methodology (including any future CIS-classifier accuracy benchmark) | `eval.md` (not yet generated) |
| Telemetry/dashboard implementation | `observability.md` (not yet generated) |
| Capacity/scaling | `SCALING.md` (not yet generated) |
| Canonical cross-document requirements traceability | `requirements-traceability.md` (not yet generated — not created by this document) |
| Architectural decision records | ADRs (not yet generated) |

---

## 35. Source Gaps and Contradictions

### Source Gaps (`SOURCE-GAP-SECURITY-NN`)

| ID | Gap | Affected Section | Blocking? |
|---|---|---|---|
| `SOURCE-GAP-SECURITY-01` | CIS's proposed layered detection mechanism (§12) has no local classifier, benchmark corpus, or measured accuracy/false-positive/false-negative evidence. This is the residual empirical-validation need `SOURCE-GAP-ARCH-02`'s CIS half is narrowed to, not closed to (§12.6). | §12 | Non-blocking for this document's own readiness (a design gap, honestly disclosed); blocking for any future claim that CIS is production-validated |
| `SOURCE-GAP-SECURITY-02` | TMG's `revalidation_interval_s` (staleness check) has no source-specified default value; no workload-derived number is proposed here since none could be validated locally (§13.3). | §13.3 | Non-blocking — the field remains policy-configurable with no universal default |
| `SOURCE-GAP-SECURITY-03` | No source specifies an exhaustive rule for which content-rewriting transformations require CIS re-screening beyond the truncation/pruning-vs-compression/summarization distinction in §12.5; ambiguous cases (e.g., aggressive reordering) are handled by defaulting to re-screening, but this default itself is `PROPOSED METHODOLOGY`, not source-derived. | §12.5 | Non-blocking |
| `SOURCE-GAP-SECURITY-04` (inherited) | `SOURCE-GAP-ARCH-01` (no specific regulatory regime asserted) is inherited, not re-opened — this document's data-classification treatment (§8, Matrix 4) deliberately does not assert GDPR/HIPAA/PCI-DSS applicability, consistent with the upstream gap's disposition. | §8, Matrix 4 | Non-blocking, inherited |

### Source Contradictions (`CONTRA-SECURITY-NN`)

None found. Every cross-reference checked during this generation (SGE↔authorization independence, TMG↔CIS independence, DGE's `UNKNOWN→SENSITIVE` default, HAG's fail-closed timeout default, CIS's fail-closed screening-unavailable default) was internally consistent across `architecture.md` §47.2, `interfaces.md` §43.1–43.5, and `scenario-matrix.md`'s `SCN-SEC-*` domain — no upstream document was found to disagree with another on any point this document elaborates.

---

## 36. Final Report

- **Document status:** Generated, Level 0 — RESEARCH / PRE-IMPLEMENTATION.
- **`GSP-NNN` entry count:** 5/5 (`GSP-001`–`005`), in `architecture.md` §47.2's order, each with all 22 required fields (§6 of the generation prompt).
- **`SC-NNN` security-control catalog:** 15 entries (§28).
- **Algorithms:** 12/12 (A–L, §29), all Control-Plane-conceptual, no infrastructure/implementation leakage.
- **Matrices:** 8/8 (§30).
- **Scenario coverage / attribution:** all 9 `SCN-SEC-001`–`009` scenarios are cited where they provide security-domain or hardening-era validation evidence, but they are **not uniformly GSP-owned** — verified against `scenario-matrix.md`'s own canonical Hardening-component-ownership table (its `DGE`/`TMG`/`CIS` rows), which lists only `SCN-SEC-005`/`006` under DGE and `SCN-SEC-007`/`008`/`009` under CIS (with `009` also under TMG as the compound CIS×TMG boundary case). `SCN-SEC-001`–`004` are broader, original (pre-hardening) security-domain scenarios validating `SEC-001`/`SEC-006`/`SEC-004`/`SEC-002` respectively and are **not individually owned by any `GSP-NNN`** in that canonical table — they are cited in this document as adjacent/supporting evidence (e.g., §22's threat matrix, §9's TMG entry), not as a `GSP`-owned validation. SGE and HAG have **no dedicated `SCN-SEC-NNN` scenario** — honestly disclosed in §7/§10 rather than force-fit to an adjacent scenario. TMG's identity/schema mechanics specifically are **`THIN COVERAGE`** in the `SCN-SEC-*` domain (only the compound CIS×TMG scenario is dedicated) — disclosed in §9. Scenario ownership/validation relationships are not inferred beyond what `scenario-matrix.md` itself establishes.
- **Edge-case coverage:** 30+ distinct `EC-NNN` citations verified against the actual current `edge-cases.md` (EC-057, EC-097, EC-156–182, EC-183–187, EC-196–197, EC-200, EC-202, EC-205, EC-207–208, EC-210, EC-212–213), none fabricated.
- **Requirement coverage:** SEC-011–016 (6/6) mapped to their owning `GSP-NNN` in Matrix 8; OBJ-027/029/033/034 and AC-043/045/048/050/051 cited.
- **Source-gap count:** 4 (`SOURCE-GAP-SECURITY-01`–`04`, one inherited), all non-blocking.
- **Contradiction count:** 0.
- **`SOURCE-GAP-ARCH-02` disposition:** **narrowed, not closed** — this document supplies a concrete, testable CIS mechanism design (§12) and elaborates TMG's schema-integrity mechanism (§13), but explicitly does not claim empirical validation, benchmark accuracy, or production closure, since no local classifier/benchmark evidence exists (§12.6). `architecture.md` itself remains the only document authorized to mark `SOURCE-GAP-ARCH-02` resolved, and this document does not attempt that.
- **Fail-open/fail-closed distinction:** preserved throughout (§3) and never contradicted by §14/§15's optimization-admission integration.
- **Security/optimization/quality precedence:** preserved (§4, §24).
- **Tenant-isolation confirmation:** preserved throughout (§16, Matrix 7).
- **File-scope confirmation:** only `docs/security.md` was created by this generation; no upstream or downstream document was modified.

**Targeted verification corrections applied (surgical fix pass, `docs/security.md` only):**
1. Scenario attribution corrected — `SCN-SEC-001`–`009` are not treated as uniformly `GSP`-owned; `SCN-SEC-001`–`004` are honestly disclosed as broader/original security-domain scenarios with no individual `GSP-NNN` owner in `scenario-matrix.md`'s canonical table (§36's Scenario coverage bullet, above).
2. `EC-186` coverage corrected — represented as **`PARTIALLY COVERED`** (via `SCN-CMP-083`/`SCN-SEC-007`, neither isolating the survive-compression case directly); `EC-207` correctly identified as `edge-cases.md`'s own self-documented duplicate/restatement of `EC-186`, not independent dedicated coverage; the post-transformation re-screening rule remains explicitly `PROPOSED METHODOLOGY` with no dedicated validating scenario (§11's Validated By field, §12.5).
3. `AuditRecord` immutability explicitly reconciled with SEC-013 deletion propagation — a new "Audit-record exception" clarification added to §26 and cross-referenced from Algorithm L (§29): `AuditRecord` is not a `DataSurface` value, is never deleted, and sensitive content is kept out of it by existing PII/classification controls applied before recording, not by post-hoc deletion.
4. `SOURCE-DEFINED` vs `PROPOSED METHODOLOGY` terminology tightened at one genuine spot — Algorithm E (§29) now distinguishes the source-defined requirement (all four TMG checks are mandatory and non-substitutable) from this document's proposed specific calling sequence (not itself mandated by `interfaces.md` §43.3).

No other content was added, removed, or restructured by this correction pass; all previously-verified sections (GSP-001–005, Algorithms A–L, all 8 matrices, the 7 `DataSurface` values, the security precedence model, and the fail-open/fail-closed distinction) are unchanged.

---

## Security Readiness

READY FOR NEXT DOCUMENTATION PHASE
