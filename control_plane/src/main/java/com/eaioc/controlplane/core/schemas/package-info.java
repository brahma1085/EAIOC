/**
 * Shared schema types ({@code conventions.md} §2.1 {@code core/schemas/}), realized at P0 per
 * {@code control_plane/core/CoreFoundation.md} (remediation {@code REM-P0.1.A-01}, approved with
 * Amendment 1).
 *
 * <p>Contents: {@link com.eaioc.controlplane.core.schemas.ControlPlaneRequest} and its realized
 * sub-types ({@code interfaces.md} §2), {@link com.eaioc.controlplane.core.schemas.OptimizationPlan}
 * and its realized sub-types (§3.2, §3.4, §43.10), and
 * {@link com.eaioc.controlplane.core.schemas.MissingTenantIdException}. Fields whose types are
 * undefined upstream are deliberately absent — see the design note's {@code CORE-GAP-01}–{@code 04}.
 *
 * <p><b>Dependency rule ({@code conventions.md} §2.2):</b> depends only on {@code core/interfaces/}
 * and the JDK — never on {@code core/errors/} or any other {@code com.eaioc.controlplane} package.
 * These are plain immutable data types, not Spring components.
 */
package com.eaioc.controlplane.core.schemas;
