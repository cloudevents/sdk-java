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
import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.rw.*;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Represents a <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#message">CloudEvent message</a> reader.
 * <p>
 * This class expands the {@link CloudEventReader} to define reading both binary and structured messages.
 */
@ParametersAreNonnullByDefault
public interface MessageReader extends StructuredMessageReader, CloudEventReader {

    /**
     * Like {@link #read(CloudEventWriterFactory, CloudEventDataMapper)}, but with the identity {@link CloudEventDataMapper}.
     *
     * @see #read(CloudEventWriterFactory, CloudEventDataMapper)
     */
    default <W extends CloudEventWriter<R>, R> R read(CloudEventWriterFactory<W, R> writerFactory) throws CloudEventRWException, IllegalStateException {
        return read(writerFactory, CloudEventDataMapper.identity());
    }

    /**
     * Read the message as binary encoded message using the provided reader factory.
     *
     * @param <W>           the {@link CloudEventWriter} type
     * @param <R>           the return type of the {@link CloudEventWriter}
     * @param writerFactory a factory that generates a reader starting from the {@link SpecVersion} of the event
     * @param mapper        the mapper to use to map the data, if any.
     * @throws CloudEventRWException if something went wrong during the visit.
     * @throws IllegalStateException if the message is not in binary encoding.
     */
    <W extends CloudEventWriter<R>, R> R read(CloudEventWriterFactory<W, R> writerFactory, CloudEventDataMapper<? extends CloudEventData> mapper) throws CloudEventRWException, IllegalStateException;

    /**
     * Read the message as structured encoded message using the provided reader
     *
     * @param <R>    the return type of the {@link StructuredMessageWriter}
     * @param writer Structured Message reader
     * @throws CloudEventRWException if something went wrong during the visit.
     * @throws IllegalStateException if the message is not in structured encoding.
     */
    <R> R read(StructuredMessageWriter<R> writer) throws CloudEventRWException, IllegalStateException;

    /**
     * @return The message encoding
     */
    Encoding getEncoding();

    /**
     * Read the content of this object using a {@link MessageWriter}. This method allows to transcode an event from one transport to another without
     * converting it to {@link CloudEvent}. The resulting encoding will be the same as the original encoding.
     *
     * @param <BW>           the {@link CloudEventWriter} type
     * @param <R>           the return type of both {@link CloudEventWriter} and {@link StructuredMessageWriter}
     * @param reader the MessageReader accepting this Message
     * @return The return value of the MessageReader
     * @throws CloudEventRWException if something went wrong during the visit.
     * @throws IllegalStateException if the message has an unknown encoding.
     */
    default <BW extends CloudEventWriter<R>, R> R read(MessageWriter<BW, R> reader) throws CloudEventRWException, IllegalStateException {
        switch (getEncoding()) {
            case BINARY:
                return this.read((CloudEventWriterFactory<BW, R>) reader);
            case STRUCTURED:
                return this.read((StructuredMessageWriter<R>) reader);
            default:
                throw new IllegalStateException(
                    "The provided Encoding doesn't exist. Please make sure your io.cloudevents deps versions are aligned."
                );
        }
    }

    /**
     * Like {@link #toEvent(CloudEventDataMapper)}, but with the identity {@link CloudEventDataMapper}.
     *
     * @see #toEvent(CloudEventDataMapper)
     */
    default CloudEvent toEvent() throws CloudEventRWException, IllegalStateException {
        return toEvent(CloudEventDataMapper.identity());
    }

    /**
     * Translate this message into a {@link CloudEvent} representation, mapping the data with the provided {@code mapper}.
     *
     * @param mapper the mapper to use to map the data, if any.
     * @return A {@link CloudEvent} with the contents of this message.
     * @throws CloudEventRWException if something went wrong during the visit.
     * @throws IllegalStateException if the message has an unknown encoding.
     */
    default CloudEvent toEvent(CloudEventDataMapper<? extends CloudEventData> mapper) throws CloudEventRWException, IllegalStateException {
        switch (getEncoding()) {
            case BINARY:
                return CloudEventUtils.toEvent(this, mapper);
            case STRUCTURED:
                return this.read((format, value) -> format.deserialize(value, mapper));
            default:
                throw new IllegalStateException(
                    "The provided Encoding doesn't exist. Please make sure your io.cloudevents deps versions are aligned."
                );
        }
    }

}
