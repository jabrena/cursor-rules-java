package info.jab.ms.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

/**
 * FilmDTO - Comprehensive Data Transfer Object for Film Query API
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
@Schema(
    name = "FilmQueryResponse",
    description = "Response structure for film query operations containing films array, result count, and applied filters",
    example = """
        {
          "films": [
            {
              "film_id": 1,
              "title": "ACADEMY DINOSAUR"
            },
            {
              "film_id": 8,
              "title": "AIRPORT POLLOCK"
            }
          ],
          "count": 46,
          "filter": {
            "startsWith": "A"
          }
        }
        """
)
public record FilmDTO(
    @Schema(
        description = "Array of films matching the query criteria, ordered by film ID",
        example = "[{\"film_id\": 1, \"title\": \"ACADEMY DINOSAUR\"}, {\"film_id\": 8, \"title\": \"AIRPORT POLLOCK\"}]"
    )
    @JsonProperty("films") List<Film> films,

    @Schema(
        description = "Total number of films returned in the response",
        example = "46",
        minimum = "0"
    )
    @JsonProperty("count") int count,

    @Schema(
        description = "Filter parameters applied to the query",
        example = "{\"startsWith\": \"A\"}",
        nullable = true
    )
    @JsonProperty("filter") Map<String, Object> filter
) {

    /**
     * Individual Film record for the films array
     */
    @Schema(
        name = "Film",
        description = "Individual film information with ID and title",
        example = """
            {
              "film_id": 1,
              "title": "ACADEMY DINOSAUR"
            }
            """
    )
    public record Film(
        @Schema(
            description = "Unique identifier for the film from the Sakila database",
            example = "1",
            minimum = "1"
        )
        @JsonProperty("film_id") Integer filmId,

        @Schema(
            description = "Title of the film in uppercase format as stored in the database",
            example = "ACADEMY DINOSAUR",
            maxLength = 255
        )
        @JsonProperty("title") String title
    ) {
        /**
         * Factory method to create Film from Film entity
         *
         * @param entity The Film entity to convert
         * @return Film record with mapped data
         */
        public static Film fromEntity(info.jab.ms.repository.Film entity) {
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
    public static FilmDTO fromEntities(List<info.jab.ms.repository.Film> entities, Map<String, Object> filterMap) {
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
