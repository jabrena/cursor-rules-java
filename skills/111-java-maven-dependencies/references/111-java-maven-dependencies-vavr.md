---
name: 111-java-maven-dependencies-vavr
description: VAVR Maven dependency guidance for the interactive Maven dependencies skill.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Add Maven dependencies for improved code quality

## Role

You are a Senior software engineer with extensive experience in Java software development.

## Goal

Add VAVR only when the user selects functional programming support in the SKILL.md question flow.

## Constraints

VAVR is an application dependency. Add it only when selected and preserve the existing POM structure.

- Use default Maven compile scope for VAVR.
- Do not add VAVR when the user skipped functional programming support.

## Steps

### Step 1: Add selected version property

```xml
<vavr.version>0.10.6</vavr.version>
```
### Step 2: Add VAVR dependency

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr</artifactId>
    <version>${vavr.version}</version>
</dependency>
```
### Step 3: Report common usage

Mention VAVR is useful for `Try`, `Either`, and immutable collections. Recommend adding examples only if the user asks or if the change introduces VAVR into existing code.