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
public record FilmResponse(
    @JsonProperty("films") List<Map<String, Object>> films,
    @JsonProperty("count") int count,
    @JsonProperty("filter") Map<String, Object> filter
) {} 