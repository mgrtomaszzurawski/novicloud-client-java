# ADR-004: Modular OpenAPI spec and component reuse

## Status

Accepted

## Context

The monolithic `openapi.yaml` became hard to maintain. We need a clear split
of endpoints, parameters, and schemas, with reusable request/response parts.

## Decision

We split the spec into files:

- `paths/*` for endpoints,
- `components/schemas/*` for models,
- `components/parameters.yaml` and `components/securitySchemes.yaml` for shared
  parts,
- root `openapi.yaml` composes everything through `$ref`.

## How shared request/response traits are extracted

- shared response metadata fields are kept in `common` components,
- shared filtering and pagination parameters are kept in `parameters`,
- domain models (for example `Towar`) are reused across responses and
  requests (POST/PUT), with differences mainly in `required`.

## Consequences

- easier maintenance and change review,
- lower duplication and inconsistency risk,
- simpler client generation and targeted debugging.
