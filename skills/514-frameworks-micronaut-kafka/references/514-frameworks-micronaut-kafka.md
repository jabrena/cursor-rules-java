---
name: 514-frameworks-micronaut-kafka
description: Use when you need Kafka in Micronaut — including Maven dependency and annotation processor, @Serdeable event records, @KafkaClient typed producers, @KafkaListener consumers with OffsetStrategy and ErrorStrategyValue, dead-letter routing, idempotency, and integration testing with @MicronautTest, TestPropertyProvider, and Testcontainers. This should trigger for requests such as Add Kafka in Micronaut; Review Micronaut Kafka listeners; Improve retry and failure handling for Micronaut Kafka.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Micronaut — Kafka messaging

## Role

You are a Senior software engineer with extensive experience in Micronaut and Apache Kafka

## Goal

Design and implement reliable Kafka messaging in Micronaut using the `micronaut-kafka` integration. Declare producers as `@KafkaClient` interfaces and consumers as `@KafkaListener` classes. Keep listener methods thin — delegate to `@Singleton` services. Configure `errorStrategy` so processing failures trigger retries or dead-letter routing instead of being silently consumed.

**Guidance tiers**

- **Minimal** (REST-triggered domain events, examples, internal topics): typed `@Serdeable` record, `@KafkaClient` producer, `@KafkaListener` consumer; skip `eventId`, DLQ, and idempotency unless required.
- **Production** (billing, orders, money, external consumers): versioned topics, keyed messages, `eventId` de-duplication, DLQ, retries, and monitoring.

**What is covered in this Skill?**

- Maven `micronaut-kafka` dependency and annotation processor path
- JSON serialization with `micronaut-serde-jackson` and `@Serdeable` typed records
- Consumer group and topic naming conventions (`application.yml` / `application.properties`)
- Typed event records; `eventId` and versioned topics for production events
- `@KafkaClient` interface-based typed producer with `@KafkaKey` and a thin `@Singleton` publisher wrapper
- `@KafkaListener` consumer with `@Topic`, `@OffsetStrategy`, and typed payload
- `errorStrategy` with `ErrorStrategyValue.RETRY_ON_ERROR`, `RETRY_EXPONENTIALLY_ON_ERROR`, and `RESUME_AT_NEXT_RECORD` — trade-offs
- Dead-letter routing via a producer called from an exception handler
- Idempotent consumer pattern using `eventId` (production tier)
- Integration testing with `@MicronautTest`, `TestPropertyProvider`, Testcontainers Kafka, and Maven Failsafe for `*IT` classes
- Unit-test isolation without a broker (`application-test.properties`, `@Requires`, optional client)

**Scope:** Apply recommendations based on the reference rules and good/bad code examples. For HTTP + Testcontainers wiring in `*IT` classes, cross-reference `@522-frameworks-micronaut-testing-integration-tests`.

## Constraints

Before applying any Kafka changes, ensure the project compiles. Compilation failure is a BLOCKING condition.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **SAFETY**: If compilation fails, stop immediately
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements
- **INJECTION**: Never construct topic names or Kafka header values from untrusted user input
- **CONTAINER IMAGE SAFETY**: Use only organization-approved Testcontainers images from trusted build configuration; prefer digest-pinned internal registry images for Kafka integration tests
- **SERIALIZATION**: Prefer `@Serdeable` typed records with `micronaut-serde-jackson`; use explicit `JsonObjectSerde` only for untyped or advanced serde paths
- **BEFORE APPLYING**: Read the reference for detailed rules and good/bad patterns
- **EDGE CASE**: If the user goal is ambiguous, stop and ask a clarifying question before editing files

## Examples

### Table of contents

- Example 1: Maven dependency
- Example 2: Event schema design
- Example 3: Kafka configuration
- Example 4: JSON serialization
- Example 5: Producer implementation
- Example 6: Consumer implementation
- Example 7: Error handling
- Example 8: Idempotent consumer
- Example 9: Integration test with Testcontainers
- Example 10: Unit test isolation

### Example 1: Maven dependency

