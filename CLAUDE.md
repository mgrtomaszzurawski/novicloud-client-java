# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

OpenAPI-first Java 17 SDK for the NoviCloud REST API (v2.10). Multi-module Maven project:
- `novicloud-client` - the SDK library (published to Maven Central)
- `demo-app` - integration demo / smoke test app (not published)

The API is a Polish POS/inventory system. All field names are Polish (`nazwa`, `kod`, `aktywny`, `stawkaVat`). These are not translatable - they map directly to server JSON fields.

## Rules

**All documentation, ADRs, code comments, and commit messages must be in English.**
Exception: Polish API field names (`nazwa`, `kod`, `towarId`, `formyplatn`) are untranslatable technical identifiers.

If the agent breaks any rule from this file, it must immediately use `/digging-own-grave`.

## Build and test commands

```bash
# Full build + all 589 tests (no credentials needed)
mvn clean verify --no-transfer-progress

# Static analysis (must pass with 0 violations)
mvn spotbugs:check pmd:check checkstyle:check -pl novicloud-client,demo-app --no-transfer-progress

# Run a single test class
mvn test -pl novicloud-client -Dtest=TowaryClientTest --no-transfer-progress

# Run a single test method
mvn test -pl novicloud-client -Dtest=TowaryClientTest#testListPageReturnsData --no-transfer-progress

# SDK module only
mvn clean verify -pl novicloud-client --no-transfer-progress
```

## Architecture

### Maven multi-module structure

Root `pom.xml` is a `<packaging>pom</packaging>` aggregator with two modules:
- `novicloud-client` - the SDK jar. Has OpenAPI Generator plugin that runs at `generate-sources` phase, producing code into `target/generated-sources/openapi/`. Also configures GPG signing and Maven Central publishing (`central-publishing-maven-plugin`). `maven.deploy.skip` is NOT set - this module is publishable.
- `demo-app` - consumer app. Depends on `novicloud-client` as a regular Maven dependency. Has `<maven.deploy.skip>true</maven.deploy.skip>` - never published. No OpenAPI generation.

Both modules independently configure SpotBugs, PMD, and Checkstyle (pointing to shared config files at project root: `checkstyle.xml`, `pmd-ruleset.xml`, `spotbugs-exclude.xml`). There is no shared `<pluginManagement>` in the root pom - each module manages its own plugin versions.

The `-pl` flag targets specific modules: `mvn test -pl novicloud-client` runs SDK tests only; `mvn test -pl demo-app` runs demo tests only.

### Two-layer pattern: generated code + hand-written SDK overlay

The OpenAPI generator produces low-level HTTP client code at build time into `target/generated-sources/openapi/`. This code lives in packages `client.api` and `client.model`. It is never edited by hand.

The hand-written SDK layer in `sdk/` wraps the generated code and adds: retry, pagination, exception mapping, typed builders, and null guards. Each of the 18 API resources splits its public surface across two packages (since 2.0.0, ADR-059):
- `sdk/resources/<name>/` (exported via JPMS):
  - `*Client.java` - public **interface** (e.g. `TowaryClient`) declaring CRUD methods
  - `*QueryBuilder.java` - filter parameters for list operations
  - `*CreateBuilder.java` - required fields enforced via constructor (for writable endpoints)
  - `*UpdateBuilder.java` - only ID required; other fields are optional partial update
- `sdk/internal/resources/<name>/` (NOT exported):
  - `*ClientImpl.java` - implementation backing the interface; constructed only by `NoviCloudClient`

Cross-cutting concerns:
- `sdk.RetryPolicy` - public, configurable retry for 429/5xx
- `sdk.internal.RetryHandler` - non-exported retry loop with SLF4J logging (since 2.0.0)
- `sdk.paging.PagedResult` (public) + `sdk.internal.paging.LinkFetcher` (non-exported) - lazy link-following pagination with random access (ADR-051)
- `sdk.internal.mapper.RawMappers` - non-exported generated-to-record mapping
- `sdk.FlexibleLocalDateTimeDeserializer` - handles mixed date formats from server
- `sdk.exception/` - typed exception hierarchy mapped from HTTP status codes

