---
description: 'Refine the technical design of an issue or OpenSpec change before implementation.'
argument-hint: '[openspec-change]'
model: 'inherit'
agent: 'plinth-architect'
tools:
  - 'Read'
  - 'Write'
  - 'Edit'
  - 'Bash'
---

# explore-design

Improve and refine the technical approach for an issue or OpenSpec change after initial specification—with alternatives, trade-offs, compatibility review, rollout controls, and verification strategy—before implementation.

## Usage

```text
/explore-design <issue|openspec-change>
```

## Accepted Inputs

- Issue or user story
- OpenSpec change with unresolved technical approaches or design gaps
- Existing architecture context and constraints
- Relevant requirements, ADRs, or technical notes linked to the issue or change

## Owning Agent

`@plinth-architect`

## Associated Skills

- `051-design-two-steps-methods` for every refinement so preparatory work, behavior change, and verification remain explicitly sequenced
- `052-design-hamburger-method` when the scope is broad enough to need smallest-useful vertical slices
- `053-design-simple-rules` when alternatives need ordered design tradeoff evaluation before finalizing the direction
- `054-design-tdd` when testing-related requirements need test-first sequencing and verification-driven refinement
- `055-design-parallel-change` when database migration requirements need expand, migrate, contract sequencing
- `056-design-avoid-breaking-changes` when compatibility surfaces may change during refinement
- `057-design-feature-toggles` when rollout, rollback, compatibility windows, or temporary behavior controls affect the design direction
- `059-design-atdd` as the final alignment gate when an OpenSpec change provides a proposal, specification scenarios, and a task checklist
- `121-java-object-oriented-design`, then `122-java-type-design`, then `123-java-design-patterns` when Java responsibilities, type boundaries, or collaboration decisions affect the refined approach
- `130-java-testing-strategies` when testing strategy, boundary coverage, flakiness, or verification quality affect the refined approach

## Workflow position

Runs after `/create-spec`, not as the first mission in the workflow.

## Workflow

1. Identify the design source, problem, goals, constraints, stakeholders, assumptions, unknowns, success criteria, and recommendation criteria.
2. Compare feasible approaches and trade-offs, recording rejected options and risks.
3. Recommend a design direction with rationale.
4. Describe components, boundaries, interactions, data flow, failure handling, testing strategy, and validation needed before implementation.
5. Identify unresolved questions and ADR candidates.
6. Refine the existing OpenSpec change when one is provided, without replacing initial OpenSpec authoring owned by `/create-spec`.
7. When an OpenSpec change provides an execution goal, acceptance criteria, and a task checklist, invoke `059-design-atdd` as the final read-only alignment gate after approved refinements. Report its evidence-backed `ready` or `changes-requested` outcome.
8. For `ready`, continue to the explicit design-approval step without treating the review itself as approval. For `changes-requested`, report every unresolved finding, ask the maintainer how the affected artifacts should be revised, apply only maintainer-approved revisions, and rerun the alignment gate before requesting approval.

## Output

- Approved design direction
- Alternative and trade-off analysis
- ADR candidates
- Open questions and assumptions
- Refined OpenSpec change artifacts when applicable
- Evidence-backed ATDD alignment outcome and unresolved findings when reviewable OpenSpec artifacts are provided

## Safeguards

- Do not implement application code.
- Do not modify source user stories, Gherkin files, ADRs, or OpenAPI files while exploring and refining the design.
- Do not hide material trade-offs or unresolved decisions.
- Do not treat the recommendation as approved until the user confirms it.
- Do not apply `042-planning-openspec`; initial OpenSpec authoring belongs to `/create-spec`.
- Do not apply `059-design-atdd` to issue-only or user-story-only input unless a reviewable OpenSpec proposal, specification scenarios, and task checklist are also supplied.
- Do not report a design direction or OpenSpec change as approved while `059-design-atdd` has unresolved partial, missing, ambiguous, absent, or divergent findings.
- Do not silently add, remove, or rewrite OpenSpec proposals, acceptance criteria, specification scenarios, or tasks in response to alignment findings; apply only maintainer-approved revisions and rerun the review.
