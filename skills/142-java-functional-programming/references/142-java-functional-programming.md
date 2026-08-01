---
name: 142-java-functional-programming
description: Use when you need to apply functional programming principles in Java — including immutable objects and Records, pure functions, functional interfaces, lambda expressions, Stream API, Optional for null safety, function composition, higher-order functions, pattern matching, sealed classes/interfaces, and concurrent-safe functional patterns.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Functional Programming rules

## Role

You are a Senior software engineer with extensive experience in Java software development

## Goal

Java functional programming revolves around immutable objects and state transformations, ensuring functions are pure (no side effects, depend only on inputs). It leverages functional interfaces, concise lambda expressions, and the Stream API for collection processing. Core paradigms include function composition, `Optional` for null safety, and higher-order functions. Modern Java features like Records enhance immutable data transfer, while pattern matching (for `instanceof` and `switch`) and switch expressions improve conditional logic. Sealed classes and interfaces enable controlled, exhaustive hierarchies, and upcoming Stream Gatherers will offer advanced custom stream operations.

### Implementing These Principles

These guidelines are built upon the following core principles:

1.  **Immutability**: Prioritize immutable data structures (e.g., Records, `List.of()`) and state transformations that produce new instances rather than modifying existing ones. This reduces side effects and simplifies reasoning about state.
2.  **Purity and Side-Effect Management**: Strive to write pure functions—functions whose output depends only on their input and which have no observable side effects. Isolate and control side effects when they are necessary.
3.  **Expressiveness and Conciseness**: Leverage lambda expressions, method references, and the Stream API to write code that is declarative, concise, and clearly expresses the intent of data transformations and operations.
4.  **Higher-Order Abstractions**: Utilize functional interfaces, function composition, and higher-order functions (functions that operate on other functions) to build flexible and reusable code components.
5.  **Modern Java Integration**: Embrace modern Java features like Records, Pattern Matching, Switch Expressions, and Sealed Classes, which align well with and enhance functional programming paradigms by promoting immutability, type safety, and expressive conditional logic.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully and pass basic validation checks before any optimization
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE with any recommendations
- **BLOCKING CONDITION**: Compilation errors must be resolved by the user before proceeding with any object-oriented design improvements
- **NO EXCEPTIONS**: Under no circumstances should design recommendations be applied to a project that fails to compile

## Examples

### Table of contents

- Example 1: Immutable Objects
- Example 2: State Immutability
- Example 3: Pure Functions
- Example 4: Functional Interfaces
- Example 5: Lambda Expressions
- Example 6: Streams
- Example 7: Functional Programming Paradigms
- Example 8: Leverage Records for Immutable Data Transfer
- Example 9: Employ Pattern Matching for `instanceof` and `switch`
- Example 10: Use Switch Expressions for Concise Multi-way Conditionals
- Example 11: Leverage Sealed Classes and Interfaces for Controlled Hierarchies
- Example 12: Create Type-Safe Wrappers for Domain Types
- Example 13: Explore Stream Gatherers for Custom Stream Operations
- Example 14: Use Optional Idiomatically
- Example 15: Currying and Partial Application
- Example 16: Separate Effects from Pure Logic
- Example 17: Collectors Best Practices
- Example 18: Use Record Patterns in Switch
- Example 19: Compose Async Pipelines Functionally
- Example 20: Leverage Laziness and Infinite Streams
- Example 21: Functional Error Handling
- Example 22: Immutable Collections
- Example 23: Avoid Shared Mutable State in Concurrency

### Example 1: Immutable Objects

Title: Ensure Objects are Immutable
Description: Use `final` classes and fields. Initialize all fields in the constructor. Do not provide setter methods. Return defensive copies of mutable fields (e.g., collections, dates) when exposing them via getters.

**Good example:**

```java
import java.util.List;
import java.util.ArrayList;

public final class Person {
    private final String name;
    private final int age;
    private final List<String> hobbies; // Make it List, not ArrayList

    public Person(String name, int age, List<String> hobbies) {
        this.name = name;
        this.age = age;
        // Ensure the incoming list is defensively copied to an immutable list
        this.hobbies = List.copyOf(hobbies);
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    // Return an immutable view or a defensive copy
    public List<String> getHobbies() {
        return this.hobbies; // List.copyOf already returns an unmodifiable list
    }
}

```

### Example 2: State Immutability

Title: Prefer Immutable State Transformations
Description: Instead of modifying existing objects, return new objects representing the new state. Utilize collectors that produce immutable collections (e.g., `Collectors.toUnmodifiableList()`). Leverage immutable collection types provided by libraries or Java itself.

