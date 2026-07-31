---
name: 312-frameworks-spring-data-jdbc
description: Use when you need to use Spring Data JDBC with Java records — including entity design with records, repository pattern, immutable updates, aggregate relationships, custom queries, transaction management, and avoiding N+1 problems. For programmatic JDBC (`JdbcTemplate`, `NamedParameterJdbcTemplate`), hand-written SQL, and maximum control without repository abstraction, use `@311-frameworks-spring-jdbc`. For Flyway-backed DDL and versioned schema changes, use `@313-frameworks-spring-db-migrations-flyway`.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Spring Data JDBC with Records

## Role

You are a Senior software engineer with extensive experience in Spring Data and Java persistence

## Goal

Spring Data JDBC maps rows to domain types with minimal magic: one repository call typically loads a whole aggregate in predictable SQL. Java records fit this model because they are immutable, constructor-friendly, and explicit. Success means correct `@Column`/`@Id` mapping, repositories that express intent through naming or `@Query`, small aggregates with `Set` children or foreign keys—not JPA-style graphs—and transactions declared at the service layer. For programmatic JDBC, reporting, or batch SQL without Spring Data repositories, use `@311-frameworks-spring-jdbc` instead of forcing everything through repositories.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully and pass basic validation checks before any Spring Data JDBC refactoring
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE with any recommendations
- **BLOCKING CONDITION**: Compilation errors must be resolved by the user before proceeding with persistence changes
- **NO EXCEPTIONS**: Under no circumstances should data-access recommendations be applied to a project that fails to compile
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements

## Examples

### Table of contents

- Example 1: Records as JDBC entities
- Example 2: Repository interfaces
- Example 3: Updates with immutable records
- Example 4: Aggregate relationships
- Example 5: Custom @Query usage
- Example 6: Transaction boundaries
- Example 7: Single-query aggregate loading
- Example 8: @Table and @Embedded mapping annotations
- Example 9: INSERT vs UPDATE: how save() decides
- Example 10: @Version for optimistic locking
- Example 11: DTO projections for read-only queries
- Example 12: @DataJdbcTest slice tests
- Example 13: Transaction propagation
- Example 14: Self-invocation pitfall

### Example 1: Records as JDBC entities

Title: Map columns explicitly; use @PersistenceCreator when multiple constructors exist
Description: Prefer records for thread-safe, concise persistence types. Use `@Column` when names differ from properties. If you add extra constructors, mark the persistence-facing one with `@PersistenceCreator`. Use factories like `of(...)` for new rows before insert.

**Good example:**

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import java.time.LocalDateTime;

public record Customer(
    @Id
    @Column("customer_id")
    Long id,
    @Column("first_name")
    String firstName,
    @Column("last_name")
    String lastName,
    @Column("email_address")
    String email,
    @Column("created_at")
    LocalDateTime createdAt
) {
    @PersistenceCreator
    public Customer(Long id, String firstName, String lastName, String email, LocalDateTime createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.createdAt = createdAt;
    }

    public static Customer of(String firstName, String lastName, String email) {
        return new Customer(null, firstName, lastName, email, LocalDateTime.now());
    }
}
```

**Bad example:**

```java
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

// Multiple constructors without @PersistenceCreator — mapping is ambiguous
public record Customer(
    @Id Long id,
    String firstName,
    String lastName,
    String email,
    LocalDateTime createdAt
) {
    public Customer(Long id, String firstName, String lastName, String email, LocalDateTime createdAt) {
        this(id, firstName, lastName, email, createdAt);
    }

    public Customer(String firstName, String lastName, String email) {
        this(null, firstName, lastName, email, LocalDateTime.now());
    }
}

// Mutable entity: boilerplate and accidental mutation
public class CustomerEntity {
    @Id
    private Long id;
    private String email;
    // getters/setters — easy to leave entity in inconsistent state
}
```

### Example 2: Repository interfaces

Title: Extend ListCrudRepository (preferred over CrudRepository); derive methods or use @Query with parameters
Description: Annotate interfaces with `@Repository`, extend `ListCrudRepository` (preferred—returns `List<T>` instead of `Iterable<T>`) or `PagingAndSortingRepository`, use query derivation names that match property paths, and bind `@Query` parameters with `@Param`—never hardcode volatile literals for user data.

**Good example:**

```java
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends ListCrudRepository<Customer, Long> {

    List<Customer> findByLastName(String lastName);

    Optional<Customer> findByEmail(String email);

    @Query("SELECT * FROM customer WHERE email LIKE :pattern")
    List<Customer> findByEmailPattern(@Param("pattern") String pattern);
}
```

**Bad example:**

```java
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

