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

package io.cloudevents.core.message;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.rw.*;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Represents a <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#message">CloudEvent message</a>.
 */
@ParametersAreNonnullByDefault
public interface MessageReader extends StructuredMessageReader, CloudEventReader {

    /**
     * Visit the message as binary encoded event using the provided visitor factory.
     *
     * @param visitorFactory a factory that generates a visitor starting from the SpecVersion of the event
     * @throws CloudEventRWException if something went wrong during the visit.
     * @throws IllegalStateException if the message is not in binary encoding.
     */
    <V extends CloudEventWriter<R>, R> R read(CloudEventWriterFactory<V, R> visitorFactory) throws CloudEventRWException, IllegalStateException;

    /**
     * Visit the message attributes as binary encoded event using the provided visitor.
     *
     * @param visitor Attributes visitor
     * @throws CloudEventRWException if something went wrong during the visit.
     * @throws IllegalStateException    if the message is not in binary encoding.
     */
    void readAttributes(CloudEventAttributesWriter visitor) throws CloudEventRWException, IllegalStateException;

    /**
     * Visit the message extensions as binary encoded event using the provided visitor.
     *
     * @param visitor Extensions visitor
     * @throws CloudEventRWException if something went wrong during the visit.
     * @throws IllegalStateException    if the message is not in binary encoding.
     */
    void readExtensions(CloudEventExtensionsWriter visitor) throws CloudEventRWException, IllegalStateException;

    /**
     * Visit the message as structured encoded event using the provided visitor
     *
     * @param visitor Structured Message visitor
     * @throws CloudEventRWException if something went wrong during the visit.
     * @throws IllegalStateException    if the message is not in structured encoding.
     */
    <T> T read(StructuredMessageWriter<T> visitor) throws CloudEventRWException, IllegalStateException;

    /**
     * @return The message encoding
     */
    Encoding getEncoding();

    /**
     * Visit the event using a {@link MessageWriter}. This method allows to transcode an event from one transport to another without
     * converting it to {@link CloudEvent}. The resulting encoding will be the same as the original encoding.
     *
     * @param visitor the MessageVisitor accepting this Message
     * @return The return value of the MessageVisitor
     * @throws CloudEventRWException if something went wrong during the visit.
     * @throws IllegalStateException    if the message has an unknown encoding.
     */
    default <BV extends CloudEventWriter<R>, R> R visit(MessageWriter<BV, R> visitor) throws CloudEventRWException, IllegalStateException {
        switch (getEncoding()) {
            case BINARY:
                return this.read((CloudEventWriterFactory<BV, R>) visitor);
            case STRUCTURED:
                return this.read((StructuredMessageWriter<R>) visitor);
            default:
                throw new IllegalStateException("Unknown encoding");
        }
    }

    /**
     * Translate this message into a {@link CloudEvent} representation.
     *
     * @return A {@link CloudEvent} with the contents of this message.
     * @throws CloudEventRWException if something went wrong during the visit.
     * @throws IllegalStateException    if the message has an unknown encoding.
     */
    default CloudEvent toEvent() throws CloudEventRWException, IllegalStateException {
        switch (getEncoding()) {
            case BINARY:
                return this.read(CloudEventBuilder::fromSpecVersion);
            case STRUCTURED:
                return this.read(EventFormat::deserialize);
            default:
                throw new IllegalStateException("Unknown encoding");
        }
    }

}
