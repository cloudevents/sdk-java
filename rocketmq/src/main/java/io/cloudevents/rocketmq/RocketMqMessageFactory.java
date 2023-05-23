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

import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.rw.CloudEventWriter;
import java.nio.ByteBuffer;
import java.util.Map;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageView;

/**
 * A factory class providing convenience methods for creating {@link MessageReader} and {@link MessageWriter} instances
 * based on RocketMQ {@link MessageView} and {@link Message}.
 */
public class RocketMqMessageFactory {
    private RocketMqMessageFactory() {
        // prevent instantiation
    }

    /**
     * Creates a {@link MessageReader} to read a RocketMQ {@link MessageView}.
     *
     * @param message The RocketMQ {@link MessageView} to read from.
     * @return A {@link MessageReader} that can read the given {@link MessageView} to a {@link io.cloudevents.CloudEvent} representation.
     */
    public static MessageReader createReader(final MessageView message) {
        final ByteBuffer byteBuffer = message.getBody();
        byte[] body = new byte[byteBuffer.remaining()];
        byteBuffer.get(body);
        final Map<String, String> properties = message.getProperties();
        final String contentType = properties.get(RocketmqConstants.PROPERTY_CONTENT_TYPE);
        return createReader(contentType, properties, body);
    }

    /**
     * Creates a {@link MessageReader} using the content type, properties, and body of a RocketMQ {@link MessageView}.
     *
     * @param contentType The content type of the message payload.
     * @param properties  The properties of the RocketMQ message containing CloudEvent metadata (attributes and/or extensions).
     * @param body        The message body as byte array.
     * @return A {@link MessageReader} capable of parsing a {@link io.cloudevents.CloudEvent} from the content-type, properties, and payload of a RocketMQ message.
     */
    public static MessageReader createReader(final String contentType, final Map<String, String> properties, final byte[] body) {
        return MessageUtils.parseStructuredOrBinaryMessage(
            () -> contentType,
            format -> new GenericStructuredMessageReader(format, body),
            () -> properties.get(RocketmqConstants.MESSAGE_PROPERTY_SPEC_VERSION),
            sv -> new RocketmqBinaryMessageReader(sv, properties, contentType, body)
        );
    }

    /**
     * Creates a {@link MessageWriter} instance capable of translating a {@link io.cloudevents.CloudEvent} to a RocketMQ {@link Message}.
     *
     * @param topic The topic to which the created RocketMQ message will be sent.
     * @return A {@link MessageWriter} capable of converting a {@link io.cloudevents.CloudEvent} to a RocketMQ {@link Message}.
     */
    public static MessageWriter<CloudEventWriter<Message>, Message> createWriter(final String topic) {
        return new RocketmqMessageWriter(topic);
    }
}
