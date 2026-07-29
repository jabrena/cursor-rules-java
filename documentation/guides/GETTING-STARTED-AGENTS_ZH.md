# Java Agents 快速入门

内置 Agents 将分析、架构、技术领导和实现职责分离。使用 `@005-agents-installation` 安装；规范定义位于 `plinth-skills-generator/src/main/resources/skill-references/assets/agents/`。

## Agent 职责

| Agent | 职责 | 用法 |
| --- | --- | --- |
| `plinth-business-analyst` | 完善 GitHub/Jira issue。 | 使用 `/update-issue`。它不会实现代码或静默修改工件。 |
| `plinth-architect` | 探索设计方案。<br>创建 ADR。<br>创建架构图。 | 使用 `/explore-design`、`/create-adr` 或 `/create-diagram`。它将已批准的约束交给 tech lead。 |
| `plinth-tech-lead` | 创建 OpenSpec change。<br>协调交付。<br>选择实现 Agent 并委托工作。<br>跟踪实现与验证。 | 使用 `/create-spec`，或提供已批准的计划/OpenSpec 任务列表进行交付。 |
| `plinth-java-performance` | 协调 profiling 和 benchmarking。<br>保留 baseline 与测量证据。<br>将已批准的优化委托给 coder agents。 | 使用 `/profile` 或 `/benchmark`。它不会直接实现应用代码。 |
| `plinth-java-coder` | 实现与框架无关的 Java 和 Maven 工作。 | 由 tech lead 选择的委托目标。 |
| `plinth-java-spring-boot-coder` | 实现 Spring Boot 工作。 | 由 tech lead 选择的委托目标。 |
| `plinth-java-quarkus-coder` | 实现 Quarkus 工作。 | 由 tech lead 选择的委托目标。 |
| `plinth-java-micronaut-coder` | 实现 Micronaut 工作。 | 由 tech lead 选择的委托目标。 |

business analyst、architect、tech lead 和 Java performance agent 不替代 coder agents。tech lead 根据仓库证据选择实现 Agent，并且仅在依赖关系和文件所有权允许时并行委托任务组。Java performance agent 会在已有 profiling 或 benchmark 证据后，将已批准的优化委托给合适的 coder。

## 迁移

`robot-coordinator` 已重命名为 `plinth-tech-lead`，不提供兼容别名。重新安装后：

1. 将直接的 `@robot-coordinator` 提及替换为 `@plinth-tech-lead`。
2. 将 `robot-coordinator.md` 引用替换为 `plinth-tech-lead.md`。
3. 保留现有委托模型：coder agents 仍是实现目标。

## 示例

- `Using @plinth-business-analyst, create a GitHub issue from these requirements.`
- `Using @plinth-architect, explore design alternatives for issue #806.`
- `Using @plinth-tech-lead, create an OpenSpec change directly from this approved issue.`
- `Using @plinth-tech-lead, deliver the selected OpenSpec tasks and delegate each implementation group.`

生命周期路径和工件权威规则请参阅[项目工作流](GETTING-STARTED-WORKFLOWS_ZH.md)。
