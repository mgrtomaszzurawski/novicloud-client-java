# NoviCloud Java SDK

OpenAPI-first Java SDK for the [NoviCloud REST API](https://system.novicloud.pl) (v2.10).
Covers all 18 API endpoints with typed builders, immutable records, automatic retry,
lazy pagination, and a structured exception hierarchy.

This is an unofficial, independent project - not affiliated with or endorsed by
NoviCloud (Insoft Sp. z o.o.).

**License:** [AGPL-3.0](LICENSE.txt) (dual licensing available on request).

## Quick start

```xml
<dependency>
    <groupId>io.github.mgrtomaszzurawski</groupId>
    <artifactId>novicloud-client</artifactId>
    <version>2.0.0</version>
</dependency>
```

```java
// Create client (Basic Auth - the only auth method the API supports)
NoviCloudClient client = NoviCloudClient.create(accountName, password);

// List all active products - lazy pagination, fetches pages on demand
for (Towar t : client.towary().list(
        TowarQueryBuilder.builder().aktywny(true).build())) {
    System.out.println(t.nazwa() + " - " + t.kod());
}

// Get single record by ID
Towar product = client.towary().getById(42L);

// Create - required fields enforced by the builder factory (since 2.0.0)
// Passing null for kod or nazwa throws NullPointerException locally.
String newId = client.towary().create(
    TowarCreateBuilder.builder("KOD-001", "Product name").build()
);

// Update (only id is required; other fields are optional partial update)
client.towary().update(
    TowarUpdateBuilder.builder(Long.parseLong(newId)).nazwa("New name").build()
);

// Delete (soft-delete for towary: sets aktywny=false, record stays in database)
client.towary().deleteById(Long.parseLong(newId));

// Close when done (guards against further use)
client.close();
```

All public API methods return immutable Java records (`Towar`, `Asorty`, `Dokument`, etc.)
with accessor methods using field names directly: `towar.nazwa()`, `towar.kod()`.

### Custom base URL (sandbox, WireMock)

```java
NoviCloudClient client = NoviCloudClient.create(
    "http://localhost:4010", accountName, password
);
```

### Custom retry policy

By default the SDK retries on HTTP 429 (rate limit, respects `Retry-After` header)
and HTTP 5xx (transient server errors) with exponential backoff + jitter, up to 3 attempts.
**POST (create) operations are not retried by default** since NoviCloud does not
document an idempotency guarantee. Opt in via `retryPost(true)` when the resource
has a server-enforced unique key.

```java
NoviCloudClient client = NoviCloudClient.builder()
    .retryPolicy(RetryPolicy.builder()
        .maxAttempts(5)
        .backoffStrategy(RetryPolicy.BackoffStrategy.FIXED)
        .retryPost(true)    // opt in only if duplicate handling is acceptable
        .build())
    .connectTimeout(Duration.ofSeconds(10))
    .readTimeout(Duration.ofMinutes(2))
    .build(accountName, password);
```

## Requirements

- Java 17+ (built and tested on OpenJDK 23)
- Maven 3.9+

## Build

```bash
mvn clean verify                          # full build + 594 tests
mvn spotbugs:check pmd:check checkstyle:check -pl novicloud-client,demo-app  # 0 violations
mvn dependency-check:check -pl novicloud-client   # OWASP CVE scan

# Release build (GPG signing + Maven Central publishing)
mvn clean deploy -Prelease -pl novicloud-client
```

## Repository structure

```
novicloud-client/             SDK library (published to Maven Central)
  openapi/                    Modular OpenAPI spec (source of truth)
  src/main/java/.../sdk/      Hand-written SDK code
    NoviCloudClient.java        Entry point - AutoCloseable facade with 18 resource accessors
    RetryPolicy.java            Configurable retry (429 + 5xx, exponential backoff + jitter)
    exception/                  Typed exception hierarchy
    model/                      Immutable records (Towar, Asorty, Dokument, etc.)
    paging/                     PagedResult (public API)
    resources/                  18 endpoint packages - public interfaces only
      towary/                     TowaryClient interface + TowarQueryBuilder + Create/UpdateBuilder
      asorty/                     AsortyClient interface + ...
      ...
    internal/                   Non-exported implementation (since 2.0.0)
      RetryHandler.java           Retry loop with SLF4J logging
      mapper/RawMappers.java      Generated-to-record mapping
      paging/LinkFetcher.java     Shared link-following pagination helper
      resources/                  18 *ClientImpl classes
  src/test/java/
    .../sdk/unit/               18 unit test classes (null guards, count fallback, error propagation)
    .../sdk/integration/        18 WireMock integration test classes (HTTP round-trip, retry, errors)
    .../sdk/builder/            Builder field round-trip + edge case tests
    .../sdk/TestConstants.java  Shared test constants (HTTP codes, headers, scenarios, indexes)
  src/test/resources/__files/   JSON fixtures for WireMock (35 files, 18 endpoint directories)

demo-app/                     Integration demo and live API smoke test
  src/main/java/.../demo/
    runner/                     18 *Runner classes (one per endpoint)
    runner/api/                 EndpointRunner, CreatesTestRecord, RunReport, RunnerHelper
    service/DemoSession.java    Orchestrates runners, captures exceptions, produces report
    config/                     AppProperties, Credentials, DemoMode, SoftDeleteIds, SoftDeleteIdsCollector

ADR/                          59 Architecture Decision Records
ADR-demo-app/                 10 demo-app specific decisions
docs/                         Postman collection + API reference
wiremock/                     WireMock stubs for local development (see wiremock/README.md)
context/                      Project documentation, session reports, reference material
```

## SDK coverage

| Endpoint | Path | Operations | Delete type | Notes |
|----------|------|------------|-------------|-------|
| towary | `/towary` | CRUD | soft (aktywny) | |
| asorty | `/asorty` | CRUD | hard | |
| jmiary | `/jmiary` | CRUD | hard | |
| stawkivat | `/stawkivat` | Create + Read + Delete | hard | PUT broken server-side ([ADR-022](ADR/ADR-022-stawkivat-write-policy.md)) |
| waluty | `/waluty` | CRUD | soft (aktywny) | `kod` must be valid ISO 4217 |
| kraje | `/kraje` | CRUD | hard | |
| formyplatn | `/formyplatn` | CRUD | soft (aktywny) | |
| kontrahenci | `/kontrahenci` | CRUD | soft (aktywny) | |
| sklepy | `/sklepy` | CRUD | soft (aktywny) | |
| kartyloj | `/f-karty-loj` | Create + Update + Read | soft (uniewazniono) | No DELETE; invalidation via PUT. Identified by `kod` (String), not numeric ID |
| stanymag | `/stanymag` | Read + Update | N/A | Three path variants: collection, by towar, by towar+sklep |
| dokumenty | `/dokumenty` | Read-only | N/A | |
| pozdok | `/pozdok` | Read-only | N/A | |
| sprzedaz | `/sprzedaz` | Read-only | N/A | |
| kasy | `/kasy` | Read-only | N/A | |
| kasjerzy | `/kasjerzy` | Read-only | N/A | |
| rapsprzed | `/rapsprzed` | Read-only | N/A | `grupowanie` enum: case-sensitive lowercase |
| rappracy | `/rappracy` | Read-only | N/A | `grupowanie` enum: case-sensitive lowercase |

**Soft-delete:** `deleteById()` does NOT remove the record. It sets `aktywny=false` (or
`uniewazniono` date for kartyloj). The record remains in the database and appears in unfiltered
list results. Use `.aktywny(true)` in the query builder to get active-only records.

**Known server-side bugs** affecting 21+ query filter parameters are documented in
[ADR-031](ADR/ADR-031-remove-broken-get-query-parameters.md). The SDK removes these broken
parameters from builders to prevent callers from hitting server errors.

## Exception hierarchy

```
NoviCloudException (base, unchecked)
  NoviCloudAuthException          401, 403
  NoviCloudAccessException        402 (REST API option not subscribed)
  NoviCloudNotFoundException      404, 410, or HTTP 200 with empty `dane`
  NoviCloudRateLimitException     429 (includes getRetryAfterSeconds())
  NoviCloudServerException        5xx
  NoviCloudNetworkException       IOException, timeout (no HTTP status)
```

All SDK methods throw `NoviCloudException` or a subclass. The retry handler automatically
retries 429 and 5xx before surfacing the exception. Other error types are thrown immediately.

POST requests are exempt from BOTH 5xx and 429 retry by default to avoid duplicate side
effects on writes. Opt in via `RetryPolicy.builder().retryPost(true)` when the resource has
a server-enforced unique key (so a duplicate-create surfaces as 400/409 instead of an extra
record). The opt-in covers 429 and 5xx on POST together.

## Testing

| Layer | Tests | Tool | What it covers |
|-------|-------|------|---------------|
| Unit | 117 | Mockito | Null guards, count() fallback logic, list() lazy construction, error propagation |
| Integration | 206 | WireMock | Full HTTP: deserialization, retry recovery, error mapping (incl. 402, network failures, unknown enum, page-2 error body), pagination, request verification |
| Builder | 151 | JUnit | Per-builder field round-trip, toBuilder() preservation, required-field NPE, kartyloj conditional, edge cases |
| Record / mapper | 14 | JUnit | RawMappers nested record mapping, empty-list fallbacks, pozycjeId/Url niuans |
| Other SDK | 72 | JUnit | RetryHandler (incl. POST 429 + Retry-After honour), RetryPolicy, NoviCloudException, NoviCloudErrorDetails, PagedResult, BuilderEdgeCases |
| Demo-app | 34 | JUnit | DemoSession, RunReport, RunResult, DemoMode, SoftDeleteIds, AppProperties |
| **Total** | **594** | | `mvn test` - no credentials or network needed |

All tests follow the `methodUnderTest_whenScenario_expectedResult` naming convention
with `// given / when / then` structure in method bodies ([ADR-052](ADR/ADR-052-test-quality-standards.md)).
Cross-cutting concerns (retry, exceptions, pagination) are tested end-to-end on every endpoint.
Per-endpoint tests verify correct field deserialization and CRUD operations.

## Demo app

The demo-app exercises all 18 SDK endpoints against a live or mock NoviCloud server.
Controlled by `demo.mode` property in `demo-app/src/main/resources/application.properties`.

| Mode | Hard-delete (4) | Soft-delete (6) | Read-only (8) |
|------|----------------|-----------------|---------------|
| **READ_ONLY** (default) | GET only | GET only | GET only |
| **CRUD_SAFE** | Full CRUD cycle | GET only | GET only |
| **CREATE_SOFT** | [SKIP] | Create + save ID | [SKIP] |
| **CRUD_ALL** | Full CRUD cycle | Update+Delete (saved IDs) | GET only |

```bash
# Credentials via environment variables
export NOVICLOUD_ACCOUNT_NAME=your_account
export NOVICLOUD_PASSWORD=your_password

# 1. Safe read-only run (default)
mvn exec:java -pl demo-app

# 2. Test hard-delete CRUD (no leftovers)
# Set demo.mode=CRUD_SAFE in application.properties
mvn exec:java -pl demo-app

# 3. Create soft-delete test records (one-time)
# Set demo.mode=CREATE_SOFT in application.properties
mvn exec:java -pl demo-app
# -> creates demo-soft-delete-ids.properties

# 4. Full CRUD including soft-delete
# Set demo.mode=CRUD_ALL in application.properties
mvn exec:java -pl demo-app
```

See [ADR-D010](ADR-demo-app/ADR-D010-demo-mode-toggle.md) for design details.

## Things you should know

**Immutable records:** All public API methods return Java records from `sdk.model`.
Generated model classes (`*Raw`) are internal - the `client.model` package is not exported
via JPMS. See [ADR-046](ADR/ADR-046-sdk-owned-immutable-records.md).

**AutoCloseable:** `NoviCloudClient` implements `AutoCloseable`. After `close()`, all accessor
methods throw `IllegalStateException`. Can be used with try-with-resources.
See [ADR-045](ADR/ADR-045-autocloseable-retry-jitter.md).

**Mixed date formats from server:** Some endpoints return `LocalDateTime` fields as
`"2019-08-28T13:57:39"`, others as `"2019-08-27"` (date-only). The SDK handles both
transparently via a custom Jackson deserializer.
See [ADR-039](ADR/ADR-039-flexible-localdatetime-deserializer.md).

**Polish field names:** The entire API uses Polish identifiers (`nazwa`, `kod`, `aktywny`,
`stawkaVat`, `towarId`, `sklepId`). These are untranslatable technical identifiers that map
directly to server JSON fields. The SDK preserves them as-is.

**Pagination:** The server controls page size (50 records max). There is no client-side page
size parameter. `list()` returns `PagedResult<T>` with lazy pagination, metadata access (`totalCount()`, `pageSize()`), and random access (`seek()`, `fetchFrom()`).

## JPMS

`novicloud-client` is a named Java module (`io.github.mgrtomaszzurawski.novicloud`).

**Exported:** `sdk`, `sdk.model`, `sdk.resources.*` (18 packages), `sdk.exception`, `sdk.paging`

**Not exported (private to the module):**
- `sdk.internal` and all `sdk.internal.*` sub-packages (RetryHandler, RawMappers, LinkFetcher, LinkUtils, 18 `*ClientImpl` classes)
- `client`, `client.api`, `client.model` (transport layer)

JPMS callers cannot import any of the above. See ADR-059 for the 2.0.0 design.

## Architecture Decision Records

59 SDK decisions in [`ADR/`](ADR/), 10 demo-app decisions in [`ADR-demo-app/`](ADR-demo-app/).

Key records:
- [ADR-005](ADR/ADR-005-overlay-on-generated-client-code.md) - SDK overlay pattern on generated code
- [ADR-019](ADR/ADR-019-exception-hierarchy-retry-handler.md) - Exception hierarchy and retry
- [ADR-020](ADR/ADR-020-draft-required-fields-in-builder.md) - Separate CreateBuilder/UpdateBuilder
- [ADR-022](ADR/ADR-022-stawkivat-write-policy.md) - StawkiVat PUT broken server-side
- [ADR-024](ADR/ADR-024-retry-policy-configurable.md) - Configurable RetryPolicy
- [ADR-031](ADR/ADR-031-remove-broken-get-query-parameters.md) - 21+ broken server-side query filters
- [ADR-032](ADR/ADR-032-rename-data-to-dane-in-response-envelopes.md) - `dane` field name in envelopes
- [ADR-034](ADR/ADR-034-wiremock-integration-testing.md) - WireMock integration testing
- [ADR-043](ADR/ADR-043-link-fetcher-shared-pagination.md) - LinkFetcher shared pagination helper
- [ADR-052](ADR/ADR-052-test-quality-standards.md) - Test naming convention and quality standards
- [ADR-044](ADR/ADR-044-sdk-public-api-redesign.md) - SDK public API redesign
- [ADR-045](ADR/ADR-045-autocloseable-retry-jitter.md) - AutoCloseable + retry jitter
- [ADR-046](ADR/ADR-046-sdk-owned-immutable-records.md) - Immutable records for response types
