---
name: 414-frameworks-quarkus-kafka
description: Use when you need Kafka in Quarkus with SmallRye Reactive Messaging — including Maven extension, channel/topic design, typed @Channel Emitter producers, @Incoming consumers with Uni, build-time Jackson serialization, failure strategies (dead-letter-queue, retry), idempotency, Dev Services, and Testcontainers integration tests. This should trigger for requests such as Add Kafka in Quarkus; Review Reactive Messaging consumers; Improve failure handling for Quarkus Kafka.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Quarkus — Kafka messaging

## Role

You are a Senior software engineer with extensive experience in Quarkus and Apache Kafka

## Goal

Design and implement reliable Kafka messaging in Quarkus using SmallRye Reactive Messaging. Keep channel names and topic names decoupled via configuration. Use typed event records on `Emitter<T>` and `@Incoming` methods so Quarkus generates Jackson serializers at build time. Delegate business logic to CDI services from thin `@Incoming` methods. For production workloads with external consumers or side effects, configure `failure-strategy=dead-letter-queue` and idempotency on `eventId`.

**Guidance tiers**

- **Minimal** (REST-triggered domain events, examples, internal topics): typed record, channel/topic config, `@Channel` producer, `@Incoming` consumer; skip `eventId`, DLQ, and idempotency unless required.
- **Production** (billing, orders, money, external consumers): versioned topics, keyed messages, `eventId` de-duplication, DLQ, retries, and monitoring.

**What is covered in this Skill?**

- Maven `quarkus-messaging-kafka` extension dependency
- Channel and topic naming conventions and configuration (`*-out` / `*-in` channel names)
- Build-time Jackson serialization for typed channels (default on Quarkus 3.x)
- Typed event records; `eventId` and versioned topics for production events
- `@Channel` / `Emitter` typed producer with optional partition key
- `@Incoming` consumer with `Uni<Void>` return type (sync and async paths)
- `failure-strategy`: `dead-letter-queue`, `fail`, and `ignore`; retry via connector properties
- Idempotent consumer pattern using `eventId` (production tier)
- Dev Services for Kafka (zero-config in dev and test)
- Integration testing with `@QuarkusTest`, `@QuarkusTestResource`, and Testcontainers Kafka

**Scope:** Apply recommendations based on the reference rules and good/bad code examples. For broader Quarkus test infrastructure patterns, see `@422-frameworks-quarkus-testing-integration-tests`.

## Constraints

Before applying any Kafka changes, ensure the project compiles. Compilation failure is a BLOCKING condition.

- **MANDATORY**: Run `./mvnw compile` or `mvn compile` before applying any change
- **SAFETY**: If compilation fails, stop immediately
- **VERIFY**: Run `./mvnw clean verify` or `mvn clean verify` after applying improvements
- **INJECTION**: Never construct topic names or header values from untrusted user input
- **SERIALIZATION**: Prefer typed channels and Quarkus build-time Jackson ser/deser; do not set `ObjectMapperDeserializer` in properties unless you provide a compatible custom deserializer
- **BEFORE APPLYING**: Read the reference for detailed rules and good/bad patterns
- **EDGE CASE**: If the user goal is ambiguous, stop and ask a clarifying question before editing files

## Examples

### Table of contents

- Example 1: Maven extension
- Example 2: Event schema design
- Example 3: Channel and topic configuration
- Example 4: Producer implementation
- Example 5: Consumer implementation
- Example 6: Error handling and dead-letter queue
- Example 7: Idempotent consumer
- Example 8: Integration test with Testcontainers

### Example 1: Maven extension

Title: Add quarkus-messaging-kafka; let Quarkus BOM manage the version
Description: Add `quarkus-messaging-kafka` to your `pom.xml`. The Quarkus BOM manages the version — do not pin it. Quarkus Dev Services automatically starts a Kafka broker in dev and test modes when this extension is on the classpath and no `kafka.bootstrap.servers` is configured, so no manual setup is needed for local development.

**Good example:**

```xml
<!-- pom.xml — Quarkus BOM manages the version -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-messaging-kafka</artifactId>
</dependency>
```

**Bad example:**

```xml
<!-- Bad: pinned version fights the Quarkus BOM -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-messaging-kafka</artifactId>
    <version>3.8.1</version>
</dependency>
```

### Example 2: Event schema design

