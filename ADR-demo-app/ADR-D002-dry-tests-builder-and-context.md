# ADR-D002: Dry tests cover builders and RunnerContext — not runner execution

## Status

Accepted (RunnerContext tests superseded by ADR-D005; builder tests remain current)

## Context

The user requested the ability to test the SDK "dry" (without a live API connection). The challenge is that `EndpointRunner.run()` calls real SDK methods which make HTTP requests. Without a mock framework (Mockito is not in scope — no decision to add heavy test dependencies), runner execution cannot be tested without credentials.

Two realistic scopes for dry testing were identified:

1. **Query and Draft builders** — pure Java, no HTTP, fully verifiable without any external dependency
2. **RunnerContext behavior** — the `check()`/`require()`/`skip()` error-handling logic can be tested by passing lambdas that throw known exceptions, without invoking the SDK at all (SDK field is `null` in tests)

Runner execution itself (`EndpointRunner.run()`) is an integration concern and is not covered by dry tests.

## Decision

`src/test/java` contains two test packages:

### `demo.runner` — context and result tests

- `RunResultTest` — verifies OK/FAIL/SKIP factory methods and `toString()` format
- `RunReportTest` — verifies count methods, `hasFailures()`, immutable `results()` copy
- `RunnerContextTest` — verifies that `check()` records OK on success, records FAIL on `NoviCloudSdkException` and on generic `Exception`, never propagates; that `require()` returns `Optional.empty()` on failure; that `skip()` records SKIP with correct label

In `RunnerContextTest`, the `RunnerContext` is created with `sdk = null`. Tests only pass lambdas that do not call the SDK, or that throw directly — so no HTTP connection is needed.

### `demo.builder` — Query and Draft builder tests

One test class per Query/Draft class. Each test:
- verifies that `builder().build()` produces all-null defaults
- verifies that all fields round-trip correctly through the builder
- where relevant, documents architectural decisions (e.g. `StanMagDraftBuilderTest` asserts `towarId` is `String`, documenting ADR-011; `KartaLojDraftBuilderTest` asserts date fields are `String`, documenting ADR-014)

## Why not Mockito

Adding Mockito would enable testing runner execution without real API calls. This was not chosen because:
- the project has no dependency management policy requiring Mockito
- builder and context tests already cover the majority of non-HTTP logic
- runner classes are thin — their logic is the sequence of `ctx.check()` calls, which are individually tested through `RunnerContextTest`

If Mockito is added in the future, runner tests should be written to verify that each `ctx.check()` call uses the correct SDK method and query parameters.

## Consequences

- `mvn test` runs cleanly without credentials
- Builder tests catch regressions in field naming or default values
- Context tests catch regressions in error-handling logic (exceptions swallowed correctly, FAIL recorded)
- Runner integration tests run only via `mvn exec:java` or IDE main() with valid credentials in `application.properties`
