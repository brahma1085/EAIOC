You are a senior systems-validation architect responsible for the Enterprise Agent & LLM Inference Optimization Control Plane (EAIOC). You think like a QA/security architect stress-testing a distributed control-plane design, not like a copywriter producing a checklist for its own sake.

<task>
Produce `docs/scenario-matrix.md`: a rigorous scenario-validation and traceability matrix that proves (or disproves) whether the Control Plane, as specified in six authoritative documents, can operate correctly, safely, economically, and predictably across normal, abnormal, dynamic, adversarial, and compound execution conditions. This becomes the gatekeeper document for every optimization-specific document generated after it (optimization-catalog.md, provider-matrix.md, cache-strategy.md, agent-optimization.md, inference-optimization.md, quality-gates.md, security.md, observability.md, eval.md, SCALING.md, implementation-plan.md, ADRs) — those are explicitly out of scope for this task and must not be created now.
The goal is NOT a pretty table. The goal is a scenario set precise enough that an engineer could turn each entry directly into a test.
</task>

## Goal
- Done condition: `docs/scenario-matrix.md` exists, contains at least 200 non-trivial scenarios in the exact record format below, covers every required domain and cross-dimension intersection, includes every mandatory summary/traceability section, and ends with a literal `SCENARIO MATRIX READINESS` verdict — and every item in the Verification section below passes.
- Effort: this is intentionally a very large, long-horizon task. Do not stop early, do not truncate to save time, and do not pad the count with trivial rewordings to hit 200 — if genuine coverage needs substantially more than 200, generate more. Completeness beats hitting a round number.

## Inputs
<inputs>
Read these six files COMPLETELY before writing anything. They are large (up to ~140KB); do not rely on partial reads, sampling, or memory of prior conversations about them — read every section, using chunked `Read` calls (offset/limit) as needed to get through each file in full. Unlike ordinary reconnaissance, this task requires comprehension of the entire content of each file, not just the sections a keyword search would surface.

1. `D:\GenAI\Practice\Tok_Agent\docs\Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt` — the single authoritative source (EAIOC-SPEC-001).
2. `D:\GenAI\Practice\Tok_Agent\docs\Ent_Agent_LLM_Inference_Opt_Control_Plane_Engineering_Spec.md` — companion engineering specification.
3. `D:\GenAI\Practice\Tok_Agent\docs\architecture.md` (EAIOC-ARCH-001).
4. `D:\GenAI\Practice\Tok_Agent\docs\interfaces.md` (EAIOC-INTF-001).
5. `D:\GenAI\Practice\Tok_Agent\docs\conventions.md` (EAIOC-CONV-001).
6. `D:\GenAI\Practice\Tok_Agent\docs\edge-cases.md` (EAIOC-EDGE-001).

These six documents are the ONLY authoritative source material. Do not assume content that isn't in them. Do not silently replace their terminology with generic terminology — preserve the exact names of components, interfaces, and requirement IDs as written.
</inputs>

## Outputs
- Create exactly one new file: `D:\GenAI\Practice\Tok_Agent\docs\scenario-matrix.md`.
- Do not modify any of the six source files.
- Do not create optimization-catalog.md, provider-matrix.md, cache-strategy.md, agent-optimization.md, inference-optimization.md, quality-gates.md, security.md, observability.md, eval.md, SCALING.md, implementation-plan.md, ADRs, or any other file in `docs/` — those come later, one at a time, after this document is reviewed.
- Any scratch/progress-tracking file you keep for yourself while working (see Process, step 1) must live outside `docs/` (your own scratchpad, or a temp working file) and should be deleted once the task is done — it is not a deliverable.

## Context the executor needs
<context>
This repository is pre-implementation: `docs/` currently holds the problem statement, an engineering spec, `architecture.md`, `interfaces.md`, `conventions.md`, and `edge-cases.md` — a "stabilized six-document baseline" that must not be second-guessed or silently patched while you build this matrix. The scenario matrix's job is to answer one question: *under what conditions can this Control Plane operate correctly, safely, economically, and predictably across normal, abnormal, dynamic, adversarial, and compound execution scenarios?* It must validate functional correctness, optimization correctness, context correctness, security, authorization, policy enforcement, quality, cost, latency, reliability, recovery, observability, auditability, multi-tenancy, provider independence, agent compatibility, coding-agent compatibility, state consistency, cache correctness, memory correctness, workflow correctness, and dynamic-mutation handling.

Why fidelity matters more than speed here: this matrix becomes the shared ground truth that every subsequent optimization-specific document is checked against. A fabricated requirement, a silently resolved contradiction, or an invented architecture component here will propagate into a dozen later documents.
</context>

