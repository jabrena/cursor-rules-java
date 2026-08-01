---
name: 112-java-maven-plugins-flatten-maven-plugin
description: Flatten Maven plugin guidance for library publishing POMs.
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

Configure Flatten Maven only when selected by the user in `112-java-maven-plugins`. Read this reference after the SKILL.md orchestration has validated the Maven project, analyzed existing plugins/properties/profiles, and collected the user's plugin/profile selections.

## Constraints

Apply only the selected Flatten Maven configuration and preserve existing project configuration.

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
<maven-plugin-flatten.version>1.7.0</maven-plugin-flatten.version>
```
            ### Step 2: Flatten Maven Plugin Configuration

**Purpose**: Configure Flatten Maven plugin to flatten POM files for library publishing to Maven repositories.

**Dependencies**: Only execute if user selected "Java Library" as project nature in the SKILL.md question flow. Requires completion of the SKILL.md orchestration steps and selected property handling.

**CRITICAL PRESERVATION RULE**: Only ADD this plugin if it doesn't already exist. Never REPLACE or REMOVE existing plugins.

## Pre-Implementation Check

**BEFORE adding Flatten plugin, check if it already exists in the pom.xml:**

If flatten-maven-plugin already exists: Ask user "Flatten plugin already exists. Do you want to enhance the existing configuration? (y/n)"

If user says "n": Skip this step entirely.
If user says "y": Proceed with adding missing configuration elements only.

**CONDITIONAL EXECUTION**: Only execute this step if user selected "Java Library" as project nature in the SKILL.md question flow.

## Flatten Plugin Configuration

**ADD this plugin to the `<build><plugins>` section ONLY if it doesn't already exist:**

```xml
<plugin>
<groupId>org.codehaus.mojo</groupId>
<artifactId>flatten-maven-plugin</artifactId>
<version>${maven-plugin-flatten.version}</version>
<configuration>
</configuration>
<executions>
    <execution>
        <id>flatten</id>
        <phase>process-resources</phase>
        <goals>
            <goal>flatten</goal>
        </goals>
    </execution>
    <execution>
        <id>flatten.clean</id>
        <phase>clean</phase>
        <goals>
            <goal>clean</goal>
        </goals>
    </execution>
</executions>
</plugin>
```

## Usage Examples

```bash
# Build library with flattened POM
./mvnw clean package

# The flattened POM will be in target/
cat target/.flattened-pom.xml

# Deploy to repository
./mvnw clean deploy

# Clean flattened files
./mvnw flatten:clean
```

## Validation

After adding this plugin, verify the configuration:

```bash
# Test Flatten plugin
./mvnw clean package
ls target/.flattened-pom.xml
```
                
            
            
#### Step Constraints

- **MUST** only add Flatten plugin if "Java Library" was selected as project nature in the SKILL.md question flow
- **MUST** check if plugin already exists before adding
- **MUST** ask user permission before modifying existing plugin configuration
- **MUST** use selected version properties for plugin versions
- **MUST** skip this step entirely if project nature is not "Java Library"