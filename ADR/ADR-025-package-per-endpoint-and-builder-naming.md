# ADR-025: Package per endpoint and `*Builder` naming convention

**Date:** 2026-03-28
**Status:** Accepted (package paths refined by ADR-037)

---

## Context

The SDK had a flat structure: all 50+ classes (Query, Draft, and Sdk) in a single
`io.github.mgrtomaszzurawski.novicloud.sdk` package. Names used inconsistent suffixes:
- `TowaryQuery` - query filter, but the `Query` suffix is ambiguous
- `TowarCreateDraft` / `TowarUpdateDraft` - "Draft" implies work-in-progress, not "builder"
- Finding classes by endpoint required scanning the full list

The demo-app consumed these classes, and developers (human and AI) repeatedly had to look up
exact class names because the naming gave no structural hint.

---

## Decision

### Naming convention

All builder/query classes renamed to `{Entity}{Operation}Builder`:
- `TowaryQuery` -> `TowarQueryBuilder`
- `TowarCreateDraft` -> `TowarCreateBuilder`
- `TowarUpdateDraft` -> `TowarUpdateBuilder`

The `Builder` suffix makes the pattern explicit in IDE autocomplete:
typing `Towar` shows `TowarQueryBuilder`, `TowarCreateBuilder`, `TowarUpdateBuilder` immediately.

### Package structure

Each API endpoint group gets its own subpackage under `sdk`:

```
sdk/                  <- facade (NoviCloudSdk, NoviCloudSdkFactory, RetryPolicy, RetryHandler)
sdk.towary/           <- TowarySdk, TowarQueryBuilder, TowarCreateBuilder, TowarUpdateBuilder
sdk.asorty/           <- AsortySdk, ...
sdk.kontrahenci/      <- KontrahenciSdk, ...
sdk.kartyloj/         <- KartyLojSdk, ...
... (18 total subpackages)
sdk.exception/        <- unchanged
sdk.paging/           <- unchanged
```

Package name = API endpoint name, lowercased, hyphens removed:
`/f-karty-loj` -> `kartyloj`, `/stany-mag` -> `stanymag`, etc.

All 18 subpackages exported from `module-info.java`.

---

## Consequences

### Positive
- IDE autocomplete: type the entity name to find all related builders
- Classes grouped by domain, not by technical type
- `TowarCreateBuilder` is unambiguous; `TowarCreateDraft` was not

### Negative
- All existing imports in demo-app and tests needed updating
- `RetryHandler` became a cross-package concern (see ADR-026)

### Migration
Executed via Python script in session 2026-03-28.
All demo-app code, tests, and module-info.java updated atomically.

---

## Alternatives considered

**Keep flat package, rename only** - Easier migration, but 50+ classes in one package
remains hard to navigate. Rejected.

**Single `sdk.builders` package for all builders** - Groups by type instead of domain.
Rejected: you would still need to know which endpoint a builder belongs to.
