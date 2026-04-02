# ADR-001: Separate SDK and demo app

## Status

Accepted

## Context

We need both:

- a stable library to be used by other projects,
- a place for quick integration testing and debugging.

Combining both goals in one module makes library API maintenance harder and
increases the risk of accidentally leaking test code into the public surface.

## Decision

We keep separate modules:

- `codex-openapi-novicloud-client` as the SDK/library,
- `codex-demo-app` as a demonstration and integration app.

## Consequences

- a clean library contract and independent SDK API versioning,
- faster validation against the real API without polluting the SDK,
- simpler manual testing and issue reproduction.
