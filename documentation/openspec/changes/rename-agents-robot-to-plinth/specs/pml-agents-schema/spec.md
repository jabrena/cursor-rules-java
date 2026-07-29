## MODIFIED Requirements

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

## Source and Derivation

- Source artifact: GitHub issue [#1094](https://github.com/jabrena/plinth/issues/1094).
- Derivation direction: issue #1094 -> `rename-agents-robot-to-plinth` requirements -> renamed XML source filenames and examples.
