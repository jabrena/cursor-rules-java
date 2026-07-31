---
name: 521-frameworks-micronaut-testing-unit-tests
description: Use when you need to write unit tests for Micronaut applications — including pure JUnit 5 + Mockito with @ExtendWith(MockitoExtension.class) for @Singleton services, @MicronautTest with @MockBean for HTTP/controller slices, @Client HttpClient against EmbeddedServer, JSON assertions with AssertJ, @ParameterizedTest with @CsvSource/@MethodSource, property overrides with @Property, and naming conventions (*Test → Surefire, *IT → Failsafe). For framework-agnostic Java use @131-java-testing-unit-testing. For integration tests use @522-frameworks-micronaut-testing-integration-tests.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Micronaut Unit Testing

## Role

You are a Senior software engineer with extensive experience in Micronaut testing and JUnit 5

## Goal

Micronaut tests should be fast by default: exercise plain services with Mockito and no container, and reserve `@MicronautTest` for cases that need HTTP routing, bean replacement, or Micronaut-specific adapters. Use `@MockBean` factory methods to substitute collaborators, inject `HttpClient` with `@Client("/")` for controller tests, and keep deterministic configuration via `@Property` on test classes or methods. Inject `java.time.Clock` (or a narrow time-supplier interface) for anything that would otherwise call `Instant.now()` — Mockito does not mock static time APIs, and `Instant` is final; a fixed `Clock` in tests removes flakiness.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully and pass basic validation checks before any test refactoring
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE with any recommendations
- **BLOCKING CONDITION**: Compilation errors must be resolved by the user before proceeding with test improvements
- **NO EXCEPTIONS**: Under no circumstances should testing recommendations be applied to a project that fails to compile
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements

## Examples

### Table of contents

- Example 1: Pure Mockito for services
- Example 2: Injectable `Clock` — avoid static time and final value mocks
- Example 3: @MicronautTest with @MockBean
- Example 4: HttpClient against embedded server
- Example 5: Test-specific configuration
- Example 6: Parameterized tests
- Example 7: Unit test class naming convention

### Example 1: Pure Mockito for services

Title: No Micronaut context
Description: Use `MockitoExtension`, `@Mock`, and `@InjectMocks` for services that only depend on interfaces.

**Good example:**

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    @Mock
    DiscountPolicy discounts;

    @InjectMocks
    PricingService pricingService;

    @Test
    void appliesDiscount() {
        when(discounts.rate()).thenReturn(0.1);
        assertThat(pricingService.price(100)).isEqualTo(90);
    }
}
```

**Bad example:**

```java
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@MicronautTest // Bad: full context for trivial arithmetic service
class PricingServiceTest {

    @Inject
    PricingService pricingService;

    @Test
    void appliesDiscount() {
        // slow startup for logic that needs no container
    }
}
```

### Example 2: Injectable `Clock` — avoid static time and final value mocks

Title: `@Singleton` services take `Clock`; tests use `Clock.fixed` or `@Mock Clock`
Description: **`Instant.now()` / `LocalDate.now()`** hide behind static methods — Mockito does not intercept them, and **`Instant`** is **final**. **Inject `java.time.Clock`** (constructor injection on `@Singleton` beans; optional `@Factory` + `@Bean` for `Clock.systemUTC()` in Micronaut). Unit tests construct the service with **`Clock.fixed(…, zone)`** or **`@Mock Clock`** and `when(clock.instant()).thenReturn(…)`. **Also hard to fake in tests:** `UUID.randomUUID()`, `System.nanoTime()`, unseeded `Random` — inject `Supplier<UUID>`, `LongSupplier`, or a seeded `Random` for deterministic assertions. In **`@MicronautTest`**, replace the `Clock` bean with `@MockBean` / `@Replaces` returning a fixed clock when the full graph must run; for pure domain logic, prefer **`@ExtendWith(MockitoExtension.class)`** without the container.

**Good example:**

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// @Singleton class OfferService { private final Clock clock; ... }
class OfferService {
    private final Clock clock;
    OfferService(Clock clock) { this.clock = clock; }

    boolean stillValid(Instant expiresAt) {
        return clock.instant().isBefore(expiresAt);
    }
}

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    Clock clock;

    @Test
    void stillValid_beforeExpiry_returnsTrue() {
        when(clock.instant()).thenReturn(Instant.parse("2024-03-01T10:00:00Z"));
        var svc = new OfferService(clock);
        assertThat(svc.stillValid(Instant.parse("2024-03-01T23:59:59Z"))).isTrue();
    }

    @Test
    void stillValid_usesFixedClock() {
        Clock fixed = Clock.fixed(
            Instant.parse("2024-03-01T10:00:00Z"), ZoneOffset.UTC);
        var svc = new OfferService(fixed);
        assertThat(svc.stillValid(Instant.parse("2024-03-01T23:59:59Z"))).isTrue();
    }
}
```

