/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import io.github.mgrtomaszzurawski.novicloud.client.model.KartaLojalnosciowaRaw;
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

    /**
     * Creates an immutable {@code KartaLojalnosciowa} from the generated {@code KartaLojalnosciowaRaw}.
     *
     * @param raw the generated model instance; must not be {@code null}
     * @return a new immutable {@code KartaLojalnosciowa}
     */
    public static KartaLojalnosciowa from(KartaLojalnosciowaRaw raw) {
        return new KartaLojalnosciowa(
                raw.getKod(),
                raw.getTyp(),
                raw.getWaznaOd(),
                raw.getWaznaDo(),
                raw.getPosiadacz(),
                raw.getOpis1(),
                raw.getOpis2(),
                raw.getUniewazniono(),
                raw.getNazwiskoImie(),
                raw.getSkrot(),
                raw.getTelefon(),
                raw.getEmail(),
                raw.getMiejscowosc(),
                raw.getUlica(),
                raw.getNrDomu(),
                raw.getNrLokalu(),
                raw.getKodPoczt(),
                raw.getPoczta(),
                raw.getNip(),
                raw.getDataUrodz(),
                raw.getPlec() != null ? Plec.fromCode(raw.getPlec().getValue()) : null
        );
    }
}
