/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.kartyloj;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kartyloj.KartaLojUpdateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KartaLojUpdateBuilderTest {

    private static final int TYP = 2;
    private static final String WAZNA_OD = "2025-01-01T00:00:00Z";
    private static final String WAZNA_DO = "2026-12-31T23:59:59Z";
    private static final String NAZWISKO_IMIE = "Anna Nowak";
    private static final String TELEFON = "987654321";
    private static final String EMAIL = "anna@test.pl";
    private static final String PLEC = "K";
    private static final String TB_BUILDER = "test-kod";
    private static final String TB_WAZNA_OD = "test-waznaOd";
    private static final String TB_WAZNA_DO = "test-waznaDo";
    private static final String TB_POSIADACZ = "test-posiadacz";
    private static final String TB_OPIS1 = "test-opis1";
    private static final String TB_OPIS2 = "test-opis2";
    private static final String TB_UNIEWAZNIONO = "test-uniewazniono";
    private static final String TB_NAZWISKO_IMIE = "test-nazwiskoImie";
    private static final String TB_SKROT = "test-skrot";
    private static final String TB_TELEFON = "test-telefon";
    private static final String TB_EMAIL = "test-email";
    private static final String TB_MIEJSCOWOSC = "test-miejscowosc";
    private static final String TB_ULICA = "test-ulica";
    private static final String TB_NR_DOMU = "test-nrDomu";
    private static final String TB_NR_LOKALU = "test-nrLokalu";
    private static final String TB_KOD_POCZT = "test-kodPoczt";
    private static final String TB_POCZTA = "test-poczta";
    private static final String TB_NIP = "test-nip";
    private static final String TB_DATA_URODZENIA = "test-dataUrodzenia";
    private static final String TB_PLEC = "test-plec";


    @Test
    void build_whenRequiredOnly_optionalsAreNull() {
        // given / when
        KartaLojUpdateBuilder d = KartaLojUpdateBuilder.builder(KartaLojTestConstants.KOD).build();

        // then
        assertEquals(KartaLojTestConstants.KOD, d.kod());
        assertNull(d.typ());
        assertNull(d.waznaOd());
        assertNull(d.nazwiskoImie());
        assertNull(d.plec());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        KartaLojUpdateBuilder d = KartaLojUpdateBuilder.builder(KartaLojTestConstants.KOD)
                .typ(TYP)
                .waznaOd(WAZNA_OD)
                .waznaDo(WAZNA_DO)
                .nazwiskoImie(NAZWISKO_IMIE)
                .telefon(TELEFON)
                .email(EMAIL)
                .plec(PLEC)
                .build();

        // then
        assertEquals(KartaLojTestConstants.KOD, d.kod());
        assertEquals(TYP, d.typ());
        assertEquals(WAZNA_OD, d.waznaOd());
        assertEquals(WAZNA_DO, d.waznaDo());
        assertEquals(NAZWISKO_IMIE, d.nazwiskoImie());
        assertEquals(TELEFON, d.telefon());
        assertEquals(EMAIL, d.email());
        assertEquals(PLEC, d.plec());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        KartaLojUpdateBuilder original = KartaLojUpdateBuilder.builder(TB_BUILDER)
                .typ(TYP)
                .waznaOd(TB_WAZNA_OD)
                .waznaDo(TB_WAZNA_DO)
                .posiadacz(TB_POSIADACZ)
                .opis1(TB_OPIS1)
                .opis2(TB_OPIS2)
                .uniewazniono(TB_UNIEWAZNIONO)
                .nazwiskoImie(TB_NAZWISKO_IMIE)
                .skrot(TB_SKROT)
                .telefon(TB_TELEFON)
                .email(TB_EMAIL)
                .miejscowosc(TB_MIEJSCOWOSC)
                .ulica(TB_ULICA)
                .nrDomu(TB_NR_DOMU)
                .nrLokalu(TB_NR_LOKALU)
                .kodPoczt(TB_KOD_POCZT)
                .poczta(TB_POCZTA)
                .nip(TB_NIP)
                .dataUrodzenia(TB_DATA_URODZENIA)
                .plec(TB_PLEC)
                .build();

        // when
        KartaLojUpdateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.kod(), copy.kod());
        assertEquals(original.typ(), copy.typ());
        assertEquals(original.waznaOd(), copy.waznaOd());
        assertEquals(original.waznaDo(), copy.waznaDo());
        assertEquals(original.posiadacz(), copy.posiadacz());
        assertEquals(original.opis1(), copy.opis1());
        assertEquals(original.opis2(), copy.opis2());
        assertEquals(original.uniewazniono(), copy.uniewazniono());
        assertEquals(original.nazwiskoImie(), copy.nazwiskoImie());
        assertEquals(original.skrot(), copy.skrot());
        assertEquals(original.telefon(), copy.telefon());
        assertEquals(original.email(), copy.email());
        assertEquals(original.miejscowosc(), copy.miejscowosc());
        assertEquals(original.ulica(), copy.ulica());
        assertEquals(original.nrDomu(), copy.nrDomu());
        assertEquals(original.nrLokalu(), copy.nrLokalu());
        assertEquals(original.kodPoczt(), copy.kodPoczt());
        assertEquals(original.poczta(), copy.poczta());
        assertEquals(original.nip(), copy.nip());
        assertEquals(original.dataUrodzenia(), copy.dataUrodzenia());
        assertEquals(original.plec(), copy.plec());
    }
}
