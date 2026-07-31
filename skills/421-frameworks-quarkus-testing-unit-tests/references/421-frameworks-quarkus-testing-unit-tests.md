---
name: 421-frameworks-quarkus-testing-unit-tests
description: Use when you need to write fast unit tests for Quarkus applications — including pure tests with @ExtendWith(MockitoExtension.class) for CDI @ApplicationScoped beans (instantiated manually), @QuarkusTest with @InjectMock to replace CDI dependencies in focused tests, REST Assured only when HTTP surface is under test, and @ParameterizedTest for data-driven cases. For framework-agnostic Java use @131-java-testing-unit-testing. For full integration use @422-frameworks-quarkus-testing-integration-tests.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Quarkus Unit Testing

## Role

You are a Senior software engineer with extensive experience in Quarkus and JUnit 5 testing

## Goal

Quarkus tests should be as fast as possible: prefer **plain JUnit 5 + Mockito** for classes that do not need the Quarkus container. When CDI wiring matters but a full integration test is too heavy, use `@QuarkusTest` with `@InjectMock` or `@InjectSpy` to substitute collaborators. Reserve `@QuarkusTest` + REST Assured for resource-focused tests; avoid booting Quarkus for pure domain logic. For time-dependent behaviour, inject `java.time.Clock` (CDI can supply `Clock.systemUTC()` in prod and `Clock.fixed(...)` or a test double in tests) — do not rely on `Instant.now()` in code you unit-test with Mockito; final time types and static `now()` methods are awkward to fake without an injectable clock or supplier.

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

- Example 1: Pure Mockito test
- Example 2: Injectable `Clock` and time APIs Mockito cannot stub
- Example 3: @QuarkusTest with @InjectMock
- Example 4: @QuarkusTest REST Assured slice
- Example 5: @InjectSpy for partial CDI bean mocking
- Example 6: Parameterized unit tests
- Example 7: @QuarkusTestProfile for test-specific configuration
- Example 8: Test class naming convention

### Example 1: Pure Mockito test

Title: No Quarkus bootstrap
Description: Instantiate the service under test with mocks — fastest feedback for business rules.

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
    PricingService pricing;

    @Test
    void appliesDiscount() {
        when(discounts.rateFor("VIP")).thenReturn(0.1);
        assertThat(pricing.price(100, "VIP")).isEqualTo(90);
    }
}
```

**Bad example:**

```java
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PricingServiceTest {
    @Test
    void appliesDiscount() {
        // Bad: full container for arithmetic — use Mockito-only test
    }
}
```

### Example 2: Injectable `Clock` and time APIs Mockito cannot stub

Title: Inject `Clock` into `@ApplicationScoped` beans; use `Clock.fixed` or `@Mock Clock` in tests
Description: **`Instant.now()` and `LocalDate.now()`** are static entry points on **final** types — Mockito does not replace them, and mocking `Instant` is the wrong layer. **Inject `java.time.Clock`** (field or constructor on CDI beans) and use `clock.instant()` / `LocalDate.now(clock)`. In unit tests, either construct the bean with **`Clock.fixed(instant, zoneId)`** or use **`@Mock Clock`** with `when(clock.instant()).thenReturn(…)`. For **`@QuarkusTest`**, register a test `Clock` via `QuarkusTestProfile` config, a test-only CDI producer, or keep business logic in a plain class tested with **`@ExtendWith(MockitoExtension.class)`** and a fixed clock (fastest). **Likewise:** `UUID.randomUUID()`, `System.nanoTime()`, unseeded `Random` — inject `Supplier<UUID>`, `LongSupplier`, or `Random` so tests control outputs without bytecode manipulation.

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

// @ApplicationScoped bean: @Inject Clock clock — constructor shown for tests
class PromotionService {
    private final Clock clock;
    PromotionService(Clock clock) { this.clock = clock; }

    boolean isActive(Instant validFrom, Instant validTo) {
        Instant now = clock.instant();
        return !now.isBefore(validFrom) && now.isBefore(validTo);
    }
}

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock
    Clock clock;

    @Test
    void isActive_whenNowInsideWindow_returnsTrue() {
        when(clock.instant()).thenReturn(Instant.parse("2024-07-01T12:00:00Z"));
        var svc = new PromotionService(clock);
        var from = Instant.parse("2024-07-01T00:00:00Z");
        var to = Instant.parse("2024-07-02T00:00:00Z");
        assertThat(svc.isActive(from, to)).isTrue();
    }

    @Test
    void isActive_usesFixedClock_withoutMockito() {
        Clock fixed = Clock.fixed(
            Instant.parse("2024-07-01T12:00:00Z"), ZoneOffset.UTC);
        var svc = new PromotionService(fixed);
        var from = Instant.parse("2024-07-01T00:00:00Z");
        var to = Instant.parse("2024-07-02T00:00:00Z");
        assertThat(svc.isActive(from, to)).isTrue();
    }
}
```

