# EAIOC — V2 Consolidated Master Prompt for `SCALING.md`

## 0. ROLE

You are the documentation engineer for the **Enterprise Agent & LLM Inference Optimization Control Plane (EAIOC)** repository.

Generate the next authoritative downstream documentation artifact:

`docs/SCALING.md`

You are operating at:

**Level 0 — RESEARCH / PRE-IMPLEMENTATION**

This is a documentation-generation task, not an implementation task.

Create exactly one file:

`docs/SCALING.md`

Do not modify, regenerate, overwrite, silently correct, or cascade changes into any upstream, sibling, prompt, implementation, or future document.

---

# 1. DOCUMENTATION CHAIN POSITION

`SCALING.md` is the **tenth generated downstream document**.

The authoritative chain is:

1. `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt`
2. `Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md`
3. `architecture.md`
4. `interfaces.md`
5. `conventions.md`
6. `edge-cases.md`
7. `scenario-matrix.md`
8. `optimization-catalog.md`
9. `provider-matrix.md`
10. `cache-strategy.md`
11. `agent-optimization.md`
12. `inference-optimization.md`
13. `quality-gates.md`
14. `security.md`
15. `observability.md`
16. `eval.md`
17. **`SCALING.md` — THIS DOCUMENT**
18. `implementation-plan`
19. `requirements-traceability`
20. ADRs

Immediate predecessor:

`eval.md`

Next document:

`implementation-plan`

The preceding documents are authoritative and their own readiness verdicts have already been passed.

Do not re-verify their entire contents unnecessarily. Verify only the specific source sections/IDs needed for this document.

---

# 2. PRIMARY OBJECTIVE

Produce a rigorous, repository-grounded scaling specification that answers:

> **How does EAIOC safely and predictably scale as request volume, token volume, concurrency, tenants, agents, sub-agents, tool calls, model/provider usage, evaluation workload, and control-plane workload increase?**

The document owns:

* capacity planning methodology;
* throughput targets;
* horizontal-scaling trigger conditions;
* scaling decision logic;
* load-shedding operational procedure beneath the existing SPC boundary;
* capacity/headroom methodology;
* multi-tenant capacity allocation;
* burst and overload handling;
* scale testing methodology;
* cross-execution scaling analysis;
* per-deployment-mode scaling profiles.

It must **not** redefine mechanisms owned elsewhere.

The fundamental distinction is:

> `SCALING.md` answers **"Can the system handle this workload safely?"**

while:

> `eval.md` answers **"Did the optimization actually produce validated benefit?"**

and:

> `security.md` answers **"Is this workload/action authorized?"**

---

# 3. CRITICAL REPOSITORY-SPECIFIC CONTEXT

Unlike most previous downstream documents, `NFR-008` does not appear to originate from a dedicated pre-enumerated scaling catalog.

The current source hypothesis is that scalability is defined primarily through:

* `architecture.md` §33 — Enterprise Deployment Model;
* `architecture.md` §37 — `NFR-008` Scalability and `NFR-014` Control-Plane Self-Protection;
* `architecture.md` §47.3.2 — Cross-Execution Coordinator (`XEC`);
* `architecture.md` §47.4.1 — Self-Protection Controller (`SPC`);
* `architecture.md` §47.4.2 — Net Optimization Economics;
* `architecture.md` §31 — Observability and Governance.

**Do not trust this characterization blindly.**

Independently verify all referenced sections and IDs against the current repository before writing.

This document is the first genuinely operational capacity/scaling build in the chain rather than merely elaborating a complete `SC-NNN` source catalog.

Therefore:

> **Research harder before writing, but do not invent source requirements merely because the source lacks a catalog.**

---

# 4. AUTHORITATIVE SOURCE ORDER

Use this precedence:

1. Problem Statement
2. Engineering Spec
3. `architecture.md`
4. `interfaces.md`
5. `conventions.md`
6. `edge-cases.md`
7. `scenario-matrix.md`
8. `optimization-catalog.md`
9. `provider-matrix.md`
10. `cache-strategy.md`
11. `agent-optimization.md`
12. `inference-optimization.md`
13. `quality-gates.md`
14. `security.md`
15. `observability.md`
16. `eval.md`
17. root `CLAUDE.md`

If a source contradicts another source:

* do not silently choose one;
* determine whether it is terminology/scope or a genuine contradiction;
* record genuine contradictions explicitly;
* do not modify upstream files to resolve them.

---

# 5. REQUIRED PRE-WRITING SOURCE VERIFICATION

Before writing `SCALING.md`, inspect at minimum:

### `architecture.md`

Verify exact current text for:

* §33 — Enterprise Deployment Model;
* §37 — `NFR-008`, `NFR-014`;
* §47.3.2 — XEC;
* §47.4.1 — SPC;
* §47.4.2 — Net Optimization Economics;
* §31 — Observability and Governance;
* all relevant NFR/OBJ/AC traceability tables.

Also search for:

* `capacity`
* `throughput`
* `horizontal`
* `scal`
* `concurrency`
* `concurrency limit`
* `rate limit`
* `autoscal`
* `load shed`
* `backpressure`
* `queue`
* `headroom`
* `overload`
* `resource`

### `interfaces.md`

Verify:

* `DepthSheddingDecision`;
* exact field `security_stages_preserved`;
* exact `INTF-NNN` identifier;
* `INTF-069` CrossExecutionCoordinator;
* `OPT-6xxx` `CAPACITY` error-code class;
* `SpendStatus`;
* `evaluate_budget()`;
* `WITHIN_BUDGET`;
* `THROTTLED`;
* `HALTED`.

