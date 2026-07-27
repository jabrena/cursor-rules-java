package info.jab.pml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Enforces a content-integrity rule the PML schema itself cannot express: no
 * skill-reference source's {@code <authors>} element may contain two
 * {@code <author>} children with an identical (trimmed) value.
 * <p>
 * Confirmed empirically (see
 * {@code documentation/openspec/changes/migrate-skill-references-pml-schema/examples/xml/invalid-duplicate-authors-example.xml})
 * that {@code pml.xsd} places no uniqueness constraint on sibling {@code author}
 * text, so {@code xmllint}/schema validation alone accepts a duplicate. Structured
 * after {@link RemoteSchemaValidationTest}: parameterized over
 * {@link SkillReferences#xmlFilenames()}, reading each source once via a classpath
 * resource stream and parsing it with the same {@link InventoryXmlLoader} used
 * elsewhere in this module.
 */
@DisplayName("Skill reference authors uniqueness")
class SkillReferenceAuthorsUniquenessTest {

    private static final String FIXTURE_RELATIVE_PATH =
        "documentation/openspec/changes/migrate-skill-references-pml-schema/examples/xml/invalid-duplicate-authors-example.xml";

    private static Stream<String> provideXmlFileNames() {
        return SkillReferences.xmlFilenames();
    }

    @ParameterizedTest
    @MethodSource("provideXmlFileNames")
    @DisplayName("Every skill-reference source declares only unique <author> values")
    void should_rejectDuplicateAuthorValues_when_sourceIsParsed(String fileName) throws Exception {
        String resourcePath = "/" + fileName;
        try (InputStream xml = getClass().getResourceAsStream(resourcePath)) {
            if (Objects.isNull(xml)) {
                throw new IllegalStateException("Test resource not found: " + resourcePath);
            }

            Document document = InventoryXmlLoader.parse(xml);
            assertNoDuplicateAuthors(fileName, document);
        }
    }

    @Test
    @DisplayName("A source with two identical <author> values fails the uniqueness check with a diagnostic")
    void should_failUniquenessCheck_when_fixtureDeclaresDuplicateAuthorValue() throws Exception {
        Path fixture = locateFixture();

        Document document;
        try (InputStream xml = Files.newInputStream(fixture)) {
            document = InventoryXmlLoader.parse(xml);
        }

        String fixtureName = fixture.getFileName().toString();

        assertThatThrownBy(() -> assertNoDuplicateAuthors(fixtureName, document))
            .isInstanceOf(AssertionError.class)
            .hasMessageContaining(fixtureName)
            .hasMessageContaining("Juan Antonio Breña Moral");
    }

    private void assertNoDuplicateAuthors(String fileName, Document document) {
        NodeList authorNodes = document.getElementsByTagName("author");
        List<String> authorValues = new ArrayList<>();
        for (int i = 0; i < authorNodes.getLength(); i++) {
            authorValues.add(normalize(authorNodes.item(i).getTextContent()));
        }

        Set<String> seen = new HashSet<>();
        List<String> duplicates = new ArrayList<>();
        for (String value : authorValues) {
            if (!seen.add(value)) {
                duplicates.add(value);
            }
        }

        assertThat(duplicates)
            .withFailMessage(
                "Skill-reference source %s declares duplicate <author> value(s): %s",
                fileName, duplicates
            )
            .isEmpty();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private Path locateFixture() {
        Path dir = Path.of("").toAbsolutePath();
        while (dir != null) {
            Path candidate = dir.resolve(FIXTURE_RELATIVE_PATH);
            if (Files.exists(candidate)) {
                return candidate;
            }
            dir = dir.getParent();
        }
        throw new IllegalStateException("Could not locate fixture: " + FIXTURE_RELATIVE_PATH);
    }
}
