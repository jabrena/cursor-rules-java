# Tasks

## 1. Agent sources (plinth-agents-generator)

- [x] 1.1 Rename the 9 agent XML sources under `plinth-agents-generator/src/main/resources/agents/` from `robot-*.xml` to `plinth-*.xml`.
- [x] 1.2 Update each renamed document's `@id`, `<metadata>/<title>`, and any self-referential `robot-` mention in `<role>`/`<goal>`/other body text to the matching `plinth-` identifier.
- [x] 1.3 Update `plinth-agents-generator/src/main/resources/agents.xml` `<agent file="...">` entries to the renamed `plinth-*.xml` filenames, preserving installation order.
- [x] 1.4 Update `plinth-agents-generator/src/test/java/info/jab/pml/AgentIndexesTest.java` assertions (file names, `@id`/title expectations, routing/delegation string checks) to the `plinth-` identifiers.
- [x] 1.5 Rename the 9 Gherkin feature files under `plinth-agents-generator/src/test/resources/gherkin/agents/` from `robot-*.feature` to `plinth-*.feature` and update in-file agent references.
- [x] 1.6 Update `plinth-agents-generator/src/test/resources/gherkin/agents/acceptance-tests-prompts-agents.md` headings and `execute @...feature` paths to the renamed files.
- [x] 1.7 Run `./mvnw clean verify -pl plinth-agents-generator` and confirm no `robot-` agent identifier remains in `src/main`.

## 2. Command sources (plinth-commands-generator)

- [x] 2.1 Update every `<agent>robot-*</agent>` declaration in `plinth-commands-generator/src/main/resources/commands/*.xml` to the renamed `plinth-*` identifier (includes at least `create-spec.xml`, `explore-design.xml`, `implement-spec.xml`, `profile.xml`, `benchmark.xml`).
- [x] 2.2 Update command-focused tests and Gherkin that assert an owning-agent string.
- [x] 2.3 Run `./mvnw clean verify -pl plinth-commands-generator`.

## 3. PML schema examples (pml-agents-schema)

- [x] 3.1 Rename `examples/xml/robot-*.xml` OpenSpec schema examples to `examples/xml/plinth-*.xml` under this change, mirroring the renamed agent XML shape.
- [x] 3.2 Validate each renamed example with `xmllint --noout examples/xml/plinth-*.xml` per `CLAUDE.md`'s XML validation rule.

## 4. Generated output and skills bridge

- [x] 4.1 Run `./mvnw clean install -pl plinth-skills-generator -am` and confirm `.agents/skills/002-agents-inventory` and `.agents/skills/005-agents-installation` embed only `plinth-` agent identifiers, with no remaining `robot-` reference.
- [x] 4.2 Run `./mvnw clean install` (or the equivalent full local build) and confirm `.cursor/agents/*.md` (and any other generated installer target) list only `plinth-*.md` files.
- [x] 4.3 Per the skill acceptance prompt validation rule in `CLAUDE.md`, check `plinth-skills-generator/src/test/resources/gherkin/skills/acceptance-tests-prompts-skills.md` for skills `002-agents-inventory` and `005-agents-installation`; execute only their listed prompts and verify the acceptance tests pass.

## 5. Living OpenSpec capability specs

- [x] 5.1 `agents-generator-module`: MODIFIED requirements drafted in this change (`specs/agents-generator-module/spec.md`).
- [x] 5.2 `analysis-design-agents`: MODIFIED requirements drafted in this change (`specs/analysis-design-agents/spec.md`).
- [x] 5.3 `analysis-design-commands`: MODIFIED requirements drafted in this change (`specs/analysis-design-commands/spec.md`).
- [x] 5.4 `analysis-design-lifecycle-documentation`: MODIFIED requirements drafted in this change (`specs/analysis-design-lifecycle-documentation/spec.md`).
- [x] 5.5 `implement-spec-command`: MODIFIED requirements drafted in this change (`specs/implement-spec-command/spec.md`).
- [x] 5.6 `performance-operation-workflows`: MODIFIED requirements drafted in this change (`specs/performance-operation-workflows/spec.md`).
- [x] 5.7 `pml-agents-schema`: MODIFIED requirements drafted in this change (`specs/pml-agents-schema/spec.md`).

