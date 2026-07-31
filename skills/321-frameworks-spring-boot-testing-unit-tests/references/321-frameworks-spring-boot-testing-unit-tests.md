---
name: 321-frameworks-spring-boot-testing-unit-tests
description: Use when you need to write unit tests for Spring Boot applications — including pure unit tests with @ExtendWith(MockitoExtension.class) for @Service/@Component, slice tests with @WebMvcTest and @MockBean/@MockitoBean for controllers, @JsonTest for JSON serialization, parameterized tests with @CsvSource/@MethodSource, test profiles, and @TestConfiguration. For framework-agnostic Java use @131-java-testing-unit-testing. For integration tests use @322-frameworks-spring-boot-testing-integration-tests.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Spring Boot Unit Testing with Mockito

## Role

You are a Senior software engineer with extensive experience in Spring Boot testing

## Goal

Spring Boot unit tests mix fast, context-free tests for domain and application services with narrow slice tests for web and JSON. Use Mockito (`@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`) for beans that do not need Spring, and MVC/JSON slices (`@WebMvcTest`, `@JsonTest`) with `@MockitoBean` when you need MockMvc or JacksonTester. Prefer constructor-injected beans and Java records so tests stay simple and readable. For time-sensitive logic, inject `java.time.Clock` (or a small `Supplier` of `Instant` / `TimeProvider` abstraction) instead of calling `Instant.now()` or `LocalDate.now()` directly — Mockito cannot mock static factories, and `Instant` / `LocalDateTime` are final value types; a fixed `Clock` or a mocked `Clock` gives deterministic tests.

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

- Example 1: Pure unit tests with MockitoExtension
- Example 2: Injectable `Clock` and time that resists mocking
- Example 3: @WebMvcTest for controllers
- Example 4: @JsonTest for JSON mapping
- Example 5: @MockBean in slice tests
- Example 6: Parameterized unit tests
- Example 7: Test profiles and @TestConfiguration
- Example 8: @MockitoBean in Spring Boot 4.0.x
- Example 9: Unit test class naming convention
- Example 10: Test-specific beans and configuration

### Example 1: Pure unit tests with MockitoExtension

Title: No Spring context for @Service and @Component
Description: Annotate tests with `@ExtendWith(MockitoExtension.class)`, declare collaborators with `@Mock`, and the unit under test with `@InjectMocks`. Stub behavior with `when` and verify interactions with `verify`. Omit `times(1)` — it is Mockito's default and adds noise. Use Java records for domain objects and structure tests with Given-When-Then. Do not use `@SpringBootTest` for simple service logic.

**Good example:**

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Domain records
record CreateOrderRequest(String productName, int quantity) {}
record Order(Long id, String productName, int quantity) {}

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreateOrder() {
        // Given
        var request = new CreateOrderRequest("Product A", 2);
        var order = new Order(1L, "Product A", 2);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        Order result = orderService.createOrder(request);

        // Then
        assertThat(result.productName()).isEqualTo("Product A");
        verify(orderRepository).save(any(Order.class));  // times(1) is the default — omit it
    }

    @Test
    void shouldThrowWhenOrderNotFound() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> orderService.getOrder(999L))
            .isInstanceOf(OrderNotFoundException.class);
    }
}
```

**Bad example:**

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest  // loads full ApplicationContext — very slow for a plain service test
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldCreateOrder() {
        Order result = orderService.createOrder(new CreateOrderRequest("Product A", 2));
        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).save(result);  // times(1) is redundant noise
    }
}
```

### Example 2: Injectable `Clock` and time that resists mocking

Title: Prefer `Clock.fixed` or a mocked `Clock` over `Instant.now()` in production code
Description: **Why Mockito struggles:** `Instant.now()`, `LocalDate.now()`, and `ZonedDateTime.now()` are static calls on **final** value types — you cannot Mockito-spy them, and you should not mock `Instant` itself. **Inject `java.time.Clock`** (constructor injection on `@Service` / `@Component`) and use `Instant.now(clock)` / `LocalDate.now(clock)` so tests pass a **`Clock.fixed(…, zone)`** for determinism, or `@Mock Clock` with `when(clock.instant()).thenReturn(…)` when you need multiple instants in one test. **Same idea elsewhere:** `UUID.randomUUID()`, `ThreadLocalRandom`, `System.nanoTime()` — inject `Supplier<UUID>`, `Random` (seeded in tests), or a tiny abstraction instead of static calls you cannot stub cleanly. Register a default `Clock` bean in `@Configuration` (`@Bean Clock clock() { return Clock.systemUTC(); }`) if the framework should supply it; in tests, override with `@TestConfiguration` + `@Primary` or pass `Clock.fixed` directly in pure Mockito tests.

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

