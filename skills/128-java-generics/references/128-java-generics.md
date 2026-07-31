---
name: 128-java-generics
description: Use when you need to review, improve, or refactor Java code for generics quality — including avoiding raw types, applying PECS wildcards, using bounded type parameters, designing effective generic methods, leveraging type inference with the diamond operator, handling type erasure, preventing heap pollution with @SafeVarargs, and integrating generics with Records, sealed types, and pattern matching.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Generics Best Practices

## Role

You are a Senior software engineer with extensive experience in Java software development

## Goal

Java Generics provide compile-time type safety, eliminate casting, and enable algorithms to work on collections of different types. Effective use involves proper wildcard usage (? extends for producer, ? super for consumer), bounded type parameters, generic method design, and avoiding raw types. Key practices include leveraging type inference with diamond operator, understanding type erasure implications, using generic constructors and factory methods, and applying PECS (Producer Extends Consumer Super) principle. Modern approaches integrate generics with Records, sealed types, functional interfaces, and pattern matching for robust, type-safe APIs.

### Implementing These Principles

These guidelines are built upon the following core principles:

1.  **Type Safety**: Generics eliminate ClassCastException at runtime by moving type checking to compile time. They ensure type correctness and make invalid type combinations impossible to express, reducing bugs and improving code reliability.
2.  **Code Reusability**: Generic types and methods allow writing flexible, reusable code that works with multiple types while maintaining type safety. This reduces code duplication and improves maintainability across different type contexts.
3.  **API Clarity**: Well-designed generic APIs communicate intent clearly through type parameters, bounded wildcards, and constraints. They make the contract between callers and implementations explicit and self-documenting.
4.  **Performance Optimization**: Generics eliminate the need for boxing/unboxing and casting operations, reducing runtime overhead. They enable the compiler to generate more efficient bytecode and allow for better JVM optimizations.
5.  **Integration with Modern Java**: Generics work seamlessly with modern Java features like Records, sealed types, pattern matching, and functional interfaces, enabling expressive and type-safe modern Java code patterns.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully and pass basic validation checks before any optimization
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE with any recommendations
- **BLOCKING CONDITION**: Compilation errors must be resolved by the user before proceeding with any generics improvements
- **NO EXCEPTIONS**: Under no circumstances should generics recommendations be applied to a project that fails to compile

## Examples

### Table of contents

- Example 1: Avoid Raw Types
- Example 2: Apply PECS Principle
- Example 3: Use Bounded Type Parameters
- Example 4: Design Effective Generic Methods
- Example 5: Use Diamond Operator for Type Inference
- Example 6: Understand Type Erasure Implications
- Example 7: Handle Generic Inheritance Correctly
- Example 8: Combine Generics with Modern Java Features
- Example 9: Prevent Heap Pollution with @SafeVarargs
- Example 10: Use Helper Methods for Wildcard Capture
- Example 11: Apply Self-Bounded Generics for Fluent Builders
- Example 12: Design APIs with Proper Wildcards
- Example 13: Avoid Arrays Covariance Pitfalls
- Example 14: Serialize Collections with Type Tokens
- Example 15: Eliminate Unchecked Warnings
- Example 16: Follow Generic Type Parameter Naming Conventions
- Example 17: Use Typesafe Heterogeneous Containers

### Example 1: Avoid Raw Types

Title: Use parameterized types instead of raw types
Description: Raw types bypass generic type checking and can lead to ClassCastException at runtime. Always use parameterized types to maintain compile-time type safety. Raw types are only acceptable when interfacing with legacy code that doesn't use generics.

**Good example:**

```java
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class TypeSafeCollections {

    // Properly parameterized generic types
    private final List<String> names = new ArrayList<>();
    private final Map<String, Integer> scores = new HashMap<>();

    public void addName(String name) {
        names.add(name); // Type safe - only String can be added
    }

    public void addScore(String name, Integer score) {
        scores.put(name, score); // Type safe - correct key/value types
    }

    public String getFirstName() {
        return names.isEmpty() ? "" : names.get(0); // No casting needed
    }

    public Integer getScore(String name) {
        return scores.get(name); // Returns Integer, not Object
    }
}
```

**Bad example:**

```java
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class RawTypeExample {

    // Raw types - dangerous and deprecated
    private final List names = new ArrayList(); // No type information
    private final Map scores = new HashMap();   // No type information

    public void addName(String name) {
        names.add(name); // Can add any type, not just String
    }

    public void addScore(String name, Integer score) {
        scores.put(name, score); // Can put any key/value types
    }

    public String getFirstName() {
        // Unsafe cast - can throw ClassCastException at runtime
        return names.isEmpty() ? "" : (String) names.get(0);
    }

    public Integer getScore(String name) {
        // Unsafe cast - can throw ClassCastException at runtime
        return (Integer) scores.get(name);
    }
}
```

### Example 2: Apply PECS Principle

Title: Producer Extends, Consumer Super for wildcards
Description: Use `? extends T` for producers (you only read from the collection) and `? super T` for consumers (you only write to the collection). This follows the PECS (Producer Extends Consumer Super) principle and provides maximum flexibility for API users while maintaining type safety.

**Good example:**

```java
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

public class PECSExample {

    // Producer: use 'extends' - we read from source
    public static <T> void copy(List<? super T> dest, List<? extends T> src) {
        for (T item : src) {
            dest.add(item); // Safe: T is subtype of dest's type
        }
    }

    // Producer: use 'extends' - we only read numbers
    public static double sum(List<? extends Number> numbers) {
        double total = 0.0;
        for (Number num : numbers) {
            total += num.doubleValue(); // Safe: all items are Numbers
        }
        return total;
    }

    // Consumer: use 'super' - we only add items
    public static <T> void addAll(Collection<? super T> collection, T... items) {
        for (T item : items) {
            collection.add(item); // Safe: collection accepts T or supertypes
        }
    }

    // Usage examples
    public static void demonstrateUsage() {
        List<Integer> integers = List.of(1, 2, 3);
        List<Double> doubles = List.of(1.5, 2.5, 3.5);
        List<Number> numbers = new ArrayList<>();

        // Can pass Integer list to sum (Integer extends Number)
        double intSum = sum(integers);
        double doubleSum = sum(doubles);

        // Can add integers to Number collection (Number super Integer)
        addAll(numbers, 1, 2, 3);

        // Can copy from specific type to more general type
        copy(numbers, integers);
    }
}
```

