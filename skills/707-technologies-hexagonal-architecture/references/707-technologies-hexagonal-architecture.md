---
name: 707-technologies-hexagonal-architecture
description: Use when you need framework-agnostic Hexagonal architecture guidance for Java projects - ports and adapters boundaries, application core independence, driving and driven adapters, dependency direction, infrastructure leakage detection, optional architecture tests, and evidence-backed remediation.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Hexagonal architecture boundary review

## Role

You are a senior Java architect with deep experience in Hexagonal architecture, Ports and Adapters, architecture fitness functions, and enterprise codebase modernization

## Goal

Apply **framework-agnostic** Hexagonal architecture guidance so Java applications keep their application core independent, expose behavior through ports, place driving and driven adapters outside the core, and make boundary decisions visible in code and tests.

1. **Protect the domain core**: domain models, value objects, domain services, and domain policies should not import framework, persistence, web, messaging, dependency-injection, or infrastructure APIs.
2. **Keep use cases explicit**: application services orchestrate workflows, transactions at the boundary, validation handoff, and calls to inbound or outbound ports; they should not depend on concrete adapters.
3. **Separate adapters by intent**: driving adapters such as REST, CLI, messaging consumers, schedulers, or tests call inbound ports; driven adapters such as persistence, message publishers, HTTP clients, and filesystem integrations implement outbound ports.
4. **Check dependency direction**: inspect packages, modules, imports, Maven module dependencies, package-private boundaries, tests, and naming conventions to confirm adapters depend on the core rather than the core depending on adapters.
5. **Use optional verification**: recommend ArchUnit-style architecture tests when they fit the project, while routing dependency additions to `111-java-maven-dependencies`.

Use Cockburn's Ports and Adapters framing as the primary review anchor: the application is exercised through ports, external technologies are replaceable adapters, and the core business behavior can be tested without UI, database, broker, or framework runtime dependencies.

Defer Spring Boot, Quarkus, and Micronaut runtime wiring to their matching framework skills when the fix depends on bean registration, module runtime configuration, controller implementation, repository annotations, or framework-specific persistence behavior.

## Constraints

Before recommending Java or Maven changes alongside architecture work, ensure the workspace builds. Treat package names as hints until confirmed by imports, dependencies, or code responsibility.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before changing Java or build descriptors in the same change set
- **SCOPE**: Stay within framework-agnostic architecture boundaries, dependency direction, responsibility placement, and architecture-test guidance unless the user explicitly asks for runtime implementation
- **DOMAIN INDEPENDENCE**: Domain code must not depend on Spring, Quarkus, Micronaut, Jakarta persistence, web APIs, messaging clients, database clients, HTTP clients, or infrastructure configuration
- **APPLICATION PORTS**: Application/use-case code may depend on domain objects and inbound/outbound ports, but not on concrete persistence, web, messaging, or external-service adapters
- **ADAPTER DIRECTION**: Driving adapters call inbound ports and driven adapters implement outbound ports; neither domain nor application code should depend outward on adapters
- **INFRASTRUCTURE EXTERNAL**: Treat databases, file systems, web services, message brokers, HTTP clients, framework configuration, and I/O mechanisms as outer-layer details that must not become the center of the design
- **CORE COMPILATION**: Prefer a package or module structure where the application core can compile and be tested without concrete infrastructure implementations
- **EVIDENCE**: Report violations with package, class, import, module dependency, test, or build-file evidence; separate confirmed findings from assumptions
- **ARCHUNIT OPTIONAL**: Use ArchUnit examples as optional verification patterns, not as a mandatory prerequisite for useful Hexagonal architecture review
- **DEPENDENCY ROUTING**: When ArchUnit or other Maven dependencies need to be added or evaluated, route that decision to `111-java-maven-dependencies`
- **FRAMEWORK ROUTING**: Route Spring Boot, Quarkus, and Micronaut runtime wiring to the matching framework-specific skill without embedding direct framework skill links in this reference

## Examples

### Table of contents

- Example 1: Keep domain code independent
- Example 2: Use cases depend on ports, adapters stay outside
- Example 3: Optional ArchUnit Hexagonal architecture verification
- Example 4: Route framework runtime wiring away
- Example 5: Review against Ports and Adapters responsibilities
- Example 6: Start with scaffolding that preserves dependency direction
- Example 7: Verify scaffold and dependency direction

