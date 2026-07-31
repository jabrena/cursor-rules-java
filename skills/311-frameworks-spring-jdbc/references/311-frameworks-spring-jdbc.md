---
name: 311-frameworks-spring-jdbc
description: Use when you need to write or review programmatic JDBC with Spring — including JdbcClient (Spring Framework 7+) as the default API, JdbcTemplate only where batch/streaming APIs require JdbcOperations, NamedParameterJdbcTemplate for legacy named-param code, parameterized SQL, RowMapper mapping to records, batch operations, transactions, safe handling of generated keys, DataAccessException handling, read-only transactions, streaming large result sets, and @JdbcTest slice testing.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Spring JDBC — JdbcClient (Spring Framework 7+)

## Role

You are a Senior software engineer with extensive experience in Spring Framework and JDBC data access

## Goal

Prefer `JdbcClient` (Spring Framework 7+) for new and refactored code: fluent SQL, indexed or named parameters, typed `query(Class)`, and `optional()` / `single()` for single-row reads. It is built on `JdbcOperations` and participates in Spring transactions like `JdbcTemplate`. Use `JdbcTemplate` (or `NamedParameterJdbcTemplate`) only when you need APIs not covered by `JdbcClient` — notably `batchUpdate`, `KeyHolder` inserts, `RowCallbackHandler` / `ResultSetExtractor` streaming — or when maintaining legacy code. Prefer explicit SQL with bind parameters, map rows to immutable records or small DTOs, keep transactions at the service layer, and let Spring translate SQL exceptions to `DataAccessException`. Choose Spring Data JDBC (`@312-frameworks-spring-data-jdbc`) when repositories and aggregate mapping fit; use `JdbcClient` (or `JdbcTemplate` for batch/streaming) for ad-hoc SQL, reporting, or tight control over statements. For schema evolution with Flyway, use `@313-frameworks-spring-db-migrations-flyway`.

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

- Example 1: Parameterized queries with JdbcClient
- Example 2: Named parameters with JdbcClient
- Example 3: JdbcClient fluent API
- Example 4: Migrate from JdbcTemplate to JdbcClient
- Example 5: Row mapping and records
- Example 6: Transactions and JDBC
- Example 7: Batch updates and generated keys
- Example 8: Safe single-row queries
- Example 9: DataAccessException hierarchy and handling
- Example 10: Read-only transactions for query paths
- Example 11: Streaming large result sets
- Example 12: Testing with @JdbcTest
- Example 13: Rollback rules
- Example 14: Self-invocation pitfall
- Example 15: Programmatic transactions with TransactionTemplate

### Example 1: Parameterized queries with JdbcClient

Title: Bind arguments; never concatenate user input into SQL
Description: Use `JdbcClient.sql(...).param(...)` for positional `?` placeholders, then `query` with a row mapper or mapped type, or `update` for writes. This keeps plans cacheable and prevents SQL injection. Spring Boot 4+ registers a `JdbcClient` bean; otherwise use `JdbcClient.create(dataSource)`.

**Good example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;
import java.util.List;

class UserRepository {

    private final JdbcClient jdbcClient;

    UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    List<String> findEmailsByStatus(String status) {
        return jdbcClient.sql("SELECT email FROM users WHERE status = ?")
            .param(status)
            .query((rs, rowNum) -> rs.getString("email"))
            .list();
    }

    int updateStatus(long userId, String newStatus) {
        return jdbcClient.sql("UPDATE users SET status = ? WHERE id = ?")
            .params(newStatus, userId)
            .update();
    }
}
```

**Bad example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;
import java.util.List;

class UnsafeUserRepository {

    private final JdbcClient jdbcClient;

    UnsafeUserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    List<String> findByStatus(String status) {
        // Bad: concatenation — SQL injection and no stable execution plan
        return jdbcClient.sql("SELECT email FROM users WHERE status = '" + status + "'")
            .query((rs, rowNum) -> rs.getString("email"))
            .list();
    }
}
```

### Example 2: Named parameters with JdbcClient

