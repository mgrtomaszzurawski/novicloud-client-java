/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.jmiary;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.jmiary.JmiaryQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JmiaryQueryBuilderTest {

    private static final int START = 2;
    private static final String ID = "1";
    private static final String NAZWA_FILTER = "~kilo~";
    private static final int TB_START = 1;
    private static final String TB_FTS = "test-fts";
    private static final String TB_ID = "test-id";
    private static final String TB_NAZWA = "test-nazwa";
    private static final String TB_PRECYZJA = "test-precyzja";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        JmiaryQueryBuilder q = JmiaryQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.fts());
        assertNull(q.id());
        assertNull(q.nazwa());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        JmiaryQueryBuilder q = JmiaryQueryBuilder.builder()
                .start(START).fts(JmiaraTestConstants.NAZWA_KG).id(ID).nazwa(NAZWA_FILTER).build();

        // then
        assertEquals(START, q.start());
        assertEquals(JmiaraTestConstants.NAZWA_KG, q.fts());
        assertEquals(ID, q.id());
        assertEquals(NAZWA_FILTER, q.nazwa());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        JmiaryQueryBuilder original = JmiaryQueryBuilder.builder()
                .start(TB_START)
                .fts(TB_FTS)
                .id(TB_ID)
                .nazwa(TB_NAZWA)
                .precyzja(TB_PRECYZJA)
                .build();

        // when
        JmiaryQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.fts(), copy.fts());
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.precyzja(), copy.precyzja());
    }
}
