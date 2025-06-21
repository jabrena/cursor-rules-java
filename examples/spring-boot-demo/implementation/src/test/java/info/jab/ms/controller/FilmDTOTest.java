package info.jab.ms.controller;

import info.jab.ms.repository.Film;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive unit tests for FilmDTO and its nested Film record.
 *
 * Following Java Unit Testing Guidelines:
 * - Given-When-Then structure
 * - Descriptive test names using should_ExpectedBehavior_when_StateUnderTest pattern
 * - @JsonTest for proper JSON serialization testing
 * - @Nested classes for logical grouping
 * - AssertJ for fluent assertions
 * - Single responsibility per test
 * - Comprehensive coverage including boundary conditions
 */
@DisplayName("FilmDTO Tests")
class FilmDTOTest {

    @Nested
    @DisplayName("FilmDTO Record Construction Tests")
    class FilmDTORecordTests {

        @Test
        @DisplayName("Should create FilmDTO with all required components")
        void should_createFilmDTOWithAllComponents_when_allParametersProvided() {
            // Given
            FilmDTO.Film film1 = new FilmDTO.Film(1, "ACADEMY DINOSAUR");
            FilmDTO.Film film2 = new FilmDTO.Film(2, "ACE GOLDFINGER");
            List<FilmDTO.Film> films = List.of(film1, film2);
            Map<String, Object> filter = Map.of("startsWith", "A");
            int expectedCount = 2;

            // When
            FilmDTO filmDTO = new FilmDTO(films, expectedCount, filter);

            // Then
            assertThat(filmDTO.films())
                .as("Should contain expected films")
                .hasSize(2)
                .containsExactly(film1, film2);

            assertThat(filmDTO.count())
                .as("Should have correct count")
                .isEqualTo(expectedCount);

            assertThat(filmDTO.filter())
                .as("Should contain expected filter")
                .containsEntry("startsWith", "A");
        }

        @Test
        @DisplayName("Should create FilmDTO with null filter")
        void should_createFilmDTOWithNullFilter_when_noFilterProvided() {
            // Given
            List<FilmDTO.Film> films = List.of(new FilmDTO.Film(1, "TEST FILM"));
            int count = 1;

            // When
            FilmDTO filmDTO = new FilmDTO(films, count, null);

            // Then
            assertThat(filmDTO.films())
                .as("Should contain expected films")
                .hasSize(1);

            assertThat(filmDTO.count())
                .as("Should have correct count")
                .isEqualTo(count);

            assertThat(filmDTO.filter())
                .as("Should handle null filter")
                .isNull();
        }

        @Test
        @DisplayName("Should create FilmDTO with empty films list")
        void should_createFilmDTOWithEmptyFilmsList_when_noFilmsFound() {
            // Given
            List<FilmDTO.Film> emptyFilms = List.of();
            Map<String, Object> filter = Map.of("startsWith", "Z");
            int count = 0;

            // When
            FilmDTO filmDTO = new FilmDTO(emptyFilms, count, filter);

            // Then
            assertThat(filmDTO.films())
                .as("Should handle empty films list")
                .isEmpty();

            assertThat(filmDTO.count())
                .as("Should have zero count")
                .isZero();

            assertThat(filmDTO.filter())
                .as("Should preserve filter even with empty results")
                .containsEntry("startsWith", "Z");
        }
    }

    @Nested
    @DisplayName("Film Inner Record Tests")
    class FilmInnerRecordTests {

        @Test
        @DisplayName("Should create Film record with valid data")
        void should_createFilmRecordWithValidData_when_validParametersProvided() {
            // Given
            Integer filmId = 1;
            String title = "ACADEMY DINOSAUR";

            // When
            FilmDTO.Film film = new FilmDTO.Film(filmId, title);

            // Then
            assertThat(film.filmId())
                .as("Should have correct film ID")
                .isEqualTo(filmId);

            assertThat(film.title())
                .as("Should have correct title")
                .isEqualTo(title);
        }

