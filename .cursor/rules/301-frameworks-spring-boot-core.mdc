---
description: 
globs: 
alwaysApply: false
---
# Spring Boot Core

Spring Boot Core guidelines focus on proper usage of main annotations, bean management, and configuration best practices to build maintainable and efficient Spring Boot applications.

## Implementing These Principles

These guidelines are built upon the following core principles:

- Principle 1: Use appropriate Spring annotations to clearly express component responsibilities
- Principle 2: Leverage Spring's dependency injection and IoC container effectively
- Principle 3: Follow configuration best practices for maintainable and testable applications
- Principle 4: Apply proper bean lifecycle management and scoping

## Table of contents

- Rule 0: Spring Boot Main Application Class
- Rule 1: Main Spring Boot Annotations Usage
- Rule 2: Bean Definition and Management
- Rule 3: Configuration Classes and Properties
- Rule 4: Component Scanning and Package Organization
- Rule 5: Conditional Configuration and Profiles
- Rule 6: Constructor Dependency Injection Best Practices
- Rule 7: Bean Minimization and Composition
- Rule 8: Scheduled Tasks and Background Processing

## Rule 0: Spring Boot Main Application Class

Title: Create a Proper Spring Boot Main Application Class
Description: Every Spring Boot application should have a main application class annotated with @SpringBootApplication. This class serves as the entry point and configuration root, combining @Configuration, @EnableAutoConfiguration, and @ComponentScan annotations.

**Good example:**

```java
@SpringBootApplication
public class MainApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}

// For more complex scenarios with custom configuration
@SpringBootApplication(
    scanBasePackages = {
        "com.company.app.controller",
        "com.company.app.service", 
        "com.company.app.repository",
        "com.company.app.config"
    },
    exclude = {
        DataSourceAutoConfiguration.class,
        SecurityAutoConfiguration.class
    }
)
```

**Bad Example:**

```java
// Missing @SpringBootApplication annotation
public class MainApplication {
    public static void main(String[] args) {
        // Manual Spring context setup instead of SpringApplication.run()
        ApplicationContext context = new AnnotationConfigApplicationContext();
        // Manual configuration - loses Spring Boot benefits
    }
}

// Using individual annotations instead of @SpringBootApplication
@Configuration
@EnableAutoConfiguration  
@ComponentScan
public class MainApplication { // Verbose and error-prone
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}

// Poor naming and structure
@SpringBootApplication
public class App { // Non-descriptive name
    
    @Autowired
    private UserService userService; // Business logic in main class
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        
        // Business logic in main method - should be in separate components
        System.out.println("Processing users...");
    }
}
```

## Rule 1: Main Spring Boot Annotations Usage

Title: Use Appropriate Spring Boot Annotations for Component Definition
Description: Use the correct Spring Boot annotations to define components, controllers, services, and repositories. Each annotation has specific semantics and should be used according to the layer's responsibility.

**Good example:**

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }
}

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    
    @Query("SELECT * FROM users WHERE email = :email")
    Optional<User> findByEmail(@Param("email") String email);
    
    @Modifying
    @Query("UPDATE users SET last_login = :lastLogin WHERE id = :id")
    void updateLastLogin(@Param("id") Long id, @Param("lastLogin") LocalDateTime lastLogin);
}

@Table("users")
public class User {
    
    @Id
    private Long id;
    
    @Column("email")
    private String email;
    
    @Column("first_name")
    private String firstName;
    
    @Column("last_name") 
    private String lastName;
    
    @Column("last_login")
    private LocalDateTime lastLogin;
    
    // Constructors, getters, and setters
}
```

**Bad Example:**

```java
@Component // Should be @RestController
public class UserController {
    
    @Inject // Use @Autowired for Spring Boot
    private UserService userService;
}

@Component // Should be @Service
public class UserService {
    // Missing @Transactional for data operations
}

