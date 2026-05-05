/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Immutable SDK model for the {@code kartyloj} (loyalty cards) resource.
 *
 * <p>Instances are created internally by the SDK. Use accessor methods to read field values.
 * @since 1.0.0
 */
public record KartaLojalnosciowa(
        String kod,
        Integer typ,
        LocalDateTime waznaOd,
        LocalDateTime waznaDo,
        String posiadacz,
        String opis1,
        String opis2,
        LocalDateTime uniewazniono,
        String nazwiskoImie,
        String skrot,
        String telefon,
        String email,
        String miejscowosc,
        String ulica,
        String nrDomu,
        String nrLokalu,
        String kodPoczt,
        String poczta,
        String nip,
        LocalDate dataUrodz,
        Plec plec
)
{

    /** Gender (plec) as returned by the NoviCloud API. {@code K} = female, {@code M} = male. */
    public enum Plec {
        /** Kobieta (female). */
        K("K"),
        /** Mezczyzna (male). */
        M("M");

        private final String code;
        Plec(String code) { this.code = code; }
        /** Returns the string code as used by the NoviCloud API. */
        public String code() { return code; }

        /** Resolves enum from API string code; returns {@code null} if unknown. */
        public static Plec fromCode(String code) {
            for (Plec v : values()) {
                if (v.code.equals(code)) { return v; }
            }
            return null;
        }
    }
}
