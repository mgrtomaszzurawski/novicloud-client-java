# ADR-057: Codex re-review fixes (round 2 of 1.1.0)

**Date:** 2026-05-03
**Status:** Accepted (released as part of 2.0.0; see ADR-059 for why
1.1.0 was promoted to a major bump). References to "1.1.0" in the body
are historical and reflect the intended target at the time of writing.

## Context

After the initial 1.1.0 fix round (ADR-053..ADR-056), Codex was run again
against the working tree. The second pass confirmed the new nested records,
`requireDane`, conditional kartyloj rules, and disabled generated tests, but
flagged 10 additional findings in
`context/codex-findings-2026-05-03-0110.md`. Each was independently
verified against the actual code; 8 of 10 were real bugs the first review
had missed (the first review focused on nested records and null `dane`
handling).

This ADR documents the second-round fixes and the rationale.

## Decision

### High-severity behavioural fixes

#### F-01: POST retry default flipped to `false`

`RetryPolicy.Builder.retryPost` now defaults to `false`. The README's
prior justification - "NoviCloud API enforces uniqueness on required
fields" - is not actually documented in
`context/deprecated/dokumentacja RestApi Novicloud.txt`. Without an
idempotency key contract, a retry after a committed-but-lost POST can
create duplicate records. Opt-in semantics are now the default; the
setter Javadoc explains when retry is safe (resources with a
server-enforced unique key like `kartyloj.kod`).

`RetryPolicyTest.defaultPolicy_whenCreated_hasExpectedDefaults` updated
to expect `assertFalse(p.retryPost())`.

#### F-02: `IOException` / `InterruptedException` mapped to `NoviCloudNetworkException`

The generated `ApiException(Throwable)` constructor leaves `code = 0` for
transport failures from `IOException` / `InterruptedException` thrown by
`HttpClient.send`. `NoviCloudException.of(...)` previously fell through
to the base class, contradicting the README claim that I/O failures
surface as `NoviCloudNetworkException` (this only worked for
`LinkFetcher`-driven page-2+ calls).

Fix: in `NoviCloudException.of`, when `code == 0` and the cause is
`IOException` or `InterruptedException`, return
`NoviCloudNetworkException` carrying the original cause. Test:
`TowaryClientIntegrationTest.count_whenConnectionFails_throwsNetworkException`
uses WireMock's `Fault.CONNECTION_RESET_BY_PEER` to simulate a transport
failure on the first-page call.

#### F-03: Unknown enum values must not break deserialization

Generated `*Enum.fromValue(Integer)` factories throw
`IllegalArgumentException("Unexpected value '<n>'")` for codes outside
their enum, before the SDK domain mapping (`Towar.Typ.fromCode`) ever
runs. A new producer-side `typ` value would break the entire `Towar`
read.

Fix: in `NoviCloudClient.Builder.build`, configure the shared
`ObjectMapper` with `DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL`.
This works even for `@JsonCreator(Integer)`-driven enum factories,
verified by
`TowaryClientIntegrationTest.getById_whenServerReturnsUnknownEnumValue_returnsRecordWithNullEnum`
(uses `typ=99` and `przy_sprzedazy=42`).

### Medium-severity behavioural fixes

#### F-04: HTTP 402 mapped to new `NoviCloudAccessException`

The producer documentation defines 402 as "odmowa dostepu z powodu nie
wykupionej opcji REST API NoviCloud" - the REST API option is not
subscribed/disabled/suspended/not ordered. Distinct from 401/403
credential failures. Previously fell through to the base
`NoviCloudException`, forcing callers to inspect status codes.

Fix: new `NoviCloudAccessException` (subclass of `NoviCloudException`)
returned by `NoviCloudException.of` for code 402. README exception
hierarchy and main-class Javadoc updated.

#### F-05: `LinkFetcher` preserves error response body

Page-1 errors went through generated code that captured the response
body in `ApiException.getResponseBody()`. Page-2+ errors went through
`LinkFetcher.fetch`, which threw
`new ApiException(status, message, headers, null)` - body discarded.
Diagnostic information (e.g. `par_niewlasciwe`) was lost.