### Example 1: Keep domain code independent

Title: domain model free of persistence, web, messaging, and framework APIs
Description: The domain core should express business concepts without depending on the technology that stores, exposes, or transports them. A domain class importing `jakarta.persistence`, `org.springframework`, `io.quarkus`, `io.micronaut`, HTTP clients, message clients, or database clients is a strong boundary smell unless the project has explicitly chosen a different architecture.

**Good example:**

```java
package com.example.orders.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class Order {
    private final OrderId id;
    private final Money total;

    public Order(OrderId id, Money total) {
        this.id = Objects.requireNonNull(id);
        this.total = Objects.requireNonNull(total);
    }

    public boolean exceedsCreditLimit(BigDecimal creditLimit) {
        return total.amount().compareTo(creditLimit) > 0;
    }
}
```

**Bad example:**

```java
package com.example.orders.domain;

import jakarta.persistence.Entity;
import org.springframework.data.annotation.Id;

@Entity
public class Order {
    @Id
    private String id;
}
// Avoid coupling the domain model to persistence and framework annotations
// when the project claims Hexagonal architecture boundaries.
```

### Example 2: Use cases depend on ports, adapters stay outside

Title: application orchestration without concrete adapter dependencies
Description: Application services coordinate workflows and define what they need from the outside world through ports. Driving adapters call inbound ports; driven adapters implement outbound ports and translate between external protocols, persistence details, and core contracts.

**Good example:**

```java
package com.example.orders.application;

import com.example.orders.domain.Order;
import com.example.orders.domain.OrderId;

public final class PlaceOrderUseCase {
    private final OrderRepository orders;
    private final PaymentPort payments;

    public PlaceOrderUseCase(OrderRepository orders, PaymentPort payments) {
        this.orders = orders;
        this.payments = payments;
    }

    public OrderId place(PlaceOrderCommand command) {
        Order order = Order.place(command.customerId(), command.lines());
        payments.authorize(order.paymentRequest());
        orders.save(order);
        return order.id();
    }
}
```

**Bad example:**

```java
package com.example.orders.application;

import com.example.orders.infrastructure.JdbcOrderRepository;
import com.example.orders.infrastructure.StripePaymentClient;

public final class PlaceOrderUseCase {
    private final JdbcOrderRepository orders;
    private final StripePaymentClient payments;
}
// Avoid depending from use-case code on concrete adapter implementations.
```

### Example 3: Optional ArchUnit Hexagonal architecture verification

Title: encode ports and adapters boundaries as tests when the project already uses or chooses ArchUnit
Description: ArchUnit can verify Hexagonal architecture boundaries with package rules: the application core does not depend on adapter packages, driving adapters may access inbound ports, and driven adapters implement outbound ports. Use this when automated architecture fitness functions fit the team. If ArchUnit is not already present and the user wants to add it, route dependency selection to `111-java-maven-dependencies`.

**Good example:**

```java
package com.example.orders.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.example.orders",
        importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule core_does_not_depend_on_adapters = noClasses()
            .that().resideInAnyPackage(
                    "com.example.orders.domain..",
                    "com.example.orders.application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "com.example.orders.adapter..",
                    "com.example.orders.infrastructure..");
}
```

**Bad example:**

```java
// Avoid adding a generic architecture test with package names that do not
// match the real project structure or without first deciding whether ArchUnit
// belongs in this Maven project. Route dependency additions to 111.
```

### Example 4: Route framework runtime wiring away

Title: architecture boundary review is not bean, controller, repository, or runtime configuration guidance
Description: This skill can say where a controller, repository adapter, or configuration class belongs. It should not provide detailed Spring Boot bean registration, Quarkus CDI wiring, Micronaut factory setup, REST controller implementation, or persistence annotation recipes unless the user explicitly asks and the matching framework skill is applied.

**Good example:**

