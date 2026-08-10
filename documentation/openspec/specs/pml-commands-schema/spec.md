# pml-commands-schema Specification

## Purpose
TBD - created by archiving change design-pml-commands-schema. Update Purpose after archive.
## Requirements
### Requirement: Command body XSD (agents parity)

The commands generator SHALL validate individual command contracts with a single no-namespace `commands.xsd` colocated at `plinth-commands-generator/src/main/resources/commands.xsd`, following the same global-element reference style as `agents.xsd` while adding the command-specific XML metadata contract used to render YAML frontmatter.

#### Scenario: Validate structural command contract sections

- **GIVEN** a command definition document with required `@id`
- **WHEN** `commands.xsd` is applied
- **THEN** the schema requires `metadata`, `goal`, and `steps`, with optional `constraints`, `output-format`, and `safeguards` in the defined sequence
- **AND** the command and `metadata` sequences compose global elements through `xs:element ref`, following the `agents.xsd` convention
- **AND** `metadata` supports optional PML-style `authors`, requires `version`, `description`, `argument-hint`, `model`, `agent`, and `tools`
- **AND** `authors` contains one or more repeatable `author` string elements when present
- **AND** `version` uses the global string element convention shared with agent and skill schemas
- **AND** `tools` requires `tools-list` containing one or more `tool` elements
- **AND** optional `output-format` is expressible in the structural sequence
- **AND** narrative contract content remains authored as Markdown inside `goal` CDATA
- **AND** documents missing a required section or metadata field fail XSD validation
- **AND** documents declare `xsi:noNamespaceSchemaLocation` pointing at `commands.xsd`

#### Scenario: Keep metadata values extensible per command

- **GIVEN** different commands can declare different authors, versions, descriptions, argument hints, agents, models, or tools
- **WHEN** their XML documents validate against `commands.xsd`
- **THEN** the schema enforces the metadata structure and repeating tool representation
- **AND** the schema does not restrict author, version, model, agent, or tool values to a closed enumeration
- **AND** each command source can declare its own `tools/tools-list/tool` entries

#### Scenario: Align with analysis-design-commands contract fields

- **GIVEN** OpenSpec `analysis-design-commands` requires purpose, accepted inputs, owning agent, associated skills or capabilities, workflow, outputs, and safeguards
- **WHEN** the command body XSD and XSLT are applied
- **THEN** metadata, workflow, outputs, and safeguards map to structured XML elements; purpose and remaining narrative fields map to Markdown inside `goal` CDATA
- **AND** kind-specific required or forbidden sections remain enforced by `analysis-design-commands`, `CommandIndexesTest`, and Gherkin rather than Schematron
- **AND** body `@id` matches the generated Markdown H1 identity

### Requirement: Inventory outside body schema

The command inventory manifest SHALL remain a separate `commands.xml` document outside `commands.xsd`, matching `agents.xml` / `agents.xsd` separation.

#### Scenario: Inventory lists XML sources mapped to Markdown assets

- **GIVEN** `plinth-commands-generator/src/main/resources/commands.xml`
- **WHEN** `CommandIndexes` loads the inventory
- **THEN** each `<command file="..."/>` entry ends in `.xml`
- **AND** `commandFiles()` returns the corresponding `.md` names in installation order
- **AND** duplicate mapped Markdown names fail inventory uniqueness checks in tests / Java loaders
- **AND** inventory is not validated by `commands.xsd`

### Requirement: XML sources and generated Markdown

Command contracts SHALL be authored as XML under `commands/` and transformed to frontmatter-enabled Markdown only under `target/`, mirroring agents generation while preserving the existing command body, authors, and release version.

#### Scenario: Build-time Markdown generation

- **GIVEN** every command XML source declared by `commands.xml` contains schema-valid XML `metadata`
- **WHEN** Maven reaches `process-classes` for `plinth-commands-generator`
- **THEN** `CommandMarkdownGenerator` writes Markdown to `target/generated-resources/commands` and `target/classes/commands`
- **AND** every generated file begins with an opening YAML delimiter and ends its frontmatter before the command H1
- **AND** the generated frontmatter contains `description`, `argument-hint`, `model`, `agent`, `tools`, nested `metadata.author`, and nested `metadata.version`
- **AND** hand-authored `commands/*.md` are not present under `src/main/resources/commands/`
- **AND** `CommandIndexesTest` assertions pass against the generated classpath Markdown

