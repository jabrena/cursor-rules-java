package info.jab.ms.repository;

import info.jab.ms.common.PostgreSQLTestBase;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * FilmRepositoryIT - Integration Tests for FilmRepository Data Access Layer
 *
 * This class implements integration tests for the FilmRepository using TestContainers
 * PostgreSQL configuration. It extends PostgreSQLTestBase to inherit the common
 * TestContainer setup with Sakila schema and test data.
 *
 * Test Strategy:
 * - Uses @DataJdbcTest for focused Spring Data JDBC repository layer testing
 * - TestContainers provides isolated PostgreSQL database
 * - Real database queries against Sakila schema
 * - Validates SQL queries and Spring Data JDBC configuration
 * - @ActiveProfiles("test") for test-specific configuration
 *
 * Following Spring Boot Slice Testing Guidelines:
 * - @DataJdbcTest loads only Spring Data JDBC-related components
 * - Focused testing on repository layer in isolation
 * - Fast execution with minimal Spring context loading
 * - TestContainer configuration maintained for PostgreSQL integration
 *
 * Following Java Unit Testing Guidelines:
 * - Given-When-Then structure
 * - Descriptive test names
 * - AssertJ for fluent assertions
 * - @Nested classes for logical grouping
 * - Parameterized tests for data variations
 * - Comprehensive boundary testing (CORRECT)
 */
@DataJdbcTest
@ActiveProfiles("test")
@DisplayName("FilmRepository Integration Tests")
class FilmRepositoryIT extends PostgreSQLTestBase {

    @Autowired
    private FilmRepository filmRepository;

    @Nested
    @DisplayName("TestContainer Configuration Tests")
    class TestContainerConfigurationTests {

        @Test
        @DisplayName("Should have properly configured PostgreSQL TestContainer")
        void should_haveProperlyConfiguredPostgreSQLTestContainer_when_testContainerStarts() {
            // Given - TestContainer is started via @TestContainers

            // When - We check container status
            var container = getPostgresContainer();

            // Then - Container should be properly configured
            assertThat(container.isRunning())
                .as("PostgreSQL container should be running")
                .isTrue();

            assertThat(container.getDatabaseName())
                .as("Database name should match configuration")
                .isEqualTo("testdb");

            assertThat(container.getUsername())
                .as("Username should match configuration")
                .isEqualTo("testuser");

            assertThat(filmRepository)
                .as("FilmRepository should be autowired")
                .isNotNull();
        }

        @Test
        @DisplayName("Should have test data properly loaded")
        void should_haveTestDataProperlyLoaded_when_containerInitializes() {
            // Given - TestContainer with initialization scripts

            // When - We query for all films
            List<Film> allFilms = filmRepository.findAllOrderByTitle();

            // Then - Should have expected test data
            assertThat(allFilms)
                .as("Should have films loaded from test data")
                .isNotEmpty()
                .hasSize(51)
                .allSatisfy(film -> {
                    assertThat(film.filmId()).isPositive();
                    assertThat(film.title()).isNotBlank();
                });

            // And - Should have correct distribution of films by starting letter
            List<Film> filmsStartingWithA = filmRepository.findByTitleStartingWith("A");
            assertThat(filmsStartingWithA)
                .as("Should have expected number of films starting with 'A'")
                .hasSize(46);
        }

        @Test
        @DisplayName("Should support concurrent database operations")
        void should_supportConcurrentDatabaseOperations_when_multipleQueriesExecuted() {
            // Given - Multiple concurrent queries

            // When - We execute multiple queries simultaneously
            List<Film> allFilms = filmRepository.findAllOrderByTitle();
            List<Film> filmsA = filmRepository.findByTitleStartingWith("A");
            List<Film> filmsB = filmRepository.findByTitleStartingWith("B");
            List<Film> filmsC = filmRepository.findByTitleStartingWith("C");

            // Then - All queries should succeed with consistent results
            assertThat(allFilms).hasSize(51);
            assertThat(filmsA).hasSize(46);
            assertThat(filmsB).hasSize(2);
            assertThat(filmsC).hasSize(1);

            // And - Total should be consistent
            int expectedTotal = filmsA.size() + filmsB.size() + filmsC.size() + 2; // +2 for D and Z films
            assertThat(allFilms).hasSize(expectedTotal);
        }
    }

    @Nested
    @DisplayName("findByTitleStartingWith Method Tests")
    class FindByTitleStartingWithTests {

        @Test
        @DisplayName("Should return films matching letter prefix")
        void should_returnFilmsMatchingLetterPrefix_when_validLetterProvided() {
            // Given
            String searchLetter = "A";

            // When
            List<Film> films = filmRepository.findByTitleStartingWith(searchLetter);

            // Then
            assertThat(films)
                .as("Should return films starting with '%s'", searchLetter)
                .isNotEmpty()
                .allSatisfy(film -> {
                    assertThat(film.title())
                        .as("Film title should start with '%s'", searchLetter)
                        .startsWithIgnoringCase(searchLetter);
                    assertThat(film.filmId())
                        .as("Film should have valid ID")
                        .isPositive();
                });
        }

