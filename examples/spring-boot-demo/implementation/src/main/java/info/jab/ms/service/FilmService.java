package info.jab.ms.service;

import info.jab.ms.repository.Film;
import info.jab.ms.repository.FilmRepository;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * FilmService - Business Logic Layer for Film Query Operations
 *
 * This service implements the business logic for querying films from the Sakila database.
 * It handles parameter validation, repository integration, data transformation, and error handling.
 *
 * Following Spring Data JDBC best practices:
 * - @Transactional(readOnly = true) for read operations to optimize performance
 * - Constructor injection for better testability
 * - Proper exception handling and logging
 */
@Service
@Transactional(readOnly = true)
public class FilmService {

    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);

    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    /**
     * Get Film entities by starting letter (internal method for controller use)
     *
     * Handles database connection errors, SQL exceptions, and data access exceptions.
     * Uses read-only transaction for optimal performance.
     *
     * @param letter Optional starting letter to filter films (A-Z, case insensitive)
     * @return List of Film entities, empty list if no matches
     * @throws RuntimeException if database operation fails
     */
    public List<Film> findFilmEntitiesByStartingLetter(String letter) {
        try {
            List<Film> films;

            if (Objects.nonNull(letter) && !letter.trim().isEmpty()) {
                logger.debug("Searching for films starting with letter: {}", letter);
                // Get films starting with the specified letter (case insensitive)
                films = filmRepository.findByTitleStartingWith(letter.trim());
                logger.debug("Found {} films starting with letter: {}", films.size(), letter);
            } else {
                logger.debug("Retrieving all films (no filter applied)");
                // Get all films when no filter is applied
                films = filmRepository.findAllOrderByTitle();
                logger.debug("Found {} total films", films.size());
            }

            return films;
        } catch (EmptyResultDataAccessException e) {
            // Handle case where no results are found (though this shouldn't happen with List return type)
            logger.info("No films found for search criteria: {}", letter);
            return List.of(); // Return empty list instead of throwing exception

        } catch (DataIntegrityViolationException e) {
            // Handle database constraint violations
            logger.error("Data integrity violation while searching films with letter: {}", letter, e);
            throw new RuntimeException("Database integrity error occurred while searching films", e);

        } catch (DataAccessException e) {
            // Handle general database access errors (connection issues, SQL errors, etc.)
            logger.error("Database access error while searching films with letter: {}", letter, e);
            throw new RuntimeException("Database error occurred while searching films. Please try again later.", e);

        } catch (Exception e) {
            // Handle any other unexpected errors
            logger.error("Unexpected error while searching films with letter: {}", letter, e);
            throw new RuntimeException("An unexpected error occurred while searching films", e);
        }
    }
}
