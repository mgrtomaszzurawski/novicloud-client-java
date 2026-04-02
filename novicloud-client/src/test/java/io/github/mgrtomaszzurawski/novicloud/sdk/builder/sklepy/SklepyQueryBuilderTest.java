/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.sklepy;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.sklepy.SklepQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SklepyQueryBuilderTest {

    private static final int START = 5;
    private static final String NAZWA_FILTER = "~Sklep~";
    private static final int TB_START = 1;
    private static final String TB_FTS = "test-fts";
    private static final String TB_ID = "test-id";
    private static final String TB_NAZWA = "test-nazwa";
    private static final String TB_NIP = "test-nip";
    private static final String TB_SKROT = "test-skrot";
    private static final String TB_NUMER = "test-numer";
    private static final String TB_ULICA = "test-ulica";
    private static final String TB_KOD_POCZT = "test-kodPoczt";
    private static final String TB_MIASTO = "test-miasto";
    private static final String TB_TELEFON = "test-telefon";
    private static final String TB_EMAIL = "test-email";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        SklepQueryBuilder q = SklepQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.id());
        assertNull(q.nazwa());
    }

    @Test
    void build_whenSelectedFieldsSet_valuesPreserved() {
        // given / when
        SklepQueryBuilder q = SklepQueryBuilder.builder().start(START).nazwa(NAZWA_FILTER).build();

        // then
        assertEquals(START, q.start());
        assertEquals(NAZWA_FILTER, q.nazwa());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        SklepQueryBuilder original = SklepQueryBuilder.builder()
                .start(TB_START)
                .fts(TB_FTS)
                .id(TB_ID)
                .nazwa(TB_NAZWA)
                .nip(TB_NIP)
                .skrot(TB_SKROT)
                .numer(TB_NUMER)
                .ulica(TB_ULICA)
                .kodPoczt(TB_KOD_POCZT)
                .miasto(TB_MIASTO)
                .telefon(TB_TELEFON)
                .email(TB_EMAIL)
                .aktywny(true)
                .build();

        // when
        SklepQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.fts(), copy.fts());
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.nip(), copy.nip());
        assertEquals(original.skrot(), copy.skrot());
        assertEquals(original.numer(), copy.numer());
        assertEquals(original.ulica(), copy.ulica());
        assertEquals(original.kodPoczt(), copy.kodPoczt());
        assertEquals(original.miasto(), copy.miasto());
        assertEquals(original.telefon(), copy.telefon());
        assertEquals(original.email(), copy.email());
        assertEquals(original.aktywny(), copy.aktywny());
    }
}
