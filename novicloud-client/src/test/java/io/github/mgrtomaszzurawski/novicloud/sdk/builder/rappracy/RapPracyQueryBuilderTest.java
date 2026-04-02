/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.rappracy;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.rappracy.RapPracyGroup;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.rappracy.RapPracyQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RapPracyQueryBuilderTest {

    private static final String DATA_POCZ = "2026-01-01";
    private static final String DATA_KONC = "2026-01-31";
    private static final RapPracyGroup GRUPOWANIE = RapPracyGroup.SKLEP;
    private static final int TB_START = 1;
    private static final String TB_DATA_KONC = "2026-12-31";
    private static final String TB_SKLEP_ID = "test-sklepId";
    private static final String TB_KASA_ID = "test-kasaId";
    private static final String TB_KASJER_ID = "test-kasjerId";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        RapPracyQueryBuilder q = RapPracyQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.dataPocz());
        assertNull(q.dataKonc());
        assertNull(q.grupowanie());
    }

    @Test
    void build_whenSelectedFieldsSet_valuesPreserved() {
        // given / when
        RapPracyQueryBuilder q = RapPracyQueryBuilder.builder()
                .dataPocz(DATA_POCZ).dataKonc(DATA_KONC).grupowanie(GRUPOWANIE).build();

        // then
        assertEquals(DATA_POCZ, q.dataPocz());
        assertEquals(DATA_KONC, q.dataKonc());
        assertEquals(GRUPOWANIE.value(), q.grupowanie());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        RapPracyQueryBuilder original = RapPracyQueryBuilder.builder()
                .start(TB_START)
                .dataPocz(DATA_POCZ)
                .dataKonc(TB_DATA_KONC)
                .grupowanie(RapPracyGroup.SKLEP)
                .sklepId(TB_SKLEP_ID)
                .kasaId(TB_KASA_ID)
                .kasjerId(TB_KASJER_ID)
                .build();

        // when
        RapPracyQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.dataPocz(), copy.dataPocz());
        assertEquals(original.dataKonc(), copy.dataKonc());
        assertEquals(original.grupowanie(), copy.grupowanie());
        assertEquals(original.sklepId(), copy.sklepId());
        assertEquals(original.kasaId(), copy.kasaId());
        assertEquals(original.kasjerId(), copy.kasjerId());
    }
}
