Feature: Validate plinth-architect agent

Background:
  Given the agent prompt file ".cursor/agents/plinth-architect.md"
  And the OpenSpec project path "examples/openspec/god-analysis-api"
  And the folder "examples/openspec/god-analysis-api/requirements/problem1" has no git changes
  And the folder "examples/openspec/god-analysis-api/openspec" has no git changes

@acceptance-test
Scenario: Create an initial OpenSpec change using composable planning workflow
  Remark: Acceptance execution verifies the OpenSpec authoring step with `042-planning-openspec` only.
  Given the user request is "/create-spec examples/openspec/god-analysis-api/requirements/problem1"
  And the functional requirements folder "examples/openspec/god-analysis-api/requirements/problem1" contains ADRs, a user story, Gherkin acceptance criteria, and OpenAPI files
  And the agent prompt source ".cursor/agents/plinth-architect.md" is read before execution
  And the requested OpenSpec change path is "examples/openspec/god-analysis-api/openspec/changes/add-god-analysis-api"
  And any existing OpenSpec change at the requested change path is removed before execution
  And application code implementation is explicitly out of scope
  When the agent "plinth-architect" is applied to the request
  Then the agent reads trusted planning inputs and establishes source artifact authority
  And the agent assesses whether the scope fits one reviewable OpenSpec change
  And the agent records source artifact paths, concern authority, and derivation direction in the OpenSpec proposal
  And the agent creates the OpenSpec change "add-god-analysis-api" containing "proposal.md", "design.md", "tasks.md", and capability specification deltas under "specs/"
  And the agent applies only "042-planning-openspec" when creating the OpenSpec change
  And the agent does not call design skills "051-design-two-steps-methods", "052-design-hamburger-method", "053-design-simple-rules", "054-design-tdd", "055-design-parallel-change", "056-design-avoid-breaking-changes", "057-design-feature-toggles", "121-java-object-oriented-design", "122-java-type-design", "123-java-design-patterns", or "130-java-testing-strategies"
  And the agent does not call "034-architecture-design-exploration"
  And the agent creates or updates OpenSpec artifacts only under "examples/openspec/god-analysis-api/openspec" when edits are requested
  And the agent does not modify ADRs, user stories, Gherkin files, or OpenAPI files under "examples/openspec/god-analysis-api/requirements/problem1"
  And the folder "examples/openspec/god-analysis-api/requirements/problem1" has no git changes
  And "openspec validate --all" is run from "examples/openspec/god-analysis-api" after approved artifact changes
  And the agent reports source artifacts, authority, derivation direction, validation evidence, unresolved questions, and handoff details for plinth-tech-lead
  And any git changes produced under "examples/openspec/god-analysis-api/openspec" during agent execution and verification are reset

@acceptance-test
Scenario: Improve an initial OpenSpec change by calling design skills during elaboration
  Remark: Acceptance execution first creates the initial OpenSpec change through `/create-spec`, then improves it through `/explore-design`.
  Given the functional requirements folder "examples/openspec/god-analysis-api/requirements/problem1" contains ADRs, a user story, Gherkin acceptance criteria, and OpenAPI files
  And the agent prompt source ".cursor/agents/plinth-architect.md" is read before execution
  And the requested OpenSpec change path is "examples/openspec/god-analysis-api/openspec/changes/add-god-analysis-api"
  And any existing OpenSpec change at the requested change path is removed before execution
  And application code implementation is explicitly out of scope
  When the agent "plinth-architect" is applied to the request "/create-spec examples/openspec/god-analysis-api/requirements/problem1"
  Then the agent creates the OpenSpec change "add-god-analysis-api" containing "proposal.md", "design.md", "tasks.md", and capability specification deltas under "specs/"
  And the agent applies only "042-planning-openspec" when creating the initial OpenSpec change
  And "openspec validate --all" is run from "examples/openspec/god-analysis-api" after the initial change is created
  When the agent "plinth-architect" is applied to the request "/explore-design examples/openspec/god-analysis-api/openspec/changes/add-god-analysis-api"
  Then the agent identifies the design source, problem, constraints, stakeholders, options, trade-offs, and recommendation criteria
  And the agent calls "051-design-two-steps-methods" to separate behavior-preserving preparation from behavior-changing refinement in the improved approach
  And the agent calls "052-design-hamburger-method" when scope needs smallest-useful vertical slices during elaboration
  And the agent calls "053-design-simple-rules" to evaluate design alternatives with ordered tradeoff rules before finalizing the direction
  And the agent calls "054-design-tdd" to shape test-first sequencing and verification boundaries for the refined approach
  And the agent calls "055-design-parallel-change" when database migration requirements need expand-migrate-contract sequencing
  And the agent calls "056-design-avoid-breaking-changes" to review compatibility surfaces affected by the refined approach
  And the agent calls "057-design-feature-toggles" when rollout, rollback, or temporary behavior controls affect the design direction
  And the agent calls "121-java-object-oriented-design" before "122-java-type-design" and "123-java-design-patterns" when Java responsibilities or collaboration affect the refined approach
  And the agent calls "122-java-type-design" when domain types, invariants, or invalid-state modeling affect the refined approach
  And the agent calls "123-java-design-patterns" only when a demonstrated collaboration or integration problem requires pattern selection
  And the agent calls "130-java-testing-strategies" when testing strategy, boundary coverage, or verification quality affect the refined approach
  And the agent does not call "042-planning-openspec" or "034-architecture-design-exploration" during design refinement
  And the agent records assumptions, rejected options, risks, ADR candidates, and validation needed before implementation
  And the agent reports the recommended design direction and any generated analysis artifact
  And the agent refines the existing OpenSpec change without replacing initial OpenSpec authoring owned by "/create-spec"
  And the agent does not modify ADRs, user stories, Gherkin files, or OpenAPI files under "examples/openspec/god-analysis-api/requirements/problem1"
  And the folder "examples/openspec/god-analysis-api/requirements/problem1" has no git changes
  And "openspec validate --all" is run from "examples/openspec/god-analysis-api" after approved refinement changes
  And any git changes produced under "examples/openspec/god-analysis-api/openspec" during agent execution and verification are reset
