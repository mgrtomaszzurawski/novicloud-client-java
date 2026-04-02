# ADR-008: Pragmatic limits for edge-case analysis

## Status

Accepted

## Context

The API contains non-standard and hard-to-reproduce edge cases, including
generic `500` responses caused by backend-side issues.

## Decision

We do not block SDK progress on fully explaining every edge case. We model a
stable and useful contract based on confirmed behavior, while unstable cases
are deferred for iterative refinement.

## Rationale

- the goal is a working client and consistent documentation,
- full analysis of backend `500` responses is often outside SDK team control,
- deeper exploration makes sense after new reproducible examples are provided.

## Consequences

- faster project progress,
- an explicit "to be validated" list instead of artificial assumptions,
- regular ADR review is required when new test data appears.
