---
description: 
globs: 
alwaysApply: false
---
# Spring Boot Native Compilation

Guidelines for building Spring Boot applications as GraalVM native images for improved startup time, reduced memory footprint, and enhanced performance characteristics.

## Implementing These Principles

These guidelines are built upon the following core principles:

- **Compile-time analysis**: All code paths must be discoverable at compile time
- **Minimal reflection usage**: Avoid runtime reflection and dynamic class loading
- **Explicit configuration**: Configure native hints for reflection, resources, and proxies
- **Profile-guided optimization**: Use AOT processing and build-time optimizations
- **Resource efficiency**: Optimize for reduced memory usage and faster startup

## Table of contents

- Rule 1: Configure Native Build Tools and Dependencies
- Rule 2: Manage Reflection and Dynamic Features
- Rule 3: Handle Resources and Configuration Files
- Rule 4: Optimize Application Profiles for Native
- Rule 5: Test Native Image Compatibility
- Rule 6: Use AOT Processing and Build-time Hints

## Rule 1: Configure Native Build Tools and Dependencies

**Title**: Proper Native Build Configuration
**Description**: Configure Maven or Gradle with the Spring Boot Native plugin and GraalVM dependencies. Ensure all necessary build tools are properly set up for native compilation.

**Good example:**

```xml
<!-- pom.xml -->
<properties>
    <native.maven.plugin.version>0.9.28</native.maven.plugin.version>
    <spring-boot.version>3.5.0</spring-boot.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>

<profiles>
    <profile>
        <id>native</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <executions>
                        <execution>
                            <id>process-aot</id>
                            <goals>
                                <goal>process-aot</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <groupId>org.graalvm.buildtools</groupId>
                    <artifactId>native-maven-plugin</artifactId>
                    <executions>
                        <execution>
                            <id>add-reachability-metadata</id>
                            <goals>
                                <goal>add-reachability-metadata</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

**Bad Example:**

```xml
<!-- Missing native plugin configuration -->
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <!-- No native image configuration -->
        </plugin>
    </plugins>
</build>
```

## Rule 2: Manage Reflection and Dynamic Features

**Title**: Minimize and Configure Reflection Usage
**Description**: Avoid runtime reflection and dynamic class loading. When reflection is necessary, provide explicit native hints or use Spring's AOT processing to register classes at build time.

**Good example:**

```java
// Use @RegisterReflectionForBinding for data classes
@RegisterReflectionForBinding({Person.class, Address.class})
@RestController
public class PersonController {
    
    @GetMapping("/person")
    public Person getPerson() {
        return new Person("John", "Doe");
    }
}

// For explicit reflection hints
@Component
public class ReflectionHints implements RuntimeHintsRegistrar {
    
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection()
            .registerType(MyClass.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerType(AnotherClass.class, MemberCategory.DECLARED_FIELDS);
    }
}

// Use @ImportRuntimeHints to register hints
@ImportRuntimeHints(ReflectionHints.class)
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**Bad Example:**

```java
// Avoid runtime class loading and reflection
@RestController
public class BadController {
    
    @GetMapping("/dynamic")
    public Object getDynamic() throws Exception {
        // This will fail in native image
        Class<?> clazz = Class.forName("com.example.DynamicClass");
        return clazz.getDeclaredConstructor().newInstance();
    }
}
```

## Rule 3: Handle Resources and Configuration Files

**Title**: Explicit Resource Configuration
**Description**: Register all resources (properties files, templates, static content) that need to be included in the native image. Use resource hints for files accessed at runtime.

**Good example:**

```java
@Component
public class ResourceHints implements RuntimeHintsRegistrar {
    
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources()
            .registerPattern("templates/*.html")
            .registerPattern("static/**")
            .registerPattern("config/*.properties")
            .registerPattern("META-INF/spring.factories");
    }
}

// For simple cases, use @RegisterReflectionForBinding
@RegisterReflectionForBinding(MyConfigProperties.class)
@ConfigurationProperties(prefix = "app")
public class MyConfigProperties {
    private String name;
    private int timeout;
    // getters and setters
}
```

**Bad Example:**

```java
// Avoid dynamic resource loading without hints
@Service
public class BadResourceService {
    
    public String loadTemplate(String templateName) {
        // This might fail if template is not registered
        return Files.readString(Paths.get("templates/" + templateName + ".html"));
    }
}
```

