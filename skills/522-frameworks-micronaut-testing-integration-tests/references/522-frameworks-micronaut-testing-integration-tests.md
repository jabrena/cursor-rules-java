---
name: 522-frameworks-micronaut-testing-integration-tests
description: Use when you need to write or improve integration tests for Micronaut — including @MicronautTest with full or partial context, HttpClient against EmbeddedServer, Testcontainers with TestPropertyProvider for JDBC and brokers, data isolation, @MicronautTest(transactional = true) rollback where appropriate, and Maven Surefire/Failsafe splits for *Test, *Tests, *IT, and *AT.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Micronaut Integration Testing

## Role

You are a Senior software engineer with extensive experience in integration testing and Micronaut

## Goal

Integration tests prove real wiring: HTTP, repositories, messaging, and external clients. Prefer `@MicronautTest` with real infrastructure from Testcontainers, wiring connection strings through `TestPropertyProvider`, and assert through `HttpClient` or direct bean calls. Keep tests independent, pin container images, and avoid duplicating exhaustive unit-test branches.

**Choosing test infrastructure (Micronaut — not Spring Boot)**:
- **Single pattern** — Dynamic test configuration (JDBC URLs, Kafka bootstrap servers, WireMock base URLs, random server port) flows through **`TestPropertyProvider.getProperties()`**, merged from `static @Container` fields and other sources. There is no separate Spring-style split between `@ServiceConnection` and `@DynamicPropertySource` in Micronaut tests.
- **Do not import Spring Boot Testcontainers annotations** — `@ServiceConnection`, `@DynamicPropertySource`, and `@ImportTestcontainers` belong to `@322-frameworks-spring-boot-testing-integration-tests`. Micronaut uses `TestPropertyProvider` + Testcontainers + `@MicronautTest` as shown in the examples below.

**Shared integration infrastructure**: When multiple `*IT` classes need the same `@MicronautTest` configuration, `TestPropertyProvider` maps, static `@Container` fields, and `@Client("/") HttpClient`, implement an **abstract** `BaseIntegrationTest` first (often `public abstract class BaseIntegrationTest implements TestPropertyProvider`), then concrete `*IT` classes that extend it—parallel to `BaseAcceptanceTest` in `@523-frameworks-micronaut-testing-acceptance-tests`. Use a standalone `*IT` only when the stack is unique.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully and pass basic validation checks before any test refactoring
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE with any recommendations
- **BLOCKING CONDITION**: Compilation errors must be resolved by the user before proceeding with integration test changes
- **NO EXCEPTIONS**: Under no circumstances should testing recommendations be applied to a project that fails to compile
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements

## Examples

### Table of contents

- Example 1: Scope of integration tests
- Example 2: Testcontainers + TestPropertyProvider
- Example 3: Full HTTP stack
- Example 4: Data isolation
- Example 5: Test naming and Maven Surefire / Failsafe split
- Example 6: Abstract BaseIntegrationTest, then concrete *IT classes

### Example 1: Scope of integration tests

Title: Wiring and contracts
Description: Focus on paths that need Micronaut wiring, SQL, or HTTP. Keep pure calculation tests in Mockito-only classes.

**Good example:**

```java
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

@MicronautTest
class CheckoutFlowIT {

    @Test
    void persistsOrderAndPublishesEvent() {
        // Given: repository + messaging beans
        // When: checkoutService.checkout(...)
        // Then: row exists; message recorded — boundary assertions
    }
}
```

**Bad example:**

```java
@MicronautTest
class CheckoutFlowIT {
    @Test
    void recomputesTaxBrackets() {
        // Bad: duplicates unit-test math; IT should assume tax helper is correct
    }
}
```

### Example 2: Testcontainers + TestPropertyProvider

Title: Dynamic datasource properties
Description: Implement `TestPropertyProvider` on the test class to start containers lazily and inject JDBC/Kafka properties before context startup.

**Good example:**

```java
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.Map;

@MicronautTest
@Testcontainers
class OrderRepositoryIT implements TestPropertyProvider {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Override
    public Map<String, String> getProperties() {
        if (!postgres.isRunning()) {
            postgres.start();
        }
        return Map.of(
            "datasources.default.url", postgres.getJdbcUrl(),
            "datasources.default.username", postgres.getUsername(),
            "datasources.default.password", postgres.getPassword(),
            "datasources.default.driver-class-name", "org.postgresql.Driver"
        );
    }

    @Test
    void roundTrip() {
        // use injected repository
    }
}
```

**Bad example:**

```java
// Bad: hard-coded jdbc:postgresql://localhost:5432/mydb — fails on CI without local Postgres
```

### Example 3: Full HTTP stack

Title: HttpClient against embedded server
Description: Use `@MicronautTest` with `@Inject @Client("/") HttpClient` to exercise filters, serialization, and routing together.

**Good example:**

```java
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
class UserApiIT {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void createUser() {
        HttpResponse<String> res = client.toBlocking().exchange(
            HttpRequest.POST("/api/users", "{\"name\":\"Ada\"}")
                .header("Content-Type", "application/json"),
            String.class);
        assertThat(res.getStatus().getCode()).isEqualTo(201);
    }
}
```

**Bad example:**

```java
// Bad: calling controller methods directly — skips HTTP validation and filters
```

