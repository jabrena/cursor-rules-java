package info.jab.ms.controller;

import info.jab.ms.repository.Film;
import info.jab.ms.service.FilmService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FilmController - REST API Controller Implementation for Film Query Operations
 *
 * This controller implements the FilmControllerApi interface and provides the business logic
 * for querying films from the Sakila database. Following Spring Data JDBC and Spring Boot
 * best practices:
 *
 * - Constructor-based dependency injection for better testability
 * - Separation of API contract (interface) from implementation
 * - Clean validation with functional predicates
 * - Proper error handling and response formatting
 * - Stateless design with immutable operations
 *
 * The controller focuses on HTTP concerns while delegating business logic to the service layer.
 */
@RestController
@RequestMapping("/api/v1")
public class FilmController implements FilmControllerApi {

    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);

    private final FilmService filmService;

    // Predicate to validate that startsWith parameter is exactly one letter
    private static final Predicate<String> IS_VALID_STARTS_WITH = startsWith ->
            Objects.nonNull(startsWith) && startsWith.trim().matches("^[a-zA-Z]$");

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * Implementation of the getFilms method defined in FilmControllerApi.
     *
     * This method handles the HTTP layer concerns:
     * - Request parameter validation
     * - Service layer delegation
     * - Response formatting and mapping
     * - HTTP status code handling
     *
     * Business logic is delegated to the service layer following separation of concerns.
     * All API documentation is defined in the FilmControllerApi interface.
     *
     * @param startsWith Optional parameter to filter films by starting letter
     * @return ResponseEntity with FilmDTO containing films, count, and filter information
     */
    @Override
    public ResponseEntity<FilmDTO> getFilms(String startsWith) {
        // Validate input parameter using functional predicate
        if (Objects.nonNull(startsWith)) {
            if (!IS_VALID_STARTS_WITH.test(startsWith)) {
                return ResponseEntity.badRequest().build();
            }
        }

        // Delegate business logic to service layer
        logger.debug("Fetching films starting with letter: {}", startsWith);
        List<Film> films = filmService.findFilmEntitiesByStartingLetter(startsWith);

        // Build filter map for response metadata
        Map<String, Object> filter = new HashMap<>();
        if (Objects.nonNull(startsWith) && !startsWith.trim().isEmpty()) {
            filter.put("startsWith", startsWith);
        }

        // Create response DTO using factory method
        FilmDTO response = FilmDTO.fromEntities(films, filter);

        return ResponseEntity.ok(response);
    }
}