## 6. Documentation

- [x] 6.1 Update `documentation/guides/*` (getting-started guides, inventories) that name agents by their `robot-` identifier, including `_ES`/`_ZH` variants where they exist. This includes `GETTING-STARTED-AGENTS.md`, `GETTING-STARTED-AGENTS_ES.md`, and `GETTING-STARTED-AGENTS_ZH.md`, which document the `robot-coordinator` -> `robot-tech-lead` migration: rename only the migration *target* to `plinth-tech-lead`; leave the retired source name `robot-coordinator` unchanged, since it is historical and never receives a `plinth-` counterpart (per `specs/analysis-design-lifecycle-documentation/spec.md` and `design.md`'s Scope boundary).
- [x] 6.2 Update `README.md`, `README_ES.md`, and `README_ZH.md` agent references in the same change.
- [x] 6.3 Update `CHANGELOG.md` only for the entry documenting this rename; do not rewrite past released-version entries. `CHANGELOG.md` also mentions `robot-coordinator` in a past released-version entry — leave that historical entry unchanged, consistent with 6.1's exception.
- [x] 6.4 Update blog posts under `site-generator/content/blog/` that name agents by their `robot-` identifier, then run `./mvnw clean generate-resources -pl site-generator -P site-update` and review the resulting `docs/` diff.
- [x] 6.5 Confirm `documentation/openspec/changes/archive/**` is left unchanged (no edits expected).
- [x] 6.6 Update the illustrative `robot-tech-lead` example string in `benchmarks/metrics-v1.schema.json:199` to `plinth-tech-lead` (approved scope addition from the `/explore-design` read-only investigation in `design.md`; doc-string only, no runtime consumer parses it as a contract).

## 7. Compatibility verification (open questions from design.md)

- [x] 7.1 Grep `.github/workflows/` and any `skill-scanner`/`skill-check` policy configuration for a hardcoded literal `robot-` agent name; fix if found before promoting. (Re-run as a regression check: the `/explore-design` read-only investigation in `design.md` found no hits as of 2026-07-29.)
- [x] 7.2 The public `skills/` release output (`skills/002-agents-inventory`, `004-commands-installation`, `005-agents-installation`) currently contains literal `robot-*` references, confirmed by the `/explore-design` investigation in `design.md`. **Decision, recorded here per maintainer approval:** refreshing `skills/` via the `-P release` profile is deferred to a separate, later release-promotion step and is explicitly **not** part of this change's closeout — this is normal release-cadence lag per `CLAUDE.md`'s local-vs-release skill generation split, not a compatibility gap this change must close. Task 8.2's verification grep excludes `skills/` accordingly.

## 8. Closeout

- [x] 8.1 Run `./mvnw clean verify` (full reactor) and confirm it passes.
- [x] 8.2 Confirm no active (non-archived) source, generated output, or documentation file contains a `robot-` agent identifier: `grep -rl "robot-" --exclude-dir=archive --exclude-dir=target --exclude-dir=.git --exclude-dir=skills .` limited to agent-identifier hits (cross-check against unrelated matches such as unrelated words before treating as a failure). Expected, approved remaining hits that are **not** failures: the public `skills/` release output (excluded above; deferred per task 7.2), the historical `robot-coordinator` mentions preserved by tasks 6.1/6.3, and `benchmarks/scenario4/results/*.json` (23 timestamped historical benchmark run records recording which agent identifier was actually invoked at that point in time — out of scope, left unchanged, per the same historical-record rationale as archived OpenSpec records and past `CHANGELOG.md` entries; approved scope decision from the `/explore-design` investigation in `design.md`).
- [x] 8.3 Run `openspec validate --all` from `documentation/` and resolve any reported issues.
