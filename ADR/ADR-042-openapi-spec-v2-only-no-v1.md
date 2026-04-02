# ADR-042: OpenAPI spec targets API v2.10 only - v1 removed

## Status

Accepted

## Context

The NoviCloud REST API has two versions: v1 and v2. The original OpenAPI spec included both
server entries:

```yaml
servers:
  - url: https://system.novicloud.pl/rest/api/v2
    description: Version 2 base URL
  - url: https://system.novicloud.pl/rest/api/v1
    description: Version 1 base URL
```

The v1 endpoint was never tested during SDK development, there is no v1-specific documentation
available, and the provider (Insoft) considers v1 deprecated. The entire SDK - all 18 endpoint
clients, all builders, all integration tests, all demo-app runners - was developed and verified
exclusively against v2.10.

Keeping v1 in the spec would mislead tools (Swagger UI, Postman imports, code generators)
into suggesting v1 is a supported option.

## Decision

Removed the v1 server entry from `openapi/openapi.yaml`. The spec now contains only:

```yaml
servers:
  - url: https://system.novicloud.pl/rest/api/v2
    description: Version 2 base URL
```

The SDK's `NoviCloudClient` uses v2 as the default base URL. Custom base URLs are supported
via `NoviCloudClient.create(baseUrl, account, password)` or the builder, but v1 compatibility
is not guaranteed.

## Consequences

- The OpenAPI spec is honest: it documents only what was tested and verified.
- Swagger UI and Postman imports from this spec will not offer v1 as an option.
- If a future need arises to support v1, it would require a separate OpenAPI spec file and
  dedicated testing, not just re-adding the server URL.
