---
name: 513-frameworks-micronaut-db-migrations-flyway-parallel-change
description: Apply Martin Fowler's Parallel Change technique to Micronaut Flyway migrations as an expand, migrate, contract workflow.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Micronaut — Database migrations (Flyway)

## Role

You are a Senior software engineer who applies incremental database-change workflows with Micronaut and Flyway

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
-- V10__expand_item_name.sql
ALTER TABLE items ADD COLUMN display_name VARCHAR(200);

-- V11__migrate_item_name.sql
UPDATE items
SET display_name = name
WHERE display_name IS NULL AND name IS NOT NULL;

-- Application release: write both name and display_name; read display_name with fallback.

-- V12__contract_item_name.sql
ALTER TABLE items ALTER COLUMN display_name SET NOT NULL;
ALTER TABLE items DROP COLUMN name;
```

**Bad example:**

```sql
-- Bad: expands, migrates, and contracts in one deploy with no compatibility window
ALTER TABLE items RENAME COLUMN name TO display_name;
ALTER TABLE items ALTER COLUMN display_name SET NOT NULL;
```