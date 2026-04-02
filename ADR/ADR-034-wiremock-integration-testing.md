# ADR-034: WireMock for integration testing and local development

## Status

Accepted

## Context

The NoviCloud SDK has two testing needs that require an HTTP mock server:

**1. SDK integration tests (automated)**

The SDK's hand-written client layer (`*Client.java`) sits between the application and the
generated OpenAPI client code. It handles retry logic (`RetryHandler`), exception mapping
(`NoviCloudException` hierarchy), pagination (`PagedIterable`), and response deserialization.

Before this ADR, these critical paths had no end-to-end HTTP coverage:
- Unit tests for `RetryHandler` used mock `ApiException` objects, not real HTTP responses
- Unit tests for `PagedIterable` used in-memory suppliers, not real link-following
- Exception hierarchy was tested via direct construction, not via actual HTTP status codes
- Deserialization of server responses was never tested against realistic JSON payloads

**2. Demo-app local testing (manual)**

The demo-app (`NoviCloudDemoApp`) needs a way to run against a mock server for development
and debugging without hitting the live NoviCloud API. This avoids creating/deleting real records
during development and allows testing without valid credentials.

## Decision

Use WireMock in two complementary forms:

### WireMock standalone (manual, port 4010)

A standalone JAR (`wiremock/wiremock-standalone.jar`, version 3.12.1) with pre-built stubs:
- `wiremock/mappings/*.json` - URL pattern to response mappings for all 18 endpoints
- `wiremock/__files/*.json` - response body files with realistic field data

Start manually:
```bash
java -jar wiremock/wiremock-standalone.jar --root-dir wiremock --port 4010 --global-response-templating
```

This serves the demo-app and allows rapid iteration on runners without live API calls.

### WireMock test dependency (automated, random port)

Added `org.wiremock:wiremock-standalone:3.12.1` as `<scope>test</scope>` dependency in
`novicloud-client/pom.xml`. Integration tests use the `@WireMockTest` JUnit 5 extension
which starts WireMock on a random port per test class and resets stubs between tests.

Test classes are in `io.github.mgrtomaszzurawski.novicloud.sdk.integration` and follow
a consistent pattern:
- `@BeforeEach` creates a `NoviCloudClient` pointing at WireMock via `TestClients.create(wm)`
- Each test stubs the relevant endpoint(s) with inline JSON and exercises the SDK client
- Assertions verify field-level deserialization, not just "no exception thrown"

### Test coverage per endpoint type

| Endpoint type | Operations tested | Count |
|--------------|-------------------|-------|
| Full CRUD (asorty, jmiary, kraje, towary, waluty, kontrahenci, sklepy, formyplatn) | list, count, getById, create, update, delete | 6 each |
| Create+Read (stawkivat) | list, count, getById, create, delete + retry, exceptions, pagination | 11 |
| Read+Update (kartyloj, stanymag) | list, count, get, create/update | 5 each |
| Read-only (dokumenty, pozdok, sprzedaz, kasy, kasjerzy) | list, count, getById | 3 each |
| Reports (rapsprzed, rappracy) | list, count | 2 each |

### Extended tests on StawkiVat (single endpoint, all cross-cutting concerns)

StawkiVat was chosen as the endpoint to verify the SDK's cross-cutting behavior because it
has a simple model (no date fields, no complex nested objects) and supports enough operations
to exercise all code paths:

1. **Retry recovery** - WireMock Scenario: first request returns HTTP 500, second returns 200.
   Verifies `RetryHandler` actually retries through real HTTP, not just mock `ApiException`.

2. **Exception hierarchy** - Four tests stub HTTP 401, 404, 429, 500 and verify the correct
   `NoviCloudException` subclass is thrown (`Auth`, `NotFound`, `RateLimit`, `Server`).
   The 429 test also verifies `Retry-After` header parsing.

3. **Multi-page pagination** - WireMock Scenario with two pages. First response includes
   `links.next` pointing to WireMock. Verifies `PagedIterable` follows the link and
   `listAll()` returns records from both pages.

These cross-cutting tests are only on one endpoint because the retry, exception, and
pagination logic is shared across all clients via `RetryHandler` and `PagedIterable`.
Testing it once end-to-end is sufficient; per-endpoint tests focus on deserialization.

## Consequences

**Positive:**
- 88 new integration tests catch deserialization bugs that unit tests cannot (e.g., the
  `data_wystawienia: "DUMMY_TEXT"` bug in dokumenty stubs that caused `JsonMappingException`)
- Retry and exception paths are tested through real HTTP for the first time
- Tests run in ~3 seconds total (WireMock in-process, no external server needed)
- `mvn clean verify` catches HTTP-level regressions automatically
- Standalone stubs and test stubs use the same JSON patterns, reducing drift

**Negative:**
- `wiremock-standalone` adds ~30MB to test classpath (acceptable for test scope)
- Some generated model getters use unexpected casing (`getwCZakNetto()` instead of
  `getWCZakNetto()`) and `Object` return types instead of `Link` for `$ref` sibling fields.
  Tests had to adapt to these quirks, which documents them as known generator behavior.
- Standalone JAR is not managed by Maven (manual download), but it only serves local
  development and is not required for the build

## Test file inventory

```
novicloud-client/src/test/java/.../sdk/integration/
  TestClients.java                        -- factory + shared constants
  StawkiVatClientIntegrationTest.java     -- 11 tests (basic + cross-cutting)
  AsortyClientIntegrationTest.java        -- 6 tests
  JmiaryClientIntegrationTest.java        -- 6 tests
  KrajeClientIntegrationTest.java         -- 6 tests
  TowaryClientIntegrationTest.java        -- 6 tests
  WalutyClientIntegrationTest.java        -- 6 tests
  KontrahenciClientIntegrationTest.java   -- 6 tests
  SklepyClientIntegrationTest.java        -- 6 tests
  FormyPlatnClientIntegrationTest.java    -- 6 tests
  KartyLojClientIntegrationTest.java      -- 5 tests
  StanyMagClientIntegrationTest.java      -- 5 tests
  DokumentyClientIntegrationTest.java     -- 3 tests
  PozdokClientIntegrationTest.java        -- 3 tests
  SprzedazClientIntegrationTest.java      -- 3 tests
  KasyClientIntegrationTest.java          -- 3 tests
  KasjerzykClientIntegrationTest.java     -- 3 tests
  RapSprzedClientIntegrationTest.java     -- 2 tests
  RapPracyClientIntegrationTest.java      -- 2 tests
```
