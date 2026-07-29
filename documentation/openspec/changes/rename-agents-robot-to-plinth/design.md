## Context

Issue [#1094](https://github.com/jabrena/plinth/issues/1094) renames all 9 embedded agent identifiers from `robot-` to `plinth-`. The issue's own Functional Specification flags one high-impact/low-confidence unknown that a technical-approach refinement pass would normally resolve: whether the maintainer intends a hard, one-time rename or a transitional period where both `robot-*` and `plinth-*` resolve, noting ADR-007 chose the compatibility-preserving option for the analogous repository-level rename.

This document does not apply design refinement skills (`051`-`057`, `121`-`123`, `130`) — those are reserved for `/explore-design`. It records how the boundary was resolved from already-approved source material and what remains open for a follow-up `/explore-design` pass if needed before implementation.

## Hard-cut resolution (from Acceptance Criteria, not a new design decision)

The maintainer-authored Acceptance Criteria comment ([issuecomment-5122514800](https://github.com/jabrena/plinth/issues/1094#issuecomment-5122514800)) is more concrete than the Functional Specification's open unknown: none of its Gherkin scenarios test for `robot-` compatibility resolution, dual-prefix routing, or an alias/shim mechanism. Its scenarios instead assert direct renaming (`Given the agent source "<robot-name>" exists ... Then the agent source is identified as "<plinth-name>"`) and that regenerated output contains **only** `plinth-`-prefixed identifiers with **no** remaining `robot-` reference.

This change treats the confirmed Acceptance Criteria as authoritative over the Functional Specification's unresolved unknown: **this is a hard-cut rename**, not a transitional/dual-prefix migration. No compatibility shim, alias, or dual-resolution mechanism is introduced.

## Scope boundary

**In scope** (active, non-archived):
- 9 agent XML sources and `agents.xml` in `plinth-agents-generator`.
- `examples/xml/robot-*.xml` under the `pml-agents-schema` OpenSpec examples.
- `<agent>robot-*</agent>` declarations in `plinth-commands-generator/src/main/resources/commands/*.xml`.
- Generated output: `.agents/skills/`, `.cursor/agents/*.md`.
- Contributor docs, README files (all three languages), `CHANGELOG.md`, blog posts, and corresponding `docs/` regeneration.
- The 7 living OpenSpec capability specs that name `robot-` agents in requirement or scenario text.

**Out of scope** (left unchanged, per ADR-007's own precedent of treating superseded references as historical record):
- `documentation/openspec/changes/archive/**` — every archived change record.
- Past `CHANGELOG.md` entries that describe already-released versions.
- The retired `robot-coordinator` name inside `analysis-design-lifecycle-documentation`'s migration-documentation requirement — it never receives a `plinth-` counterpart because it was already superseded by `robot-tech-lead` (now `plinth-tech-lead`) before this change; only the requirement's migration *target* is updated.

## Open questions carried forward (not resolved by this spec-authoring pass)

| Question | Why unresolved here | Recommendation |
|----------|---------------------|-----------------|
| Do CI workflows or `skill-check`/`cisco-ai-skill-scanner` policy configuration hardcode a literal `robot-` agent name? | Requires grepping CI/scanner config, which is an implementation-time check, not a spec-authoring input. | Grep `.github/workflows/` and any scanner policy files during implementation (task 1 in `tasks.md`); fix before promoting if found. |
| Does any downstream skill/agent consumer outside this repository invoke agents by the literal `robot-` name as a stable contract? | No evidence available from repository sources; the issue itself flags this as unconfirmed. | Check the public `skills/` release output and published guides for literal `robot-` invocation instructions before the release-profile promotion step; treat as a release gate, not a blocker for drafting this change. |

Neither question changes the hard-cut scope decision above; both are implementation-time verification steps captured in `tasks.md`.

## Compatibility Review

- No new agent capability, role, or delegation contract is introduced or removed — only identifiers change.
- Generated installer file names change (`robot-*.md` -> `plinth-*.md` under `.agents/skills`, `.cursor/agents/`); this is the intended, in-scope outcome per the Acceptance Criteria and is not treated as an unplanned breaking change.
- Archived OpenSpec records and past CHANGELOG entries are not rewritten, matching ADR-007's own confirmation criterion for historical references.
- If the CI/scanner or downstream-consumer checks above surface a hard dependency on the literal `robot-` string, that is a new finding requiring a follow-up decision (potentially a transitional shim) — out of scope for the hard-cut this change implements unless raised before implementation starts.

## Migration Plan

1. Rename the 9 agent XML sources and `agents.xml` entries in `plinth-agents-generator`.
2. Update `<agent>robot-*</agent>` declarations in `plinth-commands-generator` command sources.
3. Update `pml-agents-schema` OpenSpec examples (`examples/xml/robot-*.xml` -> `plinth-*.xml`).
4. Regenerate `.agents/skills` and `.cursor/agents/*.md`; confirm no `robot-` identifier remains in generated output.
5. Update the 7 living OpenSpec capability specs listed in `proposal.md`.
6. Update contributor docs, README files (3 languages), `CHANGELOG.md`, blog posts, and regenerate `docs/`.
7. Grep CI workflows and scanner policy configuration for hardcoded `robot-` agent names (open question above); fix if found.
8. Run full verification and `openspec validate --all`.
