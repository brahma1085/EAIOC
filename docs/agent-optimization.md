# Agent Optimization Reference (EAIOC)

**Status:** Downstream implementation reference. Elaborates `architecture.md` §18 (Agent Loop and Multi-Agent Economics) and §19 (Adaptive Model, Reasoning, and Output Control), and the agent-loop-facing `DA-NNN` modules `optimization-catalog.md` intentionally left at applicability/schema level. Subordinate to the Problem Statement, Engineering Specification, Architecture, Interfaces, Conventions, Edge Cases, and Scenario Matrix; also subordinate to `optimization-catalog.md`, `provider-matrix.md`, and `cache-strategy.md` where either conflicts with the original seven (no such conflict was found — see `## SOURCE CONTRADICTIONS`).

**Live baseline used while drafting this document** (re-derived from each source's own Final Report/§48/§35 section, not copied from any prior prompt's restated table): `scenario-matrix.md` — 263 scenarios, 30 domains, 93 compound scenarios, 71 interfaces (69 DIRECT/2 THIN/0 NOT COVERED), 213 edge cases (177 DIRECT/35 PARTIAL/0 NOT COVERED/1 DUPLICATE), `CONTRA-001` resolved, `EC-151` DIRECT via `SCN-STATE-005`. `optimization-catalog.md` — 20 `TECH-NNN`, 25 `DA-NNN`, 11 `P5 Index` entries, `READY FOR NEXT DOCUMENTATION PHASE`. No new `TECH-NNN`/`DA-NNN`/`AL-NNN`/`AR-NNN` ID is created anywhere in this document.

---

## 0. Central Principle — Agent Memory Authority (H09)

Every algorithm in this document reads or could influence state that resembles "agent memory." `conventions.md` §12.6 (traceability: `architecture.md` §47.3.1; `interfaces.md` §43.13; PS §52.9; OBJ-031; AC-047) establishes, verbatim in substance:

> Agent-owned working memory (`MemoryStore`, INTF §16) — scratch state, transient observations, local plans, temporary working information — is expected and unrestricted, but is **never authoritative** over `execution/state/` (ESM) execution state, current authorization state, current policy state (the `policy_version` pinned at admission), or `execution/context_version/` (CVM) / `execution/workflow_version/` (WVM) versions. Every read from `MemoryStore` that could inform a decision affecting execution truth must carry a `memory_version`/provenance tag, compared against current ESM/CVM/WVM state before being trusted (`MemoryAuthorityCheck`, INTF §43.13). On disagreement, the ESM/CVM/WVM-owned value wins and the disagreement is surfaced as an event, never silently resolved in memory's favor. An unresolvable conflict defaults to treating the memory claim as stale/untrusted for that decision.

The exact `MemoryAuthorityCheck` contract (`interfaces.md` §43.13, restated verbatim, not redefined):

```
interface MemoryAuthorityCheck {
  check_conflict(execution_id: string, memory_claim: MemoryClaim) -> MemoryConflictResult
}

MemoryClaim {
  entry_ref:      string    // References a MemoryEntry (INTF-028, §16)
  claimed_state:  map<string, any>
  memory_version: string
}

MemoryConflictResult {
  status:              enum { CONSISTENT, CONFLICT_DETECTED, UNRESOLVABLE }
  authoritative_source: enum { ESM, CVM, WVM, AUTHORIZATION }
  authoritative_value:  any
  claimed_value:        any
  surfaced_at:          ISO8601 string
}
```

**No algorithm in this document — loop-continuation, waste detection, sub-agent value estimation, early exit, memory optimization, or plan-context retention — may substitute a cached/remembered value for a `check_conflict()`/`reconcile()` call against ESM when the decision affects execution truth.** This principle is referenced from every section below where it is materially relevant, rather than restated in full each time.

---

## 1. Agent Optimization Control Loop

EAIOC agent optimization is a **state-aware control loop**, not a one-shot decision, because execution state can change between one iteration and the next (model/provider switch, permission revocation, policy change, external-state mutation, supersession):

```
Observe → Reconcile (H09/ESM/CVM/WVM) → Validate → Classify (progress/waste, §3)
  → Generate Candidate Optimizations → Check Preconditions
  → Authorization / Policy / Governance → Capability / Feasibility (FTR, provider-matrix.md)
  → Estimate Benefit → Estimate Optimization Overhead → Evaluate Quality / Execution Risk
  → Select → Apply → Verify → Measure
  → Continue / Reconcile / Roll Back / Recover
```

Every `AL-NNN`/`AR-NNN`/`DA-NNN` entry below is a concrete instantiation of one or more stages of this loop, not an independent mechanism.

---

## 2. Canonical Entries

Elaborates exactly `AL-001`–`AL-006` (`architecture.md` §18, `conventions.md` §12.1–12.5), `AR-001`–`AR-004` (`architecture.md` §19), and the seven agent-loop-facing `DA-NNN` modules `optimization-catalog.md` left at schema level: `DA-009`, `DA-010`, `DA-013`, `DA-014`, `DA-018`, `DA-020`, `DA-021`. No ID below is new.

### AL-001 — Agent Loop Progress Meter

**Owning Architecture Section:** `architecture.md` §18.1; `conventions.md` §12.1.
**Concrete Algorithm:** Every agent iteration emits an `AgentIterationReport` (`interfaces.md` §11.2, restated verbatim below) containing `tokens_consumed`, `cost_this_iteration`, `cumulative_cost`, `information_gained`, `state_change`, `failures_this_iteration`, and a computed `AgentProgress.objective_completion_pct`. Objective progress is tied to the task's own completion criteria (e.g., "all required tests pass," "patch applied and validated") — never a generic token/step heuristic. `information_gained` must be tracked as **marginal**, not absolute, information gain: `marginal_gain = new_unique_information / total_information_so_far` (this is `PROPOSED METHODOLOGY`, grounded directly in `EC-046`'s detection method, since no source document states a universal formula).
**Formula/Estimation Method:** `marginal_gain` per iteration, tagged `PROPOSED METHODOLOGY`; `state_change` is a distance metric between successive `AgentState.completed_actions`/`key_decisions` snapshots, method left to implementation and tagged `PROPOSED METHODOLOGY` where not interface-defined.
**Proposed Defaults:** None numeric — this component is a measurement instrument; thresholds against its output are defined in AL-002/AL-006/AR-002, not here.
**Agent Memory Authority (H09):** `AgentState.completed_actions`/`key_decisions` (`interfaces.md` §11.2) are the agent-loop's own working-state snapshot, not authoritative over WVM's `completed_actions` — a divergence between the two must be surfaced (§0), not silently trusted from the iteration report alone.
**Session-Phase Interaction:** The current `CL-007` phase (§8) may change which signals matter most (e.g., `state_change` weighted more heavily during `Implementation`, `information_gained` more heavily during `Discovery`) — `PROPOSED METHODOLOGY`, no source specifies per-phase weighting.
**Multi-Agent Interaction:** Each sub-agent produces its own `AgentIterationReport` stream, aggregated by `SubAgentAggregator` (§7) only at handoff, never mid-loop by the parent.
**Anti-Pattern Check:** Guards against treating raw/absolute information gain as sufficient evidence of progress (`EC-046`).
**Fallback:** If a signal cannot be computed for an iteration (e.g., a tool failure prevents state comparison), the iteration is treated as `information_gained = 0`/`state_change = 0` for that step only — never silently omitted from the report.
**Observability Metrics:** `agent.marginal_info_gain` (histogram), `agent.diminishing_returns_detected.count`.
**Validated By:** `SCN-AGENT-001`, `SCN-AGENT-009`, `SCN-CMP-009`.
**Traceability:** PS §37 AL-001; ES (agent loop economics section); ARCH §18.1; INTF §11.2; CONV §12.1; EC-046.

**`AgentIterationReport` (verbatim, `interfaces.md` §11.2):**

```
AgentIterationReport {
  task_id, agent_id, iteration, objective, state: AgentState, progress: AgentProgress,
  tools_called: ToolInvocationResult[], tokens_consumed: LLMUsage,
  cost_this_iteration, cumulative_cost, information_gained, state_change,
  failures_this_iteration: AgentFailure[], next_action: AgentNextAction
}
AgentState { status, current_objective, completed_actions, pending_actions,
             unresolved_questions, key_decisions, checkpointed_at }
AgentProgress { objective_completion_pct, steps_planned, steps_executed, steps_skipped,
                 early_exit_eligible, early_exit_reason }
```

---

### AL-002 — Loop Waste Detector

