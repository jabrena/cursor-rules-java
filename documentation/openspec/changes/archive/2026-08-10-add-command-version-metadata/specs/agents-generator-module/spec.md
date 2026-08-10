## MODIFIED Requirements

### Requirement: Agent inventory integrity

The agents module MUST preserve the embedded agent bundle declared by `agents.xml` and keep its source metadata aligned with the Maven project release.

#### Scenario: Preserve installation order and uniqueness

- **GIVEN** `agents.xml` defines the embedded agent bundle in installation order
- **WHEN** agent inventory tests run in `plinth-agents-generator`
- **THEN** every listed agent asset exists
- **AND** agent file names are unique
- **AND** installation order matches `agents.xml`
- **AND** the bundle contains exactly nine agents:
  - `plinth-business-analyst.md`
  - `plinth-architect.md`
  - `plinth-tech-lead.md`
  - `plinth-no-java.md`
  - `plinth-java-performance.md`
  - `plinth-java-coder.md`
  - `plinth-java-micronaut-coder.md`
  - `plinth-java-quarkus-coder.md`
  - `plinth-java-spring-boot-coder.md`

#### Scenario: Preserve per-agent routing and delegation contracts

- **GIVEN** agent markdown assets define missions, routing, skill precedence, and safeguards
- **WHEN** agent contract tests run in `plinth-agents-generator`
- **THEN** installer/inventory parity, tech-lead routing, coder skill precedence, framework JDBC preferences, and nine-agent bundle assertions continue to pass after relocation

#### Scenario: Match every agent version to the Maven project

- **GIVEN** `agents.xml` declares the authoritative agent XML inventory
- **WHEN** agent version consistency tests run
- **THEN** every inventoried agent XML metadata version equals the root `pom.xml` project version
- **AND** the expected version is derived from the root POM rather than hard-coded in the test
- **AND** a mismatch identifies the offending agent source