Title: Add micronaut-kafka; Micronaut BOM manages the version
Description: Add `micronaut-kafka` to `pom.xml`. The Micronaut BOM manages its version. Avoid pinning the version explicitly — it can create classpath conflicts with the auto-configured Micronaut Kafka factory beans. Register `micronaut-kafka` on `annotationProcessorPaths` so `@KafkaClient` implementations are generated at compile time. For integration tests, add `org.testcontainers:junit-jupiter` and `org.testcontainers:kafka` in `test` scope. When the Micronaut parent does not manage Testcontainers versions, import `testcontainers-bom`. Configure Maven Surefire for `*Test` and Failsafe for `*IT` / `*AT` so container-backed tests run during `verify`, not `test`.

**Good example:**

```xml
<!-- pom.xml — Micronaut BOM manages micronaut-kafka version -->
<dependency>
    <groupId>io.micronaut.kafka</groupId>
    <artifactId>micronaut-kafka</artifactId>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>kafka</artifactId>
    <scope>test</scope>
</dependency>

<!-- When Micronaut parent does not manage Testcontainers -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers-bom</artifactId>
            <version>${testcontainers.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths combine.children="append">
            <path>
                <groupId>io.micronaut.kafka</groupId>
                <artifactId>micronaut-kafka</artifactId>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

**Bad example:**

```xml
<!-- Bad: pinned version fights the Micronaut BOM -->
<dependency>
    <groupId>io.micronaut.kafka</groupId>
    <artifactId>micronaut-kafka</artifactId>
    <version>5.3.0</version>
</dependency>

<!-- Bad: @KafkaClient without annotation processor — compile-time client stub missing -->
```

### Example 2: Event schema design

Title: Minimal records for examples; versioned records with eventId for production
Description: For minimal/tutorial flows, a typed record with domain fields is enough (e.g. `SumCalculatedEvent(int param1, int param2, int result)`). For production events consumed by other services or with side effects, add `eventId` (UUID) for de-duplication, `schemaVersion` for forward compatibility, and the aggregate's natural key (e.g. `orderId`) as the Kafka message key. Production topic names follow `domain.entity.operation.v{N}`.

**Good example:**

```java
// Minimal — internal domain notification
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record SumCalculatedEvent(int param1, int param2, int result) { }

// Production — versioned, de-duplicatable, keyed on aggregate id
import java.time.Instant;

@Serdeable
public record OrderCreatedEvent(
    String eventId,
    String schemaVersion,
    String orderId,
    String customerId,
    Instant occurredAt
) {
    public static OrderCreatedEvent of(String orderId, String customerId) {
        return new OrderCreatedEvent(
            java.util.UUID.randomUUID().toString(), "v1",
            orderId, customerId, Instant.now()
        );
    }
}
// topic: orders.created.v1   key: orderId
```

**Bad example:**

```java
// Bad: untyped string payload, no schema version, no stable message key
String event = "{ type: orderCreated }";
```

### Example 3: Kafka configuration

Title: Bootstrap servers, consumer group, and placeholder defaults
Description: Configure the Kafka broker and consumer group in `application.yml` or `application.properties`. Use a unique, versioned `groupId` per consumer (e.g. `billing-service-v1`) so different service instances share workload without accidentally consuming events intended for another service. When the default bootstrap value contains a colon (`localhost:9092`), wrap it in backticks in Micronaut property placeholders — otherwise `${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}` parses the default as `localhost` and leaves `9092` as an invalid bootstrap URL. For typed `@KafkaClient` / `@KafkaListener` payloads with `@Serdeable` records, Micronaut Serde handles JSON automatically — explicit `JsonObjectSerde` deserializers are only needed for untyped payloads.

**Good example:**

```properties
# application.properties
kafka.bootstrap.servers=${KAFKA_BOOTSTRAP_SERVERS:`localhost:9092`}
app.kafka.messaging.enabled=${APP_KAFKA_MESSAGING_ENABLED:true}

# application.yml equivalent:
# kafka.bootstrap.servers: ${KAFKA_BOOTSTRAP_SERVERS:`localhost:9092`}
```

**Bad example:**

```properties
# Bad: default parses as "localhost"; bootstrap becomes "9092"
kafka.bootstrap.servers=${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}

