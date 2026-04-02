# ADR-038: Static analysis exclusions audit (2026-03-30)

## Status

Accepted (supersedes exclusion sections of ADR-017 and ADR-D004)

## Context

ADR-017 and ADR-D004 documented static analysis exclusions at the time of initial setup.
Since then the codebase went through significant refactoring (class renames, package
restructuring, deleted classes) but the exclusion configs and their ADRs were never
re-verified. A full audit on 2026-03-30 found:

- Dead exclusions referencing classes/patterns that no longer exist
- Exclusions with misleading justifications (e.g. "demo-app runners" for an SDK rule)
- Missing exclusions that silently passed because they were added without ADR updates

This ADR documents the **current, verified state** of all exclusions across all three
tools after cleanup. Every exclusion listed below was verified against the actual codebase
by running the tool without the exclusion and confirming the violation exists.

## Decision

### Removed (dead exclusions)

| File | Exclusion | Reason removed |
|------|-----------|----------------|
| `pmd-ruleset.xml` | `UseProperClassLoader` | `getClass().getClassLoader()` not used anywhere. Original code was refactored per ADR-D004 to use `Thread.currentThread().getContextClassLoader()`, making the exclusion unnecessary. |
| `spotbugs-exclude.xml` | `EI_EXPOSE_REP2` on `RunnerContext` | Class `RunnerContext` no longer exists. Was removed during runner refactoring. The exclusion was a dead reference. |

### Active exclusions - checkstyle.xml

| Rule | Setting | Scope | Justification | Verified against |
|------|---------|-------|---------------|------------------|
| `OneStatementPerLine` | removed | both modules | Builder pattern uses `{ this.x = x; return this; }` on one line. 363 occurrences across 39 files. Expanding them adds noise with zero readability gain. | Re-enabling produces 363 violations. |
| `LeftCurly` | `option="nlow"` | both modules | Same builder pattern - allows inline `{ return this; }` blocks. Default `nl` would force line breaks on all builders. | Re-enabling produces matching violations. |
| `EmptyCatchBlock` | `exceptionVariableName="ignored\|expected"` | both modules | Convention: empty catch blocks must name the variable `ignored` or `expected`. Currently used in `StawkiVatRunner.cleanupIfExists()` where `NoviCloudNotFoundException` is expected during cleanup. | Naming the variable `expected` documents intent. |

### Active exclusions - pmd-ruleset.xml

| Rule | Category | Scope | Justification | Verified against |
|------|----------|-------|---------------|------------------|
| `UnusedPrivateMethod` | bestpractices | demo-app | Runners keep private methods (`runCreateOnce`, `runCreateUpdateDelete`, `runProbeFilters`) commented out in `run()` but preserved as reusable tools for live API testing. These are intentional test toolkit methods, not dead code. | 22 violations without exclusion. |
| `UnusedPrivateField` | bestpractices | demo-app | Constants (`FILTER_ALL_DATE`, `FILTER_ALL_STR`, `OP_UPDATE`, `SKIP_LIVE_STOCK`) prepared for the commented-out test scenarios above. | 4 violations without exclusion. |
| `GuardLogStatement` | bestpractices | demo-app | All logging uses SLF4J parameterized placeholders (`{}`). Arguments are simple variable references with no computation. Adding `if (log.isXxxEnabled())` guards would be pure boilerplate. SDK module has zero log calls. | 10 logging calls in `RunnerHelper.java`. |
| `AvoidFieldNameMatchingMethodName` | errorprone | novicloud-client | Query/Draft classes use record-style accessors: field `id`, method `id()`. This is an intentional Java-records-inspired pattern used consistently across all 18 endpoint packages. | Pattern used in all Query/Draft builder classes. |
| `MissingSerialVersionUID` | errorprone | novicloud-client | `FlexibleLocalDateTimeDeserializer` extends Jackson's `JsonDeserializer` which implements `Serializable`. The class is stateless (only static `DateTimeFormatter` constants), never serialized. Adding `serialVersionUID` would be noise. | 1 violation on `FlexibleLocalDateTimeDeserializer`. |
| `DataflowAnomalyAnalysis` | errorprone | both modules | Known PMD false positive with try/catch scoping. PMD incorrectly reports variables assigned in try blocks and used in catch blocks as DU-anomaly. | Known PMD bug, not project-specific. |
| `AvoidLiteralsInIfCondition` | errorprone | both modules | HTTP status codes (`429`, `500`) in `NoviCloudException.of()` and validation bounds (`maxAttempts < 1`) in `RetryPolicy.Builder` are standard, self-documenting literals. Extracting `429` to `HTTP_TOO_MANY_REQUESTS` would not improve clarity. | 3 violations in novicloud-client without exclusion. |
| `AvoidInstantiatingObjectsInLoops` | performance | demo-app | `DemoSession` creates a fresh `RunnerContext` per runner iteration. Each runner needs an isolated context with its own result accumulator. One instance per iteration is correct by design. | Violation in `DemoSession` runner loop. |

### Active exclusions - spotbugs-exclude.xml

| Bug pattern | Scope | Justification | Verified against |
|-------------|-------|---------------|------------------|
| `EI_EXPOSE_REP` on `NoviCloudClient` | novicloud-client | Resource client objects (`AsortyClient`, `WalutyClient`, etc.) are effectively immutable services - all fields set at construction, never modified. Returning them from the facade is safe. `EI_EXPOSE_REP` targets truly mutable containers (arrays, Date, ArrayList). | SpotBugs flags all 18 getter methods without exclusion. |
| `SE_NO_SERIALVERSIONID` on `FlexibleLocalDateTimeDeserializer` | novicloud-client | Same as PMD `MissingSerialVersionUID` above. SpotBugs and PMD both flag it independently. | 1 violation without exclusion. |
| `UPM_UNCALLED_PRIVATE_METHOD` on `demo.runner.*` | demo-app | Same as PMD `UnusedPrivateMethod` above. SpotBugs and PMD both flag the same runner toolkit methods. | 23 violations without exclusion. |

## Consequences

**Positive:**
- All exclusions now verified against actual code - no dead references
- Misleading justifications corrected (e.g. `AvoidLiteralsInIfCondition` was attributed to "demo-app" but actually needed for SDK HTTP status mapping)
- Single source of truth for exclusion rationale - this ADR, not scattered XML comments

**Negative:**
- `UnusedPrivateMethod`/`UnusedPrivateField` and `UPM_UNCALLED_PRIVATE_METHOD` are global exclusions that also cover novicloud-client. If SDK code develops genuinely unused methods, these tools will not catch them. A module-specific ruleset split would allow scoping these to demo-app only.
- `AvoidLiteralsInIfCondition` is global. Demo-app runner code could develop problematic magic numbers without PMD catching them.

**Supersedes:**
- ADR-017 sections "PMD custom ruleset", "SpotBugs filter" (exclusion details only, not tool setup)
- ADR-D004 sections "PMD - demo-app-specific rule suppressions", "SpotBugs scope" (exclusion details only)
