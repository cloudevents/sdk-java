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
 * Interface to convert a {@link CloudEventData} instance to another one.
 */
@FunctionalInterface
@ParametersAreNonnullByDefault
public interface CloudEventDataMapper<R extends CloudEventData> {

    /**
     * Map {@code data} to another {@link CloudEventData} instance.
     *
     * @param data the input data
     * @return The new data
     * @throws CloudEventRWException is anything goes wrong while mapping the input data
     */
    R map(CloudEventData data) throws CloudEventRWException;

    /**
     * No-op identity mapper which can be used as default when no mapper is provided.
     */
    static CloudEventDataMapper<CloudEventData> identity() {
        return d -> d;
    }
}
