---
name: 111-java-maven-dependencies-jspecify
description: JSpecify, Error Prone, and NullAway Maven dependency guidance for the interactive Maven dependencies skill.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Add Maven dependencies for improved code quality

## Role

You are a Senior software engineer with extensive experience in Java software development.

## Goal

Configure nullness analysis only when selected by the user. This reference owns both the JSpecify dependency and the conditional Error Prone + NullAway compiler analysis path because enhanced NullAway analysis depends on the JSpecify decision in the question flow.

## Constraints

Apply only the selected nullness-analysis pieces after the questions embedded in SKILL.md have been answered.

- Use `provided` scope for JSpecify.
- Add Error Prone + NullAway only when the user selected enhanced compiler analysis after selecting JSpecify.
- Ask for and use the real project package name in `NullAway:AnnotatedPackages`.
- Create `.mvn/jvm.config` only when enhanced compiler analysis is selected.

## Steps

### Step 1: Add selected version properties

Add only the properties needed by the user's selections:

```xml
<jspecify.version>1.0.0</jspecify.version>
<error-prone.version>2.35.1</error-prone.version>
<nullaway.version>0.12.0</nullaway.version>
```
### Step 2: Add JSpecify dependency when selected

```xml
<dependency>
    <groupId>org.jspecify</groupId>
    <artifactId>jspecify</artifactId>
    <version>${jspecify.version}</version>
    <scope>provided</scope>
</dependency>
```
### Step 3: Add enhanced compiler analysis when selected

Update or add `maven-compiler-plugin` configuration only when the user selected enhanced analysis. Replace `com.example.myproject` with the package name confirmed by the user.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>${maven-plugin-compiler.version}</version>
    <configuration>
        <release>${java.version}</release>
        <compilerArgs>
            <arg>-Xlint:all</arg>
            <arg>-Werror</arg>
            <arg>-XDcompilePolicy=simple</arg>
            <arg>--should-stop=ifError=FLOW</arg>
            <arg>-Xplugin:ErrorProne
                -Xep:NullAway:ERROR
                -XepOpt:NullAway:JSpecifyMode=true
                -XepOpt:NullAway:TreatGeneratedAsUnannotated=true
                -XepOpt:NullAway:CheckOptionalEmptiness=true
                -XepOpt:NullAway:HandleTestAssertionLibraries=true
                -XepOpt:NullAway:AssertsEnabled=true
                -XepOpt:NullAway:AnnotatedPackages=com.example.myproject
            </arg>
        </compilerArgs>
        <annotationProcessorPaths>
            <path>
                <groupId>com.google.errorprone</groupId>
                <artifactId>error_prone_core</artifactId>
                <version>${error-prone.version}</version>
            </path>
            <path>
                <groupId>com.uber.nullaway</groupId>
                <artifactId>nullaway</artifactId>
                <version>${nullaway.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```
### Step 4: Create JVM configuration for enhanced analysis

Create `.mvn/jvm.config` only when enhanced compiler analysis is selected:

```text
--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED
```