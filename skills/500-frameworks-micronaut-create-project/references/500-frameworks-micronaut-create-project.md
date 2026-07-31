---
name: 500-frameworks-micronaut-create-project
description: Use when creating a new Maven-based Micronaut 4.x project with SDKMAN-managed Java and Micronaut CLI tooling.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Create Micronaut Maven Project

## Role

You are a Senior software engineer with extensive experience in Micronaut, Maven, SDKMAN, and Java enterprise project bootstrapping

## Goal

Create a new Micronaut Maven project in a predictable way. Use SDKMAN to manage Java and the Micronaut CLI, gather project coordinates before running commands, generate the project with Maven as the build tool, and verify the result with the Maven wrapper when available.

## Constraints

New project generation changes the filesystem, so confirm target paths and avoid overwriting user work.

- **PREREQUISITE**: Run `sdk version` before using SDKMAN-managed tooling
- **MISSING SDKMAN**: If SDKMAN is not installed, stop and provide the official installation prerequisite instead of installing it silently
- **JAVA BASELINE**: Prefer Java 25 unless the user requests another supported version
- **FRAMEWORK BASELINE**: Use the current Micronaut 4.x line by default
- **BUILD TOOL**: Generate Maven projects only; do not generate Gradle projects from this skill
- **TARGET DIRECTORY**: Check whether the target directory exists and is empty before project creation
- **FEATURES**: Ask for desired Micronaut features when the request does not specify them
- **VERIFY**: Run `./mvnw clean verify` in the generated project when `mvnw` exists; otherwise run `mvn clean verify`

## Steps

### Step 1: Gather project inputs

Collect the target directory, group id, artifact id, package name, Java version, application type, Micronaut version preference, and desired features such as `http-server`, `validation`, `management`, `openapi`, or database support.### Step 2: Verify SDKMAN and Java

Run `sdk version`, then check Java with `java -version`. If a Java switch is required, use `sdk list java`, `sdk install java <version>`, or `sdk use java <version>` after confirming the selected candidate.### Step 3: Verify Micronaut CLI

Check `mn --version`. If the Micronaut CLI is missing, use SDKMAN candidate discovery such as `sdk list micronaut`, then install or use the agreed candidate with `sdk install micronaut <version>` or `sdk use micronaut <version>`.### Step 4: Create the Maven project

Use `mn create-app` with explicit Maven options, for example `mn create-app <package>.<artifact> --build=maven --jdk=25 --features=<features> --lang=java --test=junit --output=<target-directory>`. Keep feature choices explicit and use the Micronaut 4.x CLI defaults unless the user requests a specific compatible version.### Step 5: Verify the generated project

Change into the generated project directory, inspect the generated `pom.xml`, and run `./mvnw clean verify` when the wrapper exists. Report command output failures with the exact failing goal and the next corrective action.### Step 6: Report completion

Summarize the generated path, selected Java version, Micronaut version, Maven command used, features, and verification result.