**Bad example:**

```java
import java.util.List;
import java.util.Collection;

public class BadWildcardExample {

    // Too restrictive - doesn't allow flexibility
    public static void copy(List<Object> dest, List<Object> src) {
        // Can only work with List<Object>, not List<String> etc.
        for (Object item : src) {
            dest.add(item);
        }
    }

    // Wrong wildcard direction
    public static double sum(List<? super Number> numbers) {
        double total = 0.0;
        for (Object num : numbers) { // Have to use Object, lose type info
            total += ((Number) num).doubleValue(); // Unsafe cast required
        }
        return total;
    }

    // Too restrictive - forces exact type match
    public static <T> void addAll(Collection<T> collection, T... items) {
        // Collection must be exactly T, not supertype of T
        for (T item : items) {
            collection.add(item);
        }
    }
}
```

### Example 3: Use Bounded Type Parameters

Title: Restrict type parameters with bounds for better APIs
Description: Use bounded type parameters (`<T extends SomeType>` or `<T extends Type1 & Type2>`) to restrict the types that can be used and to access methods of the bound type. This provides compile-time guarantees and enables more specific operations on the generic types.

**Good example:**

```java
import java.util.List;
import java.util.Comparator;
import java.util.Collections;

// Bounded type parameter with single bound
public class BoundedGenerics {

    // T must extend Number, allowing numeric operations
    public static <T extends Number> double average(List<T> numbers) {
        if (numbers.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (T number : numbers) {
            sum += number.doubleValue(); // Can call Number methods
        }
        return sum / numbers.size();
    }

    // T must extend Comparable<T>, enabling sorting
    public static <T extends Comparable<T>> T findMax(List<T> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("List cannot be empty");
        }
        return Collections.max(items); // Can use Comparable interface
    }

    // Multiple bounds: T must extend Number AND implement Comparable
    public static <T extends Number & Comparable<T>> T findMedian(List<T> numbers) {
        if (numbers.isEmpty()) {
            throw new IllegalArgumentException("List cannot be empty");
        }
        List<T> sorted = numbers.stream()
            .sorted() // Works because T implements Comparable
            .toList();
        return sorted.get(sorted.size() / 2);
    }
}

// Interface with bounded type parameter
interface Repository<T extends Entity> {
    void save(T entity);
    T findById(Long id);
    List<T> findAll();
}

abstract class Entity {
    protected Long id;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
```

**Bad example:**

```java
import java.util.List;

// Unbounded generics lose type information
public class UnboundedGenerics {

    // Can't perform numeric operations on T
    public static <T> double average(List<T> items) {
        if (items.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (T item : items) {
            // Compilation error: can't call doubleValue() on Object
            // sum += item.doubleValue();

            // Would need unsafe casting
            sum += ((Number) item).doubleValue(); // ClassCastException risk
        }
        return sum / items.size();
    }

    // Can't sort without bounds
    public static <T> T findMax(List<T> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("List cannot be empty");
        }
        // Compilation error: T is not guaranteed to be Comparable
        // return Collections.max(items);

        // Would need to require Comparator parameter
        return null; // Can't implement without bounds
    }
}
```

### Example 4: Design Effective Generic Methods

Title: Use generic methods for flexibility and type inference
Description: Generic methods can infer types from their usage context, making them more flexible than generic classes. Use generic methods when the type parameter is only relevant to that specific method, not the entire class. This enables type-safe utility methods and better API design.

**Good example:**

```java
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class GenericMethods {

    // Generic method with type inference
    public static <T> List<T> createList(T... elements) {
        List<T> list = new ArrayList<>();
        for (T element : elements) {
            list.add(element);
        }
        return list;
    }

    // Generic method with multiple type parameters
    public static <K, V> Map<K, V> createMap(K[] keys, V[] values) {
        if (keys.length != values.length) {
            throw new IllegalArgumentException("Arrays must have same length");
        }
        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }

    // Generic method with bounded type parameter
    public static <T extends Comparable<T>> boolean isSorted(List<T> list) {
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i - 1).compareTo(list.get(i)) > 0) {
                return false;
            }
        }
        return true;
    }

    // Generic method with wildcard parameters and functional interface
    public static <T, R> List<R> transform(List<? extends T> source,
                                          Function<? super T, ? extends R> mapper) {
        List<R> result = new ArrayList<>();
        for (T item : source) {
            result.add(mapper.apply(item));
        }
        return result;
    }

    // Generic method for filtering
    public static <T> List<T> filter(List<? extends T> source,
                                   Predicate<? super T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : source) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    // Usage demonstrates type inference
    public static void demonstrateUsage() {
        // Type inference in action
        List<String> strings = createList("a", "b", "c"); // Infers String
        List<Integer> numbers = createList(1, 2, 3);      // Infers Integer

        // Multiple type parameter inference
        String[] keys = {"name", "age"};
        Object[] values = {"John", 25};
        Map<String, Object> person = createMap(keys, values);

        // Generic method with transformation
        List<Integer> lengths = transform(strings, String::length);
        List<String> evenNumbers = filter(numbers, n -> n % 2 == 0)
            .stream()
            .map(String::valueOf)
            .toList();
    }
}
```

**Bad example:**

