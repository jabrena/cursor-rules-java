Feature: Validate changes from usage of Maven documentation skill

Background:
  Given the skill "113-java-maven-documentation"

@acceptance-test
Scenario: Generate DEVELOPER.md for Maven demo
  Given the example project "examples/maven/maven-demo"
  And the example project has a baseline "pom.xml"
  And the folder "examples/maven/maven-demo" has no git changes
  And "examples/maven/maven-demo/DEVELOPER.md" does not exist
  When the skill "113-java-maven-documentation" is applied to "examples/maven/maven-demo"
  Then every "pom.xml" in "examples/maven/maven-demo" is read before generating documentation
  And "DEVELOPER.md" is created in "examples/maven/maven-demo"
  And "DEVELOPER.md" starts with the exact base template heading:
    | heading                    |
    | # Developer commands       |
    | ## Essential maven commands |
  And "DEVELOPER.md" contains these essential Maven commands:
    | command                                    |
    | ./mvnw dependency:tree                     |
    | ./mvnw dependency:analyze                  |
    | ./mvnw clean validate -U                   |
    | ./mvnw buildplan:list-plugin               |
    | ./mvnw clean test                          |
    | ./mvnw clean verify                        |
    | ./mvnw versions:display-property-updates   |
    | ./mvnw versions:display-dependency-updates |
    | ./mvnw versions:display-plugin-updates     |
    | ./mvnw site                                |
  And "DEVELOPER.md" omits the "Submodules" section
  And "DEVELOPER.md" omits the "Maven Profiles" section
  And "DEVELOPER.md" contains the "Plugin Goals Reference" section
  And "DEVELOPER.md" declares plugin goal sections only for these explicitly configured plugins:
    | artifactId              |
    | spotless-maven-plugin   |
    | maven-failsafe-plugin   |
  And "DEVELOPER.md" contains these Spotless plugin goals:
    | goal                  | description                                |
    | ./mvnw spotless:apply | Apply formatting fixes                     |
    | ./mvnw spotless:check | Check formatting and fail on violations    |
  And "DEVELOPER.md" contains these Failsafe plugin goals:
    | goal                                | description                     |
    | ./mvnw failsafe:integration-test    | Run integration tests           |
    | ./mvnw failsafe:verify              | Verify integration test results |
  And "DEVELOPER.md" does not declare plugin goal sections for inherited or undeclared plugins:
    | artifactId               |
    | maven-compiler-plugin    |
    | maven-surefire-plugin    |
    | maven-jar-plugin         |
    | maven-install-plugin     |
  And any git changes produced during skill execution and verification are reset
