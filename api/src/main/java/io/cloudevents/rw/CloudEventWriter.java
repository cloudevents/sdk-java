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

/**
 * Interface to write the content (CloudEvents attributes, extensions and payload) from a
 * {@link io.cloudevents.rw.CloudEventReader} to a new representation.
 *
 * @param <R> return value at the end of the write process
 */
public interface CloudEventWriter<R> extends CloudEventContextWriter {

    /**
     * End the visit with a data field
     *
     * @return an eventual return value
     * @throws CloudEventRWException if the message writer cannot be ended.
     */
    R end(CloudEventData data) throws CloudEventRWException;

    /**
     * End the visit
     *
     * @return an eventual return value
     * @throws CloudEventRWException if the message writer cannot be ended.
     */
    R end() throws CloudEventRWException;

}
