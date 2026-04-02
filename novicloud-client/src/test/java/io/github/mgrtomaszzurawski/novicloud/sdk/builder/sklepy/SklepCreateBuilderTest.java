/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.sklepy;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.sklepy.SklepCreateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SklepCreateBuilderTest {

    private static final String NAZWA = "Sklep Centralny";
    private static final int NUMER = 1;
    private static final long ID = 10L;
    private static final String NIP = "1234567890";
    private static final String SKROT = "SC";
    private static final String ULICA = "Dluga";
    private static final String NR_DOMU = "1";
    private static final String NR_LOKALU = "A";
    private static final String KOD_POCZT = "00-001";
    private static final String MIASTO = "Warszawa";
    private static final String KRAJ_ID = "1";
    private static final String TELEFON = "123456789";
    private static final String EMAIL = "sklep@test.pl";
    private static final String BANK = "PKO";
    private static final String KONTO = "12345678901234567890123456";
    private static final int TB_NUMER = 2;
    private static final long TB_ID = 1L;
    private static final String TB_NIP = "test-nip";
    private static final String TB_SKROT = "test-skrot";
    private static final String TB_ULICA = "test-ulica";
    private static final String TB_NR_DOMU = "test-nrDomu";
    private static final String TB_NR_LOKALU = "test-nrLokalu";
    private static final String TB_KOD_POCZT = "test-kodPoczt";
    private static final String TB_POCZTA = "test-poczta";
    private static final String TB_MIASTO = "test-miasto";
    private static final String TB_KRAJ_ID = "test-krajId";
    private static final String TB_TELEFON = "test-telefon";
    private static final String TB_EMAIL = "test-email";
    private static final String TB_BANK = "test-bank";
    private static final String TB_KONTO = "test-konto";
    private static final String TB_NAZWA = "test-nazwa";


    @Test
    void build_whenRequiredOnly_optionalsAreNull() {
        // given / when
        SklepCreateBuilder d = SklepCreateBuilder.builder(NAZWA, NUMER).build();

        // then
        assertEquals(NAZWA, d.nazwa());
        assertEquals(NUMER, d.numer());
        assertNull(d.id());
        assertNull(d.nip());
        assertNull(d.aktywny());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        SklepCreateBuilder d = SklepCreateBuilder.builder(NAZWA, NUMER)
                .id(ID)
                .nip(NIP)
                .skrot(SKROT)
                .ulica(ULICA)
                .nrDomu(NR_DOMU)
                .nrLokalu(NR_LOKALU)
                .kodPoczt(KOD_POCZT)
                .poczta(MIASTO)
                .miasto(MIASTO)
                .krajId(KRAJ_ID)
                .telefon(TELEFON)
                .email(EMAIL)
                .bank(BANK)
                .konto(KONTO)
                .aktywny(true)
                .build();

        // then
        assertEquals(NAZWA, d.nazwa());
        assertEquals(NUMER, d.numer());
        assertEquals(ID, d.id());
        assertEquals(NIP, d.nip());
        assertEquals(SKROT, d.skrot());
        assertEquals(ULICA, d.ulica());
        assertEquals(NR_DOMU, d.nrDomu());
        assertEquals(NR_LOKALU, d.nrLokalu());
        assertEquals(KOD_POCZT, d.kodPoczt());
        assertEquals(MIASTO, d.poczta());
        assertEquals(MIASTO, d.miasto());
        assertEquals(KRAJ_ID, d.krajId());
        assertEquals(TELEFON, d.telefon());
        assertEquals(EMAIL, d.email());
        assertEquals(BANK, d.bank());
        assertEquals(KONTO, d.konto());
        assertTrue(d.aktywny());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        SklepCreateBuilder original = SklepCreateBuilder.builder(TB_NAZWA, TB_NUMER)
                .id(TB_ID)
                .nip(TB_NIP)
                .skrot(TB_SKROT)
                .ulica(TB_ULICA)
                .nrDomu(TB_NR_DOMU)
                .nrLokalu(TB_NR_LOKALU)
                .kodPoczt(TB_KOD_POCZT)
                .poczta(TB_POCZTA)
                .miasto(TB_MIASTO)
                .krajId(TB_KRAJ_ID)
                .telefon(TB_TELEFON)
                .email(TB_EMAIL)
                .bank(TB_BANK)
                .konto(TB_KONTO)
                .aktywny(true)
                .build();

        // when
        SklepCreateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.nip(), copy.nip());
        assertEquals(original.skrot(), copy.skrot());
        assertEquals(original.numer(), copy.numer());
        assertEquals(original.ulica(), copy.ulica());
        assertEquals(original.nrDomu(), copy.nrDomu());
        assertEquals(original.nrLokalu(), copy.nrLokalu());
        assertEquals(original.kodPoczt(), copy.kodPoczt());
        assertEquals(original.poczta(), copy.poczta());
        assertEquals(original.miasto(), copy.miasto());
        assertEquals(original.krajId(), copy.krajId());
        assertEquals(original.telefon(), copy.telefon());
        assertEquals(original.email(), copy.email());
        assertEquals(original.bank(), copy.bank());
        assertEquals(original.konto(), copy.konto());
        assertEquals(original.aktywny(), copy.aktywny());
    }
}
