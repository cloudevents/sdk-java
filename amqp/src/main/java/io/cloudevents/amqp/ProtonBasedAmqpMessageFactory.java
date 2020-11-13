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


import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.apache.qpid.proton.message.Message;

import io.cloudevents.amqp.impl.ProtonBasedAmqpBinaryMessageReader;
import io.cloudevents.amqp.impl.ProtonBasedAmqpMessageWriter;
import io.cloudevents.amqp.impl.AmqpConstants;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.core.message.impl.UnknownEncodingMessageReader;
import io.cloudevents.rw.CloudEventWriter;
/**
 * A factory class providing convenience methods for creating MessageReader and MessageWriter instances based on
 * {@link <a href="https://github.com/apache/qpid-proton-j">Qpid Proton</a>}.
 */
@ParametersAreNonnullByDefault
public final class ProtonBasedAmqpMessageFactory {

    private ProtonBasedAmqpMessageFactory() {
        // prevent instantiation
    }

    /**
     * Creates a MessageReader to read a proton-based {@link Message}.
     * <p>
     * This implementation simply calls {@link #createReader(String, ApplicationProperties, byte[])}.
     * 
     * @param message The proton message to read from.
     * 
     * @return        A message reader that can read the given proton message to a cloud event representation.
     */
    public static MessageReader createReader(final Message message) {

        final byte[] payload = AmqpConstants.getPayloadAsByteArray(message.getBody());
        return createReader(message.getContentType(), message.getApplicationProperties(), payload);
    }

    /**
     * Creates a MessageReader using the content-type property and payload of a proton-based message.
     * <p>
     * This method simply calls {@link #createReader(String, ApplicationProperties, byte[])}.
     * 
     * @param contentType The content-type of the message payload.
     * @param payload     The message payload in bytes.
     * @return            A message reader capable of representing a CloudEvent from
     *                    a message <em>content-type</em> property and <em>application-data</em>.
     */
    public static MessageReader createReader(final String contentType, final byte[] payload) {
        return createReader(contentType, null, payload);
    }

    /**
     * Creates a MessageWriter capable of translating both a structured and binary CloudEvent
     * to a proton-based AMQP 1.0 representation.
     * 
     * @return A message writer to read structured and binary cloud event from a proton-based message.
     */
    public static MessageWriter<CloudEventWriter<Message>, Message> createWriter() {
        return new ProtonBasedAmqpMessageWriter<>();
    }

    /**
     * Creates a MessageReader to read using the content-type property, application-propeties and data payload
     * of a proton-based message.
     * 
     * @param contentType  The content-type of the message payload.
     * @param props        The application-properties section of the proton-message containing cloud event metadata (attributes and/or extensions).
     * @param payload      The message payload in bytes or {@code null} if the message does not contain any payload.
     * @return             A message reader capable of representing a CloudEvent from the application-properties,
     *                     content-type and payload of a proton message.
     */
    public static MessageReader createReader(final String contentType, final ApplicationProperties props, final byte[] payload) {

        return MessageUtils.parseStructuredOrBinaryMessage(
                () -> contentType, 
                format -> new GenericStructuredMessageReader(format, payload),
                () -> AmqpConstants.getApplicationProperty(props, AmqpConstants.APP_PROPERTY_SPEC_VERSION, String.class),
                sv -> new ProtonBasedAmqpBinaryMessageReader(sv, props, contentType, payload),
                UnknownEncodingMessageReader::new);

    }
    
}
