---
name: 316-frameworks-spring-mongodb-migrations-mongock
description: Use when you need to add or review Mongock MongoDB data migrations in a Spring Boot application - including runner/driver selection per Spring Boot version, standalone runner wiring, migration scan packages, `@ChangeUnit` classes, lock and transaction behavior, and optional policy-approved MongoDB integration verification. For general Spring Data MongoDB persistence use `@315-frameworks-spring-mongodb`.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Spring - MongoDB migrations (Mongock)

## Role

You are a Senior software engineer with extensive experience in Spring Boot, Spring Data MongoDB, and code-first MongoDB migrations with Mongock

## Goal

Add or improve Mongock migrations as code-first, versioned data changes that run safely with Spring Boot and Spring Data MongoDB. Keep migrations separate from normal repositories and request handling. Use `@ChangeUnit` classes for forward-only document changes, make operations idempotent, and verify them against a real MongoDB instance. For document modeling, repositories, and service-layer persistence patterns, use `@315-frameworks-spring-mongodb`.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully before Mongock, MongoDB, or migration edits
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements; include a MongoDB-backed migration test where feasible
- **RUNNER SPLIT**: `mongock-springboot-v3` is version-bound to Spring Boot 3.x / Spring Framework 6 (`[3.0.0-RC1, 4.0.0)`). On Spring Boot 4.x it silently skips execution. Always use `mongock-standalone` + `mongodb-sync-v4-driver` on Spring Boot 4.x projects
- **COMPATIBILITY**: Confirm the active Spring Boot major version before choosing the runner. Use `mongock-springboot-v3` only when the POM parent is `spring-boot-starter-parent` 3.x; use `mongock-standalone` for 4.x and above

## Examples

### Table of contents

- Example 1: Maven coordinates (Spring Boot 4.x)
- Example 2: Runner bean (Spring Boot 4.x)
- Example 3: ChangeUnit design
- Example 4: Testing migrations
- Example 5: Context tests after enabling Mongock

### Example 1: Maven coordinates (Spring Boot 4.x)

Title: Use the standalone runner and raw MongoDB sync driver; do not use mongock-springboot-v3 on Spring Boot 4
Description: Mongock artifacts are versioned together through `io.mongock:mongock-bom`. The `mongock-springboot-v3` runner is version-bound to Spring Boot 3.x and Spring Framework 6 — its published POM pins `spring-boot-autoconfigure:[3.0.0-RC1,4.0.0)`. On Spring Boot 4.x (Spring Framework 7) the runner silently skips execution without errors. Use `mongock-standalone` with `mongodb-sync-v4-driver` instead, and wire it as a Spring bean. For Spring Boot 3.x projects the `mongock-springboot-v3` + `mongodb-springdata-v4-driver` combination remains valid.

**Good example:**

```xml
<!-- Spring Boot 4.x: standalone runner + raw sync driver -->
<properties>
    <mongock.version>5.5.1</mongock.version>
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
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
    <dependency>
        <groupId>io.mongock</groupId>
        <artifactId>mongock-standalone</artifactId>
    </dependency>
    <dependency>
        <groupId>io.mongock</groupId>
        <artifactId>mongodb-sync-v4-driver</artifactId>
    </dependency>
</dependencies>
```

**Bad example:**

```xml
<!-- Bad on Spring Boot 4.x: this runner is version-bound to Spring Boot 3.x only.
     It compiles and starts without error but silently never executes migrations. -->
<dependency>
    <groupId>io.mongock</groupId>
    <artifactId>mongock-springboot-v3</artifactId>
</dependency>
<dependency>
    <groupId>io.mongock</groupId>
    <artifactId>mongodb-springdata-v4-driver</artifactId>
</dependency>
```


### Example 2: Runner bean (Spring Boot 4.x)

