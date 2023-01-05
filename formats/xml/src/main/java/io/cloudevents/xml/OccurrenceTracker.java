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

package io.cloudevents.xml;

import java.util.HashSet;
import java.util.Set;

/**
 * Tracks the occurrences of a key to ensure only a single
 * instance is allowed.
 *
 * Used to help ensure that each CloudEvent context attribute
 * only occurs once in each CloudEvent element instance.
 *
 */
class OccurrenceTracker {

    private final Set<String> keySet;

    OccurrenceTracker() {
        keySet = new HashSet<>(10);
    }

    /**
     * Record an occurrence of attribute name.
     * @param name The name  to track.
     * @return boolean true => accepted, false => duplicate name.
     */
    boolean trackOccurrence(String name) {

        return keySet.add(name);

    }

}
