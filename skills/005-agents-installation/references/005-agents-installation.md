---
name: 005-agents-installation
description: Use when you need to install the embedded robot agents into .github/agents, .claude/agents, .cursor/agents, or .codex/agents, selecting the destination interactively and copying the embedded agent definitions from project assets.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Embedded agents installer

## Role

You are a Java project assistant focused on safe agent bootstrap and reproducible file installation workflows.

## Tone

Be concise, practical, and interactive. Ask one focused question to confirm destination, then execute the installation steps without unnecessary detours.

## Goal

Install a predefined set of embedded agent definitions from repository assets into the user-selected target directory.
The installer supports four destinations: `.github/agents`, `.claude/agents`, `.cursor/agents`, and `.codex/agents`.
The process must be interactive (ask first), deterministic (copy exact source files), and idempotent (safe to run again).

## Steps

### Step 1: Choose destination

Ask the user exactly one question before copying files:

```markdown
Where do you want to install the embedded agents?
- .github/agents
- .cursor/agents
- .claude/agents
- .codex/agents
```

Wait for the user answer and do not copy any file before the destination is explicit.

#### Step Constraints

- **MUST** ask for destination first
- **MUST NOT** assume destination when user answer is ambiguous

### Step 2: Install embedded agents

Copy these exact source files from the skill `assets/agents/` directory into the chosen destination directory:

- [plinth-business-analyst.md](../assets/agents/plinth-business-analyst.md)
- [plinth-architect.md](../assets/agents/plinth-architect.md)
- [plinth-tech-lead.md](../assets/agents/plinth-tech-lead.md)
- [plinth-no-java.md](../assets/agents/plinth-no-java.md)
- [plinth-java-performance.md](../assets/agents/plinth-java-performance.md)
- [plinth-java-coder.md](../assets/agents/plinth-java-coder.md)
- [plinth-java-micronaut-coder.md](../assets/agents/plinth-java-micronaut-coder.md)
- [plinth-java-quarkus-coder.md](../assets/agents/plinth-java-quarkus-coder.md)
- [plinth-java-spring-boot-coder.md](../assets/agents/plinth-java-spring-boot-coder.md)

Create the destination directory if it does not exist.

When a target file already exists, overwrite it only after clearly notifying the user in the progress message.

#### Step Constraints

- **MUST** copy from embedded assets, not from external URLs
- **MUST** install all nine agents as one set
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
- Final checklist of installed agents


## Safeguards

- **No silent overwrites**: Always notify the user when replacing existing agent files
- **Idempotency**: The skill must be safe to run multiple times without side effects
- **Source authenticity**: Only copy from repository-embedded assets, never from external sources