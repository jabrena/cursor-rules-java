Feature: Scenario 2 — Case 2 full functional requirements package
  As a Plinth maintainer running the project benchmark harness
  I want Case 2 runs to use the harness-local full functional-requirements/problem1 package from scenario2 only,
  exclude technical OpenSpec, and persist a metrics result JSON under results/
  So that we can compare richer functional packaging against Scenario 1 with measurable cost and quality

  # Agent protocol (Case 2):
  # - Derive ALL product requirements ONLY from benchmarks/scenario2/specs/functional-requirements/problem1/.
  # - Do NOT search the repository for God Analysis API artifacts outside benchmarks/scenario2/.
  # - Do NOT use technical-requirements/openspec/ or examples/openspec/ as input.
  # - Other scenarios are out of scope even if they describe the same product.
  # - Under benchmarks/scenario2/results/, read ONLY README.md and example.result.json (operator/metrics template).
  # - Do NOT read prior run JSON files under benchmarks/scenario2/results/.
  # - Skill discovery is allowed: skills under .agents/skills/ or skills/ may be read; the model decides
  #   which skills are relevant and the result JSON records only skills actually read or invoked.

  Background:
    Given a Case 2 benchmark run for scenario "scenario2"
    And the run case id is "case-2-all-problem1-requirements"
    And results are stored under "benchmarks/scenario2/results/"
    And the Case 2 allowlist is the only authorized reading set for requirements and product behavior:
      | path |
      | benchmarks/scenario2/specs/functional-requirements/problem1/README.md |
      | benchmarks/scenario2/specs/functional-requirements/problem1/US-001_God_Analysis_API.md |
      | benchmarks/scenario2/specs/functional-requirements/problem1/US-001_god_analysis_api.feature |
      | benchmarks/scenario2/specs/functional-requirements/problem1/US-001-god-analysis-api.openapi.yaml |
      | benchmarks/scenario2/specs/functional-requirements/problem1/my-json-server-oas.yaml |
      | benchmarks/scenario2/specs/functional-requirements/problem1/ADR-001-God-Analysis-API-Functional-Requirements.md |
      | benchmarks/scenario2/specs/functional-requirements/problem1/ADR-002-God-Analysis-API-Non-Functional-Requirements.md |
      | benchmarks/scenario2/specs/functional-requirements/problem1/ADR-003-God-Analysis-API-Technology-Stack.md |
      | benchmarks/scenario2/gherkin/scenario2.feature |
      | benchmarks/scenario2/README.md |
      | benchmarks/metrics-v1.schema.json |
      | benchmarks/metrics-v1.example.json |
    And the Case 2 results allowlist is the only authorized reading set under "benchmarks/scenario2/results/":
      | path |
      | benchmarks/scenario2/results/README.md |
      | benchmarks/scenario2/results/example.result.json |
    And the Case 2 Plinth tooling allowlist is the authorized reading set for skills:
      | path |
      | .agents/skills/ |
      | skills/ |
    And the Case 2 skill discovery allowlist permits any skill under the Plinth tooling skill roots:
      | path |
      | .agents/skills/ |
      | skills/ |

  @acceptance-test
  Scenario: Case 2 run records minimal v1 metrics as JSON
    Given "benchmarks/scenario2/specs/functional-requirements/problem1/" is the sole product specification
    And only files on the Case 2 allowlist are read for requirements, design, or acceptance criteria
    And only files on the Case 2 results allowlist are read under "benchmarks/scenario2/results/"
    And only files on the Case 2 Plinth tooling allowlist are read for skills
    And skills under ".agents/skills/" or "skills/" may be read and invoked for implementation guidance only
    And skills read or invoked from the Case 2 skill discovery allowlist are recorded in "plinth_usage.skills"
    And no file under "benchmarks/scenario2/results/" outside the Case 2 results allowlist may be read during the run
    And the agent must not read, open, grep, or search under any path outside "benchmarks/scenario2/" except harness metrics files on the Case 2 allowlist under "benchmarks/" and Plinth tooling on the Case 2 Plinth tooling allowlist
    And "benchmarks/scenario2/specs/technical-requirements/" is not provided
    And "benchmarks/scenario1/" must not be read or used as scenario input
    And "benchmarks/scenario3/" must not be read or used as scenario input
    And "benchmarks/scenario4/" must not be read or used as scenario input
    And "examples/openspec/god-analysis-api/" must not be read or used as scenario input
    And no "ADR-*" file outside "benchmarks/scenario2/" may be read for requirements or technology choices
    And no "openspec/changes/" design, tasks, or spec files outside "benchmarks/scenario2/" may be read for requirements
    When the agent implements the product behavior documented in "benchmarks/scenario2/specs/functional-requirements/problem1/" in "benchmarks/scenario2/demo/"
    And the run completes
    Then the @acceptance-test scenario in "benchmarks/scenario2/specs/functional-requirements/problem1/US-001_god_analysis_api.feature" passes
    And a result JSON file exists under "benchmarks/scenario2/results/"
    And the result JSON conforms to "benchmarks/metrics-v1.schema.json"
    And the result JSON includes populated group "efficiency" with fields "wall_clock_s", "active_agent_s", "tokens_in", "tokens_out", "tokens_total", and "cost_usd"
    And the result JSON includes populated group "outcome_quality" with fields "acceptance_pass", "acceptance_coverage", "rework_turns", and "artifact_completeness"
    And the result JSON includes populated group "protocol_labels" with fields "scenario", "case_id", "tool", "model", "plinth_config", "commit", "retry_count", and "human_intervention_min"
    And the result JSON includes populated group "plinth_usage" with fields "skills_count", "commands_count", "agents_count", "skills", "commands", and "agents"
    And the result JSON includes populated group "solution_snapshot" with fields "demo_root", "tree_format", "tree_encoding", "tree_b64", and "file_count"
    And the result JSON field "protocol_labels.scenario" equals "scenario2"
    And the result JSON field "protocol_labels.case_id" equals "case-2-all-problem1-requirements"
    And the result JSON field "solution_snapshot.demo_root" equals "benchmarks/scenario2/demo/"
    And the result JSON field "outcome_quality.acceptance_pass" is true only when the product @acceptance-test scenario and this scenario pass
    And the length of "plinth_usage.agents" equals the value of "plinth_usage.agents_count"
    And the length of "plinth_usage.commands" equals the value of "plinth_usage.commands_count"
    And the length of "plinth_usage.skills" equals the value of "plinth_usage.skills_count"
    And every entry in "plinth_usage.skills" is a skill read or invoked during the run
    And "benchmarks/scenario2/demo/" is restored to empty with only ".gitkeep"