Do not redefine SGE interface behavior.

### `conventions.md`

Verify the exact sections covering:

* `intelligence/self_protection/`;
* SPC/backpressure;
* governance/spend;
* per-tenant/org/user/app rate limits;
* fail-open/fail-closed precedence.

### `edge-cases.md`

Verify directly:

* `EC-077`;
* `EC-191`;
* `EC-192`;
* `EC-194`;

and search the entire file for additional scaling/capacity/concurrency/resource-related cases.

### `scenario-matrix.md`

Verify:

* Domain AA — Concurrency;
* `SCN-CONC-001`–`006`;
* exact NFR/OBJ/AC traceability;
* whether these scenarios validate:

  * concurrency correctness;
  * throughput;
  * capacity;
  * horizontal scaling;
  * scale-out triggers.

Do **not** assume concurrency-correctness scenarios validate throughput or scale-out.

Search all 30 domains for other scaling-relevant scenarios.

### `security.md`

Consume:

* SGE/GSP mechanism;
* budget throttling/halt;
* authorization/governance precedence.

Do not redefine it.

### `observability.md`

Consume existing:

* latency metrics;
* throughput metrics;
* queue metrics;
* saturation metrics;
* relevant dimensions.

Do not invent a competing metric name where an existing canonical metric exists.

### `eval.md`

Consume:

* benchmarking methodology;
* regression methodology;
* statistical methodology;
* load/capacity evaluation boundary.

Do not duplicate evaluation methodology.

### `CLAUDE.md`

Read fully for:

* anti-fabrication rules;
* ID conventions;
* tenant isolation;
* fail-open/fail-closed rules;
* documentation-chain discipline.

---

# 6. DOCUMENT HEADER

Use:

```text
# Scaling, Capacity, Throughput, Concurrency, Resource Management, and Enterprise Scale Reference

**Document ID:** EAIOC-SCALING-001
**Status:** PRE-IMPLEMENTATION — Level 0 (RESEARCH)
**Version:** 1.0.0
**Generated:** <repository date>
**Generation prompt:** <actual prompt path/version if known>
```

Then state:

* position in documentation chain;
* predecessor `eval.md`;
* next document `implementation-plan`;
* sources consumed;
* scope boundary.

---

# 7. EVIDENCE LABELING

Use the same discipline as `eval.md`.

Every substantive statement must be identifiable as:

* `SOURCE-DEFINED`
* `SOURCE-DERIVED`
* `PROPOSED METHODOLOGY`
* `PROPOSED DEFAULT — VALIDATE LOCALLY`
* `DERIVED METRIC`
* `ILLUSTRATIVE EXAMPLE`

Every concrete number not directly stated by an authoritative source must be:

`PROPOSED DEFAULT — VALIDATE LOCALLY`

Do not present any proposed number as production-proven.

---

# 8. SCOPE BOUNDARY

Create a clear ownership matrix.

At minimum:

| Concern                             | Owner                                       |
| ----------------------------------- | ------------------------------------------- |
| Capacity-planning methodology       | `SCALING.md`                                |
| Throughput targets                  | `SCALING.md`                                |
| Horizontal-scaling triggers         | `SCALING.md`                                |
| Capacity headroom                   | `SCALING.md`                                |
| Backpressure operational procedure  | `SCALING.md`, extending SPC boundary        |
| Load-shedding operational procedure | `SCALING.md`, extending SPC boundary        |
| Multi-tenant capacity allocation    | `SCALING.md`                                |
| Deployment-mode scaling profiles    | `SCALING.md`                                |
| Scale testing methodology           | `SCALING.md`, consuming `eval.md`           |
| SPC mechanism                       | `architecture.md` §47.4.1                   |
| XEC mechanism                       | `architecture.md` §47.3.2 / `interfaces.md` |
| Spend governance                    | `security.md` / SGE                         |
| Observability implementation        | `observability.md`                          |
| Evaluation/statistical methodology  | `eval.md`                                   |
| Net Optimization Economics          | `architecture.md` §47.4.2                   |
| Cache mechanics                     | `cache-strategy.md`                         |
| Agent mechanics                     | `agent-optimization.md`                     |
| Inference/P5 mechanics              | `inference-optimization.md`                 |
| Provider facts/limits               | `provider-matrix.md`                        |
| Optimization definitions            | `optimization-catalog.md`                   |
| Interface schemas                   | `interfaces.md`                             |
| Implementation sequencing           | `implementation-plan`                       |

---

# 9. CANONICAL ID-SERIES DECISION

Do not automatically invent an `SC-NNN` catalog merely to make the document resemble prior documents.

First search:

* `architecture.md`
* `interfaces.md`
* `conventions.md`

for a pre-existing enumerable capacity/scaling catalog.

Search:

* `capacity`
* `throughput`
* `horizontal`
* `scal`
* `concurrency limit`
* `rate limit`
* `autoscal`
* `load shed`
* `backpressure`

### If a genuine canonical scaling catalog exists

Use its exact IDs and terminology.

### If no canonical catalog exists

Do not pretend one exists.

If an `SC-NNN` series is useful for operational decomposition, introduce it only for independently-scaled surfaces that this document itself defines.

For example, candidate surfaces may include:

* control-plane compute/self-protection;
* cross-execution coordination;
* tenant governance/rate-control interaction;
* cache layer;
* provider/model concurrency;
* evaluation/benchmark workers.

But these are only examples.

Independently determine the actual breakdown from repository evidence.

Explicitly state that any newly introduced scaling catalog is:

