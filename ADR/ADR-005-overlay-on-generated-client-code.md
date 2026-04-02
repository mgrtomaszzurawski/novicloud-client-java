# ADR-005: Overlay on generated client code

## Status

Accepted

## Context

Generated code should not be the only public layer. In practice:

- generated models may be unstable when the spec changes,
- `required` validation does not provide the expected compile-time safety,
- we need ergonomic API usage and consistent exception handling.

## Decision

We build an SDK layer on top of generated code:

- thin DTO/request wrappers,
- mapping to/from generated models,
- custom domain exceptions and consistent error messages,
- convenient entry points for business operations (for example `TowarySdk`).

## What we plan to add and why

- manual request validations for business constraints,
- builders/wrappers for better ergonomics,
- iteration over paginated results (`PagedIterable`-like),
- a stable SDK API contract independent of internal generator changes.

## Consequences

- small implementation overhead,
- stronger SDK resilience to generated class changes,
- better DX (developer experience) for library consumers.
