## 1. Direct issue-backed create-spec workflow

- [ ] 1.1 Refine and obtain approval for the replacement trust boundary, supported tracker scope, completeness evidence, oversized-context behavior, and failure classification.
- [ ] 1.2 Update the canonical `/create-spec` command XML to prepare complete issue context without requiring a separately prepared sanitized artifact.
- [ ] 1.3 Update the `042-planning-openspec` skill index and reference XML to preserve the untrusted requirements-data boundary, exhaustive pagination, fail-closed retrieval, conflict handling, and traceability.
- [ ] 1.4 Replace command and skill generator assertions that enforce external sanitization with assertions for the approved direct issue-context contract.
- [ ] 1.5 Update the `/create-spec` and `042-planning-openspec` Gherkin sources for no-comment, paginated, prompt-injection, conflict, traceability, and incomplete-retrieval scenarios.
- [ ] 1.6 Validate all edited XML sources with `xmllint --noout`.
- [ ] 1.7 Run focused Maven verification for `plinth-commands-generator` and `plinth-skills-generator`.
- [ ] 1.8 Regenerate supported local command and skill output without editing generated files directly or refreshing public `skills/`.
- [ ] 1.9 Execute the listed `/create-spec` and `042-planning-openspec` acceptance prompts and record any skipped prompt with its reason.
- [ ] 1.10 Run applicable behavioral prompt-injection scanning against intentional release output before promotion.
- [ ] 1.11 Validate the final OpenSpec state with `openspec validate --all` and review source traceability, unresolved conflicts, and compatibility assumptions.
