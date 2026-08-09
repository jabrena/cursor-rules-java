package info.jab.pml;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Command Frontmatter Tests")
class CommandFrontmatterTest {

    private static final String FRONTMATTER_END = "---\n\n";
    private static final Map<String, String> LEGACY_BODY_SHA_256 = legacyBodyHashes();
    private static final Map<String, ExpectedMetadata> APPROVED_METADATA = approvedMetadata();

    @Test
    @DisplayName("Generated frontmatter must equal every inventoried XML metadata contract")
    void should_renderApprovedMetadata_when_allCommandsAreGenerated() throws Exception {
        for (String source : CommandIndexes.commandSources().toList()) {
            Document command = loadXml("commands/" + source);
            String markdown = CommandMarkdownRenderer.render(command);
            Map<String, Object> frontmatter = parseFrontmatter(markdown);
            Element metadata = (Element) command.getElementsByTagName("metadata").item(0);
            ExpectedMetadata expected = Objects.requireNonNull(
                APPROVED_METADATA.get(command.getDocumentElement().getAttribute("id")),
                "Approved metadata must exist for every command"
            );

            assertThat(frontmatter)
                .as("Frontmatter keys for %s", source)
                .containsOnlyKeys("description", "argument-hint", "model", "agent", "tools", "metadata");
            assertThat(frontmatter.get("metadata")).isEqualTo(Map.of(
                "author", String.join(", ", elements(metadata, "author")),
                "version", text(metadata, "version")
            ));
            assertThat(frontmatter.get("description")).isEqualTo(text(metadata, "description"));
            assertThat(frontmatter.get("argument-hint")).isEqualTo(text(metadata, "argument-hint"));
            assertThat(frontmatter.get("model")).isEqualTo(text(metadata, "model"));
            assertThat(frontmatter.get("agent")).isEqualTo(text(metadata, "agent"));
            assertThat(frontmatter.get("tools")).isEqualTo(elements(metadata, "tool"));
            assertThat(frontmatter.get("description")).isEqualTo(expected.description());
            assertThat(frontmatter.get("argument-hint")).isEqualTo(expected.argumentHint());
            assertThat(frontmatter.get("model")).isEqualTo("inherit");
            assertThat(frontmatter.get("agent")).isEqualTo(expected.agent());
            assertThat(frontmatter.get("tools")).isEqualTo(expected.tools());
        }
        assertThat(APPROVED_METADATA).hasSize(13);
    }

