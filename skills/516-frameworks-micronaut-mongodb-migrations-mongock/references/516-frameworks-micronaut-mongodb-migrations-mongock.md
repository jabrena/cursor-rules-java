---
name: 516-frameworks-micronaut-mongodb-migrations-mongock
description: Use when you need to add or review Mongock MongoDB data migrations in a Micronaut application - including Mongock runner/driver selection, Micronaut bean wiring, migration scan packages, `@ChangeUnit` classes, lock and transaction behavior, and Testcontainers validation. For general Micronaut MongoDB persistence use `@515-frameworks-micronaut-mongodb`.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Micronaut - MongoDB migrations (Mongock)

## Role

You are a Senior software engineer with extensive experience in Micronaut, Micronaut Data MongoDB, and code-first MongoDB migrations with Mongock

## Goal

Add or improve Mongock migrations as code-first, versioned data changes in Micronaut applications. Prefer a Micronaut-specific runner only when the selected Mongock version documents one that is compatible with the project. Otherwise, wire the Mongock standalone runner through Micronaut beans or a controlled startup/job path. Keep migrations separate from controllers and repositories, make `@ChangeUnit` operations idempotent, and verify them against a real MongoDB instance. For Micronaut Data Mongo repositories and document modeling, use `@515-frameworks-micronaut-mongodb`.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully before Mongock, MongoDB, or migration edits
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements; include a MongoDB-backed Micronaut test where feasible
- **COMPATIBILITY**: Verify whether the selected Mongock version documents a compatible Micronaut runner before recommending Micronaut-specific runner coordinates. No verified Micronaut-specific Mongock runner exists in Mongock 5.x — always use the standalone runner
- **INJECTABLE TYPE**: With `mongock-standalone` + `mongodb-sync-v4-driver`, Mongock resolves `@ChangeUnit` method parameters from the driver's database handle. The injectable type is `MongoDatabase` — not `MongoClient`. Do not declare `MongoClient` as a `@ChangeUnit` method parameter

## Examples

### Table of contents

- Example 1: Maven coordinates
- Example 2: application.properties
- Example 3: Runner bean
- Example 4: ChangeUnit design
- Example 5: Testing migrations

### Example 1: Maven coordinates

Title: Use the Mongock BOM with the standalone runner and MongoDB sync driver
Description: Mongock architecture lets runners and drivers be combined. No verified Micronaut-specific runner exists in Mongock 5.x, so always use `mongock-standalone` with `mongodb-sync-v4-driver` and wire execution as a Micronaut bean. Keep Micronaut MongoDB dependencies aligned with the Micronaut BOM. This is the same standalone pattern proven effective for Spring Boot 4.x projects where the Spring Boot-specific runner is also unavailable.

**Good example:**

```xml
<properties>
    <mongock.version>5.5.1</mongock.version>
    <testcontainers.mongodb.version>1.21.3</testcontainers.mongodb.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.mongock</groupId>
            <artifactId>mongock-bom</artifactId>
            <version>${mongock.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>io.mongock</groupId>
        <artifactId>mongock-standalone</artifactId>
    </dependency>
    <dependency>
        <groupId>io.mongock</groupId>
        <artifactId>mongodb-sync-v4-driver</artifactId>
    </dependency>
    <dependency>
        <groupId>io.micronaut.mongodb</groupId>
        <artifactId>micronaut-mongo-sync</artifactId>
    </dependency>

    <!-- Test dependencies -->
    <!-- org.testcontainers:mongodb is NOT managed by the Micronaut platform BOM;
         always declare an explicit version. -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>mongodb</artifactId>
        <version>${testcontainers.mongodb.version}</version>
        <scope>test</scope>
    </dependency>
    <!-- assertj-core is managed by the Micronaut platform BOM but must be declared explicitly. -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Bad example:**

```xml
<!-- Bad: adding guessed Micronaut runner coordinates without verifying they exist and match the project -->
<dependency>
    <groupId>io.mongock</groupId>
    <artifactId>mongock-micronaut</artifactId>
    <version>5.5.1</version>
