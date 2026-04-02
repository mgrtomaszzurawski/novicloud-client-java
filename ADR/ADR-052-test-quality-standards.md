# ADR-052: Test quality standards

**Date:** 2026-04-02
**Status:** Accepted

## Context

Manual code review (Phase 4l) revealed inconsistencies across ~90 test files:
- Test method names followed no consistent convention (mix of `method_condition_result`,
  `method_result`, `exceptionCode_throws`, and ad-hoc names)
- Test bodies had no structural convention (assertions mixed with setup)
- Magic strings and numbers were widespread (bare `"test-account"`, `500`, `0`, `"Server Error"`,
  WireMock URL patterns, index literals)
- Same values repeated across 18+ files without shared constants

## Decision

### Naming convention

All test methods follow: `methodUnderTest_whenScenario_expectedResult`

Examples:
- `getById_whenIdIsNull_throwsIllegalArgument`
- `count_whenServerOmitsSizeField_returnsListLength`
- `count_whenServerReturns429_throwsRateLimitException`
- `list_whenMultiplePages_iteratesAllPages`

When the test class covers a single builder type (e.g. `AsortyCreateBuilderTest`), the method
prefix is `build_when...`. When a test class covers multiple builder types (e.g. `BuilderEdgeCaseTest`),
the prefix identifies the builder: `towarBuilder_when...`, `rapPracyQuery_when...`.

### Body structure

Every test method uses `// given`, `// when`, `// then` comments (or `// when / then`
for `assertThrows`/`assertDoesNotThrow` that combine action and assertion).

### Zero magic literals

- All string and number literals in test logic are extracted to `private static final` constants
- Shared constants (account name, retry policy, HTTP codes, index constants) live in
  `TestConstants.java` in the `sdk` test package
- Per-file constants (expected field values, URLs, scenario names) stay in each test class
- Text blocks (WireMock JSON responses) stay inline as `private static final String` in each file
- Exception messages in `throw new` are acceptable inline (not test assertions)

### TestConstants shared class

Located at `sdk/TestConstants.java` (test scope), contains:
- `TEST_ACCOUNT`, `NO_RETRY`, `serverError()` - used by 18 unit tests
- `RETRY_AFTER_SECONDS`, `EXPECTED_RETRY_AFTER`, `EXPECTED_PAGINATION_SIZE`, `NON_EXISTENT_ID`
  - used by 18 integration tests
- `FIRST_INDEX` through `FIFTH_INDEX` - used across integration tests for `.get()` calls

Integration tests use specific named imports (not wildcard) to avoid collision with
`WireMock.serverError()`.

## Consequences

- 89 test files updated (18 unit, 18 integration, 33 builder, 5 SDK, 7 demo-app)
- Test count unchanged: 510 (476 SDK + 34 demo-app)
- A new contributor reads a test name and knows: what method, what scenario, what to expect
- `// given / when / then` makes test structure scannable
- No bare literals - every value has a name that explains its purpose
