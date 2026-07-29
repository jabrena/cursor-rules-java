# Flujos de trabajo del proyecto

El análisis y el diseño son componibles. Empieza desde los artefactos autoritativos disponibles; no es obligatorio crear un plan antes de OpenSpec ni OpenSpec antes de un plan.

## Opciones del ciclo de vida

1. Ejecuta opcionalmente `/create-feature-branch` antes de versionar artefactos de análisis y diseño.
2. Usa `/create-worktree` cuando cambios hijos independientes puedan ejecutarse en ramas aisladas.
3. Ejecuta `/update-issue` para refinar la necesidad registrada en GitHub o Jira.
4. Ejecuta `/explore-design` cuando existan alternativas técnicas relevantes sin resolver.
5. Ejecuta `/create-adr` para decisiones duraderas y `/create-diagram` para vistas de arquitectura útiles.
6. Ejecuta `/create-spec` desde un issue, diseño aprobado, ADRs, plan existente, OpenSpec existente o una combinación válida.
7. Pide a `@plinth-tech-lead` que entregue el plan o `tasks.md` de OpenSpec seleccionado.
8. Usa `/profile` o `/benchmark` con `@plinth-java-performance` cuando necesites evidencia de rendimiento después de la implementación.

Las rutas comunes incluyen issue a plan, issue a OpenSpec, plan a OpenSpec y OpenSpec existente a plan. `/create-spec` puede proponer varios cambios OpenSpec cuando los resultados tengan límites independientes de valor, propiedad, entrega, riesgo, rollback o despliegue. El usuario aprueba ese mapa antes de crear los cambios.

## Autoridad de los artefactos

| Artefacto | Responsabilidad autoritativa |
| --- | --- |
| Issue o user story | Problema, valor, alcance y criterios de aceptación |
| ADR | Decisión de arquitectura y consecuencias |
| Especificación OpenSpec | Requisitos y escenarios |
| Plan de implementación | Estrategia técnica, secuencia, dependencias y verificación |
| OpenSpec `tasks.md` seleccionado | Seguimiento de ejecución cuando se elige ese flujo |

La derivación es unidireccional y queda registrada. Un artefacto derivado nunca modifica silenciosamente sus fuentes. Si existen conflictos, pide a `@plinth-business-analyst` que revise los artefactos en conflicto, decide qué responsabilidad autoritativa debe cambiar, actualízala explícitamente, regenera los derivados afectados y revisa la alineación de nuevo.

## Entrega en paralelo

Usa worktrees separados para cambios independientes de comandos, agentes o planificación. Integra y valida esos cambios fuente antes de actualizar documentación compartida. El tech lead solo puede delegar grupos simultáneamente cuando las dependencias y la propiedad de archivos hagan el trabajo independiente.
