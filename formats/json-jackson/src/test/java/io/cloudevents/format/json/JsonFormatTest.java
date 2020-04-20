package io.cloudevents.format.json;

import com.fasterxml.jackson.databind.JsonNode;
import io.cloudevents.CloudEvent;
import io.cloudevents.format.EventFormat;
import io.cloudevents.format.EventFormatProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static io.cloudevents.test.Data.*;
import static org.assertj.core.api.Assertions.assertThat;

class JsonFormatTest {

    @ParameterizedTest
    @MethodSource("serializeTestArguments")
    void serialize(CloudEvent input, String outputFile) throws IOException {
        JsonNode jsonOutput = JsonFormat.MAPPER.readValue(loadFile(outputFile), JsonNode.class);

        byte[] serialized = getFormat().serialize(input);
        JsonNode serializedJson = JsonFormat.MAPPER.readValue(serialized, JsonNode.class);
        assertThat(serializedJson)
            .isEqualTo(jsonOutput);
    }

    @ParameterizedTest
    @MethodSource("deserializeTestArguments")
    void deserialize(String inputFile, CloudEvent output) {
        CloudEvent deserialized = getFormat().deserialize(loadFile(inputFile));
        assertThat(deserialized)
            .isEqualTo(output);
    }

    @ParameterizedTest
    @MethodSource("roundTripTestArguments")
    void jsonRoundTrip(String inputFile) throws IOException {
        byte[] input = loadFile(inputFile);

        JsonNode jsonInput = JsonFormat.MAPPER.readTree(input);
        CloudEvent deserialized = getFormat().deserialize(input);
        assertThat(deserialized).isNotNull();

        byte[] output = getFormat().serialize(deserialized);
        JsonNode jsonOutput = JsonFormat.MAPPER.readValue(output, JsonNode.class);
        assertThat(jsonOutput)
            .isEqualTo(jsonInput);
    }

    @ParameterizedTest
    @MethodSource("io.cloudevents.test.Data#allEvents")
    void eventRoundTrip(CloudEvent input) {
        byte[] serialized = getFormat().serialize(input);
        assertThat(serialized).isNotEmpty();

        CloudEvent output = getFormat().deserialize(serialized);
        assertThat(output).isEqualTo(input);
    }

    private static Stream<Arguments> serializeTestArguments() {
        return deserializeTestArguments().map(a -> {
            List<Object> vals = new ArrayList<>(Arrays.asList(a.get()));
            Collections.reverse(vals);
            return Arguments.of(vals.toArray());
        });
    }

    private static Stream<Arguments> deserializeTestArguments() {
        return Stream.of(
            Arguments.of("v03/min.json", V03_MIN),
            Arguments.of("v03/json_data.json", V03_WITH_JSON_DATA),
            Arguments.of("v03/base64_json_data.json", V03_WITH_JSON_DATA),
            Arguments.of("v03/xml_data.xml", V03_WITH_XML_DATA),
            Arguments.of("v03/base64_xml_data.xml", V03_WITH_XML_DATA),
            Arguments.of("v03/text_data.xml", V03_WITH_TEXT_DATA),
            Arguments.of("v03/base64_text_data.xml", V03_WITH_TEXT_DATA),
            Arguments.of("v1/min.json", V1_MIN),
            Arguments.of("v1/json_data.json", V1_WITH_JSON_DATA),
            Arguments.of("v1/base64_json_data.json", V1_WITH_JSON_DATA),
            Arguments.of("v1/xml_data.xml", V1_WITH_XML_DATA),
            Arguments.of("v1/base64_xml_data.xml", V1_WITH_XML_DATA),
            Arguments.of("v1/text_data.xml", V1_WITH_TEXT_DATA),
            Arguments.of("v1/base64_text_data.xml", V1_WITH_TEXT_DATA)
        );
    }

    private static Stream<String> roundTripTestArguments() {
        return Stream.of(
            "v03/min.json",
            "v03/json_data.json",
            "v03/base64_json_data.json",
            "v03/xml_data.xml",
            "v03/base64_xml_data.xml",
            "v03/text_data.text",
            "v03/base64_text_data.text",
            "v1/min.json",
            "v1/json_data.json",
            "v1/base64_json_data.json",
            "v1/xml_data.xml",
            "v1/base64_xml_data.xml",
            "v1/text_data.text",
            "v1/base64_text_data.text"
        );
    }

    private static byte[] loadFile(String input) {
        try {
            return String.join(
                "",
                Files.readAllLines(Paths.get(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(input)).toURI()), StandardCharsets.UTF_8)
            ).getBytes();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private EventFormat getFormat() {
        return EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE);
    }

}
