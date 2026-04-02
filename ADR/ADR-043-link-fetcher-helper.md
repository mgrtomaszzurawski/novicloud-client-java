# ADR-043: LinkFetcher helper for pagination link following

**Date:** 2026-03-30
**Status:** Accepted

## Context

All 18 `*Client` classes contained an identical `doFetchByLink(String link)` private method
(~20 lines each) that performs raw HTTP GET on a pagination `links.next` URL, applies auth
headers, timeout, and deserializes the response via Jackson. This was 360 lines of pure
copy-paste across the codebase.

An `AbstractResourceClient<T, P>` base class was considered but rejected: the only truly
shared logic was `doFetchByLink`. The remaining "shared" code (fields, constructor, count,
listAll wiring) would have required 3 abstract methods and a 5-parameter constructor just
to save a few lines per subclass. That is a premature abstraction for one method.

## Decision

Extract the shared HTTP logic into a static helper: `LinkFetcher.fetch(link, apiClient, responseType)`.

Each client's `doFetchByLink` becomes a one-liner:

```java
private ApiResponseTowaryList doFetchByLink(String link) throws ApiException {
    return LinkFetcher.fetch(link, apiClient, ApiResponseTowaryList.class);
}
```

The `fetchByLink` method (retry wrapper) and `extractNextLink` method remain in each client
unchanged, since they are already concise and type-specific.

## Consequences

- **Removed:** `objectMapper`, `requestInterceptor`, `readTimeout` fields from all 18 clients
  (LinkFetcher extracts these from `apiClient` directly)
- **Removed:** 6 constants per client (`HTTP_SUCCESS_CLASS`, `HTTP_STATUS_UNKNOWN`,
  `ACCEPT_HEADER`, `APPLICATION_JSON`, `ERR_READ_PAGE`, `ERR_INTERRUPTED`)
- **Removed:** 7 unused imports per client (IOException, InputStream, URI, HttpRequest,
  HttpResponse, Duration, Consumer, ObjectMapper, NoviCloudNetworkException)
- **Added:** `LinkFetcher` class in `io.github.mgrtomaszzurawski.novicloud.sdk.paging`
- **Net reduction:** ~15 lines per client x 18 = ~270 lines removed
- **No public API change** - `doFetchByLink` and `fetchByLink` are private
- **Error messages:** LinkFetcher uses generic messages ("Link call failed", "Failed to read page",
  "Request interrupted") instead of endpoint-specific ones. Acceptable because the retry wrapper
  `fetchByLink` still provides the endpoint-specific `ERR_LINK_CALL` message to the caller.