**Bad example:**

```java
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

class OfferService {

    boolean stillValid(Instant expiresAt) {
        return Instant.now().isBefore(expiresAt); // Bad: not injectable; tests tied to real time
    }
}

class OfferServiceTest {

    @Test
    void brittle() {
        var svc = new OfferService();
        assertThat(svc.stillValid(Instant.parse("2099-01-01T00:00:00Z"))).isTrue();
    }
}
```

### Example 3: @MicronautTest with @MockBean

Title: Replace collaborators in the test scope
Description: Declare `@MockBean` factory methods on the test class (or a companion) to supply Mockito doubles registered in the Micronaut context.

**Good example:**

```java
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

@MicronautTest
class OrderControllerTest {

    @jakarta.inject.Inject
    OrderService orderService;

    @MockBean(OrderService.class)
    OrderService mockOrderService() {
        return Mockito.mock(OrderService.class);
    }

    @Test
    void delegatesToService() {
        when(orderService.find(1L)).thenReturn(new OrderDto(1L, "A"));
        // inject @Client("/") HttpClient and call GET /orders/1 — assert 200 + body
    }
}
```

**Bad example:**

```java
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

@MicronautTest
class OrderControllerTest {
    // Bad: hits real OrderService + DB — not a unit test
}
```

### Example 4: HttpClient against embedded server

Title: @Inject @Client("/") HttpClient
Description: Inject a blocking or reactive `HttpClient` bound to the test server to assert status codes and payloads on real routing.

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
class HelloControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void ok() {
        HttpResponse<String> response = client.toBlocking().exchange(
            HttpRequest.GET("/hello"), String.class);
        assertThat(response.getStatus().getCode()).isEqualTo(200);
        assertThat(response.body()).contains("Hello");
    }
}
```

**Bad example:**

```java
// Bad: manually starting Netty in test — MicronautTest already provides EmbeddedServer + client wiring
```

### Example 5: Test-specific configuration

Title: @Property on the test class
Description: Override configuration keys for deterministic tests (feature flags, URLs of stub servers).

**Good example:**

```java
import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

@MicronautTest
@Property(name = "app.feature.x", value = "false")
class FeatureOffTest {

    @Test
    void behavesWhenDisabled() {
        // ...
    }
}
```

**Bad example:**

```java
// Bad: tests depend on developer application.yml on disk — flaky in CI
```

### Example 6: Parameterized tests

Title: @CsvSource for table-driven cases
Description: Collapse equivalent scenarios into one parameterized test for readability.

**Good example:**

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.assertThat;

class ValidatorTest {

    @ParameterizedTest
    @CsvSource({"0,false", "5,true", "120,false"})
    void range(int value, boolean ok) {
        assertThat(Validator.inRange(value)).isEqualTo(ok);
    }
}
```

**Bad example:**

```java
// Bad: three nearly identical @Test methods differing only by input literals
```

### Example 7: Unit test class naming convention

