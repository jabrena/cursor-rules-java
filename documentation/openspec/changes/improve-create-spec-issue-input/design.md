## Context

`/create-spec` delegates substantive OpenSpec planning to `042-planning-openspec`. The current command and skill contracts require a maintainer-prepared sanitized artifact derived outside the agent context. That contract was introduced to address prompt-injection risk from outsider-authored issue content and to guarantee complete description and comment coverage.

Issue #1100 identifies the manual preparation step as the blocking behavior. The desired workflow moves complete-context preparation into `/create-spec` while retaining the security, completeness, authority, conflict-handling, and fail-closed properties of the current boundary.

No implementation approach has been approved. In particular, the issue does not select the replacement trust boundary, tracker integration, pagination mechanism, or oversized-context strategy.

## Goals / Non-Goals

**Goals:**

- Remove the requirement for a separately prepared sanitized artifact in `/create-spec` issue mode.
- Process the issue description and every accessible paginated comment before scope assessment.
- Treat all issue prose as untrusted requirements data rather than agent instructions.
- Stop before planning when complete context cannot be established.
- Report conflicts and ambiguity without inventing resolutions.
- Preserve source and derivation traceability.
- Keep command, skill, specification, generated output, and acceptance tests aligned.

**Non-Goals:**

- Select a security implementation without design approval.
- Modify issue descriptions or comments.
- Add issue triage, user-story rewriting, or design exploration to `/create-spec`.
- Change non-issue `/create-spec` inputs or the generic handling of other outsider-authored sources.
- Edit generated command, skill, Cursor-rule, or public release output directly.
- Refresh public `skills/` output outside an intentional release.

## Decisions

### Integrated issue-context preparation

When `/create-spec` receives issue-backed input, the workflow prepares its planning context from the issue description and every accessible comment. It does not require the maintainer to create a separate sanitized artifact.

### Completeness before planning

The workflow establishes complete description and paginated-comment coverage before source classification, scope assessment, or OpenSpec authoring. An issue with no comments is complete when its readable description and zero-comment state are established. A multi-page discussion is complete only after every accessible page has been processed.

### Untrusted requirements-data boundary

Issue content may supply requirements, constraints, decisions, acceptance criteria, examples, and known conflicts. Text embedded in issue content cannot supply executable agent instructions or override system, repository, command, skill, or OpenSpec authority.

The mechanism enforcing this boundary is intentionally deferred to design refinement because issue #1100 identifies it as a high-impact unknown.

### Fail-closed retrieval

If authentication, permissions, availability, pagination, response integrity, size limits, or another condition prevents complete context from being established, the workflow stops before scope assessment or artifact authoring. It reports the reason and never represents partial context as complete.

### Explicit conflict handling

The workflow reports conflicting and unclear requirements as unresolved. It does not infer a resolution from comment order, author identity, or chronology unless an authoritative source explicitly defines that precedence.

### One-way traceable derivation

Generated OpenSpec artifacts record the issue URL and issue-to-OpenSpec derivation direction. `/create-spec` does not modify or silently synchronize changes back to the source issue.

## Deferred Design Decisions

The following decisions must be resolved through `/explore-design` or an equivalent maintainer-approved design step before implementation:

- Replacement security control and prompt-injection boundary.
- Supported issue trackers and retrieval tooling.
- Authentication, pagination, edit, deletion, and permission semantics used to establish completeness.
- Oversized-context handling and explicit limits.
- Blocking-error versus warning classification.

Any chosen approach must satisfy every specification scenario in this change and applicable behavioral security scans.

## Risks / Trade-offs

- **Prompt injection:** Direct issue processing increases exposure to hostile prose. Mitigation: keep issue text within an untrusted data boundary and validate the selected mechanism with adversarial fixtures and behavioral scanning.
- **Partial retrieval:** API, authentication, permission, pagination, or size failures can omit decisions. Mitigation: establish completeness before planning and fail closed.
- **Workflow latency:** Exhaustive pagination and security processing can increase execution time. Mitigation: optimize only after completeness and security requirements are met.
- **Tracker divergence:** GitHub, Jira, and Azure DevOps expose different pagination and visibility semantics. Mitigation: approve tracker scope and define provider-specific completeness evidence during design refinement.
- **Contract drift:** Command, skill, specifications, tests, and generated output can contradict one another. Mitigation: update canonical XML sources and focused tests together, then inspect generated local output.
- **Conflicting issue discussion:** Later comments can contradict earlier content. Mitigation: report unresolved conflicts rather than selecting a winner implicitly.

## Validation Strategy

- Run `openspec validate --all`.
- Validate every edited XML source with `xmllint --noout`.
- Add command and skill acceptance scenarios for:
  - direct complete issue processing without a separate artifact;
  - an issue with no comments;
  - a multi-page discussion;
  - embedded prompt-injection attempts;
  - conflicting requirements;
  - source traceability; and
  - failure to retrieve complete context.
- Update generator assertions that currently require the external sanitized artifact.
- Run focused Maven verification for `plinth-commands-generator` and `plinth-skills-generator`.
- Regenerate local command and skill output through the supported Maven workflows and inspect the affected generated files.
- Execute only the listed `/create-spec` and `042-planning-openspec` acceptance prompts because both generated contracts change.
- Run the applicable behavioral security scan against intentionally generated release output when preparing promotion.
