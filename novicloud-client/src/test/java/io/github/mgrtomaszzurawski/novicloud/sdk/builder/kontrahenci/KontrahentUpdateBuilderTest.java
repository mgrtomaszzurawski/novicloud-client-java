/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.kontrahenci;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kontrahenci.KontrahentUpdateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KontrahentUpdateBuilderTest {

    private static final String NAZWA_UPDATED = "Zaktualizowana Firma";
    private static final String NIP_UPDATED = "9876543210";
    private static final String SKROT = "ZF";
    private static final String MIASTO = "Krakow";
    private static final long TB_BUILDER = 101L;
    private static final String TB_NAZWA = "test-nazwa";
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
    void build_whenIdOnly_optionalsAreNull() {
        // given / when
        KontrahentUpdateBuilder d = KontrahentUpdateBuilder.builder(KontrahentTestConstants.ID_UPDATE).build();

        // then
        assertEquals(KontrahentTestConstants.ID_UPDATE, d.id());
        assertNull(d.nazwa());
        assertNull(d.nip());
        assertNull(d.aktywny());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        KontrahentUpdateBuilder d = KontrahentUpdateBuilder.builder(KontrahentTestConstants.ID_UPDATE)
                .nazwa(NAZWA_UPDATED)
                .nip(NIP_UPDATED)
                .skrot(SKROT)
                .miasto(MIASTO)
                .aktywny(false)
                .dostawca(true)
                .build();

        // then
        assertEquals(KontrahentTestConstants.ID_UPDATE, d.id());
        assertEquals(NAZWA_UPDATED, d.nazwa());
        assertEquals(NIP_UPDATED, d.nip());
        assertEquals(SKROT, d.skrot());
        assertEquals(MIASTO, d.miasto());
        assertFalse(d.aktywny());
        assertTrue(d.dostawca());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        KontrahentUpdateBuilder original = KontrahentUpdateBuilder.builder(TB_BUILDER)
                .nazwa(TB_NAZWA)
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
        KontrahentUpdateBuilder copy = original.toBuilder().build();

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
