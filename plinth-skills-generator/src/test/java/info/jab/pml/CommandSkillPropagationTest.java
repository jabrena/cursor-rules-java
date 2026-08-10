package info.jab.pml;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Command Skill Propagation Tests")
class CommandSkillPropagationTest {

    @Test
    @DisplayName("Generated 004 must ship command assets and reference them without embedding bodies")
    void should_shipCommandAssets_when_commandsInstallationSkillIsGenerated() {
        SkillsGenerator generator = new SkillsGenerator();
        SkillsGenerator.SkillOutput output = generator.generateSkill("004-commands-installation", true, true);

        String reference = output.referenceMds().get("004-commands-installation");
        assertThat(reference)
            .contains("assets/commands/")
            .contains("](../assets/commands/onboarding.md)")
            .contains("](../assets/commands/update-issue.md)")
            .contains("](../assets/commands/create-acceptance-criteria.md)")
            .contains("](../assets/commands/benchmark.md)")
            .doesNotContain("/update-issue <issue>")
            .doesNotContain("# update-issue");

        CommandIndexes.commandFiles().forEach(commandFile -> {
            String assetPath = "assets/commands/" + commandFile;
            String commandName = commandFile.substring(0, commandFile.length() - ".md".length());

            assertThat(output.resourceFiles())
                .withFailMessage("Generated 004 missing asset %s", assetPath)
                .containsEntry(assetPath, loadBridgedCommand(commandFile));
            assertThat(output.resourceFiles().get(assetPath))
                .startsWith("---\n")
                .contains("\ntools:\n")
                .contains("\n---\n\n# " + commandName + "\n");
            assertThat(reference)
                .withFailMessage("Generated 004 reference missing link to %s", assetPath)
                .contains("](../" + assetPath + ")");
            assertThat(reference)
                .withFailMessage("Generated 004 reference must not embed command /%s", commandName)
                .doesNotContain(loadBridgedCommand(commandFile));
        });
    }

    @Test
    @DisplayName("Generated 001 must ship inventory template asset and reference it without embedding content")
    void should_shipInventoryTemplateAsset_when_commandsInventorySkillIsGenerated() {
        SkillsGenerator generator = new SkillsGenerator();
        SkillsGenerator.SkillOutput output = generator.generateSkill("001-commands-inventory", true, true);

        String reference = output.referenceMds().get("001-commands-inventory");
        String templateAssetPath = "assets/java-commands-inventory-template.md";
        String template = Objects.requireNonNull(
            output.resourceFiles().get(templateAssetPath),
            "Generated 001 inventory template must exist"
        );

        assertThat(output.resourceFiles())
            .withFailMessage("Generated 001 missing asset %s", templateAssetPath)
            .containsEntry(templateAssetPath, template);
        assertThat(reference)
            .contains("](../" + templateAssetPath + ")")
            .doesNotContain("# Embedded Commands Inventory");

        CommandIndexes.commandFiles().forEach(commandFile -> {
            String commandName = commandFile.substring(0, commandFile.length() - ".md".length());
            assertThat(template)
                .withFailMessage("Generated 001 template asset missing command /%s", commandName)
                .contains("`/" + commandName + "`");
            assertThat(reference)
                .withFailMessage("Generated 001 reference must not embed command /%s", commandName)
                .doesNotContain("`/" + commandName + "`");
        });

    }

    private String loadBridgedCommand(String commandFile) {
        return loadBridgedAsset("skill-references/assets/commands/" + commandFile);
    }

    private String loadBridgedAsset(String resourceName) {
        try (var stream = CommandSkillPropagationTest.class.getClassLoader().getResourceAsStream(resourceName)) {
            assertThat(stream)
                .withFailMessage("Bridged asset not found: %s", resourceName)
                .isNotNull();
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load bridged asset: " + resourceName, e);
        }
    }
}
