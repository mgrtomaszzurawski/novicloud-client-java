# ADR-031: Remove broken GET query parameters from QueryBuilders

## Status

Accepted

## Context

Live integration tests run on 2026-03-29 against the NoviCloud API revealed that a significant
number of query filter parameters in the SDK `*QueryBuilder` classes cause HTTP 400 or HTTP 500
errors when sent to the server.

### Testing methodology

Two rounds of testing were performed:

**Round 1 - `runListPageAllFilters`**: sends a single `listPage` call per endpoint with every
declared QueryBuilder field populated with a boundary value (`"~ZZZZ~"` for strings,
`"32766"` for IDs, `"2099-12-31"` for dates, `false` for booleans). Result: 12 of 14 tested
endpoints failed. 2 passed (jmiary, stawkivat).
4 known-good endpoints skipped (asorty, kraje, stanymag, kasjerzy).

**Round 2 - `runProbeFilters`**: for each failing endpoint, tests each suspicious field
individually with multiple plausible values (integer strings, enum candidates, booleans,
decimals). This isolated which fields are truly broken vs which merely need correct typing.

**Round 3 - documentation cross-reference**: all results verified against the official
NoviCloud REST API documentation to determine
whether failed fields are documented as filterable.

### Initial test artifact: stawkivat.id

The initial round used `FILTER_ALL_ID="999999"` which exceeded the `short` range (max 32,767)
for `stawkivat.id` (see ADR-022). After correcting to `"32766"`, stawkivat passes cleanly.
Similarly, `jmiary.precyzja` failed with string `"~ZZZZ~"` but passes with integer values
"0", "2", "4", "8" (doc: integer, values -2 to 3). These are test artifacts, not broken fields.

### Verified results by endpoint

**6 endpoints - all filters pass:**
asorty, kraje, stanymag, kasjerzy, jmiary (after integer fix), stawkivat (after short-range fix)

---

### Category A: par_niewlasciwe - documented as filterable, server rejects

The server returned `400` with `par_niewlasciwe` (parameter not recognized). Documentation
confirms these fields SHOULD be filterable. The server does not implement the documented filter.

| Endpoint | SDK field | Doc param | Doc filtering | Status |
|----------|-----------|-----------|--------------|--------|
| dokumenty | `sklepOdbId` | `sklep_odb.id` | Lista lub zakres | server bug |
| sprzedaz | `nrRapDob` | `nr_rap_dob` | Tekst, Fts | server bug |
| kontrahenci | `osoba` | `osoba` | Boolean | server bug |
| sklepy | `nrDomu` | `nr_domu` | Tekst | server bug |
| sklepy | `nrLokalu` | `nr_lokalu` | Tekst | server bug |
| sklepy | `poczta` | `poczta` | Tekst, Fts | server bug |
| sklepy | `krajId` | `kraj.id` | Lista lub zakres | server bug |
| kasy | `ecr` | `ecr` | Tekst, Fts | server bug |
| kartyloj | `waznaOd` | `wazna_od` | Lista lub zakres | server bug |
| kartyloj | `waznaDo` | `wazna_do` | Lista lub zakres | server bug |
| kartyloj | `nazwiskoImie` | `nazwisko_imie` | Tekst, Fts | server bug |

Total: **11 fields** across 6 endpoints. All documented as filterable, all rejected by server.

### Category A2: par_niewlasciwe - NOT documented as filterable (SDK spec error)

These fields have `"Brak"` (none) in the documentation's Filtrowanie column. The SDK exposed
them as query parameters when the API specification says they are not filterable.

| Endpoint | SDK field | Doc param | Doc filtering |
|----------|-----------|-----------|--------------|
| formyplatn | `typ` | `typ` | Brak, POST |
| formyplatn | `nazwa` | `nazwa` | Brak, POST |

Total: **2 fields**. These should never have been in QueryBuilder.

### Category B: par_bledna_wart - field works with correct type

The server returned `400` with `par_bledna_wart` using boundary string values, but individual
probes with correctly typed values succeeded.

