# ADR-029: Full Javadoc coverage for SDK public API

**Date:** 2026-03-28
**Status:** Accepted

---

## Context

The SDK had no Javadoc. Every public class, method, parameter, and return value was undocumented.
For a library intended to be used by external code, this means:
- IDE tooltip on `client.towary().listAll(null)` shows nothing.
- There is no discoverable description of `@throws` conditions.
- Builder fields like `stawkaVat`, `przySprzedazy`, `naDzien` have no explanation in the IDE.

The naming conventions (ADR-028: `*Client`, ADR-025: `*QueryBuilder`/`*CreateBuilder`/`*UpdateBuilder`)
are readable but not self-explanatory for domain-specific fields.

---

## Decision

Add Javadoc to all 63 hand-written Java source files in `novicloud-client/src/main/java/`.
Generated code in `target/generated-sources/` is excluded — it carries its own `@Generated` annotation
and is never edited.

### Coverage

**Infrastructure layer:**

| Class | Coverage |
|---|---|
| `NoviCloudClient` | Class, all 18 resource accessors, two `create()` factory methods, `builder()`, inner `Builder` |
| `RetryPolicy` | Already had Javadoc; verified complete |
| `RetryHandler` | Class, constructor, `ApiCall`, `VoidApiCall`, `execute()`, `executePost()`, `run()` |
| `PagedIterable` | Class with usage example, constructor |

**Exception hierarchy:**

| Class | Coverage |
|---|---|
| `NoviCloudException` | Class with catch-block example, constructor, `getStatusCode()`, `getResponseBody()`, `of()` factory with HTTP mapping table |
| `NoviCloudAuthException` | Class (HTTP codes 401/403, common causes) |
| `NoviCloudNotFoundException` | Class (HTTP codes 404/410) |
| `NoviCloudRateLimitException` | Class, constructor, `getRetryAfterSeconds()` |
| `NoviCloudServerException` | Class (HTTP codes 5xx, retry note) |
| `NoviCloudNetworkException` | Class (IOException/InterruptedException, no HTTP code) |

**18 resource clients (`*Client`):**

Every `*Client` class and all its public methods documented:
- Class: endpoint name, how to obtain via `NoviCloudClient`, supported operations.
- `listPage(query)`: `@param`, `@return`, `@throws NoviCloudException`.
- `listAll(query)`: lazy iteration note, `@throws` on each page fetch.
- `count(query)`: fallback logic note.
- `getById(id)` / `getByKod(kod)`: `@throws NotFoundException`, `@throws IllegalArgumentException` for null.
- `create(draft)`: required fields note, `@return` ID.
- `update(draft)`: `@throws NotFoundException`.
- `deleteById(id)`: `@throws NotFoundException`, `@throws IllegalArgumentException`.
- `StanyMagClient.listByTowar()`, `getByTowarAndSklep()`: specific parameter semantics (`naDzien`).
- `StawkiVatClient`: absence of `update()` noted at class level (ADR-022 reference).
- `KartyLojClient`: `getByKod(String)` instead of `getById(Long)` noted at class level.

**38 builder classes:**

All `*QueryBuilder`, `*CreateBuilder`, `*UpdateBuilder` classes:
- Class: purpose (filter/create/update), required fields for Create/Update, usage guidance.
- `builder(...)` factory: `@param` for required arguments, `@return`.
- All accessor methods (`field()`): one-line description of the field.
- Inner `Builder` class: `@see outer class`.
- All setter methods: one-line description + `@return this builder`.
- `build()` method: `@return` new instance.

### Language

All Javadoc is in English per `CLAUDE.md` policy. Polish technical identifiers from the NoviCloud API
(`towarId`, `sklepId`, `naDzien`, `stawkaVat`, `kod`, `nazwa`, etc.) are preserved as-is — they are
untranslatable technical identifiers that map directly to API request parameters.

### `@throws` policy

`@throws` is declared on `*Client` public methods only, not on builders (builders throw no checked
exceptions). Three exception types are declared:
- `NoviCloudException` — catch-all for API failures.
- `NoviCloudNotFoundException` — on `getById`/`getByKod`/`update`/`delete` where applicable.
- `IllegalArgumentException` — on methods that null-guard their parameters.

### What is NOT documented

- Private and package-private methods (implementation details).
- `module-info.java` (no public API).
- Generated code in `target/`.

---

## Consequences

### Positive
- Full IDE tooltip coverage: hovering over any SDK method shows purpose, parameters, return type,
  and expected exceptions.
- `@throws` declarations allow callers to write targeted catch blocks without reading source.
- Builder fields with Polish names (`przySprzedazy`, `naDzien`, `pkwiu`) are now explained in English.
- The `PagedIterable` class and lazy iteration semantics are explained at the class level.
- `StawkiVatClient` missing `update()` is discoverable from the class Javadoc, not just ADR-022.

### Neutral
- Build time unchanged — Javadoc is not compiled unless `mvn javadoc:javadoc` is invoked.
- 0 new Checkstyle / PMD / SpotBugs violations.

### Negative
- None. All 96 tests pass, static analysis clean.

---

## Related

- ADR-025: Package-per-endpoint and Builder naming (establishes class naming this Javadoc documents)
- ADR-028: `*Client` naming (establishes entry point this Javadoc documents)
- ADR-022: `StawkiVatClient.update()` absent due to server-side bug
