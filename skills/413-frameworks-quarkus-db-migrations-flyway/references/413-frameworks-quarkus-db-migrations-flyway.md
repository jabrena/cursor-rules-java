---
name: 413-frameworks-quarkus-db-migrations-flyway
description: Use when you need to add or review Flyway database migrations in a Quarkus application — including the `quarkus-flyway` extension, `classpath:db/migration` scripts, `V{version}__{description}.sql` naming, `quarkus.flyway.*` configuration, migrate-at-start behavior, and coordination with JDBC (`@411-frameworks-quarkus-jdbc`) or Hibernate ORM with Panache (`@412-frameworks-quarkus-panache`). Focus on Flyway-driven schema evolution, not hand-applied DDL in production.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Quarkus — Database migrations (Flyway)

## Role

You are a Senior software engineer with extensive experience in Quarkus, relational databases, and schema evolution with Flyway

## Goal

Use the Quarkus Flyway extension to apply versioned migrations from `src/main/resources/db/migration` on startup (when configured) or from build/test workflows, keeping the database schema in sync with Panache entities or JDBC access layers. Prefer small, forward-only migrations; align DDL with entity mappings and repository queries. For raw JDBC usage see `@411-frameworks-quarkus-jdbc`; for Panache entities and repositories see `@412-frameworks-quarkus-panache`.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully before Flyway or SQL edits
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements

## Examples

### Table of contents

- Example 1: Extension and JDBC driver
- Example 2: SQL migrations
- Example 3: application.properties
- Example 4: Multiple datasources (optional)
- Example 5: First Flyway migration in a Quarkus project

### Example 1: Extension and JDBC driver

Title: Add `quarkus-flyway` alongside the JDBC driver extension for your database
Description: Quarkus Flyway integrates with Agroal datasources. Declare `quarkus-flyway` in addition to `quarkus-jdbc-postgresql` (or another `quarkus-jdbc-*`). Use the Quarkus BOM so versions stay aligned. Dev Services can provision a database in dev/test while migrations still run from the same scripts.

**Good example:**

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-flyway</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-jdbc-postgresql</artifactId>
</dependency>
```

**Bad example:**

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-flyway</artifactId>
</dependency>
```


### Example 2: SQL migrations

Title: Default location `db/migration`; follow Flyway versioned naming
Description: Store scripts under `src/main/resources/db/migration`. Use consecutive versions; split unrelated changes into separate files. Keep DDL consistent with Panache entity fields (`@Column`, `@Version`) and JDBC row mappers.

**Good example:**

```sql
-- src/main/resources/db/migration/V1__create_orders.sql
CREATE TABLE orders (
    id           BIGSERIAL PRIMARY KEY,
    customer_id  BIGINT NOT NULL,
    placed_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version      INT NOT NULL DEFAULT 0
);
```

**Bad example:**

```sql
-- Bad: non-versioned file — Flyway will not pick it up from default locations
-- src/main/resources/db/migration/orders.sql
```


### Example 3: application.properties

Title: Enable migrate-at-start and tune locations for your module layout
Description: `quarkus.flyway.migrate-at-start=true` runs migrations when the application starts — typical for services. For tests, the same scripts apply when using `@QuarkusTest` with a datasource (including Dev Services or Testcontainers). Use baseline settings when attaching to an existing database with objects already present.

**Good example:**

```properties
# application.properties (examples)
quarkus.flyway.migrate-at-start=true
quarkus.flyway.locations=classpath:db/migration
# When introducing Flyway to a DB that already has schema:
# quarkus.flyway.baseline-on-migrate=true
# quarkus.flyway.baseline-version=1
```

**Bad example:**

```properties
# Bad: migrate-at-start=false in production without another controlled migration path
quarkus.flyway.migrate-at-start=false
```


### Example 4: Multiple datasources (optional)

Title: Configure Flyway per named datasource when using more than one
Description: When multiple Agroal datasources exist, scope Flyway settings per datasource name (e.g. `%named` config keys in Quarkus). Keep migration directories or table names distinct per datasource to avoid clashes.

**Good example:**

```properties
# Illustrative pattern — exact keys follow Quarkus Flyway extension docs for your version
# quarkus.flyway.orders.migrate-at-start=true
# quarkus.flyway.orders.locations=classpath:db/migration/orders
```

**Bad example:**

