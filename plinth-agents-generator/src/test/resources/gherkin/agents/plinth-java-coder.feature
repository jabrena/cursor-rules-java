Feature: Validate plinth-java-coder agent

Background:
  Given the agent prompt file ".cursor/agents/plinth-java-coder.md"
  And the folder "examples" has no git changes

@acceptance-test
Scenario: Implement plain Java work with appropriate skill routing
  Given the user request is "Implement a Java change using Maven project conventions"
  And the agent prompt source ".cursor/agents/plinth-java-coder.md" is read before execution
  When the agent "plinth-java-coder" is applied to the request
  Then the agent understands the delegated implementation requirement before making changes
  And the agent reads relevant Java skills before implementation or refactoring
  And for every applied skill the agent reads the complete SKILL.md and every task-relevant referenced resource before editing
  And the agent applies the documented error-model, design-order, persistence, API contract, and container skill selection rules when relevant
  And the agent follows import management and repository coding conventions
  And the agent runs focused validation such as "./mvnw validate" or reports validation failure as blocking
  And the agent reports skills applied, skills skipped, exact reference paths read, files changed, validation evidence, blockers, and residual risks
  And any git changes produced during agent execution and verification are reset
