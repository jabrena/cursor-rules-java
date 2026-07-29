# analysis-design-commands Specification

## Purpose
TBD - created by archiving change add-analysis-design-commands. Update Purpose after archive.
## Requirements
### Requirement: Analysis and design command bundle

The repository SHALL provide embedded command assets for `/create-worktree`, `/explore-design`, `/create-adr`, `/create-diagram`, and `/create-spec` through the `plinth-commands-generator` module. The generator MUST NOT install or advertise `/review-breaking-changes`; breaking-change review is owned by `@056-design-avoid-breaking-changes`. The generator MUST NOT install or advertise `/review-alignment`; that command is retired and its read-only review responsibility remains with `plinth-business-analyst` without a dedicated command contract.

#### Scenario: Install the command bundle without the retired breaking-change command

- **WHEN** a user runs the embedded command installation workflow and selects a supported destination
- **THEN** the installer copies the supported analysis/design/support commands together with the existing commands
- **AND** the installer does not copy `.cursor/commands/review-breaking-changes.md`
- **AND** the command inventory does not list `/review-breaking-changes`
- **AND** planning documentation points users to `@056-design-avoid-breaking-changes` for breaking-change review

#### Scenario: Retire command source and validation coverage

- **WHEN** command source files, command registration, command inventories, command-focused tests, and command acceptance prompt inventories are inspected
- **THEN** `/review-breaking-changes` is absent from active command sources
- **AND** command-focused tests no longer assert the retired command contract
- **AND** skill-focused acceptance coverage exists for `056-design-avoid-breaking-changes`
- **AND** active command sources are owned by `plinth-commands-generator`, not `plinth-skills-generator`

#### Scenario: Update README discoverability

- **WHEN** `README.md`, `README_ES.md`, and `README_ZH.md` are inspected
- **THEN** their planning command lists do not include `/review-breaking-changes`
- **AND** their planning skill lists or equivalent discoverability sections include `056-design-avoid-breaking-changes`
- **AND** no README link points to `.cursor/commands/review-breaking-changes.md`

#### Scenario: Retire the review-alignment command source and validation coverage

- **WHEN** command source files, command registration, command inventories, command-focused tests, and command acceptance prompt inventories are inspected
- **THEN** `/review-alignment` is absent from active command sources
- **AND** command-focused tests no longer assert the retired `/review-alignment` command contract
- **AND** the skills pipeline no longer bundles `/review-alignment` installation assets
- **AND** `GETTING-STARTED-WORKFLOWS.md`, `GETTING-STARTED-AGENTS.md`, `INVENTORY-COMMANDS-JAVA.md`, `java-commands-inventory-template.md`, and their `_ES`/`_ZH` variants no longer describe `/review-alignment` as an available command
- **AND** `plinth-commands-generator/src/main/resources/commands/implement-spec.xml` no longer requests `/review-alignment` on a material artifact conflict

#### Scenario: Update README discoverability for the retired alignment command

- **WHEN** `README.md`, `README_ES.md`, and `README_ZH.md` are inspected
- **THEN** their commands tables do not include `/review-alignment`

### Requirement: Command contracts

Each analysis/design command MUST document its purpose, accepted inputs, owning agent, associated skills or capabilities, workflow, outputs, and safeguards.

#### Scenario: Invoke an analysis or design command

- **WHEN** a project user opens an installed analysis/design command
- **THEN** the command identifies the artifacts it accepts and the artifact or report it produces
- **AND** it delegates substantive behavior to the named agent and skill rather than duplicating their complete prompts

#### Scenario: Create an OpenSpec change from complete issue context

- **WHEN** a project user invokes `/create-spec` with an issue identifier or URL
- **THEN** the command requires a maintainer-prepared sanitized artifact derived outside the agent context from the issue description and every paginated comment
- **AND** the artifact confirms complete description and comment coverage before scope assessment or OpenSpec authoring
- **AND** the command does not retrieve or ingest raw issue descriptions or comments
- **AND** it records the sanitized artifact as source context and reports conflicts or unclear requirements without inventing resolutions
- **AND** it stops and reports the failure when complete sanitized context is unavailable
- **AND** it routes OpenSpec creation through `@plinth-architect` using only `042-planning-openspec`
- **AND** it does not apply design skills `051`–`057`, `121`–`123`, `130`, or `034-architecture-design-exploration`
- **AND** it creates or updates OpenSpec artifacts only under `documentation/openspec` when edits are requested
- **AND** it validates OpenSpec structure before claiming the change is ready
- **AND** it reports changed files, validation evidence, source traceability, assumptions, and unresolved planning risks
- **AND** it documents `/create-spec` as the first workflow step before design refinement
- **AND** it does not modify the issue description or comments

#### Scenario: Create an OpenSpec change from non-issue artifacts

- **WHEN** a project user invokes `/create-spec` with an approved design, ADRs, implementation plan, existing OpenSpec change, or valid non-issue combination
- **THEN** the command creates or updates OpenSpec artifacts from those authoritative inputs without requiring tracker access
- **AND** it preserves the existing source-authority, scope-assessment, validation, and traceability workflow

### Requirement: Feature branch transition

The existing `/create-feature-branch` command SHALL support the optional transition from issue registration into repository-backed analysis, design, and implementation.

#### Scenario: Create a branch before design artifacts

- **WHEN** a user invokes `/create-feature-branch` with an issue/change identifier or an explicit supported branch type and description
- **THEN** the command verifies that the working tree is safe
- **AND** it creates a conventionally named local branch
- **AND** it allows plans, OpenSpec artifacts, ADRs, diagrams, and related documentation to be committed before application-code implementation
- **AND** it does not create a commit automatically

