---
name: 305-frameworks-spring-boot-modulith
description: Use when you need to design, review, or improve modular monoliths with Spring Modulith in Spring Boot applications - including module package structure, ApplicationModules verification, named interfaces, allowed dependencies, domain events, @ApplicationModuleTest, Scenario-based module tests, generated documentation, actuator exposure, observability, and event publication registry choices.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Spring Boot - Spring Modulith

## Role

You are a Senior software engineer with extensive experience in Spring Boot, modular monolith architecture, domain-driven design, and Spring Modulith

## Goal

Apply Spring Modulith as an architectural fitness tool for Spring Boot modular monoliths. Keep business capabilities in explicit application modules, expose small module APIs, hide implementation details in internal packages, verify module dependencies with `ApplicationModules`, prefer domain events over direct cross-module service calls, and test modules with `@ApplicationModuleTest`. Use Spring Modulith documentation, actuator, observability, and event publication registry features when they provide concrete operational value.

Official references:
- https://docs.spring.io/spring-modulith/reference/index.html
- https://spring.io/projects/spring-modulith

## Constraints

Before applying recommendations, ensure the Spring Boot project compiles. Spring Modulith changes affect architecture, package boundaries, and test bootstrapping, so failures must be addressed before refactoring. Use a progressive adoption strategy when existing codebases have complex dependencies.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **PREREQUISITE**: Confirm the project is a Spring Boot application; for non-Boot Java modularity use architecture or design skills instead
- **BASELINE**: Align dependency choices with the active Spring Boot 4.0.x baseline and the Spring Modulith compatibility matrix
- **TEST-DEPENDENCIES**: Ensure compatible test dependencies for Spring Boot 4.x (use `spring-boot-starter-test` not `spring-boot-starter-webmvc-test`)
- **PROGRESSIVE**: Apply Spring Modulith in phases: 1) Dependencies + verify(), 2) Package restructure, 3) Module tests, 4) Advanced features
- **SAFETY**: If compilation fails, stop immediately and report the blocking errors
- **MODULE-ISOLATION**: When @ApplicationModuleTest fails due to module boundaries, document the isolation behavior and provide alternative testing strategies
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements; include module structure tests when adding Spring Modulith
- **BOUNDARIES**: Do not use Spring Modulith to hide cyclic, chatty, or anemic module design; use verification failures to refine package boundaries and APIs

## Examples

### Table of contents

- Example 1: Maven coordinates
- Example 2: Spring Boot and Spring Modulith compatibility matrix
- Example 3: Progressive Spring Modulith adoption
- Example 4: Application module package structure
- Example 5: Module boundary verification
- Example 6: Named interfaces and allowed dependencies
- Example 7: Domain events between modules
- Example 8: Application module tests
- Example 9: Documentation, actuator, and observability
- Example 10: Troubleshooting @ApplicationModuleTest failures
- Example 11: Validation commands and verification steps

### Example 1: Maven coordinates

Title: Use the Spring Modulith BOM and add only the modules needed by the application
Description: Spring Modulith is split into feature artifacts. Import the Spring Modulith BOM, then add the core starter and test starter first. Add runtime features such as actuator, observability, or event registry implementations only when the project will use those features. Do not pin old Modulith versions or mix unrelated generations with a Spring Boot 4.0.x application.

**Good example:**

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.modulith</groupId>
            <artifactId>spring-modulith-bom</artifactId>
            <version>${spring-modulith.version}</version>
            <scope>import</scope>
            <type>pom</type>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>org.springframework.modulith</groupId>
        <artifactId>spring-modulith-starter-core</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.modulith</groupId>
        <artifactId>spring-modulith-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Bad example:**

```xml
<!-- Bad: old, pinned, and no dependency-management alignment -->
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-core</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Bad: adding runtime features before the app needs them -->
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-actuator</artifactId>
    <version>1.0.0</version>
</dependency>
```


### Example 2: Spring Boot and Spring Modulith compatibility matrix

