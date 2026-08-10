Feature: Validate changes from usage of embedded commands inventory skill

Background:
  Given the skill "001-commands-inventory"
  And the inventory sandbox folder "examples/skills/inventory" has no git changes

@acceptance-test
Scenario: Generate embedded commands inventory with every command asset
  Given the local generated skill path ".agents/skills/001-commands-inventory"
  And the command assets directory "assets/commands"
  And the requested inventory output path is "examples/skills/inventory/INVENTORY-COMMANDS-JAVA.md"
  And any existing report at the requested output path must be overwritten
  When the skill ".agents/skills/001-commands-inventory" is applied to generate the embedded commands inventory
  Then the skill reads "references/001-commands-inventory.md"
  And the skill reads "assets/java-commands-inventory-template.md"
  And the generated inventory file exists at "examples/skills/inventory/INVENTORY-COMMANDS-JAVA.md"
  And the generated inventory file includes the heading "# Embedded Commands Inventory"
  And the generated inventory file includes the section "## Embedded commands"
  And the generated inventory file includes the section "## Installation target options"
  And the generated inventory file includes the installation targets ".github/commands", ".claude/commands", ".cursor/command", and ".codex/commands"
  And the generated inventory file includes exactly one row for each embedded command asset:
    | assetFile                | command                  |
    | onboarding.md            | /onboarding              |
    | update-issue.md          | /update-issue            |
    | explore-problem.md       | /explore-problem         |
    | create-acceptance-criteria.md | /create-acceptance-criteria |
    | create-feature-branch.md | /create-feature-branch   |
    | create-worktree.md       | /create-worktree         |
    | explore-design.md        | /explore-design          |
    | create-adr.md            | /create-adr              |
    | create-diagram.md        | /create-diagram          |
    | create-spec.md           | /create-spec             |
    | implement-spec.md       | /implement-spec         |
    | close-spec.md           | /close-spec             |
    | profile.md               | /profile                 |
    | benchmark.md             | /benchmark               |
  And every command row in the generated file corresponds to a same-named source file in "assets/commands"
  And no command asset from "assets/commands" is missing from the generated inventory
  And the generated inventory file does not include command rows outside the embedded command assets directory
  And any git changes produced under "examples/skills/inventory" during skill execution and verification are reset

@integration-test
Scenario: Skill follows the generator registration and local-output workflow
  Given skill content must be maintained through the generator pipeline
  When the embedded commands inventory skill is implemented
  Then the source changes are made under "plinth-skills-generator/src/main/resources"
  And "plinth-skills-generator/src/main/resources/skills.xml" registers skill id "001" with the commands inventory reference
  And the generated local skill output includes ".agents/skills/001-commands-inventory/SKILL.md"
  And the generated local skill output includes ".agents/skills/001-commands-inventory/assets/java-commands-inventory-template.md"
  And generated references contain no unresolved include markers or broken local reference paths
  And generated release output under "skills/" is not edited directly
  And applicable XML and skill generation validations can be executed before promotion
