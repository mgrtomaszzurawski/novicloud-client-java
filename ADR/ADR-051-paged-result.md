<!--
Copyright (c) 2026 Tomasz Zurawski
SPDX-License-Identifier: AGPL-3.0-only
https://github.com/mgrtomaszzurawski/novicloud-client-java
-->
# ADR-051: PagedResult - random access pagination

**Date:** 2026-04-01
**Status:** Accepted

## Context

`list()` on all 18 resource clients returned `Iterable<T>`. The caller had no access to
pagination metadata that the server already includes in every list response:

- `size` - total record count matching the query
- `on_page` - number of records on this page (1-50, server-controlled)
- `content` - session ID embedded in `links.self` URL (`?content=X&start=N`), used internally for random access

To get the total count, callers had to make a separate `count()` HTTP call even though
`size` is already present in the list response. Random access was impossible; reaching
record 432 required iterating through 432 records (up to 9 HTTP calls). Bidirectional
navigation was not supported.

The server supports random access via `?content=X&start=N` where `content` is constant
for a given query session. Both parameters are in the `links.self` URL of every response.

## Decision

Replace `Iterable<T>` as the return type of `list()` in all 18 clients with a new
`PagedResult<T>` class that:

1. **Exposes metadata** - `totalCount()`, `pageSize()` from the first page
2. **Supports random access** - `seek(n)` positions the next iterator by record offset;
   `seekFromPage(n)` (1-based) translates a page number to a record offset using the fixed
   server page size of 50; `fetchFrom(n)` fetches one page at any offset without changing
   iterator state
3. **Supports bidirectional iteration** - `listIterator()` returns a `ListIterator<T>` with
   `next()`, `previous()`, `nextIndex()`, `previousIndex()`; page boundary crossings cost
   one HTTP call each, in both directions
4. **Backward-compatible** - `PagedResult<T> implements Iterable<T>`, so all existing
   `for (T t : list(...))` loops continue to work

### PagedResult design

```java
PagedResult<Towar> result = client.towary().list(query);

// Metadata (triggers first page fetch):
int total   = result.totalCount();   // response.size
int perPage = result.pageSize();     // response.on_page (1-50)

// Forward iteration (backward-compatible):
for (Towar t : result) { ... }

// Random access by record offset with bidirectional iterator:
result.seek(432);
ListIterator<Towar> it = result.listIterator();
it.next();        // record 432
it.previous();    // record 432 again

// Random access by page number (1-based, server page size = 50):
result.seekFromPage(2);   // seek(50)
result.seekFromPage(9);   // seek(400)

// One-shot page fetch (does not move iterator position):
List<Towar> page = result.fetchFrom(1500);
```

### Internal design

`PagedResult<T>` hides the page response type `P` via a factory method `PagedResult.of(...)`.
Callers receive `PagedResult<T>` with no generic `P` visible. The factory takes:

- `Supplier<P> firstPage` - fetches the first page (existing `listPage()` call)
- `Function<String, P> urlFetch` - fetches any page by absolute URL (existing `fetchByLink()`)
- `Function<P, List<T>> dataExtractor` - extracts and maps items (Raw to SDK record)
- `Function<P, String> selfLinkExtractor` - extracts `links.self` URL
- `Function<P, Integer> sizeExtractor` - extracts `size`
- `Function<P, Integer> onPageExtractor` - extracts `on_page`

Internally, the first page response is captured as a `PageInfo<T>` record:
`(firstPageData, totalCount, pageSize, selfUrl)`. Metadata is fetched lazily - no HTTP
call until metadata or iterator is first accessed.

URL construction for random access: `selfUrl` contains `?content=X&start=0`. The `start`
parameter is replaced via regex to produce `?content=X&start=N`. The `content` session ID
is kept opaque - it is never exposed in the public API, only used internally by `buildUrl()`.

Page boundary crossing:
- Forward: fetch `?content=X&start=currentPageStart+currentPage.size()`
- Backward: fetch `?content=X&start=max(0, currentPageStart-pageSize)`

### Replacing PagedIterable + MappingIterable

`PagedResult` absorbs both `PagedIterable` (lazy forward pagination) and `MappingIterable`
(Raw-to-record mapping). Both classes remain in `sdk.paging` (they have their own tests and
retain conceptual value), but are no longer used by resource clients.

`extractNextLink()` was removed from all 18 clients - navigation now uses URL construction
from `links.self` rather than following `links.next`.

### Open questions (from design doc)

1. Does `?content=X&start=432` return record 432 as the first item, or the page containing
   record 432? Implementation assumes record 432 is at index 0 of the response. Needs live
   API verification.
2. Can `content` expire during a long session? If the server invalidates the session,
   subsequent `seek()` or `fetchFrom()` calls will receive an API error (wrapped as
   `NoviCloudException`). No special handling is implemented.

## Consequences

- `list()` on all 18 clients returns `PagedResult<T>` instead of `Iterable<T>`.
  Since `PagedResult<T> implements Iterable<T>`, this is source-compatible.
- `count()` is kept as a separate method; callers who need only the count without
  iterating can still use it without building a `PagedResult`.
- `PagedResult` is exported via `sdk.paging` (already in `module-info.java`).
- Thread safety: `PagedResult` is mutable (seek state, page cache) and single-thread only,
  same contract as the previous `PagedIterable`.
- `ListIterator.set()`, `add()`, `remove()` throw `UnsupportedOperationException` -
  the NoviCloud API is read-only for list operations.
