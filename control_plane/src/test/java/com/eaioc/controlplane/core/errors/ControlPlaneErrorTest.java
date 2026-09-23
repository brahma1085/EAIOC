package com.eaioc.controlplane.core.errors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@code REM-P0.1.A-02} covering {@link ControlPlaneError} and {@link ErrorClass}, per
 * {@code control_plane/core/CoreFoundation.md} §10 and decision D5: a code/class pair matching the
 * {@code interfaces.md} §25.2 taxonomy is accepted; a mismatched or malformed code is rejected.
 */
class ControlPlaneErrorTest {

    /** Every row of {@code interfaces.md} §25.2, transcribed. */
    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
        "OPT-1001, VALIDATION",
        "OPT-2001, POLICY",
        "OPT-3001, SECURITY",
        "OPT-4001, PROVIDER",
        "OPT-5001, TIMEOUT",
        "OPT-6001, CAPACITY",
        "OPT-7001, INTERNAL",
        "OPT-8001, CONFIGURATION"
    })
    void codeMatchingItsClassSeries_isAccepted(String code, ErrorClass errorClass) {
        ControlPlaneError error = error(code, errorClass);

        assertThat(error.errorCode()).isEqualTo(code);
        assertThat(error.errorClass()).isEqualTo(errorClass);
    }

    @Test
    void codeFromAnotherClassSeries_isRejected() {
        assertThatThrownBy(() -> error("OPT-4001", ErrorClass.VALIDATION))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("OPT-4001");
    }

    @ParameterizedTest
    @ValueSource(strings = {"OPT-9001", "OPT-0001", "OPT-101", "OPT-10001", "opt-1001", "ERR-1001", ""})
    void malformedCode_isRejected(String code) {
        assertThatThrownBy(() -> error(code, ErrorClass.VALIDATION))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullCodeOrNullClass_isRejected() {
        assertThatThrownBy(() -> error(null, ErrorClass.VALIDATION))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> error("OPT-1001", null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void errorClass_matchesSection25_1InSourceOrder() {
        assertThat(Arrays.stream(ErrorClass.values()).map(Enum::name).toList()).containsExactly(
            "VALIDATION", "POLICY", "PROVIDER", "TIMEOUT", "CAPACITY", "INTERNAL", "SECURITY",
            "CONFIGURATION");
    }

    @Test
    void details_areDefensivelyCopied_preserveNullValues_andNullBecomesEmpty() {
        Map<String, Object> details = new HashMap<>();
        details.put("stage", null);
        ControlPlaneError error = new ControlPlaneError(
            "err-1", "OPT-1001", ErrorClass.VALIDATION, "bad", false, null, false, null,
            "request-1", "span-1", Instant.parse("2026-09-23T00:00:00Z"), details);
        details.put("added-later", 1);

        assertThat(error.details()).containsOnlyKeys("stage");
        assertThat(error(("OPT-1001"), ErrorClass.VALIDATION).details()).isEmpty();
    }

    private static ControlPlaneError error(String code, ErrorClass errorClass) {
        return new ControlPlaneError(
            "err-1", code, errorClass, "message", true, 1000, false, null,
            "request-1", "span-1", Instant.parse("2026-09-23T00:00:00Z"), null);
    }
}
