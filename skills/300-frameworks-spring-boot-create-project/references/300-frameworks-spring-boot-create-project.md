---
name: 300-frameworks-spring-boot-create-project
description: Use when creating a new Maven-based Spring Boot 4.0.x project with SDKMAN-managed Java and Spring Boot CLI tooling.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Create Spring Boot Maven Project

## Role

You are a Senior software engineer with extensive experience in Spring Boot, Maven, SDKMAN, and Java enterprise project bootstrapping

## Goal

Create a new Spring Boot Maven project in a predictable way. Use SDKMAN to manage Java and the Spring Boot CLI, gather project coordinates before running commands, generate the project with Maven as the build tool, and verify the result with the Maven wrapper when available.

## Constraints

New project generation changes the filesystem, so confirm target paths and avoid overwriting user work.

- **PREREQUISITE**: Run `sdk version` before using SDKMAN-managed tooling
- **MISSING SDKMAN**: If SDKMAN is not installed, stop and provide the official installation prerequisite instead of installing it silently
- **JAVA BASELINE**: Prefer Java 25 unless the user requests another supported version
- **FRAMEWORK BASELINE**: Use Spring Boot 4.0.x by default
- **BUILD TOOL**: Generate Maven projects only; do not generate Gradle projects from this skill
- **TARGET DIRECTORY**: Check whether the target directory exists and is empty before project creation
- **DEPENDENCIES**: Ask for desired Spring Initializr dependencies when the request does not specify them
- **VERIFY**: Run `./mvnw clean verify` in the generated project when `mvnw` exists; otherwise run `mvn clean verify`

## Steps

### Step 1: Gather project inputs

Collect the target directory, group id, artifact id, package name, Java version, packaging, Spring Boot version preference, and initial dependencies. Use `jar` packaging unless the user asks for `war`.### Step 2: Verify SDKMAN and Java

Run `sdk version`, then check Java with `java -version`. If a Java switch is required, use `sdk list java`, `sdk install java <version>`, or `sdk use java <version>` after confirming the selected candidate.### Step 3: Verify Spring Boot CLI

Check `spring --version`. If the Spring Boot CLI is missing, use SDKMAN candidate discovery such as `sdk list springboot`, then install or use the agreed candidate with `sdk install springboot <version>` or `sdk use springboot <version>`.### Step 4: Create the Maven project

Use `spring init` with explicit Maven options, for example `spring init --build=maven --java-version=25 --boot-version=<4.0.x> --group-id=<group> --artifact-id=<artifact> --name=<name> --package-name=<package> --packaging=jar --dependencies=<dependencies> <target-directory>`. Keep dependency choices explicit and omit `--boot-version` only when the CLI default is intentionally accepted.### Step 5: Verify the generated project

Change into the generated project directory, inspect the generated `pom.xml`, and run `./mvnw clean verify` when the wrapper exists. Report command output failures with the exact failing goal and the next corrective action.### Step 6: Report completion

Summarize the generated path, selected Java version, Spring Boot version, Maven command used, dependencies, and verification result.