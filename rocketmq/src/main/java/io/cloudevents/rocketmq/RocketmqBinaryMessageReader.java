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

import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.message.impl.BaseGenericBinaryMessageReaderImpl;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A RocketMQ message reader that can be read as a <em>CloudEvent</em>.
 */
final class RocketmqBinaryMessageReader extends BaseGenericBinaryMessageReaderImpl<String, Object> {
    private final String contentType;
    private final Map<String, String> messageProperties;

    /**
     * Create an instance of an RocketMQ message reader.
     *
     * @param specVersion       The version of the cloud event message.
     * @param messageProperties The properties of the RocketMQ message that contains.
     * @param contentType       The content-type property of the RocketMQ message or {@code null} if the message content type if unknown.
     * @param body              The message payload or {@code null}/{@link RocketmqConstants#EMPTY_BODY} if the message does not contain any payload.
     */
    RocketmqBinaryMessageReader(final SpecVersion specVersion, Map<String, String> messageProperties,
        final String contentType, final byte[] body) {
        super(specVersion, body != null && !Arrays.equals(RocketmqConstants.EMPTY_BODY, body) && body.length > 0 ? BytesCloudEventData.wrap(body) : null);
        this.contentType = contentType;
        this.messageProperties = messageProperties;
    }

    @Override
    protected boolean isContentTypeHeader(String key) {
        return key.equals(RocketmqConstants.PROPERTY_CONTENT_TYPE);
    }

    /**
     * Tests whether the given property key belongs to cloud events headers.
     *
     * @param key The key to test for.
     * @return True if the specified key belongs to cloud events headers.
     */
    @Override
    protected boolean isCloudEventsHeader(String key) {
        final int prefixLength = RocketmqConstants.CE_PREFIX.length();
        return key.length() > prefixLength && key.startsWith(RocketmqConstants.CE_PREFIX);
    }

    /**
     * Transforms a RocketMQ message property key into a CloudEvents attribute or extension key.
     * <p>
     * This method removes the {@link RocketmqConstants#CE_PREFIX} prefix from the given key,
     * assuming that the key has already been determined to be a CloudEvents header by
     * {@link #isCloudEventsHeader(String)}.
     *
     * @param key The RocketMQ message property key with the CloudEvents header prefix.
     * @return The CloudEvents attribute or extension key without the prefix.
     */
    @Override
    protected String toCloudEventsKey(String key) {
        return key.substring(RocketmqConstants.CE_PREFIX.length());
    }

    @Override
    protected void forEachHeader(BiConsumer<String, Object> fn) {
        if (contentType != null) {
            // visit the content-type message property
            fn.accept(RocketmqConstants.PROPERTY_CONTENT_TYPE, contentType);
        }
        // visit message properties
        messageProperties.forEach((k, v) -> {
            if (k != null && v != null) {
                fn.accept(k, v);
            }
        });
    }

    /**
     * Gets the cloud event representation of the value.
     * <p>
     * This method simply returns the string representation of the type of value passed as argument.
     *
     * @param value The value of a CloudEvent attribute or extension.
     * @return The string representation of the specified value.
     */
    @Override
    protected String toCloudEventsValue(Object value) {
        return value.toString();
    }
}