### JPMS

The SDK is a named Java module (`io.github.mgrtomaszzurawski.novicloud`). `module-info.java` controls exports. Tests require `--add-reads` compiler args (already configured in pom.xml).

### OpenAPI spec

The source of truth is the modular spec at `novicloud-client/openapi/` (not the monolithic `openapi.json` at root). The spec is split into `paths/`, `components/schemas/`, and `components/parameters/` per endpoint.

## Code patterns and conventions

### Resource client structure

Public contract is an interface in `sdk.resources.<name>`; implementation is a `final class` in non-exported `sdk.internal.resources.<name>`. Use this template when adding or modifying endpoints (since 2.0.0, ADR-059):

```java
// sdk/resources/towary/TowaryClient.java - public interface
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary;

public interface TowaryClient {
    PagedResult<Towar> list(TowarQueryBuilder query);
    int count(TowarQueryBuilder query);
    Towar getById(Long id);
    String create(TowarCreateBuilder builder);
    void update(TowarUpdateBuilder builder);
    void deleteById(Long id);
}

// sdk/internal/resources/towary/TowaryClientImpl.java - non-exported impl
package io.github.mgrtomaszzurawski.novicloud.sdk.internal.resources.towary;

public final class TowaryClientImpl implements TowaryClient {

    // Fields: always this trio + RetryHandler
    private final ApiClient apiClient;
    private final TowaryApi api;           // generated API class
    private final String accountName;
    private final RetryHandler retryHandler;

    // Error messages as static final String constants
    private static final String ERR_LIST_PAGE = "Failed to list towary page";
    private static final String ERR_BUILDER_NULL = "builder must not be null";
    private static final String FIELD_ID = "id";

    // Constructor: always (ApiClient, String accountName, RetryPolicy)
    // Called only from NoviCloudClient; not part of the public API.
    public TowaryClientImpl(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) {
        this.apiClient = apiClient;
        this.accountName = accountName;
        this.api = new TowaryApi(apiClient);
        this.retryHandler = new RetryHandler(retryPolicy);
    }
```

Every client interface exposes these public methods (where the endpoint supports them):
- `list(QueryBuilder)` - returns `PagedResult<T>` with lazy pagination, random access (ADR-051)
- `count(QueryBuilder)` - returns `int` from `size` field, fallback to `dane.size()`
- `getById(Long)` - returns single entity, null-guarded with `requireNotNull`
- `create(CreateBuilder)` - uses `retryHandler.executePost()`, returns created ID as String
- `update(UpdateBuilder)` - uses `retryHandler.run()` (void)
- `deleteById(Long)` - uses `retryHandler.run()` (void)

### RetryHandler call conventions

Three methods, each for a different HTTP verb pattern:

```java
// GET, PUT, DELETE that return a value
retryHandler.execute(() -> api.listTowary(accountName, ...), ERR_LIST_PAGE);

// POST (create) - retryPost policy controls both 5xx and 429 retry on POST
retryHandler.executePost(() -> api.createTowar(accountName, body), ERR_CREATE);

// PUT, DELETE that return void
retryHandler.run(() -> api.updateTowary(accountName, body), ERR_UPDATE);
```

### Builder pattern

Three types of builders with distinct constructor signatures:

```java
// QueryBuilder: no required fields, static builder() factory
TowarQueryBuilder.builder()
    .aktywny(true)
    .kod("ABC")
    .build();

// CreateBuilder: required fields in builder() factory params
TowarCreateBuilder.builder("KOD-001", "Product name")  // kod, nazwa required
    .stawkaVat(2300)
    .cenaDet(19.99)
    .build();

// UpdateBuilder: only ID required
TowarUpdateBuilder.builder(42L)                        // id required
    .nazwa("Updated name")
    .build();
```

Builders are immutable records with a nested `Builder` class. Accessor methods use field name directly (not `get*`):
```java
builder.nazwa()    // not builder.getNazwa()
builder.kod()      // not builder.getKod()
```

Builder setters use inline one-liner format (allowed by checkstyle `LeftCurly nlow`):
```java
public Builder nazwa(String nazwa) { this.nazwa = nazwa; return this; }
```

