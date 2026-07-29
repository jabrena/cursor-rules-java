# Project Workflows

Analysis and design are composable. Start from the authoritative artifacts already available; a plan is not required before OpenSpec, and OpenSpec is not required before a plan.

## Lifecycle Options

1. Optionally run `/create-feature-branch` before committing repository-backed analysis and design artifacts.
2. Use `/create-worktree` when independent child changes can run in isolated branches.
3. Run `/update-issue` to refine the tracked need in GitHub or Jira.
4. Run `/explore-design` when material technical alternatives remain unresolved.
5. Run `/create-adr` for durable decisions and `/create-diagram` for useful architecture views.
6. Run `/create-spec` from an issue, approved design, ADRs, existing plan, existing OpenSpec, or a valid combination.
7. Ask `@plinth-tech-lead` to deliver the selected plan or OpenSpec `tasks.md`.
8. Use `/profile` or `/benchmark` with `@plinth-java-performance` when post-implementation performance evidence is needed.

Common paths include issue to plan, issue to OpenSpec, plan to OpenSpec, and existing OpenSpec to plan. `/create-spec` may propose multiple OpenSpec changes when outcomes have independent value, ownership, release, risk, rollback, or deployment boundaries. The user approves that change map before creation.

## Artifact Authority

| Artifact | Authoritative concern |
| --- | --- |
| Issue or user story | Problem, value, scope, and acceptance criteria |
| ADR | Architecture decision and consequences |
| OpenSpec specification | Requirements and scenarios |
| Implementation plan | Technical delivery strategy, sequence, dependencies, and verification |
| Selected OpenSpec `tasks.md` | Execution tracking when that workflow is selected |

Derivation is one-way and recorded. A derived artifact never silently rewrites its sources. When artifacts conflict, ask `@plinth-business-analyst` to review the conflicting artifacts, decide which authoritative concern must change, update it explicitly, regenerate affected derivatives, and review alignment again.

## Parallel Delivery

Use separate worktrees for independent command, agent, or planning changes. Integrate and validate those source changes before updating shared documentation. The tech lead may delegate implementation groups concurrently only when dependencies and file ownership make the work independent.