@Component // Should be @Repository
public class UserRepository {
    // Manual JDBC instead of using Spring Data JDBC
}
```

## Rule 2: Bean Definition and Management

Title: Proper Bean Definition, Scoping, and Lifecycle Management
Description: Define beans with appropriate scope, use constructor injection, and manage bean lifecycle properly. Prefer constructor injection over field injection for better testability and immutability.

**Good example:**

```java
@Configuration
public class AppConfig {
    
    @Bean
    @Scope("singleton") // Default, but explicit for clarity
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    @Scope("prototype")
    public AuditLogger auditLogger() {
        return new AuditLogger();
    }
}

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // Constructor injection - preferred approach
    public UserService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
}

@Component
public class DatabaseMigration {
    
    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        // Perform initialization after Spring context is ready
        performMigration();
    }
    
    @PreDestroy
    public void cleanup() {
        // Cleanup resources before bean destruction
    }
}
```

**Bad Example:**

```java
@Configuration
public class AppConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Creates new instance every time
    }
}

@Service
public class UserService {
    
    @Autowired // Field injection - harder to test
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // No constructor, relies on reflection
}

@Component
public class DatabaseMigration {
    
    @PostConstruct
    public void init() {
        // Heavy operations in PostConstruct can block application startup
        performHeavyMigration();
    }
}
```

## Rule 3: Configuration Classes and Properties

Title: Organize Configuration Using @Configuration Classes and External Properties
Description: Use @Configuration classes to organize beans logically, leverage @ConfigurationProperties for type-safe configuration, and externalize configuration values properly.

**Good example:**

```java
@Configuration
@EnableConfigurationProperties({DatabaseProperties.class, SecurityProperties.class})
public class AppConfig {
    
    @Bean
    @ConditionalOnProperty(name = "app.cache.enabled", havingValue = "true")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("users", "products");
    }
}

@ConfigurationProperties(prefix = "app.database")
@ConstructorBinding
public class DatabaseProperties {
    
    private final String url;
    private final String username;
    private final int maxConnections;
    private final Duration connectionTimeout;
    
    public DatabaseProperties(String url, String username, 
                            int maxConnections, Duration connectionTimeout) {
        this.url = url;
        this.username = username;
        this.maxConnections = maxConnections;
        this.connectionTimeout = connectionTimeout;
    }
    
    // Getters only - immutable
}

@Configuration
@Profile("!test")
public class ProductionConfig {
    
    @Bean
    public DataSource dataSource(DatabaseProperties properties) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(properties.getUrl());
        config.setUsername(properties.getUsername());
        config.setMaximumPoolSize(properties.getMaxConnections());
        return new HikariDataSource(config);
    }
}
```

**Bad Example:**

```java
@Configuration
public class AppConfig {
    
    @Value("${database.url}") // Scattered @Value annotations
    private String databaseUrl;
    
    @Value("${database.username}")
    private String username;
    
    @Bean
    public DataSource dataSource() {
        // Hardcoded values mixed with properties
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        config.setUsername(username);
        config.setPassword("hardcoded-password"); // Security risk
        config.setMaximumPoolSize(10); // Magic number
        return new HikariDataSource(config);
    }
}

// No type safety, no validation
public class DatabaseConfig {
    @Value("${app.database.max-connections:#{null}}")
    private Integer maxConnections; // Can be null, no validation
}
```

## Rule 4: Component Scanning and Package Organization

Title: Organize Components with Proper Package Structure and Component Scanning
Description: Use logical package organization and configure component scanning appropriately. Avoid over-broad scanning and organize code by feature or layer consistently.

**Good example:**

```java
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.company.app.controller",
    "com.company.app.service", 
    "com.company.app.repository",
    "com.company.app.config"
})
@EnableJdbcRepositories("com.company.app.repository")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// Package structure:
// com.company.app
//   ├── controller/
//   │   ├── UserController.java
//   │   └── ProductController.java
//   ├── service/
//   │   ├── UserService.java
//   │   └── ProductService.java
//   ├── repository/
//   │   ├── UserRepository.java
//   │   └── ProductRepository.java
//   ├── config/
//   │   ├── DatabaseConfig.java
//   │   └── SecurityConfig.java
//   └── model/
//       ├── User.java
//       └── Product.java

