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

import io.cloudevents.types.Time;

import javax.annotation.ParametersAreNonnullByDefault;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Base64;

/**
 * Interface to write the context attributes/extensions from a {@link io.cloudevents.rw.CloudEventContextReader} to a new representation.
 */
@ParametersAreNonnullByDefault
public interface CloudEventContextWriter {

    /**
     * Set attribute with type {@link String}.
     * This setter should not be invoked for specversion, because the writer should
     * already know the specversion or because it doesn't need it to correctly write the value.
     *
     * @param name  name of the attribute
     * @param value value of the attribute
     * @return self
     * @throws CloudEventRWException    if anything goes wrong while writing this attribute.
     * @throws IllegalArgumentException if you're trying to set the specversion attribute.
     */
    CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException;

    /**
     * Set attribute with type {@link URI}.
     * This setter should not be invoked for specversion, because the writer should
     * already know the specversion or because it doesn't need it to correctly write the value.
     *
     * @param name  name of the attribute
     * @param value value of the attribute
     * @return self
     * @throws CloudEventRWException if anything goes wrong while writing this attribute.
     * @throws IllegalArgumentException if you're trying to set the specversion attribute.
     */
    default CloudEventContextWriter withContextAttribute(String name, URI value) throws CloudEventRWException {
        return withContextAttribute(name, value.toString());
    }

    /**
     * Set attribute with type {@link OffsetDateTime} attribute.
     * This setter should not be invoked for specversion, because the writer should
     * already know the specversion or because it doesn't need it to correctly write the value.
     *
     * @param name  name of the attribute
     * @param value value of the attribute
     * @return self
     * @throws CloudEventRWException if anything goes wrong while writing this attribute.
     * @throws IllegalArgumentException if you're trying to set the specversion attribute.
     */
    default CloudEventContextWriter withContextAttribute(String name, OffsetDateTime value) throws CloudEventRWException {
        return withContextAttribute(name, Time.writeTime(name, value));
    }

    /**
     * Set attribute with type {@link Number}.
     * This setter should not be invoked for specversion, because the writer should
     * already know the specversion or because it doesn't need it to correctly write the value.
     *
     * @param name  name of the attribute
     * @param value value of the attribute
     * @return self
     * @throws CloudEventRWException if anything goes wrong while writing this extension.
     * @throws IllegalArgumentException if you're trying to set the specversion attribute.
     *
     * @deprecated CloudEvent specification only permits {@link Integer} type as a
     * numeric val
     */
    default CloudEventContextWriter withContextAttribute(String name, Number value) throws CloudEventRWException {
        return withContextAttribute(name, value.toString());
    }

    /**
     * Set attribute with type {@link Integer}.
     * This setter should not be invoked for specversion, because the writer should
     * already know the specversion or because it doesn't need it to correctly write the value.
     *
     * @param name  name of the attribute
     * @param value value of the attribute
     * @return self
     * @throws CloudEventRWException if anything goes wrong while writing this extension.
     * @throws IllegalArgumentException if you're trying to set the specversion attribute.
     */
    default CloudEventContextWriter withContextAttribute(String name, Integer value) throws CloudEventRWException {
        return withContextAttribute(name, value.toString());
    }

    /**
     * Set attribute with type {@link Boolean} attribute.
     * This setter should not be invoked for specversion, because the writer should
     * already know the specversion or because it doesn't need it to correctly write the value.
     *
     * @param name  name of the attribute
     * @param value value of the attribute
     * @return self
     * @throws CloudEventRWException if anything goes wrong while writing this extension.
     * @throws IllegalArgumentException if you're trying to set the specversion attribute.
     */
    default CloudEventContextWriter withContextAttribute(String name, Boolean value) throws CloudEventRWException {
        return withContextAttribute(name, value.toString());
    }

    /**
     * Set attribute with a binary type.
     * This setter should not be invoked for specversion, because the writer should
     * already know the specversion or because it doesn't need it to correctly write the value.
     *
     * @param name  name of the attribute
     * @param value value of the attribute
     * @return self
     * @throws CloudEventRWException if anything goes wrong while writing this extension.
     * @throws IllegalArgumentException if you're trying to set the specversion attribute.
     */
    default CloudEventContextWriter withContextAttribute(String name, byte[] value) throws CloudEventRWException {
        return withContextAttribute(name, Base64.getEncoder().encodeToString(value));
    }
}
