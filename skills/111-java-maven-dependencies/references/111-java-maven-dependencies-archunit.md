---
name: 111-java-maven-dependencies-archunit
description: ArchUnit Maven dependency guidance for the interactive Maven dependencies skill.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Add Maven dependencies for improved code quality

## Role

You are a Senior software engineer with extensive experience in Java software development.

## Goal

Add ArchUnit only when the user selects architecture testing support in the SKILL.md question flow.

## Constraints

ArchUnit is a test dependency used to enforce architectural rules in the normal test lifecycle.

- Use `test` scope for ArchUnit.
- Use `archunit-junit5` when JUnit 5 is the test framework.
- Do not add ArchUnit when the user skipped architecture testing support.

## Steps

### Step 1: Add selected version property

```xml
<archunit.version>1.4.1</archunit.version>
```
### Step 2: Add ArchUnit JUnit 5 dependency

```xml
<dependency>
    <groupId>com.tngtech.archunit</groupId>
    <artifactId>archunit-junit5</artifactId>
    <version>${archunit.version}</version>
    <scope>test</scope>
</dependency>
```
### Step 3: Validate architecture tests

After adding ArchUnit, recommend running:

```bash
./mvnw test
```

If an architecture test is added, a focused check can use:

```bash
./mvnw test -Dtest="*ArchitectureTest"
```