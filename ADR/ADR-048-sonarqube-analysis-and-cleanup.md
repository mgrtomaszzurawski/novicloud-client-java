# ADR-048: SonarQube analysis and code cleanup

## Status

Accepted

## Context

Before v1.0.0 release, SonarQube Community Edition 9.9.8 LTS was run against the full project
to identify code quality issues beyond what SpotBugs, PMD, and Checkstyle catch.

Initial scan (2026-03-31) reported **181 issues** (all CODE_SMELL, 0 bugs, 0 vulnerabilities):
- `novicloud-client` (SDK): 105 issues
- `demo-app`: 76 issues

### SDK issue breakdown (105 total)

| Rule | Count | Description |
|------|-------|-------------|
| S125 | 55 | Commented-out code (ADR-031 references + dead code in QueryBuilders) |
| S5778 | 48 | `assertThrows` lambda with multiple invocations |
| S1488 | 1 | Variable immediately returned (`StanMagUpdateBuilder.toBuilder`) |
| S3398 | 1 | Private method should be in inner class (`NoviCloudClient.basicAuthHeader`) |
| S1068 | 1 | Unused private field (`StanyMagClient.ERR_NULL_SUFFIX`) |

## Decision

### 1. Remove all commented-out code from QueryBuilders (S125 - 55 issues)

ADR-031 documented 21+ broken server-side query parameters. The code had three forms of
commented-out remnants per parameter:
- `// private final String fieldName;` (field declaration)
- `// this.fieldName = builder.fieldName;` (constructor assignment)
- `// public String fieldName() { return fieldName; }` (getter)
- `// public Builder fieldName(String v) { this.v = v; return this; }` (setter)

All information is already captured in ADR-031's tables with endpoint, field name, server
error, and documentation status. The in-code comments added no value and made QueryBuilder
files ~30% longer than necessary.

**Removed**: ~125 lines of commented-out code across 10 QueryBuilder files.

### 2. Refactor assertThrows lambdas (S5778 - 48 issues)

SonarQube rule S5778 requires that `assertThrows` lambdas contain exactly one method
invocation, so it is unambiguous which call throws.

Before:
```java
assertThrows(IllegalArgumentException.class, () -> client().getById(null));
```

After:
```java
var c = client();
assertThrows(IllegalArgumentException.class, () -> c.getById(null));
```

For builder chains:
```java
// Before
assertThrows(IllegalArgumentException.class, () ->
        RapPracyQueryBuilder.builder().dataPocz("bad-date").build());

// After
var builder = RapPracyQueryBuilder.builder().dataPocz("bad-date");
assertThrows(IllegalArgumentException.class, () -> builder.build());
```

For setters that validate eagerly (e.g. `RetryPolicy.Builder.maxAttempts`), the setter call
itself goes inside `assertThrows`:
```java
var builder = RetryPolicy.builder();
assertThrows(IllegalArgumentException.class, () -> builder.maxAttempts(0));
```

**Changed**: 21 test files, 48 `assertThrows` call sites.

### 3. Inline return (S1488 - 1 issue)

`StanMagUpdateBuilder.toBuilder()` assigned to a local variable and immediately returned.
Inlined to `return new Builder(...)`.

### 4. Move method to inner class (S3398 - 1 issue)

`NoviCloudClient.basicAuthHeader()` was a private static method on the outer class, used
only inside `Builder.build()`. Moved into `Builder` where it belongs.

### 5. Remove unused field (S1068 - 1 issue)

`StanyMagClient.ERR_NULL_SUFFIX` was unused - this client uses dedicated error constants
(`ERR_TOWAR_ID_NULL`, `ERR_SKLEP_ID_NULL`) instead.

### 6. Demo-app issues not addressed (76 issues)

Demo-app is internal-only code (never published, `maven.deploy.skip=true`). Its 76 issues
are mostly unused private methods from exploratory testing (S1144: 34) and log-then-rethrow
patterns (S2139: 18). These are acceptable for a demo/integration test harness.

## Consequences

- SDK module drops from 105 to 0 SonarQube issues (target: verified by re-scan)
- Demo-app retains 76 issues (accepted, internal code)
- Total project: 181 -> 76 issues
- QueryBuilder files are shorter and cleaner without commented-out dead code
- Test files follow SonarQube's single-invocation pattern for `assertThrows`
- No behavioral changes - all 347 tests pass, 0 static analysis violations
