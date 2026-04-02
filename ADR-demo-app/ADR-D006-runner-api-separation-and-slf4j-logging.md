# ADR-D006: runner.api separation, RunnerHelper, and SLF4J logging

**Date:** 2026-03-28
**Status:** Accepted

---

## Context

The demo-app `runner` package had three problems:

1. **Mixed concerns**: `EndpointRunner` (interface), `RunReport` (result aggregator),
   `RunResult` (value type) were in the same package as 18 `*Runner` implementations.
   Infrastructure/API types and implementations were indistinguishable at a glance.

2. **Copy-paste logging**: Every runner repeated the same `System.out.println` boilerplate
   for count, listPage, getById, create, update, delete, listAll. Log messages were
   uninformative: `"  listPage(onPage=5): OK"` does not identify which endpoint or what happened.

3. **System.out/System.err**: SonarQube flags these in production code. Structured logging
   with configurable output is the correct tool for a demo application.

---

## Decision

### runner.api subpackage

`EndpointRunner`, `RunReport`, `RunResult` moved to `runner.api`:

```
runner/
  api/
    EndpointRunner.java    <- interface
    RunReport.java         <- result aggregator
    RunResult.java         <- value type
    RunnerHelper.java      <- static logging utilities
  TowaryRunner.java        <- implementations stay in runner/
  ... (17 more)
```

This separates the API (what runners must implement, and what orchestration uses) from the
implementations (endpoint-specific logic).

### RunnerHelper static class

`RunnerHelper` provides static methods for structured logging in runners:

```java
public final class RunnerHelper {
    public static void logCount(Logger log, String endpoint, int count)
    public static <T> void logListPage(Logger log, String endpoint, List<T> data, Integer total)
    public static void logGetById(Logger log, String endpoint, long id)
    public static void logCreate(Logger log, String endpoint, String createdId)
    public static void logUpdate(Logger log, String endpoint, long id)
    public static void logDelete(Logger log, String endpoint, long id)
    public static void logListAll(Logger log, String endpoint, int iterated)
}
```

Used via `import static ...runner.api.RunnerHelper.*` in each runner.

Log format: `[endpoint] operation -> result`, e.g.:
- `[towary] count -> 42`
- `[towary] listPage -> 20 items (total=42)`
- `[kontrahenci] create -> id=123`

**Rejected alternatives for RunnerHelper:**
- Default methods in `EndpointRunner` interface: would pollute the interface contract
  and force every test stub to inherit logging behavior.
- Abstract base class: Java single inheritance; forces a class hierarchy for what
  is purely a logging utility.

### SLF4J + Logback

All `System.out.println` and `System.err.println` replaced with SLF4J:

```java
private static final Logger LOG = LoggerFactory.getLogger(TowaryRunner.class);
```

Logback used as the implementation (logback-classic 1.5.6).
`logback.xml` pattern: `%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n`.

Logger field name `LOG` (uppercase) to satisfy Checkstyle `ConstantName` rule
(`private static final` fields must be UPPERCASE in this project's configuration).

### PMD ruleset additions

Two rules excluded for the demo-app:

- `AvoidDuplicateLiterals` (errorprone): endpoint name string repeats in each runner
  because every `RunnerHelper` call identifies the endpoint. Acceptable duplication.
- `GuardLogStatement` (bestpractices): requires `if (LOG.isInfoEnabled())` guards.
  All log calls use SLF4J `{}` parameterization with simple variable references;
  guards would add boilerplate with no performance benefit.

---

## Consequences

### Positive
- `EndpointRunner` / `RunReport` / `RunResult` clearly separate from implementations
- Log messages identify endpoint and operation: `[towary] create -> id=123`
- No `System.out` in production code; output controlled via `logback.xml`
- Boilerplate removed from all 18 runners

### Negative
- Additional `import static` in every runner
- `RetryHandler.ApiCall` / `VoidApiCall` still not imported (not needed in demo-app)
- Logback is a runtime dependency (adds ~500KB to the jar)

---

## Related

- ADR-D005: DemoSession orchestration pattern (unchanged)
- ADR-D001: runner-per-endpoint architecture (unchanged)