**Bad example:**

```java
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

class PromotionService {

    boolean isActive(Instant validFrom, Instant validTo) {
        Instant now = Instant.now(); // Bad: static — Mockito cannot stub; flaky tests
        return !now.isBefore(validFrom) && now.isBefore(validTo);
    }
}

class PromotionServiceTest {

    @Test
    void isActive_flaky() {
        var svc = new PromotionService();
        assertThat(svc.isActive(
            Instant.parse("2000-01-01T00:00:00Z"),
            Instant.parse("3000-01-01T00:00:00Z"))).isTrue();
    }
}
```

### Example 3: @QuarkusTest with @InjectMock

Title: Replace a CDI collaborator
Description: When the class under test is produced by CDI and you need to isolate external systems, `@InjectMock` replaces the bean in the test archive.

**Good example:**

```java
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class OrderAppServiceTest {

    @InjectMock
    PaymentGateway payments;

    @Inject
    OrderAppService orders;

    @Test
    void chargesOnPlace() {
        Mockito.when(payments.charge(Mockito.any())).thenReturn(true);
        assertThat(orders.place(1L)).isTrue();
    }
}
```

**Bad example:**

```java
@QuarkusTest
class OrderAppServiceTest {
    // Bad: hitting real PaymentGateway in unit scope — flakiness and slow tests
}
```

### Example 4: @QuarkusTest REST Assured slice

Title: Test JAX-RS resources for routing, serialization, and status codes
Description: Use `@QuarkusTest` with REST Assured for JAX-RS resource tests. Replace service dependencies with `@InjectMock` to keep the test focused on HTTP routing, JSON serialization, and status codes. Assert the response body with JsonPath matchers rather than deserializing a full DTO.

**Good example:**

```java
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

record BookDTO(long id, String title, String author) {}

@QuarkusTest
class BookResourceTest {

    @InjectMock
    BookService bookService;

    @Test
    void getBook_returns200WithBody() {
        // Given
        when(bookService.findById(1L)).thenReturn(Optional.of(new BookDTO(1L, "Quarkus in Action", "Jane")));

        // When / Then
        given()
            .when().get("/books/1")
            .then()
                .statusCode(200)
                .body("title", equalTo("Quarkus in Action"))
                .body("author", equalTo("Jane"));
    }

    @Test
    void getBook_returns404WhenAbsent() {
        // Given
        when(bookService.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        given()
            .when().get("/books/999")
            .then()
                .statusCode(404);
    }

    @Test
    void createBook_returns201WithLocation() {
        // Given
        when(bookService.create(any())).thenReturn(new BookDTO(5L, "New Book", "Alice"));

        // When / Then
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"title":"New Book","author":"Alice"}
                """)
            .when().post("/books")
            .then()
                .statusCode(201)
                .header("Location", containsString("/books/5"));
    }
}
```

**Bad example:**

```java
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

// Bad: no @InjectMock — test hits the real BookService and its real dependencies
// (database, external APIs), making this an integration test in disguise
@QuarkusTest
class BookResourceTest {

    @Test
    void getBook_returns200() {
        given()
            .when().get("/books/1")
            .then()
                .statusCode(200)
                .body("title", equalTo("Some Title")); // depends on actual DB state
    }
}
```

### Example 5: @InjectSpy for partial CDI bean mocking

Title: Wrap a real CDI bean as a Mockito spy to verify calls without replacing behaviour
Description: `@InjectSpy` wraps the real CDI bean in a Mockito spy. The real implementation runs unless you explicitly stub a method. Use it when you want to verify that a side-effect method (e.g. audit logging, event publishing) was called, while still executing the production code path. Prefer `@InjectMock` when you want complete isolation from the real implementation.

**Good example:**

