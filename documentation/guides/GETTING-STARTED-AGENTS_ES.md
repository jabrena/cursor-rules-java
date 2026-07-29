# Primeros pasos con Agents for Java

Los agentes integrados separan análisis, arquitectura, liderazgo técnico e implementación. Instálalos con `@005-agents-installation`; las definiciones canónicas están en `plinth-skills-generator/src/main/resources/skill-references/assets/agents/`.

## Misiones de los agentes

| Agent | Misiones | Uso |
| --- | --- | --- |
| `plinth-business-analyst` | Refinar issues en GitHub/Jira. | Usa `/update-issue`. No implementa código ni corrige artefactos de forma silenciosa. |
| `plinth-architect` | Explorar alternativas de diseño.<br>Crear ADRs.<br>Crear diagramas de arquitectura. | Usa `/explore-design`, `/create-adr` o `/create-diagram`. Entrega las restricciones aprobadas al tech lead. |
| `plinth-tech-lead` | Crear cambios OpenSpec.<br>Coordinar la entrega.<br>Seleccionar y delegar en agentes de implementación.<br>Controlar implementación y verificación. | Usa `/create-spec` o proporciona un plan/lista de tareas OpenSpec aprobada para la entrega. |
| `plinth-java-performance` | Coordinar profiling y benchmarking.<br>Preservar evidencias de baseline y medición.<br>Delegar optimizaciones aprobadas a coder agents. | Usa `/profile` o `/benchmark`. No implementa código de aplicación directamente. |
| `plinth-java-coder` | Implementar trabajo Java y Maven independiente del framework. | Objetivo de delegación seleccionado por el tech lead. |
| `plinth-java-spring-boot-coder` | Implementar trabajo Spring Boot. | Objetivo de delegación seleccionado por el tech lead. |
| `plinth-java-quarkus-coder` | Implementar trabajo Quarkus. | Objetivo de delegación seleccionado por el tech lead. |
| `plinth-java-micronaut-coder` | Implementar trabajo Micronaut. | Objetivo de delegación seleccionado por el tech lead. |

El business analyst, architect, tech lead y Java performance agent no sustituyen a los coder agents. El tech lead selecciona un agente de implementación usando evidencias del repositorio y solo delega grupos en paralelo cuando las dependencias y la propiedad de archivos lo permiten. El Java performance agent delega optimizaciones aprobadas al coder adecuado cuando ya existe evidencia de profiling o benchmark.

## Migración

`robot-coordinator` se renombró a `plinth-tech-lead`. No existe un alias de compatibilidad. Después de reinstalar el paquete:

1. Sustituye las menciones directas a `@robot-coordinator` por `@plinth-tech-lead`.
2. Sustituye referencias a `robot-coordinator.md` por `plinth-tech-lead.md`.
3. Mantén el modelo de delegación existente: los coder agents siguen siendo objetivos de implementación.

## Ejemplos

- `Using @plinth-business-analyst, create a GitHub issue from these requirements.`
- `Using @plinth-architect, explore design alternatives for issue #806.`
- `Using @plinth-tech-lead, create an OpenSpec change directly from this approved issue.`
- `Using @plinth-tech-lead, deliver the selected OpenSpec tasks and delegate each implementation group.`

Consulta [Flujos de trabajo del proyecto](GETTING-STARTED-WORKFLOWS_ES.md) para ver las rutas del ciclo de vida y la autoridad de los artefactos.
