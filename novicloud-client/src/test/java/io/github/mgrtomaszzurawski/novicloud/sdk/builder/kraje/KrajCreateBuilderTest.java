/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.kraje;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.kraje.KrajCreateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KrajCreateBuilderTest {

    private static final long TB_ID = 1L;
    private static final String TB_WALUTA_ID = "test-walutaId";
    private static final String TB_NAZWA = "test-nazwa";
    private static final String TB_KOD = "test-kod";

    @Test
    void build_whenRequiredOnly_optionalsAreNull() {
        // given / when
        KrajCreateBuilder d = KrajCreateBuilder.builder(KrajTestConstants.NAZWA, KrajTestConstants.KOD).build();

        // then
        assertEquals(KrajTestConstants.NAZWA, d.nazwa());
        assertEquals(KrajTestConstants.KOD, d.kod());
        assertNull(d.id());
        assertNull(d.walutaId());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        KrajCreateBuilder d = KrajCreateBuilder.builder(KrajTestConstants.NAZWA, KrajTestConstants.KOD)
                .id(KrajTestConstants.ID)
                .walutaId(KrajTestConstants.WALUTA_ID)
                .build();

        // then
        assertEquals(KrajTestConstants.NAZWA, d.nazwa());
        assertEquals(KrajTestConstants.KOD, d.kod());
        assertEquals(KrajTestConstants.ID, d.id());
        assertEquals(KrajTestConstants.WALUTA_ID, d.walutaId());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        KrajCreateBuilder original = KrajCreateBuilder.builder(TB_NAZWA, TB_KOD)
                .id(TB_ID)
                .walutaId(TB_WALUTA_ID)
                .build();

        // when
        KrajCreateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.kod(), copy.kod());
        assertEquals(original.walutaId(), copy.walutaId());
    }
}
