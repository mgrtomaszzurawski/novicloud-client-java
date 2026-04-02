# ADR-039: FlexibleLocalDateTimeDeserializer for mixed date formats

## Status

Accepted

## Context

The NoviCloud REST API returns `date-time` fields in two formats depending on the endpoint
and record type:

- Full datetime: `"2019-08-28T13:57:39"`
- Date-only: `"2019-08-27"`

Both formats appear for the same field type (`LocalDateTime` in the generated Java model,
configured via `dateLibrary: java8-localdatetime` in the OpenAPI generator). The server
decides which format to use based on internal logic - there is no documented rule.

Jackson's default `LocalDateTime` deserializer expects the full `ISO_LOCAL_DATE_TIME` format
(`yyyy-MM-ddTHH:mm:ss`). When the server returns a date-only string, Jackson throws
`DateTimeParseException` and the entire response deserialization fails.

OpenAPI 3.0 has no way to express "this field may be either `date-time` or `date`" - the
`format` keyword accepts only one value. The generator produces a single Java type per field.

## Decision

Register a custom Jackson `StdDeserializer<LocalDateTime>` globally on the shared
`ObjectMapper` in `NoviCloudClient.Builder.build()`.

The deserializer checks whether the value contains `"T"`:
- If yes: parse as `LocalDateTime.parse(value)` (standard ISO datetime)
- If no: parse as `LocalDate.parse(value).atStartOfDay()` (date-only, midnight)

The class is package-private (`FlexibleLocalDateTimeDeserializer`) - not part of the
public SDK API surface.

### Why `java8-localdatetime` dateLibrary option

The generator is configured with `<dateLibrary>java8-localdatetime</dateLibrary>` which maps
`format: date-time` to `LocalDateTime` instead of the default `OffsetDateTime`. This is correct
because the NoviCloud server returns dates without timezone information. Using `OffsetDateTime`
would require fabricating a timezone offset that the server never provides.

These are two separate problems solved by two separate mechanisms:
- `java8-localdatetime` solves "no timezone in server response" (generator config)
- `FlexibleLocalDateTimeDeserializer` solves "sometimes date-only instead of datetime" (runtime)

Removing `java8-localdatetime` would break ALL date fields across the entire SDK because
`OffsetDateTime.parse("2019-08-28T13:57:39")` fails without a zone offset suffix (`+02:00`
or `Z`). The custom deserializer cannot fix this because it produces `LocalDateTime`, not
`OffsetDateTime`.

## Consequences

- All `LocalDateTime` fields in all 37+ generated model classes are correctly deserialized
  regardless of which format the server chooses to return.
- Date-only values lose time precision (always midnight). This matches the server's intent -
  when only a date is returned, there is no meaningful time component.
- If the server starts returning a third format (e.g. with timezone), the deserializer would
  need updating. This is unlikely given the API's maturity.
