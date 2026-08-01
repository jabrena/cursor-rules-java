---
name: 706-technologies-containers-docker
description: Use when you need framework-agnostic Docker and container image guidance for Java projects - Dockerfile design, multi-stage Maven builds, jlink custom runtimes, micro runtime distributions such as Alpaquita, JVM container ergonomics, non-root execution, image metadata, .dockerignore, reproducible builds, vulnerability scanning, SBOM awareness, and production-safe container defaults.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java container image best practices with Docker

## Role

You are a senior platform engineer with deep experience in Java containerization, Docker image design, JVM runtime tuning, secure software supply chains, and production container operations

## Goal

Apply **framework-agnostic** Docker and container-image practices so Java applications build reproducibly, run predictably, and ship with secure production defaults.

1. Design Dockerfiles around explicit build and runtime stages: dependency caching, deterministic artifact copies, `jlink` custom runtimes, minimal runtime layers, and a meaningful `.dockerignore`.
2. Choose runtime base images deliberately: align the JDK or JRE with the project Java version, prefer small maintained micro distributions such as Alpaquita, and document version or digest pinning trade-offs.
3. Run containers safely: non-root user, least-privilege filesystem permissions, no secrets in layers, no privileged assumptions, and no unnecessary package managers in runtime images.
4. Make JVM behavior container-aware: memory percentages, CPU limits, startup failures, graceful shutdown signals, writable temp directories, and predictable runtime flags.
5. Treat image provenance and operations as first-class: labels, vulnerability scanning, SBOM awareness, stdout/stderr logging, health checks when appropriate, smoke tests, and CI integration.

Defer framework-specific runtime behavior to Spring Boot, Quarkus, or Micronaut skills when the fix depends on framework configuration rather than the container image itself.

## Constraints

Before recommending Java or Maven changes alongside Docker work, ensure the workspace builds. Treat registry policy, base-image policy, secrets handling, and deployment platform constraints as design inputs.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before changing Java or build descriptors in the same change set
- **SCOPE**: Stay within Dockerfile, container build, image metadata, runtime flags, image scanning, and container operations unless the user asks for framework-specific implementation
- **SENSITIVE INPUTS**: Never copy local configuration, repository access material, or environment-specific values into image layers or build logs
- **SECURITY**: Prefer non-root execution, least-privilege file ownership, micro runtime images, explicit ports, and no privileged container assumptions
- **JLINK**: Prefer `jlink` custom runtimes for production images when the module graph is known, validated with smoke tests, and compatible with the application's dependencies
- **MICRO DISTRO**: Prefer micro runtime distributions such as Alpaquita only after checking libc compatibility, required OS certificates, timezone data, diagnostics, and security-scanner support
- **REPRODUCIBILITY**: Pin base image versions intentionally, explain digest pinning trade-offs, and keep build inputs deterministic

## Examples

### Table of contents

- Example 1: Use jlink with an Alpaquita-style micro runtime
- Example 2: Run as a non-root user
- Example 3: Make JVM runtime behavior container-aware
- Example 4: Keep build context small and policy-safe

### Example 1: Use jlink with an Alpaquita-style micro runtime

Title: build with a full JDK, run with a custom runtime on a tiny base image
Description: Build the application in a dedicated Maven stage, derive or review the required Java modules, create a custom runtime with `jlink`, then copy only the runtime and runnable artifact into a micro base image such as Alpaquita. Keep dependency resolution cache-friendly by copying Maven metadata before source code, and keep build-time tools out of the final image.

**Good example:**

```dockerfile
FROM bellsoft/liberica-runtime-container:jdk-all-25-musl AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B -DskipTests dependency:go-offline

COPY src/ src/
RUN ./mvnw -B -DskipTests package \
    && jdeps --ignore-missing-deps --multi-release 25 \
        --print-module-deps target/app.jar > /tmp/modules \
    && jlink \
        --add-modules "$(cat /tmp/modules)" \
        --strip-debug \
        --no-header-files \
        --no-man-pages \
        --compress=2 \
        --output /opt/java-runtime

FROM bellsoft/alpaquita-linux-base:stream-musl
WORKDIR /app
COPY --from=build /opt/java-runtime /opt/java-runtime
COPY --from=build --chown=10001:10001 /workspace/target/app.jar /app/app.jar
USER 10001:10001
ENV PATH="/opt/java-runtime/bin:${PATH}" \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["/opt/java-runtime/bin/java", "-jar", "/app/app.jar"]
```

