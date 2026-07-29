package info.jab.pml;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Agent Index Tests")
class AgentIndexesTest {

    private static final List<String> CODER_AGENTS = List.of(
        "plinth-java-coder.md",
        "plinth-java-spring-boot-coder.md",
        "plinth-java-quarkus-coder.md",
        "plinth-java-micronaut-coder.md"
    );

    private static final List<String> IMPLEMENTATION_AGENTS = List.of(
        "plinth-java-coder.md",
        "plinth-java-spring-boot-coder.md",
        "plinth-java-quarkus-coder.md",
        "plinth-java-micronaut-coder.md",
        "plinth-no-java.md"
    );

    @Test
    @DisplayName("Agent inventory XML must list XML sources and map to Markdown assets")
    void should_loadAgentFiles_when_agentInventoryIsParsed() {
        List<String> sources = AgentIndexes.agentSources().toList();
        List<String> agentFiles = expectedAgentFiles();

        assertThat(sources)
            .isNotEmpty()
            .allSatisfy(source -> assertThat(source).endsWith(".xml"));
        assertThat(agentFiles)
            .hasSize(sources.size())
            .allSatisfy(agentFile -> assertThat(agentFile).endsWith(".md"));
        assertThat(agentFiles)
            .containsExactlyElementsOf(
                sources.stream().map(AgentIndexes::toMarkdownFileName).toList()
            );
        assertThat(new HashSet<>(agentFiles))
            .withFailMessage("Agent inventory must not contain duplicate agent files")
            .hasSize(agentFiles.size());
    }

    @Test
    @DisplayName("Agent assets must include the complete generated Markdown bundle")
    void should_haveCompleteAgentAssets_when_agentBundleIsInstalled() {
        expectedAgentFiles().forEach(agentFile -> {
            String resource = "agents/" + agentFile;
            assertThat(getTestResource(resource))
                .withFailMessage("Agent asset missing: %s", resource)
                .isNotNull();
        });
        AgentIndexes.agentSources().forEach(sourceFile -> {
            String resource = "agents/" + sourceFile;
            assertThat(getTestResource(resource))
                .withFailMessage("Agent XML source missing: %s", resource)
                .isNotNull();
        });
    }

    @Test
    @DisplayName("Classpath Markdown must match generation from inventory XML sources")
    void should_matchGeneratedMarkdown_when_agentXmlIsSourceOfTruth() throws Exception {
        for (String sourceFile : AgentIndexes.agentSources().toList()) {
            String markdownFile = AgentIndexes.toMarkdownFileName(sourceFile);
            Document document = InventoryXmlLoader.parse(requireResource("agents/" + sourceFile));
            String rendered = AgentMarkdownRenderer.render(document);
            String classpathMarkdown = loadClasspathResource("agents/" + markdownFile);

            assertThat(classpathMarkdown)
                .withFailMessage("Generated Markdown parity failed for %s", sourceFile)
                .isEqualTo(rendered);
        }
    }

    @Test
    @DisplayName("Agent inventory template must list the exact agent bundle")
    void should_listExactAgents_when_inventoryTemplateIsGenerated() {
        String inventory = loadClasspathResource("java-agents-inventory-template.md");

        expectedAgentFiles().stream()
            .map(agentFile -> agentFile.substring(0, agentFile.length() - ".md".length()))
            .map(agentName -> "`" + agentName + "`")
            .forEach(agent -> assertThat(inventory).contains(agent));

        long agentRows = inventory.lines()
            .filter(line -> line.startsWith("| `plinth-"))
            .count();
        assertThat(agentRows).isEqualTo(expectedAgentFiles().size());
        assertThat(inventory).doesNotContain("robot" + "-");
    }

    @Test
    @DisplayName("Inventory provides architect and tech lead without coordinator")
    void should_installRenamedAnalysisDesignAgents_withoutCoordinatorAlias() {
        String inventory = loadClasspathResource("java-agents-inventory-template.md");

        assertThat(inventory)
            .contains("`plinth-architect`")
            .contains("`plinth-tech-lead`")
            .contains("`plinth-no-java`")
            .contains("`plinth-java-performance`")
            .doesNotContain("`robot-coordinator`");
        assertThat(getTestResource("agents/robot-coordinator.md"))
            .isNull();
    }