// Production: clock injected — no static Instant.now()
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

// Bad: static time — unit test depends on wall clock or needs brittle "approximate now" assertions
class PromotionService {

    boolean isActive(Instant validFrom, Instant validTo) {
        Instant now = Instant.now(); // cannot stub; flaky / non-deterministic tests
        return !now.isBefore(validFrom) && now.isBefore(validTo);
    }
}

class PromotionServiceTest {

    @Test
    void isActive_flaky() {
        var svc = new PromotionService();
        // Either passes only in a narrow real-time window, or uses loose tolerances
        assertThat(svc.isActive(
            Instant.parse("2000-01-01T00:00:00Z"),
            Instant.parse("3000-01-01T00:00:00Z"))).isTrue();
    }
}
```

### Example 3: @WebMvcTest for controllers

Title: MockMvc slice with @MockBean for services
Description: Use `@WebMvcTest(YourController.class)` to load only MVC infrastructure. Inject `MockMvc`, mock dependencies with `@MockBean`, and assert status and JSON with `MockMvc` matchers. **Spring Boot 4.0.x:** use `@MockitoBean` in place of `@MockBean` for those dependencies (see the dedicated example below). Avoid `TestRestTemplate` with full context for unit-style controller tests.

**Good example:**

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldReturnUserWhenValidId() throws Exception {
        User user = new User(1L, "John Doe", "john@example.com");
        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        when(userService.findById(999L)).thenThrow(new UserNotFoundException(999L));

        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isNotFound());
    }
}
```

**Bad example:**

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnUser() {
        ResponseEntity<User> response = restTemplate.getForEntity("/api/users/1", User.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

### Example 4: @JsonTest for JSON mapping

Title: JacksonTester without full Spring Boot
Description: Use `@JsonTest` to auto-configure Jackson and inject `JacksonTester<T>` for round-trip serialization and JSON assertions. Prefer this over `ObjectMapper` under `@SpringBootTest` for DTO mapping tests. **Important API distinction**: `hasJsonPathNumberValue("$.id")` only verifies that a numeric value *exists* at the path — it does NOT compare the value. To assert the actual value, use `extractingJsonPathNumberValue("$.id").isEqualTo(1)` (number) or `extractingJsonPathStringValue("$.name").isEqualTo("John Doe")` (string). Passing a second argument to `hasJsonPath*Value(expression, args...)` is a printf-style format argument for the expression string, not a value comparison.

**Good example:**

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import static org.assertj.core.api.Assertions.assertThat;

record User(Long id, String name, String email) {}

@JsonTest
class UserJsonTest {

    @Autowired
    private JacksonTester<User> json;

    @Test
    void shouldSerializeUser() throws Exception {
        var user = new User(1L, "John Doe", "john@example.com");
        var written = json.write(user);

        // extractingJsonPath* compares the actual value — hasJsonPath*Value only checks existence
        assertThat(written).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(written).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(written).extractingJsonPathStringValue("$.email").isEqualTo("john@example.com");
    }

    @Test
    void shouldDeserializeUser() throws Exception {
        String content = """
            {"id": 1, "name": "John Doe", "email": "john@example.com"}
            """;

        assertThat(json.parse(content))
            .usingRecursiveComparison()
            .isEqualTo(new User(1L, "John Doe", "john@example.com"));
    }
}
```

**Bad example:**

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest  // loads full context just to get an ObjectMapper
class UserJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeUser() throws Exception {
        User user = new User(1L, "John Doe", "john@example.com");
        String json = objectMapper.writeValueAsString(user);
        assertThat(json).contains("John Doe");  // substring match — fragile and incomplete

        // Also wrong: hasJsonPathNumberValue("$.id", 1) does NOT compare values;
        // the second arg is a printf format arg for the expression, not a value matcher
    }
}
```

### Example 5: @MockBean in slice tests

Title: Register Mockito mocks in the Spring test context
Description: In `@WebMvcTest` (and similar slices), declare each dependency the controller needs as `@MockBean` so Spring can inject mocks. Use `when`/`verify` as usual. If a bean is missing, the context fails to start. Use record accessors (`order.productName()`) rather than JavaBean getters when domain types are records.

**Good example:**

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Domain records
record CreateOrderRequest(String productName, int quantity) {}
record Order(Long id, String productName, int quantity) {}

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private PaymentService paymentService;

    @Test
    void shouldCreateOrder() throws Exception {
        // Given
        var order = new Order(1L, "Product A", 2);
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(order);

        // When / Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"productName": "Product A", "quantity": 2}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.productName").value(order.productName()));  // record accessor
    }
}
```

**Bad example:**

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateOrder() throws Exception {
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isCreated());
    }
}
```

### Example 6: Parameterized unit tests

Title: @CsvSource and @MethodSource with MockitoExtension
Description: Use `@ParameterizedTest` with `@CsvSource` for inline tabular data or `@MethodSource` for complex objects. Combine with `@ExtendWith(MockitoExtension.class)` to cover multiple input scenarios without duplicating test code. Each row acts as an independent Given-When-Then scenario.

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

record Product(Long id, String category, double price) {}

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DiscountService discountService;

    @ParameterizedTest
    @CsvSource({
        "ELECTRONICS, 100.0, 10.0",
        "CLOTHING,    200.0, 40.0",
        "FOOD,         50.0,  2.5"
    })
    void shouldApplyCorrectDiscount(String category, double price, double expectedDiscount) {
        // Given
        var product = new Product(1L, category, price);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When
        double discount = discountService.calculate(1L);

        // Then
        assertThat(discount).isEqualTo(expectedDiscount);
    }

    static Stream<String> invalidCategories() {
        return Stream.of("", "  ", "UNKNOWN");
    }

    @ParameterizedTest
    @MethodSource("invalidCategories")
    void shouldRejectInvalidCategory(String category) {
        // Given
        var product = new Product(2L, category, 100.0);
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));

        // When / Then
        assertThatThrownBy(() -> discountService.calculate(2L))
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
class DiscountServiceTest {

