# ADR-D003: CRUD lifecycle pattern in endpoint runners

## Status

Accepted

## Context

Runners for CRUD-capable endpoints (towary, asorty, jmiary, waluty, kraje, formyplatn, kontrahenci, sklepy, kartyloj) need to test create, update, and delete without leaving test data behind in the live account.

Two approaches were considered:

1. **Fixed known IDs** — update/delete a record that already exists. Risk: the record may not exist, or deleting it causes data loss.
2. **Self-contained lifecycle** — create a test item, exercise all write operations on it, delete it in the same run.

## Decision

Each CRUD runner follows this lifecycle pattern:

```
create(test draft) → [on success] updateById(id) → update(collection form, id) → deleteById(id)
```

Each step is wrapped in `ctx.require()` or `ctx.check()`. If create fails (e.g. 402 subscription error, 400 validation), all dependent steps are recorded as SKIP with an explanatory reason. The runner does not abort — subsequent read-only checks still execute.

The created ID is extracted from the `String` returned by `sdk.xxx().create()` (which returns `Link.id`, a String per ADR-011). It is parsed to `Long` for path-param operations. If parsing fails, dependent steps are skipped.

### Test data naming

Test items use recognizable prefixes:
- `kod`: `SDK-DEMO-T-001`, `SDK-DEMO-LOJ-001`
- `nazwa`: `SDK Demo Towar`, `SDK Demo Kontrahent`, etc.

This makes demo-run artifacts identifiable and manually cleanable if the runner fails before deleting.

### StanyMag exception

StanyMag update operations are skipped in the runner because modifying warehouse stock has real business consequences. The skip message instructs the user to run update calls manually with known IDs.

### StawkiVat exception

StawkiVat create/update/delete are skipped because stawki VAT are system reference records (numeric VAT rate codes). Modifying them corrupts reference data used by all other resources.

## Consequences

- Each runner run leaves no permanent test data if all steps succeed
- CRUD methods (create, updateById, update collection form, deleteById) are all exercised
- Failures in CRUD steps do not prevent read-only checks from running
- The runner output clearly separates read failures from write failures
