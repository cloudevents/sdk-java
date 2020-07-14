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

import io.cloudevents.lang.Nullable;

/**
 * Interface to write the content (CloudEvents attributes, extensions and payload) from a
 * {@link io.cloudevents.rw.CloudEventReader} to a new representation.
 *
 * @param <R> return value at the end of the write process
 */
public interface CloudEventWriter<R> extends CloudEventAttributesWriter, CloudEventExtensionsWriter {

    /**
     * End the visit with a data field
     * <p>
     * TODO add not that explains that contentType is already provided before visiting the attributes
     * TODO explain that value cannot be a base64 serialized byte array
     *
     * @return an eventual return value
     */
    R end(@Nullable String contentType, Object value) throws CloudEventRWException;

    /**
     * End the visit
     *
     * @return an eventual return value
     */
    R end();

}
