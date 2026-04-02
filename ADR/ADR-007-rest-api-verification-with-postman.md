# ADR-007: REST API verification with Postman

## Status

Accepted

## Context

Vendor documentation and real API behavior are not always aligned. We needed
a fast way to empirically validate the contract.

## Decision

We use Postman plus archived JSON samples as the primary source for endpoint
behavior verification.

## Findings

- some responses and HTTP codes differ from the documented behavior,
- the "no data" case can return `200` with a specific `status_opis`,
- API quality and predictability require cautious error modeling,
- OpenAPI and SDK decisions are based on tested data, not assumptions.

## Consequences

- OpenAPI documentation is updated iteratively after each test batch,
- we maintain a list of missing cases to collect next.
