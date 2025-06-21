package info.jab.ms.controller;

import info.jab.ms.repository.Film;
import info.jab.ms.service.FilmService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FilmController - REST API Controller Implementation for Film Query Operations
 *
 * This controller implements the FilmControllerApi interface and provides the business logic
 * for querying films from the Sakila database. It focuses on the implementation details
 * while the API contract and documentation are defined in the interface.
 *
 * The controller implements endpoints for retrieving films that start with specific letters,
 * with proper validation, error handling, and response formatting.
 */
@RestController
@RequestMapping("/api/v1")
public class FilmController implements FilmControllerApi {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * Implementation of the getFilms method defined in FilmControllerApi.
     *
     * This method contains the business logic for retrieving films from the Sakila database,
     * with parameter validation, service layer calls, and response formatting.
     * All API documentation is defined in the interface.
     */
    @Override
    public ResponseEntity<FilmDTO> getFilms(String startsWith) {
        if (Objects.nonNull(startsWith)) {
            if (!isValidStartsWithParameter(startsWith)) {
                return ResponseEntity.badRequest().build();
            }
        }

        List<Film> films = filmService.findFilmEntitiesByStartingLetter(startsWith);

        Map<String, Object> filter = new HashMap<>();
        if (Objects.nonNull(startsWith) && !startsWith.trim().isEmpty()) {
            filter.put("startsWith", startsWith);
        }

        FilmDTO response = FilmDTO.fromEntities(films, filter);

        return ResponseEntity.ok(response);
    }

    private boolean isValidStartsWithParameter(String startsWith) {
        if (Objects.isNull(startsWith) || startsWith.trim().isEmpty()) {
            return false;
        }

        String trimmed = startsWith.trim();

        // Check if it's a single character
        if (trimmed.length() != 1) {
            return false;
        }

        char character = trimmed.charAt(0);

        // Check if it's a letter (A-Z, a-z)
        return Character.isLetter(character);
    }
}
