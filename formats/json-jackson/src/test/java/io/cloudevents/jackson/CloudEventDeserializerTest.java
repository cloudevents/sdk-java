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

    private static final String nonBinaryPayload = """
        {
            "specversion" : "1.0",
            "type" : "com.example.someevent",
            "source" : "/mycontext",
            "subject": null,
            "id" : "D234-1234-1234",
            "time" : "2018-04-05T17:31:00Z",
            "comexampleextension1" : "value",
            "comexampleothervalue" : 5,
            "data" : "I'm just a string"
        }\
        """;

    private static final String binaryPayload = """
        {
            "specversion" : "1.0",
            "type" : "com.example.someevent",
            "source" : "/mycontext",
            "id" : "D234-1234-1234",
            "data_base64" : "eyAieHl6IjogMTIzIH0="
        }\
        """;

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
