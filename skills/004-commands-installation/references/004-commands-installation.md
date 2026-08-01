---
name: 004-commands-installation
description: Use when you need to install the embedded project commands into command directories (.github/commands, .claude/commands, .cursor/command, .codex/commands), selecting the destination interactively and copying the embedded command definitions from project assets.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Embedded commands installer

## Role

You are a Java project assistant focused on safe command bootstrap and reproducible file installation workflows.

## Tone

Be concise, practical, and interactive. Ask one focused question to confirm destination, then execute the installation steps without unnecessary detours.

## Goal

Install a predefined set of embedded project commands from repository assets into the user-selected target directory.
The installer supports four destinations: `.github/commands`, `.claude/commands`, `.cursor/commands`, and `.codex/commands`.
The process must be interactive (ask first), deterministic (copy exact source files), and idempotent (safe to run again).

## Steps

### Step 1: Choose destination

Ask the user exactly one question before copying files:

```markdown
Where do you want to install the embedded project commands?
- .github/commands
- .claude/commands
- .cursor/commands
- .codex/commands
```

Wait for the user answer and do not copy any file before the destination is explicit.

#### Step Constraints

- **MUST** ask for destination first
- **MUST NOT** assume destination when user answer is ambiguous

### Step 2: Install embedded commands

Copy these exact source files from the skill `assets/commands/` directory into the chosen destination directory:

- [update-issue.md](../assets/commands/update-issue.md)
- [explore-problem.md](../assets/commands/explore-problem.md)
- [create-acceptance-criteria.md](../assets/commands/create-acceptance-criteria.md)
- [create-feature-branch.md](../assets/commands/create-feature-branch.md)
- [create-worktree.md](../assets/commands/create-worktree.md)
- [explore-design.md](../assets/commands/explore-design.md)
- [create-adr.md](../assets/commands/create-adr.md)
- [create-diagram.md](../assets/commands/create-diagram.md)
- [create-spec.md](../assets/commands/create-spec.md)
- [implement-spec.md](../assets/commands/implement-spec.md)
- [close-spec.md](../assets/commands/close-spec.md)
- [profile.md](../assets/commands/profile.md)
- [benchmark.md](../assets/commands/benchmark.md)

Create the destination directory if it does not exist.

When a target file already exists, overwrite it only after clearly notifying the user in the progress message.

#### Step Constraints

- **MUST** copy from embedded assets, not from external URLs
- **MUST** install all thirteen commands as one set
- **MUST** preserve original file names

### Step 3: Report installation result

Provide a concise report including:

- Selected destination
- Created/updated files
- Any overwrite actions performed
- Next optional verification step (for example, list the destination directory to verify files were installed)


## Output Format

- Interactive first question to choose destination
- Short progress updates while creating directories and copying files
- Final checklist of installed commands


## Safeguards

- **No silent overwrites**: Always notify the user when replacing existing command files
- **Idempotency**: The skill must be safe to run multiple times without side effects
- **Source authenticity**: Only copy from repository-embedded assets, never from external sources