---
name: 121-java-object-oriented-design-principles
description: SOLID, DRY, and YAGNI guidance for Java object-oriented design.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Object-Oriented Design Guidelines

## Role

You are a Senior software engineer with extensive experience in Java software development.

## Goal

Apply SOLID, DRY, and YAGNI principles when the skill orchestration identifies fundamental design problems.
Read this reference only after `121-java-object-oriented-design` has compiled the project and classified this concern as applicable.

## Constraints

Apply only guidance relevant to the diagnosed concern and preserve observable behavior.

- Read this reference only when the SKILL.md orchestration maps the request or code problem to this concern.
- Apply focused, incremental refactorings and preserve existing business behavior.
- Do not broaden the change into unrelated object-oriented design concerns.

## Examples

### Table of contents

- Example 1: Apply Fundamental Software Design Principles
- Example 2: Single Responsibility Principle (SRP)
- Example 3: Open/Closed Principle (OCP)
- Example 4: Liskov Substitution Principle (LSP)
- Example 5: Interface Segregation Principle (ISP)
- Example 6: Dependency Inversion Principle (DIP)
- Example 7: DRY (Don't Repeat Yourself)
- Example 8: YAGNI (You Ain't Gonna Need It)
- Example 9: Design Well-Structured and Maintainable Classes and Interfaces

### Example 1: Apply Fundamental Software Design Principles

Title: Apply Fundamental Software Design Principles
Description: Core principles like SOLID, DRY, and YAGNI are foundational to good object-oriented design, leading to more robust, maintainable, and understandable systems.

### Example 2: Single Responsibility Principle (SRP)

Title: A class should have one, and only one, reason to change.
Description: This means a class should only have one job or primary responsibility. If a class handles multiple responsibilities, changes to one responsibility might inadvertently affect others.

**Good example:**

```java
// Good: Separate responsibilities
class UserData {
    private String name;
    private String email;
    // constructor, getters
    public UserData(String name, String email) { this.name = name; this.email = email; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}

class UserPersistence {
    public void saveUser(UserData user) {
        System.out.println("Saving user " + user.getName() + " to database.");
        // Database saving logic
    }
}

class UserEmailer {
    public void sendWelcomeEmail(UserData user) {
        System.out.println("Sending welcome email to " + user.getEmail());
        // Email sending logic
    }
}
```

**Bad example:**

```java
// Bad: User class with multiple responsibilities
class User {
    private String name;
    private String email;

    public User(String name, String email) { this.name = name; this.email = email; }

    public String getName() { return name; }
    public String getEmail() { return email; }

    public void saveToDatabase() {
        System.out.println("Saving user " + name + " to database.");
        // Database logic mixed in
    }

    public void sendWelcomeEmail() {
        System.out.println("Sending welcome email to " + email);
        // Email logic mixed in
    }
    // If email sending changes, or DB logic changes, this class needs to change.
}
```

### Example 3: Open/Closed Principle (OCP)

Title: Software entities should be open for extension but closed for modification.
Description: You should be able to add new functionality without changing existing, tested code. This is often achieved using interfaces, abstract classes, and polymorphism.

**Good example:**

```java
interface Shape {
    double calculateArea();
}

class Rectangle implements Shape {
    private double width, height;
    public Rectangle(double w, double h) { width=w; height=h; }
    @Override public double calculateArea() { return width * height; }
}

class Circle implements Shape {
    private double radius;
    public Circle(double r) { radius=r; }
    @Override public double calculateArea() { return Math.PI * radius * radius; }
}

// New shapes (e.g., Triangle) can be added by implementing Shape
// without modifying existing Shape, Rectangle, Circle, or AreaCalculator.
class AreaCalculator {
    public double getTotalArea(List<Shape> shapes) {
        return shapes.stream().mapToDouble(Shape::calculateArea).sum();
    }
}
```

**Bad example:**

```java
// Bad: AreaCalculator needs modification for new shapes
class AreaCalculatorBad {
    public double calculateRectangleArea(Rectangle rect) { return rect.width * rect.height; }
    public double calculateCircleArea(Circle circ) { return Math.PI * circ.radius * circ.radius; }
    // If a Triangle class is added, this class must be modified to add calculateTriangleArea().
}
class Rectangle { public double width, height; /* ... */ }
class Circle { public double radius; /* ... */ }
```

### Example 4: Liskov Substitution Principle (LSP)

Title: Subtypes must be substitutable for their base types.
Description: Objects of a superclass should be replaceable with objects of its subclasses without affecting the correctness of the program or causing unexpected behavior.

**Good example:**

```java
interface Bird {
    void move();
}

class FlyingBird implements Bird {
    public void fly() { System.out.println("Flying high!"); }
    @Override public void move() { fly(); }
}

class Sparrow extends FlyingBird { /* Can fly */ }

class Ostrich implements Bird { // Ostrich is a Bird but doesn't fly in the typical sense
    public void runFast() { System.out.println("Running fast on the ground!"); }
    @Override public void move() { runFast(); }
}

public class BirdLSPExample {
    public static void makeBirdMove(Bird bird) {
        bird.move(); // Works correctly for Sparrow (flies) and Ostrich (runs)
    }
    public static void main(String args) {
        makeBirdMove(new Sparrow());
        makeBirdMove(new Ostrich());
    }
}
```

**Bad example:**

```java
// Bad: Violating LSP
class Bird {
    public void fly() { System.out.println("Bird is flying."); }
}

class Penguin extends Bird {
    @Override
    public void fly() {
        // Penguins can't fly, so this method might do nothing or throw an exception.
        // This violates LSP because a Penguin can't simply replace a generic Bird where fly() is expected.
        throw new UnsupportedOperationException("Penguins can't fly.");
    }
    public void swim() { System.out.println("Penguin is swimming."); }
}

public class BirdLSPViolation {
    public static void letTheBirdFly(Bird bird) {
        bird.fly(); // This will crash if bird is a Penguin
    }
    public static void main(String args) {
        try {
            letTheBirdFly(new Penguin());
        } catch (UnsupportedOperationException e) {
            System.err.println(e.getMessage());
        }
    }
}
```

### Example 5: Interface Segregation Principle (ISP)

Title: Clients should not be forced to depend on interfaces they do not use.
Description: It's better to have many small, specific interfaces (role interfaces) than one large, general-purpose interface. This prevents classes from having to implement methods they don't need.

**Good example:**

```java
// Good: Segregated interfaces
interface Worker {
    void work();
}

interface Eater {
    void eat();
}

class HumanWorker implements Worker, Eater {
    @Override public void work() { System.out.println("Human working."); }
    @Override public void eat() { System.out.println("Human eating."); }
}

class RobotWorker implements Worker {
    @Override public void work() { System.out.println("Robot working efficiently."); }
    // RobotWorker does not need to implement eat()
}
```

**Bad example:**

```java
// Bad: Fat interface
interface IWorkerAndEater {
    void work();
    void eat(); // All implementers must provide eat(), even if they don't eat.
}

class Human implements IWorkerAndEater {
    @Override public void work() { /* ... */ }
    @Override public void eat() { /* ... */ }
}

class Robot implements IWorkerAndEater {
    @Override public void work() { System.out.println("Robot working."); }
    @Override public void eat() {
        // Robots don't eat. This method is forced and likely empty or throws exception.
        throw new UnsupportedOperationException("Robots don't eat.");
    }
}
```

### Example 6: Dependency Inversion Principle (DIP)

Title: High-level modules should not depend on low-level modules. Both should depend on abstractions.
Description: Abstractions (e.g., interfaces) should not depend on details. Details (concrete implementations) should depend on abstractions. This promotes loose coupling.

**Good example:**

```java
// Abstraction
interface MessageSender {
    void sendMessage(String message);
}

// Low-level module (detail)
class EmailSender implements MessageSender {
    @Override public void sendMessage(String message) { System.out.println("Email sent: " + message); }
}

// Low-level module (detail)
class SMSSender implements MessageSender {
    @Override public void sendMessage(String message) { System.out.println("SMS sent: " + message); }
}

// High-level module
class NotificationService {
    private final MessageSender sender; // Depends on abstraction

    public NotificationService(MessageSender sender) { // Dependency injected
        this.sender = sender;
    }

    public void notify(String message) {
        sender.sendMessage(message);
    }
}

public class DIPExample {
    public static void main(String args) {
        NotificationService emailNotifier = new NotificationService(new EmailSender());
        emailNotifier.notify("Hello via Email!");

        NotificationService smsNotifier = new NotificationService(new SMSSender());
        smsNotifier.notify("Hello via SMS!");
    }
}
```

**Bad example:**

```java
// Bad: High-level module depends directly on low-level module
class EmailerBad {
    public void sendEmail(String message) { System.out.println("Email sent: " + message); }
}

class NotificationServiceBad {
    private EmailerBad emailer; // Direct dependency on concrete EmailerBad

    public NotificationServiceBad() {
        this.emailer = new EmailerBad(); // Instantiates concrete class
    }

    public void sendNotification(String message) {
        emailer.sendEmail(message); // Tightly coupled
    }
    // If we want to use SMSSender, NotificationServiceBad needs to be changed.
}
```

### Example 7: DRY (Don't Repeat Yourself)

Title: Avoid duplication of code.
Description: Every piece of knowledge or logic must have a single, unambiguous, authoritative representation within a system. Use methods, classes, inheritance, or composition to centralize and reuse code.

**Good example:**

```java
class CalculationUtils {
    // Centralized validation logic
    public static void validatePositive(double value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive.");
        }
    }
}

class RectangleArea {
    public double calculate(double width, double height) {
        CalculationUtils.validatePositive(width, "Width");
        CalculationUtils.validatePositive(height, "Height");
        return width * height;
    }
}

class CircleVolume {
    public double calculate(double radius, double height) {
        CalculationUtils.validatePositive(radius, "Radius");
        CalculationUtils.validatePositive(height, "Height");
        return Math.PI * radius * radius * height;
    }
}
```

**Bad example:**

```java
// Bad: Duplicated validation logic
class RectangleAreaBad {
    public double calculate(double width, double height) {
        if (width <= 0) throw new IllegalArgumentException("Width must be positive."); // Duplicated
        if (height <= 0) throw new IllegalArgumentException("Height must be positive."); // Duplicated
        return width * height;
    }
}

class CircleVolumeBad {
    public double calculate(double radius, double height) {
        if (radius <= 0) throw new IllegalArgumentException("Radius must be positive."); // Duplicated
        if (height <= 0) throw new IllegalArgumentException("Height must be positive."); // Duplicated
        return Math.PI * radius * radius * height;
    }
}
```

### Example 8: YAGNI (You Ain't Gonna Need It)

Title: Implement features only when you actually need them.
Description: Avoid implementing functionality based on speculation that it might be needed in the future. This helps prevent over-engineering and keeps the codebase simpler and more focused on current requirements.

**Good example:**

```java
// Good: Simple class meeting current needs
class ReportGenerator {
    public String generateSimpleReport(List<String> data) {
        System.out.println("Generating simple report.");
        return "Report: " + String.join(", ", data);
    }
    // If PDF export is needed later, it can be added then.
    // No need to implement generatePdfReport, generateExcelReport etc. upfront.
}
```

**Bad example:**

```java
// Bad: Over-engineered with features not currently needed
class ReportGeneratorOverkill {
    public String generateHtmlReport(List<String> data) { /* ... */ return "html";}
    public byte[] generatePdfReport(List<String> data) {
        System.out.println("Generating PDF report (not actually used yet).");
        return new byte[0];
    }
    public byte[] generateExcelReport(List<String> data) {
        System.out.println("Generating Excel report (not actually used yet).");
        return new byte[0];
    }
    // Current requirement is only for HTML, but PDF and Excel are added "just in case".
}
```

### Example 9: Design Well-Structured and Maintainable Classes and Interfaces

Title: Design Well-Structured and Maintainable Classes and Interfaces
Description: Good class and interface design is crucial for building flexible and understandable OOD systems. Favor composition over inheritance, program to interfaces rather than implementations, keep classes small and focused, and design for immutability where appropriate. Use clear, descriptive naming conventions.

**Good example:**

```java
// Interface (Abstraction)
interface Engine {
    void start();
    void stop();
}

// Concrete Implementations
class PetrolEngine implements Engine {
    @Override public void start() { System.out.println("Petrol engine started."); }
    @Override public void stop() { System.out.println("Petrol engine stopped."); }
}

class ElectricEngine implements Engine {
    @Override public void start() { System.out.println("Electric engine silently started."); }
    @Override public void stop() { System.out.println("Electric engine silently stopped."); }
}

// Class using Composition and Programming to an Interface
class Car {
    private final Engine engine; // Depends on Engine interface (abstraction)
    private final String modelName;

    // Engine is injected (composition)
    public Car(String modelName, Engine engine) {
        this.modelName = modelName;
        this.engine = engine;
    }

    public void startCar() {
        System.out.print(modelName + ": ");
        engine.start();
    }

    public void stopCar() {
        System.out.print(modelName + ": ");
        engine.stop();
    }

    public String getModelName(){ return modelName; }
}

public class ClassDesignExample {
    public static void main(String args) {
        Car petrolCar = new Car("SedanX", new PetrolEngine());
        Car electricCar = new Car("EVMax", new ElectricEngine());

        petrolCar.startCar();
        electricCar.startCar();
        petrolCar.stopCar();
        electricCar.stopCar();
    }
}
```

**Bad example:**

```java
// Bad: Tight coupling, not programming to an interface
class BadCar {
    private final BadPetrolEngine engine; // Direct dependency on concrete BadPetrolEngine
    public BadCar() {
        this.engine = new BadPetrolEngine(); // Instantiates concrete class
    }
    public void start() { engine.startPetrol(); }
    // If we want an electric car, this class needs significant changes or a new similar class.
}
class BadPetrolEngine { public void startPetrol() { System.out.println("Bad petrol engine starts."); } }
```