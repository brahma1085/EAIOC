package com.eaioc.controlplane.output_controls;

import java.util.List;
import java.util.Objects;

/**
 * The already-resolved output-structure/schema choice {@link OutputController} accepts as a
 * parameter and validates a {@link CandidateOutput} against.
 *
 * <p><b>HD-CG-P0.6-02 (2026-10-08):</b> {@code architecture.md} §12.3's entire purpose is
 * intent-keyed output-structure selection ("Select output structure based on intent"), but
 * {@code ControlPlaneRequest} carries no intent-classification field anywhere in the corpus
 * ({@code requestType} is an execution-environment enum, not the Factual/Comparative/Procedural/
 * Extraction/Coding/Agent-tool taxonomy §12.3's own table keys on — the same {@code CORE-GAP-01}
 * shape that made Capability 5's "select intent-specific instructions" responsibility vacuously
 * satisfied). {@link OutputController} therefore never performs §12.3's intent-based selection
 * itself; it accepts an already-resolved schema as an input parameter. Selection logic is deferred,
 * registered as {@code SOURCE-GAP-EXECPLAN-37}.
 *
 * <p><b>HD-CG-P0.6-05 (2026-10-08):</b> {@code JSONSchema} is referenced (e.g. {@code interfaces.md}
 * §7.2) but never elaborated anywhere in the corpus. This is the smallest workable representation —
 * a required-field-name-plus-type-check list — explicitly NOT a full JSON Schema validator (no
 * nested structure, no format/pattern/enum constraints, no optional-field typing).
 *
 * @param requiredFields every field a conformant {@link CandidateOutput} must carry, with its
 *     expected runtime type
 */
public record OutputSchema(List<RequiredField> requiredFields) {

    public OutputSchema {
        Objects.requireNonNull(requiredFields, "requiredFields");
        requiredFields = List.copyOf(requiredFields);
    }

    /**
     * One required field: present under {@code name} in {@link CandidateOutput#fields()}, with a
     * value assignable to {@code type}.
     */
    public record RequiredField(String name, Class<?> type) {

        public RequiredField {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(type, "type");
        }
    }
}
