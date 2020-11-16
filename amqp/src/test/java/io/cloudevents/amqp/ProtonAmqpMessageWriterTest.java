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

package io.cloudevents.amqp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.apache.qpid.proton.message.Message;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.cloudevents.CloudEvent;
import io.cloudevents.amqp.impl.ProtonAmqpMessageWriter;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.mock.CSVFormat;

/**
 * Tests verifying the behavior of the {@code ProtonAmqpMessageWriter}.
 */
public class ProtonAmqpMessageWriterTest {

    /**
     * Verifies that a binary CloudEvent message can be successfully represented
     * as an AMQP message.
     */
    @ParameterizedTest()
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithStringExtensions")
    public void testWriteBinaryCloudEventToAmqpRepresentation(final CloudEvent binaryEvent) {

        final Message expectedMessage = translateBinaryEvent(binaryEvent);

        final MessageWriter<?, Message> writer = new ProtonAmqpMessageWriter<Message>();
        final Message actualMessage = writer.writeBinary(binaryEvent);

        assertThat(actualMessage.getContentType()).isEqualTo(expectedMessage.getContentType());
        assertThat(Objects.toString(actualMessage.getBody())).isEqualTo(Objects.toString(expectedMessage.getBody()));
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

        final MessageWriter<?, Message> writer = new ProtonAmqpMessageWriter<Message>();
        final Message actualMessage = writer.writeStructured(event, format.serializedContentType());

        assertThat(actualMessage.getContentType()).isEqualTo(expectedMessage.getContentType());
        assertThat(Objects.toString(actualMessage.getBody())).isEqualTo(Objects.toString(expectedMessage.getBody()));
        assertThat(actualMessage.getApplicationProperties()).isNull();
    }

    private Message translateBinaryEvent(final CloudEvent event) {

        final Message message = Message.Factory.create();

        final Map<String, Object> map = new HashMap<>();

        if (!event.getAttributeNames().isEmpty()) {
            event.getAttributeNames().forEach(name -> {
                if (name.equals("datacontentencoding")) {
                    // ignore
                } else if (name.equals("datacontenttype") && event.getAttribute(name) != null) {
                    message.setContentType(event.getAttribute(name).toString());
                } else {
                    addProperty(map, name, Objects.toString(event.getAttribute(name)), true);
                }
            });
        }
        if (!event.getExtensionNames().isEmpty()) {
            event.getExtensionNames().forEach(name -> addProperty(map, name, Objects.toString(event.getExtension(name)), false));
        }

        if (event.getData() != null) {
            final Section payload = new org.apache.qpid.proton.amqp.messaging.Data(new Binary(event.getData().toBytes()));
            message.setBody(payload);
        }

        message.setApplicationProperties(new ApplicationProperties(map));
        return message;
    }

    private Message translateStructured(final CloudEvent event, final EventFormat format) {
        final Message message = Message.Factory.create();
        message.setContentType(format.serializedContentType());
        message.setBody(new org.apache.qpid.proton.amqp.messaging.Data(new Binary(format.serialize(event))));
        return message;
    }
    private void addProperty(final Map<String, Object> map, final String name, final String value, final boolean prefix) {
        if (prefix) {
            map.put(String.format("cloudEvents:%s", name), value);
        } else {
            map.put(name, value);
        }
    }
}
