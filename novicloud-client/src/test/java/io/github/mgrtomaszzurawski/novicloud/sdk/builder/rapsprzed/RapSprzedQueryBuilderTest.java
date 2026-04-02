/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.rapsprzed;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.rapsprzed.RapSprzedGroup;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.rapsprzed.RapSprzedQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RapSprzedQueryBuilderTest {

    private static final String DATA_POCZ = "2024-01-01";
    private static final String DATA_KONC = "2024-01-31";
    private static final RapSprzedGroup GRUPOWANIE = RapSprzedGroup.TOWAR;
    private static final String SKLADNIKI = "1,2,4";
    private static final String TOWAR_ID = "10";
    private static final String SKLEP_ID = "1";
    private static final int TB_START = 1;
    private static final String TB_DATA_POCZ = "2026-01-01";
    private static final String TB_DATA_KONC = "2026-12-31";
    private static final String TB_SKLADNIKI = "test-skladniki";
    private static final String TB_TOWAR_ID = "test-towarId";
    private static final String TB_ASORT_ID = "test-asortId";
    private static final String TB_SKLEP_ID = "test-sklepId";
    private static final String TB_KASA_ID = "test-kasaId";
    private static final String TB_KASJER_ID = "test-kasjerId";
    private static final String TB_KONTRAHENT_ID = "test-kontrahentId";
    private static final String TB_FORMA_PLATN_ID = "test-formaPlatnId";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        RapSprzedQueryBuilder q = RapSprzedQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.dataPocz());
        assertNull(q.dataKonc());
        assertNull(q.grupowanie());
        assertNull(q.skladniki());
        assertNull(q.towarId());
        assertNull(q.asortId());
        assertNull(q.sklepId());
        assertNull(q.kasaId());
        assertNull(q.kasjerId());
        assertNull(q.kontrahentId());
        assertNull(q.formaPlatnId());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        RapSprzedQueryBuilder q = RapSprzedQueryBuilder.builder()
                .dataPocz(DATA_POCZ)
                .dataKonc(DATA_KONC)
                .grupowanie(GRUPOWANIE)
                .skladniki(SKLADNIKI)
                .towarId(TOWAR_ID)
                .sklepId(SKLEP_ID)
                .build();

        // then
        assertEquals(DATA_POCZ, q.dataPocz());
        assertEquals(DATA_KONC, q.dataKonc());
        assertEquals(GRUPOWANIE.value(), q.grupowanie());
        assertEquals(SKLADNIKI, q.skladniki());
        assertEquals(TOWAR_ID, q.towarId());
        assertEquals(SKLEP_ID, q.sklepId());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        RapSprzedQueryBuilder original = RapSprzedQueryBuilder.builder()
                .start(TB_START)
                .dataPocz(TB_DATA_POCZ)
                .dataKonc(TB_DATA_KONC)
                .grupowanie(RapSprzedGroup.TOWAR)
                .skladniki(TB_SKLADNIKI)
                .towarId(TB_TOWAR_ID)
                .asortId(TB_ASORT_ID)
                .sklepId(TB_SKLEP_ID)
                .kasaId(TB_KASA_ID)
                .kasjerId(TB_KASJER_ID)
                .kontrahentId(TB_KONTRAHENT_ID)
                .formaPlatnId(TB_FORMA_PLATN_ID)
                .build();

        // when
        RapSprzedQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.dataPocz(), copy.dataPocz());
        assertEquals(original.dataKonc(), copy.dataKonc());
        assertEquals(original.grupowanie(), copy.grupowanie());
        assertEquals(original.skladniki(), copy.skladniki());
        assertEquals(original.towarId(), copy.towarId());
        assertEquals(original.asortId(), copy.asortId());
        assertEquals(original.sklepId(), copy.sklepId());
        assertEquals(original.kasaId(), copy.kasaId());
        assertEquals(original.kasjerId(), copy.kasjerId());
        assertEquals(original.kontrahentId(), copy.kontrahentId());
        assertEquals(original.formaPlatnId(), copy.formaPlatnId());
    }
}
