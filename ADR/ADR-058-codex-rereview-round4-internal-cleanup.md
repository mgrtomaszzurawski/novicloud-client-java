# ADR-058: Codex round-4 fixes - hide remaining internal types and align POST retry

**Date:** 2026-05-03
**Status:** Accepted (released as part of 2.0.0; see ADR-059 for why
1.1.0 was promoted to a major bump). References to "1.1.0" in the body
are historical.

## Context

After ADR-057 closed Codex's first re-review pass, Codex was run again
(`context/codex-findings-2026-05-03-1135.md`,
`context/codex-confidence-report-2026-05-03-1135.md`). It confirmed every
behavioural fix from rounds 1-3 and flagged four medium-or-lower issues
that remained:

- **CF-01:** `RetryHandler`, `LinkUtils`, and
  `NoviCloudException.of(String, ApiException)` still leak generated
  internal types into the exported public API even after the records,
  `LinkFetcher`, and `from(XRaw)` factories were hidden.
- **CF-02:** `RetryHandler` was retrying POST 429 even with
  `retryPost(false)`. The README's "POST create operations are not retried
  by default" contract was therefore inconsistent for the rate-limit class.
- **CF-03:** README, CLAUDE.md, CHANGELOG, and `sdk.paging` package-info
  still described the previous architecture (548 tests, public
  `LinkFetcher`, "create record with `from(XRaw)` factory" recipe).
- **CF-04:** The `LinkFetcher` body-preservation fix from F-05 was
  implemented and behaviourally correct, but no dedicated regression test
  locked it in for the page-2+ path.

The user also rejected an earlier decision to mark 14 demo-app SonarQube
S2629 (logger guarding) findings as "old debt accepted in ADR-048" -
"wszystkie rzeczy z kazdej analizy czyscimy zawsze". Tracked here as
SQ-1.

## Decision

### CF-01 - hide remaining internal types

#### `RetryHandler` -> `sdk.internal.RetryHandler`

The class moves from the exported `sdk` package to the non-exported
`sdk.internal` package. Its public functional interfaces
(`ApiCall<T>`, `VoidApiCall`) keep their `throws ApiException` signature,
but they are no longer reachable from JPMS module-path consumers because
the package is not exported. All 18 resource clients update their import.
The single test (`RetryHandlerTest`) gains an explicit import.

#### `LinkUtils` -> `sdk.internal.mapper.LinkUtils`

The class moves from the exported `sdk.model` package to the
non-exported `sdk.internal.mapper` package, joining `RawMappers`. The
single caller (`RawMappers`) is in the same package and no longer needs
an import.

#### `NoviCloudException.of(...)` neutral signature

The public static method previously took
`(String message, ApiException e)` and used `e.getCode()`,
`e.getResponseHeaders()`, `e.getResponseBody()`, and `e.getCause()`
internally. The new signature is
`(String message, Throwable cause, int statusCode, HttpHeaders headers, String responseBody)`
- four neutral parameters, no `ApiException` reference.

The internal mapper in `RetryHandler` (`mapToSdk(message, ApiException)`)
unwraps the generated exception's cause and forwards the decomposed
parameters. External callers who somehow had access to the old method
signature must adapt; the previous behaviour is preserved when called
through the new entry point.

The `NoviCloudException.java` file no longer imports
`io.github.mgrtomaszzurawski.novicloud.client.ApiException`, completing
the removal of the generated type from the exported `sdk.exception`
package.

### CF-02 - POST retry contract aligned for 429

