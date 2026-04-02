# ADR-036: Maven Central Publishing Setup

**Date:** 2026-03-30
**Status:** Accepted

## Context

The SDK is ready for public distribution. The project uses AGPL-3.0 license with a dual-licensing
model: open source use is free, commercial use requires a separate license from the author.
Maven Central is the standard distribution channel for Java libraries, providing zero-configuration
dependency resolution for consumers.

## Decision

Publish `novicloud-client` to Maven Central via Sonatype Central Portal (central.sonatype.com).

### What is published

Only `novicloud-client` module. `demo-app` is excluded via `<maven.deploy.skip>true</maven.deploy.skip>`.

Maven Central receives:
- `novicloud-client-1.0.0.jar` - compiled library
- `novicloud-client-1.0.0-sources.jar` - source code
- `novicloud-client-1.0.0-javadoc.jar` - Javadoc
- `novicloud-client-1.0.0.pom` - POM metadata
- GPG signatures (`.asc`) for all four artifacts

### Tools

- `maven-source-plugin:3.3.1` - generates sources jar
- `maven-javadoc-plugin:3.7.0` - generates Javadoc jar
- `maven-gpg-plugin:3.2.4` - signs all artifacts with key `3D4D5089B6567EAF`
- `central-publishing-maven-plugin:0.7.0` - uploads to Sonatype Central Portal

### Configuration

Credentials stored in `~/.m2/settings.xml` under server id `central`, read from environment
variables `MVN_CENTRAL_USERNAME` and `MVN_CENTRAL_PASSWORD` (Sonatype Central Portal user token).

`autoPublish=false` - upload creates a deployment in "pending" state on central.sonatype.com.
Manual review and publish button required before artifacts go live. This prevents accidental
releases.

### Namespace

`io.github.mgrtomaszzurawski` - verified automatically via GitHub OAuth login on central.sonatype.com.
No manual verification required.

### GPG key

RSA 4096, id `3D4D5089B6567EAF`, published to `keyserver.ubuntu.com`.

## Version

Root pom, `novicloud-client`, and `demo-app` all set to `1.0.0` (SNAPSHOT suffix removed).

## Consequences

- Any consumer can add `novicloud-client:1.0.0` as a Maven/Gradle dependency without
  configuring custom repositories
- AGPL-3.0 license is visible on Maven Central - companies are notified of license terms
  before adopting the library
- `mvn deploy` from root publishes only SDK; demo-app is never uploaded
- Future releases require bumping version in root pom, novicloud-client pom, and demo-app
  dependency declaration