// Missing @Repository; non-idiomatic names; unsafe literal in SQL;
// CrudRepository returns Iterable<T> — prefer ListCrudRepository for List<T>
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    List<Customer> getCustomersWithLastName(String lastName);

    @Query("SELECT * FROM customer WHERE email LIKE '%@gmail.com%'")
    List<Customer> findGmailUsers();
}
```

### Example 3: Updates with immutable records

Title: Return new instances via with* methods; save in a transaction
Description: Records cannot be mutated in place. Expose `withEmail`, `withName`, or similar methods that copy fields and replace what changed, then `save` the new instance inside a transactional service.

**Good example:**

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

public record Customer(
    @Id @Column("customer_id") Long id,
    @Column("first_name") String firstName,
    @Column("last_name") String lastName,
    @Column("email_address") String email,
    @Column("created_at") LocalDateTime createdAt
) {
    public Customer withEmail(String newEmail) {
        return new Customer(id, firstName, lastName, newEmail, createdAt);
    }
}

@Service
class CustomerService {

    private final CustomerRepository customerRepository;

    CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    Customer updateCustomerEmail(Long customerId, String newEmail) {
        return customerRepository.findById(customerId)
            .map(c -> c.withEmail(newEmail))
            .map(customerRepository::save)
            .orElseThrow(() -> new IllegalArgumentException("customer: " + customerId));
    }
}

interface CustomerRepository extends org.springframework.data.repository.ListCrudRepository<Customer, Long> {
    java.util.Optional<Customer> findById(Long id);
}
```

**Bad example:**

```java
import org.springframework.transaction.annotation.Transactional;

// Attempting to "mutate" a record is invalid — no setters on components
// Anti-pattern: partial re-construction that drops fields or bypasses with* helpers
@Transactional
class CustomerService {

    Customer updateEmail(Long id, String email, CustomerRepository repo) {
        Customer c = repo.findById(id).orElseThrow();
        return repo.save(new Customer(c.id(), email)); // wrong arity / lost fields if record has more components
    }
}

record Customer(Long id, String firstName, String lastName, String email, java.time.LocalDateTime createdAt) {
    Customer(Long id, String email) {
        this(id, null, null, email, null);
    }
}
interface CustomerRepository extends org.springframework.data.repository.CrudRepository<Customer, Long> {
    java.util.Optional<Customer> findById(Long id);
}
```

### Example 4: Aggregate relationships

Title: Sets inside the root; FK ids across aggregates; avoid bidirectional graphs
Description: Model one-to-many as `Set<Child>` on the aggregate root loaded with the root. Use scalar foreign keys (`Long customerId`) for references to other aggregates. Many-to-many: junction entity or denormalization—not `Set<Course>` on `Student` and `Set<Student>` on `Course`. Do not expose separate repositories for every line item if the line is part of the order aggregate.

**Good example:**

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record Order(
    @Id @Column("order_id") Long id,
    @Column("customer_id") Long customerId,
    @Column("order_date") LocalDateTime orderDate,
    Set<OrderItem> items
) {}

public record OrderItem(
    @Id @Column("item_id") Long id,
    @Column("product_name") String productName,
    @Column("price") BigDecimal price,
    @Column("quantity") int quantity
) {}

// Junction-style link entity for many-to-many style data
public record Enrollment(
    @Id @Column("enrollment_id") Long id,
    @Column("student_id") Long studentId,
    @Column("course_id") Long courseId,
    @Column("enrolled_at") LocalDateTime enrolledAt
) {}
```

**Bad example:**

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import java.util.Set;

// Full object graph / bidirectional — breaks aggregate boundaries for JDBC
public record Order(
    @Id @Column("order_id") Long id,
    Customer customer,
    Set<OrderItem> items
) {}

public record OrderItem(
    @Id @Column("item_id") Long id,
    String productName,
    Order order
) {}

// Direct many-to-many sets — not supported like JPA
public record Student(@Id Long id, String name, Set<Course> courses) {}
public record Course(@Id Long id, String title, Set<Student> students) {}

// Child repository for entities that belong to Order aggregate
interface OrderItemRepository extends org.springframework.data.repository.CrudRepository<OrderItem, Long> {
    java.util.List<OrderItem> findByOrderId(Long orderId);
}
```