## Rule 4: Optimize Application Profiles for Native

**Title**: Native-Specific Configuration
**Description**: Create native-specific application profiles and configurations that optimize for native image characteristics like reduced memory usage and faster startup.

**Good example:**

```properties
# application-native.properties
spring.jpa.defer-datasource-initialization=false
spring.sql.init.mode=never
spring.jpa.hibernate.ddl-auto=none

# Reduce logging overhead
logging.level.root=WARN
logging.level.org.springframework=INFO

# Optimize connection pools for native
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=1

# Disable features that don't work well with native
spring.devtools.enabled=false
management.endpoint.beans.enabled=false
```

```java
@Profile("native")
@Configuration
public class NativeConfiguration {
    
    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "native")
    public DataSource nativeDataSource() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10000);
        return new HikariDataSource(config);
    }
}
```

**Bad Example:**

```properties
# Don't use development-oriented settings in native
spring.devtools.enabled=true
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=create-drop
logging.level.root=DEBUG
```

## Rule 5: Test Native Image Compatibility

**Title**: Comprehensive Native Testing Strategy
**Description**: Implement testing strategies that verify native image compatibility, including integration tests that run against the native executable.

**Good example:**

```java
@ActiveProfiles("native")
@SpringBootTest
class NativeImageIntegrationTest {
    
    @Test
    void contextLoads() {
        // Test that application context loads successfully
    }
    
    @Test
    void serializationWorks() {
        // Test JSON serialization/deserialization
        ObjectMapper mapper = new ObjectMapper();
        Person person = new Person("John", "Doe");
        
        assertDoesNotThrow(() -> {
            String json = mapper.writeValueAsString(person);
            Person deserialized = mapper.readValue(json, Person.class);
            assertEquals(person.getName(), deserialized.getName());
        });
    }
}

// Maven configuration for native tests
```

```xml
<plugin>
    <groupId>org.graalvm.buildtools</groupId>
    <artifactId>native-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>test-native</id>
            <goals>
                <goal>test</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**Bad Example:**

```java
// Don't assume JVM tests will work in native
@SpringBootTest
class OnlyJvmTest {
    
    @Test
    void testReflection() {
        // This might pass on JVM but fail in native
        Class.forName("some.dynamic.Class");
    }
}
```

## Rule 6: Use AOT Processing and Build-time Hints

**Title**: Leverage Ahead-of-Time Processing
**Description**: Use Spring's AOT processing capabilities to generate optimized code and configuration at build time, reducing runtime overhead and improving native image compatibility.

**Good example:**

```java
@Component
public class AotHints implements BeanFactoryInitializationAotProcessor {
    
    @Override
    public BeanRegistrationAotContribution processAheadOfTime(
            ConfigurableListableBeanFactory beanFactory) {
        
        return (generationContext, beanRegistrationCode) -> {
            // Generate AOT-optimized bean registration code
            RuntimeHints runtimeHints = generationContext.getRuntimeHints();
            runtimeHints.reflection().registerType(MyService.class);
        };
    }
}

// Use @Conditional annotations that work at build time
@ConditionalOnProperty(name = "feature.enabled", havingValue = "true")
@Component
public class ConditionalService {
    // Implementation
}
```

**Bad Example:**

```java
// Avoid runtime conditional logic that can't be determined at build time
@Component
public class BadConditionalService {
    
    @PostConstruct
    public void init() {
        if (System.getProperty("runtime.feature") != null) {
            // This kind of runtime decision making doesn't work well with AOT
            loadDynamicConfiguration();
        }
    }
}
```

### Build Commands for Native Compilation

**Maven Commands:**
```bash
# Build native image
./mvnw -Pnative native:compile

# Build and test native image
./mvnw -Pnative clean native:test

# Build container image with native binary
./mvnw spring-boot:build-image -Pnative
```

### Performance Considerations

- **Startup Time**: Native images typically start 10-100x faster than JVM
- **Memory Usage**: Reduced memory footprint, especially for smaller applications
- **Build Time**: Native compilation takes longer than regular JAR builds
- **Image Size**: Native executables are typically larger than JAR files but smaller when considering JVM overhead
- **Runtime Performance**: May have slightly different performance characteristics compared to JVM with JIT compilation