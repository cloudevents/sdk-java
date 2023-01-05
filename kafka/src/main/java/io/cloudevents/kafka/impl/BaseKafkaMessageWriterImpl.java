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

import io.cloudevents.CloudEventData;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;

abstract class BaseKafkaMessageWriterImpl<R> implements MessageWriter<CloudEventWriter<R>, R>, CloudEventWriter<R> {

    final Headers headers;
    byte[] value;

    public BaseKafkaMessageWriterImpl(Headers headers) {
        this.headers = headers;
    }

    @Override
    public BaseKafkaMessageWriterImpl<R> withContextAttribute(String name, String value) throws CloudEventRWException {
        String headerName = KafkaHeaders.ATTRIBUTES_TO_HEADERS.get(name);
        if (headerName == null) {
            headerName = KafkaHeaders.CE_PREFIX + name;
        }
        headers.add(new RecordHeader(headerName, value.getBytes(StandardCharsets.UTF_8)));
        return this;
    }

    @Override
    public R end(CloudEventData value) throws CloudEventRWException {
        this.value = value.toBytes();
        return this.end();
    }

    @Override
    public R setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
        this.headers.add(new RecordHeader(KafkaHeaders.CONTENT_TYPE, format.serializedContentType().getBytes(StandardCharsets.UTF_8)));
        this.value = value;
        return this.end();
    }
}