**Owning Architecture Section:** `architecture.md` §18.2; `conventions.md` §12.2. **H10 hardening (2026-09-15, PS §52.10) applies in full.**
**Concrete Algorithm:** **Repeated iteration is not inherently waste.** The same surface pattern (repeated tool calls, repeated reasoning over similar state) may be productive incremental progress, legitimate reflection/self-correction, a justified retry, or genuine oscillation/failure. The detector classifies against AL-001's per-iteration signals (state change, objective progress, information gain, errors introduced/resolved) and expected task value — **never against iteration count or pattern-match alone** (`architecture.md` §47.7). If the classifier cannot produce a confident category, the iteration is treated as unclassified/continuing under existing SGE/SPC budget constraints, rather than force-stopped or force-continued on an unsupported classification — this exact fallback is stated in `architecture.md` §47.7 itself, not proposed here.
**Formula/Estimation Method:** Classifier inputs = {`state_change`, `objective_completion_pct` delta, `marginal_gain`, `errors_introduced`, `errors_resolved`, `expected_value` of continuing}. The mapping from these inputs to {productive, reflection, justified retry, redundant, oscillation, failure} is `PROPOSED METHODOLOGY` — no source document specifies the classifier's internal decision function, only its required inputs and its required abstention behavior.
**Proposed Defaults:** `max_no_progress_iterations` and `min_state_change` (both named in `interfaces.md` §11.3's `EarlyExitPolicy`) require concrete values to operate; any value proposed here is `PROPOSED DEFAULT — VALIDATE LOCALLY BEFORE PRODUCTION USE`, never authoritative. **Do not encode a fixed "N repeated calls = waste" rule anywhere this document's pseudocode (§18) touches AL-002.**
**Agent Memory Authority (H09):** Loop-state signals come from AL-001's `AgentIterationReport`, which is agent-loop working state — a waste/no-waste classification is never treated as authoritative over WVM's own `completed_actions` record when the two disagree.
**Session-Phase Interaction:** `PROPOSED METHODOLOGY` — the same classifier may reasonably tolerate more "reflection" iterations during `Discovery`/`Planning` than during `Completion`, but no source states this; record as part of `SOURCE-GAP-AGENTOPT-01` (§14) if adopted.
**Multi-Agent Interaction:** A sub-agent's own loop is classified independently by its own AL-002 instance; the parent's AL-003 (sub-agent value re-check, not covered by AL-002) is a separate gate.
**Anti-Pattern Check:** The primary anti-pattern this component exists to prevent is exactly "classifying waste based on iteration count or pattern-match alone" — this is itself an explicit hardening correction (H10), not a generic style note.
**Fallback:** Unclassified iteration → continue under existing SGE/SPC budget constraints (verbatim, `architecture.md` §47.7).
**Observability Metrics:** `agent.loop_detected.count`, `agent.diminishing_returns_detected.count`.
**Validated By:** `SCN-AGENT-009` (loop-awareness classifier distinguishes productive progress from oscillation), `SCN-CODE-010`.
**Traceability:** PS §37 AL-002, §52.10 (H10); ARCH §18.2, §47.7; INTF §11.3; CONV §12.2; EC-045, EC-046, EC-075.

---

### AL-003 — Sub-Agent Value Predictor

**Owning Architecture Section:** `architecture.md` §18.3; `conventions.md` §12.3.
**Concrete Algorithm:** Before spawning a sub-agent, compute:

```
Sub-Agent ROI = Expected Sub-Agent Value /
    (Agent Inference Cost + Context Duplication Cost + Aggregation Cost
     + Parent Ingestion Cost + Latency + Coordination Overhead)
```

restated exactly from `architecture.md` §18.3 / `conventions.md` §12.3. Step 1 of the algorithm, required by `EC-047`, is a `completed_actions_check`: if the delegated objective already appears in `AgentState.completed_actions`, `spawn = false, reason = ALREADY_COMPLETED` — computed before the ROI estimate is even attempted, since a completed task's expected value is definitionally zero. Step 2 computes `Expected Sub-Agent Value` via one or more of: information-gain-based (how much of the parent's remaining uncertainty the sub-agent's finding would resolve), task-decomposition-based (is the delegated objective a genuinely separable subtask), or independent-verification-based (does an independent agent re-deriving the same conclusion increase confidence). All three are `PROPOSED METHODOLOGY` — the source establishes the ROI formula's structure and cost components, not the value-estimation function itself.
**Formula/Estimation Method:** As above; `Sub-Agent ROI` denominator components (`Agent Inference Cost`, `Context Duplication Cost`, `Aggregation Cost`, `Parent Ingestion Cost`, `Latency`, `Coordination Overhead`) map directly to `interfaces.md` §12.1's `SubAgentSpawnRequest`/`SubAgentSpawnDecision` fields (`value_estimate`, `cost_estimate`, `value_to_cost_ratio`) — restate those field names exactly, do not invent new ones.
**Proposed Defaults:** A minimum `value_to_cost_ratio` (`EC-048` uses `1.0` as its illustrative failing-to-configure example, not as an authoritative requirement) — any concrete threshold adopted here is `PROPOSED DEFAULT — VALIDATE LOCALLY`.
**Agent Memory Authority (H09):** The `completed_actions_check` step must read WVM's `completed_actions` (execution truth), not merely the parent agent's own in-context memory of what it has done — these can diverge (§0).
**Session-Phase Interaction:** `PROPOSED METHODOLOGY` — a `Discovery`-phase sub-agent spawn may tolerate a lower ROI floor than an `Implementation`-phase one, since exploratory value is harder to estimate upfront; not source-stated.
**Multi-Agent Interaction:** This is the primary spawn gate for the coordination protocols in §7 — a sub-agent that fails this gate is never scheduled (AL-004) or handed off to (AL-005).
**Anti-Pattern Check:** Directly guards against "spawning sub-agents without value/cost analysis" (`conventions.md` §24.2) and "re-running completed agent steps" (`conventions.md` §24.1).
**Fallback:** `spawn = false` → parent agent handles the task directly; no partial/best-effort spawn.
**Observability Metrics:** `agent.subagent_spawn_prevented.already_completed.count`, `agent.subagent_spawn_prevented.low_roi.count`, `agent.subagent_spawn.value_to_cost_ratio` (histogram).
**Validated By:** `SCN-AGENT-006` (Sub-Agent Spawned Without Value/Cost Analysis — Negative Control).
**Traceability:** PS §37 AL-003; ARCH §18.3; INTF-023 §12.1; CONV §12.3; EC-047, EC-048.

---

### AL-004 — Sub-Agent Scheduler

**Owning Architecture Section:** `architecture.md` §18.4.
**Concrete Algorithm:** For a set of sub-agent tasks that have each independently passed AL-003, identify which are safely parallelizable. Two tasks are treated as independent **only when all of the following hold**: (a) no shared mutable-state dependency between them, (b) no ordering dependency (task B does not require task A's output), (c) no unsafe shared non-idempotent side effect (e.g., both must not write the same file, call the same payment API, etc.), (d) required authorization exists for both, (e) required capabilities/feasibility tier (FTR) permit both, (f) concurrency limits and budget allow simultaneous execution, and (g) aggregation of both results remains feasible under the parent's context budget. Concurrency safety itself is delegated to **XEC** (Cross-Execution Coordinator) — this component decides *what to schedule*, XEC governs *how concurrent execution is made safe*; XEC's own mechanism is not redefined here.
**Formula/Estimation Method:** Independence check is a boolean AND over conditions (a)–(g) above; no numeric formula.
**Proposed Defaults:** A maximum fan-out (concurrently spawned sub-agents per parent) — no source states one; any value adopted is `PROPOSED DEFAULT — VALIDATE LOCALLY`, tracked under `SOURCE-GAP-AGENTOPT-02` (§14) as "unlimited fan-out" is an explicit failure mode to avoid (§13).
**Agent Memory Authority (H09):** N/A — not applicable to this entry; scheduling reads AL-003's output and XEC's concurrency state, not agent memory directly.
**Session-Phase Interaction:** N/A — not applicable to this entry.
**Multi-Agent Interaction:** This *is* the multi-agent coordination "parallel vs. sequential" decision referenced in §7.
**Anti-Pattern Check:** Guards against "unlimited sub-agent fan-out" and duplicated repository exploration across concurrently-scheduled sub-agents.
**Fallback:** If independence cannot be established, default to sequential execution rather than assuming safety.
**Observability Metrics:** `agent.subagents_scheduled_parallel.count`, `agent.subagents_scheduled_sequential.count`.
**Validated By:** No dedicated `SCN-AGENT-NNN` isolates AL-004 specifically; the closest scenarios are the sub-agent economics pair `SCN-AGENT-005`/`006` — flagged as thin coverage, not force-fit.
**Traceability:** PS §37 AL-004; ARCH §18.4; EC-047 (adjacent, re-derivation-avoidance principle).

---

### AL-005 — Sub-Agent Handoff Compression

**Owning Architecture Section:** `architecture.md` §18.5; `conventions.md` §12.4. **This is the most load-bearing entry in this document — root `CLAUDE.md` rule 8 and `conventions.md` §12.4 make full-transcript replay to a sub-agent unconditionally prohibited, with no efficiency exception.**
**Concrete Algorithm:** A completed sub-agent's raw working state (its own iteration history, tool results, intermediate reasoning) is never handed to the parent directly. It is compressed into a `SubAgentResultHandoff` (`interfaces.md` §12.2, restated verbatim below): `findings: SubAgentFinding[]` (typed `FACT | DECISION | CODE_CHANGE | ERROR | RECOMMENDATION`, each with `evidence_refs`, `confidence`, optional `locations: CodeLocation[]`), `unresolved_questions`, `recommended_actions`, `confidence`. The compression algorithm — how raw working state becomes a `SubAgentFinding[]` list — is `PROPOSED METHODOLOGY`: extract one finding per distinct fact/decision/error/recommendation the sub-agent produced, attach the evidence location(s) that support it, and compute `confidence` from the sub-agent's own verification signal (e.g., a test passing raises confidence in a `CODE_CHANGE` finding) where available, else leave as the sub-agent's self-reported estimate.

The `SubAgentSpawnRequest.delegated_context` (the *incoming* side, before the sub-agent even starts) already uses the identically-structured `SubAgentContextHandoff` (`interfaces.md` §12.1), which carries a `full_transcript_excluded: boolean` field the schema requires to be `true` — **not a policy option, a required field value.** `EC-049` is the canonical violation this guards against: a 45,000-token parent transcript passed to a sub-agent before any task-specific context is even added.
**Formula/Estimation Method:** `confidence` per finding — `PROPOSED METHODOLOGY`, no universal formula stated. `context_token_count` (an actual schema field) must be validated below the sub-agent's configured context budget before spawn.
**Proposed Defaults:** None — this component's correctness is binary (schema-compliant or not), not threshold-tunable.
**Agent Memory Authority (H09):** The handoff's `evidence_refs`/`locations` must trace to actual, verifiable sources (files, tool results, prior findings) — never to an unverified agent-memory claim presented as fact.
**Session-Phase Interaction:** N/A — not applicable to this entry; the handoff schema is identical regardless of session phase.
**Multi-Agent Interaction:** This is the return-path counterpart to AL-003's spawn-gate and AL-004's scheduling — together they form the full spawn→execute→return lifecycle.
**Anti-Pattern Check:** Directly guards against "full conversation replay to sub-agent" (`conventions.md` §24.3, `interfaces.md` §33's anti-pattern table: correct approach = use `SubAgentContextHandoff`/`SubAgentResultHandoff`).
**Fallback:** **There is no "pass the full transcript instead" fallback.** If compression cannot fit the handoff within budget: compress more aggressively first; if still over budget, reject the spawn/handoff with a structured error (`HANDOFF_TOO_LARGE`) — never bypass the schema. If a handoff is later found insufficient, the correct action is to **enrich the `SubAgentResultHandoff`** (spawn a follow-up query for the missing specific finding) — never to replay history.
**Observability Metrics:** `agent.subagent_handoff.transcript_excluded_violated.count` (alert if > 0), `da009.unresolved_questions_preserved`, `da009.handoff_tokens`.
**Validated By:** `SCN-AGENT-005` (Sub-Agent Handoff Uses Compressed Object, Never Full Transcript), `SCN-CMP-008`.
**Traceability:** PS §37 AL-005; ARCH §18.5; INTF-023 §12.1–12.2; CONV §12.4; EC-049, EC-074.

**Schemas (verbatim, `interfaces.md` §12.1–12.2):**

```
SubAgentContextHandoff {
  objective, key_findings: string[], evidence: ContextItem[], files_and_symbols: string[],
  confidence, decisions_made: string[], unresolved_questions: string[],
  recommended_next_actions: string[], context_token_count,
  full_transcript_excluded: boolean    // Must be true; AL-005: no full replay
}

SubAgentResultHandoff {
  spawn_id, child_agent_id, parent_task_id, status: enum { SUCCESS, PARTIAL, FAILED, CANCELLED },
  findings: SubAgentFinding[], unresolved_questions: string[], recommended_actions: string[],
  confidence,
  parent_cost, child_cost, duplicated_context_cost, aggregation_cost,
  coordination_latency_ms, total_cost, roi,
  child_tokens_consumed: LLMUsage, context_tokens_sent, result_tokens_returned
}
SubAgentFinding { finding_id, type: enum { FACT, DECISION, CODE_CHANGE, ERROR, RECOMMENDATION },
                   content, evidence_refs: string[], confidence, locations: CodeLocation[] | null }
```

---

### AL-006 — Agent Task Value / Early Exit

**Owning Architecture Section:** `architecture.md` §18.6, §13.1 (T3.1 Agent Stop Controller — the generic, non-agent-loop-specific version of the same decision); `conventions.md` §12.5.
**Concrete Algorithm:** Stop an agent when the objective is satisfied, required validation passes, additional information has low expected value, or additional model/tool calls are unlikely to change the outcome. `interfaces.md` §11.3's `AgentEarlyExitEvaluator.should_stop(report, policy) -> EarlyExitDecision` is the exact interface this algorithm implements — restated, not redefined. **Per `architecture.md` §47.7 (H10), the STOP decision consults the identical expected-value classifier AL-002 consults for the CONTINUE decision — one model, evaluated in opposite directions, not two independent mechanisms.** `EC-044` establishes the critical correctness requirement: for an objective with a clear boolean success criterion (e.g., "all tests passing"), early exit is only valid when that criterion is **verifiably true**, not merely when a completion-percentage metric crosses a threshold — a 95%-complete "fix all failing tests" task with 3 tests still failing must not exit. After any early exit, a completion-verification step (re-running the stated criterion) is required; if it fails, the agent resumes.
**Formula/Estimation Method:** `EarlyExitPolicy` fields (`min_quality_threshold`, `max_iterations`, `min_information_gain`, `min_state_change`, `max_no_progress_iterations`, `max_cost`) are the exact, interface-defined decision inputs (`interfaces.md` §11.3) — restated, not invented. The function mapping these to `EarlyExitDecision.reason` (`OBJECTIVE_SATISFIED | QUALITY_THRESHOLD_MET | NO_PROGRESS | MAX_ITERATIONS | BUDGET_EXHAUSTED | LOOP_DETECTED | POLICY_REQUIRED`) is `PROPOSED METHODOLOGY` for the boolean-criterion-verification step specifically (not interface-specified how verification is performed, only that it's required per `EC-044`).
**Proposed Defaults:** Concrete values for `min_quality_threshold`/`max_iterations`/etc. are task- and deployment-specific; any value proposed is `PROPOSED DEFAULT — VALIDATE LOCALLY`.
**Agent Memory Authority (H09):** `AgentState.completed_actions` (used to judge "is the objective satisfied") must reconcile against WVM's authoritative `completed_actions` before an exit decision is finalized — an agent's own belief that a step is done is not sufficient (§0).
**Session-Phase Interaction:** A `CL-007` `Completion` phase is, definitionally, where this component's STOP branch is expected to fire; an exit during `Discovery`/`Planning` is comparatively unusual and may warrant the classifier's "unclassified/continue" fallback (§3, AL-002) more often — `PROPOSED METHODOLOGY`.
**Multi-Agent Interaction:** Applies identically to a parent agent and to a sub-agent's own internal loop before its AL-005 handoff is produced.
**Anti-Pattern Check:** Guards against premature "success" reporting (`EC-044`) and against treating iteration-count exhaustion alone as evidence of completion.
**Fallback:** Completion-verification failure after exit → resume the agent, do not report a false success.
**Observability Metrics:** `agent.tokens_saved_by_exit`, `agent.steps_skipped`, `agent.early_exit_triggered.count`, `agent.early_exit_false_positive.count`.
**Validated By:** `SCN-AGENT-001` (Single Agent Normal Completion), `SCN-AGENT-009`, `SCN-CMP-039`, `SCN-CMP-053`.
**Traceability:** PS §37 AL-006; ARCH §13.1, §18.6, §47.7; INTF-022 §11.3; CONV §12.5; EC-044, EC-045.

---

### AR-001 — Marginal-Gain Model Routing (within the agent loop)

**Owning Architecture Section:** `architecture.md` §19.1. Cross-reference `TECH-013` (Model Routing) and `TECH-014` (Model Cascade/Escalation) in `optimization-catalog.md` — **this entry does not redefine either technique**; it specifies how the agent loop invokes them iteration-by-iteration.
**Concrete Algorithm:** `Marginal Model Value = Expected Quality Gain / Additional Cost`, re-evaluated **per iteration**, not only once at loop start. The agent loop re-runs this check specifically after a failed verification (AR-004), after a change in task state that raises quality requirements, or after a `CapabilityAvailabilityResolver` (CAR) availability/degradation event forces a candidate re-evaluation — escalating mid-loop only when marginal value is positive and quality constraints justify it.
**Formula/Estimation Method:** `Expected Quality Gain` estimation is `TECH-013`/`TECH-014`'s own concern (not redefined here); this entry's contribution is the *triggering condition* for re-evaluation (failed verification, state change, CAR event), which is `PROPOSED METHODOLOGY` at the trigger-list level — no source enumerates these three triggers exhaustively, though each is individually source-grounded (AR-004 for verification failure, CAR for availability change).
**Proposed Defaults:** None numeric.
**Agent Memory Authority (H09):** N/A — not applicable to this entry.
**Session-Phase Interaction:** `PROPOSED METHODOLOGY` — a `Discovery` phase may tolerate a lower-cost/lower-quality model more readily than `Implementation`/`Review`; not source-stated.
**Multi-Agent Interaction:** Applies independently within each sub-agent's own loop; a sub-agent's model selection is not inherited from the parent's.
**Anti-Pattern Check:** Guards against "always using the strongest/most expensive model" and "always using the cheapest model" (`conventions.md` §24.1) at the per-iteration granularity, not just at initial routing.
**Fallback:** If marginal value cannot be estimated, retain the currently-selected model rather than escalating speculatively.
**Observability Metrics:** `agent.model_escalations.count`, `agent.marginal_model_value` (histogram).
**Validated By:** `SCN-MODEL-002` (Router Must Not Route Safety-Critical Task to a Cheap/Incapable Model, per-iteration analog).
**Traceability:** PS §38 AR-001; ARCH §19.1; Optimization Catalog: TECH-013, TECH-014.

---

### AR-002 — Reasoning Budget Prediction (within the agent loop)

**Owning Architecture Section:** `architecture.md` §19.2. Cross-reference `TECH-015` (Reasoning Budget Control). **Root `CLAUDE.md` rule 3 governs this entry absolutely: never reduce reasoning budget solely to hit a token target. Any reasoning-budget reduction must be benchmarked against a quality baseline first (`conventions.md` §10.3).**
**Concrete Algorithm:** Predict required reasoning depth per iteration from task complexity, historical task outcomes (if available), tool requirements, verification requirements, model behavior, and user-defined quality tier. Start with a predicted budget; **escalate** (never silently truncate) when validation (AR-004's verifier signal, or a failed downstream check) indicates the prior budget was insufficient.
**Formula/Estimation Method:** The complexity→budget mapping is `PROPOSED METHODOLOGY` — no source gives a formula; the five listed input signals are source-stated (`architecture.md` §19.2), the function combining them is not.
**Proposed Defaults:** A starting reasoning-budget tier per task-complexity bucket — `PROPOSED DEFAULT — VALIDATE LOCALLY`, and explicitly **not** a substitute for the quality-baseline benchmark rule above, which applies regardless of which default is chosen.
**Agent Memory Authority (H09):** "Historical task outcomes" used as an input signal must be sourced from durable, provenance-tagged memory (DA-010, §6), not an unverified in-session claim.
**Session-Phase Interaction:** A `Discovery` phase plausibly warrants a different reasoning-budget default than `Implementation` — `PROPOSED METHODOLOGY`, not source-stated; candidate for `CL-007`'s per-phase policy set (§8).
**Multi-Agent Interaction:** Each sub-agent predicts its own reasoning budget independently; a parent does not impose its own budget on a sub-agent's internal reasoning.
**Anti-Pattern Check:** The specific, named anti-pattern this entry must never enable: "optimizing token count at the expense of correctness" (`conventions.md` §24.1) via a reasoning-budget cut with no quality check.
**Fallback:** If prediction is unavailable, use the model/provider's default reasoning configuration rather than guessing a reduced one.
**Observability Metrics:** `agent.reasoning_budget_predicted`, `agent.reasoning_budget_escalations.count`.
**Validated By:** No `SCN-AGENT-NNN` isolates this specifically at the per-iteration level; `TECH-015`'s own `Validated By` scenarios apply by extension — flagged thin, not force-fit.
**Traceability:** PS §38 AR-002; ARCH §19.2; CONV §10.3; Optimization Catalog: TECH-015.

---

### AR-003 — Output Budget Forecasting (within the agent loop)

**Owning Architecture Section:** `architecture.md` §19.3. Cross-reference `TECH-018` (Output Length Control).
**Concrete Algorithm:** Before each iteration's generation, forecast expected output length and set the smallest safe output budget, using the source-stated per-output-type policy table:

| Output type | Policy |
|---|---|
| Extraction | Minimal schema |
| Classification | Categorical |
| Coding | Code + bounded explanation |
| Summarization | Bounded length |
| Analysis | Structured, bounded |
| Tool/action responses | Machine-readable, minimal |

**Formula/Estimation Method:** Forecasting method (how "expected output length" is predicted before generation) is `PROPOSED METHODOLOGY` — the per-type policy table is source-stated, the length-prediction function is not.
**Proposed Defaults:** Numeric budget-per-type defaults — `PROPOSED DEFAULT — VALIDATE LOCALLY`; `EC-050`/`EC-051` are the canonical failure modes (schema truncates required information; code truncated mid-statement) any adopted default must be checked against.
**Agent Memory Authority (H09):** N/A — not applicable to this entry.
**Session-Phase Interaction:** N/A — not applicable to this entry; output-type policy is task-shape-driven, not phase-driven.
**Multi-Agent Interaction:** A sub-agent's `SubAgentFinding.content` (AL-005) is itself an output subject to the same per-type budgeting discipline.
**Anti-Pattern Check:** Guards against schema over-constraint that truncates required information (`EC-050`) and code truncation at non-semantic boundaries (`EC-051`) — both are named failure modes of exactly this component.
**Fallback:** If the forecast is under-confident, prefer a controlled-overflow allowance (per `EC-050`/`EC-051`'s expected behavior) over silent truncation.
**Observability Metrics:** `output.truncations.count`, `output.expansions.count`, `output.completeness_score` (histogram), `output.semantic_boundary_truncation.count`.
**Validated By:** `SCN-CMP-068` (output-length forecasting underestimates, truncating mid-structured-output on a failed-over model).
**Traceability:** PS §38 AR-003; ARCH §19.3; INTF-029 §17; CONV §15.1; EC-050, EC-051; Optimization Catalog: TECH-018.

---

### AR-004 — Verifier-Guided Escalation (within the agent loop)

**Owning Architecture Section:** `architecture.md` §19.4. Cross-reference `TECH-014` (Model Cascade/Escalation) and the **Verifier Calibration Layer (VCL)** — this entry does not redefine VCL's own calibration mechanism.
**Concrete Algorithm:** Where a deterministic or inexpensive verifier exists (unit tests, schema validation, type checking, static analysis, business rules, citation checks, format validation), run a lower-cost inference first, verify the output, and escalate/retry only on failure. This must be benchmarked against always using the stronger model (source-stated requirement, `architecture.md` §19.4) — an unbenchmarked assumption that verify-then-escalate is always cheaper is itself a violation of root `CLAUDE.md` rule 7 (research/assumed savings must be locally re-verified, not hard-coded as guaranteed).
**Formula/Estimation Method:** N/A beyond the verify→escalate-on-fail control flow itself, which is source-stated exactly.
**Proposed Defaults:** None — the decision (escalate or not) is binary on verifier outcome, not threshold-tunable at this layer.
**Agent Memory Authority (H09):** N/A — not applicable to this entry.
**Session-Phase Interaction:** N/A — not applicable to this entry.
**Multi-Agent Interaction:** A verifier failure that would otherwise trigger escalation may instead trigger a sub-agent spawn (AL-003) if independent re-verification, rather than a stronger model, is the better-value action — this composition choice is `PROPOSED METHODOLOGY`.
**Anti-Pattern Check:** **A verifier's pass/fail signal is not automatically a production guarantee of correctness beyond what the verifier itself checks** (root `CLAUDE.md` rule 7's agent-loop instantiation) — a passing unit-test verifier says nothing about un-tested behavior.
**Fallback:** If no verifier is available for a given output type, this component does not apply — proceed via AR-001's marginal-gain routing alone.
**Observability Metrics:** `verifier.pass_rate`, `verifier.escalations_triggered.count`, `verifier.acceptance_rate_drift` (cross-reference VCL's own `VERIFIER_DRIFT_DETECTED` event).
**Validated By:** `SCN-NOPT-003` (escalation correctly skipped when a deterministic verifier already passed) — cited from `optimization-catalog.md`'s `TECH-014` entry, not re-derived here.
**Traceability:** PS §38 AR-004; ARCH §19.4; CONV §7.10, §10.2, §15.5, §24.1; Optimization Catalog: TECH-014.

---

### DA-009 — Sub-Agent Result Compressor (concrete algorithm)

**Owning Architecture Section:** `architecture.md` §22 (schema); this document supplies the algorithm. **This module implements AL-005 (above) — they are the same mechanism at two documentation layers; the relationship is definitional, not merely "related."**
**Concrete Algorithm:** See AL-005 in full. `optimization-catalog.md`'s existing `DA-009` entry already states the module's Quality Risk precisely: "Dropping unresolved questions during compression (`EC-074`) — this is the single most severe failure mode for this module, since unresolved questions are what drive continuation." This document adds the concrete extraction step order: (1) extract all `DECISION`/`ERROR` findings first (highest continuation-relevance), (2) extract `unresolved_questions` verbatim from the sub-agent's own final state — never re-derived or summarized, since summarizing an open question risks silently closing it, (3) extract supporting `FACT`/`RECOMMENDATION` findings last, bounded by remaining handoff budget.
**Formula/Estimation Method:** `PROPOSED METHODOLOGY` (extraction ordering above).
**Proposed Defaults:** None.
**Agent Memory Authority (H09):** N/A beyond AL-005's own statement.
**Session-Phase Interaction:** N/A — not applicable to this entry.
**Multi-Agent Interaction:** See AL-005.
**Anti-Pattern Check:** Same as AL-005.
**Fallback:** Same as AL-005 — retry compression or escalate/abort the handoff; never replay full history.
**Observability Metrics:** `da009.unresolved_questions_preserved`, `da009.handoff_tokens`.
**Validated By:** `SCN-AGENT-005`, `SCN-CMP-008`.
**Traceability:** Optimization Catalog: DA-009 (full schema entry, not restated here); PS §37 AL-005; ARCH §18.5, §22; INTF-023 §12; CONV §12.4; EC-049, EC-074.

---

### DA-010 — Agent Memory Optimizer (concrete algorithm)

**Owning Architecture Section:** `architecture.md` §22 (schema), §15.6 (CL-006 memory layers), §47.3.1 (H09). This document supplies the algorithm, subordinate throughout to §0's Memory Authority principle.
**Concrete Algorithm:** Separates short-term task state (TURN, TASK layers per CL-006) from durable project/task memory (SESSION, PROJECT, ORGANIZATION, HISTORICAL layers). The retention algorithm per layer: TURN-layer entries are retained only for the current session lifetime and are the first candidates for eviction under budget pressure; PROJECT/ORGANIZATION-layer entries persist across sessions and require an explicit retention policy (deferred to data-governance configuration, not invented here). Before any memory-derived value is used in a decision affecting execution truth, a `MemoryAuthorityCheck.check_conflict()` call is required (§0) — this is the concrete integration point where `optimization-catalog.md`'s `DA-010` entry's stated fallback ("on ambiguous provenance, default to treating the memory claim as stale/untrusted") is actually invoked.
**Formula/Estimation Method:** Per-layer retention/eviction priority is `PROPOSED METHODOLOGY`, grounded in CL-006's layer taxonomy (source-stated) but not a source-stated eviction order.
**Proposed Defaults:** TURN-layer TTL = session lifetime (this much is stated in `optimization-catalog.md`'s `DA-010` entry, "Appendix C"); other layers' concrete retention windows are `PROPOSED DEFAULT — VALIDATE LOCALLY` and deployment/data-governance-specific.
**Agent Memory Authority (H09):** This module's entire purpose is subordinate to H09 — restated in full at §0, not re-derived here.
**Session-Phase Interaction:** A `CL-007` phase transition is a plausible trigger for promoting TASK-layer memory to SESSION-layer (a phase's key decisions becoming durable for the rest of the session) — `PROPOSED METHODOLOGY`.
**Multi-Agent Interaction:** Composes with DA-018 (plan-specific memory) and DA-023 (Context Provenance Tracker, supplies the provenance tags this module's conflict-check depends on) — cited by ID, not redefined.
**Anti-Pattern Check:** "Allowing agent-owned working memory to override Control Plane execution state, authorization state, policy state, or context/workflow version" (`conventions.md` §24.4, H09).
**Fallback:** Ambiguous provenance → treat as stale/untrusted (verbatim, `conventions.md` §12.6).
**Observability Metrics:** `da010.memory_conflicts_detected`, `da010.layer_retention_compliance`.
**Validated By:** `SCN-AGENT-007` (Agent State Conflicts With Control Plane Execution State), `SCN-MEM-002`, `SCN-MEM-004`, `SCN-MEM-005`.
**Traceability:** Optimization Catalog: DA-010; PS §34 CL-006, §52.9; ARCH §15.6, §22, §47.3.1; INTF-028 §16, INTF §43.13; CONV §12.6; EC-118, EC-119, EC-120, EC-196, EC-197.

---

### DA-013 — Developer-Agent Context Budget Manager (concrete algorithm)

**Owning Architecture Section:** `architecture.md` §22 (schema); specializes `TECH-020` (Soft Reset/Context Budgeting) for coding agents. This document supplies the per-section allocation algorithm.
**Concrete Algorithm:** Implements the 13-section Context Priority Order (`conventions.md` §8.2): `SYSTEM → DEVELOPER_INSTRUCTIONS → COMPILER_ERRORS → SYMBOL_CONTEXT → GIT_DIFF → FILE_CONTENT → CONVERSATION → REPOSITORY_MAP → TERMINAL_OUTPUT → MCP_TOOLS → MEMORY → TEST_OUTPUT → AGENT_STATE`. Allocation algorithm: (1) reserve `SYSTEM`'s full required size unconditionally (Tier 0, never pruned — same exclusion as `TECH-020`), (2) allocate each subsequent section its minimum-required size in priority order, (3) if `sum(min_tokens) > total_budget` after minimums, apply `EC-055`'s expected behavior — reduce lower-priority sections proportionally, and if even minimum allocations cannot fit, return `BUDGET_OVERFLOW` with a per-section breakdown rather than silently truncating any single section.
**Formula/Estimation Method:** Proportional reduction below priority-order minimums is `PROPOSED METHODOLOGY` (`EC-055` states the *requirement* — no silent truncation — not the exact proportional-reduction formula).
**Proposed Defaults:** Per-section minimum-token floors — `PROPOSED DEFAULT — VALIDATE LOCALLY`, task- and repository-size-dependent.
**Agent Memory Authority (H09):** The `MEMORY` and `AGENT_STATE` sections in this priority order are themselves subject to DA-010's Memory Authority discipline — this allocator does not grant memory sections authority merely by budgeting space for them.
**Session-Phase Interaction:** A `CL-007` phase plausibly shifts relative section pressure (e.g., `TEST_OUTPUT` dominates during `Testing`, `GIT_DIFF` during `Implementation`) — `PROPOSED METHODOLOGY`.
**Multi-Agent Interaction:** Orchestrates DA-001 (Code-Aware Context Pruner), DA-002 (Symbol-Level Context Selector), DA-004 (Git-Diff Optimizer), DA-005 (Terminal Output Optimizer), DA-016 (File-Section Lazy Loader) as constituent per-section allocators — cited by ID.
**Anti-Pattern Check:** Same as `TECH-020` — guards against silent truncation of any section under budget pressure.
**Fallback:** Same tiered eviction protocol as `TECH-020`; `BUDGET_OVERFLOW` with details if minimums cannot fit.
**Observability Metrics:** `da013.section_budget_used` (per section, 13 dimensions), `budget.conflict.count`, `budget.overflow.count`.
**Validated By:** `SCN-CMP-007` (budget allocation conflict between context sections), `SCN-CMP-013` (budget exhaustion at multiple sections simultaneously).
**Traceability:** Optimization Catalog: DA-013; ARCH §22; CONV §8.2; EC-055; Optimization Catalog: TECH-020.

---

### DA-014 — Agent Loop Cost / Token Controller (concrete algorithm)

**Owning Architecture Section:** `architecture.md` §22 (schema), §47.7 (shared expected-value model with T3.1/AL-006). Specializes `TECH-016` (Agent Early Exit) for coding agents. **This module is AL-002's loop-waste detection, implemented as a running, cumulative controller — the relationship is definitional, not merely "related."**
**Concrete Algorithm:** Consumes AL-001's per-iteration signals and WVM's `completed_actions`/`unresolved_questions`. Maintains a running cumulative-cost tally (`cumulative_cost` from `AgentIterationReport`) against a budget ceiling; on each iteration, consults the *same* AL-002 classifier (productive/reflection/justified-retry/redundant/oscillation/failure) before considering a stop — **it must never stop a loop the classifier judges to be making genuine progress** (`EC-075`'s exact failure mode: stopping a loop that is actually making progress, named as "the single most important failure mode to avoid for this module" in `optimization-catalog.md`'s existing `DA-014` entry). If the classifier cannot produce a confident category, the fallback (§47.7, verbatim) is to continue under existing SGE/SPC budget constraints — not to force-stop for cost reasons alone.
**Formula/Estimation Method:** Same classifier as AL-002 (§2); no separate formula.
**Proposed Defaults:** A cumulative-cost ceiling that triggers stop-consideration (not automatic stop) — `PROPOSED DEFAULT — VALIDATE LOCALLY`.
**Agent Memory Authority (H09):** Reads WVM's `completed_actions` (execution truth), not the agent's own claimed completion state.
**Session-Phase Interaction:** `PROPOSED METHODOLOGY` — cost tolerance plausibly differs by phase (more tolerance for `Discovery` exploration cost than for repeated `Implementation` cost).
**Multi-Agent Interaction:** N/A — not applicable to this entry; this controller is single-agent-loop-scoped. A parent's own cost controller does not directly govern a sub-agent's (the sub-agent has its own instance).
**Anti-Pattern Check:** "Repeated iteration is not inherently waste — the classifier targets expected-value-negative loops, never iteration count as an end in itself" (H10, `conventions.md` §12.2) — restated here because this is the component most tempted to encode a raw iteration/cost cap.
**Fallback:** Unclassifiable iteration → continue under existing SGE/SPC constraints (§47.7).
**Observability Metrics:** `da014.loops_stopped`, `da014.loops_stopped_incorrectly` (regression signal — must trend toward 0).
**Validated By:** `SCN-AGENT-009`, `SCN-CODE-010`, `SCN-CMP-009`.
**Traceability:** Optimization Catalog: DA-014; PS §37 AL-001, AL-002, §52.10; ARCH §18.1–18.2, §22, §47.7; CONV §12.1–12.2; EC-045, EC-046, EC-075.

---

### DA-018 — Agent Plan Context Optimizer (concrete algorithm)

**Owning Architecture Section:** `architecture.md` §22 (schema), §46.2.3 (WVM). This document supplies the retention algorithm.
**Concrete Algorithm:** Retains active plan state, decisions, milestones, and unresolved work instead of full planning history. Retention rule: an earlier planning decision is retained in full (not summarized away) if and only if it remains an active, unresolved dependency for the current step — `optimization-catalog.md`'s existing `DA-018` entry states this exactly as the "Explicitly Not Applicable When" boundary. Concretely: (1) walk the plan's decision log, (2) for each decision, check whether any pending/future step depends on it (via WVM's dependency tracking, not this module's own guess), (3) retain dependency-linked decisions verbatim; summarize or drop only decisions with no forward dependency. A workflow mutation between planning and action (`EC-086`) invalidates a completed step under the new `workflow_version` — this module's retained plan snapshot must reconcile against the new version, never silently persist the stale one.
**Formula/Estimation Method:** Dependency-linkage check via WVM is source-referenced (not invented); the summarization method for non-dependency-linked decisions is `PROPOSED METHODOLOGY`.
**Proposed Defaults:** None numeric.
**Agent Memory Authority (H09):** Plan state is itself a memory layer (TASK/SESSION per CL-006, per `optimization-catalog.md`'s `DA-018` composition note) — subject to DA-010's H09 discipline, not a separate authority.
**Session-Phase Interaction:** A `CL-007` phase transition is a natural plan-milestone boundary — the plan's milestone list plausibly gets a checkpoint entry at each phase transition; `PROPOSED METHODOLOGY`.
**Multi-Agent Interaction:** A delegated sub-agent's own plan (if any) is not inherited from the parent's plan context; only the `SubAgentContextHandoff.decisions_made` subset relevant to the delegated objective is passed (AL-005).
**Anti-Pattern Check:** "Carrying complete conversation history indefinitely" (`conventions.md` §24.1), applied to plan/decision history specifically.
**Fallback:** Retain full planning history if summarization fails (revert to unoptimized representation) rather than dropping decisions unrecoverably.
**Observability Metrics:** `da018.plan_tokens_retained`, `da018.milestones_preserved`.
**Validated By:** `SCN-WF-002` (workflow mutation mid-execution, step added), `SCN-CMP-021` (newly added mandatory step discovered only after resume).
**Traceability:** Optimization Catalog: DA-018; ARCH §22, §46.2.3; EC-086.

---

### DA-020 — Developer-Agent Output Controller (concrete algorithm)

**Owning Architecture Section:** `architecture.md` §22 (schema); specializes `TECH-017`/`TECH-018` for coding agents. Extends AR-003 (above) to the developer-agent case.
**Concrete Algorithm:** Bounds verbose explanations, patches, summaries, and machine-readable outputs using AR-003's per-output-type policy table, with one coding-specific hard rule: **a patch/diff must never be truncated at a non-semantic (mid-statement) boundary** — same requirement as `EC-051`, restated for `DA-020` since a truncated patch is not merely incomplete prose, it is invalid code that could be mistakenly applied. Concretely: truncation candidates for `CODE`-typed output are evaluated against syntactic boundaries (function end, class end, statement end) before any token-count limit is applied; if a semantic-boundary-respecting truncation would slightly exceed the configured limit, controlled overflow (up to a configurable margin) is preferred over a syntactically broken result; if it still cannot fit, the result is returned as `OUTPUT_TRUNCATED` with the structurally complete partial patch, never silently presented as complete.
**Formula/Estimation Method:** Semantic-boundary detection method is `PROPOSED METHODOLOGY` (language-specific parsing/AST-boundary detection, not source-specified in mechanism).
**Proposed Defaults:** The "configurable margin" for controlled overflow — `PROPOSED DEFAULT — VALIDATE LOCALLY`.
**Agent Memory Authority (H09):** N/A — not applicable to this entry.
**Session-Phase Interaction:** N/A — not applicable to this entry.
**Multi-Agent Interaction:** Applied after DA-019 (Code Edit Context Optimizer) produces patch content, per `optimization-catalog.md`'s existing composition note.
**Anti-Pattern Check:** Same as `TECH-018`, plus: "a truncated patch must never be presented as if it were complete and valid" (`optimization-catalog.md`'s existing `DA-020` entry, restated).
**Fallback:** `OUTPUT_TRUNCATED` status with structurally complete partial result, never a broken code block.
**Observability Metrics:** `output.semantic_boundary_truncation.count`, `output.mid_statement_truncation_prevented.count`.
**Validated By:** `SCN-CMP-068`.
**Traceability:** Optimization Catalog: DA-020; EC-051; Optimization Catalog: TECH-017, TECH-018.

---

### DA-021 — Agent Task Completion / Early-Exit Controller (concrete algorithm)

**Owning Architecture Section:** `architecture.md` §22 (schema); the coding-agent-specific instantiation of AL-006/T3.1 above — **not an unrelated mechanism.** Specializes `TECH-016`.
**Concrete Algorithm:** Applies AL-006's exact algorithm with a coding-specific completion signal: test-pass rate and patch-correctness check, in place of a generic "objective satisfied" judgment. Per `optimization-catalog.md`'s existing `DA-021` entry, this must never stop before all required tests have actually run — "code generated" is explicitly not equivalent to "task complete" (a code-specific instance of `EC-044`'s boolean-criterion-verification requirement).
**Formula/Estimation Method:** Same as AL-006; the coding-specific verification step is: re-run the full required test suite (not a sampled subset) before finalizing a STOP decision.
**Proposed Defaults:** None beyond AL-006's.
**Agent Memory Authority (H09):** Same as AL-006 — WVM's `completed_actions`/test-run records are authoritative over the agent's own belief that tests passed.
**Session-Phase Interaction:** This is the `CL-007` `Completion` phase's primary gate.
**Multi-Agent Interaction:** Shares its expected-value model with DA-014 (per `optimization-catalog.md`'s existing composition note) — one model, not two.
**Anti-Pattern Check:** Same as `TECH-016`/AL-006.
**Fallback:** Same as AL-006 — verification failure after exit → resume.
**Observability Metrics:** Same as `TECH-016` with a `da021.` prefix, plus `da021.tests_passed_at_exit`.
**Validated By:** `SCN-CODE-010` (coding agent's reasoning-tool-reasoning loop exits on no-progress across iterations).
**Traceability:** Optimization Catalog: DA-021; EC-044, EC-045; Optimization Catalog: TECH-016.

---

## 3. Tool-Execution Optimization as Consumed by the Agent Loop

Cross-references only — none of the following are redefined here; each is `optimization-catalog.md`'s or `architecture.md`'s own:

- **TE-001 — Tool Call ROI Predictor** (`architecture.md` §17.1, `conventions.md` §11.1): `Tool ROI = Expected Information Gain / (Tool Cost + Token Cost + Latency Cost)`. Invoked by the agent loop **before** AL-003's sub-agent-value check when a tool call (not a sub-agent spawn) is the candidate action — both are ROI gates on the same "should the loop take this action" decision, evaluated against different action types.
- **TE-005 — Parallel Tool Execution** (`conventions.md` §11.4): independent tool calls execute concurrently under the same independence conditions AL-004 (§2) applies to sub-agents — no inter-call data dependency, safe side effects, policy permits it.
- **TE-006 — Tool Result Filtering** (`conventions.md` §11.3): never automatically propagate a full tool result; retain only fields required for task completion, evidence/provenance, and required authorization/audit fields (`SEC-006` — filtering must never remove auth/audit fields).
- **DA-007 — Dynamic MCP/Tool Selector** and **DA-008 — Tool Schema Optimizer** (`optimization-catalog.md`, full schema there): invoked by the agent loop at the point where a candidate tool call is identified, before TE-001's ROI check — a tool that isn't selected/exposed for the current task context never reaches the ROI gate at all.

The agent loop's own contribution here is sequencing: **tool/sub-agent selection (DA-007/DA-008) → ROI gate (TE-001/AL-003) → parallel-safety check (TE-005/AL-004) → execution → result filtering (TE-006) → AL-001 progress measurement.** This ordering is `PROPOSED METHODOLOGY` — no single source states the full six-step sequence, though each pairwise adjacency is individually source-grounded.

---

## 4. MCP Optimization

MCP (Model Context Protocol) tools are treated identically to other tools for ROI/selection purposes (§3), with one addition: **the Tool/MCP Trust Gate (TMG) takes precedence over optimization for every MCP interaction** — a favorable ROI or cache-hit never substitutes for TMG's identity/schema-staleness/authorization check (root `CLAUDE.md`'s non-negotiable rules, TMG's own charter). This document does not redefine TMG. Concretely, the agent loop's MCP-specific sequence extends §3's with a TMG check immediately after DA-007's selection and before TE-001's ROI gate: **selection → TMG trust check → ROI gate → execution → TE-006 filtering.** MCP capability/version changes (a server's tool schema changing between calls) invalidate any cached tool-selection decision and force re-selection — this is `SRP`'s (Stale Result Protection) concern at the cache layer (`cache-strategy.md`), cited not redefined.

---

## 5. Multi-Agent / Sub-Agent Coordination

- **Parallel vs. sequential:** governed by AL-004 (§2) — no separate mechanism.
- **Shared-context minimization:** a sub-agent receives only what `SubAgentSpawnRequest.delegated_context` (a `SubAgentContextHandoff`, §2 AL-005) carries — never the parent's full context. Tenant/context isolation between concurrently-running sub-agents follows the same tenant-scoping rules `cache-strategy.md` establishes for cache keys (cited, not restated).
- **Result aggregation:** `interfaces.md` §12.3's `SubAgentAggregator` interface is the exact contract:
  ```
  interface SubAgentAggregator {
    aggregate(results: SubAgentResultHandoff[]) -> AggregatedResult
    compress_for_parent(result: AggregatedResult, budget: integer) -> CompressedAgentResult
  }
  ```
  restated verbatim, not redefined. This document's contribution is the aggregation *policy* for conflicting findings (below), not the interface itself.
- **Conflicting sub-agent findings:** when two sub-agents' `SubAgentFinding`s disagree (e.g., two `DECISION` findings contradict), the aggregator does not silently pick one — it surfaces the conflict as an `unresolved_question` in the `AggregatedResult`, deferring resolution to the parent's own verification step (AR-004) or to a human-approval gate (HAG) if the conflict concerns a consequential decision. `PROPOSED METHODOLOGY` — no source states this exact conflict-surfacing behavior, though it follows directly from §0's "surface, never silently resolve" principle applied to sub-agent findings instead of memory claims.
- **Partial failure:** a `SubAgentResultHandoff.status = FAILED | PARTIAL | CANCELLED` is handled by the parent's own AL-006 early-exit evaluation of *its own* loop — a failed sub-agent does not automatically fail the parent task; the parent re-evaluates whether the delegated objective's absence changes its own completion criteria.
- **Budget allocation across concurrent sub-agents:** `PROPOSED METHODOLOGY` — partition the parent's remaining budget proportionally to each scheduled sub-agent's AL-003 `expected_value`, with a reserved floor per sub-agent to avoid starving a low-but-positive-ROI task entirely. Interacts with **SGE** (Spend Governance Engine) for the actual budget-ceiling enforcement — SGE's own mechanism is not redefined here; this entry supplies SGE's allocation input, not its enforcement logic.
- **Cancellation and supersession:** when a parent execution is superseded (a new request replaces the in-flight one), in-flight sub-agents must not continue as if the superseded execution remained authoritative. **SPM** (Supersession Manager) issues the supersession record; **XEC** (Cross-Execution Coordinator) ensures in-flight sub-agents observe it before completing any further non-idempotent action. Neither mechanism is redefined here — this document states only that a sub-agent's AL-001 iteration loop must check supersession status (via XEC) at each iteration boundary, not only at spawn time.

---

## 6. Coding / Developer Agents — First-Class Requirement

Per root `CLAUDE.md`, developer/coding agents are a first-class execution environment (Environment C), not an optional specialization. This section makes explicit what §2's `DA-009/010/013/014/018/020/021` entries already establish plus the remaining first-class requirements:

- **Repository/worktree state reconciliation:** every coding-agent optimization decision (DA-013's budgeting, DA-016's lazy loading, DA-019's edit scoping) depends on the *current* repository version, branch, commit, and uncommitted-working-tree state — a decision made against a prior commit is stale the moment the repository changes, and must be re-validated via DA-024 (Context Invalidation Engine, cited not redefined) before reuse.
- **Build/test state:** DA-006 (Compiler/Test/Linter Error Extractor) and DA-012 (Error-Driven Context Retrieval) feed AL-001's `information_gained` signal directly — a build/test run is itself an information-gathering "tool call" subject to TE-001's ROI gate (§3), not a free action.
- **Never blindly reuse a coding-agent result against changed repository state:** this is `SRP`'s cache-layer concern for the repository-map/tool-result caches DA-022 defines (cited from `optimization-catalog.md`, not redefined) — the agent-loop-level consequence is that DA-021's early-exit test-verification step (§2) must re-run against current repository state, never a cached prior test result.
- **CI/CD and IDE/CLI integration surfaces:** bounded by the platform's declared **FTR** (Feasibility Tier Registry) tier and `reachable_modules` set (`provider-matrix.md`, cited not redefined) — a Tier 4/5 (`PROTOCOL_TOOL_LEVEL`/`ADVISORY_OBSERVABILITY_ONLY`) integration cannot claim the full DA-001–025 pipeline coverage this document assumes for a Tier 1/2 integration; `provider-matrix.md` §"FTR Tier Assignments" is authoritative for which modules a given platform can actually reach.

---

## 7. Agentic RAG Optimization

Retrieval within an agent loop is a tool call subject to §3's sequencing (selection → ROI gate → execution → filtering), specialized for retrieval: query decomposition and multi-hop retrieval are themselves loop iterations subject to AL-001/AL-002 (a retrieval loop that repeats the same query with no new results is exactly AL-002's loop-waste case, `EC-045`'s mechanism, applied to retrieval instead of a generic tool). Retrieval deduplication, ranking, and compression are `TECH-004`/`005`/`006`/`007`'s concern (`optimization-catalog.md`, cited not redefined) — this document's contribution is only the stopping criterion: retrieval continues while TE-001's ROI for "one more retrieval call" remains positive, and stops (not merely pauses) once marginal information gain (AL-001's metric, §2) falls below the configured floor. **Evidence must not be discarded solely because it consumes tokens** — this restates root `CLAUDE.md`'s security-classified-content protection in retrieval-specific terms: a low-ROI retrieval call is skipped, but an already-retrieved, task-relevant citation is never dropped purely for token economy once admitted (`cache-strategy.md`'s Tier 0/1 context-criticality model applies identically here, cited not redefined).

---

## 8. Session-Phase Policy (CL-007)

**Session phase is source-defined — it is not this document's invention.** `architecture.md` §15.7 (`CL-007 — Session Phase Management`, also PS §CL-007, Engineering Spec §9.7, `conventions.md` line in the Layer 1 summary table) establishes exactly six named phases for long-running tasks:

| Phase |
|---|
| Discovery |
| Planning |
| Implementation |
| Testing |
| Review |
| Completion |

The source states, verbatim in substance: "Each phase may use different context, tool, model, reasoning, and memory policies" — **the phase taxonomy is authoritative; the concrete per-phase policy values are not source-specified.** `CL-007` is architecturally filed under Context Lifecycle (`architecture.md` §15, Layer 2), not under Agent Loop (§18) — its concrete policy defaults are elaborated here specifically because `optimization-catalog.md`'s own Scope Boundary defers "session-phase policy defaults" to `agent-optimization.md`, not because CL-007 is architecturally an agent-loop component. This is stated explicitly to avoid mis-citing CL-007's architectural home.

**Concrete per-phase policy proposal** (every cell below is `PROPOSED DEFAULT — VALIDATE LOCALLY BEFORE PRODUCTION USE`; the source gives no per-phase values):

| Phase | Context emphasis | Tool policy | Model/reasoning policy | Memory policy |
|---|---|---|---|---|
| Discovery | Broad retrieval tolerated; lower marginal-gain floor (§1) | Exploratory tool calls favored; TE-001 ROI floor lower | Lower-cost model default (AR-001); reasoning budget conservative-start | TASK-layer writes frequent; low promotion to SESSION |
| Planning | Plan/decision context (DA-018) prioritized | Minimal tool use; planning is largely reasoning-only | Escalate (AR-001) if plan complexity is high | Decisions recorded as `key_decisions`, candidates for SESSION promotion |
| Implementation | `GIT_DIFF`/`FILE_CONTENT`/`SYMBOL_CONTEXT` prioritized (DA-013) | Code-edit tools (DA-019) favored; parallel tool execution (TE-005) more common | Verifier-guided escalation (AR-004) most active here | Plan state (DA-018) actively consulted |
| Testing | `TEST_OUTPUT`/`COMPILER_ERRORS` prioritized (DA-013) | Test/build tools dominant | Verifier-guided escalation (AR-004) primary quality gate | Test-result history retained for regression comparison |
| Review | Full-diff/full-context tolerance higher | Read-only inspection tools favored | Highest reasoning-budget tolerance (AR-002) for correctness review | Decision log surfaced for review, not re-derived |
| Completion | Minimal — only verification-relevant context | Verification-only tool calls (test re-run, DA-021) | AL-006/DA-021 early-exit gate is primary | SESSION-layer memory finalized/promoted per DA-010 retention policy |

This table is this document's own engineering proposal, not an architectural requirement, and is recorded as `SOURCE-GAP-AGENTOPT-01` (§14) precisely because the *values* (not the phase names) are unsourced. A phase transition (per `SCN-CMP-069`) does not reset or invalidate recovery pointers from the prior phase — a recovered item is re-admitted under the *new* phase's policies, not the phase that pruned it (§14 gap does not affect this recovery-correctness requirement, which is source-stated).

---

## 9. Cache Integration

`cache-strategy.md` is authoritative for all seven canonical cache types (`EXACT`, `SEMANTIC`, `TOOL_RESULT`, `EMBEDDING`, `PROVIDER_NATIVE`, `APPLICATION`, `CONTEXT`), tenant scoping, invalidation, poisoning defenses, and the provider-native/EAIOC-cache boundary — none of that is redefined here. This document's only addition: the agent loop consults `TOOL_RESULT` cache (via TE-006's filtering pipeline, §3) and `CONTEXT` cache (via DA-013's budgeting, §2) as inputs to its own ROI/budget decisions, and a cache miss/stale-result event (`cache-strategy.md`'s `explain_miss()`/SRP integration) is itself an `AgentFailure` entry in that iteration's `AgentIterationReport` (§2, AL-001) — cache behavior is observable to the loop's own progress measurement, not a side channel invisible to it.

---

## 10. Provider / Model Optimization

`provider-matrix.md` is authoritative for the Provider Accessibility Model (`Discovered ≠ Accessible ≠ Available ≠ Authorized ≠ Recommended ≠ Eligible`), `CAR` failover data, and FTR tier assignments — none of that is redefined here. The agent loop's routing decisions (AR-001, §2) must never select a model/provider that `provider-matrix.md`'s accessibility facts mark as inaccessible, unauthorized, unavailable, or capability-incompatible for the current task (context capacity, tool support, structured output, reasoning requirements, coding capability) — this is a hard constraint on AR-001's candidate set, evaluated before the marginal-value formula is even computed, not a tiebreaker after.

---

## 11. Dynamic/Hardening Component Cross-Reference

### 11.1 Core Components (22)

Every one of the 22 dynamic-execution/hardening components established across this document chain (`architecture.md` §46/§47; identically counted in `scenario-matrix.md`, `optimization-catalog.md`, `provider-matrix.md`, `cache-strategy.md`) that this document's algorithms touch, with the specific touchpoint (none redefined):

| Component | Touchpoint in this document |
|---|---|
| ESM | §0 — execution-truth authority every H09 check reconciles against |
| CVM | §0 — context-version authority |
| WVM | §0, §2 (AL-001/003/006, DA-018) — `completed_actions`/`workflow_version` authority |
| CPM | §5, §12 — checkpoint records a sub-agent's/agent's state for recovery |
| Reconciliation Engine (RE) | §12 — resume-time reconciliation of agent/sub-agent state |
| CIG | §9 (indirectly, via cache-strategy.md's citation) |
| Context Expansion Controller (CEC) | §2 (DA-016/DA-019 fallback: expand context on insufficiency) |
| PRV | §12 — permission revalidation on resume |
| DPE | §12 — policy revalidation on resume |
| CAR | §2 (AR-001's third trigger), §10 |
| SRP | §9 — stale tool-result/cache data feeding AL-001 |
| SPM | §5 — supersession of in-flight sub-agents |
| RCO | §12 — recovery coordination |
| SGE | §5 — sub-agent budget allocation input |
| DGE | §6 (data-classification propagation through DA-009/010's handoffs, cited not redefined) |
| TMG | §4 — MCP trust precedence |
| HAG | §5 — consequential-conflict escalation |
| CIS | Cited via `cache-strategy.md`/`provider-matrix.md`; not independently touched by an agent-loop-specific mechanism in this document |
| FTR | §6, §10 — coding-agent integration tier bounding |
| XEC | §2 (AL-004), §5 — concurrency safety and supersession observation |
| SPC | §2 (AL-002/DA-014's fallback references SPC budget constraints) |
| VCL | §2 (AR-004) — verifier calibration, cited not redefined |

### 11.2 Supporting/Control Components (3) — called out separately for agent-loop/multi-agent relevance

`XEC`, `SPC`, and `VCL` — already among the 22 core components in §11.1 above — are additionally cross-listed here as a dedicated **supporting/control** tier, because each plays a distinct control-plane role specifically inside this document's agent-loop and multi-agent mechanisms rather than a general execution-truth or governance role:

| Component | Supporting/Control Role in This Document |
|---|---|
| XEC (Cross-Execution Coordinator) | The concurrency-safety authority AL-004's sub-agent scheduler and §5's supersession handling defer to — governs whether two agent-loop tasks may safely run in parallel |
| SPC (Self-Protection Controller) | The overload/self-protection authority AL-002's and DA-014's fallback paths defer to when the loop-waste/cost-controller's own inputs are unavailable |
| VCL (Verifier Calibration Layer) | The calibration authority AR-004's verifier-guided escalation defers to for interpreting a verifier's pass/fail signal, per root `CLAUDE.md` rule 7 (a verifier result is not automatically a production guarantee) |

**Note on the 22/25 figures:** this section lists **25 total rows (22 core + 3 supporting/control)**, per this document's own presentation choice to give XEC/SPC/VCL a second, role-specific entry. This is a *row count*, not a claim of 25 distinct architecture components — the authoritative component total remains **22** (13 Dynamic Execution + 9 Hardening, per `architecture.md` §46/§47), unchanged and consistent with every other document in this chain. XEC, SPC, and VCL are simultaneously 3 of the 22 and the 3 components singled out in §11.2; they are not additional components beyond the established 22.

---

## 12. Security / Governance Precedence

Restated exactly as established elsewhere in this chain (`conventions.md` §13, `provider-matrix.md`, `cache-strategy.md`), not re-derived:

```
Security / Authorization / Policy / Governance
> Correctness / Safety
> Required Quality
> Execution Integrity
> Optimization
> Cost / Token Reduction
```

No algorithm in this document may: bypass authorization; cross tenants; select an unauthorized/inaccessible model, provider, or tool; expose restricted context via a sub-agent handoff; ignore a policy change mid-loop; remove mandatory evidence from a handoff or output; reuse stale authorized state (a permission revoked mid-loop invalidates any decision made under the prior grant, §0); propagate poisoned content through a cache-assisted decision (§9); bypass a Human Approval Gate for a consequential action a loop is working toward (DA-014's explicit constraint, §2); or disable a required verifier (AR-004) to save cost.

---

## 13. Negative Optimization

`DO_NOT_OPTIMIZE` applies to any agent-loop/sub-agent decision when: optimization overhead exceeds expected benefit (an AL-003 sub-agent spawn whose coordination overhead exceeds its expected value, §2); quality risk is unacceptable (AR-002's reasoning-budget-cut-without-baseline case, §2); execution risk increases (a parallelization that would violate AL-004's independence conditions, §2); required context would be lost (DA-013's `BUDGET_OVERFLOW` case rather than silent truncation, §2); policy/authorization is insufficient (§12); data governance prohibits reuse (§9's cache citation); capability/feasibility is unavailable (FTR tier bound, §6/§10); state is stale (a memory claim H09 has marked untrusted, §0); optimization would make recovery harder (a compression that discards recovery-required provenance, contrary to `cache-strategy.md`'s Tier 0/1 protection); or the action is economically unjustified by the loop's own ROI gates (TE-001/AL-003, §2–3).

---

## 14. SOURCE GAPS DISCOVERED

No gap from `optimization-catalog.md`, `provider-matrix.md`, or `cache-strategy.md` is re-numbered here; only genuinely new gaps discovered while writing this document receive a new ID.

| Gap ID | Affected Entry | Missing Information | Why It Matters | Authoritative Source That Should Define It | Recommended Disposition | Blocking? |
|---|---|---|---|---|---|---|
| `SOURCE-GAP-AGENTOPT-01` | §8 (CL-007 Session-Phase Policy table) | `architecture.md` §15.7 names six session phases and states they "may use different context, tool, model, reasoning, and memory policies," but specifies no concrete per-phase values | Without this, the §8 table is this document's own engineering proposal, not an authoritative default — a build-sequencing decision for per-phase tuning has no source to validate against beyond this document | A future revision of `architecture.md` §15.7, or `implementation-plan.md` | Non-blocking — the proposal is explicitly labeled `PROPOSED DEFAULT`, not presented as authoritative | No |
| `SOURCE-GAP-AGENTOPT-02` | AL-004 (§2), §5 (Multi-Agent budget allocation) | No document specifies a maximum sub-agent fan-out or a budget-partitioning formula across concurrently-spawned sub-agents | Unlimited fan-out is a named failure mode (this document's own anti-pattern list, §12/§13's ROI discipline notwithstanding) with no authoritative ceiling to enforce | `architecture.md` §18.4 (a future revision) or SGE's own specification | Non-blocking — AL-003's per-sub-agent ROI gate provides a partial mitigation even without a fan-out ceiling | No |
| `SOURCE-GAP-AGENTOPT-03` | §5 (conflicting sub-agent findings) | No document specifies how `SubAgentAggregator.aggregate()` should behave when two `SubAgentFinding`s directly contradict | Silent resolution in either direction risks losing a genuine safety-relevant disagreement between independent findings | `interfaces.md` §12.3 (a future revision) | Non-blocking — this document's proposed "surface as `unresolved_question`" behavior is a conservative, non-lossy default consistent with §0's general principle | No |

---

## 15. SOURCE CONTRADICTIONS

No contradiction was found between the Problem Statement, Engineering Specification, `architecture.md`, `interfaces.md`, `conventions.md`, `edge-cases.md`, `scenario-matrix.md`, `optimization-catalog.md`, `provider-matrix.md`, or `cache-strategy.md` and this document's own content. Every apparent tension checked during drafting (e.g., AL-002's H10 correction vs. DA-014's cost-controller framing; CL-007's Context-Lifecycle architectural placement vs. its Agent-Optimization-deferred policy elaboration, §8) was resolved by an explicit precedence or scope-boundary rule already present in the source documents themselves, restated in this document rather than re-derived independently.

---

## 16. Scope Boundary

| Content | Owner |
|---|---|
| `TECH-NNN`/`DA-NNN` identity and catalog schema | `optimization-catalog.md` |
| Provider/model capability facts, FTR tier assignments | `provider-matrix.md` |
| Cache mechanics, key schemas, invalidation | `cache-strategy.md` |
| Full agent-loop/sub-agent-economics algorithms, session-phase defaults, multi-agent coordination | `agent-optimization.md` (this document) |
| Inference-infrastructure mechanism | `inference-optimization.md` (not yet generated) |
| Quality-gate methodology | `quality-gates.md` (not yet generated) |
| Security/CIS implementation mechanism | `security.md` (not yet generated) |
| Observability architecture | `observability.md` (not yet generated) |
| Evaluation framework | `eval.md` (not yet generated) |
| Scaling | `SCALING.md` (not yet generated) |
| Implementation sequencing | `implementation-plan.md` (not yet generated) |
| Requirements traceability | `requirements-traceability.md` (not yet generated — acknowledged as part of the planned chain, not created by this task) |
| Architectural decisions | ADRs (not yet generated) |

---

## 17. Required Matrices

**Agent Type Matrix**

| Agent Type | Optimization Opportunities | Risks | Dependencies |
|---|---|---|---|
| Generic/Autonomous | AL-001/002/006, AR-001–004, TE-001/005/006 | Loop waste misclassification (H10) | ESM/CVM/WVM (§0) |
| Tool-using | + DA-007/008, §4 MCP sequence | TMG bypass via favorable ROI | TMG |
| Coding/Developer | + DA-009/010/013/014/018/020/021, §6 | Stale repository-state reuse | DA-024, repository/build/test state |
| RAG | §7's retrieval-loop specialization of AL-001/002 | Discarding evidence for token economy | Context criticality tiers |
| Multi-agent/Sub-agent | AL-003/004/005, §5 | Full-transcript replay; unbounded fan-out (`SOURCE-GAP-AGENTOPT-02`) | SGE, SPM, XEC |

**Recovery Matrix**

| Trigger | Agent-Loop Response |
|---|---|
| Model/provider switch mid-loop (CAR) | AR-001 re-evaluation (§2); effective-budget recheck via `provider-matrix.md`/`cache-strategy.md` |
| Permission revocation mid-loop | §12 precedence — decision made under prior grant is invalidated |
| Workflow mutation mid-plan | DA-018 reconciliation against new `workflow_version` (§2) |
| Parent execution superseded | §5 — SPM/XEC-observed sub-agent halt |
| Checkpoint resume | §0/§12 — full PRV/DPE/CAR/WVM-consult/SRP/RE reconciliation before any loop iteration resumes; **resume != replay** |

Tool/Memory/Multi-Agent-Coordination/Provider-Model/Cache-Integration/Security-Governance matrices are covered inline in §3–§13 rather than as separate redundant tables, since each already cites the exact owning section.

---

## 18. Implementation-Neutral Pseudocode

Illustrative only — not an API specification; every named interface call is restated from the live `interfaces.md`, not invented.

```text
function agent_loop_iterate(task, state):
    reconcile(state, ESM, CVM, WVM)                         # §0 — mandatory, every iteration
    report = measure_progress(state)                        # AL-001, §2
    classification = classify_iteration(report, history)     # AL-002 classifier, §2 — never count/pattern-only
    if classification == UNCLASSIFIED:
        classification = CONTINUE_UNDER_BUDGET_CONSTRAINTS   # §47.7 fallback, verbatim

    exit_decision = AgentEarlyExitEvaluator.should_stop(report, policy)   # AL-006, §2
    if exit_decision.stop and requires_boolean_verification(task):
        if not verify_completion_criterion(task):             # EC-044 requirement
            exit_decision.stop = False

    if exit_decision.stop:
        return finalize(report, exit_decision)

    candidate = select_next_action(state)                     # tool, sub-agent, or model-only reasoning
    if candidate.type == TOOL_CALL:
        if not tmg_trust_check(candidate):                    # §4 — precedes ROI, no exception
            reject(candidate); return continue_loop(state)
        if tool_roi(candidate) < configured_floor:             # TE-001, §3 — PROPOSED DEFAULT floor
            skip(candidate)
        else:
            result = execute_tool(candidate)
            filtered = filter_tool_result(result)              # TE-006, §3 — never full propagation

    elif candidate.type == SUBAGENT_SPAWN:
        if candidate.objective in state.completed_actions:      # EC-047 — WVM-sourced, not agent memory
            reject(candidate, reason=ALREADY_COMPLETED)
        elif sub_agent_roi(candidate) < configured_floor:        # AL-003, §2 — PROPOSED DEFAULT floor
            reject(candidate, reason=LOW_ROI)
        elif not independent(candidate, in_flight_subagents):    # AL-004, §2
            schedule_sequential(candidate)
        else:
            handoff = SubAgentContextHandoff(candidate)           # AL-005, §2
            assert handoff.full_transcript_excluded == True        # non-negotiable, no exception
            spawn(candidate, handoff)

    elif candidate.type == MODEL_ESCALATION:
        if marginal_model_value(candidate) > 0:                  # AR-001, §2
            escalate(candidate)

    return continue_loop(state)


function on_subagent_complete(spawn_id, raw_state):
    result = compress_to_result_handoff(raw_state)              # DA-009/AL-005, §2 — never raw replay
    assert result.unresolved_questions == raw_state.unresolved_questions_verbatim
    aggregated = SubAgentAggregator.aggregate([result])          # §5
    if has_conflicting_findings(aggregated):
        aggregated.unresolved_questions.append(conflict_summary)  # §5 — surfaced, never silently resolved
    return SubAgentAggregator.compress_for_parent(aggregated, parent_budget)
```

---

## 19. Test Strategy

Covers unit, contract, integration, state-transition, authorization, policy, tool/MCP-trust, cache, memory, multi-agent, coding-agent, provider/model-routing, recovery, supersession, and performance/chaos categories, concretely instantiated per this document's own entries — no test below is claimed to have actually been executed by this document's generation, only specified:

- **State-transition:** verify a permission revocation mid-loop invalidates any pending decision made under the prior grant (§12).
- **Authorization/Policy:** verify AR-001 never selects a `provider-matrix.md`-flagged unauthorized/inaccessible model regardless of marginal-value score (§10).
- **Tool/MCP trust:** verify TMG rejection blocks a tool call before TE-001's ROI gate is even evaluated (§4).
- **Multi-agent:** verify `EC-047` (spawn blocked when objective already in `completed_actions`), `EC-048` (spawn blocked below ROI floor), `EC-049` (spawn/handoff rejected if `full_transcript_excluded != true`).
- **Coding-agent:** verify `EC-044`'s boolean-criterion re-verification fires before a "tests passing" early exit is finalized (DA-021, §2).
- **Recovery:** verify a checkpoint resume runs the full PRV→DPE→CAR→WVM-consult→SRP→RE sequence before any loop iteration continues (§0/§12), never a partial reconciliation.
- **Chaos/resilience:** verify a mid-loop supersession event (SPM) halts an in-flight sub-agent's next non-idempotent action, not merely its next spawn decision (§5).

---

## 20. Cross-Document Coverage Matrix

| Source Document | Relevance to This Document | Coverage |
|---|---|---|
| Problem Statement | AL-001–006, AR-001–004 named at PS §37/§38; CL-007 at PS's CL-007 section; DA-009/010/013/014/018/020/021 at PS §4 | Strong |
| Engineering Specification | Agent-loop-economics elaboration; CL-007 at ES §9.7 | Strong |
| Architecture | §13.1, §15.7, §18, §19, §22, §46.2.3/2.7, §47.3.1, §47.7 — the primary structural source for this entire document | Strong (primary source) |
| Interfaces | §11 (Agent Interface), §12 (Sub-Agent Interface), §43.13 (MemoryAuthorityCheck) — every schema this document restates comes from here | Strong |
| Conventions | §8.2, §10.3, §11, §12, §13, §24 — every anti-pattern and precedence rule cited | Strong |
| Edge Cases | EC-044–056, EC-074–075, EC-086, EC-118–120, EC-136, EC-196–197 individually reviewed and cited where materially relevant; not force-mapped | Strong, honest about which are thin (§2 notes several explicitly) |
| Scenario Matrix | `SCN-AGENT-001`–`009`, relevant `SCN-CMP-NNN`, `SCN-CODE-010/011/012`, `SCN-WF-002`, `SCN-MEM-002/004/005`, `SCN-MODEL-002` — all verified to exist before citing | Strong |
| Optimization Catalog | `TECH-013`–`018`/`020`, `DA-007`–`010`/`013`–`014`/`016`–`021`/`023`–`025` cited by ID throughout; none redefined | Strong, by design (this document elaborates what that one deferred) |
| Provider Matrix | Provider Accessibility Model, CAR, FTR — cited in §6/§10, not restated | Adequate (citation-level, as intended) |
| Cache Strategy | Seven cache types, tenant scoping, SRP integration — cited in §9, not restated | Adequate (citation-level, as intended) |

---

## Final Report

**AL entry count:** 6/6 (`AL-001`–`006`). **AR entry count:** 4/4 (`AR-001`–`004`). **DA entry count (agent-loop-facing subset):** 7/7 (`DA-009`, `010`, `013`, `014`, `018`, `020`, `021`). **17/17 canonical entries complete**, each with the full required schema (Owning Architecture Section, Concrete Algorithm, Formula/Estimation Method, Proposed Defaults, H09 interaction, Session-Phase interaction, Multi-Agent interaction, Anti-Pattern Check, Fallback, Observability Metrics, Validated By, Traceability). No new `AL-*`/`AR-*`/`DA-*`/`TECH-*` ID was created anywhere in this document.

**Agent types covered:** generic/autonomous, tool-using, coding/developer, agentic RAG, multi-agent/sub-agent, MCP (§6–7, §4).

**Optimization domains covered:** loop progress/waste, sub-agent economics/handoff, early exit, model/reasoning/output adaptive control, tool/MCP sequencing, memory optimization (subordinate to H09), coding-agent context/output/completion, session-phase policy, multi-agent coordination, cache/provider integration (citation-level), recovery/supersession, negative optimization.

**Dynamic/hardened components addressed:** 22 core components (§11.1) + 3 supporting/control components cross-listed for their agent-loop-specific role (XEC, SPC, VCL — §11.2) = 25 total listed rows; every component has a specific, non-redundant touchpoint; none redefined. The 25 figure is a row count, not a claim of 25 distinct architecture components — the authoritative total remains 22 (13 Dynamic Execution + 9 Hardening), unchanged and consistent with `scenario-matrix.md`, `optimization-catalog.md`, `provider-matrix.md`, and `cache-strategy.md`.

**Proposed methodologies:** 14 distinct instances, each explicitly tagged `PROPOSED METHODOLOGY` inline (marginal-gain formula, waste-classifier decision function, sub-agent value-estimation categories, handoff-compression extraction order, output-length forecasting method, semantic-boundary-detection method, per-phase policy table (§8), multi-agent budget-partitioning formula, conflicting-findings resolution, tool/MCP/sub-agent sequencing order, and others named inline).

**Proposed defaults:** 9 distinct instances, each tagged `PROPOSED DEFAULT — VALIDATE LOCALLY` (loop-waste heuristic starting point, sub-agent ROI floor, max fan-out, per-section context-budget floors, cumulative-cost stop-consideration ceiling, reasoning-budget-per-complexity-bucket, output-budget-per-type numbers, semantic-boundary overflow margin, CL-007 per-phase policy values).

**Source gaps:** 3 new (`SOURCE-GAP-AGENTOPT-01`–`03`), all non-blocking; zero carried forward from upstream documents required re-numbering (none of the upstream open gaps materially concern agent-loop mechanics specifically).

**Contradictions:** 0 new.

**Out-of-scope items correctly deferred, not leaked into this document:** `TECH-NNN`/`DA-NNN` full schemas (optimization-catalog.md), provider pricing/FTR-platform-assignment facts (provider-matrix.md), cache key/TTL mechanics (cache-strategy.md), CIS/TMG/SGE/SPM/XEC/VCL/CAR's own internal mechanisms (all cited, none redefined), `requirements-traceability.md` (acknowledged as planned, not created).

**Unresolved limitations:** (1) CL-007's per-phase policy values (§8) are this document's own proposal, not an authoritative default (`SOURCE-GAP-AGENTOPT-01`); (2) no authoritative sub-agent fan-out ceiling exists to enforce against unlimited parallel spawning (`SOURCE-GAP-AGENTOPT-02`); (3) conflicting-sub-agent-finding resolution (§5) is a conservative proposal, not a source-specified behavior (`SOURCE-GAP-AGENTOPT-03`); (4) AL-004 and several DA entries (DA-011/DA-015/DA-017's cross-referenced coverage) inherit thin scenario coverage already flagged honestly in `optimization-catalog.md`, not newly introduced here.

## Agent Optimization Readiness

All 17 canonical entries (`AL-001`–`006`, `AR-001`–`004`, and the seven agent-loop-facing `DA-NNN` modules) are fully elaborated with concrete algorithms, correctly distinguishing source-stated structure from this document's own labeled proposals. Agent Memory Authority (H09) is stated once as the document's central principle (§0) and referenced consistently from every section where it is materially relevant, never contradicted. The H10 correction (repeated iteration is not inherently waste) is applied verbatim everywhere AL-002/DA-014 appear, with no fixed iteration-count threshold presented as authoritative. The mandatory, non-negotiable `SubAgentResultHandoff`/`SubAgentContextHandoff` schema is restated exactly from the live `interfaces.md`, with no replay-shortcut permitted anywhere in this document, including its pseudocode. Session-phase policy (§8) correctly identifies `CL-007` as source-defined at the taxonomy level while honestly labeling its own per-phase policy values as proposed and gap-tracked. Multi-agent coordination protocols are covered without redefining SGE, SPM, XEC, or VCL's own mechanisms. Cache and provider integration are handled at citation level, deferring entirely to `cache-strategy.md` and `provider-matrix.md`. Security/governance precedence is restated, never weakened. No new `TECH-NNN`/`DA-NNN`/`AL-NNN`/`AR-NNN` identifier was invented. No source document was modified. No downstream document was created. Remaining source gaps (3, all new, all non-blocking) and contradictions (0 new) are documented and bounded.

`READY FOR NEXT DOCUMENTATION PHASE`
