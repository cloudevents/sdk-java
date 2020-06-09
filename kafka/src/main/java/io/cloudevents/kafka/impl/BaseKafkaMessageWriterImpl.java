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

package io.cloudevents.kafka.impl;

import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;

abstract class BaseKafkaMessageWriterImpl<S extends MessageWriter<S, R> & CloudEventWriter<R>, R> implements MessageWriter<S, R>, CloudEventWriter<R> {

    byte[] value;
    final Headers headers;

    public BaseKafkaMessageWriterImpl(Headers headers) {
        this.headers = headers;
    }

    @Override
    public void setAttribute(String name, String value) throws CloudEventRWException {
        headers.add(new RecordHeader(KafkaHeaders.ATTRIBUTES_TO_HEADERS.get(name), value.getBytes()));
    }

    @Override
    public void setExtension(String name, String value) throws CloudEventRWException {
        headers.add(new RecordHeader(KafkaHeaders.CE_PREFIX + name, value.getBytes()));
    }

    @Override
    public R end(byte[] value) throws CloudEventRWException {
        this.value = value;
        return this.end();
    }

    @Override
    public R setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
        this.headers.add(new RecordHeader(KafkaHeaders.CONTENT_TYPE, format.serializedContentType().getBytes()));
        return this.end(value);
    }
}
