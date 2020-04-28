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

import io.cloudevents.format.EventFormat;
import io.cloudevents.message.BinaryMessageVisitor;
import io.cloudevents.message.MessageVisitException;
import io.cloudevents.message.MessageVisitor;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;

abstract class BaseKafkaMessageVisitorImpl<S extends MessageVisitor<S, R> & BinaryMessageVisitor<R>, R> implements MessageVisitor<S, R>, BinaryMessageVisitor<R> {

    byte[] value;
    final Headers headers;

    public BaseKafkaMessageVisitorImpl(Headers headers) {
        this.headers = headers;
    }

    @Override
    public void setAttribute(String name, String value) throws MessageVisitException {
        headers.add(new RecordHeader(KafkaHeaders.ATTRIBUTES_TO_HEADERS.get(name), value.getBytes()));
    }

    @Override
    public void setExtension(String name, String value) throws MessageVisitException {
        headers.add(new RecordHeader(KafkaHeaders.CE_PREFIX + name, value.getBytes()));
    }

    @Override
    public void setBody(byte[] value) throws MessageVisitException {
        this.value = value;
    }

    @Override
    public R setEvent(EventFormat format, byte[] value) throws MessageVisitException {
        this.headers.add(new RecordHeader(KafkaHeaders.CONTENT_TYPE, format.serializedContentType().getBytes()));
        this.value = value;
        return this.end();
    }
}
