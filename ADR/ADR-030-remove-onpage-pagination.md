# ADR-030: Remove onPage from QueryBuilders

**Date:** 2026-03-28
**Status:** Accepted

---

## Context

All 18 `*QueryBuilder` classes had an `onPage` field (type `Long`) intended to control the number
of records returned per page. This field was mapped to the `content` query parameter slot in the
generated `*Api` classes after the modular OpenAPI spec replaced `on_page` with `content`.

Investigation of the actual NoviCloud API behavior revealed:

1. The server **ignores** any numeric value sent as `on_page` / `content` in request query parameters.
   Page size is fixed server-side (returns up to 50 records per page; the actual count is in `on_page`
   in the response body).
2. The `content` parameter in the generated API is a **server-generated pagination session ID**
   returned inside pagination links (e.g. `?content=62206&start=0`). It must not be set manually;
   clients must follow the full URLs from `response.getLinks().getNext()`.
3. Sending an arbitrary integer as `content` would be silently ignored at best, or cause unexpected
   behavior at worst.

---

## Decision

Remove `onPage` entirely from all 18 `*QueryBuilder` classes:

- Removed: `private final Long onPage` field
- Removed: `this.onPage = builder.onPage` constructor assignment
- Removed: `public Long onPage()` accessor
- Removed: `private Long onPage` Builder field
- Removed: `public Builder onPage(Integer)` Builder setter
- `*Client.listPage()` calls: replaced `safe.onPage()` with `null` for the `content` parameter slot

In `demo-app`:
- Removed all `.onPage(N)` calls from runner builder chains
- Removed `logListPageOnPage` and `logListPageAktywnyOnPage` helper methods from `RunnerHelper`
  (they logged page size which no longer has meaning)
- Removed now-unused `PAGE_SIZE` and `LIST_ALL_PAGE_SIZE` constants from runner classes
- Removed now-unused `*QueryBuilder` imports from 7 runner classes whose only usage was the
  removed `onPage` call

---

## Consequences

### Positive
- SDK public API no longer exposes a no-op parameter that would mislead callers into thinking
  they can control page size.
- `*QueryBuilder` classes are smaller and cleaner.
- No behavioral change at runtime: `onPage` was being passed as `content` which the server ignores.

### Neutral
- Callers that previously used `.onPage(N)` in builder chains will get a compile error pointing
  to the removed method. The fix is to remove the call.
- Page size is fixed by the server. SDK users who need to process a limited number of records
  should either use `listPage(null)` (first page only) or break out of `listAll()` iteration early.

### Negative
- None. No test failures, 0 static analysis violations.

---

## Related

- ADR-025: QueryBuilder naming (establishes the builder classes this decision modifies)
- `novicloud-client/openapi/components/parameters/common.yaml`: `ContentQuery` parameter definition
  with note: "Do not set manually - use full URLs from response links for subsequent pages."
