package info.jab.pml;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Command Schema Tests")
class CommandSchemaTest {

    private final Schema schema = loadSchema();

    @Test
    @DisplayName("Every inventoried command source must satisfy commands.xsd")
    void should_validateAllCommandSources_when_inventoryIsLoaded() {
        CommandIndexes.commandSources().forEach(source -> assertThatCode(() -> validateResource("commands/" + source))
            .as("Schema-valid command source: %s", source)
            .doesNotThrowAnyException());
    }

    @Test
    @DisplayName("Metadata must contain required version provenance")
    void should_rejectMissingVersion_when_metadataIsValidated() {
        String xml = validCommand("<description>Description</description>"
            + "<argument-hint>[input]</argument-hint>"
            + "<model>inherit</model>"
            + "<agent>custom-agent</agent>"
            + "<tools><tools-list><tool>Read</tool></tools-list></tools>");

        assertThatThrownBy(() -> validate(xml)).isInstanceOf(SAXException.class);
    }

    @Test
    @DisplayName("Metadata must contain at least one tool")
    void should_rejectZeroTools_when_metadataIsValidated() {
        assertThatThrownBy(() -> validate(validCommand(metadataScalars()
            + "<tools><tools-list/></tools>")))
            .isInstanceOf(SAXException.class);
    }

    @Test
    @DisplayName("Metadata accepts one or multiple extensible tools")
    void should_acceptOneAndMultipleTools_when_metadataIsValidated() {
        assertThatCode(() -> validate(validCommand(metadataScalars()
            + "<tools><tools-list><tool>Read</tool></tools-list></tools>")))
            .doesNotThrowAnyException();
        assertThatCode(() -> validate(validCommand(metadataScalars()
            + "<tools><tools-list><tool>Custom Read</tool><tool>Edit</tool></tools-list></tools>")))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Metadata accepts optional, one, or multiple PML-style authors")
    void should_acceptOptionalOneAndMultipleAuthors_when_metadataIsValidated() {
        String tools = "<tools><tools-list><tool>Read</tool></tools-list></tools>";

        assertThatCode(() -> validate(validCommand(metadataScalars() + tools)))
            .doesNotThrowAnyException();
        assertThatCode(() -> validate(validCommand("<authors><author>First Author</author></authors>"
            + metadataScalars() + tools)))
            .doesNotThrowAnyException();
        assertThatCode(() -> validate(validCommand("<authors><author>First Author</author>"
            + "<author>Second Author</author></authors>" + metadataScalars() + tools)))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Metadata must compose the global agents-style element vocabulary by reference")
    void should_referenceGlobalElements_when_metadataSchemaIsInspected() throws Exception {
        Document schemaDocument;
        try (var stream = CommandSchemaTest.class.getClassLoader().getResourceAsStream("commands.xsd")) {
            if (stream == null) {
                throw new IllegalStateException("Missing commands.xsd");
            }
            schemaDocument = InventoryXmlLoader.parse(stream);
        }

        assertThatCode(() -> globalElement(schemaDocument, "metadata")).doesNotThrowAnyException();
        assertThat(sequenceReferences(globalElement(schemaDocument, "metadata")))
            .containsExactly("authors", "version", "description", "argument-hint", "model", "agent", "tools");
        assertThat(sequenceReferences(globalElement(schemaDocument, "authors")))
            .containsExactly("author");
        assertThat(sequenceReferences(globalElement(schemaDocument, "tools")))
            .containsExactly("tools-list");
        assertThat(sequenceReferences(globalElement(schemaDocument, "tools-list")))
            .containsExactly("tool");
        assertThat(List.of("authors", "author", "version", "description", "argument-hint", "model", "agent", "tool"))
            .allSatisfy(name -> assertThat(globalElement(schemaDocument, name))
                .as("Global PML-style declaration for %s", name)
                .isNotNull());
    }

    private static String metadataScalars() {
        return "<version>0.19.0-SNAPSHOT</version>"
            + "<description>Description</description>"
            + "<argument-hint>[input]</argument-hint>"
            + "<model>custom-model</model>"
            + "<agent>custom-agent</agent>";
    }

    private static String validCommand(String metadata) {
        return "<command id=\"test\"><metadata>" + metadata
            + "</metadata><goal>Goal</goal><steps/></command>";
    }

    private void validateResource(String resource) throws Exception {
        try (var stream = CommandSchemaTest.class.getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IllegalStateException("Missing command resource: " + resource);
            }
            schema.newValidator().validate(new StreamSource(stream));
        }
    }

    private void validate(String xml) throws Exception {
        schema.newValidator().validate(new StreamSource(new StringReader(xml)));
    }

    private static Schema loadSchema() {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            return factory.newSchema(CommandSchemaTest.class.getClassLoader().getResource("commands.xsd"));
        } catch (SAXException e) {
            throw new IllegalStateException("Cannot load commands.xsd", e);
        }
    }

    private static Element globalElement(Document document, String name) {
        Element root = document.getDocumentElement();
        for (Node child = root.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element element
                && element.getNodeName().endsWith(":element")
                && name.equals(element.getAttribute("name"))) {
                return element;
            }
        }
        throw new IllegalStateException("Missing global XSD element: " + name);
    }

    private static List<String> sequenceReferences(Element globalElement) {
        Element sequence = (Element) globalElement.getElementsByTagName("xs:sequence").item(0);
        List<String> references = new ArrayList<>();
        for (Node child = sequence.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element element && element.hasAttribute("ref")) {
                references.add(element.getAttribute("ref"));
            }
        }
        return List.copyOf(references);
    }
}
