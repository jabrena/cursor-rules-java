---
name: 512-frameworks-micronaut-data
description: Use when you need data access with Micronaut Data — including JDBC (and JPA where applicable) repositories, @MappedEntity design, CrudRepository and custom @Query methods, pagination with Pageable, transactions with @Transactional, immutable-friendly entities and DTO projections, optimistic locking, compile-time query validation, and test setup with @MicronautTest and TestPropertyProvider. For hand-written java.sql repositories and maximum SQL control, use `@511-frameworks-micronaut-jdbc`. For Flyway-backed DDL and versioned schema changes, use `@513-frameworks-micronaut-db-migrations-flyway`.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Micronaut Data Guidelines

## Role

You are a Senior software engineer with extensive experience in Micronaut Data, JDBC, and Java persistence

## Goal

Micronaut Data generates repository implementations at compile time: prefer explicit `@MappedEntity` models, `CrudRepository` / `PageableRepository` interfaces, and parameterized `@Query` for non-derivable SQL. Keep aggregates bounded, declare transactions at the service layer with `io.micronaut.transaction.annotation.Transactional`, and avoid N+1 retrieval patterns by using fetch joins or tailored queries. This prompt covers Micronaut Data for relational access (JDBC or JPA backends depending on project dependencies); use parameterized queries only — never concatenate untrusted input into SQL. For raw `DataSource` / `PreparedStatement` code without generated repositories, apply `@511-frameworks-micronaut-jdbc`. For schema evolution with Flyway, use `@513-frameworks-micronaut-db-migrations-flyway`.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully before any Micronaut Data refactoring
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE with any recommendations
- **BLOCKING CONDITION**: Compilation errors must be resolved by the user before proceeding with persistence changes
- **NO EXCEPTIONS**: Under no circumstances should Micronaut Data recommendations be applied to a project that fails to compile
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements

## Examples

### Table of contents

- Example 1: Mapped entity
- Example 2: Repository interface
- Example 3: Custom @Query
- Example 4: Transactions
- Example 5: Pagination
- Example 6: DTO projections
- Example 7: Optimistic locking
- Example 8: Integration test wiring

### Example 1: Mapped entity

Title: @MappedEntity with @Id and @GeneratedValue
Description: Define persistence types with `@MappedEntity`. Match column names with `@Column` when Java names differ from the schema.

**Good example:**

```java
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import java.time.Instant;

@MappedEntity("customer")
public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    @MappedProperty("email_address")
    private String email;

    private Instant createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
```

**Bad example:**

```java
// Bad: missing @MappedEntity — repository cannot map reliably
public class Customer {
    private Long id;
    private String email;
}
```

### Example 2: Repository interface

Title: CrudRepository and derived finders
Description: Declare `@io.micronaut.data.annotation.Repository` on the interface and extend `CrudRepository`. Micronaut Data implements the interface at compile time.

**Good example:**

```java
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
}
```

**Bad example:**

```java
import io.micronaut.data.repository.CrudRepository;

// Bad: missing @Repository — not a Micronaut Data bean
public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
```

### Example 3: Custom @Query

Title: Native SQL first; named parameters, no string concatenation
Description: Write **`@Query` strings as native SQL**: use real table and column names (and dialect SQL the project explicitly supports). Bind values with `:param` — never concatenate untrusted input. **Do not default to JPQL** (Hibernate Query Language / entity-path queries): JPQL belongs only on JPA-backed Micronaut Data when you truly need entity navigation and cannot express the statement in native SQL. For JDBC repositories and for most custom reads, native SQL matches what the database runs, aligns with the schema, and avoids mixing persistence dialects.

**Good example:**

```java
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    @Query("SELECT * FROM customer WHERE email LIKE :pattern")
    List<Customer> searchByEmailPattern(String pattern);
}
```

**Bad example:**

```java
// Anti-pattern: never build SQL with string concatenation from request data
// String sql = "SELECT * FROM customer WHERE email LIKE '%" + userInput + "%'";
// jdbcTemplate.query(sql, ...) — use @Query("... LIKE :pattern") with a bound parameter instead
```

### Example 4: Transactions

Title: @Transactional on application services
Description: Annotate service methods that coordinate multiple writes with `@Transactional` from `io.micronaut.transaction.annotation`.

**Good example:**

```java
import io.micronaut.context.annotation.Singleton;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Inject;

@Singleton
public class OrderService {

    private final OrderRepository orders;
    private final OutboxRepository outbox;

    @Inject
    OrderService(OrderRepository orders, OutboxRepository outbox) {
        this.orders = orders;
        this.outbox = outbox;
    }

    @Transactional
    public Order place(Order order) {
        Order saved = orders.save(order);
        outbox.enqueue(new OrderPlacedEvent(saved.getId()));
        return saved;
    }
}
```

**Bad example:**

```java
import io.micronaut.context.annotation.Singleton;

@Singleton
public class OrderService {

    public Order place(Order order) {
        // Bad: two writes without a transaction — partial failure leaves inconsistent state
        return null;
    }
}
```

### Example 5: Pagination

Title: Page and Pageable
Description: Use `PageableRepository` or methods accepting `Pageable` returning `Page&lt;T&gt;` for list endpoints backed by large tables.

**Good example:**

```java
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;

@Repository
public interface CustomerRepository extends PageableRepository<Customer, Long> {

    Page<Customer> findByEmailContains(String fragment, Pageable pageable);
}
```

