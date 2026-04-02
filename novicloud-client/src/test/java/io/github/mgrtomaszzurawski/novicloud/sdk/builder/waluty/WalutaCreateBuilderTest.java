/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.waluty;

import io.github.mgrtomaszzurawski.novicloud.sdk.resources.waluty.WalutaCreateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WalutaCreateBuilderTest {

    private static final long ID = 5L;
    private static final double KURS = 4.25;
    private static final long TB_ID = 1L;
    private static final double TB_KURS = 4.8;
    private static final String TB_NAZWA = "test-nazwa";
    private static final String TB_KOD = "test-kod";


    @Test
    void build_whenRequiredOnly_optionalsAreNull() {
        // given / when
        WalutaCreateBuilder d = WalutaCreateBuilder.builder(WalutaTestConstants.NAZWA, WalutaTestConstants.KOD).build();

        // then
        assertEquals(WalutaTestConstants.NAZWA, d.nazwa());
        assertEquals(WalutaTestConstants.KOD, d.kod());
        assertNull(d.id());
        assertNull(d.kurs());
        assertNull(d.domyslna());
        assertNull(d.aktywny());
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        WalutaCreateBuilder d = WalutaCreateBuilder.builder(WalutaTestConstants.NAZWA, WalutaTestConstants.KOD)
                .id(ID)
                .kurs(KURS)
                .domyslna(false)
                .aktywny(true)
                .build();

        // then
        assertEquals(WalutaTestConstants.NAZWA, d.nazwa());
        assertEquals(WalutaTestConstants.KOD, d.kod());
        assertEquals(ID, d.id());
        assertEquals(KURS, d.kurs(), WalutaTestConstants.DELTA);
        assertFalse(d.domyslna());
        assertTrue(d.aktywny());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        WalutaCreateBuilder original = WalutaCreateBuilder.builder(TB_NAZWA, TB_KOD)
                .id(TB_ID)
                .kurs(TB_KURS)
                .domyslna(true)
                .aktywny(true)
                .build();

        // when
        WalutaCreateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.kod(), copy.kod());
        assertEquals(original.kurs(), copy.kurs());
        assertEquals(original.domyslna(), copy.domyslna());
        assertEquals(original.aktywny(), copy.aktywny());
    }
}
