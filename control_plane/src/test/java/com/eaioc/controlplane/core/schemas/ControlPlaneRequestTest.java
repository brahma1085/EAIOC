package com.eaioc.controlplane.core.schemas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@code REM-P0.1.A-02} (shared core foundation, Minimal Scaffold remediation) covering
 * {@link ControlPlaneRequest}, per {@code control_plane/core/CoreFoundation.md} §10: round-trip,
 * REQUIRED-field failure paths (D4, including the dedicated {@link MissingTenantIdException} and the
 * BATCH-only exemption for {@code userInput}), and defensive copies (D3).
 */
class ControlPlaneRequestTest {

    @Test
    void constructWithAllRequiredFields_roundTripsUnchanged() {
        Builder b = new Builder();
        ControlPlaneRequest request = b.build();

        assertThat(request.schemaVersion()).isEqualTo("1.0.0");
        assertThat(request.requestId()).isEqualTo("request-1");
        assertThat(request.correlationId()).isEqualTo("correlation-1");
        assertThat(request.tenantId()).isEqualTo("tenant-a");
        assertThat(request.organizationId()).isEqualTo("org-1");
        assertThat(request.taskId()).isEqualTo("task-1");
        assertThat(request.requestType()).isEqualTo(RequestType.GENERIC_LLM);
        assertThat(request.userInput()).isEqualTo("hello");
        assertThat(request.qualityRequirements()).isEqualTo(b.qualityRequirements);
        assertThat(request.latencyRequirements()).isEqualTo(b.latencyRequirements);
        assertThat(request.securityClassification()).isEqualTo(b.securityClassification);
        assertThat(request.complianceRequirements()).containsExactly("SOC2");
        assertThat(request.metadata()).containsEntry("k", "v");
        assertThat(request.timestamp()).isEqualTo(Instant.parse("2026-09-23T00:00:00Z"));
        assertThat(request.bypassOptimization()).isFalse();
        // Optional fields left null stay null — never defaulted to something invented.
        assertThat(request.agentId()).isNull();
        assertThat(request.budgetConstraints()).isNull();
        assertThat(request.agentState()).isNull();
        assertThat(request.ttlMs()).isNull();
    }

    @Test
    void allOptionalRealizedFields_roundTripUnchanged() {
        InstructionContext system = new InstructionContext(
            "be concise", InstructionContext.Type.SYSTEM, InstructionContext.Stability.STABLE, 3, "v1");
        AgentState agentState = new AgentState(
            AgentState.Status.RUNNING, "objective", List.of("a"), List.of("b"), List.of(), List.of(), null);
        BudgetConstraints budget = new BudgetConstraints(1000, 200, 0.5, null);
        FreshnessRequirements freshness =
            new FreshnessRequirements(60, true, FreshnessRequirements.StalenessAction.REJECT);

        ControlPlaneRequest request = new Builder().with(b -> {
            b.systemContext = system;
            b.agentState = agentState;
            b.budgetConstraints = budget;
            b.freshnessRequirements = freshness;
            b.ttlMs = 5000;
            b.agentId = "agent-1";
        }).build();

        assertThat(request.systemContext()).isEqualTo(system);
        assertThat(request.agentState()).isEqualTo(agentState);
        assertThat(request.budgetConstraints()).isEqualTo(budget);
        assertThat(request.freshnessRequirements()).isEqualTo(freshness);
        assertThat(request.ttlMs()).isEqualTo(5000);
        assertThat(request.agentId()).isEqualTo("agent-1");
    }

    @Test
    void nullTenantId_isRejectedWithDedicatedException_notDefaulted() {
        assertThatThrownBy(() -> new Builder().with(b -> b.tenantId = null).build())
            .isExactlyInstanceOf(MissingTenantIdException.class);
    }

    @Test
    void blankTenantId_isRejectedWithDedicatedException_notDefaulted() {
        assertThatThrownBy(() -> new Builder().with(b -> b.tenantId = "   ").build())
            .isExactlyInstanceOf(MissingTenantIdException.class);
    }

