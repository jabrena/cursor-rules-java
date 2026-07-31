---
name: 411-frameworks-quarkus-jdbc
description: Use when you need to write or review programmatic JDBC in Quarkus — including Agroal-backed DataSource injection, PreparedStatement with bind parameters, mapping rows to Java records, transactions (@Transactional), batch updates, SQL text blocks and upserts with domain exception translation, and optional NamedParameterJdbcTemplate when spring-jdbc is on the classpath. Prefer explicit SQL without ORM.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Quarkus JDBC — programmatic SQL

## Role

You are a Senior software engineer with extensive experience in Quarkus and JDBC data access

## Goal

Quarkus pairs JDBC drivers (`quarkus-jdbc-*`) with Agroal connection pooling. Application code should use injected `javax.sql.DataSource`, always bind parameters, map rows to immutable records or small DTOs, and declare transactions at the service boundary with `jakarta.transaction.Transactional`. Use Panache (`@412-frameworks-quarkus-panache`) when you want repository-style Hibernate access; use raw JDBC for reporting, bulk ETL, or maximum SQL control. For Flyway migrations with Quarkus, use `@413-frameworks-quarkus-db-migrations-flyway`.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully and pass basic validation checks before any JDBC refactoring
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE with any recommendations
- **BLOCKING CONDITION**: Compilation errors must be resolved by the user before proceeding with data-access changes
- **NO EXCEPTIONS**: Under no circumstances should JDBC recommendations be applied to a project that fails to compile
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements

## Examples

### Table of contents

- Example 1: Injected DataSource
- Example 2: Transactional boundary
- Example 3: Insert with generated keys
- Example 4: Row mapping to records
- Example 5: Safe single-row queries
- Example 6: SQL exception translation
- Example 7: Batch updates
- Example 8: Streaming large result sets
- Example 9: Transaction propagation types
- Example 10: Self-invocation pitfall
- Example 11: Text blocks, upserts, and domain exceptions

### Example 1: Injected DataSource

Title: Parameterized query and record mapping
Description: Inject `DataSource`, open a connection per operation (or let transaction demarcation scope it), and map `ResultSet` rows to records.

**Good example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CustomerJdbcRepository {

    private final DataSource dataSource;

    @Inject
    public CustomerJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<CustomerRow> findByStatus(String status) throws Exception {
        String sql = "SELECT id, email FROM customer WHERE status = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                List<CustomerRow> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new CustomerRow(rs.getLong("id"), rs.getString("email")));
                }
                return out;
            }
        }
    }
}

record CustomerRow(long id, String email) { }
```

**Bad example:**

```java
public List<CustomerRow> findByStatus(String status) throws Exception {
    String sql = "SELECT id, email FROM customer WHERE status = '" + status + "'";
    // SQL injection — never build SQL with string concatenation from inputs
}
```

### Example 2: Transactional boundary

Title: @Transactional on application service
Description: Place `@Transactional` on the service layer so multiple JDBC operations commit or roll back together.

**Good example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OrderService {

    private final OrderJdbcRepository orders;

    public OrderService(OrderJdbcRepository orders) {
        this.orders = orders;
    }

    @Transactional
    public void placeOrder(long customerId, String sku) {
        orders.insertOrder(customerId, sku);
        orders.insertInventoryMovement(sku, -1);
    }
}
```

**Bad example:**

```java
// Bad: each repository method opens and commits separately with no coordinating transaction
public void placeOrder(long customerId, String sku) {
    orders.insertOrder(customerId, sku);
    orders.insertInventoryMovement(sku, -1);
}
```

### Example 3: Insert with generated keys

Title: RETURNING or getGeneratedKeys
Description: Prefer database-specific `RETURNING` clauses or `Statement.RETURN_GENERATED_KEYS` for inserts; handle absence of rows explicitly.

**Good example:**

```java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

long insert(Connection c, String email) throws Exception {
    String sql = "INSERT INTO customer(email) VALUES (?)";
    try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, email);
        ps.executeUpdate();
        var keys = ps.getGeneratedKeys();
        if (!keys.next()) {
            throw new IllegalStateException("No generated key");
        }
        return keys.getLong(1);
    }
}
```

**Bad example:**

