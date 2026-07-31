---
name: 112-java-maven-plugins-jib-maven-plugin
description: Jib Maven plugin guidance for container image builds.
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

Configure Jib only when selected by the user in `112-java-maven-plugins`. Read this reference after the SKILL.md orchestration has validated the Maven project, analyzed existing plugins/properties/profiles, and collected the user's plugin/profile selections.

## Constraints

Apply only the selected Jib configuration and preserve existing project configuration.

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
<maven-plugin-jib.version>3.5.1</maven-plugin-jib.version>
```
            ### Step 2: Jib Maven Plugin Configuration

**Purpose**: Configure Jib Maven plugin for building container images without Docker daemon, using the image name provided in the Jib target image question.

**Dependencies**: Only execute if user selected "Container image build (Jib)" in the SKILL.md question flow. Requires completion of the SKILL.md orchestration steps and selected property handling.

**CRITICAL PRESERVATION RULE**: Only ADD this plugin if it doesn't already exist. Never REPLACE or REMOVE existing plugins.

## Pre-Implementation Check

**BEFORE adding Jib plugin, check if it already exists in the pom.xml:**

If jib-maven-plugin already exists: Ask user "Jib plugin already exists. Do you want to enhance the existing configuration? (y/n)"

If user says "n": Skip this step entirely.
If user says "y": Proceed with adding missing configuration elements only.

**CONDITIONAL EXECUTION**: Only execute this step if user selected "Container image build (Jib)" in the SKILL.md question flow.

## Implementation Guidelines

1. **Replace image placeholder**: Use the target container image from Question 3.1 (e.g., `gcr.io/my-project/my-app`, `docker.io/username/myimage`, or `myimage` for local Docker)
2. **No Docker daemon required**: Jib builds images directly; useful for CI/CD and local development

## Jib Plugin Configuration

**ADD this plugin to the `<build><plugins>` section ONLY if it doesn't already exist:**

```xml
<plugin>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>jib-maven-plugin</artifactId>
    <version>${maven-plugin-jib.version}</version>
    <configuration>
        <to>
            <image>REPLACE_WITH_ACTUAL_IMAGE</image>
        </to>
    </configuration>
</plugin>
```

**Replace `REPLACE_WITH_ACTUAL_IMAGE`** with the value from Question 3.1 (e.g., `myimage`, `gcr.io/my-project/my-app`).

## Usage Examples

```bash
# Build container image (no Docker daemon required)
./mvnw compile jib:build

# Build to Docker daemon (for local testing)
./mvnw compile jib:dockerBuild

# Build image tarball
./mvnw compile jib:buildTar
```

## Validation

After adding this plugin, verify the configuration:

```bash
# Validate plugin configuration
./mvnw validate
```
                
            
            
#### Step Constraints

- **MUST** only add Jib plugin if "Container image build (Jib)" was selected in the SKILL.md question flow
- **MUST** check if plugin already exists before adding
- **MUST** ask user permission before modifying existing plugin configuration
- **MUST** use selected version properties for plugin version
- **MUST** replace REPLACE_WITH_ACTUAL_IMAGE with the target container image from Question 3.1
- **MUST** skip this step entirely if Container image build (Jib) was not selected