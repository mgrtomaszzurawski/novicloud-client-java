# NoviCloud SDK Examples

Standalone Java examples showing common SDK usage patterns.
Each file is self-contained - read it as reference or copy into your project.

## Prerequisites

- Java 17+
- `novicloud-client-1.0.0.jar` on classpath (or as Maven dependency)
- NoviCloud API credentials (account name + password)

## Running an example

Set credentials as environment variables:

```bash
export NOVICLOUD_ACCOUNT_NAME=your-account
export NOVICLOUD_PASSWORD=your-password
```

Then compile and run:

```bash
javac -cp novicloud-client-1.0.0.jar ListProducts.java
java -cp novicloud-client-1.0.0.jar:. ListProducts
```

Or just read the source - each file is under 40 lines.

## Examples

| File | Description |
|------|-------------|
| `ListProducts.java` | List and filter products; `totalCount()` from first page, no extra HTTP call |
| `CreateAndUpdateProduct.java` | Full CRUD lifecycle with soft-delete |
| `HandleErrors.java` | Exception hierarchy and error handling |
| `CustomRetryPolicy.java` | Retry configuration and backoff |
| `PaginateAllRecords.java` | `PagedResult` metadata, random access via `seek()`, bidirectional `listIterator()`, `fetchFrom()` |
| `WorkWithReports.java` | Sales and work reports with date range |