`SOURCE-DERIVED`

and not an upstream-defined canonical catalog.

---

# 10. SCALING INVARIANTS

Define explicit invariants.

At minimum preserve:

1. tenant isolation;
2. security/governance precedence;
3. fail-open optimization behavior;
4. fail-closed security behavior;
5. fallback availability;
6. quality protection;
7. cost accounting;
8. evaluation integrity;
9. observability;
10. provider-neutrality;
11. bounded resource consumption;
12. no unbounded fan-out.

Most importantly:

> **Self-protection failures are optimization failures (fail-open) unless overload would otherwise cause a security/authorization/PII check to be skipped; in that specific case the affected request fails closed.**

Verify exact source wording and preserve it accurately.

---

# 11. CORE SCALING DIMENSIONS

Define capacity across multiple dimensions.

### Request dimensions

* requests/sec;
* requests/minute;
* sustained request rate;
* burst request rate;
* concurrent requests.

### LLM dimensions

* model calls/request;
* input tokens/request;
* output tokens/request;
* total tokens/request;
* input tokens/sec;
* output tokens/sec;
* total tokens/sec;
* context size;
* cached-token ratio.

### Agent dimensions

* agents/request;
* iterations/request;
* sub-agents/request;
* tool calls/request;
* parallel tool calls;
* workflow depth;
* fan-out/fan-in.

### Control-plane dimensions

* optimization decisions/sec;
* policy evaluations/sec;
* cache-control operations;
* evaluation jobs;
* benchmark jobs;
* regression checks;
* telemetry events;
* policy updates;
* tenant configurations.

Explain why:

> **RPS alone is not a sufficient capacity model for EAIOC.**

---

# 12. CAPACITY MODEL

Define a conceptual capacity model relating:

```text
request rate
× work per request
× concurrency
× amplification
× resource cost
```

to required capacity.

Cover:

* control-plane compute;
* control-plane memory;
* queue capacity;
* cache capacity;
* provider capacity;
* evaluation capacity;
* telemetry capacity;
* governance-check throughput.

Where formulas are derived, label them:

`DERIVED METRIC`

or:

`PROPOSED METHODOLOGY`

Do not invent actual EAIOC capacity.

---

# 13. THROUGHPUT TARGETS

Define the methodology for establishing:

* sustainable throughput;
* peak throughput;
* burst throughput;
* degraded-mode throughput;
* recovery throughput.

Propose measurable dimensions such as:

* requests/sec;
* decisions/sec;
* tokens/sec;
* queue depth;
* concurrent executions;
* added latency;
* error rate.

Use existing observability metric names wherever available.

Any actual target number must be:

`PROPOSED DEFAULT — VALIDATE LOCALLY`

If SPC defines a latency/processing-cost budget, distinguish:

* source-defined budget;
* scaling target;
* scale-out trigger;
* overload trigger.

Do not conflate them.

---

# 14. HORIZONTAL-SCALING TRIGGERS

Define concrete conceptual scale-out triggers using signals such as:

* queue depth;
* queue growth rate;
* request concurrency;
* throughput;
* latency percentile;
* error rate;
* CPU saturation;
* memory pressure;
* token rate;
* worker utilization;
* provider saturation.

SPC already identifies overload signal categories.

`SCALING.md` owns translating those signals into proposed scale-out thresholds.

Clearly distinguish:

### Scale-out

from:

### Backpressure

from:

### Load shedding

from:

### Hard overload protection.

Do not make them interchangeable.

---

# 15. FAN-OUT / AMPLIFICATION

Mandatory.

Model how:

```text
1 incoming request
    ↓
optimization decisions
    ↓
LLM calls
    ↓
agent iterations
    ↓
sub-agent fan-out
    ↓
tool calls
    ↓
cache operations
    ↓
telemetry events
    ↓
evaluation/regression signals
```

can amplify resource consumption.

Address:

* retries;
* provider fallback;
* model cascades;
* cache misses;
* agent fan-out;
* sub-agent fan-out;
* tool-call amplification;
* shadow evaluation amplification.

Explicitly evaluate whether an optimization can:

> reduce inference cost while increasing control-plane overhead enough to erase its net benefit.

---

# 16. CONCURRENCY MODEL

Define concurrency at:

1. request level;
2. optimization level;
3. model-call level;
4. agent level;
5. sub-agent level;
6. tool-call level;
7. evaluation level;
8. telemetry level;
9. cache level;
10. tenant level.

Cover:

* global concurrency;
* tenant concurrency;
* workload concurrency;
* fairness;
* queueing;
* starvation;
* resource exhaustion;
* fan-out amplification.

---

# 17. BACKPRESSURE

Build the operational scaling layer beneath SPC.

Cover:

* bounded queues;
* admission control;
* rate limiting;
* concurrency limiting;
* queue prioritization;
* tenant-aware backpressure;
* asynchronous processing;
* retry suppression;
* overload signaling.

The critical invariant:

> **Backpressure may reduce optimization depth, but must never skip governance/security/authorization/PII checks.**

Preserve:

`DepthSheddingDecision.security_stages_preserved = true`

after verifying the exact current interface field and ID.

---

# 18. LOAD SHEDDING / DEGRADED MODES

Define a proposed tiered degradation model only if supported by repository evidence.

If introducing conceptual levels, clearly label them:

`PROPOSED METHODOLOGY`

Possible conceptual progression:

```text
NORMAL
  ↓
REDUCED OPTIMIZATION
  ↓
DISABLE EXPENSIVE OPTIONAL OPTIMIZATIONS
  ↓
BYPASS NON-CRITICAL CONTROL-PLANE WORK
  ↓
BASELINE / FALLBACK
```