### Broken parameters convention (ADR-031)

When a server-side query parameter is broken, it is removed from the QueryBuilder entirely. The corresponding position in the generated API call passes `null`. ADR-031 documents all 21+ broken parameters with their failure categories.

### Model-to-builder conversion (toModel pattern)

Each client has private `toTowar(CreateBuilder)` and `toTowar(UpdateBuilder)` methods that convert builder to generated model. Linked entities (foreign keys) use `Link` objects:

```java
if (builder.jmId() != null) {
    Link jm = new Link();
    jm.setId(builder.jmId());
    towar.setJm(jm);
}
```

### Null-safety pattern

All `getById`/`deleteById` methods validate input:
```java
private static void requireNotNull(Object value, String fieldName) {
    if (value == null) {
        throw new IllegalArgumentException(fieldName + ERR_NULL_SUFFIX);
    }
}
```

`list()` and `count()` accept null query (defaults to empty builder):
```java
TowarQueryBuilder safe = query != null ? query : TowarQueryBuilder.builder().build();
```

### Exception hierarchy

`NoviCloudException.of(message, cause, statusCode, headers, body)` (since 2.0.0,
neutral signature - no generated `ApiException` in the public API) maps HTTP
status to typed subclass:
- 401, 403 -> `NoviCloudAuthException`
- 402 -> `NoviCloudAccessException` (REST API option not subscribed; since 2.0.0)
- 404, 410, or HTTP 200 with empty `dane` -> `NoviCloudNotFoundException`
- 429 -> `NoviCloudRateLimitException` (parses `Retry-After` header)
- 5xx -> `NoviCloudServerException`
- code 0 with `IOException`/`InterruptedException` cause -> `NoviCloudNetworkException`

`NoviCloudException.getErrorDetails(): Optional<NoviCloudErrorDetails>` (since 2.0.0)
parses the HTTP 400 envelope and exposes `parNiewlasciwe` / `parBlednaWart` lists.

All exceptions are unchecked (`RuntimeException`).

## Test patterns

### Unit tests (`unit/*ClientTest.java`)

Each endpoint gets one test class with these standard tests:
```java
class TowaryClientTest {
    private static final String ACCOUNT = "test-account";
    private static final RetryPolicy NO_RETRY = RetryPolicy.builder().enabled(false).build();

    // Null guard tests
    void getById_nullId_throwsIllegalArgument()
    void deleteById_nullId_throwsIllegalArgument()

    // count() fallback logic - uses MockedConstruction
    void count_sizeNullFallsBackToListSize()
    void count_sizeNullNullListReturnsZero()
    void count_whenSizePresent_returnsSize()

    // list() accepts null
    void list_nullQuery_returnsIterable()
}
```

### Integration tests (`integration/*IntegrationTest.java`)

Use `@WireMockTest` annotation. JSON responses stored as fixture files in `src/test/resources/__files/{endpoint}/` (e.g. `__files/towary/list.json`, `__files/waluty/single.json`). Client created via `TestClients.create(wm)`:

```java
@WireMockTest
class TowaryClientIntegrationTest {
    private static final String LIST_FILE = "towary/list.json";
    private static final String SINGLE_FILE = "towary/single.json";

    private NoviCloudClient client;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wm) {
        client = TestClients.create(wm);  // retry disabled by default
    }

    @Test
    void list_returnsAllFieldsDeserialized() {
        stubFor(get(urlPathMatching("/[^/]+/towary"))
                .willReturn(TestClients.jsonFile(LIST_FILE)));  // WireMock withBodyFile

        List<Towar> dane = new ArrayList<>();
        client.towary().list(null).forEach(dane::add);
        // assert every field deserialized correctly
    }
}
```

URL patterns always use `/[^/]+/<endpoint>` regex (account name is dynamic path segment).

Dynamic JSON (pagination tests with port-dependent URLs) stays inline as local text blocks in the method. Static response fixtures go to `__files/`.