**Good example:**

```java
import java.util.List;
import java.util.stream.Collectors;

public class PriceCalculator {
    public static List<Double> applyDiscount(List<Double> prices, double discount) {
        return prices.stream()
            .map(price -> price * (1 - discount))
            .collect(Collectors.toUnmodifiableList()); // Ensures the returned list is immutable
    }
}

```

### Example 3: Pure Functions

Title: Write Pure Functions
Description: Functions should depend only on their input parameters and not on any external or hidden state. They should not cause any side effects (e.g., modifying external variables, I/O operations). Given the same input, a pure function must always return the same output. Avoid modifying external state or relying on it.

**Good example:**

```java
import java.util.List;
import java.util.stream.Collectors;

public class MathOperations {
    // Pure function: depends only on input, no side effects
    public static int add(int a, int b) {
        return a + b;
    }

    // Pure function: transforms input list to a new list without modifying the original
    public static List<Integer> doubleNumbers(List<Integer> numbers) {
        return numbers.stream()
            .map(n -> n * 2)
            .collect(Collectors.toList()); // Could also be toUnmodifiableList()
    }
}

```

### Example 4: Functional Interfaces

Title: Utilize Functional Interfaces Effectively
Description: Prefer built-in functional interfaces from `java.util.function` (e.g., `Function`, `Predicate`, `Consumer`, `Supplier`, `UnaryOperator`) when they suit the need. Create custom functional interfaces (annotated with `@FunctionalInterface`) for specific, clearly defined single abstract methods. Keep functional interfaces focused on a single responsibility.

**Good example:**

```java
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.time.LocalDateTime;

// Built-in functional interfaces
class FunctionalInterfaceExamples {
    Function<String, Integer> stringToLength = String::length;
    Predicate<Integer> isEven = n -> n % 2 == 0;
    Consumer<String> printer = System.out::println;
    Supplier<LocalDateTime> now = LocalDateTime::now;
}

// Custom functional interface
@FunctionalInterface
interface Validator<T> {
    boolean validate(T value);
}

```

### Example 5: Lambda Expressions

Title: Employ Lambda Expressions Clearly and Concisely
Description: Use method references (e.g., `String::length`, `System.out::println`) when they are clearer and more concise than an equivalent lambda expression. Keep lambda expressions short and focused on a single piece of logic to maintain readability. Extract complex or multi-line lambda logic into separate, well-named private methods.

**Good example:**

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LambdaExamples {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David", "Eve");

        // Method reference for conciseness
        names.forEach(System.out::println);

        // Simple, readable lambda
        List<String> longNames = names.stream()
            .filter(name -> name.length() > 4)
            .collect(Collectors.toList());

        // Complex logic extracted to a private helper method
        List<String> validNames = names.stream()
            .filter(LambdaExamples::isValidName)
            .collect(Collectors.toList());

        System.out.println("Long names: " + longNames);
        System.out.println("Valid names: " + validNames);
    }

    // Helper method for more complex lambda logic
    private static boolean isValidName(String name) {
        return name.length() > 3 && Character.isUpperCase(name.charAt(0));
    }
}

```

### Example 6: Streams

Title: Leverage Streams for Collection Processing
Description: Use the Stream API for processing sequences of elements from collections or other sources. Chain stream operations (intermediate operations like `filter`, `map`, `sorted`) to create a pipeline for complex transformations. Consider using parallel streams (`collection.parallelStream()`) for potentially improved performance on large datasets, but be mindful of the overhead and suitability for the task. Choose appropriate terminal operations (e.g., `collect`, `forEach`, `reduce`, `findFirst`, `anyMatch`) to produce a result or side-effect.

**Good example:**

```java
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamExamples {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Basic stream operations: filter even numbers and square them
        List<Integer> evenSquares = numbers.stream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * n)
            .collect(Collectors.toList());
        System.out.println("Even squares: " + evenSquares);

        // Advanced stream operations: partitioning numbers
        Map<Boolean, List<Integer>> partitionedByGreaterThanFive = numbers.stream()
            .collect(Collectors.partitioningBy(n -> n > 5));
        System.out.println("Partitioned by > 5: " + partitionedByGreaterThanFive);

        // Parallel stream for calculating average (use with caution, consider dataset size)
        double average = numbers.parallelStream()
            .mapToDouble(Integer::doubleValue)
            .average()
            .orElse(0.0);
        System.out.println("Average: " + average);
    }
}