Fix: `LinkFetcher` now reads the error response into a UTF-8 string and
passes it to `ApiException`, matching first-page behaviour.

### Low-severity behavioural fix

#### F-09: `Retry-After` is a hard minimum, not a jitter base

`RetryHandler.sleep(...)` previously computed
`nextLong(baseMillis / 2, baseMillis + 1)` for any base, including a
server-supplied `Retry-After` value. With `Retry-After: 60`, the SDK
might sleep 30 seconds and retry early, violating the HTTP contract and
risking repeat 429s.

Fix: branch in `sleep`. When `preferredSeconds > 0` (Retry-After
honoured), sleep exactly `preferredSeconds * 1000` ms with no jitter.
Backoff jitter (`[base/2, base+1]`) remains for the exponential and
fixed strategies.

### Trivial fix

#### F-10: SCM `<tag>` updated to `v1.1.0`

Root pom had a stale `<tag>v1.0.0</tag>` despite version `1.1.0`.

### Medium-severity structural fix added in the same round

#### F-06: Physically hide LinkFetcher and `from(XRaw)` factories

After the user's "fix everything, not just what I cherry-picked" feedback
the round expanded:

- **`LinkFetcher`** moved from `sdk.paging` (exported) to
  `sdk.internal.paging` (not exported). External callers - even on the
  classpath - now have to reach into a clearly internal package to use
  it. All 18 resource clients updated their import.
- **All 25 record `from(XRaw)` factories deleted.** Conversion logic
  moved to a single `sdk.internal.mapper.RawMappers` class (not exported
  under JPMS). Records are now pure data containers with no generated-type
  imports. 18 resource clients call `RawMappers.toFoo(raw)` instead of
  `Foo.from(raw)`.
- **`*Client` public constructors stay `@Deprecated(forRemoval=true)`.**
  Making them truly package-private requires moving implementations behind
  interfaces, which is a 2.0.0 breaking change documented in ADR-056.
  Codex correctly notes that JPMS protects only module-path users; the
  classpath leak remains until 2.0.0.

#### F-07: typed access to 400 validation details

Added `sdk.exception.NoviCloudErrorDetails` record holding `parNiewlasciwe`
and `parBlednaWart` lists, plus
`NoviCloudException.getErrorDetails(): Optional<NoviCloudErrorDetails>`
that lazily parses the response body envelope. 5 unit tests cover the
happy path, missing body, non-JSON body, dane-without-error-fields, and
the lookup contract.

#### F-08: consistent OpenAPI error responses

All 17 path files now declare `'410'` and `'501'` on every operation,
matching `towary.yaml` (the only path that was already complete in 1.0.0).
Spec polish, no runtime impact.

#### F-09: release flag retried after SpotBugs upgrade

After upgrading `spotbugs-maven-plugin` to `4.9.6.0` (round 1) the ASM
that supports JDK 25 became available. Re-attempted
`<maven.compiler.release>17</maven.compiler.release>` and confirmed
SpotBugs analysis runs clean. Both `novicloud-client/pom.xml` and
`demo-app/pom.xml` are now release-flagged.

## Consequences

- 1.1.0 ships with 5 behavioural improvements over 1.0.0 in
  cross-cutting SDK guarantees (POST safety, network exception
  contract, forward enum compatibility, 402 access exception,
  pagination diagnostics).
- One additional new exception class (`NoviCloudAccessException`,
  `@since 1.1.0`).
- `Retry-After` now sleeps slightly longer than before (no negative
  jitter) - desirable for rate-limited callers.
- Default POST retry change is **observable behaviour change** but
  considered safety-positive: prior behaviour could produce duplicates
  silently; new behaviour surfaces server failures the user can decide
  about. Documented prominently in CHANGELOG.

## References

- `context/codex-findings-2026-05-03-0110.md`
- `context/codex-confidence-report-2026-05-03-0110.md`
- ADR-053 / ADR-054 / ADR-055 / ADR-056 (round 1 of 1.1.0)
