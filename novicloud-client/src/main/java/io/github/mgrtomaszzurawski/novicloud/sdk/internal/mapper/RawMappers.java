/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.internal.mapper;

import io.github.mgrtomaszzurawski.novicloud.client.model.AsortyRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.DokumentRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.DokumentRozbicieVatRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.DokumentRozliczaneRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.FormaPlatnRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.JmiaryRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.KartaLojalnosciowaRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.KasaRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.KasjerRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.KontrahentRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.KrajRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.LinkRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.PlatnoscRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.PozycjaDokumentuRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.RaportPracyRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.RaportSprzedazyRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.SklepRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.SprzedazRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.StanMagRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.StawkaVatRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.TowarCenaWSklepieRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.TowarKodDodatkowyRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.TowarRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.TowarSkladnikRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.TowarSkladnikTowarRaw;
import io.github.mgrtomaszzurawski.novicloud.client.model.WalutaRaw;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Asorty;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Dokument;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.DokumentRozbicieVat;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.DokumentRozliczany;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.FormaPlatn;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Jmiary;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.KartaLojalnosciowa;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Kasa;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Kasjer;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Kontrahent;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Kraj;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Platnosc;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.PozycjaDokumentu;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.RaportPracy;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.RaportSprzedazy;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Sklep;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Sprzedaz;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.StanMag;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.StawkaVat;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Towar;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarCenaWSklepie;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarKodDodatkowy;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarSkladnik;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.TowarSkladnikTowar;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Waluta;

import java.util.List;

/**
 * Internal helper that maps the SDK's transport-layer types to the public
 * SDK records returned by {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient}.
 *
 * <p>This class lives in a non-exported package and is not part of the public
 * API. SDK consumers do not call it directly. (See ADR-057, ADR-058 for the
 * 2.0.0 history of internalising this mapping.)
 */
public final class RawMappers {

    private RawMappers() { }

    public static Asorty toAsorty(AsortyRaw raw) {
        return new Asorty(
                raw.getId(),
                raw.getNazwa(),
                LinkUtils.extractId(raw.getParent())
        );
    }

    public static Jmiary toJmiary(JmiaryRaw raw) {
        return new Jmiary(
                raw.getId(),
                raw.getNazwa(),
                raw.getPrecyzja() != null ? Jmiary.Precyzja.fromCode(raw.getPrecyzja().getValue()) : null
        );
    }

    public static StawkaVat toStawkaVat(StawkaVatRaw raw) {
        return new StawkaVat(
                raw.getId(),
                raw.getOpis(),
                raw.getEtykieta() != null ? StawkaVat.Etykieta.fromCode(raw.getEtykieta().getValue()) : null
        );
    }

    public static Kraj toKraj(KrajRaw raw) {
        return new Kraj(
                raw.getId(),
                raw.getNazwa(),
                raw.getKod(),
                LinkUtils.extractId(raw.getWaluta())
        );
    }

    public static Waluta toWaluta(WalutaRaw raw) {
        return new Waluta(
                raw.getId(),
                raw.getNazwa(),
                raw.getKod(),
                raw.getKurs(),
                raw.getDomyslna(),
                raw.getAktywny()
        );
    }

    public static FormaPlatn toFormaPlatn(FormaPlatnRaw raw) {
        return new FormaPlatn(
                raw.getId(),
                raw.getNazwa(),
                raw.getReszta(),
                raw.getAktywny(),
                raw.getTyp() != null ? FormaPlatn.Typ.fromCode(raw.getTyp().getValue()) : null
        );
    }

    public static Kasa toKasa(KasaRaw raw) {
        return new Kasa(
                raw.getId(),
                raw.getNazwa(),
                raw.getNumer(),
                raw.getEcr(),
                raw.getOstatniaSync(),
                raw.getOstatniaSprzed(),
                raw.getAktywny()
        );
    }

    public static Kasjer toKasjer(KasjerRaw raw) {
        return new Kasjer(
                raw.getId(),
                raw.getNazwisko(),
                raw.getKodKasjera(),
                raw.getAktywny()
        );
    }

    public static Kontrahent toKontrahent(KontrahentRaw raw) {
        return new Kontrahent(
                raw.getId(),
                raw.getNazwa(),
                raw.getNip(),
                raw.getSkrot(),
                raw.getUlica(),
                raw.getNrDomu(),
                raw.getNrLokalu(),
                raw.getUlicaINumer(),
                raw.getKodPoczt(),
                raw.getPoczta(),
                raw.getMiasto(),
                raw.getGmina(),
                raw.getPowiat(),
                raw.getWojewodztwo(),
                raw.getTelefon(),
                raw.getEmail(),
                raw.getAktywny(),
                raw.getDostawca(),
                raw.getStaly(),
                raw.getProducent(),
                raw.getOdbiorca(),
                raw.getOsoba(),
                LinkUtils.extractId(raw.getKraj())
        );
    }

