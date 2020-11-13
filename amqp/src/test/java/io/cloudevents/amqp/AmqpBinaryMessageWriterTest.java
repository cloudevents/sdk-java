/*
 * Copyright 2020-Present The CloudEvents Authors
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

package io.cloudevents.amqp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.apache.qpid.proton.message.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.test.Data;
import io.cloudevents.types.Time;

/**
 * Tests verifying the behavior of the {@code AmqpBinaryMessageWriter}.
 */
public class AmqpBinaryMessageWriterTest {

    /**
     * Verifies that a binary CloudEvent message can be successfully represented
     * as an AMQP message.
     */
    @Test
    public void testWriteBinaryCloudEventToAmqpRepresentation() {

        final CloudEvent binaryEvent = Data.V1_WITH_JSON_DATA;
        final Message expectedMessage = translateBinaryEvent(binaryEvent);

        final AmqpBinaryMessageWriter writer = new AmqpBinaryMessageWriter();
        final Message actualMessage = writer.writeBinary(binaryEvent);

        assertThat(actualMessage.getContentType()).isEqualTo(expectedMessage.getContentType());
        assertThat(actualMessage.getBody().toString()).isEqualTo(expectedMessage.getBody().toString());
        assertThat(actualMessage.getApplicationProperties().getValue()).
                  isEqualTo(expectedMessage.getApplicationProperties().getValue());
    }

    /**
     * Verifies that a structured CloudEvent message (in CSV format) can be successfully represented
     * as an AMQP message.
     */
    @ParameterizedTest()
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    public void testWriteStructuredCloudEventToAmqpRepresentation(final CloudEvent event) {
        final EventFormat format = CSVFormat.INSTANCE;
        final Message expectedMessage = translateStructured(event, format);

        final AmqpBinaryMessageWriter writer = new AmqpBinaryMessageWriter();
        final Message actualMessage = writer.writeStructured(event, format.serializedContentType());

        assertThat(actualMessage.getContentType()).isEqualTo(expectedMessage.getContentType());
        assertThat(actualMessage.getBody().toString()).isEqualTo(expectedMessage.getBody().toString());
        assertThat(actualMessage.getApplicationProperties()).isNull();
    }

    private Message translateBinaryEvent(final CloudEvent event) {

        final Message message = Message.Factory.create();
        if (event.getAttribute("datacontenttype") != null) {
            message.setContentType(event.getAttribute("datacontenttype").toString());
        }

        final Map<String, Object> map = new HashMap<>();
        map.put("cloudEvents:id", event.getId());
        map.put("cloudEvents:source", event.getSource().toString());
        map.put("cloudEvents:specversion", event.getSpecVersion().toString());
        map.put("cloudEvents:type", event.getType());
        map.put("cloudEvents:subject", event.getSubject());
        map.put("cloudEvents:time", Time.writeTime(event.getTime()));
        map.put("cloudEvents:dataschema", event.getDataSchema().toString());

        message.setApplicationProperties(new ApplicationProperties(map));
        final Section payload = new org.apache.qpid.proton.amqp.messaging.Data(new Binary(event.getData().toBytes()));
        message.setBody(payload);
        return message;
    }

    private Message translateStructured(final CloudEvent event, final EventFormat format) {
        final Message message = Message.Factory.create();
        message.setContentType(format.serializedContentType());
        message.setBody(new org.apache.qpid.proton.amqp.messaging.Data(new Binary(format.serialize(event))));
        return message;
    }
}
