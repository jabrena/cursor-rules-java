---
name: 152-java-performance-gatling
description: Use when you need to set up Gatling performance testing for a Java Maven project — including adding Gatling dependencies and the Gatling Maven plugin, creating Java simulations, running gatling:test, configuring a simulation class, and reviewing generated reports.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Run performance tests based on Gatling

## Role

You are a Senior software engineer with extensive experience in Java software development, Maven, and performance testing with Gatling

## Goal

When a user requests Gatling performance testing setup, provide a Maven-based Gatling solution for Java simulations, including build configuration, simulation structure, execution commands, and report verification.

## Constraints

Prerequisites and requirements that must be met before proceeding with Gatling performance testing setup

- Verify that the project is Maven-based before applying this skill
- Prefer the Maven wrapper (`./mvnw`) when it exists in the project root
- Use the project's existing dependency and plugin version-management style
- If no Gatling version convention exists, ask before pinning versions or use explicit placeholders that require replacement
- Do not invent unsupported Gatling Maven plugin options; use `gatling:help` for available options
- Do not edit generated reports or generated build output directly

## Steps

### Step 1: Inspect the Project

When a user requests Gatling performance testing setup:

1. Check whether `pom.xml` exists in the project root.
2. Check whether the Maven wrapper exists (`./mvnw` or `mvnw.cmd`).
3. Inspect existing dependency management, plugin management, and test dependencies.
4. Search for existing Gatling simulations or configuration before adding new files.

**Required Maven Project Structure:**
```
project-root/
├── pom.xml
├── mvnw                                      # Preferred when present
├── src/test/java/
│   └── .../ExampleSimulation.java            # Java Gatling simulation
├── src/test/resources/
│   ├── gatling.conf                          # Optional Gatling configuration
│   ├── logback-test.xml                      # Optional logging override
│   └── data/                                 # Optional feeders or request bodies
└── target/
└── gatling/                              # Generated reports
```
### Step 2: Add Maven Configuration

Add Gatling with the project's existing Maven style. Prefer properties or dependency management when the project already centralizes versions.

**Dependency:**
```xml
<dependency>
    <groupId>io.gatling.highcharts</groupId>
    <artifactId>gatling-charts-highcharts</artifactId>
    <version>MANUALLY_REPLACE_WITH_LATEST_VERSION</version>
    <scope>test</scope>
</dependency>
```

**Plugin:**
```xml
<plugin>
    <groupId>io.gatling</groupId>
    <artifactId>gatling-maven-plugin</artifactId>
    <version>MANUALLY_REPLACE_WITH_LATEST_VERSION</version>
</plugin>
```

If the user wants Gatling bound to the Maven lifecycle, configure an execution for the `test` goal. By default, the Gatling Maven plugin binds the `test` goal to the `integration-test` phase when configured with an execution.
            ### Step 3: Create Java Simulation

                Create simulations as Java test sources under `src/test/java` using a package that matches the project conventions.

                **Complete Java Simulation Example:**
```java
package com.example.performance;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class ExampleSimulation extends Simulation {

    // Configurable base URL
    private static final String BASE_URL = System.getProperty("gatling.baseUrl", "http://localhost:8080");

    // HTTP protocol configuration
    HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Scenario definition
    ScenarioBuilder exampleScenario = scenario("Example Load Test")
            .exec(
                    http("GET /api/example")
                            .get("/api/example")
                            .check(status().is(200))
            );

    // Load injection profile
    {
        setUp(
                exampleScenario.injectOpen(
                        rampUsers(10).during(Duration.ofSeconds(10)),  // Ramp up to 10 users
                        constantUsersPerSec(2).during(Duration.ofSeconds(30)) // 2 users/sec for 30s
                ).protocols(httpProtocol)
        ).assertions(
                global().responseTime().max().lt(2000), // Max response time under 2 seconds
                global().successfulRequests().percent().gt(95.0) // 95% success rate
        );
    }
}
```

                **Simulation Guidance:**
                - Use descriptive simulation class names ending with `Simulation`
                - **CRITICAL**: Import `java.time.Duration` for time specifications
                - **INJECTION PROFILES**: Use `constantUsersPerSec()` not `constantUsers()`, `rampUsers().during(Duration.ofSeconds())` for proper timing
                - Keep base URLs configurable through system properties or environment variables when appropriate
                - Use explicit scenario names that describe the user journey under load
                - Define injection profiles that match the requested load model
                - Add assertions only when the expected latency, success rate, or throughput target is clear
                - Keep feeders and request body templates under `src/test/resources`

                **Common Compilation Issues:**
                - Missing import: `import java.time.Duration;`
                - Wrong method: `constantUsers()` → use `constantUsersPerSec()` instead
                - Duration syntax: Use `Duration.ofSeconds(30)` not just `30`
            ### Step 4: Run Gatling Tests

**Basic Usage:**
```bash
# Run with the Maven wrapper
./mvnw gatling:test

# Run with a specific simulation class
./mvnw gatling:test -Dgatling.simulationClass=com.example.performance.ExampleSimulation

# Inspect available options for the test goal
./mvnw gatling:help -Ddetail=true -Dgoal=test
```

If there is exactly one simulation, Gatling can run it directly. If multiple simulations exist, pass `-Dgatling.simulationClass=<FullyQualifiedClassName>` or configure the plugin with a simulation class for repeatable non-interactive execution.
### Step 5: Review Reports

After execution, read the command output to locate the generated Gatling report. Reports are generated under the Maven build output, typically below `target/gatling/`.

**Report Review Checklist:**
- Confirm the simulation completed without build failures
- Check failed requests and error distributions
- Review response time percentiles and throughput
- Review assertion failures when assertions are configured
- Summarize findings and recommend follow-up load profiles only when supported by the observed result


## Output Format

- Summarize the Maven changes applied to `pom.xml`
- List created or updated Gatling simulation and resource files
- Provide the exact command used to run Gatling tests
- Point to the generated report location shown by Gatling
- Summarize failed assertions, errors, or notable performance findings


## Safeguards

- **PROJECT TYPE CHECK**: Stop with guidance if the project is not Maven-based
- **VERSION MANAGEMENT**: Follow existing Maven version-management conventions instead of scattering hard-coded versions
- **NON-INTERACTIVE RUNS**: Use `-Dgatling.simulationClass=<FullyQualifiedClassName>` when multiple simulations would make execution ambiguous
- **CONFIGURATION SAFETY**: Place `gatling.conf` in `src/test/resources` because Gatling resolves it from the classpath
- **REPORT SAFETY**: Treat `target/gatling/` as generated output and do not edit reports directly
- **VALIDATION**: Run `./mvnw gatling:test` or the closest project-specific Maven verification command after setup when feasible