@Component("userService") // Explicit bean name when needed
public class UserService {
    // Implementation
}
```

**Bad Example:**

```java
@SpringBootApplication
@ComponentScan("com") // Too broad - scans entire classpath
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// Poor package structure:
// com.company.app
//   ├── UserController.java      // Mixed responsibilities
//   ├── UserService.java         // in same package
//   ├── UserRepository.java
//   ├── ProductStuff.java        // Unclear naming
//   └── Utils.java               // Generic naming

@Component
public class UserService { // No explicit naming strategy
    // Multiple unrelated responsibilities in one class
    public void handleUser() { }
    public void sendEmail() { }
    public void generateReport() { }
}
```

## Rule 5: Conditional Configuration and Profiles

Title: Use Conditional Configuration and Profiles for Environment-Specific Setup
Description: Leverage Spring's conditional annotations and profiles to create flexible, environment-aware configurations that adapt to different deployment scenarios.

**Good example:**

```java
@Configuration
@Profile("development")
public class DevConfig {
    
    @Bean
    @ConditionalOnMissingBean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
    
    @Bean
    public DataSource devDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/devdb");
        config.setUsername("dev_user");
        config.setPassword("dev_password");
        config.setMaximumPoolSize(5);
        return new HikariDataSource(config);
    }
}

@Configuration
@Profile("production")
public class ProdConfig {
    
    @Bean
    @ConditionalOnProperty(
        name = "app.monitoring.enabled", 
        havingValue = "true", 
        matchIfMissing = true
    )
    public MeterRegistry meterRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }
    
    @Bean
    @ConditionalOnClass(name = "redis.clients.jedis.Jedis")
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}

@Service
@ConditionalOnProperty(name = "features.advanced-analytics", havingValue = "true")
public class AdvancedAnalyticsService {
    // Only available when feature flag is enabled
}

// application-dev.yml
// spring:
//   profiles:
//     active: development
//   datasource:
//     url: jdbc:postgresql://localhost:5432/devdb
//     username: dev_user
//     password: dev_password

// application-prod.yml  
// spring:
//   profiles:
//     active: production
//   datasource:
//     url: ${DATABASE_URL}
//     username: ${DATABASE_USERNAME}
//     password: ${DATABASE_PASSWORD}
```

**Bad Example:**

```java
@Configuration
public class AppConfig {
    
    @Value("${spring.profiles.active:}")
    private String activeProfile;
    
    @Bean
    public DataSource dataSource() {
        if ("development".equals(activeProfile)) {
            // Manual profile checking instead of @Profile
            return createDevDataSource();
        } else if ("production".equals(activeProfile)) {
            return createProdDataSource();
        }
        return createDefaultDataSource();
    }
    
    @Bean
    public FeatureService featureService() {
        // No conditional logic - always creates bean
        return new ExpensiveFeatureService();
    }
}

@Service
public class NotificationService {
    
    @Value("${app.env}")
    private String environment;
    
    public void sendNotification(String message) {
        if ("prod".equals(environment)) {
            // Environment logic scattered in business code
            sendRealNotification(message);
        } else {
            // Development behavior mixed with production code
            System.out.println("DEV: " + message);
        }
    }
}
```

## Rule 6: Constructor Dependency Injection Best Practices

Title: Favor Constructor Injection for Immutable and Testable Components
Description: Use constructor injection as the primary dependency injection mechanism. It promotes immutability, makes dependencies explicit, enables easier testing, and prevents circular dependencies.

**Good example:**

```java
@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AuditService auditService;
    
    // Single constructor - @Autowired is optional since Spring 4.3
    public UserService(UserRepository userRepository, 
                      EmailService emailService,
                      AuditService auditService) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository cannot be null");
        this.emailService = Objects.requireNonNull(emailService, "emailService cannot be null");
        this.auditService = Objects.requireNonNull(auditService, "auditService cannot be null");
    }
    
    public User createUser(CreateUserRequest request) {
        User user = new User(request.getEmail(), request.getName());
        User savedUser = userRepository.save(user);
        emailService.sendWelcomeEmail(savedUser);
        auditService.logUserCreation(savedUser);
        return savedUser;
    }
}

