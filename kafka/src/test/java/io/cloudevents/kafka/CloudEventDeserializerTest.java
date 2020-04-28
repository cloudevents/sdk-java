package io.cloudevents.kafka;

import io.cloudevents.CloudEvent;
import io.cloudevents.test.Data;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CloudEventDeserializerTest {

    @Test
    public void deserializerShouldWork() {
        String topic = "test";
        CloudEvent inEvent = Data.V1_WITH_JSON_DATA;

        CloudEventDeserializer deserializer = new CloudEventDeserializer();

        // Serialize the event first
        ProducerRecord<Void, byte[]> inRecord = inEvent.asBinaryMessage().visit(KafkaProducerMessageVisitor.create(topic));
        CloudEvent outEvent = deserializer.deserialize(topic, inRecord.headers(), inRecord.value());

        assertThat(outEvent)
            .isEqualTo(inEvent);
    }

}
