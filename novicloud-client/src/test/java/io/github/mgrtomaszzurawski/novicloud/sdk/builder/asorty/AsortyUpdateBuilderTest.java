/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.asorty;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.asorty.AsortyUpdateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AsortyUpdateBuilderTest {

    private static final long ID = 5L;
    private static final String NAZWA = "Zaktualizowany";
    private static final String PARENT_ID_2 = "2";
    private static final long TB_BUILDER = 101L;
    private static final String TB_NAZWA = "test-nazwa";
    private static final String TB_PARENT_ID = "test-parentId";


    @Test
    void build_whenIdOnly_optionalsAreNull() {
        // given / when
        AsortyUpdateBuilder d = AsortyUpdateBuilder.builder(ID).build();

        // then
        assertEquals(ID, d.id());
        assertNull(d.nazwa());
        assertNull(d.parentId());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        AsortyUpdateBuilder d = AsortyUpdateBuilder.builder(ID)
                .nazwa(NAZWA)
                .parentId(PARENT_ID_2)
                .build();

        // then
        assertEquals(ID, d.id());
        assertEquals(NAZWA, d.nazwa());
        assertEquals(PARENT_ID_2, d.parentId());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        AsortyUpdateBuilder original = AsortyUpdateBuilder.builder(TB_BUILDER)
                .nazwa(TB_NAZWA)
                .parentId(TB_PARENT_ID)
                .build();

        // when
        AsortyUpdateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.parentId(), copy.parentId());
    }
}
