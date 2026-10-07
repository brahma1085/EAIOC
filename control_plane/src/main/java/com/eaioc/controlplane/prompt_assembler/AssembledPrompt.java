package com.eaioc.controlplane.prompt_assembler;

import java.util.Objects;

/**
 * EXE-P0.5.B (Capability 5, Prompt Assembler — Minimal Implementation): the output of {@link
 * PromptAssembler#assemble}, named by {@code execution-plan.md} §18.8's B row, human decision
 * HD-CG-P0.5-01.
 *
 * <p><b>Not a realization of any {@code interfaces.md} schema — none exists.</b> {@code
 * architecture.md} §12.2 lists "Produce a complete assembly ledger" as a Prompt Assembler
 * responsibility, but no `AssemblyLedger` type, or any other schema for a Prompt Assembler output,
 * is defined anywhere in the corpus (confirmed by exhaustive grep, independently re-confirmed by
 * the {@code EXE-P0.5.B} Architect pre-flight). HD-CG-P0.5-01 (2026-10-07) resolves this: this type
 * carries only {@code architecture.md} §12.2's three explicit structural regions plus a final token
 * count. The "assembly ledger" responsibility itself is deferred, not invented — {@code
 * SOURCE-GAP-EXECPLAN-36}.
 *
 * <p>Each region is always non-null; a region with nothing to carry is an empty string, never
 * {@code null} — honestly representing "nothing populated this region" (e.g. {@code
 * semiStableRegion}, which is always empty at P0 scope per HD-CG-P0.5-04: no retrieval/memory layer
 * exists anywhere in P0, {@code CORE-GAP-01}), rather than conflating "empty" with "absent."
 */
public record AssembledPrompt(
    String cacheablePrefix,
    String semiStableRegion,
    String volatileSuffix,
    int finalTokenCount
) {

    public AssembledPrompt {
        Objects.requireNonNull(cacheablePrefix, "cacheablePrefix");
        Objects.requireNonNull(semiStableRegion, "semiStableRegion");
        Objects.requireNonNull(volatileSuffix, "volatileSuffix");
    }
}