### Example 5: Custom @Query usage

Title: Parameterized SQL; @Modifying for updates
Description: Use text-block or string SQL with `@Param` bindings. For updates/deletes, add `@Modifying` where required. Split oversized multi-join reports into views, separate reads, or bounded DTO queries instead of `List<Object[]>`.

**Good example:**

```java
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerRepository extends ListCrudRepository<Customer, Long> {

    @Query("""
        SELECT c.* FROM customer c
        JOIN orders o ON c.id = o.customer_id
        WHERE o.order_date BETWEEN :startDate AND :endDate
        GROUP BY c.id
        HAVING COUNT(o.id) >= :minOrders
        """)
    List<Customer> findActiveCustomers(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("minOrders") int minOrders
    );

    @Modifying
    @Query("UPDATE customer SET email = :email WHERE id = :id")
    void updateCustomerEmail(@Param("id") Long id, @Param("email") String email);
}
```

**Bad example:**

```java
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    // Never build SQL by concatenating untrusted input
    @Query("SELECT * FROM customer WHERE email = :email")
    Customer findByEmailUnsafe(@org.springframework.data.repository.query.Param("email") String email);

    // Over-wide join returning Object[] — hard to maintain
    @Query("""
        SELECT c.*, o.*, oi.* FROM customer c
        LEFT JOIN orders o ON c.id = o.customer_id
        LEFT JOIN order_item oi ON o.id = oi.order_id
        WHERE c.created_at > :d
        """)
    List<Object[]> findEverything(@org.springframework.data.repository.query.Param("d") LocalDateTime date);
}
```

### Example 6: Transaction boundaries

Title: @Transactional on services; readOnly for queries
Description: Declare transactions at the service layer. Use `readOnly = true` on classes or methods that only read. Avoid relying on implicit per-call auto-commit for multi-step writes.

**Good example:**

```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
class CustomerService {

    private final CustomerRepository customerRepository;

    CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Transactional
    Customer createCustomer(String firstName, String lastName, String email) {
        return customerRepository.save(Customer.of(firstName, lastName, email));
    }

    @Transactional
    Customer updateCustomerEmail(Long customerId, String newEmail) {
        return customerRepository.findById(customerId)
            .map(c -> c.withEmail(newEmail))
            .map(customerRepository::save)
            .orElseThrow();
    }
}
```

**Bad example:**

```java
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

// No class-level transaction; each repository call may commit independently
class CustomerService {

    private final CustomerRepository customerRepository;

    Customer createCustomer(String firstName, String lastName, String email) {
        return customerRepository.save(Customer.of(firstName, lastName, email));
    }

    // Read path marked @Transactional without readOnly — misses optimization hint
    @Transactional
    Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
}
```

### Example 7: Single-query aggregate loading

Title: Prefer Spring Data JDBC aggregate load over JPA lazy N+1 or manual fan-out
Description: Loading an aggregate root typically runs one SQL statement with joins for its collection. Iterate `order.items()` without expecting extra lazy queries (unlike JPA `LAZY`). Design aggregates so that payload size stays acceptable; avoid pulling huge graphs in one root.

**Good example:**

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record Order(
    @Id @Column("order_id") Long id,
    @Column("customer_id") Long customerId,
    @Column("order_date") LocalDateTime orderDate,
    @Column("total_amount") BigDecimal totalAmount,
    Set<OrderItem> items
) {}

public record OrderItem(
    @Id @Column("item_id") Long id,
    @Column("unit_price") BigDecimal unitPrice,
    @Column("quantity") int quantity
) {}

@Service
@Transactional(readOnly = true)
class OrderService {

    private final OrderRepository orderRepository;

    OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    List<OrderSummary> getCustomerOrderSummaries(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
            .map(order -> new OrderSummary(
                order.id(),
                order.orderDate(),
                order.totalAmount(),
                order.items().size(),
                order.items().stream()
                    .mapToDouble(i -> i.unitPrice().doubleValue() * i.quantity())
                    .sum()
            ))
            .toList();
    }
}

record OrderSummary(Long orderId, LocalDateTime orderDate, BigDecimal totalAmount, int itemCount, double lineTotal) {}

