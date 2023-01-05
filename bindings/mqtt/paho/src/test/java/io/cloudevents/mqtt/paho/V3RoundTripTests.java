package io.cloudevents.mqtt.paho;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.core.test.Data;
import io.cloudevents.jackson.JsonFormat;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Round-Trip Tests
 * <p>
 * - serialize a CloudEvent into an MQTT Message.
 * - de-serialize the message into a new CloudEvent
 * - verify that the new CE matches the original CE
 */
public class V3RoundTripTests {


    @ParameterizedTest
    @MethodSource("simpleEvents")
    public void roundTrip(CloudEvent ce) {

        EventFormat format = new JsonFormat();
        Assertions.assertNotNull(format);

        // Write the event out as a message.
        MessageWriter writer = V3MqttMessageFactory.createWriter();
        Assertions.assertNotNull(writer);

        MqttMessage message = (MqttMessage) writer.writeStructured(ce, format);
        Assertions.assertNotNull(message);

        // Read it back and verify

        // Read the message back into an event
        MessageReader reader = V3MqttMessageFactory.createReader(message);
        Assertions.assertNotNull(reader);

        CloudEvent newCE = reader.toEvent();
        Assertions.assertNotNull(newCE);

        // And now ensure we got back what we wrote
        Assertions.assertEquals(ce, newCE);

    }

    /**
     * This test set is limited owing to the the fact that:
     *  (a) We only support JSON Format
     *  (b) Round-tripping of events with JSON 'data' doesn't reliably work owing to the way the equality tests work on the event.
     * @return
     */
    static Stream<CloudEvent> simpleEvents() {
        return Stream.of(
            Data.V03_MIN,
            Data.V03_WITH_TEXT_DATA,
            Data.V1_MIN,
            Data.V1_WITH_TEXT_DATA,
            Data.V1_WITH_XML_DATA
        );
    }

}
