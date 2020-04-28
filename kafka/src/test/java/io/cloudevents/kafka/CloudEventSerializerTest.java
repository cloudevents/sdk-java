package io.cloudevents.kafka;

import io.cloudevents.CloudEvent;
import io.cloudevents.format.EventFormatProvider;
import io.cloudevents.message.Encoding;
import io.cloudevents.message.Message;
import io.cloudevents.mock.CSVFormat;
import io.cloudevents.test.Data;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class CloudEventSerializerTest {

    @Test
    public void serializerWithDefaultsShouldWork() {
        CloudEventSerializer serializer = new CloudEventSerializer();

        assertProducesMessagesWithEncoding(serializer, Encoding.BINARY);
    }

    @Test
    public void serializerNotKey() {
        CloudEventSerializer serializer = new CloudEventSerializer();

        assertThatCode(() -> serializer.configure(new HashMap<>(), true))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void serializerWithEncodingBinary() {
        CloudEventSerializer serializer = new CloudEventSerializer();

        HashMap<String, Object> config = new HashMap<>();
        config.put(CloudEventSerializer.ENCODING_CONFIG, "BINARY");

        assertThatCode(() -> serializer.configure(config, false))
            .doesNotThrowAnyException();

        assertProducesMessagesWithEncoding(serializer, Encoding.BINARY);
    }

    @Test
    public void serializerWithEncodingStructuredWithoutEventFormat() {
        CloudEventSerializer serializer = new CloudEventSerializer();

        HashMap<String, Object> config = new HashMap<>();
        config.put(CloudEventSerializer.ENCODING_CONFIG, "STRUCTURED");

        assertThatCode(() -> serializer.configure(config, false))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void serializerWithEncodingStructuredAndFormat() {
        CloudEventSerializer serializer = new CloudEventSerializer();

        EventFormatProvider.getInstance().registerFormat(CSVFormat.INSTANCE);

        HashMap<String, Object> config = new HashMap<>();
        config.put(CloudEventSerializer.ENCODING_CONFIG, "STRUCTURED");
        config.put(CloudEventSerializer.EVENT_FORMAT_CONFIG, CSVFormat.INSTANCE.serializedContentType());

        assertThatCode(() -> serializer.configure(config, false))
            .doesNotThrowAnyException();

        assertProducesMessagesWithEncoding(serializer, Encoding.STRUCTURED);
    }

    private void assertProducesMessagesWithEncoding(CloudEventSerializer serializer, Encoding expectedEncoding) {
        String topic = "test";
        CloudEvent event = Data.V1_MIN;

        Headers headers = new RecordHeaders();
        byte[] payload = serializer.serialize(topic, headers, event);

        Message message = KafkaMessageFactory.create(headers, payload);

        assertThat(message.getEncoding())
            .isEqualTo(expectedEncoding);
        assertThat(message.toEvent())
            .isEqualTo(event);
    }

}