Title: `:name` placeholders with `.param(name, value)` — legacy code may use NamedParameterJdbcTemplate
Description: Use `:name` placeholders in SQL, then chain `.param("name", value)` on `JdbcClient`. Prefer this over long positional `?` lists. Existing code using `NamedParameterJdbcTemplate` + `MapSqlParameterSource` can migrate to the same SQL and bindings on `JdbcClient` (see the migration example).

**Good example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;
import java.util.List;

class OrderRepository {

    private final JdbcClient jdbcClient;

    OrderRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    List<Long> findIdsByCustomerAndState(long customerId, String state) {
        return jdbcClient.sql(
                "SELECT id FROM orders WHERE customer_id = :customerId AND state = :state")
            .param("customerId", customerId)
            .param("state", state)
            .query((rs, rowNum) -> rs.getLong("id"))
            .list();
    }
}
```

**Bad example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;
import java.util.List;

class OrderRepository {

    private final JdbcClient jdbcClient;

    List<Long> findIds(long customerId, String state) {
        // Bad: untrusted fragments in SQL — use bound parameters only
        return jdbcClient.sql(
                "SELECT id FROM orders WHERE customer_id = " + customerId + " AND state = ?")
            .param(state)
            .query((rs, rowNum) -> rs.getLong("id"))
            .list();
    }
}
```

### Example 3: JdbcClient fluent API

Title: Spring Framework 7+ chainable SQL, named or indexed params, and typed results
Description: Inject `JdbcClient` (typically built from `DataSource` or `JdbcTemplate`). Use `sql(String)`, then `.param(value)` for indexed params or `.param(name, value)` for named placeholders (`:name`), then `query` with a row mapper or mapped type, or `update` for writes. Prefer `single()` / `optional()` when at most one row is expected.

**Good example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;
import java.util.List;
import java.util.Optional;

class ProductRepository {

    private final JdbcClient jdbcClient;

    ProductRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // Indexed positional params
    Optional<String> findNameById(long id) {
        return jdbcClient.sql("SELECT name FROM products WHERE id = ?")
            .param(id)
            .query(String.class)
            .optional();
    }

    List<ProductRow> findActive() {
        return jdbcClient.sql("SELECT id, name FROM products WHERE active = ?")
            .param(true)
            .query(ProductRow.class)
            .list();
    }

    // Named params — improves readability for multiple bindings
    List<ProductRow> findByCategoryAndMinPrice(String category, java.math.BigDecimal minPrice) {
        return jdbcClient.sql("""
                SELECT id, name FROM products
                WHERE category = :category AND price >= :minPrice
                """)
            .param("category", category)
            .param("minPrice", minPrice)
            .query(ProductRow.class)
            .list();
    }

    int deactivate(long id) {
        return jdbcClient.sql("UPDATE products SET active = false WHERE id = ?")
            .param(id)
            .update();
    }
}

record ProductRow(long id, String name) {}
```

**Bad example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;

class ProductRepository {

    private final JdbcClient jdbcClient;

    String findNameUnsafe(long id) {
        // Bad: SQL built by concatenation — injection risk and no plan caching
        return jdbcClient.sql("SELECT name FROM products WHERE id = " + id)
            .query(String.class)
            .single();
    }

    String findNameMissingOptional(long id) {
        // Bad: single() throws IncorrectResultSizeDataAccessException when 0 rows found
        return jdbcClient.sql("SELECT name FROM products WHERE id = ?")
            .param(id)
            .query(String.class)
            .single();
    }
}
```

### Example 4: Migrate from JdbcTemplate to JdbcClient

Title: Same SQL and parameters; inject `JdbcClient` and use the fluent API
Description: Replace `JdbcTemplate` / `NamedParameterJdbcTemplate` with `JdbcClient` when on Spring Framework 7+ (Spring Boot 4+). Inject `JdbcClient` or build it with `JdbcClient.create(jdbcTemplate)` during a gradual migration. Map `query` → `sql(...).param(...).query(...).list()` (or `.query(Class).list()`), `queryForObject` for a single row → `query(...).optional()` or `query(Class).single()`, `update` → `sql(...).param(...).update()`. Keep `JdbcTemplate` only for `batchUpdate`, `KeyHolder` inserts, or `RowCallbackHandler` until you refactor those call sites.

