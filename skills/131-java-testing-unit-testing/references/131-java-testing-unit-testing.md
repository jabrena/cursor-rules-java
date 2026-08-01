---
name: 131-java-testing-unit-testing
description: Use when you need to review, improve, or write Java unit tests for framework-agnostic applications (no Spring Boot, Quarkus, Micronaut) — including migrating from JUnit 4 to JUnit 5, adopting AssertJ for fluent assertions, structuring tests with Given-When-Then, ensuring test independence, applying parameterized tests, mocking dependencies with Mockito, verifying boundary conditions (RIGHT-BICEP, CORRECT, A-TRIP), leveraging JSpecify null-safety annotations, or eliminating testing anti-patterns such as reflection-based tests or shared mutable state. For Spring Boot use @321-frameworks-spring-boot-testing-unit-tests. For Quarkus use @421-frameworks-quarkus-testing-unit-tests.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Unit testing guidelines

## Role

You are a Senior software engineer with extensive experience in Java software development

## Goal

Effective Java unit testing involves using JUnit 5 annotations and AssertJ for fluent assertions. Tests should follow the Given-When-Then structure with descriptive names for clarity. Each test must have a single responsibility, be independent, and leverage parameterized tests for data variations. Mocking dependencies with frameworks like Mockito is crucial for isolating the unit under test. While code coverage is a useful guide, the focus should be on meaningful tests for critical logic and edge cases. Test classes and methods should typically be package-private. Strategies for code splitting include small test methods and helper functions. Anti-patterns like testing implementation details, hard-coded values, and ignoring failures should be avoided. Proper state management involves isolated state and immutable objects, and error handling should include testing for expected exceptions and their messages.

### Implementing These Principles

These guidelines are built upon the following core principles:

1.  **Clarity and Readability**: Tests should be easy to understand. This is achieved through descriptive names (or `@DisplayName`), a clear Given-When-Then structure, and focused assertions. Readable tests serve as living documentation for the code under test.
2.  **Isolation and Independence**: Each test must be self-contained, not relying on the state or outcome of other tests. Dependencies should be mocked to ensure the unit under test is validated in isolation. This leads to reliable and stable test suites.
3.  **Comprehensive Validation**: Tests should thoroughly verify the behavior of the unit, including its responses to valid inputs, edge cases, boundary conditions, and error scenarios. This involves not just positive paths but also how the code handles failures and exceptions.
4.  **Modern Tooling and Practices**: Leverage modern testing frameworks (JUnit 5), fluent assertion libraries (AssertJ), and mocking tools (Mockito) to write expressive, maintainable, and powerful tests. Utilize features like parameterized tests to reduce boilerplate and improve coverage of data variations.
5.  **Maintainability and Focus**: Tests should be easy to maintain. This means avoiding tests that are too complex, test implementation details, or have multiple responsibilities. A well-written test makes it clear what is being tested and why, simplifying debugging and refactoring efforts.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **PRECONDITION**: The project MUST NOT use Spring Boot, Quarkus, or Micronaut — stop and direct the user to `@321-frameworks-spring-boot-testing-unit-tests` for Spring Boot or `@421-frameworks-quarkus-testing-unit-tests` for Quarkus unit testing
- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully and pass basic validation checks before any optimization
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE with any recommendations
- **BLOCKING CONDITION**: Compilation errors must be resolved by the user before proceeding with any object-oriented design improvements
- **NO EXCEPTIONS**: Under no circumstances should design recommendations be applied to a project that fails to compile

## Examples

### Table of contents

- Example 1: Use JUnit 5 Annotations
- Example 2: Use AssertJ for Assertions
- Example 3: Structure Tests with Given-When-Then
- Example 4: Use Descriptive Test Names
- Example 5: Aim for Single Responsibility in Tests
- Example 6: Ensure Tests are Independent
- Example 7: Use Parameterized Tests for Data Variations
- Example 8: Utilize Mocking for Dependencies (Mockito)
- Example 9: Consider Test Coverage, But Don't Obsess
- Example 10: Test Scopes
- Example 11: Code Splitting Strategies
- Example 12: Anti-patterns and Code Smells
- Example 13: State Management
- Example 14: Error Handling
- Example 15: Leverage JSpecify for Null Safety
- Example 16: Avoid Reflection in Unit Tests

### Example 1: Use JUnit 5 Annotations

