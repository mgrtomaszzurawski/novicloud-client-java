/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.model;

import io.github.mgrtomaszzurawski.novicloud.client.model.DokumentRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.DokumentRozbicieVatRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.DokumentRozliczaneRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.LinkRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.PlatnoscRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.SprzedazRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.TowarCenaWSklepieRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.TowarKodDodatkowyRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.TowarRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.TowarSkladnikRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.TowarSkladnikTowarRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.internal.mapper.RawMappers;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@code from(XRaw)} factories on nested records added in 1.1.0
 * (F-04 / F-12 / F-13 / F-14, ADR-054).
 *
 * <p>Verifies field-by-field mapping plus the "null collection -> empty list" fallback.
 */
class NestedRecordsFromTest {

    private static final String SKLEP_ID = "5";
    private static final String FORMA_ID = "3";
    private static final String WALUTA_ID = "1";
    private static final String DOK_PLATNOSCI_ID = "9";
    private static final String DOKUMENT_ID = "100";
    private static final String TOWAR_ID = "200";
    private static final String KOD_DOD = "5901234567899";
    private static final String NAZWA = "Main";
    private static final String KOD_WALUTY = "PLN";
    private static final Double ILE_W_OPAK = 6.0;
    private static final int POZIOM_CEN = 2;
    private static final Double CENA_EW = 10.0;
    private static final Double CENA_DET = 12.5;
    private static final Double CENA_HURT = 11.0;
    private static final Double CENA_NOC = 9.5;
    private static final Double CENA_DOD = 13.0;
    private static final int PRZY_SPRZEDAZY = 1;
    private static final int VAT_RATE = 2300;
    private static final Double NETTO = 100.0;
    private static final Double PODATEK = 23.0;
    private static final Double BRUTTO = 123.0;
    private static final Double KURS = 1.0;
    private static final Double KWOTA = 50.0;
    private static final Double WPLATA = 50.0;
    private static final Double RESZTA = 0.0;
    private static final Double ZAPLACONO = 25.0;
    private static final int TYP_FORMY = 0;
    private static final Double ILOSC = 2.0;
    private static final Double CENA = 19.99;

    private static LinkRaw link(String id) {
        LinkRaw l = new LinkRaw();
        l.setId(id);
        return l;
    }

    @Test
    void towarKodDodatkowy_from_mapsAllFields() {
        // given
        TowarKodDodatkowyRaw raw = new TowarKodDodatkowyRaw();
        raw.setKod(KOD_DOD);
        raw.setIleWOpak(ILE_W_OPAK);
        raw.setPoziomCen(TowarKodDodatkowyRaw.PoziomCenEnum.NUMBER_2);

        // when
        TowarKodDodatkowy result = RawMappers.toTowarKodDodatkowy(raw);

        // then
        assertEquals(KOD_DOD, result.kod());
        assertEquals(ILE_W_OPAK, result.ileWOpak());
        assertEquals(POZIOM_CEN, result.poziomCen());
    }

    @Test
    void towarKodDodatkowy_from_whenPoziomCenNull_returnsNull() {
        // given
        TowarKodDodatkowyRaw raw = new TowarKodDodatkowyRaw();
        raw.setKod(KOD_DOD);

        // when
        TowarKodDodatkowy result = RawMappers.toTowarKodDodatkowy(raw);

        // then
        assertNull(result.poziomCen());
    }

    @Test
    void towarCenaWSklepie_from_mapsAllFields() {
        // given
        TowarCenaWSklepieRaw raw = new TowarCenaWSklepieRaw();
        raw.setSklep(link(SKLEP_ID));
        raw.setCenaEw(CENA_EW);
        raw.setCenaDet(CENA_DET);
        raw.setCenaHurt(CENA_HURT);
        raw.setCenaNoc(CENA_NOC);
        raw.setCenaDod(CENA_DOD);
        raw.setPrzySprzedazy(PRZY_SPRZEDAZY);

        // when
        TowarCenaWSklepie result = RawMappers.toTowarCenaWSklepie(raw);

        // then
        assertEquals(SKLEP_ID, result.sklepId());
        assertEquals(CENA_EW, result.cenaEw());
        assertEquals(CENA_DET, result.cenaDet());
        assertEquals(CENA_HURT, result.cenaHurt());
        assertEquals(CENA_NOC, result.cenaNoc());
        assertEquals(CENA_DOD, result.cenaDod());
        assertEquals(PRZY_SPRZEDAZY, result.przySprzedazy());
    }

