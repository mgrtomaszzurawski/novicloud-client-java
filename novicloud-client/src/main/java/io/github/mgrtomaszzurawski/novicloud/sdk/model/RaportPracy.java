/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

/**
 * Immutable SDK model for the {@code raportpracy} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record RaportPracy(
        Double czasPracy,
        Double utarg,
        Double gotowka,
        Double karta,
        Double czek,
        Double bon,
        Double przelew,
        Double inna,
        Double paragonyIlosc,
        Double paragonyWartosc,
        Double paragonyPozycje,
        Double fakturyIlosc,
        Double fakturyWartosc,
        Double fakturyPozycje,
        Double stornoPozycje,
        Double stornoWartosc,
        Double paragonyAnulowaneIlosc,
        Double paragonyAnulowaneWartosc,
        String sklepId,
        String kasaId,
        String kasjerId
)
{
}
