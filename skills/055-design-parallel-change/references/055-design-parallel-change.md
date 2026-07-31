---
name: 055-design-parallel-change
description: Apply Parallel Change to database migration scenarios as an expand, migrate, and contract workflow before choosing framework-specific Flyway implementation guidance.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Parallel Change Design

## Role

You are a senior Java Enterprise engineer who designs database migrations so old and new application versions can coexist safely.

## Tone

Be practical, cautious, and explicit. Explain the compatibility window, data-preservation risk, verification signal, and cleanup trigger before recommending SQL or framework implementation details.

## Goal

Apply Parallel Change to database migration scenarios. Use expand, migrate, and contract when a migration changes schema shape or data interpretation and old and new application versions may overlap during deployment or rollback.

## Constraints

Design the migration strategy first. Framework-specific Flyway skills implement the selected approach later.

- **PATTERN FIRST**: Decide whether the migration needs Parallel Change before editing framework-specific Flyway files
- **SEPARATE DEPLOYABLE STEPS**: Keep expand, migrate, and contract independently deployable when a compatibility window is required
- **DATA PRESERVATION**: Do not drop, overwrite, narrow, reinterpret, or deduplicate production data without explicit verification and an owner decision
- **ROLLBACK COMPATIBILITY**: During expand and migrate, preserve old read/write paths or fallback behavior so rollback does not require rolling back the database
- **CONTRACT ONLY AFTER EVIDENCE**: Remove old columns, tables, relationships, constraints, indexes, or access paths only after code rollout and data migration evidence are complete
- **COMPLEMENT FLYWAY SKILLS**: Use `313-frameworks-spring-db-migrations-flyway`, `413-frameworks-quarkus-db-migrations-flyway`, or `513-frameworks-micronaut-db-migrations-flyway` for framework wiring and Flyway test details

## Steps

### Step 1: Classify the Change

Identify the migration pressure before choosing the pattern:

- Schema shape: column, table, relationship, index, constraint, default, or generated value
- Data meaning: enum/status value, timezone interpretation, numeric precision, unit, encoding, or historical default
- Runtime coupling: application versions, workers, readers, writers, reports, analytics, batch jobs, or external integrations
- Operational window: rolling deploy, blue/green deploy, canary, rollback expectation, lock duration, and table size
- Evidence gap: production-like data sample, duplicate checks, previous-release fixture, and row-count expectations

Use the smallest safe approach, but do not treat DDL success as proof that business data is preserved.
### Step 2: Choose the Migration Strategy

Use Parallel Change when old and new shapes or meanings must coexist across deployment boundaries. Common triggers include:

- Column rename or split/merge where old code still reads the old column
- Type, precision, length, unit, timezone, enum, or status meaning changes
- Large-table backfills where a single transaction, lock, or startup migration is unsafe
- Relationship-table or foreign-key changes that preserve existing associations
- Default or `NOT NULL` changes that could assign false meaning to old rows
- Unique/index changes that require duplicate detection, cleanup, or online index behavior

A simpler single migration is usually sufficient when the change is additive, immediately safe, and compatible with all deployed code. Examples: creating a new empty table unused by old code, adding a nullable column that no current code reads, adding a non-unique index online where the database supports it, or correcting test-only schema before shared environments see it.
### Step 3: Design Expand, Migrate, Contract

For Parallel Change, split the work into separate deployable steps:

1. Expand: add the new column, table, relationship, index, constraint, or access path in a backward-compatible way while the old schema shape still works. Avoid immediate `NOT NULL`, destructive defaults, or drops unless existing data proves they are safe.
2. Migrate: backfill or dual-write data, update reads safely, compare old and new paths, and verify rollout behavior. Prefer idempotent batches, precise predicates, expected row counts, and previous-release fixtures for semantic changes.
3. Contract: remove the old column, table, relationship, constraint, index, or access path only after all deployed code and data have moved to the new shape and rollback no longer depends on the old shape.

Name the contract trigger before starting. Examples: all services deployed past version N, no reads from the old column for seven days, backfill reached 100 percent, duplicate cleanup approved, or a DBA confirms online index completion.
### Step 4: State Tradeoffs and Verification

Parallel Change reduces deployment coupling and data-loss risk, but it is not free. State the tradeoffs explicitly:

- Extra migrations and delayed cleanup
- Temporary dual reads, dual writes, fallbacks, or triggers
- More tests and monitoring for old/new path equivalence
- Rollout coordination across services, jobs, and reporting consumers
- Operational complexity for backfills, lock management, and index creation

Verification should match the risk. Use production-dialect migration tests, previous-release fixture rows, duplicate checks before uniqueness changes, backfill row counts, checksums or sampled comparisons, and monitoring that proves the old path can be removed.

## Examples

### Table of contents

- Example 1: Column rename
- Example 2: Type or data reinterpretation
- Example 3: Large-table backfill
- Example 4: Relationship-table change
- Example 5: Index or uniqueness change

### Example 1: Column rename

Title: Preserve the old column until every deployed reader and writer uses the new name
Description: A physical `RENAME COLUMN` can break old code during rolling deployment. Prefer adding the new column, copying data, deploying code that reads the new column with fallback and writes both paths, then dropping the old column after evidence shows it is unused.

**Good example:**

```sql
-- Expand
ALTER TABLE customers ADD COLUMN display_name VARCHAR(200);

-- Migrate
UPDATE customers
SET display_name = full_name
WHERE display_name IS NULL AND full_name IS NOT NULL;

-- Application release: read display_name with fallback to full_name; write both.

-- Contract, in a later deploy after old code is gone
ALTER TABLE customers ALTER COLUMN display_name SET NOT NULL;
ALTER TABLE customers DROP COLUMN full_name;
```

