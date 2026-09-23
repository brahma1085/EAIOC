package com.eaioc.controlplane.core.errors;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Structured Control Plane error — {@code interfaces.md} §25.1 {@code ControlPlaneError}, realized
 * field-for-field.
 *
 * <p><b>Code/class consistency (design note D5):</b> the compact constructor rejects an
 * {@code errorCode} that is not of the form {@code OPT-Nxxx}, or whose series digit {@code N}
 * disagrees with {@code errorClass} under the §25.2 taxonomy (e.g. {@code OPT-4001} with
 * {@link ErrorClass#VALIDATION}). The mapping comes from the source, so enforcing it invents
 * nothing.
 *
 * <p>{@code details} ({@code map<string, any>}) is copied into an unmodifiable map preserving
 * {@code null} values; {@code null} becomes empty (D3).
 */
public record ControlPlaneError(
    String errorId,
    String errorCode,
    ErrorClass errorClass,
    String message,
    boolean retryable,
    Integer retryAfterMs,       // nullable
    boolean fallbackApplied,
    String fallbackAction,      // nullable
    String requestId,
    String spanId,
    Instant timestamp,
    Map<String, Object> details
) {

    private static final Pattern ERROR_CODE = Pattern.compile("OPT-([1-8])\\d{3}");

    public ControlPlaneError {
        if (errorClass == null) {
            throw new IllegalArgumentException(
                "ControlPlaneError.errorClass is required to validate errorCode (interfaces.md §25.2)");
        }
        Matcher matcher = errorCode == null ? null : ERROR_CODE.matcher(errorCode);
        if (matcher == null || !matcher.matches()) {
            throw new IllegalArgumentException(
                "ControlPlaneError.errorCode must match OPT-Nxxx with N in 1-8 "
                    + "(interfaces.md §25.2); errorCode=" + errorCode);
        }
        int series = Integer.parseInt(matcher.group(1));
        if (series != errorClass.codeSeries()) {
            throw new IllegalArgumentException(
                "ControlPlaneError.errorCode " + errorCode + " belongs to series OPT-" + series
                    + "xxx, but errorClass=" + errorClass + " requires OPT-"
                    + errorClass.codeSeries() + "xxx (interfaces.md §25.2)");
        }
        details = details == null
            ? Map.of()
            : Collections.unmodifiableMap(new LinkedHashMap<>(details));
    }
}
