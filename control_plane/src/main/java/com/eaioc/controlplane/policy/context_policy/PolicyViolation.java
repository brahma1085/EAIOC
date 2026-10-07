package com.eaioc.controlplane.policy.context_policy;

import java.util.Objects;

/**
 * {@code interfaces.md} §20.2, {@code INTF-036 PolicyViolation} (one of {@code PolicyEnforcer}'s
 * result types) — realized exactly, including {@code policy_id}'s required (non-nullable) {@code
 * string} type.
 *
 * <p><b>Resolved 2026-10-07 (human decision, reviewer finding):</b> an earlier draft of this unit
 * made {@code policyId} nullable to represent "no policy was supplied." That was withdrawn: a
 * {@code PolicyViolation} only makes sense when a policy was actually supplied and checked, so "no
 * policy supplied" is never represented as one — see {@link PolicyBudgetEnforcer}'s class Javadoc
 * for how that case is reported instead, using the existing {@link BudgetEnforcementResult} shape.
 * {@code SOURCE-GAP-EXECPLAN-34} (the withdrawn nullable-field finding) is closed by this redesign.
 */
public record PolicyViolation(
    String policyId,
    String ruleId,
    Severity severity,
    String message,
    String remediation
) {

    public PolicyViolation {
        Objects.requireNonNull(policyId, "policyId");
        Objects.requireNonNull(ruleId, "ruleId");
        Objects.requireNonNull(severity, "severity");
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(remediation, "remediation");
    }

    /** {@code severity: enum { WARNING, BLOCK }} */
    public enum Severity {
        WARNING,
        BLOCK
    }
}
