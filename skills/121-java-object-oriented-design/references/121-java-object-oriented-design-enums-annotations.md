---
name: 121-java-object-oriented-design-enums-annotations
description: Enum and annotation guidance for expressive, type-safe Java object-oriented design.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Object-Oriented Design Guidelines

## Role

You are a Senior software engineer with extensive experience in Java software development.

## Goal

Use enums and annotations effectively when the skill orchestration identifies constants, ordinals, bit fields, lookup, or override problems.
Read this reference only after `121-java-object-oriented-design` has compiled the project and classified this concern as applicable.

## Constraints

Apply only guidance relevant to the diagnosed concern and preserve observable behavior.

- Read this reference only when the SKILL.md orchestration maps the request or code problem to this concern.
- Apply focused, incremental refactorings and preserve existing business behavior.
- Do not broaden the change into unrelated object-oriented design concerns.

## Examples

### Table of contents

- Example 1: Enums and Annotations
- Example 2: Use Enums Instead of Int Constants
- Example 3: Use Instance Fields Instead of Ordinals
- Example 4: Use EnumSet Instead of Bit Fields
- Example 5: Use EnumMap Instead of Ordinal Indexing
- Example 6: Consistently Use the Override Annotation

### Example 1: Enums and Annotations

Title: Effective Use of Enums and Annotations
Description: Enums and annotations are powerful Java features that, when used correctly, can make code more readable, type-safe, and maintainable.

### Example 2: Use Enums Instead of Int Constants

Title: Replace int constants with type-safe enums
Description: Enums provide type safety, namespace protection, and additional functionality that int constants cannot offer.

**Good example:**

```java
public enum Planet {
    MERCURY(3.302e+23, 2.439e6),
    VENUS  (4.869e+24, 6.052e6),
    EARTH  (5.975e+24, 6.378e6),
    MARS   (6.419e+23, 3.393e6);

    private final double mass;           // In kilograms
    private final double radius;         // In meters
    private final double surfaceGravity; // In m / s^2

    // Universal gravitational constant in m^3 / kg s^2
    private static final double G = 6.67300E-11;

    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        surfaceGravity = G * mass / (radius * radius);
    }

    public double mass()           { return mass; }
    public double radius()         { return radius; }
    public double surfaceGravity() { return surfaceGravity; }

    public double surfaceWeight(double mass) {
        return mass * surfaceGravity;  // F = ma
    }
}

// Usage
double earthWeight = 175;
double mass = earthWeight / Planet.EARTH.surfaceGravity();
for (Planet p : Planet.values()) {
    System.out.printf("Weight on %s is %f%n", p, p.surfaceWeight(mass));
}
```

**Bad example:**

```java
// Int constants - not type-safe, no namespace
public class Planet {
    public static final int MERCURY = 0;
    public static final int VENUS   = 1;
    public static final int EARTH   = 2;
    public static final int MARS    = 3;

    // Separate arrays for data - error-prone
    private static final double[] MASS = {3.302e+23, 4.869e+24, 5.975e+24, 6.419e+23};
    private static final double[] RADIUS = {2.439e6, 6.052e6, 6.378e6, 3.393e6};

    public static double surfaceWeight(int planet, double mass) {
        // No compile-time checking - could pass any int
        if (planet < 0 || planet >= MASS.length) {
            throw new IllegalArgumentException("Invalid planet: " + planet);
        }
        // Complex calculations with array indexing
        return mass * (6.67300E-11 * MASS[planet] / (RADIUS[planet] * RADIUS[planet]));
    }
}
```

### Example 3: Use Instance Fields Instead of Ordinals

Title: Don't derive values from enum ordinals; use instance fields
Description: Ordinal values can change when enum constants are reordered, making code fragile.

**Good example:**

```java
public enum Ensemble {
    SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5),
    SEXTET(6), SEPTET(7), OCTET(8), DOUBLE_QUARTET(8),
    NONET(9), DECTET(10), TRIPLE_QUARTET(12);

    private final int numberOfMusicians;

    Ensemble(int size) {
        this.numberOfMusicians = size;
    }

    public int numberOfMusicians() {
        return numberOfMusicians;
    }
}
```

