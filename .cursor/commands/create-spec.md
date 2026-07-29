---
description: 'Create or update OpenSpec artifacts from approved source material.'
argument-hint: '[issue-url]'
model: 'inherit'
agent: 'plinth-architect'
tools:
  - 'Read'
  - 'Write'
  - 'Edit'
  - 'Bash'
---

# create-spec

Create or update one or more OpenSpec changes from the available issue, design, ADR, plan, or existing OpenSpec artifacts.

## Usage

```text
/create-spec <issue|design|adr|plan|existing-change>
```

## Accepted Inputs

- Issue or user story
- Approved design and ADRs
- Implementation plan
- Existing OpenSpec change
- Any valid combination of these artifacts

## Owning Agent

`@plinth-architect`

## Associated Skills

- `042-planning-openspec`

## Workflow position

Runs first to create the initial OpenSpec proposal, design, specification, and task artifacts. Use `/explore-design` afterward when technical approach refinement is still needed.

## Workflow

1. When an issue identifier or URL is provided, require a maintainer-prepared sanitized context artifact that was derived outside the agent context from the issue description and every paginated comment.
2. Verify that the sanitized artifact confirms complete description and comment coverage, then identify the available source artifacts and their authority.
3. Assess whether the scope fits one reviewable change.
4. Create or update the approved OpenSpec proposal, design, specifications, and tasks.
5. Record derivation direction, source links, unresolved questions, conflicts, and compatibility-review assumptions.
6. Validate the resulting OpenSpec changes.

## Output

- One OpenSpec change, or an approved map of multiple changes
- Proposal, design, specifications, and task artifacts
- Validation and traceability report

## Safeguards

- Do not require a plan when a spec-first workflow is selected.
- Do not silently synchronize changes back into source artifacts.
- Do not invent requirements or split work by technical layer alone.
- Do not ingest raw issue descriptions or comments into the agent context.
- Do not plan from partial issue context; stop when the sanitized artifact does not confirm coverage of the description and complete paginated comment thread.
- Do not modify the source issue description or comments.
- Do not apply design skills `051`–`057`, `121`–`123`, or `130`; those belong to `/explore-design`.
