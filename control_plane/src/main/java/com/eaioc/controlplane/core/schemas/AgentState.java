package com.eaioc.controlplane.core.schemas;

import java.time.Instant;
import java.util.List;

/**
 * {@code interfaces.md} §11 {@code AgentState}, realized field-for-field. Optional on
 * {@link ControlPlaneRequest#agentState()}.
 *
 * <p>The four {@code string[]} fields are non-nullable in the source: {@code null} becomes an empty
 * list and every list is defensively copied (design note D3).
 */
public record AgentState(
    Status status,
    String currentObjective,
    List<String> completedActions,
    List<String> pendingActions,
    List<String> unresolvedQuestions,
    List<String> keyDecisions,
    Instant checkpointedAt          // nullable
) {

    public AgentState {
        completedActions = completedActions == null ? List.of() : List.copyOf(completedActions);
        pendingActions = pendingActions == null ? List.of() : List.copyOf(pendingActions);
        unresolvedQuestions = unresolvedQuestions == null ? List.of() : List.copyOf(unresolvedQuestions);
        keyDecisions = keyDecisions == null ? List.of() : List.copyOf(keyDecisions);
    }

    /** {@code status: enum { RUNNING, PAUSED, WAITING_FOR_TOOL, COMPLETED, FAILED }} */
    public enum Status {
        RUNNING,
        PAUSED,
        WAITING_FOR_TOOL,
        COMPLETED,
        FAILED
    }
}
