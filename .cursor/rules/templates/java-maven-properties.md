# Java Maven properties

## Rule 1: Maven Properties

Update the pom.xml with this set of properties:

```xml
<properties>
    <java.version>24</java.version>
    <maven.version>3.9.10</maven.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>

    <!-- Dependencies -->
    <jspecify.version>1.0.0</jspecify.version>

    <!-- Test dependencies -->
    <junit.version>5.12.0</junit.version>
    <mockito.version>5.18.0</mockito.version>
    <assertj.version>3.27.3</assertj.version>

    <!-- Maven Extensions -->
    <maven-extensions-build-cache.version>1.2.0</maven-extensions-build-cache.version>

    <!-- Maven Plugins -->
    <maven-plugins-flatten.version>1.7.0</maven-plugins-flatten.version>
    <maven-plugins-enforcer.version>3.5.0</maven-plugins-enforcer.version>
    <maven-plugins-compiler.version>3.14.0</maven-plugins-compiler.version>
    <error-prone.version>2.38.0</error-prone.version>
    <nullaway.version>0.11.0</nullaway.version>
    <maven-plugins-surefire.version>3.5.3</maven-plugins-surefire.version>
    <maven-plugins-failsafe.version>3.5.3</maven-plugins-failsafe.version>
    <maven-plugins-jxr.version>3.6.0</maven-plugins-jxr.version>
    <maven-plugins-jacoco.version>0.8.13</maven-plugins-jacoco.version>
    <maven-plugins-versions.version>2.18.0</maven-plugins-versions.version>
    <maven-plugins-git-commit-id.version>4.9.10</maven-plugins-git-commit-id.version>
    <maven-plugins-pitest.version>1.19.4</maven-plugins-pitest.version>
    <maven-plugins-pitest-junit5.version>1.2.3</maven-plugins-pitest-junit5.version>
    <maven-plugins-dependency-check.version>12.1.1</maven-plugins-dependency-check.version>

    <!-- Coverage -->
    <coverage.level>80</coverage.level>
</properties>
```