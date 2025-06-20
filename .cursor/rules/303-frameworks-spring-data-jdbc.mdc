---
description: 
globs: 
alwaysApply: false
---
# Spring Data JDBC with Records

Spring Data JDBC provides a simpler alternative to JPA, offering direct SQL control while maintaining Spring's repository abstractions. When combined with Java records, it creates clean, immutable data models perfect for modern Java applications.

## Implementing These Principles

These guidelines are built upon the following core principles:

- **Immutability**: Use records for immutable entities that are thread-safe and predictable
- **Simplicity**: Leverage Spring Data JDBC's straightforward approach over complex ORM mapping
- **Constructor Injection**: Always use constructor-based dependency injection for better testability
- **Transaction Boundaries**: Keep transactions at the service layer, not repository layer
- **SQL Control**: Use custom queries when needed for optimal performance

## Table of contents

- Rule 1: Use Records for Entity Classes
- Rule 2: Implement Repository Pattern Correctly
- Rule 3: Handle Updates with Immutable Records
- Rule 4: Design Aggregate Relationships Properly
- Rule 5: Use Custom Queries for Complex Operations
- Rule 6: Implement Proper Transaction Management
- Rule 7: Embrace Single Query Loading to Eliminate N+1 Problems

## Rule 1: Use Records for Entity Classes

Title: Prefer Records Over Classes for Entity Definitions
Description: Records provide immutability, automatic equals/hashCode, and clean constructor-based mapping that works perfectly with Spring Data JDBC. They eliminate boilerplate code and ensure thread safety. Use @PersistenceCreator when you have multiple constructors to specify which one Spring Data JDBC should use. Use @Column to explicitly map record fields to database columns, especially when field names differ from column names.

**Good example:**

```java
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
    // Constructor for Spring Data JDBC (explicit annotation when multiple constructors exist)
    @PersistenceCreator
    public Customer(Long id, String firstName, String lastName, String email, LocalDateTime createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.createdAt = createdAt;
    }
    
    // Factory method for new entities
    public static Customer of(String firstName, String lastName, String email) {
        return new Customer(null, firstName, lastName, email, LocalDateTime.now());
    }
}
```

**Bad Example:**

```java
// Missing @PersistenceCreator annotation with multiple constructors
public record Customer(
    @Id Long id,
    String firstName,
    String lastName,
    String email,
    LocalDateTime createdAt
) {
    // Multiple constructors without @PersistenceCreator - Spring Data JDBC won't know which to use
    public Customer(Long id, String firstName, String lastName, String email, LocalDateTime createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.createdAt = createdAt;
    }
    
    public Customer(String firstName, String lastName, String email) {
        this(null, firstName, lastName, email, LocalDateTime.now());
    }
}

// Or using mutable entity class with boilerplate
public class Customer {
    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime createdAt;
    
    // Constructors, getters, setters, equals, hashCode...
    // 50+ lines of boilerplate code
}
```

## Rule 2: Implement Repository Pattern Correctly

Title: Extend Appropriate Repository Interfaces
Description: Use CrudRepository or PagingAndSortingRepository as base interfaces. Leverage method query derivation for simple queries and @Query for complex ones. Always annotate with @Repository.

**Good example:**

```java
@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    
    // Method query derivation
    List<Customer> findByLastName(String lastName);
    Optional<Customer> findByEmail(String email);
    
    // Custom query for complex operations
    @Query("SELECT * FROM customer WHERE email LIKE :pattern")
    List<Customer> findByEmailPattern(@Param("pattern") String pattern);
}
```

**Bad Example:**

```java
// Missing @Repository annotation and poor method naming
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    
    // Unclear method names that don't follow Spring Data conventions
    List<Customer> getCustomersWithLastName(String lastName);
    
    // Raw SQL without parameters
    @Query("SELECT * FROM customer WHERE email LIKE '%@gmail.com%'")
    List<Customer> findGmailUsers();
}
```

## Rule 3: Handle Updates with Immutable Records

Title: Create New Record Instances for Updates
Description: Since records are immutable, create update methods that return new instances with modified values. This ensures data integrity and prevents accidental mutations.

**Good example:**