```

### Example 7: Functional Programming Paradigms

Title: Apply Core Functional Programming Paradigms
Description: **Function Composition**: Combine simpler functions to create more complex ones. Use `Function.compose()` and `Function.andThen()`. **Optional for Null Safety**: Use `Optional<T>` to represent values that may be absent, avoiding `NullPointerExceptions` and clearly signaling optionality. **Recursion**: Implement algorithms using recursion where it naturally fits the problem (e.g., tree traversal), especially tail recursion if supported or optimized by the JVM. **Higher-Order Functions**: Utilize functions that accept other functions as arguments or return them as results (e.g., `Stream.map`, `Stream.filter`).

**Good example:**

```java
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.IntStream;

public class FunctionalParadigms {

    // Function composition
    public static void demonstrateComposition() {
        Function<Integer, String> intToString = Object::toString;
        Function<String, Integer> stringLength = String::length;
        // Executes intToString first, then stringLength
        Function<Integer, Integer> composedLengthAfterToString = stringLength.compose(intToString);
        System.out.println("Composed (123 -> length): " + composedLengthAfterToString.apply(123)); // Output: 3
    }

    // Optional usage for safe division
    public static Optional<Double> divideNumbers(Double numerator, Double denominator) {
        if (Objects.isNull(denominator) || denominator == 0) {
            return Optional.empty();
        }
        return Optional.of(numerator / denominator);
    }

    // Factorial using IntStream (more functional and often safer for large n)
    public static long factorialFunctional(int n) {
        if (n < 0) throw new IllegalArgumentException("Factorial not defined for negative numbers");
        return IntStream.rangeClosed(1, n)
                .asLongStream() // Ensure long for intermediate products
                .reduce(1L, (a, b) -> a * b);
    }

    // Recursion example: factorial (iterative version often preferred for stack safety in Java)
    // Note: Streams provide a more functional way for such operations in many cases.
    public static long factorialRecursive(int n) {
        if (n < 0) throw new IllegalArgumentException("Factorial not defined for negative numbers");
        if (n == 0 || n == 1) return 1;
        return n * factorialRecursive(n - 1);
    }

    // Higher-order function: memoization
    public static <T, R> Function<T, R> memoize(Function<T, R> function) {
        Map<T, R> cache = new ConcurrentHashMap<>();
        // The returned function closes over the cache
        return input -> cache.computeIfAbsent(input, function);
    }

    public static void main(String[] args) {
        demonstrateComposition();

        System.out.println("Divide 10 by 2: " + divideNumbers(10.0, 2.0).orElse(Double.NaN));
        System.out.println("Divide 10 by 0: " + divideNumbers(10.0, 0.0).orElse(Double.NaN));

        System.out.println("Factorial recursive (5): " + factorialRecursive(5));
        System.out.println("Factorial functional (5): " + factorialFunctional(5));

        Function<Integer, Integer> expensiveOperation = x -> {
            System.out.println("Computing for " + x);
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            return x * x;
        };

        Function<Integer, Integer> memoizedOp = memoize(expensiveOperation);
        System.out.println("Memoized (4): " + memoizedOp.apply(4)); // Computes
        System.out.println("Memoized (4): " + memoizedOp.apply(4)); // Returns from cache
        System.out.println("Memoized (5): " + memoizedOp.apply(5)); // Computes
    }
}

```

### Example 8: Leverage Records for Immutable Data Transfer

Title: Use Records for Type-Safe Immutable Data
Description: Use Records (JEP 395, standardized in Java 16) as the primary way to model simple, immutable data aggregates. Records automatically provide constructors, getters (accessor methods with the same name as the field), `equals()`, `hashCode()`, and `toString()` methods, reducing boilerplate. This aligns perfectly with the functional paradigm's preference for immutability and conciseness.

**Good example:**

```java
public record PointRecord(int x, int y) {
    // Optional: add custom compact constructors, static factory methods, or instance methods.
    // By default, all fields are final, and public accessor methods (e.g., x(), y()) are generated.
}

// Usage:
// PointRecord p = new PointRecord(10, 20);
// int xVal = p.x(); // Accessor method
// int yVal = p.y(); // Accessor method

```

**Bad example:**

```java
public final class PointClass {
    private final int x;
    private final int y;

    public PointClass(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (Objects.isNull(o) || getClass() != o.getClass()) return false;
        PointClass that = (PointClass) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "PointClass[" +
               "x=" + x + ", " +
               "y=" + y + ']';
    }
}

