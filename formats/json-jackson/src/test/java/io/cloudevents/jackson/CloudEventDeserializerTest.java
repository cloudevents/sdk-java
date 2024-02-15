package io.cloudevents.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.cloudevents.CloudEvent;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static io.cloudevents.jackson.JsonFormat.getCloudEventJacksonModule;
import static org.assertj.core.api.Assertions.assertThat;

public class CloudEventDeserializerTest {

    private static final String nonBinaryPayload = "{\n" +
        "    \"specversion\" : \"1.0\",\n" +
        "    \"type\" : \"com.example.someevent\",\n" +
        "    \"source\" : \"/mycontext\",\n" +
        "    \"subject\": null,\n" +
        "    \"id\" : \"D234-1234-1234\",\n" +
        "    \"time\" : \"2018-04-05T17:31:00Z\",\n" +
        "    \"comexampleextension1\" : \"value\",\n" +
        "    \"comexampleothervalue\" : 5,\n" +
        "    \"data\" : \"I'm just a string\"\n" +
        "}";

    private static final String binaryPayload = "{\n" +
        "    \"specversion\" : \"1.0\",\n" +
        "    \"type\" : \"com.example.someevent\",\n" +
        "    \"source\" : \"/mycontext\",\n" +
        "    \"id\" : \"D234-1234-1234\",\n" +
        "    \"data_base64\" : \"eyAieHl6IjogMTIzIH0=\"\n" +
        "}";

    @Test
    void impliedDataContentTypeNonBinaryData() throws IOException {
        ObjectMapper mapper = getObjectMapper(false);
        StringReader reader = new StringReader(nonBinaryPayload);
        CloudEvent ce = mapper.readValue(reader, CloudEvent.class);
        assertThat(ce.getDataContentType()).isEqualTo("application/json");

        mapper = getObjectMapper(true);
        reader = new StringReader(nonBinaryPayload);
        ce = mapper.readValue(reader, CloudEvent.class);
        assertThat(ce.getDataContentType()).isNull();
    }

    @Test
    void impliedDataContentTypeBinaryData() throws IOException {
        final ObjectMapper mapper = getObjectMapper(false);
        StringReader reader = new StringReader(binaryPayload);
        CloudEvent ce = mapper.readValue(reader, CloudEvent.class);
        assertThat(ce.getDataContentType()).isNull();
    }

    private static ObjectMapper getObjectMapper(boolean disableDataContentTypeDefaulting) {
        final ObjectMapper mapper = new ObjectMapper();
        final SimpleModule module = getCloudEventJacksonModule(
            JsonFormatOptions
                .builder()
                .disableDataContentTypeDefaulting(disableDataContentTypeDefaulting)
                .build()
        );
        mapper.registerModule(module);
        return mapper;
    }

}
