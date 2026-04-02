# ADR-012: Explicit `null` for FtsQuery in Sdk classes that do not expose full-text search

## Status

Accepted

## Context

The generated API methods for endpoints that include `FtsQuery` (full-text search) have it as the **3rd positional parameter** after `start` and `onPage`. For example:

```java
api.listKontrahenci(accountName, start, onPage, fts, id, nazwa, ...)
```

The SDK Query builders for some domains deliberately do not expose `fts` as a filter option (e.g. `KasyQuery`, `KasjerzyQuery`, `DokumentyQuery`, `PozdokQuery`, `SprzedazQuery`, `KartyLojQuery`). Reasons vary: the filter is not useful for those resources given their typical access patterns, or the resource is read-only with narrow filter needs.

If `null` is not explicitly passed for the `fts` slot, all remaining parameters shift one position and silently map to the wrong API query parameters. This type of bug does not fail at compile time and produces incorrect filter results at runtime.

## Decision

SDK classes that do not expose `fts` in their Query class must pass `null` explicitly for the FtsQuery positional argument in the generated API call:

```java
api.listKasy(accountName, safe.start(), safe.onPage(), null,
        safe.id(), safe.nazwa(), ...);
```

SDK classes that expose `fts` in their Query class pass it normally:

```java
api.listWaluty(accountName, safe.start(), safe.onPage(), safe.fts(), safe.id(), ...);
```

## Endpoints with FtsQuery (all require explicit null or fts value)

With fts exposed in Query: towary, asorty, jmiary, stawkivat, waluty, kraje, kontrahenci, sklepy.
With null injected: formyplatn, kasy, kasjerzy, dokumenty, pozdok, sprzedaz, kartyloj.
Without FtsQuery at all (no null needed): stanymag, rapsprzed, rappracy.

## Consequences

- Correct parameter binding for all SDK list calls.
- Adding `fts` to a Query class in the future only requires removing the `null` injection and wiring through the builder field — no API method signature changes.
- Anyone adding a new Sdk class must check the path YAML for FtsQuery presence before writing the `api.listXxx(...)` call.