# Bad: custom flags under kafka.* may leak into Kafka AdminClient config
kafka.messaging.enabled=false
```

### Example 4: JSON serialization

Title: @Serdeable records with micronaut-serde-jackson
Description: Annotate Kafka event records with `@Serdeable` and ensure `micronaut-serde-jackson` is on the classpath. Micronaut generates serializers/deserializers for typed `@KafkaClient` and `@KafkaListener` method parameters — no manual `ProducerFactory` / `ConsumerFactory` beans required for JSON events.

**Good example:**

```java
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record SumCalculatedEvent(int param1, int param2, int result) { }

// @KafkaClient and @KafkaListener accept SumCalculatedEvent directly
```

**Bad example:**

```java
// Bad: plain record without @Serdeable — serde may fail at runtime
public record SumCalculatedEvent(int param1, int param2, int result) { }
```

### Example 5: Producer implementation

Title: @KafkaClient interface plus @Singleton publisher wrapper
Description: Declare the producer as a `@KafkaClient` interface — Micronaut generates the implementation at compile time. Annotate the key parameter with `@KafkaKey` so the Kafka record key is set explicitly, ensuring all events for the same aggregate land in the same partition and are consumed in order. Keep HTTP resources/controllers free of Kafka annotations — inject a thin `@Singleton` publisher that delegates to the client and derives the key from domain fields. Gate the client with `@Requires` when messaging should be disabled in unit tests.

**Good example:**

```java
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Requires(property = "app.kafka.messaging.enabled", value = "true")
@KafkaClient
public interface SumCalculatedEventClient {

    @Topic(SumEventPublisher.TOPIC)
    void publish(@KafkaKey String key, SumCalculatedEvent event);
}

@Singleton
public class SumEventPublisher {

    public static final String TOPIC = "sum-calculated";

    private final SumCalculatedEventClient eventClient;

    SumEventPublisher(@jakarta.annotation.Nullable SumCalculatedEventClient eventClient) {
        this.eventClient = eventClient;
    }

    public void publish(SumCalculatedEvent event) {
        if (eventClient == null) {
            return;
        }
        String key = event.param1() + "+" + event.param2();
        eventClient.publish(key, event);
    }
}
```

**Bad example:**

```java
@KafkaClient
interface OrderEventsClient {
    @Topic("orders.created.v1")
    void send(OrderCreatedEvent event); // Bad: no @KafkaKey → null key → no partition ordering
}

// Bad: @KafkaClient injected directly into @Controller — couples HTTP to messaging API
```

### Example 6: Consumer implementation

Title: @KafkaListener with typed payload; thin method delegating to a service
Description: Annotate the class with `@KafkaListener` and specify the `groupId`. Accept the typed event record as the parameter. Inject and delegate immediately to an application service — keep the listener method as thin as a dispatcher. Use `@OffsetStrategy(SYNC)` to commit offsets synchronously after successful processing, reducing duplicate delivery on restart. Gate the listener with `@Requires` when Kafka consumers should not start in fast unit tests.

**Good example:**

```java
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetStrategy;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Inject;

@Requires(property = "app.kafka.messaging.enabled", value = "true")
@KafkaListener(groupId = "sum-calculated-consumer", offsetStrategy = OffsetStrategy.SYNC)
public class SumCalculatedEventListener {

    @Inject
    LastSumCalculatedEventStore eventStore;

