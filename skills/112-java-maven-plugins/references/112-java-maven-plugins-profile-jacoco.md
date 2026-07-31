---
name: 112-java-maven-plugins-profile-jacoco
description: JaCoCo profile guidance for coverage reporting and thresholds.
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

Configure JaCoCo profile only when selected by the user in `112-java-maven-plugins`. Read this reference after the SKILL.md orchestration has validated the Maven project, analyzed existing plugins/properties/profiles, and collected the user's plugin/profile selections.

## Constraints

Apply only the selected JaCoCo profile configuration and preserve existing project configuration.

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
<maven-plugin-jacoco.version>0.8.13</maven-plugin-jacoco.version>
<coverage.level>[USER_SELECTED_COVERAGE_THRESHOLD]</coverage.level>
```
            ### Step 2: JaCoCo Code Coverage Profile Configuration

**Purpose**: Configure JaCoCo code coverage profile to analyze and enforce coverage thresholds with detailed reporting.

**Dependencies**: Only execute if user selected "Code coverage reporting (JaCoCo)" in the SKILL.md question flow. Requires completion of the SKILL.md orchestration steps and selected property handling.

**CRITICAL PRESERVATION RULE**: Only ADD this profile if it doesn't already exist. Never REPLACE or REMOVE existing profiles.

## Pre-Implementation Check

**BEFORE adding JaCoCo profile, check if it already exists in the pom.xml:**

If `<profiles>` section with `jacoco` profile already exists: Ask user "JaCoCo profile already exists. Do you want to enhance the existing configuration? (y/n)"

If user says "n": Skip this step entirely.
If user says "y": Proceed with adding missing configuration elements only.

**CONDITIONAL EXECUTION**: Only execute this step if user selected "Code coverage reporting (JaCoCo)" in the SKILL.md question flow.

## JaCoCo Profile Configuration

**ADD this profile to the `<profiles>` section in pom.xml ONLY if it doesn't already exist:**

```xml
<profile>
<id>jacoco</id>
<activation>
    <activeByDefault>false</activeByDefault>
</activation>
<build>
    <plugins>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>${maven-plugin-jacoco.version}</version>
            <executions>
                <execution>
                    <id>prepare-agent</id>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
                <execution>
                    <id>check</id>
                    <phase>verify</phase>
                    <goals>
                        <goal>check</goal>
                    </goals>
                    <configuration>
                        <rules>
                            <rule>
                                <element>BUNDLE</element>
                                <limits>
                                    <limit>
                                        <counter>LINE</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>${coverage.level}%</minimum>
                                    </limit>
                                    <limit>
                                        <counter>BRANCH</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>${coverage.level}%</minimum>
                                    </limit>
                                    <limit>
                                        <counter>METHOD</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>${coverage.level}%</minimum>
                                    </limit>
                                    <limit>
                                        <counter>CLASS</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>${coverage.level}%</minimum>
                                    </limit>
                                    <limit>
                                        <counter>INSTRUCTION</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>${coverage.level}%</minimum>
                                    </limit>
                                    <limit>
                                        <counter>COMPLEXITY</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>${coverage.level}%</minimum>
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
</profile>
```

## Usage Examples

```bash
# Run tests with coverage
./mvnw clean verify -Pjacoco

# View coverage reports
open target/site/jacoco/index.html
```

## Validation

After adding this profile, verify the configuration:

```bash
# Test JaCoCo profile
./mvnw clean verify -Pjacoco
```
                
            
            
#### Step Constraints

- **MUST** only add JaCoCo profile if code coverage was selected in the SKILL.md question flow
- **MUST** check if profile already exists before adding
- **MUST** ask user permission before modifying existing profile configuration
- **MUST** use coverage threshold values from the SKILL.md question flow
- **MUST** use selected version properties for plugin versions
- **MUST** skip this step entirely if code coverage was not selected