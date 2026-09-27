package com.eaioc.controlplane.benchmarking;

/**
 * Thrown by {@link BaselineRunner#runBaseline} when a valid baseline measurement cannot be acquired —
 * the decided outcome for that situation (execution-plan.md §18.14.8, "No measurement, no record — the
 * operation fails", human contract decision of 2026-09-25; {@code interfaces.md} §18 P0 realization
 * notes). {@code run_baseline()}'s declared return type stays non-null: this is the unit-local failure
 * mechanism that decision explicitly leaves open ("the concrete failure mechanism ... is a unit-level
 * implementation concern until an authoritative error-transport contract exists"), and it introduces no
 * new public {@code ControlPlaneError} code, no {@code error_class} value, no nullable
 * {@code BaselineEvaluationRecord}, no outcome wrapper, and no {@code PartialSuccess} — exactly the
 * things that decision forbids. It changes no public contract, so it does not by itself require
 * {@code AI VERIFICATION: BLOCKED} (§18.14.8, "Blocking rule").
 *
 * <p>Thrown for all three "no measurement" situations alike (zero matching ledger entries, several
 * matching entries, or a single matching entry that is not {@code verified = true}) — per the D-A
 * retrieval rule, these are deliberately not distinguished by a public error code or schema.
 */
public class NoBaselineMeasurementException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoBaselineMeasurementException(String message) {
        super(message);
    }
}
