/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.builder.towary;

import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarCenaWSklepie;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarKodDodatkowy;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarSkladnik;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarSkladnikTowar;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary.TowarCreateBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TowarCreateBuilderTest {

    private static final String KOD_001 = "KOD-001";
    private static final String NAZWA_TOWAR_TESTOWY = "Towar testowy";
    private static final String KOD_002 = "KOD-002";
    private static final String NAZWA_TEST = "Test";
    private static final long ID = 5L;
    private static final int STAWKA_VAT = 2300;
    private static final int TYP = 0;
    private static final double CENA_DET = 9.99;
    private static final double DELTA = 0.001;
    private static final long TB_ID = 1L;
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
    private static final String TB_KOD = "test-kod";
    private static final String TB_NAZWA = "test-nazwa";

    private static final String BARCODE = "5901234123457";
    private static final double ILE_W_OPAK = 6.0;
    private static final int POZIOM_CEN = 2;
    private static final String SKLEP_ID = "3";
    private static final double SKLEP_CENA_DET = 14.50;
    private static final String SKLADNIK_NAZWA = "Sos";
    private static final double SKLADNIK_CENA = 2.0;
    private static final String SKLADNIK_TOWAR_ID = "42";
    private static final double SKLADNIK_TOWAR_ILOSC = 1.0;


    @Test
    void build_whenRequiredOnly_optionalsAreNull() {
        // given / when
        TowarCreateBuilder d = TowarCreateBuilder.builder(KOD_001, NAZWA_TOWAR_TESTOWY).build();

        // then
        assertEquals(KOD_001, d.kod());
        assertEquals(NAZWA_TOWAR_TESTOWY, d.nazwa());
        assertNull(d.id());
        assertNull(d.stawkaVat());
        assertNull(d.aktywny());
        assertNull(d.kodyDod());
        assertNull(d.cenyWSklepach());
        assertNull(d.skladniki());
    }

    @Test
    void build_whenNestedListsSet_valuesPreserved() {
        // given / when
        TowarCreateBuilder d = TowarCreateBuilder.builder(KOD_001, NAZWA_TOWAR_TESTOWY)
                .kodyDod(List.of(new TowarKodDodatkowy(BARCODE, ILE_W_OPAK, POZIOM_CEN)))
                .cenyWSklepach(List.of(new TowarCenaWSklepie(
                        SKLEP_ID, null, SKLEP_CENA_DET, null, null, null, null)))
                .skladniki(List.of(new TowarSkladnik(
                        SKLADNIK_NAZWA, SKLADNIK_CENA, true, false, false,
                        List.of(new TowarSkladnikTowar(
                                SKLADNIK_TOWAR_ID, SKLADNIK_TOWAR_ILOSC, true, null, true)))))
                .build();

        // then
        assertEquals(1, d.kodyDod().size());
        assertEquals(BARCODE, d.kodyDod().get(0).kod());
        assertEquals(ILE_W_OPAK, d.kodyDod().get(0).ileWOpak(), DELTA);
        assertEquals(POZIOM_CEN, d.kodyDod().get(0).poziomCen());

        assertEquals(1, d.cenyWSklepach().size());
        assertEquals(SKLEP_ID, d.cenyWSklepach().get(0).sklepId());
        assertEquals(SKLEP_CENA_DET, d.cenyWSklepach().get(0).cenaDet(), DELTA);

        assertEquals(1, d.skladniki().size());
        assertEquals(SKLADNIK_NAZWA, d.skladniki().get(0).nazwa());
        assertEquals(1, d.skladniki().get(0).towary().size());
        assertEquals(SKLADNIK_TOWAR_ID, d.skladniki().get(0).towary().get(0).towarId());
    }

    @Test
    void kodyDod_accessor_returnsDefensiveCopy() {
        // given
        List<TowarKodDodatkowy> source = new ArrayList<>();
        source.add(new TowarKodDodatkowy(BARCODE, ILE_W_OPAK, POZIOM_CEN));
        TowarCreateBuilder d = TowarCreateBuilder.builder(KOD_001, NAZWA_TOWAR_TESTOWY)
                .kodyDod(source)
                .build();

        // when - mutating the source list must not affect the built DTO
        source.clear();

        // then
        assertEquals(1, d.kodyDod().size());
        // and the returned list is unmodifiable
        List<TowarKodDodatkowy> returned = d.kodyDod();
        assertThrows(UnsupportedOperationException.class,
                () -> returned.add(new TowarKodDodatkowy(BARCODE, ILE_W_OPAK, POZIOM_CEN)));
    }

    @Test
    void build_whenAllCoreFieldsSet_allValuesPreserved() {
        // given / when
        TowarCreateBuilder d = TowarCreateBuilder.builder(KOD_002, NAZWA_TEST)
                .id(ID)
                .stawkaVat(STAWKA_VAT)
                .akcyzowy(false)
                .typ(TYP)
                .cenaDet(CENA_DET)
                .aktywny(true)
                .build();

        // then
        assertEquals(KOD_002, d.kod());
        assertEquals(NAZWA_TEST, d.nazwa());
        assertEquals(ID, d.id());
        assertEquals(STAWKA_VAT, d.stawkaVat());
        assertFalse(d.akcyzowy());
        assertEquals(TYP, d.typ());
        assertEquals(CENA_DET, d.cenaDet(), DELTA);
        assertTrue(d.aktywny());
    }

    @Test
    void toBuilder_whenAllFieldsSet_preservesAllFields() {
        // given
        TowarCreateBuilder original = TowarCreateBuilder.builder(TB_KOD, TB_NAZWA)
                .id(TB_ID)
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
                .kodyDod(List.of(new TowarKodDodatkowy(BARCODE, ILE_W_OPAK, POZIOM_CEN)))
                .cenyWSklepach(List.of(new TowarCenaWSklepie(
                        SKLEP_ID, null, SKLEP_CENA_DET, null, null, null, null)))
                .skladniki(List.of(new TowarSkladnik(
                        SKLADNIK_NAZWA, SKLADNIK_CENA, true, false, false, List.of())))
                .build();

        // when
        TowarCreateBuilder copy = original.toBuilder().build();

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
        assertEquals(original.kodyDod(), copy.kodyDod());
        assertEquals(original.cenyWSklepach(), copy.cenyWSklepach());
        assertEquals(original.skladniki(), copy.skladniki());
    }
}
