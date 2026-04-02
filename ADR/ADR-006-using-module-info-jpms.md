# ADR-006: Using `module-info.java` (JPMS)

## Status

Accepted

## Context

We want to control which packages are public and which remain internal
(for example generated client internals).

## Decision

We use JPMS and keep `module-info.java` in modules to:

- export only the public SDK API,
- hide internal packages and reduce coupling,
- enforce clear modular boundaries.

## Consequences

- stronger project discipline,
- easier maintenance of SDK API binary compatibility,
- need to maintain exports carefully during development.
