## Why

GitHub issue [#1094](https://github.com/jabrena/plinth/issues/1094) requests renaming every embedded agent identifier from the legacy `robot-` prefix to the `plinth-` prefix (for example `robot-architect` -> `plinth-architect`).

[ADR-007](../../adr/ADR-007-rename-repository-from-cursor-rules-java-to-plinth.md) already renamed the repository itself to `plinth` and explicitly deferred "compatibility-sensitive identifiers" to a later, separately evidenced migration. Commits `42fd7330` and `3d749308` (both 2026-07-13) already applied the `plinth-*` pattern to the Maven parent artifact and the generator modules. Agent identifiers are the remaining `robot-` naming surface: all 9 agent sources in `plinth-agents-generator/src/main/resources/agents/`, the `agents.xml` inventory, generated output (`.agents/skills`, `.cursor/agents/*.md`), command definitions that declare an owning agent (`plinth-commands-generator/src/main/resources/commands/*.xml`), skills documentation, contributor guides, README files, `CHANGELOG.md`, blog posts, and 7 living OpenSpec capability specs.

Two prior OpenSpec artifacts on the issue (maintainer-authored, `authorAssociation: OWNER`) already establish the requirements baseline:
- A Functional Specification comment ([issuecomment-5122495620](https://github.com/jabrena/plinth/issues/1094#issuecomment-5122495620)) covering problem framing, root cause, assumptions, context mapping, and quality-attribute discovery.
- An Acceptance Criteria comment ([issuecomment-5122514800](https://github.com/jabrena/plinth/issues/1094#issuecomment-5122514800)) with confirmed Gherkin scenarios.

## What Changes

- Rename the 9 agent XML sources under `plinth-agents-generator/src/main/resources/agents/` from `robot-*.xml` to `plinth-*.xml`, updating each document's `id`, `title`, and any self-referential text to the `plinth-` identifier.
- Update `plinth-agents-generator/src/main/resources/agents.xml` to list the renamed `plinth-*.xml` files in the existing installation order.
- Update `examples/xml/robot-*.xml` OpenSpec schema examples referenced by the `pml-agents-schema` capability to the `plinth-` naming.
- Update every command definition in `plinth-commands-generator/src/main/resources/commands/*.xml` that declares `<agent>robot-*</agent>` to reference the renamed `plinth-*` identifier.
- Regenerate `.agents/skills` and `.cursor/agents/*.md` (and other generated installer targets) so no `robot-` agent identifier remains in active generated output.
- Update contributor documentation (`documentation/guides/*`, `README.md`, `README_ES.md`, `README_ZH.md`), `CHANGELOG.md`, and blog posts under `site-generator/content/blog/` that name agents by their `robot-` identifier; regenerate `docs/` where sourced from `site-generator/`.
- Update the 7 living OpenSpec capability specs that reference `robot-` agent identifiers in requirement or scenario text (see Capabilities below) to the `plinth-` naming.
- Leave archived OpenSpec change records under `documentation/openspec/changes/archive/` unchanged as historical record.

## Capabilities

### Modified Capabilities

- `agents-generator-module`: Agent bundle requirements (module registration, inventory integrity, per-agent contracts) reference `plinth-` agent identifiers instead of `robot-`; the bundle still contains exactly 9 agents.
- `analysis-design-agents`: Business-analyst, architect, tech-lead, and non-Java agent missions, routing, and delegation requirements are restated with `plinth-` identifiers.
- `analysis-design-commands`: Command routing requirements that name `@robot-architect` are restated as `@plinth-architect`.
- `analysis-design-lifecycle-documentation`: The agent-migration documentation requirement is restated so the migration target is `plinth-tech-lead`; the retired source name `robot-coordinator` remains as historical record since it never receives a `plinth-` counterpart.
- `implement-spec-command`: The `/implement-spec` delegation requirement is restated to route through `@plinth-tech-lead`.
- `performance-operation-workflows`: `/profile` and `/benchmark` routing requirements and the coder delegation boundary are restated with `@plinth-java-performance` and the renamed coder agents.
- `pml-agents-schema`: XML source path and example requirements are restated for `plinth-*.xml` filenames while preserving the schema shape itself.

No new capability is introduced; this is a naming migration across existing capabilities.

## Source and Derivation

- Source artifact: GitHub issue [#1094](https://github.com/jabrena/plinth/issues/1094) (User Story + INVEST validation).
- Source artifact: Functional Specification comment on #1094 ([issuecomment-5122495620](https://github.com/jabrena/plinth/issues/1094#issuecomment-5122495620)).
- Source artifact: Acceptance Criteria comment on #1094 ([issuecomment-5122514800](https://github.com/jabrena/plinth/issues/1094#issuecomment-5122514800)).
- Related precedent: [ADR-007](../../adr/ADR-007-rename-repository-from-cursor-rules-java-to-plinth.md) (repository rename to `plinth`, decision-makers and stakeholder groups referenced by the issue's Functional Specification).
- Related precedent: commits `42fd7330` (parent Maven artifact rename) and `3d749308` (generator module rename), both 2026-07-13.
- Existing specifications updated by this change: `agents-generator-module`, `analysis-design-agents`, `analysis-design-commands`, `analysis-design-lifecycle-documentation`, `implement-spec-command`, `performance-operation-workflows`, `pml-agents-schema`.
- Current implementation anchors:
  - `plinth-agents-generator/src/main/resources/agents/robot-*.xml` (9 files)
  - `plinth-agents-generator/src/main/resources/agents.xml`
  - `plinth-commands-generator/src/main/resources/commands/*.xml` (`<agent>robot-*</agent>` declarations)
  - `.agents/skills/`, `.cursor/agents/*.md` (generated output)
  - `documentation/guides/`, `README.md`, `README_ES.md`, `README_ZH.md`, `CHANGELOG.md`, `site-generator/content/blog/`
- Derivation direction: issue #1094 (User Story + Functional Specification + Acceptance Criteria, all maintainer-authored) -> this OpenSpec change's proposal/design/specs/tasks -> implementation across `plinth-agents-generator`, `plinth-commands-generator`, generated output, and documentation.

## Change Boundary Assessment

This is one OpenSpec change because the requested outcome is atomic: every agent identifier in the repository uses the `plinth-` prefix, with no partial or dual-naming end state. The issue's own INVEST validation scopes it to "a rename-only change with no new agent capability" comparable in size to the prior `42fd7330` and `3d749308` rename commits.

The change does not include: any new agent capability, role, or delegation contract change; rewriting historical CHANGELOG entries predating this change; or rewriting archived OpenSpec change records under `documentation/openspec/changes/archive/`, which remain accurate historical record per ADR-007's own precedent.

## Impact

- `plinth-agents-generator` (agent XML sources, `agents.xml`, generated Markdown/skills bridge).
- `plinth-commands-generator` (command `<agent>` declarations referencing renamed agents).
- Generated output: `.agents/skills/`, `.cursor/agents/*.md`, and any other installer target produced from the agent bundle.
- Documentation: `documentation/guides/*`, `README.md`/`README_ES.md`/`README_ZH.md`, `CHANGELOG.md`, `site-generator/content/blog/` (with corresponding `docs/` regeneration).
- OpenSpec: 7 living capability specs listed above; `documentation/openspec/changes/archive/` is explicitly out of scope and unchanged.
- `benchmarks/metrics-v1.schema.json`: an illustrative `robot-tech-lead` example string, updated to `plinth-tech-lead` (scope addition confirmed during the `/explore-design` pass, 2026-07-29).
- Explicitly out of scope: the public `skills/` release output (deferred to a later `-P release` promotion, not this change) and `benchmarks/scenario4/results/*.json` (23 historical benchmark run records, left unchanged as historical evidence).