    @Mock ProductRepository productRepository;
    @InjectMocks DiscountService discountService;

    // Copy-pasted tests — identical structure repeated for each input variant
    @Test void shouldApplyElectronicsDiscount() {
        when(productRepository.findById(1L))
            .thenReturn(Optional.of(new Product(1L, "ELECTRONICS", 100.0)));
        assertThat(discountService.calculate(1L)).isEqualTo(10.0);
    }

    @Test void shouldApplyClothingDiscount() {
        when(productRepository.findById(1L))
            .thenReturn(Optional.of(new Product(1L, "CLOTHING", 200.0)));
        assertThat(discountService.calculate(1L)).isEqualTo(40.0);
    }

    @Test void shouldApplyFoodDiscount() {
        when(productRepository.findById(1L))
            .thenReturn(Optional.of(new Product(1L, "FOOD", 50.0)));
        assertThat(discountService.calculate(1L)).isEqualTo(2.5);
    }
}
```

### Example 7: Test profiles and @TestConfiguration

Title: Stable time, beans, and test-only wiring
Description: Use `@ActiveProfiles("test")` to isolate configuration. Use `@TestConfiguration` inner classes or static nested config to define beans such as a fixed `Clock` with `@Primary` for deterministic assertions. Avoid asserting on “now” without controlling time.

**Good example:**

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        Clock testClock() {
            return Clock.fixed(Instant.parse("2023-12-01T10:00:00Z"), ZoneOffset.UTC);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldUseFixedTimeForTesting() throws Exception {
        mockMvc.perform(get("/api/users/current-time"))
            .andExpect(status().isOk())
            .andExpect(content().string("2023-12-01T10:00:00Z"));
    }
}
```

**Bad example:**

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldReturnCurrentTime() throws Exception {
        mockMvc.perform(get("/api/users/current-time"))
            .andExpect(status().isOk());  // no Clock control — assertion on "now" is flaky
    }
}
```

### Example 8: @MockitoBean in Spring Boot 4.0.x

Title: Standard replacement for the removed @MockBean
Description: Spring Boot 4.0.x uses `@MockitoBean` and `@MockitoSpyBean` from the `org.springframework.test.context.bean.override.mockito` package as the standard mock registration API. `@MockBean` and `@SpyBean` (from `org.springframework.boot.test.mock.mockito`) were deprecated in Spring Boot 3.4 and are no longer available in 4.x — replace all usages with `@MockitoBean` / `@MockitoSpyBean`.

**Good example:**

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;  // Spring Boot 4.0.x
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean  // Spring Boot 4.0.x standard — replaces removed @MockBean
    private UserService userService;

    @Test
    void shouldReturnUser() throws Exception {
        // Given
        var user = new User(1L, "Alice", "alice@example.com");
        when(userService.findById(1L)).thenReturn(user);

        // When / Then
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Alice"));
    }
}
```

