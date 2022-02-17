package io.cloudevents.xml;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.*;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

import static io.cloudevents.core.test.Data.*;
import static org.assertj.core.api.Assertions.assertThat;

public class XMLFormatTest {

    private final EventFormat format = new XMLFormat();

    @Test
    public void testRegistration() {
        assertThat(format.serializedContentType()).isNotNull();
        assertThat(format.serializedContentType()).isEqualTo("application/cloudevents+xml");
    }

    @ParameterizedTest
    @MethodSource("serializeTestArgumentsDefault")
    /**
     * 1. Serialized a CloudEvent object into XML.
     * 2. Compare the serialized output with the expected (control) content.
     */
    public void serialize(io.cloudevents.CloudEvent input, String xmlFile) throws IOException {

        System.out.println("Serialize(" + xmlFile + ")");

        // Serialize the event.
        byte[] raw = format.serialize(input);

        Assertions.assertNotNull(raw);
        Assertions.assertTrue(raw.length > 0);

        System.out.println("Serialized Size : " + raw.length + " bytes");

        if (xmlFile != null) {

            Source expectedSource = getTestSource(xmlFile);
            Source actualSource = Input.fromByteArray(raw).build();

            assertThat(expectedSource).isNotNull();
            assertThat(actualSource).isNotNull();

            // Now compare the documents

            Diff diff = DiffBuilder.compare(expectedSource)
                .withTest(actualSource)
                .ignoreComments()
                .ignoreElementContentWhitespace()
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
                .checkForSimilar()
                .build();

            if (diff.hasDifferences()){

                // Dump what was actually generated.
                dumpXml(raw);

                for (Difference d : diff.getDifferences()) {
                    System.out.println(d);
                }
            }
            Assertions.assertFalse(diff.hasDifferences(), diff.toString());
        }

    }

    public static Stream<Arguments> serializeTestArgumentsDefault() {
        return Stream.of(
            Arguments.of(V1_MIN, "v1/min.xml"),
            Arguments.of(V1_WITH_JSON_DATA, "v1/json_data.xml"),
            Arguments.of(V1_WITH_TEXT_DATA, "v1/text_data.xml"),
            Arguments.of(V1_WITH_JSON_DATA_WITH_EXT, "v1/json_data_with_ext.xml"),
            Arguments.of(V1_WITH_XML_DATA, "v1/xml_data.xml"),
            Arguments.of(V1_WITH_BINARY_EXT, "v1/binary_attr.xml"),

            Arguments.of(V03_MIN, "v03/min.xml")

        );
    }

    @ParameterizedTest
    @MethodSource("deserializeArgs")
    /**
     * Basic test to deserialize an XML representation into
     * a CloudEvent - no correctness checks.
     */
    public void deserialize(String xmlFile) throws IOException {

        // Get the test data
        byte[] data = getData(xmlFile);

        assertThat(data).isNotNull();
        assertThat(data).isNotEmpty();

        // Attempt deserialize
        CloudEvent ce = format.deserialize(data);

        // Did we return something
        assertThat(ce).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("deserializeArgs")
    /**
     * Round-trip test starting with an XML Formated event
     * 1. Deserialize an XML Formated Event into a CE
     * 2. Serialize the CE back into XML
     * 3. Compare the orginal (expected) and new XML document
     */
    public void roundTrip(String fileName) throws IOException {

        byte[] inputData = getData(fileName);

        // (1) DeSerialize
        CloudEvent ce = format.deserialize(inputData);
        assertThat(ce).isNotNull();

        // (2) Serialize
        byte[] outputData = format.serialize(ce);
        assertThat(outputData).isNotNull();
        assertThat(outputData).isNotEmpty();

        // (3) Compare the two XML Documents
        Source expectedSource = getStreamSource(inputData);
        Source actualSource = getStreamSource(outputData);

        Diff diff = DiffBuilder.compare(expectedSource)
            .withTest(actualSource)
            .ignoreComments()
            .ignoreElementContentWhitespace()
            .checkForSimilar()
            .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
            .build();

        if (diff.hasDifferences()){
            dumpXml(outputData);

            if (diff.hasDifferences()){
                for (Difference d : diff.getDifferences()) {
                    System.out.println(d);
                }
            }
        }
        Assertions.assertFalse(diff.hasDifferences());

    }

    public static Stream<Arguments> deserializeArgs() {
        return Stream.of(

            Arguments.of("v1/min.xml"),
            Arguments.of("v1/text_data.xml"),
            Arguments.of("v1/json_data.xml"),
            Arguments.of("v1/binary_attr.xml"),
            Arguments.of("v1/json_data_with_ext.xml"),
            Arguments.of("v1/xml_data.xml"),
            Arguments.of("v1/xml_data_with_ns1.xml"),
            Arguments.of("v1/xml_data_with_ns2.xml"),
            Arguments.of("v1/xml_data_with_ns3.xml")
        );
    }

    //-------------------------------------------------------

    private static File getFile(String filename) throws IOException {
        URL file = Thread.currentThread().getContextClassLoader().getResource(filename);
        assertThat(file).isNotNull();
        File dataFile = new File(file.getFile());
        assertThat(dataFile).isNotNull();
        return dataFile;
    }

    private static Reader getReader(String filename) throws IOException {
        File dataFile = getFile(filename);
        return new FileReader(dataFile);
    }

    private static byte[] getData(String filename) throws IOException {
        File f = getFile(filename);
        return Files.readAllBytes(f.toPath());
    }

    private StreamSource getStreamSource(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        return new StreamSource(bais);
    }

    private Source getTestSource(String filename) throws IOException {
        return Input.fromFile(getFile(filename)).build();
    }

    private void dumpXml(byte[] data) {
        System.out.println(dumpAsString(data));
    }

    private String dumpAsString(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        return StandardCharsets.UTF_8.decode(bb).toString();
    }


    static class CustomComparisonFormatter extends DefaultComparisonFormatter {

        @Override
        public String getDetails(Comparison.Detail difference, ComparisonType type, boolean formatXml) {
            return super.getDetails(difference, type, formatXml);
        }
    }
}
