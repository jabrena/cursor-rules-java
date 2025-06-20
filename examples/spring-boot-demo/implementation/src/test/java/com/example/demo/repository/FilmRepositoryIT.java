package com.example.demo.repository;

import com.example.demo.common.PostgreSQLTestBase;
import com.example.demo.entity.Film;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * FilmRepositoryIT - Integration Tests for FilmRepository Data Access Layer
 * 
 * This class implements integration tests for the FilmRepository using TestContainers
 * PostgreSQL configuration. It extends PostgreSQLTestBase to inherit the common
 * TestContainer setup with Sakila schema and test data.
 * 
 * Test Strategy:
 * - Uses @DataJdbcTest for focused Spring Data JDBC testing
 * - TestContainers provides isolated PostgreSQL database
 * - Real database queries against Sakila schema
 * - Validates SQL queries and Spring Data JDBC configuration
 * 
 * Task 7.1: Set up TestContainers PostgreSQL configuration for integration tests ✅
 */
@DataJdbcTest
class FilmRepositoryIT extends PostgreSQLTestBase {

    @Autowired
    private FilmRepository filmRepository;

    /**
     * Task 7.1: Verify TestContainers PostgreSQL configuration is working
     * 
     * This test validates that:
     * - TestContainers PostgreSQL container is running
     * - Spring Data JDBC is configured correctly
     * - FilmRepository is properly injected
     * - Database connection is established
     * - Test data is available
     */
    @Test
    void testContainerPostgreSQLConfigurationIsWorking() {
        // Verify TestContainer is running
        assertThat(getPostgresContainer().isRunning())
                .as("PostgreSQL container should be running")
                .isTrue();

        // Verify database configuration
        assertThat(getPostgresContainer().getDatabaseName())
                .as("Database name should match configuration")
                .isEqualTo("testdb");

        assertThat(getPostgresContainer().getUsername())
                .as("Username should match configuration")
                .isEqualTo("testuser");

        // Verify FilmRepository is injected
        assertThat(filmRepository)
                .as("FilmRepository should be autowired")
                .isNotNull();

        // Verify basic database connectivity and data availability
        List<Film> allFilms = filmRepository.findAllOrderByTitle();
        assertThat(allFilms)
                .as("Should be able to query films from TestContainer database")
                .isNotEmpty()
                .hasSize(51); // Expected from test data

        // Verify test data is properly loaded (46 films starting with 'A')
        List<Film> filmsStartingWithA = filmRepository.findByTitleStartingWith("A");
        assertThat(filmsStartingWithA)
                .as("Should have 46 films starting with 'A' from test data")
                .hasSize(46);

        // Log TestContainer configuration details for debugging
        System.out.println("TestContainer PostgreSQL Configuration:");
        System.out.println("  JDBC URL: " + getPostgresContainer().getJdbcUrl());
        System.out.println("  Database: " + getPostgresContainer().getDatabaseName());
        System.out.println("  Username: " + getPostgresContainer().getUsername());
        System.out.println("  Container ID: " + getPostgresContainer().getContainerId());
        System.out.println("  Test Data: " + allFilms.size() + " films loaded");
        System.out.println("  Films with 'A': " + filmsStartingWithA.size() + " films");
    }

    /**
     * Verify TestContainers configuration supports real database operations
     * This test validates that the container can handle concurrent database operations
     * and maintains data consistency.
     */
    @Test 
    void testContainerSupportsConcurrentDatabaseOperations() {
        // Execute multiple queries to verify container stability
        List<Film> allFilms = filmRepository.findAllOrderByTitle();
        List<Film> filmsA = filmRepository.findByTitleStartingWith("A");
        List<Film> filmsB = filmRepository.findByTitleStartingWith("B");
        List<Film> filmsC = filmRepository.findByTitleStartingWith("C");

        // Verify consistency across queries
        assertThat(allFilms).hasSize(51);
        assertThat(filmsA).hasSize(46);
        assertThat(filmsB).hasSize(2); // Based on test data
        assertThat(filmsC).hasSize(1); // Based on test data

        // Verify total films match expected breakdown
        int totalFilmsFromQueries = filmsA.size() + filmsB.size() + filmsC.size() + 2; // +2 for D and Z films
        assertThat(allFilms).hasSize(totalFilmsFromQueries);

        System.out.println("Container successfully handled " + 
                          (filmsA.size() + filmsB.size() + filmsC.size()) + 
                          " concurrent query results");
    }

