# NoviCloud Java SDK - 1.0.1 relocation branch

This branch (`relocation/1.0.1`) is a deployment-only branch. It contains a single
Maven POM whose only purpose is to publish coordinates
`io.github.mgrtomaszzurawski:novicloud-client:1.0.1` as a relocation artifact
pointing to `2.0.0`.

It is NOT a normal release branch. It is never merged into `main`. The only
"product" it produces is the 1.0.1 POM artifact on Maven Central, which makes
Maven and Gradle warn 1.0.0 consumers to update to 2.0.0.

For the actual SDK source, see `main`.

## Why 1.0.1 exists

Maven Central does not allow deletion of published artifacts. The 1.0.0
release contained unresolved bugs (see `CHANGELOG.md` on `main`, [2.0.0]
section). To signal "do not use 1.0.0" without rewriting history, a follow-up
1.0.1 artifact is published whose POM contains a `<distributionManagement>/<relocation>`
element. Build tools surface a relocation warning when consumers resolve any
1.x coordinate above 1.0.0.

This decision is documented in `ADR/ADR-059-2.0.0-release-decision.md` on the
`main` branch.

## Deploy

```bash
mvn clean deploy -Prelease
```

The POM has packaging=pom and contains no source code, so deploy uploads
only the signed POM (no jar, no sources, no javadoc - none are required for
relocation artifacts).
