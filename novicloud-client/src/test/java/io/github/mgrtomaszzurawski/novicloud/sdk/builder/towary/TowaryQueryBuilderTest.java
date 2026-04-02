/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.towary;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary.TowarQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TowaryQueryBuilderTest {

    private static final int START = 10;
    private static final String FTS = "napoj";
    private static final String ID = "1,2,3";
    private static final String NAZWA_FILTER = "~piwo~";
    private static final String KOD = "ABC123";
    private static final String STAWKA_VAT = "2300";
    private static final String JM_ID = "1";
    private static final String ASORT_ID = "2";
    private static final String BUILDER_NAZWA_1 = "A";
    private static final String BUILDER_NAZWA_2 = "B";
    private static final int TB_START = 1;
    private static final String TB_FTS = "test-fts";
    private static final String TB_ID = "test-id";
    private static final String TB_NAZWA = "test-nazwa";
    private static final String TB_KOD = "test-kod";
    private static final String TB_STAWKA_VAT = "test-stawkaVat";
    private static final String TB_JM_ID = "test-jmId";
    private static final String TB_ASORT_ID = "test-asortId";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        TowarQueryBuilder q = TowarQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.fts());
        assertNull(q.id());
        assertNull(q.nazwa());
        assertNull(q.kod());
        assertNull(q.stawkaVat());
        assertNull(q.akcyzowy());
        assertNull(q.jmId());
        assertNull(q.asortId());
        assertNull(q.aktywny());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        TowarQueryBuilder q = TowarQueryBuilder.builder()
                .start(START)
                .fts(FTS)
                .id(ID)
                .nazwa(NAZWA_FILTER)
                .kod(KOD)
                .stawkaVat(STAWKA_VAT)
                .akcyzowy(true)
                .jmId(JM_ID)
                .asortId(ASORT_ID)
                .aktywny(true)
                .build();

        // then
        assertEquals(START, q.start());
        assertEquals(FTS, q.fts());
        assertEquals(ID, q.id());
        assertEquals(NAZWA_FILTER, q.nazwa());
        assertEquals(KOD, q.kod());
        assertEquals(STAWKA_VAT, q.stawkaVat());
        assertTrue(q.akcyzowy());
        assertEquals(JM_ID, q.jmId());
        assertEquals(ASORT_ID, q.asortId());
        assertTrue(q.aktywny());
    }

    @Test
    void build_whenTwoBuildersCreated_theyAreIndependent() {
        // given / when
        TowarQueryBuilder q1 = TowarQueryBuilder.builder().nazwa(BUILDER_NAZWA_1).build();
        TowarQueryBuilder q2 = TowarQueryBuilder.builder().nazwa(BUILDER_NAZWA_2).build();

        // then
        assertEquals(BUILDER_NAZWA_1, q1.nazwa());
        assertEquals(BUILDER_NAZWA_2, q2.nazwa());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        TowarQueryBuilder original = TowarQueryBuilder.builder()
                .start(TB_START)
                .fts(TB_FTS)
                .id(TB_ID)
                .nazwa(TB_NAZWA)
                .kod(TB_KOD)
                .stawkaVat(TB_STAWKA_VAT)
                .akcyzowy(true)
                .jmId(TB_JM_ID)
                .asortId(TB_ASORT_ID)
                .aktywny(true)
                .build();

        // when
        TowarQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.fts(), copy.fts());
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.kod(), copy.kod());
        assertEquals(original.stawkaVat(), copy.stawkaVat());
        assertEquals(original.akcyzowy(), copy.akcyzowy());
        assertEquals(original.jmId(), copy.jmId());
        assertEquals(original.asortId(), copy.asortId());
        assertEquals(original.aktywny(), copy.aktywny());
    }
}
