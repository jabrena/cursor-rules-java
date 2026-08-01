---
name: 130-java-testing-strategies-a-trip
description: Focused A-TRIP guidance for evaluating Java test quality, reliability, and maintainability.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java testing strategies

## Role

You are a Senior software engineer with extensive experience in Java software development and test design.

## Goal

Use A-TRIP to evaluate whether Java tests are good tests: Automatic, Thorough, Repeatable, Independent, and Professional.

## Constraints

Apply this reference when the user asks about flaky tests, brittle tests, manual test steps, shared state, order dependency, unclear assertions, noisy fixtures, or general test maintainability.

- Keep this reference focused on test quality characteristics; use RIGHT-BICEP for what behavior is missing and CORRECT for detailed boundary categories.
- Prefer deterministic test design over retries, sleeps, test ordering, or environment assumptions.
- Do not mask flakiness by weakening assertions or ignoring failures.

## Examples

### Table of contents

- Example 1: Automatic
- Example 2: Thorough
- Example 3: Repeatable
- Example 4: Independent
- Example 5: Professional

### Example 1: Automatic

Title: Tests run in normal build or focused test commands without manual setup.
Description: Automatic tests can run in CI and locally from a standard command. They do not require a developer to click through a UI, seed a database by hand, edit local files, or inspect logs manually. Validation signals: - The test is part of the Maven test lifecycle or a documented focused test command. - Required fixtures are created by the test or test harness. - Assertions decide pass/fail without human inspection.

**Good example:**

```java
class RegistrationServiceTest {

    @Test
    void register_validCommand_persistsUser() {
        InMemoryUserRepository users = new InMemoryUserRepository();
        RegistrationService service = new RegistrationService(users);

        service.register(new RegisterUser("ada@example.com"));

        assertThat(users.findByEmail("ada@example.com")).isPresent();
    }
}
```

**Bad example:**

```java
@Test
void register_validCommand_checkDatabaseManually() {
    service.register(new RegisterUser("ada@example.com"));

    System.out.println("Now inspect the local database and confirm the row exists");
}
```

### Example 2: Thorough

Title: Cover meaningful behavior without duplicating implementation.
Description: Thorough tests cover important paths, risks, and observable outcomes. They are not the same as tests that mirror every line of implementation. Validation signals: - Main path, error path, and important variants are covered. - Assertions verify observable behavior and state changes. - The test suite avoids duplicating the production algorithm as its oracle.

**Good example:**

```java
class ShippingQuoteTest {

    @ParameterizedTest
    @CsvSource({
        "STANDARD, 2.00",
        "EXPRESS, 9.50",
        "OVERNIGHT, 25.00"
    })
    void quote_knownShippingMethods_returnsConfiguredPrice(ShippingMethod method, BigDecimal expected) {
        ShippingQuote quote = quotes.quoteFor(method);

        assertThat(quote.amount()).isEqualByComparingTo(expected);
    }

    @Test
    void quote_unknownShippingMethod_rejectsCommand() {
        assertThatThrownBy(() -> quotes.quoteFor(null))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
```

### Example 3: Repeatable

Title: Tests produce the same result in any order and environment.
Description: Repeatable tests control time, randomness, filesystem paths, network behavior, locale, and data setup. They fail because behavior changed, not because the environment changed. Validation signals: - Time uses `Clock` or an equivalent controllable time source. - Random values use a fixed seed or explicit generated fixtures. - Files use isolated temporary directories. - Data is created per test and does not depend on prior runs.

**Good example:**

```java
class TrialPeriodTest {

    private final Clock fixedClock = Clock.fixed(
        Instant.parse("2026-06-27T10:15:30Z"),
        ZoneOffset.UTC
    );

    @Test
    void expiresAt_addsThirtyDaysFromRegistrationTime() {
        TrialPeriod trial = new TrialPeriod(fixedClock);

        Instant expiresAt = trial.expiresAt();

        assertThat(expiresAt).isEqualTo(Instant.parse("2026-07-27T10:15:30Z"));
    }

    @Test
    void generatedCoupon_isDeterministicWithFixedSeed() {
        CouponGenerator generator = new CouponGenerator(new Random(42));

        assertThat(generator.nextCode()).isEqualTo("AHWMARNQ");
    }

    @Test
    void export_writesToIsolatedDirectory(@TempDir Path tempDir) {
        Path report = exporter.exportTo(tempDir);

        assertThat(report).exists().startsWith(tempDir);
    }
}
```

**Bad example:**

```java
@Test
void expiresAt_usesCurrentTime() {
    TrialPeriod trial = new TrialPeriod(Clock.systemUTC());

    assertThat(trial.expiresAt()).isAfter(Instant.now());
}
```

### Example 4: Independent

Title: Tests do not share mutable state or rely on execution order.
Description: Independent tests can run alone, together, repeatedly, and in parallel without changing the result. Validation signals: - Each test creates its own subject and fixtures. - Static mutable state is reset or avoided. - No test depends on a side effect from another test. - There is no reliance on method ordering.

**Good example:**

```java
class CartTest {

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
    }

    @Test
    void addItem_increasesItemCount() {
        cart.add(ProductId.of("P-1"));

        assertThat(cart.itemCount()).isEqualTo(1);
    }

    @Test
    void emptyCart_hasZeroItems() {
        assertThat(cart.itemCount()).isZero();
    }
}
```

**Bad example:**

```java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartTest {
    private static final Cart sharedCart = new Cart();

    @Test
    @Order(1)
    void addItem() {
        sharedCart.add(ProductId.of("P-1"));
    }

    @Test
    @Order(2)
    void itemCountReflectsPreviousTest() {
        assertThat(sharedCart.itemCount()).isEqualTo(1);
    }
}
```

### Example 5: Professional

Title: Test code is readable, maintainable, and low-noise.
Description: Professional tests communicate intent. They use descriptive names, clear assertions, maintainable fixtures, and limited noise. They do not hide important setup in magic helpers or bury the behavior under incidental detail. Validation signals: - Test names describe condition and expected result. - Assertions explain the behavior under test. - Fixture builders expose relevant values and default irrelevant ones. - Logs, sleeps, broad catch blocks, and comments are not used as substitutes for assertions.

**Good example:**

```java
class PasswordPolicyTest {

    @Test
    void validate_missingDigit_returnsReadableViolation() {
        PasswordPolicy policy = new PasswordPolicy();

        ValidationResult result = policy.validate("long-password");

        assertThat(result.violations())
            .containsExactly("Password must contain at least one digit");
    }
}
```

**Bad example:**

```java
@Test
void testPolicy() throws Exception {
    Object result = helper("x", true, false, 3, null);
    System.out.println(result);
    assertThat(result.toString()).contains("digit");
}
```

## Output Format

- **IDENTIFY** A-TRIP violations by characteristic: Automatic, Thorough, Repeatable, Independent, Professional
- **EXPLAIN** the reliability or maintainability risk of each violation
- **RECOMMEND** deterministic fixes such as fixed `Clock`, seeded randomness, isolated temp files, per-test fixtures, and clearer assertions
- **VALIDATE** compile and verification commands before and after code changes when implementation is requested