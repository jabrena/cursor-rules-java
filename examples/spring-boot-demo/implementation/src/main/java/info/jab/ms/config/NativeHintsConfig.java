package info.jab.ms.config;

import info.jab.ms.controller.FilmDTO;
import info.jab.ms.repository.Film;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * Native Hints Configuration for GraalVM Native Image Compilation
 *
 * This configuration class provides runtime hints for reflection, serialization,
 * and resources needed for proper native image compilation with Spring Boot.
 *
 * Following Spring Boot Native Compilation best practices:
 * - Explicit reflection registration for data classes
 * - Resource pattern registration
 * - Serialization hints for JSON processing
 */
@Configuration
@RegisterReflectionForBinding({
    Film.class,
    FilmDTO.class,
    FilmDTO.Film.class
})
@ImportRuntimeHints(NativeHintsConfig.ApplicationRuntimeHints.class)
public class NativeHintsConfig {

    /**
     * Custom runtime hints registrar for application-specific native image requirements
     */
    static class ApplicationRuntimeHints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // Register reflection hints for key application classes
            hints.reflection()
                .registerType(Film.class,
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.DECLARED_FIELDS)
                .registerType(FilmDTO.class,
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.DECLARED_FIELDS)
                .registerType(FilmDTO.Film.class,
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.DECLARED_FIELDS);

            // Register resource patterns for configuration files
            hints.resources()
                .registerPattern("application*.yaml")
                .registerPattern("application*.yml")
                .registerPattern("application*.properties")
                .registerPattern("META-INF/spring.factories")
                .registerPattern("META-INF/spring-configuration-metadata.json")
                .registerPattern("static/**")
                .registerPattern("templates/**")
                .registerPattern("*.sql");

            // Register proxy interfaces for Spring Data repositories
            hints.proxies()
                .registerJdkProxy(info.jab.ms.repository.FilmRepository.class);
        }
    }
}
