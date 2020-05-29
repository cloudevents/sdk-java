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
import io.cloudevents.builder.CloudEventBuilder;
import io.cloudevents.format.EventFormat;
import io.cloudevents.impl.CloudEventUtils;
import io.cloudevents.message.impl.GenericStructuredMessage;
import io.cloudevents.visitor.*;

public interface Message extends StructuredMessage, CloudEventVisitable {

    /**
     * Visit the message as binary encoded event using the provided visitor factory
     *
     * @param visitorFactory a factory that generates a visitor starting from the SpecVersion of the event
     * @throws CloudEventVisitException if something went wrong during the visit.
     * @throws IllegalStateException    if the message is not in binary encoding
     */
    <V extends CloudEventVisitor<R>, R> R visit(CloudEventVisitorFactory<V, R> visitorFactory) throws CloudEventVisitException, IllegalStateException;

    /**
     * Visit the message attributes as binary encoded event using the provided visitor
     *
     * @param visitor Attributes visitor
     * @throws CloudEventVisitException if something went wrong during the visit.
     * @throws IllegalStateException    if the message is not in binary encoding
     */
    void visitAttributes(CloudEventAttributesVisitor visitor) throws CloudEventVisitException, IllegalStateException;

    /**
     * Visit the message extensions as binary encoded event using the provided visitor
     *
     * @param visitor Extensions visitor
     * @throws CloudEventVisitException if something went wrong during the visit.
     * @throws IllegalStateException    if the message is not in binary encoding
     */
    void visitExtensions(CloudEventExtensionsVisitor visitor) throws CloudEventVisitException, IllegalStateException;

    /**
     * Visit the message as structured encoded event using the provided visitor
     *
     * @param visitor Structured Message visitor
     * @throws CloudEventVisitException if something went wrong during the visit.
     * @throws IllegalStateException    if the message is not in structured encoding
     */
    <T> T visit(StructuredMessageVisitor<T> visitor) throws CloudEventVisitException, IllegalStateException;

    Encoding getEncoding();

    default <BV extends CloudEventVisitor<R>, R> R visit(MessageVisitor<BV, R> visitor) throws CloudEventVisitException, IllegalStateException {
        switch (getEncoding()) {
            case BINARY:
                return this.visit((CloudEventVisitorFactory<BV, R>) visitor);
            case STRUCTURED:
                return this.visit((StructuredMessageVisitor<R>) visitor);
            default:
                throw new IllegalStateException("Unknown encoding");
        }
    }

    default CloudEvent toEvent() throws CloudEventVisitException, IllegalStateException {
        switch (getEncoding()) {
            case BINARY:
                return this.visit(CloudEventBuilder::fromSpecVersion);
            case STRUCTURED:
                return this.visit(EventFormat::deserialize);
            default:
                throw new IllegalStateException("Unknown encoding");
        }
    }

    static <R> R writeStructuredEvent(CloudEvent event, String format, StructuredMessageVisitor<R> visitor) {
        GenericStructuredMessage message = GenericStructuredMessage.fromEvent(format, event);
        if (message == null) {
            throw new IllegalArgumentException("Format " + format + " not found");
        }

        return message.visit(visitor);
    }

    static <R> R writeStructuredEvent(CloudEvent event, EventFormat format, StructuredMessageVisitor<R> visitor) {
        return GenericStructuredMessage.fromEvent(format, event).visit(visitor);
    }


    static <V extends CloudEventVisitor<R>, R> R writeBinaryEvent(CloudEvent event, CloudEventVisitorFactory<V, R> visitor) {
        return CloudEventUtils.toVisitable(event).visit(visitor);
    }

}