interface OrderRepository extends org.springframework.data.repository.ListCrudRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
}
```

**Bad example:**

```java
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Manual two-step load and map — error-prone when JDBC already loads the aggregate
@Service
class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    List<OrderSummary> getCustomerOrderSummaries(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        List<Long> ids = orders.stream().map(Order::id).toList();
        List<OrderItem> allItems = orderItemRepository.findByOrderIdIn(ids);
        Map<Long, List<OrderItem>> byOrder = allItems.stream()
            .collect(Collectors.groupingBy(OrderItem::orderId));
        return orders.stream()
            .map(o -> new OrderSummary(o.id(), o.orderDate(), o.totalAmount(), byOrder.getOrDefault(o.id(), List.of()).size(), 0))
            .toList();
    }
}

record Order(Long id, java.time.LocalDateTime orderDate, java.math.BigDecimal totalAmount) {}

record OrderItem(Long id, Long orderId, java.math.BigDecimal unitPrice, int quantity) {}

interface OrderRepository extends org.springframework.data.repository.CrudRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
}

interface OrderItemRepository {
    List<OrderItem> findByOrderIdIn(List<Long> orderIds);
}

record OrderSummary(Long orderId, java.time.LocalDateTime orderDate, java.math.BigDecimal totalAmount, int itemCount, double lineTotal) {}

// JPA-style mental model: lazy collections causing N+1 if misapplied
// @Entity class Order { @jakarta.persistence.OneToMany(fetch = jakarta.persistence.FetchType.LAZY) Set<OrderItem> items; }
```

### Example 8: @Table and @Embedded mapping annotations

Title: Name the table explicitly; inline value objects with @Embedded
Description: Use `@Table` to declare the exact SQL table name when it does not match the record name (e.g. `Customer` vs `customers`). Use `@Embedded(onEmpty = USE_NULL)` to inline a value object's columns into the parent table, keeping a rich object model while the schema stays flat. Value objects embedded this way have no independent lifecycle and need no separate repository.

**Good example:**

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

@Table("customers")
public record Customer(
    @Id @Column("customer_id") Long id,
    @Column("first_name") String firstName,
    @Column("last_name") String lastName,
    @Column("email_address") String email,
    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    Address address
) {
    public static Customer of(String firstName, String lastName, String email, Address address) {
        return new Customer(null, firstName, lastName, email, address);
    }
}

// Columns street, city, postal_code live in the customers table — no join needed
public record Address(
    @Column("street") String street,
    @Column("city") String city,
    @Column("postal_code") String postalCode
) {}
```

**Bad example:**

```java
import org.springframework.data.annotation.Id;

// Missing @Table — Spring Data JDBC derives "customer" from class name;
// breaks silently when the actual table is "customers"
public record Customer(
    @Id Long id,
    String firstName,
    String email,
    // Storing address as a JSON blob loses SQL filterability
    String addressJson
) {}

// Separate aggregate for Address when it has no independent lifecycle — creates
// needless foreign-key join and an extra repository in the wrong aggregate boundary
public record Address(@Id Long id, Long customerId, String street, String city) {}
interface AddressRepository extends org.springframework.data.repository.CrudRepository<Address, Long> {}
```

### Example 9: INSERT vs UPDATE: how save() decides

Title: Null @Id triggers INSERT; non-null @Id triggers UPDATE — use factory methods to stay explicit
Description: Spring Data JDBC checks `isNew()`: if `@Id` is `null` it issues INSERT and returns the saved record with the database-generated id; if `@Id` is non-null it issues UPDATE. Use a static factory (`of(...)`) that leaves id as `null` for new rows. Always use `with*` helpers to carry the existing id when updating so no fields are accidentally dropped.

**Good example:**

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Table("products")
public record Product(
    @Id Long id,
    String name,
    BigDecimal price
) {
    public static Product of(String name, BigDecimal price) {
        return new Product(null, name, price);  // null id → INSERT
    }

    public Product withPrice(BigDecimal newPrice) {
        return new Product(id, name, newPrice);  // same id → UPDATE
    }
}

@Service
class ProductService {
    private final ProductRepository repository;

    ProductService(ProductRepository repository) { this.repository = repository; }

    @Transactional
    Product create(String name, BigDecimal price) {
        Product saved = repository.save(Product.of(name, price));
        // saved.id() now holds the database-generated value
        return saved;
    }

    @Transactional
    Product updatePrice(Long id, BigDecimal newPrice) {
        return repository.findById(id)
            .map(p -> p.withPrice(newPrice))
            .map(repository::save)
            .orElseThrow();
    }
}

