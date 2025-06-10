# Java Maven Properties Configuration

## Properties Configuration Strategy

Build properties incrementally based on user's actual needs and project requirements. This template provides a comprehensive, conversational approach to configuring Maven properties.

## Core Properties (Always Added)

Start with essential build properties that every project needs (use the Java version selected in the initial questions):

```xml
<properties>
  <java.version>[USER_SELECTED_JAVA_VERSION]</java.version>
  <maven.version>3.9.10</maven.version>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>  
</properties>
```

## Maven Version and Plugin Properties (Conditional)

**Ask**: "Do you want to enforce specific Maven and plugin versions for build consistency? (y/n)"

**If yes, add**:
```xml
<maven-compiler-plugin.version>3.14.0</maven-compiler-plugin.version>
<maven-surefire-plugin.version>3.5.3</maven-surefire-plugin.version>
<maven-enforcer-plugin.version>3.5.0</maven-enforcer-plugin.version>
```

## Dependency Version Properties (Conditional)

**Based on dependency selections**, add relevant version properties:

### Code Quality Dependencies
**If JSpecify selected**:
```xml
<jspecify.version>1.0.0</jspecify.version>
```

### Test Dependencies (If testing frameworks selected)
```xml
<junit.version>5.12.0</junit.version>
<mockito.version>5.18.0</mockito.version>
<assertj.version>3.27.3</assertj.version>
```

## Quality and Analysis Properties (Conditional)

**Ask**: "Do you want to configure quality thresholds for code coverage and analysis? (y/n)"

**If yes, ask for specific thresholds**:
- "What minimum code coverage percentage do you want? (default: 80%)"
- "What minimum mutation testing score do you want? (default: 70%)"

**Add based on answers**:
```xml
<!-- Quality thresholds -->
<coverage.level>[USER_SPECIFIED_COVERAGE]</coverage.level>
<mutation.level>[USER_SPECIFIED_MUTATION]</mutation.level>
```

## Additional Plugin Version Properties (Feature-Based)

**Only add plugin version properties for selected features**:

### Integration Testing
**If Integration Testing selected**:
```xml
<maven-failsafe-plugin.version>3.5.3</maven-failsafe-plugin.version>
```

### Code Coverage
**If Code Coverage selected**:
```xml
<jacoco-maven-plugin.version>0.8.13</jacoco-maven-plugin.version>
<maven-jxr-plugin.version>3.6.0</maven-jxr-plugin.version>
```

### Mutation Testing
**If Mutation Testing selected**:
```xml
<pitest-maven.version>1.19.4</pitest-maven.version>
<pitest-junit5-plugin.version>1.2.3</pitest-junit5-plugin.version>
```

### Security Scanning
**If Security Scanning selected**:
```xml
<dependency-check-maven.version>12.1.1</dependency-check-maven.version>
```

### Static Analysis
**If Static Analysis selected**:
```xml
<spotbugs-maven-plugin.version>4.9.3.0</spotbugs-maven-plugin.version>
<maven-pmd-plugin.version>3.26.0</maven-pmd-plugin.version>
```

### Enhanced Compiler Analysis
**If Enhanced Code Analysis selected**:
```xml
<error-prone.version>2.38.0</error-prone.version>
<nullaway.version>0.11.0</nullaway.version>
<extra-enforcer-rules.version>1.10.0</extra-enforcer-rules.version>
```

### Version Management
**If Version Management selected**:
```xml
<versions-maven-plugin.version>2.18.0</versions-maven-plugin.version>
```

### Build Info
**If Build Info selected**:
```xml
<git-commit-id-plugin.version>4.9.10</git-commit-id-plugin.version>
```

### Library Publishing
**If Library Publishing selected**:
```xml
<flatten-maven-plugin.version>1.7.0</flatten-maven-plugin.version>
```

### Site and Reporting
**If Site Generation selected**:
```xml
<maven-site-plugin.version>3.20.0</maven-site-plugin.version>
<maven-project-info-reports-plugin.version>3.7.0</maven-project-info-reports-plugin.version>
```

### SonarQube Integration
**If SonarQube Integration selected**:
```xml
<sonar-maven-plugin.version>4.0.0.4121</sonar-maven-plugin.version>
```

## Final Properties Structure Example

The final `<properties>` section will look like this (example with common selections):

```xml
<properties>
  <java.version>24</java.version> 
  <maven.version>3.9.10</maven.version>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>

  <!-- Dependency versions (based on selections) -->
  <jspecify.version>1.0.0</jspecify.version>

  <!-- Feature-specific plugin versions (based on selections) -->
  <maven-compiler-plugin.version>3.14.0</maven-compiler-plugin.version>
  <maven-surefire-plugin.version>3.5.3</maven-surefire-plugin.version>
  <maven-enforcer-plugin.version>3.5.0</maven-enforcer-plugin.version>
  <jacoco-maven-plugin.version>0.8.13</jacoco-maven-plugin.version>
  <pitest-maven.version>1.19.4</pitest-maven.version>
  <pitest-junit5-plugin.version>1.2.3</pitest-junit5-plugin.version>
  <spotbugs-maven-plugin.version>4.9.3.0</spotbugs-maven-plugin.version>
  <error-prone.version>2.38.0</error-prone.version>
  <nullaway.version>0.11.0</nullaway.version>

  <!-- Quality thresholds (if configured) -->
  <coverage.level>80</coverage.level>
  <mutation.level>70</mutation.level>
</properties>
```

## Implementation Guidelines

1. **Build incrementally**: Start with core properties and add only what's needed
2. **Ask before adding**: Don't include properties for unselected features
3. **Customize versions**: Use the Java version and thresholds specified by the user
4. **Group logically**: Keep related properties together with comments
5. **Use descriptive names**: Property names should clearly indicate their purpose
6. **Version consistency**: Ensure compatible versions across related plugins