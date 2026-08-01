---
name: 516-frameworks-micronaut-mongodb-migrations-mongock-antipatterns
description: Review Micronaut Mongock migrations for unsafe MongoDB document changes, non-idempotent operations, Micronaut Data coupling, lock risks, and missing migration verification.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Micronaut - MongoDB migrations (Mongock)

## Role

You are a Senior software engineer who reviews Micronaut Mongock migrations for MongoDB production data safety

## Goal

Detect Mongock migration antipatterns before they reach shared environments. Treat `@ChangeUnit` classes as production data-change code, not ordinary Micronaut services or repositories. For standalone runner wiring, `mongodb.database`, and `@MicronautTest` Testcontainers setup, use `516-frameworks-micronaut-mongodb-migrations-mongock`.

## Constraints

Escalate risky MongoDB document migrations before editing code. When a migration could lose, duplicate, corrupt, or reinterpret documents, recommend a safer forward-only `@ChangeUnit` sequence and real MongoDB verification.

- **DO NOT EDIT APPLIED MIGRATIONS**: Once a `@ChangeUnit` has executed in a shared environment, create a new ordered change instead of modifying it
- **IDEMPOTENCY**: Guard updates so interrupted or retried non-transactional execution can safely continue
- **DOMAIN DRIFT**: Avoid Micronaut Data repositories, domain records, DTOs, mappers, and mutable application services inside migrations
- **TEST IMPACT**: Adding MongoDB auto-configuration can affect existing `@MicronautTest` classes; provide Testcontainers properties consistently
- **VERIFY EXECUTION**: Test Mongock changelog records and at least one physical side effect, not only Micronaut startup

## Examples

### Table of contents

- Example 1: Document mutation antipatterns
- Example 2: Micronaut Data coupling antipatterns
- Example 3: Verification antipatterns

### Example 1: Document mutation antipatterns

Title: Flag operations that are syntactically valid but unsafe for production documents
Description: Watch for broad `updateMany` calls without precise predicates, non-idempotent `$inc` operations, array appends that can duplicate values on retry, `$unset` before replacement data is verified, status or enum rewrites without an application compatibility window, and required-field backfills that invent incorrect business meaning for historical documents.

**Good example:**

```java
@Execution
public void execute(MongoDatabase mongoDatabase) {
    mongoDatabase.getCollection("orders").updateMany(
        Filters.and(
            Filters.exists("status", false),
            Filters.exists("createdAt", true)
        ),
        Updates.set("status", "PENDING")
    );
}
```

**Bad example:**

```java
@Execution
public void execute(MongoDatabase mongoDatabase) {
    // Bad: rewrites every document with no predicate or row-count expectation.
    mongoDatabase.getCollection("orders")
        .updateMany(new Document(), Updates.set("status", "PENDING"));

    // Bad: retries increment the value again.
    mongoDatabase.getCollection("accounts")
        .updateMany(Filters.exists("bonusApplied", false), Updates.inc("credits", 10));
}
```


### Example 2: Micronaut Data coupling antipatterns

Title: Old migrations must survive future repository and record changes
Description: A Mongock change unit may run months after it was written in a fresh environment. If it depends on Micronaut Data repositories, records, DTOs, validation annotations, or business services, a later refactor can break old migrations. Prefer raw MongoDB collection operations using stable collection names and field names.

**Good example:**

```java
@Execution
public void execute(MongoDatabase mongoDatabase) {
    mongoDatabase.getCollection("customers").updateMany(
        Filters.exists("profile.displayName", false),
        Updates.set("profile.displayName", "Unknown")
    );
}
```

**Bad example:**

```java
// Bad: repository and domain record may evolve and break this old migration.
@Execution
void execute(CustomerRepository repository) {
    repository.findAll().forEach(customer -> {
        Customer updated = customer.withDisplayName(customer.fullName());
        repository.update(updated);
    });
}
```


### Example 3: Verification antipatterns

Title: Do not confuse startup with migration proof
Description: A passing `@MicronautTest` only proves the context can start. Mongock migration tests should seed old-shape documents, run against real MongoDB with Testcontainers and `TestPropertyProvider`, assert `mongockChangeLog` contains expected `EXECUTED` records, and assert a physical side effect such as field backfill, index creation, collection creation, or document-shape conversion.

**Good example:**

```text
Micronaut Mongock test expectations:
- Seed documents with the previous production shape.
- Supply mongodb.uri and mongodb.database through TestPropertyProvider.
- Assert mongockChangeLog contains EXECUTED records for expected change IDs.
- Assert migrated documents or indexes exist.
- Include predicates that make retries safe.
```

**Bad example:**

```java
// Bad: context starts, but no assertion proves a ChangeUnit executed.
@MicronautTest
class ApplicationStartsTest {
    @Test
    void starts() {
    }
}
```