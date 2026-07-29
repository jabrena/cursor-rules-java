## MODIFIED Requirements

### Requirement: Agent inventory integrity

The agents module MUST preserve the embedded agent bundle declared by `agents.xml`.

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

### Requirement: Agent-to-skill propagation verification

The build MUST prove that bridged agent assets from `plinth-agents-generator` reach generated skills for `002-agents-inventory` and `005-agents-installation`.

#### Scenario: Generated 005 skill embeds agent bodies

- **GIVEN** `./mvnw clean install -pl plinth-skills-generator -am` has been executed
- **WHEN** generated output under `.agents/skills/005-agents-installation` is inspected
- **THEN** `references/005-agents-installation.md` embeds full agent bodies sourced from bridged `plinth-agents-generator` assets through XInclude expansion
- **AND** identifiable markers from agents listed in `agents.xml` are present inline (for example `name: plinth-architect`, `name: plinth-tech-lead`, `name: plinth-no-java`, `name: plinth-java-performance`)
- **AND** the generated `005` skill does not rely on a separate `assets/agents/` resource list in `skills.xml` (embed-first model preserved)

#### Scenario: Generated 002 skill embeds the inventory template

- **GIVEN** `./mvnw clean install -pl plinth-skills-generator -am` has been executed
- **WHEN** generated output under `.agents/skills/002-agents-inventory` is inspected
- **THEN** `references/002-agents-inventory.md` embeds the inventory template content inline
- **AND** the embedded content lists every agent row corresponding to `agents.xml`
- **AND** no agent asset from `plinth-agents-generator` is missing from the generated `002` or `005` skill references

#### Scenario: Installer XML parity uses XInclude hrefs

- **GIVEN** `005-agents-installation.xml` remains owned by `plinth-skills-generator` and has no `skills.xml` resource list
- **WHEN** `AgentInstallerParityTest` runs
- **THEN** it compares direct-child XInclude hrefs in the installer XML against `AgentIndexes.agentFiles()`
- **AND** the test does not depend on `SkillIndexes.SkillDescriptor.resources()` for `005`

#### Scenario: Automated guard fails when bridge breaks

- **GIVEN** `plinth-skills-generator` has an integration or generator test for agent-backed skills
- **WHEN** `./mvnw clean verify -pl plinth-skills-generator -am` is executed
- **THEN** the test asserts generated references for `002` and `005` contain content from staged `plinth-agents-generator` assets
- **AND** the test fails when an agent asset is removed, renamed, or not staged into skill generation

## Source and Derivation

- Source artifact: GitHub issue [#1094](https://github.com/jabrena/plinth/issues/1094).
- Derivation direction: issue #1094 -> `rename-agents-robot-to-plinth` requirements -> renamed agent module sources and generated output.
