---
name: 516-frameworks-micronaut-mongodb-migrations-mongock
description: Use when you need to add or review Mongock MongoDB data migrations in a Micronaut application — including Mongock runner/driver selection, Micronaut bean wiring, migration scan packages, @ChangeUnit classes, lock/transaction settings, and Testcontainers verification. This should trigger for requests such as Add Mongock migrations in Micronaut; Review Micronaut MongoDB data migrations; Configure Mongock change units with Micronaut Data MongoDB; Create Mongock change units for Micronaut MongoDB; Review Mongock migration ordering in a Micronaut service. Part of Plinth Toolkit
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Micronaut - MongoDB migrations (Mongock)

Apply Mongock migration guidelines for Micronaut and Micronaut Data MongoDB.

**What is covered in this Skill?**

- Mongock runner/driver selection for Micronaut projects
- Micronaut bean wiring for MongoDB client access and controlled migration execution
- Code-first `@ChangeUnit` migrations with `@Execution` and rollback hooks
- Locking, idempotency, transaction limits, and startup/runtime trade-offs
- Integration tests with `@MicronautTest` and Testcontainers MongoDB
- Coordination with `@515-frameworks-micronaut-mongodb`

**Scope:** Apply recommendations based on the reference rules and good/bad examples.

## Constraints

Compile before Mongock or MongoDB migration changes; verify after changes.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **SAFETY**: If compilation fails, stop immediately
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements
- **BEFORE APPLYING**: Read the reference for detailed rules and good/bad patterns
- **EDGE CASE**: If a Micronaut-specific Mongock runner is unavailable or incompatible, use an explicit standalone runner bean/job and document that trade-off

## When to use this skill

- Add Mongock migrations in Micronaut
- Review Micronaut MongoDB data migrations
- Configure Mongock change units with Micronaut Data MongoDB
- Create Mongock change units for Micronaut MongoDB
- Review Mongock migration ordering in a Micronaut service

## Workflow

1. **Read references and inspect MongoDB setup**

Read `references/516-frameworks-micronaut-mongodb-migrations-mongock.md`, `references/516-frameworks-micronaut-mongodb-migrations-mongock-antipatterns.md`, and `references/516-frameworks-micronaut-mongodb-migrations-mongock-parallel-change.md`, then inspect `pom.xml`, Micronaut MongoDB configuration, and existing `@515-frameworks-micronaut-mongodb` persistence patterns.

2. **Choose runner, driver, and execution policy**

Select compatible Mongock coordinates, wire the runner through Micronaut beans, configure migration scan packages, and decide startup vs controlled-job execution.

3. **Apply framework-aligned migrations**

Implement/refactor `@ChangeUnit` classes with idempotent operations, explicit ordering, rollback hooks, and safe lock/transaction settings.

4. **Run verification and report results**

Execute build/tests, including `@MicronautTest` with Testcontainers MongoDB where feasible, and summarize outcomes and follow-up risks.

## Reference

For detailed guidance, examples, and constraints, see:

- [references/516-frameworks-micronaut-mongodb-migrations-mongock.md](references/516-frameworks-micronaut-mongodb-migrations-mongock.md)
- [references/516-frameworks-micronaut-mongodb-migrations-mongock-antipatterns.md](references/516-frameworks-micronaut-mongodb-migrations-mongock-antipatterns.md)
- [references/516-frameworks-micronaut-mongodb-migrations-mongock-parallel-change.md](references/516-frameworks-micronaut-mongodb-migrations-mongock-parallel-change.md)
