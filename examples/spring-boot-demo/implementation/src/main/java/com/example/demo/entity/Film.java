package com.example.demo.entity;

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
public class Film {

    @Id
    @Column("film_id")
    private Integer filmId;

    @Column("title")
    private String title;

    // Default constructor
    public Film() {}

    // Constructor with all fields
    public Film(Integer filmId, String title) {
        this.filmId = filmId;
        this.title = title;
    }

    // Getters and setters
    public Integer getFilmId() {
        return filmId;
    }

    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Film film = (Film) obj;
        return filmId != null ? filmId.equals(film.filmId) : film.filmId == null &&
               title != null ? title.equals(film.title) : film.title == null;
    }

    @Override
    public int hashCode() {
        int result = filmId != null ? filmId.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Film{" +
                "filmId=" + filmId +
                ", title='" + title + '\'' +
                '}';
    }
} 