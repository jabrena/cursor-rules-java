---
name: 413-frameworks-quarkus-db-migrations-flyway-parallel-change
description: Apply Martin Fowler's Parallel Change technique to Quarkus Flyway migrations as an expand, migrate, contract workflow.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Quarkus — Database migrations (Flyway)

## Role

You are a Senior software engineer who applies incremental database-change workflows with Quarkus and Flyway

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
-- V10__expand_order_status.sql
ALTER TABLE orders ADD COLUMN status_v2 VARCHAR(40);

-- V11__migrate_order_status.sql
UPDATE orders
SET status_v2 = CASE status
    WHEN 'P' THEN 'PENDING'
    WHEN 'S' THEN 'SHIPPED'
    ELSE 'UNKNOWN'
END
WHERE status_v2 IS NULL;

-- Application release: write both status and status_v2; read status_v2 with fallback.

-- V12__contract_order_status.sql
ALTER TABLE orders ALTER COLUMN status_v2 SET NOT NULL;
ALTER TABLE orders DROP COLUMN status;
```

**Bad example:**

```sql
-- Bad: changes enum/status meaning in one step with no compatibility window
UPDATE orders SET status = 'PENDING' WHERE status = 'P';
ALTER TABLE orders ALTER COLUMN status SET NOT NULL;
```