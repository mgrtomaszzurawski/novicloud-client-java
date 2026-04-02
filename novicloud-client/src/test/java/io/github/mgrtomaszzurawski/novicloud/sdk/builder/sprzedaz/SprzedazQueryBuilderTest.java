/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.sprzedaz;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.sprzedaz.SprzedazQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SprzedazQueryBuilderTest {

    private static final int START = 0;
    private static final String ID = "1";
    private static final String DATA = "min2024-01-01";
    private static final String TYP_DOK = "PA";
    private static final String TOWAR_ID = "10";
    private static final String SKLEP_ID = "2";
    private static final String KONTRAHENT_ID = "5";
    private static final int TB_START = 1;
    private static final String TB_ID = "test-id";
    private static final String TB_DATA = "test-data";
    private static final String TB_NR_DOK = "test-nrDok";
    private static final String TB_TYP_DOK = "test-typDok";
    private static final String TB_NR_SYSTEMOWY = "test-nrSystemowy";
    private static final String TB_NR_FISKALNY = "test-nrFiskalny";
    private static final String TB_ILOSC = "test-ilosc";
    private static final String TB_CENA = "test-cena";
    private static final String TB_STAWKA_VAT = "test-stawkaVat";
    private static final String TB_BRUTTO = "test-brutto";
    private static final String TB_PODATEK = "test-podatek";
    private static final String TB_RABAT = "test-rabat";
    private static final String TB_TOWAR_ID = "test-towarId";
    private static final String TB_SKLEP_ID = "test-sklepId";
    private static final String TB_KASA_ID = "test-kasaId";
    private static final String TB_KASJER_ID = "test-kasjerId";
    private static final String TB_KONTRAHENT_ID = "test-kontrahentId";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        SprzedazQueryBuilder q = SprzedazQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.id());
        assertNull(q.data());
        assertNull(q.nrDok());
        assertNull(q.typDok());
        assertNull(q.towarId());
        assertNull(q.sklepId());
        assertNull(q.kasaId());
        assertNull(q.kasjerId());
        assertNull(q.kontrahentId());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        SprzedazQueryBuilder q = SprzedazQueryBuilder.builder()
                .start(START)
                .id(ID)
                .data(DATA)
                .typDok(TYP_DOK)
                .towarId(TOWAR_ID)
                .sklepId(SKLEP_ID)
                .kontrahentId(KONTRAHENT_ID)
                .build();

        // then
        assertEquals(START, q.start());
        assertEquals(ID, q.id());
        assertEquals(DATA, q.data());
        assertEquals(TYP_DOK, q.typDok());
        assertEquals(TOWAR_ID, q.towarId());
        assertEquals(SKLEP_ID, q.sklepId());
        assertEquals(KONTRAHENT_ID, q.kontrahentId());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        SprzedazQueryBuilder original = SprzedazQueryBuilder.builder()
                .start(TB_START)
                .id(TB_ID)
                .data(TB_DATA)
                .nrDok(TB_NR_DOK)
                .typDok(TB_TYP_DOK)
                .nrSystemowy(TB_NR_SYSTEMOWY)
                .nrFiskalny(TB_NR_FISKALNY)
                .ilosc(TB_ILOSC)
                .cena(TB_CENA)
                .stawkaVat(TB_STAWKA_VAT)
                .brutto(TB_BRUTTO)
                .podatek(TB_PODATEK)
                .rabat(TB_RABAT)
                .towarId(TB_TOWAR_ID)
                .sklepId(TB_SKLEP_ID)
                .kasaId(TB_KASA_ID)
                .kasjerId(TB_KASJER_ID)
                .kontrahentId(TB_KONTRAHENT_ID)
                .build();

        // when
        SprzedazQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.id(), copy.id());
        assertEquals(original.data(), copy.data());
        assertEquals(original.nrDok(), copy.nrDok());
        assertEquals(original.typDok(), copy.typDok());
        assertEquals(original.nrSystemowy(), copy.nrSystemowy());
        assertEquals(original.nrFiskalny(), copy.nrFiskalny());
        assertEquals(original.ilosc(), copy.ilosc());
        assertEquals(original.cena(), copy.cena());
        assertEquals(original.stawkaVat(), copy.stawkaVat());
        assertEquals(original.brutto(), copy.brutto());
        assertEquals(original.podatek(), copy.podatek());
        assertEquals(original.rabat(), copy.rabat());
        assertEquals(original.towarId(), copy.towarId());
        assertEquals(original.sklepId(), copy.sklepId());
        assertEquals(original.kasaId(), copy.kasaId());
        assertEquals(original.kasjerId(), copy.kasjerId());
        assertEquals(original.kontrahentId(), copy.kontrahentId());
    }
}