**Good example:**

```java
// Before: JdbcTemplate
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import java.util.List;
import java.util.Optional;

class LegacyUserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate named;

    LegacyUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate named) {
        this.jdbcTemplate = jdbcTemplate;
        this.named = named;
    }

    List<String> findEmailsByStatus(String status) {
        return jdbcTemplate.query(
            "SELECT email FROM users WHERE status = ?",
            (rs, rowNum) -> rs.getString("email"),
            status
        );
    }

    Optional<Long> findIdByEmail(String email) {
        var params = new MapSqlParameterSource("email", email);
        List<Long> rows = named.query(
            "SELECT id FROM users WHERE email = :email",
            params,
            (rs, rowNum) -> rs.getLong("id")
        );
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    int updateStatus(long userId, String newStatus) {
        return jdbcTemplate.update(
            "UPDATE users SET status = ? WHERE id = ?",
            newStatus, userId
        );
    }
}
```

**Bad example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;

class UserRepository {

    private final JdbcClient jdbcClient;

    UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // Bad: mixing JdbcTemplate-style queryForObject mental model — use optional() for 0..1 rows
    Long findIdByEmail(String email) {
        return jdbcClient.sql("SELECT id FROM users WHERE email = ?")
            .param(email)
            .query(Long.class)
            .single(); // throws if no row — often wrong for lookups by natural key
    }
}
```

### Example 5: Row mapping and records

Title: `JdbcClient.query(Class)` or `DataClassRowMapper` with explicit `RowMapper`
Description: Map `ResultSet` columns to records or immutable types. Prefer `jdbcClient.sql(...).query(YourRecord.class).list()` for records whose constructor matches column names (or `@ConstructorProperties`). Use `DataClassRowMapper` with `JdbcTemplate` only if you have not migrated that call site yet; otherwise use `JdbcClient` with an explicit `RowMapper` when names or conversions need control.

**Good example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;
import java.util.List;

record CustomerRow(long id, String email, String status) {}

class CustomerQuery {

    private final JdbcClient jdbcClient;

    CustomerQuery(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    List<CustomerRow> findAll() {
        return jdbcClient.sql("SELECT id, email, status FROM customers")
            .query(CustomerRow.class)
            .list();
    }
}
```

**Bad example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;
import java.util.List;

class CustomerQuery {

    private final JdbcClient jdbcClient;

    List<Object[]> findAllRaw() {
        return jdbcClient.sql("SELECT id, email, status FROM customers")
            .query((rs, rowNum) -> new Object[] {
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("status")
            })
            .list();
    }
    // Bad: untyped Object[] — use a record or DTO mapping instead
}
```

### Example 6: Transactions and JDBC

Title: Declare boundaries on services — `JdbcClient` joins the current transaction
Description: Wrap multi-statement workflows in `@Transactional` on the service. `JdbcClient` participates in the current Spring transaction the same way `JdbcTemplate` does (it delegates to `JdbcOperations`).

**Good example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class TransferService {

    private final JdbcClient jdbcClient;

    TransferService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Transactional
    void transfer(long fromId, long toId, long amount) {
        jdbcClient.sql("UPDATE accounts SET balance = balance - ? WHERE id = ?")
            .params(amount, fromId)
            .update();
        jdbcClient.sql("UPDATE accounts SET balance = balance + ? WHERE id = ?")
            .params(amount, toId)
            .update();
    }
}
```

**Bad example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;

class TransferDao {

    private final JdbcClient jdbcClient;

    void debit(long id, long amount) {
        jdbcClient.sql("UPDATE accounts SET balance = balance - ? WHERE id = ?")
            .params(amount, id).update();
    }

