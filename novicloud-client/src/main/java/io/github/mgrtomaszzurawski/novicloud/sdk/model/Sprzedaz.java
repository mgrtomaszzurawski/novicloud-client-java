/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Immutable SDK model for the {@code sprzedaz} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record Sprzedaz(
        Long id,
        LocalDateTime data,
        String nrDok,
        Integer typDok,
        String nrSystemowy,
        String nrFiskalny,
        String nrRapDob,
        Double ilosc,
        Double cena,
        Double cenaPrzedRab,
        Integer stawkaVat,
        Double brutto,
        Double podatek,
        Double rabat,
        String towarId,
        String sklepId,
        String kasaId,
        String kasjerId,
        String kontrahentId,
        List<Platnosc> platnosci
)
{
}
