# ADR-028: Rename *Sdk to *Client — industry-standard naming

**Date:** 2026-03-28
**Status:** Accepted

---

## Context

The original naming used `*Sdk` suffix for resource-level classes (`AsortySdk`, `TowarysSdk`, etc.)
and `NoviCloudSdk` / `NoviCloudSdkFactory` as the entry point. This did not align with the naming
convention established by major Java SDK libraries:

- AWS SDK v2: `S3Client`, `DynamoDbClient`, `SqsClient`
- Google Cloud Java: `BigQueryClient`, `StorageClient`
- Azure SDK: `BlobClient`, `SecretClient`

Additionally, `NoviCloudSdkFactory` as a separate class conflicted with the AWS-style pattern where
the client exposes a static `builder()` method directly, making the factory a redundant indirection.

The `NoviCloudSdkException` base class name was also inconsistent - exception hierarchy does not
benefit from an `Sdk` infix; the class is part of the public API and should be named after its
domain role, not the library layer it belongs to.

---

## Decision

### Resource clients: `*Sdk` -> `*Client`

All 18 resource-level classes renamed:

| Before | After |
|---|---|
| `AsortySdk` | `AsortyClient` |
| `DokumentySdk` | `DokumentyClient` |
| `FormyPlatnSdk` | `FormyPlatnClient` |
| `JmiarySdk` | `JmiaryClient` |
| `KartyLojSdk` | `KartyLojClient` |
| `KasjerzykSdk` | `KasjerzykClient` |
| `KasySdk` | `KasyClient` |
| `KontrahenciSdk` | `KontrahenciClient` |
| `KrajeSdk` | `KrajeClient` |
| `PozdokSdk` | `PozdokClient` |
| `RapPracySdk` | `RapPracyClient` |
| `RapSprzedSdk` | `RapSprzedClient` |
| `SklepySdk` | `SklepyClient` |
| `SprzedazSdk` | `SprzedazClient` |
| `StanyMagSdk` | `StanyMagClient` |
| `StawkiVatSdk` | `StawkiVatClient` |
| `TowarySdk` | `TowaryClient` |
| `WalutySdk` | `WalutyClient` |

### Entry point: `NoviCloudSdk` + `NoviCloudSdkFactory` -> `NoviCloudClient`

The two classes are merged. `NoviCloudClient` contains:
- The 18 resource client fields and accessors (`towary()`, `asorty()`, etc.)
- The static factory methods: `create(accountName, password)` and `create(baseUrl, accountName, password)`
- The inner `Builder` class (previously `NoviCloudSdkFactory.Builder`)

Usage after:

```java
// static factory
NoviCloudClient client = NoviCloudClient.create(accountName, password);

// builder
NoviCloudClient client = NoviCloudClient.builder()
    .baseUrl(url)
    .retryPolicy(policy)
    .build(accountName, password);

// resource access
client.towary().list(query);
client.kontrahenci().getById(42L);
```

### Base exception: `NoviCloudSdkException` -> `NoviCloudException`

The `Sdk` infix in exception names adds no semantic value. The exception hierarchy describes
error categories (`Auth`, `NotFound`, `RateLimit`, `Server`, `Network`), not library layers.

Subclass names were already correct and required no change:
`NoviCloudAuthException`, `NoviCloudNetworkException`, `NoviCloudNotFoundException`,
`NoviCloudRateLimitException`, `NoviCloudServerException`.

### Package names: unchanged

Packages (`sdk.towary`, `sdk.asorty`, etc.) remain `sdk.*`. Renaming them to `client.*` would
conflict with the generated `novicloud.client.*` package, require `module-info.java` changes, and
provide no benefit visible to library users (who see class names, not package paths, in most IDEs).

---

## Consequences

### Positive
- Naming matches industry standard (AWS, GCP, Azure SDKs)
- Single entry point `NoviCloudClient` with `builder()` - no separate factory class
- `NoviCloudException` is a cleaner public API name
- `spotbugs-exclude.xml` updated to reference `NoviCloudClient`

### Negative
- MAJOR version bump required if this library were published with existing `*Sdk` consumers
- (Not applicable here - library is not yet published externally)

---

## Related

- ADR-025: Package-per-endpoint and Builder naming (establishes `*QueryBuilder`/`*CreateBuilder` pattern, unchanged)
- ADR-026: RetryHandler public visibility
- ADR-019: Exception hierarchy