    static Stream<Arguments> otherRequiredFieldViolations() {
        return Stream.of(
            Arguments.of("schemaVersion null", (Consumer<Builder>) b -> b.schemaVersion = null),
            Arguments.of("schemaVersion blank", (Consumer<Builder>) b -> b.schemaVersion = " "),
            Arguments.of("requestId null", (Consumer<Builder>) b -> b.requestId = null),
            Arguments.of("requestId blank", (Consumer<Builder>) b -> b.requestId = ""),
            Arguments.of("correlationId null", (Consumer<Builder>) b -> b.correlationId = null),
            Arguments.of("organizationId null", (Consumer<Builder>) b -> b.organizationId = null),
            Arguments.of("organizationId blank", (Consumer<Builder>) b -> b.organizationId = " "),
            Arguments.of("taskId null", (Consumer<Builder>) b -> b.taskId = null),
            Arguments.of("taskId blank", (Consumer<Builder>) b -> b.taskId = ""),
            Arguments.of("requestType null", (Consumer<Builder>) b -> b.requestType = null),
            Arguments.of("qualityRequirements null", (Consumer<Builder>) b -> b.qualityRequirements = null),
            Arguments.of("latencyRequirements null", (Consumer<Builder>) b -> b.latencyRequirements = null),
            Arguments.of("securityClassification null", (Consumer<Builder>) b -> b.securityClassification = null),
            Arguments.of("userInput null for non-batch", (Consumer<Builder>) b -> b.userInput = null));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("otherRequiredFieldViolations")
    void otherRequiredFieldViolation_isRejected_notDefaulted(String label, Consumer<Builder> violation) {
        assertThatThrownBy(() -> new Builder().with(violation).build())
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullUserInput_isAcceptedOnlyForBatch() {
        ControlPlaneRequest batch = new Builder().with(b -> {
            b.requestType = RequestType.BATCH;
            b.userInput = null;
        }).build();

        assertThat(batch.userInput()).isNull();
        assertThat(batch.requestType()).isEqualTo(RequestType.BATCH);
    }

    @Test
    void collections_areDefensivelyCopied_callerMutationDoesNotLeak() {
        List<String> compliance = new ArrayList<>(List.of("SOC2"));
        Map<String, String> metadata = new HashMap<>(Map.of("k", "v"));
        ControlPlaneRequest request = new Builder().with(b -> {
            b.complianceRequirements = compliance;
            b.metadata = metadata;
        }).build();

        compliance.add("HIPAA");
        metadata.put("k2", "v2");

        assertThat(request.complianceRequirements()).containsExactly("SOC2");
        assertThat(request.metadata()).containsOnlyKeys("k");
        assertThatThrownBy(() -> request.complianceRequirements().add("x"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void nullNonNullableCollections_becomeEmpty() {
        ControlPlaneRequest request = new Builder().with(b -> {
            b.complianceRequirements = null;
            b.metadata = null;
        }).build();

        assertThat(request.complianceRequirements()).isEmpty();
        assertThat(request.metadata()).isEmpty();
    }

    @Test
    void nestedSubTypeCollections_areDefensivelyCopied() {
        List<String> pii = new ArrayList<>(List.of("EMAIL"));
        List<String> completed = new ArrayList<>(List.of("step-1"));
        SecurityClassification classification =
            new SecurityClassification(SecurityClassification.Level.CONFIDENTIAL, true, pii, null);
        AgentState state = new AgentState(
            AgentState.Status.PAUSED, "obj", completed, null, null, null, null);

        pii.add("PHONE");
        completed.add("step-2");

        assertThat(classification.piiCategories()).containsExactly("EMAIL");
        assertThat(classification.dataResidency()).isNull();   // nullable in source: stays null
        assertThat(state.completedActions()).containsExactly("step-1");
        assertThat(state.pendingActions()).isEmpty();          // non-nullable in source: null -> empty
    }

    /** Mutable test-only builder so each test varies exactly one field of a valid request. */
    static final class Builder {
        String schemaVersion = "1.0.0";
        String requestId = "request-1";
        String correlationId = "correlation-1";
        String tenantId = "tenant-a";
        String organizationId = "org-1";
        String applicationId = "app-1";
        String agentId;
        String sessionId;
        String workflowId;
        String taskId = "task-1";
        String parentTaskId;
        int iterationNumber;
        RequestType requestType = RequestType.GENERIC_LLM;
        String userInput = "hello";
        InstructionContext systemContext;
        InstructionContext developerContext;
        AgentState agentState;
        QualityRequirements qualityRequirements =
            new QualityRequirements(0.9, QualityRequirements.QualityTier.STANDARD, false, true);
        LatencyRequirements latencyRequirements = new LatencyRequirements(2000, 500, true);
        BudgetConstraints budgetConstraints;
        SecurityClassification securityClassification =
            new SecurityClassification(SecurityClassification.Level.INTERNAL, false, null, null);
        List<String> complianceRequirements = List.of("SOC2");
        FreshnessRequirements freshnessRequirements;
        boolean bypassOptimization;
        Map<String, String> metadata = Map.of("k", "v");
        Instant timestamp = Instant.parse("2026-09-23T00:00:00Z");
        Integer ttlMs;

        Builder with(Consumer<Builder> change) {
            change.accept(this);
            return this;
        }

        ControlPlaneRequest build() {
            return new ControlPlaneRequest(
                schemaVersion, requestId, correlationId, tenantId, organizationId,
                applicationId, agentId, sessionId, workflowId, taskId, parentTaskId, iterationNumber,
                requestType,
                userInput, systemContext, developerContext, agentState,
                qualityRequirements, latencyRequirements, budgetConstraints,
                securityClassification, complianceRequirements, freshnessRequirements,
                bypassOptimization,
                metadata, timestamp, ttlMs);
        }
    }
}
