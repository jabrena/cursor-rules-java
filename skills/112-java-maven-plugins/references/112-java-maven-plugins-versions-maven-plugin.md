---
name: 112-java-maven-plugins-versions-maven-plugin
description: Versions Maven plugin guidance for dependency and plugin version management.
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

Configure Versions Maven only when selected by the user in `112-java-maven-plugins`. Read this reference after the SKILL.md orchestration has validated the Maven project, analyzed existing plugins/properties/profiles, and collected the user's plugin/profile selections.

## Constraints

Apply only the selected Versions Maven configuration and preserve existing project configuration.

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
<maven-plugin-versions.version>2.18.0</maven-plugin-versions.version>
```
            ### Step 2: Versions Maven Plugin Configuration

**Purpose**: Configure Versions Maven plugin to help manage and update dependency and plugin versions systematically.

**Dependencies**: Only execute if user selected "Version management" in the SKILL.md question flow. Requires completion of the SKILL.md orchestration steps and selected property handling.

**CRITICAL PRESERVATION RULE**: Only ADD this plugin if it doesn't already exist. Never REPLACE or REMOVE existing plugins.

## Pre-Implementation Check

**BEFORE adding Versions plugin, check if it already exists in the pom.xml:**

If versions-maven-plugin already exists: Ask user "Versions plugin already exists. Do you want to enhance the existing configuration? (y/n)"

If user says "n": Skip this step entirely.
If user says "y": Proceed with adding missing configuration elements only.

**CONDITIONAL EXECUTION**: Only execute this step if user selected "Version management" in the SKILL.md question flow.

## Versions Plugin Configuration

**ADD this plugin to the `<build><plugins>` section ONLY if it doesn't already exist:**

```xml
<plugin>
<groupId>org.codehaus.mojo</groupId>
<artifactId>versions-maven-plugin</artifactId>
<version>${maven-plugin-versions.version}</version>
<configuration>
    <allowSnapshots>false</allowSnapshots>
</configuration>
</plugin>
```

## Usage Examples

```bash
# Check for dependency updates
./mvnw versions:display-dependency-updates

# Check for plugin updates
./mvnw versions:display-plugin-updates

# Check for property updates
./mvnw versions:display-property-updates

# Update to next snapshot versions
./mvnw versions:set -DnextSnapshot=true

# Update specific dependency
./mvnw versions:use-latest-versions -Dincludes=org.junit.jupiter:*
```

## Validation

After adding this plugin, verify the configuration:

```bash
# Test Versions plugin configuration
./mvnw versions:display-plugin-updates
```
                
            
            
#### Step Constraints

- **MUST** only add Versions plugin if version management was selected in the SKILL.md question flow
- **MUST** check if plugin already exists before adding
- **MUST** ask user permission before modifying existing plugin configuration
- **MUST** use selected version properties for plugin versions
- **MUST** skip this step entirely if version management was not selected