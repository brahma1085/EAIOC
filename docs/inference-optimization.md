# Inference / Serving Optimization Layer (P5) — Control-Plane Elaboration

**Status:** PRE-IMPLEMENTATION — documentation-phase deliverable, Level 0 (RESEARCH) throughout, no local benchmark evidence.
**Version:** 1.0.0
**Generated:** 2026-09-17
**Document role:** Fifth document in the `docs/prompts/generate-*.prompt.md` chain — `optimization-catalog.md` → `provider-matrix.md` → `cache-strategy.md` → `agent-optimization.md` → **`inference-optimization.md`** → (next) `quality-gates.md`.
**Generated from:** `docs/prompts/generate-inference-optimization.prompt.md` ("V3 CONSOLIDATED MASTER PROMPT").

## 1. Purpose and Scope

This document is the canonical **Control-Plane elaboration of the 11 P5 (Layer 3) inference/serving capabilities** that `optimization-catalog.md`'s P5 Index deliberately left at index level (`optimization-catalog.md` §"P5 Index: Inference/Serving Capabilities", each stub reading only `Control-Plane Role / Deferred To / Source`). It exists to answer, for each of the 11 capabilities, exactly one question in depth: *what does the Control Plane itself do* — detect, route/select, negotiate, measure — never *how does the underlying inference engine work*.

**In scope:** Control-Plane-side detection logic, routing/selection policy, provider-adapter negotiation parameters, net-value accounting, measurement-separation enforcement, fallback/recovery behavior, and security/governance constraints for each of the 11 capabilities.

**Out of scope (see §4):** any implementation detail of the underlying inference-serving mechanism itself — KV-cache engine internals, continuous-batching scheduler algorithms, speculative-decoding draft/verify mechanics, quantization numerics, GPU-kernel/utilization formulas, prefill/decode engine internals, or inference-queue implementation. These are provider/inference-infrastructure-owned and are never designed here.

## 2. How to Read This Document

- **Evidence tags** (used throughout, per root `CLAUDE.md` rule 5/7 and `architecture.md` §47.12/H19): `PROPOSED DEFAULT — VALIDATE LOCALLY`, `PROPOSED METHODOLOGY`, `ILLUSTRATIVE — NOT VERIFIED`, `SOURCE-GAP`. No concrete number in this document is a production guarantee.
- **Maturity:** every `INFOPT-NNN` entry is Level 0 — RESEARCH (`architecture.md` §42's maturity model), consistent with every other document in this chain — this repository is pre-implementation with zero local benchmark evidence.
- **Structured fields:** each `INFOPT-NNN` entry uses one labeled field per table row, matching this repository's established structured-document convention (see `optimization-catalog.md`'s `TECH-NNN`/`DA-NNN` entries, `cache-strategy.md`'s `CACHE-NNN` entries, `edge-cases.md`'s `EC-NNN` entries).
- **Citation discipline:** where a fact belongs to `provider-matrix.md`, `cache-strategy.md`, or `agent-optimization.md`, this document cites the exact section rather than restating it — restating a fact that later drifts from its owning document is exactly the failure mode `CLAUDE.md`'s "Reconciliation passes are single-file-scoped" rule and the Cross-Document Coverage discipline established by the prior four documents exist to prevent.

## 3. Authoritative Sources Read

