Feature: Validate plinth-tech-lead agent

Background:
  Given the agent prompt file ".cursor/agents/plinth-tech-lead.md"
  And the OpenSpec project path "examples/openspec/god-analysis-api"
  And the OpenSpec change path "examples/openspec/god-analysis-api/openspec/changes/add-god-analysis-api"
  And the implementation target directory "examples/openspec/god-analysis-api/demo"
  And the folder "examples/openspec/god-analysis-api/openspec" has no git changes
  And the folder "examples/openspec/god-analysis-api/demo" has no git changes

Scenario: Reject incomplete selected-scope OpenSpec evidence before delegation
  Given the selected OpenSpec scope has absent, ambiguous, placeholder, completed-only, partial, or divergent scenario-to-task evidence
  When the agent evaluates delivery readiness
  Then the agent reports every unsupported scenario or task and its owning OpenSpec artifact
  And the agent instructs the contributor to update the change and rerun delivery
  And the agent does not set up a location, discover skills, or invoke an implementation agent

Scenario: Accept complete selected-scope OpenSpec evidence
  Given every selected behavior-changing task maps to at least one concrete scenario
  And every applicable concrete scenario maps to an actionable implementation or verification task
  When the agent evaluates delivery readiness
  Then the agent continues to workspace and implementation-location checks
  And unrelated future task groups remain outside the current readiness decision

Scenario: Resolve or ask for the implementation location before delegation
  Given the OpenSpec readiness gate has passed
  When invocation constraints provide an implementation location
  Then the agent uses the invocation location instead of the design location
  When invocation constraints omit the location and design.md has a valid Implementation Location section
  Then the agent uses the design strategy and optional reference
  When both sources omit a valid location or the design strategy is invalid
  Then the agent asks the contributor to choose main, a feature branch, or a worktree
  And the agent waits before location setup, skill discovery, or implementation delegation

Scenario: Preserve default-branch approval after the contributor selects main
  Given the confirmed implementation location is main or the repository default branch
  When the agent prepares delivery
  Then the agent warns about direct implementation on the default branch
  And the agent requires separate explicit approval before invoking an implementation agent

@acceptance-test
Scenario: Coordinate God Analysis API implementation from a validated OpenSpec change
  Remark: Acceptance execution must coordinate delivery through coder delegation and must not implement outside the requested demo directory.
  Given the OpenSpec change "add-god-analysis-api" contains "proposal.md", "design.md", "tasks.md", and "specs/god-analysis-api/spec.md"
  And the OpenSpec change is validated with "openspec validate --all" from "examples/openspec/god-analysis-api"
  And the user request is "Coordinate implementation for examples/openspec/god-analysis-api/openspec/changes/add-god-analysis-api and implement in examples/openspec/god-analysis-api/demo"
  And the agent prompt source ".cursor/agents/plinth-tech-lead.md" is read before execution
  And the implementation target directory starts empty except for ".gitkeep"
  When the agent "plinth-tech-lead" is applied to the request
  Then the agent loads the selected OpenSpec "tasks.md" as the execution contract
  And the agent reads the proposal, design, and affected specs as bounded read-only skill-discovery context
  And the agent reviews the available skill catalog without recursively reading every skill body
  And the agent records concrete OpenSpec evidence for every dynamically discovered candidate skill
  And the agent confirms the selected OpenSpec change is current, validated, and internally consistent
  And the agent identifies the implementation as a Spring Boot MVC Java service from the OpenSpec design and technology constraints
  And the agent reads the complete 042-planning-openspec SKILL.md and its required reference before delegating
  And the agent delegates every implementation, test, and verification step to "@plinth-java-spring-boot-coder"
  And every implementation handoff requires the complete SKILL.md and every task-relevant referenced resource to be read before editing
  And the Spring Boot coder treats delegated candidates as a baseline and owns final framework-specific skill discovery
  And the agent does not implement code, edit tests, or run the build as a substitute for developers
  And the agent does not create or refine OpenSpec proposal, design, or specification artifacts as a primary mission
  And all generated implementation files are created under "examples/openspec/god-analysis-api/demo"
  And the implementation provides "GET /api/v1/gods/stats/sum"
  And the implementation covers the documented happy path sum "78179288397447443426"
  And the implementation covers the documented partial timeout sum "78101109179220212216"
  And the implementation rejects missing, empty, multi-character, and invalid query parameters with HTTP 400
  And the implementation does not add WebFlux, WebClient, Rest Assured, Resilience4j Retry, Spring Retry, or custom retry loops for US-001
  And the agent runs the focused Maven verification command from "examples/openspec/god-analysis-api/demo"
  And the agent marks OpenSpec tasks complete only after their acceptance criteria and verification gates pass
  And the agent reports delegation groups, task status, skills applied, skills skipped, exact reference paths read, validation evidence, risks, blockers, and artifact authority
  And any git changes produced under "examples/openspec/god-analysis-api/demo" during agent execution and verification are reset
  And any git changes produced under "examples/openspec/god-analysis-api/openspec" during agent execution and verification are reset
