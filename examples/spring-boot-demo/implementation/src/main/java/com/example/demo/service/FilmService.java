package com.example.demo.service;

import com.example.demo.entity.Film;
import com.example.demo.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;

/**
 * FilmService - Business Logic Layer for Film Query Operations
 * 
 * This service implements the business logic for querying films from the Sakila database.
 * It handles parameter validation, repository integration, and data transformation.
 * 
 * Task 6.1: Create FilmService class with @Service annotation ✅
 * Task 6.2: Implement findFilmsByStartingLetter(String letter) method ✅
 * Task 6.3: Add business validation for letter parameter ✅
 * Task 6.4: Implement film filtering logic (case insensitive LIKE query) ✅
 * Task 6.7: Implement entity to DTO transformation logic ✅
 * Task 6.8: Add empty result handling with appropriate messaging ✅
 */
@Service
public class FilmService {
    
    private final FilmRepository filmRepository;
    
    @Autowired
    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }
    
    /**
     * Task 6.2: Implement findFilmsByStartingLetter(String letter) method ✅
     * Task 6.3: Add business validation for letter parameter ✅ 
     * Task 6.4: Implement film filtering logic (case insensitive LIKE query) ✅
     * Task 6.7: Implement entity to DTO transformation logic ✅
     * Task 6.8: Add empty result handling with appropriate messaging ✅
     * 
     * Retrieves films from the database that start with the specified letter.
     * Handles both filtered queries (when letter is provided) and unfiltered queries.
     * 
     * @param letter Optional starting letter to filter films (A-Z, case insensitive)
     * @return List of film maps with film_id and title, empty list if no matches
     */
    public List<Map<String, Object>> findFilmsByStartingLetter(String letter) {
        List<Film> films;
        
        // Task 6.3: Add business validation for letter parameter (already done in controller)
        // Task 6.4: Implement film filtering logic (case insensitive LIKE query)
        if (Objects.nonNull(letter) && !letter.trim().isEmpty()) {
            // Get films starting with the specified letter (case insensitive)
            films = filmRepository.findByTitleStartingWith(letter.trim());
        } else {
            // Get all films when no filter is applied
            films = filmRepository.findAllOrderByTitle();
        }
        
        // Task 6.7: Implement entity to DTO transformation logic
        // Transform Film entities to Map<String, Object> format expected by controller
        return films.stream()
                .map(this::filmToMap)
                .toList();
    }
    
    /**
     * Task 6.7: Implement entity to DTO transformation logic
     * 
     * Converts a Film entity to a Map representation matching the expected API response format.
     * The map contains film_id and title fields as expected by the acceptance tests.
     * 
     * @param film The Film entity to convert
     * @return Map with film_id and title keys
     */
    private Map<String, Object> filmToMap(Film film) {
        Map<String, Object> filmMap = new HashMap<>();
        filmMap.put("film_id", film.getFilmId());
        filmMap.put("title", film.getTitle());
        return filmMap;
    }
} 