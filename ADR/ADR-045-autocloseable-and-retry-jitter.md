# ADR-045: AutoCloseable client and retry jitter

**Date:** 2026-03-31
**Status:** Accepted

## Context

Architecture audit (AUDIT-2026-03-31) identified two gaps vs AWS/GCP/Azure SDK standards:

1. **F-18:** `NoviCloudClient` did not implement `AutoCloseable`. Gold-standard SDKs
   (AWS `SdkClient`, GCP `ServiceClient`) all support `try-with-resources` for resource
   cleanup. However, the SDK targets JDK 17, where `java.net.http.HttpClient` does not
   expose a `close()` method (added in JDK 21).

2. **F-24:** Exponential backoff in `RetryHandler` had no jitter. Plain `1s << attempt`
   creates synchronized retry waves when multiple clients hit the same 429/5xx at the
   same time (thundering herd problem). AWS and Azure mandate full jitter.

## Decision

### AutoCloseable

`NoviCloudClient` implements `AutoCloseable`. The `close()` method sets a `volatile boolean closed`
flag. All 18 resource accessor methods (`towary()`, `asorty()`, etc.) call `ensureOpen()` which
throws `IllegalStateException` if the client has been closed.

On JDK 17 no I/O resources are actually released - the flag is the only effect. When the SDK
upgrades to JDK 21+, `close()` will delegate to `HttpClient.close()` for real connection pool
cleanup. The `AutoCloseable` contract is correct now and will gain substance later.

### Retry jitter

The `sleep()` method in `RetryHandler` now applies equal jitter: the actual sleep duration is
a random value in `[baseMillis/2, baseMillis]`. This preserves the average backoff duration
while eliminating synchronized retry storms across concurrent clients.

`ThreadLocalRandom` is used (no contention, no seed management).

## Consequences

- `NoviCloudClient` works with `try-with-resources`
- Calling any accessor after `close()` throws `IllegalStateException`
- `close()` is idempotent (multiple calls are safe)
- Retry timing is no longer deterministic (acceptable - retries are inherently timing-sensitive)
- No new dependencies added
