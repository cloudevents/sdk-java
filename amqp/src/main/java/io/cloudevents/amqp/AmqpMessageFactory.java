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

import java.nio.charset.StandardCharsets;

import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.apache.qpid.proton.message.Message;

import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.core.message.impl.UnknownEncodingMessageReader;

public class AmqpMessageFactory {

    public static MessageReader createReader(final Message message) {

        final byte[] payload = getPayloadAsByteArray(message.getBody());
        return createReader(message.getContentType(), message.getApplicationProperties(), payload);
    }

    public static MessageReader createReader(final String contentType, final byte[] payload) {
        return createReader(contentType, null, payload);
    }

    public static MessageReader createReader(final String contentType, final ApplicationProperties props, final byte[] payload) {

        return MessageUtils.parseStructuredOrBinaryMessage(
                () -> contentType, 
                format -> new GenericStructuredMessageReader(format, payload),
                () -> getApplicationProperty(props, AmqpConstants.APP_PROPERTY_SPEC_VERSION, String.class),
                sv -> new AmqpBinaryMessageReader(sv, props, contentType, payload),
                UnknownEncodingMessageReader::new);

    }
    
    //------------------------------< private methods >---
    /**
     * Gets the value of a specific <em>application property</em>.
     *
     * @param <T> The expected type of the property to retrieve.
     * @param props The application properties to retrieve the value from.
     * @param name The name of the application property.
     * @param type The expected value type.
     * @return The value or {@code null} if the properties do not contain a value of the expected type for the given
     *         name.
     */
    @SuppressWarnings("unchecked")
    private static <T> T getApplicationProperty(final ApplicationProperties props, final String name,
            final Class<T> type) {

        if (props == null) {
            return null;
        } else {
            final Object value = props.getValue().get(name);
            if (type.isInstance(value)) {
                return (T) value;
            } else {
                return null;
            }
        }
    }

    private static byte[] getPayloadAsByteArray(final Section payload) {
        if (payload == null) {
            return null;
        }

        if (payload instanceof Data) {
            final Data body = (Data) payload;
            return body.getValue().getArray();
        } else if (payload instanceof AmqpValue) {
            final AmqpValue body = (AmqpValue) payload;
            if (body.getValue() instanceof byte[]) {
                return (byte[]) body.getValue();
            } else if (body.getValue() instanceof String) {
                return ((String) body.getValue()).getBytes(StandardCharsets.UTF_8);
            }
        }

        return null;
    }

}
