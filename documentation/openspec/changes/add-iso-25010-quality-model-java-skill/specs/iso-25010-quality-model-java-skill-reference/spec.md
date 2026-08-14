## ADDED Requirements

### Requirement: ISO/IEC 25010:2023 quality-attribute review skill

The repository MUST define `814-regulations-iso-25010` as the ISO/IEC 25010:2023 skill for structured, repeatable Java Enterprise quality-attribute review.

#### Scenario: ISO/IEC 25010:2023 skill identifier is standardized

- **GIVEN** maintainers implement ISO/IEC 25010:2023 guidance in generator sources
- **WHEN** they create or reference the skill in XML, inventories, OpenSpec artifacts, or generated local skill output
- **THEN** the identifier is `814-regulations-iso-25010`
- **AND** the skill's chapters-summary reference cites ISO/IEC 25010:2023 as the official source of its quality model

#### Scenario: Local build generates the skill with its reference assets

- **GIVEN** the `814-regulations-iso-25010` skill sources exist under `plinth-skills-generator/src/main/resources`
- **WHEN** the maintainer runs `./mvnw clean install -pl plinth-skills-generator -am`
- **THEN** the build produces a `814-regulations-iso-25010` skill under `.agents/skills`
- **AND** that skill contains a chapters-summary reference and an engineering-examples reference

#### Scenario: Chapters summary covers all named ISO/IEC 25010:2023 quality characteristics

- **GIVEN** the `814-regulations-iso-25010` skill has been generated
- **WHEN** a reviewer opens its chapters-summary reference
- **THEN** it covers Functional Suitability, Performance Efficiency, Compatibility, Interaction Capability, Reliability, Security, Maintainability, Flexibility, and Safety

#### Scenario: Java architect applies ISO/IEC 25010:2023 guidance to a Java Enterprise system

- **GIVEN** a Java architect, tech lead, or reviewer is reviewing a Java Enterprise system
- **WHEN** the `814-regulations-iso-25010` skill is applied
- **THEN** the skill explains how each of the nine ISO/IEC 25010:2023 quality characteristics applies to the system under review
- **AND** the skill provides practical, Java-focused engineering review guidance for each quality characteristic
- **AND** the guidance frames findings as engineering evidence and action items rather than certification, compliance, or conformity conclusions
- **AND** the skill produces findings and action items using an ISO/IEC 25010:2023 engineering review report template

### Requirement: Relationship to `032-architecture-adr-non-functional-requirements` and other regulation skills

The ISO/IEC 25010:2023 skill MUST complement `032-architecture-adr-non-functional-requirements` and existing regulation skills without changing their workflows.

#### Scenario: Disambiguate structured review from conversational ADR discovery

- **GIVEN** a Java architect, tech lead, or reviewer needs guidance on ISO/IEC 25010:2023 quality characteristics
- **WHEN** they choose between `814-regulations-iso-25010` and `032-architecture-adr-non-functional-requirements`
- **THEN** `032-architecture-adr-non-functional-requirements` is used for interactive, conversational discovery that produces a new Architectural Decision Record for non-functional requirements
- **AND** `814-regulations-iso-25010` is used for a structured, repeatable quality-attribute review of a Java Enterprise system that produces a fixed engineering review report as reviewable evidence
- **AND** `032-architecture-adr-non-functional-requirements` remains unchanged by the addition of `814-regulations-iso-25010`

#### Scenario: Select ISO/IEC 25010:2023 alongside other regulation and quality-model skills

- **GIVEN** a Java system may involve quality-attribute, AI management, EU AI Act, privacy, cybersecurity, product, or operational concerns
- **WHEN** an agent chooses regulation or quality-model guidance
- **THEN** `814-regulations-iso-25010` is used for ISO/IEC 25010:2023 product-quality characteristic review, evidence, and reporting
- **AND** `813-regulations-iso-42001` is used for AI management system practices and GenAI software delivery risk
- **AND** `801-regulations-eu-ai-act` is used for EU AI Act classification, prohibited or high-risk AI, transparency, or general-purpose AI governance concerns
- **AND** `803-regulations-gdpr` is used for personal-data processing concerns
- **AND** `805-regulations-eu-cyber-resilience-act` is used for product cybersecurity and vulnerability-handling concerns
- **AND** multiple regulation and quality-model skills may be used together when the same Java system crosses those concern boundaries

### Requirement: Generator registration

The ISO/IEC 25010:2023 skill source MUST be registered in the generator inventory so local skill generation emits it.

#### Scenario: Skill inventory is wired correctly

- **WHEN** `plinth-skills-generator/src/main/resources/skills.xml` is inspected
- **THEN** it contains a skill entry with id `814` and `skillId="814-regulations-iso-25010"`
- **AND** that entry references the `814-regulations-iso-25010-chapters-summary` and `814-regulations-iso-25010-engineering-examples` reference ids

#### Scenario: Generate local ISO/IEC 25010:2023 skill

- **WHEN** `./mvnw clean install -pl plinth-skills-generator -am` is run
- **THEN** generated local skill output includes `.agents/skills/814-regulations-iso-25010/SKILL.md`
- **AND** generated local skill output includes `.agents/skills/814-regulations-iso-25010/assets/questions/814-iso-25010-engineering-review-questionnaire.md`
- **AND** generated local skill output includes `.agents/skills/814-regulations-iso-25010/assets/reports/814-iso-25010-engineering-review-report-template.md`
- **AND** generated references contain no unresolved include markers or broken local reference paths

### Requirement: Source and generated-output boundaries

The implementation MUST edit XML sources and validate generated local skill output without directly editing generated legacy or release outputs.

#### Scenario: Local install does not modify the public release output

- **GIVEN** the maintainer runs `./mvnw clean install -pl plinth-skills-generator -am` without the release profile
- **THEN** the public `skills/` directory is not modified
- **AND** the generated skill is available only under `.agents/skills/814-regulations-iso-25010`

#### Scenario: Release profile refreshes the public skill output

- **GIVEN** the `814-regulations-iso-25010` skill has been validated locally
- **WHEN** the maintainer runs `./mvnw clean install -pl plinth-skills-generator -am -P release`
- **THEN** the public `skills/814-regulations-iso-25010` output is refreshed with the chapters-summary and engineering-examples assets
- **AND** `.cursor/rules/` is not edited directly

### Requirement: Generated skill passes release validation gates

The generated `814-regulations-iso-25010` release skill MUST pass the repository's skill-quality and security scanning gates.

#### Scenario: Generated skill passes the skill-check format gate

- **GIVEN** the `814-regulations-iso-25010` skill has been generated under `skills/` via the release profile
- **WHEN** the maintainer runs `npx skill-check@latest skills --no-security-scan --format github`
- **THEN** the check reports no errors for the `814-regulations-iso-25010` skill

#### Scenario: Generated skill passes the strict behavioral security scan

- **GIVEN** the `814-regulations-iso-25010` skill has been generated under `skills/` via the release profile
- **WHEN** the maintainer runs `skill-scanner scan-all ./skills --recursive --use-behavioral --policy strict --fail-on-severity high`
- **THEN** the scan reports no high-severity findings for the `814-regulations-iso-25010` skill

#### Scenario: Edited XML sources are well-formed

- **GIVEN** the `814-regulations-iso-25010` XML sources have been authored under `plinth-skills-generator/src/main/resources`
- **WHEN** the maintainer runs `xmllint --noout` against each edited XML file
- **THEN** each file is reported well-formed