</dependency>
```


### Example 2: application.properties

Title: Configure MongoDB URI and database name for the standalone runner
Description: The `MongockMigrationRunner` bean reads `mongodb.database` via `@Value`. Declare both `mongodb.uri` and `mongodb.database` in `application.properties` for local development and non-test environments. In tests, these values are overridden dynamically by `TestPropertyProvider` pointing to a Testcontainers MongoDB instance — the static values in `application.properties` are never used during test execution.

**Good example:**

```properties
mongodb.uri=mongodb://localhost:27017
mongodb.database=mydb
```

**Bad example:**

```properties
# Bad: omitting mongodb.database forces the runner bean to fail at startup
# because @Value("${mongodb.database}") has no value to bind
```


### Example 3: Runner bean

Title: Wire a controlled Mongock standalone runner through Micronaut
Description: Run migrations at a deliberate lifecycle point. For small required migrations, a startup event listener can work. For long-running backfills, prefer a maintenance job or explicit operational command and gate application rollout around it.

**Good example:**

```java
import com.mongodb.client.MongoClient;
import io.micronaut.context.annotation.Value;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import io.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.mongock.runner.standalone.MongockStandalone;
import jakarta.inject.Singleton;

@Singleton
public class MongockMigrationRunner {

    private final MongoClient mongoClient;
    private final String databaseName;

    public MongockMigrationRunner(
            MongoClient mongoClient,
            @Value("${mongodb.database}") String databaseName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
    }

    @EventListener
    void migrate(ServerStartupEvent event) {
        MongockStandalone.builder()
            .setDriver(MongoSync4Driver.withDefaultLock(mongoClient, databaseName))
            .addMigrationScanPackage("com.example.migrations.mongo")
            .setTransactional(false)
            .buildRunner()
            .execute();
    }
}
```

**Bad example:**

```java
// Bad: running migrations from a controller allows repeated ad hoc execution paths
@Controller("/admin/migrations")
class MigrationController {
    @Post
    void migrate() {
        runner.migrate();
    }
}
```


### Example 4: ChangeUnit design

Title: Inject MongoDatabase from the sync driver; do not use MongoClient or Micronaut Data repositories
Description: With the standalone runner and `mongodb-sync-v4-driver`, Mongock resolves `@ChangeUnit` method parameters from the driver's database handle. The type that Mongock makes available is `MongoDatabase` — not `MongoClient`. Use raw MongoDB collection operations (`Filters`, `Updates`) via `MongoDatabase` so the migration remains compilable after the domain model evolves. Do not inject Micronaut Data repositories or domain records that may change.

**Good example:**

```java
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

@ChangeUnit(id = "orders-add-status", order = "001", author = "platform")
public class OrdersAddStatusChange {

    @Execution
    public void execute(MongoDatabase mongoDatabase) {
        mongoDatabase.getCollection("orders")
            .updateMany(Filters.exists("status", false), Updates.set("status", "PENDING"));
    }