```java
import java.util.List;
import java.util.ArrayList;

// Non-generic methods lose type safety and flexibility
public class NonGenericMethods {

    // Only works with Object, requires casting
    public static List createList(Object... elements) {
        List list = new ArrayList(); // Raw type
        for (Object element : elements) {
            list.add(element);
        }
        return list; // Raw type return
    }

    // Separate methods needed for each type
    public static List<String> createStringList(String... elements) {
        List<String> list = new ArrayList<>();
        for (String element : elements) {
            list.add(element);
        }
        return list;
    }

    public static List<Integer> createIntegerList(Integer... elements) {
        List<Integer> list = new ArrayList<>();
        for (Integer element : elements) {
            list.add(element);
        }
        return list;
    }

    // Can't check if sorted without knowing the type
    public static boolean isSorted(List list) {
        for (int i = 1; i < list.size(); i++) {
            Object prev = list.get(i - 1);
            Object curr = list.get(i);
            // Can't compare without casting and type knowledge
            // Would need instanceof checks and casting
        }
        return false; // Implementation not possible
    }

    // Usage requires casting and loses type safety
    public static void demonstrateProblems() {
        List strings = createList("a", "b", "c"); // Raw type
        String first = (String) strings.get(0);   // Unsafe cast

        // Easy to mix types accidentally
        List mixed = createList("string", 42, true);
        String item = (String) mixed.get(1); // ClassCastException!
    }
}
```

### Example 5: Use Diamond Operator for Type Inference

Title: Leverage diamond operator to reduce verbosity
Description: Use the diamond operator (`<>`) introduced in Java 7 to avoid repeating type parameters on the right side of assignments. The compiler can infer the types from the left side, reducing verbosity while maintaining type safety. This works with constructors, method calls, and anonymous classes.

**Good example:**

```java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DiamondOperatorExample {

    // Simple collection initialization
    private final List<String> names = new ArrayList<>();
    private final Set<Integer> numbers = new HashSet<>();
    private final Map<String, List<Integer>> groupedData = new HashMap<>();

    // Nested generics with diamond operator
    private final Map<String, Map<String, List<Object>>> complexMap = new ConcurrentHashMap<>();

    // Method return with diamond operator
    public List<String> createStringList() {
        return new ArrayList<>(); // Type inferred from return type
    }

    // Anonymous class with diamond operator (Java 9+)
    private final Comparator<String> lengthComparator = new Comparator<>() {
        @Override
        public int compare(String s1, String s2) {
            return Integer.compare(s1.length(), s2.length());
        }
    };

    // Method parameter inference
    public void processData() {
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 95);
        scores.put("Bob", 87);

        // Diamond operator with method calls
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(scores.entrySet());

        // Complex nested types
        Map<String, List<Map<String, Object>>> nested = new HashMap<>();
        nested.put("data", new ArrayList<>());
    }

    // Factory method with diamond operator
    public static <T> Optional<T> createOptional(T value) {
        return value != null ? Optional.of(value) : Optional.empty();
    }
}
```

**Bad example:**

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Verbose repetition of type parameters
public class VerboseGenerics {

    // Redundant type specification
    private final List<String> names = new ArrayList<String>();
    private final Set<Integer> numbers = new HashSet<Integer>();
    private final Map<String, List<Integer>> groupedData = new HashMap<String, List<Integer>>();

    // Extremely verbose nested generics
    private final Map<String, Map<String, List<Object>>> complexMap =
        new HashMap<String, Map<String, List<Object>>>();

    public List<String> createStringList() {
        // Unnecessary repetition
        return new ArrayList<String>();
    }

    public void processData() {
        // Repetitive type declarations
        Map<String, Integer> scores = new HashMap<String, Integer>();
        scores.put("Alice", 95);
        scores.put("Bob", 87);

        // More unnecessary verbosity
        List<Map.Entry<String, Integer>> entries =
            new ArrayList<Map.Entry<String, Integer>>(scores.entrySet());

        // Overly complex nested type declarations
        Map<String, List<Map<String, Object>>> nested =
            new HashMap<String, List<Map<String, Object>>>();
        nested.put("data", new ArrayList<Map<String, Object>>());
    }
}
```

### Example 6: Understand Type Erasure Implications

Title: Be aware of type erasure limitations and work around them
Description: Java generics use type erasure - generic type information is removed at runtime. This affects reflection, instanceof checks, array creation, and exception handling. Use type tokens, factory patterns, or bounds to work around erasure limitations when runtime type information is needed.

**Good example:**

```java
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;

public class TypeErasureWorkarounds {

    // Type token pattern for runtime type information
    public abstract static class TypeToken<T> {
        private final Type type;

        protected TypeToken() {
            Type superclass = getClass().getGenericSuperclass();
            this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        }

        public Type getType() {
            return type;
        }

        @SuppressWarnings("unchecked")
        public Class<T> getRawType() {
            if (type instanceof Class) {
                return (Class<T>) type;
            } else if (type instanceof ParameterizedType) {
                return (Class<T>) ((ParameterizedType) type).getRawType();
            }
            throw new IllegalArgumentException("Cannot determine raw type for " + type);
        }
    }

    // Generic array creation with Class parameter
    @SuppressWarnings("unchecked")
    public static <T> T[] createArray(Class<T> componentType, int size) {
        return (T[]) Array.newInstance(componentType, size);
    }

    // Factory pattern to preserve type information
    public static class GenericFactory<T> {
        private final Class<T> type;

        public GenericFactory(Class<T> type) {
            this.type = type;
        }

        public T createInstance() {
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Cannot create instance of " + type, e);
            }
        }

        public T[] createArray(int size) {
            return createArray(type, size);
        }

        public boolean isInstance(Object obj) {
            return type.isInstance(obj);
        }
    }

    // Safe generic collection conversion
    @SuppressWarnings("unchecked")
    public static <T> List<T> castList(List<?> list, Class<T> elementType) {
        for (Object item : list) {
            if (!elementType.isInstance(item)) {
                throw new ClassCastException("List contains non-" + elementType.getSimpleName() + " element: " + item);
            }
        }
        return (List<T>) list;
    }

    // Usage examples
    public static void demonstrateUsage() {
        // Type token usage
        TypeToken<List<String>> listToken = new TypeToken<List<String>>() {};
        Type listType = listToken.getType();

        // Generic array creation
        String[] stringArray = createArray(String.class, 10);
        Integer[] intArray = createArray(Integer.class, 5);

        // Factory pattern usage
        GenericFactory<StringBuilder> sbFactory = new GenericFactory<>(StringBuilder.class);
        StringBuilder sb = sbFactory.createInstance();
        StringBuilder[] sbArray = sbFactory.createArray(3);

        // Safe casting
        List<Object> objectList = new ArrayList<>();
        objectList.add("Hello");
        objectList.add("World");
        List<String> stringList = castList(objectList, String.class);
    }
}
```

**Bad example:**

```java
import java.util.List;
import java.util.ArrayList;

