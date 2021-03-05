/*
 * Copyright 2018-Present The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.cloudevents.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.rw.CloudEventRWException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import static io.cloudevents.core.test.Data.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class JsonFormatTest {

    private ObjectMapper mapper = new ObjectMapper();

    @ParameterizedTest
    @MethodSource("serializeTestArgumentsDefault")
    void serialize(CloudEvent input, String outputFile) throws IOException {
        JsonNode jsonOutput = mapper.readValue(loadFile(outputFile), JsonNode.class);

        byte[] serialized = getFormat().serialize(input);
        JsonNode serializedJson = mapper.readValue(serialized, JsonNode.class);
        assertThat(serializedJson)
            .isEqualTo(jsonOutput);
    }

    @ParameterizedTest
    @MethodSource("serializeTestArgumentsString")
    void serializeWithStringData(CloudEvent input, String outputFile) throws IOException {
        JsonNode jsonOutput = mapper.readValue(loadFile(outputFile), JsonNode.class);

        byte[] serialized = getFormat().withForceNonJsonDataToString().serialize(input);
        JsonNode serializedJson = mapper.readValue(serialized, JsonNode.class);
        assertThat(serializedJson)
            .isEqualTo(jsonOutput);
    }

    @ParameterizedTest
    @MethodSource("serializeTestArgumentsBase64")
    void serializeWithBase64Data(CloudEvent input, String outputFile) throws IOException {
        JsonNode jsonOutput = mapper.readValue(loadFile(outputFile), JsonNode.class);

        byte[] serialized = getFormat().withForceJsonDataToBase64().serialize(input);
        JsonNode serializedJson = mapper.readValue(serialized, JsonNode.class);
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

        JsonNode jsonInput = mapper.readTree(input);
        CloudEvent deserialized = getFormat().deserialize(input);
        assertThat(deserialized).isNotNull();

        byte[] output = getFormat().serialize(deserialized);
        JsonNode jsonOutput = mapper.readValue(output, JsonNode.class);
        assertThat(jsonOutput)
            .isEqualTo(jsonInput);
    }

    @ParameterizedTest
    @MethodSource("io.cloudevents.core.test.Data#allEvents")
    void eventRoundTrip(CloudEvent input) {
        byte[] serialized = getFormat().serialize(input);
        assertThat(serialized).isNotEmpty();

        CloudEvent output = getFormat().deserialize(serialized);
        assertThat(output).isEqualTo(normalizeToJsonValueIfNeeded(input));
    }

    @Test
    void throwExpectedOnInvalidSpecversion() {
        assertThatCode(() -> getFormat().deserialize(("{\"specversion\":\"9000.1\"}").getBytes(StandardCharsets.UTF_8)))
            .hasCauseInstanceOf(MismatchedInputException.class)
            .hasMessageContaining(CloudEventRWException.newInvalidSpecVersion("9000.1").getMessage());
    }

    public static Stream<Arguments> serializeTestArgumentsDefault() {
        return Stream.of(
            Arguments.of(V03_MIN, "v03/min.json"),
            Arguments.of(V03_WITH_JSON_DATA, "v03/json_data.json"),
            Arguments.of(V03_WITH_JSON_DATA_WITH_EXT, "v03/json_data_with_ext.json"),
            Arguments.of(V03_WITH_XML_DATA, "v03/base64_xml_data.json"),
            Arguments.of(V03_WITH_TEXT_DATA, "v03/base64_text_data.json"),
            Arguments.of(V1_MIN, "v1/min.json"),
            Arguments.of(V1_WITH_JSON_DATA, "v1/json_data.json"),
            Arguments.of(V1_WITH_JSON_DATA_WITH_FRACTIONAL_TIME, "v1/json_data_with_fractional_time.json"),
            Arguments.of(V1_WITH_JSON_DATA_WITH_EXT, "v1/json_data_with_ext.json"),
            Arguments.of(V1_WITH_XML_DATA, "v1/base64_xml_data.json"),
            Arguments.of(V1_WITH_TEXT_DATA, "v1/base64_text_data.json"),
            Arguments.of(V1_WITH_BINARY_ATTR, "v1/binary_attr.json")
        );
    }

    public static Stream<Arguments> serializeTestArgumentsString() {
        return Stream.of(
            Arguments.of(V03_WITH_JSON_DATA, "v03/json_data.json"),
            Arguments.of(V03_WITH_JSON_DATA_WITH_EXT, "v03/json_data_with_ext.json"),
            Arguments.of(V03_WITH_XML_DATA, "v03/xml_data.json"),
            Arguments.of(V03_WITH_TEXT_DATA, "v03/text_data.json"),
            Arguments.of(V1_WITH_JSON_DATA, "v1/json_data.json"),
            Arguments.of(V1_WITH_JSON_DATA_WITH_EXT, "v1/json_data_with_ext.json"),
            Arguments.of(V1_WITH_XML_DATA, "v1/xml_data.json"),
            Arguments.of(V1_WITH_TEXT_DATA, "v1/text_data.json")
        );
    }

    public static Stream<Arguments> serializeTestArgumentsBase64() {
        return Stream.of(
            Arguments.of(V03_WITH_JSON_DATA, "v03/base64_json_data.json"),
            Arguments.of(V03_WITH_JSON_DATA_WITH_EXT, "v03/base64_json_data_with_ext.json"),
            Arguments.of(V03_WITH_XML_DATA, "v03/base64_xml_data.json"),
            Arguments.of(V03_WITH_TEXT_DATA, "v03/base64_text_data.json"),
            Arguments.of(V1_WITH_JSON_DATA, "v1/base64_json_data.json"),
            Arguments.of(V1_WITH_JSON_DATA_WITH_EXT, "v1/base64_json_data_with_ext.json"),
            Arguments.of(V1_WITH_XML_DATA, "v1/base64_xml_data.json"),
            Arguments.of(V1_WITH_TEXT_DATA, "v1/base64_text_data.json")
        );
    }

    public static Stream<Arguments> deserializeTestArguments() {
        return Stream.of(
            Arguments.of("v03/min.json", V03_MIN),
            Arguments.of("v03/json_data.json", normalizeToJsonValueIfNeeded(V03_WITH_JSON_DATA)),
            Arguments.of("v03/json_data_with_ext.json", normalizeToJsonValueIfNeeded(V03_WITH_JSON_DATA_WITH_EXT)),
            Arguments.of("v03/base64_json_data.json", V03_WITH_JSON_DATA),
            Arguments.of("v03/base64_json_data_with_ext.json", V03_WITH_JSON_DATA_WITH_EXT),
            Arguments.of("v03/xml_data.json", V03_WITH_XML_DATA),
            Arguments.of("v03/base64_xml_data.json", V03_WITH_XML_DATA),
            Arguments.of("v03/text_data.json", V03_WITH_TEXT_DATA),
            Arguments.of("v03/base64_text_data.json", V03_WITH_TEXT_DATA),
            Arguments.of("v1/min.json", V1_MIN),
            Arguments.of("v1/json_data.json", normalizeToJsonValueIfNeeded(V1_WITH_JSON_DATA)),
            Arguments.of("v1/json_data_with_ext.json", normalizeToJsonValueIfNeeded(V1_WITH_JSON_DATA_WITH_EXT)),
            Arguments.of("v1/base64_json_data.json", V1_WITH_JSON_DATA),
            Arguments.of("v1/base64_json_data_with_ext.json", V1_WITH_JSON_DATA_WITH_EXT),
            Arguments.of("v1/xml_data.json", V1_WITH_XML_DATA),
            Arguments.of("v1/base64_xml_data.json", V1_WITH_XML_DATA),
            Arguments.of("v1/text_data.json", V1_WITH_TEXT_DATA),
            Arguments.of("v1/base64_text_data.json", V1_WITH_TEXT_DATA)
        );
    }

    public static Stream<String> roundTripTestArguments() {
        return Stream.of(
            "v03/min.json",
            "v03/json_data.json",
            "v03/json_data_with_ext.json",
            "v03/base64_xml_data.json",
            "v03/base64_text_data.json",
            "v1/min.json",
            "v1/json_data.json",
            "v1/base64_xml_data.json",
            "v1/base64_text_data.json"
        );
    }

    private JsonFormat getFormat() {
        return (JsonFormat) EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE);
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

    private static CloudEvent normalizeToJsonValueIfNeeded(CloudEvent event) {
        if (event.getData() != null && JsonFormat.dataIsJsonContentType(event.getDataContentType())) {
            CloudEventBuilder builder = null;
            if (event.getSpecVersion() == SpecVersion.V1) {
                builder = CloudEventBuilder.v1(event);
            } else {
                builder = CloudEventBuilder.v03(event);
            }
            return builder
                .withData(new JsonCloudEventData(JsonNodeFactory.instance.objectNode()))
                .build();
        } else {
            return event;
        }
    }

}