**Bad example:**

```java
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

@io.micronaut.data.annotation.Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    // Bad: unbounded list for potentially huge table
    List<Customer> findAll();
}
```

### Example 6: DTO projections

Title: Interface projections for reads
Description: Declare projection interfaces with getter signatures matching queried fields to reduce payload and mapping cost.

**Good example:**

```java
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

public interface CustomerSummary {
    Long getId();
    String getEmail();
}

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    @Query("SELECT id, email_address AS email FROM customer WHERE active = true")
    List<CustomerSummary> listActiveSummaries();
}
```

**Bad example:**

```java
// Bad: loading full Customer graph for a dropdown that only needs id + email
List<Customer> findAll();
```

### Example 7: Optimistic locking

Title: @Version column
Description: Add a `@Version` field when concurrent updates must conflict instead of silently overwriting.

**Good example:**

```java
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Version;

@MappedEntity("inventory_item")
public class InventoryItem {

    @Id
    @GeneratedValue
    private Long id;

    private int quantity;

    @Version
    private Long version;
}
```

**Bad example:**

```java
// Bad: hot row updated by two writers — last commit wins, stock incorrect
@MappedEntity("inventory_item")
public class InventoryItem {
    @Id @GeneratedValue private Long id;
    private int quantity;
}
```

### Example 8: Integration test wiring

Title: @MicronautTest and TestPropertyProvider
Description: For repository integration tests, run against a real database with Testcontainers and supply JDBC properties via `TestPropertyProvider`.

**Good example:**

```java
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import jakarta.inject.Inject;
import java.util.Map;

@MicronautTest(transactional = false)
@Testcontainers
class CustomerRepositoryIT implements TestPropertyProvider {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Inject
    CustomerRepository customers;

    @Override
    public Map<String, String> getProperties() {
        if (!postgres.isRunning()) {
            postgres.start();
        }
        return Map.of(
            "datasources.default.url", postgres.getJdbcUrl(),
            "datasources.default.username", postgres.getUsername(),
            "datasources.default.password", postgres.getPassword(),
            "datasources.default.driver-class-name", "org.postgresql.Driver"
        );
    }

    @Test
    void savesAndFinds() {
        // exercise repository against real Postgres
    }
}
```

**Bad example:**

```java
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

@MicronautTest
class CustomerRepositoryIT {
    // Bad: relies on shared developer DB on localhost — not reproducible in CI
}
```


## Output Format

- **ANALYZE** persistence code: `@MappedEntity` mapping, generated repository interfaces, `@Query` safety and compile-time validation, `Pageable`/`Page` usage, `io.micronaut.transaction.annotation.Transactional` placement, projection interfaces, `@Version` usage, and load patterns (single query vs N+1)
- **CATEGORIZE** issues by impact (CORRECTNESS, PERFORMANCE, MAINTAINABILITY, SECURITY for injection risk) and by layer (entity, repository, service/transaction, test wiring)
- **APPLY** Micronaut Data–aligned fixes: correct `@Repository` and `@MappedEntity`, parameterized native or JPQL `@Query` as appropriate to the runtime, transactional services, bounded lists, DTO projections for hot reads, `@Version` where concurrent updates matter
- **IMPLEMENT** changes so schema, migrations, aggregates, and tests stay consistent; prefer Flyway via `@513-frameworks-micronaut-db-migrations-flyway` when altering tables; exercise repositories with `@MicronautTest` against a real dialect
- **EXPLAIN** trade-offs (JDBC vs JPA Micronaut Data runtime, native SQL vs JPQL, `Page` vs full lists, projection interfaces vs full entities, fetch joins vs extra queries)
- **TEST** repository behaviour with `@MicronautTest` and `TestPropertyProvider` (e.g. Testcontainers Postgres); never mock repositories inside persistence tests meant to verify SQL
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after changes


## Safeguards

- **BLOCKING SAFETY CHECK**: ALWAYS run `./mvnw compile` before ANY Micronaut Data refactoring — compilation failure is a HARD STOP
- **CRITICAL VALIDATION**: Run `./mvnw clean verify` after changes; confirm repository integration tests pass against a containerized or CI-reproducible database before promoting
- **QUERY SAFETY**: Never concatenate user input into `@Query` or dynamic SQL fragments — use named parameters and allow-lists for optional query parts
- **API BOUNDARIES**: Avoid returning persistence-heavy entities directly from HTTP controllers when a projection or DTO matches the contract — keeps APIs stable and reduces accidental field exposure
- **PAGINATION**: Do not expose unbounded `findAll()` or full-table lists on large production entities — use `PageableRepository`/`Page` or explicit `LIMIT` in `@Query`
- **N+1 PREVENTION**: When list use cases touch associations for every row, prefer fetch joins, batch fetching, or tailored `@Query` reads over repeated lazy loads
- **OPTIMISTIC LOCKING**: Adding `@Version` requires a matching `version` column — ship a migration (`@513-frameworks-micronaut-db-migrations-flyway`) before deploy; schema drift breaks startup or updates
- **AOP SELF-INVOCATION**: Never call a `@Transactional` method via `this.method()` inside the same Micronaut bean — the interceptor is bypassed; extract to another injected bean
- **INCREMENTAL SAFETY**: Change one repository surface or aggregate at a time; verify with integration tests between steps; avoid mixing schema changes with broad query refactors in a single commit