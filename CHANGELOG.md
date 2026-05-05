# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2026-05-03

This release was originally planned as `1.1.0` (Codex review fixes plus internal-leak
cleanup) but was bumped to `2.0.0` after round 5 review confirmed that several of the
"hardening" changes (`sdk.RetryHandler` -> `sdk.internal.RetryHandler`, `LinkUtils` ->
`sdk.internal.mapper.LinkUtils`, `NoviCloudException.of(String, ApiException)` ->
`(String, Throwable, int, HttpHeaders, String)`, all 18 `*Client` classes turned into
interfaces in `sdk.resources.*` with implementations relocated to non-exported
`sdk.internal.resources.*`) are source-incompatible breaking changes and therefore
require a major version under SemVer. There is no separate `1.1.0` release. (ADR-059)

### Migration guide for 1.0.0 callers

For the standard recommended usage path (`NoviCloudClient.builder().build(...)` plus
the 18 `client.<endpoint>()` accessors) **no source changes are required**. Breaking
changes affect only:

- Code that imported `io.github.mgrtomaszzurawski.novicloud.sdk.RetryHandler` directly
  -> the type is now `sdk.internal.RetryHandler` and is no longer part of the public API.
  Construct via `NoviCloudClient.builder().retryPolicy(...)` instead.
- Code that imported `io.github.mgrtomaszzurawski.novicloud.sdk.model.LinkUtils`
  -> moved to `sdk.internal.mapper.LinkUtils` (no longer public).
- Code that called `NoviCloudException.of(message, apiException)` directly -> the
  signature changed to `of(message, cause, statusCode, headers, body)`. Application
  code should not have been catching this constructor; it is intended for internal
  call sites.
- Code that constructed `*Client` instances directly (for example
  `new TowaryClient(apiClient, account, retryPolicy)`) -> not possible in 2.0.0.
  `*Client` types are now interfaces; implementations live in `sdk.internal.resources.*`
  and are constructed by the `NoviCloudClient` facade.

### Behaviour changes (read carefully)

- **POST retry default flipped from `true` to `false`** (F-01, ADR-057).
  NoviCloud does not document an idempotency contract for POST. The 1.0.0
  default could silently produce duplicate records when a successful POST
  was followed by a lost response or a delayed 5xx. Callers that need the
  old behaviour can opt in with `RetryPolicy.builder().retryPost(true)`.
- `Retry-After` now sleeps **at least** the server-requested duration. The
  1.0.0 jitter formula could sleep less than the server asked, breaking
  HTTP contract and risking repeated 429s (F-09, ADR-057).

### Fixed

- **F-02 (HIGH, second-pass Codex review):** `IOException` and `InterruptedException`
  thrown by the underlying `HttpClient` now surface as `NoviCloudNetworkException`
  on first-page / single-record calls. Previously only `LinkFetcher`-driven
  page-2+ calls mapped correctly; the README's I/O exception promise was a
  half-truth. (ADR-057)
- **F-03 (HIGH):** Producer-introduced enum codes no longer abort
  deserialization. `ObjectMapper` now uses
  `READ_UNKNOWN_ENUM_VALUES_AS_NULL`; unknown values land in the SDK record
  as `null` (e.g. `Towar.typ()` returns `null` if the server sends `typ=99`).
  (ADR-057)
- **F-04 (MEDIUM):** New `NoviCloudAccessException` for HTTP 402 (REST API
  option not subscribed). Previously fell through to the base
  `NoviCloudException`. (ADR-057)
- **F-05 (MEDIUM):** Pagination errors now preserve the response body, matching
  first-page behaviour. (ADR-057)
- **F-06 (MEDIUM):** Internal-leak surface physically hidden:
  - `LinkFetcher` moved from exported `sdk.paging` to non-exported
    `sdk.internal.paging` package.
  - All record `from(XRaw)` factories removed; mapping logic centralised
    in `sdk.internal.mapper.RawMappers` (non-exported). Records are now
    pure data classes with no generated-type imports.
  - All 18 `*Client` types are now interfaces in `sdk.resources.*`;
    implementations (`*ClientImpl`) moved to non-exported
    `sdk.internal.resources.*`. The previous `*Client` public constructors
    no longer exist - external code must use `NoviCloudClient` accessors.
  (ADR-057, ADR-058, ADR-059)
