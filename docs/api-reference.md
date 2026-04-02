# NoviCloud REST API – Kompletna mapa endpointów

Źródło: `openapi.json` v2.10. Wygenerowane: 2026-03-20.

---

## Wszystkie ścieżki i metody HTTP

| Ścieżka | GET | POST | PUT | DELETE |
|---------|-----|------|-----|--------|
| `/{konto}/towary` | ✅ | ✅ | ✅ | – |
| `/{konto}/towary/{id}` | ✅ | – | ✅ | ✅ |
| `/{konto}/asorty` | ✅ | ✅ | ✅ | – |
| `/{konto}/asorty/{id}` | ✅ | – | ✅ | ✅ |
| `/{konto}/jmiary` | ✅ | ✅ | ✅ | – |
| `/{konto}/jmiary/{id}` | ✅ | – | ✅ | ✅ |
| `/{konto}/stawkivat` | ✅ | ✅ | ✅ | – |
| `/{konto}/stawkivat/{id}` | ✅ | – | ✅ | ✅ |
| `/{konto}/kontrahenci` | ✅ | ✅ | ✅ | – |
| `/{konto}/kontrahenci/{id}` | ✅ | – | ✅ | ✅ |
| `/{konto}/sklepy` | ✅ | ✅ | ✅ | – |
| `/{konto}/sklepy/{id}` | ✅ | – | ✅ | ✅ |
| `/{konto}/kasy` | ✅ | – | – | – |
| `/{konto}/kasy/{id}` | ✅ | – | – | – |
| `/{konto}/kasjerzy` | ✅ | – | – | – |
| `/{konto}/kasjerzy/{id}` | ✅ | – | – | – |
| `/{konto}/waluty` | ✅ | ✅ | ✅ | – |
| `/{konto}/waluty/{id}` | ✅ | – | ✅ | ✅ |
| `/{konto}/kraje` | ✅ | ✅ | ✅ | – |
| `/{konto}/kraje/{id}` | ✅ | – | ✅ | ✅ |
| `/{konto}/formyplatn` | ✅ | ✅ | ✅ | – |
| `/{konto}/formyplatn/{id}` | ✅ | – | ✅ | ✅ |
| `/{konto}/dokumenty` | ✅ | – | – | – |
| `/{konto}/dokumenty/{id}` | ✅ | – | – | – |
| `/{konto}/pozdok` | ✅ | – | – | – |
| `/{konto}/pozdok/{id}` | ✅ | – | – | – |
| `/{konto}/stanymag` | ✅ | – | ✅ | – |
| `/{konto}/stanymag/{id_towaru}` | ✅ | – | ✅ | – |
| `/{konto}/stanymag/{id_towaru}/{id_sklepu}` | ✅ | – | ✅ | – |
| `/{konto}/sprzedaz` | ✅ | – | – | – |
| `/{konto}/sprzedaz/{id}` | ✅ | – | – | – |
| `/{konto}/rapsprzed` | ✅ | – | – | – |
| `/{konto}/rappracy` | ✅ | – | – | – |
| `/{konto}/f-karty-loj` | ✅ | ✅ | ✅ | – |
| `/{konto}/f-karty-loj/{kod}` | ✅ | – | ✅ | – |

---

## Parametry GET – szczegóły

### /towary
- `start` integer, `on_page` integer, `fts` string
- `id` string – lista lub zakres: `1,2,3` lub `min10` / `max50`
- `nazwa` string – dokładna lub wildcard `~napój~`
- `kod` string
- `stawka_vat` integer – np. `2300`=23%, `-1`=zwolniona
- `akcyzowy` boolean
- `typ` integer – 0=zwykły, 2=paliwo, 4=winieta, 5=zestaw, 6=opakowanie, 7=usługa, 8=doładowanie
- `cena_det` string – `min5.5` lub `max10`
- `jm.id` integer
- `asort.id` integer
- `aktywny` boolean

### /asorty
- `start`, `on_page`, `fts`, `id`, `nazwa`
- `parent.id` integer

### /jmiary
- `start`, `on_page`, `fts`, `id`, `nazwa`
- `precyzja` integer – -2=ćwiartki, -1=połówki, 0=jedności, 1–3=dziesiętne

### /stawkivat
- `start`, `on_page`, `id`

### /kontrahenci
- `start`, `on_page`, `fts`, `id`, `nazwa`, `nip`
- `dostawca`, `producent`, `staly`, `odbiorca`, `osoba`, `aktywny` – boolean
- `kraj.id` integer

