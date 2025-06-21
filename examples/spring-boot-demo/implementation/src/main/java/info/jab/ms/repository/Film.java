package info.jab.ms.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Film Entity - Immutable Record for Sakila Database Film Table
 *
 * This record represents a film from the Sakila sample database, following
 * Spring Data JDBC best practices for entity design:
 *
 * - Uses Java record for immutability and thread safety
 * - Explicit @Column annotations for clear database mapping
 * - @Table annotation for table name mapping
 * - Simple, focused entity with only required fields
 *
 * Database table: film
 * Primary key: film_id (Integer)
 *
 * This entity is designed for read-only operations in the Film Query API.
 */
@Table("film")
public record Film(
    @Id @Column("film_id") Integer filmId,
    @Column("title") String title
) {}