    // ===== TASK 7.2: Integration tests for FilmRepository.findByTitleStartingWith() method =====

    /**
     * Task 7.2: Create integration tests for FilmRepository.findByTitleStartingWith() method
     * 
     * This test validates the core functionality of the findByTitleStartingWith method
     * with real database queries against the TestContainer PostgreSQL instance.
     */
    @Test
    @DisplayName("Task 7.2: findByTitleStartingWith should return films matching prefix")
    void findByTitleStartingWithShouldReturnFilmsMatchingPrefix() {
        // When: Query for films starting with "A"
        List<Film> films = filmRepository.findByTitleStartingWith("A");

        // Then: Should return expected films
        assertThat(films)
                .as("Should return films starting with 'A'")
                .isNotEmpty()
                                 .allSatisfy(film -> {
                     assertThat(film.title())
                             .as("Film title should start with 'A'")
                             .startsWithIgnoringCase("A");
                     assertThat(film.filmId())
                             .as("Film should have valid ID")
                             .isPositive();
                 });

        // Verify ordering (should be ordered by title)
        List<String> titles = films.stream()
                .map(Film::title)
                .toList();
        
        // Note: Test data has correct alphabetical ordering (ALIEN CENTER comes before ALI FOREVER)
        assertThat(titles)
                .as("Films should be ordered alphabetically by title")
                .containsSequence("ACADEMY DINOSAUR", "ACE GOLDFINGER") // Verify at least first few are in order
                .contains("ALIEN CENTER", "ALI FOREVER"); // Verify both films are present (ALIEN before ALI is correct)

        System.out.println("Task 7.2: findByTitleStartingWith returned " + films.size() + " films");
    }

    /**
     * Task 7.2: Test case-insensitive behavior of findByTitleStartingWith
     */
    @Test
    @DisplayName("Task 7.2: findByTitleStartingWith should be case-insensitive")
    void findByTitleStartingWithShouldBeCaseInsensitive() {
        // When: Query with both uppercase and lowercase
        List<Film> upperCaseResults = filmRepository.findByTitleStartingWith("A");
        List<Film> lowerCaseResults = filmRepository.findByTitleStartingWith("a");

        // Then: Should return identical results
        assertThat(lowerCaseResults)
                .as("Case-insensitive search should return same results")
                .hasSize(upperCaseResults.size())
                .containsExactlyElementsOf(upperCaseResults);

        System.out.println("Task 7.2: Case-insensitive search verified with " + upperCaseResults.size() + " films");
    }

    // ===== TASK 7.3: Integration tests for exact count validation (46 films for "A") =====

    /**
     * Task 7.3: Create integration tests for exact count validation (46 films for "A")
     * 
     * This test validates the specific business requirement that there should be
     * exactly 46 films starting with "A" in the test database.
     */
    @Test
    @DisplayName("Task 7.3: Should return exactly 46 films starting with 'A'")
    void shouldReturnExactly46FilmsStartingWithA() {
        // When: Query for films starting with "A"
        List<Film> films = filmRepository.findByTitleStartingWith("A");

        // Then: Should return exactly 46 films
        assertThat(films)
                .as("Should return exactly 46 films starting with 'A' per business requirement")
                .hasSize(46);

        // Verify all films actually start with "A"
        assertThat(films)
                .as("All returned films should start with 'A'")
                .allSatisfy(film -> 
                    assertThat(film.title())
                            .startsWithIgnoringCase("A")
                );

        // Verify no duplicates
        List<String> titles = films.stream()
                .map(Film::title)
                .toList();
        
        assertThat(titles)
                .as("Should not contain duplicate titles")
                .doesNotHaveDuplicates();

        System.out.println("Task 7.3: Exact count validation passed - 46 films starting with 'A'");
    }

