# ADR-050: SPDX license headers in all source files

**Date:** 2026-04-01
**Status:** Accepted

## Context

The SDK had `LICENSE.txt` (AGPL-3.0) in the root and `<licenses>` in the root pom.xml, but zero
license headers inside individual source files. If someone copies a single `.java` or `.yaml` file
out of the repo, there is no indication of license or author.

SPDX file headers are the standard mechanism in open source projects for per-file license
identification. They are machine-readable, concise, and survive copy-paste.

## Decision

Add a 3-line SPDX header to every source file in the project:

```
Copyright (c) 2026 Tomasz Zurawski
SPDX-License-Identifier: AGPL-3.0-only
https://github.com/mgrtomaszzurawski/novicloud-client-java
```

### Mechanism by file type

| File type | Mechanism | Phase |
|-----------|-----------|-------|
| Hand-written `*.java` (SDK + demo-app + tests) | mycila `license-maven-plugin` 4.6 | `process-sources` |
| `*.yaml` (all 55 OpenAPI spec files) | mycila `license-maven-plugin` 4.6 | `process-sources` |
| `examples/*.java` (standalone examples) | mycila `license-maven-plugin` 4.6 | `process-sources` |
| Generated `*.java` (target/) | Custom `licenseInfo.mustache` template | `generate-sources` |

### Why two mechanisms

The openapi-generator's default Java `licenseInfo.mustache` template does NOT render the
`licenseInfo` template variable in the file header comment. The template only uses `appName`,
`appDescription`, `version`, and `infoEmail`. The `licenseInfo`/`licenseUrl` configOptions and
additionalProperties are available in the template context but the default template ignores them.

Confirmed by reading the template source on GitHub (2026-04-01):
https://github.com/OpenAPITools/openapi-generator/blob/master/modules/openapi-generator/src/main/resources/Java/licenseInfo.mustache

The fix: a custom `licenseInfo.mustache` in `openapi/templates/` that adds the SPDX block between
the version line and the "auto generated" note. Referenced via `<templateDirectory>` in pom.xml.

mycila `license-maven-plugin` handles all non-generated files. It runs in `process-sources` phase
(after `generate-sources`), is idempotent (won't add the header twice), and supports both Java
(`/* */` block comment) and YAML (`#` line comment) styles.

### Why not mycila on generated files too

Generated files live in `target/` and are deleted on every `mvn clean`. Running mycila on them
would work but adds unnecessary post-processing on every build. The custom mustache template
generates the header at source, which is cleaner.

### Plugin configuration

The plugin is configured in 3 pom.xml files (root + novicloud-client + demo-app) because
child modules do not declare `<parent>` on the root pom. The root `pom.xml` has
`<pluginManagement>` for centralized version/config, but child modules need their own
`<plugin>` declaration to activate it.

Header source file: `LICENSE-HEADER.txt` in project root, referenced via
`${maven.multiModuleProjectDirectory}/LICENSE-HEADER.txt`.

## Consequences

- Every `.java` and `.yaml` file carries license identification
- `mvn process-sources` (or any later phase) auto-formats headers on new files
- Generated classes carry the header from generation time (no post-processing)
- Developers adding new files don't need to manually add headers; next build adds them
- `mvn com.mycila:license-maven-plugin:check` can be used in CI to verify compliance
