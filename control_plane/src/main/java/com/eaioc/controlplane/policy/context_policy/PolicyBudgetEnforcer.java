package com.eaioc.controlplane.policy.context_policy;

import com.eaioc.controlplane.accounting.ledger.CostLedgerEntry;
import com.eaioc.controlplane.accounting.ledger.LedgerCostReporter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * EXE-P0.4.B (Capability 4, Context Policy — Minimal Implementation): the budget-enforcement hook
 * named by {@code execution-plan.md} §18.7's B row, consuming Capability 1's cost ledger via
 * {@link LedgerCostReporter}.
 *
 * <p><b>Not a realization of {@code interfaces.md} §20.2's full {@code PolicyEnforcer} (INTF-036).</b>
 * That interface has four methods: {@code enforce(plan, policy)}, {@code check_model_allowed},
 * {@code check_cache_allowed}, {@code check_tool_allowed}. Only a budget check is built here:
 * {@code enforce()} needs {@code core.schemas.OptimizationPlan}, which this unit does not touch;
 * {@code check_cache_allowed} needs {@code CacheEntry} (interfaces.md §6.3 — defined, but out of this
 * capability's declared B-row scope); {@code check_tool_allowed} needs {@code AuthorizationContext},
 * which is referenced only in §20.2 and defined nowhere else in the corpus. Naming this class
 * {@code PolicyEnforcer} would misrepresent how much of INTF-036 exists, the same reasoning
 * {@link LedgerCostReporter}'s own Javadoc gives for not calling itself {@code CostReporter}.
 *
 * <p><b>Scope (HD-CG-P0.4-02, 2026-10-07):</b> an {@link OptimizationPolicy} is accepted as a
 * parameter, never fetched — no storage/lookup component exists (`SOURCE-GAP-EXECPLAN-32`).
 *
 * <p><b>Only {@code max_cost_per_request} is enforced.</b> {@code max_monthly_cost} is realized as a
 * schema field (see {@link OptimizationPolicy}) but not enforced here: {@link LedgerCostReporter}
 * supports only a single request's cost (its own Javadoc discloses that {@code get_tenant_cost} and
 * the other period-aggregate members of INTF-048 have no defined return type anywhere in the
 * corpus). This is that pre-existing, already-disclosed gap, not a new one this unit introduces.
 *
 * <p><b>Tenant identity (explicit, per Architect pre-flight condition 5):</b> the ledger lookup uses
 * the caller-supplied {@code tenantId} parameter, never {@link OptimizationPolicy#tenantId()}. The
 * policy's own {@code tenant_id} field is not cross-checked against the caller's tenant context at
 * this sub-phase — nothing in {@code interfaces.md} §20 resolves what should happen on a mismatch,
 * and inventing that rule is out of scope here. {@code edge-cases.md} EC-060 also asks for
 * {@code organization_id} co-scoping alongside {@code tenant_id}; that co-scoping is explicitly out
 * of scope at this narrowed B sub-phase (HD-CG-P0.4-02), not silently omitted.
 *
 * <p><b>Two distinct conservative failure paths (explicit, per Architect pre-flight condition 4):</b>
 * <ol>
 *   <li><b>{@code policy == null}</b> (missing/unreadable policy) — HD-CG-P0.4-01 (2026-10-07):
 *       conservative/fail-closed, never permissive, citing root {@code CLAUDE.md} rule 2.
 *       {@code edge-cases.md} EC-060's "permissive" default is a frozen-source conflict recorded as
 *       {@code CONTRA-EXECPLAN-02}, resolved by that same human decision; EC-060 is not edited.</li>
 *   <li><b>no verified ledger measurement exists yet</b> ({@link LedgerCostReporter#getRequestCost}
 *       returns empty) — a different scenario HD-CG-P0.4-01's literal text does not by itself cover.
 *       Treated the same, conservative way here by analogy to {@code conventions.md} §16.2's SGE row
 *       ("budget-unavailable defaults to policy-configured THROTTLED/HALTED, never WITHIN_BUDGET")
 *       and its governing note that governance/policy-adjacent unresolved failures are conservative,
 *       never permissive. Context Policy is not itself a {@code GSP-NNN} component, so this is an
 *       analogy, not a literally-applicable rule — stated explicitly here, not assumed silently.</li>
 * </ol>
 *
 * <p><b>{@code policy == null} never produces a {@link PolicyViolation} (reviewer finding, resolved
 * 2026-10-07; replaces the earlier, now-withdrawn design that made {@code PolicyViolation.policyId}
 * nullable, {@code SOURCE-GAP-EXECPLAN-34}).</b> {@code interfaces.md} §20.2's {@code policy_id} is a
 * required {@code string}; a violation only makes sense when a policy was actually supplied and its
 * content was checked. "No policy supplied" is not a policy-content violation — it is reported as
 * {@code compliant = false} with an <em>empty</em> {@code violations} list, the already-existing
 * {@link BudgetEnforcementResult} shape, inventing no new field, type, or enum. The missing-ledger-
 * measurement path is unaffected: a policy is supplied there, so {@code policy.policyId()} remains a
 * real, non-fabricated value for that {@link PolicyViolation}.
 *
 * <p><b>Observability (EXE-P0.4.D, plan §18.7 D row):</b> exactly one structured log line per call,
 * matching the T1.1 Sanitizer's own D-row discipline. {@code architecture.md} §23/§36 and
 * {@code observability.md} name no Context-Policy-specific <em>metric</em>, so the D row's own cell
 * ("Cite existing policy-adjacent metrics where named; otherwise structured log output only")
 * resolves to log-output-only — but two of the three event names below are genuinely local
 * ({@code POLICY_ENFORCEMENT_COMPLETED}, {@code POLICY_ENFORCEMENT_FALLBACK_NO_POLICY}, chosen the
 * same way {@code SANITIZER_COMPLETED} was). The third, {@code POLICY_VIOLATION}, is <b>not</b>
 * locally invented: {@code interfaces.md} §23.2's Standard Event Types table already defines exactly
 * this event type, with source component {@code POLICY-ENFORCER} and payload fields {@code
 * policy_id}, {@code rule_id}, {@code severity} — the same shape {@link #logViolation} emits. Level
 * selection follows {@code conventions.md} §17.2's table exactly, not a level invented here:
 * <ul>
 *   <li>{@code compliant = true} → INFO ("normal optimization decisions").</li>
 *   <li>{@code compliant = false} with a non-empty {@code violations} list → ERROR ("policy
 *       violation" / "stage failure" — covers both {@code MAX_COST_PER_REQUEST_EXCEEDED} and
 *       {@code LEDGER_MEASUREMENT_UNAVAILABLE}).</li>
 *   <li>{@code compliant = false} with an <em>empty</em> {@code violations} list (the {@code policy
 *       == null} case) → WARNING ("fallback triggered... near-threshold behavior"), since this is
 *       conservative-default fallback behavior, not a policy-content violation.</li>
 * </ul>
 * No user input or policy content beyond identifiers/counts is logged. {@code span_id} is a fresh
 * per-line {@link UUID} linking to no real span — the same disclosed, not-claimed-as-realized
 * {@code conventions.md} §17.1 gap every other P0 log line carries (`SOURCE-GAP-EXECPLAN-29`).
 *
 * <p><b>{@code POLICY_VIOLATION} is cited, not fully realized (disclosed gap, {@code
 * SOURCE-GAP-EXECPLAN-35}):</b> {@code conventions.md} §17.4 / {@code interfaces.md} §23.3 require
 * {@code POLICY_VIOLATION} to be a <em>durably written</em> event, not merely a log line — no event
 * bus exists anywhere in P0 (the same pre-existing, already-disclosed disposition every other
 * capability's log-only interim sink carries, {@code SOURCE-GAP-EXECPLAN-01}), so this requirement is
 * not met here. The reused event name and payload fields are an intentional, accurate citation of
 * §23.2's contract shape — only the durable-write and event-bus-delivery parts of §23.1/§23.3 are out
 * of scope for this interim sink, exactly as they are for every other P0 capability's structured
 * logging.
 *
 * <p>{@code component_id} is {@code CONTEXT-POLICY}, not {@code P0.4-CONTEXT-POLICY}: Capability 4
 * has no dedicated architecture component ({@code execution-plan.md} §18.7, "Architecture
 * components: None dedicated"), so there is no canonical {@code conventions.md} §3.1 ID to use
 * (unlike the T1.1 Sanitizer's real {@code T1.1-SANITIZER}, {@code interfaces.md} §44/INTF-072) —
 * the "P0.4" prefix is {@code execution-plan.md}'s own gated-unit numbering, a build-sequencing
 * artifact, never a component identity, and does not belong in a field meant to outlive the gated
 * build process. {@code CONTEXT-POLICY} instead follows the same plain, non-numbered,
 * no-canonical-ID precedent as {@link
 * com.eaioc.controlplane.accounting.ledger.LedgerObservability}'s {@code ACCOUNTING-LEDGER} and
 * {@code BaselineObservability}'s {@code EVALUATION-BASELINE}. It is also deliberately not the
 * documented {@code POLICY-ENFORCER} (§23.2's Source Component for this event): this class is
 * explicitly not a {@code PolicyEnforcer} realization (see the first paragraph above), so logging
 * under that documented source component would falsely imply one exists.
 */
@Component
public class PolicyBudgetEnforcer {

    private static final Logger LOG = LoggerFactory.getLogger(PolicyBudgetEnforcer.class);

    private final LedgerCostReporter costReporter;

    public PolicyBudgetEnforcer(LedgerCostReporter costReporter) {
        this.costReporter = Objects.requireNonNull(costReporter, "costReporter");
    }

    /**
     * Enforces {@code policy}'s {@code max_cost_per_request} against the request's recorded ledger
     * cost. {@code policy} may be {@code null} (see class Javadoc, failure path 1).
     */
    public BudgetEnforcementResult enforceRequestBudget(String requestId, String tenantId, OptimizationPolicy policy) {
        Objects.requireNonNull(requestId, "requestId");
        Objects.requireNonNull(tenantId, "tenantId");

        if (policy == null) {
            // No PolicyViolation: §20.2's policy_id is required, and there is no policy to cite one
            // from. "No policy supplied" is conservative (never permissive) but not a content
            // violation, so an empty violations list is the honest, already-supported representation.
            logFallbackNoPolicy(requestId, tenantId);
            return new BudgetEnforcementResult(false, List.of());
        }

        Optional<CostLedgerEntry> entry = costReporter.getRequestCost(requestId, tenantId);
        if (entry.isEmpty()) {
            PolicyViolation violation = new PolicyViolation(
                policy.policyId(),
                "LEDGER_MEASUREMENT_UNAVAILABLE",
                PolicyViolation.Severity.BLOCK,
                "no verified ledger measurement exists for this request",
                "retry once a verified ledger entry exists, or treat the request as unbudgeted");
            logViolation(requestId, tenantId, violation);
            return BudgetEnforcementResult.violation(violation);
        }

        Double limit = policy.maxCostPerRequest();
        if (limit == null) {
            // §20.1: `max_cost_per_request: float | null` — null means no cap configured.
            logCompliant(requestId, tenantId, policy.policyId());
            return BudgetEnforcementResult.ok();
        }

        // Cost.totalOptimized is the actual incurred cost, not Cost.baselineEstimated (the
        // counterfactual); a budget check must compare against what was actually spent.
        double actualCost = entry.get().cost().totalOptimized();
        if (actualCost > limit) {
            PolicyViolation violation = new PolicyViolation(
                policy.policyId(),
                "MAX_COST_PER_REQUEST_EXCEEDED",
                PolicyViolation.Severity.BLOCK,
                "request cost " + actualCost + " exceeds max_cost_per_request " + limit,
                "reduce the request's cost or raise the policy's max_cost_per_request");
            logViolation(requestId, tenantId, violation);
            return BudgetEnforcementResult.violation(violation);
        }

        logCompliant(requestId, tenantId, policy.policyId());
        return BudgetEnforcementResult.ok();
    }

    /**
     * Exactly one structured INFO line for a compliant resolution (conventions.md §17.2: "normal
     * optimization decisions"). {@code correlation_id} defaults to {@code requestId}: this method
     * takes no separate correlation identifier (no {@code ControlPlaneRequest} parameter exists at
     * this capability's narrowed B/C scope), so there is no caller-supplied value to prefer.
     */
    private static void logCompliant(String requestId, String tenantId, String policyId) {
        LOG.info(
            "event_type=POLICY_ENFORCEMENT_COMPLETED component_id=CONTEXT-POLICY "
                + "message=\"budget enforcement resolved compliant\" "
                + "tenant_id={} request_id={} correlation_id={} span_id={} parent_span_id=null "
                + "policy_id={} fields={}",
            tenantId, requestId, requestId, UUID.randomUUID(), policyId, Map.of());
    }

    /**
     * Exactly one structured ERROR line for a resolution with a populated {@code violations} list
     * (conventions.md §17.2: "policy violation" / "stage failure"). Carries {@code rule_id} and
     * {@code severity} as fields; never logs {@code message}/{@code remediation} text, which are
     * fixed, non-user-controlled strings but are not themselves part of this capability's declared
     * log schema.
     */
    private static void logViolation(String requestId, String tenantId, PolicyViolation violation) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("rule_id", violation.ruleId());
        fields.put("severity", violation.severity());
        LOG.error(
            "event_type=POLICY_VIOLATION component_id=CONTEXT-POLICY "
                + "message=\"budget enforcement resolved non-compliant\" "
                + "tenant_id={} request_id={} correlation_id={} span_id={} parent_span_id=null "
                + "policy_id={} fields={}",
            tenantId, requestId, requestId, UUID.randomUUID(), violation.policyId(), fields);
    }

    /**
     * Exactly one structured WARNING line for the {@code policy == null} conservative-fallback
     * resolution (conventions.md §17.2: "fallback triggered... near-threshold behavior"). Distinct
     * from {@link #logViolation}: this path produces no {@link PolicyViolation} (HD-CG-P0.4-03), so
     * there is no {@code policy_id} to log.
     */
    private static void logFallbackNoPolicy(String requestId, String tenantId) {
        LOG.warn(
            "event_type=POLICY_ENFORCEMENT_FALLBACK_NO_POLICY component_id=CONTEXT-POLICY "
                + "message=\"no policy supplied; conservative denial\" "
                + "tenant_id={} request_id={} correlation_id={} span_id={} parent_span_id=null "
                + "reason=no_policy_supplied fields={}",
            tenantId, requestId, requestId, UUID.randomUUID(), Map.of());
    }
}