@Configuration
public class ServiceConfig {
    
    // Constructor injection for configuration classes
    private final DatabaseProperties databaseProperties;
    
    public ServiceConfig(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }
    
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
            .url(databaseProperties.getUrl())
            .username(databaseProperties.getUsername())
            .password(databaseProperties.getPassword())
            .build();
    }
}

// Optional dependencies using constructor with default values
@Service
public class NotificationService {
    
    private final EmailService emailService;
    private final SmsService smsService;
    
    // Primary constructor for all dependencies
    public NotificationService(EmailService emailService, SmsService smsService) {
        this.emailService = emailService;
        this.smsService = smsService;
    }
    
    // Secondary constructor for partial dependencies
    public NotificationService(EmailService emailService) {
        this(emailService, new NoOpSmsService());
    }
}
```

**Bad Example:**

```java
@Service
public class UserService {
    
    @Autowired // Field injection - harder to test and debug
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private AuditService auditService;
    
    // No constructor - dependencies can be null
    // Cannot create immutable fields
    // Harder to unit test
}

@Service
public class OrderService {
    
    private UserService userService;
    private PaymentService paymentService;
    
    @Autowired // Setter injection - allows partial initialization
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    // Service can be in invalid state if setters not called
    public void processOrder(Order order) {
        // NullPointerException risk if dependencies not injected
        userService.validateUser(order.getUserId());
        paymentService.processPayment(order.getPayment());
    }
}

@Configuration
public class BadConfig {
    
    @Autowired // Field injection in configuration
    private Environment environment;
    
    @Bean
    public ApiClient apiClient() {
        // Configuration depends on field injection
        return new ApiClient(environment.getProperty("api.url"));
    }
}
```

## Rule 7: Bean Minimization and Composition

Title: Minimize Bean Count Through Composition and Logical Grouping
Description: Reduce the number of Spring beans by composing related functionality, using factory methods, and avoiding over-decomposition. Prefer composition over excessive bean granularity.

**Good example:**

```java
// Compose related services into cohesive units
@Service
public class UserManagementService {
    
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserNotificationService notificationService;
    
    public UserManagementService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userValidator = new UserValidator(); // Simple composition
        this.notificationService = new UserNotificationService(new EmailClient(), new SmsClient());
    }
    
    public User createUser(CreateUserRequest request) {
        userValidator.validate(request);
        User user = userRepository.save(new User(request));
        notificationService.sendWelcomeNotification(user);
        return user;
    }
}

// Use factory methods for related beans
@Configuration
public class CommunicationConfig {
    
    @Bean
    public CommunicationService communicationService(
            @Value("${app.email.enabled:true}") boolean emailEnabled,
            @Value("${app.sms.enabled:false}") boolean smsEnabled) {
        
        List<NotificationChannel> channels = new ArrayList<>();
        
        if (emailEnabled) {
            channels.add(createEmailChannel());
        }
        if (smsEnabled) {
            channels.add(createSmsChannel());
        }
        
        return new CommunicationService(channels);
    }
    
    // Private factory methods instead of separate beans
    private EmailChannel createEmailChannel() {
        return new EmailChannel(new SmtpClient());
    }
    
    private SmsChannel createSmsChannel() {
        return new SmsChannel(new TwilioClient());
    }
}

