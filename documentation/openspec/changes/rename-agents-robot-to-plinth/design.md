## Context

Issue [#1094](https://github.com/jabrena/plinth/issues/1094) renames all 9 embedded agent identifiers from `robot-` to `plinth-`. The issue's own Functional Specification flags one high-impact/low-confidence unknown that a technical-approach refinement pass would normally resolve: whether the maintainer intends a hard, one-time rename or a transitional period where both `robot-*` and `plinth-*` resolve, noting ADR-007 chose the compatibility-preserving option for the analogous repository-level rename.

This document does not apply design refinement skills (`051`-`057`, `121`-`123`, `130`) — those are reserved for `/explore-design`. It records how the boundary was resolved from already-approved source material and what remains open for a follow-up `/explore-design` pass if needed before implementation.

## Hard-cut resolution (from Acceptance Criteria, not a new design decision)

The maintainer-authored Acceptance Criteria comment ([issuecomment-5122514800](https://github.com/jabrena/plinth/issues/1094#issuecomment-5122514800)) is more concrete than the Functional Specification's open unknown: none of its Gherkin scenarios test for `robot-` compatibility resolution, dual-prefix routing, or an alias/shim mechanism. Its scenarios instead assert direct renaming (`Given the agent source "<robot-name>" exists ... Then the agent source is identified as "<plinth-name>"`) and that regenerated output contains **only** `plinth-`-prefixed identifiers with **no** remaining `robot-` reference.

This change treats the confirmed Acceptance Criteria as authoritative over the Functional Specification's unresolved unknown: **this is a hard-cut rename**, not a transitional/dual-prefix migration. No compatibility shim, alias, or dual-resolution mechanism is introduced.

## Scope boundary

**In scope** (active, non-archived):
- 9 agent XML sources and `agents.xml` in `plinth-agents-generator`.
- `examples/xml/robot-*.xml` under the `pml-agents-schema` OpenSpec examples.
- `<agent>robot-*</agent>` declarations in `plinth-commands-generator/src/main/resources/commands/*.xml`.
- Generated output: `.agents/skills/`, `.cursor/agents/*.md`.
- Contributor docs, README files (all three languages), `CHANGELOG.md`, blog posts, and corresponding `docs/` regeneration.
- The 7 living OpenSpec capability specs that name `robot-` agents in requirement or scenario text.

**Out of scope** (left unchanged, per ADR-007's own precedent of treating superseded references as historical record):
- `documentation/openspec/changes/archive/**` — every archived change record.
- Past `CHANGELOG.md` entries that describe already-released versions.
- The retired `robot-coordinator` name inside `analysis-design-lifecycle-documentation`'s migration-documentation requirement — it never receives a `plinth-` counterpart because it was already superseded by `robot-tech-lead` (now `plinth-tech-lead`) before this change; only the requirement's migration *target* is updated.

## Open questions carried forward (not resolved by this spec-authoring pass)

| Question | Why unresolved here | Recommendation |
|----------|---------------------|-----------------|
| Do CI workflows or `skill-check`/`cisco-ai-skill-scanner` policy configuration hardcode a literal `robot-` agent name? | Requires grepping CI/scanner config, which is an implementation-time check, not a spec-authoring input. | Grep `.github/workflows/` and any scanner policy files during implementation (task 1 in `tasks.md`); fix before promoting if found. |
| Does any downstream skill/agent consumer outside this repository invoke agents by the literal `robot-` name as a stable contract? | No evidence available from repository sources; the issue itself flags this as unconfirmed. | Check the public `skills/` release output and published guides for literal `robot-` invocation instructions before the release-profile promotion step; treat as a release gate, not a blocker for drafting this change. |

Neither question changes the hard-cut scope decision above; both are implementation-time verification steps captured in `tasks.md`.

**Resolved during the `/explore-design` pass (2026-07-29), read-only investigation, no source files changed:**

- Q1 (CI/scanner): `grep -rn "robot-" .github/workflows/` returns no matches. No `skill-scanner`/`skill-check` policy file exists in the repository (`find . -iname "*scanner*polic*"` returns nothing). **Finding: no hardcoded `robot-` dependency in CI or scanner configuration.** Task 7.1 in `tasks.md` should still run this grep at implementation time as a regression check (CI config can change between now and implementation), but no fix is currently expected.
- Q2 (downstream/public consumers): the public `skills/` release output **does** currently contain literal `robot-` invocation instructions — confirmed in `skills/002-agents-inventory/references/002-agents-inventory.md`, `skills/004-commands-installation/references/004-commands-installation.md`, and `skills/005-agents-installation/references/005-agents-installation.md` (agent table rows, `@robot-architect`-style delegation instructions, and Markdown links to `robot-*.md`). This is expected: `skills/` is release output refreshed only through the explicit `-P release` Maven profile (per `CLAUDE.md`), not part of ordinary local builds. **Clarification, not a new finding requiring a shim**: this OpenSpec change's task list (`tasks.md` section 4) only regenerates the local bridge (`.agents/skills` via `plinth-skills-generator -am`), not the public `skills/` release output. The public `skills/` registry will continue to show `robot-*` identifiers until a maintainer intentionally runs the `release` profile in a separate release-promotion step — this is normal release-cadence lag, not a compatibility shim, and does not reopen the hard-cut decision. See "New scope findings" below for the task-list implication.

## New scope findings from read-only investigation (require maintainer decision, not silently added to `tasks.md`)

Grepping the full repository (not just the paths `proposal.md` already lists) surfaced two additional tracked, in-repo hits that `proposal.md`'s Impact list does not currently mention:

1. `benchmarks/metrics-v1.schema.json:199` — a JSON Schema `description` field uses `robot-tech-lead` as an illustrative example string (forward-looking documentation text, not historical data). Recommendation: in scope, update to `plinth-tech-lead` for consistency with the rest of the rename; low risk (doc-string only, no consumers parse the example text programmatically).
2. `benchmarks/scenario4/results/*.json` (23 tracked files, e.g. `20260718T200637Z-cursor-composer.json`) — timestamped historical benchmark run records containing `"agents": ["robot-tech-lead", "robot-java-spring-boot-coder"]` as **recorded evidence of which agent identifier was actually invoked at that point in time**. Recommendation: **out of scope, leave unchanged** — same rationale as archived OpenSpec records and past `CHANGELOG.md` entries in the Scope boundary above: rewriting historical run evidence would misrepresent what was actually executed.

(Not a new finding, confirmed out of scope: `.claude/settings.local.json` also matches `robot-` in two `xmllint` permission-allowlist entries, but that file is untracked — globally gitignored via `~/.config/git/ignore` — so it is a personal machine artifact outside this repository's version control, not repository scope.)

**Maintainer approval recorded (2026-07-29):** both items are approved as scoped above. Item 1 (`benchmarks/metrics-v1.schema.json`) is now task 6.6 and listed in `proposal.md`'s Impact section. Item 2 (`benchmarks/scenario4/results/*.json`) is confirmed out of scope and documented as an expected, non-failing grep hit at task 8.2.

## Compatibility Review

- No new agent capability, role, or delegation contract is introduced or removed — only identifiers change.
- Generated installer file names change (`robot-*.md` -> `plinth-*.md` under `.agents/skills`, `.cursor/agents/`); this is the intended, in-scope outcome per the Acceptance Criteria and is not treated as an unplanned breaking change.
- Archived OpenSpec records and past CHANGELOG entries are not rewritten, matching ADR-007's own confirmation criterion for historical references.
- If the CI/scanner or downstream-consumer checks above surface a hard dependency on the literal `robot-` string, that is a new finding requiring a follow-up decision (potentially a transitional shim) — out of scope for the hard-cut this change implements unless raised before implementation starts.

## Design refinement: alternatives and trade-offs (`/explore-design` pass, 2026-07-29)

This section applies `053-design-simple-rules` to compare feasible approaches. All three options were evaluated; only Option A is compatible with the confirmed Acceptance Criteria.

| Option | Description | Passes the tests (AC scenarios) | Reveals intention | Duplication / elements | Verdict |
|---|---|---|---|---|---|
| **A. Hard-cut rename** (current direction) | Rename every active identifier in one atomic change; no `robot-` reference remains in active source or generated output. | Yes — matches Gherkin scenarios asserting direct rename and "no remaining `robot-` reference" exactly. | Yes — one identifier per agent, no ambiguity about which name is current. | Fewest elements — no alias table, no dual registration, no toggle to own or later remove. | **Selected.** |
| **B. Transitional dual-prefix / alias / compatibility shim** (mirrors ADR-007's repo-level choice) | Both `robot-*` and `plinth-*` resolve for a deprecation window (e.g. an alias map in `agents.xml`, redirect stubs in generated Markdown, or a compatibility note). | No — no AC scenario tests for dual-prefix routing or an alias mechanism; several scenarios explicitly assert *no* remaining `robot-` reference in regenerated output, which a shim would violate. | Reduces clarity — two names resolve to one agent, and contributors must track which is canonical during the window. | Adds elements: alias registry, redirect logic, a removal trigger, and tests for both paths. | **Rejected.** Contradicts confirmed Acceptance Criteria; would require re-deriving new Gherkin scenarios before it could be authorized, which is outside `/explore-design`'s scope. |
| **C. Staged rollout behind a rollout-control mechanism** (e.g. feature-toggle-gated dual registration, or a multi-PR/multi-release rollout by layer) | Ship the rename incrementally across releases, keeping `robot-*` live until a later cutover release. | No — same conflict as Option B: any interval where both names resolve, or where regenerated output still contains `robot-`, fails the "no remaining `robot-` reference" scenarios. | Reduces clarity for the same reason as B, plus adds release-sequencing complexity. | Adds elements: toggle/flag ownership, a cleanup trigger, cross-release coordination. | **Rejected**, evaluated in more detail under Feature Toggle Evaluation below. |

**Confirmed recommendation: Option A (hard-cut rename), unchanged from the prior spec-authoring pass.** The Acceptance Criteria comment is maintainer-authored (`authorAssociation: OWNER`) and more concrete than the Functional Specification's open unknown, so it controls per the existing project convention of treating confirmed Gherkin scenarios as authoritative over an earlier, less concrete unknown.

## Design refinement: sequencing and implementation slicing

**Two-step method (`051`) applicability.** This change has no distinct "behavior change" step: the nine agents' missions, routing, and delegation contracts are unchanged — only identifiers change. There is no `robot-` compatible design-preparation step that would make a later behavior change "easy," because there is no separate behavior change to make easy; the mechanical rename *is* the entire change. Two-step method's spirit still applies at a coarser grain: separate the **mechanical/generated-source layer** (XML sources, `agents.xml`, command `<agent>` declarations, PML examples, regenerated `.agents/skills`/`.cursor/agents`) from the **prose/documentation layer** (guides, READMEs, CHANGELOG, blog posts), and validate after each layer before moving to the next — which `tasks.md` sections 1-4 (mechanical) followed by section 6 (prose) already do. Recommendation: keep that ordering, and treat the end of section 4 (generated output confirmed clean) as an explicit checkpoint gate before starting section 6, rather than an implicit one — see refined task-list note below.

**Hamburger method (`052`) applicability.** Layers identified: (1) agent XML sources, (2) command `<agent>` references, (3) PML schema examples, (4) generated output/skills bridge, (5) living OpenSpec specs, (6) contributor documentation, (7) compatibility verification (CI/scanner/public-release grep), (8) closeout validation. `tasks.md` already maps 1:1 onto these layers. Applying the smallest-useful-version challenge ("if this had to ship tomorrow, what's the smallest useful version?") returns **no smaller version than the whole set**: `proposal.md`'s own Change Boundary Assessment states the outcome is atomic with "no partial or dual-naming end state," which rules out shipping only a subset of layers as an independently valuable intermediate state (that would itself be a form of Option B/C above). **Self-check conclusion: do not fragment this into separate shippable OpenSpec changes or PRs.** The layers remain useful as internal task-batch checkpoints (as `tasks.md` already structures them), not as independently deliverable vertical slices.

**Sequencing risk called out explicitly:** layer 1 (agent XML rename) and layer 2 (command `<agent>` references) must land together in the same working-tree change before any full-reactor build or release regeneration runs; an intermediate state where agent XML is renamed but command XML still declares `<agent>robot-*</agent>` would break command-to-agent routing. `tasks.md`'s existing order (section 1 before section 2, both before section 4's generated-output regeneration) already avoids this — this pass confirms rather than changes that ordering.

## Design refinement: breaking-change review (`056`)

| Surface | Classification | Notes |
|---|---|---|
| Agent XML `@id`/filenames, `agents.xml` | BREAKING (intentional, in scope) | Any tool or script that reads `plinth-agents-generator/src/main/resources/agents/robot-*.xml` by literal path breaks. Confirmed intentional per Acceptance Criteria. |
| Command `<agent>` declarations | BREAKING if not synced with agent rename in the same change | Must land atomically with the agent rename (see sequencing risk above); not breaking if both change together. |
| Generated `.agents/skills`, `.cursor/agents/*.md` (local bridge) | BREAKING (intentional, in scope) | File names change from `robot-*.md` to `plinth-*.md`; this is the confirmed, intended outcome per the Acceptance Criteria, not an unplanned break. |
| Public `skills/` release output | BREAKING, but **deferred to a future release-profile promotion**, not this change | Confirmed by grep: `skills/002-agents-inventory`, `004-commands-installation`, and `005-agents-installation` currently reference `robot-*` literally. `tasks.md` section 4 does not run `-P release`. Any external consumer that installed the public `skills/` registry and invokes agents by literal `robot-*` name will not see the rename until that separate promotion happens — this is normal release-cadence lag per `CLAUDE.md`'s local-vs-release skill generation split, not a new compatibility gap introduced by this change. |
| `documentation/openspec/changes/archive/**`, past `CHANGELOG.md` entries, `benchmarks/scenario4/results/*.json` | NON-BREAKING (historical record, unchanged) | Confirmed unchanged by design; rewriting would misrepresent historical execution evidence. |
| `benchmarks/metrics-v1.schema.json` example string | NON-BREAKING, but currently unaddressed | New scope finding above; doc-string only, no runtime consumer parses it as a contract. |
| `.claude/settings.local.json` | Out of repository scope | Untracked, globally gitignored personal file; not part of this change. |

No unresolved BREAKING classification blocks the hard-cut direction; the public-`skills/`-release row needs the maintainer decision recorded above (defer vs. include in this change's task list).

## Design refinement: feature toggle evaluation (`057`) — explicitly rejected

Classified this rename against feature-toggle applicability and rejected a toggle/flag mechanism:

- **Toggle type considered:** a migration/compatibility toggle that would let `robot-*` and `plinth-*` resolve simultaneously during a rollout window (the Option C mechanism above).
- **Why rejected:** (1) This is a build-time/source-identifier rename with no runtime request path, service boundary, or live-traffic audience to gate — there is no "evaluation context" for a toggle to inspect. (2) A toggle necessarily means both names resolve while it is on, which directly contradicts the confirmed Acceptance Criteria ("no remaining `robot-` reference" in regenerated output). (3) The existing, simpler rollback mechanism — reverting the single atomic commit/PR via git — fully covers the rollback need without introducing toggle ownership, typed decision API, observability, or a cleanup trigger to design and later retire.
- **Outcome:** No feature toggle is introduced. Rollback control is git revert of the atomic change, which is safe here because the change has no live-traffic runtime behavior to roll back mid-flight.

## Design refinement: testing-strategy recommendation (`130`, RIGHT-BICEP / CORRECT)

`plinth-agents-generator/src/test/java/info/jab/pml/AgentIndexesTest.java` already establishes the right pattern: it asserts an **absence** boundary (`.doesNotContain("`robot-coordinator`")`) for one retired name. Applying RIGHT-BICEP's "Right result" and CORRECT's "Existence" dimension to the acceptance criterion "no remaining `robot-` reference in regenerated output": **recommend extending this existing pattern** with a broader negative assertion (e.g. `.doesNotContain("robot-")` over the full generated inventory/Markdown, or an equivalent check in `plinth-skills-generator`'s integration test for `002`/`005`) so that criterion becomes an automated, repeatable (A-TRIP) regression check instead of relying solely on the manual `grep -rl "robot-"` in `tasks.md` task 8.2. This is a recommended task-list refinement (see below), not applied to test code by this design pass.

## Migration Plan (refined)

1. Rename the 9 agent XML sources and `agents.xml` entries in `plinth-agents-generator`.
2. Update `<agent>robot-*</agent>` declarations in `plinth-commands-generator` command sources — land together with step 1 in the same working-tree change (sequencing risk above).
3. Update `pml-agents-schema` OpenSpec examples (`examples/xml/robot-*.xml` -> `plinth-*.xml`).
4. Regenerate `.agents/skills` and `.cursor/agents/*.md`; confirm no `robot-` identifier remains in generated output. **Checkpoint:** treat a clean build + the recommended automated absence assertion (testing-strategy recommendation above) passing as the gate before starting step 6.
5. Update the 7 living OpenSpec capability specs listed in `proposal.md`.
6. Update contributor docs, README files (3 languages), `CHANGELOG.md`, blog posts, and regenerate `docs/`; include the `benchmarks/metrics-v1.schema.json` example-string update once the maintainer confirms it in scope (new scope finding above).
7. Grep CI workflows and scanner policy configuration for hardcoded `robot-` agent names — resolved during this design pass (no hits found); re-run at implementation time as a regression check.
8. Explicitly confirm (do not silently regenerate) whether the public `skills/` release output is refreshed via `-P release` as part of this change or deferred to a separate release-promotion step (breaking-change review above); record the maintainer's answer in `tasks.md`.
9. Run full verification and `openspec validate --all`.

## ADR candidate (identification and outline only — not created by this pass)

**Candidate: "Adopt hard-cut identifier renaming for compatibility-sensitive technical identifiers, distinct from ADR-007's repository-name compatibility approach."**

Rationale for treating this as ADR-worthy rather than folding it into this OpenSpec change's `design.md` alone:

- It is durable and precedent-setting: it establishes how *future* renames of compatibility-sensitive technical identifiers (skill IDs, command names, generated file names) should be decided in this repository, not just how this one rename was decided.
- It explicitly diverges from ADR-007's own chosen option (Option 2: compatibility-preserving, deferred technical renaming) for a technically analogous decision (identifier rename with external consumers), which ADR-007 itself anticipated needing "its own migration evidence" for.
- It is externally visible: downstream consumers of the public `skills/` registry and any repository that scripts against `robot-*` agent names are affected.

Outline (mirrors ADR-007's structure for consistency):

- **Status/date/decision-makers:** proposed; same decision-maker as ADR-007 (repository maintainer).
- **Context and Problem Statement:** ADR-007 renamed the repository to `plinth` but explicitly deferred compatibility-sensitive identifiers. Issue #1094 is the first such identifier migration (agent names); the repository needs a documented, reusable answer for whether technical identifier renames follow the same compatibility-preserving pattern as the repository name, or a different pattern.
- **Decision Drivers:** consistency of the visible `plinth-` identity; avoiding indefinite dual-naming maintenance burden; confirmed maintainer Acceptance Criteria for issue #1094; keeping generated output and documentation unambiguous; bounded blast radius (agent identifiers are internal to this repository's generated artifacts, not a public API contract with a versioning promise).
- **Considered Options:** (1) keep `robot-` indefinitely; (2) hard-cut rename, no shim (this change's approach); (3) transitional dual-prefix/alias, mirroring ADR-007's repository-level choice.
- **Decision Outcome:** Option 2 (hard-cut), sourced from the confirmed Acceptance Criteria on issue #1094; contrast explicitly with ADR-007's Option 2 (which was compatibility-preserving) to explain why the same repository took different approaches at two different layers (project identity vs. internal technical identifier).
- **Consequences:** Good — no dual-naming maintenance burden, no toggle to retire, unambiguous generated output. Bad — any external script or documentation still using literal `robot-*` breaks without a deprecation window; public `skills/` release output lags until the next release-profile promotion (see breaking-change review above).
- **Confirmation:** no active (non-archived) source, generated output, or documentation contains a `robot-` agent identifier; `openspec validate --all` passes; public `skills/` release timing is explicitly recorded, not silently deferred.
- **More Information:** Issue #1094; this OpenSpec change (`rename-agents-robot-to-plinth`); ADR-007.

This is reported as a candidate for the maintainer to approve before `@plinth-architect` creates it via `030-architecture-adr-general` / `031-architecture-adr-functional-requirements`; it is not created by this `/explore-design` pass.

## `059-design-atdd` alignment gate (2026-07-29)

Read-only goal-to-criteria-to-task traceability review across `proposal.md`, the 7 modified specs under `specs/`, and `tasks.md`.

**Initial outcome: `changes-requested`**, four unresolved findings — all partial/ambiguous gaps in task coverage, none requiring a change to the hard-cut design direction:

- Missing `xmllint` validation step for renamed `pml-agents-schema` examples.
- `analysis-design-lifecycle-documentation`'s migration-doc requirement not itemized by file in the generic documentation task bucket, risking the retired `robot-coordinator` source name being incorrectly renamed.
- Ambiguous timing for the public `skills/` release-output refresh (`-P release`) relative to this change's closeout grep.
- The two new scope findings above (`benchmarks/metrics-v1.schema.json`, `benchmarks/scenario4/results/*.json`) not yet reflected in `tasks.md`.

**Resolution (maintainer-approved 2026-07-29):** all four findings closed by refining `tasks.md`:
- Added task 3.2 (`xmllint --noout` on renamed examples).
- Expanded task 6.1 to name the specific migration-doc files and the `robot-coordinator` exception; expanded task 6.3 with the same exception for the historical `CHANGELOG.md` entry.
- Task 7.2 rewritten to record the decision explicitly: public `skills/` release refresh is deferred to a separate release-promotion step, out of scope for this change's closeout.
- Added task 6.6 (`benchmarks/metrics-v1.schema.json` update, in scope) and expanded task 8.2 to exclude `skills/` from the closeout grep and document `benchmarks/scenario4/results/*.json` and the historical `robot-coordinator` mentions as expected, non-failing hits.

**Outcome after refinement: `ready`.** Re-checking the traceability matrix against the refined `tasks.md`: every capability requirement now has explicit, evidence-backed task coverage with no remaining partial, missing, ambiguous, absent, or divergent finding. Per `/explore-design`'s workflow, this alignment result is reported for the maintainer's continued attention but does not itself constitute design approval — the explicit design-approval step is still separate and still pending.
