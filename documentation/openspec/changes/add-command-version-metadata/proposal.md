## Why

Command definitions cannot currently declare authors and previously could not propagate release versions, leaving their provenance model inconsistent with PML 0.9.0, agents, and skills.

## What Changes

- Add PML-style `authors/author` and required version metadata to every authoritative command XML document.
- Validate command authors and versions through `commands.xsd` using the existing global-element vocabulary style.
- Render XML authors and version into generated command YAML frontmatter under `metadata.author` and `metadata.version`, matching agent and skill output conventions.
- Extend schema, frontmatter, generation, and propagation tests to cover provenance metadata.
- Enforce that every inventoried command and agent XML version matches the root Maven project version.
- Preserve command behavior, inventory order, and Markdown bodies.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `pml-commands-schema`: Support command author metadata, require version metadata, and preserve both through XML-to-Markdown generation.
- `commands-generator-module`: Verify every inventoried command source uses the root Maven project version.
- `agents-generator-module`: Verify every inventoried agent source uses the root Maven project version.

## Impact

The change affects `plinth-commands-generator` command XML, XSD, XSLT, and tests, `plinth-agents-generator` tests, plus the command assets bridged into locally generated skills. It adds backward-compatible YAML provenance metadata and no dependencies. Existing source documents must add the required version element; authors remain structurally optional for PML and agent parity, while all repository-owned commands declare them.

Source authority: the maintainer requests in the 2026-08-09 working session approve command parity with agent and skill provenance support; PML 0.9.0 at `https://jabrena.github.io/pml/schemas/0.9.0/pml.xsd` is authoritative for the `authors/author` structure; the existing `documentation/openspec/specs/pml-commands-schema/spec.md` remains authoritative for unchanged command behavior. Derivation is one-way from these sources into this change.