    public static Sklep toSklep(SklepRaw raw) {
        return new Sklep(
                raw.getId(),
                raw.getNazwa(),
                raw.getNip(),
                raw.getSkrot(),
                raw.getNumer(),
                raw.getUlica(),
                raw.getNrDomu(),
                raw.getNrLokalu(),
                raw.getUlicaINumer(),
                raw.getKodPoczt(),
                raw.getPoczta(),
                raw.getMiasto(),
                raw.getGmina(),
                raw.getPowiat(),
                raw.getWojewodztwo(),
                raw.getTelefon(),
                raw.getEmail(),
                raw.getBank(),
                raw.getKonto(),
                raw.getAktywny(),
                LinkUtils.extractId(raw.getKraj())
        );
    }

    public static KartaLojalnosciowa toKartaLojalnosciowa(KartaLojalnosciowaRaw raw) {
        return new KartaLojalnosciowa(
                raw.getKod(),
                raw.getTyp(),
                raw.getWaznaOd(),
                raw.getWaznaDo(),
                raw.getPosiadacz(),
                raw.getOpis1(),
                raw.getOpis2(),
                raw.getUniewazniono(),
                raw.getNazwiskoImie(),
                raw.getSkrot(),
                raw.getTelefon(),
                raw.getEmail(),
                raw.getMiejscowosc(),
                raw.getUlica(),
                raw.getNrDomu(),
                raw.getNrLokalu(),
                raw.getKodPoczt(),
                raw.getPoczta(),
                raw.getNip(),
                raw.getDataUrodz(),
                raw.getPlec() != null ? KartaLojalnosciowa.Plec.fromCode(raw.getPlec().getValue()) : null
        );
    }

    public static StanMag toStanMag(StanMagRaw raw) {
        return new StanMag(
                raw.getIlosc(),
                raw.getwCZakNetto(),
                raw.getwCZakBrutto(),
                raw.getwCSprzedNetto(),
                raw.getwCSprzedBrutto(),
                LinkUtils.extractId(raw.getTowar()),
                LinkUtils.extractId(raw.getSklep())
        );
    }

    public static PozycjaDokumentu toPozycjaDokumentu(PozycjaDokumentuRaw raw) {
        return new PozycjaDokumentu(
                raw.getId(),
                raw.getNrPozycji(),
                raw.getIlosc(),
                raw.getIloscPocz(),
                raw.getStawkaVat(),
                raw.getcPrzedRabNetto(),
                raw.getcPrzedRabBrutto(),
                raw.getcPoRabNetto(),
                raw.getcPoRabBrutto(),
                raw.getRabatKwota(),
                raw.getwNetto(),
                raw.getwPodatek(),
                raw.getwBrutto(),
                raw.getOrgIlosc(),
                raw.getOrgCPrzedRabNetto(),
                raw.getOrgCPrzedRabBrutto(),
                raw.getOrgCPoRabNetto(),
                raw.getOrgCPoRabBrutto(),
                raw.getOrgRabatKwota(),
                raw.getOrgWNetto(),
                raw.getOrgWPodatek(),
                raw.getOrgWBrutto(),
                raw.getRozlNetto(),
                raw.getRozlPodatek(),
                raw.getRozlBrutto(),
                raw.getStorno(),
                LinkUtils.extractId(raw.getDokument()),
                LinkUtils.extractId(raw.getTowar())
        );
    }

    public static RaportPracy toRaportPracy(RaportPracyRaw raw) {
        return new RaportPracy(
                raw.getCzasPracy(),
                raw.getUtarg(),
                raw.getGotowka(),
                raw.getKarta(),
                raw.getCzek(),
                raw.getBon(),
                raw.getPrzelew(),
                raw.getInna(),
                raw.getParagonyIlosc(),
                raw.getParagonyWartosc(),
                raw.getParagonyPozycje(),
                raw.getFakturyIlosc(),
                raw.getFakturyWartosc(),
                raw.getFakturyPozycje(),
                raw.getStornoPozycje(),
                raw.getStornoWartosc(),
                raw.getParagonyAnulowaneIlosc(),
                raw.getParagonyAnulowaneWartosc(),
                LinkUtils.extractId(raw.getSklep()),
                LinkUtils.extractId(raw.getKasa()),
                LinkUtils.extractId(raw.getKasjer())
        );
    }