For each level define:

* trigger;
* allowed actions;
* prohibited actions;
* tenant behavior;
* governance behavior;
* recovery.

Never allow degradation to bypass:

* SGE;
* DGE;
* TMG;
* HAG;
* CIS;
* other mandatory security/governance stages identified by the sources.

---

# 19. AUTOSCALING

Define conceptual autoscaling methodology.

Cover:

* horizontal scale-out;
* scale-in;
* queue-based signals;
* concurrency signals;
* latency signals;
* utilization signals;
* token-rate signals;
* saturation signals;
* provider-limit signals.

Explain why CPU-only autoscaling is insufficient for EAIOC.

Do not invent:

* exact CPU percentages;
* exact queue thresholds;
* exact cooldowns;
* exact replica counts.

Any proposed numeric threshold must carry:

`PROPOSED DEFAULT — VALIDATE LOCALLY`

---

# 20. CAPACITY HEADROOM

Define:

* baseline capacity;
* normal capacity;
* peak capacity;
* burst capacity;
* reserved capacity;
* safety headroom;
* degraded capacity.

Define methodology for determining headroom.

Do not invent a fixed percentage unless clearly marked as a proposed default.

---

# 21. MULTI-TENANT SCALING

Mandatory.

Define:

* tenant-specific rate limits;
* tenant concurrency;
* tenant capacity;
* tenant budgets;
* fairness;
* noisy-neighbor protection;
* weighted allocation;
* tenant-aware queues;
* tenant-aware degradation;
* tenant-level telemetry.

Preserve:

> One tenant's workload must not silently starve, contaminate, or alter another tenant's capacity/evaluation behavior.

Do not blend tenant measurements where the source requires isolation.

---

# 22. FAIRNESS / NOISY NEIGHBOR

Operationalize protection against a tenant consuming disproportionate shared capacity.

Cover:

* quotas;
* rate limits;
* concurrency limits;
* weighted fair allocation;
* admission control;
* tenant queue isolation;
* tenant priority;
* workload shedding.

Distinguish:

* global overload;
* tenant-specific overload.

A single tenant reaching its quota should not automatically imply global system saturation.

---

# 23. TOKEN-BASED CAPACITY

RPS is insufficient.

Define capacity in terms of:

* input tokens/sec;
* output tokens/sec;
* total tokens/sec;
* tokens/request;
* context size;
* cached-token ratio;
* model mix.

Explain effects on:

* provider capacity;
* latency;
* cost;
* concurrency;
* queueing;
* optimization overhead.

Provider-specific token limits must come from `provider-matrix.md`.

---

# 24. MODEL / PROVIDER CAPACITY

Consume `provider-matrix.md`.

Cover:

* provider limits;
* model limits;
* rate limits;
* token limits;
* provider fallback;
* provider outage;
* throttling;
* model distribution;
* routing/cascade effects.

Do not invent provider facts.

`SCALING.md` defines how provider limits participate in capacity planning.

---

# 25. CACHE SCALING

Consume `cache-strategy.md`.

Do not redefine cache mechanics.

Cover:

* cache hit/miss volume;
* cache lookup load;
* storage growth;
* invalidation;
* hot keys;
* cache stampede;
* tenant isolation;
* provider-native cache separation.

Distinguish:

> high cache hit rate

from:

> high cache operation volume.

---

# 26. AGENT / MULTI-AGENT SCALE

Consume `agent-optimization.md`.

Cover:

* agent concurrency;
* iterations;
* sub-agent fan-out;
* tool calls;
* parallel calls;
* long-running workflows;
* runaway workflows;
* cancellation;
* timeout;
* resource budgets.

A reduction in iterations/tool calls is not automatically a scaling success if task success deteriorates.

---

# 27. P5 / INFERENCE-SERVING SCALE

Consume `inference-optimization.md`.

Maintain separate attribution for:

* Layer 1/2 optimization;
* P5 inference-serving.

Combined reports may exist, but P5 contribution must remain separately attributable.

---

# 28. CROSS-EXECUTION COORDINATION AT SCALE

Consume XEC's mechanism from:

* `architecture.md` §47.3.2;
* `interfaces.md` `INTF-069`.

Do not redefine XEC.

Analyze:

* concurrent execution volume;
* conflict frequency;
* coordination overhead;
* lock contention;
* reconciliation volume;
* non-idempotent resources;
* coordination mechanism unavailability;
* scaling implications.

Verify and incorporate:

* `EC-191`;
* `EC-192`;
* `EC-194`.

Do not claim that XEC's correctness mechanisms themselves constitute throughput/scaling targets.

---

# 29. SELF-PROTECTION CONTROLLER BOUNDARY

Consume SPC from `architecture.md` §47.4.1.

Do not redefine SPC's internal mechanism.

`SCALING.md` owns:

* when capacity pressure requires scaling;
* how scale-out interacts with SPC;
* how proposed thresholds relate to SPC signal types;
* how load-shedding tiers interact with SPC.

Clearly separate:

### SPC mechanism

from:

### scaling decision methodology.

---

# 30. SPEND GOVERNANCE BOUNDARY

Consume SGE from `security.md`.

Do not redefine:

* budget calculation;
* authorization;
* throttle;
* halt;
* governance.

Capacity logic must never imply authorization.

For example:

> `WITHIN_BUDGET` does not mean "authorized to execute."

And:

> available capacity does not override security/governance controls.

---

