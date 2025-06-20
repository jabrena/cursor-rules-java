package info.jab.ms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * FilmDTO - Comprehensive Data Transfer Object for Film Query API
 * 
 * Task 6.6: Create FilmDTO for data transfer ✅
 * 
 * This DTO represents the complete film query response, replacing FilmResponse.
 * It provides a clean separation between entity and response formats.
 * 
 * Complete JSON structure:
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
public record FilmDTO(
    @JsonProperty("films") List<Film> films,
    @JsonProperty("count") int count,
    @JsonProperty("filter") Map<String, Object> filter
) {
    
    /**
     * Individual Film record for the films array
     */
    public record Film(
        @JsonProperty("film_id") Integer filmId,
        @JsonProperty("title") String title
    ) {
        /**
         * Factory method to create Film from Film entity
         * 
         * @param entity The Film entity to convert
         * @return Film record with mapped data
         */
        public static Film fromEntity(info.jab.ms.entity.Film entity) {
            return new Film(entity.filmId(), entity.title());
        }
        
        /**
         * Convert Film to Map format for backward compatibility
         * 
         * @return Map representation of the film
         */
        public Map<String, Object> toMap() {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("film_id", filmId);
            map.put("title", title);
            return map;
        }
    }
    
    /**
     * Factory method to create FilmDTO from film entities and filter
     * 
     * @param entities List of Film entities to convert
     * @param filterMap Map containing filter parameters
     * @return FilmDTO instance with complete response structure
     */
    public static FilmDTO fromEntities(List<info.jab.ms.entity.Film> entities, Map<String, Object> filterMap) {
        List<Film> films = entities.stream()
                .map(Film::fromEntity)
                .toList();
        
        return new FilmDTO(films, films.size(), filterMap);
    }
    
    /**
     * Factory method to create FilmDTO from Map-based films (for backward compatibility)
     * 
     * @param filmMaps List of film maps
     * @param filterMap Map containing filter parameters
     * @return FilmDTO instance with complete response structure
     */
    public static FilmDTO fromMaps(List<Map<String, Object>> filmMaps, Map<String, Object> filterMap) {
        List<Film> films = filmMaps.stream()
                .map(map -> new Film(
                    (Integer) map.get("film_id"),
                    (String) map.get("title")
                ))
                .toList();
        
        return new FilmDTO(films, films.size(), filterMap);
    }
} 