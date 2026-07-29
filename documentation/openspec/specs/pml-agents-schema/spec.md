# pml-agents-schema Specification

## Purpose
TBD - created by archiving change design-pml-agents-schema. Update Purpose after archive.
## Requirements
### Requirement: Agent XSD for individual agent documents

The PML agent schema family SHALL define a production XSD (`agents.xsd`) that validates individual agent definition documents with root `<agent>`.

#### Scenario: Canonical schema location and shim

- **GIVEN** Plinth hosts the agents generator module
- **WHEN** contributors validate or mirror the agent schema
- **THEN** the canonical schema path is `plinth-agents-generator/src/main/resources/agents.xsd`
- **AND** the OpenSpec mirror lives at `documentation/openspec/changes/design-pml-agents-schema/examples/xsd/pml/0.9.0/agents.xsd`
- **AND** `examples/xsd/pml/0.9.0/agent.xsd` remains a shim that includes `agents.xsd`

#### Scenario: Validate agent body structural contract

- **GIVEN** an agent definition document for an embedded Plinth agent
- **WHEN** `agents.xsd` is applied
- **THEN** the schema requires `@id`, `<metadata>` with `<title>` and `<description>`, `<role>`, and `<goal>`
- **AND** the schema allows optional `<constraints>`, `<steps>`, `<output-format>`, and `<safeguards>`
- **AND** the schema does not require typed `<missions>`, `<frontmatter>`, or other per-shape body roots
- **AND** documents missing required identity/role/goal fields fail validation
- **AND** required vs optional cardinalities are documented in this change’s [`design.md`](../../design.md)

#### Scenario: Inventory remains outside agents.xsd

- **GIVEN** Plinth agent inventory at `plinth-agents-generator/src/main/resources/agents.xml`
- **WHEN** `agents.xsd` is applied to agent definition sources
- **THEN** the schema does not claim `<agent-inventory>` as a root
- **AND** the inventory continues to list ordered `<agent file="….xml"/>` entries for installer discovery
- **AND** `AgentIndexes.agentFiles()` maps each inventory XML `@file` to the corresponding generated `.md` name for installers

### Requirement: XML sources under agents/ with Markdown generation for the skills bridge

Agent contracts SHALL be authored as XML under `plinth-agents-generator/src/main/resources/agents/` and SHALL emit Cursor-compatible Markdown consumed by the existing skills-generator bridge.

#### Scenario: XML under agents/ validated with agents.xsd

- **GIVEN** nine embedded agents such as `plinth-business-analyst`
- **WHEN** contributor sources are maintained for this change
- **THEN** each agent has an XML document at `plinth-agents-generator/src/main/resources/agents/plinth-*.xml`
- **AND** each document references `agents.xsd` for local validation (for example `xsi:noNamespaceSchemaLocation="../agents.xsd"`)
- **AND** valid OpenSpec examples under `examples/xml/plinth-*.xml` mirror that shape

#### Scenario: Java generates Markdown for the skills bridge

- **GIVEN** validated agent XML under `plinth-agents-generator/src/main/resources/agents/`
- **WHEN** the `plinth-agents-generator` build runs Markdown generation (`AgentMarkdownGenerator` / `AgentMarkdownRenderer`, wired via `exec-maven-plugin` at `process-classes`)
- **THEN** Cursor-compatible `.md` files (YAML frontmatter + body) are written under `plinth-agents-generator/target/generated-resources/agents` and `target/classes/agents` (not under `src/main/resources/agents`)
- **AND** `plinth-skills-generator` copies `*.md` only from `plinth-agents-generator/target/generated-resources/agents` into skill-reference assets
- **AND** hand-authored Markdown is not the sole source of truth for installer payloads

#### Scenario: Map identity fields for generated frontmatter

- **GIVEN** agent XML uses `@id` and `<metadata>/<description>` (with title/role/goal as required companions)
- **WHEN** Markdown is generated
- **THEN** YAML frontmatter `name` derives from `@id` and `description` from `<metadata>/<description>`
- **AND** mission, responsibility, and routing prose remain in `<goal>` CDATA (and optional structured sections) rather than a separate `<frontmatter>` XML element

### Requirement: Behavioral validation outside XSD

Kind-specific section requirements and delegation semantics SHALL remain enforced by existing specification and test assets, not by Schematron or `@kind` schema profiles.

#### Scenario: Preserve behavioral enforcement path

- **GIVEN** nine agent contracts with distinct Markdown body shapes after generation
- **WHEN** `agents.xsd` and XML sources are introduced
- **THEN** structural validation is limited to XSD on individual `<agent>` documents
- **AND** `analysis-design-agents`, `AgentIndexesTest`, and agent Gherkin acceptance tests continue to enforce behavioral contracts
- **AND** the design does not introduce Schematron rules or `@kind` validation profiles for required sections

### Requirement: PML schema relationship documentation

The schema design SHALL document how `agents.xsd` relates to staged PML `pml.xsd` 0.9.0 and to `pml-workflow.xsd`.

#### Scenario: Document parallel skill and agent schema families

- **GIVEN** skills validate against PML `<prompt>` documents (published skill schemas and the staged OpenSpec `pml.xsd` 0.9.0 mirror)
- **AND** workflows may validate against `pml-workflow.xsd`
- **WHEN** the agent schema design is published
- **THEN** the design states that `agents.xsd` is a parallel extension for orchestration personas that shares element shapes with staged `pml.xsd` 0.9.0 (`metadata`, `role`, `goal`, optional `constraints` / `steps` / `output-format` / `safeguards`)
- **AND** the design states that agents are not a `<prompt>` reuse and must not require a skill document root
- **AND** the design states that workflow graph structure remains in `pml-workflow.xsd` while agent goals may reference workflow commands in prose
- **AND** the design references ADR-001 XSD-to-Markdown generation precedent

### Requirement: Representative XML examples

The change SHALL provide representative valid and invalid XML examples for the agent contract surface.

#### Scenario: Valid plinth agent examples

- **GIVEN** the shipped `plinth-*.xml` contracts under `plinth-agents-generator/src/main/resources/agents/`
- **WHEN** examples are authored for the schema design
- **THEN** OpenSpec `examples/xml/plinth-*.xml` cover valid agent-body structures for those agents
- **AND** examples are stored with the OpenSpec change for reviewer inspection

### Requirement: Migration notes from Markdown-first sources

The change SHALL document migration guidance from Markdown-first agent assets to schema-validated XML sources and generated Markdown used by the skills bridge.

#### Scenario: Slice-ordered migration guidance

- **GIVEN** agents previously lived as Markdown-first `agents/*.md` contracts with YAML frontmatter
- **WHEN** migration guidance is recorded in this change's design artifacts
- **THEN** they describe moving sources to XML under `src/main/resources/agents/`
- **AND** they map frontmatter to `<metadata>` + `@id` and body content to `<role>` / `<goal>` plus optional structured sections
- **AND** they state that generated `.md` remains the skill-bridge input
- **AND** they preserve agent-by-agent slice order (analyst → architect → performance → framework coders → `plinth-no-java` → tech lead)

