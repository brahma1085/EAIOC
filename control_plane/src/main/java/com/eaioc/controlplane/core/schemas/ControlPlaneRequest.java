package com.eaioc.controlplane.core.schemas;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * The canonical normalized request entering the Control Plane — {@code interfaces.md} §2.1
 * (INTF-001 entrypoint schema), realized at P0 per {@code control_plane/core/CoreFoundation.md}.
 *
 * <p><b>P0 subset (design note D1, §2.1):</b> every field whose type is defined upstream is
 * realized, in source order. Seven source fields are deliberately absent because their types are
 * undefined anywhere in the corpus — {@code conversation_history} ({@code CORE-GAP-02}),
 * {@code retrieved_context}, {@code memory_references}, {@code available_tools},
 * {@code model_preferences}, {@code optimization_hints} ({@code CORE-GAP-01}). All seven are
 * nullable in the source, so no REQUIRED input is dropped; each is added by the capability that
 * first consumes it, once its type is defined.
 *
 * <p><b>REQUIRED-field enforcement (design note D4, §2.1/§2.3):</b> the compact constructor rejects
 * — never defaults — a missing or blank {@code tenantId} with {@link MissingTenantIdException}
 * (root {@code CLAUDE.md} rule 4), and throws {@link IllegalArgumentException} for every other
 * REQUIRED violation: blank/missing identity fields and {@code taskId}; missing
 * {@code requestType}, {@code qualityRequirements}, {@code latencyRequirements},
 * {@code securityClassification}; and a {@code null} {@code userInput} unless {@code requestType}
 * is {@link RequestType#BATCH} (§2.3: "REQUIRED (non-batch)").
 *
 * <p><b>Collections (D3):</b> {@code complianceRequirements} and {@code metadata} are non-nullable
 * in the source; {@code null} becomes empty and both are defensively copied so a caller's mutable
 * collection cannot alter a constructed request.
 */
public record ControlPlaneRequest(
    // IDENTITY (all REQUIRED)
    String schemaVersion,
    String requestId,
    String correlationId,
    String tenantId,
    String organizationId,

    // ROUTING SCOPE
    String applicationId,
    String agentId,                 // nullable
    String sessionId,               // nullable
    String workflowId,              // nullable
    String taskId,                  // REQUIRED
    String parentTaskId,            // nullable
    int iterationNumber,            // 0 for first call

    // REQUEST CLASSIFICATION (REQUIRED)
    RequestType requestType,

    // CONTENT
    String userInput,               // REQUIRED unless requestType == BATCH
    InstructionContext systemContext,       // nullable
    InstructionContext developerContext,    // nullable
    AgentState agentState,                  // nullable

    // REQUIREMENTS
    QualityRequirements qualityRequirements,    // REQUIRED
    LatencyRequirements latencyRequirements,    // REQUIRED
    BudgetConstraints budgetConstraints,        // nullable

    // GOVERNANCE
    SecurityClassification securityClassification, // REQUIRED
    List<String> complianceRequirements,
    FreshnessRequirements freshnessRequirements,    // nullable

    // OPTIMIZATION HINTS
    boolean bypassOptimization,     // source: "Default false"

    // METADATA
    Map<String, String> metadata,
    Instant timestamp,
    Integer ttlMs                   // nullable
) {

    public ControlPlaneRequest {
        // tenant_id first: the tenant-isolation violation gets its own dedicated type.
        if (tenantId == null || tenantId.isBlank()) {
            throw new MissingTenantIdException(
                "ControlPlaneRequest requires a non-blank tenantId (root CLAUDE.md rule 4); "
                    + "requestId=" + requestId);
        }
        requireNonBlank(schemaVersion, "schemaVersion");
        requireNonBlank(requestId, "requestId");
        requireNonBlank(correlationId, "correlationId");
        requireNonBlank(organizationId, "organizationId");
        requireNonBlank(taskId, "taskId");
        requirePresent(requestType, "requestType");
        requirePresent(qualityRequirements, "qualityRequirements");
        requirePresent(latencyRequirements, "latencyRequirements");
        requirePresent(securityClassification, "securityClassification");
        if (userInput == null && requestType != RequestType.BATCH) {
            throw new IllegalArgumentException(
                "ControlPlaneRequest.userInput is REQUIRED for non-batch requests "
                    + "(interfaces.md §2.3); requestType=" + requestType
                    + ", requestId=" + requestId);
        }
        complianceRequirements =
            complianceRequirements == null ? List.of() : List.copyOf(complianceRequirements);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    private static void requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                "ControlPlaneRequest." + field + " is REQUIRED and must be non-blank "
                    + "(interfaces.md §2.1/§2.3)");
        }
    }

    private static void requirePresent(Object value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(
                "ControlPlaneRequest." + field + " is REQUIRED (interfaces.md §2.1/§2.3)");
        }
    }
}
