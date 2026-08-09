Feature: Scenario 3 — Case 3 OpenSpec technical requirements
  As a Plinth maintainer running the project benchmark harness
  I want Case 3 runs to use OpenSpec technical requirements from scenario3 only as the implementation input
  and persist a metrics result JSON under results/
  So that we can compare OpenSpec-driven implementation against Scenario 1 and Scenario 2 with measurable cost and quality

  # Agent protocol (Case 3):
  # - Derive ALL implementation requirements ONLY from benchmarks/problem2/scenario3/specs/technical-requirements/openspec/.
  # - Do NOT read benchmarks/problem2/scenario3/specs/functional-requirements/ directly as agent input
  #   (those files exist only so OpenSpec derivation links resolve within the harness).
  # - Do NOT use examples/openspec/ or other scenarios as input authority.
  # - Under benchmarks/problem2/scenario3/results/, read ONLY README.md and example.result.json (operator/metrics template).
  # - Do NOT read prior run JSON files under benchmarks/problem2/scenario3/results/.
  # - Skill discovery is allowed: skills under .agents/skills/ or skills/ may be read (including the
  #   bundled openspec-propose skill under benchmarks/problem2/scenario3/.agents/skills/); the model decides
  #   which skills are relevant and the result JSON records only skills actually read or invoked.

  Background:
    Given a Case 3 benchmark run for scenario "scenario3"
    And the run case id is "case-3-current-openspec-problem2"
    And results are stored under "benchmarks/problem2/scenario3/results/"
    And the Case 3 allowlist is the only authorized reading set for requirements and product behavior:
      | path |
      | benchmarks/problem2/scenario3/specs/technical-requirements/openspec/ |
      | benchmarks/problem2/scenario3/gherkin/scenario3.feature |
      | benchmarks/problem2/scenario3/README.md |
      | benchmarks/problem2/metrics-v1.schema.json |
      | benchmarks/problem2/metrics-v1.example.json |
    And the Case 3 results allowlist is the only authorized reading set under "benchmarks/problem2/scenario3/results/":
      | path |
      | benchmarks/problem2/scenario3/results/README.md |
      | benchmarks/problem2/scenario3/results/example.result.json |
    And the Case 3 Plinth tooling allowlist is the authorized reading set for skills:
      | path |
      | benchmarks/problem2/scenario3/.agents/skills/ |
      | .agents/skills/ |
      | skills/ |
    And the Case 3 skill discovery allowlist permits any skill under the Plinth tooling skill roots:
      | path |
      | benchmarks/problem2/scenario3/.agents/skills/ |
      | .agents/skills/ |
      | skills/ |

  @acceptance-test
  Scenario: Case 3 run records minimal v1 metrics as JSON
    Given "benchmarks/problem2/scenario3/specs/technical-requirements/openspec/" is the sole implementation specification
    And only files on the Case 3 allowlist are read for requirements, design, or acceptance criteria
    And only files on the Case 3 results allowlist are read under "benchmarks/problem2/scenario3/results/"
    And only files on the Case 3 Plinth tooling allowlist are read for skills
    And skills under "benchmarks/problem2/scenario3/.agents/skills/", ".agents/skills/", or "skills/" may be read and invoked for implementation guidance only
    And skills read or invoked from the Case 3 skill discovery allowlist are recorded in "plinth_usage.skills"
    And no file under "benchmarks/problem2/scenario3/results/" outside the Case 3 results allowlist may be read during the run
    And the agent must not read, open, grep, or search under any path outside "benchmarks/problem2/scenario3/" except harness metrics files on the Case 3 allowlist under "benchmarks/problem2/" and Plinth tooling on the Case 3 Plinth tooling allowlist
    And "benchmarks/problem2/scenario3/specs/functional-requirements/" must not be read directly for requirements or technology choices
    And "benchmarks/problem2/scenario1/" must not be read or used as scenario input
    And "benchmarks/problem2/scenario2/" must not be read or used as scenario input
    And "benchmarks/problem2/scenario4/" must not be read or used as scenario input
    And "benchmarks/problem2/scenario5/" must not be read or used as scenario input
    And "benchmarks/problem1/" must not be read or used as scenario input
    And "examples/openspec/" must not be read or used as scenario input
    And no "ADR-*" file outside "benchmarks/problem2/scenario3/" may be read for requirements or technology choices
    And no "openspec/changes/" design, tasks, or spec files outside "benchmarks/problem2/scenario3/" may be read for requirements
    When the agent implements the product behavior documented in "benchmarks/problem2/scenario3/specs/technical-requirements/openspec/" in "benchmarks/problem2/scenario3/demo/"
    And the run completes
    Then the happy path in "benchmarks/problem2/scenario3/specs/technical-requirements/openspec/changes/add-greek-gods-api/specs/greek-gods-api/spec.md" passes
    And a result JSON file exists under "benchmarks/problem2/scenario3/results/"
    And the result JSON conforms to "benchmarks/problem2/metrics-v1.schema.json"
    And the result JSON includes populated group "efficiency" with fields "wall_clock_s", "active_agent_s", "tokens_in", "tokens_out", "tokens_total", and "cost_usd"
    And the result JSON includes populated group "outcome_quality" with fields "acceptance_pass", "acceptance_coverage", "rework_turns", and "artifact_completeness"
    And the result JSON includes populated group "protocol_labels" with fields "scenario", "case_id", "tool", "model", "plinth_config", "commit", "retry_count", and "human_intervention_min"
    And the result JSON includes populated group "plinth_usage" with fields "skills_count", "commands_count", "agents_count", "skills", "commands", and "agents"
    And the result JSON includes populated group "solution_snapshot" with fields "demo_root", "tree_format", "tree_encoding", "tree_b64", and "file_count"
    And the result JSON field "protocol_labels.scenario" equals "scenario3"
    And the result JSON field "protocol_labels.case_id" equals "case-3-current-openspec-problem2"
    And the result JSON field "solution_snapshot.demo_root" equals "benchmarks/problem2/scenario3/demo/"
    And the result JSON field "outcome_quality.acceptance_pass" is true only when the OpenSpec happy path and this scenario pass
    And the length of "plinth_usage.agents" equals the value of "plinth_usage.agents_count"
    And the length of "plinth_usage.commands" equals the value of "plinth_usage.commands_count"
    And the length of "plinth_usage.skills" equals the value of "plinth_usage.skills_count"
    And every entry in "plinth_usage.skills" is a skill read or invoked during the run
    And "benchmarks/problem2/scenario3/demo/" is restored to empty with only ".gitkeep"
