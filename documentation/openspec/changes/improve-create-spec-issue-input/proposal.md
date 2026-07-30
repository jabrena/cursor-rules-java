## Why

`/create-spec` currently stops on issue-backed input unless a maintainer first prepares a separate sanitized context artifact. That prerequisite prevents maintainers from creating an OpenSpec change directly from the complete issue context and duplicates work that the command workflow should perform.

Issue #1100 requires the workflow to remove the separate artifact-preparation step without weakening complete paginated-context coverage, prompt-injection resistance, failure transparency, conflict handling, or source traceability.

## What Changes

- Allow `/create-spec` to prepare planning context directly from an issue description and every accessible paginated comment without requiring a separately prepared sanitized artifact.
- Require complete issue retrieval and verification before scope assessment or OpenSpec authoring, including issues with no comments and discussions spanning multiple pages.
- Treat issue descriptions and comments only as untrusted requirements data that cannot override system, repository, command, skill, or OpenSpec instructions.
- Stop explicitly when authentication, permissions, availability, pagination, size, or another retrieval condition prevents the workflow from establishing complete issue context.
- Report conflicting or unclear requirements without inventing a resolution.
- Preserve the source issue and the issue-to-OpenSpec derivation direction without modifying the issue.
- Align the command, planning skill, normative specifications, generated output, and acceptance-test contracts.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `analysis-design-commands`: Changes the `/create-spec` issue-backed command contract from mandatory external sanitization to direct, complete, fail-closed issue-context preparation.
- `composable-planning-artifacts`: Changes `042-planning-openspec` issue mode to consume complete issue-backed context through `/create-spec` without requiring a separate maintainer-prepared artifact.

## Source and Derivation

- Authoritative source: [GitHub issue #1100](https://github.com/jabrena/plinth/issues/1100), including its description and two comments.
- Issue authority:
  - The issue description defines the user story and the requirement to remove the separate artifact prerequisite.
  - The first comment supplies problem framing, root-cause analysis, assumptions, context mapping, quality attributes, constraints, and validation considerations.
  - The second comment supplies the detailed acceptance scenarios.
- Supporting repository evidence:
  - `plinth-commands-generator/src/main/resources/commands/create-spec.xml`
  - `plinth-skills-generator/src/main/resources/skill-indexes/042-skill.xml`
  - `plinth-skills-generator/src/main/resources/skill-references/042-planning-openspec.xml`
  - `documentation/openspec/specs/analysis-design-commands/spec.md`
  - `documentation/openspec/specs/composable-planning-artifacts/spec.md`
  - `documentation/openspec/changes/archive/2026-07-22-read-complete-issue-context-in-create-spec/`
- Derivation direction: issue #1100 description and complete comment thread → this OpenSpec change → command and skill XML sources, tests, and generated local output.
- Synchronization boundary: this change does not modify issue #1100 or silently propagate later OpenSpec edits back to GitHub.

## Scope Assessment

This is one reviewable change. The command, planning skill, specifications, tests, and generated output describe one atomic workflow outcome and do not have independent value, release, ownership, deployment, or rollback boundaries.

## Impact

Maintainers will be able to invoke `/create-spec` with issue-backed input without manually preparing an intermediate sanitized artifact. The workflow must still establish that the issue description and every accessible comment were processed before planning begins.

Implementation will affect XML sources and tests in `plinth-commands-generator` and `plinth-skills-generator`. Generated `.claude/commands`, `.cursor`, `.agents`, and public `skills/` content remains generated output and must not be edited directly. Public `skills/` is refreshed only through an intentional release workflow.

Non-issue `/create-spec` inputs and generic outsider-authored sources outside this issue-backed command path remain unchanged.

## Approved Design Direction

- `/create-spec` directly reads one supplied issue through available authenticated, read-only tracker tooling.
- Complete context is the current accessible provider snapshot: the description, the provider-reported zero-comment state or every accessible paginated comment, and a count cross-check when the provider exposes a total.
- Deleted or permission-hidden historical content is outside the accessible snapshot unless the provider signals an omission or count mismatch; a signaled gap makes completeness unavailable.
- Issue text is untrusted requirements data. Embedded commands, links, code, or instructions are never executed or used to initiate tool actions.
- Authentication, permission, availability, pagination, count, response-integrity, truncation, and size failures block scope assessment and authoring rather than becoming warnings.
- Oversized context is not silently summarized or truncated. The workflow stops unless every accessible item can be processed.
- Sanitized repository artifacts remain valid optional planning inputs, but `/create-spec` does not require one as a prerequisite for issue-backed input.

## ADR Candidate

The repository already uses a maintainer-approved direct-read model for deep single-issue analysis in `/explore-problem`. A repository-level ADR should record when commands may diverge from tracker skills' default no-raw-ingestion posture, the required untrusted-data boundary, and the accepted low/medium W011 scanner posture. The ADR is recommended follow-up and is not required to implement this command-scoped change.
