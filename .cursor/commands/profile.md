---
description: 'Coordinate a reproducible Java profiling and optimization lifecycle.'
argument-hint: '[target]'
model: 'inherit'
agent: 'plinth-java-performance'
tools:
  - 'Read'
  - 'Write'
  - 'Edit'
  - 'Bash'
---

# profile

Coordinate a Java profiling lifecycle from reproducible baseline through evidence-backed optimization verification.

## Usage

```text
/profile <application-or-module> [issue|plan|openspec-change|suspected-problem] [runtime-command] [workload]
```

## Accepted inputs

- Application, module, or repository path
- Optional issue, implementation plan, OpenSpec change, or suspected performance problem
- Runtime command and representative workload
- Optional existing baseline or profiling artifacts

## Owner and skills

- Owner: `@plinth-java-performance`
- Associated skills: `@161-java-profiling-detect`, `@162-java-profiling-analyze`, `@163-java-profiling-refactor`, and `@164-java-profiling-verify`

## Workflow

1. Record runtime, environment, workload, and baseline metadata.
2. Use profiling detection to collect reproducible evidence.
3. Analyze hotspots and rank findings by impact, confidence, and risk.
4. Ask for user approval before selecting an optimization target.
5. Delegate application-code changes to the appropriate Java/framework coder agent.
6. Repeat an equivalent measurement and compare against the baseline.
7. Report the outcome as verified, inconclusive, or regressed with evidence.

## Output

- Baseline metadata
- Profiling artifacts and findings
- Prioritized optimization recommendation
- Delegation record
- Before/after comparison
- Verified, inconclusive, or regressed outcome

## Safeguards

- Do not optimize without user approval.
- Do not claim improvement from non-equivalent measurements.
- Do not let `@plinth-java-performance` implement application-code changes directly.
- Keep baseline, evidence, delegation, and verification artifacts traceable.
