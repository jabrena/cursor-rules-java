Feature: Validate onboarding command

Background:
  Given the command prompt file ".cursor/commands/onboarding.md"
  And the command is owned by "@plinth-architect"
  And repository fixtures are isolated and reset after each scenario

@acceptance-test
Scenario: Create both missing prerequisites in safe order
  Given root "AGENTS.md" is absent
  And no directory named "openspec" exists
  When the maintainer invokes "/onboarding"
  And accepts the default result path "documentation/openspec"
  Then the command completes recursive OpenSpec discovery before mutation
  And delegates initialization to "042-planning-openspec" with result path "documentation/openspec" and project root "documentation"
  And waits for OpenSpec initialization to succeed before delegating root guidance to "200-agents-md"
  And the two interactive delegations are not run concurrently
  And the final report identifies OpenSpec as initialized and root "AGENTS.md" as created

@acceptance-test
Scenario: Preserve existing root guidance while initializing missing OpenSpec
  Given root "AGENTS.md" exists with known content
  And no directory named "openspec" exists
  When the maintainer invokes "/onboarding"
  And selects result path "documentation/openspec"
  Then root "AGENTS.md" remains byte-for-byte unchanged
  And no "200-agents-md" workflow is started
  And OpenSpec initialization is delegated with project root "documentation"

@acceptance-test
Scenario: Preserve one nested OpenSpec project while creating root guidance
  Given root "AGENTS.md" is absent
  And exactly one OpenSpec directory exists at "architecture/openspec"
  When the maintainer invokes "/onboarding"
  Then the existing OpenSpec directory and its contents remain unchanged
  And no OpenSpec result path is requested
  And no "042-planning-openspec" workflow is started
  And root guidance creation is delegated to "200-agents-md"

@acceptance-test
Scenario: Do nothing when both prerequisites already exist
  Given root "AGENTS.md" exists
  And exactly one OpenSpec directory exists at "documentation/openspec"
  When the maintainer invokes "/onboarding"
  Then no repository content changes
  And no delegated workflow is started
  And the command reports the existing OpenSpec location

@acceptance-test
Scenario: Accept exactly one OpenSpec directory at repository root
  Given root "AGENTS.md" exists
  And exactly one OpenSpec directory exists at "openspec"
  When the maintainer invokes "/onboarding"
  Then the OpenSpec directory is preserved unchanged
  And the command reports "openspec" as the existing location

@acceptance-test
Scenario: Stop before mutation when OpenSpec discovery is ambiguous
  Given root "AGENTS.md" is absent
  And OpenSpec directories exist at "documentation/openspec" and "architecture/openspec"
  When the maintainer invokes "/onboarding"
  Then every conflicting location is reported
  And the ambiguity is identified as technical debt
  And no repository content changes
  And neither "200-agents-md" nor "042-planning-openspec" is started

@acceptance-test
Scenario: Use a custom OpenSpec result path
  Given no directory named "openspec" exists
  When the maintainer invokes "/onboarding"
  And selects result path "architecture/openspec"
  Then initialization is delegated with project root "architecture"
  And one OpenSpec project is created at "architecture/openspec"
  And no OpenSpec project is created at "documentation/openspec"

@acceptance-test
Scenario Outline: Reject an invalid OpenSpec result path before mutation
  Given no directory named "openspec" exists
  When the maintainer invokes "/onboarding"
  And selects invalid result path "<path>"
  Then the command reports "<reason>"
  And no delegated workflow is started
  And no repository content changes
  And the command asks for a valid repository-relative OpenSpec directory or explicit cancellation

  Examples:
    | path                        | reason                                      |
    | /tmp/openspec               | absolute paths are not allowed              |
    | ../outside/openspec         | the path escapes the repository             |
    | documentation/specification | the final segment must be exactly openspec  |

@acceptance-test
Scenario: Report OpenSpec initialization failure without fallback
  Given root "AGENTS.md" is absent
  And no directory named "openspec" exists
  When delegated initialization at "documentation/openspec" fails
  Then no alternate path is selected
  And no "200-agents-md" workflow is started
  And the command reports both prerequisites as incomplete
  And onboarding is not reported as successful

@acceptance-test
Scenario: Preserve partial completion when root guidance creation fails
  Given root "AGENTS.md" is absent
  And no directory named "openspec" exists
  When delegated OpenSpec initialization succeeds at "documentation/openspec"
  And delegated "200-agents-md" creation fails or is cancelled
  Then "documentation/openspec" remains unchanged
  And root guidance is reported as incomplete
  And onboarding is reported as partially complete
  And successful OpenSpec work is not rolled back

@acceptance-test
Scenario: Retry only root guidance after partial completion
  Given a previous attempt initialized exactly one project at "documentation/openspec"
  And root "AGENTS.md" remains absent
  When the maintainer invokes "/onboarding" again
  Then the existing OpenSpec project is preserved
  And no OpenSpec initialization workflow is started
  And root guidance creation is delegated to "200-agents-md"

@acceptance-test
Scenario: Cancel an invalid-path retry without mutation
  Given root "AGENTS.md" is absent
  And no directory named "openspec" exists
  When the maintainer selects an invalid path and explicitly cancels
  Then neither delegated workflow is started
  And no repository content changes
  And onboarding is reported as cancelled

@acceptance-test
Scenario: Onboard an implemented repository without synthesizing specifications
  Given the repository contains implementation files
  And no directory named "openspec" exists
  When the maintainer completes "/onboarding"
  Then the only OpenSpec effect is delegated standard "openspec init" at the selected path
  And the command does not detect project maturity
  And no custom specification baseline is created
  And no issue is selected or implemented