// Compose utilities and helpers as inner classes or packages
@Service
public class ReportService {
    
    private final ReportRepository reportRepository;
    private final ReportFormatter formatter;
    private final ReportExporter exporter;
    
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
        this.formatter = new ReportFormatter();
        this.exporter = new ReportExporter();
    }
    
    // Inner class for related functionality
    private static class ReportFormatter {
        public String formatAsJson(Report report) { return "..."; }
        public String formatAsXml(Report report) { return "..."; }
    }
    
    private static class ReportExporter {
        public void exportToPdf(String content) { /* implementation */ }
        public void exportToExcel(String content) { /* implementation */ }
    }
}

// Use configuration properties instead of multiple property beans
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {
    
    private final Database database = new Database();
    private final Security security = new Security();
    private final Cache cache = new Cache();
    
    // Nested static classes for logical grouping
    public static class Database {
        private String url;
        private String username;
        private int maxConnections = 10;
        // getters and setters
    }
    
    public static class Security {
        private boolean enabled = true;
        private String algorithm = "SHA-256";
        // getters and setters
    }
    
    public static class Cache {
        private boolean enabled = false;
        private Duration ttl = Duration.ofMinutes(30);
        // getters and setters
    }
}
```

**Bad Example:**

```java
// Over-decomposition - too many beans for simple functionality
@Component
public class EmailValidator {
    public boolean isValid(String email) { return email.contains("@"); }
}

@Component
public class PasswordValidator {
    public boolean isValid(String password) { return password.length() >= 8; }
}

@Component
public class PhoneValidator {
    public boolean isValid(String phone) { return phone.matches("\\d{10}"); }
}

@Component
public class UserValidator {
    @Autowired private EmailValidator emailValidator;
    @Autowired private PasswordValidator passwordValidator;
    @Autowired private PhoneValidator phoneValidator;
    
    // Three beans for simple validation logic
}

// Separate beans for configuration values
@Component
public class DatabaseUrlProvider {
    @Value("${database.url}")
    private String url;
    public String getUrl() { return url; }
}

@Component
public class DatabaseUsernameProvider {
    @Value("${database.username}")
    private String username;
    public String getUsername() { return username; }
}

@Component
public class DatabasePasswordProvider {
    @Value("${database.password}")
    private String password;
    public String getPassword() { return password; }
}

// Multiple similar beans instead of composition
@Component
public class EmailSender {
    public void send(String to, String message) { /* implementation */ }
}

@Component
public class SmsSender {
    public void send(String phone, String message) { /* implementation */ }
}

@Component
public class PushNotificationSender {
    public void send(String deviceId, String message) { /* implementation */ }
}

@Service
public class NotificationService {
    @Autowired private EmailSender emailSender;
    @Autowired private SmsSender smsSender;
    @Autowired private PushNotificationSender pushSender;
    
    // Managing multiple beans instead of composed solution
}

// Utility classes as beans
@Component
public class StringUtils {
    public boolean isEmpty(String str) { return str == null || str.trim().isEmpty(); }
}

@Component
public class DateUtils {
    public String format(LocalDate date) { return date.toString(); }
}
```

## Rule 8: Scheduled Tasks and Background Processing

Title: Implement Robust Scheduled Tasks with Proper Configuration and Error Handling
Description: Use Spring's scheduling capabilities effectively with appropriate configuration, error handling, and monitoring. Ensure scheduled tasks are resilient, maintainable, and don't impact application performance.

**Good example:**

```java
@Configuration
@EnableScheduling
@EnableAsync
public class SchedulingConfig {
    
    @Bean
    @Primary
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setErrorHandler(new CustomErrorHandler());
        return scheduler;
    }
    
    @Bean
    public TaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-task-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }
}

