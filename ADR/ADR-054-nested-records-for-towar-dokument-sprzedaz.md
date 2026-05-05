# ADR-054: SDK records for nested response structures

**Date:** 2026-05-02
**Status:** Accepted (supersedes the deferral in ADR-046)

## Context

ADR-046 (1.0.0) introduced SDK-owned records for top-level response types
(`Towar`, `Dokument`, `Sprzedaz`, etc.) and explicitly deferred wrapping
nested composite arrays. The deferral assumption was that nested types
were "scope limitation, not blocker".

External review (Codex 2026-05-02, findings F-04 / F-12 / F-13 / F-14)
re-classified the deferral as **functional gap**, not scope:

| Field | Endpoint | Why it matters |
|---|---|---|
| `Towar.kody_dod` | towary | Additional barcodes are core POS data; no alternate retrieval path. |
| `Towar.ceny_w_sklepach` | towary | Per-store pricing is required for multi-shop setups. |
| `Towar.skladniki` | towary | Bundle components for product type 5 cannot be inspected. |
| `Dokument.rozbicie_vat` | dokumenty | VAT breakdown needed for fiscal/accounting code paths. |
| `Dokument.platnosci` | dokumenty | Payment breakdown needed for settlements. |
| `Dokument.korekty/faktury/dok_magazynowe/paragony` | dokumenty | Related document references. |
| `Dokument.dok_roliczane` | dokumenty | Settlement details. |
| `Sprzedaz.platnosci` | sprzedaz | Sale payment breakdown. |

Additionally, the modular OpenAPI `TowarCenaWSklepie` schema was missing
the `sklep` link present in real API responses (verified against the
producer PDF and stored live response fixtures). Without `sklep` the
prices array would be ambiguous.

## Decision

### New SDK records (in `sdk.model`)

```
Platnosc                 (shared by Dokument and Sprzedaz)
DokumentRozbicieVat
DokumentRozliczany
TowarKodDodatkowy
TowarCenaWSklepie
TowarSkladnik
TowarSkladnikTowar
```

Each record follows the established pattern (immutable Java record,
clean accessor names, `Link` objects flattened to plain `String` IDs
via `LinkUtils.extractId()`, `from(XRaw)` factory).

### OpenAPI fix

`TowarCenaWSklepie` now includes the `sklep: $ref './common.yaml#/Link'`
property so the generated `TowarCenaWSklepieRaw` exposes the store
identifier. Before this change the spec was demonstrably incomplete
(producer documentation includes `sklep` in the JSON examples).

### Existing records updated

- `Towar` - added `kodyDod()`, `cenyWSklepach()`, `skladniki()`
- `Dokument` - added `rozbicieVat()`, `platnosci()`, `korektyIds()`,
  `fakturyIds()`, `dokMagazynoweIds()`, `paragonyIds()`, `dokRozliczane()`,
  plus `pozycjeId()` and `pozycjeUrl()` to replace the misleadingly-named
  `pozycjeLink()` (which is now `@Deprecated(forRemoval = true)`)
- `Sprzedaz` - added `platnosci()`

Lists are always non-null (empty list when the server omits the field).

## Alternatives considered

- **Document the omission as unsupported:** rejected. The SDK already
  reaches the relevant endpoints; users would need to drop down to raw
  generated types, which the JPMS module does not export.
- **Expose `*Raw` directly:** rejected. Would re-leak the generated
  package and expose `LinkRaw`/enum-with-NUMBER_X names to consumers.
- **Backport `pozycjeLink()` removal to 1.0.x:** rejected. Removing a
  record component is a breaking change; deprecation in 1.1, removal
  in 2.0 is the correct semver path.

## Consequences

- 7 new public records and 1 OpenAPI schema field. All `@since 1.1.0`.
- 2 new integration test assertions per affected endpoint to guard the
  mapping (`single.json` fixtures extended with realistic nested data).
- `Dokument.pozycjeLink()` retains its old behaviour (returns the ID,
  not the URL) for backwards compatibility with 1.0.0 callers, but is
  marked deprecated and will be removed in 2.0.0.
- Future server-side fields surface easily: the generated `*Raw` classes
  already include them; only the SDK record needs a new accessor.

## References

- Codex findings F-04, F-12, F-13, F-14 (context/codex-findings.md)
- ADR-046 (sdk-owned immutable records, deferred nested types)
- ADR-032 (`dane` envelope name)
- Producer documentation: context/RestApi_v_2_10.txt section 6.4