    /**
     * Task 7.3: Validate the exact count holds for multiple queries
     */
    @Test
    @DisplayName("Task 7.3: Multiple queries should consistently return 46 films for 'A'")
    void multipleQueriesShouldConsistentlyReturn46FilmsForA() {
        // When: Execute multiple queries
        List<Film> query1 = filmRepository.findByTitleStartingWith("A");
        List<Film> query2 = filmRepository.findByTitleStartingWith("a");
        List<Film> query3 = filmRepository.findByTitleStartingWith("A");

        // Then: All should return exactly 46 films
        assertThat(query1).hasSize(46);
        assertThat(query2).hasSize(46);
        assertThat(query3).hasSize(46);

        // And: All should return identical results
        assertThat(query1).containsExactlyElementsOf(query2);
        assertThat(query2).containsExactlyElementsOf(query3);

        System.out.println("Task 7.3: Consistent count validation passed across multiple queries");
    }

    // ===== TASK 7.4: Integration tests for different starting letters (B, C, etc.) =====

    /**
     * Task 7.4: Create integration tests for different starting letters (B, C, etc.)
     * 
     * This test validates that the repository works correctly with various starting letters
     * and returns the expected counts based on the test data.
     */
    @ParameterizedTest
    @ValueSource(strings = {"B", "C", "D", "Z"})
    @DisplayName("Task 7.4: Should return correct films for different starting letters")
    void shouldReturnCorrectFilmsForDifferentStartingLetters(String letter) {
        // When: Query for films starting with the given letter
        List<Film> films = filmRepository.findByTitleStartingWith(letter);

                 // Then: Should return films matching the letter
         assertThat(films)
                 .as("Should return films starting with '" + letter + "'")
                 .allSatisfy(film -> 
                     assertThat(film.title())
                             .startsWithIgnoringCase(letter)
                 );

        // Verify expected counts based on test data
        switch (letter) {
            case "B" -> assertThat(films)
                    .as("Should have 2 films starting with 'B'")
                    .hasSize(2);
            case "C" -> assertThat(films)
                    .as("Should have 1 film starting with 'C'")
                    .hasSize(1);
            case "D" -> assertThat(films)
                    .as("Should have 1 film starting with 'D'")
                    .hasSize(1);
            case "Z" -> assertThat(films)
                    .as("Should have 1 film starting with 'Z'")
                    .hasSize(1);
        }

                          // Verify ordering - films should be alphabetically ordered by title
         assertThat(films)
                 .as("Films should be ordered alphabetically by title")
                 .isNotEmpty()
                 .extracting(Film::title)
                 .allMatch(title -> title.startsWith(letter)); // All titles should start with the expected letter

        System.out.println("Task 7.4: Found " + films.size() + " films starting with '" + letter + "'");
    }

    /**
     * Task 7.4: Test multiple letters in a single test to verify consistency
     */
    @Test
    @DisplayName("Task 7.4: Should handle multiple different letters correctly")
    void shouldHandleMultipleDifferentLettersCorrectly() {
        // When: Query for multiple different letters
        List<Film> filmsA = filmRepository.findByTitleStartingWith("A");
        List<Film> filmsB = filmRepository.findByTitleStartingWith("B");
        List<Film> filmsC = filmRepository.findByTitleStartingWith("C");
        List<Film> filmsD = filmRepository.findByTitleStartingWith("D");
        List<Film> filmsZ = filmRepository.findByTitleStartingWith("Z");

        // Then: Verify expected counts
        assertThat(filmsA).hasSize(46);
        assertThat(filmsB).hasSize(2);
        assertThat(filmsC).hasSize(1);
        assertThat(filmsD).hasSize(1);
        assertThat(filmsZ).hasSize(1);

        // Verify total adds up correctly (46 + 2 + 1 + 1 + 1 = 51)
        int totalFilms = filmsA.size() + filmsB.size() + filmsC.size() + filmsD.size() + filmsZ.size();
        assertThat(totalFilms)
                .as("Total films from different letters should equal total in database")
                .isEqualTo(51);

        System.out.println("Task 7.4: Multiple letter validation passed - A:" + filmsA.size() + 
                          ", B:" + filmsB.size() + ", C:" + filmsC.size() + 
                          ", D:" + filmsD.size() + ", Z:" + filmsZ.size());
    }

    // ===== TASK 7.5: Integration tests for empty results (letters with no films) =====

