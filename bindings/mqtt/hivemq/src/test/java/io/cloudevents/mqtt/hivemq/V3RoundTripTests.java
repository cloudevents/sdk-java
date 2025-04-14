/*
 * Copyright 2018-Present The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.cloudevents.mqtt.hivemq;

import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3PublishBuilder;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.test.Data;
import io.cloudevents.jackson.JsonFormat;
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


    /**
     * This test set is limited owing to the fact that:
     * (a) We only support JSON Format
     * (b) Round-tripping of events with JSON 'data' doesn't reliably work owing to the way the equality tests work on the event.
     *
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

    @ParameterizedTest
    @MethodSource("simpleEvents")
    public void roundTrip(CloudEvent ce) {

        EventFormat format = new JsonFormat();
        Assertions.assertNotNull(format);

        Mqtt3Publish message = null;
        Mqtt3PublishBuilder.Complete builder = (Mqtt3PublishBuilder.Complete) Mqtt3Publish.builder();
        builder.topic("test.test.test");

        // Write the event out as a message.
        MessageWriter writer = MqttMessageFactory.createWriter(builder);
        Assertions.assertNotNull(writer);

        writer.writeStructured(ce, format);
        message = builder.build();

        Assertions.assertNotNull(message);

        // Read it back and verify

        // Read the message back into an event
        MessageReader reader = MqttMessageFactory.createReader(message);
        Assertions.assertNotNull(reader);

        CloudEvent newCE = reader.toEvent();
        Assertions.assertNotNull(newCE);

        // And now ensure we got back what we wrote
        Assertions.assertEquals(ce, newCE);

    }

}
