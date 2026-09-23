/**
 * Zero-dependency contract layer ({@code conventions.md} §2.1 {@code core/interfaces/}).
 *
 * <p>Eventually holds the shared INTF-NNN interface definitions. At P0 it is intentionally empty
 * apart from this file ({@code control_plane/core/CoreFoundation.md} decision D6): each interface
 * is placed by the unit that implements it — e.g. Capability 2's {@code EvaluationFramework}
 * (INTF-030) lives in {@code control_plane/evaluation/}, per {@code docs/execution-plan.md} §9.
 *
 * <p><b>Dependency rule ({@code conventions.md} §2.2):</b> this package has zero dependencies —
 * it imports nothing from any other {@code com.eaioc.controlplane} package and nothing beyond the
 * JDK.
 */
package com.eaioc.controlplane.core.interfaces;
