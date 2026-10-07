package com.eaioc.controlplane.policy.context_policy.integration;

import com.eaioc.controlplane.policy.context_policy.BudgetEnforcementResult;
import com.eaioc.controlplane.policy.context_policy.OptimizationPolicy;
import com.eaioc.controlplane.policy.context_policy.PolicyBudgetEnforcer;

/**
 * TEST STUB: a minimal downstream consumer of Capability 4's public read API, standing in for its
 * first declared consumers (Prompt Assembler / Output Controls, Capabilities 5/6 — neither exists
 * yet). It is test-tree code only.
 *
 * <p>It uses only the public surface: {@link
 * PolicyBudgetEnforcer#enforceRequestBudget(String, String, OptimizationPolicy)} and the {@link
 * BudgetEnforcementResult} accessors. It is deliberately in a separate package, so it cannot reach
 * any package-private seam of {@code policy.context_policy}.
 *
 * <p>This is a consumer stub, not pipeline wiring. It does not fetch an {@link OptimizationPolicy}
 * itself — no storage/lookup component exists ({@code SOURCE-GAP-EXECPLAN-32}) — and does not
 * implement Capability 5 or 6 behaviour.
 */
public final class StubDownstreamConsumer {

    private final PolicyBudgetEnforcer enforcer;

    public StubDownstreamConsumer(PolicyBudgetEnforcer enforcer) {
        this.enforcer = enforcer;
    }

    public BudgetEnforcementResult checkBudget(String requestId, String tenantId, OptimizationPolicy policy) {
        return enforcer.enforceRequestBudget(requestId, tenantId, policy);
    }
}
