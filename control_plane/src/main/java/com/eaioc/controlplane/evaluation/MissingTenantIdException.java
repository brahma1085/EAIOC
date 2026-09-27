package com.eaioc.controlplane.evaluation;

/**
 * Thrown when a {@link BaselineEvaluationRecord} would be constructed without a non-blank
 * {@code tenantId} (root {@code CLAUDE.md} rule 4: "every ... cost record ... must be scoped by
 * {@code tenant_id}"). Mirrors the same-named, deliberately separate exception classes already in
 * {@code core.schemas} and {@code accounting.ledger} — this repository's established pattern is one
 * small, package-local exception per package that constructs a tenant-scoped record, rather than one
 * shared cross-package type, matching {@code conventions.md} §2.2's dependency-direction discipline.
 */
public class MissingTenantIdException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MissingTenantIdException(String message) {
        super(message);
    }
}
