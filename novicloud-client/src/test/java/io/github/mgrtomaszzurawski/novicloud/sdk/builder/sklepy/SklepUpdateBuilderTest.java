/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.sklepy;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.sklepy.SklepUpdateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SklepUpdateBuilderTest {

    private static final long ID = 4L;
    private static final String NAZWA = "Sklep Zachodni";
    private static final int NUMER = 2;
    private static final String SKROT = "SZ";
    private static final String MIASTO = "Gdansk";
    private static final long TB_BUILDER = 101L;
    private static final String TB_NAZWA = "test-nazwa";
    private static final String TB_NIP = "test-nip";
    private static final String TB_SKROT = "test-skrot";
    private static final int TB_NUMER = 5;
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


    @Test
    void build_whenIdOnly_optionalsAreNull() {
        // given / when
        SklepUpdateBuilder d = SklepUpdateBuilder.builder(ID).build();

        // then
        assertEquals(ID, d.id());
        assertNull(d.nazwa());
        assertNull(d.nip());
        assertNull(d.numer());
        assertNull(d.aktywny());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        SklepUpdateBuilder d = SklepUpdateBuilder.builder(ID)
                .nazwa(NAZWA)
                .numer(NUMER)
                .skrot(SKROT)
                .miasto(MIASTO)
                .aktywny(true)
                .build();

        // then
        assertEquals(ID, d.id());
        assertEquals(NAZWA, d.nazwa());
        assertEquals(NUMER, d.numer());
        assertEquals(SKROT, d.skrot());
        assertEquals(MIASTO, d.miasto());
        assertTrue(d.aktywny());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        SklepUpdateBuilder original = SklepUpdateBuilder.builder(TB_BUILDER)
                .nazwa(TB_NAZWA)
                .nip(TB_NIP)
                .skrot(TB_SKROT)
                .numer(TB_NUMER)
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
        SklepUpdateBuilder copy = original.toBuilder().build();

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
