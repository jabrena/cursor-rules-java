---
description: 'Create and switch to a conventionally named feature branch.'
argument-hint: '[issue-or-change|type description] [base-reference]'
model: 'inherit'
agent: 'plinth-tech-lead'
tools:
  - 'Read'
  - 'Bash'
---

# create-feature-branch

Create and switch the current checkout to a conventionally named local branch for GitHub issue, OpenSpec, analysis, design, documentation, or implementation work.

## Usage

```text
/create-feature-branch <issue-or-change|type description> [<base-reference>]
```

## Accepted Inputs

- An issue/change identifier, or an explicit branch type and description
- Optional base reference
- Supported branch types: `feat`, `fix`, `docs`, `refactor`, and `chore`
- Preferred branch format: `<type>/<issue-or-change>-<kebab-case-description>` when an issue or OpenSpec change is available, otherwise `<type>/<kebab-case-description>`

## Owning Agent

`@plinth-tech-lead`

## Associated Capabilities

- Git branch naming and validation
- GitHub issue and OpenSpec change traceability
- Analysis/design-to-implementation transition
- Safe handling of existing local work

## Workflow

1. Read the repository instructions and resolve the branch type, issue/change identifier, and kebab-case description from the request.
2. Prefer GitHub issue or OpenSpec change identifiers for traceable work; for complex architectural changes, keep the branch aligned to the OpenSpec change name.
3. Resolve the repository default branch and the current branch.
4. Verify the current checkout is `main` or the repository default branch before creating the new branch.
5. If the current branch is not `main` or the default branch, stop and ask whether to switch to the default branch, choose a different base, or continue explicitly from the current branch.
6. Verify the repository, selected base reference, branch name, and a safe working tree before changing the current checkout.
7. Stop if the proposed branch already exists or is checked out in another worktree.
8. Create and switch to the conventionally named local branch.
9. Report the branch name, base reference, working tree status, and traceability source used to derive the branch.

## Output

- Created local branch
- Confirmation with the exact branch name and base reference
- Working tree status and any traceability source used for naming
- A checkout ready for GitHub issue follow-up, OpenSpec artifacts, ADRs, diagrams, documentation, or application code

## Safeguards

- Stop when uncommitted work could be displaced or mixed into the new branch.
- Never discard, stash, reset, or move existing uncommitted changes unless the user explicitly requests that action.
- Stop and ask when the current branch is not `main` or the repository default branch.
- Do not overwrite, delete, or force-update an existing branch.
- Allow analysis and design artifacts to be committed before application-code implementation.
- The command does not create a commit automatically.
- Do not push or open a pull request automatically.
