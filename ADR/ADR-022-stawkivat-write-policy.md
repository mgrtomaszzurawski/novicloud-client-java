# ADR-022: StawkiVat write policy - CREATE and DELETE only, no UPDATE

## Status

Accepted

## Context

Live API verification against the NoviCloud server (2026-03-22) revealed the following
behavior for `/stawkivat` write operations:

- **POST `/stawkivat`** - works correctly, returns `201 Created` with the new rate id.
- **DELETE `/stawkivat/{id}`** - works correctly, hard-deletes the row from the database.
- **PUT `/stawkivat`** (collection form) and **PUT `/stawkivat/{id}`** - both fail with
  HTTP 500 and a server-side `ClassCastException`:

```
class java.lang.Short cannot be cast to class java.lang.Long
```

  No request variant was found that successfully persisted an update. The failure is in the
  server implementation, not in the client payload. The server also returns HTTP 500 with
  `"Brak stawki vat"` on `PUT /{id}` even when `GET /{id}` for the same id succeeds.

**Root cause analysis (black-box, high confidence):**
Additional live testing confirmed that the server stores the VAT rate `id` field internally
as a 16-bit integer (range -32,768 to 32,767). VAT rate codes such as 2300 (23%) fit
comfortably in this range. The GET path reads the field correctly and serializes it to JSON.
The PUT path fails due to a server-side type casting bug that has no client-side workaround.
Every other endpoint in the NoviCloud API uses `Long` for its id field. The narrower type
on `/stawkivat` is an isolated anomaly specific to this resource.

The previous SDK state had a single `StawkaVatDraft` class used for both POST and PUT.
ADR-020 deferred splitting this class into `StawkaVatCreateDraft` / `StawkaVatUpdateDraft`
as a known gap.

## Decision

1. **Create `StawkaVatCreateDraft`** with `id` (Integer, required) as the rate code
   (e.g., 2300 for 23%) and `opis` / `etykieta` as optional fields. This supports the
   working POST path.

2. **Do not create `StawkaVatUpdateDraft`.** Since PUT is broken server-side and no fix
   is available on the client side, providing an update draft class would only produce
   confusing HTTP 500 errors. Intentional omission is preferable to a method that always
   fails.

3. **Remove `update()` and `updateById()` from `StawkiVatSdk`.**
   `StawkiVatSdk` exposes only: `listPage`, `listAll`, `count`, `getById`, `create`,
   `deleteById`.

4. **Delete `StawkaVatDraft`** (the old temporary gap class).

## Rationale

- Providing an API that always produces a server error misleads callers and wastes time.
- The method's absence is a clear signal that the operation is not supported.
- When the vendor fixes the server-side type bug, `StawkaVatUpdateDraft` can be added in
  a new minor version without breaking existing callers.
- VAT rate records are typically system reference data rarely changed via REST;
  the panel is the recommended path for updates until the server bug is resolved.

## Consequences

- `StawkiVatSdk.update()` and `StawkiVatSdk.updateById()` no longer exist.
- Callers who need to change a VAT rate must use the web panel until the server is fixed.
- `StawkaVatDraft` is deleted; any code that was using it must migrate to
  `StawkaVatCreateDraft` for POST or drop update calls entirely.
- `ARCHITECTURE.md` updated: stawkivat row now shows `StawkaVatCreateDraft` / no UpdateDraft.
