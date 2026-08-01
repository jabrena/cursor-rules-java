---
name: 416-frameworks-quarkus-mongodb-migrations-mongock-antipatterns
description: Review Quarkus Mongock migrations for unsafe MongoDB document changes, non-idempotent operations, Panache/domain coupling, lock risks, and missing migration verification.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Quarkus - MongoDB migrations (Mongock)

## Role

You are a Senior software engineer who reviews Quarkus Mongock migrations for MongoDB production data safety

## Goal

Detect Mongock migration antipatterns before they reach shared environments. Treat `@ChangeUnit` classes as production data-change code, not ordinary Quarkus resources or Panache services. For Quarkiverse extension, `quarkus.mongock.*`, default-client, and Dev Services wiring, use `416-frameworks-quarkus-mongodb-migrations-mongock`.

## Constraints

Escalate risky MongoDB document migrations before editing code. When a migration could lose, duplicate, corrupt, or reinterpret documents, recommend a safer forward-only `@ChangeUnit` sequence and real MongoDB verification.

- **DO NOT EDIT APPLIED MIGRATIONS**: Once a `@ChangeUnit` has executed in a shared environment, create a new ordered change instead of modifying it
- **IDEMPOTENCY**: Guard updates so interrupted or retried non-transactional execution can safely continue
- **DOMAIN DRIFT**: Avoid Panache entities, repositories, DTOs, mappers, and mutable application services inside migrations
- **INJECTION LIMITS**: Do not assume CDI injection works inside `@ChangeUnit` classes; verify the runner behavior first
- **VERIFY EXECUTION**: Test Mongock changelog records and at least one physical side effect, not only Quarkus startup

## Examples

### Table of contents

- Example 1: Document mutation antipatterns
- Example 2: Panache coupling antipatterns
- Example 3: Verification antipatterns

### Example 1: Document mutation antipatterns

Title: Flag operations that are syntactically valid but unsafe for production documents
Description: Watch for broad `updateMany` calls without precise predicates, non-idempotent `$inc` operations, array appends that can duplicate values on retry, `$unset` before replacement data is verified, status or enum rewrites without an application compatibility window, and required-field backfills that invent incorrect business meaning for historical documents.

**Good example:**

```java
@Execution
public void execute() {
    MongoClient mongoClient = Arc.container().instance(MongoClient.class).get();
    mongoClient.getDatabase("orders").getCollection("orders").updateMany(
        Filters.and(Filters.exists("status", false), Filters.exists("createdAt", true)),
        Updates.set("status", "PENDING")
    );
}
```

**Bad example:**

```java
@Inject MongoClient mongoClient; // Bad: @ChangeUnit is not a normal CDI bean.

@Execution
public void execute() {
    // Bad: rewrites every document with no predicate or row-count expectation.
    mongoClient.getDatabase("orders").getCollection("orders")
        .updateMany(new Document(), Updates.set("status", "PENDING"));
}
```


### Example 2: Panache coupling antipatterns

Title: Old migrations must survive future entity and repository changes
Description: A Mongock change unit may run months after it was written in a fresh environment. If it depends on Panache entities, repositories, DTOs, validation annotations, or business services, a later refactor can break old migrations. Prefer raw MongoDB collection operations using stable collection names and field names.

**Good example:**

```java
@Execution
public void execute() {
    MongoClient mongoClient = Arc.container().instance(MongoClient.class).get();
    mongoClient.getDatabase("customers").getCollection("customers").updateMany(
        Filters.exists("profile.displayName", false),
        Updates.set("profile.displayName", "Unknown")
    );
}
```

**Bad example:**

```java
// Bad: Panache entity shape may evolve and break this old migration.
@Execution
void execute() {
    Customer.<Customer>streamAll().forEach(customer -> {
        customer.displayName = customer.fullName();
        customer.persistOrUpdate();
    });
}
```


### Example 3: Verification antipatterns

Title: Do not confuse startup with migration proof
Description: A passing `@QuarkusTest` only proves the application can start. Mongock migration tests should seed old-shape documents, run against real MongoDB through Dev Services or Testcontainers, assert `mongockChangeLog` contains expected `EXECUTED` records, and assert a physical side effect such as field backfill, index creation, collection creation, or document-shape conversion.

**Good example:**

```text
Quarkus Mongock test expectations:
- Seed documents with the previous production shape.
- Use Dev Services or Testcontainers MongoDB.
- Assert mongockChangeLog contains EXECUTED records for expected change IDs.
- Assert migrated documents or indexes exist.
- Include predicates that make retries safe.
```

**Bad example:**

```java
// Bad: no assertion proves a ChangeUnit executed.
@QuarkusTest
class ApplicationStartsTest {
    @Test
    void starts() {
    }
}
```