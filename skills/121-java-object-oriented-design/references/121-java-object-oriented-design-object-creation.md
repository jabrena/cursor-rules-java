---
name: 121-java-object-oriented-design-object-creation
description: Object creation guidance covering factories, builders, singletons, dependency injection, and unnecessary objects.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Object-Oriented Design Guidelines

## Role

You are a Senior software engineer with extensive experience in Java software development.

## Goal

Improve object creation when the skill orchestration identifies constructor, lifecycle, dependency, or allocation design problems.
Read this reference only after `121-java-object-oriented-design` has compiled the project and classified this concern as applicable.

## Constraints

Apply only guidance relevant to the diagnosed concern and preserve observable behavior.

- Read this reference only when the SKILL.md orchestration maps the request or code problem to this concern.
- Apply focused, incremental refactorings and preserve existing business behavior.
- Do not broaden the change into unrelated object-oriented design concerns.

## Examples

### Table of contents

- Example 1: Creating and Destroying Objects
- Example 2: Consider Static Factory Methods Instead of Constructors
- Example 3: Consider a Builder When Faced with Many Constructor Parameters
- Example 4: Enforce the Singleton Property with a Private Constructor or an Enum Type
- Example 5: Prefer Dependency Injection to Hardwiring Resources
- Example 6: Avoid Creating Unnecessary Objects

### Example 1: Creating and Destroying Objects

Title: Best Practices for Object Creation and Destruction
Description: Effective object creation and destruction patterns improve code clarity, performance, and maintainability. These practices help avoid common pitfalls and leverage Java's capabilities effectively.

### Example 2: Consider Static Factory Methods Instead of Constructors

Title: Use static factory methods to provide more flexibility than constructors
Description: Static factory methods offer advantages like descriptive names, ability to return existing instances, and flexibility in return types.

**Good example:**

```java
public class BigInteger {
    // Static factory method with descriptive name
    public static BigInteger valueOf(long val) {
        if (val == 0) return ZERO;  // Return cached instance
        if (val > 0 && val <= MAX_CONSTANT) return posConst[(int) val];
        return new BigInteger(val);
    }

    // Private constructor
    private BigInteger(long val) { /* implementation */ }

    private static final BigInteger ZERO = new BigInteger(0);
    private static final BigInteger[] posConst = new BigInteger[MAX_CONSTANT + 1];
}

// Usage with clear intent
BigInteger zero = BigInteger.valueOf(0);  // Clear what we're creating
BigInteger hundred = BigInteger.valueOf(100);
```

**Bad example:**

```java
public class BigInteger {
    // Only constructor available - less flexible
    public BigInteger(long val) { /* implementation */ }

    // Client code is less clear
    BigInteger zero = new BigInteger(0);  // Not clear this could be cached
    BigInteger hundred = new BigInteger(100);  // Creates new instance every time
}
```

### Example 3: Consider a Builder When Faced with Many Constructor Parameters

Title: Use the Builder pattern for classes with multiple optional parameters
Description: The Builder pattern provides a readable alternative to telescoping constructors and is safer than JavaBeans pattern.

**Good example:**

```java
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public static class Builder {
        // Required parameters
        private final int servingSize;
        private final int servings;

        // Optional parameters - initialized to default values
        private int calories = 0;
        private int fat = 0;
        private int sodium = 0;
        private int carbohydrate = 0;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int val) { calories = val; return this; }
        public Builder fat(int val) { fat = val; return this; }
        public Builder sodium(int val) { sodium = val; return this; }
        public Builder carbohydrate(int val) { carbohydrate = val; return this; }

        public NutritionFacts build() {
            return new NutritionFacts(this);
        }
    }

    private NutritionFacts(Builder builder) {
        servingSize = builder.servingSize;
        servings = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
        sodium = builder.sodium;
        carbohydrate = builder.carbohydrate;
    }
}

// Usage - readable and flexible
NutritionFacts cocaCola = new NutritionFacts.Builder(240, 8)
    .calories(100)
    .sodium(35)
    .carbohydrate(27)
    .build();
```

