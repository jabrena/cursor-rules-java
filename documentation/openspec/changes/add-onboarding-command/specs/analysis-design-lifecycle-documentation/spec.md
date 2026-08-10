## ADDED Requirements

### Requirement: Present onboarding before issue selection

The English, Spanish, and Chinese AI-native workflow documentation SHALL present `/onboarding` before `Issue` and explain that it establishes repository guidance and one unambiguous OpenSpec location before issue-driven work begins.

#### Scenario: User reads the AI-native workflow

- **WHEN** a project user reads the README or getting-started workflow sequence
- **THEN** `/onboarding` appears before `Issue`
- **AND** the guidance identifies root `AGENTS.md` and one unambiguous OpenSpec project as the onboarding outcomes
- **AND** it states that a missing OpenSpec project uses a user-selected path with `documentation/openspec` as the default
- **AND** localized English, Spanish, and Chinese guidance remains aligned
