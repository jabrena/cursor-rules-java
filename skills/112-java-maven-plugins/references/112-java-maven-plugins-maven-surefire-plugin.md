---
name: 112-java-maven-plugins-maven-surefire-plugin
description: Maven Surefire plugin guidance for unit test execution.
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

Configure Maven Surefire only when selected by the user in `112-java-maven-plugins`. Read this reference after the SKILL.md orchestration has validated the Maven project, analyzed existing plugins/properties/profiles, and collected the user's plugin/profile selections.

## Constraints

Apply only the selected Maven Surefire configuration and preserve existing project configuration.

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
            ### Step 2: Maven Surefire Plugin Configuration

**Purpose**: Configure maven-surefire-plugin for unit testing with proper includes/excludes and failure handling.

**Dependencies**: Only execute if user selected "Unit Testing (Surefire)" in the SKILL.md question flow. Requires completion of the SKILL.md orchestration steps and selected property handling.

**CRITICAL PRESERVATION RULE**: Only ADD this plugin if it doesn't already exist. Never REPLACE or REMOVE existing configuration.

**CONDITIONAL EXECUTION**: Only execute this step if user selected "Unit Testing (Surefire)" in the SKILL.md question flow.

## Pre-Implementation Check

**BEFORE adding maven-surefire-plugin, check if it already exists in the pom.xml:**

If maven-surefire-plugin already exists: Ask user "maven-surefire-plugin already exists. Do you want to enhance the existing configuration? (y/n)"

If user says "n": Skip this step entirely.
If user says "y": Proceed with adding missing configuration elements only.

## Maven Surefire Plugin Configuration

**ADD this plugin to the `<build><plugins>` section ONLY if it doesn't already exist:**

```xml
<plugin>
<groupId>org.apache.maven.plugins</groupId>
<artifactId>maven-surefire-plugin</artifactId>
<version>${maven-plugin-surefire.version}</version>
<configuration>
    <skipAfterFailureCount>1</skipAfterFailureCount>
    <includes>
        <include>**/*Test.java</include>
    </includes>
    <excludes>
        <exclude>**/*IT.java</exclude>
    </excludes>
</configuration>
</plugin>
```

## Validation

After adding this plugin, verify the configuration:

```bash
# Run unit tests
./mvnw test
```
                
            
            
#### Step Constraints

- **MUST** only add surefire plugin if "Unit Testing (Surefire)" was selected in the SKILL.md question flow
- **MUST** use selected version properties for plugin versions
- **MUST** check if plugin already exists before adding
- **MUST** ask user permission before modifying existing plugin configuration
- **MUST** configure proper includes/excludes for test file patterns
- **MUST** skip this step entirely if unit testing was not selected