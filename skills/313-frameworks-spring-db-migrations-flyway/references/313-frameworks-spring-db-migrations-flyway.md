---
name: 313-frameworks-spring-db-migrations-flyway
description: Use when you need to add or review Flyway database migrations in a Spring Boot application — including Maven dependencies, `classpath:db/migration` scripts, versioning (`V{version}__{description}.sql`), configuration via `spring.flyway.*`, baseline and repair for existing databases, validation in CI, and coordination with JDBC (`@311-frameworks-spring-jdbc`) or Spring Data JDBC (`@312-frameworks-spring-data-jdbc`). Focus on Flyway; for ORM schema generation use the stack’s Hibernate integration instead of mixing approaches blindly.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Spring — Database migrations (Flyway)

## Role

You are a Senior software engineer with extensive experience in Spring Boot, relational databases, and schema evolution with Flyway

## Goal

Ship additive, repeatable schema changes as versioned SQL (or Java) migrations under `src/main/resources/db/migration`, let Flyway run them on startup (or in a controlled job), and keep application code aligned with the live schema. Prefer explicit DDL in migrations over ad-hoc manual changes in shared environments. Pair migrations with integration tests that exercise repositories or JDBC against a real dialect. For programmatic JDBC patterns use `@311-frameworks-spring-jdbc`; for Spring Data JDBC entities and repositories use `@312-frameworks-spring-data-jdbc`.

## Constraints

Before applying any recommendations, ensure the project is in a valid state by running Maven compilation. Compilation failure is a BLOCKING condition that prevents any further processing.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Project must compile successfully before Flyway or SQL edits
- **CRITICAL SAFETY**: If compilation fails, IMMEDIATELY STOP and DO NOT CONTINUE
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements; include migration smoke tests where feasible

## Examples

### Table of contents

- Example 1: Maven coordinates
- Example 2: Versioned SQL scripts
- Example 3: Spring Boot configuration
- Example 4: Optional Java migrations
- Example 5: Migration smoke test

### Example 1: Maven coordinates

Title: Use Spring Boot Flyway support; add database-specific Flyway modules when required by your dialect
Description: Spring Boot manages Flyway versions via its BOM. For Spring Boot 4.x, prefer `spring-boot-starter-flyway` so Boot contributes Flyway auto-configuration and creates the `Flyway` bean. Pair it with JDBC/DataSource support as needed. For PostgreSQL and recent Flyway releases, add the matching `flyway-database-postgresql` artifact so Flyway can talk to the dialect. Keep JDBC driver and Flyway versions compatible with your Spring Boot BOM.

**Good example:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-flyway</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

