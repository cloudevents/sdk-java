package io.cloudevents.jackson;

import io.cloudevents.CloudEvent;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.io.StringReader;

import static io.cloudevents.jackson.JsonFormat.getCloudEventJacksonModule;
import static org.assertj.core.api.Assertions.assertThat;

class CloudEventDeserializerTest {
    private static final String NON_BINARY_PAYLOAD = """
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
        }
        """;

    private static final String BINARY_PAYLOAD = """
        {
            "specversion" : "1.0",
            "type" : "com.example.someevent",
            "source" : "/mycontext",
            "id" : "D234-1234-1234",
            "data_base64" : "eyAieHl6IjogMTIzIH0="
        }
        """;

    @Test
    void impliedDataContentTypeNonBinaryData() {
        JsonMapper mapper = getJsonMapper(false);
        StringReader reader = new StringReader(NON_BINARY_PAYLOAD);
        CloudEvent ce = mapper.readValue(reader, CloudEvent.class);
        assertThat(ce.getDataContentType()).isEqualTo("application/json");

        mapper = getJsonMapper(true);
        reader = new StringReader(NON_BINARY_PAYLOAD);
        ce = mapper.readValue(reader, CloudEvent.class);
        assertThat(ce.getDataContentType()).isNull();
    }

    @Test
    void impliedDataContentTypeBinaryData() {
        final JsonMapper mapper = getJsonMapper(false);
        StringReader reader = new StringReader(BINARY_PAYLOAD);
        CloudEvent ce = mapper.readValue(reader, CloudEvent.class);
        assertThat(ce.getDataContentType()).isNull();
    }

    private static JsonMapper getJsonMapper(boolean disableDataContentTypeDefaulting) {
        final SimpleModule module = getCloudEventJacksonModule(
            JsonFormatOptions
                .builder()
                .disableDataContentTypeDefaulting(disableDataContentTypeDefaulting)
                .build()
        );
        return JsonMapper.builder().addModule(module).build();
    }
}