```java
// Bad: assuming last inserted id from another connection or session
long id = selectMaxIdPlusOne();
```

### Example 4: Row mapping to records

Title: Extract a private mapRow method for reuse, clarity, and null safety
Description: Extract `ResultSet`-to-record conversion into a private `mapRow(ResultSet)` method. This keeps query methods readable, centralizes null-handling and type conversion, and makes the mapping easy to test and reuse across multiple queries.

**Good example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

record OrderRow(long id, long customerId, String status, Instant createdAt) { }

@ApplicationScoped
public class OrderJdbcRepository {

    private final DataSource dataSource;

    @Inject
    public OrderJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<OrderRow> findByCustomer(long customerId) throws SQLException {
        String sql = "SELECT id, customer_id, status, created_at FROM orders WHERE customer_id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<OrderRow> rows = new ArrayList<>();
                while (rs.next()) {
                    rows.add(mapRow(rs)); // centralised, reusable
                }
                return rows;
            }
        }
    }

    private OrderRow mapRow(ResultSet rs) throws SQLException {
        return new OrderRow(
            rs.getLong("id"),
            rs.getLong("customer_id"),
            rs.getString("status"),
            rs.getTimestamp("created_at").toInstant()
        );
    }
}
```

**Bad example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class OrderJdbcRepository {

    private final DataSource dataSource;

    public OrderJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Object[]> findByCustomer(long customerId) throws SQLException {
        // Bad: Object[] loses type safety; inline mapping duplicated at every call site
        String sql = "SELECT id, customer_id, status, created_at FROM orders WHERE customer_id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Object[]> rows = new ArrayList<>();
                while (rs.next()) {
                    rows.add(new Object[]{ rs.getLong("id"), rs.getString("status") });
                }
                return rows;
            }
        }
    }
}
```

### Example 5: Safe single-row queries

Title: Wrap in Optional — always check ResultSet.next() before reading columns
Description: A query that may return zero rows must check `ResultSet.next()` and return `Optional.empty()` when absent. Calling `rs.next()` without checking the return value, or reading columns before the cursor is on a row, results in `NullPointerException` or incorrect data.

**Good example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

record CustomerRow(long id, String email, String status) { }

@ApplicationScoped
public class CustomerJdbcRepository {

    private final DataSource dataSource;

    @Inject
    public CustomerJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<CustomerRow> findById(long id) throws SQLException {
        String sql = "SELECT id, email, status FROM customer WHERE id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new CustomerRow(
                        rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("status")
                    ));
                }
                return Optional.empty();
            }
        }
    }
}
```

**Bad example:**

```java
import javax.sql.DataSource;
import java.sql.*;

public class CustomerJdbcRepository {

    private final DataSource dataSource;

    public CustomerJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public CustomerRow findById(long id) throws SQLException {
        String sql = "SELECT id, email, status FROM customer WHERE id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next(); // Bad: unchecked — throws or returns wrong data when no row exists
                return new CustomerRow(rs.getLong("id"), rs.getString("email"), rs.getString("status"));
            }
        }
    }
}

record CustomerRow(long id, String email, String status) { }
```

### Example 6: SQL exception translation

Title: Translate SQLException subtypes to domain exceptions at service boundaries
Description: Catch `SQLIntegrityConstraintViolationException` (unique constraints, FK violations) and other `SQLException` subtypes at the service layer and re-throw as domain-meaningful exceptions. Never let raw `SQLException` propagate to REST resources or callers that should not know about the database schema.

**Good example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import javax.sql.DataSource;
import java.sql.*;

@ApplicationScoped
public class AccountService {

    private final DataSource dataSource;

    @Inject
    public AccountService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Transactional
    public void register(String email) {
        String sql = "INSERT INTO accounts (email) VALUES (?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException ex) {
            throw new EmailAlreadyRegisteredException(email, ex);
        } catch (SQLException ex) {
            throw new AccountPersistenceException("Failed to register: " + email, ex);
        }
    }
}

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException(String email, Throwable cause) {
        super("Email already registered: " + email, cause);
    }
}

public class AccountPersistenceException extends RuntimeException {
    public AccountPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

**Bad example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import javax.sql.DataSource;
import java.sql.*;

@ApplicationScoped
public class AccountService {

    private final DataSource dataSource;

    public AccountService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Bad: propagates raw SQLException — callers must catch java.sql.* and
    // lose all domain context; unique constraint violation looks the same as a timeout
    @Transactional
    public void register(String email) throws SQLException {
        String sql = "INSERT INTO accounts (email) VALUES (?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        }
    }
}
```

