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

package io.cloudevents.core.message.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.StructuredMessageWriter;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.lang.Nullable;
import io.cloudevents.rw.CloudEventRWException;

public class GenericStructuredMessageReader extends BaseStructuredMessageReader {

    private final EventFormat format;
    private final byte[] payload;

    public GenericStructuredMessageReader(EventFormat format, byte[] payload) {
        this.format = format;
        this.payload = payload;
    }

    @Override
    public <T> T read(StructuredMessageWriter<T> visitor) throws CloudEventRWException, IllegalStateException {
        return visitor.setEvent(format, payload);
    }

    /**
     * Create a generic structured message from a payload
     *
     * @param contentType content type to use to resolve the {@link EventFormat}
     * @param payload     serialized event
     * @return null if format was not found, otherwise returns the built message
     */
    public static GenericStructuredMessageReader fromContentType(String contentType, byte[] payload) {
        EventFormat format = EventFormatProvider.getInstance().resolveFormat(contentType);
        if (format == null) {
            return null;
        }

        return new GenericStructuredMessageReader(format, payload);
    }

    /**
     * Create a generic structured message from a {@link CloudEvent}
     *
     * @param event
     * @param contentType content type to use to resolve the {@link EventFormat}
     * @return null if format was not found, otherwise returns the built message
     */
    @Nullable
    public static GenericStructuredMessageReader from(CloudEvent event, String contentType) {
        EventFormat format = EventFormatProvider.getInstance().resolveFormat(contentType);
        if (format == null) {
            return null;
        }

        return from(event, format);
    }

    /**
     * Create a generic structured message from a {@link CloudEvent}
     *
     * @param event
     * @param format
     * @return returns the built message
     */
    public static GenericStructuredMessageReader from(CloudEvent event, EventFormat format) {
        return new GenericStructuredMessageReader(format, format.serialize(event));
    }
}
