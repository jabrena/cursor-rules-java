Feature: Validate plinth-java-performance agent

Background:
  Given the agent prompt file ".cursor/agents/plinth-java-performance.md"
  And the folder "examples" has no git changes

@acceptance-test
Scenario: Coordinate profiling and benchmarking with reproducible evidence
  Given the user request is "Profile a Java service and recommend evidence-backed performance work"
  And the agent prompt source ".cursor/agents/plinth-java-performance.md" is read before execution
  When the agent "plinth-java-performance" is applied to the request
  Then the agent establishes a reproducible baseline with runtime, environment, workload, command, and artifact metadata
  And the agent separates profiling, benchmark design, analysis, optimization delegation, and verification outcomes
  And the agent uses profiling and performance skills for detection, analysis, refactoring guidance, verification, JMeter, or Gatling when relevant
  And the agent delegates approved code optimizations to the appropriate Java or framework coder agent
  And the agent does not directly implement application-code optimizations
  And the agent reports evidence, artifacts, bottlenecks, thresholds, risks, and validation results
  And any git changes produced during agent execution and verification are reset
