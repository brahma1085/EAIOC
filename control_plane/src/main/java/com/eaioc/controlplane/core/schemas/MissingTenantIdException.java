package com.eaioc.controlplane.core.schemas;

/**
 * Thrown when a {@link ControlPlaneRequest} is constructed without a non-blank {@code tenantId}.
 *
 * <p>Root {@code CLAUDE.md} rule 4 makes tenant isolation absolute, so a request without a tenant
 * is rejected at construction rather than silently defaulted. The type is dedicated (not a plain
 * {@link IllegalArgumentException}, which every other missing REQUIRED field throws) so callers can
 * distinguish the tenant-isolation violation specifically.
 *
 * <p>Lives in {@code core/schemas/}, not {@code core/errors/}: {@code conventions.md} §2.2 forbids
 * {@code core/schemas/} from importing {@code core/errors/} ({@code CoreFoundation.md} §12,
 * Amendment 1). Deliberately separate from Capability 1's
 * {@code accounting.ledger.MissingTenantIdException}, which is not migrated (design note D7).
 */
public final class MissingTenantIdException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public MissingTenantIdException(String message) {
        super(message);
    }
}