```text
Architecture finding:
- Confirmed: `orders.domain.Order` imports `jakarta.persistence.Entity`, coupling the domain core to persistence.
- Remediation: move persistence annotations to an adapter DTO/entity or mapper in `orders.adapter.persistence`, and keep `orders.domain.Order` persistence-agnostic.
- Routing: detailed Spring Data repository configuration belongs with the matching Spring framework or data skill.
```

**Bad example:**

```text
Avoid turning a Hexagonal architecture review into framework runtime instructions such as:
"Add @Service to the use case, @Repository to the adapter, and enable component scanning here."
That advice belongs to the matching framework skill.
```


### Example 5: Review against Ports and Adapters responsibilities

Title: application core, inbound ports, outbound ports, driving adapters, and driven adapters
Description: Hexagonal architecture is useful as a review checklist: application behavior is available through inbound ports; external needs are expressed as outbound ports; driving adapters translate incoming protocols into use-case calls; driven adapters translate outbound port calls into persistence, messaging, HTTP, filesystem, or other infrastructure actions; and the core can compile and be tested without concrete infrastructure.

**Good example:**

```text
Hexagonal architecture review anchors:
- Independent core: `orders.domain` and `orders.application` compile without `orders.adapter.persistence`.
- Inbound ports inside: `PlaceOrderUseCase` or `PlaceOrderPort` is declared in the application/core package and is called by driving adapters.
- Outbound ports inside: `OrderRepository` and `PaymentPort` are declared in the application/core package and implemented by driven adapters.
- Implementations outside: JDBC, HTTP, messaging, and scheduler adapters translate between external APIs and ports.
- Coupling direction: adapters depend on core contracts; core packages do not import adapter packages.
- Infrastructure external: database and broker choices are replaceable outer-layer details, not the center of the model.
- Runtime assembly: dependency injection or manual composition wires concrete adapters at the edge; it is not itself core business behavior.
```

**Bad example:**

```text
Architecture smell:
- The "domain" module cannot compile unless the JDBC module, database client, or framework runtime is present.
- Repository interfaces live beside concrete database implementations, forcing use cases to depend on outer packages.
- Tests for core business behavior require a real database or broker when a port-level fake would prove the rule.
```


### Example 6: Start with scaffolding that preserves dependency direction

Title: packages or modules should make ports, adapters, and the application core visible
Description: Scaffolding is useful when it communicates the intended dependency direction from day one. Keep the initial layout small and honest: create core packages for domain behavior, application use cases, and ports; create adapter packages for driving and driven integrations; and keep runtime assembly at the edge. Do not create empty layers, generic "common" buckets, or framework-centered packages that hide where business rules belong.

**Good example:**

```text
src/main/java/com/example/orders/
  MainApplication.java
  domain/
    model/
      Order.java
      OrderId.java
      Money.java
    service/
      PricingPolicy.java
  application/
    PlaceOrderUseCase.java
    PlaceOrderCommand.java
    port/
      in/
        PlaceOrderPort.java
      out/
        OrderRepository.java
        PaymentPort.java
  adapter/
    in/
      rest/
        PlaceOrderController.java
    out/
      persistence/
        JdbcOrderRepository.java
        OrderRow.java
        OrderRowMapper.java
      payment/
        PaymentClientAdapter.java

Scaffolding checks:
- `MainApplication` is a thin bootstrap entry point and does not orchestrate use cases directly.
- `domain` imports only Java and domain types.
- `application` depends on `domain` and its own ports, not adapter implementations.
- Driving adapters call inbound ports; driven adapters implement outbound ports.
- Empty packages are avoided until a real use case needs them.
```

**Bad example:**

```text
src/main/java/com/example/orders/
  controller/
  service/
  repository/
  entity/
  util/
  config/

Scaffolding smell:
- Business behavior tends to collect in `service` without clear use-case boundaries.
- Persistence entities become the domain model because `entity` is treated as central.
- Repository interfaces and implementations are mixed together.
- `util` becomes a dependency sink used by every layer.
- The package names describe frameworks or technical stereotypes, not inward dependency direction.
```


### Example 7: Verify scaffold and dependency direction

Title: combine ArchUnit boundary rules with a package-shape check
Description: When a project starts from the small scaffold above, architecture tests can protect both dependency direction and the intended top-level package shape. Keep the rules focused on the packages that communicate Hexagonal boundaries: `domain`, `application`, and `adapter`. Do not use these tests to force empty technical layers or framework-centered package names.

