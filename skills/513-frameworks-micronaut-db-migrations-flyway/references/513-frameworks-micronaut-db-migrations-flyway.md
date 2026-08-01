---
name: 513-frameworks-micronaut-db-migrations-flyway
description: Use when you need to add or review Flyway database migrations in a Micronaut application — including `micronaut-flyway` (or Flyway integration aligned with your Micronaut BOM), `classpath:db/migration` scripts, `V{version}__{description}.sql` naming, per-datasource Flyway configuration, and coordination with JDBC (`@511-frameworks-micronaut-jdbc`) or Micronaut Data (`@512-frameworks-micronaut-data`). Focus on repeatable, versioned schema evolution.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Micronaut — Database migrations (Flyway)

## Role

You are a Senior software engineer with extensive experience in Micronaut, relational databases, and schema evolution with Flyway

## Goal

Configure Flyway to run migrations from `src/main/resources/db/migration` against Micronaut-managed datasources, keeping the relational schema aligned with Micronaut Data entities or hand-written JDBC. Use forward-only, numbered migrations; avoid manual DDL in production except through controlled operational procedures. For raw JDBC see `@511-frameworks-micronaut-jdbc`; for repositories and `@MappedEntity` types see `@512-frameworks-micronaut-data`.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully before Flyway or SQL edits
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements
- **TESTCONTAINERS REQUIRED**: Verify Flyway migration chains with `@MicronautTest` and Testcontainers for the target production database dialect; do not substitute H2 or hand-written test DDL for migration safety checks

## Examples

### Table of contents

- Example 1: Maven dependency
- Example 2: Migration scripts
- Example 3: Configuration
- Example 4: Tests

### Example 1: Maven dependency

Title: Add Micronaut Flyway support alongside JDBC and your database driver
Description: Use the Micronaut BOM to align versions. Combine `micronaut-flyway` with `micronaut-jdbc-hikari` (or your datasource module) and a JDBC driver. The exact coordinate names follow your Micronaut minor version — verify against current Micronaut Launch / documentation when upgrading.

**Good example:**

```xml
<dependency>
    <groupId>io.micronaut.flyway</groupId>
    <artifactId>micronaut-flyway</artifactId>
</dependency>
<dependency>
    <groupId>io.micronaut.sql</groupId>
    <artifactId>micronaut-jdbc-hikari</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Bad example:**

```xml
<dependency>
    <groupId>io.micronaut.flyway</groupId>
    <artifactId>micronaut-flyway</artifactId>
</dependency>
```


### Example 2: Migration scripts

Title: Default `classpath:db/migration` and Flyway naming
Description: Place SQL under `src/main/resources/db/migration`. Use versioned filenames. Align columns with `@MappedEntity` fields, including `@Version` columns for optimistic locking.

**Good example:**

```sql
-- src/main/resources/db/migration/V1__create_items.sql
CREATE TABLE items (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(200) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);
```

**Bad example:**

```sql
-- Bad: editing V1 after it ran in staging — checksum failure on next startup
-- (instead add V2__fix_items.sql)
```


### Example 3: Configuration

Title: Enable Flyway per datasource under `flyway.datasources.*`
Description: Micronaut Flyway typically scopes settings per datasource (e.g. `default`). Enable migrations for environments that should apply DDL automatically; use profiles for dev vs prod if you need different behavior. Set `baseline-on-migrate` when attaching Flyway to a database that already contains tables.

**Good example:**

```yaml
# application.yml (illustrative — keys per Micronaut Flyway module docs)
datasources:
  default:
    url: jdbc:postgresql://localhost:5432/app
    username: app
    password: app
    driver-class-name: org.postgresql.Driver
flyway:
  datasources:
    default:
      enabled: true
      locations: classpath:db/migration
```

**Bad example:**

```yaml
# Bad: Flyway enabled but locations point to a path with no scripts — silent no-op or failure
flyway:
  datasources:
    default:
      enabled: true
      locations: classpath:missing/migrations
```


### Example 4: Tests

Title: Run migrations against Testcontainers-backed datasources in integration tests
Description: Always use `@MicronautTest` with a Testcontainers-managed database for the target production database dialect and the same migration scripts as production. Do not substitute H2 or duplicate schema in hand-written `schema.sql` for migration safety checks.

**Good example:**

```java
// TestPropertyProvider or test resources: point default datasource to a Testcontainers JDBC URL
// matching the production dialect, for example PostgreSQL:
// jdbc:tc:postgresql:16:///app
// Flyway runs on context start when enabled — exercises the real migration chain.
```

**Bad example:**

```java
// Bad: @MicronautTest with in-memory H2 or hand-written DDL while production uses PostgreSQL + Flyway
// — drifts and hides migration failures.
```


## Output Format

- **ANALYZE** Micronaut Flyway wiring: BOM dependencies, datasource config, `flyway.datasources.*`, and migration locations
- **CATEGORIZE** gaps (DEPENDENCY, CONFIG, SCRIPT, TEST DRIFT) by severity
- **APPLY** Micronaut-aligned fixes: coordinates, YAML/properties, and versioned SQL
- **ALIGN** with `@512-frameworks-micronaut-data` or `@511-frameworks-micronaut-jdbc` access patterns
- **SAFEGUARD DATA** by checking renames, type changes, defaults, enum/status changes, timezone changes, repeatable migrations, broad updates, and unique/index changes for preservation risks
- **TEST** with `@MicronautTest` and Testcontainers mirroring the target production database dialect
- **CHECK ORDERING** for duplicate versions, branch-dependent migrations, and unsafe `outOfOrder=true` assumptions
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after changes


## Safeguards

- **IMMUTABLE APPLIED MIGRATIONS**: Do not modify files after they have run in shared environments
- **DATASOURCE SCOPE**: Configure Flyway for each datasource that needs independent schema
- **TESTCONTAINERS ONLY**: Migration verification must run against a Testcontainers-managed database matching the target production dialect; H2 and hand-written test DDL are not acceptable substitutes
- **PARALLEL CHANGE**: Expand, migrate, and contract across separate deployable steps for renames, type changes, backfills, and relationship-table changes
- **BRANCH ORDERING**: Detect duplicate versions and branch-only dependencies in CI; treat `outOfOrder=true` as an exceptional operational choice
- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` before recommending schema or build changes