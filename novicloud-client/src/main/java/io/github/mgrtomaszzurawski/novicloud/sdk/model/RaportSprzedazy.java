/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

/**
 * Immutable SDK model for the {@code raportsprzedazy} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record RaportSprzedazy(
        String kartaRabatowa,
        Double ilosc,
        Double sprzNetto,
        Double sprzBrutto,
        Double sprzZakNetto,
        Double sprzZakBrutto,
        Double marzaNetto,
        Double marzaBrutto,
        Double marzaProcNetto,
        Double marzaProcBrutto,
        Double narzutProcNetto,
        Double narzutProcBrutto,
        Double rabat,
        Double rabatProc,
        String towarId,
        String asortId,
        String sklepId,
        String kasaId,
        String kasjerId,
        String kontrahentId,
        String formaPlatnId
)
{
}
