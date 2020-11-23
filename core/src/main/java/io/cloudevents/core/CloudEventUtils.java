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

package io.cloudevents.core;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventContext;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.impl.CloudEventContextReaderAdapter;
import io.cloudevents.core.impl.CloudEventReaderAdapter;
import io.cloudevents.lang.Nullable;
import io.cloudevents.rw.CloudEventContextReader;
import io.cloudevents.rw.CloudEventDataMapper;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventReader;

/**
 * This class contains a set of utility methods to deal with conversions of io.cloudevents related interfaces
 */
public final class CloudEventUtils {

    private CloudEventUtils() {}

    /**
     * Convert a {@link CloudEvent} to a {@link CloudEventReader}. This method provides a default implementation
     * for CloudEvent that doesn't implement {@link CloudEventReader}
     * for CloudEvent that doesn't implement CloudEventVisitable.
     * <p>
     * It's safe to use the returned {@link CloudEventReader} multiple times.
     *
     * @param event the event to convert
     * @return the reader implementation
     */
    public static CloudEventReader toReader(CloudEvent event) {
        if (event instanceof CloudEventReader) {
            return (CloudEventReader) event;
        } else {
            return new CloudEventReaderAdapter(event);
        }
    }

    /**
     * Convert a {@link CloudEvent} to a {@link CloudEventContextReader}. This method provides a default implementation
     * for {@link CloudEvent} that doesn't implement {@link CloudEventContextReader}.
     * <p>
     * It's safe to use the returned {@link CloudEventReader} multiple times.
     *
     * @param event the event to convert
     * @return the context reader implementation
     */
    public static CloudEventContextReader toContextReader(CloudEventContext event) {
        if (event instanceof CloudEventContextReader) {
            return (CloudEventContextReader) event;
        } else {
            return new CloudEventContextReaderAdapter(event);
        }
    }

    /**
     * Convert a {@link CloudEventReader} to a {@link CloudEvent}.
     *
     * @param reader the reader where to read the message from
     * @return the reader implementation
     */
    public static CloudEvent toEvent(CloudEventReader reader) throws CloudEventRWException {
        return toEvent(reader, CloudEventDataMapper.identity());
    }

    /**
     * Convert a {@link CloudEventReader} to a {@link CloudEvent} mapping the data with the provided {@code mapper}.
     *
     * @param reader the reader where to read the message from
     * @param mapper the mapper to use when reading the data
     * @return the reader implementation
     */
    public static CloudEvent toEvent(CloudEventReader reader, CloudEventDataMapper<?> mapper) throws CloudEventRWException {
        return reader.read(CloudEventBuilder::fromSpecVersion, mapper);
    }

    /**
     * Get the data contained in {@code event} and map it using the provided mapper.
     */
    @Nullable
    public static <R extends CloudEventData> R mapData(CloudEvent event, CloudEventDataMapper<R> mapper) {
        CloudEventData data = event.getData();
        if (data == null) {
            return null;
        }
        return mapper.map(data);
    }

}
