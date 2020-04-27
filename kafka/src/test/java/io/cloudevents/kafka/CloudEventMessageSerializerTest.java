package io.cloudevents.kafka;

import io.cloudevents.CloudEvent;
import io.cloudevents.message.Encoding;
import io.cloudevents.message.Message;
import io.cloudevents.mock.MockBinaryMessage;
import io.cloudevents.test.Data;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CloudEventMessageSerializerTest {

    @Test
    public void serializerShouldWork() {
        String topic = "test";
        CloudEvent event = Data.V1_WITH_JSON_DATA;

        CloudEventMessageSerializer serializer = new CloudEventMessageSerializer();

        Headers headers = new RecordHeaders();

        MockBinaryMessage inMessage = new MockBinaryMessage();
        event.asBinaryMessage().visit(inMessage);

        byte[] payload = serializer.serialize(topic, headers, inMessage);

        Message outMessage = KafkaMessageFactory.create(headers, payload);

        assertThat(outMessage.getEncoding())
            .isEqualTo(Encoding.BINARY);
        assertThat(outMessage.toEvent())
            .isEqualTo(event);
    }

}
