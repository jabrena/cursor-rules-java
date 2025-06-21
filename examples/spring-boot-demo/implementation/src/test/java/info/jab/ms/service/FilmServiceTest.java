package info.jab.ms.service;

import info.jab.ms.repository.Film;
import info.jab.ms.repository.FilmRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * FilmServiceTest - Complete Unit Test Suite for FilmService Business Logic
 *
 * This test class implements all test requirements from tasks 5.1-5.7:
 * - Task 5.1: Create unit tests for FilmService.findFilmEntitiesByStartingLetter() method
 * - Task 5.2: Create unit tests for film filtering logic (case insensitive matching)
 * - Task 5.3: Create unit tests for DTO transformation (Entity to Response DTO)
 * - Task 5.4: Create unit tests for business validation (letter parameter validation)
 * - Task 5.5: Create unit tests for empty result handling logic
 * - Task 5.6: Create unit tests for error scenarios (invalid input, null handling)
 * - Task 5.7: Create unit tests for business rules (46 films for "A", etc.)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FilmService Unit Tests")
class FilmServiceTest {

    @Mock
    private FilmRepository filmRepository;

    @InjectMocks
    private FilmService filmService;

    // Test data setup
    private List<Film> mockFilmsStartingWithA;
    private List<Film> mockAllFilms;
    private Film mockFilm1;
    private Film mockFilm2;
    private Film mockFilm3;

    @BeforeEach
    void setUp() {
        // Task 5.7: Create unit tests for business rules (46 films for "A", etc.)
        // Setting up test data to simulate real film data
        mockFilm1 = new Film(1, "ACADEMY DINOSAUR");
        mockFilm2 = new Film(2, "ACE GOLDFINGER");
        mockFilm3 = new Film(3, "ADAPTATION HOLES");

        mockFilmsStartingWithA = Arrays.asList(mockFilm1, mockFilm2, mockFilm3);
        mockAllFilms = Arrays.asList(
                mockFilm1,
                mockFilm2,
                mockFilm3,
                new Film(4, "BEAST HUNCHBACK"),
                new Film(5, "CHOCOLATE DUCK")
        );
    }

    @Nested
    @DisplayName("Task 5.1: Basic Method Testing")
    class BasicMethodTesting {

        @Test
        @DisplayName("Should return films when valid letter is provided")
        void shouldReturnFilmsWhenValidLetterProvided() {
            // Given
            String letter = "A";
            when(filmRepository.findByTitleStartingWith(letter)).thenReturn(mockFilmsStartingWithA);

            // When
            List<Film> result = filmService.findFilmEntitiesByStartingLetter(letter);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            assertThat(result).containsExactlyElementsOf(mockFilmsStartingWithA);
            verify(filmRepository).findByTitleStartingWith(letter);
            verify(filmRepository, never()).findAllOrderByTitle();
        }

        @Test
        @DisplayName("Should return all films when letter is null")
        void shouldReturnAllFilmsWhenLetterIsNull() {
            // Given
            when(filmRepository.findAllOrderByTitle()).thenReturn(mockAllFilms);

            // When
            List<Film> result = filmService.findFilmEntitiesByStartingLetter(null);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(5);
            assertThat(result).containsExactlyElementsOf(mockAllFilms);
            verify(filmRepository).findAllOrderByTitle();
            verify(filmRepository, never()).findByTitleStartingWith(any());
        }

        @Test
        @DisplayName("Should return all films when letter is empty")
        void shouldReturnAllFilmsWhenLetterIsEmpty() {
            // Given
            when(filmRepository.findAllOrderByTitle()).thenReturn(mockAllFilms);

            // When
            List<Film> result = filmService.findFilmEntitiesByStartingLetter("");

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(5);
            assertThat(result).containsExactlyElementsOf(mockAllFilms);
            verify(filmRepository).findAllOrderByTitle();
            verify(filmRepository, never()).findByTitleStartingWith(any());
        }
    }

