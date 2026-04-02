/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import io.github.mgrtomaszzurawski.novicloud.client.model.DokumentRaw;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Immutable SDK model for the {@code dokument} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record Dokument(
        Long id,
        Integer typDok,
        LocalDateTime dataWystawienia,
        LocalDate dataWplywu,
        LocalDate dataWykonania,
        String nrDok,
        String kartaRabatowa,
        String nipNaPar,
        String nrSystemowy,
        String nrFiskalny,
        String nrRapDobowego,
        String komentarz,
        Boolean storno,
        Double netto,
        Double podatek,
        Double brutto,
        Double rabat,
        LocalDateTime terminPlatn,
        Double zaplacono,
        String sklepId,
        String sklepOdbId,
        String kontrahentId,
        String platnikId,
        String kasaId,
        String kasjerId,
        String formaPlatnId,
        String dotyczyId,
        String pozycjeLink
)
{

    /**
     * Creates an immutable {@code Dokument} from the generated {@code DokumentRaw}.
     *
     * @param raw the generated model instance; must not be {@code null}
     * @return a new immutable {@code Dokument}
     */
    public static Dokument from(DokumentRaw raw) {
        return new Dokument(
                raw.getId(),
                raw.getTypDok(),
                raw.getDataWystawienia(),
                raw.getDataWplywu(),
                raw.getDataWykonania(),
                raw.getNrDok(),
                raw.getKartaRabatowa(),
                raw.getNipNaPar(),
                raw.getNrSystemowy(),
                raw.getNrFiskalny(),
                raw.getNrRapDobowego(),
                raw.getKomentarz(),
                raw.getStorno(),
                raw.getNetto(),
                raw.getPodatek(),
                raw.getBrutto(),
                raw.getRabat(),
                raw.getTerminPlatn(),
                raw.getZaplacono(),
                LinkUtils.extractId(raw.getSklep()),
                LinkUtils.extractId(raw.getSklepOdb()),
                LinkUtils.extractId(raw.getKontrahent()),
                LinkUtils.extractId(raw.getPlatnik()),
                LinkUtils.extractId(raw.getKasa()),
                LinkUtils.extractId(raw.getKasjer()),
                LinkUtils.extractId(raw.getFormaPlatn()),
                LinkUtils.extractId(raw.getDotyczy()),
                LinkUtils.extractId(raw.getPozycje())
        );
    }
}
