# ADR-D007: Builder test subpackages and placement

**Date:** 2026-03-28
**Status:** Accepted

---

## Context

After ADR-025 moved SDK builder classes to per-endpoint subpackages (`sdk.towary`,
`sdk.kontrahenci`, etc.), the demo-app builder tests remained in a single flat package:
`demo.builder`. With 18 endpoints and up to 3 builder types per endpoint (Query, Create,
Update), the flat package would eventually hold 40+ test classes with no structural hint
about which endpoint each test belongs to.

Additionally, coverage was inconsistent: all 18 QueryBuilders had tests, but only 7 of 19
Create/Update builders had tests (some in `novicloud-client/src/test`, some in demo-app,
3 with stale "DraftBuilder" names from before ADR-025).

---

## Decision

### Subpackages per endpoint in demo-app tests

`demo.builder` split into 18 subpackages matching SDK package names:

```
demo.builder.towary/
demo.builder.asorty/
... (18 total, same names as sdk.* subpackages)
```

Each subpackage contains all builder tests for that endpoint: Query, Create, Update.
Endpoints with no Create/Update builders (kasy, kasjerzy, dokumenty, pozdok, sprzedaz,
rapsprzed, rappracy) have only a QueryBuilder test.

This mirrors ADR-025's decision to group by endpoint, making the test structure
self-consistent with the code structure.

### Builder tests stay in demo-app

Builder classes live in `novicloud-client`. Tests for them could logically go there.
Four tests (`TowarCreate/Update`, `AsortCreate`, `JmiaraCreate`) already existed in
`novicloud-client/src/test`.

Decision: keep all builder tests in demo-app, accept minor duplication for the 4 cases.

**Why:**
- Demo-app already had 3 builder test classes (`KontrahentCreate`, `KartaLojCreate`,
  `StanMagUpdate`); moving them to novicloud-client would split related tests across
  modules with no clear benefit
- `novicloud-client/src/test` is the right home for SDK unit tests (RetryHandler, paging,
  exception hierarchy). Builder tests are API contract tests - "does the builder accept
  these fields and return them" - which is consumer-oriented and fits demo-app
- The 4 tests in novicloud-client are not removed; they provide an additional safety net
  at the SDK level and document expected behavior from the SDK author perspective

### Complete coverage for all Create/Update builders

All 19 Create/Update builders now have tests in demo-app (10 Create + 9 Update; no
StanMagCreate and no StawkaVatUpdate per ADR-022 and API structure).

---

## Consequences

### Positive
- Finding tests for an endpoint: one subpackage, all builder types present
- Adding a new endpoint: create a subpackage, add Query + Create + Update tests
- No more "which module has the test for X?" confusion for demo-app work

### Negative
- 4 tests duplicated between demo-app and novicloud-client (TowarCreate/Update,
  AsortCreate, JmiaraCreate) - acceptable, both serve a purpose
- `novicloud-client/src/test` remains inconsistent (has 4 builder tests among SDK tests)
  - not fixed here; low priority

---

## Related

- ADR-025: package per endpoint (motivated this change)
- ADR-D002: DRY tests, builder pattern in tests (original builder test rationale)