# 31. PER-DEPLOYMENT-MODE SCALING PROFILES

Verify `architecture.md` §33's six deployment modes A–F.

For each mode document:

* workload profile;
* request pattern;
* concurrency profile;
* burst behavior;
* scaling concern;
* primary bottleneck candidates;
* relevant control-plane behavior.

Do not assume the mode descriptions in this prompt are correct.

Verify them directly.

Explicitly distinguish modes with:

* interactive/low concurrency;
* API/middleware sustained load;
* CLI/single invocation;
* background/batch;
* CI/CD burst;
* any other exact repository-defined modes.

Do not force a common scaling strategy across all modes.

---

# 32. EVALUATION / BENCHMARK SCALING

Consume `eval.md`.

Define how:

* benchmark jobs;
* shadow evaluations;
* A/B experiments;
* regression runs;
* evaluation workers

scale without distorting production traffic.

Do not reimplement statistical methodology.

Important invariant:

> Evaluation workload must not consume enough shared production capacity to distort the workload being evaluated.

Separate:

* production capacity;
* evaluation capacity;
* benchmark worker capacity.

---

# 33. OBSERVABILITY AT SCALE

Consume `observability.md`.

Address:

* metric cardinality;
* tenant dimensions;
* provider dimensions;
* model dimensions;
* optimization dimensions;
* queue-depth visibility;
* trace volume;
* event volume;
* logging volume;
* dashboard/query load.

Do not redefine telemetry implementation.

Use canonical metric names already established upstream.

---

# 34. RESOURCE MANAGEMENT

Define resource classes:

* CPU;
* memory;
* network;
* storage;
* queues;
* worker pools;
* provider quotas;
* token budgets;
* concurrency budgets.

Explain how different workloads can produce different bottlenecks.

---

# 35. BOTTLENECK IDENTIFICATION

Define a methodology to identify whether the limiting resource is:

* CPU;
* memory;
* network;
* queue;
* provider;
* token throughput;
* concurrency;
* cache;
* evaluation workers;
* telemetry;
* governance checks.

Do not claim a particular bottleneck exists without evidence.

---

# 36. LATENCY UNDER SCALE

Consume canonical latency metrics.

Cover:

* P50;
* P95;
* P99;
* queue wait;
* optimization overhead;
* model latency;
* tool latency;
* cache latency;
* control-plane latency.

Do not invent latency SLOs.

If a source defines a latency/processing budget, distinguish it from proposed scaling thresholds.

---

# 37. BURST HANDLING

Define methodology for sudden traffic changes.

Illustrative examples may include:

* 2× normal traffic;
* 5× normal traffic;
* tenant spike;
* provider throttling;
* large agent fan-out;
* cache invalidation storm.

Examples are not requirements.

A conceptual sequence may be:

```text
detect
→ absorb
→ queue
→ scale
→ degrade
→ shed
→ recover
```

If not source-defined, mark it `PROPOSED METHODOLOGY`.

---

# 38. FAILURE / OVERLOAD MATRIX

Create a detailed matrix covering at least:

| Condition             | Detection | Scaling Impact | Safe Response | Evidence/Ownership | Recovery |
| --------------------- | --------- | -------------- | ------------- | ------------------ | -------- |
| Global traffic surge  |           |                |               |                    |          |
| Tenant traffic surge  |           |                |               |                    |          |
| Provider throttling   |           |                |               |                    |          |
| Provider outage       |           |                |               |                    |          |
| Queue saturation      |           |                |               |                    |          |
| CPU saturation        |           |                |               |                    |          |
| Memory pressure       |           |                |               |                    |          |
| Token-rate saturation |           |                |               |                    |          |
| Agent fan-out         |           |                |               |                    |          |
| Sub-agent explosion   |           |                |               |                    |          |
| Cache stampede        |           |                |               |                    |          |
| Evaluation overload   |           |                |               |                    |          |
| Telemetry overload    |           |                |               |                    |          |
| Noisy neighbor        |           |                |               |                    |          |
| Autoscaling failure   |           |                |               |                    |          |
| XEC contention        |           |                |               |                    |          |
| Governance bottleneck |           |                |               |                    |          |

Do not invent source-defined recovery behavior.

---

# 39. CAPACITY TESTING METHODOLOGY

Define methodology for eventual validation:

* load testing;
* stress testing;
* spike testing;
* soak/endurance testing;
* concurrency testing;
* multi-tenant fairness testing;
* provider-throttling testing;
* degradation testing;
* recovery testing;
* scale-in testing;
* failure testing.

Consume `eval.md` where its evaluation methodology applies.

Do not claim any test has actually been executed.

---

# 40. SCALE WORKLOAD MODEL

Define scale-test dimensions:

* RPS;
* concurrency;
* tokens/request;
* context size;
* agent iterations;
* tool calls;
* sub-agent count;
* tenant count;
* tenant skew;
* model mix;
* provider mix;
* cache hit ratio;
* optimization complexity.

Explain why a single synthetic workload is insufficient.

---

# 41. SCALE TEST PHASES

Propose, if appropriate:

1. Baseline capacity measurement
2. Controlled load increase
3. Saturation identification
4. Bottleneck classification
5. Scale-out validation
6. Burst validation
7. Degraded-mode validation
8. Recovery validation
9. Multi-tenant fairness validation
10. Regression comparison

Clearly label this as:

`PROPOSED METHODOLOGY`

unless directly source-defined.

---

# 42. CAPACITY SLI / SLO BOUNDARY

Do not invent SLOs.

Distinguish:

