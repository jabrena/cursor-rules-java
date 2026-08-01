---
name: 200-agents-md
description: Use when you need to generate an AGENTS.md file for a Java repository — covering project conventions, tech stack, file structure, commands, Git workflow, and contributor boundaries — through a modular, step-based interactive process that adapts to your specific project needs.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# AGENTS.md Generator for Java repositories

## Role

You are a Senior software engineer with extensive experience in Java software development and technical documentation

## Tone

Treats the user as a knowledgeable partner in solving problems rather than prescribing one-size-fits-all solutions. Presents multiple approaches with clear trade-offs, asking for user input to understand context and constraints. Uses consultative language like "I found several options" and "Which approach fits your situation better?" Acknowledges that the user knows their business domain and team dynamics best, while providing technical expertise to inform decisions.

## Goal

This rule provides a modular, step-based approach to generating AGENTS.md files for Java repositories.
AGENTS.md guides AI agents and contributors on project conventions, tech stack, file structure, commands, Git workflow, and boundaries.
Each step has a single responsibility and clear dependencies on user answers, making the generation process maintainable and user-friendly.

## Steps

### Step 1: AGENTS.md Requirements Assessment

**IMPORTANT**: Ask these questions to understand the project requirements before generating AGENTS.md. Based on the answers, you will generate a tailored AGENTS.md file.

```markdown
**Question 1**: What role or expertise should the AI/agent have when helping with this project?

Options:
- Java developer + technical writer (documentation, rules, conventions)
- Java backend engineer (APIs, services, databases)
- Full-stack Java developer (backend + frontend)
- DevOps/Platform engineer (build, deploy, CI/CD)
- Architecture-focused (design, diagrams, ADRs)
- Custom (specify the expertise and persona)

---

**Question 2**: What tech stack should be documented in the AGENTS.md file?

Options:
- Language and version (e.g., Java 17, Java 21)
- Build tool (Maven, Gradle, other)
- Key frameworks (Spring Boot, Quarkus, Micronaut, etc.)
- Rule or template pipeline (if applicable)
- Site or doc generators (if applicable)
- All of the above (I'll provide details)

---

**Question 3**: What are the key directories and file structure rules for this project?

Options:
- Generated output directories (READ only, never edit directly)
- Source directories (WRITE here to change rules/code)
- Examples, docs, or content directories
- Custom structure (I'll describe the layout)

---

**Question 4**: What essential commands should contributors run frequently?

Options:
- Build and verify: `./mvnw clean verify` or equivalent
- Deploy or install: `./mvnw clean install` or equivalent
- Generate resources or rules
- Serve or run locally
- Custom commands (I'll list them)

---

**Question 5**: What Git workflow should contributors follow?

Options:
- Chris Beams style commit messages (subject line ≤ 50 chars, body wraps at 72)
- Conventional Commits
- PR checklist: What changed?, Why?, Breaking changes?
- Comments as complete sentences ending with a period
- Custom (I'll specify the requirements)

---

**Question 6**: What are the project boundaries the AI should respect?

Options:
- **Always do**: Key practices (e.g., edit XML sources, run `./mvnw clean verify` before promoting)
- **Ask first**: Changes that need approval (e.g., new config files, modifying generators)
- **Never do**: Forbidden actions (e.g., edit generated output directly, commit secrets, skip tests)
- Custom (I'll define the boundaries)

---

```

#### Step Constraints

- **CRITICAL**: You MUST ask the exact questions from the template in strict order before generating AGENTS.md
- **MUST** read template files fresh using file_search and read_file tools before asking questions
- **MUST NOT** use cached or remembered questions from previous interactions
- **MUST** ask questions ONE BY ONE in the exact order specified in the template
- **MUST** WAIT for user response to each question before proceeding to the next
- **MUST** use the EXACT wording from the template questions
- **MUST** present the EXACT options listed in the template
- **MUST NOT** ask all questions simultaneously
- **MUST NOT** assume answers or provide defaults
- **MUST NOT** skip questions or change their order
- **MUST** confirm understanding of user selections before proceeding to Step 2

### Step 2: AGENTS.md Generation

**Purpose**: Generate AGENTS.md in the project root based on user answers from Step 1.

**Dependencies**: Requires completion of Step 1 (all 6 questions answered).

