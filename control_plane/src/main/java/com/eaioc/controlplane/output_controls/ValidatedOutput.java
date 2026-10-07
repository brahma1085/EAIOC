package com.eaioc.controlplane.output_controls;

import java.util.Objects;

/**
 * A {@link CandidateOutput} that passed {@link OutputController#enforce} schema validation, with
 * whole-response length enforcement applied.
 *
 * <p>{@code architecture.md} §12.4's truncation handling requires marking truncation explicitly —
 * {@link #truncated()} is that marker. "Prefer semantic boundaries" and "permit controlled
 * expansion if quality gate requires it" are P1+/`TECH-018` territory (no quality gate exists in
 * P0 to trigger expansion) and are not realized here.
 *
 * <p><b>{@code EC-050}/{@code EC-051} (flagged forward by the approved {@code EXE-P0.6.A} design
 * note, {@code OutputControlsContract.md} §7 item 7, discharged here):</b> {@code edge-cases.md}'s
 * {@code EC-050} (Output Schema Truncates Required Information) and {@code EC-051} (Output Length
 * Control Cuts Code Mid-Statement) are the two dedicated, HIGH-severity edge cases for this
 * component, both cited by {@code optimization-catalog.md}'s {@code TECH-017}/{@code TECH-018}.
 * {@link OutputController} truncates at a raw token boundary — no semantic-boundary detection, no
 * completeness gate, no distinguishable {@code OUTPUT_TRUNCATED} status beyond {@link #truncated()}
 * itself. This is consistent with, not a gap introduced by, this capability's own documented P0
 * scope ({@code execution-plan.md} §18.9: "this capability's own P0 scope is the minimal enforcement
 * substrate" {@code TECH-017}/{@code TECH-018} build on) — full EC-050/EC-051 remediation (semantic
 * boundaries, completeness scoring, controlled expansion) is P1-tier {@code TECH-017}/{@code
 * TECH-018} territory, not implemented by this sub-phase. **Registered as a non-blocking P0 gap,
 * {@code SOURCE-GAP-EXECPLAN-39}** (per Reviewer finding, 2026-10-08, that a HIGH-severity
 * catalogued edge case this sub-phase's own governing design note required it to address must be
 * either fixed or explicitly, ID-cited disclosed — not left to a generic "P1 territory" footnote).
 *
 * <p><b>{@code fields()} are not carried forward (Reviewer finding, 2026-10-08, disclosed, not
 * yet decided):</b> {@link OutputController#enforce} validates {@link CandidateOutput#fields()}
 * against the schema but does not propagate them into this type — a caller that needs the
 * validated structured fields (plausible, since §12.3's whole purpose is intent-keyed
 * <em>structured</em> output) cannot retrieve them from a {@link ValidatedOutput}. Left as an
 * explicit open item for whichever future sub-phase (most likely C, "Integration") first needs
 * them from a real downstream consumer, rather than speculatively adding an unused field now.
 *
 * @param content the final response content — unchanged if no truncation was needed, or the
 *     token-boundary-truncated text otherwise
 * @param truncated whether {@code content} was shortened to fit a budget
 * @param finalTokenCount the token count of {@code content} as actually returned
 */
public record ValidatedOutput(String content, boolean truncated, int finalTokenCount) {

    public ValidatedOutput {
        Objects.requireNonNull(content, "content");
    }
}