```java
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class OrderServiceTest {

    @InjectSpy
    AuditService auditService; // real bean wrapped as spy

    @InjectMock
    PaymentGateway paymentGateway; // replaced entirely with a mock

    @Inject
    OrderService orderService; // production bean — uses the spy/mock above

    @Test
    void placeOrder_recordsAuditEvent() {
        // Given
        when(paymentGateway.charge(1L, 99.0)).thenReturn(true);

        // When
        orderService.place(1L, 99.0);

        // Then — verify the real audit method was called
        verify(auditService).record("order-placed", 1L);
    }
}
```

**Bad example:**

```java
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class OrderServiceTest {

    // Bad: @InjectMock replaces AuditService entirely with a mock
    // The real audit logic never runs — no guarantee the production path is exercised
    @InjectMock
    AuditService auditService;

    @Inject
    OrderService orderService;

    @Test
    void placeOrder_recordsAuditEvent() {
        when(auditService.record("order-placed", 1L)).thenReturn(null);
        orderService.place(1L, 99.0);
        verify(auditService).record("order-placed", 1L);
        // Production logic in AuditService is never tested here
    }
}
```

### Example 6: Parameterized unit tests

Title: @CsvSource for inline tabular data; @MethodSource for complex objects
Description: Replace copy-pasted test methods that differ only in input values with `@ParameterizedTest`. Use `@CsvSource` for simple inline rows and `@MethodSource` for complex domain objects. Combine with `@ExtendWith(MockitoExtension.class)` — no `@QuarkusTest` needed for pure service logic.

**Good example:**

```java
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

record Product(long id, String category, double price) {}

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    PricingService pricingService;

    @ParameterizedTest
    @CsvSource({
        "ELECTRONICS, 100.0, 10.0",
        "CLOTHING,    200.0, 40.0",
        "FOOD,         50.0,  2.5"
    })
    void appliesCorrectDiscount(String category, double price, double expectedDiscount) {
        // Given
        when(productRepository.findById(1L))
            .thenReturn(Optional.of(new Product(1L, category, price)));

        // When / Then
        assertThat(pricingService.calculateDiscount(1L)).isEqualTo(expectedDiscount);
    }

    static Stream<String> invalidCategories() {
        return Stream.of("", "  ", "UNKNOWN");
    }

    @ParameterizedTest
    @MethodSource("invalidCategories")
    void rejectsInvalidCategory(String category) {
        // Given
        when(productRepository.findById(2L))
            .thenReturn(Optional.of(new Product(2L, category, 100.0)));

        // When / Then
        assertThatThrownBy(() -> pricingService.calculateDiscount(2L))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
```

**Bad example:**

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    @Mock ProductRepository productRepository;
    @InjectMocks PricingService pricingService;

    // Bad: three copy-pasted test methods with identical structure — use @ParameterizedTest
    @Test void appliesElectronicsDiscount() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product(1L, "ELECTRONICS", 100.0)));
        assertThat(pricingService.calculateDiscount(1L)).isEqualTo(10.0);
    }
    @Test void appliesClothingDiscount() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product(1L, "CLOTHING", 200.0)));
        assertThat(pricingService.calculateDiscount(1L)).isEqualTo(40.0);
    }
    @Test void appliesFoodDiscount() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product(1L, "FOOD", 50.0)));
        assertThat(pricingService.calculateDiscount(1L)).isEqualTo(2.5);
    }
}
```

### Example 7: @QuarkusTestProfile for test-specific configuration

Title: Override properties and supply test-only beans without mutating global config
Description: Implement `QuarkusTestProfile` to supply config overrides and test-only CDI beans for a specific test class. Annotate the test class with `@TestProfile`. This is the Quarkus equivalent of Spring Boot's `@TestConfiguration` + `@ActiveProfiles` combination. Use `%test` properties in `application.properties` for global test defaults; use `QuarkusTestProfile` when individual test classes need different values.

**Good example:**

```java
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

// Test profile: config overrides applied only when this profile is active
public class PremiumFeatureProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "feature.premium.enabled", "true",
            "app.max-items-per-page", "5"
        );
    }
}

@QuarkusTest
@TestProfile(PremiumFeatureProfile.class) // activates overrides for this class only
class FeatureServiceTest {

    @Inject
    FeatureService featureService;

    @Test
    void premiumEnabled_whenProfileConfigured() {
        assertThat(featureService.isPremiumEnabled()).isTrue();
    }