    void credit(long id, long amount) {
        jdbcClient.sql("UPDATE accounts SET balance = balance + ? WHERE id = ?")
            .params(amount, id).update();
    }
    // Bad: callers can forget to wrap debit+credit in one transaction — boundary belongs on service
}
```

### Example 7: Batch updates and generated keys

Title: batchUpdate for bulk writes; KeyHolder for inserts; List<Object[]> shorthand
Description: `JdbcClient` does not replace batch and generated-key APIs yet — use `JdbcTemplate` (or `JdbcOperations`) for `batchUpdate` with `BatchPreparedStatementSetter` or `List<Object[]>` shorthand, and `KeyHolder` / `GeneratedKeyHolder` for inserts that return keys. Inject `JdbcTemplate` alongside `JdbcClient` for those call sites, or obtain operations from the same `DataSource`.

**Good example:**

```java
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

class ItemWriter {

    private final JdbcTemplate jdbcTemplate;

    ItemWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Shorthand: List<Object[]> — one row per array element
    int[] batchInsertSimple(List<String> names) {
        List<Object[]> args = names.stream()
            .map(n -> new Object[]{n})
            .toList();
        return jdbcTemplate.batchUpdate("INSERT INTO items (name) VALUES (?)", args);
    }

    // Full control: BatchPreparedStatementSetter for type-specific binding
    int[] batchInsert(List<String> names) {
        return jdbcTemplate.batchUpdate(
            "INSERT INTO items (name) VALUES (?)",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws java.sql.SQLException {
                    ps.setString(1, names.get(i));
                }

                @Override
                public int getBatchSize() {
                    return names.size();
                }
            }
        );
    }

    long insertReturningId(String name) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO items (name) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, name);
            return ps;
        }, keyHolder);
        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : -1L;
    }
}
```

**Bad example:**

```java
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;

class ItemWriter {

    private final JdbcTemplate jdbcTemplate;

    void insertLoop(List<String> names) {
        for (String name : names) {
            jdbcTemplate.update("INSERT INTO items (name) VALUES (?)", name);
        }
    }
    // Bad: N round-trips — use batchUpdate for large batches

    void insertNoKeys(String name) {
        jdbcTemplate.update("INSERT INTO items (name) VALUES (?)", name);
        // Bad: if caller needs the new id, fetch by unique key or use KeyHolder
    }
}
```

### Example 8: Safe single-row queries

Title: Prefer `optional()` — avoid `queryForObject` for entity lookups when zero rows are possible
Description: Legacy `JdbcTemplate.queryForObject` throws `EmptyResultDataAccessException` when it finds zero rows and `IncorrectResultSizeDataAccessException` when it finds more than one. Prefer `JdbcClient.query(...).optional()` for 0..1 rows. Use `single()` only when exactly one row is guaranteed (or when you want an exception if not). For aggregates that always return one row (e.g. `COUNT(*)`), `queryForObject` on `JdbcTemplate` or a typed `query(...).single()` remains appropriate.

**Good example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;
import java.util.Optional;

record UserRow(long id, String email) {}

class UserRepository {

    private final JdbcClient jdbcClient;

    UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    Optional<UserRow> findByEmail(String email) {
        return jdbcClient.sql("SELECT id, email FROM users WHERE email = ?")
            .param(email)
            .query(UserRow.class)
            .optional();
    }

    Optional<String> findEmailById(long id) {
        return jdbcClient.sql("SELECT email FROM users WHERE id = ?")
            .param(id)
            .query((rs, rowNum) -> rs.getString("email"))
            .optional();
    }
}
```

**Bad example:**

```java
import org.springframework.jdbc.core.JdbcTemplate;

class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    // Bad: throws EmptyResultDataAccessException when no user found
    String findEmailById(long id) {
        return jdbcTemplate.queryForObject(
            "SELECT email FROM users WHERE id = ?",
            String.class,
            id
        );
    }

    // Bad: caller has no safe way to distinguish "not found" from runtime error
    Long countByStatus(String status) {
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE status = ?",
            Long.class,
            status
        );
        // queryForObject is fine for aggregate functions that always return one row;
        // but for entity lookups, prefer JdbcClient optional()
    }
}
```

### Example 9: DataAccessException hierarchy and handling

Title: Catch specific subtypes at service boundaries; translate to domain exceptions
Description: Spring maps all SQL exceptions to `DataAccessException` subtypes. Catch specific subtypes — `DuplicateKeyException` for unique constraint violations, `DataIntegrityViolationException` for FK or check failures — at the service layer and translate them into meaningful domain exceptions. Never let raw `DataAccessException` propagate to controllers or clients.