Title: Prefer JUnit 5 annotations over JUnit 4
Description: Utilize annotations from the `org.junit.jupiter.api` package (e.g., `@Test`, `@BeforeEach`, `@AfterEach`, `@DisplayName`, `@Nested`, `@Disabled`) instead of their JUnit 4 counterparts (`@org.junit.Test`, `@Before`, `@After`, `@Ignore`). This ensures consistency and allows leveraging the full capabilities of JUnit 5.

**Good example:**

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("My Service Test")
class MyServiceTest {

    private MyService service;

    @BeforeEach
    void setUp() {
        service = new MyService(); // Setup executed before each test
    }

    @Test
    @DisplayName("should process data correctly")
    void processData() {
        // Given
        String input = "test";

        // When
        String result = service.process(input);

        // Then
        assertThat(result).isEqualTo("PROCESSED:test");
    }
}
```

**Bad example:**

```java
import org.junit.Before; // JUnit 4
import org.junit.Test;   // JUnit 4
import static org.junit.Assert.assertEquals; // JUnit 4 Assert

public class MyServiceTest {

    private MyService service;

    @Before // JUnit 4
    public void setup() {
        service = new MyService();
    }

    @Test // JUnit 4
    public void processData() {
        String input = "test";
        String result = service.process(input);
        assertEquals("PROCESSED:test", result); // JUnit 4 Assert
    }
}
```

### Example 2: Use AssertJ for Assertions

Title: Prefer AssertJ for assertions
Description: Employ AssertJ's fluent API (`org.assertj.core.api.Assertions.assertThat`) for more readable, expressive, and maintainable assertions compared to JUnit Jupiter's `Assertions` class or Hamcrest matchers.

**Good example:**

```java
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssertJExampleTest {

    @Test
    void checkValue() {
        String result = "hello";
        assertThat(result)
            .isEqualTo("hello")
            .startsWith("hell")
            .endsWith("o")
            .hasSize(5); // Chain multiple assertions fluently
    }

    @Test
    void checkException() {
        MyService service = new MyService();
        assertThatThrownBy(() -> service.divide(1, 0)) // Preferred way to test exceptions
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("zero");
    }
}
```

**Bad example:**

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JUnitAssertionsExampleTest {

    @Test
    void checkValue() {
        String result = "hello";
        assertEquals("hello", result); // Less fluent
        assertTrue(result.startsWith("hell")); // Separate assertions for each property
        assertTrue(result.endsWith("o"));
        assertEquals(5, result.length());
    }

    @Test
    void checkException() {
        MyService service = new MyService();
        // More verbose exception testing
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.divide(1, 0)
        );
        assertTrue(exception.getMessage().contains("zero")); // Separate assertion for message
    }
}
```

### Example 3: Structure Tests with Given-When-Then

Title: Structure test methods using the Given-When-Then pattern
Description: Organize the logic within test methods into three distinct, clearly separated phases: **Given** (setup preconditions), **When** (execute the code under test), and **Then** (verify the outcome). Use comments or empty lines to visually separate these phases, enhancing readability and understanding of the test's purpose.

**Good example:**

```java
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class GivenWhenThenTest {

    @Test
    void shouldCalculateSumCorrectly() {
        // Given
        Calculator calculator = new Calculator();
        int num1 = 5;
        int num2 = 10;
        int expectedSum = 15;

        // When
        int actualSum = calculator.add(num1, num2);

        // Then
        assertThat(actualSum).isEqualTo(expectedSum);
    }
}
```

**Bad example:**

```java
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UnstructuredTest {

    @Test
    void testAddition() {
        // Lack of clear separation makes it harder to follow the test flow
        Calculator calculator = new Calculator();
        assertThat(calculator.add(5, 10)).isEqualTo(15); // Combines action and verification
        // Setup might be mixed with action or verification elsewhere
    }
}
```

### Example 4: Use Descriptive Test Names

Title: Write descriptive test method names or use @DisplayName
Description: Test names should clearly communicate the scenario being tested and the expected outcome. Use either descriptive method names (e.g., following the `should_ExpectedBehavior_when_StateUnderTest` pattern) or JUnit 5's `@DisplayName` annotation for more natural language descriptions. This makes test reports easier to understand.

**Good example:**