```

### Example 9: Employ Pattern Matching for `instanceof` and `switch`

Title: Use Pattern Matching for Type-Safe Conditional Logic
Description: Utilize Pattern Matching for `instanceof` to simplify type checks and casts in a single step. Employ Pattern Matching for `switch` for more expressive and robust conditional logic, especially with sealed types and records. This reduces boilerplate, improves readability, and enhances type safety.

**Good example:**

```java
public String processShapeWithPatternInstanceof(Object shape) {
    if (shape instanceof Circle c) { // Type test and binding in one
        return "Circle with radius " + c.getRadius();
    } else if (shape instanceof Rectangle r) {
        return "Rectangle with width " + r.getWidth() + " and height " + r.getHeight();
    }
    return "Unknown shape";
}

// Pattern Matching for switch with Records and Sealed Interfaces
sealed interface Shape permits CircleRecord, RectangleRecord, SquareRecord {}
record CircleRecord(double radius) implements Shape {}
record RectangleRecord(double length, double width) implements Shape {}
record SquareRecord(double side) implements Shape {}

public String processShapeWithPatternSwitch(Shape shape) {
    return switch (shape) {
        case CircleRecord c -> "Circle with radius " + c.radius();
        case RectangleRecord r -> "Rectangle with length " + r.length() + " and width " + r.width();
        case SquareRecord s -> "Square with side " + s.side();
        // No default needed if all permitted types of the sealed interface are covered
    };
}

```

**Bad example:**

```java
public String processShapeLegacy(Object shape) {
    if (shape instanceof Circle) {
        Circle c = (Circle) shape;
        return "Circle with radius " + c.getRadius();
    } else if (shape instanceof Rectangle) {
        Rectangle r = (Rectangle) shape;
        return "Rectangle with width " + r.getWidth() + " and height " + r.getHeight();
    }
    return "Unknown shape";
}

// Assume Circle and Rectangle classes exist for this example
// class Circle { public double getRadius() { return 0; } }
// class Rectangle { public double getWidth() { return 0; } public double getHeight() { return 0; } }

```

### Example 10: Use Switch Expressions for Concise Multi-way Conditionals

Title: Employ Switch Expressions for Safer Conditional Logic
Description: Prefer Switch Expressions (JEP 361, Java 14) over traditional switch statements for assigning the result of a multi-way conditional to a variable. Switch expressions are more concise, less error-prone (e.g., no fall-through by default, compiler checks for exhaustiveness with enums/sealed types). They fit well with functional programming's emphasis on expressions over statements.

**Good example:**

```java
public String getDayTypeWithSwitchExpr(String day) {
    return switch (day) {
        case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> "Weekday";
        case "SATURDAY", "SUNDAY" -> "Weekend";
        default -> throw new IllegalArgumentException("Invalid day: " + day);
    };
}

// Example with enum for exhaustive switch
enum Day { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }

public String getDayCategory(Day day) {
    return switch (day) {
        case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Weekday";
        case SATURDAY, SUNDAY -> "Weekend";
        // No default needed if all enum constants are covered
    };
}

```

**Bad example:**

```java
public String getDayTypeLegacy(String day) {
    String type;
    switch (day) {
        case "MONDAY":
        case "TUESDAY":
        case "WEDNESDAY":
        case "THURSDAY":
        case "FRIDAY":
            type = "Weekday";
            break;
        case "SATURDAY":
        case "SUNDAY":
            type = "Weekend";
            break;
        default:
            throw new IllegalArgumentException("Invalid day: " + day);
    }
    return type;
}

```

### Example 11: Leverage Sealed Classes and Interfaces for Controlled Hierarchies

Title: Use Sealed Types for Domain Modeling
Description: Use Sealed Classes and Interfaces (JEP 409, Java 17) to define class/interface hierarchies where all direct subtypes are known, finite, and explicitly listed. This enables more robust domain modeling and allows the compiler to perform exhaustive checks in pattern matching (e.g., with `switch` expressions), eliminating the need for a default case in many scenarios. Particularly useful for creating sum types (algebraic data types) which are common in functional programming.

**Good example:**

```java
// Define a sealed interface for different types of events
public sealed interface Event permits LoginEvent, LogoutEvent, FileUploadEvent {
    long getTimestamp();
}

// Define permitted implementations (often records for immutability)
public record LoginEvent(String userId, long timestamp) implements Event {
    @Override public long getTimestamp() { return timestamp; }
}

public record LogoutEvent(String userId, long timestamp) implements Event {
    @Override public long getTimestamp() { return timestamp; }
}

public record FileUploadEvent(String userId, String fileName, long fileSize, long timestamp) implements Event {
    @Override public long getTimestamp() { return timestamp; }
}

