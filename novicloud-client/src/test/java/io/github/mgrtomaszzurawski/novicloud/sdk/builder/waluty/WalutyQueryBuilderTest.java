/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.waluty;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.waluty.WalutaQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WalutyQueryBuilderTest {

    private static final int START = 0;
    private static final String NAZWA_FILTER = "~EUR~";
    private static final int TB_START = 1;
    private static final String TB_FTS = "test-fts";
    private static final String TB_ID = "test-id";
    private static final String TB_NAZWA = "test-nazwa";
    private static final String TB_KOD = "test-kod";
    private static final String TB_KURS = "test-kurs";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        WalutaQueryBuilder q = WalutaQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.id());
        assertNull(q.nazwa());
    }

    @Test
    void build_whenSelectedFieldsSet_valuesPreserved() {
        // given / when
        WalutaQueryBuilder q = WalutaQueryBuilder.builder().start(START).nazwa(NAZWA_FILTER).build();

        // then
        assertEquals(START, q.start());
        assertEquals(NAZWA_FILTER, q.nazwa());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        WalutaQueryBuilder original = WalutaQueryBuilder.builder()
                .start(TB_START)
                .fts(TB_FTS)
                .id(TB_ID)
                .nazwa(TB_NAZWA)
                .kod(TB_KOD)
                .kurs(TB_KURS)
                .aktywny(true)
                .build();

        // when
        WalutaQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.fts(), copy.fts());
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.kod(), copy.kod());
        assertEquals(original.kurs(), copy.kurs());
        assertEquals(original.aktywny(), copy.aktywny());
    }
}
