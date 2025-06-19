package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * FilmResponse DTO - JSON Response format for Film Query API
 * 
 * Task 4.5: Create FilmResponse DTO for JSON response format
 * 
 * This DTO structures the JSON response for the GET /api/v1/films endpoint.
 * It provides a consistent response format containing films, count, and filter information.
 * 
 * Expected JSON structure:
 * {
 *   "films": [
 *     {
 *       "film_id": 1,
 *       "title": "ACADEMY DINOSAUR"
 *     }
 *   ],
 *   "count": 46,
 *   "filter": {
 *     "startsWith": "A"
 *   }
 * }
 */
public class FilmResponse {
    
    @JsonProperty("films")
    private List<Map<String, Object>> films;
    
    @JsonProperty("count")
    private int count;
    
    @JsonProperty("filter")
    private Map<String, Object> filter;
    
    // Default constructor
    public FilmResponse() {}
    
    // Constructor with all fields
    public FilmResponse(List<Map<String, Object>> films, int count, Map<String, Object> filter) {
        this.films = films;
        this.count = count;
        this.filter = filter;
    }
    
    // Getters and setters
    public List<Map<String, Object>> getFilms() {
        return films;
    }
    
    public void setFilms(List<Map<String, Object>> films) {
        this.films = films;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public Map<String, Object> getFilter() {
        return filter;
    }
    
    public void setFilter(Map<String, Object> filter) {
        this.filter = filter;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        FilmResponse that = (FilmResponse) obj;
        return count == that.count &&
               films != null ? films.equals(that.films) : that.films == null &&
               filter != null ? filter.equals(that.filter) : that.filter == null;
    }
    
    @Override
    public int hashCode() {
        int result = films != null ? films.hashCode() : 0;
        result = 31 * result + count;
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return "FilmResponse{" +
                "films=" + films +
                ", count=" + count +
                ", filter=" + filter +
                '}';
    }
} 