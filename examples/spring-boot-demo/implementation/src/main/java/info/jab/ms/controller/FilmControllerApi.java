package info.jab.ms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * FilmControllerApi - API Contract for Film Query Operations
 *
 * This interface defines the REST API contract for querying films from the Sakila database.
 * It contains all the OpenAPI/Swagger documentation and defines the API endpoints for
 * retrieving films that start with specific letters.
 *
 * API Endpoints:
 * - GET /api/v1/films - Retrieve all films or filter by starting letter
 * - GET /api/v1/films?startsWith=A - Retrieve films starting with letter "A"
 *
 * This interface separates the API contract and documentation from the business logic
 * implementation, following the principle of separation of concerns.
 */
@Tag(name = "Films", description = "Film query operations")
public interface FilmControllerApi {

    /**
     * Retrieves films from the Sakila database, optionally filtered by starting letter.
     *
     * @param startsWith Optional parameter to filter films by starting letter (single character A-Z)
     * @return JSON response containing films array, count, and filter information or error response
     */
    @Operation(
        summary = "Query films by starting letter",
        description = """
            ### Query Films from Sakila Database

            Retrieves films from the PostgreSQL Sakila database that start with the specified letter.
            The search is case-insensitive and returns complete film information including ID and title.

            ### Query Behavior
            - **Without parameter**: Returns all films in the database (1000 films)
            - **With startsWith parameter**: Returns films starting with the specified letter
            - **Case handling**: Search is case-insensitive (both 'A' and 'a' work identically)
            - **Ordering**: Results are ordered by film ID for consistent pagination

            ### Performance Characteristics
            - **Response time**: Guaranteed under 2 seconds for all queries
            - **Database optimization**: Uses indexed queries for optimal performance
            - **Result caching**: Frequently accessed results may be cached
            - **Connection pooling**: Efficient database connection management

            ### Expected Result Counts
            Based on the complete Sakila database:
            - **Letter "A"**: 46 films (e.g., "ACADEMY DINOSAUR", "AIRPORT POLLOCK")
            - **Letter "B"**: 37 films (e.g., "BADMAN DAWN", "BATMAN BEGINS")
            - **Letter "C"**: 57 films (e.g., "CABIN FLASH", "CALIFORNIA BIRDS")
            - **Letter "D"**: 41 films
            - **Letter "E"**: 20 films
            - **Letter "F"**: 58 films
            - **Letter "G"**: 42 films
            - **Letter "H"**: 49 films

            ### Usage Examples
            ```
            GET /api/v1/films?startsWith=A    # Returns 46 films starting with 'A'
            GET /api/v1/films?startsWith=a    # Same result (case-insensitive)
            GET /api/v1/films                 # Returns all films
            ```
            """,
        operationId = "getFilmsByStartingLetter",
        tags = {"Films"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = """
                Successfully retrieved films matching the query criteria.
                Response includes films array, total count, and applied filter parameters.
                """,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = info.jab.ms.controller.FilmDTO.class),
                examples = {
                    @ExampleObject(
                        name = "Films starting with A",
                        summary = "46 films starting with letter 'A' (most common scenario)",
                        description = "Returns all films from the Sakila database that start with letter 'A'. This is the most frequently requested query with 46 matching films.",
                        value = """
                            {
                              "films": [
                                {
                                  "film_id": 1,
                                  "title": "ACADEMY DINOSAUR"
                                },
                                {
                                  "film_id": 8,
                                  "title": "AIRPORT POLLOCK"
                                },
                                {
                                  "film_id": 10,
                                  "title": "ALADDIN CALENDAR"
                                },
                                {
                                  "film_id": 13,
                                  "title": "ALI FOREVER"
                                },
                                {
                                  "film_id": 15,
                                  "title": "ALIEN CENTER"
                                }
                              ],
                              "count": 46,
                              "filter": {
                                "startsWith": "A"
                              }
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Films starting with B",
                        summary = "37 films starting with letter 'B'",
                        description = "Example response showing films starting with 'B'. Note the case-insensitive filter behavior.",
                        value = """
                            {
                              "films": [
                                {
                                  "film_id": 16,
                                  "title": "ALLEY EVOLUTION"
                                },
                                {
                                  "film_id": 23,
                                  "title": "ANACONDA CONFESSIONS"
                                },
                                {
                                  "film_id": 25,
                                  "title": "ANGELS LIFE"
                                }
                              ],
                              "count": 37,
                              "filter": {
                                "startsWith": "B"
                              }
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "All films (no filter)",
                        summary = "All films when no startsWith parameter is provided",
                        description = "Returns all films from the database when no filter is applied. Shows the complete dataset structure.",
                        value = """
                            {
                              "films": [
                                {
                                  "film_id": 1,
                                  "title": "ACADEMY DINOSAUR"
                                },
                                {
                                  "film_id": 2,
                                  "title": "ACE GOLDFINGER"
                                },
                                {
                                  "film_id": 3,
                                  "title": "ADAPTATION HOLES"
                                }
                              ],
                              "count": 1000,
                              "filter": {}
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Empty result set",
                        summary = "No films found for a specific letter",
                        description = "Example response when no films match the filter criteria. Some letters might have no films in the database.",
                        value = """
                            {
                              "films": [],
                              "count": 0,
                              "filter": {
                                "startsWith": "Q"
                              }
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Case-insensitive filter",
                        summary = "Lowercase parameter produces same results",
                        description = "Demonstrates case-insensitive behavior - lowercase 'a' returns the same results as uppercase 'A'.",
                        value = """
                            {
                              "films": [
                                {
                                  "film_id": 1,
                                  "title": "ACADEMY DINOSAUR"
                                },
                                {
                                  "film_id": 8,
                                  "title": "AIRPORT POLLOCK"
                                }
                              ],
                              "count": 46,
                              "filter": {
                                "startsWith": "a"
                              }
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = """
                Bad Request - Invalid query parameter provided.
                The startsWith parameter must be a single letter (A-Z or a-z).
                Returns empty response body.
                """
        ),
        @ApiResponse(
            responseCode = "500",
            description = """
                Internal Server Error - An unexpected error occurred while processing the request.
                This could be due to database connectivity issues or other system problems.
                """,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = org.springframework.http.ProblemDetail.class),
                examples = {
                    @ExampleObject(
                        name = "Database connection error",
                        summary = "Database connectivity issues",
                        description = "Response when the service cannot connect to the PostgreSQL database",
                        value = """
                            {
                              "title": "Database Connection Error",
                              "status": 500,
                              "detail": "Unable to connect to the database. Please try again later.",
                              "instance": "/api/v1/films",
                              "timestamp": "2024-01-15T10:30:00Z"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Query timeout error",
                        summary = "Database query timeout",
                        description = "Response when a database query takes longer than the configured timeout",
                        value = """
                            {
                              "title": "Query Timeout",
                              "status": 500,
                              "detail": "The database query timed out. Please try again with a smaller dataset.",
                              "instance": "/api/v1/films",
                              "timestamp": "2024-01-15T10:30:00Z"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Generic server error",
                        summary = "General internal server error",
                        description = "Response for unexpected server errors not covered by specific error types",
                        value = """
                            {
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "An unexpected error occurred while processing your request",
                              "instance": "/api/v1/films",
                              "timestamp": "2024-01-15T10:30:00Z"
                            }
                            """
                    )
                }
            )
        )
    })
    @GetMapping("/films")
    ResponseEntity<FilmDTO> getFilms(
            @Parameter(
                name = "startsWith",
                description = """
                    Filter films by their starting letter.

                    **Requirements:**
                    - Must be a single letter (A-Z or a-z)
                    - Case-insensitive (both 'A' and 'a' return the same results)
                    - Cannot be empty, numeric, or special characters
                    - Cannot be multiple characters

                    **Examples:**
                    - `A` or `a` → Returns 46 films starting with 'A'
                    - `B` or `b` → Returns 37 films starting with 'B'
                    - `Z` or `z` → Returns films starting with 'Z'

                    **Invalid values:**
                    - `AB` (multiple characters)
                    - `1` (numeric)
                    - `@` (special character)
                    - ` ` (empty/whitespace)
                    """,
                example = "A",
                required = false,
                schema = @Schema(
                    type = "string",
                    pattern = "^[A-Za-z]$",
                    minLength = 1,
                    maxLength = 1,
                    description = "Single letter (A-Z, case-insensitive)"
                )
            )
            @RequestParam(required = false) String startsWith);
}