        @Test
        @DisplayName("Should create Film record with null values")
        void should_createFilmRecordWithNullValues_when_nullParametersProvided() {
            // Given - Null parameters

            // When
            FilmDTO.Film film = new FilmDTO.Film(null, null);

            // Then
            assertThat(film.filmId())
                .as("Should handle null film ID")
                .isNull();

            assertThat(film.title())
                .as("Should handle null title")
                .isNull();
        }

        @Test
        @DisplayName("Should create Film from entity using factory method")
        void should_createFilmFromEntity_when_entityProvided() {
            // Given
            Film entity = new Film(1, "ACADEMY DINOSAUR");

            // When
            FilmDTO.Film film = FilmDTO.Film.fromEntity(entity);

            // Then
            assertThat(film.filmId())
                .as("Should map entity ID correctly")
                .isEqualTo(entity.filmId());

            assertThat(film.title())
                .as("Should map entity title correctly")
                .isEqualTo(entity.title());
        }

        @Test
        @DisplayName("Should create Film from entity with null values")
        void should_createFilmFromEntityWithNullValues_when_entityHasNullValues() {
            // Given
            Film entity = new Film(null, null);

            // When
            FilmDTO.Film film = FilmDTO.Film.fromEntity(entity);

            // Then
            assertThat(film.filmId())
                .as("Should handle null entity ID")
                .isNull();

            assertThat(film.title())
                .as("Should handle null entity title")
                .isNull();
        }

        @Test
        @DisplayName("Should convert Film to Map using toMap method")
        void should_convertFilmToMap_when_toMapMethodCalled() {
            // Given
            FilmDTO.Film film = new FilmDTO.Film(1, "ACADEMY DINOSAUR");

            // When
            Map<String, Object> map = film.toMap();

            // Then
            assertThat(map)
                .as("Should have correct map size")
                .hasSize(2);

            assertThat(map.get("film_id"))
                .as("Should have correct film_id mapping")
                .isEqualTo(1);

            assertThat(map.get("title"))
                .as("Should have correct title mapping")
                .isEqualTo("ACADEMY DINOSAUR");
        }

        @Test
        @DisplayName("Should convert Film with null values to Map")
        void should_convertFilmWithNullValuesToMap_when_filmHasNullValues() {
            // Given
            FilmDTO.Film film = new FilmDTO.Film(null, null);

            // When
            Map<String, Object> map = film.toMap();

            // Then
            assertThat(map)
                .as("Should have correct map size even with null values")
                .hasSize(2);

            assertThat(map.get("film_id"))
                .as("Should handle null film_id")
                .isNull();

            assertThat(map.get("title"))
                .as("Should handle null title")
                .isNull();
        }
    }

    @Nested
    @DisplayName("FilmDTO Factory Methods Tests")
    class FilmDTOFactoryMethodsTests {

        @Test
        @DisplayName("Should create FilmDTO from entities using factory method")
        void should_createFilmDTOFromEntities_when_entitiesAndFilterProvided() {
            // Given
            Film entity1 = new Film(1, "ACADEMY DINOSAUR");
            Film entity2 = new Film(2, "ACE GOLDFINGER");
            List<Film> entities = List.of(entity1, entity2);
            Map<String, Object> filterMap = Map.of("startsWith", "A");

            // When
            FilmDTO filmDTO = FilmDTO.fromEntities(entities, filterMap);

            // Then
            assertThat(filmDTO.films())
                .as("Should convert entities to DTOs correctly")
                .hasSize(2);

            assertThat(filmDTO.films().get(0).filmId())
                .as("Should map first entity ID correctly")
                .isEqualTo(1);

            assertThat(filmDTO.films().get(0).title())
                .as("Should map first entity title correctly")
                .isEqualTo("ACADEMY DINOSAUR");

            assertThat(filmDTO.films().get(1).filmId())
                .as("Should map second entity ID correctly")
                .isEqualTo(2);

            assertThat(filmDTO.films().get(1).title())
                .as("Should map second entity title correctly")
                .isEqualTo("ACE GOLDFINGER");

            assertThat(filmDTO.count())
                .as("Should calculate count correctly")
                .isEqualTo(2);

            assertThat(filmDTO.filter())
                .as("Should preserve filter")
                .containsEntry("startsWith", "A");
        }

