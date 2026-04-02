# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

### Known limitations

- Nested composite types (Platnosc, RozbicieVat, TowarSkladnik) are not
  wrapped in SDK records - raw types returned inline
- `java.net.http.HttpClient` is not `AutoCloseable` on JDK 17;
  `NoviCloudClient.close()` guards accessor methods but held references
  to resource clients remain usable after close (ADR-045)
- Server-side bugs documented in ADR-031 (21+ broken filters) and
  ADR-022 (StawkiVat PUT broken)
