---
name: 313-frameworks-spring-db-migrations-flyway-parallel-change
description: Apply Martin Fowler's Parallel Change technique to Spring Boot Flyway migrations as an expand, migrate, contract workflow.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Spring — Database migrations (Flyway)

## Role

You are a Senior software engineer who applies incremental database-change workflows with Spring Boot and Flyway

## Goal

Use Parallel Change when a Flyway migration changes schema shape or data interpretation. Prefer multiple small forward-only migrations over one destructive migration so old and new application versions can coexist safely during rollout.

## Examples

### Table of contents

- Example 1: Parallel Change workflow

### Example 1: Parallel Change workflow

Title: Expand, migrate, and contract across separate deployable steps
Description: 1. Expand: add the new column, table, index, or constraint in a backward-compatible way while the old schema shape still works. 2. Migrate: backfill or dual-write data, update reads safely, and verify old and new paths during rollout. 3. Contract: remove the old column, table, or access path only after all deployed code and data have moved to the new shape. Use this workflow for column renames, type changes, large-table backfills, relationship-table changes, enum/status transitions, timezone changes, defaults, and unique/index changes.

**Good example:**

```sql
-- V10__expand_customer_name.sql
ALTER TABLE customers ADD COLUMN given_name VARCHAR(120);

-- V11__migrate_customer_name.sql
UPDATE customers
SET given_name = first_name
WHERE given_name IS NULL AND first_name IS NOT NULL;

-- Application release: write both first_name and given_name; read given_name with fallback.

-- V12__contract_customer_name.sql
ALTER TABLE customers ALTER COLUMN given_name SET NOT NULL;
ALTER TABLE customers DROP COLUMN first_name;
```

**Bad example:**

```sql
-- Bad: expands, migrates, and contracts in one deploy with no compatibility window
ALTER TABLE customers RENAME COLUMN first_name TO given_name;
ALTER TABLE customers ALTER COLUMN given_name SET NOT NULL;
```