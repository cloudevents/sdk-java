package io.cloudevents.kafka;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.test.Data;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PartitionKeyExtensionInterceptorTest {

    @Test
    public void testNoPartitionKeyAndNoOriginalKey() {
        assertKey(
            new ProducerRecord<>("aaa", Data.V1_MIN),
            null
        );
    }

    @Test
    public void testNoPartitionKey() {
        assertKey(
            new ProducerRecord<>("aaa", "blabla", Data.V1_MIN),
            "blabla"
        );
    }

    @Test
    public void testPartitionKeyAndNoOriginalKey() {
        assertKey(
            new ProducerRecord<>("aaa", CloudEventBuilder
                .v1(Data.V1_MIN)
                .withExtension(PartitionKeyExtensionInterceptor.PARTITION_KEY_EXTENSION, "albalb")
                .build()
            ),
            "albalb"
        );
    }

    @Test
    public void testPartitionKey() {
        assertKey(
            new ProducerRecord<>("aaa", "blabla", CloudEventBuilder
                .v1(Data.V1_MIN)
                .withExtension(PartitionKeyExtensionInterceptor.PARTITION_KEY_EXTENSION, "albalb")
                .build()
            ),
            "albalb"
        );
    }

    private void assertKey(ProducerRecord<Object, CloudEvent> record, Object expectedKey) {
        PartitionKeyExtensionInterceptor interceptor = new PartitionKeyExtensionInterceptor();
        assertThat(interceptor.onSend(record).key())
            .isEqualTo(expectedKey);
    }

}
