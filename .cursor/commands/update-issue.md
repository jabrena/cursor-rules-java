---
description: 'Update an issue description with structured, evidence-backed content.'
argument-hint: '[issue-url]'
model: 'inherit'
agent: 'plinth-business-analyst'
tools:
  - 'Read'
  - 'Bash'
---

# update-issue

Update an existing project issue description with structured, evidence-backed content.

## Usage

```text
/update-issue <issue> [<source>] [<tracker>]
```

## Accepted Inputs

- Existing GitHub or Jira issue number, key, or URL
- Optional source notes, supporting discussion, user story, acceptance criteria, or file path
- Optional tracker selection: GitHub or Jira

## Owning Agent

`@plinth-business-analyst`

## Associated Skills

- `043-planning-github-issues`
- `044-planning-jira`
- `045-planning-azure-devops`
- `014-agile-user-story` when user-story refinement is required

## Workflow

1. Load the current issue description and relevant discussion before drafting changes.
2. Confirm the requested update scope and the source material authority.
3. Use `014-agile-user-story` when the update needs user-story, acceptance-criteria, or Gherkin-style structure.
4. Draft the updated issue body without inventing requirements.
5. Present the proposed body before overwriting the existing issue description.
6. Update the issue in the selected tracker after approval.
7. Report the issue identifier and URL.

## Output

- Updated structured issue description
- Acceptance criteria or user-story scenario when applicable
- Updated GitHub/Jira issue reference

## Safeguards

- Do not invent requirements, acceptance criteria, or comments.
- Do not expose tracker credentials or tokens.
- Do not overwrite an issue body without showing the proposed replacement.
- Preserve relevant existing issue content unless the user explicitly asks to remove it.