    public static RaportSprzedazy toRaportSprzedazy(RaportSprzedazyRaw raw) {
        return new RaportSprzedazy(
                raw.getKartaRabatowa(),
                raw.getIlosc(),
                raw.getSprzNetto(),
                raw.getSprzBrutto(),
                raw.getSprzZakNetto(),
                raw.getSprzZakBrutto(),
                raw.getMarzaNetto(),
                raw.getMarzaBrutto(),
                raw.getMarzaProcNetto(),
                raw.getMarzaProcBrutto(),
                raw.getNarzutProcNetto(),
                raw.getNarzutProcBrutto(),
                raw.getRabat(),
                raw.getRabatProc(),
                LinkUtils.extractId(raw.getTowar()),
                LinkUtils.extractId(raw.getAsort()),
                LinkUtils.extractId(raw.getSklep()),
                LinkUtils.extractId(raw.getKasa()),
                LinkUtils.extractId(raw.getKasjer()),
                LinkUtils.extractId(raw.getKontrahent()),
                LinkUtils.extractId(raw.getFormaPlatn())
        );
    }

    public static TowarKodDodatkowy toTowarKodDodatkowy(TowarKodDodatkowyRaw raw) {
        return new TowarKodDodatkowy(
                raw.getKod(),
                raw.getIleWOpak(),
                raw.getPoziomCen() != null ? raw.getPoziomCen().getValue() : null
        );
    }

    public static TowarCenaWSklepie toTowarCenaWSklepie(TowarCenaWSklepieRaw raw) {
        return new TowarCenaWSklepie(
                LinkUtils.extractId(raw.getSklep()),
                raw.getCenaEw(),
                raw.getCenaDet(),
                raw.getCenaHurt(),
                raw.getCenaNoc(),
                raw.getCenaDod(),
                raw.getPrzySprzedazy()
        );
    }

    public static TowarSkladnikTowar toTowarSkladnikTowar(TowarSkladnikTowarRaw raw) {
        return new TowarSkladnikTowar(
                LinkUtils.extractId(raw.getTowar()),
                raw.getIlosc(),
                raw.getCenaZKartyTow(),
                raw.getCena(),
                raw.getDomyslny()
        );
    }

    public static TowarSkladnik toTowarSkladnik(TowarSkladnikRaw raw) {
        List<TowarSkladnikTowar> mapped = raw.getTowary() == null
                ? List.of()
                : raw.getTowary().stream().map(RawMappers::toTowarSkladnikTowar).toList();
        return new TowarSkladnik(
                raw.getNazwa(),
                raw.getCena(),
                raw.getObowiazkowy(),
                raw.getWyborWieluTow(),
                raw.getRozneCeny(),
                mapped
        );
    }

    public static Towar toTowar(TowarRaw raw) {
        List<TowarKodDodatkowy> kodyDod = raw.getKodyDod() == null
                ? List.of()
                : raw.getKodyDod().stream().map(RawMappers::toTowarKodDodatkowy).toList();
        List<TowarCenaWSklepie> cenyWSklepach = raw.getCenyWSklepach() == null
                ? List.of()
                : raw.getCenyWSklepach().stream().map(RawMappers::toTowarCenaWSklepie).toList();
        List<TowarSkladnik> skladniki = raw.getSkladniki() == null
                ? List.of()
                : raw.getSkladniki().stream().map(RawMappers::toTowarSkladnik).toList();
        return new Towar(
                raw.getId(),
                raw.getNazwa(),
                raw.getKod(),
                raw.getCku(),
                raw.getStawkaVat(),
                raw.getAkcyzowy(),
                raw.getCenaEw(),
                raw.getCenaDet(),
                raw.getCenaHurt(),
                raw.getCenaNoc(),
                raw.getCenaDod(),
                raw.getGtu(),
                raw.getPkwiu(),
                raw.getMasaWl(),
                raw.getAktywny(),
                raw.getOpis1(),
                raw.getOpis2(),
                raw.getOpis3(),
                raw.getOpis4(),
                raw.getOpis5(),
                raw.getOstZmiana(),
                LinkUtils.extractId(raw.getJm()),
                LinkUtils.extractId(raw.getAsort()),
                raw.getTyp() != null ? Towar.Typ.fromCode(raw.getTyp().getValue()) : null,
                raw.getPrzySprzedazy() != null ? Towar.PrzySprzedazy.fromCode(raw.getPrzySprzedazy().getValue()) : null,
                kodyDod,
                cenyWSklepach,
                skladniki
        );
    }

    public static DokumentRozbicieVat toDokumentRozbicieVat(DokumentRozbicieVatRaw raw) {
        return new DokumentRozbicieVat(
                raw.getStawka(),
                raw.getNetto(),
                raw.getPodatek(),
                raw.getBrutto()
        );
    }

