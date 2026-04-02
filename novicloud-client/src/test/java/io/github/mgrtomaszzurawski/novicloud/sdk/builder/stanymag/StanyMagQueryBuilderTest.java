/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.stanymag;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stanymag.StanMagQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StanyMagQueryBuilderTest {

    private static final int START = 0;
    private static final String NA_DZIEN = "2024-06-01";
    private static final int TB_START = 1;
    private static final String TB_TOWAR_ID = "test-towarId";
    private static final String TB_SKLEP_ID = "test-sklepId";
    private static final String TB_NA_DZIEN = "test-naDzien";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        StanMagQueryBuilder q = StanMagQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.towarId());
        assertNull(q.sklepId());
        assertNull(q.naDzien());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        StanMagQueryBuilder q = StanMagQueryBuilder.builder()
                .start(START)
                .towarId(StanMagTestConstants.TOWAR_ID)
                .sklepId(StanMagTestConstants.SKLEP_ID)
                .naDzien(NA_DZIEN)
                .build();

        // then
        assertEquals(START, q.start());
        assertEquals(StanMagTestConstants.TOWAR_ID, q.towarId());
        assertEquals(StanMagTestConstants.SKLEP_ID, q.sklepId());
        assertEquals(NA_DZIEN, q.naDzien());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        StanMagQueryBuilder original = StanMagQueryBuilder.builder()
                .start(TB_START)
                .towarId(TB_TOWAR_ID)
                .sklepId(TB_SKLEP_ID)
                .naDzien(TB_NA_DZIEN)
                .build();

        // when
        StanMagQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.towarId(), copy.towarId());
        assertEquals(original.sklepId(), copy.sklepId());
        assertEquals(original.naDzien(), copy.naDzien());
    }
}
