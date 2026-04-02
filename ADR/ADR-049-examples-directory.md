# ADR-049: Standalone examples directory for SDK consumers

## Status

Accepted

## Context

`demo-app` is an internal smoke test harness: 18 runners, RunReport, DemoSession - ~1800 lines
of infrastructure. It's good for verifying all endpoints work, bad as a "how do I use this SDK?"
reference. A new user looking for "how do I list products?" has to dig through runner abstractions,
logging setup, and test plumbing before finding the actual API call.

AWS SDK, Google Cloud SDK, and Stripe Java all ship a top-level `examples/` directory with
standalone files that a user can read or copy into their project.

## Decision

Create `examples/` as a plain directory of standalone `.java` files with a README. Not a Maven
module - no pom.xml, no tests, no build integration.

### Files

| File | What it shows |
|------|---------------|
| `ListProducts.java` | Client creation, QueryBuilder filters, lazy iteration, close() |
| `CreateAndUpdateProduct.java` | Full CRUD lifecycle, try-with-resources, soft-delete |
| `HandleErrors.java` | Exception hierarchy, catch order, status codes |
| `CustomRetryPolicy.java` | RetryPolicy builder, backoff strategy, disabled retry |
| `PaginateAllRecords.java` | Lazy pagination, collect to List, early termination |
| `WorkWithReports.java` | Report endpoints (rapsprzed, rappracy), date range, grupowanie |
| `README.md` | How to compile/run, or just read as reference |

### Why not a Maven module

- Examples should be zero-friction; no pom.xml to understand
- A user copies one file into their project and it works
- No tests, no CI integration, no build coupling
- AWS SDK examples follow the same pattern

### What stays in demo-app

Everything. demo-app is the internal test harness - 18 runners, CUD methods, RunReport. It stays
as-is and is not published (`maven.deploy.skip=true`). The `examples/` directory is what a new
user should read first.

## Consequences

- SDK ships with copy-paste-ready examples in `examples/`
- demo-app remains unchanged (internal tool)
- Examples use `System.out` (no SLF4J, no logging framework)
- Examples use env vars for credentials (no hardcoded values)
- Each file is self-contained with all necessary imports
