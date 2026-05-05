/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.exception;

import java.util.List;

/**
 * Structured details from a NoviCloud HTTP 400 error response.
 *
 * <p>The NoviCloud REST API echoes back rejected query parameters in two arrays:
 * <ul>
 *   <li>{@code par_niewlasciwe} - parameter names that the server does not recognise
 *       (typo, undocumented, or removed in this API version)</li>
 *   <li>{@code par_bledna_wart} - parameter names whose values were rejected
 *       (wrong format, out of range, or one of the broken-server-side cases
 *       documented in ADR-031)</li>
 * </ul>
 *
 * <p>Available via {@link NoviCloudException#getErrorDetails()} for callers that
 * need to surface validation errors in a UI or routing logic without parsing
 * the raw {@link NoviCloudException#getResponseBody()} themselves.
 *
 * @param parNiewlasciwe rejected parameter names; never {@code null}, may be empty
 * @param parBlednaWart  rejected parameter values; never {@code null}, may be empty
 * @since 2.0.0
 */
public record NoviCloudErrorDetails(
        List<String> parNiewlasciwe,
        List<String> parBlednaWart
)
{

    private static final List<String> EMPTY = List.of();

    /** Canonical constructor that nullsafe-coerces {@code null} into empty lists. */
    public NoviCloudErrorDetails {
        parNiewlasciwe = parNiewlasciwe == null ? EMPTY : List.copyOf(parNiewlasciwe);
        parBlednaWart = parBlednaWart == null ? EMPTY : List.copyOf(parBlednaWart);
    }
}