Title: Wire the standalone runner as an ApplicationListener triggered at ApplicationReadyEvent
Description: With the standalone runner there are no `mongock.*` Spring Boot auto-configuration properties. Instead, create a `@Configuration` class that builds and executes the runner through `MongockStandalone.builder()`. Reuse the `MongoClient` bean that Spring Boot auto-configures from the active `spring.data.mongodb.*` properties. Use `@ConditionalOnProperty` to allow tests to disable migrations without touching application code. Disable transactions when the MongoDB instance does not run as a replica set (standalone Docker containers, for example). Mongock 5.5.x logs `setTransactionEnabled(false)` / `transaction-enabled` as deprecated; use the newer `transactional` builder/configuration option when the selected Mongock version exposes it, and document the version-dependent fallback when it does not.

**Good example:**

```java
import com.mongodb.client.MongoClient;
import io.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.mongock.runner.standalone.MongockStandalone;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;

@Configuration
@ConditionalOnProperty(name = "mongock.enabled", havingValue = "true", matchIfMissing = true)
public class MongockConfig {

    @Bean
    ApplicationListener<ApplicationReadyEvent> mongockRunner(
            MongoClient mongoClient,
            MongoDatabaseFactory databaseFactory) {
        String databaseName = databaseFactory.getMongoDatabase().getName();
        return event -> MongockStandalone.builder()
            .setDriver(MongoSync4Driver.withDefaultLock(mongoClient, databaseName))
            .addMigrationScanPackage("com.example.migrations.mongo")
            .setTransactionEnabled(false)
            .buildRunner()
            .execute();
    }
}
```

**Bad example:**

```java
// Bad: running migrations from a controller allows repeated ad hoc execution paths
@RestController
class AdminController {
    @PostMapping("/migrate")
    void migrateFromHttpRequest() {
        mongockRunner.execute(); // races with normal traffic; can run multiple times
    }
}
```


### Example 3: ChangeUnit design

Title: Inject MongoDatabase from the sync driver; avoid repositories or MongoTemplate tied to changing domain models
Description: With the standalone runner and `mongodb-sync-v4-driver`, Mongock injects `MongoDatabase` into `@ChangeUnit` methods — not `MongoTemplate`. Use raw MongoDB collection operations (`Filters`, `Updates`) so the migration remains compilable and stable after the domain model evolves. Never inject repositories or domain records that may change.

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
// Bad: MongoTemplate is not injectable from the standalone sync driver.
// Also couples the migration to a domain record that may evolve and break old migrations.
@ChangeUnit(id = "orders-add-status", order = "001", author = "platform")
class OrdersAddStatusChange {
    @Execution
    void execute(MongoTemplate mongoTemplate) {          // NullPointerException at runtime
        mongoTemplate.updateMulti(
            Query.query(where("status").exists(false)),
            new Update().set("status", "PENDING"),
            OrderDocument.class);
    }
}
```


### Example 4: Testing migrations

Title: Use a project-approved MongoDB test runtime; verify changelog records and side effects
Description: Use `@ServiceConnection` (Spring Boot 3.1+) on a static `MongoDBContainer` field only when the project already defines a pinned, policy-approved MongoDB test image or the user explicitly approves one. Do not introduce an unpinned external image pull inside the skill workflow. `@ServiceConnection` automatically registers the container's connection URI before the application context starts, replacing the unreliable `@DynamicPropertySource` + `getReplicaSetUrl` pattern which fails with Testcontainers 1.19+. Verify that the `mongockChangeLog` collection has EXECUTED records for every expected change unit, and check at least one physical side effect (index or document shape) to confirm the migration ran against real MongoDB. Add `spring-boot-testcontainers`, `org.testcontainers:mongodb`, and `org.testcontainers:junit-jupiter` in test scope; if Maven reports missing Testcontainers versions, import `org.testcontainers:testcontainers-bom` with the project's existing `testcontainers.version` property or add one central property instead of hard-coding inline versions.

**Good example:**

```java
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class MongockMigrationTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = ProjectMongoContainers.approvedMongoDb();

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    void allExpectedMigrationsExecutedSuccessfully() {
        List<Document> changelog = mongoTemplate.findAll(Document.class, "mongockChangeLog");
        List<String> executedIds = changelog.stream()
            .filter(d -> "EXECUTED".equals(d.getString("state")))
            .map(d -> d.getString("changeId"))
            .toList();
        assertThat(executedIds).contains("orders-add-status");
    }

    @Test
    void migrationCreatedStatusIndex() {
        List<Document> indexes = mongoTemplate.getCollection("orders")
            .listIndexes(Document.class).into(new ArrayList<>());
        assertThat(indexes)
            .map(i -> i.get("key", Document.class))
            .anyMatch(key -> key != null && key.containsKey("status"));
    }
}
```

**Bad example:**

```java
// Bad: @DynamicPropertySource + getReplicaSetUrl() does not reliably
// propagate the container URL on Testcontainers 1.19+; the context starts
// with localhost:27017 and Mongock silently never connects.
@DynamicPropertySource
static void props(DynamicPropertyRegistry r) {
    r.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl); // unreliable
}
```


### Example 5: Context tests after enabling Mongock

Title: Disable Mongock only in generic smoke tests; keep a dedicated MongoDB-backed migration test
Description: Adding Spring Data MongoDB plus a startup Mongock runner changes every full `@SpringBootTest` context. Generic `contextLoads` tests that do not provide a MongoDB test runtime will otherwise try `localhost:27017` and fail or hang while Mongock attempts to acquire locks. Keep the real migration proof in a dedicated MongoDB-backed test when a project-approved runtime is available, and explicitly disable Mongock only for unrelated smoke tests.

**Good example:**

```java
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "mongock.enabled=false")
class ApplicationTests {

