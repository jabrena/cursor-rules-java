package info.jab.ms.repository;

import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * FilmRepository - Data Access Layer for Film Query Operations
 *
 * This repository provides database access methods for querying films from
 * the Sakila database using Spring Data JDBC.
 *
 * Following Spring Data JDBC best practices:
 * - Extends ListCrudRepository for optimal List return types
 * - Uses @Query for custom operations with proper parameter binding
 * - Provides clean, simple queries without unnecessary complexity
 */
@Repository
public interface FilmRepository extends ListCrudRepository<Film, Integer> {

    //TDDO: Remove collate
    /**
     * Finds all films where the title starts with the specified prefix.
     * The query is case-insensitive to match the API requirements.
     *
     * @param prefix The starting letters to search for (case-insensitive)
     * @return List of films with titles starting with the prefix
     */
    @Query("SELECT film_id, title FROM film WHERE UPPER(title) LIKE UPPER(:prefix || '%') ORDER BY title COLLATE \"C\"")
    List<Film> findByTitleStartingWith(@Param("prefix") String prefix);

    //TDDO: Remove collate
    /**
     * Alternative method to find all films (for when no filter is applied)
     *
     * @return List of all films ordered by title
     */
    @Query("SELECT film_id, title FROM film ORDER BY title COLLATE \"C\"")
    List<Film> findAllOrderByTitle();
}