### Example 7: Batch updates

Title: addBatch / executeBatch for bulk inserts and updates
Description: For inserting or updating many rows, use `PreparedStatement.addBatch()` to stage statements and `executeBatch()` to send them in a single round-trip. Executing one statement per loop iteration multiplies latency and overwhelms the connection pool.

**Good example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@ApplicationScoped
public class ItemBatchRepository {

    private final DataSource dataSource;

    @Inject
    public ItemBatchRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Transactional
    public void insertItems(List<String> names) throws SQLException {
        String sql = "INSERT INTO items (name) VALUES (?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (String name : names) {
                ps.setString(1, name);
                ps.addBatch();
            }
            ps.executeBatch(); // single round-trip for all rows
        }
    }
}
```

**Bad example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@ApplicationScoped
public class ItemBatchRepository {

    private final DataSource dataSource;

    public ItemBatchRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Bad: N separate round-trips — catastrophic for large lists
    @Transactional
    public void insertItems(List<String> names) throws SQLException {
        for (String name : names) {
            String sql = "INSERT INTO items (name) VALUES (?)";
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.executeUpdate();
            }
        }
    }
}
```

### Example 8: Streaming large result sets

Title: setFetchSize for server-side cursor; process rows one at a time
Description: For large exports or aggregations, set `setFetchSize()` on the `PreparedStatement` to enable a server-side cursor that streams rows in pages. Combined with `ResultSet.TYPE_FORWARD_ONLY` and `CONCUR_READ_ONLY`, this prevents loading the entire result set into heap memory before processing begins.

**Good example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;

@ApplicationScoped
public class AuditExportRepository {

    private final DataSource dataSource;

    @Inject
    public AuditExportRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Transactional
    public void exportAuditLog(PrintWriter writer) throws SQLException {
        String sql = "SELECT id, action, created_at FROM audit_log ORDER BY created_at";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql,
                 ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            ps.setFetchSize(500); // stream rows 500 at a time — no full load into heap
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    writer.printf("%d,%s,%s%n",
                        rs.getLong("id"),
                        rs.getString("action"),
                        rs.getTimestamp("created_at"));
                }
            }
        }
    }
}
```

**Bad example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

record AuditRow(long id, String action, java.sql.Timestamp createdAt) { }

@ApplicationScoped
public class AuditExportRepository {

    private final DataSource dataSource;

    public AuditExportRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Bad: loads all rows into a List before writing — OOM risk on large tables
    @Transactional
    public void exportAuditLog(PrintWriter writer) throws SQLException {
        List<AuditRow> rows = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT id, action, created_at FROM audit_log ORDER BY created_at");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new AuditRow(rs.getLong("id"), rs.getString("action"), rs.getTimestamp("created_at")));
            }
        }
        for (AuditRow r : rows) {
            writer.printf("%d,%s,%s%n", r.id(), r.action(), r.createdAt());
        }
    }
}
```

### Example 9: Transaction propagation types

Title: REQUIRED for coordinated work, REQUIRES_NEW for independent commits
Description: The default `REQUIRED` propagation joins an existing transaction or starts a new one — correct for most service calls. Use `REQUIRES_NEW` when an operation (e.g. audit logging, metrics recording) must commit independently even if the caller's transaction later rolls back. `MANDATORY` enforces that an outer transaction already exists.

**Good example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import javax.sql.DataSource;
import java.sql.*;

@ApplicationScoped
public class AuditRepository {

    private final DataSource dataSource;

    @Inject
    public AuditRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // REQUIRES_NEW: always commits independently — audit entry survives even if
    // the caller's transaction rolls back
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void logEvent(String event, long entityId) throws SQLException {
        String sql = "INSERT INTO audit_log (event, entity_id, occurred_at) VALUES (?, ?, NOW())";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, event);
            ps.setLong(2, entityId);
            ps.executeUpdate();
        }
    }
}

