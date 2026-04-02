# ADR-013: Integer (and string) enum fields in generated models require `.fromValue()` — not direct assignment

## Status

Accepted

## Context

OpenAPI fields typed as `integer` or `string` with a fixed `enum` list generate Java inner enum classes in the openapi-generator `native` library (e.g. `Jmiara.PrecyzjaEnum`, `FormaPlatn.TypEnum`, `StawkaVat.EtykietaEnum`, `KartaLojalnosciowa.PlecEnum`).

The generated setter accepts only the inner enum type, not the raw primitive:

```java
// compile error — setTyp(Integer) does not exist
formaPlatn.setTyp(draft.typ());

// correct
formaPlatn.setTyp(FormaPlatn.TypEnum.fromValue(draft.typ()));
```

The existing `TowarySdk` already followed this pattern with `Towar.TypEnum.fromValue()`. The pattern was re-discovered for `Jmiara.PrecyzjaEnum` and `FormaPlatn.TypEnum` during implementation.

## Decision

All Draft fields corresponding to OpenAPI enum-typed fields use the raw Java type (`Integer` or `String`) for maximum simplicity of the Draft builder API.

The Sdk mapping method (e.g. `toJmiara()`, `toFormaPlatn()`) converts to the generated inner enum via `.fromValue()`:

```java
jmiara.setPrecyzja(Jmiara.PrecyzjaEnum.fromValue(draft.precyzja()));
```

## Affected fields

| Model | Enum field | Generated enum class |
|---|---|---|
| `Towar` | `typ` | `Towar.TypEnum` |
| `Jmiara` | `precyzja` | `Jmiara.PrecyzjaEnum` |
| `FormaPlatn` | `typ` | `FormaPlatn.TypEnum` |
| `StawkaVat` | `etykieta` | `StawkaVat.EtykietaEnum` |
| `KartaLojalnosciowa` | `plec` | `KartaLojalnosciowa.PlecEnum` |

## Consequences

- Draft classes remain simple (no generated type imports in the public SDK surface).
- Sdk mapping methods bear the conversion responsibility.
- Invalid enum values passed by callers will throw `IllegalArgumentException` from `fromValue()` at runtime — acceptable, as these are programming errors.
