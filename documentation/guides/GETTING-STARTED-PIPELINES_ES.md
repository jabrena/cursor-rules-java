# Primeros pasos con Pipelines e IA

Puedes usar System prompts o SKILLs en tu Pipeline para automatizar tareas.

## Ejemplo usando Codex en GitHub Actions

Codex puede convertir una issue de GitHub en artefactos de planificación de
OpenSpec. En este patrón, un mantenedor aplica la etiqueta `/create-spec`, el
workflow guarda el contexto completo de la issue en archivos y
`openai/codex-action` se ejecuta con acceso de escritura limitado al workspace
del repositorio.

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

El workflow completo incorpora varias medidas para un uso en producción: trata
el texto de la issue como entrada de planificación no confiable, pide a Codex
que cree un resumen saneado, limita los cambios a `documentation/openspec`,
delega el commit y la creación de la pull request a pasos deterministas del
workflow e informa del resultado en la issue. Configura el secreto de
repositorio `OPENAI_API_KEY` antes de usarlo.

**Ejemplo completo:**
[create-spec-with-codex.yaml](../../.github/workflows/sf/create-spec-with-codex.yaml)

## Ejemplo usando Cursor CLI

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

**Ejemplo:** [cursor-agent-cli-demo](../../examples/cursor-agent-cli-demo/README.md)

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