```java
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
    // Update method returns new instance
    public Customer withEmail(String newEmail) {
        return new Customer(id, firstName, lastName, newEmail, createdAt);
    }
    
    public Customer withName(String firstName, String lastName) {
        return new Customer(id, firstName, lastName, email, createdAt);
    }
}

// Service layer update
@Transactional
public Customer updateCustomerEmail(Long customerId, String newEmail) {
    return customerRepository.findById(customerId)
        .map(customer -> customer.withEmail(newEmail))
        .map(customerRepository::save)
        .orElseThrow(() -> new CustomerNotFoundException(customerId));
}
```

**Bad Example:**

```java
// Trying to use setters with records (won't compile)
public record Customer(@Id Long id, String email) {
    public void setEmail(String email) {  // This won't work!
        this.email = email;
    }
}

// Or using mutable wrapper approach
@Transactional
public Customer updateCustomerEmail(Long customerId, String newEmail) {
    Customer customer = customerRepository.findById(customerId).orElseThrow();
    // Creating entirely new record instead of using update methods
    Customer updated = new Customer(customer.id(), newEmail);
    return customerRepository.save(updated);
}
```

## Rule 4: Design Aggregate Relationships Properly

Title: Model Aggregates with Records and Sets
Description: Spring Data JDBC supports limited relationship types compared to JPA. Use records for aggregate roots and contained entities. Model one-to-many relationships with Set collections, use foreign key references for many-to-one, and avoid bidirectional references to maintain aggregate boundaries. For unsupported relationships like many-to-many, use junction tables or denormalization.

**Good example:**

```java
// ✅ One-to-Many: Primary supported relationship
public record Order(
    @Id 
    @Column("order_id")
    Long id,
    
    @Column("customer_id")
    Long customerId,  // Foreign key reference (Many-to-One)
    
    @Column("order_date")
    LocalDateTime orderDate,
    
    @Column("order_status")
    OrderStatus status,
    
    Set<OrderItem> items  // One-to-Many aggregate collection
) {}

public record OrderItem(
    @Id 
    @Column("item_id")
    Long id,
    
    @Column("product_name")
    String productName,
    
    @Column("price")
    BigDecimal price,
    
    @Column("quantity")
    int quantity
) {}

// ✅ One-to-One: Using embedded objects
public record Customer(
    @Id 
    @Column("customer_id")
    Long id,
    
    @Column("customer_name")
    String name,
    
    @Embedded.OnEmpty(USE_NULL) Address address  // One-to-One embedded
) {}

public record Address(
    @Column("street_address")
    String street,
    
    @Column("city")
    String city,
    
    @Column("postal_code")
    String postalCode,
    
    @Column("country")
    String country
) {}

// ✅ Many-to-Many workaround: Junction table with explicit entity
public record Student(
    @Id 
    @Column("student_id")
    Long id,
    
    @Column("student_name")
    String name,
    
    Set<StudentCourse> enrollments  // Access courses through junction
) {}

public record StudentCourse(
    @Id 
    @Column("enrollment_id")
    Long id,
    
    @Column("student_id")
    Long studentId,
    
    @Column("course_id")
    Long courseId,
    
    @Column("enrolled_at")
    LocalDateTime enrolledAt,
    
    @Column("grade")
    String grade
) {}

// ✅ Many-to-Many alternative: Store as delimited string or JSON
public record User(
    @Id 
    @Column("user_id")
    Long id,
    
    @Column("username")
    String username,
    
    @Column("role_ids")
    String roleIds  // Store as "1,2,3" - simple cases only
) {
    public List<Long> getRoleIdsList() {
        return Arrays.stream(roleIds.split(","))
            .map(Long::parseLong)
            .toList();
    }
}

// Repository focuses on aggregate root only
@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByStatus(OrderStatus status);
}
```

**Bad Example:**

```java
// ❌ Bidirectional references break aggregate boundaries
public record Order(
    @Id 
    @Column("order_id")
    Long id,
    
    Customer customer,  // Don't embed full objects - use foreign keys
    Set<OrderItem> items
) {}

public record OrderItem(
    @Id 
    @Column("item_id")
    Long id,
    
    @Column("product_name")
    String productName,
    
    Order order  // Don't include parent reference in aggregate
) {}

// ❌ Attempting unsupported Many-to-Many directly
public record Student(
    @Id 
    @Column("student_id")
    Long id,
    
    @Column("student_name")
    String name,
    
    Set<Course> courses  // Spring Data JDBC doesn't support this
) {}

public record Course(
    @Id 
    @Column("course_id")
    Long id,
    
    @Column("course_title")
    String title,
    
    Set<Student> students  // Bidirectional Many-to-Many not supported
) {}

// ❌ Repository that violates aggregate boundaries
@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);  // Should go through Order aggregate
}

// ❌ Overly large aggregates
public record Customer(
    @Id 
    @Column("customer_id")
    Long id,
    
    @Column("customer_name")
    String name,
    
    Set<Order> orders,           // Too large - creates performance issues
    Set<Address> addresses,
    Set<PaymentMethod> paymentMethods,
    Set<Preference> preferences
) {}
```

