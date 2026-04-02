# ADR-010: Error response standardization (especially 400)

## Status

Accepted

## Context

Error responses were described inconsistently across endpoints. Real payloads
(especially `400`) showed a structure closer to a details map than to simple
message lists.

## Decision

We standardize error schemas in shared components and map
(`400`, `401`, `402`, `410`, `500`, `501`) to consistent response models.

## Consequences

- predictable exception handling in the SDK,
- fewer special cases in demo and integration code,
- easier extension to additional endpoints.