| # | Document | Sections consulted |
|---|---|---|
| 1 | `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt` (EAIOC-SPEC-001) | §52.17 (H17), §52.18 (H18) |
| 2 | `Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md` (Rev 1.4) | Companion elaboration of PS §52.17/§52.18 — no additional normative content beyond what ARCH §26/§47.8/§47.9 already restate; subordinate to PS |
| 3 | `architecture.md` (Rev 1.3) | §10.1–10.3 (T0.1–T0.3), §13.4–13.5 (Model Cascade, Batch/Async Optimizer), §25 (Provider/Model Profiles), §26 (P5 Index), §36 (Implementation Priority), §41 (Anti-Patterns), §42 (Maturity Model), §46.2.10 (CAR), §46.2.12 (SPM), §46.2.13 (RCO), §47.4.1–47.4.3 (SPC, Net Optimization Economics, VCL), §47.8 (H17), §47.9 (H18), §47.10 (FTR), §47.12 (H19), §52 traceability appendix |
| 4 | `interfaces.md` (v1.2.0) | §32.4 (Model Cascade Flow), T3.x references (INTF-002, INTF-014–016), §33 (Interface Anti-Patterns) |
| 5 | `conventions.md` (Rev 1.1.0) | §13.2 (Tenant Isolation Rules), §16.2 (Fail-Open vs. Fail-Closed), P5 section (§36-area, "must remain separate from prompt/context optimization"), `t3_inference/` package note |
| 6 | `edge-cases.md` (v1.2.0) | `EC-076` (P5 without separate measurement — this document's central validating edge case), `EC-151` (SPC self-failure fallback, cross-referenced for SPC interaction), `EC-154` (`DO_NOT_OPTIMIZE` selected proactively) |
| 7 | `scenario-matrix.md` | `SCN-CMP-055`, `SCN-OPT-009` (measurement-attribution separation), `SCN-NOPT-001` (`DO_NOT_OPTIMIZE`), `SCN-STATE-005` (SPC self-failure) |
| 8 | `optimization-catalog.md` | P5 Index section (11 stubs elaborated here), Scope Boundary table's deferral row, `TECH-013`/`TECH-014` (Model Routing / Cascade — cited, not redefined) |
| 9 | `provider-matrix.md` | §8 (P5 Capability Exposure Matrix — per-provider exposure facts cited verbatim by row), §2 (Provider Accessibility Model: Discovered/Accessible/Available/Authorized/Recommended/Eligible) |
| 10 | `cache-strategy.md` | `CACHE-005` (`PROVIDER_NATIVE` — cited for its EAIOC-side/infrastructure-side boundary statement, not restated) |
| 11 | `agent-optimization.md` | `AR-001` (Marginal-Gain Model Routing), `AR-004` (Verifier-Guided Escalation), H09 (Agent Memory Authority) — cited, not redefined |
| — | Root `CLAUDE.md` | Full document — scope, ID-series convention, single-file-scoped reconciliation rule, non-negotiable rules 1–9 |

## 4. The H17/H18 Boundary — Integration, Not Implementation

> **PS §52.17 (H17), restated verbatim in `architecture.md` §26/§47.8:** "the Control Plane's role at this layer is awareness, integration, routing, policy, and capability negotiation with infrastructure/providers — never the implementation of model-runtime mechanisms. The Control Plane shall NOT implement its own KV-cache engine, continuous-batching scheduler, speculative-decoding mechanism, quantization pipeline, or prefill/decode disaggregation."

For every one of the 11 P5 capabilities, the Control Plane's own responsibility is limited to exactly four things (`architecture.md` §26/§47.8):

1. **Detect** whether the provider/infrastructure exposes it (via the provider/model profile, `architecture.md` §25, populated with facts from `provider-matrix.md` §8).
2. **Route/select** to take advantage of it when net-beneficial and policy-compliant (§18 below extends §47.4.2's Net Optimization Value accounting).
3. **Negotiate** capability parameters through the provider adapter — hints only (cache-affinity hint, batch-eligibility hint), never a first-class core-schema field (`CLAUDE.md` rule 1, `interfaces.md` §33's "Provider field in core schema: PROHIBITED — Use `provider_hints: map<string, any>`").
4. **Measure** the resulting effect kept separate from Layer 1/2 measurement (`AC-036`, `EC-076` — see §19).

**PS §52.18 (H18), restated in `architecture.md` §47.9:** ownership of token/context/cache/inference optimization is split into six categories, each attributed exclusively — inference-runtime optimization (Layer 3, this document's subject) is "Awareness, integration, routing, policy only." A measurement that conflates categories (e.g., reporting a KV-cache provider improvement as a context-optimization saving) is invalid per `AC-036`.

**Out of scope, explicitly, for every one of the 11 capabilities below** — this document contains no design for: KV-cache engine internals or eviction algorithms; GPU memory-management algorithms; continuous-batching queueing algorithms; provider scheduler implementation; speculative-decoding draft-model mechanics or verification-engine implementation; quantization algorithms or bit-width optimization formulas; GPU-kernel optimization or utilization formulas; inference-server queue implementation; prefill/decode kernel implementation; replica-infrastructure orchestration internals. Where such a mechanism must be mentioned to explain a Control-Plane decision, it is described only at the conceptual integration boundary (e.g., "the provider exposes X" or "negotiation of Y failed"), with implementation explicitly deferred to the provider/inference-serving infrastructure. The Control Plane never claims to implement any of these mechanisms.

## 5. P5 Control-Plane Decision Model

Every P5 decision executes within the established EAIOC stateful execution model (`architecture.md` §46) rather than as an isolated calculation. The conceptual flow (Control-Plane-owned steps only; no step below designs infrastructure):

```
Receive current execution state (ESM snapshot)
  -> Reconcile relevant state (RE, if a prior P5 decision exists for this execution)
  -> Validate authorization/policy/governance (unchanged from the request's already-established authorization — P5 negotiation never re-derives or weakens it)
  -> Identify selected provider/model and capability profile (T0.1 Model Router's decision + ARCH §25 profile, already made — not redefined here)
  -> Detect P5 capability exposure (CAR, ARCH §46.2.10, extending T0.1; cross-reference provider-matrix.md §8)
  -> Determine applicability (per-entry Applicability Conditions, §7-§16)
  -> Estimate expected benefit and Control-Plane negotiation/measurement overhead (§18)
  -> Evaluate policy/security/tenant constraints (§25)
  -> Decide: negotiate / do not negotiate (DO_NOT_OPTIMIZE, §24, is a legitimate outcome)
  -> Negotiate through provider adapter (hints only)
  -> Request executes on provider/infrastructure (opaque to the Control Plane)
  -> Measure P5 effect in a separate metrics namespace (§19)
  -> Verify result/attribution (VCL-adjacent calibration where a probabilistic signal is involved)
  -> Continue / fall back to the unoptimized path / recover as required (§23)
```

This flow does not redesign or extend `architecture.md` §46's execution-state machine; it is this document's own restatement of how P5 decisions fit inside it.

---

## 6. `INFOPT-NNN` Entries

Eleven entries, `INFOPT-001` through `INFOPT-011`, in `architecture.md` §26's exact order (matching `provider-matrix.md` §8's numbering 1–11). This is a new document-scoped ID series, introduced the same way `provider-matrix.md` introduced `PROV-NNN` and `cache-strategy.md` introduced `CACHE-NNN` — per `CLAUDE.md`'s ID-traceability rule, no numeric series existed for these 11 capabilities before now (only the non-numeric `P5 Index — <name>` heading in `optimization-catalog.md`).

### 7. INFOPT-001 — Prefix/KV-Cache Reuse

| Field | Value |
|---|---|
| **Capability** | Prefix/KV-cache reuse |
| **Control-Plane Role** | Awareness / detection / routing-selection / negotiation / measurement only |
| **Detection Mechanism** | CAR (`architecture.md` §46.2.10) reads the provider/model profile (§25's "cache write price"/"cache read price"/"known cache constraints" dimensions) populated from `provider-matrix.md` §8 row 1 |
| **Applicability Conditions** | The already-selected provider/model (T0.1's decision) is `provider-matrix.md` §8 row 1 = **Yes** or **Conditional**; the request's prompt-assembly output contains a stable, reusable prefix (a `cache-strategy.md` `CACHE-005`/`PROVIDER_NATIVE` concern, cited not redefined) |
| **Routing/Negotiation Policy** | Negotiate a cache-affinity hint when §17's Net Optimization Value is positive AND policy-compliant; never negotiate solely because the provider exposes the capability |
| **Provider Adapter Negotiation** | Cache-affinity hint passed through the provider adapter as `provider_hints` (never a core schema field, `CLAUDE.md` rule 1) |
| **Existing Component Interaction** | Downstream of T0.1 (Model Router) and `cache-strategy.md`'s cache-aware prompt assembly (`CE-002`); upstream of measurement (§19) |
| **Measurement Separation Requirement** | Any latency/cost effect attributed to this capability is recorded in the `inference_serving.*` namespace, never merged with `cache-strategy.md`'s EAIOC-owned `EXACT`/`SEMANTIC` cache savings (`AC-036`, `EC-076`) |
| **Fallback Behavior** | If detection is Unknown/Not exposed, or negotiation fails, the request proceeds without the hint — fail-open, no request block (§47.8) |
| **Cross-Provider Variance** | `provider-matrix.md` §8 row 1: Anthropic **Yes** (VERIFIED, `claude.com/pricing`, 17-09-2026), OpenAI **Yes** (VERIFIED, `developers.openai.com/api/docs/pricing`, 17-09-2026), Azure OpenAI **Conditional** (`SOURCE-GAP-PROVMTX-04`) — never assumed to generalize |
| **Security/Authorization/Policy Constraints** | Negotiation never bypasses authorization/policy already established for the request; a cache-affinity hint carries no PII/security-classified content itself |
| **Data Governance Constraints** | Content eligible for provider-side prefix caching must already be cache-eligible under `cache-strategy.md`'s governance gate (§8.2 there) — cited, not redefined |
| **Non-Goals** | KV-cache engine internals, eviction policy, memory layout — provider-owned |
| **Validated By** | `EC-076`, `SCN-CMP-055`, `SCN-OPT-009` (collective measurement-separation validation; no capability-specific scenario exists) |
| **Traceability** | PS §52.17; ARCH §26 row 1, §47.8, §25, §46.2.10; `provider-matrix.md` §8 row 1; `cache-strategy.md` `CACHE-005`; `optimization-catalog.md` P5 Index — Prefix/KV-Cache Reuse stub |

### 8. INFOPT-002 — KV-Cache Compression

| Field | Value |
|---|---|
| **Capability** | KV-cache compression |
| **Control-Plane Role** | Awareness / detection / negotiation / measurement only |
| **Detection Mechanism** | CAR checks the provider/model profile for a documented KV-cache-compression dimension; `provider-matrix.md` §8 row 2 currently records this as **Unknown** for every catalogued provider (not exposed as a distinct billable/documented dimension) |
| **Applicability Conditions** | Applicable only if and when a provider documents this as a client-negotiable dimension — currently none do |
| **Routing/Negotiation Policy** | No negotiation is currently possible (Unknown exposure); this entry exists so the detection/negotiation pathway is defined in advance of any provider publishing the capability |
| **Provider Adapter Negotiation** | N/A today — reserved hint slot in `provider_hints`, undefined until a provider exposes a concrete parameter |
| **Existing Component Interaction** | Same detection pathway as `INFOPT-001` (CAR + §25 profile); no interaction beyond that today |
| **Measurement Separation Requirement** | If ever negotiated, effect is measured in `inference_serving.*`, never combined with `cache-strategy.md` savings (`AC-036`) |
| **Fallback Behavior** | Always fail-open by default, since the capability is never currently detected as available |
| **Cross-Provider Variance** | Unknown for Anthropic, OpenAI, and Azure OpenAI alike (`provider-matrix.md` §8 row 2) — do not infer availability from `INFOPT-001`'s exposure |
| **Security/Authorization/Policy Constraints** | Same as `INFOPT-001` — not yet exercised in practice |
| **Data Governance Constraints** | Same governance gate as any provider-side cache content would require, if the capability ever becomes negotiable |
| **Non-Goals** | Compression algorithms, ratios, bit-widths, GPU memory-management algorithms — provider-owned, and currently `SOURCE-GAP` even at the fact level |
| **Validated By** | `EC-076`, `SCN-CMP-055`, `SCN-OPT-009` (collective) |
| **Traceability** | PS §52.17; ARCH §26 row 2, §47.8; `provider-matrix.md` §8 row 2; `optimization-catalog.md` P5 Index — KV-Cache Compression stub |

### 9. INFOPT-003 — Continuous Batching

| Field | Value |
|---|---|
| **Capability** | Continuous batching |
| **Control-Plane Role** | Awareness / detection / negotiation / measurement only |
| **Detection Mechanism** | CAR checks §25's "batch support" profile dimension; `provider-matrix.md` §8 row 3 distinguishes continuous batching (server-side, not client-controllable — Unknown for all three catalogued providers) from the separate, client-visible async **Batch API** discount product feature (VERIFIED for Anthropic/OpenAI) |
| **Applicability Conditions** | Continuous-batching *negotiation* is inapplicable today (Unknown exposure); the Batch API product feature is a distinct, already-defined optimization owned by `architecture.md` §13.5 (T3.5 Batch/Async Optimizer) — this entry does not redefine it, only notes the boundary so the two are never conflated |
| **Routing/Negotiation Policy** | No continuous-batching-specific negotiation exists today; T3.5's own asynchronous/interactive-priority separation (§13.5) governs whether a request is Batch-API-eligible, which is a separate decision from this entry |
| **Provider Adapter Negotiation** | N/A for continuous batching itself; a batching-eligibility hint would be the reserved slot if a provider ever exposes client-influenceable continuous batching |
| **Existing Component Interaction** | T3.5 Batch/Async Optimizer (cited, not redefined) — an interactive request is never routed to a batch-eligible path for freshness/latency reasons (§13.5's caution) |
| **Measurement Separation Requirement** | Continuous-batching effects, if ever detected, are measured in `inference_serving.*`; Batch API discount effects are already a T3.5/Layer-1 concern with its own accounting, never merged with this entry's namespace (`AC-036`) |
| **Fallback Behavior** | Fail-open — no continuous-batching negotiation is attempted when Unknown |
| **Cross-Provider Variance** | Unknown for continuous batching across all three; Batch API discount VERIFIED separately for Anthropic and OpenAI (`provider-matrix.md` §8 row 3) |
| **Security/Authorization/Policy Constraints** | A request must remain interactive-latency-appropriate before any batch-eligible routing is even considered by T3.5 — this entry never overrides that |
| **Data Governance Constraints** | None beyond what T3.5 already requires for batch eligibility |
| **Non-Goals** | Continuous-batching queueing algorithms, provider scheduler implementation — provider-owned |
| **Validated By** | `EC-076`, `SCN-CMP-055`, `SCN-OPT-009` (collective) |
| **Traceability** | PS §52.17; ARCH §26 row 3, §13.5, §47.8; `provider-matrix.md` §8 row 3; `optimization-catalog.md` P5 Index — Continuous Batching stub |

### 10. INFOPT-004 — Request Scheduling

| Field | Value |
|---|---|
| **Capability** | Request scheduling |
| **Control-Plane Role** | Awareness / detection / negotiation / measurement only |
| **Detection Mechanism** | CAR checks §25's latency-characteristics/known-routing-constraints dimensions; `provider-matrix.md` §8 row 4 records **Not exposed** for all three catalogued providers |
| **Applicability Conditions** | Inapplicable today — no provider exposes a client-controllable scheduling-priority dimension |
| **Routing/Negotiation Policy** | No negotiation possible; entry defines the pathway for if/when a provider exposes one |
| **Provider Adapter Negotiation** | N/A today — reserved hint slot (e.g., a future priority/eligibility hint) |
| **Existing Component Interaction** | Would interact with T3.5's interactive-vs-asynchronous priority split if ever negotiable; no interaction today |
| **Measurement Separation Requirement** | Same `inference_serving.*` namespace discipline as every other entry (`AC-036`) |
| **Fallback Behavior** | Fail-open by default |
| **Cross-Provider Variance** | Not exposed by Anthropic, OpenAI, or Azure OpenAI (`provider-matrix.md` §8 row 4) |
| **Security/Authorization/Policy Constraints** | Not yet exercised |
| **Data Governance Constraints** | Not yet exercised |
| **Non-Goals** | Provider queue/scheduler internals — provider-owned |
| **Validated By** | `EC-076`, `SCN-CMP-055`, `SCN-OPT-009` (collective) |
| **Traceability** | PS §52.17; ARCH §26 row 4, §47.8; `provider-matrix.md` §8 row 4; `optimization-catalog.md` P5 Index — Request Scheduling stub |

### 11. INFOPT-005 — Prefill Optimization

| Field | Value |
|---|---|
| **Capability** | Prefill optimization |
| **Control-Plane Role** | Awareness / detection / negotiation / measurement only |
| **Detection Mechanism** | CAR checks §25's context-limit/latency dimensions; `provider-matrix.md` §8 row 5 records **Not exposed** for all three |
| **Applicability Conditions** | Inapplicable today |
| **Routing/Negotiation Policy** | No negotiation possible today |
| **Provider Adapter Negotiation** | N/A — reserved hint slot |
| **Existing Component Interaction** | Would interact with T0.2 (Task/Complexity Analyzer)'s input-size signal if ever negotiable, since prefill cost scales with input length; no interaction today |
| **Measurement Separation Requirement** | `inference_serving.*` namespace discipline (`AC-036`) |
| **Fallback Behavior** | Fail-open by default |
| **Cross-Provider Variance** | Not exposed by any catalogued provider (`provider-matrix.md` §8 row 5) |
| **Security/Authorization/Policy Constraints** | Not yet exercised |
| **Data Governance Constraints** | Not yet exercised |
| **Non-Goals** | Prefill kernel implementation, engine algorithms — provider-owned |
| **Validated By** | `EC-076`, `SCN-CMP-055`, `SCN-OPT-009` (collective) |
| **Traceability** | PS §52.17; ARCH §26 row 5, §47.8, §10.2; `provider-matrix.md` §8 row 5; `optimization-catalog.md` P5 Index — Prefill Optimization stub |

### 12. INFOPT-006 — Decode Optimization

| Field | Value |
|---|---|
| **Capability** | Decode optimization |
| **Control-Plane Role** | Awareness / detection / negotiation / measurement only |
| **Detection Mechanism** | CAR checks §25's output-limit/reasoning-controls dimensions; `provider-matrix.md` §8 row 6 records **Not exposed** for all three |
| **Applicability Conditions** | Inapplicable today |
| **Routing/Negotiation Policy** | No negotiation possible today |
| **Provider Adapter Negotiation** | N/A — reserved hint slot |
| **Existing Component Interaction** | Would interact with T0.3 (Reasoning Budget Controller) and T2's Output Length Controller if ever negotiable, since decode cost scales with output length/reasoning depth; **this entry does not redefine either** — those two already own the actual budget/length decision (§10.3, §12.4), and never reduce a budget solely to hit a token target (`CLAUDE.md` rule 3) |
| **Measurement Separation Requirement** | `inference_serving.*` namespace discipline (`AC-036`) |
| **Fallback Behavior** | Fail-open by default |
| **Cross-Provider Variance** | Not exposed by any catalogued provider (`provider-matrix.md` §8 row 6) |
| **Security/Authorization/Policy Constraints** | Not yet exercised |
| **Data Governance Constraints** | Not yet exercised |
| **Non-Goals** | Decode kernel/serving algorithms — provider-owned |
| **Validated By** | `EC-076`, `SCN-CMP-055`, `SCN-OPT-009` (collective) |
| **Traceability** | PS §52.17; ARCH §26 row 6, §47.8, §10.3, §12.4; `provider-matrix.md` §8 row 6; `optimization-catalog.md` P5 Index — Decode Optimization stub |

### 13. INFOPT-007 — Speculative Decoding

| Field | Value |
|---|---|
| **Capability** | Speculative decoding |
| **Control-Plane Role** | Awareness / detection / negotiation / measurement only |
| **Detection Mechanism** | CAR checks §25's profile; `provider-matrix.md` §8 row 7 records **Not exposed** for all three catalogued providers |
| **Applicability Conditions** | Inapplicable today |
| **Routing/Negotiation Policy** | No negotiation possible today |
| **Provider Adapter Negotiation** | N/A — reserved hint slot |
| **Existing Component Interaction** | If ever negotiable, quality/correctness verification of speculative output would route through the same verifier-confidence discipline VCL already establishes for `AR-004` (Verifier-Guided Escalation, `agent-optimization.md`, cited not redefined) — speculative decoding's own correctness is provider-guaranteed, not EAIOC-verified, but any EAIOC-side quality signal downstream of it uses existing verifier calibration, not a new mechanism |
| **Measurement Separation Requirement** | `inference_serving.*` namespace discipline (`AC-036`) |
| **Fallback Behavior** | Fail-open by default; if speculative decoding were ever negotiated and later degrades quality, the fallback is the same verifier-guided escalation path `AR-004` already defines, not a new mechanism |
| **Cross-Provider Variance** | Not exposed by any catalogued provider (`provider-matrix.md` §8 row 7) |
| **Security/Authorization/Policy Constraints** | Not yet exercised |
| **Data Governance Constraints** | Not yet exercised |
| **Non-Goals** | Draft-model selection, speculative-decoding verify-loop mechanics — provider-owned |
| **Validated By** | `EC-076`, `SCN-CMP-055`, `SCN-OPT-009` (collective) |
| **Traceability** | PS §52.17; ARCH §26 row 7, §47.8, §19.4; `provider-matrix.md` §8 row 7; `agent-optimization.md` `AR-004`; `optimization-catalog.md` P5 Index — Speculative Decoding stub |

### 14. INFOPT-008 — Quantization

| Field | Value |
|---|---|
| **Capability** | Quantization |
| **Control-Plane Role** | Awareness / detection / negotiation / measurement only |
| **Detection Mechanism** | CAR checks §25's profile; `provider-matrix.md` §8 row 8 records **Not exposed** for all three catalogued providers |
| **Applicability Conditions** | Inapplicable today — no provider exposes quantization as a client-selectable serving option |
| **Routing/Negotiation Policy** | No negotiation possible today |
| **Provider Adapter Negotiation** | N/A — reserved hint slot |
| **Existing Component Interaction** | If ever negotiable as a distinct served-model variant (e.g., a "fast/quantized" model tier alongside a full-precision tier), it would surface as an additional candidate in T0.1's model-selection signal set (§10.1) — this entry does not redefine T0.1's selection logic, only notes where a future quantized-tier fact would enter it |
| **Measurement Separation Requirement** | `inference_serving.*` namespace discipline (`AC-036`) |
| **Fallback Behavior** | Fail-open by default |
| **Cross-Provider Variance** | Not exposed by any catalogued provider (`provider-matrix.md` §8 row 8) |
| **Security/Authorization/Policy Constraints** | Quality/compliance implications of ever selecting a quantized tier would need to clear the same quality-gate discipline any model choice must (§28 area of `optimization-catalog.md`, cited not redefined) — never assumed cost-neutral to quality |
| **Data Governance Constraints** | Not yet exercised |
| **Non-Goals** | Quantization algorithms, bit-width optimization formulas, accuracy guarantees — provider-owned, and explicitly never invented here |
| **Validated By** | `EC-076`, `SCN-CMP-055`, `SCN-OPT-009` (collective) |
| **Traceability** | PS §52.17; ARCH §26 row 8, §47.8, §10.1; `provider-matrix.md` §8 row 8; `optimization-catalog.md` P5 Index — Quantization stub |

### 15. INFOPT-009 — Model Replica Selection

| Field | Value |
|---|---|
| **Capability** | Model replica selection |
| **Control-Plane Role** | Awareness / detection / negotiation / measurement only |
| **Detection Mechanism** | CAR checks §25's provider-availability dimension; `provider-matrix.md` §8 row 9 records **Not exposed to API consumers** for Anthropic/OpenAI, and **Conditional** for Azure OpenAI (Global/Data Zone/Regional deployment selection — a coarser, customer-controllable analogue, VERIFIED, but a deployment/region choice, not model-replica-level selection) |
| **Applicability Conditions** | Azure OpenAI's deployment-region selection is the only currently-exposed analogue; true replica-level selection is inapplicable elsewhere |
| **Routing/Negotiation Policy** | Where a deployment/region choice is exposed (Azure), negotiate only when net-beneficial (latency/cost) and compliance-appropriate (data-residency requirements, §25's "Compliance/data-residency requirements" dimension, take precedence over any latency/cost gain) |
| **Provider Adapter Negotiation** | Deployment/region preference hint, never a hard requirement bypassing residency policy |
| **Existing Component Interaction** | Directly extends CAR's own stated purpose (`architecture.md` §46.2.10: "Selects the next available model from the configured cascade... Validates that the failover model meets context window, tool support, and compliance constraints... Updates the running `execution_version` when a model change is made mid-execution") — this entry's replica-selection negotiation is CAR's failover mechanism applied at the deployment/region granularity, not a new mechanism |
| **Measurement Separation Requirement** | `inference_serving.*` namespace discipline (`AC-036`) |
| **Fallback Behavior** | On replica/deployment unavailability, CAR activates its circuit breaker and selects the next available model/deployment from the configured cascade, per its existing spec — fail-open, request proceeds |
| **Cross-Provider Variance** | Not exposed to API consumers by Anthropic or OpenAI; Conditional (region-level only) for Azure OpenAI (`provider-matrix.md` §8 row 9) |
| **Security/Authorization/Policy Constraints** | Data-residency/compliance constraints (`architecture.md` §25) are evaluated **before** any latency/cost-driven region negotiation — a region switch is never made purely for cost when it would violate a compliance/residency requirement |
| **Data Governance Constraints** | Region/deployment selection must respect data-residency policy; this is a DGE-adjacent constraint (`architecture.md` §47.2, cited not redefined) |
| **Non-Goals** | Infrastructure replica orchestration internals — provider-owned |
| **Validated By** | `EC-076`, `SCN-CMP-055`, `SCN-OPT-009` (collective); `EC-124` (CAR failover to a smaller-context fallback, cited in `provider-matrix.md` §2 as the illustrative case for `Recommended != Eligible`) is the closest related, non-P5-specific validating edge case for the underlying CAR mechanism this entry extends |
| **Traceability** | PS §52.17; ARCH §26 row 9, §47.8, §46.2.10, §25; `provider-matrix.md` §8 row 9, §2; `optimization-catalog.md` P5 Index — Model Replica Selection stub |

### 16. INFOPT-010 — GPU Utilization Optimization

| Field | Value |
|---|---|
| **Capability** | GPU utilization optimization |
| **Control-Plane Role** | Awareness / detection / negotiation / measurement only |
| **Detection Mechanism** | CAR checks §25's profile; `provider-matrix.md` §8 row 10 records **Not exposed** for all three catalogued providers |
| **Applicability Conditions** | Inapplicable today — GPU utilization is an infrastructure-internal metric, not a client-facing dimension for any catalogued provider |
| **Routing/Negotiation Policy** | No negotiation possible today; the Control Plane never attempts to influence provider-side GPU utilization directly |
| **Provider Adapter Negotiation** | N/A — no reserved hint slot proposed, since no plausible client-side negotiation parameter exists for this capability as currently exposed (or rather, not exposed) by any provider |
| **Existing Component Interaction** | None today |
| **Measurement Separation Requirement** | `inference_serving.*` namespace discipline (`AC-036`) — would apply only if a provider ever surfaces a client-visible utilization-linked pricing/latency signal |
| **Fallback Behavior** | Fail-open by default (trivially, since no negotiation is ever attempted) |
| **Cross-Provider Variance** | Not exposed by any catalogued provider (`provider-matrix.md` §8 row 10) |
| **Security/Authorization/Policy Constraints** | Not applicable today |
| **Data Governance Constraints** | Not applicable today |
| **Non-Goals** | GPU-kernel optimization, GPU-utilization formulas, infrastructure control logic — provider-owned, and not even fact-level observable today |
| **Validated By** | `EC-076`, `SCN-CMP-055`, `SCN-OPT-009` (collective) |
| **Traceability** | PS §52.17; ARCH §26 row 10, §47.8; `provider-matrix.md` §8 row 10; `optimization-catalog.md` P5 Index — GPU Utilization Optimization stub |

### 17. INFOPT-011 — Inference Queue Management

| Field | Value |
|---|---|
| **Capability** | Inference queue management |
| **Control-Plane Role** | Awareness / detection / negotiation / measurement only |
| **Detection Mechanism** | CAR monitors for the externally-visible symptom of provider-side queue/capacity management — rate-limit/429 responses (`edge-cases.md` `EC-064`, "Provider Returns Rate Limit Error Repeatedly," cited not redefined); `provider-matrix.md` §8 row 11 records this as **not exposed directly** for all three, symptom-only visibility |
| **Applicability Conditions** | Applicable whenever the provider adapter observes rate-limit/capacity-pressure signals; no direct queue-priority negotiation is possible with any catalogued provider today |
| **Routing/Negotiation Policy** | No priority negotiation is possible; the Control Plane's only lever is CAR's failover — routing to the next available model/provider in the configured cascade when capacity pressure is sustained |
| **Provider Adapter Negotiation** | N/A for direct queue-priority hints today; retry/backoff parameters (standard adapter-level resilience, not a P5-specific negotiation) are the only currently-available lever, and are not redefined here |
| **Existing Component Interaction** | CAR's circuit breaker (§46.2.10) and SPC's overload-detection/backpressure mechanism (`architecture.md` §47.4.1, cited not redefined — SPC governs the Control Plane's *own* overload, not the provider's, but the two are related: sustained provider-side rate-limiting is one input a request-level retry/backoff policy must respect so it does not itself become a self-inflicted overload) |
| **Measurement Separation Requirement** | `inference_serving.*` namespace discipline (`AC-036`); rate-limit/429 frequency is a P5-layer observability signal (§26), never conflated with Layer 1/2 optimization-effectiveness metrics |
| **Fallback Behavior** | On sustained rate-limiting, CAR activates its circuit breaker and fails over to the next available model in the configured cascade (fail-open); the request is never blocked solely because one provider is queue-constrained, provided a compliant failover target exists |
| **Cross-Provider Variance** | Not exposed directly by Anthropic or OpenAI; same symptom-only visibility for Azure OpenAI (`provider-matrix.md` §8 row 11) |
| **Security/Authorization/Policy Constraints** | A failover triggered by queue pressure must still validate the failover model's context window, tool support, and compliance constraints before use (CAR's existing requirement, §46.2.10) — queue pressure never bypasses that validation |
| **Data Governance Constraints** | None specific to this entry beyond CAR's existing failover-validation requirement |
| **Non-Goals** | Inference-server queue implementation — provider-owned |
| **Validated By** | `EC-064` (rate-limit symptom, adjacent — not a direct P5 validating scenario), `EC-076`, `SCN-CMP-055`, `SCN-OPT-009` (collective) |
| **Traceability** | PS §52.17; ARCH §26 row 11, §47.8, §46.2.10, §47.4.1; `provider-matrix.md` §8 row 11; `edge-cases.md` `EC-064`; `optimization-catalog.md` P5 Index — Inference Queue Management stub |

---

## 18. Net Optimization Value for P5

This section extends `architecture.md` §47.4.2's Net Optimization Value accounting to P5-layer negotiation decisions specifically — it introduces no new accounting terms.

| Category | Terms (per §47.4.2, applied to P5) |
|---|---|
| Benefit | Inference cost/latency improvement realized by the *infrastructure* once a capability is successfully negotiated — never a Control-Plane-side token/context saving (that would be a Layer 1/2 category error per H18) |
| Overhead | Detection cost (CAR check), negotiation cost (provider-adapter round-trip if any), measurement/attribution overhead |
| Cost | Additional latency from negotiation itself, retry/escalation cost if negotiation fails and a fallback path is taken, downstream cost if a capability-driven routing choice (e.g., `INFOPT-009`'s region selection) changes which provider/deployment serves the request |

**Governing principle (restated from the generation prompt, consistent with §47.4.2's own framing):** a P5 capability is valuable only when its verified or measurable benefit exceeds its relevant overhead **and** remains policy-compliant. A P5 capability is never assumed to always save money, latency, or tokens merely because it exists or is exposed.

**Benefit-confidence ladder** (distinguishing degrees of certainty, `PROPOSED METHODOLOGY` — no source enumerates this exact ladder, though each rung individually follows from `CLAUDE.md` rule 5's "never fabricate savings" and `architecture.md` §42's maturity model):

| Rung | Meaning |
|---|---|
| Theoretical benefit | The capability, per vendor documentation, plausibly improves latency/cost — not yet estimated for this workload |
| Estimated benefit | A workload-specific estimate exists but is unverified locally — `PROPOSED DEFAULT — VALIDATE LOCALLY` |
| Measured benefit | A local measurement exists for at least one request/workload sample |
| Verified benefit | A local measurement has been reproduced across enough samples/time to be trusted for a routing decision (§42's Level 2+ threshold, cited not redefined) |

Only **measured** or **verified** benefit may ever be reported in a savings claim, per `CLAUDE.md` rule 5 — theoretical/estimated benefit may inform a *decision to attempt* negotiation, never a savings *report*.

## 19. Measurement Separation

The central requirement of this entire document, restated once, in full, here (each `INFOPT-NNN` entry above cross-references this section rather than restating it):

- **`AC-036`:** "The framework must distinguish context optimization from inference-serving optimization." P5 metrics live in a separate metrics namespace (`inference_serving.*`) from Layer 1/2 optimization metrics (`context_optimization.*`, cache-layer metrics, etc.).
- **`EC-076`** (P5 Inference Optimization Enabled Without Separate Measurement): the canonical failure this requirement prevents — e.g., continuous batching enabled at the inference-server level with its latency improvement misattributed entirely to prompt-level optimization. Expected behavior per `EC-076`: (1) P5 optimizations are always measured in a separate namespace; (2) savings attributed to P5 are never combined with savings attributed to context optimization; (3) when both layers are active, total savings are reported with a breakdown by layer.
- **`SCN-CMP-055`/`SCN-OPT-009`** are the validating scenarios for this separation requirement **across all 11 capabilities collectively** — there is no capability-specific validating scenario for any single `INFOPT-NNN` entry, and this document does not fabricate one to make coverage appear more granular than the authoritative scenario matrix supports (consistent with how `optimization-catalog.md`'s own P5 Index section already treats this).

**Per-decision measurement fields** (what a P5 negotiation record should carry, `PROPOSED METHODOLOGY`): capability considered (`INFOPT-NNN`), capability detected (bool + source), capability eligible (bool), capability negotiated (bool), capability accepted/rejected by provider, negotiation latency, request latency, expected benefit (confidence-ladder rung, §17), actual/measured benefit where available, optimization overhead, provider/infrastructure attribution note, fallback taken (bool), measurement-confidence rung.

## 20. Provider/Model Integration

`provider-matrix.md` is the sole authority for provider/model capability-exposure facts (§8) and for the six-state Provider Accessibility Model (§2): **Discovered**, **Accessible**, **Available**, **Authorized**, **Recommended**, **Eligible** — six independent, never-conflated dimensions. This document respects the inequalities `provider-matrix.md` §2 states explicitly: `Discovered != Accessible`; `Accessible != Available`; `Available != Authorized`; `Authorized != Eligible`; `Recommended != Eligible`.

Applied to P5 negotiation specifically: a capability being **Discovered** (this document names it, ARCH §26 defines it) does not make it **Accessible** for a given deployment, nor **Available** at runtime, nor **Authorized** for a given tenant, nor **Recommended**, nor technically **Eligible**. Every `INFOPT-NNN` entry's "Cross-Provider Variance" field cites `provider-matrix.md` §8's exposure fact as the closest-available static signal (VERIFIED/Conditional/Unknown/Not exposed) — this document never invents pricing, availability, capability exposure, latency, throughput, or GPU behavior beyond what `provider-matrix.md` has already verified or honestly flagged as `SOURCE-GAP`.

## 21. Cache Integration

`cache-strategy.md` is the sole authority for the seven canonical cache types, their key/TTL/invalidation/economics policy, and semantic-cache/poisoning controls. This document's only cache-adjacent entry, `INFOPT-001` (Prefix/KV-cache reuse), interacts with `CACHE-005` (`PROVIDER_NATIVE`) at exactly the boundary `cache-strategy.md` itself already draws: `cache-strategy.md` §"Provider/Model Dependencies" for `EXACT` states "Cross-reference `provider-matrix.md` §4/§8/§12 for whether the selected provider/model exposes a provider-native equivalent... do not restate provider facts here," and `CACHE-005`'s own entry notes writes for `PROVIDER_NATIVE` are "plausibly provider-adapter-internal, never issued by core EAIOC write-path code." This document adds the P5-facing negotiation counterpart (detect/route/negotiate/measure whether the provider exposes prefix reuse at all) beneath that boundary, and redefines no cache key, TTL, invalidation rule, or economics formula.

## 22. Agent Integration

`agent-optimization.md` is the sibling authority for agent-loop optimization (`AL-NNN`) and adaptive model/reasoning/output control (`AR-NNN`). This document does not duplicate `AL` algorithms, `AR` routing decisions, agent-iteration policy, sub-agent orchestration, or agent-memory ownership. Where a P5 capability's negotiation sits beneath an agent-loop-facing decision — `AR-001` (Marginal-Gain Model Routing) selecting or re-evaluating a model mid-loop, `AR-004` (Verifier-Guided Escalation) triggering a cascade — this document's entries (`INFOPT-001`, `INFOPT-007`, `INFOPT-009`) cite that decision as already made and describe only the capability-negotiation layer beneath it.

**H09 (Agent Memory Authority), preserved:** per `agent-optimization.md` §0/§18's restatement, agent-owned working memory is never authoritative over ESM/CVM/WVM execution truth. No P5 decision in this document ever substitutes a cached/remembered capability-exposure value for a fresh CAR check against the current execution state when the decision affects execution truth (e.g., a mid-execution provider failover, `INFOPT-009`) — this mirrors `agent-optimization.md` §0's own governing principle rather than introducing a new one.

## 23. State, Revalidation, and Recovery

P5 decisions occur within the dynamic execution state `architecture.md` §46 owns. When a previously negotiated P5 capability becomes invalid — provider availability changes, capability exposure changes, authorization/policy/tenant context changes, or the workflow/context state supersedes the execution — the response is: **revalidate** (re-run CAR detection), **fall back** (proceed on the unoptimized path), **renegotiate** (attempt the capability again against the new state), or **switch provider/model where authorized** (CAR's existing failover mechanism, §46.2.10, and SPM's supersession handling, §46.2.12, cited not redefined).

**Non-idempotent replay is prohibited** — `AC-049` states the framework "must not blindly replay non-idempotent operations across executions," and `AC-053` requires checkpoint/resume state to "remain interpretable for reconciliation across a change in selected model/provider," without depending on a specific provider's native session mechanism to be correct. Applied here: if a P5 negotiation (e.g., a cache-affinity hint tied to a since-superseded context version) is resumed after an interruption, RCO's resume protocol (`architecture.md` §46.2.13 — `RESUME | RESULT_ALREADY_AVAILABLE | PRECONDITION_FAILED`) governs whether the prior negotiation state is still valid, not a blind replay of the original negotiation call. A superseded execution's P5 negotiation is never allowed to continue producing unwanted side effects (SPM's supersession handling, cited not redefined).

## 24. Negative Optimization

`DO_NOT_OPTIMIZE` (`edge-cases.md` `EC-154`, validated by `SCN-NOPT-001`) is a legitimate, proactively-selected outcome for any P5 capability, not only a failure fallback. Cases where this document expects `DO_NOT_OPTIMIZE` for a P5 capability, even when technically feasible:

- Capability unavailable, or unknown with material risk (e.g., an unverified Azure parity claim per `SOURCE-GAP-PROVMTX-04`)
- Policy or authorization restriction (e.g., `INFOPT-009`'s region negotiation conflicting with a data-residency requirement)
- Tenant-isolation risk
- Quality regression risk (e.g., a hypothetical quantized-tier candidate for `INFOPT-008` that has not cleared the quality-gate discipline)
- Excessive latency from the negotiation itself
- Negotiation overhead exceeding expected benefit (§17's net-value test failing)
- Measurement cannot reliably attribute benefit (no confidence rung above "theoretical," §17)
- Stale capability state (a detection result older than the current execution's policy allows)
- Recovery complexity exceeding value (§23)

A P5 capability is never negotiated simply because it exists or is exposed — every negotiation must clear the applicability, net-value, and policy checks each `INFOPT-NNN` entry states.

## 25. Security, Authorization, and Governance Precedence

Preserved precedence, unchanged from and never overridden by this document (per `CLAUDE.md` rule 2 and `architecture.md` §47's governance-plane discipline):

**Security / Authorization / Policy / Governance** > **Correctness / Safety** > **Required Quality** > **Execution Integrity** > **Optimization** > **Cost / Token Reduction**

No P5 negotiation in this document ever bypasses authorization, policy, tenant isolation, or a required quality/security control merely because a capability appears net-beneficial or cheaper. `INFOPT-009` (region/deployment negotiation) is the entry most exposed to this risk and states explicitly that compliance/residency constraints are evaluated before any cost/latency-driven negotiation. Tenant isolation (`conventions.md` §13.2: "all cache keys must include `tenant_id` as the first namespace component... cross-tenant... is unconditionally prohibited") applies to any measurement/observability record this document's entries produce, exactly as it applies everywhere else in this repository.

## 26. Observability

P5-specific observability signals (deferred in canonical schema form to `observability.md`, not yet generated — this section states requirements, not a dashboard implementation):

Selected provider/model; `INFOPT-NNN` capability considered; capability detected/eligible/negotiated/accepted/rejected; negotiation latency; request latency; expected benefit (confidence rung); actual/measured benefit; optimization overhead; provider/infrastructure attribution note; fallback taken; capability failure; retry; provider/model switch; optimization skipped; `DO_NOT_OPTIMIZE` selected (with reason category from §24); measurement-confidence rung. All of the above are recorded in the `inference_serving.*` namespace (§19) — this section does not define alerting thresholds, dashboard layout, or metric-pipeline implementation, which remain `observability.md`'s scope.

## 27. Failure and Recovery Matrix

| Failure | Detection | Safe Response | Recovery |
|---|---|---|---|
| Capability unknown | CAR detection returns no data | Fail-open — proceed without negotiating | Re-check on next execution or policy-defined refresh interval |
| Capability unavailable | Provider-matrix fact = Not exposed / negotiation rejected | Fail-open — proceed without the capability | No retry needed; not a transient condition |
| Negotiation failure | Provider adapter returns an error/timeout for the hint | Fail-open — proceed on the unoptimized path | Retry per adapter-level resilience policy (not P5-specific) |
| Provider degradation/timeout | CAR/adapter-level signal | Fail-open; if sustained, CAR failover (§46.2.10) | Circuit breaker + cascade selection |
| Request queue overload (provider-side) | Rate-limit/429 (`EC-064`) | Retry/backoff per adapter policy; CAR failover if sustained | Failover to next compliant model/provider |
| Capability mismatch after failover | CAR validates failover model against context/tool/compliance constraints | Reject failover candidate, try next in cascade | Continue cascade or safe abort if none qualify |
| Stale capability metadata | Detection timestamp older than policy-defined freshness window | Re-detect before negotiating | N/A |
| Authorization/policy/tenant change mid-execution | RE reconciliation (§46) detects state divergence | Revalidate before continuing negotiation | Renegotiate or fall back |
| Measurement failure | Attribution record cannot be completed | Mark `unverified`, exclude from savings reporting (`CLAUDE.md` rule 5) | N/A — never fabricate the missing measurement |
| Infrastructure optimization failure (post-negotiation) | Provider signals degraded/failed execution of the negotiated capability | Fail-open; treat as if capability were never negotiated for this request | Standard request-level retry/escalation, not P5-specific |

Not every failure requires retry — several rows above resolve directly to fail-open with no retry (capability unknown/unavailable, measurement failure).

## 28. Control-Plane Algorithms

Structured Control-Plane decision logic only — every algorithm below describes **what EAIOC decides**, never **how the inference infrastructure internally executes it** (§4). No algorithm here designs KV-cache eviction, a batching scheduler, a speculative-decoding engine, a quantization algorithm, a GPU kernel, a provider queue implementation, or prefill/decode kernels — those remain provider/infrastructure-owned per H17/H18.

### Algorithm A — P5 Capability Detection

```
INPUT: execution_state, provider_id, model_id, capability (INFOPT-NNN)
  1. Reconcile current execution state (RE, ARCH §46) — do not act on a stale snapshot.
  2. Confirm provider/model is still the one T0.1 selected for this execution.
  3. Query the provider/model profile (ARCH §25) for the capability's exposure dimension,
     populated from provider-matrix.md §8.
  4. Classify exposure as exactly one of: VERIFIED-available | Conditional | Unknown | Not-exposed.
     Never upgrade Unknown to VERIFIED-available without a provider-matrix.md §8 citation.
  5. If a runtime-observable signal exists (e.g. a rate-limit response for INFOPT-011),
     fold it into the classification; a static catalog fact and a runtime signal may disagree —
     runtime observation takes precedence for this execution only, and does not rewrite the
     static fact.
  6. RETURN capability_state (one of the four classifications) + evidence citation.
  7. Never invent a capability_state value not derivable from provider-matrix.md or a
     runtime-observable signal.
```

### Algorithm B — P5 Eligibility Decision

```
INPUT: capability_state, tenant_id, authorization_context, policy_context,
       data_governance_context, quality_requirements, latency_requirements
  1. If capability_state != VERIFIED-available and != Conditional -> RETURN NOT_ELIGIBLE.
  2. Validate authorization_context is already established for this request (never re-derived
     or weakened here, CLAUDE.md rule 2).
  3. Validate policy_context permits this capability for this tenant/request class.
  4. Validate tenant isolation is unaffected (conventions.md §13.2 — no cross-tenant negotiation
     state, no cross-tenant measurement record).
  5. Validate capability compatibility with the request's data-governance classification
     (e.g. cache-eligibility gate for INFOPT-001, residency requirement for INFOPT-009).
  6. Validate quality/safety requirements are unaffected by negotiating this capability.
  7. RETURN one of: ELIGIBLE | NOT_ELIGIBLE | UNKNOWN (UNKNOWN when capability_state itself
     is Conditional/Unknown and no runtime signal resolves it further).
```

### Algorithm C — P5 Net-Value Decision

```
INPUT: eligibility (from Algorithm B), expected_benefit (confidence-ladder rung, §18),
       optimization_overhead, negotiation_overhead, latency_impact, retry_fallback_cost,
       quality_impact, policy_constraints
  1. If eligibility != ELIGIBLE -> RETURN DO_NOT_OPTIMIZE.
  2. Compute net_value using the existing §18/§47.4.2 Net Optimization Value accounting —
     benefit terms minus overhead/cost terms. No new economics formula is introduced here.
  3. If net_value is not positive -> RETURN DO_NOT_OPTIMIZE.
  4. If expected_benefit's confidence rung is below "Estimated" (i.e. only "Theoretical") AND
     the decision would be reported as a savings claim -> treat as informational only, per
     CLAUDE.md rule 5; the decision to attempt negotiation may still proceed, but no benefit
     may be *reported* below the Measured/Verified rungs.
  5. If policy_constraints are not satisfied -> RETURN DO_NOT_OPTIMIZE (policy/security always
     outranks a positive net_value, §25).
  6. RETURN NEGOTIATE.
  Numerical thresholds for "positive" net_value are PROPOSED DEFAULT — VALIDATE LOCALLY;
  no source document supplies a concrete number.
```

### Algorithm D — Provider Adapter Negotiation

```
INPUT: decision (NEGOTIATE, from Algorithm C), capability, execution_state
  1. Confirm decision == NEGOTIATE; if not, skip negotiation entirely.
  2. Construct a provider_hints entry (e.g. cache-affinity hint, batch-eligibility hint,
     deployment/region preference) — never a first-class core-schema field
     (CLAUDE.md rule 1, interfaces.md §33).
  3. Send the hint through the provider adapter (opaque to the Control Plane beyond this
     boundary).
  4. Handle the adapter response: ACCEPTED | REJECTED | UNSUPPORTED | TIMEOUT.
  5. On ACCEPTED: proceed to request execution (opaque) and measurement (Algorithm E).
  6. On REJECTED | UNSUPPORTED | TIMEOUT: fail open (Algorithm G) — the request continues
     without the capability.
  7. Never retry a negotiation indefinitely; retry policy is the adapter-level resilience
     policy already established, not a new P5-specific retry loop.
```

### Algorithm E — P5 Measurement Attribution

```
INPUT: INFOPT-NNN, capability_state, eligibility, decision, adapter_response,
       request_latency, expected_benefit, measured_effect (if available)
  1. Write one attribution record per decision to the inference_serving.* namespace (§19) —
     never to a Layer 1/2 (context_optimization.*, cache) namespace.
  2. Record: capability considered, detected state, eligible (bool), negotiated (bool),
     accepted/rejected, negotiation latency, request latency, expected_benefit rung,
     measured_effect (if any), optimization overhead, attribution_confidence, fallback_taken (bool).
  3. If measured_effect cannot be reliably attributed to this capability specifically
     (e.g. a confound with a concurrent Layer 1/2 change) -> mark attribution_confidence
     as "unverified" and exclude from any savings report (CLAUDE.md rule 5) rather than
     estimate it.
  4. Never combine this record's benefit with a Layer 1/2 savings report (AC-036, EC-076).
```

### Algorithm F — P5 Revalidation

```
INPUT: prior_negotiation_state, execution_state
TRIGGERS: provider change | model change | authorization change | policy change |
          tenant-context change | capability-state change | execution supersession (SPM)
  1. Reconcile current execution state (RE) before touching prior_negotiation_state.
  2. Re-run Algorithm A (capability detection) against the current state — never assume
     prior_negotiation_state is still accurate.
  3. Re-run Algorithm B (eligibility) against the current authorization/policy/tenant context.
  4. If still ELIGIBLE and net-value still positive (Algorithm C) -> renegotiate (Algorithm D).
  5. If not -> fall back to the unoptimized path (Algorithm G).
  6. Never blindly reuse or replay a stale negotiation result — "Resume != Replay" (§23,
     AC-049/AC-053).
```

### Algorithm G — P5 Fallback

```
INPUT: failure_type (capability-unavailable | negotiation-failure | provider-degradation |
       provider-timeout | queue-pressure | stale-metadata | authorization/policy-change |
       measurement-failure)
  1. Classify failure_type (§27's Failure and Recovery Matrix enumerates the mapping).
  2. DEFAULT: continue the request without the P5 capability — fail-open (§4/§47.8);
     Layer 3 unavailability never blocks a request.
  3. If failure_type indicates sustained provider degradation/queue pressure -> defer to
     CAR's existing circuit breaker and cascade failover (ARCH §46.2.10) — this algorithm
     does not reimplement CAR, only triggers it.
  4. If failure_type is measurement-failure -> mark the record "unverified" and exclude from
     savings reporting (Algorithm E, step 3) — this is not a request-blocking failure.
  5. Never escalate a P5-layer failure into a full request rejection; that would conflate
     an optimization-stage failure with an authorization/policy failure (CLAUDE.md rule 2).
```

### Algorithm H — DO_NOT_OPTIMIZE

```
INPUT: eligibility, net_value_decision, policy_constraints, capability_state,
       recovery_complexity_estimate
RETURN DO_NOT_OPTIMIZE when any of:
  - capability_state is Not-exposed, or Unknown with material risk
  - policy or authorization restricts this negotiation
  - tenant-isolation risk exists
  - capability_state is stale beyond the policy-defined freshness window
  - quality-regression risk exists and has not cleared the applicable quality gate
  - negotiation overhead exceeds expected benefit (Algorithm C)
  - measurement cannot reliably attribute benefit (no rung above "Theoretical")
  - recovery_complexity_estimate exceeds the expected benefit
This is a legitimate, proactively-selected outcome (EC-154, §24) — never only a failure
fallback. A capability is never negotiated merely because it exists or is exposed.
```

## 29. P5 Capability Matrix

Summary only — derived from the per-entry fields in §7–§17; not a second source of truth. Where an entry's fields above state more nuance (e.g. `INFOPT-003`'s Batch-API-vs-continuous-batching distinction), that nuance governs; this table is a compact index into it.

| INFOPT | Capability | Detection | Eligibility (today) | Negotiation | Measurement | Fallback |
|---|---|---|---|---|---|---|
| `INFOPT-001` | Prefix/KV-cache reuse | CAR + ARCH §25 profile, `provider-matrix.md` §8 row 1 | Conditionally eligible (Anthropic/OpenAI Yes; Azure Conditional) | Cache-affinity hint via provider adapter | `inference_serving.*`, separate from `cache-strategy.md` savings | Fail-open, no hint |
| `INFOPT-002` | KV-cache compression | CAR + §25 profile, §8 row 2 | Not eligible (Unknown for all three) | N/A — reserved hint slot | `inference_serving.*` (if ever negotiated) | Fail-open (trivial — never attempted) |
| `INFOPT-003` | Continuous batching | CAR + §25 "batch support", §8 row 3 | Not eligible for continuous-batching negotiation (Unknown); Batch API is a distinct T3.5 feature | N/A for continuous batching itself | `inference_serving.*`, kept distinct from T3.5's own accounting | Fail-open |
| `INFOPT-004` | Request scheduling | CAR + §25, §8 row 4 | Not eligible (Not exposed, all three) | N/A — reserved hint slot | `inference_serving.*` | Fail-open |
| `INFOPT-005` | Prefill optimization | CAR + §25, §8 row 5 | Not eligible (Not exposed, all three) | N/A — reserved hint slot | `inference_serving.*` | Fail-open |
| `INFOPT-006` | Decode optimization | CAR + §25, §8 row 6 | Not eligible (Not exposed, all three) | N/A — reserved hint slot | `inference_serving.*` | Fail-open |
| `INFOPT-007` | Speculative decoding | CAR + §25, §8 row 7 | Not eligible (Not exposed, all three) | N/A — reserved hint slot | `inference_serving.*` | Fail-open; if ever negotiated, quality fallback reuses `AR-004`'s verifier-guided escalation |
| `INFOPT-008` | Quantization | CAR + §25, §8 row 8 | Not eligible (Not exposed, all three) | N/A — reserved hint slot | `inference_serving.*` | Fail-open |
| `INFOPT-009` | Model replica selection | CAR + §25 provider-availability, §8 row 9 | Conditionally eligible for Azure region/deployment selection only | Deployment/region preference hint, residency-gated | `inference_serving.*` | CAR circuit breaker + cascade failover |
| `INFOPT-010` | GPU utilization optimization | CAR + §25, §8 row 10 | Not eligible (Not exposed; not fact-level observable) | N/A — no hint slot proposed | `inference_serving.*` (if ever exposed) | Fail-open (trivial) |
| `INFOPT-011` | Inference queue management | CAR monitors rate-limit/429 symptom (`EC-064`), §8 row 11 | Not eligible for direct priority negotiation; symptom-only visibility | N/A for priority hints; adapter-level retry/backoff only | `inference_serving.*` | CAR circuit breaker + cascade failover on sustained pressure |

**Provider Exposure Reference Matrix** (sourced only from `provider-matrix.md` §8 — qualifications preserved exactly as that document states them; never reinterpreted, never converted between Unknown/Not-exposed/a universal-impossibility claim):

| INFOPT | Capability | Anthropic | OpenAI | Azure OpenAI | Source |
|---|---|---|---|---|---|
| `INFOPT-001` | Prefix/KV-cache reuse | Yes (VERIFIED, `claude.com/pricing`, 17-09-2026) | Yes (VERIFIED, `developers.openai.com/api/docs/pricing`, 17-09-2026) | Conditional (`SOURCE-GAP-PROVMTX-04`) | `provider-matrix.md` §8 row 1 |
| `INFOPT-002` | KV-cache compression | Unknown | Unknown | Unknown | `provider-matrix.md` §8 row 2 |
| `INFOPT-003` | Continuous batching | Unknown (Batch API discount VERIFIED as a separate feature) | Unknown (Batch API discount VERIFIED as a separate feature) | Unknown | `provider-matrix.md` §8 row 3 |
| `INFOPT-004` | Request scheduling | Not exposed | Not exposed | Not exposed | `provider-matrix.md` §8 row 4 |
| `INFOPT-005` | Prefill optimization | Not exposed | Not exposed | Not exposed | `provider-matrix.md` §8 row 5 |
| `INFOPT-006` | Decode optimization | Not exposed | Not exposed | Not exposed | `provider-matrix.md` §8 row 6 |
| `INFOPT-007` | Speculative decoding | Not exposed | Not exposed | Not exposed | `provider-matrix.md` §8 row 7 |
| `INFOPT-008` | Quantization | Not exposed | Not exposed | Not exposed | `provider-matrix.md` §8 row 8 |
| `INFOPT-009` | Model replica selection | Not exposed to API consumers | Not exposed to API consumers | Conditional (region/deployment selection, VERIFIED) | `provider-matrix.md` §8 row 9 |
| `INFOPT-010` | GPU utilization optimization | Not exposed | Not exposed | Not exposed | `provider-matrix.md` §8 row 10 |
| `INFOPT-011` | Inference queue management | Not exposed directly (symptom-only) | Not exposed directly (symptom-only) | Not exposed directly (symptom-only) | `provider-matrix.md` §8 row 11 |

## 30. Net Optimization Decision Matrix

Qualitative categories only, per §18's benefit-confidence ladder — no numerical threshold is invented here.

| # | Condition | Expected Benefit | Overhead | Policy/Security | Decision |
|---|---|---|---|---|---|
| 1 | Capability VERIFIED-available, workload-appropriate, low negotiation overhead | Estimated or higher | Low | Compliant | `NEGOTIATE` |
| 2 | Capability available, but negotiation/measurement overhead is disproportionate | Estimated | High | Compliant | `DO_NOT_OPTIMIZE` (Algorithm C step 3) |
| 3 | Capability exposure Unknown | Theoretical at best | N/A — undeterminable | Compliant | `DO_NOT_OPTIMIZE` (Algorithm B step 1/7) |
| 4 | Capability Not-exposed | None | N/A | Compliant | `DO_NOT_OPTIMIZE` |
| 5 | Capability available, but policy restricts it for this tenant/request class | Estimated or higher | Any | Non-compliant | `DO_NOT_OPTIMIZE` (policy outranks benefit, §25) |
| 6 | Capability available, but authorization does not cover it | Estimated or higher | Any | Non-compliant | `DO_NOT_OPTIMIZE` |
| 7 | Negotiation or its measurement would create cross-tenant state/leakage risk | N/A | N/A | Non-compliant | `DO_NOT_OPTIMIZE` (conventions.md §13.2, absolute) |
| 8 | Capability technically available, but selecting it risks a quality regression not yet cleared by the applicable quality gate | Estimated | Low-Medium | Compliant | `DO_NOT_OPTIMIZE` |
| 9 | Capability detection result is older than the policy-defined freshness window | Unknown (stale) | N/A | Compliant | `DO_NOT_OPTIMIZE` until re-detected (Algorithm A/F) |
| 10 | Measurement attribution cannot be reliably isolated from a concurrent Layer 1/2 change | Unverifiable | N/A | Compliant | `DO_NOT_OPTIMIZE` (never fabricate an attributed benefit, `CLAUDE.md` rule 5) |
| 11 | Revalidation/renegotiation recovery complexity exceeds the benefit at stake | Estimated, low magnitude | High (recovery cost) | Compliant | `DO_NOT_OPTIMIZE` |
| 12 | Provider degradation/timeout observed for the capability's provider | N/A — provider-side fault | N/A | Compliant | `DO_NOT_OPTIMIZE` for this capability this request; defer to CAR failover (Algorithm G step 3), never a P5-specific retry loop |

## 31. Validation Coverage Matrix

`INFOPT → Requirement/Behavior → Scenario → Edge Case → Expected Behavior → Coverage`. Only IDs verified to exist in `edge-cases.md`/`scenario-matrix.md` are used; no capability-specific scenario is fabricated where none exists.

| INFOPT | Requirement/Behavior | Scenario | Edge Case | Expected Behavior | Coverage |
|---|---|---|---|---|---|
| `INFOPT-001`–`011` (all) | Measurement separation (AC-036) | `SCN-CMP-055`, `SCN-OPT-009` | `EC-076` | P5 effect measured in a separate namespace; never combined with Layer 1/2 savings | **COLLECTIVE** — validates the requirement across all 11 capabilities at once; no capability-specific scenario exists in `scenario-matrix.md`, and none is fabricated here |
| `INFOPT-001`–`011` (all) | `DO_NOT_OPTIMIZE` as a legitimate proactive outcome | `SCN-NOPT-001` | `EC-154` | Optimization is skipped without being treated as a failure | **COLLECTIVE** — `SCN-NOPT-001` validates the general behavior (illustrated for context compression, not P5-specific); applied here by analogy, not by a dedicated P5 scenario |
| `INFOPT-009` | CAR failover to next available model/deployment | *(none dedicated)* | `EC-124` (model-specific overhead/context-budget staleness after a model/provider switch) | Failover candidate revalidated against context/tool/compliance constraints before use | **THIN — no dedicated scenario in scenario-matrix.md**; `EC-124` is the closest related, non-P5-specific edge case for the underlying CAR mechanism this entry extends |
| `INFOPT-011` | Sustained provider rate-limiting triggers failover, not request block | *(none dedicated)* | `EC-064` (provider returns rate-limit error repeatedly) | Retry/backoff, then CAR failover if sustained; request never blocked solely for queue pressure given a compliant failover target | **THIN — no dedicated P5 scenario**; `EC-064` validates the underlying rate-limit-handling behavior this entry's fallback reuses |
| `INFOPT-001`–`008`, `010` | Capability-specific detection/eligibility/negotiation behavior | *(none)* | *(none)* | N/A | **NOT AVAILABLE — collective validation only.** No dedicated `SCN-*`/`EC-*` scenario isolates any single one of these 9 capabilities; this document does not manufacture one. |

## 32. Requirement / Scenario / Edge-Case Traceability

`Requirement/Source → INFOPT → Scenario → Edge Case → Expected Behavior → Status`. Where no dedicated requirement ID exists, the source section reference is used directly rather than inventing one.

| Requirement/Source | INFOPT | Scenario | Edge Case | Expected Behavior | Status |
|---|---|---|---|---|---|
| PS §52.17 (H17 — integration, not implementation) | All 11 | *(none dedicated — architectural boundary, not a runtime scenario)* | *(none)* | No entry claims Control-Plane implementation ownership of any P5 mechanism | **NO DEDICATED SCENARIO** — enforced structurally (§4, every entry's Non-Goals field), not scenario-validated, consistent with `ARCH §47.9`'s own treatment of H17 as a scope-boundary constraint rather than a runtime interface |
| PS §52.18 / `architecture.md` §47.9 (H18 — six-category ownership) | All 11 | `SCN-OPT-009` | *(none dedicated to H18 itself)* | A measurement is never attributed to more than one ownership category | **COLLECTIVE** |
| `AC-036` (distinguish context vs. inference-serving optimization) | All 11 | `SCN-CMP-054`, `SCN-CMP-055`, `SCN-OPT-009` | `EC-076` | Separate metrics namespace maintained; savings never combined without decomposition | **COLLECTIVE** |
| `architecture.md` §26 (P5 Index — the 11 capabilities themselves) | All 11 | *(none — index-level fact, not a runtime scenario)* | *(none)* | Names/order preserved verbatim from `optimization-catalog.md`'s P5 Index | **NO DEDICATED SCENARIO** |
| Fail-open requirement (`conventions.md` §16.2; `architecture.md` §47.8) | All 11 | *(none dedicated)* | *(none dedicated — general fail-open pattern, not P5-specific)* | Capability unavailability/negotiation failure never blocks the request | **NO DEDICATED SCENARIO** — enforced in every entry's Fallback Behavior field and §27's Failure and Recovery Matrix |
| Security/policy/tenant-isolation precedence (`CLAUDE.md` rule 2/4; `conventions.md` §13.2) | All 11 | *(none dedicated)* | *(none dedicated)* | No P5 negotiation ever bypasses authorization, policy, or tenant isolation | **NO DEDICATED SCENARIO** — this document's own structural constraint (§25), inherited from the repository-wide rule |
| Provider exposure boundary (`provider-matrix.md` §8) | All 11 | *(provider-matrix.md's own validation, not re-validated here)* | *(none)* | Exposure facts cited, never re-derived or invented | **CITED, NOT RE-VALIDATED** |
| Cache boundary (`cache-strategy.md` `CACHE-005`) | `INFOPT-001` | *(cache-strategy.md's own validation)* | *(none)* | `PROVIDER_NATIVE` key/TTL/economics never redefined here | **CITED, NOT RE-VALIDATED** |
| Agent routing boundary (`agent-optimization.md` `AR-001`/`AR-004`) | `INFOPT-001`, `007`, `009` | *(agent-optimization.md's own validation)* | *(none)* | Model-routing/cascade decision logic never redefined here | **CITED, NOT RE-VALIDATED** |
| CAR failover mechanism (`architecture.md` §46.2.10) | `INFOPT-009`, `011` | *(none dedicated)* | `EC-124` (adjacent, `INFOPT-009`); *(none, `INFOPT-011`)* | Failover candidate validated against context/tool/compliance constraints before use | **THIN** (`INFOPT-009`) / **NO DEDICATED SCENARIO** (`INFOPT-011`) |
| Rate-limit/queue-pressure symptom handling | `INFOPT-011` | *(none dedicated)* | `EC-064` | Retry/backoff then failover, never a hard block given a compliant target | **THIN** |
| `DO_NOT_OPTIMIZE` as legitimate outcome (`edge-cases.md` `EC-154`) | All 11 | `SCN-NOPT-001` | `EC-154` | Optimization proactively skipped without being logged as a failure | **COLLECTIVE** |

## 33. Cross-Document Ownership Boundary

| Concern | Owning Document | How This Document Interacts |
|---|---|---|
| P5 capability index/names | `optimization-catalog.md` | Cites the 11 stubs verbatim; elaborates beneath each, never redefines the stub's `Control-Plane Role` line |
| P5 Control-Plane elaboration | **`inference-optimization.md` (this document)** | — |
| Provider/model capability exposure | `provider-matrix.md` | Cites §8 exposure facts per row; never restates or re-derives them |
| Provider-native cache facts | `provider-matrix.md` | Cited via `INFOPT-001`'s Cross-Provider Variance field |
| Cache key/TTL/invalidation/economics | `cache-strategy.md` | Cited via `CACHE-005`; never redefined |
| Model routing | `architecture.md` §10.1/§13.4, `agent-optimization.md` `AR-001`/`AR-004` | Cited as an already-made decision; this document owns only the capability-negotiation layer beneath it |
| Agent-loop optimization | `agent-optimization.md` | Cited for H09 and for `AR-001`/`AR-004`; never duplicated |
| Security | `security.md` (not yet generated) | Referenced conceptually (§25); mechanism deferred |
| Observability | `observability.md` (not yet generated) | Requirements stated (§26); schema/dashboard deferred |
| Evaluation | `eval.md` (not yet generated) | Not addressed here |
| Scaling | `SCALING.md` (not yet generated) | Not addressed here |
| Implementation | `implementation-plan.md` (not yet generated) | Not addressed here |
| Requirements traceability | `requirements-traceability.md` (not yet generated) | Not created by this document; acknowledged as planned, not created, consistent with `agent-optimization.md`'s own treatment |

## 34. Coverage Matrix

| Metric | Count |
|---|---|
| Canonical `INFOPT-NNN` entries | 11 (exactly, matching `architecture.md` §26's 11 P5 capabilities, index-level names preserved verbatim) |
| Entries citing `provider-matrix.md` §8 | 11/11 (100%) |
| Entries with a currently-exercisable negotiation path (per `provider-matrix.md` §8 exposure facts) | 2/11 (`INFOPT-001` Yes/Conditional; `INFOPT-009` Conditional for Azure region selection) |
| Entries currently Unknown/Not-exposed for all catalogued providers | 9/11 |
| Source gaps | 2 (`SOURCE-GAP-INFOPT-01`, `SOURCE-GAP-INFOPT-02`, §35) |
| Contradictions | 0 (§36) |
| Control-Plane algorithms (§28) | 8/8 (A–H, decision-logic only) |
| P5 Capability Matrix / Provider Exposure Reference Matrix (§29) | 11/11 rows each |
| Net Optimization Decision Matrix (§30) | 12/12 conditions |
| Validation Coverage Matrix (§31) | 11/11 `INFOPT` entries indexed; 2 collective rows, 2 thin rows, 1 "not available" row covering the remaining 9 |
| Requirement/Scenario/Edge-Case Traceability (§32) | 12 requirement rows, honestly marked COLLECTIVE / THIN / NO DEDICATED SCENARIO / CITED-NOT-RE-VALIDATED as applicable |

**Source-citation completeness (per source document):**

| Source Document | Entries Citing It | Coverage |
|---|---|---|
| Problem Statement (PS §52.17/§52.18) | 11/11 (100%) | Comprehensive — every entry's Traceability field cites PS §52.17 |
| Architecture (ARCH §26, §47.8/§47.9, plus the specific interacting component section) | 11/11 (100%) | Comprehensive |
| Interfaces (INTF §32.4, §33) | Cited at the document level (§3, §4) rather than per-entry — no `INFOPT`-specific interface exists yet (a genuine, honestly-flagged thin spot, not fabricated) | Adequate |
| Conventions (CONV §13.2, §16.2) | Cited at the document level (§4, §25) | Adequate |
| Edge Cases (`EC-076`, `EC-064`, `EC-154`) | 11/11 cite `EC-076`; `INFOPT-011` additionally cites `EC-064`; §24 cites `EC-154` | Strong for the collective measurement requirement; thin for capability-specific edge cases, honestly so (none exist) |
| Scenario Matrix (`SCN-CMP-055`, `SCN-OPT-009`, `SCN-NOPT-001`) | 11/11 cite the collective pair; §24 cites `SCN-NOPT-001` | Strong, collectively; no capability-specific scenario fabricated |
| `provider-matrix.md` §8 | 11/11 (100%) | Comprehensive |
| `cache-strategy.md` `CACHE-005` | 1/11 (`INFOPT-001` only — the only entry with a cache-mechanism interaction) | Adequate, scoped correctly |
| `agent-optimization.md` (`AR-001`, `AR-004`, H09) | 3/11 directly (`INFOPT-001`, `INFOPT-007`, `INFOPT-009`) plus document-level H09 restatement (§22) | Adequate |

**Maturity Level distribution:** 11/11 entries are Level 0 — RESEARCH, consistent with the rest of the chain; no entry claims Level 2 or higher.

## 35. Source Gaps

| ID | Missing Information | Affected Capability/Section | Why It Matters | Expected Authoritative Source | Current Disposition | Blocking? |
|---|---|---|---|---|---|---|
| `SOURCE-GAP-INFOPT-01` | No source document (PS, ES, ARCH, INTF, CONV) defines a concrete provider-adapter negotiation-parameter *schema* (field names/types) for any of the 11 P5 capabilities beyond the general `provider_hints: map<string, any>` contract | All 11 entries' "Provider Adapter Negotiation" fields | Without a concrete schema, this document can describe negotiation *conceptually* (a hint passed through the adapter) but cannot specify field-level structure without inventing one, which `CLAUDE.md` rule 5/architecture.md §47.12 (H19) prohibit | A future revision of `interfaces.md` defining P5-specific negotiation-hint fields, if the project decides P5 negotiation needs one | Recorded, not resolved by inventing a schema | No — non-blocking; the general `provider_hints` contract already suffices for this document's scope |
| `SOURCE-GAP-INFOPT-02` | `provider-matrix.md` §8's exposure facts for capabilities 2 and 4–8, 10–11 are recorded as "Unknown"/"Not exposed" based on a single fetch date (17-09-2026) of each provider's public pricing/docs page; none of these have been checked against a dedicated capabilities/API-reference page (as opposed to the pricing page) that might document them separately | `INFOPT-002`, `INFOPT-004`–`008`, `INFOPT-010`–`011` | A pricing page not mentioning a capability is weaker evidence of true non-exposure than a capabilities/API-reference page explicitly stating it is unsupported; this document inherits, not corrects, `provider-matrix.md`'s own evidence standard | A future `provider-matrix.md` revision performing a capabilities-page-specific verification pass | Recorded as an inherited limitation; not independently re-verified here, per the single-file-scoped reconciliation rule (this document does not modify `provider-matrix.md`) | No — non-blocking; the current facts are honestly labeled Unknown/Not exposed rather than asserted as definitively absent |

No source gap from `optimization-catalog.md`, `provider-matrix.md`, or `cache-strategy.md` that bears specifically on P5/inference-serving scope was found requiring a new record beyond the two above; `SOURCE-GAP-PROVMTX-04` (Azure prompt-cache parity) is cited where relevant (`INFOPT-001`) rather than re-opened under a new ID.

## 36. Source Contradictions

None found. Every source consulted (§3) is internally consistent on the H17/H18 boundary, the 11 capability names/order, and the measurement-separation requirement — `optimization-catalog.md`, `provider-matrix.md`, `cache-strategy.md`, and `agent-optimization.md` all describe this boundary identically, and no downstream document has yet redefined it in a conflicting way.

## 37. Final Report

**Document generated:** `docs/inference-optimization.md`, this file.
**`INFOPT-NNN` count:** 11/11 — `INFOPT-001` through `INFOPT-011`, in `architecture.md` §26's exact order, names verbatim, matching `provider-matrix.md` §8's numbering.
**Source documents actually read (this session):** root `CLAUDE.md`; `architecture.md` §10.1–10.3, §13.4–13.5, §25, §26, §36, §41, §42, §46.2.10, §46.2.12, §46.2.13, §47.4.1–47.4.3, §47.8, §47.9, §47.10, §47.12, §52; `interfaces.md` §32.4, §33; `conventions.md` §13.2, §16.2, P5 section; `edge-cases.md` `EC-076`, `EC-151`, `EC-154`, `EC-064`; `scenario-matrix.md` `SCN-CMP-055`, `SCN-OPT-009`, `SCN-NOPT-001`, `SCN-STATE-005`; `optimization-catalog.md` P5 Index section; `provider-matrix.md` §2, §8; `cache-strategy.md` `CACHE-005`; `agent-optimization.md` `AR-001`, `AR-004`, H09 restatement; problem-statement §52.17/§52.18 (verbatim H17 text located and quoted in §4).
**Key architecture boundaries preserved:** H17 (integration, not implementation) and H18 (six-category measurement-ownership separation) are restated at the top of this document (§4) and enforced in every one of the 11 entries' Non-Goals fields; no entry designs a KV-cache engine, batching scheduler, speculative-decoding mechanism, quantization pipeline, GPU-kernel formula, or queue implementation.
**Provider/cache/agent integration:** `provider-matrix.md` §8 cited by row in every entry (never restated); `cache-strategy.md` `CACHE-005` cited, not redefined, for `INFOPT-001`; `agent-optimization.md` `AR-001`/`AR-004`/H09 cited, not redefined, for `INFOPT-001`/`007`/`009` and §22.
**Validation coverage:** collective (`EC-076`, `SCN-CMP-055`, `SCN-OPT-009`) across all 11 entries; no capability-specific scenario exists and none was fabricated. §31's Validation Coverage Matrix and §32's Requirement/Scenario/Edge-Case Traceability make this honestly explicit per row (COLLECTIVE / THIN / NO DEDICATED SCENARIO / NOT AVAILABLE / CITED-NOT-RE-VALIDATED) rather than presenting collective coverage as if it were capability-specific.
**Remediation-pass artifacts (added this pass, all complete):**
- Control-Plane Algorithms (§28): 8/8 present (A–H) — capability detection, eligibility, net-value decision, provider-adapter negotiation, measurement attribution, revalidation, fallback, `DO_NOT_OPTIMIZE`. All Control-Plane decision logic only; none reaches into infrastructure-implementation territory.
- P5 Capability Matrix + Provider Exposure Reference Matrix (§29): 11/11 rows each, summarizing §7–§17's entries without introducing new facts.
- Net Optimization Decision Matrix (§30): 12/12 conditions, qualitative categories only, no invented numeric thresholds.
- Validation Coverage Matrix (§31): 11/11 `INFOPT` entries indexed against real `EC-NNN`/`SCN-NNN` IDs only; 9 of 11 capabilities honestly marked "NOT AVAILABLE — collective validation only" since no dedicated scenario isolates any single one of them.
- Requirement/Scenario/Edge-Case Traceability (§32): 12 requirement rows tracing to PS/ARCH sections, `AC-036`, and sibling-document boundaries, each marked COLLECTIVE/THIN/NO DEDICATED SCENARIO/CITED-NOT-RE-VALIDATED rather than claimed complete where it isn't.
- Two pre-existing internal section-number cross-references in §5 and one in `INFOPT-011` were also corrected during this pass (they pointed to the wrong section after the document's own numbering shifted during original generation); no content was changed, only the reference numbers.
**Source gaps:** 2 — `SOURCE-GAP-INFOPT-01` (no concrete negotiation-parameter schema beyond `provider_hints`), `SOURCE-GAP-INFOPT-02` (provider exposure facts inherited from a pricing-page-only verification pass) — both non-blocking, both unchanged from the prior pass; no new gap was manufactured to pad completeness.
**Contradictions:** 0 — unchanged.
**Proposed/illustrative items:** the benefit-confidence ladder (§18) and the per-decision measurement field list (§19) are both `PROPOSED METHODOLOGY` — no source enumerates either exhaustively, though each rung/field individually follows from an existing rule (`CLAUDE.md` rule 5, `architecture.md` §42).
**Deferred implementation areas:** every one of the 11 capabilities' actual infrastructure mechanism (§4's out-of-scope list); observability schema/dashboard implementation (`observability.md`); security mechanism detail (`security.md`).
**Limitations:** 9 of 11 capabilities currently have no exercisable negotiation path at all (Unknown/Not-exposed per `provider-matrix.md` §8) — this document defines the pathway each would use if a provider ever exposes them, without inventing present-day availability.
**H17/H18 ownership confirmation (mandatory):** No claim of Control-Plane implementation ownership of any P5 mechanism appears anywhere in this document. Every one of the 11 `INFOPT-NNN` entries states its Control-Plane Role as "awareness / detection / negotiation / measurement only" and its Non-Goals field explicitly excludes the corresponding infrastructure mechanism. §4 restates the H17/H18 boundary prominently and §18/§19 enforce the net-value and measurement-separation disciplines that depend on it.
**File-scope confirmation:** only `docs/inference-optimization.md` was created in this pass. No upstream document (`problemStatement.txt`, Engineering Spec, `architecture.md`, `interfaces.md`, `conventions.md`, `edge-cases.md`, `scenario-matrix.md`, `optimization-catalog.md`, `provider-matrix.md`, `cache-strategy.md`, `agent-optimization.md`) was modified. No downstream document (`quality-gates.md`, `requirements-traceability.md`, or any other) was created.

---

## Inference Optimization Readiness

READY FOR NEXT DOCUMENTATION PHASE
