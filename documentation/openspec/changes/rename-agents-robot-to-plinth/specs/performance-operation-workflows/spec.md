## MODIFIED Requirements

### Requirement: Profile lifecycle coordination

`/profile` MUST route profiling work to `@plinth-java-performance` and use skills `161`-`164` to coordinate baseline detection, evidence collection, analysis, approved optimization delegation, equivalent remeasurement, and result verification.

#### Scenario: Profile and verify a Java performance improvement

- **GIVEN** a project user has a Java application and a representative workload
- **WHEN** the user invokes `/profile`
- **THEN** `@plinth-java-performance` records the runtime, environment, workload, and baseline
- **AND** it uses the `161`-`164` lifecycle to detect, analyze, delegate, and verify
- **AND** it requires user approval before delegating an optimization
- **AND** application-code changes are delegated to the appropriate coder agent
- **AND** verification repeats an equivalent measurement
- **AND** the result is reported as verified, inconclusive, or regressed with evidence

### Requirement: Benchmark workflow selection

`/benchmark` MUST route performance-test design to `@plinth-java-performance` and select JMeter, Gatling, or JMH based on the test boundary, objective, workload model, and reporting needs.

#### Scenario: Create an appropriate performance test

- **GIVEN** a project user supplies a performance objective and target
- **WHEN** the user invokes `/benchmark`
- **THEN** `@plinth-java-performance` selects JMeter, Gatling, or JMH based on the test boundary
- **AND** it records the selection rationale
- **AND** the generated workflow defines reproducible workload and environment parameters
- **AND** results are evaluated against explicit thresholds
- **AND** non-equivalent runs are not presented as valid before/after comparisons

### Requirement: Performance engineer delegation boundary

`@plinth-java-performance` MUST coordinate profiling and performance-testing workflows without directly implementing application-code optimizations.

#### Scenario: Delegate approved optimization work

- **WHEN** profiling evidence identifies an optimization candidate and the user approves the target
- **THEN** `@plinth-java-performance` delegates implementation to `@plinth-java-coder`, `@plinth-java-spring-boot-coder`, `@plinth-java-quarkus-coder`, or `@plinth-java-micronaut-coder`
- **AND** it preserves the profiling evidence, benchmark results, implementation delegation record, and verification result as traceable artifacts
- **AND** it does not perform the coder agent's implementation work directly

### Requirement: Documentation and generated-output boundaries

The workflow documentation MUST describe tool selection, reproducibility, baseline authority, equivalent-comparison safeguards, and the boundary between performance comparison and the general implementation verification workflow.

#### Scenario: Update repository documentation

- **WHEN** the performance operation workflow is integrated
- **THEN** README and localized guidance describe `/profile`, `/benchmark`, and `@plinth-java-performance`
- **AND** generated outputs are refreshed only through the approved generator profiles
- **AND** generated `skills/`, `.cursor/rules/`, and `docs/` are not edited directly

## Source and Derivation

- Source artifact: GitHub issue [#1094](https://github.com/jabrena/plinth/issues/1094).
- Derivation direction: issue #1094 -> `rename-agents-robot-to-plinth` requirements -> renamed agent references in performance workflow routing.
