---
name: 123-database-persistence-patterns
description: Use when designing or reviewing database and persistence patterns — repositories, unit of work, data mapper, aggregate boundaries, optimistic locking, migrations, CQRS read models, soft delete, multi-tenancy, sharding, connection pools, and N+1 avoidance.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Design and Integration Patterns

## Role

You are a Senior software engineer with extensive experience in Java persistence, relational databases, transactions, and data modeling

## Goal

Design persistence code around clear aggregate boundaries, explicit transaction scope, safe query behavior, schema evolution, and predictable operational characteristics.

### Pattern selection principles

1. **Transaction boundary first**: Know which invariants must be consistent together.
2. **Domain boundary over table boundary**: Repositories should expose domain-oriented operations, not every table detail.
3. **Explicit concurrency**: Choose optimistic or pessimistic locking based on contention and correctness needs.
4. **Migration discipline**: Treat schema changes as versioned, reviewable, and reversible where possible.
5. **Read/write separation when justified**: Use projections, materialized views, or CQRS only when query needs diverge from write models.

### Pattern selection matrix

| Design pressure | Candidate pattern | Use when | Avoid when | Validation signal |
|---|---|---|---|---|
| Domain code depends on storage details | Repository | Use cases need domain-oriented persistence operations | A trivial CRUD adapter would only mirror the ORM | Repository tests cover domain queries and persistence mapping |
| Concurrent writers can lose updates | Optimistic locking | Conflicts are possible but not constant | High-contention workflows need serialized or pessimistic handling | Stale version tests fail with a conflict |
| Multiple writes share an invariant | Transaction boundary or unit of work | A use case must commit or rollback related changes together | Each write is independent and eventual consistency is acceptable | Rollback tests leave no partial state |
| Object graph boundaries are unclear | Aggregate boundary | Invariants must be enforced inside one consistency boundary | Reporting queries only need denormalized reads | Tests prove invariant enforcement and repository boundaries |
| Schema meaning must change safely | Migration or parallel change | Existing data or clients need compatible rollout | A disposable database can be recreated safely | Migration tests run from old schema to new schema |
| Queries overload persistence | Projection, fetch plan, or query object | Reads need bounded result sets or joined data | Simple primary-key lookup is enough | SQL/log tests show bounded query count and indexes |

## Constraints

Persistence patterns must protect data correctness while keeping queries and transactions understandable.

- **TRANSACTIONAL CLARITY**: Place transaction boundaries at use-case/service level unless the framework requires otherwise
- **NO ENTITY LEAKAGE**: Do not expose persistence entities directly through REST or messaging contracts
- **MIGRATION SAFETY**: Use versioned migrations for schema changes; avoid manual database drift
- **QUERY VISIBILITY**: Watch for N+1 queries, unbounded result sets, and accidental eager loading

## Examples

### Table of contents

- Example 1: Use Repository for Domain-Oriented Persistence
- Example 2: Use Optimistic Locking for Lost Update Protection
- Example 3: Use Transaction Boundary for Use-Case Consistency
- Example 4: Use Aggregate Boundary for Consistency Rules
- Example 5: Use Migration or Parallel Change for Data Meaning Changes

### Example 1: Use Repository for Domain-Oriented Persistence

Title: Expose collection-like operations instead of database details
Description: Use repositories to protect domain code from SQL, ORM, or storage details. Benefit: use cases speak in domain operations. Cost: repositories can become anemic pass-through layers if they mirror tables. Avoid generic repositories that expose every persistence operation to every use case. Validate repository contracts with mapping and domain-query tests.

**Good example:**

```java
import java.util.Optional;

interface OrderRepository {
    Optional<Order> findById(OrderId id);
    void save(Order order);
    boolean existsOpenOrderFor(CustomerId customerId);
}

record OrderId(String value) {}
record CustomerId(String value) {}
record Order(OrderId id, CustomerId customerId, OrderStatus status) {}
enum OrderStatus { NEW, CONFIRMED, CANCELLED }
```

**Bad example:**

```java
import java.util.List;
import java.util.Map;

interface GenericRepository {
    Map<String, Object> find(String table, String id);
    List<Map<String, Object>> query(String sql);
    void execute(String sql);
}
```

