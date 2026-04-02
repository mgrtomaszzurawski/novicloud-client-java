# ADR-011: `Link.id` is `type: string`, not integer

## Status

Accepted

## Context

The `Link` object in `openapi/components/schemas/common.yaml` defines the `id` field as:

```yaml
id:
  type: string
```

During initial implementation of `StanMagDraft`, the assumption was made that link ID references are numeric (`Long`), matching the pattern of domain model IDs. This turned out to be incorrect.

Cross-checking with `TowarDraft` (already implemented) confirmed that `jmId` and `asortId` are `String`, not `Long`. All link ID references across all Draft classes must use `String`.

## Decision

All link ID fields in SDK Draft classes (e.g. `krajId`, `jmId`, `asortId`, `walutaId`, `parentId`, `towarId`, `sklepId`) are typed as `String`.

The SDK mapping methods (e.g. `toKontrahent()`, `toSklep()`) create `Link` objects from those String values.

## Affected classes

`StanMagDraft`, `KontrahentDraft`, `SklepDraft`, `KrajeDraft`, `KrajSdk.toKraj()`, `KontrahenciSdk.toKontrahent()`, `SklepySdk.toSklep()`, `StanyMagSdk.toStanMag()`

## Consequences

- All Draft classes have a consistent String type for link IDs.
- No numeric parsing is needed when building `Link` objects.
- Future Draft classes must follow the same convention.
- Callers passing numeric IDs must convert to String themselves (`String.valueOf(id)`).
