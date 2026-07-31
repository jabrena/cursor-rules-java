---
name: 112-java-maven-plugins-maven-compiler-plugin
description: Maven Compiler plugin guidance for source and target release configuration.
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

Configure Maven Compiler only when selected by the user in `112-java-maven-plugins`. Read this reference after the SKILL.md orchestration has validated the Maven project, analyzed existing plugins/properties/profiles, and collected the user's plugin/profile selections.

## Constraints

Apply only the selected Maven Compiler configuration and preserve existing project configuration.

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
<maven-plugin-compiler.version>3.13.0</maven-plugin-compiler.version>
<java.version>[USER_SELECTED_JAVA_VERSION]</java.version>
```
            ### Step 2: Maven Compiler Plugin Configuration

**Purpose**: Configure maven-compiler-plugin with explicit Java release, strict lint options, and treat warnings as errors for code quality.

**Dependencies**: Only execute if user selected "Maven Compiler" in the SKILL.md question flow. Requires completion of the SKILL.md orchestration steps and selected property handling.

**CRITICAL PRESERVATION RULE**: Only ADD this plugin if it doesn't already exist. Never REPLACE or REMOVE existing configuration.

## Pre-Implementation Check

**BEFORE adding maven-compiler-plugin, check if it already exists in the pom.xml:**

If maven-compiler-plugin already exists: Ask user "maven-compiler-plugin already exists. Do you want to enhance the existing configuration? (y/n)"

If user says "n": Skip this step entirely.
If user says "y": Proceed with adding missing configuration elements only.

**CONDITIONAL EXECUTION**: Only execute this step if user selected "Maven Compiler" in the SKILL.md question flow.

## Maven Compiler Plugin Configuration

**ADD this plugin to the `<build><plugins>` section ONLY if it doesn't already exist:**

```xml
<!-- Maven Compiler Plugin -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>${maven-plugin-compiler.version}</version>
    <configuration>
        <release>${java.version}</release>
        <compilerArgs>
            <arg>-Xlint:all</arg>
            <arg>-Werror</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

## Implementation Guidelines

1. **release**: Uses `${java.version}` from properties - ensures consistent Java target across the build
2. **-Xlint:all**: Enables all compiler warnings for potential code quality issues
3. **-Werror**: Treats compiler warnings as errors - prevents shipping code with warnings

## Usage Examples

```bash
# Compile with strict settings
./mvnw clean compile

# Verify compilation succeeds (warnings will fail the build)
./mvnw clean package
```

## Validation

After adding this plugin, verify the configuration:

```bash
# Test Maven Compiler plugin
./mvnw clean compile
```
                
            
            
#### Step Constraints

- **MUST** only add maven-compiler-plugin if "Maven Compiler" was selected in the SKILL.md question flow
- **MUST** check if plugin already exists before adding
- **MUST** ask user permission before modifying existing plugin configuration
- **MUST** use selected version properties for plugin version and java.version
- **MUST** skip this step entirely if Maven Compiler was not selected