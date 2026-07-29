Feature: Validate plinth-no-java agent

Background:
  Given the agent prompt file ".cursor/agents/plinth-no-java.md"
  And the folder "examples" has no git changes

@acceptance-test
Scenario: Implement non-Java work without claiming Java expertise
  Given the user request is "Implement a non-Java repository task using existing project tooling"
  And the agent prompt source ".cursor/agents/plinth-no-java.md" is read before execution
  When the agent "plinth-no-java" is applied to the request
  Then the agent identifies the target stack from repository evidence before making changes
  And for every applied skill the agent reads the complete SKILL.md and every task-relevant referenced resource before editing
  And the agent follows existing project scripts, tests, linters, formatters, dependency files, and conventions
  And the agent runs the most focused available validation command for the changed area
  And the agent requests rerouting when the task requires Java, Maven, Spring Boot, Quarkus, or Micronaut behavior
  And the agent does not invent a toolchain or add dependencies without repository evidence or explicit scope
  And the agent reports detected stack, skills applied, skills skipped, exact reference paths read, files changed, validation evidence, blockers, and residual risks
  And any git changes produced during agent execution and verification are reset
