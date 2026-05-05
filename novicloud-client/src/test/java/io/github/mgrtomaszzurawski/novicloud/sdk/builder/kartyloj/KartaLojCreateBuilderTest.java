/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.kartyloj;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kartyloj.KartaLojCreateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KartaLojCreateBuilderTest {

    private static final int TYP = 1;
    private static final String WAZNA_OD = "2024-01-01T00:00:00Z";
    private static final String WAZNA_DO = "2025-12-31T23:59:59Z";
    private static final String POSIADACZ = "true";
    private static final String OPIS1 = "VIP";
    private static final String OPIS2 = "Premium";
    private static final String NAZWISKO_IMIE = "Jan Kowalski";
    private static final String SKROT = "JK";
    private static final String TELEFON = "123456789";
    private static final String EMAIL = "jan@test.pl";
    private static final String MIEJSCOWOSC = "Warszawa";
    private static final String ULICA = "Testowa";
    private static final String NR_DOMU = "1";
    private static final String NR_LOKALU = "2";
    private static final String KOD_POCZT = "00-001";
    private static final String POCZTA = "Warszawa";
    private static final String NIP = "1234567890";
    private static final String DATA_URODZENIA = "1990-05-15";
    private static final String PLEC = "K";
    private static final String TB_BUILDER = "test-kod";
    private static final int TB_TYP = 2;
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
        // 1.1.0 (ADR-055): build() also requires nazwiskoImie and at least one of telefon/email.
        KartaLojCreateBuilder d = KartaLojCreateBuilder.builder(KartaLojTestConstants.KOD)
                .nazwiskoImie(NAZWISKO_IMIE)
                .telefon(TELEFON)
                .build();

        // then
        assertEquals(KartaLojTestConstants.KOD, d.kod());
        assertNull(d.typ());
        assertNull(d.waznaOd());
        assertNull(d.waznaDo());
        assertNull(d.posiadacz());
        assertEquals(NAZWISKO_IMIE, d.nazwiskoImie());
        assertNull(d.dataUrodzenia());
        assertNull(d.plec());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        KartaLojCreateBuilder d = KartaLojCreateBuilder.builder(KartaLojTestConstants.KOD)
                .typ(TYP)
                .waznaOd(WAZNA_OD)
                .waznaDo(WAZNA_DO)
                .posiadacz(POSIADACZ)
                .opis1(OPIS1)
                .opis2(OPIS2)
                .uniewazniono(null)
                .nazwiskoImie(NAZWISKO_IMIE)
                .skrot(SKROT)
                .telefon(TELEFON)
                .email(EMAIL)
                .miejscowosc(MIEJSCOWOSC)
                .ulica(ULICA)
                .nrDomu(NR_DOMU)
                .nrLokalu(NR_LOKALU)
                .kodPoczt(KOD_POCZT)
                .poczta(POCZTA)
                .nip(NIP)
                .dataUrodzenia(DATA_URODZENIA)
                .plec(PLEC)
                .build();

        // then
        assertEquals(KartaLojTestConstants.KOD, d.kod());
        assertEquals(TYP, d.typ());
        assertEquals(WAZNA_OD, d.waznaOd());
        assertEquals(WAZNA_DO, d.waznaDo());
        assertEquals(POSIADACZ, d.posiadacz());
        assertEquals(NAZWISKO_IMIE, d.nazwiskoImie());
        assertEquals(DATA_URODZENIA, d.dataUrodzenia());
        assertEquals(PLEC, d.plec());
    }

    @Test
    void build_whenDateFields_typeIsString() {
        // given
        // Documents ADR-014: draft date fields are String, parsing happens in Sdk layer.

        // when
        KartaLojCreateBuilder d = KartaLojCreateBuilder.builder(KartaLojTestConstants.KOD)
                .nazwiskoImie(NAZWISKO_IMIE)
                .telefon(TELEFON)
                .waznaOd(WAZNA_OD)
                .dataUrodzenia(DATA_URODZENIA)
                .build();

        // then
        assertInstanceOf(String.class, d.waznaOd());
        assertInstanceOf(String.class, d.dataUrodzenia());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        KartaLojCreateBuilder original = KartaLojCreateBuilder.builder(TB_BUILDER)
                .typ(TB_TYP)
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
        KartaLojCreateBuilder copy = original.toBuilder().build();

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
