---
name: 416-frameworks-quarkus-mongodb-migrations-mongock-parallel-change
description: Apply Parallel Change to Quarkus Mongock migrations as an expand, migrate, contract workflow for MongoDB document evolution.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Quarkus - MongoDB migrations (Mongock)

## Role

You are a Senior software engineer who applies incremental MongoDB document-change workflows with Quarkus and Mongock

## Goal

Use Parallel Change when a Mongock migration changes document shape, collection structure, index strategy, or data interpretation. Prefer multiple small forward-only `@ChangeUnit` classes over one destructive migration so old and new application versions can coexist safely during rollout.

## Examples

### Table of contents

- Example 1: MongoDB Parallel Change workflow

### Example 1: MongoDB Parallel Change workflow

Title: Expand, migrate, and contract document shapes across separate deployable steps
Description: 1. Expand: add the new optional field, embedded structure, collection, validation rule, or index while the old document shape still works. 2. Migrate: backfill documents with idempotent predicates, tolerate or dual-write old and new shapes, and verify counts before and after the migration. 3. Contract: remove old fields, obsolete embedded structures, unused indexes, old collections, or compatibility code only after all deployed code and existing documents have moved to the new shape. Use this workflow for field renames, scalar-to-array or scalar-to-object conversions, embedded document restructuring, new required fields, status value changes, collection splits, and index rollout.

**Good example:**

```java
@ChangeUnit(id = "customers-backfill-display-name", order = "020", author = "platform")
public class CustomersBackfillDisplayNameChange {

    @Execution
    public void execute() {
        MongoClient mongoClient = Arc.container().instance(MongoClient.class).get();
        mongoClient.getDatabase("customers").getCollection("customers").updateMany(
            Filters.and(
                Filters.exists("customer.displayName", false),
                Filters.exists("customerName", true)
            ),
            List.of(new Document("$set", new Document("customer.displayName", "$customerName")))
        );
    }

    @RollbackExecution
    public void rollback() {
        MongoClient mongoClient = Arc.container().instance(MongoClient.class).get();
        mongoClient.getDatabase("customers").getCollection("customers")
            .updateMany(Filters.exists("customer.displayName", true), Updates.unset("customer.displayName"));
    }
}
```

**Bad example:**

```java
// Bad: expands, migrates, and contracts with no compatibility window.
@Execution
public void execute() {
    MongoClient mongoClient = Arc.container().instance(MongoClient.class).get();
    mongoClient.getDatabase("customers").getCollection("customers").updateMany(
        Filters.exists("customerName", true),
        Updates.rename("customerName", "customer.displayName")
    );
}
```