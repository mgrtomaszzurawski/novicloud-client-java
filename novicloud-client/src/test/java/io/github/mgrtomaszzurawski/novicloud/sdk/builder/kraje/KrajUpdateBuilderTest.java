/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.kraje;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kraje.KrajUpdateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KrajUpdateBuilderTest {

    private static final long TB_BUILDER = 101L;
    private static final String TB_NAZWA = "test-nazwa";
    private static final String TB_KOD = "test-kod";
    private static final String TB_WALUTA_ID = "test-walutaId";

    @Test
    void build_whenIdOnly_optionalsAreNull() {
        // given / when
        KrajUpdateBuilder d = KrajUpdateBuilder.builder(KrajTestConstants.ID).build();

        // then
        assertEquals(KrajTestConstants.ID, d.id());
        assertNull(d.nazwa());
        assertNull(d.kod());
        assertNull(d.walutaId());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        KrajUpdateBuilder d = KrajUpdateBuilder.builder(KrajTestConstants.ID)
                .nazwa(KrajTestConstants.NAZWA)
                .kod(KrajTestConstants.KOD)
                .walutaId(KrajTestConstants.WALUTA_ID)
                .build();

        // then
        assertEquals(KrajTestConstants.ID, d.id());
        assertEquals(KrajTestConstants.NAZWA, d.nazwa());
        assertEquals(KrajTestConstants.KOD, d.kod());
        assertEquals(KrajTestConstants.WALUTA_ID, d.walutaId());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        KrajUpdateBuilder original = KrajUpdateBuilder.builder(TB_BUILDER)
                .nazwa(TB_NAZWA)
                .kod(TB_KOD)
                .walutaId(TB_WALUTA_ID)
                .build();

        // when
        KrajUpdateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.kod(), copy.kod());
        assertEquals(original.walutaId(), copy.walutaId());
    }
}
