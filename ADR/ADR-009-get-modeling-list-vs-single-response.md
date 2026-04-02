# ADR-009: GET modeling as `XxxListResponse` and `XxxResponse`

## Status

Accepted

## Context

During analysis, we considered unifying GET responses into one list type.
After correcting tests and validating endpoints, we returned to separate
responses for collection and single-resource access.

## Decision

We keep two response variants:

- `XxxListResponse` for collections,
- `XxxResponse` for single-resource responses.

Both variants reuse the same domain model (for example `Towar`), and the
`dane` field can be optional for "no data" cases.

## Consequences

- a clearer client contract and lower semantic confusion risk,
- preserved domain model consistency across GET/POST/PUT,
- better handling of "no data" scenarios without misusing HTTP errors.
