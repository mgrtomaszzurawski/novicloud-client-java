/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.dokumenty;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.dokumenty.DokumentQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DokumentyQueryBuilderTest {

    private static final String TYP_DOK = "FS";
    private static final String DATA_WYSTAWIENIA = "2026-01-01";
    private static final int TB_START = 1;
    private static final String TB_ID = "test-id";
    private static final String TB_TYP_DOK = "test-typDok";
    private static final String TB_DATA_WYSTAWIENIA = "test-dataWystawienia";
    private static final String TB_DATA_WPLYWU = "test-dataWplywu";
    private static final String TB_DATA_WYKONANIA = "test-dataWykonania";
    private static final String TB_NR_DOK = "test-nrDok";
    private static final String TB_SKLEP_ID = "test-sklepId";
    private static final String TB_KONTRAHENT_ID = "test-kontrahentId";
    private static final String TB_PLATNIK_ID = "test-platnikId";
    private static final String TB_KASA_ID = "test-kasaId";
    private static final String TB_KASJER_ID = "test-kasjerId";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        DokumentQueryBuilder q = DokumentQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.id());
        assertNull(q.typDok());
        assertNull(q.dataWystawienia());
    }

    @Test
    void build_whenSelectedFieldsSet_valuesPreserved() {
        // given / when
        DokumentQueryBuilder q = DokumentQueryBuilder.builder().typDok(TYP_DOK).dataWystawienia(DATA_WYSTAWIENIA).build();

        // then
        assertEquals(TYP_DOK, q.typDok());
        assertEquals(DATA_WYSTAWIENIA, q.dataWystawienia());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        DokumentQueryBuilder original = DokumentQueryBuilder.builder()
                .start(TB_START)
                .id(TB_ID)
                .typDok(TB_TYP_DOK)
                .dataWystawienia(TB_DATA_WYSTAWIENIA)
                .dataWplywu(TB_DATA_WPLYWU)
                .dataWykonania(TB_DATA_WYKONANIA)
                .nrDok(TB_NR_DOK)
                .sklepId(TB_SKLEP_ID)
                .kontrahentId(TB_KONTRAHENT_ID)
                .platnikId(TB_PLATNIK_ID)
                .kasaId(TB_KASA_ID)
                .kasjerId(TB_KASJER_ID)
                .storno(true)
                .build();

        // when
        DokumentQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.id(), copy.id());
        assertEquals(original.typDok(), copy.typDok());
        assertEquals(original.dataWystawienia(), copy.dataWystawienia());
        assertEquals(original.dataWplywu(), copy.dataWplywu());
        assertEquals(original.dataWykonania(), copy.dataWykonania());
        assertEquals(original.nrDok(), copy.nrDok());
        assertEquals(original.sklepId(), copy.sklepId());
        assertEquals(original.kontrahentId(), copy.kontrahentId());
        assertEquals(original.platnikId(), copy.platnikId());
        assertEquals(original.kasaId(), copy.kasaId());
        assertEquals(original.kasjerId(), copy.kasjerId());
        assertEquals(original.storno(), copy.storno());
    }
}
