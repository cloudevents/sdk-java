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

import java.net.URI;

/**
 * Interface to write the extensions from a {@link io.cloudevents.rw.CloudEventReader} to a new representation.
 */
public interface CloudEventExtensionsWriter {

    /**
     * Set an extension with type {@link String}.
     *
     * @param name
     * @param value
     * @throws CloudEventRWException
     */
    CloudEventExtensionsWriter withExtension(String name, @Nullable String value) throws CloudEventRWException;

    /**
     * Set attribute with type {@link URI}.
     *
     * @param name
     * @param value
     * @throws CloudEventRWException
     */
    default CloudEventExtensionsWriter withExtension(String name, @Nullable Number value) throws CloudEventRWException {
        return withExtension(name, value == null ? null : value.toString());
    }

    /**
     * Set attribute with type {@link Boolean} attribute.
     *
     * @param name
     * @param value
     * @throws CloudEventRWException
     */
    default CloudEventExtensionsWriter withExtension(String name, @Nullable Boolean value) throws CloudEventRWException {
        return withExtension(name, value == null ? null : value.toString());
    }

}