    @Test
    void towarSkladnikTowar_from_mapsAllFields() {
        // given
        TowarSkladnikTowarRaw raw = new TowarSkladnikTowarRaw();
        raw.setTowar(link(TOWAR_ID));
        raw.setIlosc(ILOSC);
        raw.setCenaZKartyTow(true);
        raw.setCena(CENA);
        raw.setDomyslny(true);

        // when
        TowarSkladnikTowar result = RawMappers.toTowarSkladnikTowar(raw);

        // then
        assertEquals(TOWAR_ID, result.towarId());
        assertEquals(ILOSC, result.ilosc());
        assertEquals(Boolean.TRUE, result.cenaZKartyTow());
        assertEquals(CENA, result.cena());
        assertEquals(Boolean.TRUE, result.domyslny());
    }

    @Test
    void towarSkladnik_from_mapsAllFields() {
        // given
        TowarSkladnikTowarRaw inner = new TowarSkladnikTowarRaw();
        inner.setTowar(link(TOWAR_ID));
        TowarSkladnikRaw raw = new TowarSkladnikRaw();
        raw.setNazwa(NAZWA);
        raw.setCena(CENA);
        raw.setObowiazkowy(true);
        raw.setWyborWieluTow(false);
        raw.setRozneCeny(false);
        raw.addTowaryItem(inner);

        // when
        TowarSkladnik result = RawMappers.toTowarSkladnik(raw);

        // then
        assertEquals(NAZWA, result.nazwa());
        assertEquals(CENA, result.cena());
        assertEquals(Boolean.TRUE, result.obowiazkowy());
        assertEquals(Boolean.FALSE, result.wyborWieluTow());
        assertEquals(Boolean.FALSE, result.rozneCeny());
        assertEquals(1, result.towary().size());
        assertEquals(TOWAR_ID, result.towary().get(0).towarId());
    }

    @Test
    void towarSkladnik_from_whenTowaryNull_returnsEmptyList() {
        // given
        TowarSkladnikRaw raw = new TowarSkladnikRaw();
        raw.setNazwa(NAZWA);

        // when
        TowarSkladnik result = RawMappers.toTowarSkladnik(raw);

        // then
        assertNotNull(result.towary());
        assertTrue(result.towary().isEmpty());
    }

    @Test
    void platnosc_from_mapsAllFields() {
        // given
        PlatnoscRaw raw = new PlatnoscRaw();
        raw.setFormaPlatnosci(link(FORMA_ID));
        raw.setWaluta(link(WALUTA_ID));
        raw.setTypFormyPlatnosci(TYP_FORMY);
        raw.setKodWaluty(KOD_WALUTY);
        raw.setKurs(KURS);
        raw.setWplataWaluta(WPLATA);
        raw.setWplataWalutaDomyslna(WPLATA);
        raw.setResztaWaluta(RESZTA);
        raw.setResztaWalutaDomyslna(RESZTA);
        raw.setDokPlatnosci(link(DOK_PLATNOSCI_ID));
        raw.setKwota(KWOTA);

        // when
        Platnosc result = RawMappers.toPlatnosc(raw);

        // then
        assertEquals(FORMA_ID, result.formaPlatnosciId());
        assertEquals(WALUTA_ID, result.walutaId());
        assertEquals(TYP_FORMY, result.typFormyPlatnosci());
        assertEquals(KOD_WALUTY, result.kodWaluty());
        assertEquals(KURS, result.kurs());
        assertEquals(WPLATA, result.wplataWaluta());
        assertEquals(WPLATA, result.wplataWalutaDomyslna());
        assertEquals(RESZTA, result.resztaWaluta());
        assertEquals(RESZTA, result.resztaWalutaDomyslna());
        assertEquals(DOK_PLATNOSCI_ID, result.dokPlatnosciId());
        assertEquals(KWOTA, result.kwota());
    }

