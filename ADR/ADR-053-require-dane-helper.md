# ADR-053: requireDane() helper for empty-payload responses

**Date:** 2026-05-02
**Status:** Accepted

## Context

External code review (Codex 2026-05-02, finding F-01) showed that `getById()`
across 16 SDK clients dereferenced the unwrapped `dane` value without a null
check:

```java
TowarRaw raw = retryHandler.execute(...).getDane();
return Towar.from(raw); // NPE if raw is null
```

ADR-033 documents that the NoviCloud server returns HTTP 200 with `dane = null`
(or omits the `dane` key) when:
- A hard-deleted record is fetched (asorty, jmiary, kraje, stawkivat)
- The producer documentation paragraph "successful GET 200 may omit `dane`"
  applies to single-record reads with no data

The SDK exception contract advertises `NoviCloudNotFoundException` for
"record does not exist". Returning a raw NPE breaks that contract.

## Decision

Add a centralized helper in `NoviCloudException`:

```java
public static <T> T requireDane(T dane, String resource, Object id) {
    if (dane == null) {
        throw new NoviCloudNotFoundException(
            String.format("%s with id %s not found (HTTP 200 with empty dane)", resource, id),
            null, 200, null);
    }
    return dane;
}
```

Call sites in 16 single-record reads + `KartyLojClient.getByKod()` +
`StanyMagClient.getByTowarAndSklep()` are updated to:

```java
TowarRaw raw = retryHandler.execute(...).getDane();
return Towar.from(NoviCloudException.requireDane(raw, "towar", id));
```

The exception carries HTTP status `200` to distinguish it from a real 404
in error logs and metrics.

## Alternatives considered

- **Dedicated `NoviCloudEmptyResponseException` subclass:** rejected to keep
  the hierarchy compact. Callers already special-case `NoviCloudNotFoundException`
  for "missing record" semantics; "empty dane" is the same logical state.
- **Allow `from()` to accept null and return a "shell" record:** rejected. It
  would silently propagate emptiness deeper into caller code.
- **Inline the check in each `getById`:** rejected. 17 copies, each with the
  same logic. The helper is shared and tested in one place.

## Consequences

- Callers now reliably catch `NoviCloudNotFoundException` for hard-deleted
  records and the "no data" case the producer documents.
- WireMock tests can stub `{"status":200,"dane":null}` to verify the contract.
- Status code `200` is used for the not-found exception. This is intentional
  and documented in the message; consumers comparing status codes should
  catch `NoviCloudNotFoundException` rather than `getStatusCode() == 404`.

## References

- Codex finding F-01 (context/codex-findings.md)
- ADR-033 (CUD testing - documented hard-delete behavior)
- Issue surfaced by external review on 2026-05-02