    @Test
    @DisplayName("YAML scalars must preserve punctuation, backslashes, apostrophes, and boundary spaces")
    void should_preserveYamlSignificantCharacters_when_frontmatterIsRendered() throws Exception {
        String xml = "<command id=\"yaml-boundaries\"><metadata>"
            + "<authors><author>First O'Author</author><author>Second Author</author></authors>"
            + "<version>0.19.0-SNAPSHOT</version>"
            + "<description>  Plan: team's #1 [work] \\ path  </description>"
            + "<argument-hint>[issue: 'value']</argument-hint><model>inherit</model><agent>inherit</agent>"
            + "<tools><tools-list><tool>Read</tool><tool>Custom: Tool #1</tool></tools-list></tools>"
            + "</metadata><goal>Goal</goal><steps/></command>";

        Map<String, Object> metadata = parseFrontmatter(CommandMarkdownRenderer.render(
            InventoryXmlLoader.parse(new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)))));

        assertThat(metadata.get("description")).isEqualTo("  Plan: team's #1 [work] \\ path  ");
        assertThat(metadata.get("argument-hint")).isEqualTo("[issue: 'value']");
        assertThat(metadata.get("tools")).isEqualTo(List.of("Read", "Custom: Tool #1"));
        assertThat(metadata.get("metadata")).isEqualTo(Map.of(
            "author", "First O'Author, Second Author",
            "version", "0.19.0-SNAPSHOT"
        ));
    }

    @Test
    @DisplayName("Frontmatter must not change any byte in the legacy Markdown body")
    void should_preserveLegacyBodyByteForByte_when_frontmatterIsPrepended() throws Exception {
        for (String source : CommandIndexes.commandSources().toList()) {
            String commandFile = CommandIndexes.toMarkdownFileName(source);
            String body = bodyAfterFrontmatter(CommandMarkdownRenderer.render(loadXml("commands/" + source)));

            assertThat(sha256(body))
                .as("Legacy Markdown body hash for %s", commandFile)
                .isEqualTo(LEGACY_BODY_SHA_256.get(commandFile));
        }
    }

    static Map<String, Object> parseFrontmatter(String markdown) {
        assertThat(markdown).startsWith("---\n");
        int end = markdown.indexOf(FRONTMATTER_END, 4);
        assertThat(end).isGreaterThan(3);
        String yaml = markdown.substring(4, end);
        Object loaded = new Load(LoadSettings.builder().build()).loadFromString(yaml);
        assertThat(loaded).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) loaded;
        return metadata;
    }

    static String bodyAfterFrontmatter(String markdown) {
        int end = markdown.indexOf(FRONTMATTER_END, 4);
        assertThat(end).isGreaterThan(3);
        return markdown.substring(end + FRONTMATTER_END.length());
    }

    private static Document loadXml(String resource) throws Exception {
        try (InputStream stream = CommandFrontmatterTest.class.getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IllegalStateException("Missing command resource: " + resource);
            }
            return InventoryXmlLoader.parse(stream);
        }
    }

    private static String text(Element parent, String name) {
        return parent.getElementsByTagName(name).item(0).getTextContent();
    }

    private static List<String> elements(Element parent, String name) {
        var nodes = parent.getElementsByTagName(name);
        return java.util.stream.IntStream.range(0, nodes.getLength())
            .mapToObj(index -> nodes.item(index).getTextContent())
            .toList();
    }

    private static String sha256(String value) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
            .digest(value.getBytes(StandardCharsets.UTF_8)));
    }

    private static Map<String, String> legacyBodyHashes() {
        Map<String, String> hashes = new LinkedHashMap<>();
        hashes.put("benchmark.md", "2b87f52d7647bf4a71b4532058471c27aa7cfa8b77224211e5067c4eb19abcdd");
        hashes.put("close-spec.md", "6c1ee4cf686ba3d9b6e729bf26840863416184be83a15620e5141d7c9edc82cb");
        hashes.put("create-acceptance-criteria.md", "12e068cbc9910416da8acb767d41e27dbeee91083274ccc1469c855f3e08c708");
        hashes.put("create-adr.md", "6e83c791ab01fe24d69c2333c766dbad250c63a08f2338a289f608086689e59d");
        hashes.put("create-diagram.md", "5153ad922dc4312b02ea9eb25c5ff577ef7fb449b6fc20cf9cab7b6d852311c4");
        hashes.put("create-feature-branch.md", "54303fafa8f4cf6a382d4261eba12d01fc00674ed7cc4817061c23c687d0a748");
        hashes.put("create-spec.md", "0f9de66b1a5c8ed7a556e2255dec53adff1518e26b8323f4f95f53854fdce5ff");
        hashes.put("create-worktree.md", "4820d69aa31318cf32a891e41a7d436ee1623bee410c33b4f42876e8b62253a3");
        hashes.put("explore-design.md", "7a8860875168d89032d64bf42c6ec2b85c17a32a5ca58318ae37d3344354fd2b");
        hashes.put("explore-problem.md", "473f875921df4b54007a28cc07dde73519f0658e2a4fbfd52062e01015f09300");
        hashes.put("implement-spec.md", "f91e9d4183965454f39fc998123fcd0ef377e3dd4b4d3ba0b9dc147224a52d6e");
        hashes.put("profile.md", "103066f8d47f4b46753e301e11607b5f2a72d7e78357e1061ad6465a9a3b497f");
        hashes.put("update-issue.md", "6b034e24a43adad78e061fd755dd1a967c23bf50032644e4369d8669b28b8fc7");
        return Map.copyOf(hashes);
    }

    private static Map<String, ExpectedMetadata> approvedMetadata() {
        Map<String, ExpectedMetadata> metadata = new LinkedHashMap<>();
        metadata.put("benchmark", expected("plinth-java-performance",
            "Design and coordinate a reproducible Java performance test.",
            "[target]", "Read", "Write", "Edit", "Bash"));
        metadata.put("close-spec", expected("plinth-architect", "Archive a completed OpenSpec change by name.", "[openspec-change]", "Read", "Bash"));
        metadata.put("create-acceptance-criteria", expected("plinth-business-analyst", "Derive and post confirmed Gherkin acceptance criteria for an issue.", "[issue-url]", "Read", "Bash"));
        metadata.put("create-adr", expected("plinth-architect", "Create a repository ADR for an approved architectural decision.", "[decision-source] [adr-type]", "Read", "Write", "Edit"));
        metadata.put("create-diagram", expected("plinth-architect", "Create an architecture or design diagram from selected source artifacts.", "[source-artifact] [diagram-type]", "Read", "Write", "Edit"));
        metadata.put("create-feature-branch", expected("plinth-tech-lead", "Create and switch to a conventionally named feature branch.", "[issue-or-change|type description] [base-reference]", "Read", "Bash"));
        metadata.put("create-spec", expected("plinth-architect", "Create or update OpenSpec artifacts from approved source material.", "[issue-url]", "Read", "Write", "Edit", "Bash"));
        metadata.put("create-worktree", expected("plinth-tech-lead", "Create an isolated Git worktree on a new conventionally named branch.", "[issue-or-change|type description] [target-path] [base-reference]", "Read", "Bash"));
        metadata.put("explore-design", expected("plinth-architect", "Refine the technical design of an issue or OpenSpec change before implementation.", "[openspec-change]", "Read", "Write", "Edit", "Bash"));
        metadata.put("explore-problem", expected("plinth-business-analyst", "Analyze an issue through five lenses and post a Functional Specification.", "[issue-url]", "Read", "Bash"));
        metadata.put("implement-spec", expected("plinth-tech-lead", "Deliver an approved plan or OpenSpec change through controlled implementation.", "[openspec-change]", "Read", "Write", "Edit", "Bash"));
        metadata.put("profile", expected("plinth-java-performance", "Coordinate a reproducible Java profiling and optimization lifecycle.", "[target]", "Read", "Write", "Edit", "Bash"));
        metadata.put("update-issue", expected("plinth-business-analyst", "Update an issue description with structured, evidence-backed content.", "[issue-url]", "Read", "Bash"));
        return Map.copyOf(metadata);
    }

    private static ExpectedMetadata expected(String agent, String description, String argumentHint, String... tools) {
        return new ExpectedMetadata(agent, description, argumentHint, List.of(tools));
    }

    private record ExpectedMetadata(String agent, String description, String argumentHint, List<String> tools) {}
}