    @Topic("sum-calculated")
    void onSumCalculated(@KafkaKey String key, SumCalculatedEvent event) {
        eventStore.store(event);
    }
}
```

**Bad example:**

```java
@KafkaListener
class RawListener {
    @Topic("orders.created.v1")
    void receive(String rawJson) {
        // Bad: untyped payload, no groupId (random group on each restart),
        // full business logic inlined
    }
}
```

### Example 7: Error handling

Title: errorStrategy RETRY_ON_ERROR with dead-letter routing for unrecoverable failures
Description: Configure `errorStrategy` with `ErrorStrategyValue` so transient failures (network timeouts, temporary DB unavailability) trigger automatic retries. Prefer a bounded strategy such as `RETRY_EXPONENTIALLY_ON_ERROR` with `retryCount` instead of unbounded retry loops. For unrecoverable failures (deserialization errors, permanent business constraint violations) implement a `KafkaListenerExceptionHandler` that publishes the original record to a dead-letter topic and resumes according to the configured strategy. Never swallow exceptions with an empty `catch` block.

**Good example:**

```java
import io.micronaut.configuration.kafka.annotation.ErrorStrategy;
import io.micronaut.configuration.kafka.annotation.ErrorStrategyValue;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetStrategy;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.configuration.kafka.exceptions.KafkaListenerException;
import io.micronaut.configuration.kafka.exceptions.KafkaListenerExceptionHandler;
import jakarta.inject.Inject;

@KafkaListener(
    groupId = "billing-service-v1",
    offsetStrategy = OffsetStrategy.SYNC,
    errorStrategy = @ErrorStrategy(
        value = ErrorStrategyValue.RETRY_EXPONENTIALLY_ON_ERROR,
        retryCount = 3
    )
)
public class BillingEventListener implements KafkaListenerExceptionHandler {

    @Inject BillingService billingService;
    @Inject OrderEventsDeadLetterClient dlqClient;

    @Topic("orders.created.v1")
    void onOrderCreated(@KafkaKey String orderId, OrderCreatedEvent event) {
        billingService.createInvoice(event.orderId(), event.customerId());
    }

    @Override
    public void handle(KafkaListenerException ex) {
        // After retries exhausted: route to DLQ and resume
        ex.getKafkaRecord().ifPresent(record ->
            dlqClient.sendToDlq(record.key().toString(), (OrderCreatedEvent) record.value()));
    }
}

@KafkaClient
interface OrderEventsDeadLetterClient {
    @Topic("orders.created.dlq")
    void sendToDlq(@KafkaKey String key, OrderCreatedEvent event);
}
```

**Bad example:**

```java
@Topic("orders.created.v1")
void onOrderCreated(OrderCreatedEvent event) {
    try {
        billingService.createInvoice(event.orderId(), event.customerId());
    } catch (Exception ignored) {
        // Bad: offset committed, message consumed, error never visible
    }
}
```


### Example 8: Idempotent consumer

Title: De-duplicate on eventId to survive at-least-once redelivery
Description: Kafka at-least-once delivery means a consumer can receive the same message more than once — on retries, after a group rebalance, or from DLQ re-processing. Guard processing with an `eventId` check before calling business logic. In production, back the de-dup store with a database or Redis. In tests, an in-memory `ConcurrentHashMap` is sufficient.

**Good example:**

```java
import io.micronaut.configuration.kafka.annotation.ErrorStrategy;
import io.micronaut.configuration.kafka.annotation.ErrorStrategyValue;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetStrategy;
import io.micronaut.configuration.kafka.annotation.Topic;
import jakarta.inject.Inject;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@KafkaListener(
    groupId = "billing-service-v1",
    offsetStrategy = OffsetStrategy.SYNC,
    errorStrategy = @ErrorStrategy(
        value = ErrorStrategyValue.RETRY_EXPONENTIALLY_ON_ERROR,
        retryCount = 3
    )
)
public class IdempotentBillingListener {

    private final Set<String> processedIds = ConcurrentHashMap.newKeySet();

    @Inject BillingService billingService;