**Good example:**

```java
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class AccountService {

    private final JdbcClient jdbcClient;

    AccountService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Transactional
    void register(String email) {
        try {
            jdbcClient.sql("INSERT INTO accounts (email) VALUES (?)")
                .param(email)
                .update();
        } catch (DuplicateKeyException ex) {
            throw new EmailAlreadyRegisteredException(email, ex);
        } catch (DataIntegrityViolationException ex) {
            throw new AccountCreationException("Constraint violated for: " + email, ex);
        }
    }
}

class EmailAlreadyRegisteredException extends RuntimeException {
    EmailAlreadyRegisteredException(String email, Throwable cause) {
        super("Email already registered: " + email, cause);
    }
}

class AccountCreationException extends RuntimeException {
    AccountCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

**Bad example:**

```java
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import java.sql.SQLException;

@Service
class AccountService {

    private final JdbcClient jdbcClient;

    void register(String email) {
        // Bad: catching raw DataAccessException loses specific context
        try {
            jdbcClient.sql("INSERT INTO accounts (email) VALUES (?)")
                .param(email)
                .update();
        } catch (DataAccessException ex) {
            throw new RuntimeException("DB error", ex);
        }
    }

    void registerUnchecked(String email) {
        // Bad: catching raw SQLException bypasses Spring's exception translation
        try {
            jdbcClient.sql("INSERT INTO accounts (email) VALUES (?)").param(email).update();
        } catch (Exception ex) {
            // Swallowing the exception — callers never know it failed
        }
    }
}
```

### Example 10: Read-only transactions for query paths

Title: @Transactional(readOnly = true) at class level; override for writes
Description: Apply `@Transactional(readOnly = true)` as the class-level default for services that are predominantly query-oriented. Override with plain `@Transactional` on individual write methods. This allows the connection pool and underlying driver to skip dirty-tracking and may enable read replicas or optimistic locking shortcuts.

**Good example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

record ProductRow(long id, String name, boolean active) {}

@Service
@Transactional(readOnly = true)
class ProductService {

    private final JdbcClient jdbcClient;

    ProductService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    List<ProductRow> findActive() {
        return jdbcClient.sql("SELECT id, name, active FROM products WHERE active = true")
            .query(ProductRow.class)
            .list();
    }

    Optional<ProductRow> findById(long id) {
        return jdbcClient.sql("SELECT id, name, active FROM products WHERE id = ?")
            .param(id)
            .query(ProductRow.class)
            .optional();
    }

    @Transactional
    void deactivate(long id) {
        jdbcClient.sql("UPDATE products SET active = false WHERE id = ?")
            .param(id)
            .update();
    }
}
```

**Bad example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

record ProductRow(long id, String name, boolean active) {}

@Service
class ProductService {

    private final JdbcClient jdbcClient;

    // Bad: no @Transactional at all — reads run in auto-commit mode,
    // losing connection-pool read hints and consistent snapshot
    List<ProductRow> findActive() {
        return jdbcClient.sql("SELECT id, name, active FROM products WHERE active = true")
            .query(ProductRow.class)
            .list();
    }

    // Bad: @Transactional without readOnly on a read-only method —
    // misses optimization signal; write transaction acquired unnecessarily
    @Transactional
    List<ProductRow> findAll() {
        return jdbcClient.sql("SELECT id, name, active FROM products")
            .query(ProductRow.class)
            .list();
    }
}
```

### Example 11: Streaming large result sets

Title: `JdbcTemplate` / `JdbcOperations` — RowCallbackHandler and ResultSetExtractor
Description: Prefer `JdbcClient` for bounded queries that fit in memory. When a query may return very large row counts, loading them all via `JdbcClient.query(...).list()` risks heap pressure — use `JdbcTemplate` with `RowCallbackHandler` to process each row as it arrives, or `ResultSetExtractor` to aggregate during iteration (these APIs live on `JdbcOperations`, not the fluent `JdbcClient` surface).

**Good example:**

```java
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
class ReportService {

