## ADDED Requirements

### Requirement: Skill-reference XML sources track the current published PML schema version

Every in-scope XML file under `plinth-skills-generator/src/main/resources/skill-references/` SHALL declare `xsi:noNamespaceSchemaLocation` for the PML 0.9.0 `pml.xsd`, and SHALL declare its author metadata using `<authors>` containing one or more `<author>` elements instead of a bare `<author>` element under `<metadata>`.

#### Scenario: A previously unmigrated skill-reference source adopts schema 0.9.0 and authors/author metadata

- **GIVEN** a skill-reference XML source under `plinth-skills-generator/src/main/resources/skill-references/` declares `xsi:noNamespaceSchemaLocation` for the PML 0.8.0 `pml.xsd` and a bare `<author>` element directly under `<metadata>`
- **WHEN** the source is migrated
- **THEN** its `xsi:noNamespaceSchemaLocation` points to the PML 0.9.0 `pml.xsd`
- **AND** its metadata declares `<authors>` containing one `<author>` element with the original author name
- **AND** `xmllint --noout` validates the file against the PML 0.9.0 schema with no errors

#### Scenario: An already-migrated multi-author skill-reference source only has its schema pointer bumped

- **GIVEN** a skill-reference XML source already declares `<authors>` containing more than one `<author>` element
- **WHEN** the source is migrated
- **THEN** its `xsi:noNamespaceSchemaLocation` points to the PML 0.9.0 `pml.xsd`
- **AND** its existing `<authors>` element and every `<author>` child are unchanged
- **AND** `xmllint --noout` validates the file against the PML 0.9.0 schema with no errors

#### Scenario: The preserved authors block is byte-identical, not merely valid

- **GIVEN** a skill-reference XML source already declares `<authors>` containing more than one `<author>` element
- **WHEN** the source is migrated and a diff is taken between its pre-migration and post-migration content
- **THEN** the only differing line is the `xsi:noNamespaceSchemaLocation` value
- **AND** the `<authors>` element and every `<author>` child are byte-identical in content, order, and whitespace to their pre-migration state

#### Scenario: No in-scope skill-reference source is left on the deprecated shape

- **GIVEN** every in-scope skill-reference XML source under `skill-references/` (excluding the schema-less files under `skill-references/assets/questions/`) has been migrated
- **WHEN** `skill-references/` is searched for the PML 0.8.0 URL or a bare `<author>` element directly under `<metadata>`
- **THEN** no matches are found

### Requirement: Remote schema validation test tracks the same PML version as the sources it validates

`RemoteSchemaValidationTest` SHALL validate every skill-reference source against the same PML schema version those sources declare.

#### Scenario: The remote schema validation test tracks the same schema version as the sources it validates

- **GIVEN** `RemoteSchemaValidationTest.REMOTE_XSD` is updated to the PML 0.9.0 `pml.xsd` URL
- **WHEN** the parameterized `RemoteSchemaValidationTest` suite runs against every skill-reference source
- **THEN** every skill-reference source validates against PML 0.9.0 with no failures

### Requirement: Contributor documentation matches the schema skill-reference sources actually use

`AGENTS.md` (and its `CLAUDE.md` symlink) SHALL reference the same PML schema version that skill-reference XML sources declare.

#### Scenario: Contributor documentation matches the schema skill-reference sources actually use

- **GIVEN** `AGENTS.md`, together with its `CLAUDE.md` symlink, instructs contributors to follow the PML schema for skill-reference sources
- **WHEN** the migration completes
- **THEN** `AGENTS.md` references the PML 0.9.0 `pml.xsd` URL instead of 0.8.0

### Requirement: Skill-index sources and the locally forked skills.xsd remain unaffected

This migration SHALL NOT modify `plinth-skills-generator/src/main/resources/skills.xsd` or any `skill-indexes/*.xml` file, since they validate against an independently-evolving local schema fork per ADR-008, not the remote `pml.xsd` this change migrates.

#### Scenario: Skill-index sources and the locally forked skills.xsd remain unaffected

- **GIVEN** `skill-indexes/*.xml` sources validate against the locally forked `skills.xsd`, which still declares `<triggers>` and the deprecated `<author>` element as its own independently-evolving baseline
- **WHEN** the skill-reference migration to PML 0.9.0 completes
- **THEN** `skills.xsd` is unchanged
- **AND** every `skill-indexes/*.xml` source still validates against `skills.xsd` with no failures

### Requirement: No skill-reference source declares duplicate author values

Every skill-reference XML source's `<authors>` element SHALL NOT contain two `<author>` children with an identical (normalized) value. Because the PML schema places no uniqueness constraint on sibling `author` text, this SHALL be enforced by a repository-owned test, not by `xmllint`/schema validation alone.

#### Scenario: The schema alone does not reject a duplicate author value

- **GIVEN** a `<metadata>` block whose `<authors>` element contains two `<author>` children with the identical value
- **WHEN** the document is validated with `xmllint --noout --schema` against the PML 0.9.0 `pml.xsd`
- **THEN** validation succeeds, because `pml.xsd` imposes no uniqueness constraint on sibling `author` text

#### Scenario: A repository-owned test rejects a duplicate author value

- **GIVEN** a new JUnit test parameterized over `SkillReferences.xmlFilenames()` that parses each file's `<authors>/<author>` values
- **WHEN** the test runs against a skill-reference source whose `<authors>` element contains two `<author>` children with the identical value
- **THEN** the test fails with an assertion message identifying the offending file and the duplicated value

#### Scenario: All migrated skill-reference sources pass the duplicate-author check

- **GIVEN** all 196 in-scope skill-reference sources have been migrated to `<authors>`
- **WHEN** the new duplicate-author JUnit test runs against every one of them
- **THEN** the test passes for all 196 sources, including the 2 sources that already used `<authors>` before this migration
