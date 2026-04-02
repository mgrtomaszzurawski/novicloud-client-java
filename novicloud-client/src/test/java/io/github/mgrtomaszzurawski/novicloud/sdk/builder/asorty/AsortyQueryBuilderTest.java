/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.asorty;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.asorty.AsortyQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AsortyQueryBuilderTest {

    private static final int START = 0;
    private static final String FTS = "napoje";
    private static final String ID = "5";
    private static final String NAZWA_FILTER = "~Piwne~";
    private static final int TB_START = 1;
    private static final String TB_FTS = "test-fts";
    private static final String TB_ID = "test-id";
    private static final String TB_NAZWA = "test-nazwa";
    private static final String TB_PARENT_ID = "test-parentId";


    @Test
    void build_whenDefault_allFieldsAreNull() {
        // given / when
        AsortyQueryBuilder q = AsortyQueryBuilder.builder().build();

        // then
        assertNull(q.start());
        assertNull(q.fts());
        assertNull(q.id());
        assertNull(q.nazwa());
        assertNull(q.parentId());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        AsortyQueryBuilder q = AsortyQueryBuilder.builder()
                .start(START)
                .fts(FTS)
                .id(ID)
                .nazwa(NAZWA_FILTER)
                .parentId(AsortTestConstants.PARENT_ID)
                .build();

        // then
        assertEquals(START, q.start());
        assertEquals(FTS, q.fts());
        assertEquals(ID, q.id());
        assertEquals(NAZWA_FILTER, q.nazwa());
        assertEquals(AsortTestConstants.PARENT_ID, q.parentId());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        AsortyQueryBuilder original = AsortyQueryBuilder.builder()
                .start(TB_START)
                .fts(TB_FTS)
                .id(TB_ID)
                .nazwa(TB_NAZWA)
                .parentId(TB_PARENT_ID)
                .build();

        // when
        AsortyQueryBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.start(), copy.start());
        assertEquals(original.fts(), copy.fts());
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.parentId(), copy.parentId());
    }
}
