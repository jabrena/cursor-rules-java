---
name: 416-frameworks-quarkus-mongodb-migrations-mongock
description: Use when you need to add or review Mongock MongoDB data migrations in a Quarkus application — including the Quarkiverse Mongock extension, Quarkus MongoDB client configuration, migrate-at-start, @ChangeUnit classes, lock/transaction settings, and Quarkus test verification. This should trigger for requests such as Add Mongock migrations in Quarkus; Configure quarkus-mongock; Review Quarkus MongoDB data migrations; Create Mongock change units for Quarkus MongoDB; Review Mongock migration ordering in a Quarkus service. Part of Plinth Toolkit
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Quarkus - MongoDB migrations (Mongock)

Apply Mongock migration guidelines for Quarkus and Quarkus MongoDB client/Panache Mongo.

**What is covered in this Skill?**

- Quarkiverse `quarkus-mongock` extension and Quarkus MongoDB client setup
- `quarkus.mongock.*` configuration, including `migrate-at-start`
- Code-first `@ChangeUnit` migrations with `@Execution` and rollback hooks
- Locking, idempotency, transaction limits, and default-client constraints
- Quarkus tests with Dev Services or Testcontainers MongoDB
- Coordination with `@415-frameworks-quarkus-mongodb`

**Scope:** Apply recommendations based on the reference rules and good/bad examples.

## Constraints

Compile before Mongock or MongoDB migration changes; verify after changes.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **SAFETY**: If compilation fails, stop immediately
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements
- **BEFORE APPLYING**: Read the reference for detailed rules and good/bad patterns
- **EXTENSION COMPATIBILITY**: Before adding `quarkus-mongock`, verify the latest Quarkiverse Mongock release and Quarkus extension catalog compatibility with the active Quarkus platform. If the latest extension targets a significantly older Quarkus version, stop and document the incompatibility instead of applying changes.
- **EDGE CASE**: If the application uses multiple MongoDB clients, verify Quarkiverse Mongock support before applying a default-client migration design

## When to use this skill

- Add Mongock migrations in Quarkus
- Configure quarkus-mongock
- Review Quarkus MongoDB data migrations
- Create Mongock change units for Quarkus MongoDB
- Review Mongock migration ordering in a Quarkus service

## Workflow

1. **Read references and inspect MongoDB setup**

Read `references/416-frameworks-quarkus-mongodb-migrations-mongock.md`, `references/416-frameworks-quarkus-mongodb-migrations-mongock-antipatterns.md`, and `references/416-frameworks-quarkus-mongodb-migrations-mongock-parallel-change.md`, then inspect `pom.xml`, Quarkus MongoDB configuration, and existing `@415-frameworks-quarkus-mongodb` persistence patterns.

2. **Verify extension compatibility and execution policy**

Verify the Quarkiverse `quarkus-mongock` release and Quarkus extension catalog compatibility before configuring MongoDB connection settings, migration scan behavior, and startup vs explicit `MongockFactory` execution.

3. **Apply framework-aligned migrations**

Implement/refactor `@ChangeUnit` classes with idempotent operations, explicit ordering, rollback hooks, and safe lock/transaction settings.

4. **Run verification and report results**

Execute build/tests, including Quarkus tests with Dev Services or Testcontainers MongoDB where feasible, and summarize outcomes and follow-up risks.

## Reference

For detailed guidance, examples, and constraints, see:

- [references/416-frameworks-quarkus-mongodb-migrations-mongock.md](references/416-frameworks-quarkus-mongodb-migrations-mongock.md)
- [references/416-frameworks-quarkus-mongodb-migrations-mongock-antipatterns.md](references/416-frameworks-quarkus-mongodb-migrations-mongock-antipatterns.md)
- [references/416-frameworks-quarkus-mongodb-migrations-mongock-parallel-change.md](references/416-frameworks-quarkus-mongodb-migrations-mongock-parallel-change.md)
