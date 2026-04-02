/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import io.github.mgrtomaszzurawski.novicloud.client.model.KontrahentRaw;

/**
 * Immutable SDK model for the {@code kontrahent} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record Kontrahent(
        Long id,
        String nazwa,
        String nip,
        String skrot,
        String ulica,
        String nrDomu,
        String nrLokalu,
        String ulicaINumer,
        String kodPoczt,
        String poczta,
        String miasto,
        String gmina,
        String powiat,
        String wojewodztwo,
        String telefon,
        String email,
        Boolean aktywny,
        Boolean dostawca,
        Boolean staly,
        Boolean producent,
        Boolean odbiorca,
        Boolean osoba,
        String krajId
)
{

    /**
     * Creates an immutable {@code Kontrahent} from the generated {@code KontrahentRaw}.
     *
     * @param raw the generated model instance; must not be {@code null}
     * @return a new immutable {@code Kontrahent}
     */
    public static Kontrahent from(KontrahentRaw raw) {
        return new Kontrahent(
                raw.getId(),
                raw.getNazwa(),
                raw.getNip(),
                raw.getSkrot(),
                raw.getUlica(),
                raw.getNrDomu(),
                raw.getNrLokalu(),
                raw.getUlicaINumer(),
                raw.getKodPoczt(),
                raw.getPoczta(),
                raw.getMiasto(),
                raw.getGmina(),
                raw.getPowiat(),
                raw.getWojewodztwo(),
                raw.getTelefon(),
                raw.getEmail(),
                raw.getAktywny(),
                raw.getDostawca(),
                raw.getStaly(),
                raw.getProducent(),
                raw.getOdbiorca(),
                raw.getOsoba(),
                LinkUtils.extractId(raw.getKraj())
        );
    }
}
