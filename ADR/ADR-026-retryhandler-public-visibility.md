# ADR-026: RetryHandler made public

**Date:** 2026-03-28
**Status:** Accepted

---

## Context

`RetryHandler` was package-private (`final class RetryHandler`) in
`io.github.mgrtomaszzurawski.novicloud.sdk`. All `*Sdk` classes (TowarySdk, AsortySdk, etc.)
were in the same package and could access it without imports.

After ADR-025 moved all `*Sdk` classes to subpackages (`sdk.towary`, `sdk.asorty`, etc.),
they can no longer access package-private members of the root `sdk` package.

---

## Decision

`RetryHandler` and its public-facing members made `public`:
- `public final class RetryHandler`
- `public RetryHandler(RetryPolicy policy)`
- `public interface ApiCall<T>`
- `public interface VoidApiCall`
- `public <T> T execute(ApiCall<T> call, String message)`
- `public <T> T executePost(ApiCall<T> call, String message)`
- `public void run(VoidApiCall call, String message)`

`RetryHandler` is exported as part of the `sdk` package (already exported in module-info.java),
so it is technically accessible to consumers of the SDK module.

---

## Consequences

### Positive
- `*Sdk` subpackages can use `RetryHandler` correctly
- No change to runtime behavior; no circular dependencies introduced

### Negative
- `RetryHandler` is now part of the public API surface of the module.
  External code can instantiate `RetryHandler` directly, which was not intended.
  However, `RetryHandler` without a `NoviCloudSdk` instance is useless to external callers.

### Why not move RetryHandler to a shared internal package?

Moving `RetryHandler` to e.g. `sdk.internal` and not exporting that package would be cleaner.
It was not done because:
1. JPMS `requires` cannot distinguish "exported for internal use" from "exported publicly"
2. Adding `sdk.internal` would require updating `module-info.java` and all 18 `*Sdk` imports
3. The practical risk of external misuse is low; `RetryHandler` is a low-level utility

If the SDK is ever published as a library, reconsider: move to unexported `sdk.internal`.

---

## Related

- ADR-019: exception hierarchy and RetryHandler design
- ADR-025: package-per-endpoint (root cause of this change)