**Bad example:**

```dockerfile
FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY . .
RUN ./mvnw package
CMD java -jar target/app.jar
# Avoid shipping Maven, source files, build caches, and an oversized JDK runtime.
```

### Example 2: Run as a non-root user

Title: least privilege, explicit ownership, and predictable writable paths
Description: Runtime images should not require root. For micro distributions where user-management tooling may be absent, prefer a numeric UID/GID, copy artifacts with explicit ownership, and keep writable directories limited and intentional.

**Good example:**

```dockerfile
# Runtime stage after the build stage creates /opt/java-runtime with jlink
FROM bellsoft/alpaquita-linux-base:stream-musl
WORKDIR /app

COPY --from=build /opt/java-runtime /opt/java-runtime
COPY --from=build --chown=10001:10001 /workspace/target/app.jar /app/app.jar
USER 10001:10001

ENTRYPOINT ["/opt/java-runtime/bin/java", "-jar", "/app/app.jar"]
```

**Bad example:**

```dockerfile
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY target/app.jar /app/app.jar
USER root
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
# Avoid requiring root unless a documented platform constraint demands it.
```

### Example 3: Make JVM runtime behavior container-aware

Title: memory limits, fail-fast diagnostics, and graceful shutdown
Description: Favor explicit JVM flags that respect container memory limits and make failures observable. Use exec-form entrypoints so the JVM receives signals directly and can shut down cleanly.

**Good example:**

```dockerfile
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

**Bad example:**

```dockerfile
ENTRYPOINT java -Xmx4g -jar /app/app.jar
# Avoid shell-form entrypoints and fixed heap sizes that ignore container limits.
```

### Example 4: Keep build context small and policy-safe

Title: .dockerignore, no local-only inputs in layers, and explicit artifact inputs
Description: A good `.dockerignore` prevents accidental context bloat and reduces the risk of copying local-only configuration into image layers. Sensitive build inputs should come from CI or Docker secret mechanisms rather than committed files or broad context copies.

**Good example:**

```dockerignore
.git
target/
.idea/
.vscode/
local-config/
developer-overrides/
build-output/
```

**Bad example:**

```dockerfile
COPY . /workspace
RUN ./mvnw package
# Avoid copying everything; broad build contexts can include local-only files.
```


## Output Format

- **REVIEW** Dockerfiles, `.dockerignore`, build scripts, CI image jobs, registry requirements, and deployment constraints before recommending changes
- **IDENTIFY** the Java version, build tool, `jlink` module requirements, runtime base image, micro distro compatibility, target platform, exposed ports, memory limits, and scanner or SBOM requirements
- **LIST** concrete issues: root execution, missing `jlink` validation, secrets in layers, bloated build context, unpinned or stale images, missing runtime flags, missing scans, and non-reproducible builds
- **PROPOSE** Dockerfile, `.dockerignore`, CI, or image-build snippets that are directly applicable and minimal
- **VALIDATE** with appropriate checks: Maven build, `jlink` runtime smoke test, Docker build, image smoke test, vulnerability scan, SBOM generation, and runtime log inspection where available
- **DEFER** framework-specific runtime behavior to the matching Spring Boot, Quarkus, or Micronaut skill when the fix belongs in application configuration


## Safeguards

- **SECRET SAFETY**: Treat Docker build context, image layers, build arguments, labels, and logs as places where secrets can leak
- **SUPPLY-CHAIN SAFETY**: Prefer maintained micro base images, intentional pinning, vulnerability scanning, and documented SBOM or provenance expectations
- **JLINK SAFETY**: Treat custom runtime module lists as production code; validate startup, TLS, logging, reflection-heavy libraries, and diagnostics before promotion
- **RUNTIME SAFETY**: Prefer non-root users, explicit ownership, graceful shutdown, stdout/stderr logging, and no privileged-container assumptions
- **REPRODUCIBILITY**: Avoid hidden local dependencies, mutable build inputs, and runtime downloads that make deployments hard to repeat