Title: Use compatible versions and handle test dependency changes
Description: Spring Boot 4.x introduced changes in test starter packaging and dependency structure. Ensure you use compatible Spring Modulith versions and correct test dependencies. @ApplicationModuleTest requires proper test setup and may need @MockBean for cross-module dependencies.

**Good example:**

```xml
<!-- Spring Boot 4.0.x with Spring Modulith 1.3.x -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.6</version>
</parent>

<properties>
    <spring-modulith.version>1.3.1</spring-modulith.version>
</properties>

<dependencies>
    <!-- Core Spring Boot dependencies -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webmvc</artifactId>
    </dependency>

    <!-- Correct test dependency for Spring Boot 4.x -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Spring Modulith -->
    <dependency>
        <groupId>org.springframework.modulith</groupId>
        <artifactId>spring-modulith-starter-core</artifactId>
        <version>${spring-modulith.version}</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.modulith</groupId>
        <artifactId>spring-modulith-starter-test</artifactId>
        <version>${spring-modulith.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Bad example:**

```xml
<!-- Bad: Wrong test starter for Spring Boot 4.x -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Bad: Incompatible Spring Modulith version -->
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-core</artifactId>
    <version>1.0.0</version>
</dependency>
```


### Example 3: Progressive Spring Modulith adoption

Title: Apply Spring Modulith in phases to minimize risk and complexity
Description: Don't try to modularize everything at once. Start with dependencies and architecture verification, then gradually restructure packages and add advanced features. This approach helps identify issues early and provides stable checkpoints.

**Good example:**

```java
// Phase 1: Add dependencies and basic verification
@Test
void shouldHaveValidModuleStructure() {
    ApplicationModules modules = ApplicationModules.of(Application.class);
    modules.verify(); // Start with this - it will show current module structure
}

// Phase 2: After package restructuring, verify no cycles
@Test
void shouldHaveNoCyclicDependencies() {
    ApplicationModules modules = ApplicationModules.of(Application.class);
    modules.verify(); // Now should pass with clean module boundaries
}

// Phase 3: Add module boundary tests (may need mocking)
@ApplicationModuleTest
class OrderModuleTest {

    @MockBean // Mock external module dependencies when needed
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    @Test
    void shouldProcessOrderInIsolation() {
        // Test module in isolation
    }
}

// Phase 4: Add advanced features like domain events
@Component
class OrderEventHandler {

    @ApplicationModuleListener
    void handle(OrderCompletedEvent event) {
        // Handle cross-module communication via events
    }
}
```

**Bad example:**

```java
// Bad: Trying to do everything at once
@ApplicationModuleTest  // This will likely fail
class EverythingModuleTest {

    @Autowired // Will fail if module boundaries aren't clean
    private ServiceFromAnotherModule service;

    @Test
    void shouldDoEverything() {
        // Too complex, too early
    }
}
```


### Example 4: Application module package structure

Title: Let package boundaries express business capabilities
Description: By default, Spring Modulith treats direct sub-packages of the main application package as application modules. Organize modules by business capability, keep public API types in the module root or named interfaces, and keep implementation details in sub-packages such as `internal`. Avoid technical top-level packages that force every feature to depend on shared service, repository, or mapper buckets.

**Good example:**

```text
src/main/java/com/acme/shop
├── ShopApplication.java
├── catalog
│   ├── CatalogApi.java
│   ├── ProductReserved.java
│   └── internal
│       ├── CatalogService.java
│       └── ProductRepository.java
├── ordering
│   ├── OrderApi.java
│   ├── OrderPlaced.java
│   └── internal
│       ├── OrderService.java
│       └── OrderRepository.java
└── fulfillment
    ├── FulfillmentApi.java
    └── internal
        └── FulfillmentService.java
```

**Bad example:**

```text
src/main/java/com/acme/shop
├── controller
├── service
├── repository
├── dto
└── mapper