```properties
# Bad: two datasources but only one Flyway location — ambiguous or wrong schema gets migrated
quarkus.datasource.orders.jdbc.url=jdbc:postgresql://localhost/orders
quarkus.datasource.audit.jdbc.url=jdbc:postgresql://localhost/audit
quarkus.flyway.locations=classpath:db/migration
```


### Example 5: First Flyway migration in a Quarkus project

Title: Bootstrap the datasource, Flyway extension, migration folder, and production-dialect verification together
Description: When a Quarkus project has no datasource, no JDBC or Panache persistence layer, and no Flyway setup yet, treat the request as a bootstrap plus migration change. Add `quarkus-flyway` together with the matching `quarkus-jdbc-*` driver, configure `quarkus.flyway.migrate-at-start=true` and `classpath:db/migration`, then create a versioned migration under `src/main/resources/db/migration`. For data-sensitive changes such as `status` to `status_v2`, keep the first migration in the expand phase: add the new nullable column, backfill from the old value, keep the old column available, and defer `NOT NULL` plus dropping the old column to later deployable contract migrations. Verify the migration with the production database dialect when feasible. In Quarkus tests, Dev Services or Testcontainers can start PostgreSQL, MySQL, or another matching database without hardcoded datasource URLs. For semantic changes, add or recommend a previous-release fixture test that loads representative old rows, runs Flyway, and asserts the business meaning is preserved after migration.

**Good example:**

```sql
-- src/main/resources/db/migration/V1__expand_order_status_v2.sql
ALTER TABLE orders ADD COLUMN status_v2 VARCHAR(40);

UPDATE orders
SET status_v2 = CASE status
    WHEN 'P' THEN 'PENDING'
    WHEN 'S' THEN 'SHIPPED'
    WHEN 'C' THEN 'CANCELLED'
    ELSE 'UNKNOWN'
END
WHERE status_v2 IS NULL;

-- Later deployable steps:
-- 1. Release code that writes both status and status_v2 and reads status_v2 with fallback.
-- 2. Verify previous-release data snapshots keep the same business meaning.
-- 3. Contract only after rollout: SET NOT NULL on status_v2, then DROP status.
```

**Bad example:**

```sql
-- Bad: combines expand, semantic rewrite, and contract in one migration
UPDATE orders SET status = 'PENDING' WHERE status = 'P';
ALTER TABLE orders ADD COLUMN status_v2 VARCHAR(40) NOT NULL DEFAULT 'PENDING';
ALTER TABLE orders DROP COLUMN status;
```


## Output Format

- **ANALYZE** Quarkus Flyway setup: extensions, datasource config, migration locations, migrate-at-start, and test profile behavior
- **CATEGORIZE** issues (EXTENSION, CONFIG, SCRIPT, ENTITY MISMATCH) by impact
- **BOOTSTRAP** first-time Flyway projects by adding `quarkus-flyway`, the matching `quarkus-jdbc-*` driver, `quarkus.flyway.*` configuration, and `src/main/resources/db/migration` together
- **APPLY** Quarkus-aligned fixes: correct `pom.xml`, `application.properties`, and forward-only SQL
- **ALIGN** with `@412-frameworks-quarkus-panache` entities or `@411-frameworks-quarkus-jdbc` SQL
- **SAFEGUARD DATA** by checking renames, type changes, defaults, enum/status changes, timezone changes, repeatable migrations, broad updates, and unique/index changes for preservation risks
- **TEST** with `@QuarkusTest` and a real production-dialect database using Dev Services or Testcontainers after migration changes; for semantic changes, include previous-release fixture data that proves business meaning is preserved
- **CHECK ORDERING** for duplicate versions, branch-dependent migrations, and unsafe `outOfOrder=true` assumptions
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after changes


## Safeguards

- **FORWARD ONLY**: Never rewrite migrations that already applied in shared environments
- **PANACHE SYNC**: Adding `@Version` or columns requires matching DDL in Flyway before rollout
- **DEV SERVICES**: Dev databases are ephemeral — do not treat them as migration history sources of truth
- **PARALLEL CHANGE**: Expand, migrate, and contract across separate deployable steps for renames, type changes, backfills, relationship-table changes, and enum/status meaning changes
- **FIRST SETUP**: If no datasource or migration chain exists, add Flyway, the JDBC driver, configuration, and versioned migration layout as one coherent bootstrap; avoid hardcoded test datasource URLs when Dev Services or Testcontainers can provide the production dialect
- **BRANCH ORDERING**: Detect duplicate versions and branch-only dependencies in CI; treat `outOfOrder=true` as an exceptional operational choice
- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` before recommending schema or build changes