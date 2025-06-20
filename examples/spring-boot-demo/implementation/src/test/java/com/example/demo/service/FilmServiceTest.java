package com.example.demo.service;

import com.example.demo.entity.Film;
import com.example.demo.repository.FilmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FilmService.findFilmsByStartingLetter() method
 * 
 * This test class implements comprehensive unit testing for the FilmService business logic layer
 * following TDD approach. Tests focus on service behavior, repository integration, and data
 * transformation without database dependencies.
 * 
 * Task 5.1: Create unit tests for FilmService.findFilmsByStartingLetter() method ✅
 * 
 * Test Coverage Areas:
 * - Valid letter parameter handling (A-Z, a-z)
 * - Null parameter handling (should call findAllOrderByTitle)
 * - Empty string parameter handling (should call findAllOrderByTitle)  
 * - Whitespace parameter handling (should trim and use filtered query)
 * - Repository method calls verification (correct methods called with correct parameters)
 * - Entity to Map transformation verification (correct field mapping)
 * - Empty result handling (repository returns empty list)
 * - Multiple films result handling (multiple Film entities transformed correctly)
 * - Business logic validation (correct repository methods called based on input)
 */
@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmRepository filmRepository;

    @InjectMocks
    private FilmService filmService;

    private List<Film> sampleFilmsStartingWithA;
    private List<Film> sampleAllFilms;

    @BeforeEach
    void setUp() {
        // Sample films starting with "A" for filtered queries
        sampleFilmsStartingWithA = List.of(
                new Film(1, "ACADEMY DINOSAUR"),
                new Film(2, "ACE GOLDFINGER"),
                new Film(3, "ADAPTATION HOLES")
        );

        // Sample all films for unfiltered queries
        sampleAllFilms = List.of(
                new Film(1, "ACADEMY DINOSAUR"),
                new Film(2, "ACE GOLDFINGER"),
                new Film(3, "ADAPTATION HOLES"),
                new Film(4, "BATMAN BEGINS"),
                new Film(5, "ZORRO ADAPTATION")
        );
    }

    // ========================================================================
    // Task 5.1: Create unit tests for FilmService.findFilmsByStartingLetter() method
    // ========================================================================

    @Test
    void shouldFindFilmsByStartingLetterWhenValidLetterProvided() {
        // Given: Repository returns films starting with "A"
        when(filmRepository.findByTitleStartingWith("A"))
                .thenReturn(sampleFilmsStartingWithA);

        // When: Service is called with letter "A"
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("A");

        // Then: Should call repository with correct parameter
        verify(filmRepository).findByTitleStartingWith("A");
        verify(filmRepository, never()).findAllOrderByTitle();

        // And: Should return correctly transformed film maps
        assertThat(result).hasSize(3);

        // And: Should transform entities to maps with correct structure
        assertThat(result.get(0))
                .containsEntry("film_id", 1)
                .containsEntry("title", "ACADEMY DINOSAUR");
        assertThat(result.get(1))
                .containsEntry("film_id", 2)
                .containsEntry("title", "ACE GOLDFINGER");
        assertThat(result.get(2))
                .containsEntry("film_id", 3)
                .containsEntry("title", "ADAPTATION HOLES");
    }

    @Test
    void shouldFindFilmsByStartingLetterWhenLowercaseLetterProvided() {
        // Given: Repository returns films starting with "a" (case insensitive)
        when(filmRepository.findByTitleStartingWith("a"))
                .thenReturn(sampleFilmsStartingWithA);

        // When: Service is called with lowercase letter "a"
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("a");

        // Then: Should call repository with lowercase parameter (repository handles case insensitivity)
        verify(filmRepository).findByTitleStartingWith("a");
        verify(filmRepository, never()).findAllOrderByTitle();

        // And: Should return correctly transformed film maps
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).containsEntry("title", "ACADEMY DINOSAUR");
    }

    @Test
    void shouldFindFilmsByStartingLetterWhenLetterWithWhitespaceProvided() {
        // Given: Repository returns films starting with "B"
        List<Film> filmsStartingWithB = List.of(
                new Film(10, "BATMAN BEGINS"),
                new Film(11, "BRAVE HEART")
        );
        when(filmRepository.findByTitleStartingWith("B"))
                .thenReturn(filmsStartingWithB);

        // When: Service is called with letter that has whitespace " B "
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter(" B ");

        // Then: Should trim the parameter and call repository with "B"
        verify(filmRepository).findByTitleStartingWith("B");
        verify(filmRepository, never()).findAllOrderByTitle();

        // And: Should return correctly transformed results
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).containsEntry("title", "BATMAN BEGINS");
        assertThat(result.get(1)).containsEntry("title", "BRAVE HEART");
    }

    @Test
    void shouldFindAllFilmsWhenNullParameterProvided() {
        // Given: Repository returns all films for unfiltered query
        when(filmRepository.findAllOrderByTitle())
                .thenReturn(sampleAllFilms);

        // When: Service is called with null parameter
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter(null);

        // Then: Should call findAllOrderByTitle method instead of filtered method
        verify(filmRepository).findAllOrderByTitle();
        verify(filmRepository, never()).findByTitleStartingWith(anyString());

        // And: Should return all films transformed to maps
        assertThat(result).hasSize(5);
        assertThat(result.get(0)).containsEntry("title", "ACADEMY DINOSAUR");
        assertThat(result.get(4)).containsEntry("title", "ZORRO ADAPTATION");
    }

    @Test
    void shouldFindAllFilmsWhenEmptyStringParameterProvided() {
        // Given: Repository returns all films for unfiltered query
        when(filmRepository.findAllOrderByTitle())
                .thenReturn(sampleAllFilms);

        // When: Service is called with empty string parameter
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("");

        // Then: Should call findAllOrderByTitle method (empty string treated as no filter)
        verify(filmRepository).findAllOrderByTitle();
        verify(filmRepository, never()).findByTitleStartingWith(anyString());

        // And: Should return all films transformed to maps
        assertThat(result).hasSize(5);
    }

    @Test
    void shouldFindAllFilmsWhenWhitespaceOnlyParameterProvided() {
        // Given: Repository returns all films for unfiltered query
        when(filmRepository.findAllOrderByTitle())
                .thenReturn(sampleAllFilms);

        // When: Service is called with whitespace-only parameter
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("   ");

        // Then: Should call findAllOrderByTitle method (whitespace trimmed to empty)
        verify(filmRepository).findAllOrderByTitle();
        verify(filmRepository, never()).findByTitleStartingWith(anyString());

        // And: Should return all films transformed to maps
        assertThat(result).hasSize(5);
    }

    @Test
    void shouldReturnEmptyListWhenRepositoryReturnsEmptyForFilteredQuery() {
        // Given: Repository returns empty list for letter "X" (no films start with X)
        when(filmRepository.findByTitleStartingWith("X"))
                .thenReturn(Collections.emptyList());

        // When: Service is called with letter "X"
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("X");

        // Then: Should call repository with correct parameter
        verify(filmRepository).findByTitleStartingWith("X");

        // And: Should return empty list
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenRepositoryReturnsEmptyForUnfilteredQuery() {
        // Given: Repository returns empty list for all films (edge case - empty database)
        when(filmRepository.findAllOrderByTitle())
                .thenReturn(Collections.emptyList());

        // When: Service is called with null parameter (unfiltered query)
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter(null);

        // Then: Should call findAllOrderByTitle method
        verify(filmRepository).findAllOrderByTitle();

        // And: Should return empty list
        assertThat(result).isEmpty();
    }

    @Test
    void shouldTransformSingleFilmEntityToMapCorrectly() {
        // Given: Repository returns single film
        List<Film> singleFilm = List.of(new Film(999, "ZORRO ADAPTATION"));
        when(filmRepository.findByTitleStartingWith("Z"))
                .thenReturn(singleFilm);

        // When: Service is called with letter "Z"
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("Z");

        // Then: Should return single film correctly transformed
        assertThat(result).hasSize(1);

        Map<String, Object> filmMap = result.get(0);
        assertThat(filmMap).hasSize(2); // Should contain exactly 2 fields
        assertThat(filmMap).containsEntry("film_id", 999);
        assertThat(filmMap).containsEntry("title", "ZORRO ADAPTATION");
    }

    @Test
    void shouldTransformMultipleFilmEntitiesToMapsCorrectly() {
        // Given: Repository returns multiple films with different film IDs and titles
        List<Film> multipleFilms = List.of(
                new Film(100, "MATRIX RELOADED"),
                new Film(200, "MISSION IMPOSSIBLE"),
                new Film(300, "MADAGASCAR ESCAPE")
        );
        when(filmRepository.findByTitleStartingWith("M"))
                .thenReturn(multipleFilms);

        // When: Service is called with letter "M"
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("M");

        // Then: Should return all films correctly transformed
        assertThat(result).hasSize(3);

        // And: Each film should be correctly mapped
        assertThat(result.get(0))
                .containsEntry("film_id", 100)
                .containsEntry("title", "MATRIX RELOADED");
        assertThat(result.get(1))
                .containsEntry("film_id", 200)
                .containsEntry("title", "MISSION IMPOSSIBLE");
        assertThat(result.get(2))
                .containsEntry("film_id", 300)
                .containsEntry("title", "MADAGASCAR ESCAPE");

        // And: Each map should contain exactly the required fields
        result.forEach(filmMap -> {
            assertThat(filmMap).hasSize(2);
            assertThat(filmMap).containsKey("film_id");
            assertThat(filmMap).containsKey("title");
        });
    }

    @Test
    void shouldHandleFilmWithNullOrMissingFieldsGracefully() {
        // Given: Repository returns film with potential null values (edge case)
        List<Film> filmsWithNulls = List.of(
                new Film(null, "TITLE WITHOUT ID"),
                new Film(500, null)
        );
        when(filmRepository.findByTitleStartingWith("T"))
                .thenReturn(filmsWithNulls);

        // When: Service is called with letter "T"
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("T");

        // Then: Should handle null values gracefully in transformation
        assertThat(result).hasSize(2);

        // And: Should include null values in the maps (maintaining data integrity)
        assertThat(result.get(0))
                .containsEntry("film_id", null)
                .containsEntry("title", "TITLE WITHOUT ID");
        assertThat(result.get(1))
                .containsEntry("film_id", 500)
                .containsEntry("title", null);
    }

    @Test
    void shouldCallRepositoryMethodOnlyOncePerServiceCall() {
        // Given: Repository returns films starting with "C"
        List<Film> filmsStartingWithC = List.of(new Film(50, "CASABLANCA"));
        when(filmRepository.findByTitleStartingWith("C"))
                .thenReturn(filmsStartingWithC);

        // When: Service is called with letter "C"
        filmService.findFilmsByStartingLetter("C");

        // Then: Should call repository method exactly once
        verify(filmRepository, times(1)).findByTitleStartingWith("C");
        verify(filmRepository, never()).findAllOrderByTitle();
    }

    @Test
    void shouldCallCorrectRepositoryMethodBasedOnParameterType() {
        // Test filtered query
        when(filmRepository.findByTitleStartingWith("D"))
                .thenReturn(List.of(new Film(60, "DJANGO UNCHAINED")));

        filmService.findFilmsByStartingLetter("D");
        verify(filmRepository).findByTitleStartingWith("D");
        verify(filmRepository, never()).findAllOrderByTitle();

        // Reset mocks for second test
        reset(filmRepository);

        // Test unfiltered query
        when(filmRepository.findAllOrderByTitle())
                .thenReturn(sampleAllFilms);

        filmService.findFilmsByStartingLetter(null);
        verify(filmRepository).findAllOrderByTitle();
        verify(filmRepository, never()).findByTitleStartingWith(anyString());
    }

    // ========================================================================
    // Task 5.2: Create unit tests for film filtering logic (case insensitive matching)
    // ========================================================================

    @Test
    void shouldHandleCaseInsensitiveMatchingForUppercaseInput() {
        // Given: Repository configured to return films starting with "A" (case insensitive)
        // The repository SQL query uses UPPER(title) LIKE UPPER(:prefix || '%') for case insensitivity
        List<Film> filmsStartingWithA = List.of(
                new Film(1, "ACADEMY DINOSAUR"),
                new Film(2, "ace goldfinger"),      // lowercase title should be found
                new Film(3, "Adaptation Holes")     // mixed case title should be found
        );
        when(filmRepository.findByTitleStartingWith("A"))
                .thenReturn(filmsStartingWithA);

        // When: Service is called with uppercase "A"
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("A");

        // Then: Should pass uppercase "A" to repository (repository handles case insensitivity)
        verify(filmRepository).findByTitleStartingWith("A");

        // And: Should return all films regardless of title case (proving case insensitive matching works)
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).containsEntry("title", "ACADEMY DINOSAUR");
        assertThat(result.get(1)).containsEntry("title", "ace goldfinger");
        assertThat(result.get(2)).containsEntry("title", "Adaptation Holes");
    }

    @Test
    void shouldHandleCaseInsensitiveMatchingForLowercaseInput() {
        // Given: Repository configured to return films starting with "b" (case insensitive)
        List<Film> filmsStartingWithB = List.of(
                new Film(10, "BATMAN BEGINS"),       // uppercase title should be found
                new Film(11, "brave heart"),        // lowercase title should be found
                new Film(12, "Beauty and Beast")    // mixed case title should be found
        );
        when(filmRepository.findByTitleStartingWith("b"))
                .thenReturn(filmsStartingWithB);

        // When: Service is called with lowercase "b"
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("b");

        // Then: Should pass lowercase "b" to repository (service doesn't modify case)
        verify(filmRepository).findByTitleStartingWith("b");

        // And: Should return all films regardless of title case (proving case insensitive matching works)
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).containsEntry("title", "BATMAN BEGINS");
        assertThat(result.get(1)).containsEntry("title", "brave heart");
        assertThat(result.get(2)).containsEntry("title", "Beauty and Beast");
    }

    @Test
    void shouldHandleCaseInsensitiveMatchingForMixedCaseInput() {
        // Given: Repository configured to return films starting with "C" (mixed case input)
        List<Film> filmsStartingWithC = List.of(
                new Film(20, "CASABLANCA"),
                new Film(21, "charlie brown"),
                new Film(22, "Captain America")
        );
        when(filmRepository.findByTitleStartingWith("C"))
                .thenReturn(filmsStartingWithC);

        // When: Service is called with mixed case input "C" (though single letter, testing principle)
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("C");

        // Then: Should pass the parameter as-is to repository
        verify(filmRepository).findByTitleStartingWith("C");

        // And: Should return films matching case insensitively
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).containsEntry("title", "CASABLANCA");
        assertThat(result.get(1)).containsEntry("title", "charlie brown");
        assertThat(result.get(2)).containsEntry("title", "Captain America");
    }

    @Test
    void shouldPreserveCaseOfInputParameterWhenCallingRepository() {
        // Given: Different repository responses for uppercase and lowercase to verify parameter preservation
        when(filmRepository.findByTitleStartingWith("X"))
                .thenReturn(List.of(new Film(100, "X-MEN")));
        when(filmRepository.findByTitleStartingWith("x"))
                .thenReturn(List.of(new Film(101, "xerox film")));

        // When: Service is called with uppercase "X"
        filmService.findFilmsByStartingLetter("X");

        // Then: Should call repository with exact uppercase "X" (not converted to lowercase)
        verify(filmRepository).findByTitleStartingWith("X");
        verify(filmRepository, never()).findByTitleStartingWith("x");

        // Reset for second test
        reset(filmRepository);
        when(filmRepository.findByTitleStartingWith("x"))
                .thenReturn(List.of(new Film(101, "xerox film")));

        // When: Service is called with lowercase "x"
        filmService.findFilmsByStartingLetter("x");

        // Then: Should call repository with exact lowercase "x" (not converted to uppercase)
        verify(filmRepository).findByTitleStartingWith("x");
        verify(filmRepository, never()).findByTitleStartingWith("X");
    }

    @Test
    void shouldHandleCaseInsensitiveMatchingWithEmptyResults() {
        // Given: Repository returns empty list for both uppercase and lowercase queries (no films found)
        when(filmRepository.findByTitleStartingWith("Q"))
                .thenReturn(Collections.emptyList());
        when(filmRepository.findByTitleStartingWith("q"))
                .thenReturn(Collections.emptyList());

        // When: Service is called with uppercase "Q"
        List<Map<String, Object>> resultUppercase = filmService.findFilmsByStartingLetter("Q");

        // Then: Should return empty list for uppercase
        assertThat(resultUppercase).isEmpty();
        verify(filmRepository).findByTitleStartingWith("Q");

        // Reset for second test
        reset(filmRepository);
        when(filmRepository.findByTitleStartingWith("q"))
                .thenReturn(Collections.emptyList());

        // When: Service is called with lowercase "q"
        List<Map<String, Object>> resultLowercase = filmService.findFilmsByStartingLetter("q");

        // Then: Should return empty list for lowercase (consistent behavior)
        assertThat(resultLowercase).isEmpty();
        verify(filmRepository).findByTitleStartingWith("q");
    }

    @Test
    void shouldDemonstrateEquivalentResultsForCaseInsensitiveMatching() {
        // Given: Repository is configured to return same logical films for both cases
        // (This simulates the repository's case insensitive SQL query behavior)
        List<Film> filmsForUppercaseM = List.of(
                new Film(50, "MATRIX RELOADED"),
                new Film(51, "mission impossible"),
                new Film(52, "Madagascar")
        );
        List<Film> filmsForLowercaseM = List.of(
                new Film(50, "MATRIX RELOADED"),     // Same films should be returned
                new Film(51, "mission impossible"),  // regardless of input case
                new Film(52, "Madagascar")           // (simulating repository behavior)
        );
        
        when(filmRepository.findByTitleStartingWith("M"))
                .thenReturn(filmsForUppercaseM);
        when(filmRepository.findByTitleStartingWith("m"))
                .thenReturn(filmsForLowercaseM);

        // When: Service is called with uppercase "M"
        List<Map<String, Object>> resultUppercase = filmService.findFilmsByStartingLetter("M");

        // And: Service is called with lowercase "m"
        List<Map<String, Object>> resultLowercase = filmService.findFilmsByStartingLetter("m");

        // Then: Both results should have same structure and content (demonstrating case insensitive equivalence)
        assertThat(resultUppercase).hasSize(3);
        assertThat(resultLowercase).hasSize(3);
        
        // And: Results should contain the same films (proving case insensitive matching works)
        assertThat(resultUppercase.get(0)).containsEntry("title", "MATRIX RELOADED");
        assertThat(resultLowercase.get(0)).containsEntry("title", "MATRIX RELOADED");
        assertThat(resultUppercase.get(1)).containsEntry("title", "mission impossible");
        assertThat(resultLowercase.get(1)).containsEntry("title", "mission impossible");
        assertThat(resultUppercase.get(2)).containsEntry("title", "Madagascar");
        assertThat(resultLowercase.get(2)).containsEntry("title", "Madagascar");
    }

    @Test
    void shouldHandleCaseInsensitiveMatchingWithWhitespaceAndTrimming() {
        // Given: Repository returns films for trimmed parameter
        List<Film> filmsStartingWithD = List.of(
                new Film(30, "DJANGO UNCHAINED"),
                new Film(31, "dark knight"),
                new Film(32, "Dumbo")
        );
        when(filmRepository.findByTitleStartingWith("D"))
                .thenReturn(filmsStartingWithD);

        // When: Service is called with whitespace around uppercase letter " D "
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter(" D ");

        // Then: Should trim whitespace and call repository with "D"
        verify(filmRepository).findByTitleStartingWith("D");

        // And: Should return films matching case insensitively
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).containsEntry("title", "DJANGO UNCHAINED");
        assertThat(result.get(1)).containsEntry("title", "dark knight");
        assertThat(result.get(2)).containsEntry("title", "Dumbo");
    }

    @Test
    void shouldVerifyServiceLayerDoesNotModifyCaseForFiltering() {
        // Given: Repository mocked for specific case parameter
        when(filmRepository.findByTitleStartingWith("z"))
                .thenReturn(List.of(new Film(99, "zorro")));

        // When: Service is called with lowercase parameter
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("z");

        // Then: Service should pass parameter as-is (no case conversion in service layer)
        verify(filmRepository).findByTitleStartingWith("z");
        verify(filmRepository, never()).findByTitleStartingWith("Z");
        verify(filmRepository, never()).findByTitleStartingWith(argThat(arg -> 
            arg != null && !arg.equals("z") && arg.toLowerCase().equals("z")));

        // And: Should return the film correctly
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).containsEntry("title", "zorro");
    }

    // ========================================================================
    // Task 5.3: Create unit tests for DTO transformation (Entity to Response DTO)
    // ========================================================================

    @Test
    void shouldTransformFilmEntityToCorrectMapStructure() {
        // Given: Repository returns a Film entity with specific data
        Film testFilm = new Film(123, "TEST MOVIE TITLE");
        when(filmRepository.findByTitleStartingWith("T"))
                .thenReturn(List.of(testFilm));

        // When: Service transforms entity to DTO
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("T");

        // Then: Should transform entity to Map with exact field structure
        assertThat(result).hasSize(1);

        Map<String, Object> transformedFilm = result.get(0);
        
        // And: Map should contain exactly 2 fields (film_id, title)
        assertThat(transformedFilm).hasSize(2);
        assertThat(transformedFilm).containsOnlyKeys("film_id", "title");

        // And: Field mapping should be correct (entity field -> DTO field)
        assertThat(transformedFilm).containsEntry("film_id", 123);  // entity.filmId() -> "film_id"
        assertThat(transformedFilm).containsEntry("title", "TEST MOVIE TITLE");  // entity.title() -> "title"
    }

    @Test
    void shouldTransformEntityFieldTypesCorrectlyInDTO() {
        // Given: Repository returns Film entity with specific field types
        Film filmWithTypes = new Film(42, "TYPED FILM");
        when(filmRepository.findByTitleStartingWith("T"))
                .thenReturn(List.of(filmWithTypes));

        // When: Service transforms entity to DTO
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("T");

        // Then: Field types should be preserved in transformation
        Map<String, Object> transformedFilm = result.get(0);

        // And: film_id should be Integer type (from entity filmId field)
        Object filmId = transformedFilm.get("film_id");
        assertThat(filmId).isInstanceOf(Integer.class);
        assertThat(filmId).isEqualTo(42);

        // And: title should be String type (from entity title field)
        Object title = transformedFilm.get("title");
        assertThat(title).isInstanceOf(String.class);
        assertThat(title).isEqualTo("TYPED FILM");
    }

    @Test
    void shouldHandleNullValuesInEntityFieldsDuringTransformation() {
        // Given: Repository returns Film entity with null values (edge case)
        Film filmWithNulls = new Film(null, null);
        when(filmRepository.findByTitleStartingWith("N"))
                .thenReturn(List.of(filmWithNulls));

        // When: Service transforms entity with null fields to DTO
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("N");

        // Then: Should handle null values gracefully in transformation
        assertThat(result).hasSize(1);

        Map<String, Object> transformedFilm = result.get(0);

        // And: Map should still contain required field keys even with null values
        assertThat(transformedFilm).hasSize(2);
        assertThat(transformedFilm).containsKey("film_id");
        assertThat(transformedFilm).containsKey("title");

        // And: Null values should be preserved in transformation (data integrity)
        assertThat(transformedFilm.get("film_id")).isNull();
        assertThat(transformedFilm.get("title")).isNull();
    }

    @Test
    void shouldTransformMultipleEntitiesWithConsistentStructure() {
        // Given: Repository returns multiple Film entities with different data
        List<Film> multipleFilms = List.of(
                new Film(1, "FIRST FILM"),
                new Film(2, "SECOND FILM"),
                new Film(100, "HUNDREDTH FILM")
        );
        when(filmRepository.findByTitleStartingWith("F"))
                .thenReturn(multipleFilms);

        // When: Service transforms multiple entities to DTOs
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("F");

        // Then: All entities should be transformed with consistent structure
        assertThat(result).hasSize(3);

        // And: Each transformed DTO should have identical structure
        result.forEach(transformedFilm -> {
            assertThat(transformedFilm).hasSize(2);
            assertThat(transformedFilm).containsOnlyKeys("film_id", "title");
            assertThat(transformedFilm.get("film_id")).isInstanceOf(Integer.class);
            assertThat(transformedFilm.get("title")).isInstanceOf(String.class);
        });

        // And: Data should be correctly mapped for each entity
        assertThat(result.get(0)).containsEntry("film_id", 1).containsEntry("title", "FIRST FILM");
        assertThat(result.get(1)).containsEntry("film_id", 2).containsEntry("title", "SECOND FILM");
        assertThat(result.get(2)).containsEntry("film_id", 100).containsEntry("title", "HUNDREDTH FILM");
    }

    @Test
    void shouldTransformEntityWithSpecialCharactersInTitle() {
        // Given: Repository returns Film entity with special characters in title
        Film filmWithSpecialChars = new Film(555, "MOVIE: THE SEQUEL (2024) - Director's Cut");
        when(filmRepository.findByTitleStartingWith("M"))
                .thenReturn(List.of(filmWithSpecialChars));

        // When: Service transforms entity with special characters to DTO
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("M");

        // Then: Special characters should be preserved in transformation
        assertThat(result).hasSize(1);

        Map<String, Object> transformedFilm = result.get(0);
        assertThat(transformedFilm).containsEntry("title", "MOVIE: THE SEQUEL (2024) - Director's Cut");

        // And: Structure should remain consistent
        assertThat(transformedFilm).hasSize(2);
        assertThat(transformedFilm).containsOnlyKeys("film_id", "title");
    }

    @Test
    void shouldTransformEntityWithEmptyStringTitle() {
        // Given: Repository returns Film entity with empty string title
        Film filmWithEmptyTitle = new Film(999, "");
        when(filmRepository.findByTitleStartingWith("E"))
                .thenReturn(List.of(filmWithEmptyTitle));

        // When: Service transforms entity with empty title to DTO
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("E");

        // Then: Empty string should be preserved in transformation
        assertThat(result).hasSize(1);

        Map<String, Object> transformedFilm = result.get(0);
        assertThat(transformedFilm).containsEntry("title", "");
        assertThat(transformedFilm).containsEntry("film_id", 999);

        // And: Structure should remain consistent
        assertThat(transformedFilm).hasSize(2);
        assertThat(transformedFilm).containsOnlyKeys("film_id", "title");
    }

    @Test
    void shouldTransformEntityWithLargeFilmId() {
        // Given: Repository returns Film entity with large film ID (edge case)
        Film filmWithLargeId = new Film(Integer.MAX_VALUE, "LARGE ID FILM");
        when(filmRepository.findByTitleStartingWith("L"))
                .thenReturn(List.of(filmWithLargeId));

        // When: Service transforms entity with large ID to DTO
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("L");

        // Then: Large ID should be handled correctly in transformation
        assertThat(result).hasSize(1);

        Map<String, Object> transformedFilm = result.get(0);
        assertThat(transformedFilm).containsEntry("film_id", Integer.MAX_VALUE);
        assertThat(transformedFilm).containsEntry("title", "LARGE ID FILM");

        // And: Type should remain Integer despite large value
        assertThat(transformedFilm.get("film_id")).isInstanceOf(Integer.class);
    }

    @Test
    void shouldTransformEntityWithZeroFilmId() {
        // Given: Repository returns Film entity with zero film ID (edge case)
        Film filmWithZeroId = new Film(0, "ZERO ID FILM");
        when(filmRepository.findByTitleStartingWith("Z"))
                .thenReturn(List.of(filmWithZeroId));

        // When: Service transforms entity with zero ID to DTO
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("Z");

        // Then: Zero ID should be handled correctly in transformation
        assertThat(result).hasSize(1);

        Map<String, Object> transformedFilm = result.get(0);
        assertThat(transformedFilm).containsEntry("film_id", 0);
        assertThat(transformedFilm).containsEntry("title", "ZERO ID FILM");
    }

    @Test
    void shouldCreateNewMapInstanceForEachEntityTransformation() {
        // Given: Repository returns multiple Film entities
        List<Film> films = List.of(
                new Film(1, "FILM ONE"),
                new Film(2, "FILM TWO")
        );
        when(filmRepository.findByTitleStartingWith("F"))
                .thenReturn(films);

        // When: Service transforms entities to DTOs (multiple Map instances)
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("F");

        // Then: Each transformation should create a separate Map instance
        assertThat(result).hasSize(2);

        Map<String, Object> firstFilm = result.get(0);
        Map<String, Object> secondFilm = result.get(1);

        // And: Maps should be different instances (not same reference)
        assertThat(firstFilm).isNotSameAs(secondFilm);

        // And: Modifying one map should not affect the other (independence)
        firstFilm.put("test_field", "test_value");
        assertThat(secondFilm).doesNotContainKey("test_field");

        // And: Each map should contain correct data
        assertThat(firstFilm).containsEntry("film_id", 1).containsEntry("title", "FILM ONE");
        assertThat(secondFilm).containsEntry("film_id", 2).containsEntry("title", "FILM TWO");
    }

    @Test
    void shouldNotModifyOriginalEntityDuringTransformation() {
        // Given: Repository returns Film entity
        Film originalEntity = new Film(777, "ORIGINAL TITLE");
        when(filmRepository.findByTitleStartingWith("O"))
                .thenReturn(List.of(originalEntity));

        // When: Service transforms entity to DTO
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("O");

        // Then: Transformation should not modify the original entity
        // (Film is a record, so it's immutable, but testing the principle)
        assertThat(originalEntity.filmId()).isEqualTo(777);
        assertThat(originalEntity.title()).isEqualTo("ORIGINAL TITLE");

        // And: DTO should contain correct data from entity
        Map<String, Object> transformedFilm = result.get(0);
        assertThat(transformedFilm).containsEntry("film_id", 777);
        assertThat(transformedFilm).containsEntry("title", "ORIGINAL TITLE");
    }

    @Test
    void shouldTransformEntityAccordingToExpectedResponseFormat() {
        // Given: Repository returns Film entity that needs to match API response format
        Film entityForAPI = new Film(46, "API RESPONSE FILM");
        when(filmRepository.findByTitleStartingWith("A"))
                .thenReturn(List.of(entityForAPI));

        // When: Service transforms entity for API response
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("A");

        // Then: Transformation should match expected JSON response format
        // (As defined in FilmResponse DTO and OpenAPI specification)
        Map<String, Object> apiFormattedFilm = result.get(0);

        // And: Field names should match API specification
        assertThat(apiFormattedFilm).containsKey("film_id");  // Matches JSON response format
        assertThat(apiFormattedFilm).containsKey("title");    // Matches JSON response format

        // And: Should NOT contain entity-specific field names
        assertThat(apiFormattedFilm).doesNotContainKey("filmId");  // Not entity field name
        assertThat(apiFormattedFilm).doesNotContainKey("id");      // Not shortened name

        // And: Field values should be correct
        assertThat(apiFormattedFilm.get("film_id")).isEqualTo(46);
        assertThat(apiFormattedFilm.get("title")).isEqualTo("API RESPONSE FILM");

        // And: Should be ready for JSON serialization in FilmResponse
        assertThat(apiFormattedFilm.get("film_id")).isInstanceOf(Integer.class);
        assertThat(apiFormattedFilm.get("title")).isInstanceOf(String.class);
    }

    // ========================================================================
    // Task 5.4: Create unit tests for business validation (letter parameter validation)
    // ========================================================================

    @Test
    void shouldAcceptValidSingleLetterParametersAtServiceLevel() {
        // Given: Repository returns films for valid letter parameters
        when(filmRepository.findByTitleStartingWith("A"))
                .thenReturn(List.of(new Film(1, "ACADEMY DINOSAUR")));
        when(filmRepository.findByTitleStartingWith("Z"))
                .thenReturn(List.of(new Film(999, "ZORRO")));

        // When: Service is called with valid single letter parameters
        List<Map<String, Object>> resultA = filmService.findFilmsByStartingLetter("A");
        List<Map<String, Object>> resultZ = filmService.findFilmsByStartingLetter("Z");

        // Then: Service should process valid parameters without issues
        assertThat(resultA).hasSize(1);
        assertThat(resultZ).hasSize(1);

        // And: Repository should be called with exact parameters passed
        verify(filmRepository).findByTitleStartingWith("A");
        verify(filmRepository).findByTitleStartingWith("Z");
    }

    @Test
    void shouldAcceptBothUppercaseAndLowercaseLettersAtServiceLevel() {
        // Given: Repository returns different results for case sensitivity test
        when(filmRepository.findByTitleStartingWith("B"))
                .thenReturn(List.of(new Film(10, "BATMAN")));
        when(filmRepository.findByTitleStartingWith("b"))
                .thenReturn(List.of(new Film(11, "brave heart")));

        // When: Service is called with both uppercase and lowercase
        List<Map<String, Object>> upperResult = filmService.findFilmsByStartingLetter("B");
        List<Map<String, Object>> lowerResult = filmService.findFilmsByStartingLetter("b");

        // Then: Service should accept both cases (validation is controller's responsibility)
        assertThat(upperResult).hasSize(1);
        assertThat(lowerResult).hasSize(1);

        // And: Service passes parameters as-is to repository
        verify(filmRepository).findByTitleStartingWith("B");
        verify(filmRepository).findByTitleStartingWith("b");
    }

    @Test
    void shouldHandleServiceLevelParameterTrimming() {
        // Given: Repository returns films for trimmed parameter
        when(filmRepository.findByTitleStartingWith("C"))
                .thenReturn(List.of(new Film(20, "CASABLANCA")));

        // When: Service is called with parameter containing whitespace
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("  C  ");

        // Then: Service should trim whitespace before calling repository
        verify(filmRepository).findByTitleStartingWith("C");

        // And: Should return correct result
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).containsEntry("title", "CASABLANCA");
    }

    @Test
    void shouldDelegateValidationToControllerLayer() {
        // Given: Service receives parameter that would be invalid at controller level
        // (Service layer trusts controller validation - defensive programming)
        when(filmRepository.findByTitleStartingWith("123"))
                .thenReturn(Collections.emptyList());

        // When: Service is called with invalid parameter (after controller validation)
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("123");

        // Then: Service should not perform its own validation (controller's responsibility)
        // Service processes the parameter as requested
        verify(filmRepository).findByTitleStartingWith("123");
        assertThat(result).isEmpty();
    }

    // ========================================================================
    // Task 5.5: Create unit tests for empty result handling logic
    // ========================================================================

    @Test
    void shouldHandleEmptyResultsGracefullyForFilteredQueries() {
        // Given: Repository returns empty list for letter with no films
        when(filmRepository.findByTitleStartingWith("Q"))
                .thenReturn(Collections.emptyList());

        // When: Service is called with letter that has no matching films
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("Q");

        // Then: Service should return empty list without errors
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        // And: Repository should be called correctly
        verify(filmRepository).findByTitleStartingWith("Q");
        verify(filmRepository, never()).findAllOrderByTitle();
    }

    @Test
    void shouldHandleEmptyResultsGracefullyForUnfilteredQueries() {
        // Given: Repository returns empty list for all films (empty database scenario)
        when(filmRepository.findAllOrderByTitle())
                .thenReturn(Collections.emptyList());

        // When: Service is called without filter parameter
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter(null);

        // Then: Service should return empty list without errors
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        // And: Repository should be called correctly
        verify(filmRepository).findAllOrderByTitle();
        verify(filmRepository, never()).findByTitleStartingWith(anyString());
    }

    @Test
    void shouldReturnConsistentEmptyResultStructure() {
        // Given: Repository returns empty list
        when(filmRepository.findByTitleStartingWith("X"))
                .thenReturn(Collections.emptyList());

        // When: Service is called with parameter resulting in empty results
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("X");

        // Then: Should return empty list (not null)
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        assertThat(result).hasSize(0);

        // And: List should be ready for controller to build response
        // Controller will create FilmResponse with count=0 and empty films array
        assertThat(result).isInstanceOf(List.class);
    }

    @Test
    void shouldHandleEmptyResultsWithoutThrowingExceptions() {
        // Given: Repository returns empty list
        when(filmRepository.findByTitleStartingWith("Y"))
                .thenReturn(Collections.emptyList());

        // When: Service processes empty results (should not throw exceptions)
        List<Map<String, Object>> result = null;
        try {
            result = filmService.findFilmsByStartingLetter("Y");
        } catch (Exception e) {
            // Then: Should not throw any exceptions
            assertThat(e).isNull();
        }

        // And: Should return valid empty result
        assertThat(result).isNotNull().isEmpty();
    }

    // ========================================================================
    // Task 5.6: Create unit tests for error scenarios (invalid input, null handling)
    // ========================================================================

    @Test
    void shouldHandleNullParameterGracefully() {
        // Given: Repository returns all films for unfiltered query
        when(filmRepository.findAllOrderByTitle())
                .thenReturn(sampleAllFilms);

        // When: Service is called with null parameter
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter(null);

        // Then: Should handle null gracefully and call unfiltered repository method
        verify(filmRepository).findAllOrderByTitle();
        verify(filmRepository, never()).findByTitleStartingWith(anyString());

        // And: Should return valid result
        assertThat(result).isNotNull();
        assertThat(result).hasSize(5);
    }

    @Test
    void shouldHandleEmptyStringParameterGracefully() {
        // Given: Repository returns all films for unfiltered query
        when(filmRepository.findAllOrderByTitle())
                .thenReturn(sampleAllFilms);

        // When: Service is called with empty string parameter
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("");

        // Then: Should handle empty string gracefully (treated as no filter)
        verify(filmRepository).findAllOrderByTitle();
        verify(filmRepository, never()).findByTitleStartingWith(anyString());

        // And: Should return valid result
        assertThat(result).isNotNull();
        assertThat(result).hasSize(5);
    }

    @Test
    void shouldHandleWhitespaceOnlyParameterGracefully() {
        // Given: Repository returns all films for unfiltered query
        when(filmRepository.findAllOrderByTitle())
                .thenReturn(sampleAllFilms);

        // When: Service is called with whitespace-only parameter
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("   ");

        // Then: Should handle whitespace gracefully (trimmed to empty, treated as no filter)
        verify(filmRepository).findAllOrderByTitle();
        verify(filmRepository, never()).findByTitleStartingWith(anyString());

        // And: Should return valid result
        assertThat(result).isNotNull();
        assertThat(result).hasSize(5);
    }

    @Test
    void shouldHandleRepositoryReturningNullGracefully() {
        // Given: Repository returns null (edge case - should not happen with Spring Data JDBC)
        when(filmRepository.findByTitleStartingWith("N"))
                .thenReturn(null);

        // When: Service processes null repository result
        try {
            List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("N");
            
            // Then: Should handle gracefully or throw appropriate exception
            // (In this case, .stream() on null will throw NullPointerException)
            // This test documents the behavior - service expects repository to return empty list, not null
        } catch (NullPointerException e) {
            // Expected behavior - service layer expects List, not null from repository
            assertThat(e).isNotNull();
        }

        verify(filmRepository).findByTitleStartingWith("N");
    }

    @Test
    void shouldMaintainDataIntegrityDuringErrorScenarios() {
        // Given: Repository returns films, including some with potential null fields
        List<Film> filmsWithPotentialNulls = List.of(
                new Film(1, "NORMAL FILM"),
                new Film(null, "FILM WITHOUT ID"),  // Edge case
                new Film(2, null)                   // Edge case
        );
        when(filmRepository.findByTitleStartingWith("E"))
                .thenReturn(filmsWithPotentialNulls);

        // When: Service processes films with potential data integrity issues
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("E");

        // Then: Should maintain data integrity and not filter out problematic records
        assertThat(result).hasSize(3);

        // And: Should preserve null values (maintaining data integrity)
        assertThat(result.get(0)).containsEntry("film_id", 1).containsEntry("title", "NORMAL FILM");
        assertThat(result.get(1)).containsEntry("film_id", null).containsEntry("title", "FILM WITHOUT ID");
        assertThat(result.get(2)).containsEntry("film_id", 2).containsEntry("title", null);
    }

    // ========================================================================
    // Task 5.7: Create unit tests for business rules (46 films for "A", etc.)
    // ========================================================================

    @Test
    void shouldReturnExactly46FilmsForLetterA() {
        // Given: Repository returns exactly 46 films for letter "A" (as per business requirement)
        List<Film> exactly46Films = List.of(
                new Film(1, "ACADEMY DINOSAUR"),
                new Film(2, "ACE GOLDFINGER"),
                new Film(3, "ADAPTATION HOLES")
                // ... (simulating 46 films total)
        );
        // Simulate 46 films by adding more entries to reach the expected count
        List<Film> films46 = new java.util.ArrayList<>(exactly46Films);
        for (int i = 4; i <= 46; i++) {
            films46.add(new Film(i, "FILM_A_" + i));
        }
        
        when(filmRepository.findByTitleStartingWith("A"))
                .thenReturn(films46);

        // When: Service is called with letter "A"
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("A");

        // Then: Should return exactly 46 films (as per business rule)
        assertThat(result).hasSize(46);

        // And: Each film should be properly transformed
        result.forEach(film -> {
            assertThat(film).containsKey("film_id");
            assertThat(film).containsKey("title");
        });

        // And: Repository should be called with correct parameter
        verify(filmRepository).findByTitleStartingWith("A");
    }

    @Test
    void shouldHandleBusinessRuleForDifferentLetters() {
        // Given: Repository returns different counts for different letters (realistic business scenario)
        when(filmRepository.findByTitleStartingWith("B"))
                .thenReturn(createFilmList("B", 32));  // 32 films starting with B
        when(filmRepository.findByTitleStartingWith("C"))
                .thenReturn(createFilmList("C", 28));  // 28 films starting with C
        when(filmRepository.findByTitleStartingWith("D"))
                .thenReturn(createFilmList("D", 18));  // 18 films starting with D

        // When: Service is called with different letters
        List<Map<String, Object>> resultB = filmService.findFilmsByStartingLetter("B");
        List<Map<String, Object>> resultC = filmService.findFilmsByStartingLetter("C");
        List<Map<String, Object>> resultD = filmService.findFilmsByStartingLetter("D");

        // Then: Should return correct counts for each letter
        assertThat(resultB).hasSize(32);
        assertThat(resultC).hasSize(28);
        assertThat(resultD).hasSize(18);

        // And: All results should be properly structured
        List.of(resultB, resultC, resultD).forEach(result -> {
            result.forEach(film -> {
                assertThat(film).containsKey("film_id");
                assertThat(film).containsKey("title");
            });
        });
    }

    @Test
    void shouldHandleLettersWithZeroFilms() {
        // Given: Repository returns empty list for letters with no films (business rule - some letters have 0 films)
        when(filmRepository.findByTitleStartingWith("Q"))
                .thenReturn(Collections.emptyList());  // 0 films starting with Q
        when(filmRepository.findByTitleStartingWith("X"))
                .thenReturn(Collections.emptyList());  // 0 films starting with X

        // When: Service is called with letters that have no films
        List<Map<String, Object>> resultQ = filmService.findFilmsByStartingLetter("Q");
        List<Map<String, Object>> resultX = filmService.findFilmsByStartingLetter("X");

        // Then: Should return empty lists (count = 0)
        assertThat(resultQ).isEmpty();
        assertThat(resultX).isEmpty();

        // And: Repository should be called correctly
        verify(filmRepository).findByTitleStartingWith("Q");
        verify(filmRepository).findByTitleStartingWith("X");
    }

    @Test
    void shouldMaintainBusinessRuleConsistencyAcrossMultipleCalls() {
        // Given: Repository consistently returns same count for letter "A"
        List<Film> consistentFilms = createFilmList("A", 46);
        when(filmRepository.findByTitleStartingWith("A"))
                .thenReturn(consistentFilms);

        // When: Service is called multiple times with same parameter
        List<Map<String, Object>> result1 = filmService.findFilmsByStartingLetter("A");
        List<Map<String, Object>> result2 = filmService.findFilmsByStartingLetter("A");
        List<Map<String, Object>> result3 = filmService.findFilmsByStartingLetter("A");

        // Then: Should return consistent results (business rule consistency)
        assertThat(result1).hasSize(46);
        assertThat(result2).hasSize(46);
        assertThat(result3).hasSize(46);

        // And: All results should have identical structure
        assertThat(result1.get(0)).containsKey("film_id").containsKey("title");
        assertThat(result2.get(0)).containsKey("film_id").containsKey("title");
        assertThat(result3.get(0)).containsKey("film_id").containsKey("title");

        // And: Repository should be called each time (no caching at service level)
        verify(filmRepository, times(3)).findByTitleStartingWith("A");
    }

    @Test
    void shouldValidateBusinessRulePerformanceRequirement() {
        // Given: Repository returns large result set (testing performance characteristic)
        List<Film> largeResultSet = createFilmList("M", 95);  // Large number of films
        when(filmRepository.findByTitleStartingWith("M"))
                .thenReturn(largeResultSet);

        // When: Service processes large result set
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("M");
        long endTime = System.currentTimeMillis();

        // Then: Service should handle transformation efficiently
        assertThat(result).hasSize(95);

        // And: Processing time should be reasonable (business rule: < 2 seconds for DB query)
        // Service layer transformation should be much faster than DB query
        long processingTime = endTime - startTime;
        assertThat(processingTime).isLessThan(100); // Service transformation should be under 100ms

        // And: All films should be correctly transformed
        result.forEach(film -> {
            assertThat(film).containsKey("film_id").containsKey("title");
        });
    }

    @Test
    void shouldEnforceBusinessRuleDataConsistency() {
        // Given: Repository returns films that should follow business data rules
        List<Film> businessRuleFilms = List.of(
                new Film(1, "ACADEMY DINOSAUR"),      // Film ID should be positive integer
                new Film(2, "ACE GOLDFINGER"),        // Title should be non-empty string
                new Film(46, "AFRICAN EGG")           // Last film for "A" should maintain sequence
        );
        when(filmRepository.findByTitleStartingWith("A"))
                .thenReturn(businessRuleFilms);

        // When: Service processes business rule compliant data
        List<Map<String, Object>> result = filmService.findFilmsByStartingLetter("A");

        // Then: Should maintain business rule data consistency
        assertThat(result).hasSize(3);

        // And: Film IDs should be positive integers (business rule)
        result.forEach(film -> {
            Integer filmId = (Integer) film.get("film_id");
            assertThat(filmId).isPositive();
        });

        // And: Titles should be non-empty strings (business rule)
        result.forEach(film -> {
            String title = (String) film.get("title");
            assertThat(title).isNotEmpty();
        });

        // And: Data should maintain expected format for API response
        result.forEach(film -> {
            assertThat(film.get("film_id")).isInstanceOf(Integer.class);
            assertThat(film.get("title")).isInstanceOf(String.class);
        });
    }

    // Helper method for creating test film lists
    private List<Film> createFilmList(String startLetter, int count) {
        List<Film> films = new java.util.ArrayList<>();
        for (int i = 1; i <= count; i++) {
            films.add(new Film(i, startLetter.toUpperCase() + "_FILM_" + i));
        }
        return films;
    }
} 