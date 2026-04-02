# ADR-046: SDK-owned immutable records for public API

**Date:** 2026-03-31
**Status:** Accepted

## Context

The SDK returned OpenAPI-generated mutable classes (e.g. `Towar`, `Asorty`) directly from
public methods like `getById()` and `list()`. These classes:

- Are mutable POJOs with setters (consumers can accidentally modify response objects)
- Expose internal Link objects (`getJm()` returns `Link` with `id` and `href`)
- Use generated enum types (`TowarRaw.TypEnum.NUMBER_0`) - opaque names
- Couple consumers to generator output - any regeneration can break downstream code
- Expose `client.model` package in `module-info.java`

Initially rejected as YAGNI (Phase 4 of cleanup plan). Reversed after supplementary audit
(F-35) and discussion: the Link unwrapping alone justified the change, records are cheap
to add, and pre-v1.0.0 is the only chance to do this without breaking consumers.

## Decision

1. **modelNameSuffix: Raw** on the OpenAPI generator. All generated classes get `Raw` suffix
   (`TowarRaw`, `AsortyRaw`, etc.). Package stays `client.model`.

2. **18 immutable Java records** in `sdk.model` with clean names (`Towar`, `Asorty`, etc.).
   Each record has a `static X from(XRaw raw)` factory method.

3. **Link unwrapping**: generated `Link` / `Object` fields become plain `String` IDs
   in the record (e.g. `jmId()`, `asortId()`). Consumers who need the linked entity
   call the corresponding client: `client.jmiary().getById(Long.parseLong(towar.jmId()))`.

4. **SDK-owned enums** with `fromCode()` factory instead of generated `NUMBER_0`-style names.
   Enum values use `VALUE_0` (numeric) or literal names (`A`, `B`, `K`, `M`).

5. **MappingIterable** in `sdk.paging` wraps `PagedIterable<XRaw>` and lazily maps each
   element via `X::from`. No buffering.

6. **`client.model` no longer exported** in `module-info.java`. Only `sdk.model` is public.
   Jackson still has `opens` access for deserialization.

7. **Nested composite types** (e.g. `DokumentRozbicieVat`, `Platnosc`, `TowarSkladnik`)
   are NOT wrapped in records in v1.0.0. They are omitted from the record fields.
   Can be added in a future version if needed.

## Consequences

- All public API methods return immutable records
- Record accessor pattern: `towar.nazwa()` not `towar.getNazwa()`
- Generated types hidden from consumers (no `exports client.model`)
- Adding fields to records is source-compatible (new accessor appears, existing code compiles)
- Consumers cannot construct response records with `new Towar(...)` meaningfully (no use case)
- F-36 (KartyLojClient.getByKod) and F-37 (StanyMagClient special methods) fixed as part
  of this change - all public methods now return records instead of raw envelopes
