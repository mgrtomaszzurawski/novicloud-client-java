/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import io.github.mgrtomaszzurawski.novicloud.client.model.SklepRaw;

/**
 * Immutable SDK model for the {@code sklep} resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record Sklep(
        Long id,
        String nazwa,
        String nip,
        String skrot,
        Integer numer,
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
        String bank,
        String konto,
        Boolean aktywny,
        String krajId
)
{

    /**
     * Creates an immutable {@code Sklep} from the generated {@code SklepRaw}.
     *
     * @param raw the generated model instance; must not be {@code null}
     * @return a new immutable {@code Sklep}
     */
    public static Sklep from(SklepRaw raw) {
        return new Sklep(
                raw.getId(),
                raw.getNazwa(),
                raw.getNip(),
                raw.getSkrot(),
                raw.getNumer(),
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
                raw.getBank(),
                raw.getKonto(),
                raw.getAktywny(),
                LinkUtils.extractId(raw.getKraj())
        );
    }
}