    private final JdbcTemplate jdbcTemplate;

    ReportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowCallbackHandler: no return value, process/write row immediately
    void exportAuditLog(PrintWriter writer) {
        jdbcTemplate.query(
            "SELECT id, action, created_at FROM audit_log ORDER BY created_at",
            rs -> {
                writer.printf("%d,%s,%s%n",
                    rs.getLong("id"),
                    rs.getString("action"),
                    rs.getTimestamp("created_at"));
            }
        );
    }

    // ResultSetExtractor: accumulate into a custom structure
    Map<String, Long> countByCategory() {
        return jdbcTemplate.query(
            "SELECT category, COUNT(*) AS cnt FROM products GROUP BY category",
            (ResultSetExtractor<Map<String, Long>>) rs -> {
                Map<String, Long> result = new LinkedHashMap<>();
                while (rs.next()) {
                    result.put(rs.getString("category"), rs.getLong("cnt"));
                }
                return result;
            }
        );
    }
}
```

**Bad example:**

```java
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.io.PrintWriter;
import java.util.List;

record AuditEntry(long id, String action, java.sql.Timestamp createdAt) {}

@Service
class ReportService {

    private final JdbcTemplate jdbcTemplate;

    // Bad: loads every row into a List before writing — OOM risk on large tables
    void exportAuditLog(PrintWriter writer) {
        List<AuditEntry> entries = jdbcTemplate.query(
            "SELECT id, action, created_at FROM audit_log ORDER BY created_at",
            DataClassRowMapper.newInstance(AuditEntry.class)
        );
        for (AuditEntry e : entries) {
            writer.printf("%d,%s,%s%n", e.id(), e.action(), e.createdAt());
        }
    }
}
```

### Example 12: Testing with @JdbcTest

Title: Lightweight slice test for JDBC repositories; @Sql for fixtures
Description: Use `@JdbcTest` to load only `DataSource`, `JdbcTemplate`, `NamedParameterJdbcTemplate`, and `JdbcClient` — no web layer, no full application context. Wire your repository under test with `@Import`. Use `@Sql` to set up and tear down fixture data. Prefer Testcontainers with the production database dialect instead of embedded H2 so SQL, schema, and transaction behavior match production. Add `@AutoConfigureTestDatabase(replace = NONE)` so Spring Boot does not replace the container datasource with an embedded database.

**Good example:**

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // use real DB, not H2
@Import(ProductRepository.class)
@Testcontainers
@Sql("/sql/products.sql")                          // inserts fixture rows before each test
@Sql(scripts = "/sql/cleanup.sql",
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ProductRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    ProductRepository productRepository;

    @Test
    void findActive_returnsOnlyActiveProducts() {
        List<ProductRow> active = productRepository.findActive();

        assertThat(active).isNotEmpty()
            .allMatch(ProductRow::active);
    }

    @Test
    void findById_returnsEmpty_whenNotFound() {
        assertThat(productRepository.findById(Long.MAX_VALUE)).isEmpty();
    }
}

// Minimal repository wired by the slice
class ProductRepository {

    private final org.springframework.jdbc.core.simple.JdbcClient jdbcClient;

    ProductRepository(org.springframework.jdbc.core.simple.JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    java.util.List<ProductRow> findActive() {
        return jdbcClient.sql("SELECT id, name, active FROM products WHERE active = true")
            .query(ProductRow.class).list();
    }

    java.util.Optional<ProductRow> findById(long id) {
        return jdbcClient.sql("SELECT id, name, active FROM products WHERE id = ?")
            .param(id).query(ProductRow.class).optional();
    }
}

record ProductRow(long id, String name, boolean active) {}
```

**Bad example:**

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