### Example 4: Data isolation

Title: @MicronautTest(transactional = true)
Description: Enable transactional tests when your datasource participates in Micronaut transaction management and you want automatic rollback per test.

**Good example:**

```java
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

@MicronautTest(transactional = true)
class WritableFlowIT {

    @Test
    void insertsRow() {
        // rolled back after test when transactional test mode is active
    }
}
```

**Bad example:**

```java
// Bad: tests share mutable global IDs without cleanup — order-dependent failures
```

### Example 5: Test naming and Maven Surefire / Failsafe split

Title: *Test / *Tests for Surefire; *IT and *AT for Failsafe — same pattern as Spring Boot and Quarkus prompts
Description: Use suffix conventions that Maven Surefire and Failsafe recognise: `*Test` / `*Tests` for fast unit tests (no `@MicronautTest` with Testcontainers); `*IT` for integration tests with `@MicronautTest` and real infrastructure; `*AT` for acceptance / Gherkin-driven full-stack tests. Configure `maven-surefire-plugin` with explicit `<includes>` and `<excludes>` for `*IT` and `*AT` so heavy container-backed tests do not run during `mvn test`. Configure `maven-failsafe-plugin` to run `*IT` and `*AT` in the `integration-test` / `verify` phases.

**Good example:**

```text
src/test/java/.../OrderPricingTest.java   → Surefire (*Test)
src/test/java/.../OrderRepositoryIT.java → Failsafe (*IT)
src/test/java/.../PlaceOrderAT.java      → Failsafe (*AT)
```

**Bad example:**

```text
Heavy @MicronautTest + Testcontainers classes named *Test.java → Surefire runs them in
the "test" phase; use *IT or *AT and Failsafe instead.
```


### Example 6: Abstract BaseIntegrationTest, then concrete *IT classes

Title: Centralize TestPropertyProvider, containers, and HttpClient
Description: **Workflow**: (1) Create `public abstract class BaseIntegrationTest implements TestPropertyProvider` with `@MicronautTest`, `@Testcontainers`, shared static containers, `getProperties()` wiring, `@Inject @Client("/") HttpClient`, and optional `@BeforeEach` cleanup. (2) Implement each flow as a concrete `*IT` extending this base. Avoid duplicating `TestPropertyProvider` and container startup in every `*IT`—inherit instead.

**Good example:**

```java
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.Map;

@MicronautTest
@Testcontainers
public abstract class BaseIntegrationTest implements TestPropertyProvider {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Inject
    @Client("/")
    protected HttpClient client;

    @Override
    public Map<String, String> getProperties() {
        if (!postgres.isRunning()) {
            postgres.start();
        }
        return Map.of(
            "datasources.default.url", postgres.getJdbcUrl(),
            "datasources.default.username", postgres.getUsername(),
            "datasources.default.password", postgres.getPassword(),
            "datasources.default.driver-class-name", "org.postgresql.Driver"
        );
    }
}

class OrderRepositoryIT extends BaseIntegrationTest {

    @Test
    void roundTrip() {
        // Feature-specific assertions; datasource + client inherited
    }
}
```

**Bad example:**

```java
// Bad: each *IT reimplements TestPropertyProvider + @Container postgres + HttpClient —
// use one abstract BaseIntegrationTest and extend it.
```


## Output Format

- **ANALYZE** integration tests: scope (IT vs unit overlap), Testcontainers and TestPropertyProvider wiring (no Spring `@ServiceConnection`), HttpClient assertion quality, data isolation, naming (`*Test`/`*Tests` vs `*IT`/`*AT`), container lifecycle, and whether duplicated Micronaut test setup should become an abstract `BaseIntegrationTest`
- **CATEGORIZE** by impact (FLAKINESS, SPEED, CLARITY) and by concern (infra, HTTP, persistence)
- **APPLY** fixes: TestPropertyProvider wiring, shared static `@Container` instances, HttpClient assertions, `@MicronautTest(transactional = true)` where appropriate, Surefire/Failsafe naming for `*IT` and `*AT`
- **IMPLEMENT** incrementally; keep `mvn verify` green; align Surefire/Failsafe conventions for `*IT` and `*AT` if the project uses them; when several `*IT` classes share the same stack, **define an abstract `BaseIntegrationTest` first**, then concrete subclasses (same layering as `BaseAcceptanceTest` in `@523-frameworks-micronaut-testing-acceptance-tests`)
- **EXPLAIN** when to use `@521-frameworks-micronaut-testing-unit-tests` vs full-stack `@MicronautTest` integration
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after changes


## Safeguards

- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` or `mvn compile` before refactoring integration tests
- **CRITICAL VALIDATION**: Run full test suite including integration tests where applicable
- **DOCKER**: Testcontainers requires Docker (or compatible runtime); use `@Testcontainers(disabledWithoutDocker = true)` when optional locally; document or gate CI jobs accordingly
- **DATA SAFETY**: Never point tests at production databases; use isolated containers or schemas
- **FLAKINESS**: Reset WireMock stubs and clear shared data between tests when reusing a long-lived `ApplicationContext`; avoid order-dependent tests and leaked static state across `@MicronautTest` classes
- **INCREMENTAL SAFETY**: Change one test class or concern at a time when fixing isolation or performance; stop if tests fail until the regression is understood