// Common type erasure mistakes
public class TypeErasureProblems {

    // COMPILATION ERROR: Cannot create generic array directly
    // private T[] array = new T[10];

    // COMPILATION ERROR: Cannot use instanceof with generic types
    public static <T> boolean isList(Object obj) {
        // return obj instanceof List<T>; // Doesn't compile
        return obj instanceof List; // Loses type information
    }

    // COMPILATION ERROR: Cannot catch generic exception types
    public static <T extends Exception> void handleException() {
        try {
            // some operation
        }
        // catch (T e) { // Doesn't compile
        //     handle(e);
        // }
        catch (Exception e) { // Loses specific type
            // Handle generically
        }
    }

    // Unsafe operations due to type erasure
    @SuppressWarnings("unchecked")
    public static <T> T[] createArrayUnsafe(int size) {
        // This compiles but fails at runtime with ClassCastException
        return (T[]) new Object[size];
    }

    // Loses type information at runtime
    public static <T> void processGeneric(List<T> list) {
        // Cannot determine T at runtime
        // if (list.get(0) instanceof String) { // Can check individual elements
        //     // But can't know if T is String
        // }

        // Type information is erased
        System.out.println("Processing list of unknown type");
    }

    // Reflection doesn't work with generic types
    public static <T> Class<T> getGenericClass() {
        // return T.class; // Doesn't compile - T is not available at runtime
        return null; // Cannot obtain Class<T>
    }

    // Unsafe casting without proper checks
    @SuppressWarnings("unchecked")
    public static <T> List<T> unsafeCast(List<?> list) {
        return (List<T>) list; // Compiles but may fail at runtime
    }

    public static void demonstrateProblems() {
        // These operations compile but may fail at runtime
        String[] strings = createArrayUnsafe(5); // ClassCastException
        List<String> stringList = unsafeCast(new ArrayList<Integer>()); // No immediate error
        // stringList.add("test"); // ClassCastException when used
    }
}
```

### Example 7: Handle Generic Inheritance Correctly

Title: Understand variance and inheritance with generics
Description: Generics are invariant in Java - `List<String>` is not a subtype of `List<Object>` even though `String` extends `Object`. Use wildcards for covariance (`? extends`) and contravariance (`? super`). Be careful with generic inheritance and override rules.

**Good example:**

```java
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

// Proper generic inheritance design
public class GenericInheritanceExample {

    // Base generic class
    public static abstract class Container<T> {
        protected T item;

        public Container(T item) {
            this.item = item;
        }

        public T getItem() {
            return item;
        }

        // Generic method that subclasses can override properly
        public abstract boolean accepts(T item);

        // Method using wildcards for flexibility
        public void copyFrom(Container<? extends T> other) {
            this.item = other.getItem(); // Safe: ? extends T means subtype of T
        }
    }

    // Concrete implementation maintains type parameter
    public static class StringContainer extends Container<String> {
        public StringContainer(String item) {
            super(item);
        }

        @Override
        public boolean accepts(String item) {
            return item != null && !item.trim().isEmpty();
        }

        // Additional type-specific methods
        public int getLength() {
            return item != null ? item.length() : 0;
        }
    }

    // Generic inheritance with additional type parameter
    public static class PairContainer<T, U> extends Container<T> {
        private final U secondItem;

        public PairContainer(T first, U second) {
            super(first);
            this.secondItem = second;
        }

        public U getSecondItem() {
            return secondItem;
        }

        @Override
        public boolean accepts(T item) {
            return item != null;
        }
    }

    // Utility class demonstrating covariance/contravariance
    public static class CollectionUtils {

        // Covariant parameter - can read from collection of subtypes
        public static double sum(Collection<? extends Number> numbers) {
            double total = 0.0;
            for (Number num : numbers) {
                total += num.doubleValue();
            }
            return total;
        }

        // Contravariant parameter - can write to collection of supertypes
        public static <T> void addItems(Collection<? super T> collection, T... items) {
            for (T item : items) {
                collection.add(item);
            }
        }

        // Proper generic method inheritance
        public static <T extends Comparable<T>> T max(T first, T second) {
            return first.compareTo(second) >= 0 ? first : second;
        }
    }

    // Interface with proper generic inheritance
    public interface Repository<T, ID> {
        void save(T entity);
        T findById(ID id);
        List<T> findAll();
    }

    public static abstract class AbstractRepository<T, ID> implements Repository<T, ID> {
        @Override
        public List<T> findAll() {
            // Default implementation
            return new ArrayList<>();
        }
    }

    // Usage demonstration
    public static void demonstrateInheritance() {
        StringContainer stringContainer = new StringContainer("Hello");
        Container<String> container = stringContainer; // Proper inheritance

        // Variance examples
        List<Integer> integers = List.of(1, 2, 3);
        List<Double> doubles = List.of(1.5, 2.5, 3.5);

        // Both work with covariant parameter
        double intSum = CollectionUtils.sum(integers);
        double doubleSum = CollectionUtils.sum(doubles);

        // Contravariant usage
        Collection<Number> numbers = new ArrayList<>();
        CollectionUtils.addItems(numbers, 1, 2, 3); // Integers
        CollectionUtils.addItems(numbers, 1.5, 2.5); // Doubles
    }
}
```

**Bad example:**

```java
import java.util.List;
import java.util.ArrayList;

// Incorrect understanding of generic inheritance
public class BadGenericInheritance {

    // Common mistake: trying to use inheritance improperly
    public static void problematicMethods() {
        List<String> strings = new ArrayList<>();

        // COMPILATION ERROR: List<String> is not a subtype of List<Object>
        // List<Object> objects = strings; // Doesn't compile

        // Even though String extends Object, this doesn't work
        // because generics are invariant
    }