- **F-07 (MEDIUM):** New `NoviCloudErrorDetails` record and
  `NoviCloudException.getErrorDetails()` provide typed access to HTTP 400
  validation envelopes (`par_niewlasciwe`, `par_bledna_wart`). (ADR-057)
- **F-08 (MEDIUM):** OpenAPI `paths/*.yaml` now consistently declare
  HTTP 410 (API version no longer available) and 501 (API version not
  implemented) responses on every operation. (ADR-057)
- **F-09 (LOW):** `Retry-After` honoured exactly (no jitter below the server
  delay). Backoff jitter still applies to the exponential/fixed strategies
  when no `Retry-After` was supplied. Maven compiler now uses
  `<release>17</release>` after the SpotBugs upgrade unblocked it. (ADR-057)
- **F-10 (LOW):** Root pom `<tag>` updated from `v1.0.0` to `v2.0.0`
  (originally bumped to `v1.1.0` during round-3 work, then re-bumped to
  `v2.0.0` in round 5 per ADR-059). (ADR-057, ADR-059)
- **CF-01 (MEDIUM, round 4):** `RetryHandler` moved from exported `sdk` to
  non-exported `sdk.internal`. `LinkUtils` moved from exported `sdk.model` to
  non-exported `sdk.internal.mapper`. `NoviCloudException.of(...)` signature
  changed from `(String, ApiException)` to
  `(String, Throwable, int, HttpHeaders, String)` so the public exception API
  no longer references the generated `ApiException` type. (ADR-058)
- **CF-02 (MEDIUM, round 4):** `RetryPolicy.retryPost(false)` now also blocks
  POST retry on HTTP 429 (previously only blocked 5xx for POST). The README
  contract "POST create operations are not retried by default" is now true
  for both transient failure classes. (ADR-058)
- **CF-04 (LOW, round 4):** Added a regression test that asserts page-2+
  pagination errors preserve the response body via `LinkFetcher`.
- **SQ-1 (round 4):** Cleared 14 SonarQube S2629 logger-guarding warnings in
  demo-app `*Runner.java` files by hoisting record accessors to local
  variables before the log call.
- **F-01 (HIGH):** `getById()` no longer throws `NullPointerException` when the server
  returns HTTP 200 with `dane=null` (e.g. after hard-delete on asorty/jmiary/kraje/stawkivat,
  or per ADR-033). The SDK now throws `NoviCloudNotFoundException` with HTTP status 200
  and a clear message. Applies to all 16 single-record reads, plus
  `KartyLojClient.getByKod()` and `StanyMagClient.getByTowarAndSklep()`. (ADR-053)
- **F-11:** Stale Javadoc example in `NoviCloudException` updated to current API
  (`getById()` returns the record directly, not an envelope).

### Added

- New exception type `NoviCloudAccessException` for HTTP 402 (REST API option
  not subscribed). Subclass of `NoviCloudException`. (F-04, ADR-057)
- New record `NoviCloudErrorDetails` and accessor
  `NoviCloudException.getErrorDetails()` returning `Optional<NoviCloudErrorDetails>`.
  Parses `dane.par_niewlasciwe` / `dane.par_bledna_wart` from HTTP 400 envelopes.
  (F-07, ADR-057)
- **F-12:** `Towar` now exposes nested product data:
  - `kodyDod()` returns `List<TowarKodDodatkowy>` (additional barcodes)
  - `cenyWSklepach()` returns `List<TowarCenaWSklepie>` (per-store prices)
  - `skladniki()` returns `List<TowarSkladnik>` (bundle components for product type 5)