    @Topic("orders.created.v1")
    void onOrderCreated(@KafkaKey String orderId, OrderCreatedEvent event) {
        if (!processedIds.add(event.eventId())) {
            return; // duplicate — skip without error
        }
        billingService.createInvoice(event.orderId(), event.customerId());
    }
}
```

**Bad example:**

```java
@Topic("orders.created.v1")
void onOrderCreated(OrderCreatedEvent event) {
    // Bad: no de-duplication — rebalance or retry causes double billing
    billingService.createInvoice(event.orderId(), event.customerId());
}
```


### Example 9: Integration test with Testcontainers

Title: TestPropertyProvider wires Kafka bootstrap servers before startup
Description: For `*IT` classes that POST to HTTP and assert a `@KafkaListener` received an event, start a `KafkaContainer` with Testcontainers and wire `kafka.bootstrap.servers` through `TestPropertyProvider.getProperties()` before Micronaut boots. Implement `TestPropertyProvider` on the test class and annotate it with `@TestInstance(Lifecycle.PER_CLASS)` — Micronaut requires this lifecycle when using dynamic test properties. Use `@MicronautTest` with `@Inject @Client("/") HttpClient` for the HTTP call and Awaitility for async consumer assertions against an in-memory store. Do not use Spring Boot patterns (`@ServiceConnection`, `@DynamicPropertySource`) — see `@522-frameworks-micronaut-testing-integration-tests`. Name integration test classes with the `IT` suffix and run them with Maven Failsafe. Do not hard-code public Docker Hub images in reusable guidance. Resolve the Kafka image from a trusted build property or helper owned by the project, and configure that property in CI to an organization-approved image, preferably pinned by digest in an internal registry.

**Good example:**

```java
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import java.time.Duration;
import java.util.Map;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SumControllerIT implements TestPropertyProvider {

    @Container
    static KafkaContainer kafka =
        new KafkaContainer(approvedKafkaImage());

    private static DockerImageName approvedKafkaImage() {
        return DockerImageName.parse(System.getProperty("test.kafka.image"));
    }

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    LastSumCalculatedEventStore eventStore;

    @Override
    public Map<String, String> getProperties() {
        if (!kafka.isRunning()) {
            kafka.start();
        }
        return Map.of(
            "kafka.bootstrap.servers", kafka.getBootstrapServers(),
            "app.kafka.messaging.enabled", "true");
    }

    @BeforeEach
    void setUp() {
        eventStore.clear();
    }

    @Test
    void sumPublishesSumCalculatedEvent() {
        HttpResponse<SumResponse> response = client.toBlocking().exchange(
            HttpRequest.POST("/api/v1/sum", "{\"param1\": 10, \"param2\": 32}")
                .contentType(MediaType.APPLICATION_JSON),
            SumResponse.class);

        assertEquals(200, response.getStatus().getCode());
        assertEquals(42, response.body().result());

        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            SumCalculatedEvent event = eventStore.getLastEvent();
            assertNotNull(event);
            assertEquals(42, event.result());
        });
    }
}
```

**Bad example:**

```java
@MicronautTest
class SumControllerIT {

    static KafkaContainer kafka = new KafkaContainer(approvedKafkaImage());

    private static DockerImageName approvedKafkaImage() {
        return DockerImageName.parse(System.getProperty("test.kafka.image"));
    }

    @BeforeAll
    static void startContainer() {
        kafka.start(); // too late — Micronaut already chose its Kafka config
        System.setProperty("kafka.bootstrap.servers", kafka.getBootstrapServers());
    }

    @Test
    void sumPublishesSumCalculatedEvent() { }
}

// Bad: implements TestPropertyProvider without @TestInstance(Lifecycle.PER_CLASS)
```


### Example 10: Unit test isolation

Title: Keep @MicronautTest unit tests fast without a broker
Description: Fast `*Test` classes should not require Docker or a running Kafka broker. Disable messaging in the test environment, gate `@KafkaClient` and `@KafkaListener` beans with `@Requires(property = "app.kafka.messaging.enabled", value = "true")`, and inject an optional `@Nullable` client into the publisher so POST handlers still succeed when the client bean is absent. Use `application-test.properties` for test-only overrides — Micronaut 5 rejects duplicate `application.properties` on the classpath (main + test). Enable messaging only in `*IT` classes via `TestPropertyProvider`.

**Good example:**

```java
// src/test/resources/application-test.properties
// app.kafka.messaging.enabled=false

@Requires(property = "app.kafka.messaging.enabled", value = "true")
@KafkaClient
public interface SumCalculatedEventClient { /* ... */ }

@Singleton
public class SumEventPublisher {
    private final SumCalculatedEventClient eventClient;

