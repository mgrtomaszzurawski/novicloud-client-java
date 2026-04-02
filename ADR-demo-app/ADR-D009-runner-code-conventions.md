# ADR-D009: Runner code conventions - types, names, structure

**Date:** 2026-03-29
**Status:** Accepted

---

## Context

A code review of all 18 *Runner classes identified four recurring issues that reduced
readability without providing any benefit:

1. **`var` for API return types** - `var page = api.listPage(null)` hides the response type.
   The reader must look up the client signature to know what `page` holds.

2. **`Long` after null guard** - `Long id = Long.parseLong(createdId)` inside
   `if (createdId != null)` causes unnecessary autoboxing. `Long.parseLong` returns `long`
   (primitive). IntelliJ flags this. The null guard already rules out NPE.

3. **Magic index `get(0)`** - The list index `0` is a magic number with no stated intent.
   The project already uses `ITERATION_START = 0` in RunnerHelper for the same reason.

4. **Flat `run()` method** - All operations were inlined in one method 30-60 lines long.
   The reader must scan the whole method to understand the sequence. The operation names
   are buried in implementation detail.

5. **Truncated variable name `listAllIter`** - The `Iter` suffix is a shortened form.
   The project convention (established in ADR-D008) is full, unambiguous names.

6. **Missing `LIST_ALL_LIMIT` constant** - Four runners (`PozdokRunner`, `SprzedazRunner`,
   `RapSprzedRunner`, `RapPracyRunner`) used a bare `3` literal in the while condition
   instead of the named constant used by the other 14 runners.

---

## Decisions

### 1. No `var` in runner classes

Use explicit types for all local variables in runner classes. The demo-app is documentation
as much as it is code - a reader unfamiliar with the SDK should be able to read a runner
from top to bottom without consulting external sources.

```java
// Bad
var page = api.listPage(null);

// Good
ApiResponseAsortyList page = api.listPage(null);
```

### 2. `long` (primitive) after null guard

When `Long.parseLong(s)` is called inside a null guard that guarantees `s != null`, use
the primitive `long`. Boxing to `Long` has no purpose and misleads the reader into thinking
null is possible.

```java
// Bad
if (createdId != null) {
    Long id = Long.parseLong(createdId);  // autoboxing for no reason

// Good
if (createdId != null) {
    long id = Long.parseLong(createdId);
```

The `Long firstId = page.getData().get(FIRST_INDEX).getId()` pattern is intentionally kept
as `Long` because `getId()` returns `Long` and null is a valid API response.

### 3. `FIRST_INDEX = 0` in RunnerHelper

Added alongside `ITERATION_START = 0`. Every runner that accesses the first element of a
list uses `get(FIRST_INDEX)` instead of `get(0)`.

```java
public static final int FIRST_INDEX = 0;
```

### 4. `run()` as table of contents

Extract all implementation from `run()` into private methods. The `run()` method shows
what the runner does in sequence; the private methods show how.

```java
@Override
public void run(NoviCloudClient client) throws Exception {
    AsortyClient api = client.asorty();
    runCount(api);
    runListPage(api);
    runListAll(api);
    runCreateUpdateDelete(api);
}
```

Standard private method names across all runners:
- `runCount(api)` - calls `api.count(null)` and logs
- `runListPage(api)` - calls `api.listPage(null)`, conditionally `api.getById()`, and any
  filtered `api.listPage()` calls
- `runListAll(api)` - iterates `api.listAll()` up to `LIST_ALL_LIMIT` and logs
- `runCreateUpdateDelete(api)` - create, update, delete lifecycle (CRUD runners only)
- `runCreateUpdate(api)` - create + update without delete (KartyLojRunner: no DELETE endpoint)

Runners with special operations (StanyMagRunner) add:
- `runListByTowar(api, towarId)`
- `runGetByTowarAndSklep(api, towarId, sklepId)`
- `extractTowarId(page)`, `extractSklepId(page)` - pure data extraction helpers

For StanyMagRunner, `runListPage` returns the page so subsequent methods can extract IDs
from it without repeating the API call.

### 5. Full variable names - `listAllIterator` not `listAllIter`

```java
// Bad
var listAllIter = api.listAll(null).iterator();

// Good
Iterator<Asort> listAllIterator = api.listAll(null).iterator();
```

### 6. `LIST_ALL_LIMIT` in every runner

All 18 runners now declare `private static final int LIST_ALL_LIMIT = <n>`. No bare integer
literals in while conditions.

---

## Consequences

- `run()` in every runner is 5-8 lines: one line per operation, self-documenting.
- All runner classes follow the same structural template, making new runners easy to write
  and existing runners easy to scan.
- The type map (API response type per runner) is available in the demo-app source for reference when adding runners.
- Existing `AvoidLiteralsInIfCondition` PMD exclusion in `pmd-ruleset.xml` remains valid;
  the `LIST_ALL_LIMIT` constants satisfy the underlying intent.

---

## Related

- ADR-D001: runner per endpoint (establishes the runner pattern)
- ADR-D008: no magic strings in runners (established `ENDPOINT` and `ITERATION_START` pattern)
