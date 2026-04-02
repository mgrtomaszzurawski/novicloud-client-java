/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.kasy;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kasy.KasaQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KasyQueryBuilderTest {

    private static final String NUMER = "K01";
    private static final int TB_START = 1;
    private static final String TB_ID = "test-id";
    private static final String TB_NAZWA = "test-nazwa";
    private static final String TB_NUMER = "test-numer";
    private static final String TB_OSTATNIA_SYNC = "test-ostatniaSync";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        KasaQueryBuilder q = KasaQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.id());
        assertNull(q.nazwa());
        assertNull(q.numer());
    }

    @Test
    void build_whenSelectedFieldsSet_valuesPreserved() {
        // given / when
        KasaQueryBuilder q = KasaQueryBuilder.builder().numer(NUMER).build();

        // then
        assertEquals(NUMER, q.numer());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        KasaQueryBuilder original = KasaQueryBuilder.builder()
                .start(TB_START)
                .id(TB_ID)
                .nazwa(TB_NAZWA)
                .numer(TB_NUMER)
                .ostatniaSync(TB_OSTATNIA_SYNC)
                .aktywny(true)
                .build();

        // when
        KasaQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.numer(), copy.numer());
        assertEquals(original.ostatniaSync(), copy.ostatniaSync());
        assertEquals(original.aktywny(), copy.aktywny());
    }
}
