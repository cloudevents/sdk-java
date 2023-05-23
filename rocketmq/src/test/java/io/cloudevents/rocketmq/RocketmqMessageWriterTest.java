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

package io.cloudevents.rocketmq;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.mock.CSVFormat;
import io.cloudevents.core.v1.CloudEventV1;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public class RocketmqMessageWriterTest {

    /**
     * Verifies that a binary CloudEvent message can be successfully represented
     * as a RocketMQ message.
     */
    @ParameterizedTest()
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithStringExtensions")
    public void testWriteBinaryCloudEventToRocketmqRepresentation(final CloudEvent binaryEvent) {

        String topic = "foobar";
        final Message expectedMessage = translateBinaryEvent(topic, binaryEvent);

        final MessageWriter<?, Message> writer = RocketMqMessageFactory.createWriter(topic);
        final Message actualMessage = writer.writeBinary(binaryEvent);

        assertThat(Objects.toString(actualMessage.getBody())).isEqualTo(Objects.toString(expectedMessage.getBody()));
        assertThat(actualMessage.getProperties()).isEqualTo(expectedMessage.getProperties());
    }

    /**
     * Verifies that a structured CloudEvent message (in CSV format) can be successfully represented
     * as a RocketMQ message.
     */
    @ParameterizedTest()
    @MethodSource("io.cloudevents.core.test.Data#allEventsWithoutExtensions")
    public void testWriteStructuredCloudEventToRocketmqRepresentation(final CloudEvent event) {
        final EventFormat format = CSVFormat.INSTANCE;
        final Message expectedMessage = translateStructured(event, format);

        String topic = "foobar";
        final MessageWriter<?, Message> writer = RocketMqMessageFactory.createWriter(topic);
        final Message actualMessage = writer.writeStructured(event, format.serializedContentType());

        assertThat(Objects.toString(actualMessage.getBody())).isEqualTo(Objects.toString(expectedMessage.getBody()));
        assertThat(actualMessage.getProperties()).isEqualTo(expectedMessage.getProperties());
    }

    private Message translateBinaryEvent(final String topic, final CloudEvent event) {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();

        final MessageBuilder messageBuilder = provider.newMessageBuilder();
        messageBuilder.setTopic(topic);
        messageBuilder.setBody(RocketmqConstants.EMPTY_BODY);

        final Map<String, String> map = new HashMap<>();
        if (!event.getAttributeNames().isEmpty()) {
            event.getAttributeNames().forEach(name -> {
                if (name.equals(CloudEventV1.DATACONTENTTYPE) && event.getAttribute(name) != null) {
                    map.put(RocketmqConstants.PROPERTY_CONTENT_TYPE, event.getAttribute(name).toString());
                } else {
                    addProperty(map, name, Objects.toString(event.getAttribute(name)), true);
                }
            });
        }
        if (!event.getExtensionNames().isEmpty()) {
            event.getExtensionNames().forEach(name -> addProperty(map, name, Objects.toString(event.getExtension(name)), false));
        }
        map.forEach(messageBuilder::addProperty);
        if (event.getData() != null) {
            messageBuilder.setBody(event.getData().toBytes());
        }
        return messageBuilder.build();
    }

    private Message translateStructured(final CloudEvent event, final EventFormat format) {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        final MessageBuilder messageBuilder = provider.newMessageBuilder();
        messageBuilder.setTopic("foobar");
        messageBuilder.addProperty(RocketmqConstants.PROPERTY_CONTENT_TYPE, format.serializedContentType());
        messageBuilder.setBody(format.serialize(event));
        return messageBuilder.build();
    }

    private void addProperty(final Map<String, String> map, final String name, final String value, final boolean prefix) {
        if (prefix) {
            map.put(String.format(RocketmqConstants.CE_PREFIX + "%s", name), value);
        } else {
            map.put(name, value);
        }
    }
}
