/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import io.github.mgrtomaszzurawski.novicloud.client.model.SprzedazRaw;
import java.time.LocalDateTime;

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
        String kontrahentId
)
{

    /**
     * Creates an immutable {@code Sprzedaz} from the generated {@code SprzedazRaw}.
     *
     * @param raw the generated model instance; must not be {@code null}
     * @return a new immutable {@code Sprzedaz}
     */
    public static Sprzedaz from(SprzedazRaw raw) {
        return new Sprzedaz(
                raw.getId(),
                raw.getData(),
                raw.getNrDok(),
                raw.getTypDok(),
                raw.getNrSystemowy(),
                raw.getNrFiskalny(),
                raw.getNrRapDob(),
                raw.getIlosc(),
                raw.getCena(),
                raw.getCenaPrzedRab(),
                raw.getStawkaVat(),
                raw.getBrutto(),
                raw.getPodatek(),
                raw.getRabat(),
                LinkUtils.extractId(raw.getTowar()),
                LinkUtils.extractId(raw.getSklep()),
                LinkUtils.extractId(raw.getKasa()),
                LinkUtils.extractId(raw.getKasjer()),
                LinkUtils.extractId(raw.getKontrahent())
        );
    }
}
