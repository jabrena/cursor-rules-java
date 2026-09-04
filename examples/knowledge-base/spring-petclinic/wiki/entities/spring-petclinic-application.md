---
type: entity
tags: [spring-boot, java, application]
updated: 2026-09-04
sources: [spring-petclinic]
---
# spring-petclinic (application)

Spring's reference sample application demonstrating Spring Boot conventions. Built with Maven, parented on `spring-boot-starter-parent:4.1.0`, requiring Java 17+ ([[spring-petclinic]]). The entry point is `PetClinicApplication` (`@SpringBootApplication`) in package `org.springframework.samples.petclinic`.

The domain model splits across four sub-packages:

- `owner` — the core clinic domain: `Owner`, `Pet`, `Visit`, `PetType`, with `OwnerController`, `PetController`, `VisitController`, `OwnerRepository`, `PetTypeRepository`, `PetValidator`, `PetTypeFormatter`
- `vet` — the veterinarian domain: `Vet`, `Specialty`, `VetController`, `VetRepository`, `Vets`
- `system` — not yet inspected
- `model` — shared base types, not yet inspected

Runs locally via `./mvnw spring-boot:run` or `./gradlew bootRun`; a container image is built through the Spring Boot build plugin rather than a checked-in `Dockerfile` ([[spring-petclinic]]).

## Open questions

- Contents of the `system` and `model` packages — out of scope for this ingest
- Default persistence/database configuration — would require reading `src/main/resources/**`

## Related

- [[spring-petclinic]] — source-summary page for the ingested repository at this commit
