package info.jab.ms.controller;

import info.jab.ms.repository.Film;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive unit tests for FilmDTO and its nested Film record
 * to achieve maximum JaCoCo coverage.
 */
@DisplayName("FilmDTO Tests")
class FilmDTOTest {

    @Nested
    @DisplayName("FilmDTO Record Tests")
    class FilmDTORecordTests {

        @Test
        @DisplayName("Should create FilmDTO with all components")
        void shouldCreateFilmDTOWithAllComponents() {
            // Given
            FilmDTO.Film film1 = new FilmDTO.Film(1, "ACADEMY DINOSAUR");
            FilmDTO.Film film2 = new FilmDTO.Film(2, "ACE GOLDFINGER");
            List<FilmDTO.Film> films = List.of(film1, film2);
            Map<String, Object> filter = Map.of("startsWith", "A");

            // When
            FilmDTO filmDTO = new FilmDTO(films, 2, filter);

            // Then
            assertThat(filmDTO.films()).hasSize(2);
            assertThat(filmDTO.films()).containsExactly(film1, film2);
            assertThat(filmDTO.count()).isEqualTo(2);
            assertThat(filmDTO.filter()).containsEntry("startsWith", "A");
        }

        @Test
        @DisplayName("Should create FilmDTO with null filter")
        void shouldCreateFilmDTOWithNullFilter() {
            // Given
            List<FilmDTO.Film> films = List.of(new FilmDTO.Film(1, "TEST FILM"));

            // When
            FilmDTO filmDTO = new FilmDTO(films, 1, null);

            // Then
            assertThat(filmDTO.films()).hasSize(1);
            assertThat(filmDTO.count()).isEqualTo(1);
            assertThat(filmDTO.filter()).isNull();
        }

        @Test
        @DisplayName("Should create FilmDTO with empty films list")
        void shouldCreateFilmDTOWithEmptyFilmsList() {
            // Given
            List<FilmDTO.Film> emptyFilms = List.of();
            Map<String, Object> filter = Map.of("startsWith", "Z");

            // When
            FilmDTO filmDTO = new FilmDTO(emptyFilms, 0, filter);

            // Then
            assertThat(filmDTO.films()).isEmpty();
            assertThat(filmDTO.count()).isZero();
            assertThat(filmDTO.filter()).containsEntry("startsWith", "Z");
        }
    }

    @Nested
    @DisplayName("Film Inner Record Tests")
    class FilmInnerRecordTests {

        @Test
        @DisplayName("Should create Film record with valid data")
        void shouldCreateFilmRecordWithValidData() {
            // When
            FilmDTO.Film film = new FilmDTO.Film(1, "ACADEMY DINOSAUR");

            // Then
            assertThat(film.filmId()).isEqualTo(1);
            assertThat(film.title()).isEqualTo("ACADEMY DINOSAUR");
        }

        @Test
        @DisplayName("Should create Film record with null values")
        void shouldCreateFilmRecordWithNullValues() {
            // When
            FilmDTO.Film film = new FilmDTO.Film(null, null);

            // Then
            assertThat(film.filmId()).isNull();
            assertThat(film.title()).isNull();
        }

        @Test
        @DisplayName("Should create Film from entity using fromEntity factory method")
        void shouldCreateFilmFromEntityUsingFactory() {
            // Given
            Film entity = new Film(1, "ACADEMY DINOSAUR");

            // When
            FilmDTO.Film film = FilmDTO.Film.fromEntity(entity);

            // Then
            assertThat(film.filmId()).isEqualTo(1);
            assertThat(film.title()).isEqualTo("ACADEMY DINOSAUR");
        }

        @Test
        @DisplayName("Should create Film from entity with null values")
        void shouldCreateFilmFromEntityWithNullValues() {
            // Given
            Film entity = new Film(null, null);

            // When
            FilmDTO.Film film = FilmDTO.Film.fromEntity(entity);

            // Then
            assertThat(film.filmId()).isNull();
            assertThat(film.title()).isNull();
        }

        @Test
        @DisplayName("Should convert Film to Map using toMap method")
        void shouldConvertFilmToMapUsingToMap() {
            // Given
            FilmDTO.Film film = new FilmDTO.Film(1, "ACADEMY DINOSAUR");

            // When
            Map<String, Object> map = film.toMap();

            // Then
            assertThat(map).hasSize(2);
            assertThat(map.get("film_id")).isEqualTo(1);
            assertThat(map.get("title")).isEqualTo("ACADEMY DINOSAUR");
        }

