# ADR-041: JPMS --add-reads for test compilation (IntelliJ compatibility)

## Status

Accepted

## Context

The `novicloud-client` module has a `module-info.java` making it a named JPMS module. Test
dependencies (JUnit 5, WireMock, Mockito) are `scope=test` and not declared in `module-info.java`
because they are not runtime dependencies of the published library.

Maven's `maven-surefire-plugin` automatically adds `--add-reads` flags when running tests,
so `mvn clean verify` works without any extra configuration. However, IntelliJ IDEA applies
strict JPMS rules during its own compilation step and cannot see the test dependencies.

This caused ~100 build errors in IntelliJ - all test source files failed to compile because
JUnit, WireMock, and Mockito types were not accessible from the named module.

The same issue affected `demo-app` tests. `demo-app` has no `module-info.java` but depends
on the named `novicloud-client` module, which caused IntelliJ to apply module path rules to
demo-app test compilation as well.

## Decision

Added `--add-reads io.github.mgrtomaszzurawski.novicloud=ALL-UNNAMED` to the
`maven-compiler-plugin` test compilation configuration in both `novicloud-client/pom.xml`
and `demo-app/pom.xml`:

```xml
<executions>
    <execution>
        <id>default-testCompile</id>
        <configuration>
            <compilerArgs>
                <arg>--add-reads</arg>
                <arg>io.github.mgrtomaszzurawski.novicloud=ALL-UNNAMED</arg>
            </compilerArgs>
        </configuration>
    </execution>
</executions>
```

IntelliJ reads `<compilerArgs>` from `maven-compiler-plugin` on Maven import and applies them
to its own javac invocations. After "Reload All Maven Projects" in IntelliJ, both modules
compile correctly without any per-developer IDE configuration.

## Consequences

- IntelliJ compiles test sources correctly out of the box after Maven import.
- No manual IDE configuration required per developer.
- The `--add-reads` flag affects only the test compilation phase - it has no effect on the
  published library artifact or runtime behavior.
- This is a standard workaround for JPMS + test-scope dependencies. It does not weaken
  module encapsulation at runtime.
