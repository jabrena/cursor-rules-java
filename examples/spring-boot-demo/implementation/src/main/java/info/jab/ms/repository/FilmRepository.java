package info.jab.ms.repository;

import info.jab.ms.entity.Film;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * FilmRepository - Data Access Layer for Film Query Operations
 * 
 * This repository provides database access methods for querying films from
 * the Sakila database using Spring Data JDBC.
 * 
 * Task 8.3: Create FilmRepository interface extending CrudRepository ✅
 * Task 8.4: Implement findByTitleStartingWith(String prefix) method ✅
 */
@Repository
public interface FilmRepository extends ListCrudRepository<Film, Integer> {

    /**
     * Task 8.4: Implement findByTitleStartingWith(String prefix) method
     * 
     * Finds all films where the title starts with the specified prefix.
     * The query is case-insensitive to match the API requirements.
     * 
     * @param prefix The starting letters to search for (case-insensitive)
     * @return List of films with titles starting with the prefix
     */
    @Query("SELECT film_id, title FROM film WHERE UPPER(title) LIKE UPPER(:prefix || '%') ORDER BY title")
    List<Film> findByTitleStartingWith(@Param("prefix") String prefix);

    /**
     * Alternative method to find all films (for when no filter is applied)
     * 
     * @return List of all films ordered by title
     */
    @Query("SELECT film_id, title FROM film ORDER BY title")
    List<Film> findAllOrderByTitle();
} 