Title: Minimal records for examples; versioned records with eventId for production
Description: For minimal/tutorial flows, a typed record with domain fields is enough (e.g. `SumCalculatedEvent(int param1, int param2, int result)`). For production events consumed by other services or with side effects, add `eventId` (UUID) for de-duplication, `schemaVersion` for forward compatibility, and the aggregate's natural key (e.g. `orderId`) as the Kafka message key. Production topic names follow `domain.entity.operation.v{N}`.

**Good example:**

```java
// Minimal — internal domain notification
public record SumCalculatedEvent(int param1, int param2, int result) { }

// Production — versioned, de-duplicatable, keyed on aggregate id
import java.time.Instant;

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
// Bad: untyped map, no schema version, no aggregate key
Map<String, Object> event = Map.of("type", "order", "data", "...");
```

### Example 3: Channel and topic configuration

Title: Map logical channel names to Kafka topics; let Quarkus generate serializers
Description: Decouple the channel name used in code from the physical Kafka topic name via configuration. Name outgoing channels with a `-out` suffix and incoming channels with `-in`. On Quarkus 3.x, when producer and consumer methods use typed payloads (`Emitter<OrderCreatedEvent>`, `@Incoming ... OrderCreatedEvent`), the build generates Jackson serializers automatically — do not configure `ObjectMapperSerializer` / `ObjectMapperDeserializer` in properties for the default case. Configure explicit serializers only for Avro, Protobuf, Schema Registry, or other custom formats.

**Good example:**

```properties
# application.properties — typed channels; Quarkus generates Jackson ser/deser at build time
mp.messaging.outgoing.orders-out.connector=smallrye-kafka
mp.messaging.outgoing.orders-out.topic=orders.created.v1

mp.messaging.incoming.orders-in.connector=smallrye-kafka
mp.messaging.incoming.orders-in.topic=orders.created.v1
mp.messaging.incoming.orders-in.group.id=billing-service-v1
```

**Bad example:**

```properties
# Bad: ObjectMapperDeserializer in properties needs a no-arg constructor Kafka can invoke;
# startup fails with "Could not find a public no-argument constructor"
mp.messaging.incoming.orders-in.value.deserializer=io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
mp.messaging.incoming.orders-in.value.deserializer.type=com.example.OrderCreatedEvent

# Bad: hardcoded topic string inside a Java annotation — cannot be overridden per environment
@Incoming("orders.created.v1")
void onMessage(OrderCreatedEvent event) { }
```

### Example 4: Producer implementation

Title: @Channel Emitter with keyed Message for partition ordering
Description: Inject `@Channel("orders-out") Emitter&lt;OrderCreatedEvent&gt;` into the CDI bean that needs to publish. Wrap the payload in a `Message` with an `OutgoingKafkaRecordMetadata` to set the partition key so all events for the same aggregate go to the same partition. For minimal events without a natural aggregate key, a derived key (e.g. `param1 + "+" + param2`) is acceptable.

**Good example:**

```java
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

@ApplicationScoped
public class OrderEventPublisher {

    @Inject
    @Channel("orders-out")
    Emitter<OrderCreatedEvent> emitter;

    public void publish(OrderCreatedEvent event) {
        emitter.send(
            Message.of(event)
                .addMetadata(OutgoingKafkaRecordMetadata.<String>builder()
                    .withKey(event.orderId())
                    .build())
        );
    }
}
```

**Bad example:**

```java
@ApplicationScoped
class OrderEventPublisher {
    @Inject @Channel("orders-out")
    Emitter<String> emitter; // Bad: untyped String emitter; no message key set

    void publish(OrderCreatedEvent event) {
        emitter.send(event.toString()); // loses type safety and partition ordering
    }
}
```

### Example 5: Consumer implementation

Title: @Incoming with Uni return; thin handler for sync or async work
Description: The `@Incoming` method should be thin: receive the typed event and delegate to an `@ApplicationScoped` bean. Return `Uni&lt;Void&gt;` — use `Uni.createFrom().voidItem()` for synchronous work (e.g. storing the last event for tests) or delegate to a service that returns `Uni` for asynchronous I/O. Avoid raw JSON `String` payloads and inline business logic.

**Good example:**

```java
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class SumCalculatedEventConsumer {

    @Inject
    LastSumCalculatedEventStore eventStore;

    @Incoming("sum-calculated-in")
    public Uni<Void> onSumCalculated(SumCalculatedEvent event) {
        eventStore.store(event);
        return Uni.createFrom().voidItem();
    }
}

@ApplicationScoped
class BillingEventConsumer {

    @Inject
    BillingService billingService;

    @Incoming("orders-in")
    public Uni<Void> onOrderCreated(OrderCreatedEvent event) {
        return billingService.createInvoice(event.orderId(), event.customerId());
    }
}
```