**Bad example:**

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>8.0.0</version>
</dependency>
```


### Example 2: Versioned SQL scripts

Title: Place scripts under `db/migration`; use Flyway’s `V{version}__{description}.sql` convention
Description: One migration file per logical change. Use sequential versions; never reuse a version number that already ran in an environment. Prefer idempotent-friendly DDL where the database allows (e.g. `IF NOT EXISTS` where supported) but do not “fix” a broken migration by editing it after deploy — add a new forward migration instead.

**Good example:**

```sql
-- src/main/resources/db/migration/V1__create_customers.sql
CREATE TABLE customers (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(320) NOT NULL UNIQUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- src/main/resources/db/migration/V2__add_customer_name.sql
ALTER TABLE customers
    ADD COLUMN first_name VARCHAR(120) NOT NULL DEFAULT '',
    ADD COLUMN last_name  VARCHAR(120) NOT NULL DEFAULT '';
```

**Bad example:**

```sql
-- Bad: ambiguous naming / duplicate version prefix — breaks ordering or fails validation
-- db/migration/create_customers.sql (missing V1__ prefix)
-- V1__customers.sql and V1__orders.sql (duplicate V1)
```


### Example 3: Spring Boot configuration

Title: Control locations, baseline, and validation with `spring.flyway.*`
Description: Default location is `classpath:db/migration`. For brownfield databases, `spring.flyway.baseline-on-migrate=true` establishes a baseline without replaying history. Keep `validate-on-migrate` enabled in production unless you have a documented exception. Use placeholders for environment-specific names only when truly necessary.

**Good example:**

```properties
# application.properties (examples)
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true
```

**Bad example:**

```properties
# Bad: disabling validation in production hides checksum drift and risky edits
spring.flyway.validate-on-migrate=false
```


### Example 4: Optional Java migrations

Title: Use Java-based migrations when SQL alone is insufficient; keep them small and testable
Description: Implement `org.flywaydb.core.api.migration.BaseJavaMigration` (or `JavaMigration`) for data backfills that need procedural logic. Prefer SQL for DDL; reserve Java for complex data fixes with clear logging and idempotence where possible.

**Good example:**

```java
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import java.sql.Statement;

public class V3__BackfillCustomerDisplayName extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        try (Statement st = context.getConnection().createStatement()) {
            st.executeUpdate("""
                UPDATE customers
                SET display_name = trim(coalesce(first_name, '') || ' ' || coalesce(last_name, ''))
                WHERE display_name IS NULL
                """);
        }
    }
}
```

**Bad example:**

```java
// Bad: embedding environment-specific secrets or one-off manual steps inside a migration
public class V3__LoadProdData extends BaseJavaMigration {
    public void migrate(Context context) {
        throw new UnsupportedOperationException("Run this manually in prod"); // never ship this
    }
}
```


### Example 5: Migration smoke test

Title: Verify Spring Boot wires Flyway and applies the migration chain
Description: Add or update a Spring Boot test that proves Flyway auto-configuration is active. The test should load the application context, inject `Flyway`, assert that expected migrations are applied, and verify the migrated schema through JDBC. Prefer Testcontainers with the production database dialect when production does not use an embedded database.

**Good example:**

```java
@SpringBootTest
class FlywayMigrationTest {

    @Autowired
    Flyway flyway;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void appliesCustomerMigration() {
        assertThat(flyway.info().applied()).hasSize(1);

        Integer customerTableCount = jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM INFORMATION_SCHEMA.TABLES
            WHERE TABLE_NAME = 'CUSTOMERS'
            """,
            Integer.class);

        assertThat(customerTableCount).isEqualTo(1);
    }
}
```

**Bad example:**

```java
// Bad: only checks that the web context starts; it never proves Flyway ran.
@SpringBootTest
class ApplicationTests {

    @Test
    void contextLoads() {
    }
}
```


## Output Format

- **ANALYZE** current Flyway setup: dependencies, locations, Spring properties, migration history strategy, and alignment with JDBC/Data JDBC code
- **CATEGORIZE** gaps (MISSING MODULE, NAMING, CONFIG, OPERATIONS) and risk (data loss, downtime, checksum mismatch)
- **APPLY** Spring Boot–aligned fixes: correct artifacts, script layout, `spring.flyway.*` tuning, and forward-only migration discipline
- **COORDINATE** with `@311-frameworks-spring-jdbc` / `@312-frameworks-spring-data-jdbc` so entities and SQL match applied migrations
- **SAFEGUARD DATA** by checking renames, type changes, defaults, enum/status changes, timezone changes, repeatable migrations, broad updates, and unique/index changes for preservation risks
- **TEST** with integration tests hitting a real database (e.g. Testcontainers) after migration changes; include a Spring Boot migration smoke test that injects `Flyway` and asserts applied migrations
- **CHECK ORDERING** for duplicate versions, branch-dependent migrations, and unsafe `outOfOrder=true` assumptions
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after changes


## Safeguards

- **IMMUTABLE HISTORY**: Do not edit migrations that already ran in shared environments; add a new versioned script to correct schema or data
- **CHECKSUMS**: Changing a applied file breaks `validate-on-migrate` — use repair only with operational awareness
- **ROLLBACK**: Flyway does not auto-rollback; plan forward migrations (or manual runbooks) for reversibility
- **LOCKS**: Long DDL can block traffic — schedule risky changes and prefer online migration patterns for large tables
- **PARALLEL CHANGE**: Expand, migrate, and contract across separate deployable steps for renames, type changes, backfills, and relationship-table changes
- **BRANCH ORDERING**: Detect duplicate versions and branch-only dependencies in CI; treat `outOfOrder=true` as an exceptional operational choice
- **SECRETS**: Never put secrets inside migration sources committed to Git
- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` before recommending schema or build changes