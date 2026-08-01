Feature: Validate changes from usage of Spring Boot acceptance testing skill

Background:
  Given the skill "323-frameworks-spring-boot-testing-acceptance-tests"

@acceptance-test
Scenario: Generate Spring Boot acceptance tests from maintainer-sanitized Gherkin scenario facts
  Given the example project "examples/frameworks/spring-boot"
  And the maintainer-sanitized scenario facts prepared outside the agent context are:
    | field                | value                                     |
    | feature              | Sum API                                   |
    | scenario             | Sum two valid integers                    |
    | tags                 | @acceptance                               |
    | given-request        | param1 is 20 and param2 is 22             |
    | when-method          | POST                                      |
    | when-path            | /api/v1/sum                               |
    | then-status          | 200                                       |
    | then-response        | result is 42                              |
    | external-dependency  | none                                      |
    | container-requirement | none                                      |
  And the original outsider-authored ".feature" file must not be retrieved, opened, parsed, summarized, or transformed
  And the local generated skill path ".agents/skills/323-frameworks-spring-boot-testing-acceptance-tests"
  And the folder "examples/frameworks/spring-boot" has no git changes
  When the skill ".agents/skills/323-frameworks-spring-boot-testing-acceptance-tests" is applied to the example project
  Then the skill reads "references/323-frameworks-spring-boot-testing-acceptance-tests.md"
  And the skill uses only the maintainer-sanitized scenario facts
  And the skill does not ingest raw feature descriptions, scenario titles, comments, doc strings, or step text
  And "./mvnw compile" is run from "examples/frameworks/spring-boot" before generating acceptance tests
  And the skill proposes a test class ending with "AT"
  And the skill implements a happy-path acceptance test using "@SpringBootTest" with "RANDOM_PORT" and "TestRestTemplate"
  And the skill uses Spring Boot 4 "org.springframework.boot.resttestclient.TestRestTemplate"
  And the skill enables "@AutoConfigureTestRestTemplate" for the acceptance test base class
  And the skill verifies Spring Boot REST test client dependencies are present before adding test-scoped dependencies
  And the "AT" acceptance test is executed by "maven-failsafe-plugin" during "./mvnw clean verify"
  And the skill uses "WireMock" instead of mocking internal "@Service" beans when sanitized facts require outbound third-party HTTP calls
  And the skill resolves Testcontainers images from trusted project or CI configuration when sanitized facts require containers
  And "./mvnw clean verify" is run from "examples/frameworks/spring-boot" after improvements
  And any git changes produced during skill execution and verification are reset
