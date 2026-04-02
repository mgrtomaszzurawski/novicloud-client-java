# ADR-014: Date/time fields in Draft classes are `String`; parsing happens in the Sdk layer

## Status

Accepted

## Context

Generated model setters for `date-time` fields accept `OffsetDateTime` and for `date` fields accept `LocalDate` (generator option `java8-localdatetime`). Draft classes that hold write payloads must somehow represent these fields.

Two options were considered:

1. **Draft fields typed as `OffsetDateTime`/`LocalDate`** — callers must import and construct date objects from `java.time`, making the builder API heavier for simple use cases (e.g. tests, scripting).
2. **Draft fields typed as `String`** — callers pass ISO-formatted strings; the Sdk layer parses them before calling the generated setters.

The API documents dates as ISO 8601 strings in JSON. The NoviCloud backend expects string representations. Keeping Strings in the Draft layer aligns with how the API naturally expresses these values.

## Decision

All date/time fields in Draft classes are typed as `String`. The Sdk mapping method parses them using standard Java:

```java
// date-time → OffsetDateTime
karta.setWaznaOd(draft.waznaOd() != null ? OffsetDateTime.parse(draft.waznaOd()) : null);

// date → LocalDate
karta.setDataUrodzenia(draft.dataUrodzenia() != null ? LocalDate.parse(draft.dataUrodzenia()) : null);
```

## Affected classes

`KartaLojDraft` — fields `waznaOd`, `waznaDo`, `uniewazniono` (date-time), `dataUrodzenia` (date).

Other existing Draft classes (TowarDraft, KontrahentDraft, SklepDraft, etc.) do not have date fields in the write body.

## Consequences

- Draft builder API stays free of `java.time` imports.
- Callers who already work with `OffsetDateTime` objects must call `.toString()` before passing to the builder — acceptable trade-off.
- Malformed date strings throw `DateTimeParseException` from `OffsetDateTime.parse()` or `LocalDate.parse()` in the Sdk layer — not wrapped in `NoviCloudSdkException` since it is a programming error, not an API error.
- If new Draft classes with date fields are added, the same pattern applies.