        @Test
        @DisplayName("Should create FilmDTO from empty entities list")
        void should_createFilmDTOFromEmptyEntitiesList_when_noEntitiesProvided() {
            // Given
            List<Film> entities = List.of();
            Map<String, Object> filterMap = Map.of("startsWith", "Z");

            // When
            FilmDTO filmDTO = FilmDTO.fromEntities(entities, filterMap);

            // Then
            assertThat(filmDTO.films())
                .as("Should handle empty entities list")
                .isEmpty();

            assertThat(filmDTO.count())
                .as("Should have zero count for empty list")
                .isZero();

            assertThat(filmDTO.filter())
                .as("Should preserve filter even with empty entities")
                .containsEntry("startsWith", "Z");
        }

        @Test
        @DisplayName("Should create FilmDTO from entities with null filter")
        void should_createFilmDTOFromEntitiesWithNullFilter_when_noFilterProvided() {
            // Given
            Film entity = new Film(1, "TEST FILM");
            List<Film> entities = List.of(entity);

            // When
            FilmDTO filmDTO = FilmDTO.fromEntities(entities, null);

            // Then
            assertThat(filmDTO.films())
                .as("Should convert entities correctly")
                .hasSize(1);

            assertThat(filmDTO.count())
                .as("Should calculate count correctly")
                .isEqualTo(1);

            assertThat(filmDTO.filter())
                .as("Should handle null filter")
                .isNull();
        }

        @Test
        @DisplayName("Should create FilmDTO from Maps using factory method")
        void should_createFilmDTOFromMaps_when_mapsAndFilterProvided() {
            // Given
            Map<String, Object> filmMap1 = Map.of("film_id", 1, "title", "ACADEMY DINOSAUR");
            Map<String, Object> filmMap2 = Map.of("film_id", 2, "title", "ACE GOLDFINGER");
            List<Map<String, Object>> filmMaps = List.of(filmMap1, filmMap2);
            Map<String, Object> filterMap = Map.of("startsWith", "A");

            // When
            FilmDTO filmDTO = FilmDTO.fromMaps(filmMaps, filterMap);

            // Then
            assertThat(filmDTO.films())
                .as("Should convert maps to DTOs correctly")
                .hasSize(2);

            assertThat(filmDTO.films().get(0).filmId())
                .as("Should map first film ID from map")
                .isEqualTo(1);

            assertThat(filmDTO.films().get(0).title())
                .as("Should map first film title from map")
                .isEqualTo("ACADEMY DINOSAUR");

            assertThat(filmDTO.count())
                .as("Should calculate count from maps")
                .isEqualTo(2);

            assertThat(filmDTO.filter())
                .as("Should preserve filter")
                .containsEntry("startsWith", "A");
        }

        @Test
        @DisplayName("Should create FilmDTO from empty Maps list")
        void should_createFilmDTOFromEmptyMapsList_when_noMapsProvided() {
            // Given
            List<Map<String, Object>> filmMaps = List.of();
            Map<String, Object> filterMap = Map.of("startsWith", "Z");

            // When
            FilmDTO filmDTO = FilmDTO.fromMaps(filmMaps, filterMap);

            // Then
            assertThat(filmDTO.films())
                .as("Should handle empty maps list")
                .isEmpty();

            assertThat(filmDTO.count())
                .as("Should have zero count for empty maps")
                .isZero();

            assertThat(filmDTO.filter())
                .as("Should preserve filter even with empty maps")
                .containsEntry("startsWith", "Z");
        }

