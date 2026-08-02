# 内置 Commands 清单

## 目标

提供本仓库中可安装的内置 commands 快速检查清单。

## 内置 commands

| Command | SDLC 阶段 | 主要用途 |
| --- | --- | --- |
| `/update-issue` | 分析 / 设计 | 使用结构化 user story、验收标准和资源内容更新现有 GitHub 或 Jira issue。 |
| `/explore-problem` | 分析 / 设计 | 从五个视角评估 issue，并在 issue 中发布功能规格评论。 |
| `/create-acceptance-criteria` | 分析 / 设计 | 根据功能规格评论生成 Gherkin 验收标准，并将其作为单独的 issue 评论发布。 |
| `/create-feature-branch` | 分析 / 设计到实现 | 为基于仓库的分析、设计或实现创建并切换到按约定命名的分支。 |
| `/create-worktree` | 分析 / 设计到实现 | 为并行工作创建隔离分支和关联 worktree。 |
| `/create-adr` | 设计 | 记录架构决策、备选方案、理由和后果。 |
| `/create-diagram` | 设计 | 根据已批准的工件创建聚焦的架构或设计图。 |
| `/create-spec` | 分析 / 设计 | 创建或更新一个或多个已验证的 OpenSpec change。 |
| `/explore-design` | 设计 | 比较技术方案并获得批准的设计方向。 |
| `/implement-spec` | 实现 | 通过框架感知的委派交付已批准的计划或已验证的 OpenSpec 任务列表。 |
| `/profile` | 运维 | 协调 Java profiling，从基线检测到验证优化。 |
| `/benchmark` | 运维 | 选择并协调 JMeter、Gatling 或 JMH 性能 workflow。 |
| `/close-spec` | 运维 / 维护 | 使用 OpenSpec CLI 按名称归档 OpenSpec change。 |

## 安装目标选项

- `.github/commands`
- `.claude/commands`
- `.cursor/command`
- `.codex/commands`
