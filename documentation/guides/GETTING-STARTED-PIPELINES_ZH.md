# Pipelines 与 AI 快速入门

你可以在 Pipeline 中使用 System prompts 或 SKILLs 来自动化任务。

## 在 GitHub Actions 中使用 Codex 的示例

Codex 可以将 GitHub issue 转换为 OpenSpec 规划产物。在此模式中，维护者添加
`/create-spec` 标签，工作流将完整的 issue 上下文保存到文件中，然后以仅限当前
仓库 workspace 的写入权限运行 `openai/codex-action`。

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

完整工作流还加入了多项生产环境防护：将 issue 文本视为不可信的规划输入，要求
Codex 先生成净化后的摘要，将变更限制在 `documentation/openspec`，由确定性的工作流
步骤负责提交和创建 pull request，并将结果反馈到 issue。使用前请配置仓库密钥
`OPENAI_API_KEY`。

**完整示例：**
[create-spec-with-codex.yaml](../../.github/workflows/sf/create-spec-with-codex.yaml)

## 使用 Cursor CLI 的示例

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

**示例：** [cursor-agent-cli-demo](../../examples/cursor-agent-cli-demo/README.md)

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
