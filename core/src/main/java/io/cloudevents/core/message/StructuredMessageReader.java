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

package io.cloudevents.core.message;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.rw.CloudEventDataMapper;
import io.cloudevents.rw.CloudEventRWException;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Represents a <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#message">CloudEvent message</a> in structured mode.
 */
@FunctionalInterface
@ParametersAreNonnullByDefault
public interface StructuredMessageReader {

    /**
     * @param visitor
     * @throws CloudEventRWException If something went wrong when
     * @throws IllegalStateException If the message is not a valid structured message
     */
    <T> T read(StructuredMessageWriter<T> visitor) throws CloudEventRWException, IllegalStateException;

    default CloudEvent toEvent() throws CloudEventRWException, IllegalStateException {
        return this.read(EventFormat::deserialize);
    }

    default CloudEvent toEvent(CloudEventDataMapper<? extends CloudEventData> mapper) throws CloudEventRWException, IllegalStateException {
        return this.read((format, value) -> format.deserialize(value, mapper));
    }

    /**
     * Create a generic structured message from a {@link CloudEvent}
     *
     * @param event
     * @param contentType content type to use to resolve the {@link EventFormat}
     * @return null if format was not found, otherwise returns the built message
     */
    static StructuredMessageReader from(CloudEvent event, String contentType) {
        return GenericStructuredMessageReader.from(event, contentType);
    }

    /**
     * Create a generic structured message from a {@link CloudEvent}
     *
     * @param event
     * @param format
     * @return null if format was not found, otherwise returns the built message
     */
    static StructuredMessageReader from(CloudEvent event, EventFormat format) {
        return GenericStructuredMessageReader.from(event, format);
    }

}