        @Test
        @DisplayName("Should create FilmDTO from Maps with null values")
        void should_createFilmDTOFromMapsWithNullValues_when_mapsContainNulls() {
            // Given
            Map<String, Object> filmMap = Map.of("film_id", 1, "title", "ACADEMY DINOSAUR");
            List<Map<String, Object>> filmMaps = List.of(filmMap);

            // When
            FilmDTO filmDTO = FilmDTO.fromMaps(filmMaps, null);

            // Then
            assertThat(filmDTO.films())
                .as("Should convert maps correctly")
                .hasSize(1);

            assertThat(filmDTO.count())
                .as("Should calculate count correctly")
                .isEqualTo(1);

            assertThat(filmDTO.filter())
                .as("Should handle null filter")
                .isNull();
        }

        @Test
        @DisplayName("Should handle Maps with null film_id and title")
        void should_handleMapsWithNullFilmIdAndTitle_when_mapValuesAreNull() {
            // Given
            Map<String, Object> filmMap1 = Map.of("film_id", 1, "title", "VALID FILM");
            Map<String, Object> filmMap2 = Map.of(); // Map without required keys
            List<Map<String, Object>> filmMaps = List.of(filmMap1, filmMap2);
            Map<String, Object> filterMap = Map.of("startsWith", "A");

            // When
            FilmDTO filmDTO = FilmDTO.fromMaps(filmMaps, filterMap);

            // Then
            assertThat(filmDTO.films())
                .as("Should handle maps with missing values")
                .hasSize(2);

            assertThat(filmDTO.films().get(0))
                .as("Should map valid film correctly")
                .satisfies(film -> {
                    assertThat(film.filmId()).isEqualTo(1);
                    assertThat(film.title()).isEqualTo("VALID FILM");
                });

            assertThat(filmDTO.films().get(1))
                .as("Should handle missing map values as null")
                .satisfies(film -> {
                    assertThat(film.filmId()).isNull();
                    assertThat(film.title()).isNull();
                });
        }
    }

    @Nested
    @DisplayName("Data Consistency Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should maintain data consistency through entity->DTO->Map conversion")
        void should_maintainDataConsistency_when_convertingEntityToDtoToMap() {
            // Given
            Film originalEntity = new Film(42, "ORIGINAL TITLE");

            // When - Convert entity to DTO to Map
            FilmDTO.Film dtoFilm = FilmDTO.Film.fromEntity(originalEntity);
            Map<String, Object> resultMap = dtoFilm.toMap();

            // Then - Data should be preserved through conversions
            assertThat(resultMap.get("film_id"))
                .as("Film ID should be preserved through conversions")
                .isEqualTo(originalEntity.filmId());

            assertThat(resultMap.get("title"))
                .as("Title should be preserved through conversions")
                .isEqualTo(originalEntity.title());
        }