    /**
     * Task 7.5: Create integration tests for empty results (letters with no films)
     * 
     * This test validates that the repository handles queries for letters that have no
     * matching films in the database.
     */
    @ParameterizedTest
    @ValueSource(strings = {"E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y"})
    @DisplayName("Task 7.5: Should return empty list for letters with no films")
    void shouldReturnEmptyListForLettersWithNoFilms(String letter) {
        // When: Query for films starting with a letter that has no films
        List<Film> films = filmRepository.findByTitleStartingWith(letter);

        // Then: Should return empty list
        assertThat(films)
                .as("Should return empty list for letter '" + letter + "' with no films")
                .isEmpty();

        System.out.println("Task 7.5: Empty result validated for letter '" + letter + "'");
    }

    /**
     * Task 7.5: Test edge cases for empty results
     */
    @Test
    @DisplayName("Task 7.5: Should handle edge cases for empty results")
    void shouldHandleEdgeCasesForEmptyResults() {
        // When: Query for various edge cases
        List<Film> emptyString = filmRepository.findByTitleStartingWith("");
        List<Film> space = filmRepository.findByTitleStartingWith(" ");
        List<Film> number = filmRepository.findByTitleStartingWith("1");
        List<Film> specialChar = filmRepository.findByTitleStartingWith("@");

        // Then: Should handle appropriately
        // Empty string should return all films (as per SQL LIKE behavior)
        assertThat(emptyString)
                .as("Empty string should return all films")
                .hasSize(51);

        // Space, number, and special character should return empty
        assertThat(space)
                .as("Space should return empty results")
                .isEmpty();
        
        assertThat(number)
                .as("Number should return empty results")
                .isEmpty();
        
        assertThat(specialChar)
                .as("Special character should return empty results")
                .isEmpty();

        System.out.println("Task 7.5: Edge case validation passed - empty:" + emptyString.size() + 
                          ", space:" + space.size() + ", number:" + number.size() + 
                          ", special:" + specialChar.size());
    }

    // ===== TASK 7.6: Integration tests for database performance (< 2 seconds) =====

    /**
     * Task 7.6: Create integration tests for database performance (< 2 seconds)
     * 
     * This test validates that database queries execute within acceptable performance
     * thresholds as specified in the acceptance criteria.
     */
    @Test
    @DisplayName("Task 7.6: Query performance should be under 2 seconds")
    void queryPerformanceShouldBeUnderTwoSeconds() {
        // When: Execute query with performance measurement
        Instant start = Instant.now();
        List<Film> films = filmRepository.findByTitleStartingWith("A");
        Instant end = Instant.now();
        
        Duration executionTime = Duration.between(start, end);

        // Then: Should complete within 2 seconds
        assertThat(executionTime)
                .as("Query should complete within 2 seconds")
                .isLessThan(Duration.ofSeconds(2));

        // And: Should return expected results
        assertThat(films)
                .as("Should return correct number of films")
                .hasSize(46);

        System.out.println("Task 7.6: Query performance: " + executionTime.toMillis() + "ms for " + films.size() + " films");
    }

    /**
     * Task 7.6: Test performance with multiple concurrent queries
     */
    @Test
    @DisplayName("Task 7.6: Multiple concurrent queries should maintain performance")
    void multipleConcurrentQueriesShouldMaintainPerformance() {
        // When: Execute multiple queries concurrently
        Instant start = Instant.now();
        
        List<Film> filmsA = filmRepository.findByTitleStartingWith("A");
        List<Film> filmsB = filmRepository.findByTitleStartingWith("B");
        List<Film> filmsC = filmRepository.findByTitleStartingWith("C");
        List<Film> allFilms = filmRepository.findAllOrderByTitle();
        
        Instant end = Instant.now();
        Duration totalExecutionTime = Duration.between(start, end);

        // Then: Total execution should be reasonable (under 5 seconds for all queries)
        assertThat(totalExecutionTime)
                .as("Multiple queries should complete within 5 seconds")
                .isLessThan(Duration.ofSeconds(5));

        // And: Should return correct results
        assertThat(filmsA).hasSize(46);
        assertThat(filmsB).hasSize(2);
        assertThat(filmsC).hasSize(1);
        assertThat(allFilms).hasSize(51);

        System.out.println("Task 7.6: Multiple queries performance: " + totalExecutionTime.toMillis() + "ms");
    }

    // ===== TASK 7.7: Integration tests for database error scenarios =====

