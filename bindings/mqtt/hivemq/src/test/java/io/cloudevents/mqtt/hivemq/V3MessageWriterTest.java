package io.cloudevents.mqtt.hivemq;

import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3PublishBuilder;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.core.test.Data;
import io.cloudevents.rw.CloudEventRWException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class V3MessageWriterTest {

    Mqtt3PublishBuilder.Complete builder;
    V3MessageWriter writer;
    EventFormat csvFormat = CSVFormat.INSTANCE;


    V3MessageWriterTest() {

        builder = (Mqtt3PublishBuilder.Complete) Mqtt3Publish.builder();
        writer = new V3MessageWriter(builder);
        EventFormatProvider.getInstance().registerFormat(csvFormat);
    }

    @Test
    void create() {
    }

    @Test
    void setEvent() {
    }

    @Test
    void writeStructuredA() {
        assertNotNull(writer.writeStructured(Data.V1_MIN, csvFormat.serializedContentType()));
    }

    @Test
    void testWriteStructuredB() {
        assertNotNull(writer.writeStructured(Data.V1_MIN, csvFormat));
    }

    @Test
    void writeBinary() {

        // This should fail
        Assertions.assertThrows(CloudEventRWException.class, () -> {
            writer.writeBinary(Data.V1_MIN);
        });
    }
}
