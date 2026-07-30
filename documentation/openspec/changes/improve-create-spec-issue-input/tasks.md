## 1. Approved design and behavior-preserving preparation

- [x] 1.1 Approve direct read-only issue retrieval, accessible-snapshot completeness, fail-closed oversized handling, untrusted requirements-data boundaries, and blocking retrieval failures.
- [x] 1.2 Add or refine characterization assertions for retained `/create-spec` behavior: non-issue inputs, source authority, scope assessment, no source mutation, and OpenSpec scaffolding.
- [x] 1.3 Separate sanitization-specific assertions from retained completeness and authority assertions without changing generated command or skill behavior.
- [x] 1.4 Run focused command-generator and skills-generator tests to prove the preparatory test refactoring preserves current behavior.

## 2. Test-driven behavior change

- [x] 2.1 Write failing command and skill tests for direct issue reading without a mandatory artifact, optional trusted-artifact combinations, accessible zero-comment state, exhaustive pagination and count checks, embedded-instruction rejection, conflicts, traceability, and incomplete-context failure.
- [x] 2.2 Update the canonical `/create-spec` command XML to use available authenticated read-only tooling, prepare the complete accessible issue snapshot, and stop on any completeness uncertainty.
- [x] 2.3 Update the `042-planning-openspec` skill index and reference XML with the approved direct-read exception while leaving non-`/create-spec` outsider-source handling unchanged.
- [x] 2.4 Make the focused tests pass with the smallest XML contract changes, then refactor duplicated wording only when command and skill intent remains explicit.

## 3. Verification and promotion

- [x] 3.1 Validate every edited XML source with `xmllint --noout`.
- [x] 3.2 Run `./mvnw clean verify -pl plinth-commands-generator` and `./mvnw clean verify -pl plinth-skills-generator -am`.
- [x] 3.3 Regenerate supported local output through Maven, inspect generated command output and `.agents/skills/042-planning-openspec`, and do not edit generated files or refresh public `skills/` directly.
- [x] 3.4 Execute the listed `/create-spec` and `042-planning-openspec` acceptance prompts; record any skipped prompt with its reason.
  - Skipped both interactive prompt executions because this environment has no shell or CI acceptance-prompt runner; the matching inventory entries and updated Gherkin contracts were inspected directly.
- [x] 3.5 Run `skill-check` and applicable behavioral scanners against generated local `.agents/skills` output; accept only the repository-reviewed low/medium W011 posture and block higher-severity or different findings.
- [x] 3.6 Run `openspec validate --all`, the repository Markdown validator, and final source-traceability and compatibility review.
