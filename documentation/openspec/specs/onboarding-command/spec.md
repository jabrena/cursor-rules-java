# onboarding-command Specification

## Purpose
TBD - created by archiving change add-onboarding-command. Update Purpose after archive.
## Requirements
### Requirement: Provide the onboarding command

The embedded command bundle SHALL provide `/onboarding` as a pre-issue repository-readiness command owned by `@plinth-architect` that coordinates `200-agents-md` and `042-planning-openspec` without duplicating their substantive workflows.

#### Scenario: Invoke onboarding before issue work

- **WHEN** a maintainer invokes `/onboarding` from a repository
- **THEN** the command checks root `AGENTS.md` and recursively discovers directories named `openspec`
- **AND** it determines the complete prerequisite state before starting a delegated workflow
- **AND** it reports preserved, created, initialized, skipped, or blocked outcomes
- **AND** it does not select or implement an issue

### Requirement: Preserve existing prerequisites independently

`/onboarding` MUST preserve an existing root `AGENTS.md` and an existing single OpenSpec project without modifying their contents or starting the corresponding delegated workflow.

#### Scenario: Preserve AGENTS.md while initializing OpenSpec

- **GIVEN** the repository root contains `AGENTS.md`
- **AND** the repository contains no directory named `openspec`
- **WHEN** the maintainer runs `/onboarding`
- **THEN** the existing `AGENTS.md` remains unchanged
- **AND** no `AGENTS.md` generation workflow is started
- **AND** missing OpenSpec initialization follows the selected-path workflow

#### Scenario: Preserve one nested OpenSpec project while creating AGENTS.md

- **GIVEN** the repository root does not contain `AGENTS.md`
- **AND** exactly one directory named `openspec` exists below the repository root
- **WHEN** the maintainer runs `/onboarding`
- **THEN** the existing OpenSpec directory and its contents remain unchanged
- **AND** no OpenSpec initialization workflow is started
- **AND** missing root guidance creation is delegated to `200-agents-md`

### Requirement: Delegate missing root guidance

When root `AGENTS.md` is absent and OpenSpec discovery is not ambiguous, `/onboarding` MUST delegate its creation to `200-agents-md` with the repository root as context.

#### Scenario: Create missing root guidance

- **GIVEN** the repository root does not contain `AGENTS.md`
- **AND** the repository contains no more than one directory named `openspec`
- **WHEN** the maintainer runs `/onboarding`
- **THEN** missing guidance creation is delegated to `200-agents-md`
- **AND** completing the delegated workflow creates `AGENTS.md` at the repository root
- **AND** `/onboarding` does not duplicate the skill's guidance-generation workflow

### Requirement: Initialize missing OpenSpec at a selected path

When recursive discovery finds no directory named `openspec`, `/onboarding` MUST ask the user to select the resulting OpenSpec directory, MUST offer `documentation/openspec` as the default, and MUST delegate initialization to `042-planning-openspec` using `openspec init` with the selected directory's parent as the initialization project root. The selected directory MUST be a normalized repository-relative path whose final segment is `openspec`.

#### Scenario: Accept the default initialization path

- **GIVEN** the repository contains no directory named `openspec`
- **WHEN** the maintainer runs `/onboarding`
- **AND** accepts the default OpenSpec path
- **THEN** initialization with `documentation` as the project root is delegated to `042-planning-openspec`
- **AND** completing the delegated workflow creates one OpenSpec project at `documentation/openspec`

#### Scenario: Select a custom initialization path

- **GIVEN** the repository contains no directory named `openspec`
- **WHEN** the maintainer runs `/onboarding`
- **AND** selects the resulting directory `architecture/openspec`
- **THEN** initialization with `architecture` as the project root is delegated to `042-planning-openspec`
- **AND** completing the delegated workflow creates one OpenSpec project at `architecture/openspec`
- **AND** the command does not also initialize the default path

#### Scenario: Reject an invalid selected path before mutation

