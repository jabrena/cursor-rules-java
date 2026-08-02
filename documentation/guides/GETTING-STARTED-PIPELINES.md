# Getting started for Pipelines and AI

You could use System prompts or SKILLs in your Pipeline
to automate tasks.

## Example using Codex in GitHub Actions

Codex can turn a GitHub issue into OpenSpec planning artifacts. In this pattern,
a maintainer applies the `/create-spec` label, the workflow stores the complete
issue context in files, and `openai/codex-action` runs with write access limited
to the checked-out workspace.

```yaml
name: Create OpenSpec from an issue with Codex

on:
  issues:
    types: [labeled]

permissions:
  contents: write
  issues: write
  pull-requests: write

jobs:
  create-spec:
    if: github.event.label.name == '/create-spec'
    runs-on: ubuntu-latest
    steps:
      - name: Checkout repository
        uses: actions/checkout@v5

      - name: Read full issue context
        env:
          GH_TOKEN: ${{ github.token }}
          ISSUE_NUMBER: ${{ github.event.issue.number }}
          REPOSITORY: ${{ github.repository }}
        run: |
          mkdir -p .codex/issue
          gh api "repos/${REPOSITORY}/issues/${ISSUE_NUMBER}" > .codex/issue/issue.json
          jq -r '.title // ""' .codex/issue/issue.json > .codex/issue/title.txt
          jq -r '.body // ""' .codex/issue/issue.json > .codex/issue/body.md

      - name: Ask Codex to create OpenSpec artifacts
        uses: openai/codex-action@v1
        with:
          openai-api-key: ${{ secrets.OPENAI_API_KEY }}
          sandbox: workspace-write
          prompt-file: .codex/issue/create-openspec-prompt.md
```

The complete workflow adds several production safeguards: it treats issue text
as untrusted planning input, asks Codex to create a sanitized summary, restricts
changes to `documentation/openspec`, delegates commit and pull-request creation
to deterministic workflow steps, and reports the result back on the issue.
Configure the `OPENAI_API_KEY` repository secret before using it.

**Complete example:**
[create-spec-with-codex.yaml](../../.github/workflows/sf/create-spec-with-codex.yaml)

## Example using Cursor CLI

```bash
name: Run Cursor Agent on Demand

on:
  workflow_dispatch:

jobs:
  agent-on-demand:
    runs-on: ubuntu-latest
    timeout-minutes: 5
    permissions:
      contents: write
      pull-requests: write
    steps:
      - name: Checkout repository
        uses: actions/checkout@v6
        with:
          token: ${{ secrets.GITHUB_TOKEN }}
          fetch-depth: 0

      - name: Setup Java 25 with GraalVM
        uses: actions/setup-java@v5
        with:
          distribution: 'graalvm'
          java-version: '25'

      - name: Setup jbang
        run: |
          curl --proto '=https' -Ls https://sh.jbang.dev | bash -s - app setup
          echo "$HOME/.jbang/bin" >> $GITHUB_PATH

      - name: Install Cursor CLI
        run: |
          curl https://cursor.com/install -fsS | bash
          echo "$HOME/.cursor/bin" >> $GITHUB_PATH

      - name: Run Cursor Agent
        env:
          CURSOR_API_KEY: ${{ secrets.CURSOR_API_KEY }}
        run: |
          echo "=== User Prompt:===";
          jbang trust add https://github.com/jabrena/
          PROMPT=$(jbang pml-to-md@jabrena convert pml-hello-world-java.xml)
          echo "$PROMPT";
          echo "=== Cursor Agent Execution:===";
          echo "";
          cursor-agent -p "$PROMPT" --model auto

      - name: Create PR with changes
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          PAT_TOKEN: ${{ secrets.PAT_TOKEN }}
          GITHUB_REPOSITORY: ${{ github.repository }}
          GITHUB_ACTOR: ${{ github.actor }}
        run: |
          chmod +x .github/scripts/create-pr.sh
          .github/scripts/create-pr.sh
```

**Example:** [cursor-agent-cli-demo](../../examples/cursor-agent-cli-demo/README.md)

## Claude Code

```bash
name: Run Claude Code on Demand

on:
  workflow_dispatch:

jobs:
  agent-on-demand:
    runs-on: ubuntu-latest
    timeout-minutes: 5
    permissions:
      contents: write
      pull-requests: write
    steps:
      - name: Checkout repository
        uses: actions/checkout@v6
        with:
          token: ${{ secrets.GITHUB_TOKEN }}
          fetch-depth: 0

      - name: Setup Java 25 with GraalVM
        uses: actions/setup-java@v5
        with:
          distribution: 'graalvm'
          java-version: '25'

      - name: Setup jbang
        run: |
          curl --proto '=https' -Ls https://sh.jbang.dev | bash -s - app setup
          echo "$HOME/.jbang/bin" >> $GITHUB_PATH

      - name: Install Claude Code
        run: |
          curl -fsSL https://claude.ai/install.sh | sh
          echo "$HOME/.local/bin" >> $GITHUB_PATH

      - name: Run Claude Agent
        env:
          ANTHROPIC_API_KEY: ${{ secrets.ANTHROPIC_API_KEY }}
        run: |
          echo "=== User Prompt:===";
          jbang trust add https://github.com/jabrena/
          PROMPT=$(jbang pml-to-md@jabrena convert pml-hello-world-java.xml)
          echo "$PROMPT";
          echo "=== Cursor Agent Execution:===";
          echo "";
          $HOME/.local/bin/claude --permission-mode=acceptEdits --verbose "$PROMPT"

      - name: Create PR with changes
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          PAT_TOKEN: ${{ secrets.PAT_TOKEN }}
          GITHUB_REPOSITORY: ${{ github.repository }}
          GITHUB_ACTOR: ${{ github.actor }}
        run: |
          chmod +x .github/scripts/create-pr.sh
          .github/scripts/create-pr.sh
```
