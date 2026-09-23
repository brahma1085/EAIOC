/**
 * The {@code ControlPlaneError} taxonomy ({@code conventions.md} §2.1 {@code core/errors/};
 * {@code interfaces.md} §25), realized at P0 per {@code control_plane/core/CoreFoundation.md}.
 *
 * <p>Contents: {@link com.eaioc.controlplane.core.errors.ControlPlaneError} and
 * {@link com.eaioc.controlplane.core.errors.ErrorClass}. {@code PartialSuccess} (§25.3) is not
 * realized at P0 (design note §4.1).
 *
 * <p><b>Dependency rule:</b> may depend only on {@code core/interfaces/} and {@code core/schemas/}
 * (design note §9 — the most restrictive reading consistent with {@code conventions.md} §2.2,
 * which states no explicit rule for {@code errors/}), plus the JDK.
 */
package com.eaioc.controlplane.core.errors;