**Bad example:**

```java
// Telescoping constructor pattern - hard to read and error-prone
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public NutritionFacts(int servingSize, int servings) {
        this(servingSize, servings, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories) {
        this(servingSize, servings, calories, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat) {
        this(servingSize, servings, calories, fat, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium) {
        this(servingSize, servings, calories, fat, sodium, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate) {
        this.servingSize = servingSize;
        this.servings = servings;
        this.calories = calories;
        this.fat = fat;
        this.sodium = sodium;
        this.carbohydrate = carbohydrate;
    }
}

// Usage - confusing parameter order, easy to make mistakes
NutritionFacts cocaCola = new NutritionFacts(240, 8, 100, 0, 35, 27);  // What do these numbers mean?
```

### Example 4: Enforce the Singleton Property with a Private Constructor or an Enum Type

Title: Use enum or private constructor with static field for singletons
Description: Enum-based singletons are the best way to implement singletons, providing serialization and reflection safety.

**Good example:**

```java
// Enum singleton - preferred approach
public enum DatabaseConnection {
    INSTANCE;

    public void connect() {
        System.out.println("Connecting to database...");
    }

    public void executeQuery(String query) {
        System.out.println("Executing: " + query);
    }
}

// Alternative: Static field with private constructor
public class Logger {
    private static final Logger INSTANCE = new Logger();

    private Logger() { /* private constructor */ }

    public static Logger getInstance() {
        return INSTANCE;
    }

    public void log(String message) {
        System.out.println("LOG: " + message);
    }
}

// Usage
DatabaseConnection.INSTANCE.connect();
Logger.getInstance().log("Application started");
```

**Bad example:**

```java
// Not thread-safe singleton
public class BadSingleton {
    private static BadSingleton instance;

    private BadSingleton() {}

    public static BadSingleton getInstance() {
        if (instance == null) {  // Race condition possible
            instance = new BadSingleton();
        }
        return instance;
    }
}
```

### Example 5: Prefer Dependency Injection to Hardwiring Resources

Title: Use dependency injection instead of hardcoded dependencies
Description: Classes should not create their dependencies directly but receive them from external sources, improving testability and flexibility.

**Good example:**

```java
public class SpellChecker {
    private final Lexicon dictionary;

    // Dependency injected through constructor
    public SpellChecker(Lexicon dictionary) {
        this.dictionary = Objects.requireNonNull(dictionary);
    }

    public boolean isValid(String word) {
        return dictionary.contains(word);
    }
}

interface Lexicon {
    boolean contains(String word);
}

class EnglishLexicon implements Lexicon {
    public boolean contains(String word) {
        // English dictionary lookup
        return true;
    }
}

// Usage - flexible and testable
Lexicon englishDict = new EnglishLexicon();
SpellChecker checker = new SpellChecker(englishDict);
```

**Bad example:**

```java
// Hardwired dependency - inflexible and hard to test
public class SpellChecker {
    private static final Lexicon dictionary = new EnglishLexicon();  // Hardcoded

    private SpellChecker() {}  // Noninstantiable

    public static boolean isValid(String word) {
        return dictionary.contains(word);
    }
}
```

### Example 6: Avoid Creating Unnecessary Objects

Title: Reuse objects when possible to improve performance
Description: Object creation can be expensive. Reuse immutable objects and avoid creating objects in loops when possible.

**Good example:**

```java
public class DateUtils {
    // Reuse expensive objects
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String formatDate(LocalDate date) {
        return FORMATTER.format(date);  // Reuse formatter
    }

    // Use primitives when possible
    public boolean isEven(int number) {
        return number % 2 == 0;  // No object creation
    }
}
```

**Bad example:**

```java
public class DateUtils {
    public String formatDate(LocalDate date) {
        // Creates new formatter every time - expensive
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return formatter.format(date);
    }

    // Unnecessary autoboxing
    public boolean isEven(Integer number) {
        return number % 2 == 0;  // Creates Integer objects
    }
}
```