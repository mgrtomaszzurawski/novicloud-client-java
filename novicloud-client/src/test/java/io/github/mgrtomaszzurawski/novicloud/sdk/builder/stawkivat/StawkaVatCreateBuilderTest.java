/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.stawkivat;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.stawkivat.StawkaVatCreateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StawkaVatCreateBuilderTest {

    private static final int ID_2300 = 2300;
    private static final int ID_800 = 800;
    private static final String OPIS = "VAT 8%";
    private static final String ETYKIETA = "8%";
    private static final int TB_BUILDER = 101;
    private static final String TB_OPIS = "test-opis";
    private static final String TB_ETYKIETA = "test-etykieta";


    @Test
    void build_whenRequiredIdOnly_optionalsAreNull() {
        // given / when
        StawkaVatCreateBuilder d = StawkaVatCreateBuilder.builder(ID_2300).build();

        // then
        assertEquals(ID_2300, d.id());
        assertNull(d.opis());
        assertNull(d.etykieta());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        StawkaVatCreateBuilder d = StawkaVatCreateBuilder.builder(ID_800)
                .opis(OPIS)
                .etykieta(ETYKIETA)
                .build();

        // then
        assertEquals(ID_800, d.id());
        assertEquals(OPIS, d.opis());
        assertEquals(ETYKIETA, d.etykieta());
    }

    @Test
    void builder_whenNullId_throwsIllegalArgument() {
        // given / when / then
        assertThrows(IllegalArgumentException.class,
                () -> StawkaVatCreateBuilder.builder(null));
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        StawkaVatCreateBuilder original = StawkaVatCreateBuilder.builder(TB_BUILDER)
                .opis(TB_OPIS)
                .etykieta(TB_ETYKIETA)
                .build();

        // when
        StawkaVatCreateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.id(), copy.id());
        assertEquals(original.opis(), copy.opis());
        assertEquals(original.etykieta(), copy.etykieta());
        assertEquals(original.id(), copy.id());
    }
}
