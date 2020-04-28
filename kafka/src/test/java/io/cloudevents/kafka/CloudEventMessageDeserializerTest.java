package io.cloudevents.kafka;

import io.cloudevents.CloudEvent;
import io.cloudevents.message.Encoding;
import io.cloudevents.message.Message;
import io.cloudevents.test.Data;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CloudEventMessageDeserializerTest {

    @Test
    public void deserializerShouldWork() {
        String topic = "test";
        CloudEvent inEvent = Data.V1_WITH_JSON_DATA;

        CloudEventMessageDeserializer deserializer = new CloudEventMessageDeserializer();

        // Serialize the event first
        ProducerRecord<Void, byte[]> inRecord = inEvent.asBinaryMessage().visit(KafkaProducerMessageVisitor.create(topic));
        Message outMessage = deserializer.deserialize(topic, inRecord.headers(), inRecord.value());

        assertThat(outMessage.getEncoding())
            .isEqualTo(Encoding.BINARY);
        assertThat(outMessage.toEvent())
            .isEqualTo(inEvent);
    }

}