## Tools
- `Read` (with offset/limit) to read the six source files in full.
- `Grep` for verification counts and spot-checks after drafting (prefer it over shell grep, per this repo's tool-preference convention).
- `Write` to create `docs/scenario-matrix.md`, then `Edit` to extend it domain-by-domain rather than holding the whole 200+ scenario document in memory until the very end. Work incrementally: draft one domain's scenarios, append them, move to the next. Track what's done and what's left in a scratch progress note (outside `docs/`) so the work can resume cleanly if interrupted.

## Reference
`docs/edge-cases.md` is a useful reference for tone and rigor (it is similarly granular, one structured entry per case) — but NOT for field structure. Its `EC-NNN` schema (Domain/Objective/Severity/Likelihood/Reversible + Scenario/Trigger/Why It Matters/Detection/Expected Behavior/Fallback/Safety Implications/Observability/Testing Requirements) is a different, already-existing document; do not copy it. Every scenario in this new document must use the Scenario Record Format defined below instead.

## Constraints (non-negotiable)
Restated from this project's engineering rules, phrased as what to do, each with a way to check it:

1. **Provider neutrality.** Domain L (provider/gateway) scenarios must not assume a specific vendor API unless the source documents establish it. Check: `Grep` the finished file for hard-coded vendor names used as load-bearing logic rather than illustrative examples.
2. **Fail-open on optimization, fail-closed on security.** When an optimization step fails but authorization, policy, and security checks remain valid, fall back to the unoptimized path and continue — optimization failure alone must never block a task that is otherwise authorized and safe. When authorization, policy, or security checks themselves fail, fail closed and reject the request — a security/authorization failure must never be waved through as "fall back to unoptimized." Check: for any scenario with both an optimization-failure and a security/authorization-failure component, confirm the expected behavior rejects the request (fail-closed) rather than merely falling back to an unoptimized but still-executed path.
3. **Tenant isolation is absolute.** Domain Z (multi-tenancy) and any cache/memory/cost scenario must treat cross-tenant sharing or blending as unconditionally prohibited. Check: `Grep` for "tenant" across cache/memory scenarios and confirm none describe a cross-tenant fallback as acceptable.
4. **Never fabricate savings.** Cost-domain scenarios where token/cost accounting can't be verified must mark the outcome `unverified` and excluded from savings reporting, not report a number. Check: spot-check Domain S scenarios for this framing.
5. **Required protected information is never silently lost — but policy-permitted minimization/redaction is allowed.** Security-classified or sensitive content must never be pruned, compressed, or deduplicated away by an optimization stage acting on its own judgment. It MAY be minimized or redacted when a policy explicitly authorizes that specific minimization/redaction — but that must be an explicit, policy-cited, auditable decision, never an incidental side effect of a generic optimization pass. Applies to Domain B (context) and Domain Y (security) scenarios where context reduction meets sensitive content. Check: `Grep` sensitive/PII-related context scenarios — confirm each one's expected behavior either preserves the protected information in full, or attributes any reduction to an explicit, cited policy decision rather than to the optimizer's own discretion.
6. **No full conversation-history replay to a sub-agent.** Domain E (agents) scenarios involving sub-agent handoff must expect a compressed handoff object (objective, findings, evidence, decisions, unresolved questions), never full history replay. Check: spot-check sub-agent scenarios for this.
7. **Do not invent a new component, interface, or requirement ID.** If something needed for a scenario isn't established in the six documents, mark it `SOURCE-GAP` (see Process) instead of inventing it or silently treating it as established.
8. **Do not silently resolve contradictions between source documents.** Record them under `SOURCE CONTRADICTIONS` instead (see Process).
9. **Do not remove or weaken any source requirement.** This document only adds validation scenarios; it does not redesign the six source documents.
10. **Logical Task Context is distinct from Model-Admitted Context.** Treat "what the task logically needs" and "what has actually been admitted into the model's context window" as two separate concepts throughout — never conflate the model's lack of awareness of something with that thing being logically irrelevant, or vice versa. Check: Domain B/D scenarios include cases where the two diverge, and the expected behavior addresses the divergence explicitly (retrieve, ask user, defer, or fail safely) rather than silently assuming the model's admitted view is complete.
11. **Agent memory cannot override Control Plane execution truth or authoritative external system state.** Where agent-owned memory conflicts with Control-Plane-owned execution state or an authoritative external system (actual repository state, actual policy-service state, etc.), the Control Plane's own state and the external system of record win — memory is advisory, never authoritative. Check: Domain N scenarios involving conflict or staleness resolve in favor of Control Plane state or the external system, never agent memory.

## Process
Work through these in order; steps 3–13 can interleave domain-by-domain rather than strictly sequentially.

1. **Read all six source documents completely** (see Inputs). Do not draft anything until this is done.

2. **Define the scenario ID system.** Format: `SCN-<CODE>-<NUMBER>` (3-digit, zero-padded), unique, never reused, stable across regeneration unless the underlying scenario materially changes. Use this exact domain→code mapping so IDs stay deterministic:

   | Domain | Code | Domain | Code | Domain | Code |
   |---|---|---|---|---|---|
   | A Request/User | REQ | K Model Selection | MODEL | U Execution State | STATE |
   | B Context | CTX | L Provider/Gateway | PROV | V Long Wait | WAIT |
   | C Context Budget | BUD | M Tools/MCP | TOOL | W Interruption/Recovery | REC |
   | D Context Admission | ADM | N Memory | MEM | X Idempotency/Side Effects | IDEM |
   | E Agents | AGENT | O Cache | CACHE | Y Security | SEC |
   | F Coding/Developer Agents | CODE | P Optimization | OPT | Z Multi-Tenancy | TEN |
   | G Workflow | WF | Q Negative Optimization | NOPT | AA Concurrency | CONC |
   | H File/Artifact Mutation | FILE | R Quality | QUAL | AB Supersession | SUPER |
   | I Permissions | PERM | S Cost | COST | AC Observability/Audit | AUDIT |
   | J Policy | POL | T Latency | LAT | AD Compound | CMP |

3. **Cover every required domain** (A–AD). For each, generate scenarios covering at minimum the listed conditions — treat these as a minimum checklist, not a cap:
   - **A — Request/User:** normal, empty, malformed, ambiguous, changed, corrected, cancelled, partial, expanded, conflicting, duplicate, repeated, superseding, out-of-context, and intent-change requests.
   - **B — Context:** no/minimal/normal/large/oversized context, overflow, underflow, fully pruned, mandatory context accidentally removed, stale, contradictory, duplicate, irrelevant, sensitive, expansion, reduction, replacement, mutation, version mismatch, reconstruction, externalization, retrieval failure/timeout, compression, summarization, and compression/retrieval quality failure. Include explicit **Logical Task Context ≠ Model-Admitted Context** cases: information the task logically requires but that was never admitted into the model's context window; information the agent believes it has but was pruned or never retrieved; the two sets diverging after a context mutation. Define the expected behavior for each divergence (retrieve, ask user, defer, fail safely) — never assume the model's admitted view is complete.
   - **C — Effective Context Budget:** model context limit, effective budget, system-prompt/developer-instruction/policy/tool-definition/protocol overhead, output and reasoning reservation, safety margin, budget changing mid-execution, budget too small for mandatory context, larger model available/unavailable, no eligible model.
   - **D — Context Admission:** mandatory context admitted; irrelevant/sensitive/stale/unauthorized context rejected; duplicates eliminated; priority conflicts; admission failure; admission recomputation and incremental admission after mutation.
   - **E — Agents:** single agent, autonomous agent, multi-agent, sub-agent, delegation, agent loop, retry, termination, agent asking for missing/additional context, requesting prohibited information or an unavailable tool, receiving stale context or a superseded workflow, resuming from checkpoint, agent state conflicting with Control Plane state.
   - **F — Coding/Developer Agents (first-class domain):** across environments such as Antigravity, Cursor, GitHub Copilot, Claude Code, and other compatible developer-agent environments (do not assume a specific vendor API unless supported by the source docs) — repository exploration, file/symbol/dependency/test discovery, build-error and runtime-log discovery, git/branch state, large repository, huge diff, generated/binary/ignored/deleted/modified files, file changed externally, repository changed mid-execution, stale/conflicting patch, agent generating code outside admitted context, agent legitimately discovering missing context, agent making an unsupported assumption, agent requesting an unauthorized file or prohibited operation, agent attempting to bypass policy, tool/repository permission revoked, test/commit/deployment denied, MCP tool unavailable or schema-changed, build/test output causing context overflow.
   - **G — Workflow:** normal, optional steps, mandatory steps, reduction, expansion, reordering, cancellation, mutation, version change, supersession, partial completion, blocked, resumed, restarted.
   - **H — File/Artifact Mutation:** added, removed, replaced, modified, becomes inaccessible, permissions changed, new attachment mid-execution, removed while processed, changed after caching, version mismatch, artifact becomes stale, large attachment causing overflow, sensitive/unauthorized attachment.
   - **I — Permissions:** exists, missing, granted/revoked mid-execution, expires, role change, repository/tool/model/provider permission change, permission differing between initial request and resume, permission differing between cache creation and reuse.
   - **J — Policy:** normal, denial, changed mid-execution, stricter, relaxed, version mismatch, service unavailable, evaluation timeout, cached decision invalidated, previously permitted action becomes prohibited, optimization conflicting with policy, context optimization attempting to expose restricted information.
   - **K — Model Selection:** optimal model available/unavailable, recommended model inaccessible, unauthorized, capability mismatch, insufficient context window, excessive latency/cost, quota exhausted, rate limited, temporarily/permanently unavailable, becomes (un)available mid-execution, stale capability metadata, no eligible model.
   - **L — Provider/Enterprise Gateway:** direct provider access, enterprise gateway, internal AI platform, private/on-premise model, hybrid architecture, gateway/provider unavailable, gateway auth failure, provider timeout/rate limit/capability mismatch, provider changes routing, org prohibits direct external provider access, credentials unavailable to the Control Plane. The Control Plane must remain provider-neutral.
   - **M — Tools/MCP:** available, unavailable, timeout, failure, schema change, permission revoked, huge/malformed result, result becomes stale, MCP discovery/authorization failure, result causing context overflow or conflicting with existing context.
   - **N — Memory:** no memory, available, stale, corrupted, unauthorized, conflict, version mismatch, retrieval failure, retention expiry, deletion, agent-owned vs. Control-Plane-owned execution state, memory differing from authoritative external state. Every such conflict/staleness scenario must resolve with Control Plane execution state or the authoritative external system winning — **agent memory is advisory and must never override either.**
   - **O — Cache:** hit, miss, partial hit, stale, invalid, poisoning, authorization/policy/tenant/context-version/workflow/task-intent/source-version mismatch, invalidation, unavailable, lookup overhead exceeding savings, negative optimization decision.
   - **P — Optimization:** every optimization category actually established in the source documents (e.g. prompt optimization, context pruning, dedup, compression, summarization, retrieval, caching, model routing/selection, batching, request shaping, tool optimization, agent-loop optimization, memory optimization, inference/reasoning/output optimization). Do not add techniques merely because they're common in the industry if unsupported by the sources — mark genuinely unsupported candidates `SOURCE-GAP`.
   - **Q — Negative Optimization:** explicit `DO_NOT_OPTIMIZE` scenarios where overhead exceeds savings, quality risk is excessive, latency increases, context is already efficient, compression is unsafe, retrieval/cache/routing benefit is negligible, or optimization confidence is low.
   - **R — Quality:** preserved, improved, degraded, semantic drift, lost requirements/constraints/dependencies, wrong model selection, incorrect summarization/retrieval/pruning, optimization causing task failure, token savings achieved but task quality fails. Quality must take precedence over raw token reduction.
   - **S — Cost:** tokens saved, total cost saved, token reduction with higher total cost, overhead exceeding savings, retries increasing cost, fallback increasing cost, context reconstruction/evaluation increasing cost, cache miss increasing cost, model switch changing cost, budget exhausted or changing mid-execution.
   - **T — Latency:** optimization improving/increasing latency, slow provider/retrieval/compression/cache/policy-evaluation, long user/external wait, timeout, deadline exceeded, latency budget changes.
   - **U — Execution State:** created, running, waiting, paused, blocked, interrupted, cancelled, superseded, resumable, completed, failed, aborted — test both valid and invalid transitions.
   - **V — Long Wait:** waiting for user/approval/tool/provider/external system, hours- or days-long wait, state/context/policy/permission/model/workflow changing while waiting. Resume must reconcile current state, not blindly replay stale state.
   - **W — Interruption/Recovery:** network interruption, Control Plane crash, agent crash, provider/tool failure, timeout, process/infrastructure restart, user cancellation, partial completion, checkpoint available/unavailable/corrupted/stale, resume, recompute, rollback, restart, safe abort.
   - **X — Idempotency/Side Effects:** classify operations as read-only, idempotent, conditionally idempotent, non-idempotent, or irreversible; test duplicate-execution protection for commits, deployments, database writes, emails, tickets, external mutations, destructive operations.
   - **Y — Security:** authentication/authorization failure, privilege escalation, unauthorized context/file/tool/model/provider, cross-tenant access, cache/memory leakage, prompt injection, malicious retrieved content or tool output, sensitive information in summaries/logs/cache, revoked permission, policy-bypass attempt. Security must override optimization.
   - **Z — Multi-Tenancy:** tenant/context/cache/memory/policy/model-access/provider-access/quota/observability isolation, cross-tenant cache or context-retrieval attempts.
   - **AA — Concurrency:** simultaneous requests, simultaneous context/file mutation, permission or policy change mid-execution, concurrent workflow modification, concurrent agent/sub-agent operations, concurrent cache update/checkpoint/resume, race conditions, stale-write protection, optimistic concurrency conflict.
   - **AB — Supersession:** newer request replacing an old one; superseded workflow, context, agent execution, checkpoint, pending side effect, cache result, optimization decision; superseded task attempting to continue.
   - **AC — Observability/Audit:** the system must be able to explain why a context item was removed, why a model was selected or rejected, why a provider was rejected, why an optimization was applied or skipped, why cache was invalidated, why execution was paused or failed, why authorization was denied, why policy blocked an operation, why recovery chose recomputation, what state version was active. Audit data must not itself create unauthorized information exposure.
   - **AD — Compound Scenarios (mandatory):** meaningful combinations of multiple independent state changes — e.g. a sensitive file added while context nears overflow, the model becomes unavailable, and policy becomes stricter, all at once; or a coding agent waiting on a tool while the repository changes, permission is revoked, and the workflow is modified. This is not a minor category — **target roughly 40–60 meaningful compound scenarios**, not a token handful. Prioritize combinations that mix context + permission + policy + model/provider availability + agent state + workflow state + concurrency/recovery state. At least several compound scenarios must each incorporate one of the Decision-Action Gap, stale-optimization-decision/result, or partial-optimization-outcome patterns (Process steps 4a–4c) alongside a security- or tenant-isolation-relevant dimension.

4. **Cover cross-dimension intersections explicitly**, not just isolated domains — at minimum: Context×Model, Context×Provider, Context×Policy, Context×Permission, Context×Cache, Context×Memory, Context×Coding-Agent, Context×Workflow; Agent×Tool, Agent×Permission, Agent×Policy, Agent×Memory, Agent×Context; Coding-Agent×Repository, Coding-Agent×MCP, Coding-Agent×Permission, Coding-Agent×Policy, Coding-Agent×Context, Coding-Agent×Recovery; Workflow×Permission, Workflow×Policy, Workflow×Context, Workflow×Recovery; Cache×Permission, Cache×Policy, Cache×Context-Version, Cache×Tenant; Model×Provider, Model×Context, Model×Cost, Model×Latency; Recovery×Idempotency, Recovery×Supersession, Recovery×Policy, Recovery×Permission. Create higher-order combinations where meaningful.

4a. **Decision-Action Gap: state changed after decision, but before action.** For every domain where the Control Plane makes an admission/authorization/routing/optimization decision and then acts on it, generate scenarios where the relevant state changes in the gap between decision and action — at minimum: permission revoked after an authorization decision but before the authorized action executes; policy tightened after a policy check but before the gated operation runs; context mutated or superseded after admission but before the model call is dispatched; workflow mutated or cancelled after a step is scheduled but before it executes; model/provider becomes unavailable after selection but before dispatch; a tool/MCP permission or schema changes after a tool call is decided but before it's invoked. Each such scenario's `Expected Control Plane Decision` must state whether the stale decision is revalidated, aborted, or requires re-decision — it must never be silently executed on stale authority.

4b. **Stale optimization decisions and results.** Generate scenarios covering both: (a) an optimization decision (e.g. "use this cached result," "route to model X," "prune these items") becomes invalid before it is applied, because policy, permissions, context, or availability changed in the interim, and the Control Plane must detect and revalidate rather than apply it blindly; and (b) an optimization result was computed against Context Version N but the context has already advanced to Version N+1 by the time the result is ready to use — the Control Plane must detect the version mismatch and recompute, invalidate, or reconcile rather than silently applying an N-versioned result to N+1 state.

4c. **Partial optimization success/failure.** Generate scenarios where a single optimization pass has mixed outcomes across its sub-operations — e.g. deduplication succeeds but compression fails on the same context; some files/items in a batch optimize successfully while others cannot (and must fall back individually); one stage of a multi-stage optimization pipeline succeeds while a downstream stage fails. Define the expected behavior per sub-operation — does the successful part still apply, or does the whole optimization roll back to the unoptimized path? — rather than treating optimization success/failure as all-or-nothing.

5. **Record every scenario in exactly this format** — do not invent a different schema, do not skip fields, and do not fill an inapplicable field with a meaningless placeholder (write `N/A — not applicable to this scenario.` instead):

   ```
   ### SCN-<CODE>-<NNN> — Scenario Title

   **Category:**
   **Subcategory:**
   **Type:** Positive | Negative | Boundary | Failure | Recovery
   **Priority:** P0 | P1 | P2 | P3
   **Automation Candidate:** unit test | integration test | contract test | end-to-end test | chaos test | security test | performance test | load test | evaluation | manual validation

   **Source Requirements:**
   - ...

   **Initial State:**
   **Trigger:**
   **Relevant Context:**
   **Context Version:**
   **Workflow State/Version:**
   **Permission State:**
   **Policy State/Version:**
   **Model State:**
   **Provider State:**
   **Tool/MCP State:**
   **Cache State:**
   **Memory State:**

   **Expected Control Plane Decision:**
   **Expected Optimization Behavior:**
   **Expected Security Behavior:**
   **Expected Quality Behavior:**
   **Expected Cost Behavior:**
   **Expected Latency Behavior:**
   **Expected State Transition:**
   **Expected Recovery:**
   **Side-Effect Requirements:**
   **Idempotency Requirement:**
   **Audit/Observability Requirement:**
   **Failure Classification:** (see the taxonomy below, if applicable)

   **Acceptance Criteria:** (testable Given/Then — never "the system should behave correctly")

   **Traceability:**
   - Problem Statement: ...
   - Engineering Specification: ...
   - Architecture: ...
   - Interfaces: ...
   - Conventions: ...
   - Edge Cases: ...
   ```

   Write concrete triggers, not vague ones: not "Provider fails," but "Provider becomes unavailable after model selection but before inference dispatch" — then define exactly what the Control Plane must do.

6. **Trace every scenario to the source documents.** Prefer exact section/heading/requirement-ID/interface/architecture-component/convention/edge-case references when the sources have stable IDs (e.g. `OBJ-NNN`, `SEC-NNN`, `NFR-NNN`, `AC-NNN`, `EC-NNN`, component IDs like `T1.3-CONTEXT-PRUNER`). Do not invent requirement IDs that don't exist — if a source document lacks a stable ID for something, cite the document and section heading instead.

7. **Track what's genuinely missing from the sources as `SOURCE-GAP`, never as an established requirement.** If a behavior seems necessary for a robust enterprise Control Plane but isn't represented in the six documents, do not silently add it. Instead add it to a `## SOURCE GAPS DISCOVERED` section with: Gap ID, affected scenario(s), the missing requirement, why it matters, which source document should eventually be updated, and a recommended disposition.

8. **Track contradictions between source documents as `SOURCE CONTRADICTIONS`, never silently resolved.** Add a `## SOURCE CONTRADICTIONS` section with: contradiction ID, documents involved, the conflicting statements, affected scenarios, and a recommended resolution. Mark an affected scenario `BLOCKED-BY-CONTRADICTION` where appropriate.

9. **Build the summary sections**, after the scenarios:
   - `## Coverage Matrix` — Domain × {Positive, Negative, Boundary, Failure, Recovery, Compound} counts, plus rollup coverage for context, agents, coding agents, security, model/provider, workflow, permissions, cache, memory, recovery, concurrency, cost, quality, latency.
   - `## Cross-Document Coverage Matrix` — for each of the six source documents, how many scenarios reference it and where coverage is thin or absent.
   - `## Requirement → Scenario Traceability` — reverse index: requirement/section → scenario IDs → expected behavior → how it's validated. Should let an engineer start from a requirement and find the scenarios that validate it.
   - `## Scenario → Architecture Traceability` — map scenarios to the actual named architecture components (e.g. Execution State Manager, Context Version Manager, Workflow Version Manager, Checkpoint Manager, Reconciliation Engine, Context Integrity Gate, Context Expansion Controller, Permission Revalidation, Dynamic Policy Evaluation, Capability/Availability Resolver, Stale Result Protection, Supersession Manager, Recovery Coordinator, and the relevant optimization/cache/provider-abstraction/agent-integration components) — use the exact terminology from `architecture.md`; do not invent components.
   - `## Control Plane Invariants` — only invariants actually supported by the source documents (e.g. unauthorized context must never be admitted; revoked permission must not authorize new operations; optimization cannot override policy; quality cannot be sacrificed solely for token savings; stale state must not silently become authoritative; inaccessible models cannot be selected; non-idempotent operations must not be blindly replayed; superseded executions must not continue unauthorized side effects; logical task context must not be confused with model-admitted context; agent-owned memory must never override Control Plane execution state or authoritative external system state; a decision made by the Control Plane must be revalidated, not blindly executed, if the state it depended on changed before the corresponding action ran; optimization failure must not automatically become task failure unless policy/safety requires it). Verify each against the source documents; use source terminology.
   - `## Failure Taxonomy` — classify failures where supported: user error, validation failure, authorization failure, policy failure, context failure, optimization failure, model failure, provider failure, tool failure, memory failure, cache failure, execution failure, infrastructure failure, state conflict, stale state, timeout, budget failure, quality failure, security failure, unrecoverable failure. Do not invent categories merely for completeness.
   - `## Recovery Taxonomy` — where applicable: retry, retry with same state, retry after reconciliation, recompute, retrieve missing context, expand context, reduce context, switch model, switch provider, invalidate cache, restore checkpoint, rollback, restart, pause, ask user, abort safely.
   - **No blind fallback:** for each relevant scenario, distinguish Fallback Allowed / Not Allowed / Requires Reconciliation / Requires User Approval / Violates Policy / Would Reduce Quality / Would Increase Cost Excessively — don't assume every failure triggers fallback.
   - **No silent data loss:** explicitly identify scenarios where optimization could lose requirements, constraints, context, provenance, permissions, policy state, workflow state, or recovery state, and make sure the expected behavior prevents it.
   - **No silent policy bypass:** every optimization path is subject to the same authorization, policy, tenant-isolation, security, and compliance requirements as the non-optimized path.
   - **No silent context fabrication:** when required context is unavailable, the system must not fabricate it — each such scenario must explicitly state whether the expected response is retrieve, ask user, defer, or fail safely.

10. **Deduplicate before finalizing.** Remove duplicate scenarios, merge semantically identical ones, but retain distinct boundary conditions and meaningful compound interactions. Don't inflate the count with superficial wording changes.

11. **Run a completeness review** across the lifecycle (Create, Admit, Optimize, Execute, Mutate, Reconcile, Continue, Pause, Resume, Complete) and the failure path (Fail, Detect, Classify, Recover, Retry/Recompute/Fallback, Reconcile, Resume/Abort) before declaring the document done.

12. **Meet the coverage target:** at least 200 meaningful scenarios, roughly 30–40% normal/positive, 30–40% negative/failure, 15–20% boundary/extreme, 10–20% compound/cross-dimensional (guidance, not a rigid split) — generate more than 200 if genuine coverage demands it.

13. **Write the closing `## Final Report`** with: total scenario count; domain coverage counts; Positive/Negative/Boundary/Failure/Recovery/Compound distribution; P0/P1/P2/P3 distribution; source-document coverage counts; which architecture components are exercised; which interfaces/contracts are exercised; the full list of Source Gaps; the full list of Source Contradictions; the highest-risk (P0) scenarios; recovery-critical scenarios; security-critical scenarios; the highest-risk coding-agent scenarios; the highest-risk context scenarios.

14. **End with a `## Scenario Matrix Readiness` verdict** — exactly one of:
    - `READY FOR NEXT DOCUMENTATION PHASE`, or
    - `NOT READY — BASELINE REQUIREMENTS MUST BE REVISED` (and if this is the verdict, list exactly which source requirements must be revised, and do not revise them yourself).

## Verification (run before you consider this done)
- `Grep` `^### SCN-` in the finished file: count ≥ 200.
- `Grep` all `SCN-` IDs, sort, and confirm none repeat.
- Confirm every one of the 30 domain codes in the mapping table (step 2) appears in at least one scenario ID.
- Confirm every scenario has a `Type` from {Positive, Negative, Boundary, Failure, Recovery} and a `Priority` from {P0, P1, P2, P3}; none left blank.
- Confirm no field contains a meaningless placeholder — an inapplicable field must read `N/A — not applicable to this scenario.`, not be blank or say "TBD".
- Confirm every required section exists exactly once: `SOURCE GAPS DISCOVERED`, `SOURCE CONTRADICTIONS`, `Coverage Matrix`, `Cross-Document Coverage Matrix`, `Requirement → Scenario Traceability`, `Scenario → Architecture Traceability`, `Control Plane Invariants`, `Failure Taxonomy`, `Recovery Taxonomy`, `Final Report`, `Scenario Matrix Readiness`.
- Confirm the readiness verdict is one of the two literal strings specified, with nothing else appended to that line.
- Confirm none of the six source files were modified (their content/hashes are unchanged from before you started).
- Confirm no scenario in a cache/memory/cost context describes cross-tenant sharing as acceptable (Constraint 3).
- Confirm optimization-failure and security/authorization-failure scenarios fail closed rather than merely falling back to unoptimized-but-executed (Constraint 2).
- Confirm sensitive/PII context scenarios either preserve the protected information in full or attribute any reduction to an explicit cited policy decision, never to optimizer discretion (Constraint 5).
- Confirm Domain N memory-conflict scenarios always resolve in favor of Control Plane state or the authoritative external system, never agent memory (Constraint 11).
- Confirm Domain B/D includes explicit Logical-Task-Context-≠-Model-Admitted-Context divergence scenarios (Constraint 10).
- Confirm Decision-Action Gap scenarios (step 4a) exist for at least permissions, policy, context, workflow, model/provider, and tools/MCP — one gap scenario per domain is not enough; check for more than a token example in each.
- Confirm stale-optimization-decision and stale-optimization-result scenarios (step 4b) exist and are distinct from ordinary cache-staleness scenarios.
- Confirm partial-optimization-outcome scenarios (step 4c) exist and define per-sub-operation expected behavior rather than an all-or-nothing outcome.
- Confirm Domain AD (compound) scenario count is in the 40–60 range (or justified if outside it) and that the required combination classes (context+permission+policy+model/provider+agent+workflow+concurrency/recovery) are actually represented, not just named in the section header.

**Automated structural validation** — run these as scripted/grep-based checks across the whole document, not by eye:
- Every `### SCN-` block contains every field from the Scenario Record Format (step 5) in order, with none missing and none renamed.
- Every scenario ID referenced anywhere in `Coverage Matrix`, `Cross-Document Coverage Matrix`, `Requirement → Scenario Traceability`, or `Scenario → Architecture Traceability` corresponds to a scenario ID that actually exists in the document (no dangling references) — and conversely, every scenario ID appears in at least one of those tables (no orphaned scenarios).
- No two scenarios share an ID (sort + uniq check).
- No field value is empty or a bare placeholder ("...", "TBD", "N/A" without the required "— not applicable to this scenario." suffix).

**Targeted sampling** (in addition to, not instead of, the automated checks) — verify these by opening the actual cited source-document section, not from memory: at least 3 Positive, 3 Negative, 3 Boundary, 3 Failure, and 3 Recovery scenarios; at least 3 coding-agent (`CODE`) scenarios; at least 3 security (`SEC`) scenarios; and at least 5 compound (`CMP`) scenarios, including at least one each covering the Decision-Action Gap, stale-optimization, and partial-optimization patterns. For each sampled scenario, confirm: the trigger is concrete (not vague), the acceptance criteria is a testable Given/Then, and the cited traceability reference actually exists in the named source file.

## Failure modes to defend against
1. Drafting from memory of the source documents instead of what you actually read — producing hallucinated traceability references. Defense: re-open the cited section during the verification spot-check.
2. Padding the scenario count with trivial rewordings of the same scenario to hit 200. Defense: the deduplication pass (Process step 10) and the "don't inflate" rule.
3. Inventing an architecture component, interface, or requirement ID that doesn't exist in the six documents. Defense: `Grep` the source docs for the exact term before citing it.
4. Silently resolving a contradiction between two source documents instead of flagging it. Defense: the `SOURCE CONTRADICTIONS` section is mandatory, not optional.
5. Treating an industry-standard optimization technique as an established Control Plane capability when the sources don't support it. Defense: mark it `SOURCE-GAP` instead.
6. Running out of context mid-document and leaving the Coverage Matrix / Final Report / Readiness sections unwritten. Defense: work domain-by-domain, append incrementally, track progress in a scratch note, and treat those closing sections as equally mandatory as the scenarios themselves.
7. Reusing an ID or inventing a new ID scheme instead of the deterministic `SCN-<CODE>-<NNN>` mapping in step 2.
8. Editing one of the six source documents "to fix" something noticed while drafting. Defense: Output Scope explicitly forbids this — file a `SOURCE-GAP` or `SOURCE CONTRADICTION` entry instead.
9. Writing vague, untestable acceptance criteria like "the system should behave correctly" instead of a concrete Given/Then. Defense: Process step 5's determinism requirement.
10. Under-covering the mandatory compound (Domain AD) and cross-dimension (step 4) scenarios in favor of easier single-domain ones. Defense: the Coverage Matrix must show non-trivial compound coverage (40–60 scenarios), not a token handful.
11. Treating "decision" and "action" as atomic when they're not — skipping revalidation for state that changed in the gap between them, or treating a stale cached/optimization decision as still valid because it once passed a check. Defense: Process steps 4a–4b are mandatory, not optional extras.
12. Treating optimization success/failure as all-or-nothing when a real pass has independently-failing sub-operations, or conflating agent memory with Control Plane execution truth when they disagree. Defense: Process step 4c and Constraint 11.

## Audience
This document's eventual readers are the same engineers who will build against `architecture.md` and `interfaces.md` — assume familiarity with those documents' terminology and component names. Use no jargon beyond what those source documents already use; do not introduce new terms of art for concepts the sources already name.

Start by reading all six source documents in full (Inputs), then propose your `SCN-<CODE>` domain mapping confirmation and scratch-progress plan before writing the first scenario.
