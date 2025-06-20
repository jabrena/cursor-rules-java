package com.example.demo.common;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;

/**
 * Base class for tests that require a PostgreSQL TestContainer with Sakila schema.
 * 
 * This class provides a common PostgreSQL container setup that can be extended by
 * integration and acceptance tests. It includes:
 * - PostgreSQL 15 container with consistent database configuration
 * - Sakila schema and test data initialization
 * - Automatic Spring Boot configuration via @ServiceConnection
 * - Container logging for debugging purposes
 * 
 * Usage: Extend this class and add @SpringBootTest, @Testcontainers annotations
 * to your test class as needed.
 */
@Testcontainers
@ActiveProfiles("test")
public abstract class PostgreSQLTestBase {

    /**
     * PostgreSQL TestContainer configured with Sakila schema and test data.
     * 
     * Features:
     * - Uses PostgreSQL 15 for consistency with production
     * - Loads Sakila-compatible schema for production-like testing
     * - Includes focused test data (51 films, 46 starting with 'A')
     * - Automatic Spring Boot configuration via @ServiceConnection
     * - Container logging enabled for debugging
     * - 2-minute startup timeout for reliable CI/CD environments
     */
    @Container
    @ServiceConnection(name = "postgres")
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("1.1-postgress-sakila-schema-compatible.sql"), 
                "/docker-entrypoint-initdb.d/01-schema.sql"
            )
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("2.1-postgres-sakila-film-data.sql"), 
                "/docker-entrypoint-initdb.d/02-data.sql"
            )
            .withStartupTimeout(Duration.ofMinutes(2))
            .withLogConsumer(outputFrame -> {
                System.out.print("[POSTGRES] " + outputFrame.getUtf8String());
            });

    /**
     * Get the PostgreSQL container instance for direct access if needed.
     * Useful for executing queries directly against the container or
     * accessing connection details.
     * 
     * @return The PostgreSQL container instance
     */
    protected static PostgreSQLContainer<?> getPostgresContainer() {
        return postgres;
    }
} 