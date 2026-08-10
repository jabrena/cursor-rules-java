# agents-generator-module Specification

## Purpose
TBD - created by archiving change add-agents-generator-module. Update Purpose after archive.
## Requirements
### Requirement: Dedicated agents Maven module

The repository MUST provide a dedicated `plinth-agents-generator` Maven module that owns embedded agent inventory, markdown assets, loader code, and agent-focused tests.

#### Scenario: Register the agents module in Maven

- **GIVEN** the repository root `pom.xml` defines the Maven module list
- **WHEN** maintainers implement the agents module extraction
- **THEN** the root build registers `plinth-agents-generator`
- **AND** `./mvnw clean verify -pl plinth-agents-generator` passes

#### Scenario: Relocate agent sources out of plinth-skills-generator

- **GIVEN** agent sources currently live under `plinth-skills-generator`
- **WHEN** the agents module is implemented
- **THEN** `agents.xml` is owned by `plinth-agents-generator`
- **AND** `AgentIndexes.java` is owned by `plinth-agents-generator`
- **AND** the nine agent markdown assets embedded by `005-agents-installation.xml` are owned by `plinth-agents-generator`
- **AND** `java-agents-inventory-template.md` is owned by `plinth-agents-generator`
- **AND** agent bundle tests relocated from `SkillsGeneratorTest.EmbeddedAgentBundleTests` are owned by `plinth-agents-generator`
- **AND** agent Gherkin features are owned by `plinth-agents-generator`
- **AND** `SkillsGeneratorTest` no longer contains a TODO to move `EmbeddedAgentBundleTests` to `plinth-agents-generator`

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

### Requirement: Agents-generator dependency on plinth-commands-generator

`plinth-agents-generator` MUST reuse `InventoryXmlLoader` from `plinth-commands-generator` through a one-way Maven dependency.

#### Scenario: Declare one-way helper dependency

- **GIVEN** `InventoryXmlLoader` provides safe XML parser configuration for inventory documents
- **WHEN** agent sources are implemented in `plinth-agents-generator`
- **THEN** `plinth-agents-generator` declares a Maven dependency on `plinth-commands-generator`
- **AND** `plinth-agents-generator` reuses `InventoryXmlLoader` without duplicating the helper
- **AND** `plinth-commands-generator` does not declare a dependency on `plinth-agents-generator`
- **AND** no additional shared-library module is introduced for this helper

### Requirement: Skills-generator bridge at generate-resources

`plinth-skills-generator` MUST consume `plinth-agents-generator` output through a one-way Maven dependency and a `generate-resources` bridge.

#### Scenario: Declare one-way module dependency

- **GIVEN** `plinth-skills-generator` generates skills that embed agent content
- **WHEN** the bridge is implemented
- **THEN** `plinth-skills-generator` declares a Maven dependency on `plinth-agents-generator`
- **AND** `plinth-agents-generator` does not declare a dependency on `plinth-skills-generator`

#### Scenario: Stage agent assets before skill generation

- **GIVEN** skills `002-agents-inventory` and `005-agents-installation` resolve agent assets through XInclude during generation
- **WHEN** `plinth-skills-generator` runs `generate-resources`
- **THEN** agent assets from `plinth-agents-generator` are unpacked or staged into the paths expected by skill XML (for example `skill-references/assets/agents/`)
- **AND** `java-agents-inventory-template.md` is staged into `skill-references/assets/`
- **AND** skill generation does not depend on stale agent copies left in `plinth-skills-generator` source tree
- **AND** staged agent assets are build output only and are not committed under `plinth-skills-generator/src/main/resources`

#### Scenario: Split agent and skill validation responsibilities

- **GIVEN** `005-agents-installation.xml` remains owned by `plinth-skills-generator`
- **WHEN** agent validation is implemented after extraction
- **THEN** `plinth-agents-generator` owns manifest, asset presence, and per-agent contract tests
- **AND** `plinth-skills-generator` owns installer XInclude parity, bridge staging, and generated-skill propagation tests

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

### Requirement: Contributor documentation for bridged builds

Contributor docs MUST document isolated and integrated Maven commands for the new module boundary.

#### Scenario: Document module commands

- **GIVEN** `DEVELOPER.md` documents reactor modules and per-module `./mvnw` commands
- **WHEN** the agents module is added
- **THEN** `DEVELOPER.md` includes an `plinth-agents-generator` submodule row with `./mvnw clean verify -pl plinth-agents-generator`
- **AND** contributor docs document `./mvnw clean install -pl plinth-skills-generator -am` as the command to regenerate local skills from the `plinth-agents-generator` outcome
- **AND** `AGENTS.md` file-structure guidance points agent sources to `plinth-agents-generator`

