---
type: source
tags: [git-repository, spring-boot, java]
updated: 2026-09-04
sources: []
---
# spring-petclinic (git repository @ 818c4136ea971c21674525f9053de0d9c7ad8cfe)

- Author or organization: Spring team / spring-projects
- Published: commit 818c4136ea971c21674525f9053de0d9c7ad8cfe, resolved from `main` (HEAD) on 2026-09-04
- Location: [raw/repositories/spring-petclinic/818c4136ea971c21674525f9053de0d9c7ad8cfe.md](../../raw/repositories/spring-petclinic/818c4136ea971c21674525f9053de0d9c7ad8cfe.md) (origin: https://github.com/spring-projects/spring-petclinic)

## Summary

Spring PetClinic is Spring's reference sample application, built on Spring Boot 4 (parent `spring-boot-starter-parent:4.1.0`) with Maven, targeting Java 17+. Its main package `org.springframework.samples.petclinic` (`spring-petclinic@818c4136ea971c21674525f9053de0d9c7ad8cfe:src/main/java/org/springframework/samples/petclinic/PetClinicApplication.java`) is the `@SpringBootApplication` entry point, and the domain splits into `owner` (Owner, Pet, Visit, PetType and their MVC controllers/repositories) and `vet` (Vet, Specialty, VetController, VetRepository, Vets), plus `system` and `model` support packages. The README documents running locally with `./mvnw spring-boot:run` / `./gradlew bootRun` and building a container image via the Spring Boot build plugin.

## Claims integrated

- Application entry point is `PetClinicApplication` in `org.springframework.samples.petclinic` → [[spring-petclinic-application]]
- Domain splits into an `owner` package (Owner, Pet, Visit, PetType) and a `vet` package (Vet, Specialty) → [[spring-petclinic-application]]
- Build is Maven-based, parented on `spring-boot-starter-parent:4.1.0`, requiring Java 17+ → [[spring-petclinic-application]]

## Questions answered or raised

- What do the `system` and `model` packages contain? Not yet inspected — scope for this ingest excluded `src/main/resources/**` and `src/test/**`.
- What persistence/database configuration does the app use by default? Not yet inspected.