    @Test
    void dokumentRozbicieVat_from_mapsAllFields() {
        // given
        DokumentRozbicieVatRaw raw = new DokumentRozbicieVatRaw();
        raw.setStawka(VAT_RATE);
        raw.setNetto(NETTO);
        raw.setPodatek(PODATEK);
        raw.setBrutto(BRUTTO);

        // when
        DokumentRozbicieVat result = RawMappers.toDokumentRozbicieVat(raw);

        // then
        assertEquals(VAT_RATE, result.stawka());
        assertEquals(NETTO, result.netto());
        assertEquals(PODATEK, result.podatek());
        assertEquals(BRUTTO, result.brutto());
    }

    @Test
    void dokumentRozliczany_from_mapsAllFields() {
        // given
        DokumentRozliczaneRaw raw = new DokumentRozliczaneRaw();
        raw.setDokument(link(DOKUMENT_ID));
        raw.setZaplacono(ZAPLACONO);

        // when
        DokumentRozliczany result = RawMappers.toDokumentRozliczany(raw);

        // then
        assertEquals(DOKUMENT_ID, result.dokumentId());
        assertEquals(ZAPLACONO, result.zaplacono());
    }

    @Test
    void towar_from_whenAllNestedListsNull_returnsEmptyLists() {
        // given - server returns Towar without kody_dod, ceny_w_sklepach, skladniki keys
        TowarRaw raw = new TowarRaw();
        raw.setId(1L);
        raw.setKod("X");
        raw.setNazwa("Y");

        // when
        Towar result = RawMappers.toTowar(raw);

        // then
        assertNotNull(result.kodyDod());
        assertTrue(result.kodyDod().isEmpty());
        assertNotNull(result.cenyWSklepach());
        assertTrue(result.cenyWSklepach().isEmpty());
        assertNotNull(result.skladniki());
        assertTrue(result.skladniki().isEmpty());
    }

    @Test
    void dokument_from_whenAllNestedListsNull_returnsEmptyLists() {
        // given
        DokumentRaw raw = new DokumentRaw();
        raw.setId(1L);

        // when
        Dokument result = RawMappers.toDokument(raw);

        // then
        assertNotNull(result.rozbicieVat());
        assertTrue(result.rozbicieVat().isEmpty());
        assertNotNull(result.platnosci());
        assertTrue(result.platnosci().isEmpty());
        assertNotNull(result.korektyIds());
        assertTrue(result.korektyIds().isEmpty());
        assertNotNull(result.fakturyIds());
        assertTrue(result.fakturyIds().isEmpty());
        assertNotNull(result.dokMagazynoweIds());
        assertTrue(result.dokMagazynoweIds().isEmpty());
        assertNotNull(result.paragonyIds());
        assertTrue(result.paragonyIds().isEmpty());
        assertNotNull(result.dokRozliczane());
        assertTrue(result.dokRozliczane().isEmpty());
    }

    @Test
    void sprzedaz_from_whenPlatnosciNull_returnsEmptyList() {
        // given
        SprzedazRaw raw = new SprzedazRaw();
        raw.setId(1L);

        // when
        Sprzedaz result = RawMappers.toSprzedaz(raw);

        // then
        assertNotNull(result.platnosci());
        assertTrue(result.platnosci().isEmpty());
    }

    @Test
    @SuppressWarnings("removal")
    void dokument_from_whenPozycjeLinkPresent_exposesIdAndUrlSeparately() {
        // given - F-13 niuans: pozycjeId is the resource ID, pozycjeUrl is the absolute URL,
        // pozycjeLink (deprecated) keeps 1.0.0 behaviour and returns the ID.
        LinkRaw pozycje = new LinkRaw();
        pozycje.setId("42");
        pozycje.setLink(URI.create("http://localhost/demo/pozdok?dokument.id=42"));
        DokumentRaw raw = new DokumentRaw();
        raw.setId(42L);
        raw.setPozycje(pozycje);

        // when
        Dokument result = RawMappers.toDokument(raw);

        // then
        assertEquals("42", result.pozycjeId());
        assertEquals("http://localhost/demo/pozdok?dokument.id=42", result.pozycjeUrl());
        assertEquals("42", result.pozycjeLink()); // deprecated, kept for back-compat
    }

    @Test
    void dokument_from_whenPozycjeNull_pozycjeIdAndUrlAreNull() {
        // given
        DokumentRaw raw = new DokumentRaw();
        raw.setId(7L);

        // when
        Dokument result = RawMappers.toDokument(raw);

        // then
        assertNull(result.pozycjeId());
        assertNull(result.pozycjeUrl());
    }
}
