package info.jab.pml;

import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import java.util.stream.Stream;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThatCode;

class RemoteSchemaValidationTest {

    //TODO: Use maven-central ASAP
    private static final String REMOTE_XSD = "https://jabrena.github.io/pml/schemas/0.9.0/pml.xsd";

    private static Schema schema;

    @BeforeAll
    static void loadRemoteSchema() throws Exception {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "all");
        schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "all");

        schema = schemaFactory.newSchema(URI.create(REMOTE_XSD).toURL());
    }

    private static Stream<String> provideXmlFileNames() {
        return SkillReferences.xmlFilenames();
    }

    @ParameterizedTest
    @MethodSource("provideXmlFileNames")
    void xmlFilesAreValidAgainstRemoteSchema(String fileName) throws Exception {
        String resourcePath = "/" + fileName;
        try (InputStream xml = getClass().getResourceAsStream(resourcePath)) {
            if (Objects.isNull(xml)) {
                throw new IllegalStateException("Test resource not found: " + resourcePath);
            }

            Validator validator = schema.newValidator();

            Source source = new StreamSource(xml);
            assertThatCode(() -> validator.validate(source)).doesNotThrowAnyException();
        }
    }
}
