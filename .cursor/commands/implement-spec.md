---
description: 'Deliver an approved plan or OpenSpec change through controlled implementation.'
argument-hint: '[openspec-change]'
model: 'inherit'
agent: 'plinth-tech-lead'
tools:
  - 'Read'
  - 'Write'
  - 'Edit'
  - 'Bash'
---

# implement-spec

Deliver an approved implementation plan or validated OpenSpec task list through controlled implementation.

## Usage

```text
/implement-spec <approved-plan|openspec-change> [task-or-group] [constraints]
```

## Accepted inputs

- An approved implementation plan (`*.plan.md`)
- An OpenSpec change containing a validated `tasks.md`
- Optional task or group selection, branch/worktree context, and implementation constraints

A bare issue is context, not an execution contract. When repository policy requires structured planning and neither executable artifact exists, stop and direct the user to provide an approved implementation plan or run `/create-spec`.

## Owner and delegation

- Owner: `@plinth-tech-lead`
- Delegation targets: `@plinth-java-coder`, `@plinth-java-spring-boot-coder`, `@plinth-java-quarkus-coder`, `@plinth-java-micronaut-coder`, or `@plinth-no-java`
- The tech lead coordinates delivery and MUST NOT implement application code directly.

## Mandatory execution contract

- If the command runner is not `@plinth-tech-lead`, immediately delegate the whole command execution to `@plinth-tech-lead` and wait for its result.
- `@plinth-tech-lead` MUST invoke the selected implementation agent for implementation, test, and verification work; naming an agent in the response is not delegation.
- If agent invocation is unavailable in the current environment, stop and report that `/implement-spec` cannot proceed instead of implementing directly.
- For OpenSpec inputs, pass the **OpenSpec readiness gate** below before skill discovery, Git-location changes, or implementation delegation.
- Before any implementation agent starts, pass the branch/worktree gate below and report the selected isolation strategy.
- Before any implementation agent starts, `@plinth-tech-lead` MUST publish a **Skill discovery brief** and pass an ordered **candidate skills to read** list in every implementation handoff.

## OpenSpec readiness gate

- After loading and structurally validating the selected change, determine the **selected execution scope** from the requested task or group, or all incomplete tasks when no narrower scope is supplied.
- Require **bidirectional traceability**: every selected behavior-changing implementation task has one or more concrete scenarios in `specs/**/spec.md`, and every scenario applicable to the selected scope has an actionable implementation or verification task. Repository validation tasks must map to an explicit quality, safeguard, or verification obligation.
- A concrete scenario defines its trigger, required preconditions, and observable outcome without unresolved placeholders. An actionable task names a specific remaining implementation or verification outcome; completed-only tasks do not provide executable work.
- Treat evidence as not ready when it is **absent, ambiguous, placeholder, completed-only, partial, or divergent** from the selected change requirements or safeguards.
- When the gate fails, stop and report **each unsupported scenario or task**, identify the owning OpenSpec artifact, and instruct the contributor to **update the OpenSpec change and rerun `/implement-spec`**.
- The command must not invent acceptance criteria or tasks. A failed readiness gate stops **before skill discovery, Git-location changes, or implementation delegation**.
- Do not require unrelated future task groups to be implementation-complete when a narrower task or group was explicitly selected.

## Branch/worktree gate

- After the OpenSpec readiness gate passes, inspect the workspace with `git status --short` before resolving or changing an implementation location.
- If the workspace is dirty, stop immediately and report the changed/untracked paths. Do not create a feature branch, create a worktree, delegate implementation, or ask for approval to continue in the dirty checkout.
- Continue only after the user cleans, commits, or stashes the workspace and reruns `/implement-spec`.
- The dirty-workspace stop remains non-bypassable.

## Implementation location precedence

- Resolve location only after readiness and workspace cleanliness checks pass; invocation constraints take precedence over any artifact location.
- If invocation constraints omit location, read an exact `## Implementation Location` section from the selected change's `design.md` using this canonical form:

```markdown
## Implementation Location

- Strategy: `main` | `feature-branch` | `worktree`
- Reference: `<branch-name-or-worktree-path>`
```

- `Strategy` is required. `Reference` is optional for `main` and may be omitted for a new feature branch or worktree that the existing creation command will name and report.
- Treat a missing, blank, or unsupported `Strategy` as unresolved. Unless invocation constraints already resolved the conflict, ask the contributor to choose `main`, a feature branch, or a worktree and wait for the answer before creating or selecting a location.
- Do not silently guess a strategy and do not delegate implementation until the location is confirmed.
- If the confirmed strategy is a feature branch or worktree, apply the existing creation and conflict safeguards below and report the isolation strategy.
- Report the selected feature branch or worktree paths before delegating implementation.
- If the work is serial and the current checkout is not already a safe, suitable feature branch, execute `/create-feature-branch` before delegating implementation.
- If independent groups can run in parallel or need isolation, execute `/create-worktree` for each independent branch/worktree before delegating implementation.
- If the confirmed strategy is `main` or the repository default branch, warn about direct implementation there and require **separate explicit approval** before implementation starts.
- Do not start implementation on `main` or the repository default branch unless the user explicitly approves that exception after being warned.
- If branch or worktree creation is blocked by unsafe git state, existing branches, existing worktrees, or ambiguous base references, stop and ask the user how to proceed.