interface ProductRepository extends org.springframework.data.repository.ListCrudRepository<Product, Long> {}
```

**Bad example:**

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Table("products")
public record Product(@Id Long id, String name, BigDecimal price) {}

@Service
class ProductService {
    private final ProductRepository repository;

    ProductService(ProductRepository repository) { this.repository = repository; }

    @Transactional
    Product create(String name, BigDecimal price) {
        // Hardcoded id=1L triggers UPDATE if a row with id=1 already exists; silent data corruption
        return repository.save(new Product(1L, name, price));
    }

    @Transactional
    Product updatePrice(Long id, BigDecimal newPrice) {
        // Drops the name field; causes data loss or a NOT NULL constraint violation
        return repository.save(new Product(id, null, newPrice));
    }
}

interface ProductRepository extends org.springframework.data.repository.CrudRepository<Product, Long> {}
```

### Example 10: @Version for optimistic locking

Title: Prevent lost updates under concurrency by adding a version field to the aggregate root
Description: Add `@Version Long version` to the aggregate root record. Spring Data JDBC increments it on every `save` and includes a `WHERE version = ?` clause in the UPDATE. If another transaction has already committed a newer version, an `OptimisticLockingFailureException` is thrown, preventing silent lost updates without database-level locking.

**Good example:**

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Table("inventory_items")
public record InventoryItem(
    @Id @Column("item_id") Long id,
    String sku,
    int quantity,
    @Version Long version
) {
    public static InventoryItem of(String sku, int quantity) {
        return new InventoryItem(null, sku, quantity, null);
    }

    public InventoryItem deduct(int amount) {
        if (amount > quantity) throw new IllegalArgumentException("Insufficient stock for: " + sku);
        return new InventoryItem(id, sku, quantity - amount, version);
    }
}

@Service
class InventoryService {
    private final InventoryItemRepository repository;

    InventoryService(InventoryItemRepository repository) { this.repository = repository; }

    @Transactional
    InventoryItem deductStock(Long itemId, int amount) {
        return repository.findById(itemId)
            .map(item -> item.deduct(amount))
            .map(repository::save)  // throws OptimisticLockingFailureException on stale version
            .orElseThrow();
    }
}

interface InventoryItemRepository extends org.springframework.data.repository.ListCrudRepository<InventoryItem, Long> {}
```

**Bad example:**

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// No @Version — two concurrent threads read quantity=10, both deduct 8,
// both save quantity=2; net stock should be -6 but nothing throws
@Table("inventory_items")
public record InventoryItem(@Id Long id, String sku, int quantity) {}

@Service
class InventoryService {
    private final InventoryItemRepository repository;

    InventoryService(InventoryItemRepository repository) { this.repository = repository; }

    @Transactional
    InventoryItem deductStock(Long itemId, int amount) {
        InventoryItem item = repository.findById(itemId).orElseThrow();
        return repository.save(new InventoryItem(item.id(), item.sku(), item.quantity() - amount));
    }
}

interface InventoryItemRepository extends org.springframework.data.repository.CrudRepository<InventoryItem, Long> {}
```

### Example 11: DTO projections for read-only queries

Title: Map complex query results to a record DTO via rowMapperClass instead of Object[]
Description: When a query joins multiple tables or aggregates computed columns, map results to a dedicated DTO record using `rowMapperClass` on `@Query`. This avoids the fragile `Object[]` anti-pattern and keeps the aggregate root free of reporting-only fields. The DTO record lives in the read/query side; it is never saved.

**Good example:**

```java
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

// DTO record — separate from the aggregate root; read-only, never saved
public record CustomerOrderSummary(
    Long customerId,
    String customerName,
    long orderCount,
    BigDecimal totalSpent
) {}

@Repository
public interface CustomerRepository extends ListCrudRepository<Customer, Long> {

    @Query(value = """
        SELECT c.customer_id,
               c.first_name || ' ' || c.last_name AS customer_name,
               COUNT(o.order_id)                   AS order_count,
               COALESCE(SUM(o.total_amount), 0)    AS total_spent
        FROM customers c
        LEFT JOIN orders o ON c.customer_id = o.customer_id
        WHERE c.created_at >= :since
        GROUP BY c.customer_id, c.first_name, c.last_name
        ORDER BY total_spent DESC
        """,
        rowMapperClass = CustomerOrderSummaryMapper.class)
    List<CustomerOrderSummary> findTopCustomerSummaries(@Param("since") LocalDate since);
}

class CustomerOrderSummaryMapper implements RowMapper<CustomerOrderSummary> {
    @Override
    public CustomerOrderSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CustomerOrderSummary(
            rs.getLong("customer_id"),
            rs.getString("customer_name"),
            rs.getLong("order_count"),
            rs.getBigDecimal("total_spent")
        );
    }
}
```

