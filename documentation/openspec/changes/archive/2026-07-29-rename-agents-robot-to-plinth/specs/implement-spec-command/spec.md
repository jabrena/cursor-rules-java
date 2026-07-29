## MODIFIED Requirements

### Requirement: Spec implementation command

The command bundle SHALL provide `/implement-spec`, and the command MUST use `@plinth-tech-lead` to coordinate delivery through the existing Java, Spring Boot, Quarkus, Micronaut, or non-Java coder agent.

#### Scenario: Install the implementation command

- **WHEN** a user installs the embedded command bundle
- **THEN** the bundle contains `implement-spec.md`
- **AND** it does not contain `implement.md`
- **AND** the inventory identifies `/implement-spec` as an implementation command

### Requirement: Executable artifact inputs

`/implement-spec` MUST accept an approved implementation plan or an OpenSpec change containing a validated `tasks.md` as its execution contract.

#### Scenario: Implement from an approved plan

- **WHEN** a user invokes `/implement-spec` with an approved implementation plan
- **THEN** `@plinth-tech-lead` delegates implementation against the plan milestones and verification steps

#### Scenario: Implement from an OpenSpec change

- **WHEN** a user invokes `/implement-spec` with an OpenSpec change containing validated incomplete tasks
- **THEN** `@plinth-tech-lead` delegates implementation against those tasks
- **AND** marks tasks complete only after acceptance criteria and focused checks pass

#### Scenario: Referenced issue has no executable artifact

- **WHEN** a user supplies only an issue and repository policy requires structured planning
- **THEN** the command stops implementation
- **AND** directs the user to create an approved plan or OpenSpec change first

### Requirement: Controlled delegation and verification

`/implement-spec` MUST require framework-aware coder routing, dependency and file-ownership controls, evidence-based completion, and focused validation reporting within the selected execution artifact.

#### Scenario: Delegate independent task groups

- **WHEN** the execution artifact contains independent groups without dependency or file-ownership conflicts
- **THEN** `@plinth-tech-lead` may delegate them concurrently to the selected specialized coder
- **AND** integrates their results before final checks

#### Scenario: Stop on artifact conflict

- **WHEN** authoritative issue, ADR, specification, or plan content conflicts materially
- **THEN** implementation stops
- **AND** the conflict is routed to `plinth-business-analyst` for manual assessment, since the `/review-alignment` command is retired

#### Scenario: Complete implementation workflow

- **WHEN** delegated tasks pass their acceptance criteria and focused checks
- **THEN** `/implement-spec` reports changed files, test and build evidence, task status, blockers, and risks

#### Scenario: Select branch or worktree execution

- **WHEN** `/implement-spec` reviews the approved plan or OpenSpec task list
- **THEN** it decides whether the work should run on a feature branch or in one or more linked worktrees
- **AND** it uses `/create-feature-branch` for serial current-checkout work
- **AND** it uses `/create-worktree` when independent groups can run safely in parallel

## Source and Derivation

- Source artifact: GitHub issue [#1094](https://github.com/jabrena/plinth/issues/1094).
- Derivation direction: issue #1094 -> `rename-agents-robot-to-plinth` requirements -> renamed agent references in the implementation command contract.
