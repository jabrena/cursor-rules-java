## 1. Command Contract and Inventory

- [ ] 1.1 Add the authoritative `/onboarding` command XML under `plinth-commands-generator/src/main/resources/commands/`, owned by `@plinth-architect` and associated with `200-agents-md` and `042-planning-openspec`.
- [ ] 1.2 Implement complete preflight ordering: check root `AGENTS.md`, recursively discover every directory named `openspec`, and stop before all mutation when discovery returns more than one location.
- [ ] 1.3 Implement independent preservation and delegation behavior for all missing/present prerequisite combinations, including idempotent no-op behavior when both prerequisites exist.
- [ ] 1.4 Implement missing-OpenSpec path selection with `documentation/openspec` as the default and pass the selected path to delegated `042-planning-openspec` initialization without custom baseline generation.
- [ ] 1.5 Register `/onboarding` in `plinth-commands-generator/src/main/resources/commands.xml` before issue-oriented commands and update the authoritative command inventory template and guide.
- [ ] 1.6 Validate every edited command and inventory XML file with `xmllint --noout`.

## 2. Focused Command and Propagation Tests

- [ ] 2.1 Extend command inventory and contract tests for `/onboarding` ownership, skill delegation, workflow position, preservation, path default, ambiguity stop, idempotence, and no-custom-baseline safeguards.
- [ ] 2.2 Add `plinth-commands-generator/src/test/resources/gherkin/commands/onboarding.feature` covering both prerequisites missing, each mixed state, both present, one nested OpenSpec directory, multiple OpenSpec directories, default and custom initialization paths, delegated failure, and an implemented repository.
- [ ] 2.3 Register the new command feature in `plinth-commands-generator/src/test/resources/gherkin/commands/acceptance-tests-prompts-commands.md` using the existing `execute @...feature` format.
- [ ] 2.4 Update `001-commands-inventory.feature`, `004-commands-installation.feature`, and affected command-to-skill propagation assertions so generated local skills include `onboarding.md` in inventory order.

## 3. Workflow Documentation

- [ ] 3.1 Update `README.md`, `README_ES.md`, and `README_ZH.md` so `/onboarding` appears before `Issue` and its prerequisite outcomes and default path are discoverable.
- [ ] 3.2 Update affected English getting-started and command-inventory guides and keep their existing `_ES.md` and `_ZH.md` counterparts synchronized.
- [ ] 3.3 Inspect every changed local Markdown link and confirm generated website output is untouched unless a `site-generator` source is intentionally changed.

## 4. Validation

- [ ] 4.1 Run `./mvnw clean verify -pl plinth-commands-generator`.
- [ ] 4.2 Run `./mvnw clean install -pl plinth-skills-generator -am` and inspect `.agents/skills/001-commands-inventory` and `.agents/skills/004-commands-installation` for the generated onboarding entry and embedded asset.
- [ ] 4.3 Execute only the listed acceptance prompts for changed generated skills `001-commands-inventory` and `004-commands-installation`, recording any skipped prompt with its reason.
- [ ] 4.4 Execute the listed `onboarding.feature` command acceptance prompt and record any unavailable interactive or filesystem-fixture environment explicitly.
- [ ] 4.5 Run `jbang markdown-validator/src/main/java/info/jab/mv/MarkdownValidator.java .` and resolve applicable Markdown failures.
- [ ] 4.6 Run `openspec validate --all` from `documentation/` and resolve all validation errors before implementation promotion.
