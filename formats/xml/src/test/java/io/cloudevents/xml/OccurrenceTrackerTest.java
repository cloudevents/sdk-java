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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OccurrenceTrackerTest {

    private final OccurrenceTracker tracker = new OccurrenceTracker();

    @Test
    public void verifyTracking() {

        // These should all work...
        Assertions.assertTrue(tracker.trackOccurrence("CE1"));
        Assertions.assertTrue(tracker.trackOccurrence("CE2"));
        Assertions.assertTrue(tracker.trackOccurrence("ce1"));

        // This should fail
        Assertions.assertFalse(tracker.trackOccurrence("CE2"));

    }

}
