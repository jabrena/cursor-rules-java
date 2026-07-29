package info.jab.pml;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Agent Skill Propagation Tests")
class AgentSkillPropagationTest {

    @Test
    @DisplayName("Generated 005 must ship agent assets and reference them without embedding bodies")
    void should_shipAgentAssets_when_agentsInstallationSkillIsGenerated() {
        SkillsGenerator generator = new SkillsGenerator();
        SkillsGenerator.SkillOutput output = generator.generateSkill("005-agents-installation", true, true);

        String reference = output.referenceMds().get("005-agents-installation");
        assertThat(reference)
            .contains("assets/agents/")
            .contains("](../assets/agents/plinth-business-analyst.md)")
            .contains("](../assets/agents/plinth-java-spring-boot-coder.md)")
            .doesNotContain("robot" + "-")
            .doesNotContain("name: plinth-business-analyst")
            .doesNotContain("name: plinth-java-spring-boot-coder");

        AgentIndexes.agentFiles().forEach(agentFile -> {
            String assetPath = "assets/agents/" + agentFile;
            String agentName = agentFile.substring(0, agentFile.length() - ".md".length());

            assertThat(output.resourceFiles())
                .withFailMessage("Generated 005 missing asset %s", assetPath)
                .containsKey(assetPath);
            assertThat(loadBridgedAgent(agentFile))
                .withFailMessage("Generated 005 asset uses a legacy agent identifier: %s", assetPath)
                .doesNotContain("robot" + "-");
            assertThat(reference)
                .withFailMessage("Generated 005 reference missing link to %s", assetPath)
                .contains("](../" + assetPath + ")");
            assertThat(reference)
                .withFailMessage("Generated 005 reference must not embed agent %s", agentName)
                .doesNotContain(loadBridgedAgent(agentFile));
        });
    }

    @Test
    @DisplayName("Generated 002 must ship inventory template asset and reference it without embedding content")
    void should_shipInventoryTemplateAsset_when_agentsInventorySkillIsGenerated() {
        SkillsGenerator generator = new SkillsGenerator();
        SkillsGenerator.SkillOutput output = generator.generateSkill("002-agents-inventory", true, true);

        String reference = output.referenceMds().get("002-agents-inventory");
        String templateAssetPath = "assets/java-agents-inventory-template.md";
        String template = output.resourceFiles().get(templateAssetPath);

        assertThat(output.resourceFiles())
            .withFailMessage("Generated 002 missing asset %s", templateAssetPath)
            .containsEntry(templateAssetPath, template);
        assertThat(reference)
            .contains("](../" + templateAssetPath + ")")
            .doesNotContain("robot" + "-")
            .doesNotContain("# Embedded Agents Inventory");
        assertThat(template).doesNotContain("robot" + "-");

        AgentIndexes.agentFiles().forEach(agentFile -> {
            String agentName = agentFile.substring(0, agentFile.length() - ".md".length());
            assertThat(template)
                .withFailMessage("Generated 002 template asset missing agent `%s`", agentName)
                .contains("`" + agentName + "`");
            assertThat(reference)
                .withFailMessage("Generated 002 reference must not embed agent `%s`", agentName)
                .doesNotContain("`" + agentName + "`");
        });
    }

    private String loadBridgedAgent(String agentFile) {
        return loadBridgedAsset("skill-references/assets/agents/" + agentFile);
    }

    private String loadBridgedAsset(String resourceName) {
        try (var stream = AgentSkillPropagationTest.class.getClassLoader().getResourceAsStream(resourceName)) {
            assertThat(stream)
                .withFailMessage("Bridged asset not found: %s", resourceName)
                .isNotNull();
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load bridged asset: " + resourceName, e);
        }
    }
}