// Bad: full application context loaded just to test a JDBC repository —
// slow startup, loads web layer and all beans unnecessarily
@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    void findActive() {
        // Bad: no fixture setup — test depends on pre-existing database state
        List<ProductRow> active = productRepository.findActive();
        assertThat(active).isNotEmpty();
    }
}
```

### Example 13: Rollback rules

Title: rollbackFor for checked exceptions; noRollbackFor for expected failures
Description: By default Spring rolls back on unchecked (`RuntimeException`) and `Error` only. Declare `rollbackFor` to include checked exceptions that should abort the transaction. Use `noRollbackFor` when a specific exception is an expected, non-fatal condition that must not roll back an otherwise healthy transaction.

**Good example:**

```java
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class OrderService {

    private final JdbcClient jdbcClient;

    OrderService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // Roll back on the checked IOException (e.g. downstream enrichment failure)
    @Transactional(rollbackFor = java.io.IOException.class)
    void placeOrder(long customerId, long productId) throws java.io.IOException {
        jdbcClient.sql("INSERT INTO orders (customer_id, product_id) VALUES (?, ?)")
            .params(customerId, productId)
            .update();
        enrichOrder(productId); // may throw IOException — transaction rolls back
    }

    private void enrichOrder(long productId) throws java.io.IOException { }

    // DuplicateKeyException is expected for idempotent inserts — do not abort the transaction
    @Transactional(noRollbackFor = DuplicateKeyException.class)
    void ensureTag(String tag) {
        try {
            jdbcClient.sql("INSERT INTO tags (name) VALUES (?)").param(tag).update();
        } catch (DuplicateKeyException ignored) {
            // tag already exists — idempotent, not an error
        }
    }
}
```

**Bad example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class OrderService {

    private final JdbcClient jdbcClient;

    OrderService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // Bad: default @Transactional does NOT roll back on checked IOException —
    // the INSERT persists even when enrichment fails
    @Transactional
    void placeOrder(long customerId, long productId) throws java.io.IOException {
        jdbcClient.sql("INSERT INTO orders (customer_id, product_id) VALUES (?, ?)")
            .params(customerId, productId)
            .update();
        enrichOrder(productId);
    }

    private void enrichOrder(long productId) throws java.io.IOException { }
}
```

### Example 14: Self-invocation pitfall

Title: Calling @Transactional from within the same bean bypasses the proxy
Description: Spring applies `@Transactional` through a proxy. When a method calls another `@Transactional` method on the same bean instance via `this.method()`, the proxy is bypassed and no transaction is started or joined. Extract the inner method to a separate Spring-managed bean to ensure proxy interception.

**Good example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Separate bean — proxy is intercepted correctly for each method
@Service
class AuditService {

    private final JdbcClient jdbcClient;

    AuditService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Transactional
    void log(String event) {
        jdbcClient.sql("INSERT INTO audit_log (event) VALUES (?)").param(event).update();
    }
}

@Service
class OrderService {

    private final JdbcClient jdbcClient;
    private final AuditService auditService;

    OrderService(JdbcClient jdbcClient, AuditService auditService) {
        this.jdbcClient = jdbcClient;
        this.auditService = auditService;
    }

    @Transactional
    void placeOrder(long customerId, long productId) {
        jdbcClient.sql("INSERT INTO orders (customer_id, product_id) VALUES (?, ?)")
            .params(customerId, productId).update();
        auditService.log("order-placed"); // proxy intercepted — @Transactional honoured
    }
}
```

**Bad example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class OrderService {

    private final JdbcClient jdbcClient;

    OrderService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    void placeOrder(long customerId, long productId) {
        jdbcClient.sql("INSERT INTO orders (customer_id, product_id) VALUES (?, ?)")
            .params(customerId, productId).update();
        this.auditLog("order-placed"); // Bad: self-invocation — @Transactional on auditLog is ignored
    }

    @Transactional
    void auditLog(String event) {
        jdbcClient.sql("INSERT INTO audit_log (event) VALUES (?)").param(event).update();
    }
}
```

### Example 15: Programmatic transactions with TransactionTemplate

Title: Fine-grained control when declarative @Transactional is not applicable
Description: Use `TransactionTemplate` when transaction boundaries must be determined at runtime — for example, inside lambdas, per-item conditional commit/rollback in a loop, or in contexts where AOP proxy interception is unavailable. `TransactionTemplate` is thread-safe and can be shared across instances.

**Good example:**

