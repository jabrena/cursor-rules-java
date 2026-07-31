---
name: 112-java-maven-plugins-maven-dependency-plugin
description: Maven Dependency Plugin guidance for declared and undeclared dependency analysis.
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

Configure Maven Dependency only when selected by the user in `112-java-maven-plugins`. Read this reference after the SKILL.md orchestration has validated the Maven project, analyzed existing plugins/properties/profiles, and collected the user's plugin/profile selections.

## Constraints

Apply only the selected Maven Dependency configuration and preserve existing project configuration.

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
<maven-plugin-dependency.version>3.11.0</maven-plugin-dependency.version>
```
            ### Step 2: Maven Dependency Plugin Analysis Configuration

**Purpose**: Configure maven-dependency-plugin to detect unused declared dependencies and used undeclared dependencies during the build.

**Dependencies**: Only execute if user selected "Dependency analysis (maven-dependency-plugin)" in the SKILL.md question flow. Requires completion of the SKILL.md orchestration steps and selected property handling.

**CRITICAL PRESERVATION RULE**: Only ADD this plugin if it doesn't already exist. Never REPLACE or REMOVE existing plugins.

## Pre-Implementation Check

**BEFORE adding maven-dependency-plugin, check if it already exists in the pom.xml:**

If maven-dependency-plugin already exists: Ask user "maven-dependency-plugin already exists. Do you want to enhance the existing configuration? (y/n)"

If user says "n": Skip this step entirely.
If user says "y": Proceed with adding missing configuration elements only.

**CONDITIONAL EXECUTION**: Only execute this step if user selected "Dependency analysis (maven-dependency-plugin)" in the SKILL.md question flow.

## Maven Dependency Plugin Configuration

**ADD this plugin to the `<build><plugins>` section ONLY if it doesn't already exist:**

```xml
<plugin>
<groupId>org.apache.maven.plugins</groupId>
<artifactId>maven-dependency-plugin</artifactId>
<version>${maven-plugin-dependency.version}</version>
<executions>
    <execution>
        <id>analyze-dependencies</id>
        <phase>verify</phase>
        <goals>
            <goal>analyze-only</goal>
        </goals>
        <configuration>
            <failOnWarning>true</failOnWarning>
            <ignoreNonCompile>true</ignoreNonCompile>
        </configuration>
    </execution>
</executions>
</plugin>
```

## Implementation Guidelines

1. **Use `analyze-only` in lifecycle bindings**: The `analyze` goal is useful from the command line, while `analyze-only` participates cleanly in the build lifecycle after classes have been compiled.
2. **Fail on dependency warnings**: `failOnWarning` should be `true` when the team wants unused declared dependencies or used undeclared dependencies to block `verify`.
3. **Reduce false positives for non-compile scopes**: `ignoreNonCompile` avoids failing unused dependency analysis on runtime, provided, test, and system scoped dependencies.
4. **Preserve known exceptions**: If the project intentionally declares dependencies only used reflectively or through generated code, configure the appropriate ignored dependency lists instead of removing the plugin.

## Usage Examples

```bash
# Run dependency analysis directly
./mvnw dependency:analyze

# Run the lifecycle-bound dependency analysis
./mvnw verify
```

## Validation

After adding this plugin, verify the configuration:

```bash
# Test Maven Dependency plugin configuration
./mvnw verify
```
                
            
            
#### Step Constraints

- **MUST** only add maven-dependency-plugin if "Dependency analysis (maven-dependency-plugin)" was selected in the SKILL.md question flow
- **MUST** check if plugin already exists before adding
- **MUST** ask user permission before modifying existing plugin configuration
- **MUST** use selected version properties for plugin version
- **MUST** configure `analyze-only` bound to the `verify` phase for lifecycle execution
- **MUST** enable `failOnWarning` so dependency warnings can fail the build
- **MUST** skip this step entirely if Dependency analysis was not selected