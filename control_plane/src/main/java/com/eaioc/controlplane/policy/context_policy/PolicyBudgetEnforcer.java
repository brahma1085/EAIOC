package com.eaioc.controlplane.policy.context_policy;

import com.eaioc.controlplane.accounting.ledger.CostLedgerEntry;
import com.eaioc.controlplane.accounting.ledger.LedgerCostReporter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 */
@Component
public class PolicyBudgetEnforcer {

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
            return new BudgetEnforcementResult(false, List.of());
        }

        Optional<CostLedgerEntry> entry = costReporter.getRequestCost(requestId, tenantId);
        if (entry.isEmpty()) {
            return BudgetEnforcementResult.violation(new PolicyViolation(
                policy.policyId(),
                "LEDGER_MEASUREMENT_UNAVAILABLE",
                PolicyViolation.Severity.BLOCK,
                "no verified ledger measurement exists for this request",
                "retry once a verified ledger entry exists, or treat the request as unbudgeted"));
        }

        Double limit = policy.maxCostPerRequest();
        if (limit == null) {
            // §20.1: `max_cost_per_request: float | null` — null means no cap configured.
            return BudgetEnforcementResult.ok();
        }

        // Cost.totalOptimized is the actual incurred cost, not Cost.baselineEstimated (the
        // counterfactual); a budget check must compare against what was actually spent.
        double actualCost = entry.get().cost().totalOptimized();
        if (actualCost > limit) {
            return BudgetEnforcementResult.violation(new PolicyViolation(
                policy.policyId(),
                "MAX_COST_PER_REQUEST_EXCEEDED",
                PolicyViolation.Severity.BLOCK,
                "request cost " + actualCost + " exceeds max_cost_per_request " + limit,
                "reduce the request's cost or raise the policy's max_cost_per_request"));
        }

        return BudgetEnforcementResult.ok();
    }
}
