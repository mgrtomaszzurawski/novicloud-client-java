# ADR-020: CreateDraft / UpdateDraft - separate classes for POST and PUT

## Status

Accepted

## Context

POST endpoints in the NoviCloud API require a minimum set of fields to return `201 Created`.
Requirements were verified by manual Postman testing against a live account
(see `api-verification/novicloud-rest/verification-document.md`).

Initial decision: a single `*Draft` class with required fields enforced in the builder constructor.
Problem: forcing POST-required fields onto the Draft class also used by PUT is a time bomb.
PUT does a partial update and requires nothing except the record identifier. A developer using
the SDK calls `update()`, sees a `*CreateDraft` argument, and cannot understand why they must
supply `kod` and `nazwa` just to change one field. That is a broken API design.

Final decision (Option D): separate `*CreateDraft` and `*UpdateDraft` classes.

## Decision

### CreateDraft

Class for resource creation (POST). Required fields enforced in the builder constructor:

```java
// POST /towary - kod and nazwa required
TowarCreateDraft draft = TowarCreateDraft.builder("ABC-001", "Towar testowy")
    .stawkaVat(2300)
    .aktywny(true)
    .build();
sdk.towary().create(draft);
```

### UpdateDraft

Class for resource update (PUT with id in body - collection form). `id` is the only
required field in the builder constructor. All other fields are optional.

```java
// PUT /towary with id in body
TowarUpdateDraft draft = TowarUpdateDraft.builder(42L)
    .nazwa("New name")   // only fields being changed
    .build();
sdk.towary().update(draft);
```

### Removal of updateById

The `updateById(Long id, *Draft)` method is removed from all `*Sdk` classes.
The SDK uses only the collection PUT form (`PUT /{resource}`) with id in the body.
See PUT semantics verification below.

---

## Required fields per endpoint (verified on live API)

| Endpoint | CreateDraft | Required for CREATE | UpdateDraft | Required for UPDATE |
|---|---|---|---|---|
| `/towary` | `TowarCreateDraft` | `kod` (String), `nazwa` (String) | `TowarUpdateDraft` | `id` (Long) |
| `/asorty` | `AsortCreateDraft` | `nazwa` (String) | `AsortUpdateDraft` | `id` (Long) |
| `/jmiary` | `JmiaraCreateDraft` | `nazwa` (String) | `JmiaraUpdateDraft` | `id` (Long) |
| `/kontrahenci` | `KontrahentCreateDraft` | `nazwa` (String) | `KontrahentUpdateDraft` | `id` (Long) |
| `/sklepy` | `SklepCreateDraft` | `nazwa` (String), `numer` (Integer) | `SklepUpdateDraft` | `id` (Long) |
| `/waluty` | `WalutaCreateDraft` | `nazwa` (String), `kod` (String) | `WalutaUpdateDraft` | `id` (Long) |
| `/kraje` | `KrajCreateDraft` | `nazwa` (String), `kod` (String) | `KrajUpdateDraft` | `id` (Long) |
| `/formyplatn` | `FormaPlatnCreateDraft` | `nazwa` (String), `typ` (Integer) | `FormaPlatnUpdateDraft` | `id` (Long) |
| `/f-karty-loj` | `KartaLojCreateDraft` | `kod` (String) | `KartaLojUpdateDraft` | `kod` (String) |
| `/stanymag` | none (PUT only) | - | `StanMagUpdateDraft` | `towarId` (String), `sklepId` (String), `ilosc` (Double) |
| `/stawkivat` | GAP - not implemented | - | GAP - not implemented | - |

### Per-endpoint notes

**`/waluty`:** `kod` is validated by the server against real ISO 4217 currency codes (e.g. AED, EUR, PLN).
Test values must use existing currency codes.

**`/formyplatn`:** System allows only one record with the default cash type.
Both fields always required.

**`/f-karty-loj`:** POST not verified on live API. Domain knowledge: `kod` is the loyalty card
identifier - required both for POST (new record) and PUT (record identification).
This endpoint has no DELETE.

**`/stanymag`:** GET and PUT only. `towarId` and `sklepId` are `String` (Link.id = String, ADR-011).
Three URL variants exist (base / byTowar / byTowarAndSklep) - SDK uses only the base endpoint
`PUT /stanymag` with `towarId` and `sklepId` in the body. Methods `updateByTowar` and
`updateByTowarAndSklep` are removed from `StanyMagSdk`.

**`/stawkivat`:** CRUD partially implemented in SDK but never tested.
`StawkaVatDraft` exists with required `id` (Integer - not Long, see CLAUDE.md type drift).
Full CreateDraft/UpdateDraft implementation deferred to a separate session.

---

## PUT semantics - findings from the same test session

- **Partial update:** PUT updates only fields present in the body. Absent fields are unchanged.
- **`null` and `""` are ignored:** sending `"nazwa": null` or `"nazwa": ""` does NOT clear the
  field. Server returns 200, field remains unchanged. Clearing a field is not supported via REST.
- **Collection PUT requires `id` in body:** `PUT /{resource}` without `id` returns
  `400 "Blad w skladni zapytania - blad id zasobu"`.
- **URL PUT ignores `id` in body:** `PUT /{resource}/{id}` - any `id` field in the body is
  silently ignored; the URL `id` is used.
- **Unknown field causes 500:** server returns raw `UnrecognizedPropertyException` text (not JSON).
  The SDK must never send fields not in the schema.
- **Invalid field type causes 500:** raw `InvalidFormatException`. The Java type system prevents
  this at compile time.

---

## Consequences

- Double the number of Draft classes - more files, but a cleaner API for SDK consumers.
- `create()` and `update()` in `*Sdk` have different argument types - misuse is a compile error.
- Existing calls in demo-app runners must be updated - breaking change.
- Builder tests in `demo.builder` must be updated.
- StawkaVat CRUD remains with temporary `StawkaVatDraft` class - known gap.