* SLI;
* SLO;
* capacity target;
* scaling trigger;
* overload threshold;
* hard safety limit.

Consume quality, latency, reliability, cost, and security SLOs from their owning documents.

---

# 43. COST OF SCALING

Scaling has cost.

Address:

* control-plane compute cost;
* worker cost;
* evaluation cost;
* telemetry cost;
* cache cost;
* provider cost;
* additional model calls;
* optimization overhead;
* idle capacity.

Connect this to `eval.md`'s canonical:

* `gross_savings`;
* `optimizer_overhead`;
* `net_savings`;
* `net_savings_pct`.

Do not create a competing cost-accounting model.

---

# 44. SCALING + QUALITY

Explicitly analyze how overload/degradation can affect:

* quality;
* task success;
* context;
* tool usage;
* model choice;
* agent iteration;
* early exit;
* cache behavior.

Any quality degradation must remain subject to `quality-gates.md`.

Scaling pressure must never silently convert a quality requirement into an optional behavior.

---

# 45. SCALING + SECURITY

Consume `security.md`.

Cover:

* resource exhaustion;
* tenant isolation;
* admission control;
* budget limits;
* abuse;
* governance precedence.

Preserve:

> optimization scaling must never bypass authorization.

---

# 46. SCALING + GOVERNANCE

Ensure:

* autoscaling cannot authorize workloads;
* available capacity cannot override governance;
* learned policy changes remain governed;
* emergency degradation cannot skip governance;
* tenant-specific capacity policies remain within security boundaries.

---

# 47. ADVERSARIAL / OVERLOAD DEFENSE

Operationalize `EC-077`.

Verify its exact current content.

Address:

* optimization-overhead abuse;
* overhead-budget cap;
* tenant-level rate limiting;
* resource amplification;
* deliberately expensive input;
* runaway optimization work.

Keep authorization mechanisms in `security.md`.

`SCALING.md` owns the capacity impact and operational response.

---

# 48. EDGE-CASE COVERAGE

Search the complete `edge-cases.md`.

Create:

| Edge Case | Scaling Relevance | Coverage | Evidence |
| --------- | ----------------- | -------- | -------- |

Use only verified IDs.

Possible statuses:

* `DIRECTLY COVERED`
* `PARTIALLY COVERED`
* `NOT COVERED`
* `NOT RELEVANT`

Do not force-fit cases.

---

# 49. SCENARIO COVERAGE

Search all 30 scenario domains.

Specifically verify:

* Domain AA;
* `SCN-CONC-001`–`006`;
* all other potentially scaling-related domains.

Explicitly state whether Domain AA validates:

* concurrency correctness;
* throughput;
* capacity;
* scale-out triggers.

Do not claim more coverage than the scenarios actually provide.

If throughput or horizontal-scale trigger validation is absent, record it as a source gap.

---

# 50. CONTROL-PLANE SCALING ALGORITHMS

Use the established lettered-algorithm convention.

At minimum consider:

### A — Capacity Estimation

### B — Workload Classification

### C — Bottleneck Detection

### D — Admission Control

### E — Concurrency Allocation

### F — Tenant Capacity Allocation

### G — Backpressure Decision

### H — Load-Shedding Decision

### I — Horizontal Scale-Out Decision

### J — Burst Handling

### K — Scale-In / Recovery Decision

### L — Capacity Validation

Add or remove algorithms according to actual source requirements.

For each algorithm specify:

* purpose;
* inputs;
* decision logic;
* outputs;
* failure behavior;
* source classification;
* ownership boundary.

Do not invent APIs or schemas.

---

# 51. SCALE STATE MODEL

Define a conceptual state model if useful:

```text
NORMAL
  ↓
ELEVATED
  ↓
HIGH_LOAD
  ↓
SATURATED
  ↓
DEGRADED
  ↓
SHED
  ↓
RECOVERY
  ↓
NORMAL
```

Mark it `PROPOSED METHODOLOGY` unless source-defined.

Define:

* entry conditions;
* exit conditions;
* allowed actions;
* prohibited actions;
* tenant implications;
* observability requirements.

---

# 52. SCALE-IN / RECOVERY

Cover:

* scale-in;
* stabilization;
* hysteresis;
* draining;
* in-flight requests;
* agent cancellation;
* queue draining;
* provider recovery;
* cache recovery;
* workload redistribution.

Do not invent exact cooldown periods.

---

# 53. RESILIENCE PRINCIPLES

Confirm that scaling preserves:

1. tenant isolation;
2. fail-open optimization;
3. fail-closed security;
4. fallback;
5. quality;
6. observability;
7. evaluation integrity;
8. attribution integrity;
9. governance;
10. bounded resource consumption.

---

# 54. TRACEABILITY

Create a traceability matrix covering all verified scaling-relevant:

* `NFR-NNN`;
* `OBJ-NNN`;
* `AC-NNN`;
* `H-NNN`;
* `EL-NNN`;
* `TECH-NNN`;
* `DA-NNN`;
* `P5` references;
* relevant `EC-NNN`;
* relevant `SCN-*`;
* relevant `INTF-NNN`.

Do not invent IDs.

At minimum independently verify the repository relationships involving:

* `NFR-008`;
* `NFR-014`;
* `OBJ-025`;
* `OBJ-032`;
* `AC-041`;
* `AC-049`;

if they exist as characterized by the source.

---

# 55. SCALING-DIMENSION CATALOG

If a new `SC-NNN` series is justified after source verification, create a concise catalog such as:

| ID | Scaling Surface | Capacity Dimension | Owner | Source |
| -- | --------------- | ------------------ | ----- | ------ |

