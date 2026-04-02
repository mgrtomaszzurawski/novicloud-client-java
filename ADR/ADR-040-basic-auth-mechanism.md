# ADR-040: HTTP Basic Authentication via request interceptor

## Status

Accepted

## Context

The NoviCloud REST API uses HTTP Basic Authentication exclusively. The API documentation
states: username = account name (`nazwa_konta`), password = set in the NoviCloud admin panel
(Administracja - Dane firmy - Baza danych). There is no OAuth, API key, or token-based
authentication option.

Source: official NoviCloud REST API documentation v2.10.

## Decision

Authentication is implemented as a request interceptor on the generated `ApiClient`:

```java
apiClient.setRequestInterceptor(requestBuilder -> requestBuilder
    .header("Authorization", basicAuthHeader(accountName, password))
    .header("User-Agent", USER_AGENT));
```

`basicAuthHeader()` encodes `accountName:password` as Base64 per RFC 7617.

Credentials are passed once at SDK construction time (`NoviCloudClient.create(account, password)`
or via the builder). They are not stored as fields on `NoviCloudClient` - only the configured
`ApiClient` retains the interceptor closure.

### No token refresh or session management

The API has no session concept, no token expiration, and no refresh mechanism. Every request
carries the same Basic Auth header. If credentials change server-side, the client must be
reconstructed.

## Consequences

- SDK construction requires credentials upfront. There is no deferred or lazy authentication.
- Credentials travel with every HTTP request (standard Basic Auth behavior).
- HTTPS is assumed (the default base URL uses `https://`). Basic Auth over plain HTTP would
  expose credentials - the SDK does not enforce HTTPS but the server's base URL uses it.
- If the vendor adds alternative auth mechanisms in the future, the SDK would need a new
  auth strategy (new ADR, likely a builder option).
