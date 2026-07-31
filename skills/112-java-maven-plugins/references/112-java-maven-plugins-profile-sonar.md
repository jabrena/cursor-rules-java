---
name: 112-java-maven-plugins-profile-sonar
description: Sonar profile guidance for SonarQube and SonarCloud analysis.
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

Configure Sonar profile only when selected by the user in `112-java-maven-plugins`. Read this reference after the SKILL.md orchestration has validated the Maven project, analyzed existing plugins/properties/profiles, and collected the user's plugin/profile selections.

## Constraints

Apply only the selected Sonar profile configuration and preserve existing project configuration.

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
<maven-plugin-sonar.version>4.0.0.4121</maven-plugin-sonar.version>
```
            ### Step 2: SonarQube/SonarCloud Profile Configuration

**Purpose**: Configure SonarQube/SonarCloud profile for comprehensive code quality analysis integration.

**Dependencies**: Only execute if user selected "Sonar" in the SKILL.md question flow and provided Sonar configuration. Requires completion of the SKILL.md orchestration steps and selected property handling.

**CRITICAL PRESERVATION RULE**: Only ADD this profile if it doesn't already exist. Never REPLACE or REMOVE existing profiles.

## Pre-Implementation Check

**BEFORE adding Sonar profile, check if it already exists in the pom.xml:**

If `<profiles>` section with `sonar` profile already exists: Ask user "Sonar profile already exists. Do you want to enhance the existing configuration? (y/n)"

If user says "n": Skip this step entirely.
If user says "y": Proceed with adding missing configuration elements only.

**CONDITIONAL EXECUTION**: Only execute this step if user selected "Sonar" in the SKILL.md question flow and provided Sonar configuration.

## Sonar Profile Configuration

**ADD this profile to the `<profiles>` section in pom.xml ONLY if it doesn't already exist:**

```xml
<profile>
<id>sonar</id>
<activation>
    <activeByDefault>false</activeByDefault>
</activation>
<properties>
    <!-- SonarCloud configuration -->
    <sonar.host.url>REPLACE_WITH_SONAR_HOST_URL</sonar.host.url>
    <sonar.organization>REPLACE_WITH_SONAR_ORGANIZATION</sonar.organization>
    <sonar.projectKey>REPLACE_WITH_SONAR_PROJECT_KEY</sonar.projectKey>
    <sonar.projectName>REPLACE_WITH_SONAR_PROJECT_NAME</sonar.projectName>
    <sonar.projectVersion>${project.version}</sonar.projectVersion>
    <sonar.sources>src/main/java</sonar.sources>
    <sonar.tests>src/test/java</sonar.tests>
    <sonar.java.binaries>target/classes</sonar.java.binaries>
    <sonar.java.test.binaries>target/test-classes</sonar.java.test.binaries>
    <sonar.jacoco.reportPath>target/jacoco.exec</sonar.jacoco.reportPath>
    <sonar.junit.reportPaths>target/surefire-reports</sonar.junit.reportPaths>
    <sonar.coverage.exclusions>**/*Test.java,**/*IT.java</sonar.coverage.exclusions>
    <sonar.java.source>${java.version}</sonar.java.source>
</properties>
<build>
    <plugins>
        <plugin>
            <groupId>org.sonarsource.scanner.maven</groupId>
            <artifactId>sonar-maven-plugin</artifactId>
            <version>${maven-plugin-sonar.version}</version>
        </plugin>
    </plugins>
</build>
</profile>
```

## Implementation Guidelines

1. **Replace Sonar placeholders** with actual values from the SKILL.md question flow:
- `REPLACE_WITH_SONAR_HOST_URL`
- `REPLACE_WITH_SONAR_ORGANIZATION`
- `REPLACE_WITH_SONAR_PROJECT_KEY`
- `REPLACE_WITH_SONAR_PROJECT_NAME`

## Usage Examples

```bash
# Run Sonar analysis (requires token)
./mvnw clean verify sonar:sonar -Psonar -Dsonar.login=YOUR_TOKEN

# For SonarCloud with GitHub Actions
./mvnw clean verify sonar:sonar -Psonar -Dsonar.login=$SONAR_TOKEN

# Combined with JaCoCo
./mvnw clean verify sonar:sonar -Pjacoco,sonar -Dsonar.login=$SONAR_TOKEN
```

## Validation

After adding this profile, verify the configuration:

```bash
# Validate Sonar profile (requires token)
./mvnw clean verify sonar:sonar -Psonar -Dsonar.login=YOUR_TOKEN
```
                
            
            
#### Step Constraints

- **MUST** only add Sonar profile if Sonar integration was selected in the SKILL.md question flow
- **MUST** check if profile already exists before adding
- **MUST** ask user permission before modifying existing profile configuration
- **MUST** configure Sonar properties with actual values from the SKILL.md question flow
- **MUST** use selected version properties for plugin versions
- **MUST** skip this step entirely if Sonar was not selected