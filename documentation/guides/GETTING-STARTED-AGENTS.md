# Getting Started with Agents for Java

The embedded agents separate analysis, architecture, technical leadership, and implementation. Install them with `@005-agents-installation`; the canonical definitions live under `plinth-skills-generator/src/main/resources/skill-references/assets/agents/`.

## Agent Missions

| Agent | Missions | Usage |
| --- | --- | --- |
| `plinth-business-analyst` | Refine GitHub/Jira issues. | Use `/update-issue`. It does not implement code or silently correct artifacts. |
| `plinth-architect` | Explore design alternatives.<br>Create ADRs.<br>Create architecture diagrams. | Use `/explore-design`, `/create-adr`, or `/create-diagram`. It hands approved constraints to the tech lead. |
| `plinth-tech-lead` | Create OpenSpec changes.<br>Coordinate delivery.<br>Select and delegate to implementation agents.<br>Track implementation and verification. | Use `/create-spec`, or provide an approved plan/OpenSpec task list for delivery. |
| `plinth-java-performance` | Coordinate profiling and benchmarking.<br>Preserve baseline and measurement evidence.<br>Delegate approved optimizations to coder agents. | Use `/profile` or `/benchmark`. It does not implement application code directly. |
| `plinth-java-coder` | Implement framework-neutral Java and Maven work. | Delegation target selected by the tech lead. |
| `plinth-java-spring-boot-coder` | Implement Spring Boot work. | Delegation target selected by the tech lead. |
| `plinth-java-quarkus-coder` | Implement Quarkus work. | Delegation target selected by the tech lead. |
| `plinth-java-micronaut-coder` | Implement Micronaut work. | Delegation target selected by the tech lead. |

The business analyst, architect, tech lead, and Java performance agent do not replace coder agents. The tech lead selects one implementation agent from repository evidence and delegates parallel groups only when dependencies and file ownership permit it. The Java performance agent delegates approved optimizations to the appropriate coder after profiling or benchmark evidence is available.

## Migration

`robot-coordinator` was renamed to `plinth-tech-lead`. There is no compatibility alias. After reinstalling the bundle:

1. Replace direct `@robot-coordinator` mentions with `@plinth-tech-lead`.
2. Replace references to `robot-coordinator.md` with `plinth-tech-lead.md`.
3. Keep the existing delegation model: coder agents remain implementation targets.

## Examples

- `Using @plinth-business-analyst, create a GitHub issue from these requirements.`
- `Using @plinth-architect, explore design alternatives for issue #806.`
- `Using @plinth-tech-lead, create an OpenSpec change directly from this approved issue.`
- `Using @plinth-tech-lead, deliver the selected OpenSpec tasks and delegate each implementation group.`

See [Project Workflows](GETTING-STARTED-WORKFLOWS.md) for lifecycle paths and artifact authority.
