package com.eaioc.controlplane.core.schemas;

/**
 * {@code ControlPlaneRequest.request_type} — {@code interfaces.md} §2.1 (REQUIRED; "Drives
 * optimization path", §2.3). Constants spelled exactly as in the source.
 */
public enum RequestType {
    GENERIC_LLM,
    AGENTIC_WORKFLOW,
    DEVELOPER_CODING,
    MULTI_AGENT,
    RAG,
    TOOL_ENABLED,
    BATCH,
    SUB_AGENT
}
