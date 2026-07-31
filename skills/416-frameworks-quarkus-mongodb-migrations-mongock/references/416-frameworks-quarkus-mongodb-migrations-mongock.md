---
name: 416-frameworks-quarkus-mongodb-migrations-mongock
description: Use when you need to add or review Mongock MongoDB data migrations in a Quarkus application - including the Quarkiverse Mongock extension, Quarkus MongoDB client configuration, `quarkus.mongock.*` settings, `@ChangeUnit` classes, lock and transaction behavior, and Quarkus test validation. For general Quarkus MongoDB persistence use `@415-frameworks-quarkus-mongodb`.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Quarkus - MongoDB migrations (Mongock)

## Role

You are a Senior software engineer with extensive experience in Quarkus, Quarkus MongoDB client/Panache Mongo, and code-first MongoDB migrations with Mongock

## Goal

Add or improve Mongock migrations as code-first, versioned data changes that run safely in Quarkus. Use the Quarkiverse Mongock extension when it matches the project version and MongoDB client model. Keep migrations separate from resources and repositories, make `@ChangeUnit` operations idempotent, and verify them against a real MongoDB instance. For Panache Mongo entities, repositories, and service-layer persistence patterns, use `@415-frameworks-quarkus-mongodb`.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully before Mongock, MongoDB, or migration edits
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements; include a MongoDB-backed Quarkus test where feasible
- **EXTENSION COMPATIBILITY**: Verify the Quarkiverse Mongock extension version against the active Quarkus platform BOM before adding dependencies; the extension version and the Quarkus platform version must align
- **DEFAULT CLIENT LIMIT**: If the application uses multiple MongoDB clients, confirm the extension supports that topology before implementing migrations
- **RUNNER NOTE**: The Quarkiverse Mongock extension wires Mongock through `MongockStandalone` (not a CDI-aware runner). `@ChangeUnit` instances are created by Mongock via reflection — CDI `@Inject` field injection and `@Execution` method-parameter injection do NOT work. To access `MongoClient` or other CDI beans inside a `@ChangeUnit`, use `Arc.container().instance(MongoClient.class).get()` directly in the method body. The extension is NOT the same as `mongock-springboot-v3` and is NOT affected by the Spring Boot 3/4 version split.
- **VERSION GAP**: If `quarkus-mongock` latest release targets a Quarkus version significantly older than the active platform (check https://github.com/quarkiverse/quarkus-mongock/releases), the production `quarkus:build` may fail with `UnsatisfiedResolutionException` for `MongoClient`. In that case STOP and document the incompatibility — do not apply changes that break the production build.

## Examples

### Table of contents

- Example 1: Maven extension
- Example 2: application.properties
- Example 3: ChangeUnit design
- Example 4: Explicit execution
- Example 5: Testing migrations

### Example 1: Maven extension

Title: Add Quarkiverse Mongock with the Quarkus MongoDB client; verify extension version against the Quarkus platform
Description: Quarkiverse Mongock provides Quarkus CDI integration around Mongock and relies on the Quarkus MongoDB client. If the extension is not part of the active Quarkus platform BOM, declare an explicit version through a property and keep it reviewed during Quarkus upgrades. Check https://github.com/quarkiverse/quarkus-mongock for the latest release compatible with your Quarkus platform version. The extension wires Mongock internally through CDI — it does not use `mongock-springboot-v3` or any Spring-specific runner.

**Good example:**

```xml
<properties>
    <quarkus-mongock.version>0.6.0</quarkus-mongock.version>
</properties>

<dependencies>
    <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-mongodb-client</artifactId>
    </dependency>
    <dependency>
        <groupId>io.quarkiverse.mongock</groupId>
        <artifactId>quarkus-mongock</artifactId>
        <version>${quarkus-mongock.version}</version>
    </dependency>
</dependencies>
```

**Bad example:**

```xml
<!-- Bad: adding the Spring Boot runner coordinates to a Quarkus project.
     The Spring runner is version-bound to Spring Boot 3.x and will not integrate
     with Quarkus CDI. Use quarkus-mongock from the Quarkiverse instead. -->
<dependency>
    <groupId>io.mongock</groupId>
    <artifactId>mongock-springboot-v3</artifactId>
</dependency>
```


### Example 2: application.properties

Title: Configure MongoDB and make migrate-at-start intentional
Description: The extension can run migrations automatically at startup with `quarkus.mongock.migrate-at-start=true`, or you can inject `MongockFactory` and run a migration explicitly. Automatic startup is simple; a controlled job is safer for long-running backfills.

**Good example:**

```properties
quarkus.mongodb.connection-string=mongodb://localhost:27017
quarkus.mongodb.database=orders

quarkus.mongock.enabled=true
quarkus.mongock.migrate-at-start=true
quarkus.mongock.transaction-enabled=true
```

**Bad example:**

```properties
# Bad: no controlled migration path; app starts without applying required data changes
quarkus.mongock.migrate-at-start=false
```


### Example 3: ChangeUnit design

Title: Use stable MongoDB collection operations via MongoClient rather than mutable Panache entity APIs
Description: `@ChangeUnit` classes should remain compilable and meaningful after the domain model evolves. The Quarkiverse extension uses `MongockStandalone` — Mongock instantiates `@ChangeUnit` classes directly via reflection, outside of CDI. Neither `@Inject` field injection nor `@Execution` method-parameter injection will receive CDI beans. Access `MongoClient` (or any other CDI bean) programmatically via `Arc.container().instance(MongoClient.class).get()` inside each method body. Prefer stable collection names and raw field operations so the migration works regardless of how Panache entities or repositories evolve later.

**Good example:**

```java
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import io.quarkus.arc.Arc;

@ChangeUnit(id = "orders-add-status", order = "001", author = "platform")
public class OrdersAddStatusChange {

    @Execution
    public void execute() {
        MongoClient mongoClient = Arc.container().instance(MongoClient.class).get();
        mongoClient.getDatabase("orders")
            .getCollection("orders")
            .updateMany(Filters.exists("status", false), Updates.set("status", "PENDING"));
    }

    @RollbackExecution
    public void rollback() {
        MongoClient mongoClient = Arc.container().instance(MongoClient.class).get();
        mongoClient.getDatabase("orders")
            .getCollection("orders")
            .updateMany(Filters.eq("status", "PENDING"), Updates.unset("status"));
    }
}
```

**Bad example:**

```java
// Bad: @Inject field injection does not work — Mongock creates @ChangeUnit instances
// via reflection outside of CDI, so mongoClient will be null at runtime.
@ChangeUnit(id = "orders-add-status", order = "001", author = "platform")
public class OrdersAddStatusChange {
    @Inject MongoClient mongoClient; // null — CDI never sets this field

    @Execution
    public void execute() {
        mongoClient.getDatabase("orders") // NullPointerException
            .getCollection("orders")
            .updateMany(Filters.exists("status", false), Updates.set("status", "PENDING"));
    }
}
```


### Example 4: Explicit execution

Title: Use MongockFactory when startup execution is not the right operational model
Description: For migrations that may exceed startup probes or need a maintenance window, inject `MongockFactory` and execute from a controlled CDI bean, CLI command, or operator-managed path. Ensure the application does not serve traffic that assumes the new data shape before the migration completes.

**Good example:**

```java
import io.mongock.runner.core.executor.MongockRunner;
import io.quarkiverse.mongock.MongockFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MigrationService {

    @Inject MongockFactory mongockFactory;

    public void migrate() {
        MongockRunner runner = mongockFactory.createMongockRunner();
        runner.execute();
    }
}
```

**Bad example:**

```java
// Bad: endpoint-triggered migration can race with normal traffic and repeated requests
@POST
@Path("/migrate")
public void migrateFromHttpRequest() {
    migrationService.migrate();
}
```


### Example 5: Testing migrations

Title: Run migrations with @QuarkusTest and real MongoDB infrastructure
Description: Quarkus Dev Services provides a real MongoDB container automatically for `@QuarkusTest` without extra configuration. Alternatively, use Testcontainers or Quarkus Test Resources. Verify that the `mongockChangeLog` collection has EXECUTED records and check at least one physical side effect of the migration.

**Good example:**

```java
@QuarkusTest
class MongockMigrationTest {

    @Inject MongoClient mongoClient;

    @Test
    void allExpectedMigrationsExecutedSuccessfully() {
        List<Document> changelog = mongoClient.getDatabase("orders")
            .getCollection("mongockChangeLog")
            .find().into(new ArrayList<>());
        List<String> executedIds = changelog.stream()
            .filter(d -> "EXECUTED".equals(d.getString("state")))
            .map(d -> d.getString("changeId"))
            .toList();
        assertThat(executedIds).contains("orders-add-status");
    }
}
```

**Bad example:**

```java
// Bad: only unit-testing the ChangeUnit with mocks; no lock/history/startup behavior is exercised
```


## Output Format

- **ANALYZE** Quarkus MongoDB setup: platform version, Quarkiverse Mongock extension version, default MongoDB client, migration execution mode, and tests
- **CATEGORIZE** migration risks (EXTENSION COMPATIBILITY, DEFAULT CLIENT, LOCKING, IDEMPOTENCY, DOMAIN COUPLING, TEST GAP) by impact
- **APPLY** Quarkus-aligned fixes: Quarkiverse extension coordinates (only when compatible with the active Quarkus platform), `quarkus.mongock.*` properties, stable `@ChangeUnit` classes using `Arc.container().instance(MongoClient.class).get()` for MongoDB access, and Quarkus Dev Services tests
- **ALIGN** with `@415-frameworks-quarkus-mongodb` for normal MongoDB/Panache Mongo design while keeping migrations independent
- **VALIDATE** with `./mvnw compile` before changes and `./mvnw clean verify` after changes


## Safeguards

- **DO NOT EDIT APPLIED MIGRATIONS**: Once a `@ChangeUnit` has run in a shared environment, add a new ordered change instead of modifying it
- **IDEMPOTENCY**: Write migrations so interrupted non-transactional execution can safely continue on the next run
- **LOCKING**: Keep lock acquisition failures visible in clustered deployments unless an external coordinator is documented
- **EXTENSION LIMITS**: Re-check Quarkiverse Mongock limitations for multiple clients, native mode, and transaction behavior during Quarkus upgrades. Check https://github.com/quarkiverse/quarkus-mongock for the latest compatible version
- **WRONG RUNNER**: Do not add `mongock-springboot-v3` to a Quarkus project — it is unrelated to Quarkus CDI and will not function. Use `quarkus-mongock` from Quarkiverse instead
- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` before recommending dependency or migration changes
- **VERSION GAP**: If the latest `quarkus-mongock` release targets a Quarkus version significantly older than the active platform (e.g. extension built for 3.19.3 while project uses 3.35.4), the production `quarkus:build` will fail with `UnsatisfiedResolutionException` for `MongoClient` even though `@QuarkusTest` tests may appear to pass. Do NOT apply changes in this case — revert, document the incompatibility, and check https://github.com/quarkiverse/quarkus-mongock/releases for a compatible release.
- **CDI INJECTION IN CHANGUNIT**: Neither `@Inject` field injection nor `@Execution` method-parameter injection works inside `@ChangeUnit` classes. Always use `Arc.container().instance(Type.class).get()` to access CDI beans programmatically.