    @Test
    @DisplayName("Tech lead preserves all framework coder routes")
    void should_referenceAllCoderAgents_when_techLeadCoordinatesDelivery() {
        String techLead = loadClasspathResource("agents/plinth-tech-lead.md");

        CODER_AGENTS.forEach(coderAgent -> assertThat(techLead).contains(coderAgent));
        assertThat(techLead)
            .contains("Keep artifact authority explicit")
            .contains("OpenSpec specs own requirements")
            .contains("the selected task list owns execution tracking")
            .contains("**MUST** delegate **every** implementation, test, and verification step")
            .contains("If you catch yourself about to write or patch application code, **stop** and delegate instead")
            .contains("Skill discovery before delegation")
            .contains("The implementation coder owns final framework-specific discovery")
            .contains("still with **no** direct implementation by you");
    }

    @Test
    @DisplayName("Tech lead must own selected-scope OpenSpec readiness and remediation")
    void should_stopDelivery_when_selectedOpenSpecScopeIsNotReady() {
        String techLead = loadClasspathResource("agents/plinth-tech-lead.md");

        assertThat(techLead)
            .contains("OpenSpec readiness ownership")
            .contains("selected execution scope")
            .contains("bidirectional traceability")
            .contains("absent, ambiguous, placeholder, completed-only, partial, or divergent")
            .contains("report each unsupported scenario or task")
            .contains("update the OpenSpec change and rerun delivery")
            .contains("before location setup, skill discovery, or implementation delegation")
            .contains("must not invent acceptance criteria or tasks");
    }

    @Test
    @DisplayName("Tech lead must resolve OpenSpec implementation location by explicit precedence")
    void should_resolveLocationByPrecedence_when_openSpecReadinessPasses() {
        String techLead = loadClasspathResource("agents/plinth-tech-lead.md");

        assertThat(techLead)
            .contains("Implementation location precedence")
            .contains("invocation constraints take precedence")
            .contains("## Implementation Location")
            .contains("Strategy: `main` | `feature-branch` | `worktree`")
            .contains("missing, blank, or unsupported `Strategy`")
            .contains("ask the contributor to choose `main`, a feature branch, or a worktree")
            .contains("wait for the answer before location setup, skill discovery, or implementation delegation")
            .contains("separate explicit approval");
    }

    @Test
    @DisplayName("Tech lead must route non-Java work to default non-Java agent")
    void should_routeNonJavaWork_when_executionArtifactIsNotJava() {
        String techLead = loadClasspathResource("agents/plinth-tech-lead.md");
        String noJavaAgent = loadClasspathResource("agents/plinth-no-java.md");

        assertThat(techLead)
            .contains("no Java, Maven, or JVM implementation scope")
            .contains("| Plain Java, Maven/JVM work, Java CLI-only work, or Java framework-neutral requirements | [@plinth-java-coder](plinth-java-coder.md) |")
            .contains("| Explicit non-Java stack, no Java/JVM implementation scope, or no Java evidence in the selected issue/plan/spec | [@plinth-no-java](plinth-no-java.md) |")
            .contains("Prefer **plinth-no-java** when the selected issue, plan, or OpenSpec tasks do not use Java");
        assertThat(noJavaAgent)
            .contains("name: plinth-no-java")
            .contains("does not use Java, Maven, or a JVM-based framework")
            .contains("If the task is actually plain Java or Maven work");
    }

    @Test
    @DisplayName("Java performance agent must coordinate profiling and benchmarks without direct implementation")
    void should_coordinatePerformanceWorkflows_when_javaPerformanceAgentIsInstalled() {
        String performanceAgent = loadClasspathResource("agents/plinth-java-performance.md");

        assertThat(performanceAgent)
            .contains("name: plinth-java-performance")
            .contains("`@161-java-profiling-detect`")
            .contains("`@162-java-profiling-analyze`")
            .contains("`@164-java-profiling-verify`")
            .contains("`@151-java-performance-jmeter`")
            .contains("`@152-java-performance-gatling`")
            .contains("JMH")
            .contains("You do not directly implement application-code optimizations")
            .contains("verified, inconclusive, or regressed");
    }

