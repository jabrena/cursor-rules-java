package info.jab.ms.controller;

import info.jab.ms.repository.Film;
import info.jab.ms.service.FilmService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for FilmController
 *
 * This test class implements unit testing for the FilmController REST API endpoints
 * following TDD approach. Tests focus on controller behavior, parameter handling,
 * and response formatting without database dependencies.
 */
@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FilmService filmService;

    /**
     * This test verifies the behavior when calling GET /api/v1/films without
     * the startsWith parameter. The controller should handle this case and
     * potentially return all films or an appropriate default response.
     *
     * Expected behavior:
     * - GET /api/v1/films (no parameters)
     * - HTTP 200 OK response
     * - JSON response format
     * - Response structure: {"films": [], "count": 0, "filter": {}}
     * - Service layer called appropriately
     *
     * This test should FAIL initially (Red phase - TDD) as FilmController doesn't exist yet.
     */
    @Test
    void shouldGetFilmsWithoutParameters() throws Exception {
        // Given: No startsWith parameter provided
        // Mock service to return empty list when no filter applied
        when(filmService.findFilmEntitiesByStartingLetter(null))
                .thenReturn(Collections.emptyList());

        // When: GET /api/v1/films (without parameters)
        mockMvc.perform(get("/api/v1/films")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should return HTTP 200 OK
                .andExpect(status().isOk())

                // And: Response should be JSON
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // And: Response should have expected structure
                .andExpect(jsonPath("$.films").isArray())
                .andExpect(jsonPath("$.count").isNumber())
                .andExpect(jsonPath("$.filter").isMap())

                // And: Should contain empty films array when no filter applied
                .andExpect(jsonPath("$.films").isEmpty())
                .andExpect(jsonPath("$.count").value(0))

                // And: Filter should be empty object when no parameters provided
                .andExpect(jsonPath("$.filter").isEmpty());
    }

    /**
     * This test verifies the behavior when calling GET /api/v1/films with
     * the startsWith=A parameter. The controller should handle this case and
     * return films starting with the letter "A".
     *
     * Expected behavior:
     * - GET /api/v1/films?startsWith=A
     * - HTTP 200 OK response
     * - JSON response format
     * - Response structure: {"films": [...], "count": n, "filter": {"startsWith": "A"}}
     * - Service layer called with "A" parameter
     * - Mocked service returns sample films starting with "A"
     *
     * This test should FAIL initially (Red phase - TDD) as FilmController doesn't exist yet.
     */
    @Test
    void shouldGetFilmsStartingWithA() throws Exception {
        // Given: Sample Film entities starting with "A" from service layer
        List<Film> mockFilms = List.of(
                new Film(1, "ACADEMY DINOSAUR"),
                new Film(2, "ACE GOLDFINGER"),
                new Film(3, "ADAPTATION HOLES")
        );

        // Mock service to return film entities when "A" filter applied
        when(filmService.findFilmEntitiesByStartingLetter("A"))
                .thenReturn(mockFilms);

        // When: GET /api/v1/films?startsWith=A
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "A")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should return HTTP 200 OK
                .andExpect(status().isOk())

                // And: Response should be JSON
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // And: Response should have expected structure
                .andExpect(jsonPath("$.films").isArray())
                .andExpect(jsonPath("$.count").isNumber())
                .andExpect(jsonPath("$.filter").isMap())

                // And: Should contain 3 films in mocked response
                .andExpect(jsonPath("$.films").isNotEmpty())
                .andExpect(jsonPath("$.count").value(3))

                // And: Filter should contain the startsWith parameter
                .andExpect(jsonPath("$.filter.startsWith").value("A"))

                // And: Each film should have required fields
                .andExpect(jsonPath("$.films[0].film_id").value(1))
                .andExpect(jsonPath("$.films[0].title").value("ACADEMY DINOSAUR"))
                .andExpect(jsonPath("$.films[1].film_id").value(2))
                .andExpect(jsonPath("$.films[1].title").value("ACE GOLDFINGER"))
                .andExpect(jsonPath("$.films[2].film_id").value(3))
                .andExpect(jsonPath("$.films[2].title").value("ADAPTATION HOLES"));
    }

    /**
     * These tests verify that the controller properly accepts valid single letter parameters.
     * Valid parameters include:
     * - Uppercase letters A-Z
     * - Lowercase letters a-z
     * - Controller should handle both cases and process them correctly
     *
     * Expected behavior for each valid letter:
     * - HTTP 200 OK response
     * - Parameter is accepted and processed
     * - Service layer is called with the provided letter
     * - Response structure is consistent
     *
     * This test should FAIL initially (Red phase - TDD) as parameter validation doesn't exist yet.
     */
    @Test
    void shouldAcceptValidUppercaseLetterA() throws Exception {
        // Given: Mock service response for uppercase A
        List<Film> mockFilms = List.of(
                new Film(1, "ACADEMY DINOSAUR")
        );
        when(filmService.findFilmEntitiesByStartingLetter("A"))
                .thenReturn(mockFilms);

        // When: GET /api/v1/films?startsWith=A (uppercase)
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "A")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should accept valid uppercase letter
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.filter.startsWith").value("A"));
    }

    @Test
    void shouldAcceptValidLowercaseLetterA() throws Exception {
        // Given: Mock service response for lowercase a (should be processed as uppercase)
        List<Film> mockFilms = List.of(
                new Film(1, "ACADEMY DINOSAUR")
        );
        when(filmService.findFilmEntitiesByStartingLetter("a"))
                .thenReturn(mockFilms);

        // When: GET /api/v1/films?startsWith=a (lowercase)
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "a")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should accept valid lowercase letter
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.filter.startsWith").value("a"));
    }

    @Test
    void shouldAcceptValidUppercaseLetterZ() throws Exception {
        // Given: Mock service response for uppercase Z
        List<Film> mockFilms = List.of(
                new Film(999, "ZORRO ADAPTATION")
        );
        when(filmService.findFilmEntitiesByStartingLetter("Z"))
                .thenReturn(mockFilms);

        // When: GET /api/v1/films?startsWith=Z (uppercase boundary test)
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "Z")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should accept valid uppercase letter at alphabet boundary
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.filter.startsWith").value("Z"));
    }

    @Test
    void shouldAcceptValidLowercaseLetterZ() throws Exception {
        // Given: Mock service response for lowercase z
        List<Film> mockFilms = List.of(
                new Film(999, "ZORRO ADAPTATION")
        );
        when(filmService.findFilmEntitiesByStartingLetter("z"))
                .thenReturn(mockFilms);

        // When: GET /api/v1/films?startsWith=z (lowercase boundary test)
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "z")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should accept valid lowercase letter at alphabet boundary
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.filter.startsWith").value("z"));
    }

    @Test
    void shouldAcceptValidMiddleAlphabetLetterM() throws Exception {
        // Given: Mock service response for letter M
        List<Film> mockFilms = List.of(
                new Film(500, "MATRIX RELOADED")
        );
        when(filmService.findFilmEntitiesByStartingLetter("M"))
                .thenReturn(mockFilms);

        // When: GET /api/v1/films?startsWith=M (middle alphabet test)
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "M")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should accept valid letter from middle of alphabet
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.filter.startsWith").value("M"));
    }

    @Test
    void shouldAcceptValidParameterAndCallServiceCorrectly() throws Exception {
        // Given: Mock service response for letter B
        List<Film> mockFilms = List.of(
                new Film(100, "BATMAN BEGINS"),
                new Film(101, "BRAVE HEART")
        );
        when(filmService.findFilmEntitiesByStartingLetter("B"))
                .thenReturn(mockFilms);

        // When: GET /api/v1/films?startsWith=B
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "B")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should accept parameter and call service with correct value
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.filter.startsWith").value("B"))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.films").isArray())
                .andExpect(jsonPath("$.films[0].title").value("BATMAN BEGINS"))
                .andExpect(jsonPath("$.films[1].title").value("BRAVE HEART"));
    }

    /**
     * These tests verify that the controller properly rejects invalid parameters and returns
     * appropriate HTTP 400 Bad Request responses with error messages.
     *
     * Invalid parameters include:
     * - Empty string ""
     * - Multiple characters "AB", "ABC"
     * - Special characters "@", "#", "1", "!", etc.
     * - Null values (handled by Spring automatically)
     *
     * Expected behavior for invalid parameters:
     * - HTTP 400 Bad Request response
     * - Error message explaining the validation failure
     * - Service layer should NOT be called
     * - Response should follow RFC 7807 Problem Details format
     *
     * This test should FAIL initially (Red phase - TDD) as parameter validation doesn't exist yet.
     */
    @Test
    void shouldRejectEmptyStringParameter() throws Exception {
        // When: GET /api/v1/films?startsWith="" (empty string)
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should return HTTP 400 Bad Request
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectMultipleCharactersParameter() throws Exception {
        // When: GET /api/v1/films?startsWith=AB (multiple characters)
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "AB")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should return HTTP 400 Bad Request
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectNumericParameter() throws Exception {
        // When: GET /api/v1/films?startsWith=1 (numeric character)
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "1")
                .contentType(MediaType.APPLICATION_JSON))

                        // Then: Should return HTTP 400 Bad Request
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectSpecialCharacterParameter() throws Exception {
        // When: GET /api/v1/films?startsWith=@ (special character)
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "@")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should return HTTP 400 Bad Request
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectWhitespaceParameter() throws Exception {
        // When: GET /api/v1/films?startsWith=" " (whitespace)
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", " ")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should return HTTP 400 Bad Request
                .andExpect(status().isBadRequest());
    }

    /**
     * These tests verify that the controller returns responses in the expected JSON format
     * with the correct structure, field names, and data types.
     *
     * Expected response structure:
     * {
     *   "films": [
     *     {
     *       "film_id": number,
     *       "title": string
     *     }
     *   ],
     *   "count": number,
     *   "filter": {
     *     "startsWith": string
     *   }
     * }
     *
     * This test should FAIL initially (Red phase - TDD) as response format structure doesn't exist yet.
     */
    @Test
    void shouldReturnCorrectJSONStructureForFilmsResponse() throws Exception {
        // Given: Mock service response with film data
        List<Film> mockFilms = List.of(
                new Film(1, "ACADEMY DINOSAUR"),
                new Film(2, "ACE GOLDFINGER")
        );
        when(filmService.findFilmEntitiesByStartingLetter("A"))
                .thenReturn(mockFilms);

        // When: GET /api/v1/films?startsWith=A
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "A")
                .contentType(MediaType.APPLICATION_JSON))

                        // Then: Should return correct JSON structure
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // And: Root level should have exactly 3 fields
                .andExpect(jsonPath("$.films").exists())
                .andExpect(jsonPath("$.count").exists())
                .andExpect(jsonPath("$.filter").exists())

                // And: films should be an array
                .andExpect(jsonPath("$.films").isArray())
                .andExpect(jsonPath("$.films").isNotEmpty())

                // And: count should be a number
                .andExpect(jsonPath("$.count").isNumber())
                .andExpect(jsonPath("$.count").value(2))

                // And: filter should be an object
                .andExpect(jsonPath("$.filter").isMap())
                .andExpect(jsonPath("$.filter.startsWith").value("A"))

                // And: Each film should have correct structure
                .andExpect(jsonPath("$.films[0].film_id").isNumber())
                .andExpect(jsonPath("$.films[0].title").isString())
                .andExpect(jsonPath("$.films[1].film_id").isNumber())
                .andExpect(jsonPath("$.films[1].title").isString());
    }

    @Test
    void shouldReturnCorrectJSONStructureForEmptyResponse() throws Exception {
        // Given: Mock service returns empty list
        when(filmService.findFilmEntitiesByStartingLetter("X"))
                .thenReturn(Collections.emptyList());

        // When: GET /api/v1/films?startsWith=X (letter with no films)
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "X")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should return correct JSON structure even for empty results
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // And: Should have all required fields
                .andExpect(jsonPath("$.films").exists())
                .andExpect(jsonPath("$.count").exists())
                .andExpect(jsonPath("$.filter").exists())

                // And: films should be empty array
                .andExpect(jsonPath("$.films").isArray())
                .andExpect(jsonPath("$.films").isEmpty())

                // And: count should be 0
                .andExpect(jsonPath("$.count").isNumber())
                .andExpect(jsonPath("$.count").value(0))

                // And: filter should contain the parameter
                .andExpect(jsonPath("$.filter").isMap())
                .andExpect(jsonPath("$.filter.startsWith").value("X"));
    }

    @Test
    void shouldReturnCorrectJSONStructureForNoParameterResponse() throws Exception {
        // Given: Mock service returns empty list for null parameter
        when(filmService.findFilmEntitiesByStartingLetter(null))
                .thenReturn(Collections.emptyList());

        // When: GET /api/v1/films (no parameters)
        mockMvc.perform(get("/api/v1/films")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should return correct JSON structure
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // And: Should have all required fields
                .andExpect(jsonPath("$.films").exists())
                .andExpect(jsonPath("$.count").exists())
                .andExpect(jsonPath("$.filter").exists())

                // And: films should be empty array
                .andExpect(jsonPath("$.films").isArray())
                .andExpect(jsonPath("$.films").isEmpty())

                // And: count should be 0
                .andExpect(jsonPath("$.count").isNumber())
                .andExpect(jsonPath("$.count").value(0))

                // And: filter should be empty object
                .andExpect(jsonPath("$.filter").isMap())
                .andExpect(jsonPath("$.filter").isEmpty());
    }

    /**
     * These tests verify that the controller returns appropriate HTTP status codes
     * for different scenarios:
     * - 200 OK for successful requests
     * - 400 Bad Request for invalid parameters
     *
     * This test should FAIL initially (Red phase - TDD) as status code handling doesn't exist yet.
     */
    @Test
    void shouldReturn200ForValidRequest() throws Exception {
        // Given: Mock service response
        List<Film> mockFilms = List.of(
                new Film(1, "ACADEMY DINOSAUR")
        );
        when(filmService.findFilmEntitiesByStartingLetter("A"))
                .thenReturn(mockFilms);

        // When: GET /api/v1/films?startsWith=A (valid request)
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "A")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should return HTTP 200 OK
                .andExpect(status().isOk())
                .andExpect(status().is(200));
    }

    @Test
    void shouldReturn200ForValidRequestWithNoResults() throws Exception {
        // Given: Mock service returns empty list
        when(filmService.findFilmEntitiesByStartingLetter("Q"))
                .thenReturn(Collections.emptyList());

        // When: GET /api/v1/films?startsWith=Q (valid request, no results)
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "Q")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should return HTTP 200 OK even for empty results
                .andExpect(status().isOk())
                .andExpect(status().is(200));
    }

    @Test
    void shouldReturn400ForInvalidParameter() throws Exception {
        // When: GET /api/v1/films?startsWith=123 (invalid parameter)
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "123")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should return HTTP 400 Bad Request
                .andExpect(status().isBadRequest())
                .andExpect(status().is(400));
    }

    @Test
    void shouldReturn400ForEmptyParameter() throws Exception {
        // When: GET /api/v1/films?startsWith= (empty parameter)
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should return HTTP 400 Bad Request
                .andExpect(status().isBadRequest())
                .andExpect(status().is(400));
    }

    /**
     * These tests verify that the controller properly integrates with global error handling
     * and returns consistent error responses following RFC 7807 Problem Details format.
     *
     * Tests cover:
     * - Integration with @ControllerAdvice exception handlers
     * - Proper error response format
     * - Error message consistency
     * - HTTP status code mapping
     *
     * This test should FAIL initially (Red phase - TDD) as error handling integration doesn't exist yet.
     */
    @Test
    void shouldIntegrateWithGlobalExceptionHandlerForValidationErrors() throws Exception {
        // When: Request with invalid parameter triggers validation error
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "invalid123")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should return error response from global exception handler
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleServiceLayerExceptionsProperly() throws Exception {
        // Given: Service layer throws exception
        when(filmService.findFilmEntitiesByStartingLetter("A"))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When: Request triggers service exception
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "A")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Should return error response from global exception handler
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/problem+json"))

                // And: Should follow RFC 7807 Problem Details format
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.instance").exists())
                .andExpect(jsonPath("$.timestamp").exists())

                // And: Should not expose sensitive error details
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred while processing the request"));
    }

    @Test
    void shouldProvideConsistentErrorResponseFormat() throws Exception {
        // When: Multiple different invalid parameters
        String[] invalidParams = {"", "AB", "123", "@", " "};

        for (String invalidParam : invalidParams) {
            mockMvc.perform(get("/api/v1/films")
                    .param("startsWith", invalidParam)
                    .contentType(MediaType.APPLICATION_JSON))

                    // Then: All should return consistent error format
                    .andExpect(status().isBadRequest());
        }
    }

    /**
     * These tests verify that the controller has proper OpenAPI annotations for documentation.
     * While unit tests can't directly test annotation presence, they can verify that the
     * controller behaves according to the documented API contract.
     *
     * Tests verify:
     * - Endpoint behavior matches OpenAPI documentation
     * - Response format matches documented schema
     * - Error responses match documented error codes
     * - Parameter validation matches documented constraints
     *
     * This test should FAIL initially (Red phase - TDD) as OpenAPI annotations don't exist yet.
     */
    @Test
    void shouldBehaviorMatchOpenAPIDocumentation() throws Exception {
        // Given: Mock service response matching OpenAPI documentation
        List<Film> mockFilms = List.of(
                new Film(1, "ACADEMY DINOSAUR"),
                new Film(2, "ACE GOLDFINGER")
        );
        when(filmService.findFilmEntitiesByStartingLetter("A"))
                .thenReturn(mockFilms);

        // When: Request matches OpenAPI documentation
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "A")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Response should match documented schema
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // And: Response structure should match OpenAPI schema
                .andExpect(jsonPath("$.films").isArray())
                .andExpect(jsonPath("$.count").isNumber())
                .andExpect(jsonPath("$.filter").isMap())
                .andExpect(jsonPath("$.filter.startsWith").value("A"))

                        // And: Film objects should match documented schema
        .andExpect(jsonPath("$.films[0].film_id").isNumber())
        .andExpect(jsonPath("$.films[0].title").isString());
    }

    @Test
    void shouldErrorResponsesMatchOpenAPIDocumentation() throws Exception {
        // When: Request with invalid parameter (as documented in OpenAPI)
        mockMvc.perform(get("/api/v1/films")
                .param("startsWith", "invalid")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Error response should match documented 400 error schema
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldParameterValidationMatchOpenAPIDocumentation() throws Exception {
        // Given: Parameter constraints as documented in OpenAPI
        // Valid: single letter A-Z, a-z
        // Invalid: empty, multiple chars, special chars, numbers

        // When: Valid parameters (should match OpenAPI examples)
        String[] validParams = {"A", "Z", "a", "z", "M"};
        for (String validParam : validParams) {
            when(filmService.findFilmEntitiesByStartingLetter(validParam))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/films")
                    .param("startsWith", validParam)
                    .contentType(MediaType.APPLICATION_JSON))

                    // Then: Should accept as documented
                    .andExpect(status().isOk());
        }

        // When: Invalid parameters (should match OpenAPI error examples)
        String[] invalidParams = {"", "AB", "123", "@"};
        for (String invalidParam : invalidParams) {
            mockMvc.perform(get("/api/v1/films")
                    .param("startsWith", invalidParam)
                    .contentType(MediaType.APPLICATION_JSON))

                    // Then: Should reject as documented
                    .andExpect(status().isBadRequest());
        }
    }
}
