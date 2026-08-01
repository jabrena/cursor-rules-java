---
name: 112-java-maven-plugins-maven-enforcer-plugin
description: Maven Enforcer plugin guidance for dependency convergence, Java/Maven requirements, and build safety rules.
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

Configure Maven Enforcer only when selected by the user in `112-java-maven-plugins`. Read this reference after the SKILL.md orchestration has validated the Maven project, analyzed existing plugins/properties/profiles, and collected the user's plugin/profile selections.

## Constraints

Apply only the selected Maven Enforcer configuration and preserve existing project configuration.

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
<maven-plugin-enforcer.version>3.5.0</maven-plugin-enforcer.version>
<extra-enforcer-rules.version>1.10.0</extra-enforcer-rules.version>
<maven.version>3.9.10</maven.version>
<java.version>[USER_SELECTED_JAVA_VERSION]</java.version>
```
            ### Step 2: Maven Enforcer Plugin Configuration

**Purpose**: Configure maven-enforcer-plugin to enforce dependency convergence, prevent circular dependencies, and ensure consistent Maven/Java versions.

**Dependencies**: Requires completion of the SKILL.md orchestration steps and selected property handling.

**CRITICAL PRESERVATION RULE**: Only ADD this plugin if it doesn't already exist. Never REPLACE or REMOVE existing configuration.

## Pre-Implementation Check

**BEFORE adding maven-enforcer-plugin, check if it already exists in the pom.xml:**

If maven-enforcer-plugin already exists: Ask user "maven-enforcer-plugin already exists. Do you want to enhance the existing configuration? (y/n)"

If user says "n": Skip this step entirely.
If user says "y": Proceed with adding missing configuration elements only.

## Maven Enforcer Plugin Configuration

**ADD this plugin to the `<build><plugins>` section ONLY if it doesn't already exist:**

```xml
<plugin>
<groupId>org.apache.maven.plugins</groupId>
<artifactId>maven-enforcer-plugin</artifactId>
<version>${maven-plugin-enforcer.version}</version>
<dependencies>
    <dependency>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>extra-enforcer-rules</artifactId>
        <version>${extra-enforcer-rules.version}</version>
    </dependency>
</dependencies>
<executions>
    <execution>
        <id>enforce</id>
        <configuration>
            <rules>
                <banCircularDependencies/>
                <dependencyConvergence />
                <banDuplicatePomDependencyVersions />
                <requireMavenVersion>
                    <version>${maven.version}</version>
                </requireMavenVersion>
                <requireJavaVersion>
                    <version>${java.version}</version>
                </requireJavaVersion>
                <bannedDependencies>
                    <excludes>
                        <exclude>org.projectlombok:lombok</exclude>
                    </excludes>
                </bannedDependencies>
            </rules>
            <fail>true</fail>
        </configuration>
        <goals>
            <goal>enforce</goal>
        </goals>
    </execution>
</executions>
</plugin>
```

## Validation

After adding this plugin, verify the configuration:

```bash
# Validate plugin configuration
./mvnw validate
```
                
            
            
#### Step Constraints

- **MUST** include `extra-enforcer-rules` dependency and all specified rules
- **MUST** use selected version properties for plugin versions
- **MUST** check if plugin already exists before adding
- **MUST** ask user permission before modifying existing plugin configuration