**Bad example:**

```java
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

// Object[] — column order is an implicit contract; breaks silently when the query changes
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    @Query("""
        SELECT c.customer_id, c.first_name || ' ' || c.last_name,
               COUNT(o.order_id), COALESCE(SUM(o.total_amount), 0)
        FROM customers c
        LEFT JOIN orders o ON c.customer_id = o.customer_id
        WHERE c.created_at >= :since
        GROUP BY c.customer_id, c.first_name, c.last_name
        """)
    List<Object[]> findTopCustomers(@Param("since") LocalDate since);
    // caller: (Long) row[0], (String) row[1] — fragile position-based access
}
```

### Example 12: @DataJdbcTest slice tests

Title: Test repositories in isolation — no web layer, no service beans
Description: Use `@DataJdbcTest` to spin up only the JDBC slice: the `DataSource`, `JdbcTemplate`, and Spring Data JDBC repositories. Seed fixtures with `@Sql`. Never mock the repository inside this test — the entire point is to exercise real SQL against an embedded database, verifying derived queries, `@Query` statements, and aggregate loading.

**Good example:**

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.jdbc.Sql;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Sql("/sql/customer-test-data.sql")
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void findByEmail_returnsCustomer_whenEmailExists() {
        Optional<Customer> result = customerRepository.findByEmail("alice@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().firstName()).isEqualTo("Alice");
    }

    @Test
    void save_generatesId_forNewRecord() {
        Customer saved = customerRepository.save(Customer.of("Bob", "Smith", "bob@example.com"));

        assertThat(saved.id()).isNotNull();
    }

    @Test
    void findByLastName_returnsAllMatches() {
        List<Customer> customers = customerRepository.findByLastName("Smith");

        assertThat(customers).hasSize(2);
    }
}
```

**Bad example:**

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// @SpringBootTest loads the full context — web layer, security, services — far too heavy for a repository test
@SpringBootTest
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void findsNothing_becauseMockIsUsed() {
        // Anti-pattern: mocking the very repository being tested adds no value;
        // it only verifies Mockito wiring, not SQL correctness
        CustomerRepository mockRepo = mock(CustomerRepository.class);
        when(mockRepo.findByEmail("x@example.com")).thenReturn(java.util.Optional.empty());
        assertThat(mockRepo.findByEmail("x@example.com")).isEmpty();
    }
}
```

### Example 13: Transaction propagation

Title: REQUIRES_NEW for independent audit writes; MANDATORY to enforce caller contract
Description: The default propagation `REQUIRED` joins an existing transaction or starts a new one. Use `REQUIRES_NEW` when an operation must commit independently — such as audit logging — so it persists even if the outer transaction rolls back. Use `MANDATORY` to assert that a transaction must already exist, failing fast when called without one.

**Good example:**

```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public record AuditLog(@org.springframework.data.annotation.Id Long id, String action, long entityId) {
    static AuditLog of(String action, long entityId) { return new AuditLog(null, action, entityId); }
}

interface AuditLogRepository extends org.springframework.data.repository.ListCrudRepository<AuditLog, Long> {}

@Service
class AuditService {

    private final AuditLogRepository auditLogRepository;

    AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // REQUIRES_NEW: always commits audit entry in its own transaction,
    // even if the caller's transaction is rolled back
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String action, long entityId) {
        auditLogRepository.save(AuditLog.of(action, entityId));
    }
}

@Service
@Transactional(readOnly = true)
class OrderService {

    private final OrderRepository orderRepository;
    private final AuditService auditService;

    OrderService(OrderRepository orderRepository, AuditService auditService) {
        this.orderRepository = orderRepository;
        this.auditService = auditService;
    }

    @Transactional
    void cancelOrder(Long orderId) {
        orderRepository.findById(orderId)
            .map(Order::cancel)
            .map(orderRepository::save)
            .orElseThrow();
        auditService.record("order-cancelled", orderId); // commits in its own independent tx
    }
}
```