```java
@Test
void should_throwException_when_divisorIsZero() {
    // Given
    Calculator calculator = new Calculator();

    // When & Then
    assertThatThrownBy(() -> calculator.divide(1, 0))
        .isInstanceOf(ArithmeticException.class);
}

@Test
@DisplayName("Should return the correct sum for positive numbers")
void additionWithPositives() {
     // Given
     Calculator calculator = new Calculator();
     int num1 = 5;
     int num2 = 10;

     // When
     int actualSum = calculator.add(num1, num2);

     // Then
     assertThat(actualSum).isEqualTo(15);
}
```

**Bad example:**

```java
@Test
void testDivide() { // Name is too generic, doesn't explain the scenario
    // ... test logic ...
}

@Test
void test1() { // Uninformative name
    // ... test logic ...
}
```

### Example 5: Aim for Single Responsibility in Tests

Title: Each test method should verify a single logical concept
Description: Avoid testing multiple unrelated things within a single test method. Each test should focus on one specific aspect of the unit's behavior or one particular scenario. This makes tests easier to understand, debug, and maintain. If a test fails, its specific focus makes pinpointing the cause simpler.

**Good example:**

```java
// Separate tests for different validation aspects
@Test
void should_reject_when_emailIsNull() {
    // ... test logic for null email ...
}

@Test
void should_reject_when_emailFormatIsInvalid() {
    // ... test logic for invalid email format ...
}
```

**Bad example:**

```java
@Test
void testUserValidation() { // Tests multiple conditions at once
    // Given user with null email
    // ... assertion for null email ...

    // Given user with invalid email format
    // ... assertion for invalid format ...

    // Given user with valid email
    // ... assertion for valid email ...
}
```

### Example 6: Ensure Tests are Independent

Title: Tests must be independent and runnable in any order
Description: Avoid creating tests that depend on the state left behind by previously executed tests. Each test should set up its own required preconditions (using `@BeforeEach` or within the test method itself) and should not rely on the execution order. This ensures test suite stability and reliability, preventing flickering tests.

**Good example:**

```java
class IndependentTests {
    private MyRepository repository = new InMemoryRepository(); // Or use @BeforeEach

    @Test
    void should_findItem_when_itemExists() {
        // Given
        Item item = new Item("testId", "TestData");
        repository.save(item); // Setup specific to this test

        // When
        Optional<Item> found = repository.findById("testId");

        // Then
        assertThat(found).isPresent();
    }

    @Test
    void should_returnEmpty_when_itemDoesNotExist() {
        // Given - Repository is clean (or re-initialized via @BeforeEach)

        // When
        Optional<Item> found = repository.findById("nonExistentId");

        // Then
        assertThat(found).isNotPresent();
    }
}
```

**Bad example:**

```java
class DependentTests {
    private static MyRepository repository = new InMemoryRepository(); // Shared state
    private static Item savedItem;

    @Test // Test 1 (might run first)
    void testSave() {
        savedItem = new Item("testId", "Data");
        repository.save(savedItem);
        // ... assertions ...
    }

    @Test // Test 2 (depends on Test 1 having run)
    void testFind() {
        // This test fails if testSave() hasn't run or if run order changes
        Optional<Item> found = repository.findById("testId");
        assertThat(found).isPresent();
    }
}
```

### Example 7: Use Parameterized Tests for Data Variations

Title: Use @ParameterizedTest for testing the same logic with different inputs
Description: When testing a method's behavior across various input values or boundary conditions, leverage JUnit 5's parameterized tests (`@ParameterizedTest` with sources like `@ValueSource`, `@CsvSource`, `@MethodSource`). This avoids code duplication and clearly separates the test logic from the test data.

**Good example:**

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.assertThat;

class ParameterizedCalculatorTest {

    private final Calculator calculator = new Calculator();

