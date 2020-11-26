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

package io.cloudevents.amqp.impl;

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.v1.CloudEventV1;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;
import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.apache.qpid.proton.message.Message;

import java.util.HashMap;

/**
 * A proton-based MessageWriter capable of writing both structured and binary CloudEvent messages to an AMQP 1.0 representation as
 * mandated by the AMQP 1.0 protocol binding specification for cloud events.
 * <p>
 * This writer returns an AMQP message at the end of the write process.
 */
public final class ProtonAmqpMessageWriter<R> implements MessageWriter<CloudEventWriter<Message>, Message>, CloudEventWriter<Message> {

    private ApplicationProperties applicationProperties;
    private Message message;

    /**
     * Creates a proton-base message writer.
     */
    public ProtonAmqpMessageWriter() {
        message = Message.Factory.create();
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException {
        if (name.equals(CloudEventV1.DATACONTENTTYPE)) {
            message.setContentType(value);
        } else {
            // for now, extensions are mapped to application-properties
            // see https://github.com/cloudevents/sdk-java/issues/30#issuecomment-723982190
            if (applicationProperties == null) {
                throw new IllegalStateException("This Writer is not initialized");
            }
            String propName = AmqpConstants.ATTRIBUTES_TO_PROPERTYNAMES.get(name);
            if (propName == null) {
                propName = name;
            }
            applicationProperties.getValue().put(propName, value);
        }
        return this;
    }

    @Override
    public ProtonAmqpMessageWriter<R> create(final SpecVersion version) {
        if (applicationProperties == null) {
            applicationProperties = new ApplicationProperties(new HashMap<>());
        }
        applicationProperties.getValue().put(AmqpConstants.APP_PROPERTY_SPEC_VERSION, version.toString());
        return this;
    }

    @Override
    public Message setEvent(final EventFormat format, final byte[] value) throws CloudEventRWException {
        message.setContentType(format.serializedContentType());
        message.setBody(new Data(new Binary(value)));
        return message;
    }

    @Override
    public Message end(final CloudEventData data) throws CloudEventRWException {
        message.setBody(new Data(new Binary(data.toBytes())));
        message.setApplicationProperties(applicationProperties);
        return message;
    }

    @Override
    public Message end() {
        message.setBody(null);
        message.setApplicationProperties(applicationProperties);
        return message;
    }
}
