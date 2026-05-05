/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

/**
 * Immutable SDK model for a store-specific price entry on a product.
 *
 * <p>Returned by {@link Towar#cenyWSklepach()}. The {@code sklepId} identifies the
 * shop the prices apply to; the price fields override the global product prices
 * when present.
 *
 * @param sklepId         the shop ID this price entry applies to; {@code null} if absent
 * @param cenaEw          inventory net price; {@code null} if not set
 * @param cenaDet         retail price; {@code null} if not set
 * @param cenaHurt        wholesale price; {@code null} if not set
 * @param cenaNoc         night price; {@code null} if not set
 * @param cenaDod         additional price; {@code null} if not set
 * @param przySprzedazy   sale-time behavior code; {@code null} if not set
 * @since 2.0.0
 */
public record TowarCenaWSklepie(
        String sklepId,
        Double cenaEw,
        Double cenaDet,
        Double cenaHurt,
        Double cenaNoc,
        Double cenaDod,
        Integer przySprzedazy
)
{
}