## Skill discovery gate

- `@plinth-tech-lead` MUST build the **Skill discovery brief** before the first implementation handoff, following **Skill discovery before delegation** in `@plinth-tech-lead`.
- OpenSpec execution artifacts MUST include `@042-planning-openspec` as the planning anchor skill.
- The tech lead MUST read `@042-planning-openspec` from `.agents/skills/` or `skills/` when the selected artifact is OpenSpec before delegating implementation.
- Every implementation handoff MUST include **candidate skills to read**, **required skill report**, and a **telemetry reminder** so benchmark metrics can record exact skill ids.
- The selected implementation agent MUST read each candidate skill from `.agents/skills/<skill-id>/SKILL.md` or `skills/<skill-id>/` before editing owned files, unless it returns a one-line skip reason in **Skills skipped**.
- Benchmark and metrics runs MUST record in `plinth_usage.skills` only skill ids whose SKILL files were actually read or invoked during the run.
- Reading `.cursor/agents/*.md` or mentioning `@skill-id` in prose does **not** count as reading or invoking a skill.
- Naming `@plinth-tech-lead` or an implementation agent in the response does **not** count as invoking that agent; use the environment's agent or subagent invocation mechanism when available.

## Workflow

1. Load the actual selected plan or OpenSpec `tasks.md`; structurally validate OpenSpec input and confirm the artifact is current, approved, and internally consistent.
2. For OpenSpec input, determine the selected execution scope and pass the bidirectional scenario/task readiness gate; stop with artifact-specific remediation before side effects when it fails.
3. Stop and route the conflict to `@plinth-business-analyst` for manual assessment when the issue, ADRs, specification, plan, or task list conflicts materially.
4. Inspect workspace cleanliness and stop on dirty state, then resolve location from invocation constraints, valid `design.md`, or an explicit contributor answer.
5. Complete the branch/worktree gate by applying the existing `main`, feature-branch, or worktree safeguards and report the selected location before delegation.
6. Identify the framework from authoritative artifacts, build files, and code; select the matching specialized coder or `@plinth-no-java` when the execution artifact does not use Java.
7. Extract task groups, dependencies, milestones, verification gates, and expected file ownership.
8. Build the Skill discovery brief per `@plinth-tech-lead`; when the artifact is OpenSpec, read `@042-planning-openspec` and select framework-specific candidate skills for the routed implementation agent.
9. Serialize dependent or overlapping groups; run groups concurrently only when dependencies and owned files do not conflict.
10. Invoke the selected implementation agent for each group with task IDs, owned files, acceptance criteria, blocked-by relationships, focused validation commands, candidate skills to read, required skill report, and telemetry reminder.
11. Integrate delegated results and require changed-file, test, build, risk, blocker, skills applied, and skills skipped evidence.
12. Mark OpenSpec tasks complete only after their acceptance criteria and focused verification gates pass, then report completion against the selected artifact.

## Output

- Selected execution artifact and source reference
- Framework decision and coder routing rationale
- Feature-branch or worktree gate result, branch names, worktree paths, and rationale
- Serial/parallel execution map with dependencies and file ownership
- Skill discovery brief and candidate skills passed to implementation agents
- Results and changed files by task or group
- Skills applied and skills skipped reported by each implementation agent
- Test and build evidence
- Updated OpenSpec task status, when applicable
- Remaining blockers, risks, and follow-up work

## Safeguards

- Do not implement from a stale, unapproved, missing, or conflicting execution artifact.
- Do not continue from OpenSpec input whose selected scope lacks complete bidirectional scenario/task traceability.
- Do not perform skill discovery, Git-location changes, or implementation delegation before the OpenSpec readiness gate passes.
- Do not continue in the original command runner when `@plinth-tech-lead` has not accepted the orchestration handoff.
- Do not start implementation before the feature-branch or worktree gate has passed.
- Do not start implementation before the Skill discovery brief is published and candidate skills are included in the handoff.
- Do not record an agent in benchmark metrics unless that agent was actually invoked, not merely referenced or read from `.cursor/agents/`.
- Do not record a skill in benchmark metrics unless its SKILL file was read or the skill was invoked.
- Do not bypass a dirty workspace by asking for approval to continue; stop and resume only after the workspace is clean.
- Do not treat a written plan to delegate as delegation; invoke the selected implementation agent.
- Do not silently change issue scope, requirements, ADR decisions, or plan approach.
- Do not run dependent groups before prerequisite verification gates pass.
- Do not delegate concurrent groups with overlapping file ownership without an explicit integration strategy.
- Do not mark tasks complete before acceptance criteria and focused checks pass.
- Do not bypass repository validation or edit generated outputs directly.
