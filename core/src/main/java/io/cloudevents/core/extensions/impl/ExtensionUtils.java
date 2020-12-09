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

package io.cloudevents.core.extensions.impl;

import io.cloudevents.Extension;

/**
 * Collection of utilities to deal with materialized extensions
 */
public final class ExtensionUtils {

    private ExtensionUtils() {
    }

    /**
     * @param clazz the {@link Extension} class
     * @param key   the invalid key
     * @return an {@link IllegalArgumentException} when trying to access a key of the extension not existing.
     */
    public static IllegalArgumentException generateInvalidKeyException(Class<? extends Extension> clazz, String key) {
        return new IllegalArgumentException(clazz.getName() + " doesn't expect the attribute key \"" + key + "\"");
    }

}
