# Getting Started with Agents for Java

The embedded agents separate analysis, architecture, technical leadership, and implementation. Install them with `@005-agents-installation`; the canonical definitions live under `plinth-agents-generator/src/main/resources/agents/`.

## Agent Missions

| Agent | Missions | Usage |
| --- | --- | --- |
| `plinth-business-analyst` | Create or update GitHub, Jira, or Azure DevOps issues.<br>Evaluate problems through five points of view.<br>Derive Gherkin acceptance criteria. | Use `/update-issue`, `/explore-problem`, or `/create-acceptance-criteria`. It does not implement code or invent requirements. |
| `plinth-architect` | Create or update OpenSpec changes.<br>Explore and refine design alternatives.<br>Create ADRs and architecture diagrams.<br>Prepare implementation artifacts. | Use `/create-spec` first, then `/explore-design` when refinement is needed. Use `/create-adr`, `/create-diagram`, or `/close-spec` for their focused outcomes. |
| `plinth-tech-lead` | Assess delivery readiness.<br>Coordinate delivery.<br>Select and delegate to implementation agents.<br>Track implementation and verification. | Use `/implement-spec` with an approved plan or validated OpenSpec task list. It does not create or refine plans or OpenSpec changes. |
| `plinth-java-coder` | Implement framework-neutral Java and Maven work. | Delegation target selected by the tech lead. |
| `plinth-java-spring-boot-coder` | Implement Spring Boot work. | Delegation target selected by the tech lead. |
| `plinth-java-quarkus-coder` | Implement Quarkus work. | Delegation target selected by the tech lead. |
| `plinth-java-micronaut-coder` | Implement Micronaut work. | Delegation target selected by the tech lead. |
| `plinth-no-java` | Implement work outside Java and JVM-based frameworks using the repository's existing stack. | Delegation target selected by the tech lead when the execution artifact has no Java scope. |
| `plinth-java-performance` | Coordinate profiling and benchmarking.<br>Preserve baseline and measurement evidence.<br>Delegate approved optimizations to coder agents. | Use `/profile` or `/benchmark`. It does not implement application code directly. |

The business analyst, architect, tech lead, and Java performance agent do not replace implementation agents. The architect owns pre-implementation planning and specification; the tech lead owns delivery from an approved execution artifact. The tech lead selects one Java, framework-specific, or non-Java implementation agent from repository evidence and delegates parallel groups only when dependencies and file ownership permit it. The Java performance agent delegates approved optimizations to the appropriate Java or framework coder after profiling or benchmark evidence is available.

## Migration

`robot-coordinator` was renamed to `plinth-tech-lead`. There is no compatibility alias. After reinstalling the bundle:

1. Replace direct `@robot-coordinator` mentions with `@plinth-tech-lead`.
2. Replace references to `robot-coordinator.md` with `plinth-tech-lead.md`.
3. Keep the existing delegation model: coder agents remain implementation targets.

## Examples

- `Using @plinth-business-analyst, evaluate issue #806 through the five problem-analysis perspectives.`
- `Using @plinth-architect, create an OpenSpec change directly from this approved issue.`
- `Using @plinth-architect, refine the technical approach in this OpenSpec change.`
- `Using @plinth-tech-lead, deliver the selected OpenSpec tasks and delegate each implementation group.`

See [Project Workflows](GETTING-STARTED-WORKFLOWS.md) for lifecycle paths and artifact authority.
