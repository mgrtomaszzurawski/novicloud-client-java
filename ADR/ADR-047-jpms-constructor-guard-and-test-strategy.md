# ADR-047: JPMS as constructor guard + cross-cutting test strategy

**Date:** 2026-03-31
**Status:** Accepted

## Context

### Public constructors on resource clients (F-40)

All 18 resource client constructors (e.g. `TowaryClient(ApiClient, String, RetryPolicy)`)
are `public`. The audit flagged this as HIGH - consumers could bypass the `NoviCloudClient`
facade by constructing clients directly.

Making constructors package-private is not possible because `NoviCloudClient` lives in
`sdk` package while each client lives in its own sub-package (e.g. `sdk.resources.towary`).
Java package-private visibility does not extend to sub-packages.

Alternatives considered:
- Move all clients to same package as `NoviCloudClient` - flat package, poor organization
- Factory pattern with package-private token - over-engineering for the actual risk
- Extract interfaces for each client - doubles the class count for no consumer benefit

### Cross-cutting test coverage (F-06, F-07)

The audit requested error scenario tests (401/404/429/500) and pagination tests for all
18 integration test classes. Currently these are tested only on `StawkiVatClientIntegrationTest`.

## Decision

### JPMS guards constructors

The `client` package (containing `ApiClient`) is NOT exported in `module-info.java`.
Only `opens client.model to com.fasterxml.jackson.databind` exists for deserialization.

Consumers cannot obtain an `ApiClient` instance, so they cannot call
`new TowaryClient(apiClient, ...)` even though the constructor is public.
The JPMS module boundary is the guard - no code change needed.

### Cross-cutting tests on representative endpoints

Error scenarios (401/404/429/500) and pagination use shared infrastructure:
- Error mapping: `NoviCloudException.of()` in `RetryHandler`
- Pagination: `PagedIterable` + `LinkFetcher`

Testing the same shared code path 18 times adds maintenance burden without coverage value.
Instead, tests are on 3 representative clients:

| Client | Pattern | Tests |
|--------|---------|-------|
| StawkiVat | CRUD + hard delete | error (4) + retry (1) + pagination (1) |
| Towary | CRUD + soft delete + links | error (4) + pagination (1) |
| Dokumenty | Read-only | error (4) |

This covers both CRUD and read-only patterns with different response shapes.

## Consequences

- Resource client constructors stay `public` - no refactoring needed
- Consumers on classpath (no JPMS) could technically bypass the facade;
  this is acceptable for v1.0.0 (documented as internal API)
- New cross-cutting behavior (e.g. new exception type) only needs tests
  on the 3 representative clients, not all 18
- Builder edge case tests (F-09) in `BuilderEdgeCaseTest` cover representative
  builders from different categories (query, create, special)