// A function processing the sealed hierarchy can be made exhaustive
public class EventProcessor {
    public String processEvent(Event event) {
        return switch (event) {
            case LoginEvent le -> "User " + le.userId() + " logged in at " + le.getTimestamp();
            case LogoutEvent loe -> "User " + loe.userId() + " logged out at " + loe.getTimestamp();
            case FileUploadEvent fue -> "User " + fue.userId() + " uploaded " + fue.fileName() + " at " + fue.getTimestamp();
            // No default case is necessary if the switch is exhaustive for all permitted types of Event.
        };
    }
}

```

### Example 12: Create Type-Safe Wrappers for Domain Types

Title: Use Strong Types for Domain Modeling
Description: Create type-safe wrappers for domain-specific types instead of using primitive types or general-purpose types like String. These wrapper types enhance type safety by enforcing invariants at compile-time and clearly communicate the intended meaning and constraints of data. This approach from type design thinking improves the functional programming paradigm by making invalid states unrepresentable.

**Good example:**

```java
// Type-safe wrappers for functional programming domains
public record UserId(String value) {
    public UserId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId cannot be null or empty");
        }
    }
}

public record EmailAddress(String value) {
    public EmailAddress {
        if (value == null || !isValidEmail(value)) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
    }

    private static boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
}

// Usage in functional context
public class UserService {
    public Optional<User> findUser(UserId userId) {
        // Type safety ensures only valid UserIds are passed
        return userRepository.findById(userId.value());
    }

    public List<User> findUsersByEmail(EmailAddress email) {
        // Type safety ensures only valid emails are processed
        return userRepository.findByEmail(email.value());
    }
}

```

**Bad example:**

```java
// Primitive obsession - error prone
public class UserService {
    public Optional<User> findUser(String userId) {
        // No validation, could be null or empty
        return userRepository.findById(userId);
    }

    public List<User> findUsersByEmail(String email) {
        // No validation, could be invalid email format
        return userRepository.findByEmail(email);
    }

    // Easy to make mistakes:
    // findUser(null); // Runtime error
    // findUsersByEmail("invalid-email"); // Invalid data propagated
    // findUser("user@example.com"); // Wrong parameter type confusion
}

```

### Example 13: Explore Stream Gatherers for Custom Stream Operations

Title: Use Stream Gatherers for Advanced Stream Processing
Description: For complex or highly custom stream processing tasks that are not easily achieved with standard terminal operations or collectors, investigate Stream Gatherers (JEP 461). Gatherers (`java.util.stream.Gatherer`) allow defining custom intermediate operations, offering more flexibility and power for sophisticated data transformations within functional pipelines. This feature is aimed at more advanced use cases where reusability and composition of stream operations are key.

**Good example:**

```java
import java.util.List;
import java.util.stream.Stream;
// import java.util.stream.Gatherers; // Assuming this is where predefined gatherers might reside

public class StreamGathererExample {

    // Hypothetical: A custom gatherer that creates sliding windows of elements.
    // The actual implementation of such a gatherer would be more involved.
    // static <T> Gatherer<T, ?, List<T>> windowed(int size) {
    //     // ... implementation details ...
    //     return null; // Placeholder
    // }

    public static void main(String[] args) {
        // List<List<Integer>> windows = Stream.of(1, 2, 3, 4, 5, 6, 7)
        //        .gather(windowed(3)) // Using a hypothetical custom 'windowed' gatherer
        //        .toList();
        //
        // // Expected output might be: [[1, 2, 3], [2, 3, 4], [3, 4, 5], [4, 5, 6], [5, 6, 7]]
        // System.out.println(windows);

        System.out.println("Stream Gatherers are a new feature. Refer to official Java documentation for concrete examples and API details as they become available.");
    }
}

// Rule of Thumb:
// Before implementing very complex custom collectors or resorting to imperative loops for intricate stream transformations,
// evaluate if a Stream Gatherer could offer a more declarative, reusable, and composable solution.
// This is for advanced stream users looking to build sophisticated data processing pipelines.

```

### Example 14: Use Optional Idiomatically

Title: Prefer map/flatMap/filter over isPresent/get
Description: Prefer `map`, `flatMap`, `filter`, `orElseGet`, and `orElseThrow` to express transformations and defaults declaratively. Avoid `isPresent()` + `get()` imperative patterns. Use `Optional` for return types to signal absence; avoid using it for fields and method parameters in most cases.

**Good example:**

```java
import java.util.Optional;

public class OptionalExamples {
    record User(String id, String email) {}

