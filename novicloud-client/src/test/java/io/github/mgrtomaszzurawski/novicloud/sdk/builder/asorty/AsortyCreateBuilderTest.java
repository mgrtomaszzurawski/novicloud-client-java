/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.asorty;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.asorty.AsortyCreateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AsortyCreateBuilderTest {

    private static final long ID = 10L;
    private static final String TB_BUILDER = "test-nazwa";
    private static final long TB_ID = 1L;
    private static final String TB_PARENT_ID = "test-parentId";


    @Test
    void build_whenRequiredOnly_optionalsAreNull() {
        // given / when
        AsortyCreateBuilder d = AsortyCreateBuilder.builder(AsortTestConstants.NAZWA).build();

        // then
        assertEquals(AsortTestConstants.NAZWA, d.nazwa());
        assertNull(d.id());
        assertNull(d.parentId());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        AsortyCreateBuilder d = AsortyCreateBuilder.builder(AsortTestConstants.NAZWA)
                .id(ID)
                .parentId(AsortTestConstants.PARENT_ID)
                .build();

        // then
        assertEquals(AsortTestConstants.NAZWA, d.nazwa());
        assertEquals(ID, d.id());
        assertEquals(AsortTestConstants.PARENT_ID, d.parentId());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        AsortyCreateBuilder original = AsortyCreateBuilder.builder(TB_BUILDER)
                .id(TB_ID)
                .parentId(TB_PARENT_ID)
                .build();

        // when
        AsortyCreateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.parentId(), copy.parentId());
    }
}