    @Test
    @DisplayName("Coder agents must share implementation skill precedence")
    void should_shareSkillPrecedence_when_coderAgentsImplementChanges() {
        CODER_AGENTS.forEach(coderAgent -> {
            String coder = loadClasspathResource("agents/" + coderAgent);

            assertThat(coder)
                .contains("Prefer `@143-java-functional-exception-handling`")
                .contains("Use `@126-java-exception-handling`")
                .contains("`@121-java-object-oriented-design`")
                .contains("`@122-java-type-design`")
                .contains("`@123-java-design-patterns`")
                .contains("`@142-java-functional-programming`")
                .contains("`@124-java-secure-coding`")
                .contains("`@701-technologies-openapi`")
                .contains("`@704-technologies-sql`")
                .contains("`@705-technologies-nosql-mongodb`")
                .contains("`@706-technologies-containers-docker`");
        });
    }

    @Test
    @DisplayName("Implementation agents must read task-relevant skill references")
    void should_requireTaskRelevantReferences_when_implementationAgentsApplySkills() {
        IMPLEMENTATION_AGENTS.forEach(implementationAgent -> {
            String agent = loadClasspathResource("agents/" + implementationAgent);

            assertThat(agent)
                .contains("Opening only `SKILL.md` is not sufficient")
                .contains("every task-relevant referenced resource")
                .contains("do not bulk-read unrelated references")
                .contains("`References read`")
                .contains("exact relative path");
        });

        assertThat(loadClasspathResource("agents/plinth-tech-lead.md"))
            .contains("Opening only a skill's `SKILL.md` is not sufficient")
            .contains("including you when reading a planning anchor")
            .contains("`references/042-planning-openspec.md`")
            .contains("Reference-reading requirement")
            .contains("exact skill ids and reference paths")
            .contains("Reject an applied-skill report that lists only `SKILL.md`");
    }

    @Test
    @DisplayName("Tech lead and coders must share bounded layered skill discovery")
    void should_shareBoundedSkillDiscovery_when_techLeadDelegatesToCoders() {
        String techLead = loadClasspathResource("agents/plinth-tech-lead.md");

        assertThat(techLead)
            .contains("`proposal.md`, `design.md`, and affected `specs/**/spec.md`")
            .contains("Skill catalog review")
            .contains("Do not recursively read every available `SKILL.md`")
            .contains("Hardcoded routing baseline")
            .contains("Record the artifact path and concern")
            .contains("The implementation coder owns final framework-specific discovery")
            .contains("Discovery evidence");

        IMPLEMENTATION_AGENTS.forEach(implementationAgent ->
            assertThat(loadClasspathResource("agents/" + implementationAgent))
                .contains("Discovery ownership")
                .contains("delegated candidate list is a baseline, not a ceiling")
                .contains("without broadening the approved scope")
        );
    }

    @Test
    @DisplayName("Framework coders must prefer JDBC for relational persistence")
    void should_preferJdbc_when_frameworkCoderSelectsRelationalPersistence() {
        assertThat(loadClasspathResource("agents/plinth-java-spring-boot-coder.md"))
            .contains("Prefer `@311-frameworks-spring-jdbc`")
            .contains("Use `@312-frameworks-spring-data-jdbc` only");
        assertThat(loadClasspathResource("agents/plinth-java-quarkus-coder.md"))
            .contains("Prefer `@411-frameworks-quarkus-jdbc`")
            .contains("Use `@412-frameworks-quarkus-panache` only");
        assertThat(loadClasspathResource("agents/plinth-java-micronaut-coder.md"))
            .contains("Prefer `@511-frameworks-micronaut-jdbc`")
            .contains("Use `@512-frameworks-micronaut-data` only");
    }

    private static List<String> expectedAgentFiles() {
        return AgentIndexes.agentFiles().toList();
    }

    private String loadClasspathResource(String resourceName) {
        try (InputStream stream = requireResource(resourceName)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load classpath resource: " + resourceName, e);
        }
    }

    private InputStream requireResource(String resourceName) {
        InputStream stream = getTestResource(resourceName);
        if (stream == null) {
            throw new IllegalArgumentException("Resource not found: " + resourceName);
        }
        return stream;
    }

    private InputStream getTestResource(String resourceName) {
        return AgentIndexesTest.class.getClassLoader().getResourceAsStream(resourceName);
    }
}
