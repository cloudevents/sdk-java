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

package io.cloudevents.rw;

import io.cloudevents.CloudEventData;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Represents an object that can be read as CloudEvent.
 * <p>
 * The read may consume this object, hence it's not safe to invoke it multiple times, unless it's explicitly allowed by the implementer.
 */
@ParametersAreNonnullByDefault
public interface CloudEventReader {

    /**
     * Visit self using the provided visitor factory
     *
     * @param writerFactory a factory that generates a visitor starting from the SpecVersion of the event
     * @throws CloudEventRWException if something went wrong during the read.
     */
    default <V extends CloudEventWriter<R>, R> R read(CloudEventWriterFactory<V, R> writerFactory) throws CloudEventRWException {
        return read(writerFactory, CloudEventDataMapper.identity());
    }

    /**
     * Like {@link CloudEventReader#read(CloudEventWriterFactory)}, but providing a mapper for {@link io.cloudevents.CloudEventData} to be invoked when the data field is available.
     */
    <V extends CloudEventWriter<R>, R> R read(CloudEventWriterFactory<V, R> writerFactory, CloudEventDataMapper<? extends CloudEventData> mapper) throws CloudEventRWException;

}