# Bad: technical layers become global buckets and hide module ownership.
```


### Example 5: Module boundary verification

Title: Use ApplicationModules.verify() as an architecture test
Description: Add a fast architecture test that builds the module model from the Spring Boot application class and verifies the dependency graph. This catches cycles, illegal access to internal packages, and unplanned coupling before it becomes application design debt.

**Good example:**

```java
import com.acme.shop.ShopApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

    @Test
    void verifiesApplicationModuleBoundaries() {
        ApplicationModules.of(ShopApplication.class).verify();
    }
}
```

**Bad example:**

```java
// Bad: module internals are injected directly from another module.
package com.acme.shop.ordering.internal;

import com.acme.shop.catalog.internal.ProductRepository;
import org.springframework.stereotype.Service;

@Service
class OrderService {
    private final ProductRepository products;

    OrderService(ProductRepository products) {
        this.products = products;
    }
}
```


### Example 6: Named interfaces and allowed dependencies

Title: Expose intentional APIs instead of whole modules
Description: Use package-level annotations when a module needs more than one public surface or when allowed dependencies should be explicit. Named interfaces prevent accidental exposure of every public type in a package. Allowed dependencies make the intended module graph visible and testable.

**Good example:**

```java
// src/main/java/com/acme/shop/catalog/package-info.java
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = { "ordering :: spi" }
)
package com.acme.shop.catalog;

// src/main/java/com/acme/shop/ordering/spi/package-info.java
@org.springframework.modulith.NamedInterface("spi")
package com.acme.shop.ordering.spi;
```

**Bad example:**

```java
// Bad: allow everything because the current design is tangled.
@org.springframework.modulith.ApplicationModule(type = org.springframework.modulith.ApplicationModule.Type.OPEN)
package com.acme.shop.catalog;
```


### Example 7: Domain events between modules

Title: Publish events for decoupling instead of invoking another module's internals
Description: Prefer immutable domain events for cross-module notification when the caller does not need an immediate answer. Use `ApplicationEventPublisher` from the originating module and handle events in interested modules with `@ApplicationModuleListener` or transactional event listeners. Keep event payloads stable, small, and free of persistence entities.

**Good example:**

```java
package com.acme.shop.ordering;

public record OrderPlaced(String orderId, String customerId) {
}

package com.acme.shop.ordering.internal;

import com.acme.shop.ordering.OrderPlaced;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
class OrderService {
    private final ApplicationEventPublisher events;

    OrderService(ApplicationEventPublisher events) {
        this.events = events;
    }

    void placeOrder(String orderId, String customerId) {
        // persist order first
        events.publishEvent(new OrderPlaced(orderId, customerId));
    }
}
```

**Bad example:**

```java
// Bad: ordering reaches into fulfillment internals synchronously for side effects.
import com.acme.shop.fulfillment.internal.FulfillmentService;

class OrderService {
    void placeOrder(Order order) {
        fulfillmentService.reserveWarehouseCapacity(order);
    }
}
```


### Example 8: Application module tests

Title: Use @ApplicationModuleTest to bootstrap one module or its declared dependencies
Description: Module tests should prove a module works through its exposed API while keeping coupling visible. Start with the default standalone mode. Use direct or all dependency bootstrap modes only when the scenario genuinely crosses module boundaries. If a module needs too many mocks for other modules, revisit the design and consider domain events.

**Good example:**

```java
package com.acme.shop.ordering;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;

@ApplicationModuleTest
class OrderingModuleTests {

    @Autowired OrderApi orders;

