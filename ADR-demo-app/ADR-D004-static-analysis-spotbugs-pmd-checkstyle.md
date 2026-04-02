# ADR-D004: Static analysis — SpotBugs, PMD, Checkstyle in demo-app

## Status

Accepted (exclusion details superseded by ADR-038)

## Context

See ADR-017 for the general rationale for adding static analysis tools to the project.

The demo-app module has a different character than the SDK module:

- It is application code, not library code — encapsulation and API surface rules apply less strictly
- It uses `System.out` and `System.err` as its primary output mechanism (CLI tool)
- It loads classpath resources using the standard classloader
- It instantiates per-request context objects (`RunnerContext`) inside loops
- Runner classes are demonstration code with intentionally small, readable iteration counts

These characteristics generate a distinct set of false positives compared to the SDK module.
The shared `pmd-ruleset.xml` and `spotbugs-exclude.xml` needed to account for demo-app patterns
without removing rules that are genuinely useful for SDK code.

## Decision

Add SpotBugs, PMD, and Checkstyle plugins to `demo-app/pom.xml` using the same shared
configuration files as `novicloud-client`.

### SpotBugs scope

`<onlyAnalyze>io.github.mgrtomaszzurawski.novicloud.demo.-</onlyAnalyze>` restricts analysis
to demo-app hand-written classes. No generated sources exist in demo-app, so no additional
exclusions are needed.

One additional suppression was added to `spotbugs-exclude.xml`:

| Bug | Class | Justification |
|-----|-------|---------------|
| `EI_EXPOSE_REP2` | `RunnerContext` | `RunReport` is passed into `RunnerContext` from `NoviCloudDemoApp`, which also retains a reference to it. Sharing a mutable accumulator between the context and the orchestrator is intentional — it allows `report.print()` to be called after all runners complete. Defensive copying would break this design. |

As part of this analysis, `RunnerContext.report()` (a public getter for the `RunReport`) was
found to be completely unused externally. It was removed. This also eliminated the companion
`EI_EXPOSE_REP` violation (returning the internal mutable object).

### PMD — demo-app-specific rule suppressions

Three rules were added to the shared exclusion list specifically because of demo-app patterns:

| Rule | Category | Justification |
|------|----------|---------------|
| `SystemPrintln` | bestpractices | demo-app is a standalone CLI application. `System.out` and `System.err` are the correct output mechanisms. Replacing them with a logging framework would be architectural over-engineering for a demo tool. |
| `UseProperClassLoader` | bestpractices | PMD recommends `Thread.currentThread().getContextClassLoader()` for J2EE container environments. This application is a standalone CLI — not a J2EE/Jakarta EE container. The recommendation was actually applied in code as a good practice fix (see below), removing the violation. |
| `AvoidInstantiatingObjectsInLoops` | performance | `new RunnerContext(sdk, report, runner.name())` is created inside the runner loop. Each runner requires a fresh, isolated context. Creating one instance per iteration is correct and necessary by design. |

### Checkstyle — same rules, same exclusions

The shared `checkstyle.xml` applies unchanged. `module-info.java` is excluded
(same reason as SDK module: JPMS declaration not parseable by Checkstyle 3.3.1).

### Real bugs and issues found in demo-app

**AssignmentInOperand (PMD) — 18 violations fixed:**
All runner files contained `if (++n >= 3) { break; }` (or `>= 10`, etc.) for iterating
the first N items. The `++n` increment was moved before the `if` condition:
```java
// before
if (++n >= 3) { break; }

// after
n++;
if (n >= 3) { break; }
```
This eliminates a subtle readability issue where the increment side-effect was hidden inside
the condition evaluation.

**UseLocaleWithCaseConversions (PMD) — 1 violation fixed:**
`runner.name().toUpperCase()` in `NoviCloudDemoApp` was locale-sensitive.
Fixed to `toUpperCase(Locale.ROOT)` with `import java.util.Locale`.

**UseProperClassLoader (PMD) — 1 violation fixed:**
`PropertiesLoader` used `PropertiesLoader.class.getClassLoader()`.
Updated to `Thread.currentThread().getContextClassLoader()` — more portable in
modular classpath environments.

**MissingStaticMethodInNonInstantiatableClass (PMD) — 1 violation fixed:**
`TowaryService` was a private-constructor class with no static methods — an empty,
deprecated skeleton left from an early prototype phase. Deleted entirely.

**NeedBraces (Checkstyle) — 26 violations fixed:**
18 runner files contained bare `if` statements (same pattern as SDK module).
All expanded to use `{}` via the same automated fix.

## Consequences

**Positive:**
- demo-app code is held to the same analysis standards as SDK code.
- Genuine issues fixed: removed dead class (`TowaryService`), fixed locale-unsafe string
  operation, improved classloader portability, corrected increment-before-condition style.
- Unused public API (`RunnerContext.report()`) discovered and removed — tightens the
  demo-app's internal surface.
- Shared configuration files (`pmd-ruleset.xml`, `spotbugs-exclude.xml`, `checkstyle.xml`)
  keep maintenance cost low — one place to update rules for both modules.

**Negative / Trade-offs:**
- `AvoidLiteralsInIfCondition` is suppressed globally including in the SDK. The SDK no longer
  triggers it (all HTTP status literals extracted to `HTTP_SUCCESS_CLASS`), but the suppression
  is broader than necessary. A module-specific ruleset split would allow re-enabling the rule
  for the SDK only.
- `SystemPrintln` suppression is global. If SDK code ever accidentally used `System.out`,
  the rule would not catch it.
