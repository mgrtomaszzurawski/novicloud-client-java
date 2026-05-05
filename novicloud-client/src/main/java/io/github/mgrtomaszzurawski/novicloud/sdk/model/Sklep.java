/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

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
}
