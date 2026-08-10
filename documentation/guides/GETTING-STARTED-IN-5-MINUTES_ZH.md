# 5 分钟快速入门

> **语言：** [English](./GETTING-STARTED-IN-5-MINUTES.md) · [Español](./GETTING-STARTED-IN-5-MINUTES_ES.md)

这条路径为首次使用者提供一个小而实用的起点，方便你在阅读完整项目文档之前先动手尝试。

## 1. 如果需要，先安装 Node.js

Skills.sh CLI 通过 `npx` 运行，因此你需要可用的 Node.js：

```bash
node --version
npx --version
```

如果其中任一命令不可用，请使用你的操作系统包管理器安装 Node.js。

## 2. 选择你的 agent 并下载 Skills

从本项目的 [Skills.sh registry 条目](https://skills.sh/jabrena/plinth) 将所有 skills 安装到你最常用的 agent：

```bash
# Cursor
npx skills add jabrena/plinth --skill '*' --agent cursor -y

# Claude Code
npx skills add jabrena/plinth --skill '*' --agent claude-code -y

# Codex
npx skills add jabrena/plinth --skill '*' --agent codex -y

# GitHub Copilot
npx skills add jabrena/plinth --skill '*' --agent github-copilot -y
```

如果要安装到所有受支持的 agent，请使用 CLI 的全 agent 模式：

```bash
npx skills add jabrena/plinth --skill '*' --agent '*' -y
```

如果想先查看可用 skills：

```bash
npx skills add jabrena/plinth --list
```

## 3. 安装项目 commands

安装 skills 后，告诉你的 agent 你使用的 AI agent harness 工具：`cursor`、`claude-code`、`codex` 或 `github-copilot`。

```text
install @004-commands-installation cursor
install @004-commands-installation claude-code
install @004-commands-installation codex
install @004-commands-installation github-copilot
```

安装器会把项目内嵌 commands 复制到你的工具对应的 command 目录。它支持 Cursor、Claude、Codex 和 GitHub Copilot 的 command 目标目录，并且会在写入文件前询问确认。

常见的 command 目标目录包括：

- `.cursor/commands`
- `.claude/commands`
- `.codex/commands`
- `.github/commands`

## 4. 当工具支持时安装项目 agents

Commands 和 skills 已足够开始使用。如果你还想使用预定义的 robot agents，请告诉你的 agent 你使用的 AI agent harness 工具：`cursor`、`claude-code`、`codex` 或 `github-copilot`。

```text
install @005-agents-installation cursor
install @005-agents-installation claude-code
install @005-agents-installation codex
install @005-agents-installation github-copilot
```

常见的 agent 目标目录包括：

- `.cursor/agents`
- `.claude/agents`
- `.codex/agents`
- `.github/agents`

## 5. 使用 workflow

本项目围绕四个构建块组织 AI 辅助的 Java 工作：

- `Commands` 是入口点，例如 `/onboarding`、`/update-issue`、`/create-spec` 和 `/implement-spec`。
- `Agents` 定义职责，例如业务分析、架构、技术领导、Java 实现或性能工作。
- `Skills` 提供面向 Java、框架、测试、文档、安全和可观测性的具体实践。
- `MCP Servers` 在可用时把 agents 连接到外部工具和项目上下文。

一个常见路径是：

```text
/onboarding
  |
  v
Issue
  |
  v
/update-issue --> /explore-problem --> /create-acceptance-criteria
  |
  v
/create-spec --> /explore-design
  |
  v
/implement-spec --> /close-spec
```

首先运行 `/onboarding`，建立根目录 `AGENTS.md` 和一个无歧义的 OpenSpec 项目。已有先决条件会被保留。若缺少 OpenSpec，请选择生成路径或接受默认值 `documentation/openspec`。

对于只涉及文档或规划的工作，你可以在 issue、plan、specification、ADR 或 diagram 完成后停止。

## 深入了解

- [Commands 清单](./INVENTORY-COMMANDS-JAVA_ZH.md)
- [Agents 快速入门](./GETTING-STARTED-AGENTS_ZH.md)
- [Skills 快速入门](./GETTING-STARTED-SKILLS_ZH.md)
- [Pipelines 快速入门](./GETTING-STARTED-PIPELINES_ZH.md)
