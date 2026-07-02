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

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.apache.qpid.proton.amqp.messaging.Section;

import io.cloudevents.core.message.impl.MessageUtils;

/**
 * Constants and methods used throughout the Proton-based implementation of the AMQP 1.0 protocol
 * binding for cloud events.
 */
public final class AmqpConstants {

    private AmqpConstants() {
        // prevent instantiation
    }

	/**
	 * The prefix name for CloudEvent attributes for use in the <em>application-properties</em> section
	 * of an AMQP 1.0 message.
	 */
	public static final String CE_PREFIX = "cloudEvents:";

	/**
	 * The AMQP 1.0 <em>content-type</em> message property
	 */
	public static final String PROPERTY_CONTENT_TYPE = "content-type";

	/**
	 * Map a cloud event attribute name to a value. All values except the <em>datacontenttype</em> attribute are prefixed
	 * with "cloudEvents:" as mandated by the spec.
	 */
	public static final Map<String, String> ATTRIBUTES_TO_PROPERTYNAMES = MessageUtils.generateAttributesToHeadersMapping(CEA -> {
		if (CEA.equals("datacontenttype")) {
			return PROPERTY_CONTENT_TYPE;
		}
		// prefix the attribute
		return CE_PREFIX + CEA;
	});

	public static final String APP_PROPERTY_SPEC_VERSION = ATTRIBUTES_TO_PROPERTYNAMES.get("specversion");

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
    public static <T> T getApplicationProperty(final ApplicationProperties props, final String name,
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

    /**
     * Parses a message payload into a byte array.
     * <p>
     * The bytes in the array are determined as follows:
     * <ul>
     * <li>If the body is a Data section, the bytes contained in the
     * Data section are returned.</li>
     * <li>If the body is an AmqpValue section and contains a byte array,
     * the bytes in the array are returned.</li>
     * <li>If the body is an AmqpValue section and contains a non-empty String,
     * the UTF-8 encoding of the String is returned.</li>
     * <li>In all other cases, {@code null} is returned.</li>
     * </ul>

     * @param payload  The message payload to extract the bytes from.
     * @return         The payload bytes or {@code null} if the above stated conditions are not met.
     */
    public static byte[] getPayloadAsByteArray(final Section payload) {
        if (payload == null) {
            return null;
        }

        if (payload instanceof Data body) {
            return body.getValue().getArray();
        } else if (payload instanceof AmqpValue body) {
            if (body.getValue() instanceof byte[]) {
                return (byte[]) body.getValue();
            } else if (body.getValue() instanceof String &&
                    ((String) body.getValue()).length() > 0 ) {
                return ((String) body.getValue()).getBytes(StandardCharsets.UTF_8);
            }
        }

        return null;
    }

}
