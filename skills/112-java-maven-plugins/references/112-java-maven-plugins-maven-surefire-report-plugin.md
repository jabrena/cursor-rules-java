---
name: 112-java-maven-plugins-maven-surefire-report-plugin
description: Maven Surefire Report plugin guidance for HTML unit test reports.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Maven Plugins: pom.xml Configuration Best Practices

## Role

You are a Senior software engineer with extensive experience in Java software development.

## Tone

Treat the user as a knowledgeable partner. Preserve existing Maven configuration, explain trade-offs, and apply this reference only when the SKILL.md question flow selected this plugin or profile.

## Goal

Configure Maven Surefire Report only when selected by the user in `112-java-maven-plugins`. Read this reference after the SKILL.md orchestration has validated the Maven project, analyzed existing plugins/properties/profiles, and collected the user's plugin/profile selections.

## Constraints

Apply only the selected Maven Surefire Report configuration and preserve existing project configuration.

- Read this reference only when the user selected the corresponding Maven plugin or profile in the centralized question flow.
- Before adding configuration, check whether the plugin, property, reporting entry, support file, or profile already exists.
- Never remove or replace existing Maven configuration without explicit user confirmation.
- Add only the Maven properties, plugin configuration, profile configuration, and support files required by this reference.

## Steps

### Step 1: Add selected Maven properties

Add only the properties needed for this selected plugin/profile. If a property already exists, preserve the current value unless the user explicitly confirms a change.

```xml
<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
<maven.version>3.9.10</maven.version>
<java.version>[USER_SELECTED_JAVA_VERSION]</java.version>
<maven-plugin-surefire.version>3.5.3</maven-plugin-surefire.version>
```
            ### Step 2: Maven Surefire Report Plugin Configuration

**Purpose**: Configure `maven-surefire-report-plugin` to generate HTML unit test reports.

**Dependencies**: Only execute if the user selected "Unit Testing Reports (Surefire Reports)" in the SKILL.md question flow.

**CRITICAL PRESERVATION RULE**: Only add the reporting plugin if it does not already exist. Never replace or remove existing reporting configuration without user confirmation.

## Pre-Implementation Check

Before adding `maven-surefire-report-plugin`, check whether `<reporting>` and this reporting plugin already exist.

If `<reporting>` exists, ask the user whether to enhance it with the missing Surefire report plugin.

## Maven Surefire Report Plugin Configuration

```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-report-plugin</artifactId>
            <version>${maven-plugin-surefire.version}</version>
            <configuration>
                <outputName>junit-report</outputName>
                <showSuccess>true</showSuccess>
            </configuration>
        </plugin>
    </plugins>
</reporting>
```

## Usage Examples

```bash
./mvnw site
open target/site/junit-report.html
./mvnw site:run
```

## Validation

```bash
./mvnw clean test site
```
            
#### Step Constraints

- **MUST** only add maven-surefire-report-plugin if unit test reports were selected in the SKILL.md question flow
- **MUST** check if reporting configuration already exists before adding
- **MUST** ask user permission before modifying existing reporting configuration
- **MUST** use selected version properties