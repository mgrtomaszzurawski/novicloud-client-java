# ADR-D005: Service-style runners and DemoSession orchestration

**Date:** 2026-03-28
**Status:** Accepted
**Supersedes:** ADR-D001 (runner architecture), ADR-D002 (dry tests and context)

---

## Context

The original `EndpointRunner` / `RunnerContext` pattern treated runners as test harnesses:
every SDK call was wrapped in `ctx.check(label, () -> ...)` or `ctx.require(label, supplier)`.
This made runner bodies hard to read as SDK usage examples - they looked like JUnit test code,
not like application service code.

The primary goals of `demo-app` are:
1. **Smoke test** - verify all 18 SDK domains work end-to-end against a real or mock server.
2. **Showcase** - show how to use the SDK in a real service context.

The `ctx.check` pattern served goal 1 but not goal 2.

---

## Decision

### EndpointRunner interface

Changed from:
```java
void run(RunnerContext ctx);
```
To:
```java
void run(NoviCloudSdk sdk) throws Exception;
```

Runners receive the SDK directly. They call SDK methods directly, use local variables,
log real-time via `System.out.println`, and throw on unrecoverable error.
This mirrors how a real service class would use the SDK.

### DemoSession (new class in `demo.service`)

`DemoSession` owns orchestration:
- Iterates runners in order.
- Logs runner start/end with timing.
- Catches exceptions per runner, adds `RunResult.fail(name, message)` to `RunReport`.
- On success, adds `RunResult.ok(name)`.
- Returns `RunReport` at end.

`RunReport` and `RunResult` are unchanged. Each runner now produces **one** result in the
report (OK or FAIL), not one per step. Per-step granularity is replaced by real-time log output.

### main is a runner list

`NoviCloudDemoApp.main`:
- Loads config and builds SDK.
- Defines an explicit `List<EndpointRunner>` (user comments lines out to test subsets).
- Delegates to `DemoSession`.
- Prints `RunReport`, sets exit code.

### RunnerContext removed

`RunnerContext` is deleted. Its role split:
- Error capture: moved to `DemoSession` (per-runner try/catch).
- SDK access: runners receive `NoviCloudSdk` directly.
- Skip recording: runners log a message and return early; `DemoSession` records no FAIL
  for intentional skips.

### Runner body style

Runners use private methods and local variables. Example pattern:
```java
public void run(NoviCloudSdk sdk) throws Exception {
    var XXX = sdk.xxx();
    System.out.println("  count: " + XXX.count(null));
    var page = XXX.listPage(XxxQuery.builder().onPage(5).build());
    System.out.println("  listPage: " + pageSize(page) + " items");
    // CRUD lifecycle
    String id = XXX.create(XxxCreateDraft.builder("required").build());
    System.out.println("  create: id=" + id);
    if (id != null) {
        XXX.update(XxxUpdateDraft.builder(Long.parseLong(id)).build());
        XXX.deleteById(Long.parseLong(id));
    }
}
```

---

## Consequences

**Positive:**
- Runner code reads like real application service code.
- SDK usage patterns are immediately understandable without knowing the test harness API.
- DemoSession is a single, testable class responsible for orchestration.

**Negative / risks:**
- Per-step granularity in RunReport is lost. A single failed SDK call marks the whole runner
  FAIL. Real-time logs must be consulted to identify the exact failing call.
- `RunnerContextTest` is removed; DemoSession behavior is covered by `DemoSessionTest`
  (added in Phase 5 test suite).
- Some runners have intentional SKIP steps (e.g. StanyMag update skipped to avoid
  modifying live stock data). These are now early returns with a log message.
