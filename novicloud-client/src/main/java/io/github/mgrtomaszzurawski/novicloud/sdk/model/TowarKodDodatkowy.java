/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

/**
 * Immutable SDK model for an additional product barcode.
 *
 * <p>Returned by {@link Towar#kodyDod()}.
 *
 * @param kod        the additional barcode value
 * @param ileWOpak   how many units the barcode represents (e.g. multipack)
 * @param poziomCen  price level (1, 2, 3 or 4); {@code null} if unspecified
 * @since 2.0.0
 */
public record TowarKodDodatkowy(
        String kod,
        Double ileWOpak,
        Integer poziomCen
)
{
}
