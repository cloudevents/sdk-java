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

package io.cloudevents.core.impl;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.lang.Nullable;
import io.cloudevents.rw.CloudEventDataMapper;
import io.cloudevents.rw.CloudEventReader;

public final class CloudEventUtils {

    private CloudEventUtils() {}

    /**
     * Convert a {@link CloudEvent} to a {@link CloudEventReader}. This method provides a default implementation
     * for CloudEvent that doesn't implement CloudEventVisitable
     *
     * @param event the event to convert
     * @return the visitable implementation
     */
    public static CloudEventReader toVisitable(CloudEvent event) {
        if (event instanceof CloudEventReader) {
            return (CloudEventReader) event;
        } else {
            return new CloudEventReaderAdapter(event);
        }
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
