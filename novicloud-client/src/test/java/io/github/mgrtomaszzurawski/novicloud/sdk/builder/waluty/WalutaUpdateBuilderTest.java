/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.waluty;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.waluty.WalutaUpdateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WalutaUpdateBuilderTest {

    private static final long ID = 2L;
    private static final double KURS = 4.30;
    private static final long TB_BUILDER = 101L;
    private static final String TB_NAZWA = "test-nazwa";
    private static final String TB_KOD = "test-kod";
    private static final double TB_KURS = 4.8;


    @Test
    void build_whenIdOnly_optionalsAreNull() {
        // given / when
        WalutaUpdateBuilder d = WalutaUpdateBuilder.builder(ID).build();

        // then
        assertEquals(ID, d.id());
        assertNull(d.nazwa());
        assertNull(d.kod());
        assertNull(d.kurs());
        assertNull(d.domyslna());
        assertNull(d.aktywny());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        WalutaUpdateBuilder d = WalutaUpdateBuilder.builder(ID)
                .nazwa(WalutaTestConstants.NAZWA)
                .kod(WalutaTestConstants.KOD)
                .kurs(KURS)
                .domyslna(false)
                .aktywny(true)
                .build();

        // then
        assertEquals(ID, d.id());
        assertEquals(WalutaTestConstants.NAZWA, d.nazwa());
        assertEquals(WalutaTestConstants.KOD, d.kod());
        assertEquals(KURS, d.kurs(), WalutaTestConstants.DELTA);
        assertFalse(d.domyslna());
        assertTrue(d.aktywny());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        WalutaUpdateBuilder original = WalutaUpdateBuilder.builder(TB_BUILDER)
                .nazwa(TB_NAZWA)
                .kod(TB_KOD)
                .kurs(TB_KURS)
                .domyslna(true)
                .aktywny(true)
                .build();

        // when
        WalutaUpdateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.kod(), copy.kod());
        assertEquals(original.kurs(), copy.kurs());
        assertEquals(original.domyslna(), copy.domyslna());
        assertEquals(original.aktywny(), copy.aktywny());
    }
}