        @Test
        @DisplayName("Should maintain data consistency through Map->DTO->Map conversion")
        void should_maintainDataConsistency_when_convertingMapToDtoToMap() {
            // Given
            Map<String, Object> originalMap = Map.of("film_id", 99, "title", "MAP TITLE");

            // When - Create DTO from map and convert back to map
            FilmDTO filmDTO = FilmDTO.fromMaps(List.of(originalMap), null);
            Map<String, Object> resultMap = filmDTO.films().get(0).toMap();

            // Then - Data should be preserved through conversions
            assertThat(resultMap)
                .as("Map should be preserved through DTO conversion")
                .containsExactlyInAnyOrderEntriesOf(originalMap);
        }
    }

    @Nested
    @JsonTest
    @DisplayName("JSON Serialization Tests")
    class JsonSerializationTests {

        @Autowired
        private JacksonTester<FilmDTO> json;

        @Autowired
        private JacksonTester<FilmDTO.Film> filmJson;

        @Test
        @DisplayName("Should serialize FilmDTO to JSON correctly")
        void should_serializeFilmDTOToJson_when_validFilmDTOProvided() throws Exception {
            // Given
            FilmDTO.Film film1 = new FilmDTO.Film(1, "ACADEMY DINOSAUR");
            FilmDTO.Film film2 = new FilmDTO.Film(2, "ACE GOLDFINGER");
            List<FilmDTO.Film> films = List.of(film1, film2);
            Map<String, Object> filter = Map.of("startsWith", "A");
            FilmDTO filmDTO = new FilmDTO(films, 2, filter);

            // When & Then
            assertThat(json.write(filmDTO))
                .as("Should serialize films array correctly")
                .hasJsonPath("$.films")
                .hasJsonPath("$.films[0].film_id", 1)
                .hasJsonPath("$.films[0].title", "ACADEMY DINOSAUR")
                .hasJsonPath("$.films[1].film_id", 2)
                .hasJsonPath("$.films[1].title", "ACE GOLDFINGER")
                .hasJsonPath("$.count", 2)
                .hasJsonPath("$.filter.startsWith", "A");
        }

        @Test
        @DisplayName("Should serialize empty FilmDTO to JSON correctly")
        void should_serializeEmptyFilmDTOToJson_when_emptyFilmDTOProvided() throws Exception {
            // Given
            FilmDTO filmDTO = new FilmDTO(List.of(), 0, Map.of("startsWith", "Z"));

            // When & Then
            assertThat(json.write(filmDTO))
                .as("Should serialize empty films array correctly")
                .hasJsonPath("$.films")
                .hasJsonPath("$.count", 0)
                .hasJsonPath("$.filter.startsWith", "Z");

            assertThat(json.write(filmDTO))
                .as("Films array should be empty")
                .hasEmptyJsonPathValue("$.films");
        }

        @Test
        @DisplayName("Should deserialize JSON to FilmDTO correctly")
        void should_deserializeJsonToFilmDTO_when_validJsonProvided() throws Exception {
            // Given
            String jsonContent = """
                {
                    "films": [
                        {"film_id": 1, "title": "ACADEMY DINOSAUR"},
                        {"film_id": 2, "title": "ACE GOLDFINGER"}
                    ],
                    "count": 2,
                    "filter": {"startsWith": "A"}
                }
                """;

            // When & Then
            assertThat(json.parse(jsonContent))
                .as("Should deserialize JSON to FilmDTO correctly")
                .satisfies(filmDTO -> {
                    assertThat(filmDTO.films()).hasSize(2);
                    assertThat(filmDTO.films().get(0).filmId()).isEqualTo(1);
                    assertThat(filmDTO.films().get(0).title()).isEqualTo("ACADEMY DINOSAUR");
                    assertThat(filmDTO.count()).isEqualTo(2);
                    assertThat(filmDTO.filter()).containsEntry("startsWith", "A");
                });
        }

        @Test
        @DisplayName("Should serialize individual Film to JSON correctly")
        void should_serializeFilmToJson_when_validFilmProvided() throws Exception {
            // Given
            FilmDTO.Film film = new FilmDTO.Film(42, "TEST FILM TITLE");

            // When & Then
            assertThat(filmJson.write(film))
                .as("Should serialize Film correctly")
                .hasJsonPath("$.film_id", 42)
                .hasJsonPath("$.title", "TEST FILM TITLE");
        }

        @Test
        @DisplayName("Should handle null values in JSON serialization")
        void should_handleNullValuesInJsonSerialization_when_nullValuesPresent() throws Exception {
            // Given
            FilmDTO.Film filmWithNulls = new FilmDTO.Film(null, null);
            FilmDTO filmDTOWithNulls = new FilmDTO(List.of(filmWithNulls), 1, null);

            // When & Then
            assertThat(json.write(filmDTOWithNulls))
                .as("Should handle null values in serialization")
                .hasJsonPath("$.films[0].film_id")
                .hasJsonPath("$.films[0].title")
                .hasJsonPath("$.count", 1)
                .hasJsonPath("$.filter");
        }
    }
}
