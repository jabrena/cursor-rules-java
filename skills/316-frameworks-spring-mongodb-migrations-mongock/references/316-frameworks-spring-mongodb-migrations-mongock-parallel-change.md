---
name: 316-frameworks-spring-mongodb-migrations-mongock-parallel-change
description: Apply Parallel Change to Spring Boot Mongock migrations as an expand, migrate, contract workflow for MongoDB document evolution.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Spring - MongoDB migrations (Mongock)

## Role

You are a Senior software engineer who applies incremental MongoDB document-change workflows with Spring Boot and Mongock

## Goal

Use Parallel Change when a Mongock migration changes document shape, collection structure, index strategy, or data interpretation. Prefer multiple small forward-only `@ChangeUnit` classes over one destructive migration so old and new application versions can coexist safely during rollout.

## Examples

### Table of contents

- Example 1: MongoDB Parallel Change workflow
- Example 2: Index rollout

### Example 1: MongoDB Parallel Change workflow

Title: Expand, migrate, and contract document shapes across separate deployable steps
Description: 1. Expand: add the new optional field, embedded structure, collection, validation rule, or index while the old document shape still works. 2. Migrate: backfill documents with idempotent predicates, tolerate or dual-write old and new shapes, and verify counts before and after the migration. 3. Contract: remove old fields, obsolete embedded structures, unused indexes, old collections, or compatibility code only after all deployed code and existing documents have moved to the new shape. Use this workflow for field renames, scalar-to-array or scalar-to-object conversions, embedded document restructuring, new required fields, status value changes, collection splits, and index rollout.

**Good example:**

```java
// Expand release: application writes both customerName and customer.displayName.

@ChangeUnit(id = "customers-backfill-display-name", order = "020", author = "platform")
public class CustomersBackfillDisplayNameChange {

    @Execution
    public void execute(MongoDatabase mongoDatabase) {
        mongoDatabase.getCollection("customers").updateMany(
            Filters.and(
                Filters.exists("customer.displayName", false),
                Filters.exists("customerName", true)
            ),
            List.of(new Document("$set", new Document("customer.displayName", "$customerName")))
        );
    }

    @RollbackExecution
    public void rollback(MongoDatabase mongoDatabase) {
        mongoDatabase.getCollection("customers")
            .updateMany(Filters.exists("customer.displayName", true), Updates.unset("customer.displayName"));
    }
}

// Contract release later: remove customerName after all code reads customer.displayName.
```

**Bad example:**

```java
// Bad: expands, migrates, and contracts with no compatibility window.
@Execution
public void execute(MongoDatabase mongoDatabase) {
    mongoDatabase.getCollection("customers").updateMany(
        Filters.exists("customerName", true),
        Updates.rename("customerName", "customer.displayName")
    );
}
```


### Example 2: Index rollout

Title: Create indexes before application code depends on them
Description: Treat index changes as part of the expand phase. Add the index in a separate change unit, verify it exists, deploy read or write paths that rely on it, and remove obsolete indexes only in a later contract step. Check duplicate or malformed values before adding unique indexes.

**Good example:**

```java
@Execution
public void execute(MongoDatabase mongoDatabase) {
    mongoDatabase.getCollection("orders").createIndex(
        Indexes.ascending("customerId", "createdAt"),
        new IndexOptions().name("idx_orders_customer_created_at")
    );
}
```

**Bad example:**

```text
Bad rollout:
1. Deploy code that requires a new query index.
2. Add the index after users hit the path.
3. Drop the old index in the same deploy with no performance rollback path.
```