`RetryHandler.shouldRetry5xx(isPost)` was renamed to
`shouldRetryPostHazard(isPost)` and is now used to gate **both** the 429
branch and the 5xx branch in the retry loop. `RetryPolicy.retryPost()`
documentation now states explicitly that the flag covers 429 and 5xx for
POST. The README contract ("POST create operations are not retried by
default") is true for both transient failure classes.

The previous test `executePost_whenRetryPostDisabledAnd429_stillRetries`
(which asserted the now-incorrect "still retries" behaviour) was
rewritten to `_doesNotRetry`, and a complementary
`executePost_whenRetryPostEnabledAnd429_retriesUntilLimit` was added to
lock in the opt-in path.

### CF-03 - documentation drift

- README test count `548` -> `~589`. The test breakdown table updated
  to reflect the new layers (record/mapper, error-details, network and
  402 integration tests).
- README `sdk.paging` description now notes that `LinkFetcher` moved
  to `sdk.internal.paging` in 1.1.0.
- CLAUDE.md "Adding a new endpoint" recipe rewritten: pure record (no
  `from(XRaw)`) plus a `RawMappers.to<Record>` method in
  `sdk/internal/mapper`.
- CLAUDE.md exception hierarchy section adds 402 ->
  `NoviCloudAccessException`, the empty-`dane` -> `NotFoundException`
  conversion, and the new `getErrorDetails()` accessor.
- CHANGELOG `[1.1.0]` "Notes" section pruned of obsolete
  "deferred to 2.0.0" lines and updated to describe what actually
  shipped.
- `sdk/paging/package-info.java` lists only `PagedResult` and explicitly
  notes `LinkFetcher` is now internal.

### CF-04 - pagination error body regression test

`TowaryClientIntegrationTest.list_whenSecondPageReturnsError_preservesErrorBody`
sets up a two-page WireMock scenario where page 2 returns HTTP 400 with
a NoviCloud error envelope. The test asserts:

1. `getResponseBody()` is non-null on the thrown exception.
2. The body contains `par_niewlasciwe`.
3. `getErrorDetails()` returns a populated `NoviCloudErrorDetails`.

This locks the fix from F-05 (round 1) for the link-followed page path.

### SQ-1 - 14 SonarQube S2629 cleared in demo-app

In each affected `*Runner.java` (Asorty, FormyPlatn, Jmiary, KartyLoj,
Kontrahenci, Kraje, Sklepy, StawkiVat, Towary, Waluty), the record
accessor calls inside `LOG.info(...)` arguments were hoisted to local
variables before the log call. Reuse of those variables in subsequent
`verifyField(...)` calls also makes the code marginally less repetitive.

No `@SuppressWarnings("java:S2629")` was added; the rule is now
satisfied by construction.

## Rejected alternatives

- **Keep `NoviCloudException.of(String, ApiException)` as a
  `@Deprecated` overload.** Rejected: the only caller is `RetryHandler`
  (now internal) plus tests. Adding a deprecated bridge would keep the
  generated type in the public Javadoc forever to spare exactly zero
  external callers.
- **`@SuppressWarnings("java:S2629")` on each `*Runner.java`.** Rejected
  because the user's directive is "wszystkie rzeczy z kazdej analizy
  czyscimy zawsze". Suppressing a rule per file is not the same as
  cleaning the warning - and the actual fix (local variables) is
  one-line per call site.
- **Move resource client constructors to package-private now.** Still
  rejected. Java has no module-friend access modifier; making the
  constructors truly package-private requires the interface
  introduction documented in ADR-056 and is a 2.0.0 breaking change.

## Consequences

- The exported public surface no longer references generated types.
  External callers can stay within `NoviCloudClient`,
  `RetryPolicy`, `PagedResult`, `sdk.model.*` records,
  `sdk.exception.*`, and the resource clients.
- POST retry semantics now match the README and Javadoc claims.
  Behavioural change for `retryPost(false)` + 429 (previously: retried;
  now: not retried). Documented in CHANGELOG.
- Documentation no longer contradicts the source.
- One additional integration test (588 -> 589 in the project total).
- 14 SonarQube smells gone. Remaining smells in the project: 19 INFO
  S1133 deprecation reminders that are intentional and tied to ADR-056's
  2.0.0 plan.

## References

- `context/codex-findings-2026-05-03-1135.md`
- `context/codex-confidence-report-2026-05-03-1135.md`
- ADR-056 (deprecate internal-leak surface), ADR-057 (round-3 fixes)
