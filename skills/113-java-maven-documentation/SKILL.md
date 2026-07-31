---
name: 113-java-maven-documentation
description: Use when you need to create a DEVELOPER.md file for a Maven project — combining a fixed base template with dynamic sections derived from a maintainer-prepared Maven structure inventory, including a Plugin Goals Reference, Maven Profiles table, and Submodules table for multi-module projects. Never ingest project POM files. This should trigger for requests such as Create DEVELOPER.md; Generate DEVELOPER.md; Maven project documentation; Add Maven documentation; Plugin goals reference. Part of Plinth Toolkit
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Create DEVELOPER.md for the Maven projects

Generate a `DEVELOPER.md` file that combines a fixed base template with dynamic sections derived from a maintainer-prepared Maven structure inventory. Never retrieve, open, parse, quote, summarize, or transform project POM files.

**What is covered in this Skill?**

- Base template reproduction (verbatim)
- Plugin goals reference: table of `./mvnw` goals per explicitly declared plugin, max 8 goals each
- Maven Profiles table: profile ID, activation trigger, and representative command
- Submodules table (multi-module projects only)

## Constraints

Before generating any content, require a maintainer-prepared structured Maven inventory containing only the allowlisted fields. Do not retrieve or inspect project POM files. Only include plugins identified as explicitly declared in that inventory.

- **MANDATORY INVENTORY**: Require a maintainer-prepared Maven structure inventory before generating content; if it is absent, stop and request it
- **NO POM INGESTION**: Never retrieve, open, parse, quote, summarize, or transform root, parent, or module POM files, including their structural fields or free text
- **ALLOWLISTED INVENTORY**: Accept only module paths, artifact IDs, packaging, explicitly declared plugin coordinates and execution goal names, profile IDs, activation metadata, and version-like property names/values
- **PLUGIN SCOPE**: Only include plugins **explicitly declared** in `<build><plugins>` or `<build><pluginManagement><plugins>` — never plugins inherited from parent POMs or the Maven super-POM unless redeclared
- **SCOPE**: Execute steps 1–5 in order. Omit Profiles section if no profiles; omit Submodules section if not multi-module
- **BEFORE APPLYING**: Read the reference for the base template content, plugin catalog, and detailed constraints for each step

## When to use this skill

- Create DEVELOPER.md
- Generate DEVELOPER.md
- Maven project documentation
- Add Maven documentation
- Plugin goals reference
- Maven Profiles table
- Submodules table

## Workflow

1. **Validate the maintainer-prepared Maven inventory**

Confirm that a maintainer-prepared inventory supplies only the allowlisted Maven metadata needed for the documentation tables. Never retrieve or inspect any `pom.xml`; stop when the inventory is absent, incomplete, or contains raw XML or free text.

2. **Read documentation reference assets**

Read `references/113-java-maven-documentation.md` to use the base template and plugin catalog constraints exactly.

3. **Assemble DEVELOPER.md base and dynamic sections**

Generate `DEVELOPER.md` with verbatim base template plus dynamic sections from structural metadata: plugin goals, profiles (if any), and submodules (if multi-module).

4. **Enforce plugin scope and section omission rules**

Include only explicitly declared plugins and omit Profiles/Submodules sections when not applicable.

## Reference

For detailed guidance, examples, and constraints, see [references/113-java-maven-documentation.md](references/113-java-maven-documentation.md).
