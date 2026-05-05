# ADR-055: Builder required-field null validation

**Date:** 2026-05-02
**Status:** Accepted (supersedes T-09 documentation in ADR-052)

## Context

The 1.0.0 README and the `@param required` Javadoc on every `*CreateBuilder`
factory advertised that "required fields are enforced by the builder factory
method". `BuilderEdgeCaseTest` instead documented the opposite behaviour:

```java
// SDK does not validate required fields client-side (server validates).
TowarCreateBuilder d = TowarCreateBuilder.builder(null, null).build();
assertNull(d.kod());
assertNull(d.nazwa());
```

Only `StawkaVatCreateBuilder` actually rejected nulls. The README claim was
wrong, surfacing as Codex finding F-05.

Server-side validation does eventually catch missing required fields, but:
- The error arrives as a generic HTTP 400 with a string message, after a
  full network round-trip
- Required fields are positional in the factory; passing positional `null`
  is a developer mistake worth catching locally
- Some required fields are conditional (`KartaLojalnosciowa`: at least one
  of telefon/email plus nazwiskoImie per ADR-033) and the server's HTTP 400
  message is the same shape regardless of which condition failed

## Decision

### Per-field `Objects.requireNonNull`

Every `*CreateBuilder.builder(...)` factory now validates each required
positional argument:

```java
private Builder(String nazwa, String kod) {
    this.nazwa = Objects.requireNonNull(nazwa, "nazwa must not be null");
    this.kod = Objects.requireNonNull(kod, "kod must not be null");
}
```

Affected: `AsortyCreateBuilder`, `JmiaryCreateBuilder`, `KrajCreateBuilder`,
`FormaPlatnCreateBuilder`, `KontrahentCreateBuilder`, `SklepCreateBuilder`,
`TowarCreateBuilder`, `WalutaCreateBuilder`, `KartaLojCreateBuilder`.
`StawkaVatCreateBuilder` already validated id; pattern unchanged.

### Conditional rules in `KartaLojCreateBuilder.build()`

```java
public KartaLojCreateBuilder build() {
    if (telefon == null && email == null) {
        throw new IllegalStateException("at least one of telefon or email must be set");
    }
    if (nazwiskoImie == null) {
        throw new IllegalStateException("nazwiskoImie must not be null");
    }
    return new KartaLojCreateBuilder(this);
}
```

Reason: OpenAPI lacks an idiomatic way to express "at least one of X or Y";
the SDK must enforce it. The OpenAPI schema description is updated to
explain this (see F-06 in the same release).

### Test updates

`BuilderEdgeCaseTest` T-09 section is rewritten: tests now assert
`NullPointerException` on null required arguments. The misleading
"acceptedClientSide" tests are removed.

## Alternatives considered

- **Defer all validation to server:** rejected. Wastes a network round-trip
  on a developer mistake the SDK can detect locally with one method call.
- **Validate at `build()` instead of factory:** rejected. The factory accepts
  the required fields positionally; null there is unambiguously wrong, while
  fluent setters can intentionally pass `null` for optional partial updates.
- **Custom `MissingRequiredFieldException`:** rejected. `NullPointerException`
  is the JDK-standard signal for "non-null parameter received null"; users
  see a clear stack frame and message. `IllegalStateException` is the
  JDK-standard signal for "build() preconditions not met".

## Consequences

- README claim is now true.
- Behavioural change: 1.0.0 callers who happened to pass `null` for required
  fields received a server 400 at request time; in 1.1.0 they receive a
  local NPE/ISE at builder time. Documented in CHANGELOG.

## References

- Codex finding F-05 (context/codex-findings.md)
- ADR-052 (T-09 prior documentation of the gap)
- ADR-020 (CreateBuilder/UpdateBuilder design)
- ADR-033 (kartyloj conditional contact-channel rule)