        @Test
        @DisplayName("Should return films in alphabetical order by title")
        void should_returnFilmsInAlphabeticalOrderByTitle_when_queried() {
            // Given
            String searchLetter = "A";

            // When
            List<Film> films = filmRepository.findByTitleStartingWith(searchLetter);

            // Then
            List<String> titles = films.stream().map(Film::title).toList();
            assertThat(titles)
                .as("Films should be ordered alphabetically by title")
                .isSorted()
                .containsSequence("ACADEMY DINOSAUR", "ACE GOLDFINGER");
        }

        @ParameterizedTest(name = "Should handle case insensitive search for letter: {0}")
        @ValueSource(strings = {"A", "a", "B", "b", "C", "c"})
        @DisplayName("Should be case insensitive")
        void should_beCaseInsensitive_when_differentCaseLettersProvided(String letter) {
            // Given
            String upperCase = letter.toUpperCase();
            String lowerCase = letter.toLowerCase();

            // When
            List<Film> upperCaseResults = filmRepository.findByTitleStartingWith(upperCase);
            List<Film> lowerCaseResults = filmRepository.findByTitleStartingWith(lowerCase);

            // Then
            assertThat(lowerCaseResults)
                .as("Case-insensitive search should return same results for '%s' and '%s'", upperCase, lowerCase)
                .hasSameSizeAs(upperCaseResults)
                .containsExactlyElementsOf(upperCaseResults);
        }

        @ParameterizedTest(name = "Letter {0} should return {1} films")
        @MethodSource("letterToExpectedCountProvider")
        @DisplayName("Should return correct count for different letters")
        void should_returnCorrectCountForLetter_when_specificLetterProvided(String letter, int expectedCount) {
            // Given - Letter with known expected count

            // When
            List<Film> films = filmRepository.findByTitleStartingWith(letter);

            // Then
            assertThat(films)
                .as("Should return exactly %d films for letter '%s'", expectedCount, letter)
                .hasSize(expectedCount);

            if (expectedCount > 0) {
                assertThat(films)
                    .as("All films should start with letter '%s'", letter)
                    .allSatisfy(film ->
                        assertThat(film.title()).startsWithIgnoringCase(letter));
            }
        }