| Endpoint | Field | Probed values | Working values | Doc type |
|----------|-------|---------------|----------------|----------|
| jmiary | `precyzja` | "0","2","4","8" | all OK | integer (-2 to 3) |
| dokumenty | `typDok` | "WZ","PZ","FA","PA","0","1" | "0","1" OK | integer (21,22,33,...) |
| sprzedaz | `typDok` | "WZ","FA","PA","0" | "0" OK; strings->500 | integer (21,33,34,36,112,113) |
| sprzedaz | `brutto` | "1" | OK (2381) | number |
| sprzedaz | `ilosc` | "1" | OK (292233) | number |
| sprzedaz | `cena` | "1","1.00" | both OK (3114) | number |
| sprzedaz | `cenaPrzedRab` | "1" | HTTP 500 (Hibernate: cenaPrzedRap) | **moved to Category C** |
| sprzedaz | `rabat` | "0" | OK (276927) | number |
| sprzedaz | `stawkaVat` | "2300","800" | both OK | integer (VAT code) |
| sprzedaz | `podatek` | "1" | OK (2149) | number |
| rapsprzed | `skladniki` | "0","1","true","false","T","N" | "0","1" OK | integer (1-8, doc says document type codes) |
| waluty | `kurs` | "1","4","4.20" | all OK | number |
| sklepy | `numer` | "1","99" | both OK | integer |
| kasy | `numer` | "1","99" | both OK | integer |

Total: **14 fields** across 7 endpoints (cenaPrzedRab moved to Category C after live verification).

### Category B2: par_bledna_wart - case-sensitive string enum (verified working with lowercase)

Initial probes used UPPERCASE values which all failed. Re-probing with documented lowercase
values confirmed these fields work correctly. The server requires exact lowercase enum strings.

| Endpoint | Field | Verified lowercase values (all OK) |
|----------|-------|------------------------------------|
| rapsprzed | `grupowanie` | `towar`(577), `asort`(11), `sklep`(2), `kasa`(2), `kasjer`(2), `kontr`(2), `kartarab`(2), `formaplatn`(3) |
| rappracy | `grupowanie` | `sklep`(2), `kasa`(3), `kasjer`(2) |
| rapsprzed | `skladniki` | `1`(1), `2`(1), `3`(1), `1,2,4,5,6,7,8`(1) - comma-separated list of document type codes |

Total: **3 fields** across 2 endpoints. All work with correct values. Need Java enum type.

### Category B-broken: par_bledna_wart - documented as filterable, server rejects all values

The server recognizes the parameter name (`par_bledna_wart`) but rejects every probed value
including values that match the documentation exactly.

| Endpoint | Field | Doc filtering | Doc values | Probed | Result |
|----------|-------|--------------|------------|--------|--------|
| towary | `typ` | Lista lub zakres | 0,2,4,5,6,7,8 | "0","2","5","7" | all 400 par_bledna_wart |
| waluty | `domyslna` | Boolean | true, false | true, false | both 400 par_bledna_wart |

Total: **2 fields**. Both documented as filterable, server rejects all documented values. Server bugs.

### Category C: server-side 500 errors

Individual probe calls that trigger HTTP 500 with server-side exceptions. These are vendor bugs
in the query implementation (wrong Hibernate column names, null collection dereferences).

| Endpoint | Field | Server error |
|----------|-------|-------------|
| pozdok | `id` | `Unknown column 'this.pozDokId' in 'where clause'` |
| pozdok | `dokumentTypDok` | NPE: `Cannot invoke Collection.size() because "c" is null` |
| pozdok | `dokumentDataWystawienia` | `Unknown column 'this.dok.dataDod'` |
| pozdok | `dokumentDataWplywu` | `Unknown column 'this.dok.data'` |
| pozdok | `dokumentDataWykonania` | `Unknown column 'this.dok.dataPom'` |
| towary | `cenaDet` | Hibernate: `could not resolve property: cenaDetBrutto of: WSTowar` |
| sprzedaz | `cenaPrzedRab` | Hibernate: `could not resolve property: cenaPrzedRap of: WSSprzedaz` |
| sprzedaz | `typDok` (string values) | NPE: `Cannot invoke Collection.size()` (integer "0" works) |

Pozdok has **9 fields that work individually**: `dokumentId`, `dokumentNrDok`,
`dokumentKontrahentId`, `dokumentPlatnikId`, `dokumentSklepId`, `dokumentKasaId`,
`dokumentKasjerId`, `towarId`, `nrPozycji`.

Total: **8 field/value combinations** across 3 endpoints.

## Decision

### 1. Remove Category A (server bug) parameters from QueryBuilders

All 11 fields documented as filterable but rejected by server are removed. The server does not
implement these filters. While the documentation says they should work, sending them crashes
every API call that includes them. Retaining them creates a trap for callers.

These can be re-added when the vendor fixes the server implementation.

### 2. Remove Category A2 (SDK spec error) parameters from QueryBuilders

