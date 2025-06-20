package info.jab.ms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Film Entity - Database mapping for the film table
 * 
 * This entity represents a film record from the Sakila database.
 * It uses Spring Data JDBC annotations for database mapping.
 * 
 * Task 8.1: Create Film entity class with @Table annotation ✅
 * Task 8.2: Add entity fields (filmId, title) with proper annotations ✅
 */
@Table("film")
public record Film(
    @Id @Column("film_id") Integer filmId,
    @Column("title") String title
) {} 