    @ParameterizedTest(name = "{index} {0} + {1} = {2}") // Clear naming for each case
    @CsvSource({
        "1,  2,  3",
        "0,  0,  0",
        "-5, 5,  0",
        "10, -3, 7"
    })
    void additionTest(int a, int b, int expectedResult) {
        // Given inputs a, b (from @CsvSource)

        // When
        int actualResult = calculator.add(a, b);

        // Then
        assertThat(actualResult).isEqualTo(expectedResult);
    }
}
```

**Bad example:**

```java
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class RepetitiveCalculatorTest {

    private final Calculator calculator = new Calculator();

    // Redundant tests for the same logic
    @Test
    void add1and2() {
        assertThat(calculator.add(1, 2)).isEqualTo(3);
    }

    @Test
    void add0and0() {
        assertThat(calculator.add(0, 0)).isEqualTo(0);
    }

    @Test
    void addNegative5and5() {
        assertThat(calculator.add(-5, 5)).isEqualTo(0);
    }

    @Test
    void add10andNegative3() {
        assertThat(calculator.add(10, -3)).isEqualTo(7);
    }
}
```

### Example 8: Utilize Mocking for Dependencies (Mockito)

Title: Isolate the unit under test using mocking frameworks like Mockito
Description: Unit tests should focus solely on the logic of the class being tested (System Under Test - SUT), not its dependencies (database, network services, other classes). Use mocking frameworks like Mockito to create mock objects that simulate the behavior of these dependencies. This ensures tests are fast, reliable, and truly test the unit in isolation.

**Good example:**

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should return user when found by id")
    void findUserById_Success() {
        // Given
        User expectedUser = new User("123", "John Doe");
        when(userRepository.findById("123")).thenReturn(Optional.of(expectedUser));

        // When
        Optional<User> actualUser = userService.findUserById("123");

        // Then
        assertThat(actualUser).isPresent().contains(expectedUser);
        verify(userRepository, times(1)).findById("123");
        verifyNoMoreInteractions(userRepository);
    }
}
```

**Bad example:**

```java
import org.junit.jupiter.api.Test;

class UserServiceTestBad {

    @Test
    void findUserById() {
        // Bad: Using real dependencies instead of mocks
        DatabaseConnection connection = new DatabaseConnection("localhost", 5432);
        UserRepository userRepository = new PostgresUserRepository(connection);
        UserService userService = new UserService(userRepository);

        // This test depends on database availability and state
        Optional<User> user = userService.findUserById("123");

        // Test is slow, brittle, and doesn't isolate the unit under test
        assertThat(user).isPresent();
    }
}
```

### Example 9: Consider Test Coverage, But Don't Obsess

Title: Use code coverage as a guide, not a definitive quality metric
Description: Tools like JaCoCo can measure which lines of code are executed by your tests (code coverage). Aiming for high coverage (e.g., >80% line/branch coverage) is generally good practice, as it indicates most code paths are tested. However, 100% coverage doesn't guarantee bug-free code or high-quality tests. Focus on writing meaningful tests for critical logic and edge cases rather than solely chasing coverage numbers. A test might cover a line but not actually verify its correctness effectively.

### Example 10: Test Scopes

Title: Package-private visibility for test classes and methods
Description: Test classes should have package-private visibility. There is no need for them to be public. Test methods should also have package-private visibility. There is no need for them to be public.

### Example 11: Code Splitting Strategies

Title: Organize test code effectively
Description: - **Small Test Methods:** Keep test methods small and focused on testing a single behavior. - **Helper Methods:** Use helper methods to avoid code duplication in test setup and assertions. - **Parameterized Tests:** Utilize JUnit's parameterized tests to test the same logic with different input values.

### Example 12: Anti-patterns and Code Smells

Title: Common testing mistakes to avoid
Description: - **Testing Implementation Details:** Avoid testing implementation details that might change, leading to brittle tests. Focus on testing behavior and outcomes. - **Hard-coded Values:** Avoid hard-coding values in tests. Use constants or test data to make tests more maintainable. - **Complex Test Logic:** Keep test logic simple and avoid complex calculations or conditional statements within tests. - **Ignoring Edge Cases:** Don't ignore edge cases or boundary conditions. Ensure tests cover a wide range of inputs, including invalid or unexpected values. - **Slow Tests:** Avoid slow tests that discourage developers from running them frequently. - **Over-reliance on Mocks:** Mock judiciously; too many mocks can obscure the actual behavior and make tests less reliable. - **Ignoring Test Failures:** Never ignore failing tests. Investigate and fix them promptly.

### Example 13: State Management

Title: Managing test state effectively
Description: - **Isolated State:** Ensure each test has its own isolated state to avoid interference between tests. Use `@BeforeEach` to reset the state before each test. - **Immutable Objects:** Prefer immutable objects to simplify state management and avoid unexpected side effects. - **Stateless Components:** Design stateless components whenever possible to reduce the need for state management in tests.

### Example 14: Error Handling

