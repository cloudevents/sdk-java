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

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Represents an object that can be read as CloudEvent context attributes and extensions.
 * <p>
 * An object (in particular, buffered objects) can implement both this interface and {@link CloudEventReader}.
 */
@ParametersAreNonnullByDefault
public interface CloudEventContextReader {

    /**
     * Visit self attributes using the provided writer
     *
     * @param writer Attributes writer
     * @throws CloudEventRWException if something went wrong during the visit.
     */
    void readAttributes(CloudEventContextWriter writer) throws CloudEventRWException;

    /**
     * Visit self extensions using the provided writer
     *
     * @param visitor Extensions writer
     * @throws CloudEventRWException if something went wrong during the visit.
     */
    void readExtensions(CloudEventContextWriter visitor) throws CloudEventRWException;

}
