---
name: 400-frameworks-quarkus-create-project
description: Use when creating a new Maven-based Quarkus 3.x project with SDKMAN-managed Java and Quarkus CLI tooling.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Create Quarkus Maven Project

## Role

You are a Senior software engineer with extensive experience in Quarkus, Maven, SDKMAN, and Java enterprise project bootstrapping

## Goal

Create a new Quarkus Maven project in a predictable way. Use SDKMAN to manage Java and the Quarkus CLI, gather project coordinates before running commands, generate the project with Maven as the build tool, and verify the result with the Maven wrapper when available.

## Constraints

New project generation changes the filesystem, so confirm target paths and avoid overwriting user work.

- **PREREQUISITE**: Run `sdk version` before using SDKMAN-managed tooling
- **MISSING SDKMAN**: If SDKMAN is not installed, stop and provide the official installation prerequisite instead of installing it silently
- **JAVA BASELINE**: Prefer Java 25 unless the user requests another supported version
- **FRAMEWORK BASELINE**: Use the current Quarkus 3.x line by default
- **BUILD TOOL**: Generate Maven projects only; do not generate Gradle projects from this skill
- **TARGET DIRECTORY**: Check whether the target directory exists and is empty before project creation
- **EXTENSIONS**: Ask for desired Quarkus extensions when the request does not specify them
- **VERIFY**: Run `./mvnw clean verify` in the generated project when `mvnw` exists; otherwise run `mvn clean verify`

## Steps

### Step 1: Gather project inputs

Collect the target directory, group id, artifact id, package name, Java version, Quarkus platform/version preference, and desired extensions such as `rest`, `hibernate-validator`, `jdbc-postgresql`, or `smallrye-openapi`.### Step 2: Verify SDKMAN and Java

Run `sdk version`, then check Java with `java -version`. If a Java switch is required, use `sdk list java`, `sdk install java <version>`, or `sdk use java <version>` after confirming the selected candidate.### Step 3: Verify Quarkus CLI

Check `quarkus --version`. If the Quarkus CLI is missing, use SDKMAN candidate discovery such as `sdk list quarkus`, then install or use the agreed candidate with `sdk install quarkus <version>` or `sdk use quarkus <version>`.### Step 4: Create the Maven project

Use `quarkus create app` with explicit Maven options, for example `quarkus create app <group>:<artifact>:<version> --maven --java=25 --package-name=<package> --extension=<extensions> --output-directory=<target-directory>`. Keep extension choices explicit and use the Quarkus 3.x CLI/platform defaults unless the user requests a specific compatible version.### Step 5: Verify the generated project

Change into the generated project directory, inspect the generated `pom.xml`, and run `./mvnw clean verify` when the wrapper exists. Report command output failures with the exact failing goal and the next corrective action.### Step 6: Report completion

Summarize the generated path, selected Java version, Quarkus version or platform stream, Maven command used, extensions, and verification result.