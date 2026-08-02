# Primeros pasos con Agents for Java

Los agentes integrados separan análisis, arquitectura, liderazgo técnico e implementación. Instálalos con `@005-agents-installation`; las definiciones canónicas están en `plinth-agents-generator/src/main/resources/agents/`.

## Misiones de los agentes

| Agent | Misiones | Uso |
| --- | --- | --- |
| `plinth-business-analyst` | Crear o actualizar issues de GitHub, Jira o Azure DevOps.<br>Evaluar problemas desde cinco puntos de vista.<br>Derivar criterios de aceptación en Gherkin. | Usa `/update-issue`, `/explore-problem` o `/create-acceptance-criteria`. No implementa código ni inventa requisitos. |
| `plinth-architect` | Crear o actualizar cambios OpenSpec.<br>Explorar y refinar alternativas de diseño.<br>Crear ADRs y diagramas de arquitectura.<br>Preparar artefactos de implementación. | Usa primero `/create-spec` y después `/explore-design` cuando sea necesario refinar el diseño. Usa `/create-adr`, `/create-diagram` o `/close-spec` para sus resultados específicos. |
| `plinth-tech-lead` | Evaluar la preparación para la entrega.<br>Coordinar la entrega.<br>Seleccionar y delegar en agentes de implementación.<br>Controlar implementación y verificación. | Usa `/implement-spec` con un plan aprobado o una lista de tareas OpenSpec validada. No crea ni refina planes o cambios OpenSpec. |
| `plinth-java-coder` | Implementar trabajo Java y Maven independiente del framework. | Objetivo de delegación seleccionado por el tech lead. |
| `plinth-java-spring-boot-coder` | Implementar trabajo Spring Boot. | Objetivo de delegación seleccionado por el tech lead. |
| `plinth-java-quarkus-coder` | Implementar trabajo Quarkus. | Objetivo de delegación seleccionado por el tech lead. |
| `plinth-java-micronaut-coder` | Implementar trabajo Micronaut. | Objetivo de delegación seleccionado por el tech lead. |
| `plinth-no-java` | Implementar trabajo fuera de Java y de los frameworks basados en la JVM usando el stack existente del repositorio. | Objetivo de delegación seleccionado por el tech lead cuando el artefacto de ejecución no tiene alcance Java. |
| `plinth-java-performance` | Coordinar profiling y benchmarking.<br>Preservar evidencias de baseline y medición.<br>Delegar optimizaciones aprobadas a coder agents. | Usa `/profile` o `/benchmark`. No implementa código de aplicación directamente. |

El business analyst, architect, tech lead y Java performance agent no sustituyen a los agentes de implementación. El architect es responsable de la planificación y especificación previas a la implementación; el tech lead es responsable de la entrega desde un artefacto de ejecución aprobado. El tech lead selecciona un agente de implementación Java, específico del framework o no Java usando evidencias del repositorio y solo delega grupos en paralelo cuando las dependencias y la propiedad de archivos lo permiten. El Java performance agent delega optimizaciones aprobadas al coder Java o de framework adecuado cuando ya existe evidencia de profiling o benchmark.
