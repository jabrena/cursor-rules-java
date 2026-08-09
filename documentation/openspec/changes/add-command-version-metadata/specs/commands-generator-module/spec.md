## MODIFIED Requirements

### Requirement: Command inventory integrity

The commands module MUST preserve the embedded command bundle declared by `commands.xml` and keep its source metadata aligned with the Maven project release.

#### Scenario: Preserve installation order and uniqueness

- **GIVEN** `commands.xml` defines the embedded command bundle
- **WHEN** command inventory tests run in `plinth-commands-generator`
- **THEN** every listed command asset exists
- **AND** command file names are unique
- **AND** installation order matches `commands.xml`

#### Scenario: Preserve per-command routing contracts

- **GIVEN** command markdown assets define purpose, usage, owning agent, and safeguards
- **WHEN** command contract tests run in `plinth-commands-generator`
- **THEN** routing and contract assertions for the embedded command bundle continue to pass after relocation

#### Scenario: Match every command version to the Maven project

- **GIVEN** `commands.xml` declares the authoritative command XML inventory
- **WHEN** command version consistency tests run
- **THEN** every inventoried command XML metadata version equals the root `pom.xml` project version
- **AND** the expected version is derived from the root POM rather than hard-coded in the test
- **AND** a mismatch identifies the offending command source
