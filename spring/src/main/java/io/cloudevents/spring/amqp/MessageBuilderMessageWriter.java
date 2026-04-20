/*
 * Copyright 2020-Present The CloudEvents Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.spring.amqp;

import static io.cloudevents.spring.amqp.CloudEventsHeaders.*;

import java.util.HashMap;
import java.util.Map;

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;

/**
 * Internal utility class for copying <code>CloudEvent</code> context to {@link MessageProperties} (message
 * headers).
 *
 * @author Lars Michele
 * @see io.cloudevents.spring.messaging.MessageBuilderMessageWriter used as stencil for the implementation
 */
class MessageBuilderMessageWriter implements CloudEventWriter<Message>, MessageWriter<MessageBuilderMessageWriter, Message> {

    private final Map<String, Object> headers = new HashMap<>();

    public MessageBuilderMessageWriter(MessageProperties properties) {
        this.headers.putAll(properties.getHeaders());
    }

    @Override
    public Message setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
        headers.put(CONTENT_TYPE, format.serializedContentType());
        return MessageBuilder.withBody(value).copyHeaders(headers).build();
    }

    @Override
    public Message end(CloudEventData value) throws CloudEventRWException {
        return MessageBuilder.withBody(value == null ? new byte[0] : value.toBytes()).copyHeaders(headers).build();
    }

    @Override
    public Message end() {
        return MessageBuilder.withBody(new byte[0]).copyHeaders(headers).build();
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException {
        headers.put(CE_PREFIX + name, value);
        return this;
    }

    @Override
    public MessageBuilderMessageWriter create(SpecVersion version) {
        headers.put(SPEC_VERSION, version.toString());
        return this;
    }
}
