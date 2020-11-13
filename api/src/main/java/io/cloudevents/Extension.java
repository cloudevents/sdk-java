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

package io.cloudevents;

import io.cloudevents.lang.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

/**
 * Materialized CloudEvent Extension interface to read/write the extension attributes key/values.
 */
@ParametersAreNonnullByDefault
public interface Extension {

    /**
     * Fill this materialized extension with values from a {@link CloudEventExtensions} implementation.
     *
     * @param extensions the extensions where to read from
     */
    void readFrom(CloudEventExtensions extensions);

    /**
     * Get the attribute of extension named {@code key}.
     *
     * @param key the name of the extension attribute
     * @return the extension value in one of the valid types String/Number/Boolean
     * @throws IllegalArgumentException if the key is unknown to this extension
     */
    @Nullable
    Object getValue(String key) throws IllegalArgumentException;

    /**
     * Get the set of possible extension attribute keys
     *
     * @return set of possible extension attribute keys
     */
    Set<String> getKeys();

}