- **GIVEN** the repository contains no directory named `openspec`
- **WHEN** the maintainer selects an absolute path, a path that escapes the repository, or a path whose final segment is not `openspec`
- **THEN** `/onboarding` reports why the selected path is invalid
- **AND** no delegated workflow is started
- **AND** no repository content is changed
- **AND** the command asks for a valid repository-relative OpenSpec directory or an explicit cancellation

#### Scenario: Selected-path initialization fails

- **GIVEN** the repository contains no directory named `openspec`
- **AND** the selected path cannot be initialized by the delegated workflow
- **WHEN** `042-planning-openspec` reports the failure
- **THEN** `/onboarding` reports that OpenSpec initialization did not complete
- **AND** it does not silently select a different path
- **AND** it does not claim that onboarding succeeded

### Requirement: Sequence missing-prerequisite delegation safely

When both prerequisites are missing, `/onboarding` MUST complete delegated OpenSpec initialization before starting delegated root guidance creation, MUST NOT run the interactive workflows concurrently, and MUST report partial completion without rolling back successfully created prerequisite content.

#### Scenario: OpenSpec initialization fails before root guidance starts

- **GIVEN** root `AGENTS.md` is absent
- **AND** the repository contains no directory named `openspec`
- **WHEN** delegated OpenSpec initialization fails
- **THEN** no `200-agents-md` workflow is started
- **AND** `/onboarding` reports that both prerequisites remain incomplete
- **AND** it does not claim that onboarding succeeded

#### Scenario: Root guidance creation fails after OpenSpec initialization

- **GIVEN** root `AGENTS.md` is absent
- **AND** the repository contains no directory named `openspec`
- **WHEN** delegated OpenSpec initialization succeeds
- **AND** delegated `200-agents-md` creation fails or is cancelled
- **THEN** the initialized OpenSpec project remains unchanged
- **AND** `/onboarding` reports OpenSpec as initialized and root guidance as incomplete
- **AND** it reports onboarding as partially complete rather than successful

#### Scenario: Retry after partial completion

- **GIVEN** a previous onboarding attempt initialized exactly one OpenSpec project
- **AND** root `AGENTS.md` remains absent
- **WHEN** the maintainer runs `/onboarding` again
- **THEN** the existing OpenSpec project is preserved
- **AND** no OpenSpec initialization workflow is started
- **AND** missing root guidance creation is delegated to `200-agents-md`

### Requirement: Stop before mutation on ambiguous OpenSpec discovery

When recursive discovery finds more than one directory named `openspec`, `/onboarding` MUST stop before changing repository content or starting any delegated workflow.

#### Scenario: Multiple OpenSpec projects are found

- **GIVEN** the repository contains more than one directory named `openspec`
- **WHEN** the maintainer runs `/onboarding`
- **THEN** onboarding stops before changing repository content
- **AND** every conflicting OpenSpec location is reported
- **AND** the ambiguity is identified as technical debt
- **AND** no `200-agents-md` or `042-planning-openspec` workflow is started

### Requirement: Make onboarding idempotent

Repeated `/onboarding` execution against a repository with root `AGENTS.md` and exactly one OpenSpec directory MUST produce no repository changes.

#### Scenario: Both prerequisites already exist

- **GIVEN** the repository root contains `AGENTS.md`
- **AND** exactly one directory named `openspec` exists in the repository
- **WHEN** the maintainer runs `/onboarding`
- **THEN** no repository content is changed
- **AND** no `AGENTS.md` generation workflow is started
- **AND** no OpenSpec initialization workflow is started
- **AND** the command reports the existing OpenSpec location

### Requirement: Limit OpenSpec onboarding to standard initialization

`/onboarding` MUST NOT detect project maturity, infer specifications from existing implementation, or create a custom specification baseline.

#### Scenario: Onboard a repository that already contains implementation

- **GIVEN** the repository contains implementation files
- **AND** contains no directory named `openspec`
- **WHEN** the maintainer completes `/onboarding`
- **THEN** the OpenSpec effect is limited to delegated `openspec init` at the selected path
- **AND** no custom specification baseline is created by `/onboarding`

