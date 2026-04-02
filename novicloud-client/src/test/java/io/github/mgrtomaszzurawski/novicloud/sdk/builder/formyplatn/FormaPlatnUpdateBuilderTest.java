/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.formyplatn;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.formyplatn.FormaPlatnUpdateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormaPlatnUpdateBuilderTest {

    private static final long ID = 3L;
    private static final String NAZWA = "Przelew";
    private static final int TYP = 2;
    private static final long TB_BUILDER = 101L;
    private static final String TB_NAZWA = "test-nazwa";
    private static final int TB_TYP = 3;


    @Test
    void build_whenIdOnly_optionalsAreNull() {
        // given / when
        FormaPlatnUpdateBuilder d = FormaPlatnUpdateBuilder.builder(ID).build();

        // then
        assertEquals(ID, d.id());
        assertNull(d.nazwa());
        assertNull(d.typ());
        assertNull(d.reszta());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        FormaPlatnUpdateBuilder d = FormaPlatnUpdateBuilder.builder(ID)
                .nazwa(NAZWA)
                .typ(TYP)
                .reszta(false)
                .build();

        // then
        assertEquals(ID, d.id());
        assertEquals(NAZWA, d.nazwa());
        assertEquals(TYP, d.typ());
        assertFalse(d.reszta());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        FormaPlatnUpdateBuilder original = FormaPlatnUpdateBuilder.builder(TB_BUILDER)
                .nazwa(TB_NAZWA)
                .typ(TB_TYP)
                .reszta(true)
                .aktywny(true)
                .build();

        // when
        FormaPlatnUpdateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.typ(), copy.typ());
        assertEquals(original.reszta(), copy.reszta());
        assertEquals(original.aktywny(), copy.aktywny());
    }
}
