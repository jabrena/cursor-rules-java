---
name: 112-java-maven-plugins-profile-security
description: Security profile guidance for OWASP Dependency Check.
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

Configure security profile only when selected by the user in `112-java-maven-plugins`. Read this reference after the SKILL.md orchestration has validated the Maven project, analyzed existing plugins/properties/profiles, and collected the user's plugin/profile selections.

## Constraints

Apply only the selected security profile configuration and preserve existing project configuration.

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
<maven-plugin-dependency-check.version>12.1.1</maven-plugin-dependency-check.version>
<project.build.directory>[VERIFY_VERSION]</project.build.directory>
```
            ### Step 2: Security Vulnerability Scanning Profile Configuration

**Purpose**: Configure OWASP Dependency Check profile to scan dependencies for known security vulnerabilities.

**Dependencies**: Only execute if user selected "Security vulnerability scanning (OWASP)" in the SKILL.md question flow. Requires completion of the SKILL.md orchestration steps and selected property handling.

**CRITICAL PRESERVATION RULE**: Only ADD this profile if it doesn't already exist. Never REPLACE or REMOVE existing profiles.

## Pre-Implementation Check

**BEFORE adding Security profile, check if it already exists in the pom.xml:**

If `<profiles>` section with `security` profile already exists: Ask user "Security profile already exists. Do you want to enhance the existing configuration? (y/n)"

If user says "n": Skip this step entirely.
If user says "y": Proceed with adding missing configuration elements only.

**CONDITIONAL EXECUTION**: Only execute this step if user selected "Security vulnerability scanning (OWASP)" in the SKILL.md question flow.

## Security Profile Configuration

**ADD this profile to the `<profiles>` section in pom.xml ONLY if it doesn't already exist:**

```xml
<profile>
<id>security</id>
<activation>
    <activeByDefault>false</activeByDefault>
</activation>
<build>
    <plugins>
        <plugin>
            <groupId>org.owasp</groupId>
            <artifactId>dependency-check-maven</artifactId>
            <version>${maven-plugin-dependency-check.version}</version>
            <configuration>
                <outputDirectory>${project.build.directory}/dependency-check</outputDirectory>
                <format>ALL</format>
                <failBuildOnCVSS>7</failBuildOnCVSS>
                <skipProvidedScope>false</skipProvidedScope>
                <skipRuntimeScope>false</skipRuntimeScope>
                <skipSystemScope>false</skipSystemScope>
                <skipTestScope>false</skipTestScope>
                <!-- Performance and reliability improvements -->
                <nvdApiDelay>4000</nvdApiDelay>
                <nvdMaxRetryCount>3</nvdMaxRetryCount>
                <nvdValidForHours>24</nvdValidForHours>
                <!-- Skip analyzers that might cause issues -->
                <nodeAnalyzerEnabled>false</nodeAnalyzerEnabled>
                <retireJsAnalyzerEnabled>false</retireJsAnalyzerEnabled>
            </configuration>
            <executions>
                <execution>
                    <id>dependency-check</id>
                    <goals>
                        <goal>check</goal>
                    </goals>
                    <phase>verify</phase>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
</profile>
```

## Usage Examples

```bash
# Run security scan
./mvnw clean verify -Psecurity

# View security reports
open target/dependency-check/dependency-check-report.html
```

## Validation

After adding this profile, verify the configuration:

```bash
# Test Security profile
./mvnw clean verify -Psecurity
```
                
            
            
#### Step Constraints

- **MUST** only add Security profile if security scanning was selected in the SKILL.md question flow
- **MUST** check if profile already exists before adding
- **MUST** ask user permission before modifying existing profile configuration
- **MUST** use selected version properties for plugin versions
- **MUST** skip this step entirely if security scanning was not selected