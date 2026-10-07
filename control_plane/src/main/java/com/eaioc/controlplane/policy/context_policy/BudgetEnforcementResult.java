package com.eaioc.controlplane.policy.context_policy;

import java.util.List;

/**
 * The result of {@link PolicyBudgetEnforcer#enforceRequestBudget}. Not {@code interfaces.md} §20.2's
 * {@code EnforcementResult} — that type's {@code modified_plan: OptimizationPlan} field has no
 * meaning for a post-hoc, ledger-based budget check, since no {@code OptimizationPlan} is in scope
 * here (see {@link PolicyBudgetEnforcer}'s class Javadoc for the full scope narrowing).
 */
public record BudgetEnforcementResult(boolean compliant, List<PolicyViolation> violations) {

    public BudgetEnforcementResult {
        violations = violations == null ? List.of() : List.copyOf(violations);
    }

    public static BudgetEnforcementResult ok() {
        return new BudgetEnforcementResult(true, List.of());
    }

    public static BudgetEnforcementResult violation(PolicyViolation violation) {
        return new BudgetEnforcementResult(false, List.of(violation));
    }
}
