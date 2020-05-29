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

package io.cloudevents.message;

import io.cloudevents.CloudEvent;
import io.cloudevents.format.EventFormat;
import io.cloudevents.message.impl.GenericStructuredMessage;
import io.cloudevents.visitor.CloudEventVisitException;

@FunctionalInterface
public interface StructuredMessage {

    /**
     * @param visitor
     * @throws CloudEventVisitException
     * @throws IllegalStateException    If the message is not a valid structured message
     */
    <T> T visit(StructuredMessageVisitor<T> visitor) throws CloudEventVisitException, IllegalStateException;

    default CloudEvent toEvent() throws CloudEventVisitException, IllegalStateException {
        return this.visit(EventFormat::deserialize);
    }

    /**
     * Create a generic structured message from a {@link CloudEvent}
     *
     * @param contentType content type to use to resolve the {@link EventFormat}
     * @param event
     * @return null if format was not found, otherwise returns the built message
     */
    static StructuredMessage fromEvent(String contentType, CloudEvent event) {
        return GenericStructuredMessage.fromEvent(contentType, event);
    }

    /**
     * Create a generic structured message from a {@link CloudEvent}
     *
     * @param format
     * @param event
     * @return null if format was not found, otherwise returns the built message
     */
    static StructuredMessage fromEvent(EventFormat format, CloudEvent event) {
        return GenericStructuredMessage.fromEvent(format, event);
    }

}