Do not introduce the catalog merely for stylistic consistency.

If no catalog is justified, explicitly state:

> No canonical source-defined scaling catalog was found.

---

# 56. CROSS-DOCUMENT BOUNDARY MATRIX

Create a final matrix covering:

* `architecture.md`;
* `interfaces.md`;
* `conventions.md`;
* `edge-cases.md`;
* `scenario-matrix.md`;
* `optimization-catalog.md`;
* `provider-matrix.md`;
* `cache-strategy.md`;
* `agent-optimization.md`;
* `inference-optimization.md`;
* `quality-gates.md`;
* `security.md`;
* `observability.md`;
* `eval.md`.

For each state:

* what `SCALING.md` consumes;
* what it owns;
* what it does not redefine.

---

# 57. SOURCE GAPS

Create explicit:

`SOURCE-GAP-SCALING-NN`

entries for genuinely verified missing definitions.

Potential categories include, only if verified:

* no canonical capacity catalog;
* no exact throughput targets;
* no exact scale-out thresholds;
* no concurrency limits;
* no tenant capacity quotas;
* no headroom target;
* no load-shedding thresholds;
* no scale-test acceptance thresholds;
* no provider capacity guarantee;
* no dedicated throughput/scaling scenarios.

For each gap state:

* ID;
* source;
* missing definition;
* why it matters;
* proposed treatment;
* blocking/non-blocking.

Do not manufacture gaps.

---

# 58. SOURCE CONTRADICTIONS

Use:

`CONTRA-SCALING-NN`

only if a genuine contradiction exists.

Distinguish:

* terminology differences;
* scope differences;
* ordering differences;
* genuine normative contradictions.

If none:

`Source Contradictions: 0`

---

# 59. INTERNAL CONSISTENCY AUDIT

Before finalizing, verify:

* every ID exists;
* every cited section exists;
* every numeric threshold is sourced or explicitly labeled;
* no invented production evidence;
* no invented provider limits;
* no competing metric names;
* no duplicate ownership;
* no SPC mechanism redefinition;
* no XEC mechanism redefinition;
* no SGE mechanism redefinition;
* no evaluation methodology redefinition;
* no observability implementation redefinition;
* no cache mechanism redefinition;
* no agent mechanism redefinition;
* no P5 mechanism redefinition;
* tenant isolation remains consistent;
* governance precedence remains consistent;
* fail-open/fail-closed behavior remains consistent;
* scenario coverage is honest;
* edge-case coverage is honest;
* source gaps remain open unless actually resolved upstream.

---

# 60. NO INVENTED PRECISION

Do not invent:

* production RPS;
* production TPS;
* token limits;
* concurrency limits;
* CPU thresholds;
* memory thresholds;
* queue sizes;
* replica counts;
* autoscaling percentages;
* headroom percentages;
* tenant quotas;
* latency SLOs;
* provider limits;
* benchmark results.

If an example is needed:

`ILLUSTRATIVE EXAMPLE`

If proposing a starting value:

`PROPOSED DEFAULT — VALIDATE LOCALLY`

---

# 61. NO FAKE PRODUCTION EVIDENCE

The repository is pre-implementation.

Never claim:

* EAIOC has been load tested;
* EAIOC has achieved a measured throughput;
* EAIOC has proven scale-out;
* EAIOC has production P99;
* EAIOC has validated quotas;
* EAIOC has validated autoscaling;
* EAIOC has proven multi-tenant fairness.

Unless explicit repository evidence exists, none of these should be stated as facts.

---

# 62. FILE-SCOPE RULE

Modify/create **ONLY**:

`docs/SCALING.md`

Do not modify:

* problem statement;
* Engineering Spec;
* architecture;
* interfaces;
* conventions;
* edge-cases;
* scenario-matrix;
* optimization-catalog;
* provider-matrix;
* cache-strategy;
* agent-optimization;
* inference-optimization;
* quality-gates;
* security;
* observability;
* eval;
* `CLAUDE.md`;
* prompts;
* implementation-plan;
* requirements-traceability;
* ADRs;
* source code;
* tests;
* infrastructure files.

---

# 63. NO IMPLEMENTATION

Do not create:

* Java;
* Spring Boot;
* Kubernetes;
* Terraform;
* Docker;
* AWS configuration;
* database schemas;
* executable load tests;
* production autoscaling configuration.

This is a conceptual/research scaling specification.

---

# 64. REQUIRED DOCUMENT STRUCTURE

Use approximately this structure, adapting only when source evidence requires:

1. Status and Maturity
2. How to Read This Document
3. Scope Boundary
4. Scaling Invariants
5. Scaling Architecture
6. Canonical Scaling-ID Decision
7. Scaling Dimensions
8. Capacity Model
9. Throughput Model
10. Concurrency Model
11. Fan-Out / Amplification
12. Backpressure
13. Load Shedding / Degraded Modes
14. Autoscaling
15. Capacity Headroom
16. Multi-Tenant Scaling
17. Fairness / Noisy Neighbor
18. Token-Based Capacity
19. Model / Provider Capacity
20. Cache Scaling
21. Agent / Multi-Agent Scaling
22. P5 / Inference-Serving Scaling
23. Cross-Execution Scaling
24. Self-Protection Boundary
25. Spend Governance Boundary
26. Deployment-Mode Scaling Profiles
27. Evaluation / Benchmark Scaling
28. Observability at Scale
29. Resource Management
30. Bottleneck Identification
31. Latency Under Scale
32. Burst Handling
33. Failure / Overload Matrix
34. Capacity Testing Methodology
35. Scale Workload Model
36. Scale Test Phases
37. Capacity SLI/SLO Boundary
38. Cost of Scaling
39. Scaling + Quality
40. Scaling + Security
41. Scaling + Governance
42. Adversarial / Overload Defense
43. Control-Plane Scaling Algorithms
44. Scale State Model
45. Recovery / Scale-In
46. Resilience Principles
47. Scenario Coverage
48. Edge-Case Coverage
49. Requirement / Traceability Matrix
50. Scaling-Dimension Catalog, if justified
51. Cross-Document Boundary Matrix
52. Source Gaps
53. Source Contradictions
54. Final Report
55. Scaling Readiness

