# Tasks

## 1. Agent sources (plinth-agents-generator)

- [ ] 1.1 Rename the 9 agent XML sources under `plinth-agents-generator/src/main/resources/agents/` from `robot-*.xml` to `plinth-*.xml`.
- [ ] 1.2 Update each renamed document's `@id`, `<metadata>/<title>`, and any self-referential `robot-` mention in `<role>`/`<goal>`/other body text to the matching `plinth-` identifier.
- [ ] 1.3 Update `plinth-agents-generator/src/main/resources/agents.xml` `<agent file="...">` entries to the renamed `plinth-*.xml` filenames, preserving installation order.
- [ ] 1.4 Update `plinth-agents-generator/src/test/java/info/jab/pml/AgentIndexesTest.java` assertions (file names, `@id`/title expectations, routing/delegation string checks) to the `plinth-` identifiers.
- [ ] 1.5 Rename the 9 Gherkin feature files under `plinth-agents-generator/src/test/resources/gherkin/agents/` from `robot-*.feature` to `plinth-*.feature` and update in-file agent references.
- [ ] 1.6 Update `plinth-agents-generator/src/test/resources/gherkin/agents/acceptance-tests-prompts-agents.md` headings and `execute @...feature` paths to the renamed files.
- [ ] 1.7 Run `./mvnw clean verify -pl plinth-agents-generator` and confirm no `robot-` agent identifier remains in `src/main`.

## 2. Command sources (plinth-commands-generator)

- [ ] 2.1 Update every `<agent>robot-*</agent>` declaration in `plinth-commands-generator/src/main/resources/commands/*.xml` to the renamed `plinth-*` identifier (includes at least `create-spec.xml`, `explore-design.xml`, `implement-spec.xml`, `profile.xml`, `benchmark.xml`).
- [ ] 2.2 Update command-focused tests and Gherkin that assert an owning-agent string.
- [ ] 2.3 Run `./mvnw clean verify -pl plinth-commands-generator`.

## 3. PML schema examples (pml-agents-schema)

- [ ] 3.1 Rename `examples/xml/robot-*.xml` OpenSpec schema examples to `examples/xml/plinth-*.xml` under this change, mirroring the renamed agent XML shape.

## 4. Generated output and skills bridge

- [ ] 4.1 Run `./mvnw clean install -pl plinth-skills-generator -am` and confirm `.agents/skills/002-agents-inventory` and `.agents/skills/005-agents-installation` embed only `plinth-` agent identifiers, with no remaining `robot-` reference.
- [ ] 4.2 Run `./mvnw clean install` (or the equivalent full local build) and confirm `.cursor/agents/*.md` (and any other generated installer target) list only `plinth-*.md` files.
- [ ] 4.3 Per the skill acceptance prompt validation rule in `CLAUDE.md`, check `plinth-skills-generator/src/test/resources/gherkin/skills/acceptance-tests-prompts-skills.md` for skills `002-agents-inventory` and `005-agents-installation`; execute only their listed prompts and verify the acceptance tests pass.

## 5. Living OpenSpec capability specs

- [x] 5.1 `agents-generator-module`: MODIFIED requirements drafted in this change (`specs/agents-generator-module/spec.md`).
- [x] 5.2 `analysis-design-agents`: MODIFIED requirements drafted in this change (`specs/analysis-design-agents/spec.md`).
- [x] 5.3 `analysis-design-commands`: MODIFIED requirements drafted in this change (`specs/analysis-design-commands/spec.md`).
- [x] 5.4 `analysis-design-lifecycle-documentation`: MODIFIED requirements drafted in this change (`specs/analysis-design-lifecycle-documentation/spec.md`).
- [x] 5.5 `implement-spec-command`: MODIFIED requirements drafted in this change (`specs/implement-spec-command/spec.md`).
- [x] 5.6 `performance-operation-workflows`: MODIFIED requirements drafted in this change (`specs/performance-operation-workflows/spec.md`).
- [x] 5.7 `pml-agents-schema`: MODIFIED requirements drafted in this change (`specs/pml-agents-schema/spec.md`).

## 6. Documentation

- [ ] 6.1 Update `documentation/guides/*` (getting-started guides, inventories) that name agents by their `robot-` identifier, including `_ES`/`_ZH` variants where they exist.
- [ ] 6.2 Update `README.md`, `README_ES.md`, and `README_ZH.md` agent references in the same change.
- [ ] 6.3 Update `CHANGELOG.md` only for the entry documenting this rename; do not rewrite past released-version entries.
- [ ] 6.4 Update blog posts under `site-generator/content/blog/` that name agents by their `robot-` identifier, then run `./mvnw clean generate-resources -pl site-generator -P site-update` and review the resulting `docs/` diff.
- [ ] 6.5 Confirm `documentation/openspec/changes/archive/**` is left unchanged (no edits expected).

## 7. Compatibility verification (open questions from design.md)

- [ ] 7.1 Grep `.github/workflows/` and any `skill-scanner`/`skill-check` policy configuration for a hardcoded literal `robot-` agent name; fix if found before promoting.
- [ ] 7.2 Check the public `skills/` release output and published guides for any instruction that tells consumers to invoke agents by the literal `robot-` name; fix if found before the `release` profile promotion.

## 8. Closeout

- [ ] 8.1 Run `./mvnw clean verify` (full reactor) and confirm it passes.
- [ ] 8.2 Confirm no active (non-archived) source, generated output, or documentation file contains a `robot-` agent identifier: `grep -rl "robot-" --exclude-dir=archive --exclude-dir=target --exclude-dir=.git .` limited to agent-identifier hits (cross-check against unrelated matches such as unrelated words before treating as a failure).
- [ ] 8.3 Run `openspec validate --all` from `documentation/` and resolve any reported issues.
