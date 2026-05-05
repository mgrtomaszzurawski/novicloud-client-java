/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

/**
 * Immutable SDK model for the {@code pozycjadokumentu} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record PozycjaDokumentu(
        Long id,
        Integer nrPozycji,
        Double ilosc,
        Double iloscPocz,
        Integer stawkaVat,
        Double cPrzedRabNetto,
        Double cPrzedRabBrutto,
        Double cPoRabNetto,
        Double cPoRabBrutto,
        Double rabatKwota,
        Double wNetto,
        Double wPodatek,
        Double wBrutto,
        Double orgIlosc,
        Double orgCPrzedRabNetto,
        Double orgCPrzedRabBrutto,
        Double orgCPoRabNetto,
        Double orgCPoRabBrutto,
        Double orgRabatKwota,
        Double orgWNetto,
        Double orgWPodatek,
        Double orgWBrutto,
        Double rozlNetto,
        Double rozlPodatek,
        Double rozlBrutto,
        Boolean storno,
        String dokumentId,
        String towarId
)
{
}
