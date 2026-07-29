Feature: Validate implement-spec command with the God Analysis API OpenSpec example

Background:
  Given the command prompt file ".cursor/commands/implement-spec.md"
  And the OpenSpec project path "examples/openspec/god-analysis-api"
  And the OpenSpec change path "examples/openspec/god-analysis-api/openspec/changes/add-god-analysis-api"
  And the implementation target directory "examples/openspec/god-analysis-api/demo"
  And the implementation target directory starts empty except for ".gitkeep"

Scenario: Stop before side effects when selected-scope acceptance evidence is incomplete
  Given the selected OpenSpec scope has absent, ambiguous, placeholder, completed-only, partial, or divergent scenario-to-task evidence
  When the user executes the implement-spec command
  Then the command reports every unsupported scenario or task
  And the command instructs the contributor to update the OpenSpec change and rerun implement-spec
  And the command does not perform skill discovery, change a Git location, or delegate implementation

Scenario: Continue when selected-scope acceptance evidence is complete
  Given every selected behavior-changing task maps to at least one concrete scenario
  And every applicable concrete scenario maps to an actionable implementation or verification task
  When the user executes the implement-spec command
  Then the command continues to workspace and implementation-location checks
  And unrelated future task groups remain outside the current readiness decision

Scenario: Resolve implementation location by explicit precedence
  Given the OpenSpec readiness gate has passed
  When invocation constraints provide an implementation location
  Then the command uses the invocation location instead of the design location
  When invocation constraints omit the location and design.md has a valid Implementation Location section
  Then the command uses the design strategy and optional reference
  When both sources omit a valid location or the design strategy is invalid
  Then the command asks the contributor to choose main, a feature branch, or a worktree
  And the command waits before creating or selecting a location and before delegation

Scenario: Preserve default-branch approval after location resolution
  Given the OpenSpec readiness gate has passed
  And the confirmed implementation location is main or the repository default branch
  When the command prepares implementation
  Then the command warns about direct implementation on the default branch
  And the command requires separate explicit approval before implementation starts

@acceptance-test
Scenario: Implement God Analysis API from a validated OpenSpec change
  Remark: Acceptance execution must use the implement-spec command contract and must not implement outside the requested demo directory.
  Given the OpenSpec change "add-god-analysis-api" contains "proposal.md", "design.md", "tasks.md", and "specs/god-analysis-api/spec.md"
  And the OpenSpec change is validated with "openspec validate --all" from "examples/openspec/god-analysis-api"
  And the command prompt source ".cursor/commands/implement-spec.md" is read before execution
  When the user executes the prompt "/implement-spec examples/openspec/god-analysis-api/openspec/changes/add-god-analysis-api implement in examples/openspec/god-analysis-api/demo"
  Then the command loads the selected OpenSpec "tasks.md" as the execution contract
  And the command confirms the selected OpenSpec change is current, validated, and internally consistent
  And the command identifies the implementation as a Spring Boot MVC Java service from the OpenSpec design and technology constraints
  And the command routes implementation work through "@plinth-tech-lead" and the appropriate Java Spring Boot implementation agent
  And the command publishes a Skill discovery brief before the first implementation handoff
  And the command includes candidate skills to read in the implementation agent handoff
  And the command reports using the current branch as the isolation strategy before implementation starts
  And all generated implementation files are created under "examples/openspec/god-analysis-api/demo"
  And the implementation provides "GET /api/v1/gods/stats/sum"
  And the implementation covers the documented happy path sum "78179288397447443426"
  And the implementation covers the documented partial timeout sum "78101109179220212216"
  And the implementation rejects missing, empty, multi-character, and invalid query parameters with HTTP 400
  And the implementation does not add WebFlux, WebClient, Rest Assured, Resilience4j Retry, Spring Retry, or custom retry loops for US-001
  And the command runs the focused Maven verification command from "examples/openspec/god-analysis-api/demo"
  And the command marks OpenSpec tasks complete only after their acceptance criteria and verification gates pass
  And the command reports changed files, validation evidence, updated OpenSpec task status, risks, and blockers
  And any git changes produced under "examples/openspec/god-analysis-api/demo" during command execution and verification are reset
