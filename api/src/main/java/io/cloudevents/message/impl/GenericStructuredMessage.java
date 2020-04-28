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

package io.cloudevents.message.impl;

import io.cloudevents.format.EventFormat;
import io.cloudevents.format.EventFormatProvider;
import io.cloudevents.message.MessageVisitException;
import io.cloudevents.message.StructuredMessageVisitor;

public class GenericStructuredMessage extends BaseStructuredMessage {

    private EventFormat format;
    private byte[] payload;

    public GenericStructuredMessage(EventFormat format, byte[] payload) {
        this.format = format;
        this.payload = payload;
    }

    @Override
    public <T> T visit(StructuredMessageVisitor<T> visitor) throws MessageVisitException, IllegalStateException {
        return visitor.setEvent(format, payload);
    }

    /**
     * TODO
     *
     * @param contentType
     * @param payload
     * @return null if format was not found, otherwise returns the built message
     */
    public static GenericStructuredMessage fromContentType(String contentType, byte[] payload) {
        EventFormat format = EventFormatProvider.getInstance().resolveFormat(contentType);
        if (format == null) {
            return null;
        }

        return new GenericStructuredMessage(format, payload);
    }

}
