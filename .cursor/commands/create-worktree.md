---
description: 'Create an isolated Git worktree on a new conventionally named branch.'
argument-hint: '[issue-or-change|type description] [target-path] [base-reference]'
model: 'inherit'
agent: 'plinth-tech-lead'
tools:
  - 'Read'
  - 'Bash'
---

# create-worktree

Always create a new branch and linked Git worktree for isolated or parallel work without changing the current checkout.

## Usage

```text
/create-worktree <issue-or-change|type description> [<target-path>] [<base-reference>]
```

## Accepted Inputs

- An issue/change identifier, or an explicit branch type and description
- Optional preferred target path
- Optional base reference
- Supported branch types: `feat`, `fix`, `docs`, `refactor`, and `chore`
- Preferred branch format: `<type>/<issue-or-change>-<kebab-case-description>` when an issue or OpenSpec change is available, otherwise `<type>/<kebab-case-description>`
- Fresh-name rule: if the preferred branch name or target path already exists, derive the next unused suffix such as `-2`, `-3`, and continue with the fresh branch/worktree

## Owning Agent

`@plinth-tech-lead`

## Associated Capabilities

- Git branch naming and validation
- Git linked worktrees
- Parallel OpenSpec child-change coordination

## Workflow

1. Resolve the preferred branch name from the issue/change identifier or explicit branch details.
2. Resolve an absolute preferred target path and the base reference.
3. Resolve the repository default branch and the current branch.
4. Verify the current checkout is `main` or the repository default branch before creating the worktree branch.
5. If the current branch is not `main` or the default branch, stop and ask whether to switch to the default branch, choose a different base, or continue explicitly from the current branch.
6. Verify the repository and selected base reference are valid before creating anything.
7. Derive a fresh branch name and fresh target path when the preferred branch exists, is checked out in another worktree, or the preferred target path exists.
8. Verify the final branch name does not exist, is not checked out in another worktree, and the final target path does not exist.
9. Run the equivalent of `git worktree add -b <branch> <target-path> <base-reference>`.
10. Report the created branch, absolute worktree path, base reference, and cleanup command.

## Output

- Created local branch
- Created linked worktree
- Exact branch name, absolute path, and base reference
- Cleanup command: `git worktree remove <absolute-worktree-path>`

## Safeguards

- Never reuse the current checkout or an existing worktree for the requested work.
- When the preferred branch or path conflicts with existing state, choose a fresh unused branch name and target path instead of reusing or overwriting existing state.
- Stop and ask when the current branch is not `main` or the repository default branch.
- Stop if the base reference is invalid.
- Leave existing branches, worktrees, directories, and files unchanged on conflict.
- Do not commit, push, remove worktrees, delete branches, or use force.