    @Test
    void maxItemsPerPage_limitedByProfileConfig() {
        assertThat(featureService.getMaxItemsPerPage()).isEqualTo(5);
    }
}
```

**Bad example:**

```java
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class FeatureServiceTest {

    @Inject
    FeatureService featureService;

    // Bad: mutating system properties in @BeforeEach/@AfterEach is fragile —
    // parallel tests can interfere; properties may not be read by already-started CDI beans
    @BeforeEach
    void setUp() {
        System.setProperty("feature.premium.enabled", "true");
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("feature.premium.enabled");
    }

    @Test
    void premiumEnabled() {
        assertThat(featureService.isPremiumEnabled()).isTrue(); // may still be false — CDI bean already initialised
    }
}
```

### Example 8: Test class naming convention

Title: Test suffix for Surefire; IT suffix for Failsafe / @QuarkusIntegrationTest
Description: Maven Surefire picks up `**/*Test.java` by default; Maven Failsafe picks up `**/*IT.java`. Use the `Test` suffix for fast unit and `@QuarkusTest` focused tests, and the `IT` suffix for `@QuarkusIntegrationTest` tests that run against a packaged artifact. Misnamed classes are silently skipped, giving a false green build.

**Good example:**

```java
// File: src/test/java/com/example/OrderServiceTest.java
// Maven Surefire runs this during "mvn test" because the name ends with "Test"

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

    @Mock OrderRepository orderRepository;
    @InjectMocks OrderService orderService;

    @Test
    void returnsOrderWhenFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(new Order(1L, "SKU-A")));
        assertThat(orderService.getOrder(1L).sku()).isEqualTo("SKU-A");
    }
}

// File: src/test/java/com/example/OrderResourceIT.java
// Maven Failsafe runs this during "mvn verify" because the name ends with "IT"

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
class OrderResourceIT {           // ✔ "IT" suffix — Failsafe runs against packaged artifact

    @Test
    void healthEndpointIsReachable() {
        given().when().get("/q/health").then().statusCode(200);
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

    @Mock OrderRepository orderRepository;
    @InjectMocks OrderService orderService;

    @Test
    void returnsOrderWhenFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(new Order(1L, "SKU-A")));
        assertThat(orderService.getOrder(1L).sku()).isEqualTo("SKU-A");
        // This test never runs — wrong class name suffix
    }
}
```


## Output Format

- **ANALYZE** the test suite: identify which tests need `@QuarkusTest` vs plain JUnit + Mockito, flag unnecessary Quarkus bootstrap for pure domain logic, and spot missing `@InjectMock` / `@InjectSpy` separations, copy-pasted methods suitable for `@ParameterizedTest`, and flaky time or env coupling
- **CATEGORIZE** findings by impact (SPEED for unnecessary container boot, FLAKINESS for wall-clock or real-dependency coupling, CLARITY for copy-paste and missing structure)
- **APPLY** improvements: convert container-free service tests to `@ExtendWith(MockitoExtension.class)`, narrow `@QuarkusTest` classes with `@InjectMock`, introduce `@InjectSpy` for partial mocks, add `@ParameterizedTest` for data-driven cases, use `@QuarkusTestProfile` for test-specific config, and fix test class name suffixes
- **IMPLEMENT** changes incrementally; keep tests green after each step and verify with `./mvnw clean verify`
- **EXPLAIN** when to use `@131-java-testing-unit-testing` vs `@QuarkusTest` vs `@422-frameworks-quarkus-testing-integration-tests` if the user is mixing concerns
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after substantive test refactors


## Safeguards

- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` or `mvn compile` before ANY test refactoring — compilation failure is a HARD STOP
- **CRITICAL VALIDATION**: Run `./mvnw clean verify` after changes and confirm the full test suite is green before promoting
- **SPEED**: Never use `@QuarkusTest` for tests that never touch CDI wiring or HTTP routing — use `@ExtendWith(MockitoExtension.class)` for pure domain and service logic
- **MOCK ACCURACY**: Over-mocking with `@InjectMock` can hide real wiring issues — pair unit tests with `@QuarkusTest` integration tests (`@422`) where the CDI graph must be exercised end-to-end
- **TEST PROFILE ISOLATION**: `@QuarkusTestProfile` triggers a Quarkus restart between test classes — limit the number of distinct profiles to avoid slow test cycles; consolidate config overrides where possible
- **INCREMENTAL SAFETY**: Refactor one test class at a time when converting heavy `@QuarkusTest` classes to Mockito-first; verify with `./mvnw clean verify` between steps