### Requirement: Isolated worktree creation

`/create-worktree` SHALL always create a new branch and linked Git worktree for isolated or parallel work without changing the user's current checkout.

#### Scenario: Create a worktree for a child change

- **WHEN** a user invokes `/create-worktree` with an issue/change identifier or explicit branch details and an optional target path and base reference
- **THEN** the command verifies or derives a fresh target branch and path
- **AND** it verifies that the selected base reference exists
- **AND** it does not reuse the current checkout or an existing worktree for the requested work
- **AND** it creates the branch and linked checkout using a non-destructive Git worktree operation
- **AND** it reports the created branch, absolute path, base reference, and cleanup command
- **AND** it does not commit, push, remove a worktree, delete a branch, or use force

#### Scenario: Requested worktree conflicts with existing state

- **WHEN** the requested branch is already checked out or the target path exists
- **THEN** the command derives the next unused branch name or target path
- **AND** it leaves existing branches, worktrees, and files unchanged

#### Scenario: Requested worktree has invalid base

- **WHEN** the base reference is invalid
- **THEN** the command stops and reports the conflict
- **AND** it leaves existing branches, worktrees, and files unchanged

### Requirement: Design exploration command routing

`/explore-design` SHALL route design refinement to `plinth-architect` using design skills `051`–`057`, `121`–`123`, and `130` after initial specification, and MUST apply `059-design-atdd` as the final alignment gate when reviewable OpenSpec artifacts are provided, without implementing application code.

#### Scenario: Refine an OpenSpec change after create-spec

- **WHEN** a project user invokes `/explore-design` with an OpenSpec change that has unresolved technical approaches or design gaps
- **THEN** the command routes design refinement through `@plinth-architect`
- **AND** it applies design skills `051-design-two-steps-methods`, `052-design-hamburger-method`, `053-design-simple-rules`, `054-design-tdd`, `055-design-parallel-change`, `056-design-avoid-breaking-changes`, `057-design-feature-toggles`, `121-java-object-oriented-design`, `122-java-type-design`, `123-java-design-patterns`, and `130-java-testing-strategies` when refining the approach
- **AND** it applies `059-design-atdd` after approved refinements are reflected in the reviewable OpenSpec artifacts and before requesting final design approval
- **AND** it reports the ATDD goal-to-criteria-to-task alignment outcome with evidence
- **AND** it does not apply `042-planning-openspec` or `034-architecture-design-exploration`
- **AND** it reports alternatives, trade-offs, recommended design direction, ADR candidates, and unresolved questions
- **AND** it refines the existing OpenSpec change without replacing initial OpenSpec authoring owned by `/create-spec`
- **AND** it does not invoke implementation behavior

#### Scenario: Continue to approval when ATDD alignment is ready

- **GIVEN** `/explore-design` has completed refinement of an OpenSpec change with an execution goal, acceptance criteria, and associated implementation and verification tasks
- **WHEN** `059-design-atdd` returns `ready`
- **THEN** the command includes the evidence-backed alignment result in its report
- **AND** it continues to the existing explicit design-approval step
- **AND** it does not treat the alignment review itself as maintainer approval

#### Scenario: Request changes when ATDD alignment is unresolved

- **GIVEN** `/explore-design` has completed refinement of an OpenSpec change with reviewable artifacts
- **WHEN** `059-design-atdd` returns `changes-requested` for at least one partial, missing, ambiguous, absent, or divergent finding
- **THEN** the command reports every unresolved finding and its available goal-to-criteria-to-task evidence
- **AND** it does not report the design direction or OpenSpec change as approved
- **AND** it does not silently add, remove, or rewrite the proposal, specification scenarios, or task checklist
- **AND** it asks the maintainer how the affected OpenSpec artifacts should be revised
- **AND** it reruns the alignment review after the maintainer-approved revisions are applied

#### Scenario: Explore design options from an issue

- **WHEN** a project user invokes `/explore-design` with an issue or user story
- **THEN** the command accepts the issue as input
- **AND** it reports alternatives, trade-offs, recommended design direction, ADR candidates, and unresolved questions
- **AND** it does not apply `059-design-atdd` unless a reviewable OpenSpec proposal, specification scenarios, and task checklist are also supplied
- **AND** it does not apply `042-planning-openspec`

#### Scenario: Apply feature-toggle guidance during design refinement

- **WHEN** a project user invokes `/explore-design` on a change that needs rollout or rollback controls after initial specification
- **THEN** the command applies `057-design-feature-toggles` guidance as part of the approved design direction

#### Scenario: Preserve functional and technical workflow boundaries

- **WHEN** `/explore-design` reviews and refines an OpenSpec change
- **THEN** initial OpenSpec proposal, design, specification, and task authoring remains owned by `/create-spec`
- **AND** user-story creation and Gherkin acceptance-file creation remain outside `/explore-design`
- **AND** the command does not modify source user stories, Gherkin files, ADRs, or application code

#### Scenario: Document explore-design contract surfaces

- **WHEN** command source assets and generated command prompts for `/explore-design` are inspected
- **THEN** the purpose states that the command improves the technical approach for an issue or OpenSpec change after initial specification
- **AND** the usage is `/explore-design <issue|openspec-change>`
- **AND** accepted inputs list `OpenSpec change with unresolved technical approaches or design gaps` immediately after `Issue or user story`
- **AND** the associated skills include `059-design-atdd` for the final OpenSpec alignment gate
- **AND** the command documents that it runs after `/create-spec`, not as the first workflow mission
