package info.jab.pml;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Command Bridge Tests")
class CommandBridgeTest {

    @Test
    @DisplayName("Bridged command assets must match plinth-commands-generator manifest")
    void should_stageAllCommandAssets_when_generateResourcesBridgeRuns() {
        assertThat(CommandIndexes.commandFiles().toList())
            .as("Onboarding command propagation order")
            .startsWith("onboarding.md", "update-issue.md");

        CommandIndexes.commandFiles().forEach(commandFile -> {
            String resource = "skill-references/assets/commands/" + commandFile;
            String bridged = loadResource(resource);
            String generated = loadResource("commands/" + commandFile);

            assertThat(bridged)
                .withFailMessage("Bridged command asset must exactly match generated output: %s", resource)
                .startsWith("---\n")
                .contains("\ntools:\n")
                .contains("\n---\n\n# ")
                .isEqualTo(generated);
        });
    }

    @Test
    @DisplayName("Bridged inventory template must be available for skill generation")
    void should_stageInventoryTemplate_when_generateResourcesBridgeRuns() {
        assertThat(getTestResource("skill-references/assets/java-commands-inventory-template.md"))
            .isNotNull();
    }

    private InputStream getTestResource(String resourceName) {
        return CommandBridgeTest.class.getClassLoader().getResourceAsStream(resourceName);
    }

    private String loadResource(String resourceName) {
        try (var stream = getTestResource(resourceName)) {
            assertThat(stream)
                .withFailMessage("Resource not found: %s", resourceName)
                .isNotNull();
            return new String(Objects.requireNonNull(stream).readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load resource: " + resourceName, e);
        }
    }
}
