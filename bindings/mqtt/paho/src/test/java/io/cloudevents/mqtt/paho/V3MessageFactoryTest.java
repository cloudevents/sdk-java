package io.cloudevents.mqtt.paho;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventDeserializationException;
import io.cloudevents.core.format.EventSerializationException;
import io.cloudevents.core.message.Encoding;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.test.Data;
import io.cloudevents.core.v03.CloudEventV03;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.types.Time;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.beans.EventSetDescriptor;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class V3MessageFactoryTest {

    @Test
    public void ensureSerializationFormat() {

        MqttMessage message = null;

        // This should fail as we don't support CSV Format

        MessageWriter writer = V3MqttMessageFactory.createWriter();
        Assertions.assertNotNull(writer);

        // Expect an exception

        Assertions.assertThrows(CloudEventRWException.class, () -> {
            writer.writeStructured(Data.V1_MIN, CSVFormat.INSTANCE);
        });
    }


    @ParameterizedTest()
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    public void ensureDeserialization(CloudEvent ce) {


        final String contentType = CSVFormat.INSTANCE.serializedContentType() + "; charset=utf8";
        final byte[] contentPayload = CSVFormat.INSTANCE.serialize(ce);

        // Build the MQTT Message

        MqttMessage m = new MqttMessage();
        m.setPayload(contentPayload);

        // Get a reader
        MessageReader reader = V3MqttMessageFactory.createReader(m);
        Assertions.assertNotNull(reader);

       // This should fail
        // Expect an exception

        Assertions.assertThrows(EventDeserializationException.class, () -> {
            reader.toEvent();
        });

    }
}
