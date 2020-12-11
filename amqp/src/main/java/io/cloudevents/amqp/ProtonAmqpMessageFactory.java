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

import io.cloudevents.SpecVersion;
import io.cloudevents.amqp.impl.AmqpConstants;
import io.cloudevents.amqp.impl.ProtonAmqpBinaryMessageReader;
import io.cloudevents.amqp.impl.ProtonAmqpMessageWriter;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.lang.Nullable;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;
import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.apache.qpid.proton.message.Message;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A factory class providing convenience methods for creating {@link MessageReader} and {@link MessageWriter} instances based on Qpid Proton {@link Message}.
 */
@ParametersAreNonnullByDefault
public final class ProtonAmqpMessageFactory {

    private ProtonAmqpMessageFactory() {
        // prevent instantiation
    }

    /**
     * Creates a {@link MessageReader} to read a proton-based {@link Message}.
     * This reader is able to read both binary and structured encoded {@link io.cloudevents.CloudEvent}.
     *
     * @param message The proton {@link Message} to read from.
     * @return A {@link MessageReader} that can read the given proton {@link Message} to a {@link io.cloudevents.CloudEvent} representation.
     * @throws CloudEventRWException if something goes wrong while resolving the {@link SpecVersion} or if the message has unknown encoding
     * @see #createReader(String, ApplicationProperties, Section)
     */
    public static MessageReader createReader(final Message message) throws CloudEventRWException {
        return createReader(message.getContentType(), message.getApplicationProperties(), message.getBody());
    }

    /**
     * Creates a MessageReader to read using the {@code content-type} property, {@code application-properties} and data payload
     * of a proton-based {@link Message}. This reader is able to read both binary and structured encoded {@link io.cloudevents.CloudEvent}.
     *
     * @param contentType The {@code content-type} of the message payload.
     * @param props       The {@code application-properties} section of the proton-message containing cloud event metadata (attributes and/or extensions).
     * @param body        The message body or {@code null} if the message does not contain any body.
     * @return A {@link MessageReader} capable of representing a {@link io.cloudevents.CloudEvent} from the {@code application-properties},
     * {@code content-type} and payload of a proton message.
     * @throws CloudEventRWException if something goes wrong while resolving the {@link SpecVersion} or if the message has unknown encoding
     */
    public static MessageReader createReader(final String contentType, final ApplicationProperties props, @Nullable final Section body) throws CloudEventRWException {
        final byte[] payload = AmqpConstants.getPayloadAsByteArray(body);
        return MessageUtils.parseStructuredOrBinaryMessage(
            () -> contentType,
            format -> new GenericStructuredMessageReader(format, payload),
            () -> AmqpConstants.getApplicationProperty(props, AmqpConstants.APP_PROPERTY_SPEC_VERSION, String.class),
            sv -> new ProtonAmqpBinaryMessageReader(sv, props, contentType, payload)
        );
    }

    /**
     * Creates a {@link MessageWriter} capable of translating both a structured and binary CloudEvent
     * to a proton-based AMQP 1.0 {@link Message}.
     *
     * @return A {@link MessageWriter} to write a {@link io.cloudevents.CloudEvent} to Proton {@link Message} using structured or binary encoding.
     */
    public static MessageWriter<CloudEventWriter<Message>, Message> createWriter() {
        return new ProtonAmqpMessageWriter<>();
    }

}