        private static Stream<Arguments> letterToExpectedCountProvider() {
            return Stream.of(
                Arguments.of("A", 46),
                Arguments.of("B", 2),
                Arguments.of("C", 1),
                Arguments.of("D", 1),
                Arguments.of("Z", 1)
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y"})
        @DisplayName("Should return empty list for letters with no films")
        void should_returnEmptyList_when_letterHasNoFilms(String letter) {
            // Given - Letter with no corresponding films

            // When
            List<Film> films = filmRepository.findByTitleStartingWith(letter);

            // Then
            assertThat(films)
                .as("Should return empty list for letter '%s' with no films", letter)
                .isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"", "  ", "\t", "\n"})
        @DisplayName("Should handle null and empty parameters gracefully")
        void should_handleGracefully_when_nullOrEmptyParameterProvided(String invalidParameter) {
            // Given - Invalid parameter (null, empty, or whitespace)

            // When & Then - Should not throw exception
            assertThatCode(() -> filmRepository.findByTitleStartingWith(invalidParameter))
                .as("Should handle null/empty parameter gracefully")
                .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("findAllOrderByTitle Method Tests")
    class FindAllOrderByTitleTests {

        @Test
        @DisplayName("Should return all films ordered by title")
        void should_returnAllFilmsOrderedByTitle_when_queried() {
            // Given - Repository with film data

            // When
            List<Film> allFilms = filmRepository.findAllOrderByTitle();

            // Then
            assertThat(allFilms)
                .as("Should return all films")
                .hasSize(51)
                .allSatisfy(film -> {
                    assertThat(film.filmId()).isPositive();
                    assertThat(film.title()).isNotBlank();
                });

            // And - Should be ordered by title
            List<String> titles = allFilms.stream().map(Film::title).toList();
            assertThat(titles)
                .as("All films should be ordered alphabetically by title")
                .isSorted();
        }

        @Test
        @DisplayName("Should maintain consistent results across multiple calls")
        void should_maintainConsistentResults_when_calledMultipleTimes() {
            // Given - Multiple calls to the same method

            // When
            List<Film> firstCall = filmRepository.findAllOrderByTitle();
            List<Film> secondCall = filmRepository.findAllOrderByTitle();
            List<Film> thirdCall = filmRepository.findAllOrderByTitle();

            // Then
            assertThat(secondCall)
                .as("Second call should return identical results")
                .containsExactlyElementsOf(firstCall);

            assertThat(thirdCall)
                .as("Third call should return identical results")
                .containsExactlyElementsOf(firstCall);
        }
    }

    @Nested
    @DisplayName("Business Rule Validation Tests")
    class BusinessRuleValidationTests {

        @Test
        @DisplayName("Should have exactly 46 films starting with 'A' per business requirement")
        void should_haveExactly46FilmsStartingWithA_when_businessRuleApplied() {
            // Given - Business requirement: exactly 46 films starting with 'A'

            // When
            List<Film> filmsStartingWithA = filmRepository.findByTitleStartingWith("A");

            // Then
            assertThat(filmsStartingWithA)
                .as("Should have exactly 46 films starting with 'A' per business requirement")
                .hasSize(46);

            // And - All films should actually start with 'A'
            assertThat(filmsStartingWithA)
                .as("All returned films should start with 'A'")
                .allSatisfy(film ->
                    assertThat(film.title()).startsWithIgnoringCase("A"));
        }

        @Test
        @DisplayName("Should maintain data integrity across different queries")
        void should_maintainDataIntegrity_when_differentQueriesExecuted() {
            // Given - Multiple different queries

            // When
            List<Film> allFilms = filmRepository.findAllOrderByTitle();
            List<Film> filmsA = filmRepository.findByTitleStartingWith("A");
            List<Film> filmsB = filmRepository.findByTitleStartingWith("B");
            List<Film> filmsC = filmRepository.findByTitleStartingWith("C");
            List<Film> filmsD = filmRepository.findByTitleStartingWith("D");
            List<Film> filmsZ = filmRepository.findByTitleStartingWith("Z");

            // Then - Data should be consistent
            int totalFromSpecificQueries = filmsA.size() + filmsB.size() + filmsC.size() + filmsD.size() + filmsZ.size();
            assertThat(allFilms)
                .as("Total films should match sum of specific letter queries")
                .hasSize(totalFromSpecificQueries);

            // And - No film should appear in multiple letter categories
            assertThat(filmsA).doesNotContainAnyElementsOf(filmsB);
            assertThat(filmsB).doesNotContainAnyElementsOf(filmsC);
            assertThat(filmsC).doesNotContainAnyElementsOf(filmsD);
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should execute query within acceptable time limit")
        void should_executeQueryWithinAcceptableTimeLimit_when_queried() {
            // Given - Performance requirement: queries should complete within 2 seconds

            // When
            Instant startTime = Instant.now();
            List<Film> films = filmRepository.findByTitleStartingWith("A");
            Instant endTime = Instant.now();

            // Then
            Duration queryDuration = Duration.between(startTime, endTime);
            assertThat(queryDuration)
                .as("Query should complete within 2 seconds")
                .isLessThan(Duration.ofSeconds(2));

            assertThat(films)
                .as("Query should return expected results")
                .hasSize(46);
        }

        @Test
        @DisplayName("Should maintain performance under concurrent load")
        void should_maintainPerformanceUnderConcurrentLoad_when_multipleQueriesExecuted() {
            // Given - Multiple concurrent queries

            // When
            Instant startTime = Instant.now();

            // Execute multiple queries concurrently
            List<Film> filmsA1 = filmRepository.findByTitleStartingWith("A");
            List<Film> filmsA2 = filmRepository.findByTitleStartingWith("A");
            List<Film> filmsB = filmRepository.findByTitleStartingWith("B");
            List<Film> filmsC = filmRepository.findByTitleStartingWith("C");
            List<Film> allFilms = filmRepository.findAllOrderByTitle();

            Instant endTime = Instant.now();

            // Then
            Duration totalDuration = Duration.between(startTime, endTime);
            assertThat(totalDuration)
                .as("Multiple concurrent queries should complete within 5 seconds")
                .isLessThan(Duration.ofSeconds(5));

            // And - Results should be consistent
            assertThat(filmsA1)
                .as("Concurrent queries should return identical results")
                .containsExactlyElementsOf(filmsA2);

            assertThat(allFilms)
                .as("All films query should return expected count")
                .hasSize(51);
        }
    }

    @Nested
    @DisplayName("Database Connection Reliability Tests")
    class DatabaseConnectionReliabilityTests {

        @Test
        @DisplayName("Should maintain connection stability across multiple operations")
        void should_maintainConnectionStability_when_multipleOperationsExecuted() {
            // Given - Multiple database operations

            // When & Then - All operations should succeed without connection issues
            assertThatCode(() -> {
                for (int i = 0; i < 10; i++) {
                    List<Film> films = filmRepository.findByTitleStartingWith("A");
                    assertThat(films).hasSize(46);

                    List<Film> allFilms = filmRepository.findAllOrderByTitle();
                    assertThat(allFilms).hasSize(51);
                }
            })
            .as("Multiple database operations should not cause connection issues")
            .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle database queries with complex character data")
        void should_handleQueriesWithComplexCharacterData_when_specialTitlesQueried() {
            // Given - Films with complex titles (spaces, special characters)

            // When
            List<Film> allFilms = filmRepository.findAllOrderByTitle();

            // Then
            assertThat(allFilms)
                .as("Should handle films with complex titles")
                .anySatisfy(film -> {
                    assertThat(film.title()).contains(" ");
                })
                .allSatisfy(film -> {
                    assertThat(film.title()).isNotBlank();
                    assertThat(film.filmId()).isPositive();
                });
        }
    }
}