### Relationship Modeling Guidelines

**Supported Relationships:**
- **One-to-Many**: Use `Set<ChildEntity>` in aggregate root (primary pattern)
- **One-to-One**: Use `@Embedded` for value objects or foreign key references
- **Many-to-One**: Use foreign key fields (`Long parentId`)

**Unsupported Relationships:**
- **Many-to-Many**: Use junction tables or denormalization workarounds
- **Bidirectional**: Always model relationships unidirectionally

**Best Practices:**
- Keep aggregates small and focused
- Use foreign key references between aggregates
- Load related data separately when needed
- Consider eventual consistency for cross-aggregate operations

### Summary of Relationship Capabilities:

| Relationship Type | Support Level | Approach |
|------------------|---------------|----------|
| **One-to-Many** | ✅ Full | Collections in aggregate root |
| **One-to-One** | ✅ Good | Embedded objects |
| **Many-to-One** | ⚠️ Limited | Foreign key references |
| **Many-to-Many** | ❌ None | Junction tables or denormalization |

## Rule 5: Use Custom Queries for Complex Operations

Title: Leverage @Query for Complex SQL Operations
Description: Use @Query annotation for complex queries that can't be expressed through method naming. Use proper parameter binding and consider performance implications.

**Good example:**

```java
@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    
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

**Bad Example:**

```java
@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    
    // SQL injection risk - not using parameters
    @Query("SELECT * FROM customer WHERE email = '" + "email" + "'")
    Customer findByEmailUnsafe(String email);
    
    // Overly complex query that should be broken down
    @Query("""
        SELECT c.*, o.*, oi.*, p.* FROM customer c
        LEFT JOIN orders o ON c.id = o.customer_id
        LEFT JOIN order_item oi ON o.id = oi.order_id
        LEFT JOIN product p ON oi.product_id = p.id
        WHERE c.created_at > ?1 AND o.status = ?2
        """)
    List<Object[]> findComplexCustomerData(LocalDateTime date, String status);
}
```

## Rule 6: Implement Proper Transaction Management

Title: Use @Transactional at Service Layer
Description: Apply transaction boundaries at the service layer, not repository layer. Use readOnly=true for read operations and ensure proper transaction propagation.

**Good example:**

```java
@Service
@Transactional(readOnly = true)
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
    
    @Transactional
    public Customer createCustomer(String firstName, String lastName, String email) {
        var customer = Customer.of(firstName, lastName, email);
        return customerRepository.save(customer);
    }
    
    @Transactional
    public Customer updateCustomerEmail(Long customerId, String newEmail) {
        return customerRepository.findById(customerId)
            .map(customer -> customer.withEmail(newEmail))
            .map(customerRepository::save)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }
}
```

**Bad Example:**

```java
// No transaction management
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    // Auto-commit for each operation - no transaction control
    public Customer createCustomer(String firstName, String lastName, String email) {
        var customer = Customer.of(firstName, lastName, email);
        return customerRepository.save(customer);  // Each call is separate transaction
    }
    
    // Read operation without readOnly optimization
    @Transactional
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);  // Should be readOnly=true
    }
}
```

## Rule 7: Embrace Single Query Loading to Eliminate N+1 Problems

Title: Leverage Spring Data JDBC's Eager Loading to Avoid N+1 Query Issues
Description: Spring Data JDBC loads entire aggregates in single queries, automatically eliminating the N+1 problem that plagues JPA/Hibernate applications. Unlike JPA's lazy loading approach, Spring Data JDBC eagerly loads all aggregate data in one query, ensuring predictable performance and eliminating the need for complex fetch strategies.

**Good example:**

```java
// ✅ Spring Data JDBC loads entire aggregate in single query
public record Order(
    @Id 
    @Column("order_id")
    Long id,
    
    @Column("customer_id")
    Long customerId,
    
    @Column("order_date")
    LocalDateTime orderDate,
    
    @Column("total_amount")
    BigDecimal totalAmount,
    
    Set<OrderItem> items  // All items loaded in single query
) {}