Title: Testing exception scenarios effectively
Description: - **Expected Exceptions:** Use AssertJ's `assertThatThrownBy` to verify that a method throws the expected exception under specific conditions. - **Exception Messages:** Assert the exception message to ensure the correct error is being thrown with helpful context. - **Graceful Degradation:** Test how the application handles errors and gracefully degrades when dependencies are unavailable.

### Example 15: Leverage JSpecify for Null Safety

Title: Utilize JSpecify annotations for explicit nullness contracts
Description: Employ JSpecify annotations (`org.jspecify.annotations.*`) such as `@NullMarked`, `@Nullable`, and `@NonNull` to clearly define the nullness expectations of method parameters, return types, and fields within your tests and the code under test. This practice enhances code clarity, enables static analysis tools to catch potential `NullPointerExceptions` early, and improves the overall robustness of your tests and application code.

**Good example:**

```java
@NullMarked
@ExtendWith(MockitoExtension.class)
class MyProcessorTest {

    @Mock
    private DataService mockDataService;

    private MyProcessor myProcessor;

    @Test
    void should_handleNullData_when_serviceReturnsNull() {
        // Given
        myProcessor = new MyProcessor(mockDataService);
        String key = "testKey";
        when(mockDataService.getData(key)).thenReturn(null);
        when(mockDataService.processData(null)).thenReturn("processed:null");

        // When
        String result = myProcessor.execute(key);

        // Then
        assertThat(result).isEqualTo("processed:null");
    }
}
```

**Bad example:**

```java
// No JSpecify annotations, nullness is ambiguous
class MyProcessorTest {
    @Test
    void testProcessing() {
        // Ambiguity: if getData returns null, this test might pass or fail unexpectedly
        when(mockDataService.getData(key)).thenReturn("someData");
        when(mockDataService.processData("SOMEDATA")).thenReturn("processed:SOMEDATA");

        String result = myProcessor.execute(key);

        assertThat(result).isEqualTo("processed:SOMEDATA");
    }
}
```

### Example 16: Avoid Reflection in Unit Tests

Title: Test through public API or extract logic to testable components instead of using reflection
Description: Avoid using reflection (`getDeclaredMethod`, `setAccessible`, `invoke`) to test private or package-private methods. Reflection creates brittle tests that tie to implementation details, break when methods are renamed or refactored, and bypass access modifiers that exist for a reason. Instead, either test through the class's public API (verify the observable behavior that depends on the internal logic) or extract the logic into a separate, testable component (e.g., a utility class or a collaborator) that can be tested directly.

**Good example:**

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

// Option A: Extract terminal status logic to a testable component
class AgentStatusHelperTest {

    @Test
    @DisplayName("Should identify terminal status COMPLETED")
    void should_identifyTerminalStatus_when_completed() {
        // Given
        AgentStatusHelper helper = new AgentStatusHelper();

        // When
        boolean result = helper.isTerminalStatus(Agent.StatusEnum.COMPLETED);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should identify terminal status FAILED")
    void should_identifyTerminalStatus_when_failed() {
        assertThat(new AgentStatusHelper().isTerminalStatus(Agent.StatusEnum.FAILED)).isTrue();
    }

    @Test
    @DisplayName("Should not identify RUNNING as terminal")
    void should_notIdentifyTerminalStatus_when_running() {
        assertThat(new AgentStatusHelper().isTerminalStatus(Agent.StatusEnum.RUNNING)).isFalse();
    }
}

// Option B: Test through public API - verify behavior that uses the logic
class CursorAgentTest {

