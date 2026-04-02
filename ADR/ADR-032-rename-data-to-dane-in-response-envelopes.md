# ADR-032: Rename `data` to `dane` in response envelopes

## Status

Accepted

## Context

The NoviCloud REST API returns response data in a JSON field named `"dane"` (Polish for "data").
The OpenAPI spec incorrectly defined this field as `data` with a `@JsonAlias({"dane"})` annotation
via `x-field-extra-annotation` to bridge the mismatch.

This worked for list responses (e.g. `ApiResponseTowaryList`) because the property was defined as
`type: array` with `items.$ref`, and the generator correctly applied the annotation.

For single-record responses (e.g. `ApiResponseTowar`, `ApiResponseCreated`) the property used
`$ref` directly. Per OpenAPI 3.0 specification, sibling properties next to `$ref` are ignored.
The generator never generated the `@JsonAlias` annotation on these fields.

### Impact

- `getById().getData()` returned `null` on all 18 endpoints (single-record response)
- `create()` returned `null` id on all endpoints with POST (ApiResponseCreated)
- `listPage().getData()` worked correctly (list response, alias present)

This bug existed since the spec was written. It was never caught because demo-app runners
only used `listPage` results, and `getById` return values were never validated.

## Decision

Rename the property from `data` to `dane` in all response envelopes in `envelopes.yaml`.
Remove all `x-field-extra-annotation` with `@JsonAlias({"dane"})`.

The server sends `"dane"`, so the spec should use `dane`. No alias needed.

### Changes

**OpenAPI spec** (`envelopes.yaml`): all 37 envelopes changed `data:` to `dane:`,
removed `x-field-extra-annotation` lines.

**Generated code** now produces:
```java
@JsonProperty("dane")
private StawkaVat dane;
public StawkaVat getDane() { return dane; }
```

**SDK Clients** (19 files): all `getData()` calls changed to `getDane()`,
all `::getData` method references changed to `::getDane`.

**Demo-app runners** (18 files): all `.getData()` changed to `.getDane()`.

## Rationale

- The spec is the source of truth. It should match what the server sends.
- No workarounds needed (allOf wrappers, post-generation scripts, Jackson mixins).
- Portable to any language SDK - Python, Node, Go generators will get the correct field name.
- Consistent with the API domain language - the entire API uses Polish names
  (nazwa, kod, aktywny, stawka_vat...), so `dane` fits naturally.
- The "breaking change" from `getData()` to `getDane()` has zero real impact because
  `getData()` returned null on single-record responses anyway.

## Consequences

- All generated response envelope classes use `getDane()` instead of `getData()`
- SDK clients and demo-app runners updated to match
- `mvn clean verify` green, 0 static analysis violations on SDK
- All 18 endpoints verified against live server: getById, create, update, delete all
  correctly deserialize data from the `"dane"` field