Both `formyplatn.typ` and `formyplatn.nazwa` are removed. The documentation explicitly marks
these as "Brak" (no filtering). They should never have been exposed in the QueryBuilder.

### 3. Retype Category B working fields

The 15 fields confirmed working with correct types are retyped:

- `precyzja` (jmiary): `String` -> `Integer`
- `typDok` (dokumenty, sprzedaz): `String` -> `Integer` (enum codes: 21=paragon, 33=faktura
  odbiorcy, 34=faktura korygująca, 36=faktura do paragonu, 112=faktura fiskalna, 113=faktura
  korygująca do fakt fiskalnej; string values like "WZ" cause server 500)
- `brutto`, `cena`, `cenaPrzedRab`, `ilosc`, `podatek`, `rabat` (sprzedaz): keep as `String`
  (accepts both integer and decimal notation, supports min/max range prefixes)
- `stawkaVat` (sprzedaz): `String` -> `Integer` (VAT rate code, e.g. 2300=23%)
- `skladniki` (rapsprzed): `String` -> `Integer` (document type codes 1-8)
- `kurs` (waluty): keep as `String` (decimal, supports "4.20")
- `numer` (sklepy, kasy): `String` -> `Integer`

### 4. Retype Category B2 case-sensitive enum fields

Verified working with lowercase values. Change to Java enum types:

- `grupowanie` (rapsprzed): `String` -> Java enum with values: `towar`, `asort`, `sklep`,
  `kasa`, `kasjer`, `kontr`, `kartarab`, `formaplatn`
- `grupowanie` (rappracy): `String` -> Java enum with values: `sklep`, `kasa`, `kasjer`
- `skladniki` (rapsprzed): keep as `String` - accepts comma-separated lists like
  `"1,2,4,5,6,7,8"` which a Java enum cannot represent cleanly

### 5. Remove Category B-broken and Category C parameters from QueryBuilders

- `towary.typ`: documented as filterable (values 0,2,4,5,6,7,8) but server rejects all. Remove.
- `waluty.domyslna`: documented as Boolean filter but server rejects true/false. Remove.
- `pozdok.id`, `pozdok.dokumentTypDok`, `pozdok.dokumentData*`: all cause server 500. Remove.
- `towary.cenaDet`: causes server 500 (wrong Hibernate property). Remove.

Keep pozdok's 9 working fields (`dokumentId`, `dokumentNrDok`, `dokumentKontrahentId`,
`dokumentPlatnikId`, `dokumentSklepId`, `dokumentKasaId`, `dokumentKasjerId`,
`towarId`, `nrPozycji`).

### 6. Update demo-app runners after SDK changes

After removing/retyping fields, `runListPageAllFilters` in each runner is updated to use only
valid fields with correct types. A clean run with 0 failures across all 18 endpoints becomes
the regression baseline.

## Rationale

- A QueryBuilder method that always produces HTTP 400 or 500 is a trap. Removing it is
  strictly better than leaving it, regardless of whether the documentation says it should work.
- Distinguishing server bugs (Category A) from SDK spec errors (Category A2) is important:
  server bugs may be fixed by the vendor in the future, while spec errors need an SDK-side fix
  to the OpenAPI spec.
- The `grupowanie` field gets a re-probe before deciding because the only difference between
  our probed values and the documented values is letter case. This is a low-cost test that
  could save two useful fields.
- Retyping Category B fields from `String` to `Integer` catches type errors at compile time.
  Fields that accept decimal notation (cena, brutto, kurs, etc.) stay as `String` because
  `Integer` would be incorrect for "4.20" or "min5.5".
- Documentation cross-referencing adds a second source of truth. When both live testing AND
  documentation agree a field is filterable but the server rejects it, the diagnosis of
  "server bug" is high-confidence.

## Consequences

- **Breaking change** to QueryBuilder public API: ~19 methods removed, ~10 methods retyped
  (including 2 new enum types for grupowanie). Requires a minor version bump.
- Callers using removed fields must stop using them. For Category A (server bugs), the filter
  never worked anyway. For Category A2 (spec errors), the documentation never promised
  filtering.
- Callers using retyped Category B fields must change from `String` to `Integer`. The
  compiler will catch all such usages.
- The `runListPageAllFilters` regression test will pass cleanly after implementation.
- When the vendor fixes Category A server bugs, the corresponding fields can be re-added.
  Category A2 fields should only be re-added if the vendor updates the documentation to
  include filtering support.
