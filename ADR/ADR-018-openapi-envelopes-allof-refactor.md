# ADR-018: OpenAPI envelopes — allOf refactor and removal of components/responses layer

## Status

Accepted

## Context

Every API response in NoviCloud is wrapped in an envelope with 6 common fields:
`status`, `statusDescription`, `size`, `start`, `onPage`, `links`.

Before this change, `envelopes.yaml` defined 37 `ApiResponse*` schemas, each repeating
these 6 fields verbatim. Adding or renaming a base field required editing 37 places.

`ApiResponseBase` existed in `common.yaml` as the intended base schema, but was never used —
an earlier attempt to apply `allOf` was abandoned because the OpenAPI generator (default config)
was wrapping the composed result in a nested Java object instead of producing flat fields.

Additionally, `components/responses/common.yaml` registered only 5 of the 37 envelopes
as named response components. The remaining 32 were referenced directly from path files
via `$ref: '../components/schemas/envelopes.yaml#/ApiResponseXxx'`, making the responses
layer inconsistent and architecturally pointless.

## Decision

### 1. Enable `useInheritanceInAllOf` in the generator

Added to `pom.xml` `configOptions`:
```xml
<useInheritanceInAllOf>true</useInheritanceInAllOf>
```

With this option the generator performs a **flat merge** for `allOf` schemas:
all properties from the referenced base schema are inlined directly into the child class.
The result is a plain POJO with flat fields — identical to what was generated before,
but now the YAML source is DRY.

Verified behavior: `ApiResponseTowar extends ApiResponseTowar` — does NOT extend `ApiResponseBase`
in Java. The generator copies fields, it does not use Java inheritance. Jackson deserializes
via standard POJO mapping, no `@JsonTypeInfo` or discriminator needed.

### 2. Rewrite `envelopes.yaml` using `allOf`

Each envelope now has only the `data` property (or nothing for `ApiResponseEmpty`):

```yaml
# Before (37 × ~25 lines = ~925 lines):
ApiResponseTowaryList:
  type: object
  properties:
    status: ...
    statusDescription: ...   # x-field-extra-annotation repeated 37 times
    size: ...
    start: ...
    onPage: ...              # x-field-extra-annotation repeated 37 times
    links: ...
    data:
      type: array
      items:
        $ref: '...Towar'
      x-field-extra-annotation: '...'

# After (37 × ~8 lines = ~296 lines):
ApiResponseTowaryList:
  description: Response envelope with a list of products.
  allOf:
    - $ref: './common.yaml#/ApiResponseBase'
    - type: object
      properties:
        data:
          type: array
          items:
            $ref: '../../openapi.yaml#/components/schemas/Towar'
          x-field-extra-annotation: '@com.fasterxml.jackson.annotation.JsonAlias({"dane"})'
```

### 3. Remove `components/responses/common.yaml`

The file provided no value — it was a thin wrapper re-exporting 5 schemas from `envelopes.yaml`
while the other 32 schemas were accessed directly. Removed entirely.

All 320 error response references in 18 path files updated from:
```yaml
$ref: '../openapi.yaml#/components/responses/ApiResponseError'
```
to:
```yaml
$ref: '../components/schemas/envelopes.yaml#/ApiResponseError'
```

The `responses:` section in `openapi.yaml` was removed. Path files now reference
`envelopes.yaml` directly for all responses — both success and error.

## Consequences

- **`envelopes.yaml`** reduced from ~995 to ~370 lines
- Adding a new base field requires editing only `ApiResponseBase` in `common.yaml`
- Generated Java classes are identical in structure to before — flat POJOs, no regression
- `mvn clean verify` green, 0 static analysis violations after change
- `components/responses/` directory is now empty and can be removed if desired
- Path files have one fewer indirection layer for response references