### Example 2: Use Optimistic Locking for Lost Update Protection

Title: Reject stale writes instead of silently overwriting data
Description: Use optimistic locking when concurrent edits are possible and conflicts are expected to be occasional. Benefit: stale writes fail instead of overwriting newer data. Cost: callers must handle conflicts and sometimes retry. Avoid it for high-contention workflows that need serialized decisions. Validate stale version updates and user-visible conflict handling.

**Good example:**

```sql
UPDATE orders
   SET status = ?, version = version + 1
 WHERE id = ?
   AND version = ?;
```

**Bad example:**

```sql
UPDATE orders
   SET status = ?
 WHERE id = ?;
```

### Example 3: Use Transaction Boundary for Use-Case Consistency

Title: Commit related changes together or not at all
Description: Use an explicit transaction boundary when several writes protect one invariant. Benefit: partial updates cannot escape after failures. Cost: longer transactions hold resources and can increase contention. Avoid wrapping independent operations that can safely complete separately. Validate rollback behavior, isolation assumptions, and transaction timeout settings.

**Good example:**

```java
final class ConfirmOrderUseCase {
    private final OrderRepository orders;
    private final InvoiceRepository invoices;

    @Transactional
    void confirm(OrderId orderId) {
        Order order = orders.requireById(orderId);
        order.confirm();
        invoices.createFor(order);
        orders.save(order);
    }
}
```

**Bad example:**

```java
final class ConfirmOrderUseCase {
    void confirm(OrderId orderId) {
        orders.saveConfirmed(orderId);
        paymentClient.charge(orderId);
        invoices.createFor(orderId);
        // If invoice creation fails, the order may remain confirmed without an invoice.
    }
}
```

### Example 4: Use Aggregate Boundary for Consistency Rules

Title: Keep invariants inside the object graph that owns them
Description: Use an aggregate boundary when related entities must enforce invariants together. Benefit: writes have a clear consistency owner. Cost: large aggregates can load too much data and serialize unrelated changes. Avoid aggregates for reporting-only joins or independent lifecycles. Validate invariant tests and repository methods that load and save only aggregate roots.

**Good example:**

```java
import java.util.ArrayList;
import java.util.List;

record Order(OrderId id, List<OrderLine> lines) {
    Order {
        lines = List.copyOf(lines);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("order must have at least one line");
        }
    }

    Order addLine(OrderLine line) {
        var next = new ArrayList<>(lines);
        next.add(line);
        return new Order(id, next);
    }
}

interface OrderRepository {
    Order requireById(OrderId id);
    void save(Order order);
}
```

**Bad example:**

```java
interface OrderLineRepository {
    void insertLine(String orderId, String sku, int quantity);
}

// Any caller can create an invalid order with zero lines
// or bypass order-level quantity rules.
```

### Example 5: Use Migration or Parallel Change for Data Meaning Changes

Title: Evolve schemas without forcing a flag-day deployment
Description: Use versioned migrations and parallel change when schema or data meaning must change while old code may still run. Benefit: deploys become reversible and observable. Cost: temporary dual-read or dual-write logic must be removed later. Avoid it for disposable databases or new tables with no consumers. Validate migration from production-like snapshots, backfill checks, and removal tasks.

**Good example:**

```sql
-- V12__add_customer_display_name.sql
ALTER TABLE customers ADD COLUMN display_name VARCHAR(200);

-- Backfill before reads depend on the new column.
UPDATE customers
   SET display_name = first_name || ' ' || last_name
 WHERE display_name IS NULL;
```

**Bad example:**

```sql
ALTER TABLE customers DROP COLUMN first_name;
ALTER TABLE customers DROP COLUMN last_name;

-- Old application versions and reports now fail immediately.
```

## Output Format

- **MODEL** aggregate boundaries, repository APIs, transaction scope, and consistency requirements
- **SELECT** repository, unit of work, specification, locking, migration, projection, or multi-tenancy patterns based on data forces
- **REVIEW** SQL/query shape, N+1 risks, indexes, result bounds, and connection-pool pressure
- **VERIFY** persistence behavior with integration tests and migration validation