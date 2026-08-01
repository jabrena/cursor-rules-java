---
description: 'Create a repository ADR for an approved architectural decision.'
argument-hint: '[decision-source] [adr-type]'
model: 'inherit'
agent: 'plinth-architect'
tools:
  - 'Read'
  - 'Write'
  - 'Edit'
---

# create-adr

Record an important architectural decision, its context, alternatives, and consequences.

## Usage

```text
/create-adr <decision-source> [<adr-type>]
```

## Accepted Inputs

- Approved design exploration, issue, specification, or implementation plan
- Existing architecture constraints and related ADRs
- Optional ADR type: general, functional requirements, or non-functional requirements

## Owning Agent

`@plinth-architect`

## Associated Skills

- `030-architecture-adr-general`
- `031-architecture-adr-functional-requirements`
- `032-architecture-adr-non-functional-requirements`

## Workflow

1. Confirm the decision scope and select the appropriate ADR skill.
2. Gather context, constraints, considered alternatives, and consequences.
3. Draft the ADR using the repository convention.
4. Check consistency with related issues, designs, specifications, and ADRs.
5. Present the ADR for approval before finalizing it.

## Output

- One repository-compliant ADR
- Decision status, rationale, alternatives, and consequences
- Links to relevant source artifacts

## Safeguards

- Do not create an ADR for a decision that has not been made.
- Do not invent alternatives or constraints.
- Do not modify unrelated ADRs.
