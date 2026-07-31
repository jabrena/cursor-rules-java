---
description: 'Create an architecture or design diagram from selected source artifacts.'
argument-hint: '[source-artifact] [diagram-type]'
model: 'inherit'
agent: 'plinth-architect'
tools:
  - 'Read'
  - 'Write'
  - 'Edit'
---

# create-diagram

Create an architecture or design diagram that explains a selected system view.

## Usage

```text
/create-diagram <source-artifact> [<diagram-type>]
```

## Accepted Inputs

- Issue, approved design, ADR, specification, plan, or repository architecture
- Optional diagram type: sequence, class, state machine, ER, or C4
- Desired audience and level of detail

## Owning Agent

`@plinth-architect`

## Associated Skill

`033-architecture-diagrams`

## Workflow

1. Confirm the diagram purpose, audience, type, and scope.
2. Extract only the components and relationships needed for that view.
3. Generate the diagram source using the repository convention.
4. Check names and relationships against the source artifacts.
5. Report the diagram path and any assumptions.

## Output

- Diagram source for the selected architecture view
- Concise explanation of scope and assumptions
- Links to the source artifacts represented

## Safeguards

- Do not mix unrelated architecture views into one diagram.
- Do not invent components or interactions.
- Do not replace an existing diagram without reviewing its current purpose.