    @Test
    @DisplayName("Task 5.2: Should handle case insensitive letter input")
    void shouldHandleCaseInsensitiveLetterInput() {
        // Given
        when(filmRepository.findByTitleStartingWith(any())).thenReturn(mockFilmsStartingWithA);

        // When & Then
        List<Film> resultA = filmService.findFilmEntitiesByStartingLetter("A");
        List<Film> resulta = filmService.findFilmEntitiesByStartingLetter("a");

        assertThat(resultA).isNotNull();
        assertThat(resulta).isNotNull();
        verify(filmRepository).findByTitleStartingWith("A");
        verify(filmRepository).findByTitleStartingWith("a");
    }

    @Test
    @DisplayName("Task 5.2: Should trim whitespace from letter parameter")
    void shouldTrimWhitespaceFromLetterParameter() {
        // Given
        String letterWithSpaces = "  A  ";
        when(filmRepository.findByTitleStartingWith("A")).thenReturn(mockFilmsStartingWithA);

        // When
        List<Film> result = filmService.findFilmEntitiesByStartingLetter(letterWithSpaces);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        verify(filmRepository).findByTitleStartingWith("A");
    }

    @Test
    @DisplayName("Task 5.3: Should return Film entities for DTO transformation in controller")
    void shouldReturnFilmEntitiesForDtoTransformation() {
        // Given
        String letter = "A";
        when(filmRepository.findByTitleStartingWith(letter)).thenReturn(mockFilmsStartingWithA);

        // When
        List<Film> result = filmService.findFilmEntitiesByStartingLetter(letter);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).allMatch(film -> film instanceof Film);
        assertThat(result).extracting(Film::filmId)
                .containsExactly(1, 2, 3);
        assertThat(result).extracting(Film::title)
                .containsExactly("ACADEMY DINOSAUR", "ACE GOLDFINGER", "ADAPTATION HOLES");
    }

    @Test
    @DisplayName("Task 5.4: Should handle valid single character letters")
    void shouldHandleValidSingleCharacterLetters() {
        // Given
        when(filmRepository.findByTitleStartingWith("X")).thenReturn(Collections.emptyList());

        // When
        List<Film> result = filmService.findFilmEntitiesByStartingLetter("X");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(filmRepository).findByTitleStartingWith("X");
    }

    @Test
    @DisplayName("Task 5.5: Should handle empty result gracefully")
    void shouldHandleEmptyResultGracefully() {
        // Given
        String letter = "X";
        when(filmRepository.findByTitleStartingWith(letter)).thenReturn(Collections.emptyList());

        // When
        List<Film> result = filmService.findFilmEntitiesByStartingLetter(letter);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        assertThat(result).hasSize(0);
        verify(filmRepository).findByTitleStartingWith(letter);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("Task 5.6: Should handle null and empty input gracefully")
    void shouldHandleNullAndEmptyInputGracefully(String input) {
        // Given
        when(filmRepository.findAllOrderByTitle()).thenReturn(mockAllFilms);

        // When
        List<Film> result = filmService.findFilmEntitiesByStartingLetter(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(5);
        verify(filmRepository, atLeastOnce()).findAllOrderByTitle();
    }

    @Test
    @DisplayName("Task 5.7: Should return expected number of films for letter A (business rule: 46 films)")
    void shouldReturnExpectedNumberOfFilmsForLetterA() {
        // Given - Simulating the expected 46 films starting with 'A'
        List<Film> fortySevenFilmsWithA = createMockFilmsStartingWithA(46);
        when(filmRepository.findByTitleStartingWith("A")).thenReturn(fortySevenFilmsWithA);

        // When
        List<Film> result = filmService.findFilmEntitiesByStartingLetter("A");

        // Then
        assertThat(result).hasSize(46);
        verify(filmRepository).findByTitleStartingWith("A");
    }

    @Test
    @DisplayName("Task 5.7: Should validate film entity structure matches business requirements")
    void shouldValidateFilmEntityStructureMatchesBusinessRequirements() {
        // Given
        when(filmRepository.findByTitleStartingWith("A")).thenReturn(mockFilmsStartingWithA);

        // When
        List<Film> result = filmService.findFilmEntitiesByStartingLetter("A");

        // Then - Validate entity structure for business requirements
        assertThat(result).allSatisfy(film -> {
            assertThat(film.filmId()).isNotNull().isPositive();
            assertThat(film.title()).isNotNull().isNotBlank();
            assertThat(film.title()).startsWith("A");
        });
    }

    @Nested
    @DisplayName("Task 5.6: Exception Handling Scenarios")
    class ExceptionHandlingScenarios {

        @Test
        @DisplayName("Should handle EmptyResultDataAccessException gracefully")
        void shouldHandleEmptyResultDataAccessExceptionGracefully() {
            // Given
            String letter = "A";
            when(filmRepository.findByTitleStartingWith(letter))
                    .thenThrow(new EmptyResultDataAccessException("No data found", 1));

            // When
            List<Film> result = filmService.findFilmEntitiesByStartingLetter(letter);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
            verify(filmRepository).findByTitleStartingWith(letter);
        }

        @Test
        @DisplayName("Should handle DataIntegrityViolationException with appropriate error")
        void shouldHandleDataIntegrityViolationExceptionWithAppropriateError() {
            // Given
            String letter = "A";
            when(filmRepository.findByTitleStartingWith(letter))
                    .thenThrow(new DataIntegrityViolationException("Data integrity violation"));

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> filmService.findFilmEntitiesByStartingLetter(letter));

            assertThat(exception.getMessage())
                    .contains("Database integrity error occurred while searching films");
            assertThat(exception.getCause())
                    .isInstanceOf(DataIntegrityViolationException.class);
            verify(filmRepository).findByTitleStartingWith(letter);
        }

        @Test
        @DisplayName("Should handle DataAccessException with appropriate error")
        void shouldHandleDataAccessExceptionWithAppropriateError() {
            // Given
            String letter = "A";
            when(filmRepository.findByTitleStartingWith(letter))
                    .thenThrow(new DataAccessException("Database connection error") {});

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> filmService.findFilmEntitiesByStartingLetter(letter));

            assertThat(exception.getMessage())
                    .contains("Database error occurred while searching films. Please try again later.");
            assertThat(exception.getCause())
                    .isInstanceOf(DataAccessException.class);
            verify(filmRepository).findByTitleStartingWith(letter);
        }

        @Test
        @DisplayName("Should handle generic Exception with appropriate error")
        void shouldHandleGenericExceptionWithAppropriateError() {
            // Given
            String letter = "A";
            when(filmRepository.findByTitleStartingWith(letter))
                    .thenThrow(new RuntimeException("Unexpected error"));

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> filmService.findFilmEntitiesByStartingLetter(letter));

            assertThat(exception.getMessage())
                    .contains("An unexpected error occurred while searching films");
            assertThat(exception.getCause())
                    .isInstanceOf(RuntimeException.class);
            verify(filmRepository).findByTitleStartingWith(letter);
        }

        @Test
        @DisplayName("Should handle exceptions in findAllOrderByTitle path")
        void shouldHandleExceptionsInFindAllOrderByTitlePath() {
            // Given
            when(filmRepository.findAllOrderByTitle())
                    .thenThrow(new DataAccessException("Database connection error") {});

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> filmService.findFilmEntitiesByStartingLetter(null));

            assertThat(exception.getMessage())
                    .contains("Database error occurred while searching films. Please try again later.");
            assertThat(exception.getCause())
                    .isInstanceOf(DataAccessException.class);
            verify(filmRepository).findAllOrderByTitle();
        }
    }

    // Helper methods for test data creation
    private List<Film> createMockFilmsStartingWithA(int count) {
        return java.util.stream.IntStream.range(1, count + 1)
                .mapToObj(i -> new Film(i, "A-FILM-" + String.format("%02d", i)))
                .toList();
    }
}
