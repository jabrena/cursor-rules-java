---
name: 111-java-maven-dependencies-javamoney
description: JavaMoney Maven dependency guidance for the interactive Maven dependencies skill.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Add Maven dependencies for improved code quality

## Role

You are a Senior software engineer with extensive experience in Java software development.

## Goal

Add JavaMoney only when the user selects Money and Currency API support in the SKILL.md question flow. Use JavaMoney when the domain needs explicit monetary amounts, currencies, formatting, conversion, or precision-safe money calculations instead of primitive numbers or ad hoc `BigDecimal` fields.

JavaMoney reference: https://javamoney.github.io/

## Constraints

JavaMoney is an application dependency for domain models that handle money and currency values.

- Add JavaMoney only when selected and preserve the existing POM structure.
- Use Moneta as the JSR 354 reference implementation for application runtime support.
- Use the API-only dependency only for modules that intentionally expose or compile against JSR 354 contracts without bundling an implementation.
- Do not add both API-only and Moneta dependencies unless the project structure explicitly needs separate API and runtime modules.
- Do not add JavaMoney when the user skipped Money and Currency API support.

## Steps

### Step 1: Add selected version property

For application modules that need a JSR 354 implementation, add:

```xml
<javamoney-moneta.version>1.4.5</javamoney-moneta.version>
```

For API-only modules, add `money-api.version` only when the module should compile against the JSR 354 API without providing a runtime implementation:

```xml
<money-api.version>1.1</money-api.version>
```
### Step 2: Add JavaMoney dependency

For application modules, add Moneta, the JSR 354 reference implementation:

```xml
<dependency>
    <groupId>org.javamoney</groupId>
    <artifactId>moneta</artifactId>
    <version>${javamoney-moneta.version}</version>
    <type>pom</type>
</dependency>
```

For API-only modules, add the API dependency instead:

```xml
<dependency>
    <groupId>javax.money</groupId>
    <artifactId>money-api</artifactId>
    <version>${money-api.version}</version>
</dependency>
```
### Step 3: Report domain-modeling trade-offs

Mention JavaMoney provides the Java Money and Currency API (JSR 354) and Moneta reference implementation. Recommend JavaMoney when the project needs monetary amounts, currencies, formatting, currency conversion, or precision-safe calculations.

If the project only needs storage of a fixed decimal value without currency semantics, explain that a simpler `BigDecimal` plus explicit currency field may be enough. If the project handles business money rules, prefer explicit monetary types over primitive numbers or unlabelled decimal fields.