    public static Optional<String> normalizedEmail(Optional<User> maybeUser) {
        return maybeUser
                .map(User::email)
                .filter(email -> !email.isBlank())
                .map(String::trim)
                .map(String::toLowerCase);
    }

    public static String emailOrThrow(Optional<User> maybeUser) {
        return maybeUser
                .map(User::email)
                .filter(e -> !e.isBlank())
                .orElseThrow(() -> new IllegalStateException("Missing email"));
    }

    public static String emailOrDefault(Optional<User> maybeUser) {
        return maybeUser
                .map(User::email)
                .filter(e -> !e.isBlank())
                .orElseGet(() -> "unknown@example.com");
    }
}

```

### Example 15: Currying and Partial Application

Title: Build specialized functions from generic ones
Description: Use higher-order helpers to transform `BiFunction` into chains of `Function` (currying) or to bind the first argument (partial application). This enables composition and reuse without mutable state.

**Good example:**

```java
import java.util.function.BiFunction;
import java.util.function.Function;

public class CurryingExamples {
    public static <A, B, R> Function<A, Function<B, R>> curry(BiFunction<A, B, R> bi) {
        return a -> b -> bi.apply(a, b);
    }

    public static <A, B, R> Function<B, R> partial(BiFunction<A, B, R> bi, A a) {
        return b -> bi.apply(a, b);
    }

    public static void main(String[] args) {
        BiFunction<Integer, Integer, Integer> add = Integer::sum;

        Function<Integer, Function<Integer, Integer>> curriedAdd = curry(add);
        int six = curriedAdd.apply(1).apply(5);

        Function<Integer, Integer> addTen = partial(add, 10);
        int thirteen = addTen.apply(3);

        System.out.println(six + ", " + thirteen);
    }
}

```

### Example 16: Separate Effects from Pure Logic

Title: Model I/O as Suppliers and keep cores pure
Description: Isolate side effects at the edges. Pass `Supplier`/`Function` for effectful operations and keep transformation pipelines pure. This improves testability and reasoning.

**Good example:**

```java
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EffectBoundaries {
    record Order(String id, double amount) {}

    // Pure transformation
    public static List<String> highValueOrderIds(List<Order> orders, double min) {
        return orders.stream()
                .filter(o -> o.amount() >= min)
                .map(Order::id)
                .collect(Collectors.toUnmodifiableList());
    }

    // Effect boundary
    public static List<String> fetchAndFilter(Supplier<List<Order>> fetchOrders, double min) {
        List<Order> orders = fetchOrders.get(); // side-effect here only
        return highValueOrderIds(orders, min);  // pure
    }
}

```

### Example 17: Collectors Best Practices

Title: Use downstream, merging, and unmodifiable collectors
Description: Prefer `toUnmodifiable*` or `collectingAndThen(..., List::copyOf)` for immutability. With `toMap`, always provide a merge function when keys may collide. Use downstream collectors like `mapping`, `flatMapping`, `filtering`, and `teeing` to express logic declaratively.

**Good example:**

```java
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectorPatterns {
    public static Map<String, Long> wordFrequencies(List<String> words) {
        return words.stream()
                .collect(Collectors.toMap(
                        w -> w,
                        w -> 1L,
                        Long::sum,
                        LinkedHashMap::new)); // stable order
    }

    public static Map<Character, List<String>> groupedByInitial(List<String> names) {
        return names.stream()
                .collect(Collectors.groupingBy(
                        s -> s.charAt(0),
                        Collectors.collectingAndThen(
                                Collectors.mapping(String::toUpperCase, Collectors.toList()),
                                List::copyOf)));
    }

    public static double averageOfDistinct(List<Integer> numbers) {
        return numbers.stream()
                .distinct()
                .collect(Collectors.teeing(
                        Collectors.summingDouble(Integer::doubleValue),
                        Collectors.counting(),
                        (sum, count) -> count == 0 ? 0.0 : sum / count));
    }
}

```

### Example 18: Use Record Patterns in Switch

Title: Deconstruct records directly in pattern switches
Description: Record patterns (Java 21) allow deconstruction directly in `switch`, improving expressiveness and type safety. Combine with sealed hierarchies for exhaustiveness.

**Good example:**

```java
sealed interface Shape2 permits Circle2, Rect2 {}
record Circle2(double radius) implements Shape2 {}
record Rect2(double width, double height) implements Shape2 {}

public class RecordPatternDemo {
    public static double perimeter(Shape2 s) {
        return switch (s) {
            case Circle2(double r) -> 2 * Math.PI * r;
            case Rect2(double w, double h) -> 2 * (w + h);
        };
    }
}

