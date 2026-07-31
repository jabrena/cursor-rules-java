---
name: 313-frameworks-spring-db-migrations-flyway-antipatterns
description: Review Spring Boot Flyway migrations for data loss, data reinterpretation, repeatable migration surprises, and branch-ordering risks before recommending SQL changes.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Spring — Database migrations (Flyway)

## Role

You are a Senior software engineer who reviews Spring Boot Flyway migrations for production data safety

## Goal

Detect Flyway migration antipatterns before they reach shared environments. Treat migrations as production code and production data changes, not only DDL. For Spring Boot setup, dependency, and `spring.flyway.*` configuration guidance, use `313-frameworks-spring-db-migrations-flyway`.

## Constraints

Escalate risky migrations before editing SQL. When the migration could lose, truncate, overwrite, or reinterpret data, recommend a safer forward-only sequence and verification.

- **DATA SAFETY**: Check existing production-like data before narrowing types, reducing lengths, adding constraints, or changing defaults
- **BRANCH ORDERING**: Detect duplicate version numbers and migrations that assume another branch migration already ran
- **OUT OF ORDER**: Treat `outOfOrder=true` as an exceptional operational decision, not a default fix for branch conflicts
- **REPEATABLE MIGRATIONS**: Review repeatable scripts for destructive operations because checksum changes make them rerun

## Examples

### Table of contents

- Example 1: Data-loss and reinterpretation antipatterns
- Example 2: Concrete Flyway examples to avoid
- Example 3: Branch-ordering antipatterns
- Example 4: Verification antipatterns

### Example 1: Data-loss and reinterpretation antipatterns

Title: Flag changes that preserve DDL intent but damage business data
Description: Watch for drop-and-add column changes when the intent is a rename; type narrowing or length reduction without checking existing values; `NOT NULL` defaults that assign false business meaning to historical rows; default-value changes that unexpectedly alter future inserts; destructive table, relationship-table, constraint, or index rewrites; enum/status meaning changes without an application compatibility plan; timestamp/timezone conversions without verifying existing value interpretation; broad updates without precise `WHERE` clauses and expected row counts; and unique/index changes without duplicate detection and cleanup.

**Good example:**

```sql
-- Rename with data preservation:
ALTER TABLE customers ADD COLUMN given_name VARCHAR(120);
UPDATE customers
SET given_name = first_name
WHERE given_name IS NULL AND first_name IS NOT NULL;
-- A later contract migration drops first_name only after all deployed code reads given_name.

-- Unique/index change with cleanup proof:
-- 1. SELECT lower(email), COUNT(*) FROM customers GROUP BY lower(email) HAVING COUNT(*) > 1;
-- 2. Resolve duplicates intentionally.
-- 3. Add the unique index/constraint after cleanup.
```

**Bad example:**

```sql
-- Bad: loses data when the intent was a rename
ALTER TABLE customers DROP COLUMN first_name;
ALTER TABLE customers ADD COLUMN given_name VARCHAR(120) NOT NULL DEFAULT '';

-- Bad: unsafe broad rewrite with no predicate or row-count expectation
UPDATE orders SET status = 'ARCHIVED';
```


### Example 2: Concrete Flyway examples to avoid

Title: Use these checks when reviewing migration files from pull requests
Description: The migration-risk examples from the source issue map to concrete review questions. Before approving a Spring Boot Flyway migration, check whether it drops data while renaming, narrows values, invents defaults, changes business semantics, rewrites relationship data, or relies on branch ordering. Prefer a forward-only repair sequence and explicit verification over a single destructive migration.

**Good example:**

```text
Review expectations:
- Rename: preserve old values, backfill the new column, and contract later.
- Type/length change: query existing values for truncation or precision loss first.
- Defaults and NOT NULL: confirm the default is correct for historical rows.
- Enum/status/timezone changes: verify application compatibility and data interpretation.
- Repeatable migrations: avoid destructive reruns; scope and make operations idempotent.
- Branch ordering: reject duplicate versions and migrations that depend on missing branch files.
- Updates and indexes: require precise WHERE clauses, row-count expectations, and duplicate checks.
```

**Bad example:**

```sql
-- Bad rename: drops existing data instead of preserving it
ALTER TABLE users DROP COLUMN full_name;
ALTER TABLE users ADD COLUMN name VARCHAR(255);

-- Bad type/length changes: can truncate cents or existing long emails
ALTER TABLE orders ALTER COLUMN amount TYPE INT;
ALTER TABLE users ALTER COLUMN email TYPE VARCHAR(50);

-- Bad historical default: marks every old row as active without business proof
ALTER TABLE users ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- Bad future default: changes new-row behavior silently
ALTER TABLE invoices ALTER COLUMN paid SET DEFAULT true;

-- Bad relationship rewrite: recreates a table that may contain existing links
DROP TABLE user_roles;
CREATE TABLE user_roles (...);

-- Bad semantic change: existing rows keep old numeric values but code reads new meanings
-- 0 used to mean PENDING; new code treats 0 as CANCELLED.

-- Bad time reinterpretation: verify existing values before changing timestamp semantics
-- TIMESTAMP -> TIMESTAMP WITH TIME ZONE

-- Bad repeatable migration: rerun can overwrite real configuration data
-- R__config.sql
DELETE FROM config;
INSERT INTO config (...) VALUES (...);

-- Bad branch ordering: duplicate versions or assumptions about another branch
-- Branch A: V12__add_status.sql
-- Branch B: V12__change_user_table.sql

-- Bad broad update: no WHERE clause or expected row count
UPDATE users SET status = 'DISABLED';

-- Bad unique rule: can fail or force unsafe cleanup if duplicates exist
CREATE UNIQUE INDEX ux_users_email ON users(email);
```


### Example 3: Branch-ordering antipatterns

Title: Prevent duplicate versions and unsafe assumptions across concurrent branches
Description: In parallel development, two branches can create the same Flyway version or create migrations that depend on objects added only by another branch. Do not enable `outOfOrder=true` just to make branch conflicts pass. Prefer CI checks that scan `V*__*.sql` filenames for duplicate versions, run Flyway validation with `validate-on-migrate` enabled, and execute the full migration chain in merge order.

**Good example:**

```text
# CI intent:
# - fail when two files share the same V* prefix
# - run Flyway validate against the merged migration set
# - run the full migration chain from an empty database
# - run risky changes from a previous-release data snapshot
```

**Bad example:**

```text
# Bad: assumes V42 from another branch is already present
V43__add_order_fk.sql references a table introduced only by a different branch's V42.

# Bad: hides ordering mistakes instead of reviewing them
spring.flyway.out-of-order=true
```


### Example 4: Verification antipatterns

Title: Do not confuse application startup with migration safety
Description: A passing Spring context test is not enough. Migration tests should exercise the real Flyway chain and assert preserved values across renames, backfills, defaults, enum/status changes, timezone changes, repeatable migrations, broad updates, and unique/index changes. Prefer Testcontainers with the production dialect over H2 when production uses PostgreSQL, MySQL, or another specific database.

**Good example:**

```java
// Spring integration test intent:
// - Start PostgreSQL/MySQL/etc. with Testcontainers.
// - Run Flyway against an empty schema and assert all migrations apply.
// - Load a previous-release fixture, migrate, then assert preserved business data.
// - Fail fast on duplicate V* prefixes and unexpected out-of-order migrations.
```

**Bad example:**

```java
// Bad: only verifies that @SpringBootTest starts against H2 with schema.sql
// while production uses PostgreSQL and Flyway migrations are never exercised.
```