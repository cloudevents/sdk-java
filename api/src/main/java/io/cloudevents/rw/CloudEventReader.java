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
     * Like {@link #read(CloudEventWriterFactory, CloudEventDataMapper)}, but with the identity {@link CloudEventDataMapper}.
     *
     * @param <W> The type of the {@link CloudEventWriter} created by writerFactory
     * @param <R> The return value of the {@link CloudEventWriter} created by writerFactory
     * @param writerFactory a factory that generates a visitor starting from the SpecVersion of the event
     * @see #read(CloudEventWriterFactory, CloudEventDataMapper)
     * @return the value returned by {@link CloudEventWriter#end()} or {@link CloudEventWriter#end(CloudEventData)}
     * @throws CloudEventRWException if something went wrong during the read.
     */
    default <W extends CloudEventWriter<R>, R> R read(CloudEventWriterFactory<W, R> writerFactory) throws CloudEventRWException {
        return read(writerFactory, CloudEventDataMapper.identity());
    }

    /**
     * Read self using the provided writer factory.
     *
     * @param <W>           the {@link CloudEventWriter} type
     * @param <R>           the return type of the {@link CloudEventWriter}
     * @param writerFactory a factory that generates a visitor starting from the SpecVersion of the event
     * @param mapper        the mapper to invoke when building the {@link CloudEventData}
     * @return the value returned by {@link CloudEventWriter#end()} or {@link CloudEventWriter#end(CloudEventData)}
     * @throws CloudEventRWException if something went wrong during the read.
     */
    <W extends CloudEventWriter<R>, R> R read(CloudEventWriterFactory<W, R> writerFactory, CloudEventDataMapper<? extends CloudEventData> mapper) throws CloudEventRWException;

}
