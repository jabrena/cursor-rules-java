---
name: 121-java-object-oriented-design-oop-concepts
description: Encapsulation, inheritance, and polymorphism guidance for Java object-oriented design.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Object-Oriented Design Guidelines

## Role

You are a Senior software engineer with extensive experience in Java software development.

## Goal

Apply encapsulation, inheritance, and polymorphism correctly when the skill orchestration identifies core OOP misuse.
Read this reference only after `121-java-object-oriented-design` has compiled the project and classified this concern as applicable.

## Constraints

Apply only guidance relevant to the diagnosed concern and preserve observable behavior.

- Read this reference only when the SKILL.md orchestration maps the request or code problem to this concern.
- Apply focused, incremental refactorings and preserve existing business behavior.
- Do not broaden the change into unrelated object-oriented design concerns.

## Examples

### Table of contents

- Example 1: Effectively Utilize Core Object-Oriented Concepts
- Example 2: Encapsulation
- Example 3: Inheritance
- Example 4: Polymorphism

### Example 1: Effectively Utilize Core Object-Oriented Concepts

Title: Effectively Utilize Core Object-Oriented Concepts
Description: Encapsulation, Inheritance, and Polymorphism are the three pillars of object-oriented programming.

### Example 2: Encapsulation

Title: Protect Internal State and Implementation Details
Description: Hide the internal state (fields) and implementation details of an object from the outside world. Expose a well-defined public interface (methods) for interacting with the object. Use access modifiers effectively to control visibility and protect invariants.

**Good example:**

```java
class BankAccount {
    private double balance; // Encapsulated: internal state is private
    private final String accountNumber;

    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        if (initialBalance < 0) throw new IllegalArgumentException("Initial balance cannot be negative.");
        this.balance = initialBalance;
    }

    // Public interface to interact with the balance
    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        this.balance += amount;
        System.out.println("Deposited: " + amount + ", New Balance: " + this.balance);
    }

    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (amount > this.balance) throw new IllegalArgumentException("Insufficient funds.");
        this.balance -= amount;
        System.out.println("Withdrew: " + amount + ", New Balance: " + this.balance);
    }

    public double getBalance() { // Controlled access to balance
        return this.balance;
    }
    public String getAccountNumber() { return this.accountNumber; }
}
```

**Bad example:**

```java
// Bad: Poor encapsulation, exposing internal state
class UnsafeBankAccount {
    public double balance; // Public field: internal state exposed and can be freely modified
    public String accountNumber;

    public UnsafeBankAccount(String accNum, double initial) { this.accountNumber = accNum; this.balance = initial; }
    // No methods to control how balance is changed, invariants can be broken.
}
public class BadEncapsulationExample {
    public static void main(String args) {
        UnsafeBankAccount account = new UnsafeBankAccount("123", 100.0);
        account.balance = -500.0; // Direct modification, potentially breaking business rules
        System.out.println("Unsafe balance: " + account.balance);
    }
}
```

### Example 3: Inheritance

Title: Model "is-a" Relationships and Ensure LSP
Description: Use inheritance to model true "is-a" relationships, where a subclass is a more specific type of its superclass. Ensure that the Liskov Substitution Principle (LSP) is followed: subclasses must be substitutable for their base types without altering the correctness of the program.

**Good example:**

```java
abstract class Animal {
    private String name;
    public Animal(String name) { this.name = name; }
    public String getName() { return name; }
    public abstract void makeSound(); // Abstract method for polymorphism
}

class Dog extends Animal { // Dog IS-A Animal
    public Dog(String name) { super(name); }
    @Override public void makeSound() { System.out.println(getName() + " says: Woof!"); }
    public void fetch() { System.out.println(getName() + " is fetching."); }
}

class Cat extends Animal { // Cat IS-A Animal
    public Cat(String name) { super(name); }
    @Override public void makeSound() { System.out.println(getName() + " says: Meow!"); }
    public void purr() { System.out.println(getName() + " is purring."); }
}

public class InheritanceExample {
    public static void main(String args) {
        Animal myDog = new Dog("Buddy");
        Animal myCat = new Cat("Whiskers");
        myDog.makeSound();
        myCat.makeSound();
        // ((Dog)myDog).fetch(); // Can cast if sure of type to access specific methods
    }
}
```

**Bad example:**

```java
// Bad: Incorrect "is-a" relationship using composition instead
class Window {
    public void open() { System.out.println("Window opened."); }
    public void close() { System.out.println("Window closed."); }
}

class BetterCarDoor {
    private WindowComponent window = new WindowComponent();
    public void openDoor() { System.out.println("Car door opened."); }
    public void closeDoor() { System.out.println("Car door closed."); }
    public void openWindow() { window.open(); }
    public void closeWindow() { window.close(); }
    static class WindowComponent { /* Similar to Window */
        public void open() {System.out.println("Car window rolling down.");}
        public void close() {System.out.println("Car window rolling up.");}
    }
}
```

### Example 4: Polymorphism

Title: Enable Objects to Respond to the Same Message Differently
Description: Polymorphism allows objects of different classes (that share a common superclass or interface) to respond to the same message (method call) in their own specific ways. It simplifies client code, as it can interact with different types of objects through a common interface without needing to know their concrete types.

**Good example:**

```java
interface Drawable {
    void draw();
}

class CircleShape implements Drawable {
    @Override public void draw() { System.out.println("Drawing a Circle: O"); }
}

class SquareShape implements Drawable {
    @Override public void draw() { System.out.println("Drawing a Square: □"); }
}

class TriangleShape implements Drawable {
    @Override public void draw() { System.out.println("Drawing a Triangle: /\\"); }
}

public class PolymorphismExample {
    public static void drawShapes(List<Drawable> shapes) {
        for (Drawable shape : shapes) {
            shape.draw(); // Polymorphic call: actual method executed depends on shape's concrete type
        }
    }
    public static void main(String args) {
        List<Drawable> myShapes = List.of(
            new CircleShape(),
            new SquareShape(),
            new TriangleShape()
        );
        drawShapes(myShapes);
    }
}
```

**Bad example:**

```java
// Bad: Lacking polymorphism, using type checking and casting
class ShapeDrawer {
    public void drawSpecificShape(Object shape) {
        if (shape instanceof CircleShapeBad) {
            ((CircleShapeBad) shape).drawCircle();
        } else if (shape instanceof SquareShapeBad) {
            ((SquareShapeBad) shape).drawSquare();
        } else if (shape instanceof TriangleShapeBad) {
            ((TriangleShapeBad) shape).drawTriangle();
        } else {
            System.out.println("Unknown shape type.");
        }
        // This is not polymorphic. Adding new shapes requires modifying this method.
    }
}

class CircleShapeBad { public void drawCircle() { System.out.println("Drawing Circle (Bad)."); } }
class SquareShapeBad { public void drawSquare() { System.out.println("Drawing Square (Bad)."); } }
class TriangleShapeBad { public void drawTriangle() { System.out.println("Drawing Triangle (Bad)."); } }
```