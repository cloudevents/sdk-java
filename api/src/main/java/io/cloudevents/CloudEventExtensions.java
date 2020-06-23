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
 * The event extensions
 * <p>
 * Extensions values could be String/Number/Boolean
 */
@ParametersAreNonnullByDefault
public interface CloudEventExtensions {

    /**
     * Get the extension attribute named {@code extensionName}
     *
     * @param extensionName the extension name
     * @return the extension value or null if this instance doesn't contain such extension
     */
    @Nullable
    Object getExtension(String extensionName);

    /**
     * @return The non-null extension attributes names in this instance
     */
    Set<String> getExtensionNames();

}
