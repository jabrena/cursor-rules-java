## Context

The current workflow starts at `Issue`, but repositories can enter that workflow in four ordinary states: both root `AGENTS.md` and OpenSpec are present, neither is present, or only one is present. OpenSpec discovery also has an unsafe fifth state when multiple directories named `openspec` exist and specification authority is ambiguous.

The repository already owns the substantive prerequisite workflows:

- `200-agents-md` creates repository guidance.
- `042-planning-openspec` verifies the OpenSpec CLI and initializes an OpenSpec project.

`/onboarding` is therefore a coordinating command. It detects repository state, protects existing artifacts, chooses whether delegation is necessary, and reports the result without duplicating either skill.

## Goals / Non-Goals

**Goals:**

- Establish root agent guidance and one unambiguous OpenSpec location before issue work.
- Preserve each existing prerequisite independently.
- Make missing-prerequisite delegation explicit and composable.
- Let the user choose a missing OpenSpec project's location while providing the repository convention `documentation/openspec` as the default.
- Stop safely before mutation when OpenSpec authority is ambiguous.
- Make a second run against an onboarded repository produce no changes.

**Non-Goals:**

- Detect whether a repository is new, mature, or already implemented.
- Generate, import, or infer a custom initial specification baseline.
- Modify an existing `AGENTS.md` or OpenSpec project.
- Merge or select among multiple existing OpenSpec projects.
- Change the behavior of `200-agents-md`, `042-planning-openspec`, or the OpenSpec CLI.
- Implement application code or select later issue work.

## Change Boundary Assessment

This is one atomic OpenSpec change. Command registration, repository preflight, the two delegated paths, inventory propagation, tests, and workflow discoverability jointly deliver one reviewable outcome: a maintainer can establish repository prerequisites before selecting an issue. Splitting by prerequisite, Maven module, test type, or documentation language would divide one user-visible workflow by technical layer rather than value, ownership, release, risk, approval, or rollback boundary.

## Decisions

### Run ambiguity discovery before any mutation

The command first checks for a root `AGENTS.md` and recursively discovers all directories whose name is exactly `openspec`. It evaluates the complete discovered set before starting either delegated workflow.

This ordering is required because a missing `AGENTS.md` combined with multiple OpenSpec projects must stop before repository content changes. The two prerequisites remain logically independent, but the ambiguity safety gate precedes mutation.

### Preserve zero, one, and many OpenSpec states explicitly

```text
/onboarding
  |
  +-- inspect <repository-root>/AGENTS.md
  |
  +-- recursively discover directories named openspec
        |
        +-- more than one -> report all paths and technical debt; stop
        +-- exactly one   -> preserve it; continue
        +-- none          -> ask for path (default documentation/openspec)
                              and delegate openspec init to 042-planning-openspec
  |
  +-- AGENTS.md missing -> delegate creation to 200-agents-md
  +-- AGENTS.md present -> preserve it
```

An exactly-one result is accepted whether the directory is at the repository root or nested. The command does not relocate it or prefer `documentation/openspec` over an existing location.

### Select a path only when OpenSpec is absent

When discovery returns zero directories, the command asks the user for the initialization path and offers `documentation/openspec` as the default. Accepting the default or supplying another path becomes input to the delegated `042-planning-openspec` workflow. The command does not ask for a path when exactly one OpenSpec project already exists.

The installed command remains `/onboarding`; path selection is an interactive step rather than a required command-line argument. This preserves the issue's exact command name while supporting user choice.

### Delegate behavior instead of copying skill prompts

The command delegates root guidance creation to `200-agents-md` and missing OpenSpec initialization to `042-planning-openspec`. It names each delegation and passes the repository root or selected initialization path as context. The delegated skills retain ownership of their interaction, validation, CLI, and safeguard details.

The command is owned by `@plinth-architect`, matching its responsibility for OpenSpec readiness and coordination of `042-planning-openspec`. This is an implementation-level ownership decision and does not transfer ownership of `AGENTS.md` generation away from `200-agents-md`.

### Do not synthesize a baseline

Onboarding does not inspect source code to classify project maturity and does not author specifications from existing implementation. Its OpenSpec effect is limited to the normal result of delegated `openspec init` at the selected path.

### Report a coherent result

On completion, the command reports the preserved or created state of root `AGENTS.md`, the preserved or initialized OpenSpec path, and any skipped delegation. On ambiguity, it reports every conflicting OpenSpec path and does not claim onboarding succeeded.

## Compatibility and Data Preservation

The command is additive. Existing command behavior remains unchanged. Existing prerequisite artifacts are read-only to `/onboarding`; when both are present, the command performs no writes and starts no delegated workflow. No migration, feature toggle, or deprecation period is needed.

The principal safety boundary is the all-path ambiguity check before delegation. Tests must prove that the missing-`AGENTS.md` path does not begin generation when several OpenSpec projects exist.

## Risks / Trade-offs

- Recursive discovery may encounter repository areas that are expensive to scan; correctness and ambiguity detection take precedence over optimizing an unspecified exclusion policy.
- Delegating two interactive skills can expose separate prompts; the command must frame them as one onboarding session without hiding either skill's safeguards.
- A user-selected path outside the repository or otherwise invalid may be rejected by the delegated OpenSpec workflow; onboarding must surface that failure rather than falling back silently.
- Selecting `@plinth-architect` as owner centralizes technical readiness, while the actual `AGENTS.md` content remains owned by a separate skill.

## Open Questions

- Whether a later design refinement should define repository areas excluded from recursive discovery, such as generated or vendor directories. No exclusion is authorized by the current requirements, so v1 specifies complete repository discovery.
- Whether future versions should accept an optional path argument in addition to interactive selection. V1 keeps the exact `/onboarding` invocation and asks only when OpenSpec is absent.