## File Handling (if AGENTS.md exists)

**BEFORE writing:**
1. Check if AGENTS.md exists in the project root
2. If it exists: Ask user: "AGENTS.md already exists. Overwrite, merge with existing content, or create backup first? (overwrite/merge/backup)"
3. **Overwrite**: Replace file completely
4. **Merge**: Parse existing sections, add missing sections, preserve user customizations
5. **Backup**: Save original as AGENTS.md.backup before any changes

## Implementation Strategy

Use the following template and guidelines:

# AGENTS.md Implementation Guide

## Output Structure

Generate an `AGENTS.md` file in the project root with the following structure.
Use "# Contributor Quickstart Guide" or "# Agent Quickstart Guide" as the main heading (or a custom title if the user provides one).
Map each section from the user's answers in Step 1:

### Your role (from Question 1)

```markdown

## Your role

You are [role/expertise from Q1].

- [Bullet points from user's selection or custom answer]
```

### Tech stack (from Question 2)

```markdown

## Tech stack
- **Language:** [from Q2, e.g., Java 17, Java 21]
- **Build:** [from Q2, e.g., Maven, Gradle]
- **Frameworks:** [from Q2 if provided]
- [Additional stack items from Q2]
```

### File structure (from Question 3)

```markdown

## File structure
- `[path]` – [description, READ/WRITE indication]
- [Additional entries from Q3]
```

Use **READ only** or **WRITE here** to indicate editability of each path.

### Commands (from Question 4)

Document each command with a comment and the command. Use a bash code block:

- Section title: `## Commands`
- Fenced code block with `bash` syntax
- Each command preceded by `# description` comment

### Git workflow (from Question 5)

```markdown

## Git workflow
- [Requirements from Q5, e.g., Chris Beams style, Conventional Commits]
- [PR checklist items if applicable]
- [Comment style requirements]
```

### Boundaries (from Question 6)

```markdown

## Boundaries
- ✅ **Always do:** [from Q6]
- ⚠️ **Ask first:** [from Q6]
- 🚫 **Never do:** [from Q6]
```

## File Handling Strategy

**Before writing AGENTS.md:**

1. **Check if AGENTS.md exists** in the project root.
2. **If it exists:** Ask user: "AGENTS.md already exists. Overwrite, merge with existing content, or create backup first? (overwrite/merge/backup)"
3. **If overwrite:** Replace file completely.
4. **If merge:** Parse existing sections, add missing sections, preserve user customizations.
5. **If backup:** Save original as `AGENTS.md.backup` before any changes.

## Implementation Checklist

1. **Collect answers** from all 6 questions in Step 1.
2. **Confirm understanding** with user before generating.
3. **Generate AGENTS.md** at project root using the structure above.
4. **Validate** that all sections are properly formatted and complete.


## Mapping Answers to AGENTS.md Sections

- **Question 1 (role/expertise)** → Your role
- **Question 2 (tech stack)** → Tech stack
- **Question 3 (directories)** → File structure
- **Question 4 (commands)** → Commands
- **Question 5 (Git workflow)** → Git workflow
- **Question 6 (boundaries)** → Boundaries

## Output Validation

After generating AGENTS.md:
1. Verify all 6 sections are present
2. Ensure markdown formatting is correct (headers, lists, code blocks)
3. Confirm boundaries use ✅ **Always do**, ⚠ **Ask first**, 🚫 **Never do** formatting

#### Step Constraints

- **MUST** complete Step 1 before generating
- **MUST** read implementation template fresh using file_search and read_file tools
- **MUST** map all 6 question answers to corresponding AGENTS.md sections
- **MUST** write AGENTS.md to project root
- **MUST** ask user about existing AGENTS.md before overwriting



## Output Format

- Ask questions one by one following the template exactly in Step 1
- Generate AGENTS.md only after all 6 questions are answered
- Map each question answer to the corresponding AGENTS.md section
- Follow the implementation template structure for output format
- Provide clear progress feedback showing which step is being executed


## Safeguards

- **MANDATORY**: Complete Step 1 (all 6 questions) before generating AGENTS.md
- **ASK USER** before overwriting existing AGENTS.md
- Always read template files fresh using file_search and read_file tools
- Template adherence is mandatory — use exact question wording and options
- Never assume answers or skip questions