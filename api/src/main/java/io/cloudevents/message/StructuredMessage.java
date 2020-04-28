/*
 * Copyright 2020 The CloudEvents Authors
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

package io.cloudevents.message;

import io.cloudevents.CloudEvent;
import io.cloudevents.format.EventFormat;

@FunctionalInterface
public interface StructuredMessage {

    /**
     * @param visitor
     * @throws MessageVisitException
     * @throws IllegalStateException If the message is not a valid structured message
     */
    <T> T visit(StructuredMessageVisitor<T> visitor) throws MessageVisitException, IllegalStateException;

    default CloudEvent toEvent() throws MessageVisitException, IllegalStateException {
        return this.visit(EventFormat::deserialize);
    }

    ;

}