Title: `Test` suffix for Surefire; `IT` for Failsafe — avoid `Spec` and other mismatches
Description: Unit test class names must end with the suffix `Test` (e.g., `OrderServiceTest`, `HelloControllerTest`). Maven Surefire picks up classes matching `**/*Test.java`, `**/Test*.java`, `**/*Tests.java`, and `**/*TestCase.java` by default. Using a different suffix (or none) silently excludes tests from the build. Reserve the `IT` suffix for integration tests executed by Maven Failsafe (`**/*IT.java`) when configured. Consistency also makes it trivial to navigate from a production class (`OrderService`) to its test (`OrderServiceTest`).

**Good example:**

```java
// File: src/test/java/com/example/OrderServiceTest.java
// Surefire picks this up by default because the name ends with "Test"

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {          // ✔ "Test" suffix — Surefire runs this class

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void returnsOrderWhenFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(new Order(1L, "SKU-A")));
        assertThat(orderService.getOrder(1L).sku()).isEqualTo("SKU-A");
    }
}

// File: src/test/java/com/example/OrderRepositoryIT.java
// Maven Failsafe runs this during "mvn verify" when the name ends with "IT"

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

@MicronautTest
class OrderRepositoryIT {          // ✔ "IT" suffix — Failsafe (integration) when configured

    @Test
    void loadsFromDatabase() {
        // ...
    }
}
```

**Bad example:**

```java
// File: src/test/java/com/example/OrderServiceSpec.java
// Surefire does NOT pick this up — tests are silently skipped during "mvn test"

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceSpec {          // ✘ "Spec" suffix — Surefire skips this class silently

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void returnsOrderWhenFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(new Order(1L, "SKU-A")));
        assertThat(orderService.getOrder(1L).sku()).isEqualTo("SKU-A");
        // This test never runs — the class name doesn't match Surefire's default pattern
    }
}
```


## Output Format

- **ANALYZE** the test suite: which tests need `@MicronautTest` vs plain JUnit + Mockito, unnecessary container bootstrap for pure domain logic, missing `@MockBean`/`@Replaces` for collaborators, weak `HttpClient` assertions, copy-pasted methods suitable for `@ParameterizedTest`, flaky time or environment coupling, and non-deterministic config without `@Property`
- **CATEGORIZE** findings by impact (SPEED, FLAKINESS, CLARITY) and by layer (service, HTTP, JSON, config)
- **APPLY** improvements: Mockito-first for pure services, `@MockBean`/`@Replaces` for collaborators, `HttpClient` assertions, `@Property` overrides, `@ParameterizedTest` for data-driven cases, and correct `*Test` / `*IT` class name suffixes
- **IMPLEMENT** changes incrementally; keep tests green after each step and verify with `./mvnw clean verify`
- **EXPLAIN** when to use `@131-java-testing-unit-testing` vs `@MicronautTest` vs `@522-frameworks-micronaut-testing-integration-tests` if the user is mixing concerns
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after substantive test refactors


## Safeguards

- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` or `mvn compile` before ANY test refactoring — compilation failure is a HARD STOP
- **CRITICAL VALIDATION**: Run `./mvnw clean verify` after changes and confirm the full test suite is green before promoting
- **SPEED**: Do not use `@MicronautTest` for pure domain logic — it slows the suite without benefit
- **CONTEXT SCOPE**: Do not add `@MicronautTest` only to “make it work” — fix missing `@MockBean`/`@Replaces` and `@Property` overrides first
- **MOCK DRIFT**: Mocks that do not match real contracts hide integration bugs — pair unit tests with `@522-frameworks-micronaut-testing-integration-tests` where the Micronaut wiring must be exercised end-to-end
- **TEST PROPERTY ISOLATION**: Prefer `@Property` on test classes over mutating `System` properties in lifecycle hooks — parallel tests and bean initialization order can make the latter flaky
- **INCREMENTAL SAFETY**: Refactor one test class at a time when converting heavy `@MicronautTest` classes to Mockito-first; verify with `./mvnw clean verify` between steps
- **SAFETY PROTOCOL**: Stop if tests fail after edits until the failure is understood