```java
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.List;

@Service
class BatchImportService {

    private final JdbcClient jdbcClient;
    private final TransactionTemplate transactionTemplate;

    BatchImportService(JdbcClient jdbcClient, TransactionTemplate transactionTemplate) {
        this.jdbcClient = jdbcClient;
        this.transactionTemplate = transactionTemplate;
    }

    // Each item commits independently; duplicate items are skipped, not aborted
    void importItems(List<String> items) {
        items.forEach(item ->
            transactionTemplate.executeWithoutResult(status -> {
                try {
                    jdbcClient.sql("INSERT INTO items (name) VALUES (?)").param(item).update();
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly(); // skip this item, continue loop
                }
            })
        );
    }
}
```

**Bad example:**

```java
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

// Bad: raw PlatformTransactionManager — verbose, error-prone commit/rollback pairing;
// no try/finally means the transaction leaks if the update throws
@Component
class BatchImportService {

    private final JdbcClient jdbcClient;
    private final PlatformTransactionManager txManager;

    BatchImportService(JdbcClient jdbcClient, PlatformTransactionManager txManager) {
        this.jdbcClient = jdbcClient;
        this.txManager = txManager;
    }

    void importItem(String item) {
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
        jdbcClient.sql("INSERT INTO items (name) VALUES (?)").param(item).update();
        txManager.commit(status); // transaction leaks if update throws
    }
}
```


## Output Format

- **ANALYZE** JDBC usage: SQL safety, parameter style, mapping approach, transaction boundaries, batch opportunities, exception handling, and whether Spring Data JDBC would simplify the case
- **CATEGORIZE** findings by impact (SECURITY for injection risk, CORRECTNESS for mapping or unsafe single-row access, PERFORMANCE for N+1 / missing readOnly / missing streaming)
- **APPLY** improvements: introduce parameter binding, prefer `JdbcClient` (named or indexed params), RowMapper or record mapping via `query(Class)`, service-level `@Transactional`, `readOnly = true` on query paths; use `JdbcTemplate` only for batch/streaming APIs where `JdbcClient` is insufficient
- **HANDLE** exceptions: translate `DuplicateKeyException` and `DataIntegrityViolationException` to domain exceptions at the service boundary
- **TEST** with `@JdbcTest` slice and `@Sql` fixture annotations; verify repository behaviour with integration tests before and after refactoring
- **EXPLAIN** when to keep programmatic JDBC vs adopt `@312-frameworks-spring-data-jdbc`
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after changes


## Safeguards

- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` or `mvn compile` before JDBC refactoring
- **SQL INJECTION**: Never build SQL by concatenating untrusted strings
- **TRANSACTIONS**: Multi-step writes must share a transaction boundary at the service layer
- **READ-ONLY**: Mark pure query service methods with `@Transactional(readOnly = true)` for connection-pool and database optimization hints
- **ROLLBACK RULES**: Default `@Transactional` does not roll back on checked exceptions — declare `rollbackFor` explicitly when checked exceptions must abort the transaction
- **SELF-INVOCATION**: Never call a `@Transactional` method via `this.method()` inside the same bean — the Spring proxy is bypassed; extract to a separate Spring-managed bean
- **TRANSACTION-TEMPLATE**: Prefer `TransactionTemplate` over raw `PlatformTransactionManager` for programmatic transactions; always use `executeWithoutResult` or `execute` to avoid manual commit/rollback pairing
- **SINGLE-ROW SAFETY**: Use `optional()` or `stream().findFirst()` for entity lookups; reserve `queryForObject` for aggregate functions that always return one row
- **EXCEPTION TRANSLATION**: Catch `DuplicateKeyException` / `DataIntegrityViolationException` at the service layer and wrap in meaningful domain exceptions
- **COMPATIBILITY**: `JdbcClient` requires Spring Framework 7+ — verify dependency versions before recommending migration
- **INCREMENTAL SAFETY**: Change data-access code in small steps, covered by `@JdbcTest` slice tests
- **TESTING**: Exercise refactored JDBC code with `@JdbcTest` integration tests; do not rely on `./mvnw compile` alone to verify correctness