---
description: 'Design and coordinate a reproducible Java performance test.'
argument-hint: '[target]'
model: 'inherit'
agent: 'plinth-java-performance'
tools:
  - 'Read'
  - 'Write'
  - 'Edit'
  - 'Bash'
---

# benchmark

Select and coordinate an appropriate Java performance test with reproducible workload, environment, thresholds, and result artifacts.

## Usage

```text
/benchmark <target> [objective-or-threshold] [workload] [environment] [preferred-tool]
```

## Accepted inputs

- Target application, endpoint, workflow, method, or component
- Performance objective or threshold
- Expected workload and environment
- Optional preferred tool

## Owner and skills

- Owner: `@plinth-java-performance`
- Associated skills: `@151-java-performance-jmeter`, `@152-java-performance-gatling`, and existing Maven/JMH guidance

## Tool selection

| Need | Tool |
| --- | --- |
| HTTP/API load and performance test | JMeter or Gatling |
| Scenario-oriented load model and reports | Gatling |
| Isolated JVM method or component microbenchmark | JMH |

## Workflow

1. Clarify target boundary, objective, thresholds, workload, and environment.
2. Select JMeter, Gatling, or JMH and record the rationale.
3. Define warm-up, duration, concurrency, data setup, and result artifacts.
4. Generate or coordinate the reproducible performance workflow using the selected skill or Maven/JMH guidance.
5. Evaluate results against explicit thresholds.
6. Report limitations, environment metadata, and whether results are comparable.

## Output

- Selected tool and rationale
- Reproducible test configuration
- Baseline or result artifacts
- Threshold assessment
- Limitations and environment metadata

## Safeguards

- Do not present non-equivalent runs as valid before/after comparisons.
- Do not use JMeter or Gatling for isolated JVM microbenchmarks when JMH is the correct boundary.
- Do not use JMH for end-to-end load behavior.
- Keep workload, environment, and threshold assumptions explicit.
