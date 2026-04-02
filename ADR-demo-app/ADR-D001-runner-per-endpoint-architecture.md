# ADR-D001: Runner-per-endpoint architecture for demo-app

## Status

Accepted (architecture superseded by ADR-D005; runner-per-endpoint pattern remains)

## Context

The original demo-app was a single `main()` method calling `count()` on 14 resources. This fulfilled no part of the ADR-001 goal ("a place for quick integration testing and debugging"). It provided no per-method coverage, no filter variation testing, no CRUD lifecycle validation, and no actionable failure output.

The goal is to create a demo-app that:
- exercises all SDK methods for every endpoint
- tests filter parameter variations
- produces structured, readable output
- can be extended without modifying the orchestrator

## Decision

Each of the 18 API resource domains gets its own `EndpointRunner` implementation in `demo.runner`. All runners implement a common interface:

```java
public interface EndpointRunner {
    String name();
    void run(RunnerContext ctx);
}
```

`NoviCloudDemoApp.main()` holds a static list of all runners and iterates them in order, creating a `RunnerContext` per runner and passing a shared `RunReport`.

### RunnerContext

Holds the SDK instance and the report. Provides:
- `check(label, Runnable)` — runs action, records OK or FAIL, never throws
- `require(label, Supplier<T>)` — same, but returns `Optional<T>` so caller can chain dependent steps with `ifPresent()`
- `skip(label, reason)` — records SKIP when a step cannot run due to a prior failure

### RunReport + RunResult

`RunResult` is a value type with status (OK / FAIL / SKIP), label, and optional detail string.
`RunReport` collects all results and prints a summary. `main()` exits with code 1 if any failures occurred.

## Alternatives considered

1. **Single main() with all assertions inline** — not extensible, mixing structure and execution
2. **JUnit integration tests** — would require credentials in CI; integration runners belong in `main`, not in test phase
3. **One runner class with all endpoints** — no separation, impossible to run one endpoint in isolation

## Consequences

- Adding a new endpoint requires only a new `XxxRunner` class and one line in `NoviCloudDemoApp`
- Each runner is independently readable and debuggable
- Output clearly identifies which step failed and on which resource
- `exit(1)` on failures makes the demo-app usable in scripts or CI health checks
