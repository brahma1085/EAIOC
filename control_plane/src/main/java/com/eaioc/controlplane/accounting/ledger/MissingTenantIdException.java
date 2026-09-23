package com.eaioc.controlplane.accounting.ledger;

/**
 * Thrown when a {@link CostLedgerEntry} is constructed without a non-blank {@code tenantId}.
 *
 * <p>Unchecked, and deliberately local to this package rather than drawn from a shared
 * {@code core/errors/} taxonomy: {@code interfaces.md} §25's {@code ControlPlaneError} taxonomy is
 * a separate, broader contract this sub-phase's own scope (
 * {@code control_plane/accounting/ledger/} only, per {@code docs/execution-plan.md} §18.4's
 * {@code EXE-P0.1.B} row) does not include designing. Introducing that shared taxonomy now would be
 * scope creep beyond this unit — see {@code control_plane/accounting/ledger/LedgerEntry.md} §5 for
 * the same reasoning applied to Maven/{@code core/} scaffolding in Sub-phase A.
 */
public final class MissingTenantIdException extends IllegalArgumentException {

    public MissingTenantIdException(String message) {
        super(message);
    }
}
