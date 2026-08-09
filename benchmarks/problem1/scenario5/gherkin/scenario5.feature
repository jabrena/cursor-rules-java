Feature: Scenario 5 — Case 5 direct OpenSpec implementation
  As a Plinth maintainer running the project benchmark harness
  I want Case 5 runs to use OpenSpec technical requirements from scenario5 only as the implementation input
  and persist a metrics result JSON under results/
  So that we can compare direct OpenSpec implementation with Scenario 4 command orchestration using measurable cost and quality

  # Agent protocol (Case 5):
  # - The benchmark harness asks the agent to implement the OpenSpec change directly in
  #   benchmarks/problem1/scenario5/demo/ without reading or invoking /implement-spec.
  # - Agents under .cursor/agents/ and skills under .agents/skills/ or skills/ may be used;
  #   the model decides which are relevant and the result JSON records only agents and
  #   skills actually invoked or read during the run.
  # - Derive ALL implementation requirements ONLY from that OpenSpec change (tasks.md, design.md, spec.md).
  # - Capture the OpenSpec change state before execution and restore it after metrics are written;
  #   the implementation may update tasks.md, but benchmark inputs must remain reusable.
  # - Do NOT read benchmarks/problem1/scenario5/specs/functional-requirements/problem1/ directly as agent input
  #   (those files exist only so OpenSpec derivation links resolve within the harness).
  # - Under benchmarks/problem1/scenario5/results/, read ONLY README.md and example.result.json (operator/metrics template).
  # - Do NOT read prior run JSON files under benchmarks/problem1/scenario5/results/.
  # - Do NOT use examples/openspec/god-analysis-api/ or other scenarios as input authority.

  Background:
    Given a Case 5 benchmark run for scenario "scenario5"
    And the run case id is "case-5-direct-openspec-problem1"
    And results are stored under "benchmarks/problem1/scenario5/results/"
    And the Case 5 allowlist is the only authorized reading set for requirements and product behavior:
      | path |
      | benchmarks/problem1/scenario5/specs/technical-requirements/openspec/ |
      | benchmarks/problem1/scenario5/gherkin/scenario5.feature |
      | benchmarks/problem1/metrics-v1.schema.json |
      | benchmarks/problem1/metrics-v1.example.json |
    And the Case 5 results allowlist is the only authorized reading set under "benchmarks/problem1/scenario5/results/":
      | path |
      | benchmarks/problem1/scenario5/results/README.md |
      | benchmarks/problem1/scenario5/results/example.result.json |
    And the Case 5 Plinth tooling allowlist is the authorized reading set for agents and skills:
      | path |
      | .cursor/agents/ |
      | .agents/skills/ |
      | skills/ |
    And the Case 5 skill discovery allowlist permits any skill under the Plinth tooling skill roots:
      | path |
      | .agents/skills/ |
      | skills/ |

  @acceptance-test
  Scenario: Case 5 run records minimal v1 metrics as JSON
    Given the OpenSpec change path "benchmarks/problem1/scenario5/specs/technical-requirements/openspec/changes/add-god-analysis-api"
    And the OpenSpec change "add-god-analysis-api" contains "proposal.md", "design.md", "tasks.md", and "specs/god-analysis-api/spec.md"
    And the command prompt source ".cursor/commands/implement-spec.md" must not be read or invoked
    And Plinth agents under ".cursor/agents/" and skills under ".agents/skills/" or "skills/" may be read and invoked for implementation guidance only
    And only files on the Case 5 allowlist are read for requirements, design, or acceptance criteria
    And only files on the Case 5 results allowlist are read under "benchmarks/problem1/scenario5/results/"
    And only files on the Case 5 Plinth tooling allowlist are read for agents and skills
    And skills read or invoked from the Case 5 skill discovery allowlist are recorded in "plinth_usage.skills"
    And no file under "benchmarks/problem1/scenario5/results/" outside the Case 5 results allowlist may be read during the run
    And the agent must not read, open, grep, or search under any path outside "benchmarks/problem1/scenario5/" except harness metrics files on the Case 5 allowlist under "benchmarks/problem1/" and Plinth tooling on the Case 5 Plinth tooling allowlist
    And "benchmarks/problem1/scenario5/specs/functional-requirements/problem1/" must not be read directly for requirements or technology choices
    And "benchmarks/problem1/scenario1/" must not be read or used as scenario input
    And "benchmarks/problem1/scenario2/" must not be read or used as scenario input
    And "benchmarks/problem1/scenario3/" must not be read or used as scenario input
    And "benchmarks/problem1/scenario4/" must not be read or used as scenario input
    And "examples/openspec/god-analysis-api/" must not be read or used as scenario input
    And no "ADR-*" file outside "benchmarks/problem1/scenario5/" may be read for requirements or technology choices
    And no "openspec/changes/" design, tasks, or spec files outside "benchmarks/problem1/scenario5/" may be read for requirements
    When the benchmark harness asks the agent to implement the OpenSpec change directly in "benchmarks/problem1/scenario5/demo/"
    And the run completes
    Then the happy path in "benchmarks/problem1/scenario5/specs/technical-requirements/openspec/changes/add-god-analysis-api/specs/god-analysis-api/spec.md" passes
    And a result JSON file exists under "benchmarks/problem1/scenario5/results/"
    And the result JSON conforms to "benchmarks/problem1/metrics-v1.schema.json"
    And the result JSON includes populated group "efficiency" with fields "wall_clock_s", "active_agent_s", "tokens_in", "tokens_out", "tokens_total", and "cost_usd"
    And the result JSON includes populated group "outcome_quality" with fields "acceptance_pass", "acceptance_coverage", "rework_turns", and "artifact_completeness"
    And the result JSON includes populated group "protocol_labels" with fields "scenario", "case_id", "tool", "model", "plinth_config", "commit", "retry_count", and "human_intervention_min"
    And the result JSON includes populated group "plinth_usage" with fields "skills_count", "commands_count", "agents_count", "skills", "commands", and "agents"
    And the result JSON includes populated group "solution_snapshot" with fields "demo_root", "tree_format", "tree_encoding", "tree_b64", and "file_count"
    And the result JSON field "protocol_labels.scenario" equals "scenario5"
    And the result JSON field "protocol_labels.case_id" equals "case-5-direct-openspec-problem1"
    And the result JSON field "solution_snapshot.demo_root" equals "benchmarks/problem1/scenario5/demo/"
    And the result JSON field "outcome_quality.acceptance_pass" is true only when the OpenSpec happy path and this scenario pass
    And the length of "plinth_usage.agents" equals the value of "plinth_usage.agents_count"
    And the length of "plinth_usage.commands" equals the value of "plinth_usage.commands_count"
    And the length of "plinth_usage.skills" equals the value of "plinth_usage.skills_count"
    And every entry in "plinth_usage.skills" is a skill read or invoked during the run
    And "plinth_usage.commands_count" equals 0
    And "plinth_usage.commands" is empty
    And "benchmarks/problem1/scenario5/demo/" is restored to empty with only ".gitkeep"
    And the OpenSpec change path "benchmarks/problem1/scenario5/specs/technical-requirements/openspec/changes/add-god-analysis-api" is restored to its pre-run contents, including "tasks.md"