Cross-cutting tests (retry recovery, exception hierarchy, pagination) are tested on every endpoint, using WireMock scenarios. All write operations (create, update, delete) and retry tests use `WireMock.verify()` to confirm the HTTP request was actually sent:
```java
// Retry: first call returns 500, second succeeds
stubFor(get(...).inScenario(SCENARIO_RETRY)
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(serverError())
        .willSetStateTo(SCENARIO_STATE_RECOVERED));
// ...
verify(RETRY_REQUEST_COUNT, getRequestedFor(urlPathMatching(URL_LIST)));
```

### Builder tests (`builder/*BuilderTest.java`)

Test required-only construction, all-fields construction, and toBuilder preservation:
```java
void requiredFieldsOnly_optionalsNull()            // verify nulls for unset optional fields
void allCoreFieldsSet()                            // verify every field round-trips through builder
void toBuilder_whenAllFieldsSet_preservesAllFields() // verify toBuilder() copies all fields
```

Use named constants for test values (TB_ prefix for toBuilder tests), `DELTA` for double comparisons.

### TestClients helper

Shared factory in integration tests:
```java
TestClients.create(wm)        // retry disabled - for happy-path tests
TestClients.withRetry(wm)     // retry enabled  - for retry recovery tests
TestClients.jsonFile("e/f.json")  // WireMock ResponseDefinitionBuilder with bodyFile + Content-Type
TestClients.CREATED_JSON      // standard 201 response: {"status":201,"dane":{"id":"9999",...}}
TestClients.OK_JSON           // standard 200 response: {"status":200,"status_opis":"Ok"}
```

### TestConstants

