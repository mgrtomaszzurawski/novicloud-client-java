# ADR-033: CUD testing results - hard-delete vs soft-delete behavior

## Status

Accepted

## Context

Live CUD (Create, Update, Delete) testing of all NoviCloud API endpoints that support
write operations. Goal: verify SDK create/update/delete methods work correctly, determine
delete behavior (hard vs soft) for each endpoint, and identify missing fields in the
OpenAPI spec.

### Testing methodology

For each endpoint with write operations:

1. **Create** a test record with all available fields
2. **getById** to verify the created record and all field values
3. **Update** mutable fields with new values
4. **getById** to verify the update
5. **Delete** the record
6. **getById** after delete to determine hard-delete (dane=null) vs soft-delete (dane present, aktywny=false)
7. **Restore** original state for soft-delete endpoints (update with aktywny=true)

### Delete behavior detection

The NoviCloud API returns HTTP 200 for all responses including after delete. The delete type
is determined by the `dane` field in the getById response after delete:
- `dane = null` - record physically removed (hard-delete)
- `dane` present with `aktywny = false` - record deactivated (soft-delete)
- `dane` present with `uniewazniono` date set - card invalidated (kartyloj soft-delete)

## Results

### Hard-delete endpoints (4)

Record is physically removed. getById after delete returns 200 with `dane = null`.

| Endpoint | Create fields | Update fields | Notes |
|----------|--------------|---------------|-------|
| asorty | nazwa (required) | nazwa | parentId needs valid parent |
| jmiary | nazwa (required), precyzja | nazwa, precyzja | |
| stawkivat | id (required, VAT rate), opis, etykieta | N/A | No update - PUT broken server-side (ADR-022). Server auto-formats opis. |
| kraje | nazwa (required), kod (required) | nazwa, kod | walutaId needs valid waluta |

### Soft-delete endpoints with aktywny flag (5)

Record stays in database with `aktywny = false`. Can be reactivated via update with `aktywny = true`.

| Endpoint | Create fields | Update fields | Test ID | Notes |
|----------|--------------|---------------|---------|-------|
| towary | kod (required), nazwa (required) | nazwa, aktywny, +20 more | 4272 | |
| waluty | nazwa (required), kod (required, ISO currency) | nazwa, aktywny | 3 | kod must be valid ISO (e.g. USD) |
| kontrahenci | nazwa (required) | nazwa, aktywny, +15 more | 975 | |
| sklepy | nazwa (required), numer (required) | nazwa, aktywny, +13 more | 978 | |
| formyplatn | nazwa (required), typ (required), reszta | nazwa, typ, reszta, aktywny | 7 | aktywny missing from OpenAPI spec (added in this session) |

### Soft-delete endpoint with uniewazniono (1)

No DELETE operation. Invalidation via PUT with `uniewazniono` date. Reactivation via PUT
with `uniewazniono = null`.

| Endpoint | Create fields | Update fields | Test ID | Notes |
|----------|--------------|---------------|---------|-------|
| kartyloj | kod (required), nazwiskoImie, email | nazwiskoImie, uniewazniono, +15 more | SDK-DEMO-LOJ-001 | email or telefon required for create. Uses kod not numeric id. |

### Read-only endpoints (6)

No write operations available.

| Endpoint | Operations |
|----------|-----------|
| dokumenty | GET |
| pozdok | GET |
| sprzedaz | GET |
| kasy | GET |
| kasjerzy | GET |
| rapsprzed | GET |
| rappracy | GET |

### Special: stanymag (stock levels)

GET + PUT only (no POST/DELETE). Update modifies stock quantity for a towar+sklep pair.
Tested with towar id=4272, sklep id=978: update stock, verify, restore original.

## Decision

### 1. Runner architecture for soft-delete endpoints

Soft-delete endpoints use a two-phase approach:
- `runCreateOnce()` - one-time method to create a test record, commented out after first run
- `runUpdateDelete()` - idempotent method that works on a known test ID:
  reactivates if needed, updates, verifies, deletes, verifies aktywny=false, restores

This avoids creating garbage records on each run.

### 2. Add aktywny to FormaPlatn model

The API returns `aktywny` for formyplatn but it was missing from the OpenAPI spec.
Added `aktywny: boolean` to `formyplatn.yaml` and `aktywny(Boolean)` to
`FormaPlatnUpdateBuilder`.

### 3. Hard-delete endpoints use full CUD cycle

These endpoints create a record, verify, update, verify, delete, and verify dane=null.
Cleanup logic handles leftovers from previously failed runs.

## Consequences

- All 18 endpoints verified against live NoviCloud server
- Delete behavior documented for all endpoints with write operations
- Soft-delete test records persist in the database with known IDs for re-testing
- `formyplatn.yaml` updated with missing `aktywny` field
- `FormaPlatnUpdateBuilder` gains `aktywny(Boolean)` method
- Demo-app runners are idempotent - safe to run repeatedly without creating garbage
