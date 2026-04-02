/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.kontrahenci;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kontrahenci.KontrahentCreateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KontrahentCreateBuilderTest {

    private static final String SKROT = "TF";
    private static final String ULICA = "Testowa";
    private static final String NR_DOMU = "1";
    private static final String NR_LOKALU = "2A";
    private static final String KOD_POCZT = "00-001";
    private static final String MIASTO = "Warszawa";
    private static final String TELEFON = "123456789";
    private static final String EMAIL = "test@test.pl";
    private static final String TB_BUILDER = "test-nazwa";
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


    @Test
    void build_whenRequiredOnly_optionalsAreNull() {
        // given / when
        KontrahentCreateBuilder d = KontrahentCreateBuilder.builder(KontrahentTestConstants.NAZWA).build();

        // then
        assertNull(d.id());
        assertEquals(KontrahentTestConstants.NAZWA, d.nazwa());
        assertNull(d.nip());
        assertNull(d.aktywny());
        assertNull(d.dostawca());
        assertNull(d.krajId());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        KontrahentCreateBuilder d = KontrahentCreateBuilder.builder(KontrahentTestConstants.NAZWA)
                .id(KontrahentTestConstants.ID_CREATE)
                .nip(KontrahentTestConstants.NIP)
                .skrot(SKROT)
                .ulica(ULICA)
                .nrDomu(NR_DOMU)
                .nrLokalu(NR_LOKALU)
                .kodPoczt(KOD_POCZT)
                .poczta(MIASTO)
                .miasto(MIASTO)
                .telefon(TELEFON)
                .email(EMAIL)
                .krajId(KontrahentTestConstants.KRAJ_ID)
                .aktywny(true)
                .dostawca(true)
                .staly(false)
                .producent(false)
                .odbiorca(true)
                .osoba(false)
                .build();

        // then
        assertEquals(KontrahentTestConstants.ID_CREATE, d.id());
        assertEquals(KontrahentTestConstants.NAZWA, d.nazwa());
        assertEquals(KontrahentTestConstants.NIP, d.nip());
        assertEquals(SKROT, d.skrot());
        assertEquals(ULICA, d.ulica());
        assertEquals(NR_DOMU, d.nrDomu());
        assertEquals(NR_LOKALU, d.nrLokalu());
        assertEquals(KOD_POCZT, d.kodPoczt());
        assertEquals(MIASTO, d.poczta());
        assertEquals(MIASTO, d.miasto());
        assertEquals(TELEFON, d.telefon());
        assertEquals(EMAIL, d.email());
        assertEquals(KontrahentTestConstants.KRAJ_ID, d.krajId());
        assertTrue(d.aktywny());
        assertTrue(d.dostawca());
        assertFalse(d.staly());
        assertFalse(d.producent());
        assertTrue(d.odbiorca());
        assertFalse(d.osoba());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        KontrahentCreateBuilder original = KontrahentCreateBuilder.builder(TB_BUILDER)
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
                .aktywny(true)
                .dostawca(true)
                .staly(true)
                .producent(true)
                .odbiorca(true)
                .osoba(true)
                .build();

        // when
        KontrahentCreateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.nip(), copy.nip());
        assertEquals(original.skrot(), copy.skrot());
        assertEquals(original.ulica(), copy.ulica());
        assertEquals(original.nrDomu(), copy.nrDomu());
        assertEquals(original.nrLokalu(), copy.nrLokalu());
        assertEquals(original.kodPoczt(), copy.kodPoczt());
        assertEquals(original.poczta(), copy.poczta());
        assertEquals(original.miasto(), copy.miasto());
        assertEquals(original.krajId(), copy.krajId());
        assertEquals(original.telefon(), copy.telefon());
        assertEquals(original.email(), copy.email());
        assertEquals(original.aktywny(), copy.aktywny());
        assertEquals(original.dostawca(), copy.dostawca());
        assertEquals(original.staly(), copy.staly());
        assertEquals(original.producent(), copy.producent());
        assertEquals(original.odbiorca(), copy.odbiorca());
        assertEquals(original.osoba(), copy.osoba());
    }
}