@Component
public class DataMaintenanceScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(DataMaintenanceScheduler.class);
    
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final MeterRegistry meterRegistry;
    
    public DataMaintenanceScheduler(UserRepository userRepository,
                                  AuditLogRepository auditLogRepository,
                                  MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.meterRegistry = meterRegistry;
    }
    
    // Fixed rate - executes every 30 minutes regardless of previous execution time
    @Scheduled(fixedRateString = "${app.cleanup.rate:1800000}") // 30 minutes default
    public void cleanupExpiredSessions() {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            logger.info("Starting session cleanup task");
            
            int deletedCount = userRepository.deleteExpiredSessions(
                LocalDateTime.now().minusHours(24)
            );
            
            logger.info("Cleaned up {} expired sessions", deletedCount);
            meterRegistry.counter("scheduled.cleanup.sessions", "status", "success")
                        .increment(deletedCount);
                        
        } catch (Exception e) {
            logger.error("Failed to cleanup expired sessions", e);
            meterRegistry.counter("scheduled.cleanup.sessions", "status", "error")
                        .increment();
        } finally {
            sample.stop(Timer.builder("scheduled.cleanup.duration")
                            .tag("task", "sessions")
                            .register(meterRegistry));
        }
    }
    
    // Fixed delay - waits specified time after previous execution completes
    @Scheduled(fixedDelayString = "${app.audit.cleanup.delay:3600000}") // 1 hour default
    public void cleanupOldAuditLogs() {
        try {
            logger.debug("Starting audit log cleanup");
            
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);
            int deletedCount = auditLogRepository.deleteLogsOlderThan(cutoffDate);
            
            if (deletedCount > 0) {
                logger.info("Cleaned up {} old audit logs", deletedCount);
            }
            
        } catch (Exception e) {
            logger.error("Failed to cleanup old audit logs", e);
            // Don't rethrow - let other scheduled tasks continue
        }
    }
    
    // Cron expression - runs at 2 AM every day
    @Scheduled(cron = "${app.reports.schedule:0 0 2 * * *}")
    @Async("asyncExecutor")
    public CompletableFuture<Void> generateDailyReports() {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Starting daily report generation");
                
                // Long-running task executed asynchronously
                generateUserActivityReport();
                generateSystemHealthReport();
                
                logger.info("Daily reports generated successfully");
                
            } catch (Exception e) {
                logger.error("Failed to generate daily reports", e);
                // Could send alert or notification here
            }
        });
    }
    
    // Conditional scheduling based on profiles
    @Scheduled(fixedRate = 300000) // 5 minutes
    @ConditionalOnProperty(name = "app.monitoring.enabled", havingValue = "true")
    public void healthCheck() {
        logger.debug("Performing health check");
        // Implementation
    }
    
    private void generateUserActivityReport() {
        // Heavy computation that should run async
    }
    
    private void generateSystemHealthReport() {
        // Another heavy computation
    }
}

@Component
public class CustomErrorHandler implements ErrorHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomErrorHandler.class);
    
    @Override
    public void handleError(Throwable t) {
        logger.error("Scheduled task failed with error", t);
        
        // Could implement alerting, metrics, or other error handling logic
        if (t instanceof DataAccessException) {
            // Handle database-related errors
            logger.warn("Database error in scheduled task, will retry on next execution");
        } else {
            // Handle other types of errors
            logger.error("Unexpected error in scheduled task", t);
        }
    }
}

// Configuration properties for scheduling
@ConfigurationProperties(prefix = "app.scheduling")
public class SchedulingProperties {
    
    private boolean enabled = true;
    private int poolSize = 5;
    private Duration shutdownTimeout = Duration.ofSeconds(30);
    
    // getters and setters
}
```

**Bad Example:**

```java
@Component
@EnableScheduling // Should be in @Configuration class
public class BadScheduler {
    
    @Autowired // Field injection
    private UserRepository userRepository;
    
    // Hardcoded timing, no error handling
    @Scheduled(fixedRate = 30000) // 30 seconds - too frequent for cleanup
    public void cleanupUsers() {
        // No logging, no error handling
        userRepository.deleteInactiveUsers();
        
        // Blocking operation in scheduled thread
        sendEmailNotifications(); // Should be async
    }
    
