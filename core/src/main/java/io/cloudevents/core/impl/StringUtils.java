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

package io.cloudevents.core.impl;

import javax.annotation.Nonnull;

final public class StringUtils {

    private StringUtils() {
        // Prevent construction.
    }

    public static boolean startsWithIgnoreCase(@Nonnull final String s, @Nonnull final String prefix) {
        return s.regionMatches(true /* ignoreCase */, 0, prefix, 0, prefix.length());
    }
}
