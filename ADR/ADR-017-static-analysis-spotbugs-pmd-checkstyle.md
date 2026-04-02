# ADR-017: Static analysis — SpotBugs, PMD, Checkstyle in novicloud-client

## Status

Accepted (exclusion details superseded by ADR-038)

## Context

The novicloud-client module contains hand-written SDK code wrapping OpenAPI-generated client classes.
The SDK code is written by an AI agent without live compilation feedback during generation.
This creates a specific class of risks:

- **Silent logic errors** that compile but behave incorrectly (null dereference, resource leaks)
- **Style drift** across 17+ similar SDK classes generated from the same pattern
- **Pattern inconsistency** visible only when comparing many files at once

Before adding static analysis, the only quality gate was compilation + unit tests.
Tests cover builder semantics but do not cover internal SDK method safety, resource handling,
or inter-class consistency.

The goal was to add three CLI-runnable tools that can be triggered from within the development
environment (including by an AI agent) without requiring an IDE plugin or CI/CD server:

- **SpotBugs** — bytecode-level analysis; catches NPE risk, resource leaks, mutability exposure
- **PMD** — source-level analysis; catches code smells, error-prone patterns, performance issues
- **Checkstyle** — style conformance; catches formatting inconsistencies across similar classes

## Decision

Add all three Maven plugins to `novicloud-client/pom.xml` and configure them to analyze only
hand-written SDK code, explicitly excluding OpenAPI-generated sources.

### Scope restriction

Generated code under `target/generated-sources/openapi/` is excluded from all three tools:

| Tool | Exclusion mechanism |
|------|---------------------|
| SpotBugs | `<onlyAnalyze>io.github.mgrtomaszzurawski.novicloud.sdk.-</onlyAnalyze>` |
| PMD | `<excludeRoots>target/generated-sources</excludeRoots>` + `<excludes>**/client/**</excludes>` |
| Checkstyle | `<sourceDirectories>src/main/java</sourceDirectories>` (does not include generated) |

`module-info.java` is excluded from Checkstyle because Checkstyle 9.x (used by plugin 3.3.1)
cannot parse JPMS module declarations.

### Checkstyle rule configuration

A shared `checkstyle.xml` at the project root applies to both modules.
Two standard rules were adjusted to match project coding style:

- **`LeftCurly`** set to `option="nlow"` — allows inline single-statement method bodies
  (`{ return this; }`) used throughout the builder pattern in Query and Draft classes.
  The default `nl` option would require expanding ~600 method bodies with no readability gain.
- **`OneStatementPerLine`** removed — incompatible with the builder-style inline methods.
  The builder pattern intentionally places assignment + return on one line.

All other rules are kept at default strictness: `NeedBraces`, `EqualsHashCode`,
`EmptyCatchBlock`, `UnusedImports`, naming conventions.

### PMD custom ruleset

A shared `pmd-ruleset.xml` at the project root replaces the three standard category includes.
Two rules are excluded from the SDK scope:

| Rule | Category | Justification |
|------|----------|---------------|
| `AvoidFieldNameMatchingMethodName` | errorprone | Query/Draft classes use record-style accessors (`field id`, method `id()`). This is intentional — same pattern as Java records. The rule targets traditional JavaBeans, not modern accessor style. |
| `DataflowAnomalyAnalysis` | errorprone | False positive: PMD incorrectly flags `safe` variable in SDK catch blocks as DU-anomaly. Known PMD limitation with try/catch scoping. |

### SpotBugs filter

A shared `spotbugs-exclude.xml` at the project root suppresses one pattern:

| Bug | Class | Justification |
|-----|-------|---------------|
| `EI_EXPOSE_REP` | `NoviCloudSdk` | Sub-SDK objects (`AsortySdk`, `WalutySdk`, etc.) are effectively immutable after construction — all fields are set in the constructor and never modified. Returning them from a facade is not an encapsulation risk. The bug pattern targets truly mutable objects (arrays, Date, collections). |

### Real bugs and issues found

The following were genuine findings that were fixed:

**NeedBraces (Checkstyle) — 127 violations fixed:**
All `if` statements without braces across 17 SDK files and 18 runner files.
Pattern: `if (total != null) return total;` → expanded to use `{}`.
Fixed with a targeted Python script to avoid manual churn across ~35 files.

**AvoidLiteralsInIfCondition (PMD) — 18 violations fixed:**
Each `fetchByLink` method in every SDK class contained `if (response.statusCode() / 100 != 2)`.
The literal `2` was extracted to `private static final int HTTP_SUCCESS_CLASS = 2;`
added to each class.

**AvoidDuplicateLiterals (PMD) — 1 violation fixed:**
`StanyMagSdk` repeated `"idTowaru must not be null"` in 4 validation checks.
Extracted to `private static final String ERR_TOWAR_ID_NULL = "idTowaru must not be null";`.

**UnusedPrivateField (PMD) — 2 violations fixed:**
`NoviCloudSdk` stored `apiClient` and `accountName` as fields after passing them to
sub-SDK constructors. The fields were never read again. Removed.

**MissingSerialVersionUID (PMD) — 1 violation fixed:**
`NoviCloudSdkException extends RuntimeException` (which implements `Serializable`)
had no `serialVersionUID`. Added `private static final long serialVersionUID = 1L;`.

**NullAssignment (PMD) — 1 violation fixed:**
`PagedIterable.loadNextPage()` had a dead-code guard: `if (nextLink == null) { current = null; return; }`.
The method is only ever called from `hasNext()` which already guards `if (nextLink != null)`.
The unreachable null branch was removed.

**EI_EXPOSE_REP (SpotBugs) — addressed via filter + unused method removal:**
`NoviCloudSdk` accessor methods were flagged. Suppressed via filter with justification.
`RunnerContext.report()` was flagged and also found to be unused — method removed.

## Consequences

**Positive:**
- All three tools run cleanly: 0 SpotBugs bugs, 0 PMD violations, 0 Checkstyle violations.
- Real defects found: unused fields, missing serialVersionUID, dead code, duplicate literals,
  bare if-statements (which hide accidental scope bugs), unsafe HTTP status check.
- CI/CD can gate on `mvn spotbugs:check pmd:check checkstyle:check` without additional configuration.
- Future AI-generated SDK additions will be caught by the same rules on the next run.

**Negative / Trade-offs:**
- SpotBugs requires Java 23-compatible version (≥ 4.8.6.1). Earlier versions fail on JDK 23
  class files (major version 67). This pins the plugin version.
- Some suppressions are broad (e.g. `AvoidLiteralsInIfCondition` in PMD covers demo-app too).
  Future improvements should re-enable this rule for SDK code specifically.
- `module-info.java` cannot be analyzed by Checkstyle 3.3.1 — JPMS module declarations
  are excluded from style checks.
