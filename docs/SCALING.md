# Scaling, Capacity, Throughput, Concurrency, Resource Management, and Enterprise Scale Reference

**Document ID:** EAIOC-SCALING-001
**Status:** PRE-IMPLEMENTATION — Level 0 (RESEARCH)
**Version:** 1.0.0
**Generated:** 2026-09-18 (per repository date)
**Generation prompt:** `docs/prompts/generate-SCALING.prompt.md` (V2 Consolidated Master Prompt)

**Position in the documentation chain:** Tenth generated downstream document, following `optimization-catalog.md` → `provider-matrix.md` → `cache-strategy.md` → `agent-optimization.md` → `inference-optimization.md` → `quality-gates.md` → `security.md` → `observability.md` → `eval.md`. Gated by each of those nine documents' own passing readiness verdict. Next in chain: `implementation-plan` (not generated here, not cascaded into).

**Sources read in full or by targeted section/ID lookup:** `architecture.md` (§21.1–21.3 EL/rollout cross-reference, §31 Observability and Governance, §33 Enterprise Deployment Model, §34 Rollout Strategy, §37 NFR-008/NFR-014, §47.3.2 XEC, §47.4.1 SPC, §47.4.2 Net Optimization Economics, and a full-text search for `capacity`/`throughput`/`horizontal`/`scal`/`autoscal`/`load shed`/`backpressure`/`queue depth`/`headroom`/`overload` — no additional hits beyond the sections already listed); `interfaces.md` (§43.7 `CrossExecutionCoordinator`/`INTF-069`, §43.8 `SelfProtectionController`/`INTF-070`, `DepthSheddingDecision`/`OverloadSignal`/`security_stages_preserved`, the `OPT-6xxx` `CAPACITY` error class); `conventions.md` (§7.9 SPC, §13.6 SGE, the governance-precedence list including the SPC/XEC bullets — full-text search for the same scaling/capacity terms found no additional enumerable catalog); `edge-cases.md` (`EC-077` verified directly; `EC-191`/`EC-192`/`EC-193`/`EC-194` verified directly); `scenario-matrix.md` (all 30 scenario domains' headers enumerated directly; Domain AA `SCN-CONC-001`–`006` read in full; Domain Z Multi-Tenancy and Domain S/T Cost/Latency spot-checked for burst/fairness/throughput content — none found); `security.md` (GSP-001 SGE, cited only); `observability.md` (confirmed it names no queue-depth/throughput/RPS canonical metric of its own — it explicitly defers "Capacity/throughput targets" to this document); `eval.md` (§2/§13–14 benchmarking-methodology boundary, cited only); `cache-strategy.md`, `agent-optimization.md`, `inference-optimization.md`, `provider-matrix.md`, `optimization-catalog.md` (each consumed for its own already-owned mechanics, not redefined). Root `CLAUDE.md` re-read for scope/ID/process discipline.

**Scope boundary reminder:** This document owns capacity-planning methodology, throughput targets, horizontal-scaling triggers, the operational load-shedding/backpressure procedure layered on top of `SPC`, and the operational concurrency-at-scale analysis layered on top of `XEC`. It does **not** redefine `SPC`'s or `XEC`'s own mechanism, `security.md`'s SGE spend-governance mechanism, `observability.md`'s telemetry/dashboard implementation, `eval.md`'s benchmarking/statistical methodology, or `architecture.md` §47.4.2's Net Optimization Economics cost accounting — all cited, never restated as if newly defined here.

---

## 1. Status and Maturity

This document is **Level 0 — RESEARCH / PRE-IMPLEMENTATION**, consistent with every other document in this chain. No local production traffic, no deployed Control Plane, and no measured capacity/throughput history exist. Accordingly:

- No throughput number, scale-out threshold, headroom percentage, cooldown period, replica count, or SLO in this document is presented as validated or production-proven.
- Every concrete number this document proposes that is not directly stated in an upstream source is tagged `PROPOSED DEFAULT — VALIDATE LOCALLY`.
- Every procedure this document proposes that is not directly stated in an upstream source is tagged `PROPOSED METHODOLOGY`.
- An example used only to illustrate a concept, not proposed as a starting operational value, is tagged `ILLUSTRATIVE EXAMPLE`.
- A calculated relationship between already-canonical source terms is tagged `DERIVED METRIC`.
- This repository has never load-tested, benchmarked at a measured throughput, or proven multi-tenant fairness for EAIOC in production. No claim in this document should be read otherwise.

## 2. How to Read This Document

Every substantive claim carries one of: `SOURCE-DEFINED` (an upstream document states this exactly), `SOURCE-DERIVED` (directly implied by combining source requirements, formulation synthesized here), `PROPOSED METHODOLOGY`, `PROPOSED DEFAULT — VALIDATE LOCALLY`, `DERIVED METRIC`, or `ILLUSTRATIVE EXAMPLE`. `PROPOSED`/`ILLUSTRATIVE` labels are starting points for local validation once an implementation and real traffic exist — never authoritative.

## 3. Scope Boundary

| Concern | Owner |
|---|---|
| Capacity-planning methodology | This document |
| Throughput targets | This document |
| Horizontal-scaling trigger conditions/thresholds | This document |
| Backpressure/load-shedding *operational procedure* | This document, extending the `SPC` boundary |
| Cross-execution concurrency-at-scale *operational analysis* | This document, extending the `XEC` boundary |
| Multi-tenant capacity allocation, fairness/noisy-neighbor protection | This document |
| Per-deployment-mode (§33 A–F) scaling profiles | This document |
| Scale-testing methodology | This document, consuming `eval.md`'s benchmarking methodology |
| `SPC`'s own mechanism (latency/compute budgets, overload detection, depth-shedding decision) | `architecture.md` §47.4.1 / `interfaces.md` §43.8 (cited) |
| `XEC`'s own mechanism (conflict detection, reconciliation, locking) | `architecture.md` §47.3.2 / `interfaces.md` §43.7 (cited) |
| Spend governance (budget throttle/halt) | `security.md` GSP-001/SGE (cited) |
| Observability/telemetry/dashboard implementation | `observability.md` (cited) |
| Benchmarking/A-B/statistical methodology | `eval.md` (cited) |
| Net Optimization Economics cost accounting | `architecture.md` §47.4.2 (cited) |
| Cache mechanics | `cache-strategy.md` (cited) |
| Agent-loop/sub-agent mechanics | `agent-optimization.md` (cited) |
| P5/inference-serving mechanics | `inference-optimization.md` (cited) |
| Provider/model facts and limits | `provider-matrix.md` (cited) |
| Implementation sequencing | `implementation-plan` (not yet generated) |

This document answers **"can the system handle this workload safely?"** — distinct from `eval.md` ("did the optimization actually produce validated benefit?") and `security.md` ("is this workload/action authorized?"). Available capacity never implies authorization, and authorization never implies available capacity — the two questions are independent and must never be conflated.

## 4. Scaling Invariants

1. **Tenant isolation** (root `CLAUDE.md` rule 4, `conventions.md` §13.2) extends to capacity/rate-limiting scoping exactly as it does to caching/memory — one tenant's load must never starve, blend with, or contaminate another tenant's capacity allocation or measurement (`SCN-CONC-003`, `SOURCE-DEFINED` for the state-isolation half; the fairness/quota half is `SOURCE-DERIVED`, see §16).
2. **Security/governance precedence over self-protection** (`SOURCE-DEFINED`, `architecture.md` §47.4.1's exact wording): "self-protection failures are optimization failures (fail-open) *unless* the overload condition would otherwise cause a security/authorization/PII check (SGE, DGE, TMG, HAG, CIS) to be skipped — in that specific case the affected request fails closed rather than proceeding unchecked under load." This is the single most consequential invariant in this document — see §12–13.
3. **`DepthSheddingDecision.security_stages_preserved` is always `true`** (`SOURCE-DEFINED`, `interfaces.md` §43.8's exact invariant note). No load-shedding tier this document proposes may ever cause a `governance/` module to be skipped.
4. **Fail-open on optimization, fail-closed on security** (root `CLAUDE.md` rule 2) applies identically at the capacity dimension: an overloaded optimization stage degrades or falls back; an overloaded governance check never silently passes.
5. **Bounded resource consumption / no unbounded fan-out**: every amplification path this document identifies (§10) must terminate in a bounded worst case, not an open-ended one.
6. **Cost accounting is not capacity accounting**: this document never substitutes a throughput/capacity number for `architecture.md` §47.4.2's Net Optimization Economics terms, or vice versa (§34).
7. **Evaluation integrity under load**: `eval.md`'s benchmarking/A-B evidence must not be distorted by production capacity pressure, and evaluation workload must not consume enough shared capacity to distort the production workload it measures (§26).
8. **Provider-neutrality** (root `CLAUDE.md` rule 1) applies to every scaling mechanism proposed here — no algorithm in this document may hard-code a specific provider's rate-limit behavior; provider-specific facts are consumed from `provider-matrix.md` via `provider_hints`.

## 5. Canonical Scaling-ID Decision

**Finding, independently verified rather than assumed:** no pre-existing enumerable capacity/scaling catalog exists anywhere in `architecture.md`, `interfaces.md`, or `conventions.md`. A full-text search of all three for `capacity`, `throughput`, `horizontal`, `scal`, `concurrency limit`, `rate limit`, `autoscal`, `load shed`, `backpressure`, `queue depth`, `headroom`, and `overload` surfaces only: `NFR-008`'s one-line Scalability statement (traces only to §33 + §31, neither of which is a scaling catalog); `NFR-014`/`SPC`'s already-cited overload-detection mechanism (three signal *types* — queue depth, latency p99, error rate — with no concrete thresholds); `XEC`'s already-cited conflict-detection mechanism; and the single `OPT-6xxx` `CAPACITY` error-code class (a throttling/rate-limit error category, not a scaling catalog). **`architecture.md` §37's `NFR-008` row states only "Section 33 + 31" — unlike every prior document's primary requirement, it does not point to a dedicated numbered component list.**

Rather than force a catalog into existence to match the chain's stylistic pattern, this document introduces a new `SC-NNN` series only for the distinct, independently-scaled surfaces it itself must plan capacity for — one entry per upstream-owned mechanism this document layers throughput/trigger detail on top of, plus the two mechanisms (`SPC`, `XEC`) that have no other capacity-planning home. This breakdown is `SOURCE-DERIVED`: the *existence* of eight independently-scaled surfaces follows from the fact that eight prior documents each own a distinct mechanism/resource with its own capacity behavior, but the *specific eight-entry decomposition and every threshold under it* is this document's own synthesis, not an upstream-defined catalog. See §46 for the full table.

## 6. Scaling Dimensions

**`SOURCE-DERIVED`, from the request pipeline's own structure (`architecture.md` §8–§9, §18, §26):** requests/sec alone is not a sufficient capacity model for EAIOC, because a single incoming request can fan out into a highly variable amount of downstream work. Four dimension groups matter jointly:

- **Request dimensions:** requests/sec, sustained vs. burst request rate, concurrent requests.
- **LLM dimensions:** model calls/request, input/output/total tokens/request, input/output/total tokens/sec, context size, cached-token ratio.
- **Agent dimensions** (cite `agent-optimization.md`, not redefined): agents/request, iterations/request, sub-agent fan-out, tool calls/request, parallel tool calls, workflow depth.
- **Control-Plane dimensions:** optimization decisions/sec, policy/governance evaluations/sec, cache-control operations/sec, evaluation/benchmark jobs, telemetry events/sec.

A workload with low RPS but high per-request agent fan-out or context size can saturate the Control Plane while a high-RPS, low-fan-out workload does not — capacity planning must use the full dimension set, not RPS in isolation.

## 7. Capacity Model

**`PROPOSED METHODOLOGY`:** required capacity for a given surface scales as `request_rate × work_per_request × concurrency_factor × amplification_factor × unit_resource_cost`, where `work_per_request` and `amplification_factor` are themselves workload-dependent (§6, §10) rather than constants. This is a conceptual/heuristic relationship for capacity planning, exactly analogous in spirit to `architecture.md` §47.4.2's own framing of Net Optimization Economics as "a conceptual/heuristic accounting framework... not a literal per-request constraint-solver computation" — this document adopts the same discipline rather than presenting a false precision. Surfaces this model must be applied to independently: Control-Plane compute (SC-001), cross-execution coordination (SC-002), per-tenant governance-check throughput (SC-003), cache-layer operations (SC-004), provider/model concurrency (SC-005), agent/sub-agent fan-out (SC-006 — see §46 for the full catalog), evaluation/benchmark workers (SC-007), and telemetry ingestion (SC-008). Each surface's required capacity is computed independently — a global single-number capacity plan is insufficient given §6's finding.

## 8. Throughput Targets

**`PROPOSED METHODOLOGY`:** distinguish sustainable throughput (indefinitely maintainable without headroom erosion), peak throughput (maximum before `SPC`'s latency-budget or compute-budget exhaustion), burst throughput (short-duration excess absorbed by queueing, §30), degraded-mode throughput (available capacity once `SPC` has shed optimization depth, §13), and recovery throughput (post-incident ramp, §41).

`observability.md` names no existing queue-depth/throughput/RPS metric of its own — it explicitly defers "Capacity/throughput targets" to this document (verified: `docs/observability.md` line 44). This document therefore proposes the following candidate metric names for `observability.md` to implement (not implemented here — implementation is `observability.md`'s scope): `scaling.throughput.requests_per_sec`, `scaling.throughput.tokens_per_sec`, `scaling.queue_depth` (reusing `OverloadSignal.queue_depth`'s exact field name, `SOURCE-DEFINED` from `interfaces.md` §43.8), `scaling.latency_p99_ms` (reusing `OverloadSignal.latency_p99_ms`), `scaling.error_rate` (reusing `OverloadSignal.error_rate`).

Any concrete target number (e.g., a specific requests/sec figure) is `PROPOSED DEFAULT — VALIDATE LOCALLY` and is deliberately not stated here as a fixed figure, because no source provides a starting traffic-volume assumption to derive one from without inventing an organization-specific fact (root `CLAUDE.md` rule 5 — never fabricate a number that can't be backed). Where `SPC` defines a latency/processing-cost *budget* (`SOURCE-DEFINED`, per-decision, configurable), that budget is distinct from a scaling *target* (this document's aggregate throughput goal) and a scale-out *trigger* (§9) — the three must not be conflated.

## 9. Horizontal-Scaling Triggers

`SPC`'s overload-detection mechanism already names three signal types (`SOURCE-DEFINED`, `OverloadSignal`: `queue_depth`, `latency_p99_ms`, `error_rate`) but defines no concrete threshold at which any of them fires — this is exactly the gap this document exists to fill.

**`PROPOSED DEFAULT — VALIDATE LOCALLY`**, proposed starting thresholds (per Control-Plane instance/pool, to be locally recalibrated against real traffic): scale-out considered when `queue_depth` sustains above a configured ceiling for a configured window (e.g., illustratively, tens of pending requests sustained for tens of seconds — `ILLUSTRATIVE EXAMPLE`, not a number to hard-code); when `latency_p99_ms` exceeds the SPC latency budget for a sustained window rather than a single sample; or when `error_rate` exceeds a small fraction sustained over a window. These three signals are necessary inputs, not sufficient in isolation — a momentary spike must not trigger scale-out (thrashing risk, §41).

Four responses to the same three signal types must be kept distinct and never used interchangeably:

| Response | Trigger relationship to signals | Effect | Governed by |
|---|---|---|---|
| **Backpressure** | Sustained elevation, below hard overload | `SPC` steps optimization depth tier down (`SOURCE-DEFINED`) | `SPC` (cited) |
| **Load shedding** | Backpressure insufficient to recover | Optional Control-Plane stages disabled (§13) | This document, extending `SPC` |
| **Horizontal scale-out** | Sustained elevation exceeding a capacity ceiling regardless of depth-shedding | Additional Control-Plane capacity added | This document (new) |
| **Hard overload protection** | Scale-out not available/not fast enough | Admission control / request rejection with safe fallback | This document (new), never bypassing `SPC`'s `security_stages_preserved` invariant |

## 10. Fan-Out / Amplification

**`SOURCE-DERIVED`**, from the pipeline stages named across `architecture.md` §8–9, §18, §26 and `agent-optimization.md`: one incoming request can amplify into optimization decisions → LLM calls → agent iterations → sub-agent fan-out → tool calls → cache operations → telemetry events → evaluation/regression signals. Amplification sources: retries, provider fallback (cite `provider-matrix.md`), model cascades, cache misses, agent/sub-agent fan-out (cite `agent-optimization.md`), tool-call amplification, and shadow-evaluation amplification (cite `eval.md` `EL-001`).

A specific risk this document must name explicitly: an optimization can reduce inference cost while increasing Control-Plane overhead (additional classifier calls, additional cache-lookup fan-out, additional evaluation-shadow load) enough to erase its own net benefit — this is exactly `architecture.md` §47.4.2's "Overhead" and "Risk" categories, cited not redefined, but the *capacity* consequence (does the Control Plane have headroom to absorb that overhead at scale) is this document's concern.

## 11. Concurrency Model

Concurrency exists at ten levels, several with an already-owned correctness mechanism this document cites rather than redefines: request level (`SCN-CONC-003`, tenant-scoped independence — `SOURCE-DEFINED`), optimization-decision level, model-call level, agent level and sub-agent level (cite `agent-optimization.md`), tool-call level, evaluation level (cite `eval.md`), telemetry level (cite `observability.md`), cache level (cite `cache-strategy.md`), and tenant level (§16). Cross-cutting concerns at every level: global concurrency ceiling, tenant-scoped concurrency ceiling, fairness (§16), queueing (§9, §30), starvation avoidance, and resource exhaustion (§28).

## 12. Backpressure

This section builds the *operational* scaling layer beneath `SPC`'s already-defined mechanism (`architecture.md` §47.4.1; `interfaces.md` §43.8) — it does not redefine `SPC` itself. `PROPOSED METHODOLOGY` operational layer: bounded queues per Control-Plane stage class (never unbounded — ties to Invariant 5, §4), admission control at the queue boundary (reject/defer new work once a queue-depth ceiling is reached, rather than accepting unboundedly and degrading everyone), tenant-aware queue prioritization (§16), and retry suppression during backpressure (an already-throttled request retrying immediately worsens the condition it's reacting to).

**The critical invariant, restated exactly (`SOURCE-DEFINED`, `conventions.md` §7.9's `[!CAUTION]` block):** "SPC may shed any `pipeline/`, `context_engine/`, or `cache_economics/` stage under backpressure, but it must never cause a `governance/` module (SGE, DGE, TMG, HAG, CIS) to be skipped. If shedding would otherwise skip a mandatory security/authorization/PII check, the affected request fails **closed**, not open." Every backpressure mechanism this document proposes must preserve `DepthSheddingDecision.security_stages_preserved = true` (`SOURCE-DEFINED`, `interfaces.md` §43.8) without exception.

## 13. Load Shedding / Degraded Modes

**`PROPOSED METHODOLOGY`**, a tiered degradation ladder consistent with §12's invariant (not source-defined verbatim — synthesized to make `SPC`'s depth-tier concept operational):

| Tier | Trigger (§9 signals) | Allowed actions | Prohibited actions | Tenant behavior | Recovery |
|---|---|---|---|---|---|
| **NORMAL** | Signals within budget | Full `HIGH`/`MEDIUM`/`LOW` depth tiers available per `OI-003` policy | — | Normal allocation | — |
| **REDUCED OPTIMIZATION** | Sustained elevation | `OI-003` steps tier to `LOW` (`SOURCE-DEFINED` backpressure behavior) | Skipping a `governance/` module | Uniform, unless tenant-specific policy differs | Automatic once signals recover |
| **OPTIONAL-STAGE SHEDDING** | Backpressure insufficient | Shed optional `pipeline/`/`context_engine/`/`cache_economics/` stages (`shed_stages`, `SOURCE-DEFINED` field) | Skipping a `governance/` module; degrading correctness silently | Fairness-weighted shedding order (§16) preferred over uniform | Restore shed stages once signals recover |
| **HARD OVERLOAD PROTECTION** | Shedding insufficient, scale-out not yet effective | Admission control: defer/reject new work with the same deterministic safe fallback as `architecture.md` §30's failure model | Ever proceeding with a governance check silently skipped — this tier fails **closed** for any request whose governance evaluation cannot complete, per Invariant 2 | Per-tenant fairness in what gets rejected (§16), not first-come-first-served only | Resume admission once capacity recovers (§41) |

Every tier above `NORMAL` degrades optimization behavior, never governance/security behavior — this is the one property this table exists to make unambiguous.

## 14. Autoscaling

**`PROPOSED METHODOLOGY`:** horizontal scale-out driven jointly by queue-based, concurrency, latency-percentile, and error-rate signals (§9) — never CPU-utilization alone, because a Control-Plane instance can be CPU-idle while blocked on provider-call latency or saturated on token-throughput rather than compute, making CPU-only autoscaling systematically blind to EAIOC's actual bottleneck classes (§28). No exact CPU percentage, queue threshold, cooldown period, or replica count is asserted here as a production fact; any such number an implementer adopts is `PROPOSED DEFAULT — VALIDATE LOCALLY` territory, to be set from local capacity testing (§32), not from this document.

## 15. Capacity Headroom

**`PROPOSED METHODOLOGY`:** distinguish baseline capacity (steady-state minimum), normal capacity (typical operating point), peak capacity (maximum before `SPC` backpressure engages), burst capacity (short-duration excess absorbed by queueing before scale-out completes), reserved/safety headroom (capacity deliberately held back from normal allocation to absorb an unanticipated spike before hard overload protection engages), and degraded capacity (what remains after load-shedding, §13). No fixed headroom percentage is proposed as a production fact; a specific percentage an implementer adopts is `PROPOSED DEFAULT — VALIDATE LOCALLY`.

## 16. Multi-Tenant Scaling, Fairness, and Noisy-Neighbor Protection

**`SOURCE-DERIVED`** from Invariant 1 (§4) and `conventions.md` §13.6's explicit statement that "§13.2 tenant isolation applies to budget scoping too": the same principle extends to capacity/rate scoping. One tenant reaching its own quota is a tenant-specific overload, not evidence of global system saturation, and must not be treated as one. `PROPOSED METHODOLOGY` mechanisms: per-tenant rate/concurrency quotas (distinct from and never a substitute for `security.md`'s SGE spend quotas — a tenant can be `WITHIN_BUDGET` and still be at its capacity quota, or vice versa), weighted-fair allocation of shared queue/worker capacity across tenants, tenant-aware queue isolation (a saturated tenant's queue must not starve another tenant's queue), and tenant-aware degradation ordering in §13's shedding tiers. `SCN-CONC-003` (`SOURCE-DEFINED`) already establishes that two simultaneous same-tenant requests keep fully independent execution state except for intentional tenant-scoped cache sharing — this document's fairness mechanisms extend that same isolation discipline across tenants under load, rather than introducing a new isolation model.

**Honest coverage gap:** no scenario in `scenario-matrix.md` (all 30 domains checked, see §43) exercises multi-tenant fairness or noisy-neighbor behavior specifically — Domain Z (Multi-Tenancy) validates data/retrieval isolation (`SCN-TEN-001` and siblings), not capacity fairness under concurrent load. Recorded as `SOURCE-GAP-SCALING-04` (§48).

## 17. Token-Based Capacity

**`SOURCE-DERIVED`:** requests/sec is insufficient; capacity must also be expressed in input/output/total tokens/sec, tokens/request, context size, and cached-token ratio, because two requests with identical RPS but very different token volumes impose very different provider-concurrency and Control-Plane compute loads. Provider-specific token/rate limits are consumed from `provider-matrix.md` (cited, never invented here) — this document defines how those limits participate in capacity planning (§18), not what the limits themselves are.

## 18. Model / Provider Capacity

Cite `provider-matrix.md` for all provider/model rate limits, token limits, and outage/fallback behavior — not redefined here. This document's own contribution: provider/model concurrency is one of the eight independently-scaled surfaces (`SC-005`, §46); a Control-Plane scale-out that increases request concurrency without regard to a provider's own rate limit merely shifts the bottleneck downstream to `EC-064`-class repeated rate-limit errors (`PARTIALLY COVERED` by `SCN-PROV-002` per `scenario-matrix.md`'s own traceability table — cited, not re-litigated here) rather than resolving it; capacity planning for `SC-005` must size Control-Plane-side concurrency against the provider's own advertised limits, not independently of them.

## 19. Cache Scaling

Cite `cache-strategy.md` for all cache mechanics — not redefined here. This document's own contribution (`SC-004`, §46): cache hit/miss *volume* at scale, cache-lookup load, storage growth, and cache-stampede risk (many concurrent misses against the same key simultaneously triggering redundant recomputation) are capacity concerns distinct from cache *hit rate* (a quality/efficiency concern owned by `cache-strategy.md`). A high hit rate does not imply low operation volume — a cache serving a very high request rate can have both a high hit rate and a capacity-relevant lookup-operation volume. Provider-native cache savings remain a separate attribution from EAIOC's own application cache, exactly as `cache-strategy.md` and `inference-optimization.md` already establish (cited).

## 20. Agent / Multi-Agent Scale

Cite `agent-optimization.md` for agent-loop/sub-agent mechanics — not redefined here. This document's own contribution (`SC-006`, §46): agent concurrency, sub-agent fan-out, and long-running/runaway workflows are a capacity surface with its own amplification risk (§10) distinct from the agent-loop *economics* `agent-optimization.md` already owns. A reduction in iteration/tool-call count is not automatically a capacity win if task success deteriorates — that tradeoff belongs to `quality-gates.md` (cited), not this document, but this document must not silently assume fewer iterations means lower Control-Plane load without checking whether early exit or escalation shifted load elsewhere (e.g., to a more expensive model call).

## 21. P5 / Inference-Serving Scale

Cite `inference-optimization.md` for all P5/inference-serving mechanics — not redefined here, and its own measurement-separation requirement (H17/H18) is preserved: Layer 1/2 optimization capacity and P5 inference-serving capacity remain separately attributable even where a combined capacity report exists. This document does not merge the two.

## 22. Cross-Execution Coordination at Scale

`XEC`'s own mechanism (`architecture.md` §47.3.2; `interfaces.md` §43.7, `INTF-069`) is cited, not redefined. This document's own contribution (`SC-002`, §46): as concurrent execution volume grows, `check_conflict()`/`acquire_lock()` call volume, reconciliation frequency, and lock-contention rate all grow with it — this is a capacity surface distinct from XEC's correctness guarantee. `EC-191` (second write silently overwriting a dependency), `EC-192` (non-idempotent action dispatched without an acquired lock), `EC-193` (two executions' completed actions overlap post-hoc), and `EC-194` (XEC's own backing coordination store unavailable) are all verified directly (`edge-cases.md` §43-hardening block) and all describe *correctness* failure modes, not throughput limits — none of the four specifies a concurrency volume at which contention becomes a capacity problem. This document does not claim XEC's correctness mechanisms constitute a throughput/scaling target; that gap is recorded as `SOURCE-GAP-SCALING-02` (§48). `EC-194` specifically implies a capacity requirement this document must state explicitly: XEC's own backing coordination store must itself be sized to remain available under peak concurrent-conflict-check volume, since its unavailability blocks the entire non-idempotent-concurrently-reachable action subset (fail-closed on coordination unavailability, `SOURCE-DEFINED`).

## 23. Self-Protection Controller Boundary

`SPC`'s own mechanism (`architecture.md` §47.4.1; `interfaces.md` §43.8, `INTF-070`) is cited, not redefined; §9, §12, and §13 above are this document's operational layer on top of it. Kept strictly separate: `SPC` mechanism (what the Control Plane does to protect itself once overload is detected) vs. scaling decision methodology (when and how much additional capacity to provision, §7–§9, §14) — the former is a within-instance response, the latter is a capacity-provisioning decision, and conflating them would incorrectly suggest that depth-shedding alone is a substitute for actually scaling out.

## 24. Spend Governance Boundary

`security.md`'s SGE mechanism (budget calculation, throttle, halt) is cited, not redefined. Capacity logic in this document must never imply authorization: `WITHIN_BUDGET` does not mean "authorized to execute" (`SOURCE-DEFINED`, `interfaces.md`'s own `SpendStatus` semantics, cited), and available Control-Plane capacity never overrides a governance/security control (Invariant 2, §4). A tenant that is `WITHIN_BUDGET` can still be capacity-throttled by this document's own per-tenant quotas (§16), and a tenant at its capacity quota can still be `WITHIN_BUDGET` — the two systems are independent and their outputs must not be merged into a single status.

## 25. Per-Deployment-Mode Scaling Profiles

`architecture.md` §33's six modes (`SOURCE-DEFINED`, verified directly):

| Mode | Description (verbatim) | Scaling profile (`SOURCE-DERIVED`) |
|---|---|---|
| **A** | Python SDK/library | In-process with the calling application; scales with the host application's own scaling, not independently horizontally scaled by EAIOC itself |
| **B** | CLI | Single invocation, ephemeral process; no sustained concurrency to plan for beyond one invocation's own resource use |
| **C** | API/middleware service | The primary target for this document's horizontal-scale-out/backpressure/load-shedding methodology (§9, §12–14) — sustained multi-tenant concurrent load |
| **D** | Drop-in integration at prompt assembly | Scales with the host application's request volume, similar to Mode A but as a library seam rather than an SDK call |
| **E** | Agent-orchestration middleware | Sustained load plus the agent/sub-agent fan-out amplification of §10/§20 — capacity planning must include fan-out, not just request count |
| **F** | CI/CD or GitHub Action for benchmark/regression testing | Burst-not-sustained: capacity spikes around CI trigger events, then idles; provisioning for this mode should favor fast burst absorption (§30) over sustained headroom (§15) |

No single scaling strategy applies uniformly across all six modes — this table exists specifically to prevent that conflation.

## 26. Evaluation / Benchmark Scaling

Cite `eval.md` for all benchmarking/statistical/A-B methodology — not redefined here. **Critical invariant** (`SOURCE-DERIVED` from `eval.md`'s own tenant-isolation and contamination discipline, cited): evaluation workload (shadow evaluation, A/B experiments, regression runs, benchmark-corpus refresh) must not consume enough shared production capacity to distort the very workload it is measuring. This document's contribution (`SC-007`, §46): production capacity, evaluation capacity, and benchmark-worker capacity are separately provisioned pools, not a shared undifferentiated pool — an evaluation surge must not itself trigger this document's own §9 scale-out/backpressure signals against production traffic.

## 27. Observability at Scale

Cite `observability.md` for telemetry/dashboard implementation — not redefined here. This document's contribution (`SC-008`, §46): metric cardinality (tenant × application × workflow × model × provider dimensions, per `observability.md` §31.2's required-dimension list, cited), trace/event/log volume, and dashboard query load all grow with request volume and must themselves be capacity-planned — a Control Plane that scales its request-handling capacity without also scaling its telemetry-ingestion capacity produces silent observability gaps exactly when visibility matters most (during an overload event, §13).

## 28. Resource Management and Bottleneck Identification

Resource classes relevant to capacity planning: Control-Plane compute, Control-Plane memory, queue capacity, cache-layer capacity, provider/model concurrency, evaluation-worker capacity, and telemetry-ingestion capacity — corresponding to `SC-001` through `SC-008` (§46). **`PROPOSED METHODOLOGY`:** identifying which resource is the limiting one for a given workload requires correlating §9's three signals with per-surface utilization (e.g., high `queue_depth` with high provider-call latency implicates `SC-005`, not `SC-001`); no single signal identifies the bottleneck in isolation, and no specific bottleneck is asserted to exist for EAIOC without local evidence.

## 29. Latency Under Scale

Canonical latency percentiles (P50/P95/P99) and their component breakdown (queue wait, optimization overhead, model latency, tool latency, cache latency, Control-Plane latency) are cited from `observability.md`'s already-established latency-metric concept, not redefined here. No latency SLO is invented in this document. Where `SPC` defines a per-decision latency *budget* (`SOURCE-DEFINED`), that budget bounds an individual decision's added latency; it is distinct from an end-to-end request SLO, which remains `observability.md`'s/`quality-gates.md`'s domain to define (cited).

## 30. Burst Handling

**`PROPOSED METHODOLOGY`** conceptual sequence (not source-defined verbatim): detect → absorb (via bounded queueing, §12) → scale (§9, §14) → degrade (§13) → shed (§13's hard-overload tier) → recover (§41). Illustrative burst magnitudes (e.g., a sudden multiple of normal traffic, a single tenant's traffic spike, a cache-invalidation storm) are `ILLUSTRATIVE EXAMPLE` only, not requirements — the sequence above applies regardless of the specific magnitude that triggers it.

## 31. Failure / Overload Matrix

| Condition | Detection | Scaling Impact | Safe Response | Evidence/Ownership | Recovery |
|---|---|---|---|---|---|
| Global traffic surge | `queue_depth`/`latency_p99_ms`/`error_rate` (§9, `SOURCE-DEFINED` signals) | All surfaces under pressure | Backpressure → scale-out → shedding (§12–14) | This document | §41 |
| Tenant traffic surge | Same signals, tenant-scoped | Single tenant's quota (§16) | Tenant-scoped throttling, not global shedding | This document | Automatic once tenant load recedes |
| Provider throttling/outage | Provider error class (`OPT-6xxx CAPACITY`, `SOURCE-DEFINED`) | `SC-005` bottleneck | Provider fallback/cascade (cite `provider-matrix.md`) | `provider-matrix.md` (cited) | Per provider's own recovery |
| Queue saturation | `queue_depth` | `SC-001` | Admission control (§13 hard-overload tier) | This document | Drain once signals recover |
| CPU/memory pressure | Infra-level monitoring (out of this document's scope to define) | `SC-001` | Standard infra autoscaling (§14) | `implementation-plan` (future) | Standard |
| Token-rate saturation | `SC-005`/`SC-001` combined | Provider concurrency ceiling | Same as provider throttling | This document + `provider-matrix.md` | Per provider |
| Agent/sub-agent fan-out explosion | §10 amplification | `SC-006` | Bounded fan-out ceiling (Invariant 5) — cite `agent-optimization.md`'s own bounds if any exist | `agent-optimization.md` (cited) + this document | Per-request cancellation/timeout |
| Cache stampede | Concurrent misses against one key | `SC-004` | Cite `cache-strategy.md`'s own stampede protection if defined; else recorded gap | `cache-strategy.md` (cited) | Per cache mechanism |
| Evaluation overload | `SC-007` isolation breach | Production capacity distortion risk (§26) | Separate evaluation capacity pool | This document | Throttle evaluation workers first |
| Telemetry overload | `SC-008` | Observability gap risk | Cite `observability.md`'s own degradation behavior if defined; else recorded gap | `observability.md` (cited) | Per telemetry mechanism |
| Noisy neighbor | Tenant-scoped signal divergence | Fairness violation (§16) | Weighted-fair allocation | This document | Automatic |
| XEC coordination-store outage | `EC-194` (`SOURCE-DEFINED`) | `SC-002` | Block non-idempotent concurrently-reachable actions (`SOURCE-DEFINED`) | `architecture.md` §47.3.2 (cited) | Per XEC's own recovery |
| Governance-check bottleneck | Elevated governance-evaluation latency | Cannot be shed (Invariant 3) | Governance checks are never shed; only optimization stages are — the request may queue, but never bypass | This document, preserving `security.md`'s precedence | Scale governance-check capacity, never skip it |

## 32. Capacity Testing Methodology

**`PROPOSED METHODOLOGY`**, consuming `eval.md`'s benchmarking methodology (cited, not reimplemented) for the statistical rigor applied to capacity-test results: load testing, stress testing, spike testing, soak/endurance testing, concurrency testing, multi-tenant fairness testing, provider-throttling testing, degradation testing, recovery testing, scale-in testing. Scale-test dimensions mirror §6: RPS, concurrency, tokens/request, context size, agent iterations, tool calls, sub-agent count, tenant count and skew, model/provider mix, cache-hit ratio — a single synthetic workload is insufficient given §6's finding that no single dimension characterizes load. Illustrative phase sequence (`PROPOSED METHODOLOGY`): baseline measurement → controlled load increase → saturation/bottleneck identification (§28) → scale-out validation → burst validation (§30) → degraded-mode validation (§13) → recovery validation (§41) → multi-tenant fairness validation (§16) → regression comparison (cite `eval.md` `EL-005`). **No claim is made that any of this testing has actually been executed** — this repository is pre-implementation.

## 33. Capacity SLI/SLO Boundary

This document does not invent SLOs. It distinguishes: SLI (a measured signal, e.g. `queue_depth` — cite `observability.md` for implementation), SLO (a target for that signal — owned by whichever document the underlying quality/latency/reliability concern belongs to), capacity target (§8, this document), scaling trigger (§9, this document), and hard safety limit (§13's hard-overload tier, this document). Quality, latency, reliability, cost, and security SLOs remain owned by `quality-gates.md`, `observability.md`, and `security.md` respectively (cited, not redefined).

## 34. Cost of Scaling

Scaling has cost: additional Control-Plane compute, additional worker capacity (`SC-007`, `SC-008`), additional model/provider calls attributable to retries or cascades under load, and idle reserved headroom (§15). This document connects to but does not duplicate `eval.md`'s canonical cost-accounting fields (`gross_savings`, `optimizer_overhead`, `net_savings`, `net_savings_pct`, cited) and `architecture.md` §47.4.2's Net Optimization Economics categories (cited): capacity/scaling cost is itself part of the "Overhead" term in that accounting, not a separate competing cost model. This document never creates a second cost-accounting framework alongside the one that already exists.

## 35. Scaling + Quality

Overload/degradation (§13) can affect quality, task success, context handling, tool usage, model choice, agent iteration, early exit, and cache behavior — but any such quality effect remains subject to `quality-gates.md`'s gates (cited, not redefined). Scaling pressure must never silently convert a mandatory quality gate into optional behavior; §13's degradation ladder sheds *optimization* stages, never quality-gate evaluation itself, which is a `governance`-adjacent, non-shedable concern in the same sense as security checks (Invariant 3).

## 36. Scaling + Security

Cite `security.md`. Resource exhaustion, tenant isolation, admission control, and abuse resistance under load must never let optimization-level scaling bypass authorization (Invariant 2, restated). §38 operationalizes the specific adversarial case (`EC-077`).

## 37. Scaling + Governance

Autoscaling cannot authorize workloads; available capacity cannot override governance; a learned-policy change from `eval.md`'s `EL-004` remains governed by `security.md`'s HAG regardless of capacity pressure; emergency degradation (§13's hard-overload tier) cannot skip governance; and tenant-specific capacity policies (§16) remain within the security boundaries `security.md` already establishes, never superseding them.

## 38. Adversarial / Overload Defense

`EC-077` (Adversarial Input Designed to Trigger Maximum Optimization Cost), verified directly against `edge-cases.md`: an adversarial request maximizing ambiguous intent, large context, and near-miss cache similarity can make the Control Plane spend far more on optimization than it saves — a DoS-amplification risk. `edge-cases.md`'s own expected behavior (`SOURCE-DEFINED`) is that `OI-002` enforces a maximum optimization-overhead budget per request, stopping additional optimization stages once exceeded, with tenant-level rate limiting on optimization overhead. `security.md` owns the authorization dimension of adversarial abuse; **this document owns the capacity-impact and operational response**: the same overhead-budget-cap and tenant-rate-limiting mechanisms named by `EC-077` are, from this document's perspective, a specific instance of §16's per-tenant fairness/quota mechanism and §13's shedding ladder — an adversarially expensive request should be shed at the "optional-stage" tier (§13) rather than being allowed to consume enough Control-Plane capacity to become a capacity incident of its own.

## 39. Control-Plane Scaling Algorithms

**A — Capacity Estimation.** *Purpose:* translate an expected workload description (§6's dimensions) into a required-capacity figure per surface (§46). *Inputs:* workload profile, per-surface unit-resource-cost estimates (§7). *Decision logic:* apply §7's capacity-model relationship per surface independently. *Outputs:* per-surface capacity requirement. *Failure behavior:* insufficient input data → flag as `UNVERIFIED`, do not guess (root `CLAUDE.md` rule 5). *Classification:* `PROPOSED METHODOLOGY`.

**B — Workload Classification.** *Purpose:* classify an incoming workload against §6's dimension groups to select the right capacity model. *Inputs:* observed or declared workload characteristics. *Outputs:* workload class (e.g., high-fan-out agent workload vs. high-RPS simple-request workload). *Failure behavior:* unclassifiable workload defaults to the most conservative (highest-headroom) capacity assumption. *Classification:* `PROPOSED METHODOLOGY`.

**C — Bottleneck Detection.** *Purpose:* identify which of `SC-001`–`008` is the limiting surface (§28). *Inputs:* per-surface utilization plus §9's three signals. *Outputs:* identified bottleneck surface, or `INDETERMINATE`. *Failure behavior:* never assert a specific bottleneck without correlating evidence. *Classification:* `PROPOSED METHODOLOGY`.

**D — Admission Control.** *Purpose:* decide whether to admit, defer, or reject new work at the queue boundary (§12). *Inputs:* current queue depth vs. ceiling, tenant quota state (§16). *Outputs:* ADMIT / DEFER / REJECT. *Failure behavior:* on failure to evaluate, default to the same deterministic safe fallback as `architecture.md` §30 — never silently admit unboundedly. *Classification:* `PROPOSED METHODOLOGY`.

**E — Concurrency Allocation.** *Purpose:* allocate available concurrency across concurrently-competing requests. *Inputs:* current global and per-tenant concurrency, fairness weights (§16). *Outputs:* per-request concurrency slot or queue placement. *Failure behavior:* fail toward fairness, not toward first-come-first-served starvation. *Classification:* `PROPOSED METHODOLOGY`.

**F — Tenant Capacity Allocation.** *Purpose:* enforce §16's per-tenant quota independent of global SGE budget (§24). *Inputs:* tenant quota configuration, tenant's current usage. *Outputs:* within-quota / at-quota (tenant-scoped throttle, not global). *Failure behavior:* quota-evaluation failure defaults to the tenant's own conservative policy-configured default, mirroring `security.md`'s SGE conservative-default pattern (cited) rather than defaulting to unconstrained. *Classification:* `PROPOSED METHODOLOGY`.

**G — Backpressure Decision.** *Purpose:* decide whether to step the optimization depth tier down (§12). *Inputs:* `OverloadSignal` (`SOURCE-DEFINED`). *Outputs:* `DepthSheddingDecision` (`SOURCE-DEFINED` — this algorithm consumes, not redefines, `SPC`'s own interface). *Failure behavior:* `SPC`'s own failure behavior applies (fail-open unless it would skip governance). *Classification:* consumes `SOURCE-DEFINED` interface; the decision to invoke it at a given signal level is `PROPOSED METHODOLOGY`.

**H — Load-Shedding Decision.** *Purpose:* select §13's degradation tier. *Inputs:* backpressure state, elapsed time at current tier. *Outputs:* tier selection (never a governance-module shed). *Failure behavior:* ambiguous state defaults to the more conservative (more-shed, never less-secure) tier. *Classification:* `PROPOSED METHODOLOGY`.

**I — Horizontal Scale-Out Decision.** *Purpose:* decide whether to provision additional Control-Plane capacity (§9). *Inputs:* sustained signal elevation beyond backpressure's own recovery, per-surface bottleneck (Algorithm C). *Outputs:* scale-out request for the identified surface. *Failure behavior:* if scale-out cannot complete in time, fall through to §13's hard-overload tier rather than silently accepting unbounded queueing. *Classification:* `PROPOSED METHODOLOGY`.

**J — Burst Handling.** *Purpose:* execute §30's detect→absorb→scale→degrade→shed→recover sequence. *Inputs:* burst-onset signal. *Outputs:* sequenced response. *Failure behavior:* any stage failing falls through to the next more-conservative stage. *Classification:* `PROPOSED METHODOLOGY`.

**K — Scale-In / Recovery Decision.** *Purpose:* decide when to safely reduce provisioned capacity after a scale-out (§41). *Inputs:* sustained signal recovery below the scale-out trigger, with hysteresis to avoid thrashing. *Outputs:* scale-in authorization. *Failure behavior:* ambiguous recovery state defers scale-in (conservative) rather than prematurely reducing capacity. *Classification:* `PROPOSED METHODOLOGY`.

**L — Capacity Validation.** *Purpose:* confirm via testing (§32) that a proposed capacity plan actually holds under load before treating it as validated. *Inputs:* capacity-test results. *Outputs:* validated / not-yet-validated, `UNVERIFIED` if evidence is incomplete. *Failure behavior:* absent evidence, a capacity plan remains `PROPOSED DEFAULT — VALIDATE LOCALLY`, never promoted to validated by assertion alone. *Classification:* `PROPOSED METHODOLOGY`.

## 40. Scale State Model

**`PROPOSED METHODOLOGY`** (not source-defined verbatim): `NORMAL` → `ELEVATED` → `HIGH_LOAD` → `SATURATED` → `DEGRADED` → `SHED` → `RECOVERY` → `NORMAL`. Entry to each state is governed by §9's signals; exit requires sustained recovery, not a single good sample (thrashing avoidance, §41). Allowed/prohibited actions at each state mirror §13's tiers exactly — this state model is the sequencing view of that same table, not a separate mechanism.

## 41. Scale-In / Recovery

**`PROPOSED METHODOLOGY`:** scale-in requires sustained signal recovery with hysteresis (do not scale in on the first good sample after a scale-out, to avoid oscillation/thrashing); draining in-flight requests before removing capacity; agent/workflow cancellation and timeout handling for work that was in progress during the overload (cite `agent-optimization.md`'s and `architecture.md` §46's own resume/checkpoint mechanisms rather than redefining them); provider-side and cache-side recovery (cite respective owning documents); and workload redistribution across recovered capacity. No exact cooldown period is asserted as a production fact.

## 42. Resilience Principles

Scaling mechanisms in this document must preserve, without exception: tenant isolation (§4.1, §16); fail-open optimization / fail-closed security (§4.2–3); fallback availability (§13's hard-overload tier always has a deterministic safe fallback); quality protection (§35); observability (§27); evaluation integrity (§26); attribution integrity (§21, §34); governance precedence (§37); and bounded resource consumption (§4.5, §10).

## 43. Scenario Coverage

All 30 scenario-matrix.md domains (A through AD) were enumerated directly by header. Domain AA (Concurrency, `SCN-CONC-001`–`006`) is the only domain whose name bears directly on this document's subject matter; Domain Z (Multi-Tenancy) and Domains S/T (Cost/Latency) were spot-checked for burst, fairness, throughput, or rate-limit content and found to contain none beyond `SCN-PROV-002`'s tangential relationship to `EC-064` (repeated provider rate-limit errors, a policy-routing scenario, not a capacity one).

| Scenario | Verified content | Actually validates |
|---|---|---|
| `SCN-CONC-001` | Concurrent context mutations from two sub-agents serialize correctly via CVM | Concurrency **correctness** (no lost update) — not throughput or scale-out |
| `SCN-CONC-003` | Two same-tenant simultaneous requests keep independent state except intentional cache sharing | Tenant-scoped isolation under concurrency — not throughput or scale-out |
| `SCN-CONC-004`/`005`/`006` | Shared-resource conflict, non-idempotent lock requirement, XEC coordination-store outage | XEC correctness under concurrency — not throughput or scale-out |

**Honest finding, stated per the prompt's own requirement not to claim more coverage than exists:** no scenario in this scenario matrix validates throughput targets or horizontal-scale-out trigger thresholds. Domain AA validates concurrency *correctness*; it does not and was never intended to validate *capacity*. This is recorded as `SOURCE-GAP-SCALING-01` (§48), not silently glossed over.

## 44. Edge-Case Coverage

| Edge Case | Scaling Relevance | Coverage | Evidence |
|---|---|---|---|
| `EC-077` | Adversarial optimization-cost maximization — direct capacity/DoS relevance | DIRECTLY COVERED | §38, verified directly against `edge-cases.md` |
| `EC-191` | Concurrent shared-resource write conflict (XEC) | NOT RELEVANT to throughput/capacity — correctness only | §22, verified directly |
| `EC-192` | Non-idempotent action without lock (XEC) | NOT RELEVANT to throughput/capacity — correctness only | §22, verified directly |
| `EC-193` | Two completed actions overlap (XEC) | NOT RELEVANT to throughput/capacity — correctness only | §22, verified directly |
| `EC-194` | XEC coordination-store unavailable | PARTIALLY COVERED — implies a capacity requirement on XEC's own backing store (§22), though the edge case itself is a correctness/availability case, not a throughput one | §22, verified directly |
| `EC-064` | Provider returns rate-limit error repeatedly | PARTIALLY COVERED via `SC-005` (§18) — tangential, `scenario-matrix.md`'s own traceability marks its scenario coverage `PARTIALLY COVERED` for an unrelated reason (policy-routing, not rate-limit repetition itself) | §18, cited from `scenario-matrix.md`'s existing table, not re-verified line-by-line here |

No edge case in `edge-cases.md` was found describing a burst-traffic, horizontal-scale-out, or multi-tenant-fairness scenario specifically — this absence is folded into `SOURCE-GAP-SCALING-01`/`04` (§48) rather than treated as a separate gap.

## 45. Requirement / Traceability Matrix

| ID | Statement (verified) | Traces to |
|---|---|---|
| `NFR-008` | Scalability — the framework must support high-volume enterprise traffic | `architecture.md` §37; row states "Section 33 + 31" only, no dedicated catalog |
| `NFR-014` | Control-Plane Self-Protection — bound own latency/cost, backpressure, graceful degradation | `architecture.md` §37, §47.4.1 |
| `OBJ-025` | Bound the Control Plane's own latency/cost/resource consumption with backpressure, overload protection, deterministic safe fallback | `architecture.md` §47.4 SPC |
| `OBJ-032` | Cross-execution concurrency | `architecture.md` §47.3 XEC |
| `AC-041` | Framework must enforce its own latency/cost budgets and degrade to a deterministic safe fallback under overload without degrading application correctness or security | SPC |
| `AC-049` | Concurrent mutation/stale-decision/version-conflict detection across agents/sub-agents/executions | XEC |
| `SCN-CONC-001`/`003` | Concurrency correctness, tenant isolation under simultaneous requests | Domain AA — see §43's honest-coverage note |
| `INTF-069` | `CrossExecutionCoordinator` | `interfaces.md` §43.7 |
| `INTF-070` | `SelfProtectionController` | `interfaces.md` §43.8 |
| `EC-077`/`191`/`192`/`193`/`194` | See §44 | `edge-cases.md` |

## 46. Scaling-Dimension Catalog

Per §5's decision: eight independently-scaled surfaces, `SOURCE-DERIVED`, not an upstream-defined canonical catalog.

| ID | Scaling Surface | Capacity Dimension | Cited Owning Mechanism/Document |
|---|---|---|---|
| `SC-001` | Control-Plane compute & self-protection | Latency/compute budget, overload detection, depth shedding | `SPC`, `architecture.md` §47.4.1 |
| `SC-002` | Cross-execution / concurrency coordination | Conflict-check volume, reconciliation frequency, lock contention | `XEC`, `architecture.md` §47.3.2 |
| `SC-003` | Per-tenant spend/rate governance | Budget throttle/halt, tenant quota | `SGE`, `security.md` GSP-001 |
| `SC-004` | Cache-layer capacity | Lookup volume, storage growth, stampede risk | `cache-strategy.md` |
| `SC-005` | Provider/model concurrency | Rate/token limits, throttling, fallback | `provider-matrix.md` |
| `SC-006` | Agent/sub-agent fan-out | Iteration/fan-out amplification | `agent-optimization.md` |
| `SC-007` | Evaluation/benchmark-pipeline capacity | Shadow/A-B/regression worker load, production-isolation | `eval.md` |
| `SC-008` | Observability/telemetry ingestion | Metric cardinality, trace/event/log volume | `observability.md` |

## 47. Cross-Document Boundary Matrix

| Document | What `SCALING.md` consumes | What it owns here | What it never redefines |
|---|---|---|---|
| `architecture.md` | §33, §37, §47.3.2, §47.4.1, §47.4.2, §31 | Operational capacity/throughput/trigger layer | `SPC`/`XEC` mechanisms, Net Optimization Economics |
| `interfaces.md` | `DepthSheddingDecision`, `OverloadSignal`, `CrossExecutionCoordinator`, `OPT-6xxx` | Concrete trigger thresholds on top of named signal types | Schema/type definitions |
| `conventions.md` | §7.9 SPC, §13.6 SGE, precedence list | — | Package-layout/precedence rules themselves |
| `edge-cases.md` | `EC-077`, `EC-191`–`194` | Capacity-impact framing of adversarial/concurrency cases | The edge cases' own correctness requirements |
| `scenario-matrix.md` | Domain AA, spot-checked Z/S/T | Honest coverage-gap disclosure | Scenario definitions |
| `security.md` | GSP-001/SGE | Capacity-vs-authorization independence | SGE mechanism |
| `observability.md` | Confirmed no throughput/queue-depth metric exists yet | Proposed metric names for it to implement | Telemetry/dashboard implementation |
| `eval.md` | Benchmarking/statistical methodology, canonical cost fields | Capacity-testing methodology, evaluation-capacity isolation | Statistical methodology itself |
| `cache-strategy.md` | Cache mechanics | Cache-volume capacity framing | Cache mechanics themselves |
| `agent-optimization.md` | Agent-loop/sub-agent mechanics | Fan-out capacity framing | Agent-loop mechanics themselves |
| `inference-optimization.md` | P5 measurement-separation requirement | Preserves the separation at scale | P5 mechanics themselves |
| `provider-matrix.md` | Provider/model facts and limits | How limits participate in capacity planning | The facts/limits themselves |
| `optimization-catalog.md` | Cross-document boundary table's own `SCALING.md` scope line | Fulfillment of that named scope | Technique definitions |

## 48. Source Gaps

`SOURCE-GAP-SCALING-01` — **No scenario validates throughput or horizontal-scale-out triggers.** *Source:* `scenario-matrix.md`, all 30 domains checked. *Missing definition:* a scenario exercising sustained load against a concrete throughput/scale-out threshold. *Why it matters:* Domain AA's concurrency-correctness coverage cannot be mistaken for capacity validation. *Proposed treatment:* recorded; not force-fit to an existing scenario. *Blocking:* No — non-blocking; this document's methodology is usable without it, but the methodology's specific proposed thresholds remain unvalidated by any scenario.

`SOURCE-GAP-SCALING-02` — **XEC's correctness edge cases don't specify a contention-volume capacity threshold.** *Source:* `EC-191`–`194`. *Missing definition:* the concurrent-conflict-check volume at which XEC's backing coordination store itself becomes a bottleneck. *Why it matters:* affects `SC-002` sizing. *Proposed treatment:* recorded as an operational sizing question for local load testing (§32). *Blocking:* No.

`SOURCE-GAP-SCALING-03` — **No source-defined concrete numeric throughput target, scale-out threshold, headroom percentage, cooldown period, or replica count exists anywhere upstream.** *Source:* full-text search of `architecture.md`/`interfaces.md`/`conventions.md`, confirmed empty of such numbers. *Why it matters:* every concrete number in this document is necessarily `PROPOSED DEFAULT — VALIDATE LOCALLY`, not sourced. *Proposed treatment:* every such number tagged accordingly throughout (§8, §9, §14, §15). *Blocking:* No — by design, this is the gap this document exists to hold open honestly rather than close by invention.

`SOURCE-GAP-SCALING-04` — **No scenario or edge case exercises multi-tenant capacity fairness/noisy-neighbor behavior.** *Source:* `scenario-matrix.md` Domain Z (data/retrieval isolation only) and full edge-case search. *Why it matters:* §16's fairness mechanisms are `PROPOSED METHODOLOGY` with no validating scenario. *Proposed treatment:* recorded; not force-fit. *Blocking:* No.

`SOURCE-GAP-SCALING-05` — **No canonical scaling/capacity catalog exists upstream** (see §5). *Source:* full-text search across `architecture.md`/`interfaces.md`/`conventions.md`. *Why it matters:* this document's own `SC-NNN` series (§46) is `SOURCE-DERIVED`, not upstream-defined, and a future architecture amendment could define a competing canonical catalog that would need reconciliation. *Proposed treatment:* flagged for architecture-owner awareness; not treated as blocking. *Blocking:* No.

## 49. Source Contradictions

None found. `Source Contradictions: 0`. One near-miss was checked and is not a genuine contradiction: `architecture.md` §33's six deployment modes and this document's eight `SC-NNN` scaling surfaces (§46) use different, non-overlapping decomposition axes (deployment packaging vs. independently-scaled subsystem) — they are not meant to align 1:1, and neither document claims they should.

## Final Report

- **Document ID:** `EAIOC-SCALING-001`, Version 1.0.0, Status PRE-IMPLEMENTATION — Level 0 (RESEARCH).
- **Major scaling domains covered:** capacity model, throughput targets, horizontal-scaling triggers, backpressure, load shedding, autoscaling, headroom, multi-tenant fairness, token-based capacity, provider/cache/agent/P5/evaluation/observability capacity, per-deployment-mode profiles, burst handling, failure/overload matrix, capacity testing methodology, cost of scaling, scaling+quality/security/governance, adversarial defense.
- **Scaling algorithms:** 12 (A–L).
- **Scaling catalog:** 8 `SC-NNN` entries (§46), `SOURCE-DERIVED`, explicitly not an upstream-defined canonical catalog (§5, `SOURCE-GAP-SCALING-05`).
- **Scenarios verified:** `SCN-CONC-001/003/004/005/006` (Domain AA, full domain), plus a spot-check of Domains Z/S/T finding no additional relevant content.
- **Edge cases verified:** `EC-077`, `EC-191`, `EC-192`, `EC-193`, `EC-194`, `EC-064` (cited from existing traceability, not re-derived).
- **Traceability coverage:** `NFR-008`, `NFR-014`, `OBJ-025`, `OBJ-032`, `AC-041`, `AC-049`, `INTF-069`, `INTF-070` all independently verified against live source text.
- **Source gaps:** 5 (`SOURCE-GAP-SCALING-01`–`05`), all non-blocking.
- **Contradictions:** 0.
- **Proposed methodologies:** capacity model formula, throughput-target methodology, horizontal-scale-out trigger translation, load-shedding tier ladder, autoscaling signal combination, headroom methodology, multi-tenant fairness mechanism, burst-handling sequence, scale state model, scale-in/recovery methodology, capacity testing methodology and phases — all `PROPOSED METHODOLOGY`, none source-defined verbatim.
- **Proposed defaults:** every concrete numeric threshold in §8, §9, §14, §15 is `PROPOSED DEFAULT — VALIDATE LOCALLY`; none is asserted as production fact.
- **Unresolved limitations:** no scenario validates throughput/scale-out (`SOURCE-GAP-SCALING-01`); no multi-tenant-fairness validation exists (`SOURCE-GAP-SCALING-04`); the `SC-NNN` catalog is this document's own synthesis (`SOURCE-GAP-SCALING-05`).
- **Upstream/sibling documents consumed:** `architecture.md`, `interfaces.md`, `conventions.md`, `edge-cases.md`, `scenario-matrix.md`, `security.md`, `observability.md`, `eval.md`, `cache-strategy.md`, `agent-optimization.md`, `inference-optimization.md`, `provider-matrix.md`, `optimization-catalog.md`. None modified.
- **File-scope confirmation:** only `docs/SCALING.md` was created in this pass. No upstream, sibling, or prompt file was modified.
- **Next document:** `implementation-plan` (not generated here, not cascaded into).

## Scaling Readiness

The scaling methodology in this document is sufficiently defined to guide future implementation — capacity model, throughput-target methodology, horizontal-scale-out trigger translation, load-shedding discipline preserving governance precedence, multi-tenant fairness approach, per-deployment-mode profiles, and a failure/overload matrix are all present and grounded in verified upstream requirements. Exact production thresholds, limits, and validation evidence remain intentionally unresolved pending real traffic and local capacity testing (§32) — this is recorded honestly via five non-blocking source gaps (§48), not treated as a blocking issue.

READY FOR NEXT DOCUMENTATION PHASE
