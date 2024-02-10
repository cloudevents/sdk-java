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

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventContext;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * A {@link MessageConverter} that can translate to and from a {@link Message} and a {@link CloudEvent}.
 * The {@link CloudEventContext} is canonicalized, with key names given a {@code ce-} prefix in the
 * {@link MessageProperties}.
 *
 * @author Lars Michele
 * @see io.cloudevents.spring.messaging.CloudEventMessageConverter used as stencil for the implementation
 */
public class CloudEventMessageConverter implements MessageConverter {

    @Override
    public CloudEvent fromMessage(Message message) {
        return createMessageReader(message).toEvent();
    }

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) {
        if (object instanceof CloudEvent) {
            CloudEvent event = (CloudEvent) object;
            return CloudEventUtils.toReader(event).read(new MessageBuilderMessageWriter(messageProperties));
        }
        return null;
    }

    private MessageReader createMessageReader(Message message) {
        return MessageUtils.parseStructuredOrBinaryMessage(
            () -> contentType(message.getMessageProperties()),
            format -> structuredMessageReader(message, format),
            () -> version(message.getMessageProperties()),
            version -> binaryMessageReader(message, version)
        );
    }

    private String version(MessageProperties properties) {
        Object header = properties.getHeader(CloudEventsHeaders.SPEC_VERSION);
        return header == null ? null : header.toString();
    }

    private MessageReader binaryMessageReader(Message message, SpecVersion version) {
        return new MessageBinaryMessageReader(version, message.getMessageProperties(), message.getBody());
    }

    private MessageReader structuredMessageReader(Message message, EventFormat format) {
        return new GenericStructuredMessageReader(format, message.getBody());
    }

    private String contentType(MessageProperties properties) {
        String contentType = properties.getContentType();
        if (contentType == null) {
            Object header = properties.getHeader(CloudEventsHeaders.CONTENT_TYPE);
            return header == null ? null : header.toString();
        }
        return contentType;
    }
}