        @Test
        @DisplayName("Should convert Film with null values to Map")
        void shouldConvertFilmWithNullValuesToMap() {
            // Given
            FilmDTO.Film film = new FilmDTO.Film(null, null);

            // When
            Map<String, Object> map = film.toMap();

            // Then
            assertThat(map).hasSize(2);
            assertThat(map.get("film_id")).isNull();
            assertThat(map.get("title")).isNull();
        }
    }

    @Nested
    @DisplayName("FilmDTO Factory Methods Tests")
    class FilmDTOFactoryMethodsTests {

        @Test
        @DisplayName("Should create FilmDTO from entities using fromEntities factory method")
        void shouldCreateFilmDTOFromEntitiesUsingFactory() {
            // Given
            Film entity1 = new Film(1, "ACADEMY DINOSAUR");
            Film entity2 = new Film(2, "ACE GOLDFINGER");
            List<Film> entities = List.of(entity1, entity2);
            Map<String, Object> filterMap = Map.of("startsWith", "A");

            // When
            FilmDTO filmDTO = FilmDTO.fromEntities(entities, filterMap);

            // Then
            assertThat(filmDTO.films()).hasSize(2);
            assertThat(filmDTO.films().get(0).filmId()).isEqualTo(1);
            assertThat(filmDTO.films().get(0).title()).isEqualTo("ACADEMY DINOSAUR");
            assertThat(filmDTO.films().get(1).filmId()).isEqualTo(2);
            assertThat(filmDTO.films().get(1).title()).isEqualTo("ACE GOLDFINGER");
            assertThat(filmDTO.count()).isEqualTo(2);
            assertThat(filmDTO.filter()).containsEntry("startsWith", "A");
        }

        @Test
        @DisplayName("Should create FilmDTO from empty entities list")
        void shouldCreateFilmDTOFromEmptyEntitiesList() {
            // Given
            List<Film> entities = List.of();
            Map<String, Object> filterMap = Map.of("startsWith", "Z");

            // When
            FilmDTO filmDTO = FilmDTO.fromEntities(entities, filterMap);

            // Then
            assertThat(filmDTO.films()).isEmpty();
            assertThat(filmDTO.count()).isZero();
            assertThat(filmDTO.filter()).containsEntry("startsWith", "Z");
        }

        @Test
        @DisplayName("Should create FilmDTO from entities with null filter")
        void shouldCreateFilmDTOFromEntitiesWithNullFilter() {
            // Given
            Film entity = new Film(1, "TEST FILM");
            List<Film> entities = List.of(entity);

            // When
            FilmDTO filmDTO = FilmDTO.fromEntities(entities, null);

            // Then
            assertThat(filmDTO.films()).hasSize(1);
            assertThat(filmDTO.count()).isEqualTo(1);
            assertThat(filmDTO.filter()).isNull();
        }

        @Test
        @DisplayName("Should create FilmDTO from Maps using fromMaps factory method")
        void shouldCreateFilmDTOFromMapsUsingFactory() {
            // Given
            Map<String, Object> map1 = Map.of("film_id", 1, "title", "ACADEMY DINOSAUR");
            Map<String, Object> map2 = Map.of("film_id", 2, "title", "ACE GOLDFINGER");
            List<Map<String, Object>> filmMaps = List.of(map1, map2);
            Map<String, Object> filterMap = Map.of("startsWith", "A");

            // When
            FilmDTO filmDTO = FilmDTO.fromMaps(filmMaps, filterMap);

            // Then
            assertThat(filmDTO.films()).hasSize(2);
            assertThat(filmDTO.films().get(0).filmId()).isEqualTo(1);
            assertThat(filmDTO.films().get(0).title()).isEqualTo("ACADEMY DINOSAUR");
            assertThat(filmDTO.films().get(1).filmId()).isEqualTo(2);
            assertThat(filmDTO.films().get(1).title()).isEqualTo("ACE GOLDFINGER");
            assertThat(filmDTO.count()).isEqualTo(2);
            assertThat(filmDTO.filter()).containsEntry("startsWith", "A");
        }

