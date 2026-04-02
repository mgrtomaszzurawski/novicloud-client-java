# ADR-044: SDK public API redesign before v1.0.0

**Date:** 2026-03-31
**Status:** Accepted

## Context

Before the first public release, the SDK's public API had several inconsistencies
and unnecessary complexity inherited from early development:

1. **Wrong Polish abbreviations in OpenAPI spec.** Model names `Asort` and `Jmiara`
   were incorrect abbreviations. The correct forms are `Asorty` (from "asortymenty")
   and `Jmiary` (from "jednostki miary"). These propagated through generated code
   into all SDK classes.

2. **Response envelopes leaked to consumers.** `getById(id)` returned `ApiResponseTowar`
   instead of `Towar`. Consumers had to call `.getDane()` on every single-record fetch,
   even though the envelope's status/description fields were redundant (errors already
   throw exceptions). Meanwhile `create()` already returned unwrapped `String` and
   `count()` returned unwrapped `int` - inconsistent.

3. **Pagination internals exposed.** `listPage(query)` was public, returning the raw
   envelope with pagination links. But consumers cannot use pagination links directly
   because the `content` token is server-generated and not exposed in QueryBuilder.
   `listAll(query)` was the only useful list method. Having both was confusing.

4. **Naming inconsistencies.** `KasjerzykClient` (typo, should match endpoint `/kasjerzy`).
   `RapSprzedGrupowanie`/`RapPracyGrupowanie` used Polish for an SDK-defined enum type
   when the rest of the SDK uses English for non-API concepts. Parameter name `draft`
   remained in private methods after the `*Draft` -> `*Builder` class rename.

## Decision

Apply all changes as a single batch before v1.0.0 (no external consumers to break).

### 1. Fix OpenAPI model names

Changed in `openapi/components/schemas/`:
- `asorty.yaml`: `Asort:` -> `Asorty:`
- `jmiary.yaml`: `Jmiara:` -> `Jmiary:`

Updated all `$ref` references in envelopes, paths, and openapi.yaml. Updated operationIds
(`createAsort` -> `createAsorty`, `getAsortById` -> `getAsortyById`, etc.).

Generator now produces `Asorty.java`, `Jmiary.java` and all derived types with correct names.

Other model names (`Towar`, `Kraj`, `Kontrahent`, etc.) are correctly declined and unchanged.

### 2. Unwrap getById return type

Before: `public ApiResponseTowar getById(Long id)`
After: `public Towar getById(Long id)`

The client calls `.getDane()` internally. If the server returns an error status,
the SDK throws an exception before reaching `.getDane()`, so the envelope adds nothing
for the consumer.

Applied to all 14 clients that have `getById` (or `getByKod` for KartyLojClient -
unchanged, already returned `ApiResponseKartaLoj`).

KartyLojClient's `getByKod` was NOT changed because the response structure differs
(lookup by string code, not numeric ID).

### 3. Remove listPage from public API

`listPage` changed from `public` to `private`. It remains as an internal method used by
`list()`, `count()`, and `PagedIterable` pagination.

`listAll(query)` renamed to `list(query)`. It is the only public list method.
Returns `Iterable<T>` (lazy, fetches pages on demand). Consumers who need `List<T>`
collect manually - this is a conscious memory decision the SDK should not hide.

Final public API per client:
```java
client.towary().list(query)        // Iterable<Towar> - lazy, all pages
client.towary().count(query)       // int
client.towary().getById(id)        // Towar
client.towary().create(builder)    // String (id)
client.towary().update(builder)    // void
client.towary().deleteById(id)     // void
```

### 4. Naming fixes

- `KasjerzykClient` -> `KasjerzyClient` (typo fix, endpoint is `/kasjerzy`)
- `RapSprzedGrupowanie` -> `RapSprzedGroup` (English for SDK-defined types)
- `RapPracyGrupowanie` -> `RapPracyGroup`
- `draft` parameter -> `builder` in all private `toXxx()` conversion methods
- `ERR_DRAFT_NULL` -> `ERR_BUILDER_NULL`
- Builder classes renamed to match: `AsortyQueryBuilder`, `JmiaryCreateBuilder`, etc.

## Consequences

- All 18 SDK clients updated
- All 18 demo-app runners updated
- All SDK unit tests (308) and demo-app tests (15) updated and passing
- Static analysis: 0 violations (SpotBugs, PMD, Checkstyle)
- Removed unused single-record envelope imports from 14 clients
- `module-info.java` unchanged (exports same packages)
- Breaking change for any code using old method names/return types - acceptable pre-v1.0.0
- Design details documented separately (internal working document)
