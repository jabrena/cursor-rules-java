# Java 命令、智能体与技能

<a href="https://trendshift.io/repositories/15013" target="_blank"><img src="https://trendshift.io/api/badge/repositories/15013" alt="jabrena%2Fcursor-rules-java | Trendshift" style="width: 250px; height: 55px;" width="250" height="55"/></a>

[![Stargazers over time](https://starchart.cc/jabrena/plinth.svg?variant=adaptive)](https://starchart.cc/jabrena/plinth)

[![CI Builds](https://github.com/jabrena/plinth/actions/workflows/maven.yaml/badge.svg)](https://github.com/jabrena/plinth/actions/workflows/maven.yaml)

> **语言：** [English](./README.md) · [Español](./README_ES.md)
>
> **帮助项目成长：** [成为赞助者](https://github.com/sponsors/jabrena)

## 目标

一套带有明确观点的 AI 原生工作流，通过可复用的 `Skills`、`Agents`、`Commands` 与 `MCP servers`，持续演进现代 Java 企业级 `SDLC` 实践。

## 什么是 Plinth？

> `plinth` 指艺术和雕塑中用于支撑雕像或艺术品的坚实基座或平台。它曾是柱子、雕像和整座神庙台基的结构性与象征性基础。罗马人从希腊建筑中继承了这一理念，并进一步扩展其用途，以强调纪念性、等级秩序和帝国权力。

## 项目概览

- 13 Commands
- 9 Agents
- 125 Skills
- ~226 开发小时

## 最新动态

访问 https://jabrena.github.io/plinth/ 探索最新发布的内容，并通过 [CHANGELOG](./CHANGELOG.md) 了解新 skills、改进和修复如何推动项目持续演进。

## 60 秒开始使用

为你常用的智能体安装全部 skills：

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

### 查看实际效果

向你的智能体提出：

```text
使用 @110-java-maven-best-practices 审查位于 examples/@maven/maven-demo 的 Maven 项目。
解释发现的问题，应用已批准的改进，并验证构建。
```

![](documentation/images/herdr-example.png)

该 skill 会引导智能体完成结构化的 Maven 审查，同时由你决定是否接受建议的变更。

## 5 分钟上手指南

按照快速指南 [5 分钟快速入门](./documentation/guides/GETTING-STARTED-IN-5-MINUTES_ZH.md) 学习如何使用本项目。

### 从旧版规则迁移

当前的 `System prompts/rules` 已弃用，并将在 `v0.18.0` 中移除。如果你仍在使用它们，请阅读 [0.14.0 版本文章](https://jabrena.github.io/plinth/blog/2026/04/release-0.14.0.html)。

## 选择你的路径

Commands 通过把工作路由到合适的 agent 与 skill 集合来组合完整工作流：

```text
Analysis & Design
  /update-issue
    @robot-business-analyst
      @043-planning-github-issues
      @044-planning-jira
      @045-planning-azure-devops
      @014-agile-user-story
  /explore-problem
    @robot-business-analyst
      @021-problem-framing
      @022-root-cause-analysis
      @023-assumption-analysis
      @024-context-mapping
      @025-quality-attribute-discovery
  /create-adr
    @robot-architect
      @030-architecture-adr-general
      @031-architecture-adr-functional-requirements
      @032-architecture-adr-non-functional-requirements
  /create-diagram
    @robot-architect
      @033-architecture-diagrams

  /create-spec
    @robot-architect
      @042-planning-openspec
  /explore-design
    @robot-architect
      @051-design-two-steps-methods
      @052-design-hamburger-method
      @053-design-simple-rules
      @054-design-tdd
      @055-design-parallel-change
      @056-design-avoid-breaking-changes
      @057-design-feature-toggles
      @059-design-atdd
      @121-java-object-oriented-design
      @122-java-type-design
      @123-java-design-patterns
      @130-java-testing-strategies

Build
  /implement-spec
      @robot-tech-lead
      /create-feature-branch
      /create-worktree
      @robot-java-coder
      @robot-java-spring-boot-coder
      @robot-java-quarkus-coder
      @robot-java-micronaut-coder
      @robot-no-java

  MCP Servers
    Jbang-Quarkus-JDBC
    MongoDB
    JavaDocs
    Serena-LSP

Operate
  /profile
    @robot-java-performance
      @161-java-profiling-detect
      @162-java-profiling-analyze
      @163-java-profiling-refactor
      @164-java-profiling-verify
  /benchmark
    @robot-java-performance
      @151-java-performance-jmeter
      @152-java-performance-gatling

MCP Servers
  Jbang-Quarkus-JDBC
  MongoDB
  JavaDocs
  Serena-LSP
  Grafana
```

### 分析与设计

通过 user stories、GitHub Issues 或 Jira、ADR、图表、AI plan mode 和 OpenSpec，将想法转化为可执行的变更。

| 资源 | 可用选项 |
| --- | --- |
| **Commands** | [`/update-issue`](./.cursor/commands/update-issue.md) · `/create-adr` · `/create-diagram` · `/create-spec` · `/explore-design` |
| **Agents** | `@robot-business-analyst` · `@robot-architect` · `@robot-tech-lead` |
| **Skills** | [014-agile-user-story](https://www.skills.sh/jabrena/plinth/014-agile-user-story) · [030-architecture-adr-general](https://www.skills.sh/jabrena/plinth/030-architecture-adr-general) · [031-architecture-adr-functional-requirements](https://www.skills.sh/jabrena/plinth/031-architecture-adr-functional-requirements) · [032-architecture-adr-non-functional-requirements](https://www.skills.sh/jabrena/plinth/032-architecture-adr-non-functional-requirements) · [033-architecture-diagrams](https://www.skills.sh/jabrena/plinth/033-architecture-diagrams) · [034-architecture-design-exploration](https://www.skills.sh/jabrena/plinth/034-architecture-design-exploration) · [041-planning-plan-mode](https://www.skills.sh/jabrena/plinth/041-planning-plan-mode) · [042-planning-openspec](https://www.skills.sh/jabrena/plinth/042-planning-openspec) · [043-planning-github-issues](https://www.skills.sh/jabrena/plinth/043-planning-github-issues) · [044-planning-jira](https://www.skills.sh/jabrena/plinth/044-planning-jira) · [051-design-two-steps-methods](https://www.skills.sh/jabrena/plinth/051-design-two-steps-methods) · [052-design-hamburger-method](https://www.skills.sh/jabrena/plinth/052-design-hamburger-method) · [053-design-simple-rules](https://www.skills.sh/jabrena/plinth/053-design-simple-rules) · [056-design-avoid-breaking-changes](https://www.skills.sh/jabrena/plinth/056-design-avoid-breaking-changes) · [200-agents-md](https://www.skills.sh/jabrena/plinth/200-agents-md) |
| **MCP Servers** | [Jbang-Quarkus-JDBC](https://github.com/quarkiverse/quarkus-mcp-servers/blob/main/jdbc/README.md) · [MongoDB](https://github.com/mongodb-js/mongodb-mcp-server) · [Serena-LSP](https://oraios.github.io/serena/01-about/000_intro.html) |

### 构建

借助 Maven、设计、编码、测试、安全、文档、Spring Boot、Quarkus、Micronaut、OpenAPI 和 WireMock 指南，实现并改进 Java 应用程序。

| 资源 | 可用选项 |
| --- | --- |
| **Commands** | [`/create-feature-branch`](./.cursor/commands/create-feature-branch.md) · [`/create-worktree`](./.cursor/commands/create-worktree.md) · [`/implement-spec`](./.cursor/commands/implement-spec.md) |
| **Agents** | `@robot-tech-lead` · `@robot-no-java` · `@robot-java-coder` · `@robot-java-spring-boot-coder` · `@robot-java-quarkus-coder` · `@robot-java-micronaut-coder` |
| **Skills** | [110-java-maven-best-practices](https://www.skills.sh/jabrena/plinth/110-java-maven-best-practices) · [111-java-maven-dependencies](https://www.skills.sh/jabrena/plinth/111-java-maven-dependencies) · [121-java-object-oriented-design](https://www.skills.sh/jabrena/plinth/121-java-object-oriented-design) · [124-java-secure-coding](https://www.skills.sh/jabrena/plinth/124-java-secure-coding) · [143-java-functional-exception-handling](https://www.skills.sh/jabrena/plinth/143-java-functional-exception-handling) |
| **MCP Servers** | [Jbang-Quarkus-JDBC](https://github.com/quarkiverse/quarkus-mcp-servers/blob/main/jdbc/README.md) · [MongoDB](https://github.com/mongodb-js/mongodb-mcp-server) · [JavaDocs](https://www.javadocs.dev/mcp) · [Serena-LSP](https://oraios.github.io/serena/01-about/000_intro.html) |

### 运维

通过可观测性、profiling、benchmarking 和性能测试来衡量并改进生产行为。

| 资源 | 可用选项 |
| --- | --- |
| **Commands** | [`/profile`](./.cursor/commands/profile.md) · [`/benchmark`](./.cursor/commands/benchmark.md) |
| **Agents** | `@robot-java-performance` |
| **Skills** | [151-java-performance-jmeter](https://www.skills.sh/jabrena/plinth/151-java-performance-jmeter) · [161-java-profiling-detect](https://www.skills.sh/jabrena/plinth/161-java-profiling-detect) · [162-java-profiling-analyze](https://www.skills.sh/jabrena/plinth/162-java-profiling-analyze) · [163-java-profiling-refactor](https://www.skills.sh/jabrena/plinth/163-java-profiling-refactor) · [164-java-profiling-verify](https://www.skills.sh/jabrena/plinth/164-java-profiling-verify) |
| **MCP Servers** | [Jbang-Quarkus-JDBC](https://github.com/quarkiverse/quarkus-mcp-servers/blob/main/jdbc/README.md) · [MongoDB](https://github.com/mongodb-js/mongodb-mcp-server) · [Serena-LSP](https://oraios.github.io/serena/01-about/000_intro.html) · [Grafana](https://grafana.com/docs/grafana/latest/developer-resources/mcp/) |

### 合规 (Alpha)

审查 Java 系统、AI 模型，以及 GenAI 工具在应用程序和交付流水线中的使用方式，以识别涉及 AI、数据、安全、产品、平台、市场和治理的法规感知工程控制、证据及向合格责任人的移交。**<u>这些 skills 用于提升工程认知，不构成法律建议。</u>**

| 法规 | Skill |
| --- | --- |
| [EU AI Act](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=OJ:L_202401689) | `801-regulations-eu-ai-act` |
| [DORA](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022R2554) | `802-regulations-dora` |
| [GDPR](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32016R0679) | `803-regulations-gdpr` |
| [NIS2](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022L2555) | `804-regulations-eu-nis2` |
| [Cyber Resilience Act](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32024R2847) | `805-regulations-eu-cyber-resilience-act` |
| [Data Act](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32023R2854) | `806-regulations-eu-data-act` |
| [Digital Services Act](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022R2065) | `807-regulations-eu-digital-services-act` |
| [Digital Markets Act](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32022R1925) | `808-regulations-eu-digital-markets-act` |
| [MiFID II](https://eur-lex.europa.eu/eli/dir/2014/65/oj/eng) | `810-regulations-eu-mifid-ii` |
| [Market Abuse Regulation](https://eur-lex.europa.eu/eli/reg/2014/596/oj/eng) | `811-regulations-eu-market-abuse-regulation` |
| [Product Liability Directive](https://eur-lex.europa.eu/eli/dir/2024/2853/oj/eng) | `812-regulations-eu-product-liability-directive` |

**注意：** 这组 skills 可以很好地补充未来的 [OWASP EU Compliance MCP](https://genai.owasp.org/solution/eu-compliance-mcp/)。

浏览完整的 [Commands](./documentation/guides/INVENTORY-COMMANDS-JAVA.md)、[Agents](./documentation/guides/INVENTORY-AGENTS-JAVA.md)、[Skills](./documentation/guides/INVENTORY-SKILLS-JAVA.md) 和 [MCP Servers](./documentation/guides/THIRD-PARTIES.md) 清单。

## 项目组件

项目会在每次迭代结束时生成一组交付物。

| 清单     | 安装                                                                                    | 快速入门                                                                           |
| --------------- | -------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------- |
| 1. [Commands](./documentation/guides/INVENTORY-COMMANDS-JAVA.md) | `@004-commands-installation` 在项目中安装 Commands | [`Commands`](./documentation/guides/COMMANDS.md) |
| 2. [Agents](./documentation/guides/INVENTORY-AGENTS-JAVA.md) | `@005-agents-installation` 在 Cursor/Claude 中安装 Agents | [`Agents`](./documentation/guides/GETTING-STARTED-AGENTS_ZH.md)     |
| 3. [Skills](./documentation/guides/INVENTORY-SKILLS-JAVA.md) | `npx skills add jabrena/plinth --skill '*' --agent cursor -y` | [`Skills`](./documentation/guides/GETTING-STARTED-SKILLS_ZH.md)     |

### 兼容性

本项目兼容任何支持 `Commands`、`Agents`、`Skills`、`MCP Servers` 与 `AGENTS.md` 的工具。

## Skill 验证

每次 push 都会在 [CI Builds](./.github/workflows/maven.yaml) 中运行以下验证检查，以保持文档和生成的 skills 正确、一致且安全：

| 名称 | 用途 |
| --- | --- |
| 1. [MarkdownValidator](./markdown-validator/src/main/java/info/jab/mv/MarkdownValidator.java) | 保护文档层，在运行 skill 专项检查之前发现 Markdown 解析漂移和远程链接故障。 |
| 2. [skill-check](https://github.com/thedaviddias/skill-check) | 确认每个生成的 skill 符合预期的打包约定，补充更关注行为或安全风险的扫描器。 |
| 3. [cisco-ai-skill-scanner](https://github.com/cisco-ai-defense/skill-scanner) by Cisco | 提供面向行为的安全覆盖，发现结构校验无法识别的高风险 skill 流程。 |
| 4. [SkillSpector](https://github.com/NVIDIA/SkillSpector) by NVIDIA | 提供独立的静态质量和安全审查，便于与其他扫描器的发现进行对照。 |
| 5. [Snyk Agent Scan](https://github.com/snyk/agent-scan) by SNYK | 聚焦 agent skill 的供应链和 prompt 风险信号，与 Cisco 和 SkillSpector 一起提供另一种安全视角。 |

## 局限性

### 缺乏确定性

从一开始就要意识到，由于模型行为的原因，与这些 `Skills` 和 agents 交互的结果并非确定性的；但你可以通过明确目标和验证检查点来降低影响。

### 并非所有模型表现一致

部分交互式 skills 需要 `Premium` 模型才能进行交互式使用；否则会按固定步骤顺序执行。

### 与模型交互的限制

模型可以生成代码，但无法针对你的本地数据执行代码。为弥补这一差距，部分 Skills 包含可在本地运行的脚本。

### 软件工程师必须参与其中

本项目用于支持软件工程工作，但不能替代工程判断。在使用 AI 生成的决策、代码和结果之前，必须由软件工程师进行审查、指导和验证。

### 访问企业数据

当问题涉及企业数据库或其他组织敏感数据时，请谨慎使用。在向 AI 辅助工作流授予访问权限之前，应评估授权、隐私、数据泄露、数据保留和意外修改等风险，并实施最小权限访问、人工审查、验证和监控。请参阅 [OWASP GenAI Data Security Risks & Mitigations 2026](https://genai.owasp.org/resource/owasp-genai-data-security-risks-mitigations-2026/) 以及新的[欧盟法规 skills](#合规-alpha)。

## 贡献

- 按照 [5 分钟快速入门](./documentation/guides/GETTING-STARTED-IN-5-MINUTES_ZH.md) 操作，并告诉我们哪些体验可以改进。
- [浏览 skill 清单](./documentation/guides/INVENTORY-SKILLS-JAVA.md)，并提出尚未覆盖的 Java 工作流。
- [创建 issue](https://github.com/jabrena/plinth/issues)，报告问题或提出改进建议。
- 阅读 [CONTRIBUTING.md](./CONTRIBUTING.md)，改进 skill、agent、command 或项目指南。
- 如果这些工作流对你的 Java 项目有所帮助，请为本仓库加星。

## Architecture Decision Records (ADR)

- 查看 [ADR 索引](./documentation/adr/README.md) 获取完整列表。

## 自 Java 8 起的 Java JEP

Java 使用 JEP（JDK Enhancement Proposals）描述新的语言与平台特性。本仓库跟踪哪些 JEP 可能改进此处的 Skills 与指导内容。

- [JEP 列表](./documentation/jeps/All-JEPS.md)

## 更多资源

演讲、文章、参考链接、skill 门户和相关项目请参阅[项目参考资料](./documentation/guides/PROJECT-REFERENCES_ZH.md)。

由人类开发，并得到 [Cursor](https://www.cursor.com/) 与 [Codex](https://openai.com/codex/) 的支持，带着来自 [Madrid](https://www.google.com/maps/place/Community+of+Madrid,+Madrid/@40.4983324,-6.3162283,8z/data=!3m1!4b1!4m6!3m5!1s0xd41817a40e033b9:0x10340f3be4bc880!8m2!3d40.4167088!4d-3.5812692!16zL20vMGo0eGc?entry=ttu&g_ep=EgoyMDI1MDgxOC4wIKXMDSoASAFQAw%3D%3D) 的 ❤️