```

### Example 19: Compose Async Pipelines Functionally

Title: Prefer thenApply/thenCompose and allOf
Description: Use `CompletableFuture` combinators to build non-blocking, declarative pipelines. Avoid premature `join()`/`get()` in the middle of the flow; keep blocking at the outer boundary if needed.

**Good example:**

```java
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AsyncComposition {
    static final ExecutorService pool = Executors.newFixedThreadPool(4);

    public static CompletableFuture<Integer> fetchPrice(String sku) {
        return CompletableFuture.supplyAsync(() -> sku.length() * 10, pool);
    }

    public static CompletableFuture<Double> applyDiscount(CompletableFuture<Integer> price, double discount) {
        return price.thenApply(p -> p * (1 - discount));
    }

    public static CompletableFuture<List<Double>> pricesForSkus(List<String> skus, double discount) {
        List<CompletableFuture<Double>> futures = skus.stream()
                .map(AsyncComposition::fetchPrice)
                .map(p -> applyDiscount(p, discount))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }
}

```

### Example 20: Leverage Laziness and Infinite Streams

Title: Generate values on-demand with iterate/generate
Description: Streams are lazy; nothing runs until a terminal operation. Model infinite sequences with `iterate`/`generate` and bound them with `limit`/`takeWhile`. Keep operations stateless and side-effect free.

**Good example:**

```java
import java.util.stream.Stream;

public class LazyStreams {
    public static Stream<Long> fibonacci() {
        return Stream.iterate(new long[]{0, 1}, p -> new long[]{p[1], p[0] + p[1]})
                .map(p -> p[0]);
    }

    public static void main(String[] args) {
        System.out.println(fibonacci().limit(10).toList());
        System.out.println(Stream.generate(Math::random).limit(3).toList());
    }
}

```

### Example 21: Functional Error Handling

Title: Use Either for Value or Error
Description: Model computations that may fail using an Either type, where Left represents error and Right represents success. This allows functional composition without throwing exceptions, improving flow and testability.

**Good example:**

```java
sealed interface Either<L, R> permits Left, Right {}
record Left<L, R>(L value) implements Either<L, R> {}
record Right<L, R>(R value) implements Either<L, R> {}

public class ErrorHandling {
    public static Either<String, Integer> safeDivide(int a, int b) {
        return (b == 0) ? new Left<>("Division by zero") : new Right<>(a / b);
    }

    public static Either<String, Integer> divideAndAdd(int a, int b, int add) {
        return safeDivide(a, b).flatMap(res -> safeDivide(res, add));
    }

    // Extension method for flatMap (would be in a utility class)
    public static <L, R, T> Either<L, T> flatMap(Either<L, R> either, Function<R, Either<L, T>> mapper) {
        return switch (either) {
            case Left<L, R> left -> new Left<>(left.value());
            case Right<L, R> right -> mapper.apply(right.value());
        };
    }
}

```

### Example 22: Immutable Collections

Title: Use Factory Methods and Unmodifiable Wrappers
Description: Create immutable collections using factory methods like List.of() or Collectors.toUnmodifiableList(). For existing collections, use Collections.unmodifiableList() to prevent modifications.

**Good example:**

```java
import java.util.List;
import java.util.Collections;

public class ImmutableCollections {
    public static List<String> getImmutableList() {
        return List.of("apple", "banana", "cherry"); // Immutable by design
    }

    public static List<String> makeImmutable(List<String> mutable) {
        return Collections.unmodifiableList(mutable); // Wrapper prevents changes
    }
}

```

### Example 23: Avoid Shared Mutable State in Concurrency

Title: Use Immutable Data and Pure Functions
Description: In concurrent code, avoid shared mutable state by using immutable objects and pure functions. Prefer concurrent collections only when necessary, and favor functional transformations.

**Good example:**

```java
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ConcurrentFunctional {
    private final List<String> sharedData = new CopyOnWriteArrayList<>(); // Thread-safe but mutable

    public List<String> processConcurrently() {
        // Prefer functional transformation without mutating shared state
        return sharedData.parallelStream()
                .map(String::toUpperCase)
                .collect(Collectors.toUnmodifiableList()); // Returns new immutable list
    }
}