@ApplicationScoped
public class OrderService {

    private final DataSource dataSource;
    private final AuditRepository auditRepository;

    @Inject
    public OrderService(DataSource dataSource, AuditRepository auditRepository) {
        this.dataSource = dataSource;
        this.auditRepository = auditRepository;
    }

    // REQUIRED: joins existing transaction or starts one
    @Transactional
    public void placeOrder(long customerId, String sku) throws SQLException {
        String sql = "INSERT INTO orders (customer_id, sku) VALUES (?, ?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            ps.setString(2, sku);
            ps.executeUpdate();
        }
        auditRepository.logEvent("order-placed", customerId); // commits in its own transaction
    }
}
```

**Bad example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import javax.sql.DataSource;
import java.sql.*;

@ApplicationScoped
public class OrderService {

    private final DataSource dataSource;

    public OrderService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Bad: no @Transactional — each repository call runs in its own auto-commit connection;
    // if the inventory update fails, the order insert is already committed and cannot be rolled back
    public void placeOrder(long customerId, String sku) throws SQLException {
        insertOrder(customerId, sku);
        insertInventoryMovement(sku, -1); // failure here cannot undo insertOrder
    }

    private void insertOrder(long customerId, String sku) throws SQLException { }
    private void insertInventoryMovement(String sku, int delta) throws SQLException { }
}
```

### Example 10: Self-invocation pitfall

Title: Calling @Transactional via this.method() bypasses the CDI interceptor
Description: CDI applies `@Transactional` through an interceptor that wraps the CDI client proxy. When a method inside a bean calls another method on the same instance via `this.method()`, it bypasses the proxy entirely and the `@Transactional` annotation on the inner method has no effect. Extract the inner method to a separate CDI-managed bean to ensure proxy interception.

**Good example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import javax.sql.DataSource;
import java.sql.*;

// Separate bean — CDI proxy is intercepted correctly for every cross-bean call
@ApplicationScoped
public class AuditRepository {

    private final DataSource dataSource;

    @Inject
    public AuditRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Transactional
    public void logEvent(String event, long entityId) throws SQLException {
        String sql = "INSERT INTO audit_log (event, entity_id) VALUES (?, ?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, event);
            ps.setLong(2, entityId);
            ps.executeUpdate();
        }
    }
}

@ApplicationScoped
public class OrderService {

    private final DataSource dataSource;
    private final AuditRepository auditRepository; // injected — calls go through CDI proxy

    @Inject
    public OrderService(DataSource dataSource, AuditRepository auditRepository) {
        this.dataSource = dataSource;
        this.auditRepository = auditRepository;
    }

    @Transactional
    public void placeOrder(long customerId, String sku) throws SQLException {
        // ... insert order ...
        auditRepository.logEvent("order-placed", customerId); // proxy intercepted — @Transactional honoured
    }
}
```

**Bad example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import javax.sql.DataSource;
import java.sql.*;

@ApplicationScoped
public class OrderService {

    private final DataSource dataSource;

    public OrderService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void placeOrder(long customerId, String sku) throws SQLException {
        // ... insert order ...
        this.logAudit("order-placed", customerId); // Bad: self-invocation bypasses CDI proxy
                                                   // @Transactional on logAudit is silently ignored
    }

    @Transactional
    public void logAudit(String event, long entityId) throws SQLException {
        String sql = "INSERT INTO audit_log (event, entity_id) VALUES (?, ?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, event);
            ps.setLong(2, entityId);
            ps.executeUpdate();
        }
    }
}
```

### Example 11: Text blocks, upserts, and domain exceptions

Title: Readable multi-line SQL and fail-fast translation
Description: Use Java text blocks for multi-line SQL (PostgreSQL `ON CONFLICT`, etc.). Keep `PreparedStatement` parameters bound; translate `SQLException` to a single domain runtime type so layers above the repository stay persistence-agnostic. Structured debug logging belongs at appropriate levels — avoid logging secrets or full row payloads at INFO.