    @Test
    void contextLoads() {
    }
}
```

**Bad example:**

```java
// Bad after enabling startup Mongock: no MongoDB container and no explicit opt-out.
@SpringBootTest
class ApplicationTests {

    @Test
    void contextLoads() {
    }
}
```


## Output Format

- **ANALYZE** Spring Boot version first: confirm whether the POM parent is 3.x or 4.x — this determines the runner choice before any other step
- **ANALYZE** MongoDB setup: Spring Data MongoDB generation, Mongock BOM version, runner type, driver, scan package, and startup policy
- **ANALYZE** Maven dependency management for MongoDB-backed integration tests before adding them; import `testcontainers-bom` when Testcontainers is already approved and module versions are not already managed
- **ANALYZE** existing full-context tests after enabling startup Mongock; disable Mongock only in unrelated smoke tests and keep dedicated migration tests MongoDB-backed only when an approved runtime is available
- **CATEGORIZE** migration risks (RUNNER COMPATIBILITY, LOCKING, IDEMPOTENCY, DOMAIN COUPLING, TEST GAP) by impact
- **APPLY** version-appropriate runner: standalone runner + sync driver for Spring Boot 4.x; `@ServiceConnection` only for approved MongoDB test-runtime wiring
- **ALIGN** with `@315-frameworks-spring-mongodb` for normal document/repository design while keeping migrations independent
- **VALIDATE** with `./mvnw compile` before changes and `./mvnw clean verify` after changes


## Safeguards

- **RUNNER SPLIT**: `mongock-springboot-v3` compiles and starts on Spring Boot 4.x without errors but never executes migrations. Always verify the Spring Boot major version before choosing the runner
- **DO NOT EDIT APPLIED MIGRATIONS**: Once a `@ChangeUnit` has run in a shared environment, add a new ordered change instead of modifying it
- **IDEMPOTENCY**: Write migrations so interrupted non-transactional execution can safely continue on the next run
- **LOCKING**: Do not hide lock acquisition failures in clustered deployments unless there is a documented external migration coordinator
- **DOMAIN MODEL DRIFT**: Avoid repositories and mutable domain models inside old migrations; use stable raw collection/field operations via `MongoDatabase`
- **TEST ISOLATION**: Generic `@SpringBootTest` smoke tests must either provide MongoDB or set `mongock.enabled=false`; do not let unrelated tests prove migrations by accident
- **EXTERNAL RUNTIME GATE**: Do not pull or execute Docker images for verification unless the user explicitly approves and the image is pinned, policy-approved, or already configured in the project
- **VERSION MANAGEMENT**: Keep Testcontainers versions centralized through an existing project BOM/property or `testcontainers-bom`; do not add inline test dependency versions
- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` before recommending dependency or migration changes