    // Raw type base class breaks type safety
    public static class BadContainer {
        protected Object item; // Lost type information

        public Object getItem() {
            return item;
        }

        public void setItem(Object item) { // Too general
            this.item = item;
        }
    }

    public static class BadStringContainer extends BadContainer {
        @Override
        public String getItem() { // Incorrect override
            return (String) super.getItem(); // Unsafe cast
        }

        // Can't prevent wrong types being set
        public void setStringItem(String item) {
            setItem(item); // But setItem(Integer) would also work!
        }
    }

    // Incorrect generic override
    public static class BaseProcessor<T> {
        public void process(T item) {
            System.out.println("Processing: " + item);
        }
    }

    // COMPILATION ERROR: Can't change generic type in override
    // public static class StringProcessor extends BaseProcessor<Object> {
    //     @Override
    //     public void process(String item) { // Doesn't override base method
    //         System.out.println("Processing string: " + item);
    //     }
    // }

    // Unsafe operations assuming inheritance works
    public static void unsafeOperations() {
        List<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);

        // This would be unsafe if it compiled:
        // List<Number> numbers = integers; // Doesn't compile (good!)
        // numbers.add(3.14); // Would break integers list

        // Workaround loses compile-time safety
        @SuppressWarnings("unchecked")
        List<Number> numbers = (List<Number>) (List<?>) integers;
        // numbers.add(3.14); // Runtime ClassCastException when reading integers
    }

    // Method that's too restrictive due to invariance misunderstanding
    public static void processStrings(List<String> strings) {
        // Can only accept exactly List<String>
        // Won't accept List<? extends String> or more flexible types
        for (String s : strings) {
            System.out.println(s);
        }
    }
}
```

### Example 8: Combine Generics with Modern Java Features

Title: Use generics with Records, sealed types, and pattern matching
Description: Modern Java features like Records, sealed types, and pattern matching work well with generics to create expressive, type-safe APIs. Records can be generic, sealed types can constrain generic hierarchies, and pattern matching can work with generic types for powerful conditional logic.

**Good example:**

```java
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

// Generic Records for immutable data structures
public record Result<T, E> (T value, E error, boolean isSuccess) {

    // Factory methods for success and failure
    public static <T, E> Result<T, E> success(T value) {
        return new Result<>(value, null, true);
    }

    public static <T, E> Result<T, E> failure(E error) {
        return new Result<>(null, error, false);
    }

    // Transform success value
    public <U> Result<U, E> map(Function<T, U> mapper) {
        return isSuccess ? success(mapper.apply(value)) : failure(error);
    }

    // Get value as Optional
    public Optional<T> toOptional() {
        return isSuccess ? Optional.of(value) : Optional.empty();
    }
}

// Generic Record with validation
public record Pair<T, U>(T first, U second) {
    public Pair {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Neither first nor second can be null");
        }
    }

    // Generic method in record
    public <V> Pair<V, U> mapFirst(Function<T, V> mapper) {
        return new Pair<>(mapper.apply(first), second);
    }
}

// Sealed interface with generic constraints
public sealed interface Container<T>
    permits SingleContainer, MultiContainer, EmptyContainer {

    boolean isEmpty();
    List<T> getItems();

    // Default method with generics
    default Optional<T> getFirst() {
        List<T> items = getItems();
        return items.isEmpty() ? Optional.empty() : Optional.of(items.get(0));
    }
}

// Implementations of sealed generic interface
public record SingleContainer<T>(T item) implements Container<T> {
    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public List<T> getItems() {
        return List.of(item);
    }
}

public record MultiContainer<T>(List<T> items) implements Container<T> {
    public MultiContainer {
        items = List.copyOf(items); // Defensive copy
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public List<T> getItems() {
        return items;
    }
}

public record EmptyContainer<T>() implements Container<T> {
    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public List<T> getItems() {
        return List.of();
    }
}

// Pattern matching with generics
public class GenericPatternMatching {

    // Pattern matching with sealed types and generics
    public static <T> String describe(Container<T> container) {
        return switch (container) {
            case SingleContainer<T> single ->
                "Single item: " + single.item();
            case MultiContainer<T> multi ->
                "Multiple items (" + multi.items().size() + ")";
            case EmptyContainer<T> empty ->
                "Empty container";
        };
    }

    // Pattern matching with generic Result
    public static <T> String processResult(Result<T, String> result) {
        return switch (result.isSuccess()) {
            case true -> "Success: " + result.value();
            case false -> "Error: " + result.error();
        };
    }

    // Generic method with pattern matching and instanceof
    public static <T> String processValue(Object value, Class<T> expectedType) {
        if (value instanceof String s) {
            return "String: " + s;
        } else if (value instanceof Integer i) {
            return "Integer: " + i;
        } else if (expectedType.isInstance(value)) {
            @SuppressWarnings("unchecked")
            T typedValue = (T) value;
            return "Expected type: " + typedValue;
        } else {
            return "Unknown type: " + value.getClass().getSimpleName();
        }
    }
}

// Usage examples
public class ModernGenericsUsage {
    public static void demonstrateUsage() {
        // Generic Records
        Result<String, Exception> stringResult = Result.success("Hello");
        Result<Integer, Exception> intResult = stringResult.map(String::length);

        Pair<String, Integer> nameAge = new Pair<>("Alice", 30);
        Pair<Integer, Integer> lengthAge = nameAge.mapFirst(String::length);

        // Sealed types with generics
        Container<String> single = new SingleContainer<>("Hello");
        Container<String> multi = new MultiContainer<>(List.of("A", "B", "C"));
        Container<String> empty = new EmptyContainer<>();

        // Pattern matching
        System.out.println(GenericPatternMatching.describe(single));
        System.out.println(GenericPatternMatching.describe(multi));
        System.out.println(GenericPatternMatching.describe(empty));

        // Result processing
        System.out.println(GenericPatternMatching.processResult(stringResult));
        System.out.println(GenericPatternMatching.processResult(
            Result.<String, String>failure("Something went wrong")));
    }
}
```

**Bad example:**

```java
import java.util.List;

// Not leveraging modern Java features with generics
public class OldStyleGenerics {

