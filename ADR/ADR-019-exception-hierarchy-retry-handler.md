# ADR-019: Exception hierarchy and RetryHandler with exponential backoff

**Date:** 2026-03-20
**Status:** Accepted (retry behavior extended by ADR-024)

---

## Context

The SDK previously used a single `NoviCloudSdkException` class for every error type
(401 auth failures, 404 not found, 429 rate limit, 5xx server errors, network errors).
Callers had to inspect `getStatusCode()` to distinguish error types — not type-safe.

Additionally:
- `NoviCloudSdkFactory.create()` set no timeouts, leaving SDK hanging on stalled connections.
- `PagedIterable` could NPE if `dataExtractor.apply(page)` returned null.
- 429 responses caused immediate failure with no retry, making large paginated scans fragile.

---

## Decision

### 1. Exception hierarchy

Added 5 typed subclasses, all extending `NoviCloudSdkException`:

| Class | HTTP codes | Extra field |
|-------|------------|-------------|
| `NoviCloudAuthException` | 401, 403 | — |
| `NoviCloudNotFoundException` | 404, 410 | — |
| `NoviCloudRateLimitException` | 429 | `retryAfterSeconds` |
| `NoviCloudServerException` | 5xx | — |
| `NoviCloudNetworkException` | 0 (IOException/timeout) | — |

Static factory `NoviCloudSdkException.of(String, ApiException)` maps HTTP status to subclass.
`NoviCloudRateLimitException` reads the `Retry-After` header via `parseRetryAfter(HttpHeaders)`.

### 2. RetryHandler (package-private)

`RetryHandler` encapsulates the retry loop in one place. All 18 `*Sdk` classes use it.
Key design choices:
- `MAX_RETRIES = 3` (configurable by changing the constant)
- Retries only on `NoviCloudRateLimitException` — other exceptions thrown immediately
- Backoff: respects `Retry-After` header value; falls back to exponential `2^attempt` seconds
- `ApiCall<T>` functional interface for return-value calls; `VoidApiCall` for void calls
- `run()` delegates to `execute()` via a `Void`-typed lambda

### 3. fetchByLink split

Each `*Sdk.fetchByLink()` was split into:
- `doFetchByLink(String link) throws ApiException` — raw HTTP call, IOException/InterruptedException
  become `NoviCloudNetworkException` (bypasses RetryHandler catch for non-rate-limit errors)
- `fetchByLink(String link)` — delegates to `RetryHandler.execute(() -> doFetchByLink(link), ...)`

### 4. NoviCloudSdkFactory builder

`NoviCloudSdkFactory.builder()` added with default timeouts:
- `connectTimeout = 5s` (prevents hang on TCP connect)
- `readTimeout = 30s` (prevents hang on slow responses)

Existing `create()` methods kept for backward compatibility, now delegate to builder.

### 5. PagedIterable null guard

```java
current = Objects.requireNonNullElseGet(dataExtractor.apply(page), List::of);
```
Applied in both `ensureInitialized()` and `loadNextPage()`.

---

## Consequences

**Positive:**
- Callers can `catch (NoviCloudRateLimitException e)` or `catch (NoviCloudAuthException e)` — type-safe
- Large paginated scans (e.g. 380k sales records) survive transient 429 rate limits automatically
- Network stalls bounded by default 30s read timeout
- Single responsibility: retry logic in one class, not repeated in 18 files
- `NoviCloudNetworkException` for IOException distinguishes transport errors from API errors

**Negative / risks:**
- `MAX_RETRIES = 3` is a hardcoded constant (not per-call configurable) — acceptable for now
- Retry sleep uses `Thread.sleep()` — blocks calling thread; async SDK out of scope
- 5xx server errors are NOT retried (intentional: server errors should surface immediately)

---

## Alternatives considered

**Resilience4j** — adds a dependency, more configuration, overkill for a single retry policy.
**Recursive retry** — harder to read, stack depth risk on 3+ retries.
**Retry on all exceptions** — rejected: only 429 warrants retry; auth/not-found errors are definitive.
