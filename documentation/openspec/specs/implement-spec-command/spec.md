# implement-spec-command Specification

## Purpose
Define the controlled implementation command for approved plans and validated OpenSpec task lists.
## Requirements
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

### Requirement: Coder skill selection

All implementation coder agents MUST use explicit skill-selection precedence for overlapping implementation concerns.

#### Scenario: Select an error model

- **WHEN** a coder handles an expected domain outcome or composable failure
- **THEN** it prefers `@143-java-functional-exception-handling`
- **AND** it reserves `@126-java-exception-handling` for unexpected, infrastructure, resource, interruption, timeout, and framework-boundary failures
- **AND** it does not model the same failure with both approaches

#### Scenario: Select design guidance

- **WHEN** implementation requires design guidance
- **THEN** the coder applies `@121-java-object-oriented-design` for responsibilities and boundaries
- **AND** applies `@122-java-type-design` for domain types and signatures
- **AND** applies `@123-java-design-patterns` only for a demonstrated collaboration or integration problem
- **AND** uses `@142-java-functional-programming` within those boundaries when composition improves clarity

#### Scenario: Select persistence and contract guidance

- **WHEN** implementation includes relational persistence
- **THEN** the framework coder prefers its JDBC skill together with `@704-technologies-sql`
- **AND** uses repository or ORM abstractions only when they provide a clear benefit
- **AND** applies `@124-java-secure-coding` for general security concerns
- **AND** applies `@701-technologies-openapi` when an API contract is in scope
- **AND** applies `@705-technologies-nosql-mongodb` when MongoDB modeling or queries are in scope

### Requirement: OpenSpec implementation readiness gate

Before any skill discovery, Git-location change, or implementation delegation, `/implement-spec` MUST require bidirectional traceability between concrete acceptance scenarios in `specs/**/spec.md` and the behavior-changing implementation or verification tasks in the selected execution scope from `tasks.md`.

#### Scenario: OpenSpec change contains concrete execution evidence

- **GIVEN** the selected OpenSpec change is structurally valid
- **AND** every selected behavior-changing task is supported by one or more concrete acceptance scenarios
- **AND** every scenario applicable to the selected scope has an actionable implementation or verification task
- **AND** each concrete scenario defines a trigger and observable outcome without unresolved placeholders
- **WHEN** the user invokes `/implement-spec` for that change
- **THEN** the readiness gate passes
- **AND** the command may continue to workspace and location checks

#### Scenario: OpenSpec change lacks concrete or complete acceptance evidence

- **GIVEN** the selected OpenSpec change has no scenario, an ambiguous or placeholder scenario, no actionable incomplete task, or partial bidirectional traceability for the selected scope
- **WHEN** the user invokes `/implement-spec` for that change
- **THEN** the command stops before skill discovery, Git-location changes, and implementation delegation
- **AND** identifies each unsupported or ambiguous scenario or task
- **AND** instructs the contributor to update the OpenSpec change and rerun `/implement-spec`
- **AND** does not invent acceptance criteria or tasks

#### Scenario: Selected task group limits readiness scope

- **GIVEN** the OpenSpec change contains multiple task groups
- **AND** the user selects one task or group for execution
- **WHEN** `/implement-spec` evaluates readiness
- **THEN** it requires complete traceability for the selected scope
- **AND** does not require unrelated future task groups to be implementation-complete
- **AND** still rejects a selected task that diverges from the change requirements or safeguards

### Requirement: Explicit implementation location resolution

For an OpenSpec input, `/implement-spec` MUST use an implementation location supplied by invocation constraints or a valid `## Implementation Location` section in the selected change's `design.md`, and MUST ask the contributor to choose `main`, a feature branch, or a worktree when neither source specifies a valid location.

#### Scenario: Invocation supplies implementation location

- **GIVEN** the OpenSpec readiness gate has passed
- **AND** the `/implement-spec` invocation explicitly supplies an implementation location
- **WHEN** the command resolves its location
- **THEN** it uses the invocation location subject to the existing workspace safety gates
- **AND** does not ask the contributor to choose another location

#### Scenario: OpenSpec design supplies implementation location

- **GIVEN** the OpenSpec readiness gate has passed
- **AND** the invocation does not supply an implementation location
- **AND** the selected OpenSpec `design.md` contains `## Implementation Location`
- **AND** the section defines `Strategy` as `main`, `feature-branch`, or `worktree`
- **WHEN** the command resolves its location
- **THEN** it uses the strategy and optional reference recorded in that section subject to the existing workspace safety gates
- **AND** does not ask the contributor to choose another location

#### Scenario: Implementation location is missing

- **GIVEN** the OpenSpec readiness gate has passed
- **AND** neither the invocation nor a valid `## Implementation Location` section supplies an implementation location
- **WHEN** the command resolves its location
- **THEN** it asks the contributor to choose `main`, a feature branch, or a worktree
- **AND** waits for the answer before creating or selecting a location
- **AND** does not delegate implementation before the location is confirmed

#### Scenario: OpenSpec design contains an invalid implementation location

- **GIVEN** the OpenSpec readiness gate has passed
- **AND** the invocation does not supply an implementation location
- **AND** `design.md` has a missing, blank, or unsupported `Strategy` value
- **WHEN** the command resolves its location
- **THEN** it treats the artifact location as unresolved
- **AND** asks the contributor to choose `main`, a feature branch, or a worktree
- **AND** does not silently guess a strategy

#### Scenario: Contributor selects main

- **GIVEN** the command asked for a missing implementation location
- **WHEN** the contributor selects `main` or the repository default branch
- **THEN** the command warns about direct implementation on the default branch
- **AND** requires separate explicit approval before implementation starts

#### Scenario: Contributor selects a feature branch or worktree

- **GIVEN** the command asked for a missing implementation location
- **WHEN** the contributor selects a feature branch or worktree
- **THEN** the command applies the existing feature-branch or worktree safety workflow
- **AND** reports the selected isolation strategy before delegation
