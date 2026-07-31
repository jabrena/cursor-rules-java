---
name: 121-java-object-oriented-design-classes-interfaces
description: Class and interface design guidance covering accessibility, mutability, composition, and inheritance.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Object-Oriented Design Guidelines

## Role

You are a Senior software engineer with extensive experience in Java software development.

## Goal

Improve class and interface boundaries when the skill orchestration identifies encapsulation, mutability, composition, or inheritance design problems.
Read this reference only after `121-java-object-oriented-design` has compiled the project and classified this concern as applicable.

## Constraints

Apply only guidance relevant to the diagnosed concern and preserve observable behavior.

- Read this reference only when the SKILL.md orchestration maps the request or code problem to this concern.
- Apply focused, incremental refactorings and preserve existing business behavior.
- Do not broaden the change into unrelated object-oriented design concerns.

## Examples

### Table of contents

- Example 1: Classes and Interfaces Best Practices
- Example 2: Minimize the Accessibility of Classes and Members
- Example 3: In Public Classes, Use Accessor Methods, Not Public Fields
- Example 4: Minimize Mutability
- Example 5: Favor Composition Over Inheritance
- Example 6: Design and Document for Inheritance or Else Prohibit It

### Example 1: Classes and Interfaces Best Practices

Title: Design Classes and Interfaces for Maximum Effectiveness
Description: Well-designed classes and interfaces are the foundation of maintainable and robust Java applications. These practices ensure proper encapsulation, inheritance, and interface design.

### Example 2: Minimize the Accessibility of Classes and Members

Title: Use the most restrictive access level that makes sense
Description: Proper encapsulation hides implementation details and allows for easier maintenance and evolution of code.

**Good example:**

```java
public class BankAccount {
    private final String accountNumber;  // Private - implementation detail
    private double balance;              // Private - internal state

    // Package-private for testing
    static final double MINIMUM_BALANCE = 0.0;

    public BankAccount(String accountNumber, double initialBalance) {  // Public - part of API
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public double getBalance() {  // Public - part of API
        return balance;
    }

    public void deposit(double amount) {  // Public - part of API
        validateAmount(amount);
        balance += amount;
    }

    private void validateAmount(double amount) {  // Private - implementation detail
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
```

**Bad example:**

```java
public class BankAccount {
    public String accountNumber;  // Should be private
    public double balance;        // Should be private
    public static final double MINIMUM_BALANCE = 0.0;  // Unnecessarily public

    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public void validateAmount(double amount) {  // Should be private
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
```

### Example 3: In Public Classes, Use Accessor Methods, Not Public Fields

Title: Provide getter and setter methods instead of exposing fields directly
Description: Accessor methods provide flexibility to add validation, logging, or other logic without breaking clients.

**Good example:**

```java
public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public void setX(double x) {
        // Can add validation or other logic
        if (Double.isNaN(x)) {
            throw new IllegalArgumentException("x cannot be NaN");
        }
        this.x = x;
    }

    public void setY(double y) {
        if (Double.isNaN(y)) {
            throw new IllegalArgumentException("y cannot be NaN");
        }
        this.y = y;
    }
}
```

**Bad example:**

```java
public class Point {
    public double x;  // Direct field access - no validation possible
    public double y;  // Cannot add logic later without breaking clients

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
```

### Example 4: Minimize Mutability

Title: Make classes immutable when possible
Description: Immutable classes are simpler, safer, and can be freely shared. They are inherently thread-safe and have no temporal coupling.

**Good example:**

```java
public final class Complex {
    private final double real;
    private final double imaginary;

    public Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public double realPart() { return real; }
    public double imaginaryPart() { return imaginary; }

    // Operations return new instances instead of modifying
    public Complex plus(Complex c) {
        return new Complex(real + c.real, imaginary + c.imaginary);
    }

    public Complex minus(Complex c) {
        return new Complex(real - c.real, imaginary - c.imaginary);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Complex)) return false;
        Complex c = (Complex) o;
        return Double.compare(c.real, real) == 0 &&
               Double.compare(c.imaginary, imaginary) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(real, imaginary);
    }
}
```

**Bad example:**

```java
public class Complex {
    private double real;      // Mutable fields
    private double imaginary; // Mutable fields

    public Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public double getRealPart() { return real; }
    public double getImaginaryPart() { return imaginary; }

    // Mutating operations - not thread-safe, harder to reason about
    public void plus(Complex c) {
        this.real += c.real;
        this.imaginary += c.imaginary;
    }

    public void setReal(double real) { this.real = real; }
    public void setImaginary(double imaginary) { this.imaginary = imaginary; }
}
```

### Example 5: Favor Composition Over Inheritance

Title: Use composition instead of inheritance when you want to reuse code
Description: Composition is more flexible than inheritance and avoids the fragility of inheritance hierarchies.

**Good example:**

```java
// Using composition
public class InstrumentedSet<E> {
    private final Set<E> s;
    private int addCount = 0;

    public InstrumentedSet(Set<E> s) {
        this.s = s;
    }

    public boolean add(E e) {
        addCount++;
        return s.add(e);
    }

    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return s.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }

    // Delegate other methods to the wrapped set
    public int size() { return s.size(); }
    public boolean isEmpty() { return s.isEmpty(); }
    public boolean contains(Object o) { return s.contains(o); }
    // ... other delegating methods
}
```

**Bad example:**

```java
// Using inheritance - fragile and error-prone
public class InstrumentedHashSet<E> extends HashSet<E> {
    private int addCount = 0;

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);  // This calls add() internally, double-counting!
    }

    public int getAddCount() {
        return addCount;
    }
}
```

### Example 6: Design and Document for Inheritance or Else Prohibit It

Title: Either design classes specifically for inheritance or make them final
Description: Classes not designed for inheritance can break when subclassed. Document self-use patterns or prohibit inheritance.

**Good example:**

```java
// Designed for inheritance with proper documentation
public abstract class AbstractProcessor {

    /**
     * Processes the given data. This implementation calls {@link #validate(String)}
     * followed by {@link #transform(String)}. Subclasses may override this method
     * to provide different processing logic.
     *
     * @param data the data to process
     * @return the processed result
     * @throws IllegalArgumentException if data is invalid
     */
    public String process(String data) {
        validate(data);
        return transform(data);
    }

    /**
     * Validates the input data. The default implementation checks for null.
     * Subclasses may override to provide additional validation.
     */
    protected void validate(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
    }

    /**
     * Transforms the validated data. Subclasses must implement this method.
     */
    protected abstract String transform(String data);
}

// Or prohibit inheritance
public final class UtilityClass {
    private UtilityClass() { /* prevent instantiation */ }

    public static String formatName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }
}
```

**Bad example:**

```java
// Not designed for inheritance but not prohibited
public class DataProcessor {
    public String process(String data) {
        // Complex logic that might break if overridden
        String validated = validate(data);
        String transformed = transform(validated);
        return finalize(transformed);
    }

    private String validate(String data) { /* ... */ return data; }
    private String transform(String data) { /* ... */ return data; }
    private String finalize(String data) { /* ... */ return data; }
}
```