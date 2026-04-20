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
package io.cloudevents.mqtt.paho;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventDeserializationException;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.test.Data;
import io.cloudevents.rw.CloudEventRWException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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