### /sklepy
- `start`, `on_page`, `fts`, `id`, `nazwa`
- `numer` integer
- `aktywny` boolean
- `kraj.id` integer

### /kasy
- `start`, `on_page`, `id`, `nazwa`
- `numer` integer, `ecr` string, `aktywny` boolean

### /kasjerzy
- `start`, `on_page`, `fts`, `id`, `nazwisko`, `kod_kasjera`, `aktywny` boolean

### /waluty
- `start`, `on_page`, `fts`, `id`, `nazwa`, `kod`
- `kurs` string – `min4.0` / `max10.0`
- `domyslna` boolean, `aktywny` boolean

### /kraje
- `start`, `on_page`, `fts`, `id`, `nazwa`, `kod`
- `waluta.id` integer

### /formyplatn
- `start`, `on_page`, `id`

### /dokumenty
- `start`, `on_page`, `id`
- `typ_dok` integer, `nr_dok` string
- `data_wystawienia`, `data_wplywu`, `data_wykonania` string
- `sklep.id`, `kontrahent.id`, `kasa.id`, `kasjer.id` integer

### /pozdok
- `start`, `on_page`, `id`
- `dokument.id`, `dokument.typ_dok` integer
- `dokument.data_wystawienia` string
- `dokument.kontrahent.id`, `dokument.sklep.id` integer
- `towar.id` integer

### /stanymag
- `start`, `on_page`
- `towar.id`, `sklep.id` integer
- `na_dzien` string – format `yyyy-MM-dd`

### /stanymag/{id_towaru} i /{id_towaru}/{id_sklepu}
- `na_dzien` string

### /sprzedaz
- `start`, `on_page`, `id`
- `data` string – `min2024-01-01` / `max2024-12-31T23:59:59`
- `nr_dok` string, `typ_dok` integer
- `sklep.id`, `kasa.id`, `kasjer.id`, `towar.id` integer
- `kontrahent.id` string – `null` lub `maxnull`

### /rapsprzed
- `data_pocz`, `data_konc` date (domyślnie: ostatnie 7 dni / dziś)
- `grupowanie` enum: `towar`, `asort`, `sklep`, `kasa`, `kasjer`, `kontr`, `kartarab`, `formaplatn`
- `skladniki` string – `1,2,4,5,6,7,8`

### /rappracy
- `data_pocz`, `data_konc` date
- `grupowanie` enum: `sklep`, `kasa`, `kasjer`

### /f-karty-loj
- `start`, `on_page`, `fts`
- `kod`, `posiadacz`, `nazwisko_imie`, `telefon`, `email` string
- `typ` integer
- `wazna_od`, `wazna_do` string

---

## Schematy – pola modeli

### Towar
`id` int64, `nazwa`, `kod`, `kody_dod[]`, `stawka_vat` int, `akcyzowy` bool, `typ` int,
`cena_ew`, `cena_det`, `cena_hurt`, `cena_noc`, `cena_dod` double,
`przy_sprzedazy` int (0=zamknięta, 1=otwarta, 2=rabat z karty),
`ceny_w_sklepach[]`, `pkwiu`, `masa_wl`, `aktywny` bool,
`jm` Link, `asort` Link,
`opis_1`…`opis_5`, `skladniki[]`, `ost_zmiana` datetime

### Asortyment
`id` int64, `nazwa`, `parent` Link

### JednostkaMiary
`id` int64, `nazwa`, `precyzja` int (-2 / -1 / 0 / 1 / 2 / 3)

### StawkaVat
`id` int (wartość*100: 2300=23%, -1=zwolniona), `opis`, `etykieta` enum A–G

### Kontrahent
`id` int64, `nazwa`, `nip`, `skrot`, `ulica`, `nr_domu`, `nr_lokalu`, `ulica_i_numer`,
`kod_poczt`, `poczta`, `miasto`, `gmina`, `powiat`, `województwo`, `kraj` Link,
`telefon`, `email`, `aktywny`, `dostawca`, `staly`, `producent`, `odbiorca`, `osoba` bool

### Sklep
`id` int64, `nazwa`, `nip`, `skrot`, `numer` int, `ulica`, `nr_domu`, `nr_lokalu`,
`ulica_i_numer`, `kod_poczt`, `poczta`, `miasto`, `gmina`, `powiat`, `województwo`,
`kraj` Link, `telefon`, `email`, `bank`, `konto`, `aktywny` bool

