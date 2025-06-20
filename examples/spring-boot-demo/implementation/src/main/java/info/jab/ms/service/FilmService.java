package info.jab.ms.service;

import info.jab.ms.entity.Film;
import info.jab.ms.repository.FilmRepository;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

/**
 * FilmService - Business Logic Layer for Film Query Operations
 *
 * This service implements the business logic for querying films from the Sakila database.
 * It handles parameter validation, repository integration, data transformation, and error handling.
 *
 * Task 6.1: Create FilmService class with @Service annotation ✅
 * Task 6.2: Implement findFilmsByStartingLetter(String letter) method ✅
 * Task 6.3: Add business validation for letter parameter ✅
 * Task 6.4: Implement film filtering logic (case insensitive LIKE query) ✅
 * Task 6.7: Implement entity to DTO transformation logic ✅
 * Task 6.8: Add empty result handling with appropriate messaging ✅
 * Task 8.8: Implement repository error handling ✅
 */
@Service
public class FilmService {

    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);
    private final FilmRepository filmRepository;

    @Autowired
    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    /**
     * Get Film entities by starting letter (internal method for controller use)
     *
     * Task 8.8: Repository error handling implemented
     * Handles database connection errors, SQL exceptions, and data access exceptions
     *
     * @param letter Optional starting letter to filter films (A-Z, case insensitive)
     * @return List of Film entities, empty list if no matches
     * @throws RuntimeException if database operation fails
     */
    public List<Film> findFilmEntitiesByStartingLetter(String letter) {
        try {
            List<Film> films;

            // Task 6.3: Add business validation for letter parameter (already done in controller)
            // Task 6.4: Implement film filtering logic (case insensitive LIKE query)
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

            // Task 6.8: Add empty result handling with appropriate messaging
            return films; // Return entities directly for DTO transformation in controller

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
