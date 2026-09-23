package com.eaioc.controlplane.accounting.ledger;

/**
 * Thrown when a write targets an {@code entryId} that already exists for the given tenant.
 *
 * <p>The ledger is append-only per request ({@code docs/execution-plan.md} §12, Data Architecture:
 * "Versioning: Append-only per request") — a second write for the same {@code entryId} is rejected
 * rather than silently overwriting an earlier record, which would corrupt the cost history the
 * ledger exists to preserve.
 */
public final class DuplicateLedgerEntryException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    public DuplicateLedgerEntryException(String message) {
        super(message);
    }
}
