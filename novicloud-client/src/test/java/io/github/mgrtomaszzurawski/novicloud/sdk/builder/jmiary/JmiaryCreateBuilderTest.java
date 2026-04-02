/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.jmiary;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.jmiary.JmiaryCreateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JmiaryCreateBuilderTest {

    private static final long ID = 5L;
    private static final int PRECYZJA = 3;
    private static final String TB_BUILDER = "test-nazwa";
    private static final long TB_ID = 1L;


    @Test
    void build_whenRequiredOnly_optionalsAreNull() {
        // given / when
        JmiaryCreateBuilder d = JmiaryCreateBuilder.builder(JmiaraTestConstants.NAZWA_KG).build();

        // then
        assertEquals(JmiaraTestConstants.NAZWA_KG, d.nazwa());
        assertNull(d.id());
        assertNull(d.precyzja());
    }

    @Test
    void build_whenWithOptionals_allValuesPreserved() {
        // given / when
        JmiaryCreateBuilder d = JmiaryCreateBuilder.builder(JmiaraTestConstants.NAZWA_KG)
                .id(ID)
                .precyzja(PRECYZJA)
                .build();

        // then
        assertEquals(JmiaraTestConstants.NAZWA_KG, d.nazwa());
        assertEquals(ID, d.id());
        assertEquals(PRECYZJA, d.precyzja());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        JmiaryCreateBuilder original = JmiaryCreateBuilder.builder(TB_BUILDER)
                .id(TB_ID)
                .precyzja(PRECYZJA)
                .build();

        // when
        JmiaryCreateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.precyzja(), copy.precyzja());
    }
}
