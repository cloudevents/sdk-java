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

package io.cloudevents.core.format;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.rw.CloudEventDataMapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Set;

/**
 * An <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#event-format">Event format</a>
 * specifies how to serialize a CloudEvent as a sequence of bytes.
 * <p>
 * An implementation of this interface should support all specification versions of {@link CloudEvent}.
 * <p>
 * Implementations of this interface can be registered to the {@link io.cloudevents.core.provider.EventFormatProvider} to use them.
 *
 * @see io.cloudevents.core.provider.EventFormatProvider
 */
@ParametersAreNonnullByDefault
public interface EventFormat {

    /**
     * Serialize a {@link CloudEvent} to a byte array.
     *
     * @param event the event to serialize.
     * @return the byte representation of the provided event.
     * @throws EventSerializationException if something goes wrong during serialization.
     */
    byte[] serialize(CloudEvent event) throws EventSerializationException;

    /**
     * Deserialize a byte array to a {@link CloudEvent}.
     *
     * @param bytes the serialized event.
     * @return the deserialized event.
     * @throws EventDeserializationException if something goes wrong during deserialization.
     */
    default CloudEvent deserialize(byte[] bytes) throws EventDeserializationException {
        return this.deserialize(bytes, null);
    }

    /**
     * Like {@link EventFormat#deserialize(byte[])}, but allows a mapper that maps the parsed {@link io.cloudevents.CloudEventData} to another one.
     */
    CloudEvent deserialize(byte[] bytes, CloudEventDataMapper<? extends CloudEventData> mapper) throws EventDeserializationException;

    /**
     * @return the set of content types this event format can deserialize. These content types are used
     * by the {@link io.cloudevents.core.provider.EventFormatProvider} to resolve an {@link EventFormat} starting
     * from the content type {@link String}.
     */
    default Set<String> deserializableContentTypes() {
        return Collections.singleton(serializedContentType());
    }

    /**
     * @return The content type to use when writing an event with this {@link EventFormat}.
     */
    String serializedContentType();

}