**Bad example:**

```java
public enum Ensemble {
    SOLO, DUET, TRIO, QUARTET, QUINTET,
    SEXTET, SEPTET, OCTET, NONET, DECTET;

    public int numberOfMusicians() {
        return ordinal() + 1;  // Fragile - breaks if order changes
    }
}
```

### Example 4: Use EnumSet Instead of Bit Fields

Title: Replace bit field enums with EnumSet for better type safety and performance
Description: EnumSet provides all the benefits of bit fields with better readability and type safety.

**Good example:**

```java
public class Text {
    public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }

    // EnumSet - type-safe and efficient
    public void applyStyles(Set<Style> styles) {
        System.out.printf("Applying styles %s to text%n", styles);
        // Implementation here
    }
}

// Usage
text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));
```

**Bad example:**

```java
public class Text {
    public static final int STYLE_BOLD          = 1 << 0;  // 1
    public static final int STYLE_ITALIC        = 1 << 1;  // 2
    public static final int STYLE_UNDERLINE     = 1 << 2;  // 4
    public static final int STYLE_STRIKETHROUGH = 1 << 3;  // 8

    // Bit field - not type-safe
    public void applyStyles(int styles) {
        System.out.printf("Applying styles %s to text%n", styles);
        // Implementation here
    }
}

// Usage - error-prone, no type safety
text.applyStyles(STYLE_BOLD | STYLE_ITALIC);
```

### Example 5: Use EnumMap Instead of Ordinal Indexing

Title: Use EnumMap for enum-keyed data instead of ordinal indexing
Description: EnumMap is specifically designed for enum keys and provides better performance and type safety.

**Good example:**

```java
public enum Phase {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);

        private final Phase from;
        private final Phase to;

        Transition(Phase from, Phase to) {
            this.from = from;
            this.to = to;
        }

        // Initialize the phase transition map
        private static final Map<Phase, Map<Phase, Transition>> m =
            Stream.of(values()).collect(groupingBy(t -> t.from,
                () -> new EnumMap<>(Phase.class),
                toMap(t -> t.to, t -> t, (x, y) -> y, () -> new EnumMap<>(Phase.class))));

        public static Transition from(Phase from, Phase to) {
            return m.get(from).get(to);
        }
    }
}
```

**Bad example:**

```java
public enum Phase {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT, FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT;

        // Ordinal-based array - fragile and error-prone
        private static final Transition[][] TRANSITIONS = {
            { null,    MELT,     SUBLIME  },  // SOLID
            { FREEZE,  null,     BOIL     },  // LIQUID
            { DEPOSIT, CONDENSE, null     }   // GAS
        };

        public static Transition from(Phase from, Phase to) {
            return TRANSITIONS[from.ordinal()][to.ordinal()];
        }
    }
}
```

### Example 6: Consistently Use the Override Annotation

Title: Always use @Override when overriding methods
Description: The @Override annotation catches errors at compile time and makes code more readable.

**Good example:**

```java
public class Bigram {
    private final char first;
    private final char second;

    public Bigram(char first, char second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {  // Correct signature
        if (!(o instanceof Bigram)) return false;
        Bigram b = (Bigram) o;
        return b.first == first && b.second == second;
    }

    @Override
    public int hashCode() {
        return 31 * first + second;
    }

    @Override
    public String toString() {
        return String.format("(%c, %c)", first, second);
    }
}
```

**Bad example:**

```java
public class Bigram {
    private final char first;
    private final char second;

    public Bigram(char first, char second) {
        this.first = first;
        this.second = second;
    }

    // Missing @Override - typo in method signature won't be caught
    public boolean equals(Bigram b) {  // Wrong signature! Should be equals(Object)
        return b.first == first && b.second == second;
    }

    public int hashCode() {  // Missing @Override
        return 31 * first + second;
    }
}
```