**Bad example:**

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;  // removed in Spring Boot 4 — use @MockitoBean
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean  // removed in Spring Boot 4 — use @MockitoBean instead
    private UserService userService;

    @Test
    void shouldReturnUser() throws Exception {
        when(userService.findById(1L)).thenReturn(new User(1L, "Alice", "alice@example.com"));

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk());
    }
}
```

### Example 9: Unit test class naming convention

Title: Always suffix unit test classes with "Test"
Description: Unit test class names must end with the suffix `Test` (e.g., `OrderServiceTest`, `UserControllerTest`). Maven Surefire picks up classes matching `**/*Test.java`, `**/Test*.java`, `**/*Tests.java`, and `**/*TestCase.java` by default. Using a different suffix (or none) silently excludes tests from the build. Reserve the `IT` suffix for integration tests executed by Maven Failsafe (`**/*IT.java`). Consistency also makes it trivial to navigate from a production class (`OrderService`) to its test (`OrderServiceTest`).

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
    void shouldReturnOrderWhenFound() {
        // Given
        var order = new Order(1L, "Product A", 2);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When
        Order result = orderService.getOrder(1L);

        // Then
        assertThat(result.productName()).isEqualTo("Product A");
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
    void shouldReturnOrderWhenFound() {
        var order = new Order(1L, "Product A", 2);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrder(1L);
        assertThat(result.productName()).isEqualTo("Product A");
        // This test never runs — the class name doesn't match Surefire's default pattern
    }
}
```

### Example 10: Test-specific beans and configuration

Title: Use @TestConfiguration to isolate test doubles and avoid polluting the production context
Description: When defining beans exclusively for testing (like stubs, fakes, or custom test setup), place them in `src/test/java` and annotate the class with `@TestConfiguration`. Unlike standard `@Configuration`, `@TestConfiguration` is intentionally excluded from component scanning, ensuring it only applies when explicitly imported via `@Import` or nested inside a test class. Avoid putting test beans in `src/main/java` behind `@Profile("test")`.

**Good example:**

```java
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
class ExternalServiceTestConfig {

    @Bean
    @Primary
    ExternalServiceClient fakeExternalServiceClient() {
        return new FakeExternalServiceClient();
    }
}

// Usage in test:
// @org.springframework.boot.test.context.SpringBootTest
// @org.springframework.context.annotation.Import(ExternalServiceTestConfig.class)
// class MyIntegrationTest { ... }

interface ExternalServiceClient { }
class FakeExternalServiceClient implements ExternalServiceClient { }
```

**Bad example:**

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

// Anti-pattern 1: Standard @Configuration in src/test/java
// If component scanning accidentally reaches this package, it might override production beans
@Configuration
class BadTestConfig {
    @Bean
    ExternalServiceClient mockClient() {
        return new MockExternalServiceClient();
    }
}

// Anti-pattern 2: Test beans in src/main/java hidden behind a profile
// Pollutes production classpath with test dependencies and logic
@Configuration
@Profile("test")
class ProductionPollutingTestConfig {
    @Bean
    ExternalServiceClient testClient() {
        return new FakeExternalServiceClient();
    }
}

interface ExternalServiceClient { }
class MockExternalServiceClient implements ExternalServiceClient { }
class FakeExternalServiceClient implements ExternalServiceClient { }
```


## Output Format

- **ANALYZE** the test suite: pure Mockito vs slice tests, unnecessary `@SpringBootTest`, missing `@MockitoBean`/`@MockBean`, JSON coverage, and flaky time or environment
- **CATEGORIZE** findings by impact (SPEED, FLAKINESS, CLARITY) and by layer (service, web, JSON, config)
- **APPLY** improvements: convert heavy tests to `@ExtendWith(MockitoExtension.class)`, narrow `@WebMvcTest`/`@JsonTest`, add `@MockitoBean` where the slice needs mocks, introduce `@ActiveProfiles` and `@TestConfiguration` for determinism, replace copy-pasted tests with `@ParameterizedTest`, replace any remaining `@MockBean` with `@MockitoBean` (removed in Spring Boot 4), fix `hasJsonPath*Value` to `extractingJsonPath*Value` for value assertions
- **IMPLEMENT** changes incrementally; keep tests green after each step
- **EXPLAIN** when to use `@131-java-testing-unit-testing` vs `@322-frameworks-spring-boot-testing-integration-tests` if the user is mixing concerns
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after substantive test refactors


## Safeguards

- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` or `mvn compile` before ANY test refactoring
- **CRITICAL VALIDATION**: Run `./mvnw clean verify` after changes; ensure test suite passes
- **CONTEXT SCOPE**: Do not add `@SpringBootTest` only to “make it work” — fix missing slices and mocks first
- **MOCK ACCURACY**: Over-mocking or missing `verify` can hide integration issues — pair unit tests with integration tests where appropriate
- **INCREMENTAL SAFETY**: Refactor one test class or package at a time when converting to slices
- **SAFETY PROTOCOL**: Stop if tests fail after edits until the failure is understood