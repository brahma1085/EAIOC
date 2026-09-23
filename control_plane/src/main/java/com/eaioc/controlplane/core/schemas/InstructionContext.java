package com.eaioc.controlplane.core.schemas;

/**
 * System/developer/user instruction content — {@code interfaces.md} §2.2 {@code InstructionContext},
 * realized field-for-field. Referenced by {@link ControlPlaneRequest#systemContext()} and
 * {@link ControlPlaneRequest#developerContext()}.
 */
public record InstructionContext(
    String content,
    Type type,
    Stability stability,
    int tokenEstimate,
    String version          // nullable — "version: string | null"
) {

    /** {@code type: enum { SYSTEM, DEVELOPER, USER }} */
    public enum Type {
        SYSTEM,
        DEVELOPER,
        USER
    }

    /** {@code stability: enum { STABLE, SEMI_STABLE, VOLATILE }} */
    public enum Stability {
        STABLE,
        SEMI_STABLE,
        VOLATILE
    }
}
