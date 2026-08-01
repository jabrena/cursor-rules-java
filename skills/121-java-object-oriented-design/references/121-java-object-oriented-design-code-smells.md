---
name: 121-java-object-oriented-design-code-smells
description: Guidance for identifying and refactoring common object-oriented design code smells.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Object-Oriented Design Guidelines

## Role

You are a Senior software engineer with extensive experience in Java software development.

## Goal

Identify and refactor God Classes, Feature Envy, Inappropriate Intimacy, Refused Bequest, Shotgun Surgery, and Data Clumps.
Read this reference only after `121-java-object-oriented-design` has compiled the project and classified this concern as applicable.

## Constraints

Apply only guidance relevant to the diagnosed concern and preserve observable behavior.

- Read this reference only when the SKILL.md orchestration maps the request or code problem to this concern.
- Apply focused, incremental refactorings and preserve existing business behavior.
- Do not broaden the change into unrelated object-oriented design concerns.

## Examples

### Table of contents

- Example 1: Recognize and Address Common OOD Code Smells
- Example 2: Large Class / God Class
- Example 3: Feature Envy
- Example 4: Inappropriate Intimacy
- Example 5: Refused Bequest
- Example 6: Shotgun Surgery
- Example 7: Data Clumps

### Example 1: Recognize and Address Common OOD Code Smells

Title: Recognize and Address Common OOD Code Smells
Description: Code smells are symptoms of potential underlying problems in the design. Recognizing and refactoring them can significantly improve code quality.

### Example 2: Large Class / God Class

Title: A class that knows or does too much.
Description: Such classes violate SRP and are hard to understand, maintain, and test. Consider breaking them down into smaller, more focused classes.

### Example 3: Feature Envy

Title: A method that seems more interested in a class other than the one it actually is in.
Description: This often means the method is using data from another class more than its own. Consider moving the method to the class it's "envious" of, or introduce a new class to mediate.

**Good example:**

```java
class Customer {
    private String name;
    private Address address;
    public Customer(String name, Address address) { this.name = name; this.address = address; }
    public String getFullAddressDetails() { // Method operates on its own Address object
        return address.getStreet() + ", " + address.getCity() + ", " + address.getZipCode();
    }
}
class Address {
    private String street, city, zipCode;
    public Address(String s, String c, String z) { street=s; city=c; zipCode=z; }
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getZipCode() { return zipCode; }
}
```

**Bad example:**

```java
class Order {
    private double amount;
    private Customer customer; // Has a Customer
    public Order(double amount, Customer customer) { this.amount = amount; this.customer = customer; }

    // Bad: This method is more interested in Customer's Address than Order itself
    public String getCustomerShippingLabel() {
        Address addr = customer.getAddress(); // Assuming Customer has getAddress()
        return customer.getName() + "\n" + addr.getStreet() +
               "\n" + addr.getCity() + ", " + addr.getZipCode();
        // Better: Move this logic to Customer class as getShippingLabel() or similar.
    }
}
```

### Example 4: Inappropriate Intimacy

Title: Classes that spend too much time delving into each other's private parts.
Description: This indicates tight coupling and poor encapsulation. Classes should interact through well-defined public interfaces, not by accessing internal implementation details of others.

**Bad example:**

```java
class ServiceA {
    public int internalCounter = 0; // Public field, bad
    public void doSomething() { internalCounter++; }
}
class ServiceB {
    public void manipulateServiceA(ServiceA serviceA) {
        // Bad: Directly accessing and modifying internal state of ServiceA
        serviceA.internalCounter = 100;
        System.out.println("ServiceA counter directly set to: " + serviceA.internalCounter);
        // Better: ServiceA should have a method like resetCounter(int value) if this is valid behavior.
    }
}
```

### Example 5: Refused Bequest

Title: A subclass uses only some of the methods and properties inherited from its parents.
Description: This might indicate a violation of LSP or an incorrect inheritance hierarchy. The subclass might not truly be a substitutable type of the superclass.

### Example 6: Shotgun Surgery

Title: When a single conceptual change requires modifications in many different classes.
Description: This often indicates that a single responsibility has been spread too thinly across multiple classes, leading to high coupling and difficulty in making changes.

### Example 7: Data Clumps

Title: Bunches of data items that regularly appear together in multiple places.
Description: These data clumps often represent a missing concept that should be encapsulated into its own object or record.

**Good example:**

```java
// Good: Encapsulating related data into a Range object
record DateRange(LocalDate start, LocalDate end) {
    public DateRange {
        if (start.isAfter(end)) throw new IllegalArgumentException("Start date must be before end date.");
    }
}

class EventScheduler {
    public void scheduleEvent(String eventName, DateRange range) {
        System.out.println("Scheduling " + eventName + " from " + range.start() + " to " + range.end());
    }
    public boolean isDateInRange(LocalDate date, DateRange range) {
        return !date.isBefore(range.start()) && !date.isAfter(range.end());
    }
}
```

**Bad example:**

```java
// Bad: Data clump (startDay, startMonth, startYear, endDay, endMonth, endYear) passed around
class EventSchedulerBad {
    public void scheduleEvent(String eventName,
                              int startDay, int startMonth, int startYear,
                              int endDay, int endMonth, int endYear) {
        // ... logic using these separate date parts ...
        System.out.println("Scheduling event with many date parameters.");
    }
    public boolean checkOverlap(int sDay1, int sMon1, int sYr1, int eDay1, int eMon1, int eYr1,
                              int sDay2, int sMon2, int sYr2, int eDay2, int eMon2, int eYr2) {
        // ... complex logic with many parameters ...
        return false;
    }
    // This pattern of passing around many related date parts is a data clump.
}
```