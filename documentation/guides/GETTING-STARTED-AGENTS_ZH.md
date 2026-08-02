# Java Agents 快速入门

内置 Agents 将分析、架构、技术领导和实现职责分离。使用 `@005-agents-installation` 安装；规范定义位于 `plinth-agents-generator/src/main/resources/agents/`。

## Agent 职责

| Agent | 职责 | 用法 |
| --- | --- | --- |
| `plinth-business-analyst` | 创建或更新 GitHub、Jira 或 Azure DevOps issue。<br>从五个视角评估问题。<br>生成 Gherkin 验收标准。 | 使用 `/update-issue`、`/explore-problem` 或 `/create-acceptance-criteria`。它不会实现代码或虚构需求。 |
| `plinth-architect` | 创建或更新 OpenSpec change。<br>探索并完善设计方案。<br>创建 ADR 和架构图。<br>准备实现工件。 | 先使用 `/create-spec`，需要完善设计时再使用 `/explore-design`。使用 `/create-adr`、`/create-diagram` 或 `/close-spec` 生成对应的专项结果。 |
| `plinth-tech-lead` | 评估交付就绪状态。<br>协调交付。<br>选择实现 Agent 并委托工作。<br>跟踪实现与验证。 | 使用 `/implement-spec` 交付已批准的计划或已验证的 OpenSpec 任务列表。它不会创建或完善计划或 OpenSpec change。 |
| `plinth-java-coder` | 实现与框架无关的 Java 和 Maven 工作。 | 由 tech lead 选择的委托目标。 |
| `plinth-java-spring-boot-coder` | 实现 Spring Boot 工作。 | 由 tech lead 选择的委托目标。 |
| `plinth-java-quarkus-coder` | 实现 Quarkus 工作。 | 由 tech lead 选择的委托目标。 |
| `plinth-java-micronaut-coder` | 实现 Micronaut 工作。 | 由 tech lead 选择的委托目标。 |
| `plinth-no-java` | 使用仓库现有技术栈实现 Java 和 JVM 框架以外的工作。 | 当执行工件不包含 Java 范围时，由 tech lead 选择该委托目标。 |
| `plinth-java-performance` | 协调 profiling 和 benchmarking。<br>保留 baseline 与测量证据。<br>将已批准的优化委托给 coder agents。 | 使用 `/profile` 或 `/benchmark`。它不会直接实现应用代码。 |

business analyst、architect、tech lead 和 Java performance agent 不替代实现 Agents。architect 负责实现前的规划与规格说明；tech lead 负责基于已批准的执行工件进行交付。tech lead 根据仓库证据选择 Java、特定框架或非 Java 实现 Agent，并且仅在依赖关系和文件所有权允许时并行委托任务组。Java performance agent 会在已有 profiling 或 benchmark 证据后，将已批准的优化委托给合适的 Java 或框架 coder。
