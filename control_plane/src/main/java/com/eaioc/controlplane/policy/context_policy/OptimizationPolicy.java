package com.eaioc.controlplane.policy.context_policy;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * {@code interfaces.md} §20.1, {@code INTF-035 OptimizationPolicy} (verified verbatim against the
 * live file at {@code EXE-P0.4.A}, {@code control_plane/policy/context_policy/ContextPolicyContract.md}).
 *
 * <p>Lives in this package, not {@code core.schemas} — {@code OptimizationPolicy} is an INTF-numbered
 * schema owned by this capability, the same placement {@code SanitizerOutput} (INTF-072) uses rather
 * than {@code core.schemas}.
 *
 * <p><b>EXPLICITLY INCOMPLETE REALIZATION (`SOURCE-GAP-EXECPLAN-33`) — not a conformant INTF-035
 * record.</b> Two source fields are absent because their type cannot be realized without inventing
 * content:
 * <ul>
 *   <li>{@code per_workflow_overrides: map<string, PolicyOverride>}</li>
 *   <li>{@code per_intent_overrides: map<string, PolicyOverride>}</li>
 * </ul>
 * {@code PolicyOverride} is referenced at both sites and defined nowhere else in the corpus (verified
 * by exhaustive grep across {@code docs/*.md}). This mirrors {@code core.schemas.OptimizationPlan}'s
 * own {@code CORE-GAP-03}/{@code CORE-GAP-04} disclosure pattern. Acceptable at P0 only because
 * {@code EXE-P0.4.B}'s own narrowed scope (budget enforcement only, HD-CG-P0.4-02) never reads either
 * field. Nothing may treat an instance of this type as a complete policy until the gap closes
 * upstream and the fields are added.
 *
 * <p>Every other field is realized in source order. List fields the source does not mark nullable
 * are non-nullable here too: {@code null} becomes an empty list and every list is defensively
 * copied, the same rule {@code OptimizationPlan} uses. {@code data_residency} is the one list field
 * the source marks {@code | null}, so it is left {@code null} rather than forced to empty.
 */
public record OptimizationPolicy(
    String policyId,
    String policyVersion,
    String schemaVersion,
    String tenantId,
    String organizationId,
    Instant effectiveFrom,
    Instant effectiveUntil,              // nullable

    List<String> enabledStages,
    List<String> disabledStages,
    OptimizationDepth optimizationDepth,
    boolean requireNetSavings,

    List<String> modelAllowlist,
    List<String> modelDenylist,
    String defaultModelId,
    boolean cascadeEnabled,

    Integer maxInputTokens,               // nullable
    Integer maxOutputTokens,              // nullable
    Double maxCostPerRequest,             // nullable
    Double maxMonthlyCost,                // nullable

    Integer maxE2eLatencyMs,              // nullable
    Integer maxOptimizationOverheadMs,    // nullable

    double minQualityScore,
    double minTaskSuccessRate,
    double maxRegressionDelta,

    boolean exactCacheEnabled,
    boolean semanticCacheEnabled,
    double semanticSimilarityThreshold,
    boolean cachePiiProhibited,

    PiiHandling piiHandling,
    List<String> dataResidency,           // nullable

    List<String> complianceFrameworks,
    double rolloutPct,
    boolean shadowMode,

    // per_workflow_overrides / per_intent_overrides deferred — see class Javadoc, SOURCE-GAP-EXECPLAN-33

    String createdBy,
    String approvedBy,                    // nullable
    String changeReason
) {

    public OptimizationPolicy {
        Objects.requireNonNull(policyId, "policyId");
        Objects.requireNonNull(policyVersion, "policyVersion");
        Objects.requireNonNull(schemaVersion, "schemaVersion");
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(organizationId, "organizationId");
        Objects.requireNonNull(effectiveFrom, "effectiveFrom");
        Objects.requireNonNull(optimizationDepth, "optimizationDepth");
        Objects.requireNonNull(defaultModelId, "defaultModelId");
        Objects.requireNonNull(piiHandling, "piiHandling");
        Objects.requireNonNull(createdBy, "createdBy");
        Objects.requireNonNull(changeReason, "changeReason");

        enabledStages = enabledStages == null ? List.of() : List.copyOf(enabledStages);
        disabledStages = disabledStages == null ? List.of() : List.copyOf(disabledStages);
        modelAllowlist = modelAllowlist == null ? List.of() : List.copyOf(modelAllowlist);
        modelDenylist = modelDenylist == null ? List.of() : List.copyOf(modelDenylist);
        complianceFrameworks = complianceFrameworks == null ? List.of() : List.copyOf(complianceFrameworks);
        dataResidency = dataResidency == null ? null : List.copyOf(dataResidency);
    }

    /** {@code optimization_depth: enum { MINIMAL, STANDARD, DEEP }} (§20.1's own local declaration). */
    public enum OptimizationDepth {
        MINIMAL,
        STANDARD,
        DEEP
    }

    /** {@code pii_handling: enum { SCRUB, BLOCK, PASSTHROUGH_WITH_AUDIT }} */
    public enum PiiHandling {
        SCRUB,
        BLOCK,
        PASSTHROUGH_WITH_AUDIT
    }
}
