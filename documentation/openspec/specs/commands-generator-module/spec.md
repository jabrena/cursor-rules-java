# plinth-commands-generator-module Specification

## Purpose
TBD - created by archiving change add-commands-generator-module. Update Purpose after archive.
## Requirements
### Requirement: Dedicated commands Maven module

The repository MUST provide a dedicated `plinth-commands-generator` Maven module that owns embedded command inventory, markdown assets, loader code, and command-focused tests.

#### Scenario: Register the commands module in Maven

- **GIVEN** the repository root `pom.xml` defines the Maven module list
- **WHEN** maintainers implement the commands module extraction
- **THEN** the root build registers `plinth-commands-generator`
- **AND** `./mvnw clean verify -pl plinth-commands-generator` passes

#### Scenario: Relocate command sources out of plinth-skills-generator

- **GIVEN** command sources currently live under `plinth-skills-generator`
- **WHEN** the commands module is implemented
- **THEN** `commands.xml` is owned by `plinth-commands-generator`
- **AND** `CommandIndexes.java` is owned by `plinth-commands-generator`
- **AND** `InventoryXmlLoader.java` is owned by `plinth-commands-generator`
- **AND** the 11 command markdown assets listed in `commands.xml` are owned by `plinth-commands-generator`
- **AND** `CommandIndexesTest` is owned by `plinth-commands-generator`
- **AND** command Gherkin features are owned by `plinth-commands-generator`
- **AND** `CommandIndexesTest` no longer contains a TODO to move to `plinth-commands-generator`

### Requirement: Command inventory integrity

The commands module MUST preserve the embedded command bundle declared by `commands.xml` and keep its source metadata aligned with the Maven project release.

#### Scenario: Preserve installation order and uniqueness

- **GIVEN** `commands.xml` defines the embedded command bundle
- **WHEN** command inventory tests run in `plinth-commands-generator`
- **THEN** every listed command asset exists
- **AND** command file names are unique
- **AND** installation order matches `commands.xml`

#### Scenario: Preserve per-command routing contracts

- **GIVEN** command markdown assets define purpose, usage, owning agent, and safeguards
- **WHEN** command contract tests run in `plinth-commands-generator`
- **THEN** routing and contract assertions for the embedded command bundle continue to pass after relocation

#### Scenario: Match every command version to the Maven project

- **GIVEN** `commands.xml` declares the authoritative command XML inventory
- **WHEN** command version consistency tests run
- **THEN** every inventoried command XML metadata version equals the root `pom.xml` project version
- **AND** the expected version is derived from the root POM rather than hard-coded in the test
- **AND** a mismatch identifies the offending command source

### Requirement: Skills-generator bridge at generate-resources

`plinth-skills-generator` MUST consume `plinth-commands-generator` output through a one-way Maven dependency and a `generate-resources` bridge.

#### Scenario: Declare one-way module dependency

- **GIVEN** `plinth-skills-generator` generates skills that embed command content
- **WHEN** the bridge is implemented
- **THEN** `plinth-skills-generator` declares a Maven dependency on `plinth-commands-generator`
- **AND** `plinth-commands-generator` does not declare a dependency on `plinth-skills-generator`

#### Scenario: Reuse InventoryXmlLoader without duplication

- **GIVEN** `InventoryXmlLoader` provides safe XML parser configuration for inventory documents
- **WHEN** command sources move into `plinth-commands-generator`
- **THEN** `InventoryXmlLoader` is owned by `plinth-commands-generator`
- **AND** `plinth-skills-generator` reuses it through the module dependency
- **AND** the repository does not keep duplicate `InventoryXmlLoader` implementations
- **AND** no additional shared-library module is introduced for this helper

#### Scenario: Stage command assets before skill generation

- **GIVEN** skills `001-commands-inventory` and `004-commands-installation` resolve command assets through XInclude during generation
- **WHEN** `plinth-skills-generator` runs `generate-resources`
- **THEN** command assets from `plinth-commands-generator` are unpacked or staged into the paths expected by skill XML (for example `skill-references/assets/commands/`)
- **AND** skill generation does not depend on stale command copies left in `plinth-skills-generator` source tree
- **AND** staged command assets are build output only and are not committed under `plinth-skills-generator/src/main/resources`

#### Scenario: Split command and skill validation responsibilities

- **GIVEN** `004-commands-installation.xml` remains owned by `plinth-skills-generator`
- **WHEN** command validation is implemented after extraction
- **THEN** `plinth-commands-generator` owns manifest, asset presence, and per-command contract tests
- **AND** `plinth-skills-generator` owns installer XInclude parity, bridge staging, and generated-skill propagation tests

### Requirement: Command-to-skill propagation verification

The build MUST prove that frontmatter-enabled command assets from `plinth-commands-generator` reach generated skills for `001-commands-inventory` and `004-commands-installation` without losing inventory coverage or command content.

#### Scenario: Generated 004 skill packages frontmatter-enabled command assets

- **GIVEN** `./mvnw clean verify -pl plinth-skills-generator -am` has been executed
- **WHEN** generated output under `.agents/skills/004-commands-installation` is inspected
- **THEN** `assets/commands` contains one Markdown asset for every command listed in `commands.xml`
- **AND** every embedded command asset begins with valid YAML frontmatter
- **AND** every embedded command retains its existing Markdown content after the frontmatter
- **AND** the set of asset file names remains unchanged
- **AND** no filesystem directory order is used as an acceptance condition

#### Scenario: Generated 001 skill lists the command inventory

- **GIVEN** `./mvnw clean verify -pl plinth-skills-generator -am` has been executed
- **WHEN** generated output under `.agents/skills/001-commands-inventory` is inspected
- **THEN** `references/001-commands-inventory.md` lists every command row corresponding to `commands.xml`
- **AND** the generated inventory preserves the command order declared by `commands.xml`
- **AND** no command from `plinth-commands-generator` is missing from the generated `001` inventory or `004` installer assets

#### Scenario: Propagate command-specific tool metadata

- **GIVEN** an inventoried command source declares `tools/tools-list/tool` entries
- **WHEN** the generated `004-commands-installation` asset for that command is inspected
- **THEN** its YAML frontmatter contains the corresponding `tools` sequence entries
- **AND** the YAML sequence preserves their XML document order
- **AND** the bridge does not replace them with a global or stale tool list

#### Scenario: Automated guard fails when frontmatter propagation breaks

- **GIVEN** `plinth-skills-generator` has generator tests for command-backed skills
- **WHEN** `./mvnw clean verify -pl plinth-skills-generator -am` is executed
- **THEN** the tests verify inventory parity, frontmatter presence and validity, command-specific tool propagation, and existing body preservation
- **AND** verification fails when an inventoried asset is removed, renamed, not staged, or loses its required frontmatter

### Requirement: Contributor documentation for bridged builds

Contributor docs MUST document isolated and integrated Maven commands for the new module boundary.

#### Scenario: Document module commands

- **GIVEN** `DEVELOPER.md` documents reactor modules and per-module `./mvnw` commands
- **WHEN** the commands module is added
- **THEN** `DEVELOPER.md` includes a `plinth-commands-generator` submodule row with `./mvnw clean verify -pl plinth-commands-generator`
- **AND** contributor docs document `./mvnw clean install -pl plinth-skills-generator -am` as the command to regenerate local skills from the `plinth-commands-generator` outcome
- **AND** `AGENTS.md` file-structure guidance points command sources to `plinth-commands-generator`

