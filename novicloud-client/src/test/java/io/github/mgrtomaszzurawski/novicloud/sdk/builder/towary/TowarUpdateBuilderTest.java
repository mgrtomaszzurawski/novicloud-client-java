/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.towary;

import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarKodDodatkowy;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary.TowarUpdateBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TowarUpdateBuilderTest {

    private static final long ID_42 = 42L;
    private static final long ID_1 = 1L;
    private static final String NAZWA = "Zaktualizowany";
    private static final int STAWKA_VAT = 800;
    private static final long TB_BUILDER = 101L;
    private static final String TB_NAZWA = "test-nazwa";
    private static final String TB_KOD = "test-kod";
    private static final String TB_CKU = "test-cku";
    private static final int TB_STAWKA_VAT = 5;
    private static final int TB_TYP = 7;
    private static final double TB_CENA_EW = 8.12;
    private static final double TB_CENA_DET = 9.13;
    private static final double TB_CENA_HURT = 10.14;
    private static final double TB_CENA_NOC = 11.15;
    private static final double TB_CENA_DOD = 12.16;
    private static final int TB_PRZY_SPRZEDAZY = 13;
    private static final String TB_GTU = "test-gtu";
    private static final String TB_PKWIU = "test-pkwiu";
    private static final double TB_MASA_WL = 16.20;
    private static final String TB_JM_ID = "test-jmId";
    private static final String TB_ASORT_ID = "test-asortId";
    private static final String TB_OPIS1 = "test-opis1";
    private static final String TB_OPIS2 = "test-opis2";
    private static final String TB_OPIS3 = "test-opis3";
    private static final String TB_OPIS4 = "test-opis4";
    private static final String TB_OPIS5 = "test-opis5";


    @Test
    void build_whenIdOnly_optionalsAreNull() {
        // given / when
        TowarUpdateBuilder d = TowarUpdateBuilder.builder(ID_42).build();

        // then
        assertEquals(ID_42, d.id());
        assertNull(d.nazwa());
        assertNull(d.stawkaVat());
        assertNull(d.aktywny());
        assertNull(d.kodyDod());
        assertNull(d.cenyWSklepach());
        assertNull(d.skladniki());
    }

    @Test
    void build_whenKodyDodSet_roundTripsThroughToBuilder() {
        // given
        TowarUpdateBuilder original = TowarUpdateBuilder.builder(ID_42)
                .kodyDod(List.of(new TowarKodDodatkowy("5901234123457", 6.0, 2)))
                .build();

        // when
        TowarUpdateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(1, copy.kodyDod().size());
        assertEquals("5901234123457", copy.kodyDod().get(0).kod());
        assertEquals(original.kodyDod(), copy.kodyDod());
        // and the accessor returns an unmodifiable copy
        List<TowarKodDodatkowy> returned = copy.kodyDod();
        assertThrows(UnsupportedOperationException.class,
                () -> returned.add(new TowarKodDodatkowy("0", 1.0, 1)));
    }

    @Test
    void build_whenAllFieldsSet_allValuesPreserved() {
        // given / when
        TowarUpdateBuilder d = TowarUpdateBuilder.builder(ID_1)
                .nazwa(NAZWA)
                .stawkaVat(STAWKA_VAT)
                .aktywny(true)
                .build();

        // then
        assertEquals(ID_1, d.id());
        assertEquals(NAZWA, d.nazwa());
        assertEquals(STAWKA_VAT, d.stawkaVat());
        assertTrue(d.aktywny());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        TowarUpdateBuilder original = TowarUpdateBuilder.builder(TB_BUILDER)
                .nazwa(TB_NAZWA)
                .kod(TB_KOD)
                .cku(TB_CKU)
                .stawkaVat(TB_STAWKA_VAT)
                .akcyzowy(true)
                .typ(TB_TYP)
                .cenaEw(TB_CENA_EW)
                .cenaDet(TB_CENA_DET)
                .cenaHurt(TB_CENA_HURT)
                .cenaNoc(TB_CENA_NOC)
                .cenaDod(TB_CENA_DOD)
                .przySprzedazy(TB_PRZY_SPRZEDAZY)
                .gtu(TB_GTU)
                .pkwiu(TB_PKWIU)
                .masaWl(TB_MASA_WL)
                .aktywny(true)
                .jmId(TB_JM_ID)
                .asortId(TB_ASORT_ID)
                .opis1(TB_OPIS1)
                .opis2(TB_OPIS2)
                .opis3(TB_OPIS3)
                .opis4(TB_OPIS4)
                .opis5(TB_OPIS5)
                .build();

        // when
        TowarUpdateBuilder copy = original.toBuilder().build();

        // then
        assertEquals(original.id(), copy.id());
        assertEquals(original.nazwa(), copy.nazwa());
        assertEquals(original.kod(), copy.kod());
        assertEquals(original.cku(), copy.cku());
        assertEquals(original.stawkaVat(), copy.stawkaVat());
        assertEquals(original.akcyzowy(), copy.akcyzowy());
        assertEquals(original.typ(), copy.typ());
        assertEquals(original.cenaEw(), copy.cenaEw());
        assertEquals(original.cenaDet(), copy.cenaDet());
        assertEquals(original.cenaHurt(), copy.cenaHurt());
        assertEquals(original.cenaNoc(), copy.cenaNoc());
        assertEquals(original.cenaDod(), copy.cenaDod());
        assertEquals(original.przySprzedazy(), copy.przySprzedazy());
        assertEquals(original.gtu(), copy.gtu());
        assertEquals(original.pkwiu(), copy.pkwiu());
        assertEquals(original.masaWl(), copy.masaWl());
        assertEquals(original.aktywny(), copy.aktywny());
        assertEquals(original.jmId(), copy.jmId());
        assertEquals(original.asortId(), copy.asortId());
        assertEquals(original.opis1(), copy.opis1());
        assertEquals(original.opis2(), copy.opis2());
        assertEquals(original.opis3(), copy.opis3());
        assertEquals(original.opis4(), copy.opis4());
        assertEquals(original.opis5(), copy.opis5());
    }
}