Shared constants across all test types (unit, integration, exception):
- HTTP status codes: `HTTP_CREATED`, `HTTP_UNAUTHORIZED`, `HTTP_NOT_FOUND`, `HTTP_RATE_LIMITED`, `HTTP_SERVER_ERROR`, etc.
- HTTP headers: `CONTENT_TYPE_HEADER`, `APPLICATION_JSON`, `RETRY_AFTER_HEADER`
- WireMock scenarios: `SCENARIO_PAGINATION`, `SCENARIO_RETRY`, `SCENARIO_STATE_PAGE2`, `SCENARIO_STATE_RECOVERED`
- Unit test mocks: `MOCK_LIST_COUNT`, `MOCK_REPORTED_SIZE`, `EXPECTED_ZERO`
- Collection indexes: `FIRST_INDEX` through `FOURTH_INDEX`
- `apiServerError()` factory (named to avoid WireMock's `serverError()` conflict)

## Key conventions

### Response envelopes use `getDane()` not `getData()`
The server returns `"dane"` (Polish for "data"). All response envelopes use `getDane()`. See ADR-032.

### Soft-delete vs hard-delete
`deleteById()` on soft-delete endpoints (towary, waluty, kontrahenci, sklepy, formyplatn) sets `aktywny=false` - it does NOT remove the record. Hard-delete endpoints (asorty, jmiary, kraje, stawkivat) actually remove it. KartyLoj uses invalidation via `uniewazniono` date field instead of delete.

### Broken server-side query parameters
21+ query filter parameters are broken server-side (documented in ADR-031). The SDK intentionally removes them from builders. Do not re-add without verifying the server fix.

### StawkiVat PUT is broken
The server's PUT endpoint for stawkivat is broken. The SDK does not expose an update method. See ADR-022.

### Static analysis rules
- All string literals in error messages must be `static final` constants.
- Builder setters: one-liner `{ this.x = x; return this; }` format.
- Inline blocks are allowed (checkstyle `LeftCurly` set to `nlow`).
- Empty catch blocks must name the variable `ignored` or `expected`.
- Static analysis excludes generated code (`client.*` packages) - only `sdk.*` is analyzed.
- All three tools (SpotBugs, PMD, Checkstyle) must pass with 0 violations.

## Adding a new endpoint

Follow the existing pattern (each of the 18 endpoints is identical in structure since 2.0.0, ADR-059):
1. Add OpenAPI spec files: `openapi/paths/<name>.yaml`, `openapi/components/schemas/<name>.yaml`, `openapi/components/parameters/<name>.yaml`
2. Reference the new path in `openapi/openapi.yaml`
3. Create `sdk/resources/<name>/` with the **public interface** `*Client.java` plus Query/Create/Update builders (copy from an existing endpoint like `asorty` for hard-delete or `towary` for soft-delete). The interface declares the public method signatures only - no fields, no constructor.
4. Create the implementation `*ClientImpl.java` in non-exported `sdk/internal/resources/<name>/` (`public final class FooClientImpl implements FooClient`). The impl holds the `ApiClient`, generated `*Api`, account name, and `RetryHandler` fields and is constructed only by `NoviCloudClient`.
5. Create immutable record in `sdk/model/` (data-only - no `from()` factory)
6. Add a `static <Record> to<Record>(<Record>Raw)` mapping method in
   `sdk/internal/mapper/RawMappers.java` (since 2.0.0, ADR-058; replaces the
   old per-record `from(XRaw)` factories)
7. Wire record into the impl: `getById()` returns the record (call
   `RawMappers.to<Record>(raw)`), `list()` returns `PagedResult<T>` (ADR-051)
8. Add accessor method to `NoviCloudClient.java` (field of the **interface** type, constructor init that instantiates `*ClientImpl`, public accessor returning the interface)
9. Export ONLY the resource interface package in `module-info.java` (`exports sdk.resources.<name>;`). Do NOT export `sdk.internal.*` packages.
10. Add unit test, integration test, and builder tests (follow the existing
    test class templates) plus a `RawMappers` test if the record has nested
    structures or enums
11. Add a runner in `demo-app/` if needed

## Known issues

- (resolved in 2.0.0) Nested composite types are now wrapped: `Platnosc`,
  `DokumentRozbicieVat`, `DokumentRozliczany`, `TowarKodDodatkowy`,
  `TowarCenaWSklepie`, `TowarSkladnik`, `TowarSkladnikTowar` live in `sdk.model`

## ADRs

59 SDK decisions in `ADR/`, 10 demo-app decisions in `ADR-demo-app/`. Consult these before making architectural changes - many decisions document server-side bugs and API quirks that constrain the design.

Key records:
- ADR-005: SDK overlay pattern on generated code
- ADR-019: Exception hierarchy and retry
- ADR-020: Separate CreateBuilder/UpdateBuilder with required fields
- ADR-022: StawkiVat PUT broken server-side
- ADR-024: Configurable RetryPolicy
- ADR-028: *Client naming convention (AWS/GCP style)
- ADR-031: 21+ broken server-side query filters removed
- ADR-032: `data` -> `dane` in response envelopes
- ADR-034: WireMock integration testing
- ADR-039: Mixed date format handling
- ADR-043: LinkFetcher shared pagination helper (extracted from 18 duplicated clients)
- ADR-044: SDK public API redesign (envelope unwrap, list rename, model name fixes)
- ADR-045: AutoCloseable + retry jitter
- ADR-046: SDK-owned immutable records (modelNameSuffix: Raw, sdk.model package)
- ADR-D010: Demo-app mode toggle (READ_ONLY, CRUD_SAFE, CREATE_SOFT, CRUD_ALL)
- ADR-051: PagedResult - random access pagination (replaces PagedIterable)
- ADR-052: Test quality standards (naming, structure, TestConstants)
- ADR-056: Deprecate internal leak surface (sdk.RetryHandler, ApiException-typed signatures)
- ADR-058: Codex round-4 internal cleanup (sdk.internal.* hidden packages, neutral exception of())
- ADR-059: 2.0.0 release decision (interfaces in sdk.resources.*, impls in sdk.internal.resources.*)

## Additional context

**OpenAPI spec has edit protection.** A `.claude/hooks/` pre-tool hook blocks accidental edits to `openapi/` files. Always ask before modifying the spec.

**Type drift notes:**
- `StanMagUpdateBuilder.towarId()` and `sklepId()` return `String` (Link.id is String per ADR-011), not Long
- `StawkaVatCreateBuilder` uses `id` as `Integer` (VAT rate code), not Long like other endpoints
- Date fields in builders are `String` (parsed in client's `toXxx()` method), not `LocalDateTime`
