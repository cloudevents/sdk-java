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

import io.cloudevents.core.format.EventFormat;
import io.cloudevents.rw.CloudEventRWException;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface to write the {@link MessageReader} content (CloudEvents attributes, extensions and payload) to a new representation structured representation.
 *
 * @param <R> return value at the end of the write process.
 */
@ParametersAreNonnullByDefault
@FunctionalInterface
public interface StructuredMessageWriter<R> {

    /**
     * Write an event using the provided {@link EventFormat}.
     */
    R setEvent(EventFormat format, byte[] value) throws CloudEventRWException;

}
