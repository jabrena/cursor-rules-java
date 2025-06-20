package info.jab.ms;

import info.jab.ms.common.PostgreSQLTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Basic integration tests for the Demo Application.
 * 
 * This class extends PostgreSQLTestBase to inherit the common TestContainer setup
 * with Sakila schema and test data. It provides basic smoke tests to verify
 * the Spring Boot application context loads correctly with the database.
 */
@SpringBootTest
class DemoApplicationTests extends PostgreSQLTestBase {

    /**
     * Basic smoke test to verify the Spring Boot application context loads
     * successfully with the PostgreSQL TestContainer.
     * 
     * This test validates:
     * - Spring Boot application context loads without errors
     * - Database connection is established via @ServiceConnection
     * - All auto-configuration works properly
     */
    @Test
    void contextLoads() {
        // This test will load the Spring context with the TestContainer database
        // The @ServiceConnection annotation in the base class automatically configures
        // the database connection properties, so no manual configuration is needed
        
        // Verify the container is running (inherited from base class)
        assert getPostgresContainer().isRunning() : "PostgreSQL container should be running";
        assert getPostgresContainer().getDatabaseName().equals("testdb") : "Database should be testdb";
        
        // Context loading success implies database connectivity is working
        System.out.println("Spring Boot context loaded successfully with TestContainer database");
        System.out.println("Database URL: " + getPostgresContainer().getJdbcUrl());
    }
} 