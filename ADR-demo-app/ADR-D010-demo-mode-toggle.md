<!--
Copyright (c) 2026 Tomasz Zurawski
SPDX-License-Identifier: AGPL-3.0-only
https://github.com/mgrtomaszzurawski/novicloud-client-java
-->
# ADR-D010: Demo-app run mode toggle

**Date:** 2026-04-01
**Status:** Accepted
**Relates to:** DESIGN-demo-mode-toggle.md

## Context

CUD methods in demo-app runners were commented out with `//`. No toggle mechanism,
no visibility in output, easy to forget which are safe to run. Soft-delete endpoints
create permanent records - running CREATE repeatedly spams the database.

## Decision

Four explicit run modes controlled by `demo.mode` property in `application.properties`:

- **READ_ONLY** (default) - all runners GET only, CRUD logged as [SKIP]
- **CRUD_SAFE** - hard-delete endpoints full CRUD cycle (idempotent), soft-delete GET only
- **CREATE_SOFT** - creates one test record per soft-delete endpoint, saves IDs to file
- **CRUD_ALL** - full CRUD on all writable endpoints, soft-delete uses saved IDs

### Architecture

**Mode injection via constructor** - each runner receives `DemoMode` at construction time.
`EndpointRunner` interface unchanged, `DemoSession` unchanged. Boolean flags in `run()`
control which methods execute: `boolean read`, `boolean create`, `boolean crud`.
Methods accept `boolean enabled` parameter; if false, silently return (read ops) or
log [SKIP] (write ops).

**Immutable ID reader + mutable collector pattern:**
- `SoftDeleteIds` - immutable, loaded from `demo-soft-delete-ids.properties`, passed to
  runners for CRUD_ALL mode (read saved IDs)
- `SoftDeleteIdsCollector` - mutable, used only in `NoviCloudDemoApp.collectAndSaveIds()`
  after all runners finish. Never passed to runners.
- `CreatesTestRecord` interface - soft-delete runners implement it, exposing `idsKey()`
  and `createdId()`. Main app collects IDs after `runAll()` via `instanceof` check.

This avoids shared mutable state between runners (no EI_EXPOSE_REP2 SpotBugs warning,
no exclusions needed).

**Startup validation via exceptions** - `parseMode()` and `loadIds()` throw
`IllegalStateException` on invalid config. No `System.exit()` in new code.

### File guards

- CREATE_SOFT: if `demo-soft-delete-ids.properties` exists, throw (prevents orphaning old records)
- CRUD_ALL: if file missing, throw (must run CREATE_SOFT first)

### Runner categories

| Category | Endpoints | CRUD_SAFE | CREATE_SOFT | CRUD_ALL |
|----------|-----------|-----------|-------------|----------|
| Hard-delete (4) | asorty, jmiary, kraje, stawkivat | Full CRUD | [SKIP] | Full CRUD |
| Soft-delete aktywny (5) | towary, waluty, kontrahenci, sklepy, formyplatn | [SKIP] | Create + save ID | Update + Delete from saved ID |
| Soft-delete uniewazniono (1) | kartyloj | [SKIP] | Create + save kod | Update + Invalidate from saved kod |
| Read-only (8) | dokumenty, pozdok, sprzedaz, kasy, kasjerzy, stanymag, rapsprzed, rappracy | GET only | [SKIP] | GET only |

## New files

- `DemoMode.java` - enum (READ_ONLY, CRUD_SAFE, CREATE_SOFT, CRUD_ALL)
- `SoftDeleteIds.java` - immutable reader, `load(Path)`, `get(key)`
- `SoftDeleteIdsCollector.java` - mutable writer, `put(key, value)`, `save(Path)`
- `CreatesTestRecord.java` - interface for soft-delete runners

## Modified files

- `AppProperties.java` - parses `demo.mode` property
- `NoviCloudDemoApp.java` - startup validation, mode-based runner construction, ID collection
- `EndpointRunner.java` - `throws NoviCloudException` (was `throws Exception`)
- `RunnerHelper.java` - `logModeSkip()` for [SKIP] messages
- `DemoSession.java` - extracted `runner.name()` to local variable (S2629)
- All 18 runners - constructor accepts DemoMode, boolean flags, no commented-out CUD
- `application.properties` - `demo.mode=READ_ONLY`
- `.gitignore` - `demo-soft-delete-ids.properties`

## Tests

- `DemoModeTest` - enum values, valueOf, rejects old CUD names
- `SoftDeleteIdsTest` - load, get, summary, missing file
- `SoftDeleteIdsCollectorTest` - put, save, roundtrip with SoftDeleteIds.load
- `AppPropertiesTest` - demo.mode parsing

## Consequences

- No more commented-out CUD code in runners
- Safe to run against live server by default (READ_ONLY)
- Soft-delete test records managed via file, not hardcoded IDs
- SonarQube: 0 code smells (was 92 before mode toggle, 58 after initial implementation)