public record OrderItem(
    @Id 
    @Column("item_id")
    Long id,
    
    @Column("product_name")
    String productName,
    
    @Column("unit_price")
    BigDecimal unitPrice,
    
    @Column("quantity")
    int quantity
) {}

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
}

// ✅ Service that benefits from single query loading
@Service
@Transactional(readOnly = true)
public class OrderService {
    
    private final OrderRepository orderRepository;
    
    public List<OrderSummary> getCustomerOrderSummaries(Long customerId) {
        // Single query loads all orders with their items
        return orderRepository.findByCustomerId(customerId)
            .stream()
            .map(order -> new OrderSummary(
                order.id(),
                order.orderDate(),
                order.totalAmount(),
                order.items().size(),  // No additional query needed
                order.items().stream()
                    .mapToDouble(item -> item.unitPrice().doubleValue() * item.quantity())
                    .sum()
            ))
            .toList();
    }
}

public record OrderSummary(
    Long orderId,
    LocalDateTime orderDate,
    BigDecimal totalAmount,
    int itemCount,
    double calculatedTotal
) {}

// Generated SQL - Single query with JOIN:
// SELECT o.order_id, o.customer_id, o.order_date, o.total_amount,
//        oi.item_id, oi.product_name, oi.unit_price, oi.quantity
// FROM orders o 
// LEFT JOIN order_item oi ON o.order_id = oi.order_id
// WHERE o.customer_id = ?
```

**Bad Example:**

```java
// ❌ JPA-style thinking that would cause N+1 problems
@Entity  // Wrong - this is JPA, not Spring Data JDBC
public class Order {
    @Id
    private Long id;
    
    @OneToMany(fetch = FetchType.LAZY)  // Lazy loading causes N+1
    private Set<OrderItem> items;
    
    // getters/setters...
}

// ❌ Code that would trigger N+1 in JPA (but works fine in Spring Data JDBC)
@Service
public class OrderService {
    
    public List<OrderSummary> getCustomerOrderSummaries(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        
        return orders.stream()
            .map(order -> new OrderSummary(
                order.getId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getItems().size(),  // In JPA: N+1 query here!
                order.getItems().stream()  // In JPA: Additional queries!
                    .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                    .sum()
            ))
            .toList();
    }
}

// JPA would generate N+1 queries:
// 1. SELECT * FROM orders WHERE customer_id = ?
// 2. SELECT * FROM order_item WHERE order_id = 1  -- For each order
// 3. SELECT * FROM order_item WHERE order_id = 2
// 4. SELECT * FROM order_item WHERE order_id = 3
// ... N additional queries for N orders

// ❌ Trying to manually optimize with separate queries
@Service
public class OrderService {
    
    public List<OrderSummary> getCustomerOrderSummaries(Long customerId) {
        // Manually loading in separate steps - unnecessary complexity
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        
        List<Long> orderIds = orders.stream()
            .map(Order::id)
            .toList();
            
        // Additional repository method needed
        List<OrderItem> allItems = orderItemRepository.findByOrderIdIn(orderIds);
        
        // Complex manual mapping required
        Map<Long, List<OrderItem>> itemsByOrder = allItems.stream()
            .collect(Collectors.groupingBy(OrderItem::orderId));
            
        // Error-prone manual association
        return orders.stream()
            .map(order -> {
                List<OrderItem> orderItems = itemsByOrder.getOrDefault(order.id(), List.of());
                return new OrderSummary(/* complex mapping */);
            })
            .toList();
    }
}
```

### Key Benefits of Spring Data JDBC's Approach

**Eliminates N+1 Problems:**
- Entire aggregates loaded in single query with JOINs
- No lazy loading means no surprise additional queries
- Predictable query patterns and performance

**Simplified Development:**
- No need for `@EntityGraph` or fetch strategies
- No need to worry about Hibernate session management
- No proxy objects or lazy initialization exceptions

**Performance Transparency:**
- What you see is what you get - one query per repository call
- Easy to predict and optimize database access patterns
- No hidden queries triggered by accessing collections

**Trade-offs to Consider:**
- Larger initial queries (but often more efficient overall)
- Cannot selectively load parts of aggregates
- May load more data than needed in some scenarios (design aggregates carefully)






