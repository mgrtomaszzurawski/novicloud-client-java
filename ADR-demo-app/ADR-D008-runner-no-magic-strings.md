# ADR-D008: No magic strings in runners

**Date:** 2026-03-28
**Status:** Accepted

---

## Context

Each runner class repeated its endpoint name as a string literal in every `logXxx()` call
(8-10 occurrences per class, 18 classes). Additionally, inline `LOG.info` calls for
filtered listPage queries bypassed `RunnerHelper`, with the filter description pre-formatted
as a string literal by the caller (`"onPage=5"`, `"nazwa=~a~"`, `"aktywny=true, onPage=5"`).

Both patterns violate zero-tolerance for magic strings: a rename or format change requires
editing every occurrence across every runner.

---

## Decision

### ENDPOINT constant in every runner

```java
private static final String ENDPOINT = "asorty";

@Override
public String name() { return ENDPOINT; }
```

All `logXxx(LOG, ENDPOINT, ...)` calls use the constant. The string literal exists once per class.

### Typed listPage log helpers in RunnerHelper

Instead of a generic `logListPageQuery(Logger, String, String queryDesc)` where callers
pre-format the description, RunnerHelper exposes one method per filter pattern:

```java
logListPageOnPage(Logger log, String endpoint, int onPage)
logListPageNazwa(Logger log, String endpoint, String nazwa)
logListPageAktywny(Logger log, String endpoint, boolean aktywny)
logListPageAktywnyOnPage(Logger log, String endpoint, boolean aktywny, int onPage)
logSkipped(Logger log, String endpoint, String operation, String reason)
```

Field names (`"onPage"`, `"nazwa"`, `"aktywny"`) live only in RunnerHelper. Callers pass
typed values - no string formatting at the call site.

**Why typed methods over a generic `(String field, Object value)` helper:**
- The 4 patterns cover 100% of filter queries across all 18 runners
- Method names are self-documenting at the call site
- The int/boolean types make misuse a compile error, not a runtime surprise
- Adding a new filter pattern requires adding one method to RunnerHelper - the change is
  visible and deliberate

### logSkipped for omitted operations

Operations skipped intentionally (live data modification, reference data) use:
```java
logSkipped(LOG, ENDPOINT, "update", "modifies live stock - run manually with known ids");
```

Consistent format; skip reason is documented at the call site.

---

## Consequences

### Positive
- Endpoint name change: edit one constant per runner, not 8-10 lines
- Log format change: edit one method in RunnerHelper, not N runner files
- New runner template: add `ENDPOINT`, call `logListPageOnPage`/`logListPageNazwa` etc. -
  no free-form strings

### Negative
- `logListPageAktywnyOnPage` covers only `aktywny + onPage`. A third combined filter would
  need a new helper method. Acceptable: 18 runners exist and the filter set is stable.
- `StawkiVatRunner` has a unique `id` filter - handled with a `FILTER_ID` constant and
  inline `LOG.info("[{}] listPage(id={}) -> OK", ENDPOINT, FILTER_ID)`. No dedicated helper
  for a single-use pattern.

---

## Extension: full demo-app sweep (2026-03-28)

The same zero-magic-strings policy was subsequently applied to all other demo-app source:

**RunnerHelper.java** - added `public static final int ITERATION_START = 0;`. All 18 runners
had `int iterated = 0;` in method bodies - replaced with `ITERATION_START` via the existing
static import. One definition, consistent across all runners.

**RunResult.java** - `"[OK  ]"`, `"[FAIL]"`, `"[SKIP]"` extracted to `ICON_OK`, `ICON_FAIL`,
`ICON_SKIP` constants.

**AppProperties.java** - property key strings (`"novicloud.account-name"` etc.), placeholder
parse characters (`"${"`, `"}"`, `':'`), and empty default `""` extracted to constants.
`substring(2, ...)` rewritten as `substring(PLACEHOLDER_PREFIX.length(), ...)`.

**NoviCloudDemoApp.java** - `"application.properties"`, error log message, fallback account
name `"demo"` extracted.

**PropertiesLoader.java** - error message prefix extracted.

Test files followed the same rule: repeated test data values extracted to `private static final`
constants at the top of each test class. Single-use boundary inputs (`0`, `-1` in exception
tests) extracted to named constants with `INVALID_` prefix to document intent.

---

## Related

- ADR-D001: runner per endpoint (establishes the runner pattern)
- ADR-D006: SLF4J logging and RunnerHelper (original RunnerHelper rationale)
- ADR-027: No magic strings in SDK source (SDK-side equivalent)
