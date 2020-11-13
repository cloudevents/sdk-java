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

import java.util.HashMap;

import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.apache.qpid.proton.message.Message;

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.rw.CloudEventAttributesWriter;
import io.cloudevents.rw.CloudEventExtensionsWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;

/**
 * A binary mode MessageWriter capable of writing a CloudEvent message to an AMQP 1.0 representation as
 * mandated by the AMQP 1.0 protocol binding specification for cloud events.
 * <p>
 * This writer returns an AMQP message at the end of the write process.
 */
public class AmqpBinaryMessageWriter implements MessageWriter<AmqpBinaryMessageWriter, Message>, CloudEventWriter<Message> {

    private ApplicationProperties applicationProperties;
    private Message message;

    public AmqpBinaryMessageWriter() {
        message = Message.Factory.create();
    }

    @Override
    public CloudEventAttributesWriter withAttribute(String name, String value) throws CloudEventRWException {
        if (name.equals("datacontenttype")) {
            message.setContentType(value);
        } else {
            if (applicationProperties == null) {
                throw new IllegalStateException("This Writer is not initialized");
            }
            applicationProperties.getValue().put(AmqpConstants.ATTRIBUTES_TO_PROPERTYNAMES.get(name), value);
        }
        return null;
    }

    @Override
    public CloudEventExtensionsWriter withExtension(String name, String value) throws CloudEventRWException {
        // TODO: for now, extensions are mapped to application-properties
        if (applicationProperties == null) {
            throw new IllegalStateException("This Writer is not initialized");
        }
        applicationProperties.getValue().put(name, value);
        return null;
    }

    @Override
    public AmqpBinaryMessageWriter create(SpecVersion version) {
        if (applicationProperties == null) {
            applicationProperties = new ApplicationProperties(new HashMap<String, Object>());
        }
        applicationProperties.getValue().put(AmqpConstants.APP_PROPERTY_SPEC_VERSION, version.toString());
        return this;
    }

    @Override
    public Message setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
        message.setContentType(format.serializedContentType());
        return this.end(new BytesCloudEventData(value));
    }

    @Override
    public Message end(CloudEventData data) throws CloudEventRWException {
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
