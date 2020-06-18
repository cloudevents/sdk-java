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

package io.cloudevents.core.mock;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.StructuredMessageWriter;
import io.cloudevents.core.message.impl.BaseStructuredMessageReader;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.rw.CloudEventRWException;

public class MockStructuredMessageReader extends BaseStructuredMessageReader implements MessageReader, StructuredMessageWriter<MockStructuredMessageReader> {

    private EventFormat format;
    private byte[] payload;

    public MockStructuredMessageReader() {
    }

    public MockStructuredMessageReader(CloudEvent event, EventFormat format) {
        this();
        GenericStructuredMessageReader
            .from(event, format)
            .read(this);
    }

    @Override
    public <T> T read(StructuredMessageWriter<T> visitor) throws CloudEventRWException, IllegalStateException {
        if (this.format == null) {
            throw new IllegalStateException("MockStructuredMessage is empty");
        }

        return visitor.setEvent(this.format, this.payload);
    }

    @Override
    public MockStructuredMessageReader setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
        this.format = format;
        this.payload = value;

        return this;
    }
}
