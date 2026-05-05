/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

/**
 * Immutable SDK model for the {@code stanmag} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record StanMag(
        Double ilosc,
        Double wCZakNetto,
        Double wCZakBrutto,
        Double wCSprzedNetto,
        Double wCSprzedBrutto,
        String towarId,
        String sklepId
)
{
}
