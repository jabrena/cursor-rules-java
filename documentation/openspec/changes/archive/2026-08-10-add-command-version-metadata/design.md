## Context

PML 0.9.0 defines optional `metadata/authors` containing one or more `author` strings followed by optional `version`. The local agent schema matches that author structure, all nine agent XML sources declare authors, and agent and skill XSLT pipelines emit provenance in generated YAML frontmatter. Command metadata originally contained only operational fields; version support was added in this active change, but author support remains absent.

This is one atomic generator change because schema representation, source migration, rendering, validation, and bridged local assets share one release and rollback boundary.

## Goals / Non-Goals

**Goals:**

- Give every authoritative command XML source explicit author and release-version metadata.
- Validate PML-style repeatable authors and version metadata and propagate them to generated Markdown.
- Match the nested `metadata.author` and `metadata.version` convention already used by agents and skills.
- Preserve all existing command fields, bodies, routing, and tool ordering.

**Non-Goals:**

- Add licenses or titles to command metadata.
- Change command inventory structure or installation behavior.
- Refresh the public `skills/` release output.
- Define semantic-version syntax in XSD beyond the existing string approach used by agents and skills.

## Decisions

### Require version in authoritative command sources

Add a global string `version` element and require it first in the command metadata sequence. Although agent and skill schemas allow version to be absent for general PML compatibility, this repository's inventoried command sources form a release set and must not silently omit provenance.

Alternative considered: make version optional. Rejected because all production commands move together and optionality would permit incomplete release metadata.

### Support PML 0.9.0 authors and populate repository commands

Add optional `authors` before `version`, containing one or more repeatable `author` strings, matching PML 0.9.0 and `agents.xsd`. Populate every repository-owned command with `Juan Antonio Breña Moral`, matching current agent and skill ownership.

Alternative considered: require authors at the XSD level. Rejected because PML 0.9.0 and the agent schema keep the collection optional for reusable schema compatibility; inventory-wide rendering tests still ensure production sources declare and propagate it.

### Render authors and version as nested YAML metadata

Emit `metadata.author` as a comma-separated scalar in XML order and `metadata.version` after the existing command frontmatter fields. This mirrors generated agent and skill files while keeping command-specific fields unchanged.

Alternative considered: emit a top-level `version`. Rejected because it would diverge from the established agent and skill convention requested by the maintainer.

### Keep version a schema string

Use `xs:string`, consistent with agents and skills, so snapshot identifiers such as `0.19.0-SNAPSHOT` remain valid and future version policy is not duplicated in three schemas.

Alternative considered: add a semantic-version restriction. Rejected because neither peer schema enforces one and snapshot versions require additional policy.

### Verify source-to-output equality rather than hard-code a release

Tests will verify the PML-style author structure, require the version schema element, compare generated `metadata.author` and `metadata.version` to their XML sources, and continue verifying every inventoried command. This catches propagation errors without coupling tests to one release number.

## Risks / Trade-offs

- Additional YAML metadata may be ignored by command consumers → keep existing supported keys and bodies unchanged; treat authors and version as additive provenance.
- XSD sequence changes make old command XML invalid → migrate all inventoried sources atomically with the schema.
- A future version bump could miss one source → inventory-wide schema/frontmatter tests and source searches expose drift.

## Migration Plan

1. Update the OpenSpec delta and command metadata contract.
2. Add the XSD declarations and migrate all inventoried command XML sources.
3. Update XSLT rendering and tests.
4. Run XML validation and the integrated Maven build to regenerate `.agents/skills`.

Rollback is atomic: revert the schema, source metadata, XSLT, tests, and generated local output together.

## Open Questions

None.
