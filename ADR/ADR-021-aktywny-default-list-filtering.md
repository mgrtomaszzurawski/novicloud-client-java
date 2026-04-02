# ADR-021: No default `aktywny` filter on list queries

## Status

Accepted

## Context

Several NoviCloud resources support soft delete via an `aktywny` (active) flag.
For these resources, `DELETE` sets `aktywny = false`; the row is never physically removed.

The customer web panel hides inactive rows by default in most list screens.
An SDK caller who issues `client.towary().listAll(null)` gets both active and inactive
records, which differs from what the panel shows. After a logical delete, the record still
appears in unfiltered lists. This can surprise integrators who expect "deleted means gone
from list".

### Delete behavior per resource (live-verified 2026-03-22)

**Soft delete** - `DELETE` sets `aktywny = false`, row remains in the database:

| Resource | QueryBuilder `aktywny` filter |
|---|---|
| `/towary` | yes |
| `/kontrahenci` | yes |
| `/sklepy` | yes |
| `/waluty` | yes |
| `/formyplatn` | no (server has `aktywny` but QueryBuilder does not expose it) |

**Hard delete** - `DELETE` physically removes the row:

| Resource |
|---|
| `/asorty` (standalone; parent blocked while children exist) |
| `/jmiary` |
| `/stawkivat` |
| `/kraje` |

**No DELETE operation** - `/f-karty-loj`:
Loyalty cards are a separate module that does not follow the `aktywny` convention.
There is no DELETE endpoint. Deactivation is done via `PUT` with the `uniewazniono`
field set (invalidation timestamp). Card validity is controlled by `waznaOd` / `waznaDo`
date range fields.

**Unverified** - `/kasy`, `/kasjerzy`:
Both expose an `aktywny` filter in their QueryBuilders. Delete behavior (soft vs hard)
has not been confirmed on live. The `aktywny` filter Javadoc uses neutral language.

## Decision

**Do not add a default `aktywny = true` filter in any `*QueryBuilder`.**

All `*QueryBuilder` builders remain filter-free by default. Callers who want active-only
results must set the filter explicitly:

```java
TowarQueryBuilder activeOnly = TowarQueryBuilder.builder().aktywny(true).build();
```

## Rationale

1. **SDK is a transport layer.** Defaulting a filter would make the SDK second-guess what
   data the caller needs, which is a business-layer concern.

2. **Sync and audit tools need inactive records.** A migration or audit processing all rows
   would be silently broken by an opt-out default. Opt-in is safe; opt-out is dangerous.

3. **Symmetry across resources.** Some endpoints have no `aktywny` at all. Applying a
   default only to a subset creates an inconsistent mental model for SDK users.

4. **Documentation is the right tool.** The mismatch between API and panel defaults is
   documented in Javadoc on `aktywny()` accessors and `deleteById()` methods in the
   affected classes, not hidden by library behaviour.

## Consequences

- Callers must explicitly add `.aktywny(true)` to get panel-like active-only lists.
- `deleteById()` Javadoc on soft-delete clients states that the row is not physically
  removed and will still appear in unfiltered list results.
- For `/f-karty-loj`: no DELETE exists; deactivation is via `PUT` with `uniewazniono`.
- No changes to filtering behaviour; this ADR records the deliberate decision not to
  change it.
