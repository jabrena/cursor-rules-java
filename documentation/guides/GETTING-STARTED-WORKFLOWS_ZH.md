# 项目工作流

分析和设计流程可以组合。请从已有的权威工件开始；创建 OpenSpec 前不强制要求计划，创建计划前也不强制要求 OpenSpec。

## 生命周期选项

1. 在提交仓库内的分析和设计工件前，可选择运行 `/create-feature-branch`。
2. 当独立子变更可以在隔离分支中执行时，使用 `/create-worktree`。
3. 运行 `/update-issue`，在 GitHub 或 Jira 中完善已记录的需求。
4. 当重要技术方案尚未确定时，运行 `/explore-design`。
5. 使用 `/create-adr` 记录长期决策，使用 `/create-diagram` 创建有用的架构视图。
6. 从 issue、已批准设计、ADR、现有计划、现有 OpenSpec 或有效组合运行 `/create-spec`。
7. 让 `@plinth-tech-lead` 交付选定的计划或 OpenSpec `tasks.md`。
8. 当实现后需要性能证据时，使用 `/profile` 或 `/benchmark` 并交给 `@plinth-java-performance`。

常见路径包括 issue 到计划、issue 到 OpenSpec、计划到 OpenSpec，以及现有 OpenSpec 到计划。当结果在价值、负责人、发布时间、风险、回滚或部署边界上相互独立时，`/create-spec` 可以提出多个 OpenSpec changes。创建前必须由用户批准该 change map。

## 工件权威

| 工件 | 权威范围 |
| --- | --- |
| Issue 或 user story | 问题、价值、范围和验收标准 |
| ADR | 架构决策及其后果 |
| OpenSpec specification | 需求和场景 |
| 实现计划 | 技术交付策略、顺序、依赖和验证 |
| 选定的 OpenSpec `tasks.md` | 选择该流程时的执行跟踪 |

派生方向是单向且有记录的。派生工件绝不会静默重写其来源。发生冲突时，请 `@plinth-business-analyst` 审查存在冲突的工件，明确决定应修改哪个权威范围，显式更新它，重新生成受影响的派生工件，然后再次检查对齐。

## 并行交付

对独立的 commands、agents 或 planning 变更使用单独的 worktree。先集成并验证这些源变更，再更新共享文档。只有当依赖关系和文件所有权确保工作独立时，tech lead 才能并行委托实现组。
