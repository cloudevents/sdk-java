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
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.rw.CloudEventWriter;
import io.cloudevents.rw.CloudEventWriterFactory;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface to write the {@link MessageReader} content (CloudEvents attributes, extensions and payload) to a new representation.
 *
 * @param <R> return value at the end of the write process.
 */
@ParametersAreNonnullByDefault
public interface MessageWriter<CEV extends CloudEventWriter<R>, R> extends CloudEventWriterFactory<CEV, R>, StructuredMessageWriter<R> {

    /**
     * Write the provided event as structured.
     *
     * @param event  event to write.
     * @param format {@link EventFormat} to use to serialize the event.
     * @return return value at the end of the write process.
     */
    default R writeStructured(CloudEvent event, String format) {
        GenericStructuredMessageReader message = GenericStructuredMessageReader.from(event, format);
        if (message == null) {
            throw new IllegalArgumentException("Format " + format + " not found");
        }

        return message.read((StructuredMessageWriter<R>) this);
    }

    /**
     * Write the provided event as structured.
     *
     * @param event  event to write.
     * @param format string to resolve the {@link EventFormat} to use to serialize the event.
     * @return return value at the end of the write process.
     */
    default R writeStructured(CloudEvent event, EventFormat format) {
        return GenericStructuredMessageReader.from(event, format).read((StructuredMessageWriter<R>) this);
    }

    /**
     * Write the provided event as binary.
     *
     * @param event event to write.
     * @return return value at the end of the write process.
     */
    default R writeBinary(CloudEvent event) {
        return CloudEventUtils.toReader(event).read(this);
    }

}
