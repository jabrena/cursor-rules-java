---
description: 'Archive a completed OpenSpec change by name.'
argument-hint: '[openspec-change]'
model: 'inherit'
agent: 'plinth-architect'
tools:
  - 'Read'
  - 'Bash'
---

# close-spec

Archive an OpenSpec change by name, using the OpenSpec CLI, so completed changes can be reconciled and removed from the active change list.

## Usage

```text
/close-spec <change-name>
```

## Inputs

- `<change-name>`: the OpenSpec change name to archive (for example `add-close-spec-command`)

## Owner

`@plinth-architect`

## Workflow

1. Validate the argument is present.
   - If missing, print usage: `/close-spec <change-name>` and stop.
2. Verify the OpenSpec CLI is available (for example, `openspec --version`).
   - If OpenSpec cannot be executed, report the missing prerequisite and provide a short hint to install/enable OpenSpec.
3. Verify the change exists before attempting to archive it.
   - Prefer checking `openspec list` and matching `<change-name>`, or
   - Use `openspec show <change-name>` and treat “not found” as an unknown-change error.
   - If the change is unknown, report that the change was not found and suggest running `openspec list`.
4. From the `documentation/` working directory, execute the equivalent of:
   - `openspec archive <change-name>`
5. If the archive command fails, surface the OpenSpec CLI output and stop (do not claim success).
6. If the archive command succeeds, report a success message that includes `<change-name>`.

## Safeguards

- Do not guess or auto-correct the change name.
- Do not claim success when `openspec archive` fails; surface the CLI output.
- Do not run `openspec archive` unless the change exists.
