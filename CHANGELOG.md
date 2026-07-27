# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.18.0] 2026-07-27

### Added

- **Benchmark**:
  - Project benchmark harness under `benchmarks/` with reproducible scenario scorecards, including result samples generated with GitHub Copilot, Claude Sonnet 4.5, and Opus (#1012, #1052, #1057, #1068)

- **Skills**:
  - Five-lens problem exploration skills — problem framing, root cause analysis, assumption analysis, context mapping, and quality attribute discovery (`@021-problem-framing`, `@022-root-cause-analysis`, `@023-assumption-analysis`, `@024-context-mapping`, `@025-quality-attribute-discovery`) (#1071)
  - BDD design skill (`@058-design-bdd`) (#1060)
  - ATDD alignment review skill for classifying and reporting alignment between OpenSpec goals, acceptance criteria, and implementation/verification tasks (`@059-design-atdd`) (#1062)

- **Agents & commands**:
  - `/explore-problem` command to analyze an issue through five lenses and post a Functional Specification (#1043, #1071, #1076)
  - Generated YAML frontmatter metadata for all commands (#1075, #1081)
  - `create-spec` now reads complete issue context before drafting specs (#1078)
  - Moved planning ownership from Tech Lead to the Architect agent; decoupled `/explore-design` from `create-spec` (#1024, #1026, #1028)
  - Added an `implement-spec` readiness gate (#1080)
  - `/close-spec` command to archive a completed OpenSpec change (#1041)

- **PML & generators**:
  - Extracted `plinth-commands-generator` and `plinth-agents-generator` as standalone modules (mirroring `plinth-skills-generator`), each with its own XSD schema (`commands.xsd`, `agents.xsd`), manifest, and propagation/bridge tests; renamed the parent Maven artifact to `plinth` (#1035, #1036, #1037, #1038, #992, #993, #1042)

### Removed

- **Skills**:
  - Removed the `034-architecture-design-exploration` skill; its steps were absorbed directly into the `/explore-design` command now that the command runs after `/create-spec` instead of standing in as the first design step (#1024, #1026, #1028)

## [0.17.0] 2026-07-13

### Added

- **Skills**:
  - Design method skills for two-step changes, the hamburger method, simple design rules, TDD, parallel change, and breaking-change avoidance (`@051-design-two-steps-methods`, `@052-design-hamburger-method`, `@053-design-simple-rules`, `@054-design-tdd`, `@055-design-parallel-change`, `@056-design-avoid-breaking-changes`) (#925, #928, #930, #935, #937, #954)
  - Azure DevOps planning workflow for safe Azure Boards work item ID discovery, WIQL-backed queries, and create/update actions through `az` (`@045-planning-azure-devops`) (#966, #967)
  - Hexagonal Architecture technology guidance (`@707-technologies-hexagonal-architecture`) with framework agent routing support (#945)
  - Regulation engineering review skills for EU MiFID II, EU Market Abuse Regulation, EU Product Liability Directive, and ISO 42001 (`@810`-`@813`) with questionnaires, report templates, examples, and acceptance prompts (#949, #950)
  - JavaMoney dependency guidance for Maven dependency selection (`@111-java-maven-dependencies`) (#926)

- **Agents & commands**:
  - Acceptance-test prompt inventories and Gherkin coverage for agents, commands, and a broader set of generated skills (#951)

- **CI & validation**:
  - VirusTotal checks in the Maven workflow (#910)
  - Codex-backed OpenSpec workflow for generating specs from issues, including branch creation, generated PR labeling, and blocked-run tolerance (#914, #915, #917, #918)
  - Issue update workflow automation (#912)
  - Maven-based `markdown-validator` module with CLI, remote-link validation, unit tests, PMD configuration, and architecture tests (#946, #947)

### Changed

- **PML & generators**:
  - Updated skill index XML schema references to PML 0.8.0 and rendered the new plural `<authors>` metadata block as comma-separated author frontmatter in generated skill and reference Markdown (#938)

- **Skills**:
  - Hardened Maven and Mongock skill guidance for Snyk Agent Scan by requiring allowlisted XML query output for POM analysis and gating MongoDB container verification behind explicit approval and pinned/project-approved images (#938)
  - Hardened architecture diagram, profiling refactor, and Spring Boot acceptance-test skills for Snyk Agent Scan by replacing runtime PlantUML container execution with trusted local tooling and adding explicit trust gates for profiling and Gherkin prose inputs (`@033-architecture-diagrams`, `@163-java-profiling-refactor`, `@323-frameworks-spring-boot-testing-acceptance-tests`)
  - Expanded Flyway and Mongock migration guidance for Spring Boot, Quarkus, and Micronaut with migration safety, antipattern, and parallel-change coverage (#901, #908, #909)
  - Improved design pattern, Java testing strategy, acceptance testing, OpenAPI, and WireMock guidance with richer examples and reference structure (#943)
  - Split Maven search guidance into Maven Central lookup and project version update workflows (`@114-java-maven-search`) (#948)
  - Enforced minimum trigger coverage across generated skill indexes (#944)
  - Removed `h2` headings from guidance examples to keep generated skill content flatter and more consistent (#920)

- **Documentation & website**:
  - Refreshed README navigation and localized README content for the new design, regulation, command, and validator capabilities (#927, #933, #934, #945, #949, #950)

### Removed

- **Agents & commands**:
  - Removed retired command assets for `create-issue`, `create-plan`, and `kill-port`; command guidance now points to the supported command set (#951)

- **CI & validation**:
  - Removed the legacy script-based Markdown validator and validation script after moving Markdown validation into the Maven module (#946)

## [0.16.0] 2026-06-22

### Added

- **Skills**:
  - Command inventory and installation workflows for embedded project commands and agents (`@001-commands-inventory`, `@004-commands-installation`, `@005-agents-installation`) (#790, #791)
  - Architecture design exploration and expanded diagram support, including bounded-context and deployment diagram generation (`@034-architecture-design-exploration`, `@033-architecture-diagrams`) (#819, #820, #822, #852)
  - Java design patterns, Gatling performance testing, Spring Modulith, and SDKMAN project starter skills for Spring Boot, Quarkus, and Micronaut (`@123-java-design-patterns`, `@152-java-performance-gatling`, `@305-frameworks-spring-boot-modulith`, `@300-frameworks-spring-boot-create-project`, `@400-frameworks-quarkus-create-project`, `@500-frameworks-micronaut-create-project`) (#762, #763, #764, #781)
  - Mongock migration skills for Spring Boot, Quarkus, and Micronaut (`@316-frameworks-spring-mongodb-migrations-mongock`, `@416-frameworks-quarkus-mongodb-migrations-mongock`, `@516-frameworks-micronaut-mongodb-migrations-mongock`) (#761)
  - Technology guidance for SQL, MongoDB/NoSQL modeling, and Docker containers (`@704-technologies-sql`, `@705-technologies-nosql-mongodb`, `@706-technologies-containers-docker`) (#773, #776, #778, #838)
  - Regulation engineering review skills for the EU AI Act, DORA, GDPR, EU NIS2, Cyber Resilience Act, EU Data Act, Digital Services Act, and Digital Markets Act (`@801`-`@808`) (#830, #853, #862, #864, #865, #866, #867)

- **Agents & commands**:
  - Tech Lead, Java Performance, and default non-Java agents, plus updated architect, business analyst, and framework coder responsibilities (#807, #815)
  - Embedded command suite for ADRs, diagrams, plans, specs, feature branches, issues, worktrees, design exploration, issue implementation, profiling, benchmarking, and alignment review (#790, #814)

- **Examples**:
  - Kafka, MongoDB, and PostgreSQL Sakila infrastructure examples (#845, #849)
  - Maven multi-module acceptance fixture and deployment diagram examples (#850, #852)

- **CI & validation**:
  - Snyk agent scan validation in the Maven workflow (#841)
  - Skill acceptance prompt inventory for changed generated skills

### Changed

- **PML & generators**:
  - Separated local skill generation from release publishing and added runtime resource packaging (#777, #803)
  - Added command index generation and tests alongside the existing skill index workflow (#790, #791)
  - Split large skill references into modular files for architecture diagrams, Maven dependencies, Maven plugins, and object-oriented design (#784, #786, #802, #819)
  - Expanded `@181-java-observability-logging` with structured SLF4J examples, secure logging patterns, correlation context, log configuration guidance, and monitoring-oriented events
  - Aligned skill versions and refreshed generated local output for the 0.16.0 cycle (#782)

- **Agents**:
  - Reworked command routing around specialized agents and replaced coordinator-centered delegation with Tech Lead, Java, framework, performance, and non-Java agent flows (#807, #814, #815, #834)

- **Documentation & website**:
  - Added five-minute getting-started guidance, skill portal links, validation guidance, project references, and focused README navigation (#833, #835, #836, #851)
  - Moved workflow documentation into guides and normalized Chinese documentation suffixes from `_CN` to `_ZH` (#797, #798)
  - Published and generated the 0.15.0 release article and supporting site assets (#752)
  - Clarified project limitations and human PR policy (#799, #842)

### Removed

- **Agents & commands**:
  - Removed the legacy `robot-coordinator` agent and replaced the narrow `kill-port-8820` command with a generic `kill-port` command (#807, #790)

- **Documentation**:
  - Removed `README_CN.md` after migrating Chinese documentation to `README_ZH.md` (#797)

## [0.15.0] 2026-06-01

### Added

- **Skills**:
  - Framework validation skills for Spring Boot, Quarkus, and Micronaut (`@303-frameworks-spring-boot-validation`, `@403-frameworks-quarkus-validation`, `@503-frameworks-micronaut-validation`) (#688)
  - Framework security skills for Spring Boot, Quarkus, and Micronaut (`@304-frameworks-spring-boot-security`, `@404-frameworks-quarkus-security`, `@504-frameworks-micronaut-security`) (#688)
  - Kafka messaging skills for Spring Boot, Quarkus, and Micronaut (`@314-frameworks-spring-kafka`, `@414-frameworks-quarkus-kafka`, `@514-frameworks-micronaut-kafka`) (#710)
  - MongoDB persistence skills for Spring Boot, Quarkus, and Micronaut (`@315-frameworks-spring-mongodb`, `@415-frameworks-quarkus-mongodb`, `@515-frameworks-micronaut-mongodb`) (#710)
  - High-performance refactoring skill (`@145-java-refactoring-high-performance`) (#689)
  - Micrometer metrics skill (`@182-java-observability-metrics-micrometer`) and OpenTelemetry tracing skill (`@183-java-observability-tracing-opentelemetry`) (#707)
  - Exception-handling skill renumbered to `@126-java-exception-handling` (from `@123`) (#727)
  - Docker-based CATS fuzz-testing workflow and assets for `@703-technologies-fuzzing-testing` (#728)

- **CI:**
  - [Cisco AI Skill Scanner](https://github.com/cisco-ai-defense/skill-scanner) in the Maven workflow with strict behavioral policy (#726)

### Changed

- **PML & generators**:
  - Standardized generated `SKILL.md` files with a defined workflow section across skills (#682, #683, #684)
  - `@181-java-observability-logging` refocused; metrics and tracing guidance split into dedicated skills (#707)
  - Renamed OpenTelemetry tracing skill from `@183-observability-tracing-opentelemetry` to `@183-java-observability-tracing-opentelemetry` for naming consistency with `@182-java-observability-metrics-micrometer`

- **Agents**:
  - Extended `robot-spring-boot-coder`, `robot-quarkus-coder`, and `robot-micronaut-coder` with validation, security, Kafka messaging, and MongoDB responsibilities and skill references (#688, #710)
  - Wired specialized coders and `robot-java-coder` to additional 0.15.0 skills: `@126-java-exception-handling`, `@145-java-refactoring-high-performance`, `@182-java-observability-metrics-micrometer`, `@183-java-observability-tracing-opentelemetry`, and `@703-technologies-fuzzing-testing`
  - Updated `robot-coordinator` framework identification and delegation briefs to route validation, security, Kafka, and MongoDB work to the framework coders (#688, #710)

- **Documentation:**
  - Improved project usage guidance in `README.md` and skills documentation (#732, #733)
  - Added localized README translations (`README_ES.md`, `README_ZH.md`) with a language switcher in `README.md`, refreshed the Goal statement, and documented sync requirements in `AGENTS.md`
  - Third-party tools and links documented in `documentation/THIRD-PARTIES.md` (#734)

## [0.14.0] 2026-04-12

### Added

- **Skills**:
  - `@001-skills-inventory` checklist skill that emits `INVENTORY-SKILLS-JAVA.md` in the project root using the embedded system-prompts template
  - `@002-agents-inventory` checklist skill that emits `INVENTORY-AGENTS-JAVA.md` with the embedded agents table and installation targets
  - `@003-agents-installation` interactive installer that copies the six embedded robot agents into `.cursor/agents` or `.claude/agents`
  - Add INVEST validation to agile user-story workflow in `@014-agile-user-story` (#633)
  - Maven Central search guidance skill (`@114-java-maven-search`) (#605)
  - OpenSpec adoption for project change management in `@042-planning-openspec` (#620, #621, #616)
  - GitHub issue management workflow support (`@043-planning-github-issues`) (#607)
  - Jira planning support and guidance improvements in `@044-planning-jira` (#631, #641, #642)
  - OpenAPI guidance skill `@701-technologies-openapi` (#635)
  - WireMock guidance skill `@702-technologies-wiremock` (#636)
  - Fuzz testing skill `@703-technologies-fuzzing-testing` and CATS example coverage (#634, #646)
  - Cursor and Claude plugin support documentation/workflow (#622)

- **Rules:**
  - Added an ADR documenting the decision to drop Rules in favor of Skills.

- **Examples:**
  - Reduced CI pipeline duration by removing non-essential examples

## [0.13.0] 2026-03-30

### Added

- **Skills**:
  - ER diagram support in architecture diagrams guidance (`@033-architecture-diagrams`) (#511)
  - Plan mode skill enhancements (`@040-planning-plan-mode`) (#488)
  - JIB coverage and improved skill summaries (`@112-java-maven-plugins`) (#522)
  - Skill for Java testing strategies (`@130-java-testing-strategies`) (#523)
  - Spring Boot support (`@301-frameworks-spring-boot-core`, `@302-frameworks-spring-boot-rest`, `@311-frameworks-spring-jdbc`, `@312-frameworks-spring-data-jdbc`, `@313-frameworks-spring-db-migrations-flyway`, `@321-frameworks-spring-boot-testing-unit-tests`, `@322-frameworks-spring-boot-testing-integration-tests`, `@323-frameworks-spring-boot-testing-acceptance-tests`) (#502)
  - Quarkus support (`@401-frameworks-quarkus-core`, `@402-frameworks-quarkus-rest`, `@411-frameworks-quarkus-jdbc`, `@412-frameworks-quarkus-panache`, `@413-frameworks-quarkus-db-migrations-flyway`, `@421-frameworks-quarkus-testing-unit-tests`, `@422-frameworks-quarkus-testing-integration-tests`, `@423-frameworks-quarkus-testing-acceptance-tests`) (#560)
  - Micronaut support (`@501-frameworks-micronaut-core`, `@502-frameworks-micronaut-rest`, `@511-frameworks-micronaut-jdbc`, `@512-frameworks-micronaut-data`, `@513-frameworks-micronaut-db-migrations-flyway`, `@521-frameworks-micronaut-testing-unit-tests`, `@522-frameworks-micronaut-testing-integration-tests`, `@523-frameworks-micronaut-testing-acceptance-tests`) (#573, #584)

- **Agents**:
  - Business Analyst (#547)
  - Coordinator agent (#547)
  - Java agent (#547)
  - Spring Boot agent (#502)
  - Quarkus agent (#560)
  - Micronaut agent (#573, #584)

### Changed

- **PML & generators**:
  - Migrated XML rule sources to **PML Schema 0.7.0**; generator validates against https://jabrena.github.io/pml/schemas/0.7.0/pml.xsd (#525).

- **Skills**:
  - Improved the way to activate Skills (#574).
  - Reduced complexity for ADR discovery on functional requirements (`@031-architecture-adr-functional-requirements`) (#532).
  - C4 model guidance limited to levels 1–3 (`@033-architecture-diagrams`) (#504).
  - Improved `@113-java-maven-documentation` (#529).

### Removed

- Structural Concurrency guidance removed from `@125-java-concurrency` while the feature remains in preview (#530).

## [0.12.0] 2026-03-08

### Added

- **Skills & Agent system**:
  - SKILL generator for Cursor agent skills (#421).
  - Skills support evolution (v1–v4) with Skill JARs and a validation pipeline (#416, #418, #420, #423, #424).
  - Improved skill generation and minimum requirements (#417, #427, #451).

- **New Cursor Rules**:
  - `@132-java-testing-integration-testing` with WireMock support (#443).
  - ArchUnit support in `@111-java-maven-dependencies` (#445).

- **Maven rules & docs**:
  - Centralized version management and best practices for multi-module POM in `@110-java-maven-best-practices` (#441).
  - Cyclomatic complexity analysis support in `@112-java-maven-plugins` (#439).
  - `DEVELOPER.md` and plugin catalog in `@113-java-maven-documentation` (#438).
  - Minimum Maven compiler support in `@112-java-maven-plugins` (#433).

- **Documentation & ADRs**:
  - System prompt to generate AGENTS.md files: `@173-java-agents` (#436).
  - Split Java documentation into ADR capabilities in `@170-java-documentation` and `@171-java-adr` (#455).

### Changed

- **PML migration**:
  - Migrated XML rule sources to PML Schema 0.5.0; generator validates against https://jabrena.github.io/pml/schemas/0.5.0/pml.xsd.

## [0.11.0] 2025-09-29

### Added

- **Behaviours**:
  - Added tone variations to consultative and progressive learning behaviours (#330).
  - Added tone support for interactive prompts.

- **Website**:
  - Added a website covering system prompts for Java: [jabrena/plinth](https://github.com/jabrena/plinth)

- **Educational content**:
  - New "System Prompts for Java" course (#335). [jabrena/plinth](https://github.com/jabrena/plinth)

- **Documentation & Diagrams**:
  - ADR-003 documenting JBake website generation strategy (#326).
  - Added UML State Machine diagram support to documentation rule `@170-java-documentation` (#310).

- Added initial support for AGENTS.md https://agents.md/

### Changed

- **Platform & Build**:
  - Upgraded project to Java 25; updated Maven Java version in builds (#315, #343).

- **Cursor rules**:
  - Extensive refinements across multiple rules (dependencies, plugins, secure coding, concurrency, exception handling, functional programming, documentation, diagrams) (#309, #341).
  - Decoupled system prompts from behaviours and added learning behaviour; reorganized generator templates and inventory (#309).
  - All examples use single-type imports (no wildcard `import ...*`)
  - Updated profiling script to add support for Async-profiler 4.1: https://github.com/async-profiler/async-profiler/releases/tag/v4.1
  - Improved JFR support

### Removed

- **Cursor rules**:
  - Removed `@123-java-general-guidelines` (superseded by specialized rules) (#301).

## [0.10.0] 2025-09-04

### Added

- **New Cursor Rules**:
  - `@127-java-exception-handling`: Comprehensive cursor rule for Java exception handling with best practices and examples
  - `@128-java-generics`: Advanced cursor rule covering Java generics patterns, bounded types, and type erasure workarounds
  - `@170-java-documentation`: Specialized cursor rule for generating comprehensive Java documentation with C4 diagrams and UML

- **Enhanced Development Tools & Scripts**:
  - JMH (Java Microbenchmark Harness) support for Maven projects without modules
  - Enhanced Maven plugins cursor rule with comprehensive JMH integration

- Tested the project for Cursor, Cursor CLI, Claude Code, GitHub Copilot, and JetBrains Junie. Further information: https://github.com/jabrena/plinth/blob/main/docs/reviews/review-20250829.md

### Changed

- **File Extension Migration**:
  - **Breaking Change**: Renamed all cursor rule files from `.mdc` to `.md` extension for better readability

- **Generator System Enhancements**:
  - Added remote XML schema fetching capabilities (https://github.com/jabrena/pml)

## [0.9.0] 2025-07-22

### Added

- **Version Control for Cursor Rules**:
  - All cursor rules include version control

- **New Cursor Rules**:
  - `@127-java-functional-exception-handling`: Comprehensive cursor rule for handling exceptions in functional programming style with Either & Optional
  - `@111-java-maven-dependencies`: Focused cursor rule for Maven dependency management
  - `@112-java-maven-plugins`: Dedicated cursor rule for Maven plugins management

- **Enhanced Documentation & Getting Started Guide**:
  - New `GETTING-STARTED.md`: comprehensive guide for new users
  - `docs/articles/prompt-quality-framework.md`: Framework for evaluating prompt quality and cursor rule effectiveness

- **Performance & Profiling Enhancements**:
  - Enhanced JMeter integration with improved scripts and detailed performance analysis reports

- **Project Examples & Templates**:
  - `examples/maven-demo-ko/`: New negative example project demonstrating common Maven pitfalls
  - Enhanced generator template system with modular fragments
  - New behavioral templates for consultative interaction patterns

### Changed

- **Cursor Rules Architecture Overhaul**:
  - **Externalized Behavior**: Moved common behavioral patterns from individual rules to shared templates for consistency
  - **Enhanced Structure**: All cursor rules now include explicit sections for:
    - Detailed constraints and preconditions
    - Standardized output format specifications
    - Comprehensive safeguards and verification steps
  - **Consultative Approach**: Reinforced interactive, consultative methodology across all rules

- **Rule Organization & Refinement**:
  - Split `@111-java-maven-deps-and-plugins` into two focused, specialized rules for better clarity
  - Simplified and refined questioning approach in Maven-related cursor rules

- **Quality & Consistency Improvements**:
  - Standardized all cursor rules with enhanced constraints, output formats, and safeguards
  - Improved template system with shared behavioral patterns and reduced duplication
  - Enhanced XML schema validation and XSL transformation consistency

### Removed

- **Template Consolidation**:
  - Removed redundant template files that were consolidated into the generator system:
    - `java-checklist-template.md`
    - `java-maven-deps-template.md`
    - `java-maven-plugins-template.md`
    - `java-performance-script-template.md`
  - Removed old combined `@111-java-maven-deps-and-plugins.md` (split into separate rules)
  - Removed `@100-java-checklist-guide.md` (replaced with cursor rules list)

## [0.8.0] 2025-07-11

### Added

- **XML-Based Generation System**: Implemented comprehensive XML/XSL transformation system for generating cursor rules
  - New `generator` module with XML schema validation (pml.xsd)
  - XSL transformations for consistent markdown generation
  - Automated generation of cursor rules from XML definitions
- **Architecture Decision Records (ADRs)**: Added formal documentation for architectural decisions
  - ADR-001: Generate cursor rules from XML files
  - ADR-002: Configure cursor rules with manual scope
- **Comprehensive User Guide**: Added `SYSTEM-PROMPTS-JAVA.md` as a complete reference guide for all cursor rules
- **Build Infrastructure Improvements**:
  - JBang script for markdown validation
  - GitHub workflow artifact upload for generated cursor rules

### Changed

- **Cursor Rule Interaction Model**: All cursor rules now use consultative approach instead of prescriptive
  - Present 2-3 solution options with pros/cons for each improvement
  - Wait for user choice before implementing changes
  - Interactive approach with clear problem identification
- **Manual Scope Configuration**: All cursor rules configured with manual scope by design to improve performance
  - Eliminates automatic activation that caused performance degradation
  - Requires explicit user activation for better control and deterministic results
- **Enhanced Safeguards**: All cursor rules now include verification commands (`mvn clean verify` or `./mvnw clean verify`)

### Technical Improvements

- **Schema Validation**: Implemented XSD schema validation for all XML cursor rule definitions
- **Build Process**: Enhanced build pipeline with XML validation and automated rule generation
- **Code Quality**: Improved code formatting and consistency across all generated cursor rules
- **Error Handling**: Better error handling and validation in generation process

## [0.7.0] 2025-06-30

### Added

- **Java Profiling Support**: Added comprehensive profiling cursor rules (#81, #88, #91)
  - `@161-java-profiling-detect` for detecting performance issues
  - `@162-java-profiling-analyze` for analyzing profiling results
  - `@164-java-profiling-compare` for comparing profiling data
- **Java Checklist Guide**: Added `@100-java-checklist-guide` cursor rule to help developers use cursor rules effectively (#59)
- **Maven Documentation**: Added `@112-java-maven-documentation` cursor rule to generate README-DEV.md from existing pom.xml files
- **Maven Dependencies & Plugins**: Added `@111-java-maven-deps-and-plugins` cursor rule for better Maven dependency management
- **Performance Testing**: Added `@151-java-performance-jmeter` cursor rule for JMeter-based performance testing (#97)
- **Cloud Examples**: Added serverless examples with native image support
  - AWS Lambda Hello World example with GraalVM native image configuration
  - Azure Function Hello World example with GraalVM configuration and Azure-specific setup
- **Maven Demo**: Added complete Maven demo project with Euler problem examples and proper testing structure
- **Quarkus Demo**: Added Quarkus framework example with profiling support and Docker configurations
- **Performance Examples**: Added specialized demo projects (#82, #86, #95)
  - Spring Boot comprehensive demo with film query service, PostgreSQL integration, and full testing suite
  - Spring Boot Memory Leak Demo with profiling tools and detailed analysis documentation
  - Spring Boot Performance Bottleneck Demo with CPU profiling and optimization examples
  - Spring Boot JMeter Demo for performance testing integration
- **Template System**: Added comprehensive template files to support cursor rule generation
  - Java checklist templates for systematic development approaches
  - Maven dependencies and plugins templates for project setup
  - Performance testing script templates
  - Profiling script templates for application analysis
- **Documentation**: Added extensive documentation and diagrams
  - Cursor interaction sequence diagrams

### Changed

- **Rule Organization**: Reorganized cursor rule numbering system for better categorization
- **Documentation**: Significantly improved README and development guides (#74, #90)
- **Maven Plugins**: Improved cursor rules for Maven plugins with better examples and guidance (#54, #56)
- **Modularization**: Improved project structure to make cursor rules more modular (#105)

### Removed

- **Cache Files**: Removed Maven cache files that were not useful for daily development work (#44)
- **Logging**: Removed unnecessary MDC behavior that added complexity without clear criteria (#89)
- **Redundant Rules**: Removed or consolidated several cursor rules for better organization
  - Removed `@122-java-integration-testing` (consolidated into other testing rules)
  - Removed framework-specific rules to separate repositories:
    - `@301-framework-spring-boot` (moved to separate Spring Boot rules project) (#105)
    - `@304-java-rest-api-design` (moved to separate Spring Boot rules project)
    - `@500-sql` (moved to Spring Boot rules)

## [0.6.0] 2025-05-30

### Added

- Added new cursor rules about Maven dependencies & plugins

### Changed

- Removed Cursor rules about Books for clarity
- Moved the Cursor rule about acceptance criteria as part of the repository about [Agile](https://github.com/jabrena/cursor-rules-agile)
- Increased consistency in all cursor rules, now all examples use asserts from AssertJ

## [0.5.0] 2025-05-20

### Added

- Added new cursor rules (Maven, Acceptance Criteria, Object-oriented design, Type Design, Secure coding guidelines, REST API Design)
- Added template for future cursor rules
- Added JEP inventory

### Changed

- Updated all cursor rules

## [0.4.0] 2025-04-27

### Added

- Added new cursor rules (Refactoring, Unit Testing & Integration testing)

### Changed

- Updated all cursor rules

## [0.3.0] 2025-04-06

### Added

- Added new cursor rules (SQL, Logging, Modern Java features)

### Changed

- Updated cursor rules (Java, Effective Java, Concurrency, Functional programming, Data-Oriented programming, Pragmatic Unit Testing, Spring Boot & Quarkus)


## [0.2.0] 2025-03-01

### Added

- Added new cursor rules (Pragmatic Unit Testing, Quarkus)

### Changed

- Updated cursor rules (Java, Effective Java, Concurrency, Functional programming, Data-Oriented programming & Spring Boot)

## [0.1.0] 2025-02-09

### Added

- Added initial cursor rules (Java, Effective Java, Concurrency, Functional programming, Data-Oriented programming & Spring Boot)

[0.15.0]: https://github.com/jabrena/plinth/compare/0.14.0...0.15.0
[0.14.0]: https://github.com/jabrena/plinth/compare/0.13.0...0.14.0
[0.13.0]: https://github.com/jabrena/plinth/compare/0.12.0...0.13.0
[0.12.0]: https://github.com/jabrena/plinth/compare/0.11.0...0.12.0
[0.11.0]: https://github.com/jabrena/plinth/compare/0.10.0...0.11.0
[0.10.0]: https://github.com/jabrena/plinth/compare/0.9.0...0.10.0
[0.9.0]: https://github.com/jabrena/plinth/compare/0.8.0...0.9.0
[0.8.0]: https://github.com/jabrena/plinth/compare/0.7.0...0.8.0
[0.7.0]: https://github.com/jabrena/plinth/compare/0.6.0...0.7.0
[0.6.0]: https://github.com/jabrena/plinth/compare/0.5.0...0.6.0
[0.5.0]: https://github.com/jabrena/plinth/compare/0.4.0...0.5.0
[0.4.0]: https://github.com/jabrena/plinth/compare/0.3.0...0.4.0
[0.3.0]: https://github.com/jabrena/plinth/compare/0.2.0...0.3.0
[0.2.0]: https://github.com/jabrena/plinth/compare/0.1.0...0.2.0
[0.1.0]: https://github.com/jabrena/plinth/releases/tag/0.1.0
