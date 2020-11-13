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

import java.util.Objects;
import java.util.function.BiConsumer;

import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;

import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.message.impl.BaseGenericBinaryMessageReaderImpl;

/**
 * An AMQP 1.0 message reader that can be read as a <em>CloudEvent</em>.
 * <p>
 * 
 * This reader reads sections of an AMQP message to construct a CloudEvent representation by doing the following:
 * <ul>
 *    <li> If the content-type property is set for an AMQP message, the value of the property
 *         is represented as a cloud event datacontenttype attribute.
 *    <li> If the (mandatory) application-properties of the AMQP message contains attributes and/or extentions,
 *         this reader will represent each property/extension as a cloud event attribute.
 * </ul>
 * 
 */
public final class AmqpBinaryMessageReader extends BaseGenericBinaryMessageReaderImpl<String, Object> {

    private final String contentType;
    private final ApplicationProperties applicationPropereties;

    /**
     * Create an instance of an AMQP message reader.
     *
     * @param version                  The version of the cloud event message.
     * @param applicationPropereties   The application properties of the AMQP message that contains
     *                                 the cloud event metadata (i.e attributes and extensions).
     *                                 The applicationProperties MUST not be {@code null}.
     * @param contentType              The content-type property of the AMQP message or {@null} if the message content type is unknown.
     * @param payload                  The message payload or {@code null} if the message does not contain any payload.
     * 
     * @throws NullPointerException if the applicationPropereties is {@code null}.
     */
    protected AmqpBinaryMessageReader(final SpecVersion version, final ApplicationProperties applicationPropereties, 
            final String contentType, final byte[] payload) {
        super(version, payload != null && payload.length > 0 ? new BytesCloudEventData(payload) : null);
        this.contentType = contentType;
        this.applicationPropereties = Objects.requireNonNull(applicationPropereties);
    }

    @Override
    protected boolean isContentTypeHeader(final String key) {
        return key.equals(AmqpConstants.PROPERTY_CONTENT_TYPE);
    }

    /**
     * Tests whether the given attribute key is prefixed with <em>cloudEvents:</em>
     * 
     * @param key   The key to test for the presence of the prefix.
     * @return      True if the specified key starts with the prefix or
     *              false otherwise.
     */
    @Override
    protected boolean isCloudEventsHeader(final String key) {
        final int prefixLength = AmqpConstants.CE_PREFIX.length();
        return key.length() > prefixLength && key.startsWith(AmqpConstants.CE_PREFIX);
    }

    /**
     * Gets the cloud event attribute key without the preceding prefix.
     * 
     * @param key  The key containing the AMQP specific prefix.
     * 
     * @return     The key without the prefix.
     */
    @Override
    protected String toCloudEventsKey(final String key) {
        return key.substring(AmqpConstants.CE_PREFIX.length());
    }

    /**
     * Visits the <em>content-type</em> message property and all <em>application-properties</em> 
     * of this message reader.
     * 
     * @param  fn A callback to consume this reader's application-properties
     *            and content-type property.
     */
    @Override
    protected void forEachHeader(final BiConsumer<String, Object> fn) {
        if (contentType != null) {
            // visit the content-type message property
            fn.accept(AmqpConstants.PROPERTY_CONTENT_TYPE, contentType);
        }
        // visit application-properties
        applicationPropereties.getValue().forEach((k, v) -> fn.accept(k, v));
    }

    /**
     * Gets the cloud event representation of the value.
     * <p>
     * This method simply returns the string representation of the type of value passed as argument.
     * 
     * @param value The value of a CloudEvent attribute or extension.
     * @return The string representation of the specified value.
     * 
     * @throws NullpointerException if the value is {@code null}.
     */
    @Override
    protected String toCloudEventsValue(final Object value) {
        return Objects.requireNonNull(value).toString();
    }
}