    // Traditional class instead of generic Record
    public static class OldResult<T, E> {
        private final T value;
        private final E error;
        private final boolean isSuccess;

        public OldResult(T value, E error, boolean isSuccess) {
            this.value = value;
            this.error = error;
            this.isSuccess = isSuccess;
        }

        // Verbose getters and methods
        public T getValue() { return value; }
        public E getError() { return error; }
        public boolean isSuccess() { return isSuccess; }

        // Need to implement equals, hashCode, toString manually
        @Override
        public boolean equals(Object obj) {
            // ... verbose implementation
            return false;
        }

        @Override
        public int hashCode() {
            // ... verbose implementation
            return 0;
        }

        @Override
        public String toString() {
            // ... verbose implementation
            return "";
        }
    }

    // Traditional inheritance hierarchy without sealed types
    public static abstract class OldContainer<T> {
        public abstract boolean isEmpty();
        public abstract List<T> getItems();
    }

    public static class OldSingleContainer<T> extends OldContainer<T> {
        private final T item;

        public OldSingleContainer(T item) {
            this.item = item;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public List<T> getItems() {
            return List.of(item);
        }

        public T getItem() {
            return item;
        }
    }

    // No pattern matching - verbose conditional logic
    public static <T> String describeOldWay(OldContainer<T> container) {
        if (container instanceof OldSingleContainer) {
            OldSingleContainer<T> single = (OldSingleContainer<T>) container;
            return "Single item: " + single.getItem();
        } else {
            // Would need more instanceof checks for other types
            return "Unknown container type";
        }
    }

    // Verbose error handling without modern patterns
    public static <T> String processOldResult(OldResult<T, String> result) {
        if (result.isSuccess()) {
            return "Success: " + result.getValue();
        } else {
            return "Error: " + result.getError();
        }
    }
}
```

### Example 9: Prevent Heap Pollution with @SafeVarargs

Title: Use @SafeVarargs on trusted generic varargs and avoid unsafe patterns
Description: Generic varargs can cause heap pollution because the varargs array is reifiable while its component type is erased. Mark only provably safe varargs methods with `@SafeVarargs` (final, static, or private) and avoid storing or exposing the varargs array. Validate when suppression is necessary and keep `@SuppressWarnings` as narrow as possible.

**Good example:**

```java
import java.util.Collection;
import java.util.List;

public class SafeVarargsExample {

    @SafeVarargs
    public static <T> List<T> of(T... items) {
        return List.of(items); // Doesn't store or mutate the varargs array
    }

    @SafeVarargs
    public static <T> void addAll(Collection<T> target, T... items) {
        for (T item : items) {
            target.add(item);
        }
    }
}
```

**Bad example:**

```java
import java.util.List;
import java.util.ArrayList;

public class UnsafeVarargsExample {

    // Generic array of parameterized type is unsafe (heap pollution risk)
    @SafeVarargs // Misused: method is not provably safe
    public static <T> void dangerous(List<T>... lists) {
        Object[] array = lists;               // Allowed by arrays covariance
        array[0] = List.of(42);               // Heap pollution
        T item = lists[0].get(0);             // ClassCastException at runtime
    }

    // Storing the varargs array leaks its alias and is unsafe
    private static List<Object[]> cache = new ArrayList<>();
    public static <T> void cacheVarargs(T... items) {
        cache.add(new Object[]{items});       // Stores generic varargs array
    }
}
```

### Example 10: Use Helper Methods for Wildcard Capture

Title: Implement operations like swap(List<?>) via capture
Description: Wildcard types like `List<?>` are read-only with respect to element type. To perform write operations, use a private helper method with its own type parameter so the compiler can capture the wildcard (`capture-of ?`). This enables safe implementations while keeping a flexible public API.

**Good example:**

```java
import java.util.List;

public class WildcardCaptureUtils {

    public static void swap(List<?> list, int i, int j) {
        swapHelper(list, i, j); // capture-of ? => T
    }

    private static <T> void swapHelper(List<T> list, int i, int j) {
        T tmp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, tmp);
    }
}
```

**Bad example:**

```java
import java.util.List;

public class BadWildcardCapture {

    public static void swap(List<?> list, int i, int j) {
        // list.set(i, list.get(j)); // COMPILATION ERROR: cannot put into List<?>
    }
}
```

### Example 11: Apply Self-Bounded Generics for Fluent Builders

Title: Use CRTP to preserve subtype return types in chains
Description: Fluent APIs often need methods in a base class to return the most specific subtype to keep chaining type-safe. Use the Curiously Recurring Template Pattern (CRTP): `abstract class Base<T extends Base<T>>` with `protected abstract T self()` so base methods can return `T`.

**Good example:**

```java
abstract class Builder<T extends Builder<T>> {
    private String name;

    public T withName(String name) {
        this.name = name;
        return self();
    }

    protected abstract T self();
    public String build() { return name; }
}

final class UserBuilder extends Builder<UserBuilder> {
    private int age;

    public UserBuilder withAge(int age) {
        this.age = age;
        return self();
    }

    @Override protected UserBuilder self() { return this; }
}
```

**Bad example:**

```java
class BaseBuilder {
    public BaseBuilder withName(String name) { return this; }
}

class BadUserBuilder extends BaseBuilder {
    public BadUserBuilder withAge(int age) { return this; }