### Kasa
`id` int64, `nazwa`, `numer` int, `ecr`, `ostatnia_sync` datetime, `ostatnia_sprzed` datetime, `aktywny` bool

### Kasjer
`id` int64, `nazwisko`, `kod_kasjera`, `aktywny` bool

### Waluta
`id` int64, `nazwa`, `kod`, `kurs` number, `domyslna` bool, `aktywny` bool

### Kraj
`id` int64, `nazwa`, `kod`, `waluta` Link

### FormaPlatnosci
`id` int64, `nazwa`, `typ` int (0=gotówka,1=karta kredytowa,2=karta płatnicza,3=czek,4=bon,5=przelew,6=inna), `reszta` bool

### Dokument
`id` int64, `typ_dok` int, `data_wystawienia` datetime, `data_wplywu` date, `data_wykonania` date,
`nr_dok`, `karta_rabatowa`, `nip_na_par`, `nr_systemowy`, `nr_fiskalny`, `nr_rap_dobowego`, `komentarz`,
`sklep` Link, `sklep_odb` Link, `kontrahent` Link, `platnik` Link, `kasa` Link, `kasjer` Link,
`storno` bool, `netto`, `podatek`, `brutto`, `rabat` number,
`rozbicie_vat[]`, `forma_platn` Link, `termin_platn` datetime, `zaplacono` number,
`platnosci[]`, `dotyczy` Link, `korekty[]`, `faktury[]`, `dok_magazynowe[]`, `paragony[]`,
`dok_roliczane[]`, `pozycje` Link

### Pozdok (pozycja dokumentu)
`id` int64, `dokument` Link, `towar` Link, `nr_pozycji` int, `ilosc`, `ilosc_pocz`,
`stawka_vat` int, `c_przed_rab_netto`, `c_przed_rab_brutto`, `c_po_rab_netto`, `c_po_rab_brutto`,
`rabat_kwota`, `w_netto`, `w_podatek`, `w_brutto`,
`org_*` (oryginalne wartości), `rozl_*` (rozliczeniowe), `storno` bool

### StanMagazynowy
`towar` Link, `sklep` Link, `ilosc`, `w_c_zak_netto`, `w_c_zak_brutto`,
`w_c_sprzed_netto`, `w_c_sprzed_brutto` number

### Sprzedaz
`id` int64, `data` datetime, `nr_dok`, `typ_dok` int, `nr_systemowy`, `nr_fiskalny`, `nr_rap_dob`,
`ilosc`, `cena`, `cena_przed_rab`, `stawka_vat` int, `brutto`, `podatek`, `rabat`,
`towar` Link, `sklep` Link, `kasa` Link, `kasjer` Link, `kontrahent` Link, `platnosci[]`

### RaportSprzedazy
`towar`, `asort`, `sklep`, `kasa`, `kasjer`, `kontrahent`, `forma_platn` Link, `karta_rabatowa`,
`ilosc`, `sprz_netto`, `sprz_brutto`, `sprz_zak_netto`, `sprz_zak_brutto`,
`marza_netto`, `marza_brutto`, `marza_proc_netto`, `marza_proc_brutto`,
`narzut_proc_netto`, `narzut_proc_brutto`, `rabat`, `rabat_proc`

### RaportPracyKasjerow
`sklep`, `kasa`, `kasjer` Link, `czas_pracy` (ms), `utarg`, `gotowka`, `karta`, `czek`, `bon`, `przelew`, `inna`,
`paragony_ilosc`, `paragony_wartosc`, `paragony_pozycje`,
`faktury_ilosc`, `faktury_wartosc`, `faktury_pozycje`,
`storno_pozycje`, `storno_wartosc`,
`paragony_anulowane_ilosc`, `paragony_anulowane_wartosc`

### KartaLojalnosciowa
`kod`, `typ` int, `wazna_od`, `wazna_do` datetime, `posiadacz`, `opis_1`, `opis_2`,
`uniewazniono` datetime, `nazwisko_imie`, `skrot`, `telefon`, `email`,
`miejscowosc`, `ulica`, `nr_domu`, `nr_lokalu`, `kod_poczt`, `poczta`,
`nip`, `data_urodz` date, `plec` enum K/M

---

## Link (typ pomocniczy)
`id` int64, `link` uri
