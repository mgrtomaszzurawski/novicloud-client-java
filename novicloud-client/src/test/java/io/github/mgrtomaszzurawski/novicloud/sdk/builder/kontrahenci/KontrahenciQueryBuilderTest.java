/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.kontrahenci;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kontrahenci.KontrahentQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KontrahenciQueryBuilderTest {

    private static final String NAZWA_FILTER = "~Kowalski~";
    private static final int TB_START = 1;
    private static final String TB_FTS = "test-fts";
    private static final String TB_ID = "test-id";
    private static final String TB_NAZWA = "test-nazwa";
    private static final String TB_NIP = "test-nip";
    private static final String TB_SKROT = "test-skrot";
    private static final String TB_ULICA = "test-ulica";
    private static final String TB_NR_DOMU = "test-nrDomu";
    private static final String TB_NR_LOKALU = "test-nrLokalu";
    private static final String TB_KOD_POCZT = "test-kodPoczt";
    private static final String TB_POCZTA = "test-poczta";
    private static final String TB_MIASTO = "test-miasto";
    private static final String TB_TELEFON = "test-telefon";
    private static final String TB_EMAIL = "test-email";
    private static final String TB_KRAJ_ID = "test-krajId";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        KontrahentQueryBuilder q = KontrahentQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.id());
        assertNull(q.nazwa());
        assertNull(q.nip());
        assertNull(q.aktywny());
        assertNull(q.dostawca());
        assertNull(q.odbiorca());
        assertNull(q.staly());
        assertNull(q.producent());
        assertNull(q.krajId());
    }

    @Test
    void build_whenAllBooleanFiltersSet_valuesPreserved() {
        // given / when
        KontrahentQueryBuilder q = KontrahentQueryBuilder.builder()
                .aktywny(true)
                .dostawca(false)
                .odbiorca(true)
                .staly(true)
                .producent(false)
                .build();

        // then
        assertTrue(q.aktywny());
        assertFalse(q.dostawca());
        assertTrue(q.odbiorca());
        assertTrue(q.staly());
        assertFalse(q.producent());
    }

    @Test
    void build_whenTextAndLinkFiltersSet_valuesPreserved() {
        // given / when
        KontrahentQueryBuilder q = KontrahentQueryBuilder.builder()
                .nazwa(NAZWA_FILTER)
                .nip(KontrahentTestConstants.NIP)
                .krajId(KontrahentTestConstants.KRAJ_ID)
                .build();

        // then
        assertEquals(NAZWA_FILTER, q.nazwa());
        assertEquals(KontrahentTestConstants.NIP, q.nip());
        assertEquals(KontrahentTestConstants.KRAJ_ID, q.krajId());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        KontrahentQueryBuilder original = KontrahentQueryBuilder.builder()
                .start(TB_START)
                .fts(TB_FTS)
                .id(TB_ID)
                .nazwa(TB_NAZWA)
                .nip(TB_NIP)
                .skrot(TB_SKROT)
                .ulica(TB_ULICA)
                .nrDomu(TB_NR_DOMU)
                .nrLokalu(TB_NR_LOKALU)
                .kodPoczt(TB_KOD_POCZT)
                .poczta(TB_POCZTA)
                .miasto(TB_MIASTO)
                .telefon(TB_TELEFON)
                .email(TB_EMAIL)
                .krajId(TB_KRAJ_ID)
                .aktywny(true)
                .dostawca(true)
                .staly(true)
                .producent(true)
                .odbiorca(true)
                .build();

        // when
        KontrahentQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.fts(), copy.fts());
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
        assertEquals(original.telefon(), copy.telefon());
        assertEquals(original.email(), copy.email());
        assertEquals(original.krajId(), copy.krajId());
        assertEquals(original.aktywny(), copy.aktywny());
        assertEquals(original.dostawca(), copy.dostawca());
        assertEquals(original.staly(), copy.staly());
        assertEquals(original.producent(), copy.producent());
        assertEquals(original.odbiorca(), copy.odbiorca());
    }
}
