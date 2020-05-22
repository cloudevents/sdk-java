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

package io.cloudevents.message.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventVisitException;
import io.cloudevents.format.EventFormat;
import io.cloudevents.format.EventFormatProvider;
import io.cloudevents.lang.Nullable;
import io.cloudevents.message.StructuredMessageVisitor;

public class GenericStructuredMessage extends BaseStructuredMessage {

    private EventFormat format;
    private byte[] payload;

    public GenericStructuredMessage(EventFormat format, byte[] payload) {
        this.format = format;
        this.payload = payload;
    }

    @Override
    public <T> T visit(StructuredMessageVisitor<T> visitor) throws CloudEventVisitException, IllegalStateException {
        return visitor.setEvent(format, payload);
    }

    /**
     * Create a generic structured message from a payload
     *
     * @param contentType content type to use to resolve the {@link EventFormat}
     * @param payload     serialized event
     * @return null if format was not found, otherwise returns the built message
     */
    public static GenericStructuredMessage fromContentType(String contentType, byte[] payload) {
        EventFormat format = EventFormatProvider.getInstance().resolveFormat(contentType);
        if (format == null) {
            return null;
        }

        return new GenericStructuredMessage(format, payload);
    }

    /**
     * Create a generic structured message from a {@link CloudEvent}
     *
     * @param contentType content type to use to resolve the {@link EventFormat}
     * @param event
     * @return null if format was not found, otherwise returns the built message
     */
    @Nullable
    public static GenericStructuredMessage fromEvent(String contentType, CloudEvent event) {
        EventFormat format = EventFormatProvider.getInstance().resolveFormat(contentType);
        if (format == null) {
            return null;
        }

        return fromEvent(format, event);
    }

    /**
     * Create a generic structured message from a {@link CloudEvent}
     *
     * @param format
     * @param event
     * @return returns the built message
     */
    public static GenericStructuredMessage fromEvent(EventFormat format, CloudEvent event) {
        return new GenericStructuredMessage(format, format.serialize(event));
    }
}
