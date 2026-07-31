# Embedded Commands Inventory

## Goal

Provide a quick checklist of the embedded commands available for installation in this repository.

## Embedded commands

| Command | SDLC phase | Primary purpose |
| --- | --- | --- |
| `/update-issue` | Analysis / Design | Update existing GitHub or Jira issues with structured user story, acceptance criteria, and resource content. |
| `/explore-problem` | Analysis / Design | Evaluate an issue through five points of view and post a Functional Specification comment on the issue. |
| `/create-acceptance-criteria` | Analysis / Design | Derive Gherkin acceptance criteria from a Functional Specification comment and post them as a separate issue comment. |
| `/create-feature-branch` | Analysis / Design to Implementation | Create and switch to a conventionally named branch for repository-backed analysis, design, or implementation. |
| `/create-worktree` | Analysis / Design to Implementation | Create an isolated branch and linked worktree for parallel work. |
| `/explore-design` | Design | Compare technical approaches and obtain an approved design direction. |
| `/create-adr` | Design | Record an architectural decision, alternatives, rationale, and consequences. |
| `/create-diagram` | Design | Create a focused architecture or design diagram from approved artifacts. |
| `/create-spec` | Analysis / Design | Create or update one or more validated OpenSpec changes. |
| `/implement-spec` | Implementation | Deliver an approved plan or validated OpenSpec task list through framework-aware delegation. |
| `/close-spec` | Operation / Maintenance | Archive an OpenSpec change by name using the OpenSpec CLI. |
| `/profile` | Operation | Coordinate Java profiling from baseline detection through verified optimization. |
| `/benchmark` | Operation | Select and coordinate JMeter, Gatling, or JMH performance workflows. |

## Installation target options

- `.github/commands`
- `.claude/commands`
- `.cursor/command`
- `.codex/commands`
