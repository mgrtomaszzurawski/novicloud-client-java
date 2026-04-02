# ADR-037: Grouping endpoint packages under `sdk.resources`

**Date:** 2026-03-30
**Status:** Accepted

## Context

The `sdk` package contained 18 endpoint-specific subpackages (`asorty`, `dokumenty`, `towary`, ...)
alongside two cross-cutting infrastructure packages (`exception`, `paging`). Browsing the source
tree gave no visual distinction between endpoint code and infrastructure code - everything appeared
at the same level.

ADR-025 established the package-per-endpoint convention. This ADR refines the layout within `sdk`.

## Decision

Move all 18 endpoint packages one level deeper into `sdk.resources`:

```
sdk/
  NoviCloudClient.java
  RetryPolicy.java
  RetryHandler.java
  FlexibleLocalDateTimeDeserializer.java
  exception/
    NoviCloudException.java (+ subclasses)
  paging/
    PagedIterable.java
  resources/
    asorty/
    dokumenty/
    formyplatn/
    jmiary/
    kartyloj/
    kasjerzy/
    kasy/
    kontrahenci/
    kraje/
    pozdok/
    rappracy/
    rapsprzed/
    sklepy/
    sprzedaz/
    stanymag/
    stawkivat/
    towary/
    waluty/
```

The refactor was done via IntelliJ Refactor - Move on each package. All imports, `module-info.java`
exports, and demo-app references were updated automatically by the IDE.

## Rationale

- `exception` and `paging` are SDK infrastructure, not endpoint-specific - they belong at the `sdk`
  level, not mixed with resource packages
- `resources` makes the separation explicit in the package tree
- `mvn clean verify` - BUILD SUCCESS after the move, 0 test failures

## Consequences

- Package names for all endpoint client and builder classes changed
  (e.g. `sdk.asorty.AsortyClient` -> `sdk.resources.asorty.AsortyClient`)
- Breaking change - acceptable because `mvn deploy` had not been run before this refactor;
  no external consumers existed at the time
- Future endpoint additions go into `sdk.resources.<endpoint>`
