/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.jmiary;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.jmiary.JmiaryUpdateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JmiaryUpdateBuilderTest {

    private static final long ID = 3L;
    private static final String NAZWA = "kilogram";
    private static final int PRECYZJA = 2;
    private static final long TB_BUILDER = 101L;
    private static final String TB_NAZWA = "test-nazwa";
    private static final int TB_PRECYZJA = 3;


    @Test
    void build_whenIdOnly_optionalsAreNull() {
        // given / when
        JmiaryUpdateBuilder d = JmiaryUpdateBuilder.builder(ID).build();

        // then
        assertEquals(ID, d.id());
        assertNull(d.nazwa());
        assertNull(d.precyzja());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        JmiaryUpdateBuilder d = JmiaryUpdateBuilder.builder(ID)
                .nazwa(NAZWA)
                .precyzja(PRECYZJA)
                .build();

        // then
        assertEquals(ID, d.id());
        assertEquals(NAZWA, d.nazwa());
        assertEquals(PRECYZJA, d.precyzja());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        JmiaryUpdateBuilder original = JmiaryUpdateBuilder.builder(TB_BUILDER)
                .nazwa(TB_NAZWA)
                .precyzja(TB_PRECYZJA)
                .build();

        // when
        JmiaryUpdateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.precyzja(), copy.precyzja());
    }
}