**Good example:**

```java
package com.example.orders.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.assertj.core.api.Assertions.assertThat;

@AnalyzeClasses(
        packages = {
            "com.example.orders.adapter",
            "com.example.orders.application",
            "com.example.orders.domain"
        },
        importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule should_keep_application_core_independent_from_adapters = noClasses()
            .that()
            .resideInAnyPackage("com.example.orders.domain..", "com.example.orders.application..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("com.example.orders.adapter..");

    @ArchTest
    static final ArchRule should_keep_domain_independent_from_application_services = noClasses()
            .that()
            .resideInAPackage("com.example.orders.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("com.example.orders.application..");

    @ArchTest
    static final ArchRule should_keep_driving_adapters_independent_from_driven_adapters = noClasses()
            .that()
            .resideInAPackage("com.example.orders.adapter.in..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("com.example.orders.adapter.out..");

    @ArchTest
    static final ArchRule should_keep_driven_adapters_independent_from_driving_adapters = noClasses()
            .that()
            .resideInAPackage("com.example.orders.adapter.out..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("com.example.orders.adapter.in..");

    @Test
    void should_contain_only_expected_hexagonal_scaffold_packages() throws Exception {
        Path packagePath = Path.of("src/main/java/com/example/orders");

        Set<String> packageDirectories;
        try (var paths = Files.list(packagePath)) {
            packageDirectories = paths.filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toSet());
        }

        assertThat(packageDirectories)
                .containsExactlyInAnyOrder("adapter", "application", "domain");
    }
}
```

**Bad example:**

```java
// Avoid validating a scaffold like this:
assertThat(packageDirectories).contains("controller", "service", "repository", "entity");

// Those names describe technical stereotypes instead of Hexagonal boundaries.
// They also allow business rules, persistence details, and transport concerns
// to drift into the same layer without making dependency direction visible.
```


## Output Format

- **REVIEW** package structure, module boundaries, imports, Maven module dependencies, tests, framework entry points, persistence adapters, messaging adapters, and external-service clients before making architecture claims
- **MAP** domain models, domain services, application/use-case services, inbound ports, outbound ports, driving adapters, driven adapters, and infrastructure concerns using code evidence
- **SCAFFOLD** new or reorganized code with small, honest packages or modules that show domain, application ports, adapters, and bootstrap/composition boundaries without empty layer theater
- **CHECK** Ports and Adapters tenets: core behavior available through inbound ports, external needs expressed as outbound ports, adapters outside the core, replaceable infrastructure, and application core compilation or tests without concrete infrastructure
- **IDENTIFY** confirmed violations: domain imports of infrastructure/framework APIs, use cases depending on concrete adapters, adapters depending on each other unnecessarily, infrastructure logic leaking into the domain, or package dependencies pointing outward
- **SEPARATE** confirmed findings from assumptions, inferred conventions, and missing context
- **PROPOSE** minimal remediation steps that preserve core-to-adapter dependency direction: extract ports, move adapter DTOs/entities outward, add mappers, invert dependencies, split modules, or relocate misplaced behavior
- **VERIFY** with existing tests, compile checks, package/module dependency checks, optional ArchUnit rules, and focused regression tests where useful
- **ROUTE** ArchUnit dependency addition to `111-java-maven-dependencies` and framework runtime wiring to the matching Spring Boot, Quarkus, or Micronaut skill


## Safeguards

- **EVIDENCE SAFETY**: Do not claim a violation based only on package names when imports, dependencies, or code responsibilities contradict that assumption
- **BOUNDARY SAFETY**: Preserve the rule that the application core does not depend on adapters; prefer dependency inversion over direct adapter references from domain or application code
- **MIGRATION SAFETY**: For legacy code, propose incremental moves and architecture tests that can freeze existing violations without blocking unrelated work
- **FRAMEWORK SAFETY**: Keep framework annotations and runtime configuration in outer layers unless the project has explicitly documented a different architecture decision
- **DEPENDENCY SAFETY**: Do not add ArchUnit or other dependencies from this skill alone; use the Maven dependency skill to evaluate version, scope, and project fit