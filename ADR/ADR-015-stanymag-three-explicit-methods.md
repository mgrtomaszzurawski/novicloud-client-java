# ADR-015: StanyMag Sdk exposes three explicit methods for three path variants

## Status

Accepted

## Context

The `/stanymag` endpoint has three distinct path forms:

| Path | Meaning |
|---|---|
| `/stanymag` | All warehouse stock records (collection) |
| `/stanymag/{id_towaru}` | Stock for one product across all shops |
| `/stanymag/{id_towaru}/{id_sklepu}` | Stock for one product in one shop |

Each path variant maps to a separate generated API method: `listStanyMag`, `listStanyMagByTowar`, `getStanMagByTowarAndSklep`.

The optional `na_dzien` (date as of) query param appears on the second and third variants but not the first.

Two design options were considered:

1. **Overloaded `list(...)` methods** — callers infer semantics from which parameters they pass null.
2. **Three explicitly named methods** — `listPage`/`listAll`/`count` for the collection, `listByTowar(Long, String)` for by-product, `getByTowarAndSklep(Long, Long, String)` for the single-record case.

## Decision

Three explicitly named methods are used, each with a clear signature:

```java
// collection — uses StanyMagQuery (towarId, sklepId, naDzien as filters)
Iterable<StanMag> listAll(StanyMagQuery query)

// by product — naDzien is a dedicated String param
Iterable<StanMag> listByTowar(Long idTowaru, String naDzien)

// by product + shop — returns a single record wrapped in ApiResponseStanMag
ApiResponseStanMag getByTowarAndSklep(Long idTowaru, Long idSklepu, String naDzien)
```

Update variants mirror the same three paths:

```java
void update(StanMagDraft draft)
void updateByTowar(Long idTowaru, StanMagDraft draft)
void updateByTowarAndSklep(Long idTowaru, Long idSklepu, StanMagDraft draft)
```

## Consequences

- Each method has unambiguous semantics regardless of which parameters are null.
- The public SDK surface clearly communicates which path variant is being used.
- `StanyMagQuery` still supports `towarId` and `sklepId` as cross-cutting filters on the collection endpoint.
- More methods to maintain if the API adds new path variants — acceptable given the API is stable.