**Bad example:**

```java
// Bad: asynchronous work is hidden behind a void method;
// business logic inline instead of delegating to a service
@Incoming("orders-in")
void onOrderCreated(String rawJson) {
    // 40 lines of parsing + DB calls + HTTP calls
}
```

### Example 6: Error handling and dead-letter queue

Title: failure-strategy=dead-letter-queue with nack for unrecoverable messages
Description: For production consumers with side effects, configure `failure-strategy=dead-letter-queue` so failed messages are routed to a dedicated DLQ topic instead of stopping the channel or being silently ignored. For transient failures (network, timeout), enable the connector retry options (`retry=true`, bounded attempts, bounded wait) alongside the failure strategy. For deterministic failures (deserialization errors, schema mismatch), use explicit `message.nack(ex)` to route immediately to the DLQ. Minimal/example consumers may omit DLQ until side effects require it.

**Good example:**

```properties
# application.properties — production tier
mp.messaging.incoming.orders-in.failure-strategy=dead-letter-queue
mp.messaging.incoming.orders-in.dead-letter-queue.topic=orders.created.dlq
mp.messaging.incoming.orders-in.retry=true
mp.messaging.incoming.orders-in.retry-attempts=3
mp.messaging.incoming.orders-in.retry-max-wait=10S
mp.messaging.incoming.orders-in.commit-strategy=throttled
```

**Bad example:**

```java
@Incoming("orders-in")
public CompletionStage<Void> onOrderCreated(Message<OrderCreatedEvent> message) {
    try {
        billingService.createInvoice(message.getPayload().orderId(), message.getPayload().customerId());
        return message.ack();
    } catch (Exception e) {
        return message.ack(); // Bad: ack on failure — record consumed and lost silently
    }
}
```

### Example 7: Idempotent consumer

Title: De-duplicate on eventId to survive at-least-once redelivery
Description: Kafka guarantees at-least-once delivery, so production consumers can receive duplicate messages on broker retries, consumer group rebalances, or DLQ re-processing. Guard each consumer method with an `eventId` check against a de-duplication store. In production, back the store with a database or Redis; in tests, an in-memory `ConcurrentHashMap` is sufficient. Skip this pattern for minimal fire-and-forget domain notifications without side effects.

**Good example:**

```java
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class BillingEventConsumer {

    private final Set<String> processedIds = ConcurrentHashMap.newKeySet();

    @Inject BillingService billingService;

    @Incoming("orders-in")
    public Uni<Void> onOrderCreated(OrderCreatedEvent event) {
        if (!processedIds.add(event.eventId())) {
            return Uni.createFrom().voidItem(); // duplicate — skip
        }
        return billingService.createInvoice(event.orderId(), event.customerId());
    }
}
```

**Bad example:**

```java
@Incoming("orders-in")
public Uni<Void> onOrderCreated(OrderCreatedEvent event) {
    // Bad for billing side effects: no idempotency guard — rebalance or DLQ re-play doubles billing
    return billingService.createInvoice(event.orderId(), event.customerId());
}
```

### Example 8: Integration test with Testcontainers

Title: @QuarkusTestResource wires Kafka bootstrap servers before startup
Description: Use Dev Services for zero-config `@QuarkusTest` runs when no `kafka.bootstrap.servers` is set. When the spec or CI requires an explicit broker, start a `KafkaContainer` via `QuarkusTestResourceLifecycleManager` and return `kafka.bootstrap.servers` (and per-channel bootstrap overrides if needed) from `start()` before Quarkus boots. Assert async delivery with Awaitility against an in-memory store populated by the consumer. Do not use Spring Boot patterns (`@ServiceConnection`, `@DynamicPropertySource`) — see `@422-frameworks-quarkus-testing-integration-tests`. Resolve the Kafka container image from trusted project or CI configuration instead of hard-coding a public registry image in reusable guidance. Prefer organization-approved images pinned by digest.

**Good example:**