**Bad example:**

```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class AuditService {

    private final AuditLogRepository auditLogRepository;

    AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // Bad: default REQUIRED joins the caller's transaction —
    // if cancelOrder rolls back, the audit entry is also lost
    @Transactional
    void record(String action, long entityId) {
        auditLogRepository.save(AuditLog.of(action, entityId));
    }
}
```

### Example 14: Self-invocation pitfall

Title: Calling @Transactional from within the same bean bypasses the proxy
Description: Spring applies `@Transactional` through a proxy. Calling a `@Transactional` method via `this.method()` within the same bean bypasses the proxy — no new transaction is opened and no propagation rule is applied. Extract the method to a separate Spring-managed bean to guarantee proxy interception.

**Good example:**

```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Separate bean — proxy is intercepted correctly
@Service
class CustomerEmailService {

    private final CustomerRepository customerRepository;

    CustomerEmailService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public void updateEmail(Long customerId, String newEmail) {
        customerRepository.findById(customerId)
            .map(c -> c.withEmail(newEmail))
            .map(customerRepository::save)
            .orElseThrow();
    }
}

@Service
@Transactional(readOnly = true)
class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerEmailService customerEmailService;

    CustomerService(CustomerRepository customerRepository,
                    CustomerEmailService customerEmailService) {
        this.customerRepository = customerRepository;
        this.customerEmailService = customerEmailService;
    }

    @Transactional
    void onboardCustomer(String firstName, String lastName, String email) {
        Customer c = customerRepository.save(Customer.of(firstName, lastName, email));
        customerEmailService.updateEmail(c.id(), email); // proxy intercepted — correct
    }
}
```

**Bad example:**

```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class CustomerService {

    private final CustomerRepository customerRepository;

    CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    void onboardCustomer(String firstName, String lastName, String email) {
        Customer c = customerRepository.save(Customer.of(firstName, lastName, email));
        this.updateEmail(c.id(), email); // Bad: self-invocation — proxy bypassed, no transaction opened
    }

    @Transactional
    void updateEmail(Long customerId, String newEmail) {
        customerRepository.findById(customerId)
            .map(cu -> cu.withEmail(newEmail))
            .map(customerRepository::save)
            .orElseThrow();
    }
}
```


## Output Format

- **ANALYZE** persistence code: record/entity mapping, repository APIs, aggregate shape, `@Query` safety, transaction placement, and load patterns (single query vs extra round-trips)
- **CATEGORIZE** issues by impact (CORRECTNESS, PERFORMANCE, MAINTAINABILITY) and by layer (entity mapping, repository, service/transaction, aggregate design)
- **APPLY** Spring Data JDBC–aligned fixes: add `@Column`/`@Id`, narrow repositories, introduce `with*` updates, reshape aggregates and FKs, parameterize SQL, move `@Transactional` to services with `readOnly` where fit
- **IMPLEMENT** changes so schema, aggregates, and tests stay consistent; prefer Flyway migrations (`@313-frameworks-spring-db-migrations-flyway`) and integration tests for repository behavior
- **EXPLAIN** trade-offs (aggregate size vs query size, explicit SQL vs derived queries, `@Embedded` vs FK reference)
- **TEST** repository behavior with `@DataJdbcTest` slices seeded via `@Sql`; never mock repositories inside persistence tests
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after changes


## Safeguards

- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` or `mvn compile` before ANY persistence refactoring
- **CRITICAL VALIDATION**: Run `./mvnw clean verify` after changes; exercise repository integration tests
- **DATA SAFETY**: Review DDL and migration impact before changing aggregate boundaries or `@Query` updates
- **SQL INJECTION**: Never concatenate user input into `@Query` strings; use parameters
- **INCREMENTAL SAFETY**: Change one aggregate or repository surface at a time when possible
- **SAFETY PROTOCOL**: Stop if compilation or data tests fail after edits
- **ROLLBACK RULES**: Default `@Transactional` does not roll back on checked exceptions — declare `rollbackFor` explicitly when checked exceptions must abort the transaction
- **PROPAGATION**: Use `Propagation.REQUIRES_NEW` for operations that must commit independently (e.g. audit logs) regardless of the outer transaction outcome
- **SELF-INVOCATION**: Never call a `@Transactional` method via `this.method()` inside the same bean — the Spring proxy is bypassed; extract to a separate Spring-managed bean