    @Test
    @DisplayName("Should complete when status reaches COMPLETED")
    void should_complete_when_statusIsTerminal() {
        // Given - mock API returns COMPLETED status
        CursorAgent agent = new CursorAgent(mockApiReturningCompleted());

        // When
        Agent.Result result = agent.waitForCompletion("task-id");

        // Then - verify observable outcome, not internal isTerminalStatus
        assertThat(result.getStatus()).isEqualTo(Agent.StatusEnum.COMPLETED);
    }
}
```

**Bad example:**

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import static org.assertj.core.api.Assertions.assertThat;

class CursorAgentReflectionTest {

    @Test
    @DisplayName("Should identify terminal status COMPLETED")
    void should_identifyTerminalStatus_when_completed() throws Exception {
        // Given - Using reflection to bypass encapsulation
        CursorAgent agent = new CursorAgent(TEST_API_KEY, TEST_BASE_URL);
        Method method = CursorAgent.class.getDeclaredMethod("isTerminalStatus", Agent.StatusEnum.class);
        method.setAccessible(true);

        // When
        Boolean result = (Boolean) method.invoke(agent, Agent.StatusEnum.COMPLETED);

        // Then
        assertThat(result).isTrue();
    }
}
// Problems: Brittle (breaks on rename/refactor), tests implementation details,
// bypasses access modifiers, requires reflection boilerplate and exception handling
```


## Output Format

- **ANALYZE** Java test code to identify specific unit testing issues and categorize them by impact (CRITICAL, MAINTAINABILITY, PERFORMANCE, COVERAGE, RELIABILITY) and testing area (framework usage, assertion style, test structure, test independence, coverage gaps)
- **CATEGORIZE** unit testing improvements found: Framework Issues (JUnit 4 vs JUnit 5 modern features, outdated annotations vs current testing capabilities), Assertion Problems (basic JUnit assertions vs expressive AssertJ fluent assertions, unclear error messages vs descriptive failure descriptions), Test Structure Issues (poor naming vs descriptive test names, disorganized tests vs Given-When-Then structure, missing documentation vs clear test intent), Test Independence Problems (shared state issues vs isolated test execution, test order dependencies vs independent test methods), and Coverage Gaps (missing boundary conditions vs comprehensive edge case testing, untested error scenarios vs complete exception handling validation, insufficient parameterized testing vs thorough input validation)
- **APPLY** unit testing best practices directly by implementing the most appropriate improvements for each identified issue: Migrate to JUnit 5 with modern annotations and features, adopt AssertJ for expressive and readable assertions, implement Given-When-Then structure with descriptive test naming, ensure test independence through proper setup and teardown, eliminate shared state between tests, implement comprehensive boundary testing using RIGHT-BICEP principles, add parameterized tests for thorough input validation, and establish proper mocking strategies with Mockito for external dependencies
- **IMPLEMENT** comprehensive unit testing refactoring using proven patterns: Establish modern JUnit 5 test structure with @Test, @BeforeEach, @AfterEach, and lifecycle annotations, integrate AssertJ assertions for fluent and expressive test validation, apply Given-When-Then methodology with clear test organization and descriptive naming, implement test independence through proper resource management and state isolation, create comprehensive boundary testing covering RIGHT-BICEP scenarios (Right results, Inverse relationships, Cross-checks, Error conditions, Performance, Existence), and integrate parameterized testing for thorough input validation and edge case coverage
- **REFACTOR** test code systematically following the unit testing improvement roadmap: First migrate test framework to JUnit 5 with modern annotations and capabilities, then adopt AssertJ for expressive assertions and better error messages, restructure tests using Given-When-Then methodology with descriptive naming, ensure test independence by eliminating shared state and order dependencies, implement comprehensive boundary testing and edge case coverage, integrate parameterized testing for thorough validation, and establish proper mocking strategies for external dependencies and complex interactions
- **EXPLAIN** the applied unit testing improvements and their benefits: Test maintainability enhancements through JUnit 5 modern features and clear test structure, readability improvements via AssertJ expressive assertions and Given-When-Then organization, reliability gains from test independence and proper state management, coverage improvements through comprehensive boundary testing and parameterized validation, and debugging capabilities enhancement through descriptive test names and detailed assertion messages
- **VALIDATE** that all applied unit testing refactoring compiles successfully, maintains existing test functionality, improves test reliability and maintainability, achieves comprehensive test coverage, and follows established testing best practices through comprehensive verification and test execution


## Safeguards

- **BLOCKING SAFETY CHECK**: ALWAYS run `./mvnw compile` before ANY testing recommendations to ensure project stability
- **CRITICAL VALIDATION**: Execute `./mvnw clean verify` to ensure all existing tests pass before implementing new test strategies
- **MANDATORY VERIFICATION**: Confirm all existing functionality remains intact after applying any test improvements
- **ROLLBACK REQUIREMENT**: Ensure all test changes can be easily reverted if they introduce compilation or runtime issues
- **INCREMENTAL SAFETY**: Apply test improvements incrementally, validating compilation and test execution after each modification
- **DEPENDENCY VALIDATION**: Verify that any new testing dependencies (AssertJ, Mockito extensions) are properly configured and compatible
- **TEST ISOLATION VERIFICATION**: Ensure new tests don't introduce dependencies between test methods or classes
- **PERFORMANCE MONITORING**: Validate that test execution times remain reasonable and don't significantly impact build performance