    @RollbackExecution
    public void rollback(MongoDatabase mongoDatabase) {
        mongoDatabase.getCollection("orders")
            .updateMany(Filters.eq("status", "PENDING"), Updates.unset("status"));
    }
}
```

**Bad example:**

```java
// Bad: MongoClient is not injectable from the standalone sync driver's parameter resolver.
// Also couples the migration to a repository that may change after this migration has run.
@ChangeUnit(id = "orders-add-status", order = "001", author = "platform")
class OrdersAddStatusChange {
    @Execution
    void execute(MongoClient mongoClient) {  // not injectable via MongoSync4Driver
        mongoClient.getDatabase("orders").getCollection("orders")
            .updateMany(Filters.exists("status", false), Updates.set("status", "PENDING"));
    }
}
```


### Example 5: Testing migrations

Title: Run Micronaut migration wiring against Testcontainers MongoDB; verify changelog records and side effects
Description: Use `@MicronautTest` with `TestPropertyProvider` and a static `MongoDBContainer` so the same runner and driver execute against a real MongoDB instance. Micronaut resolves `mongodb.uri` at context creation time, before beans are injected, so `TestPropertyProvider` (not `@Property`) is the only way to supply a dynamic Testcontainers URI. Also annotate the class with `@TestInstance(Lifecycle.PER_CLASS)` — this is required for `TestPropertyProvider` to work correctly with `@MicronautTest`. Verify that the `mongockChangeLog` collection has EXECUTED records for every expected change unit, and check at least one physical side effect (index or document shape) to confirm the migration ran — do not merely assert that the context loaded. If the project already has other `@MicronautTest` classes, those will also fail with `MongoTimeoutException` after `micronaut-mongo-sync` is added (see safeguard EXISTING TEST IMPACT). Update them to also implement `TestPropertyProvider` using a shared static container.

**Good example:**

```java
import com.mongodb.client.MongoClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongockMigrationTest implements TestPropertyProvider {

    static final MongoDBContainer MONGODB =
        new MongoDBContainer(DockerImageName.parse("mongo:7.0"));

    @Override
    public Map<String, String> getProperties() {
        if (!MONGODB.isRunning()) {
            MONGODB.start();
        }
        return Map.of(
            "mongodb.uri", MONGODB.getConnectionString(),
            "mongodb.database", "testdb"
        );
    }

    @Inject
    MongoClient mongoClient;

    @Test
    void allExpectedMigrationsExecutedSuccessfully() {
        List<Document> changelog = mongoClient.getDatabase("testdb")
            .getCollection("mongockChangeLog")
            .find()
            .into(new ArrayList<>());

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
// Bad: bare @MicronautTest without TestPropertyProvider.
// Micronaut resolves mongodb.uri at context creation — the context will fail to start
// with MongoTimeoutException because no real MongoDB is reachable at localhost:27017.
@MicronautTest
class MongockMigrationTest {
    @Inject MongoClient mongoClient; // context never starts; injection never happens
}
```


## Output Format

- **ANALYZE** Micronaut MongoDB setup: Micronaut version, MongoDB client type, available Mongock runner, driver, scan package, and startup policy
- **CATEGORIZE** migration risks (RUNNER COMPATIBILITY, INJECTABLE TYPE, LOCKING, IDEMPOTENCY, DOMAIN COUPLING, STARTUP TIME, TEST GAP) by impact
- **APPLY** Micronaut-aligned fixes: standalone runner + sync driver, `MongoDatabase` in `@ChangeUnit` methods, startup runner bean, and Testcontainers checks verifying changelog records and side effects
- **ALIGN** with `@515-frameworks-micronaut-mongodb` for normal document/repository design while keeping migrations independent
- **VALIDATE** with `./mvnw compile` before changes and `./mvnw clean verify` after changes


## Safeguards

- **DO NOT EDIT APPLIED MIGRATIONS**: Once a `@ChangeUnit` has run in a shared environment, add a new ordered change instead of modifying it
- **INJECTABLE TYPE**: With `mongodb-sync-v4-driver`, Mongock injects `MongoDatabase` into `@ChangeUnit` methods — not `MongoClient`. Declaring `MongoClient` as a method parameter will cause a runtime error
- **IDEMPOTENCY**: Write migrations so interrupted non-transactional execution can safely continue on the next run
- **STARTUP TIME**: Do not run long backfills in a startup listener if probes or rollout windows can interrupt them repeatedly
- **RUNNER CLAIMS**: Do not invent Micronaut-specific Mongock coordinates; verify official artifacts or use the standalone runner pattern
- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` before recommending dependency or migration changes
- **EXISTING TEST IMPACT**: Adding `micronaut-mongo-sync` to a project that did not previously include MongoDB will cause every existing `@MicronautTest` class to fail with `MongoTimeoutException` — Micronaut auto-configures a `MongoClient` at context creation and attempts to reach `mongodb.uri`. Update every `@MicronautTest` class in the project to implement `TestPropertyProvider` and supply a Testcontainers-based `mongodb.uri`, or share a common `TestPropertyProvider` interface backed by a single static `MongoDBContainer`.