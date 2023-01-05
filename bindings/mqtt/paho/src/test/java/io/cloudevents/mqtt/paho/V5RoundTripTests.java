package io.cloudevents.mqtt.paho;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.mock.CSVFormat;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Round-Trip Tests
 * <p>
 * For both Binary and Structured modes:
 * - serialize a CloudEvent into an MQTT Message.
 * - de-serialize the message into a new CloudEvent
 * - verify that the new CE matches the original CE
 */
public class V5RoundTripTests {

    private static void readAndVerify(CloudEvent ce, MqttMessage message) {

        Assertions.assertNotNull(message);

        // Read the message back into an event
        MessageReader reader = V5MqttMessageFactory.createReader(message);
        Assertions.assertNotNull(reader);

        CloudEvent newCE = reader.toEvent();
        Assertions.assertNotNull(newCE);

        // And now ensure we got back what we wrote
        Assertions.assertEquals(ce, newCE);
    }

    @ParameterizedTest
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    public void roundTripBinary(CloudEvent ce) {

        // Write the event out as a message.
        MessageWriter writer = V5MqttMessageFactory.createWriter();
        Assertions.assertNotNull(writer);

        MqttMessage message = (MqttMessage) writer.writeBinary(ce);

        // Read it back and verify
        readAndVerify(ce, message);
    }

    @ParameterizedTest
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    public void roundTripStructured(CloudEvent ce) {

        EventFormat format = CSVFormat.INSTANCE;

        // Write the event out as a message.
        MessageWriter writer = V5MqttMessageFactory.createWriter();
        Assertions.assertNotNull(writer);

        MqttMessage message = (MqttMessage) writer.writeStructured(ce, format);

        // Read it back and verify
        readAndVerify(ce, message);

    }


}