    static void demo() {
        BadUserBuilder b = new BadUserBuilder();
        // Chaining loses subtype after calling BaseBuilder methods
        // b.withName("a").withAge(10); // Not type-safe without casts
    }
}
```

### Example 12: Design APIs with Proper Wildcards

Title: Prefer wildcards in parameters, support comparator contravariance
Description: Place wildcards in input positions to increase flexibility: `List<? extends T>` for producers and `Comparator<? super T>` for consumers. For functional interfaces, prefer `Function<? super T, ? extends R>`. Avoid wildcards in return types which reduce usability.

**Good example:**

```java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public final class ApiDesign {
    public static <T> void sort(List<T> list, Comparator<? super T> comparator) {
        list.sort(comparator);
    }

    public static <T, R> List<R> map(List<? extends T> source,
                                     Function<? super T, ? extends R> mapper) {
        List<R> out = new ArrayList<>();
        for (T t : source) {
            out.add(mapper.apply(t));
        }
        return out;
    }
}
```

**Bad example:**

```java
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public final class RigidApi {
    public static <T> void sort(List<T> list, Comparator<T> comparator) { /* too strict */ }

    public static <T, R> List<R> map(List<T> source, Function<T, R> mapper) {
        return Collections.emptyList(); // Can't accept supertypes/subtypes easily
    }
}
```

### Example 13: Avoid Arrays Covariance Pitfalls

Title: Prefer generic collections over arrays for type safety
Description: Arrays are covariant (`Integer[]` is a subtype of `Object[]`), which allows runtime `ArrayStoreException`. Generics are invariant and prevent unsafe writes at compile time. Prefer `List<T>` with wildcards to achieve variance safely.

**Good example:**

```java
import java.util.ArrayList;
import java.util.List;

public class PreferCollectionsOverArrays {
    public static void safeVariance() {
        List<Integer> ints = new ArrayList<>();
        List<? extends Number> numbers = ints; // Read-only w.r.t element type
        // numbers.add(3.14); // COMPILATION ERROR: prevents runtime corruption
    }
}
```

**Bad example:**

```java
public class ArrayCovariancePitfall {
    public static void main(String[] args) {
        Integer[] ints = new Integer[1];
        Object[] objects = ints;      // Arrays are covariant
        objects[0] = 3.14;            // ArrayStoreException at runtime
    }
}
```

### Example 14: Serialize Collections with Type Tokens

Title: Use Jackson TypeReference or Gson TypeToken for generic types
Description: Due to type erasure, serializers need explicit type information for `List<Foo>` and similar. Provide a type token: Jackson's `TypeReference<T>` or Gson's `TypeToken<T>`. Avoid raw types and unchecked casts in (de)serialization code.

**Good example:**

```java
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class SerializationGenerics {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static List<Foo> fromJson(String json) throws Exception {
        return MAPPER.readValue(json, new TypeReference<List<Foo>>() {});
    }

    public static java.lang.reflect.Type fooListType() {
        return new TypeToken<List<Foo>>() {}.getType();
    }

    public static final class Foo { public String name; }
}
```

**Bad example:**

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class RawSerialization {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static List<Foo> fromJson(String json) throws Exception {
        List list = MAPPER.readValue(json, List.class); // Raw type loses T
        return (List<Foo>) list; // Unsafe cast, may fail at runtime
    }

    public static final class Foo { public String name; }
}
```

### Example 15: Eliminate Unchecked Warnings

Title: Fix root causes instead of suppressing warnings
Description: Unchecked warnings indicate potential heap pollution or type safety issues. Prefer eliminating them by using generics properly rather than suppressing. Use @SuppressWarnings("unchecked") only for provably safe operations and scope it as narrowly as possible (method or statement level).

**Good example:**

```java
import java.util.List;
import java.util.ArrayList;

public class NoWarningsExample {

    // Proper generics usage
    public static <T> List<T> createList(T item) {
        List<T> list = new ArrayList<>();
        list.add(item);
        return list;
    }

    // Safe operation with narrow suppression if needed
    @SuppressWarnings("unchecked") // Provably safe
    private static <T> T[] toArray(List<T> list, Class<T> type) {
        return list.toArray((T[]) java.lang.reflect.Array.newInstance(type, list.size()));
    }
}
```

**Bad example:**

```java
@SuppressWarnings("unchecked") // Broad suppression hides issues
public class WarningSuppressionAbuse {

    public static List createList(Object item) {
        List list = new ArrayList(); // Raw types cause warnings
        list.add(item);
        return list; // Unchecked conversion
    }
}
```

### Example 16: Follow Generic Type Parameter Naming Conventions

Title: Use standard conventions for better code readability
Description: Use established naming conventions for generic type parameters: `<T>` for general types, `<E>` for collection elements, `<K,V>` for map keys and values, `<?>` for unknown types (often read-only). While not mandatory, these conventions make code more readable and follow Java ecosystem standards. Use descriptive names when the purpose is specific (e.g., `<Request, Response>`).

**Good example:**

```java
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.function.Function;

// Standard conventions for better readability
public class GenericNamingConventions {

    // T for general type parameter
    public static <T> T identity(T value) {
        return value;
    }

    // E for collection element type
    public static class CustomList<E> {
        private List<E> elements;

        public void add(E element) {
            elements.add(element);
        }

        public E get(int index) {
            return elements.get(index);
        }
    }

    // K, V for map key-value pairs
    public static class Cache<K, V> {
        private Map<K, V> storage;

        public void put(K key, V value) {
            storage.put(key, value);
        }

        public V get(K key) {
            return storage.get(key);
        }
    }

    // ? for unknown types (wildcards)
    public static int size(Collection<?> collection) {
        return collection.size(); // Read-only access
    }

    public static void printAll(List<?> items) {
        for (Object item : items) {
            System.out.println(item); // Can only read as Object
        }
    }

    // Multiple type parameters with clear naming
    public static <K, V, R> Map<K, R> transformValues(
            Map<K, V> map,
            Function<V, R> transformer) {
        // Implementation
        return null;
    }

    // Descriptive names for specific contexts
    public interface ApiClient<Request, Response> {
        Response call(Request request);
    }

    public static class EventHandler<Event, Result> {
        public Result handle(Event event) {
            // Implementation
            return null;
        }
    }

    // Bounded type parameters with conventional names
    public static <T extends Comparable<T>> T max(T first, T second) {
        return first.compareTo(second) >= 0 ? first : second;
    }

    public static <E extends Enum<E>> Set<E> getAllValues(Class<E> enumClass) {
        return Set.of(enumClass.getEnumConstants());
    }
}
```

**Bad example:**

```java
import java.util.List;
import java.util.Map;
import java.util.Collection;

// Poor naming conventions make code harder to understand
public class PoorGenericNaming {

    // Unclear single letters
    public static <X> X doSomething(X value) {
        return value;
    }

    // Inconsistent naming for collections
    public static class BadList<Type> {
        private List<Type> elements;

        public void add(Type element) {
            elements.add(element);
        }
    }

    // Non-standard map parameter names
    public static class BadCache<Key, Val> {
        private Map<Key, Val> storage;
    }

    // Using concrete types instead of wildcards
    public static int getSize(Collection<Object> collection) {
        return collection.size(); // Too restrictive
    }

    // Confusing parameter names
    public static <A, B, C, D> Map<A, C> transform(
            Map<A, B> input,
            java.util.function.Function<B, C> func) {
        return null; // Hard to understand what A, B, C, D represent
    }

    // Overly verbose names
    public interface VeryLongApiClientInterface<
            VeryLongRequestParameterType,
            VeryLongResponseParameterType> {
        VeryLongResponseParameterType call(VeryLongRequestParameterType request);
    }

    // Mixed naming styles
    public static class InconsistentNaming<t, Element, KEY, value_type> {
        // Inconsistent case and style
    }
}
```

### Example 17: Use Typesafe Heterogeneous Containers

Title: Safely store multiple types using Class as key
Description: For containers needing different types (like annotations or preferences), use Class<T> as map keys and cast on retrieval. This maintains type safety without raw types or unchecked casts visible to clients.

**Good example:**

```java
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class HeterogeneousContainer {
    private final Map<Class<?>, Object> container = new HashMap<>();

    public <T> void put(Class<T> type, T instance) {
        container.put(Objects.requireNonNull(type), type.cast(instance));
    }

    public <T> T get(Class<T> type) {
        return type.cast(container.get(type));
    }
}
```

**Bad example:**

```java
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class UnsafeContainer {
    private final Map<String, Object> container = new HashMap<>();

    public void put(String key, Object instance) {
        container.put(key, instance);
    }

    public <T> T get(String key) {
        return (T) container.get(key); // Unsafe cast
    }
}
```


## Output Format

- **ANALYZE** Java code to identify specific generics usage issues and categorize them by impact (CRITICAL, MAINTAINABILITY, PERFORMANCE, TYPE_SAFETY) and area (raw types usage, wildcard misuse, bounded parameter opportunities, type erasure problems, modern feature integration gaps)
- **CATEGORIZE** generics improvements found: Type Safety Issues (raw types vs parameterized types, unsafe casts vs type-safe operations, missing bounds vs proper constraints), API Design Problems (inflexible method signatures vs PECS wildcards, verbose type declarations vs diamond operator, inheritance issues vs proper variance), Performance Concerns (unnecessary boxing vs primitive specialization, type erasure workarounds vs efficient patterns), and Modern Integration Opportunities (traditional classes vs Records with generics, old inheritance vs sealed types, verbose conditionals vs pattern matching)
- **APPLY** generics best practices directly by implementing the most appropriate improvements for each identified issue: Eliminate raw types with proper parameterization, replace unsafe casts with type-safe operations, add proper bounds and constraints, implement PECS wildcards for flexible APIs, utilize diamond operator for cleaner syntax, apply bounded parameters for better type constraints, integrate primitive specialization for performance, and modernize with Records and sealed types where beneficial
- **IMPLEMENT** comprehensive generics refactoring using proven patterns: Convert raw types to parameterized types, transform inflexible method signatures to PECS wildcards, replace verbose type declarations with diamond operator, add proper bounds to type parameters, introduce generic methods where appropriate, apply type tokens for runtime type information, integrate with functional programming patterns, and modernize inheritance hierarchies with sealed types
- **REFACTOR** code systematically following the generics improvement roadmap: First eliminate raw types and unsafe casts, then add proper bounds and constraints, apply PECS wildcards for API flexibility, utilize diamond operator and var for cleaner syntax, integrate with Records and sealed types, implement pattern matching where applicable, and optimize performance with primitive specialization
- **EXPLAIN** the applied generics improvements and their benefits: Compile-time safety enhancements through proper parameterization, runtime performance improvements via primitive specialization, API usability gains from PECS wildcards, code maintainability benefits from cleaner syntax and modern features, and backward compatibility considerations for each implemented pattern
- **VALIDATE** that all applied generics refactoring compiles successfully, maintains type safety guarantees, preserves API contracts, and achieves expected flexibility and performance benefits through comprehensive testing and verification


## Safeguards

- **BLOCKING SAFETY CHECK**: ALWAYS run `./mvnw compile` or `mvn compile` before ANY generics refactoring recommendations - compilation failure is a HARD STOP
- **CRITICAL VALIDATION**: Execute `./mvnw clean verify` or `mvn clean verify` to ensure all tests pass after applying generics improvements
- **MANDATORY VERIFICATION**: Confirm all existing functionality remains intact after generics refactoring, especially type safety guarantees and API compatibility
- **SAFETY PROTOCOL**: If ANY compilation error occurs during generics transformation, IMMEDIATELY cease recommendations and require user intervention
- **TYPE SAFETY VALIDATION**: Ensure generics improvements eliminate ClassCastException risks and maintain compile-time type checking without introducing new type safety issues
- **API COMPATIBILITY CHECK**: Validate that generics changes don't break existing client code, especially when modifying public APIs or method signatures
- **PERFORMANCE VERIFICATION**: Ensure generics refactoring doesn't introduce performance regressions, particularly with collection operations and type erasure workarounds
- **ROLLBACK REQUIREMENT**: Ensure all generics refactoring changes can be easily reverted if they introduce complexity or compatibility issues
- **INCREMENTAL SAFETY**: Apply generics improvements incrementally, validating compilation and tests after each significant type system change
- **FINAL VERIFICATION**: After completing all generics improvements, perform a final full project compilation, test run, and verification that type safety and API contracts are maintained