#### Scenario: Map XML tools to YAML tools

- **GIVEN** a command source declares one or more `tools/tools-list/tool` values
- **WHEN** the command Markdown is generated
- **THEN** its frontmatter contains a YAML `tools` sequence
- **AND** every declared XML tool appears as a corresponding YAML sequence entry
- **AND** the YAML sequence preserves the XML `tool` document order

#### Scenario: Render approved command-specific metadata

- **GIVEN** the maintainer-approved YAML-frontmatter values are recorded in each inventoried command XML `metadata` element
- **WHEN** the generated frontmatter is parsed as YAML 1.2
- **THEN** nested `metadata.author`, nested `metadata.version`, `description`, `argument-hint`, `model`, and `agent` equal the corresponding XML values
- **AND** multiple XML authors are rendered in document order using the same comma-separated scalar convention as agents and skills
- **AND** `tools` equals the complete ordered `tools/tools-list/tool` sequence from that command source
- **AND** YAML-significant punctuation in scalar values remains valid and retains its parsed meaning

#### Scenario: Preserve existing Markdown after frontmatter

- **GIVEN** a command has an existing generated Markdown heading and body
- **WHEN** frontmatter generation is enabled
- **THEN** the heading and body follow the closing YAML delimiter
- **AND** after removing the frontmatter and its required separator, the remaining Markdown matches the previous generated command Markdown byte-for-byte

### Requirement: Command kind taxonomy

The schema design SHALL define optional `@kind` values that select documentation heading profiles without Schematron.

#### Scenario: Classify embedded commands by kind

- **GIVEN** the twelve commands under `plinth-commands-generator/src/main/resources/commands/`
- **WHEN** command XML documents are authored
- **THEN** `@kind` values `standard`, `delivery`, `performance`, and `cli` are available
- **AND** each command id maps to exactly one kind in design documentation
- **AND** kind-profile section rules stay in behavioral tests

### Requirement: PML / agents relationship documentation

The schema design SHALL document that command schemas are a parallel, agents-style no-namespace family — not namespaced split schemas under `/pml/schemas/command/1.0.0/`.

#### Scenario: Document parallel skill, agent, and command families

- **GIVEN** skills use `pml.xsd` `<prompt>` documents and agents use no-namespace `agents.xsd`
- **WHEN** the command schema design is published
- **THEN** the design states commands follow the agents generator pattern (single body XSD + inventory XML + XSLT Markdown)
- **AND** the design states command schemas are not a replacement for skill `<prompt>` documents
- **AND** the design references ADR-001 and the shipped agents generator as the parity target

### Requirement: Authoritative command XML sources

The change SHALL treat production command XML under `plinth-commands-generator` as the authoritative examples for `commands.xsd`.

#### Scenario: Production sources and XSD mirror

- **GIVEN** twelve command XML sources under `plinth-commands-generator/src/main/resources/commands/`
- **WHEN** the schema design is published
- **THEN** those documents validate against `commands.xsd`
- **AND** the OpenSpec XSD mirror lives at `examples/xsd/pml/0.9.0/commands.xsd`
- **AND** separate OpenSpec valid/invalid XML fixture trees are not required

### Requirement: Migration notes

The change SHALL document migration from Markdown-first command assets to XML sources with agents-parity generation.

#### Scenario: Completed cutover guidance

- **GIVEN** former Markdown-first contracts and the rejected namespaced `pml/` schema tree
- **WHEN** migration notes are published
- **THEN** they describe XML under `commands/`, generation via `command-to-markdown.xsl`, and removal of hand-authored Markdown from `src/`
- **AND** they state inventory remains outside `commands.xsd`
- **AND** they preserve behavioral contract stability for installers consuming generated Markdown

