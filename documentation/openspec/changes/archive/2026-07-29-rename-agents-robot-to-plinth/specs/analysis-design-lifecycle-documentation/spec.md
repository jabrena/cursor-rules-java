## MODIFIED Requirements

### Requirement: Agent migration documentation

The project MUST explain migration from `robot-coordinator` to `plinth-tech-lead` in every affected language and inventory.

#### Scenario: Existing user upgrades the agent bundle

- **WHEN** an existing user reads the updated agent guidance
- **THEN** the documentation identifies the renamed file and direct mention
- **AND** it preserves the existing coder delegation model

## Source and Derivation

- Source artifact: GitHub issue [#1094](https://github.com/jabrena/plinth/issues/1094).
- Derivation direction: issue #1094 -> `rename-agents-robot-to-plinth` requirements -> updated migration-documentation target name.
- Note: `robot-coordinator` remains unchanged as the historical, already-retired source name; it never receives a `plinth-` counterpart. Only the migration target (`robot-tech-lead` -> `plinth-tech-lead`) is updated.