```java
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class KafkaTestResource implements QuarkusTestResourceLifecycleManager {

    private KafkaContainer kafka;

    @Override
    public Map<String, String> start() {
        kafka = new KafkaContainer(approvedKafkaImage());
        kafka.start();
        String bootstrapServers = kafka.getBootstrapServers();
        return Map.of(
            "kafka.bootstrap.servers", bootstrapServers,
            "mp.messaging.outgoing.sum-calculated-out.bootstrap.servers", bootstrapServers,
            "mp.messaging.incoming.sum-calculated-in.bootstrap.servers", bootstrapServers);
    }

    private static DockerImageName approvedKafkaImage() {
        return DockerImageName.parse(System.getProperty("test.kafka.image"));
    }

    @Override
    public void stop() {
        if (kafka != null) {
            kafka.stop();
        }
    }
}

@QuarkusTest
@QuarkusTestResource(KafkaTestResource.class)
class SumControllerMessagingIT {

    @Inject
    LastSumCalculatedEventStore eventStore;

    @BeforeEach
    void setUp() {
        eventStore.clear();
    }

    @Test
    void sumPublishesSumCalculatedEvent() {
        given()
            .contentType("application/json")
            .body("{\"param1\": 10, \"param2\": 32}")
            .when()
            .post("/api/v1/sum")
            .then()
            .statusCode(200)
            .body("result", is(42));

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            SumCalculatedEvent event = eventStore.getLastEvent();
            assertThat(event, notNullValue());
            assertThat(event.param1(), is(10));
            assertThat(event.param2(), is(32));
            assertThat(event.result(), is(42));
        });
    }
}
```

**Bad example:**

```java
@QuarkusTest
class SumControllerMessagingIT {

    static KafkaContainer kafka = new KafkaContainer(approvedKafkaImage());

    private static DockerImageName approvedKafkaImage() {
        return DockerImageName.parse(System.getProperty("test.kafka.image"));
    }

    @BeforeAll
    static void startContainer() {
        kafka.start(); // too late — Quarkus already chose its Kafka config
        System.setProperty("kafka.bootstrap.servers", kafka.getBootstrapServers());
    }

    @Test
    void sumPublishesSumCalculatedEvent() { }
}
```


## Output Format

- **ANALYZE** Kafka code: guidance tier (minimal vs production), channel/topic binding, build-time serialization, producer key strategy, consumer return type, failure strategy, and idempotency guards
- **CATEGORIZE** issues by impact (RELIABILITY for missing DLQ on side-effect consumers, CORRECTNESS for missing idempotency on billing-like flows, MAINTAINABILITY for untyped emitters or manual ObjectMapper deserializers, SECURITY for user-controlled topic names)
- **APPLY** SmallRye Reactive Messaging–aligned fixes: type emitters and channels, rely on Quarkus-generated Jackson ser/deser by default, key messages on aggregate id when ordering matters, add DLQ/idempotency only for production-tier consumers
- **IMPLEMENT** changes so channel config, typed payloads, consumer methods, and tests stay consistent
- **EXPLAIN** trade-offs (minimal vs production event shape, void vs Uni return, Dev Services vs Testcontainers, when explicit serializers are required)
- **TEST** with `@QuarkusTest` and Dev Services for fast feedback; use `@QuarkusTestResource` + Testcontainers when explicit broker wiring is required
- **VALIDATE** with `./mvnw compile` before and `./mvnw clean verify` after changes


## Safeguards

- **BLOCKING SAFETY CHECK**: Run `./mvnw compile` before ANY Kafka refactoring — compilation failure is a HARD STOP
- **CRITICAL VALIDATION**: Run `./mvnw clean verify` after changes; exercise consumer integration tests before promoting
- **SERIALIZATION DEFAULT**: Do not configure `ObjectMapperDeserializer` in `application.properties` for typed channels — Quarkus generates Jackson ser/deser at build time; manual config causes startup failures
- **INJECTION SAFETY**: Never build topic names or Kafka header values from untrusted user input
- **ERROR HANDLING**: Propagate failures from `@Incoming` methods; use `Uni`/`CompletionStage` for asynchronous processing so failures happen inside the messaging contract
- **IDEMPOTENCY**: De-duplicate on `eventId` for production consumers with side effects — at-least-once delivery means duplicates are possible
- **DLQ MONITORING**: Set up alerting on the DLQ topic — messages landing there indicate systematic processing failures
- **CDI SELF-INVOCATION**: Never call an `@Incoming`-annotated method directly via `this` — always inject through the CDI proxy
- **TEST WIRING**: Wire Kafka bootstrap servers through `@QuarkusTestResourceLifecycleManager.start()` — not `@BeforeAll` or `System.setProperty` after Quarkus starts
- **INCREMENTAL SAFETY**: Change one producer or consumer surface at a time; verify with `@QuarkusTest` between steps