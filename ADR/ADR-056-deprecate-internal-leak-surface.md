# ADR-056: Deprecate internal-leak public API surface

**Date:** 2026-05-02
**Status:** Superseded by ADR-059. The 1.1.0 deprecation step described
here was collapsed into the 2.0.0 release: instead of `@Deprecated(since
= "1.1.0", forRemoval = true)` on `*Client` constructors, 2.0.0 turns
`*Client` into interfaces with implementations hidden in
`sdk.internal.resources.*`. References to "1.1.0" below are historical.

## Context

External review (Codex 2026-05-02, findings F-02, F-03, F-10) flagged three
classes of public API that exposed generated internals:

1. **F-02:** Each of 18 `*Client` resource classes (e.g. `TowaryClient`)
   has a `public` constructor `(ApiClient, String, RetryPolicy)`. The
   `ApiClient` parameter type is in the `client` package, which is *not*
   exported by `module-info.java`. JPMS module-path consumers cannot
   reach it, but classpath consumers can. Since most Maven Central
   consumers use the classpath, this is a real leak of the generated
   layer and lets callers bypass the SDK facade (auth setup, ObjectMapper
   configuration, AutoCloseable guard).

2. **F-03:** `LinkFetcher.fetch(String, ApiClient, Class<P>)` lives in the
   exported `sdk.paging` package, but it accepts `ApiClient` and a generated
   `*RawList` response class. It is consumed only by the SDK's own 18
   resource clients. No external user can call it sensibly on the module
   path.

3. **F-10:** Each public record (`Towar.from(TowarRaw)`, etc.) carries a
   `public static` factory taking the generated `XRaw` parameter. Same
   story: `XRaw` is not exported, but the factory is.

The proper fix - moving these into a non-exported `sdk.internal` package -
is a binary-breaking change. We deferred the breaking move to 2.0.0 and
chose deprecation in 1.1.0.

## Decision

### F-02: deprecate 18 `*Client` constructors

```java
@Deprecated(since = "1.1.0", forRemoval = true)
public TowaryClient(ApiClient apiClient, String accountName, RetryPolicy retryPolicy) { ... }
```

Each constructor gets a Javadoc note marking it `<strong>Internal API.</strong>`
and pointing to the matching `NoviCloudClient.<resource>()` accessor.

The single internal call site is `NoviCloudClient` (private constructor),
which receives `@SuppressWarnings("removal")` to silence the otherwise
self-inflicted warning storm.

### F-03: document LinkFetcher as internal, no annotation

Adding `@Deprecated(forRemoval=true)` on the class would generate 18
"deprecated for removal" warnings (one per resource client) without
moving anything. We instead documented the class as "Internal API" in
its Javadoc with an explicit "will move to a non-exported package in
2.0.0" note. The deprecation tag is reserved for *external* surface
that we want callers to stop touching; LinkFetcher has no external
callers (the `ApiClient` parameter is unreachable from the module path).

### F-10: document `from(XRaw)` factories as internal, no annotation

Same reasoning as LinkFetcher: 18 record factories, called only by 18
resource clients. Javadoc note added to each (covered by the existing
Javadoc edits in `Towar.java`; rest follow the same template in 2.0.0
when the move actually happens).

## Plan for 2.0.0

- Introduce `public interface TowaryClient` (and 17 siblings) in the
  exported `sdk.resources.<name>` package; the current concrete classes
  become package-private implementations under `sdk.internal.resources`.
  `NoviCloudClient.towary()` returns the interface.
- Move `LinkFetcher` to `sdk.internal.paging`; remove its export.
- Make record `from(XRaw)` factories package-private; SDK clients
  continue to use them via package-internal mappers.
- Remove the `@Deprecated` constructors entirely.

A 2.0.0 release will be a major bump precisely because of this move. The
1.1.0 deprecation gives any 1.0.0 user who reached for these constructors
a release cycle's notice.

## Alternatives considered

- **Skip deprecation, leave Javadoc-only:** rejected for F-02. Constructors
  are the primary leak vector (callers see them in IDE autocomplete). The
  `@Deprecated(forRemoval=true)` annotation makes IDEs strike through them.
- **Remove now in 1.1.0:** rejected. Binary-breaking change; would force
  major bump on a release that is otherwise additive.
- **Sealed-class approach (`sealed permits ...`):** rejected. Requires
  the implementation to live in the same module *and* either the same
  package or a named subclass. Either way it doesn't help when the goal
  is hiding the constructor from classpath consumers; sealed classes
  still expose the constructor.

## Consequences

- 1.1.0 is fully backwards-compatible (annotations are not breaking).
- IDEs warn external callers who instantiated resource clients directly.
- The 2.0.0 path is documented; no new questions to answer when we get
  there beyond "should the interfaces have method bodies for default
  conveniences" (likely no).

## References

- Codex findings F-02, F-03, F-10 (context/codex-findings.md)
- ADR-047 (acceptance of public *Client constructors in 1.0.0; reversed here)
- module-info.java exports (current state)
