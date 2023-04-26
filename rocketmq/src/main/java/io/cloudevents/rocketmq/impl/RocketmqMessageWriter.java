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

package io.cloudevents.rocketmq.impl;

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.v1.CloudEventV1;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;
import java.util.HashMap;
import java.util.Map;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageBuilder;

/**
 * The RocketmqMessageWriter class is a CloudEvents message writer for RocketMQ.
 * It allows CloudEvents attributes, context attributes, and the event payload to be populated
 * in a RocketMQ {@link Message} instance. This class implements the
 * {@link MessageWriter} interface for creating and completing CloudEvents messages in a
 * RocketMQ-compatible format.
 */
public final class RocketmqMessageWriter implements MessageWriter<CloudEventWriter<Message>, Message>, CloudEventWriter<Message> {
    private final Map<String, String> messageProperties;
    private final MessageBuilder messageBuilder;

    /**
     * Create a RocketMQ message writer.
     *
     * @param topic message's topic.
     */
    public RocketmqMessageWriter(String topic) {
        this.messageProperties = new HashMap<>();
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        this.messageBuilder = provider.newMessageBuilder();
        messageBuilder.setTopic(topic);
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException {
        if (name.equals(CloudEventV1.DATACONTENTTYPE)) {
            messageProperties.put(RocketmqConstants.PROPERTY_CONTENT_TYPE, value);
            return this;
        }
        String propertyName = RocketmqConstants.ATTRIBUTES_TO_PROPERTY_NAMES.get(name);
        if (propertyName == null) {
            propertyName = name;
        }
        messageProperties.put(propertyName, value);
        return this;
    }

    @Override
    public CloudEventWriter<Message> create(SpecVersion version) throws CloudEventRWException {
        messageProperties.put(RocketmqConstants.MESSAGE_PROPERTY_SPEC_VERSION, version.toString());
        return this;
    }

    @Override
    public Message setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
        messageProperties.put(RocketmqConstants.PROPERTY_CONTENT_TYPE, format.serializedContentType());
        messageBuilder.setBody(value);
        messageProperties.forEach(messageBuilder::addProperty);
        return messageBuilder.build();
    }

    @Override
    public Message end(CloudEventData data) throws CloudEventRWException {
        messageBuilder.setBody(data.toBytes());
        messageProperties.forEach(messageBuilder::addProperty);
        return messageBuilder.build();
    }

    @Override
    public Message end() throws CloudEventRWException {
        messageBuilder.setBody(RocketmqConstants.EMPTY_BODY);
        messageProperties.forEach(messageBuilder::addProperty);
        return messageBuilder.build();
    }
}
