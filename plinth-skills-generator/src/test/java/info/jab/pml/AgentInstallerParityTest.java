package info.jab.pml;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Agent Installer Parity Tests")
class AgentInstallerParityTest {

    private static final List<String> CODER_AGENTS = List.of(
        "plinth-java-coder.md",
        "plinth-java-spring-boot-coder.md",
        "plinth-java-quarkus-coder.md",
        "plinth-java-micronaut-coder.md"
    );

    @Test
    @DisplayName("Agent installer resources must match the manifest bundle")
    void should_includeExactAgents_when_agentInstallerRegistersResources() {
        List<String> agentAssets = SkillIndexes.skillDescriptors()
            .filter(descriptor -> "005-agents-installation".equals(descriptor.skillId()))
            .findFirst()
            .orElseThrow()
            .resources()
            .stream()
            .map(SkillIndexes.SkillResource::targetPath)
            .toList();

        assertThat(agentAssets)
            .containsExactlyElementsOf(AgentIndexes.agentFiles()
                .map(agentFile -> "assets/agents/" + agentFile)
                .toList());
    }

    @Test
    @DisplayName("Installer provides architect and tech lead without coordinator")
    void should_installRenamedAnalysisDesignAgents_withoutCoordinatorAlias() {
        String installer = loadClasspathResource("skill-references/005-agents-installation.xml");

        assertThat(installer)
            .contains("](../assets/agents/plinth-architect.md)")
            .contains("](../assets/agents/plinth-tech-lead.md)")
            .contains("](../assets/agents/plinth-no-java.md)")
            .contains("](../assets/agents/plinth-java-performance.md)")
            .doesNotContain("robot-coordinator.md");
    }

    @Test
    @DisplayName("Installer preserves all framework coder routes")
    void should_referenceAllCoderAgents_when_techLeadCoordinatesDelivery() {
        String installer = loadClasspathResource("skill-references/005-agents-installation.xml");

        CODER_AGENTS.forEach(coderAgent -> assertThat(installer).contains(coderAgent));
    }

    @Test
    @DisplayName("Installer must reference the nine-agent bundle")
    void should_referenceNineAgentBundle_when_agentInstallerIsDefined() {
        String installer = loadClasspathResource("skill-references/005-agents-installation.xml");

        assertThat(installer)
            .contains("plinth-no-java.md")
            .contains("all nine agents")
            .contains("assets/agents/");
    }

    private String loadClasspathResource(String resourceName) {
        try (var stream = AgentInstallerParityTest.class.getClassLoader().getResourceAsStream(resourceName)) {
            assertThat(stream)
                .withFailMessage("Resource not found: %s", resourceName)
                .isNotNull();
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load classpath resource: " + resourceName, e);
        }
    }
}