- **F-13:** `Dokument` now exposes nested document data:
  - `rozbicieVat()` returns `List<DokumentRozbicieVat>` (VAT breakdown)
  - `platnosci()` returns `List<Platnosc>` (payment breakdown)
  - `korektyIds()`, `fakturyIds()`, `dokMagazynoweIds()`, `paragonyIds()` return
    `List<String>` (related document IDs)
  - `dokRozliczane()` returns `List<DokumentRozliczany>` (settlement details)
  - `pozycjeId()` (clear name for the line-items resource ID)
  - `pozycjeUrl()` (the actual URL from the link object)
- **F-14:** `Sprzedaz.platnosci()` returns `List<Platnosc>` (payment breakdown).
- **F-05:** `*CreateBuilder.builder(...)` factories now reject `null` required arguments
  with `NullPointerException` (`Objects.requireNonNull`). `KartaLojCreateBuilder.build()`
  also enforces the conditional rule "at least one of telefon/email" plus required
  `nazwiskoImie` per ADR-033. (ADR-055)
- **F-06:** OpenAPI `KartaLojalnosciowa` schema now declares `kod` and `nazwiskoImie`
  as `required`. The conditional `telefon-or-email` rule remains documented in the
  description and enforced at SDK level (no idiomatic OpenAPI for "at least one of").

### Removed (breaking)

- **F-02 / CF-02:** Public constructors of all 18 `*Client` classes are gone.
  `*Client` types are now interfaces in `sdk.resources.*`; implementations
  (`*ClientImpl`) live in non-exported `sdk.internal.resources.*`. Construct
  via `NoviCloudClient.builder().build(...)` and access through the facade
  methods. (ADR-056, ADR-058, ADR-059)
- **CF-01:** `io.github.mgrtomaszzurawski.novicloud.sdk.RetryHandler` removed
  from the public API. The type now lives in non-exported `sdk.internal`.
  (ADR-058)
- **CF-01:** `io.github.mgrtomaszzurawski.novicloud.sdk.model.LinkUtils` removed
  from the public API. Moved to non-exported `sdk.internal.mapper.LinkUtils`.
  (ADR-058)

### Deprecated

- **F-13:** `Dokument.pozycjeLink()` accessor is deprecated due to its misleading
  name (it returns the resource ID, not the URL). Use `pozycjeId()` or
  `pozycjeUrl()` instead.

### Changed (build hygiene)

- **F-08:** SCM `developerConnection` reformatted to standard
  `scm:git:git@github.com:...` syntax. GPG `keyname` is now driven by the
  `gpg.keyname` Maven property (default unchanged for the current maintainer).
- **F-07:** Generated OpenAPI test sources (`*RawTest`, `*ApiTest` skeletons)
  are no longer added to the test compile path. Only handwritten SDK and
  demo-app tests are compiled and executed.
- `spotbugs-maven-plugin` upgraded to 4.9.6.0 (was 4.8.6.1). The bundled ASM
  version 4.8.6.1 cannot read JDK 25 bytecode, causing analysis crashes on
  systems with JDK 25 installed (`Unsupported class file major version 69`).
- `NoviCloudDemoApp.main()` now uses try-with-resources around `NoviCloudClient`,
  matching the AutoCloseable contract introduced in 1.0.0 (PMD CloseResource).

### Notes

- `LinkFetcher` was physically moved to the non-exported package
  `sdk.internal.paging` in this release (ADR-057).
- All record `from(XRaw)` factories were physically removed in this release;
  mapping logic now lives in `sdk.internal.mapper.RawMappers` (ADR-057).
- `RetryHandler` and `LinkUtils` were moved to `sdk.internal.*` in this
  release (ADR-058). `NoviCloudException.of(...)` no longer references
  the generated `ApiException` type in its signature.
- "Round 3" / "round 4" labels in the audit reports refer to internal
  Codex re-review iterations during the development of 2.0.0; they do
  not correspond to separate published releases.

## [1.0.0] - 2026-04-02

### Added

