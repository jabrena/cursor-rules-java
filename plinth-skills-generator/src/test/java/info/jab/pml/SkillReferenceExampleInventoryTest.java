package info.jab.pml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Skill reference example inventory")
class SkillReferenceExampleInventoryTest {

    private static final int MINIMUM_EXAMPLE_COUNT = 3;

    /**
     * Workflow, catalogue, scaffold, and tooling skills are not required to carry
     * code-heavy {@code <example>} blocks in every reference.
     */
    private static final Set<String> EXEMPT_SKILL_IDS = Set.of(
        "001-commands-inventory",
        "002-agents-inventory",
        "003-skills-inventory",
        "004-commands-installation",
        "005-agents-installation",
        "012-agile-epic",
        "013-agile-feature",
        "014-agile-user-story",
        "030-architecture-adr-general",
        "033-architecture-diagrams",
        "041-planning-plan-mode",
        "042-planning-openspec",
        "043-planning-github-issues",
        "044-planning-jira",
        "045-planning-azure-devops",
        "051-design-two-steps-methods",
        "052-design-hamburger-method",
        "053-design-simple-rules",
        "054-design-tdd",
        "111-java-maven-dependencies",
        "112-java-maven-plugins",
        "113-java-maven-documentation",
        "114-java-maven-search",
        "151-java-performance-jmeter",
        "152-java-performance-gatling",
        "161-java-profiling-detect",
        "162-java-profiling-analyze",
        "163-java-profiling-refactor",
        "164-java-profiling-verify",
        "170-java-documentation",
        "200-agents-md",
        "703-technologies-fuzzing-testing",
        "300-frameworks-spring-boot-create-project",
        "400-frameworks-quarkus-create-project",
        "500-frameworks-micronaut-create-project"
    );

    @Test
    @DisplayName("Should require minimum examples for every skill reference")
    void should_requireMinimumExamplesForEverySkillReference() throws Exception {
        List<ReferenceExampleCount> counts = SkillIndexes.skillDescriptors()
            .flatMap(descriptor -> descriptor.references().stream()
                .map(reference -> countExamples(descriptor.skillId(), reference)))
            .sorted(Comparator
                .comparing(ReferenceExampleCount::skillId)
                .thenComparing(ReferenceExampleCount::reference))
            .toList();

        assertThat(counts).isNotEmpty();

        List<ReferenceExampleCount> belowMinimumExampleCount = counts.stream()
            .filter(count -> count.examples() < MINIMUM_EXAMPLE_COUNT)
            .filter(count -> !isExemptFromMinimumExampleCount(count.skillId(), count.reference()))
            .toList();

        assertThat(belowMinimumExampleCount)
            .withFailMessage(
                "Found %d reference(s) with fewer than %d examples:%s",
                belowMinimumExampleCount.size(),
                MINIMUM_EXAMPLE_COUNT,
                formatBelowMinimumExampleCount(belowMinimumExampleCount)
            )
            .isEmpty();
    }

    private ReferenceExampleCount countExamples(String skillId, String reference) {
        String resourceName = "skill-references/" + reference + ".xml";
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            assertThat(stream)
                .withFailMessage("System-prompt XML not found: %s", resourceName)
                .isNotNull();

            Document document = parseXml(stream, resourceName);
            int examples = document.getElementsByTagName("example").getLength();
            return new ReferenceExampleCount(skillId, reference, examples);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + resourceName, e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse " + resourceName, e);
        }
    }

    private Document parseXml(InputStream stream, String resourceName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource inputSource = new InputSource(stream);
        inputSource.setSystemId(resolveBaseUri(resourceName));
        return builder.parse(inputSource);
    }

    private String resolveBaseUri(String resourceName) {
        URL resourceUrl = getClass().getClassLoader().getResource(resourceName);
        if (resourceUrl == null) {
            return "";
        }
        String url = resourceUrl.toString();
        int lastSlash = url.lastIndexOf('/');
        return lastSlash > 0 ? url.substring(0, lastSlash + 1) : url;
    }

    private boolean isExemptFromMinimumExampleCount(String skillId, String reference) {
        if (EXEMPT_SKILL_IDS.contains(skillId)) {
            return true;
        }
        return reference.endsWith("-chapters-summary")
            || reference.endsWith("-parallel-change");
    }

    private String formatBelowMinimumExampleCount(List<ReferenceExampleCount> counts) {
        if (counts.isEmpty()) {
            return "";
        }

        return System.lineSeparator()
            + counts.stream()
                .sorted(Comparator
                    .comparingInt(ReferenceExampleCount::examples)
                    .thenComparing(ReferenceExampleCount::skillId)
                    .thenComparing(ReferenceExampleCount::reference))
                .map(count -> " - " + count.skillId() + " / " + count.reference())
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private record ReferenceExampleCount(String skillId, String reference, int examples) {}
}