**Good example:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class GreekGodRepository {

    private static final Logger LOG = LoggerFactory.getLogger(GreekGodRepository.class);

    private static final String SELECT_ORDERED = "SELECT name FROM greek_god ORDER BY name";
    private static final String UPSERT = """
            INSERT INTO greek_god (name) VALUES (?)
            ON CONFLICT (name) DO NOTHING
            """;

    private final DataSource dataSource;

    @Inject
    public GreekGodRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<String> findAllNamesOrdered() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_ORDERED);
             ResultSet rs = ps.executeQuery()) {
            List<String> names = new ArrayList<>();
            while (rs.next()) {
                names.add(rs.getString(1));
            }
            LOG.debug("Loaded {} Greek god names from database", names.size());
            return names;
        } catch (SQLException e) {
            throw new GreekGodsDataAccessException("Failed to load Greek god names", e);
        }
    }

    public void upsertByName(String name) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(UPSERT)) {
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new GreekGodsDataAccessException("Failed to upsert Greek god: " + name, e);
        }
    }
}

public class GreekGodsDataAccessException extends RuntimeException {
    public GreekGodsDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

**Bad example:**

```java
// Bad: concatenated multi-line SQL without bind params; swallows or logs-and-ignores SQLException
public void upsertByName(String name) {
    String sql = "INSERT INTO greek_god (name) VALUES ('" + name + "') ON CONFLICT (name) DO NOTHING";
    // ...
}
```


## Output Format

- **ANALYZE** JDBC code for SQL injection risk, parameter binding style, try-with-resources coverage, transaction boundaries, missing `Optional` on single-row queries, exception translation, batch opportunities, and streaming gaps for large result sets
- **CATEGORIZE** findings by impact (SECURITY for injection risk, CORRECTNESS for missing transactions or unsafe single-row access, PERFORMANCE for N+1 / missing batch / missing fetch-size streaming, MAINTAINABILITY for inline mapping)
- **APPLY** improvements: introduce parameter binding, extract `mapRow` methods, wrap single-row queries in `Optional`, add `@Transactional` at service boundaries, add batch operations, set `setFetchSize` for large exports, translate `SQLException` subtypes to domain exceptions
- **HANDLE** exceptions: translate `SQLIntegrityConstraintViolationException` and other `SQLException` subtypes at the service layer; wrap in meaningful domain exceptions preserving the cause
- **TEST** with `@QuarkusTest` and Dev Services for realistic database coverage; avoid mocking `DataSource` or `Connection` — use real schema with test profiles
- **RECOMMEND** Panache (`@412-frameworks-quarkus-panache`) when CRUD and entity-state management dominate; keep raw JDBC for ad-hoc SQL, reporting, and bulk ETL
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after substantive edits


## Safeguards

- **BLOCKING SAFETY CHECK**: ALWAYS run `./mvnw compile` or `mvn compile` before ANY JDBC refactoring — compilation failure is a HARD STOP
- **SQL INJECTION**: Never build SQL by concatenating untrusted input — use `PreparedStatement` bind parameters exclusively
- **CONNECTION LEAKS**: Every `Connection`, `PreparedStatement`, and `ResultSet` must be closed — always use try-with-resources
- **TRANSACTION BOUNDARIES**: Multi-step writes must share a `@Transactional` boundary at the service layer; never rely on auto-commit for coordinated operations
- **SINGLE-ROW SAFETY**: Always check `ResultSet.next()` before reading columns; return `Optional.empty()` when no row is found rather than `null` or throwing
- **EXCEPTION TRANSLATION**: Translate `SQLIntegrityConstraintViolationException` and other `SQLException` subtypes to domain exceptions at service boundaries; preserve the original cause for diagnostics
- **BATCH SAFETY**: Wrap batch inserts/updates in `@Transactional`; call `executeBatch()` inside the same try-with-resources block as `prepareStatement()`
- **STREAMING**: A server-side cursor via `setFetchSize()` requires the `ResultSet` to remain open while rows are processed — do not close the connection until streaming is complete
- **SELF-INVOCATION**: Never call a `@Transactional` method via `this.method()` within the same bean — the CDI interceptor is bypassed; extract to a separate CDI-managed bean
- **INCREMENTAL SAFETY**: Change data-access code in small steps, covered by `@QuarkusTest` integration tests using Dev Services; do not rely on `./mvnw compile` alone to verify correctness