    public static DokumentRozliczany toDokumentRozliczany(DokumentRozliczaneRaw raw) {
        return new DokumentRozliczany(
                LinkUtils.extractId(raw.getDokument()),
                raw.getZaplacono()
        );
    }

    public static Platnosc toPlatnosc(PlatnoscRaw raw) {
        return new Platnosc(
                LinkUtils.extractId(raw.getFormaPlatnosci()),
                LinkUtils.extractId(raw.getWaluta()),
                raw.getTypFormyPlatnosci(),
                raw.getKodWaluty(),
                raw.getKurs(),
                raw.getWplataWaluta(),
                raw.getWplataWalutaDomyslna(),
                raw.getResztaWaluta(),
                raw.getResztaWalutaDomyslna(),
                LinkUtils.extractId(raw.getDokPlatnosci()),
                raw.getKwota()
        );
    }

    @SuppressWarnings("deprecation")
    public static Dokument toDokument(DokumentRaw raw) {
        LinkRaw pozycje = raw.getPozycje();
        String pozId = pozycje != null ? pozycje.getId() : null;
        String pozUrl = pozycje != null && pozycje.getLink() != null ? pozycje.getLink().toString() : null;
        List<DokumentRozbicieVat> rozbicieVat = raw.getRozbicieVat() == null
                ? List.of()
                : raw.getRozbicieVat().stream().map(RawMappers::toDokumentRozbicieVat).toList();
        List<Platnosc> platnosci = raw.getPlatnosci() == null
                ? List.of()
                : raw.getPlatnosci().stream().map(RawMappers::toPlatnosc).toList();
        List<DokumentRozliczany> dokRozliczane = raw.getDokRoliczane() == null
                ? List.of()
                : raw.getDokRoliczane().stream().map(RawMappers::toDokumentRozliczany).toList();
        return new Dokument(
                raw.getId(),
                raw.getTypDok(),
                raw.getDataWystawienia(),
                raw.getDataWplywu(),
                raw.getDataWykonania(),
                raw.getNrDok(),
                raw.getKartaRabatowa(),
                raw.getNipNaPar(),
                raw.getNrSystemowy(),
                raw.getNrFiskalny(),
                raw.getNrRapDobowego(),
                raw.getKomentarz(),
                raw.getStorno(),
                raw.getNetto(),
                raw.getPodatek(),
                raw.getBrutto(),
                raw.getRabat(),
                raw.getTerminPlatn(),
                raw.getZaplacono(),
                LinkUtils.extractId(raw.getSklep()),
                LinkUtils.extractId(raw.getSklepOdb()),
                LinkUtils.extractId(raw.getKontrahent()),
                LinkUtils.extractId(raw.getPlatnik()),
                LinkUtils.extractId(raw.getKasa()),
                LinkUtils.extractId(raw.getKasjer()),
                LinkUtils.extractId(raw.getFormaPlatn()),
                LinkUtils.extractId(raw.getDotyczy()),
                pozId,
                pozId,
                pozUrl,
                rozbicieVat,
                platnosci,
                mapLinkIds(raw.getKorekty()),
                mapLinkIds(raw.getFaktury()),
                mapLinkIds(raw.getDokMagazynowe()),
                mapLinkIds(raw.getParagony()),
                dokRozliczane
        );
    }

    public static Sprzedaz toSprzedaz(SprzedazRaw raw) {
        List<Platnosc> platnosci = raw.getPlatnosci() == null
                ? List.of()
                : raw.getPlatnosci().stream().map(RawMappers::toPlatnosc).toList();
        return new Sprzedaz(
                raw.getId(),
                raw.getData(),
                raw.getNrDok(),
                raw.getTypDok(),
                raw.getNrSystemowy(),
                raw.getNrFiskalny(),
                raw.getNrRapDob(),
                raw.getIlosc(),
                raw.getCena(),
                raw.getCenaPrzedRab(),
                raw.getStawkaVat(),
                raw.getBrutto(),
                raw.getPodatek(),
                raw.getRabat(),
                LinkUtils.extractId(raw.getTowar()),
                LinkUtils.extractId(raw.getSklep()),
                LinkUtils.extractId(raw.getKasa()),
                LinkUtils.extractId(raw.getKasjer()),
                LinkUtils.extractId(raw.getKontrahent()),
                platnosci
        );
    }

    private static List<String> mapLinkIds(List<LinkRaw> links) {
        return links == null ? List.of() : links.stream().map(LinkUtils::extractId).toList();
    }
}
