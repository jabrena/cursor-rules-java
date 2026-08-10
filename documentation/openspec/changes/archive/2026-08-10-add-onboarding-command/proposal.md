## Why

Plinth's AI-native workflow begins with issue selection even though a repository may not yet contain the two prerequisites that guide subsequent agent work: a root `AGENTS.md` and one unambiguous OpenSpec project. Starting without those prerequisites leaves the coding harness without repository-specific instructions or later specification commands without a reliable OpenSpec location.

GitHub issue [#1112](https://github.com/jabrena/plinth/issues/1112) requests a pre-issue `/onboarding` command that establishes those prerequisites without replacing existing content. The maintainer clarified that a missing OpenSpec project is initialized at a user-selected path, defaulting to `documentation/openspec`, and that onboarding delegates to the existing OpenSpec initialization workflow rather than generating a custom specification baseline.

## What Changes

- Add an embedded `/onboarding` command before `Issue` in the documented AI-native workflow.
- Check root `AGENTS.md` and recursively discover directories named `openspec` as independent repository prerequisites.
- Preserve existing `AGENTS.md` and OpenSpec content unchanged.
- Delegate missing root guidance to `200-agents-md`.
- When no OpenSpec directory exists, ask the user to select an initialization path, default the selection to `documentation/openspec`, and delegate initialization to `042-planning-openspec` using `openspec init`.
- Accept exactly one discovered OpenSpec directory wherever it is located in the repository.
- Stop before any mutation when multiple OpenSpec directories are found, report every conflicting location, and identify the ambiguity as technical debt.
- Keep repeated execution idempotent and do not detect project maturity or create a custom specification baseline.
- Add command inventory, propagation, focused contract, Gherkin acceptance, and localized workflow-documentation coverage.

## Capabilities

### New Capabilities

- `onboarding-command`: Defines repository preflight discovery, preservation, delegation, path selection, ambiguity handling, idempotence, and command reporting.

### Modified Capabilities

- `analysis-design-lifecycle-documentation`: Places `/onboarding` before issue selection in the documented AI-native workflow and explains its prerequisite-establishment role.

## Impact

This is an additive command-bundle change. Implementation will affect authoritative command XML and inventory under `plinth-commands-generator/src/main/resources/`, command-focused tests under `plinth-commands-generator/src/test/`, generated-command propagation expectations under `plinth-skills-generator/src/test/`, command inventory documentation, and the English, Spanish, and Chinese workflow documentation that currently starts at `Issue`.

The change composes existing skills `200-agents-md` and `042-planning-openspec`; it does not change their generated source content, edit `.cursor/rules/`, refresh the public `skills/` release output, or introduce a custom baseline generator.

## Source and Derivation

| Source | Concern authority | Derivation direction |
|---|---|---|
| GitHub issue [#1112](https://github.com/jabrena/plinth/issues/1112), retrieved `2026-08-10T12:33:11Z` with a provider-reported and retrieved count of 2 accessible comments | Command name, user value, root guidance, selectable OpenSpec initialization, and original acceptance boundary | Issue -> OpenSpec change |
| [Functional Specification comment](https://github.com/jabrena/plinth/issues/1112#issuecomment-5240192317) | Repository-state matrix, preservation, recursive discovery, delegation, ambiguity handling, idempotence, workflow position, and no-custom-baseline constraint | Issue comment -> OpenSpec change |
| [Acceptance Criteria comment](https://github.com/jabrena/plinth/issues/1112#issuecomment-5240226562) | Observable onboarding scenarios and mutation-safety expectations | Issue comment -> OpenSpec scenarios |
| Maintainer clarification in the `2026-08-10` `/create-spec` session | When no OpenSpec directory exists, use a user-selected initialization path with `documentation/openspec` as the default | Maintainer decision -> OpenSpec change |
| Existing repository capabilities and command-generator conventions | Unchanged XML source ownership, generated-output boundaries, localization, and validation workflow | Repository specifications -> compatibility constraints |

Derivation is one-way into this OpenSpec change. The source issue and its comments remain unchanged.

## Conflict Resolution

The issue description originally requires an initial specification baseline when implementation already exists. Both accessible comments instead prohibit project-maturity detection and custom baseline generation, limiting onboarding to delegated `openspec init`. The maintainer's `2026-08-10` clarification selects the delegation-only behavior and supplies the missing path-selection rule. This change therefore excludes custom baseline generation and records the issue-description criterion as superseded for this OpenSpec derivation without modifying the source issue.
