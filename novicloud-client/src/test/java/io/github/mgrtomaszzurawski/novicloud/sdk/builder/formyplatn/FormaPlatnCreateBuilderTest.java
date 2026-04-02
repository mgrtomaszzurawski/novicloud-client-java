/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.formyplatn;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.formyplatn.FormaPlatnCreateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormaPlatnCreateBuilderTest {

    private static final String NAZWA = "Gotowka";
    private static final int TYP = 1;
    private static final long ID = 10L;
    private static final long TB_ID = 1L;
    private static final int TB_TYP = 2;
    private static final String TB_NAZWA = "test-nazwa";


    @Test
    void build_whenRequiredOnly_optionalsAreNull() {
        // given / when
        FormaPlatnCreateBuilder d = FormaPlatnCreateBuilder.builder(NAZWA, TYP).build();

        // then
        assertEquals(NAZWA, d.nazwa());
        assertEquals(TYP, d.typ());
        assertNull(d.id());
        assertNull(d.reszta());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        FormaPlatnCreateBuilder d = FormaPlatnCreateBuilder.builder(NAZWA, TYP)
                .id(ID)
                .reszta(true)
                .build();

        // then
        assertEquals(NAZWA, d.nazwa());
        assertEquals(TYP, d.typ());
        assertEquals(ID, d.id());
        assertTrue(d.reszta());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        FormaPlatnCreateBuilder original = FormaPlatnCreateBuilder.builder(TB_NAZWA, TB_TYP)
                .id(TB_ID)
                .reszta(true)
                .build();

        // when
        FormaPlatnCreateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.typ(), copy.typ());
        assertEquals(original.reszta(), copy.reszta());
    }
}