    SumEventPublisher(@Nullable SumCalculatedEventClient eventClient) {
        this.eventClient = eventClient;
    }

    public void publish(SumCalculatedEvent event) {
        if (eventClient == null) {
            return;
        }
        eventClient.publish(keyFor(event), event);
    }
}
```

**Bad example:**

```properties
# src/test/resources/application.properties — Bad in Micronaut 5:
app.kafka.messaging.enabled=false

# Duplicate resource name 'application.properties' on classpath (main + test)
```


## Output Format

- **ANALYZE** Kafka code: guidance tier (minimal vs production), `@Serdeable` coverage, consumer group isolation, producer key strategy, listener offset/error strategy, test wiring, and idempotency guards
- **CATEGORIZE** issues by impact (RELIABILITY for missing retries/DLQ, CORRECTNESS for missing idempotency or wrong offset strategy, MAINTAINABILITY for untyped payloads or inline business logic, SECURITY for user-controlled topic names, FLAKINESS for wrong test bootstrap wiring)
- **APPLY** Micronaut Kafka–aligned fixes: typed `@KafkaClient`, `@KafkaKey` producers, `@Singleton` publisher wrapper, versioned `groupId`, `SYNC` offset strategy, bounded `ErrorStrategyValue` retry configuration + DLQ exception handler, eventId de-duplication for production consumers
- **IMPLEMENT** changes so configuration bindings, `@Serdeable` records, producer interfaces, listener classes, and tests stay consistent
- **EXPLAIN** trade-offs (minimal vs production event shape, SYNC vs ASYNC offset strategy, retry strategies vs `RESUME_AT_NEXT_RECORD`, Testcontainers vs broker-less unit tests)
- **TEST** with `@MicronautTest` + Testcontainers Kafka in Failsafe `*IT` classes; use `application-test.properties` and `@Requires` for broker-less `*Test` classes; never mock `@KafkaClient` in integration tests meant to verify messaging behaviour
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after changes


## Safeguards

- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` before ANY Kafka refactoring — compilation failure is a HARD STOP
- **CRITICAL VALIDATION**: Run `./mvnw clean verify` after changes; exercise listener integration tests before promoting
- **SERIALIZATION DEFAULT**: Annotate Kafka event records with `@Serdeable`; ensure `micronaut-serde-jackson` is on the classpath for typed JSON payloads
- **INJECTION SAFETY**: Never construct topic names or Kafka header values from untrusted user input
- **CONTAINER IMAGE SAFETY**: Do not hard-code public Testcontainers images in reusable guidance; resolve Kafka images from trusted CI or project configuration and prefer digest-pinned internal registry references
- **PROPERTY PLACEHOLDERS**: When a Micronaut default value contains `:`, wrap it in backticks (e.g. `` `localhost:9092` ``) — bare colons split the default incorrectly
- **CUSTOM FLAGS**: Keep application toggles outside the `kafka.*` namespace (e.g. `app.kafka.messaging.enabled`) so they are not forwarded to Kafka client configuration
- **ERROR HANDLING**: Never swallow exceptions inside `@KafkaListener` methods — propagate so the configured `errorStrategy` can retry or route to DLQ
- **IDEMPOTENCY**: De-duplicate on `eventId` for production consumers with side effects — at-least-once delivery means duplicates are possible
- **GROUP ID**: Use a unique, versioned `groupId` per consumer class — a shared `groupId` across unrelated services causes accidental competitive consumption
- **DLQ MONITORING**: Set up alerting on the DLQ topic — messages landing there indicate systematic processing failures
- **TEST WIRING**: Wire Kafka bootstrap servers through `TestPropertyProvider.getProperties()` — not `@BeforeAll` or `System.setProperty` after Micronaut starts; use `@TestInstance(Lifecycle.PER_CLASS)` when implementing `TestPropertyProvider`
- **TEST RESOURCES**: Use `application-test.properties` for test overrides — do not duplicate `application.properties` in `src/test/resources` (Micronaut 5 fails on duplicate config resources)
- **INCREMENTAL SAFETY**: Change one producer or consumer surface at a time; verify with `@MicronautTest` between steps