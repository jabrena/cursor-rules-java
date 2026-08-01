---
name: 112-java-maven-plugins-spotless-maven-plugin
description: Spotless Maven plugin guidance for source formatting.
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

Configure Spotless only when selected by the user in `112-java-maven-plugins`. Read this reference after the SKILL.md orchestration has validated the Maven project, analyzed existing plugins/properties/profiles, and collected the user's plugin/profile selections.

## Constraints

Apply only the selected Spotless configuration and preserve existing project configuration.

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
<maven-plugin-spotless.version>2.44.5</maven-plugin-spotless.version>
```
            ### Step 2: Spotless Code Formatting Plugin Configuration

**Purpose**: Configure Spotless Maven plugin to automatically format and enforce code style consistency.

**Dependencies**: Only execute if user selected "Format source code (Spotless)" in the SKILL.md question flow. Requires completion of the SKILL.md orchestration steps and selected property handling.

**CRITICAL PRESERVATION RULE**: Only ADD this plugin if it doesn't already exist. Never REPLACE or REMOVE existing plugins.

## Pre-Implementation Check

**BEFORE adding Spotless plugin, check if it already exists in the pom.xml:**

If spotless-maven-plugin already exists: Ask user "Spotless plugin already exists. Do you want to enhance the existing configuration? (y/n)"

If user says "n": Skip this step entirely.
If user says "y": Proceed with adding missing configuration elements only.

**CONDITIONAL EXECUTION**: Only execute this step if user selected "Format source code (Spotless)" in the SKILL.md question flow.

## Spotless Plugin Configuration

**ADD this plugin to the `<build><plugins>` section ONLY if it doesn't already exist:**

```xml
<plugin>
<groupId>com.diffplug.spotless</groupId>
<artifactId>spotless-maven-plugin</artifactId>
<version>${maven-plugin-spotless.version}</version>
<configuration>
    <encoding>UTF-8</encoding>
    <java>
        <removeUnusedImports />
        <importOrder>
            <order>,\#</order>
        </importOrder>
        <endWithNewline />
        <trimTrailingWhitespace />
        <indent>
            <spaces>true</spaces>
            <spacesPerTab>4</spacesPerTab>
        </indent>
    </java>
</configuration>
<executions>
    <execution>
        <goals>
            <goal>check</goal>
        </goals>
        <phase>process-sources</phase>
    </execution>
</executions>
</plugin>
```

## Usage Examples

```bash
# Check code formatting
./mvnw spotless:check

# Apply code formatting
./mvnw spotless:apply

# Integration with build
./mvnw clean compile  # Will fail if formatting violations exist
```

## Validation

After adding this plugin, verify the configuration:

```bash
# Test Spotless configuration
./mvnw spotless:check
```
                
            
            
#### Step Constraints

- **MUST** only add Spotless plugin if code formatting was selected in the SKILL.md question flow
- **MUST** check if plugin already exists before adding
- **MUST** ask user permission before modifying existing plugin configuration
- **MUST** use selected version properties for plugin versions
- **MUST** skip this step entirely if code formatting was not selected