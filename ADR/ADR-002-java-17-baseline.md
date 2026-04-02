# ADR-002: Java 17 baseline

## Status

Accepted

## Context

The project must stay stable, modern, and free from outdated stack
dependencies. We also need compatibility with `java.net.http.HttpClient`,
strong tooling support, and predictable generator behavior.

## Decision

Java 17 is the minimum version for SDK and demo modules.

## Consequences

- access to a mature LTS release,
- compatibility with a modern library and tooling ecosystem,
- ability to use JPMS (`module-info.java`) without compromises,
- potential exclusion of very old runtime environments.
