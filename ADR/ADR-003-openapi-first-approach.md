# ADR-003: OpenAPI-first approach

## Status

Accepted

## Context

A fully manual client for an extensive REST API creates a high maintenance
cost: lots of boilerplate, model inconsistency risk, and slower response to
contract changes.

## Decision

The OpenAPI specification is the source of truth, and HTTP client code is
generated automatically.

## Alternatives and comparison

1. Manual HTTP client
   - plus: full control,
   - minus: highest maintenance cost and highest API drift risk.
2. Ad hoc JSON mapping (without a formal contract)
   - plus: fast start,
   - minus: no validatable contract and poor scalability.
3. OpenAPI-first (chosen)
   - plus: consistent contract, generated models/APIs, easier evolution,
   - minus: dependency on spec quality and generator limitations.

## Consequences

- work focuses on `openapi.yaml` quality,
- API changes can be reproduced and regenerated,
- the SDK overlay can stay thin and stable.