- SDK facade: `NoviCloudClient` with builder pattern, `AutoCloseable`, static factory methods
- 18 resource clients covering all NoviCloud REST API v2.10 endpoints:
  asorty, dokumenty, formyplatn, jmiary, kartyloj, kasjerzy, kasy,
  kontrahenci, kraje, pozdok, rappracy, rapsprzed, sklepy, sprzedaz,
  stanymag, stawkivat, towary, waluty
- Immutable SDK records in `sdk.model` for all 18 response types (ADR-046);
  generated model classes (`*Raw`) are internal and not exported via JPMS
- `PagedResult<T>` - lazy pagination with metadata access (`totalCount()`, `pageSize()`),
  random access (`seek()`, `seekFromPage()`, `fetchFrom()`), and bidirectional
  `ListIterator` support (ADR-051)
- `LinkFetcher` - shared pagination helper extracted from 18 clients (ADR-043)
- `LinkUtils` - shared link-to-ID extraction for model records
- Automatic retry with exponential backoff and jitter for 429/5xx (ADR-045)
- Configurable `RetryPolicy` builder (ADR-024)
- SLF4J logging in `RetryHandler` (retry INFO/DEBUG) and `LinkFetcher` (pagination DEBUG)
- Typed exception hierarchy mapped from HTTP status codes (ADR-019):
  `NoviCloudAuthException` (401/403), `NoviCloudNotFoundException` (404/410),
  `NoviCloudRateLimitException` (429, includes `getRetryAfterSeconds()`),
  `NoviCloudServerException` (5xx), `NoviCloudNetworkException` (IOException)
- Three builder types per endpoint: `*QueryBuilder`, `*CreateBuilder`, `*UpdateBuilder`
  with required fields enforced via constructor, `toBuilder()` on all 38 builders (ADR-020)
- Input validation on all public methods (null guards, date format checks)
- JPMS module: `io.github.mgrtomaszzurawski.novicloud`
- `package-info.java` for all 21 exported packages with `@since 1.0.0`
- `@since 1.0.0` on all public classes
- SPDX license headers (AGPL-3.0-only) on all source files (ADR-050)
- 548 tests (514 SDK + 34 demo-app), 0 static analysis violations:
  - 117 unit tests (null guards, count fallback, error propagation)
  - 193 WireMock integration tests (deserialization, retry, errors, pagination, CRUD verification)
  - 139 builder tests (field round-trip, toBuilder preservation, edge cases)
  - 65 infrastructure tests (RetryHandler, RetryPolicy, PagedResult, exceptions)
  - 34 demo-app tests (DemoSession, DemoMode, SoftDeleteIds, RunReport)
- Test quality standards: `methodUnderTest_whenScenario_expectedResult` naming,
  `// given / when / then` structure, `TestConstants` shared constants,
  JSON fixtures in `__files/{endpoint}/`, `WireMock.verify()` on all write and retry tests (ADR-052)
- Demo application with 4 modes: READ_ONLY, CRUD_SAFE, CREATE_SOFT, CRUD_ALL (ADR-D010)
- 52 SDK ADRs + 10 demo-app ADRs documenting all architectural decisions
- Examples: 6 standalone Java files in `examples/`
- OpenAPI spec for NoviCloud REST API v2.10
- OWASP dependency-check-maven configured (manual, not bound to lifecycle)
- SonarQube 9.9.8 LTS: A/A/A, 0 bugs, 0 vulnerabilities, 0 code smells (ADR-048)

### Known limitations (resolved in 2.0.0)

- Nested composite types (Platnosc, RozbicieVat, TowarSkladnik, TowarKodDodatkowy,
  TowarCenaWSklepie, DokumentRozliczany) are not wrapped in SDK records - raw types
  returned inline. Resolved in 2.0.0; see [2.0.0] section above.
- `java.net.http.HttpClient` is not `AutoCloseable` on JDK 17;
  `NoviCloudClient.close()` guards accessor methods but held references
  to resource clients remain usable after close (ADR-045)
- Server-side bugs documented in ADR-031 (21+ broken filters) and
  ADR-022 (StawkiVat PUT broken)
