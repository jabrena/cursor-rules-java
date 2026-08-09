package info.jab.pml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static org.assertj.core.api.Assertions.assertThat;

class CommandVersionConsistencyTest {

    @TestFactory
    @DisplayName("Every command metadata version matches the root Maven project version")
    Stream<DynamicTest> commandVersionsMatchRootPom() throws Exception {
        String expectedVersion = readRootProjectVersion();

        return CommandIndexes.commandSources()
            .map(source -> DynamicTest.dynamicTest(source, () -> assertCommandVersion(source, expectedVersion)));
    }

    private void assertCommandVersion(String source, String expectedVersion) throws Exception {
        String resourceName = "commands/" + source;
        try (InputStream stream = requireResource(resourceName)) {
            Element metadata = requiredDirectChild(InventoryXmlLoader.parse(stream).getDocumentElement(), "metadata");
            String actualVersion = requiredDirectChild(metadata, "version").getTextContent().trim();

            assertThat(actualVersion)
                .withFailMessage(
                    "Command %s has metadata version '%s', but the root pom.xml version is '%s'.",
                    source, actualVersion, expectedVersion)
                .isEqualTo(expectedVersion);
        }
    }

    private String readRootProjectVersion() throws Exception {
        Path rootPom = findRootPom();
        try (InputStream stream = Files.newInputStream(rootPom)) {
            Element project = InventoryXmlLoader.parse(stream).getDocumentElement();
            return requiredDirectChild(project, "version").getTextContent().trim();
        }
    }

    private Path findRootPom() {
        String reactorRoot = System.getProperty("maven.multiModuleProjectDirectory");
        Stream<Path> candidates = Stream.concat(
            reactorRoot == null ? Stream.empty() : Stream.of(Path.of(reactorRoot, "pom.xml")),
            Stream.of(Path.of("..", "pom.xml"), Path.of("pom.xml")));

        return candidates
            .map(Path::normalize)
            .filter(Files::isRegularFile)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Root pom.xml not found"));
    }

    private InputStream requireResource(String resourceName) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resourceName);
        if (stream == null) {
            throw new IllegalStateException("Resource not found: " + resourceName);
        }
        return stream;
    }

    private Element requiredDirectChild(Element parent, String name) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element element && name.equals(element.getNodeName())) {
                return element;
            }
        }
        throw new IllegalStateException("Missing direct child <" + name + "> under <" + parent.getNodeName() + ">");
    }
}