You may adjust numbering if the source material requires it, but do not omit substantive required coverage.

---

# 65. FINAL REPORT

End with a concise final report containing:

* Document ID;
* version;
* status;
* major scaling domains covered;
* number of scaling algorithms;
* scaling catalog count, if one exists;
* relevant scenarios;
* relevant edge cases;
* traceability coverage;
* source gaps;
* contradiction count;
* proposed methodologies;
* proposed defaults;
* unresolved limitations;
* upstream documents consumed;
* file-scope confirmation;
* next document: `implementation-plan`.

---

# 66. SCALING READINESS

The readiness verdict must distinguish:

### READY

All required scaling methodology is documented and no blocking source gap prevents coherent scaling design.

### READY WITH NON-BLOCKING GAPS

The scaling methodology is sufficiently defined, but exact production thresholds, limits, or validation evidence remain intentionally unresolved.

### NOT READY

A missing source definition prevents coherent scaling design.

Do not manufacture a passing verdict.

If ready, the literal final line of the file must be:

`READY FOR NEXT DOCUMENTATION PHASE`

It must occur **exactly once** and be the true final line.

---

# 67. FINAL EXECUTOR VERIFICATION

Before delivery:

### Content verification

* [ ] NFR-008 independently verified.
* [ ] NFR-014 independently verified.
* [ ] SPC independently verified.
* [ ] XEC independently verified.
* [ ] SGE boundary independently verified.
* [ ] `DepthSheddingDecision` independently verified.
* [ ] `security_stages_preserved` independently verified.
* [ ] `EC-077` independently verified.
* [ ] `EC-191/192/194` independently verified if relevant.
* [ ] Domain AA independently verified.
* [ ] All scaling-relevant scenario domains independently searched.
* [ ] All scaling-relevant edge cases independently searched.
* [ ] Any new `SC-NNN` series is justified rather than artificial.
* [ ] Every numeric threshold is sourced or tagged.
* [ ] No production capacity is fabricated.
* [ ] No upstream mechanism is redefined.
* [ ] No competing metrics are invented.
* [ ] Tenant isolation preserved.
* [ ] Governance/security precedence preserved.
* [ ] Fail-open optimization / fail-closed security preserved.
* [ ] Evaluation methodology remains owned by `eval.md`.
* [ ] Net Optimization Economics remains owned by `architecture.md`.

### File verification

Run:

```text
git status --short
git diff --stat
```

Confirm that only:

`docs/SCALING.md`

is new/changed.

### Final-line verification

Search for:

`READY FOR NEXT DOCUMENTATION PHASE`

Confirm:

1. it occurs exactly once;
2. it is under `## Scaling Readiness`;
3. it is the literal final line.

---

# 68. FAILURE MODES TO DEFEND AGAINST

The final document must specifically avoid:

1. Artificially creating `SC-NNN` merely to match previous documents.
2. Treating proposed throughput values as production facts.
3. Treating concurrency correctness as proof of throughput scalability.
4. Letting load shedding skip governance/security/PII checks.
5. Redefining SPC.
6. Redefining XEC.
7. Redefining SGE.
8. Conflating capacity planning with net optimization economics.
9. Conflating scaling methodology with evaluation methodology.
10. Inventing provider limits.
11. Inventing production measurements.
12. Ignoring tenant-level isolation.
13. Ignoring noisy-neighbor behavior.
14. Ignoring token-based workload amplification.
15. Ignoring agent/sub-agent fan-out.
16. Ignoring scale-in/recovery.
17. Force-fitting unrelated scenarios.
18. Silently closing source gaps.
19. Silently resolving genuine contradictions.
20. Modifying any file other than `docs/SCALING.md`.

---

# 69. EXECUTION ORDER

Execute in this order:

### Step 1

Grep `architecture.md` for:

* `## 33.`
* `## 37.`
* `## 47.3.2`
* `## 47.4.1`
* `## 47.4.2`
* `## 31.`

### Step 2

Verify all IDs and fields in `interfaces.md`.

### Step 3

Verify scaling-related sections in `conventions.md`.

### Step 4

Verify `EC-077`, `EC-191`, `EC-192`, `EC-194` and search all edge cases for additional relevant cases.

### Step 5

Search all 30 scenario domains, not only Domain AA.

### Step 6

Verify the canonical metric/telemetry names in `observability.md`.

### Step 7

Verify the evaluation boundary in `eval.md`.

### Step 8

Determine whether a canonical scaling catalog exists.

### Step 9

Draft the complete document.

### Step 10

Perform ID, threshold, ownership, scenario, edge-case, contradiction, and file-scope audits.

### Step 11

Write only:

`docs/SCALING.md`

### Step 12

Run final repository status/diff verification.

### Step 13

Verify the readiness line exactly as specified.

Do not stop at an outline.

Produce the complete `SCALING.md` artifact.