        @Test
        @DisplayName("Should create FilmDTO from empty Maps list")
        void shouldCreateFilmDTOFromEmptyMapsList() {
            // Given
            List<Map<String, Object>> filmMaps = List.of();
            Map<String, Object> filterMap = Map.of("startsWith", "Z");

            // When
            FilmDTO filmDTO = FilmDTO.fromMaps(filmMaps, filterMap);

            // Then
            assertThat(filmDTO.films()).isEmpty();
            assertThat(filmDTO.count()).isZero();
            assertThat(filmDTO.filter()).containsEntry("startsWith", "Z");
        }

        @Test
        @DisplayName("Should create FilmDTO from Maps with null values")
        void shouldCreateFilmDTOFromMapsWithNullValues() {
            // Given
            Map<String, Object> map = Map.of("film_id", 1, "title", "TEST FILM");
            List<Map<String, Object>> filmMaps = List.of(map);

            // When
            FilmDTO filmDTO = FilmDTO.fromMaps(filmMaps, null);

            // Then
            assertThat(filmDTO.films()).hasSize(1);
            assertThat(filmDTO.films().get(0).filmId()).isEqualTo(1);
            assertThat(filmDTO.films().get(0).title()).isEqualTo("TEST FILM");
            assertThat(filmDTO.count()).isEqualTo(1);
            assertThat(filmDTO.filter()).isNull();
        }

        @Test
        @DisplayName("Should handle Maps with null film_id and title")
        void shouldHandleMapsWithNullFilmIdAndTitle() {
            // Given
            Map<String, Object> map1 = Map.of("film_id", 1, "title", "VALID FILM");
            Map<String, Object> map2 = new java.util.HashMap<>();
            map2.put("film_id", null);
            map2.put("title", null);
            List<Map<String, Object>> filmMaps = List.of(map1, map2);
            Map<String, Object> filterMap = Map.of("includeNull", true);

            // When
            FilmDTO filmDTO = FilmDTO.fromMaps(filmMaps, filterMap);

            // Then
            assertThat(filmDTO.films()).hasSize(2);
            assertThat(filmDTO.films().get(0).filmId()).isEqualTo(1);
            assertThat(filmDTO.films().get(0).title()).isEqualTo("VALID FILM");
            assertThat(filmDTO.films().get(1).filmId()).isNull();
            assertThat(filmDTO.films().get(1).title()).isNull();
            assertThat(filmDTO.count()).isEqualTo(2);
            assertThat(filmDTO.filter()).containsEntry("includeNull", true);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should maintain data consistency through entity->DTO->Map conversion")
        void shouldMaintainDataConsistencyThroughEntityDtoMapConversion() {
            // Given
            Film originalEntity = new Film(42, "LIFE UNIVERSE");
            List<Film> entities = List.of(originalEntity);
            Map<String, Object> filter = Map.of("search", "universe");

            // When
            FilmDTO filmDTO = FilmDTO.fromEntities(entities, filter);
            Map<String, Object> convertedMap = filmDTO.films().get(0).toMap();

            // Then
            assertThat(convertedMap.get("film_id")).isEqualTo(originalEntity.filmId());
            assertThat(convertedMap.get("title")).isEqualTo(originalEntity.title());
            assertThat(filmDTO.count()).isEqualTo(1);
            assertThat(filmDTO.filter()).isEqualTo(filter);
        }

        @Test
        @DisplayName("Should maintain data consistency through Map->DTO->Map conversion")
        void shouldMaintainDataConsistencyThroughMapDtoMapConversion() {
            // Given
            Map<String, Object> originalMap = Map.of("film_id", 99, "title", "ORIGINAL TITLE");
            List<Map<String, Object>> filmMaps = List.of(originalMap);
            Map<String, Object> filter = Map.of("category", "action");

            // When
            FilmDTO filmDTO = FilmDTO.fromMaps(filmMaps, filter);
            Map<String, Object> convertedMap = filmDTO.films().get(0).toMap();

            // Then
            assertThat(convertedMap.get("film_id")).isEqualTo(originalMap.get("film_id"));
            assertThat(convertedMap.get("title")).isEqualTo(originalMap.get("title"));
            assertThat(filmDTO.count()).isEqualTo(1);
            assertThat(filmDTO.filter()).isEqualTo(filter);
        }
    }
}
