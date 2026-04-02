# ADR-035: Mockito unit tests for *Client classes

## Status

Accepted

## Context

Each of the 18 `*Client` classes in the SDK wraps a generated `*Api` class and exposes:
- `getById(Long id)` / `getByKod(String kod)` - fetch single record
- `deleteById(Long id)` - delete record
- `listAll(Query q)` - returns a lazy `PagedIterable`
- `count(Query q)` - calls the API and inspects the response envelope

The WireMock integration tests (ADR-034) cover the HTTP round-trip but are heavier: they start
an embedded HTTP server and require full request/response stubs. For lightweight contract
verification (null guards, count fallback logic) a faster layer of unit tests is more appropriate.

Before this ADR, there were no tests for:
- Null guard behavior: does `getById(null)` throw `IllegalArgumentException` before touching the network?
- `count()` fallback: when `size` is absent from the envelope, does it fall back to `dane.size()`?
- Lazy `listAll`: does constructing a `PagedIterable` avoid any network call?

## Decision

Add 18 unit test classes in `src/test/java/.../sdk/unit/`, one per `*Client`.

### Test types

**Null guard tests** - no mock needed. The `requireNotNull()` helper in each `*Client`
throws `IllegalArgumentException` before `RetryPolicy` or `*Api` is touched:

```java
@Test
void getById_nullId_throwsIllegalArgument() {
    assertThrows(IllegalArgumentException.class, () -> client().getById(null));
}
```

**`listAll(null)` returns iterable** - no mock needed. `PagedIterable` receives lambdas
in its constructor but does not invoke them. Construction is always synchronous and null-safe:

```java
@Test
void listAll_nullQuery_returnsIterable() {
    assertNotNull(client().listAll(null));
}
```

**`count()` fallback logic** - requires mocking the `*Api` constructor. Three cases:

```java
// Case 1: size absent, dane list present -> returns dane.size()
// Case 2: size absent, dane null -> returns 0
// Case 3: size present -> returns size directly
try (MockedConstruction<AsortyApi> mc = mockConstructionWithAnswer(AsortyApi.class,
        inv -> response)) {
    assertEquals(2, new AsortyClient(new ApiClient(), ACCOUNT, NO_RETRY).count(null));
}
```

`mockConstructionWithAnswer` intercepts `new AsortyApi(apiClient)` inside `AsortyClient`
and returns the configured response object for any method call on the mock.
`RetryPolicy.builder().enabled(false).build()` ensures the SDK calls the API once without
retry wrapping.

### Why mockConstructionWithAnswer and not WireMock for count() tests

`count()` calls `api.listXxx(account, ...)` with 4-17 parameters depending on the endpoint.
Stubbing a full HTTP interaction for all 18 endpoints would require 54 WireMock stubs.
Constructor mocking intercepts at the Java layer before any HTTP is involved, which is faster
and avoids encoding irrelevant parameter details.

## Consequences

### Dependencies added

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.12.0</version>
    <scope>test</scope>
</dependency>
```

`scope=test`: invisible to SDK consumers, not transitive, not included in the published jar.

### Java 23 compatibility fix required

Mockito 5.12.0 bundles Byte Buddy 1.14.x which officially supports Java up to version 22
(class file major version 66). Running on Java 23 (major version 67) causes:

```
java.lang.IllegalArgumentException: Java 23 (67) is not supported by the current version
of Byte Buddy which officially supports Java 22 (66) - update Byte Buddy or set
net.bytebuddy.experimental as a VM property
```

Fix: add to `maven-surefire-plugin` argLine in `novicloud-client/pom.xml`:

```xml
<argLine>-Djdk.attach.allowAttachSelf=true -Dnet.bytebuddy.experimental=true</argLine>
```

- `-Djdk.attach.allowAttachSelf=true` - allows Mockito to self-attach its Java agent on newer JDKs.
- `-Dnet.bytebuddy.experimental=true` - allows Byte Buddy to instrument classes on Java versions
  beyond its officially declared support. Works correctly in practice on Java 23.

These flags affect only the test JVM process (surefire forks). They have no effect on SDK
consumers or the published jar.

Alternative not taken: upgrade to `mockito-core:5.14+` which bundles Byte Buddy 1.15+ with
official Java 23 support. Deferred to avoid unnecessary churn during the current development phase.

### module-info.java: unchanged

The `client.api` package remains unexported and unopened. `mockConstructionWithAnswer` does not
require the package to be opened - Byte Buddy instruments at the bytecode level without reflective
access to the package. Tests confirmed that `opens io.github.mgrtomaszzurawski.novicloud.client.api`
is not needed.

### Test count

18 new test classes, approximately 99 new unit tests added to the SDK module.
