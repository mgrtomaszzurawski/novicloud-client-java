# ADR-027: Zero magic strings and numbers in SDK source

**Date:** 2026-03-28
**Status:** Accepted

---

## Context

After introducing the `ENDPOINT` constant pattern and typed log helpers in demo-app runners
(ADR-D008), a full audit revealed the same problem in the SDK core library:

- 18 `*Sdk.java` classes each contained 8-12 raw string literals as error messages passed
  to `RetryHandler.execute/run/executePost`, `NoviCloudNetworkException`, and
  `Objects.requireNonNull`
- `"Accept"` and `"application/json"` repeated verbatim in every `doFetchByLink` method
- `0` as a literal HTTP status code in `NoviCloudNetworkException` network error paths
- `0` as a fallback return value in `count()` methods
- `" must not be null"` as a literal suffix in `requireNotNull` helpers
- `RetryHandler` and `PagedIterable` contained magic numeric literals for backoff durations
  and index initialization

---

## Decision

### Sdk classes: named constants for all string and numeric literals

Every `*Sdk.java` class declares a block of `private static final` constants immediately
after `HTTP_SUCCESS_CLASS`:

```java
private static final int HTTP_SUCCESS_CLASS = 2;
private static final int HTTP_STATUS_UNKNOWN = 0;   // status code for network (non-HTTP) errors
private static final int EMPTY_COUNT = 0;            // fallback for count() when data is null
private static final String ACCEPT_HEADER = "Accept";
private static final String APPLICATION_JSON = "application/json";
private static final String ERR_LIST_PAGE     = "Failed to list X page";
private static final String ERR_GET_BY_ID     = "Failed to fetch X by id";
private static final String ERR_CREATE        = "Failed to create X";
private static final String ERR_UPDATE        = "Failed to update X";
private static final String ERR_DELETE        = "Failed to delete X by id";
private static final String ERR_LINK_CALL     = "X link call failed";
private static final String ERR_READ_PAGE     = "Failed to read X page";
private static final String ERR_INTERRUPTED   = "X request interrupted";
private static final String ERR_DRAFT_NULL    = "draft must not be null";
private static final String ERR_NULL_SUFFIX   = " must not be null";
private static final String FIELD_ID          = "id";
```

Read-only Sdk classes (no create/update/delete) omit the inapplicable constants.

### RetryHandler: named constants for backoff values

```java
private static final int MIN_ATTEMPTS = 1;
private static final long NO_PREFERRED_WAIT = 0L;
private static final long BASE_BACKOFF_SECONDS = 1L;
```

### PagedIterable: named constant for index initialization

```java
private static final int INITIAL_INDEX = 0;
```

Applied to both `ensureInitialized` and `loadNextPage`.

### Log format strings in dedicated helper methods: exempt

Log format strings inside `RunnerHelper` static helpers are exempt. A format string that
is the sole content of a dedicated method is already isolated at a single definition site.
Extracting it to an additional constant would reference it exactly once - immediately above
the method that uses it - adding indirection with no maintenance benefit.

This exemption is scoped to: format strings that are the only string literal inside a
dedicated log helper method. It does not extend to format strings in business logic,
`Sdk` classes, or inline `LOG.info` calls outside helpers.

---

## Consequences

### Positive

- Error message text changes require editing one constant per class, not searching call sites
- `"Accept"` / `"application/json"` header names appear once per class, eliminating
  the risk of typos across repeated occurrences
- `HTTP_STATUS_UNKNOWN = 0` documents intent: this is a network error with no HTTP status,
  not a successful HTTP 0 response
- Consistent naming convention (`ERR_*`, `FIELD_*`) allows IDE search across all Sdk classes
- Static analysis tools (SpotBugs, PMD) report 0 violations with this pattern

### Negative

- Each Sdk class now has a larger constant block (~14 entries for full CRUD classes).
  Accepted: the constants are grouped, alphabetically consistent, and self-documenting.
- `ERR_NULL_SUFFIX = " must not be null"` is a string fragment (not a full message).
  It is combined with a `fieldName` parameter at runtime. The suffix is fixed across all
  null guards, so one constant is correct.

---

## Alternatives considered

**Shared base class or interface with common constants**
Rejected: the Sdk classes are `final` and deliberately not coupled to each other.
A shared `SdkConstants` class would require an extra import and obscure which constants
a given class actually uses.

**Inheriting constants from a generated class**
Rejected: generated sources are not edited manually (ADR-005).

---

## Related

- ADR-D008: No magic strings in runners (demo-app equivalent)
- ADR-019: Exception hierarchy and RetryHandler (establishes the error path where constants apply)
- ADR-024: Configurable RetryPolicy (establishes RetryHandler constants context)