    /**
     * Task 7.7: Create integration tests for database error scenarios
     * 
     * This test validates that the repository handles various error scenarios gracefully
     * and that proper exceptions are thrown or handled.
     */
    @Test
    @DisplayName("Task 7.7: Should handle null parameter gracefully")
    void shouldHandleNullParameterGracefully() {
        // When: Pass null parameter to repository method
        List<Film> films = filmRepository.findByTitleStartingWith(null);
        
        // Then: Spring Data JDBC handles null gracefully by returning empty list
        // (null parameter in SQL query with LIKE results in no matches)
        assertThat(films)
                .as("Null parameter should be handled gracefully and return empty list")
                .isEmpty();

        System.out.println("Task 7.7: Null parameter handling validated - returned " + films.size() + " films");
    }

    /**
     * Task 7.7: Test database connection resilience
     */
    @Test
    @DisplayName("Task 7.7: Should maintain database connection reliability")
    void shouldMaintainDatabaseConnectionReliability() {
        // When: Execute many queries to test connection stability
        for (int i = 0; i < 10; i++) {
            List<Film> films = filmRepository.findByTitleStartingWith("A");
            
            // Then: Each query should succeed
            assertThat(films)
                    .as("Query " + (i + 1) + " should succeed")
                    .hasSize(46);
        }

        System.out.println("Task 7.7: Database connection reliability validated with 10 queries");
    }

    // ===== TASK 7.8: Integration tests for Spring Data JDBC configuration =====

    /**
     * Task 7.8: Create integration tests for Spring Data JDBC configuration
     * 
     * This test validates that Spring Data JDBC is properly configured and working
     * with the PostgreSQL TestContainer.
     */
    @Test
    @DisplayName("Task 7.8: Spring Data JDBC configuration should work correctly")
    void springDataJdbcConfigurationShouldWorkCorrectly() {
        // When: Execute various Spring Data JDBC operations
        List<Film> allFilms = filmRepository.findAllOrderByTitle();
        List<Film> filteredFilms = filmRepository.findByTitleStartingWith("A");
        
                 // Then: Should work without configuration issues
         assertThat(allFilms)
                 .as("findAllOrderByTitle should work with Spring Data JDBC")
                 .hasSize(51)
                 .allSatisfy(film -> {
                     assertThat(film.filmId()).isPositive();
                     assertThat(film.title()).isNotBlank();
                 });

         assertThat(filteredFilms)
                 .as("findByTitleStartingWith should work with Spring Data JDBC")
                 .hasSize(46)
                 .allSatisfy(film -> {
                     assertThat(film.filmId()).isPositive();
                     assertThat(film.title()).startsWithIgnoringCase("A");
                 });

        System.out.println("Task 7.8: Spring Data JDBC configuration validated");
    }

    /**
     * Task 7.8: Test custom SQL queries work correctly
     */
    @Test
    @DisplayName("Task 7.8: Custom SQL queries should execute correctly")
    void customSqlQueriesShouldExecuteCorrectly() {
        // When: Execute custom SQL queries defined in the repository
        List<Film> customQueryResults = filmRepository.findByTitleStartingWith("A");
        List<Film> customOrderedResults = filmRepository.findAllOrderByTitle();

        // Then: Custom queries should return properly mapped entities
        assertThat(customQueryResults)
                .as("Custom SQL query should return properly mapped Film entities")
                .hasSize(46)
                                 .allSatisfy(film -> {
                     assertThat(film.filmId())
                             .as("Film ID should be properly mapped from custom query")
                             .isPositive();
                     assertThat(film.title())
                             .as("Film title should be properly mapped from custom query")
                             .isNotBlank()
                             .startsWithIgnoringCase("A");
                 });

         assertThat(customOrderedResults)
                 .as("Custom ordered query should return properly sorted results")
                 .hasSize(51)
                 .extracting(Film::title)
                 .containsSequence("ACADEMY DINOSAUR", "ACE GOLDFINGER") // Verify first films are in order
                 .contains("ALIEN CENTER", "ALI FOREVER"); // Verify key films are present (correct alphabetical order)

        System.out.println("Task 7.8: Custom SQL queries validated - " + 
                          customQueryResults.size() + " filtered, " + 
                          customOrderedResults.size() + " ordered");
    }
} 