    @Scheduled(cron = "0 0 2 * * *") // Hardcoded, not configurable
    public void heavyProcessing() {
        // Long-running synchronous operation blocks scheduler
        for (int i = 0; i < 1000000; i++) {
            performComplexCalculation();
            // No progress tracking, no way to monitor or stop
        }
        
        // No error handling - exception will break scheduling
        riskyOperation();
    }
    
    @Scheduled(fixedDelay = 1000) // Too frequent, will impact performance
    public void constantPolling() {
        // Polling database every second
        checkForNewMessages(); // Should use messaging or webhooks instead
    }
    
    // Multiple methods with same timing - inefficient
    @Scheduled(fixedRate = 60000)
    public void task1() { /* implementation */ }
    
    @Scheduled(fixedRate = 60000)
    public void task2() { /* implementation */ }
    
    @Scheduled(fixedRate = 60000)  
    public void task3() { /* implementation */ }
    
    private void sendEmailNotifications() {
        // Synchronous email sending blocks the scheduler
        for (User user : getAllUsers()) {
            emailService.sendEmail(user.getEmail(), "notification");
            // No timeout, no retry logic, no error handling
        }
    }
    
    private void riskyOperation() {
        // Operation that might throw uncaught exception
        throw new RuntimeException("This will break all scheduling");
    }
}

// No thread pool configuration
@Configuration
public class BadSchedulingConfig {
    // Using default single-threaded scheduler
    // No error handling configuration
    // No monitoring or metrics
}

@Service
public class BlockingScheduledService {
    
    @Scheduled(fixedRate = 5000)
    public void blockingTask() {
        try {
            // Blocking I/O operation
            Thread.sleep(30000); // 30 second sleep blocks scheduler
            
            // Synchronous HTTP calls
            restTemplate.getForObject("http://slow-service/api", String.class);
            
        } catch (InterruptedException e) {
            // Poor exception handling
            e.printStackTrace(); // Never use printStackTrace in production
        }
    }
}
```

### Scheduling Best Practices

**Configuration Guidelines:**
- Always use `@EnableScheduling` in a `@Configuration` class
- Configure custom `TaskScheduler` with appropriate thread pool size
- Set up proper error handling with `ErrorHandler`
- Use externalized configuration for timing and scheduling parameters

**Error Handling:**
- Implement comprehensive logging for all scheduled tasks
- Use try-catch blocks to prevent one task failure from affecting others
- Consider implementing retry logic for transient failures
- Add metrics and monitoring for scheduled task execution

**Performance Considerations:**
- Use `@Async` for long-running tasks to avoid blocking the scheduler
- Choose appropriate scheduling intervals based on business requirements
- Monitor thread pool usage and adjust pool sizes accordingly
- Avoid frequent polling - consider event-driven alternatives

**Testing:**
```java
@TestConfiguration
public class TestSchedulingConfig {
    
    @Bean
    @Primary
    public TaskScheduler testTaskScheduler() {
        // Use synchronous scheduler for testing
        return new SyncTaskExecutor();
    }
}

@SpringBootTest
class ScheduledTaskTest {
    
    @Test
    @DirtiesContext
    void shouldExecuteScheduledTask() {
        // Test scheduled task logic without actual scheduling
        scheduler.cleanupExpiredSessions();
        // Verify expected behavior
    }
}
```

### Advanced Configuration Patterns

For complex applications, consider these additional patterns:

- **@ConfigurationPropertiesBinding**: Create custom property converters
- **@ConditionalOnBean/@ConditionalOnMissingBean**: Fine-grained bean creation control  
- **@Import**: Compose configuration classes
- **@EnableAutoConfiguration(exclude = {...})**: Disable specific auto-configurations
- **ApplicationContextInitializer**: For programmatic context customization

