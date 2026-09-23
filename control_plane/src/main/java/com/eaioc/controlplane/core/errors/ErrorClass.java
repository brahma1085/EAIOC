package com.eaioc.controlplane.core.errors;

/**
 * {@code ControlPlaneError.error_class} — {@code interfaces.md} §25.1, constants in source order.
 *
 * <p>Each constant carries its §25.2 error-code series digit ({@code OPT-Nxxx}), so the code/class
 * consistency rule is taken directly from the source table rather than hard-coded elsewhere
 * (design note D5).
 */
public enum ErrorClass {
    VALIDATION(1),      // OPT-1xxx — Request schema invalid
    POLICY(2),          // OPT-2xxx — Request blocked by policy
    PROVIDER(4),        // OPT-4xxx — LLM provider unavailable
    TIMEOUT(5),         // OPT-5xxx — Stage exceeded timeout
    CAPACITY(6),        // OPT-6xxx — Throttling / rate limit
    INTERNAL(7),        // OPT-7xxx — Unexpected internal error
    SECURITY(3),        // OPT-3xxx — Auth/PII failure
    CONFIGURATION(8);   // OPT-8xxx — Bad policy or config

    private final int codeSeries;

    ErrorClass(int codeSeries) {
        this.codeSeries = codeSeries;
    }

    /** The {@code N} in this class's {@code OPT-Nxxx} error-code series (§25.2). */
    public int codeSeries() {
        return codeSeries;
    }
}