**Bad example:**

```sql
-- Bad: breaks old code and may force database rollback during app rollback
ALTER TABLE customers RENAME COLUMN full_name TO display_name;
```


### Example 2: Type or data reinterpretation

Title: Use a new representation when old values need explicit semantic mapping
Description: Type and meaning changes can pass DDL checks while corrupting business interpretation. Use a parallel representation when old values need mapping, precision changes, unit changes, enum/status transitions, or timezone reinterpretation.

**Good example:**

```sql
-- Expand
ALTER TABLE orders ADD COLUMN status_v2 VARCHAR(40);

-- Migrate with explicit old-to-new meaning
UPDATE orders
SET status_v2 = CASE status
    WHEN 'P' THEN 'PENDING'
    WHEN 'S' THEN 'SHIPPED'
    WHEN 'C' THEN 'CANCELLED'
    ELSE 'UNKNOWN'
END
WHERE status_v2 IS NULL;

-- Contract later only after code reads status_v2 and old values are verified.
```

**Bad example:**

```sql
-- Bad: mutates meaning in place with no old/new comparison path
UPDATE orders SET status = 'PENDING' WHERE status = 'P';
```


### Example 3: Large-table backfill

Title: Separate schema expansion from chunked data movement and verification
Description: For large tables, a single migration that adds a non-null column with a heavy backfill can lock the table or exceed deployment windows. Expand cheaply, backfill in controlled batches or jobs, and contract after coverage and runtime behavior are proven.

**Good example:**

```sql
-- Expand
ALTER TABLE invoices ADD COLUMN amount_cents BIGINT;

-- Migrate in batches through an operational job or repeatable idempotent script
UPDATE invoices
SET amount_cents = CAST(amount * 100 AS BIGINT)
WHERE amount_cents IS NULL
  AND id BETWEEN :batch_start AND :batch_end;

-- Verification: expected remaining NULL count reaches zero before contract.
-- Contract later: SET NOT NULL, remove old reads, then drop amount.
```

**Bad example:**

```sql
-- Bad: one deploy couples table rewrite, semantic conversion, and constraint
ALTER TABLE invoices ADD COLUMN amount_cents BIGINT NOT NULL DEFAULT 0;
UPDATE invoices SET amount_cents = CAST(amount * 100 AS BIGINT);
ALTER TABLE invoices DROP COLUMN amount;
```


### Example 4: Relationship-table change

Title: Copy associations to the new relationship shape before dropping the old table
Description: Relationship rewrites can silently lose many-to-many links, ordering, audit columns, or implied defaults. Add the new relationship structure alongside the old one, copy links with conflict handling, dual-write during rollout, and remove the old table only after consumers have moved.

**Good example:**

```sql
-- Expand
CREATE TABLE account_permissions (
    account_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    source_role_id BIGINT,
    PRIMARY KEY (account_id, permission_id)
);

-- Migrate existing links without dropping user_roles
INSERT INTO account_permissions (account_id, permission_id, source_role_id)
SELECT ur.user_id, rp.permission_id, ur.role_id
FROM user_roles ur
JOIN role_permissions rp ON rp.role_id = ur.role_id
ON CONFLICT DO NOTHING;

-- Contract later after code no longer reads user_roles for permissions.
```

**Bad example:**

```sql
-- Bad: destroys existing links before the replacement is verified
DROP TABLE user_roles;
CREATE TABLE account_permissions (...);
```


### Example 5: Index or uniqueness change

Title: Detect duplicates and use online/concurrent creation when the database supports it
Description: Unique constraints and indexes can fail deployment, block writes, or force unsafe cleanup. First detect duplicates and decide how to resolve them. When supported by the database, create new indexes online or concurrently, then switch application assumptions and remove obsolete indexes later.

**Good example:**

```sql
-- Expand/check
SELECT lower(email) AS normalized_email, COUNT(*)
FROM users
GROUP BY lower(email)
HAVING COUNT(*) > 1;

-- Migrate: resolve duplicates with a business-approved cleanup plan.

-- Expand after cleanup, using dialect-supported online behavior when available
CREATE UNIQUE INDEX CONCURRENTLY ux_users_email_normalized
ON users (lower(email));

-- Contract later: remove the old non-unique index after queries use the new path.
```

**Bad example:**

```sql
-- Bad: can fail in production or block writes without duplicate evidence
CREATE UNIQUE INDEX ux_users_email ON users(email);
```


## Output Format

- **CLASSIFY** the migration risk: schema shape, data meaning, old/new application overlap, table size, rollout style, and rollback expectation
- **DECIDE** whether Parallel Change is needed or a simpler single forward-only migration is sufficient
- **SEQUENCE** expand, migrate, and contract as separate deployable steps when a compatibility window is required
- **VERIFY** with production-dialect migration tests, previous-release fixtures, duplicate checks, row counts, sampled comparisons, and monitoring appropriate to the risk
- **ROUTE** framework-specific implementation to Spring Boot, Quarkus, or Micronaut Flyway skills after the strategy is clear
- **REPORT** tradeoffs, cleanup trigger, skipped checks, owner decisions, and remaining risks


## Safeguards

- Do not recommend dropping an old database shape in the same deploy that introduces its replacement when old code may still run
- Do not assign historical defaults, narrow types, reinterpret statuses, or change timezone semantics without checking existing values and business meaning
- Do not treat framework startup or DDL success as sufficient migration safety evidence
- Do not use Parallel Change to add unnecessary ceremony to a small additive migration that is already compatible
- Do not replace Spring Boot, Quarkus, or Micronaut Flyway setup guidance with this design skill; use both when implementation is in scope
- Do not claim expand, migrate, or contract validation passed unless the checks were actually run or clearly reported as planned but not executed