```


## Output Format

- **ANALYZE** Java code to identify specific functional programming opportunities and categorize them by impact (CRITICAL, MAINTAINABILITY, PERFORMANCE, EXPRESSIVENESS) and area (immutability violations, side effects, imperative patterns, non-functional constructs, type safety gaps)
- **CATEGORIZE** functional programming improvements found: Immutability Issues (mutable objects vs immutable Records/classes, state mutation vs functional transformation, defensive copying vs inherent immutability), Purity Problems (side effects in functions vs pure functions, external state dependencies vs self-contained operations, non-deterministic behavior vs predictable functional logic), Imperative Code Patterns (traditional loops vs Stream API, null checks vs Optional chaining, imperative exception handling vs functional error handling), and Type Safety Opportunities (primitive obsession vs domain-specific functional types, unsafe casting vs pattern matching, weak type boundaries vs strong functional type systems)
- **APPLY** functional programming best practices directly by implementing the most appropriate improvements for each identified opportunity: Convert mutable objects to immutable Records or classes, extract pure functions from methods with side effects, replace imperative loops with Stream API operations, adopt Optional for null-safe functional programming, implement functional composition patterns, establish immutable data structures throughout the codebase, apply higher-order functions for reusable logic, and integrate pattern matching for type-safe functional operations
- **IMPLEMENT** comprehensive functional programming refactoring using proven patterns: Establish immutability through Records and immutable data structures, extract pure functions from methods containing side effects, replace imperative loops with declarative Stream API operations, integrate Optional for monadic null handling and chaining, implement function composition for modular logic design, apply higher-order functions for abstraction and reusability, and establish consistent functional programming idioms throughout the codebase
- **REFACTOR** code systematically following the functional programming improvement roadmap: First establish immutability by converting mutable objects to Records and immutable data structures, then extract pure functions from methods containing side effects, replace imperative loops with declarative Stream API operations, integrate Optional for null-safe functional programming patterns, implement functional composition for modular logic design, apply higher-order functions for abstraction and reusability, and establish consistent functional programming idioms throughout the codebase
- **EXPLAIN** the applied functional programming improvements and their benefits: Code expressiveness enhancements through declarative Stream API and functional composition, maintainability improvements via immutability and pure functions, concurrency safety gains from immutable data structures and side-effect-free operations, reasoning simplification through predictable functional logic, and overall code quality improvements through functional programming principles and patterns
- **VALIDATE** that all applied functional programming refactoring compiles successfully, maintains behavioral equivalence, preserves business logic correctness, achieves expected expressiveness benefits, and follows functional programming best practices through comprehensive testing and verification
- **STANDARDIZE** idiomatic `Optional` usage: prefer map/flatMap/filter/orElse*; avoid `isPresent()` + `get()` patterns; encode absence explicitly in return types
- **HARDEN** Stream collectors: specify merge functions for `toMap`, use downstream collectors (`mapping`, `flatMapping`, `filtering`, `teeing`), and return unmodifiable results
- **VERIFY LANGUAGE LEVEL** for used features (records, sealed types, switch/record patterns, gatherers) and provide alternatives if the project's Java version is lower


## Safeguards

- **BLOCKING SAFETY CHECK**: ALWAYS run `./mvnw compile` or `mvn compile` before ANY functional programming refactoring recommendations - compilation failure is a HARD STOP
- **CRITICAL VALIDATION**: Execute `./mvnw clean verify` or `mvn clean verify` to ensure all tests pass after applying functional programming patterns
- **MANDATORY VERIFICATION**: Confirm all existing functionality remains intact after functional refactoring, especially behavioral equivalence of pure function extractions and immutable transformations
- **SAFETY PROTOCOL**: If ANY compilation error occurs during functional programming transformation, IMMEDIATELY cease recommendations and require user intervention
- **PERFORMANCE VALIDATION**: Ensure functional programming patterns don't introduce performance regressions, especially with stream operations, immutable object creation, and recursive function calls
- **PURITY VERIFICATION**: Validate that extracted pure functions truly have no side effects and that immutable transformations don't inadvertently modify original state
- **ROLLBACK REQUIREMENT**: Ensure all functional programming refactoring changes can be easily reverted if they introduce complexity or performance issues
- **INCREMENTAL SAFETY**: Apply functional programming patterns incrementally, validating compilation and tests after each significant transformation step
- **DEPENDENCY VALIDATION**: Check that functional programming patterns are compatible with existing frameworks and don't break dependency injection or serialization requirements
- **FINAL VERIFICATION**: After completing all functional programming improvements, perform a final full project compilation, test run, and verification that functional invariants are maintained
- **LANGUAGE LEVEL CHECK**: Ensure the project's `maven-compiler-plugin` source/target support the used language features; if not, avoid those features or guide an upgrade
- **PARALLEL STREAM SAFETY**: For parallel operations, guarantee stateless lambdas and avoid shared mutable state or non-thread-safe collectors