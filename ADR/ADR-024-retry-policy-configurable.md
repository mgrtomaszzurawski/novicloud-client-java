# ADR-024: Configurable RetryPolicy for 5xx and 429

**Date:** 2026-03-28
**Status:** Accepted
**Extends:** ADR-019 (exception hierarchy and RetryHandler)

---

## Context

ADR-019 introduced `RetryHandler` with a fixed `MAX_RETRIES = 3` constant that retried only on
HTTP 429 (rate limit). All other errors, including 5xx server errors, were surfaced immediately.

Live API testing revealed that the NoviCloud server frequently returns transient HTTP 500 errors
caused by internal JDBC connectivity issues. These are not permanent domain errors - they succeed
on a second attempt. With the current behavior, callers must implement their own retry or accept
spurious failures on otherwise correct operations.

Additionally, users running bulk imports or long automation sessions need to tune retry behavior:
number of attempts, backoff, whether to retry POST operations.

---

## Decision

Introduce a public immutable `RetryPolicy` class with a builder. The policy is attached at SDK
construction time via `NoviCloudSdkFactory.Builder.retryPolicy(RetryPolicy)`.

`RetryHandler` is refactored from a purely-static utility to an instance-based class that holds
a `RetryPolicy`. Each `*Sdk` class creates and holds its own `RetryHandler` instance.

### RetryPolicy knobs and defaults

| Knob | Type | Default | Meaning |
|------|------|---------|---------|
| `enabled` | boolean | `true` | Master switch; `false` = no retries at all |
| `retryOn5xx` | boolean | `true` | Retry on HTTP 500-599 (transient server errors) |
| `maxAttempts` | int | `3` | Total attempts including the first call |
| `backoffStrategy` | enum | `EXPONENTIAL` | `EXPONENTIAL` (1s/2s/4s) or `FIXED` (1s) |
| `retryOn429` | boolean | `true` | Retry on HTTP 429 (preserves ADR-019 behavior) |
| `maxRetryAfterSeconds` | long | `60` | Cap on Retry-After header value; prevents stalling on malformed headers |
| `retryPost` | boolean | `true` | Whether POST operations are retried on 5xx |

### Default policy rationale

- **5xx ON by default:** the live NoviCloud API returns transient JDBC 500s that are not business
  errors. Retrying is the correct behavior for integrators who cannot distinguish transient from
  permanent 500s at the call site.
- **POST retryable by default:** the NoviCloud API enforces uniqueness on required fields. If a
  POST 5xx occurs before the record is committed, a retry creates the record correctly. If the
  record was committed but the response was lost (network cut), a retry will receive a conflict
  error (400/409, not 5xx), so no duplicate is created. Users who need stricter behavior can set
  `retryPost(false)`.
- **PUT / DELETE always retryable:** PUT is idempotent by HTTP semantics; DELETE is idempotent
  (deleting an already-deleted resource returns 404, not 5xx).

### 503 Retry-After

The Retry-After header on 503 responses is not parsed in this release. The `maxRetryAfterSeconds`
cap applies only to 429 responses. Honoring Retry-After on 503 is deferred (known gap).

### RetryHandler becomes instance-based

`RetryHandler` is now package-private and instance-based. Each `*Sdk` receives a `RetryPolicy`
in its constructor and builds a `RetryHandler` from it. `RetryHandler.execute` and `.run` are
now non-static instance methods. `RetryHandler.executePost` is added for POST operations (checks
`retryPost` policy knob). The functional interfaces `ApiCall<T>` and `VoidApiCall` remain unchanged.

---

## Consequences

**Positive:**
- Transient 5xx JDBC errors are automatically retried without caller code changes.
- Integrators can disable retries, reduce attempts, or switch to fixed backoff for latency-sensitive scenarios.
- `retryPost(false)` lets callers opt out of POST retries without disabling all retry behavior.
- `maxRetryAfterSeconds` prevents indefinite stalling on a bad `Retry-After: 9999` header.

**Negative / risks:**
- Every `*Sdk` constructor now requires a `RetryPolicy` parameter. `NoviCloudSdk` and
  `NoviCloudSdkFactory` are updated accordingly. Callers who construct `NoviCloudSdk` directly
  (not via factory) must also pass a policy; `RetryPolicy.defaultPolicy()` is provided for this.
- `RetryHandler` is no longer a pure static utility. The change is internal (package-private);
  no public API is broken.
- POST retries remain theoretically risky if the API's uniqueness guarantees are relaxed in a
  future server version. The `retryPost` knob allows disabling this behavior.

---

## Alternatives considered

**Keep static RetryHandler, pass policy per-call:** would require adding a `RetryPolicy` parameter
to every `execute` / `run` call site across 18 `*Sdk` classes without encapsulating the policy
in one place. Noisier call sites.

**Thread-local policy:** not thread-safe by design; rejected.

**Resilience4j / retry library:** adds a compile-time dependency. Overkill for a bounded retry
loop. Rejected per ADR-019.