    @Test
    void placesOrderThroughModuleApi() {
        orders.placeOrder("customer-123");
    }
}
```

**Bad example:**

```java
@SpringBootTest
class OrderingModuleTests {
    // Bad: full application bootstrap hides module boundaries and slows feedback.
}
```


### Example 9: Documentation, actuator, and observability

Title: Add runtime and documentation features deliberately
Description: Spring Modulith can generate architecture documentation from the module model and expose module information through Spring Boot actuator. Use these features when teams need living architecture docs or production visibility into module interactions. Keep actuator exposure behind normal security and management endpoint controls.

**Good example:**

```java
import com.acme.shop.ShopApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModulithDocumentationTests {

    @Test
    void writesModuleDocumentation() {
        var modules = ApplicationModules.of(ShopApplication.class);
        new Documenter(modules).writeDocumentation();
    }
}
```

**Bad example:**

```properties
# Bad: exposing all management endpoints publicly to inspect modulith state.
management.endpoints.web.exposure.include=*
```


### Example 10: Troubleshooting @ApplicationModuleTest failures

Title: Handle module isolation issues and provide alternative testing strategies
Description: @ApplicationModuleTest is designed to load modules in isolation, which may fail when modules have strong coupling. This is expected behavior that reveals architectural issues. Use @MockBean for external dependencies, or fall back to integration tests while improving module boundaries.

**Good example:**

```java
// When @ApplicationModuleTest fails due to missing dependencies
@ApplicationModuleTest
class ApiModuleTest {

    @MockBean // Mock external module dependencies
    private CalculationService calculationService;

    @Autowired
    private SumController controller;

    @Test
    void shouldWorkWithMockedDependencies() {
        when(calculationService.sum(5, 3)).thenReturn(8);

        SumRequest request = new SumRequest(5, 3);
        SumResponse response = controller.sum(request);

        assertThat(response.result()).isEqualTo(8);
    }
}

// Alternative: Full integration test when module isolation isn't ready
@SpringBootTest
class ApiIntegrationTest {

    @Autowired
    private SumController controller;

    @Test
    void shouldWorkWithRealDependencies() {
        SumRequest request = new SumRequest(5, 3);
        SumResponse response = controller.sum(request);

        assertThat(response.result()).isEqualTo(8);
    }
}
```

**Bad example:**

```java
// Bad: Forcing @ApplicationModuleTest to work by making modules @OPEN
@org.springframework.modulith.ApplicationModule(
    type = org.springframework.modulith.ApplicationModule.Type.OPEN
)
package com.example.badmodule;

// This defeats the purpose of module boundaries
```


### Example 11: Validation commands and verification steps

Title: Commands to verify Spring Modulith setup and troubleshoot issues
Description: Use these specific commands to validate Spring Modulith setup, check for issues, and verify the configuration works as expected. These commands help diagnose problems during skill application.

**Good example:**

```bash
# Verify module structure and architecture
./mvnw test -Dtest=*ModuleStructure*

# Generate documentation and verify it's created
./mvnw test -Dtest=*ModuleStructure*
ls target/spring-modulith-docs/

# Check for cyclic dependencies in compilation output
./mvnw compile -X | grep -i "cycle"

# Run only Spring Modulith-related tests
./mvnw test -Dtest=*Module*Test

# Verify full build including all module tests
./mvnw clean verify
```


## Output Format

- **PHASE 1 - VERIFY**: Run `./mvnw compile` to ensure project builds; stop if compilation fails
- **PHASE 1 - DEPENDENCIES**: Add Spring Modulith dependencies compatible with detected Spring Boot version and correct test dependencies for Spring Boot 4.x
- **PHASE 1 - ARCHITECTURE**: Add ApplicationModules.verify() test and validate current module structure
- **PHASE 2 - RESTRUCTURE**: Analyze package structure and create logical business-capability modules with package-info.java documentation
- **PHASE 3 - MODULE-TESTS**: Add @ApplicationModuleTest coverage; if isolation fails due to module boundaries, document why and provide @MockBean or integration test alternatives
- **PHASE 4 - DOCUMENTATION**: Ensure Spring Modulith generates documentation in target/spring-modulith-docs/ with PlantUML diagrams
- **FINAL-VERIFY**: Run `./mvnw clean verify` to ensure all changes work together; provide troubleshooting guidance if tests fail
- **FOLLOW-UP**: Recommend domain events, actuator exposure, observability, and event publication registry based on application needs