---
name: 112-java-maven-plugins-git-commit-id-maven-plugin
description: Git Commit ID Maven plugin guidance for build traceability metadata.
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

Configure Git Commit ID only when selected by the user in `112-java-maven-plugins`. Read this reference after the SKILL.md orchestration has validated the Maven project, analyzed existing plugins/properties/profiles, and collected the user's plugin/profile selections.

## Constraints

Apply only the selected Git Commit ID configuration and preserve existing project configuration.

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
<maven-plugin-git-commit-id.version>4.9.10</maven-plugin-git-commit-id.version>
<project.build.outputDirectory>[VERIFY_VERSION]</project.build.outputDirectory>
```
            ### Step 2: Git Commit ID Plugin Configuration

**Purpose**: Configure Git Commit ID plugin to include Git commit information in the build artifacts for traceability.

**Dependencies**: Only execute if user selected "Build information tracking" in the SKILL.md question flow. Requires completion of the SKILL.md orchestration steps and selected property handling.

**CRITICAL PRESERVATION RULE**: Only ADD this plugin if it doesn't already exist. Never REPLACE or REMOVE existing plugins.

## Pre-Implementation Check

**BEFORE adding Git Commit ID plugin, check if it already exists in the pom.xml:**

If git-commit-id-plugin already exists: Ask user "Git Commit ID plugin already exists. Do you want to enhance the existing configuration? (y/n)"

If user says "n": Skip this step entirely.
If user says "y": Proceed with adding missing configuration elements only.

**CONDITIONAL EXECUTION**: Only execute this step if user selected "Build information tracking" in the SKILL.md question flow.

## Git Commit ID Plugin Configuration

**ADD this plugin to the `<build><plugins>` section ONLY if it doesn't already exist:**

```xml
<plugin>
<groupId>pl.project13.maven</groupId>
<artifactId>git-commit-id-plugin</artifactId>
<version>${maven-plugin-git-commit-id.version}</version>
<executions>
    <execution>
        <id>get-the-git-infos</id>
        <goals>
            <goal>revision</goal>
        </goals>
        <phase>initialize</phase>
    </execution>
</executions>
<configuration>
    <generateGitPropertiesFile>true</generateGitPropertiesFile>
    <generateGitPropertiesFilename>${project.build.outputDirectory}/git.properties</generateGitPropertiesFilename>
    <commitIdGenerationMode>full</commitIdGenerationMode>
</configuration>
</plugin>
```

## Usage Examples

```bash
# Build with git information
./mvnw clean package

# Git properties will be available at runtime
cat target/classes/git.properties
```

**Access in Java code:**
```java
Properties gitProps = new Properties();
gitProps.load(getClass().getResourceAsStream("/git.properties"));
String commitId = gitProps.getProperty("git.commit.id");
String buildTime = gitProps.getProperty("git.build.time");
```

## Validation

After adding this plugin, verify the configuration:

```bash
# Test Git Commit ID plugin
./mvnw clean package
cat target/classes/git.properties
```
                
            
            
#### Step Constraints

- **MUST** only add Git Commit ID plugin if build information tracking was selected in the SKILL.md question flow
- **MUST** check if plugin already exists before adding
- **MUST** ask user permission before modifying existing plugin configuration
- **MUST** use selected version properties for plugin versions
- **MUST** skip this step entirely if build information tracking was not selected
- **MUST** ensure project is in a git repository for proper functionality